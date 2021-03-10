package org.geektimes.work.project.service;

import org.geektimes.work.project.domain.User;
import org.geektimes.work.project.repository.UserRepository;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserService {

	@Resource(name = "bean/UserRepository")
	private UserRepository userRepository;

	public boolean doRegister(HttpServletRequest request, HttpServletResponse response) {
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
