package android.media.session;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaMetadata;
import android.media.Rating;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;

public class MediaSessionLegacyHelper
{
  private static final boolean DEBUG = Log.isLoggable("MediaSessionHelper", 3);
  private static final String TAG = "MediaSessionHelper";
  private static MediaSessionLegacyHelper sInstance;
  private static final Object sLock = new Object();
  private Context mContext;
  private Handler mHandler = new Handler(Looper.getMainLooper());
  private MediaSessionManager mSessionManager;
  private ArrayMap<PendingIntent, SessionHolder> mSessions = new ArrayMap();
  
  private MediaSessionLegacyHelper(Context paramContext)
  {
    this.mContext = paramContext;
    this.mSessionManager = ((MediaSessionManager)paramContext.getSystemService("media_session"));
  }
  
  public static MediaSessionLegacyHelper getHelper(Context paramContext)
  {
    synchronized (sLock)
    {
      if (sInstance == null) {
        sInstance = new MediaSessionLegacyHelper(paramContext.getApplicationContext());
      }
      return sInstance;
    }
  }
  
  private SessionHolder getHolder(PendingIntent paramPendingIntent, boolean paramBoolean)
  {
    SessionHolder localSessionHolder = (SessionHolder)this.mSessions.get(paramPendingIntent);
    Object localObject = localSessionHolder;
    if (localSessionHolder == null)
    {
      localObject = localSessionHolder;
      if (paramBoolean)
      {
        localObject = new MediaSession(this.mContext, "MediaSessionHelper-" + paramPendingIntent.getCreatorPackage());
        ((MediaSession)localObject).setActive(true);
        localObject = new SessionHolder((MediaSession)localObject, paramPendingIntent);
        this.mSessions.put(paramPendingIntent, localObject);
      }
    }
    return (SessionHolder)localObject;
  }
  
  public static Bundle getOldMetadata(MediaMetadata paramMediaMetadata, int paramInt1, int paramInt2)
  {
    int i;
    Bundle localBundle;
    if ((paramInt1 != -1) && (paramInt2 != -1))
    {
      i = 1;
      localBundle = new Bundle();
      if (paramMediaMetadata.containsKey("android.media.metadata.ALBUM")) {
        localBundle.putString(String.valueOf(1), paramMediaMetadata.getString("android.media.metadata.ALBUM"));
      }
      if ((i == 0) || (!paramMediaMetadata.containsKey("android.media.metadata.ART"))) {
        break label482;
      }
      localBundle.putParcelable(String.valueOf(100), scaleBitmapIfTooBig(paramMediaMetadata.getBitmap("android.media.metadata.ART"), paramInt1, paramInt2));
    }
    for (;;)
    {
      if (paramMediaMetadata.containsKey("android.media.metadata.ALBUM_ARTIST")) {
        localBundle.putString(String.valueOf(13), paramMediaMetadata.getString("android.media.metadata.ALBUM_ARTIST"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.ARTIST")) {
        localBundle.putString(String.valueOf(2), paramMediaMetadata.getString("android.media.metadata.ARTIST"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.AUTHOR")) {
        localBundle.putString(String.valueOf(3), paramMediaMetadata.getString("android.media.metadata.AUTHOR"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.COMPILATION")) {
        localBundle.putString(String.valueOf(15), paramMediaMetadata.getString("android.media.metadata.COMPILATION"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.COMPOSER")) {
        localBundle.putString(String.valueOf(4), paramMediaMetadata.getString("android.media.metadata.COMPOSER"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.DATE")) {
        localBundle.putString(String.valueOf(5), paramMediaMetadata.getString("android.media.metadata.DATE"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.DISC_NUMBER")) {
        localBundle.putLong(String.valueOf(14), paramMediaMetadata.getLong("android.media.metadata.DISC_NUMBER"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.DURATION")) {
        localBundle.putLong(String.valueOf(9), paramMediaMetadata.getLong("android.media.metadata.DURATION"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.GENRE")) {
        localBundle.putString(String.valueOf(6), paramMediaMetadata.getString("android.media.metadata.GENRE"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.NUM_TRACKS")) {
        localBundle.putLong(String.valueOf(10), paramMediaMetadata.getLong("android.media.metadata.NUM_TRACKS"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.RATING")) {
        localBundle.putParcelable(String.valueOf(101), paramMediaMetadata.getRating("android.media.metadata.RATING"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.USER_RATING")) {
        localBundle.putParcelable(String.valueOf(268435457), paramMediaMetadata.getRating("android.media.metadata.USER_RATING"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.TITLE")) {
        localBundle.putString(String.valueOf(7), paramMediaMetadata.getString("android.media.metadata.TITLE"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.TRACK_NUMBER")) {
        localBundle.putLong(String.valueOf(0), paramMediaMetadata.getLong("android.media.metadata.TRACK_NUMBER"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.WRITER")) {
        localBundle.putString(String.valueOf(11), paramMediaMetadata.getString("android.media.metadata.WRITER"));
      }
      if (paramMediaMetadata.containsKey("android.media.metadata.YEAR")) {
        localBundle.putLong(String.valueOf(8), paramMediaMetadata.getLong("android.media.metadata.YEAR"));
      }
      return localBundle;
      i = 0;
      break;
      label482:
      if ((i != 0) && (paramMediaMetadata.containsKey("android.media.metadata.ALBUM_ART"))) {
        localBundle.putParcelable(String.valueOf(100), scaleBitmapIfTooBig(paramMediaMetadata.getBitmap("android.media.metadata.ALBUM_ART"), paramInt1, paramInt2));
      }
    }
  }
  
  private static Bitmap scaleBitmapIfTooBig(Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    Object localObject1 = paramBitmap;
    if (paramBitmap != null)
    {
      int j = paramBitmap.getWidth();
      int i = paramBitmap.getHeight();
      if (j <= paramInt1)
      {
        localObject1 = paramBitmap;
        if (i <= paramInt2) {}
      }
      else
      {
        float f = Math.min(paramInt1 / j, paramInt2 / i);
        paramInt1 = Math.round(j * f);
        paramInt2 = Math.round(i * f);
        Object localObject2 = paramBitmap.getConfig();
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = Bitmap.Config.ARGB_8888;
        }
        localObject1 = Bitmap.createBitmap(paramInt1, paramInt2, (Bitmap.Config)localObject1);
        localObject2 = new Canvas((Bitmap)localObject1);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setFilterBitmap(true);
        ((Canvas)localObject2).drawBitmap(paramBitmap, null, new RectF(0.0F, 0.0F, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight()), localPaint);
      }
    }
    return (Bitmap)localObject1;
  }
  
  private static void sendKeyEvent(PendingIntent paramPendingIntent, Context paramContext, Intent paramIntent)
  {
    try
    {
      paramPendingIntent.send(paramContext, 0, paramIntent);
      return;
    }
    catch (PendingIntent.CanceledException paramPendingIntent)
    {
      Log.e("MediaSessionHelper", "Error sending media key down event:", paramPendingIntent);
    }
  }
  
  public void addMediaButtonListener(PendingIntent paramPendingIntent, ComponentName paramComponentName, Context paramContext)
  {
    if (paramPendingIntent == null)
    {
      Log.w("MediaSessionHelper", "Pending intent was null, can't addMediaButtonListener.");
      return;
    }
    paramComponentName = getHolder(paramPendingIntent, true);
    if (paramComponentName == null) {
      return;
    }
    if ((paramComponentName.mMediaButtonListener != null) && (DEBUG)) {
      Log.d("MediaSessionHelper", "addMediaButtonListener already added " + paramPendingIntent);
    }
    paramComponentName.mMediaButtonListener = new MediaButtonListener(paramPendingIntent, paramContext);
    paramComponentName.mFlags |= 0x1;
    paramComponentName.mSession.setFlags(paramComponentName.mFlags);
    paramComponentName.mSession.setMediaButtonReceiver(paramPendingIntent);
    paramComponentName.update();
    if (DEBUG) {
      Log.d("MediaSessionHelper", "addMediaButtonListener added " + paramPendingIntent);
    }
  }
  
  public void addRccListener(PendingIntent paramPendingIntent, MediaSession.Callback paramCallback)
  {
    if (paramPendingIntent == null)
    {
      Log.w("MediaSessionHelper", "Pending intent was null, can't add rcc listener.");
      return;
    }
    SessionHolder localSessionHolder = getHolder(paramPendingIntent, true);
    if (localSessionHolder == null) {
      return;
    }
    if ((localSessionHolder.mRccListener != null) && (localSessionHolder.mRccListener == paramCallback))
    {
      if (DEBUG) {
        Log.d("MediaSessionHelper", "addRccListener listener already added.");
      }
      return;
    }
    localSessionHolder.mRccListener = paramCallback;
    localSessionHolder.mFlags |= 0x2;
    localSessionHolder.mSession.setFlags(localSessionHolder.mFlags);
    localSessionHolder.update();
    if (DEBUG) {
      Log.d("MediaSessionHelper", "Added rcc listener for " + paramPendingIntent + ".");
    }
  }
  
  public MediaSession getSession(PendingIntent paramPendingIntent)
  {
    paramPendingIntent = (SessionHolder)this.mSessions.get(paramPendingIntent);
    if (paramPendingIntent == null) {
      return null;
    }
    return paramPendingIntent.mSession;
  }
  
  public boolean isGlobalPriorityActive()
  {
    return this.mSessionManager.isGlobalPriorityActive();
  }
  
  public void removeMediaButtonListener(PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent == null) {
      return;
    }
    SessionHolder localSessionHolder = getHolder(paramPendingIntent, false);
    if ((localSessionHolder != null) && (localSessionHolder.mMediaButtonListener != null))
    {
      localSessionHolder.mFlags &= 0xFFFFFFFE;
      localSessionHolder.mSession.setFlags(localSessionHolder.mFlags);
      localSessionHolder.mMediaButtonListener = null;
      localSessionHolder.update();
      if (DEBUG) {
        Log.d("MediaSessionHelper", "removeMediaButtonListener removed " + paramPendingIntent);
      }
    }
  }
  
  public void removeRccListener(PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent == null) {
      return;
    }
    SessionHolder localSessionHolder = getHolder(paramPendingIntent, false);
    if ((localSessionHolder != null) && (localSessionHolder.mRccListener != null))
    {
      localSessionHolder.mRccListener = null;
      localSessionHolder.mFlags &= 0xFFFFFFFD;
      localSessionHolder.mSession.setFlags(localSessionHolder.mFlags);
      localSessionHolder.update();
      if (DEBUG) {
        Log.d("MediaSessionHelper", "Removed rcc listener for " + paramPendingIntent + ".");
      }
    }
  }
  
  public void sendAdjustVolumeBy(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mSessionManager.dispatchAdjustVolume(paramInt1, paramInt2, paramInt3);
    if (DEBUG) {
      Log.d("MediaSessionHelper", "dispatched volume adjustment");
    }
  }
  
  public void sendMediaButtonEvent(KeyEvent paramKeyEvent, boolean paramBoolean)
  {
    if (paramKeyEvent == null)
    {
      Log.w("MediaSessionHelper", "Tried to send a null key event. Ignoring.");
      return;
    }
    this.mSessionManager.dispatchMediaKeyEvent(paramKeyEvent, paramBoolean);
    if (DEBUG) {
      Log.d("MediaSessionHelper", "dispatched media key " + paramKeyEvent);
    }
  }
  
  public void sendVolumeKeyEvent(KeyEvent paramKeyEvent, boolean paramBoolean)
  {
    if (paramKeyEvent == null)
    {
      Log.w("MediaSessionHelper", "Tried to send a null key event. Ignoring.");
      return;
    }
    int k;
    int m;
    label35:
    int i;
    int n;
    label80:
    int j;
    if (paramKeyEvent.getAction() == 0)
    {
      k = 1;
      if (paramKeyEvent.getAction() != 1) {
        break label130;
      }
      m = 1;
      i = 0;
      n = 0;
      switch (paramKeyEvent.getKeyCode())
      {
      default: 
        if ((k != 0) || (m != 0))
        {
          if (!paramBoolean) {
            break label152;
          }
          j = 4608;
          label99:
          if (i == 0) {
            break label173;
          }
          if (m != 0) {
            i = 0;
          }
          this.mSessionManager.dispatchAdjustVolume(Integer.MIN_VALUE, i, j);
        }
        break;
      }
    }
    label130:
    label152:
    label173:
    while ((n == 0) || (k == 0) || (paramKeyEvent.getRepeatCount() != 0))
    {
      return;
      k = 0;
      break;
      m = 0;
      break label35;
      i = 1;
      break label80;
      i = -1;
      break label80;
      n = 1;
      break label80;
      if (m != 0)
      {
        j = 4116;
        break label99;
      }
      j = 4113;
      break label99;
    }
    this.mSessionManager.dispatchAdjustVolume(Integer.MIN_VALUE, 101, j);
  }
  
  private static final class MediaButtonListener
    extends MediaSession.Callback
  {
    private final Context mContext;
    private final PendingIntent mPendingIntent;
    
    public MediaButtonListener(PendingIntent paramPendingIntent, Context paramContext)
    {
      this.mPendingIntent = paramPendingIntent;
      this.mContext = paramContext;
    }
    
    private void sendKeyEvent(int paramInt)
    {
      KeyEvent localKeyEvent = new KeyEvent(0, paramInt);
      Intent localIntent = new Intent("android.intent.action.MEDIA_BUTTON");
      localIntent.addFlags(268435456);
      localIntent.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent);
      MediaSessionLegacyHelper.-wrap0(this.mPendingIntent, this.mContext, localIntent);
      localIntent.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(1, paramInt));
      MediaSessionLegacyHelper.-wrap0(this.mPendingIntent, this.mContext, localIntent);
      if (MediaSessionLegacyHelper.-get0()) {
        Log.d("MediaSessionHelper", "Sent " + paramInt + " to pending intent " + this.mPendingIntent);
      }
    }
    
    public void onFastForward()
    {
      sendKeyEvent(90);
    }
    
    public boolean onMediaButtonEvent(Intent paramIntent)
    {
      MediaSessionLegacyHelper.-wrap0(this.mPendingIntent, this.mContext, paramIntent);
      return true;
    }
    
    public void onPause()
    {
      sendKeyEvent(127);
    }
    
    public void onPlay()
    {
      sendKeyEvent(126);
    }
    
    public void onRewind()
    {
      sendKeyEvent(89);
    }
    
    public void onSkipToNext()
    {
      sendKeyEvent(87);
    }
    
    public void onSkipToPrevious()
    {
      sendKeyEvent(88);
    }
    
    public void onStop()
    {
      sendKeyEvent(86);
    }
  }
  
  private class SessionHolder
  {
    public SessionCallback mCb;
    public int mFlags;
    public MediaSessionLegacyHelper.MediaButtonListener mMediaButtonListener;
    public final PendingIntent mPi;
    public MediaSession.Callback mRccListener;
    public final MediaSession mSession;
    
    public SessionHolder(MediaSession paramMediaSession, PendingIntent paramPendingIntent)
    {
      this.mSession = paramMediaSession;
      this.mPi = paramPendingIntent;
    }
    
    public void update()
    {
      if ((this.mMediaButtonListener == null) && (this.mRccListener == null))
      {
        this.mSession.setCallback(null);
        this.mSession.release();
        this.mCb = null;
        MediaSessionLegacyHelper.-get1(MediaSessionLegacyHelper.this).remove(this.mPi);
      }
      while (this.mCb != null) {
        return;
      }
      this.mCb = new SessionCallback(null);
      Handler localHandler = new Handler(Looper.getMainLooper());
      this.mSession.setCallback(this.mCb, localHandler);
    }
    
    private class SessionCallback
      extends MediaSession.Callback
    {
      private SessionCallback() {}
      
      public void getNowPlayingEntries()
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mRccListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mRccListener.getNowPlayingEntries();
        }
      }
      
      public void onFastForward()
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener.onFastForward();
        }
      }
      
      public boolean onMediaButtonEvent(Intent paramIntent)
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener.onMediaButtonEvent(paramIntent);
        }
        return true;
      }
      
      public void onPause()
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener.onPause();
        }
      }
      
      public void onPlay()
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener.onPlay();
        }
      }
      
      public void onRewind()
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener.onRewind();
        }
      }
      
      public void onSeekTo(long paramLong)
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mRccListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mRccListener.onSeekTo(paramLong);
        }
      }
      
      public void onSetRating(Rating paramRating)
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mRccListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mRccListener.onSetRating(paramRating);
        }
      }
      
      public void onSkipToNext()
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener.onSkipToNext();
        }
      }
      
      public void onSkipToPrevious()
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener.onSkipToPrevious();
        }
      }
      
      public void onStop()
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mMediaButtonListener.onStop();
        }
      }
      
      public void setBrowsedPlayer()
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mRccListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mRccListener.setBrowsedPlayer();
        }
      }
      
      public void setPlayItem(int paramInt, long paramLong)
      {
        if (MediaSessionLegacyHelper.SessionHolder.this.mRccListener != null) {
          MediaSessionLegacyHelper.SessionHolder.this.mRccListener.setPlayItem(paramInt, paramLong);
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/session/MediaSessionLegacyHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */