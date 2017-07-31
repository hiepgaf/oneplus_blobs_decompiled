package com.oneplus.cache;

public abstract interface MemoryCache<TKey, TValue>
  extends Cache<TKey, TValue>
{
  public abstract void addCallback(Callback<TKey, TValue> paramCallback);
  
  public abstract TValue peek(TKey paramTKey);
  
  public abstract void removeCallback(Callback<TKey, TValue> paramCallback);
  
  public static abstract interface Callback<TKey, TValue>
  {
    public abstract void onEntryAdded(MemoryCache<TKey, TValue> paramMemoryCache, TKey paramTKey, TValue paramTValue);
    
    public abstract void onEntryRemoved(MemoryCache<TKey, TValue> paramMemoryCache, TKey paramTKey, TValue paramTValue);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/cache/MemoryCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */