package org.geektimes.work.project.controller;

import org.geektimes.work.mvc.controller.PageController;
import org.geektimes.work.project.service.RegisterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;


@Path("/register")
public class RegisterController implements PageController{

    /**
     * @param request  HTTP 请求
     * @param response HTTP 相应
     * @return 视图地址路径
     * @throws Throwable 异常发生时
     */
    @GET
    @POST
    @Path("/doRegister")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        // TODO 执行注册逻辑
        try{
            RegisterService registerService = new RegisterService();
            registerService.register(request, response);
            return "/success.jsp";
        } catch (Exception e){
            throw new Throwable();
        }
    }
}
