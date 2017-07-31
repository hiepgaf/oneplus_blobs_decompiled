package com.oneplus.gallery2.media;

import com.oneplus.base.BitFlagsGroup;
import com.oneplus.base.Handle;

public abstract interface PhotoMedia
  extends Media
{
  public static final int FLAG_RAW_MEDIA_CHANGED = FLAGS_GROUP.nextIntFlag();
  
  public abstract Handle checkAnimatable(CheckAnimatableCallback paramCheckAnimatableCallback);
  
  public abstract PhotoMedia getEncodedMedia();
  
  public abstract PhotoMedia getRawMedia();
  
  public abstract boolean isBokeh();
  
  public abstract boolean isBurstGroup();
  
  public abstract boolean isPanorama();
  
  public abstract boolean isRaw();
  
  public abstract Boolean peekIsAnimatable();
  
  public static abstract interface CheckAnimatableCallback
  {
    public abstract void onChecked(PhotoMedia paramPhotoMedia, boolean paramBoolean);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/PhotoMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */