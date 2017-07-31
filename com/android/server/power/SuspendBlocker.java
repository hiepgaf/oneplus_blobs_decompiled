package com.android.server.power;

abstract interface SuspendBlocker
{
  public abstract void acquire();
  
  public abstract void release();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/power/SuspendBlocker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */