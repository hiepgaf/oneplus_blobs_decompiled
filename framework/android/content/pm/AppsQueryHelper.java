package android.content.pm;

import android.app.AppGlobals;
import android.content.Intent;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArraySet;
import java.util.ArrayList;
import java.util.List;

public class AppsQueryHelper
{
  public static int GET_APPS_WITH_INTERACT_ACROSS_USERS_PERM = 2;
  public static int GET_IMES = 4;
  public static int GET_NON_LAUNCHABLE_APPS = 1;
  public static int GET_REQUIRED_FOR_SYSTEM_USER = 8;
  private List<ApplicationInfo> mAllApps;
  private final IPackageManager mPackageManager;
  
  public AppsQueryHelper()
  {
    this(AppGlobals.getPackageManager());
  }
  
  public AppsQueryHelper(IPackageManager paramIPackageManager)
  {
    this.mPackageManager = paramIPackageManager;
  }
  
  protected List<ApplicationInfo> getAllApps(int paramInt)
  {
    try
    {
      List localList = this.mPackageManager.getInstalledApplications(8704, paramInt).getList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  protected List<PackageInfo> getPackagesHoldingPermission(String paramString, int paramInt)
  {
    try
    {
      paramString = this.mPackageManager.getPackagesHoldingPermissions(new String[] { paramString }, 0, paramInt).getList();
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public List<String> queryApps(int paramInt, boolean paramBoolean, UserHandle paramUserHandle)
  {
    int m;
    int k;
    label22:
    int j;
    if ((GET_NON_LAUNCHABLE_APPS & paramInt) > 0)
    {
      m = 1;
      if ((GET_APPS_WITH_INTERACT_ACROSS_USERS_PERM & paramInt) <= 0) {
        break label145;
      }
      k = 1;
      if ((GET_IMES & paramInt) <= 0) {
        break label151;
      }
      j = 1;
      label33:
      if ((GET_REQUIRED_FOR_SYSTEM_USER & paramInt) <= 0) {
        break label157;
      }
    }
    ArrayList localArrayList;
    label145:
    label151:
    label157:
    for (int i = 1;; i = 0)
    {
      if (this.mAllApps == null) {
        this.mAllApps = getAllApps(paramUserHandle.getIdentifier());
      }
      localArrayList = new ArrayList();
      if (paramInt != 0) {
        break label166;
      }
      i = this.mAllApps.size();
      paramInt = 0;
      while (paramInt < i)
      {
        paramUserHandle = (ApplicationInfo)this.mAllApps.get(paramInt);
        if ((!paramBoolean) || (paramUserHandle.isSystemApp())) {
          localArrayList.add(paramUserHandle.packageName);
        }
        paramInt += 1;
      }
      m = 0;
      break;
      k = 0;
      break label22;
      j = 0;
      break label33;
    }
    return localArrayList;
    label166:
    Object localObject2;
    Object localObject1;
    if (m != 0)
    {
      localObject2 = queryIntentActivitiesAsUser(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER"), paramUserHandle.getIdentifier());
      localObject1 = new ArraySet();
      m = ((List)localObject2).size();
      paramInt = 0;
      while (paramInt < m)
      {
        ((ArraySet)localObject1).add(((ResolveInfo)((List)localObject2).get(paramInt)).activityInfo.packageName);
        paramInt += 1;
      }
      m = this.mAllApps.size();
      paramInt = 0;
      while (paramInt < m)
      {
        localObject2 = (ApplicationInfo)this.mAllApps.get(paramInt);
        if ((!paramBoolean) || (((ApplicationInfo)localObject2).isSystemApp()))
        {
          localObject2 = ((PackageItemInfo)localObject2).packageName;
          if (!((ArraySet)localObject1).contains(localObject2)) {
            localArrayList.add(localObject2);
          }
        }
        paramInt += 1;
      }
    }
    if (k != 0)
    {
      localObject1 = getPackagesHoldingPermission("android.permission.INTERACT_ACROSS_USERS", paramUserHandle.getIdentifier());
      k = ((List)localObject1).size();
      paramInt = 0;
      while (paramInt < k)
      {
        localObject2 = (PackageInfo)((List)localObject1).get(paramInt);
        if (((!paramBoolean) || (((PackageInfo)localObject2).applicationInfo.isSystemApp())) && (!localArrayList.contains(((PackageInfo)localObject2).packageName))) {
          localArrayList.add(((PackageInfo)localObject2).packageName);
        }
        paramInt += 1;
      }
    }
    if (j != 0)
    {
      paramUserHandle = queryIntentServicesAsUser(new Intent("android.view.InputMethod"), paramUserHandle.getIdentifier());
      j = paramUserHandle.size();
      paramInt = 0;
      while (paramInt < j)
      {
        localObject1 = ((ResolveInfo)paramUserHandle.get(paramInt)).serviceInfo;
        if (((!paramBoolean) || (((ComponentInfo)localObject1).applicationInfo.isSystemApp())) && (!localArrayList.contains(((PackageItemInfo)localObject1).packageName))) {
          localArrayList.add(((PackageItemInfo)localObject1).packageName);
        }
        paramInt += 1;
      }
    }
    if (i != 0)
    {
      i = this.mAllApps.size();
      paramInt = 0;
      while (paramInt < i)
      {
        paramUserHandle = (ApplicationInfo)this.mAllApps.get(paramInt);
        if (((!paramBoolean) || (paramUserHandle.isSystemApp())) && (paramUserHandle.isRequiredForSystemUser())) {
          localArrayList.add(paramUserHandle.packageName);
        }
        paramInt += 1;
      }
    }
    return localArrayList;
  }
  
  protected List<ResolveInfo> queryIntentActivitiesAsUser(Intent paramIntent, int paramInt)
  {
    try
    {
      paramIntent = this.mPackageManager.queryIntentActivities(paramIntent, null, 8704, paramInt).getList();
      return paramIntent;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  protected List<ResolveInfo> queryIntentServicesAsUser(Intent paramIntent, int paramInt)
  {
    try
    {
      paramIntent = this.mPackageManager.queryIntentServices(paramIntent, null, 32896, paramInt).getList();
      return paramIntent;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/AppsQueryHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */