package android.app.admin;

import android.util.EventLog;

public class SecurityLogTags
{
  public static final int SECURITY_ADB_SHELL_COMMAND = 210002;
  public static final int SECURITY_ADB_SHELL_INTERACTIVE = 210001;
  public static final int SECURITY_ADB_SYNC_RECV = 210003;
  public static final int SECURITY_ADB_SYNC_SEND = 210004;
  public static final int SECURITY_APP_PROCESS_START = 210005;
  public static final int SECURITY_KEYGUARD_DISMISSED = 210006;
  public static final int SECURITY_KEYGUARD_DISMISS_AUTH_ATTEMPT = 210007;
  public static final int SECURITY_KEYGUARD_SECURED = 210008;
  
  public static void writeSecurityAdbShellCommand(String paramString)
  {
    EventLog.writeEvent(210002, paramString);
  }
  
  public static void writeSecurityAdbShellInteractive()
  {
    EventLog.writeEvent(210001, new Object[0]);
  }
  
  public static void writeSecurityAdbSyncRecv(String paramString)
  {
    EventLog.writeEvent(210003, paramString);
  }
  
  public static void writeSecurityAdbSyncSend(String paramString)
  {
    EventLog.writeEvent(210004, paramString);
  }
  
  public static void writeSecurityAppProcessStart(String paramString1, long paramLong, int paramInt1, int paramInt2, String paramString2, String paramString3)
  {
    EventLog.writeEvent(210005, new Object[] { paramString1, Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString2, paramString3 });
  }
  
  public static void writeSecurityKeyguardDismissAuthAttempt(int paramInt1, int paramInt2)
  {
    EventLog.writeEvent(210007, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public static void writeSecurityKeyguardDismissed()
  {
    EventLog.writeEvent(210006, new Object[0]);
  }
  
  public static void writeSecurityKeyguardSecured()
  {
    EventLog.writeEvent(210008, new Object[0]);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/admin/SecurityLogTags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */