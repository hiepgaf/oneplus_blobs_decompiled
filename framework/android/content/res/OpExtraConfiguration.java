package android.content.res;

import android.os.Parcel;

public class OpExtraConfiguration
  implements Comparable
{
  public static final int THEME_OLD_SKIN_CHANGED = 134217728;
  private final boolean DEBUG = true;
  private final String TAG = "OpExtraConfiguration";
  public int mThemeChanged;
  
  public static boolean needNewResources(int paramInt)
  {
    return (0x8000000 & paramInt) != 0;
  }
  
  public int compareTo(OpExtraConfiguration paramOpExtraConfiguration)
  {
    return this.mThemeChanged - paramOpExtraConfiguration.mThemeChanged;
  }
  
  public int compareTo(Object paramObject)
  {
    return compareTo((OpExtraConfiguration)paramObject);
  }
  
  public int diff(OpExtraConfiguration paramOpExtraConfiguration)
  {
    int j = 0;
    int i = j;
    if (paramOpExtraConfiguration.mThemeChanged > 0)
    {
      i = j;
      if (this.mThemeChanged != paramOpExtraConfiguration.mThemeChanged) {
        i = 134217728;
      }
    }
    return i;
  }
  
  public int hashCode()
  {
    return this.mThemeChanged;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.mThemeChanged = paramParcel.readInt();
  }
  
  public void setTo(OpExtraConfiguration paramOpExtraConfiguration)
  {
    this.mThemeChanged = paramOpExtraConfiguration.mThemeChanged;
  }
  
  public void setToDefaults()
  {
    this.mThemeChanged = 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("mThemeChanged = ");
    localStringBuilder.append(this.mThemeChanged);
    return localStringBuilder.toString();
  }
  
  public int updateFrom(OpExtraConfiguration paramOpExtraConfiguration)
  {
    int j = 0;
    int i = j;
    if (paramOpExtraConfiguration.mThemeChanged > 0)
    {
      i = j;
      if (this.mThemeChanged != paramOpExtraConfiguration.mThemeChanged)
      {
        i = 134217728;
        this.mThemeChanged = paramOpExtraConfiguration.mThemeChanged;
      }
    }
    return i;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mThemeChanged);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/OpExtraConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */