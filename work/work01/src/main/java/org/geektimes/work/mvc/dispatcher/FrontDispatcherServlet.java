package org.geektimes.work.mvc.dispatcher;

import org.apache.commons.lang.StringUtils;
import org.geektimes.work.mvc.controller.Controller;
import org.geektimes.work.mvc.controller.PageController;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class FrontDispatcherServlet extends HttpServlet {

    /**
     * 请求路径和 Controller 的映射关系缓存
     */
    private Map<String, Controller> controllersMapping = new HashMap<>();

    /**
     * 请求路径和 {@link HandlerMethodInfo} 映射关系缓存
     */
    private Map<String, HandlerMethodInfo> handleMethodInfoMapping = new HashMap<>();

    /**
     * 初始化 Servlet
     *
     * @param servletConfig
     */
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        initHandleMethods();
    }

    /**
     * 读取所有的 RestController 的注解元信息 @Path
     * 利用 ServiceLoader 技术（Java SPI）
     */
    private void initHandleMethods() {
        for (Controller controller : ServiceLoader.load(Controller.class)){
            Class<?> controllerClass = controller.getClass();
            Path path = controllerClass.getAnnotation(Path.class);
            String requestPath = path.value();

            Method[] methods =  controllerClass.getMethods();
            for (Method method : methods){
                Set<String> supportHttpMethod = findSupportHttpMethods(method);
                Path methodPath = method.getAnnotation(Path.class);
                if (methodPath != null){
                    requestPath += methodPath.value();
                } else {
                    continue;
                }
                handleMethodInfoMapping.put(requestPath,
                        new HandlerMethodInfo(requestPath, method, supportHttpMethod));
            }

            controllersMapping.put(requestPath, controller);
        }
    }

    /**
     * 获取处理方法中标注的http方法集合
     * @param method
     * @return
     */
    private Set<String> findSupportHttpMethods(Method method) {
        Set<String> supportedHttpMethods = new LinkedHashSet<>();
        for (Annotation methodAnnotation : method.getAnnotations()){
            HttpMethod httpMethod = methodAnnotation.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethod != null){
                supportedHttpMethods.add(httpMethod.value());
            }

            if (supportedHttpMethods.isEmpty()){
                supportedHttpMethods.addAll(Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.PUT));
            }
        }

        return supportedHttpMethods;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String prefixPath = contextPath;
        String requestMappingPath = StringUtils.substringAfter(requestUri, StringUtils.replace(prefixPath, "//", "/"));
        Controller controller = controllersMapping.get(requestMappingPath);

        if (controller != null){
            HandlerMethodInfo handlerMethodInfo = handleMethodInfoMapping.get(requestMappingPath);
            try {
                if (handlerMethodInfo != null){
                    String httpMethod = request.getMethod();

                    if (!handlerMethodInfo.getSupportHttpMethods().contains(httpMethod)){
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }

                    if (controller instanceof PageController){
                        PageController pageController = PageController.class.cast(controller);
                        String viewPath = pageController.execute(request, response);
                        ServletContext servletContext = request.getServletContext();
                        if (!viewPath.startsWith("/")){
                            viewPath = "/" + viewPath;
                        }
                        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(viewPath);
                        requestDispatcher.forward(request, response);
                    }


                }
            } catch (Throwable throwable) {
                if (throwable.getCause() instanceof IOException) {
                    throw (IOException) throwable.getCause();

                } else {
                    throw new ServletException(throwable.getCause());
                }
            }
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
    }

}
