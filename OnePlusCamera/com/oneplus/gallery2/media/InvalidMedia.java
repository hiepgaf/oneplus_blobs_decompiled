package com.oneplus.gallery2.media;

import android.location.Location;
import android.net.Uri;
import android.util.Size;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Ref;
import java.io.IOException;
import java.io.InputStream;

public abstract class InvalidMedia
  extends BaseMedia
{
  private final Uri m_ContentUri;
  private final String m_Id;
  private final String m_MimeType;
  
  protected InvalidMedia(MediaType paramMediaType, Uri paramUri, String paramString)
  {
    super((MediaSource)BaseApplication.current().findComponent(TempMediaSource.class), paramMediaType);
    this.m_ContentUri = paramUri;
    this.m_MimeType = paramString;
    if (paramUri == null)
    {
      this.m_Id = ("Temp/" + System.currentTimeMillis() + "_" + (int)(Math.random() * 2.147483647E9D));
      return;
    }
    this.m_Id = ("Temp/" + paramUri);
  }
  
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
    return this.m_ContentUri;
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
  
  public String getId()
  {
    return this.m_Id;
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
    return this.m_MimeType;
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
    throw new RuntimeException("Cannot open invalid media");
  }
  
  public Size peekSize()
  {
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/InvalidMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */