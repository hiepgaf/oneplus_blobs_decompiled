package com.oneplus.gallery.media;

import com.oneplus.base.EventArgs;

public class MediaEventArgs
  extends EventArgs
{
  private final Media m_Media;
  
  public MediaEventArgs(Media paramMedia)
  {
    this.m_Media = paramMedia;
  }
  
  public final Media getMedia()
  {
    return this.m_Media;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/MediaEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */