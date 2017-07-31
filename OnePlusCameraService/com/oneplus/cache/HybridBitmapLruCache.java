package com.oneplus.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.os.SystemClock;
import java.io.Serializable;

public class HybridBitmapLruCache<TKey extends Serializable>
  implements Cache<TKey, Bitmap>
{
  private final CompressedBitmapLruCache<TKey> m_CompressedMemoryCache;
  private final DiskBitmapLruCache<TKey> m_DiskCache;
  private final MemoryBitmapLruCache<TKey> m_MemoryCache;
  private final Bitmap.Config m_PreferredConfig;
  
  public HybridBitmapLruCache(Context paramContext, String paramString, Bitmap.Config paramConfig, Bitmap.CompressFormat paramCompressFormat, long paramLong1, long paramLong2)
  {
    this(paramContext, paramString, paramConfig, paramCompressFormat, paramLong1, 0L, paramLong2);
  }
  
  public HybridBitmapLruCache(Context paramContext, String paramString, Bitmap.Config paramConfig, Bitmap.CompressFormat paramCompressFormat, long paramLong1, long paramLong2, long paramLong3)
  {
    MemoryBitmapLruCache localMemoryBitmapLruCache;
    if (paramLong1 > 0L)
    {
      localMemoryBitmapLruCache = new MemoryBitmapLruCache(paramLong1);
      this.m_MemoryCache = localMemoryBitmapLruCache;
      this.m_PreferredConfig = paramConfig;
      if (paramLong2 <= 0L) {
        break label98;
      }
      paramConfig = new CompressedBitmapLruCache(paramContext, paramString, Bitmap.Config.ARGB_8888, paramCompressFormat, paramLong2);
      label57:
      this.m_CompressedMemoryCache = paramConfig;
      if (paramLong3 <= 0L) {
        break label103;
      }
    }
    label98:
    label103:
    for (paramContext = new DiskBitmapLruCache(paramContext, paramString, Bitmap.Config.ARGB_8888, paramCompressFormat, paramLong3);; paramContext = null)
    {
      this.m_DiskCache = paramContext;
      return;
      localMemoryBitmapLruCache = null;
      break;
      paramConfig = null;
      break label57;
    }
  }
  
  public boolean add(TKey paramTKey, Bitmap paramBitmap)
  {
    if (this.m_CompressedMemoryCache != null) {
      this.m_CompressedMemoryCache.add(paramTKey, paramBitmap);
    }
    if (this.m_DiskCache != null) {
      this.m_DiskCache.add(paramTKey, paramBitmap);
    }
    if (this.m_MemoryCache != null)
    {
      Bitmap localBitmap = paramBitmap;
      if (paramBitmap.getConfig() != this.m_PreferredConfig) {
        localBitmap = paramBitmap.copy(this.m_PreferredConfig, false);
      }
      if (!this.m_MemoryCache.add(paramTKey, localBitmap)) {
        return false;
      }
    }
    return (this.m_MemoryCache != null) || (this.m_DiskCache != null);
  }
  
  public void clear()
  {
    if (this.m_MemoryCache != null) {
      this.m_MemoryCache.clear();
    }
    if (this.m_CompressedMemoryCache != null) {
      this.m_CompressedMemoryCache.clear();
    }
    if (this.m_DiskCache != null) {
      this.m_DiskCache.clear();
    }
  }
  
  public void close()
  {
    if (this.m_MemoryCache != null) {
      this.m_MemoryCache.close();
    }
    if (this.m_CompressedMemoryCache != null) {
      this.m_CompressedMemoryCache.close();
    }
    if (this.m_DiskCache != null) {
      this.m_DiskCache.close();
    }
  }
  
  public void disableStatistic()
  {
    if (this.m_CompressedMemoryCache != null) {
      this.m_CompressedMemoryCache.disableStatistic();
    }
    if (this.m_DiskCache != null) {
      this.m_DiskCache.disableStatistic();
    }
  }
  
  public void enableStatistic()
  {
    if (this.m_CompressedMemoryCache != null) {
      this.m_CompressedMemoryCache.enableStatistic();
    }
    if (this.m_DiskCache != null) {
      this.m_DiskCache.enableStatistic();
    }
  }
  
  public void flush()
  {
    if (this.m_DiskCache != null) {
      this.m_DiskCache.flush();
    }
  }
  
  public Bitmap get(TKey paramTKey, Bitmap paramBitmap, long paramLong)
  {
    if (this.m_MemoryCache != null) {}
    for (Bitmap localBitmap1 = (Bitmap)this.m_MemoryCache.get(paramTKey, paramBitmap, 0L); localBitmap1 != paramBitmap; localBitmap1 = paramBitmap) {
      return localBitmap1;
    }
    long l = paramLong;
    if (this.m_CompressedMemoryCache != null)
    {
      int i;
      if (paramLong != 0L)
      {
        i = 1;
        if (i == 0) {
          break label141;
        }
      }
      label141:
      for (l = SystemClock.elapsedRealtime();; l = 0L)
      {
        localBitmap1 = (Bitmap)this.m_CompressedMemoryCache.get(paramTKey, paramBitmap, paramLong);
        if (localBitmap1 == paramBitmap) {
          break label147;
        }
        paramBitmap = localBitmap1;
        if (this.m_MemoryCache != null)
        {
          paramBitmap = localBitmap1;
          if (localBitmap1.getConfig() != this.m_PreferredConfig) {
            paramBitmap = localBitmap1.copy(this.m_PreferredConfig, false);
          }
          this.m_MemoryCache.add(paramTKey, paramBitmap);
        }
        return paramBitmap;
        i = 0;
        break;
      }
      label147:
      if (i == 0) {
        return paramBitmap;
      }
      paramLong -= SystemClock.elapsedRealtime() - l;
      l = paramLong;
      if (paramLong <= 0L) {
        return paramBitmap;
      }
    }
    if (this.m_DiskCache != null) {}
    for (localBitmap1 = (Bitmap)this.m_DiskCache.get(paramTKey, paramBitmap, l);; localBitmap1 = paramBitmap)
    {
      Bitmap localBitmap2 = localBitmap1;
      if (localBitmap1 != paramBitmap)
      {
        if (this.m_CompressedMemoryCache != null) {
          this.m_CompressedMemoryCache.add(paramTKey, localBitmap1);
        }
        localBitmap2 = localBitmap1;
        if (this.m_MemoryCache != null)
        {
          paramBitmap = localBitmap1;
          if (localBitmap1.getConfig() != this.m_PreferredConfig) {
            paramBitmap = localBitmap1.copy(this.m_PreferredConfig, false);
          }
          this.m_MemoryCache.add(paramTKey, paramBitmap);
          localBitmap2 = paramBitmap;
        }
      }
      return localBitmap2;
    }
  }
  
  public Bitmap peek(TKey paramTKey)
  {
    Bitmap localBitmap = null;
    if (this.m_MemoryCache != null) {
      localBitmap = (Bitmap)this.m_MemoryCache.peek(paramTKey);
    }
    return localBitmap;
  }
  
  public void remove(Cache.RemovingPredication<TKey> paramRemovingPredication)
  {
    if (this.m_MemoryCache != null) {
      this.m_MemoryCache.remove(paramRemovingPredication);
    }
    if (this.m_CompressedMemoryCache != null) {
      this.m_CompressedMemoryCache.remove(paramRemovingPredication);
    }
    if (this.m_DiskCache != null) {
      this.m_DiskCache.remove(paramRemovingPredication);
    }
  }
  
  public boolean remove(TKey paramTKey)
  {
    boolean bool3 = false;
    boolean bool1;
    if (this.m_MemoryCache != null)
    {
      bool1 = this.m_MemoryCache.remove(paramTKey);
      if (this.m_CompressedMemoryCache == null) {
        break label64;
      }
    }
    label64:
    for (boolean bool2 = this.m_CompressedMemoryCache.remove(paramTKey);; bool2 = false)
    {
      if (this.m_DiskCache != null) {
        bool3 = this.m_DiskCache.remove(paramTKey);
      }
      return bool1 | bool2 | bool3;
      bool1 = false;
      break;
    }
  }
  
  public void setCapacity(Long paramLong1, Long paramLong2)
  {
    setCapacity(paramLong1, null, paramLong2);
  }
  
  public void setCapacity(Long paramLong1, Long paramLong2, Long paramLong3)
  {
    if ((paramLong1 != null) && (this.m_MemoryCache != null)) {
      this.m_MemoryCache.setCapacity(paramLong1.longValue());
    }
    if ((paramLong2 != null) && (this.m_CompressedMemoryCache != null)) {
      this.m_CompressedMemoryCache.setCapacity(paramLong2.longValue());
    }
    if ((paramLong3 != null) && (this.m_DiskCache != null)) {
      this.m_DiskCache.setCapacity(paramLong3.longValue());
    }
  }
  
  public void trim(Long paramLong1, Long paramLong2)
  {
    trim(paramLong1, null, paramLong2);
  }
  
  public void trim(Long paramLong1, Long paramLong2, Long paramLong3)
  {
    if ((paramLong1 != null) && (this.m_MemoryCache != null)) {
      this.m_MemoryCache.trim(paramLong1.longValue());
    }
    if ((paramLong2 != null) && (this.m_CompressedMemoryCache != null)) {
      this.m_CompressedMemoryCache.trim(paramLong2.longValue());
    }
    if ((paramLong3 != null) && (this.m_DiskCache != null)) {
      this.m_DiskCache.trim(paramLong3.longValue());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/cache/HybridBitmapLruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */