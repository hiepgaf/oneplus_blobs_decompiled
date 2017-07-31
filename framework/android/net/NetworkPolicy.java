package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.BackupUtils;
import android.util.BackupUtils.BadVersionException;
import com.android.internal.util.Preconditions;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class NetworkPolicy
  implements Parcelable, Comparable<NetworkPolicy>
{
  private static final int BACKUP_VERSION = 1;
  public static final Parcelable.Creator<NetworkPolicy> CREATOR = new Parcelable.Creator()
  {
    public NetworkPolicy createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NetworkPolicy(paramAnonymousParcel);
    }
    
    public NetworkPolicy[] newArray(int paramAnonymousInt)
    {
      return new NetworkPolicy[paramAnonymousInt];
    }
  };
  public static final int CYCLE_NONE = -1;
  private static final long DEFAULT_MTU = 1500L;
  public static final long LIMIT_DISABLED = -1L;
  public static final long SNOOZE_NEVER = -1L;
  public static final long WARNING_DISABLED = -1L;
  public int cycleDay;
  public String cycleTimezone;
  public boolean inferred;
  public long lastLimitSnooze;
  public long lastWarningSnooze;
  public long limitBytes;
  public boolean metered;
  public NetworkTemplate template;
  public long warningBytes;
  
  public NetworkPolicy(NetworkTemplate paramNetworkTemplate, int paramInt, String paramString, long paramLong1, long paramLong2, long paramLong3, long paramLong4, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.template = ((NetworkTemplate)Preconditions.checkNotNull(paramNetworkTemplate, "missing NetworkTemplate"));
    this.cycleDay = paramInt;
    this.cycleTimezone = ((String)Preconditions.checkNotNull(paramString, "missing cycleTimezone"));
    this.warningBytes = paramLong1;
    this.limitBytes = paramLong2;
    this.lastWarningSnooze = paramLong3;
    this.lastLimitSnooze = paramLong4;
    this.metered = paramBoolean1;
    this.inferred = paramBoolean2;
  }
  
  @Deprecated
  public NetworkPolicy(NetworkTemplate paramNetworkTemplate, int paramInt, String paramString, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this(paramNetworkTemplate, paramInt, paramString, paramLong1, paramLong2, -1L, -1L, paramBoolean, false);
  }
  
  public NetworkPolicy(Parcel paramParcel)
  {
    this.template = ((NetworkTemplate)paramParcel.readParcelable(null));
    this.cycleDay = paramParcel.readInt();
    this.cycleTimezone = paramParcel.readString();
    this.warningBytes = paramParcel.readLong();
    this.limitBytes = paramParcel.readLong();
    this.lastWarningSnooze = paramParcel.readLong();
    this.lastLimitSnooze = paramParcel.readLong();
    if (paramParcel.readInt() != 0)
    {
      bool1 = true;
      this.metered = bool1;
      if (paramParcel.readInt() == 0) {
        break label100;
      }
    }
    label100:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.inferred = bool1;
      return;
      bool1 = false;
      break;
    }
  }
  
  public static NetworkPolicy getNetworkPolicyFromBackup(DataInputStream paramDataInputStream)
    throws IOException, BackupUtils.BadVersionException
  {
    int i = paramDataInputStream.readInt();
    if ((i < 1) || (i > 1)) {
      throw new BackupUtils.BadVersionException("Unknown Backup Serialization Version");
    }
    NetworkTemplate localNetworkTemplate = NetworkTemplate.getNetworkTemplateFromBackup(paramDataInputStream);
    i = paramDataInputStream.readInt();
    String str = BackupUtils.readString(paramDataInputStream);
    long l1 = paramDataInputStream.readLong();
    long l2 = paramDataInputStream.readLong();
    long l3 = paramDataInputStream.readLong();
    long l4 = paramDataInputStream.readLong();
    boolean bool1;
    if (paramDataInputStream.readInt() == 1)
    {
      bool1 = true;
      if (paramDataInputStream.readInt() != 1) {
        break label117;
      }
    }
    label117:
    for (boolean bool2 = true;; bool2 = false)
    {
      return new NetworkPolicy(localNetworkTemplate, i, str, l1, l2, l3, l4, bool1, bool2);
      bool1 = false;
      break;
    }
  }
  
  public void clearSnooze()
  {
    this.lastWarningSnooze = -1L;
    this.lastLimitSnooze = -1L;
  }
  
  public int compareTo(NetworkPolicy paramNetworkPolicy)
  {
    if ((paramNetworkPolicy == null) || (paramNetworkPolicy.limitBytes == -1L)) {
      return -1;
    }
    if ((this.limitBytes == -1L) || (paramNetworkPolicy.limitBytes < this.limitBytes)) {
      return 1;
    }
    return 0;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if ((paramObject instanceof NetworkPolicy))
    {
      paramObject = (NetworkPolicy)paramObject;
      boolean bool1 = bool2;
      if (this.cycleDay == ((NetworkPolicy)paramObject).cycleDay)
      {
        bool1 = bool2;
        if (this.warningBytes == ((NetworkPolicy)paramObject).warningBytes)
        {
          bool1 = bool2;
          if (this.limitBytes == ((NetworkPolicy)paramObject).limitBytes)
          {
            bool1 = bool2;
            if (this.lastWarningSnooze == ((NetworkPolicy)paramObject).lastWarningSnooze)
            {
              bool1 = bool2;
              if (this.lastLimitSnooze == ((NetworkPolicy)paramObject).lastLimitSnooze)
              {
                bool1 = bool2;
                if (this.metered == ((NetworkPolicy)paramObject).metered)
                {
                  bool1 = bool2;
                  if (this.inferred == ((NetworkPolicy)paramObject).inferred)
                  {
                    bool1 = bool2;
                    if (Objects.equals(this.cycleTimezone, ((NetworkPolicy)paramObject).cycleTimezone)) {
                      bool1 = Objects.equals(this.template, ((NetworkPolicy)paramObject).template);
                    }
                  }
                }
              }
            }
          }
        }
      }
      return bool1;
    }
    return false;
  }
  
  public byte[] getBytesForBackup()
    throws IOException
  {
    int j = 1;
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    localDataOutputStream.writeInt(1);
    localDataOutputStream.write(this.template.getBytesForBackup());
    localDataOutputStream.writeInt(this.cycleDay);
    BackupUtils.writeString(localDataOutputStream, this.cycleTimezone);
    localDataOutputStream.writeLong(this.warningBytes);
    localDataOutputStream.writeLong(this.limitBytes);
    localDataOutputStream.writeLong(this.lastWarningSnooze);
    localDataOutputStream.writeLong(this.lastLimitSnooze);
    if (this.metered)
    {
      i = 1;
      localDataOutputStream.writeInt(i);
      if (!this.inferred) {
        break label132;
      }
    }
    label132:
    for (int i = j;; i = 0)
    {
      localDataOutputStream.writeInt(i);
      return localByteArrayOutputStream.toByteArray();
      i = 0;
      break;
    }
  }
  
  public boolean hasCycle()
  {
    return this.cycleDay != -1;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.template, Integer.valueOf(this.cycleDay), this.cycleTimezone, Long.valueOf(this.warningBytes), Long.valueOf(this.limitBytes), Long.valueOf(this.lastWarningSnooze), Long.valueOf(this.lastLimitSnooze), Boolean.valueOf(this.metered), Boolean.valueOf(this.inferred) });
  }
  
  public boolean isOverLimit(long paramLong)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.limitBytes != -1L)
    {
      bool1 = bool2;
      if (paramLong + 3000L >= this.limitBytes) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isOverWarning(long paramLong)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.warningBytes != -1L)
    {
      bool1 = bool2;
      if (paramLong >= this.warningBytes) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("NetworkPolicy");
    localStringBuilder.append("[").append(this.template).append("]:");
    localStringBuilder.append(" cycleDay=").append(this.cycleDay);
    localStringBuilder.append(", cycleTimezone=").append(this.cycleTimezone);
    localStringBuilder.append(", warningBytes=").append(this.warningBytes);
    localStringBuilder.append(", limitBytes=").append(this.limitBytes);
    localStringBuilder.append(", lastWarningSnooze=").append(this.lastWarningSnooze);
    localStringBuilder.append(", lastLimitSnooze=").append(this.lastLimitSnooze);
    localStringBuilder.append(", metered=").append(this.metered);
    localStringBuilder.append(", inferred=").append(this.inferred);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeParcelable(this.template, paramInt);
    paramParcel.writeInt(this.cycleDay);
    paramParcel.writeString(this.cycleTimezone);
    paramParcel.writeLong(this.warningBytes);
    paramParcel.writeLong(this.limitBytes);
    paramParcel.writeLong(this.lastWarningSnooze);
    paramParcel.writeLong(this.lastLimitSnooze);
    if (this.metered)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.inferred) {
        break label93;
      }
    }
    label93:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramInt = 0;
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */