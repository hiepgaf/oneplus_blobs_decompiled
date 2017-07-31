package com.amap.api.mapcore2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import com.amap.api.maps2d.model.CameraPosition;

class o
  extends LinearLayout
{
  private Bitmap a;
  private Bitmap b;
  private ImageView c;
  private ar d;
  private w e;
  
  public o(Context paramContext, ar paramar, w paramw)
  {
    super(paramContext);
    this.d = paramar;
    this.e = paramw;
    try
    {
      paramar = cj.a("maps_dav_compass_needle_large2d.png");
      this.b = cj.a(paramar, p.a * 0.8F);
      paramar = cj.a(paramar, p.a * 0.7F);
      this.a = Bitmap.createBitmap(this.b.getWidth(), this.b.getHeight(), Bitmap.Config.ARGB_8888);
      paramw = new Canvas(this.a);
      Paint localPaint = new Paint();
      localPaint.setAntiAlias(true);
      localPaint.setFilterBitmap(true);
      paramw.drawBitmap(paramar, (this.b.getWidth() - paramar.getWidth()) / 2, (this.b.getHeight() - paramar.getHeight()) / 2, localPaint);
      this.c = new ImageView(paramContext);
      this.c.setScaleType(ImageView.ScaleType.MATRIX);
      this.c.setImageBitmap(this.a);
      this.c.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView) {}
      });
      this.c.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          if (paramAnonymousMotionEvent.getAction() != 0)
          {
            if (paramAnonymousMotionEvent.getAction() != 1) {
              return false;
            }
          }
          else
          {
            o.b(o.this).setImageBitmap(o.a(o.this));
            return false;
          }
          try
          {
            o.b(o.this).setImageBitmap(o.c(o.this));
            paramAnonymousView = o.d(o.this).g();
            o.d(o.this).b(l.a(new CameraPosition(paramAnonymousView.target, paramAnonymousView.zoom, 0.0F, 0.0F)));
            return false;
          }
          catch (Exception paramAnonymousView)
          {
            cj.a(paramAnonymousView, "CompassView", "onTouch");
          }
          return false;
        }
      });
      addView(this.c);
      return;
    }
    catch (Throwable paramar)
    {
      for (;;)
      {
        cj.a(paramar, "CompassView", "CompassView");
      }
    }
  }
  
  public void a()
  {
    try
    {
      this.a.recycle();
      this.b.recycle();
      this.a = null;
      this.b = null;
      return;
    }
    catch (Exception localException)
    {
      cj.a(localException, "CompassView", "destory");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/o.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */