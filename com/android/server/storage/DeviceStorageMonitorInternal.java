package com.android.server.storage;

public abstract interface DeviceStorageMonitorInternal
{
  public abstract void checkMemory();
  
  public abstract long getMemoryLowThreshold();
  
  public abstract boolean isMemoryLow();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/storage/DeviceStorageMonitorInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */