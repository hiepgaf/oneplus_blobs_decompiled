package android.nfc;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class TechListParcel
  implements Parcelable
{
  public static final Parcelable.Creator<TechListParcel> CREATOR = new Parcelable.Creator()
  {
    public TechListParcel createFromParcel(Parcel paramAnonymousParcel)
    {
      int j = paramAnonymousParcel.readInt();
      String[][] arrayOfString = new String[j][];
      int i = 0;
      while (i < j)
      {
        arrayOfString[i] = paramAnonymousParcel.readStringArray();
        i += 1;
      }
      return new TechListParcel(arrayOfString);
    }
    
    public TechListParcel[] newArray(int paramAnonymousInt)
    {
      return new TechListParcel[paramAnonymousInt];
    }
  };
  private String[][] mTechLists;
  
  public TechListParcel(String[]... paramVarArgs)
  {
    this.mTechLists = paramVarArgs;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String[][] getTechLists()
  {
    return this.mTechLists;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = this.mTechLists.length;
    paramParcel.writeInt(i);
    paramInt = 0;
    while (paramInt < i)
    {
      paramParcel.writeStringArray(this.mTechLists[paramInt]);
      paramInt += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/TechListParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */