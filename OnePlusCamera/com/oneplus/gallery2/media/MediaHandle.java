package com.oneplus.gallery2.media;

import com.oneplus.base.Handle;

public abstract class MediaHandle
  extends Handle
{
  private final int m_Flags;
  private final Media m_Media;
  
  protected MediaHandle(String paramString, Media paramMedia, int paramInt)
  {
    super(paramString);
    this.m_Media = paramMedia;
    this.m_Flags = paramInt;
  }
  
  public final int getFlags()
  {
    return this.m_Flags;
  }
  
  public final Media getMedia()
  {
    return this.m_Media;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaHandle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */