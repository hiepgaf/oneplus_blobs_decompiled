package com.amap.api.mapcore2d;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

class j
{
  private static float I;
  private static final float[] J;
  private static final float[] K;
  private static float O;
  private static float P;
  private float A;
  private float B;
  private boolean C = true;
  private Interpolator D;
  private boolean E;
  private float F;
  private int G;
  private float H = ViewConfiguration.getScrollFriction();
  private float L;
  private final float M;
  private float N;
  private int a;
  private int b;
  private int c;
  private float d;
  private float e;
  private float f;
  private int g;
  private int h;
  private float i;
  private float j;
  private float k;
  private int l;
  private int m;
  private int n;
  private int o;
  private int p;
  private int q;
  private float r;
  private float s;
  private float t;
  private long u;
  private long v;
  private float w;
  private float x;
  private float y;
  private float z;
  
  static
  {
    float f2 = 0.0F;
    I = (float)(Math.log(0.78D) / Math.log(0.9D));
    J = new float[101];
    K = new float[101];
    int i1 = 0;
    float f1 = 0.0F;
    if (i1 >= 100)
    {
      float[] arrayOfFloat = J;
      K[100] = 1.0F;
      arrayOfFloat[100] = 1.0F;
      O = 8.0F;
      P = 1.0F;
      P = 1.0F / a(1.0F);
      return;
    }
    float f5 = i1 / 100.0F;
    float f3 = 1.0F;
    label92:
    float f4 = (f3 - f1) / 2.0F + f1;
    float f6 = 3.0F * f4 * (1.0F - f4);
    float f7 = ((1.0F - f4) * 0.175F + 0.35000002F * f4) * f6 + f4 * f4 * f4;
    if (Math.abs(f7 - f5) < 1.0E-5D)
    {
      J[i1] = (f4 * (f4 * f4) + f6 * ((1.0F - f4) * 0.5F + f4));
      f3 = 1.0F;
    }
    for (;;)
    {
      f4 = (f3 - f2) / 2.0F + f2;
      f6 = 3.0F * f4 * (1.0F - f4);
      f7 = ((1.0F - f4) * 0.5F + f4) * f6 + f4 * f4 * f4;
      if (Math.abs(f7 - f5) < 1.0E-5D)
      {
        K[i1] = (f4 * (f4 * f4) + ((1.0F - f4) * 0.175F + 0.35000002F * f4) * f6);
        i1 += 1;
        break;
        if (f7 > f5)
        {
          f3 = f4;
          break label92;
        }
        f1 = f4;
        break label92;
      }
      if (f7 > f5) {
        f3 = f4;
      } else {
        f2 = f4;
      }
    }
  }
  
  public j(Context paramContext)
  {
    this(paramContext, null);
  }
  
  private j(Context paramContext, Interpolator paramInterpolator) {}
  
  private j(Context paramContext, Interpolator paramInterpolator, boolean paramBoolean)
  {
    this.D = paramInterpolator;
    this.M = (paramContext.getResources().getDisplayMetrics().density * 160.0F);
    this.L = b(ViewConfiguration.getScrollFriction());
    this.E = paramBoolean;
    this.N = b(0.84F);
  }
  
  static float a(float paramFloat)
  {
    paramFloat = O * paramFloat;
    if (paramFloat < 1.0F) {}
    for (paramFloat -= 1.0F - (float)Math.exp(-paramFloat);; paramFloat = (1.0F - (float)Math.exp(1.0F - paramFloat)) * 0.63212055F + 0.36787945F) {
      return paramFloat * P;
    }
  }
  
  private float b(float paramFloat)
  {
    return this.M * 386.0878F * paramFloat;
  }
  
  public final void a(boolean paramBoolean)
  {
    this.C = paramBoolean;
  }
  
  public final boolean a()
  {
    return this.C;
  }
  
  public final int b()
  {
    return this.p;
  }
  
  public final int c()
  {
    return this.q;
  }
  
  public final float d()
  {
    return this.r;
  }
  
  public final float e()
  {
    return this.s;
  }
  
  public final float f()
  {
    return this.t;
  }
  
  public boolean g()
  {
    int i1 = 0;
    int i2;
    if (!this.C)
    {
      i2 = (int)(AnimationUtils.currentAnimationTimeMillis() - this.u);
      if (i2 >= this.v) {
        i1 = 1;
      }
      if (i1 != 0) {}
    }
    else
    {
      float f2;
      switch (this.a)
      {
      default: 
        return true;
        return false;
      case 1: 
        float f3 = i2 / (float)this.v;
        i1 = (int)(100.0F * f3);
        f2 = 1.0F;
        f1 = 0.0F;
        if (i1 >= 100) {}
        for (;;)
        {
          this.F = (f1 * this.G / (float)this.v * 1000.0F);
          this.p = (this.b + Math.round((this.g - this.b) * f2));
          this.p = Math.min(this.p, this.m);
          this.p = Math.max(this.p, this.l);
          this.q = (this.c + Math.round(f2 * (this.h - this.c)));
          this.q = Math.min(this.q, this.o);
          this.q = Math.max(this.q, this.n);
          if ((this.p != this.g) || (this.q != this.h)) {
            break;
          }
          this.C = true;
          return true;
          f2 = i1 / 100.0F;
          f1 = (i1 + 1) / 100.0F;
          float f4 = J[i1];
          f1 = (J[(i1 + 1)] - f4) / (f1 - f2);
          f2 = (f3 - f2) * f1 + f4;
        }
      }
      float f1 = i2 * this.w;
      if (this.D != null) {}
      for (f1 = this.D.getInterpolation(f1);; f1 = a(f1))
      {
        this.p = (this.b + Math.round(this.x * f1));
        this.q = (this.c + Math.round(this.y * f1));
        this.r = (this.d + this.z * f1);
        this.s = (this.e + this.A * f1);
        f2 = this.f;
        this.t = (f1 * this.B + f2);
        return true;
      }
    }
    this.p = this.g;
    this.q = this.h;
    this.r = this.i;
    this.s = this.j;
    this.t = this.k;
    this.C = true;
    return true;
  }
  
  public final int h()
  {
    return this.a;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/j.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */