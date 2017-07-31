package com.android.server;

import android.content.Context;
import android.os.AsyncTask;
import java.util.concurrent.Executor;
import net.oneplus.odm.insight.tracker.OSTracker;

public class SystemEventCollector
{
  private Context mContext;
  private String mModuleName;
  private OSTracker mTracker;
  
  public SystemEventCollector(Context paramContext, String paramString)
  {
    this.mContext = paramContext;
    this.mModuleName = paramString;
    this.mTracker = new OSTracker(paramContext);
  }
  
  public void submit(final SystemEvent paramSystemEvent)
  {
    AsyncTask.SERIAL_EXECUTOR.execute(new Runnable()
    {
      public void run()
      {
        SystemEventCollector.-get1(SystemEventCollector.this).onEvent(SystemEventCollector.-get0(SystemEventCollector.this) + "_" + paramSystemEvent.name, paramSystemEvent);
      }
    });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/SystemEventCollector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */