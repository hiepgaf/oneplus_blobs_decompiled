package com.oneplus.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.Size;
import android.view.Display;
import android.view.WindowManager;
import com.oneplus.util.AspectRatio;

public final class ScreenSize
  implements Comparable<ScreenSize>
{
  public static final ScreenSize EMPTY = new ScreenSize();
  private static volatile int NAV_BAR_HEIGHT_RES_ID;
  private static volatile int NAV_BAR_WIDTH_RES_ID;
  private static volatile int STATUS_BAR_HEIGHT_RES_ID;
  private AspectRatio m_AspectRatio;
  private final int m_Height;
  private final int m_NavBarSize;
  private final int m_StatusBarSize;
  private final int m_Width;
  
  private ScreenSize()
  {
    this.m_Width = 0;
    this.m_Height = 0;
    this.m_NavBarSize = 0;
    this.m_StatusBarSize = 0;
  }
  
  public ScreenSize(Context paramContext, boolean paramBoolean)
  {
    Display localDisplay = ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay();
    Point localPoint = new Point();
    if (paramBoolean)
    {
      localDisplay.getRealSize(localPoint);
      this.m_Width = localPoint.x;
      this.m_Height = localPoint.y;
      paramContext = paramContext.getResources();
      if (NAV_BAR_HEIGHT_RES_ID <= 0) {
        NAV_BAR_HEIGHT_RES_ID = paramContext.getIdentifier("navigation_bar_height", "dimen", "android");
      }
      if (NAV_BAR_WIDTH_RES_ID <= 0) {
        NAV_BAR_WIDTH_RES_ID = paramContext.getIdentifier("navigation_bar_width", "dimen", "android");
      }
      if (this.m_Width >= this.m_Height) {
        break label181;
      }
      if (NAV_BAR_HEIGHT_RES_ID <= 0) {
        break label173;
      }
      this.m_NavBarSize = paramContext.getDimensionPixelSize(NAV_BAR_HEIGHT_RES_ID);
    }
    for (;;)
    {
      if (STATUS_BAR_HEIGHT_RES_ID <= 0) {
        STATUS_BAR_HEIGHT_RES_ID = paramContext.getIdentifier("status_bar_height", "dimen", "android");
      }
      if (STATUS_BAR_HEIGHT_RES_ID <= 0) {
        break label209;
      }
      this.m_StatusBarSize = paramContext.getDimensionPixelSize(STATUS_BAR_HEIGHT_RES_ID);
      return;
      localDisplay.getSize(localPoint);
      break;
      label173:
      this.m_NavBarSize = 0;
      continue;
      label181:
      if (NAV_BAR_WIDTH_RES_ID > 0) {
        this.m_NavBarSize = paramContext.getDimensionPixelSize(NAV_BAR_WIDTH_RES_ID);
      } else {
        this.m_NavBarSize = 0;
      }
    }
    label209:
    this.m_StatusBarSize = 0;
  }
  
  public int compareTo(ScreenSize paramScreenSize)
  {
    if (paramScreenSize != null) {
      return this.m_Width * this.m_Height - paramScreenSize.m_Width * paramScreenSize.m_Height;
    }
    return 1;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if ((paramObject instanceof ScreenSize))
    {
      paramObject = (ScreenSize)paramObject;
      boolean bool1 = bool2;
      if (this.m_Width == ((ScreenSize)paramObject).m_Width)
      {
        bool1 = bool2;
        if (this.m_Height == ((ScreenSize)paramObject).m_Height) {
          bool1 = true;
        }
      }
      return bool1;
    }
    return false;
  }
  
  public AspectRatio getAspectRatio()
  {
    if (this.m_AspectRatio == null) {
      this.m_AspectRatio = AspectRatio.get(this.m_Width, this.m_Height);
    }
    return this.m_AspectRatio;
  }
  
  public int getHeight()
  {
    return this.m_Height;
  }
  
  public int getNavigationBarSize()
  {
    return this.m_NavBarSize;
  }
  
  public int getStatusBarSize()
  {
    return this.m_StatusBarSize;
  }
  
  public int getWidth()
  {
    return this.m_Width;
  }
  
  public int hashCode()
  {
    return this.m_Width << 16 | this.m_Height & 0xFFFF;
  }
  
  public boolean isFullHD()
  {
    return Math.min(this.m_Width, this.m_Height) == 1080;
  }
  
  public boolean isHD()
  {
    return Math.min(this.m_Width, this.m_Height) == 720;
  }
  
  public Size toSize()
  {
    return new Size(this.m_Width, this.m_Height);
  }
  
  public String toString()
  {
    String str = null;
    switch (Math.min(this.m_Width, this.m_Height))
    {
    }
    while (str != null)
    {
      return this.m_Width + "x" + this.m_Height + " (" + str + ")";
      str = "Full HD";
      continue;
      str = "HD";
    }
    return this.m_Width + "x" + this.m_Height;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/ScreenSize.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */