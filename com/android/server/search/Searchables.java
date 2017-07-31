package com.android.server.search;

import android.app.AppGlobals;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Searchables
{
  public static String ENHANCED_GOOGLE_SEARCH_COMPONENT_NAME = "com.google.android.providers.enhancedgooglesearch/.Launcher";
  private static final Comparator<ResolveInfo> GLOBAL_SEARCH_RANKER = new Comparator()
  {
    public int compare(ResolveInfo paramAnonymousResolveInfo1, ResolveInfo paramAnonymousResolveInfo2)
    {
      if (paramAnonymousResolveInfo1 == paramAnonymousResolveInfo2) {
        return 0;
      }
      boolean bool1 = Searchables.-wrap0(paramAnonymousResolveInfo1);
      boolean bool2 = Searchables.-wrap0(paramAnonymousResolveInfo2);
      if ((!bool1) || (bool2))
      {
        if ((!bool2) || (bool1)) {
          return paramAnonymousResolveInfo2.priority - paramAnonymousResolveInfo1.priority;
        }
      }
      else {
        return -1;
      }
      return 1;
    }
  };
  public static String GOOGLE_SEARCH_COMPONENT_NAME = "com.android.googlesearch/.GoogleSearch";
  private static final String LOG_TAG = "Searchables";
  private static final String MD_LABEL_DEFAULT_SEARCHABLE = "android.app.default_searchable";
  private static final String MD_SEARCHABLE_SYSTEM_SEARCH = "*";
  private Context mContext;
  private ComponentName mCurrentGlobalSearchActivity = null;
  private List<ResolveInfo> mGlobalSearchActivities;
  private final IPackageManager mPm;
  private ArrayList<SearchableInfo> mSearchablesInGlobalSearchList = null;
  private ArrayList<SearchableInfo> mSearchablesList = null;
  private HashMap<ComponentName, SearchableInfo> mSearchablesMap = null;
  private int mUserId;
  private ComponentName mWebSearchActivity = null;
  
  public Searchables(Context paramContext, int paramInt)
  {
    this.mContext = paramContext;
    this.mUserId = paramInt;
    this.mPm = AppGlobals.getPackageManager();
  }
  
  private List<ResolveInfo> findGlobalSearchActivities()
  {
    List localList = queryIntentActivities(new Intent("android.search.action.GLOBAL_SEARCH"), 268500992);
    if ((localList == null) || (localList.isEmpty())) {
      return localList;
    }
    Collections.sort(localList, GLOBAL_SEARCH_RANKER);
    return localList;
  }
  
  private ComponentName findGlobalSearchActivity(List<ResolveInfo> paramList)
  {
    Object localObject = getGlobalSearchProviderSetting();
    if (!TextUtils.isEmpty((CharSequence)localObject))
    {
      localObject = ComponentName.unflattenFromString((String)localObject);
      if ((localObject != null) && (isInstalled((ComponentName)localObject))) {
        return (ComponentName)localObject;
      }
    }
    return getDefaultGlobalSearchProvider(paramList);
  }
  
  private ComponentName findWebSearchActivity(ComponentName paramComponentName)
  {
    if (paramComponentName == null) {
      return null;
    }
    Intent localIntent = new Intent("android.intent.action.WEB_SEARCH");
    localIntent.setPackage(paramComponentName.getPackageName());
    paramComponentName = queryIntentActivities(localIntent, 65536);
    if ((paramComponentName == null) || (paramComponentName.isEmpty()))
    {
      Log.w("Searchables", "No web search activity found");
      return null;
    }
    paramComponentName = ((ResolveInfo)paramComponentName.get(0)).activityInfo;
    return new ComponentName(paramComponentName.packageName, paramComponentName.name);
  }
  
  private ComponentName getDefaultGlobalSearchProvider(List<ResolveInfo> paramList)
  {
    if ((paramList == null) || (paramList.isEmpty()))
    {
      Log.w("Searchables", "No global search activity found");
      return null;
    }
    paramList = ((ResolveInfo)paramList.get(0)).activityInfo;
    return new ComponentName(paramList.packageName, paramList.name);
  }
  
  private String getGlobalSearchProviderSetting()
  {
    return Settings.Secure.getString(this.mContext.getContentResolver(), "search_global_search_activity");
  }
  
  private boolean isInstalled(ComponentName paramComponentName)
  {
    Intent localIntent = new Intent("android.search.action.GLOBAL_SEARCH");
    localIntent.setComponent(paramComponentName);
    paramComponentName = queryIntentActivities(localIntent, 65536);
    return (paramComponentName != null) && (!paramComponentName.isEmpty());
  }
  
  private static final boolean isSystemApp(ResolveInfo paramResolveInfo)
  {
    boolean bool = false;
    if ((paramResolveInfo.activityInfo.applicationInfo.flags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private List<ResolveInfo> queryIntentActivities(Intent paramIntent, int paramInt)
  {
    try
    {
      paramIntent = this.mPm.queryIntentActivities(paramIntent, paramIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), paramInt, this.mUserId).getList();
      return paramIntent;
    }
    catch (RemoteException paramIntent) {}
    return null;
  }
  
  void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("Searchable authorities:");
    try
    {
      if (this.mSearchablesList != null)
      {
        paramFileDescriptor = this.mSearchablesList.iterator();
        while (paramFileDescriptor.hasNext())
        {
          paramArrayOfString = (SearchableInfo)paramFileDescriptor.next();
          paramPrintWriter.print("  ");
          paramPrintWriter.println(paramArrayOfString.getSuggestAuthority());
        }
      }
    }
    finally {}
  }
  
  public ArrayList<ResolveInfo> getGlobalSearchActivities()
  {
    try
    {
      ArrayList localArrayList = new ArrayList(this.mGlobalSearchActivities);
      return localArrayList;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public ComponentName getGlobalSearchActivity()
  {
    try
    {
      ComponentName localComponentName = this.mCurrentGlobalSearchActivity;
      return localComponentName;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public SearchableInfo getSearchableInfo(ComponentName paramComponentName)
  {
    Object localObject1;
    Object localObject2;
    try
    {
      localObject1 = (SearchableInfo)this.mSearchablesMap.get(paramComponentName);
      if (localObject1 != null) {
        return (SearchableInfo)localObject1;
      }
      Object localObject3;
      localObject1 = paramComponentName.getPackageName();
    }
    finally
    {
      try
      {
        localObject3 = this.mPm.getActivityInfo(paramComponentName, 128, this.mUserId);
        localObject1 = null;
        localObject2 = ((ActivityInfo)localObject3).metaData;
        if (localObject2 != null) {
          localObject1 = ((Bundle)localObject2).getString("android.app.default_searchable");
        }
        localObject2 = localObject1;
        if (localObject1 == null)
        {
          localObject3 = ((ActivityInfo)localObject3).applicationInfo.metaData;
          localObject2 = localObject1;
          if (localObject3 != null) {
            localObject2 = ((Bundle)localObject3).getString("android.app.default_searchable");
          }
        }
        if (localObject2 == null) {
          break label231;
        }
        if (!((String)localObject2).equals("*")) {
          break label142;
        }
        return null;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("Searchables", "Error getting activity info " + paramComponentName);
        return null;
      }
      paramComponentName = finally;
    }
    label142:
    if (((String)localObject2).charAt(0) == '.') {
      localObject1 = new ComponentName((String)localObject1, (String)localObject1 + (String)localObject2);
    }
    for (;;)
    {
      try
      {
        localObject1 = (SearchableInfo)this.mSearchablesMap.get(localObject1);
        if (localObject1 != null)
        {
          this.mSearchablesMap.put(paramComponentName, localObject1);
          return (SearchableInfo)localObject1;
          localObject1 = new ComponentName((String)localObject1, (String)localObject2);
          continue;
        }
        label231:
        return null;
      }
      finally {}
    }
  }
  
  public ArrayList<SearchableInfo> getSearchablesInGlobalSearchList()
  {
    try
    {
      ArrayList localArrayList = new ArrayList(this.mSearchablesInGlobalSearchList);
      return localArrayList;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public ArrayList<SearchableInfo> getSearchablesList()
  {
    try
    {
      ArrayList localArrayList = new ArrayList(this.mSearchablesList);
      return localArrayList;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public ComponentName getWebSearchActivity()
  {
    try
    {
      ComponentName localComponentName = this.mWebSearchActivity;
      return localComponentName;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void updateSearchableList()
  {
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Object localObject1 = new Intent("android.intent.action.SEARCH");
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      List localList2;
      int k;
      int i;
      int j;
      try
      {
        localObject5 = queryIntentActivities((Intent)localObject1, 268435584);
        localList2 = queryIntentActivities(new Intent("android.intent.action.WEB_SEARCH"), 268435584);
        if (localObject5 != null) {
          break label358;
        }
        if (localList2 == null) {
          break label283;
        }
      }
      finally
      {
        Object localObject4;
        Binder.restoreCallingIdentity(l);
      }
      if (k < i + j)
      {
        if (k < i)
        {
          localObject1 = (ResolveInfo)((List)localObject5).get(k);
          localObject4 = ((ResolveInfo)localObject1).activityInfo;
          localObject1 = localHashMap.get(new ComponentName(((ActivityInfo)localObject4).packageName, ((ActivityInfo)localObject4).name));
          if (localObject1 != null) {
            break label377;
          }
          localObject1 = null;
        }
        try
        {
          localObject4 = SearchableInfo.getActivityMetaData(this.mContext, (ActivityInfo)localObject4, this.mUserId);
          localObject1 = localObject4;
        }
        catch (RuntimeException localRuntimeException)
        {
          Log.e("Searchables", "RuntimeException : cannot get searchable, may CAUSE system_servce crashed !!!");
          localRuntimeException.printStackTrace();
          continue;
        }
        if (localObject1 == null) {
          break label377;
        }
        localArrayList1.add(localObject1);
        localHashMap.put(((SearchableInfo)localObject1).getSearchActivity(), localObject1);
        if (!((SearchableInfo)localObject1).shouldIncludeInGlobalSearch()) {
          break label377;
        }
        localArrayList2.add(localObject1);
        break label377;
        i = ((List)localObject5).size();
        break label365;
        j = localList2.size();
        break label372;
        localObject1 = (ResolveInfo)localList2.get(k - i);
        continue;
      }
      label283:
      List localList1 = findGlobalSearchActivities();
      ComponentName localComponentName = findGlobalSearchActivity(localList1);
      Object localObject5 = findWebSearchActivity(localComponentName);
      try
      {
        this.mSearchablesMap = localHashMap;
        this.mSearchablesList = localArrayList1;
        this.mSearchablesInGlobalSearchList = localArrayList2;
        this.mGlobalSearchActivities = localList1;
        this.mCurrentGlobalSearchActivity = localComponentName;
        this.mWebSearchActivity = ((ComponentName)localObject5);
        Binder.restoreCallingIdentity(l);
        return;
      }
      finally {}
      label358:
      if (localObject5 == null)
      {
        i = 0;
        label365:
        if (localList2 == null)
        {
          j = 0;
          label372:
          k = 0;
          continue;
          label377:
          k += 1;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/search/Searchables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */