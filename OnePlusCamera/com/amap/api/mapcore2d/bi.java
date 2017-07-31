package com.amap.api.mapcore2d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.RemoteException;
import android.view.View;

class bi
  extends View
{
  private String a = "";
  private int b = 0;
  private b c;
  private Paint d;
  private Paint e;
  private Rect f;
  
  public bi(Context paramContext, b paramb)
  {
    super(paramContext);
    this.c = paramb;
    this.d = new Paint();
    this.f = new Rect();
    this.d.setAntiAlias(true);
    this.d.setColor(-16777216);
    this.d.setStrokeWidth(p.a * 2.0F);
    this.d.setStyle(Paint.Style.STROKE);
    this.e = new Paint();
    this.e.setAntiAlias(true);
    this.e.setColor(-16777216);
    this.e.setTextSize(p.a * 20.0F);
  }
  
  public void a()
  {
    this.d = null;
    this.e = null;
    this.f = null;
    this.a = null;
  }
  
  public void a(int paramInt)
  {
    this.b = paramInt;
  }
  
  public void a(String paramString)
  {
    this.a = paramString;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    try
    {
      boolean bool = this.c.q().a();
      if (!bool) {
        break label32;
      }
    }
    catch (RemoteException localRemoteException)
    {
      label32:
      do
      {
        for (;;)
        {
          localRemoteException.printStackTrace();
        }
      } while (this.b == 0);
      i = this.b;
    }
    if (this.a.equals(""))
    {
      return;
      return;
    }
    for (;;)
    {
      try
      {
        j = this.c.getWidth() / 5;
        if (i > j) {
          continue;
        }
      }
      catch (Exception localException)
      {
        int i;
        Point localPoint;
        int k;
        cj.a(localException, "ScaleView", "onDraw");
        continue;
        int j = this.c.getWidth() - 10 - (this.f.width() + i) / 2;
        continue;
      }
      localPoint = this.c.A();
      this.e.getTextBounds(this.a, 0, this.a.length(), this.f);
      if (localPoint.x + i > this.c.getWidth() - 10) {
        continue;
      }
      j = localPoint.x + (i - this.f.width()) / 2;
      k = localPoint.y - this.f.height() + 5;
      paramCanvas.drawText(this.a, j, k, this.e);
      j -= (i - this.f.width()) / 2;
      k += this.f.height() - 5;
      paramCanvas.drawLine(j, k - 2, j, k + 2, this.d);
      paramCanvas.drawLine(j, k, j + i, k, this.d);
      paramCanvas.drawLine(j + i, k - 2, j + i, k + 2, this.d);
      return;
      j = this.c.getWidth() / 5;
      i = j;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */