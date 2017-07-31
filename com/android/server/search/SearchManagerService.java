package com.android.server.search;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.ISearchManager.Stub;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class SearchManagerService
  extends ISearchManager.Stub
{
  private static final String TAG = "SearchManagerService";
  private final Context mContext;
  final Handler mHandler;
  @GuardedBy("mSearchables")
  private final SparseArray<Searchables> mSearchables = new SparseArray();
  
  public SearchManagerService(Context paramContext)
  {
    this.mContext = paramContext;
    new MyPackageMonitor().register(paramContext, null, UserHandle.ALL, true);
    new GlobalSearchProviderObserver(paramContext.getContentResolver());
    this.mHandler = BackgroundThread.getHandler();
  }
  
  private ComponentName getLegacyAssistComponent(int paramInt)
  {
    try
    {
      paramInt = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), paramInt, true, false, "getLegacyAssistComponent", null);
      Object localObject = AppGlobals.getPackageManager();
      Intent localIntent = new Intent("android.intent.action.ASSIST");
      localObject = ((IPackageManager)localObject).resolveIntent(localIntent, localIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 65536, paramInt);
      if (localObject != null)
      {
        localObject = new ComponentName(((ResolveInfo)localObject).activityInfo.applicationInfo.packageName, ((ResolveInfo)localObject).activityInfo.name);
        return (ComponentName)localObject;
      }
    }
    catch (Exception localException)
    {
      Log.e("SearchManagerService", "Exception in getLegacyAssistComponent: " + localException);
      return null;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SearchManagerService", "RemoteException in getLegacyAssistComponent: " + localRemoteException);
    }
    return null;
  }
  
  private Searchables getSearchables(int paramInt)
  {
    return getSearchables(paramInt, false);
  }
  
  private Searchables getSearchables(int paramInt, boolean paramBoolean)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      UserManager localUserManager = (UserManager)this.mContext.getSystemService(UserManager.class);
      if (localUserManager.getUserInfo(paramInt) == null) {
        throw new IllegalStateException("User " + paramInt + " doesn't exist");
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    if (!((UserManager)localObject1).isUserUnlockingOrUnlocked(paramInt)) {
      throw new IllegalStateException("User " + paramInt + " isn't unlocked");
    }
    Binder.restoreCallingIdentity(l);
    synchronized (this.mSearchables)
    {
      Searchables localSearchables = (Searchables)this.mSearchables.get(paramInt);
      if (localSearchables == null)
      {
        localObject2 = new Searchables(this.mContext, paramInt);
        ((Searchables)localObject2).updateSearchableList();
        this.mSearchables.append(paramInt, localObject2);
      }
      do
      {
        return (Searchables)localObject2;
        localObject2 = localSearchables;
      } while (!paramBoolean);
      localSearchables.updateSearchableList();
      Object localObject2 = localSearchables;
    }
  }
  
  private void onCleanupUser(int paramInt)
  {
    synchronized (this.mSearchables)
    {
      this.mSearchables.remove(paramInt);
      return;
    }
  }
  
  private void onUnlockUser(int paramInt)
  {
    try
    {
      getSearchables(paramInt, true);
      return;
    }
    catch (IllegalStateException localIllegalStateException) {}
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "SearchManagerService");
    IndentingPrintWriter localIndentingPrintWriter = new IndentingPrintWriter(paramPrintWriter, "  ");
    paramPrintWriter = this.mSearchables;
    int i = 0;
    try
    {
      while (i < this.mSearchables.size())
      {
        localIndentingPrintWriter.print("\nUser: ");
        localIndentingPrintWriter.println(this.mSearchables.keyAt(i));
        localIndentingPrintWriter.increaseIndent();
        ((Searchables)this.mSearchables.valueAt(i)).dump(paramFileDescriptor, localIndentingPrintWriter, paramArrayOfString);
        localIndentingPrintWriter.decreaseIndent();
        i += 1;
      }
      return;
    }
    finally
    {
      paramFileDescriptor = finally;
      throw paramFileDescriptor;
    }
  }
  
  public List<ResolveInfo> getGlobalSearchActivities()
  {
    return getSearchables(UserHandle.getCallingUserId()).getGlobalSearchActivities();
  }
  
  public ComponentName getGlobalSearchActivity()
  {
    return getSearchables(UserHandle.getCallingUserId()).getGlobalSearchActivity();
  }
  
  public SearchableInfo getSearchableInfo(ComponentName paramComponentName)
  {
    if (paramComponentName == null)
    {
      Log.e("SearchManagerService", "getSearchableInfo(), activity == null");
      return null;
    }
    return getSearchables(UserHandle.getCallingUserId()).getSearchableInfo(paramComponentName);
  }
  
  public List<SearchableInfo> getSearchablesInGlobalSearch()
  {
    return getSearchables(UserHandle.getCallingUserId()).getSearchablesInGlobalSearchList();
  }
  
  public ComponentName getWebSearchActivity()
  {
    return getSearchables(UserHandle.getCallingUserId()).getWebSearchActivity();
  }
  
  public void launchAssist(Bundle paramBundle)
  {
    StatusBarManagerInternal localStatusBarManagerInternal = (StatusBarManagerInternal)LocalServices.getService(StatusBarManagerInternal.class);
    if (localStatusBarManagerInternal != null) {
      localStatusBarManagerInternal.startAssist(paramBundle);
    }
  }
  
  public boolean launchLegacyAssist(String paramString, int paramInt, Bundle paramBundle)
  {
    ComponentName localComponentName = getLegacyAssistComponent(paramInt);
    if (localComponentName == null) {
      return false;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      Intent localIntent = new Intent("android.intent.action.ASSIST");
      localIntent.setComponent(localComponentName);
      boolean bool = ActivityManagerNative.getDefault().launchAssistIntent(localIntent, 0, paramString, paramInt, paramBundle);
      Binder.restoreCallingIdentity(l);
      return bool;
    }
    catch (RemoteException paramString)
    {
      paramString = paramString;
      Binder.restoreCallingIdentity(l);
      return true;
    }
    finally
    {
      paramString = finally;
      Binder.restoreCallingIdentity(l);
      throw paramString;
    }
  }
  
  class GlobalSearchProviderObserver
    extends ContentObserver
  {
    private final ContentResolver mResolver;
    
    public GlobalSearchProviderObserver(ContentResolver paramContentResolver)
    {
      super();
      this.mResolver = paramContentResolver;
      this.mResolver.registerContentObserver(Settings.Secure.getUriFor("search_global_search_activity"), false, this);
    }
    
    public void onChange(boolean paramBoolean)
    {
      Object localObject1 = SearchManagerService.-get1(SearchManagerService.this);
      int i = 0;
      try
      {
        while (i < SearchManagerService.-get1(SearchManagerService.this).size())
        {
          ((Searchables)SearchManagerService.-get1(SearchManagerService.this).valueAt(i)).updateSearchableList();
          i += 1;
        }
        localObject1 = new Intent("android.search.action.GLOBAL_SEARCH_ACTIVITY_CHANGED");
        ((Intent)localObject1).addFlags(536870912);
        SearchManagerService.-get0(SearchManagerService.this).sendBroadcastAsUser((Intent)localObject1, UserHandle.ALL);
        return;
      }
      finally {}
    }
  }
  
  public static class Lifecycle
    extends SystemService
  {
    private SearchManagerService mService;
    
    public Lifecycle(Context paramContext)
    {
      super();
    }
    
    public void onCleanupUser(int paramInt)
    {
      SearchManagerService.-wrap0(this.mService, paramInt);
    }
    
    public void onStart()
    {
      this.mService = new SearchManagerService(getContext());
      publishBinderService("search", this.mService);
    }
    
    public void onUnlockUser(final int paramInt)
    {
      this.mService.mHandler.post(new Runnable()
      {
        public void run()
        {
          SearchManagerService.-wrap1(SearchManagerService.this, paramInt);
        }
      });
    }
  }
  
  class MyPackageMonitor
    extends PackageMonitor
  {
    MyPackageMonitor() {}
    
    /* Error */
    private void updateSearchables()
    {
      // Byte code:
      //   0: aload_0
      //   1: invokevirtual 22	com/android/server/search/SearchManagerService$MyPackageMonitor:getChangingUserId	()I
      //   4: istore_2
      //   5: aload_0
      //   6: getfield 13	com/android/server/search/SearchManagerService$MyPackageMonitor:this$0	Lcom/android/server/search/SearchManagerService;
      //   9: invokestatic 26	com/android/server/search/SearchManagerService:-get1	(Lcom/android/server/search/SearchManagerService;)Landroid/util/SparseArray;
      //   12: astore_3
      //   13: aload_3
      //   14: monitorenter
      //   15: iconst_0
      //   16: istore_1
      //   17: iload_1
      //   18: aload_0
      //   19: getfield 13	com/android/server/search/SearchManagerService$MyPackageMonitor:this$0	Lcom/android/server/search/SearchManagerService;
      //   22: invokestatic 26	com/android/server/search/SearchManagerService:-get1	(Lcom/android/server/search/SearchManagerService;)Landroid/util/SparseArray;
      //   25: invokevirtual 31	android/util/SparseArray:size	()I
      //   28: if_icmpge +35 -> 63
      //   31: iload_2
      //   32: aload_0
      //   33: getfield 13	com/android/server/search/SearchManagerService$MyPackageMonitor:this$0	Lcom/android/server/search/SearchManagerService;
      //   36: invokestatic 26	com/android/server/search/SearchManagerService:-get1	(Lcom/android/server/search/SearchManagerService;)Landroid/util/SparseArray;
      //   39: iload_1
      //   40: invokevirtual 35	android/util/SparseArray:keyAt	(I)I
      //   43: if_icmpne +59 -> 102
      //   46: aload_0
      //   47: getfield 13	com/android/server/search/SearchManagerService$MyPackageMonitor:this$0	Lcom/android/server/search/SearchManagerService;
      //   50: invokestatic 26	com/android/server/search/SearchManagerService:-get1	(Lcom/android/server/search/SearchManagerService;)Landroid/util/SparseArray;
      //   53: iload_1
      //   54: invokevirtual 39	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
      //   57: checkcast 41	com/android/server/search/Searchables
      //   60: invokevirtual 44	com/android/server/search/Searchables:updateSearchableList	()V
      //   63: aload_3
      //   64: monitorexit
      //   65: new 46	android/content/Intent
      //   68: dup
      //   69: ldc 48
      //   71: invokespecial 51	android/content/Intent:<init>	(Ljava/lang/String;)V
      //   74: astore_3
      //   75: aload_3
      //   76: ldc 52
      //   78: invokevirtual 56	android/content/Intent:addFlags	(I)Landroid/content/Intent;
      //   81: pop
      //   82: aload_0
      //   83: getfield 13	com/android/server/search/SearchManagerService$MyPackageMonitor:this$0	Lcom/android/server/search/SearchManagerService;
      //   86: invokestatic 60	com/android/server/search/SearchManagerService:-get0	(Lcom/android/server/search/SearchManagerService;)Landroid/content/Context;
      //   89: aload_3
      //   90: new 62	android/os/UserHandle
      //   93: dup
      //   94: iload_2
      //   95: invokespecial 65	android/os/UserHandle:<init>	(I)V
      //   98: invokevirtual 71	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
      //   101: return
      //   102: iload_1
      //   103: iconst_1
      //   104: iadd
      //   105: istore_1
      //   106: goto -89 -> 17
      //   109: astore 4
      //   111: aload_3
      //   112: monitorexit
      //   113: aload 4
      //   115: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	116	0	this	MyPackageMonitor
      //   16	90	1	i	int
      //   4	91	2	j	int
      //   12	100	3	localObject1	Object
      //   109	5	4	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   17	63	109	finally
    }
    
    public void onPackageModified(String paramString)
    {
      updateSearchables();
    }
    
    public void onSomePackagesChanged()
    {
      updateSearchables();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/search/SearchManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */