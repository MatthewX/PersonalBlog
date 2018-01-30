package com.xcb.blog.interceptor;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.xcb.blog.dao.LoginTicketDao;
import com.xcb.blog.dao.UserDao;
import com.xcb.blog.model.LoginTicket;
import com.xcb.blog.model.User;

@Component
public class PassportInterceptor implements HandlerInterceptor {

	@Autowired
	private LoginTicketDao loginTicketDao;

	@Autowired
	private JedisService jedisService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private HostHolder hostHolder;

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object object) throws Exception {
		String ticket = null;
		if (httpServletRequest.getCookies() != null) {
			for (Cookie cookie : httpServletRequest.getCookies()) {
				if ("ticket".equals(cookie.getName())) {
					ticket = cookie.getValue();
					break;
				}
			}
		}
		if (ticket != null) {
			LoginTicket loginTicket = loginTicketDao.seleteByTicket(ticket);
			if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
				return true;
			}
			User user = userDao.seleteById(loginTicket.getUserId());
			hostHolder.setUser(user);
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			modelAndView.addObject("user", hostHolder.getUser());
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {
		hostHolder.clear();
	}
}