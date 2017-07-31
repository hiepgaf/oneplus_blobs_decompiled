package com.adobe.xmp;

public abstract interface XMPVersionInfo
{
  public abstract int getBuild();
  
  public abstract int getMajor();
  
  public abstract String getMessage();
  
  public abstract int getMicro();
  
  public abstract int getMinor();
  
  public abstract boolean isDebug();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/XMPVersionInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */