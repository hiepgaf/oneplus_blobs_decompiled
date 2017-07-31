package android.support.v4.media.session;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class MediaControllerCompat
{
  private static final String TAG = "MediaControllerCompat";
  private final MediaControllerImpl mImpl;
  private final MediaSessionCompat.Token mToken;
  
  public MediaControllerCompat(Context paramContext, MediaSessionCompat.Token paramToken)
    throws RemoteException
  {
    if (paramToken != null)
    {
      this.mToken = paramToken;
      if (Build.VERSION.SDK_INT < 21) {
        this.mImpl = new MediaControllerImplBase(this.mToken);
      }
    }
    else
    {
      throw new IllegalArgumentException("sessionToken must not be null");
    }
    this.mImpl = new MediaControllerImplApi21(paramContext, paramToken);
  }
  
  public MediaControllerCompat(Context paramContext, MediaSessionCompat paramMediaSessionCompat)
  {
    if (paramMediaSessionCompat != null)
    {
      this.mToken = paramMediaSessionCompat.getSessionToken();
      if (Build.VERSION.SDK_INT < 21) {
        this.mImpl = new MediaControllerImplBase(this.mToken);
      }
    }
    else
    {
      throw new IllegalArgumentException("session must not be null");
    }
    this.mImpl = new MediaControllerImplApi21(paramContext, paramMediaSessionCompat);
  }
  
  public void adjustVolume(int paramInt1, int paramInt2)
  {
    this.mImpl.adjustVolume(paramInt1, paramInt2);
  }
  
  public boolean dispatchMediaButtonEvent(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent != null) {
      return this.mImpl.dispatchMediaButtonEvent(paramKeyEvent);
    }
    throw new IllegalArgumentException("KeyEvent may not be null");
  }
  
  public Bundle getExtras()
  {
    return this.mImpl.getExtras();
  }
  
  public long getFlags()
  {
    return this.mImpl.getFlags();
  }
  
  public Object getMediaController()
  {
    return this.mImpl.getMediaController();
  }
  
  public MediaMetadataCompat getMetadata()
  {
    return this.mImpl.getMetadata();
  }
  
  public String getPackageName()
  {
    return this.mImpl.getPackageName();
  }
  
  public PlaybackInfo getPlaybackInfo()
  {
    return this.mImpl.getPlaybackInfo();
  }
  
  public PlaybackStateCompat getPlaybackState()
  {
    return this.mImpl.getPlaybackState();
  }
  
  public List<MediaSessionCompat.QueueItem> getQueue()
  {
    return this.mImpl.getQueue();
  }
  
  public CharSequence getQueueTitle()
  {
    return this.mImpl.getQueueTitle();
  }
  
  public int getRatingType()
  {
    return this.mImpl.getRatingType();
  }
  
  public PendingIntent getSessionActivity()
  {
    return this.mImpl.getSessionActivity();
  }
  
  public MediaSessionCompat.Token getSessionToken()
  {
    return this.mToken;
  }
  
  public TransportControls getTransportControls()
  {
    return this.mImpl.getTransportControls();
  }
  
  public void registerCallback(Callback paramCallback)
  {
    registerCallback(paramCallback, null);
  }
  
  public void registerCallback(Callback paramCallback, Handler paramHandler)
  {
    if (paramCallback != null) {
      if (paramHandler == null) {
        break label30;
      }
    }
    for (;;)
    {
      this.mImpl.registerCallback(paramCallback, paramHandler);
      return;
      throw new IllegalArgumentException("callback cannot be null");
      label30:
      paramHandler = new Handler();
    }
  }
  
  public void sendCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
  {
    if (!TextUtils.isEmpty(paramString))
    {
      this.mImpl.sendCommand(paramString, paramBundle, paramResultReceiver);
      return;
    }
    throw new IllegalArgumentException("command cannot be null or empty");
  }
  
  public void setVolumeTo(int paramInt1, int paramInt2)
  {
    this.mImpl.setVolumeTo(paramInt1, paramInt2);
  }
  
  public void unregisterCallback(Callback paramCallback)
  {
    if (paramCallback != null)
    {
      this.mImpl.unregisterCallback(paramCallback);
      return;
    }
    throw new IllegalArgumentException("callback cannot be null");
  }
  
  public static abstract class Callback
    implements IBinder.DeathRecipient
  {
    private final Object mCallbackObj;
    private MessageHandler mHandler;
    private boolean mRegistered = false;
    
    public Callback()
    {
      if (Build.VERSION.SDK_INT < 21)
      {
        this.mCallbackObj = new StubCompat(null);
        return;
      }
      this.mCallbackObj = MediaControllerCompatApi21.createCallback(new StubApi21(null));
    }
    
    private void setHandler(Handler paramHandler)
    {
      this.mHandler = new MessageHandler(paramHandler.getLooper());
    }
    
    public void binderDied()
    {
      onSessionDestroyed();
    }
    
    public void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo paramPlaybackInfo) {}
    
    public void onExtrasChanged(Bundle paramBundle) {}
    
    public void onMetadataChanged(MediaMetadataCompat paramMediaMetadataCompat) {}
    
    public void onPlaybackStateChanged(PlaybackStateCompat paramPlaybackStateCompat) {}
    
    public void onQueueChanged(List<MediaSessionCompat.QueueItem> paramList) {}
    
    public void onQueueTitleChanged(CharSequence paramCharSequence) {}
    
    public void onSessionDestroyed() {}
    
    public void onSessionEvent(String paramString, Bundle paramBundle) {}
    
    private class MessageHandler
      extends Handler
    {
      private static final int MSG_DESTROYED = 8;
      private static final int MSG_EVENT = 1;
      private static final int MSG_UPDATE_EXTRAS = 7;
      private static final int MSG_UPDATE_METADATA = 3;
      private static final int MSG_UPDATE_PLAYBACK_STATE = 2;
      private static final int MSG_UPDATE_QUEUE = 5;
      private static final int MSG_UPDATE_QUEUE_TITLE = 6;
      private static final int MSG_UPDATE_VOLUME = 4;
      
      public MessageHandler(Looper paramLooper)
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        if (MediaControllerCompat.Callback.this.mRegistered) {}
        switch (paramMessage.what)
        {
        default: 
          return;
          return;
        case 1: 
          MediaControllerCompat.Callback.this.onSessionEvent((String)paramMessage.obj, paramMessage.getData());
          return;
        case 2: 
          MediaControllerCompat.Callback.this.onPlaybackStateChanged((PlaybackStateCompat)paramMessage.obj);
          return;
        case 3: 
          MediaControllerCompat.Callback.this.onMetadataChanged((MediaMetadataCompat)paramMessage.obj);
          return;
        case 5: 
          MediaControllerCompat.Callback.this.onQueueChanged((List)paramMessage.obj);
          return;
        case 6: 
          MediaControllerCompat.Callback.this.onQueueTitleChanged((CharSequence)paramMessage.obj);
          return;
        case 7: 
          MediaControllerCompat.Callback.this.onExtrasChanged((Bundle)paramMessage.obj);
          return;
        case 4: 
          MediaControllerCompat.Callback.this.onAudioInfoChanged((MediaControllerCompat.PlaybackInfo)paramMessage.obj);
          return;
        }
        MediaControllerCompat.Callback.this.onSessionDestroyed();
      }
      
      public void post(int paramInt, Object paramObject, Bundle paramBundle)
      {
        obtainMessage(paramInt, paramObject).sendToTarget();
      }
    }
    
    private class StubApi21
      implements MediaControllerCompatApi21.Callback
    {
      private StubApi21() {}
      
      public void onMetadataChanged(Object paramObject)
      {
        MediaControllerCompat.Callback.this.onMetadataChanged(MediaMetadataCompat.fromMediaMetadata(paramObject));
      }
      
      public void onPlaybackStateChanged(Object paramObject)
      {
        MediaControllerCompat.Callback.this.onPlaybackStateChanged(PlaybackStateCompat.fromPlaybackState(paramObject));
      }
      
      public void onSessionDestroyed()
      {
        MediaControllerCompat.Callback.this.onSessionDestroyed();
      }
      
      public void onSessionEvent(String paramString, Bundle paramBundle)
      {
        MediaControllerCompat.Callback.this.onSessionEvent(paramString, paramBundle);
      }
    }
    
    private class StubCompat
      extends IMediaControllerCallback.Stub
    {
      private StubCompat() {}
      
      public void onEvent(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaControllerCompat.Callback.this.mHandler.post(1, paramString, paramBundle);
      }
      
      public void onExtrasChanged(Bundle paramBundle)
        throws RemoteException
      {
        MediaControllerCompat.Callback.this.mHandler.post(7, paramBundle, null);
      }
      
      public void onMetadataChanged(MediaMetadataCompat paramMediaMetadataCompat)
        throws RemoteException
      {
        MediaControllerCompat.Callback.this.mHandler.post(3, paramMediaMetadataCompat, null);
      }
      
      public void onPlaybackStateChanged(PlaybackStateCompat paramPlaybackStateCompat)
        throws RemoteException
      {
        MediaControllerCompat.Callback.this.mHandler.post(2, paramPlaybackStateCompat, null);
      }
      
      public void onQueueChanged(List<MediaSessionCompat.QueueItem> paramList)
        throws RemoteException
      {
        MediaControllerCompat.Callback.this.mHandler.post(5, paramList, null);
      }
      
      public void onQueueTitleChanged(CharSequence paramCharSequence)
        throws RemoteException
      {
        MediaControllerCompat.Callback.this.mHandler.post(6, paramCharSequence, null);
      }
      
      public void onSessionDestroyed()
        throws RemoteException
      {
        MediaControllerCompat.Callback.this.mHandler.post(8, null, null);
      }
      
      public void onVolumeInfoChanged(ParcelableVolumeInfo paramParcelableVolumeInfo)
        throws RemoteException
      {
        if (paramParcelableVolumeInfo == null) {}
        for (paramParcelableVolumeInfo = null;; paramParcelableVolumeInfo = new MediaControllerCompat.PlaybackInfo(paramParcelableVolumeInfo.volumeType, paramParcelableVolumeInfo.audioStream, paramParcelableVolumeInfo.controlType, paramParcelableVolumeInfo.maxVolume, paramParcelableVolumeInfo.currentVolume))
        {
          MediaControllerCompat.Callback.this.mHandler.post(4, paramParcelableVolumeInfo, null);
          return;
        }
      }
    }
  }
  
  static abstract interface MediaControllerImpl
  {
    public abstract void adjustVolume(int paramInt1, int paramInt2);
    
    public abstract boolean dispatchMediaButtonEvent(KeyEvent paramKeyEvent);
    
    public abstract Bundle getExtras();
    
    public abstract long getFlags();
    
    public abstract Object getMediaController();
    
    public abstract MediaMetadataCompat getMetadata();
    
    public abstract String getPackageName();
    
    public abstract MediaControllerCompat.PlaybackInfo getPlaybackInfo();
    
    public abstract PlaybackStateCompat getPlaybackState();
    
    public abstract List<MediaSessionCompat.QueueItem> getQueue();
    
    public abstract CharSequence getQueueTitle();
    
    public abstract int getRatingType();
    
    public abstract PendingIntent getSessionActivity();
    
    public abstract MediaControllerCompat.TransportControls getTransportControls();
    
    public abstract void registerCallback(MediaControllerCompat.Callback paramCallback, Handler paramHandler);
    
    public abstract void sendCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver);
    
    public abstract void setVolumeTo(int paramInt1, int paramInt2);
    
    public abstract void unregisterCallback(MediaControllerCompat.Callback paramCallback);
  }
  
  static class MediaControllerImplApi21
    implements MediaControllerCompat.MediaControllerImpl
  {
    private final Object mControllerObj;
    
    public MediaControllerImplApi21(Context paramContext, MediaSessionCompat.Token paramToken)
      throws RemoteException
    {
      this.mControllerObj = MediaControllerCompatApi21.fromToken(paramContext, paramToken.getToken());
      if (this.mControllerObj != null) {
        return;
      }
      throw new RemoteException();
    }
    
    public MediaControllerImplApi21(Context paramContext, MediaSessionCompat paramMediaSessionCompat)
    {
      this.mControllerObj = MediaControllerCompatApi21.fromToken(paramContext, paramMediaSessionCompat.getSessionToken().getToken());
    }
    
    public void adjustVolume(int paramInt1, int paramInt2)
    {
      MediaControllerCompatApi21.adjustVolume(this.mControllerObj, paramInt1, paramInt2);
    }
    
    public boolean dispatchMediaButtonEvent(KeyEvent paramKeyEvent)
    {
      return MediaControllerCompatApi21.dispatchMediaButtonEvent(this.mControllerObj, paramKeyEvent);
    }
    
    public Bundle getExtras()
    {
      return MediaControllerCompatApi21.getExtras(this.mControllerObj);
    }
    
    public long getFlags()
    {
      return MediaControllerCompatApi21.getFlags(this.mControllerObj);
    }
    
    public Object getMediaController()
    {
      return this.mControllerObj;
    }
    
    public MediaMetadataCompat getMetadata()
    {
      Object localObject = MediaControllerCompatApi21.getMetadata(this.mControllerObj);
      if (localObject == null) {
        return null;
      }
      return MediaMetadataCompat.fromMediaMetadata(localObject);
    }
    
    public String getPackageName()
    {
      return MediaControllerCompatApi21.getPackageName(this.mControllerObj);
    }
    
    public MediaControllerCompat.PlaybackInfo getPlaybackInfo()
    {
      Object localObject = MediaControllerCompatApi21.getPlaybackInfo(this.mControllerObj);
      if (localObject == null) {
        return null;
      }
      return new MediaControllerCompat.PlaybackInfo(MediaControllerCompatApi21.PlaybackInfo.getPlaybackType(localObject), MediaControllerCompatApi21.PlaybackInfo.getLegacyAudioStream(localObject), MediaControllerCompatApi21.PlaybackInfo.getVolumeControl(localObject), MediaControllerCompatApi21.PlaybackInfo.getMaxVolume(localObject), MediaControllerCompatApi21.PlaybackInfo.getCurrentVolume(localObject));
    }
    
    public PlaybackStateCompat getPlaybackState()
    {
      Object localObject = MediaControllerCompatApi21.getPlaybackState(this.mControllerObj);
      if (localObject == null) {
        return null;
      }
      return PlaybackStateCompat.fromPlaybackState(localObject);
    }
    
    public List<MediaSessionCompat.QueueItem> getQueue()
    {
      Object localObject = MediaControllerCompatApi21.getQueue(this.mControllerObj);
      ArrayList localArrayList;
      if (localObject != null)
      {
        localArrayList = new ArrayList();
        localObject = ((List)localObject).iterator();
      }
      for (;;)
      {
        if (!((Iterator)localObject).hasNext())
        {
          return localArrayList;
          return null;
        }
        localArrayList.add(MediaSessionCompat.QueueItem.obtain(((Iterator)localObject).next()));
      }
    }
    
    public CharSequence getQueueTitle()
    {
      return MediaControllerCompatApi21.getQueueTitle(this.mControllerObj);
    }
    
    public int getRatingType()
    {
      return MediaControllerCompatApi21.getRatingType(this.mControllerObj);
    }
    
    public PendingIntent getSessionActivity()
    {
      return MediaControllerCompatApi21.getSessionActivity(this.mControllerObj);
    }
    
    public MediaControllerCompat.TransportControls getTransportControls()
    {
      Object localObject = MediaControllerCompatApi21.getTransportControls(this.mControllerObj);
      if (localObject == null) {
        return null;
      }
      return new MediaControllerCompat.TransportControlsApi21(localObject);
    }
    
    public void registerCallback(MediaControllerCompat.Callback paramCallback, Handler paramHandler)
    {
      MediaControllerCompatApi21.registerCallback(this.mControllerObj, paramCallback.mCallbackObj, paramHandler);
    }
    
    public void sendCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
    {
      MediaControllerCompatApi21.sendCommand(this.mControllerObj, paramString, paramBundle, paramResultReceiver);
    }
    
    public void setVolumeTo(int paramInt1, int paramInt2)
    {
      MediaControllerCompatApi21.setVolumeTo(this.mControllerObj, paramInt1, paramInt2);
    }
    
    public void unregisterCallback(MediaControllerCompat.Callback paramCallback)
    {
      MediaControllerCompatApi21.unregisterCallback(this.mControllerObj, paramCallback.mCallbackObj);
    }
  }
  
  static class MediaControllerImplBase
    implements MediaControllerCompat.MediaControllerImpl
  {
    private IMediaSession mBinder;
    private MediaSessionCompat.Token mToken;
    private MediaControllerCompat.TransportControls mTransportControls;
    
    public MediaControllerImplBase(MediaSessionCompat.Token paramToken)
    {
      this.mToken = paramToken;
      this.mBinder = IMediaSession.Stub.asInterface((IBinder)paramToken.getToken());
    }
    
    public void adjustVolume(int paramInt1, int paramInt2)
    {
      try
      {
        this.mBinder.adjustVolume(paramInt1, paramInt2, null);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in adjustVolume. " + localRemoteException);
      }
    }
    
    public boolean dispatchMediaButtonEvent(KeyEvent paramKeyEvent)
    {
      if (paramKeyEvent != null) {}
      try
      {
        this.mBinder.sendMediaButton(paramKeyEvent);
        return false;
        throw new IllegalArgumentException("event may not be null.");
      }
      catch (RemoteException paramKeyEvent)
      {
        for (;;)
        {
          Log.e("MediaControllerCompat", "Dead object in dispatchMediaButtonEvent. " + paramKeyEvent);
        }
      }
    }
    
    public Bundle getExtras()
    {
      try
      {
        Bundle localBundle = this.mBinder.getExtras();
        return localBundle;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in getExtras. " + localRemoteException);
      }
      return null;
    }
    
    public long getFlags()
    {
      try
      {
        long l = this.mBinder.getFlags();
        return l;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in getFlags. " + localRemoteException);
      }
      return 0L;
    }
    
    public Object getMediaController()
    {
      return null;
    }
    
    public MediaMetadataCompat getMetadata()
    {
      try
      {
        MediaMetadataCompat localMediaMetadataCompat = this.mBinder.getMetadata();
        return localMediaMetadataCompat;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in getMetadata. " + localRemoteException);
      }
      return null;
    }
    
    public String getPackageName()
    {
      try
      {
        String str = this.mBinder.getPackageName();
        return str;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in getPackageName. " + localRemoteException);
      }
      return null;
    }
    
    public MediaControllerCompat.PlaybackInfo getPlaybackInfo()
    {
      try
      {
        Object localObject = this.mBinder.getVolumeAttributes();
        localObject = new MediaControllerCompat.PlaybackInfo(((ParcelableVolumeInfo)localObject).volumeType, ((ParcelableVolumeInfo)localObject).audioStream, ((ParcelableVolumeInfo)localObject).controlType, ((ParcelableVolumeInfo)localObject).maxVolume, ((ParcelableVolumeInfo)localObject).currentVolume);
        return (MediaControllerCompat.PlaybackInfo)localObject;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in getPlaybackInfo. " + localRemoteException);
      }
      return null;
    }
    
    public PlaybackStateCompat getPlaybackState()
    {
      try
      {
        PlaybackStateCompat localPlaybackStateCompat = this.mBinder.getPlaybackState();
        return localPlaybackStateCompat;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in getPlaybackState. " + localRemoteException);
      }
      return null;
    }
    
    public List<MediaSessionCompat.QueueItem> getQueue()
    {
      try
      {
        List localList = this.mBinder.getQueue();
        return localList;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in getQueue. " + localRemoteException);
      }
      return null;
    }
    
    public CharSequence getQueueTitle()
    {
      try
      {
        CharSequence localCharSequence = this.mBinder.getQueueTitle();
        return localCharSequence;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in getQueueTitle. " + localRemoteException);
      }
      return null;
    }
    
    public int getRatingType()
    {
      try
      {
        int i = this.mBinder.getRatingType();
        return i;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in getRatingType. " + localRemoteException);
      }
      return 0;
    }
    
    public PendingIntent getSessionActivity()
    {
      try
      {
        PendingIntent localPendingIntent = this.mBinder.getLaunchPendingIntent();
        return localPendingIntent;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in getSessionActivity. " + localRemoteException);
      }
      return null;
    }
    
    public MediaControllerCompat.TransportControls getTransportControls()
    {
      if (this.mTransportControls != null) {}
      for (;;)
      {
        return this.mTransportControls;
        this.mTransportControls = new MediaControllerCompat.TransportControlsBase(this.mBinder);
      }
    }
    
    public void registerCallback(MediaControllerCompat.Callback paramCallback, Handler paramHandler)
    {
      if (paramCallback != null) {}
      try
      {
        this.mBinder.asBinder().linkToDeath(paramCallback, 0);
        this.mBinder.registerCallbackListener((IMediaControllerCallback)paramCallback.mCallbackObj);
        paramCallback.setHandler(paramHandler);
        MediaControllerCompat.Callback.access$302(paramCallback, true);
        return;
      }
      catch (RemoteException paramHandler)
      {
        Log.e("MediaControllerCompat", "Dead object in registerCallback. " + paramHandler);
        paramCallback.onSessionDestroyed();
      }
      throw new IllegalArgumentException("callback may not be null.");
    }
    
    public void sendCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
    {
      try
      {
        this.mBinder.sendCommand(paramString, paramBundle, new MediaSessionCompat.ResultReceiverWrapper(paramResultReceiver));
        return;
      }
      catch (RemoteException paramString)
      {
        Log.e("MediaControllerCompat", "Dead object in sendCommand. " + paramString);
      }
    }
    
    public void setVolumeTo(int paramInt1, int paramInt2)
    {
      try
      {
        this.mBinder.setVolumeTo(paramInt1, paramInt2, null);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in setVolumeTo. " + localRemoteException);
      }
    }
    
    public void unregisterCallback(MediaControllerCompat.Callback paramCallback)
    {
      if (paramCallback != null) {}
      try
      {
        this.mBinder.unregisterCallbackListener((IMediaControllerCallback)paramCallback.mCallbackObj);
        this.mBinder.asBinder().unlinkToDeath(paramCallback, 0);
        MediaControllerCompat.Callback.access$302(paramCallback, false);
        return;
      }
      catch (RemoteException paramCallback)
      {
        Log.e("MediaControllerCompat", "Dead object in unregisterCallback. " + paramCallback);
      }
      throw new IllegalArgumentException("callback may not be null.");
    }
  }
  
  public static final class PlaybackInfo
  {
    public static final int PLAYBACK_TYPE_LOCAL = 1;
    public static final int PLAYBACK_TYPE_REMOTE = 2;
    private final int mAudioStream;
    private final int mCurrentVolume;
    private final int mMaxVolume;
    private final int mPlaybackType;
    private final int mVolumeControl;
    
    PlaybackInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      this.mPlaybackType = paramInt1;
      this.mAudioStream = paramInt2;
      this.mVolumeControl = paramInt3;
      this.mMaxVolume = paramInt4;
      this.mCurrentVolume = paramInt5;
    }
    
    public int getAudioStream()
    {
      return this.mAudioStream;
    }
    
    public int getCurrentVolume()
    {
      return this.mCurrentVolume;
    }
    
    public int getMaxVolume()
    {
      return this.mMaxVolume;
    }
    
    public int getPlaybackType()
    {
      return this.mPlaybackType;
    }
    
    public int getVolumeControl()
    {
      return this.mVolumeControl;
    }
  }
  
  public static abstract class TransportControls
  {
    public abstract void fastForward();
    
    public abstract void pause();
    
    public abstract void play();
    
    public abstract void playFromMediaId(String paramString, Bundle paramBundle);
    
    public abstract void playFromSearch(String paramString, Bundle paramBundle);
    
    public abstract void rewind();
    
    public abstract void seekTo(long paramLong);
    
    public abstract void sendCustomAction(PlaybackStateCompat.CustomAction paramCustomAction, Bundle paramBundle);
    
    public abstract void sendCustomAction(String paramString, Bundle paramBundle);
    
    public abstract void setRating(RatingCompat paramRatingCompat);
    
    public abstract void skipToNext();
    
    public abstract void skipToPrevious();
    
    public abstract void skipToQueueItem(long paramLong);
    
    public abstract void stop();
  }
  
  static class TransportControlsApi21
    extends MediaControllerCompat.TransportControls
  {
    private final Object mControlsObj;
    
    public TransportControlsApi21(Object paramObject)
    {
      this.mControlsObj = paramObject;
    }
    
    public void fastForward()
    {
      MediaControllerCompatApi21.TransportControls.fastForward(this.mControlsObj);
    }
    
    public void pause()
    {
      MediaControllerCompatApi21.TransportControls.pause(this.mControlsObj);
    }
    
    public void play()
    {
      MediaControllerCompatApi21.TransportControls.play(this.mControlsObj);
    }
    
    public void playFromMediaId(String paramString, Bundle paramBundle)
    {
      MediaControllerCompatApi21.TransportControls.playFromMediaId(this.mControlsObj, paramString, paramBundle);
    }
    
    public void playFromSearch(String paramString, Bundle paramBundle)
    {
      MediaControllerCompatApi21.TransportControls.playFromSearch(this.mControlsObj, paramString, paramBundle);
    }
    
    public void rewind()
    {
      MediaControllerCompatApi21.TransportControls.rewind(this.mControlsObj);
    }
    
    public void seekTo(long paramLong)
    {
      MediaControllerCompatApi21.TransportControls.seekTo(this.mControlsObj, paramLong);
    }
    
    public void sendCustomAction(PlaybackStateCompat.CustomAction paramCustomAction, Bundle paramBundle)
    {
      MediaControllerCompatApi21.TransportControls.sendCustomAction(this.mControlsObj, paramCustomAction.getAction(), paramBundle);
    }
    
    public void sendCustomAction(String paramString, Bundle paramBundle)
    {
      MediaControllerCompatApi21.TransportControls.sendCustomAction(this.mControlsObj, paramString, paramBundle);
    }
    
    public void setRating(RatingCompat paramRatingCompat)
    {
      Object localObject1 = null;
      Object localObject2 = this.mControlsObj;
      if (paramRatingCompat == null) {}
      for (paramRatingCompat = (RatingCompat)localObject1;; paramRatingCompat = paramRatingCompat.getRating())
      {
        MediaControllerCompatApi21.TransportControls.setRating(localObject2, paramRatingCompat);
        return;
      }
    }
    
    public void skipToNext()
    {
      MediaControllerCompatApi21.TransportControls.skipToNext(this.mControlsObj);
    }
    
    public void skipToPrevious()
    {
      MediaControllerCompatApi21.TransportControls.skipToPrevious(this.mControlsObj);
    }
    
    public void skipToQueueItem(long paramLong)
    {
      MediaControllerCompatApi21.TransportControls.skipToQueueItem(this.mControlsObj, paramLong);
    }
    
    public void stop()
    {
      MediaControllerCompatApi21.TransportControls.stop(this.mControlsObj);
    }
  }
  
  static class TransportControlsBase
    extends MediaControllerCompat.TransportControls
  {
    private IMediaSession mBinder;
    
    public TransportControlsBase(IMediaSession paramIMediaSession)
    {
      this.mBinder = paramIMediaSession;
    }
    
    public void fastForward()
    {
      try
      {
        this.mBinder.fastForward();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in fastForward. " + localRemoteException);
      }
    }
    
    public void pause()
    {
      try
      {
        this.mBinder.pause();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in pause. " + localRemoteException);
      }
    }
    
    public void play()
    {
      try
      {
        this.mBinder.play();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in play. " + localRemoteException);
      }
    }
    
    public void playFromMediaId(String paramString, Bundle paramBundle)
    {
      try
      {
        this.mBinder.playFromMediaId(paramString, paramBundle);
        return;
      }
      catch (RemoteException paramString)
      {
        Log.e("MediaControllerCompat", "Dead object in playFromMediaId. " + paramString);
      }
    }
    
    public void playFromSearch(String paramString, Bundle paramBundle)
    {
      try
      {
        this.mBinder.playFromSearch(paramString, paramBundle);
        return;
      }
      catch (RemoteException paramString)
      {
        Log.e("MediaControllerCompat", "Dead object in playFromSearch. " + paramString);
      }
    }
    
    public void rewind()
    {
      try
      {
        this.mBinder.rewind();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in rewind. " + localRemoteException);
      }
    }
    
    public void seekTo(long paramLong)
    {
      try
      {
        this.mBinder.seekTo(paramLong);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in seekTo. " + localRemoteException);
      }
    }
    
    public void sendCustomAction(PlaybackStateCompat.CustomAction paramCustomAction, Bundle paramBundle)
    {
      sendCustomAction(paramCustomAction.getAction(), paramBundle);
    }
    
    public void sendCustomAction(String paramString, Bundle paramBundle)
    {
      try
      {
        this.mBinder.sendCustomAction(paramString, paramBundle);
        return;
      }
      catch (RemoteException paramString)
      {
        Log.e("MediaControllerCompat", "Dead object in sendCustomAction. " + paramString);
      }
    }
    
    public void setRating(RatingCompat paramRatingCompat)
    {
      try
      {
        this.mBinder.rate(paramRatingCompat);
        return;
      }
      catch (RemoteException paramRatingCompat)
      {
        Log.e("MediaControllerCompat", "Dead object in setRating. " + paramRatingCompat);
      }
    }
    
    public void skipToNext()
    {
      try
      {
        this.mBinder.next();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in skipToNext. " + localRemoteException);
      }
    }
    
    public void skipToPrevious()
    {
      try
      {
        this.mBinder.previous();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in skipToPrevious. " + localRemoteException);
      }
    }
    
    public void skipToQueueItem(long paramLong)
    {
      try
      {
        this.mBinder.skipToQueueItem(paramLong);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in skipToQueueItem. " + localRemoteException);
      }
    }
    
    public void stop()
    {
      try
      {
        this.mBinder.stop();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaControllerCompat", "Dead object in stop. " + localRemoteException);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/media/session/MediaControllerCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */