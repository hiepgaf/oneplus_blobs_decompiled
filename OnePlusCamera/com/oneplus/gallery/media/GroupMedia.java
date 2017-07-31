package com.oneplus.gallery.media;

public abstract interface GroupMedia
  extends Media
{
  public abstract void addGroupMediaChangeCallback(GroupMediaChangeCallback paramGroupMediaChangeCallback);
  
  public abstract int getCoverIndex();
  
  public abstract Media getCoverMedia();
  
  public abstract String getGroupId();
  
  public abstract boolean isUserCoverFound();
  
  public abstract MediaList openGroupMediaList(MediaComparator paramMediaComparator, int paramInt);
  
  public abstract void removeGroupMediaChangeCallback(GroupMediaChangeCallback paramGroupMediaChangeCallback);
  
  public abstract boolean setCoverMedia(Media paramMedia, int paramInt);
  
  public static abstract interface GroupMediaChangeCallback
  {
    public abstract void onCoverChanged(GroupMedia paramGroupMedia, int paramInt);
    
    public abstract void onSubMediaSizeChanged(GroupMedia paramGroupMedia, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/GroupMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */