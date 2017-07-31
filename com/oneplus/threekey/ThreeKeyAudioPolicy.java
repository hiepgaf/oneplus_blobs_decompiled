package com.oneplus.threekey;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.util.Slog;
import com.oem.os.IThreeKeyPolicy.Stub;
import com.oem.os.ThreeKeyManager;

public class ThreeKeyAudioPolicy
  extends IThreeKeyPolicy.Stub
{
  private static final boolean DEBUG = true;
  private static final int MAX = 100;
  private static final String TAG = "ThreeKeyAudioPolicy";
  private AudioManager mAudioManager;
  private Context mContext;
  private boolean mInitFlag = false;
  private boolean mMuteMediaFlag;
  private NotificationManager mNotificationManager;
  private boolean mOptionChangeFlag;
  private SettingsObserver mSettingsObserver;
  private ThreeKeyManager mThreeKeyManager;
  private final Object mThreeKeySettingsLock = new Object();
  private boolean mVibrateFlag;
  
  public ThreeKeyAudioPolicy(Context paramContext)
  {
    this.mContext = paramContext;
    this.mNotificationManager = ((NotificationManager)paramContext.getSystemService("notification"));
    this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
    this.mThreeKeyManager = ((ThreeKeyManager)paramContext.getSystemService("threekey"));
    this.mSettingsObserver = new SettingsObserver();
    this.mSettingsObserver.observe();
    if (Settings.System.getInt(this.mContext.getContentResolver(), "oem_zen_media_switch", 0) == 1)
    {
      bool1 = true;
      this.mMuteMediaFlag = bool1;
      if (Settings.System.getInt(this.mContext.getContentResolver(), "oem_vibrate_under_silent", 0) != 1) {
        break label144;
      }
    }
    label144:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mVibrateFlag = bool1;
      this.mOptionChangeFlag = false;
      return;
      bool1 = false;
      break;
    }
  }
  
  private void cleanAbnormalState()
  {
    this.mAudioManager.setRingerMode(2);
    this.mAudioManager.adjustStreamVolume(2, 100, 0);
  }
  
  private void muteSpeakerMediaVolume(boolean paramBoolean)
  {
    int i = 0;
    if (paramBoolean) {
      i = 1;
    }
    this.mAudioManager.threeKeySetStreamVolume(3, i, -100, 2);
  }
  
  private void restoreSpeakerMediaVolume()
  {
    this.mAudioManager.threeKeySetStreamVolume(3, 0, 100, 2);
  }
  
  public void setDontDisturb()
  {
    Slog.d("ThreeKeyAudioPolicy", "set mode dontdisturb, mVibrateFlag=" + this.mVibrateFlag + ", mMuteMediaFlag=" + this.mMuteMediaFlag);
    this.mAudioManager.setOnePlusFixedRingerMode(false);
    this.mAudioManager.setOnePlusRingVolumeRange(1, 100);
    cleanAbnormalState();
    this.mNotificationManager.setZenMode(1, null, "ThreeKeyAudioPolicy");
    Settings.Global.putInt(this.mContext.getContentResolver(), "zen_mode", 1);
    this.mAudioManager.setOnePlusFixedRingerMode(true);
    Settings.Global.putInt(this.mContext.getContentResolver(), "three_Key_mode", 2);
    restoreSpeakerMediaVolume();
    if (this.mOptionChangeFlag)
    {
      this.mOptionChangeFlag = false;
      return;
    }
  }
  
  public void setDown()
  {
    synchronized (this.mThreeKeySettingsLock)
    {
      setRing();
      return;
    }
  }
  
  public void setInitMode(boolean paramBoolean)
  {
    this.mInitFlag = paramBoolean;
  }
  
  public void setMiddle()
  {
    synchronized (this.mThreeKeySettingsLock)
    {
      setDontDisturb();
      return;
    }
  }
  
  public void setRing()
  {
    Slog.d("ThreeKeyAudioPolicy", "set mode ring, mVibrateFlag=" + this.mVibrateFlag + ", mMuteMediaFlag=" + this.mMuteMediaFlag);
    this.mAudioManager.setOnePlusFixedRingerMode(false);
    this.mAudioManager.setOnePlusRingVolumeRange(1, 100);
    this.mNotificationManager.setZenMode(0, null, "ThreeKeyAudioPolicy");
    Settings.Global.putInt(this.mContext.getContentResolver(), "zen_mode", 0);
    restoreSpeakerMediaVolume();
    Settings.Global.putInt(this.mContext.getContentResolver(), "three_Key_mode", 3);
    if (this.mOptionChangeFlag)
    {
      this.mOptionChangeFlag = false;
      return;
    }
  }
  
  public void setSlientNoVibrate()
  {
    Slog.d("ThreeKeyAudioPolicy", "set mode Slient No Vibrate, mVibrateFlag=" + this.mVibrateFlag + ", mMuteMediaFlag=" + this.mMuteMediaFlag);
    this.mAudioManager.setOnePlusFixedRingerMode(false);
    this.mAudioManager.setOnePlusRingVolumeRange(0, 0);
    this.mAudioManager.setRingerMode(2);
    this.mNotificationManager.setOnePlusVibrateInSilentMode(this.mVibrateFlag);
    this.mNotificationManager.setZenMode(3, null, "ThreeKeyAudioPolicy");
    Settings.Global.putInt(this.mContext.getContentResolver(), "zen_mode", 3);
    this.mAudioManager.setOnePlusFixedRingerMode(true);
    Settings.Global.putInt(this.mContext.getContentResolver(), "three_Key_mode", 1);
    if (this.mOptionChangeFlag)
    {
      this.mOptionChangeFlag = false;
      return;
    }
    if (this.mMuteMediaFlag) {
      muteSpeakerMediaVolume(this.mInitFlag);
    }
  }
  
  public void setSlientVibrate()
  {
    Slog.d("ThreeKeyAudioPolicy", "set mode Slient Vibrate, mVibrateFlag=" + this.mVibrateFlag + ", mMuteMediaFlag=" + this.mMuteMediaFlag);
    this.mAudioManager.setOnePlusFixedRingerMode(false);
    this.mNotificationManager.setOnePlusVibrateInSilentMode(this.mVibrateFlag);
    this.mAudioManager.setOnePlusRingVolumeRange(0, 0);
    this.mNotificationManager.setZenMode(0, null, "ThreeKeyAudioPolicy");
    Settings.Global.putInt(this.mContext.getContentResolver(), "zen_mode", 0);
    this.mAudioManager.setRingerMode(1);
    this.mAudioManager.setRingerModeInternal(1);
    this.mAudioManager.setOnePlusFixedRingerMode(true);
    Settings.Global.putInt(this.mContext.getContentResolver(), "three_Key_mode", 1);
    if (this.mOptionChangeFlag)
    {
      this.mOptionChangeFlag = false;
      return;
    }
    if (this.mMuteMediaFlag) {
      muteSpeakerMediaVolume(this.mInitFlag);
    }
  }
  
  public void setUp()
  {
    synchronized (this.mThreeKeySettingsLock)
    {
      if (this.mVibrateFlag)
      {
        setSlientVibrate();
        return;
      }
      setSlientNoVibrate();
    }
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    private final Uri MEDIA_SWITCH_MODE = Settings.System.getUriFor("oem_zen_media_switch");
    private final Uri VIBRATE_WHEN_MUTE_MODE = Settings.System.getUriFor("oem_vibrate_under_silent");
    private final Uri ZEN_MODE = Settings.Global.getUriFor("zen_mode");
    
    public SettingsObserver()
    {
      super();
    }
    
    public void observe()
    {
      ContentResolver localContentResolver = ThreeKeyAudioPolicy.-get0(ThreeKeyAudioPolicy.this).getContentResolver();
      localContentResolver.registerContentObserver(this.VIBRATE_WHEN_MUTE_MODE, false, this);
      localContentResolver.registerContentObserver(this.MEDIA_SWITCH_MODE, false, this);
      localContentResolver.registerContentObserver(this.ZEN_MODE, false, this);
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      boolean bool2 = false;
      boolean bool1 = false;
      Slog.d("ThreeKeyAudioPolicy", "settings change selfChange=" + paramBoolean + " uri=" + paramUri);
      int i = ThreeKeyAudioPolicy.-get1(ThreeKeyAudioPolicy.this).getThreeKeyStatus();
      int j = Settings.Global.getInt(ThreeKeyAudioPolicy.-get0(ThreeKeyAudioPolicy.this).getContentResolver(), "zen_mode", 0);
      if (paramUri.equals(this.VIBRATE_WHEN_MUTE_MODE))
      {
        paramUri = ThreeKeyAudioPolicy.this;
        if (Settings.System.getInt(ThreeKeyAudioPolicy.-get0(ThreeKeyAudioPolicy.this).getContentResolver(), "oem_vibrate_under_silent", 0) == 1)
        {
          paramBoolean = true;
          ThreeKeyAudioPolicy.-set2(paramUri, paramBoolean);
          if (i == 1)
          {
            ThreeKeyAudioPolicy.-set0(ThreeKeyAudioPolicy.this, false);
            ThreeKeyAudioPolicy.this.setUp();
            paramUri = ThreeKeyAudioPolicy.this;
            paramBoolean = bool1;
            if (Settings.System.getInt(ThreeKeyAudioPolicy.-get0(ThreeKeyAudioPolicy.this).getContentResolver(), "oem_zen_media_switch", 0) == 1) {
              paramBoolean = true;
            }
            ThreeKeyAudioPolicy.-set0(paramUri, paramBoolean);
          }
        }
      }
      label245:
      do
      {
        for (;;)
        {
          ThreeKeyAudioPolicy.-set1(ThreeKeyAudioPolicy.this, true);
          ThreeKeyAudioPolicy.-get1(ThreeKeyAudioPolicy.this).resetThreeKey();
          return;
          paramBoolean = false;
          break;
          if (!paramUri.equals(this.MEDIA_SWITCH_MODE)) {
            break label245;
          }
          paramUri = ThreeKeyAudioPolicy.this;
          paramBoolean = bool2;
          if (Settings.System.getInt(ThreeKeyAudioPolicy.-get0(ThreeKeyAudioPolicy.this).getContentResolver(), "oem_zen_media_switch", 0) == 1) {
            paramBoolean = true;
          }
          ThreeKeyAudioPolicy.-set0(paramUri, paramBoolean);
        }
      } while (!paramUri.equals(this.ZEN_MODE));
      Slog.d("ThreeKeyAudioPolicy", "zen mode was changed, zen mode=" + Settings.Global.zenModeToString(j) + ", three key status=" + i);
      if ((!ThreeKeyAudioPolicy.-get2(ThreeKeyAudioPolicy.this)) && (i == 1) && (j != 3)) {}
      while (((ThreeKeyAudioPolicy.-get2(ThreeKeyAudioPolicy.this)) && (i == 1) && (j != 0)) || ((i == 2) && (j != 1)) || (i == -1))
      {
        Slog.d("ThreeKeyAudioPolicy", "zen mode was changed to incorrect status,need to sync");
        break;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/threekey/ThreeKeyAudioPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */