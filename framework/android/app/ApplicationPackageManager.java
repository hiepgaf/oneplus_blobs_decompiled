package android.app;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.EphemeralApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.IOnPermissionsChangeListener;
import android.content.pm.IOnPermissionsChangeListener.Stub;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageMoveObserver.Stub;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.InstrumentationInfo;
import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.KeySet;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.LegacyPackageInstallObserver;
import android.content.pm.PackageManager.MoveCallback;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager.OnPermissionsChangedListener;
import android.content.pm.ParceledListSlice;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.VerifierDeviceIdentity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.provider.Settings.Global;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.Preconditions;
import com.android.internal.util.UserIcons;
import dalvik.system.VMRuntime;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import libcore.util.EmptyArray;

public class ApplicationPackageManager
  extends PackageManager
{
  private static final boolean DEBUG_ICONS = false;
  private static final int DEFAULT_EPHEMERAL_COOKIE_MAX_SIZE_BYTES = 16384;
  private static final String TAG = "ApplicationPackageManager";
  private static final int sDefaultFlags = 1024;
  private static ArrayMap<ResourceName, WeakReference<Drawable.ConstantState>> sIconCache = new ArrayMap();
  private static ArrayMap<ResourceName, WeakReference<CharSequence>> sStringCache = new ArrayMap();
  private static final Object sSync = new Object();
  volatile int mCachedSafeMode = -1;
  private final ContextImpl mContext;
  @GuardedBy("mDelegates")
  private final ArrayList<MoveCallbackDelegate> mDelegates = new ArrayList();
  @GuardedBy("mLock")
  private PackageInstaller mInstaller;
  private final Object mLock = new Object();
  private final IPackageManager mPM;
  private final Map<PackageManager.OnPermissionsChangedListener, IOnPermissionsChangeListener> mPermissionListeners = new ArrayMap();
  @GuardedBy("mLock")
  private String mPermissionsControllerPackageName;
  @GuardedBy("mLock")
  private UserManager mUserManager;
  
  ApplicationPackageManager(ContextImpl paramContextImpl, IPackageManager paramIPackageManager)
  {
    this.mContext = paramContextImpl;
    this.mPM = paramIPackageManager;
  }
  
  static void configurationChanged()
  {
    synchronized (sSync)
    {
      sIconCache.clear();
      sStringCache.clear();
      return;
    }
  }
  
  private int getBadgeResIdForUser(int paramInt)
  {
    if (isManagedProfile(paramInt)) {
      return 17302314;
    }
    return 0;
  }
  
  private Drawable getBadgedDrawable(Drawable paramDrawable1, Drawable paramDrawable2, Rect paramRect, boolean paramBoolean)
  {
    int i = paramDrawable1.getIntrinsicWidth();
    int j = paramDrawable1.getIntrinsicHeight();
    Bitmap localBitmap;
    label50:
    Canvas localCanvas;
    if ((paramBoolean) && ((paramDrawable1 instanceof BitmapDrawable)))
    {
      paramBoolean = ((BitmapDrawable)paramDrawable1).getBitmap().isMutable();
      if (!paramBoolean) {
        break label154;
      }
      localBitmap = ((BitmapDrawable)paramDrawable1).getBitmap();
      localCanvas = new Canvas(localBitmap);
      if (!paramBoolean)
      {
        paramDrawable1.setBounds(0, 0, i, j);
        paramDrawable1.draw(localCanvas);
      }
      if (paramRect == null) {
        break label278;
      }
      if ((paramRect.left >= 0) && (paramRect.top >= 0)) {
        break label169;
      }
    }
    label154:
    label169:
    while ((paramRect.width() > i) || (paramRect.height() > j))
    {
      throw new IllegalArgumentException("Badge location " + paramRect + " not in badged drawable bounds " + new Rect(0, 0, i, j));
      paramBoolean = false;
      break;
      localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
      break label50;
    }
    paramDrawable2.setBounds(0, 0, paramRect.width(), paramRect.height());
    localCanvas.save();
    localCanvas.translate(paramRect.left, paramRect.top);
    paramDrawable2.draw(localCanvas);
    localCanvas.restore();
    while (!paramBoolean)
    {
      paramDrawable2 = new BitmapDrawable(this.mContext.getResources(), localBitmap);
      if ((paramDrawable1 instanceof BitmapDrawable)) {
        paramDrawable2.setTargetDensity(((BitmapDrawable)paramDrawable1).getBitmap().getDensity());
      }
      return paramDrawable2;
      label278:
      paramDrawable2.setBounds(0, 0, i, j);
      paramDrawable2.draw(localCanvas);
    }
    return paramDrawable1;
  }
  
  private Drawable getCachedIcon(ResourceName paramResourceName)
  {
    synchronized (sSync)
    {
      Object localObject2 = (WeakReference)sIconCache.get(paramResourceName);
      if (localObject2 != null)
      {
        localObject2 = (Drawable.ConstantState)((WeakReference)localObject2).get();
        if (localObject2 != null)
        {
          paramResourceName = ((Drawable.ConstantState)localObject2).newDrawable();
          return paramResourceName;
        }
        sIconCache.remove(paramResourceName);
      }
      return null;
    }
  }
  
  private CharSequence getCachedString(ResourceName paramResourceName)
  {
    synchronized (sSync)
    {
      Object localObject2 = (WeakReference)sStringCache.get(paramResourceName);
      if (localObject2 != null)
      {
        localObject2 = (CharSequence)((WeakReference)localObject2).get();
        if (localObject2 != null) {
          return (CharSequence)localObject2;
        }
        sStringCache.remove(paramResourceName);
      }
      return null;
    }
  }
  
  private Drawable getDrawableForDensity(int paramInt1, int paramInt2)
  {
    int i = paramInt2;
    if (paramInt2 <= 0) {
      i = this.mContext.getResources().getDisplayMetrics().densityDpi;
    }
    return Resources.getSystem().getDrawableForDensity(paramInt1, i);
  }
  
  private Drawable getManagedProfileIconForDensity(UserHandle paramUserHandle, int paramInt1, int paramInt2)
  {
    if (isManagedProfile(paramUserHandle.getIdentifier())) {
      return getDrawableForDensity(paramInt1, paramInt2);
    }
    return null;
  }
  
  static void handlePackageBroadcast(int paramInt, String[] paramArrayOfString, boolean paramBoolean)
  {
    int j = 0;
    int i = 0;
    if (paramInt == 1) {
      i = 1;
    }
    String str;
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
    {
      paramInt = 0;
      int m = paramArrayOfString.length;
      if (j < m) {
        str = paramArrayOfString[j];
      }
    }
    for (;;)
    {
      int k;
      synchronized (sSync)
      {
        k = sIconCache.size() - 1;
        if (k >= 0)
        {
          if (!((ResourceName)sIconCache.keyAt(k)).packageName.equals(str)) {
            break label200;
          }
          sIconCache.removeAt(k);
          paramInt = 1;
          break label200;
        }
        k = sStringCache.size() - 1;
        if (k >= 0)
        {
          if (((ResourceName)sStringCache.keyAt(k)).packageName.equals(str))
          {
            sStringCache.removeAt(k);
            paramInt = 1;
          }
          k -= 1;
          continue;
        }
        j += 1;
      }
      if ((paramInt != 0) || (paramBoolean))
      {
        if (i != 0) {
          Runtime.getRuntime().gc();
        }
      }
      else {
        return;
      }
      ActivityThread.currentActivityThread().scheduleGcIdler();
      return;
      label200:
      k -= 1;
    }
  }
  
  private void installCommon(Uri paramUri, PackageInstallObserver paramPackageInstallObserver, int paramInt1, String paramString, int paramInt2)
  {
    if (!"file".equals(paramUri.getScheme())) {
      throw new UnsupportedOperationException("Only file:// URIs are supported");
    }
    paramUri = paramUri.getPath();
    try
    {
      this.mPM.installPackageAsUser(paramUri, paramPackageInstallObserver.getBinder(), paramInt1, paramString, paramInt2);
      return;
    }
    catch (RemoteException paramUri)
    {
      throw paramUri.rethrowFromSystemServer();
    }
  }
  
  private boolean isManagedProfile(int paramInt)
  {
    return getUserManager().isManagedProfile(paramInt);
  }
  
  private boolean isPackageCandidateVolume(ContextImpl paramContextImpl, ApplicationInfo paramApplicationInfo, VolumeInfo paramVolumeInfo)
  {
    if (Settings.Global.getInt(paramContextImpl.getContentResolver(), "force_allow_on_external", 0) != 0) {}
    for (int i = 1; "private".equals(paramVolumeInfo.getId()); i = 0) {
      return true;
    }
    if (paramApplicationInfo.isSystemApp()) {
      return false;
    }
    if ((i == 0) && ((paramApplicationInfo.installLocation == 1) || (paramApplicationInfo.installLocation == -1))) {
      return false;
    }
    if (!paramVolumeInfo.isMountedWritable()) {
      return false;
    }
    if (paramVolumeInfo.isPrimaryPhysical()) {
      return paramApplicationInfo.isInternal();
    }
    try
    {
      boolean bool = this.mPM.isPackageDeviceAdminOnAnyUser(paramApplicationInfo.packageName);
      if (bool) {
        return false;
      }
    }
    catch (RemoteException paramContextImpl)
    {
      throw paramContextImpl.rethrowFromSystemServer();
    }
    return paramVolumeInfo.getType() == 1;
  }
  
  private static boolean isPrimaryStorageCandidateVolume(VolumeInfo paramVolumeInfo)
  {
    if ("private".equals(paramVolumeInfo.getId())) {
      return true;
    }
    if (!paramVolumeInfo.isMountedWritable()) {
      return false;
    }
    return paramVolumeInfo.getType() == 1;
  }
  
  private static ApplicationInfo maybeAdjustApplicationInfo(ApplicationInfo paramApplicationInfo)
  {
    if ((paramApplicationInfo.primaryCpuAbi != null) && (paramApplicationInfo.secondaryCpuAbi != null))
    {
      String str2 = VMRuntime.getRuntime().vmInstructionSet();
      Object localObject = VMRuntime.getInstructionSet(paramApplicationInfo.secondaryCpuAbi);
      String str1 = SystemProperties.get("ro.dalvik.vm.isa." + (String)localObject);
      if (str1.isEmpty()) {}
      while (str2.equals(localObject))
      {
        localObject = new ApplicationInfo(paramApplicationInfo);
        ((ApplicationInfo)localObject).nativeLibraryDir = paramApplicationInfo.secondaryNativeLibraryDir;
        return (ApplicationInfo)localObject;
        localObject = str1;
      }
    }
    return paramApplicationInfo;
  }
  
  private void putCachedIcon(ResourceName paramResourceName, Drawable paramDrawable)
  {
    synchronized (sSync)
    {
      sIconCache.put(paramResourceName, new WeakReference(paramDrawable.getConstantState()));
      return;
    }
  }
  
  private void putCachedString(ResourceName paramResourceName, CharSequence paramCharSequence)
  {
    synchronized (sSync)
    {
      sStringCache.put(paramResourceName, new WeakReference(paramCharSequence));
      return;
    }
  }
  
  public void addCrossProfileIntentFilter(IntentFilter paramIntentFilter, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      this.mPM.addCrossProfileIntentFilter(paramIntentFilter, this.mContext.getOpPackageName(), paramInt1, paramInt2, paramInt3);
      return;
    }
    catch (RemoteException paramIntentFilter)
    {
      throw paramIntentFilter.rethrowFromSystemServer();
    }
  }
  
  public void addOnPermissionsChangeListener(PackageManager.OnPermissionsChangedListener paramOnPermissionsChangedListener)
  {
    synchronized (this.mPermissionListeners)
    {
      Object localObject = this.mPermissionListeners.get(paramOnPermissionsChangedListener);
      if (localObject != null) {
        return;
      }
      localObject = new OnPermissionsChangeListenerDelegate(paramOnPermissionsChangedListener, Looper.getMainLooper());
      try
      {
        this.mPM.addOnPermissionsChangeListener((IOnPermissionsChangeListener)localObject);
        this.mPermissionListeners.put(paramOnPermissionsChangedListener, localObject);
        return;
      }
      catch (RemoteException paramOnPermissionsChangedListener)
      {
        throw paramOnPermissionsChangedListener.rethrowFromSystemServer();
      }
    }
  }
  
  public void addPackageToPreferred(String paramString)
  {
    Log.w("ApplicationPackageManager", "addPackageToPreferred() is a no-op");
  }
  
  public boolean addPermission(PermissionInfo paramPermissionInfo)
  {
    try
    {
      boolean bool = this.mPM.addPermission(paramPermissionInfo);
      return bool;
    }
    catch (RemoteException paramPermissionInfo)
    {
      throw paramPermissionInfo.rethrowFromSystemServer();
    }
  }
  
  public boolean addPermissionAsync(PermissionInfo paramPermissionInfo)
  {
    try
    {
      boolean bool = this.mPM.addPermissionAsync(paramPermissionInfo);
      return bool;
    }
    catch (RemoteException paramPermissionInfo)
    {
      throw paramPermissionInfo.rethrowFromSystemServer();
    }
  }
  
  public void addPreferredActivity(IntentFilter paramIntentFilter, int paramInt, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName)
  {
    try
    {
      this.mPM.addPreferredActivity(paramIntentFilter, paramInt, paramArrayOfComponentName, paramComponentName, this.mContext.getUserId());
      return;
    }
    catch (RemoteException paramIntentFilter)
    {
      throw paramIntentFilter.rethrowFromSystemServer();
    }
  }
  
  public void addPreferredActivityAsUser(IntentFilter paramIntentFilter, int paramInt1, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName, int paramInt2)
  {
    try
    {
      this.mPM.addPreferredActivity(paramIntentFilter, paramInt1, paramArrayOfComponentName, paramComponentName, paramInt2);
      return;
    }
    catch (RemoteException paramIntentFilter)
    {
      throw paramIntentFilter.rethrowFromSystemServer();
    }
  }
  
  public String[] canonicalToCurrentPackageNames(String[] paramArrayOfString)
  {
    try
    {
      paramArrayOfString = this.mPM.canonicalToCurrentPackageNames(paramArrayOfString);
      return paramArrayOfString;
    }
    catch (RemoteException paramArrayOfString)
    {
      throw paramArrayOfString.rethrowFromSystemServer();
    }
  }
  
  public int checkPermission(String paramString1, String paramString2)
  {
    try
    {
      int i = this.mPM.checkPermission(paramString1, paramString2, this.mContext.getUserId());
      return i;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public int checkPermissionByUserId(String paramString1, String paramString2, int paramInt)
  {
    try
    {
      paramInt = this.mPM.checkPermission(paramString1, paramString2, paramInt);
      return paramInt;
    }
    catch (RemoteException paramString1)
    {
      throw new RuntimeException("Package manager has died", paramString1);
    }
  }
  
  public int checkSignatures(int paramInt1, int paramInt2)
  {
    try
    {
      paramInt1 = this.mPM.checkUidSignatures(paramInt1, paramInt2);
      return paramInt1;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int checkSignatures(String paramString1, String paramString2)
  {
    try
    {
      int i = this.mPM.checkSignatures(paramString1, paramString2);
      return i;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public void clearApplicationUserData(String paramString, IPackageDataObserver paramIPackageDataObserver)
  {
    try
    {
      this.mPM.clearApplicationUserData(paramString, paramIPackageDataObserver, this.mContext.getUserId());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void clearCrossProfileIntentFilters(int paramInt)
  {
    try
    {
      this.mPM.clearCrossProfileIntentFilters(paramInt, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void clearPackagePreferredActivities(String paramString)
  {
    try
    {
      this.mPM.clearPackagePreferredActivities(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public String[] currentToCanonicalPackageNames(String[] paramArrayOfString)
  {
    try
    {
      paramArrayOfString = this.mPM.currentToCanonicalPackageNames(paramArrayOfString);
      return paramArrayOfString;
    }
    catch (RemoteException paramArrayOfString)
    {
      throw paramArrayOfString.rethrowFromSystemServer();
    }
  }
  
  public void deleteApplicationCacheFiles(String paramString, IPackageDataObserver paramIPackageDataObserver)
  {
    try
    {
      this.mPM.deleteApplicationCacheFiles(paramString, paramIPackageDataObserver);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void deleteApplicationCacheFilesAsUser(String paramString, int paramInt, IPackageDataObserver paramIPackageDataObserver)
  {
    try
    {
      this.mPM.deleteApplicationCacheFilesAsUser(paramString, paramInt, paramIPackageDataObserver);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void deletePackage(String paramString, IPackageDeleteObserver paramIPackageDeleteObserver, int paramInt)
  {
    deletePackageAsUser(paramString, paramIPackageDeleteObserver, paramInt, this.mContext.getUserId());
  }
  
  public void deletePackageAsUser(String paramString, IPackageDeleteObserver paramIPackageDeleteObserver, int paramInt1, int paramInt2)
  {
    try
    {
      this.mPM.deletePackageAsUser(paramString, paramIPackageDeleteObserver, paramInt2, paramInt1);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void extendVerificationTimeout(int paramInt1, int paramInt2, long paramLong)
  {
    try
    {
      this.mPM.extendVerificationTimeout(paramInt1, paramInt2, paramLong);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void flushPackageRestrictionsAsUser(int paramInt)
  {
    try
    {
      this.mPM.flushPackageRestrictionsAsUser(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void freeStorage(String paramString, long paramLong, IntentSender paramIntentSender)
  {
    try
    {
      this.mPM.freeStorage(paramString, paramLong, paramIntentSender);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void freeStorageAndNotify(String paramString, long paramLong, IPackageDataObserver paramIPackageDataObserver)
  {
    try
    {
      this.mPM.freeStorageAndNotify(paramString, paramLong, paramIPackageDataObserver);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public Drawable getActivityBanner(ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException
  {
    return getActivityInfo(paramComponentName, 1024).loadBanner(this);
  }
  
  public Drawable getActivityBanner(Intent paramIntent)
    throws PackageManager.NameNotFoundException
  {
    if (paramIntent.getComponent() != null) {
      return getActivityBanner(paramIntent.getComponent());
    }
    ResolveInfo localResolveInfo = resolveActivity(paramIntent, 65536);
    if (localResolveInfo != null) {
      return localResolveInfo.activityInfo.loadBanner(this);
    }
    throw new PackageManager.NameNotFoundException(paramIntent.toUri(0));
  }
  
  public Drawable getActivityIcon(ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException
  {
    return getActivityInfo(paramComponentName, 1024).loadIcon(this);
  }
  
  public Drawable getActivityIcon(Intent paramIntent)
    throws PackageManager.NameNotFoundException
  {
    if (paramIntent.getComponent() != null) {
      return getActivityIcon(paramIntent.getComponent());
    }
    ResolveInfo localResolveInfo = resolveActivity(paramIntent, 65536);
    if (localResolveInfo != null) {
      return localResolveInfo.activityInfo.loadIcon(this);
    }
    throw new PackageManager.NameNotFoundException(paramIntent.toUri(0));
  }
  
  public ActivityInfo getActivityInfo(ComponentName paramComponentName, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      ActivityInfo localActivityInfo = this.mPM.getActivityInfo(paramComponentName, paramInt, this.mContext.getUserId());
      if (localActivityInfo != null) {
        return localActivityInfo;
      }
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException(paramComponentName.toString());
  }
  
  public Drawable getActivityLogo(ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException
  {
    return getActivityInfo(paramComponentName, 1024).loadLogo(this);
  }
  
  public Drawable getActivityLogo(Intent paramIntent)
    throws PackageManager.NameNotFoundException
  {
    if (paramIntent.getComponent() != null) {
      return getActivityLogo(paramIntent.getComponent());
    }
    ResolveInfo localResolveInfo = resolveActivity(paramIntent, 65536);
    if (localResolveInfo != null) {
      return localResolveInfo.activityInfo.loadLogo(this);
    }
    throw new PackageManager.NameNotFoundException(paramIntent.toUri(0));
  }
  
  public List<IntentFilter> getAllIntentFilters(String paramString)
  {
    try
    {
      paramString = this.mPM.getAllIntentFilters(paramString);
      if (paramString == null) {
        return Collections.emptyList();
      }
      paramString = paramString.getList();
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public List<PermissionGroupInfo> getAllPermissionGroups(int paramInt)
  {
    try
    {
      Object localObject = this.mPM.getAllPermissionGroups(paramInt);
      if (localObject == null) {
        return Collections.emptyList();
      }
      localObject = ((ParceledListSlice)localObject).getList();
      return (List<PermissionGroupInfo>)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Drawable getApplicationBanner(ApplicationInfo paramApplicationInfo)
  {
    return paramApplicationInfo.loadBanner(this);
  }
  
  public Drawable getApplicationBanner(String paramString)
    throws PackageManager.NameNotFoundException
  {
    return getApplicationBanner(getApplicationInfo(paramString, 1024));
  }
  
  public int getApplicationEnabledSetting(String paramString)
  {
    try
    {
      int i = this.mPM.getApplicationEnabledSetting(paramString, this.mContext.getUserId());
      return i;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean getApplicationHiddenSettingAsUser(String paramString, UserHandle paramUserHandle)
  {
    try
    {
      boolean bool = this.mPM.getApplicationHiddenSettingAsUser(paramString, paramUserHandle.getIdentifier());
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public Drawable getApplicationIcon(ApplicationInfo paramApplicationInfo)
  {
    return paramApplicationInfo.loadIcon(this);
  }
  
  public Drawable getApplicationIcon(String paramString)
    throws PackageManager.NameNotFoundException
  {
    return getApplicationIcon(getApplicationInfo(paramString, 1024));
  }
  
  public ApplicationInfo getApplicationInfo(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    return getApplicationInfoAsUser(paramString, paramInt, this.mContext.getUserId());
  }
  
  public ApplicationInfo getApplicationInfoAsUser(String paramString, int paramInt1, int paramInt2)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      ApplicationInfo localApplicationInfo = this.mPM.getApplicationInfo(paramString, paramInt1, paramInt2);
      if (localApplicationInfo != null)
      {
        paramString = maybeAdjustApplicationInfo(localApplicationInfo);
        return paramString;
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException(paramString);
  }
  
  public ApplicationInfo getApplicationInfoByUserId(String paramString, int paramInt1, int paramInt2)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      ApplicationInfo localApplicationInfo = this.mPM.getApplicationInfo(paramString, paramInt1, paramInt2);
      if (localApplicationInfo != null)
      {
        maybeAdjustApplicationInfo(localApplicationInfo);
        return localApplicationInfo;
      }
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("Package manager has died", paramString);
    }
    throw new PackageManager.NameNotFoundException(paramString);
  }
  
  public CharSequence getApplicationLabel(ApplicationInfo paramApplicationInfo)
  {
    return paramApplicationInfo.loadLabel(this);
  }
  
  public Drawable getApplicationLogo(ApplicationInfo paramApplicationInfo)
  {
    return paramApplicationInfo.loadLogo(this);
  }
  
  public Drawable getApplicationLogo(String paramString)
    throws PackageManager.NameNotFoundException
  {
    return getApplicationLogo(getApplicationInfo(paramString, 1024));
  }
  
  public int getComponentEnabledSetting(ComponentName paramComponentName)
  {
    try
    {
      int i = this.mPM.getComponentEnabledSetting(paramComponentName, this.mContext.getUserId());
      return i;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public Drawable getDefaultActivityIcon()
  {
    return Resources.getSystem().getDrawable(17301651);
  }
  
  public String getDefaultBrowserPackageNameAsUser(int paramInt)
  {
    try
    {
      String str = this.mPM.getDefaultBrowserPackageName(paramInt);
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  /* Error */
  public Drawable getDrawable(String paramString, int paramInt, ApplicationInfo paramApplicationInfo)
  {
    // Byte code:
    //   0: new 12	android/app/ApplicationPackageManager$ResourceName
    //   3: dup
    //   4: aload_1
    //   5: iload_2
    //   6: invokespecial 705	android/app/ApplicationPackageManager$ResourceName:<init>	(Ljava/lang/String;I)V
    //   9: astore 5
    //   11: aload_0
    //   12: aload 5
    //   14: invokespecial 707	android/app/ApplicationPackageManager:getCachedIcon	(Landroid/app/ApplicationPackageManager$ResourceName;)Landroid/graphics/drawable/Drawable;
    //   17: astore 4
    //   19: aload 4
    //   21: ifnull +6 -> 27
    //   24: aload 4
    //   26: areturn
    //   27: aload_3
    //   28: astore 4
    //   30: aload_3
    //   31: ifnonnull +13 -> 44
    //   34: aload_0
    //   35: aload_1
    //   36: sipush 1024
    //   39: invokevirtual 647	android/app/ApplicationPackageManager:getApplicationInfo	(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;
    //   42: astore 4
    //   44: iload_2
    //   45: ifeq +73 -> 118
    //   48: aload_0
    //   49: aload 4
    //   51: invokevirtual 711	android/app/ApplicationPackageManager:getResourcesForApplication	(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/Resources;
    //   54: iload_2
    //   55: aconst_null
    //   56: invokevirtual 714	android/content/res/Resources:getDrawable	(ILandroid/content/res/Resources$Theme;)Landroid/graphics/drawable/Drawable;
    //   59: astore_3
    //   60: aload_3
    //   61: ifnull +10 -> 71
    //   64: aload_0
    //   65: aload 5
    //   67: aload_3
    //   68: invokespecial 716	android/app/ApplicationPackageManager:putCachedIcon	(Landroid/app/ApplicationPackageManager$ResourceName;Landroid/graphics/drawable/Drawable;)V
    //   71: aload_3
    //   72: areturn
    //   73: astore_1
    //   74: aconst_null
    //   75: areturn
    //   76: astore_3
    //   77: ldc_w 718
    //   80: new 144	java/lang/StringBuilder
    //   83: dup
    //   84: invokespecial 145	java/lang/StringBuilder:<init>	()V
    //   87: ldc_w 720
    //   90: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   93: iload_2
    //   94: invokestatic 725	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   97: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   100: ldc_w 727
    //   103: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   106: aload_1
    //   107: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   113: aload_3
    //   114: invokestatic 730	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   117: pop
    //   118: aconst_null
    //   119: areturn
    //   120: astore_1
    //   121: ldc_w 718
    //   124: new 144	java/lang/StringBuilder
    //   127: dup
    //   128: invokespecial 145	java/lang/StringBuilder:<init>	()V
    //   131: ldc_w 732
    //   134: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   137: aload 4
    //   139: getfield 377	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   142: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   145: ldc_w 734
    //   148: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: aload_1
    //   152: invokevirtual 737	android/content/res/Resources$NotFoundException:getMessage	()Ljava/lang/String;
    //   155: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   158: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   161: invokestatic 474	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   164: pop
    //   165: aconst_null
    //   166: areturn
    //   167: astore_1
    //   168: ldc_w 718
    //   171: new 144	java/lang/StringBuilder
    //   174: dup
    //   175: invokespecial 145	java/lang/StringBuilder:<init>	()V
    //   178: ldc_w 732
    //   181: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   184: aload 4
    //   186: getfield 377	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   189: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   192: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   195: invokestatic 474	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   198: pop
    //   199: aconst_null
    //   200: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	201	0	this	ApplicationPackageManager
    //   0	201	1	paramString	String
    //   0	201	2	paramInt	int
    //   0	201	3	paramApplicationInfo	ApplicationInfo
    //   17	168	4	localObject	Object
    //   9	57	5	localResourceName	ResourceName
    // Exception table:
    //   from	to	target	type
    //   34	44	73	android/content/pm/PackageManager$NameNotFoundException
    //   48	60	76	java/lang/Exception
    //   64	71	76	java/lang/Exception
    //   48	60	120	android/content/res/Resources$NotFoundException
    //   64	71	120	android/content/res/Resources$NotFoundException
    //   48	60	167	android/content/pm/PackageManager$NameNotFoundException
    //   64	71	167	android/content/pm/PackageManager$NameNotFoundException
  }
  
  public Drawable getEphemeralApplicationIcon(String paramString)
  {
    try
    {
      paramString = this.mPM.getEphemeralApplicationIcon(paramString, this.mContext.getUserId());
      if (paramString != null)
      {
        paramString = new BitmapDrawable(null, paramString);
        return paramString;
      }
      return null;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public List<EphemeralApplicationInfo> getEphemeralApplications()
  {
    try
    {
      Object localObject = this.mPM.getEphemeralApplications(this.mContext.getUserId());
      if (localObject != null) {
        return ((ParceledListSlice)localObject).getList();
      }
      localObject = Collections.emptyList();
      return (List<EphemeralApplicationInfo>)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public byte[] getEphemeralCookie()
  {
    try
    {
      byte[] arrayOfByte = this.mPM.getEphemeralApplicationCookie(this.mContext.getPackageName(), this.mContext.getUserId());
      if (arrayOfByte != null) {
        return arrayOfByte;
      }
      arrayOfByte = EmptyArray.BYTE;
      return arrayOfByte;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getEphemeralCookieMaxSizeBytes()
  {
    return Settings.Global.getInt(this.mContext.getContentResolver(), "ephemeral_cookie_max_size_bytes", 16384);
  }
  
  public ComponentName getHomeActivities(List<ResolveInfo> paramList)
  {
    try
    {
      paramList = this.mPM.getHomeActivities(paramList);
      return paramList;
    }
    catch (RemoteException paramList)
    {
      throw paramList.rethrowFromSystemServer();
    }
  }
  
  public List<ApplicationInfo> getInstalledApplications(int paramInt)
  {
    int i = this.mContext.getUserId();
    try
    {
      Object localObject = this.mPM.getInstalledApplications(paramInt, i);
      if (localObject == null) {
        return Collections.emptyList();
      }
      localObject = ((ParceledListSlice)localObject).getList();
      return (List<ApplicationInfo>)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<PackageInfo> getInstalledPackages(int paramInt)
  {
    return getInstalledPackagesAsUser(paramInt, this.mContext.getUserId());
  }
  
  public List<PackageInfo> getInstalledPackagesAsUser(int paramInt1, int paramInt2)
  {
    try
    {
      Object localObject = this.mPM.getInstalledPackages(paramInt1, paramInt2);
      if (localObject == null) {
        return Collections.emptyList();
      }
      localObject = ((ParceledListSlice)localObject).getList();
      return (List<PackageInfo>)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getInstallerPackageName(String paramString)
  {
    try
    {
      paramString = this.mPM.getInstallerPackageName(paramString);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public InstrumentationInfo getInstrumentationInfo(ComponentName paramComponentName, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      InstrumentationInfo localInstrumentationInfo = this.mPM.getInstrumentationInfo(paramComponentName, paramInt);
      if (localInstrumentationInfo != null) {
        return localInstrumentationInfo;
      }
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException(paramComponentName.toString());
  }
  
  public List<IntentFilterVerificationInfo> getIntentFilterVerifications(String paramString)
  {
    try
    {
      paramString = this.mPM.getIntentFilterVerifications(paramString);
      if (paramString == null) {
        return Collections.emptyList();
      }
      paramString = paramString.getList();
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int getIntentVerificationStatusAsUser(String paramString, int paramInt)
  {
    try
    {
      paramInt = this.mPM.getIntentVerificationStatus(paramString, paramInt);
      return paramInt;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public KeySet getKeySetByAlias(String paramString1, String paramString2)
  {
    Preconditions.checkNotNull(paramString1);
    Preconditions.checkNotNull(paramString2);
    try
    {
      paramString1 = this.mPM.getKeySetByAlias(paramString1, paramString2);
      return paramString1;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public Intent getLaunchIntentForPackage(String paramString)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory("android.intent.category.INFO");
    localIntent.setPackage(paramString);
    List localList2 = queryIntentActivities(localIntent, 0);
    List localList1;
    if (localList2 != null)
    {
      localList1 = localList2;
      if (localList2.size() > 0) {}
    }
    else
    {
      localIntent.removeCategory("android.intent.category.INFO");
      localIntent.addCategory("android.intent.category.LAUNCHER");
      localIntent.setPackage(paramString);
      localList1 = queryIntentActivities(localIntent, 0);
    }
    if ((localList1 == null) || (localList1.size() <= 0)) {
      return null;
    }
    paramString = new Intent(localIntent);
    paramString.setFlags(268435456);
    paramString.setClassName(((ResolveInfo)localList1.get(0)).activityInfo.packageName, ((ResolveInfo)localList1.get(0)).activityInfo.name);
    return paramString;
  }
  
  public Intent getLeanbackLaunchIntentForPackage(String paramString)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory("android.intent.category.LEANBACK_LAUNCHER");
    localIntent.setPackage(paramString);
    paramString = queryIntentActivities(localIntent, 0);
    if ((paramString == null) || (paramString.size() <= 0)) {
      return null;
    }
    localIntent = new Intent(localIntent);
    localIntent.setFlags(268435456);
    localIntent.setClassName(((ResolveInfo)paramString.get(0)).activityInfo.packageName, ((ResolveInfo)paramString.get(0)).activityInfo.name);
    return localIntent;
  }
  
  public Drawable getManagedUserBadgedDrawable(Drawable paramDrawable, Rect paramRect, int paramInt)
  {
    return getBadgedDrawable(paramDrawable, getDrawableForDensity(17302310, paramInt), paramRect, true);
  }
  
  public int getMoveStatus(int paramInt)
  {
    try
    {
      paramInt = this.mPM.getMoveStatus(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getNameForUid(int paramInt)
  {
    try
    {
      String str = this.mPM.getNameForUid(paramInt);
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<VolumeInfo> getPackageCandidateVolumes(ApplicationInfo paramApplicationInfo)
  {
    Object localObject1 = (StorageManager)this.mContext.getSystemService(StorageManager.class);
    VolumeInfo localVolumeInfo1 = getPackageCurrentVolume(paramApplicationInfo);
    Object localObject2 = ((StorageManager)localObject1).getVolumes();
    localObject1 = new ArrayList();
    localObject2 = ((Iterable)localObject2).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      VolumeInfo localVolumeInfo2 = (VolumeInfo)((Iterator)localObject2).next();
      if ((Objects.equals(localVolumeInfo2, localVolumeInfo1)) || (isPackageCandidateVolume(this.mContext, paramApplicationInfo, localVolumeInfo2))) {
        ((List)localObject1).add(localVolumeInfo2);
      }
    }
    return (List<VolumeInfo>)localObject1;
  }
  
  public VolumeInfo getPackageCurrentVolume(ApplicationInfo paramApplicationInfo)
  {
    StorageManager localStorageManager = (StorageManager)this.mContext.getSystemService(StorageManager.class);
    if (paramApplicationInfo.isInternal()) {
      return localStorageManager.findVolumeById("private");
    }
    if (paramApplicationInfo.isExternalAsec()) {
      return localStorageManager.getPrimaryPhysicalVolume();
    }
    return localStorageManager.findVolumeByUuid(paramApplicationInfo.volumeUuid);
  }
  
  public int[] getPackageGids(String paramString)
    throws PackageManager.NameNotFoundException
  {
    return getPackageGids(paramString, 0);
  }
  
  public int[] getPackageGids(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      int[] arrayOfInt = this.mPM.getPackageGids(paramString, paramInt, this.mContext.getUserId());
      if (arrayOfInt != null) {
        return arrayOfInt;
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException(paramString);
  }
  
  public PackageInfo getPackageInfo(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    return getPackageInfoAsUser(paramString, paramInt, this.mContext.getUserId());
  }
  
  public PackageInfo getPackageInfoAsUser(String paramString, int paramInt1, int paramInt2)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      PackageInfo localPackageInfo = this.mPM.getPackageInfo(paramString, paramInt1, paramInt2);
      if (localPackageInfo != null) {
        return localPackageInfo;
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException(paramString);
  }
  
  public PackageInstaller getPackageInstaller()
  {
    synchronized (this.mLock)
    {
      PackageInstaller localPackageInstaller = this.mInstaller;
      if (localPackageInstaller == null) {}
      try
      {
        this.mInstaller = new PackageInstaller(this.mContext, this, this.mPM.getPackageInstaller(), this.mContext.getPackageName(), this.mContext.getUserId());
        localPackageInstaller = this.mInstaller;
        return localPackageInstaller;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
  }
  
  public void getPackageSizeInfoAsUser(String paramString, int paramInt, IPackageStatsObserver paramIPackageStatsObserver)
  {
    try
    {
      this.mPM.getPackageSizeInfo(paramString, paramInt, paramIPackageStatsObserver);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int getPackageUid(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    return getPackageUidAsUser(paramString, paramInt, this.mContext.getUserId());
  }
  
  public int getPackageUidAsUser(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    return getPackageUidAsUser(paramString, 0, paramInt);
  }
  
  public int getPackageUidAsUser(String paramString, int paramInt1, int paramInt2)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      paramInt1 = this.mPM.getPackageUid(paramString, paramInt1, paramInt2);
      if (paramInt1 >= 0) {
        return paramInt1;
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException(paramString);
  }
  
  public String[] getPackagesForUid(int paramInt)
  {
    try
    {
      String[] arrayOfString = this.mPM.getPackagesForUid(paramInt);
      return arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<PackageInfo> getPackagesHoldingPermissions(String[] paramArrayOfString, int paramInt)
  {
    int i = this.mContext.getUserId();
    try
    {
      paramArrayOfString = this.mPM.getPackagesHoldingPermissions(paramArrayOfString, paramInt, i);
      if (paramArrayOfString == null) {
        return Collections.emptyList();
      }
      paramArrayOfString = paramArrayOfString.getList();
      return paramArrayOfString;
    }
    catch (RemoteException paramArrayOfString)
    {
      throw paramArrayOfString.rethrowFromSystemServer();
    }
  }
  
  public String getPermissionControllerPackageName()
  {
    synchronized (this.mLock)
    {
      String str = this.mPermissionsControllerPackageName;
      if (str == null) {}
      try
      {
        this.mPermissionsControllerPackageName = this.mPM.getPermissionControllerPackageName();
        str = this.mPermissionsControllerPackageName;
        return str;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
  }
  
  public int getPermissionFlags(String paramString1, String paramString2, UserHandle paramUserHandle)
  {
    try
    {
      int i = this.mPM.getPermissionFlags(paramString1, paramString2, paramUserHandle.getIdentifier());
      return i;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public PermissionGroupInfo getPermissionGroupInfo(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      PermissionGroupInfo localPermissionGroupInfo = this.mPM.getPermissionGroupInfo(paramString, paramInt);
      if (localPermissionGroupInfo != null) {
        return localPermissionGroupInfo;
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException(paramString);
  }
  
  public PermissionInfo getPermissionInfo(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      PermissionInfo localPermissionInfo = this.mPM.getPermissionInfo(paramString, paramInt);
      if (localPermissionInfo != null) {
        return localPermissionInfo;
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException(paramString);
  }
  
  public int getPreferredActivities(List<IntentFilter> paramList, List<ComponentName> paramList1, String paramString)
  {
    try
    {
      int i = this.mPM.getPreferredActivities(paramList, paramList1, paramString);
      return i;
    }
    catch (RemoteException paramList)
    {
      throw paramList.rethrowFromSystemServer();
    }
  }
  
  public List<PackageInfo> getPreferredPackages(int paramInt)
  {
    Log.w("ApplicationPackageManager", "getPreferredPackages() is a no-op");
    return Collections.emptyList();
  }
  
  public List<VolumeInfo> getPrimaryStorageCandidateVolumes()
  {
    Object localObject1 = (StorageManager)this.mContext.getSystemService(StorageManager.class);
    VolumeInfo localVolumeInfo = getPrimaryStorageCurrentVolume();
    Object localObject2 = ((StorageManager)localObject1).getVolumes();
    ArrayList localArrayList = new ArrayList();
    if ((Objects.equals("primary_physical", ((StorageManager)localObject1).getPrimaryStorageUuid())) && (localVolumeInfo != null)) {
      localArrayList.add(localVolumeInfo);
    }
    for (;;)
    {
      return localArrayList;
      localObject1 = ((Iterable)localObject2).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (VolumeInfo)((Iterator)localObject1).next();
        if ((Objects.equals(localObject2, localVolumeInfo)) || (isPrimaryStorageCandidateVolume((VolumeInfo)localObject2))) {
          localArrayList.add(localObject2);
        }
      }
    }
  }
  
  public VolumeInfo getPrimaryStorageCurrentVolume()
  {
    StorageManager localStorageManager = (StorageManager)this.mContext.getSystemService(StorageManager.class);
    return localStorageManager.findVolumeByQualifiedUuid(localStorageManager.getPrimaryStorageUuid());
  }
  
  public ProviderInfo getProviderInfo(ComponentName paramComponentName, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      ProviderInfo localProviderInfo = this.mPM.getProviderInfo(paramComponentName, paramInt, this.mContext.getUserId());
      if (localProviderInfo != null) {
        return localProviderInfo;
      }
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException(paramComponentName.toString());
  }
  
  public ActivityInfo getReceiverInfo(ComponentName paramComponentName, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      ActivityInfo localActivityInfo = this.mPM.getReceiverInfo(paramComponentName, paramInt, this.mContext.getUserId());
      if (localActivityInfo != null) {
        return localActivityInfo;
      }
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException(paramComponentName.toString());
  }
  
  public Resources getResourcesForActivity(ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException
  {
    return getResourcesForApplication(getActivityInfo(paramComponentName, 1024).applicationInfo);
  }
  
  public Resources getResourcesForApplication(ApplicationInfo paramApplicationInfo)
    throws PackageManager.NameNotFoundException
  {
    if (paramApplicationInfo.packageName.equals("system")) {
      return this.mContext.mMainThread.getSystemContext().getResources();
    }
    int i;
    ActivityThread localActivityThread;
    String str;
    Object localObject;
    if (paramApplicationInfo.uid == Process.myUid())
    {
      i = 1;
      localActivityThread = this.mContext.mMainThread;
      str = paramApplicationInfo.processName;
      if (i == 0) {
        break label111;
      }
      localObject = paramApplicationInfo.sourceDir;
      label63:
      if (i == 0) {
        break label119;
      }
    }
    label111:
    label119:
    for (String[] arrayOfString = paramApplicationInfo.splitSourceDirs;; arrayOfString = paramApplicationInfo.splitPublicSourceDirs)
    {
      localObject = localActivityThread.getTopLevelResources(str, (String)localObject, arrayOfString, paramApplicationInfo.resourceDirs, paramApplicationInfo.sharedLibraryFiles, 0, this.mContext.mPackageInfo);
      if (localObject == null) {
        break label128;
      }
      return (Resources)localObject;
      i = 0;
      break;
      localObject = paramApplicationInfo.publicSourceDir;
      break label63;
    }
    label128:
    throw new PackageManager.NameNotFoundException("Unable to open " + paramApplicationInfo.publicSourceDir);
  }
  
  public Resources getResourcesForApplication(String paramString)
    throws PackageManager.NameNotFoundException
  {
    return getResourcesForApplication(getApplicationInfo(paramString, 1024));
  }
  
  public Resources getResourcesForApplicationAsUser(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Call does not support special user #" + paramInt);
    }
    if ("system".equals(paramString)) {
      return this.mContext.mMainThread.getSystemContext().getResources();
    }
    try
    {
      ApplicationInfo localApplicationInfo = this.mPM.getApplicationInfo(paramString, 1024, paramInt);
      if (localApplicationInfo != null)
      {
        paramString = getResourcesForApplication(localApplicationInfo);
        return paramString;
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException("Package " + paramString + " doesn't exist");
  }
  
  public ServiceInfo getServiceInfo(ComponentName paramComponentName, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      ServiceInfo localServiceInfo = this.mPM.getServiceInfo(paramComponentName, paramInt, this.mContext.getUserId());
      if (localServiceInfo != null) {
        return localServiceInfo;
      }
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException(paramComponentName.toString());
  }
  
  public String getServicesSystemSharedLibraryPackageName()
  {
    try
    {
      String str = this.mPM.getServicesSystemSharedLibraryPackageName();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getSharedSystemSharedLibraryPackageName()
  {
    try
    {
      String str = this.mPM.getSharedSystemSharedLibraryPackageName();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public KeySet getSigningKeySet(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    try
    {
      paramString = this.mPM.getSigningKeySet(paramString);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public FeatureInfo[] getSystemAvailableFeatures()
  {
    try
    {
      Object localObject = this.mPM.getSystemAvailableFeatures();
      if (localObject == null) {
        return new FeatureInfo[0];
      }
      localObject = ((ParceledListSlice)localObject).getList();
      FeatureInfo[] arrayOfFeatureInfo = new FeatureInfo[((List)localObject).size()];
      int i = 0;
      while (i < arrayOfFeatureInfo.length)
      {
        arrayOfFeatureInfo[i] = ((FeatureInfo)((List)localObject).get(i));
        i += 1;
      }
      return arrayOfFeatureInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String[] getSystemSharedLibraryNames()
  {
    try
    {
      String[] arrayOfString = this.mPM.getSystemSharedLibraryNames();
      return arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public CharSequence getText(String paramString, int paramInt, ApplicationInfo paramApplicationInfo)
  {
    ResourceName localResourceName = new ResourceName(paramString, paramInt);
    localObject = getCachedString(localResourceName);
    if (localObject != null) {
      return (CharSequence)localObject;
    }
    localObject = paramApplicationInfo;
    if (paramApplicationInfo == null) {}
    try
    {
      localObject = getApplicationInfo(paramString, 1024);
      return null;
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      try
      {
        paramApplicationInfo = getResourcesForApplication((ApplicationInfo)localObject).getText(paramInt);
        putCachedString(localResourceName, paramApplicationInfo);
        return paramApplicationInfo;
      }
      catch (RuntimeException paramApplicationInfo)
      {
        Log.w("PackageManager", "Failure retrieving text 0x" + Integer.toHexString(paramInt) + " in package " + paramString, paramApplicationInfo);
        return null;
      }
      catch (PackageManager.NameNotFoundException paramString)
      {
        Log.w("PackageManager", "Failure retrieving resources for " + ((ApplicationInfo)localObject).packageName);
      }
      paramString = paramString;
      return null;
    }
  }
  
  public int getUidForSharedUser(String paramString)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      int i = this.mPM.getUidForSharedUser(paramString);
      if (i != -1) {
        return i;
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException("No shared userid for user:" + paramString);
  }
  
  public Drawable getUserBadgeForDensity(UserHandle paramUserHandle, int paramInt)
  {
    return getManagedProfileIconForDensity(paramUserHandle, 17302310, paramInt);
  }
  
  public Drawable getUserBadgeForDensityNoBackground(UserHandle paramUserHandle, int paramInt)
  {
    return getManagedProfileIconForDensity(paramUserHandle, 17302311, paramInt);
  }
  
  public Drawable getUserBadgedDrawableForDensity(Drawable paramDrawable, UserHandle paramUserHandle, Rect paramRect, int paramInt)
  {
    paramUserHandle = getUserBadgeForDensity(paramUserHandle, paramInt);
    if (paramUserHandle == null) {
      return paramDrawable;
    }
    return getBadgedDrawable(paramDrawable, paramUserHandle, paramRect, true);
  }
  
  public Drawable getUserBadgedIcon(Drawable paramDrawable, UserHandle paramUserHandle)
  {
    int i = getBadgeResIdForUser(paramUserHandle.getIdentifier());
    if (i == 0) {
      return paramDrawable;
    }
    return getBadgedDrawable(paramDrawable, getDrawable("system", i, null), null, true);
  }
  
  public CharSequence getUserBadgedLabel(CharSequence paramCharSequence, UserHandle paramUserHandle)
  {
    if (isManagedProfile(paramUserHandle.getIdentifier())) {
      return Resources.getSystem().getString(17040828, new Object[] { paramCharSequence });
    }
    return paramCharSequence;
  }
  
  UserManager getUserManager()
  {
    synchronized (this.mLock)
    {
      if (this.mUserManager == null) {
        this.mUserManager = UserManager.get(this.mContext);
      }
      UserManager localUserManager = this.mUserManager;
      return localUserManager;
    }
  }
  
  public VerifierDeviceIdentity getVerifierDeviceIdentity()
  {
    try
    {
      VerifierDeviceIdentity localVerifierDeviceIdentity = this.mPM.getVerifierDeviceIdentity();
      return localVerifierDeviceIdentity;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  /* Error */
  public android.content.res.XmlResourceParser getXml(String paramString, int paramInt, ApplicationInfo paramApplicationInfo)
  {
    // Byte code:
    //   0: aload_3
    //   1: astore 4
    //   3: aload_3
    //   4: ifnonnull +13 -> 17
    //   7: aload_0
    //   8: aload_1
    //   9: sipush 1024
    //   12: invokevirtual 647	android/app/ApplicationPackageManager:getApplicationInfo	(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;
    //   15: astore 4
    //   17: aload_0
    //   18: aload 4
    //   20: invokevirtual 711	android/app/ApplicationPackageManager:getResourcesForApplication	(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/Resources;
    //   23: iload_2
    //   24: invokevirtual 1165	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   27: astore_3
    //   28: aload_3
    //   29: areturn
    //   30: astore_1
    //   31: aconst_null
    //   32: areturn
    //   33: astore_1
    //   34: ldc_w 718
    //   37: new 144	java/lang/StringBuilder
    //   40: dup
    //   41: invokespecial 145	java/lang/StringBuilder:<init>	()V
    //   44: ldc_w 732
    //   47: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   50: aload 4
    //   52: getfield 377	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   55: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   58: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   61: invokestatic 474	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   64: pop
    //   65: aconst_null
    //   66: areturn
    //   67: astore_3
    //   68: ldc_w 718
    //   71: new 144	java/lang/StringBuilder
    //   74: dup
    //   75: invokespecial 145	java/lang/StringBuilder:<init>	()V
    //   78: ldc_w 1167
    //   81: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   84: iload_2
    //   85: invokestatic 725	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   88: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: ldc_w 727
    //   94: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   97: aload_1
    //   98: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   101: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   104: aload_3
    //   105: invokestatic 730	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   108: pop
    //   109: aconst_null
    //   110: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	111	0	this	ApplicationPackageManager
    //   0	111	1	paramString	String
    //   0	111	2	paramInt	int
    //   0	111	3	paramApplicationInfo	ApplicationInfo
    //   1	50	4	localApplicationInfo	ApplicationInfo
    // Exception table:
    //   from	to	target	type
    //   7	17	30	android/content/pm/PackageManager$NameNotFoundException
    //   17	28	33	android/content/pm/PackageManager$NameNotFoundException
    //   17	28	67	java/lang/RuntimeException
  }
  
  public void grantRuntimePermission(String paramString1, String paramString2, UserHandle paramUserHandle)
  {
    try
    {
      this.mPM.grantRuntimePermission(paramString1, paramString2, paramUserHandle.getIdentifier());
      return;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public void grantSystemAppPermissions()
  {
    try
    {
      this.mPM.grantSystemAppPermissions(this.mContext.getUserId());
      return;
    }
    catch (Exception localException)
    {
      Log.e("ApplicationPackageManager", "got exception when granting system app's permissions");
      localException.printStackTrace();
    }
  }
  
  public boolean hasSystemFeature(String paramString)
  {
    return hasSystemFeature(paramString, 0);
  }
  
  public boolean hasSystemFeature(String paramString, int paramInt)
  {
    try
    {
      boolean bool = this.mPM.hasSystemFeature(paramString, paramInt);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int installExistingPackage(String paramString)
    throws PackageManager.NameNotFoundException
  {
    return installExistingPackageAsUser(paramString, this.mContext.getUserId());
  }
  
  public int installExistingPackageAsUser(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      paramInt = this.mPM.installExistingPackageAsUser(paramString, paramInt);
      if (paramInt == -3) {
        throw new PackageManager.NameNotFoundException("Package " + paramString + " doesn't exist");
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    return paramInt;
  }
  
  public void installPackage(Uri paramUri, PackageInstallObserver paramPackageInstallObserver, int paramInt, String paramString)
  {
    installCommon(paramUri, paramPackageInstallObserver, paramInt, paramString, this.mContext.getUserId());
  }
  
  public void installPackage(Uri paramUri, IPackageInstallObserver paramIPackageInstallObserver, int paramInt, String paramString)
  {
    installCommon(paramUri, new PackageManager.LegacyPackageInstallObserver(paramIPackageInstallObserver), paramInt, paramString, this.mContext.getUserId());
  }
  
  public boolean isEphemeralApplication()
  {
    try
    {
      boolean bool = this.mPM.isEphemeralApplication(this.mContext.getPackageName(), this.mContext.getUserId());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isPackageAvailable(String paramString)
  {
    try
    {
      boolean bool = this.mPM.isPackageAvailable(paramString, this.mContext.getUserId());
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean isPackageSuspendedForUser(String paramString, int paramInt)
  {
    try
    {
      boolean bool = this.mPM.isPackageSuspendedForUser(paramString, paramInt);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean isPermissionRevokedByPolicy(String paramString1, String paramString2)
  {
    try
    {
      boolean bool = this.mPM.isPermissionRevokedByPolicy(paramString1, paramString2, this.mContext.getUserId());
      return bool;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public boolean isSafeMode()
  {
    try
    {
      if (this.mCachedSafeMode < 0) {
        if (!this.mPM.isSafeMode()) {
          break label37;
        }
      }
      label37:
      for (int i = 1;; i = 0)
      {
        this.mCachedSafeMode = i;
        i = this.mCachedSafeMode;
        if (i == 0) {
          break;
        }
        return true;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isSignedBy(String paramString, KeySet paramKeySet)
  {
    Preconditions.checkNotNull(paramString);
    Preconditions.checkNotNull(paramKeySet);
    try
    {
      boolean bool = this.mPM.isPackageSignedByKeySet(paramString, paramKeySet);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean isSignedByExactly(String paramString, KeySet paramKeySet)
  {
    Preconditions.checkNotNull(paramString);
    Preconditions.checkNotNull(paramKeySet);
    try
    {
      boolean bool = this.mPM.isPackageSignedByKeySetExactly(paramString, paramKeySet);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean isUpgrade()
  {
    try
    {
      boolean bool = this.mPM.isUpgrade();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Drawable loadItemIcon(PackageItemInfo paramPackageItemInfo, ApplicationInfo paramApplicationInfo)
  {
    paramApplicationInfo = loadUnbadgedItemIcon(paramPackageItemInfo, paramApplicationInfo);
    if (paramPackageItemInfo.showUserIcon != 55536) {
      return paramApplicationInfo;
    }
    return getUserBadgedIcon(paramApplicationInfo, new UserHandle(this.mContext.getUserId()));
  }
  
  public Drawable loadUnbadgedItemIcon(PackageItemInfo paramPackageItemInfo, ApplicationInfo paramApplicationInfo)
  {
    if (paramPackageItemInfo.showUserIcon != 55536)
    {
      paramApplicationInfo = getUserManager().getUserIcon(paramPackageItemInfo.showUserIcon);
      if (paramApplicationInfo == null) {
        return UserIcons.getDefaultUserIcon(paramPackageItemInfo.showUserIcon, false);
      }
      return new BitmapDrawable(paramApplicationInfo);
    }
    Drawable localDrawable = null;
    if (paramPackageItemInfo.packageName != null) {
      localDrawable = getDrawable(paramPackageItemInfo.packageName, paramPackageItemInfo.icon, paramApplicationInfo);
    }
    paramApplicationInfo = localDrawable;
    if (localDrawable == null) {
      paramApplicationInfo = paramPackageItemInfo.loadDefaultIcon(this);
    }
    return paramApplicationInfo;
  }
  
  /* Error */
  public int movePackage(String paramString, VolumeInfo paramVolumeInfo)
  {
    // Byte code:
    //   0: ldc_w 354
    //   3: aload_2
    //   4: getfield 1268	android/os/storage/VolumeInfo:id	Ljava/lang/String;
    //   7: invokevirtual 275	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   10: ifeq +19 -> 29
    //   13: getstatic 1271	android/os/storage/StorageManager:UUID_PRIVATE_INTERNAL	Ljava/lang/String;
    //   16: astore_2
    //   17: aload_0
    //   18: getfield 85	android/app/ApplicationPackageManager:mPM	Landroid/content/pm/IPackageManager;
    //   21: aload_1
    //   22: aload_2
    //   23: invokeinterface 1273 3 0
    //   28: ireturn
    //   29: aload_2
    //   30: invokevirtual 373	android/os/storage/VolumeInfo:isPrimaryPhysical	()Z
    //   33: ifeq +10 -> 43
    //   36: ldc_w 1001
    //   39: astore_2
    //   40: goto -23 -> 17
    //   43: aload_2
    //   44: getfield 1276	android/os/storage/VolumeInfo:fsUuid	Ljava/lang/String;
    //   47: invokestatic 804	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   50: checkcast 271	java/lang/String
    //   53: astore_2
    //   54: goto -37 -> 17
    //   57: astore_1
    //   58: aload_1
    //   59: invokevirtual 331	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   62: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	63	0	this	ApplicationPackageManager
    //   0	63	1	paramString	String
    //   0	63	2	paramVolumeInfo	VolumeInfo
    // Exception table:
    //   from	to	target	type
    //   0	17	57	android/os/RemoteException
    //   17	29	57	android/os/RemoteException
    //   29	36	57	android/os/RemoteException
    //   43	54	57	android/os/RemoteException
  }
  
  /* Error */
  public int movePrimaryStorage(VolumeInfo paramVolumeInfo)
  {
    // Byte code:
    //   0: ldc_w 354
    //   3: aload_1
    //   4: getfield 1268	android/os/storage/VolumeInfo:id	Ljava/lang/String;
    //   7: invokevirtual 275	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   10: ifeq +18 -> 28
    //   13: getstatic 1271	android/os/storage/StorageManager:UUID_PRIVATE_INTERNAL	Ljava/lang/String;
    //   16: astore_1
    //   17: aload_0
    //   18: getfield 85	android/app/ApplicationPackageManager:mPM	Landroid/content/pm/IPackageManager;
    //   21: aload_1
    //   22: invokeinterface 1280 2 0
    //   27: ireturn
    //   28: aload_1
    //   29: invokevirtual 373	android/os/storage/VolumeInfo:isPrimaryPhysical	()Z
    //   32: ifeq +10 -> 42
    //   35: ldc_w 1001
    //   38: astore_1
    //   39: goto -22 -> 17
    //   42: aload_1
    //   43: getfield 1276	android/os/storage/VolumeInfo:fsUuid	Ljava/lang/String;
    //   46: invokestatic 804	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   49: checkcast 271	java/lang/String
    //   52: astore_1
    //   53: goto -36 -> 17
    //   56: astore_1
    //   57: aload_1
    //   58: invokevirtual 331	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   61: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	62	0	this	ApplicationPackageManager
    //   0	62	1	paramVolumeInfo	VolumeInfo
    // Exception table:
    //   from	to	target	type
    //   0	17	56	android/os/RemoteException
    //   17	28	56	android/os/RemoteException
    //   28	35	56	android/os/RemoteException
    //   42	53	56	android/os/RemoteException
  }
  
  public List<ResolveInfo> queryBroadcastReceivers(Intent paramIntent, int paramInt)
  {
    return queryBroadcastReceiversAsUser(paramIntent, paramInt, this.mContext.getUserId());
  }
  
  public List<ResolveInfo> queryBroadcastReceiversAsUser(Intent paramIntent, int paramInt1, int paramInt2)
  {
    try
    {
      paramIntent = this.mPM.queryIntentReceivers(paramIntent, paramIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), paramInt1, paramInt2);
      if (paramIntent == null) {
        return Collections.emptyList();
      }
      paramIntent = paramIntent.getList();
      return paramIntent;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  public List<ProviderInfo> queryContentProviders(String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      paramString = this.mPM.queryContentProviders(paramString, paramInt1, paramInt2);
      if (paramString != null) {
        return paramString.getList();
      }
      paramString = Collections.emptyList();
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public List<InstrumentationInfo> queryInstrumentation(String paramString, int paramInt)
  {
    try
    {
      paramString = this.mPM.queryInstrumentation(paramString, paramInt);
      if (paramString == null) {
        return Collections.emptyList();
      }
      paramString = paramString.getList();
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public List<ResolveInfo> queryIntentActivities(Intent paramIntent, int paramInt)
  {
    return queryIntentActivitiesAsUser(paramIntent, paramInt, this.mContext.getUserId());
  }
  
  public List<ResolveInfo> queryIntentActivitiesAsUser(Intent paramIntent, int paramInt1, int paramInt2)
  {
    try
    {
      paramIntent = this.mPM.queryIntentActivities(paramIntent, paramIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), paramInt1, paramInt2);
      if (paramIntent == null) {
        return Collections.emptyList();
      }
      paramIntent = paramIntent.getList();
      return paramIntent;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  public List<ResolveInfo> queryIntentActivityOptions(ComponentName paramComponentName, Intent[] paramArrayOfIntent, Intent paramIntent, int paramInt)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    Object localObject2 = null;
    Object localObject1 = null;
    if (paramArrayOfIntent != null)
    {
      int j = paramArrayOfIntent.length;
      int i = 0;
      for (;;)
      {
        localObject2 = localObject1;
        if (i >= j) {
          break;
        }
        Object localObject3 = paramArrayOfIntent[i];
        localObject2 = localObject1;
        if (localObject3 != null)
        {
          localObject3 = ((Intent)localObject3).resolveTypeIfNeeded(localContentResolver);
          localObject2 = localObject1;
          if (localObject3 != null)
          {
            localObject2 = localObject1;
            if (localObject1 == null) {
              localObject2 = new String[j];
            }
            localObject2[i] = localObject3;
          }
        }
        i += 1;
        localObject1 = localObject2;
      }
    }
    try
    {
      paramComponentName = this.mPM.queryIntentActivityOptions(paramComponentName, paramArrayOfIntent, (String[])localObject2, paramIntent, paramIntent.resolveTypeIfNeeded(localContentResolver), paramInt, this.mContext.getUserId());
      if (paramComponentName == null) {
        return Collections.emptyList();
      }
      paramComponentName = paramComponentName.getList();
      return paramComponentName;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public List<ResolveInfo> queryIntentContentProviders(Intent paramIntent, int paramInt)
  {
    return queryIntentContentProvidersAsUser(paramIntent, paramInt, this.mContext.getUserId());
  }
  
  public List<ResolveInfo> queryIntentContentProvidersAsUser(Intent paramIntent, int paramInt1, int paramInt2)
  {
    try
    {
      paramIntent = this.mPM.queryIntentContentProviders(paramIntent, paramIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), paramInt1, paramInt2);
      if (paramIntent == null) {
        return Collections.emptyList();
      }
      paramIntent = paramIntent.getList();
      return paramIntent;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  public List<ResolveInfo> queryIntentServices(Intent paramIntent, int paramInt)
  {
    return queryIntentServicesAsUser(paramIntent, paramInt, this.mContext.getUserId());
  }
  
  public List<ResolveInfo> queryIntentServicesAsUser(Intent paramIntent, int paramInt1, int paramInt2)
  {
    try
    {
      paramIntent = this.mPM.queryIntentServices(paramIntent, paramIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), paramInt1, paramInt2);
      if (paramIntent == null) {
        return Collections.emptyList();
      }
      paramIntent = paramIntent.getList();
      return paramIntent;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  public List<PermissionInfo> queryPermissionsByGroup(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      Object localObject = this.mPM.queryPermissionsByGroup(paramString, paramInt);
      if (localObject != null)
      {
        localObject = ((ParceledListSlice)localObject).getList();
        if (localObject != null) {
          return (List<PermissionInfo>)localObject;
        }
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    throw new PackageManager.NameNotFoundException(paramString);
  }
  
  public void registerMoveCallback(PackageManager.MoveCallback paramMoveCallback, Handler paramHandler)
  {
    synchronized (this.mDelegates)
    {
      paramMoveCallback = new MoveCallbackDelegate(paramMoveCallback, paramHandler.getLooper());
      try
      {
        this.mPM.registerMoveCallback(paramMoveCallback);
        this.mDelegates.add(paramMoveCallback);
        return;
      }
      catch (RemoteException paramMoveCallback)
      {
        throw paramMoveCallback.rethrowFromSystemServer();
      }
    }
  }
  
  public void removeOnPermissionsChangeListener(PackageManager.OnPermissionsChangedListener paramOnPermissionsChangedListener)
  {
    synchronized (this.mPermissionListeners)
    {
      IOnPermissionsChangeListener localIOnPermissionsChangeListener = (IOnPermissionsChangeListener)this.mPermissionListeners.get(paramOnPermissionsChangedListener);
      if (localIOnPermissionsChangeListener != null) {}
      try
      {
        this.mPM.removeOnPermissionsChangeListener(localIOnPermissionsChangeListener);
        this.mPermissionListeners.remove(paramOnPermissionsChangedListener);
        return;
      }
      catch (RemoteException paramOnPermissionsChangedListener)
      {
        throw paramOnPermissionsChangedListener.rethrowFromSystemServer();
      }
    }
  }
  
  public void removePackageFromPreferred(String paramString)
  {
    Log.w("ApplicationPackageManager", "removePackageFromPreferred() is a no-op");
  }
  
  public void removePermission(String paramString)
  {
    try
    {
      this.mPM.removePermission(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void replacePreferredActivity(IntentFilter paramIntentFilter, int paramInt, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName)
  {
    try
    {
      this.mPM.replacePreferredActivity(paramIntentFilter, paramInt, paramArrayOfComponentName, paramComponentName, this.mContext.getUserId());
      return;
    }
    catch (RemoteException paramIntentFilter)
    {
      throw paramIntentFilter.rethrowFromSystemServer();
    }
  }
  
  public void replacePreferredActivityAsUser(IntentFilter paramIntentFilter, int paramInt1, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName, int paramInt2)
  {
    try
    {
      this.mPM.replacePreferredActivity(paramIntentFilter, paramInt1, paramArrayOfComponentName, paramComponentName, paramInt2);
      return;
    }
    catch (RemoteException paramIntentFilter)
    {
      throw paramIntentFilter.rethrowFromSystemServer();
    }
  }
  
  public void resetApplicationPermissions()
  {
    try
    {
      this.mPM.resetApplicationPermissions(this.mContext.getUserId());
      return;
    }
    catch (Exception localException)
    {
      Log.e("ApplicationPackageManager", "got exception when reset permissions");
      localException.printStackTrace();
    }
  }
  
  public ResolveInfo resolveActivity(Intent paramIntent, int paramInt)
  {
    return resolveActivityAsUser(paramIntent, paramInt, this.mContext.getUserId());
  }
  
  public ResolveInfo resolveActivityAsUser(Intent paramIntent, int paramInt1, int paramInt2)
  {
    try
    {
      paramIntent = this.mPM.resolveIntent(paramIntent, paramIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), paramInt1, paramInt2);
      return paramIntent;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  public ProviderInfo resolveContentProvider(String paramString, int paramInt)
  {
    return resolveContentProviderAsUser(paramString, paramInt, this.mContext.getUserId());
  }
  
  public ProviderInfo resolveContentProviderAsUser(String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      paramString = this.mPM.resolveContentProvider(paramString, paramInt1, paramInt2);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public ResolveInfo resolveService(Intent paramIntent, int paramInt)
  {
    try
    {
      paramIntent = this.mPM.resolveService(paramIntent, paramIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), paramInt, this.mContext.getUserId());
      return paramIntent;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  public void revokeRuntimePermission(String paramString1, String paramString2, UserHandle paramUserHandle)
  {
    try
    {
      this.mPM.revokeRuntimePermission(paramString1, paramString2, paramUserHandle.getIdentifier());
      return;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public void setApplicationEnabledSetting(String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      this.mPM.setApplicationEnabledSetting(paramString, paramInt1, paramInt2, this.mContext.getUserId(), this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean setApplicationHiddenSettingAsUser(String paramString, boolean paramBoolean, UserHandle paramUserHandle)
  {
    try
    {
      paramBoolean = this.mPM.setApplicationHiddenSettingAsUser(paramString, paramBoolean, paramUserHandle.getIdentifier());
      return paramBoolean;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setComponentEnabledSetting(ComponentName paramComponentName, int paramInt1, int paramInt2)
  {
    try
    {
      this.mPM.setComponentEnabledSetting(paramComponentName, paramInt1, paramInt2, this.mContext.getUserId());
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean setDefaultBrowserPackageNameAsUser(String paramString, int paramInt)
  {
    try
    {
      boolean bool = this.mPM.setDefaultBrowserPackageName(paramString, paramInt);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean setEphemeralCookie(byte[] paramArrayOfByte)
  {
    try
    {
      boolean bool = this.mPM.setEphemeralApplicationCookie(this.mContext.getPackageName(), paramArrayOfByte, this.mContext.getUserId());
      return bool;
    }
    catch (RemoteException paramArrayOfByte)
    {
      throw paramArrayOfByte.rethrowFromSystemServer();
    }
  }
  
  public void setInstallerPackageName(String paramString1, String paramString2)
  {
    try
    {
      this.mPM.setInstallerPackageName(paramString1, paramString2);
      return;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public String[] setPackagesSuspendedAsUser(String[] paramArrayOfString, boolean paramBoolean, int paramInt)
  {
    try
    {
      paramArrayOfString = this.mPM.setPackagesSuspendedAsUser(paramArrayOfString, paramBoolean, paramInt);
      return paramArrayOfString;
    }
    catch (RemoteException paramArrayOfString)
    {
      throw paramArrayOfString.rethrowFromSystemServer();
    }
  }
  
  public boolean shouldShowRequestPermissionRationale(String paramString)
  {
    try
    {
      boolean bool = this.mPM.shouldShowRequestPermissionRationale(paramString, this.mContext.getPackageName(), this.mContext.getUserId());
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void unregisterMoveCallback(PackageManager.MoveCallback paramMoveCallback)
  {
    Iterator localIterator;
    MoveCallbackDelegate localMoveCallbackDelegate;
    synchronized (this.mDelegates)
    {
      localIterator = this.mDelegates.iterator();
      PackageManager.MoveCallback localMoveCallback;
      do
      {
        if (!localIterator.hasNext()) {
          break;
        }
        localMoveCallbackDelegate = (MoveCallbackDelegate)localIterator.next();
        localMoveCallback = localMoveCallbackDelegate.mCallback;
      } while (localMoveCallback != paramMoveCallback);
    }
  }
  
  public boolean updateIntentVerificationStatusAsUser(String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      boolean bool = this.mPM.updateIntentVerificationStatus(paramString, paramInt1, paramInt2);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void updatePermissionFlags(String paramString1, String paramString2, int paramInt1, int paramInt2, UserHandle paramUserHandle)
  {
    try
    {
      this.mPM.updatePermissionFlags(paramString1, paramString2, paramInt1, paramInt2, paramUserHandle.getIdentifier());
      return;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public void verifyIntentFilter(int paramInt1, int paramInt2, List<String> paramList)
  {
    try
    {
      this.mPM.verifyIntentFilter(paramInt1, paramInt2, paramList);
      return;
    }
    catch (RemoteException paramList)
    {
      throw paramList.rethrowFromSystemServer();
    }
  }
  
  public void verifyPendingInstall(int paramInt1, int paramInt2)
  {
    try
    {
      this.mPM.verifyPendingInstall(paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  private static class MoveCallbackDelegate
    extends IPackageMoveObserver.Stub
    implements Handler.Callback
  {
    private static final int MSG_CREATED = 1;
    private static final int MSG_STATUS_CHANGED = 2;
    final PackageManager.MoveCallback mCallback;
    final Handler mHandler;
    
    public MoveCallbackDelegate(PackageManager.MoveCallback paramMoveCallback, Looper paramLooper)
    {
      this.mCallback = paramMoveCallback;
      this.mHandler = new Handler(paramLooper, this);
    }
    
    public boolean handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return false;
      case 1: 
        paramMessage = (SomeArgs)paramMessage.obj;
        this.mCallback.onCreated(paramMessage.argi1, (Bundle)paramMessage.arg2);
        paramMessage.recycle();
        return true;
      }
      paramMessage = (SomeArgs)paramMessage.obj;
      this.mCallback.onStatusChanged(paramMessage.argi1, paramMessage.argi2, ((Long)paramMessage.arg3).longValue());
      paramMessage.recycle();
      return true;
    }
    
    public void onCreated(int paramInt, Bundle paramBundle)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.argi1 = paramInt;
      localSomeArgs.arg2 = paramBundle;
      this.mHandler.obtainMessage(1, localSomeArgs).sendToTarget();
    }
    
    public void onStatusChanged(int paramInt1, int paramInt2, long paramLong)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.argi1 = paramInt1;
      localSomeArgs.argi2 = paramInt2;
      localSomeArgs.arg3 = Long.valueOf(paramLong);
      this.mHandler.obtainMessage(2, localSomeArgs).sendToTarget();
    }
  }
  
  public class OnPermissionsChangeListenerDelegate
    extends IOnPermissionsChangeListener.Stub
    implements Handler.Callback
  {
    private static final int MSG_PERMISSIONS_CHANGED = 1;
    private final Handler mHandler;
    private final PackageManager.OnPermissionsChangedListener mListener;
    
    public OnPermissionsChangeListenerDelegate(PackageManager.OnPermissionsChangedListener paramOnPermissionsChangedListener, Looper paramLooper)
    {
      this.mListener = paramOnPermissionsChangedListener;
      this.mHandler = new Handler(paramLooper, this);
    }
    
    public boolean handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return false;
      }
      int i = paramMessage.arg1;
      this.mListener.onPermissionsChanged(i);
      return true;
    }
    
    public void onPermissionsChanged(int paramInt)
    {
      this.mHandler.obtainMessage(1, paramInt, 0).sendToTarget();
    }
  }
  
  private static final class ResourceName
  {
    final int iconId;
    final String packageName;
    
    ResourceName(ApplicationInfo paramApplicationInfo, int paramInt)
    {
      this(paramApplicationInfo.packageName, paramInt);
    }
    
    ResourceName(ComponentInfo paramComponentInfo, int paramInt)
    {
      this(paramComponentInfo.applicationInfo.packageName, paramInt);
    }
    
    ResourceName(ResolveInfo paramResolveInfo, int paramInt)
    {
      this(paramResolveInfo.activityInfo.applicationInfo.packageName, paramInt);
    }
    
    ResourceName(String paramString, int paramInt)
    {
      this.packageName = paramString;
      this.iconId = paramInt;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = false;
      if (this == paramObject) {
        return true;
      }
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (ResourceName)paramObject;
      if (this.iconId != ((ResourceName)paramObject).iconId) {
        return false;
      }
      if (this.packageName != null) {
        if (!this.packageName.equals(((ResourceName)paramObject).packageName)) {}
      }
      while (((ResourceName)paramObject).packageName == null)
      {
        bool = true;
        return bool;
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.packageName.hashCode() * 31 + this.iconId;
    }
    
    public String toString()
    {
      return "{ResourceName " + this.packageName + " / " + this.iconId + "}";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ApplicationPackageManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */