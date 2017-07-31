package com.android.server.am;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

final class CoreSettingsObserver
  extends ContentObserver
{
  private static final String LOG_TAG = CoreSettingsObserver.class.getSimpleName();
  private static final Map<String, Class<?>> sGlobalSettingToTypeMap;
  private static final Map<String, Class<?>> sSecureSettingToTypeMap = new HashMap();
  private static final Map<String, Class<?>> sSystemSettingToTypeMap = new HashMap();
  private final ActivityManagerService mActivityManagerService;
  private final Bundle mCoreSettings = new Bundle();
  
  static
  {
    sGlobalSettingToTypeMap = new HashMap();
    sSecureSettingToTypeMap.put("long_press_timeout", Integer.TYPE);
    sSystemSettingToTypeMap.put("time_12_24", String.class);
    sGlobalSettingToTypeMap.put("debug_view_attributes", Integer.TYPE);
  }
  
  public CoreSettingsObserver(ActivityManagerService paramActivityManagerService)
  {
    super(paramActivityManagerService.mHandler);
    this.mActivityManagerService = paramActivityManagerService;
    beginObserveCoreSettings();
    sendCoreSettings();
  }
  
  private void beginObserveCoreSettings()
  {
    Iterator localIterator = sSecureSettingToTypeMap.keySet().iterator();
    Uri localUri;
    while (localIterator.hasNext())
    {
      localUri = Settings.Secure.getUriFor((String)localIterator.next());
      this.mActivityManagerService.mContext.getContentResolver().registerContentObserver(localUri, false, this);
    }
    localIterator = sSystemSettingToTypeMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      localUri = Settings.System.getUriFor((String)localIterator.next());
      this.mActivityManagerService.mContext.getContentResolver().registerContentObserver(localUri, false, this);
    }
    localIterator = sGlobalSettingToTypeMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      localUri = Settings.Global.getUriFor((String)localIterator.next());
      this.mActivityManagerService.mContext.getContentResolver().registerContentObserver(localUri, false, this);
    }
  }
  
  private void populateSettings(Bundle paramBundle, Map<String, Class<?>> paramMap)
  {
    Context localContext = this.mActivityManagerService.mContext;
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (Map.Entry)localIterator.next();
      String str = (String)((Map.Entry)localObject).getKey();
      localObject = (Class)((Map.Entry)localObject).getValue();
      if (localObject == String.class)
      {
        if (paramMap == sSecureSettingToTypeMap) {
          localObject = Settings.Secure.getString(localContext.getContentResolver(), str);
        }
        for (;;)
        {
          paramBundle.putString(str, (String)localObject);
          break;
          if (paramMap == sSystemSettingToTypeMap) {
            localObject = Settings.System.getString(localContext.getContentResolver(), str);
          } else {
            localObject = Settings.Global.getString(localContext.getContentResolver(), str);
          }
        }
      }
      if (localObject == Integer.TYPE)
      {
        int i;
        if (paramMap == sSecureSettingToTypeMap) {
          i = Settings.Secure.getInt(localContext.getContentResolver(), str, 0);
        }
        for (;;)
        {
          paramBundle.putInt(str, i);
          break;
          if (paramMap == sSystemSettingToTypeMap) {
            i = Settings.System.getInt(localContext.getContentResolver(), str, 0);
          } else {
            i = Settings.Global.getInt(localContext.getContentResolver(), str, 0);
          }
        }
      }
      if (localObject == Float.TYPE)
      {
        float f;
        if (paramMap == sSecureSettingToTypeMap) {
          f = Settings.Secure.getFloat(localContext.getContentResolver(), str, 0.0F);
        }
        for (;;)
        {
          paramBundle.putFloat(str, f);
          break;
          if (paramMap == sSystemSettingToTypeMap) {
            f = Settings.System.getFloat(localContext.getContentResolver(), str, 0.0F);
          } else {
            f = Settings.Global.getFloat(localContext.getContentResolver(), str, 0.0F);
          }
        }
      }
      if (localObject == Long.TYPE)
      {
        long l;
        if (paramMap == sSecureSettingToTypeMap) {
          l = Settings.Secure.getLong(localContext.getContentResolver(), str, 0L);
        }
        for (;;)
        {
          paramBundle.putLong(str, l);
          break;
          if (paramMap == sSystemSettingToTypeMap) {
            l = Settings.System.getLong(localContext.getContentResolver(), str, 0L);
          } else {
            l = Settings.Global.getLong(localContext.getContentResolver(), str, 0L);
          }
        }
      }
    }
  }
  
  private void sendCoreSettings()
  {
    populateSettings(this.mCoreSettings, sSecureSettingToTypeMap);
    populateSettings(this.mCoreSettings, sSystemSettingToTypeMap);
    populateSettings(this.mCoreSettings, sGlobalSettingToTypeMap);
    this.mActivityManagerService.onCoreSettingsChange(this.mCoreSettings);
  }
  
  public Bundle getCoreSettingsLocked()
  {
    return (Bundle)this.mCoreSettings.clone();
  }
  
  public void onChange(boolean paramBoolean)
  {
    synchronized (this.mActivityManagerService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      sendCoreSettings();
      ActivityManagerService.resetPriorityAfterLockedSection();
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/CoreSettingsObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */