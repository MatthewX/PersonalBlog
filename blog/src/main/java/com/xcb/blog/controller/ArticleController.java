package com.xcb.blog.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xcb.blog.model.Article;
import com.xcb.blog.model.Tag;
import com.xcb.blog.service.ArticleService;
import com.xcb.blog.service.UserService;

@Controller
public class ArticleController {

	@Autowired
	private ArticleService articleService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private LikeService likeService;

	@Autowired
	private TagService tagService;

	@Autowired
	private UserService userService;

	@Autowired
	private HostHolder hostHolder;

	@Autowired
	private JedisService jedisService;

	@RequestMapping("/articleAdd")
	public String addArticle(@RequestParam("title") String title, @RequestParam("category") String category,
			@RequestParam("tag") String tag, @RequestParam("describe") String describe,
			@RequestParam("content") String content) {
		Article article = new Article();
		article.setTitle(title);
		article.setDescribes(describe);
		article.setCreatedDate(new Date());
		article.setCommentCount(0);
		article.setContent(BlogUtil.tranfer(content));
		article.setCategory(category);
		int articleId = articleService.addArticle(article);

		String[] tags = tag.split(",");
		for (String t : tags) {
			Tag tag1 = tagService.selectByName(t);
			if (tag1 == null) {
				Tag tag2 = new Tag();
				tag2.setName(t);
				tag2.setCount(1);
				int tagId = tagService.addTag(tag2);

				ArticleTag articleTag = new ArticleTag();
				articleTag.setTagId(tagId);
				articleTag.setArticleId(articleId);
				tagService.addArticleTag(articleTag);
			} else {
				tagService.updateCount(tag1.getId(), tag1.getCount() + 1);

				ArticleTag articleTag = new ArticleTag();
				articleTag.setTagId(tag1.getId());
				articleTag.setArticleId(articleId);
				tagService.addArticleTag(articleTag);
			}
		}

		String categoryKey = RedisKeyUtil.getCategoryKey(category);
		jedisService.incr(categoryKey);

		return "redirect:/";
	}

	@RequestMapping("/article/{articleId}")
	public String singleArticle(Model model, @PathVariable("articleId") int articleId) {
		Article article = articleService.getArticleById(articleId);
		List<Tag> tags = tagService.getTagByArticleId(article.getId());
		model.addAttribute("article", article);
		model.addAttribute("tags", tags);

		ViewObject clickCount = new ViewObject();
		String currentPage = jedisService.get(RedisKeyUntil.getClickCountKey("/article/" + articleId));
		String sumPage = jedisService.get(RedisKeyUntil.getClickCountKey("SUM"));
		clickCount.set("currentPage", currentPage);
		clickCount.set("sumPage", sumPage);
		model.addAttribute("clickCount", clickCount);

		if (hostHolder.getUser() == null)
			model.addAttribute("liked", 0);
		else
			model.addAttribute("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), articleId));
		model.addAttribute("likeCount", likeService.getLikeCount(articleId));
		model.addAttribute("dislikeCount", likeService.getDislikeCount(articleId));

		List<Comment> comments = commentService.getCommentsByArticleId(articleId);
		List<ViewObject> vos = new ArrayList<>();
		for (Comment comment : comments) {
			ViewObject vo = new ViewObject();
			vo.set("comment", comment);
			vo.set("user", userService.getUser(comment.getUserId()));
			vos.add(vo);
		}
		model.addAttribute("vos", vos);
		model.addAttribute("commentsCount", comments.size());

		String articleClickCount = jedisService.get(RedisKeyUntil.getClickCountKey("/article/" + article.getId()));
		if (articleClickCount == null)
			articleClickCount = "0";
		model.addAttribute("articleClickCount", articleClickCount);

		return "article";
	}
}
