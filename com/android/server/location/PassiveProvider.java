package com.android.server.location;

import android.location.ILocationManager;
import android.location.Location;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.WorkSource;
import android.util.Log;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class PassiveProvider
  implements LocationProviderInterface
{
  private static final ProviderProperties PROPERTIES = new ProviderProperties(false, false, false, false, false, false, false, 1, 2);
  private static final String TAG = "PassiveProvider";
  private final ILocationManager mLocationManager;
  private boolean mReportLocation;
  
  public PassiveProvider(ILocationManager paramILocationManager)
  {
    this.mLocationManager = paramILocationManager;
  }
  
  public void disable() {}
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("mReportLocation=" + this.mReportLocation);
  }
  
  public void enable() {}
  
  public String getName()
  {
    return "passive";
  }
  
  public ProviderProperties getProperties()
  {
    return PROPERTIES;
  }
  
  public int getStatus(Bundle paramBundle)
  {
    if (this.mReportLocation) {
      return 2;
    }
    return 1;
  }
  
  public long getStatusUpdateTime()
  {
    return -1L;
  }
  
  public boolean isEnabled()
  {
    return true;
  }
  
  public boolean sendExtraCommand(String paramString, Bundle paramBundle)
  {
    return false;
  }
  
  public void setRequest(ProviderRequest paramProviderRequest, WorkSource paramWorkSource)
  {
    this.mReportLocation = paramProviderRequest.reportLocation;
  }
  
  public void updateLocation(Location paramLocation)
  {
    if (this.mReportLocation) {}
    try
    {
      this.mLocationManager.reportLocation(paramLocation, true);
      return;
    }
    catch (RemoteException paramLocation)
    {
      Log.e("PassiveProvider", "RemoteException calling reportLocation");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/PassiveProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */