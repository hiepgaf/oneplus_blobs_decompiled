package android.app;

import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import android.util.Log;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.content.ReferrerIntent;
import java.io.FileDescriptor;
import java.util.List;
import java.util.Map;

class ApplicationThreadProxy
  implements IApplicationThread
{
  private final IBinder mRemote;
  
  public ApplicationThreadProxy(IBinder paramIBinder)
  {
    this.mRemote = paramIBinder;
  }
  
  public final IBinder asBinder()
  {
    return this.mRemote;
  }
  
  public final void bindApplication(String paramString, ApplicationInfo paramApplicationInfo, List<ProviderInfo> paramList, ComponentName paramComponentName, ProfilerInfo paramProfilerInfo, Bundle paramBundle1, IInstrumentationWatcher paramIInstrumentationWatcher, IUiAutomationConnection paramIUiAutomationConnection, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, Configuration paramConfiguration, CompatibilityInfo paramCompatibilityInfo, Map<String, IBinder> paramMap, Bundle paramBundle2)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeString(paramString);
    paramApplicationInfo.writeToParcel(localParcel, 0);
    localParcel.writeTypedList(paramList);
    if (paramComponentName == null)
    {
      localParcel.writeInt(0);
      if (paramProfilerInfo == null) {
        break label218;
      }
      localParcel.writeInt(1);
      paramProfilerInfo.writeToParcel(localParcel, 1);
      label61:
      localParcel.writeBundle(paramBundle1);
      localParcel.writeStrongInterface(paramIInstrumentationWatcher);
      localParcel.writeStrongInterface(paramIUiAutomationConnection);
      localParcel.writeInt(paramInt);
      if (!paramBoolean1) {
        break label227;
      }
      paramInt = 1;
      label97:
      localParcel.writeInt(paramInt);
      if (!paramBoolean2) {
        break label233;
      }
      paramInt = 1;
      label112:
      localParcel.writeInt(paramInt);
      if (!paramBoolean3) {
        break label239;
      }
      paramInt = 1;
      label127:
      localParcel.writeInt(paramInt);
      if (!paramBoolean4) {
        break label245;
      }
    }
    label218:
    label227:
    label233:
    label239:
    label245:
    for (paramInt = 1;; paramInt = 0)
    {
      localParcel.writeInt(paramInt);
      paramConfiguration.writeToParcel(localParcel, 0);
      paramCompatibilityInfo.writeToParcel(localParcel, 0);
      localParcel.writeMap(paramMap);
      localParcel.writeBundle(paramBundle2);
      this.mRemote.transact(13, localParcel, null, 1);
      localParcel.recycle();
      return;
      localParcel.writeInt(1);
      paramComponentName.writeToParcel(localParcel, 0);
      break;
      localParcel.writeInt(0);
      break label61;
      paramInt = 0;
      break label97;
      paramInt = 0;
      break label112;
      paramInt = 0;
      break label127;
    }
  }
  
  public void clearDnsCache()
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    this.mRemote.transact(38, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void dispatchPackageBroadcast(int paramInt, String[] paramArrayOfString)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeInt(paramInt);
    localParcel.writeStringArray(paramArrayOfString);
    this.mRemote.transact(34, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void dumpActivity(FileDescriptor paramFileDescriptor, IBinder paramIBinder, String paramString, String[] paramArrayOfString)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeFileDescriptor(paramFileDescriptor);
    localParcel.writeStrongBinder(paramIBinder);
    localParcel.writeString(paramString);
    localParcel.writeStringArray(paramArrayOfString);
    this.mRemote.transact(37, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void dumpDbInfo(FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeFileDescriptor(paramFileDescriptor);
    localParcel.writeStringArray(paramArrayOfString);
    this.mRemote.transact(46, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void dumpGfxInfo(FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeFileDescriptor(paramFileDescriptor);
    localParcel.writeStringArray(paramArrayOfString);
    this.mRemote.transact(44, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void dumpHeap(boolean paramBoolean, String paramString, ParcelFileDescriptor paramParcelFileDescriptor)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    int i;
    if (paramBoolean)
    {
      i = 1;
      localParcel.writeInt(i);
      localParcel.writeString(paramString);
      if (paramParcelFileDescriptor == null) {
        break label77;
      }
      localParcel.writeInt(1);
      paramParcelFileDescriptor.writeToParcel(localParcel, 1);
    }
    for (;;)
    {
      this.mRemote.transact(36, localParcel, null, 1);
      localParcel.recycle();
      return;
      i = 0;
      break;
      label77:
      localParcel.writeInt(0);
    }
  }
  
  public void dumpMemInfo(FileDescriptor paramFileDescriptor, Debug.MemoryInfo paramMemoryInfo, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, String[] paramArrayOfString)
    throws RemoteException
  {
    int j = 1;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IApplicationThread");
    localParcel1.writeFileDescriptor(paramFileDescriptor);
    paramMemoryInfo.writeToParcel(localParcel1, 0);
    if (paramBoolean1)
    {
      i = 1;
      localParcel1.writeInt(i);
      if (!paramBoolean2) {
        break label154;
      }
      i = 1;
      label55:
      localParcel1.writeInt(i);
      if (!paramBoolean3) {
        break label160;
      }
      i = 1;
      label70:
      localParcel1.writeInt(i);
      if (!paramBoolean4) {
        break label166;
      }
      i = 1;
      label85:
      localParcel1.writeInt(i);
      if (!paramBoolean5) {
        break label172;
      }
    }
    label154:
    label160:
    label166:
    label172:
    for (int i = j;; i = 0)
    {
      localParcel1.writeInt(i);
      localParcel1.writeStringArray(paramArrayOfString);
      this.mRemote.transact(43, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
      i = 0;
      break;
      i = 0;
      break label55;
      i = 0;
      break label70;
      i = 0;
      break label85;
    }
  }
  
  public void dumpProvider(FileDescriptor paramFileDescriptor, IBinder paramIBinder, String[] paramArrayOfString)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeFileDescriptor(paramFileDescriptor);
    localParcel.writeStrongBinder(paramIBinder);
    localParcel.writeStringArray(paramArrayOfString);
    this.mRemote.transact(45, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void dumpService(FileDescriptor paramFileDescriptor, IBinder paramIBinder, String[] paramArrayOfString)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeFileDescriptor(paramFileDescriptor);
    localParcel.writeStrongBinder(paramIBinder);
    localParcel.writeStringArray(paramArrayOfString);
    this.mRemote.transact(22, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void notifyCleartextNetwork(byte[] paramArrayOfByte)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeByteArray(paramArrayOfByte);
    this.mRemote.transact(56, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void processInBackground()
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    this.mRemote.transact(19, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void profilerControl(boolean paramBoolean, ProfilerInfo paramProfilerInfo, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    int i;
    if (paramBoolean)
    {
      i = 1;
      localParcel.writeInt(i);
      localParcel.writeInt(paramInt);
      if (paramProfilerInfo == null) {
        break label77;
      }
      localParcel.writeInt(1);
      paramProfilerInfo.writeToParcel(localParcel, 1);
    }
    for (;;)
    {
      this.mRemote.transact(28, localParcel, null, 1);
      localParcel.recycle();
      return;
      i = 0;
      break;
      label77:
      localParcel.writeInt(0);
    }
  }
  
  public void requestAssistContextExtras(IBinder paramIBinder1, IBinder paramIBinder2, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder1);
    localParcel.writeStrongBinder(paramIBinder2);
    localParcel.writeInt(paramInt1);
    localParcel.writeInt(paramInt2);
    this.mRemote.transact(48, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public final void scheduleActivityConfigurationChanged(IBinder paramIBinder, Configuration paramConfiguration, boolean paramBoolean)
    throws RemoteException
  {
    int i = 0;
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    if (paramConfiguration != null)
    {
      localParcel.writeInt(1);
      paramConfiguration.writeToParcel(localParcel, 0);
    }
    for (;;)
    {
      if (paramBoolean) {
        i = 1;
      }
      localParcel.writeInt(i);
      this.mRemote.transact(25, localParcel, null, 1);
      localParcel.recycle();
      return;
      localParcel.writeInt(0);
    }
  }
  
  public void scheduleBackgroundVisibleBehindChanged(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel.writeInt(i);
      this.mRemote.transact(54, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public final void scheduleBindService(IBinder paramIBinder, Intent paramIntent, boolean paramBoolean, int paramInt)
    throws RemoteException
  {
    int i = 0;
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    paramIntent.writeToParcel(localParcel, 0);
    if (paramBoolean) {
      i = 1;
    }
    localParcel.writeInt(i);
    localParcel.writeInt(paramInt);
    this.mRemote.transact(20, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void scheduleCancelVisibleBehind(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    this.mRemote.transact(53, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public final void scheduleConfigurationChanged(Configuration paramConfiguration)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    paramConfiguration.writeToParcel(localParcel, 0);
    this.mRemote.transact(16, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void scheduleCrash(String paramString)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeString(paramString);
    this.mRemote.transact(35, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public final void scheduleCreateBackupAgent(ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    paramApplicationInfo.writeToParcel(localParcel, 0);
    paramCompatibilityInfo.writeToParcel(localParcel, 0);
    localParcel.writeInt(paramInt);
    this.mRemote.transact(30, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public final void scheduleCreateService(IBinder paramIBinder, ServiceInfo paramServiceInfo, CompatibilityInfo paramCompatibilityInfo, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    paramServiceInfo.writeToParcel(localParcel, 0);
    paramCompatibilityInfo.writeToParcel(localParcel, 0);
    localParcel.writeInt(paramInt);
    try
    {
      this.mRemote.transact(11, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
    catch (TransactionTooLargeException paramIBinder)
    {
      Log.e("CREATE_SERVICE", "Binder failure starting service; service=" + paramServiceInfo);
      throw paramIBinder;
    }
  }
  
  public final void scheduleDestroyActivity(IBinder paramIBinder, boolean paramBoolean, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel.writeInt(i);
      localParcel.writeInt(paramInt);
      this.mRemote.transact(9, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public final void scheduleDestroyBackupAgent(ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    paramApplicationInfo.writeToParcel(localParcel, 0);
    paramCompatibilityInfo.writeToParcel(localParcel, 0);
    this.mRemote.transact(31, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void scheduleEnterAnimationComplete(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    this.mRemote.transact(55, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public final void scheduleExit()
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    this.mRemote.transact(14, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void scheduleInstallProvider(ProviderInfo paramProviderInfo)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    paramProviderInfo.writeToParcel(localParcel, 0);
    this.mRemote.transact(51, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public final void scheduleLaunchActivity(Intent paramIntent, IBinder paramIBinder, int paramInt1, ActivityInfo paramActivityInfo, Configuration paramConfiguration1, Configuration paramConfiguration2, CompatibilityInfo paramCompatibilityInfo, String paramString, IVoiceInteractor paramIVoiceInteractor, int paramInt2, Bundle paramBundle, PersistableBundle paramPersistableBundle, List<ResultInfo> paramList, List<ReferrerIntent> paramList1, boolean paramBoolean1, boolean paramBoolean2, ProfilerInfo paramProfilerInfo)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    paramIntent.writeToParcel(localParcel, 0);
    localParcel.writeStrongBinder(paramIBinder);
    localParcel.writeInt(paramInt1);
    paramActivityInfo.writeToParcel(localParcel, 0);
    paramConfiguration1.writeToParcel(localParcel, 0);
    if (paramConfiguration2 != null)
    {
      localParcel.writeInt(1);
      paramConfiguration2.writeToParcel(localParcel, 0);
      paramCompatibilityInfo.writeToParcel(localParcel, 0);
      localParcel.writeString(paramString);
      if (paramIVoiceInteractor == null) {
        break label211;
      }
      paramIntent = paramIVoiceInteractor.asBinder();
      label94:
      localParcel.writeStrongBinder(paramIntent);
      localParcel.writeInt(paramInt2);
      localParcel.writeBundle(paramBundle);
      localParcel.writePersistableBundle(paramPersistableBundle);
      localParcel.writeTypedList(paramList);
      localParcel.writeTypedList(paramList1);
      if (!paramBoolean1) {
        break label216;
      }
      paramInt1 = 1;
      label142:
      localParcel.writeInt(paramInt1);
      if (!paramBoolean2) {
        break label221;
      }
      paramInt1 = 1;
      label155:
      localParcel.writeInt(paramInt1);
      if (paramProfilerInfo == null) {
        break label226;
      }
      localParcel.writeInt(1);
      paramProfilerInfo.writeToParcel(localParcel, 1);
    }
    for (;;)
    {
      this.mRemote.transact(7, localParcel, null, 1);
      localParcel.recycle();
      return;
      localParcel.writeInt(0);
      break;
      label211:
      paramIntent = null;
      break label94;
      label216:
      paramInt1 = 0;
      break label142;
      label221:
      paramInt1 = 0;
      break label155;
      label226:
      localParcel.writeInt(0);
    }
  }
  
  public final void scheduleLocalVoiceInteractionStarted(IBinder paramIBinder, IVoiceInteractor paramIVoiceInteractor)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    if (paramIVoiceInteractor != null) {}
    for (paramIBinder = paramIVoiceInteractor.asBinder();; paramIBinder = null)
    {
      localParcel.writeStrongBinder(paramIBinder);
      this.mRemote.transact(61, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public final void scheduleLowMemory()
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    this.mRemote.transact(24, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public final void scheduleMultiWindowModeChanged(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel.writeInt(i);
      this.mRemote.transact(59, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public void scheduleNewIntent(List<ReferrerIntent> paramList, IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeTypedList(paramList);
    localParcel.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel.writeInt(i);
      this.mRemote.transact(8, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public void scheduleOnNewActivityOptions(IBinder paramIBinder, ActivityOptions paramActivityOptions)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    if (paramActivityOptions == null) {}
    for (paramIBinder = null;; paramIBinder = paramActivityOptions.toBundle())
    {
      localParcel.writeBundle(paramIBinder);
      this.mRemote.transact(32, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public final void schedulePauseActivity(IBinder paramIBinder, boolean paramBoolean1, boolean paramBoolean2, int paramInt, boolean paramBoolean3)
    throws RemoteException
  {
    int j = 0;
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    if (paramBoolean1)
    {
      i = 1;
      localParcel.writeInt(i);
      if (!paramBoolean2) {
        break label102;
      }
    }
    label102:
    for (int i = 1;; i = 0)
    {
      localParcel.writeInt(i);
      localParcel.writeInt(paramInt);
      paramInt = j;
      if (paramBoolean3) {
        paramInt = 1;
      }
      localParcel.writeInt(paramInt);
      this.mRemote.transact(1, localParcel, null, 1);
      localParcel.recycle();
      return;
      i = 0;
      break;
    }
  }
  
  public final void schedulePictureInPictureModeChanged(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel.writeInt(i);
      this.mRemote.transact(60, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public void schedulePreload(ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo, Configuration paramConfiguration, Map<String, IBinder> paramMap)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    paramApplicationInfo.writeToParcel(localParcel, 0);
    paramCompatibilityInfo.writeToParcel(localParcel, 0);
    paramConfiguration.writeToParcel(localParcel, 0);
    localParcel.writeMap(paramMap);
    this.mRemote.transact(103, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public final void scheduleReceiver(Intent paramIntent, ActivityInfo paramActivityInfo, CompatibilityInfo paramCompatibilityInfo, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException
  {
    int i = 0;
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    paramIntent.writeToParcel(localParcel, 0);
    paramActivityInfo.writeToParcel(localParcel, 0);
    paramCompatibilityInfo.writeToParcel(localParcel, 0);
    localParcel.writeInt(paramInt1);
    localParcel.writeString(paramString);
    localParcel.writeBundle(paramBundle);
    paramInt1 = i;
    if (paramBoolean) {
      paramInt1 = 1;
    }
    localParcel.writeInt(paramInt1);
    localParcel.writeInt(paramInt2);
    localParcel.writeInt(paramInt3);
    localParcel.writeInt(paramInt4);
    this.mRemote.transact(10, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void scheduleRegisteredReceiver(IIntentReceiver paramIIntentReceiver, Intent paramIntent, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3)
    throws RemoteException
  {
    int i = 0;
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIIntentReceiver.asBinder());
    paramIntent.writeToParcel(localParcel, 0);
    localParcel.writeInt(paramInt1);
    localParcel.writeString(paramString);
    localParcel.writeBundle(paramBundle);
    if (paramBoolean1) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      localParcel.writeInt(paramInt1);
      paramInt1 = i;
      if (paramBoolean2) {
        paramInt1 = 1;
      }
      localParcel.writeInt(paramInt1);
      localParcel.writeInt(paramInt2);
      localParcel.writeInt(paramInt3);
      this.mRemote.transact(23, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public final void scheduleRelaunchActivity(IBinder paramIBinder, List<ResultInfo> paramList, List<ReferrerIntent> paramList1, int paramInt, boolean paramBoolean1, Configuration paramConfiguration1, Configuration paramConfiguration2, boolean paramBoolean2)
    throws RemoteException
  {
    int i = 0;
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    localParcel.writeTypedList(paramList);
    localParcel.writeTypedList(paramList1);
    localParcel.writeInt(paramInt);
    if (paramBoolean1)
    {
      paramInt = 1;
      localParcel.writeInt(paramInt);
      paramConfiguration1.writeToParcel(localParcel, 0);
      if (paramConfiguration2 == null) {
        break label129;
      }
      localParcel.writeInt(1);
      paramConfiguration2.writeToParcel(localParcel, 0);
    }
    for (;;)
    {
      paramInt = i;
      if (paramBoolean2) {
        paramInt = 1;
      }
      localParcel.writeInt(paramInt);
      this.mRemote.transact(26, localParcel, null, 1);
      localParcel.recycle();
      return;
      paramInt = 0;
      break;
      label129:
      localParcel.writeInt(0);
    }
  }
  
  public final void scheduleResumeActivity(IBinder paramIBinder, int paramInt, boolean paramBoolean, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    localParcel.writeInt(paramInt);
    if (paramBoolean) {}
    for (paramInt = 1;; paramInt = 0)
    {
      localParcel.writeInt(paramInt);
      localParcel.writeBundle(paramBundle);
      this.mRemote.transact(5, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public final void scheduleSendResult(IBinder paramIBinder, List<ResultInfo> paramList)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    localParcel.writeTypedList(paramList);
    this.mRemote.transact(6, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public final void scheduleServiceArgs(IBinder paramIBinder, boolean paramBoolean, int paramInt1, int paramInt2, Intent paramIntent)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    int i;
    if (paramBoolean)
    {
      i = 1;
      localParcel.writeInt(i);
      localParcel.writeInt(paramInt1);
      localParcel.writeInt(paramInt2);
      if (paramIntent == null) {
        break label92;
      }
      localParcel.writeInt(1);
      paramIntent.writeToParcel(localParcel, 0);
    }
    for (;;)
    {
      this.mRemote.transact(17, localParcel, null, 1);
      localParcel.recycle();
      return;
      i = 0;
      break;
      label92:
      localParcel.writeInt(0);
    }
  }
  
  public final void scheduleSleeping(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel.writeInt(i);
      this.mRemote.transact(27, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public final void scheduleStopActivity(IBinder paramIBinder, boolean paramBoolean, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel.writeInt(i);
      localParcel.writeInt(paramInt);
      this.mRemote.transact(3, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public final void scheduleStopService(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    this.mRemote.transact(12, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public final void scheduleSuicide()
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    this.mRemote.transact(33, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void scheduleTranslucentConversionComplete(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel.writeInt(i);
      this.mRemote.transact(49, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public void scheduleTrimMemory(int paramInt)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeInt(paramInt);
    this.mRemote.transact(42, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public final void scheduleUnbindService(IBinder paramIBinder, Intent paramIntent)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    paramIntent.writeToParcel(localParcel, 0);
    this.mRemote.transact(21, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public final void scheduleWindowVisibility(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel.writeInt(i);
      this.mRemote.transact(4, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public void setCoreSettings(Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeBundle(paramBundle);
    this.mRemote.transact(40, localParcel, null, 1);
  }
  
  public void setHttpProxy(String paramString1, String paramString2, String paramString3, Uri paramUri)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    localParcel.writeString(paramString3);
    paramUri.writeToParcel(localParcel, 0);
    this.mRemote.transact(39, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void setProcessState(int paramInt)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeInt(paramInt);
    this.mRemote.transact(50, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void setSchedulingGroup(int paramInt)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeInt(paramInt);
    this.mRemote.transact(29, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void startBinderTracking()
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    this.mRemote.transact(57, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void stopBinderTrackingAndDump(FileDescriptor paramFileDescriptor)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeFileDescriptor(paramFileDescriptor);
    this.mRemote.transact(58, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void unstableProviderDied(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeStrongBinder(paramIBinder);
    this.mRemote.transact(47, localParcel, null, 1);
    localParcel.recycle();
  }
  
  public void updatePackageCompatibilityInfo(String paramString, CompatibilityInfo paramCompatibilityInfo)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    localParcel.writeString(paramString);
    paramCompatibilityInfo.writeToParcel(localParcel, 0);
    this.mRemote.transact(41, localParcel, null, 1);
  }
  
  public void updateTimePrefs(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    if (paramBoolean) {}
    for (byte b = 1;; b = 0)
    {
      localParcel.writeByte(b);
      this.mRemote.transact(52, localParcel, null, 1);
      localParcel.recycle();
      return;
    }
  }
  
  public void updateTimeZone()
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IApplicationThread");
    this.mRemote.transact(18, localParcel, null, 1);
    localParcel.recycle();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ApplicationThreadProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */