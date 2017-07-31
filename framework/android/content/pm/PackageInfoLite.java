package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class PackageInfoLite
  implements Parcelable
{
  public static final Parcelable.Creator<PackageInfoLite> CREATOR = new Parcelable.Creator()
  {
    public PackageInfoLite createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PackageInfoLite(paramAnonymousParcel, null);
    }
    
    public PackageInfoLite[] newArray(int paramAnonymousInt)
    {
      return new PackageInfoLite[paramAnonymousInt];
    }
  };
  public int baseRevisionCode;
  public int installLocation;
  public boolean multiArch;
  public String oplibDependencyStr;
  public String packageName;
  public int recommendedInstallLocation;
  public String[] splitNames;
  public int[] splitRevisionCodes;
  public VerifierInfo[] verifiers;
  public int versionCode;
  
  public PackageInfoLite() {}
  
  private PackageInfoLite(Parcel paramParcel)
  {
    this.packageName = paramParcel.readString();
    this.splitNames = paramParcel.createStringArray();
    this.versionCode = paramParcel.readInt();
    this.baseRevisionCode = paramParcel.readInt();
    this.splitRevisionCodes = paramParcel.createIntArray();
    this.recommendedInstallLocation = paramParcel.readInt();
    this.installLocation = paramParcel.readInt();
    boolean bool;
    int i;
    if (paramParcel.readInt() != 0)
    {
      bool = true;
      this.multiArch = bool;
      i = paramParcel.readInt();
      if (i != 0) {
        break label105;
      }
      this.verifiers = new VerifierInfo[0];
    }
    for (;;)
    {
      this.oplibDependencyStr = paramParcel.readString();
      return;
      bool = false;
      break;
      label105:
      this.verifiers = new VerifierInfo[i];
      paramParcel.readTypedArray(this.verifiers, VerifierInfo.CREATOR);
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return "PackageInfoLite{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.packageName + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.packageName);
    paramParcel.writeStringArray(this.splitNames);
    paramParcel.writeInt(this.versionCode);
    paramParcel.writeInt(this.baseRevisionCode);
    paramParcel.writeIntArray(this.splitRevisionCodes);
    paramParcel.writeInt(this.recommendedInstallLocation);
    paramParcel.writeInt(this.installLocation);
    int i;
    if (this.multiArch)
    {
      i = 1;
      paramParcel.writeInt(i);
      if ((this.verifiers != null) && (this.verifiers.length != 0)) {
        break label104;
      }
      paramParcel.writeInt(0);
    }
    for (;;)
    {
      paramParcel.writeString(this.oplibDependencyStr);
      return;
      i = 0;
      break;
      label104:
      paramParcel.writeInt(this.verifiers.length);
      paramParcel.writeTypedArray(this.verifiers, paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PackageInfoLite.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */