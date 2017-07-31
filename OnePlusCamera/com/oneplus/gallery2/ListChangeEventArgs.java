package com.oneplus.gallery2;

import com.oneplus.base.EventArgs;
import com.oneplus.base.RecyclableObject;
import java.util.ArrayDeque;
import java.util.Queue;

public class ListChangeEventArgs
  extends EventArgs
  implements RecyclableObject
{
  private static final Queue<ListChangeEventArgs> POOL = new ArrayDeque(16);
  private static final int POOL_SIZE = 16;
  private volatile int m_EndIndex;
  private volatile boolean m_IsFreeInstance;
  private volatile int m_StartIndex;
  
  protected ListChangeEventArgs(int paramInt1, int paramInt2)
  {
    this.m_StartIndex = paramInt1;
    this.m_EndIndex = paramInt2;
  }
  
  public static ListChangeEventArgs obtain(int paramInt)
  {
    return obtain(paramInt, paramInt);
  }
  
  /* Error */
  public static ListChangeEventArgs obtain(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 26	com/oneplus/gallery2/ListChangeEventArgs:POOL	Ljava/util/Queue;
    //   6: invokeinterface 45 1 0
    //   11: checkcast 2	com/oneplus/gallery2/ListChangeEventArgs
    //   14: astore_2
    //   15: aload_2
    //   16: ifnonnull +18 -> 34
    //   19: new 2	com/oneplus/gallery2/ListChangeEventArgs
    //   22: dup
    //   23: iload_0
    //   24: iload_1
    //   25: invokespecial 47	com/oneplus/gallery2/ListChangeEventArgs:<init>	(II)V
    //   28: astore_2
    //   29: ldc 2
    //   31: monitorexit
    //   32: aload_2
    //   33: areturn
    //   34: aload_2
    //   35: iload_0
    //   36: putfield 32	com/oneplus/gallery2/ListChangeEventArgs:m_StartIndex	I
    //   39: aload_2
    //   40: iload_1
    //   41: putfield 34	com/oneplus/gallery2/ListChangeEventArgs:m_EndIndex	I
    //   44: aload_2
    //   45: iconst_0
    //   46: putfield 49	com/oneplus/gallery2/ListChangeEventArgs:m_IsFreeInstance	Z
    //   49: goto -20 -> 29
    //   52: astore_2
    //   53: ldc 2
    //   55: monitorexit
    //   56: aload_2
    //   57: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	58	0	paramInt1	int
    //   0	58	1	paramInt2	int
    //   14	31	2	localListChangeEventArgs	ListChangeEventArgs
    //   52	5	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   3	15	52	finally
    //   19	29	52	finally
    //   34	49	52	finally
  }
  
  public int getEndIndex()
  {
    return this.m_EndIndex;
  }
  
  public int getItemCount()
  {
    return Math.max(0, this.m_EndIndex - this.m_StartIndex + 1);
  }
  
  public int getStartIndex()
  {
    return this.m_StartIndex;
  }
  
  /* Error */
  public void recycle()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_0
    //   4: getfield 49	com/oneplus/gallery2/ListChangeEventArgs:m_IsFreeInstance	Z
    //   7: ifne +25 -> 32
    //   10: aload_0
    //   11: iconst_0
    //   12: putfield 49	com/oneplus/gallery2/ListChangeEventArgs:m_IsFreeInstance	Z
    //   15: getstatic 26	com/oneplus/gallery2/ListChangeEventArgs:POOL	Ljava/util/Queue;
    //   18: invokeinterface 63 1 0
    //   23: bipush 16
    //   25: if_icmplt +11 -> 36
    //   28: ldc 2
    //   30: monitorexit
    //   31: return
    //   32: ldc 2
    //   34: monitorexit
    //   35: return
    //   36: getstatic 26	com/oneplus/gallery2/ListChangeEventArgs:POOL	Ljava/util/Queue;
    //   39: aload_0
    //   40: invokeinterface 67 2 0
    //   45: pop
    //   46: goto -18 -> 28
    //   49: astore_1
    //   50: ldc 2
    //   52: monitorexit
    //   53: aload_1
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	ListChangeEventArgs
    //   49	5	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   3	28	49	finally
    //   28	31	49	finally
    //   32	35	49	finally
    //   36	46	49	finally
    //   50	53	49	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/ListChangeEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */