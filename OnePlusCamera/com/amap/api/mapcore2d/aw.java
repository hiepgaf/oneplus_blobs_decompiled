package com.amap.api.mapcore2d;

import java.util.Random;

class aw
{
  private static aw b;
  private String a = "http://tm.amap.com";
  
  /* Error */
  public static aw a()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 20	com/amap/api/mapcore2d/aw:b	Lcom/amap/api/mapcore2d/aw;
    //   6: ifnull +12 -> 18
    //   9: getstatic 20	com/amap/api/mapcore2d/aw:b	Lcom/amap/api/mapcore2d/aw;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: new 2	com/amap/api/mapcore2d/aw
    //   21: dup
    //   22: invokespecial 21	com/amap/api/mapcore2d/aw:<init>	()V
    //   25: putstatic 20	com/amap/api/mapcore2d/aw:b	Lcom/amap/api/mapcore2d/aw;
    //   28: goto -19 -> 9
    //   31: astore_0
    //   32: ldc 2
    //   34: monitorexit
    //   35: aload_0
    //   36: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   12	5	0	localaw	aw
    //   31	5	0	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   3	9	31	finally
    //   9	13	31	finally
    //   18	28	31	finally
  }
  
  public String b()
  {
    return "http://grid.amap.com/grid/%d/%d/%d?dpiType=%s&lang=%s&ds=" + p.i;
  }
  
  public String c()
  {
    return this.a;
  }
  
  public String d()
  {
    int i = new Random(System.currentTimeMillis()).nextInt(100000);
    return String.format("http://mt%d.google.cn/vt/lyrs=m", new Object[] { Integer.valueOf(i % 4 + 1) }) + "@285000000&hl=zh-CN&gl=CN&src=app&expIds=201527&rlbl=1&x=%d&y=%d&z=%d&s=Gali";
  }
  
  public String e()
  {
    return String.format("http://mst0%d.is.autonavi.com", new Object[] { Integer.valueOf(new Random(System.currentTimeMillis()).nextInt(100000) % 4 + 1) });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/aw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */