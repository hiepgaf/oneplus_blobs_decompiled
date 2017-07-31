package com.android.server;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.net.INetworkScoreCache;
import android.net.INetworkScoreService.Stub;
import android.net.NetworkKey;
import android.net.NetworkScorerAppManager;
import android.net.NetworkScorerAppManager.NetworkScorerAppData;
import android.net.ScoredNetwork;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NetworkScoreService
  extends INetworkScoreService.Stub
{
  private static final boolean DBG = false;
  private static final String TAG = "NetworkScoreService";
  private final Context mContext;
  @GuardedBy("mPackageMonitorLock")
  private NetworkScorerPackageMonitor mPackageMonitor;
  private final Object mPackageMonitorLock = new Object[0];
  private final Map<Integer, INetworkScoreCache> mScoreCaches;
  private ScoringServiceConnection mServiceConnection;
  private BroadcastReceiver mUserIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536);
      if (i == 55536) {
        return;
      }
      if ("android.intent.action.USER_UNLOCKED".equals(paramAnonymousContext)) {
        NetworkScoreService.-wrap2(NetworkScoreService.this, i);
      }
    }
  };
  
  public NetworkScoreService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mScoreCaches = new HashMap();
    paramContext = new IntentFilter("android.intent.action.USER_UNLOCKED");
    this.mContext.registerReceiverAsUser(this.mUserIntentReceiver, UserHandle.SYSTEM, paramContext, null, null);
  }
  
  private void bindToScoringServiceIfNeeded()
  {
    bindToScoringServiceIfNeeded(NetworkScorerAppManager.getActiveScorer(this.mContext));
  }
  
  private void bindToScoringServiceIfNeeded(NetworkScorerAppManager.NetworkScorerAppData paramNetworkScorerAppData)
  {
    if ((paramNetworkScorerAppData != null) && (paramNetworkScorerAppData.mScoringServiceClassName != null))
    {
      paramNetworkScorerAppData = new ComponentName(paramNetworkScorerAppData.mPackageName, paramNetworkScorerAppData.mScoringServiceClassName);
      if ((this.mServiceConnection == null) || (ScoringServiceConnection.-get0(this.mServiceConnection).equals(paramNetworkScorerAppData))) {}
      for (;;)
      {
        if (this.mServiceConnection == null) {
          this.mServiceConnection = new ScoringServiceConnection(paramNetworkScorerAppData);
        }
        this.mServiceConnection.connect(this.mContext);
        return;
        unbindFromScoringServiceIfNeeded();
      }
    }
    unbindFromScoringServiceIfNeeded();
  }
  
  private void clearInternal()
  {
    Iterator localIterator = getScoreCaches().iterator();
    while (localIterator.hasNext())
    {
      INetworkScoreCache localINetworkScoreCache = (INetworkScoreCache)localIterator.next();
      try
      {
        localINetworkScoreCache.clearScores();
      }
      catch (RemoteException localRemoteException) {}
      if (Log.isLoggable("NetworkScoreService", 2)) {
        Log.v("NetworkScoreService", "Unable to clear scores", localRemoteException);
      }
    }
  }
  
  private Set<INetworkScoreCache> getScoreCaches()
  {
    synchronized (this.mScoreCaches)
    {
      HashSet localHashSet = new HashSet(this.mScoreCaches.values());
      return localHashSet;
    }
  }
  
  private void onUserUnlocked(int paramInt)
  {
    registerPackageMonitorIfNeeded();
    bindToScoringServiceIfNeeded();
  }
  
  private void registerPackageMonitorIfNeeded()
  {
    NetworkScorerAppManager.NetworkScorerAppData localNetworkScorerAppData = NetworkScorerAppManager.getActiveScorer(this.mContext);
    synchronized (this.mPackageMonitorLock)
    {
      if (this.mPackageMonitor != null)
      {
        this.mPackageMonitor.unregister();
        this.mPackageMonitor = null;
      }
      if (localNetworkScorerAppData != null)
      {
        this.mPackageMonitor = new NetworkScorerPackageMonitor(localNetworkScorerAppData.mPackageName, null);
        this.mPackageMonitor.register(this.mContext, null, UserHandle.SYSTEM, false);
      }
      return;
    }
  }
  
  private boolean setScorerInternal(String paramString)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      unbindFromScoringServiceIfNeeded();
      clearInternal();
      NetworkScorerAppManager.NetworkScorerAppData localNetworkScorerAppData = NetworkScorerAppManager.getActiveScorer(this.mContext);
      boolean bool = NetworkScorerAppManager.setActiveScorer(this.mContext, paramString);
      bindToScoringServiceIfNeeded();
      if (bool)
      {
        registerPackageMonitorIfNeeded();
        Intent localIntent = new Intent("android.net.scoring.SCORER_CHANGED");
        if (localNetworkScorerAppData != null)
        {
          localIntent.setPackage(localNetworkScorerAppData.mPackageName);
          this.mContext.sendBroadcastAsUser(localIntent, UserHandle.SYSTEM);
        }
        if (paramString != null)
        {
          localIntent.putExtra("newScorer", paramString);
          localIntent.setPackage(paramString);
          this.mContext.sendBroadcastAsUser(localIntent, UserHandle.SYSTEM);
        }
      }
      return bool;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void unbindFromScoringServiceIfNeeded()
  {
    if (this.mServiceConnection != null) {
      this.mServiceConnection.disconnect(this.mContext);
    }
    this.mServiceConnection = null;
  }
  
  public boolean clearScores()
  {
    if ((NetworkScorerAppManager.isCallerActiveScorer(this.mContext, getCallingUid())) || (this.mContext.checkCallingOrSelfPermission("android.permission.BROADCAST_NETWORK_PRIVILEGED") == 0))
    {
      clearInternal();
      return true;
    }
    throw new SecurityException("Caller is neither the active scorer nor the scorer manager.");
  }
  
  public void disableScoring()
  {
    if ((NetworkScorerAppManager.isCallerActiveScorer(this.mContext, getCallingUid())) || (this.mContext.checkCallingOrSelfPermission("android.permission.BROADCAST_NETWORK_PRIVILEGED") == 0))
    {
      setScorerInternal(null);
      return;
    }
    throw new SecurityException("Caller is neither the active scorer nor the scorer manager.");
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "NetworkScoreService");
    Object localObject = NetworkScorerAppManager.getActiveScorer(this.mContext);
    if (localObject == null)
    {
      paramPrintWriter.println("Scoring is disabled.");
      return;
    }
    paramPrintWriter.println("Current scorer: " + ((NetworkScorerAppManager.NetworkScorerAppData)localObject).mPackageName);
    localObject = getScoreCaches().iterator();
    while (((Iterator)localObject).hasNext())
    {
      INetworkScoreCache localINetworkScoreCache = (INetworkScoreCache)((Iterator)localObject).next();
      try
      {
        localINetworkScoreCache.asBinder().dump(paramFileDescriptor, paramArrayOfString);
      }
      catch (RemoteException localRemoteException)
      {
        paramPrintWriter.println("Unable to dump score cache");
      }
      if (Log.isLoggable("NetworkScoreService", 2)) {
        Log.v("NetworkScoreService", "Unable to dump score cache", localRemoteException);
      }
    }
    if (this.mServiceConnection != null) {
      this.mServiceConnection.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    for (;;)
    {
      paramPrintWriter.flush();
      return;
      paramPrintWriter.println("ScoringServiceConnection: null");
    }
  }
  
  public void registerNetworkScoreCache(int paramInt, INetworkScoreCache paramINetworkScoreCache)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.BROADCAST_NETWORK_PRIVILEGED", "NetworkScoreService");
    synchronized (this.mScoreCaches)
    {
      if (this.mScoreCaches.containsKey(Integer.valueOf(paramInt))) {
        throw new IllegalArgumentException("Score cache already registered for type " + paramInt);
      }
    }
    this.mScoreCaches.put(Integer.valueOf(paramInt), paramINetworkScoreCache);
  }
  
  public boolean setActiveScorer(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.SCORE_NETWORKS", "NetworkScoreService");
    return setScorerInternal(paramString);
  }
  
  void systemReady()
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (Settings.Global.getInt(localContentResolver, "network_scoring_provisioned", 0) == 0)
    {
      String str = this.mContext.getResources().getString(17039462);
      if (!TextUtils.isEmpty(str)) {
        NetworkScorerAppManager.setActiveScorer(this.mContext, str);
      }
      Settings.Global.putInt(localContentResolver, "network_scoring_provisioned", 1);
    }
    registerPackageMonitorIfNeeded();
  }
  
  void systemRunning()
  {
    bindToScoringServiceIfNeeded();
  }
  
  public boolean updateScores(ScoredNetwork[] paramArrayOfScoredNetwork)
  {
    if (!NetworkScorerAppManager.isCallerActiveScorer(this.mContext, getCallingUid())) {
      throw new SecurityException("Caller with UID " + getCallingUid() + " is not the active scorer.");
    }
    HashMap localHashMap = new HashMap();
    int i = 0;
    int j = paramArrayOfScoredNetwork.length;
    Object localObject2;
    Object localObject1;
    while (i < j)
    {
      ScoredNetwork localScoredNetwork = paramArrayOfScoredNetwork[i];
      localObject2 = (List)localHashMap.get(Integer.valueOf(localScoredNetwork.networkKey.type));
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new ArrayList();
        localHashMap.put(Integer.valueOf(localScoredNetwork.networkKey.type), localObject1);
      }
      ((List)localObject1).add(localScoredNetwork);
      i += 1;
    }
    paramArrayOfScoredNetwork = localHashMap.entrySet().iterator();
    while (paramArrayOfScoredNetwork.hasNext())
    {
      localObject1 = (Map.Entry)paramArrayOfScoredNetwork.next();
      localObject2 = (INetworkScoreCache)this.mScoreCaches.get(((Map.Entry)localObject1).getKey());
      if (localObject2 != null)
      {
        try
        {
          ((INetworkScoreCache)localObject2).updateScores((List)((Map.Entry)localObject1).getValue());
        }
        catch (RemoteException localRemoteException) {}
        if (Log.isLoggable("NetworkScoreService", 2)) {
          Log.v("NetworkScoreService", "Unable to update scores of type " + ((Map.Entry)localObject1).getKey(), localRemoteException);
        }
      }
      else if (Log.isLoggable("NetworkScoreService", 2))
      {
        Log.v("NetworkScoreService", "No scorer registered for type " + ((Map.Entry)localObject1).getKey() + ", discarding");
      }
    }
    return true;
  }
  
  private class NetworkScorerPackageMonitor
    extends PackageMonitor
  {
    final String mRegisteredPackage;
    
    private NetworkScorerPackageMonitor(String paramString)
    {
      this.mRegisteredPackage = paramString;
    }
    
    private void evaluateBinding(String paramString, boolean paramBoolean)
    {
      if (this.mRegisteredPackage.equals(paramString))
      {
        paramString = NetworkScorerAppManager.getActiveScorer(NetworkScoreService.-get0(NetworkScoreService.this));
        if (paramString == null)
        {
          Log.i("NetworkScoreService", "Package " + this.mRegisteredPackage + " is no longer valid, disabling scoring.");
          NetworkScoreService.-wrap0(NetworkScoreService.this, null);
        }
      }
      else
      {
        return;
      }
      if (paramString.mScoringServiceClassName == null)
      {
        NetworkScoreService.-wrap3(NetworkScoreService.this);
        return;
      }
      if (paramBoolean) {
        NetworkScoreService.-wrap3(NetworkScoreService.this);
      }
      NetworkScoreService.-wrap1(NetworkScoreService.this, paramString);
    }
    
    public boolean onHandleForceStop(Intent paramIntent, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        int i = 0;
        int j = paramArrayOfString.length;
        while (i < j)
        {
          evaluateBinding(paramArrayOfString[i], true);
          i += 1;
        }
      }
      return super.onHandleForceStop(paramIntent, paramArrayOfString, paramInt, paramBoolean);
    }
    
    public void onPackageAdded(String paramString, int paramInt)
    {
      evaluateBinding(paramString, true);
    }
    
    public void onPackageModified(String paramString)
    {
      evaluateBinding(paramString, false);
    }
    
    public void onPackageRemoved(String paramString, int paramInt)
    {
      evaluateBinding(paramString, true);
    }
    
    public void onPackageUpdateFinished(String paramString, int paramInt)
    {
      evaluateBinding(paramString, true);
    }
  }
  
  private static class ScoringServiceConnection
    implements ServiceConnection
  {
    private boolean mBound = false;
    private final ComponentName mComponentName;
    private boolean mConnected = false;
    
    ScoringServiceConnection(ComponentName paramComponentName)
    {
      this.mComponentName = paramComponentName;
    }
    
    void connect(Context paramContext)
    {
      if (!this.mBound)
      {
        Intent localIntent = new Intent();
        localIntent.setComponent(this.mComponentName);
        this.mBound = paramContext.bindServiceAsUser(localIntent, this, 67108865, UserHandle.SYSTEM);
        if (!this.mBound) {
          Log.w("NetworkScoreService", "Bind call failed for " + localIntent);
        }
      }
    }
    
    void disconnect(Context paramContext)
    {
      try
      {
        if (this.mBound)
        {
          this.mBound = false;
          paramContext.unbindService(this);
        }
        return;
      }
      catch (RuntimeException paramContext)
      {
        Log.e("NetworkScoreService", "Unbind failed.", paramContext);
      }
    }
    
    public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramPrintWriter.println("ScoringServiceConnection: " + this.mComponentName + ", bound: " + this.mBound + ", connected: " + this.mConnected);
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      this.mConnected = true;
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      this.mConnected = false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/NetworkScoreService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */