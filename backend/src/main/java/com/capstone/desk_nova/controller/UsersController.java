package com.capstone.desk_nova.controller;

import com.capstone.desk_nova.service.UsersService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UsersController {

    private UsersService usersService;
}
