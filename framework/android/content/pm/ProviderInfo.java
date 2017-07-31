package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PatternMatcher;
import android.util.Printer;

public final class ProviderInfo
  extends ComponentInfo
  implements Parcelable
{
  public static final Parcelable.Creator<ProviderInfo> CREATOR = new Parcelable.Creator()
  {
    public ProviderInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ProviderInfo(paramAnonymousParcel, null);
    }
    
    public ProviderInfo[] newArray(int paramAnonymousInt)
    {
      return new ProviderInfo[paramAnonymousInt];
    }
  };
  public static final int FLAG_SINGLE_USER = 1073741824;
  public String authority = null;
  public int flags = 0;
  public boolean grantUriPermissions = false;
  public int initOrder = 0;
  @Deprecated
  public boolean isSyncable = false;
  public boolean multiprocess = false;
  public PathPermission[] pathPermissions = null;
  public String readPermission = null;
  public PatternMatcher[] uriPermissionPatterns = null;
  public String writePermission = null;
  
  public ProviderInfo() {}
  
  public ProviderInfo(ProviderInfo paramProviderInfo)
  {
    super(paramProviderInfo);
    this.authority = paramProviderInfo.authority;
    this.readPermission = paramProviderInfo.readPermission;
    this.writePermission = paramProviderInfo.writePermission;
    this.grantUriPermissions = paramProviderInfo.grantUriPermissions;
    this.uriPermissionPatterns = paramProviderInfo.uriPermissionPatterns;
    this.pathPermissions = paramProviderInfo.pathPermissions;
    this.multiprocess = paramProviderInfo.multiprocess;
    this.initOrder = paramProviderInfo.initOrder;
    this.flags = paramProviderInfo.flags;
    this.isSyncable = paramProviderInfo.isSyncable;
  }
  
  private ProviderInfo(Parcel paramParcel)
  {
    super(paramParcel);
    this.authority = paramParcel.readString();
    this.readPermission = paramParcel.readString();
    this.writePermission = paramParcel.readString();
    if (paramParcel.readInt() != 0)
    {
      bool1 = true;
      this.grantUriPermissions = bool1;
      this.uriPermissionPatterns = ((PatternMatcher[])paramParcel.createTypedArray(PatternMatcher.CREATOR));
      this.pathPermissions = ((PathPermission[])paramParcel.createTypedArray(PathPermission.CREATOR));
      if (paramParcel.readInt() == 0) {
        break label173;
      }
      bool1 = true;
      label132:
      this.multiprocess = bool1;
      this.initOrder = paramParcel.readInt();
      this.flags = paramParcel.readInt();
      if (paramParcel.readInt() == 0) {
        break label178;
      }
    }
    label173:
    label178:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.isSyncable = bool1;
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label132;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    dump(paramPrinter, paramString, 3);
  }
  
  public void dump(Printer paramPrinter, String paramString, int paramInt)
  {
    super.dumpFront(paramPrinter, paramString);
    paramPrinter.println(paramString + "authority=" + this.authority);
    paramPrinter.println(paramString + "flags=0x" + Integer.toHexString(paramInt));
    super.dumpBack(paramPrinter, paramString, paramInt);
  }
  
  public String toString()
  {
    return "ContentProviderInfo{name=" + this.authority + " className=" + this.name + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.authority);
    paramParcel.writeString(this.readPermission);
    paramParcel.writeString(this.writePermission);
    int i;
    if (this.grantUriPermissions)
    {
      i = 1;
      paramParcel.writeInt(i);
      paramParcel.writeTypedArray(this.uriPermissionPatterns, paramInt);
      paramParcel.writeTypedArray(this.pathPermissions, paramInt);
      if (!this.multiprocess) {
        break label116;
      }
      paramInt = 1;
      label74:
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.initOrder);
      paramParcel.writeInt(this.flags);
      if (!this.isSyncable) {
        break label121;
      }
    }
    label116:
    label121:
    for (paramInt = j;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      i = 0;
      break;
      paramInt = 0;
      break label74;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/ProviderInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */