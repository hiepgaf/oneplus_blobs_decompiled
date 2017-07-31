package android.app;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ResultInfo
  implements Parcelable
{
  public static final Parcelable.Creator<ResultInfo> CREATOR = new Parcelable.Creator()
  {
    public ResultInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ResultInfo(paramAnonymousParcel);
    }
    
    public ResultInfo[] newArray(int paramAnonymousInt)
    {
      return new ResultInfo[paramAnonymousInt];
    }
  };
  public final Intent mData;
  public final int mRequestCode;
  public final int mResultCode;
  public final String mResultWho;
  
  public ResultInfo(Parcel paramParcel)
  {
    this.mResultWho = paramParcel.readString();
    this.mRequestCode = paramParcel.readInt();
    this.mResultCode = paramParcel.readInt();
    if (paramParcel.readInt() != 0)
    {
      this.mData = ((Intent)Intent.CREATOR.createFromParcel(paramParcel));
      return;
    }
    this.mData = null;
  }
  
  public ResultInfo(String paramString, int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.mResultWho = paramString;
    this.mRequestCode = paramInt1;
    this.mResultCode = paramInt2;
    this.mData = paramIntent;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return "ResultInfo{who=" + this.mResultWho + ", request=" + this.mRequestCode + ", result=" + this.mResultCode + ", data=" + this.mData + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mResultWho);
    paramParcel.writeInt(this.mRequestCode);
    paramParcel.writeInt(this.mResultCode);
    if (this.mData != null)
    {
      paramParcel.writeInt(1);
      this.mData.writeToParcel(paramParcel, 0);
      return;
    }
    paramParcel.writeInt(0);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ResultInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */