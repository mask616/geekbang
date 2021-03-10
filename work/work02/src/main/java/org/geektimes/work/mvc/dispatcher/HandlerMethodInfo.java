package org.geektimes.work.mvc.dispatcher;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 处理方法信息类
 *
 * @since 1.0
 */
public class HandlerMethodInfo {

    private String requestPath;

    private Method handlerMethod;

    private Set<String> supportHttpMethods;

    private Class<?> controllerClass;

    public HandlerMethodInfo(String requestPath, Method handlerMethod, Set<String> supportHttpMethods, Class<?> controllerClass) {
        this.requestPath = requestPath;
        this.handlerMethod = handlerMethod;
        this.supportHttpMethods = supportHttpMethods;
        this.controllerClass = controllerClass;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public Set<String> getSupportHttpMethods() {
        return supportHttpMethods;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }
}
