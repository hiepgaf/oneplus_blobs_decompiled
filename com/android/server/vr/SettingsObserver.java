package com.android.server.vr;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.ArraySet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class SettingsObserver
{
  private final ContentObserver mContentObserver;
  private final String mSecureSettingName;
  private final BroadcastReceiver mSettingRestoreReceiver;
  private final Set<SettingChangeListener> mSettingsListeners = new ArraySet();
  
  private SettingsObserver(Context paramContext, Handler paramHandler, final Uri paramUri, final String paramString)
  {
    this.mSecureSettingName = paramString;
    this.mSettingRestoreReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (("android.os.action.SETTING_RESTORED".equals(paramAnonymousIntent.getAction())) && (Objects.equals(paramAnonymousIntent.getStringExtra("setting_name"), paramString)))
        {
          paramAnonymousContext = paramAnonymousIntent.getStringExtra("previous_value");
          paramAnonymousIntent = paramAnonymousIntent.getStringExtra("new_value");
          SettingsObserver.-wrap1(SettingsObserver.this, paramAnonymousContext, paramAnonymousIntent, getSendingUserId());
        }
      }
    };
    this.mContentObserver = new ContentObserver(paramHandler)
    {
      public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
      {
        if ((paramAnonymousUri == null) || (paramUri.equals(paramAnonymousUri))) {
          SettingsObserver.-wrap0(SettingsObserver.this);
        }
      }
    };
    paramContext.getContentResolver().registerContentObserver(paramUri, false, this.mContentObserver, -1);
  }
  
  public static SettingsObserver build(Context paramContext, Handler paramHandler, String paramString)
  {
    return new SettingsObserver(paramContext, paramHandler, Settings.Secure.getUriFor(paramString), paramString);
  }
  
  private void sendSettingChanged()
  {
    Iterator localIterator = this.mSettingsListeners.iterator();
    while (localIterator.hasNext()) {
      ((SettingChangeListener)localIterator.next()).onSettingChanged();
    }
  }
  
  private void sendSettingRestored(String paramString1, String paramString2, int paramInt)
  {
    Iterator localIterator = this.mSettingsListeners.iterator();
    while (localIterator.hasNext()) {
      ((SettingChangeListener)localIterator.next()).onSettingRestored(paramString1, paramString2, paramInt);
    }
  }
  
  public void addListener(SettingChangeListener paramSettingChangeListener)
  {
    this.mSettingsListeners.add(paramSettingChangeListener);
  }
  
  public void removeListener(SettingChangeListener paramSettingChangeListener)
  {
    this.mSettingsListeners.remove(paramSettingChangeListener);
  }
  
  public static abstract interface SettingChangeListener
  {
    public abstract void onSettingChanged();
    
    public abstract void onSettingRestored(String paramString1, String paramString2, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/vr/SettingsObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */