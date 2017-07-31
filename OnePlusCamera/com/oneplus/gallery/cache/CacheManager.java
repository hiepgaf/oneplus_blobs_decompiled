package com.oneplus.gallery.cache;

import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import com.oneplus.cache.HybridBitmapLruCache;

public abstract interface CacheManager
  extends Component
{
  public static final PropertyKey<Boolean> PROP_IS_ACTIVE = new PropertyKey("IsActive", Boolean.class, CacheManager.class, Boolean.valueOf(false));
  
  public abstract Handle activate(int paramInt);
  
  public abstract HybridBitmapLruCache<ImageCacheKey> getSmallThumbnailImageCache();
  
  public abstract HybridBitmapLruCache<ImageCacheKey> getThumbnailImageCache();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/cache/CacheManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */