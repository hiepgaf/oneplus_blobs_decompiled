package com.android.server.usage;

import android.app.usage.ConfigurationStats;
import android.app.usage.TimeSparseArray;
import android.app.usage.UsageEvents.Event;
import android.app.usage.UsageStats;
import android.content.res.Configuration;
import android.util.ArrayMap;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.net.ProtocolException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

final class UsageStatsXmlV1
{
  private static final String ACTIVE_ATTR = "active";
  private static final String CLASS_ATTR = "class";
  private static final String CONFIGURATIONS_TAG = "configurations";
  private static final String CONFIG_TAG = "config";
  private static final String COUNT_ATTR = "count";
  private static final String END_TIME_ATTR = "endTime";
  private static final String EVENT_LOG_TAG = "event-log";
  private static final String EVENT_TAG = "event";
  private static final String LAST_EVENT_ATTR = "lastEvent";
  private static final String LAST_TIME_ACTIVE_ATTR = "lastTimeActive";
  private static final String PACKAGES_TAG = "packages";
  private static final String PACKAGE_ATTR = "package";
  private static final String PACKAGE_TAG = "package";
  private static final String SHORTCUT_ID_ATTR = "shortcutId";
  private static final String TIME_ATTR = "time";
  private static final String TOTAL_TIME_ACTIVE_ATTR = "timeActive";
  private static final String TYPE_ATTR = "type";
  
  private static void loadConfigStats(XmlPullParser paramXmlPullParser, IntervalStats paramIntervalStats)
    throws XmlPullParserException, IOException
  {
    Object localObject = new Configuration();
    Configuration.readXmlAttrs(paramXmlPullParser, (Configuration)localObject);
    localObject = paramIntervalStats.getOrCreateConfigurationStats((Configuration)localObject);
    ((ConfigurationStats)localObject).mLastTimeActive = (paramIntervalStats.beginTime + XmlUtils.readLongAttribute(paramXmlPullParser, "lastTimeActive"));
    ((ConfigurationStats)localObject).mTotalTimeActive = XmlUtils.readLongAttribute(paramXmlPullParser, "timeActive");
    ((ConfigurationStats)localObject).mActivationCount = XmlUtils.readIntAttribute(paramXmlPullParser, "count");
    if (XmlUtils.readBooleanAttribute(paramXmlPullParser, "active")) {
      paramIntervalStats.activeConfiguration = ((ConfigurationStats)localObject).mConfiguration;
    }
  }
  
  private static void loadEvent(XmlPullParser paramXmlPullParser, IntervalStats paramIntervalStats)
    throws XmlPullParserException, IOException
  {
    Object localObject1 = null;
    Object localObject2 = XmlUtils.readStringAttribute(paramXmlPullParser, "package");
    if (localObject2 == null) {
      throw new ProtocolException("no package attribute present");
    }
    localObject2 = paramIntervalStats.buildEvent((String)localObject2, XmlUtils.readStringAttribute(paramXmlPullParser, "class"));
    ((UsageEvents.Event)localObject2).mTimeStamp = (paramIntervalStats.beginTime + XmlUtils.readLongAttribute(paramXmlPullParser, "time"));
    ((UsageEvents.Event)localObject2).mEventType = XmlUtils.readIntAttribute(paramXmlPullParser, "type");
    switch (((UsageEvents.Event)localObject2).mEventType)
    {
    }
    for (;;)
    {
      if (paramIntervalStats.events == null) {
        paramIntervalStats.events = new TimeSparseArray();
      }
      paramIntervalStats.events.put(((UsageEvents.Event)localObject2).mTimeStamp, localObject2);
      return;
      ((UsageEvents.Event)localObject2).mConfiguration = new Configuration();
      Configuration.readXmlAttrs(paramXmlPullParser, ((UsageEvents.Event)localObject2).mConfiguration);
      continue;
      String str = XmlUtils.readStringAttribute(paramXmlPullParser, "shortcutId");
      paramXmlPullParser = (XmlPullParser)localObject1;
      if (str != null) {
        paramXmlPullParser = str.intern();
      }
      ((UsageEvents.Event)localObject2).mShortcutId = paramXmlPullParser;
    }
  }
  
  private static void loadUsageStats(XmlPullParser paramXmlPullParser, IntervalStats paramIntervalStats)
    throws IOException
  {
    Object localObject = paramXmlPullParser.getAttributeValue(null, "package");
    if (localObject == null) {
      throw new ProtocolException("no package attribute present");
    }
    localObject = paramIntervalStats.getOrCreateUsageStats((String)localObject);
    ((UsageStats)localObject).mLastTimeUsed = (paramIntervalStats.beginTime + XmlUtils.readLongAttribute(paramXmlPullParser, "lastTimeActive"));
    ((UsageStats)localObject).mTotalTimeInForeground = XmlUtils.readLongAttribute(paramXmlPullParser, "timeActive");
    ((UsageStats)localObject).mLastEvent = XmlUtils.readIntAttribute(paramXmlPullParser, "lastEvent");
  }
  
  public static void read(XmlPullParser paramXmlPullParser, IntervalStats paramIntervalStats)
    throws XmlPullParserException, IOException
  {
    paramIntervalStats.packageStats.clear();
    paramIntervalStats.configurations.clear();
    paramIntervalStats.activeConfiguration = null;
    if (paramIntervalStats.events != null) {
      paramIntervalStats.events.clear();
    }
    paramIntervalStats.endTime = (paramIntervalStats.beginTime + XmlUtils.readLongAttribute(paramXmlPullParser, "endTime"));
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if (j == 2)
      {
        String str = paramXmlPullParser.getName();
        if (str.equals("package")) {
          loadUsageStats(paramXmlPullParser, paramIntervalStats);
        } else if (str.equals("config")) {
          loadConfigStats(paramXmlPullParser, paramIntervalStats);
        } else if (str.equals("event")) {
          loadEvent(paramXmlPullParser, paramIntervalStats);
        }
      }
    }
  }
  
  public static void write(XmlSerializer paramXmlSerializer, IntervalStats paramIntervalStats)
    throws IOException
  {
    XmlUtils.writeLongAttribute(paramXmlSerializer, "endTime", paramIntervalStats.endTime - paramIntervalStats.beginTime);
    paramXmlSerializer.startTag(null, "packages");
    int j = paramIntervalStats.packageStats.size();
    int i = 0;
    while (i < j)
    {
      writeUsageStats(paramXmlSerializer, paramIntervalStats, (UsageStats)paramIntervalStats.packageStats.valueAt(i));
      i += 1;
    }
    paramXmlSerializer.endTag(null, "packages");
    paramXmlSerializer.startTag(null, "configurations");
    j = paramIntervalStats.configurations.size();
    i = 0;
    while (i < j)
    {
      boolean bool = paramIntervalStats.activeConfiguration.equals((Configuration)paramIntervalStats.configurations.keyAt(i));
      writeConfigStats(paramXmlSerializer, paramIntervalStats, (ConfigurationStats)paramIntervalStats.configurations.valueAt(i), bool);
      i += 1;
    }
    paramXmlSerializer.endTag(null, "configurations");
    paramXmlSerializer.startTag(null, "event-log");
    if (paramIntervalStats.events != null) {}
    for (i = paramIntervalStats.events.size();; i = 0)
    {
      j = 0;
      while (j < i)
      {
        writeEvent(paramXmlSerializer, paramIntervalStats, (UsageEvents.Event)paramIntervalStats.events.valueAt(j));
        j += 1;
      }
    }
    paramXmlSerializer.endTag(null, "event-log");
  }
  
  private static void writeConfigStats(XmlSerializer paramXmlSerializer, IntervalStats paramIntervalStats, ConfigurationStats paramConfigurationStats, boolean paramBoolean)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "config");
    XmlUtils.writeLongAttribute(paramXmlSerializer, "lastTimeActive", paramConfigurationStats.mLastTimeActive - paramIntervalStats.beginTime);
    XmlUtils.writeLongAttribute(paramXmlSerializer, "timeActive", paramConfigurationStats.mTotalTimeActive);
    XmlUtils.writeIntAttribute(paramXmlSerializer, "count", paramConfigurationStats.mActivationCount);
    if (paramBoolean) {
      XmlUtils.writeBooleanAttribute(paramXmlSerializer, "active", true);
    }
    Configuration.writeXmlAttrs(paramXmlSerializer, paramConfigurationStats.mConfiguration);
    paramXmlSerializer.endTag(null, "config");
  }
  
  private static void writeEvent(XmlSerializer paramXmlSerializer, IntervalStats paramIntervalStats, UsageEvents.Event paramEvent)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "event");
    XmlUtils.writeLongAttribute(paramXmlSerializer, "time", paramEvent.mTimeStamp - paramIntervalStats.beginTime);
    XmlUtils.writeStringAttribute(paramXmlSerializer, "package", paramEvent.mPackage);
    if (paramEvent.mClass != null) {
      XmlUtils.writeStringAttribute(paramXmlSerializer, "class", paramEvent.mClass);
    }
    XmlUtils.writeIntAttribute(paramXmlSerializer, "type", paramEvent.mEventType);
    switch (paramEvent.mEventType)
    {
    }
    for (;;)
    {
      paramXmlSerializer.endTag(null, "event");
      return;
      if (paramEvent.mConfiguration != null)
      {
        Configuration.writeXmlAttrs(paramXmlSerializer, paramEvent.mConfiguration);
        continue;
        if (paramEvent.mShortcutId != null) {
          XmlUtils.writeStringAttribute(paramXmlSerializer, "shortcutId", paramEvent.mShortcutId);
        }
      }
    }
  }
  
  private static void writeUsageStats(XmlSerializer paramXmlSerializer, IntervalStats paramIntervalStats, UsageStats paramUsageStats)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "package");
    XmlUtils.writeLongAttribute(paramXmlSerializer, "lastTimeActive", paramUsageStats.mLastTimeUsed - paramIntervalStats.beginTime);
    XmlUtils.writeStringAttribute(paramXmlSerializer, "package", paramUsageStats.mPackageName);
    XmlUtils.writeLongAttribute(paramXmlSerializer, "timeActive", paramUsageStats.mTotalTimeInForeground);
    XmlUtils.writeIntAttribute(paramXmlSerializer, "lastEvent", paramUsageStats.mLastEvent);
    paramXmlSerializer.endTag(null, "package");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usage/UsageStatsXmlV1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */