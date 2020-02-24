package com.jm.exception;

public class PceException extends RuntimeException
{

  /**  */
  private static final long serialVersionUID = 5039004110590768666L;
  private String errorCode;

  public PceException()
  {
    super();
  }

  public PceException(String msg)
  {
    super(msg);
  }

  public PceException(Throwable t, String msg)
  {
    super(msg, t);
    if (t instanceof PceException) {
      this.errorCode = ((PceException) t).getErrorCode();
    }
  }

  public PceException(String errorCode, Throwable t)
  {
    this(errorCode, t, null);
  }

  public PceException(String errorCode, Throwable t, String msg)
  {
    super(msg, t);
    this.errorCode = errorCode;
  }

  /**
   * Getter method for property <tt>errorCode</tt>.
   *
   * @return property value of errorCode
   */
  public String getErrorCode()
  {
    return errorCode;
  }

  /**
   * Setter method for property <tt>errorCode</tt>.
   *
   * @param errorCode value to be assigned to property errorCode
   */
  public void setErrorCode(String errorCode)
  {
    this.errorCode = errorCode;
  }

}
