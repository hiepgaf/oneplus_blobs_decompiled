package com.oneplus.gallery2.media;

import java.util.Map;

public class BaseMediaDetails
  implements MediaDetails
{
  private final Map<MediaDetails.Key<?>, Object> m_Values;
  
  public BaseMediaDetails(Map<MediaDetails.Key<?>, Object> paramMap)
  {
    this.m_Values = paramMap;
  }
  
  public <T> T get(MediaDetails.Key<T> paramKey, T paramT)
  {
    if (paramKey == null) {}
    do
    {
      do
      {
        return paramT;
      } while (this.m_Values == null);
      paramKey = this.m_Values.get(paramKey);
    } while (paramKey == null);
    return paramKey;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/BaseMediaDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */