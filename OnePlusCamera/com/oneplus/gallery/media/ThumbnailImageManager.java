package com.oneplus.gallery.media;

import android.graphics.Bitmap;
import android.os.Handler;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface ThumbnailImageManager
  extends Component
{
  public static final int FLAG_ASYNC = 1;
  public static final int FLAG_KEEP_TEMP_THUMB = 4;
  public static final int FLAG_URGENT = 2;
  public static final PropertyKey<Boolean> PROP_IS_ACTIVE = new PropertyKey("IsActive", Boolean.class, ThumbnailImageManager.class, Boolean.valueOf(false));
  
  public abstract Handle activate(int paramInt);
  
  public abstract Handle decodeSmallThumbnailImage(Media paramMedia, int paramInt, DecodingCallback paramDecodingCallback, Handler paramHandler);
  
  public abstract Handle decodeThumbnailImage(Media paramMedia, int paramInt, DecodingCallback paramDecodingCallback, Handler paramHandler);
  
  public abstract Bitmap getCachedSmallThumbnailImage(Media paramMedia);
  
  public abstract Bitmap getCachedThumbnailImage(Media paramMedia);
  
  public abstract Bitmap getTempThumbnailImage(Media paramMedia);
  
  public abstract void invalidateThumbnailImages(Media paramMedia, int paramInt);
  
  public abstract boolean setTempThumbnailImage(Media paramMedia, Bitmap paramBitmap, int paramInt);
  
  public static abstract interface DecodingCallback
  {
    public abstract void onThumbnailImageDecoded(Handle paramHandle, Media paramMedia, Bitmap paramBitmap);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/ThumbnailImageManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */