package android.media.audiopolicy;

import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioFormat.Builder;
import android.media.AudioSystem;
import java.util.Objects;

public class AudioMix
{
  private static final int CALLBACK_FLAGS_ALL = 1;
  public static final int CALLBACK_FLAG_NOTIFY_ACTIVITY = 1;
  public static final int MIX_STATE_DISABLED = -1;
  public static final int MIX_STATE_IDLE = 0;
  public static final int MIX_STATE_MIXING = 1;
  public static final int MIX_TYPE_INVALID = -1;
  public static final int MIX_TYPE_PLAYERS = 0;
  public static final int MIX_TYPE_RECORDERS = 1;
  public static final int ROUTE_FLAG_LOOP_BACK = 2;
  public static final int ROUTE_FLAG_RENDER = 1;
  private static final int ROUTE_FLAG_SUPPORTED = 3;
  int mCallbackFlags;
  String mDeviceAddress;
  final int mDeviceSystemType;
  private AudioFormat mFormat;
  int mMixState = -1;
  private int mMixType = -1;
  private int mRouteFlags;
  private AudioMixingRule mRule;
  
  private AudioMix(AudioMixingRule paramAudioMixingRule, AudioFormat paramAudioFormat, int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    this.mRule = paramAudioMixingRule;
    this.mFormat = paramAudioFormat;
    this.mRouteFlags = paramInt1;
    this.mMixType = paramAudioMixingRule.getTargetMixType();
    this.mCallbackFlags = paramInt2;
    this.mDeviceSystemType = paramInt3;
    paramAudioMixingRule = paramString;
    if (paramString == null) {
      paramAudioMixingRule = new String("");
    }
    this.mDeviceAddress = paramAudioMixingRule;
  }
  
  AudioFormat getFormat()
  {
    return this.mFormat;
  }
  
  public int getMixState()
  {
    return this.mMixState;
  }
  
  public int getMixType()
  {
    return this.mMixType;
  }
  
  public String getRegistration()
  {
    return this.mDeviceAddress;
  }
  
  int getRouteFlags()
  {
    return this.mRouteFlags;
  }
  
  AudioMixingRule getRule()
  {
    return this.mRule;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(this.mRouteFlags), this.mRule, Integer.valueOf(this.mMixType), this.mFormat });
  }
  
  void setRegistration(String paramString)
  {
    this.mDeviceAddress = paramString;
  }
  
  public static class Builder
  {
    private int mCallbackFlags = 0;
    private String mDeviceAddress = null;
    private int mDeviceSystemType = 0;
    private AudioFormat mFormat = null;
    private int mRouteFlags = 0;
    private AudioMixingRule mRule = null;
    
    Builder() {}
    
    public Builder(AudioMixingRule paramAudioMixingRule)
      throws IllegalArgumentException
    {
      if (paramAudioMixingRule == null) {
        throw new IllegalArgumentException("Illegal null AudioMixingRule argument");
      }
      this.mRule = paramAudioMixingRule;
    }
    
    public AudioMix build()
      throws IllegalArgumentException
    {
      if (this.mRule == null) {
        throw new IllegalArgumentException("Illegal null AudioMixingRule");
      }
      if (this.mRouteFlags == 0) {
        this.mRouteFlags = 2;
      }
      if (this.mRouteFlags == 3) {
        throw new IllegalArgumentException("Unsupported route behavior combination 0x" + Integer.toHexString(this.mRouteFlags));
      }
      if (this.mFormat == null)
      {
        int j = AudioSystem.getPrimaryOutputSamplingRate();
        int i = j;
        if (j <= 0) {
          i = 44100;
        }
        this.mFormat = new AudioFormat.Builder().setSampleRate(i).build();
      }
      if ((this.mDeviceSystemType != 0) && (this.mDeviceSystemType != 32768) && (this.mDeviceSystemType != -2147483392))
      {
        if ((this.mRouteFlags & 0x1) == 0) {
          throw new IllegalArgumentException("Can't have audio device without flag ROUTE_FLAG_RENDER");
        }
        if (this.mRule.getTargetMixType() != 0) {
          throw new IllegalArgumentException("Unsupported device on non-playback mix");
        }
      }
      else
      {
        if ((this.mRouteFlags & 0x1) == 1) {
          throw new IllegalArgumentException("Can't have flag ROUTE_FLAG_RENDER without an audio device");
        }
        if ((this.mRouteFlags & 0x3) == 2) {
          if (this.mRule.getTargetMixType() != 0) {
            break label251;
          }
        }
      }
      for (this.mDeviceSystemType = 32768;; this.mDeviceSystemType = -2147483392)
      {
        return new AudioMix(this.mRule, this.mFormat, this.mRouteFlags, this.mCallbackFlags, this.mDeviceSystemType, this.mDeviceAddress, null);
        label251:
        if (this.mRule.getTargetMixType() != 1) {
          break;
        }
      }
      throw new IllegalArgumentException("Unknown mixing rule type");
    }
    
    Builder setCallbackFlags(int paramInt)
      throws IllegalArgumentException
    {
      if ((paramInt != 0) && ((paramInt & 0x1) == 0)) {
        throw new IllegalArgumentException("Illegal callback flags 0x" + Integer.toHexString(paramInt).toUpperCase());
      }
      this.mCallbackFlags = paramInt;
      return this;
    }
    
    Builder setDevice(int paramInt, String paramString)
    {
      this.mDeviceSystemType = paramInt;
      this.mDeviceAddress = paramString;
      return this;
    }
    
    public Builder setDevice(AudioDeviceInfo paramAudioDeviceInfo)
      throws IllegalArgumentException
    {
      if (paramAudioDeviceInfo == null) {
        throw new IllegalArgumentException("Illegal null AudioDeviceInfo argument");
      }
      if (!paramAudioDeviceInfo.isSink()) {
        throw new IllegalArgumentException("Unsupported device type on mix, not a sink");
      }
      this.mDeviceSystemType = AudioDeviceInfo.convertDeviceTypeToInternalDevice(paramAudioDeviceInfo.getType());
      this.mDeviceAddress = paramAudioDeviceInfo.getAddress();
      return this;
    }
    
    public Builder setFormat(AudioFormat paramAudioFormat)
      throws IllegalArgumentException
    {
      if (paramAudioFormat == null) {
        throw new IllegalArgumentException("Illegal null AudioFormat argument");
      }
      this.mFormat = paramAudioFormat;
      return this;
    }
    
    Builder setMixingRule(AudioMixingRule paramAudioMixingRule)
      throws IllegalArgumentException
    {
      if (paramAudioMixingRule == null) {
        throw new IllegalArgumentException("Illegal null AudioMixingRule argument");
      }
      this.mRule = paramAudioMixingRule;
      return this;
    }
    
    public Builder setRouteFlags(int paramInt)
      throws IllegalArgumentException
    {
      if (paramInt == 0) {
        throw new IllegalArgumentException("Illegal empty route flags");
      }
      if ((paramInt & 0x3) == 0) {
        throw new IllegalArgumentException("Invalid route flags 0x" + Integer.toHexString(paramInt) + "when configuring an AudioMix");
      }
      if ((paramInt & 0xFFFFFFFC) != 0) {
        throw new IllegalArgumentException("Unknown route flags 0x" + Integer.toHexString(paramInt) + "when configuring an AudioMix");
      }
      this.mRouteFlags = paramInt;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiopolicy/AudioMix.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */