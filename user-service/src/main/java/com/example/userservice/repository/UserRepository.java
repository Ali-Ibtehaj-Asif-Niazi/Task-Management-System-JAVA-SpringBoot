package com.example.userservice.repository;

import com.example.userservice.model.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
