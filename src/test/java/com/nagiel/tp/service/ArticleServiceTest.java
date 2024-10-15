package com.nagiel.tp.service;

import com.nagiel.tp.model.Article;
import com.nagiel.tp.model.User;
import com.nagiel.tp.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    private Article article;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("username", "email@example.com", "password");
        user.setId(1L);
        
        article = new Article("Test Title", "Test Content", user);
        article.setId(1L);
    }

    @Test
    public void testGetArticle() {
        // Arrange
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        // Act
        Optional<Article> foundArticle = articleService.getArticle(1L);

        // Assert
        assertTrue(foundArticle.isPresent());
        assertEquals("Test Title", foundArticle.get().getTitle());
        verify(articleRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetArticleNotFound() {
        // Arrange
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Article> foundArticle = articleService.getArticle(1L);

        // Assert
        assertFalse(foundArticle.isPresent());
        verify(articleRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetArticlesByUser() {
        // Arrange
        when(articleRepository.findByUserId(1L)).thenReturn(Arrays.asList(article));

        // Act
        List<Article> articles = articleService.getArticlesByUser(1L);

        // Assert
        assertNotNull(articles);
        assertEquals(1, articles.size());
        assertEquals("Test Title", articles.get(0).getTitle());
        verify(articleRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testGetArticles() {
        // Arrange
        when(articleRepository.findAll()).thenReturn(Arrays.asList(article));

        // Act
        Iterable<Article> articles = articleService.getArticles();

        // Assert
        assertNotNull(articles);
        assertEquals(1, ((Collection<?>) articles).size());
        verify(articleRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteArticle() {
        // Arrange
        doNothing().when(articleRepository).deleteById(1L);

        // Act
        articleService.deleteArticle(1L);

        // Assert
        verify(articleRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testSaveArticle() {
        // Arrange
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        // Act
        Article savedArticle = articleService.saveArticle(article);

        // Assert
        assertNotNull(savedArticle);
        assertEquals("Test Title", savedArticle.getTitle());
        verify(articleRepository, times(1)).save(article);
    }

    @Test
    public void testIsArticleOwner() {
        // Arrange
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        // Act
        boolean isOwner = articleService.isArticleOwner(1L, "username");

        // Assert
        assertTrue(isOwner);
        verify(articleRepository, times(1)).findById(1L);
    }

    @Test
    public void testIsArticleOwnerNotFound() {
        // Arrange
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean isOwner = articleService.isArticleOwner(1L, "username");

        // Assert
        assertFalse(isOwner);
        verify(articleRepository, times(1)).findById(1L);
    }

    @Test
    public void testIsArticleOwnerDifferentUser() {
        // Arrange
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        // Act
        boolean isOwner = articleService.isArticleOwner(1L, "anotherUsername");

        // Assert
        assertFalse(isOwner);
        verify(articleRepository, times(1)).findById(1L);
    }
}
