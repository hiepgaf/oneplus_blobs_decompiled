package android.media;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.media.session.MediaSession;
import android.media.session.MediaSession.Callback;
import android.media.session.MediaSessionLegacyHelper;
import android.media.session.PlaybackState;
import android.media.session.PlaybackState.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

@Deprecated
public class RemoteControlClient
{
  private static final boolean DEBUG = false;
  public static final int DEFAULT_PLAYBACK_VOLUME = 15;
  public static final int DEFAULT_PLAYBACK_VOLUME_HANDLING = 1;
  public static final int FLAGS_KEY_MEDIA_NONE = 0;
  public static final int FLAG_INFORMATION_REQUEST_ALBUM_ART = 8;
  public static final int FLAG_INFORMATION_REQUEST_KEY_MEDIA = 2;
  public static final int FLAG_INFORMATION_REQUEST_METADATA = 1;
  public static final int FLAG_INFORMATION_REQUEST_PLAYSTATE = 4;
  public static final int FLAG_KEY_MEDIA_FAST_FORWARD = 64;
  public static final int FLAG_KEY_MEDIA_NEXT = 128;
  public static final int FLAG_KEY_MEDIA_PAUSE = 16;
  public static final int FLAG_KEY_MEDIA_PLAY = 4;
  public static final int FLAG_KEY_MEDIA_PLAY_PAUSE = 8;
  public static final int FLAG_KEY_MEDIA_POSITION_UPDATE = 256;
  public static final int FLAG_KEY_MEDIA_PREVIOUS = 1;
  public static final int FLAG_KEY_MEDIA_RATING = 512;
  public static final int FLAG_KEY_MEDIA_REWIND = 2;
  public static final int FLAG_KEY_MEDIA_STOP = 32;
  public static int MEDIA_POSITION_READABLE = 1;
  public static int MEDIA_POSITION_WRITABLE = 2;
  private static final int MSG_GET_NOW_PLAYING_ENTRIES = 14;
  private static final int MSG_SET_BROWSED_PLAYER = 12;
  private static final int MSG_SET_PLAY_ITEM = 13;
  public static final int PLAYBACKINFO_INVALID_VALUE = Integer.MIN_VALUE;
  public static final int PLAYBACKINFO_PLAYBACK_TYPE = 1;
  public static final int PLAYBACKINFO_USES_STREAM = 5;
  public static final int PLAYBACKINFO_VOLUME = 2;
  public static final int PLAYBACKINFO_VOLUME_HANDLING = 4;
  public static final int PLAYBACKINFO_VOLUME_MAX = 3;
  public static final long PLAYBACK_POSITION_ALWAYS_UNKNOWN = -9216204211029966080L;
  public static final long PLAYBACK_POSITION_INVALID = -1L;
  public static final float PLAYBACK_SPEED_1X = 1.0F;
  public static final int PLAYBACK_TYPE_LOCAL = 0;
  private static final int PLAYBACK_TYPE_MAX = 1;
  private static final int PLAYBACK_TYPE_MIN = 0;
  public static final int PLAYBACK_TYPE_REMOTE = 1;
  public static final int PLAYBACK_VOLUME_FIXED = 0;
  public static final int PLAYBACK_VOLUME_VARIABLE = 1;
  public static final int PLAYSTATE_BUFFERING = 8;
  public static final int PLAYSTATE_ERROR = 9;
  public static final int PLAYSTATE_FAST_FORWARDING = 4;
  public static final int PLAYSTATE_NONE = 0;
  public static final int PLAYSTATE_PAUSED = 2;
  public static final int PLAYSTATE_PLAYING = 3;
  public static final int PLAYSTATE_REWINDING = 5;
  public static final int PLAYSTATE_SKIPPING_BACKWARDS = 7;
  public static final int PLAYSTATE_SKIPPING_FORWARDS = 6;
  public static final int PLAYSTATE_STOPPED = 1;
  private static final long POSITION_DRIFT_MAX_MS = 500L;
  private static final long POSITION_REFRESH_PERIOD_MIN_MS = 2000L;
  private static final long POSITION_REFRESH_PERIOD_PLAYING_MS = 15000L;
  public static final int RCSE_ID_UNREGISTERED = -1;
  private static final String TAG = "RemoteControlClient";
  private final Object mCacheLock = new Object();
  private int mCurrentClientGenId = -1;
  private EventHandler mEventHandler;
  private OnGetNowPlayingEntriesListener mGetNowPlayingEntriesListener;
  private MediaMetadata mMediaMetadata;
  private Bundle mMetadata = new Bundle();
  private OnMetadataUpdateListener mMetadataUpdateListener;
  private boolean mNeedsPositionSync = false;
  private Bitmap mOriginalArtwork;
  private long mPlaybackPositionMs = -1L;
  private float mPlaybackSpeed = 1.0F;
  private int mPlaybackState = 0;
  private long mPlaybackStateChangeTimeMs = 0L;
  private OnGetPlaybackPositionListener mPositionProvider;
  private OnPlaybackPositionUpdateListener mPositionUpdateListener;
  private final PendingIntent mRcMediaIntent;
  private MediaSession mSession;
  private PlaybackState mSessionPlaybackState = null;
  private OnSetBrowsedPlayerListener mSetBrowsedPlayerListener;
  private OnSetPlayItemListener mSetPlayItemListener;
  private int mTransportControlFlags = 0;
  private MediaSession.Callback mTransportListener = new MediaSession.Callback()
  {
    public void getNowPlayingEntries()
    {
      if (RemoteControlClient.-get2(RemoteControlClient.this) != null)
      {
        RemoteControlClient.-get2(RemoteControlClient.this).removeMessages(14);
        RemoteControlClient.-get2(RemoteControlClient.this).sendMessage(RemoteControlClient.-get2(RemoteControlClient.this).obtainMessage(14, 0, 0, null));
      }
    }
    
    public void onSeekTo(long paramAnonymousLong)
    {
      RemoteControlClient.-wrap1(RemoteControlClient.this, RemoteControlClient.-get1(RemoteControlClient.this), paramAnonymousLong);
    }
    
    public void onSetRating(Rating paramAnonymousRating)
    {
      if ((RemoteControlClient.-get7(RemoteControlClient.this) & 0x200) != 0) {
        RemoteControlClient.-wrap4(RemoteControlClient.this, RemoteControlClient.-get1(RemoteControlClient.this), 268435457, paramAnonymousRating);
      }
    }
    
    public void setBrowsedPlayer()
    {
      Log.d("RemoteControlClient", "setBrowsedPlayer in RemoteControlClient");
      if (RemoteControlClient.-get2(RemoteControlClient.this) != null) {
        RemoteControlClient.-get2(RemoteControlClient.this).sendMessage(RemoteControlClient.-get2(RemoteControlClient.this).obtainMessage(12, 0, 0, null));
      }
    }
    
    public void setPlayItem(int paramAnonymousInt, long paramAnonymousLong)
    {
      if (RemoteControlClient.-get2(RemoteControlClient.this) != null)
      {
        RemoteControlClient.-get2(RemoteControlClient.this).removeMessages(13);
        RemoteControlClient.-get2(RemoteControlClient.this).sendMessage(RemoteControlClient.-get2(RemoteControlClient.this).obtainMessage(13, 0, paramAnonymousInt, new Long(paramAnonymousLong)));
      }
    }
  };
  
  public RemoteControlClient(PendingIntent paramPendingIntent)
  {
    this.mRcMediaIntent = paramPendingIntent;
    paramPendingIntent = Looper.myLooper();
    if (paramPendingIntent != null)
    {
      this.mEventHandler = new EventHandler(this, paramPendingIntent);
      return;
    }
    paramPendingIntent = Looper.getMainLooper();
    if (paramPendingIntent != null)
    {
      this.mEventHandler = new EventHandler(this, paramPendingIntent);
      return;
    }
    this.mEventHandler = null;
    Log.e("RemoteControlClient", "RemoteControlClient() couldn't find main application thread");
  }
  
  public RemoteControlClient(PendingIntent paramPendingIntent, Looper paramLooper)
  {
    this.mRcMediaIntent = paramPendingIntent;
    this.mEventHandler = new EventHandler(this, paramLooper);
  }
  
  private static long getCheckPeriodFromSpeed(float paramFloat)
  {
    if (Math.abs(paramFloat) <= 1.0F) {
      return 15000L;
    }
    return Math.max((15000.0F / Math.abs(paramFloat)), 2000L);
  }
  
  private void onGetNowPlayingEntries()
  {
    Log.d("RemoteControlClient", "onGetNowPlayingEntries");
    synchronized (this.mCacheLock)
    {
      if (this.mGetNowPlayingEntriesListener != null)
      {
        Log.d("RemoteControlClient", "mGetNowPlayingEntriesListener.onGetNowPlayingEntries");
        this.mGetNowPlayingEntriesListener.onGetNowPlayingEntries();
      }
      return;
    }
  }
  
  private void onSeekTo(int paramInt, long paramLong)
  {
    synchronized (this.mCacheLock)
    {
      if ((this.mCurrentClientGenId == paramInt) && (this.mPositionUpdateListener != null)) {
        this.mPositionUpdateListener.onPlaybackPositionUpdate(paramLong);
      }
      return;
    }
  }
  
  private void onSetBrowsedPlayer()
  {
    Log.d("RemoteControlClient", "onSetBrowsedPlayer");
    synchronized (this.mCacheLock)
    {
      if (this.mSetBrowsedPlayerListener != null)
      {
        Log.d("RemoteControlClient", "mSetBrowsedPlayerListener.onSetBrowsedPlayer");
        this.mSetBrowsedPlayerListener.onSetBrowsedPlayer();
      }
      return;
    }
  }
  
  private void onSetPlayItem(int paramInt, long paramLong)
  {
    Log.d("RemoteControlClient", "onSetPlayItem");
    synchronized (this.mCacheLock)
    {
      if (this.mSetPlayItemListener != null)
      {
        Log.d("RemoteControlClient", "mSetPlayItemListener.onSetPlayItem");
        this.mSetPlayItemListener.onSetPlayItem(paramInt, paramLong);
      }
      return;
    }
  }
  
  private void onUpdateMetadata(int paramInt1, int paramInt2, Object paramObject)
  {
    synchronized (this.mCacheLock)
    {
      if ((this.mCurrentClientGenId == paramInt1) && (this.mMetadataUpdateListener != null)) {
        this.mMetadataUpdateListener.onMetadataUpdate(paramInt2, paramObject);
      }
      return;
    }
  }
  
  private void playItemResponseInt(boolean paramBoolean)
  {
    Log.d("RemoteControlClient", "playItemResponseInt");
    Log.v("RemoteControlClient", "success: " + paramBoolean);
    if (this.mSession != null) {
      this.mSession.playItemResponse(paramBoolean);
    }
  }
  
  static boolean playbackPositionShouldMove(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
    case 4: 
    case 5: 
    default: 
      return true;
    }
    return false;
  }
  
  private void setPlaybackStateInt(int paramInt, long paramLong, float paramFloat, boolean paramBoolean)
  {
    for (;;)
    {
      synchronized (this.mCacheLock)
      {
        if ((this.mPlaybackState != paramInt) || (this.mPlaybackPositionMs != paramLong))
        {
          this.mPlaybackState = paramInt;
          if (!paramBoolean) {
            break label170;
          }
          if (paramLong < 0L)
          {
            this.mPlaybackPositionMs = -1L;
            this.mPlaybackSpeed = paramFloat;
            this.mPlaybackStateChangeTimeMs = SystemClock.elapsedRealtime();
            if (this.mSession != null)
            {
              paramInt = PlaybackState.getStateFromRccState(paramInt);
              if (!paramBoolean) {
                break label180;
              }
              paramLong = this.mPlaybackPositionMs;
              PlaybackState.Builder localBuilder = new PlaybackState.Builder(this.mSessionPlaybackState);
              localBuilder.setState(paramInt, paramLong, paramFloat, SystemClock.elapsedRealtime());
              localBuilder.setErrorMessage(null);
              this.mSessionPlaybackState = localBuilder.build();
              this.mSession.setPlaybackState(this.mSessionPlaybackState);
            }
          }
        }
        else
        {
          if (this.mPlaybackSpeed == paramFloat) {
            continue;
          }
          continue;
        }
        this.mPlaybackPositionMs = paramLong;
      }
      label170:
      this.mPlaybackPositionMs = -9216204211029966080L;
      continue;
      label180:
      paramLong = -1L;
    }
  }
  
  private void updateFolderInfoBrowsedPlayerInt(String paramString)
  {
    Log.d("RemoteControlClient", "updateFolderInfoBrowsedPlayerInt");
    if (this.mSession != null) {
      this.mSession.updateFolderInfoBrowsedPlayer(paramString);
    }
  }
  
  private void updateNowPlayingContentChangeInt()
  {
    Log.d("RemoteControlClient", "updateNowPlayingContentChangeInt");
    if (this.mSession != null) {
      this.mSession.updateNowPlayingContentChange();
    }
  }
  
  private void updateNowPlayingEntriesInt(long[] paramArrayOfLong)
  {
    Log.d("RemoteControlClient", "updateNowPlayingEntriesInt");
    if (this.mSession != null) {
      this.mSession.updateNowPlayingEntries(paramArrayOfLong);
    }
  }
  
  public MetadataEditor editMetadata(boolean paramBoolean)
  {
    MetadataEditor localMetadataEditor = new MetadataEditor(null);
    if (paramBoolean)
    {
      localMetadataEditor.mEditorMetadata = new Bundle();
      localMetadataEditor.mEditorArtwork = null;
      localMetadataEditor.mMetadataChanged = true;
      localMetadataEditor.mArtworkChanged = true;
      localMetadataEditor.mEditableKeys = 0L;
    }
    while ((paramBoolean) || (this.mMediaMetadata == null))
    {
      localMetadataEditor.mMetadataBuilder = new MediaMetadata.Builder();
      return localMetadataEditor;
      localMetadataEditor.mEditorMetadata = new Bundle(this.mMetadata);
      localMetadataEditor.mEditorArtwork = this.mOriginalArtwork;
      localMetadataEditor.mMetadataChanged = false;
      localMetadataEditor.mArtworkChanged = false;
    }
    localMetadataEditor.mMetadataBuilder = new MediaMetadata.Builder(this.mMediaMetadata);
    return localMetadataEditor;
  }
  
  public MediaSession getMediaSession()
  {
    return this.mSession;
  }
  
  public PendingIntent getRcMediaIntent()
  {
    return this.mRcMediaIntent;
  }
  
  public void playItemResponse(boolean paramBoolean)
  {
    Log.e("RemoteControlClient", "playItemResponse");
    playItemResponseInt(paramBoolean);
  }
  
  public void registerWithSession(MediaSessionLegacyHelper paramMediaSessionLegacyHelper)
  {
    paramMediaSessionLegacyHelper.addRccListener(this.mRcMediaIntent, this.mTransportListener);
    this.mSession = paramMediaSessionLegacyHelper.getSession(this.mRcMediaIntent);
    setTransportControlFlags(this.mTransportControlFlags);
  }
  
  public void setBrowsedPlayerUpdateListener(OnSetBrowsedPlayerListener paramOnSetBrowsedPlayerListener)
  {
    Log.d("RemoteControlClient", "setBrowsedPlayerUpdateListener");
    synchronized (this.mCacheLock)
    {
      this.mSetBrowsedPlayerListener = paramOnSetBrowsedPlayerListener;
      return;
    }
  }
  
  public void setMetadataUpdateListener(OnMetadataUpdateListener paramOnMetadataUpdateListener)
  {
    synchronized (this.mCacheLock)
    {
      this.mMetadataUpdateListener = paramOnMetadataUpdateListener;
      return;
    }
  }
  
  public void setNowPlayingEntriesUpdateListener(OnGetNowPlayingEntriesListener paramOnGetNowPlayingEntriesListener)
  {
    Log.d("RemoteControlClient", "setNowPlayingEntriesUpdateListener");
    synchronized (this.mCacheLock)
    {
      this.mGetNowPlayingEntriesListener = paramOnGetNowPlayingEntriesListener;
      return;
    }
  }
  
  public void setOnGetPlaybackPositionListener(OnGetPlaybackPositionListener paramOnGetPlaybackPositionListener)
  {
    synchronized (this.mCacheLock)
    {
      this.mPositionProvider = paramOnGetPlaybackPositionListener;
      return;
    }
  }
  
  public void setPlayItemListener(OnSetPlayItemListener paramOnSetPlayItemListener)
  {
    Log.d("RemoteControlClient", "setPlayItemListener");
    synchronized (this.mCacheLock)
    {
      this.mSetPlayItemListener = paramOnSetPlayItemListener;
      return;
    }
  }
  
  public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener paramOnPlaybackPositionUpdateListener)
  {
    synchronized (this.mCacheLock)
    {
      this.mPositionUpdateListener = paramOnPlaybackPositionUpdateListener;
      return;
    }
  }
  
  public void setPlaybackState(int paramInt)
  {
    setPlaybackStateInt(paramInt, -9216204211029966080L, 1.0F, false);
  }
  
  public void setPlaybackState(int paramInt, long paramLong, float paramFloat)
  {
    setPlaybackStateInt(paramInt, paramLong, paramFloat, true);
  }
  
  public void setTransportControlFlags(int paramInt)
  {
    synchronized (this.mCacheLock)
    {
      this.mTransportControlFlags = paramInt;
      if (this.mSession != null)
      {
        PlaybackState.Builder localBuilder = new PlaybackState.Builder(this.mSessionPlaybackState);
        localBuilder.setActions(PlaybackState.getActionsFromRccControlFlags(paramInt));
        this.mSessionPlaybackState = localBuilder.build();
        this.mSession.setPlaybackState(this.mSessionPlaybackState);
      }
      return;
    }
  }
  
  public void unregisterWithSession(MediaSessionLegacyHelper paramMediaSessionLegacyHelper)
  {
    paramMediaSessionLegacyHelper.removeRccListener(this.mRcMediaIntent);
    this.mSession = null;
  }
  
  public void updateFolderInfoBrowsedPlayer(String paramString)
  {
    Log.e("RemoteControlClient", "updateFolderInfoBrowsedPlayer");
    synchronized (this.mCacheLock)
    {
      updateFolderInfoBrowsedPlayerInt(paramString);
      return;
    }
  }
  
  public void updateNowPlayingContentChange()
  {
    Log.e("RemoteControlClient", "updateNowPlayingContentChange");
    synchronized (this.mCacheLock)
    {
      updateNowPlayingContentChangeInt();
      return;
    }
  }
  
  public void updateNowPlayingEntries(long[] paramArrayOfLong)
  {
    Log.e("RemoteControlClient", "updateNowPlayingEntries: Item numbers: " + paramArrayOfLong.length);
    updateNowPlayingEntriesInt(paramArrayOfLong);
  }
  
  private class EventHandler
    extends Handler
  {
    public EventHandler(RemoteControlClient paramRemoteControlClient, Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        Log.e("RemoteControlClient", "Unknown event " + paramMessage.what + " in RemoteControlClient handler");
        return;
      case 12: 
        Log.d("RemoteControlClient", "MSG_SET_BROWSED_PLAYER in RemoteControlClient");
        RemoteControlClient.-wrap2(RemoteControlClient.this);
        return;
      case 13: 
        RemoteControlClient.-wrap3(RemoteControlClient.this, paramMessage.arg2, ((Long)paramMessage.obj).longValue());
        return;
      }
      RemoteControlClient.-wrap0(RemoteControlClient.this);
    }
  }
  
  @Deprecated
  public class MetadataEditor
    extends MediaMetadataEditor
  {
    public static final int BITMAP_KEY_ARTWORK = 100;
    public static final int METADATA_KEY_ARTWORK = 100;
    
    private MetadataEditor() {}
    
    /* Error */
    public void apply()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 30	android/media/RemoteControlClient$MetadataEditor:mApplied	Z
      //   6: ifeq +14 -> 20
      //   9: ldc 32
      //   11: ldc 34
      //   13: invokestatic 40	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   16: pop
      //   17: aload_0
      //   18: monitorexit
      //   19: return
      //   20: aload_0
      //   21: getfield 18	android/media/RemoteControlClient$MetadataEditor:this$0	Landroid/media/RemoteControlClient;
      //   24: invokestatic 44	android/media/RemoteControlClient:-get0	(Landroid/media/RemoteControlClient;)Ljava/lang/Object;
      //   27: astore_1
      //   28: aload_1
      //   29: monitorenter
      //   30: aload_0
      //   31: getfield 18	android/media/RemoteControlClient$MetadataEditor:this$0	Landroid/media/RemoteControlClient;
      //   34: new 46	android/os/Bundle
      //   37: dup
      //   38: aload_0
      //   39: getfield 50	android/media/RemoteControlClient$MetadataEditor:mEditorMetadata	Landroid/os/Bundle;
      //   42: invokespecial 53	android/os/Bundle:<init>	(Landroid/os/Bundle;)V
      //   45: invokestatic 57	android/media/RemoteControlClient:-set1	(Landroid/media/RemoteControlClient;Landroid/os/Bundle;)Landroid/os/Bundle;
      //   48: pop
      //   49: aload_0
      //   50: getfield 18	android/media/RemoteControlClient$MetadataEditor:this$0	Landroid/media/RemoteControlClient;
      //   53: invokestatic 61	android/media/RemoteControlClient:-get4	(Landroid/media/RemoteControlClient;)Landroid/os/Bundle;
      //   56: ldc 62
      //   58: invokestatic 68	java/lang/String:valueOf	(I)Ljava/lang/String;
      //   61: aload_0
      //   62: getfield 72	android/media/RemoteControlClient$MetadataEditor:mEditableKeys	J
      //   65: invokevirtual 76	android/os/Bundle:putLong	(Ljava/lang/String;J)V
      //   68: aload_0
      //   69: getfield 18	android/media/RemoteControlClient$MetadataEditor:this$0	Landroid/media/RemoteControlClient;
      //   72: invokestatic 80	android/media/RemoteControlClient:-get5	(Landroid/media/RemoteControlClient;)Landroid/graphics/Bitmap;
      //   75: ifnull +20 -> 95
      //   78: aload_0
      //   79: getfield 18	android/media/RemoteControlClient$MetadataEditor:this$0	Landroid/media/RemoteControlClient;
      //   82: invokestatic 80	android/media/RemoteControlClient:-get5	(Landroid/media/RemoteControlClient;)Landroid/graphics/Bitmap;
      //   85: aload_0
      //   86: getfield 84	android/media/RemoteControlClient$MetadataEditor:mEditorArtwork	Landroid/graphics/Bitmap;
      //   89: invokevirtual 90	android/graphics/Bitmap:equals	(Ljava/lang/Object;)Z
      //   92: ifeq +79 -> 171
      //   95: aload_0
      //   96: getfield 18	android/media/RemoteControlClient$MetadataEditor:this$0	Landroid/media/RemoteControlClient;
      //   99: aload_0
      //   100: getfield 84	android/media/RemoteControlClient$MetadataEditor:mEditorArtwork	Landroid/graphics/Bitmap;
      //   103: invokestatic 94	android/media/RemoteControlClient:-set2	(Landroid/media/RemoteControlClient;Landroid/graphics/Bitmap;)Landroid/graphics/Bitmap;
      //   106: pop
      //   107: aload_0
      //   108: aconst_null
      //   109: putfield 84	android/media/RemoteControlClient$MetadataEditor:mEditorArtwork	Landroid/graphics/Bitmap;
      //   112: aload_0
      //   113: getfield 18	android/media/RemoteControlClient$MetadataEditor:this$0	Landroid/media/RemoteControlClient;
      //   116: invokestatic 98	android/media/RemoteControlClient:-get6	(Landroid/media/RemoteControlClient;)Landroid/media/session/MediaSession;
      //   119: ifnull +42 -> 161
      //   122: aload_0
      //   123: getfield 102	android/media/RemoteControlClient$MetadataEditor:mMetadataBuilder	Landroid/media/MediaMetadata$Builder;
      //   126: ifnull +35 -> 161
      //   129: aload_0
      //   130: getfield 18	android/media/RemoteControlClient$MetadataEditor:this$0	Landroid/media/RemoteControlClient;
      //   133: aload_0
      //   134: getfield 102	android/media/RemoteControlClient$MetadataEditor:mMetadataBuilder	Landroid/media/MediaMetadata$Builder;
      //   137: invokevirtual 108	android/media/MediaMetadata$Builder:build	()Landroid/media/MediaMetadata;
      //   140: invokestatic 112	android/media/RemoteControlClient:-set0	(Landroid/media/RemoteControlClient;Landroid/media/MediaMetadata;)Landroid/media/MediaMetadata;
      //   143: pop
      //   144: aload_0
      //   145: getfield 18	android/media/RemoteControlClient$MetadataEditor:this$0	Landroid/media/RemoteControlClient;
      //   148: invokestatic 98	android/media/RemoteControlClient:-get6	(Landroid/media/RemoteControlClient;)Landroid/media/session/MediaSession;
      //   151: aload_0
      //   152: getfield 18	android/media/RemoteControlClient$MetadataEditor:this$0	Landroid/media/RemoteControlClient;
      //   155: invokestatic 116	android/media/RemoteControlClient:-get3	(Landroid/media/RemoteControlClient;)Landroid/media/MediaMetadata;
      //   158: invokevirtual 122	android/media/session/MediaSession:setMetadata	(Landroid/media/MediaMetadata;)V
      //   161: aload_0
      //   162: iconst_1
      //   163: putfield 30	android/media/RemoteControlClient$MetadataEditor:mApplied	Z
      //   166: aload_1
      //   167: monitorexit
      //   168: aload_0
      //   169: monitorexit
      //   170: return
      //   171: aload_0
      //   172: getfield 18	android/media/RemoteControlClient$MetadataEditor:this$0	Landroid/media/RemoteControlClient;
      //   175: invokestatic 80	android/media/RemoteControlClient:-get5	(Landroid/media/RemoteControlClient;)Landroid/graphics/Bitmap;
      //   178: invokevirtual 125	android/graphics/Bitmap:recycle	()V
      //   181: goto -86 -> 95
      //   184: astore_2
      //   185: aload_1
      //   186: monitorexit
      //   187: aload_2
      //   188: athrow
      //   189: astore_1
      //   190: aload_0
      //   191: monitorexit
      //   192: aload_1
      //   193: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	194	0	this	MetadataEditor
      //   189	4	1	localObject2	Object
      //   184	4	2	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   30	95	184	finally
      //   95	161	184	finally
      //   161	166	184	finally
      //   171	181	184	finally
      //   2	17	189	finally
      //   20	30	189	finally
      //   166	168	189	finally
      //   185	189	189	finally
    }
    
    public void clear()
    {
      try
      {
        super.clear();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public Object clone()
      throws CloneNotSupportedException
    {
      throw new CloneNotSupportedException();
    }
    
    public MetadataEditor putBitmap(int paramInt, Bitmap paramBitmap)
      throws IllegalArgumentException
    {
      try
      {
        super.putBitmap(paramInt, paramBitmap);
        if (this.mMetadataBuilder != null)
        {
          String str = MediaMetadata.getKeyFromMetadataEditorKey(paramInt);
          if (str != null) {
            this.mMetadataBuilder.putBitmap(str, paramBitmap);
          }
        }
        return this;
      }
      finally {}
    }
    
    public MetadataEditor putLong(int paramInt, long paramLong)
      throws IllegalArgumentException
    {
      try
      {
        super.putLong(paramInt, paramLong);
        if (this.mMetadataBuilder != null)
        {
          String str = MediaMetadata.getKeyFromMetadataEditorKey(paramInt);
          if (str != null) {
            this.mMetadataBuilder.putLong(str, paramLong);
          }
        }
        return this;
      }
      finally {}
    }
    
    public MetadataEditor putObject(int paramInt, Object paramObject)
      throws IllegalArgumentException
    {
      try
      {
        super.putObject(paramInt, paramObject);
        if ((this.mMetadataBuilder != null) && ((paramInt == 268435457) || (paramInt == 101)))
        {
          String str = MediaMetadata.getKeyFromMetadataEditorKey(paramInt);
          if (str != null) {
            this.mMetadataBuilder.putRating(str, (Rating)paramObject);
          }
        }
        return this;
      }
      finally {}
    }
    
    public MetadataEditor putString(int paramInt, String paramString)
      throws IllegalArgumentException
    {
      try
      {
        super.putString(paramInt, paramString);
        if (this.mMetadataBuilder != null)
        {
          String str = MediaMetadata.getKeyFromMetadataEditorKey(paramInt);
          if (str != null) {
            this.mMetadataBuilder.putText(str, paramString);
          }
        }
        return this;
      }
      finally {}
    }
  }
  
  public static abstract interface OnGetNowPlayingEntriesListener
  {
    public abstract void onGetNowPlayingEntries();
  }
  
  public static abstract interface OnGetPlaybackPositionListener
  {
    public abstract long onGetPlaybackPosition();
  }
  
  public static abstract interface OnMetadataUpdateListener
  {
    public abstract void onMetadataUpdate(int paramInt, Object paramObject);
  }
  
  public static abstract interface OnPlaybackPositionUpdateListener
  {
    public abstract void onPlaybackPositionUpdate(long paramLong);
  }
  
  public static abstract interface OnSetBrowsedPlayerListener
  {
    public abstract void onSetBrowsedPlayer();
  }
  
  public static abstract interface OnSetPlayItemListener
  {
    public abstract void onSetPlayItem(int paramInt, long paramLong);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/RemoteControlClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */