package com.android.server.location;

import android.os.Bundle;
import android.os.WorkSource;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract interface LocationProviderInterface
{
  public abstract void disable();
  
  public abstract void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString);
  
  public abstract void enable();
  
  public abstract String getName();
  
  public abstract ProviderProperties getProperties();
  
  public abstract int getStatus(Bundle paramBundle);
  
  public abstract long getStatusUpdateTime();
  
  public abstract boolean isEnabled();
  
  public abstract boolean sendExtraCommand(String paramString, Bundle paramBundle);
  
  public abstract void setRequest(ProviderRequest paramProviderRequest, WorkSource paramWorkSource);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/LocationProviderInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */