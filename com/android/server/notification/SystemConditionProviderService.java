package com.android.server.notification;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.service.notification.ConditionProviderService;
import android.service.notification.IConditionProvider;
import android.util.TimeUtils;
import java.io.PrintWriter;
import java.util.Date;

public abstract class SystemConditionProviderService
  extends ConditionProviderService
{
  protected static void dumpUpcomingTime(PrintWriter paramPrintWriter, String paramString, long paramLong1, long paramLong2)
  {
    paramPrintWriter.print("      ");
    paramPrintWriter.print(paramString);
    paramPrintWriter.print('=');
    if (paramLong1 > 0L) {
      paramPrintWriter.printf("%s, in %s, now=%s", new Object[] { ts(paramLong1), formatDuration(paramLong1 - paramLong2), ts(paramLong2) });
    }
    for (;;)
    {
      paramPrintWriter.println();
      return;
      paramPrintWriter.print(paramLong1);
    }
  }
  
  protected static String formatDuration(long paramLong)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    TimeUtils.formatDuration(paramLong, localStringBuilder);
    return localStringBuilder.toString();
  }
  
  protected static String ts(long paramLong)
  {
    return new Date(paramLong) + " (" + paramLong + ")";
  }
  
  public abstract IConditionProvider asInterface();
  
  public abstract void attachBase(Context paramContext);
  
  public abstract void dump(PrintWriter paramPrintWriter, NotificationManagerService.DumpFilter paramDumpFilter);
  
  public abstract ComponentName getComponent();
  
  public abstract boolean isValidConditionId(Uri paramUri);
  
  public abstract void onBootComplete();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/SystemConditionProviderService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */