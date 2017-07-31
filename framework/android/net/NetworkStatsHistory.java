package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.util.MathUtils;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import java.io.CharArrayWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ProtocolException;
import java.util.Arrays;
import java.util.Random;

public class NetworkStatsHistory
  implements Parcelable
{
  public static final Parcelable.Creator<NetworkStatsHistory> CREATOR = new Parcelable.Creator()
  {
    public NetworkStatsHistory createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NetworkStatsHistory(paramAnonymousParcel);
    }
    
    public NetworkStatsHistory[] newArray(int paramAnonymousInt)
    {
      return new NetworkStatsHistory[paramAnonymousInt];
    }
  };
  public static final int FIELD_ACTIVE_TIME = 1;
  public static final int FIELD_ALL = -1;
  public static final int FIELD_OPERATIONS = 32;
  public static final int FIELD_RX_BYTES = 2;
  public static final int FIELD_RX_PACKETS = 4;
  public static final int FIELD_TX_BYTES = 8;
  public static final int FIELD_TX_PACKETS = 16;
  private static String TAG = "NetworkStatsHistory";
  private static final int VERSION_ADD_ACTIVE = 3;
  private static final int VERSION_ADD_PACKETS = 2;
  private static final int VERSION_INIT = 1;
  private long[] activeTime;
  private int bucketCount;
  private long bucketDuration;
  private long[] bucketStart;
  private long[] operations;
  private long[] rxBytes;
  private long[] rxPackets;
  private long totalBytes;
  private long[] txBytes;
  private long[] txPackets;
  
  public NetworkStatsHistory(long paramLong)
  {
    this(paramLong, 10, -1);
  }
  
  public NetworkStatsHistory(long paramLong, int paramInt)
  {
    this(paramLong, paramInt, -1);
  }
  
  public NetworkStatsHistory(long paramLong, int paramInt1, int paramInt2)
  {
    this.bucketDuration = paramLong;
    this.bucketStart = new long[paramInt1];
    if ((paramInt2 & 0x1) != 0) {
      this.activeTime = new long[paramInt1];
    }
    if ((paramInt2 & 0x2) != 0) {
      this.rxBytes = new long[paramInt1];
    }
    if ((paramInt2 & 0x4) != 0) {
      this.rxPackets = new long[paramInt1];
    }
    if ((paramInt2 & 0x8) != 0) {
      this.txBytes = new long[paramInt1];
    }
    if ((paramInt2 & 0x10) != 0) {
      this.txPackets = new long[paramInt1];
    }
    if ((paramInt2 & 0x20) != 0) {
      this.operations = new long[paramInt1];
    }
    this.bucketCount = 0;
    this.totalBytes = 0L;
  }
  
  public NetworkStatsHistory(NetworkStatsHistory paramNetworkStatsHistory, long paramLong)
  {
    this(paramLong, paramNetworkStatsHistory.estimateResizeBuckets(paramLong));
    recordEntireHistory(paramNetworkStatsHistory);
  }
  
  public NetworkStatsHistory(Parcel paramParcel)
  {
    this.bucketDuration = paramParcel.readLong();
    this.bucketStart = ParcelUtils.readLongArray(paramParcel);
    this.activeTime = ParcelUtils.readLongArray(paramParcel);
    this.rxBytes = ParcelUtils.readLongArray(paramParcel);
    this.rxPackets = ParcelUtils.readLongArray(paramParcel);
    this.txBytes = ParcelUtils.readLongArray(paramParcel);
    this.txPackets = ParcelUtils.readLongArray(paramParcel);
    this.operations = ParcelUtils.readLongArray(paramParcel);
    this.bucketCount = this.bucketStart.length;
    this.totalBytes = paramParcel.readLong();
  }
  
  public NetworkStatsHistory(DataInputStream paramDataInputStream)
    throws IOException
  {
    int i = paramDataInputStream.readInt();
    switch (i)
    {
    default: 
      throw new ProtocolException("unexpected version: " + i);
    case 1: 
      this.bucketDuration = paramDataInputStream.readLong();
      this.bucketStart = DataStreamUtils.readFullLongArray(paramDataInputStream);
      this.rxBytes = DataStreamUtils.readFullLongArray(paramDataInputStream);
      this.rxPackets = new long[this.bucketStart.length];
      this.txBytes = DataStreamUtils.readFullLongArray(paramDataInputStream);
      this.txPackets = new long[this.bucketStart.length];
      this.operations = new long[this.bucketStart.length];
      this.bucketCount = this.bucketStart.length;
      this.totalBytes = (ArrayUtils.total(this.rxBytes) + ArrayUtils.total(this.txBytes));
      if ((this.bucketStart.length == this.bucketCount) && (this.rxBytes.length == this.bucketCount)) {
        break;
      }
    }
    while ((this.rxPackets.length != this.bucketCount) || (this.txBytes.length != this.bucketCount) || (this.txPackets.length != this.bucketCount) || (this.operations.length != this.bucketCount))
    {
      throw new ProtocolException("Mismatched history lengths");
      this.bucketDuration = paramDataInputStream.readLong();
      this.bucketStart = DataStreamUtils.readVarLongArray(paramDataInputStream);
      if (i >= 3) {}
      for (long[] arrayOfLong = DataStreamUtils.readVarLongArray(paramDataInputStream);; arrayOfLong = new long[this.bucketStart.length])
      {
        this.activeTime = arrayOfLong;
        this.rxBytes = DataStreamUtils.readVarLongArray(paramDataInputStream);
        this.rxPackets = DataStreamUtils.readVarLongArray(paramDataInputStream);
        this.txBytes = DataStreamUtils.readVarLongArray(paramDataInputStream);
        this.txPackets = DataStreamUtils.readVarLongArray(paramDataInputStream);
        this.operations = DataStreamUtils.readVarLongArray(paramDataInputStream);
        this.bucketCount = this.bucketStart.length;
        this.totalBytes = (ArrayUtils.total(this.rxBytes) + ArrayUtils.total(this.txBytes));
        break;
      }
    }
  }
  
  private static void addLong(long[] paramArrayOfLong, int paramInt, long paramLong)
  {
    if (paramArrayOfLong != null) {
      paramArrayOfLong[paramInt] += paramLong;
    }
  }
  
  private void ensureBuckets(long paramLong1, long paramLong2)
  {
    long l4 = this.bucketDuration;
    long l1 = this.bucketDuration;
    long l2 = this.bucketDuration;
    long l3 = this.bucketDuration;
    for (paramLong1 -= paramLong1 % l4; paramLong1 < paramLong2 + (l1 - paramLong2 % l2) % l3; paramLong1 += this.bucketDuration)
    {
      int i = Arrays.binarySearch(this.bucketStart, 0, this.bucketCount, paramLong1);
      if (i < 0) {
        insertBucket(i, paramLong1);
      }
    }
  }
  
  private static long getLong(long[] paramArrayOfLong, int paramInt, long paramLong)
  {
    if (paramArrayOfLong != null) {
      paramLong = paramArrayOfLong[paramInt];
    }
    return paramLong;
  }
  
  private void insertBucket(int paramInt, long paramLong)
  {
    int i;
    if (this.bucketCount >= this.bucketStart.length)
    {
      i = Math.max(this.bucketStart.length, 10) * 3 / 2;
      this.bucketStart = Arrays.copyOf(this.bucketStart, i);
      if (this.activeTime != null) {
        this.activeTime = Arrays.copyOf(this.activeTime, i);
      }
      if (this.rxBytes != null) {
        this.rxBytes = Arrays.copyOf(this.rxBytes, i);
      }
      if (this.rxPackets != null) {
        this.rxPackets = Arrays.copyOf(this.rxPackets, i);
      }
      if (this.txBytes != null) {
        this.txBytes = Arrays.copyOf(this.txBytes, i);
      }
      if (this.txPackets != null) {
        this.txPackets = Arrays.copyOf(this.txPackets, i);
      }
      if (this.operations != null) {
        this.operations = Arrays.copyOf(this.operations, i);
      }
    }
    if (paramInt < this.bucketCount)
    {
      i = paramInt + 1;
      int j = this.bucketCount - paramInt;
      System.arraycopy(this.bucketStart, paramInt, this.bucketStart, i, j);
      if (this.activeTime != null) {
        System.arraycopy(this.activeTime, paramInt, this.activeTime, i, j);
      }
      if (this.rxBytes != null) {
        System.arraycopy(this.rxBytes, paramInt, this.rxBytes, i, j);
      }
      if (this.rxPackets != null) {
        System.arraycopy(this.rxPackets, paramInt, this.rxPackets, i, j);
      }
      if (this.txBytes != null) {
        System.arraycopy(this.txBytes, paramInt, this.txBytes, i, j);
      }
      if (this.txPackets != null) {
        System.arraycopy(this.txPackets, paramInt, this.txPackets, i, j);
      }
      if (this.operations != null) {
        System.arraycopy(this.operations, paramInt, this.operations, i, j);
      }
    }
    this.bucketStart[paramInt] = paramLong;
    setLong(this.activeTime, paramInt, 0L);
    setLong(this.rxBytes, paramInt, 0L);
    setLong(this.rxPackets, paramInt, 0L);
    setLong(this.txBytes, paramInt, 0L);
    setLong(this.txPackets, paramInt, 0L);
    setLong(this.operations, paramInt, 0L);
    this.bucketCount += 1;
  }
  
  public static long randomLong(Random paramRandom, long paramLong1, long paramLong2)
  {
    return ((float)paramLong1 + paramRandom.nextFloat() * (float)(paramLong2 - paramLong1));
  }
  
  private static void setLong(long[] paramArrayOfLong, int paramInt, long paramLong)
  {
    if (paramArrayOfLong != null) {
      paramArrayOfLong[paramInt] = paramLong;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter, boolean paramBoolean)
  {
    int i = 0;
    paramIndentingPrintWriter.print("NetworkStatsHistory: bucketDuration=");
    paramIndentingPrintWriter.println(this.bucketDuration / 1000L);
    paramIndentingPrintWriter.increaseIndent();
    if (paramBoolean) {}
    for (;;)
    {
      if (i > 0)
      {
        paramIndentingPrintWriter.print("(omitting ");
        paramIndentingPrintWriter.print(i);
        paramIndentingPrintWriter.println(" buckets)");
      }
      while (i < this.bucketCount)
      {
        paramIndentingPrintWriter.print("st=");
        paramIndentingPrintWriter.print(this.bucketStart[i] / 1000L);
        if (this.rxBytes != null)
        {
          paramIndentingPrintWriter.print(" rb=");
          paramIndentingPrintWriter.print(this.rxBytes[i]);
        }
        if (this.rxPackets != null)
        {
          paramIndentingPrintWriter.print(" rp=");
          paramIndentingPrintWriter.print(this.rxPackets[i]);
        }
        if (this.txBytes != null)
        {
          paramIndentingPrintWriter.print(" tb=");
          paramIndentingPrintWriter.print(this.txBytes[i]);
        }
        if (this.txPackets != null)
        {
          paramIndentingPrintWriter.print(" tp=");
          paramIndentingPrintWriter.print(this.txPackets[i]);
        }
        if (this.operations != null)
        {
          paramIndentingPrintWriter.print(" op=");
          paramIndentingPrintWriter.print(this.operations[i]);
        }
        paramIndentingPrintWriter.println();
        i += 1;
      }
      i = Math.max(0, this.bucketCount - 32);
    }
    paramIndentingPrintWriter.decreaseIndent();
  }
  
  public void dumpCheckin(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print("d,");
    paramPrintWriter.print(this.bucketDuration / 1000L);
    paramPrintWriter.println();
    int i = 0;
    if (i < this.bucketCount)
    {
      paramPrintWriter.print("b,");
      paramPrintWriter.print(this.bucketStart[i] / 1000L);
      paramPrintWriter.print(',');
      if (this.rxBytes != null)
      {
        paramPrintWriter.print(this.rxBytes[i]);
        label76:
        paramPrintWriter.print(',');
        if (this.rxPackets == null) {
          break label189;
        }
        paramPrintWriter.print(this.rxPackets[i]);
        label99:
        paramPrintWriter.print(',');
        if (this.txBytes == null) {
          break label199;
        }
        paramPrintWriter.print(this.txBytes[i]);
        label122:
        paramPrintWriter.print(',');
        if (this.txPackets == null) {
          break label209;
        }
        paramPrintWriter.print(this.txPackets[i]);
        label145:
        paramPrintWriter.print(',');
        if (this.operations == null) {
          break label219;
        }
        paramPrintWriter.print(this.operations[i]);
      }
      for (;;)
      {
        paramPrintWriter.println();
        i += 1;
        break;
        paramPrintWriter.print("*");
        break label76;
        label189:
        paramPrintWriter.print("*");
        break label99;
        label199:
        paramPrintWriter.print("*");
        break label122;
        label209:
        paramPrintWriter.print("*");
        break label145;
        label219:
        paramPrintWriter.print("*");
      }
    }
  }
  
  public int estimateResizeBuckets(long paramLong)
  {
    return (int)(size() * getBucketDuration() / paramLong);
  }
  
  @Deprecated
  public void generateRandom(long paramLong1, long paramLong2, long paramLong3)
  {
    Random localRandom = new Random();
    float f = localRandom.nextFloat();
    long l = ((float)paramLong3 * f);
    paramLong3 = ((float)paramLong3 * (1.0F - f));
    generateRandom(paramLong1, paramLong2, l, l / 1024L, paramLong3, paramLong3 / 1024L, l / 2048L, localRandom);
  }
  
  @Deprecated
  public void generateRandom(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, Random paramRandom)
  {
    ensureBuckets(paramLong1, paramLong2);
    NetworkStats.Entry localEntry = new NetworkStats.Entry(NetworkStats.IFACE_ALL, -1, 0, 0, 0L, 0L, 0L, 0L, 0L);
    if ((paramLong3 > 1024L) || (paramLong4 > 128L)) {}
    while ((paramLong5 > 1024L) || (paramLong6 > 128L) || (paramLong7 > 32L))
    {
      long l1 = randomLong(paramRandom, paramLong1, paramLong2);
      long l2 = randomLong(paramRandom, 0L, (paramLong2 - l1) / 2L);
      localEntry.rxBytes = randomLong(paramRandom, 0L, paramLong3);
      localEntry.rxPackets = randomLong(paramRandom, 0L, paramLong4);
      localEntry.txBytes = randomLong(paramRandom, 0L, paramLong5);
      localEntry.txPackets = randomLong(paramRandom, 0L, paramLong6);
      localEntry.operations = randomLong(paramRandom, 0L, paramLong7);
      paramLong3 -= localEntry.rxBytes;
      paramLong4 -= localEntry.rxPackets;
      paramLong5 -= localEntry.txBytes;
      paramLong6 -= localEntry.txPackets;
      paramLong7 -= localEntry.operations;
      recordData(l1, l1 + l2, localEntry);
      break;
    }
  }
  
  public long getBucketDuration()
  {
    return this.bucketDuration;
  }
  
  public long getEnd()
  {
    if (this.bucketCount > 0) {
      return this.bucketStart[(this.bucketCount - 1)] + this.bucketDuration;
    }
    return Long.MIN_VALUE;
  }
  
  public int getIndexAfter(long paramLong)
  {
    int i = Arrays.binarySearch(this.bucketStart, 0, this.bucketCount, paramLong);
    if (i < 0) {
      i = i;
    }
    for (;;)
    {
      return MathUtils.constrain(i, 0, this.bucketCount - 1);
      i += 1;
    }
  }
  
  public int getIndexBefore(long paramLong)
  {
    int i = Arrays.binarySearch(this.bucketStart, 0, this.bucketCount, paramLong);
    if (i < 0) {
      i -= 1;
    }
    for (;;)
    {
      return MathUtils.constrain(i, 0, this.bucketCount - 1);
      i -= 1;
    }
  }
  
  public long getStart()
  {
    if (this.bucketCount > 0) {
      return this.bucketStart[0];
    }
    return Long.MAX_VALUE;
  }
  
  public long getTotalBytes()
  {
    return this.totalBytes;
  }
  
  public Entry getValues(int paramInt, Entry paramEntry)
  {
    if (paramEntry != null) {}
    for (;;)
    {
      paramEntry.bucketStart = this.bucketStart[paramInt];
      paramEntry.bucketDuration = this.bucketDuration;
      paramEntry.activeTime = getLong(this.activeTime, paramInt, -1L);
      paramEntry.rxBytes = getLong(this.rxBytes, paramInt, -1L);
      paramEntry.rxPackets = getLong(this.rxPackets, paramInt, -1L);
      paramEntry.txBytes = getLong(this.txBytes, paramInt, -1L);
      paramEntry.txPackets = getLong(this.txPackets, paramInt, -1L);
      paramEntry.operations = getLong(this.operations, paramInt, -1L);
      return paramEntry;
      paramEntry = new Entry();
    }
  }
  
  public Entry getValues(long paramLong1, long paramLong2, long paramLong3, Entry paramEntry)
  {
    long l1;
    label29:
    label46:
    label63:
    label80:
    label97:
    label114:
    int i;
    if (paramEntry != null)
    {
      paramEntry.bucketDuration = (paramLong2 - paramLong1);
      paramEntry.bucketStart = paramLong1;
      if (this.activeTime == null) {
        break label173;
      }
      l1 = 0L;
      paramEntry.activeTime = l1;
      if (this.rxBytes == null) {
        break label181;
      }
      l1 = 0L;
      paramEntry.rxBytes = l1;
      if (this.rxPackets == null) {
        break label189;
      }
      l1 = 0L;
      paramEntry.rxPackets = l1;
      if (this.txBytes == null) {
        break label197;
      }
      l1 = 0L;
      paramEntry.txBytes = l1;
      if (this.txPackets == null) {
        break label205;
      }
      l1 = 0L;
      paramEntry.txPackets = l1;
      if (this.operations == null) {
        break label213;
      }
      l1 = 0L;
      paramEntry.operations = l1;
      i = getIndexAfter(paramLong2);
    }
    long l2;
    for (;;)
    {
      if (i >= 0)
      {
        l2 = this.bucketStart[i];
        l1 = l2 + this.bucketDuration;
        if (l1 > paramLong1) {}
      }
      else
      {
        return paramEntry;
        paramEntry = new Entry();
        break;
        label173:
        l1 = -1L;
        break label29;
        label181:
        l1 = -1L;
        break label46;
        label189:
        l1 = -1L;
        break label63;
        label197:
        l1 = -1L;
        break label80;
        label205:
        l1 = -1L;
        break label97;
        label213:
        l1 = -1L;
        break label114;
      }
      if (l2 < paramLong2) {
        break label237;
      }
      i -= 1;
    }
    label237:
    if ((l2 < paramLong3) && (l1 > paramLong3)) {}
    for (int j = 1;; j = 0)
    {
      if (j == 0) {
        break label481;
      }
      l1 = this.bucketDuration;
      label267:
      if (l1 <= 0L) {
        break label503;
      }
      if (this.activeTime != null) {
        paramEntry.activeTime += this.activeTime[i] * l1 / this.bucketDuration;
      }
      if (this.rxBytes != null) {
        paramEntry.rxBytes += this.rxBytes[i] * l1 / this.bucketDuration;
      }
      if (this.rxPackets != null) {
        paramEntry.rxPackets += this.rxPackets[i] * l1 / this.bucketDuration;
      }
      if (this.txBytes != null) {
        paramEntry.txBytes += this.txBytes[i] * l1 / this.bucketDuration;
      }
      if (this.txPackets != null) {
        paramEntry.txPackets += this.txPackets[i] * l1 / this.bucketDuration;
      }
      if (this.operations == null) {
        break;
      }
      paramEntry.operations += this.operations[i] * l1 / this.bucketDuration;
      break;
    }
    label481:
    if (l1 < paramLong2) {
      label488:
      if (l2 <= paramLong1) {
        break label511;
      }
    }
    for (;;)
    {
      l1 -= l2;
      break label267;
      label503:
      break;
      l1 = paramLong2;
      break label488;
      label511:
      l2 = paramLong1;
    }
  }
  
  public Entry getValues(long paramLong1, long paramLong2, Entry paramEntry)
  {
    return getValues(paramLong1, paramLong2, Long.MAX_VALUE, paramEntry);
  }
  
  public boolean intersects(long paramLong1, long paramLong2)
  {
    long l1 = getStart();
    long l2 = getEnd();
    if ((paramLong1 >= l1) && (paramLong1 <= l2)) {
      return true;
    }
    if ((paramLong2 >= l1) && (paramLong2 <= l2)) {
      return true;
    }
    if ((l1 >= paramLong1) && (l1 <= paramLong2)) {
      return true;
    }
    return (l2 >= paramLong1) && (l2 <= paramLong2);
  }
  
  @Deprecated
  public void recordData(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    recordData(paramLong1, paramLong2, new NetworkStats.Entry(NetworkStats.IFACE_ALL, -1, 0, 0, paramLong3, 0L, paramLong4, 0L, 0L));
  }
  
  public void recordData(long paramLong1, long paramLong2, NetworkStats.Entry paramEntry)
  {
    long l6 = paramEntry.rxBytes;
    long l5 = paramEntry.rxPackets;
    long l2 = paramEntry.txBytes;
    long l1 = paramEntry.txPackets;
    long l4 = paramEntry.operations;
    if (paramEntry.isNegative())
    {
      Log.d(TAG, "entry.isNegative() tried recording negative data");
      Log.d(TAG, "rxBytes = " + l6);
      Log.d(TAG, "rxPackets = " + l5);
      Log.d(TAG, "txBytes = " + l2);
      Log.d(TAG, "txPackets = " + l1);
      Log.d(TAG, "operations = " + l4);
      return;
    }
    if (paramEntry.isEmpty()) {
      return;
    }
    ensureBuckets(paramLong1, paramLong2);
    long l3 = paramLong2 - paramLong1;
    int i = getIndexAfter(paramLong2);
    long l7;
    long l8;
    if (i >= 0)
    {
      l7 = this.bucketStart[i];
      l8 = l7 + this.bucketDuration;
      if (l8 >= paramLong1) {}
    }
    else
    {
      this.totalBytes += paramEntry.rxBytes + paramEntry.txBytes;
      return;
    }
    long l12;
    long l11;
    long l10;
    long l9;
    if (l7 > paramLong2)
    {
      l12 = l1;
      l11 = l2;
      l10 = l5;
      l9 = l6;
      l8 = l4;
      l7 = l3;
    }
    for (;;)
    {
      i -= 1;
      l3 = l7;
      l4 = l8;
      l6 = l9;
      l5 = l10;
      l2 = l11;
      l1 = l12;
      break;
      long l13 = Math.min(l8, paramLong2) - Math.max(l7, paramLong1);
      l7 = l3;
      l8 = l4;
      l9 = l6;
      l10 = l5;
      l11 = l2;
      l12 = l1;
      if (l13 > 0L)
      {
        l9 = l6 * l13 / l3;
        l10 = l5 * l13 / l3;
        l11 = l2 * l13 / l3;
        l8 = l1 * l13 / l3;
        l7 = l4 * l13 / l3;
        addLong(this.activeTime, i, l13);
        addLong(this.rxBytes, i, l9);
        l9 = l6 - l9;
        addLong(this.rxPackets, i, l10);
        l10 = l5 - l10;
        addLong(this.txBytes, i, l11);
        l11 = l2 - l11;
        addLong(this.txPackets, i, l8);
        l12 = l1 - l8;
        addLong(this.operations, i, l7);
        l8 = l4 - l7;
        l7 = l3 - l13;
      }
    }
  }
  
  public void recordEntireHistory(NetworkStatsHistory paramNetworkStatsHistory)
  {
    recordHistory(paramNetworkStatsHistory, Long.MIN_VALUE, Long.MAX_VALUE);
  }
  
  public void recordHistory(NetworkStatsHistory paramNetworkStatsHistory, long paramLong1, long paramLong2)
  {
    NetworkStats.Entry localEntry = new NetworkStats.Entry(NetworkStats.IFACE_ALL, -1, 0, 0, 0L, 0L, 0L, 0L, 0L);
    int i = 0;
    if (i < paramNetworkStatsHistory.bucketCount)
    {
      long l1 = paramNetworkStatsHistory.bucketStart[i];
      long l2 = l1 + paramNetworkStatsHistory.bucketDuration;
      if ((l1 < paramLong1) || (l2 > paramLong2)) {}
      for (;;)
      {
        i += 1;
        break;
        localEntry.rxBytes = getLong(paramNetworkStatsHistory.rxBytes, i, 0L);
        localEntry.rxPackets = getLong(paramNetworkStatsHistory.rxPackets, i, 0L);
        localEntry.txBytes = getLong(paramNetworkStatsHistory.txBytes, i, 0L);
        localEntry.txPackets = getLong(paramNetworkStatsHistory.txPackets, i, 0L);
        localEntry.operations = getLong(paramNetworkStatsHistory.operations, i, 0L);
        recordData(l1, l2, localEntry);
      }
    }
  }
  
  @Deprecated
  public void removeBucketsBefore(long paramLong)
  {
    int i = 0;
    for (;;)
    {
      if ((i >= this.bucketCount) || (this.bucketStart[i] + this.bucketDuration > paramLong))
      {
        if (i > 0)
        {
          int j = this.bucketStart.length;
          this.bucketStart = Arrays.copyOfRange(this.bucketStart, i, j);
          if (this.activeTime != null) {
            this.activeTime = Arrays.copyOfRange(this.activeTime, i, j);
          }
          if (this.rxBytes != null) {
            this.rxBytes = Arrays.copyOfRange(this.rxBytes, i, j);
          }
          if (this.rxPackets != null) {
            this.rxPackets = Arrays.copyOfRange(this.rxPackets, i, j);
          }
          if (this.txBytes != null) {
            this.txBytes = Arrays.copyOfRange(this.txBytes, i, j);
          }
          if (this.txPackets != null) {
            this.txPackets = Arrays.copyOfRange(this.txPackets, i, j);
          }
          if (this.operations != null) {
            this.operations = Arrays.copyOfRange(this.operations, i, j);
          }
          this.bucketCount -= i;
        }
        return;
      }
      i += 1;
    }
  }
  
  public int size()
  {
    return this.bucketCount;
  }
  
  public String toString()
  {
    CharArrayWriter localCharArrayWriter = new CharArrayWriter();
    dump(new IndentingPrintWriter(localCharArrayWriter, "  "), false);
    return localCharArrayWriter.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.bucketDuration);
    ParcelUtils.writeLongArray(paramParcel, this.bucketStart, this.bucketCount);
    ParcelUtils.writeLongArray(paramParcel, this.activeTime, this.bucketCount);
    ParcelUtils.writeLongArray(paramParcel, this.rxBytes, this.bucketCount);
    ParcelUtils.writeLongArray(paramParcel, this.rxPackets, this.bucketCount);
    ParcelUtils.writeLongArray(paramParcel, this.txBytes, this.bucketCount);
    ParcelUtils.writeLongArray(paramParcel, this.txPackets, this.bucketCount);
    ParcelUtils.writeLongArray(paramParcel, this.operations, this.bucketCount);
    paramParcel.writeLong(this.totalBytes);
  }
  
  public void writeToStream(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeInt(3);
    paramDataOutputStream.writeLong(this.bucketDuration);
    DataStreamUtils.writeVarLongArray(paramDataOutputStream, this.bucketStart, this.bucketCount);
    DataStreamUtils.writeVarLongArray(paramDataOutputStream, this.activeTime, this.bucketCount);
    DataStreamUtils.writeVarLongArray(paramDataOutputStream, this.rxBytes, this.bucketCount);
    DataStreamUtils.writeVarLongArray(paramDataOutputStream, this.rxPackets, this.bucketCount);
    DataStreamUtils.writeVarLongArray(paramDataOutputStream, this.txBytes, this.bucketCount);
    DataStreamUtils.writeVarLongArray(paramDataOutputStream, this.txPackets, this.bucketCount);
    DataStreamUtils.writeVarLongArray(paramDataOutputStream, this.operations, this.bucketCount);
  }
  
  public static class DataStreamUtils
  {
    @Deprecated
    public static long[] readFullLongArray(DataInputStream paramDataInputStream)
      throws IOException
    {
      int i = paramDataInputStream.readInt();
      if (i < 0) {
        throw new ProtocolException("negative array size");
      }
      long[] arrayOfLong = new long[i];
      i = 0;
      while (i < arrayOfLong.length)
      {
        arrayOfLong[i] = paramDataInputStream.readLong();
        i += 1;
      }
      return arrayOfLong;
    }
    
    public static long readVarLong(DataInputStream paramDataInputStream)
      throws IOException
    {
      int i = 0;
      long l = 0L;
      while (i < 64)
      {
        int j = paramDataInputStream.readByte();
        l |= (j & 0x7F) << i;
        if ((j & 0x80) == 0) {
          return l;
        }
        i += 7;
      }
      throw new ProtocolException("malformed long");
    }
    
    public static long[] readVarLongArray(DataInputStream paramDataInputStream)
      throws IOException
    {
      int i = paramDataInputStream.readInt();
      if (i == -1) {
        return null;
      }
      if (i < 0) {
        throw new ProtocolException("negative array size");
      }
      long[] arrayOfLong = new long[i];
      i = 0;
      while (i < arrayOfLong.length)
      {
        arrayOfLong[i] = readVarLong(paramDataInputStream);
        i += 1;
      }
      return arrayOfLong;
    }
    
    public static void writeVarLong(DataOutputStream paramDataOutputStream, long paramLong)
      throws IOException
    {
      for (;;)
      {
        if ((0xFFFFFFFFFFFFFF80 & paramLong) == 0L)
        {
          paramDataOutputStream.writeByte((int)paramLong);
          return;
        }
        paramDataOutputStream.writeByte((int)paramLong & 0x7F | 0x80);
        paramLong >>>= 7;
      }
    }
    
    public static void writeVarLongArray(DataOutputStream paramDataOutputStream, long[] paramArrayOfLong, int paramInt)
      throws IOException
    {
      if (paramArrayOfLong == null)
      {
        paramDataOutputStream.writeInt(-1);
        return;
      }
      if (paramInt > paramArrayOfLong.length) {
        throw new IllegalArgumentException("size larger than length");
      }
      paramDataOutputStream.writeInt(paramInt);
      int i = 0;
      while (i < paramInt)
      {
        writeVarLong(paramDataOutputStream, paramArrayOfLong[i]);
        i += 1;
      }
    }
  }
  
  public static class Entry
  {
    public static final long UNKNOWN = -1L;
    public long activeTime;
    public long bucketDuration;
    public long bucketStart;
    public long operations;
    public long rxBytes;
    public long rxPackets;
    public long txBytes;
    public long txPackets;
  }
  
  public static class ParcelUtils
  {
    public static long[] readLongArray(Parcel paramParcel)
    {
      int i = paramParcel.readInt();
      if (i == -1) {
        return null;
      }
      long[] arrayOfLong = new long[i];
      i = 0;
      while (i < arrayOfLong.length)
      {
        arrayOfLong[i] = paramParcel.readLong();
        i += 1;
      }
      return arrayOfLong;
    }
    
    public static void writeLongArray(Parcel paramParcel, long[] paramArrayOfLong, int paramInt)
    {
      if (paramArrayOfLong == null)
      {
        paramParcel.writeInt(-1);
        return;
      }
      if (paramInt > paramArrayOfLong.length) {
        throw new IllegalArgumentException("size larger than length");
      }
      paramParcel.writeInt(paramInt);
      int i = 0;
      while (i < paramInt)
      {
        paramParcel.writeLong(paramArrayOfLong[i]);
        i += 1;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkStatsHistory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */