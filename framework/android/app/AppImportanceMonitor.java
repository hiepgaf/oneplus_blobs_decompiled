package android.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.SparseArray;
import java.util.List;

public class AppImportanceMonitor
{
  static final int MSG_UPDATE = 1;
  final SparseArray<AppEntry> mApps = new SparseArray();
  final Context mContext;
  final Handler mHandler;
  final IProcessObserver mProcessObserver = new IProcessObserver.Stub()
  {
    public void onForegroundActivitiesChanged(int paramAnonymousInt1, int paramAnonymousInt2, boolean paramAnonymousBoolean) {}
    
    public void onProcessDied(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      synchronized (AppImportanceMonitor.this.mApps)
      {
        AppImportanceMonitor.this.updateImportanceLocked(paramAnonymousInt1, paramAnonymousInt2, 1000, true);
        return;
      }
    }
    
    public void onProcessStateChanged(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      synchronized (AppImportanceMonitor.this.mApps)
      {
        AppImportanceMonitor.this.updateImportanceLocked(paramAnonymousInt1, paramAnonymousInt2, ActivityManager.RunningAppProcessInfo.procStateToImportance(paramAnonymousInt3), true);
        return;
      }
    }
  };
  
  public AppImportanceMonitor(Context paramContext, Looper paramLooper)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler(paramLooper)
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        switch (paramAnonymousMessage.what)
        {
        default: 
          super.handleMessage(paramAnonymousMessage);
          return;
        }
        AppImportanceMonitor.this.onImportanceChanged(paramAnonymousMessage.arg1, paramAnonymousMessage.arg2 & 0xFFFF, paramAnonymousMessage.arg2 >> 16);
      }
    };
    paramContext = (ActivityManager)paramContext.getSystemService("activity");
    try
    {
      ActivityManagerNative.getDefault().registerProcessObserver(this.mProcessObserver);
      paramContext = paramContext.getRunningAppProcesses();
      if (paramContext != null)
      {
        int i = 0;
        while (i < paramContext.size())
        {
          paramLooper = (ActivityManager.RunningAppProcessInfo)paramContext.get(i);
          updateImportanceLocked(paramLooper.uid, paramLooper.pid, paramLooper.importance, false);
          i += 1;
        }
      }
      return;
    }
    catch (RemoteException paramLooper)
    {
      for (;;) {}
    }
  }
  
  public int getImportance(int paramInt)
  {
    AppEntry localAppEntry = (AppEntry)this.mApps.get(paramInt);
    if (localAppEntry == null) {
      return 1000;
    }
    return localAppEntry.importance;
  }
  
  public void onImportanceChanged(int paramInt1, int paramInt2, int paramInt3) {}
  
  void updateImportanceLocked(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    AppEntry localAppEntry2 = (AppEntry)this.mApps.get(paramInt1);
    AppEntry localAppEntry1 = localAppEntry2;
    if (localAppEntry2 == null)
    {
      localAppEntry1 = new AppEntry(paramInt1);
      this.mApps.put(paramInt1, localAppEntry1);
    }
    if (paramInt3 >= 1000) {
      localAppEntry1.procs.remove(paramInt2);
    }
    for (;;)
    {
      updateImportanceLocked(localAppEntry1, paramBoolean);
      return;
      localAppEntry1.procs.put(paramInt2, Integer.valueOf(paramInt3));
    }
  }
  
  void updateImportanceLocked(AppEntry paramAppEntry, boolean paramBoolean)
  {
    int i = 1000;
    int j = 0;
    while (j < paramAppEntry.procs.size())
    {
      int m = ((Integer)paramAppEntry.procs.valueAt(j)).intValue();
      int k = i;
      if (m < i) {
        k = m;
      }
      j += 1;
      i = k;
    }
    if (i != paramAppEntry.importance)
    {
      j = paramAppEntry.importance;
      paramAppEntry.importance = i;
      if (i >= 1000) {
        this.mApps.remove(paramAppEntry.uid);
      }
      if (paramBoolean) {
        this.mHandler.obtainMessage(1, paramAppEntry.uid, i | j << 16).sendToTarget();
      }
    }
  }
  
  static class AppEntry
  {
    int importance = 1000;
    final SparseArray<Integer> procs = new SparseArray(1);
    final int uid;
    
    AppEntry(int paramInt)
    {
      this.uid = paramInt;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/AppImportanceMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */