package android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

public class SyncStatusInfo
  implements Parcelable
{
  public static final Parcelable.Creator<SyncStatusInfo> CREATOR = new Parcelable.Creator()
  {
    public SyncStatusInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SyncStatusInfo(paramAnonymousParcel);
    }
    
    public SyncStatusInfo[] newArray(int paramAnonymousInt)
    {
      return new SyncStatusInfo[paramAnonymousInt];
    }
  };
  private static final String TAG = "Sync";
  static final int VERSION = 2;
  public final int authorityId;
  public long initialFailureTime;
  public boolean initialize;
  public String lastFailureMesg;
  public int lastFailureSource;
  public long lastFailureTime;
  public int lastSuccessSource;
  public long lastSuccessTime;
  public int numSourceLocal;
  public int numSourcePeriodic;
  public int numSourcePoll;
  public int numSourceServer;
  public int numSourceUser;
  public int numSyncs;
  public boolean pending;
  private ArrayList<Long> periodicSyncTimes;
  public long totalElapsedTime;
  
  public SyncStatusInfo(int paramInt)
  {
    this.authorityId = paramInt;
  }
  
  public SyncStatusInfo(SyncStatusInfo paramSyncStatusInfo)
  {
    this.authorityId = paramSyncStatusInfo.authorityId;
    this.totalElapsedTime = paramSyncStatusInfo.totalElapsedTime;
    this.numSyncs = paramSyncStatusInfo.numSyncs;
    this.numSourcePoll = paramSyncStatusInfo.numSourcePoll;
    this.numSourceServer = paramSyncStatusInfo.numSourceServer;
    this.numSourceLocal = paramSyncStatusInfo.numSourceLocal;
    this.numSourceUser = paramSyncStatusInfo.numSourceUser;
    this.numSourcePeriodic = paramSyncStatusInfo.numSourcePeriodic;
    this.lastSuccessTime = paramSyncStatusInfo.lastSuccessTime;
    this.lastSuccessSource = paramSyncStatusInfo.lastSuccessSource;
    this.lastFailureTime = paramSyncStatusInfo.lastFailureTime;
    this.lastFailureSource = paramSyncStatusInfo.lastFailureSource;
    this.lastFailureMesg = paramSyncStatusInfo.lastFailureMesg;
    this.initialFailureTime = paramSyncStatusInfo.initialFailureTime;
    this.pending = paramSyncStatusInfo.pending;
    this.initialize = paramSyncStatusInfo.initialize;
    if (paramSyncStatusInfo.periodicSyncTimes != null) {
      this.periodicSyncTimes = new ArrayList(paramSyncStatusInfo.periodicSyncTimes);
    }
  }
  
  public SyncStatusInfo(Parcel paramParcel)
  {
    int i = paramParcel.readInt();
    if ((i != 2) && (i != 1)) {
      Log.w("SyncStatusInfo", "Unknown version: " + i);
    }
    this.authorityId = paramParcel.readInt();
    this.totalElapsedTime = paramParcel.readLong();
    this.numSyncs = paramParcel.readInt();
    this.numSourcePoll = paramParcel.readInt();
    this.numSourceServer = paramParcel.readInt();
    this.numSourceLocal = paramParcel.readInt();
    this.numSourceUser = paramParcel.readInt();
    this.lastSuccessTime = paramParcel.readLong();
    this.lastSuccessSource = paramParcel.readInt();
    this.lastFailureTime = paramParcel.readLong();
    this.lastFailureSource = paramParcel.readInt();
    this.lastFailureMesg = paramParcel.readString();
    this.initialFailureTime = paramParcel.readLong();
    boolean bool1;
    if (paramParcel.readInt() != 0)
    {
      bool1 = true;
      this.pending = bool1;
      bool1 = bool2;
      if (paramParcel.readInt() != 0) {
        bool1 = true;
      }
      this.initialize = bool1;
      if (i != 1) {
        break label204;
      }
      this.periodicSyncTimes = null;
    }
    for (;;)
    {
      return;
      bool1 = false;
      break;
      label204:
      int j = paramParcel.readInt();
      if (j < 0)
      {
        this.periodicSyncTimes = null;
        return;
      }
      this.periodicSyncTimes = new ArrayList();
      i = 0;
      while (i < j)
      {
        this.periodicSyncTimes.add(Long.valueOf(paramParcel.readLong()));
        i += 1;
      }
    }
  }
  
  private void ensurePeriodicSyncTimeSize(int paramInt)
  {
    if (this.periodicSyncTimes == null) {
      this.periodicSyncTimes = new ArrayList(0);
    }
    int i = paramInt + 1;
    if (this.periodicSyncTimes.size() < i)
    {
      paramInt = this.periodicSyncTimes.size();
      while (paramInt < i)
      {
        this.periodicSyncTimes.add(Long.valueOf(0L));
        paramInt += 1;
      }
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getLastFailureMesgAsInt(int paramInt)
  {
    int i = ContentResolver.syncErrorStringToInt(this.lastFailureMesg);
    if (i > 0) {
      return i;
    }
    Log.d("Sync", "Unknown lastFailureMesg:" + this.lastFailureMesg);
    return paramInt;
  }
  
  public long getPeriodicSyncTime(int paramInt)
  {
    if ((this.periodicSyncTimes != null) && (paramInt < this.periodicSyncTimes.size())) {
      return ((Long)this.periodicSyncTimes.get(paramInt)).longValue();
    }
    return 0L;
  }
  
  public void removePeriodicSyncTime(int paramInt)
  {
    if ((this.periodicSyncTimes != null) && (paramInt < this.periodicSyncTimes.size())) {
      this.periodicSyncTimes.remove(paramInt);
    }
  }
  
  public void setPeriodicSyncTime(int paramInt, long paramLong)
  {
    ensurePeriodicSyncTimeSize(paramInt);
    this.periodicSyncTimes.set(paramInt, Long.valueOf(paramLong));
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeInt(2);
    paramParcel.writeInt(this.authorityId);
    paramParcel.writeLong(this.totalElapsedTime);
    paramParcel.writeInt(this.numSyncs);
    paramParcel.writeInt(this.numSourcePoll);
    paramParcel.writeInt(this.numSourceServer);
    paramParcel.writeInt(this.numSourceLocal);
    paramParcel.writeInt(this.numSourceUser);
    paramParcel.writeLong(this.lastSuccessTime);
    paramParcel.writeInt(this.lastSuccessSource);
    paramParcel.writeLong(this.lastFailureTime);
    paramParcel.writeInt(this.lastFailureSource);
    paramParcel.writeString(this.lastFailureMesg);
    paramParcel.writeLong(this.initialFailureTime);
    if (this.pending)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.initialize) {
        break label203;
      }
    }
    label203:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      if (this.periodicSyncTimes == null) {
        break label208;
      }
      paramParcel.writeInt(this.periodicSyncTimes.size());
      Iterator localIterator = this.periodicSyncTimes.iterator();
      while (localIterator.hasNext()) {
        paramParcel.writeLong(((Long)localIterator.next()).longValue());
      }
      paramInt = 0;
      break;
    }
    label208:
    paramParcel.writeInt(-1);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/SyncStatusInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */