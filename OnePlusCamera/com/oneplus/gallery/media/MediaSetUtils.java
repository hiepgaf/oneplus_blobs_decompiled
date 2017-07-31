package com.oneplus.gallery.media;

public final class MediaSetUtils
{
  public static boolean containsMedia(MediaSet paramMediaSet)
  {
    if (paramMediaSet != null)
    {
      paramMediaSet = (Integer)paramMediaSet.get(MediaSet.PROP_MEDIA_COUNT);
      if (paramMediaSet != null) {
        break label25;
      }
    }
    label25:
    while (paramMediaSet.intValue() <= 0)
    {
      return false;
      return false;
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/MediaSetUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */