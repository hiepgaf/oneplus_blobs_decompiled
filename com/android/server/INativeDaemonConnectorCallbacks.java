package com.android.server;

abstract interface INativeDaemonConnectorCallbacks
{
  public abstract boolean onCheckHoldWakeLock(int paramInt);
  
  public abstract void onDaemonConnected();
  
  public abstract boolean onEvent(int paramInt, String paramString, String[] paramArrayOfString);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/INativeDaemonConnectorCallbacks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */