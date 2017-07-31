package com.android.server.pm;

import android.app.AppGlobals;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageParser.Package;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.system.ErrnoException;
import android.util.ArraySet;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import libcore.io.Libcore;
import libcore.io.Os;

public class PackageManagerServiceUtils
{
  private static final long SEVEN_DAYS_IN_MILLISECONDS = 604800000L;
  
  private static void applyPackageFilter(Predicate<PackageParser.Package> paramPredicate, Collection<PackageParser.Package> paramCollection1, Collection<PackageParser.Package> paramCollection2, List<PackageParser.Package> paramList, PackageManagerService paramPackageManagerService)
  {
    Object localObject = paramCollection2.iterator();
    while (((Iterator)localObject).hasNext())
    {
      PackageParser.Package localPackage = (PackageParser.Package)((Iterator)localObject).next();
      if (paramPredicate.test(localPackage)) {
        paramList.add(localPackage);
      }
    }
    sortPackagesByUsageDate(paramList, paramPackageManagerService);
    paramCollection2.removeAll(paramList);
    paramPredicate = paramList.iterator();
    while (paramPredicate.hasNext())
    {
      localObject = (PackageParser.Package)paramPredicate.next();
      paramCollection1.add(localObject);
      localObject = paramPackageManagerService.findSharedNonSystemLibraries((PackageParser.Package)localObject);
      if (!((Collection)localObject).isEmpty())
      {
        ((Collection)localObject).removeAll(paramCollection1);
        paramCollection1.addAll((Collection)localObject);
        paramCollection2.removeAll((Collection)localObject);
      }
    }
    paramList.clear();
  }
  
  private static ArraySet<String> getPackageNamesForIntent(Intent paramIntent, int paramInt)
  {
    ArraySet localArraySet = null;
    try
    {
      paramIntent = AppGlobals.getPackageManager().queryIntentReceivers(paramIntent, null, 0, paramInt).getList();
      localArraySet = new ArraySet();
      if (paramIntent != null)
      {
        paramIntent = paramIntent.iterator();
        while (paramIntent.hasNext()) {
          localArraySet.add(((ResolveInfo)paramIntent.next()).activityInfo.packageName);
        }
      }
      return localArraySet;
    }
    catch (RemoteException paramIntent)
    {
      for (;;)
      {
        paramIntent = localArraySet;
      }
    }
  }
  
  public static List<PackageParser.Package> getPackagesForDexopt(Collection<PackageParser.Package> paramCollection, PackageManagerService paramPackageManagerService)
  {
    ArrayList localArrayList1 = new ArrayList(paramCollection);
    LinkedList localLinkedList = new LinkedList();
    ArrayList localArrayList2 = new ArrayList(localArrayList1.size());
    applyPackageFilter(new -java_util_List_getPackagesForDexopt_java_util_Collection_packages_com_android_server_pm_PackageManagerService_packageManagerService_LambdaImpl0(), localLinkedList, localArrayList1, localArrayList2, paramPackageManagerService);
    applyPackageFilter(new -java_util_List_getPackagesForDexopt_java_util_Collection_packages_com_android_server_pm_PackageManagerService_packageManagerService_LambdaImpl1(getPackageNamesForIntent(new Intent("android.intent.action.PRE_BOOT_COMPLETED"), 0)), localLinkedList, localArrayList1, localArrayList2, paramPackageManagerService);
    applyPackageFilter(new -java_util_List_getPackagesForDexopt_java_util_Collection_packages_com_android_server_pm_PackageManagerService_packageManagerService_LambdaImpl2(), localLinkedList, localArrayList1, localArrayList2, paramPackageManagerService);
    if ((!localArrayList1.isEmpty()) && (paramPackageManagerService.isHistoricalPackageUsageAvailable()))
    {
      if (PackageManagerService.DEBUG_DEXOPT) {
        Log.i("PackageManager", "Looking at historical package use");
      }
      paramCollection = (PackageParser.Package)Collections.max(localArrayList1, new -java_util_List_getPackagesForDexopt_java_util_Collection_packages_com_android_server_pm_PackageManagerService_packageManagerService_LambdaImpl3());
      if (PackageManagerService.DEBUG_DEXOPT) {
        Log.i("PackageManager", "Taking package " + paramCollection.packageName + " as reference in time use");
      }
      long l = paramCollection.getLatestForegroundPackageUseTimeInMills();
      if (l != 0L)
      {
        paramCollection = new -java_util_List_getPackagesForDexopt_java_util_Collection_packages_com_android_server_pm_PackageManagerService_packageManagerService_LambdaImpl4(l - 604800000L);
        sortPackagesByUsageDate(localArrayList1, paramPackageManagerService);
      }
    }
    for (;;)
    {
      applyPackageFilter(paramCollection, localLinkedList, localArrayList1, localArrayList2, paramPackageManagerService);
      if (PackageManagerService.DEBUG_DEXOPT)
      {
        Log.i("PackageManager", "Packages to be dexopted: " + packagesToString(localLinkedList));
        Log.i("PackageManager", "Packages skipped from dexopt: " + packagesToString(localArrayList1));
      }
      return localLinkedList;
      paramCollection = new -java_util_List_getPackagesForDexopt_java_util_Collection_packages_com_android_server_pm_PackageManagerService_packageManagerService_LambdaImpl5();
      break;
      paramCollection = new -java_util_List_getPackagesForDexopt_java_util_Collection_packages_com_android_server_pm_PackageManagerService_packageManagerService_LambdaImpl6();
    }
  }
  
  public static String packagesToString(Collection<PackageParser.Package> paramCollection)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      PackageParser.Package localPackage = (PackageParser.Package)paramCollection.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append(localPackage.packageName);
    }
    return localStringBuilder.toString();
  }
  
  public static String realpath(File paramFile)
    throws IOException
  {
    try
    {
      paramFile = Libcore.os.realpath(paramFile.getAbsolutePath());
      return paramFile;
    }
    catch (ErrnoException paramFile)
    {
      throw paramFile.rethrowAsIOException();
    }
  }
  
  public static void sortPackagesByUsageDate(List<PackageParser.Package> paramList, PackageManagerService paramPackageManagerService)
  {
    if (!paramPackageManagerService.isHistoricalPackageUsageAvailable()) {
      return;
    }
    Collections.sort(paramList, new -void_sortPackagesByUsageDate_java_util_List_pkgs_com_android_server_pm_PackageManagerService_packageManagerService_LambdaImpl0());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageManagerServiceUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */