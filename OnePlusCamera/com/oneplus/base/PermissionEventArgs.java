package com.oneplus.base;

public class PermissionEventArgs
  extends EventArgs
{
  private final String m_Permission;
  
  public PermissionEventArgs(String paramString)
  {
    this.m_Permission = paramString;
  }
  
  public String getPermission()
  {
    return this.m_Permission;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/PermissionEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */