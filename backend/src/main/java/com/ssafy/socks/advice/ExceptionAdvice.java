package com.ssafy.socks.advice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ssafy.socks.advice.exception.CUserNotFoundException;
import com.ssafy.socks.model.response.CommonResult;
import com.ssafy.socks.service.ResponseService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @RestControllerAdvice
public class ExceptionAdvice {

	private final ResponseService responseService;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected CommonResult defaultException(HttpServletRequest request, Exception e) {
		return responseService.getFailResult();
	}

	@ExceptionHandler(CUserNotFoundException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected CommonResult userNotFoundException(HttpServletRequest request, CUserNotFoundException e) {
		return responseService.getFailResult();
	}
}
