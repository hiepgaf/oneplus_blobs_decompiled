package com.android.server.notification;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Handler;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.service.notification.ZenModeConfig.EventInfo;
import android.util.ArraySet;
import android.util.Log;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Objects;

public class CalendarTracker
{
  private static final String[] ATTENDEE_PROJECTION = { "event_id", "attendeeEmail", "attendeeStatus" };
  private static final String ATTENDEE_SELECTION = "event_id = ? AND attendeeEmail = ?";
  private static final boolean DEBUG = Log.isLoggable("ConditionProviders", 3);
  private static final boolean DEBUG_ATTENDEES = false;
  private static final int EVENT_CHECK_LOOKAHEAD = 86400000;
  private static final String INSTANCE_ORDER_BY = "begin ASC";
  private static final String[] INSTANCE_PROJECTION = { "begin", "end", "title", "visible", "event_id", "calendar_displayName", "ownerAccount", "calendar_id", "availability" };
  private static final String TAG = "ConditionProviders.CT";
  private Callback mCallback;
  private final ContentObserver mObserver = new ContentObserver(null)
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      if (CalendarTracker.-get0()) {
        Log.d("ConditionProviders.CT", "onChange selfChange=" + paramAnonymousBoolean);
      }
    }
    
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      if (CalendarTracker.-get0()) {
        Log.d("ConditionProviders.CT", "onChange selfChange=" + paramAnonymousBoolean + " uri=" + paramAnonymousUri + " u=" + CalendarTracker.-get2(CalendarTracker.this).getUserId());
      }
      CalendarTracker.-get1(CalendarTracker.this).onChanged();
    }
  };
  private boolean mRegistered;
  private final Context mSystemContext;
  private final Context mUserContext;
  
  public CalendarTracker(Context paramContext1, Context paramContext2)
  {
    this.mSystemContext = paramContext1;
    this.mUserContext = paramContext2;
  }
  
  private static String attendeeStatusToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "ATTENDEE_STATUS_UNKNOWN_" + paramInt;
    case 0: 
      return "ATTENDEE_STATUS_NONE";
    case 1: 
      return "ATTENDEE_STATUS_ACCEPTED";
    case 2: 
      return "ATTENDEE_STATUS_DECLINED";
    case 3: 
      return "ATTENDEE_STATUS_INVITED";
    }
    return "ATTENDEE_STATUS_TENTATIVE";
  }
  
  private static String availabilityToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "AVAILABILITY_UNKNOWN_" + paramInt;
    case 0: 
      return "AVAILABILITY_BUSY";
    case 1: 
      return "AVAILABILITY_FREE";
    }
    return "AVAILABILITY_TENTATIVE";
  }
  
  private ArraySet<Long> getPrimaryCalendars()
  {
    long l = System.currentTimeMillis();
    ArraySet localArraySet = new ArraySet();
    Object localObject1 = null;
    try
    {
      Cursor localCursor = this.mUserContext.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, new String[] { "_id", "(account_name=ownerAccount) AS \"primary\"" }, "\"primary\" = 1", null, null);
      while (localCursor != null)
      {
        localObject1 = localCursor;
        if (!localCursor.moveToNext()) {
          break;
        }
        localObject1 = localCursor;
        localArraySet.add(Long.valueOf(localCursor.getLong(0)));
      }
      if (localObject2 == null) {
        break label116;
      }
    }
    finally
    {
      if (localObject1 != null) {
        ((Cursor)localObject1).close();
      }
    }
    ((Cursor)localObject2).close();
    label116:
    if (DEBUG) {
      Log.d("ConditionProviders.CT", "getPrimaryCalendars took " + (System.currentTimeMillis() - l));
    }
    return localArraySet;
  }
  
  private boolean meetsAttendee(ZenModeConfig.EventInfo paramEventInfo, int paramInt, String paramString)
  {
    long l1 = System.currentTimeMillis();
    Object localObject = Integer.toString(paramInt);
    localObject = this.mUserContext.getContentResolver().query(CalendarContract.Attendees.CONTENT_URI, ATTENDEE_PROJECTION, "event_id = ? AND attendeeEmail = ?", new String[] { localObject, paramString }, null);
    try
    {
      if (((Cursor)localObject).getCount() == 0)
      {
        if (DEBUG) {
          Log.d("ConditionProviders.CT", "No attendees found");
        }
        return true;
      }
      boolean bool1 = false;
      if (((Cursor)localObject).moveToNext())
      {
        long l2 = ((Cursor)localObject).getLong(0);
        String str = ((Cursor)localObject).getString(1);
        int i = ((Cursor)localObject).getInt(2);
        boolean bool2 = meetsReply(paramEventInfo.reply, i);
        if (DEBUG) {
          Log.d("ConditionProviders.CT", "" + String.format("status=%s, meetsReply=%s", new Object[] { attendeeStatusToString(i), Boolean.valueOf(bool2) }));
        }
        if (l2 == paramInt)
        {
          boolean bool3 = Objects.equals(str, paramString);
          if (!bool3) {}
        }
        for (;;)
        {
          bool1 |= bool2;
          break;
          bool2 = false;
        }
      }
      return bool1;
    }
    finally
    {
      ((Cursor)localObject).close();
      if (DEBUG) {
        Log.d("ConditionProviders.CT", "meetsAttendee took " + (System.currentTimeMillis() - l1));
      }
    }
  }
  
  private static boolean meetsReply(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      return false;
    case 2: 
      return paramInt2 == 1;
    case 1: 
      return (paramInt2 == 1) || (paramInt2 == 4);
    }
    return paramInt2 != 2;
  }
  
  private void setRegistered(boolean paramBoolean)
  {
    if (this.mRegistered == paramBoolean) {
      return;
    }
    ContentResolver localContentResolver = this.mSystemContext.getContentResolver();
    int i = this.mUserContext.getUserId();
    if (this.mRegistered)
    {
      if (DEBUG) {
        Log.d("ConditionProviders.CT", "unregister content observer u=" + i);
      }
      localContentResolver.unregisterContentObserver(this.mObserver);
    }
    this.mRegistered = paramBoolean;
    if (DEBUG) {
      Log.d("ConditionProviders.CT", "mRegistered = " + paramBoolean + " u=" + i);
    }
    if (this.mRegistered)
    {
      if (DEBUG) {
        Log.d("ConditionProviders.CT", "register content observer u=" + i);
      }
      localContentResolver.registerContentObserver(CalendarContract.Instances.CONTENT_URI, true, this.mObserver, i);
      localContentResolver.registerContentObserver(CalendarContract.Events.CONTENT_URI, true, this.mObserver, i);
      localContentResolver.registerContentObserver(CalendarContract.Calendars.CONTENT_URI, true, this.mObserver, i);
    }
  }
  
  public CheckEventResult checkEvent(ZenModeConfig.EventInfo paramEventInfo, long paramLong)
  {
    Object localObject = CalendarContract.Instances.CONTENT_URI.buildUpon();
    ContentUris.appendId((Uri.Builder)localObject, paramLong);
    ContentUris.appendId((Uri.Builder)localObject, 86400000L + paramLong);
    localObject = ((Uri.Builder)localObject).build();
    localObject = this.mUserContext.getContentResolver().query((Uri)localObject, INSTANCE_PROJECTION, null, null, "begin ASC");
    CheckEventResult localCheckEventResult = new CheckEventResult();
    localCheckEventResult.recheckAt = (86400000L + paramLong);
    label313:
    label356:
    label477:
    label483:
    label535:
    label550:
    label570:
    label580:
    for (;;)
    {
      long l1;
      long l2;
      boolean bool1;
      int i;
      try
      {
        ArraySet localArraySet = getPrimaryCalendars();
        if ((localObject == null) || (!((Cursor)localObject).moveToNext())) {
          break label535;
        }
        l1 = ((Cursor)localObject).getLong(0);
        l2 = ((Cursor)localObject).getLong(1);
        String str1 = ((Cursor)localObject).getString(2);
        if (((Cursor)localObject).getInt(3) != 1) {
          break label477;
        }
        bool1 = true;
        k = ((Cursor)localObject).getInt(4);
        str2 = ((Cursor)localObject).getString(5);
        str3 = ((Cursor)localObject).getString(6);
        long l3 = ((Cursor)localObject).getLong(7);
        j = ((Cursor)localObject).getInt(8);
        bool2 = localArraySet.contains(Long.valueOf(l3));
        if (!DEBUG) {
          break label550;
        }
        Log.d("ConditionProviders.CT", String.format("%s %s-%s v=%s a=%s eid=%s n=%s o=%s cid=%s p=%s", new Object[] { str1, new Date(l1), new Date(l2), Boolean.valueOf(bool1), availabilityToString(j), Integer.valueOf(k), str2, str3, Long.valueOf(l3), Boolean.valueOf(bool2) }));
      }
      finally
      {
        int k;
        String str2;
        String str3;
        boolean bool2;
        if (localObject == null) {
          continue;
        }
        ((Cursor)localObject).close();
      }
      if ((bool1) && (bool2))
      {
        if ((paramEventInfo.calendar != null) && (!Objects.equals(paramEventInfo.calendar, str3)))
        {
          bool1 = Objects.equals(paramEventInfo.calendar, str2);
          break label570;
          if ((!bool1) || (j == 0)) {
            break label580;
          }
          if (DEBUG) {
            Log.d("ConditionProviders.CT", "  MEETS CALENDAR & AVAILABILITY");
          }
          if (!meetsAttendee(paramEventInfo, k, str3)) {
            continue;
          }
          if (DEBUG) {
            Log.d("ConditionProviders.CT", "    MEETS ATTENDEE");
          }
          if (i != 0)
          {
            if (DEBUG) {
              Log.d("ConditionProviders.CT", "      MEETS TIME");
            }
            localCheckEventResult.inEvent = true;
          }
          if ((l1 > paramLong) && (l1 < localCheckEventResult.recheckAt))
          {
            localCheckEventResult.recheckAt = l1;
            continue;
            bool1 = false;
            continue;
            i = 0;
            continue;
          }
        }
        else
        {
          bool1 = true;
          break label570;
        }
      }
      else {
        bool1 = false;
      }
      while (j == 1)
      {
        j = 0;
        break label356;
        if ((l2 <= paramLong) || (l2 >= localCheckEventResult.recheckAt)) {
          break;
        }
        localCheckEventResult.recheckAt = l2;
        break;
        if (localObject != null) {
          ((Cursor)localObject).close();
        }
        return localCheckEventResult;
        if ((paramLong < l1) || (paramLong >= l2)) {
          break label483;
        }
        i = 1;
        break label313;
      }
      int j = 1;
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mCallback=");
    paramPrintWriter.println(this.mCallback);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mRegistered=");
    paramPrintWriter.println(this.mRegistered);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("u=");
    paramPrintWriter.println(this.mUserContext.getUserId());
  }
  
  public void setCallback(Callback paramCallback)
  {
    if (this.mCallback == paramCallback) {
      return;
    }
    this.mCallback = paramCallback;
    if (this.mCallback != null) {}
    for (boolean bool = true;; bool = false)
    {
      setRegistered(bool);
      return;
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onChanged();
  }
  
  public static class CheckEventResult
  {
    public boolean inEvent;
    public long recheckAt;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/CalendarTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */