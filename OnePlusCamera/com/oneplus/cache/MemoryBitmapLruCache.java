package com.oneplus.cache;

import android.graphics.Bitmap;

public class MemoryBitmapLruCache<TKey>
  extends MemoryLruCache<TKey, Bitmap>
{
  public MemoryBitmapLruCache(long paramLong)
  {
    super(paramLong);
  }
  
  protected long getSizeInBytes(Object paramObject, Bitmap paramBitmap)
  {
    return paramBitmap.getByteCount();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/cache/MemoryBitmapLruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */