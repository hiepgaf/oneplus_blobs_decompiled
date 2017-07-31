package com.oneplus.gallery2.media;

import java.util.Comparator;

public abstract class MediaComparator
  implements Comparator<Media>
{
  public static final MediaComparator ADDED_TIME_DESC = new MediaComparator(Media.FLAG_FILE_PATH_CHANGED)
  {
    public int compare(Media paramAnonymousMedia1, Media paramAnonymousMedia2)
    {
      boolean bool1 = paramAnonymousMedia1 instanceof MediaStoreItem;
      boolean bool2 = paramAnonymousMedia2 instanceof MediaStoreItem;
      label22:
      label27:
      String str1;
      String str2;
      if (!bool1)
      {
        if (!bool1) {
          break label69;
        }
        if (bool1) {
          break label76;
        }
        str1 = paramAnonymousMedia1.getFilePath();
        str2 = paramAnonymousMedia2.getFilePath();
        if (str1 != null) {
          break label142;
        }
        if (str2 != null) {
          break label164;
        }
      }
      label69:
      label76:
      int i;
      label142:
      do
      {
        return ID_DESC.compare(paramAnonymousMedia1, paramAnonymousMedia2);
        if (bool2) {
          break;
        }
        return -1;
        if (!bool2) {
          break label22;
        }
        return 1;
        if (!bool2) {
          break label27;
        }
        long l = ((MediaStoreItem)paramAnonymousMedia2).getAddedTime() - ((MediaStoreItem)paramAnonymousMedia1).getAddedTime();
        if (l >= 0L) {}
        for (i = 1; i == 0; i = 0) {
          return -1;
        }
        if (l <= 1L) {}
        for (i = 1; i == 0; i = 0) {
          return 1;
        }
        break label27;
        if (str2 == null) {
          return 1;
        }
        i = str1.compareToIgnoreCase(str2);
      } while (i == 0);
      return -i;
      label164:
      return -1;
    }
  };
  public static final MediaComparator FILE_PATH_ASC = new MediaComparator(Media.FLAG_FILE_PATH_CHANGED)
  {
    public int compare(Media paramAnonymousMedia1, Media paramAnonymousMedia2)
    {
      String str1 = paramAnonymousMedia1.getFilePath();
      String str2 = paramAnonymousMedia2.getFilePath();
      if (str1 == null)
      {
        if (str2 != null) {}
      }
      else
      {
        int i;
        do
        {
          return -ID_DESC.compare(paramAnonymousMedia1, paramAnonymousMedia2);
          if (str2 == null) {
            return -1;
          }
          i = str1.compareToIgnoreCase(str2);
        } while (i == 0);
        return i;
      }
      return 1;
    }
  };
  public static final MediaComparator FILE_PATH_DESC = new MediaComparator(Media.FLAG_FILE_PATH_CHANGED)
  {
    public int compare(Media paramAnonymousMedia1, Media paramAnonymousMedia2)
    {
      String str1 = paramAnonymousMedia1.getFilePath();
      String str2 = paramAnonymousMedia2.getFilePath();
      if (str1 == null)
      {
        if (str2 != null) {}
      }
      else
      {
        int i;
        do
        {
          return ID_DESC.compare(paramAnonymousMedia1, paramAnonymousMedia2);
          if (str2 == null) {
            return 1;
          }
          i = str1.compareToIgnoreCase(str2);
        } while (i == 0);
        return -i;
      }
      return -1;
    }
  };
  public static final MediaComparator ID_DESC = new MediaComparator(0)
  {
    public int compare(Media paramAnonymousMedia1, Media paramAnonymousMedia2)
    {
      paramAnonymousMedia1 = paramAnonymousMedia1.getId();
      paramAnonymousMedia2 = paramAnonymousMedia2.getId();
      if (paramAnonymousMedia1 == null)
      {
        if (paramAnonymousMedia2 != null) {}
      }
      else
      {
        int i;
        do
        {
          return 0;
          if (paramAnonymousMedia2 == null) {
            return 1;
          }
          i = paramAnonymousMedia1.compareTo(paramAnonymousMedia2);
        } while (i == 0);
        return -i;
      }
      return -1;
    }
  };
  public static final MediaComparator TAKEN_TIME_DESC = new MediaComparator(Media.FLAG_TAKEN_TIME_CHANGED | Media.FLAG_FILE_PATH_CHANGED)
  {
    public int compare(Media paramAnonymousMedia1, Media paramAnonymousMedia2)
    {
      int j = 0;
      long l = paramAnonymousMedia2.getTakenTime() - paramAnonymousMedia1.getTakenTime();
      if (l >= 0L) {}
      for (int i = 1; i == 0; i = 0) {
        return -1;
      }
      i = j;
      if (l <= 1L) {
        i = 1;
      }
      if (i == 0) {
        return 1;
      }
      String str1 = paramAnonymousMedia1.getFilePath();
      String str2 = paramAnonymousMedia2.getFilePath();
      if (str1 == null)
      {
        if (str2 != null) {}
      }
      else
      {
        do
        {
          return ID_DESC.compare(paramAnonymousMedia1, paramAnonymousMedia2);
          if (str2 == null) {
            return 1;
          }
          i = str1.compareToIgnoreCase(str2);
        } while (i == 0);
        return -i;
      }
      return -1;
    }
  };
  private final int m_EffectiveMediaUpdateFlags;
  
  protected MediaComparator(int paramInt)
  {
    this.m_EffectiveMediaUpdateFlags = paramInt;
  }
  
  public final int getEffectiveMediaUpdateFlags()
  {
    return this.m_EffectiveMediaUpdateFlags;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaComparator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */