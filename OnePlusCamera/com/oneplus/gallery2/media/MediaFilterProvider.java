package com.oneplus.gallery2.media;

import com.oneplus.base.component.Component;

public abstract interface MediaFilterProvider
  extends Component
{
  public abstract MediaFilter createMediaFilter(MediaFilter.FilterParams paramFilterParams, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaFilterProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */