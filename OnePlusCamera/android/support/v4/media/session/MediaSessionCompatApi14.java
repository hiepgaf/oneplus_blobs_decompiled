package android.support.v4.media.session;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Bundle;
import android.os.ResultReceiver;

public class MediaSessionCompatApi14
{
  private static final String METADATA_KEY_ALBUM = "android.media.metadata.ALBUM";
  private static final String METADATA_KEY_ALBUM_ARTIST = "android.media.metadata.ALBUM_ARTIST";
  private static final String METADATA_KEY_ARTIST = "android.media.metadata.ARTIST";
  private static final String METADATA_KEY_AUTHOR = "android.media.metadata.AUTHOR";
  private static final String METADATA_KEY_COMPILATION = "android.media.metadata.COMPILATION";
  private static final String METADATA_KEY_COMPOSER = "android.media.metadata.COMPOSER";
  private static final String METADATA_KEY_DATE = "android.media.metadata.DATE";
  private static final String METADATA_KEY_DISC_NUMBER = "android.media.metadata.DISC_NUMBER";
  private static final String METADATA_KEY_DURATION = "android.media.metadata.DURATION";
  private static final String METADATA_KEY_GENRE = "android.media.metadata.GENRE";
  private static final String METADATA_KEY_NUM_TRACKS = "android.media.metadata.NUM_TRACKS";
  private static final String METADATA_KEY_TITLE = "android.media.metadata.TITLE";
  private static final String METADATA_KEY_TRACK_NUMBER = "android.media.metadata.TRACK_NUMBER";
  private static final String METADATA_KEY_WRITER = "android.media.metadata.WRITER";
  private static final String METADATA_KEY_YEAR = "android.media.metadata.YEAR";
  static final int RCC_PLAYSTATE_NONE = 0;
  static final int STATE_BUFFERING = 6;
  static final int STATE_CONNECTING = 8;
  static final int STATE_ERROR = 7;
  static final int STATE_FAST_FORWARDING = 4;
  static final int STATE_NONE = 0;
  static final int STATE_PAUSED = 2;
  static final int STATE_PLAYING = 3;
  static final int STATE_REWINDING = 5;
  static final int STATE_SKIPPING_TO_NEXT = 10;
  static final int STATE_SKIPPING_TO_PREVIOUS = 9;
  static final int STATE_STOPPED = 1;
  
  static void buildOldMetadata(Bundle paramBundle, RemoteControlClient.MetadataEditor paramMetadataEditor)
  {
    if (!paramBundle.containsKey("android.media.metadata.ALBUM"))
    {
      if (paramBundle.containsKey("android.media.metadata.ALBUM_ARTIST")) {
        break label151;
      }
      label18:
      if (paramBundle.containsKey("android.media.metadata.ARTIST")) {
        break label167;
      }
      label27:
      if (paramBundle.containsKey("android.media.metadata.AUTHOR")) {
        break label182;
      }
      label36:
      if (paramBundle.containsKey("android.media.metadata.COMPILATION")) {
        break label197;
      }
      label45:
      if (paramBundle.containsKey("android.media.metadata.COMPOSER")) {
        break label213;
      }
      label54:
      if (paramBundle.containsKey("android.media.metadata.DATE")) {
        break label228;
      }
      label63:
      if (paramBundle.containsKey("android.media.metadata.DISC_NUMBER")) {
        break label243;
      }
      label72:
      if (paramBundle.containsKey("android.media.metadata.DURATION")) {
        break label259;
      }
      label81:
      if (paramBundle.containsKey("android.media.metadata.GENRE")) {
        break label275;
      }
      label90:
      if (paramBundle.containsKey("android.media.metadata.NUM_TRACKS")) {
        break label291;
      }
      label99:
      if (paramBundle.containsKey("android.media.metadata.TITLE")) {
        break label307;
      }
      label108:
      if (paramBundle.containsKey("android.media.metadata.TRACK_NUMBER")) {
        break label323;
      }
      label117:
      if (paramBundle.containsKey("android.media.metadata.WRITER")) {
        break label338;
      }
    }
    for (;;)
    {
      if (paramBundle.containsKey("android.media.metadata.YEAR")) {
        break label354;
      }
      return;
      paramMetadataEditor.putString(1, paramBundle.getString("android.media.metadata.ALBUM"));
      break;
      label151:
      paramMetadataEditor.putString(13, paramBundle.getString("android.media.metadata.ALBUM_ARTIST"));
      break label18;
      label167:
      paramMetadataEditor.putString(2, paramBundle.getString("android.media.metadata.ARTIST"));
      break label27;
      label182:
      paramMetadataEditor.putString(3, paramBundle.getString("android.media.metadata.AUTHOR"));
      break label36;
      label197:
      paramMetadataEditor.putString(15, paramBundle.getString("android.media.metadata.COMPILATION"));
      break label45;
      label213:
      paramMetadataEditor.putString(4, paramBundle.getString("android.media.metadata.COMPOSER"));
      break label54;
      label228:
      paramMetadataEditor.putString(5, paramBundle.getString("android.media.metadata.DATE"));
      break label63;
      label243:
      paramMetadataEditor.putLong(14, paramBundle.getLong("android.media.metadata.DISC_NUMBER"));
      break label72;
      label259:
      paramMetadataEditor.putLong(9, paramBundle.getLong("android.media.metadata.DURATION"));
      break label81;
      label275:
      paramMetadataEditor.putString(6, paramBundle.getString("android.media.metadata.GENRE"));
      break label90;
      label291:
      paramMetadataEditor.putLong(10, paramBundle.getLong("android.media.metadata.NUM_TRACKS"));
      break label99;
      label307:
      paramMetadataEditor.putString(7, paramBundle.getString("android.media.metadata.TITLE"));
      break label108;
      label323:
      paramMetadataEditor.putLong(0, paramBundle.getLong("android.media.metadata.TRACK_NUMBER"));
      break label117;
      label338:
      paramMetadataEditor.putString(11, paramBundle.getString("android.media.metadata.WRITER"));
    }
    label354:
    paramMetadataEditor.putString(8, paramBundle.getString("android.media.metadata.YEAR"));
  }
  
  public static Object createRemoteControlClient(PendingIntent paramPendingIntent)
  {
    return new RemoteControlClient(paramPendingIntent);
  }
  
  static int getRccStateFromState(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return -1;
    case 6: 
    case 8: 
      return 8;
    case 7: 
      return 9;
    case 4: 
      return 4;
    case 0: 
      return 0;
    case 2: 
      return 2;
    case 3: 
      return 3;
    case 5: 
      return 5;
    case 9: 
      return 7;
    case 10: 
      return 6;
    }
    return 1;
  }
  
  public static void registerRemoteControlClient(Context paramContext, Object paramObject)
  {
    ((AudioManager)paramContext.getSystemService("audio")).registerRemoteControlClient((RemoteControlClient)paramObject);
  }
  
  public static void setMetadata(Object paramObject, Bundle paramBundle)
  {
    paramObject = ((RemoteControlClient)paramObject).editMetadata(true);
    buildOldMetadata(paramBundle, (RemoteControlClient.MetadataEditor)paramObject);
    ((RemoteControlClient.MetadataEditor)paramObject).apply();
  }
  
  public static void setState(Object paramObject, int paramInt)
  {
    ((RemoteControlClient)paramObject).setPlaybackState(getRccStateFromState(paramInt));
  }
  
  public static void unregisterRemoteControlClient(Context paramContext, Object paramObject)
  {
    ((AudioManager)paramContext.getSystemService("audio")).unregisterRemoteControlClient((RemoteControlClient)paramObject);
  }
  
  public static abstract interface Callback
  {
    public abstract void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver);
    
    public abstract void onFastForward();
    
    public abstract boolean onMediaButtonEvent(Intent paramIntent);
    
    public abstract void onPause();
    
    public abstract void onPlay();
    
    public abstract void onRewind();
    
    public abstract void onSeekTo(long paramLong);
    
    public abstract void onSetRating(Object paramObject);
    
    public abstract void onSkipToNext();
    
    public abstract void onSkipToPrevious();
    
    public abstract void onStop();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/media/session/MediaSessionCompatApi14.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */