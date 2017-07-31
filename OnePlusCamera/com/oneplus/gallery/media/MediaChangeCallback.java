package com.oneplus.gallery.media;

import com.oneplus.base.BitFlagsGroup;

public abstract interface MediaChangeCallback
{
  public static final BitFlagsGroup FLAGS_GROUP = new BitFlagsGroup(MediaChangeCallback.class);
  public static final int FLAG_IGNORE_THUMBNAIL_UPDATE = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_SINGLE_MEDIA_CHANGE = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_SUB_MEDIA = FLAGS_GROUP.nextIntFlag();
  
  public abstract void onMediaCreated(Media paramMedia, int paramInt);
  
  public abstract void onMediaDeleted(Media paramMedia, int paramInt);
  
  public abstract void onMediaRecycled(Media paramMedia, int paramInt);
  
  public abstract void onMediaRestored(Media paramMedia, int paramInt);
  
  public abstract void onMediaUpdated(Media paramMedia, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/MediaChangeCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */