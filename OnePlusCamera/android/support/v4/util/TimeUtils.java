package android.support.v4.util;

import java.io.PrintWriter;

public class TimeUtils
{
  public static final int HUNDRED_DAY_FIELD_LEN = 19;
  private static final int SECONDS_PER_DAY = 86400;
  private static final int SECONDS_PER_HOUR = 3600;
  private static final int SECONDS_PER_MINUTE = 60;
  private static char[] sFormatStr = new char[24];
  private static final Object sFormatSync = new Object();
  
  private static int accumField(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    if (paramInt1 > 99) {
      return paramInt2 + 3;
    }
    if (!paramBoolean) {}
    for (;;)
    {
      if (paramInt1 <= 9) {
        break label32;
      }
      return paramInt2 + 2;
      if (paramInt3 >= 3) {
        break;
      }
    }
    label32:
    if (!paramBoolean) {
      label36:
      if (!paramBoolean) {
        break label52;
      }
    }
    label52:
    while (paramInt1 > 0)
    {
      return paramInt2 + 1;
      if (paramInt3 >= 2) {
        break;
      }
      break label36;
    }
    return 0;
  }
  
  public static void formatDuration(long paramLong1, long paramLong2, PrintWriter paramPrintWriter)
  {
    if (paramLong1 == 0L)
    {
      paramPrintWriter.print("--");
      return;
    }
    formatDuration(paramLong1 - paramLong2, paramPrintWriter, 0);
  }
  
  public static void formatDuration(long paramLong, PrintWriter paramPrintWriter)
  {
    formatDuration(paramLong, paramPrintWriter, 0);
  }
  
  public static void formatDuration(long paramLong, PrintWriter paramPrintWriter, int paramInt)
  {
    synchronized (sFormatSync)
    {
      paramInt = formatDurationLocked(paramLong, paramInt);
      paramPrintWriter.print(new String(sFormatStr, 0, paramInt));
      return;
    }
  }
  
  public static void formatDuration(long paramLong, StringBuilder paramStringBuilder)
  {
    synchronized (sFormatSync)
    {
      int i = formatDurationLocked(paramLong, 0);
      paramStringBuilder.append(sFormatStr, 0, i);
      return;
    }
  }
  
  private static int formatDurationLocked(long paramLong, int paramInt)
  {
    char[] arrayOfChar;
    if (sFormatStr.length >= paramInt)
    {
      arrayOfChar = sFormatStr;
      if (paramLong != 0L) {}
    }
    else
    {
      for (;;)
      {
        if (paramInt - 1 <= 0)
        {
          arrayOfChar[0] = '0';
          return 1;
          sFormatStr = new char[paramInt];
          break;
        }
        arrayOfChar[0] = ' ';
      }
    }
    int i;
    int m;
    label67:
    int i4;
    int j;
    label95:
    int n;
    label105:
    int i1;
    int k;
    label117:
    int i3;
    label144:
    boolean bool;
    if (paramLong <= 0L)
    {
      i = 1;
      if (i != 0) {
        break label288;
      }
      m = 43;
      i4 = (int)(paramLong % 1000L);
      i = (int)Math.floor(paramLong / 1000L);
      j = 0;
      if (i > 86400) {
        break label298;
      }
      if (i > 3600) {
        break label315;
      }
      n = 0;
      if (i > 60) {
        break label338;
      }
      i1 = 0;
      k = i;
      if (paramInt != 0) {
        break label360;
      }
      i3 = 0;
      arrayOfChar[i3] = ((char)m);
      m = i3 + 1;
      if (paramInt != 0) {
        break label506;
      }
      paramInt = 0;
      j = printField(arrayOfChar, j, 'd', m, false, 0);
      if (j != m) {
        break label511;
      }
      bool = false;
      label169:
      if (paramInt != 0) {
        break label517;
      }
      i = 0;
      label175:
      j = printField(arrayOfChar, n, 'h', j, bool, i);
      if (j != m) {
        break label522;
      }
      bool = false;
      label201:
      if (paramInt != 0) {
        break label528;
      }
      i = 0;
      label207:
      j = printField(arrayOfChar, i1, 'm', j, bool, i);
      if (j != m) {
        break label533;
      }
      bool = false;
      label233:
      if (paramInt != 0) {
        break label539;
      }
      i = 0;
      label239:
      i = printField(arrayOfChar, k, 's', j, bool, i);
      if (paramInt != 0) {
        break label544;
      }
    }
    label258:
    for (paramInt = 0;; paramInt = 3)
    {
      paramInt = printField(arrayOfChar, i4, 'm', i, true, paramInt);
      arrayOfChar[paramInt] = 's';
      return paramInt + 1;
      i = 0;
      break;
      label288:
      paramLong = -paramLong;
      m = 45;
      break label67;
      label298:
      j = i / 86400;
      i -= 86400 * j;
      break label95;
      label315:
      k = i / 3600;
      n = k;
      i -= k * 3600;
      break label105;
      label338:
      k = i / 60;
      i1 = k;
      k = i - k * 60;
      break label117;
      label360:
      i = accumField(j, 1, false, 0);
      label376:
      label395:
      label414:
      int i2;
      if (i <= 0)
      {
        bool = false;
        i += accumField(n, 1, bool, 2);
        if (i > 0) {
          break label489;
        }
        bool = false;
        i += accumField(i1, 1, bool, 2);
        if (i > 0) {
          break label495;
        }
        bool = false;
        i2 = i + accumField(k, 1, bool, 2);
        if (i2 > 0) {
          break label501;
        }
      }
      label489:
      label495:
      label501:
      for (i = 0;; i = 3)
      {
        i3 = accumField(i4, 2, true, i);
        i = 0;
        i2 = i3 + 1 + i2;
        for (;;)
        {
          i3 = i;
          if (i2 >= paramInt) {
            break;
          }
          arrayOfChar[i] = ' ';
          i2 += 1;
          i += 1;
        }
        bool = true;
        break label376;
        bool = true;
        break label395;
        bool = true;
        break label414;
      }
      label506:
      paramInt = 1;
      break label144;
      label511:
      bool = true;
      break label169;
      label517:
      i = 2;
      break label175;
      label522:
      bool = true;
      break label201;
      label528:
      i = 2;
      break label207;
      label533:
      bool = true;
      break label233;
      label539:
      i = 2;
      break label239;
      label544:
      if (i == m) {
        break label258;
      }
    }
  }
  
  private static int printField(char[] paramArrayOfChar, int paramInt1, char paramChar, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    label10:
    int i;
    if (paramBoolean)
    {
      if (paramBoolean) {
        break label88;
      }
      if (paramInt1 > 99) {
        break label94;
      }
      i = paramInt2;
      label19:
      if (paramBoolean) {
        break label126;
      }
      label24:
      if (paramInt1 <= 9) {
        break label135;
      }
      label30:
      paramInt2 = paramInt1 / 10;
      paramArrayOfChar[i] = ((char)(char)(paramInt2 + 48));
      i += 1;
      paramInt1 -= paramInt2 * 10;
    }
    for (;;)
    {
      paramArrayOfChar[i] = ((char)(char)(paramInt1 + 48));
      paramInt1 = i + 1;
      paramArrayOfChar[paramInt1] = ((char)paramChar);
      return paramInt1 + 1;
      if (paramInt1 > 0) {
        break;
      }
      return paramInt2;
      label88:
      if (paramInt3 < 3) {
        break label10;
      }
      label94:
      int j = paramInt1 / 100;
      paramArrayOfChar[paramInt2] = ((char)(char)(j + 48));
      i = paramInt2 + 1;
      paramInt1 -= j * 100;
      break label19;
      label126:
      if (paramInt3 < 2) {
        break label24;
      }
      break label30;
      label135:
      if (paramInt2 != i) {
        break label30;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/util/TimeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */