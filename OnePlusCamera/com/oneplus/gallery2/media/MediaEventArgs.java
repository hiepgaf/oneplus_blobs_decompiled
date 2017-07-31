package com.oneplus.gallery2.media;

import com.oneplus.base.EventArgs;

public class MediaEventArgs
  extends EventArgs
{
  private final int m_Flags;
  private final Media m_Media;
  
  public MediaEventArgs(Media paramMedia, int paramInt)
  {
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */