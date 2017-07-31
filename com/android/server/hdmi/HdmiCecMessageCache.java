package com.android.server.hdmi;

import android.util.FastImmutableArraySet;
import android.util.SparseArray;

final class HdmiCecMessageCache
{
  private static final FastImmutableArraySet<Integer> CACHEABLE_OPCODES = new FastImmutableArraySet(new Integer[] { Integer.valueOf(71), Integer.valueOf(132), Integer.valueOf(135), Integer.valueOf(158) });
  private final SparseArray<SparseArray<HdmiCecMessage>> mCache = new SparseArray();
  
  private boolean isCacheable(int paramInt)
  {
    return CACHEABLE_OPCODES.contains(Integer.valueOf(paramInt));
  }
  
  public void cacheMessage(HdmiCecMessage paramHdmiCecMessage)
  {
    int i = paramHdmiCecMessage.getOpcode();
    if (!isCacheable(i)) {
      return;
    }
    int j = paramHdmiCecMessage.getSource();
    SparseArray localSparseArray2 = (SparseArray)this.mCache.get(j);
    SparseArray localSparseArray1 = localSparseArray2;
    if (localSparseArray2 == null)
    {
      localSparseArray1 = new SparseArray();
      this.mCache.put(j, localSparseArray1);
    }
    localSparseArray1.put(i, paramHdmiCecMessage);
  }
  
  public void flushAll()
  {
    this.mCache.clear();
  }
  
  public void flushMessagesFrom(int paramInt)
  {
    this.mCache.remove(paramInt);
  }
  
  public HdmiCecMessage getMessage(int paramInt1, int paramInt2)
  {
    SparseArray localSparseArray = (SparseArray)this.mCache.get(paramInt1);
    if (localSparseArray == null) {
      return null;
    }
    return (HdmiCecMessage)localSparseArray.get(paramInt2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiCecMessageCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */