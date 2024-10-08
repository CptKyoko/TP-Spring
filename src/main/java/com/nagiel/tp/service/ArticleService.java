package com.nagiel.tp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.nagiel.tp.model.Article;
import com.nagiel.tp.repository.ArticleRepository;

@Service
public class ArticleService {
	@Autowired
	private ArticleRepository articleRepository;
	
    public Optional<Article> getArticle(final Long id) {
        return articleRepository.findById(id);
    }
    
    @Query("from Article a inner join fetch a.user where a.user = :id")
    public List<Article> getArticlesByUser(@Param("id") Long id) {
        return articleRepository.findByUserId(id);
    }


    public Iterable<Article> getArticles() {
        return articleRepository.findAll();
    }

    public void deleteArticle(final Long id) {
    	articleRepository.deleteById(id);
    }

    public Article saveArticle(Article article) {
    	Article savedArticle = articleRepository.save(article);
        return savedArticle;
    }
}
