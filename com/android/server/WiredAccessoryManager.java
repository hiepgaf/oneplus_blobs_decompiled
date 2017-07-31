package com.android.server;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.UEventObserver;
import android.os.UEventObserver.UEvent;
import android.util.Log;
import android.util.Slog;
import com.android.server.input.InputManagerService;
import com.android.server.input.InputManagerService.WiredAccessoryCallbacks;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class WiredAccessoryManager
  implements InputManagerService.WiredAccessoryCallbacks
{
  private static final int BIT_HDMI_AUDIO = 16;
  private static final int BIT_HEADSET = 1;
  private static final int BIT_HEADSET_NO_MIC = 2;
  private static final int BIT_LINEOUT = 32;
  private static final int BIT_USB_HEADSET_ANLG = 4;
  private static final int BIT_USB_HEADSET_DGTL = 8;
  private static final boolean LOG = true;
  private static final int MSG_NEW_DEVICE_STATE = 1;
  private static final int MSG_SYSTEM_READY = 2;
  private static final String NAME_H2W = "h2w";
  private static final String NAME_HDMI = "hdmi";
  private static final String NAME_HDMI_AUDIO = "hdmi_audio";
  private static final String NAME_USB_AUDIO = "usb_audio";
  private static final int SUPPORTED_HEADSETS = 63;
  private static final String TAG = WiredAccessoryManager.class.getSimpleName();
  private final AudioManager mAudioManager;
  private final Handler mHandler = new Handler(Looper.myLooper(), null, true)
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        WiredAccessoryManager.-wrap1(WiredAccessoryManager.this, paramAnonymousMessage.arg1, paramAnonymousMessage.arg2, (String)paramAnonymousMessage.obj);
        WiredAccessoryManager.-get4(WiredAccessoryManager.this).release();
        return;
      }
      WiredAccessoryManager.-wrap0(WiredAccessoryManager.this);
      WiredAccessoryManager.-get4(WiredAccessoryManager.this).release();
    }
  };
  private int mHeadsetState;
  private final InputManagerService mInputManager;
  private final Object mLock = new Object();
  private final WiredAccessoryObserver mObserver;
  private int mSwitchValues;
  private final boolean mUseDevInputEventForAudioJack;
  private final PowerManager.WakeLock mWakeLock;
  
  public WiredAccessoryManager(Context paramContext, InputManagerService paramInputManagerService)
  {
    this.mWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "WiredAccessoryManager");
    this.mWakeLock.setReferenceCounted(false);
    this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
    this.mInputManager = paramInputManagerService;
    this.mUseDevInputEventForAudioJack = paramContext.getResources().getBoolean(17956990);
    this.mObserver = new WiredAccessoryObserver();
  }
  
  private void onSystemReady()
  {
    if (this.mUseDevInputEventForAudioJack)
    {
      int j = 0;
      if (this.mInputManager.getSwitchState(-1, 65280, 2) == 1) {
        j = 4;
      }
      int i = j;
      if (this.mInputManager.getSwitchState(-1, 65280, 4) == 1) {
        i = j | 0x10;
      }
      j = i;
      if (this.mInputManager.getSwitchState(-1, 65280, 6) == 1) {
        j = i | 0x40;
      }
      notifyWiredAccessoryChanged(0L, j, 84);
    }
    this.mObserver.init();
  }
  
  private void setDeviceStateLocked(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    label29:
    String str2;
    StringBuilder localStringBuilder;
    if ((paramInt2 & paramInt1) != (paramInt3 & paramInt1))
    {
      paramInt3 = 0;
      if ((paramInt2 & paramInt1) == 0) {
        break label113;
      }
      paramInt2 = 1;
      if (paramInt1 != 1) {
        break label118;
      }
      paramInt1 = 4;
      paramInt3 = -2147483632;
      str2 = TAG;
      localStringBuilder = new StringBuilder().append("headsetName: ").append(paramString);
      if (paramInt2 != 1) {
        break label206;
      }
    }
    label113:
    label118:
    label206:
    for (String str1 = " connected";; str1 = " disconnected")
    {
      Slog.v(str2, str1);
      if (paramInt1 != 0) {
        this.mAudioManager.setWiredDeviceConnectionState(paramInt1, paramInt2, "", paramString);
      }
      if (paramInt3 != 0) {
        this.mAudioManager.setWiredDeviceConnectionState(paramInt3, paramInt2, "", paramString);
      }
      return;
      paramInt2 = 0;
      break;
      if (paramInt1 == 2)
      {
        paramInt1 = 8;
        break label29;
      }
      if (paramInt1 == 32)
      {
        paramInt1 = 131072;
        break label29;
      }
      if (paramInt1 == 4)
      {
        paramInt1 = 2048;
        break label29;
      }
      if (paramInt1 == 8)
      {
        paramInt1 = 4096;
        break label29;
      }
      if (paramInt1 == 16)
      {
        paramInt1 = 1024;
        break label29;
      }
      Slog.e(TAG, "setDeviceState() invalid headset type: " + paramInt1);
      return;
    }
  }
  
  private void setDevicesState(int paramInt1, int paramInt2, String paramString)
  {
    localObject = this.mLock;
    if ((2 == paramInt2) && (1 == paramInt1)) {}
    for (;;)
    {
      try
      {
        setDeviceStateLocked(2, paramInt1, paramInt2, paramString + " not broadcast");
        setDeviceStateLocked(1, paramInt1, paramInt2, paramString);
        return;
      }
      finally {}
      int j = 63;
      int i = 1;
      if (j != 0)
      {
        int k = j;
        if ((i & j) != 0)
        {
          setDeviceStateLocked(i, paramInt1, paramInt2, paramString);
          k = j & i;
        }
        i <<= 1;
        j = k;
      }
    }
  }
  
  private String switchCodeToString(int paramInt1, int paramInt2)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (((paramInt2 & 0x4) != 0) && ((paramInt1 & 0x4) != 0)) {
      localStringBuffer.append("SW_HEADPHONE_INSERT ");
    }
    if (((paramInt2 & 0x10) != 0) && ((paramInt1 & 0x10) != 0)) {
      localStringBuffer.append("SW_MICROPHONE_INSERT");
    }
    return localStringBuffer.toString();
  }
  
  private void updateLocked(String paramString, int paramInt)
  {
    int k = paramInt & 0x3F;
    int i = 1;
    int j = 1;
    Slog.v(TAG, "newName=" + paramString + " newState=" + paramInt + " headsetState=" + k + " prev headsetState=" + this.mHeadsetState);
    if (this.mHeadsetState == k)
    {
      Log.e(TAG, "No state change.");
      return;
    }
    paramInt = i;
    if ((k & 0x23) == 35)
    {
      Log.e(TAG, "Invalid combination, unsetting h2w flag");
      paramInt = 0;
    }
    i = j;
    if ((k & 0x4) == 4)
    {
      i = j;
      if ((k & 0x8) == 8)
      {
        Log.e(TAG, "Invalid combination, unsetting usb flag");
        i = 0;
      }
    }
    if ((paramInt != 0) || (i != 0))
    {
      this.mWakeLock.acquire();
      Log.i(TAG, "MSG_NEW_DEVICE_STATE");
      paramString = this.mHandler.obtainMessage(1, k, this.mHeadsetState, paramString);
      this.mHandler.sendMessage(paramString);
      this.mHeadsetState = k;
      return;
    }
    Log.e(TAG, "invalid transition, returning ...");
  }
  
  public void notifyWiredAccessoryChanged(long paramLong, int paramInt1, int paramInt2)
  {
    Slog.v(TAG, "notifyWiredAccessoryChanged: when=" + paramLong + " bits=" + switchCodeToString(paramInt1, paramInt2) + " mask=" + Integer.toHexString(paramInt2));
    for (;;)
    {
      synchronized (this.mLock)
      {
        this.mSwitchValues = (this.mSwitchValues & paramInt2 | paramInt1);
        switch (this.mSwitchValues & 0x54)
        {
        case 0: 
          updateLocked("h2w", this.mHeadsetState & 0xFFFFFFDC | paramInt1);
          return;
          paramInt1 = 0;
          break;
        case 4: 
          paramInt1 = 2;
          break;
        case 64: 
          paramInt1 = 32;
          break;
        case 20: 
          paramInt1 = 1;
          break;
        case 16: 
          paramInt1 = 1;
        }
      }
      paramInt1 = 0;
    }
  }
  
  public void systemReady()
  {
    synchronized (this.mLock)
    {
      this.mWakeLock.acquire();
      Message localMessage = this.mHandler.obtainMessage(2, 0, 0, null);
      this.mHandler.sendMessage(localMessage);
      return;
    }
  }
  
  class WiredAccessoryObserver
    extends UEventObserver
  {
    private final List<UEventInfo> mUEventInfo = makeObservedUEventList();
    private String switchPath;
    private String tmp = null;
    
    public WiredAccessoryObserver() {}
    
    private List<UEventInfo> makeObservedUEventList()
    {
      ArrayList localArrayList = new ArrayList();
      if (!WiredAccessoryManager.-get3(WiredAccessoryManager.this))
      {
        localUEventInfo = new UEventInfo("h2w", 1, 2, 32);
        if (localUEventInfo.checkSwitchExists()) {
          localArrayList.add(localUEventInfo);
        }
      }
      else
      {
        localUEventInfo = new UEventInfo("usb_audio", 4, 8, 0);
        if (!localUEventInfo.checkSwitchExists()) {
          break label122;
        }
        localArrayList.add(localUEventInfo);
      }
      for (;;)
      {
        localUEventInfo = new UEventInfo("hdmi_audio", 16, 0, 0);
        if (!localUEventInfo.checkSwitchExists()) {
          break label134;
        }
        localArrayList.add(localUEventInfo);
        return localArrayList;
        Slog.w(WiredAccessoryManager.-get0(), "This kernel does not have wired headset support");
        break;
        label122:
        Slog.w(WiredAccessoryManager.-get0(), "This kernel does not have usb audio support");
      }
      label134:
      UEventInfo localUEventInfo = new UEventInfo("hdmi", 16, 0, 0);
      if (localUEventInfo.checkSwitchExists())
      {
        localArrayList.add(localUEventInfo);
        return localArrayList;
      }
      Slog.w(WiredAccessoryManager.-get0(), "This kernel does not have HDMI audio support");
      return localArrayList;
    }
    
    private void updateStateLocked(String paramString1, String paramString2, int paramInt)
    {
      int i = 0;
      while (i < this.mUEventInfo.size())
      {
        UEventInfo localUEventInfo = (UEventInfo)this.mUEventInfo.get(i);
        if (paramString1.equals(localUEventInfo.getDevPath()))
        {
          WiredAccessoryManager.-wrap2(WiredAccessoryManager.this, paramString2, localUEventInfo.computeNewHeadsetState(WiredAccessoryManager.-get1(WiredAccessoryManager.this), paramInt));
          return;
        }
        i += 1;
      }
    }
    
    void init()
    {
      for (;;)
      {
        char[] arrayOfChar;
        UEventInfo localUEventInfo;
        synchronized (WiredAccessoryManager.-get2(WiredAccessoryManager.this))
        {
          Slog.v(WiredAccessoryManager.-get0(), "init()");
          arrayOfChar = new char['Ѐ'];
          i = 0;
          if (i >= this.mUEventInfo.size()) {
            break label276;
          }
          localUEventInfo = (UEventInfo)this.mUEventInfo.get(i);
        }
        try
        {
          FileReader localFileReader = new FileReader(localUEventInfo.getSwitchStatePath());
          int j = localFileReader.read(arrayOfChar, 0, 1024);
          localFileReader.close();
          j = Integer.parseInt(new String(arrayOfChar, 0, j).trim());
          if (j <= 0) {
            break label341;
          }
          this.switchPath = "/sys/class/switch/h2w/name";
          try
          {
            this.tmp = FileUtils.readTextFile(new File(this.switchPath), 0, null).trim();
            Log.e(WiredAccessoryManager.-get0(), "WiredAccessoryObserver init headset name: " + this.tmp);
            updateStateLocked(localUEventInfo.getDevPath(), this.tmp, j);
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              Log.e(WiredAccessoryManager.-get0(), "failed to read from " + this.switchPath);
            }
          }
        }
        catch (FileNotFoundException localFileNotFoundException)
        {
          Slog.w(WiredAccessoryManager.-get0(), localUEventInfo.getSwitchStatePath() + " not found while attempting to determine initial switch state");
          break label341;
          localObject2 = finally;
          throw ((Throwable)localObject2);
        }
        catch (Exception localException1)
        {
          Slog.e(WiredAccessoryManager.-get0(), "", localException1);
        }
        label276:
        int i = 0;
        while (i < this.mUEventInfo.size())
        {
          ??? = (UEventInfo)this.mUEventInfo.get(i);
          startObserving("DEVPATH=" + ((UEventInfo)???).getDevPath());
          i += 1;
        }
        return;
        label341:
        i += 1;
      }
    }
    
    public void onUEvent(UEventObserver.UEvent paramUEvent)
    {
      Slog.v(WiredAccessoryManager.-get0(), "Headset UEVENT: " + paramUEvent.toString());
      try
      {
        String str1 = paramUEvent.get("DEVPATH");
        String str2 = paramUEvent.get("SWITCH_NAME");
        int i = Integer.parseInt(paramUEvent.get("SWITCH_STATE"));
        synchronized (WiredAccessoryManager.-get2(WiredAccessoryManager.this))
        {
          updateStateLocked(str1, str2, i);
          return;
        }
        return;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Slog.e(WiredAccessoryManager.-get0(), "Could not parse switch state from event " + paramUEvent);
      }
    }
    
    private final class UEventInfo
    {
      private final String mDevName;
      private final int mState1Bits;
      private final int mState2Bits;
      private final int mStateNbits;
      
      public UEventInfo(String paramString, int paramInt1, int paramInt2, int paramInt3)
      {
        this.mDevName = paramString;
        this.mState1Bits = paramInt1;
        this.mState2Bits = paramInt2;
        this.mStateNbits = paramInt3;
      }
      
      public boolean checkSwitchExists()
      {
        return new File(getSwitchStatePath()).exists();
      }
      
      public int computeNewHeadsetState(int paramInt1, int paramInt2)
      {
        int i = this.mState1Bits;
        int j = this.mState2Bits;
        int k = this.mStateNbits;
        if (paramInt2 == 1) {
          paramInt2 = this.mState1Bits;
        }
        for (;;)
        {
          return paramInt1 & (i | j | k) | paramInt2;
          if (paramInt2 == 2) {
            paramInt2 = this.mState2Bits;
          } else if (paramInt2 == this.mStateNbits) {
            paramInt2 = this.mStateNbits;
          } else {
            paramInt2 = 0;
          }
        }
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
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/WiredAccessoryManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */