package android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SyncStats
  implements Parcelable
{
  public static final Parcelable.Creator<SyncStats> CREATOR = new Parcelable.Creator()
  {
    public SyncStats createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SyncStats(paramAnonymousParcel);
    }
    
    public SyncStats[] newArray(int paramAnonymousInt)
    {
      return new SyncStats[paramAnonymousInt];
    }
  };
  public long numAuthExceptions;
  public long numConflictDetectedExceptions;
  public long numDeletes;
  public long numEntries;
  public long numInserts;
  public long numIoExceptions;
  public long numParseExceptions;
  public long numSkippedEntries;
  public long numUpdates;
  
  public SyncStats()
  {
    this.numAuthExceptions = 0L;
    this.numIoExceptions = 0L;
    this.numParseExceptions = 0L;
    this.numConflictDetectedExceptions = 0L;
    this.numInserts = 0L;
    this.numUpdates = 0L;
    this.numDeletes = 0L;
    this.numEntries = 0L;
    this.numSkippedEntries = 0L;
  }
  
  public SyncStats(Parcel paramParcel)
  {
    this.numAuthExceptions = paramParcel.readLong();
    this.numIoExceptions = paramParcel.readLong();
    this.numParseExceptions = paramParcel.readLong();
    this.numConflictDetectedExceptions = paramParcel.readLong();
    this.numInserts = paramParcel.readLong();
    this.numUpdates = paramParcel.readLong();
    this.numDeletes = paramParcel.readLong();
    this.numEntries = paramParcel.readLong();
    this.numSkippedEntries = paramParcel.readLong();
  }
  
  public void clear()
  {
    this.numAuthExceptions = 0L;
    this.numIoExceptions = 0L;
    this.numParseExceptions = 0L;
    this.numConflictDetectedExceptions = 0L;
    this.numInserts = 0L;
    this.numUpdates = 0L;
    this.numDeletes = 0L;
    this.numEntries = 0L;
    this.numSkippedEntries = 0L;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(" stats [");
    if (this.numAuthExceptions > 0L) {
      localStringBuilder.append(" numAuthExceptions: ").append(this.numAuthExceptions);
    }
    if (this.numIoExceptions > 0L) {
      localStringBuilder.append(" numIoExceptions: ").append(this.numIoExceptions);
    }
    if (this.numParseExceptions > 0L) {
      localStringBuilder.append(" numParseExceptions: ").append(this.numParseExceptions);
    }
    if (this.numConflictDetectedExceptions > 0L) {
      localStringBuilder.append(" numConflictDetectedExceptions: ").append(this.numConflictDetectedExceptions);
    }
    if (this.numInserts > 0L) {
      localStringBuilder.append(" numInserts: ").append(this.numInserts);
    }
    if (this.numUpdates > 0L) {
      localStringBuilder.append(" numUpdates: ").append(this.numUpdates);
    }
    if (this.numDeletes > 0L) {
      localStringBuilder.append(" numDeletes: ").append(this.numDeletes);
    }
    if (this.numEntries > 0L) {
      localStringBuilder.append(" numEntries: ").append(this.numEntries);
    }
    if (this.numSkippedEntries > 0L) {
      localStringBuilder.append(" numSkippedEntries: ").append(this.numSkippedEntries);
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.numAuthExceptions);
    paramParcel.writeLong(this.numIoExceptions);
    paramParcel.writeLong(this.numParseExceptions);
    paramParcel.writeLong(this.numConflictDetectedExceptions);
    paramParcel.writeLong(this.numInserts);
    paramParcel.writeLong(this.numUpdates);
    paramParcel.writeLong(this.numDeletes);
    paramParcel.writeLong(this.numEntries);
    paramParcel.writeLong(this.numSkippedEntries);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/SyncStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */