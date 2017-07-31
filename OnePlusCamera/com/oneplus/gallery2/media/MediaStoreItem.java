package com.oneplus.gallery2.media;

abstract interface MediaStoreItem
{
  public abstract long getAddedTime();
  
  public abstract String getFilePath();
  
  public abstract long getParentId();
  
  public abstract long getPreviousParentId();
  
  public abstract void notifyParentVisibilityChanged(boolean paramBoolean);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaStoreItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */