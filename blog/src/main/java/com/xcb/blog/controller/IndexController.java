package com.xcb.blog.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xcb.blog.model.Article;
import com.xcb.blog.service.UserService;

@Controller
public class IndexController {

	@Autowired
	private UserService userService;

	@RequestMapping(path = { "/", "/index" })
	public String index(Model model) {
		List<ViewObject> vos = new ArrayList<>();
		List<Article> articles = articleService.getLatestArticles(0, 4);
		for (Article article : articles) {
			ViewObject vo = new ViewObject();
			vo.set("article", article);
			vos.add(vo);
		}

		ViewObject pagination = new ViewObject();
		int count = articleService.getArticleCount();
		User user = hostHolder.getUser();
		if (user == null || "admin".equals(user.getRole())) {
			model.addAttribute("create", 1);
		} else {
			model.addAttribute("create", 0);
		}
		pagination.set("current", 1);
		pagination.set("nextPage", 2);
		pagination.set("lastPage", count / 4 + 1);
		model.addAttribute("pagination", pagination);

		return "index";
	}

	@RequestMapping("/register")
	public String register(Model model, HttpServletResponse httpResponse, @RequestParam String username,
			@RequestParam String password, @RequestParam(value = "next", required = false) String next) {
		Map<String, String> map = userService.register(username, password);
		if (map.containsKey("ticket")) {
			Cookie cookie = new Cookie("ticket", map.get("ticket"));
			cookie.setPath("/");
			httpResponse.addCookie(cookie);

			if (!StringUtils.isEmpty(next))
				return "redirect:" + next;
			else
				return "redirect:/";
		} else {
			model.addAttribute("msg", map.get("msg"));
			return "login";
		}
	}

	@RequestMapping("/login")
	public String login(Model model, HttpServletResponse httpResponse, @RequestParam String username,
			@RequestParam String password, @RequestParam(value = "next", required = false) String next) {
		Map<String, String> map = userService.login(username, password);
		if (map.containsKey("ticket")) {
			Cookie cookie = new Cookie("ticket", map.get("ticket"));
			cookie.setPath("/");
			httpResponse.addCookie(cookie);

			if (!StringUtils.isEmpty(next)) {
				return "redirect:" + next;
			}
			return "redirect:/";

		} else {
			model.addAttribute("msg", map.get("msg"));
			return "login";
		}
	}
}
