package com.oneplus.gallery2.media;

import android.location.Location;
import android.net.Uri;
import android.util.Size;
import com.oneplus.base.Handle;
import com.oneplus.base.Ref;
import java.io.IOException;
import java.io.InputStream;

public abstract class BaseDecorationMedia
  extends BaseMedia
  implements DecorationMedia
{
  protected BaseDecorationMedia(MediaSource paramMediaSource)
  {
    super(paramMediaSource, MediaType.UNKNOWN);
  }
  
  public abstract BaseDecorationMedia clone();
  
  public Handle delete(Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    return null;
  }
  
  public MediaCacheKey getCacheKey()
  {
    return null;
  }
  
  public Uri getContentUri()
  {
    return null;
  }
  
  public Handle getDetails(Media.DetailsCallback paramDetailsCallback)
  {
    return null;
  }
  
  public String getFilePath()
  {
    return null;
  }
  
  public long getFileSize()
  {
    return 0L;
  }
  
  public long getLastModifiedTime()
  {
    return 0L;
  }
  
  public Location getLocation()
  {
    return null;
  }
  
  public String getMimeType()
  {
    return null;
  }
  
  public Location getPreviousLocation()
  {
    return null;
  }
  
  public long getPreviousTakenTime()
  {
    return 0L;
  }
  
  public Handle getSize(Media.SizeCallback paramSizeCallback)
  {
    return null;
  }
  
  public long getTakenTime()
  {
    return 0L;
  }
  
  public InputStream openInputStream(Ref<Boolean> paramRef, int paramInt)
    throws IOException
  {
    return null;
  }
  
  public Size peekSize()
  {
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/BaseDecorationMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */