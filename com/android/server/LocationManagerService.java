package com.android.server;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OnOpChangedInternalListener;
import android.app.AppOpsManager.OnOpChangedListener;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.PendingIntent.OnFinished;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager.OnPermissionsChangedListener;
import android.content.pm.PackageManagerInternal;
import android.content.pm.PackageManagerInternal.PackagesProvider;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.location.ActivityRecognitionHardware;
import android.location.Address;
import android.location.Criteria;
import android.location.GeocoderParams;
import android.location.Geofence;
import android.location.IFusedGeofenceHardware;
import android.location.IGnssMeasurementsListener;
import android.location.IGnssNavigationMessageListener;
import android.location.IGnssStatusListener;
import android.location.IGnssStatusProvider;
import android.location.IGpsGeofenceHardware;
import android.location.ILocationListener;
import android.location.ILocationManager.Stub;
import android.location.INetInitiatedListener;
import android.location.Location;
import android.location.LocationProvider;
import android.location.LocationRequest;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.PowerManagerInternal;
import android.os.PowerManagerInternal.LowPowerModeListener;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import com.android.internal.content.PackageMonitor;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import com.android.internal.util.ArrayUtils;
import com.android.server.am.OnePlusProcessManager;
import com.android.server.location.ActivityRecognitionProxy;
import com.android.server.location.FlpHardwareProvider;
import com.android.server.location.FusedProxy;
import com.android.server.location.GeocoderProxy;
import com.android.server.location.GeofenceManager;
import com.android.server.location.GeofenceProxy;
import com.android.server.location.GnssLocationProvider;
import com.android.server.location.GnssLocationProvider.GnssSystemInfoProvider;
import com.android.server.location.GnssMeasurementsProvider;
import com.android.server.location.GnssNavigationMessageProvider;
import com.android.server.location.LocationBlacklist;
import com.android.server.location.LocationFudger;
import com.android.server.location.LocationProviderInterface;
import com.android.server.location.LocationProviderProxy;
import com.android.server.location.LocationRequestStatistics;
import com.android.server.location.LocationRequestStatistics.PackageProviderKey;
import com.android.server.location.LocationRequestStatistics.PackageStatistics;
import com.android.server.location.MockProvider;
import com.android.server.location.PassiveProvider;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class LocationManagerService
  extends ILocationManager.Stub
{
  private static final String ACCESS_LOCATION_EXTRA_COMMANDS = "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS";
  private static final String ACCESS_MOCK_LOCATION = "android.permission.ACCESS_MOCK_LOCATION";
  public static final boolean D = Log.isLoggable("LocationManagerService", 3);
  public static boolean DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
  private static final LocationRequest DEFAULT_LOCATION_REQUEST = new LocationRequest();
  private static final String FUSED_LOCATION_SERVICE_ACTION = "com.android.location.service.FusedLocationProvider";
  private static final long HIGH_POWER_INTERVAL_MS = 300000L;
  private static final String INSTALL_LOCATION_PROVIDER = "android.permission.INSTALL_LOCATION_PROVIDER";
  private static final int MAX_PROVIDER_SCHEDULING_JITTER_MS = 100;
  private static final int MSG_LOCATION_CHANGED = 1;
  private static final long NANOS_PER_MILLI = 1000000L;
  private static final String NETWORK_LOCATION_SERVICE_ACTION = "com.android.location.service.v3.NetworkLocationProvider";
  private static final int RESOLUTION_LEVEL_COARSE = 1;
  private static final int RESOLUTION_LEVEL_FINE = 2;
  private static final int RESOLUTION_LEVEL_NONE = 0;
  private static final String TAG = "LocationManagerService";
  private static final String WAKELOCK_KEY = "LocationManagerService";
  static ArrayList<Integer> mBlockLocationUids = new ArrayList();
  static ArrayList<Integer> mBlockReceiverUids = new ArrayList();
  private static Location sLastKnownCoarseIntervalLocation;
  private static Location sLastKnownLocation = null;
  private final AppOpsManager mAppOps;
  private LocationBlacklist mBlacklist;
  private String mComboNlpPackageName;
  private String mComboNlpReadyMarker;
  private String mComboNlpScreenMarker;
  private final Context mContext;
  private int mCurrentUserId = 0;
  private int[] mCurrentUserProfiles = { 0 };
  private final Set<String> mDisabledProviders = new HashSet();
  private final Set<String> mEnabledProviders = new HashSet();
  private GeocoderProxy mGeocodeProvider;
  private GeofenceManager mGeofenceManager;
  private GnssMeasurementsProvider mGnssMeasurementsProvider;
  private GnssNavigationMessageProvider mGnssNavigationMessageProvider;
  private IGnssStatusProvider mGnssStatusProvider;
  private GnssLocationProvider.GnssSystemInfoProvider mGnssSystemInfoProvider;
  private IGpsGeofenceHardware mGpsGeofenceProxy;
  private final ArrayList<UpdateRecord> mIsolatedUpdateRecords = new ArrayList();
  private final HashMap<String, Location> mLastLocation = new HashMap();
  private final HashMap<String, Location> mLastLocationCoarseInterval = new HashMap();
  private LocationFudger mLocationFudger;
  private LocationWorkerHandler mLocationHandler;
  private HandlerThread mLocationThread;
  private final Object mLock = new Object();
  private final HashMap<String, MockProvider> mMockProviders = new HashMap();
  private INetInitiatedListener mNetInitiatedListener;
  private PackageManager mPackageManager;
  private final PackageMonitor mPackageMonitor = new PackageMonitor()
  {
    public void onPackageDisappeared(String paramAnonymousString, int paramAnonymousInt)
    {
      label142:
      label145:
      synchronized (LocationManagerService.-get5(LocationManagerService.this))
      {
        Iterator localIterator = LocationManagerService.-get8(LocationManagerService.this).values().iterator();
        Object localObject1 = null;
        try
        {
          while (localIterator.hasNext())
          {
            LocationManagerService.Receiver localReceiver = (LocationManagerService.Receiver)localIterator.next();
            if (!localReceiver.mPackageName.equals(paramAnonymousString)) {
              break label145;
            }
            if (localObject1 != null) {
              break label142;
            }
            localObject1 = new ArrayList();
            ((ArrayList)localObject1).add(localReceiver);
          }
          if (localObject1 != null)
          {
            paramAnonymousString = ((Iterable)localObject1).iterator();
            while (paramAnonymousString.hasNext())
            {
              localObject1 = (LocationManagerService.Receiver)paramAnonymousString.next();
              LocationManagerService.-wrap6(LocationManagerService.this, (LocationManagerService.Receiver)localObject1);
            }
          }
        }
        finally {}
        return;
      }
    }
  };
  private PassiveProvider mPassiveProvider;
  private PowerManager mPowerManager;
  PowerManagerInternal mPowerManagerInternal;
  private final ArrayList<LocationProviderInterface> mProviders = new ArrayList();
  private final HashMap<String, LocationProviderInterface> mProvidersByName = new HashMap();
  private final ArrayList<LocationProviderProxy> mProxyProviders = new ArrayList();
  private final HashMap<String, LocationProviderInterface> mRealProviders = new HashMap();
  private final HashMap<Object, Receiver> mReceivers = new HashMap();
  private final HashMap<String, ArrayList<UpdateRecord>> mRecordsByProvider = new HashMap();
  private final LocationRequestStatistics mRequestStatistics = new LocationRequestStatistics();
  private UserManager mUserManager;
  
  static
  {
    sLastKnownCoarseIntervalLocation = null;
  }
  
  public LocationManagerService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAppOps = ((AppOpsManager)paramContext.getSystemService("appops"));
    ((PackageManagerInternal)LocalServices.getService(PackageManagerInternal.class)).setLocationPackagesProvider(new PackageManagerInternal.PackagesProvider()
    {
      public String[] getPackages(int paramAnonymousInt)
      {
        return LocationManagerService.-get1(LocationManagerService.this).getResources().getStringArray(17236015);
      }
    });
    if (D) {
      Log.d("LocationManagerService", "Constructed");
    }
    this.mPowerManagerInternal = ((PowerManagerInternal)LocalServices.getService(PowerManagerInternal.class));
    this.mPowerManagerInternal.registerLowPowerModeObserver(new PowerManagerInternal.LowPowerModeListener()
    {
      public void onLowPowerModeChanged(boolean paramAnonymousBoolean)
      {
        int i = SystemProperties.getInt("persist.sys.powersave.gps", 0);
        if (LocationManagerService.D) {
          Slog.d("LocationManagerService", "# onLowPowerModeChanged # enabled=" + paramAnonymousBoolean + ", flags=" + i);
        }
        ContentResolver localContentResolver = LocationManagerService.-get1(LocationManagerService.this).getContentResolver();
        int j = Settings.Secure.getInt(localContentResolver, "location_mode", 0);
        if (paramAnonymousBoolean) {
          if (j != 0) {
            Settings.Secure.putInt(localContentResolver, "location_mode", 0);
          }
        }
        for (i = j;; i = 0)
        {
          SystemProperties.set("persist.sys.powersave.gps", i + "");
          return;
          if ((j == 0) && (i != j)) {
            Settings.Secure.putInt(localContentResolver, "location_mode", i);
          }
        }
      }
    });
  }
  
  private void DumpUpdateRecord()
  {
    Log.d("LocationManagerService", "DumpUpdateRecord");
    if (D)
    {
      DumpUpdateRecordProvider("gps");
      DumpUpdateRecordProvider("network");
      DumpUpdateRecordProvider("passive");
      DumpUpdateRecordProvider("fused");
    }
  }
  
  private void DumpUpdateRecordProvider(String paramString)
  {
    int i = 0;
    Object localObject = (ArrayList)this.mRecordsByProvider.get(paramString);
    if ((localObject == null) || (((ArrayList)localObject).size() == 0)) {
      return;
    }
    Log.e("LocationManagerService", "provider= " + paramString + "  records.size=  " + ((ArrayList)localObject).size());
    paramString = ((Iterable)localObject).iterator();
    while (paramString.hasNext())
    {
      localObject = ((UpdateRecord)paramString.next()).mReceiver;
      Log.e("LocationManagerService", "i=" + i + " receiver.mUid " + ((Receiver)localObject).mUid + " receiver.mPid " + ((Receiver)localObject).mPid + "\n    receiver.mPackageName " + ((Receiver)localObject).mPackageName + "\n    receiver.mListener " + ((Receiver)localObject).mListener + "\n    receiver.mPendingIntent " + ((Receiver)localObject).mPendingIntent);
      i += 1;
    }
  }
  
  private void addProviderLocked(LocationProviderInterface paramLocationProviderInterface)
  {
    this.mProviders.add(paramLocationProviderInterface);
    this.mProvidersByName.put(paramLocationProviderInterface.getName(), paramLocationProviderInterface);
  }
  
  private void addTestProviderLocked(String paramString, ProviderProperties paramProviderProperties)
  {
    if (this.mProvidersByName.get(paramString) != null) {
      throw new IllegalArgumentException("Provider \"" + paramString + "\" already exists");
    }
    paramProviderProperties = new MockProvider(paramString, this, paramProviderProperties);
    addProviderLocked(paramProviderProperties);
    this.mMockProviders.put(paramString, paramProviderProperties);
    this.mLastLocation.put(paramString, null);
    this.mLastLocationCoarseInterval.put(paramString, null);
  }
  
  private void applyAllProviderRequirementsLocked()
  {
    Iterator localIterator = this.mProviders.iterator();
    while (localIterator.hasNext())
    {
      LocationProviderInterface localLocationProviderInterface = (LocationProviderInterface)localIterator.next();
      if (isAllowedByCurrentUserSettingsLocked(localLocationProviderInterface.getName())) {
        applyRequirementsLocked(localLocationProviderInterface.getName());
      }
    }
  }
  
  private void applyRequirementsLocked(String paramString)
  {
    LocationProviderInterface localLocationProviderInterface = (LocationProviderInterface)this.mProvidersByName.get(paramString);
    if (localLocationProviderInterface == null) {
      return;
    }
    Object localObject1 = (ArrayList)this.mRecordsByProvider.get(paramString);
    WorkSource localWorkSource = new WorkSource();
    ProviderRequest localProviderRequest = new ProviderRequest();
    if (localObject1 != null)
    {
      Object localObject2 = ((Iterable)localObject1).iterator();
      Object localObject3;
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (UpdateRecord)((Iterator)localObject2).next();
        if ((((UpdateRecord)localObject3).mReceiver != null) && (isCurrentProfile(UserHandle.getUserId(((UpdateRecord)localObject3).mReceiver.mUid))) && (!checkUidBlock(((UpdateRecord)localObject3).mReceiver.mUid)) && (checkLocationAccess(((UpdateRecord)localObject3).mReceiver.mPid, ((UpdateRecord)localObject3).mReceiver.mUid, ((UpdateRecord)localObject3).mReceiver.mPackageName, ((UpdateRecord)localObject3).mReceiver.mAllowedResolutionLevel)))
        {
          localObject3 = ((UpdateRecord)localObject3).mRequest;
          localProviderRequest.locationRequests.add(localObject3);
          if (((LocationRequest)localObject3).getInterval() < localProviderRequest.interval)
          {
            localProviderRequest.reportLocation = true;
            localProviderRequest.interval = ((LocationRequest)localObject3).getInterval();
          }
        }
      }
      if (localProviderRequest.reportLocation)
      {
        long l = (localProviderRequest.interval + 1000L) * 3L / 2L;
        localObject1 = ((Iterable)localObject1).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (UpdateRecord)((Iterator)localObject1).next();
          if (isCurrentProfile(UserHandle.getUserId(((UpdateRecord)localObject2).mReceiver.mUid)))
          {
            localObject3 = ((UpdateRecord)localObject2).mRequest;
            if ((localProviderRequest.locationRequests.contains(localObject3)) && (((LocationRequest)localObject3).getInterval() <= l)) {
              if ((((UpdateRecord)localObject2).mReceiver.mWorkSource != null) && (((UpdateRecord)localObject2).mReceiver.mWorkSource.size() > 0) && (((UpdateRecord)localObject2).mReceiver.mWorkSource.getName(0) != null)) {
                localWorkSource.add(((UpdateRecord)localObject2).mReceiver.mWorkSource);
              } else {
                localWorkSource.add(((UpdateRecord)localObject2).mReceiver.mUid, ((UpdateRecord)localObject2).mReceiver.mPackageName);
              }
            }
          }
        }
      }
    }
    if (D) {
      Log.d("LocationManagerService", "provider request: " + paramString + " " + localProviderRequest);
    }
    localLocationProviderInterface.setRequest(localProviderRequest, localWorkSource);
  }
  
  private boolean canCallerAccessMockLocation(String paramString)
  {
    boolean bool = false;
    if (this.mAppOps.noteOp(58, Binder.getCallingUid(), paramString) == 0) {
      bool = true;
    }
    return bool;
  }
  
  private void checkCallerIsProvider()
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.INSTALL_LOCATION_PROVIDER") == 0) {
      return;
    }
    if (isUidALocationProvider(Binder.getCallingUid())) {
      return;
    }
    throw new SecurityException("need INSTALL_LOCATION_PROVIDER permission, or UID of a currently bound location provider");
  }
  
  private void checkDeviceStatsAllowed()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS", null);
  }
  
  private Receiver checkListenerOrIntentLocked(ILocationListener paramILocationListener, PendingIntent paramPendingIntent, int paramInt1, int paramInt2, String paramString, WorkSource paramWorkSource, boolean paramBoolean)
  {
    if ((paramPendingIntent == null) && (paramILocationListener == null)) {
      throw new IllegalArgumentException("need either listener or intent");
    }
    if ((paramPendingIntent != null) && (paramILocationListener != null)) {
      throw new IllegalArgumentException("cannot register both listener and intent");
    }
    if (paramPendingIntent != null)
    {
      checkPendingIntent(paramPendingIntent);
      return getReceiverLocked(paramPendingIntent, paramInt1, paramInt2, paramString, paramWorkSource, paramBoolean);
    }
    return getReceiverLocked(paramILocationListener, paramInt1, paramInt2, paramString, paramWorkSource, paramBoolean);
  }
  
  private void checkPackageName(String paramString)
  {
    if (paramString == null) {
      throw new SecurityException("invalid package name: " + paramString);
    }
    int i = Binder.getCallingUid();
    String[] arrayOfString = this.mPackageManager.getPackagesForUid(i);
    if (arrayOfString == null) {
      throw new SecurityException("invalid UID " + i);
    }
    i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      if (paramString.equals(arrayOfString[i])) {
        return;
      }
      i += 1;
    }
    throw new SecurityException("invalid package name: " + paramString);
  }
  
  private void checkPendingIntent(PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent == null) {
      throw new IllegalArgumentException("invalid pending intent: " + paramPendingIntent);
    }
  }
  
  public static boolean checkReceiverUid(int paramInt)
  {
    return mBlockReceiverUids.contains(Integer.valueOf(paramInt));
  }
  
  private void checkResolutionLevelIsSufficientForGeofenceUse(int paramInt)
  {
    if (paramInt < 2) {
      throw new SecurityException("Geofence usage requires ACCESS_FINE_LOCATION permission");
    }
  }
  
  private void checkResolutionLevelIsSufficientForProviderUse(int paramInt, String paramString)
  {
    int i = getMinimumResolutionLevelForProviderUse(paramString);
    if (paramInt < i)
    {
      switch (i)
      {
      default: 
        throw new SecurityException("Insufficient permission for \"" + paramString + "\" location provider.");
      case 2: 
        throw new SecurityException("\"" + paramString + "\" location provider " + "requires ACCESS_FINE_LOCATION permission.");
      }
      throw new SecurityException("\"" + paramString + "\" location provider " + "requires ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission.");
    }
  }
  
  public static boolean checkUidBlock(int paramInt)
  {
    return mBlockLocationUids.contains(Integer.valueOf(paramInt));
  }
  
  private void checkUpdateAppOpsAllowed()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_APP_OPS_STATS", null);
  }
  
  private LocationRequest createSanitizedRequest(LocationRequest paramLocationRequest, int paramInt)
  {
    LocationRequest localLocationRequest = new LocationRequest(paramLocationRequest);
    if (paramInt < 2) {
      switch (localLocationRequest.getQuality())
      {
      }
    }
    for (;;)
    {
      if (localLocationRequest.getInterval() < 600000L) {
        localLocationRequest.setInterval(600000L);
      }
      if (localLocationRequest.getFastestInterval() < 600000L) {
        localLocationRequest.setFastestInterval(600000L);
      }
      if (localLocationRequest.getFastestInterval() > localLocationRequest.getInterval()) {
        paramLocationRequest.setFastestInterval(paramLocationRequest.getInterval());
      }
      return localLocationRequest;
      localLocationRequest.setQuality(102);
      continue;
      localLocationRequest.setQuality(201);
    }
  }
  
  private boolean doesUidHavePackage(int paramInt, String paramString)
  {
    if (paramString == null) {
      return false;
    }
    String[] arrayOfString = this.mPackageManager.getPackagesForUid(paramInt);
    if (arrayOfString == null) {
      return false;
    }
    int i = arrayOfString.length;
    paramInt = 0;
    while (paramInt < i)
    {
      if (paramString.equals(arrayOfString[paramInt])) {
        return true;
      }
      paramInt += 1;
    }
    return false;
  }
  
  private void ensureFallbackFusedProviderPresentLocked(ArrayList<String> paramArrayList)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    String str1 = this.mContext.getPackageName();
    paramArrayList = ServiceWatcher.getSignatureSets(this.mContext, paramArrayList);
    Iterator localIterator = localPackageManager.queryIntentServicesAsUser(new Intent("com.android.location.service.FusedLocationProvider"), 128, this.mCurrentUserId).iterator();
    while (localIterator.hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localIterator.next();
      String str2 = localResolveInfo.serviceInfo.packageName;
      try
      {
        if (ServiceWatcher.isSignatureMatch(localPackageManager.getPackageInfo(str2, 64).signatures, paramArrayList)) {
          break label175;
        }
        Log.w("LocationManagerService", str2 + " resolves service " + "com.android.location.service.FusedLocationProvider" + ", but has wrong signature, ignoring");
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e("LocationManagerService", "missing package: " + str2);
      }
      continue;
      label175:
      if (localNameNotFoundException.serviceInfo.metaData == null) {
        Log.w("LocationManagerService", "Found fused provider without metadata: " + str2);
      } else if (localNameNotFoundException.serviceInfo.metaData.getInt("serviceVersion", -1) == 0)
      {
        if ((localNameNotFoundException.serviceInfo.applicationInfo.flags & 0x1) == 0)
        {
          if (D) {
            Log.d("LocationManagerService", "Fallback candidate not in /system: " + str2);
          }
        }
        else if (localPackageManager.checkSignatures(str1, str2) != 0)
        {
          if (D) {
            Log.d("LocationManagerService", "Fallback candidate not signed the same as system: " + str2);
          }
        }
        else if (D) {
          Log.d("LocationManagerService", "Found fallback provider: " + str2);
        }
      }
      else if (D) {
        Log.d("LocationManagerService", "Fallback candidate not version 0: " + str2);
      }
    }
    throw new IllegalStateException("Unable to find a fused location provider that is in the system partition with version 0 and signed with the platform certificate. Such a package is needed to provide a default fused location provider in the event that no other fused location provider has been installed or is currently available. For example, coreOnly boot mode when decrypting the data partition. The fallback must also be marked coreApp=\"true\" in the manifest");
  }
  
  private int getAllowedResolutionLevel(int paramInt1, int paramInt2)
  {
    if (this.mContext.checkPermission("android.permission.ACCESS_FINE_LOCATION", paramInt1, paramInt2) == 0) {
      return 2;
    }
    if (this.mContext.checkPermission("android.permission.ACCESS_COARSE_LOCATION", paramInt1, paramInt2) == 0) {
      return 1;
    }
    return 0;
  }
  
  private int getCallerAllowedResolutionLevel()
  {
    return getAllowedResolutionLevel(Binder.getCallingPid(), Binder.getCallingUid());
  }
  
  private int getMinimumResolutionLevelForProviderUse(String paramString)
  {
    if (("gps".equals(paramString)) || ("passive".equals(paramString))) {
      return 2;
    }
    if (("network".equals(paramString)) || ("fused".equals(paramString))) {
      return 1;
    }
    paramString = (LocationProviderInterface)this.mMockProviders.get(paramString);
    if (paramString != null)
    {
      paramString = paramString.getProperties();
      if (paramString != null)
      {
        if (paramString.mRequiresSatellite) {
          return 2;
        }
        if ((paramString.mRequiresNetwork) || (paramString.mRequiresCell)) {
          return 1;
        }
      }
    }
    return 2;
  }
  
  private Receiver getReceiverLocked(PendingIntent paramPendingIntent, int paramInt1, int paramInt2, String paramString, WorkSource paramWorkSource, boolean paramBoolean)
  {
    Receiver localReceiver2 = (Receiver)this.mReceivers.get(paramPendingIntent);
    Receiver localReceiver1 = localReceiver2;
    if (localReceiver2 == null)
    {
      localReceiver1 = new Receiver(null, paramPendingIntent, paramInt1, paramInt2, paramString, paramWorkSource, paramBoolean);
      this.mReceivers.put(paramPendingIntent, localReceiver1);
    }
    return localReceiver1;
  }
  
  private Receiver getReceiverLocked(ILocationListener paramILocationListener, int paramInt1, int paramInt2, String paramString, WorkSource paramWorkSource, boolean paramBoolean)
  {
    IBinder localIBinder = paramILocationListener.asBinder();
    Receiver localReceiver2 = (Receiver)this.mReceivers.get(localIBinder);
    Receiver localReceiver1 = localReceiver2;
    if (localReceiver2 == null) {
      localReceiver1 = new Receiver(paramILocationListener, null, paramInt1, paramInt2, paramString, paramWorkSource, paramBoolean);
    }
    try
    {
      localReceiver1.getListener().asBinder().linkToDeath(localReceiver1, 0);
      this.mReceivers.put(localIBinder, localReceiver1);
      return localReceiver1;
    }
    catch (RemoteException paramILocationListener)
    {
      Slog.e("LocationManagerService", "linkToDeath failed:", paramILocationListener);
    }
    return null;
  }
  
  private String getResolutionPermission(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 2: 
      return "android.permission.ACCESS_FINE_LOCATION";
    }
    return "android.permission.ACCESS_COARSE_LOCATION";
  }
  
  private void handleLocationChanged(Location paramLocation, boolean paramBoolean)
  {
    Location localLocation = new Location(paramLocation);
    String str = localLocation.getProvider();
    if ((!localLocation.isFromMockProvider()) && (isMockProvider(str))) {
      localLocation.setIsFromMockProvider(true);
    }
    synchronized (this.mLock)
    {
      if (isAllowedByCurrentUserSettingsLocked(str))
      {
        if (!paramBoolean)
        {
          paramLocation = screenLocationLocked(paramLocation, str);
          if (paramLocation == null) {
            return;
          }
          this.mPassiveProvider.updateLocation(localLocation);
        }
        handleLocationChangedLocked(localLocation, paramBoolean);
      }
      return;
    }
  }
  
  private void handleLocationChangedLocked(Location paramLocation, boolean paramBoolean)
  {
    if (D) {
      Log.d("LocationManagerService", "incoming location: " + paramLocation);
    }
    long l1 = SystemClock.elapsedRealtime();
    if (paramBoolean) {}
    for (String str = "passive";; str = paramLocation.getProvider())
    {
      localObject4 = (LocationProviderInterface)this.mProvidersByName.get(str);
      if (localObject4 != null) {
        break;
      }
      return;
    }
    Object localObject3 = paramLocation.getExtraLocation("noGPSLocation");
    Object localObject2 = (Location)this.mLastLocation.get(str);
    Object localObject1;
    if (localObject2 == null)
    {
      localObject1 = new Location(str);
      this.mLastLocation.put(str, localObject1);
      sLastKnownLocation = (Location)localObject1;
    }
    Object localObject5;
    for (;;)
    {
      ((Location)localObject1).set(paramLocation);
      localObject3 = (Location)this.mLastLocationCoarseInterval.get(str);
      localObject2 = localObject3;
      if (localObject3 == null)
      {
        localObject2 = new Location(paramLocation);
        this.mLastLocationCoarseInterval.put(str, localObject2);
        sLastKnownCoarseIntervalLocation = (Location)localObject2;
      }
      if (paramLocation.getElapsedRealtimeNanos() - ((Location)localObject2).getElapsedRealtimeNanos() > 600000000000L) {
        ((Location)localObject2).set(paramLocation);
      }
      localObject2 = ((Location)localObject2).getExtraLocation("noGPSLocation");
      localObject3 = (ArrayList)this.mRecordsByProvider.get(str);
      if ((localObject3 != null) && (((ArrayList)localObject3).size() != 0)) {
        break;
      }
      return;
      localObject5 = ((Location)localObject2).getExtraLocation("noGPSLocation");
      localObject1 = localObject2;
      if (localObject3 == null)
      {
        localObject1 = localObject2;
        if (localObject5 != null)
        {
          paramLocation.setExtraLocation("noGPSLocation", (Location)localObject5);
          localObject1 = localObject2;
        }
      }
    }
    paramLocation = null;
    if (localObject2 != null) {
      paramLocation = this.mLocationFudger.getOrCreate((Location)localObject2);
    }
    long l2 = ((LocationProviderInterface)localObject4).getStatusUpdateTime();
    Bundle localBundle = new Bundle();
    int m = ((LocationProviderInterface)localObject4).getStatus(localBundle);
    Object localObject4 = null;
    localObject2 = null;
    Iterator localIterator = ((Iterable)localObject3).iterator();
    while (localIterator.hasNext())
    {
      localObject5 = (UpdateRecord)localIterator.next();
      Receiver localReceiver = ((UpdateRecord)localObject5).mReceiver;
      int k = 0;
      int j = 0;
      int i = UserHandle.getUserId(localReceiver.mUid);
      if ((isCurrentProfile(i)) || (isUidALocationProvider(localReceiver.mUid)))
      {
        if (this.mBlacklist.isBlacklisted(localReceiver.mPackageName))
        {
          if (!D) {
            continue;
          }
          Log.d("LocationManagerService", "skipping loc update for blacklisted app: " + localReceiver.mPackageName);
        }
      }
      else
      {
        if (!D) {
          continue;
        }
        Log.d("LocationManagerService", "skipping loc update for background user " + i + " (current user: " + this.mCurrentUserId + ", app: " + localReceiver.mPackageName + ")");
        continue;
      }
      if (checkUidBlock(localReceiver.mUid))
      {
        Log.d("LocationManagerService", "uid block  uid = " + localReceiver.mUid + " package =" + localReceiver.mPackageName);
      }
      else if (!reportLocationAccessNoThrow(localReceiver.mPid, localReceiver.mUid, localReceiver.mPackageName, localReceiver.mAllowedResolutionLevel))
      {
        if (D) {
          Log.d("LocationManagerService", "skipping loc update for no op app: " + localReceiver.mPackageName);
        }
      }
      else
      {
        label675:
        Location localLocation;
        if (localReceiver.mAllowedResolutionLevel < 2)
        {
          localObject3 = paramLocation;
          i = k;
          if (localObject3 != null)
          {
            localLocation = ((UpdateRecord)localObject5).mLastFixBroadcast;
            if (localLocation != null)
            {
              i = k;
              if (!shouldBroadcastSafe((Location)localObject3, localLocation, (UpdateRecord)localObject5, l1)) {}
            }
            else
            {
              if (localLocation != null) {
                break label995;
              }
              ((UpdateRecord)localObject5).mLastFixBroadcast = new Location((Location)localObject3);
            }
          }
        }
        for (;;)
        {
          i = j;
          if (!localReceiver.callLocationChangedLocked((Location)localObject3))
          {
            Slog.w("LocationManagerService", "RemoteException calling onLocationChanged on " + localReceiver);
            i = 1;
          }
          ((UpdateRecord)localObject5).mRequest.decrementNumUpdates();
          long l3 = ((UpdateRecord)localObject5).mLastStatusBroadcast;
          j = i;
          if (l2 > l3) {
            if (l3 == 0L)
            {
              j = i;
              if (m == 2) {}
            }
            else
            {
              ((UpdateRecord)localObject5).mLastStatusBroadcast = l2;
              j = i;
              if (!localReceiver.callStatusChangedLocked(str, m, localBundle))
              {
                j = 1;
                Slog.w("LocationManagerService", "RemoteException calling onStatusChanged on " + localReceiver);
              }
            }
          }
          if (((UpdateRecord)localObject5).mRequest.getNumUpdates() > 0)
          {
            localObject3 = localObject2;
            if (((UpdateRecord)localObject5).mRequest.getExpireAt() >= l1) {}
          }
          else
          {
            localObject3 = localObject2;
            if (localObject2 == null) {
              localObject3 = new ArrayList();
            }
            ((ArrayList)localObject3).add(localObject5);
          }
          localObject2 = localObject3;
          if (j == 0) {
            break;
          }
          localObject5 = localObject4;
          if (localObject4 == null) {
            localObject5 = new ArrayList();
          }
          localObject4 = localObject5;
          localObject2 = localObject3;
          if (((ArrayList)localObject5).contains(localReceiver)) {
            break;
          }
          ((ArrayList)localObject5).add(localReceiver);
          localObject4 = localObject5;
          localObject2 = localObject3;
          break;
          localObject3 = localObject1;
          break label675;
          label995:
          localLocation.set((Location)localObject3);
        }
      }
    }
    if (localObject4 != null)
    {
      paramLocation = ((Iterable)localObject4).iterator();
      while (paramLocation.hasNext()) {
        removeUpdatesLocked((Receiver)paramLocation.next());
      }
    }
    if (localObject2 != null)
    {
      paramLocation = ((Iterable)localObject2).iterator();
      while (paramLocation.hasNext()) {
        ((UpdateRecord)paramLocation.next()).disposeLocked(true);
      }
      applyRequirementsLocked(str);
    }
  }
  
  private boolean isAllowedByCurrentUserSettingsLocked(String paramString)
  {
    if (this.mEnabledProviders.contains(paramString)) {
      return true;
    }
    if (this.mDisabledProviders.contains(paramString)) {
      return false;
    }
    return Settings.Secure.isLocationProviderEnabledForUser(this.mContext.getContentResolver(), paramString, this.mCurrentUserId);
  }
  
  private boolean isAllowedByUserSettingsLocked(String paramString, int paramInt)
  {
    if ((isCurrentProfile(UserHandle.getUserId(paramInt))) || (isUidALocationProvider(paramInt))) {
      return isAllowedByCurrentUserSettingsLocked(paramString);
    }
    return false;
  }
  
  private boolean isCurrentProfile(int paramInt)
  {
    synchronized (this.mLock)
    {
      boolean bool = ArrayUtils.contains(this.mCurrentUserProfiles, paramInt);
      return bool;
    }
  }
  
  private boolean isMockProvider(String paramString)
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mMockProviders.containsKey(paramString);
      return bool;
    }
  }
  
  private boolean isUidALocationProvider(int paramInt)
  {
    if (paramInt == 1000) {
      return true;
    }
    if ((this.mGeocodeProvider != null) && (doesUidHavePackage(paramInt, this.mGeocodeProvider.getConnectedPackageName()))) {
      return true;
    }
    Iterator localIterator = this.mProxyProviders.iterator();
    while (localIterator.hasNext()) {
      if (doesUidHavePackage(paramInt, ((LocationProviderProxy)localIterator.next()).getConnectedPackageName())) {
        return true;
      }
    }
    return false;
  }
  
  private void loadProvidersLocked()
  {
    Object localObject1 = new PassiveProvider(this);
    addProviderLocked((LocationProviderInterface)localObject1);
    this.mEnabledProviders.add(((PassiveProvider)localObject1).getName());
    this.mPassiveProvider = ((PassiveProvider)localObject1);
    if (GnssLocationProvider.isSupported())
    {
      localObject1 = new GnssLocationProvider(this.mContext, this, this.mLocationHandler.getLooper());
      this.mGnssSystemInfoProvider = ((GnssLocationProvider)localObject1).getGnssSystemInfoProvider();
      this.mGnssStatusProvider = ((GnssLocationProvider)localObject1).getGnssStatusProvider();
      this.mNetInitiatedListener = ((GnssLocationProvider)localObject1).getNetInitiatedListener();
      addProviderLocked((LocationProviderInterface)localObject1);
      this.mRealProviders.put("gps", localObject1);
      this.mGnssMeasurementsProvider = ((GnssLocationProvider)localObject1).getGnssMeasurementsProvider();
      this.mGnssNavigationMessageProvider = ((GnssLocationProvider)localObject1).getGnssNavigationMessageProvider();
      this.mGpsGeofenceProxy = ((GnssLocationProvider)localObject1).getGpsGeofenceProxy();
    }
    Object localObject3 = this.mContext.getResources();
    localObject1 = new ArrayList();
    Object localObject2 = ((Resources)localObject3).getStringArray(17236015);
    if (D) {
      Log.d("LocationManagerService", "certificates for location providers pulled from: " + Arrays.toString((Object[])localObject2));
    }
    if (localObject2 != null) {
      ((ArrayList)localObject1).addAll(Arrays.asList((Object[])localObject2));
    }
    ensureFallbackFusedProviderPresentLocked((ArrayList)localObject1);
    localObject1 = LocationProviderProxy.createAndBind(this.mContext, "network", "com.android.location.service.v3.NetworkLocationProvider", 17956946, 17039423, 17236015, this.mLocationHandler);
    label360:
    label460:
    label490:
    label542:
    int i;
    int j;
    if (localObject1 != null)
    {
      this.mRealProviders.put("network", localObject1);
      this.mProxyProviders.add(localObject1);
      addProviderLocked((LocationProviderInterface)localObject1);
      localObject1 = LocationProviderProxy.createAndBind(this.mContext, "fused", "com.android.location.service.FusedLocationProvider", 17956947, 17039424, 17236015, this.mLocationHandler);
      if (localObject1 == null) {
        break label751;
      }
      addProviderLocked((LocationProviderInterface)localObject1);
      this.mProxyProviders.add(localObject1);
      this.mEnabledProviders.add(((LocationProviderProxy)localObject1).getName());
      this.mRealProviders.put("fused", localObject1);
      this.mGeocodeProvider = GeocoderProxy.createAndBind(this.mContext, 17956949, 17039426, 17236015, this.mLocationHandler);
      if (this.mGeocodeProvider == null) {
        Slog.e("LocationManagerService", "no geocoder provider found");
      }
      if (!FlpHardwareProvider.isSupported()) {
        break label773;
      }
      localObject2 = FlpHardwareProvider.getInstance(this.mContext);
      localObject1 = localObject2;
      if (FusedProxy.createAndBind(this.mContext, this.mLocationHandler, ((FlpHardwareProvider)localObject2).getLocationHardware(), 17956948, 17039425, 17236015) == null)
      {
        Slog.d("LocationManagerService", "Unable to bind FusedProxy.");
        localObject1 = localObject2;
      }
      localObject2 = this.mContext;
      LocationWorkerHandler localLocationWorkerHandler = this.mLocationHandler;
      IGpsGeofenceHardware localIGpsGeofenceHardware = this.mGpsGeofenceProxy;
      if (localObject1 == null) {
        break label788;
      }
      localObject1 = ((FlpHardwareProvider)localObject1).getGeofenceHardware();
      if (GeofenceProxy.createAndBind((Context)localObject2, 17956950, 17039427, 17236015, localLocationWorkerHandler, localIGpsGeofenceHardware, (IFusedGeofenceHardware)localObject1) == null) {
        Slog.d("LocationManagerService", "Unable to bind FLP Geofence proxy.");
      }
      boolean bool = ActivityRecognitionHardware.isSupported();
      localObject1 = null;
      if (!bool) {
        break label794;
      }
      localObject1 = ActivityRecognitionHardware.getInstance(this.mContext);
      if (ActivityRecognitionProxy.createAndBind(this.mContext, this.mLocationHandler, bool, (ActivityRecognitionHardware)localObject1, 17956951, 17039428, 17236015) == null) {
        Slog.d("LocationManagerService", "Unable to bind ActivityRecognitionProxy.");
      }
      this.mComboNlpPackageName = ((Resources)localObject3).getString(17039429);
      if (this.mComboNlpPackageName != null)
      {
        this.mComboNlpReadyMarker = (this.mComboNlpPackageName + ".nlp:ready");
        this.mComboNlpScreenMarker = (this.mComboNlpPackageName + ".nlp:screen");
      }
      localObject1 = ((Resources)localObject3).getStringArray(17236016);
      i = 0;
      j = localObject1.length;
    }
    for (;;)
    {
      if (i >= j) {
        return;
      }
      localObject2 = localObject1[i].split(",");
      localObject3 = localObject2[0].trim();
      if (this.mProvidersByName.get(localObject3) != null)
      {
        throw new IllegalArgumentException("Provider \"" + (String)localObject3 + "\" already exists");
        Slog.w("LocationManagerService", "no network location provider found");
        break;
        label751:
        Slog.e("LocationManagerService", "no fused location provider found", new IllegalStateException("Location service needs a fused location provider"));
        break label360;
        label773:
        localObject1 = null;
        Slog.d("LocationManagerService", "FLP HAL not supported");
        break label460;
        label788:
        localObject1 = null;
        break label490;
        label794:
        Slog.d("LocationManagerService", "Hardware Activity-Recognition not supported.");
        break label542;
      }
      addTestProviderLocked((String)localObject3, new ProviderProperties(Boolean.parseBoolean(localObject2[1]), Boolean.parseBoolean(localObject2[2]), Boolean.parseBoolean(localObject2[3]), Boolean.parseBoolean(localObject2[4]), Boolean.parseBoolean(localObject2[5]), Boolean.parseBoolean(localObject2[6]), Boolean.parseBoolean(localObject2[7]), Integer.parseInt(localObject2[8]), Integer.parseInt(localObject2[9])));
      i += 1;
    }
  }
  
  private void log(String paramString)
  {
    if (Log.isLoggable("LocationManagerService", 2)) {
      Slog.d("LocationManagerService", paramString);
    }
  }
  
  private String pickBest(List<String> paramList)
  {
    if (paramList.contains("gps")) {
      return "gps";
    }
    if (paramList.contains("network")) {
      return "network";
    }
    return (String)paramList.get(0);
  }
  
  private void removeProviderLocked(LocationProviderInterface paramLocationProviderInterface)
  {
    paramLocationProviderInterface.disable();
    this.mProviders.remove(paramLocationProviderInterface);
    this.mProvidersByName.remove(paramLocationProviderInterface.getName());
  }
  
  /* Error */
  private void removeUpdatesLocked(Receiver paramReceiver)
  {
    // Byte code:
    //   0: getstatic 257	com/android/server/LocationManagerService:D	Z
    //   3: ifeq +35 -> 38
    //   6: ldc 68
    //   8: new 377	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 378	java/lang/StringBuilder:<init>	()V
    //   15: ldc_w 1214
    //   18: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   21: aload_1
    //   22: invokestatic 1220	java/lang/System:identityHashCode	(Ljava/lang/Object;)I
    //   25: invokestatic 1223	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   28: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   31: invokevirtual 393	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   34: invokestatic 1226	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   37: pop
    //   38: aload_0
    //   39: getfield 189	com/android/server/LocationManagerService:mReceivers	Ljava/util/HashMap;
    //   42: aload_1
    //   43: getfield 1229	com/android/server/LocationManagerService$Receiver:mKey	Ljava/lang/Object;
    //   46: invokevirtual 1212	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   49: ifnull +35 -> 84
    //   52: aload_1
    //   53: invokevirtual 1232	com/android/server/LocationManagerService$Receiver:isListener	()Z
    //   56: ifeq +28 -> 84
    //   59: aload_1
    //   60: invokevirtual 820	com/android/server/LocationManagerService$Receiver:getListener	()Landroid/location/ILocationListener;
    //   63: invokeinterface 816 1 0
    //   68: aload_1
    //   69: iconst_0
    //   70: invokeinterface 1236 3 0
    //   75: pop
    //   76: aload_1
    //   77: monitorenter
    //   78: aload_1
    //   79: invokevirtual 1239	com/android/server/LocationManagerService$Receiver:clearPendingBroadcastsLocked	()V
    //   82: aload_1
    //   83: monitorexit
    //   84: aload_1
    //   85: iconst_0
    //   86: invokevirtual 1242	com/android/server/LocationManagerService$Receiver:updateMonitoring	(Z)V
    //   89: new 287	java/util/HashSet
    //   92: dup
    //   93: invokespecial 288	java/util/HashSet:<init>	()V
    //   96: astore_2
    //   97: aload_1
    //   98: getfield 1245	com/android/server/LocationManagerService$Receiver:mUpdateRecords	Ljava/util/HashMap;
    //   101: astore_1
    //   102: aload_1
    //   103: ifnull +52 -> 155
    //   106: aload_1
    //   107: invokevirtual 1249	java/util/HashMap:values	()Ljava/util/Collection;
    //   110: invokeinterface 402 1 0
    //   115: astore_3
    //   116: aload_3
    //   117: invokeinterface 408 1 0
    //   122: ifeq +24 -> 146
    //   125: aload_3
    //   126: invokeinterface 412 1 0
    //   131: checkcast 26	com/android/server/LocationManagerService$UpdateRecord
    //   134: iconst_0
    //   135: invokevirtual 968	com/android/server/LocationManagerService$UpdateRecord:disposeLocked	(Z)V
    //   138: goto -22 -> 116
    //   141: astore_2
    //   142: aload_1
    //   143: monitorexit
    //   144: aload_2
    //   145: athrow
    //   146: aload_2
    //   147: aload_1
    //   148: invokevirtual 1253	java/util/HashMap:keySet	()Ljava/util/Set;
    //   151: invokevirtual 1254	java/util/HashSet:addAll	(Ljava/util/Collection;)Z
    //   154: pop
    //   155: aload_2
    //   156: invokeinterface 402 1 0
    //   161: astore_1
    //   162: aload_1
    //   163: invokeinterface 408 1 0
    //   168: ifeq +29 -> 197
    //   171: aload_1
    //   172: invokeinterface 412 1 0
    //   177: checkcast 618	java/lang/String
    //   180: astore_2
    //   181: aload_0
    //   182: aload_2
    //   183: invokespecial 198	com/android/server/LocationManagerService:isAllowedByCurrentUserSettingsLocked	(Ljava/lang/String;)Z
    //   186: ifeq -24 -> 162
    //   189: aload_0
    //   190: aload_2
    //   191: invokespecial 222	com/android/server/LocationManagerService:applyRequirementsLocked	(Ljava/lang/String;)V
    //   194: goto -32 -> 162
    //   197: aload_0
    //   198: invokespecial 1256	com/android/server/LocationManagerService:DumpUpdateRecord	()V
    //   201: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	202	0	this	LocationManagerService
    //   0	202	1	paramReceiver	Receiver
    //   96	1	2	localHashSet	HashSet
    //   141	15	2	localObject	Object
    //   180	11	2	str	String
    //   115	11	3	localIterator	Iterator
    // Exception table:
    //   from	to	target	type
    //   78	82	141	finally
  }
  
  private void requestLocationUpdatesLocked(LocationRequest paramLocationRequest, Receiver paramReceiver, int paramInt1, int paramInt2, String paramString)
  {
    LocationRequest localLocationRequest = paramLocationRequest;
    if (paramLocationRequest == null) {
      localLocationRequest = DEFAULT_LOCATION_REQUEST;
    }
    paramLocationRequest = localLocationRequest.getProvider();
    if (paramLocationRequest == null) {
      throw new IllegalArgumentException("provider name must not be null");
    }
    if (D) {
      Log.d("LocationManagerService", "request " + Integer.toHexString(System.identityHashCode(paramReceiver)) + " " + paramLocationRequest + " " + localLocationRequest + " from " + paramString + "(" + paramInt2 + ")");
    }
    if ((LocationProviderInterface)this.mProvidersByName.get(paramLocationRequest) == null) {
      throw new IllegalArgumentException("provider doesn't exist: " + paramLocationRequest);
    }
    paramString = new UpdateRecord(paramLocationRequest, localLocationRequest, paramReceiver);
    paramString = (UpdateRecord)paramReceiver.mUpdateRecords.put(paramLocationRequest, paramString);
    if (paramString != null) {
      paramString.disposeLocked(false);
    }
    if (isAllowedByUserSettingsLocked(paramLocationRequest, paramInt2)) {
      applyRequirementsLocked(paramLocationRequest);
    }
    for (;;)
    {
      paramReceiver.updateMonitoring(true);
      DumpUpdateRecord();
      return;
      paramReceiver.callProviderEnabledLocked(paramLocationRequest, false);
    }
  }
  
  public static int resolutionLevelToOp(int paramInt)
  {
    if (paramInt != 0)
    {
      if (paramInt == 1) {
        return 0;
      }
      return 1;
    }
    return -1;
  }
  
  private Location screenLocationLocked(Location paramLocation, String paramString)
  {
    if (isMockProvider("network")) {
      return paramLocation;
    }
    Object localObject = (LocationProviderProxy)this.mProvidersByName.get("network");
    if ((this.mComboNlpPackageName == null) || (localObject == null)) {}
    while ((!paramString.equals("network")) || (isMockProvider("network"))) {
      return paramLocation;
    }
    paramString = ((LocationProviderProxy)localObject).getConnectedPackageName();
    int j;
    int i;
    if ((paramString != null) && (paramString.equals(this.mComboNlpPackageName)))
    {
      paramString = paramLocation.getExtras();
      j = 0;
      i = 0;
      if ((paramString == null) || (!paramString.containsKey(this.mComboNlpReadyMarker))) {
        break label134;
      }
      if (D) {
        Log.d("LocationManagerService", "This location is marked as ready for broadcast");
      }
      paramString.remove(this.mComboNlpReadyMarker);
    }
    label134:
    do
    {
      return paramLocation;
      return paramLocation;
      localObject = (ArrayList)this.mRecordsByProvider.get("passive");
      if (localObject != null)
      {
        Iterator localIterator = ((Iterable)localObject).iterator();
        for (;;)
        {
          j = i;
          if (!localIterator.hasNext()) {
            break;
          }
          UpdateRecord localUpdateRecord = (UpdateRecord)localIterator.next();
          if (localUpdateRecord.mReceiver.mPackageName.equals(this.mComboNlpPackageName))
          {
            localObject = paramString;
            j = i;
            if (i == 0)
            {
              j = 1;
              localObject = paramString;
              if (paramString == null)
              {
                paramLocation.setExtras(new Bundle());
                localObject = paramLocation.getExtras();
              }
              ((Bundle)localObject).putBoolean(this.mComboNlpScreenMarker, true);
            }
            if (!localUpdateRecord.mReceiver.callLocationChangedLocked(paramLocation))
            {
              Slog.w("LocationManagerService", "RemoteException calling onLocationChanged on " + localUpdateRecord.mReceiver);
              paramString = (String)localObject;
              i = j;
            }
            else
            {
              paramString = (String)localObject;
              i = j;
              if (D)
              {
                Log.d("LocationManagerService", "Sending location for screening");
                paramString = (String)localObject;
                i = j;
              }
            }
          }
        }
      }
      if (j != 0) {
        return null;
      }
    } while (!D);
    Log.d("LocationManagerService", "Not screening locations");
    return paramLocation;
  }
  
  private static boolean shouldBroadcastSafe(Location paramLocation1, Location paramLocation2, UpdateRecord paramUpdateRecord, long paramLong)
  {
    if (paramLocation2 == null) {
      return true;
    }
    long l = paramUpdateRecord.mRequest.getFastestInterval();
    if ((paramLocation1.getElapsedRealtimeNanos() - paramLocation2.getElapsedRealtimeNanos()) / 1000000L < l - 100L) {
      return false;
    }
    double d = paramUpdateRecord.mRequest.getSmallestDisplacement();
    if ((d > 0.0D) && (paramLocation1.distanceTo(paramLocation2) <= d)) {
      return false;
    }
    if (paramUpdateRecord.mRequest.getNumUpdates() <= 0) {
      return false;
    }
    return paramUpdateRecord.mRequest.getExpireAt() >= paramLong;
  }
  
  private void shutdownComponents()
  {
    if (D) {
      Log.d("LocationManagerService", "Shutting down components...");
    }
    LocationProviderInterface localLocationProviderInterface = (LocationProviderInterface)this.mProvidersByName.get("gps");
    if ((localLocationProviderInterface != null) && (localLocationProviderInterface.isEnabled())) {
      localLocationProviderInterface.disable();
    }
    if (FlpHardwareProvider.isSupported()) {
      FlpHardwareProvider.getInstance(this.mContext).cleanup();
    }
  }
  
  private void switchUser(int paramInt)
  {
    if (this.mCurrentUserId == paramInt) {
      return;
    }
    this.mBlacklist.switchUser(paramInt);
    this.mLocationHandler.removeMessages(1);
    synchronized (this.mLock)
    {
      this.mLastLocation.clear();
      this.mLastLocationCoarseInterval.clear();
      Iterator localIterator = this.mProviders.iterator();
      if (localIterator.hasNext()) {
        updateProviderListenersLocked(((LocationProviderInterface)localIterator.next()).getName(), false);
      }
    }
    this.mCurrentUserId = paramInt;
    updateUserProfiles(paramInt);
    updateProvidersLocked();
  }
  
  public static void updateLocationReceiver(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    OnePlusProcessManager.updateLocationReceiverUidsChange(paramInt, paramBoolean1, paramBoolean2, false);
  }
  
  public static void updateLocationReceiver(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    OnePlusProcessManager.updateLocationReceiverUidsChange(paramInt, paramBoolean1, paramBoolean2, paramBoolean3);
  }
  
  private void updateProviderListenersLocked(String paramString, boolean paramBoolean)
  {
    int j = 0;
    int i = 0;
    Log.d("LocationManagerService", "updateProviderListenersLocked provider " + paramString + " enabled " + paramBoolean);
    LocationProviderInterface localLocationProviderInterface = (LocationProviderInterface)this.mProvidersByName.get(paramString);
    if (localLocationProviderInterface == null) {
      return;
    }
    Object localObject2 = null;
    Object localObject1 = null;
    ArrayList localArrayList = (ArrayList)this.mRecordsByProvider.get(paramString);
    if (localArrayList != null)
    {
      int m = localArrayList.size();
      int k = 0;
      for (;;)
      {
        localObject2 = localObject1;
        j = i;
        if (k >= m) {
          break;
        }
        UpdateRecord localUpdateRecord = (UpdateRecord)localArrayList.get(k);
        localObject2 = localObject1;
        j = i;
        if (isCurrentProfile(UserHandle.getUserId(localUpdateRecord.mReceiver.mUid)))
        {
          localObject2 = localObject1;
          if (!localUpdateRecord.mReceiver.callProviderEnabledLocked(paramString, paramBoolean))
          {
            localObject2 = localObject1;
            if (localObject1 == null) {
              localObject2 = new ArrayList();
            }
            ((ArrayList)localObject2).add(localUpdateRecord.mReceiver);
          }
          j = i + 1;
        }
        k += 1;
        localObject1 = localObject2;
        i = j;
      }
    }
    if (localObject2 != null)
    {
      i = ((ArrayList)localObject2).size() - 1;
      while (i >= 0)
      {
        removeUpdatesLocked((Receiver)((ArrayList)localObject2).get(i));
        i -= 1;
      }
    }
    if (paramBoolean)
    {
      localLocationProviderInterface.enable();
      if (j > 0) {
        applyRequirementsLocked(paramString);
      }
      return;
    }
    localLocationProviderInterface.disable();
  }
  
  private void updateProvidersLocked()
  {
    int k = 0;
    int j = this.mProviders.size() - 1;
    if (j >= 0)
    {
      Object localObject = (LocationProviderInterface)this.mProviders.get(j);
      boolean bool1 = ((LocationProviderInterface)localObject).isEnabled();
      localObject = ((LocationProviderInterface)localObject).getName();
      boolean bool2 = isAllowedByCurrentUserSettingsLocked((String)localObject);
      if ((!bool1) || (bool2))
      {
        i = k;
        if (!bool1)
        {
          i = k;
          if (bool2) {
            updateProviderListenersLocked((String)localObject, true);
          }
        }
      }
      for (int i = 1;; i = 1)
      {
        j -= 1;
        k = i;
        break;
        updateProviderListenersLocked((String)localObject, false);
        this.mLastLocation.clear();
        this.mLastLocationCoarseInterval.clear();
      }
    }
    if (k != 0)
    {
      this.mContext.sendBroadcastAsUser(new Intent("android.location.PROVIDERS_CHANGED"), UserHandle.ALL);
      this.mContext.sendBroadcastAsUser(new Intent("android.location.MODE_CHANGED"), UserHandle.ALL);
    }
  }
  
  public static void updateUidBlock(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      mBlockLocationUids.remove(Integer.valueOf(paramInt));
      mBlockLocationUids.add(Integer.valueOf(paramInt));
      return;
    }
    mBlockLocationUids.remove(Integer.valueOf(paramInt));
  }
  
  public boolean addGnssMeasurementsListener(IGnssMeasurementsListener paramIGnssMeasurementsListener, String paramString)
  {
    int i = getCallerAllowedResolutionLevel();
    checkResolutionLevelIsSufficientForProviderUse(i, "gps");
    int j = Binder.getCallingPid();
    int k = Binder.getCallingUid();
    long l = Binder.clearCallingIdentity();
    try
    {
      boolean bool = checkLocationAccess(j, k, paramString, i);
      Binder.restoreCallingIdentity(l);
      if ((!bool) || (this.mGnssMeasurementsProvider == null)) {
        return false;
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    return this.mGnssMeasurementsProvider.addListener(paramIGnssMeasurementsListener);
  }
  
  public boolean addGnssNavigationMessageListener(IGnssNavigationMessageListener paramIGnssNavigationMessageListener, String paramString)
  {
    int i = getCallerAllowedResolutionLevel();
    checkResolutionLevelIsSufficientForProviderUse(i, "gps");
    int j = Binder.getCallingPid();
    int k = Binder.getCallingUid();
    long l = Binder.clearCallingIdentity();
    try
    {
      boolean bool = checkLocationAccess(j, k, paramString, i);
      Binder.restoreCallingIdentity(l);
      if ((!bool) || (this.mGnssNavigationMessageProvider == null)) {
        return false;
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    return this.mGnssNavigationMessageProvider.addListener(paramIGnssNavigationMessageListener);
  }
  
  public void addTestProvider(String paramString1, ProviderProperties paramProviderProperties, String arg3)
  {
    if (!canCallerAccessMockLocation(???)) {
      return;
    }
    if ("passive".equals(paramString1)) {
      throw new IllegalArgumentException("Cannot mock the passive location provider");
    }
    long l = Binder.clearCallingIdentity();
    synchronized (this.mLock)
    {
      if (("gps".equals(paramString1)) || ("network".equals(paramString1)) || ("fused".equals(paramString1)))
      {
        LocationProviderInterface localLocationProviderInterface = (LocationProviderInterface)this.mProvidersByName.get(paramString1);
        if (localLocationProviderInterface != null) {
          removeProviderLocked(localLocationProviderInterface);
        }
      }
      addTestProviderLocked(paramString1, paramProviderProperties);
      updateProvidersLocked();
      Binder.restoreCallingIdentity(l);
      return;
    }
  }
  
  boolean checkLocationAccess(int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    int i = resolutionLevelToOp(paramInt3);
    if ((i >= 0) && (this.mAppOps.checkOp(i, paramInt2, paramString) != 0)) {
      return false;
    }
    return getAllowedResolutionLevel(paramInt1, paramInt2) >= paramInt3;
  }
  
  public void clearAllPendingBroadcastsLocked()
  {
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mReceivers.values().iterator();
      if (localIterator.hasNext()) {
        ((Receiver)localIterator.next()).clearPendingBroadcastsLocked();
      }
    }
  }
  
  public void clearTestProviderEnabled(String paramString1, String arg2)
  {
    if (!canCallerAccessMockLocation(???)) {
      return;
    }
    synchronized (this.mLock)
    {
      if ((MockProvider)this.mMockProviders.get(paramString1) == null) {
        throw new IllegalArgumentException("Provider \"" + paramString1 + "\" unknown");
      }
    }
    long l = Binder.clearCallingIdentity();
    this.mEnabledProviders.remove(paramString1);
    this.mDisabledProviders.remove(paramString1);
    updateProvidersLocked();
    Binder.restoreCallingIdentity(l);
  }
  
  public void clearTestProviderLocation(String paramString1, String arg2)
  {
    if (!canCallerAccessMockLocation(???)) {
      return;
    }
    MockProvider localMockProvider;
    synchronized (this.mLock)
    {
      localMockProvider = (MockProvider)this.mMockProviders.get(paramString1);
      if (localMockProvider == null) {
        throw new IllegalArgumentException("Provider \"" + paramString1 + "\" unknown");
      }
    }
    localMockProvider.clearLocation();
  }
  
  public void clearTestProviderStatus(String paramString1, String arg2)
  {
    if (!canCallerAccessMockLocation(???)) {
      return;
    }
    MockProvider localMockProvider;
    synchronized (this.mLock)
    {
      localMockProvider = (MockProvider)this.mMockProviders.get(paramString1);
      if (localMockProvider == null) {
        throw new IllegalArgumentException("Provider \"" + paramString1 + "\" unknown");
      }
    }
    localMockProvider.clearStatus();
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump LocationManagerService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    Object localObject2;
    synchronized (this.mLock)
    {
      paramPrintWriter.println("Current Location Manager state:");
      paramPrintWriter.println("  Location Listeners:");
      localIterator = this.mReceivers.values().iterator();
      if (localIterator.hasNext())
      {
        localObject2 = (Receiver)localIterator.next();
        paramPrintWriter.println("    " + localObject2);
      }
    }
    paramPrintWriter.println("  Active Records by Provider:");
    Iterator localIterator = this.mRecordsByProvider.entrySet().iterator();
    Object localObject3;
    while (localIterator.hasNext())
    {
      localObject2 = (Map.Entry)localIterator.next();
      paramPrintWriter.println("    " + (String)((Map.Entry)localObject2).getKey() + ":");
      localObject2 = ((ArrayList)((Map.Entry)localObject2).getValue()).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (UpdateRecord)((Iterator)localObject2).next();
        paramPrintWriter.println("      " + localObject3);
      }
    }
    paramPrintWriter.println("  Historical Records by Provider:");
    localIterator = this.mRequestStatistics.statistics.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject3 = (Map.Entry)localIterator.next();
      localObject2 = (LocationRequestStatistics.PackageProviderKey)((Map.Entry)localObject3).getKey();
      localObject3 = (LocationRequestStatistics.PackageStatistics)((Map.Entry)localObject3).getValue();
      paramPrintWriter.println("    " + ((LocationRequestStatistics.PackageProviderKey)localObject2).packageName + ": " + ((LocationRequestStatistics.PackageProviderKey)localObject2).providerName + ": " + localObject3);
    }
    paramPrintWriter.println("  Last Known Locations:");
    localIterator = this.mLastLocation.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject3 = (Map.Entry)localIterator.next();
      localObject2 = (String)((Map.Entry)localObject3).getKey();
      localObject3 = (Location)((Map.Entry)localObject3).getValue();
      paramPrintWriter.println("    " + (String)localObject2 + ": " + localObject3);
    }
    paramPrintWriter.println("  Last Known Locations Coarse Intervals:");
    localIterator = this.mLastLocationCoarseInterval.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject3 = (Map.Entry)localIterator.next();
      localObject2 = (String)((Map.Entry)localObject3).getKey();
      localObject3 = (Location)((Map.Entry)localObject3).getValue();
      paramPrintWriter.println("    " + (String)localObject2 + ": " + localObject3);
    }
    this.mGeofenceManager.dump(paramPrintWriter);
    if (this.mEnabledProviders.size() > 0)
    {
      paramPrintWriter.println("  Enabled Providers:");
      localIterator = this.mEnabledProviders.iterator();
      while (localIterator.hasNext())
      {
        localObject2 = (String)localIterator.next();
        paramPrintWriter.println("    " + (String)localObject2);
      }
    }
    if (this.mDisabledProviders.size() > 0)
    {
      paramPrintWriter.println("  Disabled Providers:");
      localIterator = this.mDisabledProviders.iterator();
      while (localIterator.hasNext())
      {
        localObject2 = (String)localIterator.next();
        paramPrintWriter.println("    " + (String)localObject2);
      }
    }
    paramPrintWriter.append("  ");
    this.mBlacklist.dump(paramPrintWriter);
    if (this.mMockProviders.size() > 0)
    {
      paramPrintWriter.println("  Mock Providers:");
      localIterator = this.mMockProviders.entrySet().iterator();
      while (localIterator.hasNext()) {
        ((MockProvider)((Map.Entry)localIterator.next()).getValue()).dump(paramPrintWriter, "      ");
      }
    }
    paramPrintWriter.append("  fudger: ");
    this.mLocationFudger.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    if (paramArrayOfString.length > 0)
    {
      boolean bool = "short".equals(paramArrayOfString[0]);
      if (bool) {
        return;
      }
    }
    localIterator = this.mProviders.iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (LocationProviderInterface)localIterator.next();
      paramPrintWriter.print(((LocationProviderInterface)localObject2).getName() + " Internal State");
      if ((localObject2 instanceof LocationProviderProxy))
      {
        localObject3 = (LocationProviderProxy)localObject2;
        paramPrintWriter.print(" (" + ((LocationProviderProxy)localObject3).getConnectedPackageName() + ")");
      }
      paramPrintWriter.println(":");
      ((LocationProviderInterface)localObject2).dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  public boolean geocoderIsPresent()
  {
    return this.mGeocodeProvider != null;
  }
  
  public ArrayMap<Integer, String> getActiveLocationUidType()
  {
    synchronized (this.mLock)
    {
      ArrayMap localArrayMap = new ArrayMap();
      Iterator localIterator1 = this.mReceivers.values().iterator();
      Object localObject2;
      int i;
      do
      {
        do
        {
          Iterator localIterator2;
          while (!localIterator2.hasNext())
          {
            if (!localIterator1.hasNext()) {
              break;
            }
            localIterator2 = ((Receiver)localIterator1.next()).mUpdateRecords.entrySet().iterator();
          }
          localObject2 = (Map.Entry)localIterator2.next();
        } while ((localObject2 == null) || (((Map.Entry)localObject2).getValue() == null));
        i = ((UpdateRecord)((Map.Entry)localObject2).getValue()).mReceiver.mUid;
        localObject2 = ((UpdateRecord)((Map.Entry)localObject2).getValue()).mProvider;
      } while ((mBlockLocationUids.contains(Integer.valueOf(i))) || (localObject2 == null) || (!((String)localObject2).equals("gps")));
      localArrayMap.put(Integer.valueOf(i), localObject2);
    }
    return localArrayMap1;
  }
  
  public List<String> getAllProviders()
  {
    synchronized (this.mLock)
    {
      ArrayList localArrayList = new ArrayList(this.mProviders.size());
      Iterator localIterator = this.mProviders.iterator();
      while (localIterator.hasNext())
      {
        String str = ((LocationProviderInterface)localIterator.next()).getName();
        if (!"fused".equals(str)) {
          localArrayList.add(str);
        }
      }
    }
    if (D) {
      Log.d("LocationManagerService", "getAllProviders()=" + localObject2);
    }
    return (List<String>)localObject2;
  }
  
  public String getBestProvider(Criteria paramCriteria, boolean paramBoolean)
  {
    Object localObject = getProviders(paramCriteria, paramBoolean);
    if (!((List)localObject).isEmpty())
    {
      localObject = pickBest((List)localObject);
      if (D) {
        Log.d("LocationManagerService", "getBestProvider(" + paramCriteria + ", " + paramBoolean + ")=" + (String)localObject);
      }
      return (String)localObject;
    }
    localObject = getProviders(null, paramBoolean);
    if (!((List)localObject).isEmpty())
    {
      localObject = pickBest((List)localObject);
      if (D) {
        Log.d("LocationManagerService", "getBestProvider(" + paramCriteria + ", " + paramBoolean + ")=" + (String)localObject);
      }
      return (String)localObject;
    }
    if (D) {
      Log.d("LocationManagerService", "getBestProvider(" + paramCriteria + ", " + paramBoolean + ")=" + null);
    }
    return null;
  }
  
  public List<String> getCurrentProviderPackageList(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    ArrayList localArrayList;
    synchronized (this.mLock)
    {
      localArrayList = new ArrayList();
      boolean bool = ((LocationProviderInterface)this.mProvidersByName.get(paramString)).isEnabled();
      if (!bool) {
        return localArrayList;
      }
      Iterator localIterator1 = this.mReceivers.values().iterator();
      Object localObject2;
      do
      {
        do
        {
          Iterator localIterator2;
          while (!localIterator2.hasNext())
          {
            if (!localIterator1.hasNext()) {
              break;
            }
            localIterator2 = ((Receiver)localIterator1.next()).mUpdateRecords.entrySet().iterator();
          }
          localObject2 = (Map.Entry)localIterator2.next();
        } while (localObject2 == null);
        localObject2 = (UpdateRecord)((Map.Entry)localObject2).getValue();
      } while ((localObject2 == null) || (!UserHandle.isApp(((UpdateRecord)localObject2).mReceiver.mUid)) || (UserHandle.getUserId(((UpdateRecord)localObject2).mReceiver.mUid) != ActivityManager.getCurrentUser()) || (((UpdateRecord)localObject2).mRequest.getInterval() > 300000L) || (!((UpdateRecord)localObject2).mProvider.equals(paramString)) || (localArrayList.contains(((UpdateRecord)localObject2).mReceiver.mPackageName)) || (mBlockLocationUids.contains(Integer.valueOf(((UpdateRecord)localObject2).mReceiver.mUid))));
      localArrayList.add(((UpdateRecord)localObject2).mReceiver.mPackageName);
    }
    return localArrayList;
  }
  
  public String getFromLocation(double paramDouble1, double paramDouble2, int paramInt, GeocoderParams paramGeocoderParams, List<Address> paramList)
  {
    if (this.mGeocodeProvider != null) {
      return this.mGeocodeProvider.getFromLocation(paramDouble1, paramDouble2, paramInt, paramGeocoderParams, paramList);
    }
    return null;
  }
  
  public String getFromLocationName(String paramString, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt, GeocoderParams paramGeocoderParams, List<Address> paramList)
  {
    if (this.mGeocodeProvider != null) {
      return this.mGeocodeProvider.getFromLocationName(paramString, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramInt, paramGeocoderParams, paramList);
    }
    return null;
  }
  
  public int getGnssYearOfHardware()
  {
    if (this.mGnssNavigationMessageProvider != null) {
      return this.mGnssSystemInfoProvider.getGnssYearOfHardware();
    }
    return 0;
  }
  
  public Location getLastKnownLocation()
  {
    if (D) {
      Log.d("LocationManagerService", "getLastKnownLocation");
    }
    Location localLocation2 = sLastKnownLocation;
    Location localLocation1 = localLocation2;
    if (localLocation2 == null)
    {
      localLocation2 = sLastKnownCoarseIntervalLocation;
      localLocation1 = localLocation2;
      if (localLocation2 == null) {
        return null;
      }
    }
    return localLocation1;
  }
  
  /* Error */
  public Location getLastLocation(LocationRequest paramLocationRequest, String paramString)
  {
    // Byte code:
    //   0: getstatic 257	com/android/server/LocationManagerService:D	Z
    //   3: ifeq +29 -> 32
    //   6: ldc 68
    //   8: new 377	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 378	java/lang/StringBuilder:<init>	()V
    //   15: ldc_w 1576
    //   18: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   21: aload_1
    //   22: invokevirtual 442	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   25: invokevirtual 393	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   28: invokestatic 345	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   31: pop
    //   32: aload_1
    //   33: astore 9
    //   35: aload_1
    //   36: ifnonnull +8 -> 44
    //   39: getstatic 269	com/android/server/LocationManagerService:DEFAULT_LOCATION_REQUEST	Landroid/location/LocationRequest;
    //   42: astore 9
    //   44: aload_0
    //   45: invokespecial 1369	com/android/server/LocationManagerService:getCallerAllowedResolutionLevel	()I
    //   48: istore_3
    //   49: aload_0
    //   50: aload_2
    //   51: invokespecial 1578	com/android/server/LocationManagerService:checkPackageName	(Ljava/lang/String;)V
    //   54: aload_0
    //   55: iload_3
    //   56: aload 9
    //   58: invokevirtual 1259	android/location/LocationRequest:getProvider	()Ljava/lang/String;
    //   61: invokespecial 1371	com/android/server/LocationManagerService:checkResolutionLevelIsSufficientForProviderUse	(ILjava/lang/String;)V
    //   64: invokestatic 790	android/os/Binder:getCallingPid	()I
    //   67: istore 4
    //   69: invokestatic 562	android/os/Binder:getCallingUid	()I
    //   72: istore 5
    //   74: invokestatic 1374	android/os/Binder:clearCallingIdentity	()J
    //   77: lstore 6
    //   79: aload_0
    //   80: getfield 909	com/android/server/LocationManagerService:mBlacklist	Lcom/android/server/location/LocationBlacklist;
    //   83: aload_2
    //   84: invokevirtual 914	com/android/server/location/LocationBlacklist:isBlacklisted	(Ljava/lang/String;)Z
    //   87: ifeq +42 -> 129
    //   90: getstatic 257	com/android/server/LocationManagerService:D	Z
    //   93: ifeq +29 -> 122
    //   96: ldc 68
    //   98: new 377	java/lang/StringBuilder
    //   101: dup
    //   102: invokespecial 378	java/lang/StringBuilder:<init>	()V
    //   105: ldc_w 1580
    //   108: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   111: aload_2
    //   112: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   115: invokevirtual 393	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   118: invokestatic 345	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   121: pop
    //   122: lload 6
    //   124: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   127: aconst_null
    //   128: areturn
    //   129: aload_0
    //   130: iload 4
    //   132: iload 5
    //   134: aload_2
    //   135: iload_3
    //   136: invokevirtual 931	com/android/server/LocationManagerService:reportLocationAccessNoThrow	(IILjava/lang/String;I)Z
    //   139: ifne +42 -> 181
    //   142: getstatic 257	com/android/server/LocationManagerService:D	Z
    //   145: ifeq +29 -> 174
    //   148: ldc 68
    //   150: new 377	java/lang/StringBuilder
    //   153: dup
    //   154: invokespecial 378	java/lang/StringBuilder:<init>	()V
    //   157: ldc_w 1582
    //   160: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   163: aload_2
    //   164: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   167: invokevirtual 393	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   170: invokestatic 345	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   173: pop
    //   174: lload 6
    //   176: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   179: aconst_null
    //   180: areturn
    //   181: aload_0
    //   182: getfield 178	com/android/server/LocationManagerService:mLock	Ljava/lang/Object;
    //   185: astore 10
    //   187: aload 10
    //   189: monitorenter
    //   190: aload 9
    //   192: invokevirtual 1259	android/location/LocationRequest:getProvider	()Ljava/lang/String;
    //   195: astore_2
    //   196: aload_2
    //   197: astore_1
    //   198: aload_2
    //   199: ifnonnull +7 -> 206
    //   202: ldc_w 367
    //   205: astore_1
    //   206: aload_0
    //   207: getfield 186	com/android/server/LocationManagerService:mProvidersByName	Ljava/util/HashMap;
    //   210: aload_1
    //   211: invokevirtual 371	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   214: checkcast 456	com/android/server/location/LocationProviderInterface
    //   217: astore_2
    //   218: aload_2
    //   219: ifnonnull +13 -> 232
    //   222: aload 10
    //   224: monitorexit
    //   225: lload 6
    //   227: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   230: aconst_null
    //   231: areturn
    //   232: aload_0
    //   233: aload_1
    //   234: iload 5
    //   236: invokespecial 1274	com/android/server/LocationManagerService:isAllowedByUserSettingsLocked	(Ljava/lang/String;I)Z
    //   239: istore 8
    //   241: iload 8
    //   243: ifne +13 -> 256
    //   246: aload 10
    //   248: monitorexit
    //   249: lload 6
    //   251: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   254: aconst_null
    //   255: areturn
    //   256: iload_3
    //   257: iconst_2
    //   258: if_icmpge +29 -> 287
    //   261: aload_0
    //   262: getfield 308	com/android/server/LocationManagerService:mLastLocationCoarseInterval	Ljava/util/HashMap;
    //   265: aload_1
    //   266: invokevirtual 371	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   269: checkcast 835	android/location/Location
    //   272: astore_1
    //   273: aload_1
    //   274: ifnonnull +28 -> 302
    //   277: aload 10
    //   279: monitorexit
    //   280: lload 6
    //   282: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   285: aconst_null
    //   286: areturn
    //   287: aload_0
    //   288: getfield 306	com/android/server/LocationManagerService:mLastLocation	Ljava/util/HashMap;
    //   291: aload_1
    //   292: invokevirtual 371	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   295: checkcast 835	android/location/Location
    //   298: astore_1
    //   299: goto -26 -> 273
    //   302: iload_3
    //   303: iconst_2
    //   304: if_icmpge +41 -> 345
    //   307: aload_1
    //   308: ldc_w 874
    //   311: invokevirtual 878	android/location/Location:getExtraLocation	(Ljava/lang/String;)Landroid/location/Location;
    //   314: astore_1
    //   315: aload_1
    //   316: ifnull +48 -> 364
    //   319: new 835	android/location/Location
    //   322: dup
    //   323: aload_0
    //   324: getfield 893	com/android/server/LocationManagerService:mLocationFudger	Lcom/android/server/location/LocationFudger;
    //   327: aload_1
    //   328: invokevirtual 899	com/android/server/location/LocationFudger:getOrCreate	(Landroid/location/Location;)Landroid/location/Location;
    //   331: invokespecial 838	android/location/Location:<init>	(Landroid/location/Location;)V
    //   334: astore_1
    //   335: aload 10
    //   337: monitorexit
    //   338: lload 6
    //   340: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   343: aload_1
    //   344: areturn
    //   345: new 835	android/location/Location
    //   348: dup
    //   349: aload_1
    //   350: invokespecial 838	android/location/Location:<init>	(Landroid/location/Location;)V
    //   353: astore_1
    //   354: aload 10
    //   356: monitorexit
    //   357: lload 6
    //   359: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   362: aload_1
    //   363: areturn
    //   364: aload 10
    //   366: monitorexit
    //   367: lload 6
    //   369: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   372: aconst_null
    //   373: areturn
    //   374: astore_1
    //   375: aload 10
    //   377: monitorexit
    //   378: aload_1
    //   379: athrow
    //   380: astore_1
    //   381: lload 6
    //   383: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   386: aload_1
    //   387: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	388	0	this	LocationManagerService
    //   0	388	1	paramLocationRequest	LocationRequest
    //   0	388	2	paramString	String
    //   48	257	3	i	int
    //   67	64	4	j	int
    //   72	163	5	k	int
    //   77	305	6	l	long
    //   239	3	8	bool	boolean
    //   33	158	9	localLocationRequest	LocationRequest
    // Exception table:
    //   from	to	target	type
    //   190	196	374	finally
    //   206	218	374	finally
    //   232	241	374	finally
    //   261	273	374	finally
    //   287	299	374	finally
    //   307	315	374	finally
    //   319	335	374	finally
    //   345	354	374	finally
    //   79	122	380	finally
    //   129	174	380	finally
    //   181	190	380	finally
    //   222	225	380	finally
    //   246	249	380	finally
    //   277	280	380	finally
    //   335	338	380	finally
    //   354	357	380	finally
    //   364	367	380	finally
    //   375	380	380	finally
  }
  
  public int[] getLocationListenersUid()
  {
    synchronized (this.mLock)
    {
      HashMap localHashMap = new HashMap();
      localObject3 = this.mReceivers.values().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        Iterator localIterator2 = ((Receiver)((Iterator)localObject3).next()).mUpdateRecords.entrySet().iterator();
        if (localIterator2.hasNext()) {
          localHashMap.put(Integer.valueOf(((UpdateRecord)((Map.Entry)localIterator2.next()).getValue()).mReceiver.mUid), Integer.valueOf(1));
        }
      }
    }
    int i = ((HashMap)localObject2).size();
    if (i <= 0) {
      return null;
    }
    Object localObject3 = new int[i];
    Iterator localIterator1 = ((HashMap)localObject2).keySet().iterator();
    i = 0;
    while (localIterator1.hasNext())
    {
      localObject3[i] = ((Integer)localIterator1.next()).intValue();
      i += 1;
    }
    return (int[])localObject3;
  }
  
  public String getNetworkProviderPackage()
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mProvidersByName.get("network");
      if (localObject2 == null) {
        return null;
      }
      localObject2 = (LocationProviderInterface)this.mProvidersByName.get("network");
      if ((localObject2 instanceof LocationProviderProxy)) {
        return ((LocationProviderProxy)localObject2).getConnectedPackageName();
      }
    }
    return null;
  }
  
  public ProviderProperties getProviderProperties(String paramString)
  {
    if (this.mProvidersByName.get(paramString) == null) {
      return null;
    }
    checkResolutionLevelIsSufficientForProviderUse(getCallerAllowedResolutionLevel(), paramString);
    synchronized (this.mLock)
    {
      paramString = (LocationProviderInterface)this.mProvidersByName.get(paramString);
      if (paramString == null) {
        return null;
      }
    }
    return paramString.getProperties();
  }
  
  public List<String> getProviders(Criteria paramCriteria, boolean paramBoolean)
  {
    int i = getCallerAllowedResolutionLevel();
    int j = Binder.getCallingUid();
    long l = Binder.clearCallingIdentity();
    ArrayList localArrayList;
    try
    {
      synchronized (this.mLock)
      {
        localArrayList = new ArrayList(this.mProviders.size());
        Iterator localIterator = this.mProviders.iterator();
        while (localIterator.hasNext())
        {
          LocationProviderInterface localLocationProviderInterface = (LocationProviderInterface)localIterator.next();
          String str = localLocationProviderInterface.getName();
          if ((!"fused".equals(str)) && (i >= getMinimumResolutionLevelForProviderUse(str)) && ((!paramBoolean) || (isAllowedByUserSettingsLocked(str, j))) && ((paramCriteria == null) || (LocationProvider.propertiesMeetCriteria(str, localLocationProviderInterface.getProperties(), paramCriteria)))) {
            localArrayList.add(str);
          }
        }
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    Binder.restoreCallingIdentity(l);
    if (D) {
      Log.d("LocationManagerService", "getProviders()=" + localArrayList);
    }
    return localArrayList;
  }
  
  /* Error */
  public boolean isProviderEnabled(String paramString)
  {
    // Byte code:
    //   0: ldc_w 367
    //   3: aload_1
    //   4: invokevirtual 621	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   7: ifeq +5 -> 12
    //   10: iconst_0
    //   11: ireturn
    //   12: invokestatic 562	android/os/Binder:getCallingUid	()I
    //   15: istore_2
    //   16: invokestatic 1374	android/os/Binder:clearCallingIdentity	()J
    //   19: lstore_3
    //   20: aload_0
    //   21: getfield 178	com/android/server/LocationManagerService:mLock	Ljava/lang/Object;
    //   24: astore 6
    //   26: aload 6
    //   28: monitorenter
    //   29: aload_0
    //   30: getfield 186	com/android/server/LocationManagerService:mProvidersByName	Ljava/util/HashMap;
    //   33: aload_1
    //   34: invokevirtual 371	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   37: checkcast 456	com/android/server/location/LocationProviderInterface
    //   40: astore 7
    //   42: aload 7
    //   44: ifnonnull +12 -> 56
    //   47: aload 6
    //   49: monitorexit
    //   50: lload_3
    //   51: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   54: iconst_0
    //   55: ireturn
    //   56: aload_0
    //   57: aload_1
    //   58: iload_2
    //   59: invokespecial 1274	com/android/server/LocationManagerService:isAllowedByUserSettingsLocked	(Ljava/lang/String;I)Z
    //   62: istore 5
    //   64: aload 6
    //   66: monitorexit
    //   67: lload_3
    //   68: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   71: iload 5
    //   73: ireturn
    //   74: astore_1
    //   75: aload 6
    //   77: monitorexit
    //   78: aload_1
    //   79: athrow
    //   80: astore_1
    //   81: lload_3
    //   82: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   85: aload_1
    //   86: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	87	0	this	LocationManagerService
    //   0	87	1	paramString	String
    //   15	44	2	i	int
    //   19	63	3	l	long
    //   62	10	5	bool	boolean
    //   40	3	7	localLocationProviderInterface	LocationProviderInterface
    // Exception table:
    //   from	to	target	type
    //   29	42	74	finally
    //   56	64	74	finally
    //   20	29	80	finally
    //   47	50	80	finally
    //   64	67	80	finally
    //   75	80	80	finally
  }
  
  public void locationCallbackFinished(ILocationListener paramILocationListener)
  {
    synchronized (this.mLock)
    {
      paramILocationListener = paramILocationListener.asBinder();
      paramILocationListener = (Receiver)this.mReceivers.get(paramILocationListener);
      if (paramILocationListener != null) {}
      try
      {
        long l = Binder.clearCallingIdentity();
        Receiver.-wrap0(paramILocationListener);
        Binder.restoreCallingIdentity(l);
        return;
      }
      finally
      {
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
    }
  }
  
  public boolean providerMeetsCriteria(String paramString, Criteria paramCriteria)
  {
    LocationProviderInterface localLocationProviderInterface = (LocationProviderInterface)this.mProvidersByName.get(paramString);
    if (localLocationProviderInterface == null) {
      throw new IllegalArgumentException("provider=" + paramString);
    }
    boolean bool = LocationProvider.propertiesMeetCriteria(localLocationProviderInterface.getName(), localLocationProviderInterface.getProperties(), paramCriteria);
    if (D) {
      Log.d("LocationManagerService", "providerMeetsCriteria(" + paramString + ", " + paramCriteria + ")=" + bool);
    }
    return bool;
  }
  
  public boolean registerGnssStatusCallback(IGnssStatusListener paramIGnssStatusListener, String paramString)
  {
    int i = getCallerAllowedResolutionLevel();
    checkResolutionLevelIsSufficientForProviderUse(i, "gps");
    int j = Binder.getCallingPid();
    int k = Binder.getCallingUid();
    long l = Binder.clearCallingIdentity();
    try
    {
      boolean bool = checkLocationAccess(j, k, paramString, i);
      if (!bool) {
        return false;
      }
      Binder.restoreCallingIdentity(l);
      if (this.mGnssStatusProvider == null) {
        return false;
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    try
    {
      this.mGnssStatusProvider.registerGnssStatusCallback(paramIGnssStatusListener);
      return true;
    }
    catch (RemoteException paramIGnssStatusListener)
    {
      Slog.e("LocationManagerService", "mGpsStatusProvider.registerGnssStatusCallback failed", paramIGnssStatusListener);
    }
    return false;
  }
  
  public void removeGeofence(Geofence paramGeofence, PendingIntent paramPendingIntent, String paramString)
  {
    checkPendingIntent(paramPendingIntent);
    checkPackageName(paramString);
    if (D) {
      Log.d("LocationManagerService", "removeGeofence: " + paramGeofence + " " + paramPendingIntent);
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mGeofenceManager.removeFence(paramGeofence, paramPendingIntent);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void removeGnssMeasurementsListener(IGnssMeasurementsListener paramIGnssMeasurementsListener)
  {
    if (this.mGnssMeasurementsProvider != null) {
      this.mGnssMeasurementsProvider.removeListener(paramIGnssMeasurementsListener);
    }
  }
  
  public void removeGnssNavigationMessageListener(IGnssNavigationMessageListener paramIGnssNavigationMessageListener)
  {
    if (this.mGnssNavigationMessageProvider != null) {
      this.mGnssNavigationMessageProvider.removeListener(paramIGnssNavigationMessageListener);
    }
  }
  
  public void removeTestProvider(String paramString1, String paramString2)
  {
    if (!canCallerAccessMockLocation(paramString2)) {
      return;
    }
    synchronized (this.mLock)
    {
      clearTestProviderEnabled(paramString1, paramString2);
      clearTestProviderLocation(paramString1, paramString2);
      clearTestProviderStatus(paramString1, paramString2);
      if ((MockProvider)this.mMockProviders.remove(paramString1) == null) {
        throw new IllegalArgumentException("Provider \"" + paramString1 + "\" unknown");
      }
    }
    long l = Binder.clearCallingIdentity();
    removeProviderLocked((LocationProviderInterface)this.mProvidersByName.get(paramString1));
    paramString2 = (LocationProviderInterface)this.mRealProviders.get(paramString1);
    if (paramString2 != null) {
      addProviderLocked(paramString2);
    }
    this.mLastLocation.put(paramString1, null);
    this.mLastLocationCoarseInterval.put(paramString1, null);
    updateProvidersLocked();
    Binder.restoreCallingIdentity(l);
  }
  
  public void removeUpdates(ILocationListener paramILocationListener, PendingIntent paramPendingIntent, String paramString)
  {
    checkPackageName(paramString);
    int i = Binder.getCallingPid();
    int j = Binder.getCallingUid();
    synchronized (this.mLock)
    {
      Receiver localReceiver = checkListenerOrIntentLocked(paramILocationListener, paramPendingIntent, i, j, paramString, null, false);
      Log.e("LocationManagerService", "removeUpdates: uid=" + j + ", pid=" + i + ", intent=" + paramPendingIntent + ", package=" + paramString + " listener " + paramILocationListener + " intent= " + paramPendingIntent);
      long l = Binder.clearCallingIdentity();
      try
      {
        removeUpdatesLocked(localReceiver);
        Binder.restoreCallingIdentity(l);
        return;
      }
      finally
      {
        paramILocationListener = finally;
        Binder.restoreCallingIdentity(l);
        throw paramILocationListener;
      }
    }
  }
  
  public void reportLocation(Location paramLocation, boolean paramBoolean)
  {
    int i = 1;
    checkCallerIsProvider();
    if (!paramLocation.isComplete())
    {
      Log.w("LocationManagerService", "Dropping incomplete location: " + paramLocation);
      return;
    }
    this.mLocationHandler.removeMessages(1, paramLocation);
    paramLocation = Message.obtain(this.mLocationHandler, 1, paramLocation);
    if (paramBoolean) {}
    for (;;)
    {
      paramLocation.arg1 = i;
      this.mLocationHandler.sendMessageAtFrontOfQueue(paramLocation);
      return;
      i = 0;
    }
  }
  
  boolean reportLocationAccessNoThrow(int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    int i = resolutionLevelToOp(paramInt3);
    if ((i >= 0) && (this.mAppOps.noteOpNoThrow(i, paramInt2, paramString) != 0)) {
      return false;
    }
    return getAllowedResolutionLevel(paramInt1, paramInt2) >= paramInt3;
  }
  
  public void requestGeofence(LocationRequest paramLocationRequest, Geofence paramGeofence, PendingIntent paramPendingIntent, String paramString)
  {
    LocationRequest localLocationRequest = paramLocationRequest;
    if (paramLocationRequest == null) {
      localLocationRequest = DEFAULT_LOCATION_REQUEST;
    }
    int i = getCallerAllowedResolutionLevel();
    checkResolutionLevelIsSufficientForGeofenceUse(i);
    checkPendingIntent(paramPendingIntent);
    checkPackageName(paramString);
    checkResolutionLevelIsSufficientForProviderUse(i, localLocationRequest.getProvider());
    paramLocationRequest = createSanitizedRequest(localLocationRequest, i);
    if (D) {
      Log.d("LocationManagerService", "requestGeofence: " + paramLocationRequest + " " + paramGeofence + " " + paramPendingIntent);
    }
    int j = Binder.getCallingUid();
    if (UserHandle.getUserId(j) != 0)
    {
      Log.w("LocationManagerService", "proximity alerts are currently available only to the primary user");
      return;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mGeofenceManager.addFence(paramLocationRequest, paramGeofence, paramPendingIntent, i, j, paramString);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  /* Error */
  public void requestLocationUpdates(LocationRequest paramLocationRequest, ILocationListener paramILocationListener, PendingIntent paramPendingIntent, String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: astore 11
    //   3: aload_1
    //   4: ifnonnull +8 -> 12
    //   7: getstatic 269	com/android/server/LocationManagerService:DEFAULT_LOCATION_REQUEST	Landroid/location/LocationRequest;
    //   10: astore 11
    //   12: aload_0
    //   13: aload 4
    //   15: invokespecial 1578	com/android/server/LocationManagerService:checkPackageName	(Ljava/lang/String;)V
    //   18: aload_0
    //   19: invokespecial 1369	com/android/server/LocationManagerService:getCallerAllowedResolutionLevel	()I
    //   22: istore 5
    //   24: aload_0
    //   25: iload 5
    //   27: aload 11
    //   29: invokevirtual 1259	android/location/LocationRequest:getProvider	()Ljava/lang/String;
    //   32: invokespecial 1371	com/android/server/LocationManagerService:checkResolutionLevelIsSufficientForProviderUse	(ILjava/lang/String;)V
    //   35: aload 11
    //   37: invokevirtual 1705	android/location/LocationRequest:getWorkSource	()Landroid/os/WorkSource;
    //   40: astore_1
    //   41: aload_1
    //   42: ifnull +14 -> 56
    //   45: aload_1
    //   46: invokevirtual 540	android/os/WorkSource:size	()I
    //   49: ifle +7 -> 56
    //   52: aload_0
    //   53: invokespecial 1707	com/android/server/LocationManagerService:checkDeviceStatsAllowed	()V
    //   56: aload 11
    //   58: invokevirtual 1710	android/location/LocationRequest:getHideFromAppOps	()Z
    //   61: istore 8
    //   63: iload 8
    //   65: ifeq +7 -> 72
    //   68: aload_0
    //   69: invokespecial 1712	com/android/server/LocationManagerService:checkUpdateAppOpsAllowed	()V
    //   72: aload_0
    //   73: aload 11
    //   75: iload 5
    //   77: invokespecial 1691	com/android/server/LocationManagerService:createSanitizedRequest	(Landroid/location/LocationRequest;I)Landroid/location/LocationRequest;
    //   80: astore 12
    //   82: invokestatic 790	android/os/Binder:getCallingPid	()I
    //   85: istore 6
    //   87: invokestatic 562	android/os/Binder:getCallingUid	()I
    //   90: istore 7
    //   92: ldc 68
    //   94: new 377	java/lang/StringBuilder
    //   97: dup
    //   98: invokespecial 378	java/lang/StringBuilder:<init>	()V
    //   101: ldc_w 1714
    //   104: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   107: iload 7
    //   109: invokevirtual 389	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   112: ldc_w 1651
    //   115: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   118: iload 6
    //   120: invokevirtual 389	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   123: ldc_w 1716
    //   126: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: aload 11
    //   131: invokevirtual 1259	android/location/LocationRequest:getProvider	()Ljava/lang/String;
    //   134: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   137: ldc_w 1655
    //   140: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   143: aload 4
    //   145: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   148: ldc_w 1718
    //   151: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   154: aload_2
    //   155: invokevirtual 442	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   158: ldc_w 1659
    //   161: invokevirtual 384	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: aload_3
    //   165: invokevirtual 442	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   168: invokevirtual 393	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   171: invokestatic 396	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   174: pop
    //   175: invokestatic 1374	android/os/Binder:clearCallingIdentity	()J
    //   178: lstore 9
    //   180: aload_0
    //   181: iload 6
    //   183: iload 7
    //   185: aload 4
    //   187: iload 5
    //   189: invokevirtual 506	com/android/server/LocationManagerService:checkLocationAccess	(IILjava/lang/String;I)Z
    //   192: pop
    //   193: aload_0
    //   194: getfield 178	com/android/server/LocationManagerService:mLock	Ljava/lang/Object;
    //   197: astore 11
    //   199: aload 11
    //   201: monitorenter
    //   202: aload_0
    //   203: aload 12
    //   205: aload_0
    //   206: aload_2
    //   207: aload_3
    //   208: iload 6
    //   210: iload 7
    //   212: aload 4
    //   214: aload_1
    //   215: iload 8
    //   217: invokespecial 1647	com/android/server/LocationManagerService:checkListenerOrIntentLocked	(Landroid/location/ILocationListener;Landroid/app/PendingIntent;IILjava/lang/String;Landroid/os/WorkSource;Z)Lcom/android/server/LocationManagerService$Receiver;
    //   220: iload 6
    //   222: iload 7
    //   224: aload 4
    //   226: invokespecial 1720	com/android/server/LocationManagerService:requestLocationUpdatesLocked	(Landroid/location/LocationRequest;Lcom/android/server/LocationManagerService$Receiver;IILjava/lang/String;)V
    //   229: aload 11
    //   231: monitorexit
    //   232: lload 9
    //   234: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   237: return
    //   238: astore_1
    //   239: aload 11
    //   241: monitorexit
    //   242: aload_1
    //   243: athrow
    //   244: astore_1
    //   245: lload 9
    //   247: invokestatic 1378	android/os/Binder:restoreCallingIdentity	(J)V
    //   250: aload_1
    //   251: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	252	0	this	LocationManagerService
    //   0	252	1	paramLocationRequest	LocationRequest
    //   0	252	2	paramILocationListener	ILocationListener
    //   0	252	3	paramPendingIntent	PendingIntent
    //   0	252	4	paramString	String
    //   22	166	5	i	int
    //   85	136	6	j	int
    //   90	133	7	k	int
    //   61	155	8	bool	boolean
    //   178	68	9	l	long
    //   80	124	12	localLocationRequest	LocationRequest
    // Exception table:
    //   from	to	target	type
    //   202	229	238	finally
    //   180	202	244	finally
    //   229	232	244	finally
    //   239	244	244	finally
  }
  
  public boolean sendExtraCommand(String paramString1, String paramString2, Bundle paramBundle)
  {
    if (paramString1 == null) {
      throw new NullPointerException();
    }
    checkResolutionLevelIsSufficientForProviderUse(getCallerAllowedResolutionLevel(), paramString1);
    if (this.mContext.checkCallingOrSelfPermission("android.permission.ACCESS_LOCATION_EXTRA_COMMANDS") != 0) {
      throw new SecurityException("Requires ACCESS_LOCATION_EXTRA_COMMANDS permission");
    }
    synchronized (this.mLock)
    {
      paramString1 = (LocationProviderInterface)this.mProvidersByName.get(paramString1);
      if (paramString1 == null) {
        return false;
      }
      boolean bool = paramString1.sendExtraCommand(paramString2, paramBundle);
      return bool;
    }
  }
  
  public boolean sendNiResponse(int paramInt1, int paramInt2)
  {
    if (Binder.getCallingUid() != Process.myUid()) {
      throw new SecurityException("calling sendNiResponse from outside of the system is not allowed");
    }
    try
    {
      boolean bool = this.mNetInitiatedListener.sendNiResponse(paramInt1, paramInt2);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("LocationManagerService", "RemoteException in LocationManagerService.sendNiResponse");
    }
    return false;
  }
  
  public void setTestProviderEnabled(String paramString1, boolean paramBoolean, String arg3)
  {
    if (!canCallerAccessMockLocation(???)) {
      return;
    }
    MockProvider localMockProvider;
    synchronized (this.mLock)
    {
      localMockProvider = (MockProvider)this.mMockProviders.get(paramString1);
      if (localMockProvider == null) {
        throw new IllegalArgumentException("Provider \"" + paramString1 + "\" unknown");
      }
    }
    long l = Binder.clearCallingIdentity();
    if (paramBoolean)
    {
      localMockProvider.enable();
      this.mEnabledProviders.add(paramString1);
      this.mDisabledProviders.remove(paramString1);
    }
    for (;;)
    {
      updateProvidersLocked();
      Binder.restoreCallingIdentity(l);
      return;
      localMockProvider.disable();
      this.mEnabledProviders.remove(paramString1);
      this.mDisabledProviders.add(paramString1);
    }
  }
  
  public void setTestProviderLocation(String paramString1, Location paramLocation, String arg3)
  {
    if (!canCallerAccessMockLocation(???)) {
      return;
    }
    MockProvider localMockProvider;
    synchronized (this.mLock)
    {
      localMockProvider = (MockProvider)this.mMockProviders.get(paramString1);
      if (localMockProvider == null) {
        throw new IllegalArgumentException("Provider \"" + paramString1 + "\" unknown");
      }
    }
    Location localLocation = new Location(paramLocation);
    localLocation.setIsFromMockProvider(true);
    if ((TextUtils.isEmpty(paramLocation.getProvider())) || (paramString1.equals(paramLocation.getProvider()))) {}
    for (;;)
    {
      long l = Binder.clearCallingIdentity();
      localMockProvider.setLocation(localLocation);
      Binder.restoreCallingIdentity(l);
      return;
      EventLog.writeEvent(1397638484, new Object[] { "33091107", Integer.valueOf(Binder.getCallingUid()), paramString1 + "!=" + paramLocation.getProvider() });
    }
  }
  
  public void setTestProviderStatus(String paramString1, int paramInt, Bundle paramBundle, long paramLong, String arg6)
  {
    if (!canCallerAccessMockLocation(???)) {
      return;
    }
    MockProvider localMockProvider;
    synchronized (this.mLock)
    {
      localMockProvider = (MockProvider)this.mMockProviders.get(paramString1);
      if (localMockProvider == null) {
        throw new IllegalArgumentException("Provider \"" + paramString1 + "\" unknown");
      }
    }
    localMockProvider.setStatus(paramInt, paramBundle, paramLong);
  }
  
  public void systemRunning()
  {
    synchronized (this.mLock)
    {
      if (D) {
        Log.d("LocationManagerService", "systemRunning()");
      }
      this.mPackageManager = this.mContext.getPackageManager();
      this.mPowerManager = ((PowerManager)this.mContext.getSystemService("power"));
      this.mLocationThread = new HandlerThread("LocationThread");
      this.mLocationThread.start();
      this.mLocationHandler = new LocationWorkerHandler(this.mLocationThread.getLooper());
      this.mLocationFudger = new LocationFudger(this.mContext, this.mLocationHandler);
      this.mBlacklist = new LocationBlacklist(this.mContext, this.mLocationHandler);
      this.mBlacklist.init();
      this.mGeofenceManager = new GeofenceManager(this.mContext, this.mBlacklist);
      Object localObject2 = new AppOpsManager.OnOpChangedInternalListener()
      {
        public void onOpChanged(int paramAnonymousInt, String arg2)
        {
          synchronized (LocationManagerService.-get5(LocationManagerService.this))
          {
            Iterator localIterator = LocationManagerService.-get8(LocationManagerService.this).values().iterator();
            if (localIterator.hasNext()) {
              ((LocationManagerService.Receiver)localIterator.next()).updateMonitoring(true);
            }
          }
          LocationManagerService.-wrap3(LocationManagerService.this);
        }
      };
      this.mAppOps.startWatchingMode(0, null, (AppOpsManager.OnOpChangedListener)localObject2);
      localObject2 = new PackageManager.OnPermissionsChangedListener()
      {
        public void onPermissionsChanged(int paramAnonymousInt)
        {
          synchronized (LocationManagerService.-get5(LocationManagerService.this))
          {
            LocationManagerService.-wrap3(LocationManagerService.this);
            return;
          }
        }
      };
      this.mPackageManager.addOnPermissionsChangeListener((PackageManager.OnPermissionsChangedListener)localObject2);
      this.mUserManager = ((UserManager)this.mContext.getSystemService("user"));
      updateUserProfiles(this.mCurrentUserId);
      loadProvidersLocked();
      updateProvidersLocked();
      this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("location_providers_allowed"), true, new ContentObserver(this.mLocationHandler)
      {
        public void onChange(boolean paramAnonymousBoolean)
        {
          synchronized (LocationManagerService.-get5(LocationManagerService.this))
          {
            LocationManagerService.-wrap9(LocationManagerService.this);
            return;
          }
        }
      }, -1);
      this.mPackageMonitor.register(this.mContext, this.mLocationHandler.getLooper(), true);
      ??? = new IntentFilter();
      ((IntentFilter)???).addAction("android.intent.action.USER_SWITCHED");
      ((IntentFilter)???).addAction("android.intent.action.MANAGED_PROFILE_ADDED");
      ((IntentFilter)???).addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
      ((IntentFilter)???).addAction("android.intent.action.ACTION_SHUTDOWN");
      ((IntentFilter)???).addAction("android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED");
      this.mContext.registerReceiverAsUser(new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          paramAnonymousContext = paramAnonymousIntent.getAction();
          int k;
          int j;
          int i;
          if ("android.intent.action.USER_SWITCHED".equals(paramAnonymousContext))
          {
            LocationManagerService.-wrap8(LocationManagerService.this, paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 0));
            if ((!SystemProperties.get("sys.cgroup.active", "0").equals("0")) || (!"android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED".equals(paramAnonymousContext))) {
              break label594;
            }
            paramAnonymousContext = LocationManagerService.-get5(LocationManagerService.this);
            k = 0;
            j = 0;
            i = 0;
          }
          for (;;)
          {
            try
            {
              int m;
              LocationManagerService.UpdateRecord localUpdateRecord;
              if (LocationManagerService.-get6(LocationManagerService.this).isLightDeviceIdleMode())
              {
                if (LocationManagerService.DEBUG_ONEPLUS) {
                  Log.d("LocationManagerService", "in light idle mode, and remove update records in the blacklist");
                }
                paramAnonymousIntent = (ArrayList)LocationManagerService.-get9(LocationManagerService.this).get("gps");
                if (paramAnonymousIntent == null) {
                  continue;
                }
                m = paramAnonymousIntent.size();
                j = 0;
                if (j < m)
                {
                  localUpdateRecord = (LocationManagerService.UpdateRecord)paramAnonymousIntent.get(j);
                  k = i;
                  if (localUpdateRecord.mReceiver != null)
                  {
                    k = i;
                    if (localUpdateRecord.mReceiver.mPackageName != null) {
                      if ((!localUpdateRecord.mReceiver.mPackageName.equals("com.voonik.android")) && (!localUpdateRecord.mReceiver.mPackageName.equals("com.yahoo.mobile.client.android.yahoo")) && (!localUpdateRecord.mReceiver.mPackageName.equals("com.triggerteam.ninjafighting2")))
                      {
                        k = i;
                        if (!localUpdateRecord.mReceiver.mPackageName.equals("com.vodafone.vodafoneplay")) {}
                      }
                      else
                      {
                        LocationManagerService.-get3(LocationManagerService.this).add(localUpdateRecord);
                        k = 1;
                      }
                    }
                  }
                  j += 1;
                  i = k;
                  continue;
                  if (("android.intent.action.MANAGED_PROFILE_ADDED".equals(paramAnonymousContext)) || ("android.intent.action.MANAGED_PROFILE_REMOVED".equals(paramAnonymousContext)))
                  {
                    LocationManagerService.this.updateUserProfiles(LocationManagerService.-get2(LocationManagerService.this));
                    break;
                  }
                  if (!"android.intent.action.ACTION_SHUTDOWN".equals(paramAnonymousContext)) {
                    break;
                  }
                  if (LocationManagerService.D) {
                    Log.d("LocationManagerService", "Shutdown received with UserId: " + getSendingUserId());
                  }
                  if (getSendingUserId() != -1) {
                    break;
                  }
                  LocationManagerService.-wrap7(LocationManagerService.this);
                  break;
                }
                m = LocationManagerService.-get3(LocationManagerService.this).size();
                k = 0;
                j = i;
                if (k >= m) {
                  continue;
                }
                localUpdateRecord = (LocationManagerService.UpdateRecord)LocationManagerService.-get3(LocationManagerService.this).get(k);
                paramAnonymousIntent.remove(localUpdateRecord);
                if (!LocationManagerService.DEBUG_ONEPLUS) {
                  break label600;
                }
                Log.d("LocationManagerService", "Remove updaterecord: " + localUpdateRecord);
                break label600;
              }
              if (LocationManagerService.DEBUG_ONEPLUS) {
                Log.d("LocationManagerService", "leaves light idle mode, and restore update records in the blacklist.");
              }
              paramAnonymousIntent = (ArrayList)LocationManagerService.-get9(LocationManagerService.this).get("gps");
              if (paramAnonymousIntent != null)
              {
                m = LocationManagerService.-get3(LocationManagerService.this).size();
                i = 0;
                j = k;
                if (i < m)
                {
                  localUpdateRecord = (LocationManagerService.UpdateRecord)LocationManagerService.-get3(LocationManagerService.this).get(i);
                  if (localUpdateRecord.mReceiver == null) {
                    break label609;
                  }
                  if (LocationManagerService.DEBUG_ONEPLUS) {
                    Log.d("LocationManagerService", "Restore updaterecord: " + localUpdateRecord);
                  }
                  paramAnonymousIntent.add(localUpdateRecord);
                  break label609;
                }
                LocationManagerService.-get3(LocationManagerService.this).clear();
              }
              if (j != 0) {
                LocationManagerService.-wrap4(LocationManagerService.this, "gps");
              }
              label594:
              return;
            }
            finally {}
            label600:
            k += 1;
            continue;
            label609:
            j = 1;
            i += 1;
          }
        }
      }, UserHandle.ALL, (IntentFilter)???, null, this.mLocationHandler);
      return;
    }
  }
  
  public void unregisterGnssStatusCallback(IGnssStatusListener paramIGnssStatusListener)
  {
    synchronized (this.mLock)
    {
      try
      {
        this.mGnssStatusProvider.unregisterGnssStatusCallback(paramIGnssStatusListener);
        return;
      }
      catch (Exception paramIGnssStatusListener)
      {
        for (;;)
        {
          Slog.e("LocationManagerService", "mGpsStatusProvider.unregisterGnssStatusCallback failed", paramIGnssStatusListener);
        }
      }
    }
  }
  
  public void updateReceiverBlockRequest(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      mBlockLocationUids.remove(Integer.valueOf(paramInt));
      mBlockLocationUids.add(Integer.valueOf(paramInt));
      mBlockReceiverUids.remove(Integer.valueOf(paramInt));
      mBlockReceiverUids.add(Integer.valueOf(paramInt));
    }
    synchronized (this.mLock)
    {
      applyAllProviderRequirementsLocked();
      return;
      mBlockLocationUids.remove(Integer.valueOf(paramInt));
      mBlockReceiverUids.remove(Integer.valueOf(paramInt));
    }
  }
  
  void updateUserProfiles(int paramInt)
  {
    int[] arrayOfInt = this.mUserManager.getProfileIdsWithDisabled(paramInt);
    synchronized (this.mLock)
    {
      this.mCurrentUserProfiles = arrayOfInt;
      return;
    }
  }
  
  private class LocationWorkerHandler
    extends Handler
  {
    public LocationWorkerHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = true;
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      LocationManagerService localLocationManagerService = LocationManagerService.this;
      Location localLocation = (Location)paramMessage.obj;
      if (paramMessage.arg1 == 1) {}
      for (;;)
      {
        LocationManagerService.-wrap5(localLocationManagerService, localLocation, bool);
        return;
        bool = false;
      }
    }
  }
  
  private final class Receiver
    implements IBinder.DeathRecipient, PendingIntent.OnFinished
  {
    final int mAllowedResolutionLevel;
    final boolean mHideFromAppOps;
    final Object mKey;
    final ILocationListener mListener;
    boolean mOpHighPowerMonitoring;
    boolean mOpMonitoring;
    final String mPackageName;
    int mPendingBroadcasts;
    final PendingIntent mPendingIntent;
    final int mPid;
    final int mUid;
    final HashMap<String, LocationManagerService.UpdateRecord> mUpdateRecords = new HashMap();
    PowerManager.WakeLock mWakeLock;
    final WorkSource mWorkSource;
    
    Receiver(ILocationListener paramILocationListener, PendingIntent paramPendingIntent, int paramInt1, int paramInt2, String paramString, WorkSource paramWorkSource, boolean paramBoolean)
    {
      this.mListener = paramILocationListener;
      this.mPendingIntent = paramPendingIntent;
      if (paramILocationListener != null) {}
      for (this.mKey = paramILocationListener.asBinder();; this.mKey = paramPendingIntent)
      {
        this.mAllowedResolutionLevel = LocationManagerService.-wrap1(LocationManagerService.this, paramInt1, paramInt2);
        this.mUid = paramInt2;
        this.mPid = paramInt1;
        this.mPackageName = paramString;
        paramILocationListener = paramWorkSource;
        if (paramWorkSource != null)
        {
          paramILocationListener = paramWorkSource;
          if (paramWorkSource.size() <= 0) {
            paramILocationListener = null;
          }
        }
        this.mWorkSource = paramILocationListener;
        this.mHideFromAppOps = paramBoolean;
        updateMonitoring(true);
        this.mWakeLock = LocationManagerService.-get6(LocationManagerService.this).newWakeLock(1, "LocationManagerService");
        this$1 = paramILocationListener;
        if (paramILocationListener == null) {
          this$1 = new WorkSource(this.mUid, this.mPackageName);
        }
        this.mWakeLock.setWorkSource(LocationManagerService.this);
        return;
      }
    }
    
    private void decrementPendingBroadcastsLocked()
    {
      int i = this.mPendingBroadcasts - 1;
      this.mPendingBroadcasts = i;
      if ((i == 0) && (this.mWakeLock.isHeld())) {
        this.mWakeLock.release();
      }
    }
    
    private void incrementPendingBroadcastsLocked()
    {
      int i = this.mPendingBroadcasts;
      this.mPendingBroadcasts = (i + 1);
      if (i == 0) {
        this.mWakeLock.acquire();
      }
    }
    
    private boolean updateMonitoring(boolean paramBoolean1, boolean paramBoolean2, int paramInt)
    {
      boolean bool = false;
      if (!paramBoolean2)
      {
        if (paramBoolean1)
        {
          paramBoolean1 = bool;
          if (LocationManagerService.-get0(LocationManagerService.this).startOpNoThrow(paramInt, this.mUid, this.mPackageName) == 0) {
            paramBoolean1 = true;
          }
          return paramBoolean1;
        }
      }
      else if ((!paramBoolean1) || (LocationManagerService.-get0(LocationManagerService.this).checkOpNoThrow(paramInt, this.mUid, this.mPackageName) != 0))
      {
        LocationManagerService.-get0(LocationManagerService.this).finishOp(paramInt, this.mUid, this.mPackageName);
        return false;
      }
      return paramBoolean2;
    }
    
    public void binderDied()
    {
      if (LocationManagerService.D) {
        Log.d("LocationManagerService", "Location listener died");
      }
      synchronized (LocationManagerService.-get5(LocationManagerService.this))
      {
        LocationManagerService.-wrap6(LocationManagerService.this, this);
      }
    }
    
    public boolean callLocationChangedLocked(Location paramLocation)
    {
      Intent localIntent;
      if (this.mListener != null) {
        try
        {
          try
          {
            this.mListener.onLocationChanged(new Location(paramLocation));
            incrementPendingBroadcastsLocked();
            break label117;
          }
          finally
          {
            paramLocation = finally;
            throw paramLocation;
          }
          localIntent = new Intent();
        }
        catch (RemoteException paramLocation)
        {
          return false;
        }
      } else {
        localIntent.putExtra("location", new Location(paramLocation));
      }
      try
      {
        try
        {
          this.mPendingIntent.send(LocationManagerService.-get1(LocationManagerService.this), 0, localIntent, this, LocationManagerService.-get4(LocationManagerService.this), LocationManagerService.-wrap2(LocationManagerService.this, this.mAllowedResolutionLevel));
          incrementPendingBroadcastsLocked();
        }
        finally
        {
          paramLocation = finally;
          throw paramLocation;
        }
        label117:
        return true;
      }
      catch (PendingIntent.CanceledException paramLocation) {}
      return false;
    }
    
    public boolean callProviderEnabledLocked(String paramString, boolean paramBoolean)
    {
      updateMonitoring(true);
      if (this.mListener != null) {
        try
        {
          if (paramBoolean) {}
          for (;;)
          {
            try
            {
              this.mListener.onProviderEnabled(paramString);
              incrementPendingBroadcastsLocked();
              return true;
            }
            finally {}
            this.mListener.onProviderDisabled(paramString);
          }
          paramString = new Intent();
        }
        catch (RemoteException paramString)
        {
          return false;
        }
      }
      paramString.putExtra("providerEnabled", paramBoolean);
      try
      {
        try
        {
          this.mPendingIntent.send(LocationManagerService.-get1(LocationManagerService.this), 0, paramString, this, LocationManagerService.-get4(LocationManagerService.this), LocationManagerService.-wrap2(LocationManagerService.this, this.mAllowedResolutionLevel));
          incrementPendingBroadcastsLocked();
          return true;
        }
        finally
        {
          paramString = finally;
          throw paramString;
        }
        return false;
      }
      catch (PendingIntent.CanceledException paramString) {}
    }
    
    public boolean callStatusChangedLocked(String paramString, int paramInt, Bundle paramBundle)
    {
      if (this.mListener != null)
      {
        try
        {
          try
          {
            this.mListener.onStatusChanged(paramString, paramInt, paramBundle);
            incrementPendingBroadcastsLocked();
            break label118;
          }
          finally
          {
            paramString = finally;
            throw paramString;
          }
          paramString = new Intent();
        }
        catch (RemoteException paramString)
        {
          return false;
        }
      }
      else
      {
        paramString.putExtras(new Bundle(paramBundle));
        paramString.putExtra("status", paramInt);
      }
      try
      {
        try
        {
          this.mPendingIntent.send(LocationManagerService.-get1(LocationManagerService.this), 0, paramString, this, LocationManagerService.-get4(LocationManagerService.this), LocationManagerService.-wrap2(LocationManagerService.this, this.mAllowedResolutionLevel));
          incrementPendingBroadcastsLocked();
        }
        finally
        {
          paramString = finally;
          throw paramString;
        }
        label118:
        return true;
      }
      catch (PendingIntent.CanceledException paramString) {}
      return false;
    }
    
    public void clearPendingBroadcastsLocked()
    {
      if (this.mPendingBroadcasts > 0)
      {
        this.mPendingBroadcasts = 0;
        if (this.mWakeLock.isHeld()) {
          this.mWakeLock.release();
        }
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Receiver)) {
        return this.mKey.equals(((Receiver)paramObject).mKey);
      }
      return false;
    }
    
    public ILocationListener getListener()
    {
      if (this.mListener != null) {
        return this.mListener;
      }
      throw new IllegalStateException("Request for non-existent listener");
    }
    
    public int hashCode()
    {
      return this.mKey.hashCode();
    }
    
    public boolean isListener()
    {
      return this.mListener != null;
    }
    
    public boolean isPendingIntent()
    {
      return this.mPendingIntent != null;
    }
    
    public void onSendFinished(PendingIntent paramPendingIntent, Intent paramIntent, int paramInt, String paramString, Bundle paramBundle)
    {
      try
      {
        decrementPendingBroadcastsLocked();
        return;
      }
      finally
      {
        paramPendingIntent = finally;
        throw paramPendingIntent;
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Reciever[");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      if (this.mListener != null) {
        localStringBuilder.append(" listener");
      }
      for (;;)
      {
        Iterator localIterator = this.mUpdateRecords.keySet().iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          localStringBuilder.append(" ").append(((LocationManagerService.UpdateRecord)this.mUpdateRecords.get(str)).toString());
        }
        localStringBuilder.append(" intent");
      }
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
    
    public void updateMonitoring(boolean paramBoolean)
    {
      if (this.mHideFromAppOps) {
        return;
      }
      boolean bool1 = false;
      boolean bool4 = false;
      boolean bool3 = false;
      boolean bool2 = bool3;
      LocationManagerService.UpdateRecord localUpdateRecord;
      if (paramBoolean)
      {
        Iterator localIterator = this.mUpdateRecords.values().iterator();
        paramBoolean = bool4;
        do
        {
          bool2 = bool3;
          bool1 = paramBoolean;
          if (!localIterator.hasNext()) {
            break;
          }
          localUpdateRecord = (LocationManagerService.UpdateRecord)localIterator.next();
        } while (!LocationManagerService.-wrap0(LocationManagerService.this, localUpdateRecord.mProvider));
        bool1 = true;
        bool2 = true;
        localObject = (LocationProviderInterface)LocationManagerService.-get7(LocationManagerService.this).get(localUpdateRecord.mProvider);
        if (localObject == null) {
          break label228;
        }
      }
      label228:
      for (Object localObject = ((LocationProviderInterface)localObject).getProperties();; localObject = null)
      {
        paramBoolean = bool2;
        if (localObject == null) {
          break;
        }
        paramBoolean = bool2;
        if (((ProviderProperties)localObject).mPowerRequirement != 3) {
          break;
        }
        paramBoolean = bool2;
        if (localUpdateRecord.mRequest.getInterval() >= 300000L) {
          break;
        }
        bool2 = true;
        this.mOpMonitoring = updateMonitoring(bool1, this.mOpMonitoring, 41);
        paramBoolean = this.mOpHighPowerMonitoring;
        this.mOpHighPowerMonitoring = updateMonitoring(bool2, this.mOpHighPowerMonitoring, 42);
        if (this.mOpHighPowerMonitoring != paramBoolean)
        {
          localObject = new Intent("android.location.HIGH_POWER_REQUEST_CHANGE");
          LocationManagerService.-get1(LocationManagerService.this).sendBroadcastAsUser((Intent)localObject, UserHandle.ALL);
        }
        return;
      }
    }
  }
  
  private class UpdateRecord
  {
    Location mLastFixBroadcast;
    long mLastStatusBroadcast;
    final String mProvider;
    final LocationManagerService.Receiver mReceiver;
    final LocationRequest mRequest;
    
    UpdateRecord(String paramString, LocationRequest paramLocationRequest, LocationManagerService.Receiver paramReceiver)
    {
      this.mProvider = paramString;
      this.mRequest = paramLocationRequest;
      this.mReceiver = paramReceiver;
      ArrayList localArrayList2 = (ArrayList)LocationManagerService.-get9(LocationManagerService.this).get(paramString);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        LocationManagerService.-get9(LocationManagerService.this).put(paramString, localArrayList1);
      }
      if (!localArrayList1.contains(this)) {
        localArrayList1.add(this);
      }
      if (paramString.equals("gps")) {
        LocationManagerService.updateLocationReceiver(paramReceiver.mUid, true, true, true);
      }
      for (;;)
      {
        LocationManagerService.-get10(LocationManagerService.this).startRequesting(this.mReceiver.mPackageName, paramString, paramLocationRequest.getInterval());
        return;
        LocationManagerService.updateLocationReceiver(paramReceiver.mUid, true, true, false);
      }
    }
    
    void disposeLocked(boolean paramBoolean)
    {
      LocationManagerService.-get10(LocationManagerService.this).stopRequesting(this.mReceiver.mPackageName, this.mProvider);
      Object localObject = (ArrayList)LocationManagerService.-get9(LocationManagerService.this).get(this.mProvider);
      if (localObject != null) {
        ((ArrayList)localObject).remove(this);
      }
      if (this.mProvider.equals("gps")) {
        LocationManagerService.updateLocationReceiver(this.mReceiver.mUid, false, true, true);
      }
      while (!paramBoolean)
      {
        return;
        LocationManagerService.updateLocationReceiver(this.mReceiver.mUid, false, true, false);
      }
      localObject = this.mReceiver.mUpdateRecords;
      if (localObject != null)
      {
        ((HashMap)localObject).remove(this.mProvider);
        if ((paramBoolean) && (((HashMap)localObject).size() == 0)) {
          LocationManagerService.-wrap6(LocationManagerService.this, this.mReceiver);
        }
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("UpdateRecord[");
      localStringBuilder.append(this.mProvider);
      localStringBuilder.append(' ').append(this.mReceiver.mPackageName).append('(');
      localStringBuilder.append(this.mReceiver.mUid).append(')');
      localStringBuilder.append(' ').append(this.mRequest);
      localStringBuilder.append(']');
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/LocationManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */