package com.android.server.voiceinteraction;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManagerInternal;
import android.content.pm.PackageManagerInternal.PackagesProvider;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.SoundTrigger.KeyphraseSoundModel;
import android.hardware.soundtrigger.SoundTrigger.ModuleProperties;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.service.voice.IVoiceInteractionService;
import android.service.voice.IVoiceInteractionSession;
import android.service.voice.VoiceInteractionManagerInternal;
import android.service.voice.VoiceInteractionServiceInfo;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.app.IVoiceInteractionManagerService.Stub;
import com.android.internal.app.IVoiceInteractionSessionListener;
import com.android.internal.app.IVoiceInteractionSessionShowCallback.Stub;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.UiThread;
import com.android.server.soundtrigger.SoundTriggerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.TreeSet;

public class VoiceInteractionManagerService
  extends SystemService
{
  static final boolean DEBUG = false;
  static final String TAG = "VoiceInteractionManagerService";
  final ActivityManagerInternal mAmInternal;
  final Context mContext;
  final DatabaseHelper mDbHelper;
  final TreeSet<Integer> mLoadedKeyphraseIds;
  final ContentResolver mResolver;
  private final VoiceInteractionManagerServiceStub mServiceStub;
  SoundTriggerInternal mSoundTriggerInternal;
  private final RemoteCallbackList<IVoiceInteractionSessionListener> mVoiceInteractionSessionListeners = new RemoteCallbackList();
  
  public VoiceInteractionManagerService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mResolver = paramContext.getContentResolver();
    this.mDbHelper = new DatabaseHelper(paramContext);
    this.mServiceStub = new VoiceInteractionManagerServiceStub();
    this.mAmInternal = ((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class));
    this.mLoadedKeyphraseIds = new TreeSet();
    ((PackageManagerInternal)LocalServices.getService(PackageManagerInternal.class)).setVoiceInteractionPackagesProvider(new PackageManagerInternal.PackagesProvider()
    {
      public String[] getPackages(int paramAnonymousInt)
      {
        VoiceInteractionManagerService.-get0(VoiceInteractionManagerService.this).initForUser(paramAnonymousInt);
        ComponentName localComponentName = VoiceInteractionManagerService.-get0(VoiceInteractionManagerService.this).getCurInteractor(paramAnonymousInt);
        if (localComponentName != null) {
          return new String[] { localComponentName.getPackageName() };
        }
        return null;
      }
    });
  }
  
  public void onBootPhase(int paramInt)
  {
    if (500 == paramInt) {
      this.mSoundTriggerInternal = ((SoundTriggerInternal)LocalServices.getService(SoundTriggerInternal.class));
    }
    while (paramInt != 600) {
      return;
    }
    this.mServiceStub.systemRunning(isSafeMode());
  }
  
  public void onStart()
  {
    publishBinderService("voiceinteraction", this.mServiceStub);
    publishLocalService(VoiceInteractionManagerInternal.class, new LocalService());
  }
  
  public void onStartUser(int paramInt)
  {
    this.mServiceStub.initForUser(paramInt);
  }
  
  public void onSwitchUser(int paramInt)
  {
    this.mServiceStub.switchUser(paramInt);
  }
  
  public void onUnlockUser(int paramInt)
  {
    this.mServiceStub.initForUser(paramInt);
    this.mServiceStub.switchImplementationIfNeeded(false);
  }
  
  class LocalService
    extends VoiceInteractionManagerInternal
  {
    LocalService() {}
    
    public void startLocalVoiceInteraction(IBinder paramIBinder, Bundle paramBundle)
    {
      VoiceInteractionManagerService.-get0(VoiceInteractionManagerService.this).startLocalVoiceInteraction(paramIBinder, paramBundle);
    }
    
    public void stopLocalVoiceInteraction(IBinder paramIBinder)
    {
      VoiceInteractionManagerService.-get0(VoiceInteractionManagerService.this).stopLocalVoiceInteraction(paramIBinder);
    }
    
    public boolean supportsLocalVoiceInteraction()
    {
      return VoiceInteractionManagerService.-get0(VoiceInteractionManagerService.this).supportsLocalVoiceInteraction();
    }
  }
  
  class VoiceInteractionManagerServiceStub
    extends IVoiceInteractionManagerService.Stub
  {
    private int mCurUser;
    private final boolean mEnableService = shouldEnableService(VoiceInteractionManagerService.this.mContext.getResources());
    VoiceInteractionManagerServiceImpl mImpl;
    PackageMonitor mPackageMonitor = new PackageMonitor()
    {
      public boolean onHandleForceStop(Intent arg1, String[] paramAnonymousArrayOfString, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        int i = UserHandle.getUserId(paramAnonymousInt);
        ??? = VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.getCurInteractor(i);
        ComponentName localComponentName = VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.getCurRecognizer(i);
        boolean bool2 = false;
        paramAnonymousInt = 0;
        int j = paramAnonymousArrayOfString.length;
        for (;;)
        {
          boolean bool1 = bool2;
          String str;
          if (paramAnonymousInt < j)
          {
            str = paramAnonymousArrayOfString[paramAnonymousInt];
            if ((??? != null) && (str.equals(???.getPackageName()))) {
              bool1 = true;
            }
          }
          else
          {
            label70:
            if ((!bool1) || (!paramAnonymousBoolean)) {}
          }
          synchronized (VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this)
          {
            VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.-wrap0(VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this);
            if (VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.mImpl != null)
            {
              VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.mImpl.shutdownLocked();
              VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.mImpl = null;
            }
            VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.setCurInteractor(null, i);
            VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.setCurRecognizer(null, i);
            VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.resetCurAssistant(i);
            VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.initForUser(i);
            VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.switchImplementationIfNeededLocked(true);
            return bool1;
            if ((localComponentName != null) && (str.equals(localComponentName.getPackageName())))
            {
              bool1 = true;
              break label70;
            }
            paramAnonymousInt += 1;
          }
        }
      }
      
      public void onHandleUserStop(Intent paramAnonymousIntent, int paramAnonymousInt) {}
      
      public void onSomePackagesChanged()
      {
        int i = getChangingUserId();
        synchronized (VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this)
        {
          ComponentName localComponentName1 = VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.getCurInteractor(i);
          ComponentName localComponentName2 = VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.getCurRecognizer(i);
          ComponentName localComponentName3 = VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.getCurAssistant(i);
          if (localComponentName2 == null)
          {
            if (anyPackagesAppearing())
            {
              localComponentName1 = VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.findAvailRecognizer(null, i);
              if (localComponentName1 != null) {
                VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.setCurRecognizer(localComponentName1, i);
              }
            }
            return;
          }
          if (localComponentName1 != null)
          {
            if (isPackageDisappearing(localComponentName1.getPackageName()) == 3)
            {
              VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.setCurInteractor(null, i);
              VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.setCurRecognizer(null, i);
              VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.resetCurAssistant(i);
              VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.initForUser(i);
              return;
            }
            if ((isPackageAppearing(localComponentName1.getPackageName()) != 0) && (VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.mImpl != null) && (localComponentName1.getPackageName().equals(VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.mImpl.mComponent.getPackageName()))) {
              VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.switchImplementationIfNeededLocked(true);
            }
            return;
          }
          if ((localComponentName3 != null) && (isPackageDisappearing(localComponentName3.getPackageName()) == 3))
          {
            VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.setCurInteractor(null, i);
            VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.setCurRecognizer(null, i);
            VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.resetCurAssistant(i);
            VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.initForUser(i);
            return;
          }
          int j = isPackageDisappearing(localComponentName2.getPackageName());
          if ((j == 3) || (j == 2)) {
            VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.setCurRecognizer(VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.findAvailRecognizer(null, i), i);
          }
          while (!isPackageModified(localComponentName2.getPackageName())) {
            return;
          }
          VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.setCurRecognizer(VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.findAvailRecognizer(localComponentName2.getPackageName(), i), i);
        }
      }
    };
    private boolean mSafeMode;
    
    VoiceInteractionManagerServiceStub() {}
    
    private void enforceCallingPermission(String paramString)
    {
      if (VoiceInteractionManagerService.this.mContext.checkCallingOrSelfPermission(paramString) != 0) {
        throw new SecurityException("Caller does not hold the permission " + paramString);
      }
    }
    
    private String getForceVoiceInteractionServicePackage(Resources paramResources)
    {
      String str = paramResources.getString(17039467);
      paramResources = str;
      if (TextUtils.isEmpty(str)) {
        paramResources = null;
      }
      return paramResources;
    }
    
    private boolean shouldEnableService(Resources paramResources)
    {
      return (!ActivityManager.isLowRamDeviceStatic()) || (getForceVoiceInteractionServicePackage(paramResources) != null);
    }
    
    /* Error */
    private void unloadAllKeyphraseModels()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 36	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:this$0	Lcom/android/server/voiceinteraction/VoiceInteractionManagerService;
      //   6: getfield 108	com/android/server/voiceinteraction/VoiceInteractionManagerService:mLoadedKeyphraseIds	Ljava/util/TreeSet;
      //   9: invokeinterface 114 1 0
      //   14: astore 5
      //   16: aload 5
      //   18: invokeinterface 119 1 0
      //   23: ifeq +94 -> 117
      //   26: aload 5
      //   28: invokeinterface 123 1 0
      //   33: checkcast 125	java/lang/Integer
      //   36: invokevirtual 129	java/lang/Integer:intValue	()I
      //   39: istore_1
      //   40: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   43: lstore_3
      //   44: aload_0
      //   45: getfield 36	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:this$0	Lcom/android/server/voiceinteraction/VoiceInteractionManagerService;
      //   48: getfield 139	com/android/server/voiceinteraction/VoiceInteractionManagerService:mSoundTriggerInternal	Lcom/android/server/soundtrigger/SoundTriggerInternal;
      //   51: iload_1
      //   52: invokevirtual 145	com/android/server/soundtrigger/SoundTriggerInternal:unloadKeyphraseModel	(I)I
      //   55: istore_2
      //   56: iload_2
      //   57: ifeq +37 -> 94
      //   60: ldc -109
      //   62: new 68	java/lang/StringBuilder
      //   65: dup
      //   66: invokespecial 69	java/lang/StringBuilder:<init>	()V
      //   69: ldc -107
      //   71: invokevirtual 75	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   74: iload_1
      //   75: invokevirtual 152	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   78: ldc -102
      //   80: invokevirtual 75	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   83: iload_2
      //   84: invokevirtual 152	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   87: invokevirtual 79	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   90: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   93: pop
      //   94: lload_3
      //   95: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   98: goto -82 -> 16
      //   101: astore 5
      //   103: aload_0
      //   104: monitorexit
      //   105: aload 5
      //   107: athrow
      //   108: astore 5
      //   110: lload_3
      //   111: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   114: aload 5
      //   116: athrow
      //   117: aload_0
      //   118: getfield 36	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:this$0	Lcom/android/server/voiceinteraction/VoiceInteractionManagerService;
      //   121: getfield 108	com/android/server/voiceinteraction/VoiceInteractionManagerService:mLoadedKeyphraseIds	Ljava/util/TreeSet;
      //   124: invokevirtual 169	java/util/TreeSet:clear	()V
      //   127: aload_0
      //   128: monitorexit
      //   129: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	130	0	this	VoiceInteractionManagerServiceStub
      //   39	36	1	i	int
      //   55	29	2	j	int
      //   43	68	3	l	long
      //   14	13	5	localIterator	java.util.Iterator
      //   101	5	5	localObject1	Object
      //   108	7	5	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   2	16	101	finally
      //   16	44	101	finally
      //   94	98	101	finally
      //   110	117	101	finally
      //   117	127	101	finally
      //   44	56	108	finally
      //   60	94	108	finally
    }
    
    /* Error */
    public boolean activeServiceSupportsAssist()
    {
      // Byte code:
      //   0: aload_0
      //   1: ldc -84
      //   3: invokespecial 174	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:enforceCallingPermission	(Ljava/lang/String;)V
      //   6: aload_0
      //   7: monitorenter
      //   8: aload_0
      //   9: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   12: ifnull +28 -> 40
      //   15: aload_0
      //   16: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   19: getfield 182	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mInfo	Landroid/service/voice/VoiceInteractionServiceInfo;
      //   22: ifnull +18 -> 40
      //   25: aload_0
      //   26: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   29: getfield 182	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mInfo	Landroid/service/voice/VoiceInteractionServiceInfo;
      //   32: invokevirtual 187	android/service/voice/VoiceInteractionServiceInfo:getSupportsAssist	()Z
      //   35: istore_1
      //   36: aload_0
      //   37: monitorexit
      //   38: iload_1
      //   39: ireturn
      //   40: iconst_0
      //   41: istore_1
      //   42: goto -6 -> 36
      //   45: astore_2
      //   46: aload_0
      //   47: monitorexit
      //   48: aload_2
      //   49: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	50	0	this	VoiceInteractionManagerServiceStub
      //   35	7	1	bool	boolean
      //   45	4	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   8	36	45	finally
    }
    
    /* Error */
    public boolean activeServiceSupportsLaunchFromKeyguard()
      throws RemoteException
    {
      // Byte code:
      //   0: aload_0
      //   1: ldc -84
      //   3: invokespecial 174	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:enforceCallingPermission	(Ljava/lang/String;)V
      //   6: aload_0
      //   7: monitorenter
      //   8: aload_0
      //   9: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   12: ifnull +28 -> 40
      //   15: aload_0
      //   16: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   19: getfield 182	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mInfo	Landroid/service/voice/VoiceInteractionServiceInfo;
      //   22: ifnull +18 -> 40
      //   25: aload_0
      //   26: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   29: getfield 182	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mInfo	Landroid/service/voice/VoiceInteractionServiceInfo;
      //   32: invokevirtual 193	android/service/voice/VoiceInteractionServiceInfo:getSupportsLaunchFromKeyguard	()Z
      //   35: istore_1
      //   36: aload_0
      //   37: monitorexit
      //   38: iload_1
      //   39: ireturn
      //   40: iconst_0
      //   41: istore_1
      //   42: goto -6 -> 36
      //   45: astore_2
      //   46: aload_0
      //   47: monitorexit
      //   48: aload_2
      //   49: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	50	0	this	VoiceInteractionManagerServiceStub
      //   35	7	1	bool	boolean
      //   45	4	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   8	36	45	finally
    }
    
    /* Error */
    public void closeSystemDialogs(IBinder paramIBinder)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   6: ifnonnull +14 -> 20
      //   9: ldc -109
      //   11: ldc -58
      //   13: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   16: pop
      //   17: aload_0
      //   18: monitorexit
      //   19: return
      //   20: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   23: lstore_2
      //   24: aload_0
      //   25: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   28: aload_1
      //   29: invokevirtual 201	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:closeSystemDialogsLocked	(Landroid/os/IBinder;)V
      //   32: lload_2
      //   33: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   36: aload_0
      //   37: monitorexit
      //   38: return
      //   39: astore_1
      //   40: lload_2
      //   41: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   44: aload_1
      //   45: athrow
      //   46: astore_1
      //   47: aload_0
      //   48: monitorexit
      //   49: aload_1
      //   50: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	51	0	this	VoiceInteractionManagerServiceStub
      //   0	51	1	paramIBinder	IBinder
      //   23	18	2	l	long
      // Exception table:
      //   from	to	target	type
      //   24	32	39	finally
      //   2	17	46	finally
      //   20	24	46	finally
      //   32	36	46	finally
      //   40	46	46	finally
    }
    
    /* Error */
    public int deleteKeyphraseSoundModel(int paramInt, String paramString)
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore_3
      //   2: aload_0
      //   3: ldc -51
      //   5: invokespecial 174	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:enforceCallingPermission	(Ljava/lang/String;)V
      //   8: aload_2
      //   9: ifnonnull +13 -> 22
      //   12: new 207	java/lang/IllegalArgumentException
      //   15: dup
      //   16: ldc -47
      //   18: invokespecial 210	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
      //   21: athrow
      //   22: invokestatic 215	android/os/UserHandle:getCallingUserId	()I
      //   25: istore 4
      //   27: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   30: lstore 6
      //   32: aload_0
      //   33: getfield 36	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:this$0	Lcom/android/server/voiceinteraction/VoiceInteractionManagerService;
      //   36: getfield 139	com/android/server/voiceinteraction/VoiceInteractionManagerService:mSoundTriggerInternal	Lcom/android/server/soundtrigger/SoundTriggerInternal;
      //   39: iload_1
      //   40: invokevirtual 145	com/android/server/soundtrigger/SoundTriggerInternal:unloadKeyphraseModel	(I)I
      //   43: istore 5
      //   45: iload 5
      //   47: ifeq +29 -> 76
      //   50: ldc -109
      //   52: new 68	java/lang/StringBuilder
      //   55: dup
      //   56: invokespecial 69	java/lang/StringBuilder:<init>	()V
      //   59: ldc -39
      //   61: invokevirtual 75	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   64: iload 5
      //   66: invokevirtual 152	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   69: invokevirtual 79	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   72: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   75: pop
      //   76: aload_0
      //   77: getfield 36	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:this$0	Lcom/android/server/voiceinteraction/VoiceInteractionManagerService;
      //   80: getfield 221	com/android/server/voiceinteraction/VoiceInteractionManagerService:mDbHelper	Lcom/android/server/voiceinteraction/DatabaseHelper;
      //   83: iload_1
      //   84: iload 4
      //   86: aload_2
      //   87: invokevirtual 226	com/android/server/voiceinteraction/DatabaseHelper:deleteKeyphraseSoundModel	(IILjava/lang/String;)Z
      //   90: istore 8
      //   92: iload 8
      //   94: ifeq +58 -> 152
      //   97: iload 8
      //   99: ifeq +46 -> 145
      //   102: aload_0
      //   103: monitorenter
      //   104: aload_0
      //   105: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   108: ifnull +20 -> 128
      //   111: aload_0
      //   112: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   115: getfield 230	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mService	Landroid/service/voice/IVoiceInteractionService;
      //   118: ifnull +10 -> 128
      //   121: aload_0
      //   122: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   125: invokevirtual 233	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:notifySoundModelsChangedLocked	()V
      //   128: aload_0
      //   129: getfield 36	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:this$0	Lcom/android/server/voiceinteraction/VoiceInteractionManagerService;
      //   132: getfield 108	com/android/server/voiceinteraction/VoiceInteractionManagerService:mLoadedKeyphraseIds	Ljava/util/TreeSet;
      //   135: iload_1
      //   136: invokestatic 237	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   139: invokevirtual 241	java/util/TreeSet:remove	(Ljava/lang/Object;)Z
      //   142: pop
      //   143: aload_0
      //   144: monitorexit
      //   145: lload 6
      //   147: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   150: iload_3
      //   151: ireturn
      //   152: ldc -14
      //   154: istore_3
      //   155: goto -58 -> 97
      //   158: astore_2
      //   159: aload_0
      //   160: monitorexit
      //   161: aload_2
      //   162: athrow
      //   163: astore_2
      //   164: iconst_0
      //   165: ifeq +46 -> 211
      //   168: aload_0
      //   169: monitorenter
      //   170: aload_0
      //   171: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   174: ifnull +20 -> 194
      //   177: aload_0
      //   178: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   181: getfield 230	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mService	Landroid/service/voice/IVoiceInteractionService;
      //   184: ifnull +10 -> 194
      //   187: aload_0
      //   188: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   191: invokevirtual 233	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:notifySoundModelsChangedLocked	()V
      //   194: aload_0
      //   195: getfield 36	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:this$0	Lcom/android/server/voiceinteraction/VoiceInteractionManagerService;
      //   198: getfield 108	com/android/server/voiceinteraction/VoiceInteractionManagerService:mLoadedKeyphraseIds	Ljava/util/TreeSet;
      //   201: iload_1
      //   202: invokestatic 237	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   205: invokevirtual 241	java/util/TreeSet:remove	(Ljava/lang/Object;)Z
      //   208: pop
      //   209: aload_0
      //   210: monitorexit
      //   211: lload 6
      //   213: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   216: aload_2
      //   217: athrow
      //   218: astore_2
      //   219: aload_0
      //   220: monitorexit
      //   221: aload_2
      //   222: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	223	0	this	VoiceInteractionManagerServiceStub
      //   0	223	1	paramInt	int
      //   0	223	2	paramString	String
      //   1	154	3	i	int
      //   25	60	4	j	int
      //   43	22	5	k	int
      //   30	182	6	l	long
      //   90	8	8	bool	boolean
      // Exception table:
      //   from	to	target	type
      //   104	128	158	finally
      //   128	143	158	finally
      //   32	45	163	finally
      //   50	76	163	finally
      //   76	92	163	finally
      //   170	194	218	finally
      //   194	209	218	finally
    }
    
    public boolean deliverNewSession(IBinder paramIBinder, IVoiceInteractionSession paramIVoiceInteractionSession, IVoiceInteractor paramIVoiceInteractor)
    {
      try
      {
        if (this.mImpl == null) {
          throw new SecurityException("deliverNewSession without running voice interaction service");
        }
      }
      finally {}
      long l = Binder.clearCallingIdentity();
      try
      {
        boolean bool = this.mImpl.deliverNewSessionLocked(paramIBinder, paramIVoiceInteractionSession, paramIVoiceInteractor);
        Binder.restoreCallingIdentity(l);
        return bool;
      }
      finally
      {
        paramIBinder = finally;
        Binder.restoreCallingIdentity(l);
        throw paramIBinder;
      }
    }
    
    public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      if (VoiceInteractionManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramPrintWriter.println("Permission Denial: can't dump PowerManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      try
      {
        paramPrintWriter.println("VOICE INTERACTION MANAGER (dumpsys voiceinteraction)");
        paramPrintWriter.println("  mEnableService: " + this.mEnableService);
        if (this.mImpl == null)
        {
          paramPrintWriter.println("  (No active implementation)");
          return;
        }
        this.mImpl.dumpLocked(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        VoiceInteractionManagerService.this.mSoundTriggerInternal.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        return;
      }
      finally {}
    }
    
    VoiceInteractionServiceInfo findAvailInteractor(int paramInt, String paramString)
    {
      List localList = VoiceInteractionManagerService.this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.service.voice.VoiceInteractionService"), 269221888, paramInt);
      int j = localList.size();
      if (j == 0)
      {
        Slog.w("VoiceInteractionManagerService", "no available voice interaction services found for user " + paramInt);
        return null;
      }
      Object localObject1 = null;
      int i = 0;
      for (;;)
      {
        ServiceInfo localServiceInfo;
        Object localObject2;
        ComponentName localComponentName;
        Object localObject3;
        if (i < j)
        {
          localServiceInfo = ((ResolveInfo)localList.get(i)).serviceInfo;
          localObject2 = localObject1;
          if ((localServiceInfo.applicationInfo.flags & 0x1) != 0) {
            localComponentName = new ComponentName(localServiceInfo.packageName, localServiceInfo.name);
          }
        }
        else
        {
          VoiceInteractionServiceInfo localVoiceInteractionServiceInfo;
          label307:
          do
          {
            try
            {
              localVoiceInteractionServiceInfo = new VoiceInteractionServiceInfo(VoiceInteractionManagerService.this.mContext.getPackageManager(), localComponentName, paramInt);
              if (localVoiceInteractionServiceInfo.getParseError() != null) {
                break label307;
              }
              if (paramString == null) {
                continue;
              }
              localObject2 = localObject1;
              if (!localVoiceInteractionServiceInfo.getServiceInfo().packageName.equals(paramString)) {
                break;
              }
            }
            catch (PackageManager.NameNotFoundException localNameNotFoundException)
            {
              Slog.w("VoiceInteractionManagerService", "Failure looking up interaction service " + localComponentName);
              localObject3 = localObject1;
              break;
            }
            Slog.w("VoiceInteractionManagerService", "More than one voice interaction service, picking first " + new ComponentName(((VoiceInteractionServiceInfo)localObject1).getServiceInfo().packageName, ((VoiceInteractionServiceInfo)localObject1).getServiceInfo().name) + " over " + new ComponentName(localServiceInfo.packageName, localServiceInfo.name));
            localObject2 = localObject1;
            break;
            Slog.w("VoiceInteractionManagerService", "Bad interaction service " + localComponentName + ": " + localVoiceInteractionServiceInfo.getParseError());
            localObject3 = localObject1;
            break;
            return (VoiceInteractionServiceInfo)localObject1;
          } while (localObject1 != null);
          localObject3 = localVoiceInteractionServiceInfo;
        }
        i += 1;
        localObject1 = localObject3;
      }
    }
    
    ComponentName findAvailRecognizer(String paramString, int paramInt)
    {
      List localList = VoiceInteractionManagerService.this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.speech.RecognitionService"), 0, paramInt);
      int i = localList.size();
      if (i == 0)
      {
        Slog.w("VoiceInteractionManagerService", "no available voice recognition services found for user " + paramInt);
        return null;
      }
      if (paramString != null)
      {
        paramInt = 0;
        while (paramInt < i)
        {
          ServiceInfo localServiceInfo = ((ResolveInfo)localList.get(paramInt)).serviceInfo;
          if (paramString.equals(localServiceInfo.packageName)) {
            return new ComponentName(localServiceInfo.packageName, localServiceInfo.name);
          }
          paramInt += 1;
        }
      }
      if (i > 1) {
        Slog.w("VoiceInteractionManagerService", "more than one voice recognition service found, picking first");
      }
      paramString = ((ResolveInfo)localList.get(0)).serviceInfo;
      return new ComponentName(paramString.packageName, paramString.name);
    }
    
    /* Error */
    public void finish(IBinder paramIBinder)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   6: ifnonnull +15 -> 21
      //   9: ldc -109
      //   11: ldc_w 381
      //   14: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   17: pop
      //   18: aload_0
      //   19: monitorexit
      //   20: return
      //   21: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   24: lstore_2
      //   25: aload_0
      //   26: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   29: aload_1
      //   30: iconst_0
      //   31: invokevirtual 385	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:finishLocked	(Landroid/os/IBinder;Z)V
      //   34: lload_2
      //   35: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   38: aload_0
      //   39: monitorexit
      //   40: return
      //   41: astore_1
      //   42: lload_2
      //   43: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   46: aload_1
      //   47: athrow
      //   48: astore_1
      //   49: aload_0
      //   50: monitorexit
      //   51: aload_1
      //   52: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	53	0	this	VoiceInteractionManagerServiceStub
      //   0	53	1	paramIBinder	IBinder
      //   24	19	2	l	long
      // Exception table:
      //   from	to	target	type
      //   25	34	41	finally
      //   2	18	48	finally
      //   21	25	48	finally
      //   34	38	48	finally
      //   42	48	48	finally
    }
    
    public ComponentName getActiveServiceComponentName()
    {
      ComponentName localComponentName = null;
      enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
      try
      {
        if (this.mImpl != null) {
          localComponentName = this.mImpl.mComponent;
        }
        return localComponentName;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    ComponentName getCurAssistant(int paramInt)
    {
      String str = Settings.Secure.getStringForUser(VoiceInteractionManagerService.this.mContext.getContentResolver(), "assistant", paramInt);
      if (TextUtils.isEmpty(str)) {
        return null;
      }
      return ComponentName.unflattenFromString(str);
    }
    
    ComponentName getCurInteractor(int paramInt)
    {
      String str = Settings.Secure.getStringForUser(VoiceInteractionManagerService.this.mContext.getContentResolver(), "voice_interaction_service", paramInt);
      if (TextUtils.isEmpty(str)) {
        return null;
      }
      return ComponentName.unflattenFromString(str);
    }
    
    ComponentName getCurRecognizer(int paramInt)
    {
      String str = Settings.Secure.getStringForUser(VoiceInteractionManagerService.this.mContext.getContentResolver(), "voice_recognition_service", paramInt);
      if (TextUtils.isEmpty(str)) {
        return null;
      }
      return ComponentName.unflattenFromString(str);
    }
    
    /* Error */
    public int getDisabledShowContext()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   6: ifnonnull +16 -> 22
      //   9: ldc -109
      //   11: ldc_w 418
      //   14: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   17: pop
      //   18: aload_0
      //   19: monitorexit
      //   20: iconst_0
      //   21: ireturn
      //   22: invokestatic 263	android/os/Binder:getCallingUid	()I
      //   25: istore_1
      //   26: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   29: lstore_2
      //   30: aload_0
      //   31: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   34: iload_1
      //   35: invokevirtual 421	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:getDisabledShowContextLocked	(I)I
      //   38: istore_1
      //   39: lload_2
      //   40: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   43: aload_0
      //   44: monitorexit
      //   45: iload_1
      //   46: ireturn
      //   47: astore 4
      //   49: lload_2
      //   50: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   53: aload 4
      //   55: athrow
      //   56: astore 4
      //   58: aload_0
      //   59: monitorexit
      //   60: aload 4
      //   62: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	63	0	this	VoiceInteractionManagerServiceStub
      //   25	21	1	i	int
      //   29	21	2	l	long
      //   47	7	4	localObject1	Object
      //   56	5	4	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   30	39	47	finally
      //   2	18	56	finally
      //   22	30	56	finally
      //   39	43	56	finally
      //   49	56	56	finally
    }
    
    public SoundTrigger.ModuleProperties getDspModuleProperties(IVoiceInteractionService paramIVoiceInteractionService)
    {
      long l;
      try
      {
        if ((this.mImpl == null) || (this.mImpl.mService == null)) {
          throw new SecurityException("Caller is not the current voice interaction service");
        }
      }
      finally
      {
        do
        {
          throw paramIVoiceInteractionService;
        } while ((paramIVoiceInteractionService == null) || (paramIVoiceInteractionService.asBinder() != this.mImpl.mService.asBinder()));
      }
    }
    
    public SoundTrigger.KeyphraseSoundModel getKeyphraseSoundModel(int paramInt, String paramString)
    {
      enforceCallingPermission("android.permission.MANAGE_VOICE_KEYPHRASES");
      if (paramString == null) {
        throw new IllegalArgumentException("Illegal argument(s) in getKeyphraseSoundModel");
      }
      int i = UserHandle.getCallingUserId();
      long l = Binder.clearCallingIdentity();
      try
      {
        paramString = VoiceInteractionManagerService.this.mDbHelper.getKeyphraseSoundModel(paramInt, i, paramString);
        return paramString;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    /* Error */
    public int getUserDisabledShowContext()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   6: ifnonnull +16 -> 22
      //   9: ldc -109
      //   11: ldc_w 445
      //   14: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   17: pop
      //   18: aload_0
      //   19: monitorexit
      //   20: iconst_0
      //   21: ireturn
      //   22: invokestatic 263	android/os/Binder:getCallingUid	()I
      //   25: istore_1
      //   26: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   29: lstore_2
      //   30: aload_0
      //   31: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   34: iload_1
      //   35: invokevirtual 448	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:getUserDisabledShowContextLocked	(I)I
      //   38: istore_1
      //   39: lload_2
      //   40: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   43: aload_0
      //   44: monitorexit
      //   45: iload_1
      //   46: ireturn
      //   47: astore 4
      //   49: lload_2
      //   50: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   53: aload 4
      //   55: athrow
      //   56: astore 4
      //   58: aload_0
      //   59: monitorexit
      //   60: aload 4
      //   62: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	63	0	this	VoiceInteractionManagerServiceStub
      //   25	21	1	i	int
      //   29	21	2	l	long
      //   47	7	4	localObject1	Object
      //   56	5	4	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   30	39	47	finally
      //   2	18	56	finally
      //   22	30	56	finally
      //   39	43	56	finally
      //   49	56	56	finally
    }
    
    /* Error */
    public void hideCurrentSession()
      throws RemoteException
    {
      // Byte code:
      //   0: aload_0
      //   1: ldc -84
      //   3: invokespecial 174	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:enforceCallingPermission	(Ljava/lang/String;)V
      //   6: aload_0
      //   7: monitorenter
      //   8: aload_0
      //   9: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   12: astore_3
      //   13: aload_3
      //   14: ifnonnull +6 -> 20
      //   17: aload_0
      //   18: monitorexit
      //   19: return
      //   20: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   23: lstore_1
      //   24: aload_0
      //   25: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   28: getfield 453	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mActiveSession	Lcom/android/server/voiceinteraction/VoiceInteractionSessionConnection;
      //   31: ifnull +33 -> 64
      //   34: aload_0
      //   35: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   38: getfield 453	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mActiveSession	Lcom/android/server/voiceinteraction/VoiceInteractionSessionConnection;
      //   41: getfield 459	com/android/server/voiceinteraction/VoiceInteractionSessionConnection:mSession	Landroid/service/voice/IVoiceInteractionSession;
      //   44: astore_3
      //   45: aload_3
      //   46: ifnull +18 -> 64
      //   49: aload_0
      //   50: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   53: getfield 453	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mActiveSession	Lcom/android/server/voiceinteraction/VoiceInteractionSessionConnection;
      //   56: getfield 459	com/android/server/voiceinteraction/VoiceInteractionSessionConnection:mSession	Landroid/service/voice/IVoiceInteractionSession;
      //   59: invokeinterface 463 1 0
      //   64: lload_1
      //   65: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   68: aload_0
      //   69: monitorexit
      //   70: return
      //   71: astore_3
      //   72: ldc -109
      //   74: ldc_w 465
      //   77: aload_3
      //   78: invokestatic 470	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   81: pop
      //   82: goto -18 -> 64
      //   85: astore_3
      //   86: lload_1
      //   87: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   90: aload_3
      //   91: athrow
      //   92: astore_3
      //   93: aload_0
      //   94: monitorexit
      //   95: aload_3
      //   96: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	97	0	this	VoiceInteractionManagerServiceStub
      //   23	64	1	l	long
      //   12	34	3	localObject1	Object
      //   71	7	3	localRemoteException	RemoteException
      //   85	6	3	localObject2	Object
      //   92	4	3	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   49	64	71	android/os/RemoteException
      //   24	45	85	finally
      //   49	64	85	finally
      //   72	82	85	finally
      //   8	13	92	finally
      //   20	24	92	finally
      //   64	68	92	finally
      //   86	92	92	finally
    }
    
    /* Error */
    public boolean hideSessionFromSession(IBinder paramIBinder)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   6: ifnonnull +16 -> 22
      //   9: ldc -109
      //   11: ldc_w 474
      //   14: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   17: pop
      //   18: aload_0
      //   19: monitorexit
      //   20: iconst_0
      //   21: ireturn
      //   22: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   25: lstore_2
      //   26: aload_0
      //   27: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   30: invokevirtual 477	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:hideSessionLocked	()Z
      //   33: istore 4
      //   35: lload_2
      //   36: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   39: aload_0
      //   40: monitorexit
      //   41: iload 4
      //   43: ireturn
      //   44: astore_1
      //   45: lload_2
      //   46: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   49: aload_1
      //   50: athrow
      //   51: astore_1
      //   52: aload_0
      //   53: monitorexit
      //   54: aload_1
      //   55: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	56	0	this	VoiceInteractionManagerServiceStub
      //   0	56	1	paramIBinder	IBinder
      //   25	21	2	l	long
      //   33	9	4	bool	boolean
      // Exception table:
      //   from	to	target	type
      //   26	35	44	finally
      //   2	18	51	finally
      //   22	26	51	finally
      //   35	39	51	finally
      //   45	51	51	finally
    }
    
    public void initForUser(int paramInt)
    {
      Object localObject4 = Settings.Secure.getStringForUser(VoiceInteractionManagerService.this.mContext.getContentResolver(), "voice_interaction_service", paramInt);
      Object localObject3 = getCurRecognizer(paramInt);
      localObject5 = null;
      Object localObject1 = localObject5;
      Object localObject2 = localObject3;
      if (localObject4 == null)
      {
        localObject1 = localObject5;
        localObject2 = localObject3;
        if (localObject3 != null)
        {
          localObject1 = localObject5;
          localObject2 = localObject3;
          if (this.mEnableService)
          {
            localObject5 = findAvailInteractor(paramInt, ((ComponentName)localObject3).getPackageName());
            localObject1 = localObject5;
            localObject2 = localObject3;
            if (localObject5 != null)
            {
              localObject2 = null;
              localObject1 = localObject5;
            }
          }
        }
      }
      localObject5 = getForceVoiceInteractionServicePackage(VoiceInteractionManagerService.this.mContext.getResources());
      localObject3 = localObject2;
      if (localObject5 != null)
      {
        localObject5 = findAvailInteractor(paramInt, (String)localObject5);
        localObject1 = localObject5;
        localObject3 = localObject2;
        if (localObject5 != null)
        {
          localObject3 = null;
          localObject1 = localObject5;
        }
      }
      localObject2 = localObject4;
      if (!this.mEnableService)
      {
        localObject2 = localObject4;
        if (localObject4 != null)
        {
          localObject2 = localObject4;
          if (!TextUtils.isEmpty((CharSequence)localObject4))
          {
            setCurInteractor(null, paramInt);
            localObject2 = "";
          }
        }
      }
      if (localObject3 != null)
      {
        IPackageManager localIPackageManager = AppGlobals.getPackageManager();
        localObject6 = null;
        localObject5 = null;
        if (!TextUtils.isEmpty((CharSequence)localObject2)) {}
        for (localObject4 = ComponentName.unflattenFromString((String)localObject2);; localObject4 = null)
        {
          localObject2 = localObject5;
          try
          {
            localObject3 = localIPackageManager.getServiceInfo((ComponentName)localObject3, 0, paramInt);
            localObject5 = localObject6;
            localObject2 = localObject3;
            if (localObject4 != null)
            {
              localObject2 = localObject3;
              localObject5 = localIPackageManager.getServiceInfo((ComponentName)localObject4, 0, paramInt);
              localObject2 = localObject3;
            }
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              localObject5 = localObject6;
            }
          }
          if ((localObject2 == null) || ((localObject4 != null) && (localObject5 == null))) {
            break;
          }
          return;
        }
      }
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = localObject1;
        if (this.mEnableService) {
          localObject2 = findAvailInteractor(paramInt, null);
        }
      }
      if (localObject2 != null)
      {
        setCurInteractor(new ComponentName(((VoiceInteractionServiceInfo)localObject2).getServiceInfo().packageName, ((VoiceInteractionServiceInfo)localObject2).getServiceInfo().name), paramInt);
        if (((VoiceInteractionServiceInfo)localObject2).getRecognitionService() != null)
        {
          setCurRecognizer(new ComponentName(((VoiceInteractionServiceInfo)localObject2).getServiceInfo().packageName, ((VoiceInteractionServiceInfo)localObject2).getRecognitionService()), paramInt);
          return;
        }
      }
      localObject1 = findAvailRecognizer(null, paramInt);
      if (localObject1 != null)
      {
        if (localObject2 == null) {
          setCurInteractor(null, paramInt);
        }
        setCurRecognizer((ComponentName)localObject1, paramInt);
      }
    }
    
    /* Error */
    public boolean isEnrolledForKeyphrase(IVoiceInteractionService paramIVoiceInteractionService, int paramInt, String paramString)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   6: ifnull +13 -> 19
      //   9: aload_0
      //   10: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   13: getfield 230	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mService	Landroid/service/voice/IVoiceInteractionService;
      //   16: ifnonnull +19 -> 35
      //   19: new 66	java/lang/SecurityException
      //   22: dup
      //   23: ldc_w 425
      //   26: invokespecial 81	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
      //   29: athrow
      //   30: astore_1
      //   31: aload_0
      //   32: monitorexit
      //   33: aload_1
      //   34: athrow
      //   35: aload_1
      //   36: invokeinterface 431 1 0
      //   41: astore_1
      //   42: aload_0
      //   43: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   46: getfield 230	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mService	Landroid/service/voice/IVoiceInteractionService;
      //   49: invokeinterface 431 1 0
      //   54: astore 8
      //   56: aload_1
      //   57: aload 8
      //   59: if_acmpne -40 -> 19
      //   62: aload_0
      //   63: monitorexit
      //   64: aload_3
      //   65: ifnonnull +14 -> 79
      //   68: new 207	java/lang/IllegalArgumentException
      //   71: dup
      //   72: ldc_w 514
      //   75: invokespecial 210	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
      //   78: athrow
      //   79: invokestatic 215	android/os/UserHandle:getCallingUserId	()I
      //   82: istore 4
      //   84: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   87: lstore 5
      //   89: aload_0
      //   90: getfield 36	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:this$0	Lcom/android/server/voiceinteraction/VoiceInteractionManagerService;
      //   93: getfield 221	com/android/server/voiceinteraction/VoiceInteractionManagerService:mDbHelper	Lcom/android/server/voiceinteraction/DatabaseHelper;
      //   96: iload_2
      //   97: iload 4
      //   99: aload_3
      //   100: invokevirtual 442	com/android/server/voiceinteraction/DatabaseHelper:getKeyphraseSoundModel	(IILjava/lang/String;)Landroid/hardware/soundtrigger/SoundTrigger$KeyphraseSoundModel;
      //   103: astore_1
      //   104: aload_1
      //   105: ifnull +14 -> 119
      //   108: iconst_1
      //   109: istore 7
      //   111: lload 5
      //   113: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   116: iload 7
      //   118: ireturn
      //   119: iconst_0
      //   120: istore 7
      //   122: goto -11 -> 111
      //   125: astore_1
      //   126: lload 5
      //   128: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   131: aload_1
      //   132: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	133	0	this	VoiceInteractionManagerServiceStub
      //   0	133	1	paramIVoiceInteractionService	IVoiceInteractionService
      //   0	133	2	paramInt	int
      //   0	133	3	paramString	String
      //   82	16	4	i	int
      //   87	40	5	l	long
      //   109	12	7	bool	boolean
      //   54	4	8	localIBinder	IBinder
      // Exception table:
      //   from	to	target	type
      //   2	19	30	finally
      //   19	30	30	finally
      //   35	56	30	finally
      //   89	104	125	finally
    }
    
    public boolean isSessionRunning()
    {
      boolean bool2 = false;
      enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
      boolean bool1 = bool2;
      try
      {
        if (this.mImpl != null)
        {
          VoiceInteractionSessionConnection localVoiceInteractionSessionConnection = this.mImpl.mActiveSession;
          bool1 = bool2;
          if (localVoiceInteractionSessionConnection != null) {
            bool1 = true;
          }
        }
        return bool1;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    /* Error */
    public void launchVoiceAssistFromKeyguard()
    {
      // Byte code:
      //   0: aload_0
      //   1: ldc -84
      //   3: invokespecial 174	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:enforceCallingPermission	(Ljava/lang/String;)V
      //   6: aload_0
      //   7: monitorenter
      //   8: aload_0
      //   9: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   12: ifnonnull +15 -> 27
      //   15: ldc -109
      //   17: ldc_w 518
      //   20: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   23: pop
      //   24: aload_0
      //   25: monitorexit
      //   26: return
      //   27: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   30: lstore_1
      //   31: aload_0
      //   32: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   35: invokevirtual 520	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:launchVoiceAssistFromKeyguard	()V
      //   38: lload_1
      //   39: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   42: aload_0
      //   43: monitorexit
      //   44: return
      //   45: astore_3
      //   46: lload_1
      //   47: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   50: aload_3
      //   51: athrow
      //   52: astore_3
      //   53: aload_0
      //   54: monitorexit
      //   55: aload_3
      //   56: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	57	0	this	VoiceInteractionManagerServiceStub
      //   30	17	1	l	long
      //   45	6	3	localObject1	Object
      //   52	4	3	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   31	38	45	finally
      //   8	24	52	finally
      //   27	31	52	finally
      //   38	42	52	finally
      //   46	52	52	finally
    }
    
    /* Error */
    public void onLockscreenShown()
    {
      // Byte code:
      //   0: aload_0
      //   1: ldc -84
      //   3: invokespecial 174	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:enforceCallingPermission	(Ljava/lang/String;)V
      //   6: aload_0
      //   7: monitorenter
      //   8: aload_0
      //   9: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   12: astore_3
      //   13: aload_3
      //   14: ifnonnull +6 -> 20
      //   17: aload_0
      //   18: monitorexit
      //   19: return
      //   20: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   23: lstore_1
      //   24: aload_0
      //   25: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   28: getfield 453	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mActiveSession	Lcom/android/server/voiceinteraction/VoiceInteractionSessionConnection;
      //   31: ifnull +33 -> 64
      //   34: aload_0
      //   35: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   38: getfield 453	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mActiveSession	Lcom/android/server/voiceinteraction/VoiceInteractionSessionConnection;
      //   41: getfield 459	com/android/server/voiceinteraction/VoiceInteractionSessionConnection:mSession	Landroid/service/voice/IVoiceInteractionSession;
      //   44: astore_3
      //   45: aload_3
      //   46: ifnull +18 -> 64
      //   49: aload_0
      //   50: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   53: getfield 453	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mActiveSession	Lcom/android/server/voiceinteraction/VoiceInteractionSessionConnection;
      //   56: getfield 459	com/android/server/voiceinteraction/VoiceInteractionSessionConnection:mSession	Landroid/service/voice/IVoiceInteractionSession;
      //   59: invokeinterface 523 1 0
      //   64: lload_1
      //   65: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   68: aload_0
      //   69: monitorexit
      //   70: return
      //   71: astore_3
      //   72: ldc -109
      //   74: ldc_w 525
      //   77: aload_3
      //   78: invokestatic 470	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   81: pop
      //   82: goto -18 -> 64
      //   85: astore_3
      //   86: lload_1
      //   87: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   90: aload_3
      //   91: athrow
      //   92: astore_3
      //   93: aload_0
      //   94: monitorexit
      //   95: aload_3
      //   96: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	97	0	this	VoiceInteractionManagerServiceStub
      //   23	64	1	l	long
      //   12	34	3	localObject1	Object
      //   71	7	3	localRemoteException	RemoteException
      //   85	6	3	localObject2	Object
      //   92	4	3	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   49	64	71	android/os/RemoteException
      //   24	45	85	finally
      //   49	64	85	finally
      //   72	82	85	finally
      //   8	13	92	finally
      //   20	24	92	finally
      //   64	68	92	finally
      //   86	92	92	finally
    }
    
    public void onSessionHidden()
    {
      try
      {
        int j = VoiceInteractionManagerService.-get1(VoiceInteractionManagerService.this).beginBroadcast();
        int i = 0;
        for (;;)
        {
          if (i < j)
          {
            IVoiceInteractionSessionListener localIVoiceInteractionSessionListener = (IVoiceInteractionSessionListener)VoiceInteractionManagerService.-get1(VoiceInteractionManagerService.this).getBroadcastItem(i);
            try
            {
              localIVoiceInteractionSessionListener.onVoiceSessionHidden();
              i += 1;
            }
            catch (RemoteException localRemoteException)
            {
              for (;;)
              {
                Slog.e("VoiceInteractionManagerService", "Error delivering voice interaction closed event.", localRemoteException);
              }
            }
          }
        }
        VoiceInteractionManagerService.-get1(VoiceInteractionManagerService.this).finishBroadcast();
      }
      finally {}
    }
    
    public void onSessionShown()
    {
      try
      {
        int j = VoiceInteractionManagerService.-get1(VoiceInteractionManagerService.this).beginBroadcast();
        int i = 0;
        for (;;)
        {
          if (i < j)
          {
            IVoiceInteractionSessionListener localIVoiceInteractionSessionListener = (IVoiceInteractionSessionListener)VoiceInteractionManagerService.-get1(VoiceInteractionManagerService.this).getBroadcastItem(i);
            try
            {
              localIVoiceInteractionSessionListener.onVoiceSessionShown();
              i += 1;
            }
            catch (RemoteException localRemoteException)
            {
              for (;;)
              {
                Slog.e("VoiceInteractionManagerService", "Error delivering voice interaction open event.", localRemoteException);
              }
            }
          }
        }
        VoiceInteractionManagerService.-get1(VoiceInteractionManagerService.this).finishBroadcast();
      }
      finally {}
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      try
      {
        boolean bool = super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        return bool;
      }
      catch (RuntimeException paramParcel1)
      {
        if (!(paramParcel1 instanceof SecurityException)) {
          Slog.wtf("VoiceInteractionManagerService", "VoiceInteractionManagerService Crash", paramParcel1);
        }
        throw paramParcel1;
      }
    }
    
    public void registerVoiceInteractionSessionListener(IVoiceInteractionSessionListener paramIVoiceInteractionSessionListener)
    {
      enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
      try
      {
        VoiceInteractionManagerService.-get1(VoiceInteractionManagerService.this).register(paramIVoiceInteractionSessionListener);
        return;
      }
      finally
      {
        paramIVoiceInteractionSessionListener = finally;
        throw paramIVoiceInteractionSessionListener;
      }
    }
    
    void resetCurAssistant(int paramInt)
    {
      Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.mContext.getContentResolver(), "assistant", null, paramInt);
    }
    
    void setCurInteractor(ComponentName paramComponentName, int paramInt)
    {
      ContentResolver localContentResolver = VoiceInteractionManagerService.this.mContext.getContentResolver();
      if (paramComponentName != null) {}
      for (paramComponentName = paramComponentName.flattenToShortString();; paramComponentName = "")
      {
        Settings.Secure.putStringForUser(localContentResolver, "voice_interaction_service", paramComponentName, paramInt);
        return;
      }
    }
    
    void setCurRecognizer(ComponentName paramComponentName, int paramInt)
    {
      ContentResolver localContentResolver = VoiceInteractionManagerService.this.mContext.getContentResolver();
      if (paramComponentName != null) {}
      for (paramComponentName = paramComponentName.flattenToShortString();; paramComponentName = "")
      {
        Settings.Secure.putStringForUser(localContentResolver, "voice_recognition_service", paramComponentName, paramInt);
        return;
      }
    }
    
    /* Error */
    public void setDisabledShowContext(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   6: ifnonnull +15 -> 21
      //   9: ldc -109
      //   11: ldc_w 586
      //   14: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   17: pop
      //   18: aload_0
      //   19: monitorexit
      //   20: return
      //   21: invokestatic 263	android/os/Binder:getCallingUid	()I
      //   24: istore_2
      //   25: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   28: lstore_3
      //   29: aload_0
      //   30: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   33: iload_2
      //   34: iload_1
      //   35: invokevirtual 590	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:setDisabledShowContextLocked	(II)V
      //   38: lload_3
      //   39: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   42: aload_0
      //   43: monitorexit
      //   44: return
      //   45: astore 5
      //   47: lload_3
      //   48: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   51: aload 5
      //   53: athrow
      //   54: astore 5
      //   56: aload_0
      //   57: monitorexit
      //   58: aload 5
      //   60: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	61	0	this	VoiceInteractionManagerServiceStub
      //   0	61	1	paramInt	int
      //   24	10	2	i	int
      //   28	20	3	l	long
      //   45	7	5	localObject1	Object
      //   54	5	5	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   29	38	45	finally
      //   2	18	54	finally
      //   21	29	54	finally
      //   38	42	54	finally
      //   47	54	54	finally
    }
    
    /* Error */
    public void setKeepAwake(IBinder paramIBinder, boolean paramBoolean)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   6: ifnonnull +15 -> 21
      //   9: ldc -109
      //   11: ldc_w 593
      //   14: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   17: pop
      //   18: aload_0
      //   19: monitorexit
      //   20: return
      //   21: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   24: lstore_3
      //   25: aload_0
      //   26: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   29: aload_1
      //   30: iload_2
      //   31: invokevirtual 596	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:setKeepAwakeLocked	(Landroid/os/IBinder;Z)V
      //   34: lload_3
      //   35: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   38: aload_0
      //   39: monitorexit
      //   40: return
      //   41: astore_1
      //   42: lload_3
      //   43: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   46: aload_1
      //   47: athrow
      //   48: astore_1
      //   49: aload_0
      //   50: monitorexit
      //   51: aload_1
      //   52: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	53	0	this	VoiceInteractionManagerServiceStub
      //   0	53	1	paramIBinder	IBinder
      //   0	53	2	paramBoolean	boolean
      //   24	19	3	l	long
      // Exception table:
      //   from	to	target	type
      //   25	34	41	finally
      //   2	18	48	finally
      //   21	25	48	finally
      //   34	38	48	finally
      //   42	48	48	finally
    }
    
    public void showSession(IVoiceInteractionService paramIVoiceInteractionService, Bundle paramBundle, int paramInt)
    {
      long l;
      try
      {
        if ((this.mImpl == null) || (this.mImpl.mService == null)) {
          throw new SecurityException("Caller is not the current voice interaction service");
        }
      }
      finally
      {
        do
        {
          throw paramIVoiceInteractionService;
        } while (paramIVoiceInteractionService.asBinder() != this.mImpl.mService.asBinder());
      }
    }
    
    /* Error */
    public boolean showSessionForActiveService(Bundle paramBundle, int paramInt, com.android.internal.app.IVoiceInteractionSessionShowCallback paramIVoiceInteractionSessionShowCallback, IBinder paramIBinder)
    {
      // Byte code:
      //   0: aload_0
      //   1: ldc -84
      //   3: invokespecial 174	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:enforceCallingPermission	(Ljava/lang/String;)V
      //   6: aload_0
      //   7: monitorenter
      //   8: aload_0
      //   9: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   12: ifnonnull +16 -> 28
      //   15: ldc -109
      //   17: ldc_w 605
      //   20: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   23: pop
      //   24: aload_0
      //   25: monitorexit
      //   26: iconst_0
      //   27: ireturn
      //   28: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   31: lstore 5
      //   33: aload_0
      //   34: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   37: aload_1
      //   38: iload_2
      //   39: iconst_1
      //   40: ior
      //   41: iconst_2
      //   42: ior
      //   43: aload_3
      //   44: aload 4
      //   46: invokevirtual 602	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:showSessionLocked	(Landroid/os/Bundle;ILcom/android/internal/app/IVoiceInteractionSessionShowCallback;Landroid/os/IBinder;)Z
      //   49: istore 7
      //   51: lload 5
      //   53: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   56: aload_0
      //   57: monitorexit
      //   58: iload 7
      //   60: ireturn
      //   61: astore_1
      //   62: lload 5
      //   64: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   67: aload_1
      //   68: athrow
      //   69: astore_1
      //   70: aload_0
      //   71: monitorexit
      //   72: aload_1
      //   73: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	74	0	this	VoiceInteractionManagerServiceStub
      //   0	74	1	paramBundle	Bundle
      //   0	74	2	paramInt	int
      //   0	74	3	paramIVoiceInteractionSessionShowCallback	com.android.internal.app.IVoiceInteractionSessionShowCallback
      //   0	74	4	paramIBinder	IBinder
      //   31	32	5	l	long
      //   49	10	7	bool	boolean
      // Exception table:
      //   from	to	target	type
      //   33	51	61	finally
      //   8	24	69	finally
      //   28	33	69	finally
      //   51	56	69	finally
      //   62	69	69	finally
    }
    
    /* Error */
    public boolean showSessionFromSession(IBinder paramIBinder, Bundle paramBundle, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   6: ifnonnull +16 -> 22
      //   9: ldc -109
      //   11: ldc_w 609
      //   14: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   17: pop
      //   18: aload_0
      //   19: monitorexit
      //   20: iconst_0
      //   21: ireturn
      //   22: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   25: lstore 4
      //   27: aload_0
      //   28: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   31: aload_2
      //   32: iload_3
      //   33: aconst_null
      //   34: aconst_null
      //   35: invokevirtual 602	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:showSessionLocked	(Landroid/os/Bundle;ILcom/android/internal/app/IVoiceInteractionSessionShowCallback;Landroid/os/IBinder;)Z
      //   38: istore 6
      //   40: lload 4
      //   42: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   45: aload_0
      //   46: monitorexit
      //   47: iload 6
      //   49: ireturn
      //   50: astore_1
      //   51: lload 4
      //   53: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   56: aload_1
      //   57: athrow
      //   58: astore_1
      //   59: aload_0
      //   60: monitorexit
      //   61: aload_1
      //   62: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	63	0	this	VoiceInteractionManagerServiceStub
      //   0	63	1	paramIBinder	IBinder
      //   0	63	2	paramBundle	Bundle
      //   0	63	3	paramInt	int
      //   25	27	4	l	long
      //   38	10	6	bool	boolean
      // Exception table:
      //   from	to	target	type
      //   27	40	50	finally
      //   2	18	58	finally
      //   22	27	58	finally
      //   40	45	58	finally
      //   51	58	58	finally
    }
    
    void startLocalVoiceInteraction(final IBinder paramIBinder, Bundle paramBundle)
    {
      if (this.mImpl == null) {
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        this.mImpl.showSessionLocked(paramBundle, 16, new IVoiceInteractionSessionShowCallback.Stub()
        {
          public void onFailed() {}
          
          public void onShown()
          {
            VoiceInteractionManagerService.this.mAmInternal.onLocalVoiceInteractionStarted(paramIBinder, VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.mImpl.mActiveSession.mSession, VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.mImpl.mActiveSession.mInteractor);
          }
        }, paramIBinder);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    /* Error */
    public int startRecognition(IVoiceInteractionService paramIVoiceInteractionService, int paramInt, String paramString, IRecognitionStatusCallback paramIRecognitionStatusCallback, android.hardware.soundtrigger.SoundTrigger.RecognitionConfig paramRecognitionConfig)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   6: ifnull +13 -> 19
      //   9: aload_0
      //   10: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   13: getfield 230	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mService	Landroid/service/voice/IVoiceInteractionService;
      //   16: ifnonnull +19 -> 35
      //   19: new 66	java/lang/SecurityException
      //   22: dup
      //   23: ldc_w 425
      //   26: invokespecial 81	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
      //   29: athrow
      //   30: astore_1
      //   31: aload_0
      //   32: monitorexit
      //   33: aload_1
      //   34: athrow
      //   35: aload_1
      //   36: ifnull -17 -> 19
      //   39: aload_1
      //   40: invokeinterface 431 1 0
      //   45: aload_0
      //   46: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   49: getfield 230	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mService	Landroid/service/voice/IVoiceInteractionService;
      //   52: invokeinterface 431 1 0
      //   57: if_acmpne -38 -> 19
      //   60: aload 4
      //   62: ifnull +8 -> 70
      //   65: aload 5
      //   67: ifnonnull +14 -> 81
      //   70: new 207	java/lang/IllegalArgumentException
      //   73: dup
      //   74: ldc_w 618
      //   77: invokespecial 210	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
      //   80: athrow
      //   81: aload_3
      //   82: ifnull -12 -> 70
      //   85: aload_0
      //   86: monitorexit
      //   87: invokestatic 215	android/os/UserHandle:getCallingUserId	()I
      //   90: istore 6
      //   92: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   95: lstore 7
      //   97: aload_0
      //   98: getfield 36	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:this$0	Lcom/android/server/voiceinteraction/VoiceInteractionManagerService;
      //   101: getfield 221	com/android/server/voiceinteraction/VoiceInteractionManagerService:mDbHelper	Lcom/android/server/voiceinteraction/DatabaseHelper;
      //   104: iload_2
      //   105: iload 6
      //   107: aload_3
      //   108: invokevirtual 442	com/android/server/voiceinteraction/DatabaseHelper:getKeyphraseSoundModel	(IILjava/lang/String;)Landroid/hardware/soundtrigger/SoundTrigger$KeyphraseSoundModel;
      //   111: astore_1
      //   112: aload_1
      //   113: ifnull +10 -> 123
      //   116: aload_1
      //   117: getfield 624	android/hardware/soundtrigger/SoundTrigger$KeyphraseSoundModel:uuid	Ljava/util/UUID;
      //   120: ifnonnull +20 -> 140
      //   123: ldc -109
      //   125: ldc_w 626
      //   128: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   131: pop
      //   132: lload 7
      //   134: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   137: ldc -14
      //   139: ireturn
      //   140: aload_1
      //   141: getfield 630	android/hardware/soundtrigger/SoundTrigger$KeyphraseSoundModel:keyphrases	[Landroid/hardware/soundtrigger/SoundTrigger$Keyphrase;
      //   144: ifnull -21 -> 123
      //   147: aload_0
      //   148: monitorenter
      //   149: aload_0
      //   150: getfield 36	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:this$0	Lcom/android/server/voiceinteraction/VoiceInteractionManagerService;
      //   153: getfield 108	com/android/server/voiceinteraction/VoiceInteractionManagerService:mLoadedKeyphraseIds	Ljava/util/TreeSet;
      //   156: iload_2
      //   157: invokestatic 237	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   160: invokevirtual 633	java/util/TreeSet:add	(Ljava/lang/Object;)Z
      //   163: pop
      //   164: aload_0
      //   165: monitorexit
      //   166: aload_0
      //   167: getfield 36	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:this$0	Lcom/android/server/voiceinteraction/VoiceInteractionManagerService;
      //   170: getfield 139	com/android/server/voiceinteraction/VoiceInteractionManagerService:mSoundTriggerInternal	Lcom/android/server/soundtrigger/SoundTriggerInternal;
      //   173: iload_2
      //   174: aload_1
      //   175: aload 4
      //   177: aload 5
      //   179: invokevirtual 636	com/android/server/soundtrigger/SoundTriggerInternal:startRecognition	(ILandroid/hardware/soundtrigger/SoundTrigger$KeyphraseSoundModel;Landroid/hardware/soundtrigger/IRecognitionStatusCallback;Landroid/hardware/soundtrigger/SoundTrigger$RecognitionConfig;)I
      //   182: istore_2
      //   183: lload 7
      //   185: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   188: iload_2
      //   189: ireturn
      //   190: astore_1
      //   191: aload_0
      //   192: monitorexit
      //   193: aload_1
      //   194: athrow
      //   195: astore_1
      //   196: lload 7
      //   198: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   201: aload_1
      //   202: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	203	0	this	VoiceInteractionManagerServiceStub
      //   0	203	1	paramIVoiceInteractionService	IVoiceInteractionService
      //   0	203	2	paramInt	int
      //   0	203	3	paramString	String
      //   0	203	4	paramIRecognitionStatusCallback	IRecognitionStatusCallback
      //   0	203	5	paramRecognitionConfig	android.hardware.soundtrigger.SoundTrigger.RecognitionConfig
      //   90	16	6	i	int
      //   95	102	7	l	long
      // Exception table:
      //   from	to	target	type
      //   2	19	30	finally
      //   19	30	30	finally
      //   39	60	30	finally
      //   70	81	30	finally
      //   149	164	190	finally
      //   97	112	195	finally
      //   116	123	195	finally
      //   123	132	195	finally
      //   140	149	195	finally
      //   164	183	195	finally
      //   191	195	195	finally
    }
    
    /* Error */
    public int startVoiceActivity(IBinder paramIBinder, Intent paramIntent, String paramString)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   6: ifnonnull +17 -> 23
      //   9: ldc -109
      //   11: ldc_w 640
      //   14: invokestatic 160	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   17: pop
      //   18: aload_0
      //   19: monitorexit
      //   20: bipush -6
      //   22: ireturn
      //   23: invokestatic 258	android/os/Binder:getCallingPid	()I
      //   26: istore 4
      //   28: invokestatic 263	android/os/Binder:getCallingUid	()I
      //   31: istore 5
      //   33: invokestatic 135	android/os/Binder:clearCallingIdentity	()J
      //   36: lstore 6
      //   38: aload_0
      //   39: getfield 176	com/android/server/voiceinteraction/VoiceInteractionManagerService$VoiceInteractionManagerServiceStub:mImpl	Lcom/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl;
      //   42: iload 4
      //   44: iload 5
      //   46: aload_1
      //   47: aload_2
      //   48: aload_3
      //   49: invokevirtual 644	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:startVoiceActivityLocked	(IILandroid/os/IBinder;Landroid/content/Intent;Ljava/lang/String;)I
      //   52: istore 4
      //   54: lload 6
      //   56: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   59: aload_0
      //   60: monitorexit
      //   61: iload 4
      //   63: ireturn
      //   64: astore_1
      //   65: lload 6
      //   67: invokestatic 164	android/os/Binder:restoreCallingIdentity	(J)V
      //   70: aload_1
      //   71: athrow
      //   72: astore_1
      //   73: aload_0
      //   74: monitorexit
      //   75: aload_1
      //   76: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	77	0	this	VoiceInteractionManagerServiceStub
      //   0	77	1	paramIBinder	IBinder
      //   0	77	2	paramIntent	Intent
      //   0	77	3	paramString	String
      //   26	36	4	i	int
      //   31	14	5	j	int
      //   36	30	6	l	long
      // Exception table:
      //   from	to	target	type
      //   38	54	64	finally
      //   2	18	72	finally
      //   23	38	72	finally
      //   54	59	72	finally
      //   65	72	72	finally
    }
    
    public void stopLocalVoiceInteraction(IBinder paramIBinder)
    {
      if (this.mImpl == null) {
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        this.mImpl.finishLocked(paramIBinder, true);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public int stopRecognition(IVoiceInteractionService paramIVoiceInteractionService, int paramInt, IRecognitionStatusCallback paramIRecognitionStatusCallback)
    {
      long l;
      try
      {
        if ((this.mImpl == null) || (this.mImpl.mService == null)) {
          throw new SecurityException("Caller is not the current voice interaction service");
        }
      }
      finally
      {
        IBinder localIBinder;
        do
        {
          do
          {
            throw paramIVoiceInteractionService;
          } while (paramIVoiceInteractionService == null);
          paramIVoiceInteractionService = paramIVoiceInteractionService.asBinder();
          localIBinder = this.mImpl.mService.asBinder();
        } while (paramIVoiceInteractionService != localIBinder);
      }
    }
    
    public boolean supportsLocalVoiceInteraction()
    {
      if (this.mImpl == null) {
        return false;
      }
      return this.mImpl.supportsLocalVoiceInteraction();
    }
    
    void switchImplementationIfNeeded(boolean paramBoolean)
    {
      try
      {
        switchImplementationIfNeededLocked(paramBoolean);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    void switchImplementationIfNeededLocked(boolean paramBoolean)
    {
      String str;
      Object localObject1;
      Object localObject3;
      if (!this.mSafeMode)
      {
        str = Settings.Secure.getStringForUser(VoiceInteractionManagerService.this.mResolver, "voice_interaction_service", this.mCurUser);
        Object localObject4 = null;
        Object localObject5 = null;
        localObject1 = localObject4;
        localObject3 = localObject5;
        if (str != null)
        {
          if (!str.isEmpty()) {
            break label129;
          }
          localObject3 = localObject5;
          localObject1 = localObject4;
        }
        if ((!paramBoolean) && (this.mImpl != null)) {
          break label189;
        }
      }
      label129:
      Object localObject2;
      label189:
      while ((this.mImpl.mUser != this.mCurUser) || (!this.mImpl.mComponent.equals(localObject2)))
      {
        unloadAllKeyphraseModels();
        if (this.mImpl != null) {
          this.mImpl.shutdownLocked();
        }
        if ((localObject1 == null) || (localObject3 == null)) {
          break label218;
        }
        this.mImpl = new VoiceInteractionManagerServiceImpl(VoiceInteractionManagerService.this.mContext, UiThread.getHandler(), this, this.mCurUser, (ComponentName)localObject1);
        this.mImpl.startLocked();
        return;
        try
        {
          localObject1 = ComponentName.unflattenFromString(str);
          localObject3 = AppGlobals.getPackageManager().getServiceInfo((ComponentName)localObject1, 0, this.mCurUser);
        }
        catch (RuntimeException|RemoteException localRuntimeException)
        {
          Slog.wtf("VoiceInteractionManagerService", "Bad voice interaction service name " + str, localRuntimeException);
          localObject2 = null;
          localObject3 = null;
        }
        break;
      }
      return;
      label218:
      this.mImpl = null;
    }
    
    public void switchUser(int paramInt)
    {
      try
      {
        this.mCurUser = paramInt;
        switchImplementationIfNeededLocked(false);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void systemRunning(boolean paramBoolean)
    {
      this.mSafeMode = paramBoolean;
      this.mPackageMonitor.register(VoiceInteractionManagerService.this.mContext, BackgroundThread.getHandler().getLooper(), UserHandle.ALL, true);
      new SettingsObserver(UiThread.getHandler());
      try
      {
        this.mCurUser = ActivityManager.getCurrentUser();
        switchImplementationIfNeededLocked(false);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public int updateKeyphraseSoundModel(SoundTrigger.KeyphraseSoundModel paramKeyphraseSoundModel)
    {
      enforceCallingPermission("android.permission.MANAGE_VOICE_KEYPHRASES");
      if (paramKeyphraseSoundModel == null) {
        throw new IllegalArgumentException("Model must not be null");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        if (VoiceInteractionManagerService.this.mDbHelper.updateKeyphraseSoundModel(paramKeyphraseSoundModel)) {
          try
          {
            if ((this.mImpl != null) && (this.mImpl.mService != null)) {
              this.mImpl.notifySoundModelsChangedLocked();
            }
            return 0;
          }
          finally {}
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      return Integer.MIN_VALUE;
    }
    
    class SettingsObserver
      extends ContentObserver
    {
      SettingsObserver(Handler paramHandler)
      {
        super();
        VoiceInteractionManagerService.this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("voice_interaction_service"), false, this, -1);
      }
      
      public void onChange(boolean paramBoolean)
      {
        synchronized (VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this)
        {
          VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.switchImplementationIfNeededLocked(false);
          return;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/voiceinteraction/VoiceInteractionManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */