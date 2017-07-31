package com.oneplus.gallery.media;

import com.oneplus.base.BitFlagsGroup;
import java.util.Iterator;

public abstract interface MediaIterable
{
  public static final BitFlagsGroup FLAGS_GROUP = new BitFlagsGroup(MediaIterable.class);
  public static final int FLAG_GROUP_MEDIA;
  public static final int FLAG_NORMAL_MEDIA = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_PHOTO_ONLY = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_RECYCLED_MEDIA;
  public static final int FLAG_SUB_MEDIA = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_VIDEO_ONLY = FLAGS_GROUP.nextIntFlag();
  
  static
  {
    FLAG_GROUP_MEDIA = FLAGS_GROUP.nextIntFlag();
    FLAG_RECYCLED_MEDIA = FLAGS_GROUP.nextIntFlag();
  }
  
  public abstract Iterator<Media> iterateMedia();
  
  public abstract Iterator<Media> iterateMedia(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/MediaIterable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */