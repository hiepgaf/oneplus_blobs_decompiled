package android.content.pm;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.PatternMatcher;
import android.os.Trace;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Pair;
import android.util.Slog;
import android.util.TypedValue;
import android.util.apk.ApkSignatureSchemeV2Verifier;
import android.util.jar.StrictJarFile;
import com.android.internal.R.styleable;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class PackageParser
{
  private static final String ANDROID_MANIFEST_FILENAME = "AndroidManifest.xml";
  private static final String ANDROID_RESOURCES = "http://schemas.android.com/apk/res/android";
  public static final int APK_SIGNING_UNKNOWN = 0;
  public static final int APK_SIGNING_V1 = 1;
  public static final int APK_SIGNING_V2 = 2;
  private static final Set<String> CHILD_PACKAGE_TAGS = new ArraySet();
  private static final boolean DEBUG_BACKUP = false;
  private static final boolean DEBUG_JAR = false;
  private static final boolean DEBUG_PARSER = false;
  private static final int MAX_PACKAGES_PER_APK = 5;
  private static final String MNT_EXPAND = "/mnt/expand/";
  private static final boolean MULTI_PACKAGE_APK_ENABLED = false;
  public static final NewPermissionInfo[] NEW_PERMISSIONS;
  public static final int PARSE_CHATTY = 2;
  public static final int PARSE_COLLECT_CERTIFICATES = 256;
  private static final int PARSE_DEFAULT_INSTALL_LOCATION = -1;
  public static final int PARSE_ENFORCE_CODE = 1024;
  public static final int PARSE_EXTERNAL_STORAGE = 32;
  public static final int PARSE_FORCE_SDK = 4096;
  public static final int PARSE_FORWARD_LOCK = 16;
  public static final int PARSE_IGNORE_PROCESSES = 8;
  public static final int PARSE_IS_EPHEMERAL = 2048;
  public static final int PARSE_IS_PRIVILEGED = 128;
  public static final int PARSE_IS_SYSTEM = 1;
  public static final int PARSE_IS_SYSTEM_DIR = 64;
  public static final int PARSE_MUST_BE_APK = 4;
  public static final int PARSE_TRUSTED_OVERLAY = 512;
  private static final boolean RIGID_PARSER = false;
  private static final String[] SDK_CODENAMES;
  private static final int SDK_VERSION;
  public static final SplitPermissionInfo[] SPLIT_PERMISSIONS;
  private static final String TAG = "PackageParser";
  private static final String TAG_ADOPT_PERMISSIONS = "adopt-permissions";
  private static final String TAG_APPLICATION = "application";
  private static final String TAG_COMPATIBLE_SCREENS = "compatible-screens";
  private static final String TAG_EAT_COMMENT = "eat-comment";
  private static final String TAG_FEATURE_GROUP = "feature-group";
  private static final String TAG_INSTRUMENTATION = "instrumentation";
  private static final String TAG_KEY_SETS = "key-sets";
  private static final String TAG_MANIFEST = "manifest";
  private static final String TAG_ORIGINAL_PACKAGE = "original-package";
  private static final String TAG_OVERLAY = "overlay";
  private static final String TAG_PACKAGE = "package";
  private static final String TAG_PERMISSION = "permission";
  private static final String TAG_PERMISSION_GROUP = "permission-group";
  private static final String TAG_PERMISSION_TREE = "permission-tree";
  private static final String TAG_PROTECTED_BROADCAST = "protected-broadcast";
  private static final String TAG_RESTRICT_UPDATE = "restrict-update";
  private static final String TAG_SUPPORTS_INPUT = "supports-input";
  private static final String TAG_SUPPORT_SCREENS = "supports-screens";
  private static final String TAG_USES_CONFIGURATION = "uses-configuration";
  private static final String TAG_USES_FEATURE = "uses-feature";
  private static final String TAG_USES_GL_TEXTURE = "uses-gl-texture";
  private static final String TAG_USES_PERMISSION = "uses-permission";
  private static final String TAG_USES_PERMISSION_SDK_23 = "uses-permission-sdk-23";
  private static final String TAG_USES_PERMISSION_SDK_M = "uses-permission-sdk-m";
  private static final String TAG_USES_SDK = "uses-sdk";
  private static AtomicReference<byte[]> sBuffer = new AtomicReference();
  private static boolean sCompatibilityModeEnabled;
  private static final Comparator<String> sSplitNameComparator;
  @Deprecated
  private String mArchiveSourcePath;
  private Context mContext;
  private DisplayMetrics mMetrics = new DisplayMetrics();
  private boolean mOnlyCoreApps;
  private boolean mOnlyPowerOffAlarmApps;
  private ParseComponentArgs mParseActivityAliasArgs;
  private ParseComponentArgs mParseActivityArgs;
  private int mParseError = 1;
  private ParsePackageItemArgs mParseInstrumentationArgs;
  private ParseComponentArgs mParseProviderArgs;
  private ParseComponentArgs mParseServiceArgs;
  private String[] mSeparateProcesses;
  
  static
  {
    CHILD_PACKAGE_TAGS.add("application");
    CHILD_PACKAGE_TAGS.add("uses-permission");
    CHILD_PACKAGE_TAGS.add("uses-permission-sdk-m");
    CHILD_PACKAGE_TAGS.add("uses-permission-sdk-23");
    CHILD_PACKAGE_TAGS.add("uses-configuration");
    CHILD_PACKAGE_TAGS.add("uses-feature");
    CHILD_PACKAGE_TAGS.add("feature-group");
    CHILD_PACKAGE_TAGS.add("uses-sdk");
    CHILD_PACKAGE_TAGS.add("supports-screens");
    CHILD_PACKAGE_TAGS.add("instrumentation");
    CHILD_PACKAGE_TAGS.add("uses-gl-texture");
    CHILD_PACKAGE_TAGS.add("compatible-screens");
    CHILD_PACKAGE_TAGS.add("supports-input");
    CHILD_PACKAGE_TAGS.add("eat-comment");
    NEW_PERMISSIONS = new NewPermissionInfo[] { new NewPermissionInfo("android.permission.WRITE_EXTERNAL_STORAGE", 4, 0), new NewPermissionInfo("android.permission.READ_PHONE_STATE", 4, 0) };
    SPLIT_PERMISSIONS = new SplitPermissionInfo[] { new SplitPermissionInfo("android.permission.WRITE_EXTERNAL_STORAGE", new String[] { "android.permission.READ_EXTERNAL_STORAGE" }, 10001), new SplitPermissionInfo("android.permission.READ_CONTACTS", new String[] { "android.permission.READ_CALL_LOG" }, 16), new SplitPermissionInfo("android.permission.WRITE_CONTACTS", new String[] { "android.permission.WRITE_CALL_LOG" }, 16) };
    SDK_VERSION = Build.VERSION.SDK_INT;
    SDK_CODENAMES = Build.VERSION.ACTIVE_CODENAMES;
    sCompatibilityModeEnabled = true;
    sSplitNameComparator = new SplitNameComparator(null);
  }
  
  public PackageParser()
  {
    this.mMetrics.setToDefaults();
  }
  
  public PackageParser(Context paramContext)
  {
    this();
    this.mContext = paramContext;
  }
  
  private static String buildClassName(String paramString, CharSequence paramCharSequence, String[] paramArrayOfString)
  {
    if ((paramCharSequence == null) || (paramCharSequence.length() <= 0))
    {
      paramArrayOfString[0] = ("Empty class name in package " + paramString);
      return null;
    }
    paramCharSequence = paramCharSequence.toString();
    if (paramCharSequence.charAt(0) == '.') {
      return (paramString + paramCharSequence).intern();
    }
    if (paramCharSequence.indexOf('.') < 0)
    {
      paramString = new StringBuilder(paramString);
      paramString.append('.');
      paramString.append(paramCharSequence);
      return paramString.toString().intern();
    }
    return paramCharSequence.intern();
  }
  
  private static String buildCompoundName(String paramString1, CharSequence paramCharSequence, String paramString2, String[] paramArrayOfString)
  {
    paramCharSequence = paramCharSequence.toString();
    int i = paramCharSequence.charAt(0);
    if ((paramString1 != null) && (i == 58))
    {
      if (paramCharSequence.length() < 2)
      {
        paramArrayOfString[0] = ("Bad " + paramString2 + " name " + paramCharSequence + " in package " + paramString1 + ": must be at least two characters");
        return null;
      }
      str = validateName(paramCharSequence.substring(1), false, false);
      if (str != null)
      {
        paramArrayOfString[0] = ("Invalid " + paramString2 + " name " + paramCharSequence + " in package " + paramString1 + ": " + str);
        return null;
      }
      return (paramString1 + paramCharSequence).intern();
    }
    String str = validateName(paramCharSequence, true, false);
    if ((str == null) || ("system".equals(paramCharSequence))) {
      return paramCharSequence.intern();
    }
    paramArrayOfString[0] = ("Invalid " + paramString2 + " name " + paramCharSequence + " in package " + paramString1 + ": " + str);
    return null;
  }
  
  private static String buildProcessName(String paramString1, String paramString2, CharSequence paramCharSequence, int paramInt, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    if (((paramInt & 0x8) == 0) || ("system".equals(paramCharSequence)))
    {
      if (paramArrayOfString1 != null) {
        paramInt = paramArrayOfString1.length - 1;
      }
    }
    else {
      while (paramInt >= 0)
      {
        String str = paramArrayOfString1[paramInt];
        if ((str.equals(paramString1)) || (str.equals(paramString2)) || (str.equals(paramCharSequence)))
        {
          return paramString1;
          if (paramString2 != null) {
            return paramString2;
          }
          return paramString1;
        }
        paramInt -= 1;
      }
    }
    if ((paramCharSequence == null) || (paramCharSequence.length() <= 0)) {
      return paramString2;
    }
    return buildCompoundName(paramString1, paramCharSequence, "process", paramArrayOfString2);
  }
  
  private static String buildTaskAffinityName(String paramString1, String paramString2, CharSequence paramCharSequence, String[] paramArrayOfString)
  {
    if (paramCharSequence == null) {
      return paramString2;
    }
    if (paramCharSequence.length() <= 0) {
      return null;
    }
    return buildCompoundName(paramString1, paramCharSequence, "taskAffinity", paramArrayOfString);
  }
  
  private static boolean checkUseInstalledOrHidden(int paramInt, PackageUserState paramPackageUserState)
  {
    return ((paramPackageUserState.installed) && (!paramPackageUserState.hidden)) || ((paramInt & 0x2000) != 0);
  }
  
  public static void closeQuietly(StrictJarFile paramStrictJarFile)
  {
    if (paramStrictJarFile != null) {}
    try
    {
      paramStrictJarFile.close();
      return;
    }
    catch (Exception paramStrictJarFile) {}
  }
  
  public static void collectCertificates(Package paramPackage, int paramInt)
    throws PackageParser.PackageParserException
  {
    collectCertificatesInternal(paramPackage, paramInt);
    if (paramPackage.childPackages != null) {}
    for (paramInt = paramPackage.childPackages.size();; paramInt = 0)
    {
      int i = 0;
      while (i < paramInt)
      {
        Package localPackage = (Package)paramPackage.childPackages.get(i);
        localPackage.mCertificates = paramPackage.mCertificates;
        localPackage.mSignatures = paramPackage.mSignatures;
        localPackage.mSigningKeys = paramPackage.mSigningKeys;
        i += 1;
      }
    }
  }
  
  /* Error */
  private static void collectCertificates(Package paramPackage, File paramFile, int paramInt)
    throws PackageParser.PackageParserException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 457	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   4: astore 11
    //   6: iconst_0
    //   7: istore_3
    //   8: aconst_null
    //   9: astore 10
    //   11: aconst_null
    //   12: astore 8
    //   14: aconst_null
    //   15: astore 9
    //   17: aload 8
    //   19: astore_1
    //   20: aload 10
    //   22: astore 7
    //   24: ldc2_w 458
    //   27: ldc_w 461
    //   30: invokestatic 467	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   33: aload 8
    //   35: astore_1
    //   36: aload 10
    //   38: astore 7
    //   40: aload 11
    //   42: invokestatic 473	android/util/apk/ApkSignatureSchemeV2Verifier:verify	(Ljava/lang/String;)[[Ljava/security/cert/X509Certificate;
    //   45: astore 8
    //   47: aload 8
    //   49: astore_1
    //   50: aload 8
    //   52: astore 7
    //   54: aload 8
    //   56: invokestatic 477	android/content/pm/PackageParser:convertToSignatures	([[Ljava/security/cert/Certificate;)[Landroid/content/pm/Signature;
    //   59: astore 10
    //   61: aload 10
    //   63: astore 7
    //   65: iconst_1
    //   66: istore_3
    //   67: ldc2_w 458
    //   70: invokestatic 481	android/os/Trace:traceEnd	(J)V
    //   73: aload 8
    //   75: astore_1
    //   76: iload_3
    //   77: ifeq +266 -> 343
    //   80: aload_0
    //   81: getfield 434	android/content/pm/PackageParser$Package:mCertificates	[[Ljava/security/cert/Certificate;
    //   84: ifnonnull +216 -> 300
    //   87: aload_0
    //   88: aload_1
    //   89: putfield 434	android/content/pm/PackageParser$Package:mCertificates	[[Ljava/security/cert/Certificate;
    //   92: aload_0
    //   93: aload 7
    //   95: putfield 438	android/content/pm/PackageParser$Package:mSignatures	[Landroid/content/pm/Signature;
    //   98: aload_0
    //   99: new 242	android/util/ArraySet
    //   102: dup
    //   103: aload_1
    //   104: arraylength
    //   105: invokespecial 484	android/util/ArraySet:<init>	(I)V
    //   108: putfield 442	android/content/pm/PackageParser$Package:mSigningKeys	Landroid/util/ArraySet;
    //   111: iconst_0
    //   112: istore 4
    //   114: iload 4
    //   116: aload_1
    //   117: arraylength
    //   118: if_icmpge +225 -> 343
    //   121: aload_1
    //   122: iload 4
    //   124: aaload
    //   125: iconst_0
    //   126: aaload
    //   127: astore 7
    //   129: aload_0
    //   130: getfield 442	android/content/pm/PackageParser$Package:mSigningKeys	Landroid/util/ArraySet;
    //   133: aload 7
    //   135: invokevirtual 490	java/security/cert/Certificate:getPublicKey	()Ljava/security/PublicKey;
    //   138: invokevirtual 491	android/util/ArraySet:add	(Ljava/lang/Object;)Z
    //   141: pop
    //   142: iload 4
    //   144: iconst_1
    //   145: iadd
    //   146: istore 4
    //   148: goto -34 -> 114
    //   151: astore 7
    //   153: aload_0
    //   154: getfield 495	android/content/pm/PackageParser$Package:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   157: getfield 500	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   160: ifeq +15 -> 175
    //   163: aload_0
    //   164: getfield 495	android/content/pm/PackageParser$Package:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   167: getfield 500	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   170: bipush 24
    //   172: if_icmplt +51 -> 223
    //   175: new 33	android/content/pm/PackageParser$PackageParserException
    //   178: dup
    //   179: bipush -103
    //   181: new 328	java/lang/StringBuilder
    //   184: dup
    //   185: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   188: ldc_w 502
    //   191: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   194: aload 11
    //   196: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   199: ldc_w 504
    //   202: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   205: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   208: aload 7
    //   210: invokespecial 507	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;Ljava/lang/Throwable;)V
    //   213: athrow
    //   214: astore_0
    //   215: ldc2_w 458
    //   218: invokestatic 481	android/os/Trace:traceEnd	(J)V
    //   221: aload_0
    //   222: athrow
    //   223: ldc -128
    //   225: ldc_w 509
    //   228: new 33	android/content/pm/PackageParser$PackageParserException
    //   231: dup
    //   232: bipush -103
    //   234: new 328	java/lang/StringBuilder
    //   237: dup
    //   238: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   241: ldc_w 502
    //   244: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   247: aload 11
    //   249: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   252: ldc_w 504
    //   255: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   258: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   261: aload 7
    //   263: invokespecial 507	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;Ljava/lang/Throwable;)V
    //   266: invokestatic 515	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   269: pop
    //   270: ldc2_w 458
    //   273: invokestatic 481	android/os/Trace:traceEnd	(J)V
    //   276: aload 9
    //   278: astore 7
    //   280: goto -204 -> 76
    //   283: astore_1
    //   284: ldc2_w 458
    //   287: invokestatic 481	android/os/Trace:traceEnd	(J)V
    //   290: aload 7
    //   292: astore_1
    //   293: aload 9
    //   295: astore 7
    //   297: goto -221 -> 76
    //   300: aload_0
    //   301: getfield 438	android/content/pm/PackageParser$Package:mSignatures	[Landroid/content/pm/Signature;
    //   304: aload 7
    //   306: invokestatic 521	android/content/pm/Signature:areExactMatch	([Landroid/content/pm/Signature;[Landroid/content/pm/Signature;)Z
    //   309: ifne +34 -> 343
    //   312: new 33	android/content/pm/PackageParser$PackageParserException
    //   315: dup
    //   316: bipush -104
    //   318: new 328	java/lang/StringBuilder
    //   321: dup
    //   322: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   325: aload 11
    //   327: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   330: ldc_w 523
    //   333: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   336: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   339: invokespecial 526	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;)V
    //   342: athrow
    //   343: aconst_null
    //   344: astore 7
    //   346: aconst_null
    //   347: astore 9
    //   349: aconst_null
    //   350: astore 8
    //   352: aload 7
    //   354: astore_1
    //   355: ldc2_w 458
    //   358: ldc_w 528
    //   361: invokestatic 467	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   364: iload_2
    //   365: bipush 64
    //   367: iand
    //   368: ifne +188 -> 556
    //   371: aload 7
    //   373: astore_1
    //   374: aload_0
    //   375: getfield 495	android/content/pm/PackageParser$Package:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   378: getfield 500	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   381: ifeq +163 -> 544
    //   384: aload 7
    //   386: astore_1
    //   387: aload_0
    //   388: getfield 495	android/content/pm/PackageParser$Package:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   391: getfield 500	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   394: bipush 24
    //   396: if_icmplt +154 -> 550
    //   399: iconst_1
    //   400: istore 5
    //   402: iload 5
    //   404: ifne +15 -> 419
    //   407: aload 7
    //   409: astore_1
    //   410: ldc -128
    //   412: ldc_w 530
    //   415: invokestatic 533	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   418: pop
    //   419: iload_3
    //   420: ifeq +142 -> 562
    //   423: iconst_0
    //   424: istore 6
    //   426: aload 7
    //   428: astore_1
    //   429: new 409	android/util/jar/StrictJarFile
    //   432: dup
    //   433: aload 11
    //   435: iload 6
    //   437: iload 5
    //   439: invokespecial 536	android/util/jar/StrictJarFile:<init>	(Ljava/lang/String;ZZ)V
    //   442: astore 7
    //   444: ldc2_w 458
    //   447: invokestatic 481	android/os/Trace:traceEnd	(J)V
    //   450: aload 7
    //   452: ldc 68
    //   454: invokevirtual 540	android/util/jar/StrictJarFile:findEntry	(Ljava/lang/String;)Ljava/util/zip/ZipEntry;
    //   457: astore 8
    //   459: aload 8
    //   461: ifnonnull +107 -> 568
    //   464: new 33	android/content/pm/PackageParser$PackageParserException
    //   467: dup
    //   468: bipush -101
    //   470: new 328	java/lang/StringBuilder
    //   473: dup
    //   474: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   477: ldc_w 542
    //   480: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   483: aload 11
    //   485: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   488: ldc_w 544
    //   491: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   494: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   497: invokespecial 526	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;)V
    //   500: athrow
    //   501: astore_0
    //   502: aload 7
    //   504: astore_1
    //   505: new 33	android/content/pm/PackageParser$PackageParserException
    //   508: dup
    //   509: bipush -105
    //   511: new 328	java/lang/StringBuilder
    //   514: dup
    //   515: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   518: ldc_w 502
    //   521: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   524: aload 11
    //   526: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   529: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   532: aload_0
    //   533: invokespecial 507	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;Ljava/lang/Throwable;)V
    //   536: athrow
    //   537: astore_0
    //   538: aload_1
    //   539: invokestatic 546	android/content/pm/PackageParser:closeQuietly	(Landroid/util/jar/StrictJarFile;)V
    //   542: aload_0
    //   543: athrow
    //   544: iconst_1
    //   545: istore 5
    //   547: goto -145 -> 402
    //   550: iconst_0
    //   551: istore 5
    //   553: goto -151 -> 402
    //   556: iconst_0
    //   557: istore 5
    //   559: goto -157 -> 402
    //   562: iconst_1
    //   563: istore 6
    //   565: goto -139 -> 426
    //   568: iload_3
    //   569: ifeq +9 -> 578
    //   572: aload 7
    //   574: invokestatic 546	android/content/pm/PackageParser:closeQuietly	(Landroid/util/jar/StrictJarFile;)V
    //   577: return
    //   578: ldc2_w 458
    //   581: ldc_w 548
    //   584: invokestatic 467	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   587: new 423	java/util/ArrayList
    //   590: dup
    //   591: invokespecial 549	java/util/ArrayList:<init>	()V
    //   594: astore_1
    //   595: aload_1
    //   596: aload 8
    //   598: invokeinterface 552 2 0
    //   603: pop
    //   604: iload_2
    //   605: bipush 64
    //   607: iand
    //   608: ifne +116 -> 724
    //   611: aload 7
    //   613: invokevirtual 556	android/util/jar/StrictJarFile:iterator	()Ljava/util/Iterator;
    //   616: astore 8
    //   618: aload 8
    //   620: invokeinterface 562 1 0
    //   625: ifeq +99 -> 724
    //   628: aload 8
    //   630: invokeinterface 566 1 0
    //   635: checkcast 568	java/util/zip/ZipEntry
    //   638: astore 9
    //   640: aload 9
    //   642: invokevirtual 571	java/util/zip/ZipEntry:isDirectory	()Z
    //   645: ifne -27 -> 618
    //   648: aload 9
    //   650: invokevirtual 574	java/util/zip/ZipEntry:getName	()Ljava/lang/String;
    //   653: astore 10
    //   655: aload 10
    //   657: ldc_w 576
    //   660: invokevirtual 580	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   663: ifne -45 -> 618
    //   666: aload 10
    //   668: ldc 68
    //   670: invokevirtual 385	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   673: ifne -55 -> 618
    //   676: aload_1
    //   677: aload 9
    //   679: invokeinterface 552 2 0
    //   684: pop
    //   685: goto -67 -> 618
    //   688: astore_0
    //   689: aload 7
    //   691: astore_1
    //   692: new 33	android/content/pm/PackageParser$PackageParserException
    //   695: dup
    //   696: bipush -103
    //   698: new 328	java/lang/StringBuilder
    //   701: dup
    //   702: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   705: ldc_w 502
    //   708: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   711: aload 11
    //   713: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   716: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   719: aload_0
    //   720: invokespecial 507	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;Ljava/lang/Throwable;)V
    //   723: athrow
    //   724: aload_1
    //   725: invokeinterface 583 1 0
    //   730: astore_1
    //   731: aload_1
    //   732: invokeinterface 562 1 0
    //   737: ifeq +203 -> 940
    //   740: aload_1
    //   741: invokeinterface 566 1 0
    //   746: checkcast 568	java/util/zip/ZipEntry
    //   749: astore 8
    //   751: aload 7
    //   753: aload 8
    //   755: invokestatic 587	android/content/pm/PackageParser:loadCertificates	(Landroid/util/jar/StrictJarFile;Ljava/util/zip/ZipEntry;)[[Ljava/security/cert/Certificate;
    //   758: astore 9
    //   760: aload 9
    //   762: invokestatic 593	com/android/internal/util/ArrayUtils:isEmpty	([Ljava/lang/Object;)Z
    //   765: ifeq +48 -> 813
    //   768: new 33	android/content/pm/PackageParser$PackageParserException
    //   771: dup
    //   772: bipush -103
    //   774: new 328	java/lang/StringBuilder
    //   777: dup
    //   778: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   781: ldc_w 542
    //   784: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   787: aload 11
    //   789: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   792: ldc_w 595
    //   795: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   798: aload 8
    //   800: invokevirtual 574	java/util/zip/ZipEntry:getName	()Ljava/lang/String;
    //   803: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   806: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   809: invokespecial 526	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;)V
    //   812: athrow
    //   813: aload 9
    //   815: invokestatic 477	android/content/pm/PackageParser:convertToSignatures	([[Ljava/security/cert/Certificate;)[Landroid/content/pm/Signature;
    //   818: astore 10
    //   820: aload_0
    //   821: getfield 434	android/content/pm/PackageParser$Package:mCertificates	[[Ljava/security/cert/Certificate;
    //   824: ifnonnull +59 -> 883
    //   827: aload_0
    //   828: aload 9
    //   830: putfield 434	android/content/pm/PackageParser$Package:mCertificates	[[Ljava/security/cert/Certificate;
    //   833: aload_0
    //   834: aload 10
    //   836: putfield 438	android/content/pm/PackageParser$Package:mSignatures	[Landroid/content/pm/Signature;
    //   839: aload_0
    //   840: new 242	android/util/ArraySet
    //   843: dup
    //   844: invokespecial 245	android/util/ArraySet:<init>	()V
    //   847: putfield 442	android/content/pm/PackageParser$Package:mSigningKeys	Landroid/util/ArraySet;
    //   850: iconst_0
    //   851: istore_2
    //   852: iload_2
    //   853: aload 9
    //   855: arraylength
    //   856: if_icmpge -125 -> 731
    //   859: aload_0
    //   860: getfield 442	android/content/pm/PackageParser$Package:mSigningKeys	Landroid/util/ArraySet;
    //   863: aload 9
    //   865: iload_2
    //   866: aaload
    //   867: iconst_0
    //   868: aaload
    //   869: invokevirtual 490	java/security/cert/Certificate:getPublicKey	()Ljava/security/PublicKey;
    //   872: invokevirtual 491	android/util/ArraySet:add	(Ljava/lang/Object;)Z
    //   875: pop
    //   876: iload_2
    //   877: iconst_1
    //   878: iadd
    //   879: istore_2
    //   880: goto -28 -> 852
    //   883: aload_0
    //   884: getfield 438	android/content/pm/PackageParser$Package:mSignatures	[Landroid/content/pm/Signature;
    //   887: aload 10
    //   889: invokestatic 521	android/content/pm/Signature:areExactMatch	([Landroid/content/pm/Signature;[Landroid/content/pm/Signature;)Z
    //   892: ifne -161 -> 731
    //   895: new 33	android/content/pm/PackageParser$PackageParserException
    //   898: dup
    //   899: bipush -104
    //   901: new 328	java/lang/StringBuilder
    //   904: dup
    //   905: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   908: ldc_w 542
    //   911: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   914: aload 11
    //   916: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   919: ldc_w 597
    //   922: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   925: aload 8
    //   927: invokevirtual 574	java/util/zip/ZipEntry:getName	()Ljava/lang/String;
    //   930: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   933: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   936: invokespecial 526	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;)V
    //   939: athrow
    //   940: ldc2_w 458
    //   943: invokestatic 481	android/os/Trace:traceEnd	(J)V
    //   946: aload 7
    //   948: invokestatic 546	android/content/pm/PackageParser:closeQuietly	(Landroid/util/jar/StrictJarFile;)V
    //   951: return
    //   952: astore_0
    //   953: aload 8
    //   955: astore_1
    //   956: goto -451 -> 505
    //   959: astore_0
    //   960: aload 9
    //   962: astore_1
    //   963: goto -271 -> 692
    //   966: astore_0
    //   967: aload 7
    //   969: astore_1
    //   970: goto -432 -> 538
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	973	0	paramPackage	Package
    //   0	973	1	paramFile	File
    //   0	973	2	paramInt	int
    //   7	562	3	i	int
    //   112	35	4	j	int
    //   400	158	5	bool1	boolean
    //   424	140	6	bool2	boolean
    //   22	112	7	localObject1	Object
    //   151	111	7	localException	Exception
    //   278	690	7	localObject2	Object
    //   12	942	8	localObject3	Object
    //   15	946	9	localObject4	Object
    //   9	879	10	localObject5	Object
    //   4	911	11	str	String
    // Exception table:
    //   from	to	target	type
    //   24	33	151	java/lang/Exception
    //   40	47	151	java/lang/Exception
    //   54	61	151	java/lang/Exception
    //   24	33	214	finally
    //   40	47	214	finally
    //   54	61	214	finally
    //   153	175	214	finally
    //   175	214	214	finally
    //   223	270	214	finally
    //   24	33	283	android/util/apk/ApkSignatureSchemeV2Verifier$SignatureNotFoundException
    //   40	47	283	android/util/apk/ApkSignatureSchemeV2Verifier$SignatureNotFoundException
    //   54	61	283	android/util/apk/ApkSignatureSchemeV2Verifier$SignatureNotFoundException
    //   444	459	501	java/security/GeneralSecurityException
    //   464	501	501	java/security/GeneralSecurityException
    //   578	604	501	java/security/GeneralSecurityException
    //   611	618	501	java/security/GeneralSecurityException
    //   618	685	501	java/security/GeneralSecurityException
    //   724	731	501	java/security/GeneralSecurityException
    //   731	813	501	java/security/GeneralSecurityException
    //   813	850	501	java/security/GeneralSecurityException
    //   852	876	501	java/security/GeneralSecurityException
    //   883	940	501	java/security/GeneralSecurityException
    //   940	946	501	java/security/GeneralSecurityException
    //   355	364	537	finally
    //   374	384	537	finally
    //   387	399	537	finally
    //   410	419	537	finally
    //   429	444	537	finally
    //   505	537	537	finally
    //   692	724	537	finally
    //   444	459	688	java/io/IOException
    //   444	459	688	java/lang/RuntimeException
    //   464	501	688	java/io/IOException
    //   464	501	688	java/lang/RuntimeException
    //   578	604	688	java/io/IOException
    //   578	604	688	java/lang/RuntimeException
    //   611	618	688	java/io/IOException
    //   611	618	688	java/lang/RuntimeException
    //   618	685	688	java/io/IOException
    //   618	685	688	java/lang/RuntimeException
    //   724	731	688	java/io/IOException
    //   724	731	688	java/lang/RuntimeException
    //   731	813	688	java/io/IOException
    //   731	813	688	java/lang/RuntimeException
    //   813	850	688	java/io/IOException
    //   813	850	688	java/lang/RuntimeException
    //   852	876	688	java/io/IOException
    //   852	876	688	java/lang/RuntimeException
    //   883	940	688	java/io/IOException
    //   883	940	688	java/lang/RuntimeException
    //   940	946	688	java/io/IOException
    //   940	946	688	java/lang/RuntimeException
    //   355	364	952	java/security/GeneralSecurityException
    //   374	384	952	java/security/GeneralSecurityException
    //   387	399	952	java/security/GeneralSecurityException
    //   410	419	952	java/security/GeneralSecurityException
    //   429	444	952	java/security/GeneralSecurityException
    //   355	364	959	java/io/IOException
    //   355	364	959	java/lang/RuntimeException
    //   374	384	959	java/io/IOException
    //   374	384	959	java/lang/RuntimeException
    //   387	399	959	java/io/IOException
    //   387	399	959	java/lang/RuntimeException
    //   410	419	959	java/io/IOException
    //   410	419	959	java/lang/RuntimeException
    //   429	444	959	java/io/IOException
    //   429	444	959	java/lang/RuntimeException
    //   444	459	966	finally
    //   464	501	966	finally
    //   578	604	966	finally
    //   611	618	966	finally
    //   618	685	966	finally
    //   724	731	966	finally
    //   731	813	966	finally
    //   813	850	966	finally
    //   852	876	966	finally
    //   883	940	966	finally
    //   940	946	966	finally
  }
  
  private static void collectCertificatesInternal(Package paramPackage, int paramInt)
    throws PackageParser.PackageParserException
  {
    paramPackage.mCertificates = null;
    paramPackage.mSignatures = null;
    paramPackage.mSigningKeys = null;
    Trace.traceBegin(262144L, "collectCertificates");
    try
    {
      collectCertificates(paramPackage, new File(paramPackage.baseCodePath), paramInt);
      if (!ArrayUtils.isEmpty(paramPackage.splitCodePaths))
      {
        int i = 0;
        while (i < paramPackage.splitCodePaths.length)
        {
          collectCertificates(paramPackage, new File(paramPackage.splitCodePaths[i]), paramInt);
          i += 1;
        }
      }
      return;
    }
    finally
    {
      Trace.traceEnd(262144L);
    }
  }
  
  private static Signature[] convertToSignatures(Certificate[][] paramArrayOfCertificate)
    throws CertificateEncodingException
  {
    Signature[] arrayOfSignature = new Signature[paramArrayOfCertificate.length];
    int i = 0;
    while (i < paramArrayOfCertificate.length)
    {
      arrayOfSignature[i] = new Signature(paramArrayOfCertificate[i]);
      i += 1;
    }
    return arrayOfSignature;
  }
  
  private static boolean copyNeeded(int paramInt1, Package paramPackage, PackageUserState paramPackageUserState, Bundle paramBundle, int paramInt2)
  {
    if (paramInt2 != 0) {
      return true;
    }
    if (paramPackageUserState.enabled != 0)
    {
      if (paramPackageUserState.enabled == 1) {}
      for (i = 1; paramPackage.applicationInfo.enabled != i; i = 0) {
        return true;
      }
    }
    if ((paramPackage.applicationInfo.flags & 0x40000000) != 0) {}
    for (int i = 1; paramPackageUserState.suspended != i; i = 0) {
      return true;
    }
    if ((!paramPackageUserState.installed) || (paramPackageUserState.hidden)) {
      return true;
    }
    if (paramPackageUserState.stopped) {
      return true;
    }
    if (((paramInt1 & 0x80) != 0) && ((paramBundle != null) || (paramPackage.mAppMetaData != null))) {
      return true;
    }
    return ((paramInt1 & 0x400) != 0) && (paramPackage.usesLibraryFiles != null);
  }
  
  public static final ActivityInfo generateActivityInfo(ActivityInfo paramActivityInfo, int paramInt1, PackageUserState paramPackageUserState, int paramInt2)
  {
    if (paramActivityInfo == null) {
      return null;
    }
    if (!checkUseInstalledOrHidden(paramInt1, paramPackageUserState)) {
      return null;
    }
    paramActivityInfo = new ActivityInfo(paramActivityInfo);
    paramActivityInfo.applicationInfo = generateApplicationInfo(paramActivityInfo.applicationInfo, paramInt1, paramPackageUserState, paramInt2);
    return paramActivityInfo;
  }
  
  public static final ActivityInfo generateActivityInfo(Activity paramActivity, int paramInt1, PackageUserState paramPackageUserState, int paramInt2)
  {
    if (paramActivity == null) {
      return null;
    }
    if (!checkUseInstalledOrHidden(paramInt1, paramPackageUserState)) {
      return null;
    }
    if (!copyNeeded(paramInt1, paramActivity.owner, paramPackageUserState, paramActivity.metaData, paramInt2)) {
      return paramActivity.info;
    }
    ActivityInfo localActivityInfo = new ActivityInfo(paramActivity.info);
    localActivityInfo.metaData = paramActivity.metaData;
    localActivityInfo.applicationInfo = generateApplicationInfo(paramActivity.owner, paramInt1, paramPackageUserState, paramInt2);
    return localActivityInfo;
  }
  
  public static ApplicationInfo generateApplicationInfo(ApplicationInfo paramApplicationInfo, int paramInt1, PackageUserState paramPackageUserState, int paramInt2)
  {
    if (paramApplicationInfo == null) {
      return null;
    }
    if (!checkUseInstalledOrHidden(paramInt1, paramPackageUserState)) {
      return null;
    }
    paramApplicationInfo = new ApplicationInfo(paramApplicationInfo);
    paramApplicationInfo.initForUser(paramInt2);
    if (paramPackageUserState.stopped) {}
    for (paramApplicationInfo.flags |= 0x200000;; paramApplicationInfo.flags &= 0xFFDFFFFF)
    {
      updateApplicationInfo(paramApplicationInfo, paramInt1, paramPackageUserState);
      return paramApplicationInfo;
    }
  }
  
  public static ApplicationInfo generateApplicationInfo(Package paramPackage, int paramInt, PackageUserState paramPackageUserState)
  {
    return generateApplicationInfo(paramPackage, paramInt, paramPackageUserState, UserHandle.getCallingUserId());
  }
  
  public static ApplicationInfo generateApplicationInfo(Package paramPackage, int paramInt1, PackageUserState paramPackageUserState, int paramInt2)
  {
    if (paramPackage == null) {
      return null;
    }
    if ((checkUseInstalledOrHidden(paramInt1, paramPackageUserState)) && (paramPackage.isMatch(paramInt1)))
    {
      if ((!copyNeeded(paramInt1, paramPackage, paramPackageUserState, null, paramInt2)) && (((0x8000 & paramInt1) == 0) || (paramPackageUserState.enabled != 4)))
      {
        updateApplicationInfo(paramPackage.applicationInfo, paramInt1, paramPackageUserState);
        return paramPackage.applicationInfo;
      }
    }
    else {
      return null;
    }
    ApplicationInfo localApplicationInfo = new ApplicationInfo(paramPackage.applicationInfo);
    localApplicationInfo.initForUser(paramInt2);
    if ((paramInt1 & 0x80) != 0) {
      localApplicationInfo.metaData = paramPackage.mAppMetaData;
    }
    if ((paramInt1 & 0x400) != 0) {
      localApplicationInfo.sharedLibraryFiles = paramPackage.usesLibraryFiles;
    }
    if (paramPackageUserState.stopped) {}
    for (localApplicationInfo.flags |= 0x200000;; localApplicationInfo.flags &= 0xFFDFFFFF)
    {
      updateApplicationInfo(localApplicationInfo, paramInt1, paramPackageUserState);
      return localApplicationInfo;
    }
  }
  
  public static final InstrumentationInfo generateInstrumentationInfo(Instrumentation paramInstrumentation, int paramInt)
  {
    if (paramInstrumentation == null) {
      return null;
    }
    if ((paramInt & 0x80) == 0) {
      return paramInstrumentation.info;
    }
    InstrumentationInfo localInstrumentationInfo = new InstrumentationInfo(paramInstrumentation.info);
    localInstrumentationInfo.metaData = paramInstrumentation.metaData;
    return localInstrumentationInfo;
  }
  
  public static PackageInfo generatePackageInfo(Package paramPackage, int[] paramArrayOfInt, int paramInt, long paramLong1, long paramLong2, Set<String> paramSet, PackageUserState paramPackageUserState)
  {
    return generatePackageInfo(paramPackage, paramArrayOfInt, paramInt, paramLong1, paramLong2, paramSet, paramPackageUserState, UserHandle.getCallingUserId());
  }
  
  public static PackageInfo generatePackageInfo(Package paramPackage, int[] paramArrayOfInt, int paramInt1, long paramLong1, long paramLong2, Set<String> paramSet, PackageUserState paramPackageUserState, int paramInt2)
  {
    PackageInfo localPackageInfo;
    int i;
    label289:
    label333:
    int m;
    int j;
    label393:
    Object localObject;
    int k;
    if ((checkUseInstalledOrHidden(paramInt1, paramPackageUserState)) && (paramPackage.isMatch(paramInt1)))
    {
      localPackageInfo = new PackageInfo();
      localPackageInfo.packageName = paramPackage.packageName;
      localPackageInfo.splitNames = paramPackage.splitNames;
      localPackageInfo.versionCode = paramPackage.mVersionCode;
      localPackageInfo.baseRevisionCode = paramPackage.baseRevisionCode;
      localPackageInfo.splitRevisionCodes = paramPackage.splitRevisionCodes;
      localPackageInfo.versionName = paramPackage.mVersionName;
      localPackageInfo.sharedUserId = paramPackage.mSharedUserId;
      localPackageInfo.sharedUserLabel = paramPackage.mSharedUserLabel;
      localPackageInfo.applicationInfo = generateApplicationInfo(paramPackage, paramInt1, paramPackageUserState, paramInt2);
      localPackageInfo.installLocation = paramPackage.installLocation;
      localPackageInfo.coreApp = paramPackage.coreApp;
      if (((localPackageInfo.applicationInfo.flags & 0x1) != 0) || ((localPackageInfo.applicationInfo.flags & 0x80) != 0)) {
        localPackageInfo.requiredForAllUsers = paramPackage.mRequiredForAllUsers;
      }
      localPackageInfo.restrictedAccountType = paramPackage.mRestrictedAccountType;
      localPackageInfo.requiredAccountType = paramPackage.mRequiredAccountType;
      localPackageInfo.overlayTarget = paramPackage.mOverlayTarget;
      localPackageInfo.firstInstallTime = paramLong1;
      localPackageInfo.lastUpdateTime = paramLong2;
      if ((paramInt1 & 0x100) != 0) {
        localPackageInfo.gids = paramArrayOfInt;
      }
      if ((paramInt1 & 0x4000) != 0)
      {
        if (paramPackage.configPreferences == null) {
          break label463;
        }
        i = paramPackage.configPreferences.size();
        if (i > 0)
        {
          localPackageInfo.configPreferences = new ConfigurationInfo[i];
          paramPackage.configPreferences.toArray(localPackageInfo.configPreferences);
        }
        if (paramPackage.reqFeatures == null) {
          break label469;
        }
        i = paramPackage.reqFeatures.size();
        if (i > 0)
        {
          localPackageInfo.reqFeatures = new FeatureInfo[i];
          paramPackage.reqFeatures.toArray(localPackageInfo.reqFeatures);
        }
        if (paramPackage.featureGroups == null) {
          break label475;
        }
        i = paramPackage.featureGroups.size();
        if (i > 0)
        {
          localPackageInfo.featureGroups = new FeatureGroupInfo[i];
          paramPackage.featureGroups.toArray(localPackageInfo.featureGroups);
        }
      }
      if ((paramInt1 & 0x1) == 0) {
        break label495;
      }
      m = paramPackage.activities.size();
      if (m <= 0) {
        break label495;
      }
      paramArrayOfInt = new ActivityInfo[m];
      j = 0;
      i = 0;
      if (j >= m) {
        break label481;
      }
      localObject = (Activity)paramPackage.activities.get(j);
      if (!paramPackageUserState.isMatch(((Activity)localObject).info, paramInt1)) {
        break label1177;
      }
      k = i + 1;
      paramArrayOfInt[i] = generateActivityInfo((Activity)localObject, paramInt1, paramPackageUserState, paramInt2);
      i = k;
    }
    label463:
    label469:
    label475:
    label481:
    label495:
    label1163:
    label1168:
    label1171:
    label1174:
    label1177:
    for (;;)
    {
      j += 1;
      break label393;
      return null;
      i = 0;
      break;
      i = 0;
      break label289;
      i = 0;
      break label333;
      localPackageInfo.activities = ((ActivityInfo[])ArrayUtils.trimToSize(paramArrayOfInt, i));
      if ((paramInt1 & 0x2) != 0)
      {
        m = paramPackage.receivers.size();
        if (m > 0)
        {
          paramArrayOfInt = new ActivityInfo[m];
          j = 0;
          i = 0;
          if (j < m)
          {
            localObject = (Activity)paramPackage.receivers.get(j);
            if (!paramPackageUserState.isMatch(((Activity)localObject).info, paramInt1)) {
              break label1174;
            }
            k = i + 1;
            paramArrayOfInt[i] = generateActivityInfo((Activity)localObject, paramInt1, paramPackageUserState, paramInt2);
            i = k;
          }
        }
      }
      for (;;)
      {
        j += 1;
        break;
        localPackageInfo.receivers = ((ActivityInfo[])ArrayUtils.trimToSize(paramArrayOfInt, i));
        if ((paramInt1 & 0x4) != 0)
        {
          m = paramPackage.services.size();
          if (m > 0)
          {
            paramArrayOfInt = new ServiceInfo[m];
            j = 0;
            i = 0;
            if (j < m)
            {
              localObject = (Service)paramPackage.services.get(j);
              if (!paramPackageUserState.isMatch(((Service)localObject).info, paramInt1)) {
                break label1171;
              }
              k = i + 1;
              paramArrayOfInt[i] = generateServiceInfo((Service)localObject, paramInt1, paramPackageUserState, paramInt2);
              i = k;
            }
          }
        }
        for (;;)
        {
          j += 1;
          break;
          localPackageInfo.services = ((ServiceInfo[])ArrayUtils.trimToSize(paramArrayOfInt, i));
          if ((paramInt1 & 0x8) != 0)
          {
            m = paramPackage.providers.size();
            if (m > 0)
            {
              paramArrayOfInt = new ProviderInfo[m];
              j = 0;
              i = 0;
              if (j < m)
              {
                localObject = (Provider)paramPackage.providers.get(j);
                if (!paramPackageUserState.isMatch(((Provider)localObject).info, paramInt1)) {
                  break label1168;
                }
                k = i + 1;
                paramArrayOfInt[i] = generateProviderInfo((Provider)localObject, paramInt1, paramPackageUserState, paramInt2);
                i = k;
              }
            }
          }
          for (;;)
          {
            j += 1;
            break;
            localPackageInfo.providers = ((ProviderInfo[])ArrayUtils.trimToSize(paramArrayOfInt, i));
            if ((paramInt1 & 0x10) != 0)
            {
              i = paramPackage.instrumentation.size();
              if (i > 0)
              {
                localPackageInfo.instrumentation = new InstrumentationInfo[i];
                paramInt2 = 0;
                while (paramInt2 < i)
                {
                  localPackageInfo.instrumentation[paramInt2] = generateInstrumentationInfo((Instrumentation)paramPackage.instrumentation.get(paramInt2), paramInt1);
                  paramInt2 += 1;
                }
              }
            }
            if ((paramInt1 & 0x1000) != 0)
            {
              i = paramPackage.permissions.size();
              if (i > 0)
              {
                localPackageInfo.permissions = new PermissionInfo[i];
                paramInt2 = 0;
                while (paramInt2 < i)
                {
                  localPackageInfo.permissions[paramInt2] = generatePermissionInfo((Permission)paramPackage.permissions.get(paramInt2), paramInt1);
                  paramInt2 += 1;
                }
              }
              i = paramPackage.requestedPermissions.size();
              if (i > 0)
              {
                localPackageInfo.requestedPermissions = new String[i];
                localPackageInfo.requestedPermissionsFlags = new int[i];
                paramInt2 = 0;
                while (paramInt2 < i)
                {
                  paramArrayOfInt = (String)paramPackage.requestedPermissions.get(paramInt2);
                  localPackageInfo.requestedPermissions[paramInt2] = paramArrayOfInt;
                  paramPackageUserState = localPackageInfo.requestedPermissionsFlags;
                  paramPackageUserState[paramInt2] |= 0x1;
                  if ((paramSet != null) && (paramSet.contains(paramArrayOfInt)))
                  {
                    paramArrayOfInt = localPackageInfo.requestedPermissionsFlags;
                    paramArrayOfInt[paramInt2] |= 0x2;
                  }
                  paramInt2 += 1;
                }
              }
            }
            if ((paramInt1 & 0x40) != 0) {
              if (paramPackage.mSignatures == null) {
                break label1163;
              }
            }
            for (paramInt1 = paramPackage.mSignatures.length;; paramInt1 = 0)
            {
              if (paramInt1 > 0)
              {
                localPackageInfo.signatures = new Signature[paramInt1];
                System.arraycopy(paramPackage.mSignatures, 0, localPackageInfo.signatures, 0, paramInt1);
              }
              return localPackageInfo;
            }
          }
        }
      }
    }
  }
  
  public static final PermissionGroupInfo generatePermissionGroupInfo(PermissionGroup paramPermissionGroup, int paramInt)
  {
    if (paramPermissionGroup == null) {
      return null;
    }
    if ((paramInt & 0x80) == 0) {
      return paramPermissionGroup.info;
    }
    PermissionGroupInfo localPermissionGroupInfo = new PermissionGroupInfo(paramPermissionGroup.info);
    localPermissionGroupInfo.metaData = paramPermissionGroup.metaData;
    return localPermissionGroupInfo;
  }
  
  public static final PermissionInfo generatePermissionInfo(Permission paramPermission, int paramInt)
  {
    if (paramPermission == null) {
      return null;
    }
    if ((paramInt & 0x80) == 0) {
      return paramPermission.info;
    }
    PermissionInfo localPermissionInfo = new PermissionInfo(paramPermission.info);
    localPermissionInfo.metaData = paramPermission.metaData;
    return localPermissionInfo;
  }
  
  public static final ProviderInfo generateProviderInfo(Provider paramProvider, int paramInt1, PackageUserState paramPackageUserState, int paramInt2)
  {
    if (paramProvider == null) {
      return null;
    }
    if (!checkUseInstalledOrHidden(paramInt1, paramPackageUserState)) {
      return null;
    }
    if ((!copyNeeded(paramInt1, paramProvider.owner, paramPackageUserState, paramProvider.metaData, paramInt2)) && (((paramInt1 & 0x800) != 0) || (paramProvider.info.uriPermissionPatterns == null))) {
      return paramProvider.info;
    }
    ProviderInfo localProviderInfo = new ProviderInfo(paramProvider.info);
    localProviderInfo.metaData = paramProvider.metaData;
    if ((paramInt1 & 0x800) == 0) {
      localProviderInfo.uriPermissionPatterns = null;
    }
    localProviderInfo.applicationInfo = generateApplicationInfo(paramProvider.owner, paramInt1, paramPackageUserState, paramInt2);
    return localProviderInfo;
  }
  
  public static final ServiceInfo generateServiceInfo(Service paramService, int paramInt1, PackageUserState paramPackageUserState, int paramInt2)
  {
    if (paramService == null) {
      return null;
    }
    if (!checkUseInstalledOrHidden(paramInt1, paramPackageUserState)) {
      return null;
    }
    if (!copyNeeded(paramInt1, paramService.owner, paramPackageUserState, paramService.metaData, paramInt2)) {
      return paramService.info;
    }
    ServiceInfo localServiceInfo = new ServiceInfo(paramService.info);
    localServiceInfo.metaData = paramService.metaData;
    localServiceInfo.applicationInfo = generateApplicationInfo(paramService.owner, paramInt1, paramPackageUserState, paramInt2);
    return localServiceInfo;
  }
  
  public static int getApkSigningVersion(Package paramPackage)
  {
    try
    {
      boolean bool = ApkSignatureSchemeV2Verifier.hasSignature(paramPackage.baseCodePath);
      if (bool) {
        return 2;
      }
      return 1;
    }
    catch (IOException paramPackage) {}
    return 0;
  }
  
  private static boolean hasDomainURLs(Package paramPackage)
  {
    if ((paramPackage == null) || (paramPackage.activities == null)) {
      return false;
    }
    paramPackage = paramPackage.activities;
    int k = paramPackage.size();
    int i = 0;
    while (i < k)
    {
      ArrayList localArrayList = ((Activity)paramPackage.get(i)).intents;
      if (localArrayList == null)
      {
        i += 1;
      }
      else
      {
        int m = localArrayList.size();
        int j = 0;
        label64:
        ActivityIntentInfo localActivityIntentInfo;
        if (j < m)
        {
          localActivityIntentInfo = (ActivityIntentInfo)localArrayList.get(j);
          if (localActivityIntentInfo.hasAction("android.intent.action.VIEW")) {
            break label99;
          }
        }
        label99:
        while ((!localActivityIntentInfo.hasAction("android.intent.action.VIEW")) || ((!localActivityIntentInfo.hasDataScheme("http")) && (!localActivityIntentInfo.hasDataScheme("https"))))
        {
          j += 1;
          break label64;
          break;
        }
        return true;
      }
    }
    return false;
  }
  
  public static final boolean isApkFile(File paramFile)
  {
    return isApkPath(paramFile.getName());
  }
  
  private static boolean isApkPath(String paramString)
  {
    return paramString.endsWith(".apk");
  }
  
  public static boolean isAvailable(PackageUserState paramPackageUserState)
  {
    return checkUseInstalledOrHidden(0, paramPackageUserState);
  }
  
  private boolean isPowerOffAlarmPackage(String paramString)
  {
    if (this.mContext != null)
    {
      String[] arrayOfString = this.mContext.getResources().getStringArray(17235983);
      if (arrayOfString.length == 0)
      {
        Slog.w("PackageParser", "power off alarm app array is empty " + paramString);
        return false;
      }
      if (Arrays.asList(arrayOfString).contains(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  private static int loadApkIntoAssetManager(AssetManager paramAssetManager, String paramString, int paramInt)
    throws PackageParser.PackageParserException
  {
    if (((paramInt & 0x4) == 0) || (isApkPath(paramString)))
    {
      paramInt = paramAssetManager.addAssetPath(paramString);
      if (paramInt == 0) {
        throw new PackageParserException(-101, "Failed adding asset path: " + paramString);
      }
    }
    else
    {
      throw new PackageParserException(-100, "Invalid package file: " + paramString);
    }
    return paramInt;
  }
  
  private static Certificate[][] loadCertificates(StrictJarFile paramStrictJarFile, ZipEntry paramZipEntry)
    throws PackageParser.PackageParserException
  {
    Object localObject2 = null;
    Object localObject1 = null;
    try
    {
      InputStream localInputStream = paramStrictJarFile.getInputStream(paramZipEntry);
      localObject1 = localInputStream;
      localObject2 = localInputStream;
      readFullyIgnoringContents(localInputStream);
      localObject1 = localInputStream;
      localObject2 = localInputStream;
      Certificate[][] arrayOfCertificate = paramStrictJarFile.getCertificateChains(paramZipEntry);
      IoUtils.closeQuietly(localInputStream);
      return arrayOfCertificate;
    }
    catch (IOException|RuntimeException localIOException)
    {
      localObject2 = localObject1;
      throw new PackageParserException(-102, "Failed reading " + paramZipEntry.getName() + " in " + paramStrictJarFile, localIOException);
    }
    finally
    {
      IoUtils.closeQuietly((AutoCloseable)localObject2);
    }
  }
  
  private static void modifySharedLibrariesForBackwardCompatibility(Package paramPackage)
  {
    paramPackage.usesLibraries = ArrayUtils.remove(paramPackage.usesLibraries, "org.apache.http.legacy");
    paramPackage.usesOptionalLibraries = ArrayUtils.remove(paramPackage.usesOptionalLibraries, "org.apache.http.legacy");
  }
  
  private Activity parseActivity(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser, int paramInt, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestActivity);
    if (this.mParseActivityArgs == null) {
      this.mParseActivityArgs = new ParseComponentArgs(paramPackage, paramArrayOfString, 3, 1, 2, 44, 23, 30, this.mSeparateProcesses, 7, 17, 5);
    }
    Object localObject2 = this.mParseActivityArgs;
    if (paramBoolean1) {}
    for (Object localObject1 = "<receiver>";; localObject1 = "<activity>")
    {
      ((ParseComponentArgs)localObject2).tag = ((String)localObject1);
      this.mParseActivityArgs.sa = localTypedArray;
      this.mParseActivityArgs.flags = paramInt;
      localObject2 = new Activity(this.mParseActivityArgs, new ActivityInfo());
      if (paramArrayOfString[0] == null) {
        break;
      }
      localTypedArray.recycle();
      return null;
    }
    boolean bool1 = localTypedArray.hasValue(6);
    if (bool1) {
      ((Activity)localObject2).info.exported = localTypedArray.getBoolean(6, false);
    }
    ((Activity)localObject2).info.theme = localTypedArray.getResourceId(0, 0);
    ((Activity)localObject2).info.uiOptions = localTypedArray.getInt(26, ((Activity)localObject2).info.applicationInfo.uiOptions);
    localObject1 = localTypedArray.getNonConfigurationString(27, 1024);
    Object localObject3;
    boolean bool2;
    if (localObject1 != null)
    {
      localObject3 = buildClassName(((Activity)localObject2).info.packageName, (CharSequence)localObject1, paramArrayOfString);
      if (paramArrayOfString[0] == null) {
        ((Activity)localObject2).info.parentActivityName = ((String)localObject3);
      }
    }
    else
    {
      localObject1 = localTypedArray.getNonConfigurationString(4, 0);
      if (localObject1 != null) {
        break label1355;
      }
      ((Activity)localObject2).info.permission = paramPackage.applicationInfo.permission;
      localObject1 = localTypedArray.getNonConfigurationString(8, 1024);
      ((Activity)localObject2).info.taskAffinity = buildTaskAffinityName(paramPackage.applicationInfo.packageName, paramPackage.applicationInfo.taskAffinity, (CharSequence)localObject1, paramArrayOfString);
      ((Activity)localObject2).info.flags = 0;
      if (localTypedArray.getBoolean(9, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x1;
      }
      if (localTypedArray.getBoolean(10, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x2;
      }
      if (localTypedArray.getBoolean(11, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x4;
      }
      if (localTypedArray.getBoolean(21, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x80;
      }
      if (localTypedArray.getBoolean(18, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x8;
      }
      if (localTypedArray.getBoolean(12, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x10;
      }
      if (localTypedArray.getBoolean(13, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x20;
      }
      if ((paramPackage.applicationInfo.flags & 0x20) == 0) {
        break label1396;
      }
      bool2 = true;
      label564:
      if (localTypedArray.getBoolean(19, bool2))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x40;
      }
      if (localTypedArray.getBoolean(22, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x100;
      }
      if ((localTypedArray.getBoolean(29, false)) || (localTypedArray.getBoolean(39, false)))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x400;
      }
      if (localTypedArray.getBoolean(24, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x800;
      }
      if (localTypedArray.getBoolean(45, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x20000000;
      }
      if (paramBoolean1) {
        break label1487;
      }
      if (localTypedArray.getBoolean(25, paramBoolean2))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x200;
      }
      ((Activity)localObject2).info.launchMode = localTypedArray.getInt(14, 0);
      ((Activity)localObject2).info.documentLaunchMode = localTypedArray.getInt(33, 0);
      ((Activity)localObject2).info.maxRecents = localTypedArray.getInt(34, ActivityManager.getDefaultAppRecentsLimitStatic());
      ((Activity)localObject2).info.configChanges = localTypedArray.getInt(16, 0);
      ((Activity)localObject2).info.softInputMode = localTypedArray.getInt(20, 0);
      ((Activity)localObject2).info.persistableMode = localTypedArray.getInteger(32, 0);
      if (localTypedArray.getBoolean(31, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x80000000;
      }
      if (localTypedArray.getBoolean(35, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x2000;
      }
      if (localTypedArray.getBoolean(36, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x1000;
      }
      if (localTypedArray.getBoolean(37, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x4000;
      }
      ((Activity)localObject2).info.screenOrientation = localTypedArray.getInt(15, -1);
      ((Activity)localObject2).info.resizeMode = 0;
      if ((paramPackage.applicationInfo.privateFlags & 0x800) == 0) {
        break label1402;
      }
      paramBoolean2 = true;
      label1041:
      bool2 = localTypedArray.hasValue(40);
      if (!localTypedArray.getBoolean(40, paramBoolean2)) {
        break label1420;
      }
      if (!localTypedArray.getBoolean(41, false)) {
        break label1408;
      }
      ((Activity)localObject2).info.resizeMode = 3;
      label1082:
      if (localTypedArray.getBoolean(46, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x40000;
      }
      ((Activity)localObject2).info.lockTaskLaunchMode = localTypedArray.getInt(38, 0);
      localObject1 = ((Activity)localObject2).info;
      paramBoolean2 = localTypedArray.getBoolean(42, false);
      ((Activity)localObject2).info.directBootAware = paramBoolean2;
      ((ActivityInfo)localObject1).encryptionAware = paramBoolean2;
      if ("com.android.switchaccess.setupwizard.SetupWizardActivity".equals(((Activity)localObject2).className))
      {
        localObject1 = ((Activity)localObject2).info;
        ((Activity)localObject2).info.directBootAware = true;
        ((ActivityInfo)localObject1).encryptionAware = true;
      }
      ((Activity)localObject2).info.requestedVrComponent = localTypedArray.getString(43);
      paramBoolean2 = bool1;
    }
    for (;;)
    {
      if (((Activity)localObject2).info.directBootAware)
      {
        localObject1 = paramPackage.applicationInfo;
        ((ApplicationInfo)localObject1).privateFlags |= 0x100;
      }
      localTypedArray.recycle();
      if ((paramBoolean1) && ((paramPackage.applicationInfo.privateFlags & 0x2) != 0) && (((Activity)localObject2).info.processName == paramPackage.packageName)) {
        paramArrayOfString[0] = "Heavy-weight applications can not have receivers in main process";
      }
      if (paramArrayOfString[0] == null) {
        break label1676;
      }
      return null;
      Log.e("PackageParser", "Activity " + ((Activity)localObject2).info.name + " specified invalid parentActivityName " + (String)localObject1);
      paramArrayOfString[0] = null;
      break;
      label1355:
      localObject3 = ((Activity)localObject2).info;
      if (((String)localObject1).length() > 0) {}
      for (localObject1 = ((String)localObject1).toString().intern();; localObject1 = null)
      {
        ((ActivityInfo)localObject3).permission = ((String)localObject1);
        break;
      }
      label1396:
      bool2 = false;
      break label564;
      label1402:
      paramBoolean2 = false;
      break label1041;
      label1408:
      ((Activity)localObject2).info.resizeMode = 2;
      break label1082;
      label1420:
      if ((paramPackage.applicationInfo.targetSdkVersion >= 24) || (bool2))
      {
        ((Activity)localObject2).info.resizeMode = 0;
        break label1082;
      }
      if ((((Activity)localObject2).info.isFixedOrientation()) || ((((Activity)localObject2).info.flags & 0x800) != 0)) {
        break label1082;
      }
      ((Activity)localObject2).info.resizeMode = 4;
      break label1082;
      label1487:
      ((Activity)localObject2).info.launchMode = 0;
      ((Activity)localObject2).info.configChanges = 0;
      paramBoolean2 = bool1;
      if (localTypedArray.getBoolean(28, false))
      {
        localObject1 = ((Activity)localObject2).info;
        ((ActivityInfo)localObject1).flags |= 0x40000000;
        paramBoolean2 = bool1;
        if (((Activity)localObject2).info.exported)
        {
          paramBoolean2 = bool1;
          if ((paramInt & 0x80) == 0)
          {
            Slog.w("PackageParser", "Activity exported request ignored due to singleUser: " + ((Activity)localObject2).className + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
            ((Activity)localObject2).info.exported = false;
            paramBoolean2 = true;
          }
        }
      }
      localObject1 = ((Activity)localObject2).info;
      bool1 = localTypedArray.getBoolean(42, false);
      ((Activity)localObject2).info.directBootAware = bool1;
      ((ActivityInfo)localObject1).encryptionAware = bool1;
    }
    label1676:
    paramInt = paramXmlResourceParser.getDepth();
    for (;;)
    {
      int i = paramXmlResourceParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlResourceParser.getDepth() <= paramInt))) {
        break label2265;
      }
      if ((i != 3) && (i != 4)) {
        if (paramXmlResourceParser.getName().equals("intent-filter"))
        {
          localObject1 = new ActivityIntentInfo((Activity)localObject2);
          if (!parseIntent(paramResources, paramXmlResourceParser, true, true, (IntentInfo)localObject1, paramArrayOfString)) {
            return null;
          }
          if ((OpFeatures.isSupport(new int[] { 0 })) && (((Activity)localObject2).info.name.equals("com.android.stk.StkMain")) && (((ActivityIntentInfo)localObject1).hasCategory("android.intent.category.LAUNCHER"))) {
            ((ActivityIntentInfo)localObject1).removeCategory("android.intent.category.LAUNCHER");
          }
          if (((ActivityIntentInfo)localObject1).countActions() == 0) {
            Slog.w("PackageParser", "No actions in intent filter at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
          } else {
            ((Activity)localObject2).intents.add(localObject1);
          }
        }
        else if ((!paramBoolean1) && (paramXmlResourceParser.getName().equals("preferred")))
        {
          localObject1 = new ActivityIntentInfo((Activity)localObject2);
          if (!parseIntent(paramResources, paramXmlResourceParser, false, false, (IntentInfo)localObject1, paramArrayOfString)) {
            return null;
          }
          if (((ActivityIntentInfo)localObject1).countActions() == 0)
          {
            Slog.w("PackageParser", "No actions in preferred at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
          }
          else
          {
            if (paramPackage.preferredActivityFilters == null) {
              paramPackage.preferredActivityFilters = new ArrayList();
            }
            paramPackage.preferredActivityFilters.add(localObject1);
          }
        }
        else if (paramXmlResourceParser.getName().equals("meta-data"))
        {
          localObject1 = parseMetaData(paramResources, paramXmlResourceParser, ((Activity)localObject2).metaData, paramArrayOfString);
          ((Activity)localObject2).metaData = ((Bundle)localObject1);
          if (localObject1 == null) {
            return null;
          }
        }
        else
        {
          if ((paramBoolean1) || (!paramXmlResourceParser.getName().equals("layout"))) {
            break;
          }
          parseLayout(paramResources, paramXmlResourceParser, (Activity)localObject2);
        }
      }
    }
    Slog.w("PackageParser", "Problem in package " + this.mArchiveSourcePath + ":");
    if (paramBoolean1) {
      Slog.w("PackageParser", "Unknown element under <receiver>: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
    }
    for (;;)
    {
      XmlUtils.skipCurrentTag(paramXmlResourceParser);
      break;
      Slog.w("PackageParser", "Unknown element under <activity>: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
    }
    label2265:
    if (!paramBoolean2)
    {
      paramPackage = ((Activity)localObject2).info;
      if (((Activity)localObject2).intents.size() <= 0) {
        break label2299;
      }
    }
    label2299:
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      paramPackage.exported = paramBoolean1;
      return (Activity)localObject2;
    }
  }
  
  private Activity parseActivityAlias(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser, int paramInt, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestActivityAlias);
    Object localObject1 = localTypedArray.getNonConfigurationString(7, 1024);
    if (localObject1 == null)
    {
      paramArrayOfString[0] = "<activity-alias> does not specify android:targetActivity";
      localTypedArray.recycle();
      return null;
    }
    String str = buildClassName(paramPackage.applicationInfo.packageName, (CharSequence)localObject1, paramArrayOfString);
    if (str == null)
    {
      localTypedArray.recycle();
      return null;
    }
    if (this.mParseActivityAliasArgs == null)
    {
      this.mParseActivityAliasArgs = new ParseComponentArgs(paramPackage, paramArrayOfString, 2, 0, 1, 11, 8, 10, this.mSeparateProcesses, 0, 6, 4);
      this.mParseActivityAliasArgs.tag = "<activity-alias>";
    }
    this.mParseActivityAliasArgs.sa = localTypedArray;
    this.mParseActivityAliasArgs.flags = paramInt;
    Object localObject2 = null;
    int i = paramPackage.activities.size();
    paramInt = 0;
    for (;;)
    {
      localObject1 = localObject2;
      if (paramInt < i)
      {
        localObject1 = (Activity)paramPackage.activities.get(paramInt);
        if (!str.equals(((Activity)localObject1).info.name)) {}
      }
      else
      {
        if (localObject1 != null) {
          break;
        }
        paramArrayOfString[0] = ("<activity-alias> target activity " + str + " not found in manifest");
        localTypedArray.recycle();
        return null;
      }
      paramInt += 1;
    }
    paramPackage = new ActivityInfo();
    paramPackage.targetActivity = str;
    paramPackage.configChanges = ((Activity)localObject1).info.configChanges;
    paramPackage.flags = ((Activity)localObject1).info.flags;
    paramPackage.icon = ((Activity)localObject1).info.icon;
    paramPackage.logo = ((Activity)localObject1).info.logo;
    paramPackage.banner = ((Activity)localObject1).info.banner;
    paramPackage.labelRes = ((Activity)localObject1).info.labelRes;
    paramPackage.nonLocalizedLabel = ((Activity)localObject1).info.nonLocalizedLabel;
    paramPackage.launchMode = ((Activity)localObject1).info.launchMode;
    paramPackage.lockTaskLaunchMode = ((Activity)localObject1).info.lockTaskLaunchMode;
    paramPackage.processName = ((Activity)localObject1).info.processName;
    if (paramPackage.descriptionRes == 0) {
      paramPackage.descriptionRes = ((Activity)localObject1).info.descriptionRes;
    }
    paramPackage.screenOrientation = ((Activity)localObject1).info.screenOrientation;
    paramPackage.taskAffinity = ((Activity)localObject1).info.taskAffinity;
    paramPackage.theme = ((Activity)localObject1).info.theme;
    paramPackage.softInputMode = ((Activity)localObject1).info.softInputMode;
    paramPackage.uiOptions = ((Activity)localObject1).info.uiOptions;
    paramPackage.parentActivityName = ((Activity)localObject1).info.parentActivityName;
    paramPackage.maxRecents = ((Activity)localObject1).info.maxRecents;
    paramPackage.windowLayout = ((Activity)localObject1).info.windowLayout;
    paramPackage.resizeMode = ((Activity)localObject1).info.resizeMode;
    boolean bool = ((Activity)localObject1).info.directBootAware;
    paramPackage.directBootAware = bool;
    paramPackage.encryptionAware = bool;
    localObject1 = new Activity(this.mParseActivityAliasArgs, paramPackage);
    if (paramArrayOfString[0] != null)
    {
      localTypedArray.recycle();
      return null;
    }
    bool = localTypedArray.hasValue(5);
    if (bool) {
      ((Activity)localObject1).info.exported = localTypedArray.getBoolean(5, false);
    }
    paramPackage = localTypedArray.getNonConfigurationString(3, 0);
    if (paramPackage != null)
    {
      localObject2 = ((Activity)localObject1).info;
      if (paramPackage.length() > 0)
      {
        paramPackage = paramPackage.toString().intern();
        ((ActivityInfo)localObject2).permission = paramPackage;
      }
    }
    else
    {
      paramPackage = localTypedArray.getNonConfigurationString(9, 1024);
      if (paramPackage != null)
      {
        localObject2 = buildClassName(((Activity)localObject1).info.packageName, paramPackage, paramArrayOfString);
        if (paramArrayOfString[0] != null) {
          break label689;
        }
        ((Activity)localObject1).info.parentActivityName = ((String)localObject2);
      }
    }
    for (;;)
    {
      localTypedArray.recycle();
      if (paramArrayOfString[0] == null) {
        break label740;
      }
      return null;
      paramPackage = null;
      break;
      label689:
      Log.e("PackageParser", "Activity alias " + ((Activity)localObject1).info.name + " specified invalid parentActivityName " + paramPackage);
      paramArrayOfString[0] = null;
    }
    label740:
    paramInt = paramXmlResourceParser.getDepth();
    for (;;)
    {
      i = paramXmlResourceParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlResourceParser.getDepth() <= paramInt))) {
        break;
      }
      if ((i != 3) && (i != 4)) {
        if (paramXmlResourceParser.getName().equals("intent-filter"))
        {
          paramPackage = new ActivityIntentInfo((Activity)localObject1);
          if (!parseIntent(paramResources, paramXmlResourceParser, true, true, paramPackage, paramArrayOfString)) {
            return null;
          }
          if (paramPackage.countActions() == 0) {
            Slog.w("PackageParser", "No actions in intent filter at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
          } else {
            ((Activity)localObject1).intents.add(paramPackage);
          }
        }
        else if (paramXmlResourceParser.getName().equals("meta-data"))
        {
          paramPackage = parseMetaData(paramResources, paramXmlResourceParser, ((Activity)localObject1).metaData, paramArrayOfString);
          ((Activity)localObject1).metaData = paramPackage;
          if (paramPackage == null) {
            return null;
          }
        }
        else
        {
          Slog.w("PackageParser", "Unknown element under <activity-alias>: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
        }
      }
    }
    if (!bool)
    {
      paramPackage = ((Activity)localObject1).info;
      if (((Activity)localObject1).intents.size() <= 0) {
        break label1040;
      }
    }
    label1040:
    for (bool = true;; bool = false)
    {
      paramPackage.exported = bool;
      return (Activity)localObject1;
    }
  }
  
  private boolean parseAllMetaData(Resources paramResources, XmlResourceParser paramXmlResourceParser, String paramString, Component<?> paramComponent, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlResourceParser.getDepth();
    for (;;)
    {
      int j = paramXmlResourceParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlResourceParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4)) {
        if (paramXmlResourceParser.getName().equals("meta-data"))
        {
          Bundle localBundle = parseMetaData(paramResources, paramXmlResourceParser, paramComponent.metaData, paramArrayOfString);
          paramComponent.metaData = localBundle;
          if (localBundle == null) {
            return false;
          }
        }
        else
        {
          Slog.w("PackageParser", "Unknown element under " + paramString + ": " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
        }
      }
    }
    return true;
  }
  
  /* Error */
  public static ApkLite parseApkLite(File paramFile, int paramInt)
    throws PackageParser.PackageParserException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 457	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   4: astore 8
    //   6: new 1025	android/content/res/AssetManager
    //   9: dup
    //   10: invokespecial 1372	android/content/res/AssetManager:<init>	()V
    //   13: astore_3
    //   14: aload_3
    //   15: iconst_0
    //   16: iconst_0
    //   17: aconst_null
    //   18: iconst_0
    //   19: iconst_0
    //   20: iconst_0
    //   21: iconst_0
    //   22: iconst_0
    //   23: iconst_0
    //   24: iconst_0
    //   25: iconst_0
    //   26: iconst_0
    //   27: iconst_0
    //   28: iconst_0
    //   29: iconst_0
    //   30: iconst_0
    //   31: getstatic 1375	android/os/Build$VERSION:RESOURCES_SDK_INT	I
    //   34: invokevirtual 1379	android/content/res/AssetManager:setConfiguration	(IILjava/lang/String;IIIIIIIIIIIIII)V
    //   37: aload_3
    //   38: aload 8
    //   40: invokevirtual 1029	android/content/res/AssetManager:addAssetPath	(Ljava/lang/String;)I
    //   43: istore_2
    //   44: iload_2
    //   45: ifne +92 -> 137
    //   48: new 33	android/content/pm/PackageParser$PackageParserException
    //   51: dup
    //   52: bipush -100
    //   54: new 328	java/lang/StringBuilder
    //   57: dup
    //   58: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   61: ldc_w 1381
    //   64: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: aload 8
    //   69: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   72: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   75: invokespecial 526	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;)V
    //   78: athrow
    //   79: astore 5
    //   81: aconst_null
    //   82: astore_0
    //   83: aload_3
    //   84: astore 4
    //   86: aload_0
    //   87: astore 6
    //   89: new 33	android/content/pm/PackageParser$PackageParserException
    //   92: dup
    //   93: bipush -102
    //   95: new 328	java/lang/StringBuilder
    //   98: dup
    //   99: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   102: ldc_w 1381
    //   105: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   108: aload 8
    //   110: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   113: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   116: aload 5
    //   118: invokespecial 507	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;Ljava/lang/Throwable;)V
    //   121: athrow
    //   122: astore_0
    //   123: aload 4
    //   125: astore_3
    //   126: aload 6
    //   128: invokestatic 1050	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   131: aload_3
    //   132: invokestatic 1050	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   135: aload_0
    //   136: athrow
    //   137: new 308	android/util/DisplayMetrics
    //   140: dup
    //   141: invokespecial 309	android/util/DisplayMetrics:<init>	()V
    //   144: astore 4
    //   146: aload 4
    //   148: invokevirtual 314	android/util/DisplayMetrics:setToDefaults	()V
    //   151: new 1005	android/content/res/Resources
    //   154: dup
    //   155: aload_3
    //   156: aload 4
    //   158: aconst_null
    //   159: invokespecial 1384	android/content/res/Resources:<init>	(Landroid/content/res/AssetManager;Landroid/util/DisplayMetrics;Landroid/content/res/Configuration;)V
    //   162: astore 9
    //   164: aload_3
    //   165: iload_2
    //   166: ldc 68
    //   168: invokevirtual 1388	android/content/res/AssetManager:openXmlResourceParser	(ILjava/lang/String;)Landroid/content/res/XmlResourceParser;
    //   171: astore 5
    //   173: iload_1
    //   174: sipush 256
    //   177: iand
    //   178: ifeq +152 -> 330
    //   181: aload_3
    //   182: astore 4
    //   184: aload 5
    //   186: astore 6
    //   188: new 27	android/content/pm/PackageParser$Package
    //   191: dup
    //   192: aconst_null
    //   193: invokespecial 1389	android/content/pm/PackageParser$Package:<init>	(Ljava/lang/String;)V
    //   196: astore 7
    //   198: aload_3
    //   199: astore 4
    //   201: aload 5
    //   203: astore 6
    //   205: ldc2_w 458
    //   208: ldc_w 598
    //   211: invokestatic 467	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   214: aload 7
    //   216: aload_0
    //   217: iconst_0
    //   218: invokestatic 604	android/content/pm/PackageParser:collectCertificates	(Landroid/content/pm/PackageParser$Package;Ljava/io/File;I)V
    //   221: aload_3
    //   222: astore 4
    //   224: aload 5
    //   226: astore 6
    //   228: ldc2_w 458
    //   231: invokestatic 481	android/os/Trace:traceEnd	(J)V
    //   234: aload_3
    //   235: astore 4
    //   237: aload 5
    //   239: astore 6
    //   241: aload 7
    //   243: getfield 438	android/content/pm/PackageParser$Package:mSignatures	[Landroid/content/pm/Signature;
    //   246: astore_0
    //   247: aload_3
    //   248: astore 4
    //   250: aload 5
    //   252: astore 6
    //   254: aload 7
    //   256: getfield 434	android/content/pm/PackageParser$Package:mCertificates	[[Ljava/security/cert/Certificate;
    //   259: astore 7
    //   261: aload_3
    //   262: astore 4
    //   264: aload 5
    //   266: astore 6
    //   268: aload 8
    //   270: aload 9
    //   272: aload 5
    //   274: aload 5
    //   276: iload_1
    //   277: aload_0
    //   278: aload 7
    //   280: invokestatic 1392	android/content/pm/PackageParser:parseApkLite	(Ljava/lang/String;Landroid/content/res/Resources;Lorg/xmlpull/v1/XmlPullParser;Landroid/util/AttributeSet;I[Landroid/content/pm/Signature;[[Ljava/security/cert/Certificate;)Landroid/content/pm/PackageParser$ApkLite;
    //   283: astore_0
    //   284: aload 5
    //   286: invokestatic 1050	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   289: aload_3
    //   290: invokestatic 1050	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   293: aload_0
    //   294: areturn
    //   295: astore_0
    //   296: aload_3
    //   297: astore 4
    //   299: aload 5
    //   301: astore 6
    //   303: ldc2_w 458
    //   306: invokestatic 481	android/os/Trace:traceEnd	(J)V
    //   309: aload_3
    //   310: astore 4
    //   312: aload 5
    //   314: astore 6
    //   316: aload_0
    //   317: athrow
    //   318: astore 4
    //   320: aload 5
    //   322: astore_0
    //   323: aload 4
    //   325: astore 5
    //   327: goto -244 -> 83
    //   330: aconst_null
    //   331: astore_0
    //   332: aconst_null
    //   333: astore 7
    //   335: goto -74 -> 261
    //   338: astore_0
    //   339: aconst_null
    //   340: astore 6
    //   342: aconst_null
    //   343: astore_3
    //   344: goto -218 -> 126
    //   347: astore_0
    //   348: aconst_null
    //   349: astore 6
    //   351: goto -225 -> 126
    //   354: astore 5
    //   356: aconst_null
    //   357: astore_0
    //   358: aconst_null
    //   359: astore_3
    //   360: goto -277 -> 83
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	363	0	paramFile	File
    //   0	363	1	paramInt	int
    //   43	123	2	i	int
    //   13	347	3	localObject1	Object
    //   84	227	4	localObject2	Object
    //   318	6	4	localXmlPullParserException1	XmlPullParserException
    //   79	38	5	localXmlPullParserException2	XmlPullParserException
    //   171	155	5	localObject3	Object
    //   354	1	5	localXmlPullParserException3	XmlPullParserException
    //   87	263	6	localObject4	Object
    //   196	138	7	localObject5	Object
    //   4	265	8	str	String
    //   162	109	9	localResources	Resources
    // Exception table:
    //   from	to	target	type
    //   14	44	79	org/xmlpull/v1/XmlPullParserException
    //   14	44	79	java/io/IOException
    //   14	44	79	java/lang/RuntimeException
    //   48	79	79	org/xmlpull/v1/XmlPullParserException
    //   48	79	79	java/io/IOException
    //   48	79	79	java/lang/RuntimeException
    //   137	173	79	org/xmlpull/v1/XmlPullParserException
    //   137	173	79	java/io/IOException
    //   137	173	79	java/lang/RuntimeException
    //   89	122	122	finally
    //   188	198	122	finally
    //   205	214	122	finally
    //   228	234	122	finally
    //   241	247	122	finally
    //   254	261	122	finally
    //   268	284	122	finally
    //   303	309	122	finally
    //   316	318	122	finally
    //   214	221	295	finally
    //   188	198	318	org/xmlpull/v1/XmlPullParserException
    //   188	198	318	java/io/IOException
    //   188	198	318	java/lang/RuntimeException
    //   205	214	318	org/xmlpull/v1/XmlPullParserException
    //   205	214	318	java/io/IOException
    //   205	214	318	java/lang/RuntimeException
    //   228	234	318	org/xmlpull/v1/XmlPullParserException
    //   228	234	318	java/io/IOException
    //   228	234	318	java/lang/RuntimeException
    //   241	247	318	org/xmlpull/v1/XmlPullParserException
    //   241	247	318	java/io/IOException
    //   241	247	318	java/lang/RuntimeException
    //   254	261	318	org/xmlpull/v1/XmlPullParserException
    //   254	261	318	java/io/IOException
    //   254	261	318	java/lang/RuntimeException
    //   268	284	318	org/xmlpull/v1/XmlPullParserException
    //   268	284	318	java/io/IOException
    //   268	284	318	java/lang/RuntimeException
    //   303	309	318	org/xmlpull/v1/XmlPullParserException
    //   303	309	318	java/io/IOException
    //   303	309	318	java/lang/RuntimeException
    //   316	318	318	org/xmlpull/v1/XmlPullParserException
    //   316	318	318	java/io/IOException
    //   316	318	318	java/lang/RuntimeException
    //   6	14	338	finally
    //   14	44	347	finally
    //   48	79	347	finally
    //   137	173	347	finally
    //   6	14	354	org/xmlpull/v1/XmlPullParserException
    //   6	14	354	java/io/IOException
    //   6	14	354	java/lang/RuntimeException
  }
  
  private static ApkLite parseApkLite(String paramString, Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, int paramInt, Signature[] paramArrayOfSignature, Certificate[][] paramArrayOfCertificate)
    throws IOException, XmlPullParserException, PackageParser.PackageParserException
  {
    Pair localPair = parsePackageSplitNames(paramXmlPullParser, paramAttributeSet);
    int i = -1;
    int k = 0;
    int j = 0;
    boolean bool4 = false;
    boolean bool3 = false;
    boolean bool2 = false;
    boolean bool1 = true;
    Object localObject1 = "";
    int m = 0;
    Object localObject2;
    if (m < paramAttributeSet.getAttributeCount())
    {
      localObject2 = paramAttributeSet.getAttributeName(m);
      int i2;
      int i1;
      if (((String)localObject2).equals("installLocation"))
      {
        i2 = paramAttributeSet.getAttributeIntValue(m, -1);
        i1 = j;
        n = k;
      }
      for (;;)
      {
        m += 1;
        k = n;
        j = i1;
        i = i2;
        break;
        if (((String)localObject2).equals("versionCode"))
        {
          n = paramAttributeSet.getAttributeIntValue(m, 0);
          i1 = j;
          i2 = i;
        }
        else if (((String)localObject2).equals("revisionCode"))
        {
          i1 = paramAttributeSet.getAttributeIntValue(m, 0);
          n = k;
          i2 = i;
        }
        else
        {
          n = k;
          i1 = j;
          i2 = i;
          if (((String)localObject2).equals("coreApp"))
          {
            bool4 = paramAttributeSet.getAttributeBooleanValue(m, false);
            n = k;
            i1 = j;
            i2 = i;
          }
        }
      }
    }
    int n = paramXmlPullParser.getDepth() + 1;
    ArrayList localArrayList = new ArrayList();
    for (;;)
    {
      m = paramXmlPullParser.next();
      if ((m == 1) || ((m == 3) && (paramXmlPullParser.getDepth() < n))) {
        break;
      }
      if ((m != 3) && (m != 4))
      {
        if ((paramXmlPullParser.getDepth() == n) && ("package-verifier".equals(paramXmlPullParser.getName())))
        {
          localObject2 = parseVerifier(paramResources, paramXmlPullParser, paramAttributeSet, paramInt);
          if (localObject2 != null) {
            localArrayList.add(localObject2);
          }
        }
        boolean bool5 = bool3;
        boolean bool6 = bool2;
        boolean bool7 = bool1;
        if (paramXmlPullParser.getDepth() == n)
        {
          bool5 = bool3;
          bool6 = bool2;
          bool7 = bool1;
          if ("application".equals(paramXmlPullParser.getName()))
          {
            m = 0;
            for (;;)
            {
              bool5 = bool3;
              bool6 = bool2;
              bool7 = bool1;
              if (m >= paramAttributeSet.getAttributeCount()) {
                break;
              }
              localObject2 = paramAttributeSet.getAttributeName(m);
              if ("multiArch".equals(localObject2)) {
                bool3 = paramAttributeSet.getAttributeBooleanValue(m, false);
              }
              if ("use32bitAbi".equals(localObject2)) {
                bool2 = paramAttributeSet.getAttributeBooleanValue(m, false);
              }
              if ("extractNativeLibs".equals(localObject2)) {
                bool1 = paramAttributeSet.getAttributeBooleanValue(m, true);
              }
              m += 1;
            }
          }
        }
        bool3 = bool5;
        bool2 = bool6;
        bool1 = bool7;
        if (paramXmlPullParser.getDepth() == n + 1)
        {
          bool3 = bool5;
          bool2 = bool6;
          bool1 = bool7;
          if ("meta-data".equals(paramXmlPullParser.getName()))
          {
            TypedArray localTypedArray = paramResources.obtainAttributes(paramAttributeSet, R.styleable.AndroidManifestMetaData);
            bool3 = bool5;
            bool2 = bool6;
            bool1 = bool7;
            if (localTypedArray != null)
            {
              Object localObject3 = localTypedArray.getNonConfigurationString(0, 0);
              if (localObject3 == null)
              {
                localTypedArray.recycle();
                bool3 = bool5;
                bool2 = bool6;
                bool1 = bool7;
              }
              else
              {
                localObject2 = localObject1;
                if ("oneplus_libs".equals(((String)localObject3).intern()))
                {
                  localObject3 = localTypedArray.peekValue(1);
                  localObject2 = localObject1;
                  if (localObject3 != null) {
                    localObject2 = ((TypedValue)localObject3).coerceToString().toString();
                  }
                }
                localTypedArray.recycle();
                bool3 = bool5;
                bool2 = bool6;
                bool1 = bool7;
                localObject1 = localObject2;
              }
            }
          }
        }
      }
    }
    return new ApkLite(paramString, (String)localPair.first, (String)localPair.second, k, j, i, localArrayList, paramArrayOfSignature, paramArrayOfCertificate, bool4, bool3, bool2, bool1, (String)localObject1);
  }
  
  private Package parseBaseApk(Resources paramResources, XmlResourceParser paramXmlResourceParser, int paramInt, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    try
    {
      localObject2 = parsePackageSplitNames(paramXmlResourceParser, paramXmlResourceParser);
      localObject1 = (String)((Pair)localObject2).first;
      localObject2 = (String)((Pair)localObject2).second;
      if ((localObject1 != null) && (ActivityThread.inCompatConfigList(0, (String)localObject1)))
      {
        paramArrayOfString[0] = ("Don't allowed to install package " + (String)localObject1);
        this.mParseError = -106;
        return null;
      }
      if (!TextUtils.isEmpty((CharSequence)localObject2))
      {
        paramArrayOfString[0] = ("Expected base APK, but found split " + (String)localObject2);
        this.mParseError = -106;
        return null;
      }
    }
    catch (PackageParserException paramResources)
    {
      this.mParseError = -106;
      return null;
    }
    Object localObject1 = new Package((String)localObject1);
    Object localObject2 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifest);
    int i = ((TypedArray)localObject2).getInteger(1, 0);
    ((Package)localObject1).applicationInfo.versionCode = i;
    ((Package)localObject1).mVersionCode = i;
    ((Package)localObject1).baseRevisionCode = ((TypedArray)localObject2).getInteger(5, 0);
    ((Package)localObject1).mVersionName = ((TypedArray)localObject2).getNonConfigurationString(2, 0);
    if (((Package)localObject1).mVersionName != null) {
      ((Package)localObject1).mVersionName = ((Package)localObject1).mVersionName.intern();
    }
    ((Package)localObject1).coreApp = paramXmlResourceParser.getAttributeBooleanValue(null, "coreApp", false);
    ((TypedArray)localObject2).recycle();
    return parseBaseApkCommon((Package)localObject1, null, paramResources, paramXmlResourceParser, paramInt, paramArrayOfString);
  }
  
  /* Error */
  private Package parseBaseApk(File paramFile, AssetManager paramAssetManager, int paramInt)
    throws PackageParser.PackageParserException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 457	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   4: astore 11
    //   6: aconst_null
    //   7: astore 7
    //   9: aload 11
    //   11: ldc 90
    //   13: invokevirtual 580	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   16: ifeq +31 -> 47
    //   19: aload 11
    //   21: bipush 47
    //   23: ldc 90
    //   25: invokevirtual 360	java/lang/String:length	()I
    //   28: invokevirtual 1492	java/lang/String:indexOf	(II)I
    //   31: istore 4
    //   33: aload 11
    //   35: ldc 90
    //   37: invokevirtual 360	java/lang/String:length	()I
    //   40: iload 4
    //   42: invokevirtual 1494	java/lang/String:substring	(II)Ljava/lang/String;
    //   45: astore 7
    //   47: aload_0
    //   48: iconst_1
    //   49: putfield 306	android/content/pm/PackageParser:mParseError	I
    //   52: aload_0
    //   53: aload_1
    //   54: invokevirtual 457	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   57: putfield 1241	android/content/pm/PackageParser:mArchiveSourcePath	Ljava/lang/String;
    //   60: aload_2
    //   61: aload 11
    //   63: iload_3
    //   64: invokestatic 1496	android/content/pm/PackageParser:loadApkIntoAssetManager	(Landroid/content/res/AssetManager;Ljava/lang/String;I)I
    //   67: istore 4
    //   69: aconst_null
    //   70: astore 5
    //   72: aconst_null
    //   73: astore_1
    //   74: aconst_null
    //   75: astore 6
    //   77: aconst_null
    //   78: astore 9
    //   80: aconst_null
    //   81: astore 10
    //   83: aconst_null
    //   84: astore 8
    //   86: new 1005	android/content/res/Resources
    //   89: dup
    //   90: aload_2
    //   91: aload_0
    //   92: getfield 311	android/content/pm/PackageParser:mMetrics	Landroid/util/DisplayMetrics;
    //   95: aconst_null
    //   96: invokespecial 1384	android/content/res/Resources:<init>	(Landroid/content/res/AssetManager;Landroid/util/DisplayMetrics;Landroid/content/res/Configuration;)V
    //   99: astore 12
    //   101: aload 8
    //   103: astore 6
    //   105: aload 9
    //   107: astore_1
    //   108: aload 10
    //   110: astore 5
    //   112: aload_2
    //   113: iconst_0
    //   114: iconst_0
    //   115: aconst_null
    //   116: iconst_0
    //   117: iconst_0
    //   118: iconst_0
    //   119: iconst_0
    //   120: iconst_0
    //   121: iconst_0
    //   122: iconst_0
    //   123: iconst_0
    //   124: iconst_0
    //   125: iconst_0
    //   126: iconst_0
    //   127: iconst_0
    //   128: iconst_0
    //   129: getstatic 1375	android/os/Build$VERSION:RESOURCES_SDK_INT	I
    //   132: invokevirtual 1379	android/content/res/AssetManager:setConfiguration	(IILjava/lang/String;IIIIIIIIIIIIII)V
    //   135: aload 8
    //   137: astore 6
    //   139: aload 9
    //   141: astore_1
    //   142: aload 10
    //   144: astore 5
    //   146: aload_2
    //   147: iload 4
    //   149: ldc 68
    //   151: invokevirtual 1388	android/content/res/AssetManager:openXmlResourceParser	(ILjava/lang/String;)Landroid/content/res/XmlResourceParser;
    //   154: astore_2
    //   155: aload_2
    //   156: astore 6
    //   158: aload_2
    //   159: astore_1
    //   160: aload_2
    //   161: astore 5
    //   163: iconst_1
    //   164: anewarray 264	java/lang/String
    //   167: astore 8
    //   169: aload_2
    //   170: astore 6
    //   172: aload_2
    //   173: astore_1
    //   174: aload_2
    //   175: astore 5
    //   177: aload_0
    //   178: aload 12
    //   180: aload_2
    //   181: iload_3
    //   182: aload 8
    //   184: invokespecial 1498	android/content/pm/PackageParser:parseBaseApk	(Landroid/content/res/Resources;Landroid/content/res/XmlResourceParser;I[Ljava/lang/String;)Landroid/content/pm/PackageParser$Package;
    //   187: astore 9
    //   189: aload 9
    //   191: ifnonnull +79 -> 270
    //   194: aload_2
    //   195: astore 6
    //   197: aload_2
    //   198: astore_1
    //   199: aload_2
    //   200: astore 5
    //   202: new 33	android/content/pm/PackageParser$PackageParserException
    //   205: dup
    //   206: aload_0
    //   207: getfield 306	android/content/pm/PackageParser:mParseError	I
    //   210: new 328	java/lang/StringBuilder
    //   213: dup
    //   214: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   217: aload 11
    //   219: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   222: ldc_w 1500
    //   225: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   228: aload_2
    //   229: invokeinterface 1248 1 0
    //   234: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   237: ldc_w 1502
    //   240: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   243: aload 8
    //   245: iconst_0
    //   246: aaload
    //   247: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   250: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   253: invokespecial 526	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;)V
    //   256: athrow
    //   257: astore_2
    //   258: aload 6
    //   260: astore_1
    //   261: aload_2
    //   262: athrow
    //   263: astore_2
    //   264: aload_1
    //   265: invokestatic 1050	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   268: aload_2
    //   269: athrow
    //   270: aload_2
    //   271: astore 6
    //   273: aload_2
    //   274: astore_1
    //   275: aload_2
    //   276: astore 5
    //   278: aload 9
    //   280: aload 7
    //   282: invokevirtual 1505	android/content/pm/PackageParser$Package:setVolumeUuid	(Ljava/lang/String;)V
    //   285: aload_2
    //   286: astore 6
    //   288: aload_2
    //   289: astore_1
    //   290: aload_2
    //   291: astore 5
    //   293: aload 9
    //   295: aload 7
    //   297: invokevirtual 1508	android/content/pm/PackageParser$Package:setApplicationVolumeUuid	(Ljava/lang/String;)V
    //   300: aload_2
    //   301: astore 6
    //   303: aload_2
    //   304: astore_1
    //   305: aload_2
    //   306: astore 5
    //   308: aload 9
    //   310: aload 11
    //   312: invokevirtual 1511	android/content/pm/PackageParser$Package:setBaseCodePath	(Ljava/lang/String;)V
    //   315: aload_2
    //   316: astore 6
    //   318: aload_2
    //   319: astore_1
    //   320: aload_2
    //   321: astore 5
    //   323: aload 9
    //   325: aconst_null
    //   326: invokevirtual 1515	android/content/pm/PackageParser$Package:setSignatures	([Landroid/content/pm/Signature;)V
    //   329: aload_2
    //   330: invokestatic 1050	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   333: aload 9
    //   335: areturn
    //   336: astore_2
    //   337: aload 6
    //   339: astore_1
    //   340: new 33	android/content/pm/PackageParser$PackageParserException
    //   343: dup
    //   344: bipush -102
    //   346: new 328	java/lang/StringBuilder
    //   349: dup
    //   350: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   353: ldc_w 1517
    //   356: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   359: aload 11
    //   361: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   364: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   367: aload_2
    //   368: invokespecial 507	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;Ljava/lang/Throwable;)V
    //   371: athrow
    //   372: astore_2
    //   373: goto -109 -> 264
    //   376: astore_2
    //   377: aload 5
    //   379: astore_1
    //   380: goto -119 -> 261
    //   383: astore_2
    //   384: aload 5
    //   386: astore_1
    //   387: goto -47 -> 340
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	390	0	this	PackageParser
    //   0	390	1	paramFile	File
    //   0	390	2	paramAssetManager	AssetManager
    //   0	390	3	paramInt	int
    //   31	117	4	i	int
    //   70	315	5	localObject1	Object
    //   75	263	6	localObject2	Object
    //   7	289	7	str1	String
    //   84	160	8	arrayOfString	String[]
    //   78	256	9	localPackage	Package
    //   81	62	10	localObject3	Object
    //   4	356	11	str2	String
    //   99	80	12	localResources	Resources
    // Exception table:
    //   from	to	target	type
    //   112	135	257	android/content/pm/PackageParser$PackageParserException
    //   146	155	257	android/content/pm/PackageParser$PackageParserException
    //   163	169	257	android/content/pm/PackageParser$PackageParserException
    //   177	189	257	android/content/pm/PackageParser$PackageParserException
    //   202	257	257	android/content/pm/PackageParser$PackageParserException
    //   278	285	257	android/content/pm/PackageParser$PackageParserException
    //   293	300	257	android/content/pm/PackageParser$PackageParserException
    //   308	315	257	android/content/pm/PackageParser$PackageParserException
    //   323	329	257	android/content/pm/PackageParser$PackageParserException
    //   86	101	263	finally
    //   261	263	263	finally
    //   340	372	263	finally
    //   86	101	336	java/lang/Exception
    //   112	135	372	finally
    //   146	155	372	finally
    //   163	169	372	finally
    //   177	189	372	finally
    //   202	257	372	finally
    //   278	285	372	finally
    //   293	300	372	finally
    //   308	315	372	finally
    //   323	329	372	finally
    //   86	101	376	android/content/pm/PackageParser$PackageParserException
    //   112	135	383	java/lang/Exception
    //   146	155	383	java/lang/Exception
    //   163	169	383	java/lang/Exception
    //   177	189	383	java/lang/Exception
    //   202	257	383	java/lang/Exception
    //   278	285	383	java/lang/Exception
    //   293	300	383	java/lang/Exception
    //   308	315	383	java/lang/Exception
    //   323	329	383	java/lang/Exception
  }
  
  private boolean parseBaseApkChild(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser, int paramInt, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    if ((paramPackage.childPackages != null) && (paramPackage.childPackages.size() + 2 > 5))
    {
      paramArrayOfString[0] = "Maximum number of packages per APK is: 5";
      this.mParseError = -108;
      return false;
    }
    Object localObject = paramXmlResourceParser.getAttributeValue(null, "package");
    if (validateName((String)localObject, true, false) != null)
    {
      this.mParseError = -106;
      return false;
    }
    if (((String)localObject).equals(paramPackage.packageName))
    {
      paramPackage = "Child package name cannot be equal to parent package name: " + paramPackage.packageName;
      Slog.w("PackageParser", paramPackage);
      paramArrayOfString[0] = paramPackage;
      this.mParseError = -108;
      return false;
    }
    if (paramPackage.hasChildPackage((String)localObject))
    {
      paramPackage = "Duplicate child package:" + (String)localObject;
      Slog.w("PackageParser", paramPackage);
      paramArrayOfString[0] = paramPackage;
      this.mParseError = -108;
      return false;
    }
    localObject = new Package((String)localObject);
    ((Package)localObject).mVersionCode = paramPackage.mVersionCode;
    ((Package)localObject).baseRevisionCode = paramPackage.baseRevisionCode;
    ((Package)localObject).mVersionName = paramPackage.mVersionName;
    ((Package)localObject).applicationInfo.targetSdkVersion = paramPackage.applicationInfo.targetSdkVersion;
    ((Package)localObject).applicationInfo.minSdkVersion = paramPackage.applicationInfo.minSdkVersion;
    paramResources = parseBaseApkCommon((Package)localObject, CHILD_PACKAGE_TAGS, paramResources, paramXmlResourceParser, paramInt, paramArrayOfString);
    if (paramResources == null) {
      return false;
    }
    if (paramPackage.childPackages == null) {
      paramPackage.childPackages = new ArrayList();
    }
    paramPackage.childPackages.add(paramResources);
    paramResources.parentPackage = paramPackage;
    return true;
  }
  
  private Package parseBaseApkCommon(Package paramPackage, Set<String> paramSet, Resources paramResources, XmlResourceParser paramXmlResourceParser, int paramInt, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    this.mParseInstrumentationArgs = null;
    this.mParseActivityArgs = null;
    this.mParseServiceArgs = null;
    this.mParseProviderArgs = null;
    int i3 = 0;
    Object localObject1 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifest);
    Object localObject2 = ((TypedArray)localObject1).getNonConfigurationString(0, 0);
    Object localObject3;
    int i1;
    int n;
    int m;
    int j;
    int k;
    int i2;
    int i7;
    if ((localObject2 != null) && (((String)localObject2).length() > 0))
    {
      localObject3 = validateName((String)localObject2, true, false);
      if ((localObject3 == null) || ("android".equals(paramPackage.packageName)))
      {
        paramPackage.mSharedUserId = ((String)localObject2).intern();
        paramPackage.mSharedUserLabel = ((TypedArray)localObject1).getResourceId(3, 0);
      }
    }
    else
    {
      paramPackage.installLocation = ((TypedArray)localObject1).getInteger(4, -1);
      paramPackage.applicationInfo.installLocation = paramPackage.installLocation;
      if ((paramInt & 0x10) != 0)
      {
        localObject1 = paramPackage.applicationInfo;
        ((ApplicationInfo)localObject1).privateFlags |= 0x4;
      }
      if ((paramInt & 0x20) != 0)
      {
        localObject1 = paramPackage.applicationInfo;
        ((ApplicationInfo)localObject1).flags |= 0x40000;
      }
      if ((paramInt & 0x800) != 0)
      {
        localObject1 = paramPackage.applicationInfo;
        ((ApplicationInfo)localObject1).privateFlags |= 0x200;
      }
      i1 = 1;
      n = 1;
      m = 1;
      j = 1;
      k = 1;
      i2 = 1;
      i7 = paramXmlResourceParser.getDepth();
    }
    for (;;)
    {
      i = paramXmlResourceParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlResourceParser.getDepth() <= i7))) {
        break;
      }
      if ((i != 3) && (i != 4))
      {
        localObject1 = paramXmlResourceParser.getName();
        if ((paramSet == null) || (paramSet.contains(localObject1)))
        {
          if (!((String)localObject1).equals("application")) {
            break label464;
          }
          if (i3 != 0)
          {
            Slog.w("PackageParser", "<manifest> has more than one <application>");
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
            continue;
            paramArrayOfString[0] = ("<manifest> specifies bad sharedUserId name \"" + (String)localObject2 + "\": " + (String)localObject3);
            this.mParseError = -107;
            return null;
          }
        }
        else
        {
          Slog.w("PackageParser", "Skipping unsupported element under <manifest>: " + (String)localObject1 + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
          continue;
        }
        i3 = 1;
        if (!parseBaseApplication(paramPackage, paramResources, paramXmlResourceParser, paramInt, paramArrayOfString))
        {
          return null;
          label464:
          if (((String)localObject1).equals("overlay"))
          {
            localObject1 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestResourceOverlay);
            paramPackage.mOverlayTarget = ((TypedArray)localObject1).getString(1);
            paramPackage.mOverlayPriority = ((TypedArray)localObject1).getInt(0, -1);
            ((TypedArray)localObject1).recycle();
            if (paramPackage.mOverlayTarget == null)
            {
              paramArrayOfString[0] = "<overlay> does not specify a target package";
              this.mParseError = -108;
              return null;
            }
            if ((paramPackage.mOverlayPriority < 0) || (paramPackage.mOverlayPriority > 9999))
            {
              paramArrayOfString[0] = "<overlay> priority must be between 0 and 9999";
              this.mParseError = -108;
              return null;
            }
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
          }
          else if (((String)localObject1).equals("key-sets"))
          {
            if (!parseKeySets(paramPackage, paramResources, paramXmlResourceParser, paramArrayOfString)) {
              return null;
            }
          }
          else if (((String)localObject1).equals("permission-group"))
          {
            if (parsePermissionGroup(paramPackage, paramInt, paramResources, paramXmlResourceParser, paramArrayOfString) == null) {
              return null;
            }
          }
          else if (((String)localObject1).equals("permission"))
          {
            if (parsePermission(paramPackage, paramResources, paramXmlResourceParser, paramArrayOfString) == null) {
              return null;
            }
          }
          else if (((String)localObject1).equals("permission-tree"))
          {
            if (parsePermissionTree(paramPackage, paramResources, paramXmlResourceParser, paramArrayOfString) == null) {
              return null;
            }
          }
          else if (((String)localObject1).equals("uses-permission"))
          {
            if (!parseUsesPermission(paramPackage, paramResources, paramXmlResourceParser)) {
              return null;
            }
          }
          else if ((((String)localObject1).equals("uses-permission-sdk-m")) || (((String)localObject1).equals("uses-permission-sdk-23")))
          {
            if (!parseUsesPermission(paramPackage, paramResources, paramXmlResourceParser)) {
              return null;
            }
          }
          else if (((String)localObject1).equals("uses-configuration"))
          {
            localObject1 = new ConfigurationInfo();
            localObject2 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestUsesConfiguration);
            ((ConfigurationInfo)localObject1).reqTouchScreen = ((TypedArray)localObject2).getInt(0, 0);
            ((ConfigurationInfo)localObject1).reqKeyboardType = ((TypedArray)localObject2).getInt(1, 0);
            if (((TypedArray)localObject2).getBoolean(2, false)) {
              ((ConfigurationInfo)localObject1).reqInputFeatures |= 0x1;
            }
            ((ConfigurationInfo)localObject1).reqNavigation = ((TypedArray)localObject2).getInt(3, 0);
            if (((TypedArray)localObject2).getBoolean(4, false)) {
              ((ConfigurationInfo)localObject1).reqInputFeatures |= 0x2;
            }
            ((TypedArray)localObject2).recycle();
            paramPackage.configPreferences = ArrayUtils.add(paramPackage.configPreferences, localObject1);
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
          }
          else if (((String)localObject1).equals("uses-feature"))
          {
            localObject1 = parseUsesFeature(paramResources, paramXmlResourceParser);
            paramPackage.reqFeatures = ArrayUtils.add(paramPackage.reqFeatures, localObject1);
            if (((FeatureInfo)localObject1).name == null)
            {
              localObject2 = new ConfigurationInfo();
              ((ConfigurationInfo)localObject2).reqGlEsVersion = ((FeatureInfo)localObject1).reqGlEsVersion;
              paramPackage.configPreferences = ArrayUtils.add(paramPackage.configPreferences, localObject2);
            }
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
          }
          else
          {
            int i4;
            if (((String)localObject1).equals("feature-group"))
            {
              localObject2 = new FeatureGroupInfo();
              localObject1 = null;
              i = paramXmlResourceParser.getDepth();
              do
              {
                i4 = paramXmlResourceParser.next();
                if ((i4 == 1) || ((i4 == 3) && (paramXmlResourceParser.getDepth() <= i))) {
                  break;
                }
              } while ((i4 == 3) || (i4 == 4));
              localObject3 = paramXmlResourceParser.getName();
              if (((String)localObject3).equals("uses-feature"))
              {
                localObject3 = parseUsesFeature(paramResources, paramXmlResourceParser);
                ((FeatureInfo)localObject3).flags |= 0x1;
                localObject1 = ArrayUtils.add((ArrayList)localObject1, localObject3);
              }
              for (;;)
              {
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
                break;
                Slog.w("PackageParser", "Unknown element under <feature-group>: " + (String)localObject3 + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
              }
              if (localObject1 != null)
              {
                ((FeatureGroupInfo)localObject2).features = new FeatureInfo[((ArrayList)localObject1).size()];
                ((FeatureGroupInfo)localObject2).features = ((FeatureInfo[])((ArrayList)localObject1).toArray(((FeatureGroupInfo)localObject2).features));
              }
              paramPackage.featureGroups = ArrayUtils.add(paramPackage.featureGroups, localObject2);
            }
            else
            {
              if (((String)localObject1).equals("uses-sdk"))
              {
                if (SDK_VERSION > 0)
                {
                  TypedArray localTypedArray = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestUsesSdk);
                  i4 = 1;
                  localObject3 = null;
                  int i6 = 0;
                  Object localObject4 = null;
                  TypedValue localTypedValue = localTypedArray.peekValue(0);
                  localObject1 = localObject3;
                  int i5 = i4;
                  localObject2 = localObject4;
                  i = i6;
                  if (localTypedValue != null)
                  {
                    if ((localTypedValue.type == 3) && (localTypedValue.string != null))
                    {
                      localObject1 = localTypedValue.string.toString();
                      localObject2 = localObject1;
                      i = i6;
                      i5 = i4;
                    }
                  }
                  else
                  {
                    localTypedValue = localTypedArray.peekValue(1);
                    localObject4 = localObject1;
                    localObject3 = localObject2;
                    i4 = i;
                    if (localTypedValue != null)
                    {
                      if ((localTypedValue.type != 3) || (localTypedValue.string == null)) {
                        break label1524;
                      }
                      localObject2 = localTypedValue.string.toString();
                      localObject4 = localObject1;
                      localObject3 = localObject2;
                      i4 = i;
                      if (localObject1 == null)
                      {
                        localObject4 = localObject2;
                        i4 = i;
                        localObject3 = localObject2;
                      }
                    }
                    label1383:
                    localTypedArray.recycle();
                    if (localObject4 == null) {
                      break label1709;
                    }
                    i6 = 0;
                    localObject1 = SDK_CODENAMES;
                    i = 0;
                    int i8 = localObject1.length;
                    label1409:
                    i5 = i6;
                    if (i < i8)
                    {
                      if (!((String)localObject4).equals(localObject1[i])) {
                        break label1542;
                      }
                      i5 = 1;
                    }
                    if (i5 != 0) {
                      break label1585;
                    }
                    if (SDK_CODENAMES.length <= 0) {
                      break label1551;
                    }
                    paramArrayOfString[0] = ("Requires development platform " + (String)localObject4 + " (current platform is any of " + Arrays.toString(SDK_CODENAMES) + ")");
                  }
                  for (;;)
                  {
                    this.mParseError = -12;
                    return null;
                    i5 = localTypedValue.data;
                    i = i5;
                    localObject1 = localObject3;
                    localObject2 = localObject4;
                    break;
                    label1524:
                    i4 = localTypedValue.data;
                    localObject4 = localObject1;
                    localObject3 = localObject2;
                    break label1383;
                    label1542:
                    i += 1;
                    break label1409;
                    label1551:
                    paramArrayOfString[0] = ("Requires development platform " + (String)localObject4 + " but this is a release platform.");
                  }
                  label1585:
                  paramPackage.applicationInfo.minSdkVersion = 10000;
                  if (localObject3 == null) {
                    break label1841;
                  }
                  i5 = 0;
                  localObject1 = SDK_CODENAMES;
                  i = 0;
                  i6 = localObject1.length;
                  label1616:
                  i4 = i5;
                  if (i < i6)
                  {
                    if (((String)localObject3).equals(localObject1[i])) {
                      i4 = 1;
                    }
                  }
                  else
                  {
                    if (i4 != 0) {
                      break label1823;
                    }
                    if (SDK_CODENAMES.length <= 0) {
                      break label1789;
                    }
                    paramArrayOfString[0] = ("Requires development platform " + (String)localObject3 + " (current platform is any of " + Arrays.toString(SDK_CODENAMES) + ")");
                  }
                  for (;;)
                  {
                    this.mParseError = -12;
                    return null;
                    label1709:
                    if (i5 > SDK_VERSION)
                    {
                      paramArrayOfString[0] = ("Requires newer sdk version #" + i5 + " (current version is #" + SDK_VERSION + ")");
                      this.mParseError = -12;
                      return null;
                    }
                    paramPackage.applicationInfo.minSdkVersion = i5;
                    break;
                    i += 1;
                    break label1616;
                    label1789:
                    paramArrayOfString[0] = ("Requires development platform " + (String)localObject3 + " but this is a release platform.");
                  }
                }
                label1823:
                label1841:
                for (paramPackage.applicationInfo.targetSdkVersion = 10000;; paramPackage.applicationInfo.targetSdkVersion = i4)
                {
                  XmlUtils.skipCurrentTag(paramXmlResourceParser);
                  break;
                }
              }
              if (((String)localObject1).equals("supports-screens"))
              {
                localObject1 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestSupportsScreens);
                paramPackage.applicationInfo.requiresSmallestWidthDp = ((TypedArray)localObject1).getInteger(6, 0);
                paramPackage.applicationInfo.compatibleWidthLimitDp = ((TypedArray)localObject1).getInteger(7, 0);
                paramPackage.applicationInfo.largestWidthLimitDp = ((TypedArray)localObject1).getInteger(8, 0);
                i1 = ((TypedArray)localObject1).getInteger(1, i1);
                n = ((TypedArray)localObject1).getInteger(2, n);
                m = ((TypedArray)localObject1).getInteger(3, m);
                j = ((TypedArray)localObject1).getInteger(5, j);
                k = ((TypedArray)localObject1).getInteger(4, k);
                i2 = ((TypedArray)localObject1).getInteger(0, i2);
                ((TypedArray)localObject1).recycle();
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
              }
              else if (((String)localObject1).equals("protected-broadcast"))
              {
                localObject1 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestProtectedBroadcast);
                localObject2 = ((TypedArray)localObject1).getNonResourceString(0);
                ((TypedArray)localObject1).recycle();
                if ((localObject2 != null) && ((paramInt & 0x1) != 0))
                {
                  if (paramPackage.protectedBroadcasts == null) {
                    paramPackage.protectedBroadcasts = new ArrayList();
                  }
                  if (!paramPackage.protectedBroadcasts.contains(localObject2)) {
                    paramPackage.protectedBroadcasts.add(((String)localObject2).intern());
                  }
                }
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
              }
              else if (((String)localObject1).equals("instrumentation"))
              {
                if (parseInstrumentation(paramPackage, paramResources, paramXmlResourceParser, paramArrayOfString) == null) {
                  return null;
                }
              }
              else if (((String)localObject1).equals("original-package"))
              {
                localObject1 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestOriginalPackage);
                localObject2 = ((TypedArray)localObject1).getNonConfigurationString(0, 0);
                if (!paramPackage.packageName.equals(localObject2))
                {
                  if (paramPackage.mOriginalPackages == null)
                  {
                    paramPackage.mOriginalPackages = new ArrayList();
                    paramPackage.mRealPackage = paramPackage.packageName;
                  }
                  paramPackage.mOriginalPackages.add(localObject2);
                }
                ((TypedArray)localObject1).recycle();
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
              }
              else if (((String)localObject1).equals("adopt-permissions"))
              {
                localObject1 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestOriginalPackage);
                localObject2 = ((TypedArray)localObject1).getNonConfigurationString(0, 0);
                ((TypedArray)localObject1).recycle();
                if (localObject2 != null)
                {
                  if (paramPackage.mAdoptPermissions == null) {
                    paramPackage.mAdoptPermissions = new ArrayList();
                  }
                  paramPackage.mAdoptPermissions.add(localObject2);
                }
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
              }
              else if (((String)localObject1).equals("uses-gl-texture"))
              {
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
              }
              else if (((String)localObject1).equals("compatible-screens"))
              {
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
              }
              else if (((String)localObject1).equals("supports-input"))
              {
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
              }
              else if (((String)localObject1).equals("eat-comment"))
              {
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
              }
              else if (((String)localObject1).equals("package"))
              {
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
              }
              else if (((String)localObject1).equals("restrict-update"))
              {
                if ((paramInt & 0x40) != 0)
                {
                  localObject2 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestRestrictUpdate);
                  localObject1 = ((TypedArray)localObject2).getNonConfigurationString(0, 0);
                  ((TypedArray)localObject2).recycle();
                  paramPackage.restrictUpdateHash = null;
                  if (localObject1 != null)
                  {
                    i4 = ((String)localObject1).length();
                    localObject2 = new byte[i4 / 2];
                    i = 0;
                    while (i < i4)
                    {
                      localObject2[(i / 2)] = ((byte)((Character.digit(((String)localObject1).charAt(i), 16) << 4) + Character.digit(((String)localObject1).charAt(i + 1), 16)));
                      i += 2;
                    }
                    paramPackage.restrictUpdateHash = ((byte[])localObject2);
                  }
                }
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
              }
              else
              {
                Slog.w("PackageParser", "Unknown element under <manifest>: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
              }
            }
          }
        }
      }
    }
    if ((i3 == 0) && (paramPackage.instrumentation.size() == 0))
    {
      paramArrayOfString[0] = "<manifest> does not contain an <application> or <instrumentation>";
      this.mParseError = -109;
    }
    int i = NEW_PERMISSIONS.length;
    paramSet = null;
    paramInt = 0;
    if (paramInt < i)
    {
      paramXmlResourceParser = NEW_PERMISSIONS[paramInt];
      if (paramPackage.applicationInfo.targetSdkVersion < paramXmlResourceParser.sdkVersion) {}
    }
    else
    {
      if (paramSet != null) {
        Slog.i("PackageParser", paramSet.toString());
      }
      i3 = SPLIT_PERMISSIONS.length;
      paramInt = 0;
    }
    for (;;)
    {
      if (paramInt >= i3) {
        break label2866;
      }
      paramSet = SPLIT_PERMISSIONS[paramInt];
      if ((paramPackage.applicationInfo.targetSdkVersion < paramSet.targetSdk) && (paramPackage.requestedPermissions.contains(paramSet.rootPerm)))
      {
        i = 0;
        while (i < paramSet.newPerms.length)
        {
          paramResources = paramSet.newPerms[i];
          if (!paramPackage.requestedPermissions.contains(paramResources)) {
            paramPackage.requestedPermissions.add(paramResources);
          }
          i += 1;
          continue;
          paramResources = paramSet;
          if (!paramPackage.requestedPermissions.contains(paramXmlResourceParser.name))
          {
            if (paramSet != null) {
              break label2847;
            }
            paramSet = new StringBuilder(128);
            paramSet.append(paramPackage.packageName);
            paramSet.append(": compat added ");
          }
          for (;;)
          {
            paramSet.append(paramXmlResourceParser.name);
            paramPackage.requestedPermissions.add(paramXmlResourceParser.name);
            paramResources = paramSet;
            paramInt += 1;
            paramSet = paramResources;
            break;
            label2847:
            paramSet.append(' ');
          }
        }
      }
      paramInt += 1;
    }
    label2866:
    if ((i1 < 0) || ((i1 > 0) && (paramPackage.applicationInfo.targetSdkVersion >= 4)))
    {
      paramSet = paramPackage.applicationInfo;
      paramSet.flags |= 0x200;
    }
    if (n != 0)
    {
      paramSet = paramPackage.applicationInfo;
      paramSet.flags |= 0x400;
    }
    if ((m < 0) || ((m > 0) && (paramPackage.applicationInfo.targetSdkVersion >= 4)))
    {
      paramSet = paramPackage.applicationInfo;
      paramSet.flags |= 0x800;
    }
    if ((j < 0) || ((j > 0) && (paramPackage.applicationInfo.targetSdkVersion >= 9)))
    {
      paramSet = paramPackage.applicationInfo;
      paramSet.flags |= 0x80000;
    }
    if ((k < 0) || ((k > 0) && (paramPackage.applicationInfo.targetSdkVersion >= 4)))
    {
      paramSet = paramPackage.applicationInfo;
      paramSet.flags |= 0x1000;
    }
    if ((i2 < 0) || ((i2 > 0) && (paramPackage.applicationInfo.targetSdkVersion >= 4)))
    {
      paramSet = paramPackage.applicationInfo;
      paramSet.flags |= 0x2000;
    }
    return paramPackage;
  }
  
  private boolean parseBaseApplication(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser, int paramInt, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    Object localObject2 = paramPackage.applicationInfo;
    Object localObject1 = paramPackage.applicationInfo.packageName;
    TypedArray localTypedArray = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestApplication);
    if (!parsePackageItemInfo(paramPackage, (PackageItemInfo)localObject2, paramArrayOfString, "<application>", localTypedArray, false, 3, 1, 2, 42, 22, 30))
    {
      localTypedArray.recycle();
      this.mParseError = -108;
      return false;
    }
    if (((ApplicationInfo)localObject2).name != null) {
      ((ApplicationInfo)localObject2).className = ((ApplicationInfo)localObject2).name;
    }
    String str = localTypedArray.getNonConfigurationString(4, 1024);
    if (str != null) {
      ((ApplicationInfo)localObject2).manageSpaceActivityName = buildClassName((String)localObject1, str, paramArrayOfString);
    }
    if (localTypedArray.getBoolean(17, true))
    {
      ((ApplicationInfo)localObject2).flags |= 0x8000;
      str = localTypedArray.getNonConfigurationString(16, 1024);
      if (str != null)
      {
        ((ApplicationInfo)localObject2).backupAgentName = buildClassName((String)localObject1, str, paramArrayOfString);
        if (localTypedArray.getBoolean(18, true)) {
          ((ApplicationInfo)localObject2).flags |= 0x10000;
        }
        if (localTypedArray.getBoolean(21, false)) {
          ((ApplicationInfo)localObject2).flags |= 0x20000;
        }
        if (localTypedArray.getBoolean(32, false)) {
          ((ApplicationInfo)localObject2).flags |= 0x4000000;
        }
        if (localTypedArray.getBoolean(40, false)) {
          ((ApplicationInfo)localObject2).privateFlags |= 0x1000;
        }
      }
      localObject1 = localTypedArray.peekValue(35);
      if (localObject1 != null)
      {
        i = ((TypedValue)localObject1).resourceId;
        ((ApplicationInfo)localObject2).fullBackupContent = i;
        if (i == 0)
        {
          if (((TypedValue)localObject1).data != 0) {
            break label1082;
          }
          i = -1;
          ((ApplicationInfo)localObject2).fullBackupContent = i;
        }
      }
    }
    ((ApplicationInfo)localObject2).theme = localTypedArray.getResourceId(0, 0);
    ((ApplicationInfo)localObject2).descriptionRes = localTypedArray.getResourceId(13, 0);
    if (((paramInt & 0x1) != 0) && (localTypedArray.getBoolean(8, false))) {
      ((ApplicationInfo)localObject2).flags |= 0x8;
    }
    if (localTypedArray.getBoolean(27, false)) {
      paramPackage.mRequiredForAllUsers = true;
    }
    localObject1 = localTypedArray.getString(28);
    if ((localObject1 != null) && (((String)localObject1).length() > 0)) {
      paramPackage.mRestrictedAccountType = ((String)localObject1);
    }
    localObject1 = localTypedArray.getString(29);
    if ((localObject1 != null) && (((String)localObject1).length() > 0)) {
      paramPackage.mRequiredAccountType = ((String)localObject1);
    }
    if (localTypedArray.getBoolean(10, false)) {
      ((ApplicationInfo)localObject2).flags |= 0x2;
    }
    if (localTypedArray.getBoolean(20, false)) {
      ((ApplicationInfo)localObject2).flags |= 0x4000;
    }
    boolean bool;
    if (paramPackage.applicationInfo.targetSdkVersion >= 14)
    {
      bool = true;
      label510:
      paramPackage.baseHardwareAccelerated = localTypedArray.getBoolean(23, bool);
      if (paramPackage.baseHardwareAccelerated) {
        ((ApplicationInfo)localObject2).flags |= 0x20000000;
      }
      if (localTypedArray.getBoolean(7, true)) {
        ((ApplicationInfo)localObject2).flags |= 0x4;
      }
      if (localTypedArray.getBoolean(14, false)) {
        ((ApplicationInfo)localObject2).flags |= 0x20;
      }
      if (localTypedArray.getBoolean(5, true)) {
        ((ApplicationInfo)localObject2).flags |= 0x40;
      }
      if ((paramPackage.parentPackage == null) && (localTypedArray.getBoolean(15, false))) {
        ((ApplicationInfo)localObject2).flags |= 0x100;
      }
      if (localTypedArray.getBoolean(24, false)) {
        ((ApplicationInfo)localObject2).flags |= 0x100000;
      }
      if (localTypedArray.getBoolean(36, true)) {
        ((ApplicationInfo)localObject2).flags |= 0x8000000;
      }
      if (localTypedArray.getBoolean(26, false)) {
        ((ApplicationInfo)localObject2).flags |= 0x400000;
      }
      if (localTypedArray.getBoolean(33, false)) {
        ((ApplicationInfo)localObject2).flags |= 0x80000000;
      }
      if (localTypedArray.getBoolean(34, true)) {
        ((ApplicationInfo)localObject2).flags |= 0x10000000;
      }
      if (localTypedArray.getBoolean(38, false)) {
        ((ApplicationInfo)localObject2).privateFlags |= 0x20;
      }
      if (localTypedArray.getBoolean(39, false)) {
        ((ApplicationInfo)localObject2).privateFlags |= 0x40;
      }
      if (paramPackage.applicationInfo.targetSdkVersion < 24) {
        break label1094;
      }
      bool = true;
      label834:
      if (localTypedArray.getBoolean(37, bool)) {
        ((ApplicationInfo)localObject2).privateFlags |= 0x800;
      }
      ((ApplicationInfo)localObject2).networkSecurityConfigRes = localTypedArray.getResourceId(41, 0);
      localObject1 = localTypedArray.getNonConfigurationString(6, 0);
      if ((localObject1 == null) || (((String)localObject1).length() <= 0)) {
        break label1100;
      }
      localObject1 = ((String)localObject1).intern();
      label903:
      ((ApplicationInfo)localObject2).permission = ((String)localObject1);
      if (paramPackage.applicationInfo.targetSdkVersion < 8) {
        break label1106;
      }
      localObject1 = localTypedArray.getNonConfigurationString(12, 1024);
      label934:
      ((ApplicationInfo)localObject2).taskAffinity = buildTaskAffinityName(((ApplicationInfo)localObject2).packageName, ((ApplicationInfo)localObject2).packageName, (CharSequence)localObject1, paramArrayOfString);
      if (paramArrayOfString[0] == null) {
        if (paramPackage.applicationInfo.targetSdkVersion < 8) {
          break label1118;
        }
      }
    }
    label1082:
    label1094:
    label1100:
    label1106:
    label1118:
    for (localObject1 = localTypedArray.getNonConfigurationString(11, 1024);; localObject1 = localTypedArray.getNonResourceString(11))
    {
      ((ApplicationInfo)localObject2).processName = buildProcessName(((ApplicationInfo)localObject2).packageName, null, (CharSequence)localObject1, paramInt, this.mSeparateProcesses, paramArrayOfString);
      ((ApplicationInfo)localObject2).enabled = localTypedArray.getBoolean(9, true);
      if (localTypedArray.getBoolean(31, false)) {
        ((ApplicationInfo)localObject2).flags |= 0x2000000;
      }
      ((ApplicationInfo)localObject2).uiOptions = localTypedArray.getInt(25, 0);
      localTypedArray.recycle();
      if (paramArrayOfString[0] == null) {
        break label1130;
      }
      this.mParseError = -108;
      return false;
      i = 0;
      break;
      bool = false;
      break label510;
      bool = false;
      break label834;
      localObject1 = null;
      break label903;
      localObject1 = localTypedArray.getNonResourceString(12);
      break label934;
    }
    label1130:
    int i = paramXmlResourceParser.getDepth();
    for (;;)
    {
      int j = paramXmlResourceParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlResourceParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4))
      {
        localObject1 = paramXmlResourceParser.getName();
        if (((String)localObject1).equals("activity"))
        {
          localObject1 = parseActivity(paramPackage, paramResources, paramXmlResourceParser, paramInt, paramArrayOfString, false, paramPackage.baseHardwareAccelerated);
          if (localObject1 == null)
          {
            this.mParseError = -108;
            return false;
          }
          paramPackage.activities.add(localObject1);
        }
        else if (((String)localObject1).equals("receiver"))
        {
          localObject1 = parseActivity(paramPackage, paramResources, paramXmlResourceParser, paramInt, paramArrayOfString, true, false);
          if (localObject1 == null)
          {
            this.mParseError = -108;
            return false;
          }
          paramPackage.receivers.add(localObject1);
        }
        else if (((String)localObject1).equals("service"))
        {
          localObject1 = parseService(paramPackage, paramResources, paramXmlResourceParser, paramInt, paramArrayOfString);
          if (localObject1 == null)
          {
            this.mParseError = -108;
            return false;
          }
          paramPackage.services.add(localObject1);
        }
        else if (((String)localObject1).equals("provider"))
        {
          localObject1 = parseProvider(paramPackage, paramResources, paramXmlResourceParser, paramInt, paramArrayOfString);
          if (localObject1 == null)
          {
            this.mParseError = -108;
            return false;
          }
          paramPackage.providers.add(localObject1);
        }
        else if (((String)localObject1).equals("activity-alias"))
        {
          localObject1 = parseActivityAlias(paramPackage, paramResources, paramXmlResourceParser, paramInt, paramArrayOfString);
          if (localObject1 == null)
          {
            this.mParseError = -108;
            return false;
          }
          paramPackage.activities.add(localObject1);
        }
        else if (paramXmlResourceParser.getName().equals("meta-data"))
        {
          localObject1 = parseMetaData(paramResources, paramXmlResourceParser, paramPackage.mAppMetaData, paramArrayOfString);
          paramPackage.mAppMetaData = ((Bundle)localObject1);
          if (localObject1 == null)
          {
            this.mParseError = -108;
            return false;
          }
        }
        else if (((String)localObject1).equals("library"))
        {
          localObject1 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestLibrary);
          localObject2 = ((TypedArray)localObject1).getNonResourceString(0);
          ((TypedArray)localObject1).recycle();
          if (localObject2 != null)
          {
            localObject1 = ((String)localObject2).intern();
            if (!ArrayUtils.contains(paramPackage.libraryNames, localObject1)) {
              paramPackage.libraryNames = ArrayUtils.add(paramPackage.libraryNames, localObject1);
            }
          }
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
        }
        else
        {
          if (((String)localObject1).equals("uses-library"))
          {
            localObject1 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestUsesLibrary);
            localObject2 = ((TypedArray)localObject1).getNonResourceString(0);
            bool = ((TypedArray)localObject1).getBoolean(1, true);
            ((TypedArray)localObject1).recycle();
            if (localObject2 != null)
            {
              localObject1 = ((String)localObject2).intern();
              if (!bool) {
                break label1652;
              }
              paramPackage.usesLibraries = ArrayUtils.add(paramPackage.usesLibraries, localObject1);
            }
            for (;;)
            {
              XmlUtils.skipCurrentTag(paramXmlResourceParser);
              break;
              label1652:
              paramPackage.usesOptionalLibraries = ArrayUtils.add(paramPackage.usesOptionalLibraries, localObject1);
            }
          }
          if (((String)localObject1).equals("uses-package"))
          {
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
          }
          else
          {
            Slog.w("PackageParser", "Unknown element under <application>: " + (String)localObject1 + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
          }
        }
      }
    }
    modifySharedLibrariesForBackwardCompatibility(paramPackage);
    if (hasDomainURLs(paramPackage)) {
      paramPackage = paramPackage.applicationInfo;
    }
    for (paramPackage.privateFlags |= 0x10;; paramPackage.privateFlags &= 0xFFFFFFEF)
    {
      return true;
      paramPackage = paramPackage.applicationInfo;
    }
  }
  
  private Package parseClusterPackage(File paramFile, int paramInt)
    throws PackageParser.PackageParserException
  {
    int i = 0;
    PackageLite localPackageLite = parseClusterPackageLite(paramFile, 0);
    if ((this.mOnlyPowerOffAlarmApps) && (!isPowerOffAlarmPackage(localPackageLite.packageName))) {
      throw new PackageParserException(-108, "Not a powerOffAlarmApp: " + paramFile);
    }
    AssetManager localAssetManager;
    if ((this.mOnlyPowerOffAlarmApps) || (!this.mOnlyCoreApps) || (localPackageLite.coreApp)) {
      localAssetManager = new AssetManager();
    }
    int j;
    Package localPackage;
    try
    {
      loadApkIntoAssetManager(localAssetManager, localPackageLite.baseCodePath, paramInt);
      if (!ArrayUtils.isEmpty(localPackageLite.splitCodePaths))
      {
        localObject = localPackageLite.splitCodePaths;
        j = localObject.length;
        while (i < j)
        {
          loadApkIntoAssetManager(localAssetManager, localObject[i], paramInt);
          i += 1;
          continue;
          throw new PackageParserException(-108, "Not a coreApp: " + paramFile);
        }
      }
      Object localObject = new File(localPackageLite.baseCodePath);
      localPackage = parseBaseApk((File)localObject, localAssetManager, paramInt);
      if (localPackage == null) {
        throw new PackageParserException(-100, "Failed to parse base APK: " + localObject);
      }
    }
    finally
    {
      IoUtils.closeQuietly(localAssetManager);
    }
    if (!ArrayUtils.isEmpty(localPackageLite.splitNames))
    {
      j = localPackageLite.splitNames.length;
      localPackage.splitNames = localPackageLite.splitNames;
      localPackage.splitCodePaths = localPackageLite.splitCodePaths;
      localPackage.splitRevisionCodes = localPackageLite.splitRevisionCodes;
      localPackage.splitFlags = new int[j];
      localPackage.splitPrivateFlags = new int[j];
      i = 0;
      while (i < j)
      {
        parseSplitApk(localPackage, i, localAssetManager, paramInt);
        i += 1;
      }
    }
    localPackage.setCodePath(paramFile.getAbsolutePath());
    localPackage.setUse32bitAbi(localPackageLite.use32bitAbi);
    IoUtils.closeQuietly(localAssetManager);
    return localPackage;
  }
  
  private static PackageLite parseClusterPackageLite(File paramFile, int paramInt)
    throws PackageParser.PackageParserException
  {
    Object localObject3 = paramFile.listFiles();
    if (ArrayUtils.isEmpty((Object[])localObject3)) {
      throw new PackageParserException(-100, "No packages found in split");
    }
    Object localObject1 = null;
    int i = 0;
    ArrayMap localArrayMap = new ArrayMap();
    int k = 0;
    int m = localObject3.length;
    Object localObject4;
    Object localObject5;
    while (k < m)
    {
      localObject4 = localObject3[k];
      localObject2 = localObject1;
      int j = i;
      if (isApkFile((File)localObject4))
      {
        localObject5 = parseApkLite((File)localObject4, paramInt);
        if (localObject1 == null)
        {
          localObject1 = ((ApkLite)localObject5).packageName;
          j = ((ApkLite)localObject5).versionCode;
        }
        do
        {
          localObject2 = localObject1;
          if (localArrayMap.put(((ApkLite)localObject5).splitName, localObject5) == null) {
            break;
          }
          throw new PackageParserException(-101, "Split name " + ((ApkLite)localObject5).splitName + " defined more than once; most recent was " + localObject4);
          if (!((String)localObject1).equals(((ApkLite)localObject5).packageName)) {
            throw new PackageParserException(-101, "Inconsistent package " + ((ApkLite)localObject5).packageName + " in " + localObject4 + "; expected " + (String)localObject1);
          }
          j = i;
        } while (i == ((ApkLite)localObject5).versionCode);
        throw new PackageParserException(-101, "Inconsistent version " + ((ApkLite)localObject5).versionCode + " in " + localObject4 + "; expected " + i);
      }
      k += 1;
      localObject1 = localObject2;
      i = j;
    }
    ApkLite localApkLite = (ApkLite)localArrayMap.remove(null);
    if (localApkLite == null) {
      throw new PackageParserException(-101, "Missing base APK in " + paramFile);
    }
    i = localArrayMap.size();
    localObject1 = null;
    Object localObject2 = null;
    localObject3 = null;
    if (i > 0)
    {
      localObject1 = new String[i];
      localObject4 = new String[i];
      localObject5 = new int[i];
      String[] arrayOfString = (String[])localArrayMap.keySet().toArray((Object[])localObject1);
      Arrays.sort(arrayOfString, sSplitNameComparator);
      paramInt = 0;
      for (;;)
      {
        localObject1 = arrayOfString;
        localObject2 = localObject4;
        localObject3 = localObject5;
        if (paramInt >= i) {
          break;
        }
        localObject4[paramInt] = ((ApkLite)localArrayMap.get(arrayOfString[paramInt])).codePath;
        localObject5[paramInt] = ((ApkLite)localArrayMap.get(arrayOfString[paramInt])).revisionCode;
        paramInt += 1;
      }
    }
    return new PackageLite(paramFile.getAbsolutePath(), localApkLite, (String[])localObject1, (String[])localObject2, (int[])localObject3);
  }
  
  private Instrumentation parseInstrumentation(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestInstrumentation);
    if (this.mParseInstrumentationArgs == null)
    {
      this.mParseInstrumentationArgs = new ParsePackageItemArgs(paramPackage, paramArrayOfString, 2, 0, 1, 8, 6, 7);
      this.mParseInstrumentationArgs.tag = "<instrumentation>";
    }
    this.mParseInstrumentationArgs.sa = localTypedArray;
    Instrumentation localInstrumentation = new Instrumentation(this.mParseInstrumentationArgs, new InstrumentationInfo());
    if (paramArrayOfString[0] != null)
    {
      localTypedArray.recycle();
      this.mParseError = -108;
      return null;
    }
    String str = localTypedArray.getNonResourceString(3);
    InstrumentationInfo localInstrumentationInfo = localInstrumentation.info;
    if (str != null) {}
    for (str = str.intern();; str = null)
    {
      localInstrumentationInfo.targetPackage = str;
      localInstrumentation.info.handleProfiling = localTypedArray.getBoolean(4, false);
      localInstrumentation.info.functionalTest = localTypedArray.getBoolean(5, false);
      localTypedArray.recycle();
      if (localInstrumentation.info.targetPackage != null) {
        break;
      }
      paramArrayOfString[0] = "<instrumentation> does not specify targetPackage";
      this.mParseError = -108;
      return null;
    }
    if (!parseAllMetaData(paramResources, paramXmlResourceParser, "<instrumentation>", localInstrumentation, paramArrayOfString))
    {
      this.mParseError = -108;
      return null;
    }
    paramPackage.instrumentation.add(localInstrumentation);
    return localInstrumentation;
  }
  
  private boolean parseIntent(Resources paramResources, XmlResourceParser paramXmlResourceParser, boolean paramBoolean1, boolean paramBoolean2, IntentInfo paramIntentInfo, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    Object localObject1 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestIntentFilter);
    paramIntentInfo.setPriority(((TypedArray)localObject1).getInt(2, 0));
    Object localObject2 = ((TypedArray)localObject1).peekValue(0);
    int i;
    if (localObject2 != null)
    {
      i = ((TypedValue)localObject2).resourceId;
      paramIntentInfo.labelRes = i;
      if (i == 0) {
        paramIntentInfo.nonLocalizedLabel = ((TypedValue)localObject2).coerceToString();
      }
    }
    if (Resources.getSystem().getBoolean(17957076))
    {
      i = ((TypedArray)localObject1).getResourceId(6, 0);
      if (i == 0) {
        break label255;
      }
      paramIntentInfo.icon = i;
      label98:
      paramIntentInfo.logo = ((TypedArray)localObject1).getResourceId(3, 0);
      paramIntentInfo.banner = ((TypedArray)localObject1).getResourceId(4, 0);
      if (paramBoolean2) {
        paramIntentInfo.setAutoVerify(((TypedArray)localObject1).getBoolean(5, false));
      }
      ((TypedArray)localObject1).recycle();
      i = paramXmlResourceParser.getDepth();
    }
    for (;;)
    {
      int j = paramXmlResourceParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlResourceParser.getDepth() <= i))) {
        break label695;
      }
      if ((j != 3) && (j != 4))
      {
        localObject1 = paramXmlResourceParser.getName();
        if (((String)localObject1).equals("action"))
        {
          localObject1 = paramXmlResourceParser.getAttributeValue("http://schemas.android.com/apk/res/android", "name");
          if ((localObject1 == null) || (localObject1 == ""))
          {
            paramArrayOfString[0] = "No value supplied for <android:name>";
            return false;
            i = 0;
            break;
            label255:
            paramIntentInfo.icon = ((TypedArray)localObject1).getResourceId(1, 0);
            break label98;
          }
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
          paramIntentInfo.addAction((String)localObject1);
          continue;
        }
        if (((String)localObject1).equals("category"))
        {
          localObject1 = paramXmlResourceParser.getAttributeValue("http://schemas.android.com/apk/res/android", "name");
          if ((localObject1 == null) || (localObject1 == ""))
          {
            paramArrayOfString[0] = "No value supplied for <android:name>";
            return false;
          }
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
          paramIntentInfo.addCategory((String)localObject1);
        }
        else if (((String)localObject1).equals("data"))
        {
          localObject1 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestData);
          localObject2 = ((TypedArray)localObject1).getNonConfigurationString(0, 0);
          if (localObject2 != null) {}
          try
          {
            paramIntentInfo.addDataType((String)localObject2);
            localObject2 = ((TypedArray)localObject1).getNonConfigurationString(1, 0);
            if (localObject2 != null) {
              paramIntentInfo.addDataScheme((String)localObject2);
            }
            localObject2 = ((TypedArray)localObject1).getNonConfigurationString(7, 0);
            if (localObject2 != null) {
              paramIntentInfo.addDataSchemeSpecificPart((String)localObject2, 0);
            }
            localObject2 = ((TypedArray)localObject1).getNonConfigurationString(8, 0);
            if (localObject2 != null) {
              paramIntentInfo.addDataSchemeSpecificPart((String)localObject2, 1);
            }
            localObject2 = ((TypedArray)localObject1).getNonConfigurationString(9, 0);
            if (localObject2 == null) {
              break label505;
            }
            if (!paramBoolean1)
            {
              paramArrayOfString[0] = "sspPattern not allowed here; ssp must be literal";
              return false;
            }
          }
          catch (IntentFilter.MalformedMimeTypeException paramResources)
          {
            paramArrayOfString[0] = paramResources.toString();
            ((TypedArray)localObject1).recycle();
            return false;
          }
          paramIntentInfo.addDataSchemeSpecificPart((String)localObject2, 2);
          label505:
          localObject2 = ((TypedArray)localObject1).getNonConfigurationString(2, 0);
          String str = ((TypedArray)localObject1).getNonConfigurationString(3, 0);
          if (localObject2 != null) {
            paramIntentInfo.addDataAuthority((String)localObject2, str);
          }
          localObject2 = ((TypedArray)localObject1).getNonConfigurationString(4, 0);
          if (localObject2 != null) {
            paramIntentInfo.addDataPath((String)localObject2, 0);
          }
          localObject2 = ((TypedArray)localObject1).getNonConfigurationString(5, 0);
          if (localObject2 != null) {
            paramIntentInfo.addDataPath((String)localObject2, 1);
          }
          localObject2 = ((TypedArray)localObject1).getNonConfigurationString(6, 0);
          if (localObject2 != null)
          {
            if (!paramBoolean1)
            {
              paramArrayOfString[0] = "pathPattern not allowed here; path must be literal";
              return false;
            }
            paramIntentInfo.addDataPath((String)localObject2, 2);
          }
          ((TypedArray)localObject1).recycle();
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
        }
        else
        {
          Slog.w("PackageParser", "Unknown element under <intent-filter>: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
        }
      }
    }
    label695:
    paramIntentInfo.hasDefault = paramIntentInfo.hasCategory("android.intent.category.DEFAULT");
    return true;
  }
  
  private boolean parseKeySets(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    int j = paramXmlResourceParser.getDepth();
    int i = -1;
    Object localObject1 = null;
    ArrayMap localArrayMap = new ArrayMap();
    ArraySet localArraySet1 = new ArraySet();
    Object localObject2 = new ArrayMap();
    ArraySet localArraySet2 = new ArraySet();
    for (;;)
    {
      int k = paramXmlResourceParser.next();
      if ((k == 1) || ((k == 3) && (paramXmlResourceParser.getDepth() <= j))) {
        break;
      }
      if (k == 3)
      {
        if (paramXmlResourceParser.getDepth() == i)
        {
          localObject1 = null;
          i = -1;
        }
      }
      else
      {
        Object localObject3 = paramXmlResourceParser.getName();
        if (((String)localObject3).equals("key-set"))
        {
          if (localObject1 != null)
          {
            paramArrayOfString[0] = ("Improperly nested 'key-set' tag at " + paramXmlResourceParser.getPositionDescription());
            this.mParseError = -108;
            return false;
          }
          localObject3 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestKeySet);
          localObject1 = ((TypedArray)localObject3).getNonResourceString(0);
          ((ArrayMap)localObject2).put(localObject1, new ArraySet());
          i = paramXmlResourceParser.getDepth();
          ((TypedArray)localObject3).recycle();
        }
        else if (((String)localObject3).equals("public-key"))
        {
          if (localObject1 == null)
          {
            paramArrayOfString[0] = ("Improperly nested 'key-set' tag at " + paramXmlResourceParser.getPositionDescription());
            this.mParseError = -108;
            return false;
          }
          localObject3 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestPublicKey);
          String str = ((TypedArray)localObject3).getNonResourceString(0);
          Object localObject4 = ((TypedArray)localObject3).getNonResourceString(1);
          if ((localObject4 == null) && (localArrayMap.get(str) == null))
          {
            paramArrayOfString[0] = ("'public-key' " + str + " must define a public-key value" + " on first use at " + paramXmlResourceParser.getPositionDescription());
            this.mParseError = -108;
            ((TypedArray)localObject3).recycle();
            return false;
          }
          if (localObject4 != null)
          {
            localObject4 = parsePublicKey((String)localObject4);
            if (localObject4 == null)
            {
              Slog.w("PackageParser", "No recognized valid key in 'public-key' tag at " + paramXmlResourceParser.getPositionDescription() + " key-set " + (String)localObject1 + " will not be added to the package's defined key-sets.");
              ((TypedArray)localObject3).recycle();
              localArraySet2.add(localObject1);
              XmlUtils.skipCurrentTag(paramXmlResourceParser);
              continue;
            }
            if ((localArrayMap.get(str) == null) || (((PublicKey)localArrayMap.get(str)).equals(localObject4))) {
              localArrayMap.put(str, localObject4);
            }
          }
          else
          {
            ((ArraySet)((ArrayMap)localObject2).get(localObject1)).add(str);
            ((TypedArray)localObject3).recycle();
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
            continue;
          }
          paramArrayOfString[0] = ("Value of 'public-key' " + str + " conflicts with previously defined value at " + paramXmlResourceParser.getPositionDescription());
          this.mParseError = -108;
          ((TypedArray)localObject3).recycle();
          return false;
        }
        else if (((String)localObject3).equals("upgrade-key-set"))
        {
          localObject3 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestUpgradeKeySet);
          localArraySet1.add(((TypedArray)localObject3).getNonResourceString(0));
          ((TypedArray)localObject3).recycle();
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
        }
        else
        {
          Slog.w("PackageParser", "Unknown element under <key-sets>: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
        }
      }
    }
    if (localArrayMap.keySet().removeAll(((ArrayMap)localObject2).keySet()))
    {
      paramArrayOfString[0] = ("Package" + paramPackage.packageName + " AndroidManifext.xml " + "'key-set' and 'public-key' names must be distinct.");
      this.mParseError = -108;
      return false;
    }
    paramPackage.mKeySetMapping = new ArrayMap();
    paramResources = ((ArrayMap)localObject2).entrySet().iterator();
    while (paramResources.hasNext())
    {
      localObject1 = (Map.Entry)paramResources.next();
      paramXmlResourceParser = (String)((Map.Entry)localObject1).getKey();
      if (((ArraySet)((Map.Entry)localObject1).getValue()).size() == 0)
      {
        Slog.w("PackageParser", "Package" + paramPackage.packageName + " AndroidManifext.xml " + "'key-set' " + paramXmlResourceParser + " has no valid associated 'public-key'." + " Not including in package's defined key-sets.");
      }
      else if (localArraySet2.contains(paramXmlResourceParser))
      {
        Slog.w("PackageParser", "Package" + paramPackage.packageName + " AndroidManifext.xml " + "'key-set' " + paramXmlResourceParser + " contained improper 'public-key'" + " tags. Not including in package's defined key-sets.");
      }
      else
      {
        paramPackage.mKeySetMapping.put(paramXmlResourceParser, new ArraySet());
        localObject1 = ((ArraySet)((Map.Entry)localObject1).getValue()).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (String)((Iterator)localObject1).next();
          ((ArraySet)paramPackage.mKeySetMapping.get(paramXmlResourceParser)).add((PublicKey)localArrayMap.get(localObject2));
        }
      }
    }
    if (paramPackage.mKeySetMapping.keySet().containsAll(localArraySet1))
    {
      paramPackage.mUpgradeKeySets = localArraySet1;
      return true;
    }
    paramArrayOfString[0] = ("Package" + paramPackage.packageName + " AndroidManifext.xml " + "does not define all 'upgrade-key-set's .");
    this.mParseError = -108;
    return false;
  }
  
  private void parseLayout(Resources paramResources, AttributeSet paramAttributeSet, Activity paramActivity)
  {
    paramResources = paramResources.obtainAttributes(paramAttributeSet, R.styleable.AndroidManifestLayout);
    int i = -1;
    float f2 = -1.0F;
    int j = -1;
    float f3 = -1.0F;
    int k = paramResources.getType(3);
    float f1;
    if (k == 6)
    {
      f1 = paramResources.getFraction(3, 1, 1, -1.0F);
      k = paramResources.getType(4);
      if (k != 6) {
        break label160;
      }
      f2 = paramResources.getFraction(4, 1, 1, -1.0F);
    }
    for (;;)
    {
      k = paramResources.getInt(0, 17);
      int m = paramResources.getDimensionPixelSize(1, -1);
      int n = paramResources.getDimensionPixelSize(2, -1);
      paramResources.recycle();
      paramActivity.info.windowLayout = new ActivityInfo.WindowLayout(i, f1, j, f2, k, m, n);
      return;
      f1 = f2;
      if (k != 5) {
        break;
      }
      i = paramResources.getDimensionPixelSize(3, -1);
      f1 = f2;
      break;
      label160:
      f2 = f3;
      if (k == 5)
      {
        j = paramResources.getDimensionPixelSize(4, -1);
        f2 = f3;
      }
    }
  }
  
  private Bundle parseMetaData(Resources paramResources, XmlResourceParser paramXmlResourceParser, Bundle paramBundle, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    Object localObject = null;
    TypedArray localTypedArray = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestMetaData);
    paramResources = paramBundle;
    if (paramBundle == null) {
      paramResources = new Bundle();
    }
    paramBundle = localTypedArray.getNonConfigurationString(0, 0);
    if (paramBundle == null)
    {
      paramArrayOfString[0] = "<meta-data> requires an android:name attribute";
      localTypedArray.recycle();
      return null;
    }
    String str = paramBundle.intern();
    paramBundle = localTypedArray.peekValue(2);
    if ((paramBundle != null) && (paramBundle.resourceId != 0)) {
      paramResources.putInt(str, paramBundle.resourceId);
    }
    for (;;)
    {
      localTypedArray.recycle();
      XmlUtils.skipCurrentTag(paramXmlResourceParser);
      return paramResources;
      paramBundle = localTypedArray.peekValue(1);
      if (paramBundle != null)
      {
        if (paramBundle.type == 3)
        {
          paramArrayOfString = paramBundle.coerceToString();
          paramBundle = (Bundle)localObject;
          if (paramArrayOfString != null) {
            paramBundle = paramArrayOfString.toString().intern();
          }
          paramResources.putString(str, paramBundle);
        }
        else
        {
          if (paramBundle.type == 18)
          {
            if (paramBundle.data != 0) {}
            for (boolean bool = true;; bool = false)
            {
              paramResources.putBoolean(str, bool);
              break;
            }
          }
          if ((paramBundle.type >= 16) && (paramBundle.type <= 31)) {
            paramResources.putInt(str, paramBundle.data);
          } else if (paramBundle.type == 4) {
            paramResources.putFloat(str, paramBundle.getFloat());
          } else {
            Slog.w("PackageParser", "<meta-data> only supports string, integer, float, color, boolean, and resource reference types: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
          }
        }
      }
      else
      {
        paramArrayOfString[0] = "<meta-data> requires an android:value or android:resource attribute";
        paramResources = null;
      }
    }
  }
  
  private static PackageLite parseMonolithicPackageLite(File paramFile, int paramInt)
    throws PackageParser.PackageParserException
  {
    ApkLite localApkLite = parseApkLite(paramFile, paramInt);
    return new PackageLite(paramFile.getAbsolutePath(), localApkLite, null, null, null);
  }
  
  private static boolean parsePackageItemInfo(Package paramPackage, PackageItemInfo paramPackageItemInfo, String[] paramArrayOfString, String paramString, TypedArray paramTypedArray, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    String str = paramTypedArray.getNonConfigurationString(paramInt1, 0);
    if (str == null)
    {
      if (paramBoolean)
      {
        paramArrayOfString[0] = (paramString + " does not specify android:name");
        return false;
      }
    }
    else
    {
      paramPackageItemInfo.name = buildClassName(paramPackage.applicationInfo.packageName, str, paramArrayOfString);
      if (paramPackageItemInfo.name == null) {
        return false;
      }
    }
    if (Resources.getSystem().getBoolean(17957076))
    {
      paramInt1 = paramTypedArray.getResourceId(paramInt4, 0);
      if (paramInt1 == 0) {
        break label204;
      }
      paramPackageItemInfo.icon = paramInt1;
      paramPackageItemInfo.nonLocalizedLabel = null;
    }
    for (;;)
    {
      paramInt1 = paramTypedArray.getResourceId(paramInt5, 0);
      if (paramInt1 != 0) {
        paramPackageItemInfo.logo = paramInt1;
      }
      paramInt1 = paramTypedArray.getResourceId(paramInt6, 0);
      if (paramInt1 != 0) {
        paramPackageItemInfo.banner = paramInt1;
      }
      paramArrayOfString = paramTypedArray.peekValue(paramInt2);
      if (paramArrayOfString != null)
      {
        paramInt1 = paramArrayOfString.resourceId;
        paramPackageItemInfo.labelRes = paramInt1;
        if (paramInt1 == 0) {
          paramPackageItemInfo.nonLocalizedLabel = paramArrayOfString.coerceToString();
        }
      }
      paramPackageItemInfo.packageName = paramPackage.packageName;
      return true;
      paramInt1 = 0;
      break;
      label204:
      paramInt1 = paramTypedArray.getResourceId(paramInt3, 0);
      if (paramInt1 != 0)
      {
        paramPackageItemInfo.icon = paramInt1;
        paramPackageItemInfo.nonLocalizedLabel = null;
      }
    }
  }
  
  public static PackageLite parsePackageLite(File paramFile, int paramInt)
    throws PackageParser.PackageParserException
  {
    if (paramFile.isDirectory()) {
      return parseClusterPackageLite(paramFile, paramInt);
    }
    return parseMonolithicPackageLite(paramFile, paramInt);
  }
  
  private static Pair<String, String> parsePackageSplitNames(XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet)
    throws IOException, XmlPullParserException, PackageParser.PackageParserException
  {
    int i;
    do
    {
      i = paramXmlPullParser.next();
    } while ((i != 2) && (i != 1));
    if (i != 2) {
      throw new PackageParserException(-108, "No start tag found");
    }
    if (!paramXmlPullParser.getName().equals("manifest")) {
      throw new PackageParserException(-108, "No <manifest> tag");
    }
    String str1 = paramAttributeSet.getAttributeValue(null, "package");
    if (!"android".equals(str1))
    {
      paramXmlPullParser = validateName(str1, true, true);
      if (paramXmlPullParser != null) {
        throw new PackageParserException(-106, "Invalid manifest package: " + paramXmlPullParser);
      }
    }
    paramAttributeSet = paramAttributeSet.getAttributeValue(null, "split");
    paramXmlPullParser = paramAttributeSet;
    if (paramAttributeSet != null)
    {
      if (paramAttributeSet.length() != 0) {
        break label171;
      }
      paramXmlPullParser = null;
    }
    label171:
    String str2;
    do
    {
      str1 = str1.intern();
      paramAttributeSet = paramXmlPullParser;
      if (paramXmlPullParser != null) {
        paramAttributeSet = paramXmlPullParser.intern();
      }
      return Pair.create(str1, paramAttributeSet);
      str2 = validateName(paramAttributeSet, false, false);
      paramXmlPullParser = paramAttributeSet;
    } while (str2 == null);
    throw new PackageParserException(-106, "Invalid manifest split: " + str2);
  }
  
  private Permission parsePermission(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    Permission localPermission = new Permission(paramPackage);
    TypedArray localTypedArray = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestPermission);
    if (!parsePackageItemInfo(paramPackage, localPermission.info, paramArrayOfString, "<permission>", localTypedArray, true, 2, 0, 1, 9, 6, 8))
    {
      localTypedArray.recycle();
      this.mParseError = -108;
      return null;
    }
    localPermission.info.group = localTypedArray.getNonResourceString(4);
    if (localPermission.info.group != null) {
      localPermission.info.group = localPermission.info.group.intern();
    }
    localPermission.info.descriptionRes = localTypedArray.getResourceId(5, 0);
    localPermission.info.protectionLevel = localTypedArray.getInt(3, 0);
    localPermission.info.flags = localTypedArray.getInt(7, 0);
    localTypedArray.recycle();
    if (localPermission.info.protectionLevel == -1)
    {
      paramArrayOfString[0] = "<permission> does not specify protectionLevel";
      this.mParseError = -108;
      return null;
    }
    localPermission.info.protectionLevel = PermissionInfo.fixProtectionLevel(localPermission.info.protectionLevel);
    if (((localPermission.info.protectionLevel & 0xFF0) != 0) && ((localPermission.info.protectionLevel & 0xF) != 2))
    {
      paramArrayOfString[0] = "<permission>  protectionLevel specifies a flag but is not based on signature type";
      this.mParseError = -108;
      return null;
    }
    if (!parseAllMetaData(paramResources, paramXmlResourceParser, "<permission>", localPermission, paramArrayOfString))
    {
      this.mParseError = -108;
      return null;
    }
    paramPackage.permissions.add(localPermission);
    return localPermission;
  }
  
  private PermissionGroup parsePermissionGroup(Package paramPackage, int paramInt, Resources paramResources, XmlResourceParser paramXmlResourceParser, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    PermissionGroup localPermissionGroup = new PermissionGroup(paramPackage);
    TypedArray localTypedArray = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestPermissionGroup);
    if (!parsePackageItemInfo(paramPackage, localPermissionGroup.info, paramArrayOfString, "<permission-group>", localTypedArray, true, 2, 0, 1, 8, 5, 7))
    {
      localTypedArray.recycle();
      this.mParseError = -108;
      return null;
    }
    localPermissionGroup.info.descriptionRes = localTypedArray.getResourceId(4, 0);
    localPermissionGroup.info.flags = localTypedArray.getInt(6, 0);
    localPermissionGroup.info.priority = localTypedArray.getInt(3, 0);
    if ((localPermissionGroup.info.priority > 0) && ((paramInt & 0x1) == 0)) {
      localPermissionGroup.info.priority = 0;
    }
    localTypedArray.recycle();
    if (!parseAllMetaData(paramResources, paramXmlResourceParser, "<permission-group>", localPermissionGroup, paramArrayOfString))
    {
      this.mParseError = -108;
      return null;
    }
    paramPackage.permissionGroups.add(localPermissionGroup);
    return localPermissionGroup;
  }
  
  private Permission parsePermissionTree(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    Permission localPermission = new Permission(paramPackage);
    TypedArray localTypedArray = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestPermissionTree);
    if (!parsePackageItemInfo(paramPackage, localPermission.info, paramArrayOfString, "<permission-tree>", localTypedArray, true, 2, 0, 1, 5, 3, 4))
    {
      localTypedArray.recycle();
      this.mParseError = -108;
      return null;
    }
    localTypedArray.recycle();
    int j = localPermission.info.name.indexOf('.');
    int i = j;
    if (j > 0) {
      i = localPermission.info.name.indexOf('.', j + 1);
    }
    if (i < 0)
    {
      paramArrayOfString[0] = ("<permission-tree> name has less than three segments: " + localPermission.info.name);
      this.mParseError = -108;
      return null;
    }
    localPermission.info.descriptionRes = 0;
    localPermission.info.protectionLevel = 0;
    localPermission.tree = true;
    if (!parseAllMetaData(paramResources, paramXmlResourceParser, "<permission-tree>", localPermission, paramArrayOfString))
    {
      this.mParseError = -108;
      return null;
    }
    paramPackage.permissions.add(localPermission);
    return localPermission;
  }
  
  private Provider parseProvider(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser, int paramInt, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestProvider);
    if (this.mParseProviderArgs == null)
    {
      this.mParseProviderArgs = new ParseComponentArgs(paramPackage, paramArrayOfString, 2, 0, 1, 19, 15, 17, this.mSeparateProcesses, 8, 14, 6);
      this.mParseProviderArgs.tag = "<provider>";
    }
    this.mParseProviderArgs.sa = localTypedArray;
    this.mParseProviderArgs.flags = paramInt;
    Provider localProvider = new Provider(this.mParseProviderArgs, new ProviderInfo());
    if (paramArrayOfString[0] != null)
    {
      localTypedArray.recycle();
      return null;
    }
    boolean bool = false;
    if (paramPackage.applicationInfo.targetSdkVersion < 17) {
      bool = true;
    }
    localProvider.info.exported = localTypedArray.getBoolean(7, bool);
    String str = localTypedArray.getNonConfigurationString(10, 0);
    localProvider.info.isSyncable = localTypedArray.getBoolean(11, false);
    Object localObject1 = localTypedArray.getNonConfigurationString(3, 0);
    Object localObject3 = localTypedArray.getNonConfigurationString(4, 0);
    Object localObject2 = localObject3;
    if (localObject3 == null) {
      localObject2 = localObject1;
    }
    if (localObject2 == null)
    {
      localProvider.info.readPermission = paramPackage.applicationInfo.permission;
      localObject3 = localTypedArray.getNonConfigurationString(5, 0);
      localObject2 = localObject3;
      if (localObject3 == null) {
        localObject2 = localObject1;
      }
      if (localObject2 == null)
      {
        localProvider.info.writePermission = paramPackage.applicationInfo.permission;
        localProvider.info.grantUriPermissions = localTypedArray.getBoolean(13, false);
        localProvider.info.multiprocess = localTypedArray.getBoolean(9, false);
        localProvider.info.initOrder = localTypedArray.getInt(12, 0);
        localProvider.info.flags = 0;
        if (localTypedArray.getBoolean(16, false))
        {
          localObject1 = localProvider.info;
          ((ProviderInfo)localObject1).flags |= 0x40000000;
          if ((localProvider.info.exported) && ((paramInt & 0x80) == 0))
          {
            Slog.w("PackageParser", "Provider exported request ignored due to singleUser: " + localProvider.className + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
            localProvider.info.exported = false;
          }
        }
        localObject1 = localProvider.info;
        bool = localTypedArray.getBoolean(18, false);
        localProvider.info.directBootAware = bool;
        ((ProviderInfo)localObject1).encryptionAware = bool;
        if (localProvider.info.directBootAware)
        {
          localObject1 = paramPackage.applicationInfo;
          ((ApplicationInfo)localObject1).privateFlags |= 0x100;
        }
        localTypedArray.recycle();
        if (((paramPackage.applicationInfo.privateFlags & 0x2) == 0) || (localProvider.info.processName != paramPackage.packageName)) {
          break label630;
        }
        paramArrayOfString[0] = "Heavy-weight applications can not have providers in main process";
        return null;
      }
    }
    else
    {
      localObject3 = localProvider.info;
      if (((String)localObject2).length() > 0) {}
      for (localObject2 = ((String)localObject2).toString().intern();; localObject2 = null)
      {
        ((ProviderInfo)localObject3).readPermission = ((String)localObject2);
        break;
      }
    }
    localObject3 = localProvider.info;
    if (((String)localObject2).length() > 0) {}
    for (localObject1 = ((String)localObject2).toString().intern();; localObject1 = null)
    {
      ((ProviderInfo)localObject3).writePermission = ((String)localObject1);
      break;
    }
    label630:
    if (str == null)
    {
      paramArrayOfString[0] = "<provider> does not include authorities attribute";
      return null;
    }
    if (str.length() <= 0)
    {
      paramArrayOfString[0] = "<provider> has empty authorities attribute";
      return null;
    }
    localProvider.info.authority = str.intern();
    if (!parseProviderTags(paramResources, paramXmlResourceParser, localProvider, paramArrayOfString)) {
      return null;
    }
    return localProvider;
  }
  
  private boolean parseProviderTags(Resources paramResources, XmlResourceParser paramXmlResourceParser, Provider paramProvider, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    int j = paramXmlResourceParser.getDepth();
    for (;;)
    {
      int i = paramXmlResourceParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlResourceParser.getDepth() <= j))) {
        break;
      }
      if ((i != 3) && (i != 4))
      {
        Object localObject1;
        if (paramXmlResourceParser.getName().equals("intent-filter"))
        {
          localObject1 = new ProviderIntentInfo(paramProvider);
          if (!parseIntent(paramResources, paramXmlResourceParser, true, false, (IntentInfo)localObject1, paramArrayOfString)) {
            return false;
          }
          paramProvider.intents.add(localObject1);
        }
        else if (paramXmlResourceParser.getName().equals("meta-data"))
        {
          localObject1 = parseMetaData(paramResources, paramXmlResourceParser, paramProvider.metaData, paramArrayOfString);
          paramProvider.metaData = ((Bundle)localObject1);
          if (localObject1 == null) {
            return false;
          }
        }
        else
        {
          Object localObject2;
          Object localObject3;
          if (paramXmlResourceParser.getName().equals("grant-uri-permission"))
          {
            localObject2 = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestGrantUriPermission);
            localObject1 = null;
            localObject3 = ((TypedArray)localObject2).getNonConfigurationString(0, 0);
            if (localObject3 != null) {
              localObject1 = new PatternMatcher((String)localObject3, 0);
            }
            localObject3 = ((TypedArray)localObject2).getNonConfigurationString(1, 0);
            if (localObject3 != null) {
              localObject1 = new PatternMatcher((String)localObject3, 1);
            }
            localObject3 = ((TypedArray)localObject2).getNonConfigurationString(2, 0);
            if (localObject3 != null) {
              localObject1 = new PatternMatcher((String)localObject3, 2);
            }
            ((TypedArray)localObject2).recycle();
            if (localObject1 != null)
            {
              if (paramProvider.info.uriPermissionPatterns == null)
              {
                paramProvider.info.uriPermissionPatterns = new PatternMatcher[1];
                paramProvider.info.uriPermissionPatterns[0] = localObject1;
              }
              for (;;)
              {
                paramProvider.info.grantUriPermissions = true;
                XmlUtils.skipCurrentTag(paramXmlResourceParser);
                break;
                i = paramProvider.info.uriPermissionPatterns.length;
                localObject2 = new PatternMatcher[i + 1];
                System.arraycopy(paramProvider.info.uriPermissionPatterns, 0, localObject2, 0, i);
                localObject2[i] = localObject1;
                paramProvider.info.uriPermissionPatterns = ((PatternMatcher[])localObject2);
              }
            }
            Slog.w("PackageParser", "Unknown element under <path-permission>: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
          }
          else if (paramXmlResourceParser.getName().equals("path-permission"))
          {
            TypedArray localTypedArray = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestPathPermission);
            Object localObject5 = null;
            localObject3 = localTypedArray.getNonConfigurationString(0, 0);
            localObject2 = localTypedArray.getNonConfigurationString(1, 0);
            localObject1 = localObject2;
            if (localObject2 == null) {
              localObject1 = localObject3;
            }
            Object localObject4 = localTypedArray.getNonConfigurationString(2, 0);
            localObject2 = localObject4;
            if (localObject4 == null) {
              localObject2 = localObject3;
            }
            i = 0;
            localObject3 = localObject1;
            if (localObject1 != null)
            {
              localObject3 = ((String)localObject1).intern();
              i = 1;
            }
            localObject4 = localObject2;
            if (localObject2 != null)
            {
              localObject4 = ((String)localObject2).intern();
              i = 1;
            }
            if (i == 0)
            {
              Slog.w("PackageParser", "No readPermission or writePermssion for <path-permission>: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
              XmlUtils.skipCurrentTag(paramXmlResourceParser);
            }
            else
            {
              localObject2 = localTypedArray.getNonConfigurationString(3, 0);
              localObject1 = localObject5;
              if (localObject2 != null) {
                localObject1 = new PathPermission((String)localObject2, 0, (String)localObject3, (String)localObject4);
              }
              localObject2 = localTypedArray.getNonConfigurationString(4, 0);
              if (localObject2 != null) {
                localObject1 = new PathPermission((String)localObject2, 1, (String)localObject3, (String)localObject4);
              }
              localObject2 = localTypedArray.getNonConfigurationString(5, 0);
              if (localObject2 != null) {
                localObject1 = new PathPermission((String)localObject2, 2, (String)localObject3, (String)localObject4);
              }
              localTypedArray.recycle();
              if (localObject1 != null)
              {
                if (paramProvider.info.pathPermissions == null)
                {
                  paramProvider.info.pathPermissions = new PathPermission[1];
                  paramProvider.info.pathPermissions[0] = localObject1;
                }
                for (;;)
                {
                  XmlUtils.skipCurrentTag(paramXmlResourceParser);
                  break;
                  i = paramProvider.info.pathPermissions.length;
                  localObject2 = new PathPermission[i + 1];
                  System.arraycopy(paramProvider.info.pathPermissions, 0, localObject2, 0, i);
                  localObject2[i] = localObject1;
                  paramProvider.info.pathPermissions = ((PathPermission[])localObject2);
                }
              }
              Slog.w("PackageParser", "No path, pathPrefix, or pathPattern for <path-permission>: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
              XmlUtils.skipCurrentTag(paramXmlResourceParser);
            }
          }
          else
          {
            Slog.w("PackageParser", "Unknown element under <provider>: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
          }
        }
      }
    }
    return true;
  }
  
  /* Error */
  public static final PublicKey parsePublicKey(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +14 -> 15
    //   4: ldc -128
    //   6: ldc_w 2311
    //   9: invokestatic 1014	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   12: pop
    //   13: aconst_null
    //   14: areturn
    //   15: new 2313	java/security/spec/X509EncodedKeySpec
    //   18: dup
    //   19: aload_0
    //   20: iconst_0
    //   21: invokestatic 2319	android/util/Base64:decode	(Ljava/lang/String;I)[B
    //   24: invokespecial 2322	java/security/spec/X509EncodedKeySpec:<init>	([B)V
    //   27: astore_0
    //   28: ldc_w 2324
    //   31: invokestatic 2330	java/security/KeyFactory:getInstance	(Ljava/lang/String;)Ljava/security/KeyFactory;
    //   34: aload_0
    //   35: invokevirtual 2334	java/security/KeyFactory:generatePublic	(Ljava/security/spec/KeySpec;)Ljava/security/PublicKey;
    //   38: astore_1
    //   39: aload_1
    //   40: areturn
    //   41: astore_0
    //   42: ldc -128
    //   44: ldc_w 2336
    //   47: invokestatic 1014	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   50: pop
    //   51: aconst_null
    //   52: areturn
    //   53: astore_1
    //   54: ldc -128
    //   56: ldc_w 2338
    //   59: invokestatic 2341	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   62: pop
    //   63: ldc_w 2343
    //   66: invokestatic 2330	java/security/KeyFactory:getInstance	(Ljava/lang/String;)Ljava/security/KeyFactory;
    //   69: aload_0
    //   70: invokevirtual 2334	java/security/KeyFactory:generatePublic	(Ljava/security/spec/KeySpec;)Ljava/security/PublicKey;
    //   73: astore_1
    //   74: aload_1
    //   75: areturn
    //   76: astore_1
    //   77: ldc -128
    //   79: ldc_w 2345
    //   82: invokestatic 2341	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   85: pop
    //   86: ldc_w 2347
    //   89: invokestatic 2330	java/security/KeyFactory:getInstance	(Ljava/lang/String;)Ljava/security/KeyFactory;
    //   92: aload_0
    //   93: invokevirtual 2334	java/security/KeyFactory:generatePublic	(Ljava/security/spec/KeySpec;)Ljava/security/PublicKey;
    //   96: astore_0
    //   97: aload_0
    //   98: areturn
    //   99: astore_0
    //   100: ldc -128
    //   102: ldc_w 2349
    //   105: invokestatic 2341	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   108: pop
    //   109: aconst_null
    //   110: areturn
    //   111: astore_0
    //   112: aconst_null
    //   113: areturn
    //   114: astore_1
    //   115: goto -29 -> 86
    //   118: astore_1
    //   119: goto -56 -> 63
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	122	0	paramString	String
    //   38	2	1	localPublicKey1	PublicKey
    //   53	1	1	localNoSuchAlgorithmException1	java.security.NoSuchAlgorithmException
    //   73	2	1	localPublicKey2	PublicKey
    //   76	1	1	localNoSuchAlgorithmException2	java.security.NoSuchAlgorithmException
    //   114	1	1	localInvalidKeySpecException1	java.security.spec.InvalidKeySpecException
    //   118	1	1	localInvalidKeySpecException2	java.security.spec.InvalidKeySpecException
    // Exception table:
    //   from	to	target	type
    //   15	28	41	java/lang/IllegalArgumentException
    //   28	39	53	java/security/NoSuchAlgorithmException
    //   63	74	76	java/security/NoSuchAlgorithmException
    //   86	97	99	java/security/NoSuchAlgorithmException
    //   86	97	111	java/security/spec/InvalidKeySpecException
    //   63	74	114	java/security/spec/InvalidKeySpecException
    //   28	39	118	java/security/spec/InvalidKeySpecException
  }
  
  private Service parseService(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser, int paramInt, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestService);
    if (this.mParseServiceArgs == null)
    {
      this.mParseServiceArgs = new ParseComponentArgs(paramPackage, paramArrayOfString, 2, 0, 1, 15, 8, 12, this.mSeparateProcesses, 6, 7, 4);
      this.mParseServiceArgs.tag = "<service>";
    }
    this.mParseServiceArgs.sa = localTypedArray;
    this.mParseServiceArgs.flags = paramInt;
    Service localService = new Service(this.mParseServiceArgs, new ServiceInfo());
    if (paramArrayOfString[0] != null)
    {
      localTypedArray.recycle();
      return null;
    }
    boolean bool2 = localTypedArray.hasValue(5);
    if (bool2) {
      localService.info.exported = localTypedArray.getBoolean(5, false);
    }
    Object localObject = localTypedArray.getNonConfigurationString(3, 0);
    if (localObject == null)
    {
      localService.info.permission = paramPackage.applicationInfo.permission;
      localService.info.flags = 0;
      if (localTypedArray.getBoolean(9, false))
      {
        localObject = localService.info;
        ((ServiceInfo)localObject).flags |= 0x1;
      }
      if (localTypedArray.getBoolean(10, false))
      {
        localObject = localService.info;
        ((ServiceInfo)localObject).flags |= 0x2;
      }
      if (localTypedArray.getBoolean(14, false))
      {
        localObject = localService.info;
        ((ServiceInfo)localObject).flags |= 0x4;
      }
      bool1 = bool2;
      if (localTypedArray.getBoolean(11, false))
      {
        localObject = localService.info;
        ((ServiceInfo)localObject).flags |= 0x40000000;
        bool1 = bool2;
        if (localService.info.exported)
        {
          bool1 = bool2;
          if ((paramInt & 0x80) == 0)
          {
            Slog.w("PackageParser", "Service exported request ignored due to singleUser: " + localService.className + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
            localService.info.exported = false;
            bool1 = true;
          }
        }
      }
      localObject = localService.info;
      bool2 = localTypedArray.getBoolean(13, false);
      localService.info.directBootAware = bool2;
      ((ServiceInfo)localObject).encryptionAware = bool2;
      if (localService.info.directBootAware)
      {
        localObject = paramPackage.applicationInfo;
        ((ApplicationInfo)localObject).privateFlags |= 0x100;
      }
      localTypedArray.recycle();
      if (((paramPackage.applicationInfo.privateFlags & 0x2) != 0) && (localService.info.processName == paramPackage.packageName))
      {
        paramArrayOfString[0] = "Heavy-weight applications can not have services in main process";
        return null;
      }
    }
    else
    {
      ServiceInfo localServiceInfo = localService.info;
      if (((String)localObject).length() > 0) {}
      for (localObject = ((String)localObject).toString().intern();; localObject = null)
      {
        localServiceInfo.permission = ((String)localObject);
        break;
      }
    }
    paramInt = paramXmlResourceParser.getDepth();
    for (;;)
    {
      int i = paramXmlResourceParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlResourceParser.getDepth() <= paramInt))) {
        break;
      }
      if ((i != 3) && (i != 4)) {
        if (paramXmlResourceParser.getName().equals("intent-filter"))
        {
          paramPackage = new ServiceIntentInfo(localService);
          if (!parseIntent(paramResources, paramXmlResourceParser, true, false, paramPackage, paramArrayOfString)) {
            return null;
          }
          localService.intents.add(paramPackage);
        }
        else if (paramXmlResourceParser.getName().equals("meta-data"))
        {
          paramPackage = parseMetaData(paramResources, paramXmlResourceParser, localService.metaData, paramArrayOfString);
          localService.metaData = paramPackage;
          if (paramPackage == null) {
            return null;
          }
        }
        else
        {
          Slog.w("PackageParser", "Unknown element under <service>: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
        }
      }
    }
    if (!bool1)
    {
      paramPackage = localService.info;
      if (localService.intents.size() <= 0) {
        break label794;
      }
    }
    label794:
    for (boolean bool1 = true;; bool1 = false)
    {
      paramPackage.exported = bool1;
      return localService;
    }
  }
  
  private Package parseSplitApk(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser, int paramInt1, int paramInt2, String[] paramArrayOfString)
    throws XmlPullParserException, IOException, PackageParser.PackageParserException
  {
    parsePackageSplitNames(paramXmlResourceParser, paramXmlResourceParser);
    this.mParseInstrumentationArgs = null;
    this.mParseActivityArgs = null;
    this.mParseServiceArgs = null;
    this.mParseProviderArgs = null;
    int i = 0;
    int j = paramXmlResourceParser.getDepth();
    for (;;)
    {
      int k = paramXmlResourceParser.next();
      if ((k == 1) || ((k == 3) && (paramXmlResourceParser.getDepth() <= j))) {
        break;
      }
      if ((k != 3) && (k != 4)) {
        if (paramXmlResourceParser.getName().equals("application"))
        {
          if (i != 0)
          {
            Slog.w("PackageParser", "<manifest> has more than one <application>");
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
          }
          else
          {
            i = 1;
            if (!parseSplitApplication(paramPackage, paramResources, paramXmlResourceParser, paramInt1, paramInt2, paramArrayOfString)) {
              return null;
            }
          }
        }
        else
        {
          Slog.w("PackageParser", "Unknown element under <manifest>: " + paramXmlResourceParser.getName() + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
        }
      }
    }
    if (i == 0)
    {
      paramArrayOfString[0] = "<manifest> does not contain an <application>";
      this.mParseError = -109;
    }
    return paramPackage;
  }
  
  /* Error */
  private void parseSplitApk(Package paramPackage, int paramInt1, AssetManager paramAssetManager, int paramInt2)
    throws PackageParser.PackageParserException
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 607	android/content/pm/PackageParser$Package:splitCodePaths	[Ljava/lang/String;
    //   4: iload_2
    //   5: aaload
    //   6: astore 6
    //   8: aload_0
    //   9: iconst_1
    //   10: putfield 306	android/content/pm/PackageParser:mParseError	I
    //   13: aload_0
    //   14: aload 6
    //   16: putfield 1241	android/content/pm/PackageParser:mArchiveSourcePath	Ljava/lang/String;
    //   19: aload_3
    //   20: aload 6
    //   22: iload 4
    //   24: invokestatic 1496	android/content/pm/PackageParser:loadApkIntoAssetManager	(Landroid/content/res/AssetManager;Ljava/lang/String;I)I
    //   27: istore 5
    //   29: new 1005	android/content/res/Resources
    //   32: dup
    //   33: aload_3
    //   34: aload_0
    //   35: getfield 311	android/content/pm/PackageParser:mMetrics	Landroid/util/DisplayMetrics;
    //   38: aconst_null
    //   39: invokespecial 1384	android/content/res/Resources:<init>	(Landroid/content/res/AssetManager;Landroid/util/DisplayMetrics;Landroid/content/res/Configuration;)V
    //   42: astore 7
    //   44: aload_3
    //   45: iconst_0
    //   46: iconst_0
    //   47: aconst_null
    //   48: iconst_0
    //   49: iconst_0
    //   50: iconst_0
    //   51: iconst_0
    //   52: iconst_0
    //   53: iconst_0
    //   54: iconst_0
    //   55: iconst_0
    //   56: iconst_0
    //   57: iconst_0
    //   58: iconst_0
    //   59: iconst_0
    //   60: iconst_0
    //   61: getstatic 1375	android/os/Build$VERSION:RESOURCES_SDK_INT	I
    //   64: invokevirtual 1379	android/content/res/AssetManager:setConfiguration	(IILjava/lang/String;IIIIIIIIIIIIII)V
    //   67: aload_3
    //   68: iload 5
    //   70: ldc 68
    //   72: invokevirtual 1388	android/content/res/AssetManager:openXmlResourceParser	(ILjava/lang/String;)Landroid/content/res/XmlResourceParser;
    //   75: astore_3
    //   76: iconst_1
    //   77: anewarray 264	java/lang/String
    //   80: astore 8
    //   82: aload_0
    //   83: aload_1
    //   84: aload 7
    //   86: aload_3
    //   87: iload 4
    //   89: iload_2
    //   90: aload 8
    //   92: invokespecial 2384	android/content/pm/PackageParser:parseSplitApk	(Landroid/content/pm/PackageParser$Package;Landroid/content/res/Resources;Landroid/content/res/XmlResourceParser;II[Ljava/lang/String;)Landroid/content/pm/PackageParser$Package;
    //   95: ifnonnull +68 -> 163
    //   98: new 33	android/content/pm/PackageParser$PackageParserException
    //   101: dup
    //   102: aload_0
    //   103: getfield 306	android/content/pm/PackageParser:mParseError	I
    //   106: new 328	java/lang/StringBuilder
    //   109: dup
    //   110: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   113: aload 6
    //   115: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   118: ldc_w 1500
    //   121: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: aload_3
    //   125: invokeinterface 1248 1 0
    //   130: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   133: ldc_w 1502
    //   136: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: aload 8
    //   141: iconst_0
    //   142: aaload
    //   143: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   149: invokespecial 526	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;)V
    //   152: athrow
    //   153: astore_1
    //   154: aload_1
    //   155: athrow
    //   156: astore_1
    //   157: aload_3
    //   158: invokestatic 1050	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   161: aload_1
    //   162: athrow
    //   163: aload_3
    //   164: invokestatic 1050	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   167: return
    //   168: astore_1
    //   169: aconst_null
    //   170: astore_3
    //   171: new 33	android/content/pm/PackageParser$PackageParserException
    //   174: dup
    //   175: bipush -102
    //   177: new 328	java/lang/StringBuilder
    //   180: dup
    //   181: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   184: ldc_w 1517
    //   187: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   190: aload 6
    //   192: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   195: invokevirtual 339	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   198: aload_1
    //   199: invokespecial 507	android/content/pm/PackageParser$PackageParserException:<init>	(ILjava/lang/String;Ljava/lang/Throwable;)V
    //   202: athrow
    //   203: astore_1
    //   204: aconst_null
    //   205: astore_3
    //   206: goto -49 -> 157
    //   209: astore_1
    //   210: aconst_null
    //   211: astore_3
    //   212: goto -55 -> 157
    //   215: astore_1
    //   216: goto -59 -> 157
    //   219: astore_1
    //   220: aconst_null
    //   221: astore_3
    //   222: goto -68 -> 154
    //   225: astore_1
    //   226: aconst_null
    //   227: astore_3
    //   228: goto -74 -> 154
    //   231: astore_1
    //   232: aconst_null
    //   233: astore_3
    //   234: goto -63 -> 171
    //   237: astore_1
    //   238: goto -67 -> 171
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	241	0	this	PackageParser
    //   0	241	1	paramPackage	Package
    //   0	241	2	paramInt1	int
    //   0	241	3	paramAssetManager	AssetManager
    //   0	241	4	paramInt2	int
    //   27	42	5	i	int
    //   6	185	6	str	String
    //   42	43	7	localResources	Resources
    //   80	60	8	arrayOfString	String[]
    // Exception table:
    //   from	to	target	type
    //   76	153	153	android/content/pm/PackageParser$PackageParserException
    //   154	156	156	finally
    //   171	203	156	finally
    //   29	44	168	java/lang/Exception
    //   29	44	203	finally
    //   44	76	209	finally
    //   76	153	215	finally
    //   29	44	219	android/content/pm/PackageParser$PackageParserException
    //   44	76	225	android/content/pm/PackageParser$PackageParserException
    //   44	76	231	java/lang/Exception
    //   76	153	237	java/lang/Exception
  }
  
  private boolean parseSplitApplication(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser, int paramInt1, int paramInt2, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    Object localObject;
    if (paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestApplication).getBoolean(7, true))
    {
      localObject = paramPackage.splitFlags;
      localObject[paramInt2] |= 0x4;
    }
    paramInt2 = paramXmlResourceParser.getDepth();
    for (;;)
    {
      int i = paramXmlResourceParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlResourceParser.getDepth() <= paramInt2))) {
        break;
      }
      if ((i != 3) && (i != 4))
      {
        localObject = paramXmlResourceParser.getName();
        if (((String)localObject).equals("activity"))
        {
          localObject = parseActivity(paramPackage, paramResources, paramXmlResourceParser, paramInt1, paramArrayOfString, false, paramPackage.baseHardwareAccelerated);
          if (localObject == null)
          {
            this.mParseError = -108;
            return false;
          }
          paramPackage.activities.add(localObject);
        }
        else if (((String)localObject).equals("receiver"))
        {
          localObject = parseActivity(paramPackage, paramResources, paramXmlResourceParser, paramInt1, paramArrayOfString, true, false);
          if (localObject == null)
          {
            this.mParseError = -108;
            return false;
          }
          paramPackage.receivers.add(localObject);
        }
        else if (((String)localObject).equals("service"))
        {
          localObject = parseService(paramPackage, paramResources, paramXmlResourceParser, paramInt1, paramArrayOfString);
          if (localObject == null)
          {
            this.mParseError = -108;
            return false;
          }
          paramPackage.services.add(localObject);
        }
        else if (((String)localObject).equals("provider"))
        {
          localObject = parseProvider(paramPackage, paramResources, paramXmlResourceParser, paramInt1, paramArrayOfString);
          if (localObject == null)
          {
            this.mParseError = -108;
            return false;
          }
          paramPackage.providers.add(localObject);
        }
        else if (((String)localObject).equals("activity-alias"))
        {
          localObject = parseActivityAlias(paramPackage, paramResources, paramXmlResourceParser, paramInt1, paramArrayOfString);
          if (localObject == null)
          {
            this.mParseError = -108;
            return false;
          }
          paramPackage.activities.add(localObject);
        }
        else if (paramXmlResourceParser.getName().equals("meta-data"))
        {
          localObject = parseMetaData(paramResources, paramXmlResourceParser, paramPackage.mAppMetaData, paramArrayOfString);
          paramPackage.mAppMetaData = ((Bundle)localObject);
          if (localObject == null)
          {
            this.mParseError = -108;
            return false;
          }
        }
        else
        {
          if (((String)localObject).equals("uses-library"))
          {
            localObject = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestUsesLibrary);
            String str = ((TypedArray)localObject).getNonResourceString(0);
            boolean bool = ((TypedArray)localObject).getBoolean(1, true);
            ((TypedArray)localObject).recycle();
            if (str != null)
            {
              localObject = str.intern();
              if (!bool) {
                break label492;
              }
              paramPackage.usesLibraries = ArrayUtils.add(paramPackage.usesLibraries, localObject);
              paramPackage.usesOptionalLibraries = ArrayUtils.remove(paramPackage.usesOptionalLibraries, localObject);
            }
            for (;;)
            {
              XmlUtils.skipCurrentTag(paramXmlResourceParser);
              break;
              label492:
              if (!ArrayUtils.contains(paramPackage.usesLibraries, localObject)) {
                paramPackage.usesOptionalLibraries = ArrayUtils.add(paramPackage.usesOptionalLibraries, localObject);
              }
            }
          }
          if (((String)localObject).equals("uses-package"))
          {
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
          }
          else
          {
            Slog.w("PackageParser", "Unknown element under <application>: " + (String)localObject + " at " + this.mArchiveSourcePath + " " + paramXmlResourceParser.getPositionDescription());
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
          }
        }
      }
    }
    return true;
  }
  
  private FeatureInfo parseUsesFeature(Resources paramResources, AttributeSet paramAttributeSet)
  {
    FeatureInfo localFeatureInfo = new FeatureInfo();
    paramResources = paramResources.obtainAttributes(paramAttributeSet, R.styleable.AndroidManifestUsesFeature);
    localFeatureInfo.name = paramResources.getNonResourceString(0);
    localFeatureInfo.version = paramResources.getInt(3, 0);
    if (localFeatureInfo.name == null) {
      localFeatureInfo.reqGlEsVersion = paramResources.getInt(1, 0);
    }
    if (paramResources.getBoolean(2, true)) {
      localFeatureInfo.flags |= 0x1;
    }
    paramResources.recycle();
    return localFeatureInfo;
  }
  
  private boolean parseUsesPermission(Package paramPackage, Resources paramResources, XmlResourceParser paramXmlResourceParser)
    throws XmlPullParserException, IOException
  {
    paramResources = paramResources.obtainAttributes(paramXmlResourceParser, R.styleable.AndroidManifestUsesPermission);
    String str = paramResources.getNonResourceString(0);
    int j = 0;
    TypedValue localTypedValue = paramResources.peekValue(1);
    int i = j;
    if (localTypedValue != null)
    {
      i = j;
      if (localTypedValue.type >= 16)
      {
        i = j;
        if (localTypedValue.type <= 31) {
          i = localTypedValue.data;
        }
      }
    }
    paramResources.recycle();
    if (((i == 0) || (i >= Build.VERSION.RESOURCES_SDK_INT)) && (str != null))
    {
      if (paramPackage.requestedPermissions.indexOf(str) != -1) {
        break label124;
      }
      paramPackage.requestedPermissions.add(str.intern());
    }
    for (;;)
    {
      XmlUtils.skipCurrentTag(paramXmlResourceParser);
      return true;
      label124:
      Slog.w("PackageParser", "Ignoring duplicate uses-permissions/uses-permissions-sdk-m: " + str + " in package: " + paramPackage.packageName + " at: " + paramXmlResourceParser.getPositionDescription());
    }
  }
  
  private static VerifierInfo parseVerifier(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, int paramInt)
  {
    paramXmlPullParser = paramResources.obtainAttributes(paramAttributeSet, R.styleable.AndroidManifestPackageVerifier);
    paramResources = paramXmlPullParser.getNonResourceString(0);
    paramAttributeSet = paramXmlPullParser.getNonResourceString(1);
    paramXmlPullParser.recycle();
    if ((paramResources == null) || (paramResources.length() == 0))
    {
      Slog.i("PackageParser", "verifier package name was null; skipping");
      return null;
    }
    paramXmlPullParser = parsePublicKey(paramAttributeSet);
    if (paramXmlPullParser == null)
    {
      Slog.i("PackageParser", "Unable to parse verifier public key for " + paramResources);
      return null;
    }
    return new VerifierInfo(paramResources, paramXmlPullParser);
  }
  
  public static void populateCertificates(Package paramPackage, Certificate[][] paramArrayOfCertificate)
    throws PackageParser.PackageParserException
  {
    paramPackage.mCertificates = null;
    paramPackage.mSignatures = null;
    paramPackage.mSigningKeys = null;
    paramPackage.mCertificates = paramArrayOfCertificate;
    try
    {
      paramPackage.mSignatures = convertToSignatures(paramArrayOfCertificate);
      paramPackage.mSigningKeys = new ArraySet(paramArrayOfCertificate.length);
      i = 0;
      while (i < paramArrayOfCertificate.length)
      {
        Certificate localCertificate = paramArrayOfCertificate[i][0];
        paramPackage.mSigningKeys.add(localCertificate.getPublicKey());
        i += 1;
      }
      if (paramPackage.childPackages == null) {}
    }
    catch (CertificateEncodingException paramArrayOfCertificate)
    {
      throw new PackageParserException(-103, "Failed to collect certificates from " + paramPackage.baseCodePath, paramArrayOfCertificate);
    }
    for (int i = paramPackage.childPackages.size();; i = 0)
    {
      int j = 0;
      while (j < i)
      {
        paramArrayOfCertificate = (Package)paramPackage.childPackages.get(j);
        paramArrayOfCertificate.mCertificates = paramPackage.mCertificates;
        paramArrayOfCertificate.mSignatures = paramPackage.mSignatures;
        paramArrayOfCertificate.mSigningKeys = paramPackage.mSigningKeys;
        j += 1;
      }
    }
  }
  
  public static long readFullyIgnoringContents(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte2 = (byte[])sBuffer.getAndSet(null);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (arrayOfByte2 == null) {
      arrayOfByte1 = new byte['က'];
    }
    int i = 0;
    for (;;)
    {
      int j = paramInputStream.read(arrayOfByte1, 0, arrayOfByte1.length);
      if (j == -1) {
        break;
      }
      i += j;
    }
    sBuffer.set(arrayOfByte1);
    return i;
  }
  
  public static void setCompatibilityModeEnabled(boolean paramBoolean)
  {
    sCompatibilityModeEnabled = paramBoolean;
  }
  
  public static Signature stringToSignature(String paramString)
  {
    int j = paramString.length();
    byte[] arrayOfByte = new byte[j];
    int i = 0;
    while (i < j)
    {
      arrayOfByte[i] = ((byte)paramString.charAt(i));
      i += 1;
    }
    return new Signature(arrayOfByte);
  }
  
  private static void updateApplicationInfo(ApplicationInfo paramApplicationInfo, int paramInt, PackageUserState paramPackageUserState)
  {
    boolean bool = true;
    if (!sCompatibilityModeEnabled) {
      paramApplicationInfo.disableCompatibilityMode();
    }
    if (paramPackageUserState.installed)
    {
      paramApplicationInfo.flags |= 0x800000;
      if (!paramPackageUserState.suspended) {
        break label104;
      }
      paramApplicationInfo.flags |= 0x40000000;
      label50:
      if (!paramPackageUserState.hidden) {
        break label119;
      }
      paramApplicationInfo.privateFlags |= 0x1;
      label67:
      if (paramPackageUserState.enabled != 1) {
        break label133;
      }
      paramApplicationInfo.enabled = true;
    }
    for (;;)
    {
      paramApplicationInfo.enabledSetting = paramPackageUserState.enabled;
      return;
      paramApplicationInfo.flags &= 0xFF7FFFFF;
      break;
      label104:
      paramApplicationInfo.flags &= 0xBFFFFFFF;
      break label50;
      label119:
      paramApplicationInfo.privateFlags &= 0xFFFFFFFE;
      break label67;
      label133:
      if (paramPackageUserState.enabled == 4)
      {
        if ((0x8000 & paramInt) != 0) {}
        for (;;)
        {
          paramApplicationInfo.enabled = bool;
          break;
          bool = false;
        }
      }
      if ((paramPackageUserState.enabled == 2) || (paramPackageUserState.enabled == 3)) {
        paramApplicationInfo.enabled = false;
      }
    }
  }
  
  private static String validateName(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i1 = paramString.length();
    int k = 0;
    int n = 1;
    int j = 0;
    if (j < i1)
    {
      char c = paramString.charAt(j);
      label41:
      int i;
      int m;
      if ((c >= 'a') && (c <= 'z'))
      {
        i = 0;
        m = k;
      }
      for (;;)
      {
        j += 1;
        n = i;
        k = m;
        break;
        if ((c >= 'A') && (c <= 'Z')) {
          break label41;
        }
        if (n == 0)
        {
          if (c >= '0')
          {
            i = n;
            m = k;
            if (c <= '9') {}
          }
          else
          {
            i = n;
            m = k;
            if (c == '_') {}
          }
        }
        else
        {
          if (c != '.') {
            break label131;
          }
          m = 1;
          i = 1;
        }
      }
      label131:
      return "bad character '" + c + "'";
    }
    if ((!paramBoolean2) || (FileUtils.isValidExtFilename(paramString)))
    {
      if ((k == 0) && (paramBoolean1)) {
        return "must have at least one '.' separator";
      }
    }
    else {
      return "Invalid filename";
    }
    return null;
  }
  
  @Deprecated
  public Package parseMonolithicPackage(File paramFile, int paramInt)
    throws PackageParser.PackageParserException
  {
    PackageLite localPackageLite = parseMonolithicPackageLite(paramFile, paramInt);
    if ((this.mOnlyPowerOffAlarmApps) && (!isPowerOffAlarmPackage(localPackageLite.packageName))) {
      throw new PackageParserException(-108, "Not a powerOffAlarmApp: " + paramFile);
    }
    if ((!this.mOnlyPowerOffAlarmApps) && (this.mOnlyCoreApps) && (!localPackageLite.coreApp)) {
      throw new PackageParserException(-108, "Not a coreApp: " + paramFile);
    }
    AssetManager localAssetManager = new AssetManager();
    try
    {
      Package localPackage = parseBaseApk(paramFile, localAssetManager, paramInt);
      localPackage.setCodePath(paramFile.getAbsolutePath());
      localPackage.setUse32bitAbi(localPackageLite.use32bitAbi);
      return localPackage;
    }
    finally
    {
      IoUtils.closeQuietly(localAssetManager);
    }
  }
  
  public Package parsePackage(File paramFile, int paramInt)
    throws PackageParser.PackageParserException
  {
    if (paramFile.isDirectory()) {
      return parseClusterPackage(paramFile, paramInt);
    }
    return parseMonolithicPackage(paramFile, paramInt);
  }
  
  public void setDisplayMetrics(DisplayMetrics paramDisplayMetrics)
  {
    this.mMetrics = paramDisplayMetrics;
  }
  
  public void setOnlyCoreApps(boolean paramBoolean)
  {
    this.mOnlyCoreApps = paramBoolean;
  }
  
  public void setOnlyPowerOffAlarmApps(boolean paramBoolean)
  {
    this.mOnlyPowerOffAlarmApps = paramBoolean;
  }
  
  public void setSeparateProcesses(String[] paramArrayOfString)
  {
    this.mSeparateProcesses = paramArrayOfString;
  }
  
  public static final class Activity
    extends PackageParser.Component<PackageParser.ActivityIntentInfo>
  {
    public final ActivityInfo info;
    
    public Activity(PackageParser.ParseComponentArgs paramParseComponentArgs, ActivityInfo paramActivityInfo)
    {
      super(paramActivityInfo);
      this.info = paramActivityInfo;
      this.info.applicationInfo = paramParseComponentArgs.owner.applicationInfo;
    }
    
    public void setPackageName(String paramString)
    {
      super.setPackageName(paramString);
      this.info.packageName = paramString;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append("Activity{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(' ');
      appendComponentShortName(localStringBuilder);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
  
  public static final class ActivityIntentInfo
    extends PackageParser.IntentInfo
  {
    public final PackageParser.Activity activity;
    
    public ActivityIntentInfo(PackageParser.Activity paramActivity)
    {
      this.activity = paramActivity;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append("ActivityIntentInfo{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(' ');
      this.activity.appendComponentShortName(localStringBuilder);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
  
  public static class ApkLite
  {
    public final Certificate[][] certificates;
    public final String codePath;
    public final boolean coreApp;
    public final boolean extractNativeLibs;
    public final int installLocation;
    public final boolean multiArch;
    public final String oplibDependencyStr;
    public final String packageName;
    public final int revisionCode;
    public final Signature[] signatures;
    public final String splitName;
    public final boolean use32bitAbi;
    public final VerifierInfo[] verifiers;
    public final int versionCode;
    
    public ApkLite(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, List<VerifierInfo> paramList, Signature[] paramArrayOfSignature, Certificate[][] paramArrayOfCertificate, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, String paramString4)
    {
      this.codePath = paramString1;
      this.packageName = paramString2;
      this.splitName = paramString3;
      this.versionCode = paramInt1;
      this.revisionCode = paramInt2;
      this.installLocation = paramInt3;
      this.verifiers = ((VerifierInfo[])paramList.toArray(new VerifierInfo[paramList.size()]));
      this.signatures = paramArrayOfSignature;
      this.certificates = paramArrayOfCertificate;
      this.coreApp = paramBoolean1;
      this.multiArch = paramBoolean2;
      this.use32bitAbi = paramBoolean3;
      this.extractNativeLibs = paramBoolean4;
      this.oplibDependencyStr = paramString4;
    }
  }
  
  public static class Component<II extends PackageParser.IntentInfo>
  {
    public final String className;
    ComponentName componentName;
    String componentShortName;
    public final ArrayList<II> intents;
    public Bundle metaData;
    public final PackageParser.Package owner;
    
    public Component(Component<II> paramComponent)
    {
      this.owner = paramComponent.owner;
      this.intents = paramComponent.intents;
      this.className = paramComponent.className;
      this.componentName = paramComponent.componentName;
      this.componentShortName = paramComponent.componentShortName;
    }
    
    public Component(PackageParser.Package paramPackage)
    {
      this.owner = paramPackage;
      this.intents = null;
      this.className = null;
    }
    
    public Component(PackageParser.ParseComponentArgs paramParseComponentArgs, ComponentInfo paramComponentInfo)
    {
      this(paramParseComponentArgs, paramComponentInfo);
      if (paramParseComponentArgs.outError[0] != null) {
        return;
      }
      if (paramParseComponentArgs.processRes != 0) {
        if (this.owner.applicationInfo.targetSdkVersion < 8) {
          break label133;
        }
      }
      label133:
      for (String str = paramParseComponentArgs.sa.getNonConfigurationString(paramParseComponentArgs.processRes, 1024);; str = paramParseComponentArgs.sa.getNonResourceString(paramParseComponentArgs.processRes))
      {
        paramComponentInfo.processName = PackageParser.-wrap1(this.owner.applicationInfo.packageName, this.owner.applicationInfo.processName, str, paramParseComponentArgs.flags, paramParseComponentArgs.sepProcesses, paramParseComponentArgs.outError);
        if (paramParseComponentArgs.descriptionRes != 0) {
          paramComponentInfo.descriptionRes = paramParseComponentArgs.sa.getResourceId(paramParseComponentArgs.descriptionRes, 0);
        }
        paramComponentInfo.enabled = paramParseComponentArgs.sa.getBoolean(paramParseComponentArgs.enabledRes, true);
        return;
      }
    }
    
    public Component(PackageParser.ParsePackageItemArgs paramParsePackageItemArgs, PackageItemInfo paramPackageItemInfo)
    {
      this.owner = paramParsePackageItemArgs.owner;
      this.intents = new ArrayList(0);
      if (PackageParser.-wrap0(paramParsePackageItemArgs.owner, paramPackageItemInfo, paramParsePackageItemArgs.outError, paramParsePackageItemArgs.tag, paramParsePackageItemArgs.sa, true, paramParsePackageItemArgs.nameRes, paramParsePackageItemArgs.labelRes, paramParsePackageItemArgs.iconRes, paramParsePackageItemArgs.roundIconRes, paramParsePackageItemArgs.logoRes, paramParsePackageItemArgs.bannerRes))
      {
        this.className = paramPackageItemInfo.name;
        return;
      }
      this.className = null;
    }
    
    public void appendComponentShortName(StringBuilder paramStringBuilder)
    {
      ComponentName.appendShortString(paramStringBuilder, this.owner.applicationInfo.packageName, this.className);
    }
    
    public ComponentName getComponentName()
    {
      if (this.componentName != null) {
        return this.componentName;
      }
      if (this.className != null) {
        this.componentName = new ComponentName(this.owner.applicationInfo.packageName, this.className);
      }
      return this.componentName;
    }
    
    public void printComponentShortName(PrintWriter paramPrintWriter)
    {
      ComponentName.printShortString(paramPrintWriter, this.owner.applicationInfo.packageName, this.className);
    }
    
    public void setPackageName(String paramString)
    {
      this.componentName = null;
      this.componentShortName = null;
    }
  }
  
  public static final class Instrumentation
    extends PackageParser.Component<PackageParser.IntentInfo>
  {
    public final InstrumentationInfo info;
    
    public Instrumentation(PackageParser.ParsePackageItemArgs paramParsePackageItemArgs, InstrumentationInfo paramInstrumentationInfo)
    {
      super(paramInstrumentationInfo);
      this.info = paramInstrumentationInfo;
    }
    
    public void setPackageName(String paramString)
    {
      super.setPackageName(paramString);
      this.info.packageName = paramString;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append("Instrumentation{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(' ');
      appendComponentShortName(localStringBuilder);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
  
  public static class IntentInfo
    extends IntentFilter
  {
    public int banner;
    public boolean hasDefault;
    public int icon;
    public int labelRes;
    public int logo;
    public CharSequence nonLocalizedLabel;
    public int preferred;
  }
  
  public static class NewPermissionInfo
  {
    public final int fileVersion;
    public final String name;
    public final int sdkVersion;
    
    public NewPermissionInfo(String paramString, int paramInt1, int paramInt2)
    {
      this.name = paramString;
      this.sdkVersion = paramInt1;
      this.fileVersion = paramInt2;
    }
  }
  
  public static final class Package
  {
    public final ArrayList<PackageParser.Activity> activities = new ArrayList(0);
    public final ApplicationInfo applicationInfo = new ApplicationInfo();
    public String baseCodePath;
    public boolean baseHardwareAccelerated;
    public int baseRevisionCode;
    public ArrayList<Package> childPackages;
    public String codePath;
    public ArrayList<ConfigurationInfo> configPreferences = null;
    public boolean coreApp;
    public String cpuAbiOverride;
    public ArrayList<FeatureGroupInfo> featureGroups = null;
    public int installLocation;
    public final ArrayList<PackageParser.Instrumentation> instrumentation = new ArrayList(0);
    public ArrayList<String> libraryNames = null;
    public ArrayList<String> mAdoptPermissions = null;
    public Bundle mAppMetaData = null;
    public Certificate[][] mCertificates;
    public Object mExtras;
    public ArrayMap<String, ArraySet<PublicKey>> mKeySetMapping;
    public long[] mLastPackageUsageTimeInMills = new long[8];
    public ArrayList<String> mOriginalPackages = null;
    public int mOverlayPriority;
    public String mOverlayTarget;
    public int mPreferredOrder = 0;
    public String mRealPackage = null;
    public String mRequiredAccountType;
    public boolean mRequiredForAllUsers;
    public String mRestrictedAccountType;
    public String mSharedUserId;
    public int mSharedUserLabel;
    public Signature[] mSignatures;
    public ArraySet<PublicKey> mSigningKeys;
    public boolean mTrustedOverlay;
    public ArraySet<String> mUpgradeKeySets;
    public int mVersionCode;
    public String mVersionName;
    public String packageName;
    public Package parentPackage;
    public final ArrayList<PackageParser.PermissionGroup> permissionGroups = new ArrayList(0);
    public final ArrayList<PackageParser.Permission> permissions = new ArrayList(0);
    public ArrayList<PackageParser.ActivityIntentInfo> preferredActivityFilters = null;
    public ArrayList<String> protectedBroadcasts;
    public final ArrayList<PackageParser.Provider> providers = new ArrayList(0);
    public final ArrayList<PackageParser.Activity> receivers = new ArrayList(0);
    public ArrayList<FeatureInfo> reqFeatures = null;
    public final ArrayList<String> requestedPermissions = new ArrayList();
    public byte[] restrictUpdateHash;
    public final ArrayList<PackageParser.Service> services = new ArrayList(0);
    public String[] splitCodePaths;
    public int[] splitFlags;
    public String[] splitNames;
    public int[] splitPrivateFlags;
    public int[] splitRevisionCodes;
    public boolean use32bitAbi;
    public ArrayList<String> usesLibraries = null;
    public String[] usesLibraryFiles = null;
    public ArrayList<String> usesOptionalLibraries = null;
    public String volumeUuid;
    
    public Package(String paramString)
    {
      this.packageName = paramString;
      this.applicationInfo.packageName = paramString;
      this.applicationInfo.uid = -1;
    }
    
    public boolean canHaveOatDir()
    {
      if (((isSystemApp()) && (!isUpdatedSystemApp())) || (isForwardLocked())) {}
      while (this.applicationInfo.isExternalAsec()) {
        return false;
      }
      return true;
    }
    
    public List<String> getAllCodePaths()
    {
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(this.baseCodePath);
      if (!ArrayUtils.isEmpty(this.splitCodePaths)) {
        Collections.addAll(localArrayList, this.splitCodePaths);
      }
      return localArrayList;
    }
    
    public List<String> getAllCodePathsExcludingResourceOnly()
    {
      ArrayList localArrayList = new ArrayList();
      if ((this.applicationInfo.flags & 0x4) != 0) {
        localArrayList.add(this.baseCodePath);
      }
      if (!ArrayUtils.isEmpty(this.splitCodePaths))
      {
        int i = 0;
        while (i < this.splitCodePaths.length)
        {
          if ((this.splitFlags[i] & 0x4) != 0) {
            localArrayList.add(this.splitCodePaths[i]);
          }
          i += 1;
        }
      }
      return localArrayList;
    }
    
    public long getLatestForegroundPackageUseTimeInMills()
    {
      int i = 0;
      int[] arrayOfInt = new int[2];
      arrayOfInt[0] = 0;
      arrayOfInt[1] = 2;
      long l = 0L;
      int j = arrayOfInt.length;
      while (i < j)
      {
        int k = arrayOfInt[i];
        l = Math.max(l, this.mLastPackageUsageTimeInMills[k]);
        i += 1;
      }
      return l;
    }
    
    public long getLatestPackageUseTimeInMills()
    {
      long l = 0L;
      long[] arrayOfLong = this.mLastPackageUsageTimeInMills;
      int i = 0;
      int j = arrayOfLong.length;
      while (i < j)
      {
        l = Math.max(l, arrayOfLong[i]);
        i += 1;
      }
      return l;
    }
    
    public boolean hasChildPackage(String paramString)
    {
      int i;
      int j;
      if (this.childPackages != null)
      {
        i = this.childPackages.size();
        j = 0;
      }
      for (;;)
      {
        if (j >= i) {
          break label57;
        }
        if (((Package)this.childPackages.get(j)).packageName.equals(paramString))
        {
          return true;
          i = 0;
          break;
        }
        j += 1;
      }
      label57:
      return false;
    }
    
    public boolean hasComponentClassName(String paramString)
    {
      int i = this.activities.size() - 1;
      while (i >= 0)
      {
        if (paramString.equals(((PackageParser.Activity)this.activities.get(i)).className)) {
          return true;
        }
        i -= 1;
      }
      i = this.receivers.size() - 1;
      while (i >= 0)
      {
        if (paramString.equals(((PackageParser.Activity)this.receivers.get(i)).className)) {
          return true;
        }
        i -= 1;
      }
      i = this.providers.size() - 1;
      while (i >= 0)
      {
        if (paramString.equals(((PackageParser.Provider)this.providers.get(i)).className)) {
          return true;
        }
        i -= 1;
      }
      i = this.services.size() - 1;
      while (i >= 0)
      {
        if (paramString.equals(((PackageParser.Service)this.services.get(i)).className)) {
          return true;
        }
        i -= 1;
      }
      i = this.instrumentation.size() - 1;
      while (i >= 0)
      {
        if (paramString.equals(((PackageParser.Instrumentation)this.instrumentation.get(i)).className)) {
          return true;
        }
        i -= 1;
      }
      return false;
    }
    
    public boolean isForwardLocked()
    {
      return this.applicationInfo.isForwardLocked();
    }
    
    public boolean isMatch(int paramInt)
    {
      if ((0x100000 & paramInt) != 0) {
        return isSystemApp();
      }
      return true;
    }
    
    public boolean isPrivilegedApp()
    {
      return this.applicationInfo.isPrivilegedApp();
    }
    
    public boolean isSystemApp()
    {
      return this.applicationInfo.isSystemApp();
    }
    
    public boolean isUpdatedSystemApp()
    {
      return this.applicationInfo.isUpdatedSystemApp();
    }
    
    public void setApplicationInfoBaseCodePath(String paramString)
    {
      this.applicationInfo.setBaseCodePath(paramString);
      if (this.childPackages != null)
      {
        int j = this.childPackages.size();
        int i = 0;
        while (i < j)
        {
          ((Package)this.childPackages.get(i)).applicationInfo.setBaseCodePath(paramString);
          i += 1;
        }
      }
    }
    
    public void setApplicationInfoBaseResourcePath(String paramString)
    {
      this.applicationInfo.setBaseResourcePath(paramString);
      if (this.childPackages != null)
      {
        int j = this.childPackages.size();
        int i = 0;
        while (i < j)
        {
          ((Package)this.childPackages.get(i)).applicationInfo.setBaseResourcePath(paramString);
          i += 1;
        }
      }
    }
    
    public void setApplicationInfoCodePath(String paramString)
    {
      this.applicationInfo.setCodePath(paramString);
      if (this.childPackages != null)
      {
        int j = this.childPackages.size();
        int i = 0;
        while (i < j)
        {
          ((Package)this.childPackages.get(i)).applicationInfo.setCodePath(paramString);
          i += 1;
        }
      }
    }
    
    public void setApplicationInfoFlags(int paramInt1, int paramInt2)
    {
      this.applicationInfo.flags = (this.applicationInfo.flags & paramInt1 | paramInt1 & paramInt2);
      if (this.childPackages != null)
      {
        int j = this.childPackages.size();
        int i = 0;
        while (i < j)
        {
          ((Package)this.childPackages.get(i)).applicationInfo.flags = (this.applicationInfo.flags & paramInt1 | paramInt1 & paramInt2);
          i += 1;
        }
      }
    }
    
    public void setApplicationInfoResourcePath(String paramString)
    {
      this.applicationInfo.setResourcePath(paramString);
      if (this.childPackages != null)
      {
        int j = this.childPackages.size();
        int i = 0;
        while (i < j)
        {
          ((Package)this.childPackages.get(i)).applicationInfo.setResourcePath(paramString);
          i += 1;
        }
      }
    }
    
    public void setApplicationInfoSplitCodePaths(String[] paramArrayOfString)
    {
      this.applicationInfo.setSplitCodePaths(paramArrayOfString);
    }
    
    public void setApplicationInfoSplitResourcePaths(String[] paramArrayOfString)
    {
      this.applicationInfo.setSplitResourcePaths(paramArrayOfString);
    }
    
    public void setApplicationVolumeUuid(String paramString)
    {
      this.applicationInfo.volumeUuid = paramString;
      if (this.childPackages != null)
      {
        int j = this.childPackages.size();
        int i = 0;
        while (i < j)
        {
          ((Package)this.childPackages.get(i)).applicationInfo.volumeUuid = paramString;
          i += 1;
        }
      }
    }
    
    public void setBaseCodePath(String paramString)
    {
      this.baseCodePath = paramString;
      if (this.childPackages != null)
      {
        int j = this.childPackages.size();
        int i = 0;
        while (i < j)
        {
          ((Package)this.childPackages.get(i)).baseCodePath = paramString;
          i += 1;
        }
      }
    }
    
    public void setCodePath(String paramString)
    {
      this.codePath = paramString;
      if (this.childPackages != null)
      {
        int j = this.childPackages.size();
        int i = 0;
        while (i < j)
        {
          ((Package)this.childPackages.get(i)).codePath = paramString;
          i += 1;
        }
      }
    }
    
    public void setPackageName(String paramString)
    {
      this.packageName = paramString;
      this.applicationInfo.packageName = paramString;
      int i = this.permissions.size() - 1;
      while (i >= 0)
      {
        ((PackageParser.Permission)this.permissions.get(i)).setPackageName(paramString);
        i -= 1;
      }
      i = this.permissionGroups.size() - 1;
      while (i >= 0)
      {
        ((PackageParser.PermissionGroup)this.permissionGroups.get(i)).setPackageName(paramString);
        i -= 1;
      }
      i = this.activities.size() - 1;
      while (i >= 0)
      {
        ((PackageParser.Activity)this.activities.get(i)).setPackageName(paramString);
        i -= 1;
      }
      i = this.receivers.size() - 1;
      while (i >= 0)
      {
        ((PackageParser.Activity)this.receivers.get(i)).setPackageName(paramString);
        i -= 1;
      }
      i = this.providers.size() - 1;
      while (i >= 0)
      {
        ((PackageParser.Provider)this.providers.get(i)).setPackageName(paramString);
        i -= 1;
      }
      i = this.services.size() - 1;
      while (i >= 0)
      {
        ((PackageParser.Service)this.services.get(i)).setPackageName(paramString);
        i -= 1;
      }
      i = this.instrumentation.size() - 1;
      while (i >= 0)
      {
        ((PackageParser.Instrumentation)this.instrumentation.get(i)).setPackageName(paramString);
        i -= 1;
      }
    }
    
    public void setSignatures(Signature[] paramArrayOfSignature)
    {
      this.mSignatures = paramArrayOfSignature;
      if (this.childPackages != null)
      {
        int j = this.childPackages.size();
        int i = 0;
        while (i < j)
        {
          ((Package)this.childPackages.get(i)).mSignatures = paramArrayOfSignature;
          i += 1;
        }
      }
    }
    
    public void setSplitCodePaths(String[] paramArrayOfString)
    {
      this.splitCodePaths = paramArrayOfString;
    }
    
    public void setUse32bitAbi(boolean paramBoolean)
    {
      this.use32bitAbi = paramBoolean;
      if (this.childPackages != null)
      {
        int j = this.childPackages.size();
        int i = 0;
        while (i < j)
        {
          ((Package)this.childPackages.get(i)).use32bitAbi = paramBoolean;
          i += 1;
        }
      }
    }
    
    public void setVolumeUuid(String paramString)
    {
      this.volumeUuid = paramString;
      if (this.childPackages != null)
      {
        int j = this.childPackages.size();
        int i = 0;
        while (i < j)
        {
          ((Package)this.childPackages.get(i)).volumeUuid = paramString;
          i += 1;
        }
      }
    }
    
    public String toString()
    {
      return "Package{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.packageName + "}";
    }
  }
  
  public static class PackageLite
  {
    public final String baseCodePath;
    public final int baseRevisionCode;
    public final String codePath;
    public final boolean coreApp;
    public final boolean extractNativeLibs;
    public final int installLocation;
    public final boolean multiArch;
    public final String oplibDependencyStr;
    public final String packageName;
    public final String[] splitCodePaths;
    public final String[] splitNames;
    public final int[] splitRevisionCodes;
    public final boolean use32bitAbi;
    public final VerifierInfo[] verifiers;
    public final int versionCode;
    
    public PackageLite(String paramString, PackageParser.ApkLite paramApkLite, String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt)
    {
      this.packageName = paramApkLite.packageName;
      this.versionCode = paramApkLite.versionCode;
      this.installLocation = paramApkLite.installLocation;
      this.verifiers = paramApkLite.verifiers;
      this.splitNames = paramArrayOfString1;
      this.codePath = paramString;
      this.baseCodePath = paramApkLite.codePath;
      this.splitCodePaths = paramArrayOfString2;
      this.baseRevisionCode = paramApkLite.revisionCode;
      this.splitRevisionCodes = paramArrayOfInt;
      this.coreApp = paramApkLite.coreApp;
      this.multiArch = paramApkLite.multiArch;
      this.use32bitAbi = paramApkLite.use32bitAbi;
      this.extractNativeLibs = paramApkLite.extractNativeLibs;
      this.oplibDependencyStr = paramApkLite.oplibDependencyStr;
    }
    
    public List<String> getAllCodePaths()
    {
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(this.baseCodePath);
      if (!ArrayUtils.isEmpty(this.splitCodePaths)) {
        Collections.addAll(localArrayList, this.splitCodePaths);
      }
      return localArrayList;
    }
  }
  
  public static class PackageParserException
    extends Exception
  {
    public final int error;
    
    public PackageParserException(int paramInt, String paramString)
    {
      super();
      this.error = paramInt;
    }
    
    public PackageParserException(int paramInt, String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
      this.error = paramInt;
    }
  }
  
  static class ParseComponentArgs
    extends PackageParser.ParsePackageItemArgs
  {
    final int descriptionRes;
    final int enabledRes;
    int flags;
    final int processRes;
    final String[] sepProcesses;
    
    ParseComponentArgs(PackageParser.Package paramPackage, String[] paramArrayOfString1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String[] paramArrayOfString2, int paramInt7, int paramInt8, int paramInt9)
    {
      super(paramArrayOfString1, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      this.sepProcesses = paramArrayOfString2;
      this.processRes = paramInt7;
      this.descriptionRes = paramInt8;
      this.enabledRes = paramInt9;
    }
  }
  
  static class ParsePackageItemArgs
  {
    final int bannerRes;
    final int iconRes;
    final int labelRes;
    final int logoRes;
    final int nameRes;
    final String[] outError;
    final PackageParser.Package owner;
    final int roundIconRes;
    TypedArray sa;
    String tag;
    
    ParsePackageItemArgs(PackageParser.Package paramPackage, String[] paramArrayOfString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      this.owner = paramPackage;
      this.outError = paramArrayOfString;
      this.nameRes = paramInt1;
      this.labelRes = paramInt2;
      this.iconRes = paramInt3;
      this.logoRes = paramInt5;
      this.bannerRes = paramInt6;
      this.roundIconRes = paramInt4;
    }
  }
  
  public static final class Permission
    extends PackageParser.Component<PackageParser.IntentInfo>
  {
    public PackageParser.PermissionGroup group;
    public final PermissionInfo info;
    public boolean tree;
    
    public Permission(PackageParser.Package paramPackage)
    {
      super();
      this.info = new PermissionInfo();
    }
    
    public Permission(PackageParser.Package paramPackage, PermissionInfo paramPermissionInfo)
    {
      super();
      this.info = paramPermissionInfo;
    }
    
    public void setPackageName(String paramString)
    {
      super.setPackageName(paramString);
      this.info.packageName = paramString;
    }
    
    public String toString()
    {
      return "Permission{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.info.name + "}";
    }
  }
  
  public static final class PermissionGroup
    extends PackageParser.Component<PackageParser.IntentInfo>
  {
    public final PermissionGroupInfo info;
    
    public PermissionGroup(PackageParser.Package paramPackage)
    {
      super();
      this.info = new PermissionGroupInfo();
    }
    
    public PermissionGroup(PackageParser.Package paramPackage, PermissionGroupInfo paramPermissionGroupInfo)
    {
      super();
      this.info = paramPermissionGroupInfo;
    }
    
    public void setPackageName(String paramString)
    {
      super.setPackageName(paramString);
      this.info.packageName = paramString;
    }
    
    public String toString()
    {
      return "PermissionGroup{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.info.name + "}";
    }
  }
  
  public static final class Provider
    extends PackageParser.Component<PackageParser.ProviderIntentInfo>
  {
    public final ProviderInfo info;
    public boolean syncable;
    
    public Provider(PackageParser.ParseComponentArgs paramParseComponentArgs, ProviderInfo paramProviderInfo)
    {
      super(paramProviderInfo);
      this.info = paramProviderInfo;
      this.info.applicationInfo = paramParseComponentArgs.owner.applicationInfo;
      this.syncable = false;
    }
    
    public Provider(Provider paramProvider)
    {
      super();
      this.info = paramProvider.info;
      this.syncable = paramProvider.syncable;
    }
    
    public void setPackageName(String paramString)
    {
      super.setPackageName(paramString);
      this.info.packageName = paramString;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append("Provider{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(' ');
      appendComponentShortName(localStringBuilder);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
  
  public static final class ProviderIntentInfo
    extends PackageParser.IntentInfo
  {
    public final PackageParser.Provider provider;
    
    public ProviderIntentInfo(PackageParser.Provider paramProvider)
    {
      this.provider = paramProvider;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append("ProviderIntentInfo{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(' ');
      this.provider.appendComponentShortName(localStringBuilder);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
  
  public static final class Service
    extends PackageParser.Component<PackageParser.ServiceIntentInfo>
  {
    public final ServiceInfo info;
    
    public Service(PackageParser.ParseComponentArgs paramParseComponentArgs, ServiceInfo paramServiceInfo)
    {
      super(paramServiceInfo);
      this.info = paramServiceInfo;
      this.info.applicationInfo = paramParseComponentArgs.owner.applicationInfo;
    }
    
    public void setPackageName(String paramString)
    {
      super.setPackageName(paramString);
      this.info.packageName = paramString;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append("Service{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(' ');
      appendComponentShortName(localStringBuilder);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
  
  public static final class ServiceIntentInfo
    extends PackageParser.IntentInfo
  {
    public final PackageParser.Service service;
    
    public ServiceIntentInfo(PackageParser.Service paramService)
    {
      this.service = paramService;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append("ServiceIntentInfo{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(' ');
      this.service.appendComponentShortName(localStringBuilder);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
  
  private static class SplitNameComparator
    implements Comparator<String>
  {
    public int compare(String paramString1, String paramString2)
    {
      if (paramString1 == null) {
        return -1;
      }
      if (paramString2 == null) {
        return 1;
      }
      return paramString1.compareTo(paramString2);
    }
  }
  
  public static class SplitPermissionInfo
  {
    public final String[] newPerms;
    public final String rootPerm;
    public final int targetSdk;
    
    public SplitPermissionInfo(String paramString, String[] paramArrayOfString, int paramInt)
    {
      this.rootPerm = paramString;
      this.newPerms = paramArrayOfString;
      this.targetSdk = paramInt;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PackageParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */