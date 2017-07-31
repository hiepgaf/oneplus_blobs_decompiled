package android.os.storage;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.DebugUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.io.CharArrayWriter;
import java.util.Objects;

public class DiskInfo
  implements Parcelable
{
  public static final String ACTION_DISK_SCANNED = "android.os.storage.action.DISK_SCANNED";
  public static final Parcelable.Creator<DiskInfo> CREATOR = new Parcelable.Creator()
  {
    public DiskInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DiskInfo(paramAnonymousParcel);
    }
    
    public DiskInfo[] newArray(int paramAnonymousInt)
    {
      return new DiskInfo[paramAnonymousInt];
    }
  };
  public static final String EXTRA_DISK_ID = "android.os.storage.extra.DISK_ID";
  public static final String EXTRA_VOLUME_COUNT = "android.os.storage.extra.VOLUME_COUNT";
  public static final int FLAG_ADOPTABLE = 1;
  public static final int FLAG_DEFAULT_PRIMARY = 2;
  public static final int FLAG_SD = 4;
  public static final int FLAG_USB = 8;
  public final int flags;
  public final String id;
  public String label;
  public long size;
  public String sysPath;
  public int volumeCount;
  
  public DiskInfo(Parcel paramParcel)
  {
    this.id = paramParcel.readString();
    this.flags = paramParcel.readInt();
    this.size = paramParcel.readLong();
    this.label = paramParcel.readString();
    this.volumeCount = paramParcel.readInt();
    this.sysPath = paramParcel.readString();
  }
  
  public DiskInfo(String paramString, int paramInt)
  {
    this.id = ((String)Preconditions.checkNotNull(paramString));
    this.flags = paramInt;
  }
  
  private boolean isInteresting(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    if (paramString.equalsIgnoreCase("ata")) {
      return false;
    }
    if (paramString.toLowerCase().contains("generic")) {
      return false;
    }
    if (paramString.toLowerCase().startsWith("usb")) {
      return false;
    }
    return !paramString.toLowerCase().startsWith("multiple");
  }
  
  public DiskInfo clone()
  {
    Parcel localParcel = Parcel.obtain();
    try
    {
      writeToParcel(localParcel, 0);
      localParcel.setDataPosition(0);
      DiskInfo localDiskInfo = (DiskInfo)CREATOR.createFromParcel(localParcel);
      return localDiskInfo;
    }
    finally
    {
      localParcel.recycle();
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    paramIndentingPrintWriter.println("DiskInfo{" + this.id + "}:");
    paramIndentingPrintWriter.increaseIndent();
    paramIndentingPrintWriter.printPair("flags", DebugUtils.flagsToString(getClass(), "FLAG_", this.flags));
    paramIndentingPrintWriter.printPair("size", Long.valueOf(this.size));
    paramIndentingPrintWriter.printPair("label", this.label);
    paramIndentingPrintWriter.println();
    paramIndentingPrintWriter.printPair("sysPath", this.sysPath);
    paramIndentingPrintWriter.decreaseIndent();
    paramIndentingPrintWriter.println();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof DiskInfo)) {
      return Objects.equals(this.id, ((DiskInfo)paramObject).id);
    }
    return false;
  }
  
  public String getDescription()
  {
    Resources localResources = Resources.getSystem();
    if ((this.flags & 0x4) != 0)
    {
      if (isInteresting(this.label)) {
        return localResources.getString(17040607, new Object[] { this.label });
      }
      return localResources.getString(17040606);
    }
    if ((this.flags & 0x8) != 0)
    {
      if (isInteresting(this.label)) {
        return localResources.getString(17040609, new Object[] { this.label });
      }
      return localResources.getString(17040608);
    }
    return null;
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public int hashCode()
  {
    return this.id.hashCode();
  }
  
  public boolean isAdoptable()
  {
    boolean bool = false;
    if ((this.flags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isDefaultPrimary()
  {
    boolean bool = false;
    if ((this.flags & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isSd()
  {
    boolean bool = false;
    if ((this.flags & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isUsb()
  {
    boolean bool = false;
    if ((this.flags & 0x8) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public String toString()
  {
    CharArrayWriter localCharArrayWriter = new CharArrayWriter();
    dump(new IndentingPrintWriter(localCharArrayWriter, "    ", 80));
    return localCharArrayWriter.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.id);
    paramParcel.writeInt(this.flags);
    paramParcel.writeLong(this.size);
    paramParcel.writeString(this.label);
    paramParcel.writeInt(this.volumeCount);
    paramParcel.writeString(this.sysPath);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/storage/DiskInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */