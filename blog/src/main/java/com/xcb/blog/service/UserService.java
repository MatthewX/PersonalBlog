package com.xcb.blog.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.xcb.blog.dao.LoginTicketDao;
import com.xcb.blog.dao.UserDao;
import com.xcb.blog.model.LoginTicket;
import com.xcb.blog.model.User;
import com.xcb.blog.utils.BlogUtil;

@Service
public class UserService {

	@Autowired
    private UserDao userDao;

    @Autowired
    private LoginTicketDao loginTicketDao;
    
	public Map<String, String> register(String username, String password) {
		Map<String, String> map = new HashMap<>();
		Random random = new Random();

		if (StringUtils.isEmpty(username)) {
			map.put("msg", "用户名不能为空");
			return map;
		}

		if (StringUtils.isEmpty(password)) {
			map.put("msg", "密码不能为空");
			return map;
		}

		User u = userDao.seleteByName(username);
		if (u != null) {
			map.put("msg", "用户名已经被占用");
			return map;
		}

		User user = new User();
		user.setName(username);
		user.setSalt(UUID.randomUUID().toString().substring(0, 5));
		user.setHeadUrl(String.format("https://images.nowcoder.com/head/%dm.png", random.nextInt(1000)));
		user.setPassword(BlogUtil.MD5(password + user.getSalt()));
		user.setRole("user");
		userDao.insertUser(user);

		String ticket = addLoginTicket(user.getId());
		map.put("ticket", ticket);

		return map;
	}
	
	public Map<String,String> login(String username, String password){
        Map<String,String> map = new HashMap<>();
        if (StringUtils.isEmpty(username)){
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isEmpty(password)){
            map.put("msg", "密码不能为空");
            return map;
        }

        User u = userDao.seleteByName(username);
        if (u == null){
            map.put("msg", "用户名不存在");
            return map;
        }

        if (!BlogUtil.MD5(password + u.getSalt()).equals(u.getPassword())){
            map.put("msg", "密码错误");
            return map;
        }

        String ticket = addLoginTicket(u.getId());
        map.put("ticket", ticket);

        return map;
    }
	
	
	private String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 30);
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));

        loginTicketDao.insertLoginTicket(loginTicket);

        return loginTicket.getTicket();
    }
	
}
