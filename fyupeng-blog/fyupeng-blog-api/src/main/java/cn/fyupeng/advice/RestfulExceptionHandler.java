package cn.fyupeng.advice;

import cn.fyupeng.utils.BlogJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestfulExceptionHandler {


    @ExceptionHandler(Exception.class)
    public BlogJSONResult controllerExceptionHandler(Exception ex) {
        log.error("errorCode: {}\n errorMessage:", 500, ex);
        return BlogJSONResult.errorMsg(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
    }
}