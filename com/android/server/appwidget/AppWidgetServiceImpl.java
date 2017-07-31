package com.android.server.appwidget;

import android.app.AlarmManager;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManagerInternal;
import android.app.admin.DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener;
import android.appwidget.AppWidgetProviderInfo;
import android.appwidget.PendingHostUpdate;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.FilterComparison;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.LongSparseArray;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.Xml;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RemoteViews;
import com.android.internal.app.UnlaunchableAppActivity;
import com.android.internal.appwidget.IAppWidgetHost;
import com.android.internal.appwidget.IAppWidgetService.Stub;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.widget.IRemoteViewsAdapterConnection;
import com.android.internal.widget.IRemoteViewsAdapterConnection.Stub;
import com.android.internal.widget.IRemoteViewsFactory;
import com.android.internal.widget.IRemoteViewsFactory.Stub;
import com.android.server.LocalServices;
import com.android.server.WidgetBackupProvider;
import com.android.server.policy.IconUtilities;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class AppWidgetServiceImpl
  extends IAppWidgetService.Stub
  implements WidgetBackupProvider, DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener
{
  private static final int CURRENT_VERSION = 1;
  private static boolean DEBUG = false;
  public static boolean DEBUG_ONEPLUS = false;
  private static final int ID_PROVIDER_CHANGED = 1;
  private static final int ID_VIEWS_UPDATE = 0;
  private static final int KEYGUARD_HOST_ID = 1262836039;
  private static final int LOADED_PROFILE_ID = -1;
  private static final int MIN_UPDATE_PERIOD;
  private static final String NEW_KEYGUARD_HOST_PACKAGE = "com.android.keyguard";
  private static final String OLD_KEYGUARD_HOST_PACKAGE = "android";
  private static final String STATE_FILENAME = "appwidgets.xml";
  private static final String TAG = "AppWidgetServiceImpl";
  private static final int TAG_UNDEFINED = -1;
  private static final int UNKNOWN_UID = -1;
  private static final int UNKNOWN_USER_ID = -10;
  private final AlarmManager mAlarmManager;
  private final AppOpsManager mAppOpsManager;
  private final BackupRestoreController mBackupRestoreController;
  private final HashMap<Pair<Integer, Intent.FilterComparison>, ServiceConnection> mBoundRemoteViewsServices = new HashMap();
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      ??? = paramAnonymousIntent.getAction();
      int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536);
      if (AppWidgetServiceImpl.-get0()) {
        Slog.i("AppWidgetServiceImpl", "Received broadcast: " + ??? + " on user " + i);
      }
      if ("android.intent.action.CONFIGURATION_CHANGED".equals(???))
      {
        AppWidgetServiceImpl.-wrap10(AppWidgetServiceImpl.this);
        return;
      }
      if (("android.intent.action.MANAGED_PROFILE_AVAILABLE".equals(???)) || ("android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(???))) {
        synchronized (AppWidgetServiceImpl.-get5(AppWidgetServiceImpl.this))
        {
          AppWidgetServiceImpl.-wrap12(AppWidgetServiceImpl.this, i);
          return;
        }
      }
      if ("android.intent.action.PACKAGES_SUSPENDED".equals(???))
      {
        ??? = paramAnonymousIntent.getStringArrayExtra("android.intent.extra.changed_package_list");
        AppWidgetServiceImpl.-wrap20(AppWidgetServiceImpl.this, ???, true, getSendingUserId());
        return;
      }
      if ("android.intent.action.PACKAGES_UNSUSPENDED".equals(???))
      {
        ??? = paramAnonymousIntent.getStringArrayExtra("android.intent.extra.changed_package_list");
        AppWidgetServiceImpl.-wrap20(AppWidgetServiceImpl.this, ???, false, getSendingUserId());
        return;
      }
      AppWidgetServiceImpl.-wrap11(AppWidgetServiceImpl.this, paramAnonymousIntent, i);
    }
  };
  private final Handler mCallbackHandler;
  private final Context mContext;
  private final DevicePolicyManagerInternal mDevicePolicyManagerInternal;
  private final ArrayList<Host> mHosts = new ArrayList();
  private final IconUtilities mIconUtilities;
  private final KeyguardManager mKeyguardManager;
  private final SparseIntArray mLoadedUserIds = new SparseIntArray();
  private Locale mLocale;
  private final Object mLock = new Object();
  private int mMaxWidgetBitmapMemory;
  private final SparseIntArray mNextAppWidgetIds = new SparseIntArray();
  private final IPackageManager mPackageManager;
  private final ArraySet<Pair<Integer, String>> mPackagesWithBindWidgetPermission = new ArraySet();
  private final ArrayList<Provider> mProviders = new ArrayList();
  private final HashMap<Pair<Integer, Intent.FilterComparison>, HashSet<Integer>> mRemoteViewsServicesAppWidgets = new HashMap();
  private boolean mSafeMode;
  private final Handler mSaveStateHandler;
  private final SecurityPolicy mSecurityPolicy;
  private final UserManager mUserManager;
  private final SparseArray<ArraySet<String>> mWidgetPackages = new SparseArray();
  private final ArrayList<Widget> mWidgets = new ArrayList();
  
  static
  {
    int i = 0;
    DEBUG = false;
    DEBUG_ONEPLUS = DEBUG | Build.DEBUG_ONEPLUS;
    if (DEBUG) {}
    for (;;)
    {
      MIN_UPDATE_PERIOD = i;
      return;
      i = 1800000;
    }
  }
  
  AppWidgetServiceImpl(Context paramContext)
  {
    this.mContext = paramContext;
    this.mPackageManager = AppGlobals.getPackageManager();
    this.mAlarmManager = ((AlarmManager)this.mContext.getSystemService("alarm"));
    this.mUserManager = ((UserManager)this.mContext.getSystemService("user"));
    this.mAppOpsManager = ((AppOpsManager)this.mContext.getSystemService("appops"));
    this.mKeyguardManager = ((KeyguardManager)this.mContext.getSystemService("keyguard"));
    this.mDevicePolicyManagerInternal = ((DevicePolicyManagerInternal)LocalServices.getService(DevicePolicyManagerInternal.class));
    this.mSaveStateHandler = BackgroundThread.getHandler();
    this.mCallbackHandler = new CallbackHandler(this.mContext.getMainLooper());
    this.mBackupRestoreController = new BackupRestoreController(null);
    this.mSecurityPolicy = new SecurityPolicy(null);
    this.mIconUtilities = new IconUtilities(paramContext);
    computeMaximumWidgetBitmapMemory();
    registerBroadcastReceiver();
    registerOnCrossProfileProvidersChangedListener();
  }
  
  private boolean addProviderLocked(ResolveInfo paramResolveInfo)
  {
    if ((paramResolveInfo.activityInfo.applicationInfo.flags & 0x40000) != 0) {
      return false;
    }
    if (!paramResolveInfo.activityInfo.isEnabled()) {
      return false;
    }
    ComponentName localComponentName = new ComponentName(paramResolveInfo.activityInfo.packageName, paramResolveInfo.activityInfo.name);
    ProviderId localProviderId = new ProviderId(paramResolveInfo.activityInfo.applicationInfo.uid, localComponentName, null);
    Provider localProvider2 = parseProviderInfoXml(localProviderId, paramResolveInfo);
    if (localProvider2 != null)
    {
      Provider localProvider1 = lookupProviderLocked(localProviderId);
      paramResolveInfo = localProvider1;
      if (localProvider1 == null) {
        paramResolveInfo = lookupProviderLocked(new ProviderId(-1, localComponentName, null));
      }
      if (paramResolveInfo != null) {
        if ((paramResolveInfo.zombie) && (!this.mSafeMode)) {}
      }
      for (;;)
      {
        return true;
        paramResolveInfo.id = localProviderId;
        paramResolveInfo.zombie = false;
        paramResolveInfo.info = localProvider2.info;
        if (DEBUG)
        {
          Slog.i("AppWidgetServiceImpl", "Provider placeholder now reified: " + paramResolveInfo);
          continue;
          this.mProviders.add(localProvider2);
        }
      }
    }
    return false;
  }
  
  private void bindLoadedWidgetsLocked(List<LoadedWidgetState> paramList)
  {
    int i = paramList.size() - 1;
    if (i >= 0)
    {
      LoadedWidgetState localLoadedWidgetState = (LoadedWidgetState)paramList.remove(i);
      Widget localWidget = localLoadedWidgetState.widget;
      localWidget.provider = findProviderByTag(localLoadedWidgetState.providerTag);
      if (localWidget.provider == null) {}
      for (;;)
      {
        i -= 1;
        break;
        localWidget.host = findHostByTag(localLoadedWidgetState.hostTag);
        if (localWidget.host != null)
        {
          localWidget.provider.widgets.add(localWidget);
          localWidget.host.widgets.add(localWidget);
          addWidgetLocked(localWidget);
        }
      }
    }
  }
  
  private void bindService(Intent paramIntent, ServiceConnection paramServiceConnection, UserHandle paramUserHandle)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mContext.bindServiceAsUser(paramIntent, paramServiceConnection, 33554433, paramUserHandle);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void cancelBroadcasts(Provider paramProvider)
  {
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "cancelBroadcasts() for " + paramProvider);
    }
    long l;
    if (paramProvider.broadcast != null)
    {
      this.mAlarmManager.cancel(paramProvider.broadcast);
      l = Binder.clearCallingIdentity();
    }
    try
    {
      paramProvider.broadcast.cancel();
      Binder.restoreCallingIdentity(l);
      paramProvider.broadcast = null;
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void clearProvidersAndHostsTagsLocked()
  {
    int j = this.mProviders.size();
    int i = 0;
    while (i < j)
    {
      ((Provider)this.mProviders.get(i)).tag = -1;
      i += 1;
    }
    j = this.mHosts.size();
    i = 0;
    while (i < j)
    {
      ((Host)this.mHosts.get(i)).tag = -1;
      i += 1;
    }
  }
  
  private static AppWidgetProviderInfo cloneIfLocalBinder(AppWidgetProviderInfo paramAppWidgetProviderInfo)
  {
    if ((isLocalBinder()) && (paramAppWidgetProviderInfo != null)) {
      return paramAppWidgetProviderInfo.clone();
    }
    return paramAppWidgetProviderInfo;
  }
  
  private static Bundle cloneIfLocalBinder(Bundle paramBundle)
  {
    if ((isLocalBinder()) && (paramBundle != null)) {
      return (Bundle)paramBundle.clone();
    }
    return paramBundle;
  }
  
  private static RemoteViews cloneIfLocalBinder(RemoteViews paramRemoteViews)
  {
    if ((isLocalBinder()) && (paramRemoteViews != null)) {
      return paramRemoteViews.clone();
    }
    return paramRemoteViews;
  }
  
  private void computeMaximumWidgetBitmapMemory()
  {
    Display localDisplay = ((WindowManager)this.mContext.getSystemService("window")).getDefaultDisplay();
    Point localPoint = new Point();
    localDisplay.getRealSize(localPoint);
    this.mMaxWidgetBitmapMemory = (localPoint.x * 6 * localPoint.y);
  }
  
  private Bitmap createMaskedWidgetBitmap(String paramString, int paramInt)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      PackageManager localPackageManager = this.mContext.createPackageContextAsUser(paramString, 0, UserHandle.of(paramInt)).getPackageManager();
      paramString = localPackageManager.getApplicationInfo(paramString, 0).loadUnbadgedIcon(localPackageManager);
      paramString = this.mIconUtilities.createIconBitmap(paramString);
      return paramString;
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      Slog.e("AppWidgetServiceImpl", "Fail to get application icon", paramString);
      return null;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private RemoteViews createMaskedWidgetRemoteViews(Bitmap paramBitmap, boolean paramBoolean, PendingIntent paramPendingIntent)
  {
    RemoteViews localRemoteViews = new RemoteViews(this.mContext.getPackageName(), 17367310);
    if (paramBitmap != null) {
      localRemoteViews.setImageViewBitmap(16909402, paramBitmap);
    }
    if (!paramBoolean) {
      localRemoteViews.setViewVisibility(16909403, 4);
    }
    if (paramPendingIntent != null) {
      localRemoteViews.setOnClickPendingIntent(16909401, paramPendingIntent);
    }
    return localRemoteViews;
  }
  
  private void decrementAppWidgetServiceRefCount(Widget paramWidget)
  {
    Iterator localIterator = this.mRemoteViewsServicesAppWidgets.keySet().iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      HashSet localHashSet = (HashSet)this.mRemoteViewsServicesAppWidgets.get(localPair);
      if ((localHashSet.remove(Integer.valueOf(paramWidget.appWidgetId))) && (localHashSet.isEmpty()))
      {
        destroyRemoteViewsService(((Intent.FilterComparison)localPair.second).getIntent(), paramWidget);
        localIterator.remove();
      }
    }
  }
  
  private void deleteAppWidgetLocked(Widget paramWidget)
  {
    unbindAppWidgetRemoteViewsServicesLocked(paramWidget);
    Object localObject = paramWidget.host;
    ((Host)localObject).widgets.remove(paramWidget);
    pruneHostLocked((Host)localObject);
    removeWidgetLocked(paramWidget);
    localObject = paramWidget.provider;
    if (localObject != null)
    {
      ((Provider)localObject).widgets.remove(paramWidget);
      if (!((Provider)localObject).zombie)
      {
        sendDeletedIntentLocked(paramWidget);
        if (((Provider)localObject).widgets.isEmpty())
        {
          cancelBroadcasts((Provider)localObject);
          sendDisabledIntentLocked((Provider)localObject);
        }
      }
    }
  }
  
  private void deleteHostLocked(Host paramHost)
  {
    int i = paramHost.widgets.size() - 1;
    while (i >= 0)
    {
      deleteAppWidgetLocked((Widget)paramHost.widgets.remove(i));
      i -= 1;
    }
    this.mHosts.remove(paramHost);
    paramHost.callbacks = null;
  }
  
  private void deleteProviderLocked(Provider paramProvider)
  {
    deleteWidgetsLocked(paramProvider, -1);
    this.mProviders.remove(paramProvider);
    cancelBroadcasts(paramProvider);
  }
  
  private void deleteWidgetsLocked(Provider paramProvider, int paramInt)
  {
    int i = paramProvider.widgets.size() - 1;
    while (i >= 0)
    {
      Widget localWidget = (Widget)paramProvider.widgets.get(i);
      if ((paramInt == -1) || (paramInt == localWidget.host.getUserId()))
      {
        paramProvider.widgets.remove(i);
        updateAppWidgetInstanceLocked(localWidget, null, false);
        localWidget.host.widgets.remove(localWidget);
        removeWidgetLocked(localWidget);
        localWidget.provider = null;
        pruneHostLocked(localWidget.host);
        localWidget.host = null;
      }
      i -= 1;
    }
  }
  
  private void destroyRemoteViewsService(final Intent paramIntent, Widget paramWidget)
  {
    ServiceConnection local2 = new ServiceConnection()
    {
      public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
      {
        paramAnonymousComponentName = IRemoteViewsFactory.Stub.asInterface(paramAnonymousIBinder);
        try
        {
          paramAnonymousComponentName.onDestroy(paramIntent);
          AppWidgetServiceImpl.-get2(AppWidgetServiceImpl.this).unbindService(this);
          return;
        }
        catch (RemoteException paramAnonymousComponentName)
        {
          for (;;)
          {
            Slog.e("AppWidgetServiceImpl", "Error calling remove view factory", paramAnonymousComponentName);
          }
        }
      }
      
      public void onServiceDisconnected(ComponentName paramAnonymousComponentName) {}
    };
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mContext.bindServiceAsUser(paramIntent, local2, 33554433, paramWidget.provider.info.getProfile());
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private static void dumpGrant(Pair<Integer, String> paramPair, int paramInt, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print("  [");
    paramPrintWriter.print(paramInt);
    paramPrintWriter.print(']');
    paramPrintWriter.print(" user=");
    paramPrintWriter.print(paramPair.first);
    paramPrintWriter.print(" package=");
    paramPrintWriter.println((String)paramPair.second);
  }
  
  private static void dumpHost(Host paramHost, int paramInt, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print("  [");
    paramPrintWriter.print(paramInt);
    paramPrintWriter.print("] hostId=");
    paramPrintWriter.println(paramHost.id);
    paramPrintWriter.print("    callbacks=");
    paramPrintWriter.println(paramHost.callbacks);
    paramPrintWriter.print("    widgets.size=");
    paramPrintWriter.print(paramHost.widgets.size());
    paramPrintWriter.print(" zombie=");
    paramPrintWriter.println(paramHost.zombie);
  }
  
  private static void dumpProvider(Provider paramProvider, int paramInt, PrintWriter paramPrintWriter)
  {
    AppWidgetProviderInfo localAppWidgetProviderInfo = paramProvider.info;
    paramPrintWriter.print("  [");
    paramPrintWriter.print(paramInt);
    paramPrintWriter.print("] provider ");
    paramPrintWriter.println(paramProvider.id);
    paramPrintWriter.print("    min=(");
    paramPrintWriter.print(localAppWidgetProviderInfo.minWidth);
    paramPrintWriter.print("x");
    paramPrintWriter.print(localAppWidgetProviderInfo.minHeight);
    paramPrintWriter.print(")   minResize=(");
    paramPrintWriter.print(localAppWidgetProviderInfo.minResizeWidth);
    paramPrintWriter.print("x");
    paramPrintWriter.print(localAppWidgetProviderInfo.minResizeHeight);
    paramPrintWriter.print(") updatePeriodMillis=");
    paramPrintWriter.print(localAppWidgetProviderInfo.updatePeriodMillis);
    paramPrintWriter.print(" resizeMode=");
    paramPrintWriter.print(localAppWidgetProviderInfo.resizeMode);
    paramPrintWriter.print(" widgetCategory=");
    paramPrintWriter.print(localAppWidgetProviderInfo.widgetCategory);
    paramPrintWriter.print(" autoAdvanceViewId=");
    paramPrintWriter.print(localAppWidgetProviderInfo.autoAdvanceViewId);
    paramPrintWriter.print(" initialLayout=#");
    paramPrintWriter.print(Integer.toHexString(localAppWidgetProviderInfo.initialLayout));
    paramPrintWriter.print(" initialKeyguardLayout=#");
    paramPrintWriter.print(Integer.toHexString(localAppWidgetProviderInfo.initialKeyguardLayout));
    paramPrintWriter.print(" zombie=");
    paramPrintWriter.println(paramProvider.zombie);
  }
  
  private static void dumpWidget(Widget paramWidget, int paramInt, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print("  [");
    paramPrintWriter.print(paramInt);
    paramPrintWriter.print("] id=");
    paramPrintWriter.println(paramWidget.appWidgetId);
    paramPrintWriter.print("    host=");
    paramPrintWriter.println(paramWidget.host.id);
    if (paramWidget.provider != null)
    {
      paramPrintWriter.print("    provider=");
      paramPrintWriter.println(paramWidget.provider.id);
    }
    if (paramWidget.host != null)
    {
      paramPrintWriter.print("    host.callbacks=");
      paramPrintWriter.println(paramWidget.host.callbacks);
    }
    if (paramWidget.views != null)
    {
      paramPrintWriter.print("    views=");
      paramPrintWriter.println(paramWidget.views);
    }
  }
  
  private void ensureGroupStateLoadedLocked(int paramInt)
  {
    ensureGroupStateLoadedLocked(paramInt, true);
  }
  
  private void ensureGroupStateLoadedLocked(int paramInt, boolean paramBoolean)
  {
    if ((!paramBoolean) || (isUserRunningAndUnlocked(paramInt)))
    {
      if ((paramBoolean) && (isProfileWithLockedParent(paramInt))) {
        throw new IllegalStateException("Profile " + paramInt + " must have unlocked parent");
      }
    }
    else {
      throw new IllegalStateException("User " + paramInt + " must be unlocked for widgets to be available");
    }
    int[] arrayOfInt1 = this.mSecurityPolicy.getEnabledGroupProfileIds(paramInt);
    int i = 0;
    int k = arrayOfInt1.length;
    paramInt = 0;
    if (paramInt < k)
    {
      j = arrayOfInt1[paramInt];
      if (this.mLoadedUserIds.indexOfKey(j) >= 0) {
        arrayOfInt1[paramInt] = -1;
      }
      for (;;)
      {
        paramInt += 1;
        break;
        i += 1;
      }
    }
    if (i <= 0) {
      return;
    }
    paramInt = 0;
    int[] arrayOfInt2 = new int[i];
    int j = 0;
    i = paramInt;
    paramInt = j;
    while (paramInt < k)
    {
      int m = arrayOfInt1[paramInt];
      j = i;
      if (m != -1)
      {
        this.mLoadedUserIds.put(m, m);
        arrayOfInt2[i] = m;
        j = i + 1;
      }
      paramInt += 1;
      i = j;
    }
    clearProvidersAndHostsTagsLocked();
    loadGroupWidgetProvidersLocked(arrayOfInt2);
    loadGroupStateLocked(arrayOfInt2);
  }
  
  private Host findHostByTag(int paramInt)
  {
    if (paramInt < 0) {
      return null;
    }
    int j = this.mHosts.size();
    int i = 0;
    while (i < j)
    {
      Host localHost = (Host)this.mHosts.get(i);
      if (localHost.tag == paramInt) {
        return localHost;
      }
      i += 1;
    }
    return null;
  }
  
  private Provider findProviderByTag(int paramInt)
  {
    if (paramInt < 0) {
      return null;
    }
    int j = this.mProviders.size();
    int i = 0;
    while (i < j)
    {
      Provider localProvider = (Provider)this.mProviders.get(i);
      if (localProvider.tag == paramInt) {
        return localProvider;
      }
      i += 1;
    }
    return null;
  }
  
  private String getCanonicalPackageName(String paramString1, String paramString2, int paramInt)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      AppGlobals.getPackageManager().getReceiverInfo(new ComponentName(paramString1, paramString2), 0, paramInt);
      return paramString1;
    }
    catch (RemoteException paramString2)
    {
      paramString1 = this.mContext.getPackageManager().currentToCanonicalPackageNames(new String[] { paramString1 });
      if ((paramString1 != null) && (paramString1.length > 0))
      {
        paramString1 = paramString1[0];
        return paramString1;
      }
      return null;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private ActivityInfo getProviderInfo(ComponentName paramComponentName, int paramInt)
  {
    Intent localIntent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
    localIntent.setComponent(paramComponentName);
    paramComponentName = queryIntentReceivers(localIntent, paramInt);
    if (!paramComponentName.isEmpty()) {
      return ((ResolveInfo)paramComponentName.get(0)).activityInfo;
    }
    return null;
  }
  
  private static AtomicFile getSavedStateFile(int paramInt)
  {
    File localFile1 = Environment.getUserSystemDirectory(paramInt);
    File localFile2 = getStateFile(paramInt);
    if ((!localFile2.exists()) && (paramInt == 0))
    {
      if (!localFile1.exists()) {
        localFile1.mkdirs();
      }
      new File("/data/system/appwidgets.xml").renameTo(localFile2);
    }
    return new AtomicFile(localFile2);
  }
  
  private static File getStateFile(int paramInt)
  {
    return new File(Environment.getUserSystemDirectory(paramInt), "appwidgets.xml");
  }
  
  private int getUidForPackage(String paramString, int paramInt)
  {
    Object localObject = null;
    long l = Binder.clearCallingIdentity();
    try
    {
      paramString = this.mPackageManager.getPackageInfo(paramString, 0, paramInt);
      Binder.restoreCallingIdentity(l);
    }
    catch (RemoteException paramString)
    {
      for (;;)
      {
        paramString = paramString;
        Binder.restoreCallingIdentity(l);
        paramString = (String)localObject;
      }
    }
    finally
    {
      paramString = finally;
      Binder.restoreCallingIdentity(l);
      throw paramString;
    }
    if ((paramString == null) || (paramString.applicationInfo == null)) {
      return -1;
    }
    return paramString.applicationInfo.uid;
  }
  
  private static int[] getWidgetIds(ArrayList<Widget> paramArrayList)
  {
    int j = paramArrayList.size();
    int[] arrayOfInt = new int[j];
    int i = 0;
    while (i < j)
    {
      arrayOfInt[i] = ((Widget)paramArrayList.get(i)).appWidgetId;
      i += 1;
    }
    return arrayOfInt;
  }
  
  /* Error */
  private void handleNotifyAppWidgetViewDataChanged(Host paramHost, IAppWidgetHost paramIAppWidgetHost, int paramInt1, int paramInt2, long paramLong)
  {
    // Byte code:
    //   0: aload_2
    //   1: iload_3
    //   2: iload 4
    //   4: invokeinterface 1047 3 0
    //   9: aload_1
    //   10: lload 5
    //   12: putfield 1051	com/android/server/appwidget/AppWidgetServiceImpl$Host:lastWidgetUpdateTime	J
    //   15: aload_0
    //   16: getfield 165	com/android/server/appwidget/AppWidgetServiceImpl:mLock	Ljava/lang/Object;
    //   19: astore 7
    //   21: aload 7
    //   23: monitorenter
    //   24: aload_2
    //   25: ifnonnull +126 -> 151
    //   28: aload_1
    //   29: aconst_null
    //   30: putfield 784	com/android/server/appwidget/AppWidgetServiceImpl$Host:callbacks	Lcom/android/internal/appwidget/IAppWidgetHost;
    //   33: aload_0
    //   34: getfield 319	com/android/server/appwidget/AppWidgetServiceImpl:mRemoteViewsServicesAppWidgets	Ljava/util/HashMap;
    //   37: invokevirtual 708	java/util/HashMap:keySet	()Ljava/util/Set;
    //   40: invokeinterface 1054 1 0
    //   45: astore_1
    //   46: aload_1
    //   47: invokeinterface 719 1 0
    //   52: ifeq +99 -> 151
    //   55: aload_1
    //   56: invokeinterface 722 1 0
    //   61: checkcast 724	android/util/Pair
    //   64: astore_2
    //   65: aload_0
    //   66: getfield 319	com/android/server/appwidget/AppWidgetServiceImpl:mRemoteViewsServicesAppWidgets	Ljava/util/HashMap;
    //   69: aload_2
    //   70: invokevirtual 727	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   73: checkcast 729	java/util/HashSet
    //   76: iload_3
    //   77: invokestatic 738	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   80: invokevirtual 1057	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   83: ifeq -37 -> 46
    //   86: new 14	com/android/server/appwidget/AppWidgetServiceImpl$3
    //   89: dup
    //   90: aload_0
    //   91: invokespecial 1058	com/android/server/appwidget/AppWidgetServiceImpl$3:<init>	(Lcom/android/server/appwidget/AppWidgetServiceImpl;)V
    //   94: astore 8
    //   96: aload_2
    //   97: getfield 823	android/util/Pair:first	Ljava/lang/Object;
    //   100: checkcast 734	java/lang/Integer
    //   103: invokevirtual 1061	java/lang/Integer:intValue	()I
    //   106: invokestatic 1063	android/os/UserHandle:getUserId	(I)I
    //   109: istore 4
    //   111: aload_0
    //   112: aload_2
    //   113: getfield 746	android/util/Pair:second	Ljava/lang/Object;
    //   116: checkcast 748	android/content/Intent$FilterComparison
    //   119: invokevirtual 752	android/content/Intent$FilterComparison:getIntent	()Landroid/content/Intent;
    //   122: aload 8
    //   124: new 648	android/os/UserHandle
    //   127: dup
    //   128: iload 4
    //   130: invokespecial 1065	android/os/UserHandle:<init>	(I)V
    //   133: invokespecial 1067	com/android/server/appwidget/AppWidgetServiceImpl:bindService	(Landroid/content/Intent;Landroid/content/ServiceConnection;Landroid/os/UserHandle;)V
    //   136: goto -90 -> 46
    //   139: astore_1
    //   140: aload 7
    //   142: monitorexit
    //   143: aload_1
    //   144: athrow
    //   145: astore_2
    //   146: aconst_null
    //   147: astore_2
    //   148: goto -133 -> 15
    //   151: aload 7
    //   153: monitorexit
    //   154: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	155	0	this	AppWidgetServiceImpl
    //   0	155	1	paramHost	Host
    //   0	155	2	paramIAppWidgetHost	IAppWidgetHost
    //   0	155	3	paramInt1	int
    //   0	155	4	paramInt2	int
    //   0	155	5	paramLong	long
    //   19	133	7	localObject	Object
    //   94	29	8	local3	3
    // Exception table:
    //   from	to	target	type
    //   28	46	139	finally
    //   46	136	139	finally
    //   0	15	145	android/os/RemoteException
  }
  
  private void handleNotifyProviderChanged(Host paramHost, IAppWidgetHost arg2, int paramInt, AppWidgetProviderInfo paramAppWidgetProviderInfo, long paramLong)
  {
    try
    {
      ???.providerChanged(paramInt, paramAppWidgetProviderInfo);
      paramHost.lastWidgetUpdateTime = paramLong;
      return;
    }
    catch (RemoteException paramAppWidgetProviderInfo)
    {
      synchronized (this.mLock)
      {
        Slog.e("AppWidgetServiceImpl", "Widget host dead: " + paramHost.id, paramAppWidgetProviderInfo);
        paramHost.callbacks = null;
        return;
      }
    }
  }
  
  private void handleNotifyProvidersChanged(Host paramHost, IAppWidgetHost arg2)
  {
    try
    {
      ???.providersChanged();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      synchronized (this.mLock)
      {
        Slog.e("AppWidgetServiceImpl", "Widget host dead: " + paramHost.id, localRemoteException);
        paramHost.callbacks = null;
        return;
      }
    }
  }
  
  private void handleNotifyUpdateAppWidget(Host paramHost, IAppWidgetHost arg2, int paramInt, RemoteViews paramRemoteViews, long paramLong)
  {
    try
    {
      ???.updateAppWidget(paramInt, paramRemoteViews);
      paramHost.lastWidgetUpdateTime = paramLong;
      return;
    }
    catch (Exception paramRemoteViews)
    {
      synchronized (this.mLock)
      {
        Slog.e("AppWidgetServiceImpl", "Widget host dead: " + paramHost.id, paramRemoteViews);
        paramHost.callbacks = null;
        return;
      }
    }
  }
  
  private int incrementAndGetAppWidgetIdLocked(int paramInt)
  {
    int i = peekNextAppWidgetIdLocked(paramInt) + 1;
    this.mNextAppWidgetIds.put(paramInt, i);
    return i;
  }
  
  private void incrementAppWidgetServiceRefCount(int paramInt, Pair<Integer, Intent.FilterComparison> paramPair)
  {
    if (this.mRemoteViewsServicesAppWidgets.containsKey(paramPair)) {}
    HashSet localHashSet;
    for (paramPair = (HashSet)this.mRemoteViewsServicesAppWidgets.get(paramPair);; paramPair = localHashSet)
    {
      paramPair.add(Integer.valueOf(paramInt));
      return;
      localHashSet = new HashSet();
      this.mRemoteViewsServicesAppWidgets.put(paramPair, localHashSet);
    }
  }
  
  private static boolean isLocalBinder()
  {
    return Process.myPid() == Binder.getCallingPid();
  }
  
  private boolean isProfileWithLockedParent(int paramInt)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      UserInfo localUserInfo = this.mUserManager.getUserInfo(paramInt);
      boolean bool;
      if ((localUserInfo != null) && (localUserInfo.isManagedProfile()))
      {
        localUserInfo = this.mUserManager.getProfileParent(paramInt);
        if (localUserInfo != null) {
          bool = isUserRunningAndUnlocked(localUserInfo.getUserHandle().getIdentifier());
        }
      }
      return !bool;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private boolean isProfileWithUnlockedParent(int paramInt)
  {
    UserInfo localUserInfo = this.mUserManager.getUserInfo(paramInt);
    if ((localUserInfo != null) && (localUserInfo.isManagedProfile()))
    {
      localUserInfo = this.mUserManager.getProfileParent(paramInt);
      if ((localUserInfo != null) && (this.mUserManager.isUserUnlockingOrUnlocked(localUserInfo.getUserHandle()))) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isUserRunningAndUnlocked(int paramInt)
  {
    if (this.mUserManager.isUserRunning(paramInt)) {
      return StorageManager.isUserKeyUnlocked(paramInt);
    }
    return false;
  }
  
  private void loadGroupStateLocked(int[] paramArrayOfInt)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    int m = paramArrayOfInt.length;
    int k = 0;
    int j;
    for (;;)
    {
      if (k >= m) {
        break label110;
      }
      int n = paramArrayOfInt[k];
      Object localObject = getSavedStateFile(n);
      j = i;
      try
      {
        localObject = ((AtomicFile)localObject).openRead();
        j = i;
        i = readProfileStateFromFileLocked((FileInputStream)localObject, n, localArrayList);
        j = i;
        IoUtils.closeQuietly((AutoCloseable)localObject);
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        for (;;)
        {
          Slog.w("AppWidgetServiceImpl", "Failed to read state: " + localFileNotFoundException);
          i = j;
        }
      }
      k += 1;
    }
    label110:
    if (i >= 0)
    {
      bindLoadedWidgetsLocked(localArrayList);
      performUpgradeLocked(i);
    }
    for (;;)
    {
      return;
      Slog.w("AppWidgetServiceImpl", "Failed to read state, clearing widgets and hosts.");
      clearWidgetsLocked();
      this.mHosts.clear();
      j = this.mProviders.size();
      i = 0;
      while (i < j)
      {
        ((Provider)this.mProviders.get(i)).widgets.clear();
        i += 1;
      }
    }
  }
  
  private void loadGroupWidgetProvidersLocked(int[] paramArrayOfInt)
  {
    Object localObject1 = null;
    Intent localIntent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
    int j = paramArrayOfInt.length;
    int i = 0;
    if (i < j)
    {
      List localList = queryIntentReceivers(localIntent, paramArrayOfInt[i]);
      Object localObject2 = localObject1;
      if (localList != null)
      {
        if (!localList.isEmpty()) {
          break label70;
        }
        localObject2 = localObject1;
      }
      for (;;)
      {
        i += 1;
        localObject1 = localObject2;
        break;
        label70:
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new ArrayList();
        }
        ((List)localObject2).addAll(localList);
      }
    }
    if (localObject1 == null) {}
    for (i = 0;; i = ((List)localObject1).size())
    {
      j = 0;
      while (j < i)
      {
        addProviderLocked((ResolveInfo)((List)localObject1).get(j));
        j += 1;
      }
    }
  }
  
  private Host lookupHostLocked(HostId paramHostId)
  {
    int j = this.mHosts.size();
    int i = 0;
    while (i < j)
    {
      Host localHost = (Host)this.mHosts.get(i);
      if (localHost.id.equals(paramHostId)) {
        return localHost;
      }
      i += 1;
    }
    return null;
  }
  
  private Host lookupOrAddHostLocked(HostId paramHostId)
  {
    Host localHost = lookupHostLocked(paramHostId);
    if (localHost != null) {
      return localHost;
    }
    localHost = new Host(null);
    localHost.id = paramHostId;
    this.mHosts.add(localHost);
    return localHost;
  }
  
  private Provider lookupProviderLocked(ProviderId paramProviderId)
  {
    int j = this.mProviders.size();
    int i = 0;
    while (i < j)
    {
      Provider localProvider = (Provider)this.mProviders.get(i);
      if (localProvider.id.equals(paramProviderId)) {
        return localProvider;
      }
      i += 1;
    }
    return null;
  }
  
  private Widget lookupWidgetLocked(int paramInt1, int paramInt2, String paramString)
  {
    int j = this.mWidgets.size();
    int i = 0;
    while (i < j)
    {
      Widget localWidget = (Widget)this.mWidgets.get(i);
      if ((localWidget.appWidgetId == paramInt1) && (this.mSecurityPolicy.canAccessAppWidget(localWidget, paramInt2, paramString))) {
        return localWidget;
      }
      i += 1;
    }
    return null;
  }
  
  private void maskWidgetsViewsLocked(Provider paramProvider, Widget paramWidget)
  {
    int j = paramProvider.widgets.size();
    if (j == 0) {
      return;
    }
    Object localObject1 = paramProvider.info.provider.getPackageName();
    int i = paramProvider.getUserId();
    Bitmap localBitmap = createMaskedWidgetBitmap((String)localObject1, i);
    if (localBitmap == null) {
      return;
    }
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      boolean bool1;
      Widget localWidget;
      try
      {
        if (paramProvider.maskedBySuspendedPackage)
        {
          bool1 = this.mUserManager.getUserInfo(i).isManagedProfile();
          localObject1 = this.mDevicePolicyManagerInternal.createPackageSuspendedDialogIntent((String)localObject1, i);
          break label259;
          if (i >= j) {
            break label253;
          }
          localWidget = (Widget)paramProvider.widgets.get(i);
          if ((paramWidget != null) && (paramWidget != localWidget)) {
            break label264;
          }
        }
        else if (paramProvider.maskedByQuietProfile)
        {
          bool1 = true;
          localObject1 = UnlaunchableAppActivity.createInQuietModeDialogIntent(i);
        }
        else
        {
          boolean bool2 = true;
          localObject2 = this.mKeyguardManager.createConfirmDeviceCredentialIntent(null, null, i);
          localObject1 = localObject2;
          bool1 = bool2;
          if (localObject2 != null)
          {
            ((Intent)localObject2).setFlags(276824064);
            localObject1 = localObject2;
            bool1 = bool2;
          }
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      Object localObject2 = null;
      if (localObject1 != null) {
        localObject2 = PendingIntent.getActivity(this.mContext, localWidget.appWidgetId, (Intent)localObject1, 134217728);
      }
      if (Widget.-wrap1(localWidget, createMaskedWidgetRemoteViews(localBitmap, bool1, (PendingIntent)localObject2)))
      {
        scheduleNotifyUpdateAppWidgetLocked(localWidget, localWidget.getEffectiveViewsLocked());
        break label264;
        label253:
        Binder.restoreCallingIdentity(l);
        return;
        label259:
        i = 0;
        continue;
      }
      label264:
      i += 1;
    }
  }
  
  private void onConfigurationChanged()
  {
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "onConfigurationChanged()");
    }
    Object localObject1 = Locale.getDefault();
    if ((localObject1 == null) || (this.mLocale == null)) {}
    int i;
    for (;;)
    {
      this.mLocale = ((Locale)localObject1);
      synchronized (this.mLock)
      {
        ArrayList localArrayList = new ArrayList(this.mProviders);
        HashSet localHashSet = new HashSet();
        i = localArrayList.size();
        i -= 1;
        localObject1 = null;
        for (;;)
        {
          if (i >= 0) {}
          try
          {
            localProvider = (Provider)localArrayList.get(i);
            j = localProvider.getUserId();
            if (this.mUserManager.isUserUnlockingOrUnlocked(j))
            {
              boolean bool = isProfileWithLockedParent(j);
              if (!bool) {
                break label144;
              }
            }
          }
          finally
          {
            for (;;)
            {
              Provider localProvider;
              int j;
              continue;
            }
          }
          i -= 1;
          continue;
          if (!((Locale)localObject1).equals(this.mLocale)) {
            break;
          }
          return;
          label144:
          ensureGroupStateLoadedLocked(j);
          if ((localHashSet.contains(localProvider.id)) || (!updateProvidersForPackageLocked(localProvider.id.componentName.getPackageName(), localProvider.getUserId(), localHashSet))) {
            break label279;
          }
          if (localObject1 != null) {
            break label276;
          }
          localObject1 = new SparseIntArray();
          j = this.mSecurityPolicy.getGroupParent(localProvider.getUserId());
          ((SparseIntArray)localObject1).put(j, j);
        }
      }
    }
    if (localObject2 != null)
    {
      j = ((SparseIntArray)localObject2).size();
      i = 0;
      while (i < j)
      {
        saveGroupStateAsync(((SparseIntArray)localObject2).get(i));
        i += 1;
      }
    }
  }
  
  private void onPackageBroadcastReceived(Intent paramIntent, int paramInt)
  {
    if ((!this.mUserManager.isUserUnlockingOrUnlocked(paramInt)) || (isProfileWithLockedParent(paramInt))) {
      return;
    }
    ??? = paramIntent.getAction();
    boolean bool2 = false;
    int n = 0;
    int j = 0;
    int k = 0;
    Object localObject1;
    boolean bool1;
    if ("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(???))
    {
      localObject1 = paramIntent.getStringArrayExtra("android.intent.extra.changed_package_list");
      bool1 = true;
    }
    while ((localObject1 == null) || (localObject1.length == 0))
    {
      return;
      if ("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(???))
      {
        localObject1 = paramIntent.getStringArrayExtra("android.intent.extra.changed_package_list");
        bool1 = false;
      }
      else
      {
        localObject1 = paramIntent.getData();
        if (localObject1 == null) {
          return;
        }
        String str = ((Uri)localObject1).getSchemeSpecificPart();
        if (str == null) {
          return;
        }
        localObject1 = new String[1];
        localObject1[0] = str;
        bool1 = "android.intent.action.PACKAGE_ADDED".equals(???);
        bool2 = "android.intent.action.PACKAGE_CHANGED".equals(???);
      }
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        ensureGroupStateLoadedLocked(paramInt);
        paramIntent = paramIntent.getExtras();
        int i1;
        if ((bool1) || (bool2))
        {
          if (!bool1) {
            break label386;
          }
          if (paramIntent == null) {
            break label376;
          }
          if (!paramIntent.getBoolean("android.intent.extra.REPLACING", false)) {
            break label381;
          }
          i = 0;
          n = 0;
          i1 = localObject1.length;
          j = k;
          if (n < i1)
          {
            paramIntent = localObject1[n];
            k |= updateProvidersForPackageLocked(paramIntent, paramInt, null);
            if ((i == 0) || (paramInt != 0)) {
              break label367;
            }
            j = getUidForPackage(paramIntent, paramInt);
            if (j < 0) {
              break label367;
            }
            resolveHostUidLocked(paramIntent, j);
            break label367;
          }
        }
        else
        {
          if (paramIntent == null) {
            break label391;
          }
          if (!paramIntent.getBoolean("android.intent.extra.REPLACING", false)) {
            break label396;
          }
          i = 0;
          if (i != 0)
          {
            k = 0;
            i1 = localObject1.length;
            i = n;
            j = i;
            if (k < i1)
            {
              i |= removeHostsAndProvidersForPackageLocked(localObject1[k], paramInt);
              int m;
              k += 1;
              continue;
            }
          }
        }
        if (j != 0)
        {
          saveGroupStateAsync(paramInt);
          scheduleNotifyGroupHostsForProvidersChangedLocked(paramInt);
        }
        return;
      }
      label367:
      n += 1;
      continue;
      label376:
      int i = 1;
      continue;
      label381:
      i = 1;
      continue;
      label386:
      i = 0;
      continue;
      label391:
      i = 1;
      continue;
      label396:
      i = 1;
    }
  }
  
  private void onWidgetRemovedLocked(Widget paramWidget)
  {
    if (paramWidget.provider == null) {
      return;
    }
    int j = paramWidget.provider.getUserId();
    paramWidget = paramWidget.provider.info.provider.getPackageName();
    ArraySet localArraySet = (ArraySet)this.mWidgetPackages.get(j);
    if (localArraySet == null) {
      return;
    }
    int k = this.mWidgets.size();
    int i = 0;
    if (i < k)
    {
      Widget localWidget = (Widget)this.mWidgets.get(i);
      if (localWidget.provider == null) {}
      while ((localWidget.provider.getUserId() != j) || (!paramWidget.equals(localWidget.provider.info.provider.getPackageName())))
      {
        i += 1;
        break;
      }
      return;
    }
    localArraySet.remove(paramWidget);
  }
  
  private void onWidgetsClearedLocked()
  {
    this.mWidgetPackages.clear();
  }
  
  /* Error */
  private Provider parseProviderInfoXml(ProviderId paramProviderId, ResolveInfo paramResolveInfo)
  {
    // Byte code:
    //   0: aload_2
    //   1: getfield 428	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   4: astore 9
    //   6: aconst_null
    //   7: astore 8
    //   9: aconst_null
    //   10: astore 7
    //   12: aload 9
    //   14: aload_0
    //   15: getfield 154	com/android/server/appwidget/AppWidgetServiceImpl:mContext	Landroid/content/Context;
    //   18: invokevirtual 659	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   21: ldc_w 1329
    //   24: invokevirtual 1333	android/content/pm/ActivityInfo:loadXmlMetaData	(Landroid/content/pm/PackageManager;Ljava/lang/String;)Landroid/content/res/XmlResourceParser;
    //   27: astore 6
    //   29: aload 6
    //   31: ifnonnull +56 -> 87
    //   34: aload 6
    //   36: astore 7
    //   38: aload 6
    //   40: astore 8
    //   42: ldc 77
    //   44: new 483	java/lang/StringBuilder
    //   47: dup
    //   48: invokespecial 484	java/lang/StringBuilder:<init>	()V
    //   51: ldc_w 1335
    //   54: invokevirtual 490	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: aload_1
    //   58: invokevirtual 493	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   61: bipush 39
    //   63: invokevirtual 1338	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   66: invokevirtual 497	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   69: invokestatic 1158	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   72: pop
    //   73: aload 6
    //   75: ifnull +10 -> 85
    //   78: aload 6
    //   80: invokeinterface 1343 1 0
    //   85: aconst_null
    //   86: areturn
    //   87: aload 6
    //   89: astore 7
    //   91: aload 6
    //   93: astore 8
    //   95: aload 6
    //   97: invokestatic 1349	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   100: astore 11
    //   102: aload 6
    //   104: astore 7
    //   106: aload 6
    //   108: astore 8
    //   110: aload 6
    //   112: invokeinterface 1351 1 0
    //   117: istore_3
    //   118: iload_3
    //   119: iconst_1
    //   120: if_icmpeq +8 -> 128
    //   123: iload_3
    //   124: iconst_2
    //   125: if_icmpne -23 -> 102
    //   128: aload 6
    //   130: astore 7
    //   132: aload 6
    //   134: astore 8
    //   136: ldc_w 1353
    //   139: aload 6
    //   141: invokeinterface 1356 1 0
    //   146: invokevirtual 1277	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   149: ifne +67 -> 216
    //   152: aload 6
    //   154: astore 7
    //   156: aload 6
    //   158: astore 8
    //   160: ldc 77
    //   162: new 483	java/lang/StringBuilder
    //   165: dup
    //   166: invokespecial 484	java/lang/StringBuilder:<init>	()V
    //   169: ldc_w 1358
    //   172: invokevirtual 490	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: aload_1
    //   176: getfield 1261	com/android/server/appwidget/AppWidgetServiceImpl$ProviderId:componentName	Landroid/content/ComponentName;
    //   179: invokevirtual 493	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   182: ldc_w 1360
    //   185: invokevirtual 490	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   188: aload_1
    //   189: getfield 1361	com/android/server/appwidget/AppWidgetServiceImpl$ProviderId:uid	I
    //   192: invokevirtual 940	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   195: invokevirtual 497	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   198: invokestatic 1158	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   201: pop
    //   202: aload 6
    //   204: ifnull +10 -> 214
    //   207: aload 6
    //   209: invokeinterface 1343 1 0
    //   214: aconst_null
    //   215: areturn
    //   216: aload 6
    //   218: astore 7
    //   220: aload 6
    //   222: astore 8
    //   224: new 34	com/android/server/appwidget/AppWidgetServiceImpl$Provider
    //   227: dup
    //   228: aconst_null
    //   229: invokespecial 1363	com/android/server/appwidget/AppWidgetServiceImpl$Provider:<init>	(Lcom/android/server/appwidget/AppWidgetServiceImpl$Provider;)V
    //   232: astore 10
    //   234: aload 10
    //   236: aload_1
    //   237: putfield 477	com/android/server/appwidget/AppWidgetServiceImpl$Provider:id	Lcom/android/server/appwidget/AppWidgetServiceImpl$ProviderId;
    //   240: new 602	android/appwidget/AppWidgetProviderInfo
    //   243: dup
    //   244: invokespecial 1364	android/appwidget/AppWidgetProviderInfo:<init>	()V
    //   247: astore 7
    //   249: aload 10
    //   251: aload 7
    //   253: putfield 481	com/android/server/appwidget/AppWidgetServiceImpl$Provider:info	Landroid/appwidget/AppWidgetProviderInfo;
    //   256: aload 7
    //   258: aload_1
    //   259: getfield 1261	com/android/server/appwidget/AppWidgetServiceImpl$ProviderId:componentName	Landroid/content/ComponentName;
    //   262: putfield 1197	android/appwidget/AppWidgetProviderInfo:provider	Landroid/content/ComponentName;
    //   265: aload 7
    //   267: aload 9
    //   269: putfield 1367	android/appwidget/AppWidgetProviderInfo:providerInfo	Landroid/content/pm/ActivityInfo;
    //   272: invokestatic 562	android/os/Binder:clearCallingIdentity	()J
    //   275: lstore 4
    //   277: aload_0
    //   278: getfield 154	com/android/server/appwidget/AppWidgetServiceImpl:mContext	Landroid/content/Context;
    //   281: invokevirtual 659	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   284: astore 8
    //   286: aload_1
    //   287: getfield 1361	com/android/server/appwidget/AppWidgetServiceImpl$ProviderId:uid	I
    //   290: invokestatic 1063	android/os/UserHandle:getUserId	(I)I
    //   293: istore_3
    //   294: aload 8
    //   296: aload 8
    //   298: aload 9
    //   300: getfield 448	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   303: iconst_0
    //   304: iload_3
    //   305: invokevirtual 1371	android/content/pm/PackageManager:getApplicationInfoAsUser	(Ljava/lang/String;II)Landroid/content/pm/ApplicationInfo;
    //   308: invokevirtual 1375	android/content/pm/PackageManager:getResourcesForApplication	(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/Resources;
    //   311: astore 8
    //   313: lload 4
    //   315: invokestatic 571	android/os/Binder:restoreCallingIdentity	(J)V
    //   318: aload 8
    //   320: aload 11
    //   322: getstatic 1381	com/android/internal/R$styleable:AppWidgetProviderInfo	[I
    //   325: invokevirtual 1387	android/content/res/Resources:obtainAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   328: astore 8
    //   330: aload 8
    //   332: iconst_0
    //   333: invokevirtual 1393	android/content/res/TypedArray:peekValue	(I)Landroid/util/TypedValue;
    //   336: astore 11
    //   338: aload 11
    //   340: ifnull +335 -> 675
    //   343: aload 11
    //   345: getfield 1398	android/util/TypedValue:data	I
    //   348: istore_3
    //   349: aload 7
    //   351: iload_3
    //   352: putfield 862	android/appwidget/AppWidgetProviderInfo:minWidth	I
    //   355: aload 8
    //   357: iconst_1
    //   358: invokevirtual 1393	android/content/res/TypedArray:peekValue	(I)Landroid/util/TypedValue;
    //   361: astore 11
    //   363: aload 11
    //   365: ifnull +315 -> 680
    //   368: aload 11
    //   370: getfield 1398	android/util/TypedValue:data	I
    //   373: istore_3
    //   374: aload 7
    //   376: iload_3
    //   377: putfield 866	android/appwidget/AppWidgetProviderInfo:minHeight	I
    //   380: aload 8
    //   382: bipush 8
    //   384: invokevirtual 1393	android/content/res/TypedArray:peekValue	(I)Landroid/util/TypedValue;
    //   387: astore 11
    //   389: aload 11
    //   391: ifnull +294 -> 685
    //   394: aload 11
    //   396: getfield 1398	android/util/TypedValue:data	I
    //   399: istore_3
    //   400: aload 7
    //   402: iload_3
    //   403: putfield 871	android/appwidget/AppWidgetProviderInfo:minResizeWidth	I
    //   406: aload 8
    //   408: bipush 9
    //   410: invokevirtual 1393	android/content/res/TypedArray:peekValue	(I)Landroid/util/TypedValue;
    //   413: astore 11
    //   415: aload 11
    //   417: ifnull +277 -> 694
    //   420: aload 11
    //   422: getfield 1398	android/util/TypedValue:data	I
    //   425: istore_3
    //   426: aload 7
    //   428: iload_3
    //   429: putfield 874	android/appwidget/AppWidgetProviderInfo:minResizeHeight	I
    //   432: aload 7
    //   434: aload 8
    //   436: iconst_2
    //   437: iconst_0
    //   438: invokevirtual 1402	android/content/res/TypedArray:getInt	(II)I
    //   441: putfield 879	android/appwidget/AppWidgetProviderInfo:updatePeriodMillis	I
    //   444: aload 7
    //   446: aload 8
    //   448: iconst_3
    //   449: iconst_0
    //   450: invokevirtual 1405	android/content/res/TypedArray:getResourceId	(II)I
    //   453: putfield 899	android/appwidget/AppWidgetProviderInfo:initialLayout	I
    //   456: aload 7
    //   458: aload 8
    //   460: bipush 10
    //   462: iconst_0
    //   463: invokevirtual 1405	android/content/res/TypedArray:getResourceId	(II)I
    //   466: putfield 908	android/appwidget/AppWidgetProviderInfo:initialKeyguardLayout	I
    //   469: aload 8
    //   471: iconst_4
    //   472: invokevirtual 1408	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   475: astore 11
    //   477: aload 11
    //   479: ifnull +24 -> 503
    //   482: aload 7
    //   484: new 445	android/content/ComponentName
    //   487: dup
    //   488: aload_1
    //   489: getfield 1261	com/android/server/appwidget/AppWidgetServiceImpl$ProviderId:componentName	Landroid/content/ComponentName;
    //   492: invokevirtual 1198	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   495: aload 11
    //   497: invokespecial 454	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   500: putfield 1411	android/appwidget/AppWidgetProviderInfo:configure	Landroid/content/ComponentName;
    //   503: aload 7
    //   505: aload 9
    //   507: aload_0
    //   508: getfield 154	com/android/server/appwidget/AppWidgetServiceImpl:mContext	Landroid/content/Context;
    //   511: invokevirtual 659	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   514: invokevirtual 1415	android/content/pm/ActivityInfo:loadLabel	(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;
    //   517: invokeinterface 1418 1 0
    //   522: putfield 1421	android/appwidget/AppWidgetProviderInfo:label	Ljava/lang/String;
    //   525: aload 7
    //   527: aload_2
    //   528: invokevirtual 1424	android/content/pm/ResolveInfo:getIconResource	()I
    //   531: putfield 1427	android/appwidget/AppWidgetProviderInfo:icon	I
    //   534: aload 7
    //   536: aload 8
    //   538: iconst_5
    //   539: iconst_0
    //   540: invokevirtual 1405	android/content/res/TypedArray:getResourceId	(II)I
    //   543: putfield 1430	android/appwidget/AppWidgetProviderInfo:previewImage	I
    //   546: aload 7
    //   548: aload 8
    //   550: bipush 6
    //   552: iconst_m1
    //   553: invokevirtual 1405	android/content/res/TypedArray:getResourceId	(II)I
    //   556: putfield 894	android/appwidget/AppWidgetProviderInfo:autoAdvanceViewId	I
    //   559: aload 7
    //   561: aload 8
    //   563: bipush 7
    //   565: iconst_0
    //   566: invokevirtual 1402	android/content/res/TypedArray:getInt	(II)I
    //   569: putfield 884	android/appwidget/AppWidgetProviderInfo:resizeMode	I
    //   572: aload 7
    //   574: aload 8
    //   576: bipush 11
    //   578: iconst_1
    //   579: invokevirtual 1402	android/content/res/TypedArray:getInt	(II)I
    //   582: putfield 889	android/appwidget/AppWidgetProviderInfo:widgetCategory	I
    //   585: aload 8
    //   587: invokevirtual 1433	android/content/res/TypedArray:recycle	()V
    //   590: aload 6
    //   592: ifnull +10 -> 602
    //   595: aload 6
    //   597: invokeinterface 1343 1 0
    //   602: aload 10
    //   604: areturn
    //   605: astore_2
    //   606: lload 4
    //   608: invokestatic 571	android/os/Binder:restoreCallingIdentity	(J)V
    //   611: aload_2
    //   612: athrow
    //   613: astore_2
    //   614: aload 6
    //   616: astore 7
    //   618: ldc 77
    //   620: new 483	java/lang/StringBuilder
    //   623: dup
    //   624: invokespecial 484	java/lang/StringBuilder:<init>	()V
    //   627: ldc_w 1435
    //   630: invokevirtual 490	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   633: aload_1
    //   634: getfield 1261	com/android/server/appwidget/AppWidgetServiceImpl$ProviderId:componentName	Landroid/content/ComponentName;
    //   637: invokevirtual 493	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   640: ldc_w 1360
    //   643: invokevirtual 490	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   646: aload_1
    //   647: getfield 1361	com/android/server/appwidget/AppWidgetServiceImpl$ProviderId:uid	I
    //   650: invokevirtual 940	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   653: invokevirtual 497	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   656: aload_2
    //   657: invokestatic 1437	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   660: pop
    //   661: aload 6
    //   663: ifnull +10 -> 673
    //   666: aload 6
    //   668: invokeinterface 1343 1 0
    //   673: aconst_null
    //   674: areturn
    //   675: iconst_0
    //   676: istore_3
    //   677: goto -328 -> 349
    //   680: iconst_0
    //   681: istore_3
    //   682: goto -308 -> 374
    //   685: aload 7
    //   687: getfield 862	android/appwidget/AppWidgetProviderInfo:minWidth	I
    //   690: istore_3
    //   691: goto -291 -> 400
    //   694: aload 7
    //   696: getfield 866	android/appwidget/AppWidgetProviderInfo:minHeight	I
    //   699: istore_3
    //   700: goto -274 -> 426
    //   703: astore_1
    //   704: aload 7
    //   706: astore 6
    //   708: aload 6
    //   710: ifnull +10 -> 720
    //   713: aload 6
    //   715: invokeinterface 1343 1 0
    //   720: aload_1
    //   721: athrow
    //   722: astore_1
    //   723: goto -15 -> 708
    //   726: astore_2
    //   727: aload 8
    //   729: astore 6
    //   731: goto -117 -> 614
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	734	0	this	AppWidgetServiceImpl
    //   0	734	1	paramProviderId	ProviderId
    //   0	734	2	paramResolveInfo	ResolveInfo
    //   117	583	3	i	int
    //   275	332	4	l	long
    //   27	703	6	localObject1	Object
    //   10	695	7	localObject2	Object
    //   7	721	8	localObject3	Object
    //   4	502	9	localActivityInfo	ActivityInfo
    //   232	371	10	localProvider	Provider
    //   100	396	11	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   277	313	605	finally
    //   234	277	613	java/io/IOException
    //   234	277	613	android/content/pm/PackageManager$NameNotFoundException
    //   234	277	613	org/xmlpull/v1/XmlPullParserException
    //   313	338	613	java/io/IOException
    //   313	338	613	android/content/pm/PackageManager$NameNotFoundException
    //   313	338	613	org/xmlpull/v1/XmlPullParserException
    //   343	349	613	java/io/IOException
    //   343	349	613	android/content/pm/PackageManager$NameNotFoundException
    //   343	349	613	org/xmlpull/v1/XmlPullParserException
    //   349	363	613	java/io/IOException
    //   349	363	613	android/content/pm/PackageManager$NameNotFoundException
    //   349	363	613	org/xmlpull/v1/XmlPullParserException
    //   368	374	613	java/io/IOException
    //   368	374	613	android/content/pm/PackageManager$NameNotFoundException
    //   368	374	613	org/xmlpull/v1/XmlPullParserException
    //   374	389	613	java/io/IOException
    //   374	389	613	android/content/pm/PackageManager$NameNotFoundException
    //   374	389	613	org/xmlpull/v1/XmlPullParserException
    //   394	400	613	java/io/IOException
    //   394	400	613	android/content/pm/PackageManager$NameNotFoundException
    //   394	400	613	org/xmlpull/v1/XmlPullParserException
    //   400	415	613	java/io/IOException
    //   400	415	613	android/content/pm/PackageManager$NameNotFoundException
    //   400	415	613	org/xmlpull/v1/XmlPullParserException
    //   420	426	613	java/io/IOException
    //   420	426	613	android/content/pm/PackageManager$NameNotFoundException
    //   420	426	613	org/xmlpull/v1/XmlPullParserException
    //   426	477	613	java/io/IOException
    //   426	477	613	android/content/pm/PackageManager$NameNotFoundException
    //   426	477	613	org/xmlpull/v1/XmlPullParserException
    //   482	503	613	java/io/IOException
    //   482	503	613	android/content/pm/PackageManager$NameNotFoundException
    //   482	503	613	org/xmlpull/v1/XmlPullParserException
    //   503	590	613	java/io/IOException
    //   503	590	613	android/content/pm/PackageManager$NameNotFoundException
    //   503	590	613	org/xmlpull/v1/XmlPullParserException
    //   606	613	613	java/io/IOException
    //   606	613	613	android/content/pm/PackageManager$NameNotFoundException
    //   606	613	613	org/xmlpull/v1/XmlPullParserException
    //   685	691	613	java/io/IOException
    //   685	691	613	android/content/pm/PackageManager$NameNotFoundException
    //   685	691	613	org/xmlpull/v1/XmlPullParserException
    //   694	700	613	java/io/IOException
    //   694	700	613	android/content/pm/PackageManager$NameNotFoundException
    //   694	700	613	org/xmlpull/v1/XmlPullParserException
    //   12	29	703	finally
    //   42	73	703	finally
    //   95	102	703	finally
    //   110	118	703	finally
    //   136	152	703	finally
    //   160	202	703	finally
    //   224	234	703	finally
    //   618	661	703	finally
    //   234	277	722	finally
    //   313	338	722	finally
    //   343	349	722	finally
    //   349	363	722	finally
    //   368	374	722	finally
    //   374	389	722	finally
    //   394	400	722	finally
    //   400	415	722	finally
    //   420	426	722	finally
    //   426	477	722	finally
    //   482	503	722	finally
    //   503	590	722	finally
    //   606	613	722	finally
    //   685	691	722	finally
    //   694	700	722	finally
    //   12	29	726	java/io/IOException
    //   12	29	726	android/content/pm/PackageManager$NameNotFoundException
    //   12	29	726	org/xmlpull/v1/XmlPullParserException
    //   42	73	726	java/io/IOException
    //   42	73	726	android/content/pm/PackageManager$NameNotFoundException
    //   42	73	726	org/xmlpull/v1/XmlPullParserException
    //   95	102	726	java/io/IOException
    //   95	102	726	android/content/pm/PackageManager$NameNotFoundException
    //   95	102	726	org/xmlpull/v1/XmlPullParserException
    //   110	118	726	java/io/IOException
    //   110	118	726	android/content/pm/PackageManager$NameNotFoundException
    //   110	118	726	org/xmlpull/v1/XmlPullParserException
    //   136	152	726	java/io/IOException
    //   136	152	726	android/content/pm/PackageManager$NameNotFoundException
    //   136	152	726	org/xmlpull/v1/XmlPullParserException
    //   160	202	726	java/io/IOException
    //   160	202	726	android/content/pm/PackageManager$NameNotFoundException
    //   160	202	726	org/xmlpull/v1/XmlPullParserException
    //   224	234	726	java/io/IOException
    //   224	234	726	android/content/pm/PackageManager$NameNotFoundException
    //   224	234	726	org/xmlpull/v1/XmlPullParserException
  }
  
  private int peekNextAppWidgetIdLocked(int paramInt)
  {
    if (this.mNextAppWidgetIds.indexOfKey(paramInt) < 0) {
      return 1;
    }
    return this.mNextAppWidgetIds.get(paramInt);
  }
  
  private void performUpgradeLocked(int paramInt)
  {
    if (paramInt < 1) {
      Slog.v("AppWidgetServiceImpl", "Upgrading widget database from " + paramInt + " to " + 1);
    }
    int i = paramInt;
    if (paramInt == 0)
    {
      Host localHost = lookupHostLocked(new HostId(Process.myUid(), 1262836039, "android"));
      if (localHost != null)
      {
        paramInt = getUidForPackage("com.android.keyguard", 0);
        if (paramInt >= 0) {
          localHost.id = new HostId(paramInt, 1262836039, "com.android.keyguard");
        }
      }
      i = 1;
    }
    if (i != 1) {
      throw new IllegalStateException("Failed to upgrade widget database");
    }
  }
  
  private void pruneHostLocked(Host paramHost)
  {
    if ((paramHost.widgets.size() == 0) && (paramHost.callbacks == null))
    {
      if (DEBUG) {
        Slog.i("AppWidgetServiceImpl", "Pruning host " + paramHost.id);
      }
      this.mHosts.remove(paramHost);
    }
  }
  
  private List<ResolveInfo> queryIntentReceivers(Intent paramIntent, int paramInt)
  {
    long l = Binder.clearCallingIdentity();
    int i = 268435584;
    try
    {
      if (isProfileWithUnlockedParent(paramInt)) {
        i = 0x10000080 | 0xC0000;
      }
      paramIntent = this.mPackageManager.queryIntentReceivers(paramIntent, paramIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), i | 0x400, paramInt).getList();
      return paramIntent;
    }
    catch (RemoteException paramIntent)
    {
      paramIntent = Collections.emptyList();
      return paramIntent;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private int readProfileStateFromFileLocked(FileInputStream paramFileInputStream, int paramInt, List<LoadedWidgetState> paramList)
  {
    int j = -1;
    XmlPullParser localXmlPullParser;
    int k;
    int m;
    int i1;
    Object localObject1;
    int i3;
    do
    {
      for (;;)
      {
        try
        {
          localXmlPullParser = Xml.newPullParser();
          localXmlPullParser.setInput(paramFileInputStream, StandardCharsets.UTF_8.name());
          k = -1;
          m = -1;
          int i2 = localXmlPullParser.next();
          n = m;
          i1 = k;
          i = j;
          if (i2 == 2)
          {
            paramFileInputStream = localXmlPullParser.getName();
            if ("gs".equals(paramFileInputStream)) {
              paramFileInputStream = localXmlPullParser.getAttributeValue(null, "version");
            }
          }
          else
          {
            try
            {
              i = Integer.parseInt(paramFileInputStream);
              i1 = k;
              n = m;
            }
            catch (NumberFormatException paramFileInputStream)
            {
              i = 0;
              n = m;
              i1 = k;
              continue;
            }
            m = n;
            k = i1;
            j = i;
            if (i2 != 1) {
              continue;
            }
            return i;
          }
          if (!"p".equals(paramFileInputStream)) {
            break;
          }
          k += 1;
          localObject1 = localXmlPullParser.getAttributeValue(null, "pkg");
          paramFileInputStream = localXmlPullParser.getAttributeValue(null, "cl");
          localObject1 = getCanonicalPackageName((String)localObject1, paramFileInputStream, paramInt);
          n = m;
          i1 = k;
          i = j;
          if (localObject1 != null)
          {
            i3 = getUidForPackage((String)localObject1, paramInt);
            n = m;
            i1 = k;
            i = j;
            if (i3 >= 0)
            {
              paramFileInputStream = new ComponentName((String)localObject1, paramFileInputStream);
              localObject2 = getProviderInfo(paramFileInputStream, paramInt);
              n = m;
              i1 = k;
              i = j;
              if (localObject2 != null)
              {
                ProviderId localProviderId = new ProviderId(i3, paramFileInputStream, null);
                localObject1 = lookupProviderLocked(localProviderId);
                paramFileInputStream = (FileInputStream)localObject1;
                if (localObject1 == null)
                {
                  paramFileInputStream = (FileInputStream)localObject1;
                  if (this.mSafeMode)
                  {
                    paramFileInputStream = new Provider(null);
                    paramFileInputStream.info = new AppWidgetProviderInfo();
                    paramFileInputStream.info.provider = localProviderId.componentName;
                    paramFileInputStream.info.providerInfo = ((ActivityInfo)localObject2);
                    paramFileInputStream.zombie = true;
                    paramFileInputStream.id = localProviderId;
                    this.mProviders.add(paramFileInputStream);
                  }
                }
                localObject1 = localXmlPullParser.getAttributeValue(null, "tag");
                if (!TextUtils.isEmpty((CharSequence)localObject1))
                {
                  i = Integer.parseInt((String)localObject1, 16);
                  paramFileInputStream.tag = i;
                  n = m;
                  i1 = k;
                  i = j;
                }
                else
                {
                  i = k;
                }
              }
            }
          }
        }
        catch (NullPointerException|NumberFormatException|XmlPullParserException|IOException|IndexOutOfBoundsException paramFileInputStream)
        {
          Slog.w("AppWidgetServiceImpl", "failed parsing " + paramFileInputStream);
          return -1;
        }
      }
      if (!"h".equals(paramFileInputStream)) {
        break label630;
      }
      m += 1;
      paramFileInputStream = new Host(null);
      localObject1 = localXmlPullParser.getAttributeValue(null, "pkg");
      i3 = getUidForPackage((String)localObject1, paramInt);
      if (i3 < 0) {
        paramFileInputStream.zombie = true;
      }
      if (!paramFileInputStream.zombie) {
        break;
      }
      n = m;
      i1 = k;
      i = j;
    } while (!this.mSafeMode);
    int n = Integer.parseInt(localXmlPullParser.getAttributeValue(null, "id"), 16);
    Object localObject2 = localXmlPullParser.getAttributeValue(null, "tag");
    if (!TextUtils.isEmpty((CharSequence)localObject2)) {}
    for (int i = Integer.parseInt((String)localObject2, 16);; i = m)
    {
      paramFileInputStream.tag = i;
      paramFileInputStream.id = new HostId(i3, n, (String)localObject1);
      this.mHosts.add(paramFileInputStream);
      n = m;
      i1 = k;
      i = j;
      break;
      label630:
      if ("b".equals(paramFileInputStream))
      {
        paramFileInputStream = localXmlPullParser.getAttributeValue(null, "packageName");
        n = m;
        i1 = k;
        i = j;
        if (getUidForPackage(paramFileInputStream, paramInt) < 0) {
          break;
        }
        paramFileInputStream = Pair.create(Integer.valueOf(paramInt), paramFileInputStream);
        this.mPackagesWithBindWidgetPermission.add(paramFileInputStream);
        n = m;
        i1 = k;
        i = j;
        break;
      }
      n = m;
      i1 = k;
      i = j;
      if (!"g".equals(paramFileInputStream)) {
        break;
      }
      paramFileInputStream = new Widget(null);
      paramFileInputStream.appWidgetId = Integer.parseInt(localXmlPullParser.getAttributeValue(null, "id"), 16);
      setMinAppWidgetIdLocked(paramInt, paramFileInputStream.appWidgetId + 1);
      localObject1 = localXmlPullParser.getAttributeValue(null, "rid");
      if (localObject1 == null)
      {
        i = 0;
        label789:
        paramFileInputStream.restoredId = i;
        localObject1 = new Bundle();
        localObject2 = localXmlPullParser.getAttributeValue(null, "min_width");
        if (localObject2 != null) {
          ((Bundle)localObject1).putInt("appWidgetMinWidth", Integer.parseInt((String)localObject2, 16));
        }
        localObject2 = localXmlPullParser.getAttributeValue(null, "min_height");
        if (localObject2 != null) {
          ((Bundle)localObject1).putInt("appWidgetMinHeight", Integer.parseInt((String)localObject2, 16));
        }
        localObject2 = localXmlPullParser.getAttributeValue(null, "max_width");
        if (localObject2 != null) {
          ((Bundle)localObject1).putInt("appWidgetMaxWidth", Integer.parseInt((String)localObject2, 16));
        }
        localObject2 = localXmlPullParser.getAttributeValue(null, "max_height");
        if (localObject2 != null) {
          ((Bundle)localObject1).putInt("appWidgetMaxHeight", Integer.parseInt((String)localObject2, 16));
        }
        localObject2 = localXmlPullParser.getAttributeValue(null, "host_category");
        if (localObject2 != null) {
          ((Bundle)localObject1).putInt("appWidgetCategory", Integer.parseInt((String)localObject2, 16));
        }
        paramFileInputStream.options = ((Bundle)localObject1);
        n = Integer.parseInt(localXmlPullParser.getAttributeValue(null, "h"), 16);
        if (localXmlPullParser.getAttributeValue(null, "p") == null) {
          break label1072;
        }
      }
      label1072:
      for (i = Integer.parseInt(localXmlPullParser.getAttributeValue(null, "p"), 16);; i = -1)
      {
        paramList.add(new LoadedWidgetState(paramFileInputStream, n, i));
        n = m;
        i1 = k;
        i = j;
        break;
        i = Integer.parseInt((String)localObject1, 16);
        break label789;
      }
    }
  }
  
  private void registerBroadcastReceiver()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
    this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, localIntentFilter, null, null);
    localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addDataScheme("package");
    this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, localIntentFilter, null, null);
    localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
    localIntentFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
    this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, localIntentFilter, null, null);
    localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
    localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
    this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, localIntentFilter, null, null);
    localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGES_SUSPENDED");
    localIntentFilter.addAction("android.intent.action.PACKAGES_UNSUSPENDED");
    this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, localIntentFilter, null, null);
  }
  
  private void registerForBroadcastsLocked(Provider paramProvider, int[] paramArrayOfInt)
  {
    if (paramProvider.info.updatePeriodMillis > 0) {
      if (paramProvider.broadcast == null) {
        break label153;
      }
    }
    for (int i = 1;; i = 0)
    {
      Intent localIntent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
      localIntent.putExtra("appWidgetIds", paramArrayOfInt);
      localIntent.setComponent(paramProvider.info.provider);
      long l1 = Binder.clearCallingIdentity();
      long l2;
      try
      {
        paramProvider.broadcast = PendingIntent.getBroadcastAsUser(this.mContext, 1, localIntent, 134217728, paramProvider.info.getProfile());
        Binder.restoreCallingIdentity(l1);
        if (i == 0)
        {
          l2 = paramProvider.info.updatePeriodMillis;
          l1 = l2;
          if (l2 < MIN_UPDATE_PERIOD) {
            l1 = MIN_UPDATE_PERIOD;
          }
          l2 = Binder.clearCallingIdentity();
        }
      }
      finally
      {
        label153:
        Binder.restoreCallingIdentity(l1);
      }
      try
      {
        this.mAlarmManager.setInexactRepeating(2, SystemClock.elapsedRealtime() + l1, l1, paramProvider.broadcast);
        Binder.restoreCallingIdentity(l2);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l2);
      }
    }
  }
  
  private void registerOnCrossProfileProvidersChangedListener()
  {
    if (this.mDevicePolicyManagerInternal != null) {
      this.mDevicePolicyManagerInternal.addOnCrossProfileWidgetProvidersChangeListener(this);
    }
  }
  
  /* Error */
  private void reloadWidgetsMaskedState(int paramInt)
  {
    // Byte code:
    //   0: invokestatic 562	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore 5
    //   5: aload_0
    //   6: getfield 146	com/android/server/appwidget/AppWidgetServiceImpl:mUserManager	Landroid/os/UserManager;
    //   9: iload_1
    //   10: invokevirtual 1108	android/os/UserManager:getUserInfo	(I)Landroid/content/pm/UserInfo;
    //   13: astore 11
    //   15: aload_0
    //   16: getfield 146	com/android/server/appwidget/AppWidgetServiceImpl:mUserManager	Landroid/os/UserManager;
    //   19: iload_1
    //   20: invokevirtual 1257	android/os/UserManager:isUserUnlockingOrUnlocked	(I)Z
    //   23: ifeq +198 -> 221
    //   26: iconst_0
    //   27: istore 7
    //   29: aload 11
    //   31: invokevirtual 1658	android/content/pm/UserInfo:isQuietModeEnabled	()Z
    //   34: istore 9
    //   36: aload_0
    //   37: getfield 176	com/android/server/appwidget/AppWidgetServiceImpl:mProviders	Ljava/util/ArrayList;
    //   40: invokevirtual 589	java/util/ArrayList:size	()I
    //   43: istore 4
    //   45: iconst_0
    //   46: istore_2
    //   47: iload_2
    //   48: iload 4
    //   50: if_icmpge +158 -> 208
    //   53: aload_0
    //   54: getfield 176	com/android/server/appwidget/AppWidgetServiceImpl:mProviders	Ljava/util/ArrayList;
    //   57: iload_2
    //   58: invokevirtual 592	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   61: checkcast 34	com/android/server/appwidget/AppWidgetServiceImpl$Provider
    //   64: astore 11
    //   66: aload 11
    //   68: invokevirtual 1199	com/android/server/appwidget/AppWidgetServiceImpl$Provider:getUserId	()I
    //   71: iload_1
    //   72: if_icmpeq +6 -> 78
    //   75: goto +139 -> 214
    //   78: aload 11
    //   80: iload 7
    //   82: invokevirtual 1662	com/android/server/appwidget/AppWidgetServiceImpl$Provider:setMaskedByLockedProfileLocked	(Z)Z
    //   85: istore 8
    //   87: aload 11
    //   89: iload 9
    //   91: invokevirtual 1665	com/android/server/appwidget/AppWidgetServiceImpl$Provider:setMaskedByQuietProfileLocked	(Z)Z
    //   94: istore 10
    //   96: iload 8
    //   98: iload 10
    //   100: ior
    //   101: istore_3
    //   102: aload_0
    //   103: getfield 169	com/android/server/appwidget/AppWidgetServiceImpl:mPackageManager	Landroid/content/pm/IPackageManager;
    //   106: aload 11
    //   108: getfield 481	com/android/server/appwidget/AppWidgetServiceImpl$Provider:info	Landroid/appwidget/AppWidgetProviderInfo;
    //   111: getfield 1197	android/appwidget/AppWidgetProviderInfo:provider	Landroid/content/ComponentName;
    //   114: invokevirtual 1198	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   117: aload 11
    //   119: invokevirtual 1199	com/android/server/appwidget/AppWidgetServiceImpl$Provider:getUserId	()I
    //   122: invokeinterface 1668 3 0
    //   127: istore 8
    //   129: aload 11
    //   131: iload 8
    //   133: invokevirtual 1671	com/android/server/appwidget/AppWidgetServiceImpl$Provider:setMaskedBySuspendedPackageLocked	(Z)Z
    //   136: istore 8
    //   138: iload_3
    //   139: iload 8
    //   141: ior
    //   142: istore_3
    //   143: iload_3
    //   144: ifeq +70 -> 214
    //   147: aload 11
    //   149: invokevirtual 1674	com/android/server/appwidget/AppWidgetServiceImpl$Provider:isMaskedLocked	()Z
    //   152: ifeq +47 -> 199
    //   155: aload_0
    //   156: aload 11
    //   158: aconst_null
    //   159: invokespecial 1676	com/android/server/appwidget/AppWidgetServiceImpl:maskWidgetsViewsLocked	(Lcom/android/server/appwidget/AppWidgetServiceImpl$Provider;Lcom/android/server/appwidget/AppWidgetServiceImpl$Widget;)V
    //   162: goto +52 -> 214
    //   165: astore 11
    //   167: lload 5
    //   169: invokestatic 571	android/os/Binder:restoreCallingIdentity	(J)V
    //   172: aload 11
    //   174: athrow
    //   175: astore 12
    //   177: iconst_0
    //   178: istore 8
    //   180: goto -51 -> 129
    //   183: astore 12
    //   185: ldc 77
    //   187: ldc_w 1678
    //   190: aload 12
    //   192: invokestatic 679	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   195: pop
    //   196: goto -53 -> 143
    //   199: aload_0
    //   200: aload 11
    //   202: invokespecial 1681	com/android/server/appwidget/AppWidgetServiceImpl:unmaskWidgetsViewsLocked	(Lcom/android/server/appwidget/AppWidgetServiceImpl$Provider;)V
    //   205: goto +9 -> 214
    //   208: lload 5
    //   210: invokestatic 571	android/os/Binder:restoreCallingIdentity	(J)V
    //   213: return
    //   214: iload_2
    //   215: iconst_1
    //   216: iadd
    //   217: istore_2
    //   218: goto -171 -> 47
    //   221: iconst_1
    //   222: istore 7
    //   224: goto -195 -> 29
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	227	0	this	AppWidgetServiceImpl
    //   0	227	1	paramInt	int
    //   46	172	2	i	int
    //   101	43	3	bool1	boolean
    //   43	8	4	j	int
    //   3	206	5	l	long
    //   27	196	7	bool2	boolean
    //   85	94	8	bool3	boolean
    //   34	56	9	bool4	boolean
    //   94	7	10	bool5	boolean
    //   13	144	11	localObject	Object
    //   165	36	11	localProvider	Provider
    //   175	1	12	localIllegalArgumentException	IllegalArgumentException
    //   183	8	12	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   5	26	165	finally
    //   29	45	165	finally
    //   53	75	165	finally
    //   78	96	165	finally
    //   102	129	165	finally
    //   129	138	165	finally
    //   147	162	165	finally
    //   185	196	165	finally
    //   199	205	165	finally
    //   102	129	175	java/lang/IllegalArgumentException
    //   102	129	183	android/os/RemoteException
    //   129	138	183	android/os/RemoteException
  }
  
  private boolean removeHostsAndProvidersForPackageLocked(String paramString, int paramInt)
  {
    boolean bool1 = removeProvidersForPackageLocked(paramString, paramInt);
    int i = this.mHosts.size() - 1;
    while (i >= 0)
    {
      Host localHost = (Host)this.mHosts.get(i);
      boolean bool2 = bool1;
      if (paramString.equals(localHost.id.packageName))
      {
        bool2 = bool1;
        if (localHost.getUserId() == paramInt)
        {
          deleteHostLocked(localHost);
          bool2 = true;
        }
      }
      i -= 1;
      bool1 = bool2;
    }
    return bool1;
  }
  
  private boolean removeProvidersForPackageLocked(String paramString, int paramInt)
  {
    boolean bool1 = false;
    int i = this.mProviders.size() - 1;
    while (i >= 0)
    {
      Provider localProvider = (Provider)this.mProviders.get(i);
      boolean bool2 = bool1;
      if (paramString.equals(localProvider.info.provider.getPackageName()))
      {
        bool2 = bool1;
        if (localProvider.getUserId() == paramInt)
        {
          deleteProviderLocked(localProvider);
          bool2 = true;
        }
      }
      i -= 1;
      bool1 = bool2;
    }
    return bool1;
  }
  
  private void removeWidgetsForPackageLocked(String paramString, int paramInt1, int paramInt2)
  {
    int j = this.mProviders.size();
    int i = 0;
    while (i < j)
    {
      Provider localProvider = (Provider)this.mProviders.get(i);
      if ((paramString.equals(localProvider.info.provider.getPackageName())) && (localProvider.getUserId() == paramInt1) && (localProvider.widgets.size() > 0)) {
        deleteWidgetsLocked(localProvider, paramInt2);
      }
      i += 1;
    }
  }
  
  private void resolveHostUidLocked(String paramString, int paramInt)
  {
    int j = this.mHosts.size();
    int i = 0;
    while (i < j)
    {
      Host localHost = (Host)this.mHosts.get(i);
      if ((localHost.id.uid == -1) && (paramString.equals(localHost.id.packageName)))
      {
        if (DEBUG) {
          Slog.i("AppWidgetServiceImpl", "host " + localHost.id + " resolved to uid " + paramInt);
        }
        localHost.id = new HostId(paramInt, localHost.id.hostId, localHost.id.packageName);
        return;
      }
      i += 1;
    }
  }
  
  private void saveGroupStateAsync(int paramInt)
  {
    if (this.mSafeMode) {
      return;
    }
    this.mSaveStateHandler.post(new SaveStateRunnable(paramInt));
  }
  
  private void saveStateLocked(int paramInt)
  {
    tagProvidersAndHosts();
    int[] arrayOfInt = this.mSecurityPolicy.getEnabledGroupProfileIds(paramInt);
    int i = arrayOfInt.length;
    paramInt = 0;
    for (;;)
    {
      if (paramInt < i)
      {
        int j = arrayOfInt[paramInt];
        AtomicFile localAtomicFile = getSavedStateFile(j);
        try
        {
          FileOutputStream localFileOutputStream = localAtomicFile.startWrite();
          if (writeProfileStateToFileLocked(localFileOutputStream, j))
          {
            localAtomicFile.finishWrite(localFileOutputStream);
          }
          else
          {
            localAtomicFile.failWrite(localFileOutputStream);
            Slog.w("AppWidgetServiceImpl", "Failed to save state, restoring backup.");
          }
        }
        catch (IOException localIOException)
        {
          Slog.w("AppWidgetServiceImpl", "Failed open state file for write: " + localIOException);
        }
      }
      return;
      paramInt += 1;
    }
  }
  
  private void scheduleNotifyAppWidgetViewDataChanged(Widget paramWidget, int paramInt)
  {
    if ((paramInt == 0) || (paramInt == 1)) {
      return;
    }
    long l = SystemClock.uptimeMillis();
    if (paramWidget != null) {
      paramWidget.updateTimes.put(paramInt, l);
    }
    if ((paramWidget == null) || (paramWidget.host == null)) {}
    while ((paramWidget.host.zombie) || (paramWidget.host.callbacks == null) || (paramWidget.provider == null) || (paramWidget.provider.zombie)) {
      return;
    }
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.arg1 = paramWidget.host;
    localSomeArgs.arg2 = paramWidget.host.callbacks;
    localSomeArgs.arg3 = Long.valueOf(l);
    localSomeArgs.argi1 = paramWidget.appWidgetId;
    localSomeArgs.argi2 = paramInt;
    this.mCallbackHandler.obtainMessage(4, localSomeArgs).sendToTarget();
  }
  
  private void scheduleNotifyGroupHostsForProvidersChangedLocked(int paramInt)
  {
    int[] arrayOfInt = this.mSecurityPolicy.getEnabledGroupProfileIds(paramInt);
    paramInt = this.mHosts.size() - 1;
    if (paramInt >= 0)
    {
      Host localHost = (Host)this.mHosts.get(paramInt);
      int k = 0;
      int m = arrayOfInt.length;
      int i = 0;
      label47:
      int j = k;
      if (i < m)
      {
        j = arrayOfInt[i];
        if (localHost.getUserId() == j) {
          j = 1;
        }
      }
      else
      {
        if (j != 0) {
          break label90;
        }
      }
      for (;;)
      {
        paramInt -= 1;
        break;
        i += 1;
        break label47;
        label90:
        if ((localHost != null) && (!localHost.zombie) && (localHost.callbacks != null))
        {
          SomeArgs localSomeArgs = SomeArgs.obtain();
          localSomeArgs.arg1 = localHost;
          localSomeArgs.arg2 = localHost.callbacks;
          this.mCallbackHandler.obtainMessage(3, localSomeArgs).sendToTarget();
        }
      }
    }
  }
  
  private void scheduleNotifyProviderChangedLocked(Widget paramWidget)
  {
    long l = SystemClock.uptimeMillis();
    if (paramWidget != null)
    {
      paramWidget.updateTimes.clear();
      paramWidget.updateTimes.append(1, l);
    }
    if ((paramWidget == null) || (paramWidget.provider == null)) {}
    while ((paramWidget.provider.zombie) || (paramWidget.host.callbacks == null) || (paramWidget.host.zombie)) {
      return;
    }
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.arg1 = paramWidget.host;
    localSomeArgs.arg2 = paramWidget.host.callbacks;
    localSomeArgs.arg3 = paramWidget.provider.info;
    localSomeArgs.arg4 = Long.valueOf(l);
    localSomeArgs.argi1 = paramWidget.appWidgetId;
    this.mCallbackHandler.obtainMessage(2, localSomeArgs).sendToTarget();
  }
  
  private void scheduleNotifyUpdateAppWidgetLocked(Widget paramWidget, RemoteViews paramRemoteViews)
  {
    RemoteViews localRemoteViews = null;
    long l = SystemClock.uptimeMillis();
    if (paramWidget != null) {
      paramWidget.updateTimes.put(0, l);
    }
    if ((paramWidget == null) || (paramWidget.provider == null)) {}
    while ((paramWidget.provider.zombie) || (paramWidget.host.callbacks == null) || (paramWidget.host.zombie)) {
      return;
    }
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.arg1 = paramWidget.host;
    localSomeArgs.arg2 = paramWidget.host.callbacks;
    if (paramRemoteViews != null) {
      localRemoteViews = paramRemoteViews.clone();
    }
    localSomeArgs.arg3 = localRemoteViews;
    localSomeArgs.arg4 = Long.valueOf(l);
    localSomeArgs.argi1 = paramWidget.appWidgetId;
    this.mCallbackHandler.obtainMessage(1, localSomeArgs).sendToTarget();
  }
  
  private void sendBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mContext.sendBroadcastAsUser(paramIntent, paramUserHandle);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void sendDeletedIntentLocked(Widget paramWidget)
  {
    Intent localIntent = new Intent("android.appwidget.action.APPWIDGET_DELETED");
    localIntent.setComponent(paramWidget.provider.info.provider);
    localIntent.putExtra("appWidgetId", paramWidget.appWidgetId);
    sendBroadcastAsUser(localIntent, paramWidget.provider.info.getProfile());
  }
  
  private void sendDisabledIntentLocked(Provider paramProvider)
  {
    Intent localIntent = new Intent("android.appwidget.action.APPWIDGET_DISABLED");
    localIntent.setComponent(paramProvider.info.provider);
    sendBroadcastAsUser(localIntent, paramProvider.info.getProfile());
  }
  
  private void sendEnableIntentLocked(Provider paramProvider)
  {
    Intent localIntent = new Intent("android.appwidget.action.APPWIDGET_ENABLED");
    localIntent.setComponent(paramProvider.info.provider);
    sendBroadcastAsUser(localIntent, paramProvider.info.getProfile());
  }
  
  private void sendUpdateIntentLocked(Provider paramProvider, int[] paramArrayOfInt)
  {
    Intent localIntent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
    localIntent.putExtra("appWidgetIds", paramArrayOfInt);
    localIntent.setComponent(paramProvider.info.provider);
    sendBroadcastAsUser(localIntent, paramProvider.info.getProfile());
  }
  
  private static void serializeAppWidget(XmlSerializer paramXmlSerializer, Widget paramWidget)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "g");
    paramXmlSerializer.attribute(null, "id", Integer.toHexString(paramWidget.appWidgetId));
    paramXmlSerializer.attribute(null, "rid", Integer.toHexString(paramWidget.restoredId));
    paramXmlSerializer.attribute(null, "h", Integer.toHexString(paramWidget.host.tag));
    if (paramWidget.provider != null) {
      paramXmlSerializer.attribute(null, "p", Integer.toHexString(paramWidget.provider.tag));
    }
    if (paramWidget.options != null)
    {
      paramXmlSerializer.attribute(null, "min_width", Integer.toHexString(paramWidget.options.getInt("appWidgetMinWidth")));
      paramXmlSerializer.attribute(null, "min_height", Integer.toHexString(paramWidget.options.getInt("appWidgetMinHeight")));
      paramXmlSerializer.attribute(null, "max_width", Integer.toHexString(paramWidget.options.getInt("appWidgetMaxWidth")));
      paramXmlSerializer.attribute(null, "max_height", Integer.toHexString(paramWidget.options.getInt("appWidgetMaxHeight")));
      paramXmlSerializer.attribute(null, "host_category", Integer.toHexString(paramWidget.options.getInt("appWidgetCategory")));
    }
    paramXmlSerializer.endTag(null, "g");
  }
  
  private static void serializeHost(XmlSerializer paramXmlSerializer, Host paramHost)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "h");
    paramXmlSerializer.attribute(null, "pkg", paramHost.id.packageName);
    paramXmlSerializer.attribute(null, "id", Integer.toHexString(paramHost.id.hostId));
    paramXmlSerializer.attribute(null, "tag", Integer.toHexString(paramHost.tag));
    paramXmlSerializer.endTag(null, "h");
  }
  
  private static void serializeProvider(XmlSerializer paramXmlSerializer, Provider paramProvider)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "p");
    paramXmlSerializer.attribute(null, "pkg", paramProvider.info.provider.getPackageName());
    paramXmlSerializer.attribute(null, "cl", paramProvider.info.provider.getClassName());
    paramXmlSerializer.attribute(null, "tag", Integer.toHexString(paramProvider.tag));
    paramXmlSerializer.endTag(null, "p");
  }
  
  private void setMinAppWidgetIdLocked(int paramInt1, int paramInt2)
  {
    if (peekNextAppWidgetIdLocked(paramInt1) < paramInt2) {
      this.mNextAppWidgetIds.put(paramInt1, paramInt2);
    }
  }
  
  private void tagProvidersAndHosts()
  {
    int j = this.mProviders.size();
    int i = 0;
    while (i < j)
    {
      ((Provider)this.mProviders.get(i)).tag = i;
      i += 1;
    }
    j = this.mHosts.size();
    i = 0;
    while (i < j)
    {
      ((Host)this.mHosts.get(i)).tag = i;
      i += 1;
    }
  }
  
  private void unbindAppWidgetRemoteViewsServicesLocked(Widget paramWidget)
  {
    int i = paramWidget.appWidgetId;
    Iterator localIterator = this.mBoundRemoteViewsServices.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (Pair)localIterator.next();
      if (((Integer)((Pair)localObject).first).intValue() == i)
      {
        localObject = (ServiceConnectionProxy)this.mBoundRemoteViewsServices.get(localObject);
        ((ServiceConnectionProxy)localObject).disconnect();
        this.mContext.unbindService((ServiceConnection)localObject);
        localIterator.remove();
      }
    }
    decrementAppWidgetServiceRefCount(paramWidget);
  }
  
  private void unbindService(ServiceConnection paramServiceConnection)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mContext.unbindService(paramServiceConnection);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void unmaskWidgetsViewsLocked(Provider paramProvider)
  {
    int j = paramProvider.widgets.size();
    int i = 0;
    while (i < j)
    {
      Widget localWidget = (Widget)paramProvider.widgets.get(i);
      if (Widget.-wrap0(localWidget)) {
        scheduleNotifyUpdateAppWidgetLocked(localWidget, localWidget.getEffectiveViewsLocked());
      }
      i += 1;
    }
  }
  
  private void updateAppWidgetIds(String paramString, int[] paramArrayOfInt, RemoteViews paramRemoteViews, boolean paramBoolean)
  {
    int i = UserHandle.getCallingUserId();
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length == 0)) {
      return;
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      int j = paramArrayOfInt.length;
      i = 0;
      while (i < j)
      {
        Widget localWidget = lookupWidgetLocked(paramArrayOfInt[i], Binder.getCallingUid(), paramString);
        if (localWidget != null) {
          updateAppWidgetInstanceLocked(localWidget, paramRemoteViews, paramBoolean);
        }
        i += 1;
      }
      return;
    }
  }
  
  private void updateAppWidgetInstanceLocked(Widget paramWidget, RemoteViews paramRemoteViews, boolean paramBoolean)
  {
    if ((paramWidget == null) || (paramWidget.provider == null) || (paramWidget.provider.zombie)) {}
    while (paramWidget.host.zombie) {
      return;
    }
    String str;
    if ((paramBoolean) && (paramWidget.views != null))
    {
      paramWidget.views.mergeRemoteViews(paramRemoteViews);
      if (paramRemoteViews == null) {
        break label257;
      }
      str = paramRemoteViews.getPackage();
      label61:
      if (paramWidget.views == null) {
        break label265;
      }
    }
    label257:
    label265:
    for (int i = paramWidget.views.estimateMemoryUsage(true);; i = 0)
    {
      if (DEBUG_ONEPLUS) {
        Slog.d("AppWidgetServiceImpl", "updateAppWidgetInstanceLocked: " + str + ", usage: " + i + "/" + this.mMaxWidgetBitmapMemory);
      }
      if ((UserHandle.getAppId(Binder.getCallingUid()) != 1000) && (paramWidget.views != null) && (i > this.mMaxWidgetBitmapMemory))
      {
        paramWidget.views = null;
        Slog.w("AppWidgetServiceImpl", "RemoteViews for widget[" + str + "] update exceeds" + " maximum bitmap memory usage (used: " + i + ", max: " + this.mMaxWidgetBitmapMemory + "). Release as null here.");
        if (paramRemoteViews != null) {
          paramRemoteViews.clearBitmapCache();
        }
      }
      scheduleNotifyUpdateAppWidgetLocked(paramWidget, paramWidget.getEffectiveViewsLocked());
      return;
      paramWidget.views = paramRemoteViews;
      break;
      str = "";
      break label61;
    }
  }
  
  private boolean updateProvidersForPackageLocked(String paramString, int paramInt, Set<ProviderId> paramSet)
  {
    boolean bool1 = false;
    HashSet localHashSet = new HashSet();
    Object localObject1 = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
    ((Intent)localObject1).setPackage(paramString);
    localObject1 = queryIntentReceivers((Intent)localObject1, paramInt);
    int j;
    label51:
    Object localObject3;
    Object localObject2;
    boolean bool2;
    if (localObject1 == null)
    {
      i = 0;
      j = 0;
      if (j >= i) {
        break label346;
      }
      localObject3 = (ResolveInfo)((List)localObject1).get(j);
      localObject2 = ((ResolveInfo)localObject3).activityInfo;
      if ((((ActivityInfo)localObject2).applicationInfo.flags & 0x40000) == 0) {
        break label123;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      j += 1;
      bool1 = bool2;
      break label51;
      i = ((List)localObject1).size();
      break;
      label123:
      bool2 = bool1;
      if (paramString.equals(((ActivityInfo)localObject2).packageName))
      {
        Object localObject4 = new ProviderId(((ActivityInfo)localObject2).applicationInfo.uid, new ComponentName(((ActivityInfo)localObject2).packageName, ((ActivityInfo)localObject2).name), null);
        localObject2 = lookupProviderLocked((ProviderId)localObject4);
        if (localObject2 == null)
        {
          bool2 = bool1;
          if (addProviderLocked((ResolveInfo)localObject3))
          {
            localHashSet.add(localObject4);
            bool2 = true;
          }
        }
        else
        {
          localObject3 = parseProviderInfoXml((ProviderId)localObject4, (ResolveInfo)localObject3);
          if (localObject3 != null)
          {
            localHashSet.add(localObject4);
            ((Provider)localObject2).info = ((Provider)localObject3).info;
            int m = ((Provider)localObject2).widgets.size();
            if (m > 0)
            {
              localObject3 = getWidgetIds(((Provider)localObject2).widgets);
              cancelBroadcasts((Provider)localObject2);
              registerForBroadcastsLocked((Provider)localObject2, (int[])localObject3);
              int k = 0;
              while (k < m)
              {
                localObject4 = (Widget)((Provider)localObject2).widgets.get(k);
                ((Widget)localObject4).views = null;
                scheduleNotifyProviderChangedLocked((Widget)localObject4);
                k += 1;
              }
              sendUpdateIntentLocked((Provider)localObject2, (int[])localObject3);
            }
          }
          bool2 = true;
        }
      }
    }
    label346:
    int i = this.mProviders.size() - 1;
    if (i >= 0)
    {
      localObject1 = (Provider)this.mProviders.get(i);
      bool2 = bool1;
      if (paramString.equals(((Provider)localObject1).info.provider.getPackageName()))
      {
        bool2 = bool1;
        if (((Provider)localObject1).getUserId() == paramInt) {
          if (!localHashSet.contains(((Provider)localObject1).id)) {
            break label441;
          }
        }
      }
      for (bool2 = bool1;; bool2 = true)
      {
        i -= 1;
        bool1 = bool2;
        break;
        label441:
        if (paramSet != null) {
          paramSet.add(((Provider)localObject1).id);
        }
        deleteProviderLocked((Provider)localObject1);
      }
    }
    return bool1;
  }
  
  private void updateWidgetPackageSuspensionMaskedState(String[] arg1, boolean paramBoolean, int paramInt)
  {
    if (??? == null) {
      return;
    }
    ArraySet localArraySet = new ArraySet(Arrays.asList(???));
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        int j = this.mProviders.size();
        i = 0;
        if (i < j)
        {
          Provider localProvider = (Provider)this.mProviders.get(i);
          if ((localProvider.getUserId() != paramInt) || (!localArraySet.contains(localProvider.info.provider.getPackageName())) || (!localProvider.setMaskedBySuspendedPackageLocked(paramBoolean))) {
            break label134;
          }
          if (localProvider.isMaskedLocked()) {
            maskWidgetsViewsLocked(localProvider, null);
          } else {
            unmaskWidgetsViewsLocked(localProvider);
          }
        }
      }
      return;
      label134:
      i += 1;
    }
  }
  
  private boolean writeProfileStateToFileLocked(FileOutputStream paramFileOutputStream, int paramInt)
  {
    FastXmlSerializer localFastXmlSerializer;
    try
    {
      localFastXmlSerializer = new FastXmlSerializer();
      localFastXmlSerializer.setOutput(paramFileOutputStream, StandardCharsets.UTF_8.name());
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localFastXmlSerializer.startTag(null, "gs");
      localFastXmlSerializer.attribute(null, "version", String.valueOf(1));
      j = this.mProviders.size();
      i = 0;
      if (i < j)
      {
        paramFileOutputStream = (Provider)this.mProviders.get(i);
        if ((paramFileOutputStream.getUserId() != paramInt) || (paramFileOutputStream.widgets.size() <= 0)) {
          break label363;
        }
        serializeProvider(localFastXmlSerializer, paramFileOutputStream);
      }
    }
    catch (IOException paramFileOutputStream)
    {
      Slog.w("AppWidgetServiceImpl", "Failed to write state: " + paramFileOutputStream);
      return false;
    }
    int j = this.mHosts.size();
    int i = 0;
    label162:
    if (i < j)
    {
      paramFileOutputStream = (Host)this.mHosts.get(i);
      if (paramFileOutputStream.getUserId() == paramInt) {
        serializeHost(localFastXmlSerializer, paramFileOutputStream);
      }
    }
    else
    {
      j = this.mWidgets.size();
      i = 0;
    }
    for (;;)
    {
      if (i < j)
      {
        paramFileOutputStream = (Widget)this.mWidgets.get(i);
        if (paramFileOutputStream.host.getUserId() == paramInt) {
          serializeAppWidget(localFastXmlSerializer, paramFileOutputStream);
        }
      }
      else
      {
        paramFileOutputStream = this.mPackagesWithBindWidgetPermission.iterator();
        while (paramFileOutputStream.hasNext())
        {
          Pair localPair = (Pair)paramFileOutputStream.next();
          if (((Integer)localPair.first).intValue() == paramInt)
          {
            localFastXmlSerializer.startTag(null, "b");
            localFastXmlSerializer.attribute(null, "packageName", (String)localPair.second);
            localFastXmlSerializer.endTag(null, "b");
          }
        }
        localFastXmlSerializer.endTag(null, "gs");
        localFastXmlSerializer.endDocument();
        return true;
        label363:
        i += 1;
        break;
        i += 1;
        break label162;
      }
      i += 1;
    }
  }
  
  void addWidgetLocked(Widget paramWidget)
  {
    this.mWidgets.add(paramWidget);
    onWidgetProviderAddedOrChangedLocked(paramWidget);
  }
  
  public int allocateAppWidgetId(String paramString, int paramInt)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "allocateAppWidgetId() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      if (this.mNextAppWidgetIds.indexOfKey(i) < 0) {
        this.mNextAppWidgetIds.put(i, 1);
      }
      int j = incrementAndGetAppWidgetIdLocked(i);
      paramString = lookupOrAddHostLocked(new HostId(Binder.getCallingUid(), paramInt, paramString));
      Widget localWidget = new Widget(null);
      localWidget.appWidgetId = j;
      localWidget.host = paramString;
      localWidget.options = new Bundle();
      paramString.widgets.add(localWidget);
      addWidgetLocked(localWidget);
      saveGroupStateAsync(i);
      if (DEBUG) {
        Slog.i("AppWidgetServiceImpl", "Allocated widget id " + j + " for host " + paramString.id);
      }
      return j;
    }
  }
  
  public boolean bindAppWidgetId(String paramString, int paramInt1, int paramInt2, ComponentName paramComponentName, Bundle paramBundle)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "bindAppWidgetId() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    if (!this.mSecurityPolicy.isEnabledGroupProfile(paramInt2)) {
      return false;
    }
    if (!this.mSecurityPolicy.isProviderInCallerOrInProfileAndWhitelListed(paramComponentName.getPackageName(), paramInt2)) {
      return false;
    }
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      boolean bool = this.mSecurityPolicy.hasCallerBindPermissionOrBindWhiteListedLocked(paramString);
      if (!bool) {
        return false;
      }
      Widget localWidget = lookupWidgetLocked(paramInt1, Binder.getCallingUid(), paramString);
      if (localWidget == null)
      {
        Slog.e("AppWidgetServiceImpl", "Bad widget id " + paramInt1);
        return false;
      }
      if (localWidget.provider != null)
      {
        Slog.e("AppWidgetServiceImpl", "Widget id " + paramInt1 + " already bound to: " + localWidget.provider.id);
        return false;
      }
      int j = getUidForPackage(paramComponentName.getPackageName(), paramInt2);
      if (j < 0)
      {
        Slog.e("AppWidgetServiceImpl", "Package " + paramComponentName.getPackageName() + " not installed " + " for profile " + paramInt2);
        return false;
      }
      Provider localProvider = lookupProviderLocked(new ProviderId(j, paramComponentName, null));
      if (localProvider == null)
      {
        Slog.e("AppWidgetServiceImpl", "No widget provider " + paramComponentName + " for profile " + paramInt2);
        return false;
      }
      if (localProvider.zombie)
      {
        Slog.e("AppWidgetServiceImpl", "Can't bind to a 3rd party provider in safe mode " + localProvider);
        return false;
      }
      localWidget.provider = localProvider;
      if (paramBundle != null)
      {
        paramString = cloneIfLocalBinder(paramBundle);
        localWidget.options = paramString;
        if (!localWidget.options.containsKey("appWidgetCategory")) {
          localWidget.options.putInt("appWidgetCategory", 1);
        }
        localProvider.widgets.add(localWidget);
        onWidgetProviderAddedOrChangedLocked(localWidget);
        if (localProvider.widgets.size() == 1) {
          sendEnableIntentLocked(localProvider);
        }
        sendUpdateIntentLocked(localProvider, new int[] { paramInt1 });
        registerForBroadcastsLocked(localProvider, getWidgetIds(localProvider.widgets));
        saveGroupStateAsync(i);
        if (DEBUG) {
          Slog.i("AppWidgetServiceImpl", "Bound widget " + paramInt1 + " to provider " + localProvider.id);
        }
        return true;
      }
      paramString = new Bundle();
    }
  }
  
  public void bindRemoteViewsService(String paramString, int paramInt, Intent paramIntent, IBinder paramIBinder)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "bindRemoteViewsService() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramString = lookupWidgetLocked(paramInt, Binder.getCallingUid(), paramString);
      if (paramString == null) {
        throw new IllegalArgumentException("Bad widget id");
      }
    }
    if (paramString.provider == null) {
      throw new IllegalArgumentException("No provider for widget " + paramInt);
    }
    Object localObject2 = paramIntent.getComponent();
    Object localObject3 = paramString.provider.id.componentName.getPackageName();
    if (!((ComponentName)localObject2).getPackageName().equals(localObject3)) {
      throw new SecurityException("The taget service not in the same package as the widget provider");
    }
    this.mSecurityPolicy.enforceServiceExistsAndRequiresBindRemoteViewsPermission((ComponentName)localObject2, paramString.provider.getUserId());
    localObject2 = new Intent.FilterComparison(paramIntent);
    localObject3 = Pair.create(Integer.valueOf(paramInt), localObject2);
    if (this.mBoundRemoteViewsServices.containsKey(localObject3))
    {
      ServiceConnectionProxy localServiceConnectionProxy = (ServiceConnectionProxy)this.mBoundRemoteViewsServices.get(localObject3);
      localServiceConnectionProxy.disconnect();
      unbindService(localServiceConnectionProxy);
      this.mBoundRemoteViewsServices.remove(localObject3);
    }
    paramIBinder = new ServiceConnectionProxy(paramIBinder);
    bindService(paramIntent, paramIBinder, paramString.provider.info.getProfile());
    this.mBoundRemoteViewsServices.put(localObject3, paramIBinder);
    incrementAppWidgetServiceRefCount(paramInt, Pair.create(Integer.valueOf(paramString.provider.id.uid), localObject2));
  }
  
  void clearWidgetsLocked()
  {
    this.mWidgets.clear();
    onWidgetsClearedLocked();
  }
  
  public IntentSender createAppWidgetConfigIntentSender(String paramString, int paramInt1, int paramInt2)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "createAppWidgetConfigIntentSender() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramString = lookupWidgetLocked(paramInt1, Binder.getCallingUid(), paramString);
      if (paramString == null) {
        throw new IllegalArgumentException("Bad widget id " + paramInt1);
      }
    }
    paramString = paramString.provider;
    if (paramString == null) {
      throw new IllegalArgumentException("Widget not bound " + paramInt1);
    }
    Intent localIntent = new Intent("android.appwidget.action.APPWIDGET_CONFIGURE");
    localIntent.putExtra("appWidgetId", paramInt1);
    localIntent.setComponent(paramString.info.configure);
    localIntent.setFlags(paramInt2 & 0xFF3C);
    long l = Binder.clearCallingIdentity();
    try
    {
      paramString = PendingIntent.getActivityAsUser(this.mContext, 0, localIntent, 1409286144, null, new UserHandle(paramString.getUserId())).getIntentSender();
      Binder.restoreCallingIdentity(l);
      return paramString;
    }
    finally
    {
      paramString = finally;
      Binder.restoreCallingIdentity(l);
      throw paramString;
    }
  }
  
  public void deleteAllHosts()
  {
    int m = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "deleteAllHosts() " + m);
    }
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        ensureGroupStateLoadedLocked(m);
        int j = 0;
        i = this.mHosts.size() - 1;
        if (i >= 0)
        {
          Host localHost = (Host)this.mHosts.get(i);
          if (localHost.id.uid == Binder.getCallingUid())
          {
            deleteHostLocked(localHost);
            int k = 1;
            j = k;
            if (DEBUG)
            {
              Slog.i("AppWidgetServiceImpl", "Deleted host " + localHost.id);
              j = k;
            }
          }
        }
        else
        {
          if (j != 0) {
            saveGroupStateAsync(m);
          }
          return;
        }
      }
      i -= 1;
    }
  }
  
  public void deleteAppWidgetId(String paramString, int paramInt)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "deleteAppWidgetId() " + paramString + ", userId = " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramString = lookupWidgetLocked(paramInt, Binder.getCallingUid(), paramString);
      if (paramString == null) {
        return;
      }
      deleteAppWidgetLocked(paramString);
      saveGroupStateAsync(i);
      if (DEBUG) {
        Slog.i("AppWidgetServiceImpl", "Deleted widget id " + paramInt + " for host " + paramString.host.id);
      }
      return;
    }
  }
  
  public void deleteHost(String paramString, int paramInt)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "deleteHost() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramString = lookupHostLocked(new HostId(Binder.getCallingUid(), paramInt, paramString));
      if (paramString == null) {
        return;
      }
      deleteHostLocked(paramString);
      saveGroupStateAsync(i);
      if (DEBUG) {
        Slog.i("AppWidgetServiceImpl", "Deleted host " + paramString.id);
      }
      return;
    }
  }
  
  public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "Permission Denial: can't dump from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
    synchronized (this.mLock)
    {
      int j = this.mProviders.size();
      paramPrintWriter.println("Providers:");
      int i = 0;
      while (i < j)
      {
        dumpProvider((Provider)this.mProviders.get(i), i, paramPrintWriter);
        i += 1;
      }
      j = this.mWidgets.size();
      paramPrintWriter.println(" ");
      paramPrintWriter.println("Widgets:");
      i = 0;
      while (i < j)
      {
        dumpWidget((Widget)this.mWidgets.get(i), i, paramPrintWriter);
        i += 1;
      }
      j = this.mHosts.size();
      paramPrintWriter.println(" ");
      paramPrintWriter.println("Hosts:");
      i = 0;
      while (i < j)
      {
        dumpHost((Host)this.mHosts.get(i), i, paramPrintWriter);
        i += 1;
      }
      j = this.mPackagesWithBindWidgetPermission.size();
      paramPrintWriter.println(" ");
      paramPrintWriter.println("Grants:");
      i = 0;
      while (i < j)
      {
        dumpGrant((Pair)this.mPackagesWithBindWidgetPermission.valueAt(i), i, paramPrintWriter);
        i += 1;
      }
      return;
    }
  }
  
  public int[] getAppWidgetIds(ComponentName paramComponentName)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "getAppWidgetIds() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramComponentName.getPackageName());
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramComponentName = lookupProviderLocked(new ProviderId(Binder.getCallingUid(), paramComponentName, null));
      if (paramComponentName != null)
      {
        paramComponentName = getWidgetIds(paramComponentName.widgets);
        return paramComponentName;
      }
      return new int[0];
    }
  }
  
  public int[] getAppWidgetIdsForHost(String paramString, int paramInt)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "getAppWidgetIdsForHost() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramString = lookupHostLocked(new HostId(Binder.getCallingUid(), paramInt, paramString));
      if (paramString != null)
      {
        paramString = getWidgetIds(paramString.widgets);
        return paramString;
      }
      return new int[0];
    }
  }
  
  public AppWidgetProviderInfo getAppWidgetInfo(String paramString, int paramInt)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "getAppWidgetInfo() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramString = lookupWidgetLocked(paramInt, Binder.getCallingUid(), paramString);
      if ((paramString != null) && (paramString.provider != null))
      {
        boolean bool = paramString.provider.zombie;
        if (!bool) {}
      }
      else
      {
        return null;
      }
      paramString = cloneIfLocalBinder(paramString.provider.info);
      return paramString;
    }
  }
  
  public Bundle getAppWidgetOptions(String paramString, int paramInt)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "getAppWidgetOptions() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramString = lookupWidgetLocked(paramInt, Binder.getCallingUid(), paramString);
      if ((paramString != null) && (paramString.options != null))
      {
        paramString = cloneIfLocalBinder(paramString.options);
        return paramString;
      }
      paramString = Bundle.EMPTY;
      return paramString;
    }
  }
  
  public RemoteViews getAppWidgetViews(String paramString, int paramInt)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "getAppWidgetViews() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramString = lookupWidgetLocked(paramInt, Binder.getCallingUid(), paramString);
      if (paramString != null)
      {
        paramString = cloneIfLocalBinder(paramString.getEffectiveViewsLocked());
        return paramString;
      }
      return null;
    }
  }
  
  public ParceledListSlice<AppWidgetProviderInfo> getInstalledProvidersForProfile(int paramInt1, int paramInt2)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "getInstalledProvidersForProfiles() " + i);
    }
    if (!this.mSecurityPolicy.isEnabledGroupProfile(paramInt2)) {
      return null;
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        ensureGroupStateLoadedLocked(i);
        ArrayList localArrayList = new ArrayList();
        int j = this.mProviders.size();
        i = 0;
        if (i < j)
        {
          Provider localProvider = (Provider)this.mProviders.get(i);
          AppWidgetProviderInfo localAppWidgetProviderInfo = localProvider.info;
          if ((localProvider.zombie) || ((localAppWidgetProviderInfo.widgetCategory & paramInt1) == 0)) {
            break label208;
          }
          int k = localAppWidgetProviderInfo.getProfile().getIdentifier();
          if ((k != paramInt2) || (!this.mSecurityPolicy.isProviderInCallerOrInProfileAndWhitelListed(localProvider.id.componentName.getPackageName(), k))) {
            break label208;
          }
          localArrayList.add(cloneIfLocalBinder(localAppWidgetProviderInfo));
        }
      }
      ParceledListSlice localParceledListSlice = new ParceledListSlice(localList);
      return localParceledListSlice;
      label208:
      i += 1;
    }
  }
  
  public List<String> getWidgetParticipants(int paramInt)
  {
    return this.mBackupRestoreController.getWidgetParticipants(paramInt);
  }
  
  public byte[] getWidgetState(String paramString, int paramInt)
  {
    return this.mBackupRestoreController.getWidgetState(paramString, paramInt);
  }
  
  public boolean hasBindAppWidgetPermission(String paramString, int paramInt)
  {
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "hasBindAppWidgetPermission() " + UserHandle.getCallingUserId());
    }
    this.mSecurityPolicy.enforceModifyAppWidgetBindPermissions(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(paramInt);
      int i = getUidForPackage(paramString, paramInt);
      if (i < 0) {
        return false;
      }
      paramString = Pair.create(Integer.valueOf(paramInt), paramString);
      boolean bool = this.mPackagesWithBindWidgetPermission.contains(paramString);
      return bool;
    }
  }
  
  public boolean isBoundWidgetPackage(String paramString, int paramInt)
  {
    if (Binder.getCallingUid() != 1000) {
      throw new SecurityException("Only the system process can call this");
    }
    synchronized (this.mLock)
    {
      ArraySet localArraySet = (ArraySet)this.mWidgetPackages.get(paramInt);
      if (localArraySet != null)
      {
        boolean bool = localArraySet.contains(paramString);
        return bool;
      }
      return false;
    }
  }
  
  public void notifyAppWidgetViewDataChanged(String paramString, int[] paramArrayOfInt, int paramInt)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "notifyAppWidgetViewDataChanged() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length == 0)) {
      return;
    }
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      int j = paramArrayOfInt.length;
      i = 0;
      while (i < j)
      {
        Widget localWidget = lookupWidgetLocked(paramArrayOfInt[i], Binder.getCallingUid(), paramString);
        if (localWidget != null) {
          scheduleNotifyAppWidgetViewDataChanged(localWidget, paramInt);
        }
        i += 1;
      }
      return;
    }
  }
  
  public void onCrossProfileWidgetProvidersChanged(int paramInt, List<String> paramList)
  {
    int n = this.mSecurityPolicy.getProfileParent(paramInt);
    Object localObject1;
    int k;
    if (n != paramInt)
    {
      localObject1 = this.mLock;
      k = 0;
    }
    for (;;)
    {
      int m;
      boolean bool;
      try
      {
        ArraySet localArraySet = new ArraySet();
        m = this.mProviders.size();
        int i = 0;
        Object localObject2;
        if (i < m)
        {
          localObject2 = (Provider)this.mProviders.get(i);
          if (((Provider)localObject2).getUserId() == paramInt) {
            localArraySet.add(((Provider)localObject2).id.componentName.getPackageName());
          }
        }
        else
        {
          int i1 = paramList.size();
          m = 0;
          i = k;
          k = m;
          if (k < i1)
          {
            localObject2 = (String)paramList.get(k);
            localArraySet.remove(localObject2);
            i |= updateProvidersForPackageLocked((String)localObject2, paramInt, null);
            k += 1;
            continue;
          }
          m = localArraySet.size();
          k = 0;
          if (k >= m) {
            break label232;
          }
          removeWidgetsForPackageLocked((String)localArraySet.valueAt(k), paramInt, n);
          k += 1;
          continue;
          saveGroupStateAsync(paramInt);
          scheduleNotifyGroupHostsForProvidersChangedLocked(paramInt);
          return;
        }
      }
      finally {}
      int j;
      bool += true;
      continue;
      label232:
      if (j == 0) {
        if (m <= 0) {}
      }
    }
  }
  
  void onUserStopped(int paramInt)
  {
    Object localObject1 = this.mLock;
    int n = 0;
    for (;;)
    {
      int j;
      label53:
      label64:
      int m;
      try
      {
        i = this.mWidgets.size() - 1;
        Object localObject2;
        if (i >= 0)
        {
          localObject2 = (Widget)this.mWidgets.get(i);
          if (((Widget)localObject2).host.getUserId() != paramInt) {
            break label349;
          }
          j = 1;
          if (((Widget)localObject2).provider == null) {
            break label354;
          }
          k = 1;
          if ((k == 0) || (((Widget)localObject2).provider.getUserId() != paramInt)) {
            break label360;
          }
          m = 1;
          break label325;
          removeWidgetLocked((Widget)localObject2);
          ((Widget)localObject2).host.widgets.remove(localObject2);
          ((Widget)localObject2).host = null;
          if (k == 0) {
            break label342;
          }
          ((Widget)localObject2).provider.widgets.remove(localObject2);
          ((Widget)localObject2).provider = null;
          break label342;
        }
        else
        {
          j = this.mHosts.size() - 1;
          i = n;
          if (j >= 0)
          {
            localObject2 = (Host)this.mHosts.get(j);
            k = i;
            if (((Host)localObject2).getUserId() != paramInt) {
              break label366;
            }
            if (!((Host)localObject2).widgets.isEmpty()) {
              break label376;
            }
            k = 0;
            k = i | k;
            deleteHostLocked((Host)localObject2);
            break label366;
          }
          j = this.mPackagesWithBindWidgetPermission.size() - 1;
          if (j >= 0)
          {
            if (((Integer)((Pair)this.mPackagesWithBindWidgetPermission.valueAt(j)).first).intValue() != paramInt) {
              break label382;
            }
            this.mPackagesWithBindWidgetPermission.removeAt(j);
            break label382;
          }
          j = this.mLoadedUserIds.indexOfKey(paramInt);
          if (j >= 0) {
            this.mLoadedUserIds.removeAt(j);
          }
          j = this.mNextAppWidgetIds.indexOfKey(paramInt);
          if (j >= 0) {
            this.mNextAppWidgetIds.removeAt(j);
          }
          if (i != 0) {
            saveGroupStateAsync(paramInt);
          }
          return;
        }
      }
      finally {}
      for (;;)
      {
        label325:
        if ((j != 0) && ((k == 0) || (m != 0))) {
          break label364;
        }
        label342:
        i -= 1;
        break;
        label349:
        j = 0;
        break label53;
        label354:
        k = 0;
        break label64;
        label360:
        m = 0;
      }
      label364:
      continue;
      label366:
      j -= 1;
      int i = k;
      continue;
      label376:
      int k = 1;
      continue;
      label382:
      j -= 1;
    }
  }
  
  void onUserUnlocked(int paramInt)
  {
    if (isProfileWithLockedParent(paramInt)) {
      return;
    }
    if (!this.mUserManager.isUserUnlockingOrUnlocked(paramInt))
    {
      Slog.w("AppWidgetServiceImpl", "User " + paramInt + " is no longer unlocked - exiting");
      return;
    }
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        ensureGroupStateLoadedLocked(paramInt);
        reloadWidgetsMaskedStateForGroup(this.mSecurityPolicy.getGroupParent(paramInt));
        int j = this.mProviders.size();
        i = 0;
        if (i < j)
        {
          Provider localProvider = (Provider)this.mProviders.get(i);
          if ((localProvider.getUserId() != paramInt) || (localProvider.widgets.size() <= 0)) {
            break label177;
          }
          sendEnableIntentLocked(localProvider);
          int[] arrayOfInt = getWidgetIds(localProvider.widgets);
          sendUpdateIntentLocked(localProvider, arrayOfInt);
          registerForBroadcastsLocked(localProvider, arrayOfInt);
        }
      }
      return;
      label177:
      i += 1;
    }
  }
  
  void onWidgetProviderAddedOrChangedLocked(Widget paramWidget)
  {
    if (paramWidget.provider == null) {
      return;
    }
    int i = paramWidget.provider.getUserId();
    Object localObject2 = (ArraySet)this.mWidgetPackages.get(i);
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject2 = this.mWidgetPackages;
      localObject1 = new ArraySet();
      ((SparseArray)localObject2).put(i, localObject1);
    }
    ((ArraySet)localObject1).add(paramWidget.provider.info.provider.getPackageName());
    if (paramWidget.provider.isMaskedLocked())
    {
      maskWidgetsViewsLocked(paramWidget.provider, paramWidget);
      return;
    }
    Widget.-wrap0(paramWidget);
  }
  
  public void partiallyUpdateAppWidgetIds(String paramString, int[] paramArrayOfInt, RemoteViews paramRemoteViews)
  {
    if (DEBUG_ONEPLUS) {
      Slog.i("AppWidgetServiceImpl", "partiallyUpdateAppWidgetIds() " + UserHandle.getCallingUserId());
    }
    updateAppWidgetIds(paramString, paramArrayOfInt, paramRemoteViews, true);
  }
  
  void reloadWidgetsMaskedStateForGroup(int paramInt)
  {
    if (!this.mUserManager.isUserUnlockingOrUnlocked(paramInt)) {
      return;
    }
    synchronized (this.mLock)
    {
      reloadWidgetsMaskedState(paramInt);
      int[] arrayOfInt = this.mUserManager.getEnabledProfileIds(paramInt);
      paramInt = 0;
      int i = arrayOfInt.length;
      while (paramInt < i)
      {
        reloadWidgetsMaskedState(arrayOfInt[paramInt]);
        paramInt += 1;
      }
      return;
    }
  }
  
  void removeWidgetLocked(Widget paramWidget)
  {
    this.mWidgets.remove(paramWidget);
    onWidgetRemovedLocked(paramWidget);
  }
  
  public void restoreFinished(int paramInt)
  {
    this.mBackupRestoreController.restoreFinished(paramInt);
  }
  
  public void restoreStarting(int paramInt)
  {
    this.mBackupRestoreController.restoreStarting(paramInt);
  }
  
  public void restoreWidgetState(String paramString, byte[] paramArrayOfByte, int paramInt)
  {
    this.mBackupRestoreController.restoreWidgetState(paramString, paramArrayOfByte, paramInt);
  }
  
  public void sendOptionsChangedIntentLocked(Widget paramWidget)
  {
    Intent localIntent = new Intent("android.appwidget.action.APPWIDGET_UPDATE_OPTIONS");
    localIntent.setComponent(paramWidget.provider.info.provider);
    localIntent.putExtra("appWidgetId", paramWidget.appWidgetId);
    localIntent.putExtra("appWidgetOptions", paramWidget.options);
    sendBroadcastAsUser(localIntent, paramWidget.provider.info.getProfile());
  }
  
  public void setBindAppWidgetPermission(String paramString, int paramInt, boolean paramBoolean)
  {
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "setBindAppWidgetPermission() " + UserHandle.getCallingUserId());
    }
    this.mSecurityPolicy.enforceModifyAppWidgetBindPermissions(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(paramInt);
      int i = getUidForPackage(paramString, paramInt);
      if (i < 0) {
        return;
      }
      paramString = Pair.create(Integer.valueOf(paramInt), paramString);
      if (paramBoolean)
      {
        this.mPackagesWithBindWidgetPermission.add(paramString);
        saveGroupStateAsync(paramInt);
        return;
      }
      this.mPackagesWithBindWidgetPermission.remove(paramString);
    }
  }
  
  public void setSafeMode(boolean paramBoolean)
  {
    this.mSafeMode = paramBoolean;
  }
  
  public ParceledListSlice<PendingHostUpdate> startListening(IAppWidgetHost paramIAppWidgetHost, String paramString, int paramInt, int[] paramArrayOfInt)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "startListening(): " + paramString + ", userId = " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    for (;;)
    {
      synchronized (this.mLock)
      {
        ensureGroupStateLoadedLocked(i);
        paramString = lookupOrAddHostLocked(new HostId(Binder.getCallingUid(), paramInt, paramString));
        paramString.callbacks = paramIAppWidgetHost;
        int j = paramArrayOfInt.length;
        paramIAppWidgetHost = new ArrayList(j);
        LongSparseArray localLongSparseArray = new LongSparseArray();
        paramInt = 0;
        if (paramInt < j)
        {
          if (paramString.getPendingUpdatesForId(paramArrayOfInt[paramInt], localLongSparseArray))
          {
            int k = localLongSparseArray.size();
            i = 0;
            if (i < k)
            {
              paramIAppWidgetHost.add((PendingHostUpdate)localLongSparseArray.valueAt(i));
              i += 1;
              continue;
            }
          }
        }
        else
        {
          paramIAppWidgetHost = new ParceledListSlice(paramIAppWidgetHost);
          return paramIAppWidgetHost;
        }
      }
      paramInt += 1;
    }
  }
  
  public void stopListening(String paramString, int paramInt)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "stopListening(): " + paramString + ", userId = " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramString = lookupHostLocked(new HostId(Binder.getCallingUid(), paramInt, paramString));
      if (paramString != null)
      {
        paramString.callbacks = null;
        pruneHostLocked(paramString);
      }
      return;
    }
  }
  
  public void unbindRemoteViewsService(String paramString, int paramInt, Intent paramIntent)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG) {
      Slog.i("AppWidgetServiceImpl", "unbindRemoteViewsService() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramIntent = Pair.create(Integer.valueOf(paramInt), new Intent.FilterComparison(paramIntent));
      if (!this.mBoundRemoteViewsServices.containsKey(paramIntent)) {
        break label167;
      }
      if (lookupWidgetLocked(paramInt, Binder.getCallingUid(), paramString) == null) {
        throw new IllegalArgumentException("Bad widget id " + paramInt);
      }
    }
    paramString = (ServiceConnectionProxy)this.mBoundRemoteViewsServices.get(paramIntent);
    paramString.disconnect();
    this.mContext.unbindService(paramString);
    this.mBoundRemoteViewsServices.remove(paramIntent);
    label167:
  }
  
  public void updateAppWidgetIds(String paramString, int[] paramArrayOfInt, RemoteViews paramRemoteViews)
  {
    if (DEBUG_ONEPLUS) {
      Slog.i("AppWidgetServiceImpl", "updateAppWidgetIds() " + UserHandle.getCallingUserId());
    }
    updateAppWidgetIds(paramString, paramArrayOfInt, paramRemoteViews, false);
  }
  
  public void updateAppWidgetOptions(String paramString, int paramInt, Bundle paramBundle)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG_ONEPLUS) {
      Slog.i("AppWidgetServiceImpl", "updateAppWidgetOptions() " + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramString);
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramString = lookupWidgetLocked(paramInt, Binder.getCallingUid(), paramString);
      if (paramString == null) {
        return;
      }
      if (paramString.options == null) {
        paramString.options = new Bundle();
      }
      paramString.options.putAll(paramBundle);
      sendOptionsChangedIntentLocked(paramString);
      saveGroupStateAsync(i);
      return;
    }
  }
  
  public void updateAppWidgetProvider(ComponentName paramComponentName, RemoteViews paramRemoteViews)
  {
    int i = UserHandle.getCallingUserId();
    if (DEBUG_ONEPLUS) {
      Slog.i("AppWidgetServiceImpl", "updateAppWidgetProvider(): pkg:" + paramComponentName.getPackageName() + ", userId:" + i);
    }
    this.mSecurityPolicy.enforceCallFromPackage(paramComponentName.getPackageName());
    synchronized (this.mLock)
    {
      ensureGroupStateLoadedLocked(i);
      paramComponentName = new ProviderId(Binder.getCallingUid(), paramComponentName, null);
      Provider localProvider = lookupProviderLocked(paramComponentName);
      if (localProvider == null)
      {
        Slog.w("AppWidgetServiceImpl", "Provider doesn't exist " + paramComponentName);
        return;
      }
      paramComponentName = localProvider.widgets;
      int j = paramComponentName.size();
      i = 0;
      while (i < j)
      {
        updateAppWidgetInstanceLocked((Widget)paramComponentName.get(i), paramRemoteViews, false);
        i += 1;
      }
      return;
    }
  }
  
  private final class BackupRestoreController
  {
    private static final boolean DEBUG = true;
    private static final String TAG = "BackupRestoreController";
    private static final int WIDGET_STATE_VERSION = 2;
    private final HashSet<String> mPrunedApps = new HashSet();
    private final HashMap<AppWidgetServiceImpl.Host, ArrayList<RestoreUpdateRecord>> mUpdatesByHost = new HashMap();
    private final HashMap<AppWidgetServiceImpl.Provider, ArrayList<RestoreUpdateRecord>> mUpdatesByProvider = new HashMap();
    
    private BackupRestoreController() {}
    
    private boolean alreadyStashed(ArrayList<RestoreUpdateRecord> paramArrayList, int paramInt1, int paramInt2)
    {
      int j = paramArrayList.size();
      int i = 0;
      while (i < j)
      {
        RestoreUpdateRecord localRestoreUpdateRecord = (RestoreUpdateRecord)paramArrayList.get(i);
        if ((localRestoreUpdateRecord.oldId == paramInt1) && (localRestoreUpdateRecord.newId == paramInt2)) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    private int countPendingUpdates(ArrayList<RestoreUpdateRecord> paramArrayList)
    {
      int j = 0;
      int m = paramArrayList.size();
      int i = 0;
      while (i < m)
      {
        int k = j;
        if (!((RestoreUpdateRecord)paramArrayList.get(i)).notified) {
          k = j + 1;
        }
        i += 1;
        j = k;
      }
      return j;
    }
    
    private AppWidgetServiceImpl.Provider findProviderLocked(ComponentName paramComponentName, int paramInt)
    {
      int j = AppWidgetServiceImpl.-get8(AppWidgetServiceImpl.this).size();
      int i = 0;
      while (i < j)
      {
        AppWidgetServiceImpl.Provider localProvider = (AppWidgetServiceImpl.Provider)AppWidgetServiceImpl.-get8(AppWidgetServiceImpl.this).get(i);
        if ((localProvider.getUserId() == paramInt) && (localProvider.id.componentName.equals(paramComponentName))) {
          return localProvider;
        }
        i += 1;
      }
      return null;
    }
    
    private AppWidgetServiceImpl.Widget findRestoredWidgetLocked(int paramInt, AppWidgetServiceImpl.Host paramHost, AppWidgetServiceImpl.Provider paramProvider)
    {
      Slog.i("BackupRestoreController", "Find restored widget: id=" + paramInt + " host=" + paramHost + " provider=" + paramProvider);
      if ((paramProvider == null) || (paramHost == null)) {
        return null;
      }
      int j = AppWidgetServiceImpl.-get11(AppWidgetServiceImpl.this).size();
      int i = 0;
      while (i < j)
      {
        AppWidgetServiceImpl.Widget localWidget = (AppWidgetServiceImpl.Widget)AppWidgetServiceImpl.-get11(AppWidgetServiceImpl.this).get(i);
        if ((localWidget.restoredId == paramInt) && (localWidget.host.id.equals(paramHost.id)) && (localWidget.provider.id.equals(paramProvider.id)))
        {
          Slog.i("BackupRestoreController", "   Found at " + i + " : " + localWidget);
          return localWidget;
        }
        i += 1;
      }
      return null;
    }
    
    private boolean isProviderAndHostInUser(AppWidgetServiceImpl.Widget paramWidget, int paramInt)
    {
      if (paramWidget.host.getUserId() == paramInt) {
        return (paramWidget.provider == null) || (paramWidget.provider.getUserId() == paramInt);
      }
      return false;
    }
    
    private boolean packageNeedsWidgetBackupLocked(String paramString, int paramInt)
    {
      int j = AppWidgetServiceImpl.-get11(AppWidgetServiceImpl.this).size();
      int i = 0;
      if (i < j)
      {
        Object localObject = (AppWidgetServiceImpl.Widget)AppWidgetServiceImpl.-get11(AppWidgetServiceImpl.this).get(i);
        if (!isProviderAndHostInUser((AppWidgetServiceImpl.Widget)localObject, paramInt)) {}
        do
        {
          i += 1;
          break;
          if (((AppWidgetServiceImpl.Widget)localObject).host.isInPackageForUser(paramString, paramInt)) {
            return true;
          }
          localObject = ((AppWidgetServiceImpl.Widget)localObject).provider;
        } while ((localObject == null) || (!((AppWidgetServiceImpl.Provider)localObject).isInPackageForUser(paramString, paramInt)));
        return true;
      }
      return false;
    }
    
    private Bundle parseWidgetIdOptions(XmlPullParser paramXmlPullParser)
    {
      Bundle localBundle = new Bundle();
      String str = paramXmlPullParser.getAttributeValue(null, "min_width");
      if (str != null) {
        localBundle.putInt("appWidgetMinWidth", Integer.parseInt(str, 16));
      }
      str = paramXmlPullParser.getAttributeValue(null, "min_height");
      if (str != null) {
        localBundle.putInt("appWidgetMinHeight", Integer.parseInt(str, 16));
      }
      str = paramXmlPullParser.getAttributeValue(null, "max_width");
      if (str != null) {
        localBundle.putInt("appWidgetMaxWidth", Integer.parseInt(str, 16));
      }
      str = paramXmlPullParser.getAttributeValue(null, "max_height");
      if (str != null) {
        localBundle.putInt("appWidgetMaxHeight", Integer.parseInt(str, 16));
      }
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "host_category");
      if (paramXmlPullParser != null) {
        localBundle.putInt("appWidgetCategory", Integer.parseInt(paramXmlPullParser, 16));
      }
      return localBundle;
    }
    
    private void pruneWidgetStateLocked(String paramString, int paramInt)
    {
      if (!this.mPrunedApps.contains(paramString))
      {
        Slog.i("BackupRestoreController", "pruning widget state for restoring package " + paramString);
        int i = AppWidgetServiceImpl.-get11(AppWidgetServiceImpl.this).size() - 1;
        while (i >= 0)
        {
          AppWidgetServiceImpl.Widget localWidget = (AppWidgetServiceImpl.Widget)AppWidgetServiceImpl.-get11(AppWidgetServiceImpl.this).get(i);
          AppWidgetServiceImpl.Host localHost = localWidget.host;
          AppWidgetServiceImpl.Provider localProvider = localWidget.provider;
          if ((AppWidgetServiceImpl.Host.-wrap0(localHost, paramString, paramInt)) || ((localProvider != null) && (localProvider.isInPackageForUser(paramString, paramInt))))
          {
            localHost.widgets.remove(localWidget);
            localProvider.widgets.remove(localWidget);
            AppWidgetServiceImpl.-wrap19(AppWidgetServiceImpl.this, localWidget);
            AppWidgetServiceImpl.this.removeWidgetLocked(localWidget);
          }
          i -= 1;
        }
        this.mPrunedApps.add(paramString);
        return;
      }
      Slog.i("BackupRestoreController", "already pruned " + paramString + ", continuing normally");
    }
    
    private void sendWidgetRestoreBroadcastLocked(String paramString, AppWidgetServiceImpl.Provider paramProvider, AppWidgetServiceImpl.Host paramHost, int[] paramArrayOfInt1, int[] paramArrayOfInt2, UserHandle paramUserHandle)
    {
      paramString = new Intent(paramString);
      paramString.putExtra("appWidgetOldIds", paramArrayOfInt1);
      paramString.putExtra("appWidgetIds", paramArrayOfInt2);
      if (paramProvider != null)
      {
        paramString.setComponent(paramProvider.info.provider);
        AppWidgetServiceImpl.-wrap15(AppWidgetServiceImpl.this, paramString, paramUserHandle);
      }
      if (paramHost != null)
      {
        paramString.setComponent(null);
        paramString.setPackage(paramHost.id.packageName);
        paramString.putExtra("hostId", paramHost.id.hostId);
        AppWidgetServiceImpl.-wrap15(AppWidgetServiceImpl.this, paramString, paramUserHandle);
      }
    }
    
    private void stashHostRestoreUpdateLocked(AppWidgetServiceImpl.Host paramHost, int paramInt1, int paramInt2)
    {
      ArrayList localArrayList2 = (ArrayList)this.mUpdatesByHost.get(paramHost);
      ArrayList localArrayList1;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.mUpdatesByHost.put(paramHost, localArrayList1);
      }
      do
      {
        localArrayList1.add(new RestoreUpdateRecord(paramInt1, paramInt2));
        return;
        localArrayList1 = localArrayList2;
      } while (!alreadyStashed(localArrayList2, paramInt1, paramInt2));
      Slog.i("BackupRestoreController", "ID remap " + paramInt1 + " -> " + paramInt2 + " already stashed for " + paramHost);
    }
    
    private void stashProviderRestoreUpdateLocked(AppWidgetServiceImpl.Provider paramProvider, int paramInt1, int paramInt2)
    {
      ArrayList localArrayList2 = (ArrayList)this.mUpdatesByProvider.get(paramProvider);
      ArrayList localArrayList1;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.mUpdatesByProvider.put(paramProvider, localArrayList1);
      }
      do
      {
        localArrayList1.add(new RestoreUpdateRecord(paramInt1, paramInt2));
        return;
        localArrayList1 = localArrayList2;
      } while (!alreadyStashed(localArrayList2, paramInt1, paramInt2));
      Slog.i("BackupRestoreController", "ID remap " + paramInt1 + " -> " + paramInt2 + " already stashed for " + paramProvider);
    }
    
    public List<String> getWidgetParticipants(int paramInt)
    {
      Slog.i("BackupRestoreController", "Getting widget participants for user: " + paramInt);
      HashSet localHashSet = new HashSet();
      for (;;)
      {
        int i;
        synchronized (AppWidgetServiceImpl.-get5(AppWidgetServiceImpl.this))
        {
          int j = AppWidgetServiceImpl.-get11(AppWidgetServiceImpl.this).size();
          i = 0;
          if (i < j)
          {
            Object localObject2 = (AppWidgetServiceImpl.Widget)AppWidgetServiceImpl.-get11(AppWidgetServiceImpl.this).get(i);
            if (!isProviderAndHostInUser((AppWidgetServiceImpl.Widget)localObject2, paramInt)) {
              break label164;
            }
            localHashSet.add(((AppWidgetServiceImpl.Widget)localObject2).host.id.packageName);
            localObject2 = ((AppWidgetServiceImpl.Widget)localObject2).provider;
            if (localObject2 == null) {
              break label164;
            }
            localHashSet.add(((AppWidgetServiceImpl.Provider)localObject2).id.componentName.getPackageName());
          }
        }
        return new ArrayList(localCollection);
        label164:
        i += 1;
      }
    }
    
    public byte[] getWidgetState(String paramString, int paramInt)
    {
      Slog.i("BackupRestoreController", "Getting widget state for user: " + paramInt);
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      for (;;)
      {
        int i;
        int k;
        synchronized (AppWidgetServiceImpl.-get5(AppWidgetServiceImpl.this))
        {
          boolean bool = packageNeedsWidgetBackupLocked(paramString, paramInt);
          if (!bool) {
            return null;
          }
          try
          {
            FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
            localFastXmlSerializer.setOutput(localByteArrayOutputStream, StandardCharsets.UTF_8.name());
            localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
            localFastXmlSerializer.startTag(null, "ws");
            localFastXmlSerializer.attribute(null, "version", String.valueOf(2));
            localFastXmlSerializer.attribute(null, "pkg", paramString);
            j = 0;
            int m = AppWidgetServiceImpl.-get8(AppWidgetServiceImpl.this).size();
            i = 0;
            Object localObject2;
            if (i < m)
            {
              localObject2 = (AppWidgetServiceImpl.Provider)AppWidgetServiceImpl.-get8(AppWidgetServiceImpl.this).get(i);
              k = j;
              if (!((AppWidgetServiceImpl.Provider)localObject2).widgets.isEmpty()) {
                if (!((AppWidgetServiceImpl.Provider)localObject2).isInPackageForUser(paramString, paramInt))
                {
                  k = j;
                  if (!((AppWidgetServiceImpl.Provider)localObject2).hostedByPackageForUser(paramString, paramInt)) {}
                }
                else
                {
                  ((AppWidgetServiceImpl.Provider)localObject2).tag = j;
                  AppWidgetServiceImpl.-wrap18(localFastXmlSerializer, (AppWidgetServiceImpl.Provider)localObject2);
                  k = j + 1;
                }
              }
            }
            else
            {
              m = AppWidgetServiceImpl.-get4(AppWidgetServiceImpl.this).size();
              j = 0;
              i = 0;
              if (i < m)
              {
                localObject2 = (AppWidgetServiceImpl.Host)AppWidgetServiceImpl.-get4(AppWidgetServiceImpl.this).get(i);
                k = j;
                if (((AppWidgetServiceImpl.Host)localObject2).widgets.isEmpty()) {
                  break label503;
                }
                if (!((AppWidgetServiceImpl.Host)localObject2).isInPackageForUser(paramString, paramInt))
                {
                  k = j;
                  if (!AppWidgetServiceImpl.Host.-wrap0((AppWidgetServiceImpl.Host)localObject2, paramString, paramInt)) {
                    break label503;
                  }
                }
                ((AppWidgetServiceImpl.Host)localObject2).tag = j;
                AppWidgetServiceImpl.-wrap17(localFastXmlSerializer, (AppWidgetServiceImpl.Host)localObject2);
                k = j + 1;
                break label503;
              }
              j = AppWidgetServiceImpl.-get11(AppWidgetServiceImpl.this).size();
              i = 0;
              if (i < j)
              {
                localObject2 = (AppWidgetServiceImpl.Widget)AppWidgetServiceImpl.-get11(AppWidgetServiceImpl.this).get(i);
                AppWidgetServiceImpl.Provider localProvider = ((AppWidgetServiceImpl.Widget)localObject2).provider;
                if ((!((AppWidgetServiceImpl.Widget)localObject2).host.isInPackageForUser(paramString, paramInt)) && ((localProvider == null) || (!localProvider.isInPackageForUser(paramString, paramInt)))) {
                  break label514;
                }
                AppWidgetServiceImpl.-wrap16(localFastXmlSerializer, (AppWidgetServiceImpl.Widget)localObject2);
                break label514;
              }
              localFastXmlSerializer.endTag(null, "ws");
              localFastXmlSerializer.endDocument();
              return localByteArrayOutputStream.toByteArray();
            }
          }
          catch (IOException localIOException)
          {
            Slog.w("BackupRestoreController", "Unable to save widget state for " + paramString);
            return null;
          }
        }
        i += 1;
        int j = k;
        continue;
        label503:
        i += 1;
        j = k;
        continue;
        label514:
        i += 1;
      }
    }
    
    public void restoreFinished(int paramInt)
    {
      Slog.i("BackupRestoreController", "restoreFinished for " + paramInt);
      UserHandle localUserHandle1 = new UserHandle(paramInt);
      Object localObject3;
      Object localObject2;
      int[] arrayOfInt1;
      int[] arrayOfInt2;
      int k;
      int i;
      RestoreUpdateRecord localRestoreUpdateRecord;
      int j;
      synchronized (AppWidgetServiceImpl.-get5(AppWidgetServiceImpl.this))
      {
        localIterator = this.mUpdatesByProvider.entrySet().iterator();
        while (localIterator.hasNext())
        {
          localObject3 = (Map.Entry)localIterator.next();
          localObject2 = (AppWidgetServiceImpl.Provider)((Map.Entry)localObject3).getKey();
          localObject3 = (ArrayList)((Map.Entry)localObject3).getValue();
          paramInt = countPendingUpdates((ArrayList)localObject3);
          Slog.i("BackupRestoreController", "Provider " + localObject2 + " pending: " + paramInt);
          if (paramInt > 0)
          {
            arrayOfInt1 = new int[paramInt];
            arrayOfInt2 = new int[paramInt];
            k = ((ArrayList)localObject3).size();
            i = 0;
            paramInt = 0;
            if (paramInt < k)
            {
              localRestoreUpdateRecord = (RestoreUpdateRecord)((ArrayList)localObject3).get(paramInt);
              j = i;
              if (localRestoreUpdateRecord.notified) {
                break label572;
              }
              localRestoreUpdateRecord.notified = true;
              arrayOfInt1[i] = localRestoreUpdateRecord.oldId;
              arrayOfInt2[i] = localRestoreUpdateRecord.newId;
              j = i + 1;
              Slog.i("BackupRestoreController", "   " + localRestoreUpdateRecord.oldId + " => " + localRestoreUpdateRecord.newId);
              break label572;
            }
            sendWidgetRestoreBroadcastLocked("android.appwidget.action.APPWIDGET_RESTORED", (AppWidgetServiceImpl.Provider)localObject2, null, arrayOfInt1, arrayOfInt2, localUserHandle1);
          }
        }
      }
      Iterator localIterator = this.mUpdatesByHost.entrySet().iterator();
      label320:
      while (localIterator.hasNext())
      {
        localObject3 = (Map.Entry)localIterator.next();
        localObject2 = (AppWidgetServiceImpl.Host)((Map.Entry)localObject3).getKey();
        if (((AppWidgetServiceImpl.Host)localObject2).id.uid != -1)
        {
          localObject3 = (ArrayList)((Map.Entry)localObject3).getValue();
          paramInt = countPendingUpdates((ArrayList)localObject3);
          Slog.i("BackupRestoreController", "Host " + localObject2 + " pending: " + paramInt);
          if (paramInt > 0)
          {
            arrayOfInt1 = new int[paramInt];
            arrayOfInt2 = new int[paramInt];
            k = ((ArrayList)localObject3).size();
            i = 0;
            paramInt = 0;
          }
        }
      }
      for (;;)
      {
        if (paramInt < k)
        {
          localRestoreUpdateRecord = (RestoreUpdateRecord)((ArrayList)localObject3).get(paramInt);
          j = i;
          if (!localRestoreUpdateRecord.notified)
          {
            localRestoreUpdateRecord.notified = true;
            arrayOfInt1[i] = localRestoreUpdateRecord.oldId;
            arrayOfInt2[i] = localRestoreUpdateRecord.newId;
            j = i + 1;
            Slog.i("BackupRestoreController", "   " + localRestoreUpdateRecord.oldId + " => " + localRestoreUpdateRecord.newId);
          }
        }
        else
        {
          sendWidgetRestoreBroadcastLocked("android.appwidget.action.APPWIDGET_HOST_RESTORED", null, (AppWidgetServiceImpl.Host)localObject2, arrayOfInt1, arrayOfInt2, localUserHandle2);
          break label320;
          return;
          label572:
          paramInt += 1;
          i = j;
          break;
        }
        paramInt += 1;
        i = j;
      }
    }
    
    public void restoreStarting(int paramInt)
    {
      Slog.i("BackupRestoreController", "Restore starting for user: " + paramInt);
      synchronized (AppWidgetServiceImpl.-get5(AppWidgetServiceImpl.this))
      {
        this.mPrunedApps.clear();
        this.mUpdatesByProvider.clear();
        this.mUpdatesByHost.clear();
        return;
      }
    }
    
    public void restoreWidgetState(String paramString, byte[] paramArrayOfByte, int paramInt)
    {
      Slog.i("BackupRestoreController", "Restoring widget state for user:" + paramInt + " package: " + paramString);
      paramArrayOfByte = new ByteArrayInputStream(paramArrayOfByte);
      for (;;)
      {
        try
        {
          localArrayList1 = new ArrayList();
          localArrayList2 = new ArrayList();
          localXmlPullParser = Xml.newPullParser();
          localXmlPullParser.setInput(paramArrayOfByte, StandardCharsets.UTF_8.name());
        }
        catch (XmlPullParserException|IOException paramArrayOfByte)
        {
          ArrayList localArrayList1;
          ArrayList localArrayList2;
          XmlPullParser localXmlPullParser;
          int i;
          Slog.w("BackupRestoreController", "Unable to restore widget state for " + paramString);
          return;
          if (!"g".equals(paramArrayOfByte)) {
            continue;
          }
          int j = Integer.parseInt(localXmlPullParser.getAttributeValue(null, "id"), 16);
          AppWidgetServiceImpl.Host localHost = (AppWidgetServiceImpl.Host)localArrayList2.get(Integer.parseInt(localXmlPullParser.getAttributeValue(null, "h"), 16));
          paramArrayOfByte = null;
          Object localObject1 = localXmlPullParser.getAttributeValue(null, "p");
          if (localObject1 == null) {
            continue;
          }
          paramArrayOfByte = (AppWidgetServiceImpl.Provider)localArrayList1.get(Integer.parseInt((String)localObject1, 16));
          pruneWidgetStateLocked(localHost.id.packageName, paramInt);
          if (paramArrayOfByte == null) {
            continue;
          }
          pruneWidgetStateLocked(paramArrayOfByte.id.componentName.getPackageName(), paramInt);
          Object localObject2 = findRestoredWidgetLocked(j, localHost, paramArrayOfByte);
          localObject1 = localObject2;
          if (localObject2 != null) {
            continue;
          }
          localObject1 = new AppWidgetServiceImpl.Widget(null);
          ((AppWidgetServiceImpl.Widget)localObject1).appWidgetId = AppWidgetServiceImpl.-wrap3(AppWidgetServiceImpl.this, paramInt);
          ((AppWidgetServiceImpl.Widget)localObject1).restoredId = j;
          ((AppWidgetServiceImpl.Widget)localObject1).options = parseWidgetIdOptions(localXmlPullParser);
          ((AppWidgetServiceImpl.Widget)localObject1).host = localHost;
          ((AppWidgetServiceImpl.Widget)localObject1).host.widgets.add(localObject1);
          ((AppWidgetServiceImpl.Widget)localObject1).provider = paramArrayOfByte;
          if (((AppWidgetServiceImpl.Widget)localObject1).provider == null) {
            continue;
          }
          ((AppWidgetServiceImpl.Widget)localObject1).provider.widgets.add(localObject1);
          Slog.i("BackupRestoreController", "New restored id " + j + " now " + localObject1);
          AppWidgetServiceImpl.this.addWidgetLocked((AppWidgetServiceImpl.Widget)localObject1);
          if (((AppWidgetServiceImpl.Widget)localObject1).provider.info == null) {
            continue;
          }
          stashProviderRestoreUpdateLocked(((AppWidgetServiceImpl.Widget)localObject1).provider, j, ((AppWidgetServiceImpl.Widget)localObject1).appWidgetId);
          stashHostRestoreUpdateLocked(((AppWidgetServiceImpl.Widget)localObject1).host, j, ((AppWidgetServiceImpl.Widget)localObject1).appWidgetId);
          Slog.i("BackupRestoreController", "   instance: " + j + " -> " + ((AppWidgetServiceImpl.Widget)localObject1).appWidgetId + " :: p=" + ((AppWidgetServiceImpl.Widget)localObject1).provider);
          continue;
          Slog.w("BackupRestoreController", "Missing provider for restored widget " + localObject1);
          continue;
        }
        finally
        {
          AppWidgetServiceImpl.-wrap13(AppWidgetServiceImpl.this, paramInt);
        }
        synchronized (AppWidgetServiceImpl.-get5(AppWidgetServiceImpl.this))
        {
          i = localXmlPullParser.next();
          if (i == 2)
          {
            paramArrayOfByte = localXmlPullParser.getName();
            if ("ws".equals(paramArrayOfByte))
            {
              paramArrayOfByte = localXmlPullParser.getAttributeValue(null, "version");
              if (Integer.parseInt(paramArrayOfByte) > 2)
              {
                Slog.w("BackupRestoreController", "Unable to process state version " + paramArrayOfByte);
                AppWidgetServiceImpl.-wrap13(AppWidgetServiceImpl.this, paramInt);
                return;
              }
              if (!paramString.equals(localXmlPullParser.getAttributeValue(null, "pkg")))
              {
                Slog.w("BackupRestoreController", "Package mismatch in ws");
                AppWidgetServiceImpl.-wrap13(AppWidgetServiceImpl.this, paramInt);
              }
            }
            else
            {
              if (!"p".equals(paramArrayOfByte)) {
                continue;
              }
              localObject2 = new ComponentName(localXmlPullParser.getAttributeValue(null, "pkg"), localXmlPullParser.getAttributeValue(null, "cl"));
              localObject1 = findProviderLocked((ComponentName)localObject2, paramInt);
              paramArrayOfByte = (byte[])localObject1;
              if (localObject1 == null)
              {
                paramArrayOfByte = new AppWidgetServiceImpl.Provider(null);
                paramArrayOfByte.id = new AppWidgetServiceImpl.ProviderId(-1, (ComponentName)localObject2, null);
                paramArrayOfByte.info = new AppWidgetProviderInfo();
                paramArrayOfByte.info.provider = ((ComponentName)localObject2);
                paramArrayOfByte.zombie = true;
                AppWidgetServiceImpl.-get8(AppWidgetServiceImpl.this).add(paramArrayOfByte);
              }
              Slog.i("BackupRestoreController", "   provider " + paramArrayOfByte.id);
              localArrayList1.add(paramArrayOfByte);
            }
          }
          if (i != 1) {
            continue;
          }
          AppWidgetServiceImpl.-wrap13(AppWidgetServiceImpl.this, paramInt);
          return;
          if ("h".equals(paramArrayOfByte))
          {
            paramArrayOfByte = localXmlPullParser.getAttributeValue(null, "pkg");
            paramArrayOfByte = new AppWidgetServiceImpl.HostId(AppWidgetServiceImpl.-wrap2(AppWidgetServiceImpl.this, paramArrayOfByte, paramInt), Integer.parseInt(localXmlPullParser.getAttributeValue(null, "id"), 16), paramArrayOfByte);
            paramArrayOfByte = AppWidgetServiceImpl.-wrap1(AppWidgetServiceImpl.this, paramArrayOfByte);
            localArrayList2.add(paramArrayOfByte);
            Slog.i("BackupRestoreController", "   host[" + localArrayList2.size() + "]: {" + paramArrayOfByte.id + "}");
          }
        }
      }
    }
    
    private class RestoreUpdateRecord
    {
      public int newId;
      public boolean notified;
      public int oldId;
      
      public RestoreUpdateRecord(int paramInt1, int paramInt2)
      {
        this.oldId = paramInt1;
        this.newId = paramInt2;
        this.notified = false;
      }
    }
  }
  
  private final class CallbackHandler
    extends Handler
  {
    public static final int MSG_NOTIFY_PROVIDERS_CHANGED = 3;
    public static final int MSG_NOTIFY_PROVIDER_CHANGED = 2;
    public static final int MSG_NOTIFY_UPDATE_APP_WIDGET = 1;
    public static final int MSG_NOTIFY_VIEW_DATA_CHANGED = 4;
    
    public CallbackHandler(Looper paramLooper)
    {
      super(null, false);
    }
    
    public void handleMessage(Message paramMessage)
    {
      Object localObject;
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        paramMessage = (SomeArgs)paramMessage.obj;
        localHost = (AppWidgetServiceImpl.Host)paramMessage.arg1;
        localIAppWidgetHost = (IAppWidgetHost)paramMessage.arg2;
        localObject = (RemoteViews)paramMessage.arg3;
        l = ((Long)paramMessage.arg4).longValue();
        i = paramMessage.argi1;
        paramMessage.recycle();
        AppWidgetServiceImpl.-wrap9(AppWidgetServiceImpl.this, localHost, localIAppWidgetHost, i, (RemoteViews)localObject, l);
        return;
      case 2: 
        paramMessage = (SomeArgs)paramMessage.obj;
        localHost = (AppWidgetServiceImpl.Host)paramMessage.arg1;
        localIAppWidgetHost = (IAppWidgetHost)paramMessage.arg2;
        localObject = (AppWidgetProviderInfo)paramMessage.arg3;
        l = ((Long)paramMessage.arg4).longValue();
        i = paramMessage.argi1;
        paramMessage.recycle();
        AppWidgetServiceImpl.-wrap7(AppWidgetServiceImpl.this, localHost, localIAppWidgetHost, i, (AppWidgetProviderInfo)localObject, l);
        return;
      case 3: 
        paramMessage = (SomeArgs)paramMessage.obj;
        localHost = (AppWidgetServiceImpl.Host)paramMessage.arg1;
        localIAppWidgetHost = (IAppWidgetHost)paramMessage.arg2;
        paramMessage.recycle();
        AppWidgetServiceImpl.-wrap8(AppWidgetServiceImpl.this, localHost, localIAppWidgetHost);
        return;
      }
      paramMessage = (SomeArgs)paramMessage.obj;
      AppWidgetServiceImpl.Host localHost = (AppWidgetServiceImpl.Host)paramMessage.arg1;
      IAppWidgetHost localIAppWidgetHost = (IAppWidgetHost)paramMessage.arg2;
      long l = ((Long)paramMessage.arg3).longValue();
      int i = paramMessage.argi1;
      int j = paramMessage.argi2;
      paramMessage.recycle();
      AppWidgetServiceImpl.-wrap6(AppWidgetServiceImpl.this, localHost, localIAppWidgetHost, i, j, l);
    }
  }
  
  private static final class Host
  {
    IAppWidgetHost callbacks;
    AppWidgetServiceImpl.HostId id;
    long lastWidgetUpdateTime;
    int tag = -1;
    ArrayList<AppWidgetServiceImpl.Widget> widgets = new ArrayList();
    boolean zombie;
    
    private boolean hostsPackageForUser(String paramString, int paramInt)
    {
      int j = this.widgets.size();
      int i = 0;
      while (i < j)
      {
        AppWidgetServiceImpl.Provider localProvider = ((AppWidgetServiceImpl.Widget)this.widgets.get(i)).provider;
        if ((localProvider != null) && (localProvider.getUserId() == paramInt) && (localProvider.info != null) && (paramString.equals(localProvider.info.provider.getPackageName()))) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public boolean getPendingUpdatesForId(int paramInt, LongSparseArray<PendingHostUpdate> paramLongSparseArray)
    {
      long l1 = this.lastWidgetUpdateTime;
      int j = this.widgets.size();
      int i = 0;
      while (i < j)
      {
        AppWidgetServiceImpl.Widget localWidget = (AppWidgetServiceImpl.Widget)this.widgets.get(i);
        if (localWidget.appWidgetId == paramInt)
        {
          paramLongSparseArray.clear();
          i = localWidget.updateTimes.size() - 1;
          while (i >= 0)
          {
            long l2 = localWidget.updateTimes.valueAt(i);
            if (l2 <= l1)
            {
              i -= 1;
            }
            else
            {
              j = localWidget.updateTimes.keyAt(i);
              PendingHostUpdate localPendingHostUpdate;
              switch (j)
              {
              default: 
                localPendingHostUpdate = PendingHostUpdate.viewDataChanged(paramInt, j);
              }
              for (;;)
              {
                paramLongSparseArray.put(l2, localPendingHostUpdate);
                break;
                localPendingHostUpdate = PendingHostUpdate.providerChanged(paramInt, localWidget.provider.info);
                continue;
                localPendingHostUpdate = PendingHostUpdate.updateAppWidget(paramInt, AppWidgetServiceImpl.-wrap0(localWidget.getEffectiveViewsLocked()));
              }
            }
          }
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public int getUserId()
    {
      return UserHandle.getUserId(this.id.uid);
    }
    
    public boolean isInPackageForUser(String paramString, int paramInt)
    {
      if (getUserId() == paramInt) {
        return this.id.packageName.equals(paramString);
      }
      return false;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Host{").append(this.id);
      if (this.zombie) {}
      for (String str = " Z";; str = "") {
        return str + '}';
      }
    }
  }
  
  private static final class HostId
  {
    final int hostId;
    final String packageName;
    final int uid;
    
    public HostId(int paramInt1, int paramInt2, String paramString)
    {
      this.uid = paramInt1;
      this.hostId = paramInt2;
      this.packageName = paramString;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (HostId)paramObject;
      if (this.uid != ((HostId)paramObject).uid) {
        return false;
      }
      if (this.hostId != ((HostId)paramObject).hostId) {
        return false;
      }
      if (this.packageName == null)
      {
        if (((HostId)paramObject).packageName != null) {
          return false;
        }
      }
      else if (!this.packageName.equals(((HostId)paramObject).packageName)) {
        return false;
      }
      return true;
    }
    
    public int hashCode()
    {
      int j = this.uid;
      int k = this.hostId;
      if (this.packageName != null) {}
      for (int i = this.packageName.hashCode();; i = 0) {
        return (j * 31 + k) * 31 + i;
      }
    }
    
    public String toString()
    {
      return "HostId{user:" + UserHandle.getUserId(this.uid) + ", app:" + UserHandle.getAppId(this.uid) + ", hostId:" + this.hostId + ", pkg:" + this.packageName + '}';
    }
  }
  
  private class LoadedWidgetState
  {
    final int hostTag;
    final int providerTag;
    final AppWidgetServiceImpl.Widget widget;
    
    public LoadedWidgetState(AppWidgetServiceImpl.Widget paramWidget, int paramInt1, int paramInt2)
    {
      this.widget = paramWidget;
      this.hostTag = paramInt1;
      this.providerTag = paramInt2;
    }
  }
  
  private static final class Provider
  {
    PendingIntent broadcast;
    AppWidgetServiceImpl.ProviderId id;
    AppWidgetProviderInfo info;
    boolean maskedByLockedProfile;
    boolean maskedByQuietProfile;
    boolean maskedBySuspendedPackage;
    int tag = -1;
    ArrayList<AppWidgetServiceImpl.Widget> widgets = new ArrayList();
    boolean zombie;
    
    public int getUserId()
    {
      return UserHandle.getUserId(this.id.uid);
    }
    
    public boolean hostedByPackageForUser(String paramString, int paramInt)
    {
      int j = this.widgets.size();
      int i = 0;
      while (i < j)
      {
        AppWidgetServiceImpl.Widget localWidget = (AppWidgetServiceImpl.Widget)this.widgets.get(i);
        if ((paramString.equals(localWidget.host.id.packageName)) && (localWidget.host.getUserId() == paramInt)) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public boolean isInPackageForUser(String paramString, int paramInt)
    {
      if (getUserId() == paramInt) {
        return this.id.componentName.getPackageName().equals(paramString);
      }
      return false;
    }
    
    public boolean isMaskedLocked()
    {
      if ((!this.maskedByQuietProfile) && (!this.maskedByLockedProfile)) {
        return this.maskedBySuspendedPackage;
      }
      return true;
    }
    
    public boolean setMaskedByLockedProfileLocked(boolean paramBoolean)
    {
      boolean bool = this.maskedByLockedProfile;
      this.maskedByLockedProfile = paramBoolean;
      return paramBoolean != bool;
    }
    
    public boolean setMaskedByQuietProfileLocked(boolean paramBoolean)
    {
      boolean bool = this.maskedByQuietProfile;
      this.maskedByQuietProfile = paramBoolean;
      return paramBoolean != bool;
    }
    
    public boolean setMaskedBySuspendedPackageLocked(boolean paramBoolean)
    {
      boolean bool = this.maskedBySuspendedPackage;
      this.maskedBySuspendedPackage = paramBoolean;
      return paramBoolean != bool;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Provider{").append(this.id);
      if (this.zombie) {}
      for (String str = " Z";; str = "") {
        return str + '}';
      }
    }
  }
  
  private static final class ProviderId
  {
    final ComponentName componentName;
    final int uid;
    
    private ProviderId(int paramInt, ComponentName paramComponentName)
    {
      this.uid = paramInt;
      this.componentName = paramComponentName;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (ProviderId)paramObject;
      if (this.uid != ((ProviderId)paramObject).uid) {
        return false;
      }
      if (this.componentName == null)
      {
        if (((ProviderId)paramObject).componentName != null) {
          return false;
        }
      }
      else if (!this.componentName.equals(((ProviderId)paramObject).componentName)) {
        return false;
      }
      return true;
    }
    
    public int hashCode()
    {
      int j = this.uid;
      if (this.componentName != null) {}
      for (int i = this.componentName.hashCode();; i = 0) {
        return j * 31 + i;
      }
    }
    
    public String toString()
    {
      return "ProviderId{user:" + UserHandle.getUserId(this.uid) + ", app:" + UserHandle.getAppId(this.uid) + ", cmp:" + this.componentName + '}';
    }
  }
  
  private final class SaveStateRunnable
    implements Runnable
  {
    final int mUserId;
    
    public SaveStateRunnable(int paramInt)
    {
      this.mUserId = paramInt;
    }
    
    public void run()
    {
      synchronized (AppWidgetServiceImpl.-get5(AppWidgetServiceImpl.this))
      {
        AppWidgetServiceImpl.-wrap4(AppWidgetServiceImpl.this, this.mUserId, false);
        AppWidgetServiceImpl.-wrap14(AppWidgetServiceImpl.this, this.mUserId);
        return;
      }
    }
  }
  
  private final class SecurityPolicy
  {
    private SecurityPolicy() {}
    
    private boolean isCallerBindAppWidgetWhiteListedLocked(String paramString)
    {
      int i = UserHandle.getCallingUserId();
      if (AppWidgetServiceImpl.-wrap2(AppWidgetServiceImpl.this, paramString, i) < 0) {
        throw new IllegalArgumentException("No package " + paramString + " for user " + i);
      }
      synchronized (AppWidgetServiceImpl.-get5(AppWidgetServiceImpl.this))
      {
        AppWidgetServiceImpl.-wrap5(AppWidgetServiceImpl.this, i);
        paramString = Pair.create(Integer.valueOf(i), paramString);
        boolean bool = AppWidgetServiceImpl.-get7(AppWidgetServiceImpl.this).contains(paramString);
        return bool;
      }
    }
    
    private boolean isParentOrProfile(int paramInt1, int paramInt2)
    {
      if (paramInt1 == paramInt2) {
        return true;
      }
      return getProfileParent(paramInt2) == paramInt1;
    }
    
    private boolean isProfileEnabled(int paramInt)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        UserInfo localUserInfo = AppWidgetServiceImpl.-get10(AppWidgetServiceImpl.this).getUserInfo(paramInt);
        if (localUserInfo != null)
        {
          boolean bool = localUserInfo.isEnabled();
          if (bool) {
            return true;
          }
        }
        return false;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean canAccessAppWidget(AppWidgetServiceImpl.Widget paramWidget, int paramInt, String paramString)
    {
      if (isHostInPackageForUid(paramWidget.host, paramInt, paramString)) {
        return true;
      }
      if (isProviderInPackageForUid(paramWidget.provider, paramInt, paramString)) {
        return true;
      }
      if (isHostAccessingProvider(paramWidget.host, paramWidget.provider, paramInt, paramString)) {
        return true;
      }
      paramInt = UserHandle.getUserId(paramInt);
      return ((paramWidget.host.getUserId() == paramInt) || ((paramWidget.provider != null) && (paramWidget.provider.getUserId() == paramInt))) && (AppWidgetServiceImpl.-get2(AppWidgetServiceImpl.this).checkCallingPermission("android.permission.BIND_APPWIDGET") == 0);
    }
    
    public void enforceCallFromPackage(String paramString)
    {
      AppWidgetServiceImpl.-get1(AppWidgetServiceImpl.this).checkPackage(Binder.getCallingUid(), paramString);
    }
    
    public void enforceModifyAppWidgetBindPermissions(String paramString)
    {
      AppWidgetServiceImpl.-get2(AppWidgetServiceImpl.this).enforceCallingPermission("android.permission.MODIFY_APPWIDGET_BIND_PERMISSIONS", "hasBindAppWidgetPermission packageName=" + paramString);
    }
    
    public void enforceServiceExistsAndRequiresBindRemoteViewsPermission(ComponentName paramComponentName, int paramInt)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        localServiceInfo = AppWidgetServiceImpl.-get6(AppWidgetServiceImpl.this).getServiceInfo(paramComponentName, 4096, paramInt);
        if (localServiceInfo == null) {
          throw new SecurityException("Service " + paramComponentName + " not installed for user " + paramInt);
        }
      }
      catch (RemoteException paramComponentName)
      {
        ServiceInfo localServiceInfo;
        return;
        if (!"android.permission.BIND_REMOTEVIEWS".equals(localServiceInfo.permission)) {
          throw new SecurityException("Service " + paramComponentName + " in user " + paramInt + "does not require " + "android.permission.BIND_REMOTEVIEWS");
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      Binder.restoreCallingIdentity(l);
    }
    
    public int[] getEnabledGroupProfileIds(int paramInt)
    {
      paramInt = getGroupParent(paramInt);
      long l = Binder.clearCallingIdentity();
      try
      {
        int[] arrayOfInt = AppWidgetServiceImpl.-get10(AppWidgetServiceImpl.this).getEnabledProfileIds(paramInt);
        return arrayOfInt;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public int getGroupParent(int paramInt)
    {
      int i = AppWidgetServiceImpl.-get9(AppWidgetServiceImpl.this).getProfileParent(paramInt);
      if (i != -10) {
        return i;
      }
      return paramInt;
    }
    
    public int getProfileParent(int paramInt)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        UserInfo localUserInfo = AppWidgetServiceImpl.-get10(AppWidgetServiceImpl.this).getProfileParent(paramInt);
        if (localUserInfo != null)
        {
          paramInt = localUserInfo.getUserHandle().getIdentifier();
          return paramInt;
        }
        return -10;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean hasCallerBindPermissionOrBindWhiteListedLocked(String paramString)
    {
      try
      {
        AppWidgetServiceImpl.-get2(AppWidgetServiceImpl.this).enforceCallingOrSelfPermission("android.permission.BIND_APPWIDGET", null);
        return true;
      }
      catch (SecurityException localSecurityException)
      {
        while (isCallerBindAppWidgetWhiteListedLocked(paramString)) {}
      }
      return false;
    }
    
    public boolean isEnabledGroupProfile(int paramInt)
    {
      if (isParentOrProfile(UserHandle.getCallingUserId(), paramInt)) {
        return isProfileEnabled(paramInt);
      }
      return false;
    }
    
    public boolean isHostAccessingProvider(AppWidgetServiceImpl.Host paramHost, AppWidgetServiceImpl.Provider paramProvider, int paramInt, String paramString)
    {
      if ((paramHost.id.uid == paramInt) && (paramProvider != null)) {
        return paramProvider.id.componentName.getPackageName().equals(paramString);
      }
      return false;
    }
    
    public boolean isHostInPackageForUid(AppWidgetServiceImpl.Host paramHost, int paramInt, String paramString)
    {
      if (paramHost.id.uid == paramInt) {
        return paramHost.id.packageName.equals(paramString);
      }
      return false;
    }
    
    public boolean isProviderInCallerOrInProfileAndWhitelListed(String paramString, int paramInt)
    {
      int i = UserHandle.getCallingUserId();
      if (paramInt == i) {
        return true;
      }
      if (getProfileParent(paramInt) != i) {
        return false;
      }
      return isProviderWhiteListed(paramString, paramInt);
    }
    
    public boolean isProviderInPackageForUid(AppWidgetServiceImpl.Provider paramProvider, int paramInt, String paramString)
    {
      if ((paramProvider != null) && (paramProvider.id.uid == paramInt)) {
        return paramProvider.id.componentName.getPackageName().equals(paramString);
      }
      return false;
    }
    
    public boolean isProviderWhiteListed(String paramString, int paramInt)
    {
      if (AppWidgetServiceImpl.-get3(AppWidgetServiceImpl.this) == null) {
        return false;
      }
      return AppWidgetServiceImpl.-get3(AppWidgetServiceImpl.this).getCrossProfileWidgetProviders(paramInt).contains(paramString);
    }
  }
  
  private static final class ServiceConnectionProxy
    implements ServiceConnection
  {
    private final IRemoteViewsAdapterConnection mConnectionCb;
    
    ServiceConnectionProxy(IBinder paramIBinder)
    {
      this.mConnectionCb = IRemoteViewsAdapterConnection.Stub.asInterface(paramIBinder);
    }
    
    public void disconnect()
    {
      try
      {
        this.mConnectionCb.onServiceDisconnected();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("AppWidgetServiceImpl", "Error clearing service interface", localRemoteException);
      }
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      try
      {
        this.mConnectionCb.onServiceConnected(paramIBinder);
        return;
      }
      catch (RemoteException paramComponentName)
      {
        Slog.e("AppWidgetServiceImpl", "Error passing service interface", paramComponentName);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      disconnect();
    }
  }
  
  private static final class Widget
  {
    int appWidgetId;
    AppWidgetServiceImpl.Host host;
    RemoteViews maskedViews;
    Bundle options;
    AppWidgetServiceImpl.Provider provider;
    int restoredId;
    SparseLongArray updateTimes = new SparseLongArray(2);
    RemoteViews views;
    
    private boolean clearMaskedViewsLocked()
    {
      if (this.maskedViews != null)
      {
        this.maskedViews = null;
        return true;
      }
      return false;
    }
    
    private boolean replaceWithMaskedViewsLocked(RemoteViews paramRemoteViews)
    {
      this.maskedViews = paramRemoteViews;
      return true;
    }
    
    public RemoteViews getEffectiveViewsLocked()
    {
      if (this.maskedViews != null) {
        return this.maskedViews;
      }
      return this.views;
    }
    
    public String toString()
    {
      return "AppWidgetId{" + this.appWidgetId + ':' + this.host + ':' + this.provider + '}';
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/appwidget/AppWidgetServiceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */