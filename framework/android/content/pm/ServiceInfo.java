package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Printer;

public class ServiceInfo
  extends ComponentInfo
  implements Parcelable
{
  public static final Parcelable.Creator<ServiceInfo> CREATOR = new Parcelable.Creator()
  {
    public ServiceInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ServiceInfo(paramAnonymousParcel, null);
    }
    
    public ServiceInfo[] newArray(int paramAnonymousInt)
    {
      return new ServiceInfo[paramAnonymousInt];
    }
  };
  public static final int FLAG_EXTERNAL_SERVICE = 4;
  public static final int FLAG_ISOLATED_PROCESS = 2;
  public static final int FLAG_SINGLE_USER = 1073741824;
  public static final int FLAG_STOP_WITH_TASK = 1;
  public int flags;
  public String permission;
  
  public ServiceInfo() {}
  
  public ServiceInfo(ServiceInfo paramServiceInfo)
  {
    super(paramServiceInfo);
    this.permission = paramServiceInfo.permission;
    this.flags = paramServiceInfo.flags;
  }
  
  private ServiceInfo(Parcel paramParcel)
  {
    super(paramParcel);
    this.permission = paramParcel.readString();
    this.flags = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    dump(paramPrinter, paramString, 3);
  }
  
  void dump(Printer paramPrinter, String paramString, int paramInt)
  {
    super.dumpFront(paramPrinter, paramString);
    paramPrinter.println(paramString + "permission=" + this.permission);
    paramPrinter.println(paramString + "flags=0x" + Integer.toHexString(paramInt));
    super.dumpBack(paramPrinter, paramString, paramInt);
  }
  
  public String toString()
  {
    return "ServiceInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.name + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.permission);
    paramParcel.writeInt(this.flags);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/ServiceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */