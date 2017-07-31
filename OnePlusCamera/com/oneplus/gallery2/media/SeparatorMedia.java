package com.oneplus.gallery2.media;

import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;

public abstract interface SeparatorMedia
  extends PropertySource, DecorationMedia
{
  public static final PropertyKey<CharSequence> PROP_SUMMARY = new PropertyKey("Summary", CharSequence.class, SeparatorMedia.class, 1, null);
  public static final PropertyKey<CharSequence> PROP_TITLE = new PropertyKey("Title", CharSequence.class, SeparatorMedia.class, 1, null);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/SeparatorMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */