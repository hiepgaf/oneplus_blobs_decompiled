package com.android.server.connectivity.tethering;

public abstract interface IControlsTethering
{
  public static final int STATE_AVAILABLE = 1;
  public static final int STATE_TETHERED = 2;
  public static final int STATE_UNAVAILABLE = 0;
  
  public abstract void notifyInterfaceStateChange(String paramString, TetherInterfaceStateMachine paramTetherInterfaceStateMachine, int paramInt1, int paramInt2);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/tethering/IControlsTethering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */