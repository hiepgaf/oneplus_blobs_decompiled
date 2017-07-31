package android.media;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.audiopolicy.AudioMix;
import android.util.Log;
import java.util.ArrayList;

public class AudioSystem
{
  public static final int AUDIO_HW_SYNC_INVALID = 0;
  public static final int AUDIO_SESSION_ALLOCATE = 0;
  public static final int AUDIO_STATUS_ERROR = 1;
  public static final int AUDIO_STATUS_OK = 0;
  public static final int AUDIO_STATUS_SERVER_DIED = 100;
  public static final int BAD_VALUE = -2;
  public static final int DEAD_OBJECT = -6;
  public static final int DEFAULT_MUTE_STREAMS_AFFECTED = 46;
  public static int[] DEFAULT_STREAM_VOLUME = { 4, 7, 5, 15, 6, 5, 7, 7, 11, 11 };
  public static final int DEVICE_ALL_HDMI_SYSTEM_AUDIO_AND_SPEAKER = 2883586;
  public static final int DEVICE_BIT_DEFAULT = 1073741824;
  public static final int DEVICE_BIT_IN = Integer.MIN_VALUE;
  public static final int DEVICE_IN_ALL = -1038090241;
  public static final int DEVICE_IN_ALL_SCO = -2147483640;
  public static final int DEVICE_IN_ALL_USB = -2147477504;
  public static final int DEVICE_IN_AMBIENT = -2147483646;
  public static final String DEVICE_IN_AMBIENT_NAME = "ambient";
  public static final int DEVICE_IN_ANLG_DOCK_HEADSET = -2147483136;
  public static final String DEVICE_IN_ANLG_DOCK_HEADSET_NAME = "analog_dock";
  public static final int DEVICE_IN_AUX_DIGITAL = -2147483616;
  public static final String DEVICE_IN_AUX_DIGITAL_NAME = "aux_digital";
  public static final int DEVICE_IN_BACK_MIC = -2147483520;
  public static final String DEVICE_IN_BACK_MIC_NAME = "back_mic";
  public static final int DEVICE_IN_BLUETOOTH_A2DP = -2147352576;
  public static final String DEVICE_IN_BLUETOOTH_A2DP_NAME = "bt_a2dp";
  public static final int DEVICE_IN_BLUETOOTH_SCO_HEADSET = -2147483640;
  public static final String DEVICE_IN_BLUETOOTH_SCO_HEADSET_NAME = "bt_sco_hs";
  public static final int DEVICE_IN_BUILTIN_MIC = -2147483644;
  public static final String DEVICE_IN_BUILTIN_MIC_NAME = "mic";
  public static final int DEVICE_IN_BUS = -2146435072;
  public static final String DEVICE_IN_BUS_NAME = "bus";
  public static final int DEVICE_IN_COMMUNICATION = -2147483647;
  public static final String DEVICE_IN_COMMUNICATION_NAME = "communication";
  public static final int DEVICE_IN_DEFAULT = -1073741824;
  public static final int DEVICE_IN_DGTL_DOCK_HEADSET = -2147482624;
  public static final String DEVICE_IN_DGTL_DOCK_HEADSET_NAME = "digital_dock";
  public static final int DEVICE_IN_FM_TUNER = -2147475456;
  public static final String DEVICE_IN_FM_TUNER_NAME = "fm_tuner";
  public static final int DEVICE_IN_HDMI = -2147483616;
  public static final int DEVICE_IN_IP = -2146959360;
  public static final String DEVICE_IN_IP_NAME = "ip";
  public static final int DEVICE_IN_LINE = -2147450880;
  public static final String DEVICE_IN_LINE_NAME = "line";
  public static final int DEVICE_IN_LOOPBACK = -2147221504;
  public static final String DEVICE_IN_LOOPBACK_NAME = "loopback";
  public static final int DEVICE_IN_PROXY = -2113929216;
  public static final int DEVICE_IN_REMOTE_SUBMIX = -2147483392;
  public static final String DEVICE_IN_REMOTE_SUBMIX_NAME = "remote_submix";
  public static final int DEVICE_IN_SPDIF = -2147418112;
  public static final String DEVICE_IN_SPDIF_NAME = "spdif";
  public static final int DEVICE_IN_TELEPHONY_RX = -2147483584;
  public static final String DEVICE_IN_TELEPHONY_RX_NAME = "telephony_rx";
  public static final int DEVICE_IN_TV_TUNER = -2147467264;
  public static final String DEVICE_IN_TV_TUNER_NAME = "tv_tuner";
  public static final int DEVICE_IN_USB_ACCESSORY = -2147481600;
  public static final String DEVICE_IN_USB_ACCESSORY_NAME = "usb_accessory";
  public static final int DEVICE_IN_USB_DEVICE = -2147479552;
  public static final String DEVICE_IN_USB_DEVICE_NAME = "usb_device";
  public static final int DEVICE_IN_VOICE_CALL = -2147483584;
  public static final int DEVICE_IN_WIRED_HEADSET = -2147483632;
  public static final String DEVICE_IN_WIRED_HEADSET_NAME = "headset";
  public static final int DEVICE_NONE = 0;
  public static final int DEVICE_OUT_ALL = 1140850687;
  public static final int DEVICE_OUT_ALL_A2DP = 896;
  public static final int DEVICE_OUT_ALL_HDMI_SYSTEM_AUDIO = 2883584;
  public static final int DEVICE_OUT_ALL_SCO = 112;
  public static final int DEVICE_OUT_ALL_USB = 24576;
  public static final int DEVICE_OUT_ANLG_DOCK_HEADSET = 2048;
  public static final String DEVICE_OUT_ANLG_DOCK_HEADSET_NAME = "analog_dock";
  public static final int DEVICE_OUT_AUX_DIGITAL = 1024;
  public static final String DEVICE_OUT_AUX_DIGITAL_NAME = "aux_digital";
  public static final int DEVICE_OUT_AUX_LINE = 2097152;
  public static final String DEVICE_OUT_AUX_LINE_NAME = "aux_line";
  public static final int DEVICE_OUT_BLUETOOTH_A2DP = 128;
  public static final int DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES = 256;
  public static final String DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME = "bt_a2dp_hp";
  public static final String DEVICE_OUT_BLUETOOTH_A2DP_NAME = "bt_a2dp";
  public static final int DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER = 512;
  public static final String DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME = "bt_a2dp_spk";
  public static final int DEVICE_OUT_BLUETOOTH_SCO = 16;
  public static final int DEVICE_OUT_BLUETOOTH_SCO_CARKIT = 64;
  public static final String DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME = "bt_sco_carkit";
  public static final int DEVICE_OUT_BLUETOOTH_SCO_HEADSET = 32;
  public static final String DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME = "bt_sco_hs";
  public static final String DEVICE_OUT_BLUETOOTH_SCO_NAME = "bt_sco";
  public static final int DEVICE_OUT_BUS = 16777216;
  public static final String DEVICE_OUT_BUS_NAME = "bus";
  public static final int DEVICE_OUT_DEFAULT = 1073741824;
  public static final int DEVICE_OUT_DGTL_DOCK_HEADSET = 4096;
  public static final String DEVICE_OUT_DGTL_DOCK_HEADSET_NAME = "digital_dock";
  public static final int DEVICE_OUT_EARPIECE = 1;
  public static final String DEVICE_OUT_EARPIECE_NAME = "earpiece";
  public static final int DEVICE_OUT_FM = 1048576;
  public static final String DEVICE_OUT_FM_NAME = "fm_transmitter";
  public static final int DEVICE_OUT_HDMI = 1024;
  public static final int DEVICE_OUT_HDMI_ARC = 262144;
  public static final String DEVICE_OUT_HDMI_ARC_NAME = "hmdi_arc";
  public static final String DEVICE_OUT_HDMI_NAME = "hdmi";
  public static final int DEVICE_OUT_IP = 8388608;
  public static final String DEVICE_OUT_IP_NAME = "ip";
  public static final int DEVICE_OUT_LINE = 131072;
  public static final String DEVICE_OUT_LINE_NAME = "line";
  public static final int DEVICE_OUT_PROXY = 33554432;
  public static final String DEVICE_OUT_PROXY_NAME = "proxy";
  public static final int DEVICE_OUT_REMOTE_SUBMIX = 32768;
  public static final String DEVICE_OUT_REMOTE_SUBMIX_NAME = "remote_submix";
  public static final int DEVICE_OUT_SPDIF = 524288;
  public static final String DEVICE_OUT_SPDIF_NAME = "spdif";
  public static final int DEVICE_OUT_SPEAKER = 2;
  public static final String DEVICE_OUT_SPEAKER_NAME = "speaker";
  public static final int DEVICE_OUT_SPEAKER_SAFE = 4194304;
  public static final String DEVICE_OUT_SPEAKER_SAFE_NAME = "speaker_safe";
  public static final int DEVICE_OUT_TELEPHONY_TX = 65536;
  public static final String DEVICE_OUT_TELEPHONY_TX_NAME = "telephony_tx";
  public static final int DEVICE_OUT_USB_ACCESSORY = 8192;
  public static final String DEVICE_OUT_USB_ACCESSORY_NAME = "usb_accessory";
  public static final int DEVICE_OUT_USB_DEVICE = 16384;
  public static final String DEVICE_OUT_USB_DEVICE_NAME = "usb_device";
  public static final int DEVICE_OUT_WIRED_HEADPHONE = 8;
  public static final String DEVICE_OUT_WIRED_HEADPHONE_NAME = "headphone";
  public static final int DEVICE_OUT_WIRED_HEADSET = 4;
  public static final String DEVICE_OUT_WIRED_HEADSET_NAME = "headset";
  public static final int DEVICE_STATE_AVAILABLE = 1;
  public static final int DEVICE_STATE_UNAVAILABLE = 0;
  private static final int DYNAMIC_POLICY_EVENT_MIX_STATE_UPDATE = 0;
  public static final int ERROR = -1;
  public static final int FORCE_ANALOG_DOCK = 8;
  public static final int FORCE_BT_A2DP = 4;
  public static final int FORCE_BT_CAR_DOCK = 6;
  public static final int FORCE_BT_DESK_DOCK = 7;
  public static final int FORCE_BT_SCO = 3;
  public static final int FORCE_DEFAULT = 0;
  public static final int FORCE_DIGITAL_DOCK = 9;
  public static final int FORCE_ENCODED_SURROUND_ALWAYS = 14;
  public static final int FORCE_ENCODED_SURROUND_NEVER = 13;
  public static final int FORCE_HDMI_SYSTEM_AUDIO_ENFORCED = 12;
  public static final int FORCE_HEADPHONES = 2;
  public static final int FORCE_NONE = 0;
  public static final int FORCE_NO_BT_A2DP = 10;
  public static final int FORCE_SPEAKER = 1;
  public static final int FORCE_SYSTEM_ENFORCED = 11;
  public static final int FORCE_WIRED_ACCESSORY = 5;
  public static final int FOR_COMMUNICATION = 0;
  public static final int FOR_DOCK = 3;
  public static final int FOR_ENCODED_SURROUND = 6;
  public static final int FOR_HDMI_SYSTEM_AUDIO = 5;
  public static final int FOR_MEDIA = 1;
  public static final int FOR_RECORD = 2;
  public static final int FOR_SYSTEM = 4;
  public static final int INVALID_OPERATION = -3;
  public static final String IN_VOICE_COMM_FOCUS_ID = "AudioFocus_For_Phone_Ring_And_Calls";
  public static final int MODE_CURRENT = -1;
  public static final int MODE_INVALID = -2;
  public static final int MODE_IN_CALL = 2;
  public static final int MODE_IN_COMMUNICATION = 3;
  public static final int MODE_NORMAL = 0;
  public static final int MODE_RINGTONE = 1;
  static final int NATIVE_EVENT_ROUTING_CHANGE = 1000;
  public static final int NO_INIT = -5;
  private static final int NUM_DEVICE_STATES = 1;
  public static final int NUM_FORCE_CONFIG = 15;
  private static final int NUM_FORCE_USE = 7;
  public static final int NUM_MODES = 4;
  public static final int NUM_STREAMS = 5;
  private static final int NUM_STREAM_TYPES = 10;
  public static final int PERMISSION_DENIED = -4;
  public static final int PHONE_STATE_INCALL = 2;
  public static final int PHONE_STATE_OFFCALL = 0;
  public static final int PHONE_STATE_RINGING = 1;
  public static final int PLATFORM_DEFAULT = 0;
  public static final int PLATFORM_TELEVISION = 2;
  public static final int PLATFORM_VOICE = 1;
  public static final int PLAY_SOUND_DELAY = 300;
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
  public static final int STREAM_ALARM = 4;
  public static final int STREAM_BLUETOOTH_SCO = 6;
  public static final int STREAM_DEFAULT = -1;
  public static final int STREAM_DTMF = 8;
  public static final int STREAM_MUSIC = 3;
  public static final String[] STREAM_NAMES = { "STREAM_VOICE_CALL", "STREAM_SYSTEM", "STREAM_RING", "STREAM_MUSIC", "STREAM_ALARM", "STREAM_NOTIFICATION", "STREAM_BLUETOOTH_SCO", "STREAM_SYSTEM_ENFORCED", "STREAM_DTMF", "STREAM_TTS" };
  public static final int STREAM_NOTIFICATION = 5;
  public static final int STREAM_RING = 2;
  public static final int STREAM_SYSTEM = 1;
  public static final int STREAM_SYSTEM_ENFORCED = 7;
  public static final int STREAM_TTS = 9;
  public static final int STREAM_VOICE_CALL = 0;
  public static final int SUCCESS = 0;
  public static final int SYNC_EVENT_NONE = 0;
  public static final int SYNC_EVENT_PRESENTATION_COMPLETE = 1;
  private static final String TAG = "AudioSystem";
  public static final int WOULD_BLOCK = -7;
  private static ErrorCallback mErrorCallback;
  private static DynamicPolicyCallback sDynPolicyCallback;
  private static AudioRecordingCallback sRecordingCallback;
  
  public static native int checkAudioFlinger();
  
  public static native int createAudioPatch(AudioPatch[] paramArrayOfAudioPatch, AudioPortConfig[] paramArrayOfAudioPortConfig1, AudioPortConfig[] paramArrayOfAudioPortConfig2);
  
  private static void dynamicPolicyCallbackFromNative(int paramInt1, String paramString, int paramInt2)
  {
    DynamicPolicyCallback localDynamicPolicyCallback = null;
    try
    {
      if (sDynPolicyCallback != null) {
        localDynamicPolicyCallback = sDynPolicyCallback;
      }
      if (localDynamicPolicyCallback != null) {}
      switch (paramInt1)
      {
      default: 
        Log.e("AudioSystem", "dynamicPolicyCallbackFromNative: unknown event " + paramInt1);
        return;
      }
    }
    finally {}
    localDynamicPolicyCallback.onDynamicPolicyMixStateUpdate(paramString, paramInt2);
  }
  
  private static void errorCallbackFromNative(int paramInt)
  {
    ErrorCallback localErrorCallback = null;
    try
    {
      if (mErrorCallback != null) {
        localErrorCallback = mErrorCallback;
      }
      if (localErrorCallback != null) {
        localErrorCallback.onError(paramInt);
      }
      return;
    }
    finally {}
  }
  
  public static native int getAudioHwSyncForSession(int paramInt);
  
  public static int getDefaultStreamVolume(int paramInt)
  {
    return DEFAULT_STREAM_VOLUME[paramInt];
  }
  
  public static native int getDeviceConnectionState(int paramInt, String paramString);
  
  public static native int getDevicesForStream(int paramInt);
  
  public static native int getForceUse(int paramInt);
  
  public static String getInputDeviceName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case -2147483647: 
      return "communication";
    case -2147483646: 
      return "ambient";
    case -2147483644: 
      return "mic";
    case -2147483640: 
      return "bt_sco_hs";
    case -2147483632: 
      return "headset";
    case -2147483616: 
      return "aux_digital";
    case -2147483584: 
      return "telephony_rx";
    case -2147483520: 
      return "back_mic";
    case -2147483392: 
      return "remote_submix";
    case -2147483136: 
      return "analog_dock";
    case -2147482624: 
      return "digital_dock";
    case -2147481600: 
      return "usb_accessory";
    case -2147479552: 
      return "usb_device";
    case -2147475456: 
      return "fm_tuner";
    case -2147467264: 
      return "tv_tuner";
    case -2147450880: 
      return "line";
    case -2147418112: 
      return "spdif";
    case -2147352576: 
      return "bt_a2dp";
    case -2147221504: 
      return "loopback";
    case -2146959360: 
      return "ip";
    }
    return "bus";
  }
  
  public static native boolean getMasterMono();
  
  public static native boolean getMasterMute();
  
  public static native float getMasterVolume();
  
  public static final int getNumStreamTypes()
  {
    return 10;
  }
  
  public static String getOutputDeviceName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 1: 
      return "earpiece";
    case 2: 
      return "speaker";
    case 4: 
      return "headset";
    case 8: 
      return "headphone";
    case 16: 
      return "bt_sco";
    case 32: 
      return "bt_sco_hs";
    case 64: 
      return "bt_sco_carkit";
    case 128: 
      return "bt_a2dp";
    case 256: 
      return "bt_a2dp_hp";
    case 512: 
      return "bt_a2dp_spk";
    case 1024: 
      return "hdmi";
    case 2048: 
      return "analog_dock";
    case 4096: 
      return "digital_dock";
    case 8192: 
      return "usb_accessory";
    case 16384: 
      return "usb_device";
    case 32768: 
      return "remote_submix";
    case 65536: 
      return "telephony_tx";
    case 131072: 
      return "line";
    case 262144: 
      return "hmdi_arc";
    case 524288: 
      return "spdif";
    case 1048576: 
      return "fm_transmitter";
    case 2097152: 
      return "aux_line";
    case 4194304: 
      return "speaker_safe";
    case 8388608: 
      return "ip";
    case 16777216: 
      return "bus";
    }
    return "proxy";
  }
  
  public static native int getOutputLatency(int paramInt);
  
  public static native String getParameters(String paramString);
  
  public static int getPlatformType(Context paramContext)
  {
    if (paramContext.getResources().getBoolean(17956957)) {
      return 1;
    }
    if (paramContext.getPackageManager().hasSystemFeature("android.software.leanback")) {
      return 2;
    }
    return 0;
  }
  
  public static native int getPrimaryOutputFrameCount();
  
  public static native int getPrimaryOutputSamplingRate();
  
  public static native boolean getStreamMute(int paramInt);
  
  public static native int getStreamVolumeIndex(int paramInt1, int paramInt2);
  
  public static int getValueForVibrateSetting(int paramInt1, int paramInt2, int paramInt3)
  {
    return paramInt1 & 3 << paramInt2 * 2 | (paramInt3 & 0x3) << paramInt2 * 2;
  }
  
  public static native int initStreamVolume(int paramInt1, int paramInt2, int paramInt3);
  
  public static native boolean isMicrophoneMuted();
  
  public static native boolean isSourceActive(int paramInt);
  
  public static native boolean isStreamActive(int paramInt1, int paramInt2);
  
  public static native boolean isStreamActiveRemotely(int paramInt1, int paramInt2);
  
  public static native int listAudioPatches(ArrayList<AudioPatch> paramArrayList, int[] paramArrayOfInt);
  
  public static native int listAudioPorts(ArrayList<AudioPort> paramArrayList, int[] paramArrayOfInt);
  
  public static native int muteMicrophone(boolean paramBoolean);
  
  private static final native void native_register_dynamic_policy_callback();
  
  private static final native void native_register_recording_callback();
  
  public static native int newAudioSessionId();
  
  private static void recordingCallbackFromNative(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt)
  {
    try
    {
      AudioRecordingCallback localAudioRecordingCallback = sRecordingCallback;
      if (localAudioRecordingCallback != null) {
        localAudioRecordingCallback.onRecordingConfigurationChanged(paramInt1, paramInt2, paramInt3, paramArrayOfInt);
      }
      return;
    }
    finally {}
  }
  
  public static native int registerPolicyMixes(ArrayList<AudioMix> paramArrayList, boolean paramBoolean);
  
  public static native int releaseAudioPatch(AudioPatch paramAudioPatch);
  
  public static native int setAudioPortConfig(AudioPortConfig paramAudioPortConfig);
  
  public static native int setDeviceConnectionState(int paramInt1, int paramInt2, String paramString1, String paramString2);
  
  public static void setDynamicPolicyCallback(DynamicPolicyCallback paramDynamicPolicyCallback)
  {
    try
    {
      sDynPolicyCallback = paramDynamicPolicyCallback;
      native_register_dynamic_policy_callback();
      return;
    }
    finally
    {
      paramDynamicPolicyCallback = finally;
      throw paramDynamicPolicyCallback;
    }
  }
  
  public static void setErrorCallback(ErrorCallback paramErrorCallback)
  {
    try
    {
      mErrorCallback = paramErrorCallback;
      if (paramErrorCallback != null) {
        paramErrorCallback.onError(checkAudioFlinger());
      }
      return;
    }
    finally {}
  }
  
  public static native int setForceUse(int paramInt1, int paramInt2);
  
  public static native int setLowRamDevice(boolean paramBoolean);
  
  public static native int setMasterMono(boolean paramBoolean);
  
  public static native int setMasterMute(boolean paramBoolean);
  
  public static native int setMasterVolume(float paramFloat);
  
  public static native int setParameters(String paramString);
  
  public static native int setPhoneState(int paramInt);
  
  public static void setRecordingCallback(AudioRecordingCallback paramAudioRecordingCallback)
  {
    try
    {
      sRecordingCallback = paramAudioRecordingCallback;
      native_register_recording_callback();
      return;
    }
    finally
    {
      paramAudioRecordingCallback = finally;
      throw paramAudioRecordingCallback;
    }
  }
  
  public static native int setStreamMute(int paramInt, boolean paramBoolean);
  
  public static native int setStreamVolumeIndex(int paramInt1, int paramInt2, int paramInt3);
  
  public static String streamToString(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < STREAM_NAMES.length)) {
      return STREAM_NAMES[paramInt];
    }
    if (paramInt == Integer.MIN_VALUE) {
      return "USE_DEFAULT_STREAM_TYPE";
    }
    return "UNKNOWN_STREAM_" + paramInt;
  }
  
  public static native int systemReady();
  
  public static abstract interface AudioRecordingCallback
  {
    public abstract void onRecordingConfigurationChanged(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt);
  }
  
  public static abstract interface DynamicPolicyCallback
  {
    public abstract void onDynamicPolicyMixStateUpdate(String paramString, int paramInt);
  }
  
  public static abstract interface ErrorCallback
  {
    public abstract void onError(int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioSystem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */