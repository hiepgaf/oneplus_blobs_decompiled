package com.android.server.am;

import android.app.ActivityManager.AppBootMode;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageParser.Activity;
import android.content.pm.PackageParser.ActivityIntentInfo;
import android.content.pm.PackageParser.Package;
import android.content.pm.PackageParser.Service;
import android.content.pm.PackageParser.ServiceIntentInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.util.ArrayMap;
import android.util.OpFeatures;
import android.util.Slog;
import com.android.server.pm.PackageManagerService;
import com.oneplus.config.ConfigGrabber;
import com.oneplus.config.ConfigObserver;
import com.oneplus.config.ConfigObserver.ConfigUpdater;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OnePlusAppBootManager
{
  public static final String ACTION_NOTIFICATION_LISTENER_UPDATE = "action.appboot.notification_listener_update";
  private static final String ACTION_TEST = "com.haha.action.test";
  private static String APPBOOT_CONFIG_NAME;
  private static final String APPBOOT_FILE = "/data/system/appboot/appboot.xml";
  private static String APPBOOT_VERSION = "1";
  public static boolean BLACKLIST_ENABLE = false;
  public static boolean DEBUG = false;
  public static boolean DEBUG_BINDER = false;
  public static final int GLOBAL_FLAG_SETTED_SIM_COUNTRY = 1;
  private static OnePlusAppBootManager INSTANCE;
  public static boolean IN_USING = OpFeatures.isSupport(new int[] { 22 });
  private static final int MSG_DELAY_FORCE_STOP_PKG = 2;
  private static final int MSG_GET_ONLINECONFIG = 3;
  private static final int MSG_HUGEPOWER_PKG_ACTION = 31;
  private static final int MSG_PERSIST_APPBOOT_LIST = 1;
  private static final int MSG_TYPE_PKG_ADD = 23;
  private static final int MSG_TYPE_PKG_REINSTALL = 21;
  private static final int MSG_TYPE_PKG_REMOVE = 22;
  private static final String PRELIST_FILE = "/system/etc/presetlist.xml";
  private static final String PROP_ALLOW = "persist.sys.appboot.allow";
  private static final String PROP_BLACKLIST = "persist.sys.appboot.blacklist";
  private static final String PROP_DEBUG = "persist.sys.appboot.debug";
  private static final String PROP_FLAGS = "persist.sys.appboot.flags";
  private static final String PROP_REGION = "persist.sys.oem.region";
  private static final String PROP_SIM_COUNTRY = "gsm.sim.operator.iso-country";
  private static final String PROP_USING = "persist.sys.appboot.using";
  private static final String SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
  private static final String SPECIAL_FILE = "/system/etc/appbootspecial.xml";
  public static final String TAG = "OnePlusAppBootManager";
  public static final int TAG_HUGE_POWER_DEFAULT = 1;
  public static final int TAG_HUGE_POWER_HIT = 2;
  public static final int TAG_HUGE_POWER_START_PROC = 4;
  public static final int VERSION = 16110304;
  private static final String XML_ATTR_ACTION = "action";
  private static final String XML_ATTR_BOOT = "boot";
  private static final String XML_ATTR_CALLEE = "callee";
  private static final String XML_ATTR_CALLER = "caller";
  private static final String XML_ATTR_FLAG = "flag";
  private static final String XML_ATTR_PACKAGE = "package";
  private static final String XML_ATTR_SWITCH = "switch";
  private static final String XML_ATTR_VERSION = "version";
  private static final String XML_TAG_APPBOOT = "appboot";
  private static final String XML_TAG_NAMESPACE = "";
  private static final String XML_TAG_PKG = "pkg";
  private static final String XML_TAG_PRELIST = "prelist";
  private static Object mABILock;
  public static boolean mAppBootSwitch;
  private static String mCurrentActivityPkg;
  private static String mCurrentIME;
  private static String mCurrentWallPaperPkg;
  private static int mGlobalFlags;
  private static HashSet<String> mHugePowerPkgSet;
  private static String mLastActivityPkg;
  private static HashMap<String, OnePlusAppBootInfo> mPkgMap;
  private static PackageManagerService mPms;
  private static HashMap<String, PrePkgInfo> mPrePkgMap;
  private static HashMap<String, HashSet<ProcessRecord>> mProcMap;
  private static String mRegion;
  private static boolean mScreenOn;
  private static ArrayList<String> mSyncServiceClassList;
  private ArrayList<String> mActivityClassBlackList = new ArrayList();
  private ActivityManagerService mAms = null;
  private ConfigObserver mAppBootConfigObserver;
  HandlerThread mAppBootThread = null;
  AppBootProcessHander mAppbootHandler = null;
  private ArrayList<String> mBroadcastIntentActionBlackList = new ArrayList();
  private ArrayList<String> mBroadcastIntentActionWhiteList = new ArrayList();
  private ArrayList<String> mBroadcastIntentClassBlackList = new ArrayList();
  private Context mContext = null;
  private ArrayList<String> mCurAppServiceClassWhiteList = new ArrayList(Arrays.asList(new String[] { "cn.jpush.android.service.PushService" }));
  private String mDefaultDailerPackage = null;
  private String mDefaultSMSPackage = null;
  private BroadcastReceiver mGeneralReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (!OnePlusAppBootManager.IN_USING) {
        return;
      }
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (OnePlusAppBootManager.DEBUG) {
        OnePlusAppBootManager.myLog("# mGeneralReceiver # onReceive # action=" + paramAnonymousContext);
      }
      if ("com.haha.action.test".equals(paramAnonymousContext))
      {
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("code");
        OnePlusAppBootManager.myLog("# mGeneralReceiver # onReceive # code = " + paramAnonymousContext);
        if ("persist".equals(paramAnonymousContext)) {
          OnePlusAppBootManager.-wrap8(OnePlusAppBootManager.this, 1000L);
        }
      }
      do
      {
        do
        {
          do
          {
            return;
            if ("prop_using_on".equals(paramAnonymousContext))
            {
              OnePlusAppBootManager.IN_USING = true;
              SystemProperties.set("persist.sys.appboot.using", "true");
              return;
            }
            if ("prop_using_off".equals(paramAnonymousContext))
            {
              OnePlusAppBootManager.IN_USING = false;
              SystemProperties.set("persist.sys.appboot.using", "false");
              return;
            }
            if ("prop_debug_on".equals(paramAnonymousContext))
            {
              OnePlusAppBootManager.DEBUG = true;
              SystemProperties.set("persist.sys.appboot.debug", "true");
              return;
            }
            if ("prop_debug_off".equals(paramAnonymousContext))
            {
              OnePlusAppBootManager.DEBUG = false;
              SystemProperties.set("persist.sys.appboot.debug", "false");
              return;
            }
            if ("prop_blacklist_on".equals(paramAnonymousContext))
            {
              OnePlusAppBootManager.BLACKLIST_ENABLE = true;
              SystemProperties.set("persist.sys.appboot.blacklist", "true");
              return;
            }
            if ("prop_blacklist_off".equals(paramAnonymousContext))
            {
              OnePlusAppBootManager.BLACKLIST_ENABLE = false;
              SystemProperties.set("persist.sys.appboot.blacklist", "false");
              return;
            }
            if ("dump".equals(paramAnonymousContext))
            {
              OnePlusAppBootManager.-wrap4(OnePlusAppBootManager.this);
              OnePlusAppBootManager.-wrap5(OnePlusAppBootManager.this);
              return;
            }
          } while (!paramAnonymousContext.startsWith("@"));
          if (paramAnonymousContext.startsWith("@huge@dump"))
          {
            OnePlusAppBootManager.-wrap3(OnePlusAppBootManager.this);
            return;
          }
        } while (!paramAnonymousContext.startsWith("@huge@add@"));
        paramAnonymousContext = paramAnonymousContext.substring("@huge@add@".length());
        OnePlusAppBootManager.-wrap9(OnePlusAppBootManager.this, paramAnonymousContext, true, 2);
        return;
        if ("android.intent.action.SCREEN_OFF".equals(paramAnonymousContext))
        {
          OnePlusAppBootManager.-set0(false);
          paramAnonymousContext = Message.obtain(OnePlusAppBootManager.this.mAppbootHandler, 31);
          paramAnonymousContext.arg1 = 0;
          OnePlusAppBootManager.this.mAppbootHandler.sendMessageAtFrontOfQueue(paramAnonymousContext);
          OnePlusAppBootManager.-wrap8(OnePlusAppBootManager.this, 1000L);
          return;
        }
        if ("android.intent.action.SCREEN_ON".equals(paramAnonymousContext))
        {
          OnePlusAppBootManager.-set0(true);
          paramAnonymousContext = Message.obtain(OnePlusAppBootManager.this.mAppbootHandler, 31);
          paramAnonymousContext.arg1 = 1;
          OnePlusAppBootManager.this.mAppbootHandler.sendMessageAtFrontOfQueue(paramAnonymousContext);
          return;
        }
      } while (!"android.intent.action.SIM_STATE_CHANGED".equals(paramAnonymousContext));
      OnePlusAppBootManager.-wrap0(OnePlusAppBootManager.this);
    }
  };
  private HashSet<String> mNotiListenerPkgSet = new HashSet();
  private BroadcastReceiver mPackageReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (!OnePlusAppBootManager.IN_USING) {
        return;
      }
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (OnePlusAppBootManager.DEBUG) {
        OnePlusAppBootManager.myLog("# mPackageReceiver # onReceive # action=" + paramAnonymousContext + " intent=" + paramAnonymousIntent);
      }
      if ("android.intent.action.PACKAGE_REPLACED".equals(paramAnonymousContext))
      {
        paramAnonymousContext = paramAnonymousIntent.getData();
        if (paramAnonymousContext == null) {
          return;
        }
        paramAnonymousContext = paramAnonymousContext.getSchemeSpecificPart();
        if (OnePlusAppBootManager.DEBUG) {
          OnePlusAppBootManager.myLog("# mPackageReceiver # onReceive # mPkgMap replaced " + paramAnonymousContext);
        }
        if (OnePlusAppBootManager.this.mAppbootHandler.hasMessages(paramAnonymousContext.hashCode())) {
          OnePlusAppBootManager.this.mAppbootHandler.removeMessages(paramAnonymousContext.hashCode());
        }
        paramAnonymousContext = Message.obtain(OnePlusAppBootManager.this.mAppbootHandler, paramAnonymousContext.hashCode(), 21, 0, paramAnonymousContext);
        OnePlusAppBootManager.this.mAppbootHandler.sendMessageDelayed(paramAnonymousContext, 1000L);
      }
      for (;;)
      {
        OnePlusAppBootManager.-wrap8(OnePlusAppBootManager.this, 20000L);
        return;
        if ("android.intent.action.PACKAGE_REMOVED".equals(paramAnonymousContext))
        {
          paramAnonymousContext = paramAnonymousIntent.getData();
          if (paramAnonymousContext == null) {
            return;
          }
          paramAnonymousContext = paramAnonymousContext.getSchemeSpecificPart();
          if (OnePlusAppBootManager.DEBUG) {
            OnePlusAppBootManager.myLog("# mPackageReceiver # onReceive # mPkgMap removed " + paramAnonymousContext);
          }
          paramAnonymousContext = Message.obtain(OnePlusAppBootManager.this.mAppbootHandler, paramAnonymousContext.hashCode(), 22, 0, paramAnonymousContext);
          OnePlusAppBootManager.this.mAppbootHandler.sendMessageDelayed(paramAnonymousContext, 1000L);
        }
        else if ("android.intent.action.PACKAGE_ADDED".equals(paramAnonymousContext))
        {
          paramAnonymousContext = paramAnonymousIntent.getData();
          if (paramAnonymousContext == null) {
            return;
          }
          paramAnonymousContext = paramAnonymousContext.getSchemeSpecificPart();
          if (OnePlusAppBootManager.DEBUG) {
            OnePlusAppBootManager.myLog("# mPackageReceiver # onReceive # mPkgMap added " + paramAnonymousContext);
          }
          if (OnePlusAppBootManager.this.mAppbootHandler.hasMessages(paramAnonymousContext.hashCode())) {
            OnePlusAppBootManager.this.mAppbootHandler.removeMessages(paramAnonymousContext.hashCode());
          }
          paramAnonymousContext = Message.obtain(OnePlusAppBootManager.this.mAppbootHandler, paramAnonymousContext.hashCode(), 23, 0, paramAnonymousContext);
          OnePlusAppBootManager.this.mAppbootHandler.sendMessageDelayed(paramAnonymousContext, 1000L);
        }
      }
    }
  };
  private ArrayList<String> mPresetWhiteListPackagesList = new ArrayList(Arrays.asList(new String[] { "com.google.", "com.android.vending", "com.oneplus.", "net.oneplus." }));
  private ArrayList<String> mProviderClassBlackList = new ArrayList();
  private ContentResolver mResolver = null;
  private ArrayList<String> mServiceActionBlackList = new ArrayList();
  private ArrayList<String> mServiceActionWhiteList = new ArrayList();
  private ArrayList<String> mServiceClassBlackList = new ArrayList();
  private SettingsObserver mSettingsObserver = null;
  private ArrayList<String> mWidgetBroadcastActionList = new ArrayList(Arrays.asList(new String[] { "android.appwidget.action.APPWIDGET_UPDATE", "android.appwidget.action.APPWIDGET_BIND", "android.appwidget.action.APPWIDGET_CONFIGURE", "android.appwidget.action.APPWIDGET_DELETED", "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS", "android.appwidget.action.APPWIDGET_DISABLED", "android.appwidget.action.APPWIDGET_ENABLED" }));
  
  static
  {
    BLACKLIST_ENABLE = true;
    DEBUG = false;
    DEBUG_BINDER = true;
    mAppBootSwitch = IN_USING;
    mGlobalFlags = 0;
    mRegion = "";
    mCurrentIME = null;
    mCurrentWallPaperPkg = null;
    mLastActivityPkg = null;
    mCurrentActivityPkg = null;
    mScreenOn = true;
    APPBOOT_CONFIG_NAME = "AppBoot";
    mHugePowerPkgSet = new HashSet();
    mProcMap = new HashMap();
    mPkgMap = new HashMap();
    mPrePkgMap = new HashMap();
    INSTANCE = null;
    mABILock = new Object();
    mSyncServiceClassList = new ArrayList();
  }
  
  private OnePlusAppBootManager(PackageManagerService paramPackageManagerService)
  {
    mPms = paramPackageManagerService;
    IN_USING = SystemProperties.getBoolean("persist.sys.appboot.using", IN_USING);
    DEBUG = SystemProperties.getBoolean("persist.sys.appboot.debug", DEBUG);
    BLACKLIST_ENABLE = SystemProperties.getBoolean("persist.sys.appboot.blacklist", BLACKLIST_ENABLE);
    mGlobalFlags = SystemProperties.getInt("persist.sys.appboot.flags", 0);
    mRegion = SystemProperties.get("persist.sys.oem.region", "");
    if (IN_USING)
    {
      mAppBootSwitch = IN_USING;
      readXml_prelist();
    }
    dumpInfo();
  }
  
  /* Error */
  private static int WriteStringToFile(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +5 -> 6
    //   4: iconst_m1
    //   5: ireturn
    //   6: aconst_null
    //   7: astore 4
    //   9: aconst_null
    //   10: astore_3
    //   11: aload 4
    //   13: astore_2
    //   14: new 442	java/io/File
    //   17: dup
    //   18: aload_0
    //   19: invokespecial 444	java/io/File:<init>	(Ljava/lang/String;)V
    //   22: astore 5
    //   24: aload 4
    //   26: astore_2
    //   27: aload 5
    //   29: invokevirtual 447	java/io/File:exists	()Z
    //   32: ifne +91 -> 123
    //   35: aload 4
    //   37: astore_2
    //   38: aload 5
    //   40: invokevirtual 451	java/io/File:getParentFile	()Ljava/io/File;
    //   43: invokevirtual 447	java/io/File:exists	()Z
    //   46: ifne +15 -> 61
    //   49: aload 4
    //   51: astore_2
    //   52: aload 5
    //   54: invokevirtual 451	java/io/File:getParentFile	()Ljava/io/File;
    //   57: invokevirtual 454	java/io/File:mkdirs	()Z
    //   60: pop
    //   61: aload 4
    //   63: astore_2
    //   64: aload 5
    //   66: invokevirtual 457	java/io/File:createNewFile	()Z
    //   69: pop
    //   70: aload 4
    //   72: astore_2
    //   73: aload 5
    //   75: invokevirtual 447	java/io/File:exists	()Z
    //   78: ifne +45 -> 123
    //   81: aload 4
    //   83: astore_2
    //   84: ldc 92
    //   86: new 459	java/lang/StringBuilder
    //   89: dup
    //   90: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   93: ldc_w 462
    //   96: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   99: aload 5
    //   101: invokevirtual 469	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   104: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   107: ldc_w 471
    //   110: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   113: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   116: invokestatic 479	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   119: pop
    //   120: bipush -2
    //   122: ireturn
    //   123: aload 4
    //   125: astore_2
    //   126: new 481	java/io/FileOutputStream
    //   129: dup
    //   130: aload_0
    //   131: invokespecial 482	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   134: astore_0
    //   135: aload_0
    //   136: aload_1
    //   137: invokevirtual 486	java/lang/String:getBytes	()[B
    //   140: invokevirtual 490	java/io/FileOutputStream:write	([B)V
    //   143: aload_0
    //   144: ifnull +175 -> 319
    //   147: aload_0
    //   148: invokevirtual 493	java/io/FileOutputStream:close	()V
    //   151: iconst_1
    //   152: ireturn
    //   153: astore_0
    //   154: ldc 92
    //   156: new 459	java/lang/StringBuilder
    //   159: dup
    //   160: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   163: ldc_w 495
    //   166: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   169: aload_0
    //   170: invokevirtual 498	java/io/IOException:getMessage	()Ljava/lang/String;
    //   173: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   176: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   179: invokestatic 479	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   182: pop
    //   183: iconst_1
    //   184: ireturn
    //   185: astore_1
    //   186: aload_3
    //   187: astore_0
    //   188: aload_0
    //   189: astore_2
    //   190: ldc 92
    //   192: new 459	java/lang/StringBuilder
    //   195: dup
    //   196: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   199: ldc_w 495
    //   202: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   205: aload_1
    //   206: invokevirtual 498	java/io/IOException:getMessage	()Ljava/lang/String;
    //   209: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   212: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   215: invokestatic 479	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   218: pop
    //   219: aload_0
    //   220: ifnull +7 -> 227
    //   223: aload_0
    //   224: invokevirtual 493	java/io/FileOutputStream:close	()V
    //   227: bipush -3
    //   229: ireturn
    //   230: astore_0
    //   231: ldc 92
    //   233: new 459	java/lang/StringBuilder
    //   236: dup
    //   237: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   240: ldc_w 495
    //   243: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   246: aload_0
    //   247: invokevirtual 498	java/io/IOException:getMessage	()Ljava/lang/String;
    //   250: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   253: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   256: invokestatic 479	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   259: pop
    //   260: bipush -3
    //   262: ireturn
    //   263: astore_0
    //   264: aload_2
    //   265: ifnull +7 -> 272
    //   268: aload_2
    //   269: invokevirtual 493	java/io/FileOutputStream:close	()V
    //   272: aload_0
    //   273: athrow
    //   274: astore_1
    //   275: ldc 92
    //   277: new 459	java/lang/StringBuilder
    //   280: dup
    //   281: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   284: ldc_w 495
    //   287: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   290: aload_1
    //   291: invokevirtual 498	java/io/IOException:getMessage	()Ljava/lang/String;
    //   294: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   297: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   300: invokestatic 479	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   303: pop
    //   304: goto -32 -> 272
    //   307: astore_1
    //   308: aload_0
    //   309: astore_2
    //   310: aload_1
    //   311: astore_0
    //   312: goto -48 -> 264
    //   315: astore_1
    //   316: goto -128 -> 188
    //   319: iconst_1
    //   320: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	321	0	paramString1	String
    //   0	321	1	paramString2	String
    //   13	297	2	localObject1	Object
    //   10	177	3	localObject2	Object
    //   7	117	4	localObject3	Object
    //   22	78	5	localFile	File
    // Exception table:
    //   from	to	target	type
    //   147	151	153	java/io/IOException
    //   14	24	185	java/io/IOException
    //   27	35	185	java/io/IOException
    //   38	49	185	java/io/IOException
    //   52	61	185	java/io/IOException
    //   64	70	185	java/io/IOException
    //   73	81	185	java/io/IOException
    //   84	120	185	java/io/IOException
    //   126	135	185	java/io/IOException
    //   223	227	230	java/io/IOException
    //   14	24	263	finally
    //   27	35	263	finally
    //   38	49	263	finally
    //   52	61	263	finally
    //   64	70	263	finally
    //   73	81	263	finally
    //   84	120	263	finally
    //   126	135	263	finally
    //   190	219	263	finally
    //   268	272	274	java/io/IOException
    //   135	143	307	finally
    //   135	143	315	java/io/IOException
  }
  
  private void dumpHugePowerPkgInfo()
  {
    synchronized (mHugePowerPkgSet)
    {
      Iterator localIterator = mHugePowerPkgSet.iterator();
      if (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Slog.d("OnePlusAppBootManager", "# dump # HugePowerPkg # " + str);
      }
    }
  }
  
  private void dumpInfo()
  {
    Slog.d("OnePlusAppBootManager", "# dump # VERSION # 16110304");
    Slog.d("OnePlusAppBootManager", "# dump # IN_USING # " + IN_USING);
    Slog.d("OnePlusAppBootManager", "# dump # mAppBootSwitch # " + mAppBootSwitch);
    Slog.d("OnePlusAppBootManager", "# dump # DEBUG # " + DEBUG);
    Slog.d("OnePlusAppBootManager", "# dump # BLACKLIST_ENABLE # " + BLACKLIST_ENABLE);
    Slog.d("OnePlusAppBootManager", "# dump # mGlobalFlags # " + mGlobalFlags);
  }
  
  private void dumpPkgMapInfos()
  {
    synchronized (mABILock)
    {
      if ((mPkgMap == null) && (mPkgMap.size() < 1))
      {
        Slog.d("OnePlusAppBootManager", " # dump # mPkgMap = null");
        return;
      }
      int i = 0;
      Iterator localIterator = mPkgMap.values().iterator();
      if (localIterator.hasNext())
      {
        OnePlusAppBootInfo localOnePlusAppBootInfo = (OnePlusAppBootInfo)localIterator.next();
        i += 1;
        Slog.d("OnePlusAppBootManager", "# dump # " + i + " : " + localOnePlusAppBootInfo);
      }
    }
  }
  
  private int forceStopPkg(String paramString)
  {
    if (DEBUG) {
      myLog("forceStopPkg # " + paramString);
    }
    if ((this.mAms != null) && (paramString != null)) {
      try
      {
        OnePlusAppBootInfo localOnePlusAppBootInfo = getAppBootInfo(paramString);
        if (localOnePlusAppBootInfo.getAction() != 2) {
          return 1;
        }
        if ((localOnePlusAppBootInfo.getPkgFlag() & 0x40) == 1) {
          return 2;
        }
        if (paramString.equals(mCurrentActivityPkg)) {
          return 3;
        }
        if (paramString.equals(mLastActivityPkg)) {
          return 4;
        }
        synchronized (this.mAms)
        {
          ActivityManagerService.boostPriorityForLockedSection();
          this.mAms.forceStopPackage(paramString, UserHandle.myUserId());
          localOnePlusAppBootInfo.setBootFlag(0);
          ActivityManagerService.resetPriorityAfterLockedSection();
          return 0;
        }
        return -1;
      }
      catch (Exception paramString)
      {
        Slog.e("OnePlusAppBootManager", "Exception# forceStopPkg: forceStopPkg error : " + paramString.getMessage());
        paramString.printStackTrace();
      }
    }
  }
  
  private static OnePlusAppBootInfo getAppBootInfo(String paramString)
  {
    if (paramString == null) {
      myLog(" !!!!!!!Exception # getAppBootInfo # pkgName = " + paramString);
    }
    ??? = (OnePlusAppBootInfo)mPkgMap.get(paramString);
    Object localObject1 = ???;
    if (??? == null)
    {
      localObject1 = new OnePlusAppBootInfo(paramString);
      ((OnePlusAppBootInfo)localObject1).setPPPackage(mPms.getPakcageInfo(paramString));
    }
    synchronized (mABILock)
    {
      try
      {
        mPkgMap.put(paramString, localObject1);
        if (DEBUG) {
          myLog("# getAppBootInfo # abi=" + localObject1);
        }
        return (OnePlusAppBootInfo)localObject1;
      }
      catch (Exception paramString)
      {
        for (;;)
        {
          Slog.e("OnePlusAppBootManager", "Fatal Exception # getAppBootInfo # " + paramString.getMessage());
          paramString.printStackTrace();
        }
      }
    }
  }
  
  public static final OnePlusAppBootManager getInstance(PackageManagerService paramPackageManagerService)
  {
    if (INSTANCE == null) {
      INSTANCE = new OnePlusAppBootManager(paramPackageManagerService);
    }
    return INSTANCE;
  }
  
  private String getPkgNameFromIntent(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      Slog.e("OnePlusAppBootManager", "Fatal Exception # getPkgNameFromIntent # intent=null");
      return null;
    }
    String str2 = paramIntent.getPackage();
    String str1 = str2;
    if (str2 == null)
    {
      paramIntent = paramIntent.getComponent();
      str1 = str2;
      if (paramIntent != null) {
        str1 = paramIntent.getPackageName();
      }
    }
    return str1;
  }
  
  private boolean isPackageInPresetWhitelist(String paramString)
  {
    if (paramString == null) {
      return true;
    }
    if (this.mPresetWhiteListPackagesList != null)
    {
      Iterator localIterator = this.mPresetWhiteListPackagesList.iterator();
      while (localIterator.hasNext()) {
        if (paramString.contains((String)localIterator.next()))
        {
          if (DEBUG) {
            myLog("# isPackageInPresetWhitelist # whitelist-pkg # pkgName = " + paramString);
          }
          return true;
        }
      }
    }
    return false;
  }
  
  public static void myLog(String paramString)
  {
    if (DEBUG)
    {
      if (DEBUG_BINDER)
      {
        String str = "#cuid=" + Binder.getCallingUid() + ", cpid=" + Binder.getCallingPid() + " # ";
        Slog.d("OnePlusAppBootManager", str + paramString);
      }
    }
    else {
      return;
    }
    Slog.d("OnePlusAppBootManager", paramString);
  }
  
  /* Error */
  private void readXml_specialList()
  {
    // Byte code:
    //   0: ldc_w 669
    //   3: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   6: new 330	java/util/ArrayList
    //   9: dup
    //   10: invokespecial 331	java/util/ArrayList:<init>	()V
    //   13: pop
    //   14: new 442	java/io/File
    //   17: dup
    //   18: ldc 89
    //   20: invokespecial 444	java/io/File:<init>	(Ljava/lang/String;)V
    //   23: astore_3
    //   24: aload_3
    //   25: invokevirtual 447	java/io/File:exists	()Z
    //   28: ifne +10 -> 38
    //   31: ldc_w 671
    //   34: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   37: return
    //   38: aconst_null
    //   39: astore_2
    //   40: aconst_null
    //   41: astore 5
    //   43: new 673	java/io/FileInputStream
    //   46: dup
    //   47: aload_3
    //   48: invokespecial 676	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   51: astore_3
    //   52: invokestatic 682	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   55: astore_2
    //   56: aload_2
    //   57: aload_3
    //   58: aconst_null
    //   59: invokeinterface 688 3 0
    //   64: aload_2
    //   65: invokeinterface 690 1 0
    //   70: istore_1
    //   71: iload_1
    //   72: iconst_2
    //   73: if_icmpne +84 -> 157
    //   76: aload_2
    //   77: invokeinterface 693 1 0
    //   82: astore 4
    //   84: ldc_w 695
    //   87: aload 4
    //   89: invokevirtual 572	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   92: ifeq +317 -> 409
    //   95: aload_2
    //   96: invokeinterface 698 1 0
    //   101: astore 4
    //   103: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   106: ifeq +27 -> 133
    //   109: new 459	java/lang/StringBuilder
    //   112: dup
    //   113: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   116: ldc_w 700
    //   119: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   122: aload 4
    //   124: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   127: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   130: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   133: aload 4
    //   135: ifnull +22 -> 157
    //   138: aload 4
    //   140: invokevirtual 703	java/lang/String:length	()I
    //   143: iconst_1
    //   144: if_icmple +13 -> 157
    //   147: aload_0
    //   148: getfield 352	com/android/server/am/OnePlusAppBootManager:mServiceActionBlackList	Ljava/util/ArrayList;
    //   151: aload 4
    //   153: invokevirtual 706	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   156: pop
    //   157: iload_1
    //   158: iconst_1
    //   159: if_icmpne -95 -> 64
    //   162: aload_3
    //   163: ifnull +1331 -> 1494
    //   166: aload_3
    //   167: invokevirtual 707	java/io/FileInputStream:close	()V
    //   170: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   173: ifeq +235 -> 408
    //   176: new 459	java/lang/StringBuilder
    //   179: dup
    //   180: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   183: ldc_w 709
    //   186: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   189: aload_0
    //   190: getfield 352	com/android/server/am/OnePlusAppBootManager:mServiceActionBlackList	Ljava/util/ArrayList;
    //   193: invokevirtual 710	java/util/ArrayList:size	()I
    //   196: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   199: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   202: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   205: new 459	java/lang/StringBuilder
    //   208: dup
    //   209: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   212: ldc_w 712
    //   215: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   218: aload_0
    //   219: getfield 354	com/android/server/am/OnePlusAppBootManager:mServiceActionWhiteList	Ljava/util/ArrayList;
    //   222: invokevirtual 710	java/util/ArrayList:size	()I
    //   225: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   228: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   231: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   234: new 459	java/lang/StringBuilder
    //   237: dup
    //   238: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   241: ldc_w 714
    //   244: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   247: aload_0
    //   248: getfield 356	com/android/server/am/OnePlusAppBootManager:mServiceClassBlackList	Ljava/util/ArrayList;
    //   251: invokevirtual 710	java/util/ArrayList:size	()I
    //   254: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   257: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   260: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   263: new 459	java/lang/StringBuilder
    //   266: dup
    //   267: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   270: ldc_w 716
    //   273: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   276: aload_0
    //   277: getfield 393	com/android/server/am/OnePlusAppBootManager:mBroadcastIntentClassBlackList	Ljava/util/ArrayList;
    //   280: invokevirtual 710	java/util/ArrayList:size	()I
    //   283: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   286: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   289: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   292: new 459	java/lang/StringBuilder
    //   295: dup
    //   296: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   299: ldc_w 718
    //   302: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   305: aload_0
    //   306: getfield 389	com/android/server/am/OnePlusAppBootManager:mBroadcastIntentActionWhiteList	Ljava/util/ArrayList;
    //   309: invokevirtual 710	java/util/ArrayList:size	()I
    //   312: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   315: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   318: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   321: new 459	java/lang/StringBuilder
    //   324: dup
    //   325: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   328: ldc_w 720
    //   331: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   334: aload_0
    //   335: getfield 391	com/android/server/am/OnePlusAppBootManager:mBroadcastIntentActionBlackList	Ljava/util/ArrayList;
    //   338: invokevirtual 710	java/util/ArrayList:size	()I
    //   341: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   344: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   347: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   350: new 459	java/lang/StringBuilder
    //   353: dup
    //   354: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   357: ldc_w 722
    //   360: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   363: aload_0
    //   364: getfield 348	com/android/server/am/OnePlusAppBootManager:mActivityClassBlackList	Ljava/util/ArrayList;
    //   367: invokevirtual 710	java/util/ArrayList:size	()I
    //   370: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   373: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   376: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   379: new 459	java/lang/StringBuilder
    //   382: dup
    //   383: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   386: ldc_w 724
    //   389: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   392: aload_0
    //   393: getfield 350	com/android/server/am/OnePlusAppBootManager:mProviderClassBlackList	Ljava/util/ArrayList;
    //   396: invokevirtual 710	java/util/ArrayList:size	()I
    //   399: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   402: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   405: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   408: return
    //   409: ldc_w 726
    //   412: aload 4
    //   414: invokevirtual 572	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   417: ifeq +330 -> 747
    //   420: aload_2
    //   421: invokeinterface 698 1 0
    //   426: astore 4
    //   428: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   431: ifeq +27 -> 458
    //   434: new 459	java/lang/StringBuilder
    //   437: dup
    //   438: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   441: ldc_w 728
    //   444: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   447: aload 4
    //   449: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   452: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   455: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   458: aload 4
    //   460: ifnull -303 -> 157
    //   463: aload 4
    //   465: invokevirtual 703	java/lang/String:length	()I
    //   468: iconst_1
    //   469: if_icmple -312 -> 157
    //   472: aload_0
    //   473: getfield 354	com/android/server/am/OnePlusAppBootManager:mServiceActionWhiteList	Ljava/util/ArrayList;
    //   476: aload 4
    //   478: invokevirtual 706	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   481: pop
    //   482: goto -325 -> 157
    //   485: astore 4
    //   487: aload_3
    //   488: astore_2
    //   489: ldc 92
    //   491: ldc_w 730
    //   494: aload 4
    //   496: invokestatic 733	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   499: pop
    //   500: aload_3
    //   501: ifnull +7 -> 508
    //   504: aload_3
    //   505: invokevirtual 707	java/io/FileInputStream:close	()V
    //   508: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   511: ifeq -103 -> 408
    //   514: new 459	java/lang/StringBuilder
    //   517: dup
    //   518: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   521: ldc_w 709
    //   524: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   527: aload_0
    //   528: getfield 352	com/android/server/am/OnePlusAppBootManager:mServiceActionBlackList	Ljava/util/ArrayList;
    //   531: invokevirtual 710	java/util/ArrayList:size	()I
    //   534: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   537: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   540: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   543: new 459	java/lang/StringBuilder
    //   546: dup
    //   547: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   550: ldc_w 712
    //   553: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   556: aload_0
    //   557: getfield 354	com/android/server/am/OnePlusAppBootManager:mServiceActionWhiteList	Ljava/util/ArrayList;
    //   560: invokevirtual 710	java/util/ArrayList:size	()I
    //   563: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   566: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   569: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   572: new 459	java/lang/StringBuilder
    //   575: dup
    //   576: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   579: ldc_w 714
    //   582: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   585: aload_0
    //   586: getfield 356	com/android/server/am/OnePlusAppBootManager:mServiceClassBlackList	Ljava/util/ArrayList;
    //   589: invokevirtual 710	java/util/ArrayList:size	()I
    //   592: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   595: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   598: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   601: new 459	java/lang/StringBuilder
    //   604: dup
    //   605: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   608: ldc_w 716
    //   611: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   614: aload_0
    //   615: getfield 393	com/android/server/am/OnePlusAppBootManager:mBroadcastIntentClassBlackList	Ljava/util/ArrayList;
    //   618: invokevirtual 710	java/util/ArrayList:size	()I
    //   621: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   624: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   627: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   630: new 459	java/lang/StringBuilder
    //   633: dup
    //   634: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   637: ldc_w 718
    //   640: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   643: aload_0
    //   644: getfield 389	com/android/server/am/OnePlusAppBootManager:mBroadcastIntentActionWhiteList	Ljava/util/ArrayList;
    //   647: invokevirtual 710	java/util/ArrayList:size	()I
    //   650: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   653: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   656: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   659: new 459	java/lang/StringBuilder
    //   662: dup
    //   663: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   666: ldc_w 720
    //   669: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   672: aload_0
    //   673: getfield 391	com/android/server/am/OnePlusAppBootManager:mBroadcastIntentActionBlackList	Ljava/util/ArrayList;
    //   676: invokevirtual 710	java/util/ArrayList:size	()I
    //   679: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   682: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   685: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   688: new 459	java/lang/StringBuilder
    //   691: dup
    //   692: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   695: ldc_w 722
    //   698: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   701: aload_0
    //   702: getfield 348	com/android/server/am/OnePlusAppBootManager:mActivityClassBlackList	Ljava/util/ArrayList;
    //   705: invokevirtual 710	java/util/ArrayList:size	()I
    //   708: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   711: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   714: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   717: new 459	java/lang/StringBuilder
    //   720: dup
    //   721: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   724: ldc_w 724
    //   727: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   730: aload_0
    //   731: getfield 350	com/android/server/am/OnePlusAppBootManager:mProviderClassBlackList	Ljava/util/ArrayList;
    //   734: invokevirtual 710	java/util/ArrayList:size	()I
    //   737: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   740: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   743: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   746: return
    //   747: ldc_w 735
    //   750: aload 4
    //   752: invokevirtual 572	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   755: ifeq +317 -> 1072
    //   758: aload_2
    //   759: invokeinterface 698 1 0
    //   764: astore 4
    //   766: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   769: ifeq +27 -> 796
    //   772: new 459	java/lang/StringBuilder
    //   775: dup
    //   776: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   779: ldc_w 737
    //   782: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   785: aload 4
    //   787: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   790: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   793: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   796: aload 4
    //   798: ifnull -641 -> 157
    //   801: aload 4
    //   803: invokevirtual 703	java/lang/String:length	()I
    //   806: iconst_1
    //   807: if_icmple -650 -> 157
    //   810: aload_0
    //   811: getfield 356	com/android/server/am/OnePlusAppBootManager:mServiceClassBlackList	Ljava/util/ArrayList;
    //   814: aload 4
    //   816: invokevirtual 706	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   819: pop
    //   820: goto -663 -> 157
    //   823: astore_2
    //   824: aload_3
    //   825: ifnull +7 -> 832
    //   828: aload_3
    //   829: invokevirtual 707	java/io/FileInputStream:close	()V
    //   832: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   835: ifeq +235 -> 1070
    //   838: new 459	java/lang/StringBuilder
    //   841: dup
    //   842: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   845: ldc_w 709
    //   848: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   851: aload_0
    //   852: getfield 352	com/android/server/am/OnePlusAppBootManager:mServiceActionBlackList	Ljava/util/ArrayList;
    //   855: invokevirtual 710	java/util/ArrayList:size	()I
    //   858: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   861: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   864: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   867: new 459	java/lang/StringBuilder
    //   870: dup
    //   871: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   874: ldc_w 712
    //   877: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   880: aload_0
    //   881: getfield 354	com/android/server/am/OnePlusAppBootManager:mServiceActionWhiteList	Ljava/util/ArrayList;
    //   884: invokevirtual 710	java/util/ArrayList:size	()I
    //   887: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   890: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   893: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   896: new 459	java/lang/StringBuilder
    //   899: dup
    //   900: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   903: ldc_w 714
    //   906: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   909: aload_0
    //   910: getfield 356	com/android/server/am/OnePlusAppBootManager:mServiceClassBlackList	Ljava/util/ArrayList;
    //   913: invokevirtual 710	java/util/ArrayList:size	()I
    //   916: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   919: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   922: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   925: new 459	java/lang/StringBuilder
    //   928: dup
    //   929: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   932: ldc_w 716
    //   935: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   938: aload_0
    //   939: getfield 393	com/android/server/am/OnePlusAppBootManager:mBroadcastIntentClassBlackList	Ljava/util/ArrayList;
    //   942: invokevirtual 710	java/util/ArrayList:size	()I
    //   945: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   948: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   951: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   954: new 459	java/lang/StringBuilder
    //   957: dup
    //   958: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   961: ldc_w 718
    //   964: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   967: aload_0
    //   968: getfield 389	com/android/server/am/OnePlusAppBootManager:mBroadcastIntentActionWhiteList	Ljava/util/ArrayList;
    //   971: invokevirtual 710	java/util/ArrayList:size	()I
    //   974: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   977: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   980: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   983: new 459	java/lang/StringBuilder
    //   986: dup
    //   987: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   990: ldc_w 720
    //   993: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   996: aload_0
    //   997: getfield 391	com/android/server/am/OnePlusAppBootManager:mBroadcastIntentActionBlackList	Ljava/util/ArrayList;
    //   1000: invokevirtual 710	java/util/ArrayList:size	()I
    //   1003: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1006: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1009: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   1012: new 459	java/lang/StringBuilder
    //   1015: dup
    //   1016: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   1019: ldc_w 722
    //   1022: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1025: aload_0
    //   1026: getfield 348	com/android/server/am/OnePlusAppBootManager:mActivityClassBlackList	Ljava/util/ArrayList;
    //   1029: invokevirtual 710	java/util/ArrayList:size	()I
    //   1032: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1035: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1038: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   1041: new 459	java/lang/StringBuilder
    //   1044: dup
    //   1045: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   1048: ldc_w 724
    //   1051: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1054: aload_0
    //   1055: getfield 350	com/android/server/am/OnePlusAppBootManager:mProviderClassBlackList	Ljava/util/ArrayList;
    //   1058: invokevirtual 710	java/util/ArrayList:size	()I
    //   1061: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1064: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1067: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   1070: aload_2
    //   1071: athrow
    //   1072: ldc_w 739
    //   1075: aload 4
    //   1077: invokevirtual 572	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1080: ifeq +68 -> 1148
    //   1083: aload_2
    //   1084: invokeinterface 698 1 0
    //   1089: astore 4
    //   1091: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   1094: ifeq +27 -> 1121
    //   1097: new 459	java/lang/StringBuilder
    //   1100: dup
    //   1101: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   1104: ldc_w 741
    //   1107: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1110: aload 4
    //   1112: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1115: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1118: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   1121: aload 4
    //   1123: ifnull -966 -> 157
    //   1126: aload 4
    //   1128: invokevirtual 703	java/lang/String:length	()I
    //   1131: iconst_1
    //   1132: if_icmple -975 -> 157
    //   1135: aload_0
    //   1136: getfield 393	com/android/server/am/OnePlusAppBootManager:mBroadcastIntentClassBlackList	Ljava/util/ArrayList;
    //   1139: aload 4
    //   1141: invokevirtual 706	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1144: pop
    //   1145: goto -988 -> 157
    //   1148: ldc_w 743
    //   1151: aload 4
    //   1153: invokevirtual 572	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1156: ifeq +68 -> 1224
    //   1159: aload_2
    //   1160: invokeinterface 698 1 0
    //   1165: astore 4
    //   1167: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   1170: ifeq +27 -> 1197
    //   1173: new 459	java/lang/StringBuilder
    //   1176: dup
    //   1177: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   1180: ldc_w 745
    //   1183: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1186: aload 4
    //   1188: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1191: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1194: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   1197: aload 4
    //   1199: ifnull -1042 -> 157
    //   1202: aload 4
    //   1204: invokevirtual 703	java/lang/String:length	()I
    //   1207: iconst_1
    //   1208: if_icmple -1051 -> 157
    //   1211: aload_0
    //   1212: getfield 389	com/android/server/am/OnePlusAppBootManager:mBroadcastIntentActionWhiteList	Ljava/util/ArrayList;
    //   1215: aload 4
    //   1217: invokevirtual 706	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1220: pop
    //   1221: goto -1064 -> 157
    //   1224: ldc_w 747
    //   1227: aload 4
    //   1229: invokevirtual 572	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1232: ifeq +68 -> 1300
    //   1235: aload_2
    //   1236: invokeinterface 698 1 0
    //   1241: astore 4
    //   1243: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   1246: ifeq +27 -> 1273
    //   1249: new 459	java/lang/StringBuilder
    //   1252: dup
    //   1253: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   1256: ldc_w 749
    //   1259: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1262: aload 4
    //   1264: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1267: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1270: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   1273: aload 4
    //   1275: ifnull -1118 -> 157
    //   1278: aload 4
    //   1280: invokevirtual 703	java/lang/String:length	()I
    //   1283: iconst_1
    //   1284: if_icmple -1127 -> 157
    //   1287: aload_0
    //   1288: getfield 391	com/android/server/am/OnePlusAppBootManager:mBroadcastIntentActionBlackList	Ljava/util/ArrayList;
    //   1291: aload 4
    //   1293: invokevirtual 706	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1296: pop
    //   1297: goto -1140 -> 157
    //   1300: ldc_w 751
    //   1303: aload 4
    //   1305: invokevirtual 572	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1308: ifeq +68 -> 1376
    //   1311: aload_2
    //   1312: invokeinterface 698 1 0
    //   1317: astore 4
    //   1319: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   1322: ifeq +27 -> 1349
    //   1325: new 459	java/lang/StringBuilder
    //   1328: dup
    //   1329: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   1332: ldc_w 753
    //   1335: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1338: aload 4
    //   1340: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1343: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1346: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   1349: aload 4
    //   1351: ifnull -1194 -> 157
    //   1354: aload 4
    //   1356: invokevirtual 703	java/lang/String:length	()I
    //   1359: iconst_1
    //   1360: if_icmple -1203 -> 157
    //   1363: aload_0
    //   1364: getfield 348	com/android/server/am/OnePlusAppBootManager:mActivityClassBlackList	Ljava/util/ArrayList;
    //   1367: aload 4
    //   1369: invokevirtual 706	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1372: pop
    //   1373: goto -1216 -> 157
    //   1376: ldc_w 755
    //   1379: aload 4
    //   1381: invokevirtual 572	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1384: ifeq -1227 -> 157
    //   1387: aload_2
    //   1388: invokeinterface 698 1 0
    //   1393: astore 4
    //   1395: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   1398: ifeq +27 -> 1425
    //   1401: new 459	java/lang/StringBuilder
    //   1404: dup
    //   1405: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   1408: ldc_w 757
    //   1411: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1414: aload 4
    //   1416: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1419: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1422: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   1425: aload 4
    //   1427: ifnull -1270 -> 157
    //   1430: aload 4
    //   1432: invokevirtual 703	java/lang/String:length	()I
    //   1435: iconst_1
    //   1436: if_icmple -1279 -> 157
    //   1439: aload_0
    //   1440: getfield 350	com/android/server/am/OnePlusAppBootManager:mProviderClassBlackList	Ljava/util/ArrayList;
    //   1443: aload 4
    //   1445: invokevirtual 706	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1448: pop
    //   1449: goto -1292 -> 157
    //   1452: astore_2
    //   1453: aload_2
    //   1454: invokevirtual 758	java/io/IOException:printStackTrace	()V
    //   1457: goto -1287 -> 170
    //   1460: astore_2
    //   1461: aload_2
    //   1462: invokevirtual 758	java/io/IOException:printStackTrace	()V
    //   1465: goto -957 -> 508
    //   1468: astore_3
    //   1469: aload_3
    //   1470: invokevirtual 758	java/io/IOException:printStackTrace	()V
    //   1473: goto -641 -> 832
    //   1476: astore 4
    //   1478: aload_2
    //   1479: astore_3
    //   1480: aload 4
    //   1482: astore_2
    //   1483: goto -659 -> 824
    //   1486: astore 4
    //   1488: aload 5
    //   1490: astore_3
    //   1491: goto -1004 -> 487
    //   1494: goto -1324 -> 170
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1497	0	this	OnePlusAppBootManager
    //   70	90	1	i	int
    //   39	720	2	localObject1	Object
    //   823	565	2	localObject2	Object
    //   1452	2	2	localIOException1	java.io.IOException
    //   1460	19	2	localIOException2	java.io.IOException
    //   1482	1	2	localObject3	Object
    //   23	806	3	localObject4	Object
    //   1468	2	3	localIOException3	java.io.IOException
    //   1479	12	3	localObject5	Object
    //   82	395	4	str1	String
    //   485	266	4	localException1	Exception
    //   764	680	4	str2	String
    //   1476	5	4	localObject6	Object
    //   1486	1	4	localException2	Exception
    //   41	1448	5	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   52	64	485	java/lang/Exception
    //   64	71	485	java/lang/Exception
    //   76	133	485	java/lang/Exception
    //   138	157	485	java/lang/Exception
    //   409	458	485	java/lang/Exception
    //   463	482	485	java/lang/Exception
    //   747	796	485	java/lang/Exception
    //   801	820	485	java/lang/Exception
    //   1072	1121	485	java/lang/Exception
    //   1126	1145	485	java/lang/Exception
    //   1148	1197	485	java/lang/Exception
    //   1202	1221	485	java/lang/Exception
    //   1224	1273	485	java/lang/Exception
    //   1278	1297	485	java/lang/Exception
    //   1300	1349	485	java/lang/Exception
    //   1354	1373	485	java/lang/Exception
    //   1376	1425	485	java/lang/Exception
    //   1430	1449	485	java/lang/Exception
    //   52	64	823	finally
    //   64	71	823	finally
    //   76	133	823	finally
    //   138	157	823	finally
    //   409	458	823	finally
    //   463	482	823	finally
    //   747	796	823	finally
    //   801	820	823	finally
    //   1072	1121	823	finally
    //   1126	1145	823	finally
    //   1148	1197	823	finally
    //   1202	1221	823	finally
    //   1224	1273	823	finally
    //   1278	1297	823	finally
    //   1300	1349	823	finally
    //   1354	1373	823	finally
    //   1376	1425	823	finally
    //   1430	1449	823	finally
    //   166	170	1452	java/io/IOException
    //   504	508	1460	java/io/IOException
    //   828	832	1468	java/io/IOException
    //   43	52	1476	finally
    //   489	500	1476	finally
    //   43	52	1486	java/lang/Exception
  }
  
  private void registerGeneralReceiver()
  {
    if (this.mContext == null)
    {
      Slog.e("OnePlusAppBootManager", "Fatal Exception # registerGeneralReceiver # mContext=null");
      return;
    }
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.setPriority(Integer.MAX_VALUE);
    localIntentFilter.addAction("com.haha.action.test");
    localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
    localIntentFilter.addAction("android.intent.action.SCREEN_ON");
    if ((!mRegion.equalsIgnoreCase("CN")) && ((mGlobalFlags & 0x1) == 0) && (!responseSIMStateChanged())) {
      localIntentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
    }
    this.mContext.registerReceiver(this.mGeneralReceiver, localIntentFilter);
  }
  
  private void registerPackageReceiver()
  {
    if (this.mContext == null)
    {
      Slog.e("OnePlusAppBootManager", "Fatal Exception # registerPackageReceiver # mContext=null");
      return;
    }
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addDataScheme("package");
    localIntentFilter.setPriority(1000);
    this.mContext.registerReceiver(this.mPackageReceiver, localIntentFilter);
  }
  
  private void removeAppBootInfo(String paramString)
  {
    if (DEBUG) {
      myLog("# removeAppBootInfo # pkgName=" + paramString);
    }
    synchronized (mABILock)
    {
      mPkgMap.remove(paramString);
      return;
    }
  }
  
  private void resolveAppBootConfigFromJSON(JSONArray arg1)
  {
    if (??? == null) {
      return;
    }
    int i = 0;
    label456:
    for (;;)
    {
      try
      {
        Object localObject4;
        Object localObject5;
        if (i < ???.length())
        {
          localObject4 = ???.getJSONObject(i);
          if (!((JSONObject)localObject4).getString("name").equals("pre_pkg_map")) {
            break label456;
          }
          synchronized (mPrePkgMap)
          {
            localObject4 = ((JSONObject)localObject4).getJSONArray("value");
            int j = 0;
            if (j < ((JSONArray)localObject4).length())
            {
              Object localObject6 = ((JSONArray)localObject4).getJSONObject(j);
              localObject5 = ((JSONObject)localObject6).getString("package");
              String str = ((JSONObject)localObject6).getString("flag");
              localObject6 = ((JSONObject)localObject6).getString("action");
              mPrePkgMap.put(localObject5, new PrePkgInfo((String)localObject5, Integer.parseInt(str), Integer.parseInt((String)localObject6)));
              if (DEBUG) {
                myLog("resolveAppBootConfigFromJSON # " + mPrePkgMap.get(localObject5));
              }
              j += 1;
              continue;
            }
          }
        }
        i += 1;
      }
      catch (JSONException ???)
      {
        Slog.e("OnePlusAppBootManager", "[OnlineConfig] JSONException:" + ???.getMessage());
        if ((mPrePkgMap == null) && (mPrePkgMap.size() < 1))
        {
          return;
          Slog.v("OnePlusAppBootManager", "[OnlineConfig] AppBoot updated complete");
        }
      }
      catch (Exception ???)
      {
        Slog.e("OnePlusAppBootManager", "[OnlineConfig] Exception:" + ???.getMessage());
        continue;
        synchronized (mABILock)
        {
          if (mPkgMap == null)
          {
            i = mPkgMap.size();
            if (i < 1) {
              return;
            }
          }
          ??? = mPrePkgMap.values().iterator();
          if (!((Iterator)???).hasNext()) {
            continue;
          }
          localObject4 = (PrePkgInfo)((Iterator)???).next();
          if ((localObject4 == null) || (((PrePkgInfo)localObject4).mPkgName == null) || ((((PrePkgInfo)localObject4).mFlag & 0x4000) == 0)) {
            continue;
          }
        }
        synchronized (mABILock)
        {
          localObject5 = (OnePlusAppBootInfo)mPkgMap.get(((PrePkgInfo)localObject4).mPkgName);
          if ((localObject5 != null) && (((OnePlusAppBootInfo)localObject5).getAction() != ((PrePkgInfo)localObject4).mAction) && ((((PrePkgInfo)localObject4).mAction == 1) || (((PrePkgInfo)localObject4).mAction == 2))) {
            ((OnePlusAppBootInfo)localObject5).setAction(((PrePkgInfo)localObject4).mAction);
          }
          continue;
          localObject2 = finally;
          throw ((Throwable)localObject2);
        }
        return;
      }
    }
  }
  
  private boolean responseSIMStateChanged()
  {
    if ((mGlobalFlags & 0x1) != 0) {
      return true;
    }
    boolean bool = false;
    String str = SystemProperties.get("gsm.sim.operator.iso-country", "");
    if (str.length() >= 2)
    {
      if ((!str.contains("in")) && (!str.contains("cn"))) {
        break label103;
      }
      setAppBootState(true);
    }
    for (;;)
    {
      bool = true;
      if ((DEBUG) || (bool)) {
        Slog.i("OnePlusAppBootManager", "responseSIMStateChanged # mccCountry=" + str + ", ret=" + bool);
      }
      return bool;
      label103:
      setAppBootState(false);
    }
  }
  
  private void schedulePersistAppBootInfo(long paramLong)
  {
    if (!IN_USING) {
      return;
    }
    if (DEBUG) {
      myLog("schedulePersistAppBootInfo #");
    }
    if (this.mAppbootHandler.hasMessages(1))
    {
      if (DEBUG) {
        myLog("schedulePersistAppBootInfo # hasMessages # MSG_PERSIST_APPBOOT_LIST");
      }
      this.mAppbootHandler.removeMessages(1);
    }
    this.mAppbootHandler.sendEmptyMessageDelayed(1, paramLong);
  }
  
  private OnePlusAppBootInfo updateAppBootInfo(String paramString)
  {
    if (paramString == null) {
      myLog(" !!!!!!!Exception # updateAppBootInfo # pkgName = " + paramString);
    }
    OnePlusAppBootInfo localOnePlusAppBootInfo = (OnePlusAppBootInfo)mPkgMap.get(paramString);
    if (localOnePlusAppBootInfo == null)
    {
      localOnePlusAppBootInfo = new OnePlusAppBootInfo(paramString);
      localOnePlusAppBootInfo.setPPPackage(mPms.getPakcageInfo(paramString));
    }
    for (;;)
    {
      synchronized (mABILock)
      {
        try
        {
          mPkgMap.put(paramString, localOnePlusAppBootInfo);
          paramString = localOnePlusAppBootInfo;
          if (DEBUG) {
            myLog("# updateAppBootInfo # abi=" + paramString);
          }
          return paramString;
        }
        catch (Exception paramString)
        {
          Slog.e("OnePlusAppBootManager", "Fatal Exception # getAppBootInfo # " + paramString.getMessage());
          paramString.printStackTrace();
          continue;
        }
      }
      localOnePlusAppBootInfo.setPPPackage(mPms.getPakcageInfo(paramString));
      paramString = localOnePlusAppBootInfo;
    }
  }
  
  private void updateHugePowerPackage(String paramString, boolean paramBoolean, int paramInt)
  {
    if (paramString == null) {
      return;
    }
    localObject = getAppBootInfo(paramString);
    if (DEBUG) {
      Slog.d("OnePlusAppBootManager", "updateHugePowerPackage # pkgName=" + paramString + ", add=" + paramBoolean + " abi=" + localObject);
    }
    if ((!mScreenOn) && (paramBoolean) && (paramInt == 2) && (((OnePlusAppBootInfo)localObject).getAction() == 1)) {
      ((OnePlusAppBootInfo)localObject).setAction(2);
    }
    localObject = mHugePowerPkgSet;
    if ((paramBoolean) && (paramInt == 2)) {}
    for (;;)
    {
      try
      {
        mHugePowerPkgSet.add(paramString);
        return;
      }
      finally {}
      if ((!paramBoolean) && (paramInt == 4)) {
        mHugePowerPkgSet.remove(paramString);
      }
    }
  }
  
  private void updateHugePowerPkgTempAction(boolean paramBoolean)
  {
    for (;;)
    {
      synchronized (mHugePowerPkgSet)
      {
        Iterator localIterator = mHugePowerPkgSet.iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        OnePlusAppBootInfo localOnePlusAppBootInfo = getAppBootInfo((String)localIterator.next());
        if (paramBoolean)
        {
          i = 1;
          localOnePlusAppBootInfo.setAction(i);
        }
      }
      int i = 2;
    }
  }
  
  private void updateLinkedPkgsInfo(OnePlusAppBootInfo paramOnePlusAppBootInfo, String paramString1, String paramString2)
  {
    paramString1 = getArrayListFromString(paramString1);
    if (paramString1 != null)
    {
      paramString1 = paramString1.iterator();
      while (paramString1.hasNext())
      {
        String str = (String)paramString1.next();
        if (DEBUG) {
          myLog("# updateLinkedPkgsInfo # caller pkg=" + str);
        }
        if (mPms.getPakcageInfo(str) != null) {
          paramOnePlusAppBootInfo.addCallerPackage(str);
        } else if (DEBUG) {
          myLog("# updateLinkedPkgsInfo # caller pkg=" + str + " -> not exist");
        }
      }
    }
    paramString1 = getArrayListFromString(paramString2);
    if (paramString1 != null)
    {
      paramString1 = paramString1.iterator();
      while (paramString1.hasNext())
      {
        paramString2 = (String)paramString1.next();
        if (DEBUG) {
          myLog("# updateLinkedPkgsInfo # callee pkg=" + paramString2);
        }
        if (mPms.getPakcageInfo(paramString2) != null) {
          paramOnePlusAppBootInfo.addCalleePackage(paramString2);
        } else if (DEBUG) {
          myLog("# updateLinkedPkgsInfo # callee pkg=" + paramString2 + " -> not exist");
        }
      }
    }
  }
  
  private void updateNotificationListener(String paramString)
  {
    boolean bool = false;
    synchronized (this.mNotiListenerPkgSet)
    {
      if (this.mNotiListenerPkgSet.contains(paramString))
      {
        this.mNotiListenerPkgSet.remove(paramString);
        bool = true;
      }
      if (DEBUG) {
        myLog("# updateNotificationListener # update=" + bool + " # pkgName=" + paramString);
      }
      if (bool)
      {
        ??? = new Intent("action.appboot.notification_listener_update");
        ((Intent)???).putExtra("pkg", paramString);
        this.mContext.sendBroadcastAsUser((Intent)???, UserHandle.CURRENT);
      }
      return;
    }
  }
  
  private void updateSettingsObserver(String paramString)
  {
    this.mDefaultSMSPackage = Settings.Secure.getString(this.mResolver, "sms_default_application");
    this.mDefaultDailerPackage = Settings.Secure.getString(this.mResolver, "dialer_default_application");
    if (DEBUG) {
      myLog("# updateSettingsObserver #tag=" + paramString + ",mDefaultSMSPackage=" + this.mDefaultSMSPackage + ",mDefaultDailerPackage=" + this.mDefaultDailerPackage);
    }
    if (this.mDefaultSMSPackage != null) {
      getAppBootInfo(this.mDefaultSMSPackage).setBootFlag(1);
    }
    if (this.mDefaultDailerPackage != null) {
      getAppBootInfo(this.mDefaultDailerPackage).setBootFlag(1);
    }
  }
  
  public void addDependencyPackageFlag(String paramString, int paramInt)
  {
    if (!IN_USING) {
      return;
    }
    OnePlusAppBootInfo localOnePlusAppBootInfo = getAppBootInfo(paramString);
    if ((localOnePlusAppBootInfo.getPkgFlag() & 0x40) == 0)
    {
      localOnePlusAppBootInfo.setDependencyPackageFlag();
      if (DEBUG) {
        myLog("# addDependencyPackageFlag # go pkgName=" + paramString + ", callingPid =" + paramInt);
      }
    }
  }
  
  public boolean canActivityGo(ActivityInfo paramActivityInfo, String paramString)
  {
    if ((!IN_USING) || (!mAppBootSwitch)) {
      return true;
    }
    if (paramActivityInfo != null)
    {
      OnePlusAppBootInfo localOnePlusAppBootInfo = getAppBootInfo(paramActivityInfo.packageName);
      if (DEBUG)
      {
        myLog("# canActivityGo # aInfo=" + paramActivityInfo + ", name = " + paramActivityInfo.name + ", Info=" + paramActivityInfo.applicationInfo);
        if (paramActivityInfo.applicationInfo != null) {
          myLog("# canActivityGo #2 className =" + paramActivityInfo.applicationInfo.className);
        }
      }
      if ((paramActivityInfo.name != null) && (paramActivityInfo.packageName != null) && (BLACKLIST_ENABLE) && (localOnePlusAppBootInfo.getBlackListEnableFlag()) && (this.mActivityClassBlackList.contains(paramActivityInfo.name)))
      {
        if (DEBUG) {
          myLog("# canActivityGo # ret=false callingPackage=" + paramString + ",aInfo=" + paramActivityInfo + ",name=" + paramActivityInfo.name + " # blacklist");
        }
        return false;
      }
      if (localOnePlusAppBootInfo.getBootFlag() != 1) {
        localOnePlusAppBootInfo.setBootFlag(1);
      }
      updateNotificationListener(paramActivityInfo.packageName);
      mLastActivityPkg = mCurrentActivityPkg;
      mCurrentActivityPkg = paramActivityInfo.packageName;
    }
    if ((!DEBUG) && (1 != 0)) {
      return true;
    }
    myLog("# canActivityGo # ret=" + true + ", aInfo=" + paramActivityInfo + ", callingPackage" + paramString);
    return true;
  }
  
  public boolean canInstrumentationGo(ComponentName paramComponentName, int paramInt1, int paramInt2)
  {
    if ((!IN_USING) || (!mAppBootSwitch)) {
      return true;
    }
    boolean bool = true;
    if (paramInt2 > 10000) {
      bool = false;
    }
    if ((bool) && (paramComponentName != null))
    {
      Object localObject = paramComponentName.getPackageName();
      if (localObject != null)
      {
        localObject = getAppBootInfo((String)localObject);
        ((OnePlusAppBootInfo)localObject).setAction(1);
        ((OnePlusAppBootInfo)localObject).setBootFlag(1);
      }
    }
    if (DEBUG) {
      myLog("canInstrumentationGo # className=" + paramComponentName + ", calllingPid=" + paramInt1 + ", callingUid=" + paramInt2);
    }
    return bool;
  }
  
  public boolean canNotificationListenerServiceGo(ComponentName paramComponentName)
  {
    if ((!IN_USING) || (!mAppBootSwitch)) {
      return true;
    }
    boolean bool2 = true;
    String str = paramComponentName.getPackageName();
    if (str == null) {
      return true;
    }
    OnePlusAppBootInfo localOnePlusAppBootInfo = getAppBootInfo(str);
    boolean bool1 = bool2;
    if (localOnePlusAppBootInfo.getBootFlag() != 1)
    {
      bool1 = bool2;
      if (localOnePlusAppBootInfo.getAction() == 2) {
        bool1 = false;
      }
    }
    if (!bool1) {}
    synchronized (this.mNotiListenerPkgSet)
    {
      this.mNotiListenerPkgSet.add(str);
      if (DEBUG) {
        myLog("# canNotificationListenerServiceGo # ret=" + bool1 + " # abi=" + localOnePlusAppBootInfo + " # compName=" + paramComponentName);
      }
      return bool1;
    }
  }
  
  public boolean canProcGo(ProcessRecord paramProcessRecord, String paramString)
  {
    if ((!IN_USING) || (!mAppBootSwitch)) {
      return true;
    }
    boolean bool1 = true;
    paramProcessRecord = paramProcessRecord.info.packageName;
    OnePlusAppBootInfo localOnePlusAppBootInfo = getAppBootInfo(paramProcessRecord);
    label58:
    int i;
    boolean bool2;
    if (localOnePlusAppBootInfo.getBootFlag() == 1)
    {
      bool1 = true;
      if ((bool1) && (!"embryo".equals(paramString))) {
        break label187;
      }
      if (bool1) {
        updateHugePowerPackage(paramProcessRecord, false, 4);
      }
      i = SystemProperties.getInt("persist.sys.appboot.allow", 0);
      if (i != 1) {
        break label205;
      }
      bool2 = true;
      bool1 = bool2;
      if (DEBUG)
      {
        myLog("# canProcGo # pkgName= " + paramProcessRecord + " # force can GO");
        bool1 = bool2;
      }
    }
    for (;;)
    {
      if ((DEBUG) || (!bool1)) {
        break label259;
      }
      return bool1;
      if ((paramString == null) || ("activity".equals(paramString)) || ("embryo".equals(paramString)))
      {
        bool1 = true;
        break;
      }
      if (localOnePlusAppBootInfo.getAction() != 2) {
        break;
      }
      bool1 = false;
      break;
      label187:
      if (localOnePlusAppBootInfo.getBootFlag() == 1) {
        break label58;
      }
      localOnePlusAppBootInfo.setBootFlag(1);
      break label58;
      label205:
      if (i == 2)
      {
        bool2 = false;
        bool1 = bool2;
        if (DEBUG)
        {
          myLog("# canProcGo # pkgName= " + paramProcessRecord + " # force can NOT GO");
          bool1 = bool2;
        }
      }
    }
    label259:
    myLog("# canProcGo # ret=" + bool1 + " # abi=" + localOnePlusAppBootInfo);
    return bool1;
  }
  
  public boolean canProviderGo(ContentProviderRecord paramContentProviderRecord, ProcessRecord paramProcessRecord)
  {
    if ((!IN_USING) || (!mAppBootSwitch)) {
      return true;
    }
    boolean bool2 = true;
    String str2 = null;
    if ((paramContentProviderRecord != null) && (paramContentProviderRecord.appInfo != null) && (paramProcessRecord != null))
    {
      String str1 = str2;
      if (paramProcessRecord != null)
      {
        str1 = str2;
        if (paramProcessRecord.info != null) {
          str1 = paramProcessRecord.info.packageName;
        }
      }
      str2 = paramContentProviderRecord.appInfo.packageName;
      OnePlusAppBootInfo localOnePlusAppBootInfo = getAppBootInfo(str2);
      if (DEBUG) {
        myLog("# canProviderGo # " + str1 + " calling " + str2);
      }
      boolean bool1;
      if ((str1 == null) || (str2 == null) || (str1.equals("android")))
      {
        if (localOnePlusAppBootInfo.getBootFlag() == 1) {
          break label266;
        }
        bool1 = bool2;
        if (localOnePlusAppBootInfo.getAction() == 2) {
          bool1 = false;
        }
      }
      label266:
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                if (DEBUG) {
                  myLog("# canProviderGo # ret=" + bool1 + ", callerApp=" + paramProcessRecord + ", cpr=" + paramContentProviderRecord + " # abi=" + localOnePlusAppBootInfo);
                }
                if (bool1) {
                  localOnePlusAppBootInfo.setBootFlag(1);
                }
                return bool1;
                if (str1.equals(str2)) {
                  break;
                }
                localOnePlusAppBootInfo.addCallerPackage(str1);
                getAppBootInfo(str1).addCalleePackage(str2);
                break;
                bool1 = bool2;
              } while (!BLACKLIST_ENABLE);
              bool1 = bool2;
            } while (!localOnePlusAppBootInfo.getBlackListEnableFlag());
            bool1 = bool2;
          } while (localOnePlusAppBootInfo.getAction() == 1);
          bool1 = bool2;
        } while (paramContentProviderRecord.appInfo.className == null);
        bool1 = bool2;
      } while (!this.mProviderClassBlackList.contains(paramContentProviderRecord.appInfo.className));
      Slog.e("OnePlusAppBootManager", "# canProviderGo # ret=false, cpr=" + paramContentProviderRecord + " # blackprovider");
      return false;
    }
    Slog.e("OnePlusAppBootManager", "# canProviderGo # Exception: ret=" + true + ", cpr=" + paramContentProviderRecord + ", callerApp=" + paramProcessRecord);
    return true;
  }
  
  public boolean canReceiverGo(ApplicationInfo paramApplicationInfo, BroadcastRecord paramBroadcastRecord)
  {
    if ((!IN_USING) || (!mAppBootSwitch)) {
      return true;
    }
    boolean bool1 = false;
    OnePlusAppBootInfo localOnePlusAppBootInfo = getAppBootInfo(paramApplicationInfo.packageName);
    if (SystemProperties.getInt("persist.sys.appboot.allow", 0) == 1) {
      return true;
    }
    if ((paramBroadcastRecord != null) && (paramBroadcastRecord.intent != null) && (paramBroadcastRecord.intent.getAction() != null))
    {
      if (this.mWidgetBroadcastActionList.contains(paramBroadcastRecord.intent.getAction()))
      {
        if (localOnePlusAppBootInfo.getBootFlag() != 1) {
          localOnePlusAppBootInfo.setBootFlag(1);
        }
        if (localOnePlusAppBootInfo.getAction() != 1) {
          localOnePlusAppBootInfo.setAction(1);
        }
        schedulePersistAppBootInfo(10000L);
        if (DEBUG) {
          myLog("# canReceiverGo # ret=true, info= " + paramApplicationInfo + " # r=" + paramBroadcastRecord + " # widget intent : " + localOnePlusAppBootInfo);
        }
        return true;
      }
      if (this.mBroadcastIntentActionWhiteList.contains(paramBroadcastRecord.intent.getAction()))
      {
        if (localOnePlusAppBootInfo.getBootFlag() != 1) {
          localOnePlusAppBootInfo.setBootFlag(1);
        }
        schedulePersistAppBootInfo(10000L);
        if (DEBUG) {
          myLog("# canReceiverGo # ret=true, info= " + paramApplicationInfo + " # r=" + paramBroadcastRecord + " # white intent");
        }
        return true;
      }
    }
    boolean bool2;
    if (localOnePlusAppBootInfo.getBootFlag() != 1) {
      if (localOnePlusAppBootInfo.getAction() == 1)
      {
        localOnePlusAppBootInfo.setBootFlag(1);
        bool1 = true;
        bool2 = bool1;
        if (bool1)
        {
          localOnePlusAppBootInfo.setBootFlag(1);
          bool2 = bool1;
        }
      }
    }
    for (;;)
    {
      if ((!DEBUG) && (bool2))
      {
        return bool2;
        if (localOnePlusAppBootInfo.getAction() == 2)
        {
          bool2 = false;
          bool1 = bool2;
          if (!DEBUG) {
            break;
          }
          myLog("# canReceiverGo # ret=" + false + ", info= " + paramApplicationInfo + " # r=" + paramBroadcastRecord + " # blacklist");
          bool1 = bool2;
          break;
        }
        if (paramBroadcastRecord.callingUid < 10000)
        {
          bool1 = true;
          break;
        }
        if ((paramApplicationInfo.flags & 0x81) != 0)
        {
          bool1 = true;
          break;
        }
        if (paramBroadcastRecord.callerApp != null)
        {
          if ((paramBroadcastRecord.callerApp.info.uid >= 10000) && ((paramBroadcastRecord.callerApp.info.flags & 0x81) == 0)) {
            break;
          }
          bool1 = true;
          break;
        }
        bool1 = false;
        break;
        boolean bool3 = true;
        bool1 = true;
        bool2 = bool1;
        if (BLACKLIST_ENABLE)
        {
          bool2 = bool1;
          if (localOnePlusAppBootInfo.getBlackListEnableFlag())
          {
            bool2 = bool1;
            if (localOnePlusAppBootInfo.getAction() != 1)
            {
              bool2 = bool1;
              if (paramBroadcastRecord != null)
              {
                bool2 = bool1;
                if (paramBroadcastRecord.intent != null)
                {
                  String str = paramBroadcastRecord.intent.getAction();
                  if ((str != null) && (this.mBroadcastIntentActionBlackList.contains(str)))
                  {
                    if (DEBUG) {
                      myLog("# canReceiverGo # ret=false comp " + paramBroadcastRecord.curComponent + " # black-action");
                    }
                    return false;
                  }
                  bool2 = bool1;
                  if (paramBroadcastRecord.curComponent != null)
                  {
                    paramApplicationInfo = paramBroadcastRecord.curComponent.getClassName();
                    bool1 = bool3;
                    if (paramApplicationInfo != null)
                    {
                      bool1 = bool3;
                      if (this.mBroadcastIntentClassBlackList.contains(paramApplicationInfo))
                      {
                        bool2 = false;
                        bool1 = bool2;
                        if (DEBUG)
                        {
                          myLog("# canReceiverGo # ret=false comp " + paramBroadcastRecord.curComponent + " # blackclass");
                          bool1 = bool2;
                        }
                      }
                    }
                    return bool1;
                  }
                }
              }
            }
          }
        }
      }
    }
    myLog("# canReceiverGo # ret=" + bool2 + ", info= " + paramApplicationInfo + " # r=" + paramBroadcastRecord + " # abi=" + localOnePlusAppBootInfo);
    return bool2;
  }
  
  public boolean canServiceGo(ProcessRecord paramProcessRecord, Intent paramIntent, ServiceRecord paramServiceRecord, int paramInt, String paramString)
  {
    if ((!IN_USING) || (!mAppBootSwitch)) {
      return true;
    }
    String str1 = null;
    String str2 = str1;
    if (paramServiceRecord != null)
    {
      str2 = str1;
      if (paramServiceRecord.name != null) {
        str2 = paramServiceRecord.name.getPackageName();
      }
    }
    if (str2 == null) {
      return true;
    }
    boolean bool1 = false;
    OnePlusAppBootInfo localOnePlusAppBootInfo = getAppBootInfo(str2);
    if (SystemProperties.getInt("persist.sys.appboot.allow", 0) == 1) {
      return true;
    }
    String str3 = paramIntent.getAction();
    ComponentName localComponentName = paramServiceRecord.name;
    str1 = null;
    if ((paramProcessRecord != null) && (paramProcessRecord.info != null)) {
      str1 = paramProcessRecord.info.packageName;
    }
    for (;;)
    {
      if (DEBUG) {
        myLog("# canServiceGo # " + str1 + " calling " + str2);
      }
      if ((str1 == null) || (str2 == null)) {
        break label455;
      }
      if (!str1.equals("android")) {
        break label428;
      }
      if (localOnePlusAppBootInfo.getBootFlag() == 1) {
        break label390;
      }
      if (localOnePlusAppBootInfo.getAction() != 2) {
        break label384;
      }
      if (!mSyncServiceClassList.contains(localComponentName.flattenToShortString())) {
        break;
      }
      if (DEBUG) {
        myLog("# canServiceGo # ret=false pkgName " + str2 + " # SyncAdapter");
      }
      return false;
      if (paramString != null) {
        str1 = paramString;
      }
    }
    if ((BLACKLIST_ENABLE) && (localOnePlusAppBootInfo.getBlackListEnableFlag()))
    {
      if (localComponentName != null)
      {
        paramProcessRecord = localComponentName.getClassName();
        if ((paramProcessRecord != null) && (this.mServiceClassBlackList.contains(paramProcessRecord)))
        {
          if (DEBUG) {
            myLog("# canServiceGo # ret=false r " + paramServiceRecord + " # blackclass");
          }
          return false;
        }
      }
      if ((str3 != null) && (this.mServiceActionBlackList.contains(str3)))
      {
        if (DEBUG) {
          myLog("# canServiceGo # ret=false intent " + paramIntent + " # black-action");
        }
        return false;
      }
    }
    label384:
    localOnePlusAppBootInfo.setBootFlag(1);
    label390:
    if (DEBUG) {
      myLog("# canServiceGo # ret=true pkgName " + str2 + " # android call");
    }
    return true;
    label428:
    if (!str1.equals(str2))
    {
      localOnePlusAppBootInfo.addCallerPackage(str1);
      getAppBootInfo(str1).addCalleePackage(str2);
    }
    label455:
    boolean bool2;
    if (localOnePlusAppBootInfo.getBootFlag() != 1)
    {
      if ((str3 != null) && (this.mServiceActionWhiteList.contains(str3)))
      {
        localOnePlusAppBootInfo.setBootFlag(1);
        if (DEBUG) {
          myLog("# canServiceGo # ret=" + true + " pkgName " + str2 + " # white-action");
        }
        return true;
      }
      if (localOnePlusAppBootInfo.getAction() == 1)
      {
        localOnePlusAppBootInfo.setBootFlag(1);
        bool1 = true;
        bool2 = bool1;
        if (bool1)
        {
          localOnePlusAppBootInfo.setBootFlag(1);
          bool2 = bool1;
        }
        label572:
        bool1 = bool2;
        if (bool2) {
          break label1504;
        }
        bool1 = bool2;
        if (str1 == null) {
          break label1504;
        }
        if (!str1.equals(mCurrentActivityPkg))
        {
          bool1 = bool2;
          if (!str1.equals(mLastActivityPkg)) {
            break label1504;
          }
        }
        if (localComponentName == null) {
          break label1358;
        }
        paramString = localComponentName.flattenToString() + "#";
        label647:
        if (paramProcessRecord == null) {
          break label1384;
        }
      }
    }
    label1082:
    label1102:
    label1358:
    label1384:
    for (paramProcessRecord = paramString + paramProcessRecord.info.uid + "#" + paramProcessRecord.processName;; paramProcessRecord = paramString + str1)
    {
      bool2 = localOnePlusAppBootInfo.updateLastCallingServiceBootPolicy(paramProcessRecord);
      if ((!bool2) || (!BLACKLIST_ENABLE) || (!localOnePlusAppBootInfo.getBlackListEnableFlag()) || (localOnePlusAppBootInfo.getAction() == 1)) {
        break label1462;
      }
      if (localComponentName == null) {
        break label1408;
      }
      paramProcessRecord = localComponentName.getClassName();
      if ((paramProcessRecord == null) || (!this.mServiceClassBlackList.contains(paramProcessRecord))) {
        break label1408;
      }
      if (DEBUG) {
        myLog("# canServiceGo # ret=false r " + paramServiceRecord + " # blackclass");
      }
      return false;
      if (localOnePlusAppBootInfo.getAction() == 2)
      {
        bool1 = false;
        if (DEBUG) {
          myLog("# canServiceGo # ret=" + false + " pkgName " + str2 + " # blacklist");
        }
        bool2 = bool1;
        if (paramInt > 0)
        {
          bool2 = bool1;
          if (UserHandle.getAppId(paramInt) == localOnePlusAppBootInfo.getUid())
          {
            bool1 = true;
            bool2 = bool1;
            if (DEBUG)
            {
              myLog("# canServiceGo # ret=" + true + " pkgName " + str2 + " # blacklist # callingUid");
              bool2 = bool1;
            }
          }
        }
        bool1 = bool2;
        if (bool2) {
          break;
        }
        bool1 = bool2;
        if (paramProcessRecord == null) {
          break;
        }
        bool1 = bool2;
        if (paramProcessRecord.info.uid != localOnePlusAppBootInfo.getUid()) {
          break;
        }
        bool2 = true;
        bool1 = bool2;
        if (!DEBUG) {
          break;
        }
        myLog("# canServiceGo # ret=" + true + " pkgName " + str2 + " # blacklist # proc uid");
        bool1 = bool2;
        break;
      }
      bool2 = bool1;
      if (paramInt > 0)
      {
        if (paramInt >= 10000) {
          break label1082;
        }
        bool2 = true;
      }
      for (;;)
      {
        bool1 = bool2;
        if (bool2) {
          break;
        }
        bool1 = bool2;
        if (paramProcessRecord == null) {
          break;
        }
        if (paramProcessRecord.info.uid >= 10000) {
          break label1102;
        }
        bool1 = true;
        break;
        bool2 = bool1;
        if (paramInt == localOnePlusAppBootInfo.getUid()) {
          bool2 = true;
        }
      }
      bool1 = bool2;
      if (paramProcessRecord.info.uid != localOnePlusAppBootInfo.getUid()) {
        break;
      }
      bool1 = true;
      break;
      bool1 = true;
      bool2 = bool1;
      if (!BLACKLIST_ENABLE) {
        break label572;
      }
      bool2 = bool1;
      if (!localOnePlusAppBootInfo.getBlackListEnableFlag()) {
        break label572;
      }
      bool2 = bool1;
      if (localOnePlusAppBootInfo.getAction() == 1) {
        break label572;
      }
      if (localComponentName != null)
      {
        paramString = localComponentName.getClassName();
        if (paramString != null)
        {
          if ((this.mCurAppServiceClassWhiteList.contains(paramString)) && (str2 != null) && (str2.equals(mCurrentActivityPkg)))
          {
            if (DEBUG) {
              myLog("# canServiceGo # ret=true r " + paramServiceRecord + " # temp-white-class");
            }
            return true;
          }
          if (this.mServiceClassBlackList.contains(paramString))
          {
            if (DEBUG) {
              myLog("# canServiceGo # ret=false r " + paramServiceRecord + " # blackclass");
            }
            return false;
          }
        }
      }
      bool2 = bool1;
      if (str3 == null) {
        break label572;
      }
      bool2 = bool1;
      if (!this.mServiceActionBlackList.contains(str3)) {
        break label572;
      }
      if (DEBUG) {
        myLog("# canServiceGo # ret=false intent " + paramIntent + " # black-action");
      }
      return false;
      paramString = str2 + "#";
      break label647;
    }
    label1408:
    if ((str3 != null) && (this.mServiceActionBlackList.contains(str3)))
    {
      if (DEBUG) {
        myLog("# canServiceGo # ret=false intent " + paramIntent + " # black-action");
      }
      return false;
    }
    label1462:
    bool1 = bool2;
    if (bool2)
    {
      localOnePlusAppBootInfo.setBootFlag(1);
      paramProcessRecord = Message.obtain(this.mAppbootHandler, 2, str2);
      this.mAppbootHandler.sendMessageDelayed(paramProcessRecord, 30000L);
      bool1 = bool2;
    }
    label1504:
    if ((!DEBUG) && (bool1)) {
      return bool1;
    }
    myLog("# canServiceGo # ret=" + bool1 + " # abi=" + localOnePlusAppBootInfo + " # mCurrentActivityPkg=" + mCurrentActivityPkg + " mLastActivityPkg=" + mLastActivityPkg);
    return bool1;
  }
  
  public List<ActivityManager.AppBootMode> getAllAppBootModes(int paramInt)
  {
    if (!IN_USING) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    synchronized (mABILock)
    {
      if (mPkgMap == null)
      {
        paramInt = mPkgMap.size();
        if (paramInt < 1) {
          return null;
        }
      }
      Iterator localIterator = mPkgMap.values().iterator();
      while (localIterator.hasNext())
      {
        OnePlusAppBootInfo localOnePlusAppBootInfo = (OnePlusAppBootInfo)localIterator.next();
        if ((localOnePlusAppBootInfo.getPkgFlag() & 0x4) != 0) {
          localArrayList.add(new ActivityManager.AppBootMode(localOnePlusAppBootInfo.getPkgName(), localOnePlusAppBootInfo.getAction(), localOnePlusAppBootInfo.getCallerPackageSet().size()));
        }
      }
    }
    return localList;
  }
  
  public int getAppBootMode(String paramString)
  {
    if (!IN_USING) {
      return -1;
    }
    return getAppBootInfo(paramString).getAction();
  }
  
  public boolean getAppBootState()
  {
    if (!IN_USING) {
      return false;
    }
    return mAppBootSwitch;
  }
  
  ArrayList<String> getArrayListFromString(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    if ((paramString == null) || (paramString.length() < 3)) {
      return null;
    }
    paramString = paramString.split(",");
    if (paramString.length == 1) {
      if (paramString[0].length() > 3)
      {
        paramString = paramString[0].substring(1, paramString[0].length() - 1);
        if (DEBUG) {
          myLog("ret=" + paramString);
        }
        localArrayList.add(paramString.trim());
      }
    }
    while (DEBUG)
    {
      paramString = localArrayList.iterator();
      String str;
      while (paramString.hasNext())
      {
        str = (String)paramString.next();
        myLog("s=" + str);
      }
      if (paramString.length > 1)
      {
        str = paramString[0].substring(1);
        if (DEBUG) {
          myLog("ret=" + str);
        }
        localArrayList.add(str.trim());
        str = paramString[(paramString.length - 1)];
        str = str.substring(0, str.length() - 1);
        if (DEBUG) {
          myLog("ret=" + str);
        }
        localArrayList.add(str.trim());
        int i = 1;
        while (i < paramString.length - 1)
        {
          if (DEBUG) {
            myLog("ret=" + paramString[i]);
          }
          localArrayList.add(paramString[i].trim());
          i += 1;
        }
      }
    }
    return localArrayList;
  }
  
  public String[] getCalleePackageArray(String paramString)
  {
    if (!IN_USING) {
      return null;
    }
    if ((paramString == null) || (paramString.length() < 1)) {
      return null;
    }
    paramString = getAppBootInfo(paramString).getCalleePackageSet();
    return (String[])paramString.toArray(new String[paramString.size()]);
  }
  
  public String[] getCallerPackageArray(String paramString)
  {
    if (!IN_USING) {
      return null;
    }
    if ((paramString == null) || (paramString.length() < 1)) {
      return null;
    }
    HashSet localHashSet = getAppBootInfo(paramString).getCallerPackageSet();
    if (DEBUG) {
      myLog("getCallerPackageArray # packageName=" + paramString + ", pkgSet=" + localHashSet);
    }
    return (String[])localHashSet.toArray(new String[localHashSet.size()]);
  }
  
  public void initEnv(ActivityManagerService paramActivityManagerService, Context paramContext)
  {
    if (!IN_USING) {
      return;
    }
    this.mAms = paramActivityManagerService;
    this.mContext = paramContext;
    this.mAppBootThread = new HandlerThread("AppBootThread");
    this.mAppBootThread.start();
    this.mAppbootHandler = new AppBootProcessHander(this.mAppBootThread.getLooper());
    if (paramContext != null)
    {
      registerPackageReceiver();
      registerGeneralReceiver();
    }
    initOnlineConfig();
    this.mSettingsObserver = new SettingsObserver(this.mAppbootHandler);
    this.mResolver = this.mContext.getContentResolver();
    this.mResolver.registerContentObserver(Settings.Secure.getUriFor("sms_default_application"), false, this.mSettingsObserver, -1);
    this.mResolver.registerContentObserver(Settings.Secure.getUriFor("dialer_default_application"), false, this.mSettingsObserver, -1);
    updateSettingsObserver("init");
    if ((!mRegion.equalsIgnoreCase("CN")) && ((mGlobalFlags & 0x1) == 0))
    {
      boolean bool = mAppBootSwitch;
      mAppBootSwitch = false;
      if (DEBUG) {
        Slog.i("OnePlusAppBootManager", "initEnv # set from " + bool + " -> " + mAppBootSwitch);
      }
    }
  }
  
  public void initOnlineConfig()
  {
    this.mAppBootConfigObserver = new ConfigObserver(this.mContext, this.mAppbootHandler, new AppBootConfigUpdater(), APPBOOT_CONFIG_NAME);
    this.mAppBootConfigObserver.register();
    this.mAppbootHandler.sendMessage(this.mAppbootHandler.obtainMessage(3));
  }
  
  public void initPackages(ArrayMap<String, PackageParser.Package> paramArrayMap)
  {
    if (!IN_USING) {
      return;
    }
    if (DEBUG) {
      myLog(" initPackages # ");
    }
    if ((mPkgMap != null) && (mPkgMap.size() > 0))
    {
      if (DEBUG) {
        myLog(" initPackages # mPkgMap size > 0 , return");
      }
      return;
    }
    paramArrayMap = paramArrayMap.values().iterator();
    while (paramArrayMap.hasNext())
    {
      PackageParser.Package localPackage = (PackageParser.Package)paramArrayMap.next();
      OnePlusAppBootInfo localOnePlusAppBootInfo = new OnePlusAppBootInfo(localPackage.applicationInfo.packageName);
      localOnePlusAppBootInfo.setPPPackage(localPackage);
      mPkgMap.put(localPackage.applicationInfo.packageName, localOnePlusAppBootInfo);
    }
    readXml_specialList();
    readXml_appboot();
  }
  
  public int readXml_appboot()
  {
    if (!IN_USING) {
      return -4;
    }
    if (DEBUG) {
      myLog("# readXml_appboot");
    }
    int j = 0;
    Object localObject1 = new File("/data/system/appboot/appboot.xml");
    if (!((File)localObject1).exists())
    {
      if (DEBUG) {
        myLog("# readXml_appboot # file not exists");
      }
      return -1;
    }
    Object localObject2 = DocumentBuilderFactory.newInstance();
    for (;;)
    {
      Object localObject3;
      boolean bool;
      int i;
      try
      {
        localObject1 = ((DocumentBuilderFactory)localObject2).newDocumentBuilder().parse((File)localObject1);
        localObject3 = ((Document)localObject1).getElementsByTagName("appboot").item(0).getAttributes();
        localObject2 = ((NamedNodeMap)localObject3).getNamedItem("version").getNodeValue();
        APPBOOT_VERSION = (String)localObject2;
        localObject3 = ((NamedNodeMap)localObject3).getNamedItem("switch").getNodeValue();
        if (DEBUG) {
          myLog(" readXml_appboot # version " + (String)localObject2 + " switch " + (String)localObject3);
        }
        if (!((String)localObject3).equals("0")) {
          break label542;
        }
        bool = false;
        mAppBootSwitch = bool;
        localObject1 = ((Document)localObject1).getElementsByTagName("pkg");
        if (localObject1 != null) {
          break label548;
        }
        Slog.e("OnePlusAppBootManager", "# readXml_appboot # error # nl = null");
        return -2;
      }
      catch (Exception localException)
      {
        Object localObject4;
        String str1;
        String str2;
        Slog.e("OnePlusAppBootManager", "# readXml_appboot # parse error [" + localException.getMessage() + "]");
        localException.printStackTrace();
        return -3;
      }
      if (i < ((NodeList)localObject1).getLength())
      {
        localObject4 = ((NodeList)localObject1).item(i).getAttributes();
        localObject3 = ((NamedNodeMap)localObject4).getNamedItem("package").getNodeValue();
        str1 = ((NamedNodeMap)localObject4).getNamedItem("flag").getNodeValue();
        str2 = ((NamedNodeMap)localObject4).getNamedItem("action").getNodeValue();
        localObject2 = ((NamedNodeMap)localObject4).getNamedItem("caller").getNodeValue();
        localObject4 = ((NamedNodeMap)localObject4).getNamedItem("callee").getNodeValue();
        if (DEBUG) {
          myLog("package " + (String)localObject3 + " # flag " + str1 + " # action " + str2 + " # callerStr=" + (String)localObject2 + " # calleeStr=" + (String)localObject4);
        }
        int k = Integer.parseInt(str2);
        localObject3 = getAppBootInfo((String)localObject3);
        updateLinkedPkgsInfo((OnePlusAppBootInfo)localObject3, (String)localObject2, (String)localObject4);
        if (k == 1)
        {
          ((OnePlusAppBootInfo)localObject3).setAction(k);
          ((OnePlusAppBootInfo)localObject3).setBootFlag(1);
        }
        else if (k == 2)
        {
          ((OnePlusAppBootInfo)localObject3).setAction(k);
          ((OnePlusAppBootInfo)localObject3).setBootFlag(0);
        }
        else if (k == 0)
        {
          if (((OnePlusAppBootInfo)localObject3).getBootFlag() == 1) {
            ((OnePlusAppBootInfo)localObject3).setAction(1);
          } else {
            ((OnePlusAppBootInfo)localObject3).setAction(2);
          }
        }
      }
      else
      {
        return j;
        label542:
        bool = true;
        continue;
        label548:
        i = 0;
        continue;
      }
      j += 1;
      i += 1;
    }
  }
  
  public int readXml_prelist()
  {
    j = 0;
    Object localObject1 = new File("/system/etc/presetlist.xml");
    if (!((File)localObject1).exists())
    {
      if (DEBUG) {
        myLog("# readXml_prelist # file not exists");
      }
      return -1;
    }
    Object localObject2 = DocumentBuilderFactory.newInstance();
    try
    {
      localObject1 = ((DocumentBuilderFactory)localObject2).newDocumentBuilder().parse((File)localObject1);
      Object localObject3 = ((Document)localObject1).getElementsByTagName("prelist").item(0).getAttributes();
      localObject2 = ((NamedNodeMap)localObject3).getNamedItem("version").getNodeValue();
      localObject3 = ((NamedNodeMap)localObject3).getNamedItem("switch").getNodeValue();
      if (DEBUG) {
        myLog(" readXml_prelist # version " + (String)localObject2 + " switch " + (String)localObject3);
      }
      localObject1 = ((Document)localObject1).getElementsByTagName("pkg");
      int i = 0;
      while (i < ((NodeList)localObject1).getLength())
      {
        Object localObject4 = ((NodeList)localObject1).item(i).getAttributes();
        localObject2 = ((NamedNodeMap)localObject4).getNamedItem("package").getNodeValue();
        localObject3 = ((NamedNodeMap)localObject4).getNamedItem("flag").getNodeValue();
        localObject4 = ((NamedNodeMap)localObject4).getNamedItem("action").getNodeValue();
        if (DEBUG) {
          myLog("package " + (String)localObject2 + " # flag " + (String)localObject3 + " # action " + (String)localObject4);
        }
        mPrePkgMap.put(localObject2, new PrePkgInfo((String)localObject2, Integer.parseInt((String)localObject3), Integer.parseInt((String)localObject4)));
        j += 1;
        i += 1;
      }
      return j;
    }
    catch (Exception localException)
    {
      Slog.e("OnePlusAppBootManager", "# readXml_prelist # parse error [" + localException.toString() + "]");
      return -2;
    }
  }
  
  public int setAppBootMode(String paramString, int paramInt)
  {
    if (!IN_USING) {
      return 0;
    }
    OnePlusAppBootInfo localOnePlusAppBootInfo = getAppBootInfo(paramString);
    switch (paramInt)
    {
    }
    for (int i = -10;; i = 1)
    {
      if (DEBUG) {
        myLog("# setAppBootMode # packageName=" + paramString + ", mode=" + paramInt + ", ret=" + i);
      }
      if (i > 0) {
        schedulePersistAppBootInfo(2000L);
      }
      return i;
      localOnePlusAppBootInfo.setAction(paramInt);
    }
  }
  
  public void setAppBootState(boolean paramBoolean)
  {
    if (!IN_USING) {
      return;
    }
    Slog.i("OnePlusAppBootManager", "# setAppBootState # on=" + paramBoolean + ",mAppBootSwitch=" + mAppBootSwitch);
    if ((mGlobalFlags & 0x1) == 0)
    {
      mGlobalFlags |= 0x1;
      SystemProperties.set("persist.sys.appboot.flags", mGlobalFlags + "");
    }
    if (mAppBootSwitch != paramBoolean)
    {
      mAppBootSwitch = paramBoolean;
      schedulePersistAppBootInfo(2000L);
    }
  }
  
  public void setCurrentIME(Intent paramIntent)
  {
    mCurrentIME = getPkgNameFromIntent(paramIntent);
    if (DEBUG) {
      myLog("# setCurrentIME # mCurrentIME=" + mCurrentIME);
    }
    if (mCurrentIME != null)
    {
      paramIntent = getAppBootInfo(mCurrentIME);
      paramIntent.setAction(1);
      paramIntent.setBootFlag(1);
    }
  }
  
  public void setCurrentWallpaperPackage(ComponentName paramComponentName)
  {
    if (paramComponentName == null) {
      return;
    }
    mCurrentWallPaperPkg = paramComponentName.getPackageName();
    if (DEBUG) {
      myLog("# setCurrentWallpaperPackage # " + paramComponentName);
    }
    if (mCurrentWallPaperPkg != null) {
      getAppBootInfo(mCurrentWallPaperPkg).setBootFlag(1);
    }
  }
  
  public boolean skipBroadcast(Intent paramIntent, String paramString, int paramInt1, int paramInt2, ProcessRecord paramProcessRecord)
  {
    if (DEBUG) {
      myLog("# skipBroadcast # intent=" + paramIntent + ", callerPackage = " + paramString + ",callingPid=" + paramInt1 + ",callingUid=" + paramInt2 + ",callerApp=" + paramProcessRecord);
    }
    if (paramInt2 < 10000) {
      return false;
    }
    if (paramIntent == null) {
      return false;
    }
    paramString = paramIntent.getAction();
    if ((paramString != null) && (this.mBroadcastIntentActionBlackList.contains(paramString)))
    {
      paramString = getPkgNameFromIntent(paramIntent);
      if ((paramString != null) && (getAppBootInfo(paramString).getAction() != 1))
      {
        Slog.e("OnePlusAppBootManager", "# skipBroadcast # black action : " + paramIntent);
        return true;
      }
    }
    return false;
  }
  
  public void trackProcess(boolean paramBoolean, ProcessRecord paramProcessRecord, String paramString)
  {
    if (DEBUG) {
      Slog.i("OnePlusAppBootManager", "PKG_TRACK # trackProcess # add=" + paramBoolean + ", reason=" + paramString + ", proc=" + paramProcessRecord);
    }
    if ((paramProcessRecord != null) && (paramProcessRecord.info != null)) {}
    for (paramString = paramProcessRecord.info.packageName; paramString == null; paramString = null) {
      return;
    }
    int i = 0;
    int j = 0;
    synchronized (mProcMap)
    {
      HashSet localHashSet2 = (HashSet)mProcMap.get(paramString);
      HashSet localHashSet1 = localHashSet2;
      if (localHashSet2 == null)
      {
        localHashSet2 = new HashSet();
        mProcMap.put(paramString, localHashSet2);
        localHashSet1 = localHashSet2;
        if (!paramBoolean)
        {
          Slog.e("OnePlusAppBootManager", "PKG_TRACK # Exception # no add when remove ProcessRecord:" + paramProcessRecord);
          localHashSet1 = localHashSet2;
        }
      }
      if (paramBoolean) {
        localHashSet1.add(paramProcessRecord);
      }
      int k;
      do
      {
        if (j != 0) {
          updateAppStopInfo(paramString);
        }
        return;
        j = localHashSet1.size();
        if (j == 0) {
          return;
        }
        localHashSet1.remove(paramProcessRecord);
        k = localHashSet1.size();
        if (k == 0) {
          i = 1;
        }
        j = i;
      } while (!DEBUG);
      paramBoolean = localHashSet1.contains(paramProcessRecord);
      Slog.d("OnePlusAppBootManager", "PKG_TRACK # trackProcess # pkgName=" + paramString + ", contain=" + paramBoolean + ", size=" + k);
      j = i;
    }
  }
  
  public void updateAccesibilityServiceFlag(String paramString, int paramInt)
  {
    if ((!IN_USING) || (!mAppBootSwitch)) {
      return;
    }
    if ((paramString == null) || (paramString.length() < 1)) {
      return;
    }
    if (DEBUG) {
      myLog("updateAccesibilityServiceFlag # packageName=" + paramString + ", flag=" + paramInt);
    }
    if (paramInt == 1) {
      getAppBootInfo(paramString).setBootFlag(1);
    }
  }
  
  public void updateAppStopInfo(String paramString)
  {
    if (!IN_USING) {
      return;
    }
    OnePlusAppBootInfo localOnePlusAppBootInfo = getAppBootInfo(paramString);
    if ((mCurrentWallPaperPkg != null) && (mCurrentWallPaperPkg.equals(paramString)))
    {
      if (DEBUG) {
        myLog("# updateAppStopInfo # go pkgName=" + paramString + " # wallpaper");
      }
      return;
    }
    if ((this.mDefaultSMSPackage != null) && (this.mDefaultSMSPackage.equals(paramString)))
    {
      if (DEBUG) {
        myLog("# updateAppStopInfo # go pkgName=" + paramString + " # smspkg");
      }
      return;
    }
    if ((this.mDefaultDailerPackage != null) && (this.mDefaultDailerPackage.equals(paramString)))
    {
      if (DEBUG) {
        myLog("# updateAppStopInfo # go pkgName=" + paramString + " # dailpkg");
      }
      return;
    }
    int i = localOnePlusAppBootInfo.getAction();
    int j = localOnePlusAppBootInfo.getPkgFlag();
    if (i == 1)
    {
      if ((j & 0x2) != 0)
      {
        localOnePlusAppBootInfo.setBootFlag(1);
        if (DEBUG) {
          myLog("# updateAppStopInfo # go pkgName=" + paramString + " # sys-whitelist");
        }
      }
      do
      {
        return;
        localOnePlusAppBootInfo.setBootFlag(0);
      } while (!DEBUG);
      myLog("# updateAppStopInfo # ignore pkgName=" + paramString + " # 3rd-whitelist");
      return;
    }
    if (i == 2)
    {
      localOnePlusAppBootInfo.setBootFlag(0);
      if (DEBUG) {
        myLog("# updateAppStopInfo # ignore pkgName=" + paramString + " # blacklist");
      }
      return;
    }
    if (((j & 0x8) != 0) || ((j & 0x2) != 0)) {}
    do
    {
      do
      {
        localOnePlusAppBootInfo.setBootFlag(1);
        if (DEBUG) {
          myLog("# updateAppStopInfo # go pkgName=" + paramString + " # abi=" + localOnePlusAppBootInfo);
        }
        return;
      } while ((j & 0x20) != 0);
      localOnePlusAppBootInfo.setBootFlag(0);
    } while (!DEBUG);
    myLog("# updateAppStopInfo # ignore pkgName=" + paramString + " # abi=" + localOnePlusAppBootInfo);
  }
  
  public void updateAppStopInfo(String paramString, int paramInt)
  {
    if (!IN_USING) {
      return;
    }
    ActivityManagerService localActivityManagerService = this.mAms;
    if (ActivityManagerService.MY_PID == paramInt) {}
    for (paramInt = 1; paramInt != 0; paramInt = 0)
    {
      if (DEBUG) {
        myLog("# updateAppStopInfo # go pkgName=" + paramString + " # samePid");
      }
      return;
    }
    updateAppStopInfo(paramString);
  }
  
  public void updateBootFlag(ResolveInfo paramResolveInfo, int paramInt)
  {
    if (!IN_USING) {
      return;
    }
    if (paramResolveInfo != null)
    {
      if (paramResolveInfo.activityInfo == null) {
        break label74;
      }
      paramResolveInfo = paramResolveInfo.activityInfo.packageName;
    }
    for (;;)
    {
      paramResolveInfo = getAppBootInfo(paramResolveInfo);
      if (paramResolveInfo.getBootFlag() != paramInt) {
        paramResolveInfo.setBootFlag(paramInt);
      }
      if (DEBUG) {
        myLog("# updateBootFlag # abi=" + paramResolveInfo);
      }
      return;
      label74:
      if (paramResolveInfo.serviceInfo != null)
      {
        paramResolveInfo = paramResolveInfo.serviceInfo.packageName;
      }
      else
      {
        if (paramResolveInfo.providerInfo == null) {
          break;
        }
        paramResolveInfo = paramResolveInfo.providerInfo.packageName;
      }
    }
  }
  
  public void updatePowerFlag(String paramString, int paramInt)
  {
    if (!IN_USING) {
      return;
    }
    if ((0x8000 & paramInt) != 0) {
      updateHugePowerPackage(paramString, true, 2);
    }
  }
  
  /* Error */
  public int writeXml_appboot(HashMap<String, OnePlusAppBootInfo> paramHashMap)
  {
    // Byte code:
    //   0: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   3: ifeq +9 -> 12
    //   6: ldc_w 1529
    //   9: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   12: invokestatic 1533	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
    //   15: astore 5
    //   17: new 1535	java/io/StringWriter
    //   20: dup
    //   21: invokespecial 1536	java/io/StringWriter:<init>	()V
    //   24: astore 4
    //   26: aload 5
    //   28: aload 4
    //   30: invokeinterface 1542 2 0
    //   35: aload 5
    //   37: ldc_w 1544
    //   40: iconst_1
    //   41: invokestatic 1550	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   44: invokeinterface 1554 3 0
    //   49: aload 5
    //   51: ldc_w 1556
    //   54: invokeinterface 1560 2 0
    //   59: pop
    //   60: aload 5
    //   62: ldc -128
    //   64: ldc 125
    //   66: invokeinterface 1564 3 0
    //   71: pop
    //   72: aload 5
    //   74: ldc -128
    //   76: ldc 122
    //   78: getstatic 335	com/android/server/am/OnePlusAppBootManager:APPBOOT_VERSION	Ljava/lang/String;
    //   81: invokeinterface 1568 4 0
    //   86: pop
    //   87: getstatic 297	com/android/server/am/OnePlusAppBootManager:mAppBootSwitch	Z
    //   90: ifeq +408 -> 498
    //   93: ldc_w 333
    //   96: astore_3
    //   97: aload 5
    //   99: ldc -128
    //   101: ldc 119
    //   103: aload_3
    //   104: invokeinterface 1568 4 0
    //   109: pop
    //   110: aload 5
    //   112: ldc_w 1556
    //   115: invokeinterface 1560 2 0
    //   120: pop
    //   121: aload_1
    //   122: monitorenter
    //   123: aload_1
    //   124: invokevirtual 545	java/util/HashMap:values	()Ljava/util/Collection;
    //   127: invokeinterface 504 1 0
    //   132: astore_3
    //   133: aload_3
    //   134: invokeinterface 509 1 0
    //   139: ifeq +366 -> 505
    //   142: aload_3
    //   143: invokeinterface 513 1 0
    //   148: checkcast 16	com/android/server/am/OnePlusAppBootManager$OnePlusAppBootInfo
    //   151: astore 6
    //   153: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   156: ifeq +27 -> 183
    //   159: new 459	java/lang/StringBuilder
    //   162: dup
    //   163: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   166: ldc_w 1570
    //   169: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   172: aload 6
    //   174: invokevirtual 552	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   177: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   180: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   183: aload 6
    //   185: ifnull -52 -> 133
    //   188: aload 5
    //   190: ldc -128
    //   192: ldc -125
    //   194: invokeinterface 1564 3 0
    //   199: pop
    //   200: aload 5
    //   202: ldc -128
    //   204: ldc 116
    //   206: aload 6
    //   208: invokevirtual 1201	com/android/server/am/OnePlusAppBootManager$OnePlusAppBootInfo:getPkgName	()Ljava/lang/String;
    //   211: invokeinterface 1568 4 0
    //   216: pop
    //   217: aload 5
    //   219: ldc -128
    //   221: ldc 113
    //   223: new 459	java/lang/StringBuilder
    //   226: dup
    //   227: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   230: ldc -128
    //   232: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   235: aload 6
    //   237: invokevirtual 568	com/android/server/am/OnePlusAppBootManager$OnePlusAppBootInfo:getPkgFlag	()I
    //   240: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   243: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   246: invokeinterface 1568 4 0
    //   251: pop
    //   252: aload 5
    //   254: ldc -128
    //   256: ldc 104
    //   258: new 459	java/lang/StringBuilder
    //   261: dup
    //   262: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   265: ldc -128
    //   267: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   270: aload 6
    //   272: invokevirtual 1004	com/android/server/am/OnePlusAppBootManager$OnePlusAppBootInfo:getBootFlag	()I
    //   275: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   278: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   281: invokeinterface 1568 4 0
    //   286: pop
    //   287: aload 5
    //   289: ldc -128
    //   291: ldc 101
    //   293: new 459	java/lang/StringBuilder
    //   296: dup
    //   297: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   300: ldc -128
    //   302: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   305: aload 6
    //   307: invokevirtual 565	com/android/server/am/OnePlusAppBootManager$OnePlusAppBootInfo:getAction	()I
    //   310: invokevirtual 536	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   313: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   316: invokeinterface 1568 4 0
    //   321: pop
    //   322: aload 5
    //   324: ldc -128
    //   326: ldc 110
    //   328: aload 6
    //   330: invokevirtual 1573	com/android/server/am/OnePlusAppBootManager$OnePlusAppBootInfo:getCallerPackageSetString	()Ljava/lang/String;
    //   333: invokeinterface 1568 4 0
    //   338: pop
    //   339: aload 5
    //   341: ldc -128
    //   343: ldc 107
    //   345: aload 6
    //   347: invokevirtual 1576	com/android/server/am/OnePlusAppBootManager$OnePlusAppBootInfo:getCalleePackageSetString	()Ljava/lang/String;
    //   350: invokeinterface 1568 4 0
    //   355: pop
    //   356: aload 5
    //   358: ldc -128
    //   360: ldc -125
    //   362: invokeinterface 1579 3 0
    //   367: pop
    //   368: aload 5
    //   370: ldc_w 1556
    //   373: invokeinterface 1560 2 0
    //   378: pop
    //   379: goto -246 -> 133
    //   382: astore 6
    //   384: ldc 92
    //   386: new 459	java/lang/StringBuilder
    //   389: dup
    //   390: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   393: ldc_w 1581
    //   396: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   399: aload 6
    //   401: invokevirtual 596	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   404: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   407: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   410: invokestatic 479	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   413: pop
    //   414: aload 6
    //   416: invokevirtual 599	java/lang/Exception:printStackTrace	()V
    //   419: goto -286 -> 133
    //   422: astore_3
    //   423: aload_1
    //   424: monitorexit
    //   425: aload_3
    //   426: athrow
    //   427: astore_1
    //   428: bipush -11
    //   430: istore_2
    //   431: ldc 92
    //   433: new 459	java/lang/StringBuilder
    //   436: dup
    //   437: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   440: ldc_w 1581
    //   443: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   446: aload_1
    //   447: invokevirtual 596	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   450: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   453: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   456: invokestatic 479	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   459: pop
    //   460: aload_1
    //   461: invokevirtual 599	java/lang/Exception:printStackTrace	()V
    //   464: getstatic 293	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   467: ifeq +29 -> 496
    //   470: new 459	java/lang/StringBuilder
    //   473: dup
    //   474: invokespecial 460	java/lang/StringBuilder:<init>	()V
    //   477: ldc -128
    //   479: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   482: aload 4
    //   484: invokevirtual 1582	java/io/StringWriter:toString	()Ljava/lang/String;
    //   487: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   490: invokevirtual 474	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   493: invokestatic 559	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   496: iload_2
    //   497: ireturn
    //   498: ldc_w 1389
    //   501: astore_3
    //   502: goto -405 -> 97
    //   505: aload_1
    //   506: monitorexit
    //   507: aload 5
    //   509: ldc -128
    //   511: ldc 125
    //   513: invokeinterface 1579 3 0
    //   518: pop
    //   519: aload 5
    //   521: invokeinterface 1585 1 0
    //   526: ldc 34
    //   528: aload 4
    //   530: invokevirtual 1582	java/io/StringWriter:toString	()Ljava/lang/String;
    //   533: invokestatic 1587	com/android/server/am/OnePlusAppBootManager:WriteStringToFile	(Ljava/lang/String;Ljava/lang/String;)I
    //   536: istore_2
    //   537: goto -73 -> 464
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	540	0	this	OnePlusAppBootManager
    //   0	540	1	paramHashMap	HashMap<String, OnePlusAppBootInfo>
    //   430	107	2	i	int
    //   96	47	3	localObject1	Object
    //   422	4	3	localObject2	Object
    //   501	1	3	str	String
    //   24	505	4	localStringWriter	java.io.StringWriter
    //   15	505	5	localXmlSerializer	org.xmlpull.v1.XmlSerializer
    //   151	195	6	localOnePlusAppBootInfo	OnePlusAppBootInfo
    //   382	33	6	localException	Exception
    // Exception table:
    //   from	to	target	type
    //   188	379	382	java/lang/Exception
    //   123	133	422	finally
    //   133	183	422	finally
    //   188	379	422	finally
    //   384	419	422	finally
    //   26	93	427	java/lang/Exception
    //   97	123	427	java/lang/Exception
    //   423	427	427	java/lang/Exception
    //   505	537	427	java/lang/Exception
  }
  
  class AppBootConfigUpdater
    implements ConfigObserver.ConfigUpdater
  {
    AppBootConfigUpdater() {}
    
    public void updateConfig(JSONArray paramJSONArray)
    {
      OnePlusAppBootManager.-wrap7(OnePlusAppBootManager.this, paramJSONArray);
    }
  }
  
  private class AppBootProcessHander
    extends Handler
  {
    public AppBootProcessHander(Looper paramLooper)
    {
      super();
    }
    
    private void handlePkgMessage(Message paramMessage)
    {
      if (OnePlusAppBootManager.DEBUG) {
        OnePlusAppBootManager.myLog("handlePkgMessage # msg=" + paramMessage);
      }
      switch (paramMessage.arg1)
      {
      }
      do
      {
        do
        {
          do
          {
            return;
            OnePlusAppBootManager.-wrap1(OnePlusAppBootManager.this, (String)paramMessage.obj);
          } while (!OnePlusAppBootManager.DEBUG);
          OnePlusAppBootManager.myLog("handlePkgMessage # reinstall " + paramMessage.obj);
          return;
          OnePlusAppBootManager.-wrap6(OnePlusAppBootManager.this, (String)paramMessage.obj);
        } while (!OnePlusAppBootManager.DEBUG);
        OnePlusAppBootManager.myLog("handlePkgMessage # remove " + paramMessage.obj);
        return;
        OnePlusAppBootManager.-wrap1(OnePlusAppBootManager.this, (String)paramMessage.obj);
      } while (!OnePlusAppBootManager.DEBUG);
      OnePlusAppBootManager.myLog("handlePkgMessage # install " + paramMessage.obj);
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = false;
      if (OnePlusAppBootManager.DEBUG) {
        OnePlusAppBootManager.myLog("handleMessage # msg=" + paramMessage);
      }
      switch (paramMessage.what)
      {
      default: 
        handlePkgMessage(paramMessage);
      case 1: 
      case 2: 
        int i;
        do
        {
          return;
          OnePlusAppBootManager.this.writeXml_appboot(OnePlusAppBootManager.-get2());
          return;
          i = OnePlusAppBootManager.-wrap2(OnePlusAppBootManager.this, (String)paramMessage.obj);
        } while (!OnePlusAppBootManager.DEBUG);
        OnePlusAppBootManager.myLog("forceStopPkg # ret=" + i + " " + (String)paramMessage.obj);
        return;
      case 3: 
        paramMessage = new ConfigGrabber(OnePlusAppBootManager.-get1(OnePlusAppBootManager.this), OnePlusAppBootManager.-get0());
        OnePlusAppBootManager.-wrap7(OnePlusAppBootManager.this, paramMessage.grabConfig());
        return;
      }
      OnePlusAppBootManager localOnePlusAppBootManager = OnePlusAppBootManager.this;
      if (paramMessage.arg1 != 0) {
        bool = true;
      }
      OnePlusAppBootManager.-wrap10(localOnePlusAppBootManager, bool);
    }
  }
  
  public static class OnePlusAppBootInfo
  {
    public static final int BOOT_FLAG_GO = 1;
    public static final int BOOT_FLAG_IGNORE = 0;
    public static final int PKG_ACTION_BLACKLIST = 2;
    public static final int PKG_ACTION_DEFAULT = 0;
    public static final int PKG_ACTION_INVALID = -1;
    public static final int PKG_ACTION_WHITELIST = 1;
    public static final int PKG_FLAG_BLACKLIST_APP = 16;
    public static final int PKG_FLAG_BLACKLIST_COMPONENT = 4096;
    public static final int PKG_FLAG_DATA_APP = 4;
    public static final int PKG_FLAG_DEFAULT_APP = 1;
    public static final int PKG_FLAG_FORCE_UPDATE_ACTION = 16384;
    public static final int PKG_FLAG_HUGE_POWER = 32768;
    public static final int PKG_FLAG_SPECIAL_ACCESSIBILITY_APP = 1024;
    public static final int PKG_FLAG_SPECIAL_APP = 32;
    public static final int PKG_FLAG_SPECIAL_DEPENDENCY_APP = 64;
    public static final int PKG_FLAG_SPECIAL_HIDE_LAUNCHER_APP = 512;
    public static final int PKG_FLAG_SPECIAL_INPUT_METHOD_APP = 256;
    public static final int PKG_FLAG_SPECIAL_LAUNCHER_APP = 128;
    public static final int PKG_FLAG_SPECIAL_TTS_APP = 8192;
    public static final int PKG_FLAG_SPECIAL_WIDGET_APP = 2048;
    public static final int PKG_FLAG_SYS_APP = 2;
    public static final int PKG_FLAG_WHITELIST_APP = 8;
    private volatile int mAction = 0;
    private boolean mBlackListEnable = true;
    private volatile int mBootFlag = 0;
    private HashSet<String> mCalleePkgSet = new HashSet();
    private HashSet<String> mCallerPkgSet = new HashSet();
    private String mLastCallingServiceTag = null;
    private long mLastCallingServiceTime = 0L;
    PackageParser.Package mParserPkg = null;
    private volatile int mPkgFlag = 1;
    private String mPkgName = null;
    private int mUid = 0;
    
    public OnePlusAppBootInfo(String paramString)
    {
      this.mPkgName = paramString;
      if ((OnePlusAppBootManager.-get3() != null) && (paramString != null))
      {
        paramString = (OnePlusAppBootManager.PrePkgInfo)OnePlusAppBootManager.-get3().get(paramString);
        if (paramString != null)
        {
          this.mAction = paramString.mAction;
          if ((paramString.mFlag & 0x1000) == 0) {
            break label126;
          }
          this.mBlackListEnable = true;
        }
      }
      return;
      label126:
      this.mBlackListEnable = false;
    }
    
    public void addCalleePackage(String paramString)
    {
      if ((paramString != null) && (paramString.length() > 1)) {
        this.mCalleePkgSet.add(paramString);
      }
    }
    
    public void addCallerPackage(String paramString)
    {
      if ((paramString != null) && (paramString.length() > 1)) {
        this.mCallerPkgSet.add(paramString);
      }
    }
    
    public int getAction()
    {
      return this.mAction;
    }
    
    public boolean getBlackListEnableFlag()
    {
      return this.mBlackListEnable;
    }
    
    public int getBootFlag()
    {
      return this.mBootFlag;
    }
    
    public HashSet<String> getCalleePackageSet()
    {
      return this.mCalleePkgSet;
    }
    
    public String getCalleePackageSetString()
    {
      return this.mCalleePkgSet.toString();
    }
    
    public HashSet<String> getCallerPackageSet()
    {
      return this.mCallerPkgSet;
    }
    
    public String getCallerPackageSetString()
    {
      return this.mCallerPkgSet.toString();
    }
    
    public int getPkgFlag()
    {
      return this.mPkgFlag;
    }
    
    public String getPkgName()
    {
      return this.mPkgName;
    }
    
    public int getUid()
    {
      return this.mUid;
    }
    
    public void setAction(int paramInt)
    {
      this.mAction = paramInt;
    }
    
    public void setBlackListEnableFlag(boolean paramBoolean)
    {
      this.mBlackListEnable = paramBoolean;
    }
    
    public void setBootFlag(int paramInt)
    {
      this.mBootFlag = paramInt;
    }
    
    public void setDependencyPackageFlag()
    {
      this.mPkgFlag = (this.mPkgFlag | 0x40 | 0x20);
      this.mBootFlag = 1;
    }
    
    public void setPPPackage(PackageParser.Package paramPackage)
    {
      this.mParserPkg = paramPackage;
      if ((paramPackage == null) || (paramPackage.applicationInfo == null)) {}
      while ((this.mPkgName == null) || ("android".equals(this.mPkgName)))
      {
        this.mPkgFlag |= 0x2;
        this.mPkgFlag &= 0xFFFFFFFB;
        this.mBootFlag = 1;
        OnePlusAppBootManager.myLog(" setPPPackage # abi = " + this);
        return;
      }
      if (this.mParserPkg != null)
      {
        if (this.mParserPkg.applicationInfo != null)
        {
          this.mUid = this.mParserPkg.applicationInfo.uid;
          int i = 0;
          int j;
          Iterator localIterator;
          label179:
          label180:
          Object localObject1;
          if ((this.mParserPkg.applicationInfo.flags & 0x81) != 0)
          {
            i = 1;
            this.mPkgFlag |= 0x2;
            this.mPkgFlag &= 0xFFFFFFFB;
            this.mBootFlag = 1;
            setAction(1);
            j = 0;
            localIterator = paramPackage.activities.iterator();
            break label556;
            if (localIterator.hasNext())
            {
              localObject1 = (PackageParser.Activity)localIterator.next();
              if (j == 0) {
                break label539;
              }
            }
            if (j == 0)
            {
              if (OnePlusAppBootManager.DEBUG) {
                OnePlusAppBootManager.myLog("# setPPPackage # parserPkg=" + paramPackage + " # hide-laucher app");
              }
              setPkgFlag(getPkgFlag() | 0x20);
            }
            j = 0;
            localIterator = paramPackage.services.iterator();
            label266:
            break label616;
          }
          for (;;)
          {
            Object localObject2;
            if (localIterator.hasNext())
            {
              localObject1 = (PackageParser.Service)localIterator.next();
              if (j == 0) {}
            }
            else
            {
              if (j != 0)
              {
                if (OnePlusAppBootManager.DEBUG) {
                  OnePlusAppBootManager.myLog("# setPPPackage # parserPkg=" + paramPackage + " # tts app");
                }
                setPkgFlag(getPkgFlag() | 0x20);
              }
              if (i != 0) {
                break label659;
              }
              localIterator = paramPackage.services.iterator();
              for (;;)
              {
                if (!localIterator.hasNext()) {
                  break label659;
                }
                localObject1 = (PackageParser.Service)localIterator.next();
                if (localObject1 != null)
                {
                  localObject1 = ((PackageParser.Service)localObject1).intents.iterator();
                  if (((Iterator)localObject1).hasNext())
                  {
                    localObject2 = (PackageParser.ServiceIntentInfo)((Iterator)localObject1).next();
                    if ((localObject2 == null) || (!((PackageParser.ServiceIntentInfo)localObject2).hasAction("android.content.SyncAdapter")) || (((PackageParser.ServiceIntentInfo)localObject2).service == null) || (((PackageParser.ServiceIntentInfo)localObject2).service.getComponentName() == null)) {
                      break;
                    }
                    if (OnePlusAppBootManager.DEBUG) {
                      OnePlusAppBootManager.myLog("# setPPPackage # SyncAdapter: " + ((PackageParser.ServiceIntentInfo)localObject2).service.getComponentName().flattenToShortString());
                    }
                    OnePlusAppBootManager.-get4().add(((PackageParser.ServiceIntentInfo)localObject2).service.getComponentName().flattenToShortString());
                  }
                }
              }
              this.mPkgFlag &= 0xFFFFFFFD;
              this.mPkgFlag |= 0x4;
              this.mBootFlag = 0;
              break;
              label539:
              if (localObject1 == null) {
                break label180;
              }
              localObject1 = ((PackageParser.Activity)localObject1).intents.iterator();
              label556:
              if (!((Iterator)localObject1).hasNext()) {
                break label180;
              }
              localObject2 = (PackageParser.ActivityIntentInfo)((Iterator)localObject1).next();
              if ((localObject2 == null) || (!((PackageParser.ActivityIntentInfo)localObject2).hasCategory("android.intent.category.LAUNCHER"))) {
                break label179;
              }
              j = 1;
              break label180;
            }
            if (localObject1 != null)
            {
              localObject1 = ((PackageParser.Service)localObject1).intents.iterator();
              label616:
              if (((Iterator)localObject1).hasNext())
              {
                localObject2 = (PackageParser.ServiceIntentInfo)((Iterator)localObject1).next();
                if ((localObject2 == null) || (!((PackageParser.ServiceIntentInfo)localObject2).hasAction("android.intent.action.TTS_SERVICE"))) {
                  break label266;
                }
                j = 1;
              }
            }
          }
        }
        label659:
        if ((paramPackage.packageName.contains("clock")) || (paramPackage.packageName.contains("alarm")) || (paramPackage.packageName.contains("calendar")) || (paramPackage.packageName.contains("plan")) || (paramPackage.packageName.contains("mail")) || (paramPackage.packageName.contains("note")) || (paramPackage.packageName.contains("test")) || (paramPackage.packageName.contains("cts")))
        {
          if (OnePlusAppBootManager.DEBUG) {
            OnePlusAppBootManager.myLog("# setPPPackage # parserPkg=" + paramPackage + " # alarm app");
          }
          setPkgFlag(getPkgFlag() | 0x20);
        }
        if (getAction() == 0)
        {
          if ((getPkgFlag() & 0x20) == 0) {
            break label865;
          }
          setAction(1);
          setBootFlag(1);
        }
      }
      for (;;)
      {
        if (OnePlusAppBootManager.DEBUG) {
          OnePlusAppBootManager.myLog("# setPPPackage # " + this);
        }
        return;
        label865:
        setAction(2);
      }
    }
    
    public void setPkgFlag(int paramInt)
    {
      this.mPkgFlag = paramInt;
    }
    
    public void setUid(int paramInt)
    {
      this.mUid = paramInt;
    }
    
    public String toString()
    {
      return "OnePlusAppBootInfo{mPkgName=" + this.mPkgName + ", mUid=" + this.mUid + ", mPkgFlag=" + this.mPkgFlag + ", mAction=" + this.mAction + ", mBootFlag=" + this.mBootFlag + ", mBlackListEnable=" + this.mBlackListEnable + "}";
    }
    
    public boolean updateLastCallingServiceBootPolicy(String paramString)
    {
      if (this.mBootFlag == 1) {
        return true;
      }
      boolean bool2 = false;
      long l = SystemClock.uptimeMillis();
      boolean bool1 = bool2;
      if (Math.abs(l - this.mLastCallingServiceTime) < 3000L)
      {
        bool1 = bool2;
        if (this.mLastCallingServiceTag != null)
        {
          bool1 = bool2;
          if (this.mLastCallingServiceTag.equals(paramString)) {
            bool1 = true;
          }
        }
      }
      if (OnePlusAppBootManager.DEBUG) {
        OnePlusAppBootManager.myLog("# updateLastCallingServiceBootPolicy # ret=" + bool1 + " tag=" + paramString);
      }
      this.mLastCallingServiceTime = l;
      this.mLastCallingServiceTag = paramString;
      return bool1;
    }
    
    public void updateWidgetAppFlag(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.mPkgFlag = (this.mPkgFlag | 0x800 | 0x20);
        this.mBootFlag = 1;
        this.mAction = 1;
      }
    }
  }
  
  private class PrePkgInfo
  {
    public int mAction = 0;
    public int mFlag = 0;
    public String mPkgName = null;
    
    PrePkgInfo(String paramString, int paramInt1, int paramInt2)
    {
      this.mPkgName = paramString;
      this.mFlag = paramInt1;
      this.mAction = paramInt2;
    }
    
    public String toString()
    {
      return "PrePkgInfo{mPkgName=" + this.mPkgName + ", mFlag=" + this.mFlag + ", mAction=" + this.mAction + "}";
    }
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    public SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      OnePlusAppBootManager.-wrap11(OnePlusAppBootManager.this, "change");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/OnePlusAppBootManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */