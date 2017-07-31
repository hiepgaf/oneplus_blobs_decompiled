package android.service.notification;

import android.app.ActivityManager;
import android.app.NotificationManager.Policy;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class ZenModeConfig
  implements Parcelable
{
  private static final String ALLOW_ATT_CALLS = "calls";
  private static final String ALLOW_ATT_CALLS_FROM = "callsFrom";
  private static final String ALLOW_ATT_EVENTS = "events";
  private static final String ALLOW_ATT_FROM = "from";
  private static final String ALLOW_ATT_MESSAGES = "messages";
  private static final String ALLOW_ATT_MESSAGES_FROM = "messagesFrom";
  private static final String ALLOW_ATT_REMINDERS = "reminders";
  private static final String ALLOW_ATT_REPEAT_CALLERS = "repeatCallers";
  private static final String ALLOW_ATT_SCREEN_OFF = "visualScreenOff";
  private static final String ALLOW_ATT_SCREEN_ON = "visualScreenOn";
  private static final String ALLOW_TAG = "allow";
  public static final int[] ALL_DAYS;
  private static final String AUTOMATIC_TAG = "automatic";
  private static final String CONDITION_ATT_COMPONENT = "component";
  private static final String CONDITION_ATT_FLAGS = "flags";
  private static final String CONDITION_ATT_ICON = "icon";
  private static final String CONDITION_ATT_ID = "id";
  private static final String CONDITION_ATT_LINE1 = "line1";
  private static final String CONDITION_ATT_LINE2 = "line2";
  private static final String CONDITION_ATT_STATE = "state";
  private static final String CONDITION_ATT_SUMMARY = "summary";
  private static final String CONDITION_TAG = "condition";
  public static final String COUNTDOWN_PATH = "countdown";
  public static final Parcelable.Creator<ZenModeConfig> CREATOR = new Parcelable.Creator()
  {
    public ZenModeConfig createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ZenModeConfig(paramAnonymousParcel);
    }
    
    public ZenModeConfig[] newArray(int paramAnonymousInt)
    {
      return new ZenModeConfig[paramAnonymousInt];
    }
  };
  private static final int DAY_MINUTES = 1440;
  private static final boolean DEFAULT_ALLOW_CALLS = true;
  private static final boolean DEFAULT_ALLOW_EVENTS = true;
  private static final boolean DEFAULT_ALLOW_MESSAGES = false;
  private static final boolean DEFAULT_ALLOW_REMINDERS = true;
  private static final boolean DEFAULT_ALLOW_REPEAT_CALLERS = true;
  private static final boolean DEFAULT_ALLOW_SCREEN_OFF = true;
  private static final boolean DEFAULT_ALLOW_SCREEN_ON = true;
  private static final int DEFAULT_SOURCE = 2;
  public static final String EVENT_PATH = "event";
  private static final String MANUAL_TAG = "manual";
  public static final int MAX_SOURCE = 2;
  private static final int MINUTES_MS = 60000;
  public static final int[] MINUTE_BUCKETS;
  private static final String RULE_ATT_COMPONENT = "component";
  private static final String RULE_ATT_CONDITION_ID = "conditionId";
  private static final String RULE_ATT_CREATION_TIME = "creationTime";
  private static final String RULE_ATT_ENABLED = "enabled";
  private static final String RULE_ATT_ENABLER = "enabler";
  private static final String RULE_ATT_ID = "ruleId";
  private static final String RULE_ATT_NAME = "name";
  private static final String RULE_ATT_SNOOZING = "snoozing";
  private static final String RULE_ATT_ZEN = "zen";
  public static final String SCHEDULE_PATH = "schedule";
  private static final int SECONDS_MS = 1000;
  public static final int SOURCE_ANYONE = 0;
  public static final int SOURCE_CONTACT = 1;
  public static final int SOURCE_STAR = 2;
  public static final String SYSTEM_AUTHORITY = "android";
  private static String TAG = "ZenModeConfig";
  public static final int[] WEEKEND_DAYS;
  public static final int[] WEEKNIGHT_DAYS;
  private static final int XML_VERSION = 2;
  private static final String ZEN_ATT_USER = "user";
  private static final String ZEN_ATT_VERSION = "version";
  private static final String ZEN_TAG = "zen";
  private static final int ZERO_VALUE_MS = 10000;
  public boolean allowCalls = true;
  public int allowCallsFrom = 2;
  public boolean allowEvents = true;
  public boolean allowMessages = false;
  public int allowMessagesFrom = 2;
  public boolean allowReminders = true;
  public boolean allowRepeatCallers = true;
  public boolean allowWhenScreenOff = true;
  public boolean allowWhenScreenOn = true;
  public ArrayMap<String, ZenRule> automaticRules = new ArrayMap();
  public ZenRule manualRule;
  public int user = 0;
  
  static
  {
    ALL_DAYS = new int[] { 1, 2, 3, 4, 5, 6, 7 };
    WEEKNIGHT_DAYS = new int[] { 1, 2, 3, 4, 5 };
    WEEKEND_DAYS = new int[] { 6, 7 };
    MINUTE_BUCKETS = generateMinuteBuckets();
  }
  
  public ZenModeConfig() {}
  
  public ZenModeConfig(Parcel paramParcel)
  {
    if (paramParcel.readInt() == 1)
    {
      bool1 = true;
      this.allowCalls = bool1;
      if (paramParcel.readInt() != 1) {
        break label261;
      }
      bool1 = true;
      label96:
      this.allowRepeatCallers = bool1;
      if (paramParcel.readInt() != 1) {
        break label267;
      }
      bool1 = true;
      label113:
      this.allowMessages = bool1;
      if (paramParcel.readInt() != 1) {
        break label273;
      }
      bool1 = true;
      label130:
      this.allowReminders = bool1;
      if (paramParcel.readInt() != 1) {
        break label279;
      }
    }
    label261:
    label267:
    label273:
    label279:
    for (boolean bool1 = true;; bool1 = false)
    {
      this.allowEvents = bool1;
      this.allowCallsFrom = paramParcel.readInt();
      this.allowMessagesFrom = paramParcel.readInt();
      this.user = paramParcel.readInt();
      this.manualRule = ((ZenRule)paramParcel.readParcelable(null));
      int j = paramParcel.readInt();
      if (j <= 0) {
        break label285;
      }
      String[] arrayOfString = new String[j];
      ZenRule[] arrayOfZenRule = new ZenRule[j];
      paramParcel.readStringArray(arrayOfString);
      paramParcel.readTypedArray(arrayOfZenRule, ZenRule.CREATOR);
      int i = 0;
      while (i < j)
      {
        this.automaticRules.put(arrayOfString[i], arrayOfZenRule[i]);
        i += 1;
      }
      bool1 = false;
      break;
      bool1 = false;
      break label96;
      bool1 = false;
      break label113;
      bool1 = false;
      break label130;
    }
    label285:
    if (paramParcel.readInt() == 1)
    {
      bool1 = true;
      this.allowWhenScreenOff = bool1;
      if (paramParcel.readInt() != 1) {
        break label327;
      }
    }
    label327:
    for (bool1 = bool2;; bool1 = false)
    {
      this.allowWhenScreenOn = bool1;
      return;
      bool1 = false;
      break;
    }
  }
  
  private static <T> void addKeys(ArraySet<T> paramArraySet, ArrayMap<T, ?> paramArrayMap)
  {
    if (paramArrayMap != null)
    {
      int i = 0;
      while (i < paramArrayMap.size())
      {
        paramArraySet.add(paramArrayMap.keyAt(i));
        i += 1;
      }
    }
  }
  
  private Diff diff(ZenModeConfig paramZenModeConfig)
  {
    Diff localDiff = new Diff();
    if (paramZenModeConfig == null) {
      return Diff.-wrap0(localDiff, "config", "delete");
    }
    if (this.user != paramZenModeConfig.user) {
      localDiff.addLine("user", Integer.valueOf(this.user), Integer.valueOf(paramZenModeConfig.user));
    }
    if (this.allowCalls != paramZenModeConfig.allowCalls) {
      localDiff.addLine("allowCalls", Boolean.valueOf(this.allowCalls), Boolean.valueOf(paramZenModeConfig.allowCalls));
    }
    if (this.allowRepeatCallers != paramZenModeConfig.allowRepeatCallers) {
      localDiff.addLine("allowRepeatCallers", Boolean.valueOf(this.allowRepeatCallers), Boolean.valueOf(paramZenModeConfig.allowRepeatCallers));
    }
    if (this.allowMessages != paramZenModeConfig.allowMessages) {
      localDiff.addLine("allowMessages", Boolean.valueOf(this.allowMessages), Boolean.valueOf(paramZenModeConfig.allowMessages));
    }
    if (this.allowCallsFrom != paramZenModeConfig.allowCallsFrom) {
      localDiff.addLine("allowCallsFrom", Integer.valueOf(this.allowCallsFrom), Integer.valueOf(paramZenModeConfig.allowCallsFrom));
    }
    if (this.allowMessagesFrom != paramZenModeConfig.allowMessagesFrom) {
      localDiff.addLine("allowMessagesFrom", Integer.valueOf(this.allowMessagesFrom), Integer.valueOf(paramZenModeConfig.allowMessagesFrom));
    }
    if (this.allowReminders != paramZenModeConfig.allowReminders) {
      localDiff.addLine("allowReminders", Boolean.valueOf(this.allowReminders), Boolean.valueOf(paramZenModeConfig.allowReminders));
    }
    if (this.allowEvents != paramZenModeConfig.allowEvents) {
      localDiff.addLine("allowEvents", Boolean.valueOf(this.allowEvents), Boolean.valueOf(paramZenModeConfig.allowEvents));
    }
    if (this.allowWhenScreenOff != paramZenModeConfig.allowWhenScreenOff) {
      localDiff.addLine("allowWhenScreenOff", Boolean.valueOf(this.allowWhenScreenOff), Boolean.valueOf(paramZenModeConfig.allowWhenScreenOff));
    }
    if (this.allowWhenScreenOn != paramZenModeConfig.allowWhenScreenOn) {
      localDiff.addLine("allowWhenScreenOn", Boolean.valueOf(this.allowWhenScreenOn), Boolean.valueOf(paramZenModeConfig.allowWhenScreenOn));
    }
    ArraySet localArraySet = new ArraySet();
    addKeys(localArraySet, this.automaticRules);
    addKeys(localArraySet, paramZenModeConfig.automaticRules);
    int j = localArraySet.size();
    int i = 0;
    if (i < j)
    {
      String str = (String)localArraySet.valueAt(i);
      ZenRule localZenRule1;
      if (this.automaticRules != null)
      {
        localZenRule1 = (ZenRule)this.automaticRules.get(str);
        label436:
        if (paramZenModeConfig.automaticRules == null) {
          break label506;
        }
      }
      label506:
      for (ZenRule localZenRule2 = (ZenRule)paramZenModeConfig.automaticRules.get(str);; localZenRule2 = null)
      {
        ZenRule.-wrap0(localDiff, "automaticRule[" + str + "]", localZenRule1, localZenRule2);
        i += 1;
        break;
        localZenRule1 = null;
        break label436;
      }
    }
    ZenRule.-wrap0(localDiff, "manualRule", this.manualRule, paramZenModeConfig.manualRule);
    return localDiff;
  }
  
  public static Diff diff(ZenModeConfig paramZenModeConfig1, ZenModeConfig paramZenModeConfig2)
  {
    if (paramZenModeConfig1 == null)
    {
      paramZenModeConfig1 = new Diff();
      if (paramZenModeConfig2 != null) {
        Diff.-wrap0(paramZenModeConfig1, "config", "insert");
      }
      return paramZenModeConfig1;
    }
    return paramZenModeConfig1.diff(paramZenModeConfig2);
  }
  
  private static int[] generateMinuteBuckets()
  {
    int[] arrayOfInt = new int[15];
    arrayOfInt[0] = 15;
    arrayOfInt[1] = 30;
    arrayOfInt[2] = 45;
    int i = 1;
    while (i <= 12)
    {
      arrayOfInt[(i + 2)] = (i * 60);
      i += 1;
    }
    return arrayOfInt;
  }
  
  private static String getConditionLine(Context paramContext, ZenModeConfig paramZenModeConfig, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramZenModeConfig == null) {
      return "";
    }
    Object localObject = "";
    if (paramZenModeConfig.manualRule != null)
    {
      localObject = paramZenModeConfig.manualRule.conditionId;
      if (paramZenModeConfig.manualRule.enabler != null) {
        localObject = getOwnerCaption(paramContext, paramZenModeConfig.manualRule.enabler);
      }
    }
    else
    {
      paramZenModeConfig = paramZenModeConfig.automaticRules.values().iterator();
    }
    for (;;)
    {
      if (!paramZenModeConfig.hasNext()) {
        break label261;
      }
      ZenRule localZenRule = (ZenRule)paramZenModeConfig.next();
      if (localZenRule.isAutomaticActive())
      {
        if (((String)localObject).isEmpty())
        {
          localObject = localZenRule.name;
          continue;
          if (localObject == null)
          {
            localObject = paramContext.getString(17040851);
            break;
          }
          long l = tryParseCountdownConditionId((Uri)localObject);
          localObject = paramZenModeConfig.manualRule.condition;
          if (l > 0L) {
            localObject = toTimeCondition(paramContext, l, Math.round((float)(l - System.currentTimeMillis()) / 60000.0F), paramInt, paramBoolean2);
          }
          if (localObject == null) {
            localObject = "";
          }
          for (;;)
          {
            if (!TextUtils.isEmpty((CharSequence)localObject)) {
              break label226;
            }
            localObject = "";
            break;
            if (paramBoolean1) {
              localObject = ((Condition)localObject).line1;
            } else {
              localObject = ((Condition)localObject).summary;
            }
          }
          label226:
          break;
        }
        localObject = paramContext.getResources().getString(17040853, new Object[] { localObject, localZenRule.name });
      }
    }
    label261:
    return (String)localObject;
  }
  
  public static String getConditionSummary(Context paramContext, ZenModeConfig paramZenModeConfig, int paramInt, boolean paramBoolean)
  {
    return getConditionLine(paramContext, paramZenModeConfig, paramInt, false, paramBoolean);
  }
  
  public static ComponentName getEventConditionProvider()
  {
    return new ComponentName("android", "EventConditionProvider");
  }
  
  private static CharSequence getFormattedTime(Context paramContext, long paramLong, int paramInt)
  {
    Object localObject2 = new StringBuilder().append("EEE ");
    if (DateFormat.is24HourFormat(paramContext, paramInt))
    {
      localObject1 = "Hm";
      localObject2 = (String)localObject1;
      GregorianCalendar localGregorianCalendar1 = new GregorianCalendar();
      GregorianCalendar localGregorianCalendar2 = new GregorianCalendar();
      localGregorianCalendar2.setTimeInMillis(paramLong);
      localObject1 = localObject2;
      if (localGregorianCalendar1.get(1) == localGregorianCalendar2.get(1))
      {
        localObject1 = localObject2;
        if (localGregorianCalendar1.get(2) == localGregorianCalendar2.get(2))
        {
          localObject1 = localObject2;
          if (localGregorianCalendar1.get(5) == localGregorianCalendar2.get(5)) {
            if (!DateFormat.is24HourFormat(paramContext, paramInt)) {
              break label155;
            }
          }
        }
      }
    }
    label155:
    for (Object localObject1 = "Hm";; localObject1 = "hma")
    {
      return DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), (String)localObject1), paramLong);
      localObject1 = "hma";
      break;
    }
  }
  
  private static String getOwnerCaption(Context paramContext, String paramString)
  {
    paramContext = paramContext.getPackageManager();
    try
    {
      paramString = paramContext.getApplicationInfo(paramString, 0);
      if (paramString != null)
      {
        paramContext = paramString.loadLabel(paramContext);
        if (paramContext != null)
        {
          paramContext = paramContext.toString().trim();
          int i = paramContext.length();
          if (i > 0) {
            return paramContext;
          }
        }
      }
    }
    catch (Throwable paramContext)
    {
      Slog.w(TAG, "Error loading owner caption", paramContext);
    }
    return "";
  }
  
  public static ComponentName getScheduleConditionProvider()
  {
    return new ComponentName("android", "ScheduleConditionProvider");
  }
  
  private static boolean isValidAutomaticRule(ZenRule paramZenRule)
  {
    if ((paramZenRule == null) || (TextUtils.isEmpty(paramZenRule.name))) {}
    while ((!Settings.Global.isValidZenMode(paramZenRule.zenMode)) || (paramZenRule.conditionId == null)) {
      return false;
    }
    return sameCondition(paramZenRule);
  }
  
  public static boolean isValidCountdownConditionId(Uri paramUri)
  {
    return tryParseCountdownConditionId(paramUri) != 0L;
  }
  
  public static boolean isValidEventConditionId(Uri paramUri)
  {
    return tryParseEventConditionId(paramUri) != null;
  }
  
  public static boolean isValidHour(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt < 24) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static boolean isValidManualRule(ZenRule paramZenRule)
  {
    if (paramZenRule != null)
    {
      if (Settings.Global.isValidZenMode(paramZenRule.zenMode)) {
        return sameCondition(paramZenRule);
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  public static boolean isValidMinute(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt < 60) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean isValidScheduleConditionId(Uri paramUri)
  {
    return tryParseScheduleConditionId(paramUri) != null;
  }
  
  private static boolean isValidSource(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt <= 2) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static String newRuleId()
  {
    return UUID.randomUUID().toString().replace("-", "");
  }
  
  private static int prioritySendersToSource(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      return paramInt2;
    case 1: 
      return 1;
    case 2: 
      return 2;
    }
    return 0;
  }
  
  public static Condition readConditionXml(XmlPullParser paramXmlPullParser)
  {
    Uri localUri = safeUri(paramXmlPullParser, "id");
    if (localUri == null) {
      return null;
    }
    String str1 = paramXmlPullParser.getAttributeValue(null, "summary");
    String str2 = paramXmlPullParser.getAttributeValue(null, "line1");
    String str3 = paramXmlPullParser.getAttributeValue(null, "line2");
    int i = safeInt(paramXmlPullParser, "icon", -1);
    int j = safeInt(paramXmlPullParser, "state", -1);
    int k = safeInt(paramXmlPullParser, "flags", -1);
    try
    {
      paramXmlPullParser = new Condition(localUri, str1, str2, str3, i, j, k);
      return paramXmlPullParser;
    }
    catch (IllegalArgumentException paramXmlPullParser)
    {
      Slog.w(TAG, "Unable to read condition xml", paramXmlPullParser);
    }
    return null;
  }
  
  public static ZenRule readRuleXml(XmlPullParser paramXmlPullParser)
  {
    ZenRule localZenRule = new ZenRule();
    localZenRule.enabled = safeBoolean(paramXmlPullParser, "enabled", true);
    localZenRule.snoozing = safeBoolean(paramXmlPullParser, "snoozing", false);
    localZenRule.name = paramXmlPullParser.getAttributeValue(null, "name");
    String str = paramXmlPullParser.getAttributeValue(null, "zen");
    localZenRule.zenMode = tryParseZenMode(str, -1);
    if (localZenRule.zenMode == -1)
    {
      Slog.w(TAG, "Bad zen mode in rule xml:" + str);
      return null;
    }
    localZenRule.conditionId = safeUri(paramXmlPullParser, "conditionId");
    localZenRule.component = safeComponentName(paramXmlPullParser, "component");
    localZenRule.creationTime = safeLong(paramXmlPullParser, "creationTime", 0L);
    localZenRule.enabler = paramXmlPullParser.getAttributeValue(null, "enabler");
    localZenRule.condition = readConditionXml(paramXmlPullParser);
    return localZenRule;
  }
  
  public static ZenModeConfig readXml(XmlPullParser paramXmlPullParser, Migration paramMigration)
    throws XmlPullParserException, IOException
  {
    if (paramXmlPullParser.getEventType() != 2) {
      return null;
    }
    if (!"zen".equals(paramXmlPullParser.getName())) {
      return null;
    }
    ZenModeConfig localZenModeConfig = new ZenModeConfig();
    if (safeInt(paramXmlPullParser, "version", 2) == 1) {
      return paramMigration.migrate(XmlV1.readXml(paramXmlPullParser));
    }
    localZenModeConfig.user = safeInt(paramXmlPullParser, "user", localZenModeConfig.user);
    for (;;)
    {
      int i = paramXmlPullParser.next();
      if (i == 1) {
        break;
      }
      paramMigration = paramXmlPullParser.getName();
      if ((i == 3) && ("zen".equals(paramMigration))) {
        return localZenModeConfig;
      }
      if (i == 2)
      {
        if ("allow".equals(paramMigration))
        {
          localZenModeConfig.allowCalls = safeBoolean(paramXmlPullParser, "calls", false);
          localZenModeConfig.allowRepeatCallers = safeBoolean(paramXmlPullParser, "repeatCallers", true);
          localZenModeConfig.allowMessages = safeBoolean(paramXmlPullParser, "messages", false);
          localZenModeConfig.allowReminders = safeBoolean(paramXmlPullParser, "reminders", true);
          localZenModeConfig.allowEvents = safeBoolean(paramXmlPullParser, "events", true);
          i = safeInt(paramXmlPullParser, "from", -1);
          int j = safeInt(paramXmlPullParser, "callsFrom", -1);
          int k = safeInt(paramXmlPullParser, "messagesFrom", -1);
          if ((isValidSource(j)) && (isValidSource(k)))
          {
            localZenModeConfig.allowCallsFrom = j;
            localZenModeConfig.allowMessagesFrom = k;
          }
          for (;;)
          {
            localZenModeConfig.allowWhenScreenOff = safeBoolean(paramXmlPullParser, "visualScreenOff", true);
            localZenModeConfig.allowWhenScreenOn = safeBoolean(paramXmlPullParser, "visualScreenOn", true);
            break;
            if (isValidSource(i))
            {
              Slog.i(TAG, "Migrating existing shared 'from': " + sourceToString(i));
              localZenModeConfig.allowCallsFrom = i;
              localZenModeConfig.allowMessagesFrom = i;
            }
            else
            {
              localZenModeConfig.allowCallsFrom = 2;
              localZenModeConfig.allowMessagesFrom = 2;
            }
          }
        }
        if ("manual".equals(paramMigration))
        {
          localZenModeConfig.manualRule = readRuleXml(paramXmlPullParser);
        }
        else if ("automatic".equals(paramMigration))
        {
          paramMigration = paramXmlPullParser.getAttributeValue(null, "ruleId");
          ZenRule localZenRule = readRuleXml(paramXmlPullParser);
          if ((paramMigration != null) && (localZenRule != null))
          {
            localZenRule.id = paramMigration;
            localZenModeConfig.automaticRules.put(paramMigration, localZenRule);
          }
        }
      }
    }
    throw new IllegalStateException("Failed to reach END_DOCUMENT");
  }
  
  private static boolean safeBoolean(String paramString, boolean paramBoolean)
  {
    if (TextUtils.isEmpty(paramString)) {
      return paramBoolean;
    }
    return Boolean.valueOf(paramString).booleanValue();
  }
  
  private static boolean safeBoolean(XmlPullParser paramXmlPullParser, String paramString, boolean paramBoolean)
  {
    return safeBoolean(paramXmlPullParser.getAttributeValue(null, paramString), paramBoolean);
  }
  
  private static ComponentName safeComponentName(XmlPullParser paramXmlPullParser, String paramString)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (TextUtils.isEmpty(paramXmlPullParser)) {
      return null;
    }
    return ComponentName.unflattenFromString(paramXmlPullParser);
  }
  
  private static int safeInt(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    return tryParseInt(paramXmlPullParser.getAttributeValue(null, paramString), paramInt);
  }
  
  private static long safeLong(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    return tryParseLong(paramXmlPullParser.getAttributeValue(null, paramString), paramLong);
  }
  
  private static Uri safeUri(XmlPullParser paramXmlPullParser, String paramString)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (TextUtils.isEmpty(paramXmlPullParser)) {
      return null;
    }
    return Uri.parse(paramXmlPullParser);
  }
  
  private static boolean sameCondition(ZenRule paramZenRule)
  {
    boolean bool = true;
    if (paramZenRule == null) {
      return false;
    }
    if (paramZenRule.conditionId == null) {
      return paramZenRule.condition == null;
    }
    if (paramZenRule.condition != null) {
      bool = paramZenRule.conditionId.equals(paramZenRule.condition.id);
    }
    return bool;
  }
  
  private static int sourceToPrioritySenders(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      return paramInt2;
    case 0: 
      return 0;
    case 1: 
      return 1;
    }
    return 2;
  }
  
  public static String sourceToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case 0: 
      return "anyone";
    case 1: 
      return "contacts";
    }
    return "stars";
  }
  
  public static Uri toCountdownConditionId(long paramLong)
  {
    return new Uri.Builder().scheme("condition").authority("android").appendPath("countdown").appendPath(Long.toString(paramLong)).build();
  }
  
  private static String toDayList(int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length == 0)) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      if (i > 0) {
        localStringBuilder.append('.');
      }
      localStringBuilder.append(paramArrayOfInt[i]);
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  public static Uri toEventConditionId(EventInfo paramEventInfo)
  {
    Uri.Builder localBuilder = new Uri.Builder().scheme("condition").authority("android").appendPath("event").appendQueryParameter("userId", Long.toString(paramEventInfo.userId));
    if (paramEventInfo.calendar != null) {}
    for (String str = paramEventInfo.calendar;; str = "") {
      return localBuilder.appendQueryParameter("calendar", str).appendQueryParameter("reply", Integer.toString(paramEventInfo.reply)).build();
    }
  }
  
  public static Condition toNextAlarmCondition(Context paramContext, long paramLong1, long paramLong2, int paramInt)
  {
    CharSequence localCharSequence = getFormattedTime(paramContext, paramLong2, paramInt);
    paramContext = paramContext.getResources().getString(17040850, new Object[] { localCharSequence });
    return new Condition(toCountdownConditionId(paramLong2), "", paramContext, "", 0, 1, 1);
  }
  
  public static Uri toScheduleConditionId(ScheduleInfo paramScheduleInfo)
  {
    return new Uri.Builder().scheme("condition").authority("android").appendPath("schedule").appendQueryParameter("days", toDayList(paramScheduleInfo.days)).appendQueryParameter("start", paramScheduleInfo.startHour + "." + paramScheduleInfo.startMinute).appendQueryParameter("end", paramScheduleInfo.endHour + "." + paramScheduleInfo.endMinute).appendQueryParameter("exitAtAlarm", String.valueOf(paramScheduleInfo.exitAtAlarm)).build();
  }
  
  public static Condition toTimeCondition(Context paramContext, int paramInt1, int paramInt2)
  {
    return toTimeCondition(paramContext, paramInt1, paramInt2, false);
  }
  
  public static Condition toTimeCondition(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    long l = System.currentTimeMillis();
    if (paramInt1 == 0) {}
    for (int i = 10000;; i = 60000 * paramInt1) {
      return toTimeCondition(paramContext, l + i, paramInt1, paramInt2, paramBoolean);
    }
  }
  
  public static Condition toTimeCondition(Context paramContext, long paramLong, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Object localObject2 = getFormattedTime(paramContext, paramLong, paramInt2);
    Resources localResources = paramContext.getResources();
    label66:
    Object localObject1;
    if (paramInt1 < 60) {
      if (paramBoolean)
      {
        paramInt2 = 18087964;
        paramContext = localResources.getQuantityString(paramInt2, paramInt1, new Object[] { Integer.valueOf(paramInt1), localObject2 });
        if (!paramBoolean) {
          break label139;
        }
        paramInt2 = 18087968;
        localObject1 = localResources.getQuantityString(paramInt2, paramInt1, new Object[] { Integer.valueOf(paramInt1), localObject2 });
        localObject2 = localResources.getString(17040849, new Object[] { localObject2 });
      }
    }
    for (;;)
    {
      return new Condition(toCountdownConditionId(paramLong), paramContext, (String)localObject1, (String)localObject2, 0, 1, 1);
      paramInt2 = 18087963;
      break;
      label139:
      paramInt2 = 18087967;
      break label66;
      if (paramInt1 < 1440)
      {
        paramInt2 = Math.round(paramInt1 / 60.0F);
        if (paramBoolean)
        {
          paramInt1 = 18087966;
          label174:
          paramContext = localResources.getQuantityString(paramInt1, paramInt2, new Object[] { Integer.valueOf(paramInt2), localObject2 });
          if (!paramBoolean) {
            break label265;
          }
        }
        label265:
        for (paramInt1 = 18087970;; paramInt1 = 18087969)
        {
          localObject1 = localResources.getQuantityString(paramInt1, paramInt2, new Object[] { Integer.valueOf(paramInt2), localObject2 });
          localObject2 = localResources.getString(17040849, new Object[] { localObject2 });
          break;
          paramInt1 = 18087965;
          break label174;
        }
      }
      localObject2 = localResources.getString(17040849, new Object[] { localObject2 });
      localObject1 = localObject2;
      paramContext = (Context)localObject2;
    }
  }
  
  public static long tryParseCountdownConditionId(Uri paramUri)
  {
    if (!Condition.isValidId(paramUri, "android")) {
      return 0L;
    }
    if ((paramUri.getPathSegments().size() == 2) && ("countdown".equals(paramUri.getPathSegments().get(0)))) {}
    try
    {
      long l = Long.parseLong((String)paramUri.getPathSegments().get(1));
      return l;
    }
    catch (RuntimeException localRuntimeException)
    {
      Slog.w(TAG, "Error parsing countdown condition: " + paramUri, localRuntimeException);
    }
    return 0L;
    return 0L;
  }
  
  private static int[] tryParseDayList(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return null;
    }
    paramString1 = paramString1.split(paramString2);
    if (paramString1.length == 0) {
      return null;
    }
    paramString2 = new int[paramString1.length];
    int i = 0;
    while (i < paramString1.length)
    {
      int j = tryParseInt(paramString1[i], -1);
      if (j == -1) {
        return null;
      }
      paramString2[i] = j;
      i += 1;
    }
    return paramString2;
  }
  
  public static EventInfo tryParseEventConditionId(Uri paramUri)
  {
    if ((paramUri != null) && (paramUri.getScheme().equals("condition")) && (paramUri.getAuthority().equals("android")) && (paramUri.getPathSegments().size() == 1)) {}
    for (boolean bool = ((String)paramUri.getPathSegments().get(0)).equals("event"); !bool; bool = false) {
      return null;
    }
    EventInfo localEventInfo = new EventInfo();
    localEventInfo.userId = tryParseInt(paramUri.getQueryParameter("userId"), 55536);
    localEventInfo.calendar = paramUri.getQueryParameter("calendar");
    if ((TextUtils.isEmpty(localEventInfo.calendar)) || (tryParseLong(localEventInfo.calendar, -1L) != -1L)) {
      localEventInfo.calendar = null;
    }
    localEventInfo.reply = tryParseInt(paramUri.getQueryParameter("reply"), 0);
    return localEventInfo;
  }
  
  private static int[] tryParseHourAndMinute(String paramString)
  {
    Object localObject = null;
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    int j = paramString.indexOf('.');
    if ((j < 1) || (j >= paramString.length() - 1)) {
      return null;
    }
    int i = tryParseInt(paramString.substring(0, j), -1);
    j = tryParseInt(paramString.substring(j + 1), -1);
    paramString = (String)localObject;
    if (isValidHour(i))
    {
      paramString = (String)localObject;
      if (isValidMinute(j))
      {
        paramString = new int[2];
        paramString[0] = i;
        paramString[1] = j;
      }
    }
    return paramString;
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
  
  private static long tryParseLong(String paramString, long paramLong)
  {
    if (TextUtils.isEmpty(paramString)) {
      return paramLong;
    }
    try
    {
      long l = Long.valueOf(paramString).longValue();
      return l;
    }
    catch (NumberFormatException paramString) {}
    return paramLong;
  }
  
  public static ScheduleInfo tryParseScheduleConditionId(Uri paramUri)
  {
    if ((paramUri != null) && (paramUri.getScheme().equals("condition")) && (paramUri.getAuthority().equals("android")) && (paramUri.getPathSegments().size() == 1)) {}
    for (boolean bool = ((String)paramUri.getPathSegments().get(0)).equals("schedule"); !bool; bool = false) {
      return null;
    }
    int[] arrayOfInt1 = tryParseHourAndMinute(paramUri.getQueryParameter("start"));
    int[] arrayOfInt2 = tryParseHourAndMinute(paramUri.getQueryParameter("end"));
    if ((arrayOfInt1 == null) || (arrayOfInt2 == null)) {
      return null;
    }
    ScheduleInfo localScheduleInfo = new ScheduleInfo();
    localScheduleInfo.days = tryParseDayList(paramUri.getQueryParameter("days"), "\\.");
    localScheduleInfo.startHour = arrayOfInt1[0];
    localScheduleInfo.startMinute = arrayOfInt1[1];
    localScheduleInfo.endHour = arrayOfInt2[0];
    localScheduleInfo.endMinute = arrayOfInt2[1];
    localScheduleInfo.exitAtAlarm = safeBoolean(paramUri.getQueryParameter("exitAtAlarm"), false);
    return localScheduleInfo;
  }
  
  private static int tryParseZenMode(String paramString, int paramInt)
  {
    int i = tryParseInt(paramString, paramInt);
    if (Settings.Global.isValidZenMode(i)) {
      return i;
    }
    return paramInt;
  }
  
  public static void writeConditionXml(Condition paramCondition, XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.attribute(null, "id", paramCondition.id.toString());
    paramXmlSerializer.attribute(null, "summary", paramCondition.summary);
    paramXmlSerializer.attribute(null, "line1", paramCondition.line1);
    paramXmlSerializer.attribute(null, "line2", paramCondition.line2);
    paramXmlSerializer.attribute(null, "icon", Integer.toString(paramCondition.icon));
    paramXmlSerializer.attribute(null, "state", Integer.toString(paramCondition.state));
    paramXmlSerializer.attribute(null, "flags", Integer.toString(paramCondition.flags));
  }
  
  public static void writeRuleXml(ZenRule paramZenRule, XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.attribute(null, "enabled", Boolean.toString(paramZenRule.enabled));
    paramXmlSerializer.attribute(null, "snoozing", Boolean.toString(paramZenRule.snoozing));
    if (paramZenRule.name != null) {
      paramXmlSerializer.attribute(null, "name", paramZenRule.name);
    }
    paramXmlSerializer.attribute(null, "zen", Integer.toString(paramZenRule.zenMode));
    if (paramZenRule.component != null) {
      paramXmlSerializer.attribute(null, "component", paramZenRule.component.flattenToString());
    }
    if (paramZenRule.conditionId != null) {
      paramXmlSerializer.attribute(null, "conditionId", paramZenRule.conditionId.toString());
    }
    paramXmlSerializer.attribute(null, "creationTime", Long.toString(paramZenRule.creationTime));
    if (paramZenRule.enabler != null) {
      paramXmlSerializer.attribute(null, "enabler", paramZenRule.enabler);
    }
    if (paramZenRule.condition != null) {
      writeConditionXml(paramZenRule.condition, paramXmlSerializer);
    }
  }
  
  public void applyNotificationPolicy(NotificationManager.Policy paramPolicy)
  {
    boolean bool2 = true;
    if (paramPolicy == null) {
      return;
    }
    if ((paramPolicy.priorityCategories & 0x8) != 0)
    {
      bool1 = true;
      this.allowCalls = bool1;
      if ((paramPolicy.priorityCategories & 0x4) == 0) {
        break label165;
      }
      bool1 = true;
      label35:
      this.allowMessages = bool1;
      if ((paramPolicy.priorityCategories & 0x2) == 0) {
        break label170;
      }
      bool1 = true;
      label51:
      this.allowEvents = bool1;
      if ((paramPolicy.priorityCategories & 0x1) == 0) {
        break label175;
      }
      bool1 = true;
      label67:
      this.allowReminders = bool1;
      if ((paramPolicy.priorityCategories & 0x10) == 0) {
        break label180;
      }
      bool1 = true;
      label84:
      this.allowRepeatCallers = bool1;
      this.allowCallsFrom = prioritySendersToSource(paramPolicy.priorityCallSenders, this.allowCallsFrom);
      this.allowMessagesFrom = prioritySendersToSource(paramPolicy.priorityMessageSenders, this.allowMessagesFrom);
      if (paramPolicy.suppressedVisualEffects != -1)
      {
        if ((paramPolicy.suppressedVisualEffects & 0x1) != 0) {
          break label185;
        }
        bool1 = true;
        label138:
        this.allowWhenScreenOff = bool1;
        if ((paramPolicy.suppressedVisualEffects & 0x2) != 0) {
          break label190;
        }
      }
    }
    label165:
    label170:
    label175:
    label180:
    label185:
    label190:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.allowWhenScreenOn = bool1;
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label35;
      bool1 = false;
      break label51;
      bool1 = false;
      break label67;
      bool1 = false;
      break label84;
      bool1 = false;
      break label138;
    }
  }
  
  public ZenModeConfig copy()
  {
    Parcel localParcel = Parcel.obtain();
    try
    {
      writeToParcel(localParcel, 0);
      localParcel.setDataPosition(0);
      ZenModeConfig localZenModeConfig = new ZenModeConfig(localParcel);
      return localZenModeConfig;
    }
    finally
    {
      localParcel.recycle();
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (!(paramObject instanceof ZenModeConfig)) {
      return false;
    }
    if (paramObject == this) {
      return true;
    }
    paramObject = (ZenModeConfig)paramObject;
    boolean bool1 = bool2;
    if (((ZenModeConfig)paramObject).allowCalls == this.allowCalls)
    {
      bool1 = bool2;
      if (((ZenModeConfig)paramObject).allowRepeatCallers == this.allowRepeatCallers)
      {
        bool1 = bool2;
        if (((ZenModeConfig)paramObject).allowMessages == this.allowMessages)
        {
          bool1 = bool2;
          if (((ZenModeConfig)paramObject).allowCallsFrom == this.allowCallsFrom)
          {
            bool1 = bool2;
            if (((ZenModeConfig)paramObject).allowMessagesFrom == this.allowMessagesFrom)
            {
              bool1 = bool2;
              if (((ZenModeConfig)paramObject).allowReminders == this.allowReminders)
              {
                bool1 = bool2;
                if (((ZenModeConfig)paramObject).allowEvents == this.allowEvents)
                {
                  bool1 = bool2;
                  if (((ZenModeConfig)paramObject).allowWhenScreenOff == this.allowWhenScreenOff)
                  {
                    bool1 = bool2;
                    if (((ZenModeConfig)paramObject).allowWhenScreenOn == this.allowWhenScreenOn)
                    {
                      bool1 = bool2;
                      if (((ZenModeConfig)paramObject).user == this.user)
                      {
                        bool1 = bool2;
                        if (Objects.equals(((ZenModeConfig)paramObject).automaticRules, this.automaticRules)) {
                          bool1 = Objects.equals(((ZenModeConfig)paramObject).manualRule, this.manualRule);
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Boolean.valueOf(this.allowCalls), Boolean.valueOf(this.allowRepeatCallers), Boolean.valueOf(this.allowMessages), Integer.valueOf(this.allowCallsFrom), Integer.valueOf(this.allowMessagesFrom), Boolean.valueOf(this.allowReminders), Boolean.valueOf(this.allowEvents), Boolean.valueOf(this.allowWhenScreenOff), Boolean.valueOf(this.allowWhenScreenOn), Integer.valueOf(this.user), this.automaticRules, this.manualRule });
  }
  
  public boolean isValid()
  {
    if (!isValidManualRule(this.manualRule)) {
      return false;
    }
    int j = this.automaticRules.size();
    int i = 0;
    while (i < j)
    {
      if (!isValidAutomaticRule((ZenRule)this.automaticRules.valueAt(i))) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public NotificationManager.Policy toNotificationPolicy()
  {
    int j = 0;
    if (this.allowCalls) {
      j = 8;
    }
    int i = j;
    if (this.allowMessages) {
      i = j | 0x4;
    }
    j = i;
    if (this.allowEvents) {
      j = i | 0x2;
    }
    i = j;
    if (this.allowReminders) {
      i = j | 0x1;
    }
    j = i;
    if (this.allowRepeatCallers) {
      j = i | 0x10;
    }
    i = 0;
    if (!this.allowWhenScreenOff) {
      i = 1;
    }
    int k = i;
    if (!this.allowWhenScreenOn) {
      k = i | 0x2;
    }
    return new NotificationManager.Policy(j, sourceToPrioritySenders(this.allowCallsFrom, 1), sourceToPrioritySenders(this.allowMessagesFrom, 1), k);
  }
  
  public String toString()
  {
    return ZenModeConfig.class.getSimpleName() + '[' + "user=" + this.user + ",allowCalls=" + this.allowCalls + ",allowRepeatCallers=" + this.allowRepeatCallers + ",allowMessages=" + this.allowMessages + ",allowCallsFrom=" + sourceToString(this.allowCallsFrom) + ",allowMessagesFrom=" + sourceToString(this.allowMessagesFrom) + ",allowReminders=" + this.allowReminders + ",allowEvents=" + this.allowEvents + ",allowWhenScreenOff=" + this.allowWhenScreenOff + ",allowWhenScreenOn=" + this.allowWhenScreenOn + ",automaticRules=" + this.automaticRules + ",manualRule=" + this.manualRule + ']';
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    if (this.allowCalls)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.allowRepeatCallers) {
        break label188;
      }
      paramInt = 1;
      label25:
      paramParcel.writeInt(paramInt);
      if (!this.allowMessages) {
        break label193;
      }
      paramInt = 1;
      label39:
      paramParcel.writeInt(paramInt);
      if (!this.allowReminders) {
        break label198;
      }
      paramInt = 1;
      label53:
      paramParcel.writeInt(paramInt);
      if (!this.allowEvents) {
        break label203;
      }
    }
    int j;
    String[] arrayOfString;
    ZenRule[] arrayOfZenRule;
    label188:
    label193:
    label198:
    label203:
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.allowCallsFrom);
      paramParcel.writeInt(this.allowMessagesFrom);
      paramParcel.writeInt(this.user);
      paramParcel.writeParcelable(this.manualRule, 0);
      if (this.automaticRules.isEmpty()) {
        break label256;
      }
      j = this.automaticRules.size();
      arrayOfString = new String[j];
      arrayOfZenRule = new ZenRule[j];
      paramInt = 0;
      while (paramInt < j)
      {
        arrayOfString[paramInt] = ((String)this.automaticRules.keyAt(paramInt));
        arrayOfZenRule[paramInt] = ((ZenRule)this.automaticRules.valueAt(paramInt));
        paramInt += 1;
      }
      paramInt = 0;
      break;
      paramInt = 0;
      break label25;
      paramInt = 0;
      break label39;
      paramInt = 0;
      break label53;
    }
    paramParcel.writeInt(j);
    paramParcel.writeStringArray(arrayOfString);
    paramParcel.writeTypedArray(arrayOfZenRule, 0);
    if (this.allowWhenScreenOff)
    {
      paramInt = 1;
      label236:
      paramParcel.writeInt(paramInt);
      if (!this.allowWhenScreenOn) {
        break label269;
      }
    }
    label256:
    label269:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramParcel.writeInt(0);
      break;
      paramInt = 0;
      break label236;
    }
  }
  
  public void writeXml(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "zen");
    paramXmlSerializer.attribute(null, "version", Integer.toString(2));
    paramXmlSerializer.attribute(null, "user", Integer.toString(this.user));
    paramXmlSerializer.startTag(null, "allow");
    paramXmlSerializer.attribute(null, "calls", Boolean.toString(this.allowCalls));
    paramXmlSerializer.attribute(null, "repeatCallers", Boolean.toString(this.allowRepeatCallers));
    paramXmlSerializer.attribute(null, "messages", Boolean.toString(this.allowMessages));
    paramXmlSerializer.attribute(null, "reminders", Boolean.toString(this.allowReminders));
    paramXmlSerializer.attribute(null, "events", Boolean.toString(this.allowEvents));
    paramXmlSerializer.attribute(null, "callsFrom", Integer.toString(this.allowCallsFrom));
    paramXmlSerializer.attribute(null, "messagesFrom", Integer.toString(this.allowMessagesFrom));
    paramXmlSerializer.attribute(null, "visualScreenOff", Boolean.toString(this.allowWhenScreenOff));
    paramXmlSerializer.attribute(null, "visualScreenOn", Boolean.toString(this.allowWhenScreenOn));
    paramXmlSerializer.endTag(null, "allow");
    if (this.manualRule != null)
    {
      paramXmlSerializer.startTag(null, "manual");
      writeRuleXml(this.manualRule, paramXmlSerializer);
      paramXmlSerializer.endTag(null, "manual");
    }
    int j = this.automaticRules.size();
    int i = 0;
    while (i < j)
    {
      String str = (String)this.automaticRules.keyAt(i);
      ZenRule localZenRule = (ZenRule)this.automaticRules.valueAt(i);
      paramXmlSerializer.startTag(null, "automatic");
      paramXmlSerializer.attribute(null, "ruleId", str);
      writeRuleXml(localZenRule, paramXmlSerializer);
      paramXmlSerializer.endTag(null, "automatic");
      i += 1;
    }
    paramXmlSerializer.endTag(null, "zen");
  }
  
  public static class Diff
  {
    private final ArrayList<String> lines = new ArrayList();
    
    private Diff addLine(String paramString1, String paramString2)
    {
      this.lines.add(paramString1 + ":" + paramString2);
      return this;
    }
    
    public Diff addLine(String paramString, Object paramObject1, Object paramObject2)
    {
      return addLine(paramString, paramObject1 + "->" + paramObject2);
    }
    
    public Diff addLine(String paramString1, String paramString2, Object paramObject1, Object paramObject2)
    {
      return addLine(paramString1 + "." + paramString2, paramObject1, paramObject2);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("Diff[");
      int j = this.lines.size();
      int i = 0;
      while (i < j)
      {
        if (i > 0) {
          localStringBuilder.append(',');
        }
        localStringBuilder.append((String)this.lines.get(i));
        i += 1;
      }
      return ']';
    }
  }
  
  public static class EventInfo
  {
    public static final int REPLY_ANY_EXCEPT_NO = 0;
    public static final int REPLY_YES = 2;
    public static final int REPLY_YES_OR_MAYBE = 1;
    public String calendar;
    public int reply;
    public int userId = 55536;
    
    public static int resolveUserId(int paramInt)
    {
      int i = paramInt;
      if (paramInt == 55536) {
        i = ActivityManager.getCurrentUser();
      }
      return i;
    }
    
    public EventInfo copy()
    {
      EventInfo localEventInfo = new EventInfo();
      localEventInfo.userId = this.userId;
      localEventInfo.calendar = this.calendar;
      localEventInfo.reply = this.reply;
      return localEventInfo;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!(paramObject instanceof EventInfo)) {
        return false;
      }
      paramObject = (EventInfo)paramObject;
      boolean bool1 = bool2;
      if (this.userId == ((EventInfo)paramObject).userId)
      {
        bool1 = bool2;
        if (Objects.equals(this.calendar, ((EventInfo)paramObject).calendar))
        {
          bool1 = bool2;
          if (this.reply == ((EventInfo)paramObject).reply) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      return 0;
    }
  }
  
  public static abstract interface Migration
  {
    public abstract ZenModeConfig migrate(ZenModeConfig.XmlV1 paramXmlV1);
  }
  
  public static class ScheduleInfo
  {
    public int[] days;
    public int endHour;
    public int endMinute;
    public boolean exitAtAlarm;
    public long nextAlarm;
    public int startHour;
    public int startMinute;
    
    protected static String ts(long paramLong)
    {
      return new Date(paramLong) + " (" + paramLong + ")";
    }
    
    public ScheduleInfo copy()
    {
      ScheduleInfo localScheduleInfo = new ScheduleInfo();
      if (this.days != null)
      {
        localScheduleInfo.days = new int[this.days.length];
        System.arraycopy(this.days, 0, localScheduleInfo.days, 0, this.days.length);
      }
      localScheduleInfo.startHour = this.startHour;
      localScheduleInfo.startMinute = this.startMinute;
      localScheduleInfo.endHour = this.endHour;
      localScheduleInfo.endMinute = this.endMinute;
      localScheduleInfo.exitAtAlarm = this.exitAtAlarm;
      localScheduleInfo.nextAlarm = this.nextAlarm;
      return localScheduleInfo;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!(paramObject instanceof ScheduleInfo)) {
        return false;
      }
      paramObject = (ScheduleInfo)paramObject;
      boolean bool1 = bool2;
      if (ZenModeConfig.-wrap5(this.days).equals(ZenModeConfig.-wrap5(((ScheduleInfo)paramObject).days)))
      {
        bool1 = bool2;
        if (this.startHour == ((ScheduleInfo)paramObject).startHour)
        {
          bool1 = bool2;
          if (this.startMinute == ((ScheduleInfo)paramObject).startMinute)
          {
            bool1 = bool2;
            if (this.endHour == ((ScheduleInfo)paramObject).endHour)
            {
              bool1 = bool2;
              if (this.endMinute == ((ScheduleInfo)paramObject).endMinute)
              {
                bool1 = bool2;
                if (this.exitAtAlarm == ((ScheduleInfo)paramObject).exitAtAlarm) {
                  bool1 = true;
                }
              }
            }
          }
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      return 0;
    }
    
    public String toString()
    {
      return "ScheduleInfo{days=" + Arrays.toString(this.days) + ", startHour=" + this.startHour + ", startMinute=" + this.startMinute + ", endHour=" + this.endHour + ", endMinute=" + this.endMinute + ", exitAtAlarm=" + this.exitAtAlarm + ", nextAlarm=" + ts(this.nextAlarm) + '}';
    }
  }
  
  public static final class XmlV1
  {
    private static final String EXIT_CONDITION_ATT_COMPONENT = "component";
    private static final String EXIT_CONDITION_TAG = "exitCondition";
    private static final String SLEEP_ATT_END_HR = "endHour";
    private static final String SLEEP_ATT_END_MIN = "endMin";
    private static final String SLEEP_ATT_MODE = "mode";
    private static final String SLEEP_ATT_NONE = "none";
    private static final String SLEEP_ATT_START_HR = "startHour";
    private static final String SLEEP_ATT_START_MIN = "startMin";
    public static final String SLEEP_MODE_DAYS_PREFIX = "days:";
    public static final String SLEEP_MODE_NIGHTS = "nights";
    public static final String SLEEP_MODE_WEEKNIGHTS = "weeknights";
    private static final String SLEEP_TAG = "sleep";
    public boolean allowCalls;
    public boolean allowEvents = true;
    public int allowFrom = 0;
    public boolean allowMessages;
    public boolean allowReminders = true;
    public ComponentName[] conditionComponents;
    public Uri[] conditionIds;
    public Condition exitCondition;
    public ComponentName exitConditionComponent;
    public int sleepEndHour;
    public int sleepEndMinute;
    public String sleepMode;
    public boolean sleepNone;
    public int sleepStartHour;
    public int sleepStartMinute;
    
    private static boolean isValidSleepMode(String paramString)
    {
      return (paramString == null) || (paramString.equals("nights")) || (paramString.equals("weeknights")) || (tryParseDays(paramString) != null);
    }
    
    public static XmlV1 readXml(XmlPullParser paramXmlPullParser)
      throws XmlPullParserException, IOException
    {
      XmlV1 localXmlV1 = new XmlV1();
      ArrayList localArrayList1 = new ArrayList();
      ArrayList localArrayList2 = new ArrayList();
      for (;;)
      {
        int i = paramXmlPullParser.next();
        if (i == 1) {
          break;
        }
        Object localObject = paramXmlPullParser.getName();
        if ((i == 3) && ("zen".equals(localObject)))
        {
          if (!localArrayList1.isEmpty())
          {
            localXmlV1.conditionComponents = ((ComponentName[])localArrayList1.toArray(new ComponentName[localArrayList1.size()]));
            localXmlV1.conditionIds = ((Uri[])localArrayList2.toArray(new Uri[localArrayList2.size()]));
          }
          return localXmlV1;
        }
        if (i == 2) {
          if ("allow".equals(localObject))
          {
            localXmlV1.allowCalls = ZenModeConfig.-wrap2(paramXmlPullParser, "calls", false);
            localXmlV1.allowMessages = ZenModeConfig.-wrap2(paramXmlPullParser, "messages", false);
            localXmlV1.allowReminders = ZenModeConfig.-wrap2(paramXmlPullParser, "reminders", true);
            localXmlV1.allowEvents = ZenModeConfig.-wrap2(paramXmlPullParser, "events", true);
            localXmlV1.allowFrom = ZenModeConfig.-wrap4(paramXmlPullParser, "from", 0);
            if ((localXmlV1.allowFrom < 0) || (localXmlV1.allowFrom > 2)) {
              throw new IndexOutOfBoundsException("bad source in config:" + localXmlV1.allowFrom);
            }
          }
          else
          {
            if ("sleep".equals(localObject))
            {
              localObject = paramXmlPullParser.getAttributeValue(null, "mode");
              label267:
              int j;
              if (isValidSleepMode((String)localObject))
              {
                localXmlV1.sleepMode = ((String)localObject);
                localXmlV1.sleepNone = ZenModeConfig.-wrap2(paramXmlPullParser, "none", false);
                i = ZenModeConfig.-wrap4(paramXmlPullParser, "startHour", 0);
                int m = ZenModeConfig.-wrap4(paramXmlPullParser, "startMin", 0);
                int k = ZenModeConfig.-wrap4(paramXmlPullParser, "endHour", 0);
                j = ZenModeConfig.-wrap4(paramXmlPullParser, "endMin", 0);
                if (!ZenModeConfig.isValidHour(i)) {
                  break label388;
                }
                label326:
                localXmlV1.sleepStartHour = i;
                if (!ZenModeConfig.isValidMinute(m)) {
                  break label393;
                }
                i = m;
                label343:
                localXmlV1.sleepStartMinute = i;
                if (!ZenModeConfig.isValidHour(k)) {
                  break label398;
                }
                i = k;
                label358:
                localXmlV1.sleepEndHour = i;
                if (!ZenModeConfig.isValidMinute(j)) {
                  break label403;
                }
              }
              label388:
              label393:
              label398:
              label403:
              for (i = j;; i = 0)
              {
                localXmlV1.sleepEndMinute = i;
                break;
                localObject = null;
                break label267;
                i = 0;
                break label326;
                i = 0;
                break label343;
                i = 0;
                break label358;
              }
            }
            if ("condition".equals(localObject))
            {
              localObject = ZenModeConfig.-wrap0(paramXmlPullParser, "component");
              Uri localUri = ZenModeConfig.-wrap1(paramXmlPullParser, "id");
              if ((localObject != null) && (localUri != null))
              {
                localArrayList1.add(localObject);
                localArrayList2.add(localUri);
              }
            }
            else if ("exitCondition".equals(localObject))
            {
              localXmlV1.exitCondition = ZenModeConfig.readConditionXml(paramXmlPullParser);
              if (localXmlV1.exitCondition != null) {
                localXmlV1.exitConditionComponent = ZenModeConfig.-wrap0(paramXmlPullParser, "component");
              }
            }
          }
        }
      }
      throw new IllegalStateException("Failed to reach END_DOCUMENT");
    }
    
    public static int[] tryParseDays(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      paramString = paramString.trim();
      if ("nights".equals(paramString)) {
        return ZenModeConfig.ALL_DAYS;
      }
      if ("weeknights".equals(paramString)) {
        return ZenModeConfig.WEEKNIGHT_DAYS;
      }
      if (!paramString.startsWith("days:")) {
        return null;
      }
      if (paramString.equals("days:")) {
        return null;
      }
      return ZenModeConfig.-wrap3(paramString.substring("days:".length()), ",");
    }
  }
  
  public static class ZenRule
    implements Parcelable
  {
    public static final Parcelable.Creator<ZenRule> CREATOR = new Parcelable.Creator()
    {
      public ZenModeConfig.ZenRule createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ZenModeConfig.ZenRule(paramAnonymousParcel);
      }
      
      public ZenModeConfig.ZenRule[] newArray(int paramAnonymousInt)
      {
        return new ZenModeConfig.ZenRule[paramAnonymousInt];
      }
    };
    public ComponentName component;
    public Condition condition;
    public Uri conditionId;
    public long creationTime;
    public boolean enabled;
    public String enabler;
    public String id;
    public String name;
    public boolean snoozing;
    public int zenMode;
    
    public ZenRule() {}
    
    public ZenRule(Parcel paramParcel)
    {
      if (paramParcel.readInt() == 1) {}
      for (boolean bool1 = true;; bool1 = false)
      {
        this.enabled = bool1;
        bool1 = bool2;
        if (paramParcel.readInt() == 1) {
          bool1 = true;
        }
        this.snoozing = bool1;
        if (paramParcel.readInt() == 1) {
          this.name = paramParcel.readString();
        }
        this.zenMode = paramParcel.readInt();
        this.conditionId = ((Uri)paramParcel.readParcelable(null));
        this.condition = ((Condition)paramParcel.readParcelable(null));
        this.component = ((ComponentName)paramParcel.readParcelable(null));
        if (paramParcel.readInt() == 1) {
          this.id = paramParcel.readString();
        }
        this.creationTime = paramParcel.readLong();
        if (paramParcel.readInt() == 1) {
          this.enabler = paramParcel.readString();
        }
        return;
      }
    }
    
    private void appendDiff(ZenModeConfig.Diff paramDiff, String paramString, ZenRule paramZenRule)
    {
      if (paramZenRule == null)
      {
        ZenModeConfig.Diff.-wrap0(paramDiff, paramString, "delete");
        return;
      }
      if (this.enabled != paramZenRule.enabled) {
        paramDiff.addLine(paramString, "enabled", Boolean.valueOf(this.enabled), Boolean.valueOf(paramZenRule.enabled));
      }
      if (this.snoozing != paramZenRule.snoozing) {
        paramDiff.addLine(paramString, "snoozing", Boolean.valueOf(this.snoozing), Boolean.valueOf(paramZenRule.snoozing));
      }
      if (!Objects.equals(this.name, paramZenRule.name)) {
        paramDiff.addLine(paramString, "name", this.name, paramZenRule.name);
      }
      if (this.zenMode != paramZenRule.zenMode) {
        paramDiff.addLine(paramString, "zenMode", Integer.valueOf(this.zenMode), Integer.valueOf(paramZenRule.zenMode));
      }
      if (!Objects.equals(this.conditionId, paramZenRule.conditionId)) {
        paramDiff.addLine(paramString, "conditionId", this.conditionId, paramZenRule.conditionId);
      }
      if (!Objects.equals(this.condition, paramZenRule.condition)) {
        paramDiff.addLine(paramString, "condition", this.condition, paramZenRule.condition);
      }
      if (!Objects.equals(this.component, paramZenRule.component)) {
        paramDiff.addLine(paramString, "component", this.component, paramZenRule.component);
      }
      if (!Objects.equals(this.id, paramZenRule.id)) {
        paramDiff.addLine(paramString, "id", this.id, paramZenRule.id);
      }
      if (this.creationTime != paramZenRule.creationTime) {
        paramDiff.addLine(paramString, "creationTime", Long.valueOf(this.creationTime), Long.valueOf(paramZenRule.creationTime));
      }
      if (this.enabler != paramZenRule.enabler) {
        paramDiff.addLine(paramString, "enabler", this.enabler, paramZenRule.enabler);
      }
    }
    
    private static void appendDiff(ZenModeConfig.Diff paramDiff, String paramString, ZenRule paramZenRule1, ZenRule paramZenRule2)
    {
      if (paramDiff == null) {
        return;
      }
      if (paramZenRule1 == null)
      {
        if (paramZenRule2 != null) {
          ZenModeConfig.Diff.-wrap0(paramDiff, paramString, "insert");
        }
        return;
      }
      paramZenRule1.appendDiff(paramDiff, paramString, paramZenRule2);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!(paramObject instanceof ZenRule)) {
        return false;
      }
      if (paramObject == this) {
        return true;
      }
      paramObject = (ZenRule)paramObject;
      boolean bool1 = bool2;
      if (((ZenRule)paramObject).enabled == this.enabled)
      {
        bool1 = bool2;
        if (((ZenRule)paramObject).snoozing == this.snoozing)
        {
          bool1 = bool2;
          if (Objects.equals(((ZenRule)paramObject).name, this.name))
          {
            bool1 = bool2;
            if (((ZenRule)paramObject).zenMode == this.zenMode)
            {
              bool1 = bool2;
              if (Objects.equals(((ZenRule)paramObject).conditionId, this.conditionId))
              {
                bool1 = bool2;
                if (Objects.equals(((ZenRule)paramObject).condition, this.condition))
                {
                  bool1 = bool2;
                  if (Objects.equals(((ZenRule)paramObject).component, this.component))
                  {
                    bool1 = bool2;
                    if (Objects.equals(((ZenRule)paramObject).id, this.id))
                    {
                      bool1 = bool2;
                      if (((ZenRule)paramObject).creationTime == this.creationTime) {
                        bool1 = Objects.equals(((ZenRule)paramObject).enabler, this.enabler);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      return Objects.hash(new Object[] { Boolean.valueOf(this.enabled), Boolean.valueOf(this.snoozing), this.name, Integer.valueOf(this.zenMode), this.conditionId, this.condition, this.component, this.id, Long.valueOf(this.creationTime), this.enabler });
    }
    
    public boolean isAutomaticActive()
    {
      if ((!this.enabled) || (this.snoozing)) {}
      while (this.component == null) {
        return false;
      }
      return isTrueOrUnknown();
    }
    
    public boolean isTrueOrUnknown()
    {
      if (this.condition != null) {
        return (this.condition.state == 1) || (this.condition.state == 2);
      }
      return false;
    }
    
    public String toString()
    {
      return ZenRule.class.getSimpleName() + '[' + "enabled=" + this.enabled + ",snoozing=" + this.snoozing + ",name=" + this.name + ",zenMode=" + Settings.Global.zenModeToString(this.zenMode) + ",conditionId=" + this.conditionId + ",condition=" + this.condition + ",component=" + this.component + ",id=" + this.id + ",creationTime=" + this.creationTime + ",enabler=" + this.enabler + ']';
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      if (this.enabled)
      {
        paramInt = 1;
        paramParcel.writeInt(paramInt);
        if (!this.snoozing) {
          break label137;
        }
        paramInt = 1;
        label23:
        paramParcel.writeInt(paramInt);
        if (this.name == null) {
          break label142;
        }
        paramParcel.writeInt(1);
        paramParcel.writeString(this.name);
        label48:
        paramParcel.writeInt(this.zenMode);
        paramParcel.writeParcelable(this.conditionId, 0);
        paramParcel.writeParcelable(this.condition, 0);
        paramParcel.writeParcelable(this.component, 0);
        if (this.id == null) {
          break label150;
        }
        paramParcel.writeInt(1);
        paramParcel.writeString(this.id);
      }
      for (;;)
      {
        paramParcel.writeLong(this.creationTime);
        if (this.enabler == null) {
          break label158;
        }
        paramParcel.writeInt(1);
        paramParcel.writeString(this.enabler);
        return;
        paramInt = 0;
        break;
        label137:
        paramInt = 0;
        break label23;
        label142:
        paramParcel.writeInt(0);
        break label48;
        label150:
        paramParcel.writeInt(0);
      }
      label158:
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/notification/ZenModeConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */