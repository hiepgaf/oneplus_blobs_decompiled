package android.os.storage;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.DebugUtils;
import android.util.TimeUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.util.Objects;

public class VolumeRecord
  implements Parcelable
{
  public static final Parcelable.Creator<VolumeRecord> CREATOR = new Parcelable.Creator()
  {
    public VolumeRecord createFromParcel(Parcel paramAnonymousParcel)
    {
      return new VolumeRecord(paramAnonymousParcel);
    }
    
    public VolumeRecord[] newArray(int paramAnonymousInt)
    {
      return new VolumeRecord[paramAnonymousInt];
    }
  };
  public static final String EXTRA_FS_UUID = "android.os.storage.extra.FS_UUID";
  public static final int USER_FLAG_INITED = 1;
  public static final int USER_FLAG_SNOOZED = 2;
  public long createdMillis;
  public final String fsUuid;
  public long lastBenchMillis;
  public long lastTrimMillis;
  public String nickname;
  public String partGuid;
  public final int type;
  public int userFlags;
  
  public VolumeRecord(int paramInt, String paramString)
  {
    this.type = paramInt;
    this.fsUuid = ((String)Preconditions.checkNotNull(paramString));
  }
  
  public VolumeRecord(Parcel paramParcel)
  {
    this.type = paramParcel.readInt();
    this.fsUuid = paramParcel.readString();
    this.partGuid = paramParcel.readString();
    this.nickname = paramParcel.readString();
    this.userFlags = paramParcel.readInt();
    this.createdMillis = paramParcel.readLong();
    this.lastTrimMillis = paramParcel.readLong();
    this.lastBenchMillis = paramParcel.readLong();
  }
  
  public VolumeRecord clone()
  {
    Parcel localParcel = Parcel.obtain();
    try
    {
      writeToParcel(localParcel, 0);
      localParcel.setDataPosition(0);
      VolumeRecord localVolumeRecord = (VolumeRecord)CREATOR.createFromParcel(localParcel);
      return localVolumeRecord;
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
    paramIndentingPrintWriter.println("VolumeRecord:");
    paramIndentingPrintWriter.increaseIndent();
    paramIndentingPrintWriter.printPair("type", DebugUtils.valueToString(VolumeInfo.class, "TYPE_", this.type));
    paramIndentingPrintWriter.printPair("fsUuid", this.fsUuid);
    paramIndentingPrintWriter.printPair("partGuid", this.partGuid);
    paramIndentingPrintWriter.println();
    paramIndentingPrintWriter.printPair("nickname", this.nickname);
    paramIndentingPrintWriter.printPair("userFlags", DebugUtils.flagsToString(VolumeRecord.class, "USER_FLAG_", this.userFlags));
    paramIndentingPrintWriter.println();
    paramIndentingPrintWriter.printPair("createdMillis", TimeUtils.formatForLogging(this.createdMillis));
    paramIndentingPrintWriter.printPair("lastTrimMillis", TimeUtils.formatForLogging(this.lastTrimMillis));
    paramIndentingPrintWriter.printPair("lastBenchMillis", TimeUtils.formatForLogging(this.lastBenchMillis));
    paramIndentingPrintWriter.decreaseIndent();
    paramIndentingPrintWriter.println();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof VolumeRecord)) {
      return Objects.equals(this.fsUuid, ((VolumeRecord)paramObject).fsUuid);
    }
    return false;
  }
  
  public String getFsUuid()
  {
    return this.fsUuid;
  }
  
  public String getNickname()
  {
    return this.nickname;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public int hashCode()
  {
    return this.fsUuid.hashCode();
  }
  
  public boolean isInited()
  {
    boolean bool = false;
    if ((this.userFlags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isSnoozed()
  {
    boolean bool = false;
    if ((this.userFlags & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.type);
    paramParcel.writeString(this.fsUuid);
    paramParcel.writeString(this.partGuid);
    paramParcel.writeString(this.nickname);
    paramParcel.writeInt(this.userFlags);
    paramParcel.writeLong(this.createdMillis);
    paramParcel.writeLong(this.lastTrimMillis);
    paramParcel.writeLong(this.lastBenchMillis);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/storage/VolumeRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */