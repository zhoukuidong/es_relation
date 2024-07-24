package com.zkd.demo.basic.core.advice;

import com.zkd.demo.basic.core.exceptions.CommonException;
import com.zkd.demo.basic.core.result.R;
import com.zkd.demo.basic.core.result.ReturnMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class DefaultExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionAdvice.class);
    private static final String COMMA = ",";

    public DefaultExceptionAdvice() {
    }

    @ExceptionHandler({SQLException.class})
    public R sqlExceptionHandler(SQLException e) {
        logger.error("SQL执行异常: {}", e.getMessage(), e);
        return R.fail(ReturnMessage.SQL_RUN_EXCEPTION);
    }

    @ExceptionHandler({DuplicateKeyException.class})
    public R duplicateKeyExceptionExceptionHandler(DuplicateKeyException e) {
        String msg = (String) Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse("DuplicateKey");
        logger.error("SQL执行异常, 主键或唯一索引重复: {}", msg, e);
        return R.failWithFormate(ReturnMessage.DUPLICATE_KEY_ERROR, msg);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public R dataIntegrityViolationExceptionExceptionHandler(DataIntegrityViolationException e) {
        logger.error(ReturnMessage.DATA_INTEGRITY_VIOLATION_ERROR.getMsg() + " {}", e.getMessage(), e);
        return R.fail(ReturnMessage.DATA_INTEGRITY_VIOLATION_ERROR);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public R illegalArgumentExceptionHandler(IllegalArgumentException e) {
        String message = e.getMessage();
        logger.error("参数校验异常，具体信息为：[{}]", message);
        return R.fail(ReturnMessage.PARAMS_NOT_EXISTS.getCode(), message);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public R httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        logger.error("请求Method不匹配，具体信息为：[{}]", e.getMessage());
        return R.fail(ReturnMessage.METHOD_NOT_SUPPORT.getCode(), String.format(ReturnMessage.METHOD_NOT_SUPPORT.getMsg(), req.getRequestURI(), req.getMethod()));
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public R maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        logger.error("文件上传异常，超过允许大小，具体信息为：[{}]", e.getMessage());
        return R.fail(ReturnMessage.OUT_OF_MAX_FILE_SIZE);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public R httpMessageNotReadable(HttpMessageNotReadableException e) {
        logger.error("参数格式传递异常，具体信息为：[{}]", e.getMessage());
        return R.fail(ReturnMessage.REQUEST_JSON_ERROR);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public R missingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.error(">>> 请求参数缺失异常，具体信息为：{}", e.getMessage());
        return R.fail(301, e.getMessage());
    }

    @ExceptionHandler({CommonException.class})
    public R commonExceptionHandler(CommonException e) {
        logger.error(">>>业务发生异常，异常信息：{}", e.getMsg());
        return R.fail(e.getCode(), e.getMsg());
    }

    @ExceptionHandler({BindException.class})
    public R handlerBindException(BindException e) {
        return this.handleParamException(e.getBindingResult().getAllErrors());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public R constraintViolationExceptionHandler(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        List<String> collect = (List)constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        return R.failWithFormate(ReturnMessage.PARAM_FORMATE_EXCEPTION, String.join(",", collect));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public R handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return this.handleParamException(ex.getBindingResult().getAllErrors());
    }

    private R handleParamException(List<ObjectError> allErrors) {
        List<String> errorMessage = new ArrayList(allErrors.size());
        allErrors.forEach((x) -> {
            FieldError fieldError = (FieldError)x;
            logger.error("Param Exception:{}-{}", fieldError.getField(), fieldError.getDefaultMessage());
            errorMessage.add(fieldError.getDefaultMessage());
        });
        return R.failWithFormate(ReturnMessage.PARAM_FORMATE_EXCEPTION, String.join(",", errorMessage));
    }

    @ExceptionHandler({Exception.class})
    public R exceptionHandler(Exception e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        return R.fail(ReturnMessage.SYSTEM_ERROR);
    }

    @ExceptionHandler({NullPointerException.class})
    @ResponseBody
    public R nullPointerExceptionHandler(NullPointerException e) {
        String msg = ReturnMessage.NULL_POINTER_EXCEPTION.getMsg();
        logger.error(msg, e);
        return R.fail(ReturnMessage.NULL_POINTER_EXCEPTION.getCode(), msg);
    }

    @ExceptionHandler({SocketTimeoutException.class})
    @ResponseBody
    public R socketTimeoutExceptionHandler(SocketTimeoutException e) {
        String msg = ReturnMessage.SOCKET_TIMEOUT_EXCEPTION.getMsg();
        logger.error(msg, e);
        return R.fail(ReturnMessage.SOCKET_TIMEOUT_EXCEPTION.getCode(), msg + e.getMessage());
    }
}
