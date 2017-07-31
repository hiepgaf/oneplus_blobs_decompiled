package android.net.metrics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ApfStats
  implements Parcelable
{
  public static final Parcelable.Creator<ApfStats> CREATOR = new Parcelable.Creator()
  {
    public ApfStats createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ApfStats(paramAnonymousParcel, null);
    }
    
    public ApfStats[] newArray(int paramAnonymousInt)
    {
      return new ApfStats[paramAnonymousInt];
    }
  };
  public final int droppedRas;
  public final long durationMs;
  public final int matchingRas;
  public final int maxProgramSize;
  public final int parseErrors;
  public final int programUpdates;
  public final int receivedRas;
  public final int zeroLifetimeRas;
  
  public ApfStats(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    this.durationMs = paramLong;
    this.receivedRas = paramInt1;
    this.matchingRas = paramInt2;
    this.droppedRas = paramInt3;
    this.zeroLifetimeRas = paramInt4;
    this.parseErrors = paramInt5;
    this.programUpdates = paramInt6;
    this.maxProgramSize = paramInt7;
  }
  
  private ApfStats(Parcel paramParcel)
  {
    this.durationMs = paramParcel.readLong();
    this.receivedRas = paramParcel.readInt();
    this.matchingRas = paramParcel.readInt();
    this.droppedRas = paramParcel.readInt();
    this.zeroLifetimeRas = paramParcel.readInt();
    this.parseErrors = paramParcel.readInt();
    this.programUpdates = paramParcel.readInt();
    this.maxProgramSize = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return "ApfStats(" + String.format("%dms ", new Object[] { Long.valueOf(this.durationMs) }) + String.format("%dB RA: {", new Object[] { Integer.valueOf(this.maxProgramSize) }) + String.format("%d received, ", new Object[] { Integer.valueOf(this.receivedRas) }) + String.format("%d matching, ", new Object[] { Integer.valueOf(this.matchingRas) }) + String.format("%d dropped, ", new Object[] { Integer.valueOf(this.droppedRas) }) + String.format("%d zero lifetime, ", new Object[] { Integer.valueOf(this.zeroLifetimeRas) }) + String.format("%d parse errors, ", new Object[] { Integer.valueOf(this.parseErrors) }) + String.format("%d program updates})", new Object[] { Integer.valueOf(this.programUpdates) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.durationMs);
    paramParcel.writeInt(this.receivedRas);
    paramParcel.writeInt(this.matchingRas);
    paramParcel.writeInt(this.droppedRas);
    paramParcel.writeInt(this.zeroLifetimeRas);
    paramParcel.writeInt(this.parseErrors);
    paramParcel.writeInt(this.programUpdates);
    paramParcel.writeInt(this.maxProgramSize);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/metrics/ApfStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */