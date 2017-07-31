package com.oneplus.base;

import java.util.ArrayDeque;

public class PropertyChangeEventArgs<TValue>
  extends EventArgs
  implements RecyclableObject
{
  private static final int POOL_CAPACITY = 32;
  private static final ArrayDeque<PropertyChangeEventArgs<?>> m_Pool = new ArrayDeque(32);
  private volatile boolean m_IsAvailable;
  private volatile TValue m_NewValue;
  private volatile TValue m_OldValue;
  
  private PropertyChangeEventArgs(TValue paramTValue1, TValue paramTValue2)
  {
    this.m_OldValue = paramTValue1;
    this.m_NewValue = paramTValue2;
  }
  
  /* Error */
  public static <TValue> PropertyChangeEventArgs<TValue> obtain(TValue paramTValue1, TValue paramTValue2)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 29	com/oneplus/base/PropertyChangeEventArgs:m_Pool	Ljava/util/ArrayDeque;
    //   6: invokevirtual 45	java/util/ArrayDeque:pollLast	()Ljava/lang/Object;
    //   9: checkcast 2	com/oneplus/base/PropertyChangeEventArgs
    //   12: astore_2
    //   13: aload_2
    //   14: ifnull +25 -> 39
    //   17: aload_2
    //   18: aload_0
    //   19: putfield 35	com/oneplus/base/PropertyChangeEventArgs:m_OldValue	Ljava/lang/Object;
    //   22: aload_2
    //   23: aload_1
    //   24: putfield 37	com/oneplus/base/PropertyChangeEventArgs:m_NewValue	Ljava/lang/Object;
    //   27: aload_2
    //   28: iconst_0
    //   29: putfield 47	com/oneplus/base/PropertyChangeEventArgs:m_IsAvailable	Z
    //   32: aload_2
    //   33: astore_0
    //   34: ldc 2
    //   36: monitorexit
    //   37: aload_0
    //   38: areturn
    //   39: new 2	com/oneplus/base/PropertyChangeEventArgs
    //   42: dup
    //   43: aload_0
    //   44: aload_1
    //   45: invokespecial 49	com/oneplus/base/PropertyChangeEventArgs:<init>	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   48: astore_0
    //   49: goto -15 -> 34
    //   52: astore_0
    //   53: ldc 2
    //   55: monitorexit
    //   56: aload_0
    //   57: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	58	0	paramTValue1	TValue
    //   0	58	1	paramTValue2	TValue
    //   12	21	2	localPropertyChangeEventArgs	PropertyChangeEventArgs
    // Exception table:
    //   from	to	target	type
    //   3	13	52	finally
    //   17	32	52	finally
    //   39	49	52	finally
  }
  
  private static void recycle(PropertyChangeEventArgs<?> paramPropertyChangeEventArgs)
  {
    try
    {
      boolean bool = paramPropertyChangeEventArgs.m_IsAvailable;
      if (bool) {
        return;
      }
      if (m_Pool.size() < 32) {
        m_Pool.push(paramPropertyChangeEventArgs);
      }
      paramPropertyChangeEventArgs.m_OldValue = null;
      paramPropertyChangeEventArgs.m_NewValue = null;
      paramPropertyChangeEventArgs.m_IsAvailable = true;
      paramPropertyChangeEventArgs.clearHandledState();
      return;
    }
    finally {}
  }
  
  public final TValue getNewValue()
  {
    if (this.m_IsAvailable) {
      throw new IllegalStateException();
    }
    return (TValue)this.m_NewValue;
  }
  
  public final TValue getOldValue()
  {
    if (this.m_IsAvailable) {
      throw new IllegalStateException();
    }
    return (TValue)this.m_OldValue;
  }
  
  public final void recycle()
  {
    recycle(this);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/PropertyChangeEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */