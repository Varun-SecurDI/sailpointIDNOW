package com.accountclaim.SailpointIDNOW;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.accountclaim.SailpointIDNOW.controller")
public class SailpointIdnowApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SailpointIdnowApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SailpointIdnowApplication.class, args);
	}

}
