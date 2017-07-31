package com.oneplus.gallery.media;

public abstract interface VideoMedia
  extends Media
{
  public abstract long getDuration();
  
  public abstract boolean isSlowMotion();
  
  public abstract boolean isTimeLapse();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/VideoMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */