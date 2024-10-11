package com.nagiel.tp.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagiel.tp.model.Article;
import com.nagiel.tp.model.User;
import com.nagiel.tp.repository.UserRepository;
import com.nagiel.tp.service.ArticleService;
import com.nagiel.tp.service.UserService;

@RestController
@RequestMapping("/api")
public class ArticleController {

	@Autowired
    private ArticleService articleService;
	
	@Autowired
    private UserRepository userRepository;
	
    /**
    * Read - Get all articles
    * @return - An Iterable object of Article full filled
    */
    @GetMapping("/articles")
    public Iterable<Article> getArticles() {
        return articleService.getArticles();
    }
    
	/**
	 * Create - Add a new Article
	 * @param Article An object Article
	 * @return The Article object saved
	 */
	@PostMapping("/article")
	public Article createArticle(@RequestBody Article article) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("Error: User is not found."));

	    article.setUser(user);
		article.setDate(new Date());
		return articleService.saveArticle(article);
	}
	
	/**
	 * Read - Get one Article 
	 * @param id The id of the Article
	 * @return An Article object full filled
	 */
	@GetMapping("/article/{id}")
	public Article getArticle(@PathVariable final Long id) {
		Optional<Article> Article = articleService.getArticle(id);
		if(Article.isPresent()) {
			return Article.get();
		} else {
			return null;
		}
	}
		
	/**
	 * Read - Get all Articles by User 
	 * @param id The id of the User
	 * @return An list of Articles object by user
	 */
	@GetMapping("/articles/{id}")
	public List<Article> getArticlesByUser(@PathVariable final Long id) {
		return articleService.getArticlesByUser(id);
	}

	/**
	 * Update - Update an existing Article
	 * @param id - The id of the Article to update
	 * @param Article - The Article object updated
	 * @return
	 */
	@PutMapping("/article/{id}")
	public Article updateArticle(@PathVariable final Long id, @RequestBody Article article) {
		Optional<Article> e = articleService.getArticle(id);
		if(e.isPresent()) {
			Article currentArticle = e.get();
			
			String title = article.getTitle();
			if(title != null) {
				currentArticle.setTitle(title);
			}
			String content = article.getContent();
			if(content != null) {
				currentArticle.setContent(content);
			}
			User user = article.getUser();
			if(user != null) {
				currentArticle.setUser(user);
			}
			articleService.saveArticle(currentArticle);
			return currentArticle;
		} else {
			return null;
		}
	}
	
	/**
	 * Delete - Delete an Article
	 * @param id - The id of the Article to delete
	 */
	@DeleteMapping("/article/{id}")
	public void deleteArticle(@PathVariable final Long id) {
		articleService.deleteArticle(id);
	}
}
