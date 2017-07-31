package com.android.server.pm;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.EphemeralApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser.Package;
import android.content.pm.PackageUserState;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Environment;
import android.provider.Settings.Global;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import libcore.io.IoUtils;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class EphemeralApplicationRegistry
{
  private static final String ATTR_GRANTED = "granted";
  private static final String ATTR_LABEL = "label";
  private static final String ATTR_NAME = "name";
  private static final boolean DEBUG = false;
  private static final long DEFAULT_UNINSTALLED_EPHEMERAL_APP_CACHE_DURATION_MILLIS = 2592000000L;
  private static final boolean ENABLED = false;
  private static final String EPHEMERAL_APPS_FOLDER = "ephemeral";
  private static final String EPHEMERAL_APP_COOKIE_FILE_PREFIX = "cookie_";
  private static final String EPHEMERAL_APP_COOKIE_FILE_SIFFIX = ".dat";
  private static final String EPHEMERAL_APP_ICON_FILE = "icon.png";
  private static final String EPHEMERAL_APP_METADATA_FILE = "metadata.xml";
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
  private static final String LOG_TAG = "EphemeralAppRegistry";
  private static final String TAG_PACKAGE = "package";
  private static final String TAG_PERM = "perm";
  private static final String TAG_PERMS = "perms";
  private final PackageManagerService mService;
  @GuardedBy("mService.mPackages")
  private SparseArray<List<UninstalledEphemeralAppState>> mUninstalledEphemeralApps;
  
  public EphemeralApplicationRegistry(PackageManagerService paramPackageManagerService)
  {
    this.mService = paramPackageManagerService;
  }
  
  private void addUninstalledEphemeralAppLPw(PackageParser.Package paramPackage, int paramInt)
  {
    EphemeralApplicationInfo localEphemeralApplicationInfo = createEphemeralAppInfoForPackage(paramPackage, paramInt);
    if (localEphemeralApplicationInfo == null) {
      return;
    }
    if (this.mUninstalledEphemeralApps == null) {
      this.mUninstalledEphemeralApps = new SparseArray();
    }
    List localList = (List)this.mUninstalledEphemeralApps.get(paramInt);
    Object localObject = localList;
    if (localList == null)
    {
      localObject = new ArrayList();
      this.mUninstalledEphemeralApps.put(paramInt, localObject);
    }
    ((List)localObject).add(new UninstalledEphemeralAppState(localEphemeralApplicationInfo, System.currentTimeMillis()));
    writeUninstalledEphemeralAppMetadata(localEphemeralApplicationInfo, paramInt);
    writeEphemeralApplicationIconLPw(paramPackage, paramInt);
  }
  
  private static File computeEphemeralCookieFile(PackageParser.Package paramPackage, int paramInt)
  {
    return new File(getEphemeralApplicationDir(paramPackage.packageName, paramInt), "cookie_" + computePackageCertDigest(paramPackage) + ".dat");
  }
  
  private static String computePackageCertDigest(PackageParser.Package paramPackage)
  {
    try
    {
      Object localObject = MessageDigest.getInstance("SHA256");
      ((MessageDigest)localObject).update(paramPackage.mSignatures[0].toByteArray());
      paramPackage = ((MessageDigest)localObject).digest();
      int j = paramPackage.length;
      localObject = new char[j * 2];
      int i = 0;
      while (i < j)
      {
        int k = paramPackage[i] & 0xFF;
        localObject[(i * 2)] = HEX_ARRAY[(k >>> 4)];
        localObject[(i * 2 + 1)] = HEX_ARRAY[(k & 0xF)];
        i += 1;
      }
      return new String((char[])localObject);
    }
    catch (NoSuchAlgorithmException paramPackage)
    {
      return null;
    }
  }
  
  private EphemeralApplicationInfo createEphemeralAppInfoForPackage(PackageParser.Package paramPackage, int paramInt)
  {
    Object localObject2 = (PackageSetting)paramPackage.mExtras;
    if (localObject2 == null) {
      return null;
    }
    Object localObject1 = ((PackageSetting)localObject2).readUserState(paramInt);
    if ((localObject1 == null) || (!((PackageUserState)localObject1).installed) || (((PackageUserState)localObject1).hidden)) {
      return null;
    }
    localObject1 = new String[paramPackage.requestedPermissions.size()];
    paramPackage.requestedPermissions.toArray((Object[])localObject1);
    localObject2 = ((PackageSetting)localObject2).getPermissionsState().getPermissions(paramInt);
    String[] arrayOfString = new String[((Set)localObject2).size()];
    ((Set)localObject2).toArray(arrayOfString);
    return new EphemeralApplicationInfo(paramPackage.applicationInfo, (String[])localObject1, arrayOfString);
  }
  
  private static void deleteDir(File paramFile)
  {
    if (paramFile.listFiles() != null)
    {
      File[] arrayOfFile = paramFile.listFiles();
      int i = 0;
      int j = arrayOfFile.length;
      while (i < j)
      {
        deleteDir(arrayOfFile[i]);
        i += 1;
      }
    }
    paramFile.delete();
  }
  
  private static File getEphemeralApplicationDir(String paramString, int paramInt)
  {
    return new File(getEphemeralApplicationsDir(paramInt), paramString);
  }
  
  private static File getEphemeralApplicationsDir(int paramInt)
  {
    return new File(Environment.getUserSystemDirectory(paramInt), "ephemeral");
  }
  
  private List<EphemeralApplicationInfo> getInstalledEphemeralApplicationsLPr(int paramInt)
  {
    Object localObject1 = null;
    int j = this.mService.mPackages.size();
    int i = 0;
    if (i < j)
    {
      Object localObject2 = (PackageParser.Package)this.mService.mPackages.valueAt(i);
      if (!((PackageParser.Package)localObject2).applicationInfo.isEphemeralApp()) {
        localObject2 = localObject1;
      }
      for (;;)
      {
        i += 1;
        localObject1 = localObject2;
        break;
        EphemeralApplicationInfo localEphemeralApplicationInfo = createEphemeralAppInfoForPackage((PackageParser.Package)localObject2, paramInt);
        localObject2 = localObject1;
        if (localEphemeralApplicationInfo != null)
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          ((List)localObject2).add(localEphemeralApplicationInfo);
        }
      }
    }
    return (List<EphemeralApplicationInfo>)localObject1;
  }
  
  private EphemeralApplicationInfo getOrParseUninstalledEphemeralAppInfo(String paramString, int paramInt)
  {
    if (this.mUninstalledEphemeralApps != null)
    {
      List localList = (List)this.mUninstalledEphemeralApps.get(paramInt);
      if (localList != null)
      {
        int j = localList.size();
        int i = 0;
        while (i < j)
        {
          UninstalledEphemeralAppState localUninstalledEphemeralAppState = (UninstalledEphemeralAppState)localList.get(i);
          if (localUninstalledEphemeralAppState.mEphemeralApplicationInfo.getPackageName().equals(paramString)) {
            return localUninstalledEphemeralAppState.mEphemeralApplicationInfo;
          }
          i += 1;
        }
      }
    }
    paramString = parseMetadataFile(new File(getEphemeralApplicationDir(paramString, paramInt), "metadata.xml"));
    if (paramString == null) {
      return null;
    }
    return paramString.mEphemeralApplicationInfo;
  }
  
  private List<UninstalledEphemeralAppState> getUninstalledEphemeralAppStatesLPr(int paramInt)
  {
    Object localObject1 = null;
    if (this.mUninstalledEphemeralApps != null)
    {
      localObject2 = (List)this.mUninstalledEphemeralApps.get(paramInt);
      localObject1 = localObject2;
      if (localObject2 != null) {
        return (List<UninstalledEphemeralAppState>)localObject2;
      }
    }
    Object localObject3 = getEphemeralApplicationsDir(paramInt);
    Object localObject2 = localObject1;
    if (((File)localObject3).exists())
    {
      localObject3 = ((File)localObject3).listFiles();
      localObject2 = localObject1;
      if (localObject3 != null)
      {
        int i = 0;
        int j = localObject3.length;
        localObject2 = localObject1;
        if (i < j)
        {
          localObject2 = localObject3[i];
          if (!((File)localObject2).isDirectory()) {
            localObject2 = localObject1;
          }
          for (;;)
          {
            i += 1;
            localObject1 = localObject2;
            break;
            UninstalledEphemeralAppState localUninstalledEphemeralAppState = parseMetadataFile(new File((File)localObject2, "metadata.xml"));
            localObject2 = localObject1;
            if (localUninstalledEphemeralAppState != null)
            {
              localObject2 = localObject1;
              if (localObject1 == null) {
                localObject2 = new ArrayList();
              }
              ((List)localObject2).add(localUninstalledEphemeralAppState);
            }
          }
        }
      }
    }
    if (localObject2 != null)
    {
      if (this.mUninstalledEphemeralApps == null) {
        this.mUninstalledEphemeralApps = new SparseArray();
      }
      this.mUninstalledEphemeralApps.put(paramInt, localObject2);
    }
    return (List<UninstalledEphemeralAppState>)localObject2;
  }
  
  private List<EphemeralApplicationInfo> getUninstalledEphemeralApplicationsLPr(int paramInt)
  {
    List localList = getUninstalledEphemeralAppStatesLPr(paramInt);
    if ((localList == null) || (localList.isEmpty())) {
      return Collections.emptyList();
    }
    ArrayList localArrayList = new ArrayList();
    int i = localList.size();
    paramInt = 0;
    while (paramInt < i)
    {
      localArrayList.add(((UninstalledEphemeralAppState)localList.get(paramInt)).mEphemeralApplicationInfo);
      paramInt += 1;
    }
    return localArrayList;
  }
  
  private static boolean isValidCookie(Context paramContext, byte[] paramArrayOfByte)
  {
    if (ArrayUtils.isEmpty(paramArrayOfByte)) {
      return true;
    }
    return paramArrayOfByte.length <= paramContext.getPackageManager().getEphemeralCookieMaxSizeBytes();
  }
  
  private static EphemeralApplicationInfo parseMetadata(XmlPullParser paramXmlPullParser, String paramString)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
      if ("package".equals(paramXmlPullParser.getName())) {
        return parsePackage(paramXmlPullParser, paramString);
      }
    }
    return null;
  }
  
  /* Error */
  private static UninstalledEphemeralAppState parseMetadataFile(File paramFile)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 303	java/io/File:exists	()Z
    //   4: ifne +5 -> 9
    //   7: aconst_null
    //   8: areturn
    //   9: new 365	android/util/AtomicFile
    //   12: dup
    //   13: aload_0
    //   14: invokespecial 367	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   17: invokevirtual 371	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   20: astore_3
    //   21: aload_0
    //   22: invokevirtual 375	java/io/File:getParentFile	()Ljava/io/File;
    //   25: astore 4
    //   27: aload_0
    //   28: invokevirtual 378	java/io/File:lastModified	()J
    //   31: lstore_1
    //   32: aload 4
    //   34: invokevirtual 379	java/io/File:getName	()Ljava/lang/String;
    //   37: astore 4
    //   39: invokestatic 385	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   42: astore 5
    //   44: aload 5
    //   46: aload_3
    //   47: getstatic 391	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   50: invokevirtual 395	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   53: invokeinterface 399 3 0
    //   58: new 6	com/android/server/pm/EphemeralApplicationRegistry$UninstalledEphemeralAppState
    //   61: dup
    //   62: aload 5
    //   64: aload 4
    //   66: invokestatic 401	com/android/server/pm/EphemeralApplicationRegistry:parseMetadata	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)Landroid/content/pm/EphemeralApplicationInfo;
    //   69: lload_1
    //   70: invokespecial 114	com/android/server/pm/EphemeralApplicationRegistry$UninstalledEphemeralAppState:<init>	(Landroid/content/pm/EphemeralApplicationInfo;J)V
    //   73: astore 4
    //   75: aload_3
    //   76: invokestatic 407	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   79: aload 4
    //   81: areturn
    //   82: astore_0
    //   83: ldc 45
    //   85: ldc_w 409
    //   88: invokestatic 415	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   91: pop
    //   92: aconst_null
    //   93: areturn
    //   94: astore 4
    //   96: new 417	java/lang/IllegalStateException
    //   99: dup
    //   100: new 140	java/lang/StringBuilder
    //   103: dup
    //   104: invokespecial 141	java/lang/StringBuilder:<init>	()V
    //   107: ldc_w 419
    //   110: invokevirtual 145	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   113: aload_0
    //   114: invokevirtual 422	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   117: invokevirtual 153	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   120: aload 4
    //   122: invokespecial 425	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   125: athrow
    //   126: astore_0
    //   127: aload_3
    //   128: invokestatic 407	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   131: aload_0
    //   132: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	133	0	paramFile	File
    //   31	39	1	l	long
    //   20	108	3	localFileInputStream	java.io.FileInputStream
    //   25	55	4	localObject	Object
    //   94	27	4	localXmlPullParserException	XmlPullParserException
    //   42	21	5	localXmlPullParser	XmlPullParser
    // Exception table:
    //   from	to	target	type
    //   9	21	82	java/io/FileNotFoundException
    //   39	75	94	org/xmlpull/v1/XmlPullParserException
    //   39	75	94	java/io/IOException
    //   39	75	126	finally
    //   96	126	126	finally
  }
  
  private static EphemeralApplicationInfo parsePackage(XmlPullParser paramXmlPullParser, String paramString)
    throws IOException, XmlPullParserException
  {
    String str = paramXmlPullParser.getAttributeValue(null, "label");
    Object localObject = new ArrayList();
    ArrayList localArrayList = new ArrayList();
    int i = paramXmlPullParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
      if ("perms".equals(paramXmlPullParser.getName())) {
        parsePermissions(paramXmlPullParser, (List)localObject, localArrayList);
      }
    }
    paramXmlPullParser = new String[((List)localObject).size()];
    ((List)localObject).toArray(paramXmlPullParser);
    localObject = new String[localArrayList.size()];
    localArrayList.toArray((Object[])localObject);
    return new EphemeralApplicationInfo(paramString, str, paramXmlPullParser, (String[])localObject);
  }
  
  private static void parsePermissions(XmlPullParser paramXmlPullParser, List<String> paramList1, List<String> paramList2)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
      if ("perm".equals(paramXmlPullParser.getName()))
      {
        String str = XmlUtils.readStringAttribute(paramXmlPullParser, "name");
        paramList1.add(str);
        if (XmlUtils.readBooleanAttribute(paramXmlPullParser, "granted")) {
          paramList2.add(str);
        }
      }
    }
  }
  
  private static File peekEphemeralCookieFile(String paramString, int paramInt)
  {
    paramString = getEphemeralApplicationDir(paramString, paramInt);
    if (!paramString.exists()) {
      return null;
    }
    paramString = paramString.listFiles();
    paramInt = 0;
    int i = paramString.length;
    while (paramInt < i)
    {
      File localFile = paramString[paramInt];
      if ((!localFile.isDirectory()) && (localFile.getName().startsWith("cookie_")) && (localFile.getName().endsWith(".dat"))) {
        return localFile;
      }
      paramInt += 1;
    }
    return null;
  }
  
  private void propagateEphemeralAppPermissionsIfNeeded(PackageParser.Package paramPackage, int paramInt)
  {
    Object localObject = getOrParseUninstalledEphemeralAppInfo(paramPackage.packageName, paramInt);
    if (localObject == null) {
      return;
    }
    if (ArrayUtils.isEmpty(((EphemeralApplicationInfo)localObject).getGrantedPermissions())) {
      return;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      localObject = ((EphemeralApplicationInfo)localObject).getGrantedPermissions();
      int i = 0;
      int j = localObject.length;
      while (i < j)
      {
        String str = localObject[i];
        this.mService.grantRuntimePermission(paramPackage.packageName, str, paramInt);
        i += 1;
      }
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void pruneUninstalledEphemeralAppsLPw(int paramInt)
  {
    long l = Settings.Global.getLong(this.mService.mContext.getContentResolver(), "uninstalled_ephemeral_app_cache_duration_millis", 2592000000L);
    UninstalledEphemeralAppState localUninstalledEphemeralAppState;
    if (this.mUninstalledEphemeralApps != null)
    {
      localObject = (List)this.mUninstalledEphemeralApps.get(paramInt);
      if (localObject != null)
      {
        i = ((List)localObject).size() - 1;
        while (i >= 0)
        {
          localUninstalledEphemeralAppState = (UninstalledEphemeralAppState)((List)localObject).get(i);
          if (System.currentTimeMillis() - localUninstalledEphemeralAppState.mTimestamp > l) {
            ((List)localObject).remove(i);
          }
          i -= 1;
        }
        if (((List)localObject).isEmpty()) {
          this.mUninstalledEphemeralApps.remove(paramInt);
        }
      }
    }
    Object localObject = getEphemeralApplicationsDir(paramInt);
    if (!((File)localObject).exists()) {
      return;
    }
    localObject = ((File)localObject).listFiles();
    if (localObject == null) {
      return;
    }
    paramInt = 0;
    int i = localObject.length;
    if (paramInt < i)
    {
      localUninstalledEphemeralAppState = localObject[paramInt];
      if (!localUninstalledEphemeralAppState.isDirectory()) {}
      for (;;)
      {
        paramInt += 1;
        break;
        File localFile = new File(localUninstalledEphemeralAppState, "metadata.xml");
        if ((localFile.exists()) && (System.currentTimeMillis() - localFile.lastModified() > l)) {
          deleteDir(localUninstalledEphemeralAppState);
        }
      }
    }
  }
  
  /* Error */
  private void writeEphemeralApplicationIconLPw(PackageParser.Package paramPackage, int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 5
    //   6: aload_1
    //   7: getfield 134	android/content/pm/PackageParser$Package:packageName	Ljava/lang/String;
    //   10: iload_2
    //   11: invokestatic 138	com/android/server/pm/EphemeralApplicationRegistry:getEphemeralApplicationDir	(Ljava/lang/String;I)Ljava/io/File;
    //   14: invokevirtual 303	java/io/File:exists	()Z
    //   17: ifne +4 -> 21
    //   20: return
    //   21: aload_1
    //   22: getfield 236	android/content/pm/PackageParser$Package:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   25: aload_0
    //   26: getfield 81	com/android/server/pm/EphemeralApplicationRegistry:mService	Lcom/android/server/pm/PackageManagerService;
    //   29: getfield 483	com/android/server/pm/PackageManagerService:mContext	Landroid/content/Context;
    //   32: invokevirtual 332	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   35: invokevirtual 511	android/content/pm/ApplicationInfo:loadIcon	(Landroid/content/pm/PackageManager;)Landroid/graphics/drawable/Drawable;
    //   38: astore 6
    //   40: aload 6
    //   42: instanceof 513
    //   45: ifeq +88 -> 133
    //   48: aload 6
    //   50: checkcast 513	android/graphics/drawable/BitmapDrawable
    //   53: invokevirtual 517	android/graphics/drawable/BitmapDrawable:getBitmap	()Landroid/graphics/Bitmap;
    //   56: astore_3
    //   57: new 129	java/io/File
    //   60: dup
    //   61: aload_1
    //   62: getfield 134	android/content/pm/PackageParser$Package:packageName	Ljava/lang/String;
    //   65: iload_2
    //   66: invokestatic 138	com/android/server/pm/EphemeralApplicationRegistry:getEphemeralApplicationDir	(Ljava/lang/String;I)Ljava/io/File;
    //   69: ldc 37
    //   71: invokespecial 156	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   74: astore_1
    //   75: aconst_null
    //   76: astore 7
    //   78: aconst_null
    //   79: astore 6
    //   81: new 519	java/io/FileOutputStream
    //   84: dup
    //   85: aload_1
    //   86: invokespecial 520	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   89: astore_1
    //   90: aload_3
    //   91: getstatic 526	android/graphics/Bitmap$CompressFormat:PNG	Landroid/graphics/Bitmap$CompressFormat;
    //   94: bipush 100
    //   96: aload_1
    //   97: invokevirtual 532	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   100: pop
    //   101: aload 5
    //   103: astore_3
    //   104: aload_1
    //   105: ifnull +10 -> 115
    //   108: aload_1
    //   109: invokevirtual 535	java/io/FileOutputStream:close	()V
    //   112: aload 5
    //   114: astore_3
    //   115: aload_3
    //   116: ifnull +54 -> 170
    //   119: aload_3
    //   120: athrow
    //   121: astore_1
    //   122: ldc 45
    //   124: ldc_w 537
    //   127: aload_1
    //   128: invokestatic 541	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   131: pop
    //   132: return
    //   133: aload 6
    //   135: invokevirtual 546	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   138: aload 6
    //   140: invokevirtual 549	android/graphics/drawable/Drawable:getIntrinsicHeight	()I
    //   143: getstatic 555	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   146: invokestatic 559	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   149: astore_3
    //   150: aload 6
    //   152: new 561	android/graphics/Canvas
    //   155: dup
    //   156: aload_3
    //   157: invokespecial 564	android/graphics/Canvas:<init>	(Landroid/graphics/Bitmap;)V
    //   160: invokevirtual 568	android/graphics/drawable/Drawable:draw	(Landroid/graphics/Canvas;)V
    //   163: goto -106 -> 57
    //   166: astore_3
    //   167: goto -52 -> 115
    //   170: return
    //   171: astore_3
    //   172: aload 6
    //   174: astore_1
    //   175: aload_3
    //   176: athrow
    //   177: astore 5
    //   179: aload_3
    //   180: astore 4
    //   182: aload 5
    //   184: astore_3
    //   185: aload 4
    //   187: astore 5
    //   189: aload_1
    //   190: ifnull +11 -> 201
    //   193: aload_1
    //   194: invokevirtual 535	java/io/FileOutputStream:close	()V
    //   197: aload 4
    //   199: astore 5
    //   201: aload 5
    //   203: ifnull +29 -> 232
    //   206: aload 5
    //   208: athrow
    //   209: aload 4
    //   211: astore 5
    //   213: aload 4
    //   215: aload_1
    //   216: if_acmpeq -15 -> 201
    //   219: aload 4
    //   221: aload_1
    //   222: invokevirtual 572	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   225: aload 4
    //   227: astore 5
    //   229: goto -28 -> 201
    //   232: aload_3
    //   233: athrow
    //   234: astore_3
    //   235: aload 7
    //   237: astore_1
    //   238: goto -53 -> 185
    //   241: astore_3
    //   242: goto -57 -> 185
    //   245: astore_3
    //   246: goto -71 -> 175
    //   249: astore_1
    //   250: goto -128 -> 122
    //   253: astore_1
    //   254: aload 4
    //   256: ifnonnull -47 -> 209
    //   259: aload_1
    //   260: astore 5
    //   262: goto -61 -> 201
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	265	0	this	EphemeralApplicationRegistry
    //   0	265	1	paramPackage	PackageParser.Package
    //   0	265	2	paramInt	int
    //   56	101	3	localObject1	Object
    //   166	1	3	localThrowable1	Throwable
    //   171	9	3	localThrowable2	Throwable
    //   184	49	3	localObject2	Object
    //   234	1	3	localObject3	Object
    //   241	1	3	localObject4	Object
    //   245	1	3	localThrowable3	Throwable
    //   1	254	4	localObject5	Object
    //   4	109	5	localObject6	Object
    //   177	6	5	localObject7	Object
    //   187	74	5	localObject8	Object
    //   38	135	6	localDrawable	android.graphics.drawable.Drawable
    //   76	160	7	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   108	112	121	java/lang/Exception
    //   119	121	121	java/lang/Exception
    //   108	112	166	java/lang/Throwable
    //   81	90	171	java/lang/Throwable
    //   175	177	177	finally
    //   81	90	234	finally
    //   90	101	241	finally
    //   90	101	245	java/lang/Throwable
    //   193	197	249	java/lang/Exception
    //   206	209	249	java/lang/Exception
    //   219	225	249	java/lang/Exception
    //   232	234	249	java/lang/Exception
    //   193	197	253	java/lang/Throwable
  }
  
  private void writeUninstalledEphemeralAppMetadata(EphemeralApplicationInfo paramEphemeralApplicationInfo, int paramInt)
  {
    Object localObject1 = getEphemeralApplicationDir(paramEphemeralApplicationInfo.getPackageName(), paramInt);
    if ((((File)localObject1).exists()) || (((File)localObject1).mkdirs()))
    {
      AtomicFile localAtomicFile = new AtomicFile(new File((File)localObject1, "metadata.xml"));
      Object localObject2 = null;
      localObject1 = null;
      try
      {
        FileOutputStream localFileOutputStream = localAtomicFile.startWrite();
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        XmlSerializer localXmlSerializer = Xml.newSerializer();
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        localXmlSerializer.setOutput(localFileOutputStream, StandardCharsets.UTF_8.name());
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        localXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        localXmlSerializer.startDocument(null, Boolean.valueOf(true));
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        localXmlSerializer.startTag(null, "package");
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        localXmlSerializer.attribute(null, "label", paramEphemeralApplicationInfo.loadLabel(this.mService.mContext.getPackageManager()).toString());
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        localXmlSerializer.startTag(null, "perms");
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        String[] arrayOfString = paramEphemeralApplicationInfo.getRequestedPermissions();
        paramInt = 0;
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        int i = arrayOfString.length;
        while (paramInt < i)
        {
          String str = arrayOfString[paramInt];
          localObject1 = localFileOutputStream;
          localObject2 = localFileOutputStream;
          localXmlSerializer.startTag(null, "perm");
          localObject1 = localFileOutputStream;
          localObject2 = localFileOutputStream;
          localXmlSerializer.attribute(null, "name", str);
          localObject1 = localFileOutputStream;
          localObject2 = localFileOutputStream;
          if (ArrayUtils.contains(paramEphemeralApplicationInfo.getGrantedPermissions(), str))
          {
            localObject1 = localFileOutputStream;
            localObject2 = localFileOutputStream;
            localXmlSerializer.attribute(null, "granted", String.valueOf(true));
          }
          localObject1 = localFileOutputStream;
          localObject2 = localFileOutputStream;
          localXmlSerializer.endTag(null, "perm");
          paramInt += 1;
        }
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        localXmlSerializer.endTag(null, "perms");
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        localXmlSerializer.endTag(null, "package");
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        localXmlSerializer.endDocument();
        localObject1 = localFileOutputStream;
        localObject2 = localFileOutputStream;
        localAtomicFile.finishWrite(localFileOutputStream);
        IoUtils.closeQuietly(localFileOutputStream);
        return;
      }
      catch (Throwable paramEphemeralApplicationInfo)
      {
        localObject2 = localObject1;
        Slog.wtf("EphemeralAppRegistry", "Failed to write ephemeral state, restoring backup", paramEphemeralApplicationInfo);
        localObject2 = localObject1;
        localAtomicFile.failWrite((FileOutputStream)localObject1);
        return;
      }
      finally
      {
        IoUtils.closeQuietly((AutoCloseable)localObject2);
      }
    }
  }
  
  public byte[] getEphemeralApplicationCookieLPw(String paramString, int paramInt)
  {
    return EmptyArray.BYTE;
  }
  
  public Bitmap getEphemeralApplicationIconLPw(String paramString, int paramInt)
  {
    return null;
  }
  
  public List<EphemeralApplicationInfo> getEphemeralApplicationsLPw(int paramInt)
  {
    return Collections.emptyList();
  }
  
  public void onPackageInstalledLPw(PackageParser.Package paramPackage) {}
  
  public void onPackageUninstalledLPw(PackageParser.Package paramPackage) {}
  
  public void onUserRemovedLPw(int paramInt) {}
  
  public boolean setEphemeralApplicationCookieLPw(String paramString, byte[] paramArrayOfByte, int paramInt)
  {
    return false;
  }
  
  private static final class UninstalledEphemeralAppState
  {
    final EphemeralApplicationInfo mEphemeralApplicationInfo;
    final long mTimestamp;
    
    public UninstalledEphemeralAppState(EphemeralApplicationInfo paramEphemeralApplicationInfo, long paramLong)
    {
      this.mEphemeralApplicationInfo = paramEphemeralApplicationInfo;
      this.mTimestamp = paramLong;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/EphemeralApplicationRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */