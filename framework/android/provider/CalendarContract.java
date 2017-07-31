package android.provider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorEntityIterator;
import android.content.Entity;
import android.content.EntityIterator;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.RemoteException;
import android.util.SeempLog;

public final class CalendarContract
{
  public static final String ACCOUNT_TYPE_LOCAL = "LOCAL";
  public static final String ACTION_EVENT_REMINDER = "android.intent.action.EVENT_REMINDER";
  public static final String ACTION_HANDLE_CUSTOM_EVENT = "android.provider.calendar.action.HANDLE_CUSTOM_EVENT";
  public static final String AUTHORITY = "com.android.calendar";
  public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";
  public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar");
  public static final String EXTRA_CUSTOM_APP_URI = "customAppUri";
  public static final String EXTRA_EVENT_ALL_DAY = "allDay";
  public static final String EXTRA_EVENT_BEGIN_TIME = "beginTime";
  public static final String EXTRA_EVENT_END_TIME = "endTime";
  private static final String TAG = "Calendar";
  
  public static final class Attendees
    implements BaseColumns, CalendarContract.AttendeesColumns, CalendarContract.EventsColumns
  {
    private static final String ATTENDEES_WHERE = "event_id=?";
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar/attendees");
    
    public static final Cursor query(ContentResolver paramContentResolver, long paramLong, String[] paramArrayOfString)
    {
      SeempLog.record(54);
      String str = Long.toString(paramLong);
      return paramContentResolver.query(CONTENT_URI, paramArrayOfString, "event_id=?", new String[] { str }, null);
    }
  }
  
  protected static abstract interface AttendeesColumns
  {
    public static final String ATTENDEE_EMAIL = "attendeeEmail";
    public static final String ATTENDEE_IDENTITY = "attendeeIdentity";
    public static final String ATTENDEE_ID_NAMESPACE = "attendeeIdNamespace";
    public static final String ATTENDEE_NAME = "attendeeName";
    public static final String ATTENDEE_RELATIONSHIP = "attendeeRelationship";
    public static final String ATTENDEE_STATUS = "attendeeStatus";
    public static final int ATTENDEE_STATUS_ACCEPTED = 1;
    public static final int ATTENDEE_STATUS_DECLINED = 2;
    public static final int ATTENDEE_STATUS_INVITED = 3;
    public static final int ATTENDEE_STATUS_NONE = 0;
    public static final int ATTENDEE_STATUS_TENTATIVE = 4;
    public static final String ATTENDEE_TYPE = "attendeeType";
    public static final String EVENT_ID = "event_id";
    public static final int RELATIONSHIP_ATTENDEE = 1;
    public static final int RELATIONSHIP_NONE = 0;
    public static final int RELATIONSHIP_ORGANIZER = 2;
    public static final int RELATIONSHIP_PERFORMER = 3;
    public static final int RELATIONSHIP_SPEAKER = 4;
    public static final int TYPE_NONE = 0;
    public static final int TYPE_OPTIONAL = 2;
    public static final int TYPE_REQUIRED = 1;
    public static final int TYPE_RESOURCE = 3;
  }
  
  public static final class CalendarAlerts
    implements BaseColumns, CalendarContract.CalendarAlertsColumns, CalendarContract.EventsColumns, CalendarContract.CalendarColumns
  {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar/calendar_alerts");
    public static final Uri CONTENT_URI_BY_INSTANCE = Uri.parse("content://com.android.calendar/calendar_alerts/by_instance");
    private static final boolean DEBUG = false;
    private static final String SORT_ORDER_ALARMTIME_ASC = "alarmTime ASC";
    public static final String TABLE_NAME = "CalendarAlerts";
    private static final String WHERE_ALARM_EXISTS = "event_id=? AND begin=? AND alarmTime=?";
    private static final String WHERE_FINDNEXTALARMTIME = "alarmTime>=?";
    private static final String WHERE_RESCHEDULE_MISSED_ALARMS = "state=0 AND alarmTime<? AND alarmTime>? AND end>=?";
    
    public static final boolean alarmExists(ContentResolver paramContentResolver, long paramLong1, long paramLong2, long paramLong3)
    {
      SeempLog.record(52);
      Uri localUri = CONTENT_URI;
      String str1 = Long.toString(paramLong1);
      String str2 = Long.toString(paramLong2);
      String str3 = Long.toString(paramLong3);
      paramContentResolver = paramContentResolver.query(localUri, new String[] { "alarmTime" }, "event_id=? AND begin=? AND alarmTime=?", new String[] { str1, str2, str3 }, null);
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (paramContentResolver != null) {}
      try
      {
        int i = paramContentResolver.getCount();
        bool1 = bool2;
        if (i > 0) {
          bool1 = true;
        }
        return bool1;
      }
      finally
      {
        if (paramContentResolver != null) {
          paramContentResolver.close();
        }
      }
    }
    
    public static final long findNextAlarmTime(ContentResolver paramContentResolver, long paramLong)
    {
      SeempLog.record(53);
      new StringBuilder().append("alarmTime>=").append(paramLong).toString();
      Uri localUri = CONTENT_URI;
      String str = Long.toString(paramLong);
      paramContentResolver = paramContentResolver.query(localUri, new String[] { "alarmTime" }, "alarmTime>=?", new String[] { str }, "alarmTime ASC");
      long l = -1L;
      paramLong = l;
      if (paramContentResolver != null) {
        paramLong = l;
      }
      try
      {
        if (paramContentResolver.moveToFirst()) {
          paramLong = paramContentResolver.getLong(0);
        }
        return paramLong;
      }
      finally
      {
        if (paramContentResolver != null) {
          paramContentResolver.close();
        }
      }
    }
    
    public static final Uri insert(ContentResolver paramContentResolver, long paramLong1, long paramLong2, long paramLong3, long paramLong4, int paramInt)
    {
      SeempLog.record(51);
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("event_id", Long.valueOf(paramLong1));
      localContentValues.put("begin", Long.valueOf(paramLong2));
      localContentValues.put("end", Long.valueOf(paramLong3));
      localContentValues.put("alarmTime", Long.valueOf(paramLong4));
      localContentValues.put("creationTime", Long.valueOf(System.currentTimeMillis()));
      localContentValues.put("receivedTime", Integer.valueOf(0));
      localContentValues.put("notifyTime", Integer.valueOf(0));
      localContentValues.put("state", Integer.valueOf(0));
      localContentValues.put("minutes", Integer.valueOf(paramInt));
      return paramContentResolver.insert(CONTENT_URI, localContentValues);
    }
    
    public static final void rescheduleMissedAlarms(ContentResolver paramContentResolver, Context paramContext, AlarmManager paramAlarmManager)
    {
      long l1 = System.currentTimeMillis();
      Uri localUri = CONTENT_URI;
      String str1 = Long.toString(l1);
      String str2 = Long.toString(l1 - 86400000L);
      String str3 = Long.toString(l1);
      paramContentResolver = paramContentResolver.query(localUri, new String[] { "alarmTime" }, "state=0 AND alarmTime<? AND alarmTime>? AND end>=?", new String[] { str1, str2, str3 }, "alarmTime ASC");
      if (paramContentResolver == null) {
        return;
      }
      l1 = -1L;
      try
      {
        while (paramContentResolver.moveToNext())
        {
          long l2 = paramContentResolver.getLong(0);
          if (l1 != l2)
          {
            scheduleAlarm(paramContext, paramAlarmManager, l2);
            l1 = l2;
          }
        }
        return;
      }
      finally
      {
        paramContentResolver.close();
      }
    }
    
    public static void scheduleAlarm(Context paramContext, AlarmManager paramAlarmManager, long paramLong)
    {
      AlarmManager localAlarmManager = paramAlarmManager;
      if (paramAlarmManager == null) {
        localAlarmManager = (AlarmManager)paramContext.getSystemService("alarm");
      }
      paramAlarmManager = new Intent("android.intent.action.EVENT_REMINDER");
      paramAlarmManager.setData(ContentUris.withAppendedId(CalendarContract.CONTENT_URI, paramLong));
      paramAlarmManager.putExtra("alarmTime", paramLong);
      localAlarmManager.setExactAndAllowWhileIdle(0, paramLong, PendingIntent.getBroadcast(paramContext, 0, paramAlarmManager, 0));
    }
  }
  
  protected static abstract interface CalendarAlertsColumns
  {
    public static final String ALARM_TIME = "alarmTime";
    public static final String BEGIN = "begin";
    public static final String CREATION_TIME = "creationTime";
    public static final String DEFAULT_SORT_ORDER = "begin ASC,title ASC";
    public static final String END = "end";
    public static final String EVENT_ID = "event_id";
    public static final String MINUTES = "minutes";
    public static final String NOTIFY_TIME = "notifyTime";
    public static final String RECEIVED_TIME = "receivedTime";
    public static final String STATE = "state";
    public static final int STATE_DISMISSED = 2;
    public static final int STATE_FIRED = 1;
    public static final int STATE_SCHEDULED = 0;
  }
  
  public static final class CalendarCache
    implements CalendarContract.CalendarCacheColumns
  {
    public static final String KEY_TIMEZONE_INSTANCES = "timezoneInstances";
    public static final String KEY_TIMEZONE_INSTANCES_PREVIOUS = "timezoneInstancesPrevious";
    public static final String KEY_TIMEZONE_TYPE = "timezoneType";
    public static final String TIMEZONE_TYPE_AUTO = "auto";
    public static final String TIMEZONE_TYPE_HOME = "home";
    public static final Uri URI = Uri.parse("content://com.android.calendar/properties");
  }
  
  protected static abstract interface CalendarCacheColumns
  {
    public static final String KEY = "key";
    public static final String VALUE = "value";
  }
  
  protected static abstract interface CalendarColumns
  {
    public static final String ALLOWED_ATTENDEE_TYPES = "allowedAttendeeTypes";
    public static final String ALLOWED_AVAILABILITY = "allowedAvailability";
    public static final String ALLOWED_REMINDERS = "allowedReminders";
    public static final String CALENDAR_ACCESS_LEVEL = "calendar_access_level";
    public static final String CALENDAR_COLOR = "calendar_color";
    public static final String CALENDAR_COLOR_KEY = "calendar_color_index";
    public static final String CALENDAR_DISPLAY_NAME = "calendar_displayName";
    public static final String CALENDAR_TIME_ZONE = "calendar_timezone";
    public static final int CAL_ACCESS_CONTRIBUTOR = 500;
    public static final int CAL_ACCESS_EDITOR = 600;
    public static final int CAL_ACCESS_FREEBUSY = 100;
    public static final int CAL_ACCESS_NONE = 0;
    public static final int CAL_ACCESS_OVERRIDE = 400;
    public static final int CAL_ACCESS_OWNER = 700;
    public static final int CAL_ACCESS_READ = 200;
    public static final int CAL_ACCESS_RESPOND = 300;
    public static final int CAL_ACCESS_ROOT = 800;
    public static final String CAN_MODIFY_TIME_ZONE = "canModifyTimeZone";
    public static final String CAN_ORGANIZER_RESPOND = "canOrganizerRespond";
    public static final String IS_PRIMARY = "isPrimary";
    public static final String MAX_REMINDERS = "maxReminders";
    public static final String OWNER_ACCOUNT = "ownerAccount";
    public static final String SYNC_EVENTS = "sync_events";
    public static final String VISIBLE = "visible";
  }
  
  public static final class CalendarEntity
    implements BaseColumns, CalendarContract.SyncColumns, CalendarContract.CalendarColumns
  {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar/calendar_entities");
    
    public static EntityIterator newEntityIterator(Cursor paramCursor)
    {
      return new EntityIteratorImpl(paramCursor);
    }
    
    private static class EntityIteratorImpl
      extends CursorEntityIterator
    {
      public EntityIteratorImpl(Cursor paramCursor)
      {
        super();
      }
      
      public Entity getEntityAndIncrementCursor(Cursor paramCursor)
        throws RemoteException
      {
        long l = paramCursor.getLong(paramCursor.getColumnIndexOrThrow("_id"));
        Object localObject = new ContentValues();
        ((ContentValues)localObject).put("_id", Long.valueOf(l));
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "account_name");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "account_type");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "_sync_id");
        DatabaseUtils.cursorLongToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "dirty");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "mutators");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync1");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync2");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync3");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync4");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync5");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync6");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync7");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync8");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync9");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync10");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "name");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "calendar_displayName");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "calendar_color");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "calendar_color_index");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "calendar_access_level");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "visible");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "sync_events");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "calendar_location");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "calendar_timezone");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "ownerAccount");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "canOrganizerRespond");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "canModifyTimeZone");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "maxReminders");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "canPartiallyUpdate");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "allowedReminders");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "deleted");
        localObject = new Entity((ContentValues)localObject);
        paramCursor.moveToNext();
        return (Entity)localObject;
      }
    }
  }
  
  public static final class CalendarMetaData
    implements CalendarContract.CalendarMetaDataColumns, BaseColumns
  {}
  
  protected static abstract interface CalendarMetaDataColumns
  {
    public static final String LOCAL_TIMEZONE = "localTimezone";
    public static final String MAX_EVENTDAYS = "maxEventDays";
    public static final String MAX_INSTANCE = "maxInstance";
    public static final String MIN_EVENTDAYS = "minEventDays";
    public static final String MIN_INSTANCE = "minInstance";
  }
  
  protected static abstract interface CalendarSyncColumns
  {
    public static final String CAL_SYNC1 = "cal_sync1";
    public static final String CAL_SYNC10 = "cal_sync10";
    public static final String CAL_SYNC2 = "cal_sync2";
    public static final String CAL_SYNC3 = "cal_sync3";
    public static final String CAL_SYNC4 = "cal_sync4";
    public static final String CAL_SYNC5 = "cal_sync5";
    public static final String CAL_SYNC6 = "cal_sync6";
    public static final String CAL_SYNC7 = "cal_sync7";
    public static final String CAL_SYNC8 = "cal_sync8";
    public static final String CAL_SYNC9 = "cal_sync9";
  }
  
  public static final class Calendars
    implements BaseColumns, CalendarContract.SyncColumns, CalendarContract.CalendarColumns
  {
    public static final String CALENDAR_LOCATION = "calendar_location";
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar/calendars");
    public static final String DEFAULT_SORT_ORDER = "calendar_displayName";
    public static final String NAME = "name";
    public static final String[] SYNC_WRITABLE_COLUMNS = { "account_name", "account_type", "_sync_id", "dirty", "mutators", "ownerAccount", "maxReminders", "allowedReminders", "canModifyTimeZone", "canOrganizerRespond", "canPartiallyUpdate", "calendar_location", "calendar_timezone", "calendar_access_level", "deleted", "cal_sync1", "cal_sync2", "cal_sync3", "cal_sync4", "cal_sync5", "cal_sync6", "cal_sync7", "cal_sync8", "cal_sync9", "cal_sync10" };
  }
  
  public static final class Colors
    implements CalendarContract.ColorsColumns
  {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar/colors");
    public static final String TABLE_NAME = "Colors";
  }
  
  protected static abstract interface ColorsColumns
    extends SyncStateContract.Columns
  {
    public static final String COLOR = "color";
    public static final String COLOR_KEY = "color_index";
    public static final String COLOR_TYPE = "color_type";
    public static final int TYPE_CALENDAR = 0;
    public static final int TYPE_EVENT = 1;
  }
  
  public static final class EventDays
    implements CalendarContract.EventDaysColumns
  {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar/instances/groupbyday");
    private static final String SELECTION = "selected=1";
    
    public static final Cursor query(ContentResolver paramContentResolver, int paramInt1, int paramInt2, String[] paramArrayOfString)
    {
      SeempLog.record(54);
      if (paramInt2 < 1) {
        return null;
      }
      Uri.Builder localBuilder = CONTENT_URI.buildUpon();
      ContentUris.appendId(localBuilder, paramInt1);
      ContentUris.appendId(localBuilder, paramInt1 + paramInt2 - 1);
      return paramContentResolver.query(localBuilder.build(), paramArrayOfString, "selected=1", null, "startDay");
    }
  }
  
  protected static abstract interface EventDaysColumns
  {
    public static final String ENDDAY = "endDay";
    public static final String STARTDAY = "startDay";
  }
  
  public static final class Events
    implements BaseColumns, CalendarContract.SyncColumns, CalendarContract.EventsColumns, CalendarContract.CalendarColumns
  {
    public static final Uri CONTENT_EXCEPTION_URI = Uri.parse("content://com.android.calendar/exception");
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar/events");
    private static final String DEFAULT_SORT_ORDER = "";
    public static String[] PROVIDER_WRITABLE_COLUMNS = { "account_name", "account_type", "cal_sync1", "cal_sync2", "cal_sync3", "cal_sync4", "cal_sync5", "cal_sync6", "cal_sync7", "cal_sync8", "cal_sync9", "cal_sync10", "allowedReminders", "allowedAttendeeTypes", "allowedAvailability", "calendar_access_level", "calendar_color", "calendar_timezone", "canModifyTimeZone", "canOrganizerRespond", "calendar_displayName", "canPartiallyUpdate", "sync_events", "visible" };
    public static final String[] SYNC_WRITABLE_COLUMNS = { "_sync_id", "dirty", "mutators", "sync_data1", "sync_data2", "sync_data3", "sync_data4", "sync_data5", "sync_data6", "sync_data7", "sync_data8", "sync_data9", "sync_data10" };
  }
  
  protected static abstract interface EventsColumns
  {
    public static final int ACCESS_CONFIDENTIAL = 1;
    public static final int ACCESS_DEFAULT = 0;
    public static final String ACCESS_LEVEL = "accessLevel";
    public static final int ACCESS_PRIVATE = 2;
    public static final int ACCESS_PUBLIC = 3;
    public static final String ALL_DAY = "allDay";
    public static final String AVAILABILITY = "availability";
    public static final int AVAILABILITY_BUSY = 0;
    public static final int AVAILABILITY_FREE = 1;
    public static final int AVAILABILITY_TENTATIVE = 2;
    public static final String CALENDAR_ID = "calendar_id";
    public static final String CAN_INVITE_OTHERS = "canInviteOthers";
    public static final String CUSTOM_APP_PACKAGE = "customAppPackage";
    public static final String CUSTOM_APP_URI = "customAppUri";
    public static final String DESCRIPTION = "description";
    public static final String DISPLAY_COLOR = "displayColor";
    public static final String DTEND = "dtend";
    public static final String DTSTART = "dtstart";
    public static final String DURATION = "duration";
    public static final String EVENT_COLOR = "eventColor";
    public static final String EVENT_COLOR_KEY = "eventColor_index";
    public static final String EVENT_END_TIMEZONE = "eventEndTimezone";
    public static final String EVENT_LOCATION = "eventLocation";
    public static final String EVENT_TIMEZONE = "eventTimezone";
    public static final String EXDATE = "exdate";
    public static final String EXRULE = "exrule";
    public static final String GUESTS_CAN_INVITE_OTHERS = "guestsCanInviteOthers";
    public static final String GUESTS_CAN_MODIFY = "guestsCanModify";
    public static final String GUESTS_CAN_SEE_GUESTS = "guestsCanSeeGuests";
    public static final String HAS_ALARM = "hasAlarm";
    public static final String HAS_ATTENDEE_DATA = "hasAttendeeData";
    public static final String HAS_EXTENDED_PROPERTIES = "hasExtendedProperties";
    public static final String IS_ORGANIZER = "isOrganizer";
    public static final String LAST_DATE = "lastDate";
    public static final String LAST_SYNCED = "lastSynced";
    public static final String ORGANIZER = "organizer";
    public static final String ORIGINAL_ALL_DAY = "originalAllDay";
    public static final String ORIGINAL_ID = "original_id";
    public static final String ORIGINAL_INSTANCE_TIME = "originalInstanceTime";
    public static final String ORIGINAL_SYNC_ID = "original_sync_id";
    public static final String RDATE = "rdate";
    public static final String RRULE = "rrule";
    public static final String SELF_ATTENDEE_STATUS = "selfAttendeeStatus";
    public static final String STATUS = "eventStatus";
    public static final int STATUS_CANCELED = 2;
    public static final int STATUS_CONFIRMED = 1;
    public static final int STATUS_TENTATIVE = 0;
    public static final String SYNC_DATA1 = "sync_data1";
    public static final String SYNC_DATA10 = "sync_data10";
    public static final String SYNC_DATA2 = "sync_data2";
    public static final String SYNC_DATA3 = "sync_data3";
    public static final String SYNC_DATA4 = "sync_data4";
    public static final String SYNC_DATA5 = "sync_data5";
    public static final String SYNC_DATA6 = "sync_data6";
    public static final String SYNC_DATA7 = "sync_data7";
    public static final String SYNC_DATA8 = "sync_data8";
    public static final String SYNC_DATA9 = "sync_data9";
    public static final String TITLE = "title";
    public static final String UID_2445 = "uid2445";
  }
  
  public static final class EventsEntity
    implements BaseColumns, CalendarContract.SyncColumns, CalendarContract.EventsColumns
  {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar/event_entities");
    
    public static EntityIterator newEntityIterator(Cursor paramCursor, ContentProviderClient paramContentProviderClient)
    {
      return new EntityIteratorImpl(paramCursor, paramContentProviderClient);
    }
    
    public static EntityIterator newEntityIterator(Cursor paramCursor, ContentResolver paramContentResolver)
    {
      return new EntityIteratorImpl(paramCursor, paramContentResolver);
    }
    
    private static class EntityIteratorImpl
      extends CursorEntityIterator
    {
      private static final String[] ATTENDEES_PROJECTION = { "attendeeName", "attendeeEmail", "attendeeRelationship", "attendeeType", "attendeeStatus", "attendeeIdentity", "attendeeIdNamespace" };
      private static final int COLUMN_ATTENDEE_EMAIL = 1;
      private static final int COLUMN_ATTENDEE_IDENTITY = 5;
      private static final int COLUMN_ATTENDEE_ID_NAMESPACE = 6;
      private static final int COLUMN_ATTENDEE_NAME = 0;
      private static final int COLUMN_ATTENDEE_RELATIONSHIP = 2;
      private static final int COLUMN_ATTENDEE_STATUS = 4;
      private static final int COLUMN_ATTENDEE_TYPE = 3;
      private static final int COLUMN_ID = 0;
      private static final int COLUMN_METHOD = 1;
      private static final int COLUMN_MINUTES = 0;
      private static final int COLUMN_NAME = 1;
      private static final int COLUMN_VALUE = 2;
      private static final String[] EXTENDED_PROJECTION = { "_id", "name", "value" };
      private static final String[] REMINDERS_PROJECTION = { "minutes", "method" };
      private static final String WHERE_EVENT_ID = "event_id=?";
      private final ContentProviderClient mProvider;
      private final ContentResolver mResolver;
      
      public EntityIteratorImpl(Cursor paramCursor, ContentProviderClient paramContentProviderClient)
      {
        super();
        this.mResolver = null;
        this.mProvider = paramContentProviderClient;
      }
      
      public EntityIteratorImpl(Cursor paramCursor, ContentResolver paramContentResolver)
      {
        super();
        this.mResolver = paramContentResolver;
        this.mProvider = null;
      }
      
      public Entity getEntityAndIncrementCursor(Cursor paramCursor)
        throws RemoteException
      {
        long l = paramCursor.getLong(paramCursor.getColumnIndexOrThrow("_id"));
        Object localObject = new ContentValues();
        ((ContentValues)localObject).put("_id", Long.valueOf(l));
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "calendar_id");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "title");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "description");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "eventLocation");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "eventStatus");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "selfAttendeeStatus");
        DatabaseUtils.cursorLongToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "dtstart");
        DatabaseUtils.cursorLongToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "dtend");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "duration");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "eventTimezone");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "eventEndTimezone");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "allDay");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "accessLevel");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "availability");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "eventColor");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "eventColor_index");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "hasAlarm");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "hasExtendedProperties");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "rrule");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "rdate");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "exrule");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "exdate");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "original_sync_id");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "original_id");
        DatabaseUtils.cursorLongToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "originalInstanceTime");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "originalAllDay");
        DatabaseUtils.cursorLongToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "lastDate");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "hasAttendeeData");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "guestsCanInviteOthers");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "guestsCanModify");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "guestsCanSeeGuests");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "customAppPackage");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "customAppUri");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "uid2445");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "organizer");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "isOrganizer");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "_sync_id");
        DatabaseUtils.cursorLongToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "dirty");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "mutators");
        DatabaseUtils.cursorLongToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "lastSynced");
        DatabaseUtils.cursorIntToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "deleted");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "sync_data1");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "sync_data2");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "sync_data3");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "sync_data4");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "sync_data5");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "sync_data6");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "sync_data7");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "sync_data8");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "sync_data9");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "sync_data10");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync1");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync2");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync3");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync4");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync5");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync6");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync7");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync8");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync9");
        DatabaseUtils.cursorStringToContentValuesIfPresent(paramCursor, (ContentValues)localObject, "cal_sync10");
        Entity localEntity = new Entity((ContentValues)localObject);
        if (this.mResolver != null) {}
        ContentValues localContentValues;
        for (localObject = this.mResolver.query(CalendarContract.Reminders.CONTENT_URI, REMINDERS_PROJECTION, "event_id=?", new String[] { Long.toString(l) }, null);; localObject = this.mProvider.query(CalendarContract.Reminders.CONTENT_URI, REMINDERS_PROJECTION, "event_id=?", tmp664_661, null)) {
          try
          {
            while (((Cursor)localObject).moveToNext())
            {
              localContentValues = new ContentValues();
              localContentValues.put("minutes", Integer.valueOf(((Cursor)localObject).getInt(0)));
              localContentValues.put("method", Integer.valueOf(((Cursor)localObject).getInt(1)));
              localEntity.addSubValue(CalendarContract.Reminders.CONTENT_URI, localContentValues);
            }
            tmp664_661[0] = Long.toString(l);
          }
          finally
          {
            ((Cursor)localObject).close();
          }
        }
        ((Cursor)localObject).close();
        if (this.mResolver != null) {}
        for (localObject = this.mResolver.query(CalendarContract.Attendees.CONTENT_URI, ATTENDEES_PROJECTION, "event_id=?", new String[] { Long.toString(l) }, null);; localObject = this.mProvider.query(CalendarContract.Attendees.CONTENT_URI, ATTENDEES_PROJECTION, "event_id=?", tmp896_893, null)) {
          try
          {
            while (((Cursor)localObject).moveToNext())
            {
              localContentValues = new ContentValues();
              localContentValues.put("attendeeName", ((Cursor)localObject).getString(0));
              localContentValues.put("attendeeEmail", ((Cursor)localObject).getString(1));
              localContentValues.put("attendeeRelationship", Integer.valueOf(((Cursor)localObject).getInt(2)));
              localContentValues.put("attendeeType", Integer.valueOf(((Cursor)localObject).getInt(3)));
              localContentValues.put("attendeeStatus", Integer.valueOf(((Cursor)localObject).getInt(4)));
              localContentValues.put("attendeeIdentity", ((Cursor)localObject).getString(5));
              localContentValues.put("attendeeIdNamespace", ((Cursor)localObject).getString(6));
              localEntity.addSubValue(CalendarContract.Attendees.CONTENT_URI, localContentValues);
            }
            tmp896_893[0] = Long.toString(l);
          }
          finally
          {
            ((Cursor)localObject).close();
          }
        }
        ((Cursor)localObject).close();
        if (this.mResolver != null) {}
        for (localObject = this.mResolver.query(CalendarContract.ExtendedProperties.CONTENT_URI, EXTENDED_PROJECTION, "event_id=?", new String[] { Long.toString(l) }, null);; localObject = this.mProvider.query(CalendarContract.ExtendedProperties.CONTENT_URI, EXTENDED_PROJECTION, "event_id=?", tmp1058_1055, null)) {
          try
          {
            while (((Cursor)localObject).moveToNext())
            {
              localContentValues = new ContentValues();
              localContentValues.put("_id", ((Cursor)localObject).getString(0));
              localContentValues.put("name", ((Cursor)localObject).getString(1));
              localContentValues.put("value", ((Cursor)localObject).getString(2));
              localEntity.addSubValue(CalendarContract.ExtendedProperties.CONTENT_URI, localContentValues);
            }
            tmp1058_1055[0] = Long.toString(l);
          }
          finally
          {
            ((Cursor)localObject).close();
          }
        }
        ((Cursor)localObject).close();
        paramCursor.moveToNext();
        return localEntity;
      }
    }
  }
  
  public static final class EventsRawTimes
    implements BaseColumns, CalendarContract.EventsRawTimesColumns
  {}
  
  protected static abstract interface EventsRawTimesColumns
  {
    public static final String DTEND_2445 = "dtend2445";
    public static final String DTSTART_2445 = "dtstart2445";
    public static final String EVENT_ID = "event_id";
    public static final String LAST_DATE_2445 = "lastDate2445";
    public static final String ORIGINAL_INSTANCE_TIME_2445 = "originalInstanceTime2445";
  }
  
  public static final class ExtendedProperties
    implements BaseColumns, CalendarContract.ExtendedPropertiesColumns, CalendarContract.EventsColumns
  {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar/extendedproperties");
  }
  
  protected static abstract interface ExtendedPropertiesColumns
  {
    public static final String EVENT_ID = "event_id";
    public static final String NAME = "name";
    public static final String VALUE = "value";
  }
  
  public static final class Instances
    implements BaseColumns, CalendarContract.EventsColumns, CalendarContract.CalendarColumns
  {
    public static final String BEGIN = "begin";
    public static final Uri CONTENT_BY_DAY_URI;
    public static final Uri CONTENT_SEARCH_BY_DAY_URI = Uri.parse("content://com.android.calendar/instances/searchbyday");
    public static final Uri CONTENT_SEARCH_URI;
    public static final Uri CONTENT_URI;
    private static final String DEFAULT_SORT_ORDER = "begin ASC";
    public static final String END = "end";
    public static final String END_DAY = "endDay";
    public static final String END_MINUTE = "endMinute";
    public static final String EVENT_ID = "event_id";
    public static final String START_DAY = "startDay";
    public static final String START_MINUTE = "startMinute";
    private static final String[] WHERE_CALENDARS_ARGS = { "1" };
    private static final String WHERE_CALENDARS_SELECTED = "visible=?";
    
    static
    {
      CONTENT_URI = Uri.parse("content://com.android.calendar/instances/when");
      CONTENT_BY_DAY_URI = Uri.parse("content://com.android.calendar/instances/whenbyday");
      CONTENT_SEARCH_URI = Uri.parse("content://com.android.calendar/instances/search");
    }
    
    public static final Cursor query(ContentResolver paramContentResolver, String[] paramArrayOfString, long paramLong1, long paramLong2)
    {
      SeempLog.record(54);
      Uri.Builder localBuilder = CONTENT_URI.buildUpon();
      ContentUris.appendId(localBuilder, paramLong1);
      ContentUris.appendId(localBuilder, paramLong2);
      return paramContentResolver.query(localBuilder.build(), paramArrayOfString, "visible=?", WHERE_CALENDARS_ARGS, "begin ASC");
    }
    
    public static final Cursor query(ContentResolver paramContentResolver, String[] paramArrayOfString, long paramLong1, long paramLong2, String paramString)
    {
      SeempLog.record(54);
      Uri.Builder localBuilder = CONTENT_SEARCH_URI.buildUpon();
      ContentUris.appendId(localBuilder, paramLong1);
      ContentUris.appendId(localBuilder, paramLong2);
      return paramContentResolver.query(localBuilder.appendPath(paramString).build(), paramArrayOfString, "visible=?", WHERE_CALENDARS_ARGS, "begin ASC");
    }
  }
  
  public static final class Reminders
    implements BaseColumns, CalendarContract.RemindersColumns, CalendarContract.EventsColumns
  {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar/reminders");
    private static final String REMINDERS_WHERE = "event_id=?";
    
    public static final Cursor query(ContentResolver paramContentResolver, long paramLong, String[] paramArrayOfString)
    {
      SeempLog.record(54);
      String str = Long.toString(paramLong);
      return paramContentResolver.query(CONTENT_URI, paramArrayOfString, "event_id=?", new String[] { str }, null);
    }
  }
  
  protected static abstract interface RemindersColumns
  {
    public static final String EVENT_ID = "event_id";
    public static final String METHOD = "method";
    public static final int METHOD_ALARM = 4;
    public static final int METHOD_ALERT = 1;
    public static final int METHOD_DEFAULT = 0;
    public static final int METHOD_EMAIL = 2;
    public static final int METHOD_SMS = 3;
    public static final String MINUTES = "minutes";
    public static final int MINUTES_DEFAULT = -1;
  }
  
  protected static abstract interface SyncColumns
    extends CalendarContract.CalendarSyncColumns
  {
    public static final String ACCOUNT_NAME = "account_name";
    public static final String ACCOUNT_TYPE = "account_type";
    public static final String CAN_PARTIALLY_UPDATE = "canPartiallyUpdate";
    public static final String DELETED = "deleted";
    public static final String DIRTY = "dirty";
    public static final String MUTATORS = "mutators";
    public static final String _SYNC_ID = "_sync_id";
  }
  
  public static final class SyncState
    implements SyncStateContract.Columns
  {
    private static final String CONTENT_DIRECTORY = "syncstate";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CalendarContract.CONTENT_URI, "syncstate");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/CalendarContract.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */