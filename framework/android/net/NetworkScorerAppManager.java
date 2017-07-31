package android.net;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class NetworkScorerAppManager
{
  private static final Intent SCORE_INTENT = new Intent("android.net.scoring.SCORE_NETWORKS");
  private static final String TAG = "NetworkScorerAppManager";
  
  public static NetworkScorerAppData getActiveScorer(Context paramContext)
  {
    return getScorer(paramContext, Settings.Global.getString(paramContext.getContentResolver(), "network_scorer_app"));
  }
  
  public static Collection<NetworkScorerAppData> getAllValidScorers(Context paramContext)
  {
    if (UserHandle.getCallingUserId() != 0) {
      return Collections.emptyList();
    }
    ArrayList localArrayList = new ArrayList();
    PackageManager localPackageManager = paramContext.getPackageManager();
    Iterator localIterator = localPackageManager.queryBroadcastReceiversAsUser(SCORE_INTENT, 0, 0).iterator();
    while (localIterator.hasNext())
    {
      ActivityInfo localActivityInfo = ((ResolveInfo)localIterator.next()).activityInfo;
      if ((localActivityInfo != null) && ("android.permission.BROADCAST_NETWORK_PRIVILEGED".equals(localActivityInfo.permission)) && (localPackageManager.checkPermission("android.permission.SCORE_NETWORKS", localActivityInfo.packageName) == 0))
      {
        Object localObject1 = null;
        paramContext = new Intent("android.net.scoring.CUSTOM_ENABLE");
        paramContext.setPackage(localActivityInfo.packageName);
        Object localObject2 = localPackageManager.queryIntentActivities(paramContext, 0);
        paramContext = (Context)localObject1;
        if (localObject2 != null)
        {
          if (!((List)localObject2).isEmpty()) {
            break label242;
          }
          paramContext = (Context)localObject1;
        }
        for (;;)
        {
          localObject2 = null;
          localObject1 = new Intent("android.net.scoring.SCORE_NETWORKS");
          ((Intent)localObject1).setPackage(localActivityInfo.packageName);
          ResolveInfo localResolveInfo = localPackageManager.resolveService((Intent)localObject1, 0);
          localObject1 = localObject2;
          if (localResolveInfo != null)
          {
            localObject1 = localObject2;
            if (localResolveInfo.serviceInfo != null) {
              localObject1 = localResolveInfo.serviceInfo.name;
            }
          }
          localArrayList.add(new NetworkScorerAppData(localActivityInfo.packageName, localActivityInfo.applicationInfo.uid, localActivityInfo.loadLabel(localPackageManager), paramContext, (String)localObject1));
          break;
          label242:
          localObject2 = ((ResolveInfo)((List)localObject2).get(0)).activityInfo;
          paramContext = (Context)localObject1;
          if (localObject2 != null) {
            paramContext = ((ActivityInfo)localObject2).name;
          }
        }
      }
    }
    return localArrayList;
  }
  
  public static NetworkScorerAppData getScorer(Context paramContext, String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    paramContext = getAllValidScorers(paramContext).iterator();
    while (paramContext.hasNext())
    {
      NetworkScorerAppData localNetworkScorerAppData = (NetworkScorerAppData)paramContext.next();
      if (paramString.equals(localNetworkScorerAppData.mPackageName)) {
        return localNetworkScorerAppData;
      }
    }
    return null;
  }
  
  public static boolean isCallerActiveScorer(Context paramContext, int paramInt)
  {
    boolean bool = false;
    NetworkScorerAppData localNetworkScorerAppData = getActiveScorer(paramContext);
    if (localNetworkScorerAppData == null) {
      return false;
    }
    if (paramInt != localNetworkScorerAppData.mPackageUid) {
      return false;
    }
    if (paramContext.checkCallingPermission("android.permission.SCORE_NETWORKS") == 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean setActiveScorer(Context paramContext, String paramString)
  {
    String str = Settings.Global.getString(paramContext.getContentResolver(), "network_scorer_app");
    if (TextUtils.equals(str, paramString)) {
      return true;
    }
    Log.i("NetworkScorerAppManager", "Changing network scorer from " + str + " to " + paramString);
    if (paramString == null)
    {
      Settings.Global.putString(paramContext.getContentResolver(), "network_scorer_app", null);
      return true;
    }
    if (getScorer(paramContext, paramString) != null)
    {
      Settings.Global.putString(paramContext.getContentResolver(), "network_scorer_app", paramString);
      return true;
    }
    Log.w("NetworkScorerAppManager", "Requested network scorer is not valid: " + paramString);
    return false;
  }
  
  public static class NetworkScorerAppData
  {
    public final String mConfigurationActivityClassName;
    public final String mPackageName;
    public final int mPackageUid;
    public final CharSequence mScorerName;
    public final String mScoringServiceClassName;
    
    public NetworkScorerAppData(String paramString1, int paramInt, CharSequence paramCharSequence, String paramString2, String paramString3)
    {
      this.mScorerName = paramCharSequence;
      this.mPackageName = paramString1;
      this.mPackageUid = paramInt;
      this.mConfigurationActivityClassName = paramString2;
      this.mScoringServiceClassName = paramString3;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("NetworkScorerAppData{");
      localStringBuilder.append("mPackageName='").append(this.mPackageName).append('\'');
      localStringBuilder.append(", mPackageUid=").append(this.mPackageUid);
      localStringBuilder.append(", mScorerName=").append(this.mScorerName);
      localStringBuilder.append(", mConfigurationActivityClassName='").append(this.mConfigurationActivityClassName).append('\'');
      localStringBuilder.append(", mScoringServiceClassName='").append(this.mScoringServiceClassName).append('\'');
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkScorerAppManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */