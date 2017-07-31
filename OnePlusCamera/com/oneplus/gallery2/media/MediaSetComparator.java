package com.oneplus.gallery2.media;

import com.oneplus.base.PropertyKey;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public abstract class MediaSetComparator
  implements Comparator<MediaSet>
{
  public static final MediaSetComparator DEFAULT = new MediaSetComparator(new PropertyKey[] { MediaSet.PROP_LAST_MEDIA_ADDED_TIME, MediaSet.PROP_NAME })
  {
    public int compare(MediaSet paramAnonymousMediaSet1, MediaSet paramAnonymousMediaSet2)
    {
      Class localClass1 = paramAnonymousMediaSet1.getClass();
      Class localClass2 = paramAnonymousMediaSet2.getClass();
      if (localClass1.equals(localClass2)) {}
      int i;
      do
      {
        int j;
        do
        {
          i = MediaSetComparator.compareMediaAddedTimeDesc(paramAnonymousMediaSet1, paramAnonymousMediaSet2);
          if (i != 0) {
            break label104;
          }
          i = MediaSetComparator.compareNamesAsc(paramAnonymousMediaSet1, paramAnonymousMediaSet2);
          if (i != 0) {
            return i;
          }
          return paramAnonymousMediaSet1.hashCode() - paramAnonymousMediaSet2.hashCode();
          i = MediaSetComparator.DEFAULT_MEDIA_SET_ORDER.indexOf(localClass1);
          j = MediaSetComparator.DEFAULT_MEDIA_SET_ORDER.indexOf(localClass2);
          if (i >= 0) {
            break;
          }
        } while (j < 0);
        return 1;
        if (j < 0) {
          break;
        }
        i -= j;
      } while (i == 0);
      return i;
      return -1;
      label104:
      return i;
      return i;
    }
  };
  private static final List<Class<?>> DEFAULT_MEDIA_SET_ORDER = Arrays.asList(new Class[] { AllMediaMediaSet.class, CameraRollMediaSet.class, PtpCameraRollMediaSet.class, SelfieMediaSet.class, FavoriteMediaSet.class, ScreenshotMediaSet.class });
  private final List<PropertyKey<?>> m_ReferencedProperties;
  
  protected MediaSetComparator(PropertyKey<?>... paramVarArgs)
  {
    this.m_ReferencedProperties = Arrays.asList(paramVarArgs);
  }
  
  private static int compareMediaAddedTimeDesc(MediaSet paramMediaSet1, MediaSet paramMediaSet2)
  {
    long l1 = ((Long)paramMediaSet1.get(MediaSet.PROP_LAST_MEDIA_ADDED_TIME)).longValue();
    long l2 = ((Long)paramMediaSet2.get(MediaSet.PROP_LAST_MEDIA_ADDED_TIME)).longValue();
    if (l1 <= l2) {}
    for (int i = 1; i == 0; i = 0) {
      return -1;
    }
    if (l1 >= l2) {}
    for (i = 1; i == 0; i = 0) {
      return 1;
    }
    return 0;
  }
  
  private static int compareNamesAsc(MediaSet paramMediaSet1, MediaSet paramMediaSet2)
  {
    paramMediaSet1 = (String)paramMediaSet1.get(MediaSet.PROP_NAME);
    paramMediaSet2 = (String)paramMediaSet2.get(MediaSet.PROP_NAME);
    if (paramMediaSet1 == null)
    {
      if (paramMediaSet2 == null) {
        return 0;
      }
    }
    else
    {
      if (paramMediaSet2 == null) {
        return -1;
      }
      return paramMediaSet1.compareTo(paramMediaSet2);
    }
    return 1;
  }
  
  public final List<PropertyKey<?>> getReferencedProperties()
  {
    return this.m_ReferencedProperties;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaSetComparator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */