package com.android.server.pm;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.service.persistentdata.PersistentDataBlockManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import com.google.android.collect.Sets;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class UserRestrictionsUtils
{
  private static final Set<String> DEVICE_OWNER_ONLY_RESTRICTIONS;
  private static final Set<String> GLOBAL_RESTRICTIONS = Sets.newArraySet(new String[] { "no_adjust_volume", "no_run_in_background", "no_unmute_microphone", "disallow_unmute_device" });
  private static final Set<String> IMMUTABLE_BY_OWNERS;
  private static final Set<String> NON_PERSIST_USER_RESTRICTIONS;
  private static final String TAG = "UserRestrictionsUtils";
  public static final Set<String> USER_RESTRICTIONS = newSetWithUniqueCheck(new String[] { "no_config_wifi", "no_modify_accounts", "no_install_apps", "no_uninstall_apps", "no_share_location", "no_install_unknown_sources", "no_config_bluetooth", "no_usb_file_transfer", "no_config_credentials", "no_remove_user", "no_debugging_features", "no_config_vpn", "no_config_tethering", "no_network_reset", "no_factory_reset", "no_add_user", "ensure_verify_apps", "no_config_cell_broadcasts", "no_config_mobile_networks", "no_control_apps", "no_physical_media", "no_unmute_microphone", "no_adjust_volume", "no_outgoing_calls", "no_sms", "no_fun", "no_create_windows", "no_cross_profile_copy_paste", "no_outgoing_beam", "no_wallpaper", "no_safe_boot", "allow_parent_profile_app_linking", "no_record_audio", "no_camera", "no_run_in_background", "no_data_roaming", "no_set_user_icon", "no_set_wallpaper", "no_oem_unlock", "disallow_unmute_device" });
  
  static
  {
    NON_PERSIST_USER_RESTRICTIONS = Sets.newArraySet(new String[] { "no_record_audio" });
    DEVICE_OWNER_ONLY_RESTRICTIONS = Sets.newArraySet(new String[] { "no_usb_file_transfer", "no_config_tethering", "no_network_reset", "no_factory_reset", "no_add_user", "no_config_cell_broadcasts", "no_config_mobile_networks", "no_physical_media", "no_sms", "no_fun", "no_safe_boot", "no_create_windows", "no_data_roaming" });
    IMMUTABLE_BY_OWNERS = Sets.newArraySet(new String[] { "no_record_audio", "no_wallpaper", "no_oem_unlock" });
  }
  
  private static void applyUserRestriction(Context paramContext, int paramInt, String paramString, boolean paramBoolean)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      try
      {
        if (paramString.equals("no_config_wifi"))
        {
          if (paramBoolean) {
            Settings.Secure.putIntForUser(localContentResolver, "wifi_networks_available_notification_on", 0, paramInt);
          }
          return;
        }
        if (paramString.equals("no_data_roaming"))
        {
          if (!paramBoolean) {
            continue;
          }
          paramContext = new SubscriptionManager(paramContext).getActiveSubscriptionInfoList();
          if (paramContext == null) {
            break label403;
          }
          paramContext = paramContext.iterator();
          if (!paramContext.hasNext()) {
            break label403;
          }
          paramString = (SubscriptionInfo)paramContext.next();
          Settings.Global.putStringForUser(localContentResolver, "data_roaming" + paramString.getSubscriptionId(), "0", paramInt);
          continue;
        }
        if (!paramString.equals("no_share_location")) {
          break label163;
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      if (paramBoolean)
      {
        Settings.Secure.putIntForUser(localContentResolver, "location_mode", 0, paramInt);
        continue;
        label163:
        if (paramString.equals("no_debugging_features"))
        {
          if ((paramBoolean) && (paramInt == 0)) {
            Settings.Global.putStringForUser(localContentResolver, "adb_enabled", "0", paramInt);
          }
        }
        else if (paramString.equals("ensure_verify_apps"))
        {
          if (paramBoolean)
          {
            Settings.Global.putStringForUser(paramContext.getContentResolver(), "package_verifier_enable", "1", paramInt);
            Settings.Global.putStringForUser(paramContext.getContentResolver(), "verifier_verify_adb_installs", "1", paramInt);
          }
        }
        else if (paramString.equals("no_install_unknown_sources"))
        {
          if (paramBoolean) {
            Settings.Secure.putIntForUser(localContentResolver, "install_non_market_apps", 0, paramInt);
          }
        }
        else if (paramString.equals("no_run_in_background"))
        {
          if (!paramBoolean) {
            continue;
          }
          int i = ActivityManager.getCurrentUser();
          if ((i == paramInt) || (paramInt == 0)) {
            continue;
          }
          try
          {
            ActivityManagerNative.getDefault().stopUser(paramInt, false, null);
          }
          catch (RemoteException paramContext)
          {
            throw paramContext.rethrowAsRuntimeException();
          }
        }
      }
    }
    if (paramString.equals("no_safe_boot"))
    {
      paramContext = paramContext.getContentResolver();
      if (!paramBoolean) {
        break label417;
      }
    }
    label401:
    label403:
    label417:
    for (paramInt = 1;; paramInt = 0)
    {
      Settings.Global.putInt(paramContext, "safe_boot_disallowed", paramInt);
      break;
      if (paramString.equals("no_factory_reset")) {}
      for (;;)
      {
        if (!paramBoolean) {
          break label401;
        }
        paramContext = (PersistentDataBlockManager)paramContext.getSystemService("persistent_data_block");
        if ((paramContext == null) || (!paramContext.getOemUnlockEnabled()) || (paramContext.getFlashLockState() == 0)) {
          break;
        }
        paramContext.setOemUnlockEnabled(false);
        break;
        if (!paramString.equals("no_oem_unlock")) {
          break;
        }
      }
      break;
      Settings.Global.putStringForUser(localContentResolver, "data_roaming", "0", paramInt);
      break;
    }
  }
  
  public static void applyUserRestrictions(Context paramContext, int paramInt, Bundle paramBundle1, Bundle paramBundle2)
  {
    Iterator localIterator = USER_RESTRICTIONS.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      boolean bool = paramBundle1.getBoolean(str);
      if (bool != paramBundle2.getBoolean(str)) {
        applyUserRestriction(paramContext, paramInt, str, bool);
      }
    }
  }
  
  public static boolean areEqual(Bundle paramBundle1, Bundle paramBundle2)
  {
    if (paramBundle1 == paramBundle2) {
      return true;
    }
    if (isEmpty(paramBundle1)) {
      return isEmpty(paramBundle2);
    }
    if (isEmpty(paramBundle2)) {
      return false;
    }
    Iterator localIterator = paramBundle1.keySet().iterator();
    String str;
    while (localIterator.hasNext())
    {
      str = (String)localIterator.next();
      if (paramBundle1.getBoolean(str) != paramBundle2.getBoolean(str)) {
        return false;
      }
    }
    localIterator = paramBundle2.keySet().iterator();
    while (localIterator.hasNext())
    {
      str = (String)localIterator.next();
      if (paramBundle1.getBoolean(str) != paramBundle2.getBoolean(str)) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean canDeviceOwnerChange(String paramString)
  {
    return !IMMUTABLE_BY_OWNERS.contains(paramString);
  }
  
  public static boolean canProfileOwnerChange(String paramString, int paramInt)
  {
    return (!IMMUTABLE_BY_OWNERS.contains(paramString)) && ((paramInt == 0) || (!DEVICE_OWNER_ONLY_RESTRICTIONS.contains(paramString)));
  }
  
  public static Bundle clone(Bundle paramBundle)
  {
    if (paramBundle != null) {
      return new Bundle(paramBundle);
    }
    return new Bundle();
  }
  
  public static void dumpRestrictions(PrintWriter paramPrintWriter, String paramString, Bundle paramBundle)
  {
    int i = 1;
    if (paramBundle != null)
    {
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (paramBundle.getBoolean(str, false))
        {
          paramPrintWriter.println(paramString + str);
          i = 0;
        }
      }
      if (i != 0) {
        paramPrintWriter.println(paramString + "none");
      }
      return;
    }
    paramPrintWriter.println(paramString + "null");
  }
  
  public static boolean isEmpty(Bundle paramBundle)
  {
    return (paramBundle == null) || (paramBundle.size() == 0);
  }
  
  public static boolean isValidRestriction(String paramString)
  {
    if (!USER_RESTRICTIONS.contains(paramString))
    {
      Slog.e("UserRestrictionsUtils", "Unknown restriction: " + paramString);
      return false;
    }
    return true;
  }
  
  public static void merge(Bundle paramBundle1, Bundle paramBundle2)
  {
    Preconditions.checkNotNull(paramBundle1);
    if (paramBundle1 != paramBundle2) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkArgument(bool);
      if (paramBundle2 != null) {
        break;
      }
      return;
    }
    Iterator localIterator = paramBundle2.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (paramBundle2.getBoolean(str, false)) {
        paramBundle1.putBoolean(str, true);
      }
    }
  }
  
  private static Set<String> newSetWithUniqueCheck(String[] paramArrayOfString)
  {
    ArraySet localArraySet = Sets.newArraySet(paramArrayOfString);
    if (localArraySet.size() == paramArrayOfString.length) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool);
      return localArraySet;
    }
  }
  
  public static Bundle nonNull(Bundle paramBundle)
  {
    if (paramBundle != null) {
      return paramBundle;
    }
    return new Bundle();
  }
  
  public static void readRestrictions(XmlPullParser paramXmlPullParser, Bundle paramBundle)
  {
    Iterator localIterator = USER_RESTRICTIONS.iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = paramXmlPullParser.getAttributeValue(null, str1);
      if (str2 != null) {
        paramBundle.putBoolean(str1, Boolean.parseBoolean(str2));
      }
    }
  }
  
  public static void sortToGlobalAndLocal(Bundle paramBundle1, Bundle paramBundle2, Bundle paramBundle3)
  {
    if ((paramBundle1 == null) || (paramBundle1.size() == 0)) {
      return;
    }
    Iterator localIterator = paramBundle1.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (paramBundle1.getBoolean(str)) {
        if ((DEVICE_OWNER_ONLY_RESTRICTIONS.contains(str)) || (GLOBAL_RESTRICTIONS.contains(str))) {
          paramBundle2.putBoolean(str, true);
        } else {
          paramBundle3.putBoolean(str, true);
        }
      }
    }
  }
  
  public static void writeRestrictions(XmlSerializer paramXmlSerializer, Bundle paramBundle, String paramString)
    throws IOException
  {
    if (paramBundle == null) {
      return;
    }
    paramXmlSerializer.startTag(null, paramString);
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!NON_PERSIST_USER_RESTRICTIONS.contains(str)) {
        if (USER_RESTRICTIONS.contains(str))
        {
          if (paramBundle.getBoolean(str)) {
            paramXmlSerializer.attribute(null, str, "true");
          }
        }
        else {
          Log.w("UserRestrictionsUtils", "Unknown user restriction detected: " + str);
        }
      }
    }
    paramXmlSerializer.endTag(null, paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/UserRestrictionsUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */