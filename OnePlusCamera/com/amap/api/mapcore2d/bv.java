package com.amap.api.mapcore2d;

class bv
  extends d
{
  private u c;
  private u d;
  private int e;
  private int f;
  private int g;
  private int h;
  private int i;
  private int j;
  private int k;
  private bw l;
  
  public bv(int paramInt1, int paramInt2, u paramu1, u paramu2, int paramInt3, bw parambw)
  {
    super(paramInt1, paramInt2);
    this.c = paramu1;
    this.d = paramu2;
    this.e = ((int)this.c.e());
    this.f = ((int)this.c.f());
    this.l = parambw;
    this.i = ((int)Math.abs(paramu2.e() - this.c.e()));
    this.j = ((int)Math.abs(paramu2.f() - this.c.f()));
    a(paramInt3);
  }
  
  private int a(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 <= paramInt1)
    {
      paramInt1 -= paramInt3;
      if (paramInt1 > paramInt2) {
        return paramInt1;
      }
    }
    else
    {
      paramInt1 += paramInt3;
      if (paramInt1 < paramInt2) {
        return paramInt1;
      }
      this.k = 0;
      return paramInt2;
    }
    this.k = 0;
    return paramInt2;
  }
  
  private void a(int paramInt)
  {
    paramInt = paramInt / 10 / 10;
    if (paramInt >= 2) {}
    for (;;)
    {
      this.g = (this.i / paramInt);
      this.h = (this.j / paramInt);
      return;
      paramInt = 2;
    }
  }
  
  protected void a()
  {
    int m = (int)this.d.e();
    int n = (int)this.d.f();
    if (f())
    {
      this.k += 1;
      this.e = a(this.e, m, this.g);
      this.f = a(this.f, n, this.h);
      this.l.a(new u(this.f, this.e, false));
      if (this.e == m) {
        break label143;
      }
    }
    label143:
    while (this.f != n)
    {
      return;
      this.e = m;
      this.f = n;
      this.l.a(new u(this.f, this.e, false));
      return;
    }
    a(false);
    b(true);
    g();
  }
  
  protected void b()
  {
    this.l.c();
    m.a().b();
  }
  
  protected void c()
  {
    this.l.c();
    k.a().b();
  }
  
  protected void g()
  {
    bj.a().b();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */