package android.media;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.session.MediaController;
import android.media.session.MediaController.Callback;
import android.media.session.MediaController.TransportControls;
import android.media.session.MediaSession.Token;
import android.media.session.MediaSessionLegacyHelper;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import java.util.List;

@Deprecated
public final class RemoteController
{
  private static final boolean DEBUG = false;
  private static final int MAX_BITMAP_DIMENSION = 512;
  private static final int MSG_CLIENT_CHANGE = 0;
  private static final int MSG_NEW_MEDIA_METADATA = 2;
  private static final int MSG_NEW_PLAYBACK_STATE = 1;
  public static final int POSITION_SYNCHRONIZATION_CHECK = 1;
  public static final int POSITION_SYNCHRONIZATION_NONE = 0;
  private static final int SENDMSG_NOOP = 1;
  private static final int SENDMSG_QUEUE = 2;
  private static final int SENDMSG_REPLACE = 0;
  private static final String TAG = "RemoteController";
  private static final Object mInfoLock = new Object();
  private int mArtworkHeight = -1;
  private int mArtworkWidth = -1;
  private final AudioManager mAudioManager;
  private final Context mContext;
  private MediaController mCurrentSession;
  private boolean mEnabled = true;
  private final EventHandler mEventHandler;
  private boolean mIsRegistered = false;
  private PlaybackInfo mLastPlaybackInfo;
  private final int mMaxBitmapDimension;
  private MetadataEditor mMetadataEditor;
  private OnClientAvrcpUpdateListener mOnClientAvrcpUpdateListener;
  private OnClientUpdateListener mOnClientUpdateListener;
  private MediaController.Callback mSessionCb = new MediaControllerCallback(null);
  private MediaSessionManager.OnActiveSessionsChangedListener mSessionListener;
  private MediaSessionManager mSessionManager;
  
  public RemoteController(Context paramContext, OnClientUpdateListener paramOnClientUpdateListener)
    throws IllegalArgumentException
  {
    this(paramContext, paramOnClientUpdateListener, null);
  }
  
  public RemoteController(Context paramContext, OnClientUpdateListener paramOnClientUpdateListener, Looper paramLooper)
    throws IllegalArgumentException
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("Invalid null Context");
    }
    if (paramOnClientUpdateListener == null) {
      throw new IllegalArgumentException("Invalid null OnClientUpdateListener");
    }
    if (paramLooper != null) {}
    for (this.mEventHandler = new EventHandler(this, paramLooper);; this.mEventHandler = new EventHandler(this, paramLooper))
    {
      this.mOnClientUpdateListener = paramOnClientUpdateListener;
      this.mContext = paramContext;
      this.mSessionManager = ((MediaSessionManager)paramContext.getSystemService("media_session"));
      this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
      this.mSessionListener = new TopTransportSessionListener(null);
      if (!ActivityManager.isLowRamDeviceStatic()) {
        break label181;
      }
      this.mMaxBitmapDimension = 512;
      return;
      paramLooper = Looper.myLooper();
      if (paramLooper == null) {
        break;
      }
    }
    throw new IllegalArgumentException("Calling thread not associated with a looper");
    label181:
    paramContext = paramContext.getResources().getDisplayMetrics();
    this.mMaxBitmapDimension = Math.max(paramContext.widthPixels, paramContext.heightPixels);
  }
  
  public RemoteController(Context paramContext, OnClientUpdateListener paramOnClientUpdateListener, Looper paramLooper, OnClientAvrcpUpdateListener paramOnClientAvrcpUpdateListener)
    throws IllegalArgumentException
  {
    this(paramContext, paramOnClientUpdateListener, paramLooper);
    this.mOnClientAvrcpUpdateListener = paramOnClientAvrcpUpdateListener;
  }
  
  private void onClientChange(boolean paramBoolean)
  {
    synchronized (mInfoLock)
    {
      OnClientUpdateListener localOnClientUpdateListener = this.mOnClientUpdateListener;
      this.mMetadataEditor = null;
      if (localOnClientUpdateListener != null) {
        localOnClientUpdateListener.onClientChange(paramBoolean);
      }
      return;
    }
  }
  
  private void onFolderInfoBrowsedPlayer(String paramString)
  {
    Log.d("RemoteController", "RemoteController: onFolderInfoBrowsedPlayer");
    OnClientAvrcpUpdateListener localOnClientAvrcpUpdateListener;
    synchronized (mInfoLock)
    {
      localOnClientAvrcpUpdateListener = this.mOnClientAvrcpUpdateListener;
      if (localOnClientAvrcpUpdateListener == null) {}
    }
  }
  
  private void onNewMediaMetadata(MediaMetadata paramMediaMetadata)
  {
    int j = 0;
    if (paramMediaMetadata == null) {
      return;
    }
    for (;;)
    {
      int i;
      synchronized (mInfoLock)
      {
        OnClientUpdateListener localOnClientUpdateListener = this.mOnClientUpdateListener;
        if (this.mCurrentSession != null)
        {
          if (this.mCurrentSession.getRatingType() != 0)
          {
            i = 1;
            break label111;
            long l = j;
            this.mMetadataEditor = new MetadataEditor(MediaSessionLegacyHelper.getOldMetadata(paramMediaMetadata, this.mArtworkWidth, this.mArtworkHeight), l);
            paramMediaMetadata = this.mMetadataEditor;
            if (localOnClientUpdateListener != null) {
              localOnClientUpdateListener.onClientMetadataUpdate(paramMediaMetadata);
            }
          }
          else
          {
            i = 0;
          }
        }
        else {
          i = 0;
        }
      }
      label111:
      if (i != 0) {
        j = 268435457;
      }
    }
  }
  
  private void onNewPlaybackState(PlaybackState paramPlaybackState)
  {
    for (;;)
    {
      OnClientUpdateListener localOnClientUpdateListener;
      synchronized (mInfoLock)
      {
        localOnClientUpdateListener = this.mOnClientUpdateListener;
        if (localOnClientUpdateListener != null)
        {
          if (paramPlaybackState != null) {
            break label72;
          }
          i = 0;
          if ((paramPlaybackState != null) && (paramPlaybackState.getPosition() != -1L)) {
            break label83;
          }
          localOnClientUpdateListener.onClientPlaybackStateUpdate(i);
          if (paramPlaybackState != null) {
            localOnClientUpdateListener.onClientTransportControlUpdate(PlaybackState.getRccControlFlagsFromActions(paramPlaybackState.getActions()));
          }
        }
        return;
      }
      label72:
      int i = PlaybackState.getRccStateFromState(paramPlaybackState.getState());
      continue;
      label83:
      localOnClientUpdateListener.onClientPlaybackStateUpdate(i, paramPlaybackState.getLastPositionUpdateTime(), paramPlaybackState.getPosition(), paramPlaybackState.getPlaybackSpeed());
    }
  }
  
  private void onNowPlayingContentChange()
  {
    Log.d("RemoteController", "RemoteController: onNowPlayingContentChange");
    OnClientAvrcpUpdateListener localOnClientAvrcpUpdateListener;
    synchronized (mInfoLock)
    {
      localOnClientAvrcpUpdateListener = this.mOnClientAvrcpUpdateListener;
      if (localOnClientAvrcpUpdateListener == null) {}
    }
  }
  
  private void onNowPlayingEntriesUpdate(long[] paramArrayOfLong)
  {
    Log.d("RemoteController", "RemoteController: onUpdateNowPlayingEntries");
    OnClientAvrcpUpdateListener localOnClientAvrcpUpdateListener;
    synchronized (mInfoLock)
    {
      localOnClientAvrcpUpdateListener = this.mOnClientAvrcpUpdateListener;
      if (localOnClientAvrcpUpdateListener == null) {}
    }
  }
  
  private void onSetPlayItemResponse(boolean paramBoolean)
  {
    Log.d("RemoteController", "RemoteController: onPlayItemResponse");
    OnClientAvrcpUpdateListener localOnClientAvrcpUpdateListener;
    synchronized (mInfoLock)
    {
      localOnClientAvrcpUpdateListener = this.mOnClientAvrcpUpdateListener;
      if (localOnClientAvrcpUpdateListener == null) {}
    }
  }
  
  private static void sendMsg(Handler paramHandler, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject, int paramInt5)
  {
    if (paramHandler == null)
    {
      Log.e("RemoteController", "null event handler, will not deliver message " + paramInt1);
      return;
    }
    if (paramInt2 == 0) {
      paramHandler.removeMessages(paramInt1);
    }
    while ((paramInt2 != 1) || (!paramHandler.hasMessages(paramInt1)))
    {
      paramHandler.sendMessageDelayed(paramHandler.obtainMessage(paramInt1, paramInt3, paramInt4, paramObject), paramInt5);
      return;
    }
  }
  
  private void updateController(MediaController paramMediaController)
  {
    Object localObject = mInfoLock;
    if (paramMediaController == null) {}
    for (;;)
    {
      try
      {
        if (this.mCurrentSession != null)
        {
          Log.v("RemoteController", "Updating current controller as null");
          this.mAudioManager.updateMediaPlayerList(this.mCurrentSession.getPackageName(), false);
          this.mCurrentSession.unregisterCallback(this.mSessionCb);
          this.mCurrentSession = null;
          sendMsg(this.mEventHandler, 0, 0, 0, 1, null, 0);
        }
        return;
      }
      finally {}
      if ((this.mCurrentSession == null) || (!paramMediaController.getSessionToken().equals(this.mCurrentSession.getSessionToken()))) {
        if (this.mCurrentSession != null)
        {
          Log.v("RemoteController", "Updating current controller package as " + paramMediaController.getPackageName() + " from " + this.mCurrentSession.getPackageName());
          this.mCurrentSession.unregisterCallback(this.mSessionCb);
        }
      }
    }
    for (;;)
    {
      sendMsg(this.mEventHandler, 0, 0, 0, 0, null, 0);
      this.mCurrentSession = paramMediaController;
      this.mCurrentSession.registerCallback(this.mSessionCb, this.mEventHandler);
      this.mAudioManager.updateMediaPlayerList(this.mCurrentSession.getPackageName(), true);
      PlaybackState localPlaybackState = paramMediaController.getPlaybackState();
      sendMsg(this.mEventHandler, 1, 0, 0, 0, localPlaybackState, 0);
      paramMediaController = paramMediaController.getMetadata();
      sendMsg(this.mEventHandler, 2, 0, 0, 0, paramMediaController, 0);
      break;
      Log.v("RemoteController", "Updating current controller package as " + paramMediaController.getPackageName() + " from null");
    }
  }
  
  public boolean clearArtworkConfiguration()
  {
    return setArtworkConfiguration(false, -1, -1);
  }
  
  public MetadataEditor editMetadata()
  {
    MetadataEditor localMetadataEditor = new MetadataEditor();
    localMetadataEditor.mEditorMetadata = new Bundle();
    localMetadataEditor.mEditorArtwork = null;
    localMetadataEditor.mMetadataChanged = true;
    localMetadataEditor.mArtworkChanged = true;
    localMetadataEditor.mEditableKeys = 0L;
    return localMetadataEditor;
  }
  
  public long getEstimatedMediaPosition()
  {
    synchronized (mInfoLock)
    {
      if (this.mCurrentSession != null)
      {
        PlaybackState localPlaybackState = this.mCurrentSession.getPlaybackState();
        if (localPlaybackState != null)
        {
          long l = localPlaybackState.getPosition();
          return l;
        }
      }
      return -1L;
    }
  }
  
  public void getRemoteControlClientNowPlayingEntries()
  {
    Log.e("RemoteController", "getRemoteControlClientNowPlayingEntries()");
    if (!this.mEnabled)
    {
      Log.e("RemoteController", "Cannot use getRemoteControlClientNowPlayingEntries() from a disabled RemoteController");
      return;
    }
    synchronized (mInfoLock)
    {
      if (this.mCurrentSession != null) {
        this.mCurrentSession.getTransportControls().getRemoteControlClientNowPlayingEntries();
      }
      return;
    }
  }
  
  OnClientUpdateListener getUpdateListener()
  {
    return this.mOnClientUpdateListener;
  }
  
  public boolean seekTo(long paramLong)
    throws IllegalArgumentException
  {
    Log.e("RemoteController", "seekTo() in RemoteController");
    if (!this.mEnabled)
    {
      Log.e("RemoteController", "Cannot use seekTo() from a disabled RemoteController");
      return false;
    }
    if (paramLong < 0L) {
      throw new IllegalArgumentException("illegal negative time value");
    }
    synchronized (mInfoLock)
    {
      if (this.mCurrentSession != null) {
        this.mCurrentSession.getTransportControls().seekTo(paramLong);
      }
      return true;
    }
  }
  
  public boolean sendMediaKeyEvent(KeyEvent paramKeyEvent)
    throws IllegalArgumentException
  {
    if (!KeyEvent.isMediaKey(paramKeyEvent.getKeyCode())) {
      throw new IllegalArgumentException("not a media key event");
    }
    synchronized (mInfoLock)
    {
      if (this.mCurrentSession != null)
      {
        boolean bool = this.mCurrentSession.dispatchMediaButtonEvent(paramKeyEvent);
        return bool;
      }
      return false;
    }
  }
  
  public boolean setArtworkConfiguration(int paramInt1, int paramInt2)
    throws IllegalArgumentException
  {
    return setArtworkConfiguration(true, paramInt1, paramInt2);
  }
  
  public boolean setArtworkConfiguration(boolean paramBoolean, int paramInt1, int paramInt2)
    throws IllegalArgumentException
  {
    Object localObject1 = mInfoLock;
    int i;
    if (paramBoolean) {
      if ((paramInt1 > 0) && (paramInt2 > 0)) {
        i = paramInt1;
      }
    }
    for (;;)
    {
      try
      {
        if (paramInt1 > this.mMaxBitmapDimension) {
          i = this.mMaxBitmapDimension;
        }
        paramInt1 = paramInt2;
        if (paramInt2 > this.mMaxBitmapDimension) {
          paramInt1 = this.mMaxBitmapDimension;
        }
        this.mArtworkWidth = i;
        this.mArtworkHeight = paramInt1;
        return true;
      }
      finally {}
      throw new IllegalArgumentException("Invalid dimensions");
      this.mArtworkWidth = -1;
      this.mArtworkHeight = -1;
    }
  }
  
  public void setRemoteControlClientBrowsedPlayer()
  {
    Log.e("RemoteController", "setRemoteControlClientBrowsedPlayer()");
    if (!this.mEnabled)
    {
      Log.e("RemoteController", "Cannot use setRemoteControlClientBrowsedPlayer() from a disabled RemoteController");
      return;
    }
    synchronized (mInfoLock)
    {
      if (this.mCurrentSession != null) {
        this.mCurrentSession.getTransportControls().setRemoteControlClientBrowsedPlayer();
      }
      return;
    }
  }
  
  public void setRemoteControlClientPlayItem(long paramLong, int paramInt)
  {
    Log.e("RemoteController", "setRemoteControlClientPlayItem()");
    if (!this.mEnabled)
    {
      Log.e("RemoteController", "Cannot use setRemoteControlClientPlayItem() from a disabled RemoteController");
      return;
    }
    synchronized (mInfoLock)
    {
      if (this.mCurrentSession != null) {
        this.mCurrentSession.getTransportControls().setRemoteControlClientPlayItem(paramLong, paramInt);
      }
      return;
    }
  }
  
  public boolean setSynchronizationMode(int paramInt)
    throws IllegalArgumentException
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("Unknown synchronization mode " + paramInt);
    }
    if (!this.mIsRegistered)
    {
      Log.e("RemoteController", "Cannot set synchronization mode on an unregistered RemoteController");
      return false;
    }
    return true;
  }
  
  void startListeningToSessions()
  {
    ComponentName localComponentName = new ComponentName(this.mContext, this.mOnClientUpdateListener.getClass());
    Handler localHandler = null;
    if (Looper.myLooper() == null) {
      localHandler = new Handler(Looper.getMainLooper());
    }
    this.mSessionManager.addOnActiveSessionsChangedListener(this.mSessionListener, localComponentName, UserHandle.myUserId(), localHandler);
    this.mSessionListener.onActiveSessionsChanged(this.mSessionManager.getActiveSessions(localComponentName));
  }
  
  void stopListeningToSessions()
  {
    this.mSessionManager.removeOnActiveSessionsChangedListener(this.mSessionListener);
  }
  
  private class EventHandler
    extends Handler
  {
    public EventHandler(RemoteController paramRemoteController, Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = true;
      switch (paramMessage.what)
      {
      default: 
        Log.e("RemoteController", "unknown event " + paramMessage.what);
        return;
      case 0: 
        RemoteController localRemoteController = RemoteController.this;
        if (paramMessage.arg2 == 1) {}
        for (;;)
        {
          RemoteController.-wrap0(localRemoteController, bool);
          return;
          bool = false;
        }
      case 1: 
        RemoteController.-wrap3(RemoteController.this, (PlaybackState)paramMessage.obj);
        return;
      }
      RemoteController.-wrap2(RemoteController.this, (MediaMetadata)paramMessage.obj);
    }
  }
  
  private class MediaControllerCallback
    extends MediaController.Callback
  {
    private MediaControllerCallback() {}
    
    public void onMetadataChanged(MediaMetadata paramMediaMetadata)
    {
      RemoteController.-wrap2(RemoteController.this, paramMediaMetadata);
    }
    
    public void onPlayItemResponse(boolean paramBoolean)
    {
      Log.d("RemoteController", "MediaControllerCallback: onPlayItemResponse");
      RemoteController.-wrap6(RemoteController.this, paramBoolean);
    }
    
    public void onPlaybackStateChanged(PlaybackState paramPlaybackState)
    {
      RemoteController.-wrap3(RemoteController.this, paramPlaybackState);
    }
    
    public void onUpdateFolderInfoBrowsedPlayer(String paramString)
    {
      Log.d("RemoteController", "MediaControllerCallback: onUpdateFolderInfoBrowsedPlayer");
      RemoteController.-wrap1(RemoteController.this, paramString);
    }
    
    public void onUpdateNowPlayingContentChange()
    {
      Log.d("RemoteController", "MediaControllerCallback: onUpdateNowPlayingContentChange");
      RemoteController.-wrap4(RemoteController.this);
    }
    
    public void onUpdateNowPlayingEntries(long[] paramArrayOfLong)
    {
      Log.d("RemoteController", "MediaControllerCallback: onUpdateNowPlayingEntries");
      RemoteController.-wrap5(RemoteController.this, paramArrayOfLong);
    }
  }
  
  public class MetadataEditor
    extends MediaMetadataEditor
  {
    protected MetadataEditor() {}
    
    protected MetadataEditor(Bundle paramBundle, long paramLong)
    {
      this.mEditorMetadata = paramBundle;
      this.mEditableKeys = paramLong;
      this.mEditorArtwork = ((Bitmap)paramBundle.getParcelable(String.valueOf(100)));
      if (this.mEditorArtwork != null) {
        cleanupBitmapFromBundle(100);
      }
      this.mMetadataChanged = true;
      this.mArtworkChanged = true;
      this.mApplied = false;
    }
    
    private void cleanupBitmapFromBundle(int paramInt)
    {
      if (METADATA_KEYS_TYPE.get(paramInt, -1) == 2) {
        this.mEditorMetadata.remove(String.valueOf(paramInt));
      }
    }
    
    /* Error */
    public void apply()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 52	android/media/MediaMetadataEditor:mMetadataChanged	Z
      //   6: istore_1
      //   7: iload_1
      //   8: ifne +6 -> 14
      //   11: aload_0
      //   12: monitorexit
      //   13: return
      //   14: invokestatic 77	android/media/RemoteController:-get1	()Ljava/lang/Object;
      //   17: astore_2
      //   18: aload_2
      //   19: monitorenter
      //   20: aload_0
      //   21: getfield 13	android/media/RemoteController$MetadataEditor:this$0	Landroid/media/RemoteController;
      //   24: invokestatic 81	android/media/RemoteController:-get0	(Landroid/media/RemoteController;)Landroid/media/session/MediaController;
      //   27: ifnull +47 -> 74
      //   30: aload_0
      //   31: getfield 22	android/media/MediaMetadataEditor:mEditorMetadata	Landroid/os/Bundle;
      //   34: ldc 82
      //   36: invokestatic 32	java/lang/String:valueOf	(I)Ljava/lang/String;
      //   39: invokevirtual 88	android/os/BaseBundle:containsKey	(Ljava/lang/String;)Z
      //   42: ifeq +32 -> 74
      //   45: aload_0
      //   46: ldc 82
      //   48: aconst_null
      //   49: invokevirtual 92	android/media/MediaMetadataEditor:getObject	(ILjava/lang/Object;)Ljava/lang/Object;
      //   52: checkcast 94	android/media/Rating
      //   55: astore_3
      //   56: aload_3
      //   57: ifnull +17 -> 74
      //   60: aload_0
      //   61: getfield 13	android/media/RemoteController$MetadataEditor:this$0	Landroid/media/RemoteController;
      //   64: invokestatic 81	android/media/RemoteController:-get0	(Landroid/media/RemoteController;)Landroid/media/session/MediaController;
      //   67: invokevirtual 100	android/media/session/MediaController:getTransportControls	()Landroid/media/session/MediaController$TransportControls;
      //   70: aload_3
      //   71: invokevirtual 106	android/media/session/MediaController$TransportControls:setRating	(Landroid/media/Rating;)V
      //   74: aload_2
      //   75: monitorexit
      //   76: aload_0
      //   77: iconst_0
      //   78: putfield 58	android/media/MediaMetadataEditor:mApplied	Z
      //   81: aload_0
      //   82: monitorexit
      //   83: return
      //   84: astore_3
      //   85: aload_2
      //   86: monitorexit
      //   87: aload_3
      //   88: athrow
      //   89: astore_2
      //   90: aload_0
      //   91: monitorexit
      //   92: aload_2
      //   93: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	94	0	this	MetadataEditor
      //   6	2	1	bool	boolean
      //   89	4	2	localObject2	Object
      //   55	16	3	localRating	Rating
      //   84	4	3	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   20	56	84	finally
      //   60	74	84	finally
      //   2	7	89	finally
      //   14	20	89	finally
      //   74	81	89	finally
      //   85	89	89	finally
    }
  }
  
  public static abstract interface OnClientAvrcpUpdateListener
  {
    public abstract void onClientFolderInfoBrowsedPlayer(String paramString);
    
    public abstract void onClientNowPlayingContentChange();
    
    public abstract void onClientPlayItemResponse(boolean paramBoolean);
    
    public abstract void onClientUpdateNowPlayingEntries(long[] paramArrayOfLong);
  }
  
  public static abstract interface OnClientUpdateListener
  {
    public abstract void onClientChange(boolean paramBoolean);
    
    public abstract void onClientMetadataUpdate(RemoteController.MetadataEditor paramMetadataEditor);
    
    public abstract void onClientPlaybackStateUpdate(int paramInt);
    
    public abstract void onClientPlaybackStateUpdate(int paramInt, long paramLong1, long paramLong2, float paramFloat);
    
    public abstract void onClientTransportControlUpdate(int paramInt);
  }
  
  private static class PlaybackInfo
  {
    long mCurrentPosMs;
    float mSpeed;
    int mState;
    long mStateChangeTimeMs;
    
    PlaybackInfo(int paramInt, long paramLong1, long paramLong2, float paramFloat)
    {
      this.mState = paramInt;
      this.mStateChangeTimeMs = paramLong1;
      this.mCurrentPosMs = paramLong2;
      this.mSpeed = paramFloat;
    }
  }
  
  private class TopTransportSessionListener
    implements MediaSessionManager.OnActiveSessionsChangedListener
  {
    private TopTransportSessionListener() {}
    
    public void onActiveSessionsChanged(List<MediaController> paramList)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        MediaController localMediaController = (MediaController)paramList.get(i);
        if ((0x2 & localMediaController.getFlags()) != 0L)
        {
          RemoteController.-wrap7(RemoteController.this, localMediaController);
          return;
        }
        i += 1;
      }
      RemoteController.-wrap7(RemoteController.this, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/RemoteController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */