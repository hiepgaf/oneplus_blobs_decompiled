package android.graphics;

import com.android.internal.util.XmlUtils;
import java.util.HashMap;
import java.util.Locale;

public class Color
{
  public static final int BLACK = -16777216;
  public static final int BLUE = -16776961;
  public static final int CYAN = -16711681;
  public static final int DKGRAY = -12303292;
  public static final int GRAY = -7829368;
  public static final int GREEN = -16711936;
  public static final int LTGRAY = -3355444;
  public static final int MAGENTA = -65281;
  public static final int RED = -65536;
  public static final int TRANSPARENT = 0;
  public static final int WHITE = -1;
  public static final int YELLOW = -256;
  private static final HashMap<String, Integer> sColorNameMap = new HashMap();
  
  static
  {
    sColorNameMap.put("black", Integer.valueOf(-16777216));
    sColorNameMap.put("darkgray", Integer.valueOf(-12303292));
    sColorNameMap.put("gray", Integer.valueOf(-7829368));
    sColorNameMap.put("lightgray", Integer.valueOf(-3355444));
    sColorNameMap.put("white", Integer.valueOf(-1));
    sColorNameMap.put("red", Integer.valueOf(-65536));
    sColorNameMap.put("green", Integer.valueOf(-16711936));
    sColorNameMap.put("blue", Integer.valueOf(-16776961));
    sColorNameMap.put("yellow", Integer.valueOf(65280));
    sColorNameMap.put("cyan", Integer.valueOf(-16711681));
    sColorNameMap.put("magenta", Integer.valueOf(-65281));
    sColorNameMap.put("aqua", Integer.valueOf(-16711681));
    sColorNameMap.put("fuchsia", Integer.valueOf(-65281));
    sColorNameMap.put("darkgrey", Integer.valueOf(-12303292));
    sColorNameMap.put("grey", Integer.valueOf(-7829368));
    sColorNameMap.put("lightgrey", Integer.valueOf(-3355444));
    sColorNameMap.put("lime", Integer.valueOf(-16711936));
    sColorNameMap.put("maroon", Integer.valueOf(-8388608));
    sColorNameMap.put("navy", Integer.valueOf(-16777088));
    sColorNameMap.put("olive", Integer.valueOf(-8355840));
    sColorNameMap.put("purple", Integer.valueOf(-8388480));
    sColorNameMap.put("silver", Integer.valueOf(-4144960));
    sColorNameMap.put("teal", Integer.valueOf(-16744320));
  }
  
  public static int HSVToColor(int paramInt, float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length < 3) {
      throw new RuntimeException("3 components required for hsv");
    }
    return nativeHSVToColor(paramInt, paramArrayOfFloat);
  }
  
  public static int HSVToColor(float[] paramArrayOfFloat)
  {
    return HSVToColor(255, paramArrayOfFloat);
  }
  
  public static void RGBToHSV(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length < 3) {
      throw new RuntimeException("3 components required for hsv");
    }
    nativeRGBToHSV(paramInt1, paramInt2, paramInt3, paramArrayOfFloat);
  }
  
  public static int alpha(int paramInt)
  {
    return paramInt >>> 24;
  }
  
  public static int argb(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return paramInt1 << 24 | paramInt2 << 16 | paramInt3 << 8 | paramInt4;
  }
  
  public static int blue(int paramInt)
  {
    return paramInt & 0xFF;
  }
  
  public static void colorToHSV(int paramInt, float[] paramArrayOfFloat)
  {
    RGBToHSV(paramInt >> 16 & 0xFF, paramInt >> 8 & 0xFF, paramInt & 0xFF, paramArrayOfFloat);
  }
  
  public static int getHtmlColor(String paramString)
  {
    Integer localInteger = (Integer)sColorNameMap.get(paramString.toLowerCase(Locale.ROOT));
    if (localInteger != null) {
      return localInteger.intValue();
    }
    try
    {
      int i = XmlUtils.convertValueToInt(paramString, -1);
      return i;
    }
    catch (NumberFormatException paramString) {}
    return -1;
  }
  
  public static int green(int paramInt)
  {
    return paramInt >> 8 & 0xFF;
  }
  
  public static float luminance(int paramInt)
  {
    double d1 = red(paramInt) / 255.0D;
    double d2;
    label48:
    double d3;
    if (d1 < 0.03928D)
    {
      d1 /= 12.92D;
      d2 = green(paramInt) / 255.0D;
      if (d2 >= 0.03928D) {
        break label115;
      }
      d2 /= 12.92D;
      d3 = blue(paramInt) / 255.0D;
      if (d3 >= 0.03928D) {
        break label134;
      }
    }
    label115:
    label134:
    for (d3 /= 12.92D;; d3 = Math.pow((0.055D + d3) / 1.055D, 2.4D))
    {
      return (float)(0.2126D * d1 + 0.7152D * d2 + 0.0722D * d3);
      d1 = Math.pow((0.055D + d1) / 1.055D, 2.4D);
      break;
      d2 = Math.pow((0.055D + d2) / 1.055D, 2.4D);
      break label48;
    }
  }
  
  private static native int nativeHSVToColor(int paramInt, float[] paramArrayOfFloat);
  
  private static native void nativeRGBToHSV(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat);
  
  public static int parseColor(String paramString)
  {
    if (paramString.charAt(0) == '#')
    {
      long l = Long.parseLong(paramString.substring(1), 16);
      if (paramString.length() == 7) {
        l |= 0xFFFFFFFFFF000000;
      }
      while (paramString.length() == 9) {
        return (int)l;
      }
      throw new IllegalArgumentException("Unknown color");
    }
    paramString = (Integer)sColorNameMap.get(paramString.toLowerCase(Locale.ROOT));
    if (paramString != null) {
      return paramString.intValue();
    }
    throw new IllegalArgumentException("Unknown color");
  }
  
  public static int red(int paramInt)
  {
    return paramInt >> 16 & 0xFF;
  }
  
  public static int rgb(int paramInt1, int paramInt2, int paramInt3)
  {
    return paramInt1 << 16 | 0xFF000000 | paramInt2 << 8 | paramInt3;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/Color.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */