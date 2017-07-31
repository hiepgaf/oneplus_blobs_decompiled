package com.android.server.lights;

public abstract class Light
{
  public static final int BRIGHTNESS_MODE_LOW_PERSISTENCE = 2;
  public static final int BRIGHTNESS_MODE_SENSOR = 1;
  public static final int BRIGHTNESS_MODE_USER = 0;
  public static final int LIGHT_FLASH_HARDWARE = 2;
  public static final int LIGHT_FLASH_NONE = 0;
  public static final int LIGHT_FLASH_TIMED = 1;
  
  public abstract void pulse();
  
  public abstract void pulse(int paramInt1, int paramInt2);
  
  public abstract void setBrightness(int paramInt);
  
  public abstract void setBrightness(int paramInt1, int paramInt2);
  
  public abstract void setColor(int paramInt);
  
  public abstract void setFlashing(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void turnOff();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/lights/Light.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */