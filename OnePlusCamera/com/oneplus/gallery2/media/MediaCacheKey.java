package com.oneplus.gallery2.media;

import android.net.Uri;
import java.io.Serializable;

public abstract interface MediaCacheKey
  extends Serializable
{
  public abstract Uri getContentUri();
  
  public abstract String getFilePath();
  
  public abstract boolean isExpired();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaCacheKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */