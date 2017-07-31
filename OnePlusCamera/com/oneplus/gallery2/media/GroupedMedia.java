package com.oneplus.gallery2.media;

import com.oneplus.base.BitFlagsGroup;

public abstract interface GroupedMedia
  extends Media
{
  public static final int FLAG_COVER_CHANGED = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_SUB_MEDIA_COUNT_CHANGED = FLAGS_GROUP.nextIntFlag();
  
  public abstract boolean contains(Media paramMedia);
  
  public abstract <T extends Media> T getCover();
  
  public abstract int getSubMediaCount();
  
  public abstract MediaList openSubMediaList(MediaComparator paramMediaComparator, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/GroupedMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */