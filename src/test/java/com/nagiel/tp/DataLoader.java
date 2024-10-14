package com.nagiel.tp;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.nagiel.tp.model.Article;
import com.nagiel.tp.model.User;
import com.nagiel.tp.repository.ArticleRepository;
import com.nagiel.tp.repository.UserRepository;

import jakarta.transaction.Transactional;

@Component
@Profile("test")
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    
    public DataLoader(UserRepository userRepository, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Loading data...");

        if (!userRepository.findByUsername("Guillaume").isPresent()) {
            userRepository.save(new User("Guillaume", "guillaume.nagiel@gmail.com", "guillaume"));
            System.out.println("User 'Guillaume' saved.");
        } else {
            System.out.println("User 'Guillaume' already exists.");
        }

        if (!userRepository.findByUsername("test").isPresent()) {
            userRepository.save(new User("test", "test.test@test.com", "test"));
            System.out.println("User 'test' saved.");
        } else {
            System.out.println("User 'test' already exists.");
        }
        
        if (!userRepository.findByUsername("testUserDelete").isPresent()) {
            userRepository.save(new User("testUserDelete", "testdelete.test@test.com", "test"));
            System.out.println("User 'testUserDelete' saved.");
        } else {
            System.out.println("User 'testUserDelete' already exists.");
        }
        
        Optional<User> optionalUser = userRepository.findById((long) 1);
        
        if (optionalUser.isPresent() && !articleRepository.findById((long) 1).isPresent()) {
            User user = optionalUser.get(); 
            articleRepository.save(new Article("Titre test 1", "Content 1", user));
        }
        
        if (optionalUser.isPresent() && !articleRepository.findById((long) 2).isPresent()) {
            User user = optionalUser.get(); 
            articleRepository.save(new Article("Titre test 2", "Content 2", user));
        }
        
        
    }
}