package com.amap.api.mapcore2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.amap.api.maps2d.model.LatLng;

class ao
  extends LinearLayout
{
  private Bitmap a;
  private Bitmap b;
  private Bitmap c;
  private ImageView d;
  private w e;
  private boolean f = false;
  
  public ao(Context paramContext, ar paramar, w paramw)
  {
    super(paramContext);
    this.e = paramw;
    try
    {
      this.a = cj.a("location_selected2d.png");
      this.b = cj.a("location_pressed2d.png");
      this.a = cj.a(this.a, p.a);
      this.b = cj.a(this.b, p.a);
      this.c = cj.a("location_unselected2d.png");
      this.c = cj.a(this.c, p.a);
      this.d = new ImageView(paramContext);
      this.d.setImageBitmap(this.a);
      this.d.setPadding(0, 20, 20, 0);
      this.d.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView) {}
      });
      this.d.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          if (ao.a(ao.this))
          {
            if (paramAnonymousMotionEvent.getAction() != 0)
            {
              if (paramAnonymousMotionEvent.getAction() == 1) {
                break label48;
              }
              return false;
            }
          }
          else {
            return false;
          }
          ao.c(ao.this).setImageBitmap(ao.b(ao.this));
          return false;
          try
          {
            label48:
            ao.c(ao.this).setImageBitmap(ao.d(ao.this));
            ao.e(ao.this).c(true);
            paramAnonymousView = ao.e(ao.this).p();
            if (paramAnonymousView != null)
            {
              paramAnonymousMotionEvent = new LatLng(paramAnonymousView.getLatitude(), paramAnonymousView.getLongitude());
              ao.e(ao.this).a(paramAnonymousView);
              ao.e(ao.this).a(l.a(paramAnonymousMotionEvent, ao.e(ao.this).f()));
              return false;
            }
          }
          catch (Exception paramAnonymousView)
          {
            cj.a(paramAnonymousView, "LocationView", "onTouch");
            return false;
          }
          return false;
        }
      });
      addView(this.d);
      return;
    }
    catch (Throwable paramar)
    {
      for (;;)
      {
        cj.a(paramar, "LocationView", "LocationView");
      }
    }
  }
  
  public void a()
  {
    try
    {
      this.a.recycle();
      this.b.recycle();
      this.c.recycle();
      this.a = null;
      this.b = null;
      this.c = null;
      return;
    }
    catch (Exception localException)
    {
      cj.a(localException, "LocationView", "destory");
    }
  }
  
  public void a(boolean paramBoolean)
  {
    this.f = paramBoolean;
    if (!paramBoolean) {
      this.d.setImageBitmap(this.c);
    }
    for (;;)
    {
      this.d.invalidate();
      return;
      this.d.setImageBitmap(this.a);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ao.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */