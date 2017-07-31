package com.android.server.net;

import android.net.NetworkIdentity;
import android.net.NetworkStats;
import android.net.NetworkStats.Entry;
import android.net.NetworkStatsHistory;
import android.net.NetworkStatsHistory.Entry;
import android.net.NetworkTemplate;
import android.os.Binder;
import android.util.ArrayMap;
import android.util.IntArray;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FileRotator.Reader;
import com.android.internal.util.IndentingPrintWriter;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class NetworkStatsCollection
  implements FileRotator.Reader
{
  private static final int FILE_MAGIC = 1095648596;
  private static final int VERSION_NETWORK_INIT = 1;
  private static final int VERSION_UID_INIT = 1;
  private static final int VERSION_UID_WITH_IDENT = 2;
  private static final int VERSION_UID_WITH_SET = 4;
  private static final int VERSION_UID_WITH_TAG = 3;
  private static final int VERSION_UNIFIED_INIT = 16;
  private final long mBucketDuration;
  private boolean mDirty;
  private long mEndMillis;
  private long mStartMillis;
  private ArrayMap<Key, NetworkStatsHistory> mStats = new ArrayMap();
  private long mTotalBytes;
  
  public NetworkStatsCollection(long paramLong)
  {
    this.mBucketDuration = paramLong;
    reset();
  }
  
  private void dumpCheckin(PrintWriter paramPrintWriter, long paramLong1, long paramLong2, NetworkTemplate paramNetworkTemplate, String paramString)
  {
    ArrayMap localArrayMap = new ArrayMap();
    int i = 0;
    Object localObject;
    if (i < this.mStats.size())
    {
      localObject = (Key)this.mStats.keyAt(i);
      NetworkStatsHistory localNetworkStatsHistory2 = (NetworkStatsHistory)this.mStats.valueAt(i);
      if (!templateMatches(paramNetworkTemplate, ((Key)localObject).ident)) {}
      for (;;)
      {
        i += 1;
        break;
        if (((Key)localObject).set < 1000)
        {
          Key localKey = new Key(null, ((Key)localObject).uid, ((Key)localObject).set, ((Key)localObject).tag);
          NetworkStatsHistory localNetworkStatsHistory1 = (NetworkStatsHistory)localArrayMap.get(localKey);
          localObject = localNetworkStatsHistory1;
          if (localNetworkStatsHistory1 == null)
          {
            localObject = new NetworkStatsHistory(localNetworkStatsHistory2.getBucketDuration());
            localArrayMap.put(localKey, localObject);
          }
          ((NetworkStatsHistory)localObject).recordHistory(localNetworkStatsHistory2, paramLong1, paramLong2);
        }
      }
    }
    i = 0;
    if (i < localArrayMap.size())
    {
      paramNetworkTemplate = (Key)localArrayMap.keyAt(i);
      localObject = (NetworkStatsHistory)localArrayMap.valueAt(i);
      if (((NetworkStatsHistory)localObject).size() == 0) {}
      for (;;)
      {
        i += 1;
        break;
        paramPrintWriter.print("c,");
        paramPrintWriter.print(paramString);
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramNetworkTemplate.uid);
        paramPrintWriter.print(',');
        paramPrintWriter.print(NetworkStats.setToCheckinString(paramNetworkTemplate.set));
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramNetworkTemplate.tag);
        paramPrintWriter.println();
        ((NetworkStatsHistory)localObject).dumpCheckin(paramPrintWriter);
      }
    }
  }
  
  private int estimateBuckets()
  {
    return (int)(Math.min(this.mEndMillis - this.mStartMillis, 3024000000L) / this.mBucketDuration);
  }
  
  private NetworkStatsHistory findOrCreateHistory(NetworkIdentitySet paramNetworkIdentitySet, int paramInt1, int paramInt2, int paramInt3)
  {
    Key localKey = new Key(paramNetworkIdentitySet, paramInt1, paramInt2, paramInt3);
    NetworkStatsHistory localNetworkStatsHistory = (NetworkStatsHistory)this.mStats.get(localKey);
    paramNetworkIdentitySet = null;
    if (localNetworkStatsHistory == null) {
      paramNetworkIdentitySet = new NetworkStatsHistory(this.mBucketDuration, 10);
    }
    while (paramNetworkIdentitySet != null)
    {
      this.mStats.put(localKey, paramNetworkIdentitySet);
      return paramNetworkIdentitySet;
      if (localNetworkStatsHistory.getBucketDuration() != this.mBucketDuration) {
        paramNetworkIdentitySet = new NetworkStatsHistory(localNetworkStatsHistory, this.mBucketDuration);
      }
    }
    return localNetworkStatsHistory;
  }
  
  private void noteRecordedHistory(long paramLong1, long paramLong2, long paramLong3)
  {
    if (paramLong1 < this.mStartMillis) {
      this.mStartMillis = paramLong1;
    }
    if (paramLong2 > this.mEndMillis) {
      this.mEndMillis = paramLong2;
    }
    this.mTotalBytes += paramLong3;
    this.mDirty = true;
  }
  
  private void recordHistory(Key paramKey, NetworkStatsHistory paramNetworkStatsHistory)
  {
    if (paramNetworkStatsHistory.size() == 0) {
      return;
    }
    noteRecordedHistory(paramNetworkStatsHistory.getStart(), paramNetworkStatsHistory.getEnd(), paramNetworkStatsHistory.getTotalBytes());
    NetworkStatsHistory localNetworkStatsHistory2 = (NetworkStatsHistory)this.mStats.get(paramKey);
    NetworkStatsHistory localNetworkStatsHistory1 = localNetworkStatsHistory2;
    if (localNetworkStatsHistory2 == null)
    {
      localNetworkStatsHistory1 = new NetworkStatsHistory(paramNetworkStatsHistory.getBucketDuration());
      this.mStats.put(paramKey, localNetworkStatsHistory1);
    }
    localNetworkStatsHistory1.recordEntireHistory(paramNetworkStatsHistory);
  }
  
  private static boolean templateMatches(NetworkTemplate paramNetworkTemplate, NetworkIdentitySet paramNetworkIdentitySet)
  {
    paramNetworkIdentitySet = paramNetworkIdentitySet.iterator();
    while (paramNetworkIdentitySet.hasNext()) {
      if (paramNetworkTemplate.matches((NetworkIdentity)paramNetworkIdentitySet.next())) {
        return true;
      }
    }
    return false;
  }
  
  public void clearDirty()
  {
    this.mDirty = false;
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    Object localObject1 = Lists.newArrayList();
    ((ArrayList)localObject1).addAll(this.mStats.keySet());
    Collections.sort((List)localObject1);
    localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (Key)((Iterator)localObject1).next();
      paramIndentingPrintWriter.print("ident=");
      paramIndentingPrintWriter.print(((Key)localObject2).ident.toString());
      paramIndentingPrintWriter.print(" uid=");
      paramIndentingPrintWriter.print(((Key)localObject2).uid);
      paramIndentingPrintWriter.print(" set=");
      paramIndentingPrintWriter.print(NetworkStats.setToString(((Key)localObject2).set));
      paramIndentingPrintWriter.print(" tag=");
      paramIndentingPrintWriter.println(NetworkStats.tagToString(((Key)localObject2).tag));
      localObject2 = (NetworkStatsHistory)this.mStats.get(localObject2);
      paramIndentingPrintWriter.increaseIndent();
      ((NetworkStatsHistory)localObject2).dump(paramIndentingPrintWriter, true);
      paramIndentingPrintWriter.decreaseIndent();
    }
  }
  
  public void dumpCheckin(PrintWriter paramPrintWriter, long paramLong1, long paramLong2)
  {
    dumpCheckin(paramPrintWriter, paramLong1, paramLong2, NetworkTemplate.buildTemplateMobileWildcard(), "cell");
    dumpCheckin(paramPrintWriter, paramLong1, paramLong2, NetworkTemplate.buildTemplateWifiWildcard(), "wifi");
    dumpCheckin(paramPrintWriter, paramLong1, paramLong2, NetworkTemplate.buildTemplateEthernet(), "eth");
    dumpCheckin(paramPrintWriter, paramLong1, paramLong2, NetworkTemplate.buildTemplateBluetooth(), "bt");
  }
  
  public long getEndMillis()
  {
    return this.mEndMillis;
  }
  
  public long getFirstAtomicBucketMillis()
  {
    if (this.mStartMillis == Long.MAX_VALUE) {
      return Long.MAX_VALUE;
    }
    return this.mStartMillis + this.mBucketDuration;
  }
  
  public NetworkStatsHistory getHistory(NetworkTemplate paramNetworkTemplate, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    return getHistory(paramNetworkTemplate, paramInt1, paramInt2, paramInt3, paramInt4, Long.MIN_VALUE, Long.MAX_VALUE, paramInt5);
  }
  
  public NetworkStatsHistory getHistory(NetworkTemplate paramNetworkTemplate, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong1, long paramLong2, int paramInt5)
  {
    return getHistory(paramNetworkTemplate, paramInt1, paramInt2, paramInt3, paramInt4, paramLong1, paramLong2, paramInt5, Binder.getCallingUid());
  }
  
  public NetworkStatsHistory getHistory(NetworkTemplate paramNetworkTemplate, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong1, long paramLong2, int paramInt5, int paramInt6)
  {
    if (!NetworkStatsAccess.isAccessibleToUser(paramInt1, paramInt6, paramInt5)) {
      throw new SecurityException("Network stats history of uid " + paramInt1 + " is forbidden for caller " + paramInt6);
    }
    long l = this.mBucketDuration;
    if (paramLong1 == paramLong2) {}
    NetworkStatsHistory localNetworkStatsHistory;
    for (paramInt5 = 1;; paramInt5 = estimateBuckets())
    {
      localNetworkStatsHistory = new NetworkStatsHistory(l, paramInt5, paramInt4);
      if (paramLong1 != paramLong2) {
        break;
      }
      return localNetworkStatsHistory;
    }
    paramInt4 = 0;
    while (paramInt4 < this.mStats.size())
    {
      Key localKey = (Key)this.mStats.keyAt(paramInt4);
      if ((localKey.uid == paramInt1) && (NetworkStats.setMatches(paramInt2, localKey.set)) && (localKey.tag == paramInt3) && (templateMatches(paramNetworkTemplate, localKey.ident))) {
        localNetworkStatsHistory.recordHistory((NetworkStatsHistory)this.mStats.valueAt(paramInt4), paramLong1, paramLong2);
      }
      paramInt4 += 1;
    }
    return localNetworkStatsHistory;
  }
  
  public int[] getRelevantUids(int paramInt)
  {
    return getRelevantUids(paramInt, Binder.getCallingUid());
  }
  
  public int[] getRelevantUids(int paramInt1, int paramInt2)
  {
    IntArray localIntArray = new IntArray();
    int i = 0;
    while (i < this.mStats.size())
    {
      Key localKey = (Key)this.mStats.keyAt(i);
      if (NetworkStatsAccess.isAccessibleToUser(localKey.uid, paramInt2, paramInt1))
      {
        int j = localIntArray.binarySearch(localKey.uid);
        if (j < 0) {
          localIntArray.add(j, localKey.uid);
        }
      }
      i += 1;
    }
    return localIntArray.toArray();
  }
  
  public long getStartMillis()
  {
    return this.mStartMillis;
  }
  
  public NetworkStats getSummary(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2, int paramInt)
  {
    return getSummary(paramNetworkTemplate, paramLong1, paramLong2, paramInt, Binder.getCallingUid());
  }
  
  public NetworkStats getSummary(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2, int paramInt1, int paramInt2)
  {
    long l = System.currentTimeMillis();
    NetworkStats localNetworkStats = new NetworkStats(paramLong2 - paramLong1, 24);
    if (paramLong1 == paramLong2) {
      return localNetworkStats;
    }
    NetworkStats.Entry localEntry = new NetworkStats.Entry();
    Object localObject2 = null;
    int i = 0;
    if (i < this.mStats.size())
    {
      Key localKey = (Key)this.mStats.keyAt(i);
      Object localObject1 = localObject2;
      if (templateMatches(paramNetworkTemplate, localKey.ident))
      {
        localObject1 = localObject2;
        if (NetworkStatsAccess.isAccessibleToUser(localKey.uid, paramInt2, paramInt1))
        {
          localObject1 = localObject2;
          if (localKey.set < 1000)
          {
            localObject2 = ((NetworkStatsHistory)this.mStats.valueAt(i)).getValues(paramLong1, paramLong2, l, (NetworkStatsHistory.Entry)localObject2);
            localEntry.iface = NetworkStats.IFACE_ALL;
            localEntry.uid = localKey.uid;
            localEntry.set = localKey.set;
            localEntry.tag = localKey.tag;
            if (!localKey.ident.isAnyMemberRoaming()) {
              break label291;
            }
          }
        }
      }
      label291:
      for (int j = 1;; j = 0)
      {
        localEntry.roaming = j;
        localEntry.rxBytes = ((NetworkStatsHistory.Entry)localObject2).rxBytes;
        localEntry.rxPackets = ((NetworkStatsHistory.Entry)localObject2).rxPackets;
        localEntry.txBytes = ((NetworkStatsHistory.Entry)localObject2).txBytes;
        localEntry.txPackets = ((NetworkStatsHistory.Entry)localObject2).txPackets;
        localEntry.operations = ((NetworkStatsHistory.Entry)localObject2).operations;
        localObject1 = localObject2;
        if (!localEntry.isEmpty())
        {
          localNetworkStats.combineValues(localEntry);
          localObject1 = localObject2;
        }
        i += 1;
        localObject2 = localObject1;
        break;
      }
    }
    return localNetworkStats;
  }
  
  public long getTotalBytes()
  {
    return this.mTotalBytes;
  }
  
  public boolean isDirty()
  {
    return this.mDirty;
  }
  
  public boolean isEmpty()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mStartMillis == Long.MAX_VALUE)
    {
      bool1 = bool2;
      if (this.mEndMillis == Long.MIN_VALUE) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void read(DataInputStream paramDataInputStream)
    throws IOException
  {
    int i = paramDataInputStream.readInt();
    if (i != 1095648596) {
      throw new ProtocolException("unexpected magic: " + i);
    }
    i = paramDataInputStream.readInt();
    switch (i)
    {
    default: 
      throw new ProtocolException("unexpected version: " + i);
    }
    int k = paramDataInputStream.readInt();
    i = 0;
    while (i < k)
    {
      NetworkIdentitySet localNetworkIdentitySet = new NetworkIdentitySet(paramDataInputStream);
      int m = paramDataInputStream.readInt();
      int j = 0;
      while (j < m)
      {
        recordHistory(new Key(localNetworkIdentitySet, paramDataInputStream.readInt(), paramDataInputStream.readInt(), paramDataInputStream.readInt()), new NetworkStatsHistory(paramDataInputStream));
        j += 1;
      }
      i += 1;
    }
  }
  
  public void read(InputStream paramInputStream)
    throws IOException
  {
    read(new DataInputStream(paramInputStream));
  }
  
  /* Error */
  @Deprecated
  public void readLegacyNetwork(java.io.File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: new 452	android/util/AtomicFile
    //   3: dup
    //   4: aload_1
    //   5: invokespecial 454	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   8: astore_1
    //   9: aconst_null
    //   10: astore 5
    //   12: aconst_null
    //   13: astore 4
    //   15: new 424	java/io/DataInputStream
    //   18: dup
    //   19: new 456	java/io/BufferedInputStream
    //   22: dup
    //   23: aload_1
    //   24: invokevirtual 460	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   27: invokespecial 461	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   30: invokespecial 443	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   33: astore_1
    //   34: aload_1
    //   35: invokevirtual 427	java/io/DataInputStream:readInt	()I
    //   38: istore_2
    //   39: iload_2
    //   40: ldc 12
    //   42: if_icmpeq +38 -> 80
    //   45: new 429	java/net/ProtocolException
    //   48: dup
    //   49: new 309	java/lang/StringBuilder
    //   52: dup
    //   53: invokespecial 310	java/lang/StringBuilder:<init>	()V
    //   56: ldc_w 431
    //   59: invokevirtual 316	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: iload_2
    //   63: invokevirtual 319	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   66: invokevirtual 322	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   69: invokespecial 432	java/net/ProtocolException:<init>	(Ljava/lang/String;)V
    //   72: athrow
    //   73: astore 4
    //   75: aload_1
    //   76: invokestatic 467	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   79: return
    //   80: aload_1
    //   81: invokevirtual 427	java/io/DataInputStream:readInt	()I
    //   84: istore_2
    //   85: iload_2
    //   86: tableswitch	default:+132->218, 1:+55->141
    //   104: new 429	java/net/ProtocolException
    //   107: dup
    //   108: new 309	java/lang/StringBuilder
    //   111: dup
    //   112: invokespecial 310	java/lang/StringBuilder:<init>	()V
    //   115: ldc_w 434
    //   118: invokevirtual 316	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   121: iload_2
    //   122: invokevirtual 319	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   125: invokevirtual 322	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   128: invokespecial 432	java/net/ProtocolException:<init>	(Ljava/lang/String;)V
    //   131: athrow
    //   132: astore 4
    //   134: aload_1
    //   135: invokestatic 467	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   138: aload 4
    //   140: athrow
    //   141: aload_1
    //   142: invokevirtual 427	java/io/DataInputStream:readInt	()I
    //   145: istore_3
    //   146: iconst_0
    //   147: istore_2
    //   148: iload_2
    //   149: iload_3
    //   150: if_icmpge +48 -> 198
    //   153: new 228	com/android/server/net/NetworkIdentitySet
    //   156: dup
    //   157: aload_1
    //   158: invokespecial 436	com/android/server/net/NetworkIdentitySet:<init>	(Ljava/io/DataInputStream;)V
    //   161: astore 4
    //   163: new 64	android/net/NetworkStatsHistory
    //   166: dup
    //   167: aload_1
    //   168: invokespecial 437	android/net/NetworkStatsHistory:<init>	(Ljava/io/DataInputStream;)V
    //   171: astore 5
    //   173: aload_0
    //   174: new 8	com/android/server/net/NetworkStatsCollection$Key
    //   177: dup
    //   178: aload 4
    //   180: iconst_m1
    //   181: iconst_m1
    //   182: iconst_0
    //   183: invokespecial 84	com/android/server/net/NetworkStatsCollection$Key:<init>	(Lcom/android/server/net/NetworkIdentitySet;III)V
    //   186: aload 5
    //   188: invokespecial 439	com/android/server/net/NetworkStatsCollection:recordHistory	(Lcom/android/server/net/NetworkStatsCollection$Key;Landroid/net/NetworkStatsHistory;)V
    //   191: iload_2
    //   192: iconst_1
    //   193: iadd
    //   194: istore_2
    //   195: goto -47 -> 148
    //   198: aload_1
    //   199: invokestatic 467	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   202: return
    //   203: astore 4
    //   205: aload 5
    //   207: astore_1
    //   208: goto -74 -> 134
    //   211: astore_1
    //   212: aload 4
    //   214: astore_1
    //   215: goto -140 -> 75
    //   218: goto -114 -> 104
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	221	0	this	NetworkStatsCollection
    //   0	221	1	paramFile	java.io.File
    //   38	157	2	i	int
    //   145	6	3	j	int
    //   13	1	4	localObject1	Object
    //   73	1	4	localFileNotFoundException	java.io.FileNotFoundException
    //   132	7	4	localObject2	Object
    //   161	18	4	localNetworkIdentitySet	NetworkIdentitySet
    //   203	10	4	localObject3	Object
    //   10	196	5	localNetworkStatsHistory	NetworkStatsHistory
    // Exception table:
    //   from	to	target	type
    //   34	39	73	java/io/FileNotFoundException
    //   45	73	73	java/io/FileNotFoundException
    //   80	85	73	java/io/FileNotFoundException
    //   104	132	73	java/io/FileNotFoundException
    //   141	146	73	java/io/FileNotFoundException
    //   153	191	73	java/io/FileNotFoundException
    //   34	39	132	finally
    //   45	73	132	finally
    //   80	85	132	finally
    //   104	132	132	finally
    //   141	146	132	finally
    //   153	191	132	finally
    //   15	34	203	finally
    //   15	34	211	java/io/FileNotFoundException
  }
  
  /* Error */
  @Deprecated
  public void readLegacyUid(java.io.File paramFile, boolean paramBoolean)
    throws IOException
  {
    // Byte code:
    //   0: new 452	android/util/AtomicFile
    //   3: dup
    //   4: aload_1
    //   5: invokespecial 454	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   8: astore_1
    //   9: aconst_null
    //   10: astore 13
    //   12: aconst_null
    //   13: astore 12
    //   15: new 424	java/io/DataInputStream
    //   18: dup
    //   19: new 456	java/io/BufferedInputStream
    //   22: dup
    //   23: aload_1
    //   24: invokevirtual 460	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   27: invokespecial 461	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   30: invokespecial 443	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   33: astore_1
    //   34: aload_1
    //   35: invokevirtual 427	java/io/DataInputStream:readInt	()I
    //   38: istore_3
    //   39: iload_3
    //   40: ldc 12
    //   42: if_icmpeq +38 -> 80
    //   45: new 429	java/net/ProtocolException
    //   48: dup
    //   49: new 309	java/lang/StringBuilder
    //   52: dup
    //   53: invokespecial 310	java/lang/StringBuilder:<init>	()V
    //   56: ldc_w 431
    //   59: invokevirtual 316	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: iload_3
    //   63: invokevirtual 319	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   66: invokevirtual 322	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   69: invokespecial 432	java/net/ProtocolException:<init>	(Ljava/lang/String;)V
    //   72: athrow
    //   73: astore 12
    //   75: aload_1
    //   76: invokestatic 467	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   79: return
    //   80: aload_1
    //   81: invokevirtual 427	java/io/DataInputStream:readInt	()I
    //   84: istore 6
    //   86: iload 6
    //   88: tableswitch	default:+231->319, 1:+211->299, 2:+211->299, 3:+70->158, 4:+70->158
    //   120: new 429	java/net/ProtocolException
    //   123: dup
    //   124: new 309	java/lang/StringBuilder
    //   127: dup
    //   128: invokespecial 310	java/lang/StringBuilder:<init>	()V
    //   131: ldc_w 434
    //   134: invokevirtual 316	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   137: iload 6
    //   139: invokevirtual 319	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   142: invokevirtual 322	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   145: invokespecial 432	java/net/ProtocolException:<init>	(Ljava/lang/String;)V
    //   148: athrow
    //   149: astore 12
    //   151: aload_1
    //   152: invokestatic 467	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   155: aload 12
    //   157: athrow
    //   158: aload_1
    //   159: invokevirtual 427	java/io/DataInputStream:readInt	()I
    //   162: istore 7
    //   164: iconst_0
    //   165: istore_3
    //   166: iload_3
    //   167: iload 7
    //   169: if_icmpge +130 -> 299
    //   172: new 228	com/android/server/net/NetworkIdentitySet
    //   175: dup
    //   176: aload_1
    //   177: invokespecial 436	com/android/server/net/NetworkIdentitySet:<init>	(Ljava/io/DataInputStream;)V
    //   180: astore 12
    //   182: aload_1
    //   183: invokevirtual 427	java/io/DataInputStream:readInt	()I
    //   186: istore 8
    //   188: iconst_0
    //   189: istore 4
    //   191: iload 4
    //   193: iload 8
    //   195: if_icmpge +97 -> 292
    //   198: aload_1
    //   199: invokevirtual 427	java/io/DataInputStream:readInt	()I
    //   202: istore 9
    //   204: iload 6
    //   206: iconst_4
    //   207: if_icmplt +73 -> 280
    //   210: aload_1
    //   211: invokevirtual 427	java/io/DataInputStream:readInt	()I
    //   214: istore 5
    //   216: aload_1
    //   217: invokevirtual 427	java/io/DataInputStream:readInt	()I
    //   220: istore 10
    //   222: new 8	com/android/server/net/NetworkStatsCollection$Key
    //   225: dup
    //   226: aload 12
    //   228: iload 9
    //   230: iload 5
    //   232: iload 10
    //   234: invokespecial 84	com/android/server/net/NetworkStatsCollection$Key:<init>	(Lcom/android/server/net/NetworkIdentitySet;III)V
    //   237: astore 13
    //   239: new 64	android/net/NetworkStatsHistory
    //   242: dup
    //   243: aload_1
    //   244: invokespecial 437	android/net/NetworkStatsHistory:<init>	(Ljava/io/DataInputStream;)V
    //   247: astore 14
    //   249: iload 10
    //   251: ifne +35 -> 286
    //   254: iconst_1
    //   255: istore 11
    //   257: iload 11
    //   259: iload_2
    //   260: if_icmpeq +11 -> 271
    //   263: aload_0
    //   264: aload 13
    //   266: aload 14
    //   268: invokespecial 439	com/android/server/net/NetworkStatsCollection:recordHistory	(Lcom/android/server/net/NetworkStatsCollection$Key;Landroid/net/NetworkStatsHistory;)V
    //   271: iload 4
    //   273: iconst_1
    //   274: iadd
    //   275: istore 4
    //   277: goto -86 -> 191
    //   280: iconst_0
    //   281: istore 5
    //   283: goto -67 -> 216
    //   286: iconst_0
    //   287: istore 11
    //   289: goto -32 -> 257
    //   292: iload_3
    //   293: iconst_1
    //   294: iadd
    //   295: istore_3
    //   296: goto -130 -> 166
    //   299: aload_1
    //   300: invokestatic 467	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   303: return
    //   304: astore 12
    //   306: aload 13
    //   308: astore_1
    //   309: goto -158 -> 151
    //   312: astore_1
    //   313: aload 12
    //   315: astore_1
    //   316: goto -241 -> 75
    //   319: goto -199 -> 120
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	322	0	this	NetworkStatsCollection
    //   0	322	1	paramFile	java.io.File
    //   0	322	2	paramBoolean	boolean
    //   38	258	3	i	int
    //   189	87	4	j	int
    //   214	68	5	k	int
    //   84	124	6	m	int
    //   162	8	7	n	int
    //   186	10	8	i1	int
    //   202	27	9	i2	int
    //   220	30	10	i3	int
    //   255	33	11	bool	boolean
    //   13	1	12	localObject1	Object
    //   73	1	12	localFileNotFoundException	java.io.FileNotFoundException
    //   149	7	12	localObject2	Object
    //   180	47	12	localNetworkIdentitySet	NetworkIdentitySet
    //   304	10	12	localObject3	Object
    //   10	297	13	localKey	Key
    //   247	20	14	localNetworkStatsHistory	NetworkStatsHistory
    // Exception table:
    //   from	to	target	type
    //   34	39	73	java/io/FileNotFoundException
    //   45	73	73	java/io/FileNotFoundException
    //   80	86	73	java/io/FileNotFoundException
    //   120	149	73	java/io/FileNotFoundException
    //   158	164	73	java/io/FileNotFoundException
    //   172	188	73	java/io/FileNotFoundException
    //   198	204	73	java/io/FileNotFoundException
    //   210	216	73	java/io/FileNotFoundException
    //   216	249	73	java/io/FileNotFoundException
    //   263	271	73	java/io/FileNotFoundException
    //   34	39	149	finally
    //   45	73	149	finally
    //   80	86	149	finally
    //   120	149	149	finally
    //   158	164	149	finally
    //   172	188	149	finally
    //   198	204	149	finally
    //   210	216	149	finally
    //   216	249	149	finally
    //   263	271	149	finally
    //   15	34	304	finally
    //   15	34	312	java/io/FileNotFoundException
  }
  
  public void recordCollection(NetworkStatsCollection paramNetworkStatsCollection)
  {
    int i = 0;
    while (i < paramNetworkStatsCollection.mStats.size())
    {
      recordHistory((Key)paramNetworkStatsCollection.mStats.keyAt(i), (NetworkStatsHistory)paramNetworkStatsCollection.mStats.valueAt(i));
      i += 1;
    }
  }
  
  public void recordData(NetworkIdentitySet paramNetworkIdentitySet, int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2, NetworkStats.Entry paramEntry)
  {
    paramNetworkIdentitySet = findOrCreateHistory(paramNetworkIdentitySet, paramInt1, paramInt2, paramInt3);
    paramNetworkIdentitySet.recordData(paramLong1, paramLong2, paramEntry);
    paramLong1 = paramNetworkIdentitySet.getStart();
    paramLong2 = paramNetworkIdentitySet.getEnd();
    long l = paramEntry.rxBytes;
    noteRecordedHistory(paramLong1, paramLong2, paramEntry.txBytes + l);
  }
  
  public void removeUids(int[] paramArrayOfInt)
  {
    Object localObject = Lists.newArrayList();
    ((ArrayList)localObject).addAll(this.mStats.keySet());
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      Key localKey = (Key)((Iterator)localObject).next();
      if (ArrayUtils.contains(paramArrayOfInt, localKey.uid))
      {
        if (localKey.tag == 0)
        {
          NetworkStatsHistory localNetworkStatsHistory = (NetworkStatsHistory)this.mStats.get(localKey);
          findOrCreateHistory(localKey.ident, -4, 0, 0).recordEntireHistory(localNetworkStatsHistory);
        }
        this.mStats.remove(localKey);
        this.mDirty = true;
      }
    }
  }
  
  public void reset()
  {
    this.mStats.clear();
    this.mStartMillis = Long.MAX_VALUE;
    this.mEndMillis = Long.MIN_VALUE;
    this.mTotalBytes = 0L;
    this.mDirty = false;
  }
  
  public void write(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    HashMap localHashMap = Maps.newHashMap();
    Object localObject3 = this.mStats.keySet().iterator();
    Object localObject4;
    Object localObject2;
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (Key)((Iterator)localObject3).next();
      localObject2 = (ArrayList)localHashMap.get(((Key)localObject4).ident);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = Lists.newArrayList();
        localHashMap.put(((Key)localObject4).ident, localObject1);
      }
      ((ArrayList)localObject1).add(localObject4);
    }
    paramDataOutputStream.writeInt(1095648596);
    paramDataOutputStream.writeInt(16);
    paramDataOutputStream.writeInt(localHashMap.size());
    Object localObject1 = localHashMap.keySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (NetworkIdentitySet)((Iterator)localObject1).next();
      localObject3 = (ArrayList)localHashMap.get(localObject2);
      ((NetworkIdentitySet)localObject2).writeToStream(paramDataOutputStream);
      paramDataOutputStream.writeInt(((ArrayList)localObject3).size());
      localObject2 = ((Iterable)localObject3).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Key)((Iterator)localObject2).next();
        localObject4 = (NetworkStatsHistory)this.mStats.get(localObject3);
        paramDataOutputStream.writeInt(((Key)localObject3).uid);
        paramDataOutputStream.writeInt(((Key)localObject3).set);
        paramDataOutputStream.writeInt(((Key)localObject3).tag);
        ((NetworkStatsHistory)localObject4).writeToStream(paramDataOutputStream);
      }
    }
    paramDataOutputStream.flush();
  }
  
  private static class Key
    implements Comparable<Key>
  {
    private final int hashCode;
    public final NetworkIdentitySet ident;
    public final int set;
    public final int tag;
    public final int uid;
    
    public Key(NetworkIdentitySet paramNetworkIdentitySet, int paramInt1, int paramInt2, int paramInt3)
    {
      this.ident = paramNetworkIdentitySet;
      this.uid = paramInt1;
      this.set = paramInt2;
      this.tag = paramInt3;
      this.hashCode = Objects.hash(new Object[] { paramNetworkIdentitySet, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
    }
    
    public int compareTo(Key paramKey)
    {
      int j = 0;
      int i = j;
      if (this.ident != null)
      {
        i = j;
        if (paramKey.ident != null) {
          i = this.ident.compareTo(paramKey.ident);
        }
      }
      j = i;
      if (i == 0) {
        j = Integer.compare(this.uid, paramKey.uid);
      }
      i = j;
      if (j == 0) {
        i = Integer.compare(this.set, paramKey.set);
      }
      j = i;
      if (i == 0) {
        j = Integer.compare(this.tag, paramKey.tag);
      }
      return j;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if ((paramObject instanceof Key))
      {
        paramObject = (Key)paramObject;
        boolean bool1 = bool2;
        if (this.uid == ((Key)paramObject).uid)
        {
          bool1 = bool2;
          if (this.set == ((Key)paramObject).set)
          {
            bool1 = bool2;
            if (this.tag == ((Key)paramObject).tag) {
              bool1 = Objects.equals(this.ident, ((Key)paramObject).ident);
            }
          }
        }
        return bool1;
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.hashCode;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/net/NetworkStatsCollection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */