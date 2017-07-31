package com.oneplus.gallery2.media;

import com.oneplus.base.BitFlagsGroup;

public abstract interface EditedMedia
  extends Media
{
  public static final int FLAG_ORIGINAL_MEDIA_CHANGED = FLAGS_GROUP.nextIntFlag();
  
  public abstract Media getOriginalMedia();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/EditedMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */