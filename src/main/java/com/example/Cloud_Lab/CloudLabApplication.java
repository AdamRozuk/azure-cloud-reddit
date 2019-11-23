package com.example.Cloud_Lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@EnableConfigurationProperties({FileStorageProperties.class})
@SpringBootApplication
@EnableCaching
public class CloudLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudLabApplication.class, args);
//		RedisClient red = new RedisClient();
//		red.initRedis();
	}

}
