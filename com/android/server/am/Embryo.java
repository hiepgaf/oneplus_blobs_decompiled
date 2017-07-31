package com.android.server.am;

import android.app.IApplicationThread;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

class Embryo
{
  private static final String TAG = "Embryo";
  private final ApplicationInfo info;
  private final String packageName;
  private int pid = -1;
  private IApplicationThread thread = null;
  
  public Embryo(String paramString, ApplicationInfo paramApplicationInfo)
  {
    this.packageName = paramString;
    this.info = paramApplicationInfo;
  }
  
  public void destroy()
  {
    if (this.pid != -1) {
      Process.killProcessQuiet(this.pid);
    }
    this.pid = -1;
  }
  
  public ApplicationInfo getInfo()
  {
    return this.info;
  }
  
  public String getPackageName()
  {
    return this.packageName;
  }
  
  public int getPid()
  {
    return this.pid;
  }
  
  public IApplicationThread getThread()
  {
    return this.thread;
  }
  
  public boolean isAlive()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    try
    {
      if (this.pid != -1)
      {
        bool1 = bool2;
        if (this.thread != null) {
          bool1 = this.thread.asBinder().isBinderAlive();
        }
      }
      return bool1;
    }
    catch (Exception localException) {}
    return false;
  }
  
  public void link(IBinder.DeathRecipient paramDeathRecipient)
  {
    if (this.thread == null) {
      return;
    }
    try
    {
      this.thread.asBinder().linkToDeath(paramDeathRecipient, 0);
      return;
    }
    catch (RemoteException paramDeathRecipient)
    {
      Log.d("Embryo", "linkToDeath failed. " + this.packageName);
    }
  }
  
  public void setPid(int paramInt)
  {
    this.pid = paramInt;
  }
  
  public void setThread(IApplicationThread paramIApplicationThread)
  {
    this.thread = paramIApplicationThread;
  }
  
  public void unlink(IBinder.DeathRecipient paramDeathRecipient)
  {
    if (this.thread != null) {
      this.thread.asBinder().unlinkToDeath(paramDeathRecipient, 0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/Embryo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */