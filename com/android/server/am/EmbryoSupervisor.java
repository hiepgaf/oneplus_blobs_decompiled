package com.android.server.am;

import android.content.pm.ApplicationInfo;
import android.os.Debug;
import android.os.Debug.MemoryInfo;
import android.os.IBinder.DeathRecipient;
import android.os.UserHandle;
import android.util.Log;
import java.io.PrintWriter;
import java.util.Comparator;

class EmbryoSupervisor
  implements IBinder.DeathRecipient
{
  private static final boolean DEBUG = true;
  private static final String TAG = "EmbryoSupervisor";
  private boolean doAbortion = false;
  private Embryo embryo;
  private long foregroundTime = 0L;
  private ApplicationInfo info;
  private boolean isLaunchable = false;
  private long launchCount = 0L;
  private EmbryoHelper mHelper = EmbryoHelper.getInstance();
  private boolean needUpdateSelf = false;
  private String packageName;
  private int pid;
  private int rank;
  private long startTime = 0L;
  private boolean waitingforFork = false;
  
  EmbryoSupervisor(String paramString)
  {
    this.packageName = paramString;
    this.needUpdateSelf = true;
    this.pid = 0;
  }
  
  private void updateSelf()
  {
    this.isLaunchable = this.mHelper.checkIfPackageIsLaunchable(this.packageName);
    this.info = this.mHelper.getApplicationInfo(this.packageName, 1024, UserHandle.getCallingUserId());
    this.needUpdateSelf = false;
  }
  
  public void attach(Embryo paramEmbryo)
  {
    if (this.embryo != null)
    {
      Log.e("EmbryoSupervisor", "set embryo twice?");
      this.embryo.unlink(this);
      this.embryo.destroy();
      this.embryo = null;
    }
    this.embryo = paramEmbryo;
    this.embryo.link(this);
    this.pid = this.embryo.getPid();
  }
  
  public void binderDied()
  {
    Log.d("EmbryoSupervisor", "Embryo child process died. " + this.packageName + ", pid=" + this.pid);
    try
    {
      this.embryo.unlink(this);
      this.embryo.destroy();
      this.embryo = null;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void destroy()
  {
    this.pid = 0;
    if (this.embryo == null) {
      return;
    }
    try
    {
      this.embryo.unlink(this);
      this.embryo.destroy();
      this.embryo = null;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public Embryo detach()
  {
    if (this.embryo == null) {
      return null;
    }
    Embryo localEmbryo = this.embryo;
    this.embryo.unlink(this);
    this.embryo = null;
    this.pid = 0;
    return localEmbryo;
  }
  
  public void dump()
  {
    Log.d("EmbryoSupervisor", "pkg=" + this.packageName + ", fg=" + this.foregroundTime + ", count=" + this.launchCount);
  }
  
  public void dump(PrintWriter paramPrintWriter, Uterus.EmbryoMemory paramEmbryoMemory)
  {
    if (this.pid == 0) {
      return;
    }
    Debug.MemoryInfo localMemoryInfo = new Debug.MemoryInfo();
    Debug.getMemoryInfo(this.pid, localMemoryInfo);
    paramEmbryoMemory.update(localMemoryInfo);
    paramPrintWriter.println("pid=" + this.pid + ", name=" + this.packageName + ", pss=" + localMemoryInfo.getTotalPss() + ", uss=" + localMemoryInfo.getTotalUss());
  }
  
  public long getCount()
  {
    return this.launchCount;
  }
  
  public long getForegroundTime()
  {
    return this.foregroundTime;
  }
  
  public ApplicationInfo getInfo()
  {
    if (this.needUpdateSelf) {
      updateSelf();
    }
    return this.info;
  }
  
  public String getPackageName()
  {
    return this.packageName;
  }
  
  public int getRank()
  {
    return this.rank;
  }
  
  public boolean hasEmbryo()
  {
    if (this.embryo != null) {
      return this.embryo.isAlive();
    }
    return false;
  }
  
  public boolean isLaunchable()
  {
    if (this.needUpdateSelf) {
      updateSelf();
    }
    return this.isLaunchable;
  }
  
  public boolean isWaitingForFork()
  {
    return this.waitingforFork;
  }
  
  public boolean match(ApplicationInfo paramApplicationInfo)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramApplicationInfo.versionCode == this.info.versionCode)
    {
      bool1 = bool2;
      if (paramApplicationInfo.uid == this.info.uid) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean needAbortion()
  {
    return this.doAbortion;
  }
  
  public void restoreForegroundTime(long paramLong)
  {
    this.foregroundTime = paramLong;
  }
  
  public void setAbortion()
  {
    this.doAbortion = true;
  }
  
  public void setRank(int paramInt)
  {
    this.rank = paramInt;
  }
  
  public void setSelfUpdate()
  {
    this.needUpdateSelf = true;
  }
  
  public void setWaitingForFork(boolean paramBoolean)
  {
    this.waitingforFork = paramBoolean;
    if (!this.waitingforFork) {
      this.doAbortion = false;
    }
  }
  
  public void start()
  {
    this.startTime = System.currentTimeMillis();
    this.launchCount += 1L;
  }
  
  public void stop()
  {
    long l1 = System.currentTimeMillis();
    long l2 = this.startTime;
    this.foregroundTime += l1 - l2;
  }
  
  public void updateInfo(ApplicationInfo paramApplicationInfo)
  {
    this.info = paramApplicationInfo;
  }
  
  public static final class HighToLowComparator
    implements Comparator<EmbryoSupervisor>
  {
    public int compare(EmbryoSupervisor paramEmbryoSupervisor1, EmbryoSupervisor paramEmbryoSupervisor2)
    {
      Long localLong3 = Long.valueOf(EmbryoSupervisor.-get0(paramEmbryoSupervisor1));
      Long localLong4 = Long.valueOf(EmbryoSupervisor.-get0(paramEmbryoSupervisor2));
      Long localLong2 = localLong3;
      Long localLong1 = localLong4;
      if (localLong3 == localLong4)
      {
        localLong2 = Long.valueOf(EmbryoSupervisor.-get1(paramEmbryoSupervisor1));
        localLong1 = Long.valueOf(EmbryoSupervisor.-get1(paramEmbryoSupervisor2));
      }
      return localLong1.compareTo(localLong2);
    }
  }
  
  public static final class LowToHighComparator
    implements Comparator<EmbryoSupervisor>
  {
    public int compare(EmbryoSupervisor paramEmbryoSupervisor1, EmbryoSupervisor paramEmbryoSupervisor2)
    {
      Long localLong3 = Long.valueOf(EmbryoSupervisor.-get0(paramEmbryoSupervisor1));
      Long localLong4 = Long.valueOf(EmbryoSupervisor.-get0(paramEmbryoSupervisor2));
      Long localLong2 = localLong3;
      Long localLong1 = localLong4;
      if (localLong3 == localLong4)
      {
        localLong2 = Long.valueOf(EmbryoSupervisor.-get1(paramEmbryoSupervisor1));
        localLong1 = Long.valueOf(EmbryoSupervisor.-get1(paramEmbryoSupervisor2));
      }
      return localLong2.compareTo(localLong1);
    }
  }
  
  public static final class RankComparator
    implements Comparator<EmbryoSupervisor>
  {
    public int compare(EmbryoSupervisor paramEmbryoSupervisor1, EmbryoSupervisor paramEmbryoSupervisor2)
    {
      return Integer.valueOf(EmbryoSupervisor.-get2(paramEmbryoSupervisor1)).compareTo(Integer.valueOf(EmbryoSupervisor.-get2(paramEmbryoSupervisor2)));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/EmbryoSupervisor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */