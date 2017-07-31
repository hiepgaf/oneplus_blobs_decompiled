package com.android.server.am;

import android.app.IInstrumentationWatcher;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.util.Slog;
import java.util.ArrayList;

public class InstrumentationReporter
{
  static final boolean DEBUG = false;
  static final int REPORT_TYPE_FINISHED = 1;
  static final int REPORT_TYPE_STATUS = 0;
  static final String TAG = "ActivityManager";
  final Object mLock = new Object();
  ArrayList<Report> mPendingReports;
  Thread mThread;
  
  private void report(Report paramReport)
  {
    synchronized (this.mLock)
    {
      if (this.mThread == null)
      {
        this.mThread = new MyThread();
        this.mThread.start();
      }
      if (this.mPendingReports == null) {
        this.mPendingReports = new ArrayList();
      }
      this.mPendingReports.add(paramReport);
      this.mLock.notifyAll();
      return;
    }
  }
  
  public void reportFinished(IInstrumentationWatcher paramIInstrumentationWatcher, ComponentName paramComponentName, int paramInt, Bundle paramBundle)
  {
    report(new Report(1, paramIInstrumentationWatcher, paramComponentName, paramInt, paramBundle));
  }
  
  public void reportStatus(IInstrumentationWatcher paramIInstrumentationWatcher, ComponentName paramComponentName, int paramInt, Bundle paramBundle)
  {
    report(new Report(0, paramIInstrumentationWatcher, paramComponentName, paramInt, paramBundle));
  }
  
  final class MyThread
    extends Thread
  {
    public MyThread()
    {
      super();
    }
    
    public void run()
    {
      Process.setThreadPriority(0);
      int i = 0;
      for (;;)
      {
        ArrayList localArrayList;
        synchronized (InstrumentationReporter.this.mLock)
        {
          localArrayList = InstrumentationReporter.this.mPendingReports;
          InstrumentationReporter.this.mPendingReports = null;
          if (localArrayList != null)
          {
            boolean bool = localArrayList.isEmpty();
            if (!bool) {}
          }
          else if (i != 0) {}
        }
        try
        {
          InstrumentationReporter.this.mLock.wait(10000L);
          i = 1;
          continue;
          InstrumentationReporter.this.mThread = null;
          return;
          int k = 0;
          int j = 0;
          for (;;)
          {
            i = k;
            if (j >= localArrayList.size()) {
              break;
            }
            ??? = (InstrumentationReporter.Report)localArrayList.get(j);
            try
            {
              if (((InstrumentationReporter.Report)???).mType == 0) {
                ((InstrumentationReporter.Report)???).mWatcher.instrumentationStatus(((InstrumentationReporter.Report)???).mName, ((InstrumentationReporter.Report)???).mResultCode, ((InstrumentationReporter.Report)???).mResults);
              }
              for (;;)
              {
                j += 1;
                break;
                localObject2 = finally;
                throw ((Throwable)localObject2);
                ((InstrumentationReporter.Report)???).mWatcher.instrumentationFinished(((InstrumentationReporter.Report)???).mName, ((InstrumentationReporter.Report)???).mResultCode, ((InstrumentationReporter.Report)???).mResults);
              }
            }
            catch (RemoteException localRemoteException)
            {
              for (;;)
              {
                Slog.i("ActivityManager", "Failure reporting to instrumentation watcher: comp=" + ((InstrumentationReporter.Report)???).mName + " results=" + ((InstrumentationReporter.Report)???).mResults);
              }
            }
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          for (;;) {}
        }
      }
    }
  }
  
  final class Report
  {
    final ComponentName mName;
    final int mResultCode;
    final Bundle mResults;
    final int mType;
    final IInstrumentationWatcher mWatcher;
    
    Report(int paramInt1, IInstrumentationWatcher paramIInstrumentationWatcher, ComponentName paramComponentName, int paramInt2, Bundle paramBundle)
    {
      this.mType = paramInt1;
      this.mWatcher = paramIInstrumentationWatcher;
      this.mName = paramComponentName;
      this.mResultCode = paramInt2;
      this.mResults = paramBundle;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/InstrumentationReporter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */