package com.ssafy.socks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	@Bean
	public Docket swaggerApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(swaggerInfo()).select()
			.apis(RequestHandlerSelectors.basePackage("com.ssafy.socks.controller"))	// controller package 안의 내용을 불러옴
			.paths(PathSelectors.any())
			.build()
			.useDefaultResponseMessages(false); // 기본으로 세팅되는 200,401,403,404 메시지를 표시 하지 않음
	}

	private ApiInfo swaggerInfo() {
		return new ApiInfoBuilder().title("Spring colorPOOL API Documentation")
			.description("colorPOOL 웹 앱 개발 API 문서")
			.license("SockS").licenseUrl("http://j3a303.p.ssafy.io").version("1").build();
	}
}