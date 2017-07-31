package com.android.server.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.net.Uri;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PermissionMonitor
{
  private static final boolean DBG = false;
  private static final boolean NETWORK = false;
  private static final boolean SYSTEM = true;
  private static final String TAG = "PermissionMonitor";
  private final Map<Integer, Boolean> mApps = new HashMap();
  private final Context mContext;
  private final BroadcastReceiver mIntentReceiver;
  private final INetworkManagementService mNetd;
  private final PackageManager mPackageManager;
  private final UserManager mUserManager;
  private final Set<Integer> mUsers = new HashSet();
  
  public PermissionMonitor(Context paramContext, INetworkManagementService paramINetworkManagementService)
  {
    this.mContext = paramContext;
    this.mPackageManager = paramContext.getPackageManager();
    this.mUserManager = UserManager.get(paramContext);
    this.mNetd = paramINetworkManagementService;
    this.mIntentReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        String str = paramAnonymousIntent.getAction();
        int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536);
        int j = paramAnonymousIntent.getIntExtra("android.intent.extra.UID", -1);
        paramAnonymousContext = paramAnonymousIntent.getData();
        if (paramAnonymousContext != null)
        {
          paramAnonymousContext = paramAnonymousContext.getSchemeSpecificPart();
          if (!"android.intent.action.USER_ADDED".equals(str)) {
            break label63;
          }
          PermissionMonitor.-wrap2(PermissionMonitor.this, i);
        }
        label63:
        do
        {
          return;
          paramAnonymousContext = null;
          break;
          if ("android.intent.action.USER_REMOVED".equals(str))
          {
            PermissionMonitor.-wrap3(PermissionMonitor.this, i);
            return;
          }
          if ("android.intent.action.PACKAGE_ADDED".equals(str))
          {
            PermissionMonitor.-wrap0(PermissionMonitor.this, paramAnonymousContext, j);
            return;
          }
        } while (!"android.intent.action.PACKAGE_REMOVED".equals(str));
        PermissionMonitor.-wrap1(PermissionMonitor.this, j);
      }
    };
  }
  
  private boolean hasNetworkPermission(PackageInfo paramPackageInfo)
  {
    return hasPermission(paramPackageInfo, "android.permission.CHANGE_NETWORK_STATE");
  }
  
  private boolean hasPermission(PackageInfo paramPackageInfo, String paramString)
  {
    if (paramPackageInfo.requestedPermissions != null)
    {
      paramPackageInfo = paramPackageInfo.requestedPermissions;
      int j = paramPackageInfo.length;
      int i = 0;
      while (i < j)
      {
        if (paramString.equals(paramPackageInfo[i])) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  private boolean hasRestrictedNetworkPermission(PackageInfo paramPackageInfo)
  {
    boolean bool = true;
    int i = 0;
    if (paramPackageInfo.applicationInfo != null) {
      i = paramPackageInfo.applicationInfo.flags;
    }
    if (((i & 0x1) != 0) || ((i & 0x80) != 0)) {
      return true;
    }
    if (!hasPermission(paramPackageInfo, "android.permission.CONNECTIVITY_INTERNAL")) {
      bool = hasPermission(paramPackageInfo, "android.permission.CONNECTIVITY_USE_RESTRICTED_NETWORKS");
    }
    return bool;
  }
  
  private static void log(String paramString) {}
  
  private static void loge(String paramString)
  {
    Log.e("PermissionMonitor", paramString);
  }
  
  private void onAppAdded(String paramString, int paramInt)
  {
    try
    {
      if ((TextUtils.isEmpty(paramString)) || (paramInt < 0))
      {
        loge("Invalid app in onAppAdded: " + paramString + " | " + paramInt);
        return;
      }
      try
      {
        paramString = this.mPackageManager.getPackageInfo(paramString, 4096);
        boolean bool1 = hasNetworkPermission(paramString);
        boolean bool2 = hasRestrictedNetworkPermission(paramString);
        if ((bool1) || (bool2))
        {
          paramString = (Boolean)this.mApps.get(Integer.valueOf(paramInt));
          if ((paramString == null) || (!paramString.booleanValue()))
          {
            this.mApps.put(Integer.valueOf(paramInt), Boolean.valueOf(bool2));
            paramString = new HashMap();
            paramString.put(Integer.valueOf(paramInt), Boolean.valueOf(bool2));
            update(this.mUsers, paramString, true);
          }
        }
      }
      catch (PackageManager.NameNotFoundException paramString)
      {
        for (;;)
        {
          loge("NameNotFoundException in onAppAdded: " + paramString);
        }
      }
      return;
    }
    finally {}
  }
  
  private void onAppRemoved(int paramInt)
  {
    if (paramInt < 0) {}
    try
    {
      loge("Invalid app in onAppRemoved: " + paramInt);
      return;
    }
    finally {}
    this.mApps.remove(Integer.valueOf(paramInt));
    HashMap localHashMap = new HashMap();
    localHashMap.put(Integer.valueOf(paramInt), Boolean.valueOf(false));
    update(this.mUsers, localHashMap, false);
  }
  
  private void onUserAdded(int paramInt)
  {
    if (paramInt < 0) {}
    try
    {
      loge("Invalid user in onUserAdded: " + paramInt);
      return;
    }
    finally {}
    this.mUsers.add(Integer.valueOf(paramInt));
    HashSet localHashSet = new HashSet();
    localHashSet.add(Integer.valueOf(paramInt));
    update(localHashSet, this.mApps, true);
  }
  
  private void onUserRemoved(int paramInt)
  {
    if (paramInt < 0) {}
    try
    {
      loge("Invalid user in onUserRemoved: " + paramInt);
      return;
    }
    finally {}
    this.mUsers.remove(Integer.valueOf(paramInt));
    HashSet localHashSet = new HashSet();
    localHashSet.add(Integer.valueOf(paramInt));
    update(localHashSet, this.mApps, false);
  }
  
  private int[] toIntArray(List<Integer> paramList)
  {
    int[] arrayOfInt = new int[paramList.size()];
    int i = 0;
    while (i < paramList.size())
    {
      arrayOfInt[i] = ((Integer)paramList.get(i)).intValue();
      i += 1;
    }
    return arrayOfInt;
  }
  
  private void update(Set<Integer> paramSet, Map<Integer, Boolean> paramMap, boolean paramBoolean)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Iterator localIterator1 = paramMap.entrySet().iterator();
    if (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      if (((Boolean)localEntry.getValue()).booleanValue()) {}
      for (paramMap = localArrayList2;; paramMap = localArrayList1)
      {
        Iterator localIterator2 = paramSet.iterator();
        while (localIterator2.hasNext()) {
          paramMap.add(Integer.valueOf(UserHandle.getUid(((Integer)localIterator2.next()).intValue(), ((Integer)localEntry.getKey()).intValue())));
        }
        break;
      }
    }
    if (paramBoolean) {}
    try
    {
      this.mNetd.setPermission("NETWORK", toIntArray(localArrayList1));
      this.mNetd.setPermission("SYSTEM", toIntArray(localArrayList2));
      return;
    }
    catch (RemoteException paramSet)
    {
      loge("Exception when updating permissions: " + paramSet);
    }
    this.mNetd.clearPermission(toIntArray(localArrayList1));
    this.mNetd.clearPermission(toIntArray(localArrayList2));
    return;
  }
  
  public void startMonitoring()
  {
    Object localObject4;
    for (;;)
    {
      try
      {
        log("Monitoring");
        Object localObject1 = new IntentFilter();
        ((IntentFilter)localObject1).addAction("android.intent.action.USER_ADDED");
        ((IntentFilter)localObject1).addAction("android.intent.action.USER_REMOVED");
        this.mContext.registerReceiverAsUser(this.mIntentReceiver, UserHandle.ALL, (IntentFilter)localObject1, null, null);
        localObject1 = new IntentFilter();
        ((IntentFilter)localObject1).addAction("android.intent.action.PACKAGE_ADDED");
        ((IntentFilter)localObject1).addAction("android.intent.action.PACKAGE_REMOVED");
        ((IntentFilter)localObject1).addDataScheme("package");
        this.mContext.registerReceiverAsUser(this.mIntentReceiver, UserHandle.ALL, (IntentFilter)localObject1, null, null);
        localObject1 = this.mPackageManager.getInstalledPackages(4096);
        if (localObject1 == null)
        {
          loge("No apps");
          return;
        }
        localObject1 = ((Iterable)localObject1).iterator();
        if (!((Iterator)localObject1).hasNext()) {
          break;
        }
        localObject4 = (PackageInfo)((Iterator)localObject1).next();
        int i;
        if (((PackageInfo)localObject4).applicationInfo != null)
        {
          i = ((PackageInfo)localObject4).applicationInfo.uid;
          if (i >= 0)
          {
            boolean bool1 = hasNetworkPermission((PackageInfo)localObject4);
            boolean bool2 = hasRestrictedNetworkPermission((PackageInfo)localObject4);
            if ((bool1) || (bool2))
            {
              localObject4 = (Boolean)this.mApps.get(Integer.valueOf(i));
              if ((localObject4 == null) || (!((Boolean)localObject4).booleanValue())) {
                this.mApps.put(Integer.valueOf(i), Boolean.valueOf(bool2));
              }
            }
          }
        }
        else
        {
          i = -1;
        }
      }
      finally {}
    }
    Object localObject3 = this.mUserManager.getUsers(true);
    if (localObject3 != null)
    {
      localObject3 = ((Iterable)localObject3).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (UserInfo)((Iterator)localObject3).next();
        this.mUsers.add(Integer.valueOf(((UserInfo)localObject4).id));
      }
    }
    log("Users: " + this.mUsers.size() + ", Apps: " + this.mApps.size());
    update(this.mUsers, this.mApps, true);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/PermissionMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */