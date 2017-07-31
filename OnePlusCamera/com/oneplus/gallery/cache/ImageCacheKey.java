package com.oneplus.gallery.cache;

import android.net.Uri;
import com.oneplus.gallery.media.Media;
import java.io.File;
import java.io.Serializable;

public class ImageCacheKey
  implements Serializable
{
  private static final long serialVersionUID = 6417044270020048991L;
  public final Uri contentUri;
  public final String filePath;
  public final long fileSize;
  public final long lastModifiedTime;
  
  public ImageCacheKey(Media paramMedia)
  {
    this.filePath = paramMedia.getFilePath();
    this.fileSize = paramMedia.getFileSize();
    this.lastModifiedTime = paramMedia.getLastModifiedTime();
    if (this.filePath != null) {}
    for (paramMedia = (Media)localObject;; paramMedia = paramMedia.getContentUri())
    {
      this.contentUri = paramMedia;
      return;
    }
  }
  
  public ImageCacheKey(File paramFile)
  {
    this.filePath = paramFile.getAbsolutePath();
    this.fileSize = paramFile.length();
    this.lastModifiedTime = paramFile.lastModified();
    this.contentUri = null;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ImageCacheKey)) {
      return false;
    }
    paramObject = (ImageCacheKey)paramObject;
    if (paramObject != this)
    {
      if (this.lastModifiedTime > 0L) {
        break label53;
      }
      i = 1;
      if (i == 0) {
        if (((ImageCacheKey)paramObject).lastModifiedTime <= 0L) {
          break label58;
        }
      }
    }
    label53:
    label58:
    for (int i = 1;; i = 0)
    {
      if (i != 0) {
        break label63;
      }
      return false;
      return true;
      i = 0;
      break;
    }
    label63:
    if ((this.lastModifiedTime != ((ImageCacheKey)paramObject).lastModifiedTime) || (this.fileSize != ((ImageCacheKey)paramObject).fileSize)) {
      return false;
    }
    if (this.filePath == null)
    {
      if (this.filePath == null) {
        break label135;
      }
      label103:
      if (this.contentUri != null) {
        break label145;
      }
    }
    label135:
    label145:
    while (this.contentUri.equals(((ImageCacheKey)paramObject).contentUri))
    {
      if (this.contentUri == null) {
        break label161;
      }
      return true;
      if (this.filePath.equals(((ImageCacheKey)paramObject).filePath)) {
        break;
      }
      do
      {
        return false;
      } while (((ImageCacheKey)paramObject).filePath != null);
      break label103;
    }
    label161:
    while (((ImageCacheKey)paramObject).contentUri != null) {
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = (int)(this.lastModifiedTime & 0xFFFFFFFF);
    if (this.filePath == null)
    {
      if (this.contentUri == null) {
        return i;
      }
    }
    else {
      return i | this.filePath.hashCode();
    }
    return i | this.contentUri.hashCode();
  }
  
  public String toString()
  {
    if (this.filePath == null)
    {
      if (this.contentUri == null) {
        return "[LMT=" + this.lastModifiedTime + "]";
      }
    }
    else {
      return "[" + this.filePath + " ]";
    }
    return "[" + this.contentUri + " ]";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/cache/ImageCacheKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */