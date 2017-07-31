package com.amap.api.mapcore2d;

import android.graphics.Point;
import android.graphics.PointF;
import java.util.ArrayList;

class au
{
  public int a = 256;
  public int b = 256;
  float c = 1.0F;
  public double d = 156543.0339D;
  int e = 0;
  double f = -2.003750834E7D;
  double g = 2.003750834E7D;
  public int h = p.d;
  public int i = p.c;
  public float j = 10.0F;
  public double k = 0.0D;
  public u l = null;
  public u m = null;
  public Point n = null;
  public a o = null;
  ay.d p = null;
  private double q = 116.39716D;
  private double r = 39.91669D;
  private double s = 0.01745329251994329D;
  
  public au(ay.d paramd)
  {
    this.p = paramd;
  }
  
  public float a(u paramu1, u paramu2)
  {
    double d4 = q.a(paramu1.c());
    double d3 = q.a(paramu1.d());
    double d2 = q.a(paramu2.c());
    double d1 = q.a(paramu2.d());
    double d5 = d4 * this.s;
    double d6 = d3 * this.s;
    d3 = d2 * this.s;
    d4 = d1 * this.s;
    d1 = Math.sin(d5);
    d2 = Math.sin(d6);
    d5 = Math.cos(d5);
    d6 = Math.cos(d6);
    double d7 = Math.sin(d3);
    double d8 = Math.sin(d4);
    d3 = Math.cos(d3);
    d4 = Math.cos(d4);
    paramu1 = new double[3];
    paramu2 = new double[3];
    paramu1[0] = (d5 * d6);
    paramu1[1] = (d6 * d1);
    paramu1[2] = d2;
    paramu2[0] = (d4 * d3);
    paramu2[1] = (d4 * d7);
    paramu2[2] = d8;
    return (float)(Math.asin(Math.sqrt((paramu1[0] - paramu2[0]) * (paramu1[0] - paramu2[0]) + (paramu1[1] - paramu2[1]) * (paramu1[1] - paramu2[1]) + (paramu1[2] - paramu2[2]) * (paramu1[2] - paramu2[2])) / 2.0D) * 1.27420015798544E7D);
  }
  
  public PointF a(int paramInt1, int paramInt2)
  {
    double d1 = 0.0D;
    double d2 = this.a * paramInt1;
    double d3 = this.k;
    double d4 = this.f;
    if (this.e != 0) {
      if (this.e == 1) {
        break label93;
      }
    }
    for (;;)
    {
      return a(new u(d1, d4 + d2 * d3, false), this.l, this.n, this.k);
      d1 = this.g - this.a * paramInt2 * this.k;
      continue;
      label93:
      d1 = (paramInt2 + 1) * this.a;
      d1 = this.k * d1;
    }
  }
  
  PointF a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, PointF paramPointF, int paramInt5, int paramInt6)
  {
    int i1 = 1;
    PointF localPointF = new PointF();
    localPointF.x = ((paramInt1 - paramInt3) * this.a + paramPointF.x);
    if (this.e != 0)
    {
      if (this.e == 1) {
        break label148;
      }
      if (localPointF.x + this.a > 0.0F) {
        break label172;
      }
      paramInt1 = 1;
      label65:
      if (paramInt1 == 0)
      {
        if (localPointF.x < paramInt5) {
          break label177;
        }
        paramInt1 = 1;
        label83:
        if (paramInt1 == 0) {
          if (localPointF.y + this.a > 0.0F) {
            break label182;
          }
        }
      }
    }
    label148:
    label172:
    label177:
    label182:
    for (paramInt1 = i1;; paramInt1 = 0)
    {
      if ((paramInt1 == 0) && (localPointF.y < paramInt6)) {
        break label187;
      }
      return null;
      localPointF.y = ((paramInt2 - paramInt4) * this.a + paramPointF.y);
      break;
      paramPointF.y -= (paramInt2 - paramInt4) * this.a;
      break;
      paramInt1 = 0;
      break label65;
      paramInt1 = 0;
      break label83;
    }
    label187:
    return localPointF;
  }
  
  PointF a(u paramu1, u paramu2, Point paramPoint, double paramDouble)
  {
    PointF localPointF = new PointF();
    localPointF.x = ((float)((paramu1.e() - paramu2.e()) / paramDouble + paramPoint.x));
    localPointF.y = ((float)(paramPoint.y - (paramu1.f() - paramu2.f()) / paramDouble));
    return localPointF;
  }
  
  public u a(PointF paramPointF, u paramu, Point paramPoint, double paramDouble, a parama)
  {
    return b(b(paramPointF, paramu, paramPoint, paramDouble, parama));
  }
  
  public u a(u paramu)
  {
    if (paramu != null)
    {
      double d1 = paramu.b() / 1000000.0D;
      double d2 = paramu.a() / 1000000.0D * 2.003750834E7D / 180.0D;
      return new u(Math.log(Math.tan((d1 + 90.0D) * 3.141592653589793D / 360.0D)) / 0.017453292519943295D * 2.003750834E7D / 180.0D, d2, false);
    }
    return null;
  }
  
  public ArrayList<bp> a(u paramu, int paramInt1, int paramInt2, int paramInt3)
  {
    double d2 = this.k;
    int i4 = (int)((paramu.e() - this.f) / (this.a * d2));
    double d3 = this.a * i4;
    double d4 = this.f;
    double d1 = 0.0D;
    int i1;
    if (this.e != 0)
    {
      if (this.e == 1) {
        break label231;
      }
      i1 = 0;
    }
    PointF localPointF;
    int i2;
    int i3;
    for (;;)
    {
      localPointF = a(new u(d1, d4 + d3 * d2, false), paramu, this.n, d2);
      localObject = new bp(i4, i1, b(), -1);
      ((bp)localObject).g = localPointF;
      paramu = new ArrayList();
      paramu.add(localObject);
      i2 = 1;
      for (;;)
      {
        paramInt1 = 0;
        i3 = i4 - i2;
        if (i3 <= i4 + i2) {
          break;
        }
        i3 = i1 + i2 - 1;
        label166:
        if (i3 > i1 - i2) {
          break label423;
        }
        if (paramInt1 == 0) {
          break label416;
        }
        i2 += 1;
      }
      i1 = (int)((this.g - paramu.f()) / (this.a * d2));
      d1 = this.g - this.a * i1 * d2;
      continue;
      label231:
      i1 = (int)((paramu.f() - this.g) / (this.a * d2));
      d1 = (i1 + 1) * this.a * d2;
    }
    int i5 = i1 + i2;
    label335:
    bp localbp;
    for (;;)
    {
      try
      {
        localObject = a(i3, i5, i4, i1, localPointF, paramInt2, paramInt3);
        if (localObject != null) {
          break label572;
        }
        i5 = i1 - i2;
        localObject = a(i3, i5, i4, i1, localPointF, paramInt2, paramInt3);
        if (localObject != null) {
          break label584;
        }
      }
      catch (Error localError)
      {
        cj.a(localError, "MapProjection", "getTilesInDomain");
      }
      localbp = new bp(i3, i5, b(), -1);
      localbp.g = ((PointF)localObject);
      paramu.add(localbp);
    }
    for (;;)
    {
      label370:
      localbp = new bp(i3, i5, b(), -1);
      localbp.g = ((PointF)localObject);
      paramu.add(localbp);
      break;
      label416:
      return paramu;
      label418:
      paramInt1 = 1;
    }
    label423:
    i5 = i4 + i2;
    Object localObject = a(i5, i3, i4, i1, localError, paramInt2, paramInt3);
    if (localObject == null) {
      for (;;)
      {
        i5 = i4 - i2;
        localObject = a(i5, i3, i4, i1, localError, paramInt2, paramInt3);
        if (localObject != null) {
          break label612;
        }
        break;
        label488:
        localbp = new bp(i5, i3, b(), -1);
        localbp.g = ((PointF)localObject);
        paramu.add(localbp);
      }
    }
    for (;;)
    {
      label523:
      localbp = new bp(i5, i3, b(), -1);
      localbp.g = ((PointF)localObject);
      paramu.add(localbp);
      label572:
      label584:
      label612:
      do
      {
        paramInt1 = 1;
        break label523;
        i3 += 1;
        break;
        if (paramInt1 != 0) {
          break label335;
        }
        paramInt1 = 1;
        break label335;
        if (paramInt1 == 0) {
          break label418;
        }
        break label370;
        i3 -= 1;
        break label166;
        if (paramInt1 != 0) {
          break label488;
        }
        paramInt1 = 1;
        break label488;
      } while (paramInt1 == 0);
    }
  }
  
  public void a()
  {
    this.d = (this.g * 2.0D / this.a);
    int i1 = (int)this.j;
    this.k = (this.d / (1 << i1) / (this.j + 1.0F - i1));
    this.l = a(new u(this.r, this.q, true));
    this.m = this.l.g();
    this.n = new Point(this.p.c() / 2, this.p.d() / 2);
    this.o = new a();
    this.o.a = -2.0037508E7F;
    this.o.b = 2.0037508E7F;
    this.o.c = 2.0037508E7F;
    this.o.d = -2.0037508E7F;
  }
  
  public void a(Point paramPoint)
  {
    this.n = paramPoint;
  }
  
  public void a(PointF paramPointF1, PointF paramPointF2, float paramFloat)
  {
    double d1 = this.k;
    paramPointF1 = b(paramPointF1, this.l, this.n, d1, this.o);
    paramPointF2 = b(paramPointF2, this.l, this.n, d1, this.o);
    d1 = paramPointF2.e();
    double d4 = paramPointF1.e();
    double d2 = paramPointF2.f();
    double d3 = paramPointF1.f();
    d1 = this.l.e() + (d1 - d4);
    d3 = this.l.f() + (d2 - d3);
    for (;;)
    {
      d2 = d1;
      if (d1 >= this.o.a) {
        break;
      }
      d1 += this.o.b - this.o.a;
    }
    for (;;)
    {
      d1 = d3;
      if (d2 <= this.o.b) {
        break;
      }
      d2 -= this.o.b - this.o.a;
    }
    for (;;)
    {
      d3 = d1;
      if (d1 >= this.o.d) {
        break;
      }
      d1 += this.o.c - this.o.d;
    }
    while (d3 > this.o.c) {
      d3 -= this.o.c - this.o.d;
    }
    this.l.b(d3);
    this.l.a(d2);
  }
  
  int b()
  {
    int i1 = (int)this.j;
    if (this.j - i1 < ay.a) {
      return i1;
    }
    return i1 + 1;
  }
  
  public PointF b(u paramu1, u paramu2, Point paramPoint, double paramDouble)
  {
    paramu1 = a(a(paramu1), paramu2, paramPoint, paramDouble);
    return this.p.g().b(paramu1);
  }
  
  u b(PointF paramPointF, u paramu, Point paramPoint, double paramDouble, a parama)
  {
    paramPointF = this.p.g().c(paramPointF);
    float f1 = paramPointF.x;
    float f2 = paramPoint.x;
    float f3 = paramPointF.y;
    float f4 = paramPoint.y;
    double d1 = paramu.e();
    d1 = (f1 - f2) * paramDouble + d1;
    double d2 = paramu.f();
    double d3 = f3 - f4;
    while (d1 < parama.a) {
      d1 += parama.b - parama.a;
    }
    while (d1 > parama.b) {
      d1 -= parama.b - parama.a;
    }
    for (paramDouble = d2 - d3 * paramDouble; paramDouble < parama.d; paramDouble += parama.c - parama.d) {}
    while (paramDouble > parama.c) {
      paramDouble -= parama.c - parama.d;
    }
    return new u(paramDouble, d1, false);
  }
  
  public u b(u paramu)
  {
    float f1 = (float)(paramu.e() * 180.0D / 2.003750834E7D);
    return new u((int)((float)((Math.atan(Math.exp((float)(paramu.f() * 180.0D / 2.003750834E7D) * 3.141592653589793D / 180.0D)) * 2.0D - 1.5707963267948966D) * 57.29577951308232D) * 1000000.0D), (int)(f1 * 1000000.0D));
  }
  
  static class a
  {
    float a;
    float b;
    float c;
    float d;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/au.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */