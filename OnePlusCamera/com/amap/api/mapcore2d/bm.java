package com.amap.api.mapcore2d;

import android.graphics.Canvas;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.TextOptions;

public class bm
  implements ai
{
  private b a;
  private as b;
  private String c;
  private int d;
  private int e;
  private LatLng f;
  private float g;
  private int h;
  private Typeface i;
  private boolean j;
  private float k;
  private int l;
  private int m;
  private Object n;
  private int o;
  
  public bm(w paramw, TextOptions paramTextOptions, as paramas)
  {
    this.b = paramas;
    this.c = paramTextOptions.getText();
    this.d = paramTextOptions.getFontSize();
    this.e = paramTextOptions.getFontColor();
    this.f = paramTextOptions.getPosition();
    this.g = paramTextOptions.getRotate();
    this.h = paramTextOptions.getBackgroundColor();
    this.i = paramTextOptions.getTypeface();
    this.j = paramTextOptions.isVisible();
    this.k = paramTextOptions.getZIndex();
    this.l = paramTextOptions.getAlignX();
    this.m = paramTextOptions.getAlignY();
    this.n = paramTextOptions.getObject();
    this.a = ((b)paramw);
  }
  
  public String a()
  {
    return this.c;
  }
  
  public void a(float paramFloat)
  {
    this.g = paramFloat;
    this.a.postInvalidate();
  }
  
  public void a(int paramInt)
  {
    this.d = paramInt;
    this.a.postInvalidate();
  }
  
  public void a(int paramInt1, int paramInt2)
  {
    this.l = paramInt1;
    this.m = paramInt2;
    this.a.postInvalidate();
  }
  
  public void a(Canvas paramCanvas)
  {
    if (TextUtils.isEmpty(this.c)) {}
    while (this.f == null) {
      return;
    }
    TextPaint localTextPaint = new TextPaint();
    float f1;
    float f2;
    Object localObject;
    Point localPoint;
    label188:
    label193:
    label201:
    label207:
    int i1;
    label239:
    int i2;
    if (this.i != null)
    {
      localTextPaint.setTypeface(this.i);
      localTextPaint.setAntiAlias(true);
      localTextPaint.setTextSize(this.d);
      f1 = localTextPaint.measureText(this.c);
      f2 = this.d;
      localTextPaint.setColor(this.h);
      localObject = new u((int)(this.f.latitude * 1000000.0D), (int)(this.f.longitude * 1000000.0D));
      localPoint = new Point();
      this.a.s().a((u)localObject, localPoint);
      paramCanvas.save();
      paramCanvas.rotate(-(this.g % 360.0F), localPoint.x, localPoint.y);
      localObject = localTextPaint.getFontMetrics();
      if (this.l >= 1) {
        break label351;
      }
      this.l = 3;
      if (this.m >= 4) {
        break label362;
      }
      this.m = 6;
      switch (this.l)
      {
      default: 
        i1 = 0;
        switch (this.m)
        {
        default: 
          i2 = 0;
        }
        break;
      }
    }
    for (;;)
    {
      f2 = 2.0F + f2;
      paramCanvas.drawRect(i1 - 1, i2 - 1, i1 + (f1 + 2.0F), i2 + f2, localTextPaint);
      localTextPaint.setColor(this.e);
      paramCanvas.drawText(this.c, i1, i2 + f2 - ((Paint.FontMetrics)localObject).bottom, localTextPaint);
      paramCanvas.restore();
      return;
      this.i = Typeface.DEFAULT;
      break;
      label351:
      if (this.l > 3) {
        break label188;
      }
      break label193;
      label362:
      if (this.m > 6) {
        break label201;
      }
      break label207;
      i1 = localPoint.x;
      break label239;
      i1 = (int)(localPoint.x - f1);
      break label239;
      i1 = (int)(localPoint.x - f1 / 2.0F);
      break label239;
      i2 = localPoint.y;
      continue;
      i2 = (int)(localPoint.y - f2);
      continue;
      i2 = (int)(localPoint.y - f2 / 2.0F);
    }
  }
  
  public void a(Typeface paramTypeface)
  {
    this.i = paramTypeface;
    this.a.postInvalidate();
  }
  
  public void a(Object paramObject)
  {
    this.n = paramObject;
  }
  
  public void a(String paramString)
  {
    this.c = paramString;
    this.a.postInvalidate();
  }
  
  public void a(boolean paramBoolean)
  {
    this.j = paramBoolean;
    this.a.postInvalidate();
  }
  
  public int b()
  {
    return this.d;
  }
  
  public void b(float paramFloat)
  {
    this.k = paramFloat;
    this.b.d();
  }
  
  public void b(int paramInt)
  {
    this.o = paramInt;
  }
  
  public void b(LatLng paramLatLng)
  {
    this.f = paramLatLng;
    this.a.postInvalidate();
  }
  
  public int c()
  {
    return this.e;
  }
  
  public void c(int paramInt)
  {
    this.e = paramInt;
    this.a.postInvalidate();
  }
  
  public float d()
  {
    return this.g;
  }
  
  public void d(int paramInt)
  {
    this.h = paramInt;
    this.a.postInvalidate();
  }
  
  public int e()
  {
    return this.h;
  }
  
  public Typeface f()
  {
    return this.i;
  }
  
  public int g()
  {
    return this.l;
  }
  
  public int h()
  {
    return this.m;
  }
  
  public void i()
  {
    if (this.b == null) {
      return;
    }
    this.b.b(this);
  }
  
  public float r()
  {
    return this.k;
  }
  
  public boolean s()
  {
    return this.j;
  }
  
  public LatLng t()
  {
    return this.f;
  }
  
  public Object u()
  {
    return this.n;
  }
  
  public int v()
  {
    return this.o;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */