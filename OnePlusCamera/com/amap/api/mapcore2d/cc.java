package com.amap.api.mapcore2d;

import android.graphics.Matrix;
import android.graphics.Point;
import android.view.animation.Animation.AnimationListener;

class cc
  extends d
{
  public float c = -1.0F;
  public boolean d = false;
  private Animation.AnimationListener e;
  private b f;
  private float g;
  private float h;
  private float i;
  private float j;
  private float k;
  private boolean l;
  private boolean m = false;
  
  public cc(b paramb, Animation.AnimationListener paramAnimationListener)
  {
    super(160, 40);
    this.f = paramb;
    this.e = paramAnimationListener;
  }
  
  protected void a()
  {
    try
    {
      Object localObject;
      if (!this.l) {
        localObject = this.f.a.i;
      }
      for (((au)localObject).c -= this.k;; ((au)localObject).c += this.k)
      {
        localObject = new Matrix();
        ((Matrix)localObject).setScale(this.f.a.i.c, this.f.a.i.c, this.g, this.h);
        this.f.d(this.f.a.i.c);
        this.f.b((Matrix)localObject);
        return;
        localObject = this.f.a.i;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }
  
  public void a(float paramFloat1, float paramFloat2, boolean paramBoolean, float paramFloat3, float paramFloat4)
  {
    this.l = paramBoolean;
    this.g = paramFloat3;
    this.h = paramFloat4;
    this.i = paramFloat1;
    this.f.a.i.c = this.i;
    if (!this.l)
    {
      this.k = (this.i * 0.5F * this.b / this.a);
      this.j = (this.i * 0.5F);
      return;
    }
    this.k = (this.i * this.b / this.a);
    this.j = (this.i * 2.0F);
  }
  
  public void a(float paramFloat1, boolean paramBoolean, float paramFloat2, float paramFloat3)
  {
    this.f.c[0] = this.f.c[1];
    this.f.c[1] = paramFloat1;
    if (this.f.c[0] == this.f.c[1]) {
      return;
    }
    this.f.b().a(this.f.B());
    if (f())
    {
      this.m = true;
      e();
      a(this.j, paramFloat1, paramBoolean, paramFloat2, paramFloat3);
      this.f.b().e.a(true);
      this.f.b().e.b = true;
      this.e.onAnimationStart(null);
      super.d();
      this.m = false;
      return;
    }
    this.a = 160;
    a(this.f.J(), paramFloat1, paramBoolean, paramFloat2, paramFloat3);
    this.f.b().e.a(true);
    this.f.b().e.b = true;
    this.e.onAnimationStart(null);
    super.d();
  }
  
  protected void b()
  {
    if (!this.m)
    {
      for (;;)
      {
        try
        {
          if (this.f == null) {
            return;
          }
          if (this.f.b() == null) {
            break;
          }
          this.f.b().e.b = false;
          if (this.d != true)
          {
            this.f.D().c(this.c);
            this.e.onAnimationEnd(null);
            if (this.d != true)
            {
              this.f.a.i.c = 1.0F;
              ba.j = 1.0F;
              this.f.b().a(true);
              k.a().b();
            }
          }
          else
          {
            Point localPoint1 = new Point((int)this.g, (int)this.h);
            localu = this.f.s().a((int)this.g, (int)this.h);
            this.f.b().i.l = this.f.b().i.a(localu);
            this.f.b().i.a(localPoint1);
            this.f.b().c.a(false, false);
            continue;
          }
          localPoint2 = new Point(this.f.b().c.c() / 2, this.f.b().c.d() / 2);
        }
        catch (Exception localException)
        {
          cj.a(localException, "ZoomCtlAnim", "onStop");
          return;
        }
        Point localPoint2;
        u localu = this.f.s().a(this.f.b().c.c() / 2, this.f.b().c.d() / 2);
        this.f.b().i.l = this.f.b().i.a(localu);
        this.f.b().i.a(localPoint2);
        this.f.b().c.a(false, false);
      }
      return;
    }
  }
  
  protected void c()
  {
    b();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */