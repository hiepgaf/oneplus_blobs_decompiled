package com.oneplus.gallery2;

import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import com.oneplus.cache.HybridBitmapLruCache;
import com.oneplus.gallery2.media.MediaCacheKey;

public abstract interface CacheManager
  extends Component
{
  public static final PropertyKey<Boolean> PROP_IS_ACTIVE = new PropertyKey("IsActive", Boolean.class, CacheManager.class, Boolean.valueOf(false));
  
  public abstract Handle activate(int paramInt);
  
  public abstract HybridBitmapLruCache<MediaCacheKey> getMicroThumbnailImageCache();
  
  public abstract HybridBitmapLruCache<MediaCacheKey> getSmallThumbnailImageCache();
  
  public abstract HybridBitmapLruCache<MediaCacheKey> getThumbnailImageCache();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/CacheManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */