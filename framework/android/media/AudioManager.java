package android.media;

import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.media.audiopolicy.AudioPolicy;
import android.media.session.MediaSessionLegacyHelper;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings.System;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AudioManager
{
  public static final String ACTION_AUDIO_BECOMING_NOISY = "android.media.AUDIO_BECOMING_NOISY";
  public static final String ACTION_HDMI_AUDIO_PLUG = "android.media.action.HDMI_AUDIO_PLUG";
  public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
  @Deprecated
  public static final String ACTION_SCO_AUDIO_STATE_CHANGED = "android.media.SCO_AUDIO_STATE_CHANGED";
  public static final String ACTION_SCO_AUDIO_STATE_UPDATED = "android.media.ACTION_SCO_AUDIO_STATE_UPDATED";
  public static final int ADJUST_LOWER = -1;
  public static final int ADJUST_MUTE = -100;
  public static final int ADJUST_RAISE = 1;
  public static final int ADJUST_SAME = 0;
  public static final int ADJUST_TOGGLE_MUTE = 101;
  public static final int ADJUST_UNMUTE = 100;
  public static final int AUDIOFOCUS_FLAGS_APPS = 3;
  public static final int AUDIOFOCUS_FLAGS_SYSTEM = 7;
  public static final int AUDIOFOCUS_FLAG_DELAY_OK = 1;
  public static final int AUDIOFOCUS_FLAG_LOCK = 4;
  public static final int AUDIOFOCUS_FLAG_PAUSES_ON_DUCKABLE_LOSS = 2;
  public static final int AUDIOFOCUS_GAIN = 1;
  public static final int AUDIOFOCUS_GAIN_TRANSIENT = 2;
  public static final int AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE = 4;
  public static final int AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK = 3;
  public static final int AUDIOFOCUS_LOSS = -1;
  public static final int AUDIOFOCUS_LOSS_TRANSIENT = -2;
  public static final int AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = -3;
  public static final int AUDIOFOCUS_NONE = 0;
  public static final int AUDIOFOCUS_REQUEST_DELAYED = 2;
  public static final int AUDIOFOCUS_REQUEST_FAILED = 0;
  public static final int AUDIOFOCUS_REQUEST_GRANTED = 1;
  static final int AUDIOPORT_GENERATION_INIT = 0;
  public static final int AUDIO_SESSION_ID_GENERATE = 0;
  public static final int DEVICE_IN_ANLG_DOCK_HEADSET = -2147483136;
  public static final int DEVICE_IN_BACK_MIC = -2147483520;
  public static final int DEVICE_IN_BLUETOOTH_SCO_HEADSET = -2147483640;
  public static final int DEVICE_IN_BUILTIN_MIC = -2147483644;
  public static final int DEVICE_IN_DGTL_DOCK_HEADSET = -2147482624;
  public static final int DEVICE_IN_FM_TUNER = -2147475456;
  public static final int DEVICE_IN_HDMI = -2147483616;
  public static final int DEVICE_IN_LINE = -2147450880;
  public static final int DEVICE_IN_LOOPBACK = -2147221504;
  public static final int DEVICE_IN_SPDIF = -2147418112;
  public static final int DEVICE_IN_TELEPHONY_RX = -2147483584;
  public static final int DEVICE_IN_TV_TUNER = -2147467264;
  public static final int DEVICE_IN_USB_ACCESSORY = -2147481600;
  public static final int DEVICE_IN_USB_DEVICE = -2147479552;
  public static final int DEVICE_IN_WIRED_HEADSET = -2147483632;
  public static final int DEVICE_NONE = 0;
  public static final int DEVICE_OUT_ANLG_DOCK_HEADSET = 2048;
  public static final int DEVICE_OUT_AUX_DIGITAL = 1024;
  public static final int DEVICE_OUT_BLUETOOTH_A2DP = 128;
  public static final int DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES = 256;
  public static final int DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER = 512;
  public static final int DEVICE_OUT_BLUETOOTH_SCO = 16;
  public static final int DEVICE_OUT_BLUETOOTH_SCO_CARKIT = 64;
  public static final int DEVICE_OUT_BLUETOOTH_SCO_HEADSET = 32;
  public static final int DEVICE_OUT_DEFAULT = 1073741824;
  public static final int DEVICE_OUT_DGTL_DOCK_HEADSET = 4096;
  public static final int DEVICE_OUT_EARPIECE = 1;
  public static final int DEVICE_OUT_FM = 1048576;
  public static final int DEVICE_OUT_HDMI = 1024;
  public static final int DEVICE_OUT_HDMI_ARC = 262144;
  public static final int DEVICE_OUT_LINE = 131072;
  public static final int DEVICE_OUT_REMOTE_SUBMIX = 32768;
  public static final int DEVICE_OUT_SPDIF = 524288;
  public static final int DEVICE_OUT_SPEAKER = 2;
  public static final int DEVICE_OUT_TELEPHONY_TX = 65536;
  public static final int DEVICE_OUT_USB_ACCESSORY = 8192;
  public static final int DEVICE_OUT_USB_DEVICE = 16384;
  public static final int DEVICE_OUT_WIRED_HEADPHONE = 8;
  public static final int DEVICE_OUT_WIRED_HEADSET = 4;
  public static final int ERROR = -1;
  public static final int ERROR_BAD_VALUE = -2;
  public static final int ERROR_DEAD_OBJECT = -6;
  public static final int ERROR_INVALID_OPERATION = -3;
  public static final int ERROR_NO_INIT = -5;
  public static final int ERROR_PERMISSION_DENIED = -4;
  public static final String EXTRA_AUDIO_PLUG_STATE = "android.media.extra.AUDIO_PLUG_STATE";
  public static final String EXTRA_AVAILABLITY_CHANGED_VALUE = "org.codeaurora.bluetooth.EXTRA_AVAILABLITY_CHANGED_VALUE";
  public static final String EXTRA_CALLING_PACKAGE_NAME = "org.codeaurora.bluetooth.EXTRA_CALLING_PACKAGE_NAME";
  public static final String EXTRA_ENCODINGS = "android.media.extra.ENCODINGS";
  public static final String EXTRA_FOCUS_CHANGED_VALUE = "org.codeaurora.bluetooth.EXTRA_FOCUS_CHANGED_VALUE";
  public static final String EXTRA_MASTER_VOLUME_MUTED = "android.media.EXTRA_MASTER_VOLUME_MUTED";
  public static final String EXTRA_MAX_CHANNEL_COUNT = "android.media.extra.MAX_CHANNEL_COUNT";
  public static final String EXTRA_PREV_VOLUME_STREAM_DEVICES = "android.media.EXTRA_PREV_VOLUME_STREAM_DEVICES";
  public static final String EXTRA_PREV_VOLUME_STREAM_VALUE = "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE";
  public static final String EXTRA_RINGER_MODE = "android.media.EXTRA_RINGER_MODE";
  public static final String EXTRA_SCO_AUDIO_PREVIOUS_STATE = "android.media.extra.SCO_AUDIO_PREVIOUS_STATE";
  public static final String EXTRA_SCO_AUDIO_STATE = "android.media.extra.SCO_AUDIO_STATE";
  public static final String EXTRA_STREAM_VOLUME_MUTED = "android.media.EXTRA_STREAM_VOLUME_MUTED";
  public static final String EXTRA_VIBRATE_SETTING = "android.media.EXTRA_VIBRATE_SETTING";
  public static final String EXTRA_VIBRATE_TYPE = "android.media.EXTRA_VIBRATE_TYPE";
  public static final String EXTRA_VOLUME_STREAM_DEVICES = "android.media.EXTRA_VOLUME_STREAM_DEVICES";
  public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
  public static final String EXTRA_VOLUME_STREAM_TYPE_ALIAS = "android.media.EXTRA_VOLUME_STREAM_TYPE_ALIAS";
  public static final String EXTRA_VOLUME_STREAM_VALUE = "android.media.EXTRA_VOLUME_STREAM_VALUE";
  public static final int FLAG_ACTIVE_MEDIA_ONLY = 512;
  public static final int FLAG_ALLOW_RINGER_MODES = 2;
  public static final int FLAG_BLUETOOTH_ABS_VOLUME = 64;
  public static final int FLAG_FIXED_VOLUME = 32;
  public static final int FLAG_FROM_KEY = 4096;
  public static final int FLAG_HDMI_SYSTEM_AUDIO_VOLUME = 256;
  private static final String[] FLAG_NAMES;
  public static final int FLAG_PLAY_SOUND = 4;
  public static final int FLAG_REMOVE_SOUND_AND_VIBRATE = 8;
  public static final int FLAG_SHOW_SILENT_HINT = 128;
  public static final int FLAG_SHOW_UI = 1;
  public static final int FLAG_SHOW_UI_WARNINGS = 1024;
  public static final int FLAG_SHOW_VIBRATE_HINT = 2048;
  public static final int FLAG_VIBRATE = 16;
  public static final int FX_FOCUS_NAVIGATION_DOWN = 2;
  public static final int FX_FOCUS_NAVIGATION_LEFT = 3;
  public static final int FX_FOCUS_NAVIGATION_RIGHT = 4;
  public static final int FX_FOCUS_NAVIGATION_UP = 1;
  public static final int FX_KEYPRESS_DELETE = 7;
  public static final int FX_KEYPRESS_INVALID = 9;
  public static final int FX_KEYPRESS_RETURN = 8;
  public static final int FX_KEYPRESS_SPACEBAR = 6;
  public static final int FX_KEYPRESS_STANDARD = 5;
  public static final int FX_KEY_CLICK = 0;
  public static final int GET_DEVICES_ALL = 3;
  public static final int GET_DEVICES_INPUTS = 1;
  public static final int GET_DEVICES_OUTPUTS = 2;
  public static final String INTERNAL_RINGER_MODE_CHANGED_ACTION = "android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION";
  public static final String King_of_Glory_PACKAGE_NAME = "com.tencent.tmgp.sgame";
  public static final String MASTER_MUTE_CHANGED_ACTION = "android.media.MASTER_MUTE_CHANGED_ACTION";
  public static final int MODE_CURRENT = -1;
  public static final int MODE_INVALID = -2;
  public static final int MODE_IN_CALL = 2;
  public static final int MODE_IN_COMMUNICATION = 3;
  public static final int MODE_NORMAL = 0;
  public static final int MODE_RINGTONE = 1;
  private static final int MSG_DEVICES_CALLBACK_REGISTERED = 0;
  private static final int MSG_DEVICES_DEVICES_ADDED = 1;
  private static final int MSG_DEVICES_DEVICES_REMOVED = 2;
  private static final int MSSG_FOCUS_CHANGE = 0;
  private static final int MSSG_RECORDING_CONFIG_CHANGE = 1;
  public static final int NUM_SOUND_EFFECTS = 10;
  @Deprecated
  public static final int NUM_STREAMS = 5;
  public static final String PROPERTY_OUTPUT_FRAMES_PER_BUFFER = "android.media.property.OUTPUT_FRAMES_PER_BUFFER";
  public static final String PROPERTY_OUTPUT_SAMPLE_RATE = "android.media.property.OUTPUT_SAMPLE_RATE";
  public static final String PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED = "android.media.property.SUPPORT_AUDIO_SOURCE_UNPROCESSED";
  public static final String PROPERTY_SUPPORT_MIC_NEAR_ULTRASOUND = "android.media.property.SUPPORT_MIC_NEAR_ULTRASOUND";
  public static final String PROPERTY_SUPPORT_SPEAKER_NEAR_ULTRASOUND = "android.media.property.SUPPORT_SPEAKER_NEAR_ULTRASOUND";
  public static final String RCC_CHANGED_ACTION = "org.codeaurora.bluetooth.RCC_CHANGED_ACTION";
  public static final int RECORD_CONFIG_EVENT_START = 1;
  public static final int RECORD_CONFIG_EVENT_STOP = 0;
  public static final String RINGER_MODE_CHANGED_ACTION = "android.media.RINGER_MODE_CHANGED";
  public static final int RINGER_MODE_MAX = 2;
  public static final int RINGER_MODE_NORMAL = 2;
  public static final int RINGER_MODE_SILENT = 0;
  public static final int RINGER_MODE_VIBRATE = 1;
  @Deprecated
  public static final int ROUTE_ALL = -1;
  @Deprecated
  public static final int ROUTE_BLUETOOTH = 4;
  @Deprecated
  public static final int ROUTE_BLUETOOTH_A2DP = 16;
  @Deprecated
  public static final int ROUTE_BLUETOOTH_SCO = 4;
  @Deprecated
  public static final int ROUTE_EARPIECE = 1;
  @Deprecated
  public static final int ROUTE_HEADSET = 8;
  @Deprecated
  public static final int ROUTE_SPEAKER = 2;
  public static final int SCO_AUDIO_STATE_CONNECTED = 1;
  public static final int SCO_AUDIO_STATE_CONNECTING = 2;
  public static final int SCO_AUDIO_STATE_DISCONNECTED = 0;
  public static final int SCO_AUDIO_STATE_ERROR = -1;
  public static final int STREAM_ALARM = 4;
  public static final int STREAM_BLUETOOTH_SCO = 6;
  public static final String STREAM_DEVICES_CHANGED_ACTION = "android.media.STREAM_DEVICES_CHANGED_ACTION";
  public static final int STREAM_DTMF = 8;
  public static final int STREAM_MUSIC = 3;
  public static final String STREAM_MUTE_CHANGED_ACTION = "android.media.STREAM_MUTE_CHANGED_ACTION";
  public static final int STREAM_NOTIFICATION = 5;
  public static final int STREAM_RING = 2;
  public static final int STREAM_SYSTEM = 1;
  public static final int STREAM_SYSTEM_ENFORCED = 7;
  public static final int STREAM_TTS = 9;
  public static final int STREAM_VOICE_CALL = 0;
  public static final int SUCCESS = 0;
  private static String TAG = "AudioManager";
  public static final int USE_DEFAULT_STREAM_TYPE = Integer.MIN_VALUE;
  public static final String VIBRATE_SETTING_CHANGED_ACTION = "android.media.VIBRATE_SETTING_CHANGED";
  public static final int VIBRATE_SETTING_OFF = 0;
  public static final int VIBRATE_SETTING_ON = 1;
  public static final int VIBRATE_SETTING_ONLY_SILENT = 2;
  public static final int VIBRATE_TYPE_NOTIFICATION = 1;
  public static final int VIBRATE_TYPE_RINGER = 0;
  public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
  static ArrayList<AudioPatch> sAudioPatchesCached = new ArrayList();
  private static final AudioPortEventHandler sAudioPortEventHandler = new AudioPortEventHandler();
  static Integer sAudioPortGeneration;
  static ArrayList<AudioPort> sAudioPortsCached;
  static ArrayList<AudioPort> sPreviousAudioPortsCached;
  private static IAudioService sService;
  private Context mApplicationContext;
  private final IAudioFocusDispatcher mAudioFocusDispatcher = new IAudioFocusDispatcher.Stub()
  {
    public void dispatchAudioFocusChange(int paramAnonymousInt, String paramAnonymousString)
    {
      paramAnonymousString = AudioManager.-get4(AudioManager.this).getHandler().obtainMessage(0, paramAnonymousInt, 0, paramAnonymousString);
      AudioManager.-get4(AudioManager.this).getHandler().sendMessage(paramAnonymousString);
    }
  };
  private final HashMap<String, OnAudioFocusChangeListener> mAudioFocusIdListenerMap = new HashMap();
  private ArrayMap<AudioDeviceCallback, NativeEventHandlerDelegate> mDeviceCallbacks = new ArrayMap();
  private final Object mFocusListenerLock = new Object();
  private final IBinder mICallBack = new Binder();
  private Context mOriginalContext;
  private OnAmPortUpdateListener mPortListener = null;
  private ArrayList<AudioDevicePort> mPreviousPorts = new ArrayList();
  private final IRecordingConfigDispatcher mRecCb = new IRecordingConfigDispatcher.Stub()
  {
    public void dispatchRecordingConfigChange(List<AudioRecordingConfiguration> paramAnonymousList)
    {
      synchronized (AudioManager.-get3(AudioManager.this))
      {
        if (AudioManager.-get2(AudioManager.this) != null)
        {
          int i = 0;
          while (i < AudioManager.-get2(AudioManager.this).size())
          {
            AudioManager.AudioRecordingCallbackInfo localAudioRecordingCallbackInfo = (AudioManager.AudioRecordingCallbackInfo)AudioManager.-get2(AudioManager.this).get(i);
            if (localAudioRecordingCallbackInfo.mHandler != null)
            {
              Message localMessage = localAudioRecordingCallbackInfo.mHandler.obtainMessage(1, new AudioManager.RecordConfigChangeCallbackData(localAudioRecordingCallbackInfo.mCb, paramAnonymousList));
              localAudioRecordingCallbackInfo.mHandler.sendMessage(localMessage);
            }
            i += 1;
          }
        }
        return;
      }
    }
  };
  private List<AudioRecordingCallbackInfo> mRecordCallbackList;
  private final Object mRecordCallbackLock = new Object();
  private final ServiceEventHandlerDelegate mServiceEventHandlerDelegate = new ServiceEventHandlerDelegate(null);
  private final boolean mUseFixedVolume;
  private final boolean mUseVolumeKeySounds;
  private long mVolumeKeyUpTime;
  
  static
  {
    FLAG_NAMES = new String[] { "FLAG_SHOW_UI", "FLAG_ALLOW_RINGER_MODES", "FLAG_PLAY_SOUND", "FLAG_REMOVE_SOUND_AND_VIBRATE", "FLAG_VIBRATE", "FLAG_FIXED_VOLUME", "FLAG_BLUETOOTH_ABS_VOLUME", "FLAG_SHOW_SILENT_HINT", "FLAG_HDMI_SYSTEM_AUDIO_VOLUME", "FLAG_ACTIVE_MEDIA_ONLY", "FLAG_SHOW_UI_WARNINGS", "FLAG_SHOW_VIBRATE_HINT", "FLAG_FROM_KEY" };
    sAudioPortGeneration = new Integer(0);
    sAudioPortsCached = new ArrayList();
    sPreviousAudioPortsCached = new ArrayList();
  }
  
  public AudioManager(Context paramContext)
  {
    setContext(paramContext);
    this.mUseVolumeKeySounds = getContext().getResources().getBoolean(17956879);
    this.mUseFixedVolume = getContext().getResources().getBoolean(17956998);
  }
  
  private void broadcastDeviceListChange(Handler paramHandler)
  {
    ArrayList localArrayList = new ArrayList();
    if (listAudioDevicePorts(localArrayList) != 0) {
      return;
    }
    if (paramHandler != null) {
      paramHandler.sendMessage(Message.obtain(paramHandler, 0, infoListFromPortList(localArrayList, 3)));
    }
    for (;;)
    {
      this.mPreviousPorts = localArrayList;
      return;
      AudioDeviceInfo[] arrayOfAudioDeviceInfo1 = calcListDeltas(this.mPreviousPorts, localArrayList, 3);
      AudioDeviceInfo[] arrayOfAudioDeviceInfo2 = calcListDeltas(localArrayList, this.mPreviousPorts, 3);
      if ((arrayOfAudioDeviceInfo1.length == 0) && (arrayOfAudioDeviceInfo2.length == 0)) {
        continue;
      }
      paramHandler = this.mDeviceCallbacks;
      int i = 0;
      try
      {
        while (i < this.mDeviceCallbacks.size())
        {
          Handler localHandler = ((NativeEventHandlerDelegate)this.mDeviceCallbacks.valueAt(i)).getHandler();
          if (localHandler != null)
          {
            if (arrayOfAudioDeviceInfo1.length != 0) {
              localHandler.sendMessage(Message.obtain(localHandler, 1, arrayOfAudioDeviceInfo1));
            }
            if (arrayOfAudioDeviceInfo2.length != 0) {
              localHandler.sendMessage(Message.obtain(localHandler, 2, arrayOfAudioDeviceInfo2));
            }
          }
          i += 1;
        }
      }
      finally {}
    }
  }
  
  private static AudioDeviceInfo[] calcListDeltas(ArrayList<AudioDevicePort> paramArrayList1, ArrayList<AudioDevicePort> paramArrayList2, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    int j = 0;
    while (j < paramArrayList2.size())
    {
      int i = 0;
      AudioDevicePort localAudioDevicePort = (AudioDevicePort)paramArrayList2.get(j);
      int k = 0;
      if ((k >= paramArrayList1.size()) || (i != 0))
      {
        if (i == 0) {
          localArrayList.add(localAudioDevicePort);
        }
        j += 1;
      }
      else
      {
        if (localAudioDevicePort.id() == ((AudioDevicePort)paramArrayList1.get(k)).id()) {}
        for (i = 1;; i = 0)
        {
          k += 1;
          break;
        }
      }
    }
    return infoListFromPortList(localArrayList, paramInt);
  }
  
  private static boolean checkFlags(AudioDevicePort paramAudioDevicePort, int paramInt)
  {
    if ((paramAudioDevicePort.role() == 2) && ((paramInt & 0x2) != 0)) {}
    while ((paramAudioDevicePort.role() == 1) && ((paramInt & 0x1) != 0)) {
      return true;
    }
    return false;
  }
  
  private static boolean checkTypes(AudioDevicePort paramAudioDevicePort)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (AudioDeviceInfo.convertInternalDeviceToDeviceType(paramAudioDevicePort.type()) != 0)
    {
      bool1 = bool2;
      if (paramAudioDevicePort.type() != -2147483520) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static int createAudioPatch(AudioPatch[] paramArrayOfAudioPatch, AudioPortConfig[] paramArrayOfAudioPortConfig1, AudioPortConfig[] paramArrayOfAudioPortConfig2)
  {
    return AudioSystem.createAudioPatch(paramArrayOfAudioPatch, paramArrayOfAudioPortConfig1, paramArrayOfAudioPortConfig2);
  }
  
  private static void filterDevicePorts(ArrayList<AudioPort> paramArrayList, ArrayList<AudioDevicePort> paramArrayList1)
  {
    paramArrayList1.clear();
    int i = 0;
    while (i < paramArrayList.size())
    {
      if ((paramArrayList.get(i) instanceof AudioDevicePort)) {
        paramArrayList1.add((AudioDevicePort)paramArrayList.get(i));
      }
      i += 1;
    }
  }
  
  private OnAudioFocusChangeListener findFocusListener(String paramString)
  {
    return (OnAudioFocusChangeListener)this.mAudioFocusIdListenerMap.get(paramString);
  }
  
  public static String flagsToString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int j = 0;
    int i = paramInt;
    paramInt = j;
    while (paramInt < FLAG_NAMES.length)
    {
      int k = 1 << paramInt;
      j = i;
      if ((i & k) != 0)
      {
        if (localStringBuilder.length() > 0) {
          localStringBuilder.append(',');
        }
        localStringBuilder.append(FLAG_NAMES[paramInt]);
        j = i & k;
      }
      paramInt += 1;
      i = j;
    }
    if (i != 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(',');
      }
      localStringBuilder.append(i);
    }
    return localStringBuilder.toString();
  }
  
  private Context getContext()
  {
    if (this.mApplicationContext == null) {
      setContext(this.mOriginalContext);
    }
    if (this.mApplicationContext != null) {
      return this.mApplicationContext;
    }
    return this.mOriginalContext;
  }
  
  public static AudioDeviceInfo[] getDevicesStatic(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    if (listAudioDevicePorts(localArrayList) != 0) {
      return new AudioDeviceInfo[0];
    }
    return infoListFromPortList(localArrayList, paramInt);
  }
  
  private String getIdForAudioFocusListener(OnAudioFocusChangeListener paramOnAudioFocusChangeListener)
  {
    if (paramOnAudioFocusChangeListener == null) {
      return new String(toString());
    }
    return new String(toString() + paramOnAudioFocusChangeListener.toString());
  }
  
  private static IAudioService getService()
  {
    if (sService != null) {
      return sService;
    }
    sService = IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
    return sService;
  }
  
  private boolean hasRecordCallback_sync(AudioRecordingCallback paramAudioRecordingCallback)
  {
    if (this.mRecordCallbackList != null)
    {
      int i = 0;
      while (i < this.mRecordCallbackList.size())
      {
        if (paramAudioRecordingCallback.equals(((AudioRecordingCallbackInfo)this.mRecordCallbackList.get(i)).mCb)) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  private static AudioDeviceInfo[] infoListFromPortList(ArrayList<AudioDevicePort> paramArrayList, int paramInt)
  {
    int i = 0;
    Object localObject = paramArrayList.iterator();
    AudioDevicePort localAudioDevicePort;
    while (((Iterator)localObject).hasNext())
    {
      localAudioDevicePort = (AudioDevicePort)((Iterator)localObject).next();
      if ((checkTypes(localAudioDevicePort)) && (checkFlags(localAudioDevicePort, paramInt))) {
        i += 1;
      }
    }
    localObject = new AudioDeviceInfo[i];
    i = 0;
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      localAudioDevicePort = (AudioDevicePort)paramArrayList.next();
      if ((checkTypes(localAudioDevicePort)) && (checkFlags(localAudioDevicePort, paramInt)))
      {
        localObject[i] = new AudioDeviceInfo(localAudioDevicePort);
        i += 1;
      }
    }
    return (AudioDeviceInfo[])localObject;
  }
  
  public static boolean isInputDevice(int paramInt)
  {
    return (paramInt & 0x80000000) == Integer.MIN_VALUE;
  }
  
  public static boolean isOutputDevice(int paramInt)
  {
    boolean bool = false;
    if ((0x80000000 & paramInt) == 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isValidRingerMode(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 2)) {
      return false;
    }
    IAudioService localIAudioService = getService();
    try
    {
      boolean bool = localIAudioService.isValidRingerMode(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public static int listAudioDevicePorts(ArrayList<AudioDevicePort> paramArrayList)
  {
    if (paramArrayList == null) {
      return -2;
    }
    ArrayList localArrayList = new ArrayList();
    int i = updateAudioPortCache(localArrayList, null, null);
    if (i == 0) {
      filterDevicePorts(localArrayList, paramArrayList);
    }
    return i;
  }
  
  public static int listAudioPatches(ArrayList<AudioPatch> paramArrayList)
  {
    return updateAudioPortCache(null, paramArrayList, null);
  }
  
  public static int listAudioPorts(ArrayList<AudioPort> paramArrayList)
  {
    return updateAudioPortCache(paramArrayList, null, null);
  }
  
  public static int listPreviousAudioDevicePorts(ArrayList<AudioDevicePort> paramArrayList)
  {
    if (paramArrayList == null) {
      return -2;
    }
    ArrayList localArrayList = new ArrayList();
    int i = updateAudioPortCache(null, null, localArrayList);
    if (i == 0) {
      filterDevicePorts(localArrayList, paramArrayList);
    }
    return i;
  }
  
  public static int listPreviousAudioPorts(ArrayList<AudioPort> paramArrayList)
  {
    return updateAudioPortCache(null, null, paramArrayList);
  }
  
  private boolean querySoundEffectsEnabled(int paramInt)
  {
    boolean bool = false;
    if (Settings.System.getIntForUser(getContext().getContentResolver(), "sound_effects_enabled", 0, paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static int releaseAudioPatch(AudioPatch paramAudioPatch)
  {
    return AudioSystem.releaseAudioPatch(paramAudioPatch);
  }
  
  private boolean removeRecordCallback_sync(AudioRecordingCallback paramAudioRecordingCallback)
  {
    if (this.mRecordCallbackList != null)
    {
      int i = 0;
      while (i < this.mRecordCallbackList.size())
      {
        if (paramAudioRecordingCallback.equals(((AudioRecordingCallbackInfo)this.mRecordCallbackList.get(i)).mCb))
        {
          this.mRecordCallbackList.remove(i);
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  static int resetAudioPortGeneration()
  {
    synchronized (sAudioPortGeneration)
    {
      int i = sAudioPortGeneration.intValue();
      sAudioPortGeneration = Integer.valueOf(0);
      return i;
    }
  }
  
  public static int setAudioPortGain(AudioPort paramAudioPort, AudioGainConfig paramAudioGainConfig)
  {
    if ((paramAudioPort == null) || (paramAudioGainConfig == null)) {
      return -2;
    }
    AudioPortConfig localAudioPortConfig = paramAudioPort.activeConfig();
    paramAudioPort = new AudioPortConfig(paramAudioPort, localAudioPortConfig.samplingRate(), localAudioPortConfig.channelMask(), localAudioPortConfig.format(), paramAudioGainConfig);
    paramAudioPort.mConfigMask = 8;
    return AudioSystem.setAudioPortConfig(paramAudioPort);
  }
  
  private void setContext(Context paramContext)
  {
    this.mApplicationContext = paramContext.getApplicationContext();
    if (this.mApplicationContext != null)
    {
      this.mOriginalContext = null;
      return;
    }
    this.mOriginalContext = paramContext;
  }
  
  static int updateAudioPortCache(ArrayList<AudioPort> paramArrayList1, ArrayList<AudioPatch> paramArrayList, ArrayList<AudioPort> paramArrayList2)
  {
    sAudioPortEventHandler.init();
    for (;;)
    {
      int[] arrayOfInt;
      ArrayList localArrayList1;
      ArrayList localArrayList2;
      int i;
      label206:
      Object localObject2;
      int k;
      AudioPortConfig[] arrayOfAudioPortConfig;
      int m;
      synchronized (sAudioPortGeneration)
      {
        if (sAudioPortGeneration.intValue() != 0) {
          break label405;
        }
        Object localObject1 = new int[1];
        arrayOfInt = new int[1];
        localArrayList1 = new ArrayList();
        localArrayList2 = new ArrayList();
        localArrayList1.clear();
        i = AudioSystem.listAudioPorts(localArrayList1, arrayOfInt);
        if (i != 0)
        {
          Log.w(TAG, "updateAudioPortCache: listAudioPorts failed");
          return i;
        }
        localArrayList2.clear();
        i = AudioSystem.listAudioPatches(localArrayList2, (int[])localObject1);
        if (i != 0)
        {
          Log.w(TAG, "updateAudioPortCache: listAudioPatches failed");
          return i;
        }
        if (localObject1[0] != arrayOfInt[0]) {
          continue;
        }
        i = 0;
        if (i < localArrayList2.size())
        {
          j = 0;
          if (j >= ((AudioPatch)localArrayList2.get(i)).sources().length) {
            break label458;
          }
          localObject1 = updatePortConfig(((AudioPatch)localArrayList2.get(i)).sources()[j], localArrayList1);
          ((AudioPatch)localArrayList2.get(i)).sources()[j] = localObject1;
          j += 1;
          continue;
          if (j >= ((AudioPatch)localArrayList2.get(i)).sinks().length) {
            break label464;
          }
          localObject1 = updatePortConfig(((AudioPatch)localArrayList2.get(i)).sinks()[j], localArrayList1);
          ((AudioPatch)localArrayList2.get(i)).sinks()[j] = localObject1;
          j += 1;
          continue;
        }
        localObject1 = localArrayList2.iterator();
        if (!((Iterator)localObject1).hasNext()) {
          break label379;
        }
        localObject2 = (AudioPatch)((Iterator)localObject1).next();
        k = 0;
        arrayOfAudioPortConfig = ((AudioPatch)localObject2).sources();
        j = 0;
        m = arrayOfAudioPortConfig.length;
        break label471;
        label322:
        localObject2 = ((AudioPatch)localObject2).sinks();
        k = 0;
        m = localObject2.length;
        break label494;
        label340:
        if (j == 0) {
          continue;
        }
        ((Iterator)localObject1).remove();
      }
      label361:
      j += 1;
      label379:
      label405:
      label458:
      label464:
      label471:
      label494:
      do
      {
        k += 1;
        break label494;
        sPreviousAudioPortsCached = sAudioPortsCached;
        sAudioPortsCached = localArrayList1;
        sAudioPatchesCached = localArrayList2;
        sAudioPortGeneration = Integer.valueOf(arrayOfInt[0]);
        if (paramArrayList1 != null)
        {
          paramArrayList1.clear();
          paramArrayList1.addAll(sAudioPortsCached);
        }
        if (paramArrayList != null)
        {
          paramArrayList.clear();
          paramArrayList.addAll(sAudioPatchesCached);
        }
        if (paramArrayList2 != null)
        {
          paramArrayList2.clear();
          paramArrayList2.addAll(sPreviousAudioPortsCached);
        }
        return 0;
        j = 0;
        break label206;
        i += 1;
        break;
        i = k;
        if (j >= m) {
          break label322;
        }
        if (arrayOfAudioPortConfig[j] != null) {
          break label361;
        }
        i = 1;
        break label322;
        j = i;
        if (k >= m) {
          break label340;
        }
      } while (localObject2[k] != null);
      int j = 1;
    }
  }
  
  static AudioPortConfig updatePortConfig(AudioPortConfig paramAudioPortConfig, ArrayList<AudioPort> paramArrayList)
  {
    Object localObject2 = paramAudioPortConfig.port();
    int i = 0;
    Object localObject1;
    for (;;)
    {
      localObject1 = localObject2;
      if (i < paramArrayList.size())
      {
        if (((AudioPort)paramArrayList.get(i)).handle().equals(((AudioPort)localObject2).handle())) {
          localObject1 = (AudioPort)paramArrayList.get(i);
        }
      }
      else
      {
        if (i != paramArrayList.size()) {
          break;
        }
        Log.e(TAG, "updatePortConfig port not found for handle: " + ((AudioPort)localObject1).handle().id());
        return null;
      }
      i += 1;
    }
    localObject2 = paramAudioPortConfig.gain();
    paramArrayList = (ArrayList<AudioPort>)localObject2;
    if (localObject2 != null) {
      paramArrayList = ((AudioPort)localObject1).gain(((AudioGainConfig)localObject2).index()).buildConfig(((AudioGainConfig)localObject2).mode(), ((AudioGainConfig)localObject2).channelMask(), ((AudioGainConfig)localObject2).values(), ((AudioGainConfig)localObject2).rampDurationMs());
    }
    return ((AudioPort)localObject1).buildConfig(paramAudioPortConfig.samplingRate(), paramAudioPortConfig.channelMask(), paramAudioPortConfig.format(), paramArrayList);
  }
  
  public int abandonAudioFocus(OnAudioFocusChangeListener paramOnAudioFocusChangeListener)
  {
    return abandonAudioFocus(paramOnAudioFocusChangeListener, null);
  }
  
  public int abandonAudioFocus(OnAudioFocusChangeListener paramOnAudioFocusChangeListener, AudioAttributes paramAudioAttributes)
  {
    unregisterAudioFocusListener(paramOnAudioFocusChangeListener);
    IAudioService localIAudioService = getService();
    try
    {
      int i = localIAudioService.abandonAudioFocus(this.mAudioFocusDispatcher, getIdForAudioFocusListener(paramOnAudioFocusChangeListener), paramAudioAttributes);
      return i;
    }
    catch (RemoteException paramOnAudioFocusChangeListener)
    {
      throw paramOnAudioFocusChangeListener.rethrowFromSystemServer();
    }
  }
  
  public void abandonAudioFocusForCall()
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.abandonAudioFocus(null, "AudioFocus_For_Phone_Ring_And_Calls", null);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void adjustStreamVolume(int paramInt1, int paramInt2, int paramInt3)
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.adjustStreamVolume(paramInt1, paramInt2, paramInt3, getContext().getOpPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void adjustSuggestedStreamVolume(int paramInt1, int paramInt2, int paramInt3)
  {
    MediaSessionLegacyHelper.getHelper(getContext()).sendAdjustVolumeBy(paramInt2, paramInt1, paramInt3);
  }
  
  public void adjustVolume(int paramInt1, int paramInt2)
  {
    MediaSessionLegacyHelper.getHelper(getContext()).sendAdjustVolumeBy(Integer.MIN_VALUE, paramInt1, paramInt2);
  }
  
  public void avrcpSupportsAbsoluteVolume(String paramString, boolean paramBoolean)
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.avrcpSupportsAbsoluteVolume(paramString, paramBoolean);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void disableSafeMediaVolume()
  {
    try
    {
      getService().disableSafeMediaVolume(this.mApplicationContext.getOpPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void dispatchMediaKeyEvent(KeyEvent paramKeyEvent)
  {
    MediaSessionLegacyHelper.getHelper(getContext()).sendMediaButtonEvent(paramKeyEvent, false);
  }
  
  public void forceVolumeControlStream(int paramInt)
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.forceVolumeControlStream(paramInt, this.mICallBack);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int generateAudioSessionId()
  {
    int i = AudioSystem.newAudioSessionId();
    if (i > 0) {
      return i;
    }
    Log.e(TAG, "Failure to generate a new audio session ID");
    return -1;
  }
  
  public List<AudioRecordingConfiguration> getActiveRecordingConfigurations()
  {
    Object localObject = getService();
    try
    {
      localObject = ((IAudioService)localObject).getActiveRecordingConfigurations();
      return (List<AudioRecordingConfiguration>)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public AudioDeviceInfo[] getDevices(int paramInt)
  {
    return getDevicesStatic(paramInt);
  }
  
  public int getDevicesForStream(int paramInt)
  {
    switch (paramInt)
    {
    case 6: 
    case 7: 
    default: 
      return 0;
    }
    return AudioSystem.getDevicesForStream(paramInt);
  }
  
  public int getLastAudibleStreamVolume(int paramInt)
  {
    IAudioService localIAudioService = getService();
    try
    {
      paramInt = localIAudioService.getLastAudibleStreamVolume(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getMode()
  {
    IAudioService localIAudioService = getService();
    try
    {
      int i = localIAudioService.getMode();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getOutputLatency(int paramInt)
  {
    return AudioSystem.getOutputLatency(paramInt);
  }
  
  public String getParameters(String paramString)
  {
    return AudioSystem.getParameters(paramString);
  }
  
  public String getProperty(String paramString)
  {
    Object localObject2 = null;
    Object localObject1 = null;
    int i;
    if ("android.media.property.OUTPUT_SAMPLE_RATE".equals(paramString))
    {
      i = AudioSystem.getPrimaryOutputSamplingRate();
      paramString = (String)localObject1;
      if (i > 0) {
        paramString = Integer.toString(i);
      }
      return paramString;
    }
    if ("android.media.property.OUTPUT_FRAMES_PER_BUFFER".equals(paramString))
    {
      i = AudioSystem.getPrimaryOutputFrameCount();
      paramString = (String)localObject2;
      if (i > 0) {
        paramString = Integer.toString(i);
      }
      return paramString;
    }
    if ("android.media.property.SUPPORT_MIC_NEAR_ULTRASOUND".equals(paramString)) {
      return String.valueOf(getContext().getResources().getBoolean(17957034));
    }
    if ("android.media.property.SUPPORT_SPEAKER_NEAR_ULTRASOUND".equals(paramString)) {
      return String.valueOf(getContext().getResources().getBoolean(17957035));
    }
    if ("android.media.property.SUPPORT_AUDIO_SOURCE_UNPROCESSED".equals(paramString)) {
      return String.valueOf(getContext().getResources().getBoolean(17957036));
    }
    return null;
  }
  
  public int getRingerMode()
  {
    IAudioService localIAudioService = getService();
    try
    {
      int i = localIAudioService.getRingerModeExternal();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getRingerModeInternal()
  {
    try
    {
      int i = getService().getRingerModeInternal();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public IRingtonePlayer getRingtonePlayer()
  {
    try
    {
      IRingtonePlayer localIRingtonePlayer = getService().getRingtonePlayer();
      return localIRingtonePlayer;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public int getRouting(int paramInt)
  {
    return -1;
  }
  
  public int getStreamMaxVolume(int paramInt)
  {
    IAudioService localIAudioService = getService();
    try
    {
      paramInt = localIAudioService.getStreamMaxVolume(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getStreamMinVolume(int paramInt)
  {
    IAudioService localIAudioService = getService();
    try
    {
      paramInt = localIAudioService.getStreamMinVolume(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getStreamVolume(int paramInt)
  {
    IAudioService localIAudioService = getService();
    try
    {
      paramInt = localIAudioService.getStreamVolume(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getUiSoundsStreamType()
  {
    IAudioService localIAudioService = getService();
    try
    {
      int i = localIAudioService.getUiSoundsStreamType();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getVibrateSetting(int paramInt)
  {
    IAudioService localIAudioService = getService();
    try
    {
      paramInt = localIAudioService.getVibrateSetting(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void handleKeyDown(KeyEvent paramKeyEvent, int paramInt)
  {
    int i = paramKeyEvent.getKeyCode();
    switch (i)
    {
    }
    do
    {
      return;
      if (i == 24) {}
      for (i = 1;; i = -1)
      {
        adjustSuggestedStreamVolume(i, paramInt, 17);
        return;
      }
    } while (paramKeyEvent.getRepeatCount() != 0);
    MediaSessionLegacyHelper.getHelper(getContext()).sendVolumeKeyEvent(paramKeyEvent, false);
  }
  
  public void handleKeyUp(KeyEvent paramKeyEvent, int paramInt)
  {
    switch (paramKeyEvent.getKeyCode())
    {
    default: 
      return;
    case 24: 
    case 25: 
      if (this.mUseVolumeKeySounds) {
        adjustSuggestedStreamVolume(0, paramInt, 4);
      }
      this.mVolumeKeyUpTime = SystemClock.uptimeMillis();
      return;
    }
    MediaSessionLegacyHelper.getHelper(getContext()).sendVolumeKeyEvent(paramKeyEvent, false);
  }
  
  public boolean isAudioFocusExclusive()
  {
    IAudioService localIAudioService = getService();
    try
    {
      int i = localIAudioService.getCurrentAudioFocus();
      return i == 4;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isBluetoothA2dpOn()
  {
    if (AudioSystem.getDeviceConnectionState(128, "") == 1) {
      return true;
    }
    if (AudioSystem.getDeviceConnectionState(256, "") == 1) {
      return true;
    }
    return AudioSystem.getDeviceConnectionState(512, "") == 1;
  }
  
  public boolean isBluetoothScoAvailableOffCall()
  {
    return getContext().getResources().getBoolean(17956952);
  }
  
  public boolean isBluetoothScoOn()
  {
    IAudioService localIAudioService = getService();
    try
    {
      boolean bool = localIAudioService.isBluetoothScoOn();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isHdmiSystemAudioSupported()
  {
    try
    {
      boolean bool = getService().isHdmiSystemAudioSupported();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isMasterMute()
  {
    IAudioService localIAudioService = getService();
    try
    {
      boolean bool = localIAudioService.isMasterMute();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isMicrophoneMute()
  {
    return AudioSystem.isMicrophoneMuted();
  }
  
  public boolean isMusicActive()
  {
    return AudioSystem.isStreamActive(3, 0);
  }
  
  public boolean isMusicActiveRemotely()
  {
    return AudioSystem.isStreamActiveRemotely(3, 0);
  }
  
  public boolean isSilentMode()
  {
    int i = getRingerMode();
    return (i == 0) || (i == 1);
  }
  
  public boolean isSpeakerphoneOn()
  {
    IAudioService localIAudioService = getService();
    try
    {
      boolean bool = localIAudioService.isSpeakerphoneOn();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isStreamAffectedByMute(int paramInt)
  {
    try
    {
      boolean bool = getService().isStreamAffectedByMute(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isStreamAffectedByRingerMode(int paramInt)
  {
    try
    {
      boolean bool = getService().isStreamAffectedByRingerMode(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isStreamMute(int paramInt)
  {
    IAudioService localIAudioService = getService();
    try
    {
      boolean bool = localIAudioService.isStreamMute(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isVolumeFixed()
  {
    return this.mUseFixedVolume;
  }
  
  public boolean isWiredHeadsetOn()
  {
    return (AudioSystem.getDeviceConnectionState(4, "") != 0) || (AudioSystem.getDeviceConnectionState(8, "") != 0);
  }
  
  public void loadSoundEffects()
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.loadSoundEffects();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void notifyVolumeControllerVisible(IVolumeController paramIVolumeController, boolean paramBoolean)
  {
    try
    {
      getService().notifyVolumeControllerVisible(paramIVolumeController, paramBoolean);
      return;
    }
    catch (RemoteException paramIVolumeController)
    {
      throw paramIVolumeController.rethrowFromSystemServer();
    }
  }
  
  public void playSoundEffect(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 10)) {
      return;
    }
    if (!querySoundEffectsEnabled(Process.myUserHandle().getIdentifier())) {
      return;
    }
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.playSoundEffect(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void playSoundEffect(int paramInt, float paramFloat)
  {
    if ((paramInt < 0) || (paramInt >= 10)) {
      return;
    }
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.playSoundEffectVolume(paramInt, paramFloat);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void playSoundEffect(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 >= 10)) {
      return;
    }
    if (!querySoundEffectsEnabled(paramInt2)) {
      return;
    }
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.playSoundEffect(paramInt1);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void preDispatchKeyEvent(KeyEvent paramKeyEvent, int paramInt)
  {
    int i = paramKeyEvent.getKeyCode();
    if ((i != 25) && (i != 24) && (i != 164) && (this.mVolumeKeyUpTime + 300L > SystemClock.uptimeMillis())) {
      adjustSuggestedStreamVolume(0, paramInt, 8);
    }
  }
  
  /* Error */
  public void registerAudioDeviceCallback(AudioDeviceCallback paramAudioDeviceCallback, Handler paramHandler)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 501	android/media/AudioManager:mDeviceCallbacks	Landroid/util/ArrayMap;
    //   4: astore 4
    //   6: aload 4
    //   8: monitorenter
    //   9: aload_1
    //   10: ifnull +16 -> 26
    //   13: aload_0
    //   14: getfield 501	android/media/AudioManager:mDeviceCallbacks	Landroid/util/ArrayMap;
    //   17: aload_1
    //   18: invokevirtual 1115	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   21: istore_3
    //   22: iload_3
    //   23: ifeq +7 -> 30
    //   26: aload 4
    //   28: monitorexit
    //   29: return
    //   30: aload_0
    //   31: getfield 501	android/media/AudioManager:mDeviceCallbacks	Landroid/util/ArrayMap;
    //   34: invokevirtual 556	android/util/ArrayMap:size	()I
    //   37: ifne +31 -> 68
    //   40: aload_0
    //   41: getfield 496	android/media/AudioManager:mPortListener	Landroid/media/AudioManager$OnAmPortUpdateListener;
    //   44: ifnonnull +16 -> 60
    //   47: aload_0
    //   48: new 21	android/media/AudioManager$OnAmPortUpdateListener
    //   51: dup
    //   52: aload_0
    //   53: aconst_null
    //   54: invokespecial 1118	android/media/AudioManager$OnAmPortUpdateListener:<init>	(Landroid/media/AudioManager;Landroid/media/AudioManager$OnAmPortUpdateListener;)V
    //   57: putfield 496	android/media/AudioManager:mPortListener	Landroid/media/AudioManager$OnAmPortUpdateListener;
    //   60: aload_0
    //   61: aload_0
    //   62: getfield 496	android/media/AudioManager:mPortListener	Landroid/media/AudioManager$OnAmPortUpdateListener;
    //   65: invokevirtual 1122	android/media/AudioManager:registerAudioPortUpdateListener	(Landroid/media/AudioManager$OnAudioPortUpdateListener;)V
    //   68: new 16	android/media/AudioManager$NativeEventHandlerDelegate
    //   71: dup
    //   72: aload_0
    //   73: aload_1
    //   74: aload_2
    //   75: invokespecial 1125	android/media/AudioManager$NativeEventHandlerDelegate:<init>	(Landroid/media/AudioManager;Landroid/media/AudioDeviceCallback;Landroid/os/Handler;)V
    //   78: astore_2
    //   79: aload_0
    //   80: getfield 501	android/media/AudioManager:mDeviceCallbacks	Landroid/util/ArrayMap;
    //   83: aload_1
    //   84: aload_2
    //   85: invokevirtual 1129	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   88: pop
    //   89: aload_0
    //   90: aload_2
    //   91: invokevirtual 564	android/media/AudioManager$NativeEventHandlerDelegate:getHandler	()Landroid/os/Handler;
    //   94: invokespecial 428	android/media/AudioManager:broadcastDeviceListChange	(Landroid/os/Handler;)V
    //   97: goto -71 -> 26
    //   100: astore_1
    //   101: aload 4
    //   103: monitorexit
    //   104: aload_1
    //   105: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	106	0	this	AudioManager
    //   0	106	1	paramAudioDeviceCallback	AudioDeviceCallback
    //   0	106	2	paramHandler	Handler
    //   21	2	3	bool	boolean
    //   4	98	4	localArrayMap	ArrayMap
    // Exception table:
    //   from	to	target	type
    //   13	22	100	finally
    //   30	60	100	finally
    //   60	68	100	finally
    //   68	97	100	finally
  }
  
  public void registerAudioFocusListener(OnAudioFocusChangeListener paramOnAudioFocusChangeListener)
  {
    synchronized (this.mFocusListenerLock)
    {
      boolean bool = this.mAudioFocusIdListenerMap.containsKey(getIdForAudioFocusListener(paramOnAudioFocusChangeListener));
      if (bool) {
        return;
      }
      this.mAudioFocusIdListenerMap.put(getIdForAudioFocusListener(paramOnAudioFocusChangeListener), paramOnAudioFocusChangeListener);
      return;
    }
  }
  
  public int registerAudioPolicy(AudioPolicy paramAudioPolicy)
  {
    if (paramAudioPolicy == null) {
      throw new IllegalArgumentException("Illegal null AudioPolicy argument");
    }
    Object localObject = getService();
    try
    {
      localObject = ((IAudioService)localObject).registerAudioPolicy(paramAudioPolicy.getConfig(), paramAudioPolicy.cb(), paramAudioPolicy.hasFocusListener());
      if (localObject == null) {
        return -1;
      }
      paramAudioPolicy.setRegistration((String)localObject);
      return 0;
    }
    catch (RemoteException paramAudioPolicy)
    {
      throw paramAudioPolicy.rethrowFromSystemServer();
    }
  }
  
  public void registerAudioPortUpdateListener(OnAudioPortUpdateListener paramOnAudioPortUpdateListener)
  {
    sAudioPortEventHandler.init();
    sAudioPortEventHandler.registerListener(paramOnAudioPortUpdateListener);
  }
  
  public void registerAudioRecordingCallback(AudioRecordingCallback paramAudioRecordingCallback, Handler paramHandler)
  {
    if (paramAudioRecordingCallback == null) {
      throw new IllegalArgumentException("Illegal null AudioRecordingCallback argument");
    }
    for (;;)
    {
      synchronized (this.mRecordCallbackLock)
      {
        if (this.mRecordCallbackList == null) {
          this.mRecordCallbackList = new ArrayList();
        }
        int i = this.mRecordCallbackList.size();
        if (!hasRecordCallback_sync(paramAudioRecordingCallback))
        {
          this.mRecordCallbackList.add(new AudioRecordingCallbackInfo(paramAudioRecordingCallback, new ServiceEventHandlerDelegate(paramHandler).getHandler()));
          int j = this.mRecordCallbackList.size();
          if ((i == 0) && (j > 0)) {
            paramAudioRecordingCallback = getService();
          }
          try
          {
            paramAudioRecordingCallback.registerRecordingCallback(this.mRecCb);
            return;
          }
          catch (RemoteException paramAudioRecordingCallback)
          {
            throw paramAudioRecordingCallback.rethrowFromSystemServer();
          }
        }
      }
      Log.w(TAG, "attempt to call registerAudioRecordingCallback() on a previouslyregistered callback");
    }
  }
  
  @Deprecated
  public void registerMediaButtonEventReceiver(PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent == null) {
      return;
    }
    registerMediaButtonIntent(paramPendingIntent, null);
  }
  
  @Deprecated
  public void registerMediaButtonEventReceiver(ComponentName paramComponentName)
  {
    if (paramComponentName == null) {
      return;
    }
    if (!paramComponentName.getPackageName().equals(getContext().getPackageName()))
    {
      Log.e(TAG, "registerMediaButtonEventReceiver() error: receiver and context package names don't match");
      return;
    }
    Intent localIntent = new Intent("android.intent.action.MEDIA_BUTTON");
    localIntent.setComponent(paramComponentName);
    registerMediaButtonIntent(PendingIntent.getBroadcast(getContext(), 0, localIntent, 0), paramComponentName);
  }
  
  public void registerMediaButtonIntent(PendingIntent paramPendingIntent, ComponentName paramComponentName)
  {
    if (paramPendingIntent == null)
    {
      Log.e(TAG, "Cannot call registerMediaButtonIntent() with a null parameter");
      return;
    }
    MediaSessionLegacyHelper.getHelper(getContext()).addMediaButtonListener(paramPendingIntent, paramComponentName, getContext());
  }
  
  @Deprecated
  public void registerRemoteControlClient(RemoteControlClient paramRemoteControlClient)
  {
    if ((paramRemoteControlClient == null) || (paramRemoteControlClient.getRcMediaIntent() == null)) {
      return;
    }
    paramRemoteControlClient.registerWithSession(MediaSessionLegacyHelper.getHelper(getContext()));
  }
  
  @Deprecated
  public boolean registerRemoteController(RemoteController paramRemoteController)
  {
    if (paramRemoteController == null) {
      return false;
    }
    paramRemoteController.startListeningToSessions();
    paramRemoteController = getService();
    try
    {
      paramRemoteController.updateRemoteControllerOnExistingMediaPlayers();
      return true;
    }
    catch (RemoteException paramRemoteController)
    {
      for (;;)
      {
        Log.e(TAG, "Error in calling Audio service interfaceupdateRemoteControllerOnExistingMediaPlayers() due to " + paramRemoteController);
      }
    }
  }
  
  public void reloadAudioSettings()
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.reloadAudioSettings();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int requestAudioFocus(OnAudioFocusChangeListener paramOnAudioFocusChangeListener, int paramInt1, int paramInt2)
  {
    try
    {
      paramInt1 = requestAudioFocus(paramOnAudioFocusChangeListener, new AudioAttributes.Builder().setInternalLegacyStreamType(paramInt1).build(), paramInt2, 0);
      return paramInt1;
    }
    catch (IllegalArgumentException paramOnAudioFocusChangeListener)
    {
      Log.e(TAG, "Audio focus request denied due to ", paramOnAudioFocusChangeListener);
    }
    return 0;
  }
  
  public int requestAudioFocus(OnAudioFocusChangeListener paramOnAudioFocusChangeListener, AudioAttributes paramAudioAttributes, int paramInt1, int paramInt2)
    throws IllegalArgumentException
  {
    if (paramInt2 != (paramInt2 & 0x3)) {
      throw new IllegalArgumentException("Invalid flags 0x" + Integer.toHexString(paramInt2).toUpperCase());
    }
    return requestAudioFocus(paramOnAudioFocusChangeListener, paramAudioAttributes, paramInt1, paramInt2 & 0x3, null);
  }
  
  /* Error */
  public int requestAudioFocus(OnAudioFocusChangeListener paramOnAudioFocusChangeListener, AudioAttributes paramAudioAttributes, int paramInt1, int paramInt2, AudioPolicy paramAudioPolicy)
    throws IllegalArgumentException
  {
    // Byte code:
    //   0: aload_2
    //   1: ifnonnull +14 -> 15
    //   4: new 1136	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 1278
    //   11: invokespecial 1139	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: iload_3
    //   16: iconst_1
    //   17: if_icmplt +8 -> 25
    //   20: iload_3
    //   21: iconst_4
    //   22: if_icmple +14 -> 36
    //   25: new 1136	java/lang/IllegalArgumentException
    //   28: dup
    //   29: ldc_w 1280
    //   32: invokespecial 1139	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   35: athrow
    //   36: iload 4
    //   38: iload 4
    //   40: bipush 7
    //   42: iand
    //   43: if_icmpeq +38 -> 81
    //   46: new 1136	java/lang/IllegalArgumentException
    //   49: dup
    //   50: new 614	java/lang/StringBuilder
    //   53: dup
    //   54: invokespecial 615	java/lang/StringBuilder:<init>	()V
    //   57: ldc_w 1282
    //   60: invokevirtual 625	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: iload 4
    //   65: invokestatic 1269	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   68: invokevirtual 1272	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   71: invokevirtual 625	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: invokevirtual 631	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   77: invokespecial 1139	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   80: athrow
    //   81: iload 4
    //   83: iconst_1
    //   84: iand
    //   85: iconst_1
    //   86: if_icmpne +18 -> 104
    //   89: aload_1
    //   90: ifnonnull +14 -> 104
    //   93: new 1136	java/lang/IllegalArgumentException
    //   96: dup
    //   97: ldc_w 1284
    //   100: invokespecial 1139	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   103: athrow
    //   104: iload 4
    //   106: iconst_4
    //   107: iand
    //   108: iconst_4
    //   109: if_icmpne +19 -> 128
    //   112: aload 5
    //   114: ifnonnull +14 -> 128
    //   117: new 1136	java/lang/IllegalArgumentException
    //   120: dup
    //   121: ldc_w 1286
    //   124: invokespecial 1139	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   127: athrow
    //   128: aload_0
    //   129: aload_1
    //   130: invokevirtual 1288	android/media/AudioManager:registerAudioFocusListener	(Landroid/media/AudioManager$OnAudioFocusChangeListener;)V
    //   133: invokestatic 705	android/media/AudioManager:getService	()Landroid/media/IAudioService;
    //   136: astore 6
    //   138: aload_0
    //   139: getfield 494	android/media/AudioManager:mICallBack	Landroid/os/IBinder;
    //   142: astore 7
    //   144: aload_0
    //   145: getfield 486	android/media/AudioManager:mAudioFocusDispatcher	Landroid/media/IAudioFocusDispatcher;
    //   148: astore 8
    //   150: aload_0
    //   151: aload_1
    //   152: invokespecial 887	android/media/AudioManager:getIdForAudioFocusListener	(Landroid/media/AudioManager$OnAudioFocusChangeListener;)Ljava/lang/String;
    //   155: astore 9
    //   157: aload_0
    //   158: invokespecial 510	android/media/AudioManager:getContext	()Landroid/content/Context;
    //   161: invokevirtual 898	android/content/Context:getOpPackageName	()Ljava/lang/String;
    //   164: astore 10
    //   166: aload 5
    //   168: ifnull +32 -> 200
    //   171: aload 5
    //   173: invokevirtual 1149	android/media/audiopolicy/AudioPolicy:cb	()Landroid/media/audiopolicy/IAudioPolicyCallback;
    //   176: astore_1
    //   177: aload 6
    //   179: aload_2
    //   180: iload_3
    //   181: aload 7
    //   183: aload 8
    //   185: aload 9
    //   187: aload 10
    //   189: iload 4
    //   191: aload_1
    //   192: invokeinterface 1291 9 0
    //   197: istore_3
    //   198: iload_3
    //   199: ireturn
    //   200: aconst_null
    //   201: astore_1
    //   202: goto -25 -> 177
    //   205: astore_1
    //   206: aload_1
    //   207: invokevirtual 713	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   210: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	211	0	this	AudioManager
    //   0	211	1	paramOnAudioFocusChangeListener	OnAudioFocusChangeListener
    //   0	211	2	paramAudioAttributes	AudioAttributes
    //   0	211	3	paramInt1	int
    //   0	211	4	paramInt2	int
    //   0	211	5	paramAudioPolicy	AudioPolicy
    //   136	42	6	localIAudioService	IAudioService
    //   142	40	7	localIBinder	IBinder
    //   148	36	8	localIAudioFocusDispatcher	IAudioFocusDispatcher
    //   155	31	9	str1	String
    //   164	24	10	str2	String
    // Exception table:
    //   from	to	target	type
    //   138	166	205	android/os/RemoteException
    //   171	177	205	android/os/RemoteException
    //   177	198	205	android/os/RemoteException
  }
  
  public void requestAudioFocusForCall(int paramInt1, int paramInt2)
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.requestAudioFocus(new AudioAttributes.Builder().setInternalLegacyStreamType(paramInt1).build(), paramInt2, this.mICallBack, null, "AudioFocus_For_Phone_Ring_And_Calls", getContext().getOpPackageName(), 4, null);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int setBluetoothA2dpDeviceConnectionState(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
  {
    IAudioService localIAudioService = getService();
    try
    {
      paramInt1 = localIAudioService.setBluetoothA2dpDeviceConnectionState(paramBluetoothDevice, paramInt1, paramInt2);
      return paramInt1;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      throw paramBluetoothDevice.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void setBluetoothA2dpOn(boolean paramBoolean) {}
  
  public void setBluetoothScoOn(boolean paramBoolean)
  {
    IAudioService localIAudioService = getService();
    try
    {
      StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
      int i = 0;
      int j = arrayOfStackTraceElement.length;
      while (i < j)
      {
        StackTraceElement localStackTraceElement = arrayOfStackTraceElement[i];
        Log.v(TAG, "Elem: " + localStackTraceElement);
        i += 1;
      }
      if ((getContext().getOpPackageName() != null) && (getContext().getOpPackageName().equals("android.media.cts")))
      {
        Log.d(TAG, getContext().getOpPackageName() + "service.setBluetoothCtsScoOn");
        localIAudioService.setBluetoothCtsScoOn(paramBoolean);
        return;
      }
      localIAudioService.setBluetoothScoOn(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int setHdmiSystemAudioSupported(boolean paramBoolean)
  {
    try
    {
      int i = getService().setHdmiSystemAudioSupported(paramBoolean);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setMasterMute(boolean paramBoolean, int paramInt)
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.setMasterMute(paramBoolean, paramInt, getContext().getOpPackageName(), UserHandle.getCallingUserId());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setMicrophoneMute(boolean paramBoolean)
  {
    IAudioService localIAudioService = getService();
    try
    {
      Log.d(TAG, "PPD Audiomanager setMicrophoneMute Mute = " + paramBoolean);
      StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
      int i = 0;
      int j = arrayOfStackTraceElement.length;
      while (i < j)
      {
        StackTraceElement localStackTraceElement = arrayOfStackTraceElement[i];
        Log.v(TAG, "Elem: " + localStackTraceElement);
        i += 1;
      }
      localIAudioService.setMicrophoneMute(paramBoolean, getContext().getOpPackageName(), UserHandle.getCallingUserId());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setMode(int paramInt)
  {
    int i = 0;
    Log.d(TAG, "PPD setMode mode = " + paramInt);
    Object localObject1 = Thread.currentThread().getStackTrace();
    int j = localObject1.length;
    while (i < j)
    {
      Object localObject2 = localObject1[i];
      Log.v(TAG, "Elem: " + localObject2);
      i += 1;
    }
    localObject1 = getService();
    if (3 == paramInt) {}
    try
    {
      if ((getContext().getOpPackageName() != null) && (getContext().getOpPackageName().equals("com.tencent.tmgp.sgame")))
      {
        Log.d(TAG, getContext().getOpPackageName() + " isKing_of_Glory_App=1");
        AudioSystem.setParameters("isKing_of_Glory_App=1");
      }
      if ((paramInt == 0) && (getContext().getOpPackageName() != null) && (getContext().getOpPackageName().equals("com.tencent.tmgp.sgame")))
      {
        Log.d(TAG, getContext().getOpPackageName() + " isKing_of_Glory_App=0");
        AudioSystem.setParameters("isKing_of_Glory_App=0");
      }
      ((IAudioService)localObject1).setMode(paramInt, this.mICallBack, this.mApplicationContext.getOpPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setOnePlusFixedRingerMode(boolean paramBoolean)
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.setOnePlusFixedRingerMode(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e(TAG, "Dead object in setStreamMute", localRemoteException);
    }
  }
  
  public void setOnePlusRingVolumeRange(int paramInt1, int paramInt2)
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.setOnePlusRingVolumeRange(paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e(TAG, "Dead object in setStreamMute", localRemoteException);
    }
  }
  
  @Deprecated
  public void setParameter(String paramString1, String paramString2)
  {
    setParameters(paramString1 + "=" + paramString2);
  }
  
  public void setParameters(String paramString)
  {
    AudioSystem.setParameters(paramString);
  }
  
  public void setRingerMode(int paramInt)
  {
    if (!isValidRingerMode(paramInt)) {
      return;
    }
    IAudioService localIAudioService = getService();
    try
    {
      StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
      int i = 0;
      int j = arrayOfStackTraceElement.length;
      while (i < j)
      {
        StackTraceElement localStackTraceElement = arrayOfStackTraceElement[i];
        Log.v(TAG, "Elem: " + localStackTraceElement);
        i += 1;
      }
      localIAudioService.setRingerModeExternal(paramInt, getContext().getOpPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setRingerModeInternal(int paramInt)
  {
    try
    {
      getService().setRingerModeInternal(paramInt, getContext().getOpPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void setRouting(int paramInt1, int paramInt2, int paramInt3) {}
  
  public void setSpeakerphoneOn(boolean paramBoolean)
  {
    IAudioService localIAudioService = getService();
    try
    {
      StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
      int i = 0;
      int j = arrayOfStackTraceElement.length;
      while (i < j)
      {
        StackTraceElement localStackTraceElement = arrayOfStackTraceElement[i];
        Log.v(TAG, "Elem: " + localStackTraceElement);
        i += 1;
      }
      if ((getContext().getOpPackageName() == null) || (localIAudioService.isHasSpeakerAuthority(getContext().getOpPackageName())))
      {
        localIAudioService.setSpeakerphoneOn(paramBoolean);
        return;
      }
      Log.d(TAG, getContext().getOpPackageName() + "do not have using speaker authority in call");
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void setStreamMute(int paramInt, boolean paramBoolean)
  {
    Log.w(TAG, "setStreamMute is deprecated. adjustStreamVolume should be used instead.");
    if (paramBoolean) {}
    for (int i = -100; paramInt == Integer.MIN_VALUE; i = 100)
    {
      adjustSuggestedStreamVolume(i, paramInt, 0);
      return;
    }
    adjustStreamVolume(paramInt, i, 0);
  }
  
  @Deprecated
  public void setStreamSolo(int paramInt, boolean paramBoolean)
  {
    Log.w(TAG, "setStreamSolo has been deprecated. Do not use.");
  }
  
  public void setStreamVolume(int paramInt1, int paramInt2, int paramInt3)
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.setStreamVolume(paramInt1, paramInt2, paramInt3, getContext().getOpPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setVibrateSetting(int paramInt1, int paramInt2)
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.setVibrateSetting(paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setVolumeController(IVolumeController paramIVolumeController)
  {
    try
    {
      getService().setVolumeController(paramIVolumeController);
      return;
    }
    catch (RemoteException paramIVolumeController)
    {
      throw paramIVolumeController.rethrowFromSystemServer();
    }
  }
  
  public void setVolumePolicy(VolumePolicy paramVolumePolicy)
  {
    try
    {
      getService().setVolumePolicy(paramVolumePolicy);
      return;
    }
    catch (RemoteException paramVolumePolicy)
    {
      throw paramVolumePolicy.rethrowFromSystemServer();
    }
  }
  
  public void setWiredDeviceConnectionState(int paramInt1, int paramInt2, String paramString1, String paramString2)
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.setWiredDeviceConnectionState(paramInt1, paramInt2, paramString1, paramString2, this.mApplicationContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void setWiredHeadsetOn(boolean paramBoolean) {}
  
  public boolean shouldVibrate(int paramInt)
  {
    IAudioService localIAudioService = getService();
    try
    {
      boolean bool = localIAudioService.shouldVibrate(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void startBluetoothSco()
  {
    Object localObject1 = Thread.currentThread().getStackTrace();
    int i = 0;
    int j = localObject1.length;
    while (i < j)
    {
      Object localObject2 = localObject1[i];
      Log.v(TAG, "Elem: " + localObject2);
      i += 1;
    }
    localObject1 = getService();
    try
    {
      ((IAudioService)localObject1).startBluetoothSco(this.mICallBack, getContext().getApplicationInfo().targetSdkVersion);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void startBluetoothScoVirtualCall()
  {
    Object localObject1 = Thread.currentThread().getStackTrace();
    int i = 0;
    int j = localObject1.length;
    while (i < j)
    {
      Object localObject2 = localObject1[i];
      Log.v(TAG, "Elem: " + localObject2);
      i += 1;
    }
    localObject1 = getService();
    try
    {
      ((IAudioService)localObject1).startBluetoothScoVirtualCall(this.mICallBack);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void stopBluetoothSco()
  {
    Object localObject1 = Thread.currentThread().getStackTrace();
    int i = 0;
    int j = localObject1.length;
    while (i < j)
    {
      Object localObject2 = localObject1[i];
      Log.v(TAG, "Elem: " + localObject2);
      i += 1;
    }
    localObject1 = getService();
    try
    {
      ((IAudioService)localObject1).stopBluetoothSco(this.mICallBack);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void threeKeySetStreamVolume(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.threeKeySetStreamVolume(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void unloadSoundEffects()
  {
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.unloadSoundEffects();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void unregisterAudioDeviceCallback(AudioDeviceCallback paramAudioDeviceCallback)
  {
    synchronized (this.mDeviceCallbacks)
    {
      if (this.mDeviceCallbacks.containsKey(paramAudioDeviceCallback))
      {
        this.mDeviceCallbacks.remove(paramAudioDeviceCallback);
        if (this.mDeviceCallbacks.size() == 0) {
          unregisterAudioPortUpdateListener(this.mPortListener);
        }
      }
      return;
    }
  }
  
  public void unregisterAudioFocusListener(OnAudioFocusChangeListener paramOnAudioFocusChangeListener)
  {
    synchronized (this.mFocusListenerLock)
    {
      this.mAudioFocusIdListenerMap.remove(getIdForAudioFocusListener(paramOnAudioFocusChangeListener));
      return;
    }
  }
  
  public void unregisterAudioPolicyAsync(AudioPolicy paramAudioPolicy)
  {
    if (paramAudioPolicy == null) {
      throw new IllegalArgumentException("Illegal null AudioPolicy argument");
    }
    IAudioService localIAudioService = getService();
    try
    {
      localIAudioService.unregisterAudioPolicyAsync(paramAudioPolicy.cb());
      paramAudioPolicy.setRegistration(null);
      return;
    }
    catch (RemoteException paramAudioPolicy)
    {
      throw paramAudioPolicy.rethrowFromSystemServer();
    }
  }
  
  public void unregisterAudioPortUpdateListener(OnAudioPortUpdateListener paramOnAudioPortUpdateListener)
  {
    sAudioPortEventHandler.unregisterListener(paramOnAudioPortUpdateListener);
  }
  
  public void unregisterAudioRecordingCallback(AudioRecordingCallback paramAudioRecordingCallback)
  {
    if (paramAudioRecordingCallback == null) {
      throw new IllegalArgumentException("Illegal null AudioRecordingCallback argument");
    }
    for (;;)
    {
      synchronized (this.mRecordCallbackLock)
      {
        List localList = this.mRecordCallbackList;
        if (localList == null) {
          return;
        }
        int i = this.mRecordCallbackList.size();
        if (removeRecordCallback_sync(paramAudioRecordingCallback))
        {
          int j = this.mRecordCallbackList.size();
          if ((i > 0) && (j == 0)) {
            paramAudioRecordingCallback = getService();
          }
          try
          {
            paramAudioRecordingCallback.unregisterRecordingCallback(this.mRecCb);
            return;
          }
          catch (RemoteException paramAudioRecordingCallback)
          {
            throw paramAudioRecordingCallback.rethrowFromSystemServer();
          }
        }
      }
      Log.w(TAG, "attempt to call unregisterAudioRecordingCallback() on a callback already unregistered or never registered");
    }
  }
  
  @Deprecated
  public void unregisterMediaButtonEventReceiver(PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent == null) {
      return;
    }
    unregisterMediaButtonIntent(paramPendingIntent);
  }
  
  @Deprecated
  public void unregisterMediaButtonEventReceiver(ComponentName paramComponentName)
  {
    if (paramComponentName == null) {
      return;
    }
    Intent localIntent = new Intent("android.intent.action.MEDIA_BUTTON");
    localIntent.setComponent(paramComponentName);
    unregisterMediaButtonIntent(PendingIntent.getBroadcast(getContext(), 0, localIntent, 0));
  }
  
  public void unregisterMediaButtonIntent(PendingIntent paramPendingIntent)
  {
    MediaSessionLegacyHelper.getHelper(getContext()).removeMediaButtonListener(paramPendingIntent);
  }
  
  @Deprecated
  public void unregisterRemoteControlClient(RemoteControlClient paramRemoteControlClient)
  {
    if ((paramRemoteControlClient == null) || (paramRemoteControlClient.getRcMediaIntent() == null)) {
      return;
    }
    paramRemoteControlClient.unregisterWithSession(MediaSessionLegacyHelper.getHelper(getContext()));
  }
  
  @Deprecated
  public void unregisterRemoteController(RemoteController paramRemoteController)
  {
    if (paramRemoteController == null) {
      return;
    }
    paramRemoteController.stopListeningToSessions();
  }
  
  public void updateMediaPlayerList(String paramString, boolean paramBoolean)
  {
    IAudioService localIAudioService = getService();
    if (paramBoolean) {}
    try
    {
      Log.d(TAG, "updateMediaPlayerList: Add RCC " + paramString + " to List");
      localIAudioService.addMediaPlayerAndUpdateRemoteController(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      Log.e(TAG, "Exception while executing updateMediaPlayerList: " + paramString);
    }
    Log.d(TAG, "updateMediaPlayerList: Remove RCC " + paramString + " from List");
    localIAudioService.removeMediaPlayerAndUpdateRemoteController(paramString);
    return;
  }
  
  public static abstract class AudioRecordingCallback
  {
    public void onRecordingConfigChanged(List<AudioRecordingConfiguration> paramList) {}
  }
  
  private static class AudioRecordingCallbackInfo
  {
    final AudioManager.AudioRecordingCallback mCb;
    final Handler mHandler;
    
    AudioRecordingCallbackInfo(AudioManager.AudioRecordingCallback paramAudioRecordingCallback, Handler paramHandler)
    {
      this.mCb = paramAudioRecordingCallback;
      this.mHandler = paramHandler;
    }
  }
  
  private class NativeEventHandlerDelegate
  {
    private final Handler mHandler;
    
    NativeEventHandlerDelegate(final AudioDeviceCallback paramAudioDeviceCallback, Handler paramHandler)
    {
      if (paramHandler != null) {}
      for (this$1 = paramHandler.getLooper(); AudioManager.this != null; this$1 = Looper.getMainLooper())
      {
        this.mHandler = new Handler(AudioManager.this)
        {
          public void handleMessage(Message paramAnonymousMessage)
          {
            switch (paramAnonymousMessage.what)
            {
            default: 
              Log.e(AudioManager.-get0(), "Unknown native event type: " + paramAnonymousMessage.what);
            }
            do
            {
              do
              {
                return;
              } while (paramAudioDeviceCallback == null);
              paramAudioDeviceCallback.onAudioDevicesAdded((AudioDeviceInfo[])paramAnonymousMessage.obj);
              return;
            } while (paramAudioDeviceCallback == null);
            paramAudioDeviceCallback.onAudioDevicesRemoved((AudioDeviceInfo[])paramAnonymousMessage.obj);
          }
        };
        return;
      }
      this.mHandler = null;
    }
    
    Handler getHandler()
    {
      return this.mHandler;
    }
  }
  
  private class OnAmPortUpdateListener
    implements AudioManager.OnAudioPortUpdateListener
  {
    static final String TAG = "OnAmPortUpdateListener";
    
    private OnAmPortUpdateListener() {}
    
    public void onAudioPatchListUpdate(AudioPatch[] paramArrayOfAudioPatch) {}
    
    public void onAudioPortListUpdate(AudioPort[] paramArrayOfAudioPort)
    {
      AudioManager.-wrap1(AudioManager.this, null);
    }
    
    public void onServiceDied()
    {
      AudioManager.-wrap1(AudioManager.this, null);
    }
  }
  
  public static abstract interface OnAudioFocusChangeListener
  {
    public abstract void onAudioFocusChange(int paramInt);
  }
  
  public static abstract interface OnAudioPortUpdateListener
  {
    public abstract void onAudioPatchListUpdate(AudioPatch[] paramArrayOfAudioPatch);
    
    public abstract void onAudioPortListUpdate(AudioPort[] paramArrayOfAudioPort);
    
    public abstract void onServiceDied();
  }
  
  private static final class RecordConfigChangeCallbackData
  {
    final AudioManager.AudioRecordingCallback mCb;
    final List<AudioRecordingConfiguration> mConfigs;
    
    RecordConfigChangeCallbackData(AudioManager.AudioRecordingCallback paramAudioRecordingCallback, List<AudioRecordingConfiguration> paramList)
    {
      this.mCb = paramAudioRecordingCallback;
      this.mConfigs = paramList;
    }
  }
  
  private class ServiceEventHandlerDelegate
  {
    private final Handler mHandler;
    
    ServiceEventHandlerDelegate(Handler paramHandler)
    {
      if (paramHandler == null)
      {
        paramHandler = Looper.myLooper();
        this$1 = paramHandler;
        if (paramHandler != null) {}
      }
      for (this$1 = Looper.getMainLooper(); AudioManager.this != null; this$1 = paramHandler.getLooper())
      {
        this.mHandler = new Handler(AudioManager.this)
        {
          public void handleMessage(Message paramAnonymousMessage)
          {
            switch (paramAnonymousMessage.what)
            {
            default: 
              Log.e(AudioManager.-get0(), "Unknown event " + paramAnonymousMessage.what);
            }
            do
            {
              for (;;)
              {
                return;
                synchronized (AudioManager.-get1(AudioManager.this))
                {
                  AudioManager.OnAudioFocusChangeListener localOnAudioFocusChangeListener = AudioManager.-wrap0(AudioManager.this, (String)paramAnonymousMessage.obj);
                  if (localOnAudioFocusChangeListener != null)
                  {
                    Log.d(AudioManager.-get0(), "AudioManager dispatching onAudioFocusChange(" + paramAnonymousMessage.arg1 + ") for " + paramAnonymousMessage.obj);
                    localOnAudioFocusChangeListener.onAudioFocusChange(paramAnonymousMessage.arg1);
                    return;
                  }
                }
              }
              paramAnonymousMessage = (AudioManager.RecordConfigChangeCallbackData)paramAnonymousMessage.obj;
            } while (paramAnonymousMessage.mCb == null);
            paramAnonymousMessage.mCb.onRecordingConfigChanged(paramAnonymousMessage.mConfigs);
          }
        };
        return;
      }
      this.mHandler = null;
    }
    
    Handler getHandler()
    {
      return this.mHandler;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */