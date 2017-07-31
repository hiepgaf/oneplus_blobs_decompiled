package android.content;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.Objects;

public class RestrictionEntry
  implements Parcelable
{
  public static final Parcelable.Creator<RestrictionEntry> CREATOR = new Parcelable.Creator()
  {
    public RestrictionEntry createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RestrictionEntry(paramAnonymousParcel);
    }
    
    public RestrictionEntry[] newArray(int paramAnonymousInt)
    {
      return new RestrictionEntry[paramAnonymousInt];
    }
  };
  public static final int TYPE_BOOLEAN = 1;
  public static final int TYPE_BUNDLE = 7;
  public static final int TYPE_BUNDLE_ARRAY = 8;
  public static final int TYPE_CHOICE = 2;
  public static final int TYPE_CHOICE_LEVEL = 3;
  public static final int TYPE_INTEGER = 5;
  public static final int TYPE_MULTI_SELECT = 4;
  public static final int TYPE_NULL = 0;
  public static final int TYPE_STRING = 6;
  private String[] mChoiceEntries;
  private String[] mChoiceValues;
  private String mCurrentValue;
  private String[] mCurrentValues;
  private String mDescription;
  private String mKey;
  private RestrictionEntry[] mRestrictions;
  private String mTitle;
  private int mType;
  
  public RestrictionEntry(int paramInt, String paramString)
  {
    this.mType = paramInt;
    this.mKey = paramString;
  }
  
  public RestrictionEntry(Parcel paramParcel)
  {
    this.mType = paramParcel.readInt();
    this.mKey = paramParcel.readString();
    this.mTitle = paramParcel.readString();
    this.mDescription = paramParcel.readString();
    this.mChoiceEntries = paramParcel.readStringArray();
    this.mChoiceValues = paramParcel.readStringArray();
    this.mCurrentValue = paramParcel.readString();
    this.mCurrentValues = paramParcel.readStringArray();
    paramParcel = paramParcel.readParcelableArray(null);
    if (paramParcel != null)
    {
      this.mRestrictions = new RestrictionEntry[paramParcel.length];
      int i = 0;
      while (i < paramParcel.length)
      {
        this.mRestrictions[i] = ((RestrictionEntry)paramParcel[i]);
        i += 1;
      }
    }
  }
  
  public RestrictionEntry(String paramString, int paramInt)
  {
    this.mKey = paramString;
    this.mType = 5;
    setIntValue(paramInt);
  }
  
  public RestrictionEntry(String paramString1, String paramString2)
  {
    this.mKey = paramString1;
    this.mType = 2;
    this.mCurrentValue = paramString2;
  }
  
  public RestrictionEntry(String paramString, boolean paramBoolean)
  {
    this.mKey = paramString;
    this.mType = 1;
    setSelectedState(paramBoolean);
  }
  
  private RestrictionEntry(String paramString, RestrictionEntry[] paramArrayOfRestrictionEntry, boolean paramBoolean)
  {
    this.mKey = paramString;
    if (paramBoolean)
    {
      this.mType = 8;
      if (paramArrayOfRestrictionEntry != null)
      {
        int i = 0;
        int j = paramArrayOfRestrictionEntry.length;
        while (i < j)
        {
          if (paramArrayOfRestrictionEntry[i].getType() != 7) {
            throw new IllegalArgumentException("bundle_array restriction can only have nested restriction entries of type bundle");
          }
          i += 1;
        }
      }
    }
    else
    {
      this.mType = 7;
    }
    setRestrictions(paramArrayOfRestrictionEntry);
  }
  
  public RestrictionEntry(String paramString, String[] paramArrayOfString)
  {
    this.mKey = paramString;
    this.mType = 4;
    this.mCurrentValues = paramArrayOfString;
  }
  
  public static RestrictionEntry createBundleArrayEntry(String paramString, RestrictionEntry[] paramArrayOfRestrictionEntry)
  {
    return new RestrictionEntry(paramString, paramArrayOfRestrictionEntry, true);
  }
  
  public static RestrictionEntry createBundleEntry(String paramString, RestrictionEntry[] paramArrayOfRestrictionEntry)
  {
    return new RestrictionEntry(paramString, paramArrayOfRestrictionEntry, false);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof RestrictionEntry)) {
      return false;
    }
    paramObject = (RestrictionEntry)paramObject;
    if ((this.mType == ((RestrictionEntry)paramObject).mType) && (this.mKey.equals(((RestrictionEntry)paramObject).mKey)))
    {
      if ((this.mCurrentValues == null) && (((RestrictionEntry)paramObject).mCurrentValues == null) && (this.mRestrictions == null) && (((RestrictionEntry)paramObject).mRestrictions == null) && (Objects.equals(this.mCurrentValue, ((RestrictionEntry)paramObject).mCurrentValue))) {
        return true;
      }
    }
    else {
      return false;
    }
    if ((this.mCurrentValue == null) && (((RestrictionEntry)paramObject).mCurrentValue == null) && (this.mRestrictions == null) && (((RestrictionEntry)paramObject).mRestrictions == null) && (Arrays.equals(this.mCurrentValues, ((RestrictionEntry)paramObject).mCurrentValues))) {
      return true;
    }
    return (this.mCurrentValue == null) && (((RestrictionEntry)paramObject).mCurrentValue == null) && (this.mCurrentValue == null) && (((RestrictionEntry)paramObject).mCurrentValue == null) && (Arrays.equals(this.mRestrictions, ((RestrictionEntry)paramObject).mRestrictions));
  }
  
  public String[] getAllSelectedStrings()
  {
    return this.mCurrentValues;
  }
  
  public String[] getChoiceEntries()
  {
    return this.mChoiceEntries;
  }
  
  public String[] getChoiceValues()
  {
    return this.mChoiceValues;
  }
  
  public String getDescription()
  {
    return this.mDescription;
  }
  
  public int getIntValue()
  {
    return Integer.parseInt(this.mCurrentValue);
  }
  
  public String getKey()
  {
    return this.mKey;
  }
  
  public RestrictionEntry[] getRestrictions()
  {
    return this.mRestrictions;
  }
  
  public boolean getSelectedState()
  {
    return Boolean.parseBoolean(this.mCurrentValue);
  }
  
  public String getSelectedString()
  {
    return this.mCurrentValue;
  }
  
  public String getTitle()
  {
    return this.mTitle;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public int hashCode()
  {
    int i = this.mKey.hashCode() + 527;
    int j;
    if (this.mCurrentValue != null) {
      j = i * 31 + this.mCurrentValue.hashCode();
    }
    do
    {
      return j;
      if (this.mCurrentValues != null)
      {
        String[] arrayOfString = this.mCurrentValues;
        int k = 0;
        int m = arrayOfString.length;
        for (;;)
        {
          j = i;
          if (k >= m) {
            break;
          }
          String str = arrayOfString[k];
          j = i;
          if (str != null) {
            j = i * 31 + str.hashCode();
          }
          k += 1;
          i = j;
        }
      }
      j = i;
    } while (this.mRestrictions == null);
    return i * 31 + Arrays.hashCode(this.mRestrictions);
  }
  
  public void setAllSelectedStrings(String[] paramArrayOfString)
  {
    this.mCurrentValues = paramArrayOfString;
  }
  
  public void setChoiceEntries(Context paramContext, int paramInt)
  {
    this.mChoiceEntries = paramContext.getResources().getStringArray(paramInt);
  }
  
  public void setChoiceEntries(String[] paramArrayOfString)
  {
    this.mChoiceEntries = paramArrayOfString;
  }
  
  public void setChoiceValues(Context paramContext, int paramInt)
  {
    this.mChoiceValues = paramContext.getResources().getStringArray(paramInt);
  }
  
  public void setChoiceValues(String[] paramArrayOfString)
  {
    this.mChoiceValues = paramArrayOfString;
  }
  
  public void setDescription(String paramString)
  {
    this.mDescription = paramString;
  }
  
  public void setIntValue(int paramInt)
  {
    this.mCurrentValue = Integer.toString(paramInt);
  }
  
  public void setRestrictions(RestrictionEntry[] paramArrayOfRestrictionEntry)
  {
    this.mRestrictions = paramArrayOfRestrictionEntry;
  }
  
  public void setSelectedState(boolean paramBoolean)
  {
    this.mCurrentValue = Boolean.toString(paramBoolean);
  }
  
  public void setSelectedString(String paramString)
  {
    this.mCurrentValue = paramString;
  }
  
  public void setTitle(String paramString)
  {
    this.mTitle = paramString;
  }
  
  public void setType(int paramInt)
  {
    this.mType = paramInt;
  }
  
  public String toString()
  {
    return "RestrictionEntry{mType=" + this.mType + ", mKey='" + this.mKey + '\'' + ", mTitle='" + this.mTitle + '\'' + ", mDescription='" + this.mDescription + '\'' + ", mChoiceEntries=" + Arrays.toString(this.mChoiceEntries) + ", mChoiceValues=" + Arrays.toString(this.mChoiceValues) + ", mCurrentValue='" + this.mCurrentValue + '\'' + ", mCurrentValues=" + Arrays.toString(this.mCurrentValues) + ", mRestrictions=" + Arrays.toString(this.mRestrictions) + '}';
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    paramParcel.writeString(this.mKey);
    paramParcel.writeString(this.mTitle);
    paramParcel.writeString(this.mDescription);
    paramParcel.writeStringArray(this.mChoiceEntries);
    paramParcel.writeStringArray(this.mChoiceValues);
    paramParcel.writeString(this.mCurrentValue);
    paramParcel.writeStringArray(this.mCurrentValues);
    paramParcel.writeParcelableArray(this.mRestrictions, 0);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/RestrictionEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */