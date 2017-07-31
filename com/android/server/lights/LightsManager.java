package com.android.server.lights;

public abstract class LightsManager
{
  public static final int LIGHT_ID_ATTENTION = 5;
  public static final int LIGHT_ID_BACKLIGHT = 0;
  public static final int LIGHT_ID_BATTERY = 3;
  public static final int LIGHT_ID_BLUETOOTH = 6;
  public static final int LIGHT_ID_BUTTONS = 2;
  public static final int LIGHT_ID_COUNT = 8;
  public static final int LIGHT_ID_KEYBOARD = 1;
  public static final int LIGHT_ID_NOTIFICATIONS = 4;
  public static final int LIGHT_ID_WIFI = 7;
  
  public abstract Light getLight(int paramInt);
  
  public abstract boolean setDeviceIdleMode(boolean paramBoolean);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/lights/LightsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */