package android.content.res;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ObbInfo
  implements Parcelable
{
  public static final Parcelable.Creator<ObbInfo> CREATOR = new Parcelable.Creator()
  {
    public ObbInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ObbInfo(paramAnonymousParcel, null);
    }
    
    public ObbInfo[] newArray(int paramAnonymousInt)
    {
      return new ObbInfo[paramAnonymousInt];
    }
  };
  public static final int OBB_OVERLAY = 1;
  public String filename;
  public int flags;
  public String packageName;
  public byte[] salt;
  public int version;
  
  ObbInfo() {}
  
  private ObbInfo(Parcel paramParcel)
  {
    this.filename = paramParcel.readString();
    this.packageName = paramParcel.readString();
    this.version = paramParcel.readInt();
    this.flags = paramParcel.readInt();
    this.salt = paramParcel.createByteArray();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ObbInfo{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" packageName=");
    localStringBuilder.append(this.packageName);
    localStringBuilder.append(",version=");
    localStringBuilder.append(this.version);
    localStringBuilder.append(",flags=");
    localStringBuilder.append(this.flags);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.filename);
    paramParcel.writeString(this.packageName);
    paramParcel.writeInt(this.version);
    paramParcel.writeInt(this.flags);
    paramParcel.writeByteArray(this.salt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/ObbInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */