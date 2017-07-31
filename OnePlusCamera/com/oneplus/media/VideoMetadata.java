package com.oneplus.media;

import com.oneplus.base.PropertyKey;

public abstract interface VideoMetadata
  extends Metadata
{
  public static final PropertyKey<Long> PROP_DURATION = new PropertyKey("Duration", Long.class, VideoMetadata.class, Long.valueOf(0L));
  public static final PropertyKey<Integer> PROP_HEIGHT = new PropertyKey("Height", Integer.class, VideoMetadata.class, Integer.valueOf(0));
  public static final PropertyKey<Integer> PROP_ORIENTATION = new PropertyKey("Orientation", Integer.class, VideoMetadata.class, Integer.valueOf(0));
  public static final PropertyKey<Integer> PROP_WIDTH = new PropertyKey("Width", Integer.class, VideoMetadata.class, Integer.valueOf(0));
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/VideoMetadata.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */