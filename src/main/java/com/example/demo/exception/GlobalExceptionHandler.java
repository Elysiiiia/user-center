package com.example.demo.exception;

import com.example.demo.common.BaseResponse;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.ResultUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e) {
        logger.error("BusinessException: {}", e.getMessage(),e);
        //return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
        BaseResponse response = ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
        logger.info("Response: {}", response);
        System.out.println(response.getCode());
        System.out.println(response.getMessage());
        System.out.println(response.getDescription());
        System.out.println(response.getCode());
        return response;
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        logger.error("RuntimeException",e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }
}
