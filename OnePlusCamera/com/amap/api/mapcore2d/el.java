package com.amap.api.mapcore2d;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class el
{
  private static el a = null;
  private ExecutorService b;
  private ConcurrentHashMap<em, Future<?>> c = new ConcurrentHashMap();
  private em.a d = new em.a()
  {
    public void a(em paramAnonymousem) {}
    
    public void b(em paramAnonymousem)
    {
      el.a(el.this, paramAnonymousem, false);
    }
  };
  
  private el(int paramInt)
  {
    try
    {
      this.b = Executors.newFixedThreadPool(paramInt);
      return;
    }
    catch (Throwable localThrowable)
    {
      db.b(localThrowable, "TPool", "ThreadPool");
      localThrowable.printStackTrace();
    }
  }
  
  /* Error */
  public static el a(int paramInt)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 19	com/amap/api/mapcore2d/el:a	Lcom/amap/api/mapcore2d/el;
    //   6: ifnull +12 -> 18
    //   9: getstatic 19	com/amap/api/mapcore2d/el:a	Lcom/amap/api/mapcore2d/el;
    //   12: astore_1
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_1
    //   17: areturn
    //   18: new 2	com/amap/api/mapcore2d/el
    //   21: dup
    //   22: iload_0
    //   23: invokespecial 59	com/amap/api/mapcore2d/el:<init>	(I)V
    //   26: putstatic 19	com/amap/api/mapcore2d/el:a	Lcom/amap/api/mapcore2d/el;
    //   29: goto -20 -> 9
    //   32: astore_1
    //   33: ldc 2
    //   35: monitorexit
    //   36: aload_1
    //   37: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	38	0	paramInt	int
    //   12	5	1	localel	el
    //   32	5	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   3	9	32	finally
    //   9	13	32	finally
    //   18	29	32	finally
  }
  
  private void a(em paramem, boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        paramem = (Future)this.c.remove(paramem);
        if (paramBoolean) {
          continue;
        }
      }
      catch (Throwable paramem)
      {
        db.b(paramem, "TPool", "removeQueue");
        paramem.printStackTrace();
        continue;
      }
      finally {}
      return;
      if (paramem != null) {
        paramem.cancel(true);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/el.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */