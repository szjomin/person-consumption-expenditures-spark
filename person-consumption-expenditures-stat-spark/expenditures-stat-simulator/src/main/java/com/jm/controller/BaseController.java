package com.jm.controller;


import com.jm.exception.ParameterException;
import com.jm.exception.PceException;
import com.jm.utils.MessageUtil;
import com.jm.utils.ResultUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.jm.result.IResult;
import com.jm.result.ResultConstants;
import com.jm.utils.EnhancedStringUtils;
import com.jm.utils.MyLogger;

import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindException;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public abstract class BaseController
{
  private final MyLogger LOG = MyLogger.getLogger(this.getClass());

  private static final String FIELD_BIND_ERROR_MSG = "{0}值非法:{1}";

  @Autowired(required = false)
  private HttpServletRequest httpServletRequest;

  @ExceptionHandler(BindException.class)
  @ResponseBody
  public IResult dealBindException(HttpServletRequest request, BindException e)
  {
    LOG.error("binding exception, request uri:{}", e, getUrlAndParamString(request));

    //only record first field error
    FieldError fieldError = e.getBindingResult().getFieldError();
    String field = fieldError.getField();
    String message = EnhancedStringUtils.formatMsg(FIELD_BIND_ERROR_MSG, field, fieldError.getRejectedValue());

    return createErrorResult(ResultConstants.CODE_PARAMETER_ERROR, message, null);
  }

  @ExceptionHandler
  @ResponseBody
  public IResult dealException(HttpServletRequest request, Exception e)
  {
    String errorCode = ResultConstants.CODE_UNKNOWN_ERROR;
    String msg = null;
    if (e instanceof PceException) {
      PceException ex = (PceException) e;

      if (ex.getErrorCode() != null)
      {
        LOG.warn("internal error. Msg:{},code:{}, Uri:{}", ex.getMessage(),
          ex.getErrorCode(),
          getUrlAndParamString(request));
      } else {
        LOG.error("internal error. Uri:" + getUrlAndParamString(request), e);
      }

      PceException pceException = (PceException) e;
      if (StringUtils.isNotBlank(pceException.getErrorCode())) {
        errorCode = pceException.getErrorCode();
      }
      if (StringUtils.isNotBlank(pceException.getMessage())) {
        msg = pceException.getMessage();
      }
    } else {
      LOG.error("internal error. Uri:" + getUrlAndParamString(request), e);
      if (e instanceof MethodArgumentNotValidException || e instanceof ServletRequestBindingException
        || e instanceof ParameterException || e instanceof TypeMismatchException)
      {
        errorCode = ResultConstants.CODE_PARAMETER_ERROR;
      }
    }

    if (StringUtils.isBlank(msg)) {
      msg = MessageUtil.getMessage(errorCode);
    }

    if (StringUtils.isBlank(msg)) {
      msg = e.getMessage();
    }

    return createErrorResult(errorCode, msg, null);
  }

  protected IResult createErrorResult(String code, String msg, Object data)
  {
    return ResultUtils.createErrorResult(code, msg, data);
  }

  protected IResult createResult(Object data)
  {
    return ResultUtils.createResult(data);
  }

  protected IResult createResult()
  {
    return ResultUtils.createResult(null);
  }

  protected Map<String, Object> create4Result(IResult result)
  {
    if (result.isSuccess()) {
      return ResultUtils.success(result.getData());
    } else {
      return ResultUtils.fail(result.getCode(), result.getMsg());
    }
  }

  private String getUrlAndParamString(HttpServletRequest request)
  {
    StringBuilder sb = new StringBuilder(request.getRequestURI());
    if (StringUtils.isNotBlank(request.getQueryString())) {
      sb.append("GET:").append(request.getQueryString());
    }

    return sb.toString();
  }

}
