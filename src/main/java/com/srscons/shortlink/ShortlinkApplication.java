package com.srscons.shortlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.srscons.shortlink", "service"})
public class ShortlinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortlinkApplication.class, args);
	}

}
