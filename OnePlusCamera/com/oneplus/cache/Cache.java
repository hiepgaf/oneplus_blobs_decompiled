package com.oneplus.cache;

import com.oneplus.base.Ref;

public abstract interface Cache<TKey, TValue>
{
  public abstract boolean add(TKey paramTKey, TValue paramTValue);
  
  public abstract void clear();
  
  public abstract void close();
  
  public abstract TValue get(TKey paramTKey, TValue paramTValue, long paramLong);
  
  public abstract void remove(RemovingPredication<TKey> paramRemovingPredication);
  
  public abstract boolean remove(TKey paramTKey);
  
  public static abstract interface RemovingPredication<TKey>
  {
    public abstract boolean canRemove(TKey paramTKey, Ref<Boolean> paramRef);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/cache/Cache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */