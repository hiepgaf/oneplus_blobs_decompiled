package com.android.server.am;

import android.app.EmbryoApp;
import android.app.IApplicationThread;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.Log;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

class EmbryoManager
  implements IEmbryoManager
{
  private static final boolean DEBUG = true;
  private static final boolean ENABLE = SystemProperties.getBoolean("persist.sys.embryo", true);
  private static boolean ENVIRONMENT = false;
  private static final long FLUSH_INTERVAL = 28800000L;
  private static final String TAG = "EmbryoManager";
  private static EmbryoHelper sHelper;
  private static EmbryoManagerWrapper sWrapperInstance;
  private Context mContext = null;
  private final BroadcastReceiver mDeviceIdleReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      Log.d("EmbryoManager", "receive " + paramAnonymousIntent.getAction());
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ((EmbryoManager.-get3(EmbryoManager.this) != null) && ("android.os.action.DEVICE_IDLE_MODE_CHANGED".equals(paramAnonymousContext)))
      {
        Log.d("EmbryoManager", "isDeviceIdleMode= " + EmbryoManager.-get3(EmbryoManager.this).isDeviceIdleMode());
        if ((!EmbryoManager.-get3(EmbryoManager.this).isDeviceIdleMode()) && ((EmbryoManager.-get2(EmbryoManager.this) == -1L) || (System.currentTimeMillis() - EmbryoManager.-get2(EmbryoManager.this) > 28800000L)))
        {
          EmbryoManager.-get4(EmbryoManager.this).scheduleBackup();
          EmbryoManager.-set0(EmbryoManager.this, System.currentTimeMillis());
        }
        return;
      }
    }
  };
  private long mLastFlush = -1L;
  private PowerManager mPm;
  private boolean mShuttingDown = false;
  private final Uterus mUterus = Uterus.getInstance();
  
  private EmbryoManager()
  {
    Log.d("EmbryoManager", "create Embryo Manager");
  }
  
  public static IEmbryoManager getInstance()
  {
    return sWrapperInstance;
  }
  
  public static IEmbryoManager getInstance(ActivityManagerService paramActivityManagerService)
  {
    if (sWrapperInstance != null) {
      return sWrapperInstance;
    }
    sHelper = new EmbryoHelper(paramActivityManagerService);
    ENVIRONMENT = sHelper.initEnvironment();
    sWrapperInstance = new EmbryoManagerWrapper(null);
    return sWrapperInstance;
  }
  
  private static final boolean isSupportType(String paramString)
  {
    if (!"activity".equals(paramString)) {
      return "broadcast".equals(paramString);
    }
    return true;
  }
  
  public static void resolveConfigCommon(IEmbryoManager paramIEmbryoManager, JSONArray paramJSONArray)
  {
    if (paramJSONArray == null)
    {
      Log.e("EmbryoManager", "[OnlineConfig] embryo jsonArray is null");
      return;
    }
    int i = 0;
    Object localObject2;
    int j;
    boolean bool1;
    for (;;)
    {
      try
      {
        if (i >= paramJSONArray.length()) {
          break label591;
        }
        localObject1 = paramJSONArray.getJSONObject(i);
        localObject2 = ((JSONObject)localObject1).getString("name");
        if (((String)localObject2).equals("embryo_blacklist"))
        {
          localObject1 = ((JSONObject)localObject1).getJSONArray("value");
          localObject2 = new ArrayList(((JSONArray)localObject1).length());
          j = 0;
          if (j < ((JSONArray)localObject1).length())
          {
            ((ArrayList)localObject2).add(((JSONArray)localObject1).getString(j));
            j += 1;
            continue;
          }
          if (((ArrayList)localObject2).size() > 0) {
            paramIEmbryoManager.setBlackList((List)localObject2);
          }
        }
        else if (((String)localObject2).equals("embryo_enable"))
        {
          bool1 = Boolean.valueOf(((JSONObject)localObject1).getJSONArray("value").getString(0)).booleanValue();
          boolean bool2 = SystemProperties.getBoolean("persist.sys.embryo", true);
          if (bool1 == bool2) {
            break label601;
          }
          if (bool1)
          {
            localObject1 = "1";
            SystemProperties.set("persist.sys.embryo", (String)localObject1);
            Log.v("EmbryoManager", "[OnlineConfig]set embryo enable " + bool1);
            sWrapperInstance.hotSwitch(bool1, bool2);
          }
        }
      }
      catch (Exception paramIEmbryoManager)
      {
        Log.e("EmbryoManager", "[OnlineConfig] resolve error message:" + paramIEmbryoManager.getMessage(), paramIEmbryoManager);
        return;
      }
      localObject1 = "0";
    }
    if (((String)localObject2).equals("embryo_inflate"))
    {
      bool1 = Boolean.valueOf(((JSONObject)localObject1).getJSONArray("value").getString(0)).booleanValue();
      if (bool1 != SystemProperties.getBoolean("persist.sys.embryo.inflate", true))
      {
        if (!bool1) {
          break label608;
        }
        localObject1 = "1";
        label307:
        SystemProperties.set("persist.sys.embryo.inflate", (String)localObject1);
        Log.v("EmbryoManager", "[OnlineConfig]set embryo inflate " + bool1);
      }
    }
    else if (((String)localObject2).equals("embryo_support_optheme"))
    {
      bool1 = Boolean.valueOf(((JSONObject)localObject1).getJSONArray("value").getString(0)).booleanValue();
      if (bool1 != SystemProperties.getBoolean("persist.sys.embryo.optheme", true))
      {
        if (!bool1) {
          break label615;
        }
        localObject1 = "1";
        label392:
        SystemProperties.set("persist.sys.embryo.optheme", (String)localObject1);
        Log.v("EmbryoManager", "[OnlineConfig]set embryo optheme " + bool1);
      }
    }
    else if (((String)localObject2).equals("embryo_rename"))
    {
      bool1 = Boolean.valueOf(((JSONObject)localObject1).getJSONArray("value").getString(0)).booleanValue();
      if (bool1 == SystemProperties.getBoolean("persist.sys.embryo.rename", true)) {
        break label601;
      }
      if (!bool1) {
        break label622;
      }
    }
    label591:
    label601:
    label608:
    label615:
    label622:
    for (Object localObject1 = "1";; localObject1 = "0")
    {
      SystemProperties.set("persist.sys.embryo.rename", (String)localObject1);
      Log.v("EmbryoManager", "[OnlineConfig]set embryo rename " + bool1);
      break label601;
      if (((String)localObject2).equals("embryo_limit_count"))
      {
        j = Integer.valueOf(((JSONObject)localObject1).getJSONArray("value").getString(0)).intValue();
        if (j != SystemProperties.getInt("persist.sys.embryo.optheme", 32))
        {
          SystemProperties.set("persist.sys.embryo.limit", String.valueOf(j));
          Log.v("EmbryoManager", "[OnlineConfig]set embryo limit " + j);
          break label601;
          Log.v("EmbryoManager", "[OnlineConfig] Embryo updated complete");
          return;
        }
      }
      i += 1;
      break;
      localObject1 = "0";
      break label307;
      localObject1 = "0";
      break label392;
    }
  }
  
  private void setupReceiver(Context paramContext)
  {
    this.mContext = paramContext;
    this.mPm = ((PowerManager)this.mContext.getSystemService("power"));
    paramContext = new IntentFilter("android.os.action.DEVICE_IDLE_MODE_CHANGED");
    this.mContext.registerReceiver(this.mDeviceIdleReceiver, paramContext);
  }
  
  public void activityTransition(ActivityRecord paramActivityRecord1, ActivityRecord paramActivityRecord2)
  {
    this.mUterus.resume(paramActivityRecord2.appInfo.packageName, paramActivityRecord2.isHomeActivity());
  }
  
  public boolean attach(IApplicationThread paramIApplicationThread, int paramInt)
  {
    if (!this.mUterus.attach(paramIApplicationThread, paramInt)) {
      return false;
    }
    Log.d("EmbryoManager", "Embryo attached, pid=" + paramInt);
    this.mUterus.trim();
    return true;
  }
  
  public boolean checkBackgroundLevel(List<ProcessRecord> paramList)
  {
    int k = 0;
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      ProcessRecord localProcessRecord = (ProcessRecord)paramList.get(i);
      int j = k;
      if (localProcessRecord.thread != null)
      {
        j = k;
        if (localProcessRecord.setProcState >= 14)
        {
          k += 1;
          j = k;
          if (k >= 10) {
            return true;
          }
        }
      }
      i -= 1;
      k = j;
    }
    return false;
  }
  
  public void cleanup()
  {
    this.mContext.unregisterReceiver(this.mDeviceIdleReceiver);
    this.mUterus.cleanup();
    this.mContext = null;
  }
  
  public void dumpsys(PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("Enabled");
    paramPrintWriter.println("Build: " + EmbryoApp.getVersion());
    try
    {
      this.mUterus.dumpsys(paramPrintWriter);
      return;
    }
    catch (Exception paramPrintWriter)
    {
      Log.e("EmbryoManager", "error while dumpsys ", paramPrintWriter);
    }
  }
  
  public void goingToSleep()
  {
    this.mUterus.goingToSleep();
  }
  
  public void initiate(Context paramContext)
  {
    this.mUterus.initiate();
    setupReceiver(paramContext);
    Log.d("EmbryoManager", "initiate");
  }
  
  public IApplicationThread obtain(ProcessRecord paramProcessRecord, String paramString)
  {
    if ((paramProcessRecord == null) || (paramProcessRecord.info == null)) {
      return null;
    }
    if (!isSupportType(paramString)) {
      return null;
    }
    ApplicationInfo localApplicationInfo = paramProcessRecord.info;
    if (localApplicationInfo.packageName.equals(SystemProperties.get("sys.embryo.block", "")))
    {
      Log.d("EmbryoManager", "Disable embryo by property:" + localApplicationInfo.packageName);
      return null;
    }
    if (!paramProcessRecord.processName.equals(localApplicationInfo.processName)) {
      return null;
    }
    if ("activity".equals(paramString)) {}
    for (paramString = this.mUterus.getOrCreateSupervisor(localApplicationInfo.packageName); paramString == null; paramString = this.mUterus.findSupervisor(localApplicationInfo.packageName)) {
      return null;
    }
    try
    {
      boolean bool = paramString.hasEmbryo();
      if (!bool) {
        return null;
      }
      Embryo localEmbryo = paramString.detach();
      if (!paramString.match(localApplicationInfo))
      {
        paramString.updateInfo(localApplicationInfo);
        Log.d("EmbryoManager", "not matched. " + localApplicationInfo.packageName + ", pid=" + localEmbryo.getPid());
        localEmbryo.destroy();
        return null;
      }
      Log.d("EmbryoManager", "Embryo claimed. " + localApplicationInfo.packageName + ", pid=" + localEmbryo.getPid());
      paramProcessRecord.setPid(localEmbryo.getPid());
      paramProcessRecord = localEmbryo.getThread();
      this.mUterus.finish(paramString);
      return paramProcessRecord;
    }
    finally {}
  }
  
  /* Error */
  public void packageChanged(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +4 -> 5
    //   4: return
    //   5: ldc 28
    //   7: new 204	java/lang/StringBuilder
    //   10: dup
    //   11: invokespecial 205	java/lang/StringBuilder:<init>	()V
    //   14: ldc_w 460
    //   17: invokevirtual 211	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_1
    //   21: invokevirtual 211	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 218	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokestatic 102	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   30: pop
    //   31: aload_0
    //   32: getfield 62	com/android/server/am/EmbryoManager:mUterus	Lcom/android/server/am/Uterus;
    //   35: aload_1
    //   36: invokevirtual 415	com/android/server/am/Uterus:findSupervisor	(Ljava/lang/String;)Lcom/android/server/am/EmbryoSupervisor;
    //   39: astore_1
    //   40: aload_1
    //   41: ifnonnull +4 -> 45
    //   44: return
    //   45: aload_1
    //   46: monitorenter
    //   47: aload_1
    //   48: invokevirtual 463	com/android/server/am/EmbryoSupervisor:isWaitingForFork	()Z
    //   51: ifeq +22 -> 73
    //   54: aload_1
    //   55: invokevirtual 466	com/android/server/am/EmbryoSupervisor:setAbortion	()V
    //   58: aload_1
    //   59: invokevirtual 469	com/android/server/am/EmbryoSupervisor:setSelfUpdate	()V
    //   62: aload_1
    //   63: monitorexit
    //   64: aload_0
    //   65: getfield 62	com/android/server/am/EmbryoManager:mUterus	Lcom/android/server/am/Uterus;
    //   68: aload_1
    //   69: invokevirtual 457	com/android/server/am/Uterus:finish	(Lcom/android/server/am/EmbryoSupervisor;)V
    //   72: return
    //   73: aload_1
    //   74: invokevirtual 470	com/android/server/am/EmbryoSupervisor:destroy	()V
    //   77: goto -19 -> 58
    //   80: astore_2
    //   81: aload_1
    //   82: monitorexit
    //   83: aload_2
    //   84: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	85	0	this	EmbryoManager
    //   0	85	1	paramString	String
    //   80	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   47	58	80	finally
    //   58	62	80	finally
    //   73	77	80	finally
  }
  
  public void packageInstalled(ApplicationInfo paramApplicationInfo)
  {
    if (paramApplicationInfo == null) {
      return;
    }
    Log.d("EmbryoManager", "packageInstalled " + paramApplicationInfo.packageName);
    if (!sHelper.checkIfNewPackageIsLaunchable(paramApplicationInfo)) {
      return;
    }
    paramApplicationInfo = this.mUterus.getOrCreateSupervisor(paramApplicationInfo.packageName);
    if (paramApplicationInfo == null) {
      return;
    }
    this.mUterus.prepare(paramApplicationInfo, 0, true);
  }
  
  public void prepare(ProcessRecord paramProcessRecord)
  {
    if ((paramProcessRecord == null) || (paramProcessRecord.info == null)) {}
    while ((paramProcessRecord.isolated) || (this.mShuttingDown)) {
      return;
    }
    EmbryoSupervisor localEmbryoSupervisor = this.mUterus.findSupervisor(paramProcessRecord.info.packageName);
    if (localEmbryoSupervisor == null) {
      return;
    }
    Log.d("EmbryoManager", "prepare " + paramProcessRecord.info.packageName);
    this.mUterus.prepare(localEmbryoSupervisor, 3, true);
  }
  
  public void resolveConfig(JSONArray paramJSONArray)
  {
    resolveConfigCommon(this, paramJSONArray);
  }
  
  public void setBlackList(List paramList)
  {
    this.mUterus.setBlackList(paramList);
    Log.d("EmbryoManager", "update list");
  }
  
  public void shutdown()
  {
    this.mShuttingDown = true;
    this.mUterus.shutdown();
  }
  
  public void updateConfig()
  {
    this.mUterus.updateConfig();
    Log.d("EmbryoManager", "update config");
  }
  
  public void wakingUp() {}
  
  private static final class EmbryoManagerWrapper
    implements IEmbryoManager
  {
    private final IEmbryoManager nullImpl = new EmbryoManager.UselessManager(null);
    private EmbryoManager realImpl;
    private IEmbryoManager target;
    
    private EmbryoManagerWrapper()
    {
      if ((EmbryoManager.-get0()) && (EmbryoManager.-get1()))
      {
        this.realImpl = new EmbryoManager(null);
        this.target = this.realImpl;
        return;
      }
      this.target = this.nullImpl;
    }
    
    private IEmbryoManager getImpl()
    {
      try
      {
        IEmbryoManager localIEmbryoManager = this.target;
        return localIEmbryoManager;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void activityTransition(ActivityRecord paramActivityRecord1, ActivityRecord paramActivityRecord2)
    {
      getImpl().activityTransition(paramActivityRecord1, paramActivityRecord2);
    }
    
    public boolean attach(IApplicationThread paramIApplicationThread, int paramInt)
    {
      return getImpl().attach(paramIApplicationThread, paramInt);
    }
    
    public boolean checkBackgroundLevel(List<ProcessRecord> paramList)
    {
      return getImpl().checkBackgroundLevel(paramList);
    }
    
    public void dumpsys(PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      getImpl().dumpsys(paramPrintWriter, paramArrayOfString);
    }
    
    public void goingToSleep()
    {
      getImpl().goingToSleep();
    }
    
    public void hotSwitch(boolean paramBoolean1, boolean paramBoolean2)
    {
      if (paramBoolean1) {}
      for (;;)
      {
        try
        {
          this.target = this.nullImpl;
          return;
        }
        finally {}
        if (this.realImpl != null)
        {
          this.realImpl.shutdown();
          this.realImpl.cleanup();
          this.realImpl = null;
        }
        this.target = this.nullImpl;
      }
    }
    
    public void initiate(Context paramContext)
    {
      getImpl().initiate(paramContext);
    }
    
    public IApplicationThread obtain(ProcessRecord paramProcessRecord, String paramString)
    {
      return getImpl().obtain(paramProcessRecord, paramString);
    }
    
    public void packageChanged(String paramString)
    {
      getImpl().packageChanged(paramString);
    }
    
    public void packageInstalled(ApplicationInfo paramApplicationInfo)
    {
      getImpl().packageInstalled(paramApplicationInfo);
    }
    
    public void prepare(ProcessRecord paramProcessRecord)
    {
      getImpl().prepare(paramProcessRecord);
    }
    
    public void resolveConfig(JSONArray paramJSONArray)
    {
      getImpl().resolveConfig(paramJSONArray);
    }
    
    public void setBlackList(List paramList)
    {
      getImpl().setBlackList(paramList);
    }
    
    public void shutdown()
    {
      getImpl().shutdown();
    }
    
    public void updateConfig()
    {
      getImpl().updateConfig();
    }
    
    public void wakingUp()
    {
      getImpl().wakingUp();
    }
  }
  
  private static final class UselessManager
    implements IEmbryoManager
  {
    public void activityTransition(ActivityRecord paramActivityRecord1, ActivityRecord paramActivityRecord2) {}
    
    public boolean attach(IApplicationThread paramIApplicationThread, int paramInt)
    {
      return false;
    }
    
    public boolean checkBackgroundLevel(List<ProcessRecord> paramList)
    {
      return false;
    }
    
    public void dumpsys(PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramPrintWriter.println("Disabled");
    }
    
    public void goingToSleep() {}
    
    public void initiate(Context paramContext) {}
    
    public IApplicationThread obtain(ProcessRecord paramProcessRecord, String paramString)
    {
      return null;
    }
    
    public void packageChanged(String paramString) {}
    
    public void packageInstalled(ApplicationInfo paramApplicationInfo) {}
    
    public void prepare(ProcessRecord paramProcessRecord) {}
    
    public void resolveConfig(JSONArray paramJSONArray)
    {
      EmbryoManager.resolveConfigCommon(this, paramJSONArray);
    }
    
    public void setBlackList(List paramList) {}
    
    public void shutdown() {}
    
    public void updateConfig() {}
    
    public void wakingUp() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/EmbryoManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */