package org.geektimes.work.project.listener;

import org.geektimes.work.context.ApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * {@link org.geektimes.work.context.ApplicationContext} 初始化器
 *
 */
public class ApplicationContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ApplicationContext applicationContext = new ApplicationContext();
		applicationContext.init(sce.getServletContext());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
}
