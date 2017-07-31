package android.media.session;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.MediaMetadata.Builder;
import android.media.Rating;
import android.media.VolumeProvider;
import android.media.VolumeProvider.Callback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import java.lang.ref.WeakReference;
import java.util.List;

public final class MediaSession
{
  public static final int FLAG_EXCLUSIVE_GLOBAL_PRIORITY = 65536;
  public static final int FLAG_HANDLES_MEDIA_BUTTONS = 1;
  public static final int FLAG_HANDLES_TRANSPORT_CONTROLS = 2;
  private static final String TAG = "MediaSession";
  private boolean mActive = false;
  private final ISession mBinder;
  private CallbackMessageHandler mCallback;
  private final CallbackStub mCbStub;
  private final MediaController mController;
  private final Object mLock = new Object();
  private final int mMaxBitmapSize;
  private PlaybackState mPlaybackState;
  private final Token mSessionToken;
  private VolumeProvider mVolumeProvider;
  
  public MediaSession(Context paramContext, String paramString)
  {
    this(paramContext, paramString, UserHandle.myUserId());
  }
  
  public MediaSession(Context paramContext, String paramString, int paramInt)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("context cannot be null.");
    }
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("tag cannot be null or empty");
    }
    this.mMaxBitmapSize = paramContext.getResources().getDimensionPixelSize(17104918);
    this.mCbStub = new CallbackStub(this);
    MediaSessionManager localMediaSessionManager = (MediaSessionManager)paramContext.getSystemService("media_session");
    try
    {
      this.mBinder = localMediaSessionManager.createSession(this.mCbStub, paramString, paramInt);
      this.mSessionToken = new Token(this.mBinder.getController());
      this.mController = new MediaController(paramContext, this.mSessionToken);
      return;
    }
    catch (RemoteException paramContext)
    {
      throw new RuntimeException("Remote error creating session.", paramContext);
    }
  }
  
  private void dispatchAdjustVolume(int paramInt)
  {
    postToCallback(21, Integer.valueOf(paramInt));
  }
  
  private void dispatchCustomAction(String paramString, Bundle paramBundle)
  {
    postToCallback(20, paramString, paramBundle);
  }
  
  private void dispatchFastForward()
  {
    postToCallback(16);
  }
  
  private void dispatchGetNowPlayingItemsCommand()
  {
    postToCallback(25);
  }
  
  private void dispatchMediaButton(Intent paramIntent)
  {
    postToCallback(2, paramIntent);
  }
  
  private void dispatchNext()
  {
    postToCallback(14);
  }
  
  private void dispatchPause()
  {
    postToCallback(12);
  }
  
  private void dispatchPlay()
  {
    postToCallback(7);
  }
  
  private void dispatchPlayFromMediaId(String paramString, Bundle paramBundle)
  {
    postToCallback(8, paramString, paramBundle);
  }
  
  private void dispatchPlayFromSearch(String paramString, Bundle paramBundle)
  {
    postToCallback(9, paramString, paramBundle);
  }
  
  private void dispatchPlayFromUri(Uri paramUri, Bundle paramBundle)
  {
    postToCallback(10, paramUri, paramBundle);
  }
  
  private void dispatchPrepare()
  {
    postToCallback(3);
  }
  
  private void dispatchPrepareFromMediaId(String paramString, Bundle paramBundle)
  {
    postToCallback(4, paramString, paramBundle);
  }
  
  private void dispatchPrepareFromSearch(String paramString, Bundle paramBundle)
  {
    postToCallback(5, paramString, paramBundle);
  }
  
  private void dispatchPrepareFromUri(Uri paramUri, Bundle paramBundle)
  {
    postToCallback(6, paramUri, paramBundle);
  }
  
  private void dispatchPrevious()
  {
    postToCallback(15);
  }
  
  private void dispatchRate(Rating paramRating)
  {
    postToCallback(19, paramRating);
  }
  
  private void dispatchRewind()
  {
    postToCallback(17);
  }
  
  private void dispatchSeekTo(long paramLong)
  {
    postToCallback(18, Long.valueOf(paramLong));
  }
  
  private void dispatchSetBrowsedPlayerCommand()
  {
    postToCallback(23);
  }
  
  private void dispatchSetPlayItemCommand(long paramLong, int paramInt)
  {
    postToCallback(24, new PlayItemToken(paramLong, paramInt));
  }
  
  private void dispatchSetVolumeTo(int paramInt)
  {
    postToCallback(22, Integer.valueOf(paramInt));
  }
  
  private void dispatchSkipToItem(long paramLong)
  {
    postToCallback(11, Long.valueOf(paramLong));
  }
  
  private void dispatchStop()
  {
    postToCallback(13);
  }
  
  public static boolean isActiveState(int paramInt)
  {
    switch (paramInt)
    {
    case 7: 
    default: 
      return false;
    }
    return true;
  }
  
  private void postCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
  {
    postToCallback(1, new Command(paramString, paramBundle, paramResultReceiver));
  }
  
  private void postToCallback(int paramInt)
  {
    postToCallback(paramInt, null);
  }
  
  private void postToCallback(int paramInt, Object paramObject)
  {
    postToCallback(paramInt, paramObject, null);
  }
  
  private void postToCallback(int paramInt, Object paramObject, Bundle paramBundle)
  {
    synchronized (this.mLock)
    {
      if (this.mCallback != null) {
        this.mCallback.post(paramInt, paramObject, paramBundle);
      }
      return;
    }
  }
  
  public String getCallingPackage()
  {
    try
    {
      String str = this.mBinder.getCallingPackage();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaSession", "Dead object in getCallingPackage.", localRemoteException);
    }
    return null;
  }
  
  public MediaController getController()
  {
    return this.mController;
  }
  
  public Token getSessionToken()
  {
    return this.mSessionToken;
  }
  
  public boolean isActive()
  {
    return this.mActive;
  }
  
  public void notifyRemoteVolumeChanged(VolumeProvider paramVolumeProvider)
  {
    Object localObject = this.mLock;
    if (paramVolumeProvider != null) {}
    try
    {
      if (paramVolumeProvider != this.mVolumeProvider)
      {
        Log.w("MediaSession", "Received update from stale volume provider");
        return;
      }
      return;
    }
    finally
    {
      try
      {
        this.mBinder.setCurrentVolume(paramVolumeProvider.getCurrentVolume());
        return;
      }
      catch (RemoteException paramVolumeProvider)
      {
        Log.e("MediaSession", "Error in notifyVolumeChanged", paramVolumeProvider);
      }
      paramVolumeProvider = finally;
    }
  }
  
  public void playItemResponse(boolean paramBoolean)
  {
    Log.d("MediaSession", "MediaSession: playItemResponse");
    try
    {
      this.mBinder.playItemResponse(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaSession", "Dead object in playItemResponse.", localRemoteException);
    }
  }
  
  public void release()
  {
    try
    {
      this.mBinder.destroy();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaSession", "Error releasing session: ", localRemoteException);
    }
  }
  
  public void sendSessionEvent(String paramString, Bundle paramBundle)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("event cannot be null or empty");
    }
    try
    {
      this.mBinder.sendEvent(paramString, paramBundle);
      return;
    }
    catch (RemoteException paramString)
    {
      Log.wtf("MediaSession", "Error sending event", paramString);
    }
  }
  
  public void setActive(boolean paramBoolean)
  {
    if (this.mActive == paramBoolean) {
      return;
    }
    try
    {
      this.mBinder.setActive(paramBoolean);
      this.mActive = paramBoolean;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaSession", "Failure in setActive.", localRemoteException);
    }
  }
  
  public void setCallback(Callback paramCallback)
  {
    setCallback(paramCallback, null);
  }
  
  public void setCallback(Callback paramCallback, Handler paramHandler)
  {
    localObject = this.mLock;
    if (paramCallback == null) {}
    try
    {
      if (this.mCallback != null) {
        Callback.-set0(CallbackMessageHandler.-get0(this.mCallback), null);
      }
      this.mCallback = null;
      return;
    }
    finally {}
    if (this.mCallback != null) {
      Callback.-set0(CallbackMessageHandler.-get0(this.mCallback), null);
    }
    Handler localHandler = paramHandler;
    if (paramHandler == null) {
      localHandler = new Handler();
    }
    Callback.-set0(paramCallback, this);
    this.mCallback = new CallbackMessageHandler(localHandler.getLooper(), paramCallback);
  }
  
  public void setExtras(Bundle paramBundle)
  {
    try
    {
      this.mBinder.setExtras(paramBundle);
      return;
    }
    catch (RemoteException paramBundle)
    {
      Log.wtf("Dead object in setExtras.", paramBundle);
    }
  }
  
  public void setFlags(int paramInt)
  {
    try
    {
      this.mBinder.setFlags(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaSession", "Failure in setFlags.", localRemoteException);
    }
  }
  
  public void setMediaButtonReceiver(PendingIntent paramPendingIntent)
  {
    try
    {
      this.mBinder.setMediaButtonReceiver(paramPendingIntent);
      return;
    }
    catch (RemoteException paramPendingIntent)
    {
      Log.wtf("MediaSession", "Failure in setMediaButtonReceiver.", paramPendingIntent);
    }
  }
  
  public void setMetadata(MediaMetadata paramMediaMetadata)
  {
    MediaMetadata localMediaMetadata = paramMediaMetadata;
    if (paramMediaMetadata != null) {
      localMediaMetadata = new MediaMetadata.Builder(paramMediaMetadata, this.mMaxBitmapSize).build();
    }
    try
    {
      this.mBinder.setMetadata(localMediaMetadata);
      return;
    }
    catch (RemoteException paramMediaMetadata)
    {
      Log.wtf("MediaSession", "Dead object in setPlaybackState.", paramMediaMetadata);
    }
  }
  
  public void setPlaybackState(PlaybackState paramPlaybackState)
  {
    this.mPlaybackState = paramPlaybackState;
    try
    {
      this.mBinder.setPlaybackState(paramPlaybackState);
      return;
    }
    catch (RemoteException paramPlaybackState)
    {
      Log.wtf("MediaSession", "Dead object in setPlaybackState.", paramPlaybackState);
    }
  }
  
  public void setPlaybackToLocal(AudioAttributes paramAudioAttributes)
  {
    if (paramAudioAttributes == null) {
      throw new IllegalArgumentException("Attributes cannot be null for local playback.");
    }
    try
    {
      this.mBinder.setPlaybackToLocal(paramAudioAttributes);
      return;
    }
    catch (RemoteException paramAudioAttributes)
    {
      Log.wtf("MediaSession", "Failure in setPlaybackToLocal.", paramAudioAttributes);
    }
  }
  
  public void setPlaybackToRemote(VolumeProvider paramVolumeProvider)
  {
    if (paramVolumeProvider == null) {
      throw new IllegalArgumentException("volumeProvider may not be null!");
    }
    synchronized (this.mLock)
    {
      this.mVolumeProvider = paramVolumeProvider;
      paramVolumeProvider.setCallback(new VolumeProvider.Callback()
      {
        public void onVolumeChanged(VolumeProvider paramAnonymousVolumeProvider)
        {
          MediaSession.this.notifyRemoteVolumeChanged(paramAnonymousVolumeProvider);
        }
      });
    }
  }
  
  public void setQueue(List<QueueItem> paramList)
  {
    Object localObject = null;
    try
    {
      ISession localISession = this.mBinder;
      if (paramList == null) {}
      for (paramList = (List<QueueItem>)localObject;; paramList = new ParceledListSlice(paramList))
      {
        localISession.setQueue(paramList);
        return;
      }
      return;
    }
    catch (RemoteException paramList)
    {
      Log.wtf("Dead object in setQueue.", paramList);
    }
  }
  
  public void setQueueTitle(CharSequence paramCharSequence)
  {
    try
    {
      this.mBinder.setQueueTitle(paramCharSequence);
      return;
    }
    catch (RemoteException paramCharSequence)
    {
      Log.wtf("Dead object in setQueueTitle.", paramCharSequence);
    }
  }
  
  public void setRatingType(int paramInt)
  {
    try
    {
      this.mBinder.setRatingType(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("MediaSession", "Error in setRatingType.", localRemoteException);
    }
  }
  
  public void setSessionActivity(PendingIntent paramPendingIntent)
  {
    try
    {
      this.mBinder.setLaunchPendingIntent(paramPendingIntent);
      return;
    }
    catch (RemoteException paramPendingIntent)
    {
      Log.wtf("MediaSession", "Failure in setLaunchPendingIntent.", paramPendingIntent);
    }
  }
  
  public void updateFolderInfoBrowsedPlayer(String paramString)
  {
    Log.d("MediaSession", "MediaSession: updateFolderInfoBrowsedPlayer");
    try
    {
      this.mBinder.updateFolderInfoBrowsedPlayer(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      Log.wtf("MediaSession", "Dead object in updateFolderInfoBrowsedPlayer.", paramString);
    }
  }
  
  public void updateNowPlayingContentChange()
  {
    Log.d("MediaSession", "MediaSession: updateNowPlayingContentChange");
    try
    {
      this.mBinder.updateNowPlayingContentChange();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaSession", "Dead object in updateNowPlayingContentChange.", localRemoteException);
    }
  }
  
  public void updateNowPlayingEntries(long[] paramArrayOfLong)
  {
    Log.d("MediaSession", "MediaSession: updateNowPlayingEntries");
    try
    {
      this.mBinder.updateNowPlayingEntries(paramArrayOfLong);
      return;
    }
    catch (RemoteException paramArrayOfLong)
    {
      Log.wtf("MediaSession", "Dead object in updateNowPlayingEntries.", paramArrayOfLong);
    }
  }
  
  public static abstract class Callback
  {
    private MediaSession mSession;
    
    public void getNowPlayingEntries() {}
    
    public void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver) {}
    
    public void onCustomAction(String paramString, Bundle paramBundle) {}
    
    public void onFastForward() {}
    
    public boolean onMediaButtonEvent(Intent paramIntent)
    {
      PlaybackState localPlaybackState;
      long l;
      if ((this.mSession != null) && ("android.intent.action.MEDIA_BUTTON".equals(paramIntent.getAction())))
      {
        paramIntent = (KeyEvent)paramIntent.getParcelableExtra("android.intent.extra.KEY_EVENT");
        if ((paramIntent != null) && (paramIntent.getAction() == 0))
        {
          localPlaybackState = MediaSession.-get1(this.mSession);
          if (localPlaybackState != null) {
            break label146;
          }
          l = 0L;
          switch (paramIntent.getKeyCode())
          {
          }
        }
      }
      label146:
      int i;
      int j;
      label293:
      label341:
      label346:
      label352:
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      return false;
                      l = localPlaybackState.getActions();
                      break;
                    } while ((0x4 & l) == 0L);
                    onPlay();
                    return true;
                  } while ((0x2 & l) == 0L);
                  onPause();
                  return true;
                } while ((0x20 & l) == 0L);
                onSkipToNext();
                return true;
              } while ((0x10 & l) == 0L);
              onSkipToPrevious();
              return true;
            } while ((1L & l) == 0L);
            onStop();
            return true;
          } while ((0x40 & l) == 0L);
          onFastForward();
          return true;
        } while ((0x8 & l) == 0L);
        onRewind();
        return true;
        if (localPlaybackState == null)
        {
          i = 0;
          if ((0x204 & l) == 0L) {
            break label341;
          }
          j = 1;
          if ((0x202 & l) == 0L) {
            break label346;
          }
        }
        for (int k = 1;; k = 0)
        {
          if ((i == 0) || (k == 0)) {
            break label352;
          }
          onPause();
          return true;
          if (localPlaybackState.getState() == 3)
          {
            i = 1;
            break;
          }
          i = 0;
          break;
          j = 0;
          break label293;
        }
      } while ((i != 0) || (j == 0));
      onPlay();
      return true;
    }
    
    public void onPause() {}
    
    public void onPlay() {}
    
    public void onPlayFromMediaId(String paramString, Bundle paramBundle) {}
    
    public void onPlayFromSearch(String paramString, Bundle paramBundle) {}
    
    public void onPlayFromUri(Uri paramUri, Bundle paramBundle) {}
    
    public void onPrepare() {}
    
    public void onPrepareFromMediaId(String paramString, Bundle paramBundle) {}
    
    public void onPrepareFromSearch(String paramString, Bundle paramBundle) {}
    
    public void onPrepareFromUri(Uri paramUri, Bundle paramBundle) {}
    
    public void onRewind() {}
    
    public void onSeekTo(long paramLong) {}
    
    public void onSetRating(Rating paramRating) {}
    
    public void onSkipToNext() {}
    
    public void onSkipToPrevious() {}
    
    public void onSkipToQueueItem(long paramLong) {}
    
    public void onStop() {}
    
    public void setBrowsedPlayer() {}
    
    public void setPlayItem(int paramInt, long paramLong) {}
  }
  
  private class CallbackMessageHandler
    extends Handler
  {
    private static final int MSG_ADJUST_VOLUME = 21;
    private static final int MSG_COMMAND = 1;
    private static final int MSG_CUSTOM_ACTION = 20;
    private static final int MSG_FAST_FORWARD = 16;
    private static final int MSG_GET_NOW_PLAYING_ITEMS = 25;
    private static final int MSG_MEDIA_BUTTON = 2;
    private static final int MSG_NEXT = 14;
    private static final int MSG_PAUSE = 12;
    private static final int MSG_PLAY = 7;
    private static final int MSG_PLAY_MEDIA_ID = 8;
    private static final int MSG_PLAY_SEARCH = 9;
    private static final int MSG_PLAY_URI = 10;
    private static final int MSG_PREPARE = 3;
    private static final int MSG_PREPARE_MEDIA_ID = 4;
    private static final int MSG_PREPARE_SEARCH = 5;
    private static final int MSG_PREPARE_URI = 6;
    private static final int MSG_PREVIOUS = 15;
    private static final int MSG_RATE = 19;
    private static final int MSG_REWIND = 17;
    private static final int MSG_SEEK_TO = 18;
    private static final int MSG_SET_BROWSED_PLAYER = 23;
    private static final int MSG_SET_PLAY_ITEM = 24;
    private static final int MSG_SET_VOLUME = 22;
    private static final int MSG_SKIP_TO_ITEM = 11;
    private static final int MSG_STOP = 13;
    private MediaSession.Callback mCallback;
    
    public CallbackMessageHandler(Looper paramLooper, MediaSession.Callback paramCallback)
    {
      super(null, true);
      this.mCallback = paramCallback;
    }
    
    public void handleMessage(Message paramMessage)
    {
      VolumeProvider localVolumeProvider;
      switch (paramMessage.what)
      {
      default: 
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
      case 8: 
      case 9: 
      case 10: 
      case 11: 
      case 12: 
      case 13: 
      case 14: 
      case 15: 
      case 16: 
      case 17: 
      case 18: 
      case 19: 
      case 20: 
      case 21: 
        for (;;)
        {
          return;
          paramMessage = (MediaSession.Command)paramMessage.obj;
          this.mCallback.onCommand(paramMessage.command, paramMessage.extras, paramMessage.stub);
          return;
          this.mCallback.onMediaButtonEvent((Intent)paramMessage.obj);
          return;
          this.mCallback.onPrepare();
          return;
          this.mCallback.onPrepareFromMediaId((String)paramMessage.obj, paramMessage.getData());
          return;
          this.mCallback.onPrepareFromSearch((String)paramMessage.obj, paramMessage.getData());
          return;
          this.mCallback.onPrepareFromUri((Uri)paramMessage.obj, paramMessage.getData());
          return;
          this.mCallback.onPlay();
          return;
          this.mCallback.onPlayFromMediaId((String)paramMessage.obj, paramMessage.getData());
          return;
          this.mCallback.onPlayFromSearch((String)paramMessage.obj, paramMessage.getData());
          return;
          this.mCallback.onPlayFromUri((Uri)paramMessage.obj, paramMessage.getData());
          return;
          this.mCallback.onSkipToQueueItem(((Long)paramMessage.obj).longValue());
          return;
          this.mCallback.onPause();
          return;
          this.mCallback.onStop();
          return;
          this.mCallback.onSkipToNext();
          return;
          this.mCallback.onSkipToPrevious();
          return;
          this.mCallback.onFastForward();
          return;
          this.mCallback.onRewind();
          return;
          this.mCallback.onSeekTo(((Long)paramMessage.obj).longValue());
          return;
          this.mCallback.onSetRating((Rating)paramMessage.obj);
          return;
          this.mCallback.onCustomAction((String)paramMessage.obj, paramMessage.getData());
          return;
          synchronized (MediaSession.-get0(MediaSession.this))
          {
            localVolumeProvider = MediaSession.-get2(MediaSession.this);
            if (localVolumeProvider != null)
            {
              localVolumeProvider.onAdjustVolume(((Integer)paramMessage.obj).intValue());
              return;
            }
          }
        }
      case 22: 
      case 23: 
        synchronized (MediaSession.-get0(MediaSession.this))
        {
          localVolumeProvider = MediaSession.-get2(MediaSession.this);
          if (localVolumeProvider != null) {
            localVolumeProvider.onSetVolumeTo(((Integer)paramMessage.obj).intValue());
          }
          Log.d("MediaSession", "MSG_SET_BROWSED_PLAYER received in CallbackMessageHandler");
          this.mCallback.setBrowsedPlayer();
          return;
        }
      case 24: 
        Log.d("MediaSession", "MSG_SET_PLAY_ITEM received in CallbackMessageHandler");
        paramMessage = (MediaSession.PlayItemToken)paramMessage.obj;
        this.mCallback.setPlayItem(paramMessage.getScope(), paramMessage.getUid());
        return;
      }
      Log.d("MediaSession", "MSG_GET_NOW_PLAYING_ITEMS received in CallbackMessageHandler");
      this.mCallback.getNowPlayingEntries();
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
  
  public static class CallbackStub
    extends ISessionCallback.Stub
  {
    private WeakReference<MediaSession> mMediaSession;
    
    public CallbackStub(MediaSession paramMediaSession)
    {
      this.mMediaSession = new WeakReference(paramMediaSession);
    }
    
    public void getRemoteControlClientNowPlayingEntries()
      throws RemoteException
    {
      Log.d("MediaSession", "getRemoteControlClientNowPlayingEntries in CallbackStub");
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap3(localMediaSession);
      }
    }
    
    public void onAdjustVolume(int paramInt)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap0(localMediaSession, paramInt);
      }
    }
    
    public void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap24(localMediaSession, paramString, paramBundle, paramResultReceiver);
      }
    }
    
    public void onCustomAction(String paramString, Bundle paramBundle)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap1(localMediaSession, paramString, paramBundle);
      }
    }
    
    public void onFastForward()
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap2(localMediaSession);
      }
    }
    
    public void onMediaButton(Intent paramIntent, int paramInt, ResultReceiver paramResultReceiver)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {}
      try
      {
        MediaSession.-wrap4(localMediaSession, paramIntent);
        return;
      }
      finally
      {
        if (paramResultReceiver != null) {
          paramResultReceiver.send(paramInt, null);
        }
      }
    }
    
    public void onNext()
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap5(localMediaSession);
      }
    }
    
    public void onPause()
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap6(localMediaSession);
      }
    }
    
    public void onPlay()
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap10(localMediaSession);
      }
    }
    
    public void onPlayFromMediaId(String paramString, Bundle paramBundle)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap7(localMediaSession, paramString, paramBundle);
      }
    }
    
    public void onPlayFromSearch(String paramString, Bundle paramBundle)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap8(localMediaSession, paramString, paramBundle);
      }
    }
    
    public void onPlayFromUri(Uri paramUri, Bundle paramBundle)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap9(localMediaSession, paramUri, paramBundle);
      }
    }
    
    public void onPrepare()
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap14(localMediaSession);
      }
    }
    
    public void onPrepareFromMediaId(String paramString, Bundle paramBundle)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap11(localMediaSession, paramString, paramBundle);
      }
    }
    
    public void onPrepareFromSearch(String paramString, Bundle paramBundle)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap12(localMediaSession, paramString, paramBundle);
      }
    }
    
    public void onPrepareFromUri(Uri paramUri, Bundle paramBundle)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap13(localMediaSession, paramUri, paramBundle);
      }
    }
    
    public void onPrevious()
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap15(localMediaSession);
      }
    }
    
    public void onRate(Rating paramRating)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap16(localMediaSession, paramRating);
      }
    }
    
    public void onRewind()
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap17(localMediaSession);
      }
    }
    
    public void onSeekTo(long paramLong)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap18(localMediaSession, paramLong);
      }
    }
    
    public void onSetVolumeTo(int paramInt)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap21(localMediaSession, paramInt);
      }
    }
    
    public void onSkipToTrack(long paramLong)
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap22(localMediaSession, paramLong);
      }
    }
    
    public void onStop()
    {
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap23(localMediaSession);
      }
    }
    
    public void setRemoteControlClientBrowsedPlayer()
      throws RemoteException
    {
      Log.d("MediaSession", "setRemoteControlClientBrowsedPlayer in CallbackStub");
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap19(localMediaSession);
      }
    }
    
    public void setRemoteControlClientPlayItem(long paramLong, int paramInt)
      throws RemoteException
    {
      Log.d("MediaSession", "setRemoteControlClientPlayItem in CallbackStub");
      MediaSession localMediaSession = (MediaSession)this.mMediaSession.get();
      if (localMediaSession != null) {
        MediaSession.-wrap20(localMediaSession, paramLong, paramInt);
      }
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
  
  private class PlayItemToken
  {
    private int mScope;
    private long mUid;
    
    public PlayItemToken(long paramLong, int paramInt)
    {
      this.mUid = paramLong;
      this.mScope = paramInt;
    }
    
    public int getScope()
    {
      return this.mScope;
    }
    
    public long getUid()
    {
      return this.mUid;
    }
  }
  
  public static final class QueueItem
    implements Parcelable
  {
    public static final Parcelable.Creator<QueueItem> CREATOR = new Parcelable.Creator()
    {
      public MediaSession.QueueItem createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaSession.QueueItem(paramAnonymousParcel, null);
      }
      
      public MediaSession.QueueItem[] newArray(int paramAnonymousInt)
      {
        return new MediaSession.QueueItem[paramAnonymousInt];
      }
    };
    public static final int UNKNOWN_ID = -1;
    private final MediaDescription mDescription;
    private final long mId;
    
    public QueueItem(MediaDescription paramMediaDescription, long paramLong)
    {
      if (paramMediaDescription == null) {
        throw new IllegalArgumentException("Description cannot be null.");
      }
      if (paramLong == -1L) {
        throw new IllegalArgumentException("Id cannot be QueueItem.UNKNOWN_ID");
      }
      this.mDescription = paramMediaDescription;
      this.mId = paramLong;
    }
    
    private QueueItem(Parcel paramParcel)
    {
      this.mDescription = ((MediaDescription)MediaDescription.CREATOR.createFromParcel(paramParcel));
      this.mId = paramParcel.readLong();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public MediaDescription getDescription()
    {
      return this.mDescription;
    }
    
    public long getQueueId()
    {
      return this.mId;
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
  
  public static final class Token
    implements Parcelable
  {
    public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator()
    {
      public MediaSession.Token createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaSession.Token(ISessionController.Stub.asInterface(paramAnonymousParcel.readStrongBinder()));
      }
      
      public MediaSession.Token[] newArray(int paramAnonymousInt)
      {
        return new MediaSession.Token[paramAnonymousInt];
      }
    };
    private ISessionController mBinder;
    
    public Token(ISessionController paramISessionController)
    {
      this.mBinder = paramISessionController;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (Token)paramObject;
      if (this.mBinder == null)
      {
        if (((Token)paramObject).mBinder != null) {
          return false;
        }
      }
      else if (!this.mBinder.asBinder().equals(((Token)paramObject).mBinder.asBinder())) {
        return false;
      }
      return true;
    }
    
    ISessionController getBinder()
    {
      return this.mBinder;
    }
    
    public int hashCode()
    {
      if (this.mBinder == null) {}
      for (int i = 0;; i = this.mBinder.asBinder().hashCode()) {
        return i + 31;
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeStrongBinder(this.mBinder.asBinder());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/session/MediaSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */