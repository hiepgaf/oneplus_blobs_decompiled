package com.android.server.firewall;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import com.android.server.EventLogTags;
import com.android.server.IntentResolver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class IntentFirewall
{
  private static final int LOG_PACKAGES_MAX_LENGTH = 150;
  private static final int LOG_PACKAGES_SUFFICIENT_LENGTH = 125;
  private static final File RULES_DIR = new File(Environment.getDataSystemDirectory(), "ifw");
  static final String TAG = "IntentFirewall";
  private static final String TAG_ACTIVITY = "activity";
  private static final String TAG_BROADCAST = "broadcast";
  private static final String TAG_RULES = "rules";
  private static final String TAG_SERVICE = "service";
  private static final int TYPE_ACTIVITY = 0;
  private static final int TYPE_BROADCAST = 1;
  private static final int TYPE_SERVICE = 2;
  private static final HashMap<String, FilterFactory> factoryMap;
  private FirewallIntentResolver mActivityResolver = new FirewallIntentResolver(null);
  private final AMSInterface mAms;
  private FirewallIntentResolver mBroadcastResolver = new FirewallIntentResolver(null);
  final FirewallHandler mHandler;
  private final RuleObserver mObserver;
  private FirewallIntentResolver mServiceResolver = new FirewallIntentResolver(null);
  
  static
  {
    FilterFactory[] arrayOfFilterFactory = new FilterFactory[18];
    arrayOfFilterFactory[0] = AndFilter.FACTORY;
    arrayOfFilterFactory[1] = OrFilter.FACTORY;
    arrayOfFilterFactory[2] = NotFilter.FACTORY;
    arrayOfFilterFactory[3] = StringFilter.ACTION;
    arrayOfFilterFactory[4] = StringFilter.COMPONENT;
    arrayOfFilterFactory[5] = StringFilter.COMPONENT_NAME;
    arrayOfFilterFactory[6] = StringFilter.COMPONENT_PACKAGE;
    arrayOfFilterFactory[7] = StringFilter.DATA;
    arrayOfFilterFactory[8] = StringFilter.HOST;
    arrayOfFilterFactory[9] = StringFilter.MIME_TYPE;
    arrayOfFilterFactory[10] = StringFilter.SCHEME;
    arrayOfFilterFactory[11] = StringFilter.PATH;
    arrayOfFilterFactory[12] = StringFilter.SSP;
    arrayOfFilterFactory[13] = CategoryFilter.FACTORY;
    arrayOfFilterFactory[14] = SenderFilter.FACTORY;
    arrayOfFilterFactory[15] = SenderPackageFilter.FACTORY;
    arrayOfFilterFactory[16] = SenderPermissionFilter.FACTORY;
    arrayOfFilterFactory[17] = PortFilter.FACTORY;
    factoryMap = new HashMap(arrayOfFilterFactory.length * 4 / 3);
    int i = 0;
    while (i < arrayOfFilterFactory.length)
    {
      FilterFactory localFilterFactory = arrayOfFilterFactory[i];
      factoryMap.put(localFilterFactory.getTagName(), localFilterFactory);
      i += 1;
    }
  }
  
  public IntentFirewall(AMSInterface paramAMSInterface, Handler paramHandler)
  {
    this.mAms = paramAMSInterface;
    this.mHandler = new FirewallHandler(paramHandler.getLooper());
    paramAMSInterface = getRulesDir();
    paramAMSInterface.mkdirs();
    readRulesDir(paramAMSInterface);
    this.mObserver = new RuleObserver(paramAMSInterface);
    this.mObserver.startWatching();
  }
  
  public static File getRulesDir()
  {
    return RULES_DIR;
  }
  
  private static String joinPackages(String[] paramArrayOfString)
  {
    int i = 1;
    StringBuilder localStringBuilder = new StringBuilder();
    int j = 0;
    if (j < paramArrayOfString.length)
    {
      String str = paramArrayOfString[j];
      if (localStringBuilder.length() + str.length() + 1 < 150) {
        if (i == 0)
        {
          localStringBuilder.append(',');
          localStringBuilder.append(str);
        }
      }
      while (localStringBuilder.length() < 125) {
        for (;;)
        {
          j += 1;
          break;
          i = 0;
        }
      }
      return localStringBuilder.toString();
    }
    if ((localStringBuilder.length() == 0) && (paramArrayOfString.length > 0))
    {
      paramArrayOfString = paramArrayOfString[0];
      return paramArrayOfString.substring(paramArrayOfString.length() - 150 + 1) + '-';
    }
    return null;
  }
  
  private static void logIntent(int paramInt1, Intent paramIntent, int paramInt2, String paramString)
  {
    Object localObject1 = paramIntent.getComponent();
    String str = null;
    if (localObject1 != null) {
      str = ((ComponentName)localObject1).flattenToShortString();
    }
    localObject3 = null;
    int k = 0;
    int j = 0;
    Object localObject4 = AppGlobals.getPackageManager();
    int i = j;
    localObject1 = localObject3;
    if (localObject4 != null) {
      i = k;
    }
    try
    {
      localObject4 = ((IPackageManager)localObject4).getPackagesForUid(paramInt2);
      i = j;
      localObject1 = localObject3;
      if (localObject4 != null)
      {
        i = k;
        j = localObject4.length;
        i = j;
        localObject1 = joinPackages((String[])localObject4);
        i = j;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.e("IntentFirewall", "Remote exception while retrieving packages", localRemoteException);
        Object localObject2 = localObject3;
      }
    }
    EventLogTags.writeIfwIntentMatched(paramInt1, str, paramInt2, i, (String)localObject1, paramIntent.getAction(), paramString, paramIntent.getDataString(), paramIntent.getFlags());
  }
  
  static Filter parseFilter(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    String str = paramXmlPullParser.getName();
    FilterFactory localFilterFactory = (FilterFactory)factoryMap.get(str);
    if (localFilterFactory == null) {
      throw new XmlPullParserException("Unknown element in filter list: " + str);
    }
    return localFilterFactory.newFilter(paramXmlPullParser);
  }
  
  /* Error */
  private void readRules(File paramFile, FirewallIntentResolver[] paramArrayOfFirewallIntentResolver)
  {
    // Byte code:
    //   0: new 313	java/util/ArrayList
    //   3: dup
    //   4: iconst_3
    //   5: invokespecial 314	java/util/ArrayList:<init>	(I)V
    //   8: astore 6
    //   10: iconst_0
    //   11: istore_3
    //   12: iload_3
    //   13: iconst_3
    //   14: if_icmpge +25 -> 39
    //   17: aload 6
    //   19: new 313	java/util/ArrayList
    //   22: dup
    //   23: invokespecial 315	java/util/ArrayList:<init>	()V
    //   26: invokeinterface 321 2 0
    //   31: pop
    //   32: iload_3
    //   33: iconst_1
    //   34: iadd
    //   35: istore_3
    //   36: goto -24 -> 12
    //   39: new 323	java/io/FileInputStream
    //   42: dup
    //   43: aload_1
    //   44: invokespecial 325	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   47: astore 7
    //   49: invokestatic 331	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   52: astore 8
    //   54: aload 8
    //   56: aload 7
    //   58: aconst_null
    //   59: invokeinterface 335 3 0
    //   64: aload 8
    //   66: ldc 42
    //   68: invokestatic 341	com/android/internal/util/XmlUtils:beginDocument	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)V
    //   71: aload 8
    //   73: invokeinterface 344 1 0
    //   78: istore 4
    //   80: aload 8
    //   82: iload 4
    //   84: invokestatic 348	com/android/internal/util/XmlUtils:nextElementWithin	(Lorg/xmlpull/v1/XmlPullParser;I)Z
    //   87: ifeq +208 -> 295
    //   90: iconst_m1
    //   91: istore_3
    //   92: aload 8
    //   94: invokeinterface 294 1 0
    //   99: astore 9
    //   101: aload 9
    //   103: ldc 36
    //   105: invokevirtual 351	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   108: ifeq +90 -> 198
    //   111: iconst_0
    //   112: istore_3
    //   113: iload_3
    //   114: iconst_m1
    //   115: if_icmpeq -35 -> 80
    //   118: new 18	com/android/server/firewall/IntentFirewall$Rule
    //   121: dup
    //   122: aconst_null
    //   123: invokespecial 354	com/android/server/firewall/IntentFirewall$Rule:<init>	(Lcom/android/server/firewall/IntentFirewall$Rule;)V
    //   126: astore 9
    //   128: aload 6
    //   130: iload_3
    //   131: invokeinterface 357 2 0
    //   136: checkcast 317	java/util/List
    //   139: astore 10
    //   141: aload 9
    //   143: aload 8
    //   145: invokevirtual 361	com/android/server/firewall/IntentFirewall$Rule:readFromXml	(Lorg/xmlpull/v1/XmlPullParser;)Lcom/android/server/firewall/IntentFirewall$Rule;
    //   148: pop
    //   149: aload 10
    //   151: aload 9
    //   153: invokeinterface 321 2 0
    //   158: pop
    //   159: goto -79 -> 80
    //   162: astore_2
    //   163: ldc 33
    //   165: new 209	java/lang/StringBuilder
    //   168: dup
    //   169: invokespecial 210	java/lang/StringBuilder:<init>	()V
    //   172: ldc_w 363
    //   175: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   178: aload_1
    //   179: invokevirtual 366	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   182: invokevirtual 227	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   185: aload_2
    //   186: invokestatic 283	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   189: pop
    //   190: aload 7
    //   192: invokevirtual 369	java/io/FileInputStream:close	()V
    //   195: return
    //   196: astore_1
    //   197: return
    //   198: aload 9
    //   200: ldc 39
    //   202: invokevirtual 351	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   205: ifeq +8 -> 213
    //   208: iconst_1
    //   209: istore_3
    //   210: goto -97 -> 113
    //   213: aload 9
    //   215: ldc 45
    //   217: invokevirtual 351	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   220: ifeq -107 -> 113
    //   223: iconst_2
    //   224: istore_3
    //   225: goto -112 -> 113
    //   228: astore 9
    //   230: ldc 33
    //   232: new 209	java/lang/StringBuilder
    //   235: dup
    //   236: invokespecial 210	java/lang/StringBuilder:<init>	()V
    //   239: ldc_w 371
    //   242: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   245: aload_1
    //   246: invokevirtual 366	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   249: invokevirtual 227	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   252: aload 9
    //   254: invokestatic 283	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   257: pop
    //   258: goto -178 -> 80
    //   261: astore_2
    //   262: ldc 33
    //   264: new 209	java/lang/StringBuilder
    //   267: dup
    //   268: invokespecial 210	java/lang/StringBuilder:<init>	()V
    //   271: ldc_w 363
    //   274: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   277: aload_1
    //   278: invokevirtual 366	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   281: invokevirtual 227	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   284: aload_2
    //   285: invokestatic 283	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   288: pop
    //   289: aload 7
    //   291: invokevirtual 369	java/io/FileInputStream:close	()V
    //   294: return
    //   295: aload 7
    //   297: invokevirtual 369	java/io/FileInputStream:close	()V
    //   300: iconst_0
    //   301: istore_3
    //   302: iload_3
    //   303: aload 6
    //   305: invokeinterface 374 1 0
    //   310: if_icmpge +265 -> 575
    //   313: aload 6
    //   315: iload_3
    //   316: invokeinterface 357 2 0
    //   321: checkcast 317	java/util/List
    //   324: astore_1
    //   325: aload_2
    //   326: iload_3
    //   327: aaload
    //   328: astore 7
    //   330: iconst_0
    //   331: istore 4
    //   333: iload 4
    //   335: aload_1
    //   336: invokeinterface 374 1 0
    //   341: if_icmpge +227 -> 568
    //   344: aload_1
    //   345: iload 4
    //   347: invokeinterface 357 2 0
    //   352: checkcast 18	com/android/server/firewall/IntentFirewall$Rule
    //   355: astore 8
    //   357: iconst_0
    //   358: istore 5
    //   360: iload 5
    //   362: aload 8
    //   364: invokevirtual 377	com/android/server/firewall/IntentFirewall$Rule:getIntentFilterCount	()I
    //   367: if_icmpge +156 -> 523
    //   370: aload 7
    //   372: aload 8
    //   374: iload 5
    //   376: invokevirtual 381	com/android/server/firewall/IntentFirewall$Rule:getIntentFilter	(I)Lcom/android/server/firewall/IntentFirewall$FirewallIntentFilter;
    //   379: invokevirtual 385	com/android/server/firewall/IntentFirewall$FirewallIntentResolver:addFilter	(Landroid/content/IntentFilter;)V
    //   382: iload 5
    //   384: iconst_1
    //   385: iadd
    //   386: istore 5
    //   388: goto -28 -> 360
    //   391: astore 7
    //   393: ldc 33
    //   395: new 209	java/lang/StringBuilder
    //   398: dup
    //   399: invokespecial 210	java/lang/StringBuilder:<init>	()V
    //   402: ldc_w 387
    //   405: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   408: aload_1
    //   409: invokevirtual 366	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   412: invokevirtual 227	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   415: aload 7
    //   417: invokestatic 283	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   420: pop
    //   421: goto -121 -> 300
    //   424: astore_2
    //   425: ldc 33
    //   427: new 209	java/lang/StringBuilder
    //   430: dup
    //   431: invokespecial 210	java/lang/StringBuilder:<init>	()V
    //   434: ldc_w 387
    //   437: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   440: aload_1
    //   441: invokevirtual 366	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   444: invokevirtual 227	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   447: aload_2
    //   448: invokestatic 283	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   451: pop
    //   452: return
    //   453: astore_2
    //   454: ldc 33
    //   456: new 209	java/lang/StringBuilder
    //   459: dup
    //   460: invokespecial 210	java/lang/StringBuilder:<init>	()V
    //   463: ldc_w 387
    //   466: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   469: aload_1
    //   470: invokevirtual 366	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   473: invokevirtual 227	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   476: aload_2
    //   477: invokestatic 283	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   480: pop
    //   481: return
    //   482: astore_2
    //   483: aload 7
    //   485: invokevirtual 369	java/io/FileInputStream:close	()V
    //   488: aload_2
    //   489: athrow
    //   490: astore 6
    //   492: ldc 33
    //   494: new 209	java/lang/StringBuilder
    //   497: dup
    //   498: invokespecial 210	java/lang/StringBuilder:<init>	()V
    //   501: ldc_w 387
    //   504: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   507: aload_1
    //   508: invokevirtual 366	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   511: invokevirtual 227	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   514: aload 6
    //   516: invokestatic 283	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   519: pop
    //   520: goto -32 -> 488
    //   523: iconst_0
    //   524: istore 5
    //   526: iload 5
    //   528: aload 8
    //   530: invokevirtual 390	com/android/server/firewall/IntentFirewall$Rule:getComponentFilterCount	()I
    //   533: if_icmpge +26 -> 559
    //   536: aload 7
    //   538: aload 8
    //   540: iload 5
    //   542: invokevirtual 394	com/android/server/firewall/IntentFirewall$Rule:getComponentFilter	(I)Landroid/content/ComponentName;
    //   545: aload 8
    //   547: invokevirtual 398	com/android/server/firewall/IntentFirewall$FirewallIntentResolver:addComponentFilter	(Landroid/content/ComponentName;Lcom/android/server/firewall/IntentFirewall$Rule;)V
    //   550: iload 5
    //   552: iconst_1
    //   553: iadd
    //   554: istore 5
    //   556: goto -30 -> 526
    //   559: iload 4
    //   561: iconst_1
    //   562: iadd
    //   563: istore 4
    //   565: goto -232 -> 333
    //   568: iload_3
    //   569: iconst_1
    //   570: iadd
    //   571: istore_3
    //   572: goto -270 -> 302
    //   575: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	576	0	this	IntentFirewall
    //   0	576	1	paramFile	File
    //   0	576	2	paramArrayOfFirewallIntentResolver	FirewallIntentResolver[]
    //   11	561	3	i	int
    //   78	486	4	j	int
    //   358	197	5	k	int
    //   8	306	6	localArrayList	ArrayList
    //   490	25	6	localIOException1	IOException
    //   47	324	7	localObject1	Object
    //   391	146	7	localIOException2	IOException
    //   52	494	8	localObject2	Object
    //   99	115	9	localObject3	Object
    //   228	25	9	localXmlPullParserException	XmlPullParserException
    //   139	11	10	localList	List
    // Exception table:
    //   from	to	target	type
    //   49	80	162	org/xmlpull/v1/XmlPullParserException
    //   80	90	162	org/xmlpull/v1/XmlPullParserException
    //   92	111	162	org/xmlpull/v1/XmlPullParserException
    //   118	141	162	org/xmlpull/v1/XmlPullParserException
    //   149	159	162	org/xmlpull/v1/XmlPullParserException
    //   198	208	162	org/xmlpull/v1/XmlPullParserException
    //   213	223	162	org/xmlpull/v1/XmlPullParserException
    //   230	258	162	org/xmlpull/v1/XmlPullParserException
    //   39	49	196	java/io/FileNotFoundException
    //   141	149	228	org/xmlpull/v1/XmlPullParserException
    //   49	80	261	java/io/IOException
    //   80	90	261	java/io/IOException
    //   92	111	261	java/io/IOException
    //   118	141	261	java/io/IOException
    //   141	149	261	java/io/IOException
    //   149	159	261	java/io/IOException
    //   198	208	261	java/io/IOException
    //   213	223	261	java/io/IOException
    //   230	258	261	java/io/IOException
    //   295	300	391	java/io/IOException
    //   289	294	424	java/io/IOException
    //   190	195	453	java/io/IOException
    //   49	80	482	finally
    //   80	90	482	finally
    //   92	111	482	finally
    //   118	141	482	finally
    //   141	149	482	finally
    //   149	159	482	finally
    //   163	190	482	finally
    //   198	208	482	finally
    //   213	223	482	finally
    //   230	258	482	finally
    //   262	289	482	finally
    //   483	488	490	java/io/IOException
  }
  
  private void readRulesDir(File arg1)
  {
    FirewallIntentResolver[] arrayOfFirewallIntentResolver = new FirewallIntentResolver[3];
    int i = 0;
    while (i < arrayOfFirewallIntentResolver.length)
    {
      arrayOfFirewallIntentResolver[i] = new FirewallIntentResolver(null);
      i += 1;
    }
    ??? = ???.listFiles();
    if (??? != null)
    {
      i = 0;
      while (i < ???.length)
      {
        File localFile = ???[i];
        if (localFile.getName().endsWith(".xml")) {
          readRules(localFile, arrayOfFirewallIntentResolver);
        }
        i += 1;
      }
    }
    Slog.i("IntentFirewall", "Read new rules (A:" + arrayOfFirewallIntentResolver[0].filterSet().size() + " B:" + arrayOfFirewallIntentResolver[1].filterSet().size() + " S:" + arrayOfFirewallIntentResolver[2].filterSet().size() + ")");
    synchronized (this.mAms.getAMSLock())
    {
      this.mActivityResolver = arrayOfFirewallIntentResolver[0];
      this.mBroadcastResolver = arrayOfFirewallIntentResolver[1];
      this.mServiceResolver = arrayOfFirewallIntentResolver[2];
      return;
    }
  }
  
  public boolean checkBroadcast(Intent paramIntent, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    return checkIntent(this.mBroadcastResolver, paramIntent.getComponent(), 1, paramIntent, paramInt1, paramInt2, paramString, paramInt3);
  }
  
  boolean checkComponentPermission(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    return this.mAms.checkComponentPermission(paramString, paramInt1, paramInt2, paramInt3, paramBoolean) == 0;
  }
  
  public boolean checkIntent(FirewallIntentResolver paramFirewallIntentResolver, ComponentName paramComponentName, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, String paramString, int paramInt4)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    List localList = paramFirewallIntentResolver.queryIntent(paramIntent, paramString, false, 0);
    Object localObject = localList;
    if (localList == null) {
      localObject = new ArrayList();
    }
    paramFirewallIntentResolver.queryByComponent(paramComponentName, (List)localObject);
    int i = 0;
    for (;;)
    {
      boolean bool4 = bool2;
      boolean bool3 = bool1;
      if (i < ((List)localObject).size())
      {
        paramFirewallIntentResolver = (Rule)((List)localObject).get(i);
        bool3 = bool2;
        bool4 = bool1;
        if (paramFirewallIntentResolver.matches(this, paramComponentName, paramIntent, paramInt2, paramInt3, paramString, paramInt4))
        {
          bool2 |= paramFirewallIntentResolver.getBlock();
          bool1 |= paramFirewallIntentResolver.getLog();
          bool3 = bool2;
          bool4 = bool1;
          if (bool2)
          {
            bool3 = bool2;
            bool4 = bool1;
            if (bool1)
            {
              bool3 = bool1;
              bool4 = bool2;
            }
          }
        }
      }
      else
      {
        if (bool3) {
          logIntent(paramInt1, paramIntent, paramInt2, paramString);
        }
        if (!bool4) {
          break;
        }
        return false;
      }
      i += 1;
      bool2 = bool3;
      bool1 = bool4;
    }
    return true;
  }
  
  public boolean checkService(ComponentName paramComponentName, Intent paramIntent, int paramInt1, int paramInt2, String paramString, ApplicationInfo paramApplicationInfo)
  {
    return checkIntent(this.mServiceResolver, paramComponentName, 2, paramIntent, paramInt1, paramInt2, paramString, paramApplicationInfo.uid);
  }
  
  public boolean checkStartActivity(Intent paramIntent, int paramInt1, int paramInt2, String paramString, ApplicationInfo paramApplicationInfo)
  {
    return checkIntent(this.mActivityResolver, paramIntent.getComponent(), 0, paramIntent, paramInt1, paramInt2, paramString, paramApplicationInfo.uid);
  }
  
  boolean signaturesMatch(int paramInt1, int paramInt2)
  {
    boolean bool = false;
    try
    {
      paramInt1 = AppGlobals.getPackageManager().checkUidSignatures(paramInt1, paramInt2);
      if (paramInt1 == 0) {
        bool = true;
      }
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("IntentFirewall", "Remote exception while checking signatures", localRemoteException);
    }
    return false;
  }
  
  public static abstract interface AMSInterface
  {
    public abstract int checkComponentPermission(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
    
    public abstract Object getAMSLock();
  }
  
  private final class FirewallHandler
    extends Handler
  {
    public FirewallHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      IntentFirewall.-wrap0(IntentFirewall.this, IntentFirewall.getRulesDir());
    }
  }
  
  private static class FirewallIntentFilter
    extends IntentFilter
  {
    private final IntentFirewall.Rule rule;
    
    public FirewallIntentFilter(IntentFirewall.Rule paramRule)
    {
      this.rule = paramRule;
    }
  }
  
  private static class FirewallIntentResolver
    extends IntentResolver<IntentFirewall.FirewallIntentFilter, IntentFirewall.Rule>
  {
    private final ArrayMap<ComponentName, IntentFirewall.Rule[]> mRulesByComponent = new ArrayMap(0);
    
    public void addComponentFilter(ComponentName paramComponentName, IntentFirewall.Rule paramRule)
    {
      paramRule = (IntentFirewall.Rule[])ArrayUtils.appendElement(IntentFirewall.Rule.class, (IntentFirewall.Rule[])this.mRulesByComponent.get(paramComponentName), paramRule);
      this.mRulesByComponent.put(paramComponentName, paramRule);
    }
    
    protected boolean allowFilterResult(IntentFirewall.FirewallIntentFilter paramFirewallIntentFilter, List<IntentFirewall.Rule> paramList)
    {
      return !paramList.contains(IntentFirewall.FirewallIntentFilter.-get0(paramFirewallIntentFilter));
    }
    
    protected boolean isPackageForFilter(String paramString, IntentFirewall.FirewallIntentFilter paramFirewallIntentFilter)
    {
      return true;
    }
    
    protected IntentFirewall.FirewallIntentFilter[] newArray(int paramInt)
    {
      return new IntentFirewall.FirewallIntentFilter[paramInt];
    }
    
    protected IntentFirewall.Rule newResult(IntentFirewall.FirewallIntentFilter paramFirewallIntentFilter, int paramInt1, int paramInt2)
    {
      return IntentFirewall.FirewallIntentFilter.-get0(paramFirewallIntentFilter);
    }
    
    public void queryByComponent(ComponentName paramComponentName, List<IntentFirewall.Rule> paramList)
    {
      paramComponentName = (IntentFirewall.Rule[])this.mRulesByComponent.get(paramComponentName);
      if (paramComponentName != null) {
        paramList.addAll(Arrays.asList(paramComponentName));
      }
    }
    
    protected void sortResults(List<IntentFirewall.Rule> paramList) {}
  }
  
  private static class Rule
    extends AndFilter
  {
    private static final String ATTR_BLOCK = "block";
    private static final String ATTR_LOG = "log";
    private static final String ATTR_NAME = "name";
    private static final String TAG_COMPONENT_FILTER = "component-filter";
    private static final String TAG_INTENT_FILTER = "intent-filter";
    private boolean block;
    private boolean log;
    private final ArrayList<ComponentName> mComponentFilters = new ArrayList(0);
    private final ArrayList<IntentFirewall.FirewallIntentFilter> mIntentFilters = new ArrayList(1);
    
    public boolean getBlock()
    {
      return this.block;
    }
    
    public ComponentName getComponentFilter(int paramInt)
    {
      return (ComponentName)this.mComponentFilters.get(paramInt);
    }
    
    public int getComponentFilterCount()
    {
      return this.mComponentFilters.size();
    }
    
    public IntentFirewall.FirewallIntentFilter getIntentFilter(int paramInt)
    {
      return (IntentFirewall.FirewallIntentFilter)this.mIntentFilters.get(paramInt);
    }
    
    public int getIntentFilterCount()
    {
      return this.mIntentFilters.size();
    }
    
    public boolean getLog()
    {
      return this.log;
    }
    
    protected void readChild(XmlPullParser paramXmlPullParser)
      throws IOException, XmlPullParserException
    {
      Object localObject = paramXmlPullParser.getName();
      if (((String)localObject).equals("intent-filter"))
      {
        localObject = new IntentFirewall.FirewallIntentFilter(this);
        ((IntentFirewall.FirewallIntentFilter)localObject).readFromXml(paramXmlPullParser);
        this.mIntentFilters.add(localObject);
        return;
      }
      if (((String)localObject).equals("component-filter"))
      {
        localObject = paramXmlPullParser.getAttributeValue(null, "name");
        if (localObject == null) {
          throw new XmlPullParserException("Component name must be specified.", paramXmlPullParser, null);
        }
        paramXmlPullParser = ComponentName.unflattenFromString((String)localObject);
        if (paramXmlPullParser == null) {
          throw new XmlPullParserException("Invalid component name: " + (String)localObject);
        }
        this.mComponentFilters.add(paramXmlPullParser);
        return;
      }
      super.readChild(paramXmlPullParser);
    }
    
    public Rule readFromXml(XmlPullParser paramXmlPullParser)
      throws IOException, XmlPullParserException
    {
      this.block = Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, "block"));
      this.log = Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, "log"));
      super.readFromXml(paramXmlPullParser);
      return this;
    }
  }
  
  private class RuleObserver
    extends FileObserver
  {
    private static final int MONITORED_EVENTS = 968;
    
    public RuleObserver(File paramFile)
    {
      super(968);
    }
    
    public void onEvent(int paramInt, String paramString)
    {
      if (paramString.endsWith(".xml"))
      {
        IntentFirewall.this.mHandler.removeMessages(0);
        IntentFirewall.this.mHandler.sendEmptyMessageDelayed(0, 250L);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/firewall/IntentFirewall.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */