package com.oneplus.gallery2.media;

import com.oneplus.base.PropertyKey;

public abstract interface TimeSeparatorMedia
  extends SeparatorMedia
{
  public static final PropertyKey<Long> PROP_TIME = new PropertyKey("Time", Long.class, TimeSeparatorMedia.class, Long.valueOf(0L));
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/TimeSeparatorMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */