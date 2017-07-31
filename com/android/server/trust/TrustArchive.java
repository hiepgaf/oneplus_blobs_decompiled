package com.android.server.trust;

import android.content.ComponentName;
import android.os.SystemClock;
import android.util.TimeUtils;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Iterator;

public class TrustArchive
{
  private static final int HISTORY_LIMIT = 200;
  private static final int TYPE_AGENT_CONNECTED = 4;
  private static final int TYPE_AGENT_DIED = 3;
  private static final int TYPE_AGENT_STOPPED = 5;
  private static final int TYPE_GRANT_TRUST = 0;
  private static final int TYPE_MANAGING_TRUST = 6;
  private static final int TYPE_POLICY_CHANGED = 7;
  private static final int TYPE_REVOKE_TRUST = 1;
  private static final int TYPE_TRUST_TIMEOUT = 2;
  ArrayDeque<Event> mEvents = new ArrayDeque();
  
  private void addEvent(Event paramEvent)
  {
    if (this.mEvents.size() >= 200) {
      this.mEvents.removeFirst();
    }
    this.mEvents.addLast(paramEvent);
  }
  
  private String dumpGrantFlags(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((paramInt & 0x1) != 0)
    {
      if (localStringBuilder.length() != 0) {
        localStringBuilder.append('|');
      }
      localStringBuilder.append("INITIATED_BY_USER");
    }
    if ((paramInt & 0x2) != 0)
    {
      if (localStringBuilder.length() != 0) {
        localStringBuilder.append('|');
      }
      localStringBuilder.append("DISMISS_KEYGUARD");
    }
    if (localStringBuilder.length() == 0) {
      localStringBuilder.append('0');
    }
    return localStringBuilder.toString();
  }
  
  private String dumpType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "Unknown(" + paramInt + ")";
    case 0: 
      return "GrantTrust";
    case 1: 
      return "RevokeTrust";
    case 2: 
      return "TrustTimeout";
    case 3: 
      return "AgentDied";
    case 4: 
      return "AgentConnected";
    case 5: 
      return "AgentStopped";
    case 6: 
      return "ManagingTrust";
    }
    return "DevicePolicyChanged";
  }
  
  public static String formatDuration(long paramLong)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    TimeUtils.formatDuration(paramLong, localStringBuilder);
    return localStringBuilder.toString();
  }
  
  private static String formatElapsed(long paramLong)
  {
    return TimeUtils.logTimeOfDay(paramLong - SystemClock.elapsedRealtime() + System.currentTimeMillis());
  }
  
  static String getSimpleName(ComponentName paramComponentName)
  {
    paramComponentName = paramComponentName.getClassName();
    int i = paramComponentName.lastIndexOf('.');
    if ((i < paramComponentName.length()) && (i >= 0)) {
      return paramComponentName.substring(i + 1);
    }
    return paramComponentName;
  }
  
  public void dump(PrintWriter paramPrintWriter, int paramInt1, int paramInt2, String paramString, boolean paramBoolean)
  {
    int i = 0;
    Iterator localIterator = this.mEvents.descendingIterator();
    while ((localIterator.hasNext()) && (i < paramInt1))
    {
      Event localEvent = (Event)localIterator.next();
      if ((paramInt2 == -1) || (paramInt2 == localEvent.userId) || (localEvent.userId == -1))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.printf("#%-2d %s %s: ", new Object[] { Integer.valueOf(i), formatElapsed(localEvent.elapsedTimestamp), dumpType(localEvent.type) });
        if (paramInt2 == -1)
        {
          paramPrintWriter.print("user=");
          paramPrintWriter.print(localEvent.userId);
          paramPrintWriter.print(", ");
        }
        if (localEvent.agent != null)
        {
          paramPrintWriter.print("agent=");
          if (paramBoolean) {
            paramPrintWriter.print(localEvent.agent.flattenToShortString());
          }
        }
        else
        {
          label168:
          switch (localEvent.type)
          {
          }
        }
        for (;;)
        {
          paramPrintWriter.println();
          i += 1;
          break;
          paramPrintWriter.print(getSimpleName(localEvent.agent));
          break label168;
          paramPrintWriter.printf(", message=\"%s\", duration=%s, flags=%s", new Object[] { localEvent.message, formatDuration(localEvent.duration), dumpGrantFlags(localEvent.flags) });
          continue;
          paramPrintWriter.printf(", managingTrust=" + localEvent.managingTrust, new Object[0]);
        }
      }
    }
  }
  
  public void logAgentConnected(int paramInt, ComponentName paramComponentName)
  {
    addEvent(new Event(4, paramInt, paramComponentName, null, 0L, 0, false, null));
  }
  
  public void logAgentDied(int paramInt, ComponentName paramComponentName)
  {
    addEvent(new Event(3, paramInt, paramComponentName, null, 0L, 0, false, null));
  }
  
  public void logAgentStopped(int paramInt, ComponentName paramComponentName)
  {
    addEvent(new Event(5, paramInt, paramComponentName, null, 0L, 0, false, null));
  }
  
  public void logDevicePolicyChanged()
  {
    addEvent(new Event(7, -1, null, null, 0L, 0, false, null));
  }
  
  public void logGrantTrust(int paramInt1, ComponentName paramComponentName, String paramString, long paramLong, int paramInt2)
  {
    addEvent(new Event(0, paramInt1, paramComponentName, paramString, paramLong, paramInt2, false, null));
  }
  
  public void logManagingTrust(int paramInt, ComponentName paramComponentName, boolean paramBoolean)
  {
    addEvent(new Event(6, paramInt, paramComponentName, null, 0L, 0, paramBoolean, null));
  }
  
  public void logRevokeTrust(int paramInt, ComponentName paramComponentName)
  {
    addEvent(new Event(1, paramInt, paramComponentName, null, 0L, 0, false, null));
  }
  
  public void logTrustTimeout(int paramInt, ComponentName paramComponentName)
  {
    addEvent(new Event(2, paramInt, paramComponentName, null, 0L, 0, false, null));
  }
  
  private static class Event
  {
    final ComponentName agent;
    final long duration;
    final long elapsedTimestamp;
    final int flags;
    final boolean managingTrust;
    final String message;
    final int type;
    final int userId;
    
    private Event(int paramInt1, int paramInt2, ComponentName paramComponentName, String paramString, long paramLong, int paramInt3, boolean paramBoolean)
    {
      this.type = paramInt1;
      this.userId = paramInt2;
      this.agent = paramComponentName;
      this.elapsedTimestamp = SystemClock.elapsedRealtime();
      this.message = paramString;
      this.duration = paramLong;
      this.flags = paramInt3;
      this.managingTrust = paramBoolean;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/trust/TrustArchive.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */