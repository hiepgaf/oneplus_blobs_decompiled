package com.oneplus.gallery2.media;

import android.content.ContentResolver;
import android.location.Location;
import android.net.Uri;
import android.provider.DocumentsContract;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import com.oneplus.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class TempMedia
  extends BaseMedia
{
  private final Uri m_ContentUri;
  private final String m_FilePath;
  private long m_FileSize = -1L;
  private final String m_Id;
  private final String m_MimeType;
  
  protected TempMedia(MediaType paramMediaType, Uri paramUri, String paramString1, String paramString2)
  {
    super((MediaSource)BaseApplication.current().findComponent(TempMediaSource.class), paramMediaType);
    if (paramUri != null)
    {
      this.m_ContentUri = paramUri;
      this.m_MimeType = paramString1;
      this.m_FilePath = paramString2;
      paramMediaType = new StringBuilder("Temp[");
      if (paramString1 == null) {
        break label89;
      }
    }
    for (;;)
    {
      this.m_Id = (paramString1 + "]" + paramUri);
      return;
      throw new IllegalArgumentException("No content URI");
      label89:
      paramString1 = "";
    }
  }
  
  public static TempMedia create(Uri paramUri, String paramString)
  {
    return create(paramUri, paramString, null);
  }
  
  public static TempMedia create(Uri paramUri, String paramString1, String paramString2)
  {
    String str;
    if (FileUtils.isImageFilePath(paramString2))
    {
      str = paramString2;
      if (paramString1 != null) {
        break label57;
      }
    }
    for (;;)
    {
      if (paramUri == null)
      {
        Log.e(TempMedia.class.getSimpleName(), "create() - Unknown content URI : " + paramUri);
        return null;
        str = paramString2;
        if (FileUtils.isVideoFilePath(paramString2)) {
          break;
        }
        str = null;
        break;
        label57:
        if (!paramString1.startsWith("image/"))
        {
          if (paramString1.startsWith("video/")) {
            return new TempVideoMedia(paramUri, paramString1, str);
          }
        }
        else {
          return new TempPhotoMedia(paramUri, paramString1, str);
        }
      }
    }
    if (!DocumentsContract.isDocumentUri(BaseApplication.current(), paramUri)) {}
    for (;;)
    {
      if (!FileUtils.isImageFilePath(paramUri.getPath()))
      {
        if (!FileUtils.isVideoFilePath(paramUri.getPath())) {
          break;
        }
        return new TempVideoMedia(paramUri, paramString1, str);
        paramString2 = DocumentsContract.getDocumentId(paramUri);
        if (paramString2 != null) {
          if (!paramString2.startsWith("image:"))
          {
            if (paramString2.startsWith("video:")) {
              return new TempVideoMedia(paramUri, paramString1, str);
            }
          }
          else {
            return new TempPhotoMedia(paramUri, paramString1, str);
          }
        }
      }
    }
    return new TempPhotoMedia(paramUri, paramString1, str);
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
  
  public String getDisplayName()
  {
    return this.m_ContentUri.getLastPathSegment();
  }
  
  public String getFilePath()
  {
    return this.m_FilePath;
  }
  
  public long getFileSize()
  {
    if (this.m_FilePath == null) {
      return 0L;
    }
    int i;
    if (this.m_FileSize >= 0L) {
      i = 1;
    }
    for (;;)
    {
      if (i == 0) {}
      try
      {
        this.m_FileSize = new File(this.m_FilePath).length();
        return this.m_FileSize;
        i = 0;
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          this.m_FileSize = 0L;
        }
      }
    }
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
  
  public long getTakenTime()
  {
    return 0L;
  }
  
  public InputStream openInputStream(Ref<Boolean> paramRef, int paramInt)
    throws IOException
  {
    return BaseApplication.current().getContentResolver().openInputStream(this.m_ContentUri);
  }
  
  public Handle prepareSharing(PrepareSharingCallback paramPrepareSharingCallback, int paramInt)
  {
    if (this.m_ContentUri == null) {}
    while ((this.m_MimeType == null) || (!"media".equals(this.m_ContentUri.getHost())))
    {
      MediaSharingManager localMediaSharingManager = (MediaSharingManager)BaseApplication.current().findComponent(MediaSharingManager.class);
      if (localMediaSharingManager == null) {
        break;
      }
      return localMediaSharingManager.prepareSharing(this, paramPrepareSharingCallback, 0);
    }
    return super.prepareSharing(paramPrepareSharingCallback, paramInt);
    return null;
  }
  
  public String toString()
  {
    return "[Temp, " + this.m_ContentUri + ", " + this.m_MimeType + "]";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/TempMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */