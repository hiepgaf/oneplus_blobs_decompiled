package com.amap.api.mapcore2d;

public abstract class em
  implements Runnable
{
  a d;
  
  public abstract void a();
  
  public final void run()
  {
    try
    {
      if (this.d == null) {}
      while (!Thread.interrupted())
      {
        a();
        if (Thread.interrupted()) {
          break label59;
        }
        if (this.d != null) {
          break label60;
        }
        return;
        this.d.a(this);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      db.b(localThrowable, "ThreadTask", "run");
      localThrowable.printStackTrace();
      return;
    }
    label59:
    return;
    label60:
    this.d.b(this);
  }
  
  static abstract interface a
  {
    public abstract void a(em paramem);
    
    public abstract void b(em paramem);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/em.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */