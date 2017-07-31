package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class InstrumentationInfo
  extends PackageItemInfo
  implements Parcelable
{
  public static final Parcelable.Creator<InstrumentationInfo> CREATOR = new Parcelable.Creator()
  {
    public InstrumentationInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new InstrumentationInfo(paramAnonymousParcel, null);
    }
    
    public InstrumentationInfo[] newArray(int paramAnonymousInt)
    {
      return new InstrumentationInfo[paramAnonymousInt];
    }
  };
  public String credentialProtectedDataDir;
  public String dataDir;
  public String deviceProtectedDataDir;
  public boolean functionalTest;
  public boolean handleProfiling;
  public String nativeLibraryDir;
  public String publicSourceDir;
  public String secondaryNativeLibraryDir;
  public String sourceDir;
  public String[] splitPublicSourceDirs;
  public String[] splitSourceDirs;
  public String targetPackage;
  
  public InstrumentationInfo() {}
  
  public InstrumentationInfo(InstrumentationInfo paramInstrumentationInfo)
  {
    super(paramInstrumentationInfo);
    this.targetPackage = paramInstrumentationInfo.targetPackage;
    this.sourceDir = paramInstrumentationInfo.sourceDir;
    this.publicSourceDir = paramInstrumentationInfo.publicSourceDir;
    this.splitSourceDirs = paramInstrumentationInfo.splitSourceDirs;
    this.splitPublicSourceDirs = paramInstrumentationInfo.splitPublicSourceDirs;
    this.dataDir = paramInstrumentationInfo.dataDir;
    this.deviceProtectedDataDir = paramInstrumentationInfo.deviceProtectedDataDir;
    this.credentialProtectedDataDir = paramInstrumentationInfo.credentialProtectedDataDir;
    this.nativeLibraryDir = paramInstrumentationInfo.nativeLibraryDir;
    this.secondaryNativeLibraryDir = paramInstrumentationInfo.secondaryNativeLibraryDir;
    this.handleProfiling = paramInstrumentationInfo.handleProfiling;
    this.functionalTest = paramInstrumentationInfo.functionalTest;
  }
  
  private InstrumentationInfo(Parcel paramParcel)
  {
    super(paramParcel);
    this.targetPackage = paramParcel.readString();
    this.sourceDir = paramParcel.readString();
    this.publicSourceDir = paramParcel.readString();
    this.splitSourceDirs = paramParcel.readStringArray();
    this.splitPublicSourceDirs = paramParcel.readStringArray();
    this.dataDir = paramParcel.readString();
    this.deviceProtectedDataDir = paramParcel.readString();
    this.credentialProtectedDataDir = paramParcel.readString();
    this.nativeLibraryDir = paramParcel.readString();
    this.secondaryNativeLibraryDir = paramParcel.readString();
    if (paramParcel.readInt() != 0)
    {
      bool1 = true;
      this.handleProfiling = bool1;
      if (paramParcel.readInt() == 0) {
        break label121;
      }
    }
    label121:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.functionalTest = bool1;
      return;
      bool1 = false;
      break;
    }
  }
  
  public void copyTo(ApplicationInfo paramApplicationInfo)
  {
    paramApplicationInfo.packageName = this.packageName;
    paramApplicationInfo.sourceDir = this.sourceDir;
    paramApplicationInfo.publicSourceDir = this.publicSourceDir;
    paramApplicationInfo.splitSourceDirs = this.splitSourceDirs;
    paramApplicationInfo.splitPublicSourceDirs = this.splitPublicSourceDirs;
    paramApplicationInfo.dataDir = this.dataDir;
    paramApplicationInfo.deviceProtectedDataDir = this.deviceProtectedDataDir;
    paramApplicationInfo.credentialProtectedDataDir = this.credentialProtectedDataDir;
    paramApplicationInfo.nativeLibraryDir = this.nativeLibraryDir;
    paramApplicationInfo.secondaryNativeLibraryDir = this.secondaryNativeLibraryDir;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return "InstrumentationInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.packageName + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 0;
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.targetPackage);
    paramParcel.writeString(this.sourceDir);
    paramParcel.writeString(this.publicSourceDir);
    paramParcel.writeStringArray(this.splitSourceDirs);
    paramParcel.writeStringArray(this.splitPublicSourceDirs);
    paramParcel.writeString(this.dataDir);
    paramParcel.writeString(this.deviceProtectedDataDir);
    paramParcel.writeString(this.credentialProtectedDataDir);
    paramParcel.writeString(this.nativeLibraryDir);
    paramParcel.writeString(this.secondaryNativeLibraryDir);
    if (!this.handleProfiling)
    {
      paramInt = 0;
      paramParcel.writeInt(paramInt);
      if (this.functionalTest) {
        break label122;
      }
    }
    label122:
    for (paramInt = i;; paramInt = 1)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramInt = 1;
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/InstrumentationInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */