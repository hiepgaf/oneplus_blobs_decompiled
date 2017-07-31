package com.amap.api.mapcore2d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

class bl<T>
{
  protected LinkedList<T> a = new LinkedList();
  protected final Semaphore b = new Semaphore(0, false);
  protected boolean c = true;
  
  public ArrayList<T> a(int paramInt, boolean paramBoolean)
  {
    if (this.a != null) {
      try
      {
        this.b.acquire();
        try
        {
          if (!this.c) {
            return null;
          }
          ArrayList localArrayList = b(paramInt, paramBoolean);
          return localArrayList;
        }
        catch (Throwable localThrowable)
        {
          return null;
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;) {}
      }
    }
    return null;
  }
  
  public void a()
  {
    this.c = false;
    this.b.release(100);
  }
  
  public void a(List<T> paramList, boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        if (this.a != null)
        {
          if (paramBoolean != true)
          {
            break label54;
            b();
          }
        }
        else {
          return;
        }
        this.a.clear();
      }
      finally {}
      label54:
      do
      {
        this.a.addAll(paramList);
        break;
      } while (paramList != null);
    }
  }
  
  /* Error */
  protected ArrayList<T> b(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_3
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 21	com/amap/api/mapcore2d/bl:a	Ljava/util/LinkedList;
    //   8: ifnull +42 -> 50
    //   11: aload_0
    //   12: getfield 21	com/amap/api/mapcore2d/bl:a	Ljava/util/LinkedList;
    //   15: invokevirtual 62	java/util/LinkedList:size	()I
    //   18: istore 4
    //   20: iload_1
    //   21: iload 4
    //   23: if_icmpgt +31 -> 54
    //   26: new 64	java/util/ArrayList
    //   29: dup
    //   30: iload_1
    //   31: invokespecial 66	java/util/ArrayList:<init>	(I)V
    //   34: astore 5
    //   36: iload_3
    //   37: iload_1
    //   38: if_icmplt +22 -> 60
    //   41: aload_0
    //   42: invokevirtual 50	com/amap/api/mapcore2d/bl:b	()V
    //   45: aload_0
    //   46: monitorexit
    //   47: aload 5
    //   49: areturn
    //   50: aload_0
    //   51: monitorexit
    //   52: aconst_null
    //   53: areturn
    //   54: iload 4
    //   56: istore_1
    //   57: goto -31 -> 26
    //   60: aload 5
    //   62: aload_0
    //   63: getfield 21	com/amap/api/mapcore2d/bl:a	Ljava/util/LinkedList;
    //   66: iconst_0
    //   67: invokevirtual 70	java/util/LinkedList:get	(I)Ljava/lang/Object;
    //   70: invokevirtual 74	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   73: pop
    //   74: aload_0
    //   75: getfield 21	com/amap/api/mapcore2d/bl:a	Ljava/util/LinkedList;
    //   78: invokevirtual 78	java/util/LinkedList:removeFirst	()Ljava/lang/Object;
    //   81: pop
    //   82: iload_3
    //   83: iconst_1
    //   84: iadd
    //   85: istore_3
    //   86: goto -50 -> 36
    //   89: astore 5
    //   91: aload_0
    //   92: monitorexit
    //   93: aload 5
    //   95: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	96	0	this	bl
    //   0	96	1	paramInt	int
    //   0	96	2	paramBoolean	boolean
    //   1	85	3	i	int
    //   18	37	4	j	int
    //   34	27	5	localArrayList	ArrayList
    //   89	5	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	20	89	finally
    //   26	36	89	finally
    //   41	45	89	finally
    //   60	82	89	finally
  }
  
  protected void b()
  {
    if (this.a != null) {
      if (this.c) {
        break label16;
      }
    }
    label16:
    while (this.a.size() == 0)
    {
      return;
      return;
    }
    this.b.release();
  }
  
  public void c()
  {
    if (this.a != null)
    {
      this.a.clear();
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */