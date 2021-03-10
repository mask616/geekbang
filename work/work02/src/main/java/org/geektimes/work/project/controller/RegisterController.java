package org.geektimes.work.project.controller;

import org.geektimes.work.mvc.controller.PageController;
import org.geektimes.work.project.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;


/**
 * 注册Controller
 */
@Path("/register")
public class RegisterController implements PageController{

	@Resource(name = "bean/UserService")
	private UserService userService;

    /**
     * @param request  HTTP 请求
     * @param response HTTP 相应
     * @return 视图地址路径
     * @throws Throwable 异常发生时
     */
    @GET
    @POST
    @Path("/doRegister")
    public String doRegister(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        // TODO 执行注册逻辑
        try{
            if (userService.doRegister(request, response)){
                return "/success.jsp";
            }
        } catch (Exception e){
            throw new Throwable();
        }
        return "/error.jsp";
    }

    /**
     * @param request  HTTP 请求
     * @param response HTTP 相应
     * @return 视图地址路径
     * @throws Throwable 异常发生时
     */
    @GET
    @POST
    @Path("/unRegister")
    public String unRegister(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        // TODO 执行注册逻辑
        try{

        } catch (Exception e){
            throw new Throwable();
        }
        return "/error.jsp";
    }
}
