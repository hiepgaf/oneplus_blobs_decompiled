package android.content.pm;

import android.content.ComponentName;
import android.util.SparseArray;
import java.util.List;

public abstract class PackageManagerInternal
{
  public abstract ApplicationInfo getApplicationInfo(String paramString, int paramInt);
  
  public abstract ComponentName getHomeActivitiesAsUser(List<ResolveInfo> paramList, int paramInt);
  
  public abstract void grantDefaultPermissionsToDefaultDialerApp(String paramString, int paramInt);
  
  public abstract void grantDefaultPermissionsToDefaultSimCallManager(String paramString, int paramInt);
  
  public abstract void grantDefaultPermissionsToDefaultSmsApp(String paramString, int paramInt);
  
  public abstract boolean isPackageDataProtected(int paramInt, String paramString);
  
  public abstract boolean isPermissionsReviewRequired(String paramString, int paramInt);
  
  public abstract void setDeviceAndProfileOwnerPackages(int paramInt, String paramString, SparseArray<String> paramSparseArray);
  
  public abstract void setDialerAppPackagesProvider(PackagesProvider paramPackagesProvider);
  
  public abstract void setKeepUninstalledPackages(List<String> paramList);
  
  public abstract void setLocationPackagesProvider(PackagesProvider paramPackagesProvider);
  
  public abstract void setSimCallManagerPackagesProvider(PackagesProvider paramPackagesProvider);
  
  public abstract void setSmsAppPackagesProvider(PackagesProvider paramPackagesProvider);
  
  public abstract void setSyncAdapterPackagesprovider(SyncAdapterPackagesProvider paramSyncAdapterPackagesProvider);
  
  public abstract void setVoiceInteractionPackagesProvider(PackagesProvider paramPackagesProvider);
  
  public abstract boolean wasPackageEverLaunched(String paramString, int paramInt);
  
  public static abstract interface PackagesProvider
  {
    public abstract String[] getPackages(int paramInt);
  }
  
  public static abstract interface SyncAdapterPackagesProvider
  {
    public abstract String[] getPackages(String paramString, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PackageManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */