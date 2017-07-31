package android.service.carrier;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class CarrierIdentifier
  implements Parcelable
{
  public static final Parcelable.Creator<CarrierIdentifier> CREATOR = new Parcelable.Creator()
  {
    public CarrierIdentifier createFromParcel(Parcel paramAnonymousParcel)
    {
      return new CarrierIdentifier(paramAnonymousParcel);
    }
    
    public CarrierIdentifier[] newArray(int paramAnonymousInt)
    {
      return new CarrierIdentifier[paramAnonymousInt];
    }
  };
  private String mGid1;
  private String mGid2;
  private String mImsi;
  private String mMcc;
  private String mMnc;
  private String mSpn;
  
  public CarrierIdentifier(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public CarrierIdentifier(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    this.mMcc = paramString1;
    this.mMnc = paramString2;
    this.mSpn = paramString3;
    this.mImsi = paramString4;
    this.mGid1 = paramString5;
    this.mGid2 = paramString6;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getGid1()
  {
    return this.mGid1;
  }
  
  public String getGid2()
  {
    return this.mGid2;
  }
  
  public String getImsi()
  {
    return this.mImsi;
  }
  
  public String getMcc()
  {
    return this.mMcc;
  }
  
  public String getMnc()
  {
    return this.mMnc;
  }
  
  public String getSpn()
  {
    return this.mSpn;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.mMcc = paramParcel.readString();
    this.mMnc = paramParcel.readString();
    this.mSpn = paramParcel.readString();
    this.mImsi = paramParcel.readString();
    this.mGid1 = paramParcel.readString();
    this.mGid2 = paramParcel.readString();
  }
  
  public String toString()
  {
    return "CarrierIdentifier{mcc=" + this.mMcc + ",mnc=" + this.mMnc + ",spn=" + this.mSpn + ",imsi=" + this.mImsi + ",gid1=" + this.mGid1 + ",gid2=" + this.mGid2 + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mMcc);
    paramParcel.writeString(this.mMnc);
    paramParcel.writeString(this.mSpn);
    paramParcel.writeString(this.mImsi);
    paramParcel.writeString(this.mGid1);
    paramParcel.writeString(this.mGid2);
  }
  
  public static abstract interface MatchType
  {
    public static final int ALL = 0;
    public static final int GID1 = 3;
    public static final int GID2 = 4;
    public static final int IMSI_PREFIX = 2;
    public static final int SPN = 1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/carrier/CarrierIdentifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */