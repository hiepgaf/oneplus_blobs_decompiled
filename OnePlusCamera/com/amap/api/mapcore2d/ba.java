package com.amap.api.mapcore2d;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

abstract class ba
{
  static float j = 1.0F;
  private static Method p;
  private static Method q;
  private static boolean r = false;
  private static boolean s = false;
  b a;
  int b = 0;
  Matrix c = new Matrix();
  Matrix d = new Matrix();
  PointF e = new PointF();
  PointF f = new PointF();
  PointF g = new PointF();
  float h = 1.0F;
  float i = 1.0F;
  boolean k = false;
  boolean l = false;
  boolean m = false;
  public int n = 0;
  public long o = 0L;
  
  public static a a(Context paramContext, b paramb)
  {
    paramContext = new a();
    paramContext.a = paramb;
    return paramContext;
  }
  
  private static void b(MotionEvent paramMotionEvent)
  {
    if (!s)
    {
      s = true;
      try
      {
        p = paramMotionEvent.getClass().getMethod("getX", new Class[] { Integer.TYPE });
        q = paramMotionEvent.getClass().getMethod("getY", new Class[] { Integer.TYPE });
        if (p == null) {
          return;
        }
        if (q != null)
        {
          r = true;
          return;
        }
      }
      catch (Exception paramMotionEvent)
      {
        cj.a(paramMotionEvent, "MutiTouchGestureDetector", "checkSDKForMuti");
      }
      return;
    }
  }
  
  protected static class a
    extends ba
  {
    float p;
    float q;
    float r;
    float s;
    long t = 0L;
    int u = 0;
    int v = 0;
    private long w = 0L;
    
    private void a(PointF paramPointF, MotionEvent paramMotionEvent)
    {
      f2 = 0.0F;
      try
      {
        f1 = ((Float)ba.b().invoke(paramMotionEvent, new Object[] { Integer.valueOf(0) })).floatValue();
        f3 = ((Float)ba.b().invoke(paramMotionEvent, new Object[] { Integer.valueOf(1) })).floatValue();
        f1 = f3 + f1;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        for (;;)
        {
          float f3;
          float f4;
          cj.a(localIllegalArgumentException, "MutiTouchGestureDetector", "midPoint");
          f1 = 0.0F;
        }
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        for (;;)
        {
          cj.a(localIllegalAccessException, "MutiTouchGestureDetector", "midPoint");
        }
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        for (;;)
        {
          label118:
          label125:
          cj.a(localInvocationTargetException, "MutiTouchGestureDetector", "midPoint");
          f1 = 0.0F;
        }
      }
      try
      {
        f3 = ((Float)ba.c().invoke(paramMotionEvent, new Object[] { Integer.valueOf(0) })).floatValue();
        f4 = ((Float)ba.c().invoke(paramMotionEvent, new Object[] { Integer.valueOf(1) })).floatValue();
        f2 = f3 + f4;
      }
      catch (IllegalArgumentException paramMotionEvent)
      {
        cj.a(paramMotionEvent, "MutiTouchGestureDetector", "midPoint");
        break label118;
      }
      catch (IllegalAccessException paramMotionEvent)
      {
        cj.a(paramMotionEvent, "MutiTouchGestureDetector", "midPoint");
        break label118;
      }
      catch (InvocationTargetException paramMotionEvent)
      {
        cj.a(paramMotionEvent, "MutiTouchGestureDetector", "midPoint");
        break label118;
        if (this.v == 0) {
          break label125;
        }
        f1 = this.u;
        f2 = this.v;
        break label125;
      }
      if (this.u == 0)
      {
        paramPointF.set(f1 / 2.0F, f2 / 2.0F);
        return;
      }
    }
    
    private float b(MotionEvent paramMotionEvent)
    {
      float f2 = 0.0F;
      try
      {
        f1 = ((Float)ba.b().invoke(paramMotionEvent, new Object[] { Integer.valueOf(0) })).floatValue();
        f3 = ((Float)ba.b().invoke(paramMotionEvent, new Object[] { Integer.valueOf(1) })).floatValue();
        f1 -= f3;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        for (;;)
        {
          float f3;
          float f4;
          cj.a(localIllegalArgumentException, "MutiTouchGestureDetector", "distance");
          f1 = 0.0F;
        }
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        for (;;)
        {
          cj.a(localIllegalAccessException, "MutiTouchGestureDetector", "distance");
        }
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        for (;;)
        {
          label116:
          cj.a(localInvocationTargetException, "MutiTouchGestureDetector", "distance");
          float f1 = 0.0F;
        }
      }
      try
      {
        f3 = ((Float)ba.c().invoke(paramMotionEvent, new Object[] { Integer.valueOf(0) })).floatValue();
        f4 = ((Float)ba.c().invoke(paramMotionEvent, new Object[] { Integer.valueOf(1) })).floatValue();
        f2 = f3 - f4;
      }
      catch (IllegalArgumentException paramMotionEvent)
      {
        cj.a(paramMotionEvent, "MutiTouchGestureDetector", "distance");
        break label116;
      }
      catch (IllegalAccessException paramMotionEvent)
      {
        cj.a(paramMotionEvent, "MutiTouchGestureDetector", "distance");
        break label116;
      }
      catch (InvocationTargetException paramMotionEvent)
      {
        cj.a(paramMotionEvent, "MutiTouchGestureDetector", "distance");
        break label116;
      }
      return (float)Math.sqrt(f1 * f1 + f2 * f2);
    }
    
    public boolean a(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
    {
      boolean bool1 = true;
      int i = 0;
      this.u = paramInt1;
      this.v = paramInt2;
      ba.a(paramMotionEvent);
      if (ba.a()) {
        switch (paramMotionEvent.getAction() & 0xFF)
        {
        case 4: 
        default: 
          bool1 = false;
        }
      }
      boolean bool2;
      boolean bool3;
      do
      {
        return bool1;
        return false;
        this.t = paramMotionEvent.getEventTime();
        this.p = paramMotionEvent.getX();
        this.q = paramMotionEvent.getY();
        this.d.set(this.c);
        this.e.set(this.p, this.q);
        this.b = 1;
        return false;
        this.n += 1;
        if (this.n != 1) {
          return false;
        }
        this.m = true;
        j = 1.0F;
        this.h = b(paramMotionEvent);
        if (this.h > 10.0F)
        {
          this.c.reset();
          this.d.reset();
          this.d.set(this.c);
          a(this.f, paramMotionEvent);
          this.b = 2;
          this.k = true;
          bool1 = this.a.a(this.e);
          this.r = this.f.x;
          this.s = this.f.y;
          return bool1 | false;
        }
        return false;
        this.o = paramMotionEvent.getEventTime();
        this.k = false;
        this.b = 0;
        return false;
        this.n -= 1;
        if (this.n != 1) {}
        while (this.n != 0)
        {
          return false;
          this.m = true;
          this.b = 2;
        }
        a(this.f, paramMotionEvent);
        this.l = false;
        this.m = false;
        if (!this.k) {
          return false;
        }
        bool1 = this.a.b(this.i, this.f);
        this.b = 0;
        return bool1 | false;
        if (this.b != 1)
        {
          if (this.b == 2) {
            break;
          }
          return false;
        }
        f1 = paramMotionEvent.getX();
        float f2 = paramMotionEvent.getY();
        this.c.set(this.d);
        this.c.postTranslate(paramMotionEvent.getX() - this.e.x, paramMotionEvent.getY() - this.e.y);
        bool2 = this.a.a(f1 - this.p, f2 - this.q);
        this.p = f1;
        this.q = f2;
        bool3 = this.a.a(this.c);
        paramInt1 = i;
        if (paramMotionEvent.getEventTime() - this.t >= 30L) {
          paramInt1 = 1;
        }
      } while (paramInt1 == 0);
      return bool3 | bool2 | false;
      float f1 = b(paramMotionEvent);
      this.i = 1.0F;
      long l = paramMotionEvent.getEventTime();
      if ((f1 > 10.0F) && (Math.abs(f1 - this.h) > 5.0F)) {
        if (l - this.w > 10L) {
          break label741;
        }
      }
      label741:
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        if (paramInt1 == 0)
        {
          this.w = l;
          this.i = (f1 / this.h);
          j = 1.0F;
          this.h = f1;
          a(this.g, paramMotionEvent);
          bool1 = this.a.a(this.g.x - this.r, this.g.y - this.s);
          this.r = this.g.x;
          this.s = this.g.y;
          bool2 = this.a.a(this.i, this.f);
          this.l = true;
          return bool1 | false | bool2;
        }
        return false;
      }
    }
  }
  
  public static abstract interface b
  {
    public abstract boolean a(float paramFloat1, float paramFloat2);
    
    public abstract boolean a(float paramFloat, PointF paramPointF);
    
    public abstract boolean a(Matrix paramMatrix);
    
    public abstract boolean a(PointF paramPointF);
    
    public abstract boolean b(float paramFloat, PointF paramPointF);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ba.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */