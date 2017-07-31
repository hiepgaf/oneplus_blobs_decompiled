package com.amap.api.mapcore2d;

import java.util.ArrayList;

class bj
{
  private static bj a = new bj();
  private ArrayList<a> b = new ArrayList();
  
  public static bj a()
  {
    return a;
  }
  
  public void a(a parama)
  {
    if (parama == null) {}
    for (;;)
    {
      return;
      try
      {
        this.b.remove(parama);
      }
      finally {}
    }
  }
  
  /* Error */
  public void b()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 25	com/amap/api/mapcore2d/bj:b	Ljava/util/ArrayList;
    //   6: invokevirtual 35	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   9: astore_2
    //   10: aload_2
    //   11: invokeinterface 41 1 0
    //   16: istore_1
    //   17: iload_1
    //   18: ifne +6 -> 24
    //   21: aload_0
    //   22: monitorexit
    //   23: return
    //   24: aload_2
    //   25: invokeinterface 45 1 0
    //   30: checkcast 6	com/amap/api/mapcore2d/bj$a
    //   33: astore_3
    //   34: aload_3
    //   35: ifnull -25 -> 10
    //   38: aload_3
    //   39: invokeinterface 48 1 0
    //   44: goto -34 -> 10
    //   47: astore_2
    //   48: aload_0
    //   49: monitorexit
    //   50: aload_2
    //   51: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	52	0	this	bj
    //   16	2	1	bool	boolean
    //   9	16	2	localIterator	java.util.Iterator
    //   47	4	2	localObject	Object
    //   33	6	3	locala	a
    // Exception table:
    //   from	to	target	type
    //   2	10	47	finally
    //   10	17	47	finally
    //   24	34	47	finally
    //   38	44	47	finally
  }
  
  public static abstract interface a
  {
    public abstract void P();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */