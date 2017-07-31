package android.app.admin;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class DeviceAdminReceiver
  extends BroadcastReceiver
{
  public static final String ACTION_BUGREPORT_FAILED = "android.app.action.BUGREPORT_FAILED";
  public static final String ACTION_BUGREPORT_SHARE = "android.app.action.BUGREPORT_SHARE";
  public static final String ACTION_BUGREPORT_SHARING_DECLINED = "android.app.action.BUGREPORT_SHARING_DECLINED";
  public static final String ACTION_CHOOSE_PRIVATE_KEY_ALIAS = "android.app.action.CHOOSE_PRIVATE_KEY_ALIAS";
  public static final String ACTION_DEVICE_ADMIN_DISABLED = "android.app.action.DEVICE_ADMIN_DISABLED";
  public static final String ACTION_DEVICE_ADMIN_DISABLE_REQUESTED = "android.app.action.DEVICE_ADMIN_DISABLE_REQUESTED";
  public static final String ACTION_DEVICE_ADMIN_ENABLED = "android.app.action.DEVICE_ADMIN_ENABLED";
  public static final String ACTION_LOCK_TASK_ENTERING = "android.app.action.LOCK_TASK_ENTERING";
  public static final String ACTION_LOCK_TASK_EXITING = "android.app.action.LOCK_TASK_EXITING";
  public static final String ACTION_NOTIFY_PENDING_SYSTEM_UPDATE = "android.app.action.NOTIFY_PENDING_SYSTEM_UPDATE";
  public static final String ACTION_PASSWORD_CHANGED = "android.app.action.ACTION_PASSWORD_CHANGED";
  public static final String ACTION_PASSWORD_EXPIRING = "android.app.action.ACTION_PASSWORD_EXPIRING";
  public static final String ACTION_PASSWORD_FAILED = "android.app.action.ACTION_PASSWORD_FAILED";
  public static final String ACTION_PASSWORD_SUCCEEDED = "android.app.action.ACTION_PASSWORD_SUCCEEDED";
  public static final String ACTION_PROFILE_PROVISIONING_COMPLETE = "android.app.action.PROFILE_PROVISIONING_COMPLETE";
  public static final String ACTION_SECURITY_LOGS_AVAILABLE = "android.app.action.SECURITY_LOGS_AVAILABLE";
  public static final int BUGREPORT_FAILURE_FAILED_COMPLETING = 0;
  public static final int BUGREPORT_FAILURE_FILE_NO_LONGER_AVAILABLE = 1;
  public static final String DEVICE_ADMIN_META_DATA = "android.app.device_admin";
  public static final String EXTRA_BUGREPORT_FAILURE_REASON = "android.app.extra.BUGREPORT_FAILURE_REASON";
  public static final String EXTRA_BUGREPORT_HASH = "android.app.extra.BUGREPORT_HASH";
  public static final String EXTRA_CHOOSE_PRIVATE_KEY_ALIAS = "android.app.extra.CHOOSE_PRIVATE_KEY_ALIAS";
  public static final String EXTRA_CHOOSE_PRIVATE_KEY_RESPONSE = "android.app.extra.CHOOSE_PRIVATE_KEY_RESPONSE";
  public static final String EXTRA_CHOOSE_PRIVATE_KEY_SENDER_UID = "android.app.extra.CHOOSE_PRIVATE_KEY_SENDER_UID";
  public static final String EXTRA_CHOOSE_PRIVATE_KEY_URI = "android.app.extra.CHOOSE_PRIVATE_KEY_URI";
  public static final String EXTRA_DISABLE_WARNING = "android.app.extra.DISABLE_WARNING";
  public static final String EXTRA_LOCK_TASK_PACKAGE = "android.app.extra.LOCK_TASK_PACKAGE";
  public static final String EXTRA_SYSTEM_UPDATE_RECEIVED_TIME = "android.app.extra.SYSTEM_UPDATE_RECEIVED_TIME";
  private static String TAG = "DevicePolicy";
  private static boolean localLOGV = false;
  private DevicePolicyManager mManager;
  private ComponentName mWho;
  
  public DevicePolicyManager getManager(Context paramContext)
  {
    if (this.mManager != null) {
      return this.mManager;
    }
    this.mManager = ((DevicePolicyManager)paramContext.getSystemService("device_policy"));
    return this.mManager;
  }
  
  public ComponentName getWho(Context paramContext)
  {
    if (this.mWho != null) {
      return this.mWho;
    }
    this.mWho = new ComponentName(paramContext, getClass());
    return this.mWho;
  }
  
  public void onBugreportFailed(Context paramContext, Intent paramIntent, int paramInt) {}
  
  public void onBugreportShared(Context paramContext, Intent paramIntent, String paramString) {}
  
  public void onBugreportSharingDeclined(Context paramContext, Intent paramIntent) {}
  
  public String onChoosePrivateKeyAlias(Context paramContext, Intent paramIntent, int paramInt, Uri paramUri, String paramString)
  {
    return null;
  }
  
  public CharSequence onDisableRequested(Context paramContext, Intent paramIntent)
  {
    return null;
  }
  
  public void onDisabled(Context paramContext, Intent paramIntent) {}
  
  public void onEnabled(Context paramContext, Intent paramIntent) {}
  
  public void onLockTaskModeEntering(Context paramContext, Intent paramIntent, String paramString) {}
  
  public void onLockTaskModeExiting(Context paramContext, Intent paramIntent) {}
  
  public void onPasswordChanged(Context paramContext, Intent paramIntent) {}
  
  public void onPasswordExpiring(Context paramContext, Intent paramIntent) {}
  
  public void onPasswordFailed(Context paramContext, Intent paramIntent) {}
  
  public void onPasswordSucceeded(Context paramContext, Intent paramIntent) {}
  
  public void onProfileProvisioningComplete(Context paramContext, Intent paramIntent) {}
  
  @Deprecated
  public void onReadyForUserInitialization(Context paramContext, Intent paramIntent) {}
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    String str = paramIntent.getAction();
    if ("android.app.action.ACTION_PASSWORD_CHANGED".equals(str)) {
      onPasswordChanged(paramContext, paramIntent);
    }
    do
    {
      do
      {
        return;
        if ("android.app.action.ACTION_PASSWORD_FAILED".equals(str))
        {
          onPasswordFailed(paramContext, paramIntent);
          return;
        }
        if ("android.app.action.ACTION_PASSWORD_SUCCEEDED".equals(str))
        {
          onPasswordSucceeded(paramContext, paramIntent);
          return;
        }
        if ("android.app.action.DEVICE_ADMIN_ENABLED".equals(str))
        {
          onEnabled(paramContext, paramIntent);
          return;
        }
        if (!"android.app.action.DEVICE_ADMIN_DISABLE_REQUESTED".equals(str)) {
          break;
        }
        paramContext = onDisableRequested(paramContext, paramIntent);
      } while (paramContext == null);
      getResultExtras(true).putCharSequence("android.app.extra.DISABLE_WARNING", paramContext);
      return;
      if ("android.app.action.DEVICE_ADMIN_DISABLED".equals(str))
      {
        onDisabled(paramContext, paramIntent);
        return;
      }
      if ("android.app.action.ACTION_PASSWORD_EXPIRING".equals(str))
      {
        onPasswordExpiring(paramContext, paramIntent);
        return;
      }
      if ("android.app.action.PROFILE_PROVISIONING_COMPLETE".equals(str))
      {
        onProfileProvisioningComplete(paramContext, paramIntent);
        return;
      }
      if ("android.app.action.CHOOSE_PRIVATE_KEY_ALIAS".equals(str))
      {
        setResultData(onChoosePrivateKeyAlias(paramContext, paramIntent, paramIntent.getIntExtra("android.app.extra.CHOOSE_PRIVATE_KEY_SENDER_UID", -1), (Uri)paramIntent.getParcelableExtra("android.app.extra.CHOOSE_PRIVATE_KEY_URI"), paramIntent.getStringExtra("android.app.extra.CHOOSE_PRIVATE_KEY_ALIAS")));
        return;
      }
      if ("android.app.action.LOCK_TASK_ENTERING".equals(str))
      {
        onLockTaskModeEntering(paramContext, paramIntent, paramIntent.getStringExtra("android.app.extra.LOCK_TASK_PACKAGE"));
        return;
      }
      if ("android.app.action.LOCK_TASK_EXITING".equals(str))
      {
        onLockTaskModeExiting(paramContext, paramIntent);
        return;
      }
      if ("android.app.action.NOTIFY_PENDING_SYSTEM_UPDATE".equals(str))
      {
        onSystemUpdatePending(paramContext, paramIntent, paramIntent.getLongExtra("android.app.extra.SYSTEM_UPDATE_RECEIVED_TIME", -1L));
        return;
      }
      if ("android.app.action.BUGREPORT_SHARING_DECLINED".equals(str))
      {
        onBugreportSharingDeclined(paramContext, paramIntent);
        return;
      }
      if ("android.app.action.BUGREPORT_SHARE".equals(str))
      {
        onBugreportShared(paramContext, paramIntent, paramIntent.getStringExtra("android.app.extra.BUGREPORT_HASH"));
        return;
      }
      if ("android.app.action.BUGREPORT_FAILED".equals(str))
      {
        onBugreportFailed(paramContext, paramIntent, paramIntent.getIntExtra("android.app.extra.BUGREPORT_FAILURE_REASON", 0));
        return;
      }
    } while (!"android.app.action.SECURITY_LOGS_AVAILABLE".equals(str));
    onSecurityLogsAvailable(paramContext, paramIntent);
  }
  
  public void onSecurityLogsAvailable(Context paramContext, Intent paramIntent) {}
  
  public void onSystemUpdatePending(Context paramContext, Intent paramIntent, long paramLong) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/admin/DeviceAdminReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */