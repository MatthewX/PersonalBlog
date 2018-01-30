package com.xcb.blog.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoginRequestInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object arg2, Exception arg3) throws Exception {

	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object arg2,
			ModelAndView arg3) throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object object) throws Exception {

		if(hostHolder.getUser() == null || "user".equals(hostHolder.getUser().getRole())) {
			httpServletResponse.sendRedirect("/in");
		}
		return true;
	}

}
