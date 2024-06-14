package com.unibuc.user.service;

import com.unibuc.user.model.User;
import com.unibuc.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService{
    UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
