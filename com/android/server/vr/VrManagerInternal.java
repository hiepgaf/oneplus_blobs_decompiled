package com.android.server.vr;

import android.content.ComponentName;

public abstract class VrManagerInternal
{
  public static final int NO_ERROR = 0;
  
  public abstract int hasVrPackage(ComponentName paramComponentName, int paramInt);
  
  public abstract boolean isCurrentVrListener(String paramString, int paramInt);
  
  public abstract void setVrMode(boolean paramBoolean, ComponentName paramComponentName1, int paramInt, ComponentName paramComponentName2);
  
  public abstract void setVrModeImmediate(boolean paramBoolean, ComponentName paramComponentName1, int paramInt, ComponentName paramComponentName2);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/vr/VrManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */