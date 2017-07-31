package android.media.session;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.media.AudioAttributes;
import android.media.MediaMetadata;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class MediaController
{
  private static final int MSG_DESTROYED = 8;
  private static final int MSG_EVENT = 1;
  private static final int MSG_FOLDER_INFO_BROWSED_PLAYER = 9;
  private static final int MSG_PLAY_ITEM_RESPONSE = 12;
  private static final int MSG_UPDATE_EXTRAS = 7;
  private static final int MSG_UPDATE_METADATA = 3;
  private static final int MSG_UPDATE_NOWPLAYING_CONTENT_CHANGE = 11;
  private static final int MSG_UPDATE_NOWPLAYING_ENTRIES = 10;
  private static final int MSG_UPDATE_PLAYBACK_STATE = 2;
  private static final int MSG_UPDATE_QUEUE = 5;
  private static final int MSG_UPDATE_QUEUE_TITLE = 6;
  private static final int MSG_UPDATE_VOLUME = 4;
  private static final String TAG = "MediaController";
  private final ArrayList<MessageHandler> mCallbacks = new ArrayList();
  private boolean mCbRegistered = false;
  private final CallbackStub mCbStub = new CallbackStub(this);
  private final Context mContext;
  private final Object mLock = new Object();
  private String mPackageName;
  private final ISessionController mSessionBinder;
  private String mTag;
  private final MediaSession.Token mToken;
  private final TransportControls mTransportControls;
  
  public MediaController(Context paramContext, ISessionController paramISessionController)
  {
    if (paramISessionController == null) {
      throw new IllegalArgumentException("Session token cannot be null");
    }
    if (paramContext == null) {
      throw new IllegalArgumentException("Context cannot be null");
    }
    this.mSessionBinder = paramISessionController;
    this.mTransportControls = new TransportControls(null);
    this.mToken = new MediaSession.Token(paramISessionController);
    this.mContext = paramContext;
  }
  
  public MediaController(Context paramContext, MediaSession.Token paramToken)
  {
    this(paramContext, paramToken.getBinder());
  }
  
  private void addCallbackLocked(Callback paramCallback, Handler paramHandler)
  {
    if (getHandlerForCallbackLocked(paramCallback) != null)
    {
      Log.w("MediaController", "Callback is already added, ignoring");
      return;
    }
    paramCallback = new MessageHandler(paramHandler.getLooper(), paramCallback);
    this.mCallbacks.add(paramCallback);
    MessageHandler.-set0(paramCallback, true);
    if (!this.mCbRegistered) {}
    try
    {
      this.mSessionBinder.registerCallbackListener(this.mCbStub);
      this.mCbRegistered = true;
      return;
    }
    catch (RemoteException paramCallback)
    {
      Log.e("MediaController", "Dead object in registerCallback", paramCallback);
    }
  }
  
  private MessageHandler getHandlerForCallbackLocked(Callback paramCallback)
  {
    if (paramCallback == null) {
      throw new IllegalArgumentException("Callback cannot be null");
    }
    int i = this.mCallbacks.size() - 1;
    while (i >= 0)
    {
      MessageHandler localMessageHandler = (MessageHandler)this.mCallbacks.get(i);
      if (paramCallback == MessageHandler.-get0(localMessageHandler)) {
        return localMessageHandler;
      }
      i -= 1;
    }
    return null;
  }
  
  private final void postMessage(int paramInt, Object paramObject, Bundle paramBundle)
  {
    synchronized (this.mLock)
    {
      int i = this.mCallbacks.size() - 1;
      while (i >= 0)
      {
        ((MessageHandler)this.mCallbacks.get(i)).post(paramInt, paramObject, paramBundle);
        i -= 1;
      }
      return;
    }
  }
  
  private boolean removeCallbackLocked(Callback paramCallback)
  {
    boolean bool = false;
    int i = this.mCallbacks.size() - 1;
    while (i >= 0)
    {
      MessageHandler localMessageHandler = (MessageHandler)this.mCallbacks.get(i);
      if (paramCallback == MessageHandler.-get0(localMessageHandler))
      {
        this.mCallbacks.remove(i);
        bool = true;
        MessageHandler.-set0(localMessageHandler, false);
      }
      i -= 1;
    }
    if ((this.mCbRegistered) && (this.mCallbacks.size() == 0)) {}
    try
    {
      this.mSessionBinder.unregisterCallbackListener(this.mCbStub);
      this.mCbRegistered = false;
      return bool;
    }
    catch (RemoteException paramCallback)
    {
      for (;;)
      {
        Log.e("MediaController", "Dead object in removeCallbackLocked");
      }
    }
  }
  
  public void adjustVolume(int paramInt1, int paramInt2)
  {
    try
    {
      this.mSessionBinder.adjustVolume(paramInt1, paramInt2, this.mContext.getPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaController", "Error calling adjustVolumeBy.", localRemoteException);
    }
  }
  
  public boolean controlsSameSession(MediaController paramMediaController)
  {
    boolean bool = false;
    if (paramMediaController == null) {
      return false;
    }
    if (this.mSessionBinder.asBinder() == paramMediaController.getSessionBinder().asBinder()) {
      bool = true;
    }
    return bool;
  }
  
  public boolean dispatchMediaButtonEvent(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent == null) {
      throw new IllegalArgumentException("KeyEvent may not be null");
    }
    if (!KeyEvent.isMediaKey(paramKeyEvent.getKeyCode())) {
      return false;
    }
    try
    {
      boolean bool = this.mSessionBinder.sendMediaButton(paramKeyEvent);
      return bool;
    }
    catch (RemoteException paramKeyEvent) {}
    return false;
  }
  
  public Bundle getExtras()
  {
    try
    {
      Bundle localBundle = this.mSessionBinder.getExtras();
      return localBundle;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaController", "Error calling getExtras", localRemoteException);
    }
    return null;
  }
  
  public long getFlags()
  {
    try
    {
      long l = this.mSessionBinder.getFlags();
      return l;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaController", "Error calling getFlags.", localRemoteException);
    }
    return 0L;
  }
  
  public MediaMetadata getMetadata()
  {
    try
    {
      MediaMetadata localMediaMetadata = this.mSessionBinder.getMetadata();
      return localMediaMetadata;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaController", "Error calling getMetadata.", localRemoteException);
    }
    return null;
  }
  
  public String getPackageName()
  {
    if (this.mPackageName == null) {}
    try
    {
      this.mPackageName = this.mSessionBinder.getPackageName();
      return this.mPackageName;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.d("MediaController", "Dead object in getPackageName.", localRemoteException);
      }
    }
  }
  
  public PlaybackInfo getPlaybackInfo()
  {
    try
    {
      Object localObject = this.mSessionBinder.getVolumeAttributes();
      localObject = new PlaybackInfo(((ParcelableVolumeInfo)localObject).volumeType, ((ParcelableVolumeInfo)localObject).audioAttrs, ((ParcelableVolumeInfo)localObject).controlType, ((ParcelableVolumeInfo)localObject).maxVolume, ((ParcelableVolumeInfo)localObject).currentVolume);
      return (PlaybackInfo)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaController", "Error calling getAudioInfo.", localRemoteException);
    }
    return null;
  }
  
  public PlaybackState getPlaybackState()
  {
    try
    {
      PlaybackState localPlaybackState = this.mSessionBinder.getPlaybackState();
      return localPlaybackState;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaController", "Error calling getPlaybackState.", localRemoteException);
    }
    return null;
  }
  
  public List<MediaSession.QueueItem> getQueue()
  {
    try
    {
      Object localObject = this.mSessionBinder.getQueue();
      if (localObject != null)
      {
        localObject = ((ParceledListSlice)localObject).getList();
        return (List<MediaSession.QueueItem>)localObject;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaController", "Error calling getQueue.", localRemoteException);
    }
    return null;
  }
  
  public CharSequence getQueueTitle()
  {
    try
    {
      CharSequence localCharSequence = this.mSessionBinder.getQueueTitle();
      return localCharSequence;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaController", "Error calling getQueueTitle", localRemoteException);
    }
    return null;
  }
  
  public int getRatingType()
  {
    try
    {
      int i = this.mSessionBinder.getRatingType();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaController", "Error calling getRatingType.", localRemoteException);
    }
    return 0;
  }
  
  public PendingIntent getSessionActivity()
  {
    try
    {
      PendingIntent localPendingIntent = this.mSessionBinder.getLaunchPendingIntent();
      return localPendingIntent;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaController", "Error calling getPendingIntent.", localRemoteException);
    }
    return null;
  }
  
  ISessionController getSessionBinder()
  {
    return this.mSessionBinder;
  }
  
  public MediaSession.Token getSessionToken()
  {
    return this.mToken;
  }
  
  public String getTag()
  {
    if (this.mTag == null) {}
    try
    {
      this.mTag = this.mSessionBinder.getTag();
      return this.mTag;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.d("MediaController", "Dead object in getTag.", localRemoteException);
      }
    }
  }
  
  public TransportControls getTransportControls()
  {
    return this.mTransportControls;
  }
  
  public void registerCallback(Callback paramCallback)
  {
    registerCallback(paramCallback, null);
  }
  
  public void registerCallback(Callback paramCallback, Handler arg2)
  {
    if (paramCallback == null) {
      throw new IllegalArgumentException("callback must not be null");
    }
    Handler localHandler = ???;
    if (??? == null) {
      localHandler = new Handler();
    }
    synchronized (this.mLock)
    {
      addCallbackLocked(paramCallback, localHandler);
      return;
    }
  }
  
  public void sendCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("command cannot be null or empty");
    }
    try
    {
      this.mSessionBinder.sendCommand(paramString, paramBundle, paramResultReceiver);
      return;
    }
    catch (RemoteException paramString)
    {
      Log.d("MediaController", "Dead object in sendCommand.", paramString);
    }
  }
  
  public void setVolumeTo(int paramInt1, int paramInt2)
  {
    try
    {
      this.mSessionBinder.setVolumeTo(paramInt1, paramInt2, this.mContext.getPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.wtf("MediaController", "Error calling setVolumeTo.", localRemoteException);
    }
  }
  
  public void unregisterCallback(Callback paramCallback)
  {
    if (paramCallback == null) {
      throw new IllegalArgumentException("callback must not be null");
    }
    synchronized (this.mLock)
    {
      removeCallbackLocked(paramCallback);
      return;
    }
  }
  
  public static abstract class Callback
  {
    public void onAudioInfoChanged(MediaController.PlaybackInfo paramPlaybackInfo) {}
    
    public void onExtrasChanged(Bundle paramBundle) {}
    
    public void onMetadataChanged(MediaMetadata paramMediaMetadata) {}
    
    public void onPlayItemResponse(boolean paramBoolean) {}
    
    public void onPlaybackStateChanged(PlaybackState paramPlaybackState) {}
    
    public void onQueueChanged(List<MediaSession.QueueItem> paramList) {}
    
    public void onQueueTitleChanged(CharSequence paramCharSequence) {}
    
    public void onSessionDestroyed() {}
    
    public void onSessionEvent(String paramString, Bundle paramBundle) {}
    
    public void onUpdateFolderInfoBrowsedPlayer(String paramString) {}
    
    public void onUpdateNowPlayingContentChange() {}
    
    public void onUpdateNowPlayingEntries(long[] paramArrayOfLong) {}
  }
  
  private static final class CallbackStub
    extends ISessionControllerCallback.Stub
  {
    private final WeakReference<MediaController> mController;
    
    public CallbackStub(MediaController paramMediaController)
    {
      this.mController = new WeakReference(paramMediaController);
    }
    
    public void onEvent(String paramString, Bundle paramBundle)
    {
      MediaController localMediaController = (MediaController)this.mController.get();
      if (localMediaController != null) {
        MediaController.-wrap0(localMediaController, 1, paramString, paramBundle);
      }
    }
    
    public void onExtrasChanged(Bundle paramBundle)
    {
      MediaController localMediaController = (MediaController)this.mController.get();
      if (localMediaController != null) {
        MediaController.-wrap0(localMediaController, 7, paramBundle, null);
      }
    }
    
    public void onMetadataChanged(MediaMetadata paramMediaMetadata)
    {
      MediaController localMediaController = (MediaController)this.mController.get();
      if (localMediaController != null) {
        MediaController.-wrap0(localMediaController, 3, paramMediaMetadata, null);
      }
    }
    
    public void onPlayItemResponse(boolean paramBoolean)
    {
      Log.d("MediaController", "CallBackStub: onPlayItemResponse");
      MediaController localMediaController = (MediaController)this.mController.get();
      if (localMediaController != null) {
        MediaController.-wrap0(localMediaController, 12, new Boolean(paramBoolean), null);
      }
    }
    
    public void onPlaybackStateChanged(PlaybackState paramPlaybackState)
    {
      MediaController localMediaController = (MediaController)this.mController.get();
      if (localMediaController != null) {
        MediaController.-wrap0(localMediaController, 2, paramPlaybackState, null);
      }
    }
    
    public void onQueueChanged(ParceledListSlice paramParceledListSlice)
    {
      if (paramParceledListSlice == null) {}
      for (paramParceledListSlice = null;; paramParceledListSlice = paramParceledListSlice.getList())
      {
        MediaController localMediaController = (MediaController)this.mController.get();
        if (localMediaController != null) {
          MediaController.-wrap0(localMediaController, 5, paramParceledListSlice, null);
        }
        return;
      }
    }
    
    public void onQueueTitleChanged(CharSequence paramCharSequence)
    {
      MediaController localMediaController = (MediaController)this.mController.get();
      if (localMediaController != null) {
        MediaController.-wrap0(localMediaController, 6, paramCharSequence, null);
      }
    }
    
    public void onSessionDestroyed()
    {
      MediaController localMediaController = (MediaController)this.mController.get();
      if (localMediaController != null) {
        MediaController.-wrap0(localMediaController, 8, null, null);
      }
    }
    
    public void onUpdateFolderInfoBrowsedPlayer(String paramString)
    {
      Log.d("MediaController", "CallBackStub: onUpdateFolderInfoBrowsedPlayer");
      MediaController localMediaController = (MediaController)this.mController.get();
      if (localMediaController != null) {
        MediaController.-wrap0(localMediaController, 9, paramString, null);
      }
    }
    
    public void onUpdateNowPlayingContentChange()
    {
      Log.d("MediaController", "CallBackStub: onUpdateNowPlayingContentChange");
      MediaController localMediaController = (MediaController)this.mController.get();
      if (localMediaController != null) {
        MediaController.-wrap0(localMediaController, 11, null, null);
      }
    }
    
    public void onUpdateNowPlayingEntries(long[] paramArrayOfLong)
    {
      Log.d("MediaController", "CallBackStub: onUpdateNowPlayingEntries");
      MediaController localMediaController = (MediaController)this.mController.get();
      if (localMediaController != null) {
        MediaController.-wrap0(localMediaController, 10, paramArrayOfLong, null);
      }
    }
    
    public void onVolumeInfoChanged(ParcelableVolumeInfo paramParcelableVolumeInfo)
    {
      MediaController localMediaController = (MediaController)this.mController.get();
      if (localMediaController != null) {
        MediaController.-wrap0(localMediaController, 4, new MediaController.PlaybackInfo(paramParcelableVolumeInfo.volumeType, paramParcelableVolumeInfo.audioAttrs, paramParcelableVolumeInfo.controlType, paramParcelableVolumeInfo.maxVolume, paramParcelableVolumeInfo.currentVolume), null);
      }
    }
  }
  
  private static final class MessageHandler
    extends Handler
  {
    private final MediaController.Callback mCallback;
    private boolean mRegistered = false;
    
    public MessageHandler(Looper paramLooper, MediaController.Callback paramCallback)
    {
      super(null, true);
      this.mCallback = paramCallback;
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (!this.mRegistered) {
        return;
      }
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        this.mCallback.onSessionEvent((String)paramMessage.obj, paramMessage.getData());
        return;
      case 2: 
        this.mCallback.onPlaybackStateChanged((PlaybackState)paramMessage.obj);
        return;
      case 3: 
        this.mCallback.onMetadataChanged((MediaMetadata)paramMessage.obj);
        return;
      case 5: 
        this.mCallback.onQueueChanged((List)paramMessage.obj);
        return;
      case 6: 
        this.mCallback.onQueueTitleChanged((CharSequence)paramMessage.obj);
        return;
      case 7: 
        this.mCallback.onExtrasChanged((Bundle)paramMessage.obj);
        return;
      case 4: 
        this.mCallback.onAudioInfoChanged((MediaController.PlaybackInfo)paramMessage.obj);
        return;
      case 8: 
        this.mCallback.onSessionDestroyed();
        return;
      case 9: 
        this.mCallback.onUpdateFolderInfoBrowsedPlayer((String)paramMessage.obj);
        return;
      case 10: 
        this.mCallback.onUpdateNowPlayingEntries((long[])paramMessage.obj);
        return;
      case 11: 
        this.mCallback.onUpdateNowPlayingContentChange();
        return;
      }
      this.mCallback.onPlayItemResponse(((Boolean)paramMessage.obj).booleanValue());
    }
    
    public void post(int paramInt, Object paramObject, Bundle paramBundle)
    {
      paramObject = obtainMessage(paramInt, paramObject);
      ((Message)paramObject).setData(paramBundle);
      ((Message)paramObject).sendToTarget();
    }
  }
  
  public static final class PlaybackInfo
  {
    public static final int PLAYBACK_TYPE_LOCAL = 1;
    public static final int PLAYBACK_TYPE_REMOTE = 2;
    private final AudioAttributes mAudioAttrs;
    private final int mCurrentVolume;
    private final int mMaxVolume;
    private final int mVolumeControl;
    private final int mVolumeType;
    
    public PlaybackInfo(int paramInt1, AudioAttributes paramAudioAttributes, int paramInt2, int paramInt3, int paramInt4)
    {
      this.mVolumeType = paramInt1;
      this.mAudioAttrs = paramAudioAttributes;
      this.mVolumeControl = paramInt2;
      this.mMaxVolume = paramInt3;
      this.mCurrentVolume = paramInt4;
    }
    
    public AudioAttributes getAudioAttributes()
    {
      return this.mAudioAttrs;
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
      return this.mVolumeType;
    }
    
    public int getVolumeControl()
    {
      return this.mVolumeControl;
    }
  }
  
  public final class TransportControls
  {
    private static final String TAG = "TransportController";
    
    private TransportControls() {}
    
    public void fastForward()
    {
      try
      {
        MediaController.-get0(MediaController.this).fastForward();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling fastForward.", localRemoteException);
      }
    }
    
    public void getRemoteControlClientNowPlayingEntries()
    {
      Log.d("TransportController", "getRemoteControlClientNowPlayingEntries in TransportControls");
      try
      {
        MediaController.-get0(MediaController.this).getRemoteControlClientNowPlayingEntries();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling getRemoteControlClientNowPlayingEntries.", localRemoteException);
      }
    }
    
    public void pause()
    {
      try
      {
        MediaController.-get0(MediaController.this).pause();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling pause.", localRemoteException);
      }
    }
    
    public void play()
    {
      try
      {
        MediaController.-get0(MediaController.this).play();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling play.", localRemoteException);
      }
    }
    
    public void playFromMediaId(String paramString, Bundle paramBundle)
    {
      if (TextUtils.isEmpty(paramString)) {
        throw new IllegalArgumentException("You must specify a non-empty String for playFromMediaId.");
      }
      try
      {
        MediaController.-get0(MediaController.this).playFromMediaId(paramString, paramBundle);
        return;
      }
      catch (RemoteException paramBundle)
      {
        Log.wtf("TransportController", "Error calling play(" + paramString + ").", paramBundle);
      }
    }
    
    public void playFromSearch(String paramString, Bundle paramBundle)
    {
      String str = paramString;
      if (paramString == null) {
        str = "";
      }
      try
      {
        MediaController.-get0(MediaController.this).playFromSearch(str, paramBundle);
        return;
      }
      catch (RemoteException paramString)
      {
        Log.wtf("TransportController", "Error calling play(" + str + ").", paramString);
      }
    }
    
    public void playFromUri(Uri paramUri, Bundle paramBundle)
    {
      if ((paramUri == null) || (Uri.EMPTY.equals(paramUri))) {
        throw new IllegalArgumentException("You must specify a non-empty Uri for playFromUri.");
      }
      try
      {
        MediaController.-get0(MediaController.this).playFromUri(paramUri, paramBundle);
        return;
      }
      catch (RemoteException paramBundle)
      {
        Log.wtf("TransportController", "Error calling play(" + paramUri + ").", paramBundle);
      }
    }
    
    public void prepare()
    {
      try
      {
        MediaController.-get0(MediaController.this).prepare();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling prepare.", localRemoteException);
      }
    }
    
    public void prepareFromMediaId(String paramString, Bundle paramBundle)
    {
      if (TextUtils.isEmpty(paramString)) {
        throw new IllegalArgumentException("You must specify a non-empty String for prepareFromMediaId.");
      }
      try
      {
        MediaController.-get0(MediaController.this).prepareFromMediaId(paramString, paramBundle);
        return;
      }
      catch (RemoteException paramBundle)
      {
        Log.wtf("TransportController", "Error calling prepare(" + paramString + ").", paramBundle);
      }
    }
    
    public void prepareFromSearch(String paramString, Bundle paramBundle)
    {
      String str = paramString;
      if (paramString == null) {
        str = "";
      }
      try
      {
        MediaController.-get0(MediaController.this).prepareFromSearch(str, paramBundle);
        return;
      }
      catch (RemoteException paramString)
      {
        Log.wtf("TransportController", "Error calling prepare(" + str + ").", paramString);
      }
    }
    
    public void prepareFromUri(Uri paramUri, Bundle paramBundle)
    {
      if ((paramUri == null) || (Uri.EMPTY.equals(paramUri))) {
        throw new IllegalArgumentException("You must specify a non-empty Uri for prepareFromUri.");
      }
      try
      {
        MediaController.-get0(MediaController.this).prepareFromUri(paramUri, paramBundle);
        return;
      }
      catch (RemoteException paramBundle)
      {
        Log.wtf("TransportController", "Error calling prepare(" + paramUri + ").", paramBundle);
      }
    }
    
    public void rewind()
    {
      try
      {
        MediaController.-get0(MediaController.this).rewind();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling rewind.", localRemoteException);
      }
    }
    
    public void seekTo(long paramLong)
    {
      Log.d("TransportController", "seekTo in TransportControls");
      try
      {
        MediaController.-get0(MediaController.this).seekTo(paramLong);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling seekTo.", localRemoteException);
      }
    }
    
    public void sendCustomAction(PlaybackState.CustomAction paramCustomAction, Bundle paramBundle)
    {
      if (paramCustomAction == null) {
        throw new IllegalArgumentException("CustomAction cannot be null.");
      }
      sendCustomAction(paramCustomAction.getAction(), paramBundle);
    }
    
    public void sendCustomAction(String paramString, Bundle paramBundle)
    {
      if (TextUtils.isEmpty(paramString)) {
        throw new IllegalArgumentException("CustomAction cannot be null.");
      }
      try
      {
        MediaController.-get0(MediaController.this).sendCustomAction(paramString, paramBundle);
        return;
      }
      catch (RemoteException paramString)
      {
        Log.d("TransportController", "Dead object in sendCustomAction.", paramString);
      }
    }
    
    public void setRating(Rating paramRating)
    {
      try
      {
        MediaController.-get0(MediaController.this).rate(paramRating);
        return;
      }
      catch (RemoteException paramRating)
      {
        Log.wtf("TransportController", "Error calling rate.", paramRating);
      }
    }
    
    public void setRemoteControlClientBrowsedPlayer()
    {
      Log.d("TransportController", "setRemoteControlClientBrowsedPlayer in TransportControls");
      try
      {
        MediaController.-get0(MediaController.this).setRemoteControlClientBrowsedPlayer();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling setRemoteControlClientBrowsedPlayer.", localRemoteException);
      }
    }
    
    public void setRemoteControlClientPlayItem(long paramLong, int paramInt)
    {
      Log.d("TransportController", "setRemoteControlClientPlayItem in TransportControls");
      try
      {
        MediaController.-get0(MediaController.this).setRemoteControlClientPlayItem(paramLong, paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling setRemoteControlClientPlayItem.", localRemoteException);
      }
    }
    
    public void skipToNext()
    {
      try
      {
        MediaController.-get0(MediaController.this).next();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling next.", localRemoteException);
      }
    }
    
    public void skipToPrevious()
    {
      try
      {
        MediaController.-get0(MediaController.this).previous();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling previous.", localRemoteException);
      }
    }
    
    public void skipToQueueItem(long paramLong)
    {
      try
      {
        MediaController.-get0(MediaController.this).skipToQueueItem(paramLong);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling skipToItem(" + paramLong + ").", localRemoteException);
      }
    }
    
    public void stop()
    {
      try
      {
        MediaController.-get0(MediaController.this).stop();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.wtf("TransportController", "Error calling stop.", localRemoteException);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/session/MediaController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */