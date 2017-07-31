package com.oneplus.base;

import android.util.LongSparseArray;
import java.util.Map;

public final class BitFlagsGroup
{
  private volatile long m_CurrentFlag;
  private final String m_Name;
  private final LongSparseArray<Map<BitFlagsGroup, Long>> m_Relations = new LongSparseArray();
  
  public BitFlagsGroup(Class<?> paramClass)
  {
    this(paramClass.getSimpleName());
  }
  
  public BitFlagsGroup(String paramString)
  {
    this.m_Name = paramString;
  }
  
  public int convertFlags(int paramInt, BitFlagsGroup paramBitFlagsGroup)
  {
    int i = 1;
    long l1 = 0L;
    int j = 32;
    for (;;)
    {
      long l2;
      if (j > 0)
      {
        l2 = l1;
        if ((paramInt & i) == 0) {}
      }
      try
      {
        Object localObject = (Map)this.m_Relations.get(i);
        l2 = l1;
        if (localObject != null)
        {
          localObject = (Long)((Map)localObject).get(paramBitFlagsGroup);
          l2 = l1;
          if (localObject != null)
          {
            l2 = ((Long)localObject).longValue();
            l2 = l1 | l2;
          }
        }
        j -= 1;
        i <<= 1;
        l1 = l2;
      }
      finally {}
    }
    paramInt = (int)l1;
    return paramInt;
  }
  
  public long convertFlags(long paramLong, BitFlagsGroup paramBitFlagsGroup)
  {
    long l1 = 1L;
    long l2 = 0L;
    int i = 64;
    for (;;)
    {
      long l3;
      if (i > 0)
      {
        l3 = l2;
        if ((paramLong & l1) == 0L) {}
      }
      try
      {
        Object localObject = (Map)this.m_Relations.get(l1);
        l3 = l2;
        if (localObject != null)
        {
          localObject = (Long)((Map)localObject).get(paramBitFlagsGroup);
          l3 = l2;
          if (localObject != null)
          {
            l3 = ((Long)localObject).longValue();
            l3 = l2 | l3;
          }
        }
        i -= 1;
        l1 <<= 1;
        l2 = l3;
      }
      finally {}
    }
    return l2;
  }
  
  /* Error */
  public void createRelation(long paramLong1, BitFlagsGroup paramBitFlagsGroup, long paramLong2)
  {
    // Byte code:
    //   0: aload_3
    //   1: aload_0
    //   2: if_acmpne +13 -> 15
    //   5: new 57	java/lang/IllegalArgumentException
    //   8: dup
    //   9: ldc 59
    //   11: invokespecial 60	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: aload_0
    //   16: monitorenter
    //   17: aload_0
    //   18: getfield 33	com/oneplus/base/BitFlagsGroup:m_Relations	Landroid/util/LongSparseArray;
    //   21: lload_1
    //   22: invokevirtual 41	android/util/LongSparseArray:get	(J)Ljava/lang/Object;
    //   25: checkcast 43	java/util/Map
    //   28: astore 7
    //   30: aload 7
    //   32: astore 6
    //   34: aload 7
    //   36: ifnonnull +22 -> 58
    //   39: new 62	java/util/HashMap
    //   42: dup
    //   43: invokespecial 63	java/util/HashMap:<init>	()V
    //   46: astore 6
    //   48: aload_0
    //   49: getfield 33	com/oneplus/base/BitFlagsGroup:m_Relations	Landroid/util/LongSparseArray;
    //   52: lload_1
    //   53: aload 6
    //   55: invokevirtual 67	android/util/LongSparseArray:put	(JLjava/lang/Object;)V
    //   58: aload 6
    //   60: aload_3
    //   61: lload 4
    //   63: invokestatic 71	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   66: invokeinterface 74 3 0
    //   71: pop
    //   72: aload_0
    //   73: monitorexit
    //   74: aload_3
    //   75: monitorenter
    //   76: aload_3
    //   77: getfield 33	com/oneplus/base/BitFlagsGroup:m_Relations	Landroid/util/LongSparseArray;
    //   80: lload 4
    //   82: invokevirtual 41	android/util/LongSparseArray:get	(J)Ljava/lang/Object;
    //   85: checkcast 43	java/util/Map
    //   88: astore 7
    //   90: aload 7
    //   92: astore 6
    //   94: aload 7
    //   96: ifnonnull +23 -> 119
    //   99: new 62	java/util/HashMap
    //   102: dup
    //   103: invokespecial 63	java/util/HashMap:<init>	()V
    //   106: astore 6
    //   108: aload_3
    //   109: getfield 33	com/oneplus/base/BitFlagsGroup:m_Relations	Landroid/util/LongSparseArray;
    //   112: lload 4
    //   114: aload 6
    //   116: invokevirtual 67	android/util/LongSparseArray:put	(JLjava/lang/Object;)V
    //   119: aload 6
    //   121: aload_0
    //   122: lload_1
    //   123: invokestatic 71	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   126: invokeinterface 74 3 0
    //   131: pop
    //   132: aload_3
    //   133: monitorexit
    //   134: return
    //   135: astore_3
    //   136: aload_0
    //   137: monitorexit
    //   138: aload_3
    //   139: athrow
    //   140: astore 6
    //   142: aload_3
    //   143: monitorexit
    //   144: aload 6
    //   146: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	147	0	this	BitFlagsGroup
    //   0	147	1	paramLong1	long
    //   0	147	3	paramBitFlagsGroup	BitFlagsGroup
    //   0	147	4	paramLong2	long
    //   32	88	6	localObject1	Object
    //   140	5	6	localObject2	Object
    //   28	67	7	localMap	Map
    // Exception table:
    //   from	to	target	type
    //   17	30	135	finally
    //   39	58	135	finally
    //   58	72	135	finally
    //   76	90	140	finally
    //   99	119	140	finally
    //   119	132	140	finally
  }
  
  public int nextIntFlag()
  {
    try
    {
      if (this.m_CurrentFlag >= 2147483648L) {
        throw new RuntimeException("No more flag to use");
      }
    }
    finally {}
    if (this.m_CurrentFlag == 0L) {}
    for (this.m_CurrentFlag = 1L;; this.m_CurrentFlag <<= 1)
    {
      long l = this.m_CurrentFlag;
      int i = (int)l;
      return i;
    }
  }
  
  public long nextLongFlag()
  {
    try
    {
      if (this.m_CurrentFlag >= 4611686018427387904L) {
        throw new RuntimeException("No more flag to use");
      }
    }
    finally {}
    if (this.m_CurrentFlag == 0L) {}
    for (this.m_CurrentFlag = 1L;; this.m_CurrentFlag <<= 1)
    {
      long l = this.m_CurrentFlag;
      return l;
    }
  }
  
  public String toString()
  {
    return this.m_Name;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/BitFlagsGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */