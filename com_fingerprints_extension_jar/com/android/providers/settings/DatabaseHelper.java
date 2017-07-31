package com.android.providers.settings;

import android.content.Context;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.media.AudioSystem;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.OpFeatures;
import com.android.internal.widget.LockPatternUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

@Deprecated
class DatabaseHelper
  extends SQLiteOpenHelper
{
  private static final HashSet<String> mValidTables = new HashSet();
  private boolean isCTA = false;
  private Context mContext;
  private int mUserHandle;
  
  static
  {
    mValidTables.add("system");
    mValidTables.add("secure");
    mValidTables.add("global");
    mValidTables.add("bluetooth_devices");
    mValidTables.add("bookmarks");
    mValidTables.add("favorites");
    mValidTables.add("old_favorites");
    mValidTables.add("android_metadata");
  }
  
  public DatabaseHelper(Context paramContext, int paramInt)
  {
    super(paramContext, dbNameForUser(paramInt), null, 118);
    this.mContext = paramContext;
    this.mUserHandle = paramInt;
    this.isCTA = paramContext.getPackageManager().hasSystemFeature("oem.ctaSwitch.support");
  }
  
  private void createGlobalTable(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TABLE global (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT UNIQUE ON CONFLICT REPLACE,value TEXT);");
    paramSQLiteDatabase.execSQL("CREATE INDEX globalIndex1 ON global (name);");
  }
  
  private void createSecureTable(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TABLE secure (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT UNIQUE ON CONFLICT REPLACE,value TEXT);");
    paramSQLiteDatabase.execSQL("CREATE INDEX secureIndex1 ON secure (name);");
  }
  
  static String dbNameForUser(int paramInt)
  {
    if (paramInt == 0) {
      return "settings.db";
    }
    File localFile = new File(Environment.getUserSystemDirectory(paramInt), "settings.db");
    if (!localFile.exists())
    {
      Log.i("SettingsProvider", "No previous database file exists - running in in-memory mode");
      return null;
    }
    return localFile.getPath();
  }
  
  private String getDefaultDeviceName()
  {
    return SystemProperties.get("ro.display.series");
  }
  
  private int getIntValueFromSystem(SQLiteDatabase paramSQLiteDatabase, String paramString, int paramInt)
  {
    return getIntValueFromTable(paramSQLiteDatabase, "system", paramString, paramInt);
  }
  
  private int getIntValueFromTable(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, int paramInt)
  {
    paramSQLiteDatabase = getStringValueFromTable(paramSQLiteDatabase, paramString1, paramString2, null);
    if (paramSQLiteDatabase != null) {
      paramInt = Integer.parseInt(paramSQLiteDatabase);
    }
    return paramInt;
  }
  
  private String getOldDefaultDeviceName()
  {
    return this.mContext.getResources().getString(2131099666, new Object[] { Build.MANUFACTURER, Build.MODEL });
  }
  
  private String getStringValueFromTable(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String paramString3)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    try
    {
      paramString2 = "name='" + paramString2 + "'";
      localObject1 = localObject2;
      paramSQLiteDatabase = paramSQLiteDatabase.query(paramString1, new String[] { "value" }, paramString2, null, null, null, null);
      if (paramSQLiteDatabase != null)
      {
        localObject1 = paramSQLiteDatabase;
        if (paramSQLiteDatabase.moveToFirst())
        {
          localObject1 = paramSQLiteDatabase;
          paramString1 = paramSQLiteDatabase.getString(0);
          if (paramString1 == null) {}
          for (;;)
          {
            if (paramSQLiteDatabase != null) {
              paramSQLiteDatabase.close();
            }
            return paramString3;
            paramString3 = paramString1;
          }
        }
      }
      if (paramSQLiteDatabase != null) {
        paramSQLiteDatabase.close();
      }
      return paramString3;
    }
    finally
    {
      if (localObject1 != null) {
        ((Cursor)localObject1).close();
      }
    }
  }
  
  private boolean isInMemory()
  {
    return getDatabaseName() == null;
  }
  
  public static boolean isValidTable(String paramString)
  {
    return mValidTables.contains(paramString);
  }
  
  /* Error */
  private void loadBookmarks(SQLiteDatabase paramSQLiteDatabase)
  {
    // Byte code:
    //   0: new 217	android/content/ContentValues
    //   3: dup
    //   4: invokespecial 218	android/content/ContentValues:<init>	()V
    //   7: astore 7
    //   9: aload_0
    //   10: getfield 56	com/android/providers/settings/DatabaseHelper:mContext	Landroid/content/Context;
    //   13: invokevirtual 64	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   16: astore 8
    //   18: aload_0
    //   19: getfield 56	com/android/providers/settings/DatabaseHelper:mContext	Landroid/content/Context;
    //   22: invokevirtual 150	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   25: ldc -37
    //   27: invokevirtual 223	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   30: astore 9
    //   32: aload 9
    //   34: ldc 37
    //   36: invokestatic 229	com/android/internal/util/XmlUtils:beginDocument	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)V
    //   39: aload 9
    //   41: invokeinterface 235 1 0
    //   46: istore_2
    //   47: aload 9
    //   49: invokeinterface 238 1 0
    //   54: istore_3
    //   55: iload_3
    //   56: iconst_3
    //   57: if_icmpne +14 -> 71
    //   60: aload 9
    //   62: invokeinterface 235 1 0
    //   67: iload_2
    //   68: if_icmple +465 -> 533
    //   71: iload_3
    //   72: iconst_1
    //   73: if_icmpeq +460 -> 533
    //   76: iload_3
    //   77: iconst_2
    //   78: if_icmpne -31 -> 47
    //   81: ldc -16
    //   83: aload 9
    //   85: invokeinterface 243 1 0
    //   90: invokevirtual 246	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   93: ifne +4 -> 97
    //   96: return
    //   97: aload 9
    //   99: aconst_null
    //   100: ldc -8
    //   102: invokeinterface 252 3 0
    //   107: astore 6
    //   109: aload 9
    //   111: aconst_null
    //   112: ldc -2
    //   114: invokeinterface 252 3 0
    //   119: astore 10
    //   121: aload 9
    //   123: aconst_null
    //   124: ldc_w 256
    //   127: invokeinterface 252 3 0
    //   132: astore 4
    //   134: aload 9
    //   136: aconst_null
    //   137: ldc_w 258
    //   140: invokeinterface 252 3 0
    //   145: astore 5
    //   147: aload 4
    //   149: iconst_0
    //   150: invokevirtual 262	java/lang/String:charAt	(I)C
    //   153: istore_3
    //   154: aload 4
    //   156: invokestatic 268	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   159: ifeq +56 -> 215
    //   162: ldc 108
    //   164: new 170	java/lang/StringBuilder
    //   167: dup
    //   168: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   171: ldc_w 270
    //   174: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   177: aload 6
    //   179: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   182: ldc_w 272
    //   185: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   188: aload 10
    //   190: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   193: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   196: invokestatic 275	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   199: pop
    //   200: goto -153 -> 47
    //   203: astore_1
    //   204: ldc 108
    //   206: ldc_w 277
    //   209: aload_1
    //   210: invokestatic 280	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   213: pop
    //   214: return
    //   215: aload 6
    //   217: ifnull +257 -> 474
    //   220: aload 10
    //   222: ifnull +252 -> 474
    //   225: new 282	android/content/ComponentName
    //   228: dup
    //   229: aload 6
    //   231: aload 10
    //   233: invokespecial 285	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   236: astore 4
    //   238: aload 8
    //   240: aload 4
    //   242: iconst_0
    //   243: invokevirtual 289	android/content/pm/PackageManager:getActivityInfo	(Landroid/content/ComponentName;I)Landroid/content/pm/ActivityInfo;
    //   246: astore 5
    //   248: new 291	android/content/Intent
    //   251: dup
    //   252: ldc_w 293
    //   255: aconst_null
    //   256: invokespecial 296	android/content/Intent:<init>	(Ljava/lang/String;Landroid/net/Uri;)V
    //   259: astore 6
    //   261: aload 6
    //   263: ldc_w 298
    //   266: invokevirtual 302	android/content/Intent:addCategory	(Ljava/lang/String;)Landroid/content/Intent;
    //   269: pop
    //   270: aload 6
    //   272: aload 4
    //   274: invokevirtual 306	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
    //   277: pop
    //   278: aload 5
    //   280: aload 8
    //   282: invokevirtual 312	android/content/pm/ActivityInfo:loadLabel	(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;
    //   285: invokeinterface 315 1 0
    //   290: astore 5
    //   292: aload 6
    //   294: astore 4
    //   296: aload 4
    //   298: ldc_w 316
    //   301: invokevirtual 320	android/content/Intent:setFlags	(I)Landroid/content/Intent;
    //   304: pop
    //   305: aload 7
    //   307: ldc_w 322
    //   310: aload 4
    //   312: iconst_0
    //   313: invokevirtual 325	android/content/Intent:toUri	(I)Ljava/lang/String;
    //   316: invokevirtual 328	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   319: aload 7
    //   321: ldc_w 330
    //   324: aload 5
    //   326: invokevirtual 328	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   329: aload 7
    //   331: ldc_w 256
    //   334: iload_3
    //   335: invokestatic 334	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   338: invokevirtual 337	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   341: aload_1
    //   342: ldc 37
    //   344: ldc_w 339
    //   347: iconst_1
    //   348: anewarray 184	java/lang/String
    //   351: dup
    //   352: iconst_0
    //   353: iload_3
    //   354: invokestatic 341	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   357: aastore
    //   358: invokevirtual 345	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   361: pop
    //   362: aload_1
    //   363: ldc 37
    //   365: aconst_null
    //   366: aload 7
    //   368: invokevirtual 349	android/database/sqlite/SQLiteDatabase:insert	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;)J
    //   371: pop2
    //   372: goto -325 -> 47
    //   375: astore_1
    //   376: ldc 108
    //   378: ldc_w 277
    //   381: aload_1
    //   382: invokestatic 280	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   385: pop
    //   386: return
    //   387: astore 11
    //   389: new 282	android/content/ComponentName
    //   392: dup
    //   393: aload 8
    //   395: iconst_1
    //   396: anewarray 184	java/lang/String
    //   399: dup
    //   400: iconst_0
    //   401: aload 6
    //   403: aastore
    //   404: invokevirtual 353	android/content/pm/PackageManager:canonicalToCurrentPackageNames	([Ljava/lang/String;)[Ljava/lang/String;
    //   407: iconst_0
    //   408: aaload
    //   409: aload 10
    //   411: invokespecial 285	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   414: astore 4
    //   416: aload 8
    //   418: aload 4
    //   420: iconst_0
    //   421: invokevirtual 289	android/content/pm/PackageManager:getActivityInfo	(Landroid/content/ComponentName;I)Landroid/content/pm/ActivityInfo;
    //   424: astore 5
    //   426: goto -178 -> 248
    //   429: astore 4
    //   431: ldc 108
    //   433: new 170	java/lang/StringBuilder
    //   436: dup
    //   437: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   440: ldc_w 355
    //   443: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   446: aload 6
    //   448: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   451: ldc_w 272
    //   454: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   457: aload 10
    //   459: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   462: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   465: aload 11
    //   467: invokestatic 280	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   470: pop
    //   471: goto -424 -> 47
    //   474: aload 5
    //   476: ifnull +21 -> 497
    //   479: ldc_w 293
    //   482: aload 5
    //   484: invokestatic 359	android/content/Intent:makeMainSelectorActivity	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   487: astore 4
    //   489: ldc_w 361
    //   492: astore 5
    //   494: goto -198 -> 296
    //   497: ldc 108
    //   499: new 170	java/lang/StringBuilder
    //   502: dup
    //   503: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   506: ldc_w 363
    //   509: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   512: aload 4
    //   514: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   517: ldc_w 365
    //   520: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   523: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   526: invokestatic 275	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   529: pop
    //   530: goto -483 -> 47
    //   533: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	534	0	this	DatabaseHelper
    //   0	534	1	paramSQLiteDatabase	SQLiteDatabase
    //   46	23	2	i	int
    //   54	300	3	j	int
    //   132	287	4	localObject1	Object
    //   429	1	4	localNameNotFoundException1	android.content.pm.PackageManager.NameNotFoundException
    //   487	26	4	localIntent	android.content.Intent
    //   145	348	5	localObject2	Object
    //   107	340	6	localObject3	Object
    //   7	360	7	localContentValues	android.content.ContentValues
    //   16	401	8	localPackageManager	PackageManager
    //   30	105	9	localXmlResourceParser	android.content.res.XmlResourceParser
    //   119	339	10	str	String
    //   387	79	11	localNameNotFoundException2	android.content.pm.PackageManager.NameNotFoundException
    // Exception table:
    //   from	to	target	type
    //   18	47	203	org/xmlpull/v1/XmlPullParserException
    //   47	55	203	org/xmlpull/v1/XmlPullParserException
    //   60	71	203	org/xmlpull/v1/XmlPullParserException
    //   81	96	203	org/xmlpull/v1/XmlPullParserException
    //   97	200	203	org/xmlpull/v1/XmlPullParserException
    //   225	238	203	org/xmlpull/v1/XmlPullParserException
    //   238	248	203	org/xmlpull/v1/XmlPullParserException
    //   248	292	203	org/xmlpull/v1/XmlPullParserException
    //   296	372	203	org/xmlpull/v1/XmlPullParserException
    //   389	416	203	org/xmlpull/v1/XmlPullParserException
    //   416	426	203	org/xmlpull/v1/XmlPullParserException
    //   431	471	203	org/xmlpull/v1/XmlPullParserException
    //   479	489	203	org/xmlpull/v1/XmlPullParserException
    //   497	530	203	org/xmlpull/v1/XmlPullParserException
    //   18	47	375	java/io/IOException
    //   47	55	375	java/io/IOException
    //   60	71	375	java/io/IOException
    //   81	96	375	java/io/IOException
    //   97	200	375	java/io/IOException
    //   225	238	375	java/io/IOException
    //   238	248	375	java/io/IOException
    //   248	292	375	java/io/IOException
    //   296	372	375	java/io/IOException
    //   389	416	375	java/io/IOException
    //   416	426	375	java/io/IOException
    //   431	471	375	java/io/IOException
    //   479	489	375	java/io/IOException
    //   497	530	375	java/io/IOException
    //   238	248	387	android/content/pm/PackageManager$NameNotFoundException
    //   416	426	429	android/content/pm/PackageManager$NameNotFoundException
  }
  
  private void loadBooleanSetting(SQLiteStatement paramSQLiteStatement, String paramString, int paramInt)
  {
    if (this.mContext.getResources().getBoolean(paramInt)) {}
    for (String str = "1";; str = "0")
    {
      loadSetting(paramSQLiteStatement, paramString, str);
      return;
    }
  }
  
  private void loadDefaultAnimationSettings(SQLiteStatement paramSQLiteStatement)
  {
    loadFractionSetting(paramSQLiteStatement, "window_animation_scale", 2131165184, 1);
    loadFractionSetting(paramSQLiteStatement, "transition_animation_scale", 2131165185, 1);
  }
  
  private void loadDefaultHapticSettings(SQLiteStatement paramSQLiteStatement)
  {
    loadBooleanSetting(paramSQLiteStatement, "haptic_feedback_enabled", 2130968583);
  }
  
  private void loadFractionSetting(SQLiteStatement paramSQLiteStatement, String paramString, int paramInt1, int paramInt2)
  {
    loadSetting(paramSQLiteStatement, paramString, Float.toString(this.mContext.getResources().getFraction(paramInt1, paramInt2, paramInt2)));
  }
  
  private void loadGlobalSettings(SQLiteDatabase paramSQLiteDatabase)
  {
    Object localObject = null;
    SQLiteStatement localSQLiteStatement;
    int k;
    for (;;)
    {
      try
      {
        localSQLiteStatement = paramSQLiteDatabase.compileStatement("INSERT OR IGNORE INTO global(name,value) VALUES(?,?);");
        localObject = localSQLiteStatement;
        loadIntegerSetting(localSQLiteStatement, "oem_black_mode", 2131034186);
        localObject = localSQLiteStatement;
        loadBooleanSetting(localSQLiteStatement, "mobile_data_network_enable", 2130968620);
        localObject = localSQLiteStatement;
        loadBooleanSetting(localSQLiteStatement, "app_permission_control", 2130968621);
        localObject = localSQLiteStatement;
        if (OpFeatures.isSupport(new int[] { 1 }))
        {
          localObject = localSQLiteStatement;
          loadBooleanSetting(localSQLiteStatement, "captive_portal_detection_enabled", 2130968634);
          localObject = localSQLiteStatement;
          loadBooleanSetting(localSQLiteStatement, "portal_notification_enable", 2130968635);
          localObject = localSQLiteStatement;
          loadBooleanSetting(localSQLiteStatement, "op_enable_wifi_multi_broadcast", 2131034215);
          localObject = localSQLiteStatement;
          loadBooleanSetting(localSQLiteStatement, "airplane_mode_on", 2130968577);
          localObject = localSQLiteStatement;
          loadBooleanSetting(localSQLiteStatement, "theater_mode_on", 2130968578);
          localObject = localSQLiteStatement;
          loadStringSetting(localSQLiteStatement, "airplane_mode_radios", 2131099648);
          localObject = localSQLiteStatement;
          loadStringSetting(localSQLiteStatement, "airplane_mode_toggleable_radios", 2131099649);
          localObject = localSQLiteStatement;
          loadBooleanSetting(localSQLiteStatement, "assisted_gps_enabled", 2130968588);
          localObject = localSQLiteStatement;
          loadBooleanSetting(localSQLiteStatement, "auto_time", 2130968579);
          localObject = localSQLiteStatement;
          loadBooleanSetting(localSQLiteStatement, "auto_time_zone", 2130968580);
          localObject = localSQLiteStatement;
          if ("1".equals(SystemProperties.get("ro.kernel.qemu"))) {
            break label1257;
          }
          localObject = localSQLiteStatement;
          if (this.mContext.getResources().getBoolean(2130968613))
          {
            break label1257;
            localObject = localSQLiteStatement;
            loadSetting(localSQLiteStatement, "stay_on_while_plugged_in", Integer.valueOf(i));
            localObject = localSQLiteStatement;
            loadIntegerSetting(localSQLiteStatement, "wifi_sleep_policy", 2131034115);
            localObject = localSQLiteStatement;
            loadSetting(localSQLiteStatement, "mode_ringer", Integer.valueOf(2));
            localObject = localSQLiteStatement;
            loadBooleanSetting(localSQLiteStatement, "package_verifier_enable", 2130968587);
            localObject = localSQLiteStatement;
            loadBooleanSetting(localSQLiteStatement, "wifi_on", 2130968591);
            localObject = localSQLiteStatement;
            loadBooleanSetting(localSQLiteStatement, "wifi_networks_available_notification_on", 2130968594);
            localObject = localSQLiteStatement;
            loadBooleanSetting(localSQLiteStatement, "bluetooth_on", 2130968584);
            localObject = localSQLiteStatement;
            loadSetting(localSQLiteStatement, "cdma_cell_broadcast_sms", Integer.valueOf(1));
            localObject = localSQLiteStatement;
            k = TelephonyManager.getDefault().getPhoneCount();
            localObject = localSQLiteStatement;
            loadBooleanSetting(localSQLiteStatement, "data_roaming", 2130968638);
            localObject = localSQLiteStatement;
            loadBooleanSetting(localSQLiteStatement, "device_provisioned", 2130968602);
            localObject = localSQLiteStatement;
            i = this.mContext.getResources().getInteger(2131034121);
            if (i > 0)
            {
              localObject = localSQLiteStatement;
              loadSetting(localSQLiteStatement, "download_manager_max_bytes_over_mobile", Integer.toString(i));
            }
            localObject = localSQLiteStatement;
            i = this.mContext.getResources().getInteger(2131034122);
            if (i > 0)
            {
              localObject = localSQLiteStatement;
              loadSetting(localSQLiteStatement, "download_manager_recommended_max_bytes_over_mobile", Integer.toString(i));
            }
            localObject = localSQLiteStatement;
            if (!"true".equalsIgnoreCase(SystemProperties.get("ro.com.android.mobiledata", "true"))) {
              break label689;
            }
            i = 1;
            localObject = localSQLiteStatement;
            loadSetting(localSQLiteStatement, "mobile_data", Integer.valueOf(i));
            i = 0;
            if (i >= k) {
              break;
            }
            localObject = localSQLiteStatement;
            paramSQLiteDatabase = "mobile_data" + i;
            localObject = localSQLiteStatement;
            if (!"true".equalsIgnoreCase(SystemProperties.get("ro.com.android.mobiledata", "true"))) {
              break label694;
            }
            j = 1;
            localObject = localSQLiteStatement;
            loadSetting(localSQLiteStatement, paramSQLiteDatabase, Integer.valueOf(j));
            i += 1;
            continue;
          }
        }
        else
        {
          localObject = localSQLiteStatement;
          loadBooleanSetting(localSQLiteStatement, "captive_portal_detection_enabled", 2130968633);
          continue;
        }
        i = 0;
      }
      finally
      {
        if (localObject != null) {
          ((SQLiteStatement)localObject).close();
        }
      }
      continue;
      label689:
      i = 0;
      continue;
      label694:
      int j = 0;
    }
    localObject = localSQLiteStatement;
    loadBooleanSetting(localSQLiteStatement, "netstats_enabled", 2130968589);
    localObject = localSQLiteStatement;
    loadBooleanSetting(localSQLiteStatement, "usb_mass_storage_enabled", 2130968590);
    localObject = localSQLiteStatement;
    loadIntegerSetting(localSQLiteStatement, "wifi_max_dhcp_retry_count", 2131034125);
    localObject = localSQLiteStatement;
    loadBooleanSetting(localSQLiteStatement, "wifi_display_on", 2130968585);
    localObject = localSQLiteStatement;
    loadStringSetting(localSQLiteStatement, "lock_sound", 2131099659);
    localObject = localSQLiteStatement;
    loadStringSetting(localSQLiteStatement, "unlock_sound", 2131099660);
    localObject = localSQLiteStatement;
    loadStringSetting(localSQLiteStatement, "trusted_sound", 2131099661);
    localObject = localSQLiteStatement;
    loadIntegerSetting(localSQLiteStatement, "power_sounds_enabled", 2131034116);
    localObject = localSQLiteStatement;
    loadStringSetting(localSQLiteStatement, "low_battery_sound", 2131099654);
    localObject = localSQLiteStatement;
    loadIntegerSetting(localSQLiteStatement, "dock_sounds_enabled", 2131034117);
    localObject = localSQLiteStatement;
    loadStringSetting(localSQLiteStatement, "desk_dock_sound", 2131099655);
    localObject = localSQLiteStatement;
    loadStringSetting(localSQLiteStatement, "desk_undock_sound", 2131099656);
    localObject = localSQLiteStatement;
    loadStringSetting(localSQLiteStatement, "car_dock_sound", 2131099657);
    localObject = localSQLiteStatement;
    loadStringSetting(localSQLiteStatement, "car_undock_sound", 2131099658);
    localObject = localSQLiteStatement;
    loadStringSetting(localSQLiteStatement, "wireless_charging_started_sound", 2131099662);
    localObject = localSQLiteStatement;
    loadIntegerSetting(localSQLiteStatement, "dock_audio_media_enabled", 2131034119);
    localObject = localSQLiteStatement;
    loadSetting(localSQLiteStatement, "set_install_location", Integer.valueOf(0));
    localObject = localSQLiteStatement;
    loadSetting(localSQLiteStatement, "default_install_location", Integer.valueOf(0));
    localObject = localSQLiteStatement;
    loadSetting(localSQLiteStatement, "emergency_tone", Integer.valueOf(0));
    localObject = localSQLiteStatement;
    loadSetting(localSQLiteStatement, "call_auto_retry", Integer.valueOf(0));
    paramSQLiteDatabase = "";
    int i = 0;
    for (;;)
    {
      if (i < k)
      {
        localObject = localSQLiteStatement;
        String str = TelephonyManager.getTelephonyProperty(i, "ro.telephony.default_network", Integer.toString(1));
        if (i == 0)
        {
          paramSQLiteDatabase = str;
        }
        else
        {
          localObject = localSQLiteStatement;
          paramSQLiteDatabase = paramSQLiteDatabase + "," + str;
        }
      }
      else
      {
        localObject = localSQLiteStatement;
        loadSetting(localSQLiteStatement, "preferred_network_mode", paramSQLiteDatabase);
        localObject = localSQLiteStatement;
        loadSetting(localSQLiteStatement, "subscription_mode", Integer.valueOf(SystemProperties.getInt("ro.telephony.default_cdma_sub", 0)));
        localObject = localSQLiteStatement;
        loadIntegerSetting(localSQLiteStatement, "low_battery_sound_timeout", 2131034126);
        localObject = localSQLiteStatement;
        loadIntegerSetting(localSQLiteStatement, "wifi_scan_always_enabled", 2131034127);
        localObject = localSQLiteStatement;
        loadIntegerSetting(localSQLiteStatement, "heads_up_notifications_enabled", 2131034129);
        localObject = localSQLiteStatement;
        loadSetting(localSQLiteStatement, "device_name", getDefaultDeviceName());
        localObject = localSQLiteStatement;
        loadSetting(localSQLiteStatement, "volte_vt_enabled", Integer.valueOf(-1));
        localObject = localSQLiteStatement;
        loadIntegerSetting(localSQLiteStatement, "captive_portal_detection_enabled", 2131034217);
        localObject = localSQLiteStatement;
        loadIntegerSetting(localSQLiteStatement, "wifi_ipv6_supported", 2131034216);
        if (localSQLiteStatement != null) {
          localSQLiteStatement.close();
        }
        return;
        label1257:
        i = 1;
        break;
      }
      i += 1;
    }
  }
  
  private void loadIntegerSetting(SQLiteStatement paramSQLiteStatement, String paramString, int paramInt)
  {
    loadSetting(paramSQLiteStatement, paramString, Integer.toString(this.mContext.getResources().getInteger(paramInt)));
  }
  
  private void loadSecure35Settings(SQLiteStatement paramSQLiteStatement)
  {
    loadBooleanSetting(paramSQLiteStatement, "backup_enabled", 2130968595);
    loadStringSetting(paramSQLiteStatement, "backup_transport", 2131099653);
  }
  
  private void loadSecureSettings(SQLiteDatabase paramSQLiteDatabase)
  {
    int i = 1;
    SQLiteDatabase localSQLiteDatabase = null;
    for (;;)
    {
      try
      {
        paramSQLiteDatabase = paramSQLiteDatabase.compileStatement("INSERT OR IGNORE INTO secure(name,value) VALUES(?,?);");
        localSQLiteDatabase = paramSQLiteDatabase;
        if (OpFeatures.isSupport(new int[] { 1 }))
        {
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "location_providers_allowed", 2131099652);
          localSQLiteDatabase = paramSQLiteDatabase;
          String str = SystemProperties.get("ro.com.android.wifi-watchlist");
          localSQLiteDatabase = paramSQLiteDatabase;
          if (!TextUtils.isEmpty(str))
          {
            localSQLiteDatabase = paramSQLiteDatabase;
            loadSetting(paramSQLiteDatabase, "wifi_watchdog_watch_list", str);
          }
          localSQLiteDatabase = paramSQLiteDatabase;
          if ("1".equals(SystemProperties.get("ro.allow.mock.location")))
          {
            localSQLiteDatabase = paramSQLiteDatabase;
            loadSetting(paramSQLiteDatabase, "mock_location", Integer.valueOf(i));
            localSQLiteDatabase = paramSQLiteDatabase;
            loadSecure35Settings(paramSQLiteDatabase);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "mount_play_not_snd", 2130968597);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "mount_ums_autostart", 2130968598);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "mount_ums_prompt", 2130968599);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "mount_ums_notify_enabled", 2130968600);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "accessibility_script_injection", 2130968605);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadStringSetting(paramSQLiteDatabase, "accessibility_web_content_key_bindings", 2131099663);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadIntegerSetting(paramSQLiteDatabase, "long_press_timeout", 2131034123);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "touch_exploration_enabled", 2130968607);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "speak_password", 2130968606);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadStringSetting(paramSQLiteDatabase, "accessibility_script_injection_url", 2131099664);
            localSQLiteDatabase = paramSQLiteDatabase;
            if (!SystemProperties.getBoolean("ro.lockscreen.disable.default", false)) {
              break label567;
            }
            localSQLiteDatabase = paramSQLiteDatabase;
            loadSetting(paramSQLiteDatabase, "lockscreen.disabled", "1");
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "screensaver_enabled", 17956975);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "screensaver_activate_on_dock", 17956976);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "screensaver_activate_on_sleep", 17956977);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadStringSetting(paramSQLiteDatabase, "screensaver_components", 17039450);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadStringSetting(paramSQLiteDatabase, "screensaver_default_component", 17039450);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "accessibility_display_magnification_enabled", 2130968608);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadFractionSetting(paramSQLiteDatabase, "accessibility_display_magnification_scale", 2131165186, 1);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "accessibility_display_magnification_auto_update", 2130968609);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "user_setup_complete", 2130968614);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadStringSetting(paramSQLiteDatabase, "immersive_mode_confirmations", 2131099665);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "install_non_market_apps", 2130968586);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "wake_gesture_enabled", 2130968616);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadIntegerSetting(paramSQLiteDatabase, "lock_screen_show_notifications", 2131034128);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "lock_screen_allow_private_notifications", 2130968615);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadIntegerSetting(paramSQLiteDatabase, "sleep_timeout", 2131034113);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadIntegerSetting(paramSQLiteDatabase, "doze_enabled", 2131034185);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadIntegerSetting(paramSQLiteDatabase, "night_display_custom_start_time", 2131034223);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadIntegerSetting(paramSQLiteDatabase, "night_display_custom_end_time", 2131034224);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadIntegerSetting(paramSQLiteDatabase, "hotspot_auto_shut_down", 2131034197);
            localSQLiteDatabase = paramSQLiteDatabase;
            loadIntegerSetting(paramSQLiteDatabase, "bluetooth_aptx_hd", 2131034230);
          }
        }
        else
        {
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "location_providers_allowed", 2131099651);
          continue;
        }
        i = 0;
      }
      finally
      {
        if (localSQLiteDatabase != null) {
          localSQLiteDatabase.close();
        }
      }
      continue;
      label567:
      localSQLiteDatabase = paramSQLiteDatabase;
      loadBooleanSetting(paramSQLiteDatabase, "lockscreen.disabled", 2130968601);
    }
  }
  
  private void loadSetting(SQLiteStatement paramSQLiteStatement, String paramString, Object paramObject)
  {
    paramSQLiteStatement.bindString(1, paramString);
    paramSQLiteStatement.bindString(2, paramObject.toString());
    paramSQLiteStatement.execute();
  }
  
  private void loadSettings(SQLiteDatabase paramSQLiteDatabase)
  {
    loadSystemSettings(paramSQLiteDatabase);
    loadSecureSettings(paramSQLiteDatabase);
    if (this.mUserHandle == 0) {
      loadGlobalSettings(paramSQLiteDatabase);
    }
  }
  
  private void loadStringSetting(SQLiteStatement paramSQLiteStatement, String paramString, int paramInt)
  {
    loadSetting(paramSQLiteStatement, paramString, this.mContext.getResources().getString(paramInt));
  }
  
  private void loadSystemSettings(SQLiteDatabase paramSQLiteDatabase)
  {
    SQLiteDatabase localSQLiteDatabase = null;
    for (;;)
    {
      try
      {
        paramSQLiteDatabase = paramSQLiteDatabase.compileStatement("INSERT OR IGNORE INTO system(name,value) VALUES(?,?);");
        localSQLiteDatabase = paramSQLiteDatabase;
        loadBooleanSetting(paramSQLiteDatabase, "dim_screen", 2130968576);
        localSQLiteDatabase = paramSQLiteDatabase;
        loadIntegerSetting(paramSQLiteDatabase, "screen_off_timeout", 2131034112);
        localSQLiteDatabase = paramSQLiteDatabase;
        loadSetting(paramSQLiteDatabase, "dtmf_tone_type", Integer.valueOf(0));
        localSQLiteDatabase = paramSQLiteDatabase;
        loadSetting(paramSQLiteDatabase, "hearing_aid", Integer.valueOf(0));
        localSQLiteDatabase = paramSQLiteDatabase;
        loadSetting(paramSQLiteDatabase, "tty_mode", Integer.valueOf(0));
        localSQLiteDatabase = paramSQLiteDatabase;
        int i = ((PowerManager)this.mContext.getSystemService("power")).getDefaultScreenBrightnessSetting();
        if (i > 0)
        {
          localSQLiteDatabase = paramSQLiteDatabase;
          loadSetting(paramSQLiteDatabase, "screen_brightness", Integer.valueOf(i));
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "screen_brightness_mode", 2130968582);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadDefaultAnimationSettings(paramSQLiteDatabase);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "accelerometer_rotation", 2130968581);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadDefaultHapticSettings(paramSQLiteDatabase);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "notification_light_pulse", 2130968596);
          localSQLiteDatabase = paramSQLiteDatabase;
          if (this.mContext.getResources().getBoolean(2130968593))
          {
            localSQLiteDatabase = paramSQLiteDatabase;
            loadBooleanSetting(paramSQLiteDatabase, "show_password", 2130968592);
          }
          localSQLiteDatabase = paramSQLiteDatabase;
          loadUISoundEffectsSettings(paramSQLiteDatabase);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "pointer_speed", 2131034124);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "time_12_24", 2131099670);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "date_format", 2131099669);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "sys.settings_show_navigation_bar", 2131034148);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "hotspot_start_init_date", 2131099674);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "vpn_start_init_date", 2131099675);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "oem_hand_pull_enable", 2130968622);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "oem_acc_night_mode", 2130968623);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "oem_acc_breath_light", 2130968624);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "oem_shutdown_ring", 2130968625);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "oem_startup_timer", 2130968626);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "oem_shutdown_timer", 2130968627);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "oem_vibrate_under_silent", 2130968628);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "mute", 2130968629);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "notification_light_pulse", 2130968630);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "battery_led_low_power", 2130968632);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadBooleanSetting(paramSQLiteDatabase, "battery_led_charging", 2130968631);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "sms_ringtone", 2131099676);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "status_bar_show_battery_percent", 2131034150);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "status_bar_show_battery_percent", 2131034150);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "three_swipe_screen_shot", 2131034151);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "key_home_double_tap_action", 2131034152);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_key_define", 2131034153);
          localSQLiteDatabase = paramSQLiteDatabase;
          if (!this.isCTA) {
            break label1400;
          }
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_blackscreen_gestrue_enable", 2131034155);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_doubleclick_lightscreen", 2131034156);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_startup_camera", 2131034157);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_control_music", 2131034158);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_noblock_mode", 2131034159);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_backgap_theme", 2131034160);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_control_prev", 2131034161);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_control_next", 2131034162);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_control_pause", 2131034163);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_control_play", 2131034164);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_otg_read", 2131034165);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_open_flashlight", 2131034166);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "oem_factory_reset_password", 2131099678);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_virtual_keyboard", 2131034168);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_zen_mode_enable", 2131034169);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_repeate_incall_unlimite", 2131034170);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_agree_incall_people", 2131034171);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_finger_lockscreen", 2131034172);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_finger_check_oneplus_account", 2131034173);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_notification_vibrate_settings", 2131034174);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_vibrate_under_silent", 2131034175);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_vibrate_when_incall", 2131034176);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_h_system_cts_vertion", 2131034177);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "def_timepower_config", 2131099677);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_three_key_define", 2131034178);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_app_notification", 2131034179);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_join_user_plan_settings", 2131034180);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "oem_launcher_main_color_key", 2131099679);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "oem_launcher_content_color_key", 2131099680);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_silent_status", 2131034182);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_zen_status", 2131034183);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_ring_status", 2131034184);
          localSQLiteDatabase = paramSQLiteDatabase;
          if (!SystemProperties.getBoolean("persist.sys.version.razer", false)) {
            break label1416;
          }
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_black_mode", 2131034187);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_black_mode", 2131034186);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_eyecare_enable", 2131034189);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "require_password_to_decrypt", 2131034190);
          localSQLiteDatabase = paramSQLiteDatabase;
          if (!this.mContext.getPackageManager().hasSystemFeature("oem.ctaSwitch.support")) {
            break label1432;
          }
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_sensor_rotate_silent", 2131034194);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_sensor_three_finger", 2131034195);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_acc_anti_misoperation_screen", 2131034196);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadSetting(paramSQLiteDatabase, "oem_oneplus_devicename", getDefaultDeviceName());
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "buttons_brightness", 2131034188);
          localSQLiteDatabase = paramSQLiteDatabase;
          if (!OpFeatures.isSupport(new int[] { 1 })) {
            break label1474;
          }
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "wifi_auto_change_to_mobile_data", 2131034199);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "wifi_should_switch_network", 2131034200);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "has_new_version_to_update", 2131034201);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "oem_white_mode_accent_color", 2131099683);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadStringSetting(paramSQLiteDatabase, "oem_black_mode_accent_color", 2131099684);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_white_mode_accent_color_index", 2131034202);
          localSQLiteDatabase = paramSQLiteDatabase;
          if (!SystemProperties.getBoolean("persist.sys.version.razer", false)) {
            break label1490;
          }
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_black_mode_accent_color_index", 2131034204);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "game_mode_block_notification", 2131034218);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "game_mode_lock_buttons", 2131034219);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_nightmode_progress_status", 2131034220);
          localSQLiteDatabase = paramSQLiteDatabase;
          if (!this.mContext.getPackageManager().hasSystemFeature("oem.read_mode.support")) {
            break label1506;
          }
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_screen_better_value", 2131034221);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "incoming_call_vibrate_mode", 2131034225);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "incoming_call_vibrate_intensity", 2131034226);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "notice_vibrate_intensity", 2131034227);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "vibrate_on_touch_intensity", 2131034228);
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "oem_share_wifi", 2131034229);
          return;
        }
        localSQLiteDatabase = paramSQLiteDatabase;
        if (this.mContext.getPackageManager().hasSystemFeature("oem.amoled.support"))
        {
          localSQLiteDatabase = paramSQLiteDatabase;
          loadIntegerSetting(paramSQLiteDatabase, "screen_brightness", 2131034181);
          continue;
        }
        localSQLiteDatabase = paramSQLiteDatabase;
      }
      finally
      {
        if (localSQLiteDatabase != null) {
          localSQLiteDatabase.close();
        }
      }
      loadIntegerSetting(paramSQLiteDatabase, "screen_brightness", 2131034114);
      continue;
      label1400:
      localSQLiteDatabase = paramSQLiteDatabase;
      loadIntegerSetting(paramSQLiteDatabase, "oem_acc_blackscreen_gestrue_enable", 2131034154);
      continue;
      label1416:
      localSQLiteDatabase = paramSQLiteDatabase;
      loadIntegerSetting(paramSQLiteDatabase, "oem_black_mode", 2131034186);
      continue;
      label1432:
      localSQLiteDatabase = paramSQLiteDatabase;
      loadIntegerSetting(paramSQLiteDatabase, "oem_acc_sensor_rotate_silent", 2131034191);
      localSQLiteDatabase = paramSQLiteDatabase;
      loadIntegerSetting(paramSQLiteDatabase, "oem_acc_sensor_three_finger", 2131034192);
      localSQLiteDatabase = paramSQLiteDatabase;
      loadIntegerSetting(paramSQLiteDatabase, "oem_acc_anti_misoperation_screen", 2131034193);
      continue;
      label1474:
      localSQLiteDatabase = paramSQLiteDatabase;
      loadIntegerSetting(paramSQLiteDatabase, "wifi_auto_change_to_mobile_data", 2131034198);
      continue;
      label1490:
      localSQLiteDatabase = paramSQLiteDatabase;
      loadIntegerSetting(paramSQLiteDatabase, "oem_black_mode_accent_color_index", 2131034203);
      continue;
      label1506:
      localSQLiteDatabase = paramSQLiteDatabase;
      loadIntegerSetting(paramSQLiteDatabase, "oem_screen_better_value", 2131034222);
    }
  }
  
  private void loadUISoundEffectsSettings(SQLiteStatement paramSQLiteStatement)
  {
    loadBooleanSetting(paramSQLiteStatement, "dtmf_tone", 2130968611);
    loadBooleanSetting(paramSQLiteStatement, "sound_effects_enabled", 2130968612);
    loadBooleanSetting(paramSQLiteStatement, "haptic_feedback_enabled", 2130968583);
    loadIntegerSetting(paramSQLiteStatement, "lockscreen_sounds_enabled", 2131034118);
  }
  
  private void loadVibrateSetting(SQLiteDatabase paramSQLiteDatabase, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramSQLiteDatabase.execSQL("DELETE FROM system WHERE name='vibrate_on'");
    }
    SQLiteDatabase localSQLiteDatabase = null;
    try
    {
      paramSQLiteDatabase = paramSQLiteDatabase.compileStatement("INSERT OR IGNORE INTO system(name,value) VALUES(?,?);");
      localSQLiteDatabase = paramSQLiteDatabase;
      int i = AudioSystem.getValueForVibrateSetting(0, 1, 2);
      localSQLiteDatabase = paramSQLiteDatabase;
      loadSetting(paramSQLiteDatabase, "vibrate_on", Integer.valueOf(i | AudioSystem.getValueForVibrateSetting(i, 0, 2)));
      return;
    }
    finally
    {
      if (localSQLiteDatabase != null) {
        localSQLiteDatabase.close();
      }
    }
  }
  
  private void loadVibrateWhenRingingSetting(SQLiteDatabase paramSQLiteDatabase)
  {
    if ((getIntValueFromSystem(paramSQLiteDatabase, "vibrate_on", 0) & 0x3) == 1) {}
    for (;;)
    {
      SQLiteDatabase localSQLiteDatabase = null;
      try
      {
        paramSQLiteDatabase = paramSQLiteDatabase.compileStatement("INSERT OR IGNORE INTO system(name,value) VALUES(?,?);");
        localSQLiteDatabase = paramSQLiteDatabase;
        loadIntegerSetting(paramSQLiteDatabase, "vibrate_when_ringing", 2131034147);
        return;
      }
      finally
      {
        if (localSQLiteDatabase == null) {
          break;
        }
        localSQLiteDatabase.close();
      }
    }
  }
  
  private void loadVolumeLevels(SQLiteDatabase paramSQLiteDatabase)
  {
    Object localObject = null;
    try
    {
      SQLiteStatement localSQLiteStatement = paramSQLiteDatabase.compileStatement("INSERT OR IGNORE INTO system(name,value) VALUES(?,?);");
      localObject = localSQLiteStatement;
      loadSetting(localSQLiteStatement, "volume_music", Integer.valueOf(AudioSystem.getDefaultStreamVolume(3)));
      localObject = localSQLiteStatement;
      loadSetting(localSQLiteStatement, "volume_ring", Integer.valueOf(AudioSystem.getDefaultStreamVolume(2)));
      localObject = localSQLiteStatement;
      loadSetting(localSQLiteStatement, "volume_system", Integer.valueOf(AudioSystem.getDefaultStreamVolume(1)));
      localObject = localSQLiteStatement;
      loadSetting(localSQLiteStatement, "volume_voice", Integer.valueOf(AudioSystem.getDefaultStreamVolume(0)));
      localObject = localSQLiteStatement;
      loadSetting(localSQLiteStatement, "volume_alarm", Integer.valueOf(AudioSystem.getDefaultStreamVolume(4)));
      localObject = localSQLiteStatement;
      loadSetting(localSQLiteStatement, "volume_notification", Integer.valueOf(AudioSystem.getDefaultStreamVolume(5)));
      localObject = localSQLiteStatement;
      loadSetting(localSQLiteStatement, "volume_bluetooth_sco", Integer.valueOf(AudioSystem.getDefaultStreamVolume(6)));
      int i = 166;
      localObject = localSQLiteStatement;
      if (!this.mContext.getResources().getBoolean(17956957)) {
        i = 174;
      }
      localObject = localSQLiteStatement;
      loadSetting(localSQLiteStatement, "mode_ringer_streams_affected", Integer.valueOf(i));
      localObject = localSQLiteStatement;
      loadSetting(localSQLiteStatement, "mute_streams_affected", Integer.valueOf(46));
      if (localSQLiteStatement != null) {
        localSQLiteStatement.close();
      }
      loadVibrateWhenRingingSetting(paramSQLiteDatabase);
      return;
    }
    finally
    {
      if (localObject != null) {
        ((SQLiteStatement)localObject).close();
      }
    }
  }
  
  private void movePrefixedSettingsToNewTable(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String[] paramArrayOfString)
  {
    String str2 = null;
    String str3 = null;
    paramSQLiteDatabase.beginTransaction();
    String str1 = str3;
    try
    {
      paramString2 = paramSQLiteDatabase.compileStatement("INSERT INTO " + paramString2 + " (name,value) SELECT name,value FROM " + paramString1 + " WHERE substr(name,0,?)=?");
      str1 = str3;
      str2 = paramString2;
      paramString1 = paramSQLiteDatabase.compileStatement("DELETE FROM " + paramString1 + " WHERE substr(name,0,?)=?");
      int i = 0;
      str1 = paramString1;
      str2 = paramString2;
      int j = paramArrayOfString.length;
      while (i < j)
      {
        str3 = paramArrayOfString[i];
        str1 = paramString1;
        str2 = paramString2;
        paramString2.bindLong(1, str3.length() + 1);
        str1 = paramString1;
        str2 = paramString2;
        paramString2.bindString(2, str3);
        str1 = paramString1;
        str2 = paramString2;
        paramString2.execute();
        str1 = paramString1;
        str2 = paramString2;
        paramString1.bindLong(1, str3.length() + 1);
        str1 = paramString1;
        str2 = paramString2;
        paramString1.bindString(2, str3);
        str1 = paramString1;
        str2 = paramString2;
        paramString1.execute();
        i += 1;
      }
      str1 = paramString1;
      str2 = paramString2;
      paramSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      paramSQLiteDatabase.endTransaction();
      if (str2 != null) {
        str2.close();
      }
      if (str1 != null) {
        str1.close();
      }
    }
  }
  
  private void moveSettingsToNewTable(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String[] paramArrayOfString, boolean paramBoolean)
  {
    Object localObject4 = null;
    Object localObject3 = null;
    paramSQLiteDatabase.beginTransaction();
    Object localObject1 = localObject3;
    Object localObject2 = localObject4;
    for (;;)
    {
      try
      {
        StringBuilder localStringBuilder = new StringBuilder().append("INSERT ");
        if (paramBoolean)
        {
          str = " OR IGNORE ";
          localObject1 = localObject3;
          localObject2 = localObject4;
          paramString2 = paramSQLiteDatabase.compileStatement(str + " INTO " + paramString2 + " (name,value) SELECT name,value FROM " + paramString1 + " WHERE name=?");
          localObject1 = localObject3;
          localObject2 = paramString2;
          paramString1 = paramSQLiteDatabase.compileStatement("DELETE FROM " + paramString1 + " WHERE name=?");
          int i = 0;
          localObject1 = paramString1;
          localObject2 = paramString2;
          int j = paramArrayOfString.length;
          if (i < j)
          {
            str = paramArrayOfString[i];
            localObject1 = paramString1;
            localObject2 = paramString2;
            paramString2.bindString(1, str);
            localObject1 = paramString1;
            localObject2 = paramString2;
            paramString2.execute();
            localObject1 = paramString1;
            localObject2 = paramString2;
            paramString1.bindString(1, str);
            localObject1 = paramString1;
            localObject2 = paramString2;
            paramString1.execute();
            i += 1;
            continue;
          }
          localObject1 = paramString1;
          localObject2 = paramString2;
          paramSQLiteDatabase.setTransactionSuccessful();
          paramSQLiteDatabase.endTransaction();
          if (paramString2 != null) {
            paramString2.close();
          }
          if (paramString1 != null) {
            paramString1.close();
          }
          return;
        }
      }
      finally
      {
        paramSQLiteDatabase.endTransaction();
        if (localObject2 != null) {
          ((SQLiteStatement)localObject2).close();
        }
        if (localObject1 != null) {
          ((SQLiteStatement)localObject1).close();
        }
      }
      String str = "";
    }
  }
  
  private String[] setToStringArray(Set<String> paramSet)
  {
    return (String[])paramSet.toArray(new String[paramSet.size()]);
  }
  
  /* Error */
  private void upgradeAutoBrightness(SQLiteDatabase paramSQLiteDatabase)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   4: aload_0
    //   5: getfield 56	com/android/providers/settings/DatabaseHelper:mContext	Landroid/content/Context;
    //   8: invokevirtual 150	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   11: ldc_w 792
    //   14: invokevirtual 371	android/content/res/Resources:getBoolean	(I)Z
    //   17: ifeq +46 -> 63
    //   20: ldc_w 373
    //   23: astore_2
    //   24: aload_1
    //   25: new 170	java/lang/StringBuilder
    //   28: dup
    //   29: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   32: ldc_w 1157
    //   35: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   38: aload_2
    //   39: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: ldc_w 1159
    //   45: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   51: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   54: aload_1
    //   55: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   58: aload_1
    //   59: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   62: return
    //   63: ldc_w 379
    //   66: astore_2
    //   67: goto -43 -> 24
    //   70: astore_2
    //   71: aload_1
    //   72: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   75: aload_2
    //   76: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	77	0	this	DatabaseHelper
    //   0	77	1	paramSQLiteDatabase	SQLiteDatabase
    //   23	44	2	str	String
    //   70	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	20	70	finally
    //   24	58	70	finally
  }
  
  private void upgradeLockPatternLocation(SQLiteDatabase paramSQLiteDatabase)
  {
    Cursor localCursor = paramSQLiteDatabase.query("system", new String[] { "_id", "value" }, "name='lock_pattern'", null, null, null, null);
    String str;
    if (localCursor.getCount() > 0)
    {
      localCursor.moveToFirst();
      str = localCursor.getString(1);
      if (TextUtils.isEmpty(str)) {}
    }
    try
    {
      new LockPatternUtils(this.mContext).saveLockPattern(LockPatternUtils.stringToPattern(str), null, 0);
      localCursor.close();
      paramSQLiteDatabase.delete("system", "name='lock_pattern'", null);
      return;
      localCursor.close();
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
  }
  
  private void upgradeScreenTimeoutFromNever(SQLiteDatabase paramSQLiteDatabase)
  {
    Cursor localCursor = paramSQLiteDatabase.query("system", new String[] { "_id", "value" }, "name=? AND value=?", new String[] { "screen_off_timeout", "-1" }, null, null, null);
    SQLiteDatabase localSQLiteDatabase = null;
    if (localCursor.getCount() > 0)
    {
      localCursor.close();
      try
      {
        paramSQLiteDatabase = paramSQLiteDatabase.compileStatement("INSERT OR REPLACE INTO system(name,value) VALUES(?,?);");
        localSQLiteDatabase = paramSQLiteDatabase;
        loadSetting(paramSQLiteDatabase, "screen_off_timeout", Integer.toString(1800000));
        return;
      }
      finally
      {
        if (localSQLiteDatabase != null) {
          localSQLiteDatabase.close();
        }
      }
    }
    localCursor.close();
  }
  
  private void upgradeVibrateSettingFromNone(SQLiteDatabase paramSQLiteDatabase)
  {
    int j = getIntValueFromSystem(paramSQLiteDatabase, "vibrate_on", 0);
    int i = j;
    if ((j & 0x3) == 0) {
      i = AudioSystem.getValueForVibrateSetting(0, 0, 2);
    }
    i = AudioSystem.getValueForVibrateSetting(i, 1, i);
    SQLiteDatabase localSQLiteDatabase = null;
    try
    {
      paramSQLiteDatabase = paramSQLiteDatabase.compileStatement("INSERT OR REPLACE INTO system(name,value) VALUES(?,?);");
      localSQLiteDatabase = paramSQLiteDatabase;
      loadSetting(paramSQLiteDatabase, "vibrate_on", Integer.valueOf(i));
      return;
    }
    finally
    {
      if (localSQLiteDatabase != null) {
        localSQLiteDatabase.close();
      }
    }
  }
  
  public void dropDatabase()
  {
    close();
    if (isInMemory()) {
      return;
    }
    File localFile = this.mContext.getDatabasePath(getDatabaseName());
    if (localFile.exists()) {
      localFile.delete();
    }
    localFile = this.mContext.getDatabasePath(getDatabaseName() + "-journal");
    if (localFile.exists()) {
      localFile.delete();
    }
  }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TABLE system (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT UNIQUE ON CONFLICT REPLACE,value TEXT);");
    paramSQLiteDatabase.execSQL("CREATE INDEX systemIndex1 ON system (name);");
    createSecureTable(paramSQLiteDatabase);
    if (this.mUserHandle == 0) {
      createGlobalTable(paramSQLiteDatabase);
    }
    paramSQLiteDatabase.execSQL("CREATE TABLE bluetooth_devices (_id INTEGER PRIMARY KEY,name TEXT,addr TEXT,channel INTEGER,type INTEGER);");
    paramSQLiteDatabase.execSQL("CREATE TABLE bookmarks (_id INTEGER PRIMARY KEY,title TEXT,folder TEXT,intent TEXT,shortcut INTEGER,ordering INTEGER);");
    paramSQLiteDatabase.execSQL("CREATE INDEX bookmarksIndex1 ON bookmarks (folder);");
    paramSQLiteDatabase.execSQL("CREATE INDEX bookmarksIndex2 ON bookmarks (shortcut);");
    int i = 0;
    try
    {
      boolean bool = IPackageManager.Stub.asInterface(ServiceManager.getService("package")).isOnlyCoreApps();
      i = bool;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
    if (i == 0) {
      loadBookmarks(paramSQLiteDatabase);
    }
    loadVolumeLevels(paramSQLiteDatabase);
    loadSettings(paramSQLiteDatabase);
  }
  
  /* Error */
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: ldc 108
    //   2: new 170	java/lang/StringBuilder
    //   5: dup
    //   6: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   9: ldc_w 1249
    //   12: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   15: iload_2
    //   16: invokevirtual 527	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   19: ldc_w 1251
    //   22: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: iload_3
    //   26: invokevirtual 527	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   32: invokestatic 275	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   35: pop
    //   36: iload_2
    //   37: istore 5
    //   39: iload_2
    //   40: bipush 20
    //   42: if_icmpne +13 -> 55
    //   45: aload_0
    //   46: aload_1
    //   47: iconst_1
    //   48: invokespecial 1253	com/android/providers/settings/DatabaseHelper:loadVibrateSetting	(Landroid/database/sqlite/SQLiteDatabase;Z)V
    //   51: bipush 21
    //   53: istore 5
    //   55: iload 5
    //   57: istore 4
    //   59: iload 5
    //   61: bipush 22
    //   63: if_icmpge +12 -> 75
    //   66: bipush 22
    //   68: istore 4
    //   70: aload_0
    //   71: aload_1
    //   72: invokespecial 1255	com/android/providers/settings/DatabaseHelper:upgradeLockPatternLocation	(Landroid/database/sqlite/SQLiteDatabase;)V
    //   75: iload 4
    //   77: istore 5
    //   79: iload 4
    //   81: bipush 23
    //   83: if_icmpge +14 -> 97
    //   86: aload_1
    //   87: ldc_w 1257
    //   90: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   93: bipush 23
    //   95: istore 5
    //   97: iload 5
    //   99: istore 4
    //   101: iload 5
    //   103: bipush 23
    //   105: if_icmpne +54 -> 159
    //   108: aload_1
    //   109: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   112: aload_1
    //   113: ldc_w 1259
    //   116: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   119: aload_1
    //   120: ldc_w 1261
    //   123: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   126: aload_1
    //   127: ldc_w 1263
    //   130: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   133: aload_1
    //   134: ldc_w 1265
    //   137: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   140: aload_1
    //   141: ldc_w 1267
    //   144: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   147: aload_1
    //   148: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   151: aload_1
    //   152: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   155: bipush 24
    //   157: istore 4
    //   159: iload 4
    //   161: istore 5
    //   163: iload 4
    //   165: bipush 24
    //   167: if_icmpne +33 -> 200
    //   170: aload_1
    //   171: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   174: aload_1
    //   175: ldc_w 1269
    //   178: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   181: aload_1
    //   182: ldc_w 1271
    //   185: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   188: aload_1
    //   189: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   192: aload_1
    //   193: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   196: bipush 25
    //   198: istore 5
    //   200: iload 5
    //   202: istore 4
    //   204: iload 5
    //   206: bipush 25
    //   208: if_icmpne +33 -> 241
    //   211: aload_1
    //   212: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   215: aload_1
    //   216: ldc_w 1273
    //   219: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   222: aload_1
    //   223: ldc_w 1275
    //   226: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   229: aload_1
    //   230: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   233: aload_1
    //   234: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   237: bipush 26
    //   239: istore 4
    //   241: iload 4
    //   243: istore 5
    //   245: iload 4
    //   247: bipush 26
    //   249: if_icmpne +24 -> 273
    //   252: aload_1
    //   253: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   256: aload_0
    //   257: aload_1
    //   258: invokespecial 1212	com/android/providers/settings/DatabaseHelper:createSecureTable	(Landroid/database/sqlite/SQLiteDatabase;)V
    //   261: aload_1
    //   262: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   265: aload_1
    //   266: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   269: bipush 27
    //   271: istore 5
    //   273: iload 5
    //   275: istore 4
    //   277: iload 5
    //   279: bipush 27
    //   281: if_icmpne +233 -> 514
    //   284: aload_0
    //   285: aload_1
    //   286: ldc 25
    //   288: ldc 31
    //   290: bipush 31
    //   292: anewarray 184	java/lang/String
    //   295: dup
    //   296: iconst_0
    //   297: ldc_w 1277
    //   300: aastore
    //   301: dup
    //   302: iconst_1
    //   303: ldc_w 1279
    //   306: aastore
    //   307: dup
    //   308: iconst_2
    //   309: ldc_w 485
    //   312: aastore
    //   313: dup
    //   314: iconst_3
    //   315: ldc_w 499
    //   318: aastore
    //   319: dup
    //   320: iconst_4
    //   321: ldc_w 502
    //   324: aastore
    //   325: dup
    //   326: iconst_5
    //   327: ldc_w 1281
    //   330: aastore
    //   331: dup
    //   332: bipush 6
    //   334: ldc_w 715
    //   337: aastore
    //   338: dup
    //   339: bipush 7
    //   341: ldc_w 636
    //   344: aastore
    //   345: dup
    //   346: bipush 8
    //   348: ldc_w 1283
    //   351: aastore
    //   352: dup
    //   353: bipush 9
    //   355: ldc_w 1285
    //   358: aastore
    //   359: dup
    //   360: bipush 10
    //   362: ldc_w 1287
    //   365: aastore
    //   366: dup
    //   367: bipush 11
    //   369: ldc_w 1289
    //   372: aastore
    //   373: dup
    //   374: bipush 12
    //   376: ldc_w 1291
    //   379: aastore
    //   380: dup
    //   381: bipush 13
    //   383: ldc_w 1293
    //   386: aastore
    //   387: dup
    //   388: bipush 14
    //   390: ldc_w 536
    //   393: aastore
    //   394: dup
    //   395: bipush 15
    //   397: ldc_w 1295
    //   400: aastore
    //   401: dup
    //   402: bipush 16
    //   404: ldc_w 482
    //   407: aastore
    //   408: dup
    //   409: bipush 17
    //   411: ldc_w 1297
    //   414: aastore
    //   415: dup
    //   416: bipush 18
    //   418: ldc_w 1299
    //   421: aastore
    //   422: dup
    //   423: bipush 19
    //   425: ldc_w 479
    //   428: aastore
    //   429: dup
    //   430: bipush 20
    //   432: ldc_w 1301
    //   435: aastore
    //   436: dup
    //   437: bipush 21
    //   439: ldc_w 1303
    //   442: aastore
    //   443: dup
    //   444: bipush 22
    //   446: ldc_w 1305
    //   449: aastore
    //   450: dup
    //   451: bipush 23
    //   453: ldc_w 1307
    //   456: aastore
    //   457: dup
    //   458: bipush 24
    //   460: ldc_w 1309
    //   463: aastore
    //   464: dup
    //   465: bipush 25
    //   467: ldc_w 1311
    //   470: aastore
    //   471: dup
    //   472: bipush 26
    //   474: ldc_w 1313
    //   477: aastore
    //   478: dup
    //   479: bipush 27
    //   481: ldc_w 1315
    //   484: aastore
    //   485: dup
    //   486: bipush 28
    //   488: ldc_w 1317
    //   491: aastore
    //   492: dup
    //   493: bipush 29
    //   495: ldc_w 1319
    //   498: aastore
    //   499: dup
    //   500: bipush 30
    //   502: ldc_w 1321
    //   505: aastore
    //   506: iconst_0
    //   507: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   510: bipush 28
    //   512: istore 4
    //   514: iload 4
    //   516: bipush 28
    //   518: if_icmpeq +14 -> 532
    //   521: iload 4
    //   523: istore 5
    //   525: iload 4
    //   527: bipush 29
    //   529: if_icmpne +60 -> 589
    //   532: aload_1
    //   533: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   536: aload_1
    //   537: ldc_w 1325
    //   540: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   543: aload_1
    //   544: new 170	java/lang/StringBuilder
    //   547: dup
    //   548: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   551: ldc_w 1327
    //   554: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   557: bipush 38
    //   559: invokestatic 1329	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   562: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   565: ldc_w 1331
    //   568: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   571: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   574: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   577: aload_1
    //   578: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   581: aload_1
    //   582: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   585: bipush 30
    //   587: istore 5
    //   589: iload 5
    //   591: istore 4
    //   593: iload 5
    //   595: bipush 30
    //   597: if_icmpne +33 -> 630
    //   600: aload_1
    //   601: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   604: aload_1
    //   605: ldc_w 1333
    //   608: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   611: aload_1
    //   612: ldc_w 1335
    //   615: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   618: aload_1
    //   619: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   622: aload_1
    //   623: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   626: bipush 31
    //   628: istore 4
    //   630: iload 4
    //   632: istore 5
    //   634: iload 4
    //   636: bipush 31
    //   638: if_icmpne +81 -> 719
    //   641: aload_1
    //   642: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   645: aconst_null
    //   646: astore 7
    //   648: aload 7
    //   650: astore 6
    //   652: aload_1
    //   653: ldc_w 1337
    //   656: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   659: aload 7
    //   661: astore 6
    //   663: aload_1
    //   664: ldc_w 1339
    //   667: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   670: aload 7
    //   672: astore 6
    //   674: aload_1
    //   675: ldc_w 1341
    //   678: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   681: astore 7
    //   683: aload 7
    //   685: astore 6
    //   687: aload_0
    //   688: aload 7
    //   690: invokespecial 794	com/android/providers/settings/DatabaseHelper:loadDefaultAnimationSettings	(Landroid/database/sqlite/SQLiteStatement;)V
    //   693: aload 7
    //   695: astore 6
    //   697: aload_1
    //   698: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   701: aload_1
    //   702: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   705: aload 7
    //   707: ifnull +8 -> 715
    //   710: aload 7
    //   712: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   715: bipush 32
    //   717: istore 5
    //   719: iload 5
    //   721: istore 4
    //   723: iload 5
    //   725: bipush 32
    //   727: if_icmpne +66 -> 793
    //   730: ldc_w 639
    //   733: invokestatic 129	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   736: astore 6
    //   738: aload 6
    //   740: invokestatic 268	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   743: ifne +46 -> 789
    //   746: aload_1
    //   747: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   750: aload_1
    //   751: new 170	java/lang/StringBuilder
    //   754: dup
    //   755: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   758: ldc_w 1343
    //   761: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   764: aload 6
    //   766: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   769: ldc_w 1159
    //   772: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   775: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   778: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   781: aload_1
    //   782: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   785: aload_1
    //   786: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   789: bipush 33
    //   791: istore 4
    //   793: iload 4
    //   795: istore 5
    //   797: iload 4
    //   799: bipush 33
    //   801: if_icmpne +26 -> 827
    //   804: aload_1
    //   805: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   808: aload_1
    //   809: ldc_w 1345
    //   812: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   815: aload_1
    //   816: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   819: aload_1
    //   820: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   823: bipush 34
    //   825: istore 5
    //   827: iload 5
    //   829: istore 4
    //   831: iload 5
    //   833: bipush 34
    //   835: if_icmpne +55 -> 890
    //   838: aload_1
    //   839: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   842: aconst_null
    //   843: astore 6
    //   845: aload_1
    //   846: ldc_w 634
    //   849: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   852: astore 7
    //   854: aload 7
    //   856: astore 6
    //   858: aload_0
    //   859: aload 7
    //   861: invokespecial 647	com/android/providers/settings/DatabaseHelper:loadSecure35Settings	(Landroid/database/sqlite/SQLiteStatement;)V
    //   864: aload 7
    //   866: astore 6
    //   868: aload_1
    //   869: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   872: aload_1
    //   873: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   876: aload 7
    //   878: ifnull +8 -> 886
    //   881: aload 7
    //   883: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   886: bipush 35
    //   888: istore 4
    //   890: iload 4
    //   892: istore 5
    //   894: iload 4
    //   896: bipush 35
    //   898: if_icmpne +7 -> 905
    //   901: bipush 36
    //   903: istore 5
    //   905: iload 5
    //   907: istore 4
    //   909: iload 5
    //   911: bipush 36
    //   913: if_icmpne +61 -> 974
    //   916: aload_1
    //   917: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   920: aload_1
    //   921: ldc_w 1325
    //   924: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   927: aload_1
    //   928: new 170	java/lang/StringBuilder
    //   931: dup
    //   932: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   935: ldc_w 1327
    //   938: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   941: sipush 166
    //   944: invokestatic 1329	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   947: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   950: ldc_w 1331
    //   953: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   956: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   959: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   962: aload_1
    //   963: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   966: aload_1
    //   967: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   970: bipush 37
    //   972: istore 4
    //   974: iload 4
    //   976: istore 5
    //   978: iload 4
    //   980: bipush 37
    //   982: if_icmpne +61 -> 1043
    //   985: aload_1
    //   986: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   989: aconst_null
    //   990: astore 6
    //   992: aload_1
    //   993: ldc_w 764
    //   996: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   999: astore 7
    //   1001: aload 7
    //   1003: astore 6
    //   1005: aload_0
    //   1006: aload 7
    //   1008: ldc_w 454
    //   1011: ldc_w 455
    //   1014: invokespecial 452	com/android/providers/settings/DatabaseHelper:loadStringSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   1017: aload 7
    //   1019: astore 6
    //   1021: aload_1
    //   1022: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1025: aload_1
    //   1026: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1029: aload 7
    //   1031: ifnull +8 -> 1039
    //   1034: aload 7
    //   1036: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   1039: bipush 38
    //   1041: istore 5
    //   1043: iload 5
    //   1045: istore 4
    //   1047: iload 5
    //   1049: bipush 38
    //   1051: if_icmpne +71 -> 1122
    //   1054: aload_1
    //   1055: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   1058: aload_0
    //   1059: getfield 56	com/android/providers/settings/DatabaseHelper:mContext	Landroid/content/Context;
    //   1062: invokevirtual 150	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   1065: ldc_w 458
    //   1068: invokevirtual 371	android/content/res/Resources:getBoolean	(I)Z
    //   1071: ifeq +5682 -> 6753
    //   1074: ldc_w 373
    //   1077: astore 6
    //   1079: aload_1
    //   1080: new 170	java/lang/StringBuilder
    //   1083: dup
    //   1084: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   1087: ldc_w 1347
    //   1090: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1093: aload 6
    //   1095: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1098: ldc_w 1159
    //   1101: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1104: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1107: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   1110: aload_1
    //   1111: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1114: aload_1
    //   1115: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1118: bipush 39
    //   1120: istore 4
    //   1122: iload 4
    //   1124: istore 5
    //   1126: iload 4
    //   1128: bipush 39
    //   1130: if_icmpne +12 -> 1142
    //   1133: aload_0
    //   1134: aload_1
    //   1135: invokespecial 1349	com/android/providers/settings/DatabaseHelper:upgradeAutoBrightness	(Landroid/database/sqlite/SQLiteDatabase;)V
    //   1138: bipush 40
    //   1140: istore 5
    //   1142: iload 5
    //   1144: istore 4
    //   1146: iload 5
    //   1148: bipush 40
    //   1150: if_icmpne +81 -> 1231
    //   1153: aload_1
    //   1154: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   1157: aconst_null
    //   1158: astore 7
    //   1160: aload 7
    //   1162: astore 6
    //   1164: aload_1
    //   1165: ldc_w 1337
    //   1168: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   1171: aload 7
    //   1173: astore 6
    //   1175: aload_1
    //   1176: ldc_w 1339
    //   1179: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   1182: aload 7
    //   1184: astore 6
    //   1186: aload_1
    //   1187: ldc_w 1341
    //   1190: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   1193: astore 7
    //   1195: aload 7
    //   1197: astore 6
    //   1199: aload_0
    //   1200: aload 7
    //   1202: invokespecial 794	com/android/providers/settings/DatabaseHelper:loadDefaultAnimationSettings	(Landroid/database/sqlite/SQLiteStatement;)V
    //   1205: aload 7
    //   1207: astore 6
    //   1209: aload_1
    //   1210: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1213: aload_1
    //   1214: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1217: aload 7
    //   1219: ifnull +8 -> 1227
    //   1222: aload 7
    //   1224: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   1227: bipush 41
    //   1229: istore 4
    //   1231: iload 4
    //   1233: istore 5
    //   1235: iload 4
    //   1237: bipush 41
    //   1239: if_icmpne +70 -> 1309
    //   1242: aload_1
    //   1243: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   1246: aconst_null
    //   1247: astore 7
    //   1249: aload 7
    //   1251: astore 6
    //   1253: aload_1
    //   1254: ldc_w 1351
    //   1257: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   1260: aload 7
    //   1262: astore 6
    //   1264: aload_1
    //   1265: ldc_w 1341
    //   1268: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   1271: astore 7
    //   1273: aload 7
    //   1275: astore 6
    //   1277: aload_0
    //   1278: aload 7
    //   1280: invokespecial 799	com/android/providers/settings/DatabaseHelper:loadDefaultHapticSettings	(Landroid/database/sqlite/SQLiteStatement;)V
    //   1283: aload 7
    //   1285: astore 6
    //   1287: aload_1
    //   1288: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1291: aload_1
    //   1292: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1295: aload 7
    //   1297: ifnull +8 -> 1305
    //   1300: aload 7
    //   1302: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   1305: bipush 42
    //   1307: istore 5
    //   1309: iload 5
    //   1311: istore 4
    //   1313: iload 5
    //   1315: bipush 42
    //   1317: if_icmpne +61 -> 1378
    //   1320: aload_1
    //   1321: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   1324: aconst_null
    //   1325: astore 6
    //   1327: aload_1
    //   1328: ldc_w 1341
    //   1331: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   1334: astore 7
    //   1336: aload 7
    //   1338: astore 6
    //   1340: aload_0
    //   1341: aload 7
    //   1343: ldc_w 801
    //   1346: ldc_w 802
    //   1349: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   1352: aload 7
    //   1354: astore 6
    //   1356: aload_1
    //   1357: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1360: aload_1
    //   1361: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1364: aload 7
    //   1366: ifnull +8 -> 1374
    //   1369: aload 7
    //   1371: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   1374: bipush 43
    //   1376: istore 4
    //   1378: iload 4
    //   1380: istore 5
    //   1382: iload 4
    //   1384: bipush 43
    //   1386: if_icmpne +66 -> 1452
    //   1389: aload_1
    //   1390: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   1393: aconst_null
    //   1394: astore 6
    //   1396: aload_1
    //   1397: ldc_w 764
    //   1400: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   1403: astore 7
    //   1405: aload 7
    //   1407: astore 6
    //   1409: aload_0
    //   1410: aload 7
    //   1412: ldc_w 1096
    //   1415: bipush 6
    //   1417: invokestatic 1084	android/media/AudioSystem:getDefaultStreamVolume	(I)I
    //   1420: invokestatic 334	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1423: invokespecial 377	com/android/providers/settings/DatabaseHelper:loadSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;Ljava/lang/Object;)V
    //   1426: aload 7
    //   1428: astore 6
    //   1430: aload_1
    //   1431: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1434: aload_1
    //   1435: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1438: aload 7
    //   1440: ifnull +8 -> 1448
    //   1443: aload 7
    //   1445: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   1448: bipush 44
    //   1450: istore 5
    //   1452: iload 5
    //   1454: istore 4
    //   1456: iload 5
    //   1458: bipush 44
    //   1460: if_icmpne +21 -> 1481
    //   1463: aload_1
    //   1464: ldc_w 1353
    //   1467: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   1470: aload_1
    //   1471: ldc_w 1355
    //   1474: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   1477: bipush 45
    //   1479: istore 4
    //   1481: iload 4
    //   1483: istore 5
    //   1485: iload 4
    //   1487: bipush 45
    //   1489: if_icmpne +47 -> 1536
    //   1492: aload_1
    //   1493: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   1496: aload_1
    //   1497: ldc_w 1357
    //   1500: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   1503: aload_1
    //   1504: ldc_w 1359
    //   1507: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   1510: aload_1
    //   1511: ldc_w 1361
    //   1514: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   1517: aload_1
    //   1518: ldc_w 1363
    //   1521: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   1524: aload_1
    //   1525: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1528: aload_1
    //   1529: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1532: bipush 46
    //   1534: istore 5
    //   1536: iload 5
    //   1538: istore 4
    //   1540: iload 5
    //   1542: bipush 46
    //   1544: if_icmpne +26 -> 1570
    //   1547: aload_1
    //   1548: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   1551: aload_1
    //   1552: ldc_w 1365
    //   1555: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   1558: aload_1
    //   1559: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1562: aload_1
    //   1563: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1566: bipush 47
    //   1568: istore 4
    //   1570: iload 4
    //   1572: istore 5
    //   1574: iload 4
    //   1576: bipush 47
    //   1578: if_icmpne +26 -> 1604
    //   1581: aload_1
    //   1582: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   1585: aload_1
    //   1586: ldc_w 1365
    //   1589: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   1592: aload_1
    //   1593: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1596: aload_1
    //   1597: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1600: bipush 48
    //   1602: istore 5
    //   1604: iload 5
    //   1606: istore 4
    //   1608: iload 5
    //   1610: bipush 48
    //   1612: if_icmpne +7 -> 1619
    //   1615: bipush 49
    //   1617: istore 4
    //   1619: iload 4
    //   1621: istore 5
    //   1623: iload 4
    //   1625: bipush 49
    //   1627: if_icmpne +55 -> 1682
    //   1630: aload_1
    //   1631: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   1634: aconst_null
    //   1635: astore 6
    //   1637: aload_1
    //   1638: ldc_w 1341
    //   1641: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   1644: astore 7
    //   1646: aload 7
    //   1648: astore 6
    //   1650: aload_0
    //   1651: aload 7
    //   1653: invokespecial 809	com/android/providers/settings/DatabaseHelper:loadUISoundEffectsSettings	(Landroid/database/sqlite/SQLiteStatement;)V
    //   1656: aload 7
    //   1658: astore 6
    //   1660: aload_1
    //   1661: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1664: aload_1
    //   1665: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1668: aload 7
    //   1670: ifnull +8 -> 1678
    //   1673: aload 7
    //   1675: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   1678: bipush 50
    //   1680: istore 5
    //   1682: iload 5
    //   1684: istore 4
    //   1686: iload 5
    //   1688: bipush 50
    //   1690: if_icmpne +7 -> 1697
    //   1693: bipush 51
    //   1695: istore 4
    //   1697: iload 4
    //   1699: istore 5
    //   1701: iload 4
    //   1703: bipush 51
    //   1705: if_icmpne +79 -> 1784
    //   1708: aload_0
    //   1709: aload_1
    //   1710: ldc 25
    //   1712: ldc 31
    //   1714: bipush 9
    //   1716: anewarray 184	java/lang/String
    //   1719: dup
    //   1720: iconst_0
    //   1721: ldc_w 1367
    //   1724: aastore
    //   1725: dup
    //   1726: iconst_1
    //   1727: ldc_w 1369
    //   1730: aastore
    //   1731: dup
    //   1732: iconst_2
    //   1733: ldc_w 1371
    //   1736: aastore
    //   1737: dup
    //   1738: iconst_3
    //   1739: ldc_w 1373
    //   1742: aastore
    //   1743: dup
    //   1744: iconst_4
    //   1745: ldc_w 1375
    //   1748: aastore
    //   1749: dup
    //   1750: iconst_5
    //   1751: ldc_w 1377
    //   1754: aastore
    //   1755: dup
    //   1756: bipush 6
    //   1758: ldc_w 1367
    //   1761: aastore
    //   1762: dup
    //   1763: bipush 7
    //   1765: ldc_w 1379
    //   1768: aastore
    //   1769: dup
    //   1770: bipush 8
    //   1772: ldc_w 1381
    //   1775: aastore
    //   1776: iconst_0
    //   1777: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   1780: bipush 52
    //   1782: istore 5
    //   1784: iload 5
    //   1786: istore 4
    //   1788: iload 5
    //   1790: bipush 52
    //   1792: if_icmpne +61 -> 1853
    //   1795: aload_1
    //   1796: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   1799: aconst_null
    //   1800: astore 6
    //   1802: aload_1
    //   1803: ldc_w 1341
    //   1806: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   1809: astore 7
    //   1811: aload 7
    //   1813: astore 6
    //   1815: aload_0
    //   1816: aload 7
    //   1818: ldc_w 1383
    //   1821: ldc_w 1384
    //   1824: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   1827: aload 7
    //   1829: astore 6
    //   1831: aload_1
    //   1832: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1835: aload_1
    //   1836: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1839: aload 7
    //   1841: ifnull +8 -> 1849
    //   1844: aload 7
    //   1846: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   1849: bipush 53
    //   1851: istore 4
    //   1853: iload 4
    //   1855: istore 5
    //   1857: iload 4
    //   1859: bipush 53
    //   1861: if_icmpne +7 -> 1868
    //   1864: bipush 54
    //   1866: istore 5
    //   1868: iload 5
    //   1870: istore 4
    //   1872: iload 5
    //   1874: bipush 54
    //   1876: if_icmpne +24 -> 1900
    //   1879: aload_1
    //   1880: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   1883: aload_0
    //   1884: aload_1
    //   1885: invokespecial 1386	com/android/providers/settings/DatabaseHelper:upgradeScreenTimeoutFromNever	(Landroid/database/sqlite/SQLiteDatabase;)V
    //   1888: aload_1
    //   1889: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1892: aload_1
    //   1893: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1896: bipush 55
    //   1898: istore 4
    //   1900: iload 4
    //   1902: istore 5
    //   1904: iload 4
    //   1906: bipush 55
    //   1908: if_icmpne +105 -> 2013
    //   1911: aload_0
    //   1912: aload_1
    //   1913: ldc 25
    //   1915: ldc 31
    //   1917: iconst_2
    //   1918: anewarray 184	java/lang/String
    //   1921: dup
    //   1922: iconst_0
    //   1923: ldc_w 581
    //   1926: aastore
    //   1927: dup
    //   1928: iconst_1
    //   1929: ldc_w 583
    //   1932: aastore
    //   1933: iconst_0
    //   1934: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   1937: aload_1
    //   1938: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   1941: aconst_null
    //   1942: astore 6
    //   1944: aload_1
    //   1945: ldc_w 1341
    //   1948: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   1951: astore 7
    //   1953: aload 7
    //   1955: astore 6
    //   1957: aload_0
    //   1958: aload 7
    //   1960: ldc_w 581
    //   1963: iconst_0
    //   1964: invokestatic 334	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1967: invokespecial 377	com/android/providers/settings/DatabaseHelper:loadSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;Ljava/lang/Object;)V
    //   1970: aload 7
    //   1972: astore 6
    //   1974: aload_0
    //   1975: aload 7
    //   1977: ldc_w 583
    //   1980: iconst_0
    //   1981: invokestatic 334	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1984: invokespecial 377	com/android/providers/settings/DatabaseHelper:loadSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;Ljava/lang/Object;)V
    //   1987: aload 7
    //   1989: astore 6
    //   1991: aload_1
    //   1992: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   1995: aload_1
    //   1996: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   1999: aload 7
    //   2001: ifnull +8 -> 2009
    //   2004: aload 7
    //   2006: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   2009: bipush 56
    //   2011: istore 5
    //   2013: iload 5
    //   2015: istore 4
    //   2017: iload 5
    //   2019: bipush 56
    //   2021: if_icmpne +76 -> 2097
    //   2024: aload_1
    //   2025: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   2028: aconst_null
    //   2029: astore 7
    //   2031: aload 7
    //   2033: astore 6
    //   2035: aload_1
    //   2036: ldc_w 1388
    //   2039: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   2042: aload 7
    //   2044: astore 6
    //   2046: aload_1
    //   2047: ldc_w 764
    //   2050: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   2053: astore 7
    //   2055: aload 7
    //   2057: astore 6
    //   2059: aload_0
    //   2060: aload 7
    //   2062: ldc_w 454
    //   2065: ldc_w 455
    //   2068: invokespecial 452	com/android/providers/settings/DatabaseHelper:loadStringSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   2071: aload 7
    //   2073: astore 6
    //   2075: aload_1
    //   2076: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   2079: aload_1
    //   2080: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   2083: aload 7
    //   2085: ifnull +8 -> 2093
    //   2088: aload 7
    //   2090: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   2093: bipush 57
    //   2095: istore 4
    //   2097: iload 4
    //   2099: istore 5
    //   2101: iload 4
    //   2103: bipush 57
    //   2105: if_icmpne +99 -> 2204
    //   2108: aload_1
    //   2109: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   2112: aconst_null
    //   2113: astore 6
    //   2115: aload_1
    //   2116: ldc_w 1390
    //   2119: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   2122: astore 7
    //   2124: aload 7
    //   2126: astore 6
    //   2128: aload_0
    //   2129: aload 7
    //   2131: ldc_w 661
    //   2134: ldc_w 662
    //   2137: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   2140: aload 7
    //   2142: astore 6
    //   2144: aload 7
    //   2146: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   2149: aload 7
    //   2151: astore 6
    //   2153: aload_1
    //   2154: ldc_w 1390
    //   2157: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   2160: astore 7
    //   2162: aload 7
    //   2164: astore 6
    //   2166: aload_0
    //   2167: aload 7
    //   2169: ldc_w 664
    //   2172: ldc_w 665
    //   2175: invokespecial 452	com/android/providers/settings/DatabaseHelper:loadStringSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   2178: aload 7
    //   2180: astore 6
    //   2182: aload_1
    //   2183: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   2186: aload_1
    //   2187: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   2190: aload 7
    //   2192: ifnull +8 -> 2200
    //   2195: aload 7
    //   2197: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   2200: bipush 58
    //   2202: istore 5
    //   2204: iload 5
    //   2206: istore 4
    //   2208: iload 5
    //   2210: bipush 58
    //   2212: if_icmpne +74 -> 2286
    //   2215: aload_0
    //   2216: aload_1
    //   2217: ldc_w 460
    //   2220: iconst_0
    //   2221: invokespecial 1075	com/android/providers/settings/DatabaseHelper:getIntValueFromSystem	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;I)I
    //   2224: istore 4
    //   2226: aload_1
    //   2227: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   2230: aconst_null
    //   2231: astore 6
    //   2233: aload_1
    //   2234: ldc_w 1341
    //   2237: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   2240: astore 7
    //   2242: aload 7
    //   2244: astore 6
    //   2246: aload_0
    //   2247: aload 7
    //   2249: ldc_w 463
    //   2252: iload 4
    //   2254: invokestatic 334	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2257: invokespecial 377	com/android/providers/settings/DatabaseHelper:loadSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;Ljava/lang/Object;)V
    //   2260: aload 7
    //   2262: astore 6
    //   2264: aload_1
    //   2265: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   2268: aload_1
    //   2269: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   2272: aload 7
    //   2274: ifnull +8 -> 2282
    //   2277: aload 7
    //   2279: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   2282: bipush 59
    //   2284: istore 4
    //   2286: iload 4
    //   2288: istore 5
    //   2290: iload 4
    //   2292: bipush 59
    //   2294: if_icmpne +61 -> 2355
    //   2297: aload_1
    //   2298: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   2301: aconst_null
    //   2302: astore 6
    //   2304: aload_1
    //   2305: ldc_w 1341
    //   2308: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   2311: astore 7
    //   2313: aload 7
    //   2315: astore 6
    //   2317: aload_0
    //   2318: aload 7
    //   2320: ldc_w 1392
    //   2323: ldc_w 1393
    //   2326: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   2329: aload 7
    //   2331: astore 6
    //   2333: aload_1
    //   2334: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   2337: aload_1
    //   2338: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   2341: aload 7
    //   2343: ifnull +8 -> 2351
    //   2346: aload 7
    //   2348: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   2351: bipush 60
    //   2353: istore 5
    //   2355: iload 5
    //   2357: istore 4
    //   2359: iload 5
    //   2361: bipush 60
    //   2363: if_icmpne +7 -> 2370
    //   2366: bipush 61
    //   2368: istore 4
    //   2370: iload 4
    //   2372: istore 5
    //   2374: iload 4
    //   2376: bipush 61
    //   2378: if_icmpne +7 -> 2385
    //   2381: bipush 62
    //   2383: istore 5
    //   2385: iload 5
    //   2387: istore 4
    //   2389: iload 5
    //   2391: bipush 62
    //   2393: if_icmpne +7 -> 2400
    //   2396: bipush 63
    //   2398: istore 4
    //   2400: iload 4
    //   2402: istore 5
    //   2404: iload 4
    //   2406: bipush 63
    //   2408: if_icmpne +61 -> 2469
    //   2411: aload_1
    //   2412: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   2415: aload_1
    //   2416: ldc_w 1325
    //   2419: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   2422: aload_1
    //   2423: new 170	java/lang/StringBuilder
    //   2426: dup
    //   2427: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   2430: ldc_w 1327
    //   2433: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2436: sipush 174
    //   2439: invokestatic 1329	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   2442: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2445: ldc_w 1331
    //   2448: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2451: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2454: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   2457: aload_1
    //   2458: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   2461: aload_1
    //   2462: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   2465: bipush 64
    //   2467: istore 5
    //   2469: iload 5
    //   2471: istore 4
    //   2473: iload 5
    //   2475: bipush 64
    //   2477: if_icmpne +70 -> 2547
    //   2480: aload_1
    //   2481: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   2484: aconst_null
    //   2485: astore 6
    //   2487: aload_1
    //   2488: ldc_w 1390
    //   2491: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   2494: astore 7
    //   2496: aload 7
    //   2498: astore 6
    //   2500: aload_0
    //   2501: aload 7
    //   2503: ldc_w 667
    //   2506: ldc_w 668
    //   2509: invokespecial 419	com/android/providers/settings/DatabaseHelper:loadIntegerSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   2512: aload 7
    //   2514: astore 6
    //   2516: aload 7
    //   2518: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   2521: aload 7
    //   2523: astore 6
    //   2525: aload_1
    //   2526: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   2529: aload_1
    //   2530: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   2533: aload 7
    //   2535: ifnull +8 -> 2543
    //   2538: aload 7
    //   2540: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   2543: bipush 65
    //   2545: istore 4
    //   2547: iload 4
    //   2549: istore 5
    //   2551: iload 4
    //   2553: bipush 65
    //   2555: if_icmpne +81 -> 2636
    //   2558: aload_1
    //   2559: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   2562: aconst_null
    //   2563: astore 7
    //   2565: aload 7
    //   2567: astore 6
    //   2569: aload_1
    //   2570: ldc_w 1337
    //   2573: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   2576: aload 7
    //   2578: astore 6
    //   2580: aload_1
    //   2581: ldc_w 1339
    //   2584: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   2587: aload 7
    //   2589: astore 6
    //   2591: aload_1
    //   2592: ldc_w 1341
    //   2595: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   2598: astore 7
    //   2600: aload 7
    //   2602: astore 6
    //   2604: aload_0
    //   2605: aload 7
    //   2607: invokespecial 794	com/android/providers/settings/DatabaseHelper:loadDefaultAnimationSettings	(Landroid/database/sqlite/SQLiteStatement;)V
    //   2610: aload 7
    //   2612: astore 6
    //   2614: aload_1
    //   2615: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   2618: aload_1
    //   2619: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   2622: aload 7
    //   2624: ifnull +8 -> 2632
    //   2627: aload 7
    //   2629: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   2632: bipush 66
    //   2634: istore 5
    //   2636: iload 5
    //   2638: istore 4
    //   2640: iload 5
    //   2642: bipush 66
    //   2644: if_icmpne +86 -> 2730
    //   2647: aload_1
    //   2648: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   2651: sipush 166
    //   2654: istore 4
    //   2656: aload_0
    //   2657: getfield 56	com/android/providers/settings/DatabaseHelper:mContext	Landroid/content/Context;
    //   2660: invokevirtual 150	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   2663: ldc_w 1097
    //   2666: invokevirtual 371	android/content/res/Resources:getBoolean	(I)Z
    //   2669: ifne +8 -> 2677
    //   2672: sipush 174
    //   2675: istore 4
    //   2677: aload_1
    //   2678: ldc_w 1325
    //   2681: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   2684: aload_1
    //   2685: new 170	java/lang/StringBuilder
    //   2688: dup
    //   2689: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   2692: ldc_w 1327
    //   2695: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2698: iload 4
    //   2700: invokestatic 1329	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   2703: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2706: ldc_w 1331
    //   2709: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2712: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2715: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   2718: aload_1
    //   2719: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   2722: aload_1
    //   2723: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   2726: bipush 67
    //   2728: istore 4
    //   2730: iload 4
    //   2732: istore 5
    //   2734: iload 4
    //   2736: bipush 67
    //   2738: if_icmpne +70 -> 2808
    //   2741: aload_1
    //   2742: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   2745: aconst_null
    //   2746: astore 6
    //   2748: aload_1
    //   2749: ldc_w 1390
    //   2752: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   2755: astore 7
    //   2757: aload 7
    //   2759: astore 6
    //   2761: aload_0
    //   2762: aload 7
    //   2764: ldc_w 670
    //   2767: ldc_w 671
    //   2770: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   2773: aload 7
    //   2775: astore 6
    //   2777: aload 7
    //   2779: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   2782: aload 7
    //   2784: astore 6
    //   2786: aload_1
    //   2787: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   2790: aload_1
    //   2791: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   2794: aload 7
    //   2796: ifnull +8 -> 2804
    //   2799: aload 7
    //   2801: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   2804: bipush 68
    //   2806: istore 5
    //   2808: iload 5
    //   2810: istore 4
    //   2812: iload 5
    //   2814: bipush 68
    //   2816: if_icmpne +26 -> 2842
    //   2819: aload_1
    //   2820: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   2823: aload_1
    //   2824: ldc_w 1395
    //   2827: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   2830: aload_1
    //   2831: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   2834: aload_1
    //   2835: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   2838: bipush 69
    //   2840: istore 4
    //   2842: iload 4
    //   2844: istore 5
    //   2846: iload 4
    //   2848: bipush 69
    //   2850: if_icmpne +145 -> 2995
    //   2853: aload_0
    //   2854: getfield 56	com/android/providers/settings/DatabaseHelper:mContext	Landroid/content/Context;
    //   2857: invokevirtual 150	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   2860: ldc_w 449
    //   2863: invokevirtual 762	android/content/res/Resources:getString	(I)Ljava/lang/String;
    //   2866: astore 6
    //   2868: aload_0
    //   2869: getfield 56	com/android/providers/settings/DatabaseHelper:mContext	Landroid/content/Context;
    //   2872: invokevirtual 150	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   2875: ldc_w 455
    //   2878: invokevirtual 762	android/content/res/Resources:getString	(I)Ljava/lang/String;
    //   2881: astore 7
    //   2883: aload_1
    //   2884: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   2887: aload_1
    //   2888: new 170	java/lang/StringBuilder
    //   2891: dup
    //   2892: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   2895: ldc_w 1397
    //   2898: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2901: aload 6
    //   2903: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2906: ldc_w 1399
    //   2909: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2912: ldc_w 1401
    //   2915: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2918: ldc_w 448
    //   2921: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2924: ldc -77
    //   2926: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2929: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2932: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   2935: aload_1
    //   2936: new 170	java/lang/StringBuilder
    //   2939: dup
    //   2940: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   2943: ldc_w 1397
    //   2946: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2949: aload 7
    //   2951: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2954: ldc_w 1399
    //   2957: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2960: ldc_w 1401
    //   2963: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2966: ldc_w 454
    //   2969: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2972: ldc -77
    //   2974: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2977: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2980: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   2983: aload_1
    //   2984: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   2987: aload_1
    //   2988: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   2991: bipush 70
    //   2993: istore 5
    //   2995: iload 5
    //   2997: istore 4
    //   2999: iload 5
    //   3001: bipush 70
    //   3003: if_icmpne +12 -> 3015
    //   3006: aload_0
    //   3007: aload_1
    //   3008: invokespecial 1241	com/android/providers/settings/DatabaseHelper:loadBookmarks	(Landroid/database/sqlite/SQLiteDatabase;)V
    //   3011: bipush 71
    //   3013: istore 4
    //   3015: iload 4
    //   3017: istore 5
    //   3019: iload 4
    //   3021: bipush 71
    //   3023: if_icmpne +61 -> 3084
    //   3026: aload_1
    //   3027: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   3030: aconst_null
    //   3031: astore 6
    //   3033: aload_1
    //   3034: ldc_w 1390
    //   3037: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   3040: astore 7
    //   3042: aload 7
    //   3044: astore 6
    //   3046: aload_0
    //   3047: aload 7
    //   3049: ldc_w 673
    //   3052: ldc_w 674
    //   3055: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   3058: aload 7
    //   3060: astore 6
    //   3062: aload_1
    //   3063: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   3066: aload_1
    //   3067: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   3070: aload 7
    //   3072: ifnull +8 -> 3080
    //   3075: aload 7
    //   3077: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   3080: bipush 72
    //   3082: istore 5
    //   3084: iload 5
    //   3086: istore 4
    //   3088: iload 5
    //   3090: bipush 72
    //   3092: if_icmpne +61 -> 3153
    //   3095: aload_1
    //   3096: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   3099: aconst_null
    //   3100: astore 6
    //   3102: aload_1
    //   3103: ldc_w 1189
    //   3106: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   3109: astore 7
    //   3111: aload 7
    //   3113: astore 6
    //   3115: aload_0
    //   3116: aload 7
    //   3118: ldc_w 1383
    //   3121: ldc_w 1384
    //   3124: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   3127: aload 7
    //   3129: astore 6
    //   3131: aload_1
    //   3132: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   3135: aload_1
    //   3136: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   3139: aload 7
    //   3141: ifnull +8 -> 3149
    //   3144: aload 7
    //   3146: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   3149: bipush 73
    //   3151: istore 4
    //   3153: iload 4
    //   3155: istore 5
    //   3157: iload 4
    //   3159: bipush 73
    //   3161: if_icmpne +12 -> 3173
    //   3164: aload_0
    //   3165: aload_1
    //   3166: invokespecial 1403	com/android/providers/settings/DatabaseHelper:upgradeVibrateSettingFromNone	(Landroid/database/sqlite/SQLiteDatabase;)V
    //   3169: bipush 74
    //   3171: istore 5
    //   3173: iload 5
    //   3175: istore 4
    //   3177: iload 5
    //   3179: bipush 74
    //   3181: if_icmpne +61 -> 3242
    //   3184: aload_1
    //   3185: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   3188: aconst_null
    //   3189: astore 6
    //   3191: aload_1
    //   3192: ldc_w 1390
    //   3195: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   3198: astore 7
    //   3200: aload 7
    //   3202: astore 6
    //   3204: aload_0
    //   3205: aload 7
    //   3207: ldc_w 676
    //   3210: ldc_w 677
    //   3213: invokespecial 452	com/android/providers/settings/DatabaseHelper:loadStringSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   3216: aload 7
    //   3218: astore 6
    //   3220: aload_1
    //   3221: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   3224: aload_1
    //   3225: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   3228: aload 7
    //   3230: ifnull +8 -> 3238
    //   3233: aload 7
    //   3235: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   3238: bipush 75
    //   3240: istore 4
    //   3242: iload 4
    //   3244: istore 5
    //   3246: iload 4
    //   3248: bipush 75
    //   3250: if_icmpne +152 -> 3402
    //   3253: aload_1
    //   3254: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   3257: aconst_null
    //   3258: astore 10
    //   3260: aconst_null
    //   3261: astore 7
    //   3263: aconst_null
    //   3264: astore 8
    //   3266: aload 10
    //   3268: astore 6
    //   3270: aload_1
    //   3271: ldc 31
    //   3273: iconst_2
    //   3274: anewarray 184	java/lang/String
    //   3277: dup
    //   3278: iconst_0
    //   3279: ldc_w 1164
    //   3282: aastore
    //   3283: dup
    //   3284: iconst_1
    //   3285: ldc -70
    //   3287: aastore
    //   3288: ldc_w 1405
    //   3291: aconst_null
    //   3292: aconst_null
    //   3293: aconst_null
    //   3294: aconst_null
    //   3295: invokevirtual 190	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   3298: astore 9
    //   3300: aload 9
    //   3302: ifnull +21 -> 3323
    //   3305: aload 9
    //   3307: astore 8
    //   3309: aload 10
    //   3311: astore 6
    //   3313: aload 9
    //   3315: invokeinterface 1169 1 0
    //   3320: ifne +40 -> 3360
    //   3323: aload 9
    //   3325: astore 8
    //   3327: aload 10
    //   3329: astore 6
    //   3331: aload_1
    //   3332: ldc_w 1341
    //   3335: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   3338: astore 7
    //   3340: aload 9
    //   3342: astore 8
    //   3344: aload 7
    //   3346: astore 6
    //   3348: aload_0
    //   3349: aload 7
    //   3351: ldc_w 684
    //   3354: ldc_w 745
    //   3357: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   3360: aload 9
    //   3362: astore 8
    //   3364: aload 7
    //   3366: astore 6
    //   3368: aload_1
    //   3369: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   3372: aload_1
    //   3373: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   3376: aload 9
    //   3378: ifnull +10 -> 3388
    //   3381: aload 9
    //   3383: invokeinterface 200 1 0
    //   3388: aload 7
    //   3390: ifnull +8 -> 3398
    //   3393: aload 7
    //   3395: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   3398: bipush 76
    //   3400: istore 5
    //   3402: iload 5
    //   3404: istore 4
    //   3406: iload 5
    //   3408: bipush 76
    //   3410: if_icmpne +26 -> 3436
    //   3413: aload_1
    //   3414: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   3417: aload_1
    //   3418: ldc_w 1407
    //   3421: invokevirtual 82	android/database/sqlite/SQLiteDatabase:execSQL	(Ljava/lang/String;)V
    //   3424: aload_1
    //   3425: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   3428: aload_1
    //   3429: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   3432: bipush 77
    //   3434: istore 4
    //   3436: iload 4
    //   3438: istore 5
    //   3440: iload 4
    //   3442: bipush 77
    //   3444: if_icmpne +12 -> 3456
    //   3447: aload_0
    //   3448: aload_1
    //   3449: invokespecial 1103	com/android/providers/settings/DatabaseHelper:loadVibrateWhenRingingSetting	(Landroid/database/sqlite/SQLiteDatabase;)V
    //   3452: bipush 78
    //   3454: istore 5
    //   3456: iload 5
    //   3458: istore 4
    //   3460: iload 5
    //   3462: bipush 78
    //   3464: if_icmpne +61 -> 3525
    //   3467: aload_1
    //   3468: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   3471: aconst_null
    //   3472: astore 6
    //   3474: aload_1
    //   3475: ldc_w 1409
    //   3478: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   3481: astore 7
    //   3483: aload 7
    //   3485: astore 6
    //   3487: aload_0
    //   3488: aload 7
    //   3490: ldc_w 676
    //   3493: ldc_w 677
    //   3496: invokespecial 452	com/android/providers/settings/DatabaseHelper:loadStringSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   3499: aload 7
    //   3501: astore 6
    //   3503: aload_1
    //   3504: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   3507: aload_1
    //   3508: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   3511: aload 7
    //   3513: ifnull +8 -> 3521
    //   3516: aload 7
    //   3518: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   3521: bipush 79
    //   3523: istore 4
    //   3525: iload 4
    //   3527: istore 5
    //   3529: iload 4
    //   3531: bipush 79
    //   3533: if_icmpne +95 -> 3628
    //   3536: aload_0
    //   3537: aload_1
    //   3538: ldc 31
    //   3540: ldc_w 1411
    //   3543: iconst_0
    //   3544: invokespecial 135	com/android/providers/settings/DatabaseHelper:getIntValueFromTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;I)I
    //   3547: iconst_1
    //   3548: if_icmpne +3676 -> 7224
    //   3551: iconst_1
    //   3552: istore 4
    //   3554: aload_0
    //   3555: aload_1
    //   3556: ldc 31
    //   3558: ldc_w 670
    //   3561: iconst_0
    //   3562: invokespecial 135	com/android/providers/settings/DatabaseHelper:getIntValueFromTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;I)I
    //   3565: iconst_1
    //   3566: if_icmpne +3664 -> 7230
    //   3569: iconst_1
    //   3570: istore 5
    //   3572: iload 4
    //   3574: ifeq +50 -> 3624
    //   3577: iload 5
    //   3579: ifeq +45 -> 3624
    //   3582: aload_0
    //   3583: aload_1
    //   3584: ldc 31
    //   3586: ldc_w 1413
    //   3589: ldc_w 361
    //   3592: invokespecial 139	com/android/providers/settings/DatabaseHelper:getStringValueFromTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   3595: astore 8
    //   3597: aload_0
    //   3598: aload_1
    //   3599: ldc 31
    //   3601: ldc_w 1415
    //   3604: ldc_w 361
    //   3607: invokespecial 139	com/android/providers/settings/DatabaseHelper:getStringValueFromTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   3610: invokestatic 268	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   3613: ifeq +11 -> 3624
    //   3616: aload 8
    //   3618: invokestatic 268	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   3621: ifeq +3615 -> 7236
    //   3624: bipush 80
    //   3626: istore 5
    //   3628: iload 5
    //   3630: istore 4
    //   3632: iload 5
    //   3634: bipush 80
    //   3636: if_icmpne +125 -> 3761
    //   3639: aload_1
    //   3640: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   3643: aconst_null
    //   3644: astore 6
    //   3646: aload_1
    //   3647: ldc_w 1409
    //   3650: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   3653: astore 7
    //   3655: aload 7
    //   3657: astore 6
    //   3659: aload_0
    //   3660: aload 7
    //   3662: ldc_w 686
    //   3665: ldc_w 687
    //   3668: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   3671: aload 7
    //   3673: astore 6
    //   3675: aload_0
    //   3676: aload 7
    //   3678: ldc_w 689
    //   3681: ldc_w 690
    //   3684: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   3687: aload 7
    //   3689: astore 6
    //   3691: aload_0
    //   3692: aload 7
    //   3694: ldc_w 692
    //   3697: ldc_w 693
    //   3700: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   3703: aload 7
    //   3705: astore 6
    //   3707: aload_0
    //   3708: aload 7
    //   3710: ldc_w 695
    //   3713: ldc_w 696
    //   3716: invokespecial 452	com/android/providers/settings/DatabaseHelper:loadStringSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   3719: aload 7
    //   3721: astore 6
    //   3723: aload_0
    //   3724: aload 7
    //   3726: ldc_w 698
    //   3729: ldc_w 696
    //   3732: invokespecial 452	com/android/providers/settings/DatabaseHelper:loadStringSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   3735: aload 7
    //   3737: astore 6
    //   3739: aload_1
    //   3740: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   3743: aload_1
    //   3744: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   3747: aload 7
    //   3749: ifnull +8 -> 3757
    //   3752: aload 7
    //   3754: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   3757: bipush 81
    //   3759: istore 4
    //   3761: iload 4
    //   3763: istore 5
    //   3765: iload 4
    //   3767: bipush 81
    //   3769: if_icmpne +61 -> 3830
    //   3772: aload_1
    //   3773: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   3776: aconst_null
    //   3777: astore 6
    //   3779: aload_1
    //   3780: ldc_w 1409
    //   3783: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   3786: astore 7
    //   3788: aload 7
    //   3790: astore 6
    //   3792: aload_0
    //   3793: aload 7
    //   3795: ldc_w 476
    //   3798: ldc_w 477
    //   3801: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   3804: aload 7
    //   3806: astore 6
    //   3808: aload_1
    //   3809: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   3812: aload_1
    //   3813: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   3816: aload 7
    //   3818: ifnull +8 -> 3826
    //   3821: aload 7
    //   3823: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   3826: bipush 82
    //   3828: istore 5
    //   3830: iload 5
    //   3832: istore 4
    //   3834: iload 5
    //   3836: bipush 82
    //   3838: if_icmpne +65 -> 3903
    //   3841: aload_0
    //   3842: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   3845: ifne +54 -> 3899
    //   3848: aload_1
    //   3849: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   3852: aload_0
    //   3853: aload_1
    //   3854: invokespecial 1214	com/android/providers/settings/DatabaseHelper:createGlobalTable	(Landroid/database/sqlite/SQLiteDatabase;)V
    //   3857: aload_0
    //   3858: aload_1
    //   3859: ldc 25
    //   3861: ldc 33
    //   3863: aload_0
    //   3864: getstatic 1421	com/android/providers/settings/SettingsProvider:sSystemMovedToGlobalSettings	Ljava/util/Set;
    //   3867: invokespecial 1423	com/android/providers/settings/DatabaseHelper:setToStringArray	(Ljava/util/Set;)[Ljava/lang/String;
    //   3870: iconst_0
    //   3871: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   3874: aload_0
    //   3875: aload_1
    //   3876: ldc 31
    //   3878: ldc 33
    //   3880: aload_0
    //   3881: getstatic 1426	com/android/providers/settings/SettingsProvider:sSecureMovedToGlobalSettings	Ljava/util/Set;
    //   3884: invokespecial 1423	com/android/providers/settings/DatabaseHelper:setToStringArray	(Ljava/util/Set;)[Ljava/lang/String;
    //   3887: iconst_0
    //   3888: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   3891: aload_1
    //   3892: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   3895: aload_1
    //   3896: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   3899: bipush 83
    //   3901: istore 4
    //   3903: iload 4
    //   3905: istore 5
    //   3907: iload 4
    //   3909: bipush 83
    //   3911: if_icmpne +138 -> 4049
    //   3914: aload_1
    //   3915: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   3918: aconst_null
    //   3919: astore 6
    //   3921: aload_1
    //   3922: ldc_w 1390
    //   3925: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   3928: astore 7
    //   3930: aload 7
    //   3932: astore 6
    //   3934: aload_0
    //   3935: aload 7
    //   3937: ldc_w 700
    //   3940: ldc_w 701
    //   3943: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   3946: aload 7
    //   3948: astore 6
    //   3950: aload 7
    //   3952: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   3955: aload 7
    //   3957: astore 6
    //   3959: aload_1
    //   3960: ldc_w 1390
    //   3963: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   3966: astore 7
    //   3968: aload 7
    //   3970: astore 6
    //   3972: aload_0
    //   3973: aload 7
    //   3975: ldc_w 703
    //   3978: ldc_w 704
    //   3981: iconst_1
    //   3982: invokespecial 388	com/android/providers/settings/DatabaseHelper:loadFractionSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;II)V
    //   3985: aload 7
    //   3987: astore 6
    //   3989: aload 7
    //   3991: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   3994: aload 7
    //   3996: astore 6
    //   3998: aload_1
    //   3999: ldc_w 1390
    //   4002: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   4005: astore 7
    //   4007: aload 7
    //   4009: astore 6
    //   4011: aload_0
    //   4012: aload 7
    //   4014: ldc_w 706
    //   4017: ldc_w 707
    //   4020: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   4023: aload 7
    //   4025: astore 6
    //   4027: aload_1
    //   4028: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   4031: aload_1
    //   4032: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   4035: aload 7
    //   4037: ifnull +8 -> 4045
    //   4040: aload 7
    //   4042: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   4045: bipush 84
    //   4047: istore 5
    //   4049: iload 5
    //   4051: istore 4
    //   4053: iload 5
    //   4055: bipush 84
    //   4057: if_icmpne +77 -> 4134
    //   4060: aload_0
    //   4061: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   4064: ifne +66 -> 4130
    //   4067: aload_1
    //   4068: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   4071: aload_0
    //   4072: aload_1
    //   4073: ldc 31
    //   4075: ldc 33
    //   4077: bipush 6
    //   4079: anewarray 184	java/lang/String
    //   4082: dup
    //   4083: iconst_0
    //   4084: ldc_w 1277
    //   4087: aastore
    //   4088: dup
    //   4089: iconst_1
    //   4090: ldc_w 485
    //   4093: aastore
    //   4094: dup
    //   4095: iconst_2
    //   4096: ldc_w 499
    //   4099: aastore
    //   4100: dup
    //   4101: iconst_3
    //   4102: ldc_w 502
    //   4105: aastore
    //   4106: dup
    //   4107: iconst_4
    //   4108: ldc_w 715
    //   4111: aastore
    //   4112: dup
    //   4113: iconst_5
    //   4114: ldc_w 536
    //   4117: aastore
    //   4118: iconst_1
    //   4119: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   4122: aload_1
    //   4123: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   4126: aload_1
    //   4127: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   4130: bipush 85
    //   4132: istore 4
    //   4134: iload 4
    //   4136: istore 5
    //   4138: iload 4
    //   4140: bipush 85
    //   4142: if_icmpne +46 -> 4188
    //   4145: aload_0
    //   4146: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   4149: ifne +35 -> 4184
    //   4152: aload_1
    //   4153: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   4156: aload_0
    //   4157: aload_1
    //   4158: ldc 25
    //   4160: ldc 33
    //   4162: iconst_1
    //   4163: anewarray 184	java/lang/String
    //   4166: dup
    //   4167: iconst_0
    //   4168: ldc_w 469
    //   4171: aastore
    //   4172: iconst_1
    //   4173: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   4176: aload_1
    //   4177: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   4180: aload_1
    //   4181: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   4184: bipush 86
    //   4186: istore 5
    //   4188: iload 5
    //   4190: istore 4
    //   4192: iload 5
    //   4194: bipush 86
    //   4196: if_icmpne +58 -> 4254
    //   4199: aload_0
    //   4200: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   4203: ifne +47 -> 4250
    //   4206: aload_1
    //   4207: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   4210: aload_0
    //   4211: aload_1
    //   4212: ldc 31
    //   4214: ldc 33
    //   4216: iconst_3
    //   4217: anewarray 184	java/lang/String
    //   4220: dup
    //   4221: iconst_0
    //   4222: ldc_w 476
    //   4225: aastore
    //   4226: dup
    //   4227: iconst_1
    //   4228: ldc_w 1428
    //   4231: aastore
    //   4232: dup
    //   4233: iconst_2
    //   4234: ldc_w 1430
    //   4237: aastore
    //   4238: iconst_1
    //   4239: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   4242: aload_1
    //   4243: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   4246: aload_1
    //   4247: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   4250: bipush 87
    //   4252: istore 4
    //   4254: iload 4
    //   4256: istore 5
    //   4258: iload 4
    //   4260: bipush 87
    //   4262: if_icmpne +58 -> 4320
    //   4265: aload_0
    //   4266: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   4269: ifne +47 -> 4316
    //   4272: aload_1
    //   4273: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   4276: aload_0
    //   4277: aload_1
    //   4278: ldc 31
    //   4280: ldc 33
    //   4282: iconst_3
    //   4283: anewarray 184	java/lang/String
    //   4286: dup
    //   4287: iconst_0
    //   4288: ldc_w 1432
    //   4291: aastore
    //   4292: dup
    //   4293: iconst_1
    //   4294: ldc_w 1434
    //   4297: aastore
    //   4298: dup
    //   4299: iconst_2
    //   4300: ldc_w 1436
    //   4303: aastore
    //   4304: iconst_1
    //   4305: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   4308: aload_1
    //   4309: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   4312: aload_1
    //   4313: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   4316: bipush 88
    //   4318: istore 5
    //   4320: iload 5
    //   4322: istore 4
    //   4324: iload 5
    //   4326: bipush 88
    //   4328: if_icmpne +304 -> 4632
    //   4331: aload_0
    //   4332: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   4335: ifne +293 -> 4628
    //   4338: aload_1
    //   4339: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   4342: aconst_null
    //   4343: astore 7
    //   4345: aload 7
    //   4347: astore 6
    //   4349: aload_0
    //   4350: aload_1
    //   4351: ldc 31
    //   4353: ldc 33
    //   4355: bipush 30
    //   4357: anewarray 184	java/lang/String
    //   4360: dup
    //   4361: iconst_0
    //   4362: ldc_w 1438
    //   4365: aastore
    //   4366: dup
    //   4367: iconst_1
    //   4368: ldc_w 1440
    //   4371: aastore
    //   4372: dup
    //   4373: iconst_2
    //   4374: ldc_w 1442
    //   4377: aastore
    //   4378: dup
    //   4379: iconst_3
    //   4380: ldc_w 1444
    //   4383: aastore
    //   4384: dup
    //   4385: iconst_4
    //   4386: ldc_w 1446
    //   4389: aastore
    //   4390: dup
    //   4391: iconst_5
    //   4392: ldc_w 1448
    //   4395: aastore
    //   4396: dup
    //   4397: bipush 6
    //   4399: ldc_w 1450
    //   4402: aastore
    //   4403: dup
    //   4404: bipush 7
    //   4406: ldc_w 1452
    //   4409: aastore
    //   4410: dup
    //   4411: bipush 8
    //   4413: ldc_w 1454
    //   4416: aastore
    //   4417: dup
    //   4418: bipush 9
    //   4420: ldc_w 1456
    //   4423: aastore
    //   4424: dup
    //   4425: bipush 10
    //   4427: ldc_w 1458
    //   4430: aastore
    //   4431: dup
    //   4432: bipush 11
    //   4434: ldc_w 1460
    //   4437: aastore
    //   4438: dup
    //   4439: bipush 12
    //   4441: ldc_w 1462
    //   4444: aastore
    //   4445: dup
    //   4446: bipush 13
    //   4448: ldc_w 1464
    //   4451: aastore
    //   4452: dup
    //   4453: bipush 14
    //   4455: ldc_w 1466
    //   4458: aastore
    //   4459: dup
    //   4460: bipush 15
    //   4462: ldc_w 1468
    //   4465: aastore
    //   4466: dup
    //   4467: bipush 16
    //   4469: ldc_w 1470
    //   4472: aastore
    //   4473: dup
    //   4474: bipush 17
    //   4476: ldc_w 1472
    //   4479: aastore
    //   4480: dup
    //   4481: bipush 18
    //   4483: ldc_w 1474
    //   4486: aastore
    //   4487: dup
    //   4488: bipush 19
    //   4490: ldc_w 581
    //   4493: aastore
    //   4494: dup
    //   4495: bipush 20
    //   4497: ldc_w 583
    //   4500: aastore
    //   4501: dup
    //   4502: bipush 21
    //   4504: ldc_w 1476
    //   4507: aastore
    //   4508: dup
    //   4509: bipush 22
    //   4511: ldc_w 1478
    //   4514: aastore
    //   4515: dup
    //   4516: bipush 23
    //   4518: ldc_w 1480
    //   4521: aastore
    //   4522: dup
    //   4523: bipush 24
    //   4525: ldc_w 1281
    //   4528: aastore
    //   4529: dup
    //   4530: bipush 25
    //   4532: ldc_w 1482
    //   4535: aastore
    //   4536: dup
    //   4537: bipush 26
    //   4539: ldc_w 1484
    //   4542: aastore
    //   4543: dup
    //   4544: bipush 27
    //   4546: ldc_w 1486
    //   4549: aastore
    //   4550: dup
    //   4551: bipush 28
    //   4553: ldc_w 1488
    //   4556: aastore
    //   4557: dup
    //   4558: bipush 29
    //   4560: ldc_w 1490
    //   4563: aastore
    //   4564: iconst_1
    //   4565: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   4568: aload 7
    //   4570: astore 6
    //   4572: aload_1
    //   4573: ldc_w 1492
    //   4576: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   4579: astore 7
    //   4581: aload 7
    //   4583: astore 6
    //   4585: aload_0
    //   4586: aload 7
    //   4588: ldc_w 433
    //   4591: ldc_w 621
    //   4594: invokespecial 419	com/android/providers/settings/DatabaseHelper:loadIntegerSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   4597: aload 7
    //   4599: astore 6
    //   4601: aload 7
    //   4603: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   4606: aload 7
    //   4608: astore 6
    //   4610: aload_1
    //   4611: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   4614: aload_1
    //   4615: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   4618: aload 7
    //   4620: ifnull +8 -> 4628
    //   4623: aload 7
    //   4625: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   4628: bipush 89
    //   4630: istore 4
    //   4632: iload 4
    //   4634: istore 5
    //   4636: iload 4
    //   4638: bipush 89
    //   4640: if_icmpne +57 -> 4697
    //   4643: aload_0
    //   4644: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   4647: ifne +46 -> 4693
    //   4650: aload_1
    //   4651: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   4654: aload_0
    //   4655: aload_1
    //   4656: ldc 31
    //   4658: ldc 33
    //   4660: iconst_3
    //   4661: anewarray 184	java/lang/String
    //   4664: dup
    //   4665: iconst_0
    //   4666: ldc_w 1494
    //   4669: aastore
    //   4670: dup
    //   4671: iconst_1
    //   4672: ldc_w 1496
    //   4675: aastore
    //   4676: dup
    //   4677: iconst_2
    //   4678: ldc_w 1498
    //   4681: aastore
    //   4682: invokespecial 1500	com/android/providers/settings/DatabaseHelper:movePrefixedSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V
    //   4685: aload_1
    //   4686: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   4689: aload_1
    //   4690: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   4693: bipush 90
    //   4695: istore 5
    //   4697: iload 5
    //   4699: istore 4
    //   4701: iload 5
    //   4703: bipush 90
    //   4705: if_icmpne +131 -> 4836
    //   4708: aload_0
    //   4709: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   4712: ifne +120 -> 4832
    //   4715: aload_1
    //   4716: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   4719: aload_0
    //   4720: aload_1
    //   4721: ldc 25
    //   4723: ldc 33
    //   4725: bipush 10
    //   4727: anewarray 184	java/lang/String
    //   4730: dup
    //   4731: iconst_0
    //   4732: ldc_w 383
    //   4735: aastore
    //   4736: dup
    //   4737: iconst_1
    //   4738: ldc_w 390
    //   4741: aastore
    //   4742: dup
    //   4743: iconst_2
    //   4744: ldc_w 1502
    //   4747: aastore
    //   4748: dup
    //   4749: iconst_3
    //   4750: ldc_w 1504
    //   4753: aastore
    //   4754: dup
    //   4755: iconst_4
    //   4756: ldc_w 1506
    //   4759: aastore
    //   4760: dup
    //   4761: iconst_5
    //   4762: ldc_w 585
    //   4765: aastore
    //   4766: dup
    //   4767: bipush 6
    //   4769: ldc_w 587
    //   4772: aastore
    //   4773: dup
    //   4774: bipush 7
    //   4776: ldc_w 1508
    //   4779: aastore
    //   4780: dup
    //   4781: bipush 8
    //   4783: ldc_w 1510
    //   4786: aastore
    //   4787: dup
    //   4788: bipush 9
    //   4790: ldc_w 1512
    //   4793: aastore
    //   4794: iconst_1
    //   4795: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   4798: aload_0
    //   4799: aload_1
    //   4800: ldc 31
    //   4802: ldc 33
    //   4804: iconst_2
    //   4805: anewarray 184	java/lang/String
    //   4808: dup
    //   4809: iconst_0
    //   4810: ldc_w 597
    //   4813: aastore
    //   4814: dup
    //   4815: iconst_1
    //   4816: ldc_w 599
    //   4819: aastore
    //   4820: iconst_1
    //   4821: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   4824: aload_1
    //   4825: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   4828: aload_1
    //   4829: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   4832: bipush 91
    //   4834: istore 4
    //   4836: iload 4
    //   4838: istore 5
    //   4840: iload 4
    //   4842: bipush 91
    //   4844: if_icmpne +46 -> 4890
    //   4847: aload_0
    //   4848: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   4851: ifne +35 -> 4886
    //   4854: aload_1
    //   4855: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   4858: aload_0
    //   4859: aload_1
    //   4860: ldc 25
    //   4862: ldc 33
    //   4864: iconst_1
    //   4865: anewarray 184	java/lang/String
    //   4868: dup
    //   4869: iconst_0
    //   4870: ldc_w 474
    //   4873: aastore
    //   4874: iconst_1
    //   4875: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   4878: aload_1
    //   4879: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   4882: aload_1
    //   4883: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   4886: bipush 92
    //   4888: istore 5
    //   4890: iload 5
    //   4892: istore 4
    //   4894: iload 5
    //   4896: bipush 92
    //   4898: if_icmpne +67 -> 4965
    //   4901: aconst_null
    //   4902: astore 6
    //   4904: aload_1
    //   4905: ldc_w 634
    //   4908: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   4911: astore 7
    //   4913: aload 7
    //   4915: astore 6
    //   4917: aload_0
    //   4918: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   4921: ifne +2546 -> 7467
    //   4924: aload 7
    //   4926: astore 6
    //   4928: aload_0
    //   4929: aload 7
    //   4931: ldc_w 709
    //   4934: aload_0
    //   4935: aload_1
    //   4936: ldc 33
    //   4938: ldc_w 502
    //   4941: iconst_0
    //   4942: invokespecial 135	com/android/providers/settings/DatabaseHelper:getIntValueFromTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;I)I
    //   4945: invokestatic 334	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4948: invokespecial 377	com/android/providers/settings/DatabaseHelper:loadSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;Ljava/lang/Object;)V
    //   4951: aload 7
    //   4953: ifnull +8 -> 4961
    //   4956: aload 7
    //   4958: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   4961: bipush 93
    //   4963: istore 4
    //   4965: iload 4
    //   4967: istore 5
    //   4969: iload 4
    //   4971: bipush 93
    //   4973: if_icmpne +60 -> 5033
    //   4976: aload_0
    //   4977: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   4980: ifne +49 -> 5029
    //   4983: aload_1
    //   4984: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   4987: aload_0
    //   4988: aload_1
    //   4989: ldc 25
    //   4991: ldc 33
    //   4993: aload_0
    //   4994: getstatic 1421	com/android/providers/settings/SettingsProvider:sSystemMovedToGlobalSettings	Ljava/util/Set;
    //   4997: invokespecial 1423	com/android/providers/settings/DatabaseHelper:setToStringArray	(Ljava/util/Set;)[Ljava/lang/String;
    //   5000: iconst_1
    //   5001: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   5004: aload_0
    //   5005: aload_1
    //   5006: ldc 31
    //   5008: ldc 33
    //   5010: aload_0
    //   5011: getstatic 1426	com/android/providers/settings/SettingsProvider:sSecureMovedToGlobalSettings	Ljava/util/Set;
    //   5014: invokespecial 1423	com/android/providers/settings/DatabaseHelper:setToStringArray	(Ljava/util/Set;)[Ljava/lang/String;
    //   5017: iconst_1
    //   5018: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   5021: aload_1
    //   5022: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   5025: aload_1
    //   5026: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   5029: bipush 94
    //   5031: istore 5
    //   5033: iload 5
    //   5035: istore 4
    //   5037: iload 5
    //   5039: bipush 94
    //   5041: if_icmpne +68 -> 5109
    //   5044: aload_0
    //   5045: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   5048: ifne +57 -> 5105
    //   5051: aload_1
    //   5052: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   5055: aconst_null
    //   5056: astore 6
    //   5058: aload_1
    //   5059: ldc_w 1492
    //   5062: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   5065: astore 7
    //   5067: aload 7
    //   5069: astore 6
    //   5071: aload_0
    //   5072: aload 7
    //   5074: ldc_w 575
    //   5077: ldc_w 576
    //   5080: invokespecial 452	com/android/providers/settings/DatabaseHelper:loadStringSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   5083: aload 7
    //   5085: astore 6
    //   5087: aload_1
    //   5088: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   5091: aload_1
    //   5092: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   5095: aload 7
    //   5097: ifnull +8 -> 5105
    //   5100: aload 7
    //   5102: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   5105: bipush 95
    //   5107: istore 4
    //   5109: iload 4
    //   5111: istore 5
    //   5113: iload 4
    //   5115: bipush 95
    //   5117: if_icmpne +46 -> 5163
    //   5120: aload_0
    //   5121: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   5124: ifne +35 -> 5159
    //   5127: aload_1
    //   5128: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   5131: aload_0
    //   5132: aload_1
    //   5133: ldc 31
    //   5135: ldc 33
    //   5137: iconst_1
    //   5138: anewarray 184	java/lang/String
    //   5141: dup
    //   5142: iconst_0
    //   5143: ldc_w 1514
    //   5146: aastore
    //   5147: iconst_1
    //   5148: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   5151: aload_1
    //   5152: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   5155: aload_1
    //   5156: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   5159: bipush 96
    //   5161: istore 5
    //   5163: iload 5
    //   5165: istore 4
    //   5167: iload 5
    //   5169: bipush 96
    //   5171: if_icmpne +7 -> 5178
    //   5174: bipush 97
    //   5176: istore 4
    //   5178: iload 4
    //   5180: istore 5
    //   5182: iload 4
    //   5184: bipush 97
    //   5186: if_icmpne +68 -> 5254
    //   5189: aload_0
    //   5190: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   5193: ifne +57 -> 5250
    //   5196: aload_1
    //   5197: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   5200: aconst_null
    //   5201: astore 6
    //   5203: aload_1
    //   5204: ldc_w 1492
    //   5207: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   5210: astore 7
    //   5212: aload 7
    //   5214: astore 6
    //   5216: aload_0
    //   5217: aload 7
    //   5219: ldc_w 607
    //   5222: ldc_w 608
    //   5225: invokespecial 419	com/android/providers/settings/DatabaseHelper:loadIntegerSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   5228: aload 7
    //   5230: astore 6
    //   5232: aload_1
    //   5233: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   5236: aload_1
    //   5237: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   5240: aload 7
    //   5242: ifnull +8 -> 5250
    //   5245: aload 7
    //   5247: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   5250: bipush 98
    //   5252: istore 5
    //   5254: iload 5
    //   5256: istore 4
    //   5258: iload 5
    //   5260: bipush 98
    //   5262: if_icmpne +7 -> 5269
    //   5265: bipush 99
    //   5267: istore 4
    //   5269: iload 4
    //   5271: istore 5
    //   5273: iload 4
    //   5275: bipush 99
    //   5277: if_icmpne +7 -> 5284
    //   5280: bipush 100
    //   5282: istore 5
    //   5284: iload 5
    //   5286: istore 4
    //   5288: iload 5
    //   5290: bipush 100
    //   5292: if_icmpne +68 -> 5360
    //   5295: aload_0
    //   5296: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   5299: ifne +57 -> 5356
    //   5302: aload_1
    //   5303: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   5306: aconst_null
    //   5307: astore 6
    //   5309: aload_1
    //   5310: ldc_w 1492
    //   5313: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   5316: astore 7
    //   5318: aload 7
    //   5320: astore 6
    //   5322: aload_0
    //   5323: aload 7
    //   5325: ldc_w 613
    //   5328: ldc_w 614
    //   5331: invokespecial 419	com/android/providers/settings/DatabaseHelper:loadIntegerSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   5334: aload 7
    //   5336: astore 6
    //   5338: aload_1
    //   5339: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   5342: aload_1
    //   5343: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   5346: aload 7
    //   5348: ifnull +8 -> 5356
    //   5351: aload 7
    //   5353: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   5356: bipush 101
    //   5358: istore 4
    //   5360: iload 4
    //   5362: istore 5
    //   5364: iload 4
    //   5366: bipush 101
    //   5368: if_icmpne +69 -> 5437
    //   5371: aload_0
    //   5372: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   5375: ifne +58 -> 5433
    //   5378: aload_1
    //   5379: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   5382: aconst_null
    //   5383: astore 6
    //   5385: aload_1
    //   5386: ldc_w 409
    //   5389: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   5392: astore 7
    //   5394: aload 7
    //   5396: astore 6
    //   5398: aload_0
    //   5399: aload 7
    //   5401: ldc_w 616
    //   5404: aload_0
    //   5405: invokespecial 618	com/android/providers/settings/DatabaseHelper:getDefaultDeviceName	()Ljava/lang/String;
    //   5408: invokespecial 377	com/android/providers/settings/DatabaseHelper:loadSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;Ljava/lang/Object;)V
    //   5411: aload 7
    //   5413: astore 6
    //   5415: aload_1
    //   5416: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   5419: aload_1
    //   5420: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   5423: aload 7
    //   5425: ifnull +8 -> 5433
    //   5428: aload 7
    //   5430: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   5433: bipush 102
    //   5435: istore 5
    //   5437: iload 5
    //   5439: istore 4
    //   5441: iload 5
    //   5443: bipush 102
    //   5445: if_icmpne +74 -> 5519
    //   5448: aload_1
    //   5449: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   5452: aconst_null
    //   5453: astore 8
    //   5455: aconst_null
    //   5456: astore 7
    //   5458: aload 8
    //   5460: astore 6
    //   5462: aload_0
    //   5463: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   5466: ifne +2127 -> 7593
    //   5469: aload 8
    //   5471: astore 6
    //   5473: aload_0
    //   5474: aload_1
    //   5475: ldc 33
    //   5477: ldc 31
    //   5479: iconst_1
    //   5480: anewarray 184	java/lang/String
    //   5483: dup
    //   5484: iconst_0
    //   5485: ldc_w 715
    //   5488: aastore
    //   5489: iconst_1
    //   5490: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   5493: aload 7
    //   5495: astore 6
    //   5497: aload_1
    //   5498: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   5501: aload_1
    //   5502: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   5505: aload 7
    //   5507: ifnull +8 -> 5515
    //   5510: aload 7
    //   5512: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   5515: bipush 103
    //   5517: istore 4
    //   5519: iload 4
    //   5521: istore 5
    //   5523: iload 4
    //   5525: bipush 103
    //   5527: if_icmpne +61 -> 5588
    //   5530: aload_1
    //   5531: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   5534: aconst_null
    //   5535: astore 6
    //   5537: aload_1
    //   5538: ldc_w 1409
    //   5541: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   5544: astore 7
    //   5546: aload 7
    //   5548: astore 6
    //   5550: aload_0
    //   5551: aload 7
    //   5553: ldc_w 718
    //   5556: ldc_w 719
    //   5559: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   5562: aload 7
    //   5564: astore 6
    //   5566: aload_1
    //   5567: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   5570: aload_1
    //   5571: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   5574: aload 7
    //   5576: ifnull +8 -> 5584
    //   5579: aload 7
    //   5581: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   5584: bipush 104
    //   5586: istore 5
    //   5588: iload 5
    //   5590: istore 4
    //   5592: iload 5
    //   5594: bipush 105
    //   5596: if_icmpge +7 -> 5603
    //   5599: bipush 105
    //   5601: istore 4
    //   5603: iload 4
    //   5605: istore 5
    //   5607: iload 4
    //   5609: bipush 106
    //   5611: if_icmpge +147 -> 5758
    //   5614: aload_1
    //   5615: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   5618: aconst_null
    //   5619: astore 6
    //   5621: aload_1
    //   5622: ldc_w 634
    //   5625: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   5628: astore 7
    //   5630: aload 7
    //   5632: astore 6
    //   5634: aload_0
    //   5635: aload 7
    //   5637: ldc_w 721
    //   5640: ldc_w 722
    //   5643: invokespecial 419	com/android/providers/settings/DatabaseHelper:loadIntegerSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   5646: aload 7
    //   5648: astore 6
    //   5650: aload_0
    //   5651: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   5654: ifne +78 -> 5732
    //   5657: aload 7
    //   5659: astore 6
    //   5661: aload_0
    //   5662: aload_1
    //   5663: ldc 33
    //   5665: ldc_w 721
    //   5668: iconst_m1
    //   5669: invokespecial 135	com/android/providers/settings/DatabaseHelper:getIntValueFromTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;I)I
    //   5672: istore 4
    //   5674: iload 4
    //   5676: iflt +56 -> 5732
    //   5679: aload 7
    //   5681: astore 6
    //   5683: aload_0
    //   5684: aload 7
    //   5686: ldc_w 721
    //   5689: iload 4
    //   5691: invokestatic 334	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   5694: invokespecial 377	com/android/providers/settings/DatabaseHelper:loadSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;Ljava/lang/Object;)V
    //   5697: aload 7
    //   5699: astore 6
    //   5701: aload_1
    //   5702: ldc_w 1516
    //   5705: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   5708: astore 8
    //   5710: aload 7
    //   5712: astore 6
    //   5714: aload 8
    //   5716: iconst_1
    //   5717: ldc_w 721
    //   5720: invokevirtual 749	android/database/sqlite/SQLiteStatement:bindString	(ILjava/lang/String;)V
    //   5723: aload 7
    //   5725: astore 6
    //   5727: aload 8
    //   5729: invokevirtual 753	android/database/sqlite/SQLiteStatement:execute	()V
    //   5732: aload 7
    //   5734: astore 6
    //   5736: aload_1
    //   5737: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   5740: aload_1
    //   5741: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   5744: aload 7
    //   5746: ifnull +8 -> 5754
    //   5749: aload 7
    //   5751: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   5754: bipush 106
    //   5756: istore 5
    //   5758: iload 5
    //   5760: istore 4
    //   5762: iload 5
    //   5764: bipush 107
    //   5766: if_icmpge +68 -> 5834
    //   5769: aload_0
    //   5770: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   5773: ifne +57 -> 5830
    //   5776: aload_1
    //   5777: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   5780: aconst_null
    //   5781: astore 6
    //   5783: aload_1
    //   5784: ldc_w 1492
    //   5787: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   5790: astore 7
    //   5792: aload 7
    //   5794: astore 6
    //   5796: aload_0
    //   5797: aload 7
    //   5799: ldc_w 551
    //   5802: ldc_w 552
    //   5805: invokespecial 452	com/android/providers/settings/DatabaseHelper:loadStringSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   5808: aload 7
    //   5810: astore 6
    //   5812: aload_1
    //   5813: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   5816: aload_1
    //   5817: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   5820: aload 7
    //   5822: ifnull +8 -> 5830
    //   5825: aload 7
    //   5827: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   5830: bipush 107
    //   5832: istore 4
    //   5834: iload 4
    //   5836: istore 5
    //   5838: iload 4
    //   5840: bipush 108
    //   5842: if_icmpge +61 -> 5903
    //   5845: aload_1
    //   5846: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   5849: aconst_null
    //   5850: astore 6
    //   5852: aload_1
    //   5853: ldc_w 1189
    //   5856: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   5859: astore 7
    //   5861: aload 7
    //   5863: astore 6
    //   5865: aload_0
    //   5866: aload 7
    //   5868: ldc_w 791
    //   5871: ldc_w 792
    //   5874: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   5877: aload 7
    //   5879: astore 6
    //   5881: aload_1
    //   5882: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   5885: aload_1
    //   5886: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   5889: aload 7
    //   5891: ifnull +8 -> 5899
    //   5894: aload 7
    //   5896: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   5899: bipush 108
    //   5901: istore 5
    //   5903: iload 5
    //   5905: istore 4
    //   5907: iload 5
    //   5909: bipush 109
    //   5911: if_icmpge +61 -> 5972
    //   5914: aload_1
    //   5915: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   5918: aconst_null
    //   5919: astore 6
    //   5921: aload_1
    //   5922: ldc_w 634
    //   5925: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   5928: astore 7
    //   5930: aload 7
    //   5932: astore 6
    //   5934: aload_0
    //   5935: aload 7
    //   5937: ldc_w 724
    //   5940: ldc_w 725
    //   5943: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   5946: aload 7
    //   5948: astore 6
    //   5950: aload_1
    //   5951: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   5954: aload_1
    //   5955: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   5958: aload 7
    //   5960: ifnull +8 -> 5968
    //   5963: aload 7
    //   5965: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   5968: bipush 109
    //   5970: istore 4
    //   5972: iload 4
    //   5974: istore 5
    //   5976: iload 4
    //   5978: bipush 110
    //   5980: if_icmpge +93 -> 6073
    //   5983: aload_1
    //   5984: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   5987: aconst_null
    //   5988: astore 6
    //   5990: aload_1
    //   5991: ldc_w 1518
    //   5994: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   5997: astore 7
    //   5999: aload 7
    //   6001: astore 6
    //   6003: aload 7
    //   6005: iconst_1
    //   6006: ldc_w 1520
    //   6009: invokevirtual 749	android/database/sqlite/SQLiteStatement:bindString	(ILjava/lang/String;)V
    //   6012: aload 7
    //   6014: astore 6
    //   6016: aload 7
    //   6018: iconst_2
    //   6019: ldc_w 1522
    //   6022: invokevirtual 749	android/database/sqlite/SQLiteStatement:bindString	(ILjava/lang/String;)V
    //   6025: aload 7
    //   6027: astore 6
    //   6029: aload 7
    //   6031: iconst_3
    //   6032: ldc_w 1524
    //   6035: invokevirtual 749	android/database/sqlite/SQLiteStatement:bindString	(ILjava/lang/String;)V
    //   6038: aload 7
    //   6040: astore 6
    //   6042: aload 7
    //   6044: invokevirtual 753	android/database/sqlite/SQLiteStatement:execute	()V
    //   6047: aload 7
    //   6049: astore 6
    //   6051: aload_1
    //   6052: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   6055: aload_1
    //   6056: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6059: aload 7
    //   6061: ifnull +8 -> 6069
    //   6064: aload 7
    //   6066: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6069: bipush 110
    //   6071: istore 5
    //   6073: iload 5
    //   6075: istore 4
    //   6077: iload 5
    //   6079: bipush 111
    //   6081: if_icmpge +69 -> 6150
    //   6084: aload_0
    //   6085: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   6088: ifne +58 -> 6146
    //   6091: aload_1
    //   6092: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   6095: aconst_null
    //   6096: astore 6
    //   6098: aload_1
    //   6099: ldc_w 1492
    //   6102: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   6105: astore 7
    //   6107: aload 7
    //   6109: astore 6
    //   6111: aload_0
    //   6112: aload 7
    //   6114: ldc_w 474
    //   6117: iconst_2
    //   6118: invokestatic 334	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   6121: invokespecial 377	com/android/providers/settings/DatabaseHelper:loadSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;Ljava/lang/Object;)V
    //   6124: aload 7
    //   6126: astore 6
    //   6128: aload_1
    //   6129: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   6132: aload_1
    //   6133: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6136: aload 7
    //   6138: ifnull +8 -> 6146
    //   6141: aload 7
    //   6143: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6146: bipush 111
    //   6148: istore 4
    //   6150: iload 4
    //   6152: istore 5
    //   6154: iload 4
    //   6156: bipush 112
    //   6158: if_icmpge +102 -> 6260
    //   6161: aload_0
    //   6162: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   6165: ifne +91 -> 6256
    //   6168: aload_1
    //   6169: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   6172: aconst_null
    //   6173: astore 6
    //   6175: aload_1
    //   6176: ldc_w 1526
    //   6179: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   6182: astore 7
    //   6184: aload 7
    //   6186: astore 6
    //   6188: aload 7
    //   6190: iconst_1
    //   6191: aload_0
    //   6192: invokespecial 618	com/android/providers/settings/DatabaseHelper:getDefaultDeviceName	()Ljava/lang/String;
    //   6195: invokevirtual 749	android/database/sqlite/SQLiteStatement:bindString	(ILjava/lang/String;)V
    //   6198: aload 7
    //   6200: astore 6
    //   6202: aload 7
    //   6204: iconst_2
    //   6205: ldc_w 616
    //   6208: invokevirtual 749	android/database/sqlite/SQLiteStatement:bindString	(ILjava/lang/String;)V
    //   6211: aload 7
    //   6213: astore 6
    //   6215: aload 7
    //   6217: iconst_3
    //   6218: aload_0
    //   6219: invokespecial 1528	com/android/providers/settings/DatabaseHelper:getOldDefaultDeviceName	()Ljava/lang/String;
    //   6222: invokevirtual 749	android/database/sqlite/SQLiteStatement:bindString	(ILjava/lang/String;)V
    //   6225: aload 7
    //   6227: astore 6
    //   6229: aload 7
    //   6231: invokevirtual 753	android/database/sqlite/SQLiteStatement:execute	()V
    //   6234: aload 7
    //   6236: astore 6
    //   6238: aload_1
    //   6239: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   6242: aload_1
    //   6243: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6246: aload 7
    //   6248: ifnull +8 -> 6256
    //   6251: aload 7
    //   6253: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6256: bipush 112
    //   6258: istore 5
    //   6260: iload 5
    //   6262: istore 4
    //   6264: iload 5
    //   6266: bipush 113
    //   6268: if_icmpge +61 -> 6329
    //   6271: aload_1
    //   6272: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   6275: aconst_null
    //   6276: astore 6
    //   6278: aload_1
    //   6279: ldc_w 634
    //   6282: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   6285: astore 7
    //   6287: aload 7
    //   6289: astore 6
    //   6291: aload_0
    //   6292: aload 7
    //   6294: ldc_w 727
    //   6297: ldc_w 728
    //   6300: invokespecial 419	com/android/providers/settings/DatabaseHelper:loadIntegerSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   6303: aload 7
    //   6305: astore 6
    //   6307: aload_1
    //   6308: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   6311: aload_1
    //   6312: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6315: aload 7
    //   6317: ifnull +8 -> 6325
    //   6320: aload 7
    //   6322: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6325: bipush 113
    //   6327: istore 4
    //   6329: iload 4
    //   6331: istore 5
    //   6333: iload 4
    //   6335: bipush 115
    //   6337: if_icmpge +68 -> 6405
    //   6340: aload_0
    //   6341: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   6344: ifne +57 -> 6401
    //   6347: aload_1
    //   6348: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   6351: aconst_null
    //   6352: astore 6
    //   6354: aload_1
    //   6355: ldc_w 409
    //   6358: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   6361: astore 7
    //   6363: aload 7
    //   6365: astore 6
    //   6367: aload_0
    //   6368: aload 7
    //   6370: ldc_w 445
    //   6373: ldc_w 446
    //   6376: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   6379: aload 7
    //   6381: astore 6
    //   6383: aload_1
    //   6384: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   6387: aload_1
    //   6388: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6391: aload 7
    //   6393: ifnull +8 -> 6401
    //   6396: aload 7
    //   6398: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6401: bipush 115
    //   6403: istore 5
    //   6405: iload 5
    //   6407: istore 4
    //   6409: iload 5
    //   6411: bipush 116
    //   6413: if_icmpge +69 -> 6482
    //   6416: aload_0
    //   6417: getfield 58	com/android/providers/settings/DatabaseHelper:mUserHandle	I
    //   6420: ifne +58 -> 6478
    //   6423: aload_1
    //   6424: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   6427: aconst_null
    //   6428: astore 6
    //   6430: aload_1
    //   6431: ldc_w 409
    //   6434: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   6437: astore 7
    //   6439: aload 7
    //   6441: astore 6
    //   6443: aload_0
    //   6444: aload 7
    //   6446: ldc_w 620
    //   6449: iconst_m1
    //   6450: invokestatic 334	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   6453: invokespecial 377	com/android/providers/settings/DatabaseHelper:loadSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;Ljava/lang/Object;)V
    //   6456: aload 7
    //   6458: astore 6
    //   6460: aload_1
    //   6461: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   6464: aload_1
    //   6465: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6468: aload 7
    //   6470: ifnull +8 -> 6478
    //   6473: aload 7
    //   6475: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6478: bipush 116
    //   6480: istore 4
    //   6482: iload 4
    //   6484: istore 5
    //   6486: iload 4
    //   6488: bipush 117
    //   6490: if_icmpge +39 -> 6529
    //   6493: aload_1
    //   6494: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   6497: aload_0
    //   6498: aload_1
    //   6499: ldc 25
    //   6501: ldc 31
    //   6503: iconst_1
    //   6504: anewarray 184	java/lang/String
    //   6507: dup
    //   6508: iconst_0
    //   6509: ldc_w 1530
    //   6512: aastore
    //   6513: iconst_1
    //   6514: invokespecial 1323	com/android/providers/settings/DatabaseHelper:moveSettingsToNewTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Z)V
    //   6517: aload_1
    //   6518: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   6521: aload_1
    //   6522: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6525: bipush 117
    //   6527: istore 5
    //   6529: iload 5
    //   6531: istore 4
    //   6533: iload 5
    //   6535: bipush 118
    //   6537: if_icmpge +62 -> 6599
    //   6540: aload_1
    //   6541: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   6544: aconst_null
    //   6545: astore 6
    //   6547: aload_1
    //   6548: ldc_w 1189
    //   6551: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   6554: astore 7
    //   6556: aload 7
    //   6558: astore 6
    //   6560: aload_0
    //   6561: aload 7
    //   6563: ldc_w 1532
    //   6566: iconst_0
    //   6567: invokestatic 334	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   6570: invokespecial 377	com/android/providers/settings/DatabaseHelper:loadSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;Ljava/lang/Object;)V
    //   6573: aload 7
    //   6575: astore 6
    //   6577: aload_1
    //   6578: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   6581: aload_1
    //   6582: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6585: aload 7
    //   6587: ifnull +8 -> 6595
    //   6590: aload 7
    //   6592: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6595: bipush 118
    //   6597: istore 4
    //   6599: iload 4
    //   6601: iload_3
    //   6602: if_icmpeq +12 -> 6614
    //   6605: aload_0
    //   6606: aload_1
    //   6607: iload_2
    //   6608: iload 4
    //   6610: iload_3
    //   6611: invokevirtual 1536	com/android/providers/settings/DatabaseHelper:recreateDatabase	(Landroid/database/sqlite/SQLiteDatabase;III)V
    //   6614: return
    //   6615: astore 6
    //   6617: aload_1
    //   6618: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6621: aload 6
    //   6623: athrow
    //   6624: astore 6
    //   6626: aload_1
    //   6627: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6630: aload 6
    //   6632: athrow
    //   6633: astore 6
    //   6635: aload_1
    //   6636: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6639: aload 6
    //   6641: athrow
    //   6642: astore 6
    //   6644: aload_1
    //   6645: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6648: aload 6
    //   6650: athrow
    //   6651: astore 6
    //   6653: aload_1
    //   6654: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6657: aload 6
    //   6659: athrow
    //   6660: astore 6
    //   6662: aload_1
    //   6663: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6666: aload 6
    //   6668: athrow
    //   6669: astore 7
    //   6671: aload_1
    //   6672: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6675: aload 6
    //   6677: ifnull +8 -> 6685
    //   6680: aload 6
    //   6682: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6685: aload 7
    //   6687: athrow
    //   6688: astore 6
    //   6690: aload_1
    //   6691: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6694: aload 6
    //   6696: athrow
    //   6697: astore 6
    //   6699: aload_1
    //   6700: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6703: aload 6
    //   6705: athrow
    //   6706: astore 7
    //   6708: aload_1
    //   6709: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6712: aload 6
    //   6714: ifnull +8 -> 6722
    //   6717: aload 6
    //   6719: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6722: aload 7
    //   6724: athrow
    //   6725: astore 6
    //   6727: aload_1
    //   6728: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6731: aload 6
    //   6733: athrow
    //   6734: astore 7
    //   6736: aload_1
    //   6737: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6740: aload 6
    //   6742: ifnull +8 -> 6750
    //   6745: aload 6
    //   6747: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6750: aload 7
    //   6752: athrow
    //   6753: ldc_w 379
    //   6756: astore 6
    //   6758: goto -5679 -> 1079
    //   6761: astore 6
    //   6763: aload_1
    //   6764: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6767: aload 6
    //   6769: athrow
    //   6770: astore 7
    //   6772: aload_1
    //   6773: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6776: aload 6
    //   6778: ifnull +8 -> 6786
    //   6781: aload 6
    //   6783: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6786: aload 7
    //   6788: athrow
    //   6789: astore 7
    //   6791: aload_1
    //   6792: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6795: aload 6
    //   6797: ifnull +8 -> 6805
    //   6800: aload 6
    //   6802: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6805: aload 7
    //   6807: athrow
    //   6808: astore 7
    //   6810: aload_1
    //   6811: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6814: aload 6
    //   6816: ifnull +8 -> 6824
    //   6819: aload 6
    //   6821: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6824: aload 7
    //   6826: athrow
    //   6827: astore 7
    //   6829: aload_1
    //   6830: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6833: aload 6
    //   6835: ifnull +8 -> 6843
    //   6838: aload 6
    //   6840: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6843: aload 7
    //   6845: athrow
    //   6846: astore 6
    //   6848: aload_1
    //   6849: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6852: aload 6
    //   6854: athrow
    //   6855: astore 6
    //   6857: aload_1
    //   6858: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6861: aload 6
    //   6863: athrow
    //   6864: astore 6
    //   6866: aload_1
    //   6867: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6870: aload 6
    //   6872: athrow
    //   6873: astore 7
    //   6875: aload_1
    //   6876: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6879: aload 6
    //   6881: ifnull +8 -> 6889
    //   6884: aload 6
    //   6886: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6889: aload 7
    //   6891: athrow
    //   6892: astore 7
    //   6894: aload_1
    //   6895: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6898: aload 6
    //   6900: ifnull +8 -> 6908
    //   6903: aload 6
    //   6905: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6908: aload 7
    //   6910: athrow
    //   6911: astore 6
    //   6913: aload_1
    //   6914: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6917: aload 6
    //   6919: athrow
    //   6920: astore 7
    //   6922: aload_1
    //   6923: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6926: aload 6
    //   6928: ifnull +8 -> 6936
    //   6931: aload 6
    //   6933: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6936: aload 7
    //   6938: athrow
    //   6939: astore 7
    //   6941: aload_1
    //   6942: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6945: aload 6
    //   6947: ifnull +8 -> 6955
    //   6950: aload 6
    //   6952: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6955: aload 7
    //   6957: athrow
    //   6958: astore 7
    //   6960: aload_1
    //   6961: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6964: aload 6
    //   6966: ifnull +8 -> 6974
    //   6969: aload 6
    //   6971: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6974: aload 7
    //   6976: athrow
    //   6977: astore 7
    //   6979: aload_1
    //   6980: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   6983: aload 6
    //   6985: ifnull +8 -> 6993
    //   6988: aload 6
    //   6990: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   6993: aload 7
    //   6995: athrow
    //   6996: astore 7
    //   6998: aload_1
    //   6999: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7002: aload 6
    //   7004: ifnull +8 -> 7012
    //   7007: aload 6
    //   7009: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7012: aload 7
    //   7014: athrow
    //   7015: astore 6
    //   7017: aload_1
    //   7018: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7021: aload 6
    //   7023: athrow
    //   7024: astore 7
    //   7026: aload_1
    //   7027: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7030: aload 6
    //   7032: ifnull +8 -> 7040
    //   7035: aload 6
    //   7037: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7040: aload 7
    //   7042: athrow
    //   7043: astore 7
    //   7045: aload_1
    //   7046: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7049: aload 6
    //   7051: ifnull +8 -> 7059
    //   7054: aload 6
    //   7056: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7059: aload 7
    //   7061: athrow
    //   7062: astore 6
    //   7064: aload_1
    //   7065: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7068: aload 6
    //   7070: athrow
    //   7071: astore 7
    //   7073: aload_1
    //   7074: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7077: aload 6
    //   7079: ifnull +8 -> 7087
    //   7082: aload 6
    //   7084: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7087: aload 7
    //   7089: athrow
    //   7090: astore 6
    //   7092: aload_1
    //   7093: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7096: aload 6
    //   7098: athrow
    //   7099: astore 6
    //   7101: aload_1
    //   7102: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7105: aload 6
    //   7107: athrow
    //   7108: astore 7
    //   7110: aload_1
    //   7111: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7114: aload 6
    //   7116: ifnull +8 -> 7124
    //   7119: aload 6
    //   7121: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7124: aload 7
    //   7126: athrow
    //   7127: astore 7
    //   7129: aload_1
    //   7130: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7133: aload 6
    //   7135: ifnull +8 -> 7143
    //   7138: aload 6
    //   7140: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7143: aload 7
    //   7145: athrow
    //   7146: astore 7
    //   7148: aload_1
    //   7149: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7152: aload 6
    //   7154: ifnull +8 -> 7162
    //   7157: aload 6
    //   7159: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7162: aload 7
    //   7164: athrow
    //   7165: astore 7
    //   7167: aload_1
    //   7168: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7171: aload 8
    //   7173: ifnull +10 -> 7183
    //   7176: aload 8
    //   7178: invokeinterface 200 1 0
    //   7183: aload 6
    //   7185: ifnull +8 -> 7193
    //   7188: aload 6
    //   7190: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7193: aload 7
    //   7195: athrow
    //   7196: astore 6
    //   7198: aload_1
    //   7199: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7202: aload 6
    //   7204: athrow
    //   7205: astore 7
    //   7207: aload_1
    //   7208: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7211: aload 6
    //   7213: ifnull +8 -> 7221
    //   7216: aload 6
    //   7218: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7221: aload 7
    //   7223: athrow
    //   7224: iconst_0
    //   7225: istore 4
    //   7227: goto -3673 -> 3554
    //   7230: iconst_0
    //   7231: istore 5
    //   7233: goto -3661 -> 3572
    //   7236: aconst_null
    //   7237: astore 7
    //   7239: aload 7
    //   7241: astore 6
    //   7243: aload_1
    //   7244: invokevirtual 1108	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   7247: aload 7
    //   7249: astore 6
    //   7251: aload_1
    //   7252: ldc_w 1409
    //   7255: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   7258: astore 7
    //   7260: aload 7
    //   7262: astore 6
    //   7264: aload_0
    //   7265: aload 7
    //   7267: ldc_w 1415
    //   7270: aload 8
    //   7272: invokespecial 377	com/android/providers/settings/DatabaseHelper:loadSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;Ljava/lang/Object;)V
    //   7275: aload 7
    //   7277: astore 6
    //   7279: aload_1
    //   7280: invokevirtual 1126	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   7283: aload_1
    //   7284: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7287: aload 7
    //   7289: ifnull -3665 -> 3624
    //   7292: aload 7
    //   7294: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7297: goto -3673 -> 3624
    //   7300: astore 7
    //   7302: aload_1
    //   7303: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7306: aload 6
    //   7308: ifnull +8 -> 7316
    //   7311: aload 6
    //   7313: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7316: aload 7
    //   7318: athrow
    //   7319: astore 7
    //   7321: aload_1
    //   7322: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7325: aload 6
    //   7327: ifnull +8 -> 7335
    //   7330: aload 6
    //   7332: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7335: aload 7
    //   7337: athrow
    //   7338: astore 7
    //   7340: aload_1
    //   7341: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7344: aload 6
    //   7346: ifnull +8 -> 7354
    //   7349: aload 6
    //   7351: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7354: aload 7
    //   7356: athrow
    //   7357: astore 6
    //   7359: aload_1
    //   7360: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7363: aload 6
    //   7365: athrow
    //   7366: astore 7
    //   7368: aload_1
    //   7369: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7372: aload 6
    //   7374: ifnull +8 -> 7382
    //   7377: aload 6
    //   7379: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7382: aload 7
    //   7384: athrow
    //   7385: astore 6
    //   7387: aload_1
    //   7388: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7391: aload 6
    //   7393: athrow
    //   7394: astore 6
    //   7396: aload_1
    //   7397: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7400: aload 6
    //   7402: athrow
    //   7403: astore 6
    //   7405: aload_1
    //   7406: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7409: aload 6
    //   7411: athrow
    //   7412: astore 6
    //   7414: aload_1
    //   7415: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7418: aload 6
    //   7420: athrow
    //   7421: astore 7
    //   7423: aload_1
    //   7424: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7427: aload 6
    //   7429: ifnull +8 -> 7437
    //   7432: aload 6
    //   7434: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7437: aload 7
    //   7439: athrow
    //   7440: astore 6
    //   7442: aload_1
    //   7443: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7446: aload 6
    //   7448: athrow
    //   7449: astore 6
    //   7451: aload_1
    //   7452: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7455: aload 6
    //   7457: athrow
    //   7458: astore 6
    //   7460: aload_1
    //   7461: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7464: aload 6
    //   7466: athrow
    //   7467: aload 7
    //   7469: astore 6
    //   7471: aload_0
    //   7472: aload 7
    //   7474: ldc_w 709
    //   7477: ldc_w 710
    //   7480: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   7483: goto -2532 -> 4951
    //   7486: astore_1
    //   7487: aload 6
    //   7489: ifnull +8 -> 7497
    //   7492: aload 6
    //   7494: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7497: aload_1
    //   7498: athrow
    //   7499: astore 6
    //   7501: aload_1
    //   7502: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7505: aload 6
    //   7507: athrow
    //   7508: astore 7
    //   7510: aload_1
    //   7511: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7514: aload 6
    //   7516: ifnull +8 -> 7524
    //   7519: aload 6
    //   7521: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7524: aload 7
    //   7526: athrow
    //   7527: astore 6
    //   7529: aload_1
    //   7530: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7533: aload 6
    //   7535: athrow
    //   7536: astore 7
    //   7538: aload_1
    //   7539: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7542: aload 6
    //   7544: ifnull +8 -> 7552
    //   7547: aload 6
    //   7549: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7552: aload 7
    //   7554: athrow
    //   7555: astore 7
    //   7557: aload_1
    //   7558: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7561: aload 6
    //   7563: ifnull +8 -> 7571
    //   7566: aload 6
    //   7568: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7571: aload 7
    //   7573: athrow
    //   7574: astore 7
    //   7576: aload_1
    //   7577: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7580: aload 6
    //   7582: ifnull +8 -> 7590
    //   7585: aload 6
    //   7587: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7590: aload 7
    //   7592: athrow
    //   7593: aload 8
    //   7595: astore 6
    //   7597: aload_1
    //   7598: ldc_w 634
    //   7601: invokevirtual 413	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   7604: astore 7
    //   7606: aload 7
    //   7608: astore 6
    //   7610: aload_0
    //   7611: aload 7
    //   7613: ldc_w 715
    //   7616: ldc_w 716
    //   7619: invokespecial 397	com/android/providers/settings/DatabaseHelper:loadBooleanSetting	(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/String;I)V
    //   7622: goto -2129 -> 5493
    //   7625: astore 7
    //   7627: aload_1
    //   7628: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7631: aload 6
    //   7633: ifnull +8 -> 7641
    //   7636: aload 6
    //   7638: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7641: aload 7
    //   7643: athrow
    //   7644: astore 7
    //   7646: aload_1
    //   7647: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7650: aload 6
    //   7652: ifnull +8 -> 7660
    //   7655: aload 6
    //   7657: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7660: aload 7
    //   7662: athrow
    //   7663: astore 7
    //   7665: aload_1
    //   7666: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7669: aload 6
    //   7671: ifnull +8 -> 7679
    //   7674: aload 6
    //   7676: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7679: aload 7
    //   7681: athrow
    //   7682: astore 7
    //   7684: aload_1
    //   7685: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7688: aload 6
    //   7690: ifnull +8 -> 7698
    //   7693: aload 6
    //   7695: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7698: aload 7
    //   7700: athrow
    //   7701: astore 7
    //   7703: aload_1
    //   7704: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7707: aload 6
    //   7709: ifnull +8 -> 7717
    //   7712: aload 6
    //   7714: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7717: aload 7
    //   7719: athrow
    //   7720: astore 7
    //   7722: aload_1
    //   7723: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7726: aload 6
    //   7728: ifnull +8 -> 7736
    //   7731: aload 6
    //   7733: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7736: aload 7
    //   7738: athrow
    //   7739: astore 7
    //   7741: aload_1
    //   7742: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7745: aload 6
    //   7747: ifnull +8 -> 7755
    //   7750: aload 6
    //   7752: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7755: aload 7
    //   7757: athrow
    //   7758: astore 7
    //   7760: aload_1
    //   7761: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7764: aload 6
    //   7766: ifnull +8 -> 7774
    //   7769: aload 6
    //   7771: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7774: aload 7
    //   7776: athrow
    //   7777: astore 7
    //   7779: aload_1
    //   7780: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7783: aload 6
    //   7785: ifnull +8 -> 7793
    //   7788: aload 6
    //   7790: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7793: aload 7
    //   7795: athrow
    //   7796: astore 7
    //   7798: aload_1
    //   7799: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7802: aload 6
    //   7804: ifnull +8 -> 7812
    //   7807: aload 6
    //   7809: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7812: aload 7
    //   7814: athrow
    //   7815: astore 7
    //   7817: aload_1
    //   7818: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7821: aload 6
    //   7823: ifnull +8 -> 7831
    //   7826: aload 6
    //   7828: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7831: aload 7
    //   7833: athrow
    //   7834: astore 7
    //   7836: aload_1
    //   7837: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7840: aload 6
    //   7842: ifnull +8 -> 7850
    //   7845: aload 6
    //   7847: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7850: aload 7
    //   7852: athrow
    //   7853: astore 6
    //   7855: aload_1
    //   7856: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7859: aload 6
    //   7861: athrow
    //   7862: astore 7
    //   7864: aload_1
    //   7865: invokevirtual 1129	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   7868: aload 6
    //   7870: ifnull +8 -> 7878
    //   7873: aload 6
    //   7875: invokevirtual 531	android/database/sqlite/SQLiteStatement:close	()V
    //   7878: aload 7
    //   7880: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	7881	0	this	DatabaseHelper
    //   0	7881	1	paramSQLiteDatabase	SQLiteDatabase
    //   0	7881	2	paramInt1	int
    //   0	7881	3	paramInt2	int
    //   57	7169	4	i	int
    //   37	7195	5	j	int
    //   650	5926	6	localObject1	Object
    //   6615	7	6	localObject2	Object
    //   6624	7	6	localObject3	Object
    //   6633	7	6	localObject4	Object
    //   6642	7	6	localObject5	Object
    //   6651	7	6	localObject6	Object
    //   6660	21	6	localObject7	Object
    //   6688	7	6	localObject8	Object
    //   6697	21	6	localObject9	Object
    //   6725	21	6	localObject10	Object
    //   6756	1	6	str	String
    //   6761	78	6	localObject11	Object
    //   6846	7	6	localObject12	Object
    //   6855	7	6	localObject13	Object
    //   6864	40	6	localObject14	Object
    //   6911	97	6	localObject15	Object
    //   7015	40	6	localObject16	Object
    //   7062	21	6	localObject17	Object
    //   7090	7	6	localObject18	Object
    //   7099	90	6	localObject19	Object
    //   7196	21	6	localObject20	Object
    //   7241	109	6	localSQLiteStatement1	SQLiteStatement
    //   7357	21	6	localObject21	Object
    //   7385	7	6	localObject22	Object
    //   7394	7	6	localObject23	Object
    //   7403	7	6	localObject24	Object
    //   7412	21	6	localObject25	Object
    //   7440	7	6	localObject26	Object
    //   7449	7	6	localObject27	Object
    //   7458	7	6	localObject28	Object
    //   7469	24	6	localSQLiteStatement2	SQLiteStatement
    //   7499	21	6	localObject29	Object
    //   7527	59	6	localObject30	Object
    //   7595	251	6	localObject31	Object
    //   7853	21	6	localObject32	Object
    //   646	5945	7	localObject33	Object
    //   6669	17	7	localObject34	Object
    //   6706	17	7	localObject35	Object
    //   6734	17	7	localObject36	Object
    //   6770	17	7	localObject37	Object
    //   6789	17	7	localObject38	Object
    //   6808	17	7	localObject39	Object
    //   6827	17	7	localObject40	Object
    //   6873	17	7	localObject41	Object
    //   6892	17	7	localObject42	Object
    //   6920	17	7	localObject43	Object
    //   6939	17	7	localObject44	Object
    //   6958	17	7	localObject45	Object
    //   6977	17	7	localObject46	Object
    //   6996	17	7	localObject47	Object
    //   7024	17	7	localObject48	Object
    //   7043	17	7	localObject49	Object
    //   7071	17	7	localObject50	Object
    //   7108	17	7	localObject51	Object
    //   7127	17	7	localObject52	Object
    //   7146	17	7	localObject53	Object
    //   7165	29	7	localObject54	Object
    //   7205	17	7	localObject55	Object
    //   7237	56	7	localSQLiteStatement3	SQLiteStatement
    //   7300	17	7	localObject56	Object
    //   7319	17	7	localObject57	Object
    //   7338	17	7	localObject58	Object
    //   7366	17	7	localObject59	Object
    //   7421	52	7	localSQLiteStatement4	SQLiteStatement
    //   7508	17	7	localObject60	Object
    //   7536	17	7	localObject61	Object
    //   7555	17	7	localObject62	Object
    //   7574	17	7	localObject63	Object
    //   7604	8	7	localSQLiteStatement5	SQLiteStatement
    //   7625	17	7	localObject64	Object
    //   7644	17	7	localObject65	Object
    //   7663	17	7	localObject66	Object
    //   7682	17	7	localObject67	Object
    //   7701	17	7	localObject68	Object
    //   7720	17	7	localObject69	Object
    //   7739	17	7	localObject70	Object
    //   7758	17	7	localObject71	Object
    //   7777	17	7	localObject72	Object
    //   7796	17	7	localObject73	Object
    //   7815	17	7	localObject74	Object
    //   7834	17	7	localObject75	Object
    //   7862	17	7	localObject76	Object
    //   3264	4330	8	localObject77	Object
    //   3298	84	9	localCursor	Cursor
    //   3258	70	10	localObject78	Object
    // Exception table:
    //   from	to	target	type
    //   112	151	6615	finally
    //   174	192	6624	finally
    //   215	233	6633	finally
    //   256	265	6642	finally
    //   536	581	6651	finally
    //   604	622	6660	finally
    //   652	659	6669	finally
    //   663	670	6669	finally
    //   674	683	6669	finally
    //   687	693	6669	finally
    //   697	701	6669	finally
    //   750	785	6688	finally
    //   808	819	6697	finally
    //   845	854	6706	finally
    //   858	864	6706	finally
    //   868	872	6706	finally
    //   920	966	6725	finally
    //   992	1001	6734	finally
    //   1005	1017	6734	finally
    //   1021	1025	6734	finally
    //   1058	1074	6761	finally
    //   1079	1114	6761	finally
    //   1164	1171	6770	finally
    //   1175	1182	6770	finally
    //   1186	1195	6770	finally
    //   1199	1205	6770	finally
    //   1209	1213	6770	finally
    //   1253	1260	6789	finally
    //   1264	1273	6789	finally
    //   1277	1283	6789	finally
    //   1287	1291	6789	finally
    //   1327	1336	6808	finally
    //   1340	1352	6808	finally
    //   1356	1360	6808	finally
    //   1396	1405	6827	finally
    //   1409	1426	6827	finally
    //   1430	1434	6827	finally
    //   1496	1528	6846	finally
    //   1551	1562	6855	finally
    //   1585	1596	6864	finally
    //   1637	1646	6873	finally
    //   1650	1656	6873	finally
    //   1660	1664	6873	finally
    //   1802	1811	6892	finally
    //   1815	1827	6892	finally
    //   1831	1835	6892	finally
    //   1883	1892	6911	finally
    //   1944	1953	6920	finally
    //   1957	1970	6920	finally
    //   1974	1987	6920	finally
    //   1991	1995	6920	finally
    //   2035	2042	6939	finally
    //   2046	2055	6939	finally
    //   2059	2071	6939	finally
    //   2075	2079	6939	finally
    //   2115	2124	6958	finally
    //   2128	2140	6958	finally
    //   2144	2149	6958	finally
    //   2153	2162	6958	finally
    //   2166	2178	6958	finally
    //   2182	2186	6958	finally
    //   2233	2242	6977	finally
    //   2246	2260	6977	finally
    //   2264	2268	6977	finally
    //   2304	2313	6996	finally
    //   2317	2329	6996	finally
    //   2333	2337	6996	finally
    //   2415	2461	7015	finally
    //   2487	2496	7024	finally
    //   2500	2512	7024	finally
    //   2516	2521	7024	finally
    //   2525	2529	7024	finally
    //   2569	2576	7043	finally
    //   2580	2587	7043	finally
    //   2591	2600	7043	finally
    //   2604	2610	7043	finally
    //   2614	2618	7043	finally
    //   2656	2672	7062	finally
    //   2677	2722	7062	finally
    //   2748	2757	7071	finally
    //   2761	2773	7071	finally
    //   2777	2782	7071	finally
    //   2786	2790	7071	finally
    //   2823	2834	7090	finally
    //   2887	2987	7099	finally
    //   3033	3042	7108	finally
    //   3046	3058	7108	finally
    //   3062	3066	7108	finally
    //   3102	3111	7127	finally
    //   3115	3127	7127	finally
    //   3131	3135	7127	finally
    //   3191	3200	7146	finally
    //   3204	3216	7146	finally
    //   3220	3224	7146	finally
    //   3270	3300	7165	finally
    //   3313	3323	7165	finally
    //   3331	3340	7165	finally
    //   3348	3360	7165	finally
    //   3368	3372	7165	finally
    //   3417	3428	7196	finally
    //   3474	3483	7205	finally
    //   3487	3499	7205	finally
    //   3503	3507	7205	finally
    //   7243	7247	7300	finally
    //   7251	7260	7300	finally
    //   7264	7275	7300	finally
    //   7279	7283	7300	finally
    //   3646	3655	7319	finally
    //   3659	3671	7319	finally
    //   3675	3687	7319	finally
    //   3691	3703	7319	finally
    //   3707	3719	7319	finally
    //   3723	3735	7319	finally
    //   3739	3743	7319	finally
    //   3779	3788	7338	finally
    //   3792	3804	7338	finally
    //   3808	3812	7338	finally
    //   3852	3895	7357	finally
    //   3921	3930	7366	finally
    //   3934	3946	7366	finally
    //   3950	3955	7366	finally
    //   3959	3968	7366	finally
    //   3972	3985	7366	finally
    //   3989	3994	7366	finally
    //   3998	4007	7366	finally
    //   4011	4023	7366	finally
    //   4027	4031	7366	finally
    //   4071	4126	7385	finally
    //   4156	4180	7394	finally
    //   4210	4246	7403	finally
    //   4276	4312	7412	finally
    //   4349	4568	7421	finally
    //   4572	4581	7421	finally
    //   4585	4597	7421	finally
    //   4601	4606	7421	finally
    //   4610	4614	7421	finally
    //   4654	4689	7440	finally
    //   4719	4828	7449	finally
    //   4858	4882	7458	finally
    //   4904	4913	7486	finally
    //   4917	4924	7486	finally
    //   4928	4951	7486	finally
    //   7471	7483	7486	finally
    //   4987	5025	7499	finally
    //   5058	5067	7508	finally
    //   5071	5083	7508	finally
    //   5087	5091	7508	finally
    //   5131	5155	7527	finally
    //   5203	5212	7536	finally
    //   5216	5228	7536	finally
    //   5232	5236	7536	finally
    //   5309	5318	7555	finally
    //   5322	5334	7555	finally
    //   5338	5342	7555	finally
    //   5385	5394	7574	finally
    //   5398	5411	7574	finally
    //   5415	5419	7574	finally
    //   5462	5469	7625	finally
    //   5473	5493	7625	finally
    //   5497	5501	7625	finally
    //   7597	7606	7625	finally
    //   7610	7622	7625	finally
    //   5537	5546	7644	finally
    //   5550	5562	7644	finally
    //   5566	5570	7644	finally
    //   5621	5630	7663	finally
    //   5634	5646	7663	finally
    //   5650	5657	7663	finally
    //   5661	5674	7663	finally
    //   5683	5697	7663	finally
    //   5701	5710	7663	finally
    //   5714	5723	7663	finally
    //   5727	5732	7663	finally
    //   5736	5740	7663	finally
    //   5783	5792	7682	finally
    //   5796	5808	7682	finally
    //   5812	5816	7682	finally
    //   5852	5861	7701	finally
    //   5865	5877	7701	finally
    //   5881	5885	7701	finally
    //   5921	5930	7720	finally
    //   5934	5946	7720	finally
    //   5950	5954	7720	finally
    //   5990	5999	7739	finally
    //   6003	6012	7739	finally
    //   6016	6025	7739	finally
    //   6029	6038	7739	finally
    //   6042	6047	7739	finally
    //   6051	6055	7739	finally
    //   6098	6107	7758	finally
    //   6111	6124	7758	finally
    //   6128	6132	7758	finally
    //   6175	6184	7777	finally
    //   6188	6198	7777	finally
    //   6202	6211	7777	finally
    //   6215	6225	7777	finally
    //   6229	6234	7777	finally
    //   6238	6242	7777	finally
    //   6278	6287	7796	finally
    //   6291	6303	7796	finally
    //   6307	6311	7796	finally
    //   6354	6363	7815	finally
    //   6367	6379	7815	finally
    //   6383	6387	7815	finally
    //   6430	6439	7834	finally
    //   6443	6456	7834	finally
    //   6460	6464	7834	finally
    //   6497	6521	7853	finally
    //   6547	6556	7862	finally
    //   6560	6573	7862	finally
    //   6577	6581	7862	finally
  }
  
  public void recreateDatabase(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2, int paramInt3)
  {
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS global");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS globalIndex1");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS system");
    paramSQLiteDatabase.execSQL("DROP INDEX IF EXISTS systemIndex1");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS secure");
    paramSQLiteDatabase.execSQL("DROP INDEX IF EXISTS secureIndex1");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS gservices");
    paramSQLiteDatabase.execSQL("DROP INDEX IF EXISTS gservicesIndex1");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS bluetooth_devices");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS bookmarks");
    paramSQLiteDatabase.execSQL("DROP INDEX IF EXISTS bookmarksIndex1");
    paramSQLiteDatabase.execSQL("DROP INDEX IF EXISTS bookmarksIndex2");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS favorites");
    onCreate(paramSQLiteDatabase);
    String str = paramInt1 + "/" + paramInt2 + "/" + paramInt3;
    paramSQLiteDatabase.execSQL("INSERT INTO secure(name,value) values('wiped_db_reason','" + str + "');");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/providers/settings/DatabaseHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */