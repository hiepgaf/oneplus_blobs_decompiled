package com.android.server.am;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.MutableLong;
import android.util.TimeUtils;
import java.io.PrintWriter;

public class AppTimeTracker
{
  private final ArrayMap<String, MutableLong> mPackageTimes = new ArrayMap();
  private final PendingIntent mReceiver;
  private String mStartedPackage;
  private MutableLong mStartedPackageTime;
  private long mStartedTime;
  private long mTotalTime;
  
  public AppTimeTracker(PendingIntent paramPendingIntent)
  {
    this.mReceiver = paramPendingIntent;
  }
  
  public void deliverResult(Context paramContext)
  {
    stop();
    Bundle localBundle = new Bundle();
    localBundle.putLong("android.activity.usage_time", this.mTotalTime);
    Object localObject = new Bundle();
    int i = this.mPackageTimes.size() - 1;
    while (i >= 0)
    {
      ((Bundle)localObject).putLong((String)this.mPackageTimes.keyAt(i), ((MutableLong)this.mPackageTimes.valueAt(i)).value);
      i -= 1;
    }
    localBundle.putBundle("android.usage_time_packages", (Bundle)localObject);
    localObject = new Intent();
    ((Intent)localObject).putExtras(localBundle);
    try
    {
      this.mReceiver.send(paramContext, 0, (Intent)localObject);
      return;
    }
    catch (PendingIntent.CanceledException paramContext) {}
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString, boolean paramBoolean)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mReceiver=");
    paramPrintWriter.println(this.mReceiver);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mTotalTime=");
    TimeUtils.formatDuration(this.mTotalTime, paramPrintWriter);
    paramPrintWriter.println();
    int i = 0;
    while (i < this.mPackageTimes.size())
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mPackageTime:");
      paramPrintWriter.print((String)this.mPackageTimes.keyAt(i));
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(((MutableLong)this.mPackageTimes.valueAt(i)).value, paramPrintWriter);
      paramPrintWriter.println();
      i += 1;
    }
    if ((paramBoolean) && (this.mStartedTime != 0L))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mStartedTime=");
      TimeUtils.formatDuration(SystemClock.elapsedRealtime(), this.mStartedTime, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mStartedPackage=");
      paramPrintWriter.println(this.mStartedPackage);
    }
  }
  
  public void dumpWithHeader(PrintWriter paramPrintWriter, String paramString, boolean paramBoolean)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("AppTimeTracker #");
    paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this)));
    paramPrintWriter.println(":");
    dump(paramPrintWriter, paramString + "  ", paramBoolean);
  }
  
  public void start(String paramString)
  {
    long l = SystemClock.elapsedRealtime();
    if (this.mStartedTime == 0L) {
      this.mStartedTime = l;
    }
    if (!paramString.equals(this.mStartedPackage))
    {
      if (this.mStartedPackageTime != null)
      {
        l -= this.mStartedTime;
        MutableLong localMutableLong = this.mStartedPackageTime;
        localMutableLong.value += l;
        this.mTotalTime += l;
      }
      this.mStartedPackage = paramString;
      this.mStartedPackageTime = ((MutableLong)this.mPackageTimes.get(paramString));
      if (this.mStartedPackageTime == null)
      {
        this.mStartedPackageTime = new MutableLong(0L);
        this.mPackageTimes.put(paramString, this.mStartedPackageTime);
      }
    }
  }
  
  public void stop()
  {
    if (this.mStartedTime != 0L)
    {
      long l = SystemClock.elapsedRealtime() - this.mStartedTime;
      this.mTotalTime += l;
      if (this.mStartedPackageTime != null)
      {
        MutableLong localMutableLong = this.mStartedPackageTime;
        localMutableLong.value += l;
      }
      this.mStartedPackage = null;
      this.mStartedPackageTime = null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/AppTimeTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */