package com.oneplus.gallery2.media;

import com.oneplus.base.EventArgs;

public class MediaSetEventArgs
  extends EventArgs
{
  private final MediaSet m_MediaSet;
  
  public MediaSetEventArgs(MediaSet paramMediaSet)
  {
    this.m_MediaSet = paramMediaSet;
  }
  
  public MediaSet getMediaSet()
  {
    return this.m_MediaSet;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaSetEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */