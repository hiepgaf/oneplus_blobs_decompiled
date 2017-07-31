package com.android.server.media;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.media.AudioManagerInternal;
import android.media.AudioSystem;
import android.media.MediaMetadata;
import android.media.MediaMetadata.Builder;
import android.media.Rating;
import android.media.session.ISession;
import android.media.session.ISession.Stub;
import android.media.session.ISessionCallback;
import android.media.session.ISessionController;
import android.media.session.ISessionController.Stub;
import android.media.session.ISessionControllerCallback;
import android.media.session.MediaSession;
import android.media.session.ParcelableVolumeInfo;
import android.media.session.PlaybackState;
import android.media.session.PlaybackState.Builder;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;
import android.util.Slog;
import android.view.KeyEvent;
import com.android.server.LocalServices;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MediaSessionRecord
  implements IBinder.DeathRecipient
{
  private static final int ACTIVE_BUFFER = 30000;
  private static final boolean DEBUG = Log.isLoggable("MediaSessionRecord", 3);
  private static final int OPTIMISTIC_VOLUME_TIMEOUT = 1000;
  private static final String TAG = "MediaSessionRecord";
  private static final int UID_NOT_SET = -1;
  private AudioAttributes mAudioAttrs;
  private AudioManager mAudioManager;
  private AudioManagerInternal mAudioManagerInternal;
  private String mBrowsedPlayerURI;
  private String mCallingPackage;
  private int mCallingUid = -1;
  private final Runnable mClearOptimisticVolumeRunnable = new Runnable()
  {
    public void run()
    {
      if (MediaSessionRecord.-get16(MediaSessionRecord.this) != MediaSessionRecord.-get7(MediaSessionRecord.this)) {}
      for (int i = 1;; i = 0)
      {
        MediaSessionRecord.-set12(MediaSessionRecord.this, -1);
        if (i != 0) {
          MediaSessionRecord.-wrap13(MediaSessionRecord.this);
        }
        return;
      }
    }
  };
  private final ControllerStub mController;
  private final ArrayList<ISessionControllerCallback> mControllerCallbacks = new ArrayList();
  private int mCurrentVolume = 0;
  private boolean mDestroyed = false;
  private Bundle mExtras;
  private long mFlags;
  private final MessageHandler mHandler;
  private boolean mIsActive = false;
  private long mLastActiveTime;
  private PendingIntent mLaunchIntent;
  private final Object mLock = new Object();
  private int mMaxVolume = 0;
  private PendingIntent mMediaButtonReceiver;
  private MediaMetadata mMetadata;
  private long[] mNowPlayingList;
  private int mOptimisticVolume = -1;
  private final int mOwnerPid;
  private final int mOwnerUid;
  private final String mPackageName;
  private boolean mPlayItemStatus;
  private PlaybackState mPlaybackState;
  private ParceledListSlice mQueue;
  private CharSequence mQueueTitle;
  private int mRatingType;
  private final MediaSessionService mService;
  private final SessionStub mSession;
  private final SessionCb mSessionCb;
  private final String mTag;
  private final int mUserId;
  private int mVolumeControlType = 2;
  private int mVolumeType = 1;
  
  public MediaSessionRecord(int paramInt1, int paramInt2, int paramInt3, String paramString1, ISessionCallback paramISessionCallback, String paramString2, MediaSessionService paramMediaSessionService, Handler paramHandler)
  {
    this.mOwnerPid = paramInt1;
    this.mOwnerUid = paramInt2;
    this.mUserId = paramInt3;
    this.mPackageName = paramString1;
    this.mTag = paramString2;
    this.mController = new ControllerStub();
    this.mSession = new SessionStub(null);
    this.mSessionCb = new SessionCb(paramISessionCallback);
    this.mService = paramMediaSessionService;
    this.mHandler = new MessageHandler(paramHandler.getLooper());
    this.mAudioManager = ((AudioManager)paramMediaSessionService.getContext().getSystemService("audio"));
    this.mAudioManagerInternal = ((AudioManagerInternal)LocalServices.getService(AudioManagerInternal.class));
    this.mAudioAttrs = new AudioAttributes.Builder().setUsage(1).build();
  }
  
  private int getControllerCbIndexForCb(ISessionControllerCallback paramISessionControllerCallback)
  {
    paramISessionControllerCallback = paramISessionControllerCallback.asBinder();
    int i = this.mControllerCallbacks.size() - 1;
    while (i >= 0)
    {
      if (paramISessionControllerCallback.equals(((ISessionControllerCallback)this.mControllerCallbacks.get(i)).asBinder())) {
        return i;
      }
      i -= 1;
    }
    return -1;
  }
  
  private String getShortMetadataString()
  {
    int i;
    if (this.mMetadata == null)
    {
      i = 0;
      if (this.mMetadata != null) {
        break label60;
      }
    }
    label60:
    for (Object localObject = null;; localObject = this.mMetadata.getDescription())
    {
      return "size=" + i + ", description=" + localObject;
      i = this.mMetadata.size();
      break;
    }
  }
  
  private PlaybackState getStateWithUpdatedPosition()
  {
    long l2 = -1L;
    for (;;)
    {
      PlaybackState localPlaybackState;
      long l1;
      synchronized (this.mLock)
      {
        localPlaybackState = this.mPlaybackState;
        l1 = l2;
        if (this.mMetadata != null)
        {
          l1 = l2;
          if (this.mMetadata.containsKey("android.media.metadata.DURATION")) {
            l1 = this.mMetadata.getLong("android.media.metadata.DURATION");
          }
        }
        Object localObject2 = null;
        ??? = localObject2;
        if (localPlaybackState != null)
        {
          if ((localPlaybackState.getState() != 3) && (localPlaybackState.getState() != 4)) {
            break label192;
          }
          l2 = localPlaybackState.getLastPositionUpdateTime();
          long l3 = SystemClock.elapsedRealtime();
          ??? = localObject2;
          if (l2 > 0L)
          {
            l2 = (localPlaybackState.getPlaybackSpeed() * (float)(l3 - l2)) + localPlaybackState.getPosition();
            if ((l1 < 0L) || (l2 <= l1)) {
              break label208;
            }
            ??? = new PlaybackState.Builder(localPlaybackState);
            ((PlaybackState.Builder)???).setState(localPlaybackState.getState(), l1, localPlaybackState.getPlaybackSpeed(), l3);
            ??? = ((PlaybackState.Builder)???).build();
          }
        }
        if (??? != null) {
          break;
        }
        return localPlaybackState;
      }
      label192:
      ??? = localObject3;
      if (localPlaybackState.getState() == 5)
      {
        continue;
        label208:
        l1 = l2;
        if (l2 < 0L) {
          l1 = 0L;
        }
      }
    }
    return (PlaybackState)???;
  }
  
  private void postAdjustLocalVolume(final int paramInt1, final int paramInt2, final int paramInt3, final String paramString, final int paramInt4, final boolean paramBoolean, final int paramInt5)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        if (paramBoolean)
        {
          if (AudioSystem.isStreamActive(paramInt1, 0))
          {
            MediaSessionRecord.-get3(MediaSessionRecord.this).adjustSuggestedStreamVolumeForUid(paramInt1, paramInt2, paramInt3, paramString, paramInt4);
            return;
          }
          AudioManagerInternal localAudioManagerInternal = MediaSessionRecord.-get3(MediaSessionRecord.this);
          int i = paramInt2;
          int j = paramInt3;
          localAudioManagerInternal.adjustSuggestedStreamVolumeForUid(Integer.MIN_VALUE, i, paramInt5 | j, paramString, paramInt4);
          return;
        }
        MediaSessionRecord.-get3(MediaSessionRecord.this).adjustStreamVolumeForUid(paramInt1, paramInt2, paramInt3, paramString, paramInt4);
      }
    });
  }
  
  private void pushBrowsePlayerInfo()
  {
    for (;;)
    {
      int i;
      ISessionControllerCallback localISessionControllerCallback;
      synchronized (this.mLock)
      {
        boolean bool = this.mDestroyed;
        if (bool) {
          return;
        }
        i = this.mControllerCallbacks.size() - 1;
        if (i < 0) {
          break label121;
        }
        localISessionControllerCallback = (ISessionControllerCallback)this.mControllerCallbacks.get(i);
      }
      try
      {
        Log.d("MediaSessionRecord", "pushBrowsePlayerInfo");
        localISessionControllerCallback.onUpdateFolderInfoBrowsedPlayer(this.mBrowsedPlayerURI);
        i -= 1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "unexpected exception in pushBrowsePlayerInfo. ", localRemoteException);
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (DeadObjectException localDeadObjectException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "Removing dead callback in pushBrowsePlayerInfo. ", localDeadObjectException);
          this.mControllerCallbacks.remove(i);
        }
      }
    }
    label121:
  }
  
  private void pushEvent(String paramString, Bundle paramBundle)
  {
    for (;;)
    {
      int i;
      ISessionControllerCallback localISessionControllerCallback;
      synchronized (this.mLock)
      {
        boolean bool = this.mDestroyed;
        if (bool) {
          return;
        }
        i = this.mControllerCallbacks.size() - 1;
        if (i < 0) {
          break label114;
        }
        localISessionControllerCallback = (ISessionControllerCallback)this.mControllerCallbacks.get(i);
      }
      try
      {
        localISessionControllerCallback.onEvent(paramString, paramBundle);
        i -= 1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "unexpected exception in pushEvent.", localRemoteException);
        }
        paramString = finally;
        throw paramString;
      }
      catch (DeadObjectException localDeadObjectException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "Removing dead callback in pushEvent.", localDeadObjectException);
          this.mControllerCallbacks.remove(i);
        }
      }
    }
    label114:
  }
  
  private void pushExtrasUpdate()
  {
    for (;;)
    {
      int i;
      ISessionControllerCallback localISessionControllerCallback;
      synchronized (this.mLock)
      {
        boolean bool = this.mDestroyed;
        if (bool) {
          return;
        }
        i = this.mControllerCallbacks.size() - 1;
        if (i < 0) {
          break label112;
        }
        localISessionControllerCallback = (ISessionControllerCallback)this.mControllerCallbacks.get(i);
      }
      try
      {
        localISessionControllerCallback.onExtrasChanged(this.mExtras);
        i -= 1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "unexpected exception in pushExtrasUpdate.", localRemoteException);
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (DeadObjectException localDeadObjectException)
      {
        for (;;)
        {
          this.mControllerCallbacks.remove(i);
          Log.w("MediaSessionRecord", "Removed dead callback in pushExtrasUpdate.", localDeadObjectException);
        }
      }
    }
    label112:
  }
  
  private void pushMetadataUpdate()
  {
    for (;;)
    {
      int i;
      ISessionControllerCallback localISessionControllerCallback;
      synchronized (this.mLock)
      {
        boolean bool = this.mDestroyed;
        if (bool) {
          return;
        }
        i = this.mControllerCallbacks.size() - 1;
        if (i < 0) {
          break label112;
        }
        localISessionControllerCallback = (ISessionControllerCallback)this.mControllerCallbacks.get(i);
      }
      try
      {
        localISessionControllerCallback.onMetadataChanged(this.mMetadata);
        i -= 1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "unexpected exception in pushMetadataUpdate. ", localRemoteException);
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (DeadObjectException localDeadObjectException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "Removing dead callback in pushMetadataUpdate. ", localDeadObjectException);
          this.mControllerCallbacks.remove(i);
        }
      }
    }
    label112:
  }
  
  private void pushNowPlayingContentChange()
  {
    for (;;)
    {
      int i;
      ISessionControllerCallback localISessionControllerCallback;
      synchronized (this.mLock)
      {
        boolean bool = this.mDestroyed;
        if (bool) {
          return;
        }
        i = this.mControllerCallbacks.size() - 1;
        if (i < 0) {
          break label117;
        }
        localISessionControllerCallback = (ISessionControllerCallback)this.mControllerCallbacks.get(i);
      }
      try
      {
        Log.d("MediaSessionRecord", "pushNowPlayingContentChange");
        localISessionControllerCallback.onUpdateNowPlayingContentChange();
        i -= 1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "unexpected exception in pushNowPlayingContentChange. ", localRemoteException);
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (DeadObjectException localDeadObjectException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "Removing dead callback in pushNowPlayingContentChange. ", localDeadObjectException);
          this.mControllerCallbacks.remove(i);
        }
      }
    }
    label117:
  }
  
  private void pushNowPlayingEntries()
  {
    for (;;)
    {
      int i;
      ISessionControllerCallback localISessionControllerCallback;
      synchronized (this.mLock)
      {
        boolean bool = this.mDestroyed;
        if (bool) {
          return;
        }
        i = this.mControllerCallbacks.size() - 1;
        if (i < 0) {
          break label121;
        }
        localISessionControllerCallback = (ISessionControllerCallback)this.mControllerCallbacks.get(i);
      }
      try
      {
        Log.d("MediaSessionRecord", "pushNowPlayingEntries");
        localISessionControllerCallback.onUpdateNowPlayingEntries(this.mNowPlayingList);
        i -= 1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "unexpected exception in pushNowPlayingEntries. ", localRemoteException);
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (DeadObjectException localDeadObjectException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "Removing dead callback in pushNowPlayingEntries. ", localDeadObjectException);
          this.mControllerCallbacks.remove(i);
        }
      }
    }
    label121:
  }
  
  private void pushPlayItemResponse()
  {
    for (;;)
    {
      int i;
      ISessionControllerCallback localISessionControllerCallback;
      synchronized (this.mLock)
      {
        boolean bool = this.mDestroyed;
        if (bool) {
          return;
        }
        i = this.mControllerCallbacks.size() - 1;
        if (i < 0) {
          break label121;
        }
        localISessionControllerCallback = (ISessionControllerCallback)this.mControllerCallbacks.get(i);
      }
      try
      {
        Log.d("MediaSessionRecord", "pushPlayItemResponse");
        localISessionControllerCallback.onPlayItemResponse(this.mPlayItemStatus);
        i -= 1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "unexpected exception in pushPlayItemResponse. ", localRemoteException);
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (DeadObjectException localDeadObjectException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "Removing dead callback in pushPlayItemResponse. ", localDeadObjectException);
          this.mControllerCallbacks.remove(i);
        }
      }
    }
    label121:
  }
  
  private void pushPlaybackStateUpdate()
  {
    for (;;)
    {
      int i;
      ISessionControllerCallback localISessionControllerCallback;
      synchronized (this.mLock)
      {
        boolean bool = this.mDestroyed;
        if (bool) {
          return;
        }
        i = this.mControllerCallbacks.size() - 1;
        if (i < 0) {
          break label112;
        }
        localISessionControllerCallback = (ISessionControllerCallback)this.mControllerCallbacks.get(i);
      }
      try
      {
        localISessionControllerCallback.onPlaybackStateChanged(this.mPlaybackState);
        i -= 1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "unexpected exception in pushPlaybackStateUpdate.", localRemoteException);
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (DeadObjectException localDeadObjectException)
      {
        for (;;)
        {
          this.mControllerCallbacks.remove(i);
          Log.w("MediaSessionRecord", "Removed dead callback in pushPlaybackStateUpdate.", localDeadObjectException);
        }
      }
    }
    label112:
  }
  
  private void pushQueueTitleUpdate()
  {
    for (;;)
    {
      int i;
      ISessionControllerCallback localISessionControllerCallback;
      synchronized (this.mLock)
      {
        boolean bool = this.mDestroyed;
        if (bool) {
          return;
        }
        i = this.mControllerCallbacks.size() - 1;
        if (i < 0) {
          break label112;
        }
        localISessionControllerCallback = (ISessionControllerCallback)this.mControllerCallbacks.get(i);
      }
      try
      {
        localISessionControllerCallback.onQueueTitleChanged(this.mQueueTitle);
        i -= 1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "unexpected exception in pushQueueTitleUpdate.", localRemoteException);
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (DeadObjectException localDeadObjectException)
      {
        for (;;)
        {
          this.mControllerCallbacks.remove(i);
          Log.w("MediaSessionRecord", "Removed dead callback in pushQueueTitleUpdate.", localDeadObjectException);
        }
      }
    }
    label112:
  }
  
  private void pushQueueUpdate()
  {
    for (;;)
    {
      int i;
      ISessionControllerCallback localISessionControllerCallback;
      synchronized (this.mLock)
      {
        boolean bool = this.mDestroyed;
        if (bool) {
          return;
        }
        i = this.mControllerCallbacks.size() - 1;
        if (i < 0) {
          break label112;
        }
        localISessionControllerCallback = (ISessionControllerCallback)this.mControllerCallbacks.get(i);
      }
      try
      {
        localISessionControllerCallback.onQueueChanged(this.mQueue);
        i -= 1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "unexpected exception in pushQueueUpdate.", localRemoteException);
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (DeadObjectException localDeadObjectException)
      {
        for (;;)
        {
          this.mControllerCallbacks.remove(i);
          Log.w("MediaSessionRecord", "Removed dead callback in pushQueueUpdate.", localDeadObjectException);
        }
      }
    }
    label112:
  }
  
  private void pushSessionDestroyed()
  {
    for (;;)
    {
      int i;
      ISessionControllerCallback localISessionControllerCallback;
      synchronized (this.mLock)
      {
        boolean bool = this.mDestroyed;
        if (!bool) {
          return;
        }
        i = this.mControllerCallbacks.size() - 1;
        if (i < 0) {
          break label108;
        }
        localISessionControllerCallback = (ISessionControllerCallback)this.mControllerCallbacks.get(i);
      }
      try
      {
        localISessionControllerCallback.onSessionDestroyed();
        i -= 1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "unexpected exception in pushEvent.", localRemoteException);
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (DeadObjectException localDeadObjectException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "Removing dead callback in pushEvent.", localDeadObjectException);
          this.mControllerCallbacks.remove(i);
        }
      }
    }
    label108:
    this.mControllerCallbacks.clear();
  }
  
  private void pushVolumeUpdate()
  {
    for (;;)
    {
      ParcelableVolumeInfo localParcelableVolumeInfo;
      int i;
      ISessionControllerCallback localISessionControllerCallback;
      synchronized (this.mLock)
      {
        boolean bool = this.mDestroyed;
        if (bool) {
          return;
        }
        localParcelableVolumeInfo = this.mController.getVolumeAttributes();
        i = this.mControllerCallbacks.size() - 1;
        if (i < 0) {
          break label110;
        }
        localISessionControllerCallback = (ISessionControllerCallback)this.mControllerCallbacks.get(i);
      }
      try
      {
        localISessionControllerCallback.onVolumeInfoChanged(localParcelableVolumeInfo);
        i -= 1;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "Unexpected exception in pushVolumeUpdate. ", localRemoteException);
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (DeadObjectException localDeadObjectException)
      {
        for (;;)
        {
          Log.w("MediaSessionRecord", "Removing dead callback in pushVolumeUpdate. ", localDeadObjectException);
        }
      }
    }
    label110:
  }
  
  private void updateCallingPackage()
  {
    updateCallingPackage(-1, null);
  }
  
  private void updateCallingPackage(int paramInt, String paramString)
  {
    int i = paramInt;
    if (paramInt == -1) {
      i = Binder.getCallingUid();
    }
    synchronized (this.mLock)
    {
      if ((this.mCallingUid == -1) || (this.mCallingUid != i))
      {
        this.mCallingUid = i;
        this.mCallingPackage = paramString;
        paramString = this.mCallingPackage;
        if (paramString != null) {
          return;
        }
        paramString = this.mService.getContext();
        if (paramString == null) {
          return;
        }
        paramString = paramString.getPackageManager().getPackagesForUid(i);
        if ((paramString != null) && (paramString.length > 0)) {
          this.mCallingPackage = paramString[0];
        }
      }
      return;
    }
  }
  
  public void adjustVolume(int paramInt1, int paramInt2, String paramString, int paramInt3, boolean paramBoolean)
  {
    int i;
    if (!isPlaybackActive(false))
    {
      i = paramInt2;
      if (!hasFlag(65536)) {}
    }
    else
    {
      i = paramInt2 & 0xFFFFFFFB;
    }
    if (this.mVolumeType == 1)
    {
      postAdjustLocalVolume(AudioAttributes.toLegacyStreamType(this.mAudioAttrs), paramInt1, i, paramString, paramInt3, paramBoolean, paramInt2 & 0x4);
      return;
    }
    if (this.mVolumeControlType == 0) {
      return;
    }
    if ((paramInt1 == 101) || (paramInt1 == -100)) {}
    while (paramInt1 == 100)
    {
      Log.w("MediaSessionRecord", "Muting remote playback is not supported");
      return;
    }
    this.mSessionCb.adjustVolume(paramInt1);
    if (this.mOptimisticVolume < 0) {}
    for (paramInt2 = this.mCurrentVolume;; paramInt2 = this.mOptimisticVolume)
    {
      this.mOptimisticVolume = (paramInt2 + paramInt1);
      this.mOptimisticVolume = Math.max(0, Math.min(this.mOptimisticVolume, this.mMaxVolume));
      this.mHandler.removeCallbacks(this.mClearOptimisticVolumeRunnable);
      this.mHandler.postDelayed(this.mClearOptimisticVolumeRunnable, 1000L);
      if (paramInt2 != this.mOptimisticVolume) {
        pushVolumeUpdate();
      }
      this.mService.notifyRemoteVolumeChanged(i, this);
      if (!DEBUG) {
        break;
      }
      Log.d("MediaSessionRecord", "Adjusted optimistic volume to " + this.mOptimisticVolume + " max is " + this.mMaxVolume);
      return;
    }
  }
  
  public void binderDied()
  {
    this.mService.sessionDied(this);
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    Object localObject = null;
    paramPrintWriter.println(paramString + this.mTag + " " + this);
    String str = paramString + "  ";
    paramPrintWriter.println(str + "ownerPid=" + this.mOwnerPid + ", ownerUid=" + this.mOwnerUid + ", userId=" + this.mUserId);
    paramPrintWriter.println(str + "package=" + this.mPackageName);
    paramPrintWriter.println(str + "launchIntent=" + this.mLaunchIntent);
    paramPrintWriter.println(str + "mediaButtonReceiver=" + this.mMediaButtonReceiver);
    paramPrintWriter.println(str + "active=" + this.mIsActive);
    paramPrintWriter.println(str + "flags=" + this.mFlags);
    paramPrintWriter.println(str + "rating type=" + this.mRatingType);
    paramPrintWriter.println(str + "controllers: " + this.mControllerCallbacks.size());
    StringBuilder localStringBuilder = new StringBuilder().append(str).append("state=");
    if (this.mPlaybackState == null)
    {
      paramString = (String)localObject;
      paramPrintWriter.println(paramString);
      paramPrintWriter.println(str + "audioAttrs=" + this.mAudioAttrs);
      paramPrintWriter.println(str + "volumeType=" + this.mVolumeType + ", controlType=" + this.mVolumeControlType + ", max=" + this.mMaxVolume + ", current=" + this.mCurrentVolume);
      paramPrintWriter.println(str + "metadata:" + getShortMetadataString());
      paramString = new StringBuilder().append(str).append("queueTitle=").append(this.mQueueTitle).append(", size=");
      if (this.mQueue != null) {
        break label588;
      }
    }
    label588:
    for (int i = 0;; i = this.mQueue.getList().size())
    {
      paramPrintWriter.println(i);
      return;
      paramString = this.mPlaybackState.toString();
      break;
    }
  }
  
  public AudioAttributes getAudioAttributes()
  {
    return this.mAudioAttrs;
  }
  
  public ISessionCallback getCallback()
  {
    return SessionCb.-get0(this.mSessionCb);
  }
  
  public ISessionController getControllerBinder()
  {
    return this.mController;
  }
  
  public int getCurrentVolume()
  {
    return this.mCurrentVolume;
  }
  
  public long getFlags()
  {
    return this.mFlags;
  }
  
  public int getMaxVolume()
  {
    return this.mMaxVolume;
  }
  
  public PendingIntent getMediaButtonReceiver()
  {
    return this.mMediaButtonReceiver;
  }
  
  public int getOptimisticVolume()
  {
    return this.mOptimisticVolume;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public int getPlaybackType()
  {
    return this.mVolumeType;
  }
  
  public ISession getSessionBinder()
  {
    return this.mSession;
  }
  
  public String getTag()
  {
    return this.mTag;
  }
  
  public int getUid()
  {
    return this.mOwnerUid;
  }
  
  public int getUserId()
  {
    return this.mUserId;
  }
  
  public int getVolumeControl()
  {
    return this.mVolumeControlType;
  }
  
  public boolean hasFlag(int paramInt)
  {
    return (this.mFlags & paramInt) != 0L;
  }
  
  public boolean isActive()
  {
    return (this.mIsActive) && (!this.mDestroyed);
  }
  
  public boolean isPlaybackActive(boolean paramBoolean)
  {
    if (this.mPlaybackState == null) {}
    for (int i = 0; MediaSession.isActiveState(i); i = this.mPlaybackState.getState()) {
      return true;
    }
    return (paramBoolean) && (i == 2) && (SystemClock.uptimeMillis() - this.mLastActiveTime < 30000L);
  }
  
  public boolean isSystemPriority()
  {
    return (this.mFlags & 0x10000) != 0L;
  }
  
  public boolean isTransportControlEnabled()
  {
    return hasFlag(2);
  }
  
  public void onDestroy()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mDestroyed;
      if (bool) {
        return;
      }
      this.mDestroyed = true;
      this.mHandler.post(9);
      return;
    }
  }
  
  public void sendMediaButton(KeyEvent paramKeyEvent, int paramInt1, ResultReceiver paramResultReceiver, int paramInt2, String paramString)
  {
    updateCallingPackage(paramInt2, paramString);
    this.mSessionCb.sendMediaButton(paramKeyEvent, paramInt1, paramResultReceiver);
  }
  
  public void setVolumeTo(int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    if (this.mVolumeType == 1)
    {
      int i = AudioAttributes.toLegacyStreamType(this.mAudioAttrs);
      this.mAudioManagerInternal.setStreamVolumeForUid(i, paramInt1, paramInt2, paramString, paramInt3);
      return;
    }
    if (this.mVolumeControlType != 2) {
      return;
    }
    paramInt3 = Math.max(0, Math.min(paramInt1, this.mMaxVolume));
    this.mSessionCb.setVolumeTo(paramInt3);
    if (this.mOptimisticVolume < 0) {}
    for (paramInt1 = this.mCurrentVolume;; paramInt1 = this.mOptimisticVolume)
    {
      this.mOptimisticVolume = Math.max(0, Math.min(paramInt3, this.mMaxVolume));
      this.mHandler.removeCallbacks(this.mClearOptimisticVolumeRunnable);
      this.mHandler.postDelayed(this.mClearOptimisticVolumeRunnable, 1000L);
      if (paramInt1 != this.mOptimisticVolume) {
        pushVolumeUpdate();
      }
      this.mService.notifyRemoteVolumeChanged(paramInt2, this);
      if (!DEBUG) {
        break;
      }
      Log.d("MediaSessionRecord", "Set optimistic volume to " + this.mOptimisticVolume + " max is " + this.mMaxVolume);
      return;
    }
  }
  
  public String toString()
  {
    return this.mPackageName + "/" + this.mTag + " (uid=" + this.mUserId + ")";
  }
  
  class ControllerStub
    extends ISessionController.Stub
  {
    ControllerStub() {}
    
    public void adjustVolume(int paramInt1, int paramInt2, String paramString)
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      int i = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        MediaSessionRecord.this.adjustVolume(paramInt1, paramInt2, paramString, i, false);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void fastForward()
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).fastForward();
    }
    
    public Bundle getExtras()
    {
      synchronized (MediaSessionRecord.-get13(MediaSessionRecord.this))
      {
        Bundle localBundle = MediaSessionRecord.-get9(MediaSessionRecord.this);
        return localBundle;
      }
    }
    
    public long getFlags()
    {
      return MediaSessionRecord.-get10(MediaSessionRecord.this);
    }
    
    public PendingIntent getLaunchPendingIntent()
    {
      return MediaSessionRecord.-get12(MediaSessionRecord.this);
    }
    
    public MediaMetadata getMetadata()
    {
      synchronized (MediaSessionRecord.-get13(MediaSessionRecord.this))
      {
        MediaMetadata localMediaMetadata = MediaSessionRecord.-get15(MediaSessionRecord.this);
        return localMediaMetadata;
      }
    }
    
    public String getPackageName()
    {
      return MediaSessionRecord.-get17(MediaSessionRecord.this);
    }
    
    public PlaybackState getPlaybackState()
    {
      return MediaSessionRecord.-wrap0(MediaSessionRecord.this);
    }
    
    public ParceledListSlice getQueue()
    {
      synchronized (MediaSessionRecord.-get13(MediaSessionRecord.this))
      {
        ParceledListSlice localParceledListSlice = MediaSessionRecord.-get19(MediaSessionRecord.this);
        return localParceledListSlice;
      }
    }
    
    public CharSequence getQueueTitle()
    {
      return MediaSessionRecord.-get20(MediaSessionRecord.this);
    }
    
    public int getRatingType()
    {
      return MediaSessionRecord.-get21(MediaSessionRecord.this);
    }
    
    public void getRemoteControlClientNowPlayingEntries()
      throws RemoteException
    {
      Log.d("MediaSessionRecord", "getRemoteControlClientNowPlayingEntries in ControllerStub");
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).getRemoteControlClientNowPlayingEntries();
    }
    
    public String getTag()
    {
      return MediaSessionRecord.-get24(MediaSessionRecord.this);
    }
    
    public ParcelableVolumeInfo getVolumeAttributes()
    {
      synchronized (MediaSessionRecord.-get13(MediaSessionRecord.this))
      {
        if (MediaSessionRecord.-get26(MediaSessionRecord.this) == 2)
        {
          if (MediaSessionRecord.-get16(MediaSessionRecord.this) != -1) {}
          for (i = MediaSessionRecord.-get16(MediaSessionRecord.this);; i = MediaSessionRecord.-get7(MediaSessionRecord.this))
          {
            localObject2 = new ParcelableVolumeInfo(MediaSessionRecord.-get26(MediaSessionRecord.this), MediaSessionRecord.-get1(MediaSessionRecord.this), MediaSessionRecord.-get25(MediaSessionRecord.this), MediaSessionRecord.-get14(MediaSessionRecord.this), i);
            return (ParcelableVolumeInfo)localObject2;
          }
        }
        int i = MediaSessionRecord.-get26(MediaSessionRecord.this);
        Object localObject2 = MediaSessionRecord.-get1(MediaSessionRecord.this);
        int j = AudioAttributes.toLegacyStreamType((AudioAttributes)localObject2);
        return new ParcelableVolumeInfo(i, (AudioAttributes)localObject2, 2, MediaSessionRecord.-get2(MediaSessionRecord.this).getStreamMaxVolume(j), MediaSessionRecord.-get2(MediaSessionRecord.this).getStreamVolume(j));
      }
    }
    
    public boolean isTransportControlEnabled()
    {
      return MediaSessionRecord.this.isTransportControlEnabled();
    }
    
    public void next()
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).next();
    }
    
    public void pause()
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).pause();
    }
    
    public void play()
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).play();
    }
    
    public void playFromMediaId(String paramString, Bundle paramBundle)
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).playFromMediaId(paramString, paramBundle);
    }
    
    public void playFromSearch(String paramString, Bundle paramBundle)
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).playFromSearch(paramString, paramBundle);
    }
    
    public void playFromUri(Uri paramUri, Bundle paramBundle)
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).playFromUri(paramUri, paramBundle);
    }
    
    public void prepare()
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).prepare();
    }
    
    public void prepareFromMediaId(String paramString, Bundle paramBundle)
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).prepareFromMediaId(paramString, paramBundle);
    }
    
    public void prepareFromSearch(String paramString, Bundle paramBundle)
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).prepareFromSearch(paramString, paramBundle);
    }
    
    public void prepareFromUri(Uri paramUri, Bundle paramBundle)
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).prepareFromUri(paramUri, paramBundle);
    }
    
    public void previous()
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).previous();
    }
    
    public void rate(Rating paramRating)
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).rate(paramRating);
    }
    
    public void registerCallbackListener(ISessionControllerCallback paramISessionControllerCallback)
    {
      synchronized (MediaSessionRecord.-get13(MediaSessionRecord.this))
      {
        boolean bool = MediaSessionRecord.-get8(MediaSessionRecord.this);
        if (bool) {
          try
          {
            paramISessionControllerCallback.onSessionDestroyed();
            return;
          }
          catch (Exception paramISessionControllerCallback)
          {
            for (;;) {}
          }
        }
        if (MediaSessionRecord.-wrap1(MediaSessionRecord.this, paramISessionControllerCallback) < 0)
        {
          MediaSessionRecord.-get6(MediaSessionRecord.this).add(paramISessionControllerCallback);
          if (MediaSessionRecord.-get0()) {
            Log.d("MediaSessionRecord", "registering controller callback " + paramISessionControllerCallback);
          }
        }
        return;
      }
    }
    
    public void rewind()
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).rewind();
    }
    
    public void seekTo(long paramLong)
      throws RemoteException
    {
      Log.d("MediaSessionRecord", "seekTo in ControllerStub");
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).seekTo(paramLong);
    }
    
    public void sendCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).sendCommand(paramString, paramBundle, paramResultReceiver);
    }
    
    public void sendCustomAction(String paramString, Bundle paramBundle)
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).sendCustomAction(paramString, paramBundle);
    }
    
    public boolean sendMediaButton(KeyEvent paramKeyEvent)
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      return MediaSessionRecord.-get23(MediaSessionRecord.this).sendMediaButton(paramKeyEvent, 0, null);
    }
    
    public void setRemoteControlClientBrowsedPlayer()
      throws RemoteException
    {
      Log.d("MediaSessionRecord", "setRemoteControlClientBrowsedPlayer in ControllerStub");
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).setRemoteControlClientBrowsedPlayer();
    }
    
    public void setRemoteControlClientPlayItem(long paramLong, int paramInt)
      throws RemoteException
    {
      Log.d("MediaSessionRecord", "setRemoteControlClientPlayItem in ControllerStub");
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).setRemoteControlClientPlayItem(paramLong, paramInt);
    }
    
    public void setVolumeTo(int paramInt1, int paramInt2, String paramString)
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      int i = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        MediaSessionRecord.this.setVolumeTo(paramInt1, paramInt2, paramString, i);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void skipToQueueItem(long paramLong)
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).skipToTrack(paramLong);
    }
    
    public void stop()
      throws RemoteException
    {
      MediaSessionRecord.-wrap14(MediaSessionRecord.this);
      MediaSessionRecord.-get23(MediaSessionRecord.this).stop();
    }
    
    public void unregisterCallbackListener(ISessionControllerCallback paramISessionControllerCallback)
      throws RemoteException
    {
      synchronized (MediaSessionRecord.-get13(MediaSessionRecord.this))
      {
        int i = MediaSessionRecord.-wrap1(MediaSessionRecord.this, paramISessionControllerCallback);
        if (i != -1) {
          MediaSessionRecord.-get6(MediaSessionRecord.this).remove(i);
        }
        if (MediaSessionRecord.-get0()) {
          Log.d("MediaSessionRecord", "unregistering callback " + paramISessionControllerCallback + ". index=" + i);
        }
        return;
      }
    }
  }
  
  private class MessageHandler
    extends Handler
  {
    private static final int MSG_DESTROYED = 9;
    private static final int MSG_FOLDER_INFO_BROWSED_PLAYER = 10;
    private static final int MSG_PLAY_ITEM_RESPONSE = 13;
    private static final int MSG_SEND_EVENT = 6;
    private static final int MSG_UPDATE_EXTRAS = 5;
    private static final int MSG_UPDATE_METADATA = 1;
    private static final int MSG_UPDATE_NOWPLAYING_CONTENT_CHANGE = 12;
    private static final int MSG_UPDATE_NOWPLAYING_ENTRIES = 11;
    private static final int MSG_UPDATE_PLAYBACK_STATE = 2;
    private static final int MSG_UPDATE_QUEUE = 3;
    private static final int MSG_UPDATE_QUEUE_TITLE = 4;
    private static final int MSG_UPDATE_SESSION_STATE = 7;
    private static final int MSG_UPDATE_VOLUME = 8;
    
    public MessageHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      case 7: 
      default: 
        return;
      case 1: 
        MediaSessionRecord.-wrap5(MediaSessionRecord.this);
        return;
      case 2: 
        MediaSessionRecord.-wrap9(MediaSessionRecord.this);
        return;
      case 3: 
        MediaSessionRecord.-wrap11(MediaSessionRecord.this);
        return;
      case 4: 
        MediaSessionRecord.-wrap10(MediaSessionRecord.this);
        return;
      case 5: 
        MediaSessionRecord.-wrap4(MediaSessionRecord.this);
        return;
      case 6: 
        MediaSessionRecord.-wrap3(MediaSessionRecord.this, (String)paramMessage.obj, paramMessage.getData());
        return;
      case 8: 
        MediaSessionRecord.-wrap13(MediaSessionRecord.this);
        return;
      case 9: 
        MediaSessionRecord.-wrap12(MediaSessionRecord.this);
      case 10: 
        MediaSessionRecord.-wrap2(MediaSessionRecord.this);
        return;
      case 11: 
        MediaSessionRecord.-wrap7(MediaSessionRecord.this);
        return;
      case 12: 
        MediaSessionRecord.-wrap6(MediaSessionRecord.this);
        return;
      }
      MediaSessionRecord.-wrap8(MediaSessionRecord.this);
    }
    
    public void post(int paramInt)
    {
      post(paramInt, null);
    }
    
    public void post(int paramInt, Object paramObject)
    {
      obtainMessage(paramInt, paramObject).sendToTarget();
    }
    
    public void post(int paramInt, Object paramObject, Bundle paramBundle)
    {
      paramObject = obtainMessage(paramInt, paramObject);
      ((Message)paramObject).setData(paramBundle);
      ((Message)paramObject).sendToTarget();
    }
  }
  
  class SessionCb
  {
    private final ISessionCallback mCb;
    
    public SessionCb(ISessionCallback paramISessionCallback)
    {
      this.mCb = paramISessionCallback;
    }
    
    public void adjustVolume(int paramInt)
    {
      try
      {
        this.mCb.onAdjustVolume(paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in adjustVolume.", localRemoteException);
      }
    }
    
    public void fastForward()
    {
      try
      {
        this.mCb.onFastForward();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in fastForward.", localRemoteException);
      }
    }
    
    public void getRemoteControlClientNowPlayingEntries()
      throws RemoteException
    {
      Slog.d("MediaSessionRecord", "getRemoteControlClientNowPlayingEntries in SessionCb");
      try
      {
        this.mCb.getRemoteControlClientNowPlayingEntries();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in getRemoteControlClientNowPlayingEntries.", localRemoteException);
      }
    }
    
    public void next()
    {
      try
      {
        this.mCb.onNext();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in next.", localRemoteException);
      }
    }
    
    public void pause()
    {
      try
      {
        this.mCb.onPause();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in pause.", localRemoteException);
      }
    }
    
    public void play()
    {
      try
      {
        this.mCb.onPlay();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in play.", localRemoteException);
      }
    }
    
    public void playFromMediaId(String paramString, Bundle paramBundle)
    {
      try
      {
        this.mCb.onPlayFromMediaId(paramString, paramBundle);
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.e("MediaSessionRecord", "Remote failure in playFromMediaId.", paramString);
      }
    }
    
    public void playFromSearch(String paramString, Bundle paramBundle)
    {
      try
      {
        this.mCb.onPlayFromSearch(paramString, paramBundle);
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.e("MediaSessionRecord", "Remote failure in playFromSearch.", paramString);
      }
    }
    
    public void playFromUri(Uri paramUri, Bundle paramBundle)
    {
      try
      {
        this.mCb.onPlayFromUri(paramUri, paramBundle);
        return;
      }
      catch (RemoteException paramUri)
      {
        Slog.e("MediaSessionRecord", "Remote failure in playFromUri.", paramUri);
      }
    }
    
    public void prepare()
    {
      try
      {
        this.mCb.onPrepare();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in prepare.", localRemoteException);
      }
    }
    
    public void prepareFromMediaId(String paramString, Bundle paramBundle)
    {
      try
      {
        this.mCb.onPrepareFromMediaId(paramString, paramBundle);
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.e("MediaSessionRecord", "Remote failure in prepareFromMediaId.", paramString);
      }
    }
    
    public void prepareFromSearch(String paramString, Bundle paramBundle)
    {
      try
      {
        this.mCb.onPrepareFromSearch(paramString, paramBundle);
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.e("MediaSessionRecord", "Remote failure in prepareFromSearch.", paramString);
      }
    }
    
    public void prepareFromUri(Uri paramUri, Bundle paramBundle)
    {
      try
      {
        this.mCb.onPrepareFromUri(paramUri, paramBundle);
        return;
      }
      catch (RemoteException paramUri)
      {
        Slog.e("MediaSessionRecord", "Remote failure in prepareFromUri.", paramUri);
      }
    }
    
    public void previous()
    {
      try
      {
        this.mCb.onPrevious();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in previous.", localRemoteException);
      }
    }
    
    public void rate(Rating paramRating)
    {
      try
      {
        this.mCb.onRate(paramRating);
        return;
      }
      catch (RemoteException paramRating)
      {
        Slog.e("MediaSessionRecord", "Remote failure in rate.", paramRating);
      }
    }
    
    public void rewind()
    {
      try
      {
        this.mCb.onRewind();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in rewind.", localRemoteException);
      }
    }
    
    public void seekTo(long paramLong)
    {
      Slog.d("MediaSessionRecord", "seekTo in SessionCb");
      try
      {
        this.mCb.onSeekTo(paramLong);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in seekTo.", localRemoteException);
      }
    }
    
    public void sendCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
    {
      try
      {
        this.mCb.onCommand(paramString, paramBundle, paramResultReceiver);
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.e("MediaSessionRecord", "Remote failure in sendCommand.", paramString);
      }
    }
    
    public void sendCustomAction(String paramString, Bundle paramBundle)
    {
      try
      {
        this.mCb.onCustomAction(paramString, paramBundle);
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.e("MediaSessionRecord", "Remote failure in sendCustomAction.", paramString);
      }
    }
    
    public boolean sendMediaButton(KeyEvent paramKeyEvent, int paramInt, ResultReceiver paramResultReceiver)
    {
      Intent localIntent = new Intent("android.intent.action.MEDIA_BUTTON");
      localIntent.putExtra("android.intent.extra.KEY_EVENT", paramKeyEvent);
      try
      {
        this.mCb.onMediaButton(localIntent, paramInt, paramResultReceiver);
        return true;
      }
      catch (RemoteException paramKeyEvent)
      {
        Slog.e("MediaSessionRecord", "Remote failure in sendMediaRequest.", paramKeyEvent);
      }
      return false;
    }
    
    public void setRemoteControlClientBrowsedPlayer()
    {
      Slog.d("MediaSessionRecord", "setRemoteControlClientBrowsedPlayer in SessionCb");
      try
      {
        this.mCb.setRemoteControlClientBrowsedPlayer();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in setRemoteControlClientBrowsedPlayer.", localRemoteException);
      }
    }
    
    public void setRemoteControlClientPlayItem(long paramLong, int paramInt)
      throws RemoteException
    {
      Slog.d("MediaSessionRecord", "setRemoteControlClientPlayItem in SessionCb");
      try
      {
        this.mCb.setRemoteControlClientPlayItem(paramLong, paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in setRemoteControlClientPlayItem.", localRemoteException);
      }
    }
    
    public void setVolumeTo(int paramInt)
    {
      try
      {
        this.mCb.onSetVolumeTo(paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in setVolumeTo.", localRemoteException);
      }
    }
    
    public void skipToTrack(long paramLong)
    {
      try
      {
        this.mCb.onSkipToTrack(paramLong);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in skipToTrack", localRemoteException);
      }
    }
    
    public void stop()
    {
      try
      {
        this.mCb.onStop();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("MediaSessionRecord", "Remote failure in stop.", localRemoteException);
      }
    }
  }
  
  private final class SessionStub
    extends ISession.Stub
  {
    private SessionStub() {}
    
    public void destroy()
    {
      MediaSessionRecord.-get22(MediaSessionRecord.this).destroySession(MediaSessionRecord.this);
    }
    
    public String getCallingPackage()
    {
      return MediaSessionRecord.-get4(MediaSessionRecord.this);
    }
    
    public ISessionController getController()
    {
      return MediaSessionRecord.-get5(MediaSessionRecord.this);
    }
    
    public void playItemResponse(boolean paramBoolean)
    {
      Log.d("MediaSessionRecord", "SessionStub: playItemResponse");
      MediaSessionRecord.-set13(MediaSessionRecord.this, paramBoolean);
      MediaSessionRecord.-get11(MediaSessionRecord.this).post(13);
    }
    
    public void sendEvent(String paramString, Bundle paramBundle)
    {
      Object localObject = null;
      MediaSessionRecord.MessageHandler localMessageHandler = MediaSessionRecord.-get11(MediaSessionRecord.this);
      if (paramBundle == null) {}
      for (paramBundle = (Bundle)localObject;; paramBundle = new Bundle(paramBundle))
      {
        localMessageHandler.post(6, paramString, paramBundle);
        return;
      }
    }
    
    public void setActive(boolean paramBoolean)
    {
      MediaSessionRecord.-set5(MediaSessionRecord.this, paramBoolean);
      MediaSessionRecord.-get22(MediaSessionRecord.this).updateSession(MediaSessionRecord.this);
      MediaSessionRecord.-get11(MediaSessionRecord.this).post(7);
    }
    
    public void setCurrentVolume(int paramInt)
    {
      MediaSessionRecord.-set2(MediaSessionRecord.this, paramInt);
      MediaSessionRecord.-get11(MediaSessionRecord.this).post(8);
    }
    
    public void setExtras(Bundle paramBundle)
    {
      Object localObject1 = null;
      synchronized (MediaSessionRecord.-get13(MediaSessionRecord.this))
      {
        MediaSessionRecord localMediaSessionRecord = MediaSessionRecord.this;
        if (paramBundle == null)
        {
          paramBundle = (Bundle)localObject1;
          MediaSessionRecord.-set3(localMediaSessionRecord, paramBundle);
          MediaSessionRecord.-get11(MediaSessionRecord.this).post(5);
          return;
        }
        paramBundle = new Bundle(paramBundle);
      }
    }
    
    public void setFlags(int paramInt)
    {
      if ((0x10000 & paramInt) != 0)
      {
        int i = getCallingPid();
        int j = getCallingUid();
        MediaSessionRecord.-get22(MediaSessionRecord.this).enforcePhoneStatePermission(i, j);
      }
      MediaSessionRecord.-set4(MediaSessionRecord.this, paramInt);
      MediaSessionRecord.-get11(MediaSessionRecord.this).post(7);
    }
    
    public void setLaunchPendingIntent(PendingIntent paramPendingIntent)
    {
      MediaSessionRecord.-set7(MediaSessionRecord.this, paramPendingIntent);
    }
    
    public void setMediaButtonReceiver(PendingIntent paramPendingIntent)
    {
      MediaSessionRecord.-set9(MediaSessionRecord.this, paramPendingIntent);
    }
    
    public void setMetadata(MediaMetadata paramMediaMetadata)
    {
      localObject = MediaSessionRecord.-get13(MediaSessionRecord.this);
      if (paramMediaMetadata == null) {}
      for (paramMediaMetadata = null;; paramMediaMetadata = new MediaMetadata.Builder(paramMediaMetadata).build())
      {
        if (paramMediaMetadata != null) {}
        try
        {
          paramMediaMetadata.size();
          MediaSessionRecord.-set10(MediaSessionRecord.this, paramMediaMetadata);
          MediaSessionRecord.-get11(MediaSessionRecord.this).post(1);
          return;
        }
        finally {}
      }
    }
    
    public void setPlaybackState(PlaybackState paramPlaybackState)
    {
      int i;
      if (MediaSessionRecord.-get18(MediaSessionRecord.this) == null) {
        i = 0;
      }
      for (;;)
      {
        int j;
        if (paramPlaybackState == null)
        {
          j = 0;
          if ((MediaSession.isActiveState(i)) && (j == 2)) {
            MediaSessionRecord.-set6(MediaSessionRecord.this, SystemClock.elapsedRealtime());
          }
        }
        synchronized (MediaSessionRecord.-get13(MediaSessionRecord.this))
        {
          MediaSessionRecord.-set14(MediaSessionRecord.this, paramPlaybackState);
          MediaSessionRecord.-get22(MediaSessionRecord.this).onSessionPlaystateChange(MediaSessionRecord.this, i, j);
          MediaSessionRecord.-get11(MediaSessionRecord.this).post(2);
          return;
          i = MediaSessionRecord.-get18(MediaSessionRecord.this).getState();
          continue;
          j = paramPlaybackState.getState();
        }
      }
    }
    
    public void setPlaybackToLocal(AudioAttributes paramAudioAttributes)
    {
      synchronized (MediaSessionRecord.-get13(MediaSessionRecord.this))
      {
        if (MediaSessionRecord.-get26(MediaSessionRecord.this) == 2) {}
        for (int i = 1;; i = 0)
        {
          MediaSessionRecord.-set19(MediaSessionRecord.this, 1);
          if (paramAudioAttributes == null) {
            break;
          }
          MediaSessionRecord.-set0(MediaSessionRecord.this, paramAudioAttributes);
          if (i != 0)
          {
            MediaSessionRecord.-get22(MediaSessionRecord.this).onSessionPlaybackTypeChanged(MediaSessionRecord.this);
            MediaSessionRecord.-get11(MediaSessionRecord.this).post(8);
          }
          return;
        }
        Log.e("MediaSessionRecord", "Received null audio attributes, using existing attributes");
      }
    }
    
    public void setPlaybackToRemote(int paramInt1, int paramInt2)
    {
      synchronized (MediaSessionRecord.-get13(MediaSessionRecord.this))
      {
        if (MediaSessionRecord.-get26(MediaSessionRecord.this) == 1)
        {
          i = 1;
          MediaSessionRecord.-set19(MediaSessionRecord.this, 2);
          MediaSessionRecord.-set18(MediaSessionRecord.this, paramInt1);
          MediaSessionRecord.-set8(MediaSessionRecord.this, paramInt2);
          if (i != 0)
          {
            MediaSessionRecord.-get22(MediaSessionRecord.this).onSessionPlaybackTypeChanged(MediaSessionRecord.this);
            MediaSessionRecord.-get11(MediaSessionRecord.this).post(8);
          }
          return;
        }
        int i = 0;
      }
    }
    
    public void setQueue(ParceledListSlice paramParceledListSlice)
    {
      synchronized (MediaSessionRecord.-get13(MediaSessionRecord.this))
      {
        MediaSessionRecord.-set15(MediaSessionRecord.this, paramParceledListSlice);
        MediaSessionRecord.-get11(MediaSessionRecord.this).post(3);
        return;
      }
    }
    
    public void setQueueTitle(CharSequence paramCharSequence)
    {
      MediaSessionRecord.-set16(MediaSessionRecord.this, paramCharSequence);
      MediaSessionRecord.-get11(MediaSessionRecord.this).post(4);
    }
    
    public void setRatingType(int paramInt)
    {
      MediaSessionRecord.-set17(MediaSessionRecord.this, paramInt);
    }
    
    public void updateFolderInfoBrowsedPlayer(String paramString)
    {
      Log.d("MediaSessionRecord", "SessionStub: updateFolderInfoBrowsedPlayer");
      MediaSessionRecord.-set1(MediaSessionRecord.this, paramString);
      MediaSessionRecord.-get11(MediaSessionRecord.this).post(10);
    }
    
    public void updateNowPlayingContentChange()
    {
      Log.d("MediaSessionRecord", "SessionStub: updateNowPlayingContentChange");
      MediaSessionRecord.-get11(MediaSessionRecord.this).post(12);
    }
    
    public void updateNowPlayingEntries(long[] paramArrayOfLong)
    {
      Log.d("MediaSessionRecord", "SessionStub: updateNowPlayingEntries");
      MediaSessionRecord.-set11(MediaSessionRecord.this, paramArrayOfLong);
      MediaSessionRecord.-get11(MediaSessionRecord.this).post(11);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/media/MediaSessionRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */