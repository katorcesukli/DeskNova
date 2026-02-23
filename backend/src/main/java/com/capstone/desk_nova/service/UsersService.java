package com.capstone.desk_nova.service;

import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.repository.UsersRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    //view
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }



    public Users createUsers(Users users) {
        return usersRepository.save(users);
    }
}
