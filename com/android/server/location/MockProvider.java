package com.android.server.location;

import android.location.ILocationManager;
import android.location.Location;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.WorkSource;
import android.util.Log;
import android.util.PrintWriterPrinter;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class MockProvider
  implements LocationProviderInterface
{
  private static final String TAG = "MockProvider";
  private boolean mEnabled;
  private final Bundle mExtras = new Bundle();
  private boolean mHasLocation;
  private boolean mHasStatus;
  private final Location mLocation;
  private final ILocationManager mLocationManager;
  private final String mName;
  private final ProviderProperties mProperties;
  private int mStatus;
  private long mStatusUpdateTime;
  
  public MockProvider(String paramString, ILocationManager paramILocationManager, ProviderProperties paramProviderProperties)
  {
    if (paramProviderProperties == null) {
      throw new NullPointerException("properties is null");
    }
    this.mName = paramString;
    this.mLocationManager = paramILocationManager;
    this.mProperties = paramProviderProperties;
    this.mLocation = new Location(paramString);
  }
  
  public void clearLocation()
  {
    this.mHasLocation = false;
  }
  
  public void clearStatus()
  {
    this.mHasStatus = false;
    this.mStatusUpdateTime = 0L;
  }
  
  public void disable()
  {
    this.mEnabled = false;
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    dump(paramPrintWriter, "");
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println(paramString + this.mName);
    paramPrintWriter.println(paramString + "mHasLocation=" + this.mHasLocation);
    paramPrintWriter.println(paramString + "mLocation:");
    this.mLocation.dump(new PrintWriterPrinter(paramPrintWriter), paramString + "  ");
    paramPrintWriter.println(paramString + "mHasStatus=" + this.mHasStatus);
    paramPrintWriter.println(paramString + "mStatus=" + this.mStatus);
    paramPrintWriter.println(paramString + "mStatusUpdateTime=" + this.mStatusUpdateTime);
    paramPrintWriter.println(paramString + "mExtras=" + this.mExtras);
  }
  
  public void enable()
  {
    this.mEnabled = true;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public ProviderProperties getProperties()
  {
    return this.mProperties;
  }
  
  public int getStatus(Bundle paramBundle)
  {
    if (this.mHasStatus)
    {
      paramBundle.clear();
      paramBundle.putAll(this.mExtras);
      return this.mStatus;
    }
    return 2;
  }
  
  public long getStatusUpdateTime()
  {
    return this.mStatusUpdateTime;
  }
  
  public boolean isEnabled()
  {
    return this.mEnabled;
  }
  
  public boolean sendExtraCommand(String paramString, Bundle paramBundle)
  {
    return false;
  }
  
  public void setLocation(Location paramLocation)
  {
    this.mLocation.set(paramLocation);
    this.mHasLocation = true;
    if (this.mEnabled) {}
    try
    {
      this.mLocationManager.reportLocation(this.mLocation, false);
      return;
    }
    catch (RemoteException paramLocation)
    {
      Log.e("MockProvider", "RemoteException calling reportLocation");
    }
  }
  
  public void setRequest(ProviderRequest paramProviderRequest, WorkSource paramWorkSource) {}
  
  public void setStatus(int paramInt, Bundle paramBundle, long paramLong)
  {
    this.mStatus = paramInt;
    this.mStatusUpdateTime = paramLong;
    this.mExtras.clear();
    if (paramBundle != null) {
      this.mExtras.putAll(paramBundle);
    }
    this.mHasStatus = true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/MockProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */