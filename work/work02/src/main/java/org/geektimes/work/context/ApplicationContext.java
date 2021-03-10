package org.geektimes.work.context;

import org.geektimes.work.function.ThrowableFunction;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.naming.*;
import javax.servlet.ServletContext;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ApplicationContext {

	public static final String CONTEXT_NAME = ApplicationContext.class.getName();

	private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

	private static final Logger logger = Logger.getLogger(CONTEXT_NAME);

	/**
	 * Class到bean的映射
	 */
	private Map<Class<?>, Object> componentMapByClass = new ConcurrentHashMap<>();

	/**
	 * beanName到bean的映射
	 */
	private Map<String, Object> componentMapByBeanName = new ConcurrentHashMap<>();

	private static ServletContext servletContext;

	private ClassLoader classLoader;

	private Context envContext;

	public static  ApplicationContext getApplicationContext() {
		return (ApplicationContext) servletContext.getAttribute(CONTEXT_NAME);
	}

	/**
	 * 初始化
	 *
	 * @param servletContext
	 */
	public void init(ServletContext servletContext) {
		ApplicationContext.servletContext = servletContext;
		servletContext.setAttribute(CONTEXT_NAME, this);

		// 获取当前 ServletContext（WebApp）ClassLoader
		this.classLoader = servletContext.getClassLoader();
		initEnvContext();
		instantiateComponents();
		initializeComponents();
	}

	/**
	 * 实例化组件
	 */
	private void initEnvContext() {
		if (this.envContext != null) {
			return;
		}
		Context context = null;
		try {
			context = new InitialContext();
			this.envContext = (Context) context.lookup(COMPONENT_ENV_CONTEXT_NAME);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} finally {
			close(context);
		}
	}

	private void close(Context context) {
		if (context != null) {
			try {
				context.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

	private void instantiateComponents() {
		// 遍历获取所有的组件名称
		List<String> componentNames = listAllComponentNames();
		// 通过依赖查找，实例化对象（ Tomcat BeanFactory setter 方法的执行，仅支持简单类型）
		componentNames.forEach(name -> {
			Object target = lookupComponent(name);
			componentMapByBeanName.put(name, target);
			componentMapByClass.put(target.getClass(), target);
		});
	}

	private <T> T lookupComponent(String name) {
		return executeInContext(context -> (T) context.lookup(name));
	}

	private List<String> listAllComponentNames() {
		return listComponentNames("/");
	}

	private List<String> listComponentNames(String name) {
		/*List<String> fullNames = new LinkedList<>();
		try {
			NamingEnumeration<NameClassPair> nameClassPairNamingEnumeration = envContext.list("/");

			while (nameClassPairNamingEnumeration.hasMoreElements()){
				NameClassPair element = nameClassPairNamingEnumeration.nextElement();
				String className = element.getClassName();
				Class<?> targetClass = classLoader.loadClass(className);

				if (Context.class.isAssignableFrom(targetClass)) {
					// 如果当前名称是目录（Context 实现类）的话，递归查找
					fullNames.addAll(listComponentNames(element.getName()));
				} else {
					// 否则，当前名称绑定目标类型的话话，添加该名称到集合中
					String fullName = name.startsWith("/") ?
							element.getName() : name + "/" + element.getName();
					fullNames.add(fullName);
				}
			}

		} catch (NamingException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return fullNames;*/
		return executeInContext(context -> {
			NamingEnumeration<NameClassPair> e = executeInContext(context, ctx -> ctx.list(name), true);

			if (e == null) {
				return Collections.emptyList();
			}

			List<String> fullNames = new LinkedList<>();
			while (e.hasMoreElements()) {
				NameClassPair element = e.nextElement();
				String className = element.getClassName();
				Class<?> targetClass = classLoader.loadClass(className);

				if (Context.class.isAssignableFrom(targetClass)) {
					// 如果当前名称是目录（Context 实现类）的话，递归查找
					fullNames.addAll(listComponentNames(element.getName()));
				} else {
					// 否则，当前名称绑定目标类型的话话，添加该名称到集合中
					String fullName = name.startsWith("/") ?
							element.getName() : name + "/" + element.getName();
					fullNames.add(fullName);
				}
			}

			return fullNames;
		});
	}

	private <R> R executeInContext(Context context, ThrowableFunction<Context, R> function,
								   boolean ignoredException) {
		R result = null;
		try {
			result = ThrowableFunction.execute(context, function);
		} catch (Throwable e) {
			if (ignoredException) {
				logger.warning(e.getMessage());
			} else {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	/**
	 * 在 Context 中执行，通过指定 ThrowableFunction 返回计算结果
	 *
	 * @param function         ThrowableFunction
	 * @param ignoredException 是否忽略异常
	 * @param <R>              返回结果类型
	 * @return 返回
	 * @see ThrowableFunction#apply(Object)
	 */
	protected <R> R executeInContext(ThrowableFunction<Context, R> function, boolean ignoredException) {
		return executeInContext(this.envContext, function, ignoredException);
	}

	/**
	 * 在 Context 中执行，通过指定 ThrowableFunction 返回计算结果
	 *
	 * @param function ThrowableFunction
	 * @param <R>      返回结果类型
	 * @return 返回
	 * @see ThrowableFunction#apply(Object)
	 */
	protected <R> R executeInContext(ThrowableFunction<Context, R> function) {
		return executeInContext(function, false);
	}


	private void initializeComponents() {
		componentMapByBeanName.values().forEach(component -> {
			Class<?> componentClass = component.getClass();
			// 注入阶段 - {@link Resource}
			injectComponents(component, componentClass);
			// 初始阶段 - {@link PostConstruct}
			processPostConstruct(component, componentClass);
			// TODO 实现销毁阶段 - {@link PreDestroy}
			processPreDestroy();
		});
	}

	/**
	 * Support PostConstruct
	 *
	 * @param component
	 * @param componentClass
	 */
	private void processPostConstruct(Object component, Class<?> componentClass) {
		Stream.of(componentClass.getMethods())
				.filter(method ->
						method.isAnnotationPresent(PostConstruct.class)
								&& method.getParameterCount() == 0
								&& !Modifier.isStatic(method.getModifiers())
				).forEach(method -> {
			try {
				method.invoke(component);
			} catch (Exception ex) {
				System.out.println(componentClass + " invokeException " + ex);
			}
		});
	}

	/**
	 * Support Resource
	 *
	 * @param component
	 * @param componentClass
	 */
	private void injectComponents(Object component, Class<?> componentClass) {
		Stream.of(componentClass.getDeclaredFields())
				.filter(field -> {
					int mods = field.getModifiers();
					return !Modifier.isStatic(mods) &&
							field.isAnnotationPresent(Resource.class);
				}).forEach(field -> {
			Resource resource = field.getAnnotation(Resource.class);
			String resourcesName = resource.name();
			Object injectObject = lookupComponent(resourcesName);
			field.setAccessible(true);
			try {
				field.set(component, injectObject);
			} catch (IllegalAccessException e) {
				System.out.println("injectComponents occur exception");
			}
		});
	}

	/**
	 * Support PreDestroy
	 */
	private void processPreDestroy() {

	}

	/**
	 * 获取bean
	 *
	 * @param controllerClass
	 * @param <T>
	 * @return
	 */
	public <T> T getComponent(Class<?> controllerClass) {
		return (T) componentMapByClass.get(controllerClass);
	}

	/**
	 * 通过名称进行依赖查找
	 *
	 * @param name
	 * @param <T>
	 * @return
	 */
	public <T> T getComponent(String name) {
		return (T) componentMapByBeanName.get(name);
	}
}
