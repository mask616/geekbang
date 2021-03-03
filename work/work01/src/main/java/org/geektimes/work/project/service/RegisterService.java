package org.geektimes.work.project.service;

import org.geektimes.work.project.bean.User;
import org.geektimes.work.project.repository.UserRepository;
import org.geektimes.work.project.repository.UserRepositoryImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户注册Service
 * @since 1.0
 */
public class RegisterService {

    private UserRepository userRepository = new UserRepositoryImpl();

    /**
     * 用户注册
     * @param request
     * @param response
     */
    public boolean register(HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, SQLException, IntrospectionException, IllegalAccessException {
        User user = buildUserByRequest(request);
        boolean registerFlag = userRepository.save(user);

        if (registerFlag){
            return true;
        }

        return false;
    }

    private User buildUserByRequest(HttpServletRequest request) {
        User user = new User();
        user.setName(request.getParameter("name"));
        user.setPassword(request.getParameter("password"));
        user.setEmail(request.getParameter("email"));
        user.setPhoneNumber(request.getParameter("phoneNumber"));
        return user;
    }

}
