package com.project.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
@SpringBootApplication
@RestController
public class TransactionApplication {
	public static final Logger logger = LoggerFactory.getLogger(TransactionApplication.class);
	@RequestMapping("/")
	public String index() {
		logger.trace("A TRACE Message");
		logger.debug("A DEBUG Message");
		logger.info("An INFO Message");
		logger.warn("A WARN Message");
		logger.error("An ERROR Message");

		return "YO! Check out the Logs to see the output...";
	}
	public static void main(String[] args) {
		SpringApplication.run(TransactionApplication.class, args);
	}

}
