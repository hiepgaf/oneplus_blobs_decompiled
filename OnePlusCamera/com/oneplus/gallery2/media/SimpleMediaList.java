package com.oneplus.gallery2.media;

import com.oneplus.base.ListHandlerBaseObject;

public class SimpleMediaList
  extends ListHandlerBaseObject<Media>
  implements MediaList
{
  private final Media[] m_Media;
  
  public SimpleMediaList(Media... paramVarArgs)
  {
    this.m_Media = paramVarArgs;
  }
  
  public Media get(int paramInt)
  {
    return this.m_Media[paramInt];
  }
  
  public int size()
  {
    return this.m_Media.length;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/SimpleMediaList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */