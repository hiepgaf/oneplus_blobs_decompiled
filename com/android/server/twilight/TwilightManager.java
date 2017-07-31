package com.android.server.twilight;

import android.os.Handler;

public abstract interface TwilightManager
{
  public abstract TwilightState getLastTwilightState();
  
  public abstract void registerListener(TwilightListener paramTwilightListener, Handler paramHandler);
  
  public abstract void unregisterListener(TwilightListener paramTwilightListener);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/twilight/TwilightManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */