package com.oneplus.camera.media;

import android.content.Context;
import android.util.Size;
import com.oneplus.util.AspectRatio;
import java.util.Locale;

public final class Resolution
  implements Comparable<Resolution>
{
  private AspectRatio m_AspectRatio;
  private int m_Fps;
  private final int m_Height;
  private final MediaType m_TargetType;
  private final int m_Width;
  
  public Resolution(MediaType paramMediaType, int paramInt1, int paramInt2)
  {
    this(paramMediaType, paramInt1, paramInt2, 0);
  }
  
  public Resolution(MediaType paramMediaType, int paramInt1, int paramInt2, int paramInt3)
  {
    this.m_TargetType = paramMediaType;
    this.m_Width = paramInt1;
    this.m_Height = paramInt2;
    this.m_Fps = paramInt3;
  }
  
  public Resolution(MediaType paramMediaType, Size paramSize)
  {
    this(paramMediaType, paramSize.getWidth(), paramSize.getHeight(), 0);
  }
  
  public Resolution(Resolution paramResolution, int paramInt)
  {
    this(paramResolution.m_TargetType, paramResolution.m_Width, paramResolution.m_Height, paramInt);
  }
  
  public static Resolution fromKey(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int k = paramString.indexOf('_');
    if (k < 0) {
      return null;
    }
    MediaType localMediaType;
    int m;
    int j;
    try
    {
      localMediaType = (MediaType)Enum.valueOf(MediaType.class, paramString.substring(0, k));
      m = paramString.indexOf('x', k + 1);
      j = paramString.indexOf('#', k + 1);
      if (m < 0) {
        return null;
      }
    }
    catch (Throwable paramString)
    {
      return null;
    }
    int i = j;
    if (j < 0) {
      i = paramString.length();
    }
    try
    {
      k = Integer.parseInt(paramString.substring(k + 1, m));
      m = Integer.parseInt(paramString.substring(m + 1, i));
      j = 0;
      if (i != paramString.length()) {}
      return null;
    }
    catch (Throwable paramString)
    {
      try
      {
        j = Integer.parseInt(paramString.substring(i + 1));
        return new Resolution(localMediaType, k, m, j);
      }
      catch (Throwable paramString) {}
      paramString = paramString;
      return null;
    }
  }
  
  public int compareTo(Resolution paramResolution)
  {
    if (paramResolution != null) {
      return this.m_Width * this.m_Height - paramResolution.m_Width * paramResolution.m_Height;
    }
    return 1;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if ((paramObject instanceof Resolution))
    {
      paramObject = (Resolution)paramObject;
      boolean bool1 = bool2;
      if (this.m_TargetType == ((Resolution)paramObject).m_TargetType)
      {
        bool1 = bool2;
        if (this.m_Width == ((Resolution)paramObject).m_Width)
        {
          bool1 = bool2;
          if (this.m_Height == ((Resolution)paramObject).m_Height)
          {
            bool1 = bool2;
            if (this.m_Fps == ((Resolution)paramObject).m_Fps) {
              bool1 = true;
            }
          }
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
  
  public int getFps()
  {
    return this.m_Fps;
  }
  
  public int getHeight()
  {
    return this.m_Height;
  }
  
  public String getKey()
  {
    return this.m_TargetType + "_" + this.m_Width + "x" + this.m_Height + "#" + this.m_Fps;
  }
  
  public int getMegaPixels()
  {
    return Math.round(this.m_Width * this.m_Height / 1000.0F / 1000.0F);
  }
  
  public String getMegaPixelsDescription()
  {
    int i = Math.round(this.m_Width * this.m_Height / 1000.0F / 1000.0F);
    return String.format(Locale.US, "%dMP", new Object[] { Integer.valueOf(i) });
  }
  
  public String getMenuDescription(Context paramContext)
  {
    return getMegaPixelsDescription() + " (" + this.m_Width + "x" + this.m_Height + ")";
  }
  
  public MediaType getTargetType()
  {
    return this.m_TargetType;
  }
  
  public int getWidth()
  {
    return this.m_Width;
  }
  
  public int hashCode()
  {
    return this.m_Width << 16 | this.m_Height & 0xFFFF;
  }
  
  public boolean is1080pVideo()
  {
    if ((this.m_TargetType == MediaType.VIDEO) && (this.m_Width == 1920)) {
      return (this.m_Height == 1080) || (this.m_Height == 1088);
    }
    return false;
  }
  
  public boolean is4kVideo()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.m_TargetType == MediaType.VIDEO) {
      if (this.m_Width != 4096)
      {
        bool1 = bool2;
        if (this.m_Width != 3840) {}
      }
      else
      {
        bool1 = bool2;
        if (this.m_Height == 2160) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public boolean is720pVideo()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.m_TargetType == MediaType.VIDEO)
    {
      bool1 = bool2;
      if (this.m_Width == 1280)
      {
        bool1 = bool2;
        if (this.m_Height == 720) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public boolean isMmsVideo()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.m_TargetType == MediaType.VIDEO)
    {
      bool1 = bool2;
      if (this.m_Width == 176)
      {
        bool1 = bool2;
        if (this.m_Height == 144) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public boolean isWiderThan(Resolution paramResolution)
  {
    boolean bool = false;
    if (paramResolution != null)
    {
      if (this.m_Width / this.m_Height > paramResolution.m_Width / paramResolution.m_Height) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public Size toSize()
  {
    return new Size(this.m_Width, this.m_Height);
  }
  
  public String toString()
  {
    AspectRatio localAspectRatio = getAspectRatio();
    if (this.m_Fps != 0) {}
    for (String str = "@" + this.m_Fps + "Fps"; localAspectRatio != AspectRatio.UNKNOWN; str = "") {
      return this.m_Width + "x" + this.m_Height + "(" + localAspectRatio + ", " + getMegaPixelsDescription() + ")" + str;
    }
    return this.m_Width + "x" + this.m_Height + "(" + getMegaPixelsDescription() + ")" + str;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/Resolution.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */