package com.amap.api.mapcore2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

class cb
  extends LinearLayout
{
  private Bitmap a;
  private Bitmap b;
  private Bitmap c;
  private Bitmap d;
  private Bitmap e;
  private Bitmap f;
  private ImageView g;
  private ImageView h;
  private ap i;
  private w j;
  private int k = 0;
  
  public cb(Context paramContext, ap paramap, w paramw)
  {
    super(paramContext);
    setWillNotDraw(false);
    this.i = paramap;
    this.j = paramw;
    try
    {
      this.a = cj.a("zoomin_selected2d.png");
      this.a = cj.a(this.a, p.a);
      this.b = cj.a("zoomin_unselected2d.png");
      this.b = cj.a(this.b, p.a);
      this.c = cj.a("zoomout_selected2d.png");
      this.c = cj.a(this.c, p.a);
      this.d = cj.a("zoomout_unselected2d.png");
      this.d = cj.a(this.d, p.a);
      this.e = cj.a("zoomin_pressed2d.png");
      this.f = cj.a("zoomout_pressed2d.png");
      this.e = cj.a(this.e, p.a);
      this.f = cj.a(this.f, p.a);
      this.g = new ImageView(paramContext);
      this.g.setImageBitmap(this.a);
      this.g.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          cb.b(cb.this).setImageBitmap(cb.a(cb.this));
          if (cb.c(cb.this).f() > (int)cb.c(cb.this).h() - 2) {
            cb.e(cb.this).setImageBitmap(cb.d(cb.this));
          }
          for (;;)
          {
            cb.this.a(cb.c(cb.this).f() + 1.0F);
            cb.g(cb.this).c();
            return;
            cb.e(cb.this).setImageBitmap(cb.f(cb.this));
          }
        }
      });
      this.h = new ImageView(paramContext);
      this.h.setImageBitmap(this.c);
      this.h.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          cb.e(cb.this).setImageBitmap(cb.f(cb.this));
          cb.this.a(cb.c(cb.this).f() - 1.0F);
          if (cb.c(cb.this).f() < (int)cb.c(cb.this).i() + 2) {
            cb.b(cb.this).setImageBitmap(cb.h(cb.this));
          }
          for (;;)
          {
            cb.g(cb.this).d();
            return;
            cb.b(cb.this).setImageBitmap(cb.a(cb.this));
          }
        }
      });
      this.g.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          if (cb.c(cb.this).f() >= cb.c(cb.this).h()) {
            return false;
          }
          if (paramAnonymousMotionEvent.getAction() != 0)
          {
            if (paramAnonymousMotionEvent.getAction() != 1) {
              return false;
            }
          }
          else
          {
            cb.e(cb.this).setImageBitmap(cb.i(cb.this));
            return false;
          }
          cb.e(cb.this).setImageBitmap(cb.f(cb.this));
          try
          {
            cb.c(cb.this).b(l.b());
            return false;
          }
          catch (RemoteException paramAnonymousView)
          {
            cj.a(paramAnonymousView, "ZoomControllerView", "ontouch");
          }
          return false;
        }
      });
      this.h.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          if (cb.c(cb.this).f() <= cb.c(cb.this).i()) {
            return false;
          }
          if (paramAnonymousMotionEvent.getAction() != 0)
          {
            if (paramAnonymousMotionEvent.getAction() != 1) {
              return false;
            }
          }
          else
          {
            cb.b(cb.this).setImageBitmap(cb.j(cb.this));
            return false;
          }
          cb.b(cb.this).setImageBitmap(cb.a(cb.this));
          try
          {
            cb.c(cb.this).b(l.c());
            return false;
          }
          catch (RemoteException paramAnonymousView)
          {
            cj.a(paramAnonymousView, "ZoomControllerView", "onTouch");
          }
          return false;
        }
      });
      this.g.setPadding(0, 0, 20, -2);
      this.h.setPadding(0, 0, 20, 20);
      setOrientation(1);
      addView(this.g);
      addView(this.h);
      return;
    }
    catch (Throwable paramap)
    {
      for (;;)
      {
        cj.a(paramap, "ZoomControllerView", "ZoomControllerView");
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
      this.d.recycle();
      this.e.recycle();
      this.f.recycle();
      this.a = null;
      this.b = null;
      this.c = null;
      this.d = null;
      this.e = null;
      this.f = null;
      return;
    }
    catch (Exception localException)
    {
      cj.a(localException, "ZoomControllerView", "destory");
    }
  }
  
  public void a(float paramFloat)
  {
    if ((paramFloat < this.j.h()) && (paramFloat > this.j.i()))
    {
      this.g.setImageBitmap(this.a);
      this.h.setImageBitmap(this.c);
    }
    do
    {
      return;
      if (paramFloat <= this.j.i())
      {
        this.h.setImageBitmap(this.d);
        this.g.setImageBitmap(this.a);
        return;
      }
    } while (paramFloat < this.j.h());
    this.g.setImageBitmap(this.b);
    this.h.setImageBitmap(this.c);
  }
  
  public void a(int paramInt)
  {
    this.k = paramInt;
    removeView(this.g);
    removeView(this.h);
    addView(this.g);
    addView(this.h);
  }
  
  public int b()
  {
    return this.k;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */