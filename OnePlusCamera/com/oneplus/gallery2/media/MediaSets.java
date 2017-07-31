package com.oneplus.gallery2.media;

public final class MediaSets
{
  public static boolean isEmpty(MediaSet paramMediaSet)
  {
    if (paramMediaSet != null) {
      return isEmpty((Integer)paramMediaSet.get(MediaSet.PROP_MEDIA_COUNT));
    }
    return true;
  }
  
  public static boolean isEmpty(Integer paramInteger)
  {
    if (paramInteger == null) {}
    while (paramInteger.intValue() <= 0) {
      return true;
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaSets.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */