package android.app;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.IIntentSender.Stub;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.UriPermission;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.os.IBinder;
import android.os.IProgressListener;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.StrictMode.ViolationInfo;
import android.service.voice.IVoiceInteractionSession;
import android.text.TextUtils;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.os.IResultReceiver;
import java.util.ArrayList;
import java.util.List;

class ActivityManagerProxy
  implements IActivityManager
{
  private IBinder mRemote;
  
  public ActivityManagerProxy(IBinder paramIBinder)
  {
    this.mRemote = paramIBinder;
  }
  
  private static void writeIntArray(int[] paramArrayOfInt, Parcel paramParcel)
  {
    if (paramArrayOfInt == null)
    {
      paramParcel.writeInt(0);
      return;
    }
    paramParcel.writeInt(paramArrayOfInt.length);
    paramParcel.writeIntArray(paramArrayOfInt);
  }
  
  public void activityDestroyed(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(62, localParcel1, localParcel2, 1);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void activityIdle(IBinder paramIBinder, Configuration paramConfiguration, boolean paramBoolean)
    throws RemoteException
  {
    int i = 0;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    if (paramConfiguration != null)
    {
      localParcel1.writeInt(1);
      paramConfiguration.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      if (paramBoolean) {
        i = 1;
      }
      localParcel1.writeInt(i);
      this.mRemote.transact(18, localParcel1, localParcel2, 1);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
      localParcel1.writeInt(0);
    }
  }
  
  public void activityPaused(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(19, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void activityRelaunched(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(357, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void activityResumed(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(39, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void activitySlept(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(123, localParcel1, localParcel2, 1);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void activityStopped(IBinder paramIBinder, Bundle paramBundle, PersistableBundle paramPersistableBundle, CharSequence paramCharSequence)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeBundle(paramBundle);
    localParcel1.writePersistableBundle(paramPersistableBundle);
    TextUtils.writeToParcel(paramCharSequence, localParcel1, 0);
    this.mRemote.transact(20, localParcel1, localParcel2, 1);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public int addAppTask(IBinder paramIBinder, Intent paramIntent, ActivityManager.TaskDescription paramTaskDescription, Bitmap paramBitmap)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    paramIntent.writeToParcel(localParcel1, 0);
    paramTaskDescription.writeToParcel(localParcel1, 0);
    paramBitmap.writeToParcel(localParcel1, 0);
    this.mRemote.transact(234, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return i;
  }
  
  public void addPackageDependency(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(95, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void appNotRespondingViaProvider(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(183, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public IBinder asBinder()
  {
    return this.mRemote;
  }
  
  public void attachApplication(IApplicationThread paramIApplicationThread)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIApplicationThread.asBinder());
    this.mRemote.transact(17, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void backgroundResourcesReleased(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(228, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void backupAgentCreated(String paramString, IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(91, localParcel1, localParcel2, 0);
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public boolean bindBackupAgent(String paramString, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(90, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return bool;
    }
  }
  
  public int bindService(IApplicationThread paramIApplicationThread, IBinder paramIBinder, Intent paramIntent, String paramString1, IServiceConnection paramIServiceConnection, int paramInt1, String paramString2, int paramInt2)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null) {
      localIBinder = paramIApplicationThread.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    localParcel1.writeStrongBinder(paramIBinder);
    paramIntent.writeToParcel(localParcel1, 0);
    localParcel1.writeString(paramString1);
    localParcel1.writeStrongBinder(paramIServiceConnection.asBinder());
    localParcel1.writeInt(paramInt1);
    localParcel1.writeString(paramString2);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(36, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt1 = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramInt1;
  }
  
  public void bootAnimationComplete()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(238, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public int broadcastIntent(IApplicationThread paramIApplicationThread, Intent paramIntent, String paramString1, IIntentReceiver paramIIntentReceiver, int paramInt1, String paramString2, Bundle paramBundle1, String[] paramArrayOfString, int paramInt2, Bundle paramBundle2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null)
    {
      paramIApplicationThread = paramIApplicationThread.asBinder();
      localParcel1.writeStrongBinder(paramIApplicationThread);
      paramIntent.writeToParcel(localParcel1, 0);
      localParcel1.writeString(paramString1);
      if (paramIIntentReceiver == null) {
        break label192;
      }
      paramIApplicationThread = paramIIntentReceiver.asBinder();
      label60:
      localParcel1.writeStrongBinder(paramIApplicationThread);
      localParcel1.writeInt(paramInt1);
      localParcel1.writeString(paramString2);
      localParcel1.writeBundle(paramBundle1);
      localParcel1.writeStringArray(paramArrayOfString);
      localParcel1.writeInt(paramInt2);
      localParcel1.writeBundle(paramBundle2);
      if (!paramBoolean1) {
        break label197;
      }
      paramInt1 = 1;
      label116:
      localParcel1.writeInt(paramInt1);
      if (!paramBoolean2) {
        break label203;
      }
    }
    label192:
    label197:
    label203:
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      localParcel1.writeInt(paramInt1);
      localParcel1.writeInt(paramInt3);
      this.mRemote.transact(14, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt1 = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt1;
      paramIApplicationThread = null;
      break;
      paramIApplicationThread = null;
      break label60;
      paramInt1 = 0;
      break label116;
    }
  }
  
  public boolean canBypassWorkChallenge(PendingIntent paramPendingIntent)
    throws RemoteException
  {
    boolean bool = false;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramPendingIntent.writeToParcel(localParcel1, 0);
    this.mRemote.transact(381, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    if (i != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void cancelIntentSender(IIntentSender paramIIntentSender)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIIntentSender.asBinder());
    this.mRemote.transact(64, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public int checkGrantUriPermission(int paramInt1, String paramString, Uri paramUri, int paramInt2, int paramInt3)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeString(paramString);
    paramUri.writeToParcel(localParcel1, 0);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeInt(paramInt3);
    this.mRemote.transact(119, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt1 = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramInt1;
  }
  
  public int checkPermission(String paramString, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(53, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt1 = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramInt1;
  }
  
  public int checkPermissionWithToken(String paramString, int paramInt1, int paramInt2, IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(242, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt1 = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramInt1;
  }
  
  public int checkUriPermission(Uri paramUri, int paramInt1, int paramInt2, int paramInt3, int paramInt4, IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramUri.writeToParcel(localParcel1, 0);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeInt(paramInt3);
    localParcel1.writeInt(paramInt4);
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(54, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt1 = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramInt1;
  }
  
  public boolean clearApplicationUserData(String paramString, IPackageDataObserver paramIPackageDataObserver, int paramInt)
    throws RemoteException
  {
    Object localObject = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    paramString = (String)localObject;
    if (paramIPackageDataObserver != null) {
      paramString = paramIPackageDataObserver.asBinder();
    }
    localParcel1.writeStrongBinder(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(78, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public void clearGrantedUriPermissions(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(362, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void clearPendingBackup()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(160, localParcel1, localParcel2, 0);
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public void closeSystemDialogs(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(97, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean convertFromTranslucent(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(174, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean convertToTranslucent(IBinder paramIBinder, ActivityOptions paramActivityOptions)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    if (paramActivityOptions == null)
    {
      localParcel1.writeInt(0);
      this.mRemote.transact(175, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() == 0) {
        break label96;
      }
    }
    label96:
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
      localParcel1.writeInt(1);
      localParcel1.writeBundle(paramActivityOptions.toBundle());
      break;
    }
  }
  
  public void crashApplication(int paramInt1, int paramInt2, String paramString1, String paramString2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeString(paramString1);
    localParcel1.writeString(paramString2);
    this.mRemote.transact(114, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public IActivityContainer createStackOnDisplay(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(282, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() == 1) {}
    for (IActivityContainer localIActivityContainer = IActivityContainer.Stub.asInterface(localParcel2.readStrongBinder());; localIActivityContainer = null)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return localIActivityContainer;
    }
  }
  
  public IActivityContainer createVirtualActivityContainer(IBinder paramIBinder, IActivityContainerCallback paramIActivityContainerCallback)
    throws RemoteException
  {
    Object localObject = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    if (paramIActivityContainerCallback == null)
    {
      paramIBinder = (IBinder)localObject;
      localParcel1.writeStrongBinder(paramIBinder);
      this.mRemote.transact(168, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() != 1) {
        break label100;
      }
    }
    label100:
    for (paramIBinder = IActivityContainer.Stub.asInterface(localParcel2.readStrongBinder());; paramIBinder = null)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return paramIBinder;
      paramIBinder = paramIActivityContainerCallback.asBinder();
      break;
    }
  }
  
  public void deleteActivityContainer(IActivityContainer paramIActivityContainer)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIActivityContainer.asBinder());
    this.mRemote.transact(186, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean dumpHeap(String paramString1, int paramInt, boolean paramBoolean, String paramString2, ParcelFileDescriptor paramParcelFileDescriptor)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString1);
    localParcel1.writeInt(paramInt);
    if (paramBoolean)
    {
      paramInt = 1;
      localParcel1.writeInt(paramInt);
      localParcel1.writeString(paramString2);
      if (paramParcelFileDescriptor == null) {
        break label116;
      }
      localParcel1.writeInt(1);
      paramParcelFileDescriptor.writeToParcel(localParcel1, 1);
      label67:
      this.mRemote.transact(120, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() == 0) {
        break label125;
      }
    }
    label116:
    label125:
    for (paramBoolean = true;; paramBoolean = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return paramBoolean;
      paramInt = 0;
      break;
      localParcel1.writeInt(0);
      break label67;
    }
  }
  
  public void dumpHeapFinished(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(289, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void enterPictureInPictureMode(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(356, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void enterSafeMode()
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(66, localParcel, null, 0);
    localParcel.recycle();
  }
  
  public void exitFreeformMode(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(345, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean finishActivity(IBinder paramIBinder, int paramInt1, Intent paramIntent, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeInt(paramInt1);
    if (paramIntent != null)
    {
      localParcel1.writeInt(1);
      paramIntent.writeToParcel(localParcel1, 0);
      localParcel1.writeInt(paramInt2);
      this.mRemote.transact(11, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() == 0) {
        break label108;
      }
    }
    label108:
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
      localParcel1.writeInt(0);
      break;
    }
  }
  
  public boolean finishActivityAffinity(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(149, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public void finishHeavyWeightApp()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(109, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void finishInstrumentation(IApplicationThread paramIApplicationThread, int paramInt, Bundle paramBundle)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null) {
      localIBinder = paramIApplicationThread.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    localParcel1.writeInt(paramInt);
    localParcel1.writeBundle(paramBundle);
    this.mRemote.transact(45, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void finishNotOrderReceiver(IBinder paramIBinder, int paramInt1, int paramInt2, String paramString, Bundle paramBundle, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeString(paramString);
    localParcel1.writeBundle(paramBundle);
    if (paramBoolean) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      localParcel1.writeInt(paramInt1);
      this.mRemote.transact(391, localParcel1, localParcel2, 1);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void finishReceiver(IBinder paramIBinder, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeString(paramString);
    localParcel1.writeBundle(paramBundle);
    if (paramBoolean) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      localParcel1.writeInt(paramInt1);
      localParcel1.writeInt(paramInt2);
      this.mRemote.transact(16, localParcel1, localParcel2, 1);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void finishSubActivity(IBinder paramIBinder, String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(32, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void finishVoiceTask(IVoiceInteractionSession paramIVoiceInteractionSession)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIVoiceInteractionSession.asBinder());
    this.mRemote.transact(224, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void forceStopPackage(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(79, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public ComponentName getActivityClassForToken(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(49, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramIBinder = ComponentName.readFromParcel(localParcel2);
    localParcel1.recycle();
    localParcel2.recycle();
    return paramIBinder;
  }
  
  public int getActivityDisplayId(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(185, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return i;
  }
  
  public ActivityOptions getActivityOptions(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(220, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramIBinder = ActivityOptions.fromBundle(localParcel2.readBundle());
    localParcel1.recycle();
    localParcel2.recycle();
    return paramIBinder;
  }
  
  public int getActivityStackId(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(344, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return i;
  }
  
  public List<ActivityManager.AppBootMode> getAllAppBootModes(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(321, localParcel1, localParcel2, 0);
    localParcel2.readException();
    ArrayList localArrayList = localParcel2.createTypedArrayList(ActivityManager.AppBootMode.CREATOR);
    localParcel1.recycle();
    localParcel2.recycle();
    return localArrayList;
  }
  
  public List<ActivityManager.AppControlMode> getAllAppControlModes(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(702, localParcel1, localParcel2, 0);
    localParcel2.readException();
    ArrayList localArrayList = localParcel2.createTypedArrayList(ActivityManager.AppControlMode.CREATOR);
    localParcel1.recycle();
    localParcel2.recycle();
    return localArrayList;
  }
  
  public List<ActivityManager.StackInfo> getAllStackInfos()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(171, localParcel1, localParcel2, 0);
    localParcel2.readException();
    ArrayList localArrayList = localParcel2.createTypedArrayList(ActivityManager.StackInfo.CREATOR);
    localParcel1.recycle();
    localParcel2.recycle();
    return localArrayList;
  }
  
  public int getAppBootMode(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(322, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel2.recycle();
    localParcel1.recycle();
    return i;
  }
  
  public boolean getAppBootState()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(324, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() == 1) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public int getAppControlMode(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(703, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt = localParcel2.readInt();
    localParcel2.recycle();
    localParcel1.recycle();
    return paramInt;
  }
  
  public int getAppControlState(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(705, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt = localParcel2.readInt();
    localParcel2.recycle();
    localParcel1.recycle();
    return paramInt;
  }
  
  public int getAppStartMode(int paramInt, String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    localParcel1.writeString(paramString);
    this.mRemote.transact(351, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramInt;
  }
  
  public Point getAppTaskThumbnailSize()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(235, localParcel1, localParcel2, 0);
    localParcel2.readException();
    Point localPoint = (Point)Point.CREATOR.createFromParcel(localParcel2);
    localParcel1.recycle();
    localParcel2.recycle();
    return localPoint;
  }
  
  public List<IAppTask> getAppTasks(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(221, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramString = null;
    int i = localParcel2.readInt();
    if (i >= 0)
    {
      ArrayList localArrayList = new ArrayList();
      for (;;)
      {
        paramString = localArrayList;
        if (i <= 0) {
          break;
        }
        localArrayList.add(IAppTask.Stub.asInterface(localParcel2.readStrongBinder()));
        i -= 1;
      }
    }
    localParcel1.recycle();
    localParcel2.recycle();
    return paramString;
  }
  
  public Bundle getAssistContextExtras(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(162, localParcel1, localParcel2, 0);
    localParcel2.readException();
    Bundle localBundle = localParcel2.readBundle();
    localParcel1.recycle();
    localParcel2.recycle();
    return localBundle;
  }
  
  public boolean getBgMonitorMode()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(334, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel2.recycle();
    localParcel1.recycle();
    return i == 1;
  }
  
  public List<ActivityManager.HighPowerApp> getBgPowerHungryList()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(331, localParcel1, localParcel2, 0);
    localParcel2.readException();
    ArrayList localArrayList = localParcel2.createTypedArrayList(ActivityManager.HighPowerApp.CREATOR);
    localParcel2.recycle();
    localParcel1.recycle();
    return localArrayList;
  }
  
  public String[] getCalleePackageArray(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(327, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramString = localParcel2.readStringArray();
    localParcel2.recycle();
    localParcel1.recycle();
    return paramString;
  }
  
  public String[] getCallerPackageArray(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(326, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramString = localParcel2.readStringArray();
    localParcel2.recycle();
    localParcel1.recycle();
    return paramString;
  }
  
  public ComponentName getCallingActivity(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(22, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramIBinder = ComponentName.readFromParcel(localParcel2);
    localParcel1.recycle();
    localParcel2.recycle();
    return paramIBinder;
  }
  
  public String getCallingPackage(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(21, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramIBinder = localParcel2.readString();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramIBinder;
  }
  
  public Configuration getConfiguration()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(46, localParcel1, localParcel2, 0);
    localParcel2.readException();
    Configuration localConfiguration = (Configuration)Configuration.CREATOR.createFromParcel(localParcel2);
    localParcel2.recycle();
    localParcel1.recycle();
    return localConfiguration;
  }
  
  public IActivityManager.ContentProviderHolder getContentProvider(IApplicationThread paramIApplicationThread, String paramString, int paramInt, boolean paramBoolean)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null) {
      localIBinder = paramIApplicationThread.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    if (paramBoolean) {}
    for (paramInt = 1;; paramInt = 0)
    {
      localParcel1.writeInt(paramInt);
      this.mRemote.transact(29, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt = localParcel2.readInt();
      paramIApplicationThread = null;
      if (paramInt != 0) {
        paramIApplicationThread = (IActivityManager.ContentProviderHolder)IActivityManager.ContentProviderHolder.CREATOR.createFromParcel(localParcel2);
      }
      localParcel1.recycle();
      localParcel2.recycle();
      return paramIApplicationThread;
    }
  }
  
  public IActivityManager.ContentProviderHolder getContentProviderExternal(String paramString, int paramInt, IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(141, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt = localParcel2.readInt();
    paramString = null;
    if (paramInt != 0) {
      paramString = (IActivityManager.ContentProviderHolder)IActivityManager.ContentProviderHolder.CREATOR.createFromParcel(localParcel2);
    }
    localParcel1.recycle();
    localParcel2.recycle();
    return paramString;
  }
  
  public UserInfo getCurrentUser()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(145, localParcel1, localParcel2, 0);
    localParcel2.readException();
    UserInfo localUserInfo = (UserInfo)UserInfo.CREATOR.createFromParcel(localParcel2);
    localParcel2.recycle();
    localParcel1.recycle();
    return localUserInfo;
  }
  
  public ConfigurationInfo getDeviceConfigurationInfo()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(84, localParcel1, localParcel2, 0);
    localParcel2.readException();
    ConfigurationInfo localConfigurationInfo = (ConfigurationInfo)ConfigurationInfo.CREATOR.createFromParcel(localParcel2);
    localParcel2.recycle();
    localParcel1.recycle();
    return localConfigurationInfo;
  }
  
  public int getFocusedStackId()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(283, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return i;
  }
  
  public int getFrontActivityScreenCompatMode()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(124, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel2.recycle();
    localParcel1.recycle();
    return i;
  }
  
  public ParceledListSlice<UriPermission> getGrantedUriPermissions(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(361, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramString = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
    localParcel1.recycle();
    localParcel2.recycle();
    return paramString;
  }
  
  public Intent getIntentForIntentSender(IIntentSender paramIIntentSender)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIIntentSender.asBinder());
    this.mRemote.transact(161, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (paramIIntentSender = (Intent)Intent.CREATOR.createFromParcel(localParcel2);; paramIIntentSender = null)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return paramIIntentSender;
    }
  }
  
  public IIntentSender getIntentSender(int paramInt1, String paramString1, IBinder paramIBinder, String paramString2, int paramInt2, Intent[] paramArrayOfIntent, String[] paramArrayOfString, int paramInt3, Bundle paramBundle, int paramInt4)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeString(paramString1);
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeString(paramString2);
    localParcel1.writeInt(paramInt2);
    if (paramArrayOfIntent != null)
    {
      localParcel1.writeInt(1);
      localParcel1.writeTypedArray(paramArrayOfIntent, 0);
      localParcel1.writeStringArray(paramArrayOfString);
      localParcel1.writeInt(paramInt3);
      if (paramBundle == null) {
        break label160;
      }
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      localParcel1.writeInt(paramInt4);
      this.mRemote.transact(63, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramString1 = IIntentSender.Stub.asInterface(localParcel2.readStrongBinder());
      localParcel1.recycle();
      localParcel2.recycle();
      return paramString1;
      localParcel1.writeInt(0);
      break;
      label160:
      localParcel1.writeInt(0);
    }
  }
  
  public String getLaunchedFromPackage(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(164, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramIBinder = localParcel2.readString();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramIBinder;
  }
  
  public int getLaunchedFromUid(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(150, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return i;
  }
  
  public int getLockTaskModeState()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(287, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return i;
  }
  
  public void getMemoryInfo(ActivityManager.MemoryInfo paramMemoryInfo)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(76, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramMemoryInfo.readFromParcel(localParcel2);
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public int getMemoryTrimLevel()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(370, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return i;
  }
  
  public void getMyMemoryState(ActivityManager.RunningAppProcessInfo paramRunningAppProcessInfo)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(143, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramRunningAppProcessInfo.readFromParcel(localParcel2);
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public boolean getPackageAskScreenCompat(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(128, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return bool;
    }
  }
  
  public String getPackageForIntentSender(IIntentSender paramIIntentSender)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIIntentSender.asBinder());
    this.mRemote.transact(65, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramIIntentSender = localParcel2.readString();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramIIntentSender;
  }
  
  public String getPackageForToken(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(50, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramIBinder = localParcel2.readString();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramIBinder;
  }
  
  public int getPackageProcessState(String paramString1, String paramString2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString1);
    localParcel1.writeString(paramString2);
    this.mRemote.transact(294, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return i;
  }
  
  public int getPackageScreenCompatMode(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(126, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel2.recycle();
    localParcel1.recycle();
    return i;
  }
  
  public IBinder getPermissionServiceBinderProxy(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(303, localParcel1, localParcel2, 0);
    localParcel2.readException();
    IBinder localIBinder = localParcel2.readStrongBinder();
    localParcel1.recycle();
    localParcel2.recycle();
    return localIBinder;
  }
  
  public ParceledListSlice<UriPermission> getPersistedUriPermissions(String paramString, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(182, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramString = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
      localParcel1.recycle();
      localParcel2.recycle();
      return paramString;
    }
  }
  
  public int getProcessLimit()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(52, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return i;
  }
  
  public Debug.MemoryInfo[] getProcessMemoryInfo(int[] paramArrayOfInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeIntArray(paramArrayOfInt);
    this.mRemote.transact(98, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramArrayOfInt = (Debug.MemoryInfo[])localParcel2.createTypedArray(Debug.MemoryInfo.CREATOR);
    localParcel1.recycle();
    localParcel2.recycle();
    return paramArrayOfInt;
  }
  
  public long[] getProcessPss(int[] paramArrayOfInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeIntArray(paramArrayOfInt);
    this.mRemote.transact(137, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramArrayOfInt = localParcel2.createLongArray();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramArrayOfInt;
  }
  
  public List<ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(77, localParcel1, localParcel2, 0);
    localParcel2.readException();
    ArrayList localArrayList = localParcel2.createTypedArrayList(ActivityManager.ProcessErrorStateInfo.CREATOR);
    localParcel1.recycle();
    localParcel2.recycle();
    return localArrayList;
  }
  
  public String getProviderMimeType(Uri paramUri, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramUri.writeToParcel(localParcel1, 0);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(115, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramUri = localParcel2.readString();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramUri;
  }
  
  public ParceledListSlice<ActivityManager.RecentTaskInfo> getRecentTasks(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeInt(paramInt3);
    this.mRemote.transact(60, localParcel1, localParcel2, 0);
    localParcel2.readException();
    ParceledListSlice localParceledListSlice = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
    localParcel1.recycle();
    localParcel2.recycle();
    return localParceledListSlice;
  }
  
  public int getRequestedOrientation(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(71, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return i;
  }
  
  public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(83, localParcel1, localParcel2, 0);
    localParcel2.readException();
    ArrayList localArrayList = localParcel2.createTypedArrayList(ActivityManager.RunningAppProcessInfo.CREATOR);
    localParcel1.recycle();
    localParcel2.recycle();
    return localArrayList;
  }
  
  public List<ApplicationInfo> getRunningExternalApplications()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(108, localParcel1, localParcel2, 0);
    localParcel2.readException();
    ArrayList localArrayList = localParcel2.createTypedArrayList(ApplicationInfo.CREATOR);
    localParcel1.recycle();
    localParcel2.recycle();
    return localArrayList;
  }
  
  public PendingIntent getRunningServiceControlPanel(ComponentName paramComponentName)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramComponentName.writeToParcel(localParcel1, 0);
    this.mRemote.transact(33, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramComponentName = PendingIntent.readPendingIntentOrNullFromParcel(localParcel2);
    localParcel1.recycle();
    localParcel2.recycle();
    return paramComponentName;
  }
  
  public int[] getRunningUserIds()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(157, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int[] arrayOfInt = localParcel2.createIntArray();
    localParcel2.recycle();
    localParcel1.recycle();
    return arrayOfInt;
  }
  
  public List<ActivityManager.RunningServiceInfo> getServices(int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(81, localParcel1, localParcel2, 0);
    localParcel2.readException();
    Object localObject = null;
    paramInt1 = localParcel2.readInt();
    if (paramInt1 >= 0)
    {
      ArrayList localArrayList = new ArrayList();
      for (;;)
      {
        localObject = localArrayList;
        if (paramInt1 <= 0) {
          break;
        }
        localArrayList.add((ActivityManager.RunningServiceInfo)ActivityManager.RunningServiceInfo.CREATOR.createFromParcel(localParcel2));
        paramInt1 -= 1;
      }
    }
    localParcel1.recycle();
    localParcel2.recycle();
    return (List<ActivityManager.RunningServiceInfo>)localObject;
  }
  
  public ActivityManager.StackInfo getStackInfo(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(173, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt = localParcel2.readInt();
    ActivityManager.StackInfo localStackInfo = null;
    if (paramInt != 0) {
      localStackInfo = (ActivityManager.StackInfo)ActivityManager.StackInfo.CREATOR.createFromParcel(localParcel2);
    }
    localParcel1.recycle();
    localParcel2.recycle();
    return localStackInfo;
  }
  
  public String getTagForIntentSender(IIntentSender paramIIntentSender, String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIIntentSender.asBinder());
    localParcel1.writeString(paramString);
    this.mRemote.transact(211, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramIIntentSender = localParcel2.readString();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramIIntentSender;
  }
  
  public Rect getTaskBounds(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(184, localParcel1, localParcel2, 0);
    localParcel2.readException();
    Rect localRect = (Rect)Rect.CREATOR.createFromParcel(localParcel2);
    localParcel1.recycle();
    localParcel2.recycle();
    return localRect;
  }
  
  public Bitmap getTaskDescriptionIcon(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(239, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() == 0) {}
    for (paramString = null;; paramString = (Bitmap)Bitmap.CREATOR.createFromParcel(localParcel2))
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return paramString;
    }
  }
  
  public int getTaskForActivity(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(27, localParcel1, localParcel2, 0);
      localParcel2.readException();
      i = localParcel2.readInt();
      localParcel1.recycle();
      localParcel2.recycle();
      return i;
    }
  }
  
  public ActivityManager.TaskThumbnail getTaskThumbnail(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(82, localParcel1, localParcel2, 0);
    localParcel2.readException();
    ActivityManager.TaskThumbnail localTaskThumbnail = null;
    if (localParcel2.readInt() != 0) {
      localTaskThumbnail = (ActivityManager.TaskThumbnail)ActivityManager.TaskThumbnail.CREATOR.createFromParcel(localParcel2);
    }
    localParcel1.recycle();
    localParcel2.recycle();
    return localTaskThumbnail;
  }
  
  public List<ActivityManager.RunningTaskInfo> getTasks(int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(23, localParcel1, localParcel2, 0);
    localParcel2.readException();
    Object localObject = null;
    paramInt1 = localParcel2.readInt();
    if (paramInt1 >= 0)
    {
      ArrayList localArrayList = new ArrayList();
      for (;;)
      {
        localObject = localArrayList;
        if (paramInt1 <= 0) {
          break;
        }
        localArrayList.add((ActivityManager.RunningTaskInfo)ActivityManager.RunningTaskInfo.CREATOR.createFromParcel(localParcel2));
        paramInt1 -= 1;
      }
    }
    localParcel1.recycle();
    localParcel2.recycle();
    return (List<ActivityManager.RunningTaskInfo>)localObject;
  }
  
  public int getUidForIntentSender(IIntentSender paramIIntentSender)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIIntentSender.asBinder());
    this.mRemote.transact(93, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return i;
  }
  
  public IBinder getUriPermissionOwnerForActivity(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(358, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramIBinder = localParcel2.readStrongBinder();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramIBinder;
  }
  
  public void grantUriPermission(IApplicationThread paramIApplicationThread, String paramString, Uri paramUri, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIApplicationThread.asBinder());
    localParcel1.writeString(paramString);
    paramUri.writeToParcel(localParcel1, 0);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(55, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void grantUriPermissionFromOwner(IBinder paramIBinder, int paramInt1, String paramString, Uri paramUri, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeString(paramString);
    paramUri.writeToParcel(localParcel1, 0);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeInt(paramInt3);
    localParcel1.writeInt(paramInt4);
    this.mRemote.transact(55, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void handleApplicationCrash(IBinder paramIBinder, ApplicationErrorReport.CrashInfo paramCrashInfo)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    paramCrashInfo.writeToParcel(localParcel1, 0);
    this.mRemote.transact(2, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public void handleApplicationStrictModeViolation(IBinder paramIBinder, int paramInt, StrictMode.ViolationInfo paramViolationInfo)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeInt(paramInt);
    paramViolationInfo.writeToParcel(localParcel1, 0);
    this.mRemote.transact(110, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public boolean handleApplicationWtf(IBinder paramIBinder, String paramString, boolean paramBoolean, ApplicationErrorReport.CrashInfo paramCrashInfo)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeString(paramString);
    int i;
    if (paramBoolean)
    {
      i = 1;
      localParcel1.writeInt(i);
      paramCrashInfo.writeToParcel(localParcel1, 0);
      this.mRemote.transact(102, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() == 0) {
        break label101;
      }
    }
    label101:
    for (paramBoolean = true;; paramBoolean = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return paramBoolean;
      i = 0;
      break;
    }
  }
  
  public int handleIncomingUser(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, String paramString1, String paramString2)
    throws RemoteException
  {
    int i = 1;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeInt(paramInt3);
    if (paramBoolean1)
    {
      paramInt1 = 1;
      localParcel1.writeInt(paramInt1);
      if (!paramBoolean2) {
        break label124;
      }
    }
    label124:
    for (paramInt1 = i;; paramInt1 = 0)
    {
      localParcel1.writeInt(paramInt1);
      localParcel1.writeString(paramString1);
      localParcel1.writeString(paramString2);
      this.mRemote.transact(94, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt1 = localParcel2.readInt();
      localParcel1.recycle();
      localParcel2.recycle();
      return paramInt1;
      paramInt1 = 0;
      break;
    }
  }
  
  public void hang(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(167, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public long inputDispatchingTimedOut(int paramInt, boolean paramBoolean, String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    if (paramBoolean) {}
    for (paramInt = 1;; paramInt = 0)
    {
      localParcel1.writeInt(paramInt);
      localParcel1.writeString(paramString);
      this.mRemote.transact(159, localParcel1, localParcel2, 0);
      localParcel2.readException();
      long l = localParcel2.readInt();
      localParcel1.recycle();
      localParcel2.recycle();
      return l;
    }
  }
  
  public boolean isAppForeground(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(363, localParcel1, localParcel2, 0);
    if (localParcel2.readInt() == 1) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isAppLocked(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(669, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return bool;
    }
  }
  
  public boolean isAssistDataAllowedOnCurrentActivity()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(300, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isBackgroundVisibleBehind(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(227, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() > 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isImmersive(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(111, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() == 1) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isInHomeStack(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(213, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() > 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isInLockTaskMode()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(217, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() == 1) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isInMultiWindowMode(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(353, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() == 1) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isInPictureInPictureMode(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(354, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() == 1) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isIntentSenderAnActivity(IIntentSender paramIIntentSender)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIIntentSender.asBinder());
    this.mRemote.transact(152, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isIntentSenderTargetedToPackage(IIntentSender paramIIntentSender)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIIntentSender.asBinder());
    this.mRemote.transact(135, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isKeyguardDone()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(670, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public void isRequestPermission(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(307, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public boolean isRootVoiceInteraction(IBinder paramIBinder)
    throws RemoteException
  {
    boolean bool = false;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(302, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    if (i != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isTopActivityImmersive()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(113, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() == 1) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isTopOfTask(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(225, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() == 1) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isUserAMonkey()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(104, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean isUserRunning(int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(122, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return bool;
    }
  }
  
  public boolean isVrModePackageEnabled(ComponentName paramComponentName)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramComponentName.writeToParcel(localParcel1, 0);
    this.mRemote.transact(372, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    return i == 1;
  }
  
  public void keyguardGoingAway(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(297, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void keyguardWaitingForActivityDrawn()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(232, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void killAllBackgroundProcesses()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(140, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void killApplication(String paramString1, int paramInt1, int paramInt2, String paramString2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString1);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeString(paramString2);
    this.mRemote.transact(96, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void killApplicationProcess(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(99, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void killBackgroundProcesses(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(103, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void killPackageDependents(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(355, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean killPids(int[] paramArrayOfInt, String paramString, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeIntArray(paramArrayOfInt);
    localParcel1.writeString(paramString);
    int i;
    if (paramBoolean)
    {
      i = 1;
      localParcel1.writeInt(i);
      this.mRemote.transact(80, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() == 0) {
        break label93;
      }
    }
    label93:
    for (paramBoolean = true;; paramBoolean = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return paramBoolean;
      i = 0;
      break;
    }
  }
  
  public boolean killProcessesBelowForeground(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(144, localParcel1, localParcel2, 0);
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public void killUid(int paramInt1, int paramInt2, String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeString(paramString);
    this.mRemote.transact(165, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean launchAssistIntent(Intent paramIntent, int paramInt1, String paramString, int paramInt2, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramIntent.writeToParcel(localParcel1, 0);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeBundle(paramBundle);
    this.mRemote.transact(240, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean moveActivityTaskToBack(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    int i;
    if (paramBoolean)
    {
      i = 1;
      localParcel1.writeInt(i);
      this.mRemote.transact(75, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() == 0) {
        break label84;
      }
    }
    label84:
    for (paramBoolean = true;; paramBoolean = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return paramBoolean;
      i = 0;
      break;
    }
  }
  
  public void moveTaskBackwards(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(26, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean moveTaskToDockedStack(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, Rect paramRect, boolean paramBoolean3)
    throws RemoteException
  {
    int i = 1;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    if (paramBoolean1)
    {
      paramInt1 = 1;
      localParcel1.writeInt(paramInt1);
      if (!paramBoolean2) {
        break label140;
      }
      paramInt1 = 1;
      label51:
      localParcel1.writeInt(paramInt1);
      if (paramRect == null) {
        break label145;
      }
      localParcel1.writeInt(1);
      paramRect.writeToParcel(localParcel1, 0);
      label76:
      if (!paramBoolean3) {
        break label154;
      }
      paramInt1 = i;
      label84:
      localParcel1.writeInt(paramInt1);
      this.mRemote.transact(347, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() <= 0) {
        break label159;
      }
    }
    label140:
    label145:
    label154:
    label159:
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return paramBoolean1;
      paramInt1 = 0;
      break;
      paramInt1 = 0;
      break label51;
      localParcel1.writeInt(0);
      break label76;
      paramInt1 = 0;
      break label84;
    }
  }
  
  public void moveTaskToFront(int paramInt1, int paramInt2, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    if (paramBundle != null)
    {
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      this.mRemote.transact(24, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
      localParcel1.writeInt(0);
    }
  }
  
  public void moveTaskToStack(int paramInt1, int paramInt2, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    if (paramBoolean) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      localParcel1.writeInt(paramInt1);
      this.mRemote.transact(169, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void moveTasksToFullscreenStack(int paramInt, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    if (paramBoolean) {}
    for (paramInt = 1;; paramInt = 0)
    {
      localParcel1.writeInt(paramInt);
      this.mRemote.transact(349, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public boolean moveTopActivityToPinnedStack(int paramInt, Rect paramRect)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    paramRect.writeToParcel(localParcel1, 0);
    this.mRemote.transact(350, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean navigateUpTo(IBinder paramIBinder, Intent paramIntent1, int paramInt, Intent paramIntent2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    paramIntent1.writeToParcel(localParcel1, 0);
    localParcel1.writeInt(paramInt);
    if (paramIntent2 != null)
    {
      localParcel1.writeInt(1);
      paramIntent2.writeToParcel(localParcel1, 0);
      this.mRemote.transact(147, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() == 0) {
        break label111;
      }
    }
    label111:
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
      localParcel1.writeInt(0);
      break;
    }
  }
  
  public IBinder newUriPermissionOwner(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(116, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramString = localParcel2.readStrongBinder();
    localParcel1.recycle();
    localParcel2.recycle();
    return paramString;
  }
  
  public void noteAlarmFinish(IIntentSender paramIIntentSender, int paramInt, String paramString)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IActivityManager");
    localParcel.writeStrongBinder(paramIIntentSender.asBinder());
    localParcel.writeInt(paramInt);
    localParcel.writeString(paramString);
    this.mRemote.transact(293, localParcel, null, 0);
    localParcel.recycle();
  }
  
  public void noteAlarmStart(IIntentSender paramIIntentSender, int paramInt, String paramString)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IActivityManager");
    localParcel.writeStrongBinder(paramIIntentSender.asBinder());
    localParcel.writeInt(paramInt);
    localParcel.writeString(paramString);
    this.mRemote.transact(292, localParcel, null, 0);
    localParcel.recycle();
  }
  
  public void noteWakeupAlarm(IIntentSender paramIIntentSender, int paramInt, String paramString1, String paramString2)
    throws RemoteException
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.app.IActivityManager");
    localParcel.writeStrongBinder(paramIIntentSender.asBinder());
    localParcel.writeInt(paramInt);
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    this.mRemote.transact(68, localParcel, null, 0);
    localParcel.recycle();
  }
  
  public void notifyActivityDrawn(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(176, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void notifyCleartextNetwork(int paramInt, byte[] paramArrayOfByte)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    localParcel1.writeByteArray(paramArrayOfByte);
    this.mRemote.transact(281, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void notifyEnterAnimationComplete(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(231, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void notifyLaunchTaskBehindComplete(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(229, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void notifyLockedProfile(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(374, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void notifyPinnedStackAnimationEnded()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(367, localParcel1, localParcel2, 0);
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public ParcelFileDescriptor openContentUri(Uri paramUri)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(5, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramUri = null;
    if (localParcel2.readInt() != 0) {
      paramUri = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(localParcel2);
    }
    localParcel1.recycle();
    localParcel2.recycle();
    return paramUri;
  }
  
  public void overridePendingTransition(IBinder paramIBinder, String paramString, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(101, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public IBinder peekService(Intent paramIntent, String paramString1, String paramString2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramIntent.writeToParcel(localParcel1, 0);
    localParcel1.writeString(paramString1);
    localParcel1.writeString(paramString2);
    this.mRemote.transact(85, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramIntent = localParcel2.readStrongBinder();
    localParcel2.recycle();
    localParcel1.recycle();
    return paramIntent;
  }
  
  public void performIdleMaintenance()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(179, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void positionTaskInStack(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeInt(paramInt3);
    this.mRemote.transact(343, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean profileControl(String paramString, int paramInt1, boolean paramBoolean, ProfilerInfo paramProfilerInfo, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt1);
    if (paramBoolean)
    {
      paramInt1 = 1;
      localParcel1.writeInt(paramInt1);
      localParcel1.writeInt(paramInt2);
      if (paramProfilerInfo == null) {
        break label116;
      }
      localParcel1.writeInt(1);
      paramProfilerInfo.writeToParcel(localParcel1, 1);
      label67:
      this.mRemote.transact(86, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() == 0) {
        break label125;
      }
    }
    label116:
    label125:
    for (paramBoolean = true;; paramBoolean = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return paramBoolean;
      paramInt1 = 0;
      break;
      localParcel1.writeInt(0);
      break label67;
    }
  }
  
  public void publishContentProviders(IApplicationThread paramIApplicationThread, List<IActivityManager.ContentProviderHolder> paramList)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null) {
      localIBinder = paramIApplicationThread.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    localParcel1.writeTypedList(paramList);
    this.mRemote.transact(30, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void publishService(IBinder paramIBinder1, Intent paramIntent, IBinder paramIBinder2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder1);
    paramIntent.writeToParcel(localParcel1, 0);
    localParcel1.writeStrongBinder(paramIBinder2);
    this.mRemote.transact(38, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean refContentProvider(IBinder paramIBinder, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(31, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public void registerProcessObserver(IProcessObserver paramIProcessObserver)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIProcessObserver != null) {
      localIBinder = paramIProcessObserver.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    this.mRemote.transact(133, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public Intent registerReceiver(IApplicationThread paramIApplicationThread, String paramString1, IIntentReceiver paramIIntentReceiver, IntentFilter paramIntentFilter, String paramString2, int paramInt)
    throws RemoteException
  {
    Object localObject = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null) {}
    for (paramIApplicationThread = paramIApplicationThread.asBinder();; paramIApplicationThread = null)
    {
      localParcel1.writeStrongBinder(paramIApplicationThread);
      localParcel1.writeString(paramString1);
      paramIApplicationThread = (IApplicationThread)localObject;
      if (paramIIntentReceiver != null) {
        paramIApplicationThread = paramIIntentReceiver.asBinder();
      }
      localParcel1.writeStrongBinder(paramIApplicationThread);
      paramIntentFilter.writeToParcel(localParcel1, 0);
      localParcel1.writeString(paramString2);
      localParcel1.writeInt(paramInt);
      this.mRemote.transact(12, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramIApplicationThread = null;
      if (localParcel2.readInt() != 0) {
        paramIApplicationThread = (Intent)Intent.CREATOR.createFromParcel(localParcel2);
      }
      localParcel2.recycle();
      localParcel1.recycle();
      return paramIApplicationThread;
    }
  }
  
  public void registerTaskStackListener(ITaskStackListener paramITaskStackListener)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramITaskStackListener.asBinder());
    this.mRemote.transact(243, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void registerUidObserver(IUidObserver paramIUidObserver, int paramInt)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIUidObserver != null) {
      localIBinder = paramIUidObserver.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(298, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void registerUserSwitchObserver(IUserSwitchObserver paramIUserSwitchObserver, String paramString)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIUserSwitchObserver != null) {
      localIBinder = paramIUserSwitchObserver.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    localParcel1.writeString(paramString);
    this.mRemote.transact(155, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean releaseActivityInstance(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(236, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public void releasePersistableUriPermission(Uri paramUri, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramUri.writeToParcel(localParcel1, 0);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(181, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void releaseSomeActivities(IApplicationThread paramIApplicationThread)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIApplicationThread.asBinder());
    this.mRemote.transact(237, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void removeContentProvider(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(69, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void removeContentProviderExternal(String paramString, IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(142, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void removeStack(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(368, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean removeTask(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(132, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return bool;
    }
  }
  
  public void reportActivityFullyDrawn(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(177, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void reportAssistContextExtras(IBinder paramIBinder, Bundle paramBundle, AssistStructure paramAssistStructure, AssistContent paramAssistContent, Uri paramUri)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeBundle(paramBundle);
    paramAssistStructure.writeToParcel(localParcel1, 0);
    paramAssistContent.writeToParcel(localParcel1, 0);
    if (paramUri != null)
    {
      localParcel1.writeInt(1);
      paramUri.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      this.mRemote.transact(163, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
      localParcel1.writeInt(0);
    }
  }
  
  public void reportSizeConfigurations(IBinder paramIBinder, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    writeIntArray(paramArrayOfInt1, localParcel1);
    writeIntArray(paramArrayOfInt2, localParcel1);
    writeIntArray(paramArrayOfInt3, localParcel1);
    this.mRemote.transact(346, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean requestAssistContextExtras(int paramInt, IResultReceiver paramIResultReceiver, Bundle paramBundle, IBinder paramIBinder, boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException
  {
    int i = 1;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    localParcel1.writeStrongBinder(paramIResultReceiver.asBinder());
    localParcel1.writeBundle(paramBundle);
    localParcel1.writeStrongBinder(paramIBinder);
    if (paramBoolean1)
    {
      paramInt = 1;
      localParcel1.writeInt(paramInt);
      if (!paramBoolean2) {
        break label129;
      }
      paramInt = i;
      label71:
      localParcel1.writeInt(paramInt);
      this.mRemote.transact(285, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() == 0) {
        break label134;
      }
    }
    label129:
    label134:
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return paramBoolean1;
      paramInt = 0;
      break;
      paramInt = 0;
      break label71;
    }
  }
  
  public void requestBugReport(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(158, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean requestVisibleBehind(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    int i;
    if (paramBoolean)
    {
      i = 1;
      localParcel1.writeInt(i);
      this.mRemote.transact(226, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() <= 0) {
        break label85;
      }
    }
    label85:
    for (paramBoolean = true;; paramBoolean = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return paramBoolean;
      i = 0;
      break;
    }
  }
  
  public void resizeDockedStack(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramRect1 != null)
    {
      localParcel1.writeInt(1);
      paramRect1.writeToParcel(localParcel1, 0);
      if (paramRect2 == null) {
        break label149;
      }
      localParcel1.writeInt(1);
      paramRect2.writeToParcel(localParcel1, 0);
      label51:
      if (paramRect3 == null) {
        break label158;
      }
      localParcel1.writeInt(1);
      paramRect3.writeToParcel(localParcel1, 0);
      label68:
      if (paramRect4 == null) {
        break label167;
      }
      localParcel1.writeInt(1);
      paramRect4.writeToParcel(localParcel1, 0);
      label87:
      if (paramRect5 == null) {
        break label176;
      }
      localParcel1.writeInt(1);
      paramRect5.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      this.mRemote.transact(359, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
      localParcel1.writeInt(0);
      break;
      label149:
      localParcel1.writeInt(0);
      break label51;
      label158:
      localParcel1.writeInt(0);
      break label68;
      label167:
      localParcel1.writeInt(0);
      break label87;
      label176:
      localParcel1.writeInt(0);
    }
  }
  
  public void resizePinnedStack(Rect paramRect1, Rect paramRect2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramRect1 != null)
    {
      localParcel1.writeInt(1);
      paramRect1.writeToParcel(localParcel1, 0);
      if (paramRect2 == null) {
        break label85;
      }
      localParcel1.writeInt(1);
      paramRect2.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      this.mRemote.transact(371, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
      localParcel1.writeInt(0);
      break;
      label85:
      localParcel1.writeInt(0);
    }
  }
  
  public void resizeStack(int paramInt1, Rect paramRect, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt2)
    throws RemoteException
  {
    int i = 1;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    if (paramRect != null)
    {
      localParcel1.writeInt(1);
      paramRect.writeToParcel(localParcel1, 0);
      if (!paramBoolean1) {
        break label132;
      }
      paramInt1 = 1;
      label49:
      localParcel1.writeInt(paramInt1);
      if (!paramBoolean2) {
        break label137;
      }
      paramInt1 = 1;
      label62:
      localParcel1.writeInt(paramInt1);
      if (!paramBoolean3) {
        break label142;
      }
    }
    label132:
    label137:
    label142:
    for (paramInt1 = i;; paramInt1 = 0)
    {
      localParcel1.writeInt(paramInt1);
      localParcel1.writeInt(paramInt2);
      this.mRemote.transact(170, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
      localParcel1.writeInt(0);
      break;
      paramInt1 = 0;
      break label49;
      paramInt1 = 0;
      break label62;
    }
  }
  
  public void resizeTask(int paramInt1, Rect paramRect, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    paramRect.writeToParcel(localParcel1, 0);
    this.mRemote.transact(286, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void restart()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(178, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void resumeAppSwitches()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(89, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public void revokeUriPermission(IApplicationThread paramIApplicationThread, Uri paramUri, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIApplicationThread.asBinder());
    paramUri.writeToParcel(localParcel1, 0);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(56, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void revokeUriPermissionFromOwner(IBinder paramIBinder, Uri paramUri, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    if (paramUri != null)
    {
      localParcel1.writeInt(1);
      paramUri.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      localParcel1.writeInt(paramInt1);
      localParcel1.writeInt(paramInt2);
      this.mRemote.transact(56, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
      localParcel1.writeInt(0);
    }
  }
  
  public void sendIdleJobTrigger()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(376, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public int sendIntentSender(IIntentSender paramIIntentSender, int paramInt, Intent paramIntent, String paramString1, IIntentReceiver paramIIntentReceiver, String paramString2, Bundle paramBundle)
    throws RemoteException
  {
    Object localObject = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIIntentSender.asBinder());
    localParcel1.writeInt(paramInt);
    if (paramIntent != null)
    {
      localParcel1.writeInt(1);
      paramIntent.writeToParcel(localParcel1, 0);
      localParcel1.writeString(paramString1);
      paramIIntentSender = (IIntentSender)localObject;
      if (paramIIntentReceiver != null) {
        paramIIntentSender = paramIIntentReceiver.asBinder();
      }
      localParcel1.writeStrongBinder(paramIIntentSender);
      localParcel1.writeString(paramString2);
      if (paramBundle == null) {
        break label159;
      }
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      this.mRemote.transact(377, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt = localParcel2.readInt();
      localParcel1.recycle();
      localParcel2.recycle();
      return paramInt;
      localParcel1.writeInt(0);
      break;
      label159:
      localParcel1.writeInt(0);
    }
  }
  
  public void serviceDoneExecuting(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    localParcel1.writeInt(paramInt3);
    this.mRemote.transact(61, localParcel1, localParcel2, 1);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void setActivityController(IActivityController paramIActivityController, boolean paramBoolean)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIActivityController != null) {
      localIBinder = paramIActivityController.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(57, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void setAlwaysFinish(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(43, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public int setAppBootMode(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(323, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt = localParcel2.readInt();
    localParcel2.recycle();
    localParcel1.recycle();
    return paramInt;
  }
  
  public void setAppBootState(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(325, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public int setAppControlMode(String paramString, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(704, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt1 = localParcel2.readInt();
    localParcel2.recycle();
    localParcel1.recycle();
    return paramInt1;
  }
  
  public int setAppControlState(int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(706, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt1 = localParcel2.readInt();
    localParcel2.recycle();
    localParcel1.recycle();
    return paramInt1;
  }
  
  public void setBgMonitorMode(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(332, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel2.recycle();
      localParcel1.recycle();
      return;
    }
  }
  
  public void setDebugApp(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException
  {
    int j = 1;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    if (paramBoolean1)
    {
      i = 1;
      localParcel1.writeInt(i);
      if (!paramBoolean2) {
        break label94;
      }
    }
    label94:
    for (int i = j;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(42, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
      i = 0;
      break;
    }
  }
  
  public void setDumpHeapDebugLimit(String paramString1, int paramInt, long paramLong, String paramString2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString1);
    localParcel1.writeInt(paramInt);
    localParcel1.writeLong(paramLong);
    localParcel1.writeString(paramString2);
    this.mRemote.transact(288, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void setFocusedStack(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(172, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void setFocusedTask(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(131, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void setFrontActivityScreenCompatMode(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(125, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public void setHasTopUi(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(380, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void setIgnoredAnrProcess(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(305, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void setImmersive(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(112, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void setKeyguardDone(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(668, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void setLenientBackgroundCheck(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(369, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void setLockScreenShown(boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException
  {
    int j = 1;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramBoolean1)
    {
      i = 1;
      localParcel1.writeInt(i);
      if (!paramBoolean2) {
        break label84;
      }
    }
    label84:
    for (int i = j;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(148, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
      i = 0;
      break;
    }
  }
  
  public void setPackageAskScreenCompat(String paramString, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(129, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel2.recycle();
      localParcel1.recycle();
      return;
    }
  }
  
  public void setPackageScreenCompatMode(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(127, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public void setPermissionServiceBinderProxy(IBinder paramIBinder, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(304, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void setProcessForeground(IBinder paramIBinder, int paramInt, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeInt(paramInt);
    if (paramBoolean) {}
    for (paramInt = 1;; paramInt = 0)
    {
      localParcel1.writeInt(paramInt);
      this.mRemote.transact(73, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void setProcessLimit(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(51, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean setProcessMemoryTrimLevel(String paramString, int paramInt1, int paramInt2)
    throws RemoteException
  {
    boolean bool = false;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(187, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt1 = localParcel2.readInt();
    localParcel1.recycle();
    localParcel2.recycle();
    if (paramInt1 != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void setRenderThread(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(379, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void setRequestedOrientation(IBinder paramIBinder, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(70, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void setServiceForeground(ComponentName paramComponentName, IBinder paramIBinder, int paramInt1, Notification paramNotification, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    ComponentName.writeToParcel(paramComponentName, localParcel1);
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeInt(paramInt1);
    if (paramNotification != null)
    {
      localParcel1.writeInt(1);
      paramNotification.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      localParcel1.writeInt(paramInt2);
      this.mRemote.transact(74, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
      localParcel1.writeInt(0);
    }
  }
  
  public void setTaskDescription(IBinder paramIBinder, ActivityManager.TaskDescription paramTaskDescription)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    paramTaskDescription.writeToParcel(localParcel1, 0);
    this.mRemote.transact(218, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void setTaskResizeable(int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(284, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void setUserIsMonkey(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(166, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void setVoiceKeepAwake(IVoiceInteractionSession paramIVoiceInteractionSession, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIVoiceInteractionSession.asBinder());
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(290, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public int setVrMode(IBinder paramIBinder, boolean paramBoolean, ComponentName paramComponentName)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      paramComponentName.writeToParcel(localParcel1, 0);
      this.mRemote.transact(360, localParcel1, localParcel2, 0);
      localParcel2.readException();
      i = localParcel2.readInt();
      localParcel1.recycle();
      localParcel2.recycle();
      return i;
    }
  }
  
  public void setVrThread(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(378, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean shouldUpRecreateTask(IBinder paramIBinder, String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeString(paramString);
    this.mRemote.transact(146, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public boolean showAssistFromActivity(IBinder paramIBinder, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeBundle(paramBundle);
    this.mRemote.transact(301, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public void showBootMessage(CharSequence paramCharSequence, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    TextUtils.writeToParcel(paramCharSequence, localParcel1, 0);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(138, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void showLockTaskEscapeMessage(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(295, localParcel1, localParcel2, 1);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void showWaitingForDebugger(IApplicationThread paramIApplicationThread, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIApplicationThread.asBinder());
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(58, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public boolean shutdown(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(87, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return bool;
    }
  }
  
  public void signalPersistentProcesses(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(59, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public int startActivities(IApplicationThread paramIApplicationThread, String paramString, Intent[] paramArrayOfIntent, String[] paramArrayOfString, IBinder paramIBinder, Bundle paramBundle, int paramInt)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null) {
      localIBinder = paramIApplicationThread.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    localParcel1.writeString(paramString);
    localParcel1.writeTypedArray(paramArrayOfIntent, 0);
    localParcel1.writeStringArray(paramArrayOfString);
    localParcel1.writeStrongBinder(paramIBinder);
    if (paramBundle != null)
    {
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      localParcel1.writeInt(paramInt);
      this.mRemote.transact(121, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt;
      localParcel1.writeInt(0);
    }
  }
  
  public int startActivity(IApplicationThread paramIApplicationThread, String paramString1, Intent paramIntent, String paramString2, IBinder paramIBinder, String paramString3, int paramInt1, int paramInt2, ProfilerInfo paramProfilerInfo, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null)
    {
      paramIApplicationThread = paramIApplicationThread.asBinder();
      localParcel1.writeStrongBinder(paramIApplicationThread);
      localParcel1.writeString(paramString1);
      paramIntent.writeToParcel(localParcel1, 0);
      localParcel1.writeString(paramString2);
      localParcel1.writeStrongBinder(paramIBinder);
      localParcel1.writeString(paramString3);
      localParcel1.writeInt(paramInt1);
      localParcel1.writeInt(paramInt2);
      if (paramProfilerInfo == null) {
        break label166;
      }
      localParcel1.writeInt(1);
      paramProfilerInfo.writeToParcel(localParcel1, 1);
      label101:
      if (paramBundle == null) {
        break label175;
      }
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      this.mRemote.transact(3, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt1 = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt1;
      paramIApplicationThread = null;
      break;
      label166:
      localParcel1.writeInt(0);
      break label101;
      label175:
      localParcel1.writeInt(0);
    }
  }
  
  public IActivityManager.WaitResult startActivityAndWait(IApplicationThread paramIApplicationThread, String paramString1, Intent paramIntent, String paramString2, IBinder paramIBinder, String paramString3, int paramInt1, int paramInt2, ProfilerInfo paramProfilerInfo, Bundle paramBundle, int paramInt3)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null)
    {
      paramIApplicationThread = paramIApplicationThread.asBinder();
      localParcel1.writeStrongBinder(paramIApplicationThread);
      localParcel1.writeString(paramString1);
      paramIntent.writeToParcel(localParcel1, 0);
      localParcel1.writeString(paramString2);
      localParcel1.writeStrongBinder(paramIBinder);
      localParcel1.writeString(paramString3);
      localParcel1.writeInt(paramInt1);
      localParcel1.writeInt(paramInt2);
      if (paramProfilerInfo == null) {
        break label180;
      }
      localParcel1.writeInt(1);
      paramProfilerInfo.writeToParcel(localParcel1, 1);
      label101:
      if (paramBundle == null) {
        break label189;
      }
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      localParcel1.writeInt(paramInt3);
      this.mRemote.transact(105, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramIApplicationThread = (IActivityManager.WaitResult)IActivityManager.WaitResult.CREATOR.createFromParcel(localParcel2);
      localParcel2.recycle();
      localParcel1.recycle();
      return paramIApplicationThread;
      paramIApplicationThread = null;
      break;
      label180:
      localParcel1.writeInt(0);
      break label101;
      label189:
      localParcel1.writeInt(0);
    }
  }
  
  public int startActivityAsCaller(IApplicationThread paramIApplicationThread, String paramString1, Intent paramIntent, String paramString2, IBinder paramIBinder, String paramString3, int paramInt1, int paramInt2, ProfilerInfo paramProfilerInfo, Bundle paramBundle, boolean paramBoolean, int paramInt3)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null)
    {
      paramIApplicationThread = paramIApplicationThread.asBinder();
      localParcel1.writeStrongBinder(paramIApplicationThread);
      localParcel1.writeString(paramString1);
      paramIntent.writeToParcel(localParcel1, 0);
      localParcel1.writeString(paramString2);
      localParcel1.writeStrongBinder(paramIBinder);
      localParcel1.writeString(paramString3);
      localParcel1.writeInt(paramInt1);
      localParcel1.writeInt(paramInt2);
      if (paramProfilerInfo == null) {
        break label190;
      }
      localParcel1.writeInt(1);
      paramProfilerInfo.writeToParcel(localParcel1, 1);
      label101:
      if (paramBundle == null) {
        break label199;
      }
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
      label120:
      if (!paramBoolean) {
        break label208;
      }
    }
    label190:
    label199:
    label208:
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      localParcel1.writeInt(paramInt1);
      localParcel1.writeInt(paramInt3);
      this.mRemote.transact(233, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt1 = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt1;
      paramIApplicationThread = null;
      break;
      localParcel1.writeInt(0);
      break label101;
      localParcel1.writeInt(0);
      break label120;
    }
  }
  
  public int startActivityAsUser(IApplicationThread paramIApplicationThread, String paramString1, Intent paramIntent, String paramString2, IBinder paramIBinder, String paramString3, int paramInt1, int paramInt2, ProfilerInfo paramProfilerInfo, Bundle paramBundle, int paramInt3)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null)
    {
      paramIApplicationThread = paramIApplicationThread.asBinder();
      localParcel1.writeStrongBinder(paramIApplicationThread);
      localParcel1.writeString(paramString1);
      paramIntent.writeToParcel(localParcel1, 0);
      localParcel1.writeString(paramString2);
      localParcel1.writeStrongBinder(paramIBinder);
      localParcel1.writeString(paramString3);
      localParcel1.writeInt(paramInt1);
      localParcel1.writeInt(paramInt2);
      if (paramProfilerInfo == null) {
        break label175;
      }
      localParcel1.writeInt(1);
      paramProfilerInfo.writeToParcel(localParcel1, 1);
      label101:
      if (paramBundle == null) {
        break label184;
      }
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      localParcel1.writeInt(paramInt3);
      this.mRemote.transact(153, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt1 = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt1;
      paramIApplicationThread = null;
      break;
      label175:
      localParcel1.writeInt(0);
      break label101;
      label184:
      localParcel1.writeInt(0);
    }
  }
  
  public int startActivityFromRecents(int paramInt, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    if (paramBundle == null) {
      localParcel1.writeInt(0);
    }
    for (;;)
    {
      this.mRemote.transact(230, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt;
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
    }
  }
  
  public int startActivityIntentSender(IApplicationThread paramIApplicationThread, IntentSender paramIntentSender, Intent paramIntent, String paramString1, IBinder paramIBinder, String paramString2, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null)
    {
      paramIApplicationThread = paramIApplicationThread.asBinder();
      localParcel1.writeStrongBinder(paramIApplicationThread);
      paramIntentSender.writeToParcel(localParcel1, 0);
      if (paramIntent == null) {
        break label166;
      }
      localParcel1.writeInt(1);
      paramIntent.writeToParcel(localParcel1, 0);
      label58:
      localParcel1.writeString(paramString1);
      localParcel1.writeStrongBinder(paramIBinder);
      localParcel1.writeString(paramString2);
      localParcel1.writeInt(paramInt1);
      localParcel1.writeInt(paramInt2);
      localParcel1.writeInt(paramInt3);
      if (paramBundle == null) {
        break label175;
      }
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      this.mRemote.transact(100, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt1 = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt1;
      paramIApplicationThread = null;
      break;
      label166:
      localParcel1.writeInt(0);
      break label58;
      label175:
      localParcel1.writeInt(0);
    }
  }
  
  public int startActivityWithConfig(IApplicationThread paramIApplicationThread, String paramString1, Intent paramIntent, String paramString2, IBinder paramIBinder, String paramString3, int paramInt1, int paramInt2, Configuration paramConfiguration, Bundle paramBundle, int paramInt3)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null)
    {
      paramIApplicationThread = paramIApplicationThread.asBinder();
      localParcel1.writeStrongBinder(paramIApplicationThread);
      localParcel1.writeString(paramString1);
      paramIntent.writeToParcel(localParcel1, 0);
      localParcel1.writeString(paramString2);
      localParcel1.writeStrongBinder(paramIBinder);
      localParcel1.writeString(paramString3);
      localParcel1.writeInt(paramInt1);
      localParcel1.writeInt(paramInt2);
      paramConfiguration.writeToParcel(localParcel1, 0);
      if (paramBundle == null) {
        break label162;
      }
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      localParcel1.writeInt(paramInt3);
      this.mRemote.transact(3, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt1 = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt1;
      paramIApplicationThread = null;
      break;
      label162:
      localParcel1.writeInt(0);
    }
  }
  
  public boolean startBinderTracking()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(341, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return bool;
    }
  }
  
  public void startConfirmDeviceCredentialIntent(Intent paramIntent)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramIntent.writeToParcel(localParcel1, 0);
    this.mRemote.transact(375, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void startInPlaceAnimationOnFrontMostApplication(ActivityOptions paramActivityOptions)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramActivityOptions == null) {
      localParcel1.writeInt(0);
    }
    for (;;)
    {
      this.mRemote.transact(241, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
      localParcel1.writeInt(1);
      localParcel1.writeBundle(paramActivityOptions.toBundle());
    }
  }
  
  public boolean startInstrumentation(ComponentName paramComponentName, String paramString1, int paramInt1, Bundle paramBundle, IInstrumentationWatcher paramIInstrumentationWatcher, IUiAutomationConnection paramIUiAutomationConnection, int paramInt2, String paramString2)
    throws RemoteException
  {
    Object localObject = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    ComponentName.writeToParcel(paramComponentName, localParcel1);
    localParcel1.writeString(paramString1);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeBundle(paramBundle);
    if (paramIInstrumentationWatcher != null)
    {
      paramComponentName = paramIInstrumentationWatcher.asBinder();
      localParcel1.writeStrongBinder(paramComponentName);
      paramComponentName = (ComponentName)localObject;
      if (paramIUiAutomationConnection != null) {
        paramComponentName = paramIUiAutomationConnection.asBinder();
      }
      localParcel1.writeStrongBinder(paramComponentName);
      localParcel1.writeInt(paramInt2);
      localParcel1.writeString(paramString2);
      this.mRemote.transact(44, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() == 0) {
        break label151;
      }
    }
    label151:
    for (boolean bool = true;; bool = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return bool;
      paramComponentName = null;
      break;
    }
  }
  
  public void startLocalVoiceInteraction(IBinder paramIBinder, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeBundle(paramBundle);
    this.mRemote.transact(364, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public void startLockTaskMode(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(214, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void startLockTaskMode(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(215, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean startNextMatchingActivity(IBinder paramIBinder, Intent paramIntent, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    paramIntent.writeToParcel(localParcel1, 0);
    if (paramBundle != null)
    {
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      this.mRemote.transact(67, localParcel1, localParcel2, 0);
      localParcel2.readException();
      int i = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      if (i == 0) {
        break;
      }
      return true;
      localParcel1.writeInt(0);
    }
    return false;
  }
  
  public ComponentName startService(IApplicationThread paramIApplicationThread, Intent paramIntent, String paramString1, String paramString2, int paramInt)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null) {
      localIBinder = paramIApplicationThread.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    paramIntent.writeToParcel(localParcel1, 0);
    localParcel1.writeString(paramString1);
    localParcel1.writeString(paramString2);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(34, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramIApplicationThread = ComponentName.readFromParcel(localParcel2);
    localParcel1.recycle();
    localParcel2.recycle();
    return paramIApplicationThread;
  }
  
  public void startSystemLockTaskMode(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(222, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean startUserInBackground(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(212, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return bool;
    }
  }
  
  public int startVoiceActivity(String paramString1, int paramInt1, int paramInt2, Intent paramIntent, String paramString2, IVoiceInteractionSession paramIVoiceInteractionSession, IVoiceInteractor paramIVoiceInteractor, int paramInt3, ProfilerInfo paramProfilerInfo, Bundle paramBundle, int paramInt4)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString1);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    paramIntent.writeToParcel(localParcel1, 0);
    localParcel1.writeString(paramString2);
    localParcel1.writeStrongBinder(paramIVoiceInteractionSession.asBinder());
    localParcel1.writeStrongBinder(paramIVoiceInteractor.asBinder());
    localParcel1.writeInt(paramInt3);
    if (paramProfilerInfo != null)
    {
      localParcel1.writeInt(1);
      paramProfilerInfo.writeToParcel(localParcel1, 1);
      if (paramBundle == null) {
        break label176;
      }
      localParcel1.writeInt(1);
      paramBundle.writeToParcel(localParcel1, 0);
    }
    for (;;)
    {
      localParcel1.writeInt(paramInt4);
      this.mRemote.transact(219, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt1 = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt1;
      localParcel1.writeInt(0);
      break;
      label176:
      localParcel1.writeInt(0);
    }
  }
  
  public void stopAppSwitches()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(88, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public void stopBgPowerHungryApp(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(333, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public boolean stopBinderTrackingAndDump(ParcelFileDescriptor paramParcelFileDescriptor)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramParcelFileDescriptor != null)
    {
      localParcel1.writeInt(1);
      paramParcelFileDescriptor.writeToParcel(localParcel1, 1);
      this.mRemote.transact(342, localParcel1, localParcel2, 0);
      localParcel2.readException();
      if (localParcel2.readInt() == 0) {
        break label81;
      }
    }
    label81:
    for (boolean bool = true;; bool = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return bool;
      localParcel1.writeInt(0);
      break;
    }
  }
  
  public void stopLocalVoiceInteraction(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(365, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public void stopLockTaskMode()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(216, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public int stopService(IApplicationThread paramIApplicationThread, Intent paramIntent, String paramString, int paramInt)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null) {
      localIBinder = paramIApplicationThread.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    paramIntent.writeToParcel(localParcel1, 0);
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(35, localParcel1, localParcel2, 0);
    localParcel2.readException();
    paramInt = localParcel2.readInt();
    localParcel2.recycle();
    localParcel1.recycle();
    return paramInt;
  }
  
  public boolean stopServiceToken(ComponentName paramComponentName, IBinder paramIBinder, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    ComponentName.writeToParcel(paramComponentName, localParcel1);
    localParcel1.writeStrongBinder(paramIBinder);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(48, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public void stopSystemLockTaskMode()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(223, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public int stopUser(int paramInt, boolean paramBoolean, IStopUserCallback paramIStopUserCallback)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    if (paramBoolean) {}
    for (paramInt = 1;; paramInt = 0)
    {
      localParcel1.writeInt(paramInt);
      localParcel1.writeStrongInterface(paramIStopUserCallback);
      this.mRemote.transact(154, localParcel1, localParcel2, 0);
      localParcel2.readException();
      paramInt = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt;
    }
  }
  
  public boolean supportsLocalVoiceInteraction()
    throws RemoteException
  {
    boolean bool = false;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(366, localParcel1, localParcel2, 0);
    localParcel2.readException();
    int i = localParcel2.readInt();
    localParcel2.recycle();
    localParcel1.recycle();
    if (i != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void suppressResizeConfigChanges(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(348, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public void swapDockedAndFullscreenStack()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(373, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean switchUser(int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(130, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return bool;
    }
  }
  
  public void takePersistableUriPermission(Uri paramUri, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramUri.writeToParcel(localParcel1, 0);
    localParcel1.writeInt(paramInt1);
    localParcel1.writeInt(paramInt2);
    this.mRemote.transact(180, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean testIsSystemReady()
  {
    return true;
  }
  
  public void unbindBackupAgent(ApplicationInfo paramApplicationInfo)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramApplicationInfo.writeToParcel(localParcel1, 0);
    this.mRemote.transact(92, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public void unbindFinished(IBinder paramIBinder, Intent paramIntent, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    paramIntent.writeToParcel(localParcel1, 0);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(72, localParcel1, localParcel2, 0);
      localParcel2.readException();
      localParcel1.recycle();
      localParcel2.recycle();
      return;
    }
  }
  
  public boolean unbindService(IServiceConnection paramIServiceConnection)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIServiceConnection.asBinder());
    this.mRemote.transact(37, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
  
  public void unbroadcastIntent(IApplicationThread paramIApplicationThread, Intent paramIntent, int paramInt)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIApplicationThread != null) {
      localIBinder = paramIApplicationThread.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    paramIntent.writeToParcel(localParcel1, 0);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(15, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void unhandledBack()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    this.mRemote.transact(4, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean unlockUser(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, IProgressListener paramIProgressListener)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    localParcel1.writeByteArray(paramArrayOfByte1);
    localParcel1.writeByteArray(paramArrayOfByte2);
    localParcel1.writeStrongInterface(paramIProgressListener);
    this.mRemote.transact(352, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel2.recycle();
      localParcel1.recycle();
      return bool;
    }
  }
  
  public void unregisterProcessObserver(IProcessObserver paramIProcessObserver)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIProcessObserver != null) {
      localIBinder = paramIProcessObserver.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    this.mRemote.transact(134, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void unregisterReceiver(IIntentReceiver paramIIntentReceiver)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIIntentReceiver.asBinder());
    this.mRemote.transact(13, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void unregisterUidObserver(IUidObserver paramIUidObserver)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIUidObserver != null) {
      localIBinder = paramIUidObserver.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    this.mRemote.transact(299, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void unregisterUserSwitchObserver(IUserSwitchObserver paramIUserSwitchObserver)
    throws RemoteException
  {
    IBinder localIBinder = null;
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    if (paramIUserSwitchObserver != null) {
      localIBinder = paramIUserSwitchObserver.asBinder();
    }
    localParcel1.writeStrongBinder(localIBinder);
    this.mRemote.transact(156, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void unstableProviderDied(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(151, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void updateAccesibilityServiceFlag(String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    localParcel1.writeInt(paramInt);
    this.mRemote.transact(328, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel2.recycle();
    localParcel1.recycle();
  }
  
  public void updateConfiguration(Configuration paramConfiguration)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramConfiguration.writeToParcel(localParcel1, 0);
    this.mRemote.transact(47, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void updateDeviceOwner(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(296, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void updateLockTaskPackages(int paramInt, String[] paramArrayOfString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeInt(paramInt);
    localParcel1.writeStringArray(paramArrayOfString);
    this.mRemote.transact(291, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public void updatePersistentConfiguration(Configuration paramConfiguration)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    paramConfiguration.writeToParcel(localParcel1, 0);
    this.mRemote.transact(136, localParcel1, localParcel2, 0);
    localParcel2.readException();
    localParcel1.recycle();
    localParcel2.recycle();
  }
  
  public boolean willActivityBeVisible(IBinder paramIBinder)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.app.IActivityManager");
    localParcel1.writeStrongBinder(paramIBinder);
    this.mRemote.transact(106, localParcel1, localParcel2, 0);
    localParcel2.readException();
    if (localParcel2.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localParcel1.recycle();
      localParcel2.recycle();
      return bool;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ActivityManagerProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */