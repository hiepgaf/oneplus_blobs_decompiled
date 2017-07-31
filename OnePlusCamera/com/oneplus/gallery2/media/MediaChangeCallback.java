package com.oneplus.gallery2.media;

public abstract class MediaChangeCallback
{
  public static final MediaChangeCallback EMPTY = new MediaChangeCallback() {};
  
  public void onMediaCreated(MediaSource paramMediaSource, Media paramMedia, int paramInt) {}
  
  public void onMediaDeleted(MediaSource paramMediaSource, Media paramMedia, int paramInt) {}
  
  public void onMediaUpdated(MediaSource paramMediaSource, Media paramMedia, int paramInt) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaChangeCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */