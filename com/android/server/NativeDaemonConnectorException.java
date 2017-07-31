package com.android.server;

public class NativeDaemonConnectorException
  extends Exception
{
  private String mCmd;
  private NativeDaemonEvent mEvent;
  
  public NativeDaemonConnectorException(String paramString)
  {
    super(paramString);
  }
  
  public NativeDaemonConnectorException(String paramString, NativeDaemonEvent paramNativeDaemonEvent)
  {
    super("command '" + paramString + "' failed with '" + paramNativeDaemonEvent + "'");
    this.mCmd = paramString;
    this.mEvent = paramNativeDaemonEvent;
  }
  
  public NativeDaemonConnectorException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public String getCmd()
  {
    return this.mCmd;
  }
  
  public int getCode()
  {
    if (this.mEvent != null) {
      return this.mEvent.getCode();
    }
    return -1;
  }
  
  public IllegalArgumentException rethrowAsParcelableException()
  {
    throw new IllegalStateException(getMessage(), this);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/NativeDaemonConnectorException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */