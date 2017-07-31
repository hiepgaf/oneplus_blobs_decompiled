package com.android.server.am;

import android.app.IActivityManager.ContentProviderHolder;
import android.content.ComponentName;
import android.content.IContentProvider;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

final class ContentProviderRecord
{
  final ApplicationInfo appInfo;
  final ArrayList<ContentProviderConnection> connections = new ArrayList();
  int externalProcessNoHandleCount;
  HashMap<IBinder, ExternalProcessHandle> externalProcessTokenToHandle;
  public final ProviderInfo info;
  ProcessRecord launchingApp;
  final ComponentName name;
  public boolean noReleaseNeeded;
  ProcessRecord proc;
  public IContentProvider provider;
  final ActivityManagerService service;
  String shortStringName;
  final boolean singleton;
  String stringName;
  final int uid;
  
  public ContentProviderRecord(ActivityManagerService paramActivityManagerService, ProviderInfo paramProviderInfo, ApplicationInfo paramApplicationInfo, ComponentName paramComponentName, boolean paramBoolean)
  {
    this.service = paramActivityManagerService;
    this.info = paramProviderInfo;
    this.uid = paramApplicationInfo.uid;
    this.appInfo = paramApplicationInfo;
    this.name = paramComponentName;
    this.singleton = paramBoolean;
    paramBoolean = bool;
    if (this.uid != 0) {
      if (this.uid != 1000) {
        break label85;
      }
    }
    label85:
    for (paramBoolean = bool;; paramBoolean = false)
    {
      this.noReleaseNeeded = paramBoolean;
      return;
    }
  }
  
  public ContentProviderRecord(ContentProviderRecord paramContentProviderRecord)
  {
    this.service = paramContentProviderRecord.service;
    this.info = paramContentProviderRecord.info;
    this.uid = paramContentProviderRecord.uid;
    this.appInfo = paramContentProviderRecord.appInfo;
    this.name = paramContentProviderRecord.name;
    this.singleton = paramContentProviderRecord.singleton;
    this.noReleaseNeeded = paramContentProviderRecord.noReleaseNeeded;
  }
  
  private void removeExternalProcessHandleInternalLocked(IBinder paramIBinder)
  {
    ((ExternalProcessHandle)this.externalProcessTokenToHandle.get(paramIBinder)).unlinkFromOwnDeathLocked();
    this.externalProcessTokenToHandle.remove(paramIBinder);
    if (this.externalProcessTokenToHandle.size() == 0) {
      this.externalProcessTokenToHandle = null;
    }
  }
  
  public void addExternalProcessHandleLocked(IBinder paramIBinder)
  {
    if (paramIBinder == null)
    {
      this.externalProcessNoHandleCount += 1;
      return;
    }
    if (this.externalProcessTokenToHandle == null) {
      this.externalProcessTokenToHandle = new HashMap();
    }
    ExternalProcessHandle localExternalProcessHandle2 = (ExternalProcessHandle)this.externalProcessTokenToHandle.get(paramIBinder);
    ExternalProcessHandle localExternalProcessHandle1 = localExternalProcessHandle2;
    if (localExternalProcessHandle2 == null)
    {
      localExternalProcessHandle1 = new ExternalProcessHandle(paramIBinder);
      this.externalProcessTokenToHandle.put(paramIBinder, localExternalProcessHandle1);
    }
    ExternalProcessHandle.-set0(localExternalProcessHandle1, ExternalProcessHandle.-get0(localExternalProcessHandle1) + 1);
  }
  
  public boolean canRunHere(ProcessRecord paramProcessRecord)
  {
    boolean bool2 = false;
    boolean bool1;
    if (!this.info.multiprocess)
    {
      bool1 = bool2;
      if (!this.info.processName.equals(paramProcessRecord.processName)) {}
    }
    else
    {
      bool1 = bool2;
      if (this.uid == paramProcessRecord.info.uid) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("package=");
      paramPrintWriter.print(this.info.applicationInfo.packageName);
      paramPrintWriter.print(" process=");
      paramPrintWriter.println(this.info.processName);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("proc=");
    paramPrintWriter.println(this.proc);
    if (this.launchingApp != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("launchingApp=");
      paramPrintWriter.println(this.launchingApp);
    }
    if (paramBoolean)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("uid=");
      paramPrintWriter.print(this.uid);
      paramPrintWriter.print(" provider=");
      paramPrintWriter.println(this.provider);
    }
    if (this.singleton)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("singleton=");
      paramPrintWriter.println(this.singleton);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("authority=");
    paramPrintWriter.println(this.info.authority);
    if ((paramBoolean) && ((this.info.isSyncable) || (this.info.multiprocess) || (this.info.initOrder != 0)))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("isSyncable=");
      paramPrintWriter.print(this.info.isSyncable);
      paramPrintWriter.print(" multiprocess=");
      paramPrintWriter.print(this.info.multiprocess);
      paramPrintWriter.print(" initOrder=");
      paramPrintWriter.println(this.info.initOrder);
    }
    if (paramBoolean) {
      if (hasExternalProcessHandles())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("externals:");
        if (this.externalProcessTokenToHandle != null)
        {
          paramPrintWriter.print(" w/token=");
          paramPrintWriter.print(this.externalProcessTokenToHandle.size());
        }
        if (this.externalProcessNoHandleCount > 0)
        {
          paramPrintWriter.print(" notoken=");
          paramPrintWriter.print(this.externalProcessNoHandleCount);
        }
        paramPrintWriter.println();
      }
    }
    while (this.connections.size() > 0)
    {
      if (paramBoolean)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Connections:");
      }
      int i = 0;
      while (i < this.connections.size())
      {
        ContentProviderConnection localContentProviderConnection = (ContentProviderConnection)this.connections.get(i);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  -> ");
        paramPrintWriter.println(localContentProviderConnection.toClientString());
        if (localContentProviderConnection.provider != this)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("    *** WRONG PROVIDER: ");
          paramPrintWriter.println(localContentProviderConnection.provider);
        }
        i += 1;
      }
      if ((this.connections.size() > 0) || (this.externalProcessNoHandleCount > 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print(this.connections.size());
        paramPrintWriter.print(" connections, ");
        paramPrintWriter.print(this.externalProcessNoHandleCount);
        paramPrintWriter.println(" external handles");
      }
    }
  }
  
  public boolean hasConnectionOrHandle()
  {
    if (this.connections.isEmpty()) {
      return hasExternalProcessHandles();
    }
    return true;
  }
  
  public boolean hasExternalProcessHandles()
  {
    return (this.externalProcessTokenToHandle != null) || (this.externalProcessNoHandleCount > 0);
  }
  
  public IActivityManager.ContentProviderHolder newHolder(ContentProviderConnection paramContentProviderConnection)
  {
    IActivityManager.ContentProviderHolder localContentProviderHolder = new IActivityManager.ContentProviderHolder(this.info);
    localContentProviderHolder.provider = this.provider;
    localContentProviderHolder.noReleaseNeeded = this.noReleaseNeeded;
    localContentProviderHolder.connection = paramContentProviderConnection;
    return localContentProviderHolder;
  }
  
  public boolean removeExternalProcessHandleLocked(IBinder paramIBinder)
  {
    if (hasExternalProcessHandles())
    {
      int j = 0;
      int i = j;
      if (this.externalProcessTokenToHandle != null)
      {
        ExternalProcessHandle localExternalProcessHandle = (ExternalProcessHandle)this.externalProcessTokenToHandle.get(paramIBinder);
        i = j;
        if (localExternalProcessHandle != null)
        {
          i = 1;
          ExternalProcessHandle.-set0(localExternalProcessHandle, ExternalProcessHandle.-get0(localExternalProcessHandle) - 1);
          if (ExternalProcessHandle.-get0(localExternalProcessHandle) == 0)
          {
            removeExternalProcessHandleInternalLocked(paramIBinder);
            return true;
          }
        }
      }
      if (i == 0)
      {
        this.externalProcessNoHandleCount -= 1;
        return true;
      }
    }
    return false;
  }
  
  public String toShortString()
  {
    if (this.shortStringName != null) {
      return this.shortStringName;
    }
    Object localObject = new StringBuilder(128);
    ((StringBuilder)localObject).append(Integer.toHexString(System.identityHashCode(this)));
    ((StringBuilder)localObject).append('/');
    ((StringBuilder)localObject).append(this.name.flattenToShortString());
    localObject = ((StringBuilder)localObject).toString();
    this.shortStringName = ((String)localObject);
    return (String)localObject;
  }
  
  public String toString()
  {
    if (this.stringName != null) {
      return this.stringName;
    }
    Object localObject = new StringBuilder(128);
    ((StringBuilder)localObject).append("ContentProviderRecord{");
    ((StringBuilder)localObject).append(Integer.toHexString(System.identityHashCode(this)));
    ((StringBuilder)localObject).append(" u");
    ((StringBuilder)localObject).append(UserHandle.getUserId(this.uid));
    ((StringBuilder)localObject).append(' ');
    ((StringBuilder)localObject).append(this.name.flattenToShortString());
    ((StringBuilder)localObject).append('}');
    localObject = ((StringBuilder)localObject).toString();
    this.stringName = ((String)localObject);
    return (String)localObject;
  }
  
  private class ExternalProcessHandle
    implements IBinder.DeathRecipient
  {
    private static final String LOG_TAG = "ExternalProcessHanldle";
    private int mAcquisitionCount;
    private final IBinder mToken;
    
    public ExternalProcessHandle(IBinder paramIBinder)
    {
      this.mToken = paramIBinder;
      try
      {
        paramIBinder.linkToDeath(this, 0);
        return;
      }
      catch (RemoteException this$1)
      {
        Slog.e("ExternalProcessHanldle", "Couldn't register for death for token: " + this.mToken, ContentProviderRecord.this);
      }
    }
    
    public void binderDied()
    {
      synchronized (ContentProviderRecord.this.service)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        if ((ContentProviderRecord.this.hasExternalProcessHandles()) && (ContentProviderRecord.this.externalProcessTokenToHandle.get(this.mToken) != null)) {
          ContentProviderRecord.-wrap0(ContentProviderRecord.this, this.mToken);
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
    
    public void unlinkFromOwnDeathLocked()
    {
      this.mToken.unlinkToDeath(this, 0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ContentProviderRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */