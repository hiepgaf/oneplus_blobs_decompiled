package com.oneplus.gallery.media;

public abstract interface MediaObtainCallback
{
  public static final MediaObtainCallback EMPTY = new MediaObtainCallback()
  {
    public void onObtained(MediaId paramAnonymousMediaId, Media paramAnonymousMedia) {}
  };
  
  public abstract void onObtained(MediaId paramMediaId, Media paramMedia);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/MediaObtainCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */