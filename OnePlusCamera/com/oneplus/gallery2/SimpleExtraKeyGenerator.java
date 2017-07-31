package com.oneplus.gallery2;

import java.util.ArrayDeque;
import java.util.Queue;

public final class SimpleExtraKeyGenerator
  implements ExtraKeyGenerator
{
  private static final int MAX_RECYCLED_KEY_COUNT = 64;
  private volatile long m_NextId = 1L;
  private final Queue<Key<?>> m_RecycledKeys = new ArrayDeque(64);
  
  /* Error */
  private void recycleKey(Key<?> paramKey)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: getfield 41	com/oneplus/gallery2/SimpleExtraKeyGenerator$Key:isRecycled	Z
    //   6: istore_2
    //   7: iload_2
    //   8: ifeq +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_0
    //   15: getfield 30	com/oneplus/gallery2/SimpleExtraKeyGenerator:m_RecycledKeys	Ljava/util/Queue;
    //   18: invokeinterface 47 1 0
    //   23: bipush 64
    //   25: if_icmpge -14 -> 11
    //   28: aload_0
    //   29: getfield 30	com/oneplus/gallery2/SimpleExtraKeyGenerator:m_RecycledKeys	Ljava/util/Queue;
    //   32: aload_1
    //   33: invokeinterface 51 2 0
    //   38: pop
    //   39: aload_1
    //   40: iconst_1
    //   41: putfield 41	com/oneplus/gallery2/SimpleExtraKeyGenerator$Key:isRecycled	Z
    //   44: goto -33 -> 11
    //   47: astore_1
    //   48: aload_0
    //   49: monitorexit
    //   50: aload_1
    //   51: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	52	0	this	SimpleExtraKeyGenerator
    //   0	52	1	paramKey	Key<?>
    //   6	2	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	7	47	finally
    //   14	44	47	finally
  }
  
  public <TValue> ExtraKey<TValue> generateKey(Class<TValue> paramClass)
  {
    try
    {
      paramClass = (Key)this.m_RecycledKeys.poll();
      if (paramClass == null)
      {
        long l = this.m_NextId;
        this.m_NextId = (1L + l);
        paramClass = new Key(l);
        return paramClass;
      }
      paramClass.isRecycled = false;
      return paramClass;
    }
    finally {}
  }
  
  private final class Key<TValue>
    implements ExtraKey<TValue>
  {
    public final long id;
    public volatile boolean isRecycled;
    
    public Key(long paramLong)
    {
      this.id = paramLong;
    }
    
    public long getId()
    {
      return this.id;
    }
    
    public void recycle()
    {
      SimpleExtraKeyGenerator.this.recycleKey(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/SimpleExtraKeyGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */