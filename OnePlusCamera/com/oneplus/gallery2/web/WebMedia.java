package com.oneplus.gallery2.web;

import com.oneplus.gallery2.media.Media;

public abstract interface WebMedia<TMediaInfo>
  extends Media
{
  public abstract String getOwner();
  
  public abstract void release();
  
  public abstract int update(TMediaInfo paramTMediaInfo);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/web/WebMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */