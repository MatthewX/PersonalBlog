package com.xcb.blog.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.xcb.blog.dao.ArticleDao;
import com.xcb.blog.model.Article;

public class ArticleService {
	
	@Autowired
	private ArticleDao articleDao;

	@Autowired
	private ArticleTagDao articleTagDao;

	public int addArticle(Article article) {
		return articleDao.insertArticle(article) > 0 ?article.getId(): 0;
	}
}
