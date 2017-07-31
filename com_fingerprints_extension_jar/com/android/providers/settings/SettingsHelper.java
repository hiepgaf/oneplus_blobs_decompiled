package com.android.providers.settings;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManager.Stub;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IPowerManager;
import android.os.IPowerManager.Stub;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArraySet;
import java.util.Locale;

public class SettingsHelper
{
  private static final ArraySet<String> sBroadcastOnRestore = new ArraySet(4);
  private static SettingsLookup sGlobalLookup = new SettingsLookup()
  {
    public String lookup(ContentResolver paramAnonymousContentResolver, String paramAnonymousString, int paramAnonymousInt)
    {
      return Settings.Global.getStringForUser(paramAnonymousContentResolver, paramAnonymousString, paramAnonymousInt);
    }
  };
  private static SettingsLookup sSecureLookup;
  private static SettingsLookup sSystemLookup;
  private AudioManager mAudioManager;
  private Context mContext;
  private TelephonyManager mTelephonyManager;
  
  static
  {
    sBroadcastOnRestore.add("enabled_notification_listeners");
    sBroadcastOnRestore.add("enabled_vr_listeners");
    sBroadcastOnRestore.add("enabled_accessibility_services");
    sBroadcastOnRestore.add("enabled_input_methods");
    sSystemLookup = new SettingsLookup()
    {
      public String lookup(ContentResolver paramAnonymousContentResolver, String paramAnonymousString, int paramAnonymousInt)
      {
        return Settings.System.getStringForUser(paramAnonymousContentResolver, paramAnonymousString, paramAnonymousInt);
      }
    };
    sSecureLookup = new SettingsLookup()
    {
      public String lookup(ContentResolver paramAnonymousContentResolver, String paramAnonymousString, int paramAnonymousInt)
      {
        return Settings.Secure.getStringForUser(paramAnonymousContentResolver, paramAnonymousString, paramAnonymousInt);
      }
    };
  }
  
  public SettingsHelper(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
    this.mTelephonyManager = ((TelephonyManager)paramContext.getSystemService("phone"));
  }
  
  private String getCanonicalRingtoneValue(String paramString)
  {
    paramString = Uri.parse(paramString);
    paramString = this.mContext.getContentResolver().canonicalize(paramString);
    if (paramString == null) {
      return null;
    }
    return paramString.toString();
  }
  
  private boolean isAlreadyConfiguredCriticalAccessibilitySetting(String paramString)
  {
    if (paramString.equals("accessibility_enabled")) {}
    while (Settings.Secure.getInt(this.mContext.getContentResolver(), paramString, 0) != 0)
    {
      return true;
      if ((!paramString.equals("accessibility_script_injection")) && (!paramString.equals("speak_password")) && (!paramString.equals("touch_exploration_enabled")) && (!paramString.equals("accessibility_display_daltonizer_enabled")) && (!paramString.equals("accessibility_display_magnification_enabled")) && (!paramString.equals("ui_night_mode")))
      {
        if (paramString.equals("touch_exploration_granted_accessibility_services")) {}
        while ((paramString.equals("enabled_accessibility_services")) || (paramString.equals("accessibility_display_daltonizer")) || (paramString.equals("accessibility_display_magnification_scale")))
        {
          if (!TextUtils.isEmpty(Settings.Secure.getString(this.mContext.getContentResolver(), paramString))) {
            break;
          }
          return false;
        }
        if (!paramString.equals("font_scale")) {
          break label169;
        }
        if (Settings.System.getFloat(this.mContext.getContentResolver(), paramString, 1.0F) == 1.0F) {
          break label167;
        }
        return true;
      }
    }
    return false;
    return true;
    label167:
    return false;
    label169:
    return false;
  }
  
  private void setAutoRestore(boolean paramBoolean)
  {
    try
    {
      IBackupManager localIBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
      if (localIBackupManager != null) {
        localIBackupManager.setAutoRestore(paramBoolean);
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void setBrightness(int paramInt)
  {
    try
    {
      IPowerManager localIPowerManager = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
      if (localIPowerManager != null) {
        localIPowerManager.setTemporaryScreenBrightnessSettingOverride(paramInt);
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void setGpsLocation(String paramString)
  {
    if (((UserManager)this.mContext.getSystemService("user")).hasUserRestriction("no_share_location")) {
      return;
    }
    if ((!"gps".equals(paramString)) && (!paramString.startsWith("gps,")) && (!paramString.endsWith(",gps"))) {}
    for (boolean bool = paramString.contains(",gps,");; bool = true)
    {
      Settings.Secure.setLocationProviderEnabled(this.mContext.getContentResolver(), "gps", bool);
      return;
    }
  }
  
  private void setRingtone(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return;
    }
    if ("_silent".equals(paramString2))
    {
      paramString2 = null;
      if (!"ringtone".equals(paramString1)) {
        break label64;
      }
    }
    label64:
    for (int i = 1;; i = 2)
    {
      RingtoneManager.setActualDefaultRingtoneUri(this.mContext, i, paramString2);
      return;
      paramString2 = Uri.parse(paramString2);
      Uri localUri = this.mContext.getContentResolver().uncanonicalize(paramString2);
      paramString2 = localUri;
      if (localUri != null) {
        break;
      }
      return;
    }
  }
  
  private void setSoundEffects(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mAudioManager.loadSoundEffects();
      return;
    }
    this.mAudioManager.unloadSoundEffects();
  }
  
  void applyAudioSettings()
  {
    new AudioManager(this.mContext).reloadAudioSettings();
  }
  
  byte[] getLocaleData()
  {
    Object localObject = this.mContext.getResources().getConfiguration().locale;
    String str1 = ((Locale)localObject).getLanguage();
    String str2 = ((Locale)localObject).getCountry();
    localObject = str1;
    if (!TextUtils.isEmpty(str2)) {
      localObject = str1 + "-" + str2;
    }
    return ((String)localObject).getBytes();
  }
  
  public String onBackupValue(String paramString1, String paramString2)
  {
    if (("ringtone".equals(paramString1)) || ("notification_sound".equals(paramString1)))
    {
      if (paramString2 == null)
      {
        if ("ringtone".equals(paramString1))
        {
          if ((this.mTelephonyManager != null) && (this.mTelephonyManager.isVoiceCapable())) {
            return "_silent";
          }
          return null;
        }
        return "_silent";
      }
      return getCanonicalRingtoneValue(paramString2);
    }
    return paramString2;
  }
  
  public void restoreValue(Context paramContext, ContentResolver paramContentResolver, ContentValues paramContentValues, Uri paramUri, String paramString1, String paramString2)
  {
    String str = null;
    int i = 0;
    SettingsLookup localSettingsLookup;
    if (paramUri.equals(Settings.Secure.CONTENT_URI))
    {
      localSettingsLookup = sSecureLookup;
      if (sBroadcastOnRestore.contains(paramString1))
      {
        str = localSettingsLookup.lookup(paramContentResolver, paramString1, 0);
        i = 1;
      }
    }
    label461:
    do
    {
      for (;;)
      {
        try
        {
          if ("screen_brightness".equals(paramString1))
          {
            setBrightness(Integer.parseInt(paramString2));
            paramContentValues.clear();
            paramContentValues.put("name", paramString1);
            paramContentValues.put("value", paramString2);
            paramContentResolver.insert(paramUri, paramContentValues);
            return;
            if (paramUri.equals(Settings.System.CONTENT_URI))
            {
              localSettingsLookup = sSystemLookup;
              break;
            }
            localSettingsLookup = sGlobalLookup;
            break;
          }
          if ("sound_effects_enabled".equals(paramString1)) {
            if (Integer.parseInt(paramString2) == 1)
            {
              bool = true;
              setSoundEffects(bool);
              continue;
            }
          }
        }
        catch (Exception paramContentResolver)
        {
          if (0 == 0) {
            continue;
          }
          paramContext.sendBroadcastAsUser(new Intent("android.os.action.SETTING_RESTORED").setPackage("android").addFlags(1073741824).putExtra("setting_name", paramString1).putExtra("new_value", paramString2).putExtra("previous_value", str), UserHandle.SYSTEM, null);
          return;
          bool = false;
          continue;
          if ("location_providers_allowed".equals(paramString1))
          {
            setGpsLocation(paramString2);
            return;
          }
          if (!"backup_auto_restore".equals(paramString1)) {
            break label461;
          }
          if (Integer.parseInt(paramString2) == 1)
          {
            bool = true;
            setAutoRestore(bool);
            continue;
          }
        }
        finally
        {
          if (i != 0) {
            paramContext.sendBroadcastAsUser(new Intent("android.os.action.SETTING_RESTORED").setPackage("android").addFlags(1073741824).putExtra("setting_name", paramString1).putExtra("new_value", paramString2).putExtra("previous_value", str), UserHandle.SYSTEM, null);
          }
        }
        bool = false;
      }
      boolean bool = isAlreadyConfiguredCriticalAccessibilitySetting(paramString1);
      if (bool)
      {
        if (i != 0) {
          paramContext.sendBroadcastAsUser(new Intent("android.os.action.SETTING_RESTORED").setPackage("android").addFlags(1073741824).putExtra("setting_name", paramString1).putExtra("new_value", paramString2).putExtra("previous_value", str), UserHandle.SYSTEM, null);
        }
        return;
      }
    } while ((!"ringtone".equals(paramString1)) && (!"notification_sound".equals(paramString1)));
    setRingtone(paramString1, paramString2);
    if (i != 0) {
      paramContext.sendBroadcastAsUser(new Intent("android.os.action.SETTING_RESTORED").setPackage("android").addFlags(1073741824).putExtra("setting_name", paramString1).putExtra("new_value", paramString2).putExtra("previous_value", str), UserHandle.SYSTEM, null);
    }
  }
  
  void setLocaleData(byte[] paramArrayOfByte, int paramInt)
  {
    if (this.mContext.getResources().getConfiguration().userSetLocale) {
      return;
    }
    Object localObject = this.mContext.getAssets().getLocales();
    String str = new String(paramArrayOfByte, 0, paramInt).replace('_', '-');
    IActivityManager localIActivityManager = null;
    paramInt = 0;
    for (;;)
    {
      paramArrayOfByte = localIActivityManager;
      if (paramInt < localObject.length)
      {
        if (localObject[paramInt].equals(str)) {
          paramArrayOfByte = Locale.forLanguageTag(str);
        }
      }
      else
      {
        if (paramArrayOfByte != null) {
          break;
        }
        return;
      }
      paramInt += 1;
    }
    try
    {
      localIActivityManager = ActivityManagerNative.getDefault();
      localObject = localIActivityManager.getConfiguration();
      ((Configuration)localObject).locale = paramArrayOfByte;
      ((Configuration)localObject).userSetLocale = true;
      localIActivityManager.updateConfiguration((Configuration)localObject);
      return;
    }
    catch (RemoteException paramArrayOfByte) {}
  }
  
  private static abstract interface SettingsLookup
  {
    public abstract String lookup(ContentResolver paramContentResolver, String paramString, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/providers/settings/SettingsHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */