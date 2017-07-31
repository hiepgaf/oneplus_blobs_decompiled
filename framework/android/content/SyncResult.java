package android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class SyncResult
  implements Parcelable
{
  public static final SyncResult ALREADY_IN_PROGRESS = new SyncResult(true);
  public static final Parcelable.Creator<SyncResult> CREATOR = new Parcelable.Creator()
  {
    public SyncResult createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SyncResult(paramAnonymousParcel, null);
    }
    
    public SyncResult[] newArray(int paramAnonymousInt)
    {
      return new SyncResult[paramAnonymousInt];
    }
  };
  public boolean databaseError;
  public long delayUntil;
  public boolean fullSyncRequested;
  public boolean moreRecordsToGet;
  public boolean partialSyncUnavailable;
  public final SyncStats stats;
  public final boolean syncAlreadyInProgress;
  public boolean tooManyDeletions;
  public boolean tooManyRetries;
  
  public SyncResult()
  {
    this(false);
  }
  
  private SyncResult(Parcel paramParcel)
  {
    if (paramParcel.readInt() != 0)
    {
      bool1 = true;
      this.syncAlreadyInProgress = bool1;
      if (paramParcel.readInt() == 0) {
        break label130;
      }
      bool1 = true;
      label29:
      this.tooManyDeletions = bool1;
      if (paramParcel.readInt() == 0) {
        break label135;
      }
      bool1 = true;
      label43:
      this.tooManyRetries = bool1;
      if (paramParcel.readInt() == 0) {
        break label140;
      }
      bool1 = true;
      label57:
      this.databaseError = bool1;
      if (paramParcel.readInt() == 0) {
        break label145;
      }
      bool1 = true;
      label71:
      this.fullSyncRequested = bool1;
      if (paramParcel.readInt() == 0) {
        break label150;
      }
      bool1 = true;
      label85:
      this.partialSyncUnavailable = bool1;
      if (paramParcel.readInt() == 0) {
        break label155;
      }
    }
    label130:
    label135:
    label140:
    label145:
    label150:
    label155:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.moreRecordsToGet = bool1;
      this.delayUntil = paramParcel.readLong();
      this.stats = new SyncStats(paramParcel);
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label29;
      bool1 = false;
      break label43;
      bool1 = false;
      break label57;
      bool1 = false;
      break label71;
      bool1 = false;
      break label85;
    }
  }
  
  private SyncResult(boolean paramBoolean)
  {
    this.syncAlreadyInProgress = paramBoolean;
    this.tooManyDeletions = false;
    this.tooManyRetries = false;
    this.fullSyncRequested = false;
    this.partialSyncUnavailable = false;
    this.moreRecordsToGet = false;
    this.delayUntil = 0L;
    this.stats = new SyncStats();
  }
  
  public void clear()
  {
    if (this.syncAlreadyInProgress) {
      throw new UnsupportedOperationException("you are not allowed to clear the ALREADY_IN_PROGRESS SyncStats");
    }
    this.tooManyDeletions = false;
    this.tooManyRetries = false;
    this.databaseError = false;
    this.fullSyncRequested = false;
    this.partialSyncUnavailable = false;
    this.moreRecordsToGet = false;
    this.delayUntil = 0L;
    this.stats.clear();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean hasError()
  {
    if (!hasSoftError()) {
      return hasHardError();
    }
    return true;
  }
  
  public boolean hasHardError()
  {
    if ((this.stats.numParseExceptions > 0L) || (this.stats.numConflictDetectedExceptions > 0L)) {}
    while ((this.stats.numAuthExceptions > 0L) || (this.tooManyDeletions) || (this.tooManyRetries)) {
      return true;
    }
    return this.databaseError;
  }
  
  public boolean hasSoftError()
  {
    return (this.syncAlreadyInProgress) || (this.stats.numIoExceptions > 0L);
  }
  
  public boolean madeSomeProgress()
  {
    if (((this.stats.numDeletes > 0L) && (!this.tooManyDeletions)) || (this.stats.numInserts > 0L)) {}
    while (this.stats.numUpdates > 0L) {
      return true;
    }
    return false;
  }
  
  public String toDebugString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (this.fullSyncRequested) {
      localStringBuffer.append("f1");
    }
    if (this.partialSyncUnavailable) {
      localStringBuffer.append("r1");
    }
    if (hasHardError()) {
      localStringBuffer.append("X1");
    }
    if (this.stats.numParseExceptions > 0L) {
      localStringBuffer.append("e").append(this.stats.numParseExceptions);
    }
    if (this.stats.numConflictDetectedExceptions > 0L) {
      localStringBuffer.append("c").append(this.stats.numConflictDetectedExceptions);
    }
    if (this.stats.numAuthExceptions > 0L) {
      localStringBuffer.append("a").append(this.stats.numAuthExceptions);
    }
    if (this.tooManyDeletions) {
      localStringBuffer.append("D1");
    }
    if (this.tooManyRetries) {
      localStringBuffer.append("R1");
    }
    if (this.databaseError) {
      localStringBuffer.append("b1");
    }
    if (hasSoftError()) {
      localStringBuffer.append("x1");
    }
    if (this.syncAlreadyInProgress) {
      localStringBuffer.append("l1");
    }
    if (this.stats.numIoExceptions > 0L) {
      localStringBuffer.append("I").append(this.stats.numIoExceptions);
    }
    return localStringBuffer.toString();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SyncResult:");
    if (this.syncAlreadyInProgress) {
      localStringBuilder.append(" syncAlreadyInProgress: ").append(this.syncAlreadyInProgress);
    }
    if (this.tooManyDeletions) {
      localStringBuilder.append(" tooManyDeletions: ").append(this.tooManyDeletions);
    }
    if (this.tooManyRetries) {
      localStringBuilder.append(" tooManyRetries: ").append(this.tooManyRetries);
    }
    if (this.databaseError) {
      localStringBuilder.append(" databaseError: ").append(this.databaseError);
    }
    if (this.fullSyncRequested) {
      localStringBuilder.append(" fullSyncRequested: ").append(this.fullSyncRequested);
    }
    if (this.partialSyncUnavailable) {
      localStringBuilder.append(" partialSyncUnavailable: ").append(this.partialSyncUnavailable);
    }
    if (this.moreRecordsToGet) {
      localStringBuilder.append(" moreRecordsToGet: ").append(this.moreRecordsToGet);
    }
    if (this.delayUntil > 0L) {
      localStringBuilder.append(" delayUntil: ").append(this.delayUntil);
    }
    localStringBuilder.append(this.stats);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    if (this.syncAlreadyInProgress)
    {
      i = 1;
      paramParcel.writeInt(i);
      if (!this.tooManyDeletions) {
        break label125;
      }
      i = 1;
      label26:
      paramParcel.writeInt(i);
      if (!this.tooManyRetries) {
        break label130;
      }
      i = 1;
      label40:
      paramParcel.writeInt(i);
      if (!this.databaseError) {
        break label135;
      }
      i = 1;
      label54:
      paramParcel.writeInt(i);
      if (!this.fullSyncRequested) {
        break label140;
      }
      i = 1;
      label68:
      paramParcel.writeInt(i);
      if (!this.partialSyncUnavailable) {
        break label145;
      }
      i = 1;
      label82:
      paramParcel.writeInt(i);
      if (!this.moreRecordsToGet) {
        break label150;
      }
    }
    label125:
    label130:
    label135:
    label140:
    label145:
    label150:
    for (int i = j;; i = 0)
    {
      paramParcel.writeInt(i);
      paramParcel.writeLong(this.delayUntil);
      this.stats.writeToParcel(paramParcel, paramInt);
      return;
      i = 0;
      break;
      i = 0;
      break label26;
      i = 0;
      break label40;
      i = 0;
      break label54;
      i = 0;
      break label68;
      i = 0;
      break label82;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/SyncResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */