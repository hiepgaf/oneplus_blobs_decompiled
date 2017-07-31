package com.amap.api.mapcore2d;

import android.os.Handler;
import android.os.Looper;

abstract class d
{
  protected int a;
  protected int b;
  private Handler c = null;
  private int d = 0;
  private boolean e = false;
  private boolean f = true;
  private Runnable g = new Runnable()
  {
    public void run()
    {
      int i = 0;
      d.a(d.this);
      long l1;
      long l2;
      if (d.this.f())
      {
        l1 = System.currentTimeMillis();
        d.this.a();
        d.d(d.this);
        l2 = System.currentTimeMillis();
        if (l2 - l1 >= d.this.b) {
          i = 1;
        }
        if (i != 0) {}
      }
      try
      {
        Thread.sleep(d.this.b - (l2 - l1));
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        cj.a(localInterruptedException, "AnimBase", "run");
      }
      d.b(d.this).removeCallbacks(this);
      d.a(d.this, null);
      if (!d.c(d.this))
      {
        d.this.b();
        return;
      }
      d.this.c();
      return;
    }
  };
  
  public d(int paramInt1, int paramInt2)
  {
    this.a = paramInt1;
    this.b = paramInt2;
  }
  
  private void g()
  {
    this.e = false;
  }
  
  private void h()
  {
    this.d += this.b;
    if (this.a == -1) {}
    while (this.d <= this.a) {
      return;
    }
    g();
    b(true);
  }
  
  private void i()
  {
    if (this.c == null) {
      return;
    }
    this.c.post(this.g);
  }
  
  protected abstract void a();
  
  public void a(boolean paramBoolean)
  {
    this.e = paramBoolean;
  }
  
  protected abstract void b();
  
  public void b(boolean paramBoolean)
  {
    this.f = paramBoolean;
  }
  
  protected abstract void c();
  
  public void d()
  {
    if (f()) {}
    for (;;)
    {
      i();
      return;
      this.c = new Handler(Looper.getMainLooper());
      this.e = true;
      this.f = false;
      this.d = 0;
    }
  }
  
  public void e()
  {
    m.a().b();
    g();
    this.g.run();
  }
  
  public boolean f()
  {
    return this.e;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/d.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */