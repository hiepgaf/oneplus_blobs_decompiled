package com.android.server.media;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.PendingIntent.OnFinished;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioManagerInternal;
import android.media.AudioSystem;
import android.media.IAudioService;
import android.media.IAudioService.Stub;
import android.media.IRemoteVolumeController;
import android.media.session.IActiveSessionsListener;
import android.media.session.ISession;
import android.media.session.ISessionCallback;
import android.media.session.ISessionController;
import android.media.session.ISessionManager.Stub;
import android.media.session.MediaSession.Token;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.KeyEvent;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.Watchdog;
import com.android.server.Watchdog.Monitor;
import com.android.server.am.OnePlusProcessManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MediaSessionService
  extends SystemService
  implements Watchdog.Monitor
{
  private static final boolean DEBUG = Log.isLoggable("MediaSessionService", 3);
  private static final boolean DEBUG_MEDIA_KEY_EVENT = true;
  private static final String TAG = "MediaSessionService";
  private static final int WAKELOCK_TIMEOUT = 5000;
  private final ArrayList<MediaSessionRecord> mAllSessions = new ArrayList();
  private AudioManagerInternal mAudioManagerInternal;
  private IAudioService mAudioService;
  private ContentResolver mContentResolver;
  private final List<Integer> mCurrentUserIdList = new ArrayList();
  private final MessageHandler mHandler = new MessageHandler();
  final IBinder mICallback = new Binder();
  private KeyguardManager mKeyguardManager;
  private final Object mLock = new Object();
  private final PowerManager.WakeLock mMediaEventWakeLock;
  private final MediaSessionStack mPriorityStack = new MediaSessionStack();
  private IRemoteVolumeController mRvc;
  private final SessionManagerImpl mSessionManagerImpl = new SessionManagerImpl();
  private final ArrayList<SessionsListenerRecord> mSessionsListeners = new ArrayList();
  private SettingsObserver mSettingsObserver;
  private final SparseArray<UserRecord> mUserRecords = new SparseArray();
  
  static
  {
    if (!DEBUG) {}
  }
  
  public MediaSessionService(Context paramContext)
  {
    super(paramContext);
    this.mMediaEventWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "handleMediaEvent");
  }
  
  private MediaSessionRecord createSessionInternal(int paramInt1, int paramInt2, int paramInt3, String paramString1, ISessionCallback paramISessionCallback, String paramString2)
    throws RemoteException
  {
    synchronized (this.mLock)
    {
      paramString1 = createSessionLocked(paramInt1, paramInt2, paramInt3, paramString1, paramISessionCallback, paramString2);
      return paramString1;
    }
  }
  
  private MediaSessionRecord createSessionLocked(int paramInt1, int paramInt2, int paramInt3, String paramString1, ISessionCallback paramISessionCallback, String paramString2)
  {
    UserRecord localUserRecord = (UserRecord)this.mUserRecords.get(paramInt3);
    if (localUserRecord == null)
    {
      Log.wtf("MediaSessionService", "Request from invalid user: " + paramInt3);
      throw new RuntimeException("Session request from invalid user.");
    }
    MediaSessionRecord localMediaSessionRecord = new MediaSessionRecord(paramInt1, paramInt2, paramInt3, paramString1, paramISessionCallback, paramString2, this, this.mHandler);
    try
    {
      paramISessionCallback.asBinder().linkToDeath(localMediaSessionRecord, 0);
      this.mAllSessions.add(localMediaSessionRecord);
      this.mPriorityStack.addSession(localMediaSessionRecord, this.mCurrentUserIdList.contains(Integer.valueOf(paramInt3)));
      localUserRecord.addSessionLocked(localMediaSessionRecord);
      this.mHandler.post(1, paramInt3, 0);
      if (DEBUG) {
        Log.d("MediaSessionService", "Created session for " + paramString1 + " with tag " + paramString2);
      }
      return localMediaSessionRecord;
    }
    catch (RemoteException paramString1)
    {
      throw new RuntimeException("Media Session owner died prematurely.", paramString1);
    }
  }
  
  private void destroySessionLocked(MediaSessionRecord paramMediaSessionRecord)
  {
    if (DEBUG) {
      Log.d("MediaSessionService", "Destroying " + paramMediaSessionRecord);
    }
    int i = paramMediaSessionRecord.getUserId();
    UserRecord localUserRecord = (UserRecord)this.mUserRecords.get(i);
    if (localUserRecord != null) {
      localUserRecord.removeSessionLocked(paramMediaSessionRecord);
    }
    this.mPriorityStack.removeSession(paramMediaSessionRecord);
    this.mAllSessions.remove(paramMediaSessionRecord);
    try
    {
      paramMediaSessionRecord.getCallback().asBinder().unlinkToDeath(paramMediaSessionRecord, 0);
      paramMediaSessionRecord.onDestroy();
      this.mHandler.post(1, paramMediaSessionRecord.getUserId(), 0);
      return;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  private void destroyUserLocked(UserRecord paramUserRecord)
  {
    paramUserRecord.destroyLocked();
    this.mUserRecords.remove(UserRecord.-get2(paramUserRecord));
  }
  
  private void enforceMediaPermissions(ComponentName paramComponentName, int paramInt1, int paramInt2, int paramInt3)
  {
    if (isCurrentVolumeController(paramInt2)) {
      return;
    }
    if ((getContext().checkPermission("android.permission.MEDIA_CONTENT_CONTROL", paramInt1, paramInt2) == 0) || (isEnabledNotificationListener(paramComponentName, UserHandle.getUserId(paramInt2), paramInt3))) {
      return;
    }
    throw new SecurityException("Missing permission to control media.");
  }
  
  private void enforcePackageName(String paramString, int paramInt)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("packageName may not be empty");
    }
    String[] arrayOfString = getContext().getPackageManager().getPackagesForUid(paramInt);
    int i = arrayOfString.length;
    paramInt = 0;
    while (paramInt < i)
    {
      if (paramString.equals(arrayOfString[paramInt])) {
        return;
      }
      paramInt += 1;
    }
    throw new IllegalArgumentException("packageName is not owned by the calling process");
  }
  
  private void enforceSystemUiPermission(String paramString, int paramInt1, int paramInt2)
  {
    if (isCurrentVolumeController(paramInt2)) {
      return;
    }
    if (getContext().checkPermission("android.permission.STATUS_BAR_SERVICE", paramInt1, paramInt2) != 0) {
      throw new SecurityException("Only system ui may " + paramString);
    }
  }
  
  private int findIndexOfSessionsListenerLocked(IActiveSessionsListener paramIActiveSessionsListener)
  {
    int i = this.mSessionsListeners.size() - 1;
    while (i >= 0)
    {
      if (SessionsListenerRecord.-get1((SessionsListenerRecord)this.mSessionsListeners.get(i)).asBinder() == paramIActiveSessionsListener.asBinder()) {
        return i;
      }
      i -= 1;
    }
    return -1;
  }
  
  private IAudioService getAudioService()
  {
    return IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
  }
  
  private boolean isCurrentVolumeController(int paramInt)
  {
    if (this.mAudioManagerInternal != null)
    {
      int i = this.mAudioManagerInternal.getVolumeControllerUid();
      if ((i > 0) && (paramInt == i)) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isEnabledNotificationListener(ComponentName paramComponentName, int paramInt1, int paramInt2)
  {
    if (paramInt1 != paramInt2) {
      return false;
    }
    if (DEBUG) {
      Log.d("MediaSessionService", "Checking if enabled notification listener " + paramComponentName);
    }
    if (paramComponentName != null)
    {
      Object localObject = Settings.Secure.getStringForUser(this.mContentResolver, "enabled_notification_listeners", paramInt1);
      if (localObject != null)
      {
        localObject = ((String)localObject).split(":");
        paramInt2 = 0;
        while (paramInt2 < localObject.length)
        {
          ComponentName localComponentName = ComponentName.unflattenFromString(localObject[paramInt2]);
          if ((localComponentName != null) && (paramComponentName.equals(localComponentName)))
          {
            if (DEBUG) {
              Log.d("MediaSessionService", "ok to get sessions. " + localComponentName + " is authorized notification listener");
            }
            return true;
          }
          paramInt2 += 1;
        }
      }
      if (DEBUG) {
        Log.d("MediaSessionService", "not ok to get sessions. " + paramComponentName + " is not in list of ENABLED_NOTIFICATION_LISTENERS for user " + paramInt1);
      }
    }
    return false;
  }
  
  private void pushRemoteVolumeUpdateLocked(int paramInt)
  {
    ISessionController localISessionController = null;
    if (this.mRvc != null) {
      try
      {
        MediaSessionRecord localMediaSessionRecord = this.mPriorityStack.getDefaultRemoteSession(paramInt);
        IRemoteVolumeController localIRemoteVolumeController = this.mRvc;
        if (localMediaSessionRecord == null) {}
        for (;;)
        {
          localIRemoteVolumeController.updateRemoteController(localISessionController);
          return;
          localISessionController = localMediaSessionRecord.getControllerBinder();
        }
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("MediaSessionService", "Error sending default remote volume to sys ui.", localRemoteException);
      }
    }
  }
  
  private void pushSessionsChanged(int paramInt)
  {
    synchronized (this.mLock)
    {
      Object localObject3 = this.mPriorityStack.getActiveSessions(paramInt);
      int j = ((List)localObject3).size();
      if ((j > 0) && (((MediaSessionRecord)((List)localObject3).get(0)).isPlaybackActive(false))) {
        rememberMediaButtonReceiverLocked((MediaSessionRecord)((List)localObject3).get(0));
      }
      ArrayList localArrayList = new ArrayList();
      int i = 0;
      while (i < j)
      {
        localArrayList.add(new MediaSession.Token(((MediaSessionRecord)((List)localObject3).get(i)).getControllerBinder()));
        i += 1;
      }
      pushRemoteVolumeUpdateLocked(paramInt);
      i = this.mSessionsListeners.size() - 1;
      for (;;)
      {
        if (i >= 0)
        {
          localObject3 = (SessionsListenerRecord)this.mSessionsListeners.get(i);
          if (SessionsListenerRecord.-get4((SessionsListenerRecord)localObject3) != -1)
          {
            j = SessionsListenerRecord.-get4((SessionsListenerRecord)localObject3);
            if (j != paramInt) {
              break label178;
            }
          }
          try
          {
            SessionsListenerRecord.-get1((SessionsListenerRecord)localObject3).onActiveSessionsChanged(localArrayList);
            label178:
            i -= 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Log.w("MediaSessionService", "Dead ActiveSessionsListener in pushSessionsChanged, removing", localRemoteException);
              this.mSessionsListeners.remove(i);
            }
          }
        }
      }
    }
  }
  
  private void rememberMediaButtonReceiverLocked(MediaSessionRecord paramMediaSessionRecord)
  {
    Object localObject = paramMediaSessionRecord.getMediaButtonReceiver();
    UserRecord localUserRecord = (UserRecord)this.mUserRecords.get(paramMediaSessionRecord.getUserId());
    if ((localObject != null) && (localUserRecord != null))
    {
      UserRecord.-set0(localUserRecord, (PendingIntent)localObject);
      localObject = ((PendingIntent)localObject).getIntent().getComponent();
      if ((localObject != null) && (paramMediaSessionRecord.getPackageName().equals(((ComponentName)localObject).getPackageName()))) {
        Settings.Secure.putStringForUser(this.mContentResolver, "media_button_receiver", ((ComponentName)localObject).flattenToString(), paramMediaSessionRecord.getUserId());
      }
    }
  }
  
  private void updateActiveSessionListeners()
  {
    synchronized (this.mLock)
    {
      int i = this.mSessionsListeners.size() - 1;
      for (;;)
      {
        if (i >= 0)
        {
          SessionsListenerRecord localSessionsListenerRecord = (SessionsListenerRecord)this.mSessionsListeners.get(i);
          try
          {
            enforceMediaPermissions(SessionsListenerRecord.-get0(localSessionsListenerRecord), SessionsListenerRecord.-get2(localSessionsListenerRecord), SessionsListenerRecord.-get3(localSessionsListenerRecord), SessionsListenerRecord.-get4(localSessionsListenerRecord));
            i -= 1;
          }
          catch (SecurityException localSecurityException)
          {
            for (;;)
            {
              Log.i("MediaSessionService", "ActiveSessionsListener " + SessionsListenerRecord.-get0(localSessionsListenerRecord) + " is no longer authorized. Disconnecting.");
              this.mSessionsListeners.remove(i);
              try
              {
                SessionsListenerRecord.-get1(localSessionsListenerRecord).onActiveSessionsChanged(new ArrayList());
              }
              catch (Exception localException) {}
            }
          }
        }
      }
      return;
    }
  }
  
  private void updateUser()
  {
    int i = 0;
    synchronized (this.mLock)
    {
      Object localObject2 = (UserManager)getContext().getSystemService("user");
      int j = ActivityManager.getCurrentUser();
      localObject2 = ((UserManager)localObject2).getProfileIdsWithDisabled(j);
      this.mCurrentUserIdList.clear();
      if ((localObject2 != null) && (localObject2.length > 0)) {
        j = localObject2.length;
      }
      while (i < j)
      {
        int k = localObject2[i];
        this.mCurrentUserIdList.add(Integer.valueOf(k));
        i += 1;
        continue;
        Log.w("MediaSessionService", "Failed to get enabled profiles.");
        this.mCurrentUserIdList.add(Integer.valueOf(j));
      }
      localObject2 = this.mCurrentUserIdList.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        i = ((Integer)((Iterator)localObject2).next()).intValue();
        if (this.mUserRecords.get(i) == null) {
          this.mUserRecords.put(i, new UserRecord(getContext(), i));
        }
      }
    }
  }
  
  void destroySession(MediaSessionRecord paramMediaSessionRecord)
  {
    synchronized (this.mLock)
    {
      destroySessionLocked(paramMediaSessionRecord);
      return;
    }
  }
  
  protected void enforcePhoneStatePermission(int paramInt1, int paramInt2)
  {
    if (getContext().checkPermission("android.permission.MODIFY_PHONE_STATE", paramInt1, paramInt2) != 0) {
      throw new SecurityException("Must hold the MODIFY_PHONE_STATE permission.");
    }
  }
  
  public void monitor()
  {
    Object localObject = this.mLock;
  }
  
  public void notifyRemoteVolumeChanged(int paramInt, MediaSessionRecord paramMediaSessionRecord)
  {
    if (this.mRvc == null) {
      return;
    }
    try
    {
      this.mRvc.remoteVolumeChanged(paramMediaSessionRecord.getControllerBinder(), paramInt);
      return;
    }
    catch (Exception paramMediaSessionRecord)
    {
      Log.wtf("MediaSessionService", "Error sending volume change to system UI.", paramMediaSessionRecord);
    }
  }
  
  public void onSessionPlaybackTypeChanged(MediaSessionRecord paramMediaSessionRecord)
  {
    synchronized (this.mLock)
    {
      if (!this.mAllSessions.contains(paramMediaSessionRecord))
      {
        Log.d("MediaSessionService", "Unknown session changed playback type. Ignoring.");
        return;
      }
      pushRemoteVolumeUpdateLocked(paramMediaSessionRecord.getUserId());
      return;
    }
  }
  
  public void onSessionPlaystateChange(MediaSessionRecord paramMediaSessionRecord, int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      if (!this.mAllSessions.contains(paramMediaSessionRecord))
      {
        Log.d("MediaSessionService", "Unknown session changed playback state. Ignoring.");
        return;
      }
      boolean bool = this.mPriorityStack.onPlaystateChange(paramMediaSessionRecord, paramInt1, paramInt2);
      if (bool) {
        this.mHandler.post(1, paramMediaSessionRecord.getUserId(), 0);
      }
      return;
    }
  }
  
  public void onStart()
  {
    publishBinderService("media_session", this.mSessionManagerImpl);
    Watchdog.getInstance().addMonitor(this);
    this.mKeyguardManager = ((KeyguardManager)getContext().getSystemService("keyguard"));
    this.mAudioService = getAudioService();
    this.mAudioManagerInternal = ((AudioManagerInternal)LocalServices.getService(AudioManagerInternal.class));
    this.mContentResolver = getContext().getContentResolver();
    this.mSettingsObserver = new SettingsObserver(null);
    SettingsObserver.-wrap0(this.mSettingsObserver);
    updateUser();
  }
  
  public void onStartUser(int paramInt)
  {
    if (DEBUG) {
      Log.d("MediaSessionService", "onStartUser: " + paramInt);
    }
    updateUser();
  }
  
  public void onStopUser(int paramInt)
  {
    if (DEBUG) {
      Log.d("MediaSessionService", "onStopUser: " + paramInt);
    }
    synchronized (this.mLock)
    {
      UserRecord localUserRecord = (UserRecord)this.mUserRecords.get(paramInt);
      if (localUserRecord != null) {
        destroyUserLocked(localUserRecord);
      }
      updateUser();
      return;
    }
  }
  
  public void onSwitchUser(int paramInt)
  {
    if (DEBUG) {
      Log.d("MediaSessionService", "onSwitchUser: " + paramInt);
    }
    updateUser();
  }
  
  void sessionDied(MediaSessionRecord paramMediaSessionRecord)
  {
    synchronized (this.mLock)
    {
      destroySessionLocked(paramMediaSessionRecord);
      return;
    }
  }
  
  public void updateSession(MediaSessionRecord paramMediaSessionRecord)
  {
    synchronized (this.mLock)
    {
      if (!this.mAllSessions.contains(paramMediaSessionRecord))
      {
        Log.d("MediaSessionService", "Unknown session updated. Ignoring.");
        return;
      }
      this.mPriorityStack.onSessionStateChange(paramMediaSessionRecord);
      this.mHandler.post(1, paramMediaSessionRecord.getUserId(), 0);
      return;
    }
  }
  
  final class MessageHandler
    extends Handler
  {
    private static final int MSG_SESSIONS_CHANGED = 1;
    
    MessageHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      MediaSessionService.-wrap6(MediaSessionService.this, paramMessage.arg1);
    }
    
    public void post(int paramInt1, int paramInt2, int paramInt3)
    {
      obtainMessage(paramInt1, paramInt2, paramInt3).sendToTarget();
    }
  }
  
  class SessionManagerImpl
    extends ISessionManager.Stub
  {
    private static final String EXTRA_WAKELOCK_ACQUIRED = "android.media.AudioService.WAKELOCK_ACQUIRED";
    private static final int WAKELOCK_RELEASE_ON_FINISHED = 1980;
    BroadcastReceiver mKeyEventDone = new BroadcastReceiver()
    {
      public void onReceive(Context arg1, Intent paramAnonymousIntent)
      {
        if (paramAnonymousIntent == null) {
          return;
        }
        paramAnonymousIntent = paramAnonymousIntent.getExtras();
        if (paramAnonymousIntent == null) {
          return;
        }
        synchronized (MediaSessionService.-get8(MediaSessionService.this))
        {
          if ((paramAnonymousIntent.containsKey("android.media.AudioService.WAKELOCK_ACQUIRED")) && (MediaSessionService.-get9(MediaSessionService.this).isHeld())) {
            MediaSessionService.-get9(MediaSessionService.this).release();
          }
          return;
        }
      }
    };
    private KeyEventWakeLockReceiver mKeyEventReceiver = new KeyEventWakeLockReceiver(MediaSessionService.-get6(MediaSessionService.this));
    private boolean mVoiceButtonDown = false;
    private boolean mVoiceButtonHandled = false;
    
    SessionManagerImpl() {}
    
    private void dispatchAdjustVolumeLocked(int paramInt1, int paramInt2, int paramInt3, MediaSessionRecord paramMediaSessionRecord)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (isValidLocalStreamType(paramInt1))
      {
        bool1 = bool2;
        if (AudioSystem.isStreamActive(paramInt1, 0)) {
          bool1 = true;
        }
      }
      if (MediaSessionService.-get0()) {
        Log.d("MediaSessionService", "Adjusting " + paramMediaSessionRecord + " by " + paramInt2 + ". flags=" + paramInt3 + ", suggestedStream=" + paramInt1 + ", preferSuggestedStream=" + bool1);
      }
      if ((paramMediaSessionRecord == null) || (bool1))
      {
        if (((paramInt3 & 0x200) == 0) || (AudioSystem.isStreamActive(3, 0))) {}
        try
        {
          paramMediaSessionRecord = MediaSessionService.this.getContext().getOpPackageName();
          MediaSessionService.-get3(MediaSessionService.this).adjustSuggestedStreamVolume(paramInt2, paramInt1, paramInt3, paramMediaSessionRecord, "MediaSessionService");
          return;
        }
        catch (RemoteException paramMediaSessionRecord)
        {
          Log.e("MediaSessionService", "Error adjusting default volume.", paramMediaSessionRecord);
          return;
        }
        if (MediaSessionService.-get0()) {
          Log.d("MediaSessionService", "No active session to adjust, skipping media only volume event");
        }
        return;
      }
      paramMediaSessionRecord.adjustVolume(paramInt2, paramInt3, MediaSessionService.this.getContext().getPackageName(), 1000, true);
    }
    
    private void dispatchMediaKeyEventLocked(KeyEvent paramKeyEvent, boolean paramBoolean, MediaSessionRecord paramMediaSessionRecord)
    {
      int i;
      if (paramMediaSessionRecord != null)
      {
        if (MediaSessionService.-get1()) {
          Log.d("MediaSessionService", "Sending " + paramKeyEvent + " to " + paramMediaSessionRecord);
        }
        if (paramBoolean) {
          this.mKeyEventReceiver.aquireWakeLockLocked();
        }
        if (paramMediaSessionRecord != null) {
          OnePlusProcessManager.resumeProcessByUID_out(paramMediaSessionRecord.getUid(), "MediaSession");
        }
        if (paramBoolean) {}
        for (i = KeyEventWakeLockReceiver.-get0(this.mKeyEventReceiver);; i = -1)
        {
          paramMediaSessionRecord.sendMediaButton(paramKeyEvent, i, this.mKeyEventReceiver, 1000, MediaSessionService.this.getContext().getPackageName());
          return;
        }
      }
      Object localObject = MediaSessionService.-get5(MediaSessionService.this).iterator();
      while (((Iterator)localObject).hasNext())
      {
        i = ((Integer)((Iterator)localObject).next()).intValue();
        paramMediaSessionRecord = (MediaSessionService.UserRecord)MediaSessionService.-get12(MediaSessionService.this).get(i);
        if ((MediaSessionService.UserRecord.-get0(paramMediaSessionRecord) != null) || (MediaSessionService.UserRecord.-get1(paramMediaSessionRecord) != null))
        {
          if (paramBoolean) {
            this.mKeyEventReceiver.aquireWakeLockLocked();
          }
          if (MediaSessionService.UserRecord.-get0(paramMediaSessionRecord) != null) {
            OnePlusProcessManager.resumeProcessByUID_out(MediaSessionService.UserRecord.-get0(paramMediaSessionRecord).getCreatorUid(), "MediaSession");
          }
          localObject = new Intent("android.intent.action.MEDIA_BUTTON");
          ((Intent)localObject).addFlags(268435456);
          ((Intent)localObject).putExtra("android.intent.extra.KEY_EVENT", paramKeyEvent);
        }
      }
      for (;;)
      {
        try
        {
          if (MediaSessionService.UserRecord.-get0(paramMediaSessionRecord) != null)
          {
            if (MediaSessionService.-get1()) {
              Log.d("MediaSessionService", "Sending " + paramKeyEvent + " to the last known pendingIntent " + MediaSessionService.UserRecord.-get0(paramMediaSessionRecord));
            }
            paramKeyEvent = MediaSessionService.UserRecord.-get0(paramMediaSessionRecord);
            Context localContext = MediaSessionService.this.getContext();
            if (!paramBoolean) {
              break label540;
            }
            i = KeyEventWakeLockReceiver.-get0(this.mKeyEventReceiver);
            paramKeyEvent.send(localContext, i, (Intent)localObject, this.mKeyEventReceiver, MediaSessionService.-get6(MediaSessionService.this));
            return;
          }
          if (MediaSessionService.-get1()) {
            Log.d("MediaSessionService", "Sending " + paramKeyEvent + " to the restored intent " + MediaSessionService.UserRecord.-get1(paramMediaSessionRecord));
          }
          ((Intent)localObject).setComponent(MediaSessionService.UserRecord.-get1(paramMediaSessionRecord));
          MediaSessionService.this.getContext().sendBroadcastAsUser((Intent)localObject, UserHandle.of(i));
          return;
        }
        catch (PendingIntent.CanceledException paramKeyEvent)
        {
          Log.i("MediaSessionService", "Error sending key event to media button receiver " + MediaSessionService.UserRecord.-get0(paramMediaSessionRecord), paramKeyEvent);
          return;
        }
        if (MediaSessionService.-get0()) {
          Log.d("MediaSessionService", "Sending media key ordered broadcast");
        }
        if (paramBoolean) {
          MediaSessionService.-get9(MediaSessionService.this).acquire();
        }
        paramMediaSessionRecord = new Intent("android.intent.action.MEDIA_BUTTON", null);
        paramMediaSessionRecord.addFlags(268435456);
        paramMediaSessionRecord.putExtra("android.intent.extra.KEY_EVENT", paramKeyEvent);
        if (paramBoolean) {
          paramMediaSessionRecord.putExtra("android.media.AudioService.WAKELOCK_ACQUIRED", 1980);
        }
        MediaSessionService.this.getContext().sendOrderedBroadcastAsUser(paramMediaSessionRecord, UserHandle.CURRENT, null, this.mKeyEventDone, MediaSessionService.-get6(MediaSessionService.this), -1, null, null);
        return;
        label540:
        i = -1;
      }
    }
    
    private void handleVoiceKeyEventLocked(KeyEvent paramKeyEvent, boolean paramBoolean, MediaSessionRecord paramMediaSessionRecord)
    {
      if ((paramMediaSessionRecord != null) && (paramMediaSessionRecord.hasFlag(65536)))
      {
        dispatchMediaKeyEventLocked(paramKeyEvent, paramBoolean, paramMediaSessionRecord);
        return;
      }
      int j = paramKeyEvent.getAction();
      int i;
      if ((paramKeyEvent.getFlags() & 0x80) != 0)
      {
        i = 1;
        if (j != 0) {
          break label101;
        }
        if (paramKeyEvent.getRepeatCount() != 0) {
          break label71;
        }
        this.mVoiceButtonDown = true;
        this.mVoiceButtonHandled = false;
      }
      label71:
      label101:
      do
      {
        do
        {
          do
          {
            return;
            i = 0;
            break;
          } while ((!this.mVoiceButtonDown) || (this.mVoiceButtonHandled) || (i == 0));
          this.mVoiceButtonHandled = true;
          startVoiceInput(paramBoolean);
          return;
        } while ((j != 1) || (!this.mVoiceButtonDown));
        this.mVoiceButtonDown = false;
      } while ((this.mVoiceButtonHandled) || (paramKeyEvent.isCanceled()));
      dispatchMediaKeyEventLocked(KeyEvent.changeAction(paramKeyEvent, 0), paramBoolean, paramMediaSessionRecord);
      dispatchMediaKeyEventLocked(paramKeyEvent, paramBoolean, paramMediaSessionRecord);
    }
    
    private boolean isUserSetupComplete()
    {
      boolean bool = false;
      if (Settings.Secure.getIntForUser(MediaSessionService.this.getContext().getContentResolver(), "user_setup_complete", 0, -2) != 0) {
        bool = true;
      }
      return bool;
    }
    
    private boolean isValidLocalStreamType(int paramInt)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (paramInt >= 0)
      {
        bool1 = bool2;
        if (paramInt <= 5) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    private boolean isVoiceKey(int paramInt)
    {
      return paramInt == 79;
    }
    
    private void startVoiceInput(boolean paramBoolean)
    {
      boolean bool2 = false;
      Object localObject1 = (PowerManager)MediaSessionService.this.getContext().getSystemService("power");
      boolean bool1;
      if (MediaSessionService.-get7(MediaSessionService.this) != null)
      {
        bool1 = MediaSessionService.-get7(MediaSessionService.this).isKeyguardLocked();
        if ((bool1) || (!((PowerManager)localObject1).isScreenOn())) {
          break label170;
        }
        localObject1 = new Intent("android.speech.action.WEB_SEARCH");
        Log.i("MediaSessionService", "voice-based interactions: about to use ACTION_WEB_SEARCH");
      }
      for (;;)
      {
        if (paramBoolean) {
          MediaSessionService.-get9(MediaSessionService.this).acquire();
        }
        if (localObject1 != null) {}
        try
        {
          ((Intent)localObject1).setFlags(276824064);
          if (MediaSessionService.-get0()) {
            Log.d("MediaSessionService", "voiceIntent: " + localObject1);
          }
          MediaSessionService.this.getContext().startActivityAsUser((Intent)localObject1, UserHandle.CURRENT);
          return;
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
          Log.w("MediaSessionService", "No activity for search: " + localActivityNotFoundException);
          return;
        }
        finally
        {
          if (!paramBoolean) {
            break label279;
          }
          MediaSessionService.-get9(MediaSessionService.this).release();
        }
        bool1 = false;
        break;
        label170:
        localObject1 = new Intent("android.speech.action.VOICE_SEARCH_HANDS_FREE");
        if (bool1) {
          bool2 = MediaSessionService.-get7(MediaSessionService.this).isKeyguardSecure();
        }
        ((Intent)localObject1).putExtra("android.speech.extras.EXTRA_SECURE", bool2);
        Log.i("MediaSessionService", "voice-based interactions: about to use ACTION_VOICE_SEARCH_HANDS_FREE");
      }
    }
    
    private int verifySessionsRequest(ComponentName paramComponentName, int paramInt1, int paramInt2, int paramInt3)
    {
      String str = null;
      if (paramComponentName != null)
      {
        str = paramComponentName.getPackageName();
        MediaSessionService.-wrap4(MediaSessionService.this, str, paramInt3);
      }
      paramInt1 = ActivityManager.handleIncomingUser(paramInt2, paramInt3, paramInt1, true, true, "getSessions", str);
      MediaSessionService.-wrap3(MediaSessionService.this, paramComponentName, paramInt2, paramInt3, paramInt1);
      return paramInt1;
    }
    
    /* Error */
    public void addSessionsListener(IActiveSessionsListener paramIActiveSessionsListener, ComponentName paramComponentName, int paramInt)
      throws RemoteException
    {
      // Byte code:
      //   0: invokestatic 424	android/os/Binder:getCallingPid	()I
      //   3: istore 4
      //   5: invokestatic 427	android/os/Binder:getCallingUid	()I
      //   8: istore 5
      //   10: invokestatic 431	android/os/Binder:clearCallingIdentity	()J
      //   13: lstore 6
      //   15: aload_0
      //   16: aload_2
      //   17: iload_3
      //   18: iload 4
      //   20: iload 5
      //   22: invokespecial 433	com/android/server/media/MediaSessionService$SessionManagerImpl:verifySessionsRequest	(Landroid/content/ComponentName;III)I
      //   25: istore_3
      //   26: aload_0
      //   27: getfield 32	com/android/server/media/MediaSessionService$SessionManagerImpl:this$0	Lcom/android/server/media/MediaSessionService;
      //   30: invokestatic 437	com/android/server/media/MediaSessionService:-get8	(Lcom/android/server/media/MediaSessionService;)Ljava/lang/Object;
      //   33: astore 8
      //   35: aload 8
      //   37: monitorenter
      //   38: aload_0
      //   39: getfield 32	com/android/server/media/MediaSessionService$SessionManagerImpl:this$0	Lcom/android/server/media/MediaSessionService;
      //   42: aload_1
      //   43: invokestatic 441	com/android/server/media/MediaSessionService:-wrap1	(Lcom/android/server/media/MediaSessionService;Landroid/media/session/IActiveSessionsListener;)I
      //   46: iconst_m1
      //   47: if_icmpeq +21 -> 68
      //   50: ldc 74
      //   52: ldc_w 443
      //   55: invokestatic 396	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   58: pop
      //   59: aload 8
      //   61: monitorexit
      //   62: lload 6
      //   64: invokestatic 447	android/os/Binder:restoreCallingIdentity	(J)V
      //   67: return
      //   68: new 449	com/android/server/media/MediaSessionService$SessionsListenerRecord
      //   71: dup
      //   72: aload_0
      //   73: getfield 32	com/android/server/media/MediaSessionService$SessionManagerImpl:this$0	Lcom/android/server/media/MediaSessionService;
      //   76: aload_1
      //   77: aload_2
      //   78: iload_3
      //   79: iload 4
      //   81: iload 5
      //   83: invokespecial 452	com/android/server/media/MediaSessionService$SessionsListenerRecord:<init>	(Lcom/android/server/media/MediaSessionService;Landroid/media/session/IActiveSessionsListener;Landroid/content/ComponentName;III)V
      //   86: astore_2
      //   87: aload_1
      //   88: invokeinterface 458 1 0
      //   93: aload_2
      //   94: iconst_0
      //   95: invokeinterface 464 3 0
      //   100: aload_0
      //   101: getfield 32	com/android/server/media/MediaSessionService$SessionManagerImpl:this$0	Lcom/android/server/media/MediaSessionService;
      //   104: invokestatic 468	com/android/server/media/MediaSessionService:-get11	(Lcom/android/server/media/MediaSessionService;)Ljava/util/ArrayList;
      //   107: aload_2
      //   108: invokevirtual 474	java/util/ArrayList:add	(Ljava/lang/Object;)Z
      //   111: pop
      //   112: aload 8
      //   114: monitorexit
      //   115: lload 6
      //   117: invokestatic 447	android/os/Binder:restoreCallingIdentity	(J)V
      //   120: return
      //   121: astore_1
      //   122: ldc 74
      //   124: ldc_w 476
      //   127: aload_1
      //   128: invokestatic 137	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   131: pop
      //   132: aload 8
      //   134: monitorexit
      //   135: lload 6
      //   137: invokestatic 447	android/os/Binder:restoreCallingIdentity	(J)V
      //   140: return
      //   141: astore_1
      //   142: aload 8
      //   144: monitorexit
      //   145: aload_1
      //   146: athrow
      //   147: astore_1
      //   148: lload 6
      //   150: invokestatic 447	android/os/Binder:restoreCallingIdentity	(J)V
      //   153: aload_1
      //   154: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	155	0	this	SessionManagerImpl
      //   0	155	1	paramIActiveSessionsListener	IActiveSessionsListener
      //   0	155	2	paramComponentName	ComponentName
      //   0	155	3	paramInt	int
      //   3	77	4	i	int
      //   8	74	5	j	int
      //   13	136	6	l	long
      // Exception table:
      //   from	to	target	type
      //   87	100	121	android/os/RemoteException
      //   38	59	141	finally
      //   68	87	141	finally
      //   87	100	141	finally
      //   100	112	141	finally
      //   122	132	141	finally
      //   15	38	147	finally
      //   59	62	147	finally
      //   112	115	147	finally
      //   132	135	147	finally
      //   142	147	147	finally
    }
    
    public ISession createSession(String paramString1, ISessionCallback paramISessionCallback, String paramString2, int paramInt)
      throws RemoteException
    {
      int i = Binder.getCallingPid();
      int j = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        MediaSessionService.-wrap4(MediaSessionService.this, paramString1, j);
        paramInt = ActivityManager.handleIncomingUser(i, j, paramInt, false, true, "createSession", paramString1);
        if (paramISessionCallback == null) {
          throw new IllegalArgumentException("Controller callback cannot be null");
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      paramString1 = MediaSessionService.-wrap0(MediaSessionService.this, i, j, paramInt, paramString1, paramISessionCallback, paramString2).getSessionBinder();
      Binder.restoreCallingIdentity(l);
      return paramString1;
    }
    
    /* Error */
    public void dispatchAdjustVolume(int paramInt1, int paramInt2, int paramInt3)
    {
      // Byte code:
      //   0: invokestatic 431	android/os/Binder:clearCallingIdentity	()J
      //   3: lstore 4
      //   5: aload_0
      //   6: getfield 32	com/android/server/media/MediaSessionService$SessionManagerImpl:this$0	Lcom/android/server/media/MediaSessionService;
      //   9: invokestatic 437	com/android/server/media/MediaSessionService:-get8	(Lcom/android/server/media/MediaSessionService;)Ljava/lang/Object;
      //   12: astore 6
      //   14: aload 6
      //   16: monitorenter
      //   17: aload_0
      //   18: iload_1
      //   19: iload_2
      //   20: iload_3
      //   21: aload_0
      //   22: getfield 32	com/android/server/media/MediaSessionService$SessionManagerImpl:this$0	Lcom/android/server/media/MediaSessionService;
      //   25: invokestatic 499	com/android/server/media/MediaSessionService:-get10	(Lcom/android/server/media/MediaSessionService;)Lcom/android/server/media/MediaSessionStack;
      //   28: aload_0
      //   29: getfield 32	com/android/server/media/MediaSessionService$SessionManagerImpl:this$0	Lcom/android/server/media/MediaSessionService;
      //   32: invokestatic 183	com/android/server/media/MediaSessionService:-get5	(Lcom/android/server/media/MediaSessionService;)Ljava/util/List;
      //   35: invokevirtual 505	com/android/server/media/MediaSessionStack:getDefaultVolumeSession	(Ljava/util/List;)Lcom/android/server/media/MediaSessionRecord;
      //   38: invokespecial 507	com/android/server/media/MediaSessionService$SessionManagerImpl:dispatchAdjustVolumeLocked	(IIILcom/android/server/media/MediaSessionRecord;)V
      //   41: aload 6
      //   43: monitorexit
      //   44: lload 4
      //   46: invokestatic 447	android/os/Binder:restoreCallingIdentity	(J)V
      //   49: return
      //   50: astore 7
      //   52: aload 6
      //   54: monitorexit
      //   55: aload 7
      //   57: athrow
      //   58: astore 6
      //   60: lload 4
      //   62: invokestatic 447	android/os/Binder:restoreCallingIdentity	(J)V
      //   65: aload 6
      //   67: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	68	0	this	SessionManagerImpl
      //   0	68	1	paramInt1	int
      //   0	68	2	paramInt2	int
      //   0	68	3	paramInt3	int
      //   3	58	4	l	long
      //   58	8	6	localObject2	Object
      //   50	6	7	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   17	41	50	finally
      //   5	17	58	finally
      //   41	44	58	finally
      //   52	58	58	finally
    }
    
    public void dispatchMediaKeyEvent(KeyEvent paramKeyEvent, boolean paramBoolean)
    {
      int i;
      int j;
      long l;
      if ((paramKeyEvent != null) && (KeyEvent.isMediaKey(paramKeyEvent.getKeyCode())))
      {
        i = Binder.getCallingPid();
        j = Binder.getCallingUid();
        l = Binder.clearCallingIdentity();
      }
      for (;;)
      {
        try
        {
          if (MediaSessionService.-get0()) {
            Log.d("MediaSessionService", "dispatchMediaKeyEvent, pid=" + i + ", uid=" + j + ", event=" + paramKeyEvent);
          }
          if (!isUserSetupComplete())
          {
            Slog.i("MediaSessionService", "Not dispatching media key event because user setup is in progress.");
            return;
            Log.w("MediaSessionService", "Attempted to dispatch null or non-media key event.");
            return;
          }
          if ((isGlobalPriorityActive()) && (j != 1000))
          {
            Slog.i("MediaSessionService", "Only the system can dispatch media key event to the global priority session.");
            return;
          }
          Object localObject1 = MediaSessionService.-get8(MediaSessionService.this);
          boolean bool2 = true;
          try
          {
            Object localObject2 = MediaSessionService.-get5(MediaSessionService.this).iterator();
            bool1 = bool2;
            if (((Iterator)localObject2).hasNext())
            {
              i = ((Integer)((Iterator)localObject2).next()).intValue();
              MediaSessionService.UserRecord localUserRecord = (MediaSessionService.UserRecord)MediaSessionService.-get12(MediaSessionService.this).get(i);
              if (MediaSessionService.UserRecord.-get0(localUserRecord) == null) {
                if (MediaSessionService.UserRecord.-get1(localUserRecord) == null) {
                  continue;
                }
              }
            }
            else
            {
              if (MediaSessionService.-get0()) {
                Log.d("MediaSessionService", "dispatchMediaKeyEvent, useNotPlayingSessions=" + bool1);
              }
              localObject2 = MediaSessionService.-get10(MediaSessionService.this).getDefaultMediaButtonSession(MediaSessionService.-get5(MediaSessionService.this), bool1);
              if (isVoiceKey(paramKeyEvent.getKeyCode()))
              {
                handleVoiceKeyEventLocked(paramKeyEvent, paramBoolean, (MediaSessionRecord)localObject2);
                return;
              }
              dispatchMediaKeyEventLocked(paramKeyEvent, paramBoolean, (MediaSessionRecord)localObject2);
              continue;
              paramKeyEvent = finally;
            }
          }
          finally {}
          boolean bool1 = false;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
    }
    
    public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      if (MediaSessionService.this.getContext().checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramPrintWriter.println("Permission Denial: can't dump MediaSessionService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      paramPrintWriter.println("MEDIA SESSION SERVICE (dumpsys media_session)");
      paramPrintWriter.println();
      synchronized (MediaSessionService.-get8(MediaSessionService.this))
      {
        paramPrintWriter.println(MediaSessionService.-get11(MediaSessionService.this).size() + " sessions listeners.");
        int j = MediaSessionService.-get2(MediaSessionService.this).size();
        paramPrintWriter.println(j + " Sessions:");
        int i = 0;
        while (i < j)
        {
          ((MediaSessionRecord)MediaSessionService.-get2(MediaSessionService.this).get(i)).dump(paramPrintWriter, "");
          paramPrintWriter.println();
          i += 1;
        }
        MediaSessionService.-get10(MediaSessionService.this).dump(paramPrintWriter, "");
        paramPrintWriter.println("User Records:");
        j = MediaSessionService.-get12(MediaSessionService.this).size();
        i = 0;
        while (i < j)
        {
          ((MediaSessionService.UserRecord)MediaSessionService.-get12(MediaSessionService.this).get(MediaSessionService.-get12(MediaSessionService.this).keyAt(i))).dumpLocked(paramPrintWriter, "");
          i += 1;
        }
        return;
      }
    }
    
    /* Error */
    public List<IBinder> getSessions(ComponentName arg1, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 424	android/os/Binder:getCallingPid	()I
      //   3: istore_3
      //   4: invokestatic 427	android/os/Binder:getCallingUid	()I
      //   7: istore 4
      //   9: invokestatic 431	android/os/Binder:clearCallingIdentity	()J
      //   12: lstore 5
      //   14: aload_0
      //   15: aload_1
      //   16: iload_2
      //   17: iload_3
      //   18: iload 4
      //   20: invokespecial 433	com/android/server/media/MediaSessionService$SessionManagerImpl:verifySessionsRequest	(Landroid/content/ComponentName;III)I
      //   23: istore_2
      //   24: new 470	java/util/ArrayList
      //   27: dup
      //   28: invokespecial 593	java/util/ArrayList:<init>	()V
      //   31: astore 7
      //   33: aload_0
      //   34: getfield 32	com/android/server/media/MediaSessionService$SessionManagerImpl:this$0	Lcom/android/server/media/MediaSessionService;
      //   37: invokestatic 437	com/android/server/media/MediaSessionService:-get8	(Lcom/android/server/media/MediaSessionService;)Ljava/lang/Object;
      //   40: astore_1
      //   41: aload_1
      //   42: monitorenter
      //   43: aload_0
      //   44: getfield 32	com/android/server/media/MediaSessionService$SessionManagerImpl:this$0	Lcom/android/server/media/MediaSessionService;
      //   47: invokestatic 499	com/android/server/media/MediaSessionService:-get10	(Lcom/android/server/media/MediaSessionService;)Lcom/android/server/media/MediaSessionStack;
      //   50: iload_2
      //   51: invokevirtual 597	com/android/server/media/MediaSessionStack:getActiveSessions	(I)Ljava/util/ArrayList;
      //   54: astore 8
      //   56: aload 8
      //   58: invokevirtual 567	java/util/ArrayList:size	()I
      //   61: istore_3
      //   62: iconst_0
      //   63: istore_2
      //   64: iload_2
      //   65: iload_3
      //   66: if_icmpge +33 -> 99
      //   69: aload 7
      //   71: aload 8
      //   73: iload_2
      //   74: invokevirtual 575	java/util/ArrayList:get	(I)Ljava/lang/Object;
      //   77: checkcast 142	com/android/server/media/MediaSessionRecord
      //   80: invokevirtual 601	com/android/server/media/MediaSessionRecord:getControllerBinder	()Landroid/media/session/ISessionController;
      //   83: invokeinterface 604 1 0
      //   88: invokevirtual 474	java/util/ArrayList:add	(Ljava/lang/Object;)Z
      //   91: pop
      //   92: iload_2
      //   93: iconst_1
      //   94: iadd
      //   95: istore_2
      //   96: goto -32 -> 64
      //   99: aload_1
      //   100: monitorexit
      //   101: lload 5
      //   103: invokestatic 447	android/os/Binder:restoreCallingIdentity	(J)V
      //   106: aload 7
      //   108: areturn
      //   109: astore 7
      //   111: aload_1
      //   112: monitorexit
      //   113: aload 7
      //   115: athrow
      //   116: astore_1
      //   117: lload 5
      //   119: invokestatic 447	android/os/Binder:restoreCallingIdentity	(J)V
      //   122: aload_1
      //   123: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	124	0	this	SessionManagerImpl
      //   0	124	2	paramInt	int
      //   3	64	3	i	int
      //   7	12	4	j	int
      //   12	106	5	l	long
      //   31	76	7	localArrayList1	ArrayList
      //   109	5	7	localObject	Object
      //   54	18	8	localArrayList2	ArrayList
      // Exception table:
      //   from	to	target	type
      //   43	62	109	finally
      //   69	92	109	finally
      //   14	43	116	finally
      //   99	101	116	finally
      //   111	116	116	finally
    }
    
    public boolean isGlobalPriorityActive()
    {
      return MediaSessionService.-get10(MediaSessionService.this).isGlobalPriorityActive();
    }
    
    public void removeSessionsListener(IActiveSessionsListener paramIActiveSessionsListener)
      throws RemoteException
    {
      synchronized (MediaSessionService.-get8(MediaSessionService.this))
      {
        int i = MediaSessionService.-wrap1(MediaSessionService.this, paramIActiveSessionsListener);
        if (i != -1) {
          paramIActiveSessionsListener = (MediaSessionService.SessionsListenerRecord)MediaSessionService.-get11(MediaSessionService.this).remove(i);
        }
      }
      try
      {
        MediaSessionService.SessionsListenerRecord.-get1(paramIActiveSessionsListener).asBinder().unlinkToDeath(paramIActiveSessionsListener, 0);
        return;
        paramIActiveSessionsListener = finally;
        throw paramIActiveSessionsListener;
      }
      catch (Exception paramIActiveSessionsListener)
      {
        for (;;) {}
      }
    }
    
    public void setRemoteVolumeController(IRemoteVolumeController paramIRemoteVolumeController)
    {
      int i = Binder.getCallingPid();
      int j = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        MediaSessionService.-wrap5(MediaSessionService.this, "listen for volume changes", i, j);
        MediaSessionService.-set0(MediaSessionService.this, paramIRemoteVolumeController);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    class KeyEventWakeLockReceiver
      extends ResultReceiver
      implements Runnable, PendingIntent.OnFinished
    {
      private final Handler mHandler;
      private int mLastTimeoutId = 0;
      private int mRefCount = 0;
      
      public KeyEventWakeLockReceiver(Handler paramHandler)
      {
        super();
        this.mHandler = paramHandler;
      }
      
      private void releaseWakeLockLocked()
      {
        MediaSessionService.-get9(MediaSessionService.this).release();
        this.mHandler.removeCallbacks(this);
      }
      
      public void aquireWakeLockLocked()
      {
        if (this.mRefCount == 0) {
          MediaSessionService.-get9(MediaSessionService.this).acquire();
        }
        this.mRefCount += 1;
        this.mHandler.removeCallbacks(this);
        this.mHandler.postDelayed(this, 5000L);
      }
      
      protected void onReceiveResult(int paramInt, Bundle arg2)
      {
        if (paramInt < this.mLastTimeoutId) {
          return;
        }
        synchronized (MediaSessionService.-get8(MediaSessionService.this))
        {
          if (this.mRefCount > 0)
          {
            this.mRefCount -= 1;
            if (this.mRefCount == 0) {
              releaseWakeLockLocked();
            }
          }
          return;
        }
      }
      
      public void onSendFinished(PendingIntent paramPendingIntent, Intent paramIntent, int paramInt, String paramString, Bundle paramBundle)
      {
        onReceiveResult(paramInt, null);
      }
      
      public void onTimeout()
      {
        synchronized (MediaSessionService.-get8(MediaSessionService.this))
        {
          int i = this.mRefCount;
          if (i == 0) {
            return;
          }
          this.mLastTimeoutId += 1;
          this.mRefCount = 0;
          releaseWakeLockLocked();
          return;
        }
      }
      
      public void run()
      {
        onTimeout();
      }
    }
  }
  
  final class SessionsListenerRecord
    implements IBinder.DeathRecipient
  {
    private final ComponentName mComponentName;
    private final IActiveSessionsListener mListener;
    private final int mPid;
    private final int mUid;
    private final int mUserId;
    
    public SessionsListenerRecord(IActiveSessionsListener paramIActiveSessionsListener, ComponentName paramComponentName, int paramInt1, int paramInt2, int paramInt3)
    {
      this.mListener = paramIActiveSessionsListener;
      this.mComponentName = paramComponentName;
      this.mUserId = paramInt1;
      this.mPid = paramInt2;
      this.mUid = paramInt3;
    }
    
    public void binderDied()
    {
      synchronized (MediaSessionService.-get8(MediaSessionService.this))
      {
        MediaSessionService.-get11(MediaSessionService.this).remove(this);
        return;
      }
    }
  }
  
  final class SettingsObserver
    extends ContentObserver
  {
    private final Uri mSecureSettingsUri = Settings.Secure.getUriFor("enabled_notification_listeners");
    
    private SettingsObserver()
    {
      super();
    }
    
    private void observe()
    {
      MediaSessionService.-get4(MediaSessionService.this).registerContentObserver(this.mSecureSettingsUri, false, this, -1);
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      MediaSessionService.-wrap7(MediaSessionService.this);
    }
  }
  
  final class UserRecord
  {
    private final Context mContext;
    private PendingIntent mLastMediaButtonReceiver;
    private ComponentName mRestoredMediaButtonReceiver;
    private final ArrayList<MediaSessionRecord> mSessions = new ArrayList();
    private final int mUserId;
    
    public UserRecord(Context paramContext, int paramInt)
    {
      this.mContext = paramContext;
      this.mUserId = paramInt;
      restoreMediaButtonReceiver();
    }
    
    private void restoreMediaButtonReceiver()
    {
      Object localObject = Settings.Secure.getStringForUser(MediaSessionService.-get4(MediaSessionService.this), "media_button_receiver", this.mUserId);
      if (!TextUtils.isEmpty((CharSequence)localObject))
      {
        localObject = ComponentName.unflattenFromString((String)localObject);
        if (localObject == null) {
          return;
        }
        this.mRestoredMediaButtonReceiver = ((ComponentName)localObject);
      }
    }
    
    public void addSessionLocked(MediaSessionRecord paramMediaSessionRecord)
    {
      this.mSessions.add(paramMediaSessionRecord);
    }
    
    public void destroyLocked()
    {
      int i = this.mSessions.size() - 1;
      while (i >= 0)
      {
        MediaSessionRecord localMediaSessionRecord = (MediaSessionRecord)this.mSessions.get(i);
        MediaSessionService.-wrap2(MediaSessionService.this, localMediaSessionRecord);
        i -= 1;
      }
    }
    
    public void dumpLocked(PrintWriter paramPrintWriter, String paramString)
    {
      paramPrintWriter.println(paramString + "Record for user " + this.mUserId);
      paramString = paramString + "  ";
      paramPrintWriter.println(paramString + "MediaButtonReceiver:" + this.mLastMediaButtonReceiver);
      paramPrintWriter.println(paramString + "Restored ButtonReceiver:" + this.mRestoredMediaButtonReceiver);
      int j = this.mSessions.size();
      paramPrintWriter.println(paramString + j + " Sessions:");
      int i = 0;
      while (i < j)
      {
        paramPrintWriter.println(paramString + ((MediaSessionRecord)this.mSessions.get(i)).toString());
        i += 1;
      }
    }
    
    public ArrayList<MediaSessionRecord> getSessionsLocked()
    {
      return this.mSessions;
    }
    
    public void removeSessionLocked(MediaSessionRecord paramMediaSessionRecord)
    {
      this.mSessions.remove(paramMediaSessionRecord);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/media/MediaSessionService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */