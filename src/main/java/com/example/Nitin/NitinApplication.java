package com.example.Nitin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableJpaRepositories("com.example.Nitin.repository")  // path to your repo
//@EntityScan("com.example.Nitin.entity")                 // path to your entities
//@ComponentScan("com.example.Nitin")
public class NitinApplication {

	public static void main(String[] args) {
		SpringApplication.run(NitinApplication.class, args);
	}

}
