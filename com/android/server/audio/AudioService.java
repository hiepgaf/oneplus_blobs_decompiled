package com.android.server.audio;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.NotificationManager;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.database.ContentObserver;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiPlaybackClient;
import android.hardware.hdmi.HdmiPlaybackClient.DisplayStatusCallback;
import android.hardware.hdmi.HdmiTvClient;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.AudioDevicePort;
import android.media.AudioFormat;
import android.media.AudioManagerInternal;
import android.media.AudioManagerInternal.RingerModeDelegate;
import android.media.AudioPort;
import android.media.AudioRecordingConfiguration;
import android.media.AudioRoutesInfo;
import android.media.AudioSystem;
import android.media.AudioSystem.DynamicPolicyCallback;
import android.media.AudioSystem.ErrorCallback;
import android.media.AudioTrack;
import android.media.IAudioFocusDispatcher;
import android.media.IAudioRoutesObserver;
import android.media.IAudioService.Stub;
import android.media.IRecordingConfigDispatcher;
import android.media.IRingtonePlayer;
import android.media.IVolumeController;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.SoundPool;
import android.media.SoundPool.Builder;
import android.media.SoundPool.OnLoadCompleteListener;
import android.media.VolumePolicy;
import android.media.audiopolicy.AudioMix;
import android.media.audiopolicy.AudioPolicyConfig;
import android.media.audiopolicy.IAudioPolicyCallback;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManagerInternal;
import android.os.UserManagerInternal.UserRestrictionsListener;
import android.os.Vibrator;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.MathUtils;
import android.util.Slog;
import android.util.SparseIntArray;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener;
import com.android.internal.util.XmlUtils;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.pm.UserManagerService;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;

public class AudioService
  extends IAudioService.Stub
{
  public static final String ACTION_SHUTDOWN_MUTE_MUSIC = "com.oem.intent.action.ACTION_SHUTDOWN_MUTE_MUSIC";
  private static final String ASSET_FILE_VERSION = "1.0";
  private static final String ATTR_ASSET_FILE = "file";
  private static final String ATTR_ASSET_ID = "id";
  private static final String ATTR_GROUP_NAME = "name";
  private static final String ATTR_VERSION = "version";
  private static final int BTA2DP_DOCK_TIMEOUT_MILLIS = 8000;
  private static final int BT_HEADSET_CNCT_TIMEOUT_MS = 3000;
  public static final String CONNECT_INTENT_KEY_ADDRESS = "address";
  public static final String CONNECT_INTENT_KEY_DEVICE_CLASS = "class";
  public static final String CONNECT_INTENT_KEY_HAS_CAPTURE = "hasCapture";
  public static final String CONNECT_INTENT_KEY_HAS_MIDI = "hasMIDI";
  public static final String CONNECT_INTENT_KEY_HAS_PLAYBACK = "hasPlayback";
  public static final String CONNECT_INTENT_KEY_PORT_NAME = "portName";
  public static final String CONNECT_INTENT_KEY_STATE = "state";
  protected static final boolean DEBUG_AP;
  protected static final boolean DEBUG_DEVICES;
  protected static final boolean DEBUG_MODE = Log.isLoggable("AudioService.MOD", 3);
  protected static final boolean DEBUG_VOL;
  private static final int FLAG_ADJUST_VOLUME = 1;
  private static final String GROUP_TOUCH_SOUNDS = "touch_sounds";
  private static final int INDICATE_SYSTEM_READY_RETRY_DELAY_MS = 1000;
  private static int[] MAX_STREAM_VOLUME;
  private static int[] MIN_STREAM_VOLUME;
  private static final int MSG_AUDIO_SERVER_DIED = 4;
  private static final int MSG_BROADCAST_AUDIO_BECOMING_NOISY = 15;
  private static final int MSG_BROADCAST_BT_CONNECTION_STATE = 19;
  private static final int MSG_BTA2DP_DOCK_TIMEOUT = 6;
  private static final int MSG_BT_HEADSET_CNCT_FAILED = 9;
  private static final int MSG_CHECK_MUSIC_ACTIVE = 14;
  private static final int MSG_CONFIGURE_SAFE_MEDIA_VOLUME = 16;
  private static final int MSG_CONFIGURE_SAFE_MEDIA_VOLUME_FORCED = 17;
  private static final int MSG_DYN_POLICY_MIX_STATE_UPDATE = 25;
  private static final int MSG_INDICATE_SYSTEM_READY = 26;
  private static final int MSG_LOAD_SOUND_EFFECTS = 7;
  private static final int MSG_MUTE_MUSIC = 28;
  private static final int MSG_PERSIST_MUSIC_ACTIVE_MS = 22;
  private static final int MSG_PERSIST_RINGER_MODE = 3;
  private static final int MSG_PERSIST_SAFE_VOLUME_STATE = 18;
  private static final int MSG_PERSIST_VOLUME = 1;
  private static final int MSG_PER_SPEAKER_MUSIC_VOLUME = 30;
  private static final int MSG_PLAY_SLIENT_BUFFER = 29;
  private static final int MSG_PLAY_SOUND_EFFECT = 5;
  private static final int MSG_REPORT_NEW_ROUTES = 12;
  private static final int MSG_SET_A2DP_SINK_CONNECTION_STATE = 102;
  private static final int MSG_SET_A2DP_SRC_CONNECTION_STATE = 101;
  private static final int MSG_SET_ALL_VOLUMES = 10;
  private static final int MSG_SET_DEVICE_VOLUME = 0;
  private static final int MSG_SET_FORCE_BT_A2DP_USE = 13;
  private static final int MSG_SET_FORCE_USE = 8;
  private static final int MSG_SET_WIRED_DEVICE_CONNECTION_STATE = 100;
  private static final int MSG_SYSTEM_READY = 21;
  private static final int MSG_UNLOAD_SOUND_EFFECTS = 20;
  private static final int MSG_UNMUTE_STREAM = 24;
  private static final int MUSIC_ACTIVE_POLL_PERIOD_MS = 60000;
  private static final int NUM_SOUNDPOOL_CHANNELS = 4;
  private static final int PERSIST_DELAY = 500;
  private static final String[] RINGER_MODE_NAMES = { "SILENT", "VIBRATE", "NORMAL" };
  private static final int SAFE_MEDIA_VOLUME_ACTIVE = 3;
  private static final int SAFE_MEDIA_VOLUME_DISABLED = 1;
  private static final int SAFE_MEDIA_VOLUME_INACTIVE = 2;
  private static final int SAFE_MEDIA_VOLUME_NOT_CONFIGURED = 0;
  private static final int SAFE_VOLUME_CONFIGURE_TIMEOUT_MS = 30000;
  private static final int SCO_MODE_MAX = 2;
  private static final int SCO_MODE_RAW = 1;
  private static final int SCO_MODE_UNDEFINED = -1;
  private static final int SCO_MODE_VIRTUAL_CALL = 0;
  private static final int SCO_MODE_VR = 2;
  private static final int SCO_STATE_ACTIVATE_REQ = 1;
  private static final int SCO_STATE_ACTIVE_EXTERNAL = 2;
  private static final int SCO_STATE_ACTIVE_INTERNAL = 3;
  private static final int SCO_STATE_DEACTIVATE_EXT_REQ = 4;
  private static final int SCO_STATE_DEACTIVATE_REQ = 5;
  private static final int SCO_STATE_INACTIVE = 0;
  private static final int SENDMSG_NOOP = 1;
  private static final int SENDMSG_QUEUE = 2;
  private static final int SENDMSG_REPLACE = 0;
  private static final int SOUND_EFFECTS_LOAD_TIMEOUT_MS = 5000;
  private static final String SOUND_EFFECTS_PATH = "/media/audio/ui/";
  private static final List<String> SOUND_EFFECT_FILES;
  private static final int[] STREAM_VOLUME_OPS;
  private static final String TAG = "AudioService";
  private static final String TAG_ASSET = "asset";
  private static final String TAG_AUDIO_ASSETS = "audio_assets";
  private static final String TAG_GROUP = "group";
  public static final String TEL_PACKAGE_NAME = "com.android.server.telecom";
  private static final int UNMUTE_STREAM_DELAY = 350;
  private static final int UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX = 72000000;
  private static Long mLastDeviceConnectMsgTime;
  private static final ArrayList<MediaPlayerInfo> mMediaPlayers;
  private static int sSoundEffectVolumeDb;
  private final int[][] SOUND_EFFECT_FILES_MAP = (int[][])Array.newInstance(Integer.TYPE, new int[] { 10, 2 });
  private final int[] STREAM_VOLUME_ALIAS_DEFAULT = { 0, 2, 2, 3, 4, 2, 6, 2, 2, 3 };
  private final int[] STREAM_VOLUME_ALIAS_TELEVISION = { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
  private final int[] STREAM_VOLUME_ALIAS_VOICE = { 0, 2, 2, 3, 4, 2, 6, 2, 2, 3 };
  private BluetoothA2dp mA2dp;
  private final Object mA2dpAvrcpLock = new Object();
  private String mA2dpConnectedDevice = "";
  private final AppOpsManager mAppOps;
  private PowerManager.WakeLock mAudioEventWakeLock;
  private AudioHandler mAudioHandler;
  private HashMap<IBinder, AudioPolicyProxy> mAudioPolicies = new HashMap();
  private int mAudioPolicyCounter = 0;
  private final AudioSystem.ErrorCallback mAudioSystemCallback = new AudioSystem.ErrorCallback()
  {
    public void onError(int paramAnonymousInt)
    {
      switch (paramAnonymousInt)
      {
      default: 
        return;
      }
      AudioService.-wrap36(AudioService.-get7(AudioService.this), 4, 1, 0, 0, null, 0);
    }
  };
  private AudioSystemThread mAudioSystemThread;
  private boolean mAvrcpAbsVolSupported = false;
  int mBecomingNoisyIntentDevices = 163724;
  private boolean mBluetoothA2dpEnabled;
  private final Object mBluetoothA2dpEnabledLock = new Object();
  private BluetoothHeadset mBluetoothHeadset;
  private BluetoothDevice mBluetoothHeadsetDevice;
  private BluetoothProfile.ServiceListener mBluetoothProfileServiceListener = new BluetoothProfile.ServiceListener()
  {
    public void onServiceConnected(int paramAnonymousInt, BluetoothProfile paramAnonymousBluetoothProfile)
    {
      switch (paramAnonymousInt)
      {
      default: 
      case 2: 
      case 11: 
        do
        {
          return;
          synchronized (AudioService.-get14(AudioService.this))
          {
            synchronized (AudioService.-get5(AudioService.this))
            {
              AudioService.-set0(AudioService.this, (BluetoothA2dp)paramAnonymousBluetoothProfile);
              paramAnonymousBluetoothProfile = AudioService.-get4(AudioService.this).getConnectedDevices();
              Log.d("AudioService", "onServiceConnected: A2dp Service connected: " + paramAnonymousBluetoothProfile.size());
              paramAnonymousInt = 0;
              if (paramAnonymousInt < paramAnonymousBluetoothProfile.size())
              {
                BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramAnonymousBluetoothProfile.get(paramAnonymousInt);
                int j = AudioService.-get4(AudioService.this).getConnectionState(localBluetoothDevice);
                AudioService localAudioService = AudioService.this;
                if (j == 2) {}
                for (int i = 1;; i = 0)
                {
                  i = AudioService.-wrap4(localAudioService, 128, i);
                  AudioService.-wrap30(AudioService.this, AudioService.-get7(AudioService.this), 102, j, 0, localBluetoothDevice, i);
                  paramAnonymousInt += 1;
                  break;
                }
              }
              return;
            }
          }
          ??? = paramAnonymousBluetoothProfile.getConnectedDevices();
        } while (((List)???).size() <= 0);
        ??? = (BluetoothDevice)((List)???).get(0);
        synchronized (AudioService.-get14(AudioService.this))
        {
          paramAnonymousInt = paramAnonymousBluetoothProfile.getConnectionState((BluetoothDevice)???);
          AudioService.-wrap30(AudioService.this, AudioService.-get7(AudioService.this), 101, paramAnonymousInt, 0, ???, 0);
          return;
        }
      }
      for (;;)
      {
        boolean bool2;
        boolean bool1;
        synchronized (AudioService.-get29(AudioService.this))
        {
          AudioService.-get7(AudioService.this).removeMessages(9);
          AudioService.-set3(AudioService.this, (BluetoothHeadset)paramAnonymousBluetoothProfile);
          paramAnonymousBluetoothProfile = AudioService.-get11(AudioService.this).getConnectedDevices();
          if (paramAnonymousBluetoothProfile.size() > 0)
          {
            AudioService.-set4(AudioService.this, (BluetoothDevice)paramAnonymousBluetoothProfile.get(0));
            AudioService.-wrap14(AudioService.this);
            if ((AudioService.-get28(AudioService.this) != 1) && (AudioService.-get28(AudioService.this) != 5)) {
              break label520;
            }
            bool2 = false;
            bool1 = bool2;
            if (AudioService.-get12(AudioService.this) != null) {
              bool1 = bool2;
            }
          }
          switch (AudioService.-get28(AudioService.this))
          {
          case 2: 
          case 3: 
            if (!bool1) {
              AudioService.-wrap36(AudioService.-get7(AudioService.this), 9, 0, 0, 0, null, 0);
            }
            return;
            AudioService.-set4(AudioService.this, null);
          }
        }
        label520:
        if (AudioService.-get28(AudioService.this) == 4)
        {
          continue;
          AudioService.-set12(AudioService.this, 3);
          if (AudioService.-get27(AudioService.this) == 1)
          {
            bool1 = AudioService.-get11(AudioService.this).connectAudio();
          }
          else if (AudioService.-get27(AudioService.this) == 0)
          {
            bool1 = AudioService.-get11(AudioService.this).startScoUsingVirtualVoiceCall(AudioService.-get12(AudioService.this));
          }
          else
          {
            bool1 = bool2;
            if (AudioService.-get27(AudioService.this) == 2)
            {
              bool1 = AudioService.-get11(AudioService.this).startVoiceRecognition(AudioService.-get12(AudioService.this));
              continue;
              if (AudioService.-get27(AudioService.this) == 1)
              {
                bool1 = AudioService.-get11(AudioService.this).disconnectAudio();
              }
              else if (AudioService.-get27(AudioService.this) == 0)
              {
                bool1 = AudioService.-get11(AudioService.this).stopScoUsingVirtualVoiceCall(AudioService.-get12(AudioService.this));
              }
              else
              {
                bool1 = bool2;
                if (AudioService.-get27(AudioService.this) == 2)
                {
                  bool1 = AudioService.-get11(AudioService.this).stopVoiceRecognition(AudioService.-get12(AudioService.this));
                  continue;
                  bool1 = AudioService.-get11(AudioService.this).stopVoiceRecognition(AudioService.-get12(AudioService.this));
                  continue;
                  bool1 = bool2;
                }
              }
            }
          }
        }
      }
    }
    
    public void onServiceDisconnected(int paramAnonymousInt)
    {
      switch (paramAnonymousInt)
      {
      default: 
        return;
      case 2: 
        AudioService.this.disconnectA2dp();
        return;
      case 11: 
        AudioService.this.disconnectA2dpSink();
        return;
      }
      AudioService.this.disconnectHeadset();
    }
  };
  private boolean mBootCompelet = false;
  private Boolean mCameraSoundForced;
  private final ArrayMap<String, DeviceListSpec> mConnectedDevices = new ArrayMap();
  private final ContentResolver mContentResolver;
  private final Context mContext;
  private final ControllerService mControllerService = new ControllerService();
  final AudioRoutesInfo mCurAudioRoutes = new AudioRoutesInfo();
  private int mDeviceOrientation = 0;
  private String mDockAddress;
  private boolean mDockAudioMediaEnabled = true;
  private int mDockState = 0;
  private final AudioSystem.DynamicPolicyCallback mDynPolicyCallback = new AudioSystem.DynamicPolicyCallback()
  {
    public void onDynamicPolicyMixStateUpdate(String paramAnonymousString, int paramAnonymousInt)
    {
      if (!TextUtils.isEmpty(paramAnonymousString)) {
        AudioService.-wrap36(AudioService.-get7(AudioService.this), 25, 2, paramAnonymousInt, 0, paramAnonymousString, 0);
      }
    }
  };
  int mFixedVolumeDevices = 2890752;
  private ForceControlStreamClient mForceControlStreamClient = null;
  private final Object mForceControlStreamLock = new Object();
  private int mForcedUseForComm;
  int mFullVolumeDevices = 0;
  private final boolean mHasVibrator;
  private boolean mHdmiCecSink;
  private MyDisplayStatusCallback mHdmiDisplayStatusCallback = new MyDisplayStatusCallback(null);
  private HdmiControlManager mHdmiManager;
  private HdmiPlaybackClient mHdmiPlaybackClient;
  private boolean mHdmiSystemAudioSupported = false;
  private HdmiTvClient mHdmiTvClient;
  private long mLoweredFromNormalToVibrateTime;
  private int mMcc = 0;
  private final MediaFocusControl mMediaFocusControl;
  private int mMode = 0;
  private final boolean mMonitorOrientation;
  private final boolean mMonitorRotation;
  private int mMusicActiveMs;
  private int mMuteAffectedStreams;
  private NotificationManager mNm;
  private boolean mOnePlusFixedRingerMode = false;
  public int mOnePlusMaxRingVolumeIndex = 1000;
  public int mOnePlusMinRingVolumeIndex = 0;
  private StreamVolumeCommand mPendingVolumeCommand;
  private int mPerSpeakerMediaVolume = -1;
  private final int mPlatformType;
  private int mPreDelay = 0;
  private int mPrevVolDirection = 0;
  private final BroadcastReceiver mReceiver = new AudioServiceBroadcastReceiver(null);
  private final RecordingActivityMonitor mRecordMonitor = new RecordingActivityMonitor();
  private int mRingerMode;
  private int mRingerModeAffectedStreams = 0;
  private AudioManagerInternal.RingerModeDelegate mRingerModeDelegate;
  private int mRingerModeExternal = -1;
  private int mRingerModeMutedStreams;
  private volatile IRingtonePlayer mRingtonePlayer;
  private ArrayList<RmtSbmxFullVolDeathHandler> mRmtSbmxFullVolDeathHandlers = new ArrayList();
  private int mRmtSbmxFullVolRefCount = 0;
  final RemoteCallbackList<IAudioRoutesObserver> mRoutesObservers = new RemoteCallbackList();
  private final int mSafeMediaVolumeDevices = 12;
  private int mSafeMediaVolumeIndex;
  private Integer mSafeMediaVolumeState;
  private int mScoAudioMode;
  private int mScoAudioState;
  private final ArrayList<ScoClient> mScoClients = new ArrayList();
  private int mScoConnectionState;
  private final ArrayList<SetModeDeathHandler> mSetModeDeathHandlers = new ArrayList();
  private final Object mSettingsLock = new Object();
  private SettingsObserver mSettingsObserver;
  private final Object mSoundEffectsLock = new Object();
  private SoundPool mSoundPool;
  private SoundPoolCallback mSoundPoolCallBack;
  private SoundPoolListenerThread mSoundPoolListenerThread;
  private Looper mSoundPoolLooper = null;
  private VolumeStreamState[] mStreamStates;
  private int[] mStreamVolumeAlias;
  private boolean mSystemReady;
  private final boolean mUseFixedVolume;
  private final UserManagerInternal mUserManagerInternal;
  private final UserManagerInternal.UserRestrictionsListener mUserRestrictionsListener = new AudioServiceUserRestrictionsListener(null);
  private boolean mUserSwitchedReceived;
  private int mVibrateSetting;
  private int mVolumeControlStream = -1;
  private final VolumeController mVolumeController = new VolumeController();
  private VolumePolicy mVolumePolicy = VolumePolicy.DEFAULT;
  
  static
  {
    DEBUG_AP = Log.isLoggable("AudioService.AP", 3);
    DEBUG_VOL = Log.isLoggable("AudioService.VOL", 3);
    DEBUG_DEVICES = Log.isLoggable("AudioService.DEVICES", 3);
    mMediaPlayers = new ArrayList();
    SOUND_EFFECT_FILES = new ArrayList();
    MAX_STREAM_VOLUME = new int[] { 5, 7, 7, 30, 7, 7, 15, 7, 15, 15 };
    MIN_STREAM_VOLUME = new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    STREAM_VOLUME_OPS = new int[] { 34, 36, 35, 36, 37, 38, 39, 36, 36, 36 };
    mLastDeviceConnectMsgTime = new Long(0L);
  }
  
  public AudioService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mContentResolver = paramContext.getContentResolver();
    this.mAppOps = ((AppOpsManager)paramContext.getSystemService("appops"));
    this.mPlatformType = AudioSystem.getPlatformType(paramContext);
    this.mUserManagerInternal = ((UserManagerInternal)LocalServices.getService(UserManagerInternal.class));
    this.mAudioEventWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "handleAudioEvent");
    Object localObject = (Vibrator)paramContext.getSystemService("vibrator");
    boolean bool;
    if (localObject == null)
    {
      bool = false;
      this.mHasVibrator = bool;
      i = SystemProperties.getInt("ro.config.vc_call_vol_steps", MAX_STREAM_VOLUME[0]);
      if (i != MAX_STREAM_VOLUME[0])
      {
        MAX_STREAM_VOLUME[0] = i;
        AudioSystem.DEFAULT_STREAM_VOLUME[0] = (i * 3 / 4);
      }
      i = SystemProperties.getInt("ro.config.media_vol_steps", MAX_STREAM_VOLUME[3]);
      if (i != MAX_STREAM_VOLUME[3])
      {
        MAX_STREAM_VOLUME[3] = i;
        AudioSystem.DEFAULT_STREAM_VOLUME[3] = (i * 3 / 4);
      }
      sSoundEffectVolumeDb = paramContext.getResources().getInteger(17694724);
      this.mForcedUseForComm = 0;
      createAudioSystemThread();
      AudioSystem.setErrorCallback(this.mAudioSystemCallback);
      bool = readCameraSoundForced();
      this.mCameraSoundForced = new Boolean(bool);
      localObject = this.mAudioHandler;
      if (!bool) {
        break label1204;
      }
    }
    label1204:
    for (int i = 11;; i = 0)
    {
      sendMsg((Handler)localObject, 8, 2, 4, i, null, 0);
      this.mSafeMediaVolumeState = new Integer(Settings.Global.getInt(this.mContentResolver, "audio_safe_volume_state", 0));
      this.mSafeMediaVolumeIndex = (this.mContext.getResources().getInteger(17694864) * 10);
      this.mUseFixedVolume = this.mContext.getResources().getBoolean(17956998);
      updateStreamVolumeAlias(false, "AudioService");
      readPersistedSettings();
      readUserRestrictions();
      this.mSettingsObserver = new SettingsObserver();
      createStreamStates();
      this.mMediaFocusControl = new MediaFocusControl(this.mContext);
      readAndSetLowRamDevice();
      this.mRingerModeMutedStreams = 0;
      setRingerModeInt(getRingerModeInternal(), false);
      localObject = new IntentFilter("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED");
      ((IntentFilter)localObject).addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
      ((IntentFilter)localObject).addAction("android.intent.action.DOCK_EVENT");
      ((IntentFilter)localObject).addAction("android.intent.action.SCREEN_ON");
      ((IntentFilter)localObject).addAction("android.intent.action.SCREEN_OFF");
      ((IntentFilter)localObject).addAction("android.intent.action.USER_SWITCHED");
      ((IntentFilter)localObject).addAction("android.intent.action.USER_BACKGROUND");
      ((IntentFilter)localObject).addAction("android.intent.action.USER_FOREGROUND");
      ((IntentFilter)localObject).addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
      ((IntentFilter)localObject).addAction("android.bluetooth.adapter.action.STATE_CHANGED");
      ((IntentFilter)localObject).addAction("android.intent.action.CONFIGURATION_CHANGED");
      ((IntentFilter)localObject).addAction("com.oem.intent.action.ACTION_SHUTDOWN_MUTE_MUSIC");
      ((IntentFilter)localObject).addAction("android.intent.action.ACTION_SHUTDOWN");
      ((IntentFilter)localObject).addAction("android.intent.action.BOOT_COMPLETED");
      this.mMonitorOrientation = SystemProperties.getBoolean("ro.audio.monitorOrientation", false);
      if (this.mMonitorOrientation)
      {
        Log.v("AudioService", "monitoring device orientation");
        setOrientationForAudioSystem();
      }
      this.mMonitorRotation = SystemProperties.getBoolean("ro.audio.monitorRotation", false);
      if (this.mMonitorRotation) {
        RotationHelper.init(this.mContext, this.mAudioHandler);
      }
      paramContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, (IntentFilter)localObject, null, null);
      LocalServices.addService(AudioManagerInternal.class, new AudioServiceInternal());
      this.mUserManagerInternal.addUserRestrictionsListener(this.mUserRestrictionsListener);
      this.mRecordMonitor.initMonitor();
      this.mPerSpeakerMediaVolume = Settings.Global.getInt(this.mContentResolver, "per_speaker_music_volume", -1);
      if (this.mPerSpeakerMediaVolume == -1) {
        this.mPerSpeakerMediaVolume = this.mStreamStates[3].getIndex(2);
      }
      return;
      bool = ((Vibrator)localObject).hasVibrator();
      break;
    }
  }
  
  /* Error */
  private void adjustStreamVolume(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, int paramInt4)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 556	com/android/server/audio/AudioService:mUseFixedVolume	Z
    //   4: ifeq +4 -> 8
    //   7: return
    //   8: getstatic 862	com/android/server/audio/AudioService:DEBUG_VOL	Z
    //   11: ifeq +60 -> 71
    //   14: ldc -5
    //   16: new 1240	java/lang/StringBuilder
    //   19: dup
    //   20: invokespecial 1241	java/lang/StringBuilder:<init>	()V
    //   23: ldc_w 1243
    //   26: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: iload_1
    //   30: invokevirtual 1250	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   33: ldc_w 1252
    //   36: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   39: iload_2
    //   40: invokevirtual 1250	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   43: ldc_w 1254
    //   46: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: iload_3
    //   50: invokevirtual 1250	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   53: ldc_w 1256
    //   56: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   59: aload 5
    //   61: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   64: invokevirtual 1260	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   67: invokestatic 1263	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   70: pop
    //   71: aload_0
    //   72: iload_2
    //   73: invokespecial 1266	com/android/server/audio/AudioService:ensureValidDirection	(I)V
    //   76: aload_0
    //   77: iload_1
    //   78: invokespecial 1269	com/android/server/audio/AudioService:ensureValidStreamType	(I)V
    //   81: aload_0
    //   82: iload_2
    //   83: invokespecial 1272	com/android/server/audio/AudioService:isMuteAdjust	(I)Z
    //   86: istore 15
    //   88: iload 15
    //   90: ifeq +11 -> 101
    //   93: aload_0
    //   94: iload_1
    //   95: invokevirtual 1275	com/android/server/audio/AudioService:isStreamAffectedByMute	(I)Z
    //   98: ifeq +95 -> 193
    //   101: aload_0
    //   102: getfield 546	com/android/server/audio/AudioService:mStreamVolumeAlias	[I
    //   105: iload_1
    //   106: iaload
    //   107: istore 14
    //   109: aload_0
    //   110: getfield 542	com/android/server/audio/AudioService:mStreamStates	[Lcom/android/server/audio/AudioService$VolumeStreamState;
    //   113: iload 14
    //   115: aaload
    //   116: astore 18
    //   118: aload_0
    //   119: iload 14
    //   121: invokespecial 821	com/android/server/audio/AudioService:getDeviceForStream	(I)I
    //   124: istore 13
    //   126: aload 18
    //   128: iload 13
    //   130: invokevirtual 1235	com/android/server/audio/AudioService$VolumeStreamState:getIndex	(I)I
    //   133: istore 12
    //   135: iconst_1
    //   136: istore 11
    //   138: iload_2
    //   139: istore 7
    //   141: iload 14
    //   143: iconst_2
    //   144: if_icmpne +32 -> 176
    //   147: iload_2
    //   148: istore 7
    //   150: aload 5
    //   152: ifnull +24 -> 176
    //   155: aload 5
    //   157: new 884	java/lang/String
    //   160: dup
    //   161: ldc_w 1277
    //   164: invokespecial 1278	java/lang/String:<init>	(Ljava/lang/String;)V
    //   167: invokevirtual 1282	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   170: ifeq +24 -> 194
    //   173: iload_2
    //   174: istore 7
    //   176: iload 13
    //   178: sipush 896
    //   181: iand
    //   182: ifne +141 -> 323
    //   185: iload_3
    //   186: bipush 64
    //   188: iand
    //   189: ifeq +134 -> 323
    //   192: return
    //   193: return
    //   194: iload_2
    //   195: iconst_m1
    //   196: if_icmpne +101 -> 297
    //   199: iload 12
    //   201: aload_0
    //   202: getfield 1010	com/android/server/audio/AudioService:mOnePlusMinRingVolumeIndex	I
    //   205: bipush 10
    //   207: imul
    //   208: if_icmpgt +89 -> 297
    //   211: ldc -5
    //   213: new 1240	java/lang/StringBuilder
    //   216: dup
    //   217: invokespecial 1241	java/lang/StringBuilder:<init>	()V
    //   220: ldc_w 1284
    //   223: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   226: aload_0
    //   227: getfield 1010	com/android/server/audio/AudioService:mOnePlusMinRingVolumeIndex	I
    //   230: invokevirtual 1250	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   233: ldc_w 1286
    //   236: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   239: aload_0
    //   240: getfield 1012	com/android/server/audio/AudioService:mOnePlusMaxRingVolumeIndex	I
    //   243: invokevirtual 1250	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   246: ldc_w 1288
    //   249: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   252: invokevirtual 1260	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   255: invokestatic 1291	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   258: pop
    //   259: iconst_0
    //   260: istore 8
    //   262: iload 8
    //   264: bipush -100
    //   266: if_icmpeq +14 -> 280
    //   269: iload 8
    //   271: istore 7
    //   273: iload 8
    //   275: bipush 100
    //   277: if_icmpne -101 -> 176
    //   280: iload 8
    //   282: istore 7
    //   284: aload_0
    //   285: getfield 1014	com/android/server/audio/AudioService:mOnePlusFixedRingerMode	Z
    //   288: ifeq -112 -> 176
    //   291: iconst_0
    //   292: istore 7
    //   294: goto -118 -> 176
    //   297: iload_2
    //   298: istore 8
    //   300: iload_2
    //   301: iconst_1
    //   302: if_icmpne -40 -> 262
    //   305: iload_2
    //   306: istore 8
    //   308: iload 12
    //   310: aload_0
    //   311: getfield 1012	com/android/server/audio/AudioService:mOnePlusMaxRingVolumeIndex	I
    //   314: bipush 10
    //   316: imul
    //   317: if_icmplt -55 -> 262
    //   320: goto -109 -> 211
    //   323: iload 6
    //   325: istore_2
    //   326: iload 6
    //   328: sipush 1000
    //   331: if_icmpne +16 -> 347
    //   334: aload_0
    //   335: invokespecial 1294	com/android/server/audio/AudioService:getCurrentUserId	()I
    //   338: iload 6
    //   340: invokestatic 1297	android/os/UserHandle:getAppId	(I)I
    //   343: invokestatic 1300	android/os/UserHandle:getUid	(II)I
    //   346: istore_2
    //   347: aload_0
    //   348: getfield 1030	com/android/server/audio/AudioService:mAppOps	Landroid/app/AppOpsManager;
    //   351: getstatic 875	com/android/server/audio/AudioService:STREAM_VOLUME_OPS	[I
    //   354: iload 14
    //   356: iaload
    //   357: iload_2
    //   358: aload 4
    //   360: invokevirtual 1304	android/app/AppOpsManager:noteOp	(IILjava/lang/String;)I
    //   363: ifeq +4 -> 367
    //   366: return
    //   367: aload_0
    //   368: getfield 1114	com/android/server/audio/AudioService:mSafeMediaVolumeState	Ljava/lang/Integer;
    //   371: astore 19
    //   373: aload 19
    //   375: monitorenter
    //   376: aload_0
    //   377: aconst_null
    //   378: putfield 1306	com/android/server/audio/AudioService:mPendingVolumeCommand	Lcom/android/server/audio/AudioService$StreamVolumeCommand;
    //   381: aload 19
    //   383: monitorexit
    //   384: iload_3
    //   385: bipush -33
    //   387: iand
    //   388: istore_2
    //   389: iload 14
    //   391: iconst_3
    //   392: if_icmpne +404 -> 796
    //   395: aload_0
    //   396: getfield 965	com/android/server/audio/AudioService:mFixedVolumeDevices	I
    //   399: iload 13
    //   401: iand
    //   402: ifeq +394 -> 796
    //   405: iload_2
    //   406: bipush 32
    //   408: ior
    //   409: istore 6
    //   411: aload_0
    //   412: getfield 1114	com/android/server/audio/AudioService:mSafeMediaVolumeState	Ljava/lang/Integer;
    //   415: invokevirtual 1309	java/lang/Integer:intValue	()I
    //   418: iconst_3
    //   419: if_icmpne +368 -> 787
    //   422: iload 13
    //   424: bipush 12
    //   426: iand
    //   427: ifeq +360 -> 787
    //   430: aload_0
    //   431: getfield 1117	com/android/server/audio/AudioService:mSafeMediaVolumeIndex	I
    //   434: istore_3
    //   435: iload 12
    //   437: istore 9
    //   439: iload_3
    //   440: istore 10
    //   442: iload 6
    //   444: istore_2
    //   445: iload 12
    //   447: ifeq +12 -> 459
    //   450: iload_3
    //   451: istore 9
    //   453: iload 6
    //   455: istore_2
    //   456: iload_3
    //   457: istore 10
    //   459: iload_2
    //   460: iconst_2
    //   461: iand
    //   462: ifne +18 -> 480
    //   465: iload 11
    //   467: istore 8
    //   469: iload_2
    //   470: istore_3
    //   471: iload 14
    //   473: aload_0
    //   474: invokevirtual 1312	com/android/server/audio/AudioService:getUiSoundsStreamType	()I
    //   477: if_icmpne +91 -> 568
    //   480: iload_2
    //   481: istore_3
    //   482: aload_0
    //   483: invokevirtual 1149	com/android/server/audio/AudioService:getRingerModeInternal	()I
    //   486: iconst_1
    //   487: if_icmpne +8 -> 495
    //   490: iload_2
    //   491: bipush -17
    //   493: iand
    //   494: istore_3
    //   495: aload_0
    //   496: iload 9
    //   498: iload 7
    //   500: iload 10
    //   502: aload 18
    //   504: invokestatic 1315	com/android/server/audio/AudioService$VolumeStreamState:-get3	(Lcom/android/server/audio/AudioService$VolumeStreamState;)Z
    //   507: aload 4
    //   509: iload_3
    //   510: invokespecial 1319	com/android/server/audio/AudioService:checkForRingerModeChange	(IIIZLjava/lang/String;I)I
    //   513: istore 11
    //   515: iload 11
    //   517: iconst_1
    //   518: iand
    //   519: ifeq +295 -> 814
    //   522: iconst_1
    //   523: istore_2
    //   524: iload_3
    //   525: istore 6
    //   527: iload 11
    //   529: sipush 128
    //   532: iand
    //   533: ifeq +10 -> 543
    //   536: iload_3
    //   537: sipush 128
    //   540: ior
    //   541: istore 6
    //   543: iload_2
    //   544: istore 8
    //   546: iload 6
    //   548: istore_3
    //   549: iload 11
    //   551: sipush 2048
    //   554: iand
    //   555: ifeq +13 -> 568
    //   558: iload 6
    //   560: sipush 2048
    //   563: ior
    //   564: istore_3
    //   565: iload_2
    //   566: istore 8
    //   568: aload_0
    //   569: iload 14
    //   571: iload_3
    //   572: invokespecial 1323	com/android/server/audio/AudioService:volumeAdjustmentAllowedByDnd	(II)Z
    //   575: ifne +6 -> 581
    //   578: iconst_0
    //   579: istore 8
    //   581: aload_0
    //   582: getfield 542	com/android/server/audio/AudioService:mStreamStates	[Lcom/android/server/audio/AudioService$VolumeStreamState;
    //   585: iload_1
    //   586: aaload
    //   587: iload 13
    //   589: invokevirtual 1235	com/android/server/audio/AudioService$VolumeStreamState:getIndex	(I)I
    //   592: istore 11
    //   594: iload 8
    //   596: ifeq +466 -> 1062
    //   599: iload 7
    //   601: ifeq +461 -> 1062
    //   604: aload_0
    //   605: getfield 578	com/android/server/audio/AudioService:mAudioHandler	Lcom/android/server/audio/AudioService$AudioHandler;
    //   608: bipush 24
    //   610: invokevirtual 1326	com/android/server/audio/AudioService$AudioHandler:removeMessages	(I)V
    //   613: iload 14
    //   615: iconst_3
    //   616: if_icmpne +54 -> 670
    //   619: iload 13
    //   621: sipush 896
    //   624: iand
    //   625: ifeq +45 -> 670
    //   628: iload_3
    //   629: bipush 64
    //   631: iand
    //   632: ifne +38 -> 670
    //   635: aload_0
    //   636: getfield 570	com/android/server/audio/AudioService:mA2dpAvrcpLock	Ljava/lang/Object;
    //   639: astore 4
    //   641: aload 4
    //   643: monitorenter
    //   644: aload_0
    //   645: getfield 553	com/android/server/audio/AudioService:mA2dp	Landroid/bluetooth/BluetoothA2dp;
    //   648: ifnull +19 -> 667
    //   651: aload_0
    //   652: getfield 434	com/android/server/audio/AudioService:mAvrcpAbsVolSupported	Z
    //   655: ifeq +12 -> 667
    //   658: aload_0
    //   659: getfield 553	com/android/server/audio/AudioService:mA2dp	Landroid/bluetooth/BluetoothA2dp;
    //   662: iload 7
    //   664: invokevirtual 1331	android/bluetooth/BluetoothA2dp:adjustAvrcpAbsoluteVolume	(I)V
    //   667: aload 4
    //   669: monitorexit
    //   670: iload 15
    //   672: ifeq +190 -> 862
    //   675: iload 7
    //   677: bipush 101
    //   679: if_icmpne +154 -> 833
    //   682: aload 18
    //   684: invokestatic 1315	com/android/server/audio/AudioService$VolumeStreamState:-get3	(Lcom/android/server/audio/AudioService$VolumeStreamState;)Z
    //   687: ifeq +140 -> 827
    //   690: iconst_0
    //   691: istore 15
    //   693: iload 14
    //   695: iconst_3
    //   696: if_icmpne +9 -> 705
    //   699: aload_0
    //   700: iload 15
    //   702: invokespecial 1334	com/android/server/audio/AudioService:setSystemAudioMute	(Z)V
    //   705: iconst_0
    //   706: istore 6
    //   708: iload 6
    //   710: aload_0
    //   711: getfield 542	com/android/server/audio/AudioService:mStreamStates	[Lcom/android/server/audio/AudioService$VolumeStreamState;
    //   714: arraylength
    //   715: if_icmpge +229 -> 944
    //   718: iload 14
    //   720: aload_0
    //   721: getfield 546	com/android/server/audio/AudioService:mStreamVolumeAlias	[I
    //   724: iload 6
    //   726: iaload
    //   727: if_icmpne +43 -> 770
    //   730: aload_0
    //   731: invokespecial 1099	com/android/server/audio/AudioService:readCameraSoundForced	()Z
    //   734: ifeq +123 -> 857
    //   737: aload_0
    //   738: getfield 542	com/android/server/audio/AudioService:mStreamStates	[Lcom/android/server/audio/AudioService$VolumeStreamState;
    //   741: iload 6
    //   743: aaload
    //   744: invokevirtual 1337	com/android/server/audio/AudioService$VolumeStreamState:getStreamType	()I
    //   747: bipush 7
    //   749: if_icmpne +103 -> 852
    //   752: iconst_1
    //   753: istore_2
    //   754: iload_2
    //   755: ifne +15 -> 770
    //   758: aload_0
    //   759: getfield 542	com/android/server/audio/AudioService:mStreamStates	[Lcom/android/server/audio/AudioService$VolumeStreamState;
    //   762: iload 6
    //   764: aaload
    //   765: iload 15
    //   767: invokevirtual 1340	com/android/server/audio/AudioService$VolumeStreamState:mute	(Z)V
    //   770: iload 6
    //   772: iconst_1
    //   773: iadd
    //   774: istore 6
    //   776: goto -68 -> 708
    //   779: astore 4
    //   781: aload 19
    //   783: monitorexit
    //   784: aload 4
    //   786: athrow
    //   787: aload 18
    //   789: invokevirtual 1343	com/android/server/audio/AudioService$VolumeStreamState:getMaxIndex	()I
    //   792: istore_3
    //   793: goto -358 -> 435
    //   796: aload_0
    //   797: bipush 10
    //   799: iload_1
    //   800: iload 14
    //   802: invokespecial 831	com/android/server/audio/AudioService:rescaleIndex	(III)I
    //   805: istore 10
    //   807: iload 12
    //   809: istore 9
    //   811: goto -352 -> 459
    //   814: iconst_0
    //   815: istore_2
    //   816: goto -292 -> 524
    //   819: astore 5
    //   821: aload 4
    //   823: monitorexit
    //   824: aload 5
    //   826: athrow
    //   827: iconst_1
    //   828: istore 15
    //   830: goto -137 -> 693
    //   833: iload 7
    //   835: bipush -100
    //   837: if_icmpne +9 -> 846
    //   840: iconst_1
    //   841: istore 15
    //   843: goto -150 -> 693
    //   846: iconst_0
    //   847: istore 15
    //   849: goto -156 -> 693
    //   852: iconst_0
    //   853: istore_2
    //   854: goto -100 -> 754
    //   857: iconst_0
    //   858: istore_2
    //   859: goto -105 -> 754
    //   862: iload 7
    //   864: iconst_1
    //   865: if_icmpne +19 -> 884
    //   868: aload_0
    //   869: iload 14
    //   871: iload 9
    //   873: iload 10
    //   875: iadd
    //   876: iload 13
    //   878: invokespecial 1347	com/android/server/audio/AudioService:checkSafeMediaVolume	(III)Z
    //   881: ifeq +201 -> 1082
    //   884: aload 18
    //   886: iload 7
    //   888: iload 10
    //   890: imul
    //   891: iload 13
    //   893: aload 5
    //   895: invokevirtual 1351	com/android/server/audio/AudioService$VolumeStreamState:adjustIndex	(IILjava/lang/String;)Z
    //   898: ifne +11 -> 909
    //   901: aload 18
    //   903: invokestatic 1315	com/android/server/audio/AudioService$VolumeStreamState:-get3	(Lcom/android/server/audio/AudioService$VolumeStreamState;)Z
    //   906: ifeq +38 -> 944
    //   909: aload 18
    //   911: invokestatic 1315	com/android/server/audio/AudioService$VolumeStreamState:-get3	(Lcom/android/server/audio/AudioService$VolumeStreamState;)Z
    //   914: ifeq +15 -> 929
    //   917: iload 7
    //   919: iconst_1
    //   920: if_icmpne +200 -> 1120
    //   923: aload 18
    //   925: iconst_0
    //   926: invokevirtual 1340	com/android/server/audio/AudioService$VolumeStreamState:mute	(Z)V
    //   929: aload_0
    //   930: getfield 578	com/android/server/audio/AudioService:mAudioHandler	Lcom/android/server/audio/AudioService$AudioHandler;
    //   933: iconst_0
    //   934: iconst_2
    //   935: iload 13
    //   937: iconst_0
    //   938: aload 18
    //   940: iconst_0
    //   941: invokestatic 776	com/android/server/audio/AudioService:sendMsg	(Landroid/os/Handler;IIIILjava/lang/Object;I)V
    //   944: aload_0
    //   945: getfield 542	com/android/server/audio/AudioService:mStreamStates	[Lcom/android/server/audio/AudioService$VolumeStreamState;
    //   948: iload_1
    //   949: aaload
    //   950: iload 13
    //   952: invokevirtual 1235	com/android/server/audio/AudioService$VolumeStreamState:getIndex	(I)I
    //   955: istore_2
    //   956: iload 14
    //   958: iconst_3
    //   959: if_icmpne +16 -> 975
    //   962: aload_0
    //   963: iload 11
    //   965: iload_2
    //   966: aload_0
    //   967: iload_1
    //   968: invokevirtual 1354	com/android/server/audio/AudioService:getStreamMaxVolume	(I)I
    //   971: iload_3
    //   972: invokespecial 1358	com/android/server/audio/AudioService:setSystemAudioVolume	(IIII)V
    //   975: aload_0
    //   976: getfield 485	com/android/server/audio/AudioService:mHdmiManager	Landroid/hardware/hdmi/HdmiControlManager;
    //   979: ifnull +83 -> 1062
    //   982: aload_0
    //   983: getfield 485	com/android/server/audio/AudioService:mHdmiManager	Landroid/hardware/hdmi/HdmiControlManager;
    //   986: astore 4
    //   988: aload 4
    //   990: monitorenter
    //   991: aload_0
    //   992: getfield 481	com/android/server/audio/AudioService:mHdmiCecSink	Z
    //   995: ifeq +64 -> 1059
    //   998: iload 14
    //   1000: iconst_3
    //   1001: if_icmpne +58 -> 1059
    //   1004: iload 11
    //   1006: iload_2
    //   1007: if_icmpeq +52 -> 1059
    //   1010: aload_0
    //   1011: getfield 1360	com/android/server/audio/AudioService:mHdmiPlaybackClient	Landroid/hardware/hdmi/HdmiPlaybackClient;
    //   1014: astore 5
    //   1016: aload 5
    //   1018: monitorenter
    //   1019: iload 7
    //   1021: iconst_m1
    //   1022: if_icmpne +132 -> 1154
    //   1025: bipush 25
    //   1027: istore_2
    //   1028: invokestatic 1366	android/os/Binder:clearCallingIdentity	()J
    //   1031: lstore 16
    //   1033: aload_0
    //   1034: getfield 1360	com/android/server/audio/AudioService:mHdmiPlaybackClient	Landroid/hardware/hdmi/HdmiPlaybackClient;
    //   1037: iload_2
    //   1038: iconst_1
    //   1039: invokevirtual 1371	android/hardware/hdmi/HdmiPlaybackClient:sendKeyEvent	(IZ)V
    //   1042: aload_0
    //   1043: getfield 1360	com/android/server/audio/AudioService:mHdmiPlaybackClient	Landroid/hardware/hdmi/HdmiPlaybackClient;
    //   1046: iload_2
    //   1047: iconst_0
    //   1048: invokevirtual 1371	android/hardware/hdmi/HdmiPlaybackClient:sendKeyEvent	(IZ)V
    //   1051: lload 16
    //   1053: invokestatic 1374	android/os/Binder:restoreCallingIdentity	(J)V
    //   1056: aload 5
    //   1058: monitorexit
    //   1059: aload 4
    //   1061: monitorexit
    //   1062: aload_0
    //   1063: iload_1
    //   1064: iload 11
    //   1066: aload_0
    //   1067: getfield 542	com/android/server/audio/AudioService:mStreamStates	[Lcom/android/server/audio/AudioService$VolumeStreamState;
    //   1070: iload_1
    //   1071: aaload
    //   1072: iload 13
    //   1074: invokevirtual 1235	com/android/server/audio/AudioService$VolumeStreamState:getIndex	(I)I
    //   1077: iload_3
    //   1078: invokespecial 1377	com/android/server/audio/AudioService:sendVolumeUpdate	(IIII)V
    //   1081: return
    //   1082: ldc -5
    //   1084: new 1240	java/lang/StringBuilder
    //   1087: dup
    //   1088: invokespecial 1241	java/lang/StringBuilder:<init>	()V
    //   1091: ldc_w 1379
    //   1094: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1097: iload 11
    //   1099: invokevirtual 1250	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1102: invokevirtual 1260	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1105: invokestatic 1291	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1108: pop
    //   1109: aload_0
    //   1110: getfield 563	com/android/server/audio/AudioService:mVolumeController	Lcom/android/server/audio/AudioService$VolumeController;
    //   1113: iload_3
    //   1114: invokevirtual 1382	com/android/server/audio/AudioService$VolumeController:postDisplaySafeVolumeWarning	(I)V
    //   1117: goto -173 -> 944
    //   1120: iload 7
    //   1122: iconst_m1
    //   1123: if_icmpne -194 -> 929
    //   1126: aload_0
    //   1127: getfield 1038	com/android/server/audio/AudioService:mPlatformType	I
    //   1130: iconst_2
    //   1131: if_icmpne -202 -> 929
    //   1134: aload_0
    //   1135: getfield 578	com/android/server/audio/AudioService:mAudioHandler	Lcom/android/server/audio/AudioService$AudioHandler;
    //   1138: bipush 24
    //   1140: iconst_2
    //   1141: iload 14
    //   1143: iload_3
    //   1144: aconst_null
    //   1145: sipush 350
    //   1148: invokestatic 776	com/android/server/audio/AudioService:sendMsg	(Landroid/os/Handler;IIIILjava/lang/Object;I)V
    //   1151: goto -222 -> 929
    //   1154: bipush 24
    //   1156: istore_2
    //   1157: goto -129 -> 1028
    //   1160: astore 18
    //   1162: lload 16
    //   1164: invokestatic 1374	android/os/Binder:restoreCallingIdentity	(J)V
    //   1167: aload 18
    //   1169: athrow
    //   1170: astore 18
    //   1172: aload 5
    //   1174: monitorexit
    //   1175: aload 18
    //   1177: athrow
    //   1178: astore 5
    //   1180: aload 4
    //   1182: monitorexit
    //   1183: aload 5
    //   1185: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1186	0	this	AudioService
    //   0	1186	1	paramInt1	int
    //   0	1186	2	paramInt2	int
    //   0	1186	3	paramInt3	int
    //   0	1186	4	paramString1	String
    //   0	1186	5	paramString2	String
    //   0	1186	6	paramInt4	int
    //   139	985	7	i	int
    //   260	335	8	j	int
    //   437	439	9	k	int
    //   440	451	10	m	int
    //   136	962	11	n	int
    //   133	675	12	i1	int
    //   124	949	13	i2	int
    //   107	1035	14	i3	int
    //   86	762	15	bool	boolean
    //   1031	132	16	l	long
    //   116	823	18	localVolumeStreamState	VolumeStreamState
    //   1160	8	18	localObject1	Object
    //   1170	6	18	localObject2	Object
    //   371	411	19	localInteger	Integer
    // Exception table:
    //   from	to	target	type
    //   376	381	779	finally
    //   644	667	819	finally
    //   1033	1051	1160	finally
    //   1028	1033	1170	finally
    //   1051	1056	1170	finally
    //   1162	1170	1170	finally
    //   991	998	1178	finally
    //   1010	1019	1178	finally
    //   1056	1059	1178	finally
    //   1172	1178	1178	finally
  }
  
  private void adjustSuggestedStreamVolume(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, int paramInt4)
  {
    if (DEBUG_VOL) {
      Log.d("AudioService", "adjustSuggestedStreamVolume() stream=" + paramInt2 + ", flags=" + paramInt3 + ", caller=" + paramString2);
    }
    boolean bool = isMuteAdjust(paramInt1);
    if (this.mVolumeControlStream != -1) {}
    for (int i = this.mVolumeControlStream;; i = getActiveStreamType(paramInt2))
    {
      ensureValidStreamType(i);
      int j = this.mStreamVolumeAlias[i];
      paramInt2 = paramInt3;
      if ((paramInt3 & 0x4) != 0)
      {
        paramInt2 = paramInt3;
        if (j != 2) {
          paramInt2 = paramInt3 & 0xFFFFFFFB;
        }
      }
      paramInt3 = paramInt2;
      if (this.mVolumeController.suppressAdjustment(j, paramInt2, bool))
      {
        j = 0;
        paramInt2 = paramInt2 & 0xFFFFFFFB & 0xFFFFFFEF;
        paramInt1 = j;
        paramInt3 = paramInt2;
        if (DEBUG_VOL)
        {
          Log.d("AudioService", "Volume controller suppressed adjustment");
          paramInt3 = paramInt2;
          paramInt1 = j;
        }
      }
      adjustStreamVolume(i, paramInt1, paramInt3, paramString1, paramString2, paramInt4);
      return;
    }
  }
  
  private void broadcastMasterMuteStatus(boolean paramBoolean)
  {
    Intent localIntent = new Intent("android.media.MASTER_MUTE_CHANGED_ACTION");
    localIntent.putExtra("android.media.EXTRA_MASTER_VOLUME_MUTED", paramBoolean);
    localIntent.addFlags(603979776);
    sendStickyBroadcastToAll(localIntent);
  }
  
  private void broadcastRingerMode(String paramString, int paramInt)
  {
    paramString = new Intent(paramString);
    paramString.putExtra("android.media.EXTRA_RINGER_MODE", paramInt);
    paramString.addFlags(603979776);
    sendStickyBroadcastToAll(paramString);
  }
  
  private void broadcastScoConnectionState(int paramInt)
  {
    sendMsg(this.mAudioHandler, 19, 2, paramInt, 0, null, 0);
  }
  
  private void broadcastVibrateSetting(int paramInt)
  {
    if (ActivityManagerNative.isSystemReady())
    {
      Intent localIntent = new Intent("android.media.VIBRATE_SETTING_CHANGED");
      localIntent.putExtra("android.media.EXTRA_VIBRATE_TYPE", paramInt);
      localIntent.putExtra("android.media.EXTRA_VIBRATE_SETTING", getVibrateSetting(paramInt));
      sendBroadcastToAll(localIntent);
    }
  }
  
  private void cancelA2dpDeviceTimeout()
  {
    this.mAudioHandler.removeMessages(6);
  }
  
  private void checkAllAliasStreamVolumes()
  {
    try
    {
      int j = AudioSystem.getNumStreamTypes();
      int i = 0;
      while (i < j)
      {
        if (i != this.mStreamVolumeAlias[i]) {
          this.mStreamStates[i].setAllIndexes(this.mStreamStates[this.mStreamVolumeAlias[i]], "AudioService");
        }
        if (!VolumeStreamState.-get3(this.mStreamStates[i])) {
          this.mStreamStates[i].applyAllVolumes();
        }
        i += 1;
      }
      return;
    }
    finally {}
  }
  
  private void checkAllFixedVolumeDevices()
  {
    int j = AudioSystem.getNumStreamTypes();
    int i = 0;
    while (i < j)
    {
      this.mStreamStates[i].checkFixedVolumeDevices();
      i += 1;
    }
  }
  
  private void checkAllFixedVolumeDevices(int paramInt)
  {
    this.mStreamStates[paramInt].checkFixedVolumeDevices();
  }
  
  private int checkForRingerModeChange(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, String paramString, int paramInt4)
  {
    int m;
    int j;
    int n;
    int i1;
    int k;
    int i;
    if (this.mPlatformType == 2)
    {
      m = 1;
      j = 1;
      n = 1;
      i1 = 1;
      k = getRingerModeInternal();
      switch (k)
      {
      default: 
        Log.e("AudioService", "checkForRingerModeChange() wrong ringer mode: " + k);
        i = k;
        j = i1;
        label91:
        if ((isAndroidNPlus(paramString)) && (wouldToggleZenMode(i)) && (!this.mNm.isNotificationPolicyAccessGrantedForPackage(paramString))) {
          break;
        }
      }
    }
    label588:
    while ((paramInt4 & 0x1000) != 0)
    {
      setRingerMode(i, "AudioService.checkForRingerModeChange", false);
      this.mPrevVolDirection = paramInt2;
      return j;
      m = 0;
      break;
      if (paramInt2 == -1)
      {
        if (this.mHasVibrator)
        {
          j = i1;
          i = k;
          if (paramInt3 > paramInt1) {
            break label91;
          }
          j = i1;
          i = k;
          if (paramInt1 >= paramInt3 * 2) {
            break label91;
          }
          i = 1;
          this.mLoweredFromNormalToVibrateTime = SystemClock.uptimeMillis();
          j = i1;
          break label91;
        }
        j = i1;
        i = k;
        if (paramInt1 != paramInt3) {
          break label91;
        }
        j = i1;
        i = k;
        if (!this.mVolumePolicy.volumeDownToEnterSilent) {
          break label91;
        }
        i = 0;
        j = i1;
        break label91;
      }
      j = i1;
      i = k;
      if (m == 0) {
        break label91;
      }
      if (paramInt2 != 101)
      {
        j = i1;
        i = k;
        if (paramInt2 != -100) {
          break label91;
        }
      }
      if (this.mHasVibrator) {}
      for (i = 1;; i = 0)
      {
        j = 0;
        break;
      }
      if (!this.mHasVibrator)
      {
        Log.e("AudioService", "checkForRingerModeChange() current ringer mode is vibratebut no vibrator is present");
        j = i1;
        i = k;
        break label91;
      }
      if (paramInt2 == -1)
      {
        if ((m != 0) && (paramInt1 >= paramInt3 * 2) && (paramBoolean))
        {
          i = 2;
          paramInt1 = j;
        }
        for (;;)
        {
          j = paramInt1 & 0xFFFFFFFE;
          break;
          paramInt1 = j;
          i = k;
          if (this.mPrevVolDirection != -1) {
            if (this.mVolumePolicy.volumeDownToEnterSilent)
            {
              paramInt1 = j;
              i = k;
              if (SystemClock.uptimeMillis() - this.mLoweredFromNormalToVibrateTime > this.mVolumePolicy.vibrateToSilentDebounce)
              {
                paramInt1 = j;
                i = k;
                if (this.mRingerModeDelegate != null)
                {
                  paramInt1 = j;
                  i = k;
                  if (this.mRingerModeDelegate.canVolumeDownEnterSilent())
                  {
                    i = 0;
                    paramInt1 = j;
                  }
                }
              }
            }
            else
            {
              paramInt1 = 2049;
              i = k;
            }
          }
        }
      }
      if ((paramInt2 == 1) || (paramInt2 == 101)) {}
      for (;;)
      {
        i = 2;
        paramInt1 = j;
        break;
        paramInt1 = j;
        i = k;
        if (paramInt2 != 100) {
          break;
        }
      }
      if ((m != 0) && (paramInt2 == -1) && (paramInt1 >= paramInt3 * 2) && (paramBoolean))
      {
        i = 2;
        paramInt1 = n;
      }
      for (;;)
      {
        j = paramInt1 & 0xFFFFFFFE;
        break;
        if ((paramInt2 == 1) || (paramInt2 == 101)) {}
        for (;;)
        {
          if (this.mVolumePolicy.volumeUpToExitSilent) {
            break label588;
          }
          paramInt1 = 129;
          i = k;
          break;
          paramInt1 = n;
          i = k;
          if (paramInt2 != 100) {
            break;
          }
        }
        if ((this.mHasVibrator) && (paramInt2 == 1))
        {
          i = 1;
          paramInt1 = n;
        }
        else
        {
          i = 2;
          paramInt1 = n;
        }
      }
    }
    throw new SecurityException("Not allowed to change Do Not Disturb state");
  }
  
  private void checkMuteAffectedStreams()
  {
    int i = 0;
    while (i < this.mStreamStates.length)
    {
      VolumeStreamState localVolumeStreamState = this.mStreamStates[i];
      if (VolumeStreamState.-get2(localVolumeStreamState) > 0) {
        this.mMuteAffectedStreams &= 1 << VolumeStreamState.-get4(localVolumeStreamState);
      }
      i += 1;
    }
  }
  
  private boolean checkSafeMediaVolume(int paramInt1, int paramInt2, int paramInt3)
  {
    synchronized (this.mSafeMediaVolumeState)
    {
      if ((this.mSafeMediaVolumeState.intValue() == 3) && (this.mStreamVolumeAlias[paramInt1] == 3) && ((paramInt3 & 0xC) != 0))
      {
        paramInt1 = this.mSafeMediaVolumeIndex;
        if (paramInt2 > paramInt1) {
          return false;
        }
      }
      return true;
    }
  }
  
  private void checkScoAudioState()
  {
    if ((this.mBluetoothHeadset != null) && (this.mBluetoothHeadsetDevice != null) && (this.mScoAudioState == 0) && (this.mBluetoothHeadset.getAudioState(this.mBluetoothHeadsetDevice) != 10)) {
      this.mScoAudioState = 2;
    }
  }
  
  private int checkSendBecomingNoisyIntent(int paramInt1, int paramInt2)
  {
    int k = 0;
    int i = k;
    if (paramInt2 == 0)
    {
      i = k;
      if ((this.mBecomingNoisyIntentDevices & paramInt1) != 0)
      {
        paramInt2 = 0;
        Log.d("AudioService", "checkSendBecomingNoisyIntent update the noise");
        i = 0;
        while (i < this.mConnectedDevices.size())
        {
          int m = ((DeviceListSpec)this.mConnectedDevices.valueAt(i)).mDeviceType;
          int j = paramInt2;
          if ((0x80000000 & m) == 0)
          {
            j = paramInt2;
            if ((this.mBecomingNoisyIntentDevices & m) != 0) {
              j = paramInt2 | m;
            }
          }
          i += 1;
          paramInt2 = j;
        }
        i = k;
        if (paramInt2 == paramInt1)
        {
          sendMsg(this.mAudioHandler, 15, 0, 0, 0, null, 0);
          i = SystemProperties.getInt("audio.noisy.broadcast.delay", 700);
        }
      }
    }
    if ((!this.mAudioHandler.hasMessages(101)) && (!this.mAudioHandler.hasMessages(102)))
    {
      paramInt1 = i;
      if (!this.mAudioHandler.hasMessages(100)) {
        break label221;
      }
    }
    synchronized (mLastDeviceConnectMsgTime)
    {
      long l1 = SystemClock.uptimeMillis();
      if (mLastDeviceConnectMsgTime.longValue() > l1)
      {
        long l2 = mLastDeviceConnectMsgTime.longValue();
        i = (int)(l2 - l1) + 30;
      }
      paramInt1 = i;
      label221:
      return paramInt1;
    }
  }
  
  private void configureHdmiPlugIntent(Intent paramIntent, int paramInt)
  {
    paramIntent.setAction("android.media.action.HDMI_AUDIO_PLUG");
    paramIntent.putExtra("android.media.extra.AUDIO_PLUG_STATE", paramInt);
    if (paramInt == 1)
    {
      Object localObject1 = new ArrayList();
      if (AudioSystem.listAudioPorts((ArrayList)localObject1, new int[1]) == 0)
      {
        localObject1 = ((Iterable)localObject1).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          Object localObject2 = (AudioPort)((Iterator)localObject1).next();
          if ((localObject2 instanceof AudioDevicePort))
          {
            localObject2 = (AudioDevicePort)localObject2;
            if ((((AudioDevicePort)localObject2).type() == 1024) || (((AudioDevicePort)localObject2).type() == 262144))
            {
              int[] arrayOfInt = AudioFormat.filterPublicFormats(((AudioDevicePort)localObject2).formats());
              int j;
              if (arrayOfInt.length > 0)
              {
                ArrayList localArrayList = new ArrayList(1);
                paramInt = 0;
                i = arrayOfInt.length;
                while (paramInt < i)
                {
                  j = arrayOfInt[paramInt];
                  if (j != 0) {
                    localArrayList.add(Integer.valueOf(j));
                  }
                  paramInt += 1;
                }
                arrayOfInt = new int[localArrayList.size()];
                paramInt = 0;
                while (paramInt < arrayOfInt.length)
                {
                  arrayOfInt[paramInt] = ((Integer)localArrayList.get(paramInt)).intValue();
                  paramInt += 1;
                }
                paramIntent.putExtra("android.media.extra.ENCODINGS", arrayOfInt);
              }
              int i = 0;
              localObject2 = ((AudioDevicePort)localObject2).channelMasks();
              paramInt = 0;
              int m = localObject2.length;
              while (paramInt < m)
              {
                int k = AudioFormat.channelCountFromOutChannelMask(localObject2[paramInt]);
                j = i;
                if (k > i) {
                  j = k;
                }
                paramInt += 1;
                i = j;
              }
              paramIntent.putExtra("android.media.extra.MAX_CHANNEL_COUNT", i);
            }
          }
        }
      }
    }
  }
  
  private void createAudioSystemThread()
  {
    this.mAudioSystemThread = new AudioSystemThread();
    this.mAudioSystemThread.start();
    waitForAudioHandlerCreation();
  }
  
  private void createStreamStates()
  {
    int j = AudioSystem.getNumStreamTypes();
    VolumeStreamState[] arrayOfVolumeStreamState = new VolumeStreamState[j];
    this.mStreamStates = arrayOfVolumeStreamState;
    int i = 0;
    while (i < j)
    {
      arrayOfVolumeStreamState[i] = new VolumeStreamState(Settings.System.VOLUME_SETTINGS[this.mStreamVolumeAlias[i]], i, null);
      i += 1;
    }
    checkAllFixedVolumeDevices();
    checkAllAliasStreamVolumes();
    checkMuteAffectedStreams();
  }
  
  private boolean discardRmtSbmxFullVolDeathHandlerFor(IBinder paramIBinder)
  {
    Iterator localIterator = this.mRmtSbmxFullVolDeathHandlers.iterator();
    while (localIterator.hasNext())
    {
      RmtSbmxFullVolDeathHandler localRmtSbmxFullVolDeathHandler = (RmtSbmxFullVolDeathHandler)localIterator.next();
      if (localRmtSbmxFullVolDeathHandler.isHandlerFor(paramIBinder))
      {
        localRmtSbmxFullVolDeathHandler.forget();
        this.mRmtSbmxFullVolDeathHandlers.remove(localRmtSbmxFullVolDeathHandler);
        return true;
      }
    }
    return false;
  }
  
  private void disconnectBluetoothSco(int paramInt)
  {
    for (;;)
    {
      synchronized (this.mScoClients)
      {
        checkScoAudioState();
        if ((this.mScoAudioState == 2) || (this.mScoAudioState == 4))
        {
          if (this.mBluetoothHeadsetDevice != null)
          {
            if (this.mBluetoothHeadset == null) {
              continue;
            }
            if (!this.mBluetoothHeadset.stopVoiceRecognition(this.mBluetoothHeadsetDevice)) {
              sendMsg(this.mAudioHandler, 9, 0, 0, 0, null, 0);
            }
          }
          return;
          if ((this.mScoAudioState != 2) || (!getBluetoothHeadset())) {
            continue;
          }
          this.mScoAudioState = 4;
        }
      }
      clearAllScoClients(paramInt, true);
    }
  }
  
  private void dumpAudioPolicies(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("\nAudio policies:");
    synchronized (this.mAudioPolicies)
    {
      Iterator localIterator = this.mAudioPolicies.values().iterator();
      if (localIterator.hasNext()) {
        paramPrintWriter.println(((AudioPolicyProxy)localIterator.next()).toLogFriendlyString());
      }
    }
  }
  
  private void dumpRingerMode(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("\nRinger mode: ");
    paramPrintWriter.println("- mode (internal) = " + RINGER_MODE_NAMES[this.mRingerMode]);
    paramPrintWriter.println("- mode (external) = " + RINGER_MODE_NAMES[this.mRingerModeExternal]);
    dumpRingerModeStreams(paramPrintWriter, "affected", this.mRingerModeAffectedStreams);
    dumpRingerModeStreams(paramPrintWriter, "muted", this.mRingerModeMutedStreams);
    paramPrintWriter.print("- delegate = ");
    paramPrintWriter.println(this.mRingerModeDelegate);
  }
  
  private void dumpRingerModeStreams(PrintWriter paramPrintWriter, String paramString, int paramInt)
  {
    paramPrintWriter.print("- ringer mode ");
    paramPrintWriter.print(paramString);
    paramPrintWriter.print(" streams = 0x");
    paramPrintWriter.print(Integer.toHexString(paramInt));
    if (paramInt != 0)
    {
      paramPrintWriter.print(" (");
      int k = 1;
      int j = 0;
      int i = paramInt;
      paramInt = j;
      while (paramInt < AudioSystem.STREAM_NAMES.length)
      {
        int n = 1 << paramInt;
        int m = k;
        j = i;
        if ((i & n) != 0)
        {
          if (k == 0) {
            paramPrintWriter.print(',');
          }
          paramPrintWriter.print(AudioSystem.STREAM_NAMES[paramInt]);
          j = i & n;
          m = 0;
        }
        paramInt += 1;
        k = m;
        i = j;
      }
      if (i != 0)
      {
        if (k == 0) {
          paramPrintWriter.print(',');
        }
        paramPrintWriter.print(i);
      }
      paramPrintWriter.print(')');
    }
    paramPrintWriter.println();
  }
  
  private void dumpStreamStates(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("\nStream volumes (device: index)");
    int j = AudioSystem.getNumStreamTypes();
    int i = 0;
    while (i < j)
    {
      paramPrintWriter.println("- " + AudioSystem.STREAM_NAMES[i] + ":");
      VolumeStreamState.-wrap0(this.mStreamStates[i], paramPrintWriter);
      paramPrintWriter.println("");
      i += 1;
    }
    paramPrintWriter.print("\n- mute affected streams = 0x");
    paramPrintWriter.println(Integer.toHexString(this.mMuteAffectedStreams));
  }
  
  private void enforceSafeMediaVolume(String paramString)
  {
    VolumeStreamState localVolumeStreamState = this.mStreamStates[3];
    int j = 12;
    int i = 0;
    while (j != 0)
    {
      int k = i + 1;
      i = 1 << i;
      if ((i & j) == 0)
      {
        i = k;
      }
      else
      {
        if (localVolumeStreamState.getIndex(i) > this.mSafeMediaVolumeIndex)
        {
          localVolumeStreamState.setIndex(this.mSafeMediaVolumeIndex, i, paramString);
          sendMsg(this.mAudioHandler, 0, 2, i, 0, localVolumeStreamState, 0);
        }
        j &= i;
        i = k;
      }
    }
  }
  
  private void enforceVolumeController(String paramString)
  {
    if ((ControllerService.-get0(this.mControllerService) != 0) && (Binder.getCallingUid() == ControllerService.-get0(this.mControllerService))) {
      return;
    }
    this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "Only SystemUI can " + paramString);
  }
  
  private void ensureValidDirection(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Bad direction " + paramInt);
    }
  }
  
  private void ensureValidRingerMode(int paramInt)
  {
    if (!isValidRingerMode(paramInt)) {
      throw new IllegalArgumentException("Bad ringer mode " + paramInt);
    }
  }
  
  private void ensureValidStreamType(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mStreamStates.length)) {
      throw new IllegalArgumentException("Bad stream type " + paramInt);
    }
  }
  
  private int getActiveStreamType(int paramInt)
  {
    switch (this.mPlatformType)
    {
    default: 
      if (!isInCommunication()) {
        break label183;
      }
      if (AudioSystem.getForceUse(0) == 3)
      {
        if (DEBUG_VOL) {
          Log.v("AudioService", "getActiveStreamType: Forcing STREAM_BLUETOOTH_SCO");
        }
        return 6;
      }
      break;
    case 1: 
      if (isInCommunication())
      {
        if (AudioSystem.getForceUse(0) == 3) {
          return 6;
        }
        return 0;
      }
      if (paramInt == Integer.MIN_VALUE)
      {
        if (isAfMusicActiveRecently(StreamOverride.sDelayMs))
        {
          if (DEBUG_VOL) {
            Log.v("AudioService", "getActiveStreamType: Forcing STREAM_MUSIC stream active");
          }
          return 3;
        }
        if (DEBUG_VOL) {
          Log.v("AudioService", "getActiveStreamType: Forcing STREAM_RING b/c default");
        }
        return 2;
      }
      if (!isAfMusicActiveRecently(0)) {
        break label271;
      }
      if (DEBUG_VOL) {
        Log.v("AudioService", "getActiveStreamType: Forcing STREAM_MUSIC stream active");
      }
      return 3;
    case 2: 
      if (paramInt != Integer.MIN_VALUE) {
        break label271;
      }
      return 3;
    }
    if (DEBUG_VOL) {
      Log.v("AudioService", "getActiveStreamType: Forcing STREAM_VOICE_CALL");
    }
    return 0;
    label183:
    if ((AudioSystem.isStreamActive(5, StreamOverride.sDelayMs)) || (AudioSystem.isStreamActive(2, StreamOverride.sDelayMs)))
    {
      if (DEBUG_VOL) {
        Log.v("AudioService", "getActiveStreamType: Forcing STREAM_NOTIFICATION");
      }
      return 5;
    }
    if (paramInt == Integer.MIN_VALUE)
    {
      if (isAfMusicActiveRecently(StreamOverride.sDelayMs))
      {
        if (DEBUG_VOL) {
          Log.v("AudioService", "getActiveStreamType: forcing STREAM_MUSIC");
        }
        return 3;
      }
      if (DEBUG_VOL) {
        Log.v("AudioService", "getActiveStreamType: using STREAM_NOTIFICATION as default");
      }
      return 5;
    }
    label271:
    if (DEBUG_VOL) {
      Log.v("AudioService", "getActiveStreamType: Returning suggested type " + paramInt);
    }
    return paramInt;
  }
  
  private boolean getBluetoothHeadset()
  {
    boolean bool = false;
    Object localObject = BluetoothAdapter.getDefaultAdapter();
    if (localObject != null) {
      bool = ((BluetoothAdapter)localObject).getProfileProxy(this.mContext, this.mBluetoothProfileServiceListener, 1);
    }
    localObject = this.mAudioHandler;
    if (bool) {}
    for (int i = 3000;; i = 0)
    {
      sendMsg((Handler)localObject, 9, 0, 0, 0, null, i);
      return bool;
    }
  }
  
  private int getCurrentUserId()
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      int i = ActivityManagerNative.getDefault().getCurrentUser().id;
      Binder.restoreCallingIdentity(l);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException = localRemoteException;
      Binder.restoreCallingIdentity(l);
      return 0;
    }
    finally
    {
      localObject = finally;
      Binder.restoreCallingIdentity(l);
      throw ((Throwable)localObject);
    }
  }
  
  private int getDeviceForStream(int paramInt)
  {
    int i = getDevicesForStream(paramInt);
    paramInt = i;
    if ((i - 1 & i) != 0)
    {
      if ((i & 0x2) != 0) {
        paramInt = 2;
      }
    }
    else {
      return paramInt;
    }
    if ((0x40000 & i) != 0) {
      return 262144;
    }
    if ((0x80000 & i) != 0) {
      return 524288;
    }
    if ((0x200000 & i) != 0) {
      return 2097152;
    }
    return i & 0x380;
  }
  
  private int getDevicesForStream(int paramInt)
  {
    return getDevicesForStream(paramInt, true);
  }
  
  private int getDevicesForStream(int paramInt, boolean paramBoolean)
  {
    ensureValidStreamType(paramInt);
    try
    {
      paramInt = this.mStreamStates[paramInt].observeDevicesForStream_syncVSS(paramBoolean);
      return paramInt;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private int getNewRingerMode(int paramInt1, int paramInt2, int paramInt3)
  {
    if (((paramInt3 & 0x2) != 0) || (paramInt1 == getUiSoundsStreamType()))
    {
      if (paramInt2 == 0)
      {
        if (this.mHasVibrator) {
          return 1;
        }
        if (this.mVolumePolicy.volumeDownToEnterSilent) {
          return 0;
        }
        return 2;
      }
      return 2;
    }
    return getRingerModeExternal();
  }
  
  private ScoClient getScoClient(IBinder paramIBinder, boolean paramBoolean)
  {
    synchronized (this.mScoClients)
    {
      int j = this.mScoClients.size();
      int i = 0;
      localScoClient = null;
      for (;;)
      {
        if (i < j) {}
        try
        {
          localScoClient = (ScoClient)this.mScoClients.get(i);
          IBinder localIBinder = localScoClient.getBinder();
          if (localIBinder == paramIBinder) {
            return localScoClient;
          }
          i += 1;
        }
        finally
        {
          for (;;)
          {
            continue;
            paramIBinder = localScoClient;
          }
        }
      }
      if (paramBoolean)
      {
        paramIBinder = new ScoClient(paramIBinder);
        this.mScoClients.add(paramIBinder);
        return paramIBinder;
      }
    }
  }
  
  /* Error */
  private void handleConfigurationChanged(Context paramContext)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 1081	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   4: invokevirtual 1832	android/content/res/Resources:getConfiguration	()Landroid/content/res/Configuration;
    //   7: astore 4
    //   9: aload_0
    //   10: getfield 1189	com/android/server/audio/AudioService:mMonitorOrientation	Z
    //   13: ifeq +26 -> 39
    //   16: aload 4
    //   18: getfield 1837	android/content/res/Configuration:orientation	I
    //   21: istore_2
    //   22: iload_2
    //   23: aload_0
    //   24: getfield 950	com/android/server/audio/AudioService:mDeviceOrientation	I
    //   27: if_icmpeq +12 -> 39
    //   30: aload_0
    //   31: iload_2
    //   32: putfield 950	com/android/server/audio/AudioService:mDeviceOrientation	I
    //   35: aload_0
    //   36: invokespecial 1198	com/android/server/audio/AudioService:setOrientationForAudioSystem	()V
    //   39: aload_0
    //   40: getfield 578	com/android/server/audio/AudioService:mAudioHandler	Lcom/android/server/audio/AudioService$AudioHandler;
    //   43: bipush 16
    //   45: iconst_0
    //   46: iconst_0
    //   47: iconst_0
    //   48: ldc -5
    //   50: iconst_0
    //   51: invokestatic 776	com/android/server/audio/AudioService:sendMsg	(Landroid/os/Handler;IIIILjava/lang/Object;I)V
    //   54: aload_0
    //   55: invokespecial 1099	com/android/server/audio/AudioService:readCameraSoundForced	()Z
    //   58: istore_3
    //   59: aload_0
    //   60: getfield 519	com/android/server/audio/AudioService:mSettingsLock	Ljava/lang/Object;
    //   63: astore_1
    //   64: aload_1
    //   65: monitorenter
    //   66: iconst_0
    //   67: istore_2
    //   68: aload_0
    //   69: getfield 446	com/android/server/audio/AudioService:mCameraSoundForced	Ljava/lang/Boolean;
    //   72: astore 5
    //   74: aload 5
    //   76: monitorenter
    //   77: iload_3
    //   78: aload_0
    //   79: getfield 446	com/android/server/audio/AudioService:mCameraSoundForced	Ljava/lang/Boolean;
    //   82: invokevirtual 1840	java/lang/Boolean:booleanValue	()Z
    //   85: if_icmpeq +13 -> 98
    //   88: aload_0
    //   89: iload_3
    //   90: invokestatic 1843	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   93: putfield 446	com/android/server/audio/AudioService:mCameraSoundForced	Ljava/lang/Boolean;
    //   96: iconst_1
    //   97: istore_2
    //   98: aload 5
    //   100: monitorexit
    //   101: iload_2
    //   102: ifeq +94 -> 196
    //   105: aload_0
    //   106: invokespecial 685	com/android/server/audio/AudioService:isPlatformTelevision	()Z
    //   109: ifne +42 -> 151
    //   112: aload_0
    //   113: getfield 542	com/android/server/audio/AudioService:mStreamStates	[Lcom/android/server/audio/AudioService$VolumeStreamState;
    //   116: bipush 7
    //   118: aaload
    //   119: astore 5
    //   121: iload_3
    //   122: ifeq +116 -> 238
    //   125: aload 5
    //   127: invokevirtual 1846	com/android/server/audio/AudioService$VolumeStreamState:setAllIndexesToMax	()V
    //   130: aload_0
    //   131: aload_0
    //   132: getfield 929	com/android/server/audio/AudioService:mRingerModeAffectedStreams	I
    //   135: sipush 65407
    //   138: iand
    //   139: putfield 929	com/android/server/audio/AudioService:mRingerModeAffectedStreams	I
    //   142: aload_0
    //   143: aload_0
    //   144: invokevirtual 1149	com/android/server/audio/AudioService:getRingerModeInternal	()I
    //   147: iconst_0
    //   148: invokespecial 808	com/android/server/audio/AudioService:setRingerModeInt	(IZ)V
    //   151: aload_0
    //   152: getfield 578	com/android/server/audio/AudioService:mAudioHandler	Lcom/android/server/audio/AudioService$AudioHandler;
    //   155: astore 5
    //   157: iload_3
    //   158: ifeq +108 -> 266
    //   161: bipush 11
    //   163: istore_2
    //   164: aload 5
    //   166: bipush 8
    //   168: iconst_2
    //   169: iconst_4
    //   170: iload_2
    //   171: aconst_null
    //   172: iconst_0
    //   173: invokestatic 776	com/android/server/audio/AudioService:sendMsg	(Landroid/os/Handler;IIIILjava/lang/Object;I)V
    //   176: aload_0
    //   177: getfield 578	com/android/server/audio/AudioService:mAudioHandler	Lcom/android/server/audio/AudioService$AudioHandler;
    //   180: bipush 10
    //   182: iconst_2
    //   183: iconst_0
    //   184: iconst_0
    //   185: aload_0
    //   186: getfield 542	com/android/server/audio/AudioService:mStreamStates	[Lcom/android/server/audio/AudioService$VolumeStreamState;
    //   189: bipush 7
    //   191: aaload
    //   192: iconst_0
    //   193: invokestatic 776	com/android/server/audio/AudioService:sendMsg	(Landroid/os/Handler;IIIILjava/lang/Object;I)V
    //   196: aload_1
    //   197: monitorexit
    //   198: aload_0
    //   199: getfield 563	com/android/server/audio/AudioService:mVolumeController	Lcom/android/server/audio/AudioService$VolumeController;
    //   202: aload 4
    //   204: invokevirtual 1849	android/content/res/Configuration:getLayoutDirection	()I
    //   207: invokevirtual 1852	com/android/server/audio/AudioService$VolumeController:setLayoutDirection	(I)V
    //   210: return
    //   211: astore 4
    //   213: aload 5
    //   215: monitorexit
    //   216: aload 4
    //   218: athrow
    //   219: astore 4
    //   221: aload_1
    //   222: monitorexit
    //   223: aload 4
    //   225: athrow
    //   226: astore_1
    //   227: ldc -5
    //   229: ldc_w 1854
    //   232: aload_1
    //   233: invokestatic 1857	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   236: pop
    //   237: return
    //   238: aload 5
    //   240: aload_0
    //   241: getfield 542	com/android/server/audio/AudioService:mStreamStates	[Lcom/android/server/audio/AudioService$VolumeStreamState;
    //   244: iconst_1
    //   245: aaload
    //   246: ldc -5
    //   248: invokevirtual 1440	com/android/server/audio/AudioService$VolumeStreamState:setAllIndexes	(Lcom/android/server/audio/AudioService$VolumeStreamState;Ljava/lang/String;)V
    //   251: aload_0
    //   252: aload_0
    //   253: getfield 929	com/android/server/audio/AudioService:mRingerModeAffectedStreams	I
    //   256: sipush 128
    //   259: ior
    //   260: putfield 929	com/android/server/audio/AudioService:mRingerModeAffectedStreams	I
    //   263: goto -121 -> 142
    //   266: iconst_0
    //   267: istore_2
    //   268: goto -104 -> 164
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	271	0	this	AudioService
    //   0	271	1	paramContext	Context
    //   21	247	2	i	int
    //   58	100	3	bool	boolean
    //   7	196	4	localConfiguration	Configuration
    //   211	6	4	localObject1	Object
    //   219	5	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   77	96	211	finally
    //   68	77	219	finally
    //   98	101	219	finally
    //   105	121	219	finally
    //   125	142	219	finally
    //   142	151	219	finally
    //   151	157	219	finally
    //   164	196	219	finally
    //   213	219	219	finally
    //   238	263	219	finally
    //   0	39	226	java/lang/Exception
    //   39	66	226	java/lang/Exception
    //   196	210	226	java/lang/Exception
    //   221	226	226	java/lang/Exception
  }
  
  private boolean handleDeviceConnection(boolean paramBoolean, int paramInt, String paramString1, String paramString2)
  {
    if (DEBUG_DEVICES) {
      Slog.i("AudioService", "handleDeviceConnection(" + paramBoolean + " dev:" + Integer.toHexString(paramInt) + " address:" + paramString1 + " name:" + paramString2 + ")");
    }
    for (;;)
    {
      boolean bool;
      synchronized (this.mConnectedDevices)
      {
        String str = makeDeviceListKey(paramInt, paramString1);
        if (DEBUG_DEVICES) {
          Slog.i("AudioService", "deviceKey:" + str);
        }
        DeviceListSpec localDeviceListSpec = (DeviceListSpec)this.mConnectedDevices.get(str);
        if (localDeviceListSpec != null)
        {
          bool = true;
          if (!DEBUG_DEVICES) {
            break label328;
          }
          Slog.i("AudioService", "deviceSpec:" + localDeviceListSpec + " is(already)Connected:" + bool);
          break label328;
          if ((!paramBoolean) && (bool))
          {
            AudioSystem.setDeviceConnectionState(paramInt, 0, paramString1, paramString2);
            this.mConnectedDevices.remove(str);
            return true;
          }
        }
        else
        {
          bool = false;
          continue;
          int i = AudioSystem.setDeviceConnectionState(paramInt, 1, paramString1, paramString2);
          if (i != 0)
          {
            Slog.e("AudioService", "not connecting device 0x" + Integer.toHexString(paramInt) + " due to command error " + i);
            return false;
          }
          this.mConnectedDevices.put(str, new DeviceListSpec(paramInt, paramString2, paramString1));
          return true;
        }
        return false;
      }
      label328:
      if (paramBoolean) {
        if (!bool) {}
      }
    }
  }
  
  private boolean hasRmtSbmxFullVolDeathHandlerFor(IBinder paramIBinder)
  {
    Iterator localIterator = this.mRmtSbmxFullVolDeathHandlers.iterator();
    while (localIterator.hasNext()) {
      if (((RmtSbmxFullVolDeathHandler)localIterator.next()).isHandlerFor(paramIBinder)) {
        return true;
      }
    }
    return false;
  }
  
  private boolean hasScheduledA2dpDockTimeout()
  {
    return this.mAudioHandler.hasMessages(6);
  }
  
  private boolean isASWiredHeadsetOn()
  {
    return (AudioSystem.getDeviceConnectionState(4, "") != 0) || (AudioSystem.getDeviceConnectionState(8, "") != 0);
  }
  
  private boolean isAfMusicActiveRecently(int paramInt)
  {
    if (!AudioSystem.isStreamActive(3, paramInt)) {
      return AudioSystem.isStreamActiveRemotely(3, paramInt);
    }
    return true;
  }
  
  private boolean isAndroidNPlus(String paramString)
  {
    try
    {
      int i = this.mContext.getPackageManager().getApplicationInfoAsUser(paramString, 0, UserHandle.getUserId(Binder.getCallingUid())).targetSdkVersion;
      return i >= 24;
    }
    catch (PackageManager.NameNotFoundException paramString) {}
    return true;
  }
  
  private boolean isInCommunication()
  {
    TelecomManager localTelecomManager = (TelecomManager)this.mContext.getSystemService("telecom");
    long l = Binder.clearCallingIdentity();
    boolean bool = localTelecomManager.isInCall();
    Binder.restoreCallingIdentity(l);
    return (bool) || (getMode() == 3);
  }
  
  private boolean isMuteAdjust(int paramInt)
  {
    if ((paramInt == -100) || (paramInt == 100)) {}
    while (paramInt == 101) {
      return true;
    }
    return false;
  }
  
  private boolean isPlatformTelevision()
  {
    return this.mPlatformType == 2;
  }
  
  private boolean isPlatformVoice()
  {
    return this.mPlatformType == 1;
  }
  
  private boolean isStreamMutedByRingerMode(int paramInt)
  {
    return (this.mRingerModeMutedStreams & 1 << paramInt) != 0;
  }
  
  private void killBackgroundUserProcessesWithRecordAudioPermission(UserInfo paramUserInfo)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    ComponentName localComponentName = null;
    if (!paramUserInfo.isManagedProfile()) {
      localComponentName = ((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class)).getHomeActivityForUser(paramUserInfo.id);
    }
    for (;;)
    {
      Object localObject;
      try
      {
        localObject = AppGlobals.getPackageManager();
        int i = paramUserInfo.id;
        paramUserInfo = ((IPackageManager)localObject).getPackagesHoldingPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 0, i).getList();
        i = paramUserInfo.size() - 1;
        if (i < 0) {
          break;
        }
        localObject = (PackageInfo)paramUserInfo.get(i);
        if (UserHandle.getAppId(((PackageInfo)localObject).applicationInfo.uid) < 10000)
        {
          i -= 1;
          continue;
        }
        if (localPackageManager.checkPermission("android.permission.INTERACT_ACROSS_USERS", ((PackageInfo)localObject).packageName) == 0) {
          continue;
        }
      }
      catch (RemoteException paramUserInfo)
      {
        throw new AndroidRuntimeException(paramUserInfo);
      }
      if ((localComponentName == null) || (!((PackageInfo)localObject).packageName.equals(localComponentName.getPackageName())) || (!((PackageInfo)localObject).applicationInfo.isSystemApp())) {
        try
        {
          int j = ((PackageInfo)localObject).applicationInfo.uid;
          ActivityManagerNative.getDefault().killUid(UserHandle.getAppId(j), UserHandle.getUserId(j), "killBackgroundUserProcessesWithAudioRecordPermission");
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("AudioService", "Error calling killUid", localRemoteException);
        }
      }
    }
  }
  
  private void loadTouchSoundAssetDefaults()
  {
    SOUND_EFFECT_FILES.add("Effect_Tick.ogg");
    int i = 0;
    while (i < 10)
    {
      this.SOUND_EFFECT_FILES_MAP[i][0] = 0;
      this.SOUND_EFFECT_FILES_MAP[i][1] = -1;
      i += 1;
    }
  }
  
  private void loadTouchSoundAssets()
  {
    localObject5 = null;
    localObject6 = null;
    localObject1 = null;
    localObject4 = null;
    if (!SOUND_EFFECT_FILES.isEmpty()) {
      return;
    }
    loadTouchSoundAssetDefaults();
    try
    {
      localXmlResourceParser = this.mContext.getResources().getXml(17891329);
      localObject4 = localXmlResourceParser;
      localObject5 = localXmlResourceParser;
      localObject6 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      XmlUtils.beginDocument(localXmlResourceParser, "audio_assets");
      localObject4 = localXmlResourceParser;
      localObject5 = localXmlResourceParser;
      localObject6 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      str1 = localXmlResourceParser.getAttributeValue(null, "version");
      i = 0;
      localObject4 = localXmlResourceParser;
      localObject5 = localXmlResourceParser;
      localObject6 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      if (!"1.0".equals(str1)) {
        break label229;
      }
      localObject4 = localXmlResourceParser;
      localObject5 = localXmlResourceParser;
      localObject6 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      XmlUtils.nextElement(localXmlResourceParser);
      localObject4 = localXmlResourceParser;
      localObject5 = localXmlResourceParser;
      localObject6 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      str1 = localXmlResourceParser.getName();
      if (str1 != null) {
        break label242;
      }
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      String str1;
      for (;;)
      {
        XmlResourceParser localXmlResourceParser;
        int i;
        String str2;
        int m;
        int k;
        int j;
        localNotFoundException = localNotFoundException;
        localObject1 = localObject4;
        Log.w("AudioService", "audio assets file not found", localNotFoundException);
        return;
      }
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      localObject2 = localObject5;
      Log.w("AudioService", "XML parser exception reading touch sound assets", localXmlPullParserException);
      return;
    }
    catch (IOException localIOException)
    {
      label174:
      localObject2 = localObject6;
      Log.w("AudioService", "I/O exception reading touch sound assets", localIOException);
      return;
    }
    finally
    {
      Object localObject2;
      if (localObject2 == null) {
        break label687;
      }
      ((XmlResourceParser)localObject2).close();
    }
    if (i != 0)
    {
      localObject4 = localXmlResourceParser;
      localObject5 = localXmlResourceParser;
      localObject6 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      XmlUtils.nextElement(localXmlResourceParser);
      localObject4 = localXmlResourceParser;
      localObject5 = localXmlResourceParser;
      localObject6 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      str1 = localXmlResourceParser.getName();
      if (str1 != null) {
        break label308;
      }
    }
    label229:
    label242:
    label308:
    do
    {
      if (localXmlResourceParser != null) {
        localXmlResourceParser.close();
      }
      return;
      localObject4 = localXmlResourceParser;
      localObject5 = localXmlResourceParser;
      localObject6 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      if (!str1.equals("group")) {
        break;
      }
      localObject4 = localXmlResourceParser;
      localObject5 = localXmlResourceParser;
      localObject6 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      if (!"touch_sounds".equals(localXmlResourceParser.getAttributeValue(null, "name"))) {
        break;
      }
      i = 1;
      break label174;
      localObject4 = localXmlResourceParser;
      localObject5 = localXmlResourceParser;
      localObject6 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
    } while (!str1.equals("asset"));
    localObject4 = localXmlResourceParser;
    localObject5 = localXmlResourceParser;
    localObject6 = localXmlResourceParser;
    localObject1 = localXmlResourceParser;
    str1 = localXmlResourceParser.getAttributeValue(null, "id");
    localObject4 = localXmlResourceParser;
    localObject5 = localXmlResourceParser;
    localObject6 = localXmlResourceParser;
    localObject1 = localXmlResourceParser;
    str2 = localXmlResourceParser.getAttributeValue(null, "file");
    localObject4 = localXmlResourceParser;
    localObject5 = localXmlResourceParser;
    localObject6 = localXmlResourceParser;
    localObject1 = localXmlResourceParser;
  }
  
  private void makeA2dpDeviceAvailable(String paramString1, String paramString2)
  {
    VolumeStreamState localVolumeStreamState = this.mStreamStates[3];
    sendMsg(this.mAudioHandler, 0, 2, 128, 0, localVolumeStreamState, 0);
    setBluetoothA2dpOnInt(true);
    AudioSystem.setDeviceConnectionState(128, 1, paramString1, paramString2);
    AudioSystem.setParameters("A2dpSuspended=false");
    this.mConnectedDevices.put(makeDeviceListKey(128, paramString1), new DeviceListSpec(128, paramString2, paramString1));
  }
  
  private void makeA2dpDeviceUnavailableLater(String paramString, int paramInt)
  {
    AudioSystem.setParameters("A2dpSuspended=true");
    this.mConnectedDevices.remove(makeDeviceListKey(128, paramString));
    paramString = this.mAudioHandler.obtainMessage(6, paramString);
    this.mAudioHandler.sendMessageDelayed(paramString, paramInt);
  }
  
  private void makeA2dpDeviceUnavailableNow(String arg1)
  {
    synchronized (this.mA2dpAvrcpLock)
    {
      this.mAvrcpAbsVolSupported = false;
      AudioSystem.setDeviceConnectionState(128, 0, ???, "");
      this.mConnectedDevices.remove(makeDeviceListKey(128, ???));
    }
    synchronized (this.mCurAudioRoutes)
    {
      if (this.mCurAudioRoutes.bluetoothName != null)
      {
        this.mCurAudioRoutes.bluetoothName = null;
        sendMsg(this.mAudioHandler, 12, 1, 0, 0, null, 0);
      }
      return;
      ??? = finally;
      throw ???;
    }
  }
  
  private void makeA2dpSrcAvailable(String paramString)
  {
    AudioSystem.setDeviceConnectionState(-2147352576, 1, paramString, "");
    this.mConnectedDevices.put(makeDeviceListKey(-2147352576, paramString), new DeviceListSpec(-2147352576, "", paramString));
  }
  
  private void makeA2dpSrcUnavailable(String paramString)
  {
    AudioSystem.setDeviceConnectionState(-2147352576, 0, paramString, "");
    this.mConnectedDevices.remove(makeDeviceListKey(-2147352576, paramString));
  }
  
  public static String makeAlsaAddressString(int paramInt1, int paramInt2)
  {
    return "card=" + paramInt1 + ";device=" + paramInt2 + ";";
  }
  
  private String makeDeviceListKey(int paramInt, String paramString)
  {
    return "0x" + Integer.toHexString(paramInt) + ":" + paramString;
  }
  
  private void muteRingerModeStreams()
  {
    int j = AudioSystem.getNumStreamTypes();
    int i;
    label25:
    boolean bool2;
    if (this.mRingerMode != 1)
    {
      if (this.mRingerMode != 0) {
        break label66;
      }
      i = 1;
      j -= 1;
      if (j < 0) {
        break label242;
      }
      bool2 = isStreamMutedByRingerMode(j);
      if (i == 0) {
        break label71;
      }
    }
    label66:
    label71:
    for (boolean bool1 = isStreamAffectedByRingerMode(j);; bool1 = false)
    {
      if (bool2 != bool1) {
        break label77;
      }
      j -= 1;
      break label25;
      i = 1;
      break;
      i = 0;
      break;
    }
    label77:
    if (!bool1) {
      if (this.mStreamVolumeAlias[j] != 2) {}
    }
    for (;;)
    {
      int k;
      try
      {
        VolumeStreamState localVolumeStreamState = this.mStreamStates[j];
        k = 0;
        if (k < VolumeStreamState.-get0(localVolumeStreamState).size())
        {
          int m = VolumeStreamState.-get0(localVolumeStreamState).keyAt(k);
          if (VolumeStreamState.-get0(localVolumeStreamState).valueAt(k) != 0) {
            break label243;
          }
          localVolumeStreamState.setIndex(10, m, "AudioService");
          break label243;
        }
        k = getDeviceForStream(j);
        sendMsg(this.mAudioHandler, 1, 2, k, 0, this.mStreamStates[j], 500);
        this.mStreamStates[j].mute(false);
        this.mRingerModeMutedStreams &= 1 << j;
        break;
      }
      finally {}
      this.mStreamStates[j].mute(true);
      this.mRingerModeMutedStreams |= 1 << j;
      break;
      label242:
      return;
      label243:
      k += 1;
    }
  }
  
  private void observeDevicesForStreams(int paramInt)
  {
    int i = 0;
    try
    {
      while (i < this.mStreamStates.length)
      {
        if (i != paramInt) {
          this.mStreamStates[i].observeDevicesForStream_syncVSS(false);
        }
        i += 1;
      }
      return;
    }
    finally {}
  }
  
  private void onBroadcastScoConnectionState(int paramInt)
  {
    if (paramInt != this.mScoConnectionState)
    {
      Intent localIntent = new Intent("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
      localIntent.putExtra("android.media.extra.SCO_AUDIO_STATE", paramInt);
      localIntent.putExtra("android.media.extra.SCO_AUDIO_PREVIOUS_STATE", this.mScoConnectionState);
      sendStickyBroadcastToAll(localIntent);
      this.mScoConnectionState = paramInt;
    }
  }
  
  private void onCheckMusicActive(String paramString)
  {
    synchronized (this.mSafeMediaVolumeState)
    {
      if (this.mSafeMediaVolumeState.intValue() == 2)
      {
        int i = getDeviceForStream(3);
        if ((i & 0xC) != 0)
        {
          sendMsg(this.mAudioHandler, 14, 0, 0, 0, paramString, 60000);
          i = this.mStreamStates[3].getIndex(i);
          if ((AudioSystem.isStreamActive(3, 0)) && (i > this.mSafeMediaVolumeIndex))
          {
            this.mMusicActiveMs += 60000;
            if (this.mMusicActiveMs > 72000000)
            {
              setSafeMediaVolumeEnabled(true, paramString);
              this.mMusicActiveMs = 0;
            }
            saveMusicActiveMs();
          }
        }
      }
      return;
    }
  }
  
  private void onConfigureSafeVolume(boolean paramBoolean, String paramString)
  {
    for (;;)
    {
      int j;
      synchronized (this.mSafeMediaVolumeState)
      {
        int k = this.mContext.getResources().getConfiguration().mcc;
        if ((this.mMcc != k) || ((this.mMcc == 0) && (paramBoolean)))
        {
          this.mSafeMediaVolumeIndex = (this.mContext.getResources().getInteger(17694864) * 10);
          if (!SystemProperties.getBoolean("audio.safemedia.force", false))
          {
            paramBoolean = this.mContext.getResources().getBoolean(17956991);
            boolean bool = SystemProperties.getBoolean("audio.safemedia.bypass", false);
            if ((paramBoolean) && (!bool)) {
              continue;
            }
            i = 1;
            this.mSafeMediaVolumeState = Integer.valueOf(1);
            this.mMcc = k;
            sendMsg(this.mAudioHandler, 18, 2, i, 0, null, 0);
          }
        }
        else
        {
          return;
        }
        paramBoolean = true;
        continue;
        j = 3;
        i = j;
        if (this.mSafeMediaVolumeState.intValue() == 2) {
          continue;
        }
        if (this.mMusicActiveMs == 0)
        {
          this.mSafeMediaVolumeState = Integer.valueOf(3);
          enforceSafeMediaVolume(paramString);
          i = j;
        }
      }
      this.mSafeMediaVolumeState = Integer.valueOf(2);
      int i = j;
    }
  }
  
  private void onDynPolicyMixStateUpdate(String paramString, int paramInt)
  {
    if (DEBUG_AP) {
      Log.d("AudioService", "onDynamicPolicyMixStateUpdate(" + paramString + ", " + paramInt + ")");
    }
    synchronized (this.mAudioPolicies)
    {
      Iterator localIterator1 = this.mAudioPolicies.values().iterator();
      AudioPolicyProxy localAudioPolicyProxy;
      boolean bool;
      do
      {
        Iterator localIterator2;
        while (!localIterator2.hasNext())
        {
          if (!localIterator1.hasNext()) {
            break;
          }
          localAudioPolicyProxy = (AudioPolicyProxy)localIterator1.next();
          localIterator2 = localAudioPolicyProxy.getMixes().iterator();
        }
        bool = ((AudioMix)localIterator2.next()).getRegistration().equals(paramString);
      } while (!bool);
      try
      {
        localAudioPolicyProxy.mPolicyCallback.notifyMixStateUpdate(paramString, paramInt);
        return;
      }
      catch (RemoteException paramString)
      {
        for (;;)
        {
          Log.e("AudioService", "Can't call notifyMixStateUpdate() on IAudioPolicyCallback " + localAudioPolicyProxy.mPolicyCallback.asBinder(), paramString);
        }
      }
    }
  }
  
  private void onSendBecomingNoisyIntent()
  {
    sendBroadcastToAll(new Intent("android.media.AUDIO_BECOMING_NOISY"));
  }
  
  private void onSetA2dpSinkConnectionState(BluetoothDevice arg1, int paramInt)
  {
    if (DEBUG_VOL) {
      Log.d("AudioService", "onSetA2dpSinkConnectionState btDevice=" + ??? + "state=" + paramInt);
    }
    if (??? == null) {
      return;
    }
    ??? = ???.getAddress();
    Object localObject1 = ???;
    if (!BluetoothAdapter.checkBluetoothAddress((String)???)) {
      localObject1 = "";
    }
    boolean bool;
    int j;
    int i;
    int k;
    synchronized (this.mConnectedDevices)
    {
      Object localObject5 = makeDeviceListKey(128, ???.getAddress());
      if ((DeviceListSpec)this.mConnectedDevices.get(localObject5) != null)
      {
        bool = true;
        break label517;
        if (j < this.mConnectedDevices.size())
        {
          localObject5 = (DeviceListSpec)this.mConnectedDevices.valueAt(j);
          k = i;
          if (((DeviceListSpec)localObject5).mDeviceType == 128)
          {
            Log.w("AudioService", "onSetA2dpSinkConnectionState Addr" + ((DeviceListSpec)localObject5).mDeviceAddress);
            k = i + 1;
          }
        }
        else
        {
          Log.d("AudioService", "onSetA2dpSinkConnectionState isConnected = " + bool + "state=" + paramInt + "device:" + (String)localObject1);
          if ((bool) && (paramInt != 2)) {
            if (???.isBluetoothDock()) {
              if (paramInt == 0) {
                makeA2dpDeviceUnavailableLater(???.getAddress(), 8000);
              }
            }
          }
        }
      }
    }
    for (;;)
    {
      synchronized (this.mCurAudioRoutes)
      {
        if (this.mCurAudioRoutes.bluetoothName != null)
        {
          this.mCurAudioRoutes.bluetoothName = null;
          sendMsg(this.mAudioHandler, 12, 1, 0, 0, null, 0);
        }
        return;
        if (i > 1)
        {
          Log.d("AudioService", "onSetA2dpSinkConnectionState: Not all device are disconnected");
          this.mConnectedDevices.remove(makeDeviceListKey(128, (String)localObject1));
          return;
        }
        Log.d("AudioService", "All devices are disconneted, update Policymanager ");
        makeA2dpDeviceUnavailableNow(???.getAddress());
        continue;
        ??? = finally;
        throw ???;
      }
      if ((bool) || (paramInt != 2)) {
        continue;
      }
      this.mA2dpConnectedDevice = "BluetoothA2dp";
      if (???.isBluetoothDock())
      {
        cancelA2dpDeviceTimeout();
        this.mDockAddress = this.mA2dpConnectedDevice;
        label420:
        makeA2dpDeviceAvailable(???.getAddress(), ???.getName());
      }
      label517:
      synchronized (this.mCurAudioRoutes)
      {
        String str = this.mA2dpConnectedDevice;
        if (!TextUtils.equals(this.mCurAudioRoutes.bluetoothName, str))
        {
          this.mCurAudioRoutes.bluetoothName = str;
          sendMsg(this.mAudioHandler, 12, 1, 0, 0, null, 0);
        }
        continue;
        if (!hasScheduledA2dpDockTimeout()) {
          break label420;
        }
        cancelA2dpDeviceTimeout();
        makeA2dpDeviceUnavailableNow(???.getAddress());
      }
    }
  }
  
  private void onSetA2dpSourceConnectionState(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    if (DEBUG_VOL) {
      Log.d("AudioService", "onSetA2dpSourceConnectionState btDevice=" + paramBluetoothDevice + " state=" + paramInt);
    }
    if (paramBluetoothDevice == null)
    {
      Log.d("AudioService", "onSetA2dpSourceConnectionState device is null");
      return;
    }
    ??? = paramBluetoothDevice.getAddress();
    paramBluetoothDevice = (BluetoothDevice)???;
    if (!BluetoothAdapter.checkBluetoothAddress((String)???)) {
      paramBluetoothDevice = "";
    }
    synchronized (this.mConnectedDevices)
    {
      String str = makeDeviceListKey(-2147352576, paramBluetoothDevice);
      int i;
      if ((DeviceListSpec)this.mConnectedDevices.get(str) != null)
      {
        i = 1;
        if ((i == 0) || (paramInt == 2)) {
          break label136;
        }
        makeA2dpSrcUnavailable(paramBluetoothDevice);
      }
      label136:
      while ((i != 0) || (paramInt != 2))
      {
        return;
        i = 0;
        break;
      }
      makeA2dpSrcAvailable(paramBluetoothDevice);
    }
  }
  
  private void onSetStreamVolume(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString)
  {
    boolean bool2 = true;
    int i = this.mStreamVolumeAlias[paramInt1];
    setStreamVolumeInt(i, paramInt2, paramInt4, false, paramString);
    if (((paramInt3 & 0x2) != 0) || (i == getUiSoundsStreamType())) {
      setRingerMode(getNewRingerMode(i, paramInt2, paramInt3), "AudioService.onSetStreamVolume", false);
    }
    VolumeStreamState localVolumeStreamState = this.mStreamStates[i];
    if (paramInt2 == 0)
    {
      bool1 = true;
      localVolumeStreamState.mute(bool1);
      if ((paramString != null) && (paramString.equals(new String("com.google.android.gms"))) && (paramInt1 == 2))
      {
        paramString = this.mStreamStates[5];
        if (paramInt2 != 0) {
          break label135;
        }
      }
    }
    label135:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      paramString.mute(bool1);
      return;
      bool1 = false;
      break;
    }
  }
  
  private void onSetWiredDeviceConnectionState(int paramInt1, int paramInt2, String paramString1, String paramString2, String arg5)
  {
    if (DEBUG_DEVICES) {
      Slog.i("AudioService", "onSetWiredDeviceConnectionState(dev:" + Integer.toHexString(paramInt1) + " state:" + Integer.toHexString(paramInt2) + " address:" + paramString1 + " deviceName:" + paramString2 + " caller: " + ??? + ");");
    }
    ArrayMap localArrayMap = this.mConnectedDevices;
    if (paramInt2 == 0)
    {
      if ((paramInt1 == 4) || (paramInt1 == 8)) {}
      try
      {
        setBluetoothA2dpOnInt(true);
      }
      finally
      {
        label118:
        synchronized (this.mHdmiManager)
        {
          do
          {
            int j;
            String str;
            if (this.mHdmiPlaybackClient != null)
            {
              this.mHdmiCecSink = false;
              this.mHdmiPlaybackClient.queryDisplayStatus(this.mHdmiDisplayStatusCallback);
            }
            if ((i == 0) && (paramInt1 != -2147483632) && (j != 0)) {
              sendDeviceConnectionIntent(paramInt1, paramInt2, paramString1, paramString2);
            }
            return;
          } while (paramInt1 != 131072);
        }
      }
      j = 0;
      if (paramString2.indexOf("not broadcast") != -1)
      {
        str = paramString2.substring(0, paramString2.length() - " not broadcast".length());
        Log.i("AudioService", "onSetWiredDeviceConnectionState not need broadcast name:" + str + " name_switch:" + paramString2);
        paramString2 = str;
        break label505;
      }
    }
    for (;;)
    {
      label198:
      boolean bool = handleDeviceConnection(bool, paramInt1, paramString1, paramString2);
      label229:
      int i;
      if (!bool)
      {
        return;
        if (paramInt1 != 131072) {
          break label475;
        }
        break;
        i = 1;
        break label118;
        label235:
        i = 0;
        break label118;
        label241:
        i = 0;
        break label118;
        j = 1;
      }
      label475:
      label505:
      while (paramInt2 != 1)
      {
        bool = false;
        break label198;
        if (paramInt2 != 0) {
          if ((paramInt1 == 4) || (paramInt1 == 8))
          {
            setBluetoothA2dpOnInt(false);
            if ((paramInt1 & 0xC) != 0) {
              sendMsg(this.mAudioHandler, 14, 0, 0, 0, ???, 60000);
            }
            if ((isPlatformTelevision()) && ((paramInt1 & 0x400) != 0))
            {
              this.mFixedVolumeDevices |= 0x400;
              checkAllFixedVolumeDevices();
              if (this.mHdmiManager == null) {}
            }
          }
        }
        for (;;)
        {
          if ((isPlatformTelevision()) && ((paramInt1 & 0x400) != 0) && (this.mHdmiManager != null)) {
            synchronized (this.mHdmiManager)
            {
              this.mHdmiCecSink = false;
            }
          }
        }
        if ((paramInt1 & 0x9FFF) == 0) {
          break label229;
        }
        if ((0x80000000 & paramInt1) == 0) {
          break label241;
        }
        if ((0x7FFFE7FF & paramInt1) != 0) {
          break label235;
        }
        i = 1;
        break;
      }
      bool = true;
    }
  }
  
  private void onUnmuteStream(int paramInt1, int paramInt2)
  {
    this.mStreamStates[paramInt1].mute(false);
    int i = getDeviceForStream(paramInt1);
    i = this.mStreamStates[paramInt1].getIndex(i);
    sendVolumeUpdate(paramInt1, i, i, paramInt2);
  }
  
  private void queueMsgUnderWakeLock(Handler paramHandler, int paramInt1, int paramInt2, int paramInt3, Object paramObject, int paramInt4)
  {
    long l = Binder.clearCallingIdentity();
    this.mAudioEventWakeLock.acquire();
    Binder.restoreCallingIdentity(l);
    sendMsg(paramHandler, paramInt1, 2, paramInt2, paramInt3, paramObject, paramInt4);
  }
  
  private static void readAndSetLowRamDevice()
  {
    int i = AudioSystem.setLowRamDevice(ActivityManager.isLowRamDeviceStatic());
    if (i != 0) {
      Log.w("AudioService", "AudioFlinger informed of device's low RAM attribute; status " + i);
    }
  }
  
  private void readAudioSettings(boolean paramBoolean)
  {
    readPersistedSettings();
    readUserRestrictions();
    int j = AudioSystem.getNumStreamTypes();
    int i = 0;
    if (i < j)
    {
      VolumeStreamState localVolumeStreamState = this.mStreamStates[i];
      if ((paramBoolean) && (this.mStreamVolumeAlias[i] == 3)) {}
      for (;;)
      {
        i += 1;
        break;
        localVolumeStreamState.readSettings();
        try
        {
          if ((VolumeStreamState.-get3(localVolumeStreamState)) && (((!isStreamAffectedByMute(i)) && (!isStreamMutedByRingerMode(i))) || (this.mUseFixedVolume))) {
            VolumeStreamState.-set0(localVolumeStreamState, false);
          }
        }
        finally {}
      }
    }
    setRingerModeInt(getRingerModeInternal(), false);
    checkAllFixedVolumeDevices();
    checkAllAliasStreamVolumes();
    checkMuteAffectedStreams();
    synchronized (this.mSafeMediaVolumeState)
    {
      this.mMusicActiveMs = MathUtils.constrain(Settings.Secure.getIntForUser(this.mContentResolver, "unsafe_volume_music_active_ms", 0, -2), 0, 72000000);
      if (this.mSafeMediaVolumeState.intValue() == 3) {
        enforceSafeMediaVolume("AudioService");
      }
      return;
    }
  }
  
  private boolean readCameraSoundForced()
  {
    if (!SystemProperties.getBoolean("audio.camerasound.force", false)) {
      return this.mContext.getResources().getBoolean(17956993);
    }
    return true;
  }
  
  private void readDockAudioSettings(ContentResolver paramContentResolver)
  {
    boolean bool = true;
    if (Settings.Global.getInt(paramContentResolver, "dock_audio_media_enabled", 0) == 1)
    {
      this.mDockAudioMediaEnabled = bool;
      paramContentResolver = this.mAudioHandler;
      if (!this.mDockAudioMediaEnabled) {
        break label51;
      }
    }
    label51:
    for (int i = 8;; i = 0)
    {
      sendMsg(paramContentResolver, 8, 2, 3, i, null, 0);
      return;
      bool = false;
      break;
    }
  }
  
  private void readPersistedSettings()
  {
    int k = 2;
    ContentResolver localContentResolver = this.mContentResolver;
    int m = Settings.Global.getInt(localContentResolver, "mode_ringer", 2);
    int i = m;
    if (!isValidRingerMode(m)) {
      i = 2;
    }
    int j = i;
    if (i == 1)
    {
      if (!this.mHasVibrator) {
        break label238;
      }
      j = i;
    }
    for (;;)
    {
      if (j != m) {
        Settings.Global.putInt(localContentResolver, "mode_ringer", j);
      }
      if ((this.mUseFixedVolume) || (isPlatformTelevision())) {
        j = 2;
      }
      synchronized (this.mSettingsLock)
      {
        this.mRingerMode = j;
        if (this.mRingerModeExternal == -1) {
          this.mRingerModeExternal = this.mRingerMode;
        }
        if (this.mHasVibrator) {}
        for (i = 2;; i = 0)
        {
          this.mVibrateSetting = AudioSystem.getValueForVibrateSetting(0, 1, i);
          j = this.mVibrateSetting;
          if (!this.mHasVibrator) {
            break label248;
          }
          i = k;
          this.mVibrateSetting = AudioSystem.getValueForVibrateSetting(j, 0, i);
          updateRingerModeAffectedStreams();
          readDockAudioSettings(localContentResolver);
          sendEncodedSurroundMode(localContentResolver);
          this.mMuteAffectedStreams = Settings.System.getIntForUser(localContentResolver, "mute_streams_affected", 46, -2);
          updateMasterMono(localContentResolver);
          broadcastRingerMode("android.media.RINGER_MODE_CHANGED", this.mRingerModeExternal);
          broadcastRingerMode("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION", this.mRingerMode);
          broadcastVibrateSetting(0);
          broadcastVibrateSetting(1);
          this.mVolumeController.loadSettings(localContentResolver);
          return;
          label238:
          j = 0;
          break;
        }
        label248:
        i = 0;
      }
    }
  }
  
  private void readUserRestrictions()
  {
    int i = getCurrentUserId();
    if (!this.mUserManagerInternal.getUserRestriction(i, "disallow_unmute_device")) {}
    for (boolean bool = this.mUserManagerInternal.getUserRestriction(i, "no_adjust_volume");; bool = true)
    {
      if (this.mUseFixedVolume)
      {
        bool = false;
        AudioSystem.setMasterVolume(1.0F);
      }
      if (DEBUG_VOL) {
        Log.d("AudioService", String.format("Master mute %s, user=%d", new Object[] { Boolean.valueOf(bool), Integer.valueOf(i) }));
      }
      setSystemAudioMute(bool);
      AudioSystem.setMasterMute(bool);
      broadcastMasterMuteStatus(bool);
      bool = this.mUserManagerInternal.getUserRestriction(i, "no_unmute_microphone");
      if (DEBUG_VOL) {
        Log.d("AudioService", String.format("Mic mute %s, user=%d", new Object[] { Boolean.valueOf(bool), Integer.valueOf(i) }));
      }
      AudioSystem.muteMicrophone(bool);
      return;
    }
  }
  
  private int rescaleIndex(int paramInt1, int paramInt2, int paramInt3)
  {
    return (this.mStreamStates[paramInt3].getMaxIndex() * paramInt1 + this.mStreamStates[paramInt2].getMaxIndex() / 2) / this.mStreamStates[paramInt2].getMaxIndex();
  }
  
  private void resetBluetoothSco()
  {
    synchronized (this.mScoClients)
    {
      clearAllScoClients(0, false);
      this.mScoAudioState = 0;
      broadcastScoConnectionState(0);
      AudioSystem.setParameters("A2dpSuspended=false");
      setBluetoothScoOnInt(false);
      return;
    }
  }
  
  private static String safeMediaVolumeStateToString(Integer paramInteger)
  {
    switch (paramInteger.intValue())
    {
    default: 
      return null;
    case 0: 
      return "SAFE_MEDIA_VOLUME_NOT_CONFIGURED";
    case 1: 
      return "SAFE_MEDIA_VOLUME_DISABLED";
    case 2: 
      return "SAFE_MEDIA_VOLUME_INACTIVE";
    }
    return "SAFE_MEDIA_VOLUME_ACTIVE";
  }
  
  private void saveMusicActiveMs()
  {
    this.mAudioHandler.obtainMessage(22, this.mMusicActiveMs, 0).sendToTarget();
  }
  
  private void sendBroadcastToAll(Intent paramIntent)
  {
    paramIntent.addFlags(67108864);
    paramIntent.addFlags(268435456);
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mContext.sendBroadcastAsUser(paramIntent, UserHandle.ALL);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void sendDeviceConnectionIntent(int paramInt1, int paramInt2, String paramString1, String paramString2)
  {
    if (DEBUG_DEVICES) {
      Slog.i("AudioService", "sendDeviceConnectionIntent(dev:0x" + Integer.toHexString(paramInt1) + " state:0x" + Integer.toHexString(paramInt2) + " address:" + paramString1 + " name:" + paramString2 + ");");
    }
    Intent localIntent = new Intent();
    localIntent.putExtra("state", paramInt2);
    localIntent.putExtra("address", paramString1);
    localIntent.putExtra("portName", paramString2);
    int i;
    if (paramString2.equals("American Headset"))
    {
      localIntent.putExtra("standard", 1);
      localIntent.addFlags(1073741824);
      i = 0;
      if (paramInt1 != 4) {
        break label267;
      }
      i = 1;
      localIntent.setAction("android.intent.action.HEADSET_PLUG");
      localIntent.putExtra("microphone", 1);
      label172:
      paramString1 = this.mCurAudioRoutes;
      if (i == 0) {}
    }
    for (;;)
    {
      label267:
      try
      {
        paramInt1 = this.mCurAudioRoutes.mainType;
        if (paramInt2 != 0)
        {
          paramInt1 |= i;
          if (paramInt1 != this.mCurAudioRoutes.mainType)
          {
            this.mCurAudioRoutes.mainType = paramInt1;
            sendMsg(this.mAudioHandler, 12, 1, 0, 0, null, 0);
          }
          l = Binder.clearCallingIdentity();
        }
      }
      finally {}
      try
      {
        ActivityManagerNative.broadcastStickyIntent(localIntent, null, -1);
        Binder.restoreCallingIdentity(l);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      localIntent.putExtra("standard", 0);
      break;
      if ((paramInt1 == 8) || (paramInt1 == 131072))
      {
        i = 2;
        localIntent.setAction("android.intent.action.HEADSET_PLUG");
        localIntent.putExtra("microphone", 0);
        break label172;
      }
      if ((paramInt1 == 1024) || (paramInt1 == 262144))
      {
        i = 8;
        configureHdmiPlugIntent(localIntent, paramInt2);
        break label172;
      }
      if (paramInt1 != 16384) {
        break label172;
      }
      i = 16;
      break label172;
      paramInt1 &= i;
    }
  }
  
  private void sendEncodedSurroundMode(int paramInt)
  {
    int i = 15;
    switch (paramInt)
    {
    default: 
      Log.e("AudioService", "updateSurroundSoundSettings: illegal value " + paramInt);
      paramInt = i;
    }
    for (;;)
    {
      if (paramInt != 15) {
        sendMsg(this.mAudioHandler, 8, 2, 6, paramInt, null, 0);
      }
      return;
      paramInt = 0;
      continue;
      paramInt = 13;
      continue;
      paramInt = 14;
    }
  }
  
  private void sendEncodedSurroundMode(ContentResolver paramContentResolver)
  {
    sendEncodedSurroundMode(Settings.Global.getInt(paramContentResolver, "encoded_surround_output", 0));
  }
  
  private void sendMasterMuteUpdate(boolean paramBoolean, int paramInt)
  {
    this.mVolumeController.postMasterMuteChanged(updateFlagsForSystemAudio(paramInt));
    broadcastMasterMuteStatus(paramBoolean);
  }
  
  private static void sendMsg(Handler paramHandler, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject, int paramInt5)
  {
    if (paramInt2 == 0) {
      paramHandler.removeMessages(paramInt1);
    }
    synchronized (mLastDeviceConnectMsgTime)
    {
      long l = SystemClock.uptimeMillis() + paramInt5;
      paramHandler.sendMessageAtTime(paramHandler.obtainMessage(paramInt1, paramInt3, paramInt4, paramObject), l);
      if ((paramInt1 == 100) || (paramInt1 == 101)) {
        mLastDeviceConnectMsgTime = Long.valueOf(l);
      }
      while (paramInt1 != 102)
      {
        return;
        if ((paramInt2 != 1) || (!paramHandler.hasMessages(paramInt1))) {
          break;
        }
        return;
      }
    }
  }
  
  private void sendStickyBroadcastToAll(Intent paramIntent)
  {
    paramIntent.addFlags(268435456);
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mContext.sendStickyBroadcastAsUser(paramIntent, UserHandle.ALL);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void sendVolumeUpdate(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt2 = this.mStreamVolumeAlias[paramInt1];
    paramInt1 = paramInt4;
    if (paramInt2 == 3) {
      paramInt1 = updateFlagsForSystemAudio(paramInt4);
    }
    this.mVolumeController.postVolumeChanged(paramInt2, paramInt1);
  }
  
  private void setForceUseInt_SyncDevices(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    }
    for (;;)
    {
      AudioSystem.setForceUse(paramInt1, paramInt2);
      return;
      if (paramInt2 == 10) {}
      for (this.mBecomingNoisyIntentDevices &= 0xFC7F;; this.mBecomingNoisyIntentDevices |= 0x380)
      {
        sendMsg(this.mAudioHandler, 12, 1, 0, 0, null, 0);
        break;
      }
      if (paramInt2 == 8) {
        this.mBecomingNoisyIntentDevices |= 0x800;
      } else {
        this.mBecomingNoisyIntentDevices &= 0xF7FF;
      }
    }
  }
  
  private void setMasterMuteInternal(boolean paramBoolean, int paramInt1, String paramString, int paramInt2, int paramInt3)
  {
    int i = paramInt2;
    if (paramInt2 == 1000) {
      i = UserHandle.getUid(paramInt3, UserHandle.getAppId(paramInt2));
    }
    if ((!paramBoolean) && (this.mAppOps.noteOp(33, i, paramString) != 0)) {
      return;
    }
    if ((paramInt3 != UserHandle.getCallingUserId()) && (this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0)) {
      return;
    }
    setMasterMuteInternalNoCallerCheck(paramBoolean, paramInt1, paramInt3);
  }
  
  private void setMasterMuteInternalNoCallerCheck(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (DEBUG_VOL) {
      Log.d("AudioService", String.format("Master mute %s, %d, user=%d", new Object[] { Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }));
    }
    if (this.mUseFixedVolume) {
      return;
    }
    if ((getCurrentUserId() == paramInt2) && (paramBoolean != AudioSystem.getMasterMute()))
    {
      setSystemAudioMute(paramBoolean);
      AudioSystem.setMasterMute(paramBoolean);
      sendMasterMuteUpdate(paramBoolean, paramInt1);
      Intent localIntent = new Intent("android.media.MASTER_MUTE_CHANGED_ACTION");
      localIntent.putExtra("android.media.EXTRA_MASTER_VOLUME_MUTED", paramBoolean);
      sendBroadcastToAll(localIntent);
    }
  }
  
  private void setMicrophoneMuteNoCallerCheck(boolean paramBoolean, int paramInt)
  {
    if (DEBUG_VOL) {
      Log.d("AudioService", String.format("Mic mute %s, user=%d", new Object[] { Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt) }));
    }
    if (getCurrentUserId() == paramInt) {
      AudioSystem.muteMicrophone(paramBoolean);
    }
  }
  
  private int setModeInt(int paramInt1, IBinder paramIBinder, int paramInt2, String paramString)
  {
    if (DEBUG_MODE) {
      Log.v("AudioService", "setModeInt(mode=" + paramInt1 + ", pid=" + paramInt2 + ", caller=" + paramString + ")");
    }
    int m = 0;
    int k = 0;
    if (paramIBinder == null)
    {
      Log.e("AudioService", "setModeInt() called with null binder");
      return 0;
    }
    Object localObject2 = null;
    Iterator localIterator = this.mSetModeDeathHandlers.iterator();
    Object localObject3;
    do
    {
      localObject1 = localObject2;
      if (!localIterator.hasNext()) {
        break;
      }
      localObject3 = (SetModeDeathHandler)localIterator.next();
    } while (((SetModeDeathHandler)localObject3).getPid() != paramInt2);
    Object localObject1 = localObject3;
    localIterator.remove();
    ((SetModeDeathHandler)localObject3).getBinder().unlinkToDeath((IBinder.DeathRecipient)localObject3, 0);
    localObject2 = paramIBinder;
    int i = paramInt1;
    paramIBinder = (IBinder)localObject1;
    int j;
    for (;;)
    {
      if (i == 0)
      {
        paramInt1 = i;
        if (!this.mSetModeDeathHandlers.isEmpty())
        {
          localObject1 = (SetModeDeathHandler)this.mSetModeDeathHandlers.get(0);
          localObject3 = ((SetModeDeathHandler)localObject1).getBinder();
          i = ((SetModeDeathHandler)localObject1).getMode();
          paramIBinder = (IBinder)localObject1;
          paramInt1 = i;
          localObject2 = localObject3;
          if (DEBUG_MODE)
          {
            Log.w("AudioService", " using mode=" + i + " instead due to death hdlr at pid=" + SetModeDeathHandler.-get0((SetModeDeathHandler)localObject1));
            localObject2 = localObject3;
            paramInt1 = i;
            paramIBinder = (IBinder)localObject1;
          }
        }
        if (paramInt1 == this.mMode) {
          break label624;
        }
        j = AudioSystem.setPhoneState(paramInt1);
        if (j != 0) {
          break label581;
        }
        if (DEBUG_MODE) {
          Log.v("AudioService", " mode successfully set to " + paramInt1);
        }
        this.mMode = paramInt1;
        if ((paramInt1 == 0) || (paramInt1 == 3) || (!AudioSystem.isStreamActive(3, 0))) {
          break label572;
        }
        AudioSystem.setStreamMute(3, true);
        label352:
        if (j != 0)
        {
          i = paramInt1;
          if (!this.mSetModeDeathHandlers.isEmpty()) {}
        }
        else
        {
          i = m;
          if (j == 0)
          {
            paramInt2 = k;
            if (paramInt1 != 0)
            {
              if (!this.mSetModeDeathHandlers.isEmpty()) {
                break label630;
              }
              Log.e("AudioService", "setMode() different from MODE_NORMAL with empty mode client stack");
            }
          }
        }
      }
    }
    label572:
    label581:
    label624:
    label630:
    for (paramInt2 = k;; paramInt2 = ((SetModeDeathHandler)this.mSetModeDeathHandlers.get(0)).getPid())
    {
      paramInt1 = getActiveStreamType(Integer.MIN_VALUE);
      i = paramInt2;
      if (paramInt1 != 3)
      {
        i = getDeviceForStream(paramInt1);
        j = this.mStreamStates[this.mStreamVolumeAlias[paramInt1]].getIndex(i);
        setStreamVolumeInt(this.mStreamVolumeAlias[paramInt1], j, i, true, paramString);
        updateStreamVolumeAlias(true, paramString);
        i = paramInt2;
      }
      return i;
      localObject1 = paramIBinder;
      if (paramIBinder == null) {
        localObject1 = new SetModeDeathHandler((IBinder)localObject2, paramInt2);
      }
      try
      {
        ((IBinder)localObject2).linkToDeath((IBinder.DeathRecipient)localObject1, 0);
        this.mSetModeDeathHandlers.add(0, localObject1);
        ((SetModeDeathHandler)localObject1).setMode(i);
        paramIBinder = (IBinder)localObject1;
        paramInt1 = i;
      }
      catch (RemoteException paramIBinder)
      {
        for (;;)
        {
          Log.w("AudioService", "setMode() could not link to " + localObject2 + " binder death");
        }
      }
      AudioSystem.setStreamMute(3, false);
      break label352;
      if (paramIBinder != null)
      {
        this.mSetModeDeathHandlers.remove(paramIBinder);
        ((IBinder)localObject2).unlinkToDeath(paramIBinder, 0);
      }
      if (DEBUG_MODE) {
        Log.w("AudioService", " mode set to MODE_NORMAL after phoneState pb");
      }
      paramInt1 = 0;
      break label352;
      j = 0;
      break label352;
    }
  }
  
  private void setOrientationForAudioSystem()
  {
    switch (this.mDeviceOrientation)
    {
    default: 
      Log.e("AudioService", "Unknown orientation");
      return;
    case 2: 
      AudioSystem.setParameters("orientation=landscape");
      return;
    case 1: 
      AudioSystem.setParameters("orientation=portrait");
      return;
    case 3: 
      AudioSystem.setParameters("orientation=square");
      return;
    }
    AudioSystem.setParameters("orientation=undefined");
  }
  
  /* Error */
  private void setRingerMode(int paramInt, String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 556	com/android/server/audio/AudioService:mUseFixedVolume	Z
    //   4: ifne +10 -> 14
    //   7: aload_0
    //   8: invokespecial 685	com/android/server/audio/AudioService:isPlatformTelevision	()Z
    //   11: ifeq +4 -> 15
    //   14: return
    //   15: aload_0
    //   16: getfield 1014	com/android/server/audio/AudioService:mOnePlusFixedRingerMode	Z
    //   19: ifeq +24 -> 43
    //   22: iload_3
    //   23: ifeq +19 -> 42
    //   26: iload_1
    //   27: aload_0
    //   28: getfield 927	com/android/server/audio/AudioService:mRingerModeExternal	I
    //   31: if_icmpeq +11 -> 42
    //   34: aload_0
    //   35: ldc_w 2363
    //   38: iload_1
    //   39: invokespecial 2365	com/android/server/audio/AudioService:broadcastRingerMode	(Ljava/lang/String;I)V
    //   42: return
    //   43: aload_2
    //   44: ifnull +10 -> 54
    //   47: aload_2
    //   48: invokevirtual 2283	java/lang/String:length	()I
    //   51: ifne +31 -> 82
    //   54: new 1736	java/lang/IllegalArgumentException
    //   57: dup
    //   58: new 1240	java/lang/StringBuilder
    //   61: dup
    //   62: invokespecial 1241	java/lang/StringBuilder:<init>	()V
    //   65: ldc_w 2569
    //   68: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: aload_2
    //   72: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   75: invokevirtual 1260	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   78: invokespecial 1739	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   81: athrow
    //   82: aload_2
    //   83: ifnull +56 -> 139
    //   86: aload_2
    //   87: invokevirtual 2283	java/lang/String:length	()I
    //   90: ifeq +49 -> 139
    //   93: ldc -5
    //   95: new 1240	java/lang/StringBuilder
    //   98: dup
    //   99: invokespecial 1241	java/lang/StringBuilder:<init>	()V
    //   102: ldc_w 2571
    //   105: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   108: iload_1
    //   109: invokevirtual 1250	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   112: ldc_w 1256
    //   115: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   118: aload_2
    //   119: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   122: ldc_w 2573
    //   125: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   128: iload_3
    //   129: invokevirtual 1864	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   132: invokevirtual 1260	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   135: invokestatic 1263	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   138: pop
    //   139: aload_0
    //   140: iload_1
    //   141: invokespecial 2575	com/android/server/audio/AudioService:ensureValidRingerMode	(I)V
    //   144: iload_1
    //   145: istore 4
    //   147: iload_1
    //   148: iconst_1
    //   149: if_icmpne +13 -> 162
    //   152: aload_0
    //   153: getfield 1064	com/android/server/audio/AudioService:mHasVibrator	Z
    //   156: ifeq +94 -> 250
    //   159: iload_1
    //   160: istore 4
    //   162: invokestatic 1366	android/os/Binder:clearCallingIdentity	()J
    //   165: lstore 7
    //   167: aload_0
    //   168: getfield 519	com/android/server/audio/AudioService:mSettingsLock	Ljava/lang/Object;
    //   171: astore 9
    //   173: aload 9
    //   175: monitorenter
    //   176: aload_0
    //   177: invokevirtual 1149	com/android/server/audio/AudioService:getRingerModeInternal	()I
    //   180: istore 5
    //   182: aload_0
    //   183: invokevirtual 1817	com/android/server/audio/AudioService:getRingerModeExternal	()I
    //   186: istore 6
    //   188: iload_3
    //   189: ifeq +67 -> 256
    //   192: aload_0
    //   193: iload 4
    //   195: invokespecial 2578	com/android/server/audio/AudioService:setRingerModeExt	(I)V
    //   198: iload 4
    //   200: istore_1
    //   201: aload_0
    //   202: getfield 499	com/android/server/audio/AudioService:mRingerModeDelegate	Landroid/media/AudioManagerInternal$RingerModeDelegate;
    //   205: ifnull +24 -> 229
    //   208: aload_0
    //   209: getfield 499	com/android/server/audio/AudioService:mRingerModeDelegate	Landroid/media/AudioManagerInternal$RingerModeDelegate;
    //   212: iload 6
    //   214: iload 4
    //   216: aload_2
    //   217: iload 5
    //   219: aload_0
    //   220: getfield 976	com/android/server/audio/AudioService:mVolumePolicy	Landroid/media/VolumePolicy;
    //   223: invokeinterface 2582 6 0
    //   228: istore_1
    //   229: iload_1
    //   230: iload 5
    //   232: if_icmpeq +9 -> 241
    //   235: aload_0
    //   236: iload_1
    //   237: iconst_1
    //   238: invokespecial 808	com/android/server/audio/AudioService:setRingerModeInt	(IZ)V
    //   241: aload 9
    //   243: monitorexit
    //   244: lload 7
    //   246: invokestatic 1374	android/os/Binder:restoreCallingIdentity	(J)V
    //   249: return
    //   250: iconst_0
    //   251: istore 4
    //   253: goto -91 -> 162
    //   256: iload 4
    //   258: iload 5
    //   260: if_icmpeq +10 -> 270
    //   263: aload_0
    //   264: iload 4
    //   266: iconst_1
    //   267: invokespecial 808	com/android/server/audio/AudioService:setRingerModeInt	(IZ)V
    //   270: iload 4
    //   272: istore_1
    //   273: aload_0
    //   274: getfield 499	com/android/server/audio/AudioService:mRingerModeDelegate	Landroid/media/AudioManagerInternal$RingerModeDelegate;
    //   277: ifnull +24 -> 301
    //   280: aload_0
    //   281: getfield 499	com/android/server/audio/AudioService:mRingerModeDelegate	Landroid/media/AudioManagerInternal$RingerModeDelegate;
    //   284: iload 5
    //   286: iload 4
    //   288: aload_2
    //   289: iload 6
    //   291: aload_0
    //   292: getfield 976	com/android/server/audio/AudioService:mVolumePolicy	Landroid/media/VolumePolicy;
    //   295: invokeinterface 2585 6 0
    //   300: istore_1
    //   301: aload_0
    //   302: iload_1
    //   303: invokespecial 2578	com/android/server/audio/AudioService:setRingerModeExt	(I)V
    //   306: goto -65 -> 241
    //   309: astore_2
    //   310: aload 9
    //   312: monitorexit
    //   313: aload_2
    //   314: athrow
    //   315: astore_2
    //   316: lload 7
    //   318: invokestatic 1374	android/os/Binder:restoreCallingIdentity	(J)V
    //   321: aload_2
    //   322: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	323	0	this	AudioService
    //   0	323	1	paramInt	int
    //   0	323	2	paramString	String
    //   0	323	3	paramBoolean	boolean
    //   145	142	4	i	int
    //   180	105	5	j	int
    //   186	104	6	k	int
    //   165	152	7	l	long
    // Exception table:
    //   from	to	target	type
    //   176	188	309	finally
    //   192	198	309	finally
    //   201	229	309	finally
    //   235	241	309	finally
    //   263	270	309	finally
    //   273	301	309	finally
    //   301	306	309	finally
    //   167	176	315	finally
    //   241	244	315	finally
    //   310	315	315	finally
  }
  
  private void setRingerModeExt(int paramInt)
  {
    synchronized (this.mSettingsLock)
    {
      int i = this.mRingerModeExternal;
      if (paramInt == i) {
        return;
      }
      this.mRingerModeExternal = paramInt;
      broadcastRingerMode("android.media.RINGER_MODE_CHANGED", paramInt);
      return;
    }
  }
  
  private void setRingerModeInt(int paramInt, boolean paramBoolean)
  {
    synchronized (this.mSettingsLock)
    {
      if (this.mRingerMode != paramInt)
      {
        i = 1;
        this.mRingerMode = paramInt;
        if ((!this.mBootCompelet) && (paramBoolean))
        {
          Settings.Global.putInt(this.mContentResolver, "mode_ringer", this.mRingerMode);
          if (DEBUG_VOL) {
            Log.d("AudioService", "setRingerModeInt set ringerMode  " + this.mRingerMode + " to database to avoid user-switch and data do not store in time");
          }
        }
        muteRingerModeStreams();
        if (paramBoolean) {
          sendMsg(this.mAudioHandler, 3, 0, 0, 0, null, 500);
        }
        if (i != 0) {
          broadcastRingerMode("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION", paramInt);
        }
        return;
      }
      int i = 0;
    }
  }
  
  private void setSafeMediaVolumeEnabled(boolean paramBoolean, String paramString)
  {
    synchronized (this.mSafeMediaVolumeState)
    {
      if ((this.mSafeMediaVolumeState.intValue() != 0) && (this.mSafeMediaVolumeState.intValue() != 1))
      {
        if ((!paramBoolean) || (this.mSafeMediaVolumeState.intValue() != 2)) {
          break label59;
        }
        this.mSafeMediaVolumeState = Integer.valueOf(3);
        enforceSafeMediaVolume(paramString);
      }
      label59:
      while ((paramBoolean) || (this.mSafeMediaVolumeState.intValue() != 3)) {
        return;
      }
      this.mSafeMediaVolumeState = Integer.valueOf(2);
      this.mMusicActiveMs = 1;
      saveMusicActiveMs();
      sendMsg(this.mAudioHandler, 14, 0, 0, 0, paramString, 60000);
    }
  }
  
  private void setStreamVolume(int paramInt1, int paramInt2, int paramInt3, String arg4, String paramString2, int paramInt4)
  {
    if (this.mUseFixedVolume) {
      return;
    }
    ensureValidStreamType(paramInt1);
    int j = this.mStreamVolumeAlias[paramInt1];
    VolumeStreamState localVolumeStreamState = this.mStreamStates[j];
    int k = getDeviceForStream(paramInt1);
    if (DEBUG_VOL) {
      Log.d("AudioService", "setStreamVolume() stream=" + paramInt1 + ", index=" + paramInt2 + ", flags=" + paramInt3 + ", caller=" + paramString2 + ", callingPackage=" + ???);
    }
    int i = paramInt2;
    if (j == 2)
    {
      i = paramInt2;
      if (paramString2 != null)
      {
        if (!paramString2.equals(new String("android.media.cts"))) {
          break label166;
        }
        i = paramInt2;
      }
    }
    if (((k & 0x380) == 0) && ((paramInt3 & 0x40) != 0))
    {
      return;
      label166:
      i = paramInt2;
      if (paramInt2 < this.mOnePlusMinRingVolumeIndex) {
        i = this.mOnePlusMinRingVolumeIndex;
      }
      paramInt2 = i;
      if (paramString2 != null)
      {
        if (!paramString2.equals(new String("com.google.android.gms"))) {
          break label266;
        }
        paramInt2 = i;
      }
      for (;;)
      {
        Log.e("AudioService", "volume range fixed to [" + this.mOnePlusMinRingVolumeIndex + "," + this.mOnePlusMaxRingVolumeIndex + "] by system");
        i = paramInt2;
        break;
        label266:
        paramInt2 = i;
        if (i > this.mOnePlusMaxRingVolumeIndex) {
          paramInt2 = this.mOnePlusMaxRingVolumeIndex;
        }
      }
    }
    paramInt2 = paramInt4;
    if (paramInt4 == 1000) {
      paramInt2 = UserHandle.getUid(getCurrentUserId(), UserHandle.getAppId(paramInt4));
    }
    if (this.mAppOps.noteOp(STREAM_VOLUME_OPS[j], paramInt2, ???) != 0) {
      return;
    }
    if ((!isAndroidNPlus(???)) || (!wouldToggleZenMode(getNewRingerMode(j, i, paramInt3))) || (this.mNm.isNotificationPolicyAccessGrantedForPackage(???)))
    {
      if (volumeAdjustmentAllowedByDnd(j, paramInt3)) {}
    }
    else {
      throw new SecurityException("Not allowed to change Do Not Disturb state");
    }
    for (;;)
    {
      synchronized (this.mSafeMediaVolumeState)
      {
        this.mPendingVolumeCommand = null;
        int m = localVolumeStreamState.getIndex(k);
        paramInt4 = rescaleIndex(i * 10, paramInt1, j);
        if ((j == 3) && ((k & 0x380) != 0) && ((paramInt3 & 0x40) == 0)) {}
        synchronized (this.mA2dpAvrcpLock)
        {
          if ((this.mA2dp != null) && (this.mAvrcpAbsVolSupported)) {
            this.mA2dp.setAvrcpAbsoluteVolume(paramInt4 / 10);
          }
          if (j == 3) {
            setSystemAudioVolume(m, paramInt4, getStreamMaxVolume(paramInt1), paramInt3);
          }
          i = paramInt3 & 0xFFFFFFDF;
          paramInt2 = paramInt4;
          paramInt3 = i;
          if (j == 3)
          {
            paramInt2 = paramInt4;
            paramInt3 = i;
            if ((this.mFixedVolumeDevices & k) != 0)
            {
              i |= 0x20;
              paramInt2 = paramInt4;
              paramInt3 = i;
              if (paramInt4 != 0)
              {
                if ((this.mSafeMediaVolumeState.intValue() != 3) || ((k & 0xC) == 0)) {
                  break label651;
                }
                paramInt2 = this.mSafeMediaVolumeIndex;
                paramInt3 = i;
              }
            }
          }
          if (checkSafeMediaVolume(j, paramInt2, k)) {
            break label663;
          }
          this.mVolumeController.postDisplaySafeVolumeWarning(paramInt3);
          this.mPendingVolumeCommand = new StreamVolumeCommand(paramInt1, paramInt2, paramInt3, k);
          sendVolumeUpdate(paramInt1, m, paramInt2, paramInt3);
          return;
        }
      }
      label651:
      paramInt2 = localVolumeStreamState.getMaxIndex();
      paramInt3 = i;
      continue;
      label663:
      onSetStreamVolume(paramInt1, paramInt2, paramInt3, k, paramString2);
      paramInt2 = this.mStreamStates[paramInt1].getIndex(k);
    }
  }
  
  private void setStreamVolumeInt(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, String paramString)
  {
    VolumeStreamState localVolumeStreamState = this.mStreamStates[paramInt1];
    if ((localVolumeStreamState.setIndex(paramInt2, paramInt3, paramString)) || (paramBoolean)) {
      sendMsg(this.mAudioHandler, 0, 2, paramInt3, 0, localVolumeStreamState, 0);
    }
  }
  
  private void setSystemAudioMute(boolean paramBoolean)
  {
    if ((this.mHdmiManager == null) || (this.mHdmiTvClient == null)) {
      return;
    }
    synchronized (this.mHdmiManager)
    {
      boolean bool = this.mHdmiSystemAudioSupported;
      if (!bool) {
        return;
      }
      synchronized (this.mHdmiTvClient)
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          this.mHdmiTvClient.setSystemAudioMute(paramBoolean);
          Binder.restoreCallingIdentity(l);
          return;
        }
        finally
        {
          localObject2 = finally;
          Binder.restoreCallingIdentity(l);
          throw ((Throwable)localObject2);
        }
      }
    }
  }
  
  private void setSystemAudioVolume(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.mHdmiManager == null) || (this.mHdmiTvClient == null)) {}
    while ((paramInt1 == paramInt2) || ((paramInt4 & 0x100) != 0)) {
      return;
    }
    synchronized (this.mHdmiManager)
    {
      boolean bool = this.mHdmiSystemAudioSupported;
      if (!bool) {
        return;
      }
      synchronized (this.mHdmiTvClient)
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          this.mHdmiTvClient.setSystemAudioVolume(paramInt1, paramInt2, paramInt3);
          Binder.restoreCallingIdentity(l);
          return;
        }
        finally
        {
          localObject2 = finally;
          Binder.restoreCallingIdentity(l);
          throw ((Throwable)localObject2);
        }
      }
    }
  }
  
  private int updateFlagsForSystemAudio(int paramInt)
  {
    int i = paramInt;
    if (this.mHdmiTvClient != null) {}
    synchronized (this.mHdmiTvClient)
    {
      boolean bool = this.mHdmiSystemAudioSupported;
      i = paramInt;
      if (bool)
      {
        i = paramInt;
        if ((paramInt & 0x100) == 0) {
          i = paramInt & 0xFFFFFFFE;
        }
      }
      return i;
    }
  }
  
  private void updateMasterMono(ContentResolver paramContentResolver)
  {
    if (Settings.System.getIntForUser(paramContentResolver, "master_mono", 0, -2) == 1) {}
    for (boolean bool = true;; bool = false)
    {
      if (DEBUG_VOL) {
        Log.d("AudioService", String.format("Master mono %b", new Object[] { Boolean.valueOf(bool) }));
      }
      AudioSystem.setMasterMono(bool);
      return;
    }
  }
  
  private boolean updateRingerModeAffectedStreams()
  {
    int j = Settings.System.getIntForUser(this.mContentResolver, "mode_ringer_streams_affected", 166, -2);
    int i;
    if (this.mPlatformType == 2) {
      i = 0;
    }
    label142:
    synchronized (this.mCameraSoundForced)
    {
      for (;;)
      {
        boolean bool = this.mCameraSoundForced.booleanValue();
        if (!bool) {
          break;
        }
        i &= 0xFF7F;
        if (this.mStreamVolumeAlias[8] != 2) {
          break label142;
        }
        i |= 0x100;
        if (i == this.mRingerModeAffectedStreams) {
          break label151;
        }
        Settings.System.putIntForUser(this.mContentResolver, "mode_ringer_streams_affected", i, -2);
        this.mRingerModeAffectedStreams = i;
        return true;
        i = j;
        if (this.mRingerModeDelegate != null) {
          i = this.mRingerModeDelegate.getRingerModeAffectedStreams(j);
        }
      }
      i |= 0x80;
    }
    label151:
    return false;
  }
  
  private void updateStreamVolumeAlias(boolean paramBoolean, String paramString)
  {
    int i;
    switch (this.mPlatformType)
    {
    default: 
      this.mStreamVolumeAlias = this.STREAM_VOLUME_ALIAS_DEFAULT;
      i = 3;
      if (isPlatformTelevision()) {
        this.mRingerModeAffectedStreams = 0;
      }
      break;
    }
    for (;;)
    {
      this.mStreamVolumeAlias[8] = i;
      if (paramBoolean)
      {
        this.mStreamStates[8].setAllIndexes(this.mStreamStates[i], paramString);
        setRingerModeInt(getRingerModeInternal(), false);
        sendMsg(this.mAudioHandler, 10, 2, 0, 0, this.mStreamStates[8], 0);
      }
      return;
      this.mStreamVolumeAlias = this.STREAM_VOLUME_ALIAS_VOICE;
      i = 2;
      break;
      this.mStreamVolumeAlias = this.STREAM_VOLUME_ALIAS_TELEVISION;
      i = 3;
      break;
      if (isInCommunication())
      {
        i = 0;
        this.mRingerModeAffectedStreams &= 0xFEFF;
      }
      else
      {
        this.mRingerModeAffectedStreams |= 0x100;
      }
    }
  }
  
  private boolean volumeAdjustmentAllowedByDnd(int paramInt1, int paramInt2)
  {
    return (this.mNm.getZenMode() != 2) || (!isStreamMutedByRingerMode(paramInt1)) || ((paramInt2 & 0x2) != 0) || (paramInt1 == getUiSoundsStreamType());
  }
  
  private void waitForAudioHandlerCreation()
  {
    try
    {
      for (;;)
      {
        AudioHandler localAudioHandler = this.mAudioHandler;
        if (localAudioHandler != null) {
          break;
        }
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException)
        {
          Log.e("AudioService", "Interrupted while waiting on volume handler.");
        }
      }
    }
    finally {}
  }
  
  private boolean wouldToggleZenMode(int paramInt)
  {
    if ((getRingerModeExternal() == 0) && (paramInt != 0)) {
      return true;
    }
    return (getRingerModeExternal() != 0) && (paramInt == 0);
  }
  
  public int abandonAudioFocus(IAudioFocusDispatcher paramIAudioFocusDispatcher, String paramString, AudioAttributes paramAudioAttributes)
  {
    return this.mMediaFocusControl.abandonAudioFocus(paramIAudioFocusDispatcher, paramString, paramAudioAttributes);
  }
  
  public void addMediaPlayerAndUpdateRemoteController(String paramString)
  {
    int j;
    for (;;)
    {
      MediaPlayerInfo localMediaPlayerInfo;
      synchronized (mMediaPlayers)
      {
        Log.v("AudioService", "addMediaPlayerAndUpdateRemoteController: size of existing list: " + mMediaPlayers.size());
        j = 1;
        int i = 1;
        if (mMediaPlayers.size() <= 0) {
          break;
        }
        localObject = mMediaPlayers.iterator();
        j = i;
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
        localMediaPlayerInfo = (MediaPlayerInfo)((Iterator)localObject).next();
        if (paramString.equals(localMediaPlayerInfo.getPackageName()))
        {
          Log.e("AudioService", "Player entry present, no need to add");
          i = 0;
          localMediaPlayerInfo.setFocus(true);
        }
      }
      Log.e("AudioService", "Player: " + localMediaPlayerInfo.getPackageName() + "Lost Focus");
      localMediaPlayerInfo.setFocus(false);
    }
    if (j != 0)
    {
      Log.e("AudioService", "Adding Player: " + paramString + " to available player list");
      mMediaPlayers.add(new MediaPlayerInfo(paramString, true));
    }
    Object localObject = new Intent("org.codeaurora.bluetooth.RCC_CHANGED_ACTION");
    ((Intent)localObject).putExtra("org.codeaurora.bluetooth.EXTRA_CALLING_PACKAGE_NAME", paramString);
    ((Intent)localObject).putExtra("org.codeaurora.bluetooth.EXTRA_FOCUS_CHANGED_VALUE", true);
    ((Intent)localObject).putExtra("org.codeaurora.bluetooth.EXTRA_AVAILABLITY_CHANGED_VALUE", true);
    sendBroadcastToAll((Intent)localObject);
    Log.v("AudioService", "updating focussed RCC change to RCD: CallingPackageName:" + paramString);
  }
  
  public void adjustStreamVolume(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    adjustStreamVolume(paramInt1, paramInt2, paramInt3, paramString, paramString, Binder.getCallingUid());
  }
  
  public void adjustSuggestedStreamVolume(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2)
  {
    adjustSuggestedStreamVolume(paramInt1, paramInt2, paramInt3, paramString1, paramString2, Binder.getCallingUid());
  }
  
  public void avrcpSupportsAbsoluteVolume(String arg1, boolean paramBoolean)
  {
    synchronized (this.mA2dpAvrcpLock)
    {
      this.mAvrcpAbsVolSupported = paramBoolean;
      sendMsg(this.mAudioHandler, 0, 2, 128, 0, this.mStreamStates[3], 0);
      sendMsg(this.mAudioHandler, 0, 2, 128, 0, this.mStreamStates[2], 0);
      return;
    }
  }
  
  boolean checkAudioSettingsPermission(String paramString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_SETTINGS") == 0) {
      return true;
    }
    Log.w("AudioService", "Audio Settings Permission Denial: " + paramString + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
    return false;
  }
  
  public void clearAllScoClients(int paramInt, boolean paramBoolean)
  {
    ArrayList localArrayList = this.mScoClients;
    Object localObject1 = null;
    int i;
    ScoClient localScoClient;
    try
    {
      int j = this.mScoClients.size();
      i = 0;
      if (i < j)
      {
        localScoClient = (ScoClient)this.mScoClients.get(i);
        if (localScoClient.getPid() == paramInt) {
          break label101;
        }
        localScoClient.clearCount(paramBoolean);
      }
      else
      {
        this.mScoClients.clear();
        if (localObject1 != null) {
          this.mScoClients.add(localObject1);
        }
        return;
      }
    }
    finally {}
    for (;;)
    {
      i += 1;
      break;
      label101:
      Object localObject3 = localScoClient;
    }
  }
  
  public void disableSafeMediaVolume(String paramString)
  {
    enforceVolumeController("disable the safe media volume");
    synchronized (this.mSafeMediaVolumeState)
    {
      setSafeMediaVolumeEnabled(false, paramString);
      if (this.mPendingVolumeCommand != null)
      {
        onSetStreamVolume(this.mPendingVolumeCommand.mStreamType, this.mPendingVolumeCommand.mIndex, this.mPendingVolumeCommand.mFlags, this.mPendingVolumeCommand.mDevice, paramString);
        this.mPendingVolumeCommand = null;
      }
      return;
    }
  }
  
  void disconnectA2dp()
  {
    label157:
    for (;;)
    {
      Object localObject4;
      synchronized (this.mConnectedDevices)
      {
        localObject4 = this.mA2dpAvrcpLock;
        int i = 0;
        ArraySet localArraySet = null;
        DeviceListSpec localDeviceListSpec;
        int j;
        try
        {
          if (i < this.mConnectedDevices.size())
          {
            localDeviceListSpec = (DeviceListSpec)this.mConnectedDevices.valueAt(i);
            j = localDeviceListSpec.mDeviceType;
            if (j != 128) {
              break label157;
            }
            if (localArraySet == null) {}
          }
        }
        finally {}
        try
        {
          localArraySet.add(localDeviceListSpec.mDeviceAddress);
          i += 1;
          continue;
        }
        finally {}
        localArraySet = new ArraySet();
        continue;
        if (localArraySet != null)
        {
          j = checkSendBecomingNoisyIntent(128, 0);
          i = 0;
          if (i < localArraySet.size())
          {
            makeA2dpDeviceUnavailableLater((String)localArraySet.valueAt(i), j);
            i += 1;
            continue;
          }
        }
        return;
      }
    }
  }
  
  void disconnectA2dpSink()
  {
    ArrayMap localArrayMap = this.mConnectedDevices;
    int i = 0;
    for (ArraySet localArraySet = null;; localArraySet = new ArraySet()) {
      for (;;)
      {
        DeviceListSpec localDeviceListSpec;
        try
        {
          if (i >= this.mConnectedDevices.size()) {
            break label126;
          }
          localDeviceListSpec = (DeviceListSpec)this.mConnectedDevices.valueAt(i);
          int j = localDeviceListSpec.mDeviceType;
          if (j != -2147352576) {
            break label123;
          }
          if (localArraySet == null) {}
        }
        finally {}
        try
        {
          localArraySet.add(localDeviceListSpec.mDeviceAddress);
          i += 1;
        }
        finally {}
      }
    }
    for (;;)
    {
      if (i < localArraySet.size())
      {
        makeA2dpSrcUnavailable((String)localArraySet.valueAt(i));
        i += 1;
      }
      else
      {
        label123:
        label126:
        do
        {
          return;
          break;
        } while (localObject2 == null);
        i = 0;
      }
    }
  }
  
  void disconnectAllBluetoothProfiles()
  {
    disconnectA2dp();
    disconnectA2dpSink();
    disconnectHeadset();
  }
  
  void disconnectHeadset()
  {
    synchronized (this.mScoClients)
    {
      if (this.mBluetoothHeadsetDevice != null) {
        setBtScoDeviceConnectionState(this.mBluetoothHeadsetDevice, 0);
      }
      this.mBluetoothHeadset = null;
      return;
    }
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "AudioService");
    this.mMediaFocusControl.dump(paramPrintWriter);
    dumpStreamStates(paramPrintWriter);
    dumpRingerMode(paramPrintWriter);
    paramPrintWriter.println("\nAudio routes:");
    paramPrintWriter.print("  mMainType=0x");
    paramPrintWriter.println(Integer.toHexString(this.mCurAudioRoutes.mainType));
    paramPrintWriter.print("  mBluetoothName=");
    paramPrintWriter.println(this.mCurAudioRoutes.bluetoothName);
    paramPrintWriter.println("\nOther state:");
    paramPrintWriter.print("  mVolumeController=");
    paramPrintWriter.println(this.mVolumeController);
    paramPrintWriter.print("  mSafeMediaVolumeState=");
    paramPrintWriter.println(safeMediaVolumeStateToString(this.mSafeMediaVolumeState));
    paramPrintWriter.print("  mSafeMediaVolumeIndex=");
    paramPrintWriter.println(this.mSafeMediaVolumeIndex);
    paramPrintWriter.print("  mPendingVolumeCommand=");
    paramPrintWriter.println(this.mPendingVolumeCommand);
    paramPrintWriter.print("  mMusicActiveMs=");
    paramPrintWriter.println(this.mMusicActiveMs);
    paramPrintWriter.print("  mMcc=");
    paramPrintWriter.println(this.mMcc);
    paramPrintWriter.print("  mCameraSoundForced=");
    paramPrintWriter.println(this.mCameraSoundForced);
    paramPrintWriter.print("  mHasVibrator=");
    paramPrintWriter.println(this.mHasVibrator);
    paramPrintWriter.print("  mControllerService=");
    paramPrintWriter.println(this.mControllerService);
    paramPrintWriter.print("  mVolumePolicy=");
    paramPrintWriter.println(this.mVolumePolicy);
    dumpAudioPolicies(paramPrintWriter);
  }
  
  public void forceRemoteSubmixFullVolume(boolean paramBoolean, IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return;
    }
    if (this.mContext.checkCallingOrSelfPermission("android.permission.CAPTURE_AUDIO_OUTPUT") != 0)
    {
      Log.w("AudioService", "Trying to call forceRemoteSubmixFullVolume() without CAPTURE_AUDIO_OUTPUT");
      return;
    }
    localArrayList = this.mRmtSbmxFullVolDeathHandlers;
    int k = 0;
    int j = 0;
    int i;
    if (paramBoolean) {
      i = k;
    }
    for (;;)
    {
      try
      {
        if (!hasRmtSbmxFullVolDeathHandlerFor(paramIBinder))
        {
          this.mRmtSbmxFullVolDeathHandlers.add(new RmtSbmxFullVolDeathHandler(paramIBinder));
          i = j;
          if (this.mRmtSbmxFullVolRefCount == 0)
          {
            this.mFullVolumeDevices |= 0x8000;
            this.mFixedVolumeDevices |= 0x8000;
            i = 1;
          }
          this.mRmtSbmxFullVolRefCount += 1;
        }
        if (i != 0)
        {
          checkAllFixedVolumeDevices(3);
          this.mStreamStates[3].applyAllVolumes();
        }
        return;
      }
      finally {}
      i = k;
      if (discardRmtSbmxFullVolDeathHandlerFor(paramIBinder))
      {
        i = k;
        if (this.mRmtSbmxFullVolRefCount > 0)
        {
          this.mRmtSbmxFullVolRefCount -= 1;
          i = k;
          if (this.mRmtSbmxFullVolRefCount == 0)
          {
            this.mFullVolumeDevices &= 0xFFFF7FFF;
            this.mFixedVolumeDevices &= 0xFFFF7FFF;
            i = 1;
          }
        }
      }
    }
  }
  
  public void forceVolumeControlStream(int paramInt, IBinder paramIBinder)
  {
    synchronized (this.mForceControlStreamLock)
    {
      this.mVolumeControlStream = paramInt;
      if (this.mVolumeControlStream == -1)
      {
        if (this.mForceControlStreamClient != null)
        {
          this.mForceControlStreamClient.release();
          this.mForceControlStreamClient = null;
        }
        return;
      }
      this.mForceControlStreamClient = new ForceControlStreamClient(paramIBinder);
    }
  }
  
  public List<AudioRecordingConfiguration> getActiveRecordingConfigurations()
  {
    return this.mRecordMonitor.getActiveRecordingConfigurations();
  }
  
  public int getCurrentAudioFocus()
  {
    return this.mMediaFocusControl.getCurrentAudioFocus();
  }
  
  public int getLastAudibleStreamVolume(int paramInt)
  {
    ensureValidStreamType(paramInt);
    int i = getDeviceForStream(paramInt);
    return (this.mStreamStates[paramInt].getIndex(i) + 5) / 10;
  }
  
  public int getMode()
  {
    return this.mMode;
  }
  
  public int getRingerModeExternal()
  {
    synchronized (this.mSettingsLock)
    {
      int i = this.mRingerModeExternal;
      return i;
    }
  }
  
  public int getRingerModeInternal()
  {
    synchronized (this.mSettingsLock)
    {
      int i = this.mRingerMode;
      return i;
    }
  }
  
  public IRingtonePlayer getRingtonePlayer()
  {
    return this.mRingtonePlayer;
  }
  
  public int getStreamMaxVolume(int paramInt)
  {
    ensureValidStreamType(paramInt);
    return (this.mStreamStates[paramInt].getMaxIndex() + 5) / 10;
  }
  
  public int getStreamMinVolume(int paramInt)
  {
    ensureValidStreamType(paramInt);
    return (this.mStreamStates[paramInt].getMinIndex() + 5) / 10;
  }
  
  public int getStreamVolume(int paramInt)
  {
    ensureValidStreamType(paramInt);
    int k = getDeviceForStream(paramInt);
    try
    {
      int i = this.mStreamStates[paramInt].getIndex(k);
      if (VolumeStreamState.-get3(this.mStreamStates[paramInt])) {
        i = 0;
      }
      int j = i;
      if (i != 0)
      {
        j = i;
        if (this.mStreamVolumeAlias[paramInt] == 3)
        {
          j = i;
          if ((this.mFixedVolumeDevices & k) != 0) {
            j = this.mStreamStates[paramInt].getMaxIndex();
          }
        }
      }
      paramInt = (j + 5) / 10;
      return paramInt;
    }
    finally {}
  }
  
  public int getUiSoundsStreamType()
  {
    return this.mStreamVolumeAlias[1];
  }
  
  public int getVibrateSetting(int paramInt)
  {
    if (!this.mHasVibrator) {
      return 0;
    }
    return this.mVibrateSetting >> paramInt * 2 & 0x3;
  }
  
  public boolean isASBluetoothA2dpOn()
  {
    if (AudioSystem.getDeviceConnectionState(128, "") == 1) {
      return true;
    }
    if (AudioSystem.getDeviceConnectionState(256, "") == 1) {
      return true;
    }
    return AudioSystem.getDeviceConnectionState(512, "") == 1;
  }
  
  public boolean isBluetoothA2dpOn()
  {
    synchronized (this.mBluetoothA2dpEnabledLock)
    {
      boolean bool = this.mBluetoothA2dpEnabled;
      return bool;
    }
  }
  
  public boolean isBluetoothScoOn()
  {
    return this.mForcedUseForComm == 3;
  }
  
  public boolean isCameraSoundForced()
  {
    synchronized (this.mCameraSoundForced)
    {
      boolean bool = this.mCameraSoundForced.booleanValue();
      return bool;
    }
  }
  
  public boolean isHasSpeakerAuthority(String paramString)
  {
    if (paramString.equals("com.android.server.telecom")) {}
    for (boolean bool = true;; bool = false)
    {
      Log.d("AudioService", "isHasSpeakerAuthority APP keys: " + paramString + ",mIsTelName=" + bool);
      if ((isInCallState()) && (!bool)) {
        break;
      }
      return true;
    }
    return false;
  }
  
  public boolean isHdmiSystemAudioSupported()
  {
    return this.mHdmiSystemAudioSupported;
  }
  
  public boolean isInCallState()
  {
    TelecomManager localTelecomManager = (TelecomManager)this.mContext.getSystemService("telecom");
    long l = Binder.clearCallingIdentity();
    boolean bool = localTelecomManager.isInCall();
    Binder.restoreCallingIdentity(l);
    Log.d("AudioService", "isInCallState =" + bool);
    return bool;
  }
  
  public boolean isMasterMute()
  {
    return AudioSystem.getMasterMute();
  }
  
  public boolean isSpeakerphoneOn()
  {
    return this.mForcedUseForComm == 1;
  }
  
  public boolean isStreamAffectedByMute(int paramInt)
  {
    return (this.mMuteAffectedStreams & 1 << paramInt) != 0;
  }
  
  public boolean isStreamAffectedByRingerMode(int paramInt)
  {
    return (this.mRingerModeAffectedStreams & 1 << paramInt) != 0;
  }
  
  public boolean isStreamMute(int paramInt)
  {
    int i = paramInt;
    if (paramInt == Integer.MIN_VALUE) {
      i = getActiveStreamType(paramInt);
    }
    try
    {
      boolean bool = VolumeStreamState.-get3(this.mStreamStates[i]);
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean isValidRingerMode(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt <= 2) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean loadSoundEffects()
  {
    int i;
    synchronized (new LoadSoundEffectReply())
    {
      sendMsg(this.mAudioHandler, 7, 2, 0, 0, ???, 0);
      i = 3;
    }
    try
    {
      for (;;)
      {
        int j = ???.mStatus;
        if ((j == 1) && (i > 0)) {}
        try
        {
          ???.wait(5000L);
          i -= 1;
          continue;
          if (???.mStatus != 0) {
            break label92;
          }
          return true;
        }
        catch (InterruptedException localInterruptedException)
        {
          Log.w("AudioService", "loadSoundEffects Interrupted while waiting sound pool loaded.");
        }
      }
      localObject1 = finally;
    }
    finally
    {
      label92:
      for (;;) {}
    }
    throw ((Throwable)localObject1);
    return false;
  }
  
  public void notifyVolumeControllerVisible(IVolumeController paramIVolumeController, boolean paramBoolean)
  {
    enforceVolumeController("notify about volume controller visibility");
    if (!this.mVolumeController.isSameBinder(paramIVolumeController)) {
      return;
    }
    this.mVolumeController.setVisible(paramBoolean);
    if (DEBUG_VOL) {
      Log.d("AudioService", "Volume controller visible: " + paramBoolean);
    }
  }
  
  /* Error */
  public void onAudioServerDied()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_0
    //   3: getfield 549	com/android/server/audio/AudioService:mSystemReady	Z
    //   6: ifeq +9 -> 15
    //   9: invokestatic 2865	android/media/AudioSystem:checkAudioFlinger	()I
    //   12: ifeq +28 -> 40
    //   15: ldc -5
    //   17: ldc_w 2867
    //   20: invokestatic 1291	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   23: pop
    //   24: aload_0
    //   25: getfield 578	com/android/server/audio/AudioService:mAudioHandler	Lcom/android/server/audio/AudioService$AudioHandler;
    //   28: iconst_4
    //   29: iconst_1
    //   30: iconst_0
    //   31: iconst_0
    //   32: aconst_null
    //   33: sipush 500
    //   36: invokestatic 776	com/android/server/audio/AudioService:sendMsg	(Landroid/os/Handler;IIIILjava/lang/Object;I)V
    //   39: return
    //   40: ldc -5
    //   42: ldc_w 2869
    //   45: invokestatic 1291	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   48: pop
    //   49: ldc_w 2871
    //   52: invokestatic 2098	android/media/AudioSystem:setParameters	(Ljava/lang/String;)I
    //   55: pop
    //   56: invokestatic 1144	com/android/server/audio/AudioService:readAndSetLowRamDevice	()V
    //   59: aload_0
    //   60: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   63: astore_3
    //   64: aload_3
    //   65: monitorenter
    //   66: iconst_0
    //   67: istore_1
    //   68: iload_1
    //   69: aload_0
    //   70: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   73: invokevirtual 1515	android/util/ArrayMap:size	()I
    //   76: if_icmpge +43 -> 119
    //   79: aload_0
    //   80: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   83: iload_1
    //   84: invokevirtual 1519	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   87: checkcast 39	com/android/server/audio/AudioService$DeviceListSpec
    //   90: astore 4
    //   92: aload 4
    //   94: getfield 1522	com/android/server/audio/AudioService$DeviceListSpec:mDeviceType	I
    //   97: iconst_1
    //   98: aload 4
    //   100: getfield 2215	com/android/server/audio/AudioService$DeviceListSpec:mDeviceAddress	Ljava/lang/String;
    //   103: aload 4
    //   105: getfield 2874	com/android/server/audio/AudioService$DeviceListSpec:mDeviceName	Ljava/lang/String;
    //   108: invokestatic 1893	android/media/AudioSystem:setDeviceConnectionState	(IILjava/lang/String;Ljava/lang/String;)I
    //   111: pop
    //   112: iload_1
    //   113: iconst_1
    //   114: iadd
    //   115: istore_1
    //   116: goto -48 -> 68
    //   119: aload_3
    //   120: monitorexit
    //   121: aload_0
    //   122: getfield 898	com/android/server/audio/AudioService:mMode	I
    //   125: invokestatic 2531	android/media/AudioSystem:setPhoneState	(I)I
    //   128: pop
    //   129: iconst_0
    //   130: aload_0
    //   131: getfield 1089	com/android/server/audio/AudioService:mForcedUseForComm	I
    //   134: invokestatic 2484	android/media/AudioSystem:setForceUse	(II)I
    //   137: pop
    //   138: iconst_2
    //   139: aload_0
    //   140: getfield 1089	com/android/server/audio/AudioService:mForcedUseForComm	I
    //   143: invokestatic 2484	android/media/AudioSystem:setForceUse	(II)I
    //   146: pop
    //   147: aload_0
    //   148: getfield 446	com/android/server/audio/AudioService:mCameraSoundForced	Ljava/lang/Boolean;
    //   151: invokevirtual 1840	java/lang/Boolean:booleanValue	()Z
    //   154: ifeq +66 -> 220
    //   157: bipush 11
    //   159: istore_1
    //   160: iconst_4
    //   161: iload_1
    //   162: invokestatic 2484	android/media/AudioSystem:setForceUse	(II)I
    //   165: pop
    //   166: invokestatic 1436	android/media/AudioSystem:getNumStreamTypes	()I
    //   169: iconst_1
    //   170: isub
    //   171: istore_1
    //   172: iload_1
    //   173: iflt +52 -> 225
    //   176: aload_0
    //   177: getfield 542	com/android/server/audio/AudioService:mStreamStates	[Lcom/android/server/audio/AudioService$VolumeStreamState;
    //   180: iload_1
    //   181: aaload
    //   182: astore_3
    //   183: iload_1
    //   184: aload_3
    //   185: invokestatic 1500	com/android/server/audio/AudioService$VolumeStreamState:-get2	(Lcom/android/server/audio/AudioService$VolumeStreamState;)I
    //   188: bipush 10
    //   190: idiv
    //   191: aload_3
    //   192: invokestatic 2876	com/android/server/audio/AudioService$VolumeStreamState:-get1	(Lcom/android/server/audio/AudioService$VolumeStreamState;)I
    //   195: bipush 10
    //   197: idiv
    //   198: invokestatic 2879	android/media/AudioSystem:initStreamVolume	(III)I
    //   201: pop
    //   202: aload_3
    //   203: invokevirtual 1443	com/android/server/audio/AudioService$VolumeStreamState:applyAllVolumes	()V
    //   206: iload_1
    //   207: iconst_1
    //   208: isub
    //   209: istore_1
    //   210: goto -38 -> 172
    //   213: astore 4
    //   215: aload_3
    //   216: monitorexit
    //   217: aload 4
    //   219: athrow
    //   220: iconst_0
    //   221: istore_1
    //   222: goto -62 -> 160
    //   225: aload_0
    //   226: aload_0
    //   227: getfield 454	com/android/server/audio/AudioService:mContentResolver	Landroid/content/ContentResolver;
    //   230: invokespecial 816	com/android/server/audio/AudioService:updateMasterMono	(Landroid/content/ContentResolver;)V
    //   233: aload_0
    //   234: aload_0
    //   235: invokevirtual 1149	com/android/server/audio/AudioService:getRingerModeInternal	()I
    //   238: iconst_0
    //   239: invokespecial 808	com/android/server/audio/AudioService:setRingerModeInt	(IZ)V
    //   242: aload_0
    //   243: getfield 1189	com/android/server/audio/AudioService:mMonitorOrientation	Z
    //   246: ifeq +7 -> 253
    //   249: aload_0
    //   250: invokespecial 1198	com/android/server/audio/AudioService:setOrientationForAudioSystem	()V
    //   253: aload_0
    //   254: getfield 492	com/android/server/audio/AudioService:mMonitorRotation	Z
    //   257: ifeq +6 -> 263
    //   260: invokestatic 2882	com/android/server/audio/RotationHelper:updateOrientation	()V
    //   263: aload_0
    //   264: getfield 952	com/android/server/audio/AudioService:mBluetoothA2dpEnabledLock	Ljava/lang/Object;
    //   267: astore_3
    //   268: aload_3
    //   269: monitorenter
    //   270: aload_0
    //   271: getfield 2821	com/android/server/audio/AudioService:mBluetoothA2dpEnabled	Z
    //   274: ifeq +134 -> 408
    //   277: iconst_0
    //   278: istore_1
    //   279: iconst_1
    //   280: iload_1
    //   281: invokestatic 2484	android/media/AudioSystem:setForceUse	(II)I
    //   284: pop
    //   285: aload_3
    //   286: monitorexit
    //   287: aload_0
    //   288: getfield 519	com/android/server/audio/AudioService:mSettingsLock	Ljava/lang/Object;
    //   291: astore_3
    //   292: aload_3
    //   293: monitorenter
    //   294: iload_2
    //   295: istore_1
    //   296: aload_0
    //   297: getfield 969	com/android/server/audio/AudioService:mDockAudioMediaEnabled	Z
    //   300: ifeq +6 -> 306
    //   303: bipush 8
    //   305: istore_1
    //   306: iconst_3
    //   307: iload_1
    //   308: invokestatic 2484	android/media/AudioSystem:setForceUse	(II)I
    //   311: pop
    //   312: aload_0
    //   313: aload_0
    //   314: getfield 454	com/android/server/audio/AudioService:mContentResolver	Landroid/content/ContentResolver;
    //   317: invokespecial 2358	com/android/server/audio/AudioService:sendEncodedSurroundMode	(Landroid/content/ContentResolver;)V
    //   320: aload_3
    //   321: monitorexit
    //   322: aload_0
    //   323: getfield 485	com/android/server/audio/AudioService:mHdmiManager	Landroid/hardware/hdmi/HdmiControlManager;
    //   326: ifnull +28 -> 354
    //   329: aload_0
    //   330: getfield 485	com/android/server/audio/AudioService:mHdmiManager	Landroid/hardware/hdmi/HdmiControlManager;
    //   333: astore_3
    //   334: aload_3
    //   335: monitorenter
    //   336: aload_0
    //   337: getfield 2607	com/android/server/audio/AudioService:mHdmiTvClient	Landroid/hardware/hdmi/HdmiTvClient;
    //   340: ifnull +12 -> 352
    //   343: aload_0
    //   344: aload_0
    //   345: getfield 992	com/android/server/audio/AudioService:mHdmiSystemAudioSupported	Z
    //   348: invokevirtual 2885	com/android/server/audio/AudioService:setHdmiSystemAudioSupported	(Z)I
    //   351: pop
    //   352: aload_3
    //   353: monitorexit
    //   354: aload_0
    //   355: getfield 582	com/android/server/audio/AudioService:mAudioPolicies	Ljava/util/HashMap;
    //   358: astore_3
    //   359: aload_3
    //   360: monitorenter
    //   361: aload_0
    //   362: getfield 582	com/android/server/audio/AudioService:mAudioPolicies	Ljava/util/HashMap;
    //   365: invokevirtual 1655	java/util/HashMap:values	()Ljava/util/Collection;
    //   368: invokeinterface 1551 1 0
    //   373: astore 4
    //   375: aload 4
    //   377: invokeinterface 1556 1 0
    //   382: ifeq +53 -> 435
    //   385: aload 4
    //   387: invokeinterface 1560 1 0
    //   392: checkcast 21	com/android/server/audio/AudioService$AudioPolicyProxy
    //   395: invokevirtual 2888	com/android/server/audio/AudioService$AudioPolicyProxy:connectMixes	()V
    //   398: goto -23 -> 375
    //   401: astore 4
    //   403: aload_3
    //   404: monitorexit
    //   405: aload 4
    //   407: athrow
    //   408: bipush 10
    //   410: istore_1
    //   411: goto -132 -> 279
    //   414: astore 4
    //   416: aload_3
    //   417: monitorexit
    //   418: aload 4
    //   420: athrow
    //   421: astore 4
    //   423: aload_3
    //   424: monitorexit
    //   425: aload 4
    //   427: athrow
    //   428: astore 4
    //   430: aload_3
    //   431: monitorexit
    //   432: aload 4
    //   434: athrow
    //   435: aload_3
    //   436: monitorexit
    //   437: aload_0
    //   438: invokevirtual 2891	com/android/server/audio/AudioService:onIndicateSystemReady	()V
    //   441: ldc_w 2893
    //   444: invokestatic 2098	android/media/AudioSystem:setParameters	(Ljava/lang/String;)I
    //   447: pop
    //   448: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	449	0	this	AudioService
    //   67	344	1	i	int
    //   1	294	2	j	int
    //   90	14	4	localDeviceListSpec	DeviceListSpec
    //   213	5	4	localObject2	Object
    //   373	13	4	localIterator	Iterator
    //   401	5	4	localObject3	Object
    //   414	5	4	localObject4	Object
    //   421	5	4	localObject5	Object
    //   428	5	4	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   68	112	213	finally
    //   361	375	401	finally
    //   375	398	401	finally
    //   270	277	414	finally
    //   279	285	414	finally
    //   296	303	421	finally
    //   306	320	421	finally
    //   336	352	428	finally
  }
  
  void onIndicateSystemReady()
  {
    if (AudioSystem.systemReady() == 0) {
      return;
    }
    sendMsg(this.mAudioHandler, 26, 0, 0, 0, null, 1000);
  }
  
  public void onPlaySilentBuffer()
  {
    new playSilentBufferThread().start();
  }
  
  public void onSystemReady()
  {
    this.mSystemReady = true;
    sendMsg(this.mAudioHandler, 7, 2, 0, 0, null, 0);
    this.mScoConnectionState = -1;
    resetBluetoothSco();
    getBluetoothHeadset();
    ??? = new Intent("android.media.SCO_AUDIO_STATE_CHANGED");
    ((Intent)???).putExtra("android.media.extra.SCO_AUDIO_STATE", 0);
    sendStickyBroadcastToAll((Intent)???);
    ??? = BluetoothAdapter.getDefaultAdapter();
    if (??? != null) {
      ((BluetoothAdapter)???).getProfileProxy(this.mContext, this.mBluetoothProfileServiceListener, 2);
    }
    this.mHdmiManager = ((HdmiControlManager)this.mContext.getSystemService("hdmi_control"));
    if (this.mHdmiManager != null) {}
    synchronized (this.mHdmiManager)
    {
      this.mHdmiTvClient = this.mHdmiManager.getTvClient();
      if (this.mHdmiTvClient != null) {
        this.mFixedVolumeDevices &= 0xFFD3FFFD;
      }
      this.mHdmiPlaybackClient = this.mHdmiManager.getPlaybackClient();
      this.mHdmiCecSink = false;
      this.mNm = ((NotificationManager)this.mContext.getSystemService("notification"));
      sendMsg(this.mAudioHandler, 17, 0, 0, 0, "AudioService", 30000);
      StreamOverride.init(this.mContext);
      this.mControllerService.init();
      onIndicateSystemReady();
      return;
    }
  }
  
  public void playSoundEffect(int paramInt)
  {
    playSoundEffectVolume(paramInt, -1.0F);
  }
  
  public void playSoundEffectVolume(int paramInt, float paramFloat)
  {
    if ((paramInt >= 10) || (paramInt < 0))
    {
      Log.w("AudioService", "AudioService effectType value " + paramInt + " out of range");
      return;
    }
    sendMsg(this.mAudioHandler, 5, 2, paramInt, (int)(1000.0F * paramFloat), null, 0);
  }
  
  public String registerAudioPolicy(AudioPolicyConfig paramAudioPolicyConfig, IAudioPolicyCallback paramIAudioPolicyCallback, boolean paramBoolean)
  {
    int i = 0;
    AudioSystem.setDynamicPolicyCallback(this.mDynPolicyCallback);
    if (DEBUG_AP) {
      Log.d("AudioService", "registerAudioPolicy for " + paramIAudioPolicyCallback.asBinder() + " with config:" + paramAudioPolicyConfig);
    }
    if (this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0) {
      i = 1;
    }
    if (i == 0)
    {
      Slog.w("AudioService", "Can't register audio policy for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid() + ", need MODIFY_AUDIO_ROUTING");
      return null;
    }
    synchronized (this.mAudioPolicies)
    {
      try
      {
        if (this.mAudioPolicies.containsKey(paramIAudioPolicyCallback.asBinder()))
        {
          Slog.e("AudioService", "Cannot re-register policy");
          return null;
        }
        paramAudioPolicyConfig = new AudioPolicyProxy(paramAudioPolicyConfig, paramIAudioPolicyCallback, paramBoolean);
        paramIAudioPolicyCallback.asBinder().linkToDeath(paramAudioPolicyConfig, 0);
        String str = paramAudioPolicyConfig.getRegistrationId();
        this.mAudioPolicies.put(paramIAudioPolicyCallback.asBinder(), paramAudioPolicyConfig);
        return str;
      }
      catch (RemoteException paramAudioPolicyConfig)
      {
        Slog.w("AudioService", "Audio policy registration failed, could not link to " + paramIAudioPolicyCallback + " binder death", paramAudioPolicyConfig);
        return null;
      }
    }
  }
  
  public void registerRecordingCallback(IRecordingConfigDispatcher paramIRecordingConfigDispatcher)
  {
    this.mRecordMonitor.registerRecordingCallback(paramIRecordingConfigDispatcher);
  }
  
  public void reloadAudioSettings()
  {
    readAudioSettings(false);
  }
  
  public void removeMediaPlayerAndUpdateRemoteController(String paramString)
  {
    synchronized (mMediaPlayers)
    {
      Log.v("AudioService", "removeMediaPlayerAndUpdateRemoteController: size of existing list: " + mMediaPlayers.size());
      int m = 0;
      int j = -1;
      int i = j;
      int k = m;
      MediaPlayerInfo localMediaPlayerInfo;
      if (mMediaPlayers.size() > 0)
      {
        localObject = mMediaPlayers.iterator();
        i = j;
        k = m;
        if (((Iterator)localObject).hasNext())
        {
          i = j + 1;
          localMediaPlayerInfo = (MediaPlayerInfo)((Iterator)localObject).next();
          if (!paramString.equals(localMediaPlayerInfo.getPackageName())) {
            break label255;
          }
          Log.v("AudioService", "Player entry present remove and update RemoteController");
          k = 1;
        }
      }
      if (k != 0)
      {
        Log.e("AudioService", "Removing Player: " + paramString + " from index" + i);
        mMediaPlayers.remove(i);
      }
      Object localObject = new Intent("org.codeaurora.bluetooth.RCC_CHANGED_ACTION");
      ((Intent)localObject).putExtra("org.codeaurora.bluetooth.EXTRA_CALLING_PACKAGE_NAME", paramString);
      ((Intent)localObject).putExtra("org.codeaurora.bluetooth.EXTRA_FOCUS_CHANGED_VALUE", false);
      ((Intent)localObject).putExtra("org.codeaurora.bluetooth.EXTRA_AVAILABLITY_CHANGED_VALUE", false);
      sendBroadcastToAll((Intent)localObject);
      Log.v("AudioService", "Updated List size: " + mMediaPlayers.size());
      return;
      label255:
      Log.v("AudioService", "Player entry for " + localMediaPlayerInfo.getPackageName() + " is not present");
      j = i;
    }
  }
  
  public int requestAudioFocus(AudioAttributes paramAudioAttributes, int paramInt1, IBinder paramIBinder, IAudioFocusDispatcher paramIAudioFocusDispatcher, String paramString1, String paramString2, int paramInt2, IAudioPolicyCallback paramIAudioPolicyCallback)
  {
    if ((paramInt2 & 0x4) == 4) {
      if ("AudioFocus_For_Phone_Ring_And_Calls".equals(paramString1))
      {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") == 0) {
          break label93;
        }
        Log.e("AudioService", "Invalid permission to (un)lock audio focus", new Exception());
        return 0;
      }
    }
    synchronized (this.mAudioPolicies)
    {
      if (!this.mAudioPolicies.containsKey(paramIAudioPolicyCallback.asBinder()))
      {
        Log.e("AudioService", "Invalid unregistered AudioPolicy to (un)lock audio focus");
        return 0;
      }
      label93:
      return this.mMediaFocusControl.requestAudioFocus(paramAudioAttributes, paramInt1, paramIBinder, paramIAudioFocusDispatcher, paramString1, paramString2, paramInt2);
    }
  }
  
  /* Error */
  public int setBluetoothA2dpDeviceConnectionState(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: iload_3
    //   1: iconst_2
    //   2: if_icmpeq +37 -> 39
    //   5: iload_3
    //   6: bipush 11
    //   8: if_icmpeq +31 -> 39
    //   11: new 1736	java/lang/IllegalArgumentException
    //   14: dup
    //   15: new 1240	java/lang/StringBuilder
    //   18: dup
    //   19: invokespecial 1241	java/lang/StringBuilder:<init>	()V
    //   22: ldc_w 3009
    //   25: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   28: iload_3
    //   29: invokevirtual 1250	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   32: invokevirtual 1260	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   35: invokespecial 1739	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   38: athrow
    //   39: iload_2
    //   40: iconst_1
    //   41: if_icmpne +14 -> 55
    //   44: ldc -5
    //   46: ldc_w 3011
    //   49: invokestatic 1263	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   52: pop
    //   53: iconst_0
    //   54: ireturn
    //   55: aload_0
    //   56: sipush 128
    //   59: aload_1
    //   60: invokevirtual 2207	android/bluetooth/BluetoothDevice:getAddress	()Ljava/lang/String;
    //   63: invokespecial 843	com/android/server/audio/AudioService:makeDeviceListKey	(ILjava/lang/String;)Ljava/lang/String;
    //   66: astore 9
    //   68: aload_0
    //   69: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   72: aload 9
    //   74: invokevirtual 1882	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   77: checkcast 39	com/android/server/audio/AudioService$DeviceListSpec
    //   80: ifnull +27 -> 107
    //   83: iconst_1
    //   84: istore 4
    //   86: iload 4
    //   88: ifeq +25 -> 113
    //   91: iload_2
    //   92: iconst_2
    //   93: if_icmpne +20 -> 113
    //   96: ldc -5
    //   98: ldc_w 3013
    //   101: invokestatic 1263	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   104: pop
    //   105: iconst_0
    //   106: ireturn
    //   107: iconst_0
    //   108: istore 4
    //   110: goto -24 -> 86
    //   113: iload 4
    //   115: ifne +157 -> 272
    //   118: iload_2
    //   119: iconst_2
    //   120: if_icmpne +152 -> 272
    //   123: ldc -5
    //   125: new 1240	java/lang/StringBuilder
    //   128: dup
    //   129: invokespecial 1241	java/lang/StringBuilder:<init>	()V
    //   132: ldc_w 3015
    //   135: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: aload_1
    //   139: invokevirtual 1887	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   142: invokevirtual 1260	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   145: invokestatic 1263	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   148: pop
    //   149: iconst_0
    //   150: istore 5
    //   152: aload_0
    //   153: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   156: astore 9
    //   158: aload 9
    //   160: monitorenter
    //   161: iconst_0
    //   162: istore 4
    //   164: iload 4
    //   166: aload_0
    //   167: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   170: invokevirtual 1515	android/util/ArrayMap:size	()I
    //   173: if_icmpge +83 -> 256
    //   176: aload_0
    //   177: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   180: iload 4
    //   182: invokevirtual 1519	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   185: checkcast 39	com/android/server/audio/AudioService$DeviceListSpec
    //   188: getfield 1522	com/android/server/audio/AudioService$DeviceListSpec:mDeviceType	I
    //   191: sipush 128
    //   194: if_icmpne +53 -> 247
    //   197: ldc -5
    //   199: ldc_w 3017
    //   202: invokestatic 1263	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   205: pop
    //   206: aload_0
    //   207: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   210: aload_0
    //   211: sipush 128
    //   214: aload_1
    //   215: invokevirtual 2207	android/bluetooth/BluetoothDevice:getAddress	()Ljava/lang/String;
    //   218: invokespecial 843	com/android/server/audio/AudioService:makeDeviceListKey	(ILjava/lang/String;)Ljava/lang/String;
    //   221: new 39	com/android/server/audio/AudioService$DeviceListSpec
    //   224: dup
    //   225: aload_0
    //   226: sipush 128
    //   229: aload_1
    //   230: invokevirtual 2235	android/bluetooth/BluetoothDevice:getName	()Ljava/lang/String;
    //   233: aload_1
    //   234: invokevirtual 2207	android/bluetooth/BluetoothDevice:getAddress	()Ljava/lang/String;
    //   237: invokespecial 1903	com/android/server/audio/AudioService$DeviceListSpec:<init>	(Lcom/android/server/audio/AudioService;ILjava/lang/String;Ljava/lang/String;)V
    //   240: invokevirtual 1907	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   243: pop
    //   244: iconst_1
    //   245: istore 5
    //   247: iload 4
    //   249: iconst_1
    //   250: iadd
    //   251: istore 4
    //   253: goto -89 -> 164
    //   256: aload 9
    //   258: monitorexit
    //   259: iload 5
    //   261: ifeq +223 -> 484
    //   264: iconst_0
    //   265: ireturn
    //   266: astore_1
    //   267: aload 9
    //   269: monitorexit
    //   270: aload_1
    //   271: athrow
    //   272: iload_2
    //   273: ifeq +8 -> 281
    //   276: iload_2
    //   277: iconst_3
    //   278: if_icmpne +206 -> 484
    //   281: ldc -5
    //   283: new 1240	java/lang/StringBuilder
    //   286: dup
    //   287: invokespecial 1241	java/lang/StringBuilder:<init>	()V
    //   290: ldc_w 3019
    //   293: invokevirtual 1247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   296: aload_1
    //   297: invokevirtual 1887	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   300: invokevirtual 1260	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   303: invokestatic 1263	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   306: pop
    //   307: iconst_0
    //   308: istore 6
    //   310: aload_0
    //   311: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   314: astore 9
    //   316: aload 9
    //   318: monitorenter
    //   319: iconst_0
    //   320: istore 5
    //   322: iload 5
    //   324: aload_0
    //   325: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   328: invokevirtual 1515	android/util/ArrayMap:size	()I
    //   331: if_icmpge +51 -> 382
    //   334: aload_0
    //   335: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   338: iload 5
    //   340: invokevirtual 1519	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   343: checkcast 39	com/android/server/audio/AudioService$DeviceListSpec
    //   346: getfield 1522	com/android/server/audio/AudioService$DeviceListSpec:mDeviceType	I
    //   349: istore 8
    //   351: iload 6
    //   353: istore 7
    //   355: iload 8
    //   357: sipush 128
    //   360: if_icmpne +9 -> 369
    //   363: iload 6
    //   365: iconst_1
    //   366: iadd
    //   367: istore 7
    //   369: iload 5
    //   371: iconst_1
    //   372: iadd
    //   373: istore 5
    //   375: iload 7
    //   377: istore 6
    //   379: goto -57 -> 322
    //   382: aload 9
    //   384: monitorexit
    //   385: iload 6
    //   387: iconst_1
    //   388: if_icmple +71 -> 459
    //   391: iload 4
    //   393: ifeq +66 -> 459
    //   396: ldc -5
    //   398: ldc_w 3021
    //   401: invokestatic 1263	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   404: pop
    //   405: aload_0
    //   406: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   409: astore 9
    //   411: aload 9
    //   413: monitorenter
    //   414: aload_0
    //   415: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   418: aload_0
    //   419: sipush 128
    //   422: aload_1
    //   423: invokevirtual 2207	android/bluetooth/BluetoothDevice:getAddress	()Ljava/lang/String;
    //   426: invokespecial 843	com/android/server/audio/AudioService:makeDeviceListKey	(ILjava/lang/String;)Ljava/lang/String;
    //   429: invokevirtual 1895	android/util/ArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   432: pop
    //   433: aload 9
    //   435: monitorexit
    //   436: ldc -5
    //   438: ldc_w 3023
    //   441: invokestatic 1263	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   444: pop
    //   445: iconst_0
    //   446: ireturn
    //   447: astore_1
    //   448: aload 9
    //   450: monitorexit
    //   451: aload_1
    //   452: athrow
    //   453: astore_1
    //   454: aload 9
    //   456: monitorexit
    //   457: aload_1
    //   458: athrow
    //   459: iload 4
    //   461: ifne +23 -> 484
    //   464: ldc -5
    //   466: ldc_w 3025
    //   469: invokestatic 1263	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   472: pop
    //   473: ldc -5
    //   475: ldc_w 3023
    //   478: invokestatic 1263	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   481: pop
    //   482: iconst_0
    //   483: ireturn
    //   484: aload_0
    //   485: getfield 450	com/android/server/audio/AudioService:mConnectedDevices	Landroid/util/ArrayMap;
    //   488: astore 9
    //   490: aload 9
    //   492: monitorenter
    //   493: iload_3
    //   494: iconst_2
    //   495: if_icmpne +60 -> 555
    //   498: iload_2
    //   499: iconst_2
    //   500: if_icmpne +49 -> 549
    //   503: iconst_1
    //   504: istore 4
    //   506: aload_0
    //   507: sipush 128
    //   510: iload 4
    //   512: invokespecial 796	com/android/server/audio/AudioService:checkSendBecomingNoisyIntent	(II)I
    //   515: istore 4
    //   517: aload_0
    //   518: getfield 578	com/android/server/audio/AudioService:mAudioHandler	Lcom/android/server/audio/AudioService$AudioHandler;
    //   521: astore 10
    //   523: iload_3
    //   524: iconst_2
    //   525: if_icmpne +36 -> 561
    //   528: bipush 102
    //   530: istore_3
    //   531: aload_0
    //   532: aload 10
    //   534: iload_3
    //   535: iload_2
    //   536: iconst_0
    //   537: aload_1
    //   538: iload 4
    //   540: invokespecial 745	com/android/server/audio/AudioService:queueMsgUnderWakeLock	(Landroid/os/Handler;IIILjava/lang/Object;I)V
    //   543: aload 9
    //   545: monitorexit
    //   546: iload 4
    //   548: ireturn
    //   549: iconst_0
    //   550: istore 4
    //   552: goto -46 -> 506
    //   555: iconst_0
    //   556: istore 4
    //   558: goto -41 -> 517
    //   561: bipush 101
    //   563: istore_3
    //   564: goto -33 -> 531
    //   567: astore_1
    //   568: aload 9
    //   570: monitorexit
    //   571: aload_1
    //   572: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	573	0	this	AudioService
    //   0	573	1	paramBluetoothDevice	BluetoothDevice
    //   0	573	2	paramInt1	int
    //   0	573	3	paramInt2	int
    //   84	473	4	i	int
    //   150	224	5	j	int
    //   308	81	6	k	int
    //   353	23	7	m	int
    //   349	12	8	n	int
    //   66	503	9	localObject	Object
    //   521	12	10	localAudioHandler	AudioHandler
    // Exception table:
    //   from	to	target	type
    //   164	176	266	finally
    //   176	244	266	finally
    //   322	351	447	finally
    //   414	433	453	finally
    //   506	517	567	finally
    //   517	523	567	finally
    //   531	543	567	finally
  }
  
  public void setBluetoothA2dpOn(boolean paramBoolean)
  {
    int i = 0;
    synchronized (this.mBluetoothA2dpEnabledLock)
    {
      this.mBluetoothA2dpEnabled = paramBoolean;
      AudioHandler localAudioHandler = this.mAudioHandler;
      if (this.mBluetoothA2dpEnabled)
      {
        sendMsg(localAudioHandler, 13, 2, 1, i, null, 0);
        return;
      }
      i = 10;
    }
  }
  
  public void setBluetoothA2dpOnInt(boolean paramBoolean)
  {
    synchronized (this.mBluetoothA2dpEnabledLock)
    {
      this.mBluetoothA2dpEnabled = paramBoolean;
      this.mAudioHandler.removeMessages(13);
      if (this.mBluetoothA2dpEnabled)
      {
        i = 0;
        setForceUseInt_SyncDevices(1, i);
        return;
      }
      int i = 10;
    }
  }
  
  public void setBluetoothCtsScoOn(boolean paramBoolean)
  {
    if (!checkAudioSettingsPermission("setBluetoothScoOn()")) {
      return;
    }
    if (paramBoolean) {
      this.mForcedUseForComm = 3;
    }
    for (;;)
    {
      sendMsg(this.mAudioHandler, 8, 2, 0, this.mForcedUseForComm, null, 0);
      sendMsg(this.mAudioHandler, 8, 2, 2, this.mForcedUseForComm, null, 0);
      return;
      if (this.mForcedUseForComm == 3) {
        this.mForcedUseForComm = 0;
      }
    }
  }
  
  public void setBluetoothScoOn(boolean paramBoolean)
  {
    if (!checkAudioSettingsPermission("setBluetoothScoOn()")) {
      return;
    }
    setBluetoothScoOnInt(paramBoolean);
  }
  
  public void setBluetoothScoOnInt(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if ((this.mBluetoothHeadset != null) && (this.mBluetoothHeadset.getAudioState(this.mBluetoothHeadsetDevice) != 12)) {
        return;
      }
      this.mForcedUseForComm = 3;
    }
    for (;;)
    {
      sendMsg(this.mAudioHandler, 8, 2, 0, this.mForcedUseForComm, null, 0);
      sendMsg(this.mAudioHandler, 8, 2, 2, this.mForcedUseForComm, null, 0);
      return;
      if (this.mForcedUseForComm == 3) {
        this.mForcedUseForComm = 0;
      }
    }
  }
  
  void setBtScoDeviceConnectionState(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    if (paramBluetoothDevice == null) {
      return;
    }
    String str = paramBluetoothDevice.getAddress();
    localObject = paramBluetoothDevice.getBluetoothClass();
    int j = 16;
    int i = j;
    if (localObject != null) {}
    boolean bool1;
    switch (((BluetoothClass)localObject).getDeviceClass())
    {
    default: 
      i = j;
      localObject = str;
      if (!BluetoothAdapter.checkBluetoothAddress(str)) {
        localObject = "";
      }
      if (paramInt == 2)
      {
        bool1 = true;
        label96:
        str = paramBluetoothDevice.getName();
        if (!handleDeviceConnection(bool1, i, (String)localObject, str)) {
          break label188;
        }
      }
      break;
    }
    label188:
    for (boolean bool2 = handleDeviceConnection(bool1, -2147483640, (String)localObject, str);; bool2 = false)
    {
      if (((paramInt != 0) && (paramInt != 3)) || (this.mBluetoothHeadset == null) || (this.mBluetoothHeadset.getAudioState(paramBluetoothDevice) != 12)) {
        break label194;
      }
      Log.w("AudioService", "SCO is there with another device, returning");
      return;
      i = 32;
      break;
      i = 64;
      break;
      bool1 = false;
      break label96;
    }
    label194:
    if (bool2)
    {
      localObject = this.mScoClients;
      if (!bool1) {
        break label222;
      }
    }
    for (;;)
    {
      label222:
      try
      {
        this.mBluetoothHeadsetDevice = paramBluetoothDevice;
        return;
      }
      finally {}
      this.mBluetoothHeadsetDevice = null;
      resetBluetoothSco();
    }
  }
  
  public int setFocusPropertiesForPolicy(int paramInt, IAudioPolicyCallback paramIAudioPolicyCallback)
  {
    boolean bool = true;
    if (DEBUG_AP) {
      Log.d("AudioService", "setFocusPropertiesForPolicy() duck behavior=" + paramInt + " policy " + paramIAudioPolicyCallback.asBinder());
    }
    if (this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0) {}
    for (int i = 1; i == 0; i = 0)
    {
      Slog.w("AudioService", "Cannot change audio policy ducking handling for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid() + ", need MODIFY_AUDIO_ROUTING");
      return -1;
    }
    synchronized (this.mAudioPolicies)
    {
      if (!this.mAudioPolicies.containsKey(paramIAudioPolicyCallback.asBinder()))
      {
        Slog.e("AudioService", "Cannot change audio policy focus properties, unregistered policy");
        return -1;
      }
      paramIAudioPolicyCallback = (AudioPolicyProxy)this.mAudioPolicies.get(paramIAudioPolicyCallback.asBinder());
      if (paramInt == 1)
      {
        Iterator localIterator = this.mAudioPolicies.values().iterator();
        while (localIterator.hasNext()) {
          if (((AudioPolicyProxy)localIterator.next()).mFocusDuckBehavior == 1)
          {
            Slog.e("AudioService", "Cannot change audio policy ducking behavior, already handled");
            return -1;
          }
        }
      }
      paramIAudioPolicyCallback.mFocusDuckBehavior = paramInt;
      paramIAudioPolicyCallback = this.mMediaFocusControl;
      if (paramInt == 1)
      {
        paramIAudioPolicyCallback.setDuckingInExtPolicyAvailable(bool);
        return 0;
      }
      bool = false;
    }
  }
  
  public int setHdmiSystemAudioSupported(boolean paramBoolean)
  {
    int j = 0;
    int i = 0;
    if (this.mHdmiManager != null) {}
    synchronized (this.mHdmiManager)
    {
      if (this.mHdmiTvClient == null)
      {
        Log.w("AudioService", "Only Hdmi-Cec enabled TV device supports system audio mode.");
        return 0;
      }
      synchronized (this.mHdmiTvClient)
      {
        if (this.mHdmiSystemAudioSupported != paramBoolean)
        {
          this.mHdmiSystemAudioSupported = paramBoolean;
          i = j;
          if (paramBoolean) {
            i = 12;
          }
          AudioSystem.setForceUse(5, i);
        }
        i = getDevicesForStream(3);
        return i;
      }
    }
  }
  
  public void setMasterMute(boolean paramBoolean, int paramInt1, String paramString, int paramInt2)
  {
    setMasterMuteInternal(paramBoolean, paramInt1, paramString, Binder.getCallingUid(), paramInt2);
  }
  
  public void setMicrophoneMute(boolean paramBoolean, String paramString, int paramInt)
  {
    int i = 0;
    Log.d("AudioService", "PPD audioservice setMicrophoneMute Mute = " + paramBoolean);
    StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
    int j = arrayOfStackTraceElement.length;
    while (i < j)
    {
      StackTraceElement localStackTraceElement = arrayOfStackTraceElement[i];
      Log.v("AudioService", "Elem: " + localStackTraceElement);
      i += 1;
    }
    j = Binder.getCallingUid();
    i = j;
    if (j == 1000) {
      i = UserHandle.getUid(paramInt, UserHandle.getAppId(j));
    }
    if ((!paramBoolean) && (this.mAppOps.noteOp(44, i, paramString) != 0)) {
      return;
    }
    if (!checkAudioSettingsPermission("setMicrophoneMute()")) {
      return;
    }
    if ((paramInt != UserHandle.getCallingUserId()) && (this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0)) {
      return;
    }
    setMicrophoneMuteNoCallerCheck(paramBoolean, paramInt);
  }
  
  public void setMode(int paramInt, IBinder paramIBinder, String paramString)
  {
    if (DEBUG_MODE) {
      Log.v("AudioService", "setMode(mode=" + paramInt + ", callingPackage=" + paramString + ")");
    }
    if (!checkAudioSettingsPermission("setMode()")) {
      return;
    }
    if ((paramInt == 2) && (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0))
    {
      Log.w("AudioService", "MODIFY_PHONE_STATE Permission Denial: setMode(MODE_IN_CALL) from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    if ((paramInt < -1) || (paramInt >= 4)) {
      return;
    }
    ArrayList localArrayList = this.mSetModeDeathHandlers;
    int i = paramInt;
    if (paramInt == -1) {}
    try
    {
      i = this.mMode;
      paramInt = setModeInt(i, paramIBinder, Binder.getCallingPid(), paramString);
      if (paramInt != 0) {
        disconnectBluetoothSco(paramInt);
      }
      return;
    }
    finally {}
  }
  
  public void setOnePlusFixedRingerMode(boolean paramBoolean)
  {
    this.mOnePlusFixedRingerMode = paramBoolean;
  }
  
  public void setOnePlusRingVolumeRange(int paramInt1, int paramInt2)
  {
    if (DEBUG_VOL) {
      Log.d("AudioService", "[setOnePlusRingVolumeRange] min volume:" + paramInt1 + " max volume " + paramInt2);
    }
    this.mOnePlusMinRingVolumeIndex = paramInt1;
    this.mOnePlusMaxRingVolumeIndex = paramInt2;
  }
  
  public void setRingerModeExternal(int paramInt, String paramString)
  {
    if ((!isAndroidNPlus(paramString)) || (!wouldToggleZenMode(paramInt)) || (this.mNm.isNotificationPolicyAccessGrantedForPackage(paramString)))
    {
      setRingerMode(paramInt, paramString, true);
      return;
    }
    throw new SecurityException("Not allowed to change Do Not Disturb state");
  }
  
  public void setRingerModeInternal(int paramInt, String paramString)
  {
    enforceVolumeController("setRingerModeInternal");
    setRingerMode(paramInt, paramString, false);
  }
  
  public void setRingtonePlayer(IRingtonePlayer paramIRingtonePlayer)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.REMOTE_AUDIO_PLAYBACK", null);
    this.mRingtonePlayer = paramIRingtonePlayer;
  }
  
  public void setSpeakerphoneOn(boolean paramBoolean)
  {
    if (!checkAudioSettingsPermission("setSpeakerphoneOn()")) {
      return;
    }
    if (paramBoolean)
    {
      if (this.mForcedUseForComm == 3) {
        sendMsg(this.mAudioHandler, 8, 2, 2, 0, null, 0);
      }
      this.mForcedUseForComm = 1;
    }
    for (;;)
    {
      sendMsg(this.mAudioHandler, 8, 2, 0, this.mForcedUseForComm, null, 0);
      return;
      if (this.mForcedUseForComm == 1) {
        this.mForcedUseForComm = 0;
      }
    }
  }
  
  public void setStreamVolume(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    setStreamVolume(paramInt1, paramInt2, paramInt3, paramString, paramString, Binder.getCallingUid());
  }
  
  public void setVibrateSetting(int paramInt1, int paramInt2)
  {
    if (!this.mHasVibrator) {
      return;
    }
    this.mVibrateSetting = AudioSystem.getValueForVibrateSetting(this.mVibrateSetting, paramInt1, paramInt2);
    broadcastVibrateSetting(paramInt1);
  }
  
  public void setVolumeController(final IVolumeController paramIVolumeController)
  {
    enforceVolumeController("set the volume controller");
    if (this.mVolumeController.isSameBinder(paramIVolumeController)) {
      return;
    }
    this.mVolumeController.postDismiss();
    if (paramIVolumeController != null) {}
    try
    {
      paramIVolumeController.asBinder().linkToDeath(new IBinder.DeathRecipient()
      {
        public void binderDied()
        {
          if (AudioService.-get42(AudioService.this).isSameBinder(paramIVolumeController))
          {
            Log.w("AudioService", "Current remote volume controller died, unregistering");
            AudioService.this.setVolumeController(null);
          }
        }
      }, 0);
      this.mVolumeController.setController(paramIVolumeController);
      if (DEBUG_VOL) {
        Log.d("AudioService", "Volume controller: " + this.mVolumeController);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public void setVolumePolicy(VolumePolicy paramVolumePolicy)
  {
    enforceVolumeController("set volume policy");
    if ((paramVolumePolicy == null) || (paramVolumePolicy.equals(this.mVolumePolicy))) {}
    do
    {
      return;
      this.mVolumePolicy = paramVolumePolicy;
    } while (!DEBUG_VOL);
    Log.d("AudioService", "Volume policy changed: " + this.mVolumePolicy);
  }
  
  public void setWiredDeviceConnectionState(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3)
  {
    synchronized (this.mConnectedDevices)
    {
      if (DEBUG_DEVICES) {
        Slog.i("AudioService", "setWiredDeviceConnectionState(" + paramInt2 + " nm: " + paramString2 + " addr:" + paramString1 + ")");
      }
      if (paramString2.indexOf("not broadcast") != -1)
      {
        Log.i("AudioService", "setWiredDeviceConnectionState name:" + paramString2);
        i = this.mPreDelay;
        this.mPreDelay = 0;
        queueMsgUnderWakeLock(this.mAudioHandler, 100, 0, 0, new WiredDeviceConnectionState(paramInt1, paramInt2, paramString1, paramString2, paramString3), i);
        return;
      }
      int i = checkSendBecomingNoisyIntent(paramInt1, paramInt2);
      this.mPreDelay = i;
    }
  }
  
  public boolean shouldVibrate(int paramInt)
  {
    if (!this.mHasVibrator) {
      return false;
    }
    switch (getVibrateSetting(paramInt))
    {
    default: 
      return false;
    case 1: 
      return getRingerModeExternal() != 0;
    case 2: 
      return getRingerModeExternal() == 1;
    }
    return false;
  }
  
  public void startBluetoothSco(IBinder paramIBinder, int paramInt)
  {
    if (paramInt < 18) {}
    for (paramInt = 0;; paramInt = -1)
    {
      startBluetoothScoInt(paramIBinder, paramInt);
      return;
    }
  }
  
  void startBluetoothScoInt(IBinder paramIBinder, int paramInt)
  {
    if ((checkAudioSettingsPermission("startBluetoothSco()")) && (this.mSystemReady))
    {
      paramIBinder = getScoClient(paramIBinder, true);
      long l = Binder.clearCallingIdentity();
      paramIBinder.incCount(paramInt);
      Binder.restoreCallingIdentity(l);
      return;
    }
  }
  
  public void startBluetoothScoVirtualCall(IBinder paramIBinder)
  {
    startBluetoothScoInt(paramIBinder, 0);
  }
  
  public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver paramIAudioRoutesObserver)
  {
    synchronized (this.mCurAudioRoutes)
    {
      AudioRoutesInfo localAudioRoutesInfo2 = new AudioRoutesInfo(this.mCurAudioRoutes);
      this.mRoutesObservers.register(paramIAudioRoutesObserver);
      return localAudioRoutesInfo2;
    }
  }
  
  public void stopBluetoothSco(IBinder paramIBinder)
  {
    if ((checkAudioSettingsPermission("stopBluetoothSco()")) && (this.mSystemReady))
    {
      paramIBinder = getScoClient(paramIBinder, false);
      long l = Binder.clearCallingIdentity();
      if (paramIBinder != null) {
        paramIBinder.decCount();
      }
      Binder.restoreCallingIdentity(l);
      return;
    }
  }
  
  public void systemReady()
  {
    sendMsg(this.mAudioHandler, 21, 2, 0, 0, null, 0);
  }
  
  public void threeKeySetStreamVolume(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt1 == 3) && (paramInt4 == 2))
    {
      if (paramInt3 != -100) {
        break label63;
      }
      if (paramInt2 == 0) {
        this.mPerSpeakerMediaVolume = this.mStreamStates[paramInt1].getIndex(paramInt4);
      }
      setStreamVolumeInt(3, 0, 2, false, "ThreeKeySpeakerMediaVolume");
      if (!DEBUG_VOL) {}
    }
    label63:
    do
    {
      Log.d("AudioService", "threeKeySetStreamVolume  set speaker music mute");
      do
      {
        return;
      } while (paramInt3 != 100);
      if (this.mPerSpeakerMediaVolume < 0) {
        return;
      }
      setStreamVolumeInt(3, this.mPerSpeakerMediaVolume, 2, false, "ThreeKeySpeakerMediaVolume");
    } while (!DEBUG_VOL);
    Log.d("AudioService", "threeKeySetStreamVolume  restore speaker music " + this.mPerSpeakerMediaVolume);
  }
  
  public void unloadSoundEffects()
  {
    sendMsg(this.mAudioHandler, 20, 2, 0, 0, null, 0);
  }
  
  public void unregisterAudioFocusClient(String paramString)
  {
    this.mMediaFocusControl.unregisterAudioFocusClient(paramString);
  }
  
  public void unregisterAudioPolicyAsync(IAudioPolicyCallback paramIAudioPolicyCallback)
  {
    if (DEBUG_AP) {
      Log.d("AudioService", "unregisterAudioPolicyAsync for " + paramIAudioPolicyCallback.asBinder());
    }
    synchronized (this.mAudioPolicies)
    {
      AudioPolicyProxy localAudioPolicyProxy = (AudioPolicyProxy)this.mAudioPolicies.remove(paramIAudioPolicyCallback.asBinder());
      if (localAudioPolicyProxy == null)
      {
        Slog.w("AudioService", "Trying to unregister unknown audio policy for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid());
        return;
      }
      paramIAudioPolicyCallback.asBinder().unlinkToDeath(localAudioPolicyProxy, 0);
      localAudioPolicyProxy.release();
      return;
    }
  }
  
  public void unregisterRecordingCallback(IRecordingConfigDispatcher paramIRecordingConfigDispatcher)
  {
    this.mRecordMonitor.unregisterRecordingCallback(paramIRecordingConfigDispatcher);
  }
  
  public void updateRemoteControllerOnExistingMediaPlayers()
  {
    synchronized (mMediaPlayers)
    {
      Log.v("AudioService", "updateRemoteControllerOnExistingMediaPlayers: size of Player list: " + mMediaPlayers.size());
      if (mMediaPlayers.size() > 0)
      {
        Log.v("AudioService", "Inform RemoteController regarding existing RCC entry");
        Iterator localIterator = mMediaPlayers.iterator();
        if (!localIterator.hasNext()) {
          break label181;
        }
        MediaPlayerInfo localMediaPlayerInfo = (MediaPlayerInfo)localIterator.next();
        Intent localIntent = new Intent("org.codeaurora.bluetooth.RCC_CHANGED_ACTION");
        localIntent.putExtra("org.codeaurora.bluetooth.EXTRA_CALLING_PACKAGE_NAME", localMediaPlayerInfo.getPackageName());
        localIntent.putExtra("org.codeaurora.bluetooth.EXTRA_FOCUS_CHANGED_VALUE", localMediaPlayerInfo.isFocussed());
        localIntent.putExtra("org.codeaurora.bluetooth.EXTRA_AVAILABLITY_CHANGED_VALUE", true);
        sendBroadcastToAll(localIntent);
        Log.v("AudioService", "updating RCC change: CallingPackageName:" + localMediaPlayerInfo.getPackageName());
      }
    }
    Log.e("AudioService", "No RCC entry present to update");
    label181:
  }
  
  private class AudioHandler
    extends Handler
  {
    private AudioHandler() {}
    
    private void cleanupPlayer(MediaPlayer paramMediaPlayer)
    {
      if (paramMediaPlayer != null) {}
      try
      {
        paramMediaPlayer.stop();
        paramMediaPlayer.release();
        return;
      }
      catch (IllegalStateException paramMediaPlayer)
      {
        Log.w("AudioService", "MediaPlayer IllegalStateException: " + paramMediaPlayer);
      }
    }
    
    private boolean onLoadSoundEffects()
    {
      synchronized (AudioService.-get32(AudioService.this))
      {
        if (!AudioService.-get39(AudioService.this))
        {
          Log.w("AudioService", "onLoadSoundEffects() called before boot complete");
          return false;
        }
        Object localObject2 = AudioService.-get33(AudioService.this);
        if (localObject2 != null) {
          return true;
        }
        AudioService.-wrap18(AudioService.this);
        AudioService.-set13(AudioService.this, new SoundPool.Builder().setMaxStreams(4).setAudioAttributes(new AudioAttributes.Builder().setUsage(13).setContentType(4).build()).build());
        AudioService.-set14(AudioService.this, null);
        AudioService.-set15(AudioService.this, new AudioService.SoundPoolListenerThread(AudioService.this));
        AudioService.-get35(AudioService.this).start();
        i = 3;
        for (;;)
        {
          localObject2 = AudioService.-get34(AudioService.this);
          if ((localObject2 == null) && (i > 0)) {}
          try
          {
            AudioService.-get32(AudioService.this).wait(5000L);
            i -= 1;
            continue;
            if (AudioService.-get34(AudioService.this) == null)
            {
              Log.w("AudioService", "onLoadSoundEffects() SoundPool listener or thread creation error");
              if (AudioService.-get36(AudioService.this) != null)
              {
                AudioService.-get36(AudioService.this).quit();
                AudioService.-set16(AudioService.this, null);
              }
              AudioService.-set15(AudioService.this, null);
              AudioService.-get33(AudioService.this).release();
              AudioService.-set13(AudioService.this, null);
              return false;
            }
          }
          catch (InterruptedException localInterruptedException1)
          {
            for (;;)
            {
              Log.w("AudioService", "Interrupted while waiting sound pool listener thread.");
            }
          }
        }
      }
      int[] arrayOfInt = new int[AudioService.-get2().size()];
      int i = 0;
      int j;
      while (i < AudioService.-get2().size())
      {
        arrayOfInt[i] = -1;
        i += 1;
        continue;
        if (i < 10)
        {
          if (AudioService.-get3(AudioService.this)[i][1] == 0) {
            break label745;
          }
          if (arrayOfInt[AudioService.-get3(AudioService.this)[i][0]] == -1)
          {
            String str = Environment.getRootDirectory() + "/media/audio/ui/" + (String)AudioService.-get2().get(AudioService.-get3(AudioService.this)[i][0]);
            k = AudioService.-get33(AudioService.this).load(str, 0);
            if (k <= 0)
            {
              Log.w("AudioService", "Soundpool could not load file: " + str);
              break label745;
            }
            AudioService.-get3(AudioService.this)[i][1] = k;
            arrayOfInt[AudioService.-get3(AudioService.this)[i][0]] = k;
            j += 1;
            break label745;
          }
          AudioService.-get3(AudioService.this)[i][1] = arrayOfInt[AudioService.-get3(AudioService.this)[i][0]];
          break label745;
        }
        if (j <= 0) {
          break label759;
        }
        AudioService.-get34(AudioService.this).setSamples(arrayOfInt);
        i = 1;
        j = 3;
        while (i == 1)
        {
          k = i;
          if (j <= 0) {
            break label586;
          }
          try
          {
            AudioService.-get32(AudioService.this).wait(5000L);
            k = AudioService.-get34(AudioService.this).status();
            i = k;
          }
          catch (InterruptedException localInterruptedException2)
          {
            for (;;)
            {
              Log.w("AudioService", "Interrupted while waiting sound pool callback.");
            }
            AudioService.-get33(AudioService.this).release();
            AudioService.-set13(AudioService.this, null);
            return k == 0;
          }
          j -= 1;
        }
      }
      label586:
      label745:
      label759:
      for (int k = i;; k = -1)
      {
        if (AudioService.-get36(AudioService.this) != null)
        {
          AudioService.-get36(AudioService.this).quit();
          AudioService.-set16(AudioService.this, null);
        }
        AudioService.-set15(AudioService.this, null);
        if (k != 0)
        {
          Log.w("AudioService", "onLoadSoundEffects(), Error " + k + " while loading samples");
          i = 0;
        }
        for (;;)
        {
          if (i < 10)
          {
            if (AudioService.-get3(AudioService.this)[i][1] > 0) {
              AudioService.-get3(AudioService.this)[i][1] = -1;
            }
          }
          else
          {
            j = 0;
            i = 0;
            break;
            i += 1;
            break;
          }
          i += 1;
        }
      }
    }
    
    private void onPersistSafeVolumeState(int paramInt)
    {
      Settings.Global.putInt(AudioService.-get15(AudioService.this), "audio_safe_volume_state", paramInt);
    }
    
    private void onPlaySoundEffect(int paramInt1, int paramInt2)
    {
      for (;;)
      {
        Object localObject2;
        float f;
        synchronized (AudioService.-get32(AudioService.this))
        {
          onLoadSoundEffects();
          localObject2 = AudioService.-get33(AudioService.this);
          if (localObject2 == null) {
            return;
          }
          if (paramInt2 < 0)
          {
            f = (float)Math.pow(10.0D, AudioService.-get43() / 20.0F);
            if (AudioService.-get3(AudioService.this)[paramInt1][1] > 0) {
              AudioService.-get33(AudioService.this).play(AudioService.-get3(AudioService.this)[paramInt1][1], f, f, 0, 0, 1.0F);
            }
          }
          else
          {
            f = paramInt2 / 1000.0F;
            continue;
          }
          localObject2 = new MediaPlayer();
        }
        try
        {
          ((MediaPlayer)localObject2).setDataSource(Environment.getRootDirectory() + "/media/audio/ui/" + (String)AudioService.-get2().get(AudioService.-get3(AudioService.this)[paramInt1][0]));
          ((MediaPlayer)localObject2).setAudioStreamType(1);
          ((MediaPlayer)localObject2).prepare();
          ((MediaPlayer)localObject2).setVolume(f);
          ((MediaPlayer)localObject2).setOnCompletionListener(new MediaPlayer.OnCompletionListener()
          {
            public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
            {
              AudioService.AudioHandler.-wrap0(AudioService.AudioHandler.this, paramAnonymousMediaPlayer);
            }
          });
          ((MediaPlayer)localObject2).setOnErrorListener(new MediaPlayer.OnErrorListener()
          {
            public boolean onError(MediaPlayer paramAnonymousMediaPlayer, int paramAnonymousInt1, int paramAnonymousInt2)
            {
              AudioService.AudioHandler.-wrap0(AudioService.AudioHandler.this, paramAnonymousMediaPlayer);
              return true;
            }
          });
          ((MediaPlayer)localObject2).start();
        }
        catch (IOException localIOException)
        {
          Log.w("AudioService", "MediaPlayer IOException: " + localIOException);
          continue;
          localObject3 = finally;
          throw ((Throwable)localObject3);
        }
        catch (IllegalStateException localIllegalStateException)
        {
          Log.w("AudioService", "MediaPlayer IllegalStateException: " + localIllegalStateException);
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          Log.w("AudioService", "MediaPlayer IllegalArgumentException: " + localIllegalArgumentException);
        }
      }
    }
    
    private void onUnloadSoundEffects()
    {
      for (;;)
      {
        synchronized (AudioService.-get32(AudioService.this))
        {
          Object localObject2 = AudioService.-get33(AudioService.this);
          if (localObject2 == null) {
            return;
          }
          localObject2 = new int[AudioService.-get2().size()];
          i = 0;
          if (i >= AudioService.-get2().size()) {
            break label178;
          }
          localObject2[i] = 0;
          i += 1;
          continue;
          if (i < 10)
          {
            if ((AudioService.-get3(AudioService.this)[i][1] <= 0) || (localObject2[AudioService.-get3(AudioService.this)[i][0]] != 0)) {
              break label183;
            }
            AudioService.-get33(AudioService.this).unload(AudioService.-get3(AudioService.this)[i][1]);
            AudioService.-get3(AudioService.this)[i][1] = -1;
            localObject2[AudioService.-get3(AudioService.this)[i][0]] = -1;
          }
        }
        AudioService.-get33(AudioService.this).release();
        AudioService.-set13(AudioService.this, null);
        return;
        label178:
        int i = 0;
        continue;
        label183:
        i += 1;
      }
    }
    
    private void persistRingerMode(int paramInt)
    {
      if (AudioService.-get40(AudioService.this)) {
        return;
      }
      Settings.Global.putInt(AudioService.-get15(AudioService.this), "mode_ringer", paramInt);
    }
    
    private void persistVolume(AudioService.VolumeStreamState paramVolumeStreamState, int paramInt)
    {
      if (AudioService.-get40(AudioService.this)) {
        return;
      }
      if ((AudioService.-wrap2(AudioService.this)) && (AudioService.VolumeStreamState.-get4(paramVolumeStreamState) != 3)) {
        return;
      }
      Settings.System.putIntForUser(AudioService.-get15(AudioService.this), paramVolumeStreamState.getSettingNameForDevice(paramInt), (paramVolumeStreamState.getIndex(paramInt) + 5) / 10, -2);
    }
    
    private void setAllVolumes(AudioService.VolumeStreamState paramVolumeStreamState)
    {
      paramVolumeStreamState.applyAllVolumes();
      int i = AudioSystem.getNumStreamTypes() - 1;
      while (i >= 0)
      {
        if ((i != AudioService.VolumeStreamState.-get4(paramVolumeStreamState)) && (AudioService.-get38(AudioService.this)[i] == AudioService.VolumeStreamState.-get4(paramVolumeStreamState))) {
          AudioService.-get37(AudioService.this)[i].applyAllVolumes();
        }
        i -= 1;
      }
    }
    
    private void setDeviceVolume(AudioService.VolumeStreamState paramVolumeStreamState, int paramInt)
    {
      try
      {
        paramVolumeStreamState.applyDeviceVolume_syncVSS(paramInt);
        int i = AudioSystem.getNumStreamTypes() - 1;
        while (i >= 0)
        {
          if ((i != AudioService.VolumeStreamState.-get4(paramVolumeStreamState)) && (AudioService.-get38(AudioService.this)[i] == AudioService.VolumeStreamState.-get4(paramVolumeStreamState)))
          {
            int j = AudioService.-wrap5(AudioService.this, i);
            if ((paramInt != j) && (AudioService.-get10(AudioService.this)) && ((paramInt & 0x380) != 0)) {
              AudioService.-get37(AudioService.this)[i].applyDeviceVolume_syncVSS(paramInt);
            }
            AudioService.-get37(AudioService.this)[i].applyDeviceVolume_syncVSS(j);
          }
          i -= 1;
        }
        AudioService.-wrap36(AudioService.-get7(AudioService.this), 1, 2, paramInt, 0, paramVolumeStreamState, 500);
        return;
      }
      finally {}
    }
    
    private void setForceUse(int paramInt1, int paramInt2)
    {
      synchronized (AudioService.-get14(AudioService.this))
      {
        AudioService.-wrap38(AudioService.this, paramInt1, paramInt2);
        return;
      }
    }
    
    /* Error */
    public void handleMessage(Message paramMessage)
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore_3
      //   2: iconst_0
      //   3: istore_2
      //   4: aload_1
      //   5: getfield 382	android/os/Message:what	I
      //   8: lookupswitch	default:+252->260, 0:+253->261, 1:+281->289, 3:+297->305, 4:+309->317, 5:+372->380, 6:+385->393, 7:+322->330, 8:+421->429, 9:+434->442, 10:+269->277, 12:+546->554, 13:+421->429, 14:+656->664, 15:+671->679, 16:+679->687, 17:+679->687, 18:+710->718, 19:+719->727, 20:+317->325, 21:+811->819, 22:+827->835, 24:+850->858, 25:+866->874, 26:+819->827, 28:+731->739, 29:+781->789, 30:+789->797, 100:+442->450, 101:+488->496, 102:+517->525
      //   260: return
      //   261: aload_0
      //   262: aload_1
      //   263: getfield 386	android/os/Message:obj	Ljava/lang/Object;
      //   266: checkcast 308	com/android/server/audio/AudioService$VolumeStreamState
      //   269: aload_1
      //   270: getfield 389	android/os/Message:arg1	I
      //   273: invokespecial 391	com/android/server/audio/AudioService$AudioHandler:setDeviceVolume	(Lcom/android/server/audio/AudioService$VolumeStreamState;I)V
      //   276: return
      //   277: aload_0
      //   278: aload_1
      //   279: getfield 386	android/os/Message:obj	Ljava/lang/Object;
      //   282: checkcast 308	com/android/server/audio/AudioService$VolumeStreamState
      //   285: invokespecial 393	com/android/server/audio/AudioService$AudioHandler:setAllVolumes	(Lcom/android/server/audio/AudioService$VolumeStreamState;)V
      //   288: return
      //   289: aload_0
      //   290: aload_1
      //   291: getfield 386	android/os/Message:obj	Ljava/lang/Object;
      //   294: checkcast 308	com/android/server/audio/AudioService$VolumeStreamState
      //   297: aload_1
      //   298: getfield 389	android/os/Message:arg1	I
      //   301: invokespecial 395	com/android/server/audio/AudioService$AudioHandler:persistVolume	(Lcom/android/server/audio/AudioService$VolumeStreamState;I)V
      //   304: return
      //   305: aload_0
      //   306: aload_0
      //   307: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   310: invokevirtual 398	com/android/server/audio/AudioService:getRingerModeInternal	()I
      //   313: invokespecial 400	com/android/server/audio/AudioService$AudioHandler:persistRingerMode	(I)V
      //   316: return
      //   317: aload_0
      //   318: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   321: invokevirtual 403	com/android/server/audio/AudioService:onAudioServerDied	()V
      //   324: return
      //   325: aload_0
      //   326: invokespecial 405	com/android/server/audio/AudioService$AudioHandler:onUnloadSoundEffects	()V
      //   329: return
      //   330: aload_0
      //   331: invokespecial 241	com/android/server/audio/AudioService$AudioHandler:onLoadSoundEffects	()Z
      //   334: istore_3
      //   335: aload_1
      //   336: getfield 386	android/os/Message:obj	Ljava/lang/Object;
      //   339: ifnull -79 -> 260
      //   342: aload_1
      //   343: getfield 386	android/os/Message:obj	Ljava/lang/Object;
      //   346: checkcast 407	com/android/server/audio/AudioService$LoadSoundEffectReply
      //   349: astore_1
      //   350: aload_1
      //   351: monitorenter
      //   352: iload_3
      //   353: ifeq +15 -> 368
      //   356: aload_1
      //   357: iload_2
      //   358: putfield 410	com/android/server/audio/AudioService$LoadSoundEffectReply:mStatus	I
      //   361: aload_1
      //   362: invokevirtual 413	com/android/server/audio/AudioService$LoadSoundEffectReply:notify	()V
      //   365: aload_1
      //   366: monitorexit
      //   367: return
      //   368: iconst_m1
      //   369: istore_2
      //   370: goto -14 -> 356
      //   373: astore 4
      //   375: aload_1
      //   376: monitorexit
      //   377: aload 4
      //   379: athrow
      //   380: aload_0
      //   381: aload_1
      //   382: getfield 389	android/os/Message:arg1	I
      //   385: aload_1
      //   386: getfield 416	android/os/Message:arg2	I
      //   389: invokespecial 418	com/android/server/audio/AudioService$AudioHandler:onPlaySoundEffect	(II)V
      //   392: return
      //   393: aload_0
      //   394: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   397: invokestatic 368	com/android/server/audio/AudioService:-get14	(Lcom/android/server/audio/AudioService;)Landroid/util/ArrayMap;
      //   400: astore 4
      //   402: aload 4
      //   404: monitorenter
      //   405: aload_0
      //   406: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   409: aload_1
      //   410: getfield 386	android/os/Message:obj	Ljava/lang/Object;
      //   413: checkcast 195	java/lang/String
      //   416: invokestatic 422	com/android/server/audio/AudioService:-wrap19	(Lcom/android/server/audio/AudioService;Ljava/lang/String;)V
      //   419: aload 4
      //   421: monitorexit
      //   422: return
      //   423: astore_1
      //   424: aload 4
      //   426: monitorexit
      //   427: aload_1
      //   428: athrow
      //   429: aload_0
      //   430: aload_1
      //   431: getfield 389	android/os/Message:arg1	I
      //   434: aload_1
      //   435: getfield 416	android/os/Message:arg2	I
      //   438: invokespecial 424	com/android/server/audio/AudioService$AudioHandler:setForceUse	(II)V
      //   441: return
      //   442: aload_0
      //   443: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   446: invokestatic 427	com/android/server/audio/AudioService:-wrap33	(Lcom/android/server/audio/AudioService;)V
      //   449: return
      //   450: aload_1
      //   451: getfield 386	android/os/Message:obj	Ljava/lang/Object;
      //   454: checkcast 429	com/android/server/audio/AudioService$WiredDeviceConnectionState
      //   457: astore_1
      //   458: aload_0
      //   459: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   462: aload_1
      //   463: getfield 432	com/android/server/audio/AudioService$WiredDeviceConnectionState:mType	I
      //   466: aload_1
      //   467: getfield 435	com/android/server/audio/AudioService$WiredDeviceConnectionState:mState	I
      //   470: aload_1
      //   471: getfield 439	com/android/server/audio/AudioService$WiredDeviceConnectionState:mAddress	Ljava/lang/String;
      //   474: aload_1
      //   475: getfield 442	com/android/server/audio/AudioService$WiredDeviceConnectionState:mName	Ljava/lang/String;
      //   478: aload_1
      //   479: getfield 445	com/android/server/audio/AudioService$WiredDeviceConnectionState:mCaller	Ljava/lang/String;
      //   482: invokestatic 449	com/android/server/audio/AudioService:-wrap28	(Lcom/android/server/audio/AudioService;IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
      //   485: aload_0
      //   486: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   489: invokestatic 453	com/android/server/audio/AudioService:-get6	(Lcom/android/server/audio/AudioService;)Landroid/os/PowerManager$WakeLock;
      //   492: invokevirtual 456	android/os/PowerManager$WakeLock:release	()V
      //   495: return
      //   496: aload_0
      //   497: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   500: aload_1
      //   501: getfield 386	android/os/Message:obj	Ljava/lang/Object;
      //   504: checkcast 458	android/bluetooth/BluetoothDevice
      //   507: aload_1
      //   508: getfield 389	android/os/Message:arg1	I
      //   511: invokestatic 462	com/android/server/audio/AudioService:-wrap27	(Lcom/android/server/audio/AudioService;Landroid/bluetooth/BluetoothDevice;I)V
      //   514: aload_0
      //   515: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   518: invokestatic 453	com/android/server/audio/AudioService:-get6	(Lcom/android/server/audio/AudioService;)Landroid/os/PowerManager$WakeLock;
      //   521: invokevirtual 456	android/os/PowerManager$WakeLock:release	()V
      //   524: return
      //   525: aload_0
      //   526: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   529: aload_1
      //   530: getfield 386	android/os/Message:obj	Ljava/lang/Object;
      //   533: checkcast 458	android/bluetooth/BluetoothDevice
      //   536: aload_1
      //   537: getfield 389	android/os/Message:arg1	I
      //   540: invokestatic 465	com/android/server/audio/AudioService:-wrap26	(Lcom/android/server/audio/AudioService;Landroid/bluetooth/BluetoothDevice;I)V
      //   543: aload_0
      //   544: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   547: invokestatic 453	com/android/server/audio/AudioService:-get6	(Lcom/android/server/audio/AudioService;)Landroid/os/PowerManager$WakeLock;
      //   550: invokevirtual 456	android/os/PowerManager$WakeLock:release	()V
      //   553: return
      //   554: aload_0
      //   555: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   558: getfield 469	com/android/server/audio/AudioService:mRoutesObservers	Landroid/os/RemoteCallbackList;
      //   561: invokevirtual 474	android/os/RemoteCallbackList:beginBroadcast	()I
      //   564: istore_2
      //   565: iload_2
      //   566: ifle +79 -> 645
      //   569: aload_0
      //   570: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   573: getfield 478	com/android/server/audio/AudioService:mCurAudioRoutes	Landroid/media/AudioRoutesInfo;
      //   576: astore 4
      //   578: aload 4
      //   580: monitorenter
      //   581: new 480	android/media/AudioRoutesInfo
      //   584: dup
      //   585: aload_0
      //   586: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   589: getfield 478	com/android/server/audio/AudioService:mCurAudioRoutes	Landroid/media/AudioRoutesInfo;
      //   592: invokespecial 483	android/media/AudioRoutesInfo:<init>	(Landroid/media/AudioRoutesInfo;)V
      //   595: astore_1
      //   596: aload 4
      //   598: monitorexit
      //   599: iload_2
      //   600: ifle +45 -> 645
      //   603: iload_2
      //   604: iconst_1
      //   605: isub
      //   606: istore_2
      //   607: aload_0
      //   608: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   611: getfield 469	com/android/server/audio/AudioService:mRoutesObservers	Landroid/os/RemoteCallbackList;
      //   614: iload_2
      //   615: invokevirtual 487	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
      //   618: checkcast 489	android/media/IAudioRoutesObserver
      //   621: astore 4
      //   623: aload 4
      //   625: aload_1
      //   626: invokeinterface 492 2 0
      //   631: goto -32 -> 599
      //   634: astore 4
      //   636: goto -37 -> 599
      //   639: astore_1
      //   640: aload 4
      //   642: monitorexit
      //   643: aload_1
      //   644: athrow
      //   645: aload_0
      //   646: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   649: getfield 469	com/android/server/audio/AudioService:mRoutesObservers	Landroid/os/RemoteCallbackList;
      //   652: invokevirtual 495	android/os/RemoteCallbackList:finishBroadcast	()V
      //   655: aload_0
      //   656: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   659: iconst_m1
      //   660: invokestatic 499	com/android/server/audio/AudioService:-wrap20	(Lcom/android/server/audio/AudioService;I)V
      //   663: return
      //   664: aload_0
      //   665: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   668: aload_1
      //   669: getfield 386	android/os/Message:obj	Ljava/lang/Object;
      //   672: checkcast 195	java/lang/String
      //   675: invokestatic 502	com/android/server/audio/AudioService:-wrap22	(Lcom/android/server/audio/AudioService;Ljava/lang/String;)V
      //   678: return
      //   679: aload_0
      //   680: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   683: invokestatic 505	com/android/server/audio/AudioService:-wrap25	(Lcom/android/server/audio/AudioService;)V
      //   686: return
      //   687: aload_0
      //   688: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   691: astore 4
      //   693: aload_1
      //   694: getfield 382	android/os/Message:what	I
      //   697: bipush 17
      //   699: if_icmpne +5 -> 704
      //   702: iconst_1
      //   703: istore_3
      //   704: aload 4
      //   706: iload_3
      //   707: aload_1
      //   708: getfield 386	android/os/Message:obj	Ljava/lang/Object;
      //   711: checkcast 195	java/lang/String
      //   714: invokestatic 509	com/android/server/audio/AudioService:-wrap23	(Lcom/android/server/audio/AudioService;ZLjava/lang/String;)V
      //   717: return
      //   718: aload_0
      //   719: aload_1
      //   720: getfield 389	android/os/Message:arg1	I
      //   723: invokespecial 511	com/android/server/audio/AudioService$AudioHandler:onPersistSafeVolumeState	(I)V
      //   726: return
      //   727: aload_0
      //   728: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   731: aload_1
      //   732: getfield 389	android/os/Message:arg1	I
      //   735: invokestatic 514	com/android/server/audio/AudioService:-wrap21	(Lcom/android/server/audio/AudioService;I)V
      //   738: return
      //   739: getstatic 518	com/android/server/audio/AudioService:DEBUG_VOL	Z
      //   742: ifeq +12 -> 754
      //   745: ldc 42
      //   747: ldc_w 520
      //   750: invokestatic 523	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   753: pop
      //   754: iconst_3
      //   755: iconst_0
      //   756: aload_0
      //   757: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   760: iconst_3
      //   761: invokestatic 352	com/android/server/audio/AudioService:-wrap5	(Lcom/android/server/audio/AudioService;I)I
      //   764: invokestatic 527	android/media/AudioSystem:setStreamVolumeIndex	(III)I
      //   767: pop
      //   768: iconst_1
      //   769: invokestatic 531	android/media/AudioSystem:setMasterMute	(Z)I
      //   772: pop
      //   773: getstatic 518	com/android/server/audio/AudioService:DEBUG_VOL	Z
      //   776: ifeq -516 -> 260
      //   779: ldc 42
      //   781: ldc_w 533
      //   784: invokestatic 523	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   787: pop
      //   788: return
      //   789: aload_0
      //   790: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   793: invokevirtual 536	com/android/server/audio/AudioService:onPlaySilentBuffer	()V
      //   796: return
      //   797: aload_0
      //   798: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   801: invokestatic 225	com/android/server/audio/AudioService:-get15	(Lcom/android/server/audio/AudioService;)Landroid/content/ContentResolver;
      //   804: ldc_w 538
      //   807: aload_0
      //   808: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   811: invokestatic 542	com/android/server/audio/AudioService:-get25	(Lcom/android/server/audio/AudioService;)I
      //   814: invokestatic 233	android/provider/Settings$Global:putInt	(Landroid/content/ContentResolver;Ljava/lang/String;I)Z
      //   817: pop
      //   818: return
      //   819: aload_0
      //   820: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   823: invokevirtual 545	com/android/server/audio/AudioService:onSystemReady	()V
      //   826: return
      //   827: aload_0
      //   828: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   831: invokevirtual 548	com/android/server/audio/AudioService:onIndicateSystemReady	()V
      //   834: return
      //   835: aload_1
      //   836: getfield 389	android/os/Message:arg1	I
      //   839: istore_2
      //   840: aload_0
      //   841: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   844: invokestatic 225	com/android/server/audio/AudioService:-get15	(Lcom/android/server/audio/AudioService;)Landroid/content/ContentResolver;
      //   847: ldc_w 550
      //   850: iload_2
      //   851: bipush -2
      //   853: invokestatic 553	android/provider/Settings$Secure:putIntForUser	(Landroid/content/ContentResolver;Ljava/lang/String;II)Z
      //   856: pop
      //   857: return
      //   858: aload_0
      //   859: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   862: aload_1
      //   863: getfield 389	android/os/Message:arg1	I
      //   866: aload_1
      //   867: getfield 416	android/os/Message:arg2	I
      //   870: invokestatic 556	com/android/server/audio/AudioService:-wrap29	(Lcom/android/server/audio/AudioService;II)V
      //   873: return
      //   874: aload_0
      //   875: getfield 24	com/android/server/audio/AudioService$AudioHandler:this$0	Lcom/android/server/audio/AudioService;
      //   878: aload_1
      //   879: getfield 386	android/os/Message:obj	Ljava/lang/Object;
      //   882: checkcast 195	java/lang/String
      //   885: aload_1
      //   886: getfield 389	android/os/Message:arg1	I
      //   889: invokestatic 560	com/android/server/audio/AudioService:-wrap24	(Lcom/android/server/audio/AudioService;Ljava/lang/String;I)V
      //   892: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	893	0	this	AudioHandler
      //   0	893	1	paramMessage	Message
      //   3	848	2	i	int
      //   1	706	3	bool	boolean
      //   373	5	4	localObject1	Object
      //   634	7	4	localRemoteException	RemoteException
      //   691	14	4	localAudioService	AudioService
      // Exception table:
      //   from	to	target	type
      //   356	365	373	finally
      //   405	419	423	finally
      //   623	631	634	android/os/RemoteException
      //   581	596	639	finally
    }
  }
  
  public class AudioPolicyProxy
    extends AudioPolicyConfig
    implements IBinder.DeathRecipient
  {
    private static final String TAG = "AudioPolicyProxy";
    int mFocusDuckBehavior = 0;
    boolean mHasFocusListener;
    IAudioPolicyCallback mPolicyCallback;
    
    AudioPolicyProxy(AudioPolicyConfig paramAudioPolicyConfig, IAudioPolicyCallback paramIAudioPolicyCallback, boolean paramBoolean)
    {
      super();
      paramAudioPolicyConfig = new StringBuilder().append(paramAudioPolicyConfig.hashCode()).append(":ap:");
      int i = AudioService.-get9(AudioService.this);
      AudioService.-set2(AudioService.this, i + 1);
      setRegistration(new String(i));
      this.mPolicyCallback = paramIAudioPolicyCallback;
      this.mHasFocusListener = paramBoolean;
      if (this.mHasFocusListener) {
        AudioService.-get23(AudioService.this).addFocusFollower(this.mPolicyCallback);
      }
      connectMixes();
    }
    
    public void binderDied()
    {
      synchronized (AudioService.-get8(AudioService.this))
      {
        Log.i("AudioPolicyProxy", "audio policy " + this.mPolicyCallback + " died");
        release();
        AudioService.-get8(AudioService.this).remove(this.mPolicyCallback.asBinder());
        return;
      }
    }
    
    void connectMixes()
    {
      AudioSystem.registerPolicyMixes(this.mMixes, true);
    }
    
    String getRegistrationId()
    {
      return getRegistration();
    }
    
    void release()
    {
      if (this.mFocusDuckBehavior == 1) {
        AudioService.-get23(AudioService.this).setDuckingInExtPolicyAvailable(false);
      }
      if (this.mHasFocusListener) {
        AudioService.-get23(AudioService.this).removeFocusFollower(this.mPolicyCallback);
      }
      AudioSystem.registerPolicyMixes(this.mMixes, false);
    }
  }
  
  private class AudioServiceBroadcastReceiver
    extends BroadcastReceiver
  {
    private AudioServiceBroadcastReceiver() {}
    
    public void onReceive(Context arg1, Intent paramIntent)
    {
      String str = paramIntent.getAction();
      int j;
      int i;
      if (str.equals("android.intent.action.DOCK_EVENT"))
      {
        j = paramIntent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
        switch (j)
        {
        default: 
          i = 0;
          if ((j != 3) && ((j != 0) || (AudioService.-get18(AudioService.this) != 3))) {
            AudioSystem.setForceUse(3, i);
          }
          AudioService.-set6(AudioService.this, j);
        }
      }
      label798:
      label854:
      do
      {
        do
        {
          do
          {
            return;
            i = 7;
            break;
            i = 6;
            break;
            i = 8;
            break;
            i = 9;
            break;
            if (str.equals("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED"))
            {
              i = paramIntent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
              ??? = (BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
              Log.d("AudioService", "Bt device " + ???.getAddress() + "disconnection intent received");
              AudioService.this.setBtScoDeviceConnectionState(???, i);
              return;
            }
            if (str.equals("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED"))
            {
              int k = 0;
              j = -1;
              for (;;)
              {
                synchronized (AudioService.-get29(AudioService.this))
                {
                  int m = paramIntent.getIntExtra("android.bluetooth.profile.extra.STATE", -1);
                  i = k;
                  if (!AudioService.-get29(AudioService.this).isEmpty())
                  {
                    if (AudioService.-get28(AudioService.this) != 3)
                    {
                      i = AudioService.-get28(AudioService.this);
                      if (i != 1) {
                        continue;
                      }
                    }
                    i = 1;
                  }
                  switch (m)
                  {
                  default: 
                    k = 0;
                    if (k == 0) {
                      break;
                    }
                    AudioService.-wrap12(AudioService.this, j);
                    ??? = new Intent("android.media.SCO_AUDIO_STATE_CHANGED");
                    ???.putExtra("android.media.extra.SCO_AUDIO_STATE", j);
                    AudioService.-wrap37(AudioService.this, ???);
                    return;
                    i = k;
                    if (AudioService.-get28(AudioService.this) != 5) {
                      continue;
                    }
                    break;
                  case 12: 
                    m = 1;
                    k = i;
                    j = m;
                    if (AudioService.-get28(AudioService.this) == 3) {
                      continue;
                    }
                    k = i;
                    j = m;
                    if (AudioService.-get28(AudioService.this) == 5) {
                      continue;
                    }
                    k = i;
                    j = m;
                    if (AudioService.-get28(AudioService.this) == 4) {
                      continue;
                    }
                    AudioService.-set12(AudioService.this, 2);
                    k = i;
                    j = m;
                  }
                }
                j = 0;
                AudioService.-set12(AudioService.this, 0);
                AudioService.this.clearAllScoClients(0, false);
                k = i;
                continue;
                if ((AudioService.-get28(AudioService.this) != 3) && (AudioService.-get28(AudioService.this) != 5) && (AudioService.-get28(AudioService.this) != 4)) {
                  AudioService.-set12(AudioService.this, 2);
                }
              }
            }
            if (str.equals("android.intent.action.SCREEN_ON"))
            {
              if (AudioService.-get24(AudioService.this)) {
                RotationHelper.enable();
              }
              AudioSystem.setParameters("screen_state=on");
              return;
            }
            if (str.equals("android.intent.action.SCREEN_OFF"))
            {
              if (AudioService.-get24(AudioService.this)) {
                RotationHelper.disable();
              }
              AudioSystem.setParameters("screen_state=off");
              return;
            }
            if (str.equals("android.intent.action.CONFIGURATION_CHANGED"))
            {
              AudioService.-wrap16(AudioService.this, ???);
              return;
            }
            if (str.equals("android.intent.action.USER_SWITCHED"))
            {
              if (AudioService.-get41(AudioService.this)) {
                AudioService.-wrap36(AudioService.-get7(AudioService.this), 15, 0, 0, 0, null, 0);
              }
              AudioService.-set17(AudioService.this, true);
              AudioService.-get23(AudioService.this).discardAudioFocusOwner();
              AudioService.-wrap31(AudioService.this, true);
              AudioService.-wrap36(AudioService.-get7(AudioService.this), 10, 2, 0, 0, AudioService.-get37(AudioService.this)[3], 0);
              return;
            }
            if (str.equals("android.intent.action.USER_BACKGROUND"))
            {
              i = paramIntent.getIntExtra("android.intent.extra.user_handle", -1);
              if (i >= 0)
              {
                ??? = UserManagerService.getInstance().getUserInfo(i);
                AudioService.-wrap17(AudioService.this, ???);
              }
              UserManagerService.getInstance().setUserRestriction("no_record_audio", true, i);
              return;
            }
            if (str.equals("android.intent.action.USER_FOREGROUND"))
            {
              i = paramIntent.getIntExtra("android.intent.extra.user_handle", -1);
              UserManagerService.getInstance().setUserRestriction("no_record_audio", false, i);
              return;
            }
            if (!str.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
              break label798;
            }
            i = paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1);
          } while ((i != 10) && (i != 13));
          AudioService.this.disconnectAllBluetoothProfiles();
          return;
          if (!str.equals("com.oem.intent.action.ACTION_SHUTDOWN_MUTE_MUSIC")) {
            break label854;
          }
          if (AudioService.DEBUG_VOL) {
            Log.d("AudioService", "ACTION_SHUTDOWN_MUTE_MUSIC Intent received");
          }
          AudioService.-wrap36(AudioService.-get7(AudioService.this), 28, 0, 0, 0, null, 0);
        } while (!AudioService.DEBUG_VOL);
        Log.d("AudioService", "ACTION_SHUTDOWN_MUTE_MUSIC Intent received returned");
        return;
        if (str.equals("android.intent.action.ACTION_SHUTDOWN"))
        {
          AudioSystem.setParameters("dev_shutdown=true");
          AudioService.-wrap36(AudioService.-get7(AudioService.this), 30, 0, 0, 0, null, 0);
          return;
        }
      } while (!str.equals("android.intent.action.BOOT_COMPLETED"));
      AudioService.-set5(AudioService.this, true);
      AudioService.-wrap36(AudioService.-get7(AudioService.this), 29, 0, 0, 0, null, 0);
    }
  }
  
  final class AudioServiceInternal
    extends AudioManagerInternal
  {
    AudioServiceInternal() {}
    
    public void adjustStreamVolumeForUid(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4)
    {
      AudioService.-wrap10(AudioService.this, paramInt1, paramInt2, paramInt3, paramString, paramString, paramInt4);
    }
    
    public void adjustSuggestedStreamVolumeForUid(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4)
    {
      AudioService.-wrap11(AudioService.this, paramInt2, paramInt1, paramInt3, paramString, paramString, paramInt4);
    }
    
    public int getRingerModeInternal()
    {
      return AudioService.this.getRingerModeInternal();
    }
    
    public int getVolumeControllerUid()
    {
      return AudioService.ControllerService.-get0(AudioService.-get17(AudioService.this));
    }
    
    public void setRingerModeDelegate(AudioManagerInternal.RingerModeDelegate paramRingerModeDelegate)
    {
      AudioService.-set10(AudioService.this, paramRingerModeDelegate);
      if (AudioService.-get26(AudioService.this) != null)
      {
        AudioService.-wrap3(AudioService.this);
        setRingerModeInternal(getRingerModeInternal(), "AudioService.setRingerModeDelegate");
      }
    }
    
    public void setRingerModeInternal(int paramInt, String paramString)
    {
      AudioService.this.setRingerModeInternal(paramInt, paramString);
    }
    
    public void setStreamVolumeForUid(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4)
    {
      AudioService.-wrap42(AudioService.this, paramInt1, paramInt2, paramInt3, paramString, paramString, paramInt4);
    }
    
    public void updateRingerModeAffectedStreamsInternal()
    {
      synchronized (AudioService.-get31(AudioService.this))
      {
        if (AudioService.-wrap3(AudioService.this)) {
          AudioService.-wrap41(AudioService.this, getRingerModeInternal(), false);
        }
        return;
      }
    }
  }
  
  private class AudioServiceUserRestrictionsListener
    implements UserManagerInternal.UserRestrictionsListener
  {
    private AudioServiceUserRestrictionsListener() {}
    
    public void onUserRestrictionsChanged(int paramInt, Bundle paramBundle1, Bundle paramBundle2)
    {
      boolean bool1 = paramBundle2.getBoolean("no_unmute_microphone");
      boolean bool2 = paramBundle1.getBoolean("no_unmute_microphone");
      if (bool1 != bool2) {
        AudioService.-wrap40(AudioService.this, bool2, paramInt);
      }
      if (!paramBundle2.getBoolean("no_adjust_volume"))
      {
        bool1 = paramBundle2.getBoolean("disallow_unmute_device");
        if (paramBundle1.getBoolean("no_adjust_volume")) {
          break label92;
        }
      }
      label92:
      for (bool2 = paramBundle1.getBoolean("disallow_unmute_device");; bool2 = true)
      {
        if (bool1 != bool2) {
          AudioService.-wrap39(AudioService.this, bool2, 0, paramInt);
        }
        return;
        bool1 = true;
        break;
      }
    }
  }
  
  private class AudioSystemThread
    extends Thread
  {
    AudioSystemThread()
    {
      super();
    }
    
    public void run()
    {
      
      synchronized (AudioService.this)
      {
        AudioService.-set1(AudioService.this, new AudioService.AudioHandler(AudioService.this, null));
        AudioService.this.notify();
        Looper.loop();
        return;
      }
    }
  }
  
  private class ControllerService
    extends ContentObserver
  {
    private ComponentName mComponent;
    private int mUid;
    
    public ControllerService()
    {
      super();
    }
    
    public void init()
    {
      onChange(true);
      AudioService.-get15(AudioService.this).registerContentObserver(Settings.Secure.getUriFor("volume_controller_service_component"), false, this);
    }
    
    public void onChange(boolean paramBoolean)
    {
      this.mUid = 0;
      this.mComponent = null;
      String str = Settings.Secure.getString(AudioService.-get15(AudioService.this), "volume_controller_service_component");
      if (str == null) {
        return;
      }
      try
      {
        this.mComponent = ComponentName.unflattenFromString(str);
        if (this.mComponent == null) {
          return;
        }
        this.mUid = AudioService.-get16(AudioService.this).getPackageManager().getApplicationInfo(this.mComponent.getPackageName(), 0).uid;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Log.w("AudioService", "Error loading controller service", localException);
        }
      }
      if (AudioService.DEBUG_VOL) {
        Log.d("AudioService", "Reloaded controller service: " + this);
      }
    }
    
    public String toString()
    {
      return String.format("{mUid=%d,mComponent=%s}", new Object[] { Integer.valueOf(this.mUid), this.mComponent });
    }
  }
  
  private class DeviceListSpec
  {
    String mDeviceAddress;
    String mDeviceName;
    int mDeviceType;
    
    public DeviceListSpec(int paramInt, String paramString1, String paramString2)
    {
      this.mDeviceType = paramInt;
      this.mDeviceName = paramString1;
      this.mDeviceAddress = paramString2;
    }
    
    public String toString()
    {
      return "[type:0x" + Integer.toHexString(this.mDeviceType) + " name:" + this.mDeviceName + " address:" + this.mDeviceAddress + "]";
    }
  }
  
  private class ForceControlStreamClient
    implements IBinder.DeathRecipient
  {
    private IBinder mCb;
    
    ForceControlStreamClient(IBinder paramIBinder)
    {
      this$1 = paramIBinder;
      if (paramIBinder != null) {}
      try
      {
        paramIBinder.linkToDeath(this, 0);
        this$1 = paramIBinder;
      }
      catch (RemoteException this$1)
      {
        for (;;)
        {
          Log.w("AudioService", "ForceControlStreamClient() could not link to " + paramIBinder + " binder death");
          this$1 = null;
        }
      }
      this.mCb = AudioService.this;
    }
    
    public void binderDied()
    {
      synchronized (AudioService.-get20(AudioService.this))
      {
        Log.w("AudioService", "SCO client died");
        if (AudioService.-get19(AudioService.this) != this)
        {
          Log.w("AudioService", "unregistered control stream client died");
          return;
        }
        AudioService.-set7(AudioService.this, null);
        AudioService.-set18(AudioService.this, -1);
      }
    }
    
    public void release()
    {
      if (this.mCb != null)
      {
        this.mCb.unlinkToDeath(this, 0);
        this.mCb = null;
      }
    }
  }
  
  public static final class Lifecycle
    extends SystemService
  {
    private AudioService mService;
    
    public Lifecycle(Context paramContext)
    {
      super();
      this.mService = new AudioService(paramContext);
    }
    
    public void onBootPhase(int paramInt)
    {
      if (paramInt == 550) {
        this.mService.systemReady();
      }
    }
    
    public void onStart()
    {
      publishBinderService("audio", this.mService);
    }
  }
  
  class LoadSoundEffectReply
  {
    public int mStatus = 1;
    
    LoadSoundEffectReply() {}
  }
  
  private class MediaPlayerInfo
  {
    private boolean mIsfocussed;
    private String mPackageName;
    
    public MediaPlayerInfo(String paramString, boolean paramBoolean)
    {
      this.mPackageName = paramString;
      this.mIsfocussed = paramBoolean;
    }
    
    public String getPackageName()
    {
      return this.mPackageName;
    }
    
    public boolean isFocussed()
    {
      return this.mIsfocussed;
    }
    
    public void setFocus(boolean paramBoolean)
    {
      this.mIsfocussed = paramBoolean;
    }
  }
  
  private class MyDisplayStatusCallback
    implements HdmiPlaybackClient.DisplayStatusCallback
  {
    private MyDisplayStatusCallback() {}
    
    public void onComplete(int paramInt)
    {
      if (AudioService.-get22(AudioService.this) != null) {}
      synchronized (AudioService.-get22(AudioService.this))
      {
        AudioService localAudioService = AudioService.this;
        if (paramInt != -1) {}
        for (boolean bool = true;; bool = false)
        {
          AudioService.-set8(localAudioService, bool);
          if ((AudioService.-wrap2(AudioService.this)) && (!AudioService.-get21(AudioService.this))) {
            break;
          }
          AudioService.-wrap13(AudioService.this);
          return;
        }
        localAudioService = AudioService.this;
        localAudioService.mFixedVolumeDevices &= 0xFBFF;
      }
    }
  }
  
  private class RmtSbmxFullVolDeathHandler
    implements IBinder.DeathRecipient
  {
    private IBinder mICallback;
    
    RmtSbmxFullVolDeathHandler(IBinder paramIBinder)
    {
      this.mICallback = paramIBinder;
      try
      {
        paramIBinder.linkToDeath(this, 0);
        return;
      }
      catch (RemoteException this$1)
      {
        Log.e("AudioService", "can't link to death", AudioService.this);
      }
    }
    
    public void binderDied()
    {
      Log.w("AudioService", "Recorder with remote submix at full volume died " + this.mICallback);
      AudioService.this.forceRemoteSubmixFullVolume(false, this.mICallback);
    }
    
    void forget()
    {
      try
      {
        this.mICallback.unlinkToDeath(this, 0);
        return;
      }
      catch (NoSuchElementException localNoSuchElementException)
      {
        Log.e("AudioService", "error unlinking to death", localNoSuchElementException);
      }
    }
    
    boolean isHandlerFor(IBinder paramIBinder)
    {
      return this.mICallback.equals(paramIBinder);
    }
  }
  
  private class ScoClient
    implements IBinder.DeathRecipient
  {
    private IBinder mCb;
    private int mCreatorPid;
    private int mStartcount;
    
    ScoClient(IBinder paramIBinder)
    {
      this.mCb = paramIBinder;
      this.mCreatorPid = Binder.getCallingPid();
      this.mStartcount = 0;
    }
    
    private void requestScoState(int paramInt1, int paramInt2)
    {
      AudioService.-wrap14(AudioService.this);
      if (totalCount() == 0)
      {
        if (paramInt1 != 12) {
          break label427;
        }
        AudioService.-wrap12(AudioService.this, 2);
      }
      label363:
      label374:
      label396:
      label416:
      label427:
      do
      {
        for (;;)
        {
          boolean bool;
          synchronized (AudioService.-get30(AudioService.this))
          {
            if (((!AudioService.-get30(AudioService.this).isEmpty()) && (((AudioService.SetModeDeathHandler)AudioService.-get30(AudioService.this).get(0)).getPid() != this.mCreatorPid)) || ((AudioService.-get28(AudioService.this) != 0) && (AudioService.-get28(AudioService.this) != 5))) {
              break label416;
            }
            if (AudioService.-get28(AudioService.this) != 0) {
              break label396;
            }
            AudioService.-set11(AudioService.this, paramInt2);
            if (paramInt2 == -1)
            {
              if (AudioService.-get12(AudioService.this) == null) {
                continue;
              }
              AudioService.-set11(AudioService.this, new Integer(Settings.Global.getInt(AudioService.-get15(AudioService.this), "bluetooth_sco_channel_" + AudioService.-get12(AudioService.this).getAddress(), 0)).intValue());
              if ((AudioService.-get27(AudioService.this) > 2) || (AudioService.-get27(AudioService.this) < 0)) {
                AudioService.-set11(AudioService.this, 0);
              }
            }
            if ((AudioService.-get11(AudioService.this) == null) || (AudioService.-get12(AudioService.this) == null)) {
              break label374;
            }
            bool = false;
            if (AudioService.-get27(AudioService.this) == 1)
            {
              bool = AudioService.-get11(AudioService.this).connectAudio();
              if (!bool) {
                break label363;
              }
              AudioService.-set12(AudioService.this, 3);
              return;
              AudioService.-set11(AudioService.this, 1);
            }
          }
          if (AudioService.-get27(AudioService.this) == 0)
          {
            bool = AudioService.-get11(AudioService.this).startScoUsingVirtualVoiceCall(AudioService.-get12(AudioService.this));
          }
          else if (AudioService.-get27(AudioService.this) == 2)
          {
            bool = AudioService.-get11(AudioService.this).startVoiceRecognition(AudioService.-get12(AudioService.this));
            continue;
            AudioService.-wrap12(AudioService.this, 0);
            continue;
            if (AudioService.-wrap0(AudioService.this))
            {
              AudioService.-set12(AudioService.this, 1);
              continue;
              AudioService.-set12(AudioService.this, 3);
              AudioService.-wrap12(AudioService.this, 1);
              continue;
              AudioService.-wrap12(AudioService.this, 0);
              continue;
              if ((paramInt1 == 10) && ((AudioService.-get28(AudioService.this) == 3) || (AudioService.-get28(AudioService.this) == 1)))
              {
                if (AudioService.-get28(AudioService.this) != 3) {
                  break label615;
                }
                if ((AudioService.-get11(AudioService.this) == null) || (AudioService.-get12(AudioService.this) == null)) {
                  break;
                }
                bool = false;
                if (AudioService.-get27(AudioService.this) == 1) {
                  bool = AudioService.-get11(AudioService.this).disconnectAudio();
                }
                while (!bool)
                {
                  AudioService.-set12(AudioService.this, 0);
                  AudioService.-wrap12(AudioService.this, 0);
                  return;
                  if (AudioService.-get27(AudioService.this) == 0) {
                    bool = AudioService.-get11(AudioService.this).stopScoUsingVirtualVoiceCall(AudioService.-get12(AudioService.this));
                  } else if (AudioService.-get27(AudioService.this) == 2) {
                    bool = AudioService.-get11(AudioService.this).stopVoiceRecognition(AudioService.-get12(AudioService.this));
                  }
                }
              }
            }
          }
        }
      } while (!AudioService.-wrap0(AudioService.this));
      AudioService.-set12(AudioService.this, 5);
      return;
      label615:
      AudioService.-set12(AudioService.this, 0);
      AudioService.-wrap12(AudioService.this, 0);
    }
    
    public void binderDied()
    {
      synchronized (AudioService.-get29(AudioService.this))
      {
        Log.w("AudioService", "SCO client died");
        if (AudioService.-get29(AudioService.this).indexOf(this) < 0)
        {
          Log.w("AudioService", "unregistered SCO client died");
          return;
        }
        clearCount(true);
        AudioService.-get29(AudioService.this).remove(this);
      }
    }
    
    public void clearCount(boolean paramBoolean)
    {
      synchronized (AudioService.-get29(AudioService.this))
      {
        int i = this.mStartcount;
        if (i != 0) {}
        try
        {
          this.mCb.unlinkToDeath(this, 0);
          this.mStartcount = 0;
          if (paramBoolean) {
            requestScoState(10, 0);
          }
          return;
        }
        catch (NoSuchElementException localNoSuchElementException)
        {
          for (;;)
          {
            Log.w("AudioService", "clearCount() mStartcount: " + this.mStartcount + " != 0 but not registered to binder");
          }
        }
      }
    }
    
    public void decCount()
    {
      for (;;)
      {
        synchronized (AudioService.-get29(AudioService.this))
        {
          if (this.mStartcount == 0)
          {
            Log.w("AudioService", "ScoClient.decCount() already 0");
            return;
          }
          this.mStartcount -= 1;
          int i = this.mStartcount;
          if (i != 0) {}
        }
        try
        {
          this.mCb.unlinkToDeath(this, 0);
          requestScoState(10, 0);
          continue;
          localObject = finally;
          throw ((Throwable)localObject);
        }
        catch (NoSuchElementException localNoSuchElementException)
        {
          for (;;)
          {
            Log.w("AudioService", "decCount() going to 0 but not registered to binder");
          }
        }
      }
    }
    
    public IBinder getBinder()
    {
      return this.mCb;
    }
    
    public int getCount()
    {
      return this.mStartcount;
    }
    
    public int getPid()
    {
      return this.mCreatorPid;
    }
    
    public void incCount(int paramInt)
    {
      synchronized (AudioService.-get29(AudioService.this))
      {
        requestScoState(12, paramInt);
        paramInt = this.mStartcount;
        if (paramInt == 0) {}
        try
        {
          this.mCb.linkToDeath(this, 0);
          this.mStartcount += 1;
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Log.w("AudioService", "ScoClient  incCount() could not link to " + this.mCb + " binder death");
          }
        }
      }
    }
    
    public int totalCount()
    {
      ArrayList localArrayList = AudioService.-get29(AudioService.this);
      int j = 0;
      try
      {
        int k = AudioService.-get29(AudioService.this).size();
        int i = 0;
        while (i < k)
        {
          int m = ((ScoClient)AudioService.-get29(AudioService.this).get(i)).getCount();
          j += m;
          i += 1;
        }
        return j;
      }
      finally {}
    }
  }
  
  private class SetModeDeathHandler
    implements IBinder.DeathRecipient
  {
    private IBinder mCb;
    private int mMode = 0;
    private int mPid;
    
    SetModeDeathHandler(IBinder paramIBinder, int paramInt)
    {
      this.mCb = paramIBinder;
      this.mPid = paramInt;
    }
    
    public void binderDied()
    {
      int i = 0;
      synchronized (AudioService.-get30(AudioService.this))
      {
        Log.w("AudioService", "setMode() client died");
        if (AudioService.-get30(AudioService.this).indexOf(this) < 0)
        {
          Log.w("AudioService", "unregistered setMode() client died");
          if (i != 0)
          {
            long l = Binder.clearCallingIdentity();
            AudioService.-wrap15(AudioService.this, i);
            Binder.restoreCallingIdentity(l);
          }
          return;
        }
        i = AudioService.-wrap8(AudioService.this, 0, this.mCb, this.mPid, "AudioService");
      }
    }
    
    public IBinder getBinder()
    {
      return this.mCb;
    }
    
    public int getMode()
    {
      return this.mMode;
    }
    
    public int getPid()
    {
      return this.mPid;
    }
    
    public void setMode(int paramInt)
    {
      this.mMode = paramInt;
    }
  }
  
  private class SettingsObserver
    extends ContentObserver
  {
    private int mEncodedSurroundMode;
    
    SettingsObserver()
    {
      super();
      AudioService.-get15(AudioService.this).registerContentObserver(Settings.System.getUriFor("mode_ringer_streams_affected"), false, this);
      AudioService.-get15(AudioService.this).registerContentObserver(Settings.Global.getUriFor("dock_audio_media_enabled"), false, this);
      AudioService.-get15(AudioService.this).registerContentObserver(Settings.System.getUriFor("master_mono"), false, this);
      this.mEncodedSurroundMode = Settings.Global.getInt(AudioService.-get15(AudioService.this), "encoded_surround_output", 0);
      AudioService.-get15(AudioService.this).registerContentObserver(Settings.Global.getUriFor("encoded_surround_output"), false, this);
    }
    
    private void updateEncodedSurroundOutput()
    {
      int i = Settings.Global.getInt(AudioService.-get15(AudioService.this), "encoded_surround_output", 0);
      if (this.mEncodedSurroundMode != i) {
        AudioService.-wrap35(AudioService.this, i);
      }
      synchronized (AudioService.-get14(AudioService.this))
      {
        String str = AudioService.-wrap9(AudioService.this, 1024, "");
        if ((AudioService.DeviceListSpec)AudioService.-get14(AudioService.this).get(str) != null)
        {
          AudioService.this.setWiredDeviceConnectionState(1024, 0, "", "", "android");
          AudioService.this.setWiredDeviceConnectionState(1024, 1, "", "", "android");
        }
        this.mEncodedSurroundMode = i;
        return;
      }
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      synchronized (AudioService.-get31(AudioService.this))
      {
        if (AudioService.-wrap3(AudioService.this)) {
          AudioService.-wrap41(AudioService.this, AudioService.this.getRingerModeInternal(), false);
        }
        AudioService.-wrap32(AudioService.this, AudioService.-get15(AudioService.this));
        AudioService.-wrap43(AudioService.this, AudioService.-get15(AudioService.this));
        updateEncodedSurroundOutput();
        return;
      }
    }
  }
  
  private final class SoundPoolCallback
    implements SoundPool.OnLoadCompleteListener
  {
    List<Integer> mSamples = new ArrayList();
    int mStatus = 1;
    
    private SoundPoolCallback() {}
    
    public void onLoadComplete(SoundPool arg1, int paramInt1, int paramInt2)
    {
      synchronized (AudioService.-get32(AudioService.this))
      {
        paramInt1 = this.mSamples.indexOf(Integer.valueOf(paramInt1));
        if (paramInt1 >= 0) {
          this.mSamples.remove(paramInt1);
        }
        if ((paramInt2 != 0) || (this.mSamples.isEmpty()))
        {
          this.mStatus = paramInt2;
          AudioService.-get32(AudioService.this).notify();
        }
        return;
      }
    }
    
    public void setSamples(int[] paramArrayOfInt)
    {
      int i = 0;
      while (i < paramArrayOfInt.length)
      {
        if (paramArrayOfInt[i] > 0) {
          this.mSamples.add(Integer.valueOf(paramArrayOfInt[i]));
        }
        i += 1;
      }
    }
    
    public int status()
    {
      return this.mStatus;
    }
  }
  
  class SoundPoolListenerThread
    extends Thread
  {
    public SoundPoolListenerThread()
    {
      super();
    }
    
    public void run()
    {
      Looper.prepare();
      AudioService.-set16(AudioService.this, Looper.myLooper());
      synchronized (AudioService.-get32(AudioService.this))
      {
        if (AudioService.-get33(AudioService.this) != null)
        {
          AudioService.-set14(AudioService.this, new AudioService.SoundPoolCallback(AudioService.this, null));
          AudioService.-get33(AudioService.this).setOnLoadCompleteListener(AudioService.-get34(AudioService.this));
        }
        AudioService.-get32(AudioService.this).notify();
        Looper.loop();
        return;
      }
    }
  }
  
  private static class StreamOverride
    implements AccessibilityManager.TouchExplorationStateChangeListener
  {
    private static final int DEFAULT_STREAM_TYPE_OVERRIDE_DELAY_MS = 0;
    private static final int TOUCH_EXPLORE_STREAM_TYPE_OVERRIDE_DELAY_MS = 1000;
    static int sDelayMs;
    
    static void init(Context paramContext)
    {
      paramContext = (AccessibilityManager)paramContext.getSystemService("accessibility");
      updateDefaultStreamOverrideDelay(paramContext.isTouchExplorationEnabled());
      paramContext.addTouchExplorationStateChangeListener(new StreamOverride());
    }
    
    private static void updateDefaultStreamOverrideDelay(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (sDelayMs = 1000;; sDelayMs = 0)
      {
        if (AudioService.DEBUG_VOL) {
          Log.d("AudioService", "Touch exploration enabled=" + paramBoolean + " stream override delay is now " + sDelayMs + " ms");
        }
        return;
      }
    }
    
    public void onTouchExplorationStateChanged(boolean paramBoolean)
    {
      updateDefaultStreamOverrideDelay(paramBoolean);
    }
  }
  
  class StreamVolumeCommand
  {
    public final int mDevice;
    public final int mFlags;
    public final int mIndex;
    public final int mStreamType;
    
    StreamVolumeCommand(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.mStreamType = paramInt1;
      this.mIndex = paramInt2;
      this.mFlags = paramInt3;
      this.mDevice = paramInt4;
    }
    
    public String toString()
    {
      return "{streamType=" + this.mStreamType + ",index=" + this.mIndex + ",flags=" + this.mFlags + ",device=" + this.mDevice + '}';
    }
  }
  
  public static class VolumeController
  {
    private static final String TAG = "VolumeController";
    private IVolumeController mController;
    private int mLongPressTimeout;
    private long mNextLongPress;
    private boolean mVisible;
    
    private static IBinder binder(IVolumeController paramIVolumeController)
    {
      if (paramIVolumeController == null) {
        return null;
      }
      return paramIVolumeController.asBinder();
    }
    
    public IBinder asBinder()
    {
      return binder(this.mController);
    }
    
    public boolean isSameBinder(IVolumeController paramIVolumeController)
    {
      return Objects.equals(asBinder(), binder(paramIVolumeController));
    }
    
    public void loadSettings(ContentResolver paramContentResolver)
    {
      this.mLongPressTimeout = Settings.Secure.getIntForUser(paramContentResolver, "long_press_timeout", 500, -2);
    }
    
    public void postDismiss()
    {
      if (this.mController == null) {
        return;
      }
      try
      {
        this.mController.dismiss();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("VolumeController", "Error calling dismiss", localRemoteException);
      }
    }
    
    public void postDisplaySafeVolumeWarning(int paramInt)
    {
      if (this.mController == null) {
        return;
      }
      try
      {
        this.mController.displaySafeVolumeWarning(paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("VolumeController", "Error calling displaySafeVolumeWarning", localRemoteException);
      }
    }
    
    public void postMasterMuteChanged(int paramInt)
    {
      if (this.mController == null) {
        return;
      }
      try
      {
        this.mController.masterMuteChanged(paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("VolumeController", "Error calling masterMuteChanged", localRemoteException);
      }
    }
    
    public void postVolumeChanged(int paramInt1, int paramInt2)
    {
      if (this.mController == null) {
        return;
      }
      try
      {
        this.mController.volumeChanged(paramInt1, paramInt2);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("VolumeController", "Error calling volumeChanged", localRemoteException);
      }
    }
    
    public void setController(IVolumeController paramIVolumeController)
    {
      this.mController = paramIVolumeController;
      this.mVisible = false;
    }
    
    public void setLayoutDirection(int paramInt)
    {
      if (this.mController == null) {
        return;
      }
      try
      {
        this.mController.setLayoutDirection(paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("VolumeController", "Error calling setLayoutDirection", localRemoteException);
      }
    }
    
    public void setVisible(boolean paramBoolean)
    {
      this.mVisible = paramBoolean;
    }
    
    public boolean suppressAdjustment(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      if (paramBoolean) {
        return false;
      }
      long l;
      if ((paramInt1 == 2) && (this.mController != null))
      {
        l = SystemClock.uptimeMillis();
        if (((paramInt2 & 0x1) != 0) && (!this.mVisible)) {
          break label62;
        }
        if (this.mNextLongPress > 0L)
        {
          if (l <= this.mNextLongPress) {
            break label86;
          }
          this.mNextLongPress = 0L;
        }
      }
      return false;
      label62:
      if (this.mNextLongPress < l) {
        this.mNextLongPress = (this.mLongPressTimeout + l);
      }
      return true;
      label86:
      return true;
    }
    
    public String toString()
    {
      return "VolumeController(" + asBinder() + ",mVisible=" + this.mVisible + ")";
    }
  }
  
  public class VolumeStreamState
  {
    private final SparseIntArray mIndexMap = new SparseIntArray(8);
    private final int mIndexMax;
    private final int mIndexMin;
    private boolean mIsMuted;
    private int mObservedDevices;
    private final Intent mStreamDevicesChanged;
    private final int mStreamType;
    private final Intent mVolumeChanged;
    private String mVolumeIndexSettingName;
    
    private VolumeStreamState(String paramString, int paramInt)
    {
      this.mVolumeIndexSettingName = paramString;
      this.mStreamType = paramInt;
      this.mIndexMin = (AudioService.-get1()[paramInt] * 10);
      this.mIndexMax = (AudioService.-get0()[paramInt] * 10);
      AudioSystem.initStreamVolume(paramInt, this.mIndexMin / 10, this.mIndexMax / 10);
      readSettings();
      this.mVolumeChanged = new Intent("android.media.VOLUME_CHANGED_ACTION");
      this.mVolumeChanged.putExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", this.mStreamType);
      this.mStreamDevicesChanged = new Intent("android.media.STREAM_DEVICES_CHANGED_ACTION");
      this.mStreamDevicesChanged.putExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", this.mStreamType);
    }
    
    private void dump(PrintWriter paramPrintWriter)
    {
      paramPrintWriter.print("   Muted: ");
      paramPrintWriter.println(this.mIsMuted);
      paramPrintWriter.print("   Min: ");
      paramPrintWriter.println((this.mIndexMin + 5) / 10);
      paramPrintWriter.print("   Max: ");
      paramPrintWriter.println((this.mIndexMax + 5) / 10);
      paramPrintWriter.print("   Current: ");
      int i = 0;
      if (i < this.mIndexMap.size())
      {
        if (i > 0) {
          paramPrintWriter.print(", ");
        }
        j = this.mIndexMap.keyAt(i);
        paramPrintWriter.print(Integer.toHexString(j));
        if (j == 1073741824) {}
        for (String str = "default";; str = AudioSystem.getOutputDeviceName(j))
        {
          if (!str.isEmpty())
          {
            paramPrintWriter.print(" (");
            paramPrintWriter.print(str);
            paramPrintWriter.print(")");
          }
          paramPrintWriter.print(": ");
          paramPrintWriter.print((this.mIndexMap.valueAt(i) + 5) / 10);
          i += 1;
          break;
        }
      }
      paramPrintWriter.println();
      paramPrintWriter.print("   Devices: ");
      int m = AudioService.-wrap6(AudioService.this, this.mStreamType);
      int j = 0;
      i = 0;
      int n = 1 << j;
      if (n != 1073741824)
      {
        if ((m & n) == 0) {
          break label255;
        }
        int k = i + 1;
        if (i > 0) {
          paramPrintWriter.print(", ");
        }
        paramPrintWriter.print(AudioSystem.getOutputDeviceName(n));
        i = k;
      }
      label255:
      for (;;)
      {
        j += 1;
        break;
        return;
      }
    }
    
    private int getAbsoluteVolumeIndex(int paramInt)
    {
      if (paramInt == 0) {
        return 0;
      }
      if (paramInt == 1) {
        return (int)(this.mIndexMax * 0.5D) / 10;
      }
      if (paramInt == 2) {
        return (int)(this.mIndexMax * 0.7D) / 10;
      }
      if (paramInt == 3) {
        return (int)(this.mIndexMax * 0.85D) / 10;
      }
      return (this.mIndexMax + 5) / 10;
    }
    
    private int getValidIndex(int paramInt)
    {
      if (paramInt < this.mIndexMin) {
        return this.mIndexMin;
      }
      if ((AudioService.-get40(AudioService.this)) || (paramInt > this.mIndexMax)) {
        return this.mIndexMax;
      }
      return paramInt;
    }
    
    public boolean adjustIndex(int paramInt1, int paramInt2, String paramString)
    {
      return setIndex(getIndex(paramInt2) + paramInt1, paramInt2, paramString);
    }
    
    public void applyAllVolumes()
    {
      int j = 0;
      for (;;)
      {
        try
        {
          int i;
          if (j < this.mIndexMap.size())
          {
            int k = this.mIndexMap.keyAt(j);
            if (k != 1073741824) {
              if (this.mIsMuted)
              {
                i = 0;
                AudioSystem.setStreamVolumeIndex(this.mStreamType, i, k);
              }
              else
              {
                if (((k & 0x380) != 0) && (AudioService.-get10(AudioService.this)))
                {
                  i = getAbsoluteVolumeIndex((getIndex(k) + 5) / 10);
                  continue;
                }
                if ((AudioService.this.mFullVolumeDevices & k) != 0)
                {
                  i = (this.mIndexMax + 5) / 10;
                  continue;
                }
                i = (this.mIndexMap.valueAt(j) + 5) / 10;
                continue;
              }
            }
          }
          else
          {
            if (this.mIsMuted)
            {
              i = 0;
              AudioSystem.setStreamVolumeIndex(this.mStreamType, i, 1073741824);
              return;
            }
            i = (getIndex(1073741824) + 5) / 10;
            continue;
          }
          j += 1;
        }
        finally {}
      }
    }
    
    public void applyDeviceVolume_syncVSS(int paramInt)
    {
      int i;
      if (this.mIsMuted) {
        i = 0;
      }
      for (;;)
      {
        AudioSystem.setStreamVolumeIndex(this.mStreamType, i, paramInt);
        return;
        if (((paramInt & 0x380) != 0) && (AudioService.-get10(AudioService.this))) {
          i = getAbsoluteVolumeIndex((getIndex(paramInt) + 5) / 10);
        } else if ((AudioService.this.mFullVolumeDevices & paramInt) != 0) {
          i = (this.mIndexMax + 5) / 10;
        } else {
          i = (getIndex(paramInt) + 5) / 10;
        }
      }
    }
    
    public void checkFixedVolumeDevices()
    {
      try
      {
        if (AudioService.-get38(AudioService.this)[this.mStreamType] == 3)
        {
          int i = 0;
          while (i < this.mIndexMap.size())
          {
            int j = this.mIndexMap.keyAt(i);
            int k = this.mIndexMap.valueAt(i);
            if (((AudioService.this.mFullVolumeDevices & j) != 0) || (((AudioService.this.mFixedVolumeDevices & j) != 0) && (k != 0))) {
              this.mIndexMap.put(j, this.mIndexMax);
            }
            applyDeviceVolume_syncVSS(j);
            i += 1;
          }
        }
        return;
      }
      finally {}
    }
    
    public int getIndex(int paramInt)
    {
      try
      {
        int i = this.mIndexMap.get(paramInt, -1);
        paramInt = i;
        if (i == -1) {
          paramInt = this.mIndexMap.get(1073741824);
        }
        return paramInt;
      }
      finally {}
    }
    
    public int getMaxIndex()
    {
      return this.mIndexMax;
    }
    
    public int getMinIndex()
    {
      return this.mIndexMin;
    }
    
    public String getSettingNameForDevice(int paramInt)
    {
      String str1 = this.mVolumeIndexSettingName;
      String str2 = AudioSystem.getOutputDeviceName(paramInt);
      if (str2.isEmpty()) {
        return str1;
      }
      return str1 + "_" + str2;
    }
    
    public int getStreamType()
    {
      return this.mStreamType;
    }
    
    public void mute(boolean paramBoolean)
    {
      int i = 0;
      try
      {
        if (paramBoolean != this.mIsMuted)
        {
          i = 1;
          this.mIsMuted = paramBoolean;
          AudioService.-wrap36(AudioService.-get7(AudioService.this), 10, 2, 0, 0, this, 0);
        }
        if (i != 0)
        {
          Intent localIntent = new Intent("android.media.STREAM_MUTE_CHANGED_ACTION");
          localIntent.putExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", this.mStreamType);
          localIntent.putExtra("android.media.EXTRA_STREAM_VOLUME_MUTED", paramBoolean);
          AudioService.-wrap34(AudioService.this, localIntent);
        }
        return;
      }
      finally {}
    }
    
    public int observeDevicesForStream_syncVSS(boolean paramBoolean)
    {
      int i = AudioSystem.getDevicesForStream(this.mStreamType);
      if (i == this.mObservedDevices) {
        return i;
      }
      int j = this.mObservedDevices;
      this.mObservedDevices = i;
      if (paramBoolean) {
        AudioService.-wrap20(AudioService.this, this.mStreamType);
      }
      if (AudioService.-get38(AudioService.this)[this.mStreamType] == this.mStreamType) {
        EventLogTags.writeStreamDevicesChanged(this.mStreamType, j, i);
      }
      AudioService.-wrap34(AudioService.this, this.mStreamDevicesChanged.putExtra("android.media.EXTRA_PREV_VOLUME_STREAM_DEVICES", j).putExtra("android.media.EXTRA_VOLUME_STREAM_DEVICES", i));
      return i;
    }
    
    public void readSettings()
    {
      try
      {
        if (AudioService.-get40(AudioService.this))
        {
          this.mIndexMap.put(1073741824, this.mIndexMax);
          return;
        }
        if ((this.mStreamType == 1) || (this.mStreamType == 7))
        {
          i = AudioSystem.DEFAULT_STREAM_VOLUME[this.mStreamType] * 10;
          synchronized (AudioService.-get13(AudioService.this))
          {
            if (AudioService.-get13(AudioService.this).booleanValue()) {
              i = this.mIndexMax;
            }
            this.mIndexMap.put(1073741824, i);
            return;
          }
        }
        j = 1140850687;
      }
      finally {}
      int j;
      int i = 0;
      while (j != 0)
      {
        int m = 1 << i;
        if ((m & j) == 0)
        {
          i += 1;
        }
        else
        {
          int k = j & m;
          String str = getSettingNameForDevice(m);
          if (m == 1073741824) {}
          for (j = AudioSystem.DEFAULT_STREAM_VOLUME[this.mStreamType];; j = -1)
          {
            int n = Settings.System.getIntForUser(AudioService.-get15(AudioService.this), str, j, -2);
            j = k;
            if (n == -1) {
              break;
            }
            this.mIndexMap.put(m, getValidIndex(n * 10));
            j = k;
            break;
          }
        }
      }
    }
    
    public void setAllIndexes(VolumeStreamState paramVolumeStreamState, String paramString)
    {
      try
      {
        int j = paramVolumeStreamState.getStreamType();
        int i = paramVolumeStreamState.getIndex(1073741824);
        int k = AudioService.-wrap7(AudioService.this, i, j, this.mStreamType);
        i = 0;
        while (i < this.mIndexMap.size())
        {
          this.mIndexMap.put(this.mIndexMap.keyAt(i), k);
          i += 1;
        }
        paramVolumeStreamState = paramVolumeStreamState.mIndexMap;
        i = 0;
        while (i < paramVolumeStreamState.size())
        {
          k = paramVolumeStreamState.keyAt(i);
          int m = paramVolumeStreamState.valueAt(i);
          setIndex(AudioService.-wrap7(AudioService.this, m, j, this.mStreamType), k, paramString);
          i += 1;
        }
        return;
      }
      finally {}
    }
    
    public void setAllIndexesToMax()
    {
      int i = 0;
      try
      {
        while (i < this.mIndexMap.size())
        {
          this.mIndexMap.put(this.mIndexMap.keyAt(i), this.mIndexMax);
          i += 1;
        }
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public boolean setIndex(int paramInt1, int paramInt2, String paramString)
    {
      if (paramString != null) {}
      for (boolean bool1 = paramString.equals("ThreeKeySpeakerMediaVolume");; bool1 = false)
      {
        if ((!bool1) && (this.mStreamType == 3) && (paramInt2 == 2))
        {
          AudioService.-set9(AudioService.this, paramInt1);
          if (AudioService.DEBUG_VOL) {
            Log.d("AudioService", "setIndex mPerSpeakerMediaVolume " + AudioService.-get25(AudioService.this) + " by " + paramString);
          }
        }
        for (;;)
        {
          int k;
          boolean bool2;
          int j;
          try
          {
            k = getIndex(paramInt2);
            i = getValidIndex(paramInt1);
            Boolean localBoolean = AudioService.-get13(AudioService.this);
            paramInt1 = i;
            try
            {
              if (this.mStreamType == 7)
              {
                paramInt1 = i;
                if (AudioService.-get13(AudioService.this).booleanValue()) {
                  paramInt1 = this.mIndexMax;
                }
              }
              this.mIndexMap.put(paramInt2, paramInt1);
              if (k != paramInt1)
              {
                bool2 = true;
                if (!bool2) {
                  break label325;
                }
                if (paramInt2 != AudioService.-wrap5(AudioService.this, this.mStreamType)) {
                  break label319;
                }
                i = 1;
                j = AudioSystem.getNumStreamTypes() - 1;
                if (j < 0) {
                  break label325;
                }
                if ((j == this.mStreamType) || (AudioService.-get38(AudioService.this)[j] != this.mStreamType)) {
                  break label516;
                }
                int m = AudioService.-wrap7(AudioService.this, paramInt1, this.mStreamType, j);
                AudioService.-get37(AudioService.this)[j].setIndex(m, paramInt2, paramString);
                if (i == 0) {
                  break label516;
                }
                AudioService.-get37(AudioService.this)[j].setIndex(m, AudioService.-wrap5(AudioService.this, j), paramString);
              }
            }
            finally {}
            bool2 = false;
          }
          finally {}
          continue;
          label319:
          int i = 0;
          continue;
          label325:
          if (bool2)
          {
            paramInt2 = (k + 5) / 10;
            paramInt1 = (paramInt1 + 5) / 10;
            if (AudioService.-get38(AudioService.this)[this.mStreamType] == this.mStreamType)
            {
              if (paramString == null) {
                Log.w("AudioService", "No caller for volume_changed event", new Throwable());
              }
              EventLogTags.writeVolumeChanged(this.mStreamType, paramInt2, paramInt1, this.mIndexMax / 10, paramString);
            }
            if (AudioService.this.isASBluetoothA2dpOn()) {
              break label504;
            }
            bool1 = AudioService.-wrap1(AudioService.this);
            if ((paramString == null) || (!paramString.equals("ThreeKeySpeakerMediaVolume"))) {
              break label510;
            }
          }
          for (;;)
          {
            if (!bool1)
            {
              this.mVolumeChanged.putExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", paramInt1);
              this.mVolumeChanged.putExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", paramInt2);
              this.mVolumeChanged.putExtra("android.media.EXTRA_VOLUME_STREAM_TYPE_ALIAS", AudioService.-get38(AudioService.this)[this.mStreamType]);
              AudioService.-wrap34(AudioService.this, this.mVolumeChanged);
            }
            return bool2;
            label504:
            bool1 = true;
            break;
            label510:
            bool1 = false;
          }
          label516:
          j -= 1;
        }
      }
    }
  }
  
  private class WiredDeviceConnectionState
  {
    public final String mAddress;
    public final String mCaller;
    public final String mName;
    public final int mState;
    public final int mType;
    
    public WiredDeviceConnectionState(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3)
    {
      this.mType = paramInt1;
      this.mState = paramInt2;
      this.mAddress = paramString1;
      this.mName = paramString2;
      this.mCaller = paramString3;
    }
  }
  
  class playSilentBufferThread
    extends Thread
  {
    playSilentBufferThread() {}
    
    public void run()
    {
      Log.d("AudioService", "playSilentBufferToInitPA");
      if ((AudioService.this.isASBluetoothA2dpOn()) || (AudioService.-wrap1(AudioService.this))) {
        return;
      }
      int i = AudioTrack.getMinBufferSize(8000, 4, 2);
      if (i < 0) {
        return;
      }
      byte[] arrayOfByte = new byte['㺀'];
      AudioTrack localAudioTrack = new AudioTrack(3, 8000, 4, 2, i, 1);
      localAudioTrack.play();
      localAudioTrack.write(arrayOfByte, 0, 16000);
      try
      {
        Thread.currentThread();
        Thread.sleep(1200L);
        localAudioTrack.stop();
        localAudioTrack.release();
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          Log.e("AudioService", "Interrupted while waiting on playback compelete");
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/audio/AudioService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */