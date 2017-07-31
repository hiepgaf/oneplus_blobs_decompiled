package com.oneplus.camera.util;

import android.content.Context;

public class StringUtils
{
  public static String formatTime(Context paramContext, long paramLong)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    long l1 = paramLong / 3600L;
    long l2 = paramLong - 3600L * l1;
    paramLong = l2 / 60L;
    l2 -= 60L * paramLong;
    if (l1 > 1L)
    {
      localStringBuffer.append(String.format(paramContext.getString(2131558514), new Object[] { Long.valueOf(l1) }));
      if (paramLong <= 1L) {
        break label212;
      }
      if (localStringBuffer.length() > 0) {
        localStringBuffer.append(' ');
      }
      localStringBuffer.append(String.format(paramContext.getString(2131558516), new Object[] { Long.valueOf(paramLong) }));
      label121:
      if (l2 <= 1L) {
        break label278;
      }
      if (localStringBuffer.length() > 0) {
        localStringBuffer.append(' ');
      }
      localStringBuffer.append(String.format(paramContext.getString(2131558518), new Object[] { Long.valueOf(l2) }));
    }
    for (;;)
    {
      return localStringBuffer.toString();
      if (l1 <= 0L) {
        break;
      }
      localStringBuffer.append(String.format(paramContext.getString(2131558513), new Object[] { Long.valueOf(l1) }));
      break;
      label212:
      if ((paramLong <= 0L) && ((localStringBuffer.length() <= 0) || (l2 <= 0L))) {
        break label121;
      }
      if (localStringBuffer.length() > 0) {
        localStringBuffer.append(' ');
      }
      localStringBuffer.append(String.format(paramContext.getString(2131558515), new Object[] { Long.valueOf(paramLong) }));
      break label121;
      label278:
      if (l2 > 0L)
      {
        if (localStringBuffer.length() > 0) {
          localStringBuffer.append(' ');
        }
        localStringBuffer.append(String.format(paramContext.getString(2131558517), new Object[] { Long.valueOf(l2) }));
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/util/StringUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */