package com.oneplus.util;

import android.util.Size;
import java.util.Comparator;

public class SizeComparator
  implements Comparator<Size>
{
  public static final SizeComparator DEFAULT = new SizeComparator();
  
  public int compare(Size paramSize1, Size paramSize2)
  {
    int i;
    if (paramSize1 != null)
    {
      i = paramSize1.getWidth() * paramSize1.getHeight();
      if (paramSize2 == null) {
        break label39;
      }
    }
    label39:
    for (int j = paramSize2.getWidth() * paramSize2.getHeight();; j = 0)
    {
      return i - j;
      i = 0;
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/util/SizeComparator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */