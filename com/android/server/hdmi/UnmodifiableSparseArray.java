package com.android.server.hdmi;

import android.util.SparseArray;

final class UnmodifiableSparseArray<E>
{
  private static final String TAG = "ImmutableSparseArray";
  private final SparseArray<E> mArray;
  
  public UnmodifiableSparseArray(SparseArray<E> paramSparseArray)
  {
    this.mArray = paramSparseArray;
  }
  
  public E get(int paramInt)
  {
    return (E)this.mArray.get(paramInt);
  }
  
  public E get(int paramInt, E paramE)
  {
    return (E)this.mArray.get(paramInt, paramE);
  }
  
  public int indexOfValue(E paramE)
  {
    return this.mArray.indexOfValue(paramE);
  }
  
  public int keyAt(int paramInt)
  {
    return this.mArray.keyAt(paramInt);
  }
  
  public int size()
  {
    return this.mArray.size();
  }
  
  public String toString()
  {
    return this.mArray.toString();
  }
  
  public E valueAt(int paramInt)
  {
    return (E)this.mArray.valueAt(paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/UnmodifiableSparseArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */