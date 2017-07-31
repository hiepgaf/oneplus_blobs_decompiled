package com.oneplus.gallery.media;

import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerObject;

public abstract interface Media
  extends HandlerObject
{
  public abstract boolean addToAlbum(long paramLong);
  
  public abstract Uri getContentUri();
  
  public abstract Handle getDetails(MediaDetailsCallback paramMediaDetailsCallback, Handler paramHandler);
  
  public abstract String getFilePath();
  
  public abstract long getFileSize();
  
  public abstract int getHeight();
  
  public abstract MediaId getId();
  
  public abstract long getLastModifiedTime();
  
  public abstract Location getLocation();
  
  public abstract String getMimeType();
  
  public abstract long getTakenTime();
  
  public abstract MediaType getType();
  
  public abstract int getWidth();
  
  public abstract boolean isCapturedByFrontCamera();
  
  public abstract boolean isDocumentUri();
  
  public abstract boolean isFavorite();
  
  public abstract boolean isFavoriteSupported();
  
  public abstract boolean removeFromAlbum(long paramLong);
  
  public abstract boolean setFavorite(boolean paramBoolean);
  
  public static abstract interface MediaDetailsCallback
  {
    public abstract void onMediaDetailsRetrieved(Media paramMedia, Handle paramHandle, MediaDetails paramMediaDetails);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/Media.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */