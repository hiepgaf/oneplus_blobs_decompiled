package com.android.server.hdmi;

import android.util.SparseIntArray;

final class UnmodifiableSparseIntArray
{
  private static final String TAG = "ImmutableSparseIntArray";
  private final SparseIntArray mArray;
  
  public UnmodifiableSparseIntArray(SparseIntArray paramSparseIntArray)
  {
    this.mArray = paramSparseIntArray;
  }
  
  public int get(int paramInt)
  {
    return this.mArray.get(paramInt);
  }
  
  public int get(int paramInt1, int paramInt2)
  {
    return this.mArray.get(paramInt1, paramInt2);
  }
  
  public int indexOfValue(int paramInt)
  {
    return this.mArray.indexOfValue(paramInt);
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
  
  public int valueAt(int paramInt)
  {
    return this.mArray.valueAt(paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/UnmodifiableSparseIntArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */