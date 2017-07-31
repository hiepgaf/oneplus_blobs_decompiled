package com.android.server.net;

import android.content.Context;
import android.net.INetworkPolicyManager;
import android.net.NetworkPolicy;
import android.net.NetworkTemplate;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.util.Log;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class NetworkPolicyManagerShellCommand
  extends ShellCommand
{
  private final INetworkPolicyManager mInterface;
  private final WifiManager mWifiManager;
  
  NetworkPolicyManagerShellCommand(Context paramContext, INetworkPolicyManager paramINetworkPolicyManager)
  {
    this.mInterface = paramINetworkPolicyManager;
    this.mWifiManager = ((WifiManager)paramContext.getSystemService("wifi"));
  }
  
  private int addRestrictBackgroundBlacklist()
    throws RemoteException
  {
    int i = getUidFromNextArg();
    if (i < 0) {
      return i;
    }
    this.mInterface.setUidPolicy(i, 1);
    return 0;
  }
  
  private int addRestrictBackgroundWhitelist()
    throws RemoteException
  {
    int i = getUidFromNextArg();
    if (i < 0) {
      return i;
    }
    this.mInterface.addRestrictBackgroundWhitelistedUid(i);
    return 0;
  }
  
  private String getNetworkId(NetworkPolicy paramNetworkPolicy)
  {
    return WifiInfo.removeDoubleQuotes(paramNetworkPolicy.template.getNetworkId());
  }
  
  private int getNextBooleanArg()
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    String str = getNextArg();
    if (str == null)
    {
      localPrintWriter.println("Error: didn't specify BOOLEAN");
      return -1;
    }
    if (Boolean.valueOf(str).booleanValue()) {
      return 1;
    }
    return 0;
  }
  
  private int getRestrictBackground()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    localPrintWriter.print("Restrict background status: ");
    if (this.mInterface.getRestrictBackground()) {}
    for (String str = "enabled";; str = "disabled")
    {
      localPrintWriter.println(str);
      return 0;
    }
  }
  
  private int getUidFromNextArg()
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    String str = getNextArg();
    if (str == null)
    {
      localPrintWriter.println("Error: didn't specify UID");
      return -1;
    }
    try
    {
      int i = Integer.parseInt(str);
      return i;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      localPrintWriter.println("Error: UID (" + str + ") should be a number");
    }
    return -2;
  }
  
  private List<NetworkPolicy> getWifiPolicies()
    throws RemoteException
  {
    Object localObject2 = this.mWifiManager.getConfiguredNetworks();
    if (localObject2 != null) {}
    for (int i = ((List)localObject2).size();; i = 0)
    {
      localObject1 = new HashSet(i);
      if (localObject2 == null) {
        break;
      }
      localObject2 = ((Iterable)localObject2).iterator();
      while (((Iterator)localObject2).hasNext()) {
        ((Set)localObject1).add(WifiInfo.removeDoubleQuotes(((WifiConfiguration)((Iterator)localObject2).next()).SSID));
      }
    }
    NetworkPolicy[] arrayOfNetworkPolicy = this.mInterface.getNetworkPolicies(null);
    localObject2 = new ArrayList(arrayOfNetworkPolicy.length);
    i = 0;
    int j = arrayOfNetworkPolicy.length;
    while (i < j)
    {
      NetworkPolicy localNetworkPolicy = arrayOfNetworkPolicy[i];
      if (!localNetworkPolicy.template.isMatchRuleMobile())
      {
        ((List)localObject2).add(localNetworkPolicy);
        ((Set)localObject1).remove(getNetworkId(localNetworkPolicy));
      }
      i += 1;
    }
    Object localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext()) {
      ((List)localObject2).add(newPolicy((String)((Iterator)localObject1).next()));
    }
    return (List<NetworkPolicy>)localObject2;
  }
  
  private int listRestrictBackgroundBlacklist()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    int[] arrayOfInt = this.mInterface.getUidsWithPolicy(1);
    localPrintWriter.print("Restrict background blacklisted UIDs: ");
    if (arrayOfInt.length == 0) {
      localPrintWriter.println("none");
    }
    for (;;)
    {
      localPrintWriter.println();
      return 0;
      int i = 0;
      while (i < arrayOfInt.length)
      {
        localPrintWriter.print(arrayOfInt[i]);
        localPrintWriter.print(' ');
        i += 1;
      }
    }
  }
  
  private int listRestrictBackgroundWhitelist()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    int[] arrayOfInt = this.mInterface.getRestrictBackgroundWhitelistedUids();
    localPrintWriter.print("Restrict background whitelisted UIDs: ");
    if (arrayOfInt.length == 0) {
      localPrintWriter.println("none");
    }
    for (;;)
    {
      localPrintWriter.println();
      return 0;
      int i = 0;
      while (i < arrayOfInt.length)
      {
        localPrintWriter.print(arrayOfInt[i]);
        localPrintWriter.print(' ');
        i += 1;
      }
    }
  }
  
  private int listWifiNetworks()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    Object localObject = getNextArg();
    if (localObject == null) {}
    for (localObject = null;; localObject = Boolean.valueOf((String)localObject))
    {
      Iterator localIterator = getWifiPolicies().iterator();
      while (localIterator.hasNext())
      {
        NetworkPolicy localNetworkPolicy = (NetworkPolicy)localIterator.next();
        if ((localObject == null) || (((Boolean)localObject).booleanValue() == localNetworkPolicy.metered))
        {
          localPrintWriter.print(getNetworkId(localNetworkPolicy));
          localPrintWriter.print(';');
          localPrintWriter.println(localNetworkPolicy.metered);
        }
      }
    }
    return 0;
  }
  
  private NetworkPolicy newPolicy(String paramString)
  {
    return NetworkPolicyManagerService.newWifiPolicy(NetworkTemplate.buildTemplateWifi(paramString), false);
  }
  
  private int removeRestrictBackgroundBlacklist()
    throws RemoteException
  {
    int i = getUidFromNextArg();
    if (i < 0) {
      return i;
    }
    this.mInterface.setUidPolicy(i, 0);
    return 0;
  }
  
  private int removeRestrictBackgroundWhitelist()
    throws RemoteException
  {
    int i = getUidFromNextArg();
    if (i < 0) {
      return i;
    }
    this.mInterface.removeRestrictBackgroundWhitelistedUid(i);
    return 0;
  }
  
  private int runAdd()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    String str = getNextArg();
    if (str == null)
    {
      localPrintWriter.println("Error: didn't specify type of data to add");
      return -1;
    }
    if (str.equals("restrict-background-whitelist")) {
      return addRestrictBackgroundWhitelist();
    }
    if (str.equals("restrict-background-blacklist")) {
      return addRestrictBackgroundBlacklist();
    }
    localPrintWriter.println("Error: unknown add type '" + str + "'");
    return -1;
  }
  
  private int runGet()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    String str = getNextArg();
    if (str == null)
    {
      localPrintWriter.println("Error: didn't specify type of data to get");
      return -1;
    }
    if (str.equals("restrict-background")) {
      return getRestrictBackground();
    }
    localPrintWriter.println("Error: unknown get type '" + str + "'");
    return -1;
  }
  
  private int runList()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    String str = getNextArg();
    if (str == null)
    {
      localPrintWriter.println("Error: didn't specify type of data to list");
      return -1;
    }
    if (str.equals("wifi-networks")) {
      return listWifiNetworks();
    }
    if (str.equals("restrict-background-whitelist")) {
      return listRestrictBackgroundWhitelist();
    }
    if (str.equals("restrict-background-blacklist")) {
      return listRestrictBackgroundBlacklist();
    }
    localPrintWriter.println("Error: unknown list type '" + str + "'");
    return -1;
  }
  
  private int runRemove()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    String str = getNextArg();
    if (str == null)
    {
      localPrintWriter.println("Error: didn't specify type of data to remove");
      return -1;
    }
    if (str.equals("restrict-background-whitelist")) {
      return removeRestrictBackgroundWhitelist();
    }
    if (str.equals("restrict-background-blacklist")) {
      return removeRestrictBackgroundBlacklist();
    }
    localPrintWriter.println("Error: unknown remove type '" + str + "'");
    return -1;
  }
  
  private int runSet()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    String str = getNextArg();
    if (str == null)
    {
      localPrintWriter.println("Error: didn't specify type of data to set");
      return -1;
    }
    if (str.equals("metered-network")) {
      return setMeteredWifiNetwork();
    }
    if (str.equals("restrict-background")) {
      return setRestrictBackground();
    }
    localPrintWriter.println("Error: unknown set type '" + str + "'");
    return -1;
  }
  
  private int setMeteredWifiNetwork()
    throws RemoteException
  {
    Object localObject1 = getOutPrintWriter();
    String str = getNextArg();
    if (str == null)
    {
      ((PrintWriter)localObject1).println("Error: didn't specify ID");
      return -1;
    }
    Object localObject2 = getNextArg();
    if (localObject2 == null)
    {
      ((PrintWriter)localObject1).println("Error: didn't specify BOOLEAN");
      return -1;
    }
    boolean bool = Boolean.valueOf((String)localObject2).booleanValue();
    localObject1 = this.mInterface.getNetworkPolicies(null);
    int j = 0;
    int i = 0;
    int m = localObject1.length;
    label124:
    Object localObject3;
    if (i < m)
    {
      localObject2 = localObject1[i];
      int k = j;
      if (!((NetworkPolicy)localObject2).template.isMatchRuleMobile())
      {
        if (((NetworkPolicy)localObject2).metered != bool) {
          break label124;
        }
        k = j;
      }
      for (;;)
      {
        i += 1;
        j = k;
        break;
        localObject3 = getNetworkId((NetworkPolicy)localObject2);
        k = j;
        if (str.equals(localObject3))
        {
          Log.i("NetworkPolicy", "Changing " + (String)localObject3 + " metered status to " + bool);
          ((NetworkPolicy)localObject2).metered = bool;
          k = 1;
        }
      }
    }
    if (j != 0)
    {
      this.mInterface.setNetworkPolicies((NetworkPolicy[])localObject1);
      return 0;
    }
    localObject2 = this.mWifiManager.getConfiguredNetworks().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      Object localObject4 = WifiInfo.removeDoubleQuotes(((WifiConfiguration)((Iterator)localObject2).next()).SSID);
      if (str.equals(localObject4))
      {
        localObject3 = newPolicy((String)localObject4);
        ((NetworkPolicy)localObject3).metered = true;
        Log.i("NetworkPolicy", "Creating new policy for " + (String)localObject4 + ": " + localObject3);
        localObject4 = new NetworkPolicy[localObject1.length + 1];
        System.arraycopy(localObject1, 0, localObject4, 0, localObject1.length);
        localObject4[(localObject4.length - 1)] = localObject3;
        this.mInterface.setNetworkPolicies((NetworkPolicy[])localObject4);
      }
    }
    return 0;
  }
  
  private int setRestrictBackground()
    throws RemoteException
  {
    int i = getNextBooleanArg();
    if (i < 0) {
      return i;
    }
    INetworkPolicyManager localINetworkPolicyManager = this.mInterface;
    if (i > 0) {}
    for (boolean bool = true;; bool = false)
    {
      localINetworkPolicyManager.setRestrictBackground(bool);
      return 0;
    }
  }
  
  public int onCommand(String paramString)
  {
    if (paramString == null) {
      return handleDefaultCommands(paramString);
    }
    PrintWriter localPrintWriter = getOutPrintWriter();
    try
    {
      if (paramString.equals("get")) {
        return runGet();
      }
      if (paramString.equals("set")) {
        return runSet();
      }
      if (paramString.equals("list")) {
        return runList();
      }
      if (paramString.equals("add")) {
        return runAdd();
      }
      if (paramString.equals("remove")) {
        return runRemove();
      }
      int i = handleDefaultCommands(paramString);
      return i;
    }
    catch (RemoteException paramString)
    {
      localPrintWriter.println("Remote exception: " + paramString);
    }
    return -1;
  }
  
  public void onHelp()
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    localPrintWriter.println("Network policy manager (netpolicy) commands:");
    localPrintWriter.println("  help");
    localPrintWriter.println("    Print this help text.");
    localPrintWriter.println("");
    localPrintWriter.println("  add restrict-background-whitelist UID");
    localPrintWriter.println("    Adds a UID to the whitelist for restrict background usage.");
    localPrintWriter.println("  add restrict-background-blacklist UID");
    localPrintWriter.println("    Adds a UID to the blacklist for restrict background usage.");
    localPrintWriter.println("  get restrict-background");
    localPrintWriter.println("    Gets the global restrict background usage status.");
    localPrintWriter.println("  list wifi-networks [BOOLEAN]");
    localPrintWriter.println("    Lists all saved wifi networks and whether they are metered or not.");
    localPrintWriter.println("    If a boolean argument is passed, filters just the metered (or unmetered)");
    localPrintWriter.println("    networks.");
    localPrintWriter.println("  list restrict-background-whitelist");
    localPrintWriter.println("    Lists UIDs that are whitelisted for restrict background usage.");
    localPrintWriter.println("  list restrict-background-blacklist");
    localPrintWriter.println("    Lists UIDs that are blacklisted for restrict background usage.");
    localPrintWriter.println("  remove restrict-background-whitelist UID");
    localPrintWriter.println("    Removes a UID from the whitelist for restrict background usage.");
    localPrintWriter.println("  remove restrict-background-blacklist UID");
    localPrintWriter.println("    Removes a UID from the blacklist for restrict background usage.");
    localPrintWriter.println("  set metered-network ID BOOLEAN");
    localPrintWriter.println("    Toggles whether the given wi-fi network is metered.");
    localPrintWriter.println("  set restrict-background BOOLEAN");
    localPrintWriter.println("    Sets the global restrict background usage status.");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/net/NetworkPolicyManagerShellCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */