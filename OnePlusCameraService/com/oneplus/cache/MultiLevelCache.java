package com.oneplus.cache;

import android.os.SystemClock;

public class MultiLevelCache<TKey, TValue>
  implements Cache<TKey, TValue>
{
  private final Cache<TKey, TValue>[] m_Caches;
  private final MemoryCache.Callback<TKey, TValue> m_MemoryCacheCallback = new MemoryCache.Callback()
  {
    public void onEntryAdded(MemoryCache<TKey, TValue> paramAnonymousMemoryCache, TKey paramAnonymousTKey, TValue paramAnonymousTValue) {}
    
    public void onEntryRemoved(MemoryCache<TKey, TValue> paramAnonymousMemoryCache, TKey paramAnonymousTKey, TValue paramAnonymousTValue)
    {
      int i = MultiLevelCache.-get0(MultiLevelCache.this).length - 1;
      for (;;)
      {
        if (i >= 0)
        {
          if ((MultiLevelCache.-get0(MultiLevelCache.this)[i] == paramAnonymousMemoryCache) && (i < MultiLevelCache.-get0(MultiLevelCache.this).length - 1)) {
            MultiLevelCache.-get0(MultiLevelCache.this)[(i + 1)].add(paramAnonymousTKey, paramAnonymousTValue);
          }
        }
        else {
          return;
        }
        i -= 1;
      }
    }
  };
  
  @SafeVarargs
  public MultiLevelCache(Cache<TKey, TValue>... paramVarArgs)
  {
    if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
      throw new IllegalArgumentException("No internal caches.");
    }
    this.m_Caches = paramVarArgs;
    int i = paramVarArgs.length - 1;
    while (i >= 0)
    {
      Cache<TKey, TValue> localCache = paramVarArgs[i];
      if ((localCache instanceof MemoryCache)) {
        ((MemoryCache)localCache).addCallback(this.m_MemoryCacheCallback);
      }
      i -= 1;
    }
  }
  
  public boolean add(TKey paramTKey, TValue paramTValue)
  {
    return this.m_Caches[0].add(paramTKey, paramTValue);
  }
  
  public void clear()
  {
    int i = this.m_Caches.length - 1;
    while (i >= 0)
    {
      this.m_Caches[i].clear();
      i -= 1;
    }
  }
  
  public void close()
  {
    int i = this.m_Caches.length - 1;
    while (i >= 0)
    {
      Cache localCache = this.m_Caches[i];
      if ((localCache instanceof MemoryCache)) {
        ((MemoryCache)localCache).removeCallback(this.m_MemoryCacheCallback);
      }
      localCache.close();
      i -= 1;
    }
  }
  
  public TValue get(TKey paramTKey, TValue paramTValue, long paramLong)
  {
    long l1 = SystemClock.elapsedRealtime();
    int i = 0;
    while (i < this.m_Caches.length)
    {
      Object localObject = this.m_Caches[i].get(paramTKey, paramTValue, paramLong);
      long l2;
      long l3;
      if (((localObject == null) || (localObject.equals(paramTValue))) && ((paramTValue == null) || (paramTValue.equals(localObject))))
      {
        l2 = l1;
        l3 = paramLong;
        if (paramLong >= 0L)
        {
          l2 = SystemClock.elapsedRealtime();
          paramLong -= l2 - l1;
          l3 = paramLong;
          if (paramLong <= 0L) {
            return paramTValue;
          }
        }
      }
      else
      {
        if (i != 0) {
          this.m_Caches[0].add(paramTKey, localObject);
        }
        return (TValue)localObject;
      }
      i += 1;
      l1 = l2;
      paramLong = l3;
    }
    return paramTValue;
  }
  
  public void remove(Cache.RemovingPredication<TKey> paramRemovingPredication)
  {
    int i = this.m_Caches.length - 1;
    while (i >= 0)
    {
      this.m_Caches[i].remove(paramRemovingPredication);
      i -= 1;
    }
  }
  
  public boolean remove(TKey paramTKey)
  {
    boolean bool = false;
    int i = this.m_Caches.length - 1;
    while (i >= 0)
    {
      bool |= this.m_Caches[i].remove(paramTKey);
      i -= 1;
    }
    return bool;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/cache/MultiLevelCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */