package com.amap.api.mapcore2d;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.view.View;
import java.io.InputStream;

class ca
  extends View
{
  private Bitmap a;
  private Bitmap b;
  private Paint c = new Paint();
  private boolean d = false;
  private int e = 0;
  private b f;
  private int g = 0;
  private int h = 10;
  
  public ca(Context paramContext, b paramb)
  {
    super(paramContext);
    this.f = paramb;
    paramb = paramContext.getResources().getAssets();
    for (;;)
    {
      try
      {
        if (p.e == p.a.b) {
          continue;
        }
        paramContext = paramb.open("ap2d.data");
        this.a = BitmapFactory.decodeStream(paramContext);
        this.a = cj.a(this.a, p.a);
        paramContext.close();
        if (p.e == p.a.b) {
          continue;
        }
        paramContext = paramb.open("ap12d.data");
        this.b = BitmapFactory.decodeStream(paramContext);
        this.b = cj.a(this.b, p.a);
        paramContext.close();
        this.e = this.b.getHeight();
      }
      catch (Throwable paramContext)
      {
        cj.a(paramContext, "WaterMarkerView", "WaterMarkerView");
        continue;
      }
      this.c.setAntiAlias(true);
      this.c.setColor(-16777216);
      this.c.setStyle(Paint.Style.STROKE);
      return;
      paramContext = paramb.open("apl2d.data");
      continue;
      paramContext = paramb.open("apl12d.data");
    }
  }
  
  public void a()
  {
    for (;;)
    {
      try
      {
        if (this.a == null)
        {
          if (this.b == null)
          {
            this.a = null;
            this.b = null;
            this.c = null;
          }
        }
        else
        {
          this.a.recycle();
          continue;
        }
        this.b.recycle();
      }
      catch (Exception localException)
      {
        cj.a(localException, "WaterMarkerView", "destory");
        return;
      }
    }
  }
  
  public void a(int paramInt)
  {
    this.g = paramInt;
  }
  
  public void a(boolean paramBoolean)
  {
    this.d = paramBoolean;
    invalidate();
  }
  
  public Bitmap b()
  {
    if (!this.d) {
      return this.a;
    }
    return this.b;
  }
  
  public Point c()
  {
    return new Point(this.h, getHeight() - this.e - 10);
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    int i;
    if (this.b != null)
    {
      i = this.b.getWidth() + 3;
      if (this.g == 1) {
        break label80;
      }
      if (this.g == 2) {
        break label98;
      }
      this.h = 10;
    }
    while (p.e != p.a.b)
    {
      paramCanvas.drawBitmap(b(), this.h, getHeight() - this.e - 8, this.c);
      return;
      return;
      label80:
      this.h = ((this.f.getWidth() - i) / 2);
      continue;
      label98:
      this.h = (this.f.getWidth() - i - 10);
    }
    paramCanvas.drawBitmap(b(), this.h + 15, getHeight() - this.e - 8, this.c);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ca.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */