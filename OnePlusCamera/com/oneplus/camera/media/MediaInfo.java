package com.oneplus.camera.media;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.SparseIntArray;
import com.oneplus.database.CursorUtils;
import com.oneplus.media.ThumbnailImageDrawable;
import java.io.File;
import java.util.Map;

public abstract class MediaInfo
{
  public static final String DETAILS_LOCATION = "Location";
  private final Uri m_ContentUri;
  private final String m_FilePath;
  private final long m_FileSize;
  private final int m_Height;
  private final long m_LastModifiedTime;
  private final MediaType m_MediaType;
  private final int m_OneplusFlag;
  private final int m_Width;
  
  protected MediaInfo(Uri paramUri, Cursor paramCursor)
  {
    if (paramCursor != null)
    {
      int i = 0;
      if (paramUri != null)
      {
        int j = CursorUtils.getInt(paramCursor, "_id", 0);
        if (j > 0)
        {
          this.m_ContentUri = Uri.parse(paramUri.toString() + "/" + j);
          i = MediaListManager.getMediaListFromGallery().get(j, 0);
          this.m_FilePath = CursorUtils.getString(paramCursor, "_data");
          paramUri = CursorUtils.getString(paramCursor, "mime_type");
          if (paramUri == null) {
            break label312;
          }
          if (!paramUri.startsWith("image/")) {
            break label285;
          }
          this.m_MediaType = MediaType.PHOTO;
        }
      }
      for (;;)
      {
        l2 = CursorUtils.getLong(paramCursor, "_size", 0L);
        l1 = l2;
        if (l2 <= 0L)
        {
          l1 = l2;
          if (this.m_FilePath == null) {}
        }
        try
        {
          paramUri = new File(this.m_FilePath);
          l1 = l2;
          if (paramUri.exists()) {
            l1 = paramUri.length();
          }
        }
        catch (Throwable paramUri)
        {
          for (;;)
          {
            l1 = l2;
          }
        }
        this.m_FileSize = l1;
        l2 = CursorUtils.getLong(paramCursor, "date_modified", 0L);
        l1 = l2;
        if (l2 <= 0L)
        {
          l1 = l2;
          if (this.m_FilePath == null) {}
        }
        try
        {
          paramUri = new File(this.m_FilePath);
          l1 = l2;
          if (paramUri.exists()) {
            l1 = paramUri.lastModified();
          }
        }
        catch (Throwable paramUri)
        {
          for (;;)
          {
            l1 = l2;
          }
        }
        this.m_LastModifiedTime = l1;
        this.m_OneplusFlag = i;
        this.m_Width = CursorUtils.getInt(paramCursor, "width", 0);
        this.m_Height = CursorUtils.getInt(paramCursor, "height", 0);
        return;
        this.m_ContentUri = paramUri;
        break;
        this.m_ContentUri = null;
        break;
        label285:
        if (paramUri.startsWith("video/"))
        {
          this.m_MediaType = MediaType.VIDEO;
        }
        else
        {
          this.m_MediaType = null;
          continue;
          label312:
          this.m_MediaType = null;
        }
      }
    }
    this.m_ContentUri = paramUri;
    this.m_FilePath = null;
    this.m_FileSize = 0L;
    this.m_LastModifiedTime = 0L;
    this.m_MediaType = null;
    this.m_Width = 0;
    this.m_Height = 0;
    this.m_OneplusFlag = 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof MediaInfo))
    {
      paramObject = (MediaInfo)paramObject;
      if ((this.m_ContentUri == null) || (this.m_ContentUri.equals(((MediaInfo)paramObject).m_ContentUri)))
      {
        if ((this.m_ContentUri == null) && (((MediaInfo)paramObject).m_ContentUri != null)) {
          return false;
        }
      }
      else {
        return false;
      }
      if ((this.m_FilePath == null) || (this.m_FilePath.equals(((MediaInfo)paramObject).m_FilePath)))
      {
        if ((this.m_FilePath == null) && (((MediaInfo)paramObject).m_FilePath != null)) {
          return false;
        }
      }
      else {
        return false;
      }
      return true;
    }
    return false;
  }
  
  public Uri getContentUri()
  {
    return this.m_ContentUri;
  }
  
  public Map<String, Object> getDetails(Context paramContext)
  {
    return null;
  }
  
  public Drawable getDisplayThumbnailDrawable()
  {
    if (this.m_FilePath != null)
    {
      if (this.m_MediaType == MediaType.PHOTO) {}
      for (int i = 1;; i = 3) {
        return new ThumbnailImageDrawable(this.m_FilePath, i, true);
      }
    }
    return null;
  }
  
  public String getFilePath()
  {
    return this.m_FilePath;
  }
  
  public long getFileSize()
  {
    return this.m_FileSize;
  }
  
  public int getHeight()
  {
    return this.m_Height;
  }
  
  public long getLastModifiedTime()
  {
    return this.m_LastModifiedTime;
  }
  
  public MediaType getMediaType()
  {
    return this.m_MediaType;
  }
  
  public int getOnePlusFlag()
  {
    return this.m_OneplusFlag;
  }
  
  public abstract long getTakenTime();
  
  public int getWidth()
  {
    return this.m_Width;
  }
  
  public boolean hasContentUri()
  {
    return this.m_ContentUri != null;
  }
  
  public boolean hasFilePath()
  {
    return this.m_FilePath != null;
  }
  
  public int hashCode()
  {
    if (this.m_ContentUri != null) {
      return this.m_ContentUri.hashCode();
    }
    if (this.m_FilePath != null) {
      return this.m_FilePath.hashCode();
    }
    return super.hashCode();
  }
  
  public boolean isPhoto()
  {
    return this instanceof PhotoMediaInfo;
  }
  
  public boolean isVideo()
  {
    return this instanceof VideoMediaInfo;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.m_ContentUri != null)
    {
      localStringBuilder.append(this.m_ContentUri);
      if (!isPhoto()) {
        break label72;
      }
      localStringBuilder.append(" (Photo)");
    }
    for (;;)
    {
      return localStringBuilder.toString();
      if (this.m_FilePath != null)
      {
        localStringBuilder.append(this.m_FilePath);
        break;
      }
      localStringBuilder.append("UNKNOWN");
      break;
      label72:
      if (isVideo()) {
        localStringBuilder.append(" (Video)");
      } else {
        localStringBuilder.append(" (Unknown)");
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/MediaInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */