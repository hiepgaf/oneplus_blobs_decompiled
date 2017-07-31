package android.media.audiopolicy;

import android.content.Context;
import android.media.AudioAttributes.Builder;
import android.media.AudioFocusInfo;
import android.media.AudioFormat;
import android.media.AudioFormat.Builder;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.IAudioService;
import android.media.IAudioService.Stub;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Iterator;

public class AudioPolicy
{
  private static final boolean DEBUG = false;
  public static final int FOCUS_POLICY_DUCKING_DEFAULT = 0;
  public static final int FOCUS_POLICY_DUCKING_IN_APP = 0;
  public static final int FOCUS_POLICY_DUCKING_IN_POLICY = 1;
  private static final int MSG_FOCUS_GRANT = 1;
  private static final int MSG_FOCUS_LOSS = 2;
  private static final int MSG_MIX_STATE_UPDATE = 3;
  private static final int MSG_POLICY_STATUS_CHANGE = 0;
  public static final int POLICY_STATUS_REGISTERED = 2;
  public static final int POLICY_STATUS_UNREGISTERED = 1;
  private static final String TAG = "AudioPolicy";
  private static IAudioService sService;
  private AudioPolicyConfig mConfig;
  private Context mContext;
  private final EventHandler mEventHandler;
  private AudioPolicyFocusListener mFocusListener;
  private final Object mLock = new Object();
  private final IAudioPolicyCallback mPolicyCb = new IAudioPolicyCallback.Stub()
  {
    public void notifyAudioFocusGrant(AudioFocusInfo paramAnonymousAudioFocusInfo, int paramAnonymousInt)
    {
      AudioPolicy.-wrap1(AudioPolicy.this, 1, paramAnonymousAudioFocusInfo, paramAnonymousInt);
    }
    
    public void notifyAudioFocusLoss(AudioFocusInfo paramAnonymousAudioFocusInfo, boolean paramAnonymousBoolean)
    {
      AudioPolicy localAudioPolicy = AudioPolicy.this;
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        AudioPolicy.-wrap1(localAudioPolicy, 2, paramAnonymousAudioFocusInfo, i);
        return;
      }
    }
    
    public void notifyMixStateUpdate(String paramAnonymousString, int paramAnonymousInt)
    {
      Iterator localIterator = AudioPolicy.-get0(AudioPolicy.this).getMixes().iterator();
      while (localIterator.hasNext())
      {
        AudioMix localAudioMix = (AudioMix)localIterator.next();
        if (localAudioMix.getRegistration().equals(paramAnonymousString))
        {
          localAudioMix.mMixState = paramAnonymousInt;
          AudioPolicy.-wrap1(AudioPolicy.this, 3, localAudioMix, 0);
        }
      }
    }
  };
  private String mRegistrationId;
  private int mStatus;
  private AudioPolicyStatusListener mStatusListener;
  
  private AudioPolicy(AudioPolicyConfig paramAudioPolicyConfig, Context paramContext, Looper paramLooper, AudioPolicyFocusListener paramAudioPolicyFocusListener, AudioPolicyStatusListener paramAudioPolicyStatusListener)
  {
    this.mConfig = paramAudioPolicyConfig;
    this.mStatus = 1;
    this.mContext = paramContext;
    paramAudioPolicyConfig = paramLooper;
    if (paramLooper == null) {
      paramAudioPolicyConfig = Looper.getMainLooper();
    }
    if (paramAudioPolicyConfig != null) {
      this.mEventHandler = new EventHandler(this, paramAudioPolicyConfig);
    }
    for (;;)
    {
      this.mFocusListener = paramAudioPolicyFocusListener;
      this.mStatusListener = paramAudioPolicyStatusListener;
      return;
      this.mEventHandler = null;
      Log.e("AudioPolicy", "No event handler due to looper without a thread");
    }
  }
  
  private static String addressForTag(AudioMix paramAudioMix)
  {
    return "addr=" + paramAudioMix.getRegistration();
  }
  
  private void checkMixReadyToUse(AudioMix paramAudioMix, boolean paramBoolean)
    throws IllegalArgumentException
  {
    if (paramAudioMix == null)
    {
      if (paramBoolean) {}
      for (paramAudioMix = "Invalid null AudioMix for AudioTrack creation";; paramAudioMix = "Invalid null AudioMix for AudioRecord creation") {
        throw new IllegalArgumentException(paramAudioMix);
      }
    }
    if (!this.mConfig.mMixes.contains(paramAudioMix)) {
      throw new IllegalArgumentException("Invalid mix: not part of this policy");
    }
    if ((paramAudioMix.getRouteFlags() & 0x2) != 2) {
      throw new IllegalArgumentException("Invalid AudioMix: not defined for loop back");
    }
    if ((paramBoolean) && (paramAudioMix.getMixType() != 1)) {
      throw new IllegalArgumentException("Invalid AudioMix: not defined for being a recording source");
    }
    if ((!paramBoolean) && (paramAudioMix.getMixType() != 0)) {
      throw new IllegalArgumentException("Invalid AudioMix: not defined for capturing playback");
    }
  }
  
  private static IAudioService getService()
  {
    if (sService != null) {
      return sService;
    }
    sService = IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
    return sService;
  }
  
  private void onPolicyStatusChange()
  {
    synchronized (this.mLock)
    {
      AudioPolicyStatusListener localAudioPolicyStatusListener = this.mStatusListener;
      if (localAudioPolicyStatusListener == null) {
        return;
      }
      localAudioPolicyStatusListener = this.mStatusListener;
      localAudioPolicyStatusListener.onStatusChange();
      return;
    }
  }
  
  private boolean policyReadyToUse()
  {
    synchronized (this.mLock)
    {
      if (this.mStatus != 2)
      {
        Log.e("AudioPolicy", "Cannot use unregistered AudioPolicy");
        return false;
      }
      if (this.mContext == null)
      {
        Log.e("AudioPolicy", "Cannot use AudioPolicy without context");
        return false;
      }
      if (this.mRegistrationId == null)
      {
        Log.e("AudioPolicy", "Cannot use unregistered AudioPolicy");
        return false;
      }
      if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_ROUTING") != 0)
      {
        Slog.w("AudioPolicy", "Cannot use AudioPolicy for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid() + ", needs MODIFY_AUDIO_ROUTING");
        return false;
      }
    }
    return true;
  }
  
  private void sendMsg(int paramInt)
  {
    if (this.mEventHandler != null) {
      this.mEventHandler.sendEmptyMessage(paramInt);
    }
  }
  
  private void sendMsg(int paramInt1, Object paramObject, int paramInt2)
  {
    if (this.mEventHandler != null) {
      this.mEventHandler.sendMessage(this.mEventHandler.obtainMessage(paramInt1, paramInt2, 0, paramObject));
    }
  }
  
  public IAudioPolicyCallback cb()
  {
    return this.mPolicyCb;
  }
  
  public AudioRecord createAudioRecordSink(AudioMix paramAudioMix)
    throws IllegalArgumentException
  {
    if (!policyReadyToUse())
    {
      Log.e("AudioPolicy", "Cannot create AudioRecord sink for AudioMix");
      return null;
    }
    checkMixReadyToUse(paramAudioMix, false);
    AudioFormat localAudioFormat = new AudioFormat.Builder(paramAudioMix.getFormat()).setChannelMask(AudioFormat.inChannelMaskFromOutChannelMask(paramAudioMix.getFormat().getChannelMask())).build();
    return new AudioRecord(new AudioAttributes.Builder().setInternalCapturePreset(8).addTag(addressForTag(paramAudioMix)).build(), localAudioFormat, AudioRecord.getMinBufferSize(paramAudioMix.getFormat().getSampleRate(), 12, paramAudioMix.getFormat().getEncoding()), 0);
  }
  
  public AudioTrack createAudioTrackSource(AudioMix paramAudioMix)
    throws IllegalArgumentException
  {
    if (!policyReadyToUse())
    {
      Log.e("AudioPolicy", "Cannot create AudioTrack source for AudioMix");
      return null;
    }
    checkMixReadyToUse(paramAudioMix, true);
    return new AudioTrack(new AudioAttributes.Builder().setUsage(15).addTag(addressForTag(paramAudioMix)).build(), paramAudioMix.getFormat(), AudioTrack.getMinBufferSize(paramAudioMix.getFormat().getSampleRate(), paramAudioMix.getFormat().getChannelMask(), paramAudioMix.getFormat().getEncoding()), 1, 0);
  }
  
  public AudioPolicyConfig getConfig()
  {
    return this.mConfig;
  }
  
  public int getFocusDuckingBehavior()
  {
    return this.mConfig.mDuckingPolicy;
  }
  
  public int getStatus()
  {
    return this.mStatus;
  }
  
  public boolean hasFocusListener()
  {
    return this.mFocusListener != null;
  }
  
  public int setFocusDuckingBehavior(int paramInt)
    throws IllegalArgumentException, IllegalStateException
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("Invalid ducking behavior " + paramInt);
    }
    synchronized (this.mLock)
    {
      if (this.mStatus != 2) {
        throw new IllegalStateException("Cannot change ducking behavior for unregistered policy");
      }
    }
    if ((paramInt == 1) && (this.mFocusListener == null)) {
      throw new IllegalStateException("Cannot handle ducking without an audio focus listener");
    }
    IAudioService localIAudioService = getService();
    try
    {
      int i = localIAudioService.setFocusPropertiesForPolicy(paramInt, cb());
      if (i == 0) {
        this.mConfig.mDuckingPolicy = paramInt;
      }
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("AudioPolicy", "Dead object in setFocusPropertiesForPolicy for behavior", localRemoteException);
    }
    return -1;
  }
  
  public void setRegistration(String paramString)
  {
    synchronized (this.mLock)
    {
      this.mRegistrationId = paramString;
      this.mConfig.setRegistration(paramString);
      if (paramString != null)
      {
        this.mStatus = 2;
        sendMsg(0);
        return;
      }
      this.mStatus = 1;
    }
  }
  
  public String toLogFriendlyString()
  {
    String str = new String("android.media.audiopolicy.AudioPolicy:\n");
    return str + "config=" + this.mConfig.toLogFriendlyString();
  }
  
  public static abstract class AudioPolicyFocusListener
  {
    public void onAudioFocusGrant(AudioFocusInfo paramAudioFocusInfo, int paramInt) {}
    
    public void onAudioFocusLoss(AudioFocusInfo paramAudioFocusInfo, boolean paramBoolean) {}
  }
  
  public static abstract class AudioPolicyStatusListener
  {
    public void onMixStateUpdate(AudioMix paramAudioMix) {}
    
    public void onStatusChange() {}
  }
  
  public static class Builder
  {
    private Context mContext;
    private AudioPolicy.AudioPolicyFocusListener mFocusListener;
    private Looper mLooper;
    private ArrayList<AudioMix> mMixes = new ArrayList();
    private AudioPolicy.AudioPolicyStatusListener mStatusListener;
    
    public Builder(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public Builder addMix(AudioMix paramAudioMix)
      throws IllegalArgumentException
    {
      if (paramAudioMix == null) {
        throw new IllegalArgumentException("Illegal null AudioMix argument");
      }
      this.mMixes.add(paramAudioMix);
      return this;
    }
    
    public AudioPolicy build()
    {
      if (this.mStatusListener != null)
      {
        Iterator localIterator = this.mMixes.iterator();
        while (localIterator.hasNext())
        {
          AudioMix localAudioMix = (AudioMix)localIterator.next();
          localAudioMix.mCallbackFlags |= 0x1;
        }
      }
      return new AudioPolicy(new AudioPolicyConfig(this.mMixes), this.mContext, this.mLooper, this.mFocusListener, this.mStatusListener, null);
    }
    
    public void setAudioPolicyFocusListener(AudioPolicy.AudioPolicyFocusListener paramAudioPolicyFocusListener)
    {
      this.mFocusListener = paramAudioPolicyFocusListener;
    }
    
    public void setAudioPolicyStatusListener(AudioPolicy.AudioPolicyStatusListener paramAudioPolicyStatusListener)
    {
      this.mStatusListener = paramAudioPolicyStatusListener;
    }
    
    public Builder setLooper(Looper paramLooper)
      throws IllegalArgumentException
    {
      if (paramLooper == null) {
        throw new IllegalArgumentException("Illegal null Looper argument");
      }
      this.mLooper = paramLooper;
      return this;
    }
  }
  
  private class EventHandler
    extends Handler
  {
    public EventHandler(AudioPolicy paramAudioPolicy, Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = false;
      switch (paramMessage.what)
      {
      default: 
        Log.e("AudioPolicy", "Unknown event " + paramMessage.what);
      }
      do
      {
        do
        {
          do
          {
            return;
            AudioPolicy.-wrap0(AudioPolicy.this);
            return;
          } while (AudioPolicy.-get1(AudioPolicy.this) == null);
          AudioPolicy.-get1(AudioPolicy.this).onAudioFocusGrant((AudioFocusInfo)paramMessage.obj, paramMessage.arg1);
          return;
        } while (AudioPolicy.-get1(AudioPolicy.this) == null);
        AudioPolicy.AudioPolicyFocusListener localAudioPolicyFocusListener = AudioPolicy.-get1(AudioPolicy.this);
        AudioFocusInfo localAudioFocusInfo = (AudioFocusInfo)paramMessage.obj;
        if (paramMessage.arg1 != 0) {
          bool = true;
        }
        localAudioPolicyFocusListener.onAudioFocusLoss(localAudioFocusInfo, bool);
        return;
      } while (AudioPolicy.-get2(AudioPolicy.this) == null);
      AudioPolicy.-get2(AudioPolicy.this).onMixStateUpdate((AudioMix)paramMessage.obj);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiopolicy/AudioPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */