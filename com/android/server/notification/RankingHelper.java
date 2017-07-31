package com.android.server.notification;

import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.os.Build;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.notification.NotificationListenerService.Ranking;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Slog;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class RankingHelper
  implements RankingConfig
{
  private static final String ATT_IMPORTANCE = "importance";
  private static final String ATT_NAME = "name";
  private static final String ATT_OPLEVEL = "opLevel";
  private static final String ATT_PRIORITY = "priority";
  private static final String ATT_TOPIC_ID = "id";
  private static final String ATT_TOPIC_LABEL = "label";
  private static final String ATT_UID = "uid";
  private static final String ATT_VERSION = "version";
  private static final String ATT_VISIBILITY = "visibility";
  private static final List<String> DEFAULT_DENOISE_LIST = Arrays.asList(new String[] { "com.oneplus.screenshot" });
  private static final int DEFAULT_IMPORTANCE = -1000;
  private static final int DEFAULT_OPLEVEL = 0;
  private static final int DEFAULT_PRIORITY = 0;
  private static final int DEFAULT_VISIBILITY = -1000;
  private static final boolean IS_CHINA_SKU = OpFeatures.isSupport(new int[] { 0 });
  private static final String TAG = "RankingHelper";
  private static final String TAG_PACKAGE = "package";
  private static final String TAG_RANKING = "ranking";
  private static final int XML_VERSION = 4;
  private final Context mContext;
  private final GlobalSortKeyComparator mFinalComparator = new GlobalSortKeyComparator();
  private final NotificationComparator mPreliminaryComparator = new NotificationComparator();
  private final ArrayMap<String, NotificationRecord> mProxyByGroupTmp = new ArrayMap();
  private final RankingHandler mRankingHandler;
  private final ArrayMap<String, Record> mRecords = new ArrayMap();
  private final ArrayMap<String, Record> mRestoredWithoutUids = new ArrayMap();
  private final NotificationSignalExtractor[] mSignalExtractors;
  
  public RankingHelper(Context paramContext, RankingHandler paramRankingHandler, NotificationUsageStats paramNotificationUsageStats, String[] paramArrayOfString)
  {
    this.mContext = paramContext;
    this.mRankingHandler = paramRankingHandler;
    int j = paramArrayOfString.length;
    this.mSignalExtractors = new NotificationSignalExtractor[j];
    int i = 0;
    for (;;)
    {
      if (i < j) {
        try
        {
          paramContext = (NotificationSignalExtractor)this.mContext.getClassLoader().loadClass(paramArrayOfString[i]).newInstance();
          paramContext.initialize(this.mContext, paramNotificationUsageStats);
          paramContext.setConfig(this);
          this.mSignalExtractors[i] = paramContext;
          i += 1;
        }
        catch (IllegalAccessException paramContext)
        {
          for (;;)
          {
            Slog.w("RankingHelper", "Problem accessing extractor " + paramArrayOfString[i] + ".", paramContext);
          }
        }
        catch (InstantiationException paramContext)
        {
          for (;;)
          {
            Slog.w("RankingHelper", "Couldn't instantiate extractor " + paramArrayOfString[i] + ".", paramContext);
          }
        }
        catch (ClassNotFoundException paramContext)
        {
          for (;;)
          {
            Slog.w("RankingHelper", "Couldn't find extractor " + paramArrayOfString[i] + ".", paramContext);
          }
        }
      }
    }
    OnlineConfigUtil.init(this.mContext, (Handler)this.mRankingHandler);
  }
  
  private void dataMigrateProcess()
  {
    Iterator localIterator = UserManager.get(this.mContext).getUsers().iterator();
    while (localIterator.hasNext())
    {
      int j = ((UserInfo)localIterator.next()).getUserHandle().getIdentifier();
      List localList = this.mContext.getPackageManager().getInstalledPackagesAsUser(0, j);
      int k = localList.size();
      int i = 0;
      while (i < k)
      {
        setDefaultOPLevel(j, ((PackageInfo)localList.get(i)).packageName);
        i += 1;
      }
    }
  }
  
  private static void dumpRecords(PrintWriter paramPrintWriter, String paramString, NotificationManagerService.DumpFilter paramDumpFilter, ArrayMap<String, Record> paramArrayMap)
  {
    int j = paramArrayMap.size();
    int i = 0;
    if (i < j)
    {
      Record localRecord = (Record)paramArrayMap.valueAt(i);
      if ((paramDumpFilter == null) || (paramDumpFilter.matches(localRecord.pkg)))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  ");
        paramPrintWriter.print(localRecord.pkg);
        paramPrintWriter.print(" (");
        if (localRecord.uid != Record.UNKNOWN_UID) {
          break label226;
        }
      }
      label226:
      for (String str = "UNKNOWN_UID";; str = Integer.toString(localRecord.uid))
      {
        paramPrintWriter.print(str);
        paramPrintWriter.print(')');
        if (localRecord.importance != 64536)
        {
          paramPrintWriter.print(" importance=");
          paramPrintWriter.print(NotificationListenerService.Ranking.importanceToString(localRecord.importance));
        }
        if (localRecord.priority != 0)
        {
          paramPrintWriter.print(" priority=");
          paramPrintWriter.print(Notification.priorityToString(localRecord.priority));
        }
        if (localRecord.visibility != 64536)
        {
          paramPrintWriter.print(" visibility=");
          paramPrintWriter.print(Notification.visibilityToString(localRecord.visibility));
        }
        if (localRecord.opLevel != 0)
        {
          paramPrintWriter.print(" opLevel=");
          paramPrintWriter.print(NotificationListenerService.Ranking.opLevelToString(localRecord.opLevel));
        }
        paramPrintWriter.println();
        i += 1;
        break;
      }
    }
  }
  
  private Record getOrCreateRecord(String paramString, int paramInt)
  {
    String str = recordKey(paramString, paramInt);
    Record localRecord2 = (Record)this.mRecords.get(str);
    Record localRecord1 = localRecord2;
    if (localRecord2 == null)
    {
      localRecord1 = new Record(null);
      localRecord1.pkg = paramString;
      localRecord1.uid = paramInt;
      this.mRecords.put(str, localRecord1);
    }
    return localRecord1;
  }
  
  private static String recordKey(String paramString, int paramInt)
  {
    return paramString + "|" + paramInt;
  }
  
  private static int safeInt(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    return tryParseInt(paramXmlPullParser.getAttributeValue(null, paramString), paramInt);
  }
  
  private void syncImportanceToOPLevel(Record paramRecord, int paramInt)
  {
    if (paramInt == 0) {
      paramRecord.opLevel = 2;
    }
    for (;;)
    {
      if (Build.DEBUG_ONEPLUS) {
        Log.d("RankingHelper", "sync pkg " + paramRecord.pkg + " importance " + NotificationListenerService.Ranking.importanceToString(paramRecord.importance) + " to opLevel " + NotificationListenerService.Ranking.opLevelToString(paramRecord.opLevel));
      }
      return;
      if (paramInt == 1) {
        paramRecord.opLevel = 1;
      } else {
        paramRecord.opLevel = 0;
      }
    }
  }
  
  private void syncOPLevelToImportance(Record paramRecord, int paramInt, boolean paramBoolean)
  {
    if (paramInt == 1) {}
    for (paramRecord.importance = 1;; paramRecord.importance = 0)
    {
      if (Build.DEBUG_ONEPLUS) {
        Log.d("RankingHelper", "sync pkg " + paramRecord.pkg + " opLevel " + NotificationListenerService.Ranking.opLevelToString(paramRecord.opLevel) + " to importance " + NotificationListenerService.Ranking.importanceToString(paramRecord.importance));
      }
      return;
      if (paramInt != 2) {
        break;
      }
    }
    if (paramBoolean) {}
    for (paramInt = 1000;; paramInt = 64536)
    {
      paramRecord.importance = paramInt;
      break;
    }
  }
  
  private static boolean tryParseBool(String paramString, boolean paramBoolean)
  {
    if (TextUtils.isEmpty(paramString)) {
      return paramBoolean;
    }
    return Boolean.valueOf(paramString).booleanValue();
  }
  
  private static int tryParseInt(String paramString, int paramInt)
  {
    if (TextUtils.isEmpty(paramString)) {
      return paramInt;
    }
    try
    {
      int i = Integer.parseInt(paramString);
      return i;
    }
    catch (NumberFormatException paramString) {}
    return paramInt;
  }
  
  private void updateConfig()
  {
    int j = this.mSignalExtractors.length;
    int i = 0;
    while (i < j)
    {
      this.mSignalExtractors[i].setConfig(this);
      i += 1;
    }
    this.mRankingHandler.requestSort();
  }
  
  public boolean correctImportance()
  {
    boolean bool1 = false;
    int j = this.mRecords.size();
    int i = 0;
    while (i < j)
    {
      Record localRecord = (Record)this.mRecords.valueAt(i);
      if (localRecord.importance == 1000)
      {
        localRecord.importance = 64536;
        boolean bool2 = true;
        bool1 = bool2;
        if (Build.DEBUG_ONEPLUS)
        {
          Log.i("RankingHelper", "correct pkg " + localRecord.pkg + " uid " + localRecord.uid + " importance from GOINGTO_UNSPECIFIED to UNSPECIFIED");
          bool1 = bool2;
        }
      }
      i += 1;
    }
    return bool1;
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString, NotificationManagerService.DumpFilter paramDumpFilter)
  {
    if (paramDumpFilter == null)
    {
      int j = this.mSignalExtractors.length;
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSignalExtractors.length = ");
      paramPrintWriter.println(j);
      int i = 0;
      while (i < j)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  ");
        paramPrintWriter.println(this.mSignalExtractors[i]);
        i += 1;
      }
    }
    if (paramDumpFilter == null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("per-package config:");
    }
    paramPrintWriter.println("Records:");
    dumpRecords(paramPrintWriter, paramString, paramDumpFilter, this.mRecords);
    paramPrintWriter.println("Restored without uid:");
    dumpRecords(paramPrintWriter, paramString, paramDumpFilter, this.mRestoredWithoutUids);
  }
  
  public JSONArray dumpBansJson(NotificationManagerService.DumpFilter paramDumpFilter)
  {
    JSONArray localJSONArray = new JSONArray();
    Iterator localIterator = getPackageBans().entrySet().iterator();
    for (;;)
    {
      if (localIterator.hasNext())
      {
        Object localObject = (Map.Entry)localIterator.next();
        int i = UserHandle.getUserId(((Integer)((Map.Entry)localObject).getKey()).intValue());
        String str = (String)((Map.Entry)localObject).getValue();
        if ((paramDumpFilter != null) && (!paramDumpFilter.matches(str))) {
          continue;
        }
        localObject = new JSONObject();
        try
        {
          ((JSONObject)localObject).put("userId", i);
          ((JSONObject)localObject).put("packageName", str);
          localJSONArray.put(localObject);
        }
        catch (JSONException localJSONException)
        {
          for (;;)
          {
            localJSONException.printStackTrace();
          }
        }
      }
    }
    return localJSONArray;
  }
  
  public JSONObject dumpJson(NotificationManagerService.DumpFilter paramDumpFilter)
  {
    JSONObject localJSONObject1 = new JSONObject();
    JSONArray localJSONArray = new JSONArray();
    try
    {
      localJSONObject1.put("noUid", this.mRestoredWithoutUids.size());
      int j = this.mRecords.size();
      int i = 0;
      while (i < j)
      {
        Record localRecord = (Record)this.mRecords.valueAt(i);
        JSONObject localJSONObject2;
        if ((paramDumpFilter == null) || (paramDumpFilter.matches(localRecord.pkg))) {
          localJSONObject2 = new JSONObject();
        }
        try
        {
          localJSONObject2.put("userId", UserHandle.getUserId(localRecord.uid));
          localJSONObject2.put("packageName", localRecord.pkg);
          if (localRecord.importance != 64536) {
            localJSONObject2.put("importance", NotificationListenerService.Ranking.importanceToString(localRecord.importance));
          }
          if (localRecord.priority != 0) {
            localJSONObject2.put("priority", Notification.priorityToString(localRecord.priority));
          }
          if (localRecord.visibility != 64536) {
            localJSONObject2.put("visibility", Notification.visibilityToString(localRecord.visibility));
          }
          if (localRecord.opLevel != 0) {
            localJSONObject2.put("opLevel", NotificationListenerService.Ranking.opLevelToString(localRecord.opLevel));
          }
        }
        catch (JSONException localJSONException2)
        {
          for (;;) {}
        }
        localJSONArray.put(localJSONObject2);
        i += 1;
      }
      try
      {
        localJSONObject1.put("records", localJSONArray);
        return localJSONObject1;
      }
      catch (JSONException paramDumpFilter)
      {
        return localJSONObject1;
      }
    }
    catch (JSONException localJSONException1)
    {
      for (;;) {}
    }
  }
  
  public void extractSignals(NotificationRecord paramNotificationRecord)
  {
    int j = this.mSignalExtractors.length;
    int i = 0;
    while (i < j)
    {
      Object localObject = this.mSignalExtractors[i];
      try
      {
        localObject = ((NotificationSignalExtractor)localObject).process(paramNotificationRecord);
        if (localObject != null) {
          this.mRankingHandler.requestReconsideration((RankingReconsideration)localObject);
        }
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          Slog.w("RankingHelper", "NotificationSignalExtractor failed.", localThrowable);
        }
      }
      i += 1;
    }
  }
  
  public <T extends NotificationSignalExtractor> T findExtractor(Class<T> paramClass)
  {
    int j = this.mSignalExtractors.length;
    int i = 0;
    while (i < j)
    {
      NotificationSignalExtractor localNotificationSignalExtractor = this.mSignalExtractors[i];
      if (paramClass.equals(localNotificationSignalExtractor.getClass())) {
        return localNotificationSignalExtractor;
      }
      i += 1;
    }
    return null;
  }
  
  public List<String> getDefaultDenoiseList()
  {
    return DEFAULT_DENOISE_LIST;
  }
  
  public int getImportance(String paramString, int paramInt)
  {
    return getOrCreateRecord(paramString, paramInt).importance;
  }
  
  public int getOPLevel(String paramString, int paramInt)
  {
    return getOrCreateRecord(paramString, paramInt).opLevel;
  }
  
  public Map<Integer, String> getPackageBans()
  {
    int j = this.mRecords.size();
    ArrayMap localArrayMap = new ArrayMap(j);
    int i = 0;
    while (i < j)
    {
      Record localRecord = (Record)this.mRecords.valueAt(i);
      if (localRecord.importance == 0) {
        localArrayMap.put(Integer.valueOf(localRecord.uid), localRecord.pkg);
      }
      i += 1;
    }
    return localArrayMap;
  }
  
  public int getPriority(String paramString, int paramInt)
  {
    return getOrCreateRecord(paramString, paramInt).priority;
  }
  
  public int getVisibilityOverride(String paramString, int paramInt)
  {
    return getOrCreateRecord(paramString, paramInt).visibility;
  }
  
  public int indexOf(ArrayList<NotificationRecord> paramArrayList, NotificationRecord paramNotificationRecord)
  {
    return Collections.binarySearch(paramArrayList, paramNotificationRecord, this.mFinalComparator);
  }
  
  public void onPackagesChanged(boolean paramBoolean, String[] paramArrayOfString)
  {
    int i = 0;
    if ((!paramBoolean) || (paramArrayOfString == null)) {}
    while ((paramArrayOfString.length == 0) || (this.mRestoredWithoutUids.isEmpty())) {
      return;
    }
    PackageManager localPackageManager = this.mContext.getPackageManager();
    j = 0;
    int m = paramArrayOfString.length;
    while (i < m)
    {
      String str = paramArrayOfString[i];
      Record localRecord = (Record)this.mRestoredWithoutUids.get(str);
      k = j;
      if (localRecord != null) {}
      try
      {
        localRecord.uid = localPackageManager.getPackageUidAsUser(localRecord.pkg, 0);
        this.mRestoredWithoutUids.remove(str);
        this.mRecords.put(recordKey(localRecord.pkg, localRecord.uid), localRecord);
        k = 1;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        for (;;)
        {
          k = j;
        }
      }
      i += 1;
      j = k;
    }
    if (j != 0) {
      updateConfig();
    }
  }
  
  /* Error */
  public void readXml(XmlPullParser paramXmlPullParser, boolean paramBoolean, List<String> paramList)
    throws org.xmlpull.v1.XmlPullParserException, IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 130	com/android/server/notification/RankingHelper:mContext	Landroid/content/Context;
    //   4: invokevirtual 237	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   7: astore 9
    //   9: aload_1
    //   10: invokeinterface 580 1 0
    //   15: iconst_2
    //   16: if_icmpeq +4 -> 20
    //   19: return
    //   20: ldc 58
    //   22: aload_1
    //   23: invokeinterface 583 1 0
    //   28: invokevirtual 584	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   31: ifne +4 -> 35
    //   34: return
    //   35: aload_0
    //   36: getfield 124	com/android/server/notification/RankingHelper:mRecords	Landroid/util/ArrayMap;
    //   39: invokevirtual 587	android/util/ArrayMap:clear	()V
    //   42: aload_0
    //   43: getfield 128	com/android/server/notification/RankingHelper:mRestoredWithoutUids	Landroid/util/ArrayMap;
    //   46: invokevirtual 587	android/util/ArrayMap:clear	()V
    //   49: aload_1
    //   50: ldc 34
    //   52: iconst_1
    //   53: invokestatic 589	com/android/server/notification/RankingHelper:safeInt	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   56: istore 6
    //   58: iload 6
    //   60: iconst_3
    //   61: if_icmpgt +185 -> 246
    //   64: getstatic 86	com/android/server/notification/RankingHelper:IS_CHINA_SKU	Z
    //   67: ifeq +7 -> 74
    //   70: aload_0
    //   71: invokespecial 591	com/android/server/notification/RankingHelper:dataMigrateProcess	()V
    //   74: return
    //   75: astore 7
    //   77: iload 5
    //   79: istore 4
    //   81: iload 4
    //   83: getstatic 290	com/android/server/notification/RankingHelper$Record:UNKNOWN_UID	I
    //   86: if_icmpne +266 -> 352
    //   89: aload_0
    //   90: getfield 128	com/android/server/notification/RankingHelper:mRestoredWithoutUids	Landroid/util/ArrayMap;
    //   93: aload 10
    //   95: invokevirtual 345	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   98: checkcast 8	com/android/server/notification/RankingHelper$Record
    //   101: astore 8
    //   103: aload 8
    //   105: astore 7
    //   107: aload 8
    //   109: ifnonnull +25 -> 134
    //   112: new 8	com/android/server/notification/RankingHelper$Record
    //   115: dup
    //   116: aconst_null
    //   117: invokespecial 348	com/android/server/notification/RankingHelper$Record:<init>	(Lcom/android/server/notification/RankingHelper$Record;)V
    //   120: astore 7
    //   122: aload_0
    //   123: getfield 128	com/android/server/notification/RankingHelper:mRestoredWithoutUids	Landroid/util/ArrayMap;
    //   126: aload 10
    //   128: aload 7
    //   130: invokevirtual 352	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   133: pop
    //   134: aload 7
    //   136: aload_1
    //   137: ldc 13
    //   139: sipush 64536
    //   142: invokestatic 589	com/android/server/notification/RankingHelper:safeInt	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   145: putfield 296	com/android/server/notification/RankingHelper$Record:importance	I
    //   148: aload 7
    //   150: aload_1
    //   151: ldc 22
    //   153: iconst_0
    //   154: invokestatic 589	com/android/server/notification/RankingHelper:safeInt	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   157: putfield 306	com/android/server/notification/RankingHelper$Record:priority	I
    //   160: aload 7
    //   162: aload_1
    //   163: ldc 37
    //   165: sipush 64536
    //   168: invokestatic 589	com/android/server/notification/RankingHelper:safeInt	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   171: putfield 315	com/android/server/notification/RankingHelper$Record:visibility	I
    //   174: aload 7
    //   176: aload_1
    //   177: ldc 19
    //   179: iconst_0
    //   180: invokestatic 589	com/android/server/notification/RankingHelper:safeInt	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   183: putfield 322	com/android/server/notification/RankingHelper$Record:opLevel	I
    //   186: iload 6
    //   188: iconst_2
    //   189: if_icmpgt +57 -> 246
    //   192: aload_3
    //   193: aload 10
    //   195: invokeinterface 594 2 0
    //   200: ifeq +46 -> 246
    //   203: aload 7
    //   205: getfield 296	com/android/server/notification/RankingHelper$Record:importance	I
    //   208: ifne +38 -> 246
    //   211: aload 7
    //   213: sipush 64536
    //   216: putfield 296	com/android/server/notification/RankingHelper$Record:importance	I
    //   219: ldc 52
    //   221: new 164	java/lang/StringBuilder
    //   224: dup
    //   225: invokespecial 165	java/lang/StringBuilder:<init>	()V
    //   228: ldc_w 596
    //   231: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   234: aload 10
    //   236: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   239: invokevirtual 177	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   242: invokestatic 597	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   245: pop
    //   246: aload_1
    //   247: invokeinterface 599 1 0
    //   252: istore 4
    //   254: iload 4
    //   256: iconst_1
    //   257: if_icmpeq +108 -> 365
    //   260: aload_1
    //   261: invokeinterface 583 1 0
    //   266: astore 7
    //   268: iload 4
    //   270: iconst_3
    //   271: if_icmpne +14 -> 285
    //   274: ldc 58
    //   276: aload 7
    //   278: invokevirtual 584	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   281: ifeq +4 -> 285
    //   284: return
    //   285: iload 4
    //   287: iconst_2
    //   288: if_icmpne -42 -> 246
    //   291: ldc 55
    //   293: aload 7
    //   295: invokevirtual 584	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   298: ifeq -52 -> 246
    //   301: aload_1
    //   302: ldc 31
    //   304: getstatic 290	com/android/server/notification/RankingHelper$Record:UNKNOWN_UID	I
    //   307: invokestatic 589	com/android/server/notification/RankingHelper:safeInt	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   310: istore 5
    //   312: aload_1
    //   313: aconst_null
    //   314: ldc 16
    //   316: invokeinterface 365 3 0
    //   321: astore 10
    //   323: aload 10
    //   325: invokestatic 402	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   328: ifne -82 -> 246
    //   331: iload 5
    //   333: istore 4
    //   335: iload_2
    //   336: ifeq -255 -> 81
    //   339: aload 9
    //   341: aload 10
    //   343: iconst_0
    //   344: invokevirtual 566	android/content/pm/PackageManager:getPackageUidAsUser	(Ljava/lang/String;I)I
    //   347: istore 4
    //   349: goto -268 -> 81
    //   352: aload_0
    //   353: aload 10
    //   355: iload 4
    //   357: invokespecial 539	com/android/server/notification/RankingHelper:getOrCreateRecord	(Ljava/lang/String;I)Lcom/android/server/notification/RankingHelper$Record;
    //   360: astore 7
    //   362: goto -228 -> 134
    //   365: new 601	java/lang/IllegalStateException
    //   368: dup
    //   369: ldc_w 603
    //   372: invokespecial 605	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   375: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	376	0	this	RankingHelper
    //   0	376	1	paramXmlPullParser	XmlPullParser
    //   0	376	2	paramBoolean	boolean
    //   0	376	3	paramList	List<String>
    //   79	277	4	i	int
    //   77	255	5	j	int
    //   56	134	6	k	int
    //   75	1	7	localNameNotFoundException	PackageManager.NameNotFoundException
    //   105	256	7	localObject1	Object
    //   101	7	8	localRecord	Record
    //   7	333	9	localPackageManager	PackageManager
    //   93	261	10	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   339	349	75	android/content/pm/PackageManager$NameNotFoundException
  }
  
  public void setDefaultOPLevel(int paramInt, String paramString)
  {
    Object localObject;
    if (IS_CHINA_SKU) {
      localObject = this.mContext.getPackageManager();
    }
    label190:
    for (;;)
    {
      try
      {
        int i = ((PackageManager)localObject).getPackageUidAsUser(paramString, paramInt);
        localObject = ((PackageManager)localObject).getApplicationInfo(paramString, 0);
        String str = ((ApplicationInfo)localObject).sourceDir;
        if ((str == null) || (!str.startsWith("/system/reserve/")))
        {
          if ((((ApplicationInfo)localObject).flags & 0x1) != 0)
          {
            paramInt = 1;
            if (OnlineConfigUtil.isInWhiteList(paramString)) {
              break label190;
            }
            if (paramInt == 0) {
              continue;
            }
            break label190;
            if (DEFAULT_DENOISE_LIST.contains(paramString)) {
              paramInt = 1;
            }
            getOrCreateRecord(paramString, i).opLevel = paramInt;
            syncOPLevelToImportance(getOrCreateRecord(paramString, i), paramInt, false);
            if (Build.DEBUG_ONEPLUS) {
              Log.d("RankingHelper", "set default OP level to " + NotificationListenerService.Ranking.opLevelToString(paramInt) + " for pkg " + paramString + " uid " + i);
            }
          }
        }
        else
        {
          paramInt = 1;
          continue;
        }
        paramInt = 0;
        continue;
        paramInt = 1;
        continue;
        paramInt = 0;
      }
      catch (PackageManager.NameNotFoundException paramString)
      {
        return;
      }
    }
  }
  
  public void setEnabled(String paramString, int paramInt, boolean paramBoolean)
  {
    int i = 0;
    if (getImportance(paramString, paramInt) != 0) {}
    for (boolean bool = true; bool == paramBoolean; bool = false) {
      return;
    }
    if (paramBoolean) {
      i = 64536;
    }
    setImportance(paramString, paramInt, i);
  }
  
  public void setImportance(String paramString, int paramInt1, int paramInt2)
  {
    getOrCreateRecord(paramString, paramInt1).importance = paramInt2;
    syncImportanceToOPLevel(getOrCreateRecord(paramString, paramInt1), paramInt2);
    updateConfig();
  }
  
  public void setOPLevel(String paramString, int paramInt1, int paramInt2)
  {
    getOrCreateRecord(paramString, paramInt1).opLevel = paramInt2;
    syncOPLevelToImportance(getOrCreateRecord(paramString, paramInt1), paramInt2, true);
    updateConfig();
  }
  
  public void setPriority(String paramString, int paramInt1, int paramInt2)
  {
    getOrCreateRecord(paramString, paramInt1).priority = paramInt2;
    updateConfig();
  }
  
  public void setVisibilityOverride(String paramString, int paramInt1, int paramInt2)
  {
    getOrCreateRecord(paramString, paramInt1).visibility = paramInt2;
    updateConfig();
  }
  
  public void sort(ArrayList<NotificationRecord> paramArrayList)
  {
    int j = paramArrayList.size();
    int i = j - 1;
    while (i >= 0)
    {
      ((NotificationRecord)paramArrayList.get(i)).setGlobalSortKey(null);
      i -= 1;
    }
    Collections.sort(paramArrayList, this.mPreliminaryComparator);
    ArrayMap localArrayMap = this.mProxyByGroupTmp;
    i = j - 1;
    Object localObject1;
    Object localObject2;
    if (i >= 0) {
      try
      {
        localObject1 = (NotificationRecord)paramArrayList.get(i);
        ((NotificationRecord)localObject1).setAuthoritativeRank(i);
        localObject2 = ((NotificationRecord)localObject1).getGroupKey();
        if ((!((NotificationRecord)localObject1).getNotification().isGroupSummary()) && (this.mProxyByGroupTmp.containsKey(localObject2))) {
          break label359;
        }
        this.mProxyByGroupTmp.put(localObject2, localObject1);
      }
      finally {}
    }
    i = 0;
    label142:
    label197:
    char c1;
    label218:
    int k;
    if (i < j)
    {
      localObject2 = (NotificationRecord)paramArrayList.get(i);
      NotificationRecord localNotificationRecord = (NotificationRecord)this.mProxyByGroupTmp.get(((NotificationRecord)localObject2).getGroupKey());
      localObject1 = ((NotificationRecord)localObject2).getNotification().getSortKey();
      if (localObject1 == null)
      {
        localObject1 = "nsk";
        boolean bool = ((NotificationRecord)localObject2).getNotification().isGroupSummary();
        if (!((NotificationRecord)localObject2).isRecentlyIntrusive()) {
          break label368;
        }
        c1 = '0';
        k = localNotificationRecord.getAuthoritativeRank();
        if (!bool) {
          break label374;
        }
      }
    }
    label359:
    label368:
    label374:
    for (char c2 = '0';; c2 = '1')
    {
      ((NotificationRecord)localObject2).setGlobalSortKey(String.format("intrsv=%c:grnk=0x%04x:gsmry=%c:%s:rnk=0x%04x", new Object[] { Character.valueOf(c1), Integer.valueOf(k), Character.valueOf(c2), localObject1, Integer.valueOf(((NotificationRecord)localObject2).getAuthoritativeRank()) }));
      i += 1;
      break label142;
      if (((String)localObject1).equals(""))
      {
        localObject1 = "esk";
        break label197;
      }
      localObject1 = "gsk=" + (String)localObject1;
      break label197;
      this.mProxyByGroupTmp.clear();
      Collections.sort(paramArrayList, this.mFinalComparator);
      return;
      i -= 1;
      break;
      c1 = '1';
      break label218;
    }
  }
  
  public void writeXml(XmlSerializer paramXmlSerializer, boolean paramBoolean)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "ranking");
    paramXmlSerializer.attribute(null, "version", Integer.toString(4));
    int k = this.mRecords.size();
    int j = 0;
    if (j < k)
    {
      Record localRecord = (Record)this.mRecords.valueAt(j);
      if (localRecord == null) {
        Log.w("RankingHelper", "mRecords.size() = " + this.mRecords.size() + ", N = " + k + ", loop = " + j);
      }
      label159:
      label362:
      for (;;)
      {
        j += 1;
        break;
        if ((!paramBoolean) || (UserHandle.getUserId(localRecord.uid) == 0))
        {
          int i;
          if ((localRecord.importance != 64536) || (localRecord.priority != 0)) {
            i = 1;
          }
          for (;;)
          {
            if (i == 0) {
              break label362;
            }
            paramXmlSerializer.startTag(null, "package");
            paramXmlSerializer.attribute(null, "name", localRecord.pkg);
            if (localRecord.importance != 64536) {
              paramXmlSerializer.attribute(null, "importance", Integer.toString(localRecord.importance));
            }
            if (localRecord.priority != 0) {
              paramXmlSerializer.attribute(null, "priority", Integer.toString(localRecord.priority));
            }
            if (localRecord.visibility != 64536) {
              paramXmlSerializer.attribute(null, "visibility", Integer.toString(localRecord.visibility));
            }
            if (localRecord.opLevel != 0) {
              paramXmlSerializer.attribute(null, "opLevel", Integer.toString(localRecord.opLevel));
            }
            if (!paramBoolean) {
              paramXmlSerializer.attribute(null, "uid", Integer.toString(localRecord.uid));
            }
            paramXmlSerializer.endTag(null, "package");
            break;
            if (localRecord.visibility != 64536) {
              break label159;
            }
            if (localRecord.opLevel != 0) {
              i = 1;
            } else {
              i = 0;
            }
          }
        }
      }
    }
    paramXmlSerializer.endTag(null, "ranking");
  }
  
  private static class Record
  {
    static int UNKNOWN_UID = 55536;
    int importance = 64536;
    int opLevel = 0;
    String pkg;
    int priority = 0;
    int uid = UNKNOWN_UID;
    int visibility = 64536;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/RankingHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */