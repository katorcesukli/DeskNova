package com.capstone.desk_nova.service;

import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.repository.UsersRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;


}
