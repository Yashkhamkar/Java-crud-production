package com.khamkaryash.blog.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.khamkaryash.blog.excption.UserNotFoundException;
import com.khamkaryash.blog.models.userModel;
import com.khamkaryash.blog.repository.userRepository;

@RestController
@CrossOrigin("http://localhost:3000")
public class userController {
    @Autowired
    private userRepository UserRepository;

    @PostMapping("/user")
    ResponseEntity<?> newUser(@RequestBody userModel newUser) {
        try {
            String email = newUser.getEmail();
            boolean userExists = UserRepository.existsByEmail(email);
            if (userExists) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with the same email already exists");
            } else {
                userModel savedUser = UserRepository.save(newUser);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An exception occurred: " + e.getMessage());
        } finally {
            System.out.println("Finally block executed");
        }

    }

    @PostMapping("/upload")
    String uploadImage(@RequestParam("file") MultipartFile file) throws Exception {

        System.out.println(file.getOriginalFilename());
        System.out.println(file.getName());
        System.out.println(file.getContentType());
        System.out.println(file.getSize());
        String path_directory = "C:\\Users\\yashk\\Documents\\workspace-spring-tool-suite-4-4.18.1.RELEASE\\backend\\src\\main\\resources\\static\\images";
        Files.copy(file.getInputStream(), Paths.get(path_directory + File.separator + file.getOriginalFilename()),
                StandardCopyOption.REPLACE_EXISTING);
        return "Uploaded successfully";

    }

    @GetMapping("/users")
    List<userModel> getall() {
        List<userModel> info = UserRepository.findAll();
        return info;
    }

    @GetMapping("/user/{id}")
    userModel getUserById(@PathVariable Long id) {
        return UserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @PutMapping("/user/{id}")
    userModel updateUser(@RequestBody userModel newUser, @PathVariable Long id) {
        return UserRepository.findById(id)
                .map(user -> {
                    user.setUsername(newUser.getUsername());
                    user.setName(newUser.getName());
                    user.setEmail(newUser.getEmail());
                    return UserRepository.save(user);
                }).orElseThrow(() -> new UserNotFoundException(id));
    }

    @DeleteMapping("/user/{id}")
    String deleteUser(@PathVariable Long id) {
        if (!UserRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        UserRepository.deleteById(id);
        return "User with id " + id + " has been deleted success.";
    }
}
