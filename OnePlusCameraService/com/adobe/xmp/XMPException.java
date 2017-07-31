package com.adobe.xmp;

public class XMPException
  extends Exception
{
  private int errorCode;
  
  public XMPException(String paramString, int paramInt)
  {
    super(paramString);
    this.errorCode = paramInt;
  }
  
  public XMPException(String paramString, int paramInt, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    this.errorCode = paramInt;
  }
  
  public int getErrorCode()
  {
    return this.errorCode;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/XMPException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */