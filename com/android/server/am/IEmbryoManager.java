package com.android.server.am;

import android.app.IApplicationThread;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.io.PrintWriter;
import java.util.List;
import org.json.JSONArray;

abstract interface IEmbryoManager
{
  public abstract void activityTransition(ActivityRecord paramActivityRecord1, ActivityRecord paramActivityRecord2);
  
  public abstract boolean attach(IApplicationThread paramIApplicationThread, int paramInt);
  
  public abstract boolean checkBackgroundLevel(List<ProcessRecord> paramList);
  
  public abstract void dumpsys(PrintWriter paramPrintWriter, String[] paramArrayOfString);
  
  public abstract void goingToSleep();
  
  public abstract void initiate(Context paramContext);
  
  public abstract IApplicationThread obtain(ProcessRecord paramProcessRecord, String paramString);
  
  public abstract void packageChanged(String paramString);
  
  public abstract void packageInstalled(ApplicationInfo paramApplicationInfo);
  
  public abstract void prepare(ProcessRecord paramProcessRecord);
  
  public abstract void resolveConfig(JSONArray paramJSONArray);
  
  public abstract void setBlackList(List paramList);
  
  public abstract void shutdown();
  
  public abstract void updateConfig();
  
  public abstract void wakingUp();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/IEmbryoManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */