package android.support.v4.media.session;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.VolumeProviderCompat.Callback;
import android.text.TextUtils;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MediaSessionCompat
{
  public static final int FLAG_HANDLES_MEDIA_BUTTONS = 1;
  public static final int FLAG_HANDLES_TRANSPORT_CONTROLS = 2;
  private final ArrayList<OnActiveChangeListener> mActiveListeners = new ArrayList();
  private final MediaControllerCompat mController;
  private final MediaSessionImpl mImpl;
  
  private MediaSessionCompat(Context paramContext, MediaSessionImpl paramMediaSessionImpl)
  {
    this.mImpl = paramMediaSessionImpl;
    this.mController = new MediaControllerCompat(paramContext, this);
  }
  
  public MediaSessionCompat(Context paramContext, String paramString, ComponentName paramComponentName, PendingIntent paramPendingIntent)
  {
    if (paramContext != null)
    {
      if (TextUtils.isEmpty(paramString)) {
        break label74;
      }
      if (Build.VERSION.SDK_INT >= 21) {
        break label84;
      }
      this.mImpl = new MediaSessionImplBase(paramContext, paramString, paramComponentName, paramPendingIntent);
    }
    for (;;)
    {
      this.mController = new MediaControllerCompat(paramContext, this);
      return;
      throw new IllegalArgumentException("context must not be null");
      label74:
      throw new IllegalArgumentException("tag must not be null or empty");
      label84:
      this.mImpl = new MediaSessionImplApi21(paramContext, paramString);
      this.mImpl.setMediaButtonReceiver(paramPendingIntent);
    }
  }
  
  public static MediaSessionCompat obtain(Context paramContext, Object paramObject)
  {
    return new MediaSessionCompat(paramContext, new MediaSessionImplApi21(paramObject));
  }
  
  public void addOnActiveChangeListener(OnActiveChangeListener paramOnActiveChangeListener)
  {
    if (paramOnActiveChangeListener != null)
    {
      this.mActiveListeners.add(paramOnActiveChangeListener);
      return;
    }
    throw new IllegalArgumentException("Listener may not be null");
  }
  
  public MediaControllerCompat getController()
  {
    return this.mController;
  }
  
  public Object getMediaSession()
  {
    return this.mImpl.getMediaSession();
  }
  
  public Object getRemoteControlClient()
  {
    return this.mImpl.getRemoteControlClient();
  }
  
  public Token getSessionToken()
  {
    return this.mImpl.getSessionToken();
  }
  
  public boolean isActive()
  {
    return this.mImpl.isActive();
  }
  
  public void release()
  {
    this.mImpl.release();
  }
  
  public void removeOnActiveChangeListener(OnActiveChangeListener paramOnActiveChangeListener)
  {
    if (paramOnActiveChangeListener != null)
    {
      this.mActiveListeners.remove(paramOnActiveChangeListener);
      return;
    }
    throw new IllegalArgumentException("Listener may not be null");
  }
  
  public void sendSessionEvent(String paramString, Bundle paramBundle)
  {
    if (!TextUtils.isEmpty(paramString))
    {
      this.mImpl.sendSessionEvent(paramString, paramBundle);
      return;
    }
    throw new IllegalArgumentException("event cannot be null or empty");
  }
  
  public void setActive(boolean paramBoolean)
  {
    this.mImpl.setActive(paramBoolean);
    Iterator localIterator = this.mActiveListeners.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      ((OnActiveChangeListener)localIterator.next()).onActiveChanged();
    }
  }
  
  public void setCallback(Callback paramCallback)
  {
    setCallback(paramCallback, null);
  }
  
  public void setCallback(Callback paramCallback, Handler paramHandler)
  {
    MediaSessionImpl localMediaSessionImpl = this.mImpl;
    Handler localHandler = paramHandler;
    if (paramHandler == null) {
      localHandler = new Handler();
    }
    localMediaSessionImpl.setCallback(paramCallback, localHandler);
  }
  
  public void setExtras(Bundle paramBundle)
  {
    this.mImpl.setExtras(paramBundle);
  }
  
  public void setFlags(int paramInt)
  {
    this.mImpl.setFlags(paramInt);
  }
  
  public void setMediaButtonReceiver(PendingIntent paramPendingIntent)
  {
    this.mImpl.setMediaButtonReceiver(paramPendingIntent);
  }
  
  public void setMetadata(MediaMetadataCompat paramMediaMetadataCompat)
  {
    this.mImpl.setMetadata(paramMediaMetadataCompat);
  }
  
  public void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat)
  {
    this.mImpl.setPlaybackState(paramPlaybackStateCompat);
  }
  
  public void setPlaybackToLocal(int paramInt)
  {
    this.mImpl.setPlaybackToLocal(paramInt);
  }
  
  public void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat)
  {
    if (paramVolumeProviderCompat != null)
    {
      this.mImpl.setPlaybackToRemote(paramVolumeProviderCompat);
      return;
    }
    throw new IllegalArgumentException("volumeProvider may not be null!");
  }
  
  public void setQueue(List<QueueItem> paramList)
  {
    this.mImpl.setQueue(paramList);
  }
  
  public void setQueueTitle(CharSequence paramCharSequence)
  {
    this.mImpl.setQueueTitle(paramCharSequence);
  }
  
  public void setRatingType(int paramInt)
  {
    this.mImpl.setRatingType(paramInt);
  }
  
  public void setSessionActivity(PendingIntent paramPendingIntent)
  {
    this.mImpl.setSessionActivity(paramPendingIntent);
  }
  
  public static abstract class Callback
  {
    final Object mCallbackObj;
    
    public Callback()
    {
      if (Build.VERSION.SDK_INT < 21)
      {
        this.mCallbackObj = null;
        return;
      }
      this.mCallbackObj = MediaSessionCompatApi21.createCallback(new StubApi21(null));
    }
    
    public void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver) {}
    
    public void onCustomAction(String paramString, Bundle paramBundle) {}
    
    public void onFastForward() {}
    
    public boolean onMediaButtonEvent(Intent paramIntent)
    {
      return false;
    }
    
    public void onPause() {}
    
    public void onPlay() {}
    
    public void onPlayFromMediaId(String paramString, Bundle paramBundle) {}
    
    public void onPlayFromSearch(String paramString, Bundle paramBundle) {}
    
    public void onRewind() {}
    
    public void onSeekTo(long paramLong) {}
    
    public void onSetRating(RatingCompat paramRatingCompat) {}
    
    public void onSkipToNext() {}
    
    public void onSkipToPrevious() {}
    
    public void onSkipToQueueItem(long paramLong) {}
    
    public void onStop() {}
    
    private class StubApi21
      implements MediaSessionCompatApi21.Callback
    {
      private StubApi21() {}
      
      public void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
      {
        MediaSessionCompat.Callback.this.onCommand(paramString, paramBundle, paramResultReceiver);
      }
      
      public void onCustomAction(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onCustomAction(paramString, paramBundle);
      }
      
      public void onFastForward()
      {
        MediaSessionCompat.Callback.this.onFastForward();
      }
      
      public boolean onMediaButtonEvent(Intent paramIntent)
      {
        return MediaSessionCompat.Callback.this.onMediaButtonEvent(paramIntent);
      }
      
      public void onPause()
      {
        MediaSessionCompat.Callback.this.onPause();
      }
      
      public void onPlay()
      {
        MediaSessionCompat.Callback.this.onPlay();
      }
      
      public void onPlayFromMediaId(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPlayFromMediaId(paramString, paramBundle);
      }
      
      public void onPlayFromSearch(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPlayFromSearch(paramString, paramBundle);
      }
      
      public void onRewind()
      {
        MediaSessionCompat.Callback.this.onRewind();
      }
      
      public void onSeekTo(long paramLong)
      {
        MediaSessionCompat.Callback.this.onSeekTo(paramLong);
      }
      
      public void onSetRating(Object paramObject)
      {
        MediaSessionCompat.Callback.this.onSetRating(RatingCompat.fromRating(paramObject));
      }
      
      public void onSkipToNext()
      {
        MediaSessionCompat.Callback.this.onSkipToNext();
      }
      
      public void onSkipToPrevious()
      {
        MediaSessionCompat.Callback.this.onSkipToPrevious();
      }
      
      public void onSkipToQueueItem(long paramLong)
      {
        MediaSessionCompat.Callback.this.onSkipToQueueItem(paramLong);
      }
      
      public void onStop()
      {
        MediaSessionCompat.Callback.this.onStop();
      }
    }
  }
  
  static abstract interface MediaSessionImpl
  {
    public abstract Object getMediaSession();
    
    public abstract Object getRemoteControlClient();
    
    public abstract MediaSessionCompat.Token getSessionToken();
    
    public abstract boolean isActive();
    
    public abstract void release();
    
    public abstract void sendSessionEvent(String paramString, Bundle paramBundle);
    
    public abstract void setActive(boolean paramBoolean);
    
    public abstract void setCallback(MediaSessionCompat.Callback paramCallback, Handler paramHandler);
    
    public abstract void setExtras(Bundle paramBundle);
    
    public abstract void setFlags(int paramInt);
    
    public abstract void setMediaButtonReceiver(PendingIntent paramPendingIntent);
    
    public abstract void setMetadata(MediaMetadataCompat paramMediaMetadataCompat);
    
    public abstract void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat);
    
    public abstract void setPlaybackToLocal(int paramInt);
    
    public abstract void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat);
    
    public abstract void setQueue(List<MediaSessionCompat.QueueItem> paramList);
    
    public abstract void setQueueTitle(CharSequence paramCharSequence);
    
    public abstract void setRatingType(int paramInt);
    
    public abstract void setSessionActivity(PendingIntent paramPendingIntent);
  }
  
  static class MediaSessionImplApi21
    implements MediaSessionCompat.MediaSessionImpl
  {
    private PendingIntent mMediaButtonIntent;
    private final Object mSessionObj;
    private final MediaSessionCompat.Token mToken;
    
    public MediaSessionImplApi21(Context paramContext, String paramString)
    {
      this.mSessionObj = MediaSessionCompatApi21.createSession(paramContext, paramString);
      this.mToken = new MediaSessionCompat.Token(MediaSessionCompatApi21.getSessionToken(this.mSessionObj));
    }
    
    public MediaSessionImplApi21(Object paramObject)
    {
      this.mSessionObj = MediaSessionCompatApi21.verifySession(paramObject);
      this.mToken = new MediaSessionCompat.Token(MediaSessionCompatApi21.getSessionToken(this.mSessionObj));
    }
    
    public Object getMediaSession()
    {
      return this.mSessionObj;
    }
    
    public Object getRemoteControlClient()
    {
      return null;
    }
    
    public MediaSessionCompat.Token getSessionToken()
    {
      return this.mToken;
    }
    
    public boolean isActive()
    {
      return MediaSessionCompatApi21.isActive(this.mSessionObj);
    }
    
    public void release()
    {
      MediaSessionCompatApi21.release(this.mSessionObj);
    }
    
    public void sendSessionEvent(String paramString, Bundle paramBundle)
    {
      MediaSessionCompatApi21.sendSessionEvent(this.mSessionObj, paramString, paramBundle);
    }
    
    public void setActive(boolean paramBoolean)
    {
      MediaSessionCompatApi21.setActive(this.mSessionObj, paramBoolean);
    }
    
    public void setCallback(MediaSessionCompat.Callback paramCallback, Handler paramHandler)
    {
      MediaSessionCompatApi21.setCallback(this.mSessionObj, paramCallback.mCallbackObj, paramHandler);
    }
    
    public void setExtras(Bundle paramBundle)
    {
      MediaSessionCompatApi21.setExtras(this.mSessionObj, paramBundle);
    }
    
    public void setFlags(int paramInt)
    {
      MediaSessionCompatApi21.setFlags(this.mSessionObj, paramInt);
    }
    
    public void setMediaButtonReceiver(PendingIntent paramPendingIntent)
    {
      this.mMediaButtonIntent = paramPendingIntent;
      MediaSessionCompatApi21.setMediaButtonReceiver(this.mSessionObj, paramPendingIntent);
    }
    
    public void setMetadata(MediaMetadataCompat paramMediaMetadataCompat)
    {
      MediaSessionCompatApi21.setMetadata(this.mSessionObj, paramMediaMetadataCompat.getMediaMetadata());
    }
    
    public void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      MediaSessionCompatApi21.setPlaybackState(this.mSessionObj, paramPlaybackStateCompat.getPlaybackState());
    }
    
    public void setPlaybackToLocal(int paramInt)
    {
      MediaSessionCompatApi21.setPlaybackToLocal(this.mSessionObj, paramInt);
    }
    
    public void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat)
    {
      MediaSessionCompatApi21.setPlaybackToRemote(this.mSessionObj, paramVolumeProviderCompat.getVolumeProvider());
    }
    
    public void setQueue(List<MediaSessionCompat.QueueItem> paramList)
    {
      ArrayList localArrayList = null;
      if (paramList == null)
      {
        paramList = localArrayList;
        MediaSessionCompatApi21.setQueue(this.mSessionObj, paramList);
        return;
      }
      localArrayList = new ArrayList();
      paramList = paramList.iterator();
      for (;;)
      {
        if (!paramList.hasNext())
        {
          paramList = localArrayList;
          break;
        }
        localArrayList.add(((MediaSessionCompat.QueueItem)paramList.next()).getQueueItem());
      }
    }
    
    public void setQueueTitle(CharSequence paramCharSequence)
    {
      MediaSessionCompatApi21.setQueueTitle(this.mSessionObj, paramCharSequence);
    }
    
    public void setRatingType(int paramInt)
    {
      if (Build.VERSION.SDK_INT >= 22) {
        MediaSessionCompatApi22.setRatingType(this.mSessionObj, paramInt);
      }
    }
    
    public void setSessionActivity(PendingIntent paramPendingIntent)
    {
      MediaSessionCompatApi21.setSessionActivity(this.mSessionObj, paramPendingIntent);
    }
  }
  
  static class MediaSessionImplBase
    implements MediaSessionCompat.MediaSessionImpl
  {
    private final AudioManager mAudioManager;
    private MediaSessionCompat.Callback mCallback;
    private final ComponentName mComponentName;
    private final Context mContext;
    private final RemoteCallbackList<IMediaControllerCallback> mControllerCallbacks = new RemoteCallbackList();
    private boolean mDestroyed = false;
    private Bundle mExtras;
    private int mFlags;
    private final MessageHandler mHandler;
    private boolean mIsActive = false;
    private boolean mIsMbrRegistered = false;
    private boolean mIsRccRegistered = false;
    private int mLocalStream;
    private final Object mLock = new Object();
    private final PendingIntent mMediaButtonEventReceiver;
    private MediaMetadataCompat mMetadata;
    private final String mPackageName;
    private List<MediaSessionCompat.QueueItem> mQueue;
    private CharSequence mQueueTitle;
    private int mRatingType;
    private final Object mRccObj;
    private PendingIntent mSessionActivity;
    private PlaybackStateCompat mState;
    private final MediaSessionStub mStub;
    private final String mTag;
    private final MediaSessionCompat.Token mToken;
    private VolumeProviderCompat.Callback mVolumeCallback = new VolumeProviderCompat.Callback()
    {
      public void onVolumeChanged(VolumeProviderCompat paramAnonymousVolumeProviderCompat)
      {
        if (MediaSessionCompat.MediaSessionImplBase.this.mVolumeProvider == paramAnonymousVolumeProviderCompat)
        {
          paramAnonymousVolumeProviderCompat = new ParcelableVolumeInfo(MediaSessionCompat.MediaSessionImplBase.this.mVolumeType, MediaSessionCompat.MediaSessionImplBase.this.mLocalStream, paramAnonymousVolumeProviderCompat.getVolumeControl(), paramAnonymousVolumeProviderCompat.getMaxVolume(), paramAnonymousVolumeProviderCompat.getCurrentVolume());
          MediaSessionCompat.MediaSessionImplBase.this.sendVolumeInfoChanged(paramAnonymousVolumeProviderCompat);
          return;
        }
      }
    };
    private VolumeProviderCompat mVolumeProvider;
    private int mVolumeType;
    
    public MediaSessionImplBase(Context paramContext, String paramString, ComponentName paramComponentName, PendingIntent paramPendingIntent)
    {
      if (paramComponentName != null) {
        if (paramPendingIntent == null) {
          break label190;
        }
      }
      for (;;)
      {
        this.mContext = paramContext;
        this.mPackageName = paramContext.getPackageName();
        this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
        this.mTag = paramString;
        this.mComponentName = paramComponentName;
        this.mMediaButtonEventReceiver = paramPendingIntent;
        this.mStub = new MediaSessionStub();
        this.mToken = new MediaSessionCompat.Token(this.mStub);
        this.mHandler = new MessageHandler(Looper.myLooper());
        this.mRatingType = 0;
        this.mVolumeType = 1;
        this.mLocalStream = 3;
        if (Build.VERSION.SDK_INT >= 14) {
          break;
        }
        this.mRccObj = null;
        return;
        throw new IllegalArgumentException("MediaButtonReceiver component may not be null.");
        label190:
        paramPendingIntent = new Intent("android.intent.action.MEDIA_BUTTON");
        paramPendingIntent.setComponent(paramComponentName);
        paramPendingIntent = PendingIntent.getBroadcast(paramContext, 0, paramPendingIntent, 0);
      }
      this.mRccObj = MediaSessionCompatApi14.createRemoteControlClient(paramPendingIntent);
    }
    
    private void adjustVolume(int paramInt1, int paramInt2)
    {
      if (this.mVolumeType != 2) {
        this.mAudioManager.adjustStreamVolume(paramInt1, this.mLocalStream, paramInt2);
      }
      while (this.mVolumeProvider == null) {
        return;
      }
      this.mVolumeProvider.onAdjustVolume(paramInt1);
    }
    
    private PlaybackStateCompat getStateWithUpdatedPosition()
    {
      long l1 = -1L;
      synchronized (this.mLock)
      {
        PlaybackStateCompat localPlaybackStateCompat1 = this.mState;
        if (this.mMetadata == null) {}
        while (localPlaybackStateCompat1 == null)
        {
          ??? = null;
          break label272;
          if (this.mMetadata.containsKey("android.media.metadata.DURATION")) {
            l1 = this.mMetadata.getLong("android.media.metadata.DURATION");
          }
        }
      }
      long l2;
      long l3;
      if (localPlaybackStateCompat2.getState() == 3)
      {
        l2 = localPlaybackStateCompat2.getLastPositionUpdateTime();
        l3 = SystemClock.elapsedRealtime();
        if (l2 > 0L) {
          break label219;
        }
        i = 1;
        label105:
        if (i != 0) {
          break label224;
        }
        l2 = (localPlaybackStateCompat2.getPlaybackSpeed() * (float)(l3 - l2)) + localPlaybackStateCompat2.getPosition();
        if (l1 >= 0L) {
          break label230;
        }
        i = 1;
        label138:
        if (i != 0) {
          break label240;
        }
        if (l2 > l1) {
          break label235;
        }
      }
      label219:
      label224:
      label230:
      label235:
      for (int i = 1;; i = 0)
      {
        if (i != 0) {
          break label240;
        }
        ??? = new PlaybackStateCompat.Builder(localPlaybackStateCompat2);
        ((PlaybackStateCompat.Builder)???).setState(localPlaybackStateCompat2.getState(), l1, localPlaybackStateCompat2.getPlaybackSpeed(), l3);
        ??? = ((PlaybackStateCompat.Builder)???).build();
        break label272;
        if ((localPlaybackStateCompat2.getState() == 4) || (localPlaybackStateCompat2.getState() == 5)) {
          break;
        }
        ??? = null;
        break label272;
        i = 0;
        break label105;
        ??? = null;
        break label272;
        i = 0;
        break label138;
      }
      label240:
      if (l2 >= 0L) {}
      for (i = 1;; i = 0)
      {
        if (i != 0) {
          break label263;
        }
        l1 = 0L;
        break;
      }
      label263:
      l1 = l2;
      label272:
      while (??? == null) {
        return localPlaybackStateCompat2;
      }
      return (PlaybackStateCompat)???;
    }
    
    private void sendEvent(String paramString, Bundle paramBundle)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i < 0)
        {
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onEvent(paramString, paramBundle);
          i -= 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void sendMetadata(MediaMetadataCompat paramMediaMetadataCompat)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i < 0)
        {
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onMetadataChanged(paramMediaMetadataCompat);
          i -= 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void sendQueue(List<MediaSessionCompat.QueueItem> paramList)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i < 0)
        {
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onQueueChanged(paramList);
          i -= 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void sendQueueTitle(CharSequence paramCharSequence)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i < 0)
        {
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onQueueTitleChanged(paramCharSequence);
          i -= 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void sendSessionDestroyed()
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i < 0)
        {
          this.mControllerCallbacks.finishBroadcast();
          this.mControllerCallbacks.kill();
          return;
        }
        IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onSessionDestroyed();
          i -= 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void sendState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i < 0)
        {
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onPlaybackStateChanged(paramPlaybackStateCompat);
          i -= 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void sendVolumeInfoChanged(ParcelableVolumeInfo paramParcelableVolumeInfo)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i < 0)
        {
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onVolumeInfoChanged(paramParcelableVolumeInfo);
          i -= 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void setVolumeTo(int paramInt1, int paramInt2)
    {
      if (this.mVolumeType != 2) {
        this.mAudioManager.setStreamVolume(this.mLocalStream, paramInt1, paramInt2);
      }
      while (this.mVolumeProvider == null) {
        return;
      }
      this.mVolumeProvider.onSetVolumeTo(paramInt1);
    }
    
    private boolean update()
    {
      if (!this.mIsActive)
      {
        if (!this.mIsMbrRegistered) {
          if (this.mIsRccRegistered) {
            break label262;
          }
        }
      }
      else
      {
        label30:
        label31:
        label46:
        label87:
        label180:
        label194:
        do
        {
          break label46;
          return false;
          if (Build.VERSION.SDK_INT < 8) {
            break label87;
          }
          for (;;)
          {
            if (Build.VERSION.SDK_INT >= 14)
            {
              if (!this.mIsRccRegistered) {
                break label194;
              }
              if ((!this.mIsRccRegistered) || ((this.mFlags & 0x2) != 0)) {
                break;
              }
              MediaSessionCompatApi14.unregisterRemoteControlClient(this.mContext, this.mRccObj);
              this.mIsRccRegistered = false;
              return false;
              if (this.mIsMbrRegistered)
              {
                if ((!this.mIsMbrRegistered) || ((this.mFlags & 0x1) != 0)) {
                  continue;
                }
                if (Build.VERSION.SDK_INT >= 18) {
                  break label180;
                }
                MediaSessionCompatApi8.unregisterMediaButtonEventReceiver(this.mContext, this.mComponentName);
              }
            }
          }
          for (;;)
          {
            this.mIsMbrRegistered = false;
            break label31;
            if ((this.mFlags & 0x1) == 0) {
              break label30;
            }
            if (Build.VERSION.SDK_INT < 18) {
              MediaSessionCompatApi8.registerMediaButtonEventReceiver(this.mContext, this.mComponentName);
            }
            for (;;)
            {
              this.mIsMbrRegistered = true;
              break label31;
              break;
              MediaSessionCompatApi18.registerMediaButtonEventReceiver(this.mContext, this.mMediaButtonEventReceiver);
            }
            MediaSessionCompatApi18.unregisterMediaButtonEventReceiver(this.mContext, this.mMediaButtonEventReceiver);
          }
        } while ((this.mFlags & 0x2) == 0);
        MediaSessionCompatApi14.registerRemoteControlClient(this.mContext, this.mRccObj);
        this.mIsRccRegistered = true;
        return true;
      }
      if (Build.VERSION.SDK_INT < 18) {
        MediaSessionCompatApi8.unregisterMediaButtonEventReceiver(this.mContext, this.mComponentName);
      }
      for (;;)
      {
        this.mIsMbrRegistered = false;
        break;
        MediaSessionCompatApi18.unregisterMediaButtonEventReceiver(this.mContext, this.mMediaButtonEventReceiver);
      }
      label262:
      MediaSessionCompatApi14.unregisterRemoteControlClient(this.mContext, this.mRccObj);
      this.mIsRccRegistered = false;
      return false;
    }
    
    public Object getMediaSession()
    {
      return null;
    }
    
    public Object getRemoteControlClient()
    {
      return this.mRccObj;
    }
    
    public MediaSessionCompat.Token getSessionToken()
    {
      return this.mToken;
    }
    
    public boolean isActive()
    {
      return this.mIsActive;
    }
    
    public void release()
    {
      this.mIsActive = false;
      this.mDestroyed = true;
      update();
      sendSessionDestroyed();
    }
    
    public void sendSessionEvent(String paramString, Bundle paramBundle)
    {
      sendEvent(paramString, paramBundle);
    }
    
    public void setActive(boolean paramBoolean)
    {
      if (paramBoolean != this.mIsActive)
      {
        this.mIsActive = paramBoolean;
        if (update()) {}
      }
      else
      {
        return;
      }
      setMetadata(this.mMetadata);
      setPlaybackState(this.mState);
    }
    
    public void setCallback(final MediaSessionCompat.Callback paramCallback, Handler paramHandler)
    {
      if (paramCallback != this.mCallback)
      {
        if (paramCallback != null) {
          break label35;
        }
        if (Build.VERSION.SDK_INT >= 18) {
          break label89;
        }
        if (Build.VERSION.SDK_INT >= 19) {
          break label100;
        }
      }
      label28:
      label35:
      label47:
      label89:
      label100:
      label122:
      label136:
      for (;;)
      {
        this.mCallback = paramCallback;
        return;
        return;
        if (Build.VERSION.SDK_INT < 18) {
          break;
        }
        if (paramHandler != null)
        {
          paramHandler = new MediaSessionCompatApi14.Callback()
          {
            public void onCommand(String paramAnonymousString, Bundle paramAnonymousBundle, ResultReceiver paramAnonymousResultReceiver)
            {
              paramCallback.onCommand(paramAnonymousString, paramAnonymousBundle, paramAnonymousResultReceiver);
            }
            
            public void onFastForward()
            {
              paramCallback.onFastForward();
            }
            
            public boolean onMediaButtonEvent(Intent paramAnonymousIntent)
            {
              return paramCallback.onMediaButtonEvent(paramAnonymousIntent);
            }
            
            public void onPause()
            {
              paramCallback.onPause();
            }
            
            public void onPlay()
            {
              paramCallback.onPlay();
            }
            
            public void onRewind()
            {
              paramCallback.onRewind();
            }
            
            public void onSeekTo(long paramAnonymousLong)
            {
              paramCallback.onSeekTo(paramAnonymousLong);
            }
            
            public void onSetRating(Object paramAnonymousObject)
            {
              paramCallback.onSetRating(RatingCompat.fromRating(paramAnonymousObject));
            }
            
            public void onSkipToNext()
            {
              paramCallback.onSkipToNext();
            }
            
            public void onSkipToPrevious()
            {
              paramCallback.onSkipToPrevious();
            }
            
            public void onStop()
            {
              paramCallback.onStop();
            }
          };
          if (Build.VERSION.SDK_INT >= 18) {
            break label122;
          }
        }
        for (;;)
        {
          if (Build.VERSION.SDK_INT < 19) {
            break label136;
          }
          paramHandler = MediaSessionCompatApi19.createMetadataUpdateListener(paramHandler);
          MediaSessionCompatApi19.setOnMetadataUpdateListener(this.mRccObj, paramHandler);
          break label28;
          MediaSessionCompatApi18.setOnPlaybackPositionUpdateListener(this.mRccObj, null);
          break;
          MediaSessionCompatApi19.setOnMetadataUpdateListener(this.mRccObj, null);
          break label28;
          new Handler();
          break label47;
          Object localObject = MediaSessionCompatApi18.createPlaybackPositionUpdateListener(paramHandler);
          MediaSessionCompatApi18.setOnPlaybackPositionUpdateListener(this.mRccObj, localObject);
        }
      }
    }
    
    public void setExtras(Bundle paramBundle)
    {
      this.mExtras = paramBundle;
    }
    
    public void setFlags(int paramInt)
    {
      synchronized (this.mLock)
      {
        this.mFlags = paramInt;
        update();
        return;
      }
    }
    
    public void setMediaButtonReceiver(PendingIntent paramPendingIntent) {}
    
    public void setMetadata(MediaMetadataCompat paramMediaMetadataCompat)
    {
      boolean bool = false;
      Object localObject2 = null;
      Object localObject1 = null;
      synchronized (this.mLock)
      {
        this.mMetadata = paramMediaMetadataCompat;
        sendMetadata(paramMediaMetadataCompat);
        if (this.mIsActive)
        {
          if (Build.VERSION.SDK_INT >= 19) {
            break label60;
          }
          if (Build.VERSION.SDK_INT >= 14) {
            break label111;
          }
          return;
        }
      }
      return;
      label60:
      if (this.mState == null) {}
      for (;;)
      {
        localObject2 = this.mRccObj;
        if (paramMediaMetadataCompat != null) {
          localObject1 = paramMediaMetadataCompat.getBundle();
        }
        MediaSessionCompatApi19.setMetadata(localObject2, (Bundle)localObject1, bool);
        return;
        if ((this.mState.getActions() & 0x80) != 0L) {
          bool = true;
        }
      }
      label111:
      ??? = this.mRccObj;
      localObject1 = localObject2;
      if (paramMediaMetadataCompat != null) {
        localObject1 = paramMediaMetadataCompat.getBundle();
      }
      MediaSessionCompatApi14.setMetadata(???, (Bundle)localObject1);
    }
    
    public void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      do
      {
        synchronized (this.mLock)
        {
          this.mState = paramPlaybackStateCompat;
          sendState(paramPlaybackStateCompat);
          if (this.mIsActive)
          {
            if (paramPlaybackStateCompat == null) {
              continue;
            }
            if (Build.VERSION.SDK_INT >= 18) {
              break;
            }
            if (Build.VERSION.SDK_INT >= 14) {
              break label94;
            }
            return;
          }
        }
        return;
      } while (Build.VERSION.SDK_INT < 14);
      MediaSessionCompatApi14.setState(this.mRccObj, 0);
      return;
      MediaSessionCompatApi18.setState(this.mRccObj, paramPlaybackStateCompat.getState(), paramPlaybackStateCompat.getPosition(), paramPlaybackStateCompat.getPlaybackSpeed(), paramPlaybackStateCompat.getLastPositionUpdateTime());
      return;
      label94:
      MediaSessionCompatApi14.setState(this.mRccObj, paramPlaybackStateCompat.getState());
    }
    
    public void setPlaybackToLocal(int paramInt)
    {
      if (this.mVolumeProvider == null) {}
      for (;;)
      {
        this.mVolumeType = 1;
        sendVolumeInfoChanged(new ParcelableVolumeInfo(this.mVolumeType, this.mLocalStream, 2, this.mAudioManager.getStreamMaxVolume(this.mLocalStream), this.mAudioManager.getStreamVolume(this.mLocalStream)));
        return;
        this.mVolumeProvider.setCallback(null);
      }
    }
    
    public void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat)
    {
      if (paramVolumeProviderCompat != null) {
        if (this.mVolumeProvider != null) {
          break label81;
        }
      }
      for (;;)
      {
        this.mVolumeType = 2;
        this.mVolumeProvider = paramVolumeProviderCompat;
        sendVolumeInfoChanged(new ParcelableVolumeInfo(this.mVolumeType, this.mLocalStream, this.mVolumeProvider.getVolumeControl(), this.mVolumeProvider.getMaxVolume(), this.mVolumeProvider.getCurrentVolume()));
        paramVolumeProviderCompat.setCallback(this.mVolumeCallback);
        return;
        throw new IllegalArgumentException("volumeProvider may not be null");
        label81:
        this.mVolumeProvider.setCallback(null);
      }
    }
    
    public void setQueue(List<MediaSessionCompat.QueueItem> paramList)
    {
      this.mQueue = paramList;
      sendQueue(paramList);
    }
    
    public void setQueueTitle(CharSequence paramCharSequence)
    {
      this.mQueueTitle = paramCharSequence;
      sendQueueTitle(paramCharSequence);
    }
    
    public void setRatingType(int paramInt)
    {
      this.mRatingType = paramInt;
    }
    
    public void setSessionActivity(PendingIntent paramPendingIntent)
    {
      synchronized (this.mLock)
      {
        this.mSessionActivity = paramPendingIntent;
        return;
      }
    }
    
    private static final class Command
    {
      public final String command;
      public final Bundle extras;
      public final ResultReceiver stub;
      
      public Command(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
      {
        this.command = paramString;
        this.extras = paramBundle;
        this.stub = paramResultReceiver;
      }
    }
    
    class MediaSessionStub
      extends IMediaSession.Stub
    {
      MediaSessionStub() {}
      
      public void adjustVolume(int paramInt1, int paramInt2, String paramString)
      {
        MediaSessionCompat.MediaSessionImplBase.this.adjustVolume(paramInt1, paramInt2);
      }
      
      public void fastForward()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(9);
      }
      
      public Bundle getExtras()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          Bundle localBundle = MediaSessionCompat.MediaSessionImplBase.this.mExtras;
          return localBundle;
        }
      }
      
      public long getFlags()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          long l = MediaSessionCompat.MediaSessionImplBase.this.mFlags;
          return l;
        }
      }
      
      public PendingIntent getLaunchPendingIntent()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          PendingIntent localPendingIntent = MediaSessionCompat.MediaSessionImplBase.this.mSessionActivity;
          return localPendingIntent;
        }
      }
      
      public MediaMetadataCompat getMetadata()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mMetadata;
      }
      
      public String getPackageName()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mPackageName;
      }
      
      public PlaybackStateCompat getPlaybackState()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.getStateWithUpdatedPosition();
      }
      
      public List<MediaSessionCompat.QueueItem> getQueue()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          List localList = MediaSessionCompat.MediaSessionImplBase.this.mQueue;
          return localList;
        }
      }
      
      public CharSequence getQueueTitle()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mQueueTitle;
      }
      
      public int getRatingType()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mRatingType;
      }
      
      public String getTag()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mTag;
      }
      
      public ParcelableVolumeInfo getVolumeAttributes()
      {
        int i = 2;
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          int m = MediaSessionCompat.MediaSessionImplBase.this.mVolumeType;
          int n = MediaSessionCompat.MediaSessionImplBase.this.mLocalStream;
          VolumeProviderCompat localVolumeProviderCompat = MediaSessionCompat.MediaSessionImplBase.this.mVolumeProvider;
          if (m != 2)
          {
            j = MediaSessionCompat.MediaSessionImplBase.this.mAudioManager.getStreamMaxVolume(n);
            k = MediaSessionCompat.MediaSessionImplBase.this.mAudioManager.getStreamVolume(n);
            return new ParcelableVolumeInfo(m, n, i, j, k);
          }
          i = localVolumeProviderCompat.getVolumeControl();
          int j = localVolumeProviderCompat.getMaxVolume();
          int k = localVolumeProviderCompat.getCurrentVolume();
        }
      }
      
      public boolean isTransportControlEnabled()
      {
        return (MediaSessionCompat.MediaSessionImplBase.this.mFlags & 0x2) != 0;
      }
      
      public void next()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(7);
      }
      
      public void pause()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(5);
      }
      
      public void play()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(1);
      }
      
      public void playFromMediaId(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(2, paramString, paramBundle);
      }
      
      public void playFromSearch(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(3, paramString, paramBundle);
      }
      
      public void previous()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(8);
      }
      
      public void rate(RatingCompat paramRatingCompat)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(12, paramRatingCompat);
      }
      
      public void registerCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
      {
        if (!MediaSessionCompat.MediaSessionImplBase.this.mDestroyed)
        {
          MediaSessionCompat.MediaSessionImplBase.this.mControllerCallbacks.register(paramIMediaControllerCallback);
          return;
        }
        try
        {
          paramIMediaControllerCallback.onSessionDestroyed();
          return;
        }
        catch (Exception paramIMediaControllerCallback) {}
      }
      
      public void rewind()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(10);
      }
      
      public void seekTo(long paramLong)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(11, Long.valueOf(paramLong));
      }
      
      public void sendCommand(String paramString, Bundle paramBundle, MediaSessionCompat.ResultReceiverWrapper paramResultReceiverWrapper)
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(15, new MediaSessionCompat.MediaSessionImplBase.Command(paramString, paramBundle, MediaSessionCompat.ResultReceiverWrapper.access$600(paramResultReceiverWrapper)));
      }
      
      public void sendCustomAction(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(13, paramString, paramBundle);
      }
      
      public boolean sendMediaButton(KeyEvent paramKeyEvent)
      {
        boolean bool = false;
        if ((MediaSessionCompat.MediaSessionImplBase.this.mFlags & 0x1) == 0) {}
        while (!bool)
        {
          return bool;
          bool = true;
        }
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(14, paramKeyEvent);
        return bool;
      }
      
      public void setVolumeTo(int paramInt1, int paramInt2, String paramString)
      {
        MediaSessionCompat.MediaSessionImplBase.this.setVolumeTo(paramInt1, paramInt2);
      }
      
      public void skipToQueueItem(long paramLong)
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(4, Long.valueOf(paramLong));
      }
      
      public void stop()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.mHandler.post(6);
      }
      
      public void unregisterCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
      {
        MediaSessionCompat.MediaSessionImplBase.this.mControllerCallbacks.unregister(paramIMediaControllerCallback);
      }
    }
    
    private class MessageHandler
      extends Handler
    {
      private static final int MSG_ADJUST_VOLUME = 16;
      private static final int MSG_COMMAND = 15;
      private static final int MSG_CUSTOM_ACTION = 13;
      private static final int MSG_FAST_FORWARD = 9;
      private static final int MSG_MEDIA_BUTTON = 14;
      private static final int MSG_NEXT = 7;
      private static final int MSG_PAUSE = 5;
      private static final int MSG_PLAY = 1;
      private static final int MSG_PLAY_MEDIA_ID = 2;
      private static final int MSG_PLAY_SEARCH = 3;
      private static final int MSG_PREVIOUS = 8;
      private static final int MSG_RATE = 12;
      private static final int MSG_REWIND = 10;
      private static final int MSG_SEEK_TO = 11;
      private static final int MSG_SET_VOLUME = 17;
      private static final int MSG_SKIP_TO_ITEM = 4;
      private static final int MSG_STOP = 6;
      
      public MessageHandler(Looper paramLooper)
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        if (MediaSessionCompat.MediaSessionImplBase.this.mCallback != null) {}
        switch (paramMessage.what)
        {
        default: 
          return;
          return;
        case 1: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onPlay();
          return;
        case 2: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onPlayFromMediaId((String)paramMessage.obj, paramMessage.getData());
          return;
        case 3: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onPlayFromSearch((String)paramMessage.obj, paramMessage.getData());
          return;
        case 4: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onSkipToQueueItem(((Long)paramMessage.obj).longValue());
          return;
        case 5: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onPause();
          return;
        case 6: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onStop();
          return;
        case 7: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onSkipToNext();
          return;
        case 8: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onSkipToPrevious();
          return;
        case 9: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onFastForward();
          return;
        case 10: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onRewind();
          return;
        case 11: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onSeekTo(((Long)paramMessage.obj).longValue());
          return;
        case 12: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onSetRating((RatingCompat)paramMessage.obj);
          return;
        case 13: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onCustomAction((String)paramMessage.obj, paramMessage.getData());
          return;
        case 14: 
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onMediaButtonEvent((Intent)paramMessage.obj);
          return;
        case 15: 
          paramMessage = (MediaSessionCompat.MediaSessionImplBase.Command)paramMessage.obj;
          MediaSessionCompat.MediaSessionImplBase.this.mCallback.onCommand(paramMessage.command, paramMessage.extras, paramMessage.stub);
          return;
        case 16: 
          MediaSessionCompat.MediaSessionImplBase.this.adjustVolume(((Integer)paramMessage.obj).intValue(), 0);
          return;
        }
        MediaSessionCompat.MediaSessionImplBase.this.setVolumeTo(((Integer)paramMessage.obj).intValue(), 0);
      }
      
      public void post(int paramInt)
      {
        post(paramInt, null);
      }
      
      public void post(int paramInt, Object paramObject)
      {
        obtainMessage(paramInt, paramObject).sendToTarget();
      }
      
      public void post(int paramInt1, Object paramObject, int paramInt2)
      {
        obtainMessage(paramInt1, paramInt2, 0, paramObject).sendToTarget();
      }
      
      public void post(int paramInt, Object paramObject, Bundle paramBundle)
      {
        paramObject = obtainMessage(paramInt, paramObject);
        ((Message)paramObject).setData(paramBundle);
        ((Message)paramObject).sendToTarget();
      }
    }
  }
  
  public static abstract interface OnActiveChangeListener
  {
    public abstract void onActiveChanged();
  }
  
  public static final class QueueItem
    implements Parcelable
  {
    public static final Parcelable.Creator<QueueItem> CREATOR = new Parcelable.Creator()
    {
      public MediaSessionCompat.QueueItem createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaSessionCompat.QueueItem(paramAnonymousParcel, null);
      }
      
      public MediaSessionCompat.QueueItem[] newArray(int paramAnonymousInt)
      {
        return new MediaSessionCompat.QueueItem[paramAnonymousInt];
      }
    };
    public static final int UNKNOWN_ID = -1;
    private final MediaDescriptionCompat mDescription;
    private final long mId;
    private Object mItem;
    
    private QueueItem(Parcel paramParcel)
    {
      this.mDescription = ((MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(paramParcel));
      this.mId = paramParcel.readLong();
    }
    
    public QueueItem(MediaDescriptionCompat paramMediaDescriptionCompat, long paramLong)
    {
      this(null, paramMediaDescriptionCompat, paramLong);
    }
    
    private QueueItem(Object paramObject, MediaDescriptionCompat paramMediaDescriptionCompat, long paramLong)
    {
      if (paramMediaDescriptionCompat != null)
      {
        if (paramLong == -1L) {
          throw new IllegalArgumentException("Id cannot be QueueItem.UNKNOWN_ID");
        }
      }
      else {
        throw new IllegalArgumentException("Description cannot be null.");
      }
      this.mDescription = paramMediaDescriptionCompat;
      this.mId = paramLong;
      this.mItem = paramObject;
    }
    
    public static QueueItem obtain(Object paramObject)
    {
      return new QueueItem(paramObject, MediaDescriptionCompat.fromMediaDescription(MediaSessionCompatApi21.QueueItem.getDescription(paramObject)), MediaSessionCompatApi21.QueueItem.getQueueId(paramObject));
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public MediaDescriptionCompat getDescription()
    {
      return this.mDescription;
    }
    
    public long getQueueId()
    {
      return this.mId;
    }
    
    public Object getQueueItem()
    {
      if (this.mItem != null) {}
      while (Build.VERSION.SDK_INT < 21) {
        return this.mItem;
      }
      this.mItem = MediaSessionCompatApi21.QueueItem.createItem(this.mDescription.getMediaDescription(), this.mId);
      return this.mItem;
    }
    
    public String toString()
    {
      return "MediaSession.QueueItem {Description=" + this.mDescription + ", Id=" + this.mId + " }";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      this.mDescription.writeToParcel(paramParcel, paramInt);
      paramParcel.writeLong(this.mId);
    }
  }
  
  static final class ResultReceiverWrapper
    implements Parcelable
  {
    public static final Parcelable.Creator<ResultReceiverWrapper> CREATOR = new Parcelable.Creator()
    {
      public MediaSessionCompat.ResultReceiverWrapper createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaSessionCompat.ResultReceiverWrapper(paramAnonymousParcel);
      }
      
      public MediaSessionCompat.ResultReceiverWrapper[] newArray(int paramAnonymousInt)
      {
        return new MediaSessionCompat.ResultReceiverWrapper[paramAnonymousInt];
      }
    };
    private ResultReceiver mResultReceiver;
    
    ResultReceiverWrapper(Parcel paramParcel)
    {
      this.mResultReceiver = ((ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel));
    }
    
    public ResultReceiverWrapper(ResultReceiver paramResultReceiver)
    {
      this.mResultReceiver = paramResultReceiver;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      this.mResultReceiver.writeToParcel(paramParcel, paramInt);
    }
  }
  
  public static final class Token
    implements Parcelable
  {
    public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator()
    {
      public MediaSessionCompat.Token createFromParcel(Parcel paramAnonymousParcel)
      {
        if (Build.VERSION.SDK_INT < 21) {}
        for (paramAnonymousParcel = paramAnonymousParcel.readStrongBinder();; paramAnonymousParcel = paramAnonymousParcel.readParcelable(null)) {
          return new MediaSessionCompat.Token(paramAnonymousParcel);
        }
      }
      
      public MediaSessionCompat.Token[] newArray(int paramAnonymousInt)
      {
        return new MediaSessionCompat.Token[paramAnonymousInt];
      }
    };
    private final Object mInner;
    
    Token(Object paramObject)
    {
      this.mInner = paramObject;
    }
    
    public static Token fromToken(Object paramObject)
    {
      if (paramObject == null) {}
      while (Build.VERSION.SDK_INT < 21) {
        return null;
      }
      return new Token(MediaSessionCompatApi21.verifyToken(paramObject));
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public Object getToken()
    {
      return this.mInner;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      if (Build.VERSION.SDK_INT < 21)
      {
        paramParcel.writeStrongBinder((IBinder)this.mInner);
        return;
      }
      paramParcel.writeParcelable((Parcelable)this.mInner, paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/media/session/MediaSessionCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */