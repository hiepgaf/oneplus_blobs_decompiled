package com.android.server.pm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManagerInternal.PackagesProvider;
import android.content.pm.PackageManagerInternal.SyncAdapterPackagesProvider;
import android.content.pm.PackageParser.Package;
import android.content.pm.ParceledListSlice;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.util.XmlUtils;
import com.android.server.ServiceThread;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

final class DefaultPermissionGrantPolicy
{
  private static final String ATTR_FIXED = "fixed";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_PACKAGE = "package";
  private static final String AUDIO_MIME_TYPE = "audio/mpeg";
  private static final Set<String> CALENDAR_PERMISSIONS;
  private static final Set<String> CAMERA_PERMISSIONS;
  private static final Set<String> CONTACTS_PERMISSIONS;
  private static final boolean DEBUG = false;
  private static final int DEFAULT_FLAGS = 786432;
  private static final Set<String> LOCATION_PERMISSIONS;
  private static final Set<String> MICROPHONE_PERMISSIONS;
  private static final int MSG_READ_DEFAULT_PERMISSION_EXCEPTIONS = 1;
  private static final Set<String> PHONE_PERMISSIONS = new ArraySet();
  private static final Set<String> SENSORS_PERMISSIONS;
  private static final Set<String> SMS_PERMISSIONS;
  private static final Set<String> STORAGE_PERMISSIONS;
  private static final String TAG = "DefaultPermGrantPolicy";
  private static final String TAG_EXCEPTION = "exception";
  private static final String TAG_EXCEPTIONS = "exceptions";
  private static final String TAG_PERMISSION = "permission";
  private PackageManagerInternal.PackagesProvider mDialerAppPackagesProvider;
  private ArrayMap<String, List<DefaultPermissionGrant>> mGrantExceptions;
  private final Handler mHandler;
  private PackageManagerInternal.PackagesProvider mLocationPackagesProvider;
  private final PackageManagerService mService;
  private PackageManagerInternal.PackagesProvider mSimCallManagerPackagesProvider;
  private PackageManagerInternal.PackagesProvider mSmsAppPackagesProvider;
  private PackageManagerInternal.SyncAdapterPackagesProvider mSyncAdapterPackagesProvider;
  private PackageManagerInternal.PackagesProvider mVoiceInteractionPackagesProvider;
  
  static
  {
    PHONE_PERMISSIONS.add("android.permission.READ_PHONE_STATE");
    PHONE_PERMISSIONS.add("android.permission.CALL_PHONE");
    PHONE_PERMISSIONS.add("android.permission.READ_CALL_LOG");
    PHONE_PERMISSIONS.add("android.permission.WRITE_CALL_LOG");
    PHONE_PERMISSIONS.add("com.android.voicemail.permission.ADD_VOICEMAIL");
    PHONE_PERMISSIONS.add("android.permission.USE_SIP");
    PHONE_PERMISSIONS.add("android.permission.PROCESS_OUTGOING_CALLS");
    CONTACTS_PERMISSIONS = new ArraySet();
    CONTACTS_PERMISSIONS.add("android.permission.READ_CONTACTS");
    CONTACTS_PERMISSIONS.add("android.permission.WRITE_CONTACTS");
    CONTACTS_PERMISSIONS.add("android.permission.GET_ACCOUNTS");
    LOCATION_PERMISSIONS = new ArraySet();
    LOCATION_PERMISSIONS.add("android.permission.ACCESS_FINE_LOCATION");
    LOCATION_PERMISSIONS.add("android.permission.ACCESS_COARSE_LOCATION");
    CALENDAR_PERMISSIONS = new ArraySet();
    CALENDAR_PERMISSIONS.add("android.permission.READ_CALENDAR");
    CALENDAR_PERMISSIONS.add("android.permission.WRITE_CALENDAR");
    SMS_PERMISSIONS = new ArraySet();
    SMS_PERMISSIONS.add("android.permission.SEND_SMS");
    SMS_PERMISSIONS.add("android.permission.RECEIVE_SMS");
    SMS_PERMISSIONS.add("android.permission.READ_SMS");
    SMS_PERMISSIONS.add("android.permission.RECEIVE_WAP_PUSH");
    SMS_PERMISSIONS.add("android.permission.RECEIVE_MMS");
    SMS_PERMISSIONS.add("android.permission.READ_CELL_BROADCASTS");
    MICROPHONE_PERMISSIONS = new ArraySet();
    MICROPHONE_PERMISSIONS.add("android.permission.RECORD_AUDIO");
    CAMERA_PERMISSIONS = new ArraySet();
    CAMERA_PERMISSIONS.add("android.permission.CAMERA");
    SENSORS_PERMISSIONS = new ArraySet();
    SENSORS_PERMISSIONS.add("android.permission.BODY_SENSORS");
    STORAGE_PERMISSIONS = new ArraySet();
    STORAGE_PERMISSIONS.add("android.permission.READ_EXTERNAL_STORAGE");
    STORAGE_PERMISSIONS.add("android.permission.WRITE_EXTERNAL_STORAGE");
  }
  
  public DefaultPermissionGrantPolicy(PackageManagerService paramPackageManagerService)
  {
    this.mService = paramPackageManagerService;
    this.mHandler = new Handler(this.mService.mHandlerThread.getLooper())
    {
      public void handleMessage(Message arg1)
      {
        if (???.what == 1) {}
        synchronized (DefaultPermissionGrantPolicy.-get1(DefaultPermissionGrantPolicy.this).mPackages)
        {
          if (DefaultPermissionGrantPolicy.-get0(DefaultPermissionGrantPolicy.this) == null) {
            DefaultPermissionGrantPolicy.-set0(DefaultPermissionGrantPolicy.this, DefaultPermissionGrantPolicy.-wrap0(DefaultPermissionGrantPolicy.this));
          }
          return;
        }
      }
    };
  }
  
  private static boolean doesPackageSupportRuntimePermissions(PackageParser.Package paramPackage)
  {
    return paramPackage.applicationInfo.targetSdkVersion > 22;
  }
  
  private PackageParser.Package getDefaultProviderAuthorityPackageLPr(String paramString, int paramInt)
  {
    paramString = this.mService.resolveContentProvider(paramString, 786432, paramInt);
    if (paramString != null) {
      return getSystemPackageLPr(paramString.packageName);
    }
    return null;
  }
  
  private PackageParser.Package getDefaultSystemHandlerActivityPackageLPr(Intent paramIntent, int paramInt)
  {
    paramIntent = this.mService.resolveIntent(paramIntent, paramIntent.resolveType(this.mService.mContext.getContentResolver()), 786432, paramInt);
    if ((paramIntent == null) || (paramIntent.activityInfo == null)) {
      return null;
    }
    ActivityInfo localActivityInfo = paramIntent.activityInfo;
    if ((localActivityInfo.packageName.equals(this.mService.mResolveActivity.packageName)) && (localActivityInfo.name.equals(this.mService.mResolveActivity.name))) {
      return null;
    }
    return getSystemPackageLPr(paramIntent.activityInfo.packageName);
  }
  
  private PackageParser.Package getDefaultSystemHandlerServicePackageLPr(Intent paramIntent, int paramInt)
  {
    paramIntent = this.mService.queryIntentServices(paramIntent, paramIntent.resolveType(this.mService.mContext.getContentResolver()), 786432, paramInt).getList();
    if (paramIntent == null) {
      return null;
    }
    int i = paramIntent.size();
    paramInt = 0;
    while (paramInt < i)
    {
      PackageParser.Package localPackage = getSystemPackageLPr(((ResolveInfo)paramIntent.get(paramInt)).serviceInfo.packageName);
      if (localPackage != null) {
        return localPackage;
      }
      paramInt += 1;
    }
    return null;
  }
  
  private List<PackageParser.Package> getHeadlessSyncAdapterPackagesLPr(String[] paramArrayOfString, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory("android.intent.category.LAUNCHER");
    int i = 0;
    int j = paramArrayOfString.length;
    if (i < j)
    {
      Object localObject = paramArrayOfString[i];
      localIntent.setPackage((String)localObject);
      if (this.mService.resolveIntent(localIntent, localIntent.resolveType(this.mService.mContext.getContentResolver()), 786432, paramInt) != null) {}
      for (;;)
      {
        i += 1;
        break;
        localObject = getSystemPackageLPr((String)localObject);
        if (localObject != null) {
          localArrayList.add(localObject);
        }
      }
    }
    return localArrayList;
  }
  
  private PackageParser.Package getPackageLPr(String paramString)
  {
    return (PackageParser.Package)this.mService.mPackages.get(paramString);
  }
  
  private PackageParser.Package getSystemPackageLPr(String paramString)
  {
    paramString = getPackageLPr(paramString);
    if ((paramString != null) && (paramString.isSystemApp()))
    {
      if (!isSysComponentOrPersistentPlatformSignedPrivAppLPr(paramString)) {
        return paramString;
      }
      return null;
    }
    return null;
  }
  
  private void grantDefaultPermissionExceptions(int paramInt)
  {
    for (;;)
    {
      int i;
      synchronized (this.mService.mPackages)
      {
        this.mHandler.removeMessages(1);
        if (this.mGrantExceptions == null) {
          this.mGrantExceptions = readDefaultPermissionExceptionsLPw();
        }
        ArraySet localArraySet = null;
        int k = this.mGrantExceptions.size();
        i = 0;
        if (i >= k) {
          break;
        }
        PackageParser.Package localPackage = getSystemPackageLPr((String)this.mGrantExceptions.keyAt(i));
        List localList = (List)this.mGrantExceptions.valueAt(i);
        int m = localList.size();
        int j = 0;
        if (j < m)
        {
          DefaultPermissionGrant localDefaultPermissionGrant = (DefaultPermissionGrant)localList.get(j);
          if (localArraySet == null)
          {
            localArraySet = new ArraySet();
            localArraySet.add(localDefaultPermissionGrant.name);
            grantRuntimePermissionsLPw(localPackage, localArraySet, false, localDefaultPermissionGrant.fixed, paramInt);
            j += 1;
            continue;
          }
          localArraySet.clear();
        }
      }
      i += 1;
    }
  }
  
  private void grantDefaultPermissionsToDefaultSimCallManagerLPr(PackageParser.Package paramPackage, int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to sim call manager for user:" + paramInt);
    if (doesPackageSupportRuntimePermissions(paramPackage))
    {
      grantRuntimePermissionsLPw(paramPackage, PHONE_PERMISSIONS, paramInt);
      grantRuntimePermissionsLPw(paramPackage, MICROPHONE_PERMISSIONS, paramInt);
    }
  }
  
  private void grantDefaultPermissionsToDefaultSystemDialerAppLPr(PackageParser.Package paramPackage, int paramInt)
  {
    if (doesPackageSupportRuntimePermissions(paramPackage))
    {
      boolean bool = this.mService.hasSystemFeature("android.hardware.type.watch", 0);
      grantRuntimePermissionsLPw(paramPackage, PHONE_PERMISSIONS, bool, paramInt);
      grantRuntimePermissionsLPw(paramPackage, CONTACTS_PERMISSIONS, paramInt);
      grantRuntimePermissionsLPw(paramPackage, SMS_PERMISSIONS, paramInt);
      grantRuntimePermissionsLPw(paramPackage, MICROPHONE_PERMISSIONS, paramInt);
      grantRuntimePermissionsLPw(paramPackage, CAMERA_PERMISSIONS, paramInt);
    }
  }
  
  private void grantDefaultPermissionsToDefaultSystemSmsAppLPr(PackageParser.Package paramPackage, int paramInt)
  {
    if (doesPackageSupportRuntimePermissions(paramPackage))
    {
      grantRuntimePermissionsLPw(paramPackage, PHONE_PERMISSIONS, paramInt);
      grantRuntimePermissionsLPw(paramPackage, CONTACTS_PERMISSIONS, paramInt);
      grantRuntimePermissionsLPw(paramPackage, SMS_PERMISSIONS, paramInt);
    }
  }
  
  private void grantDefaultSystemHandlerPermissions(int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to default platform handlers for user " + paramInt);
    Object localObject7;
    Object localObject11;
    Object localObject8;
    Object localObject10;
    Object localObject9;
    PackageParser.Package localPackage;
    int i;
    int j;
    label675:
    label681:
    label687:
    label693:
    label699:
    Object localObject3;
    label705:
    Object localObject5;
    synchronized (this.mService.mPackages)
    {
      localObject7 = this.mLocationPackagesProvider;
      localObject11 = this.mVoiceInteractionPackagesProvider;
      localObject8 = this.mSmsAppPackagesProvider;
      localObject10 = this.mDialerAppPackagesProvider;
      Object localObject1 = this.mSimCallManagerPackagesProvider;
      localObject9 = this.mSyncAdapterPackagesProvider;
      if (localObject11 != null)
      {
        ??? = ((PackageManagerInternal.PackagesProvider)localObject11).getPackages(paramInt);
        if (localObject7 == null) {
          break label675;
        }
        localObject7 = ((PackageManagerInternal.PackagesProvider)localObject7).getPackages(paramInt);
        if (localObject8 == null) {
          break label681;
        }
        localObject8 = ((PackageManagerInternal.PackagesProvider)localObject8).getPackages(paramInt);
        if (localObject10 == null) {
          break label687;
        }
        localObject10 = ((PackageManagerInternal.PackagesProvider)localObject10).getPackages(paramInt);
        if (localObject1 == null) {
          break label693;
        }
        localObject11 = ((PackageManagerInternal.PackagesProvider)localObject1).getPackages(paramInt);
        if (localObject9 == null) {
          break label699;
        }
        localObject1 = ((PackageManagerInternal.SyncAdapterPackagesProvider)localObject9).getPackages("com.android.contacts", paramInt);
        if (localObject9 == null) {
          break label705;
        }
        localObject9 = ((PackageManagerInternal.SyncAdapterPackagesProvider)localObject9).getPackages("com.android.calendar", paramInt);
      }
    }
  }
  
  private void grantPermissionsToSysComponentsAndPrivApps(int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to platform components for user " + paramInt);
    for (;;)
    {
      int i;
      synchronized (this.mService.mPackages)
      {
        Iterator localIterator = this.mService.mPackages.values().iterator();
        if (localIterator.hasNext())
        {
          PackageParser.Package localPackage = (PackageParser.Package)localIterator.next();
          if ((!isSysComponentOrPersistentPlatformSignedPrivAppLPr(localPackage)) || (!doesPackageSupportRuntimePermissions(localPackage)) || (localPackage.requestedPermissions.isEmpty())) {
            continue;
          }
          ArraySet localArraySet = new ArraySet();
          int j = localPackage.requestedPermissions.size();
          i = 0;
          if (i < j)
          {
            String str = (String)localPackage.requestedPermissions.get(i);
            BasePermission localBasePermission = (BasePermission)this.mService.mSettings.mPermissions.get(str);
            if ((localBasePermission == null) || (!localBasePermission.isRuntime())) {
              break label225;
            }
            localArraySet.add(str);
            break label225;
          }
          if (localArraySet.isEmpty()) {
            continue;
          }
          grantRuntimePermissionsLPw(localPackage, localArraySet, true, paramInt);
        }
      }
      return;
      label225:
      i += 1;
    }
  }
  
  private void grantRuntimePermissionsLPw(PackageParser.Package paramPackage, Set<String> paramSet, int paramInt)
  {
    grantRuntimePermissionsLPw(paramPackage, paramSet, false, false, paramInt);
  }
  
  private void grantRuntimePermissionsLPw(PackageParser.Package paramPackage, Set<String> paramSet, boolean paramBoolean, int paramInt)
  {
    grantRuntimePermissionsLPw(paramPackage, paramSet, paramBoolean, false, paramInt);
  }
  
  private void grantRuntimePermissionsLPw(PackageParser.Package paramPackage, Set<String> paramSet, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    if (paramPackage.requestedPermissions.isEmpty()) {
      return;
    }
    Object localObject3 = paramPackage.requestedPermissions;
    Object localObject4 = null;
    Object localObject2 = localObject4;
    Object localObject1 = localObject3;
    if (!paramBoolean2)
    {
      localObject2 = localObject4;
      localObject1 = localObject3;
      if (paramPackage.isUpdatedSystemApp())
      {
        PackageSetting localPackageSetting = this.mService.mSettings.getDisabledSystemPkgLPr(paramPackage.packageName);
        localObject2 = localObject4;
        localObject1 = localObject3;
        if (localPackageSetting != null)
        {
          if (localPackageSetting.pkg.requestedPermissions.isEmpty()) {
            return;
          }
          localObject2 = localObject4;
          localObject1 = localObject3;
          if (!((List)localObject3).equals(localPackageSetting.pkg.requestedPermissions))
          {
            localObject2 = new ArraySet((Collection)localObject3);
            localObject1 = localPackageSetting.pkg.requestedPermissions;
          }
        }
      }
    }
    int k = ((List)localObject1).size();
    int i = 0;
    if (i < k)
    {
      localObject3 = (String)((List)localObject1).get(i);
      int m;
      if (((localObject2 == null) || (((Set)localObject2).contains(localObject3))) && (paramSet.contains(localObject3)))
      {
        m = this.mService.getPermissionFlags((String)localObject3, paramPackage.packageName, paramInt);
        if ((m != 0) && (!paramBoolean2)) {
          break label290;
        }
        if ((m & 0x14) == 0) {
          break label244;
        }
      }
      for (;;)
      {
        i += 1;
        break;
        label244:
        this.mService.grantRuntimePermission(paramPackage.packageName, (String)localObject3, paramInt);
        int j = 32;
        if (paramBoolean1) {
          j = 48;
        }
        this.mService.updatePermissionFlags((String)localObject3, paramPackage.packageName, j, j, paramInt);
        label290:
        if (((m & 0x20) != 0) && ((m & 0x10) != 0) && (!paramBoolean1)) {
          this.mService.updatePermissionFlags((String)localObject3, paramPackage.packageName, 16, 0, paramInt);
        }
      }
    }
  }
  
  private boolean isSysComponentOrPersistentPlatformSignedPrivAppLPr(PackageParser.Package paramPackage)
  {
    if (UserHandle.getAppId(paramPackage.applicationInfo.uid) < 10000) {
      return true;
    }
    if (!paramPackage.isPrivilegedApp()) {
      return false;
    }
    PackageSetting localPackageSetting = this.mService.mSettings.getDisabledSystemPkgLPr(paramPackage.packageName);
    if ((localPackageSetting != null) && (localPackageSetting.pkg != null))
    {
      if ((localPackageSetting.pkg.applicationInfo.flags & 0x8) == 0) {
        return false;
      }
    }
    else if ((paramPackage.applicationInfo.flags & 0x8) == 0) {
      return false;
    }
    return PackageManagerService.compareSignatures(this.mService.mPlatformPackage.mSignatures, paramPackage.mSignatures) == 0;
  }
  
  private void parse(XmlPullParser paramXmlPullParser, Map<String, List<DefaultPermissionGrant>> paramMap)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4)) {
        if ("exceptions".equals(paramXmlPullParser.getName())) {
          parseExceptions(paramXmlPullParser, paramMap);
        } else {
          Log.e("DefaultPermGrantPolicy", "Unknown tag " + paramXmlPullParser.getName());
        }
      }
    }
  }
  
  private void parseExceptions(XmlPullParser paramXmlPullParser, Map<String, List<DefaultPermissionGrant>> paramMap)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4)) {
        if ("exception".equals(paramXmlPullParser.getName()))
        {
          String str = paramXmlPullParser.getAttributeValue(null, "package");
          List localList = (List)paramMap.get(str);
          Object localObject = localList;
          if (localList == null)
          {
            localObject = getSystemPackageLPr(str);
            if (localObject == null)
            {
              Log.w("DefaultPermGrantPolicy", "Unknown package:" + str);
              XmlUtils.skipCurrentTag(paramXmlPullParser);
            }
            else if (!doesPackageSupportRuntimePermissions((PackageParser.Package)localObject))
            {
              Log.w("DefaultPermGrantPolicy", "Skipping non supporting runtime permissions package:" + str);
              XmlUtils.skipCurrentTag(paramXmlPullParser);
            }
            else
            {
              localObject = new ArrayList();
              paramMap.put(str, localObject);
            }
          }
          else
          {
            parsePermission(paramXmlPullParser, (List)localObject);
          }
        }
        else
        {
          Log.e("DefaultPermGrantPolicy", "Unknown tag " + paramXmlPullParser.getName() + "under <exceptions>");
        }
      }
    }
  }
  
  private void parsePermission(XmlPullParser paramXmlPullParser, List<DefaultPermissionGrant> paramList)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4)) {
        if ("permission".contains(paramXmlPullParser.getName()))
        {
          String str = paramXmlPullParser.getAttributeValue(null, "name");
          if (str == null)
          {
            Log.w("DefaultPermGrantPolicy", "Mandatory name attribute missing for permission tag");
            XmlUtils.skipCurrentTag(paramXmlPullParser);
          }
          else
          {
            paramList.add(new DefaultPermissionGrant(str, XmlUtils.readBooleanAttribute(paramXmlPullParser, "fixed")));
          }
        }
        else
        {
          Log.e("DefaultPermGrantPolicy", "Unknown tag " + paramXmlPullParser.getName() + "under <exception>");
        }
      }
    }
  }
  
  /* Error */
  private ArrayMap<String, List<DefaultPermissionGrant>> readDefaultPermissionExceptionsLPw()
  {
    // Byte code:
    //   0: new 486	java/io/File
    //   3: dup
    //   4: invokestatic 702	android/os/Environment:getRootDirectory	()Ljava/io/File;
    //   7: ldc_w 704
    //   10: invokespecial 707	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   13: astore 7
    //   15: aload 7
    //   17: invokevirtual 710	java/io/File:exists	()Z
    //   20: ifeq +40 -> 60
    //   23: aload 7
    //   25: invokevirtual 713	java/io/File:isDirectory	()Z
    //   28: ifeq +32 -> 60
    //   31: aload 7
    //   33: invokevirtual 716	java/io/File:canRead	()Z
    //   36: ifeq +24 -> 60
    //   39: aload 7
    //   41: invokevirtual 720	java/io/File:listFiles	()[Ljava/io/File;
    //   44: astore 8
    //   46: aload 8
    //   48: ifnonnull +21 -> 69
    //   51: new 309	android/util/ArrayMap
    //   54: dup
    //   55: iconst_0
    //   56: invokespecial 722	android/util/ArrayMap:<init>	(I)V
    //   59: areturn
    //   60: new 309	android/util/ArrayMap
    //   63: dup
    //   64: iconst_0
    //   65: invokespecial 722	android/util/ArrayMap:<init>	(I)V
    //   68: areturn
    //   69: new 309	android/util/ArrayMap
    //   72: dup
    //   73: invokespecial 723	android/util/ArrayMap:<init>	()V
    //   76: astore 9
    //   78: aload 8
    //   80: arraylength
    //   81: istore_2
    //   82: iconst_0
    //   83: istore_1
    //   84: iload_1
    //   85: iload_2
    //   86: if_icmpge +268 -> 354
    //   89: aload 8
    //   91: iload_1
    //   92: aaload
    //   93: astore 10
    //   95: aload 10
    //   97: invokevirtual 726	java/io/File:getPath	()Ljava/lang/String;
    //   100: ldc_w 728
    //   103: invokevirtual 732	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   106: ifne +54 -> 160
    //   109: ldc 44
    //   111: new 349	java/lang/StringBuilder
    //   114: dup
    //   115: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   118: ldc_w 734
    //   121: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: aload 10
    //   126: invokevirtual 737	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   129: ldc_w 739
    //   132: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: aload 7
    //   137: invokevirtual 737	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   140: ldc_w 741
    //   143: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: invokevirtual 363	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   149: invokestatic 744	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   152: pop
    //   153: iload_1
    //   154: iconst_1
    //   155: iadd
    //   156: istore_1
    //   157: goto -73 -> 84
    //   160: aload 10
    //   162: invokevirtual 716	java/io/File:canRead	()Z
    //   165: ifne +39 -> 204
    //   168: ldc 44
    //   170: new 349	java/lang/StringBuilder
    //   173: dup
    //   174: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   177: ldc_w 746
    //   180: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   183: aload 10
    //   185: invokevirtual 737	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   188: ldc_w 748
    //   191: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   194: invokevirtual 363	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   197: invokestatic 749	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   200: pop
    //   201: goto -48 -> 153
    //   204: aconst_null
    //   205: astore 4
    //   207: aconst_null
    //   208: astore 5
    //   210: new 751	java/io/BufferedInputStream
    //   213: dup
    //   214: new 753	java/io/FileInputStream
    //   217: dup
    //   218: aload 10
    //   220: invokespecial 756	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   223: invokespecial 759	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   226: astore_3
    //   227: invokestatic 765	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   230: astore 4
    //   232: aload 4
    //   234: aload_3
    //   235: aconst_null
    //   236: invokeinterface 769 3 0
    //   241: aload_0
    //   242: aload 4
    //   244: aload 9
    //   246: invokespecial 771	com/android/server/pm/DefaultPermissionGrantPolicy:parse	(Lorg/xmlpull/v1/XmlPullParser;Ljava/util/Map;)V
    //   249: aload_3
    //   250: ifnull +7 -> 257
    //   253: aload_3
    //   254: invokevirtual 776	java/io/InputStream:close	()V
    //   257: aconst_null
    //   258: astore_3
    //   259: aload_3
    //   260: ifnull -107 -> 153
    //   263: aload_3
    //   264: athrow
    //   265: astore_3
    //   266: ldc 44
    //   268: new 349	java/lang/StringBuilder
    //   271: dup
    //   272: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   275: ldc_w 778
    //   278: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   281: aload 10
    //   283: invokevirtual 737	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   286: invokevirtual 363	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   289: aload_3
    //   290: invokestatic 781	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   293: pop
    //   294: goto -141 -> 153
    //   297: astore_3
    //   298: goto -39 -> 259
    //   301: astore_3
    //   302: aload_3
    //   303: athrow
    //   304: astore 4
    //   306: aload_3
    //   307: astore 6
    //   309: aload 5
    //   311: ifnull +11 -> 322
    //   314: aload 5
    //   316: invokevirtual 776	java/io/InputStream:close	()V
    //   319: aload_3
    //   320: astore 6
    //   322: aload 6
    //   324: ifnull +27 -> 351
    //   327: aload 6
    //   329: athrow
    //   330: aload_3
    //   331: astore 6
    //   333: aload_3
    //   334: aload 5
    //   336: if_acmpeq -14 -> 322
    //   339: aload_3
    //   340: aload 5
    //   342: invokevirtual 785	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   345: aload_3
    //   346: astore 6
    //   348: goto -26 -> 322
    //   351: aload 4
    //   353: athrow
    //   354: aload 9
    //   356: areturn
    //   357: astore 6
    //   359: aconst_null
    //   360: astore_3
    //   361: aload 4
    //   363: astore 5
    //   365: aload 6
    //   367: astore 4
    //   369: goto -63 -> 306
    //   372: astore 4
    //   374: aconst_null
    //   375: astore 6
    //   377: aload_3
    //   378: astore 5
    //   380: aload 6
    //   382: astore_3
    //   383: goto -77 -> 306
    //   386: astore 4
    //   388: aload_3
    //   389: astore 5
    //   391: aload 4
    //   393: astore_3
    //   394: goto -92 -> 302
    //   397: astore_3
    //   398: goto -132 -> 266
    //   401: astore 5
    //   403: aload_3
    //   404: ifnonnull -74 -> 330
    //   407: aload 5
    //   409: astore 6
    //   411: goto -89 -> 322
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	414	0	this	DefaultPermissionGrantPolicy
    //   83	74	1	i	int
    //   81	6	2	j	int
    //   226	38	3	localBufferedInputStream	java.io.BufferedInputStream
    //   265	25	3	localXmlPullParserException1	XmlPullParserException
    //   297	1	3	localThrowable1	Throwable
    //   301	45	3	localThrowable2	Throwable
    //   360	34	3	localObject1	Object
    //   397	7	3	localXmlPullParserException2	XmlPullParserException
    //   205	38	4	localXmlPullParser	XmlPullParser
    //   304	58	4	localObject2	Object
    //   367	1	4	localObject3	Object
    //   372	1	4	localObject4	Object
    //   386	6	4	localThrowable3	Throwable
    //   208	182	5	localObject5	Object
    //   401	7	5	localThrowable4	Throwable
    //   307	40	6	localThrowable5	Throwable
    //   357	9	6	localObject6	Object
    //   375	35	6	localThrowable6	Throwable
    //   13	123	7	localFile1	java.io.File
    //   44	46	8	arrayOfFile	java.io.File[]
    //   76	279	9	localArrayMap	ArrayMap
    //   93	189	10	localFile2	java.io.File
    // Exception table:
    //   from	to	target	type
    //   253	257	265	org/xmlpull/v1/XmlPullParserException
    //   253	257	265	java/io/IOException
    //   263	265	265	org/xmlpull/v1/XmlPullParserException
    //   263	265	265	java/io/IOException
    //   253	257	297	java/lang/Throwable
    //   210	227	301	java/lang/Throwable
    //   302	304	304	finally
    //   210	227	357	finally
    //   227	249	372	finally
    //   227	249	386	java/lang/Throwable
    //   314	319	397	org/xmlpull/v1/XmlPullParserException
    //   314	319	397	java/io/IOException
    //   327	330	397	org/xmlpull/v1/XmlPullParserException
    //   327	330	397	java/io/IOException
    //   339	345	397	org/xmlpull/v1/XmlPullParserException
    //   339	345	397	java/io/IOException
    //   351	354	397	org/xmlpull/v1/XmlPullParserException
    //   351	354	397	java/io/IOException
    //   314	319	401	java/lang/Throwable
  }
  
  public void grantDefaultPermissions(int paramInt)
  {
    grantPermissionsToSysComponentsAndPrivApps(paramInt);
    grantDefaultSystemHandlerPermissions(paramInt);
    grantDefaultPermissionExceptions(paramInt);
  }
  
  public void grantDefaultPermissionsToDefaultBrowserLPr(String paramString, int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to default browser for user:" + paramInt);
    if (paramString == null) {
      return;
    }
    paramString = getSystemPackageLPr(paramString);
    if ((paramString != null) && (doesPackageSupportRuntimePermissions(paramString))) {
      grantRuntimePermissionsLPw(paramString, LOCATION_PERMISSIONS, false, false, paramInt);
    }
  }
  
  public void grantDefaultPermissionsToDefaultDialerAppLPr(String paramString, int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to default dialer app for user:" + paramInt);
    if (paramString == null) {
      return;
    }
    paramString = getPackageLPr(paramString);
    if ((paramString != null) && (doesPackageSupportRuntimePermissions(paramString)))
    {
      grantRuntimePermissionsLPw(paramString, PHONE_PERMISSIONS, false, true, paramInt);
      grantRuntimePermissionsLPw(paramString, CONTACTS_PERMISSIONS, false, true, paramInt);
      grantRuntimePermissionsLPw(paramString, SMS_PERMISSIONS, false, true, paramInt);
      grantRuntimePermissionsLPw(paramString, MICROPHONE_PERMISSIONS, false, true, paramInt);
      grantRuntimePermissionsLPw(paramString, CAMERA_PERMISSIONS, false, true, paramInt);
    }
  }
  
  public void grantDefaultPermissionsToDefaultSimCallManagerLPr(String paramString, int paramInt)
  {
    if (paramString == null) {
      return;
    }
    paramString = getPackageLPr(paramString);
    if (paramString != null) {
      grantDefaultPermissionsToDefaultSimCallManagerLPr(paramString, paramInt);
    }
  }
  
  public void grantDefaultPermissionsToDefaultSmsAppLPr(String paramString, int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to default sms app for user:" + paramInt);
    if (paramString == null) {
      return;
    }
    paramString = getPackageLPr(paramString);
    if ((paramString != null) && (doesPackageSupportRuntimePermissions(paramString)))
    {
      grantRuntimePermissionsLPw(paramString, PHONE_PERMISSIONS, false, true, paramInt);
      grantRuntimePermissionsLPw(paramString, CONTACTS_PERMISSIONS, false, true, paramInt);
      grantRuntimePermissionsLPw(paramString, SMS_PERMISSIONS, false, true, paramInt);
    }
  }
  
  public void grantDefaultPermissionsToEnabledCarrierAppsLPr(String[] paramArrayOfString, int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to enabled carrier apps for user:" + paramInt);
    if (paramArrayOfString == null) {
      return;
    }
    int i = 0;
    int j = paramArrayOfString.length;
    while (i < j)
    {
      PackageParser.Package localPackage = getSystemPackageLPr(paramArrayOfString[i]);
      if ((localPackage != null) && (doesPackageSupportRuntimePermissions(localPackage)))
      {
        grantRuntimePermissionsLPw(localPackage, PHONE_PERMISSIONS, paramInt);
        grantRuntimePermissionsLPw(localPackage, LOCATION_PERMISSIONS, paramInt);
        grantRuntimePermissionsLPw(localPackage, SMS_PERMISSIONS, paramInt);
      }
      i += 1;
    }
  }
  
  public void grantSystemAppPermissions(int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to prebuilt system apps " + paramInt);
    for (;;)
    {
      int i;
      synchronized (this.mService.mPackages)
      {
        Iterator localIterator = this.mService.mPackages.values().iterator();
        if (localIterator.hasNext())
        {
          PackageParser.Package localPackage = (PackageParser.Package)localIterator.next();
          if ((localPackage.applicationInfo == null) || (!localPackage.applicationInfo.isSystemApp()) || (!doesPackageSupportRuntimePermissions(localPackage)) || (localPackage.requestedPermissions.isEmpty())) {
            continue;
          }
          ArraySet localArraySet = new ArraySet();
          int j = localPackage.requestedPermissions.size();
          i = 0;
          if (i < j)
          {
            String str = (String)localPackage.requestedPermissions.get(i);
            BasePermission localBasePermission = (BasePermission)this.mService.mSettings.mPermissions.get(str);
            if ((localBasePermission == null) || (!localBasePermission.isRuntime())) {
              break label236;
            }
            localArraySet.add(str);
            break label236;
          }
          if (localArraySet.isEmpty()) {
            continue;
          }
          grantRuntimePermissionsLPw(localPackage, localArraySet, false, true, paramInt);
        }
      }
      return;
      label236:
      i += 1;
    }
  }
  
  public void scheduleReadDefaultPermissionExceptions()
  {
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void setDialerAppPackagesProviderLPw(PackageManagerInternal.PackagesProvider paramPackagesProvider)
  {
    this.mDialerAppPackagesProvider = paramPackagesProvider;
  }
  
  public void setLocationPackagesProviderLPw(PackageManagerInternal.PackagesProvider paramPackagesProvider)
  {
    this.mLocationPackagesProvider = paramPackagesProvider;
  }
  
  public void setSimCallManagerPackagesProviderLPw(PackageManagerInternal.PackagesProvider paramPackagesProvider)
  {
    this.mSimCallManagerPackagesProvider = paramPackagesProvider;
  }
  
  public void setSmsAppPackagesProviderLPw(PackageManagerInternal.PackagesProvider paramPackagesProvider)
  {
    this.mSmsAppPackagesProvider = paramPackagesProvider;
  }
  
  public void setSyncAdapterPackagesProviderLPw(PackageManagerInternal.SyncAdapterPackagesProvider paramSyncAdapterPackagesProvider)
  {
    this.mSyncAdapterPackagesProvider = paramSyncAdapterPackagesProvider;
  }
  
  public void setVoiceInteractionPackagesProviderLPw(PackageManagerInternal.PackagesProvider paramPackagesProvider)
  {
    this.mVoiceInteractionPackagesProvider = paramPackagesProvider;
  }
  
  private static final class DefaultPermissionGrant
  {
    final boolean fixed;
    final String name;
    
    public DefaultPermissionGrant(String paramString, boolean paramBoolean)
    {
      this.name = paramString;
      this.fixed = paramBoolean;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/DefaultPermissionGrantPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */