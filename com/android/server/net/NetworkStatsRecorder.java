package com.android.server.net;

import android.net.NetworkStats;
import android.net.NetworkStats.Entry;
import android.net.NetworkStats.NonMonotonicObserver;
import android.net.NetworkTemplate;
import android.os.DropBoxManager;
import android.util.Log;
import android.util.MathUtils;
import com.android.internal.net.VpnInfo;
import com.android.internal.util.FileRotator;
import com.android.internal.util.FileRotator.Rewriter;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.google.android.collect.Sets;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import libcore.io.IoUtils;

public class NetworkStatsRecorder
{
  private static final boolean DUMP_BEFORE_DELETE = true;
  private static final boolean LOGD = false;
  private static final boolean LOGV = false;
  private static final String TAG = "NetworkStatsRecorder";
  private static final String TAG_NETSTATS_DUMP = "netstats_dump";
  private final long mBucketDuration;
  private WeakReference<NetworkStatsCollection> mComplete;
  private final String mCookie;
  private final DropBoxManager mDropBox;
  private NetworkStats mLastSnapshot;
  private final NetworkStats.NonMonotonicObserver<String> mObserver;
  private final boolean mOnlyTags;
  private final NetworkStatsCollection mPending;
  private final CombiningRewriter mPendingRewriter;
  private long mPersistThresholdBytes = 2097152L;
  private final FileRotator mRotator;
  private final NetworkStatsCollection mSinceBoot;
  
  public NetworkStatsRecorder()
  {
    this.mRotator = null;
    this.mObserver = null;
    this.mDropBox = null;
    this.mCookie = null;
    this.mBucketDuration = 31449600000L;
    this.mOnlyTags = false;
    this.mPending = null;
    this.mSinceBoot = new NetworkStatsCollection(this.mBucketDuration);
    this.mPendingRewriter = null;
  }
  
  public NetworkStatsRecorder(FileRotator paramFileRotator, NetworkStats.NonMonotonicObserver<String> paramNonMonotonicObserver, DropBoxManager paramDropBoxManager, String paramString, long paramLong, boolean paramBoolean)
  {
    this.mRotator = ((FileRotator)Preconditions.checkNotNull(paramFileRotator, "missing FileRotator"));
    this.mObserver = ((NetworkStats.NonMonotonicObserver)Preconditions.checkNotNull(paramNonMonotonicObserver, "missing NonMonotonicObserver"));
    this.mDropBox = ((DropBoxManager)Preconditions.checkNotNull(paramDropBoxManager, "missing DropBoxManager"));
    this.mCookie = paramString;
    this.mBucketDuration = paramLong;
    this.mOnlyTags = paramBoolean;
    this.mPending = new NetworkStatsCollection(paramLong);
    this.mSinceBoot = new NetworkStatsCollection(paramLong);
    this.mPendingRewriter = new CombiningRewriter(this.mPending);
  }
  
  private NetworkStatsCollection loadLocked(long paramLong1, long paramLong2)
  {
    NetworkStatsCollection localNetworkStatsCollection = new NetworkStatsCollection(this.mBucketDuration);
    try
    {
      this.mRotator.readMatching(localNetworkStatsCollection, paramLong1, paramLong2);
      localNetworkStatsCollection.recordCollection(this.mPending);
      return localNetworkStatsCollection;
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      Log.wtf("NetworkStatsRecorder", "problem completely reading network stats", localOutOfMemoryError);
      recoverFromWtf();
      return localNetworkStatsCollection;
    }
    catch (IOException localIOException)
    {
      Log.wtf("NetworkStatsRecorder", "problem completely reading network stats", localIOException);
      recoverFromWtf();
    }
    return localNetworkStatsCollection;
  }
  
  private void recoverFromWtf()
  {
    localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      this.mRotator.dumpAll(localByteArrayOutputStream);
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        localByteArrayOutputStream.reset();
        IoUtils.closeQuietly(localByteArrayOutputStream);
      }
    }
    finally
    {
      IoUtils.closeQuietly(localByteArrayOutputStream);
    }
    this.mDropBox.addData("netstats_dump", localByteArrayOutputStream.toByteArray(), 0);
    this.mRotator.deleteAll();
  }
  
  public void dumpCheckin(PrintWriter paramPrintWriter, long paramLong1, long paramLong2)
  {
    getOrLoadPartialLocked(paramLong1, paramLong2).dumpCheckin(paramPrintWriter, paramLong1, paramLong2);
  }
  
  public void dumpLocked(IndentingPrintWriter paramIndentingPrintWriter, boolean paramBoolean)
  {
    if (this.mPending != null)
    {
      paramIndentingPrintWriter.print("Pending bytes: ");
      paramIndentingPrintWriter.println(this.mPending.getTotalBytes());
    }
    if (paramBoolean)
    {
      paramIndentingPrintWriter.println("Complete history:");
      getOrLoadCompleteLocked().dump(paramIndentingPrintWriter);
      return;
    }
    paramIndentingPrintWriter.println("History since boot:");
    this.mSinceBoot.dump(paramIndentingPrintWriter);
  }
  
  public void forcePersistLocked(long paramLong)
  {
    Preconditions.checkNotNull(this.mRotator, "missing FileRotator");
    if (this.mPending.isDirty()) {}
    try
    {
      this.mRotator.rewriteActive(this.mPendingRewriter, paramLong);
      this.mRotator.maybeRotate(paramLong);
      this.mPending.reset();
      return;
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      Log.wtf("NetworkStatsRecorder", "problem persisting pending stats", localOutOfMemoryError);
      recoverFromWtf();
      return;
    }
    catch (IOException localIOException)
    {
      Log.wtf("NetworkStatsRecorder", "problem persisting pending stats", localIOException);
      recoverFromWtf();
    }
  }
  
  public NetworkStatsCollection getOrLoadCompleteLocked()
  {
    NetworkStatsCollection localNetworkStatsCollection1 = null;
    Preconditions.checkNotNull(this.mRotator, "missing FileRotator");
    if (this.mComplete != null) {
      localNetworkStatsCollection1 = (NetworkStatsCollection)this.mComplete.get();
    }
    NetworkStatsCollection localNetworkStatsCollection2 = localNetworkStatsCollection1;
    if (localNetworkStatsCollection1 == null)
    {
      localNetworkStatsCollection2 = loadLocked(Long.MIN_VALUE, Long.MAX_VALUE);
      this.mComplete = new WeakReference(localNetworkStatsCollection2);
    }
    return localNetworkStatsCollection2;
  }
  
  public NetworkStatsCollection getOrLoadPartialLocked(long paramLong1, long paramLong2)
  {
    NetworkStatsCollection localNetworkStatsCollection1 = null;
    Preconditions.checkNotNull(this.mRotator, "missing FileRotator");
    if (this.mComplete != null) {
      localNetworkStatsCollection1 = (NetworkStatsCollection)this.mComplete.get();
    }
    NetworkStatsCollection localNetworkStatsCollection2 = localNetworkStatsCollection1;
    if (localNetworkStatsCollection1 == null) {
      localNetworkStatsCollection2 = loadLocked(paramLong1, paramLong2);
    }
    return localNetworkStatsCollection2;
  }
  
  public NetworkStatsCollection getSinceBoot()
  {
    return this.mSinceBoot;
  }
  
  public NetworkStats.Entry getTotalSinceBootLocked(NetworkTemplate paramNetworkTemplate)
  {
    return this.mSinceBoot.getSummary(paramNetworkTemplate, Long.MIN_VALUE, Long.MAX_VALUE, 3).getTotal(null);
  }
  
  public void importLegacyNetworkLocked(File paramFile)
    throws IOException
  {
    Preconditions.checkNotNull(this.mRotator, "missing FileRotator");
    this.mRotator.deleteAll();
    NetworkStatsCollection localNetworkStatsCollection = new NetworkStatsCollection(this.mBucketDuration);
    localNetworkStatsCollection.readLegacyNetwork(paramFile);
    long l1 = localNetworkStatsCollection.getStartMillis();
    long l2 = localNetworkStatsCollection.getEndMillis();
    if (!localNetworkStatsCollection.isEmpty())
    {
      this.mRotator.rewriteActive(new CombiningRewriter(localNetworkStatsCollection), l1);
      this.mRotator.maybeRotate(l2);
    }
  }
  
  public void importLegacyUidLocked(File paramFile)
    throws IOException
  {
    Preconditions.checkNotNull(this.mRotator, "missing FileRotator");
    this.mRotator.deleteAll();
    NetworkStatsCollection localNetworkStatsCollection = new NetworkStatsCollection(this.mBucketDuration);
    localNetworkStatsCollection.readLegacyUid(paramFile, this.mOnlyTags);
    long l1 = localNetworkStatsCollection.getStartMillis();
    long l2 = localNetworkStatsCollection.getEndMillis();
    if (!localNetworkStatsCollection.isEmpty())
    {
      this.mRotator.rewriteActive(new CombiningRewriter(localNetworkStatsCollection), l1);
      this.mRotator.maybeRotate(l2);
    }
  }
  
  public void maybePersistLocked(long paramLong)
  {
    Preconditions.checkNotNull(this.mRotator, "missing FileRotator");
    if (this.mPending.getTotalBytes() >= this.mPersistThresholdBytes)
    {
      forcePersistLocked(paramLong);
      return;
    }
    this.mRotator.maybeRotate(paramLong);
  }
  
  public void recordSnapshotLocked(NetworkStats paramNetworkStats, Map<String, NetworkIdentitySet> paramMap, VpnInfo[] paramArrayOfVpnInfo, long paramLong)
  {
    HashSet localHashSet = Sets.newHashSet();
    if (paramNetworkStats == null) {
      return;
    }
    if (this.mLastSnapshot == null)
    {
      this.mLastSnapshot = paramNetworkStats;
      return;
    }
    if (this.mComplete != null) {}
    NetworkStats localNetworkStats;
    long l;
    Object localObject;
    for (NetworkStatsCollection localNetworkStatsCollection = (NetworkStatsCollection)this.mComplete.get();; localNetworkStatsCollection = null)
    {
      localNetworkStats = NetworkStats.subtract(paramNetworkStats, this.mLastSnapshot, this.mObserver, this.mCookie);
      l = paramLong - localNetworkStats.getElapsedRealtime();
      if (paramArrayOfVpnInfo == null) {
        break;
      }
      i = 0;
      int j = paramArrayOfVpnInfo.length;
      while (i < j)
      {
        localObject = paramArrayOfVpnInfo[i];
        localNetworkStats.migrateTun(((VpnInfo)localObject).ownerUid, ((VpnInfo)localObject).vpnIface, ((VpnInfo)localObject).primaryUnderlyingIface);
        i += 1;
      }
    }
    paramArrayOfVpnInfo = null;
    int i = 0;
    if (i < localNetworkStats.size())
    {
      paramArrayOfVpnInfo = localNetworkStats.getValues(i, paramArrayOfVpnInfo);
      localObject = (NetworkIdentitySet)paramMap.get(paramArrayOfVpnInfo.iface);
      if (localObject == null) {
        localHashSet.add(paramArrayOfVpnInfo.iface);
      }
      label321:
      for (;;)
      {
        i += 1;
        break;
        if (!paramArrayOfVpnInfo.isEmpty())
        {
          if (paramArrayOfVpnInfo.tag == 0) {}
          for (int k = 1;; k = 0)
          {
            if (k == this.mOnlyTags) {
              break label321;
            }
            if (this.mPending != null) {
              this.mPending.recordData((NetworkIdentitySet)localObject, paramArrayOfVpnInfo.uid, paramArrayOfVpnInfo.set, paramArrayOfVpnInfo.tag, l, paramLong, paramArrayOfVpnInfo);
            }
            if (this.mSinceBoot != null) {
              this.mSinceBoot.recordData((NetworkIdentitySet)localObject, paramArrayOfVpnInfo.uid, paramArrayOfVpnInfo.set, paramArrayOfVpnInfo.tag, l, paramLong, paramArrayOfVpnInfo);
            }
            if (localNetworkStatsCollection == null) {
              break;
            }
            localNetworkStatsCollection.recordData((NetworkIdentitySet)localObject, paramArrayOfVpnInfo.uid, paramArrayOfVpnInfo.set, paramArrayOfVpnInfo.tag, l, paramLong, paramArrayOfVpnInfo);
            break;
          }
        }
      }
    }
    this.mLastSnapshot = paramNetworkStats;
  }
  
  public void removeUidsLocked(int[] paramArrayOfInt)
  {
    NetworkStatsCollection localNetworkStatsCollection = null;
    if (this.mRotator != null) {}
    try
    {
      this.mRotator.rewriteAll(new RemoveUidRewriter(this.mBucketDuration, paramArrayOfInt));
      if (this.mPending != null) {
        this.mPending.removeUids(paramArrayOfInt);
      }
      if (this.mSinceBoot != null) {
        this.mSinceBoot.removeUids(paramArrayOfInt);
      }
      if (this.mLastSnapshot != null) {
        this.mLastSnapshot = this.mLastSnapshot.withoutUids(paramArrayOfInt);
      }
      if (this.mComplete != null) {
        localNetworkStatsCollection = (NetworkStatsCollection)this.mComplete.get();
      }
      if (localNetworkStatsCollection != null) {
        localNetworkStatsCollection.removeUids(paramArrayOfInt);
      }
      return;
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      for (;;)
      {
        Log.wtf("NetworkStatsRecorder", "problem removing UIDs " + Arrays.toString(paramArrayOfInt), localOutOfMemoryError);
        recoverFromWtf();
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Log.wtf("NetworkStatsRecorder", "problem removing UIDs " + Arrays.toString(paramArrayOfInt), localIOException);
        recoverFromWtf();
      }
    }
  }
  
  public void resetLocked()
  {
    this.mLastSnapshot = null;
    if (this.mPending != null) {
      this.mPending.reset();
    }
    if (this.mSinceBoot != null) {
      this.mSinceBoot.reset();
    }
    if (this.mComplete != null) {
      this.mComplete.clear();
    }
  }
  
  public void setPersistThreshold(long paramLong)
  {
    this.mPersistThresholdBytes = MathUtils.constrain(paramLong, 1024L, 104857600L);
  }
  
  private static class CombiningRewriter
    implements FileRotator.Rewriter
  {
    private final NetworkStatsCollection mCollection;
    
    public CombiningRewriter(NetworkStatsCollection paramNetworkStatsCollection)
    {
      this.mCollection = ((NetworkStatsCollection)Preconditions.checkNotNull(paramNetworkStatsCollection, "missing NetworkStatsCollection"));
    }
    
    public void read(InputStream paramInputStream)
      throws IOException
    {
      this.mCollection.read(paramInputStream);
    }
    
    public void reset() {}
    
    public boolean shouldWrite()
    {
      return true;
    }
    
    public void write(OutputStream paramOutputStream)
      throws IOException
    {
      this.mCollection.write(new DataOutputStream(paramOutputStream));
      this.mCollection.reset();
    }
  }
  
  public static class RemoveUidRewriter
    implements FileRotator.Rewriter
  {
    private final NetworkStatsCollection mTemp;
    private final int[] mUids;
    
    public RemoveUidRewriter(long paramLong, int[] paramArrayOfInt)
    {
      this.mTemp = new NetworkStatsCollection(paramLong);
      this.mUids = paramArrayOfInt;
    }
    
    public void read(InputStream paramInputStream)
      throws IOException
    {
      this.mTemp.read(paramInputStream);
      this.mTemp.clearDirty();
      this.mTemp.removeUids(this.mUids);
    }
    
    public void reset()
    {
      this.mTemp.reset();
    }
    
    public boolean shouldWrite()
    {
      return this.mTemp.isDirty();
    }
    
    public void write(OutputStream paramOutputStream)
      throws IOException
    {
      this.mTemp.write(new DataOutputStream(paramOutputStream));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/net/NetworkStatsRecorder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */