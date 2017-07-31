package com.android.server;

import java.util.HashMap;

public class SystemEvent
  extends HashMap<String, String>
{
  public String name;
  
  public SystemEvent(String paramString)
  {
    this.name = paramString;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/SystemEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */