package android.media;

import android.app.ActivityThread;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.NioUtils;
import java.util.Iterator;

public class AudioTrack
  extends PlayerBase
  implements AudioRouting
{
  public static final int CHANNEL_COUNT_MAX = ;
  public static final int ERROR = -1;
  public static final int ERROR_BAD_VALUE = -2;
  public static final int ERROR_DEAD_OBJECT = -6;
  public static final int ERROR_INVALID_OPERATION = -3;
  private static final int ERROR_NATIVESETUP_AUDIOSYSTEM = -16;
  private static final int ERROR_NATIVESETUP_INVALIDCHANNELMASK = -17;
  private static final int ERROR_NATIVESETUP_INVALIDFORMAT = -18;
  private static final int ERROR_NATIVESETUP_INVALIDSTREAMTYPE = -19;
  private static final int ERROR_NATIVESETUP_NATIVEINITFAILED = -20;
  public static final int ERROR_WOULD_BLOCK = -7;
  private static final float GAIN_MAX = 1.0F;
  private static final float GAIN_MIN = 0.0F;
  public static final int MODE_STATIC = 0;
  public static final int MODE_STREAM = 1;
  private static final int NATIVE_EVENT_MARKER = 3;
  private static final int NATIVE_EVENT_NEW_POS = 4;
  public static final int PLAYSTATE_PAUSED = 2;
  public static final int PLAYSTATE_PLAYING = 3;
  public static final int PLAYSTATE_STOPPED = 1;
  public static final int STATE_INITIALIZED = 1;
  public static final int STATE_NO_STATIC_DATA = 2;
  public static final int STATE_UNINITIALIZED = 0;
  public static final int SUCCESS = 0;
  private static final int SUPPORTED_OUT_CHANNELS = 7420;
  private static final String TAG = "android.media.AudioTrack";
  public static final int WRITE_BLOCKING = 0;
  public static final int WRITE_NON_BLOCKING = 1;
  private int mAudioFormat;
  private int mAvSyncBytesRemaining = 0;
  private ByteBuffer mAvSyncHeader = null;
  private String[] mCTSKindApps = { "com.android.cts.verifier", "android.media.cts" };
  private int mChannelConfiguration = 4;
  private int mChannelCount = 1;
  private int mChannelIndexMask = 0;
  private int mChannelMask = 4;
  private int mDataLoadMode = 1;
  private NativePositionEventHandlerDelegate mEventHandlerDelegate;
  private final Looper mInitializationLooper;
  private long mJniData;
  private String[] mLakalKindApps = { "com.lakala.android", "com.unionpay.kalefu", "com.huishuaka.credit", "air.mobilepos", "com.iboxpay.minicashbox", "com.bill99.kuaishua", "com.iboxpay.minicashbox", "com.dcsdzficb", "com.dcyiqing", "com.dcyqzf", "com.dczhongcicb", "com.epay.impay.ui.dapaizhifu", "com.epay.impay.ui.jfpal", "remob.com" };
  private int mNativeBufferSizeInBytes = 0;
  private int mNativeBufferSizeInFrames = 0;
  protected long mNativeTrackInJavaObj;
  private int mPlayState = 1;
  private final Object mPlayStateLock = new Object();
  private AudioDeviceInfo mPreferredDevice = null;
  @GuardedBy("mRoutingChangeListeners")
  private ArrayMap<AudioRouting.OnRoutingChangedListener, NativeRoutingEventHandlerDelegate> mRoutingChangeListeners = new ArrayMap();
  private int mSampleRate;
  private int mSessionId = 0;
  private int mState = 0;
  private int mStreamType = 3;
  private String[] mVoipApps = { "com.tencent.mm", "com.skype.rover", "com.whatsapp", "com.alibaba.mobileim", "com.tencent.mobileqqi", "com.tencent.qt.qtx", "com.xiaomi.channele", "com.google.android.talk", "cn.com.fetion", "cn.com.talker", "com.viber.voip", "jp.naver.line.android", "com.tencent.mobileqq", "yuku.luyinji.full", "im.yixin", "com.asiainno.pengpeng", "com.duowan.mobile", "com.immomo.momo", "com.yy.yymeet", "com.yx", "com.facebook.katana" };
  
  public AudioTrack(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    throws IllegalArgumentException
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0);
  }
  
  public AudioTrack(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
    throws IllegalArgumentException
  {
    this(new AudioAttributes.Builder().setLegacyStreamType(paramInt1).build(), new AudioFormat.Builder().setChannelMask(paramInt3).setEncoding(paramInt4).setSampleRate(paramInt2).build(), paramInt5, paramInt6, paramInt7);
  }
  
  AudioTrack(long paramLong)
  {
    super(new AudioAttributes.Builder().build());
    this.mNativeTrackInJavaObj = 0L;
    this.mJniData = 0L;
    Looper localLooper2 = Looper.myLooper();
    Looper localLooper1 = localLooper2;
    if (localLooper2 == null) {
      localLooper1 = Looper.getMainLooper();
    }
    this.mInitializationLooper = localLooper1;
    if (paramLong != 0L)
    {
      deferred_connect(paramLong);
      return;
    }
    this.mState = 0;
  }
  
  public AudioTrack(AudioAttributes paramAudioAttributes, AudioFormat paramAudioFormat, int paramInt1, int paramInt2, int paramInt3)
    throws IllegalArgumentException
  {
    super(paramAudioAttributes);
    if (paramAudioFormat == null) {
      throw new IllegalArgumentException("Illegal null AudioFormat");
    }
    Object localObject = Looper.myLooper();
    paramAudioAttributes = (AudioAttributes)localObject;
    if (localObject == null) {
      paramAudioAttributes = Looper.getMainLooper();
    }
    int i = paramAudioFormat.getSampleRate();
    int j = i;
    if (i == 0) {
      j = 0;
    }
    int k = 0;
    if ((paramAudioFormat.getPropertySetMask() & 0x8) != 0) {
      k = paramAudioFormat.getChannelIndexMask();
    }
    i = 0;
    if ((paramAudioFormat.getPropertySetMask() & 0x4) != 0) {
      i = paramAudioFormat.getChannelMask();
    }
    for (;;)
    {
      int m = 1;
      if ((paramAudioFormat.getPropertySetMask() & 0x1) != 0) {
        m = paramAudioFormat.getEncoding();
      }
      audioParamCheck(j, i, k, m, paramInt2);
      this.mStreamType = -1;
      audioBuffSizeCheck(paramInt1);
      this.mInitializationLooper = paramAudioAttributes;
      if (paramInt3 >= 0) {
        break;
      }
      throw new IllegalArgumentException("Invalid audio session ID: " + paramInt3);
      if (k == 0) {
        i = 12;
      }
    }
    paramAudioAttributes = new int[1];
    paramAudioAttributes[0] = this.mSampleRate;
    paramAudioFormat = new int[1];
    paramAudioFormat[0] = paramInt3;
    localObject = ActivityThread.currentPackageName();
    if ((localObject != null) && (((String)localObject).equals(new String("com.google.android.media.gts")))) {
      AudioSystem.setParameters("mediagts=1");
    }
    paramInt2 = native_setup(new WeakReference(this), this.mAttributes, paramAudioAttributes, this.mChannelMask, this.mChannelIndexMask, this.mAudioFormat, this.mNativeBufferSizeInBytes, this.mDataLoadMode, paramAudioFormat, 0L);
    if (paramInt2 != 0)
    {
      loge("Error code " + paramInt2 + " when initializing AudioTrack.");
      return;
    }
    this.mSampleRate = paramAudioAttributes[0];
    this.mSessionId = paramAudioFormat[0];
    Log.i("android.media.AudioTrack", "bufferSizeInBytes:" + paramInt1 + " mSampleRate:" + this.mSampleRate);
    if ((localObject != null) && (((String)localObject).equals(new String("com.tencent.mm"))) && (this.mSampleRate == 16000) && (paramInt1 == 20608)) {
      AudioSystem.setParameters("wechat16k=1");
    }
    while (this.mDataLoadMode == 0)
    {
      this.mState = 2;
      return;
      AudioSystem.setParameters("wechat16k=0");
    }
    this.mState = 1;
  }
  
  private void audioBuffSizeCheck(int paramInt)
  {
    if (AudioFormat.isEncodingLinearFrames(this.mAudioFormat)) {}
    for (int i = this.mChannelCount * AudioFormat.getBytesPerSample(this.mAudioFormat); (paramInt % i != 0) || (paramInt < 1); i = 1) {
      throw new IllegalArgumentException("Invalid audio buffer size.");
    }
    this.mNativeBufferSizeInBytes = paramInt;
    this.mNativeBufferSizeInFrames = (paramInt / i);
  }
  
  private void audioParamCheck(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (((paramInt1 < 4000) || (paramInt1 > 192000)) && (paramInt1 != 0)) {
      throw new IllegalArgumentException(paramInt1 + "Hz is not a supported sample rate.");
    }
    this.mSampleRate = paramInt1;
    if ((paramInt4 == 13) && (paramInt2 != 12)) {
      throw new IllegalArgumentException("ENCODING_IEC61937 must be configured as CHANNEL_OUT_STEREO");
    }
    this.mChannelConfiguration = paramInt2;
    switch (paramInt2)
    {
    default: 
      if ((paramInt2 == 0) && (paramInt3 != 0)) {
        this.mChannelCount = 0;
      }
      break;
    }
    for (;;)
    {
      this.mChannelIndexMask = paramInt3;
      if (this.mChannelIndexMask == 0) {
        break label275;
      }
      if (((1 << CHANNEL_COUNT_MAX) - 1 & paramInt3) == 0) {
        break;
      }
      throw new IllegalArgumentException("Unsupported channel index configuration " + paramInt3);
      this.mChannelCount = 1;
      this.mChannelMask = 4;
      continue;
      this.mChannelCount = 2;
      this.mChannelMask = 12;
      continue;
      if (!isMultichannelConfigSupported(paramInt2)) {
        throw new IllegalArgumentException("Unsupported channel configuration.");
      }
      this.mChannelMask = paramInt2;
      this.mChannelCount = AudioFormat.channelCountFromOutChannelMask(paramInt2);
    }
    paramInt1 = Integer.bitCount(paramInt3);
    if (this.mChannelCount == 0) {
      this.mChannelCount = paramInt1;
    }
    label275:
    while (this.mChannelCount == paramInt1)
    {
      paramInt1 = paramInt4;
      if (paramInt4 == 1) {
        paramInt1 = 2;
      }
      if (AudioFormat.isPublicEncoding(paramInt1)) {
        break;
      }
      throw new IllegalArgumentException("Unsupported audio encoding.");
    }
    throw new IllegalArgumentException("Channel count must match");
    this.mAudioFormat = paramInt1;
    if ((paramInt5 != 1) && (paramInt5 != 0)) {}
    while ((paramInt5 != 1) && (!AudioFormat.isEncodingLinearPcm(this.mAudioFormat))) {
      throw new IllegalArgumentException("Invalid mode.");
    }
    this.mDataLoadMode = paramInt5;
  }
  
  private void broadcastRoutingChange()
  {
    AudioManager.resetAudioPortGeneration();
    synchronized (this.mRoutingChangeListeners)
    {
      Iterator localIterator = this.mRoutingChangeListeners.values().iterator();
      while (localIterator.hasNext())
      {
        Handler localHandler = ((NativeRoutingEventHandlerDelegate)localIterator.next()).getHandler();
        if (localHandler != null) {
          localHandler.sendEmptyMessage(1000);
        }
      }
    }
  }
  
  private static float clampGainOrLevel(float paramFloat)
  {
    if (Float.isNaN(paramFloat)) {
      throw new IllegalArgumentException();
    }
    float f;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    do
    {
      return f;
      f = paramFloat;
    } while (paramFloat <= 1.0F);
    return 1.0F;
  }
  
  public static float getMaxVolume()
  {
    return 1.0F;
  }
  
  public static int getMinBufferSize(int paramInt1, int paramInt2, int paramInt3)
  {
    switch (paramInt2)
    {
    default: 
      if (!isMultichannelConfigSupported(paramInt2))
      {
        loge("getMinBufferSize(): Invalid channel configuration.");
        return -2;
      }
      break;
    case 2: 
    case 4: 
      paramInt2 = 1;
    }
    while (!AudioFormat.isPublicEncoding(paramInt3))
    {
      loge("getMinBufferSize(): Invalid audio format.");
      return -2;
      paramInt2 = 2;
      continue;
      paramInt2 = AudioFormat.channelCountFromOutChannelMask(paramInt2);
    }
    if ((paramInt1 < 4000) || (paramInt1 > 192000))
    {
      loge("getMinBufferSize(): " + paramInt1 + " Hz is not a supported sample rate.");
      return -2;
    }
    paramInt1 = native_get_min_buff_size(paramInt1, paramInt2, paramInt3);
    if (paramInt1 <= 0)
    {
      loge("getMinBufferSize(): error querying hardware");
      return -1;
    }
    return paramInt1;
  }
  
  public static float getMinVolume()
  {
    return 0.0F;
  }
  
  public static int getNativeOutputSampleRate(int paramInt)
  {
    return native_get_output_sample_rate(paramInt);
  }
  
  private static boolean isMultichannelConfigSupported(int paramInt)
  {
    if ((paramInt & 0x1CFC) != paramInt)
    {
      loge("Channel configuration features unsupported channels");
      return false;
    }
    int i = AudioFormat.channelCountFromOutChannelMask(paramInt);
    if (i > CHANNEL_COUNT_MAX)
    {
      loge("Channel configuration contains too many channels " + i + ">" + CHANNEL_COUNT_MAX);
      return false;
    }
    if ((paramInt & 0xC) != 12)
    {
      loge("Front channels must be present in multichannel configurations");
      return false;
    }
    if (((paramInt & 0xC0) != 0) && ((paramInt & 0xC0) != 192))
    {
      loge("Rear channels can't be used independently");
      return false;
    }
    if (((paramInt & 0x1800) != 0) && ((paramInt & 0x1800) != 6144))
    {
      loge("Side channels can't be used independently");
      return false;
    }
    return true;
  }
  
  private static void logd(String paramString)
  {
    Log.d("android.media.AudioTrack", paramString);
  }
  
  private static void loge(String paramString)
  {
    Log.e("android.media.AudioTrack", paramString);
  }
  
  private final native int native_attachAuxEffect(int paramInt);
  
  private final native void native_disableDeviceCallback();
  
  private final native void native_enableDeviceCallback();
  
  private final native void native_finalize();
  
  private final native void native_flush();
  
  private final native int native_getRoutedDeviceId();
  
  private static native int native_get_FCC_8();
  
  private final native int native_get_buffer_capacity_frames();
  
  private final native int native_get_buffer_size_frames();
  
  private final native int native_get_latency();
  
  private final native int native_get_marker_pos();
  
  private static final native int native_get_min_buff_size(int paramInt1, int paramInt2, int paramInt3);
  
  private static final native int native_get_output_sample_rate(int paramInt);
  
  private final native PlaybackParams native_get_playback_params();
  
  private final native int native_get_playback_rate();
  
  private final native int native_get_pos_update_period();
  
  private final native int native_get_position();
  
  private final native int native_get_timestamp(long[] paramArrayOfLong);
  
  private final native int native_get_underrun_count();
  
  private final native void native_pause();
  
  private final native int native_reload_static();
  
  private final native int native_setAuxEffectSendLevel(float paramFloat);
  
  private final native boolean native_setOutputDevice(int paramInt);
  
  private final native void native_setVolume(float paramFloat1, float paramFloat2);
  
  private final native int native_set_buffer_size_frames(int paramInt);
  
  private final native int native_set_loop(int paramInt1, int paramInt2, int paramInt3);
  
  private final native int native_set_marker_pos(int paramInt);
  
  private final native void native_set_playback_params(PlaybackParams paramPlaybackParams);
  
  private final native int native_set_playback_rate(int paramInt);
  
  private final native int native_set_pos_update_period(int paramInt);
  
  private final native int native_set_position(int paramInt);
  
  private final native int native_setup(Object paramObject1, Object paramObject2, int[] paramArrayOfInt1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt2, long paramLong);
  
  private final native void native_start();
  
  private final native void native_stop();
  
  private final native int native_write_byte(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
  
  private final native int native_write_float(float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
  
  private final native int native_write_native_bytes(Object paramObject, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
  
  private final native int native_write_short(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
  
  private static void postEventFromNative(Object paramObject1, int paramInt1, int paramInt2, int paramInt3, Object paramObject2)
  {
    paramObject1 = (AudioTrack)((WeakReference)paramObject1).get();
    if (paramObject1 == null) {
      return;
    }
    if (paramInt1 == 1000)
    {
      ((AudioTrack)paramObject1).broadcastRoutingChange();
      return;
    }
    paramObject1 = ((AudioTrack)paramObject1).mEventHandlerDelegate;
    if (paramObject1 != null)
    {
      paramObject1 = ((NativePositionEventHandlerDelegate)paramObject1).getHandler();
      if (paramObject1 != null) {
        ((Handler)paramObject1).sendMessage(((Handler)paramObject1).obtainMessage(paramInt1, paramInt2, paramInt3, paramObject2));
      }
    }
  }
  
  private void testDisableNativeRoutingCallbacksLocked()
  {
    if (this.mRoutingChangeListeners.size() == 0) {
      native_disableDeviceCallback();
    }
  }
  
  private void testEnableNativeRoutingCallbacksLocked()
  {
    if (this.mRoutingChangeListeners.size() == 0) {
      native_enableDeviceCallback();
    }
  }
  
  public void addOnRoutingChangedListener(AudioRouting.OnRoutingChangedListener paramOnRoutingChangedListener, Handler paramHandler)
  {
    ArrayMap localArrayMap1 = this.mRoutingChangeListeners;
    if (paramOnRoutingChangedListener != null) {}
    for (;;)
    {
      try
      {
        boolean bool = this.mRoutingChangeListeners.containsKey(paramOnRoutingChangedListener);
        if (bool) {
          return;
        }
        testEnableNativeRoutingCallbacksLocked();
        ArrayMap localArrayMap2 = this.mRoutingChangeListeners;
        if (paramHandler != null) {
          localArrayMap2.put(paramOnRoutingChangedListener, new NativeRoutingEventHandlerDelegate(this, paramOnRoutingChangedListener, paramHandler));
        } else {
          paramHandler = new Handler(this.mInitializationLooper);
        }
      }
      finally {}
    }
  }
  
  @Deprecated
  public void addOnRoutingChangedListener(OnRoutingChangedListener paramOnRoutingChangedListener, Handler paramHandler)
  {
    addOnRoutingChangedListener(paramOnRoutingChangedListener, paramHandler);
  }
  
  public int attachAuxEffect(int paramInt)
  {
    if (this.mState == 0) {
      return -3;
    }
    return native_attachAuxEffect(paramInt);
  }
  
  void deferred_connect(long paramLong)
  {
    if (this.mState != 1)
    {
      int[] arrayOfInt = new int[1];
      arrayOfInt[0] = 0;
      int i = native_setup(new WeakReference(this), null, new int[] { 0 }, 0, 0, 0, 0, 0, arrayOfInt, paramLong);
      if (i != 0)
      {
        loge("Error code " + i + " when initializing AudioTrack.");
        return;
      }
      this.mSessionId = arrayOfInt[0];
      this.mState = 1;
    }
  }
  
  protected void finalize()
  {
    baseRelease();
    native_finalize();
  }
  
  public void flush()
  {
    if (this.mState == 1)
    {
      native_flush();
      this.mAvSyncHeader = null;
      this.mAvSyncBytesRemaining = 0;
    }
  }
  
  public int getAudioFormat()
  {
    return this.mAudioFormat;
  }
  
  public int getAudioSessionId()
  {
    return this.mSessionId;
  }
  
  public int getBufferCapacityInFrames()
  {
    return native_get_buffer_capacity_frames();
  }
  
  public int getBufferSizeInFrames()
  {
    return native_get_buffer_size_frames();
  }
  
  public int getChannelConfiguration()
  {
    return this.mChannelConfiguration;
  }
  
  public int getChannelCount()
  {
    return this.mChannelCount;
  }
  
  public AudioFormat getFormat()
  {
    AudioFormat.Builder localBuilder = new AudioFormat.Builder().setSampleRate(this.mSampleRate).setEncoding(this.mAudioFormat);
    if (this.mChannelConfiguration != 0) {
      localBuilder.setChannelMask(this.mChannelConfiguration);
    }
    if (this.mChannelIndexMask != 0) {
      localBuilder.setChannelIndexMask(this.mChannelIndexMask);
    }
    return localBuilder.build();
  }
  
  public int getLatency()
  {
    return native_get_latency();
  }
  
  @Deprecated
  protected int getNativeFrameCount()
  {
    return native_get_buffer_capacity_frames();
  }
  
  public int getNotificationMarkerPosition()
  {
    return native_get_marker_pos();
  }
  
  public int getPlayState()
  {
    synchronized (this.mPlayStateLock)
    {
      int i = this.mPlayState;
      return i;
    }
  }
  
  public int getPlaybackHeadPosition()
  {
    return native_get_position();
  }
  
  public PlaybackParams getPlaybackParams()
  {
    return native_get_playback_params();
  }
  
  public int getPlaybackRate()
  {
    return native_get_playback_rate();
  }
  
  public int getPositionNotificationPeriod()
  {
    return native_get_pos_update_period();
  }
  
  public AudioDeviceInfo getPreferredDevice()
  {
    try
    {
      AudioDeviceInfo localAudioDeviceInfo = this.mPreferredDevice;
      return localAudioDeviceInfo;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public AudioDeviceInfo getRoutedDevice()
  {
    int j = native_getRoutedDeviceId();
    if (j == 0) {
      return null;
    }
    AudioDeviceInfo[] arrayOfAudioDeviceInfo = AudioManager.getDevicesStatic(2);
    int i = 0;
    while (i < arrayOfAudioDeviceInfo.length)
    {
      if (arrayOfAudioDeviceInfo[i].getId() == j) {
        return arrayOfAudioDeviceInfo[i];
      }
      i += 1;
    }
    return null;
  }
  
  public int getSampleRate()
  {
    return this.mSampleRate;
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  public int getStreamType()
  {
    return this.mStreamType;
  }
  
  public boolean getTimestamp(AudioTimestamp paramAudioTimestamp)
  {
    if (paramAudioTimestamp == null) {
      throw new IllegalArgumentException();
    }
    long[] arrayOfLong = new long[2];
    if (native_get_timestamp(arrayOfLong) != 0) {
      return false;
    }
    paramAudioTimestamp.framePosition = arrayOfLong[0];
    paramAudioTimestamp.nanoTime = arrayOfLong[1];
    return true;
  }
  
  public int getTimestampWithStatus(AudioTimestamp paramAudioTimestamp)
  {
    if (paramAudioTimestamp == null) {
      throw new IllegalArgumentException();
    }
    long[] arrayOfLong = new long[2];
    int i = native_get_timestamp(arrayOfLong);
    paramAudioTimestamp.framePosition = arrayOfLong[0];
    paramAudioTimestamp.nanoTime = arrayOfLong[1];
    return i;
  }
  
  public int getUnderrunCount()
  {
    return native_get_underrun_count();
  }
  
  public final native void native_release();
  
  public void pause()
    throws IllegalStateException
  {
    if (this.mState != 1) {
      throw new IllegalStateException("pause() called on uninitialized AudioTrack.");
    }
    synchronized (this.mPlayStateLock)
    {
      native_pause();
      this.mPlayState = 2;
      return;
    }
  }
  
  public void play()
    throws IllegalStateException
  {
    if (this.mState != 1) {
      throw new IllegalStateException("play() called on uninitialized AudioTrack.");
    }
    baseStart();
    for (;;)
    {
      synchronized (this.mPlayStateLock)
      {
        String str = ActivityThread.currentPackageName();
        Log.i("android.media.AudioTrack", "play() packageName: " + str);
        int k = 0;
        i = 0;
        int j = k;
        if (i < this.mVoipApps.length)
        {
          if ((str != null) && (str.equals(this.mVoipApps[i]))) {
            j = 1;
          }
        }
        else
        {
          if (j == 0) {
            continue;
          }
          AudioSystem.setParameters("isVoipApp=1");
          break label259;
          if (i >= this.mLakalKindApps.length) {
            break label264;
          }
          if ((str == null) || (!str.equals(this.mLakalKindApps[i]))) {
            break label245;
          }
          AudioSystem.setParameters("isLakalKindApp=1");
          break label264;
          if (i < this.mCTSKindApps.length)
          {
            if ((str == null) || (!str.equals(this.mCTSKindApps[i])) || ((this.mSampleRate != 44100) && (this.mSampleRate != 48000))) {
              break label252;
            }
            AudioSystem.setParameters("mediacts=1");
          }
          native_start();
          this.mPlayState = 3;
          return;
        }
        i += 1;
        continue;
        AudioSystem.setParameters("isVoipApp=0");
      }
      label245:
      i += 1;
      continue;
      label252:
      i += 1;
      continue;
      label259:
      int i = 0;
      continue;
      label264:
      i = 0;
    }
  }
  
  int playerSetAuxEffectSendLevel(float paramFloat)
  {
    if (native_setAuxEffectSendLevel(clampGainOrLevel(paramFloat)) == 0) {
      return 0;
    }
    return -1;
  }
  
  void playerSetVolume(float paramFloat1, float paramFloat2)
  {
    native_setVolume(clampGainOrLevel(paramFloat1), clampGainOrLevel(paramFloat2));
  }
  
  public void release()
  {
    try
    {
      stop();
      baseRelease();
      native_release();
      this.mState = 0;
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      for (;;) {}
    }
  }
  
  public int reloadStaticData()
  {
    if ((this.mDataLoadMode == 1) || (this.mState != 1)) {
      return -3;
    }
    return native_reload_static();
  }
  
  public void removeOnRoutingChangedListener(AudioRouting.OnRoutingChangedListener paramOnRoutingChangedListener)
  {
    synchronized (this.mRoutingChangeListeners)
    {
      if (this.mRoutingChangeListeners.containsKey(paramOnRoutingChangedListener)) {
        this.mRoutingChangeListeners.remove(paramOnRoutingChangedListener);
      }
      testDisableNativeRoutingCallbacksLocked();
      return;
    }
  }
  
  @Deprecated
  public void removeOnRoutingChangedListener(OnRoutingChangedListener paramOnRoutingChangedListener)
  {
    removeOnRoutingChangedListener(paramOnRoutingChangedListener);
  }
  
  public int setAuxEffectSendLevel(float paramFloat)
  {
    if (this.mState == 0) {
      return -3;
    }
    return baseSetAuxEffectSendLevel(paramFloat);
  }
  
  public int setBufferSizeInFrames(int paramInt)
  {
    if ((this.mDataLoadMode == 0) || (this.mState == 0)) {
      return -3;
    }
    if (paramInt < 0) {
      return -2;
    }
    return native_set_buffer_size_frames(paramInt);
  }
  
  public int setLoopPoints(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.mDataLoadMode == 1) || (this.mState == 0)) {}
    while (getPlayState() == 3) {
      return -3;
    }
    if (paramInt3 == 0) {
      return native_set_loop(paramInt1, paramInt2, paramInt3);
    }
    if ((paramInt1 < 0) || (paramInt1 >= this.mNativeBufferSizeInFrames)) {}
    for (;;)
    {
      return -2;
      if (paramInt1 < paramInt2) {
        if (paramInt2 <= this.mNativeBufferSizeInFrames) {
          break;
        }
      }
    }
  }
  
  public int setNotificationMarkerPosition(int paramInt)
  {
    if (this.mState == 0) {
      return -3;
    }
    return native_set_marker_pos(paramInt);
  }
  
  public int setPlaybackHeadPosition(int paramInt)
  {
    if ((this.mDataLoadMode == 1) || (this.mState == 0)) {}
    while (getPlayState() == 3) {
      return -3;
    }
    if ((paramInt < 0) || (paramInt > this.mNativeBufferSizeInFrames)) {
      return -2;
    }
    return native_set_position(paramInt);
  }
  
  public void setPlaybackParams(PlaybackParams paramPlaybackParams)
  {
    if (paramPlaybackParams == null) {
      throw new IllegalArgumentException("params is null");
    }
    native_set_playback_params(paramPlaybackParams);
  }
  
  public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener paramOnPlaybackPositionUpdateListener)
  {
    setPlaybackPositionUpdateListener(paramOnPlaybackPositionUpdateListener, null);
  }
  
  public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener paramOnPlaybackPositionUpdateListener, Handler paramHandler)
  {
    if (paramOnPlaybackPositionUpdateListener != null)
    {
      this.mEventHandlerDelegate = new NativePositionEventHandlerDelegate(this, paramOnPlaybackPositionUpdateListener, paramHandler);
      return;
    }
    this.mEventHandlerDelegate = null;
  }
  
  public int setPlaybackRate(int paramInt)
  {
    if (this.mState != 1) {
      return -3;
    }
    if (paramInt <= 0) {
      return -2;
    }
    return native_set_playback_rate(paramInt);
  }
  
  public int setPositionNotificationPeriod(int paramInt)
  {
    if (this.mState == 0) {
      return -3;
    }
    return native_set_pos_update_period(paramInt);
  }
  
  public boolean setPreferredDevice(AudioDeviceInfo paramAudioDeviceInfo)
  {
    int i = 0;
    boolean bool;
    if ((paramAudioDeviceInfo == null) || (paramAudioDeviceInfo.isSink()))
    {
      if (paramAudioDeviceInfo != null) {
        i = paramAudioDeviceInfo.getId();
      }
      bool = native_setOutputDevice(i);
      if (!bool) {}
    }
    try
    {
      this.mPreferredDevice = paramAudioDeviceInfo;
      return bool;
    }
    finally {}
    return false;
  }
  
  @Deprecated
  protected void setState(int paramInt)
  {
    this.mState = paramInt;
  }
  
  @Deprecated
  public int setStereoVolume(float paramFloat1, float paramFloat2)
  {
    if (this.mState == 0) {
      return -3;
    }
    baseSetVolume(paramFloat1, paramFloat2);
    return 0;
  }
  
  public int setVolume(float paramFloat)
  {
    return setStereoVolume(paramFloat, paramFloat);
  }
  
  public void stop()
    throws IllegalStateException
  {
    if (this.mState != 1) {
      throw new IllegalStateException("stop() called on uninitialized AudioTrack.");
    }
    synchronized (this.mPlayStateLock)
    {
      String str = ActivityThread.currentPackageName();
      int i = 0;
      for (;;)
      {
        if (i < this.mLakalKindApps.length)
        {
          if ((str != null) && (str.equals(this.mLakalKindApps[i]))) {
            AudioSystem.setParameters("isLakalKindApp=0");
          }
        }
        else
        {
          Log.i("android.media.AudioTrack", "stop() packageName: " + str + "mSampleRate: " + this.mSampleRate);
          i = 0;
          if (i < this.mCTSKindApps.length)
          {
            if ((str == null) || (!str.equals(this.mCTSKindApps[i]))) {
              break;
            }
            AudioSystem.setParameters("mediacts=0");
          }
          if ((str != null) && (str.equals(new String("com.google.android.media.gts")))) {
            AudioSystem.setParameters("mediagts=0");
          }
          native_stop();
          this.mPlayState = 1;
          this.mAvSyncHeader = null;
          this.mAvSyncBytesRemaining = 0;
          return;
        }
        i += 1;
      }
      i += 1;
    }
  }
  
  public int write(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    boolean bool1 = false;
    if (this.mState == 0)
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() called in invalid state STATE_UNINITIALIZED");
      return -3;
    }
    if ((paramInt2 != 0) && (paramInt2 != 1))
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() called with invalid blocking mode");
      return -2;
    }
    if ((paramByteBuffer == null) || (paramInt1 < 0)) {}
    while (paramInt1 > paramByteBuffer.remaining())
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() called with invalid size (" + paramInt1 + ") value");
      return -2;
    }
    int i;
    int j;
    if (paramByteBuffer.isDirect())
    {
      i = paramByteBuffer.position();
      j = this.mAudioFormat;
      if (paramInt2 == 0) {
        bool1 = true;
      }
    }
    byte[] arrayOfByte;
    int k;
    for (paramInt1 = native_write_native_bytes(paramByteBuffer, i, paramInt1, j, bool1);; paramInt1 = native_write_byte(arrayOfByte, j + i, paramInt1, k, bool1))
    {
      if ((this.mDataLoadMode == 0) && (this.mState == 2) && (paramInt1 > 0)) {
        this.mState = 1;
      }
      if (paramInt1 > 0) {
        paramByteBuffer.position(paramByteBuffer.position() + paramInt1);
      }
      return paramInt1;
      arrayOfByte = NioUtils.unsafeArray(paramByteBuffer);
      i = NioUtils.unsafeArrayOffset(paramByteBuffer);
      j = paramByteBuffer.position();
      k = this.mAudioFormat;
      bool1 = bool2;
      if (paramInt2 == 0) {
        bool1 = true;
      }
    }
  }
  
  public int write(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong)
  {
    if (this.mState == 0)
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() called in invalid state STATE_UNINITIALIZED");
      return -3;
    }
    if ((paramInt2 != 0) && (paramInt2 != 1))
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() called with invalid blocking mode");
      return -2;
    }
    if (this.mDataLoadMode != 1)
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() with timestamp called for non-streaming mode track");
      return -3;
    }
    if ((this.mAttributes.getFlags() & 0x10) == 0)
    {
      Log.d("android.media.AudioTrack", "AudioTrack.write() called on a regular AudioTrack. Ignoring pts...");
      return write(paramByteBuffer, paramInt1, paramInt2);
    }
    if ((paramByteBuffer == null) || (paramInt1 < 0)) {}
    while (paramInt1 > paramByteBuffer.remaining())
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() called with invalid size (" + paramInt1 + ") value");
      return -2;
    }
    if (this.mAvSyncHeader == null)
    {
      this.mAvSyncHeader = ByteBuffer.allocate(16);
      this.mAvSyncHeader.order(ByteOrder.BIG_ENDIAN);
      this.mAvSyncHeader.putInt(1431633921);
      this.mAvSyncHeader.putInt(paramInt1);
      this.mAvSyncHeader.putLong(paramLong);
      this.mAvSyncHeader.position(0);
      this.mAvSyncBytesRemaining = paramInt1;
    }
    if (this.mAvSyncHeader.remaining() != 0)
    {
      int i = write(this.mAvSyncHeader, this.mAvSyncHeader.remaining(), paramInt2);
      if (i < 0)
      {
        Log.e("android.media.AudioTrack", "AudioTrack.write() could not write timestamp header!");
        this.mAvSyncHeader = null;
        this.mAvSyncBytesRemaining = 0;
        return i;
      }
      if (this.mAvSyncHeader.remaining() > 0)
      {
        Log.v("android.media.AudioTrack", "AudioTrack.write() partial timestamp header written.");
        return 0;
      }
    }
    paramInt1 = write(paramByteBuffer, Math.min(this.mAvSyncBytesRemaining, paramInt1), paramInt2);
    if (paramInt1 < 0)
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() could not write audio data!");
      this.mAvSyncHeader = null;
      this.mAvSyncBytesRemaining = 0;
      return paramInt1;
    }
    this.mAvSyncBytesRemaining -= paramInt1;
    if (this.mAvSyncBytesRemaining == 0) {
      this.mAvSyncHeader = null;
    }
    return paramInt1;
  }
  
  public int write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return write(paramArrayOfByte, paramInt1, paramInt2, 0);
  }
  
  public int write(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool = false;
    if ((this.mState == 0) || (this.mAudioFormat == 4)) {
      return -3;
    }
    if ((paramInt3 != 0) && (paramInt3 != 1))
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() called with invalid blocking mode");
      return -2;
    }
    if ((paramArrayOfByte == null) || (paramInt1 < 0)) {}
    while ((paramInt2 < 0) || (paramInt1 + paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length)) {
      return -2;
    }
    int i = this.mAudioFormat;
    if (paramInt3 == 0) {
      bool = true;
    }
    paramInt1 = native_write_byte(paramArrayOfByte, paramInt1, paramInt2, i, bool);
    if ((this.mDataLoadMode == 0) && (this.mState == 2) && (paramInt1 > 0)) {
      this.mState = 1;
    }
    return paramInt1;
  }
  
  public int write(float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool = false;
    if (this.mState == 0)
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() called in invalid state STATE_UNINITIALIZED");
      return -3;
    }
    if (this.mAudioFormat != 4)
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write(float[] ...) requires format ENCODING_PCM_FLOAT");
      return -3;
    }
    if ((paramInt3 != 0) && (paramInt3 != 1))
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() called with invalid blocking mode");
      return -2;
    }
    if ((paramArrayOfFloat == null) || (paramInt1 < 0)) {}
    while ((paramInt2 < 0) || (paramInt1 + paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfFloat.length))
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() called with invalid array, offset, or size");
      return -2;
    }
    int i = this.mAudioFormat;
    if (paramInt3 == 0) {
      bool = true;
    }
    paramInt1 = native_write_float(paramArrayOfFloat, paramInt1, paramInt2, i, bool);
    if ((this.mDataLoadMode == 0) && (this.mState == 2) && (paramInt1 > 0)) {
      this.mState = 1;
    }
    return paramInt1;
  }
  
  public int write(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    return write(paramArrayOfShort, paramInt1, paramInt2, 0);
  }
  
  public int write(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool = false;
    if ((this.mState == 0) || (this.mAudioFormat == 4)) {
      return -3;
    }
    if ((paramInt3 != 0) && (paramInt3 != 1))
    {
      Log.e("android.media.AudioTrack", "AudioTrack.write() called with invalid blocking mode");
      return -2;
    }
    if ((paramArrayOfShort == null) || (paramInt1 < 0)) {}
    while ((paramInt2 < 0) || (paramInt1 + paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfShort.length)) {
      return -2;
    }
    int i = this.mAudioFormat;
    if (paramInt3 == 0) {
      bool = true;
    }
    paramInt1 = native_write_short(paramArrayOfShort, paramInt1, paramInt2, i, bool);
    if ((this.mDataLoadMode == 0) && (this.mState == 2) && (paramInt1 > 0)) {
      this.mState = 1;
    }
    return paramInt1;
  }
  
  public static class Builder
  {
    private AudioAttributes mAttributes;
    private int mBufferSizeInBytes;
    private AudioFormat mFormat;
    private int mMode = 1;
    private int mSessionId = 0;
    
    public AudioTrack build()
      throws UnsupportedOperationException
    {
      if (this.mAttributes == null) {
        this.mAttributes = new AudioAttributes.Builder().setUsage(1).build();
      }
      if (this.mFormat == null) {
        this.mFormat = new AudioFormat.Builder().setChannelMask(12).setEncoding(1).build();
      }
      try
      {
        if ((this.mMode == 1) && (this.mBufferSizeInBytes == 0))
        {
          int i = this.mFormat.getChannelCount();
          localObject = this.mFormat;
          this.mBufferSizeInBytes = (i * AudioFormat.getBytesPerSample(this.mFormat.getEncoding()));
        }
        Object localObject = new AudioTrack(this.mAttributes, this.mFormat, this.mBufferSizeInBytes, this.mMode, this.mSessionId);
        if (((AudioTrack)localObject).getState() == 0) {
          throw new UnsupportedOperationException("Cannot create AudioTrack");
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new UnsupportedOperationException(localIllegalArgumentException.getMessage());
      }
      return localIllegalArgumentException;
    }
    
    public Builder setAudioAttributes(AudioAttributes paramAudioAttributes)
      throws IllegalArgumentException
    {
      if (paramAudioAttributes == null) {
        throw new IllegalArgumentException("Illegal null AudioAttributes argument");
      }
      this.mAttributes = paramAudioAttributes;
      return this;
    }
    
    public Builder setAudioFormat(AudioFormat paramAudioFormat)
      throws IllegalArgumentException
    {
      if (paramAudioFormat == null) {
        throw new IllegalArgumentException("Illegal null AudioFormat argument");
      }
      this.mFormat = paramAudioFormat;
      return this;
    }
    
    public Builder setBufferSizeInBytes(int paramInt)
      throws IllegalArgumentException
    {
      if (paramInt <= 0) {
        throw new IllegalArgumentException("Invalid buffer size " + paramInt);
      }
      this.mBufferSizeInBytes = paramInt;
      return this;
    }
    
    public Builder setSessionId(int paramInt)
      throws IllegalArgumentException
    {
      if ((paramInt != 0) && (paramInt < 1)) {
        throw new IllegalArgumentException("Invalid audio session ID " + paramInt);
      }
      this.mSessionId = paramInt;
      return this;
    }
    
    public Builder setTransferMode(int paramInt)
      throws IllegalArgumentException
    {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("Invalid transfer mode " + paramInt);
      }
      this.mMode = paramInt;
      return this;
    }
  }
  
  private class NativePositionEventHandlerDelegate
  {
    private final Handler mHandler;
    
    NativePositionEventHandlerDelegate(final AudioTrack paramAudioTrack, final AudioTrack.OnPlaybackPositionUpdateListener paramOnPlaybackPositionUpdateListener, Handler paramHandler)
    {
      if (paramHandler != null) {}
      for (this$1 = paramHandler.getLooper(); AudioTrack.this != null; this$1 = AudioTrack.-get0(AudioTrack.this))
      {
        this.mHandler = new Handler(AudioTrack.this)
        {
          public void handleMessage(Message paramAnonymousMessage)
          {
            if (paramAudioTrack == null) {
              return;
            }
            switch (paramAnonymousMessage.what)
            {
            default: 
              AudioTrack.-wrap0("Unknown native event type: " + paramAnonymousMessage.what);
            }
            do
            {
              do
              {
                return;
              } while (paramOnPlaybackPositionUpdateListener == null);
              paramOnPlaybackPositionUpdateListener.onMarkerReached(paramAudioTrack);
              return;
            } while (paramOnPlaybackPositionUpdateListener == null);
            paramOnPlaybackPositionUpdateListener.onPeriodicNotification(paramAudioTrack);
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
  
  private class NativeRoutingEventHandlerDelegate
  {
    private final Handler mHandler;
    
    NativeRoutingEventHandlerDelegate(final AudioTrack paramAudioTrack, final AudioRouting.OnRoutingChangedListener paramOnRoutingChangedListener, Handler paramHandler)
    {
      if (paramHandler != null) {}
      for (this$1 = paramHandler.getLooper(); AudioTrack.this != null; this$1 = AudioTrack.-get0(AudioTrack.this))
      {
        this.mHandler = new Handler(AudioTrack.this)
        {
          public void handleMessage(Message paramAnonymousMessage)
          {
            if (paramAudioTrack == null) {
              return;
            }
            switch (paramAnonymousMessage.what)
            {
            default: 
              AudioTrack.-wrap0("Unknown native event type: " + paramAnonymousMessage.what);
            }
            do
            {
              return;
            } while (paramOnRoutingChangedListener == null);
            paramOnRoutingChangedListener.onRoutingChanged(paramAudioTrack);
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
  
  public static abstract interface OnPlaybackPositionUpdateListener
  {
    public abstract void onMarkerReached(AudioTrack paramAudioTrack);
    
    public abstract void onPeriodicNotification(AudioTrack paramAudioTrack);
  }
  
  @Deprecated
  public static abstract interface OnRoutingChangedListener
    extends AudioRouting.OnRoutingChangedListener
  {
    public void onRoutingChanged(AudioRouting paramAudioRouting)
    {
      if ((paramAudioRouting instanceof AudioTrack)) {
        onRoutingChanged(paramAudioRouting);
      }
    }
    
    public abstract void onRoutingChanged(AudioTrack paramAudioTrack);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */