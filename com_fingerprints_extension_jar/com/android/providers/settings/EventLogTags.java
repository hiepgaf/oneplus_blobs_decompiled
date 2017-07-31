package com.android.providers.settings;

import android.util.EventLog;

public class EventLogTags
{
  public static void writeUnsupportedSettingsQuery(String paramString1, String paramString2, String paramString3)
  {
    EventLog.writeEvent(52100, new Object[] { paramString1, paramString2, paramString3 });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/providers/settings/EventLogTags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */