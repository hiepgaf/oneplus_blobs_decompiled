package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.util.ArrayUtils;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import libcore.util.EmptyArray;

public class NetworkStats
  implements Parcelable
{
  public static final Parcelable.Creator<NetworkStats> CREATOR = new Parcelable.Creator()
  {
    public NetworkStats createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NetworkStats(paramAnonymousParcel);
    }
    
    public NetworkStats[] newArray(int paramAnonymousInt)
    {
      return new NetworkStats[paramAnonymousInt];
    }
  };
  public static final String IFACE_ALL;
  public static final int ROAMING_ALL = -1;
  public static final int ROAMING_NO = 0;
  public static final int ROAMING_YES = 1;
  public static final int SET_ALL = -1;
  public static final int SET_DBG_VPN_IN = 1001;
  public static final int SET_DBG_VPN_OUT = 1002;
  public static final int SET_DEBUG_START = 1000;
  public static final int SET_DEFAULT = 0;
  public static final int SET_FOREGROUND = 1;
  private static final String TAG = "NetworkStats";
  public static final int TAG_ALL = -1;
  public static final int TAG_NONE = 0;
  public static final int UID_ALL = -1;
  private int capacity;
  private long elapsedRealtime;
  private String[] iface;
  private long[] operations;
  private int[] roaming;
  private long[] rxBytes;
  private long[] rxPackets;
  private int[] set;
  private int size;
  private int[] tag;
  private long[] txBytes;
  private long[] txPackets;
  private int[] uid;
  
  public NetworkStats(long paramLong, int paramInt)
  {
    this.elapsedRealtime = paramLong;
    this.size = 0;
    if (paramInt >= 0)
    {
      this.capacity = paramInt;
      this.iface = new String[paramInt];
      this.uid = new int[paramInt];
      this.set = new int[paramInt];
      this.tag = new int[paramInt];
      this.roaming = new int[paramInt];
      this.rxBytes = new long[paramInt];
      this.rxPackets = new long[paramInt];
      this.txBytes = new long[paramInt];
      this.txPackets = new long[paramInt];
      this.operations = new long[paramInt];
      return;
    }
    this.capacity = 0;
    this.iface = EmptyArray.STRING;
    this.uid = EmptyArray.INT;
    this.set = EmptyArray.INT;
    this.tag = EmptyArray.INT;
    this.roaming = EmptyArray.INT;
    this.rxBytes = EmptyArray.LONG;
    this.rxPackets = EmptyArray.LONG;
    this.txBytes = EmptyArray.LONG;
    this.txPackets = EmptyArray.LONG;
    this.operations = EmptyArray.LONG;
  }
  
  public NetworkStats(Parcel paramParcel)
  {
    this.elapsedRealtime = paramParcel.readLong();
    this.size = paramParcel.readInt();
    this.capacity = paramParcel.readInt();
    this.iface = paramParcel.createStringArray();
    this.uid = paramParcel.createIntArray();
    this.set = paramParcel.createIntArray();
    this.tag = paramParcel.createIntArray();
    this.roaming = paramParcel.createIntArray();
    this.rxBytes = paramParcel.createLongArray();
    this.rxPackets = paramParcel.createLongArray();
    this.txBytes = paramParcel.createLongArray();
    this.txPackets = paramParcel.createLongArray();
    this.operations = paramParcel.createLongArray();
  }
  
  private Entry addTrafficToApplications(int paramInt, String paramString1, String paramString2, Entry paramEntry1, Entry paramEntry2)
  {
    Entry localEntry1 = new Entry();
    Entry localEntry2 = new Entry();
    localEntry2.iface = paramString2;
    int i = 0;
    if (i < this.size)
    {
      if ((Objects.equals(this.iface[i], paramString1)) && (this.uid[i] != paramInt))
      {
        if (paramEntry1.rxBytes <= 0L) {
          break label327;
        }
        localEntry2.rxBytes = (paramEntry2.rxBytes * this.rxBytes[i] / paramEntry1.rxBytes);
        label95:
        if (paramEntry1.rxPackets <= 0L) {
          break label336;
        }
        localEntry2.rxPackets = (paramEntry2.rxPackets * this.rxPackets[i] / paramEntry1.rxPackets);
        label129:
        if (paramEntry1.txBytes <= 0L) {
          break label345;
        }
        localEntry2.txBytes = (paramEntry2.txBytes * this.txBytes[i] / paramEntry1.txBytes);
        label163:
        if (paramEntry1.txPackets <= 0L) {
          break label354;
        }
        localEntry2.txPackets = (paramEntry2.txPackets * this.txPackets[i] / paramEntry1.txPackets);
        label197:
        if (paramEntry1.operations <= 0L) {
          break label363;
        }
      }
      label327:
      label336:
      label345:
      label354:
      label363:
      for (localEntry2.operations = (paramEntry2.operations * this.operations[i] / paramEntry1.operations);; localEntry2.operations = 0L)
      {
        localEntry2.uid = this.uid[i];
        localEntry2.tag = this.tag[i];
        localEntry2.set = this.set[i];
        localEntry2.roaming = this.roaming[i];
        combineValues(localEntry2);
        if (this.tag[i] == 0)
        {
          localEntry1.add(localEntry2);
          localEntry2.set = 1001;
          combineValues(localEntry2);
        }
        i += 1;
        break;
        localEntry2.rxBytes = 0L;
        break label95;
        localEntry2.rxPackets = 0L;
        break label129;
        localEntry2.txBytes = 0L;
        break label163;
        localEntry2.txPackets = 0L;
        break label197;
      }
    }
    return localEntry1;
  }
  
  private void deductTrafficFromVpnApp(int paramInt, String paramString, Entry paramEntry)
  {
    paramEntry.uid = paramInt;
    paramEntry.set = 1002;
    paramEntry.tag = 0;
    paramEntry.iface = paramString;
    paramEntry.roaming = -1;
    combineValues(paramEntry);
    int i = findIndex(paramString, paramInt, 0, 0, 0);
    if (i != -1) {
      tunSubtract(i, this, paramEntry);
    }
    paramInt = findIndex(paramString, paramInt, 1, 0, 0);
    if (paramInt != -1) {
      tunSubtract(paramInt, this, paramEntry);
    }
  }
  
  private Entry getTotal(Entry paramEntry, HashSet<String> paramHashSet, int paramInt, boolean paramBoolean)
  {
    int i;
    label59:
    int j;
    if (paramEntry != null)
    {
      paramEntry.iface = IFACE_ALL;
      paramEntry.uid = paramInt;
      paramEntry.set = -1;
      paramEntry.tag = 0;
      paramEntry.roaming = -1;
      paramEntry.rxBytes = 0L;
      paramEntry.rxPackets = 0L;
      paramEntry.txBytes = 0L;
      paramEntry.txPackets = 0L;
      paramEntry.operations = 0L;
      i = 0;
      if (i >= this.size) {
        return paramEntry;
      }
      if ((paramInt != -1) && (paramInt != this.uid[i])) {
        break label229;
      }
      j = 1;
      label87:
      if (paramHashSet == null) {
        break label235;
      }
    }
    label229:
    label235:
    for (boolean bool = paramHashSet.contains(this.iface[i]);; bool = true)
    {
      if ((j != 0) && (bool) && ((this.tag[i] == 0) || (paramBoolean)))
      {
        paramEntry.rxBytes += this.rxBytes[i];
        paramEntry.rxPackets += this.rxPackets[i];
        paramEntry.txBytes += this.txBytes[i];
        paramEntry.txPackets += this.txPackets[i];
        paramEntry.operations += this.operations[i];
      }
      i += 1;
      break label59;
      paramEntry = new Entry();
      break;
      j = 0;
      break label87;
    }
    return paramEntry;
  }
  
  public static String roamingToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case -1: 
      return "ALL";
    case 0: 
      return "NO";
    }
    return "YES";
  }
  
  public static boolean setMatches(int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {
      return true;
    }
    return (paramInt1 == -1) && (paramInt2 < 1000);
  }
  
  public static String setToCheckinString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unk";
    case -1: 
      return "all";
    case 0: 
      return "def";
    case 1: 
      return "fg";
    case 1001: 
      return "vpnin";
    }
    return "vpnout";
  }
  
  public static String setToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case -1: 
      return "ALL";
    case 0: 
      return "DEFAULT";
    case 1: 
      return "FOREGROUND";
    case 1001: 
      return "DBG_VPN_IN";
    }
    return "DBG_VPN_OUT";
  }
  
  public static <C> NetworkStats subtract(NetworkStats paramNetworkStats1, NetworkStats paramNetworkStats2, NonMonotonicObserver<C> paramNonMonotonicObserver, C paramC)
  {
    return subtract(paramNetworkStats1, paramNetworkStats2, paramNonMonotonicObserver, paramC, null);
  }
  
  public static <C> NetworkStats subtract(NetworkStats paramNetworkStats1, NetworkStats paramNetworkStats2, NonMonotonicObserver<C> paramNonMonotonicObserver, C paramC, NetworkStats paramNetworkStats3)
  {
    long l2 = paramNetworkStats1.elapsedRealtime - paramNetworkStats2.elapsedRealtime;
    long l1 = l2;
    if (l2 < 0L)
    {
      if (paramNonMonotonicObserver != null) {
        paramNonMonotonicObserver.foundNonMonotonic(paramNetworkStats1, -1, paramNetworkStats2, -1, paramC);
      }
      l1 = 0L;
    }
    Entry localEntry = new Entry();
    NetworkStats localNetworkStats;
    if ((paramNetworkStats3 != null) && (paramNetworkStats3.capacity >= paramNetworkStats1.size))
    {
      localNetworkStats = paramNetworkStats3;
      paramNetworkStats3.size = 0;
      paramNetworkStats3.elapsedRealtime = l1;
    }
    int i;
    int j;
    for (;;)
    {
      i = 0;
      for (;;)
      {
        if (i >= paramNetworkStats1.size) {
          break label532;
        }
        localEntry.iface = paramNetworkStats1.iface[i];
        localEntry.uid = paramNetworkStats1.uid[i];
        localEntry.set = paramNetworkStats1.set[i];
        localEntry.tag = paramNetworkStats1.tag[i];
        localEntry.roaming = paramNetworkStats1.roaming[i];
        j = paramNetworkStats2.findIndexHinted(localEntry.iface, localEntry.uid, localEntry.set, localEntry.tag, localEntry.roaming, i);
        if (j != -1) {
          break;
        }
        localEntry.rxBytes = paramNetworkStats1.rxBytes[i];
        localEntry.rxPackets = paramNetworkStats1.rxPackets[i];
        localEntry.txBytes = paramNetworkStats1.txBytes[i];
        localEntry.txPackets = paramNetworkStats1.txPackets[i];
        localEntry.operations = paramNetworkStats1.operations[i];
        localNetworkStats.addValues(localEntry);
        i += 1;
      }
      localNetworkStats = new NetworkStats(l1, paramNetworkStats1.size);
    }
    localEntry.rxBytes = (paramNetworkStats1.rxBytes[i] - paramNetworkStats2.rxBytes[j]);
    localEntry.rxPackets = (paramNetworkStats1.rxPackets[i] - paramNetworkStats2.rxPackets[j]);
    localEntry.txBytes = (paramNetworkStats1.txBytes[i] - paramNetworkStats2.txBytes[j]);
    localEntry.txPackets = (paramNetworkStats1.txPackets[i] - paramNetworkStats2.txPackets[j]);
    localEntry.operations = (paramNetworkStats1.operations[i] - paramNetworkStats2.operations[j]);
    if ((localEntry.rxBytes < 0L) || (localEntry.rxPackets < 0L)) {}
    for (;;)
    {
      if (paramNonMonotonicObserver != null) {
        paramNonMonotonicObserver.foundNonMonotonic(paramNetworkStats1, i, paramNetworkStats2, j, paramC);
      }
      localEntry.rxBytes = Math.max(localEntry.rxBytes, 0L);
      localEntry.rxPackets = Math.max(localEntry.rxPackets, 0L);
      localEntry.txBytes = Math.max(localEntry.txBytes, 0L);
      localEntry.txPackets = Math.max(localEntry.txPackets, 0L);
      localEntry.operations = Math.max(localEntry.operations, 0L);
      break;
      if ((localEntry.txBytes >= 0L) && (localEntry.txPackets >= 0L)) {
        if (localEntry.operations >= 0L) {
          break;
        }
      }
    }
    label532:
    return localNetworkStats;
  }
  
  public static String tagToString(int paramInt)
  {
    return "0x" + Integer.toHexString(paramInt);
  }
  
  private void tunAdjustmentInit(int paramInt, String paramString1, String paramString2, Entry paramEntry1, Entry paramEntry2)
  {
    Entry localEntry = new Entry();
    int i = 0;
    while (i < this.size)
    {
      getValues(i, localEntry);
      if (localEntry.uid == -1) {
        throw new IllegalStateException("Cannot adjust VPN accounting on an iface aggregated NetworkStats.");
      }
      if ((localEntry.set == 1001) || (localEntry.set == 1002)) {
        throw new IllegalStateException("Cannot adjust VPN accounting on a NetworkStats containing SET_DBG_VPN_*");
      }
      if ((localEntry.uid == paramInt) && (localEntry.tag == 0) && (Objects.equals(paramString2, localEntry.iface))) {
        paramEntry2.add(localEntry);
      }
      if ((localEntry.uid != paramInt) && (localEntry.tag == 0) && (Objects.equals(paramString1, localEntry.iface))) {
        paramEntry1.add(localEntry);
      }
      i += 1;
    }
  }
  
  private static Entry tunGetPool(Entry paramEntry1, Entry paramEntry2)
  {
    Entry localEntry = new Entry();
    localEntry.rxBytes = Math.min(paramEntry1.rxBytes, paramEntry2.rxBytes);
    localEntry.rxPackets = Math.min(paramEntry1.rxPackets, paramEntry2.rxPackets);
    localEntry.txBytes = Math.min(paramEntry1.txBytes, paramEntry2.txBytes);
    localEntry.txPackets = Math.min(paramEntry1.txPackets, paramEntry2.txPackets);
    localEntry.operations = Math.min(paramEntry1.operations, paramEntry2.operations);
    return localEntry;
  }
  
  private static void tunSubtract(int paramInt, NetworkStats paramNetworkStats, Entry paramEntry)
  {
    long l = Math.min(paramNetworkStats.rxBytes[paramInt], paramEntry.rxBytes);
    long[] arrayOfLong = paramNetworkStats.rxBytes;
    arrayOfLong[paramInt] -= l;
    paramEntry.rxBytes -= l;
    l = Math.min(paramNetworkStats.rxPackets[paramInt], paramEntry.rxPackets);
    arrayOfLong = paramNetworkStats.rxPackets;
    arrayOfLong[paramInt] -= l;
    paramEntry.rxPackets -= l;
    l = Math.min(paramNetworkStats.txBytes[paramInt], paramEntry.txBytes);
    arrayOfLong = paramNetworkStats.txBytes;
    arrayOfLong[paramInt] -= l;
    paramEntry.txBytes -= l;
    l = Math.min(paramNetworkStats.txPackets[paramInt], paramEntry.txPackets);
    paramNetworkStats = paramNetworkStats.txPackets;
    paramNetworkStats[paramInt] -= l;
    paramEntry.txPackets -= l;
  }
  
  public NetworkStats addIfaceValues(String paramString, long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    return addValues(paramString, -1, 0, 0, paramLong1, paramLong2, paramLong3, paramLong4, 0L);
  }
  
  public NetworkStats addValues(Entry paramEntry)
  {
    if (this.size >= this.capacity)
    {
      int i = Math.max(this.size, 10) * 3 / 2;
      this.iface = ((String[])Arrays.copyOf(this.iface, i));
      this.uid = Arrays.copyOf(this.uid, i);
      this.set = Arrays.copyOf(this.set, i);
      this.tag = Arrays.copyOf(this.tag, i);
      this.roaming = Arrays.copyOf(this.roaming, i);
      this.rxBytes = Arrays.copyOf(this.rxBytes, i);
      this.rxPackets = Arrays.copyOf(this.rxPackets, i);
      this.txBytes = Arrays.copyOf(this.txBytes, i);
      this.txPackets = Arrays.copyOf(this.txPackets, i);
      this.operations = Arrays.copyOf(this.operations, i);
      this.capacity = i;
    }
    this.iface[this.size] = paramEntry.iface;
    this.uid[this.size] = paramEntry.uid;
    this.set[this.size] = paramEntry.set;
    this.tag[this.size] = paramEntry.tag;
    this.roaming[this.size] = paramEntry.roaming;
    this.rxBytes[this.size] = paramEntry.rxBytes;
    this.rxPackets[this.size] = paramEntry.rxPackets;
    this.txBytes[this.size] = paramEntry.txBytes;
    this.txPackets[this.size] = paramEntry.txPackets;
    this.operations[this.size] = paramEntry.operations;
    this.size += 1;
    return this;
  }
  
  public NetworkStats addValues(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
  {
    return addValues(new Entry(paramString, paramInt1, paramInt2, paramInt3, paramInt4, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5));
  }
  
  public NetworkStats addValues(String paramString, int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
  {
    return addValues(new Entry(paramString, paramInt1, paramInt2, paramInt3, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5));
  }
  
  public NetworkStats clone()
  {
    NetworkStats localNetworkStats = new NetworkStats(this.elapsedRealtime, this.size);
    Entry localEntry = null;
    int i = 0;
    while (i < this.size)
    {
      localEntry = getValues(i, localEntry);
      localNetworkStats.addValues(localEntry);
      i += 1;
    }
    return localNetworkStats;
  }
  
  public void combineAllValues(NetworkStats paramNetworkStats)
  {
    Entry localEntry = null;
    int i = 0;
    while (i < paramNetworkStats.size)
    {
      localEntry = paramNetworkStats.getValues(i, localEntry);
      combineValues(localEntry);
      i += 1;
    }
  }
  
  public NetworkStats combineValues(Entry paramEntry)
  {
    int i = findIndex(paramEntry.iface, paramEntry.uid, paramEntry.set, paramEntry.tag, paramEntry.roaming);
    if (i == -1)
    {
      addValues(paramEntry);
      return this;
    }
    long[] arrayOfLong = this.rxBytes;
    arrayOfLong[i] += paramEntry.rxBytes;
    arrayOfLong = this.rxPackets;
    arrayOfLong[i] += paramEntry.rxPackets;
    arrayOfLong = this.txBytes;
    arrayOfLong[i] += paramEntry.txBytes;
    arrayOfLong = this.txPackets;
    arrayOfLong[i] += paramEntry.txPackets;
    arrayOfLong = this.operations;
    arrayOfLong[i] += paramEntry.operations;
    return this;
  }
  
  public NetworkStats combineValues(String paramString, int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
  {
    return combineValues(new Entry(paramString, paramInt1, paramInt2, paramInt3, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5));
  }
  
  @Deprecated
  public NetworkStats combineValues(String paramString, int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
  {
    return combineValues(paramString, paramInt1, 0, paramInt2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("NetworkStats: elapsedRealtime=");
    paramPrintWriter.println(this.elapsedRealtime);
    int i = 0;
    while (i < this.size)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  [");
      paramPrintWriter.print(i);
      paramPrintWriter.print("]");
      paramPrintWriter.print(" iface=");
      paramPrintWriter.print(this.iface[i]);
      paramPrintWriter.print(" uid=");
      paramPrintWriter.print(this.uid[i]);
      paramPrintWriter.print(" set=");
      paramPrintWriter.print(setToString(this.set[i]));
      paramPrintWriter.print(" tag=");
      paramPrintWriter.print(tagToString(this.tag[i]));
      paramPrintWriter.print(" roaming=");
      paramPrintWriter.print(roamingToString(this.roaming[i]));
      paramPrintWriter.print(" rxBytes=");
      paramPrintWriter.print(this.rxBytes[i]);
      paramPrintWriter.print(" rxPackets=");
      paramPrintWriter.print(this.rxPackets[i]);
      paramPrintWriter.print(" txBytes=");
      paramPrintWriter.print(this.txBytes[i]);
      paramPrintWriter.print(" txPackets=");
      paramPrintWriter.print(this.txPackets[i]);
      paramPrintWriter.print(" operations=");
      paramPrintWriter.println(this.operations[i]);
      i += 1;
    }
  }
  
  public int findIndex(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = 0;
    while (i < this.size)
    {
      if ((paramInt1 == this.uid[i]) && (paramInt2 == this.set[i]) && (paramInt3 == this.tag[i]) && (paramInt4 == this.roaming[i]) && (Objects.equals(paramString, this.iface[i]))) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public int findIndexHinted(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = 0;
    while (i < this.size)
    {
      int j = i / 2;
      if (i % 2 == 0) {}
      for (j = (paramInt5 + j) % this.size; (paramInt1 == this.uid[j]) && (paramInt2 == this.set[j]) && (paramInt3 == this.tag[j]) && (paramInt4 == this.roaming[j]) && (Objects.equals(paramString, this.iface[j])); j = (this.size + paramInt5 - j - 1) % this.size) {
        return j;
      }
      i += 1;
    }
    return -1;
  }
  
  public long getElapsedRealtime()
  {
    return this.elapsedRealtime;
  }
  
  public long getElapsedRealtimeAge()
  {
    return SystemClock.elapsedRealtime() - this.elapsedRealtime;
  }
  
  public Entry getTotal(Entry paramEntry)
  {
    return getTotal(paramEntry, null, -1, false);
  }
  
  public Entry getTotal(Entry paramEntry, int paramInt)
  {
    return getTotal(paramEntry, null, paramInt, false);
  }
  
  public Entry getTotal(Entry paramEntry, HashSet<String> paramHashSet)
  {
    return getTotal(paramEntry, paramHashSet, -1, false);
  }
  
  public long getTotalBytes()
  {
    Entry localEntry = getTotal(null);
    return localEntry.rxBytes + localEntry.txBytes;
  }
  
  public Entry getTotalIncludingTags(Entry paramEntry)
  {
    return getTotal(paramEntry, null, -1, true);
  }
  
  public long getTotalPackets()
  {
    long l = 0L;
    int i = this.size - 1;
    while (i >= 0)
    {
      l += this.rxPackets[i] + this.txPackets[i];
      i -= 1;
    }
    return l;
  }
  
  public String[] getUniqueIfaces()
  {
    HashSet localHashSet = new HashSet();
    String[] arrayOfString = this.iface;
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      String str = arrayOfString[i];
      if (str != IFACE_ALL) {
        localHashSet.add(str);
      }
      i += 1;
    }
    return (String[])localHashSet.toArray(new String[localHashSet.size()]);
  }
  
  public int[] getUniqueUids()
  {
    SparseBooleanArray localSparseBooleanArray = new SparseBooleanArray();
    int[] arrayOfInt = this.uid;
    int i = 0;
    int j = arrayOfInt.length;
    while (i < j)
    {
      localSparseBooleanArray.put(arrayOfInt[i], true);
      i += 1;
    }
    j = localSparseBooleanArray.size();
    arrayOfInt = new int[j];
    i = 0;
    while (i < j)
    {
      arrayOfInt[i] = localSparseBooleanArray.keyAt(i);
      i += 1;
    }
    return arrayOfInt;
  }
  
  public Entry getValues(int paramInt, Entry paramEntry)
  {
    if (paramEntry != null) {}
    for (;;)
    {
      paramEntry.iface = this.iface[paramInt];
      paramEntry.uid = this.uid[paramInt];
      paramEntry.set = this.set[paramInt];
      paramEntry.tag = this.tag[paramInt];
      paramEntry.roaming = this.roaming[paramInt];
      paramEntry.rxBytes = this.rxBytes[paramInt];
      paramEntry.rxPackets = this.rxPackets[paramInt];
      paramEntry.txBytes = this.txBytes[paramInt];
      paramEntry.txPackets = this.txPackets[paramInt];
      paramEntry.operations = this.operations[paramInt];
      return paramEntry;
      paramEntry = new Entry();
    }
  }
  
  public NetworkStats groupedByIface()
  {
    NetworkStats localNetworkStats = new NetworkStats(this.elapsedRealtime, 10);
    Entry localEntry = new Entry();
    localEntry.uid = -1;
    localEntry.set = -1;
    localEntry.tag = 0;
    localEntry.roaming = -1;
    localEntry.operations = 0L;
    int i = 0;
    if (i < this.size)
    {
      if (this.tag[i] != 0) {}
      for (;;)
      {
        i += 1;
        break;
        localEntry.iface = this.iface[i];
        localEntry.rxBytes = this.rxBytes[i];
        localEntry.rxPackets = this.rxPackets[i];
        localEntry.txBytes = this.txBytes[i];
        localEntry.txPackets = this.txPackets[i];
        localNetworkStats.combineValues(localEntry);
      }
    }
    return localNetworkStats;
  }
  
  public NetworkStats groupedByUid()
  {
    NetworkStats localNetworkStats = new NetworkStats(this.elapsedRealtime, 10);
    Entry localEntry = new Entry();
    localEntry.iface = IFACE_ALL;
    localEntry.set = -1;
    localEntry.tag = 0;
    localEntry.roaming = -1;
    int i = 0;
    if (i < this.size)
    {
      if (this.tag[i] != 0) {}
      for (;;)
      {
        i += 1;
        break;
        localEntry.uid = this.uid[i];
        localEntry.rxBytes = this.rxBytes[i];
        localEntry.rxPackets = this.rxPackets[i];
        localEntry.txBytes = this.txBytes[i];
        localEntry.txPackets = this.txPackets[i];
        localEntry.operations = this.operations[i];
        localNetworkStats.combineValues(localEntry);
      }
    }
    return localNetworkStats;
  }
  
  public int internalSize()
  {
    return this.capacity;
  }
  
  public boolean migrateTun(int paramInt, String paramString1, String paramString2)
  {
    Entry localEntry1 = new Entry();
    Entry localEntry2 = new Entry();
    tunAdjustmentInit(paramInt, paramString1, paramString2, localEntry1, localEntry2);
    localEntry2 = tunGetPool(localEntry1, localEntry2);
    if (localEntry2.isEmpty()) {
      return true;
    }
    paramString1 = addTrafficToApplications(paramInt, paramString1, paramString2, localEntry1, localEntry2);
    deductTrafficFromVpnApp(paramInt, paramString2, paramString1);
    if (!paramString1.isEmpty())
    {
      Slog.wtf("NetworkStats", "Failed to deduct underlying network traffic from VPN package. Moved=" + paramString1);
      return false;
    }
    return true;
  }
  
  public void setElapsedRealtime(long paramLong)
  {
    this.elapsedRealtime = paramLong;
  }
  
  public int size()
  {
    return this.size;
  }
  
  public void spliceOperationsFrom(NetworkStats paramNetworkStats)
  {
    int i = 0;
    if (i < this.size)
    {
      int j = paramNetworkStats.findIndex(this.iface[i], this.uid[i], this.set[i], this.tag[i], this.roaming[i]);
      if (j == -1) {
        this.operations[i] = 0L;
      }
      for (;;)
      {
        i += 1;
        break;
        this.operations[i] = paramNetworkStats.operations[j];
      }
    }
  }
  
  public NetworkStats subtract(NetworkStats paramNetworkStats)
  {
    return subtract(this, paramNetworkStats, null, null);
  }
  
  public String toString()
  {
    CharArrayWriter localCharArrayWriter = new CharArrayWriter();
    dump("", new PrintWriter(localCharArrayWriter));
    return localCharArrayWriter.toString();
  }
  
  public NetworkStats withoutUids(int[] paramArrayOfInt)
  {
    NetworkStats localNetworkStats = new NetworkStats(this.elapsedRealtime, 10);
    Entry localEntry = new Entry();
    int i = 0;
    while (i < this.size)
    {
      localEntry = getValues(i, localEntry);
      if (!ArrayUtils.contains(paramArrayOfInt, localEntry.uid)) {
        localNetworkStats.addValues(localEntry);
      }
      i += 1;
    }
    return localNetworkStats;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.elapsedRealtime);
    paramParcel.writeInt(this.size);
    paramParcel.writeInt(this.capacity);
    paramParcel.writeStringArray(this.iface);
    paramParcel.writeIntArray(this.uid);
    paramParcel.writeIntArray(this.set);
    paramParcel.writeIntArray(this.tag);
    paramParcel.writeIntArray(this.roaming);
    paramParcel.writeLongArray(this.rxBytes);
    paramParcel.writeLongArray(this.rxPackets);
    paramParcel.writeLongArray(this.txBytes);
    paramParcel.writeLongArray(this.txPackets);
    paramParcel.writeLongArray(this.operations);
  }
  
  public static class Entry
  {
    public String iface;
    public long operations;
    public int roaming;
    public long rxBytes;
    public long rxPackets;
    public int set;
    public int tag;
    public long txBytes;
    public long txPackets;
    public int uid;
    
    public Entry()
    {
      this(NetworkStats.IFACE_ALL, -1, 0, 0, 0L, 0L, 0L, 0L, 0L);
    }
    
    public Entry(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
    {
      this(NetworkStats.IFACE_ALL, -1, 0, 0, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5);
    }
    
    public Entry(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
    {
      this.iface = paramString;
      this.uid = paramInt1;
      this.set = paramInt2;
      this.tag = paramInt3;
      this.roaming = paramInt4;
      this.rxBytes = paramLong1;
      this.rxPackets = paramLong2;
      this.txBytes = paramLong3;
      this.txPackets = paramLong4;
      this.operations = paramLong5;
    }
    
    public Entry(String paramString, int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
    {
      this(paramString, paramInt1, paramInt2, paramInt3, 0, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5);
    }
    
    public void add(Entry paramEntry)
    {
      this.rxBytes += paramEntry.rxBytes;
      this.rxPackets += paramEntry.rxPackets;
      this.txBytes += paramEntry.txBytes;
      this.txPackets += paramEntry.txPackets;
      this.operations += paramEntry.operations;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if ((paramObject instanceof Entry))
      {
        paramObject = (Entry)paramObject;
        boolean bool1 = bool2;
        if (this.uid == ((Entry)paramObject).uid)
        {
          bool1 = bool2;
          if (this.set == ((Entry)paramObject).set)
          {
            bool1 = bool2;
            if (this.tag == ((Entry)paramObject).tag)
            {
              bool1 = bool2;
              if (this.roaming == ((Entry)paramObject).roaming)
              {
                bool1 = bool2;
                if (this.rxBytes == ((Entry)paramObject).rxBytes)
                {
                  bool1 = bool2;
                  if (this.rxPackets == ((Entry)paramObject).rxPackets)
                  {
                    bool1 = bool2;
                    if (this.txBytes == ((Entry)paramObject).txBytes)
                    {
                      bool1 = bool2;
                      if (this.txPackets == ((Entry)paramObject).txPackets)
                      {
                        bool1 = bool2;
                        if (this.operations == ((Entry)paramObject).operations) {
                          bool1 = this.iface.equals(((Entry)paramObject).iface);
                        }
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
    
    public boolean isEmpty()
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.rxBytes == 0L)
      {
        bool1 = bool2;
        if (this.rxPackets == 0L)
        {
          bool1 = bool2;
          if (this.txBytes == 0L)
          {
            bool1 = bool2;
            if (this.txPackets == 0L)
            {
              bool1 = bool2;
              if (this.operations == 0L) {
                bool1 = true;
              }
            }
          }
        }
      }
      return bool1;
    }
    
    public boolean isNegative()
    {
      if ((this.rxBytes < 0L) || (this.rxPackets < 0L)) {}
      while ((this.txBytes < 0L) || (this.txPackets < 0L) || (this.operations < 0L)) {
        return true;
      }
      return false;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("iface=").append(this.iface);
      localStringBuilder.append(" uid=").append(this.uid);
      localStringBuilder.append(" set=").append(NetworkStats.setToString(this.set));
      localStringBuilder.append(" tag=").append(NetworkStats.tagToString(this.tag));
      localStringBuilder.append(" roaming=").append(NetworkStats.roamingToString(this.roaming));
      localStringBuilder.append(" rxBytes=").append(this.rxBytes);
      localStringBuilder.append(" rxPackets=").append(this.rxPackets);
      localStringBuilder.append(" txBytes=").append(this.txBytes);
      localStringBuilder.append(" txPackets=").append(this.txPackets);
      localStringBuilder.append(" operations=").append(this.operations);
      return localStringBuilder.toString();
    }
  }
  
  public static abstract interface NonMonotonicObserver<C>
  {
    public abstract void foundNonMonotonic(NetworkStats paramNetworkStats1, int paramInt1, NetworkStats paramNetworkStats2, int paramInt2, C paramC);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */