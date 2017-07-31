package android.support.v4.media.session;

import android.graphics.Bitmap;
import android.media.Rating;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.media.RemoteControlClient.OnMetadataUpdateListener;
import android.os.Build.VERSION;
import android.os.Bundle;

public class MediaSessionCompatApi19
{
  private static final String METADATA_KEY_ALBUM_ART = "android.media.metadata.ALBUM_ART";
  private static final String METADATA_KEY_ART = "android.media.metadata.ART";
  private static final String METADATA_KEY_RATING = "android.media.metadata.RATING";
  private static final String METADATA_KEY_USER_RATING = "android.media.metadata.USER_RATING";
  
  static void addNewMetadata(Bundle paramBundle, RemoteControlClient.MetadataEditor paramMetadataEditor)
  {
    if (!paramBundle.containsKey("android.media.metadata.RATING")) {
      if (paramBundle.containsKey("android.media.metadata.USER_RATING")) {
        break label53;
      }
    }
    for (;;)
    {
      if (paramBundle.containsKey("android.media.metadata.ART")) {
        break label69;
      }
      if (paramBundle.containsKey("android.media.metadata.ALBUM_ART")) {
        break label86;
      }
      return;
      paramMetadataEditor.putObject(101, paramBundle.getParcelable("android.media.metadata.RATING"));
      break;
      label53:
      paramMetadataEditor.putObject(268435457, paramBundle.getParcelable("android.media.metadata.USER_RATING"));
    }
    label69:
    paramMetadataEditor.putBitmap(100, (Bitmap)paramBundle.getParcelable("android.media.metadata.ART"));
    return;
    label86:
    paramMetadataEditor.putBitmap(100, (Bitmap)paramBundle.getParcelable("android.media.metadata.ALBUM_ART"));
  }
  
  public static Object createMetadataUpdateListener(MediaSessionCompatApi14.Callback paramCallback)
  {
    return new OnMetadataUpdateListener(paramCallback);
  }
  
  public static void setMetadata(Object paramObject, Bundle paramBundle, boolean paramBoolean)
  {
    paramObject = ((RemoteControlClient)paramObject).editMetadata(true);
    MediaSessionCompatApi14.buildOldMetadata(paramBundle, (RemoteControlClient.MetadataEditor)paramObject);
    addNewMetadata(paramBundle, (RemoteControlClient.MetadataEditor)paramObject);
    if (!paramBoolean) {}
    for (;;)
    {
      ((RemoteControlClient.MetadataEditor)paramObject).apply();
      return;
      if (Build.VERSION.SDK_INT > 19) {
        ((RemoteControlClient.MetadataEditor)paramObject).addEditableKey(268435457);
      }
    }
  }
  
  public static void setOnMetadataUpdateListener(Object paramObject1, Object paramObject2)
  {
    ((RemoteControlClient)paramObject1).setMetadataUpdateListener((RemoteControlClient.OnMetadataUpdateListener)paramObject2);
  }
  
  static class OnMetadataUpdateListener<T extends MediaSessionCompatApi14.Callback>
    implements RemoteControlClient.OnMetadataUpdateListener
  {
    protected final T mCallback;
    
    public OnMetadataUpdateListener(T paramT)
    {
      this.mCallback = paramT;
    }
    
    public void onMetadataUpdate(int paramInt, Object paramObject)
    {
      if (paramInt != 268435457) {}
      while (!(paramObject instanceof Rating)) {
        return;
      }
      this.mCallback.onSetRating(paramObject);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/media/session/MediaSessionCompatApi19.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */