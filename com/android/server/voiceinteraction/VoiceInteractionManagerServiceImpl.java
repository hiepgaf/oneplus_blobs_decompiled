package com.android.server.voiceinteraction;

import android.app.ActivityManagerInternal;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.service.voice.IVoiceInteractionService;
import android.service.voice.IVoiceInteractionService.Stub;
import android.service.voice.IVoiceInteractionSession;
import android.service.voice.VoiceInteractionServiceInfo;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.app.IVoiceInteractor;
import com.android.server.LocalServices;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

class VoiceInteractionManagerServiceImpl
  implements VoiceInteractionSessionConnection.Callback
{
  static final String CLOSE_REASON_VOICE_INTERACTION = "voiceinteraction";
  static final String TAG = "VoiceInteractionServiceManager";
  VoiceInteractionSessionConnection mActiveSession;
  final IActivityManager mAm;
  boolean mBound = false;
  final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(paramAnonymousIntent.getAction()))
      {
        ??? = paramAnonymousIntent.getStringExtra("reason");
        if ((!"voiceinteraction".equals(???)) && (!"dream".equals(???))) {}
      }
      else
      {
        return;
      }
      synchronized (VoiceInteractionManagerServiceImpl.this.mServiceStub)
      {
        if (VoiceInteractionManagerServiceImpl.this.mActiveSession != null)
        {
          paramAnonymousIntent = VoiceInteractionManagerServiceImpl.this.mActiveSession.mSession;
          if (paramAnonymousIntent == null) {}
        }
      }
      try
      {
        VoiceInteractionManagerServiceImpl.this.mActiveSession.mSession.closeSystemDialogs();
        return;
        paramAnonymousIntent = finally;
        throw paramAnonymousIntent;
      }
      catch (RemoteException paramAnonymousIntent)
      {
        for (;;) {}
      }
    }
  };
  final ComponentName mComponent;
  final ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName arg1, IBinder paramAnonymousIBinder)
    {
      synchronized (VoiceInteractionManagerServiceImpl.this.mServiceStub)
      {
        VoiceInteractionManagerServiceImpl.this.mService = IVoiceInteractionService.Stub.asInterface(paramAnonymousIBinder);
      }
      try
      {
        VoiceInteractionManagerServiceImpl.this.mService.ready();
        return;
        paramAnonymousIBinder = finally;
        throw paramAnonymousIBinder;
      }
      catch (RemoteException paramAnonymousIBinder)
      {
        for (;;) {}
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      VoiceInteractionManagerServiceImpl.this.mService = null;
    }
  };
  final Context mContext;
  int mDisabledShowContext;
  final Handler mHandler;
  final IWindowManager mIWindowManager;
  final VoiceInteractionServiceInfo mInfo;
  IVoiceInteractionService mService;
  final VoiceInteractionManagerService.VoiceInteractionManagerServiceStub mServiceStub;
  final ComponentName mSessionComponentName;
  final int mUser;
  final boolean mValid;
  
  VoiceInteractionManagerServiceImpl(Context paramContext, Handler paramHandler, VoiceInteractionManagerService.VoiceInteractionManagerServiceStub paramVoiceInteractionManagerServiceStub, int paramInt, ComponentName paramComponentName)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.mServiceStub = paramVoiceInteractionManagerServiceStub;
    this.mUser = paramInt;
    this.mComponent = paramComponentName;
    this.mAm = ActivityManagerNative.getDefault();
    try
    {
      paramContext = new VoiceInteractionServiceInfo(paramContext.getPackageManager(), paramComponentName, this.mUser);
      this.mInfo = paramContext;
      if (this.mInfo.getParseError() != null)
      {
        Slog.w("VoiceInteractionServiceManager", "Bad voice interaction service: " + this.mInfo.getParseError());
        this.mSessionComponentName = null;
        this.mIWindowManager = null;
        this.mValid = false;
        return;
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Slog.w("VoiceInteractionServiceManager", "Voice interaction service not found: " + paramComponentName, paramContext);
      this.mInfo = null;
      this.mSessionComponentName = null;
      this.mIWindowManager = null;
      this.mValid = false;
      return;
    }
    this.mValid = true;
    this.mSessionComponentName = new ComponentName(paramComponentName.getPackageName(), this.mInfo.getSessionService());
    this.mIWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
    this.mContext.registerReceiver(this.mBroadcastReceiver, paramContext, null, paramHandler);
  }
  
  public void closeSystemDialogsLocked(IBinder paramIBinder)
  {
    try
    {
      if ((this.mActiveSession == null) || (paramIBinder != this.mActiveSession.mToken))
      {
        Slog.w("VoiceInteractionServiceManager", "closeSystemDialogs does not match active session");
        return;
      }
      this.mAm.closeSystemDialogs("voiceinteraction");
      return;
    }
    catch (RemoteException paramIBinder)
    {
      throw new IllegalStateException("Unexpected remote error", paramIBinder);
    }
  }
  
  public boolean deliverNewSessionLocked(IBinder paramIBinder, IVoiceInteractionSession paramIVoiceInteractionSession, IVoiceInteractor paramIVoiceInteractor)
  {
    if ((this.mActiveSession == null) || (paramIBinder != this.mActiveSession.mToken))
    {
      Slog.w("VoiceInteractionServiceManager", "deliverNewSession does not match active session");
      return false;
    }
    this.mActiveSession.deliverNewSessionLocked(paramIVoiceInteractionSession, paramIVoiceInteractor);
    return true;
  }
  
  public void dumpLocked(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (!this.mValid)
    {
      paramPrintWriter.print("  NOT VALID: ");
      if (this.mInfo == null)
      {
        paramPrintWriter.println("no info");
        return;
      }
      paramPrintWriter.println(this.mInfo.getParseError());
      return;
    }
    paramPrintWriter.print("  mUser=");
    paramPrintWriter.println(this.mUser);
    paramPrintWriter.print("  mComponent=");
    paramPrintWriter.println(this.mComponent.flattenToShortString());
    paramPrintWriter.print("  Session service=");
    paramPrintWriter.println(this.mInfo.getSessionService());
    paramPrintWriter.println("  Service info:");
    this.mInfo.getServiceInfo().dump(new PrintWriterPrinter(paramPrintWriter), "    ");
    paramPrintWriter.print("  Recognition service=");
    paramPrintWriter.println(this.mInfo.getRecognitionService());
    paramPrintWriter.print("  Settings activity=");
    paramPrintWriter.println(this.mInfo.getSettingsActivity());
    paramPrintWriter.print("  Supports assist=");
    paramPrintWriter.println(this.mInfo.getSupportsAssist());
    paramPrintWriter.print("  Supports launch from keyguard=");
    paramPrintWriter.println(this.mInfo.getSupportsLaunchFromKeyguard());
    if (this.mDisabledShowContext != 0)
    {
      paramPrintWriter.print("  mDisabledShowContext=");
      paramPrintWriter.println(Integer.toHexString(this.mDisabledShowContext));
    }
    paramPrintWriter.print("  mBound=");
    paramPrintWriter.print(this.mBound);
    paramPrintWriter.print(" mService=");
    paramPrintWriter.println(this.mService);
    if (this.mActiveSession != null)
    {
      paramPrintWriter.println("  Active session:");
      this.mActiveSession.dump("    ", paramPrintWriter);
    }
  }
  
  public void finishLocked(IBinder paramIBinder, boolean paramBoolean)
  {
    if ((this.mActiveSession == null) || ((!paramBoolean) && (paramIBinder != this.mActiveSession.mToken)))
    {
      Slog.w("VoiceInteractionServiceManager", "finish does not match active session");
      return;
    }
    this.mActiveSession.cancelLocked(paramBoolean);
    this.mActiveSession = null;
  }
  
  public int getDisabledShowContextLocked(int paramInt)
  {
    int i = this.mInfo.getServiceInfo().applicationInfo.uid;
    if (paramInt != i) {
      throw new SecurityException("Calling uid " + paramInt + " does not match active uid " + i);
    }
    return this.mDisabledShowContext;
  }
  
  public int getUserDisabledShowContextLocked(int paramInt)
  {
    int i = this.mInfo.getServiceInfo().applicationInfo.uid;
    if (paramInt != i) {
      throw new SecurityException("Calling uid " + paramInt + " does not match active uid " + i);
    }
    if (this.mActiveSession != null) {
      return this.mActiveSession.getUserDisabledShowContextLocked();
    }
    return 0;
  }
  
  public boolean hideSessionLocked()
  {
    if (this.mActiveSession != null) {
      return this.mActiveSession.hideLocked();
    }
    return false;
  }
  
  public void launchVoiceAssistFromKeyguard()
  {
    if (this.mService == null)
    {
      Slog.w("VoiceInteractionServiceManager", "Not bound to voice interaction service " + this.mComponent);
      return;
    }
    try
    {
      this.mService.launchVoiceAssistFromKeyguard();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w("VoiceInteractionServiceManager", "RemoteException while calling launchVoiceAssistFromKeyguard", localRemoteException);
    }
  }
  
  void notifySoundModelsChangedLocked()
  {
    if (this.mService == null)
    {
      Slog.w("VoiceInteractionServiceManager", "Not bound to voice interaction service " + this.mComponent);
      return;
    }
    try
    {
      this.mService.soundModelsChanged();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w("VoiceInteractionServiceManager", "RemoteException while calling soundModelsChanged", localRemoteException);
    }
  }
  
  public void onSessionHidden(VoiceInteractionSessionConnection paramVoiceInteractionSessionConnection)
  {
    this.mServiceStub.onSessionHidden();
  }
  
  public void onSessionShown(VoiceInteractionSessionConnection paramVoiceInteractionSessionConnection)
  {
    this.mServiceStub.onSessionShown();
  }
  
  public void sessionConnectionGone(VoiceInteractionSessionConnection paramVoiceInteractionSessionConnection)
  {
    synchronized (this.mServiceStub)
    {
      finishLocked(paramVoiceInteractionSessionConnection.mToken, false);
      return;
    }
  }
  
  public void setDisabledShowContextLocked(int paramInt1, int paramInt2)
  {
    int i = this.mInfo.getServiceInfo().applicationInfo.uid;
    if (paramInt1 != i) {
      throw new SecurityException("Calling uid " + paramInt1 + " does not match active uid " + i);
    }
    this.mDisabledShowContext = paramInt2;
  }
  
  public void setKeepAwakeLocked(IBinder paramIBinder, boolean paramBoolean)
  {
    try
    {
      if ((this.mActiveSession == null) || (paramIBinder != this.mActiveSession.mToken))
      {
        Slog.w("VoiceInteractionServiceManager", "setKeepAwake does not match active session");
        return;
      }
      this.mAm.setVoiceKeepAwake(this.mActiveSession.mSession, paramBoolean);
      return;
    }
    catch (RemoteException paramIBinder)
    {
      throw new IllegalStateException("Unexpected remote error", paramIBinder);
    }
  }
  
  public boolean showSessionLocked(Bundle paramBundle, int paramInt, IVoiceInteractionSessionShowCallback paramIVoiceInteractionSessionShowCallback, IBinder paramIBinder)
  {
    if (this.mActiveSession == null) {
      this.mActiveSession = new VoiceInteractionSessionConnection(this.mServiceStub, this.mSessionComponentName, this.mUser, this.mContext, this, this.mInfo.getServiceInfo().applicationInfo.uid, this.mHandler);
    }
    List localList = null;
    if (paramIBinder == null) {
      localList = ((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class)).getTopVisibleActivities();
    }
    return this.mActiveSession.showLocked(paramBundle, paramInt, this.mDisabledShowContext, paramIVoiceInteractionSessionShowCallback, paramIBinder, localList);
  }
  
  void shutdownLocked()
  {
    if (this.mActiveSession != null)
    {
      this.mActiveSession.cancelLocked(false);
      this.mActiveSession = null;
    }
    try
    {
      if (this.mService != null) {
        this.mService.shutdown();
      }
      if (this.mBound)
      {
        this.mContext.unbindService(this.mConnection);
        this.mBound = false;
      }
      if (this.mValid) {
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w("VoiceInteractionServiceManager", "RemoteException in shutdown", localRemoteException);
      }
    }
  }
  
  void startLocked()
  {
    Intent localIntent = new Intent("android.service.voice.VoiceInteractionService");
    localIntent.setComponent(this.mComponent);
    this.mBound = this.mContext.bindServiceAsUser(localIntent, this.mConnection, 67108865, new UserHandle(this.mUser));
    if (!this.mBound) {
      Slog.w("VoiceInteractionServiceManager", "Failed binding to voice interaction service " + this.mComponent);
    }
  }
  
  /* Error */
  public int startVoiceActivityLocked(int paramInt1, int paramInt2, IBinder paramIBinder, Intent paramIntent, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 175	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mActiveSession	Lcom/android/server/voiceinteraction/VoiceInteractionSessionConnection;
    //   4: ifnull +14 -> 18
    //   7: aload_3
    //   8: aload_0
    //   9: getfield 175	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mActiveSession	Lcom/android/server/voiceinteraction/VoiceInteractionSessionConnection;
    //   12: getfield 181	com/android/server/voiceinteraction/VoiceInteractionSessionConnection:mToken	Landroid/os/IBinder;
    //   15: if_acmpeq +15 -> 30
    //   18: ldc 17
    //   20: ldc_w 433
    //   23: invokestatic 116	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   26: pop
    //   27: bipush -9
    //   29: ireturn
    //   30: aload_0
    //   31: getfield 175	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mActiveSession	Lcom/android/server/voiceinteraction/VoiceInteractionSessionConnection;
    //   34: getfield 436	com/android/server/voiceinteraction/VoiceInteractionSessionConnection:mShown	Z
    //   37: ifne +15 -> 52
    //   40: ldc 17
    //   42: ldc_w 438
    //   45: invokestatic 116	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   48: pop
    //   49: bipush -10
    //   51: ireturn
    //   52: new 411	android/content/Intent
    //   55: dup
    //   56: aload 4
    //   58: invokespecial 441	android/content/Intent:<init>	(Landroid/content/Intent;)V
    //   61: astore_3
    //   62: aload_3
    //   63: ldc_w 443
    //   66: invokevirtual 447	android/content/Intent:addCategory	(Ljava/lang/String;)Landroid/content/Intent;
    //   69: pop
    //   70: aload_3
    //   71: ldc_w 448
    //   74: invokevirtual 452	android/content/Intent:addFlags	(I)Landroid/content/Intent;
    //   77: pop
    //   78: aload_0
    //   79: getfield 81	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mAm	Landroid/app/IActivityManager;
    //   82: aload_0
    //   83: getfield 73	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mComponent	Landroid/content/ComponentName;
    //   86: invokevirtual 135	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   89: iload_1
    //   90: iload_2
    //   91: aload_3
    //   92: aload 5
    //   94: aload_0
    //   95: getfield 175	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mActiveSession	Lcom/android/server/voiceinteraction/VoiceInteractionSessionConnection;
    //   98: getfield 370	com/android/server/voiceinteraction/VoiceInteractionSessionConnection:mSession	Landroid/service/voice/IVoiceInteractionSession;
    //   101: aload_0
    //   102: getfield 175	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mActiveSession	Lcom/android/server/voiceinteraction/VoiceInteractionSessionConnection;
    //   105: getfield 456	com/android/server/voiceinteraction/VoiceInteractionSessionConnection:mInteractor	Lcom/android/internal/app/IVoiceInteractor;
    //   108: iconst_0
    //   109: aconst_null
    //   110: aconst_null
    //   111: aload_0
    //   112: getfield 71	com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl:mUser	I
    //   115: invokeinterface 460 12 0
    //   120: istore_1
    //   121: iload_1
    //   122: ireturn
    //   123: astore_3
    //   124: new 190	java/lang/IllegalStateException
    //   127: dup
    //   128: ldc -64
    //   130: aload_3
    //   131: invokespecial 195	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   134: athrow
    //   135: astore_3
    //   136: goto -12 -> 124
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	139	0	this	VoiceInteractionManagerServiceImpl
    //   0	139	1	paramInt1	int
    //   0	139	2	paramInt2	int
    //   0	139	3	paramIBinder	IBinder
    //   0	139	4	paramIntent	Intent
    //   0	139	5	paramString	String
    // Exception table:
    //   from	to	target	type
    //   0	18	123	android/os/RemoteException
    //   18	27	123	android/os/RemoteException
    //   30	49	123	android/os/RemoteException
    //   52	62	123	android/os/RemoteException
    //   62	121	135	android/os/RemoteException
  }
  
  public boolean supportsLocalVoiceInteraction()
  {
    return this.mInfo.getSupportsLocalInteraction();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/voiceinteraction/VoiceInteractionManagerServiceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */