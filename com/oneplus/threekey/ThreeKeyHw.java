package com.oneplus.threekey;

import android.content.Context;
import android.content.Intent;
import android.os.UEventObserver;
import android.os.UEventObserver.UEvent;
import android.os.UserHandle;
import android.util.OpFeatures;
import android.util.Slog;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;

public class ThreeKeyHw
{
  private static final String TAG = "ThreeKeyHw";
  private static int ThreeKeyModeState = 0;
  private static final String UDEV_NAME_THREEKEY = "tri-state-key";
  private static final boolean debug = true;
  private Context mContext;
  private OemUEventObserver mOemUEventObserver = new OemUEventObserver();
  private UEventInfo mThreeKeyUEventInfo = new UEventInfo("tri-state-key");
  
  public ThreeKeyHw(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private void sendBroadcastForZenModeChanged(int paramInt)
  {
    Intent localIntent = new Intent("com.oem.intent.action.THREE_KEY_MODE");
    localIntent.putExtra("switch_state", paramInt);
    this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL);
  }
  
  public int getState()
    throws ThreeKeyHw.ThreeKeyUnsupportException
  {
    if (!isSupportThreeKey()) {
      throw new ThreeKeyUnsupportException();
    }
    try
    {
      char[] arrayOfChar = new char['Ѐ'];
      FileReader localFileReader = new FileReader(this.mThreeKeyUEventInfo.getSwitchStatePath());
      int i = localFileReader.read(arrayOfChar, 0, 1024);
      localFileReader.close();
      i = Integer.valueOf(new String(arrayOfChar, 0, i).trim()).intValue();
      return i;
    }
    catch (Exception localException)
    {
      Slog.e("ThreeKeyHw", this.mThreeKeyUEventInfo.getSwitchStatePath() + "not found while attempting to get switch state");
      throw new ThreeKeyUnsupportException();
    }
  }
  
  public void init()
  {
    this.mOemUEventObserver.startMonitor();
  }
  
  public boolean isSupportThreeKey()
  {
    boolean bool = false;
    if (OpFeatures.isSupport(new int[] { 10 })) {
      bool = this.mThreeKeyUEventInfo.checkSwitchExists();
    }
    return bool;
  }
  
  class OemUEventObserver
    extends UEventObserver
  {
    OemUEventObserver() {}
    
    public void onUEvent(UEventObserver.UEvent paramUEvent)
    {
      Slog.d("ThreeKeyHw", "OEM UEVENT: " + paramUEvent.toString());
      try
      {
        paramUEvent.get("DEVPATH");
        paramUEvent.get("SWITCH_NAME");
        int i = Integer.parseInt(paramUEvent.get("SWITCH_STATE"));
        ThreeKeyHw.-wrap0(ThreeKeyHw.this, i);
        return;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Slog.e("ThreeKeyHw", "Could not parse switch state from event " + paramUEvent);
      }
    }
    
    void startMonitor()
    {
      startObserving("DEVPATH=" + ThreeKeyHw.-get0(ThreeKeyHw.this).getDevPath());
    }
  }
  
  public static class ThreeKeyUnsupportException
    extends Exception
  {}
  
  private final class UEventInfo
  {
    private final String mDevName;
    
    public UEventInfo(String paramString)
    {
      this.mDevName = paramString;
    }
    
    public boolean checkSwitchExists()
    {
      return new File(getSwitchStatePath()).exists();
    }
    
    public String getDevName()
    {
      return this.mDevName;
    }
    
    public String getDevPath()
    {
      return String.format(Locale.US, "/devices/virtual/switch/%s", new Object[] { this.mDevName });
    }
    
    public String getSwitchStatePath()
    {
      return String.format(Locale.US, "/sys/class/switch/%s/state", new Object[] { this.mDevName });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/threekey/ThreeKeyHw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */