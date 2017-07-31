package com.oneplus.cache;

import java.util.ArrayList;
import java.util.List;

public abstract class MemoryLruCache<TKey, TValue>
  extends LruCache<TKey, TValue>
  implements MemoryCache<TKey, TValue>
{
  private final List<MemoryCache.Callback<TKey, TValue>> m_Callbacks = new ArrayList();
  
  public MemoryLruCache(long paramLong)
  {
    super(paramLong);
  }
  
  public void addCallback(MemoryCache.Callback<TKey, TValue> paramCallback)
  {
    synchronized (this.syncLock)
    {
      this.m_Callbacks.add(paramCallback);
      return;
    }
  }
  
  protected Object addEntry(TKey paramTKey, TValue paramTValue)
  {
    int i = this.m_Callbacks.size() - 1;
    while (i >= 0)
    {
      ((MemoryCache.Callback)this.m_Callbacks.get(i)).onEntryAdded(this, paramTKey, paramTValue);
      i -= 1;
    }
    return paramTValue;
  }
  
  protected TValue get(TKey paramTKey, Object paramObject, TValue paramTValue, long paramLong)
  {
    return (TValue)paramObject;
  }
  
  public TValue peek(TKey paramTKey)
  {
    return (TValue)super.peek(paramTKey);
  }
  
  public void removeCallback(MemoryCache.Callback<TKey, TValue> paramCallback)
  {
    synchronized (this.syncLock)
    {
      this.m_Callbacks.remove(paramCallback);
      return;
    }
  }
  
  protected void removeEntry(TKey paramTKey, Object paramObject)
  {
    int i = this.m_Callbacks.size() - 1;
    while (i >= 0)
    {
      ((MemoryCache.Callback)this.m_Callbacks.get(i)).onEntryRemoved(this, paramTKey, paramObject);
      i -= 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/cache/MemoryLruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */