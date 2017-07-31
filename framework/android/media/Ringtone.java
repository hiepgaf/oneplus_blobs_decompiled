package android.media;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;

public class Ringtone
{
  private static boolean DBG = Build.DEBUG_ONEPLUS;
  private static final boolean LOGD = true;
  private static final String[] MEDIA_COLUMNS = { "_id", "_data", "title" };
  private static final String MEDIA_SELECTION = "mime_type LIKE 'audio/%' OR mime_type IN ('application/ogg', 'application/x-flac')";
  private static final String TAG = "Ringtone";
  private static final ArrayList<Ringtone> sActiveRingtones = new ArrayList();
  private final boolean mAllowRemote;
  private AudioAttributes mAudioAttributes = new AudioAttributes.Builder().setUsage(6).setContentType(4).build();
  private final AudioManager mAudioManager;
  private final MyOnCompletionListener mCompletionListener = new MyOnCompletionListener();
  private final Context mContext;
  private boolean mIsLooping = false;
  private MediaPlayer mLocalPlayer;
  private final Object mPlaybackSettingsLock = new Object();
  private final IRingtonePlayer mRemotePlayer;
  private final Binder mRemoteToken;
  private String mTitle;
  private Uri mUri;
  private float mVolume = 1.0F;
  
  public Ringtone(Context paramContext, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.mAudioManager = ((AudioManager)this.mContext.getSystemService("audio"));
    this.mAllowRemote = paramBoolean;
    if (paramBoolean) {}
    for (paramContext = this.mAudioManager.getRingtonePlayer();; paramContext = null)
    {
      this.mRemotePlayer = paramContext;
      paramContext = (Context)localObject;
      if (paramBoolean) {
        paramContext = new Binder();
      }
      this.mRemoteToken = paramContext;
      return;
    }
  }
  
  private void applyPlaybackProperties_sync()
  {
    if (this.mLocalPlayer != null)
    {
      this.mLocalPlayer.setVolume(this.mVolume);
      this.mLocalPlayer.setLooping(this.mIsLooping);
      return;
    }
    if ((this.mAllowRemote) && (this.mRemotePlayer != null)) {
      try
      {
        this.mRemotePlayer.setPlaybackProperties(this.mRemoteToken, this.mVolume, this.mIsLooping);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("Ringtone", "Problem setting playback properties: ", localRemoteException);
        return;
      }
    }
    Log.w("Ringtone", "Neither local nor remote player available when applying playback properties");
  }
  
  private void destroyLocalPlayer()
  {
    if (this.mLocalPlayer != null)
    {
      this.mLocalPlayer.reset();
      finalize();
    }
    synchronized (sActiveRingtones)
    {
      sActiveRingtones.remove(this);
      return;
    }
  }
  
  public static String getTitle(Context paramContext, Uri paramUri, boolean paramBoolean1, boolean paramBoolean2)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    Object localObject7 = null;
    localObject6 = null;
    Object localObject2 = null;
    localObject1 = localObject2;
    if (paramUri != null)
    {
      localObject4 = paramUri.getAuthority();
      if (!"settings".equals(localObject4)) {
        break label124;
      }
      localObject1 = localObject2;
      if (paramBoolean1)
      {
        if (RingtoneManager.getDefaultType(paramUri) != 1) {
          break label112;
        }
        paramUri = RingtoneManager.getActualRingtoneUriBySubId(paramContext, RingtoneManager.getDefaultRingtoneSubIdByUri(paramUri));
        localObject1 = paramContext.getString(17040359, new Object[] { getTitle(paramContext, paramUri, false, paramBoolean2) });
      }
    }
    for (;;)
    {
      paramUri = (Uri)localObject1;
      if (localObject1 == null)
      {
        paramContext = paramContext.getString(17040362);
        paramUri = paramContext;
        if (paramContext == null) {
          paramUri = "";
        }
      }
      return paramUri;
      label112:
      paramUri = RingtoneManager.getActualDefaultRingtoneUri(paramContext, RingtoneManager.getDefaultType(paramUri));
      break;
      label124:
      Object localObject8 = null;
      Object localObject9 = null;
      localObject5 = null;
      localObject1 = localObject8;
      localObject2 = localObject9;
      try
      {
        if ("media".equals(localObject4))
        {
          if (paramBoolean2) {}
          for (localObject4 = null;; localObject4 = "mime_type LIKE 'audio/%' OR mime_type IN ('application/ogg', 'application/x-flac')")
          {
            localObject1 = localObject8;
            localObject2 = localObject9;
            localObject4 = localContentResolver.query(paramUri, MEDIA_COLUMNS, (String)localObject4, null, null);
            localObject5 = localObject4;
            if (localObject4 == null) {
              break;
            }
            localObject5 = localObject4;
            localObject1 = localObject4;
            localObject2 = localObject4;
            if (((Cursor)localObject4).getCount() != 1) {
              break;
            }
            localObject1 = localObject4;
            localObject2 = localObject4;
            ((Cursor)localObject4).moveToFirst();
            localObject1 = localObject4;
            localObject2 = localObject4;
            localObject5 = ((Cursor)localObject4).getString(2);
            if (localObject4 != null) {
              ((Cursor)localObject4).close();
            }
            return (String)localObject5;
          }
        }
        localObject2 = localObject7;
        if (localObject5 != null)
        {
          ((Cursor)localObject5).close();
          localObject2 = localObject7;
        }
      }
      catch (SecurityException localSecurityException)
      {
        for (;;)
        {
          localObject5 = null;
          if (!paramBoolean2) {
            break label334;
          }
          Object localObject3 = localObject1;
          localObject5 = ((AudioManager)paramContext.getSystemService("audio")).getRingtonePlayer();
          localObject4 = localObject6;
          if (localObject5 == null) {
            break label357;
          }
          localObject3 = localObject1;
          try
          {
            localObject4 = ((IRingtonePlayer)localObject5).getTitle(paramUri);
            localObject3 = localObject4;
            if (localObject1 == null) {
              continue;
            }
            ((Cursor)localObject1).close();
            localObject3 = localObject4;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              localObject4 = localObject6;
            }
          }
        }
      }
      finally
      {
        if (localRemoteException == null) {
          break label402;
        }
        localRemoteException.close();
      }
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = paramUri.getLastPathSegment();
      }
    }
  }
  
  private boolean playFallbackRingtone()
  {
    if (this.mAudioManager.getStreamVolume(AudioAttributes.toLegacyStreamType(this.mAudioAttributes)) != 0)
    {
      int i = RingtoneManager.getDefaultRingtoneSubIdByUri(this.mUri);
      if ((i == -1) || (RingtoneManager.getActualRingtoneUriBySubId(this.mContext, i) == null)) {}
    }
    else
    {
      try
      {
        localAssetFileDescriptor = this.mContext.getResources().openRawResourceFd(17825797);
        if (localAssetFileDescriptor == null) {
          break label186;
        }
        this.mLocalPlayer = new MediaPlayer();
        if (localAssetFileDescriptor.getDeclaredLength() >= 0L) {
          break label131;
        }
        this.mLocalPlayer.setDataSource(localAssetFileDescriptor.getFileDescriptor());
      }
      catch (IOException localIOException)
      {
        synchronized (this.mPlaybackSettingsLock)
        {
          for (;;)
          {
            AssetFileDescriptor localAssetFileDescriptor;
            applyPlaybackProperties_sync();
            this.mLocalPlayer.prepare();
            startLocalPlayer();
            localAssetFileDescriptor.close();
            return true;
            this.mLocalPlayer.setDataSource(localAssetFileDescriptor.getFileDescriptor(), localAssetFileDescriptor.getStartOffset(), localAssetFileDescriptor.getDeclaredLength());
          }
          localIOException = localIOException;
          destroyLocalPlayer();
          Log.e("Ringtone", "Failed to open fallback ringtone");
          return false;
        }
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        label131:
        Log.e("Ringtone", "Fallback ringtone does not exist");
        return false;
      }
      this.mLocalPlayer.setAudioAttributes(this.mAudioAttributes);
      label186:
      Log.e("Ringtone", "Could not load fallback ringtone");
      return false;
    }
    Log.w("Ringtone", "not playing fallback for " + this.mUri);
    return false;
  }
  
  private void startLocalPlayer()
  {
    if (this.mLocalPlayer == null) {
      return;
    }
    synchronized (sActiveRingtones)
    {
      sActiveRingtones.add(this);
      this.mLocalPlayer.setOnCompletionListener(this.mCompletionListener);
      this.mAudioManager.setParameters("playback=ring");
      this.mLocalPlayer.start();
      return;
    }
  }
  
  protected void finalize()
  {
    if (this.mLocalPlayer != null)
    {
      this.mLocalPlayer.release();
      this.mLocalPlayer = null;
    }
  }
  
  public AudioAttributes getAudioAttributes()
  {
    return this.mAudioAttributes;
  }
  
  @Deprecated
  public int getStreamType()
  {
    return AudioAttributes.toLegacyStreamType(this.mAudioAttributes);
  }
  
  public String getTitle(Context paramContext)
  {
    if (this.mTitle != null) {
      return this.mTitle;
    }
    paramContext = getTitle(paramContext, this.mUri, true, this.mAllowRemote);
    this.mTitle = paramContext;
    return paramContext;
  }
  
  public Uri getUri()
  {
    return this.mUri;
  }
  
  public boolean isPlaying()
  {
    if (this.mLocalPlayer != null) {
      return this.mLocalPlayer.isPlaying();
    }
    if ((this.mAllowRemote) && (this.mRemotePlayer != null)) {
      try
      {
        boolean bool = this.mRemotePlayer.isPlaying(this.mRemoteToken);
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("Ringtone", "Problem checking ringtone: " + localRemoteException);
        return false;
      }
    }
    Log.w("Ringtone", "Neither local nor remote playback available");
    return false;
  }
  
  public void play()
  {
    if (this.mUri == null)
    {
      if (DBG) {
        Log.d("Ringtone", "Skip to play for null ringtone uri.");
      }
      return;
    }
    if (this.mLocalPlayer != null) {
      if (this.mAudioManager.getStreamVolume(AudioAttributes.toLegacyStreamType(this.mAudioAttributes)) != 0) {
        startLocalPlayer();
      }
    }
    do
    {
      for (;;)
      {
        return;
        if ((this.mAllowRemote) && (this.mRemotePlayer != null))
        {
          Uri localUri = this.mUri.getCanonicalUri();
          synchronized (this.mPlaybackSettingsLock)
          {
            boolean bool = this.mIsLooping;
            float f = this.mVolume;
            try
            {
              this.mRemotePlayer.play(this.mRemoteToken, localUri, this.mAudioAttributes, f, bool);
              return;
            }
            catch (RemoteException localRemoteException) {}
            if (!playFallbackRingtone())
            {
              Log.w("Ringtone", "Problem playing ringtone: " + localRemoteException);
              return;
            }
          }
        }
      }
    } while (playFallbackRingtone());
    Log.w("Ringtone", "Neither local nor remote playback available");
  }
  
  public void setAudioAttributes(AudioAttributes paramAudioAttributes)
    throws IllegalArgumentException
  {
    if (paramAudioAttributes == null) {
      throw new IllegalArgumentException("Invalid null AudioAttributes for Ringtone");
    }
    this.mAudioAttributes = paramAudioAttributes;
    setUri(this.mUri);
  }
  
  public void setLooping(boolean paramBoolean)
  {
    synchronized (this.mPlaybackSettingsLock)
    {
      this.mIsLooping = paramBoolean;
      applyPlaybackProperties_sync();
      return;
    }
  }
  
  @Deprecated
  public void setStreamType(int paramInt)
  {
    setAudioAttributes(new AudioAttributes.Builder().setInternalLegacyStreamType(paramInt).build());
  }
  
  void setTitle(String paramString)
  {
    this.mTitle = paramString;
  }
  
  public void setUri(Uri arg1)
  {
    destroyLocalPlayer();
    this.mUri = RingtoneManager.validForSound(this.mContext, ???, null);
    if (this.mUri == null)
    {
      if (DBG) {
        Log.d("Ringtone", "Return for null ringtone uri.");
      }
      return;
    }
    this.mLocalPlayer = new MediaPlayer();
    for (;;)
    {
      try
      {
        this.mLocalPlayer.setDataSource(this.mContext, this.mUri);
        this.mLocalPlayer.setAudioAttributes(this.mAudioAttributes);
      }
      catch (SecurityException|IOException ???)
      {
        destroyLocalPlayer();
        if (this.mAllowRemote) {
          continue;
        }
        Log.w("Ringtone", "Remote playback not allowed: " + ???);
        continue;
        Log.d("Ringtone", "Problem opening; delegating to remote player");
      }
      synchronized (this.mPlaybackSettingsLock)
      {
        applyPlaybackProperties_sync();
        this.mLocalPlayer.prepare();
        if (this.mLocalPlayer != null)
        {
          Log.d("Ringtone", "Successfully created local player");
          return;
        }
      }
    }
  }
  
  public void setVolume(float paramFloat)
  {
    Object localObject1 = this.mPlaybackSettingsLock;
    float f = paramFloat;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    paramFloat = f;
    if (f > 1.0F) {
      paramFloat = 1.0F;
    }
    try
    {
      this.mVolume = paramFloat;
      applyPlaybackProperties_sync();
      return;
    }
    finally
    {
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  public void stop()
  {
    this.mAudioManager.setParameters("playback=music");
    if (this.mLocalPlayer != null) {
      destroyLocalPlayer();
    }
    while ((!this.mAllowRemote) || (this.mRemotePlayer == null)) {
      return;
    }
    try
    {
      this.mRemotePlayer.stop(this.mRemoteToken);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("Ringtone", "Problem stopping ringtone: " + localRemoteException);
    }
  }
  
  class MyOnCompletionListener
    implements MediaPlayer.OnCompletionListener
  {
    MyOnCompletionListener() {}
    
    public void onCompletion(MediaPlayer paramMediaPlayer)
    {
      synchronized ()
      {
        Ringtone.-get0().remove(Ringtone.this);
        paramMediaPlayer.setOnCompletionListener(null);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/Ringtone.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */