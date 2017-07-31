package com.amap.api.maps2d.model;

import android.graphics.Typeface;
import com.amap.api.mapcore2d.ai;

public final class Text
{
  public static final int ALIGN_BOTTOM = 5;
  public static final int ALIGN_CENTER_HORIZONTAL = 3;
  public static final int ALIGN_CENTER_VERTICAL = 6;
  public static final int ALIGN_LEFT = 1;
  public static final int ALIGN_RIGHT = 2;
  public static final int ALIGN_TOP = 4;
  private ai a;
  
  public Text(ai paramai)
  {
    this.a = paramai;
  }
  
  public int getAlignX()
  {
    return this.a.g();
  }
  
  public int getAlignY()
  {
    return this.a.h();
  }
  
  public int getBackgroundColor()
  {
    return this.a.e();
  }
  
  public int getFontColor()
  {
    return this.a.c();
  }
  
  public int getFontSize()
  {
    return this.a.b();
  }
  
  public Object getObject()
  {
    return this.a.u();
  }
  
  public LatLng getPosition()
  {
    return this.a.t();
  }
  
  public float getRotate()
  {
    return this.a.d();
  }
  
  public String getText()
  {
    return this.a.a();
  }
  
  public Typeface getTypeface()
  {
    return this.a.f();
  }
  
  public float getZIndex()
  {
    return this.a.r();
  }
  
  public boolean isVisible()
  {
    return this.a.s();
  }
  
  public void remove()
  {
    this.a.i();
  }
  
  public void setAlign(int paramInt1, int paramInt2)
  {
    this.a.a(paramInt1, paramInt2);
  }
  
  public void setBackgroundColor(int paramInt)
  {
    this.a.d(paramInt);
  }
  
  public void setFontColor(int paramInt)
  {
    this.a.c(paramInt);
  }
  
  public void setFontSize(int paramInt)
  {
    this.a.a(paramInt);
  }
  
  public void setObject(Object paramObject)
  {
    this.a.a(paramObject);
  }
  
  public void setPosition(LatLng paramLatLng)
  {
    this.a.b(paramLatLng);
  }
  
  public void setRotate(float paramFloat)
  {
    this.a.a(paramFloat);
  }
  
  public void setText(String paramString)
  {
    this.a.a(paramString);
  }
  
  public void setTypeface(Typeface paramTypeface)
  {
    this.a.a(paramTypeface);
  }
  
  public void setVisible(boolean paramBoolean)
  {
    this.a.a(paramBoolean);
  }
  
  public void setZIndex(float paramFloat)
  {
    this.a.b(paramFloat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/Text.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */