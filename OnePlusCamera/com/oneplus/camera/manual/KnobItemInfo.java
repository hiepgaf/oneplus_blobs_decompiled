package com.oneplus.camera.manual;

import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.List;

public class KnobItemInfo
  implements Comparable<KnobItemInfo>
{
  public final Drawable drawable;
  public boolean isSelected;
  public double rotationCenter;
  public double rotationLeft;
  public double rotationRight;
  public final String text;
  public final int tick;
  public final double value;
  
  public KnobItemInfo(Drawable paramDrawable, String paramString, int paramInt, double paramDouble)
  {
    this.drawable = paramDrawable;
    this.text = paramString;
    this.tick = paramInt;
    this.value = paramDouble;
  }
  
  public static List<KnobItemInfo> createItemList(Drawable[] paramArrayOfDrawable, String[] paramArrayOfString, int[] paramArrayOfInt, double[] paramArrayOfDouble)
  {
    if ((paramArrayOfDrawable == null) || (paramArrayOfString == null)) {}
    while ((paramArrayOfInt == null) || (paramArrayOfDouble == null)) {
      return null;
    }
    if ((paramArrayOfDrawable.length != paramArrayOfString.length) || (paramArrayOfString.length != paramArrayOfInt.length)) {}
    while (paramArrayOfInt.length != paramArrayOfDouble.length) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < paramArrayOfDrawable.length)
    {
      localArrayList.add(new KnobItemInfo(paramArrayOfDrawable[i], paramArrayOfString[i], paramArrayOfInt[i], paramArrayOfDouble[i]));
      i += 1;
    }
    return localArrayList;
  }
  
  public int compareTo(KnobItemInfo paramKnobItemInfo)
  {
    if (Math.abs(this.rotationCenter - paramKnobItemInfo.rotationCenter) < 0.01D) {
      return 0;
    }
    if (this.rotationCenter > paramKnobItemInfo.rotationCenter) {
      return 1;
    }
    return -1;
  }
  
  public String toString()
  {
    return "KnobItemInfo [Tick: " + this.tick + ", Text: " + this.text + ", Value: " + this.value + ", Rotation: " + this.rotationCenter + ", Rotation left: " + this.rotationLeft + ", Rotation right: " + this.rotationRight + "]";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/KnobItemInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */