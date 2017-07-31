package android.net.metrics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.SparseArray;
import com.android.internal.util.MessageUtils;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public final class ApfProgramEvent
  implements Parcelable
{
  public static final Parcelable.Creator<ApfProgramEvent> CREATOR = new Parcelable.Creator()
  {
    public ApfProgramEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ApfProgramEvent(paramAnonymousParcel, null);
    }
    
    public ApfProgramEvent[] newArray(int paramAnonymousInt)
    {
      return new ApfProgramEvent[paramAnonymousInt];
    }
  };
  public static final int FLAG_HAS_IPV4_ADDRESS = 1;
  public static final int FLAG_MULTICAST_FILTER_ON = 0;
  public final int currentRas;
  public final int filteredRas;
  public final int flags;
  public final long lifetime;
  public final int programLength;
  
  public ApfProgramEvent(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.lifetime = paramLong;
    this.filteredRas = paramInt1;
    this.currentRas = paramInt2;
    this.programLength = paramInt3;
    this.flags = paramInt4;
  }
  
  private ApfProgramEvent(Parcel paramParcel)
  {
    this.lifetime = paramParcel.readLong();
    this.filteredRas = paramParcel.readInt();
    this.currentRas = paramParcel.readInt();
    this.programLength = paramParcel.readInt();
    this.flags = paramParcel.readInt();
  }
  
  public static int flagsFor(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 0;
    if (paramBoolean1) {
      i = 2;
    }
    int j = i;
    if (paramBoolean2) {
      j = i | 0x1;
    }
    return j;
  }
  
  private static String namesOf(int paramInt)
  {
    ArrayList localArrayList = new ArrayList(Integer.bitCount(paramInt));
    BitSet localBitSet = BitSet.valueOf(new long[] { 0x7FFFFFFF & paramInt });
    for (paramInt = localBitSet.nextSetBit(0); paramInt >= 0; paramInt = localBitSet.nextSetBit(paramInt + 1)) {
      localArrayList.add((String)Decoder.constants.get(paramInt));
    }
    return TextUtils.join("|", localArrayList);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    if (this.lifetime < Long.MAX_VALUE) {}
    for (String str = this.lifetime + "s";; str = "forever") {
      return String.format("ApfProgramEvent(%d/%d RAs %dB %s %s)", new Object[] { Integer.valueOf(this.filteredRas), Integer.valueOf(this.currentRas), Integer.valueOf(this.programLength), str, namesOf(this.flags) });
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.lifetime);
    paramParcel.writeInt(this.filteredRas);
    paramParcel.writeInt(this.currentRas);
    paramParcel.writeInt(this.programLength);
    paramParcel.writeInt(paramInt);
  }
  
  static final class Decoder
  {
    static final SparseArray<String> constants = MessageUtils.findMessageNames(new Class[] { ApfProgramEvent.class }, new String[] { "FLAG_" });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/metrics/ApfProgramEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */