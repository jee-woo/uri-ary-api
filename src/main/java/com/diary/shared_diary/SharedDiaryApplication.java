package com.diary.shared_diary;

import com.diary.shared_diary.config.OAuth2Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OAuth2Properties.class)
public class SharedDiaryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SharedDiaryApplication.class, args);
	}

}
