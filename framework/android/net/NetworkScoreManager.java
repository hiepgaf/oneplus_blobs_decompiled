package android.net;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;

public class NetworkScoreManager
{
  public static final String ACTION_CHANGE_ACTIVE = "android.net.scoring.CHANGE_ACTIVE";
  public static final String ACTION_CUSTOM_ENABLE = "android.net.scoring.CUSTOM_ENABLE";
  public static final String ACTION_SCORER_CHANGED = "android.net.scoring.SCORER_CHANGED";
  public static final String ACTION_SCORE_NETWORKS = "android.net.scoring.SCORE_NETWORKS";
  public static final String EXTRA_NETWORKS_TO_SCORE = "networksToScore";
  public static final String EXTRA_NEW_SCORER = "newScorer";
  public static final String EXTRA_PACKAGE_NAME = "packageName";
  private final Context mContext;
  private final INetworkScoreService mService;
  
  public NetworkScoreManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mService = INetworkScoreService.Stub.asInterface(ServiceManager.getService("network_score"));
  }
  
  public boolean clearScores()
    throws SecurityException
  {
    try
    {
      boolean bool = this.mService.clearScores();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void disableScoring()
    throws SecurityException
  {
    try
    {
      this.mService.disableScoring();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getActiveScorerPackage()
  {
    NetworkScorerAppManager.NetworkScorerAppData localNetworkScorerAppData = NetworkScorerAppManager.getActiveScorer(this.mContext);
    if (localNetworkScorerAppData == null) {
      return null;
    }
    return localNetworkScorerAppData.mPackageName;
  }
  
  public void registerNetworkScoreCache(int paramInt, INetworkScoreCache paramINetworkScoreCache)
  {
    try
    {
      this.mService.registerNetworkScoreCache(paramInt, paramINetworkScoreCache);
      return;
    }
    catch (RemoteException paramINetworkScoreCache)
    {
      throw paramINetworkScoreCache.rethrowFromSystemServer();
    }
  }
  
  public boolean requestScores(NetworkKey[] paramArrayOfNetworkKey)
    throws SecurityException
  {
    String str = getActiveScorerPackage();
    if (str == null) {
      return false;
    }
    Intent localIntent = new Intent("android.net.scoring.SCORE_NETWORKS");
    localIntent.setPackage(str);
    localIntent.setFlags(67108864);
    localIntent.putExtra("networksToScore", paramArrayOfNetworkKey);
    this.mContext.sendBroadcastAsUser(localIntent, UserHandle.SYSTEM, "android.permission.SCORE_NETWORKS");
    return true;
  }
  
  public boolean setActiveScorer(String paramString)
    throws SecurityException
  {
    try
    {
      boolean bool = this.mService.setActiveScorer(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean updateScores(ScoredNetwork[] paramArrayOfScoredNetwork)
    throws SecurityException
  {
    try
    {
      boolean bool = this.mService.updateScores(paramArrayOfScoredNetwork);
      return bool;
    }
    catch (RemoteException paramArrayOfScoredNetwork)
    {
      throw paramArrayOfScoredNetwork.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkScoreManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */