package android.app.admin;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemProperties;
import android.util.EventLog.Event;
import java.io.IOException;
import java.util.Collection;

public class SecurityLog
{
  private static final String PROPERTY_LOGGING_ENABLED = "persist.logd.security";
  public static final int TAG_ADB_SHELL_CMD = 210002;
  public static final int TAG_ADB_SHELL_INTERACTIVE = 210001;
  public static final int TAG_APP_PROCESS_START = 210005;
  public static final int TAG_KEYGUARD_DISMISSED = 210006;
  public static final int TAG_KEYGUARD_DISMISS_AUTH_ATTEMPT = 210007;
  public static final int TAG_KEYGUARD_SECURED = 210008;
  public static final int TAG_SYNC_RECV_FILE = 210003;
  public static final int TAG_SYNC_SEND_FILE = 210004;
  
  public static boolean getLoggingEnabledProperty()
  {
    return SystemProperties.getBoolean("persist.logd.security", false);
  }
  
  public static native boolean isLoggingEnabled();
  
  public static native void readEvents(Collection<SecurityEvent> paramCollection)
    throws IOException;
  
  public static native void readEventsOnWrapping(long paramLong, Collection<SecurityEvent> paramCollection)
    throws IOException;
  
  public static native void readEventsSince(long paramLong, Collection<SecurityEvent> paramCollection)
    throws IOException;
  
  public static native void readPreviousEvents(Collection<SecurityEvent> paramCollection)
    throws IOException;
  
  public static void setLoggingEnabledProperty(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (String str = "true";; str = "false")
    {
      SystemProperties.set("persist.logd.security", str);
      return;
    }
  }
  
  public static native int writeEvent(int paramInt, String paramString);
  
  public static native int writeEvent(int paramInt, Object... paramVarArgs);
  
  public static final class SecurityEvent
    implements Parcelable
  {
    public static final Parcelable.Creator<SecurityEvent> CREATOR = new Parcelable.Creator()
    {
      public SecurityLog.SecurityEvent createFromParcel(Parcel paramAnonymousParcel)
      {
        return new SecurityLog.SecurityEvent(paramAnonymousParcel.createByteArray());
      }
      
      public SecurityLog.SecurityEvent[] newArray(int paramAnonymousInt)
      {
        return new SecurityLog.SecurityEvent[paramAnonymousInt];
      }
    };
    private EventLog.Event mEvent;
    
    SecurityEvent(byte[] paramArrayOfByte)
    {
      this.mEvent = EventLog.Event.fromBytes(paramArrayOfByte);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public Object getData()
    {
      return this.mEvent.getData();
    }
    
    public int getTag()
    {
      return this.mEvent.getTag();
    }
    
    public long getTimeNanos()
    {
      return this.mEvent.getTimeNanos();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeByteArray(this.mEvent.getBytes());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/admin/SecurityLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */