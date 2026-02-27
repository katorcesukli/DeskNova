package com.capstone.desk_nova;

import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.service.EmailService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DeskNovaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeskNovaApplication.class, args);
	}
	//email testing
	/*
	@Bean
	public CommandLineRunner runTestEmail(EmailService emailService) {
		return args -> {
			System.out.println("=====Starting Email TesT======");

			Users testUser = new Users();
			testUser.setFirstName("Rian");
			testUser.setEmail("test@gmail.com");

			try {
				emailService.sendTestEmail(testUser);
				System.out.println("check mailtrap");
			} catch (Exception e) {
				System.err.println("email test failed");
				e.printStackTrace();
			}
		};
	}

	 */


}
