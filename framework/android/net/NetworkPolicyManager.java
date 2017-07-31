package android.net;

import android.content.Context;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.DebugUtils;
import java.util.Calendar;
import java.util.TimeZone;

public class NetworkPolicyManager
{
  private static final boolean ALLOW_PLATFORM_APP_POLICY = true;
  public static final String EXTRA_NETWORK_TEMPLATE = "android.net.NETWORK_TEMPLATE";
  public static final int FIREWALL_CHAIN_DOZABLE = 1;
  public static final String FIREWALL_CHAIN_NAME_DOZABLE = "dozable";
  public static final String FIREWALL_CHAIN_NAME_NONE = "none";
  public static final String FIREWALL_CHAIN_NAME_POWERSAVE = "powersave";
  public static final String FIREWALL_CHAIN_NAME_STANDBY = "standby";
  public static final int FIREWALL_CHAIN_NONE = 0;
  public static final int FIREWALL_CHAIN_POWERSAVE = 3;
  public static final int FIREWALL_CHAIN_STANDBY = 2;
  public static final int FIREWALL_RULE_ALLOW = 1;
  public static final int FIREWALL_RULE_DEFAULT = 0;
  public static final int FIREWALL_RULE_DENY = 2;
  public static final int FIREWALL_TYPE_BLACKLIST = 1;
  public static final int FIREWALL_TYPE_WHITELIST = 0;
  public static final int MASK_ALL_NETWORKS = 240;
  public static final int MASK_METERED_NETWORKS = 15;
  public static final int POLICY_ALLOW_BACKGROUND_BATTERY_SAVE = 2;
  public static final int POLICY_NONE = 0;
  public static final int POLICY_REJECT_METERED_BACKGROUND = 1;
  public static final int RULE_ALLOW_ALL = 32;
  public static final int RULE_ALLOW_METERED = 1;
  public static final int RULE_NONE = 0;
  public static final int RULE_REJECT_ALL = 64;
  public static final int RULE_REJECT_METERED = 4;
  public static final int RULE_TEMPORARY_ALLOW_METERED = 2;
  private final Context mContext;
  private INetworkPolicyManager mService;
  
  public NetworkPolicyManager(Context paramContext, INetworkPolicyManager paramINetworkPolicyManager)
  {
    if (paramINetworkPolicyManager == null) {
      throw new IllegalArgumentException("missing INetworkPolicyManager");
    }
    this.mContext = paramContext;
    this.mService = paramINetworkPolicyManager;
  }
  
  public static long computeLastCycleBoundary(long paramLong, NetworkPolicy paramNetworkPolicy)
  {
    if (paramNetworkPolicy.cycleDay == -1) {
      throw new IllegalArgumentException("Unable to compute boundary without cycleDay");
    }
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone(paramNetworkPolicy.cycleTimezone));
    localCalendar.setTimeInMillis(paramLong);
    snapToCycleDay(localCalendar, paramNetworkPolicy.cycleDay);
    if (localCalendar.getTimeInMillis() >= paramLong)
    {
      localCalendar.set(5, 1);
      localCalendar.add(2, -1);
      snapToCycleDay(localCalendar, paramNetworkPolicy.cycleDay);
    }
    return localCalendar.getTimeInMillis();
  }
  
  public static long computeNextCycleBoundary(long paramLong, NetworkPolicy paramNetworkPolicy)
  {
    if (paramNetworkPolicy.cycleDay == -1) {
      throw new IllegalArgumentException("Unable to compute boundary without cycleDay");
    }
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone(paramNetworkPolicy.cycleTimezone));
    localCalendar.setTimeInMillis(paramLong);
    snapToCycleDay(localCalendar, paramNetworkPolicy.cycleDay);
    if (localCalendar.getTimeInMillis() <= paramLong)
    {
      localCalendar.set(5, 1);
      localCalendar.add(2, 1);
      snapToCycleDay(localCalendar, paramNetworkPolicy.cycleDay);
    }
    return localCalendar.getTimeInMillis();
  }
  
  public static NetworkPolicyManager from(Context paramContext)
  {
    return (NetworkPolicyManager)paramContext.getSystemService("netpolicy");
  }
  
  @Deprecated
  public static boolean isUidValidForPolicy(Context paramContext, int paramInt)
  {
    return UserHandle.isApp(paramInt);
  }
  
  public static void snapToCycleDay(Calendar paramCalendar, int paramInt)
  {
    paramCalendar.set(11, 0);
    paramCalendar.set(12, 0);
    paramCalendar.set(13, 0);
    if (paramInt > paramCalendar.getActualMaximum(5))
    {
      paramCalendar.set(5, 1);
      paramCalendar.add(2, 1);
      paramCalendar.add(13, -1);
      return;
    }
    paramCalendar.set(5, paramInt);
  }
  
  public static String uidRulesToString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder().append(paramInt).append(" (");
    if (paramInt == 0) {
      localStringBuilder.append("NONE");
    }
    for (;;)
    {
      localStringBuilder.append(")");
      return localStringBuilder.toString();
      localStringBuilder.append(DebugUtils.flagsToString(NetworkPolicyManager.class, "RULE_", paramInt));
    }
  }
  
  public void addUidPolicy(int paramInt1, int paramInt2)
  {
    try
    {
      this.mService.addUidPolicy(paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void factoryReset(String paramString)
  {
    try
    {
      this.mService.factoryReset(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public NetworkPolicy[] getNetworkPolicies()
  {
    try
    {
      NetworkPolicy[] arrayOfNetworkPolicy = this.mService.getNetworkPolicies(this.mContext.getOpPackageName());
      return arrayOfNetworkPolicy;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean getRestrictBackground()
  {
    try
    {
      boolean bool = this.mService.getRestrictBackground();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getUidPolicy(int paramInt)
  {
    try
    {
      paramInt = this.mService.getUidPolicy(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int[] getUidsWithPolicy(int paramInt)
  {
    try
    {
      int[] arrayOfInt = this.mService.getUidsWithPolicy(paramInt);
      return arrayOfInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void registerListener(INetworkPolicyListener paramINetworkPolicyListener)
  {
    try
    {
      this.mService.registerListener(paramINetworkPolicyListener);
      return;
    }
    catch (RemoteException paramINetworkPolicyListener)
    {
      throw paramINetworkPolicyListener.rethrowFromSystemServer();
    }
  }
  
  public void removeUidPolicy(int paramInt1, int paramInt2)
  {
    try
    {
      this.mService.removeUidPolicy(paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setNetworkPolicies(NetworkPolicy[] paramArrayOfNetworkPolicy)
  {
    try
    {
      this.mService.setNetworkPolicies(paramArrayOfNetworkPolicy);
      return;
    }
    catch (RemoteException paramArrayOfNetworkPolicy)
    {
      throw paramArrayOfNetworkPolicy.rethrowFromSystemServer();
    }
  }
  
  public void setRestrictBackground(boolean paramBoolean)
  {
    try
    {
      this.mService.setRestrictBackground(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setUidPolicy(int paramInt1, int paramInt2)
  {
    try
    {
      this.mService.setUidPolicy(paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void unregisterListener(INetworkPolicyListener paramINetworkPolicyListener)
  {
    try
    {
      this.mService.unregisterListener(paramINetworkPolicyListener);
      return;
    }
    catch (RemoteException paramINetworkPolicyListener)
    {
      throw paramINetworkPolicyListener.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkPolicyManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */