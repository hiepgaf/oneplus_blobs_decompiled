package android.media;

import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Permission;
import android.util.SeempLog;
import com.android.internal.annotations.GuardedBy;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Set;

public class AudioRecord
  implements AudioRouting
{
  private static final int AUDIORECORD_ERROR_SETUP_INVALIDCHANNELMASK = -17;
  private static final int AUDIORECORD_ERROR_SETUP_INVALIDFORMAT = -18;
  private static final int AUDIORECORD_ERROR_SETUP_INVALIDSOURCE = -19;
  private static final int AUDIORECORD_ERROR_SETUP_NATIVEINITFAILED = -20;
  private static final int AUDIORECORD_ERROR_SETUP_ZEROFRAMECOUNT = -16;
  public static final int ERROR = -1;
  public static final int ERROR_BAD_VALUE = -2;
  public static final int ERROR_DEAD_OBJECT = -6;
  public static final int ERROR_INVALID_OPERATION = -3;
  private static final int NATIVE_EVENT_MARKER = 2;
  private static final int NATIVE_EVENT_NEW_POS = 3;
  public static final int READ_BLOCKING = 0;
  public static final int READ_NON_BLOCKING = 1;
  public static final int RECORDSTATE_RECORDING = 3;
  public static final int RECORDSTATE_STOPPED = 1;
  public static final int STATE_INITIALIZED = 1;
  public static final int STATE_UNINITIALIZED = 0;
  public static final String SUBMIX_FIXED_VOLUME = "fixedVolume";
  public static final int SUCCESS = 0;
  private static final String TAG = "android.media.AudioRecord";
  private AudioAttributes mAudioAttributes;
  private int mAudioFormat;
  private int mChannelCount;
  private int mChannelIndexMask;
  private int mChannelMask;
  private NativeEventHandler mEventHandler = null;
  private final IBinder mICallBack = new Binder();
  private Looper mInitializationLooper = null;
  private boolean mIsPermGranted = true;
  private boolean mIsSubmixFullVolume = false;
  private int mNativeBufferSizeInBytes = 0;
  private long mNativeCallbackCookie;
  private long mNativeDeviceCallback;
  private long mNativeRecorderInJavaObj;
  private OnRecordPositionUpdateListener mPositionListener = null;
  private final Object mPositionListenerLock = new Object();
  private AudioDeviceInfo mPreferredDevice = null;
  private int mRecordSource;
  private int mRecordingState = 1;
  private final Object mRecordingStateLock = new Object();
  @GuardedBy("mRoutingChangeListeners")
  private ArrayMap<AudioRouting.OnRoutingChangedListener, NativeRoutingEventHandlerDelegate> mRoutingChangeListeners = new ArrayMap();
  private int mSampleRate;
  private int mSessionId = 0;
  private int mState = 0;
  
  public AudioRecord(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    throws IllegalArgumentException
  {
    this(new AudioAttributes.Builder().setInternalCapturePreset(paramInt1).build(), new AudioFormat.Builder().setChannelMask(getChannelMaskFromLegacyConfig(paramInt3, true)).setEncoding(paramInt4).setSampleRate(paramInt2).build(), paramInt5, 0);
  }
  
  AudioRecord(long paramLong)
  {
    this.mNativeRecorderInJavaObj = 0L;
    this.mNativeCallbackCookie = 0L;
    this.mNativeDeviceCallback = 0L;
    if (paramLong != 0L)
    {
      deferred_connect(paramLong);
      return;
    }
    this.mState = 0;
  }
  
  public AudioRecord(AudioAttributes paramAudioAttributes, AudioFormat paramAudioFormat, int paramInt1, int paramInt2)
    throws IllegalArgumentException
  {
    this.mRecordingState = 1;
    if (paramAudioAttributes == null) {
      throw new IllegalArgumentException("Illegal null AudioAttributes");
    }
    if (paramAudioFormat == null) {
      throw new IllegalArgumentException("Illegal null AudioFormat");
    }
    Object localObject = Looper.myLooper();
    this.mInitializationLooper = ((Looper)localObject);
    if (localObject == null) {
      this.mInitializationLooper = Looper.getMainLooper();
    }
    if (paramAudioAttributes.getCapturePreset() == 8)
    {
      localObject = new AudioAttributes.Builder();
      Iterator localIterator = paramAudioAttributes.getTags().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (str.equalsIgnoreCase("fixedVolume"))
        {
          this.mIsSubmixFullVolume = true;
          Log.v("android.media.AudioRecord", "Will record from REMOTE_SUBMIX at full fixed volume");
        }
        else
        {
          ((AudioAttributes.Builder)localObject).addTag(str);
        }
      }
      ((AudioAttributes.Builder)localObject).setInternalCapturePreset(paramAudioAttributes.getCapturePreset());
      this.mAudioAttributes = ((AudioAttributes.Builder)localObject).build();
      int j = paramAudioFormat.getSampleRate();
      int i = j;
      if (j == 0) {
        i = 0;
      }
      j = 1;
      if ((paramAudioFormat.getPropertySetMask() & 0x1) != 0) {
        j = paramAudioFormat.getEncoding();
      }
      audioParamCheck(paramAudioAttributes.getCapturePreset(), i, j);
      if ((paramAudioFormat.getPropertySetMask() & 0x8) != 0)
      {
        this.mChannelIndexMask = paramAudioFormat.getChannelIndexMask();
        this.mChannelCount = paramAudioFormat.getChannelCount();
      }
      if ((paramAudioFormat.getPropertySetMask() & 0x4) == 0) {
        break label470;
      }
      this.mChannelMask = getChannelMaskFromLegacyConfig(paramAudioFormat.getChannelMask(), false);
      this.mChannelCount = paramAudioFormat.getChannelCount();
    }
    for (;;)
    {
      audioBuffSizeCheck(paramInt1);
      paramAudioAttributes = new int[1];
      paramAudioAttributes[0] = this.mSampleRate;
      paramAudioFormat = new int[1];
      paramAudioFormat[0] = paramInt2;
      paramInt1 = native_setup(new WeakReference(this), this.mAudioAttributes, paramAudioAttributes, this.mChannelMask, this.mChannelIndexMask, this.mAudioFormat, this.mNativeBufferSizeInBytes, paramAudioFormat, ActivityThread.currentOpPackageName(), 0L);
      if (paramInt1 == 0) {
        break label500;
      }
      loge("Error code " + paramInt1 + " when initializing native AudioRecord object.");
      return;
      this.mAudioAttributes = paramAudioAttributes;
      break;
      label470:
      if (this.mChannelIndexMask == 0)
      {
        this.mChannelMask = getChannelMaskFromLegacyConfig(1, false);
        this.mChannelCount = AudioFormat.channelCountFromInChannelMask(this.mChannelMask);
      }
    }
    label500:
    this.mSampleRate = paramAudioAttributes[0];
    this.mSessionId = paramAudioFormat[0];
    this.mState = 1;
  }
  
  private void audioBuffSizeCheck(int paramInt)
    throws IllegalArgumentException
  {
    if ((paramInt % (this.mChannelCount * AudioFormat.getBytesPerSample(this.mAudioFormat)) != 0) || (paramInt < 1)) {
      throw new IllegalArgumentException("Invalid audio buffer size.");
    }
    this.mNativeBufferSizeInBytes = paramInt;
  }
  
  private void audioParamCheck(int paramInt1, int paramInt2, int paramInt3)
    throws IllegalArgumentException
  {
    if ((paramInt1 < 0) || ((paramInt1 > MediaRecorder.getAudioSourceMax()) && (paramInt1 != 1998) && (paramInt1 != 1999))) {
      throw new IllegalArgumentException("Invalid audio source.");
    }
    this.mRecordSource = paramInt1;
    if (((paramInt2 < 4000) || (paramInt2 > 192000)) && (paramInt2 != 0)) {
      throw new IllegalArgumentException(paramInt2 + "Hz is not a supported sample rate.");
    }
    this.mSampleRate = paramInt2;
    switch (paramInt3)
    {
    default: 
      throw new IllegalArgumentException("Unsupported sample encoding. Should be ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, or ENCODING_PCM_FLOAT.");
    case 1: 
      this.mAudioFormat = 2;
      return;
    }
    this.mAudioFormat = paramInt3;
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
  
  private static int getChannelMaskFromLegacyConfig(int paramInt, boolean paramBoolean)
  {
    int i;
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unsupported channel configuration.");
    case 1: 
    case 2: 
    case 16: 
      i = 16;
    }
    while ((!paramBoolean) && ((paramInt == 2) || (paramInt == 3)))
    {
      throw new IllegalArgumentException("Unsupported deprecated configuration.");
      i = 12;
      continue;
      i = paramInt;
    }
    return i;
  }
  
  public static int getMinBufferSize(int paramInt1, int paramInt2, int paramInt3)
  {
    switch (paramInt2)
    {
    default: 
      loge("getMinBufferSize(): Invalid channel configuration.");
      return -2;
    case 1: 
    case 2: 
    case 16: 
      paramInt2 = 1;
    }
    for (;;)
    {
      paramInt1 = native_get_min_buff_size(paramInt1, paramInt2, paramInt3);
      if (paramInt1 != 0) {
        break;
      }
      return -2;
      paramInt2 = 2;
      continue;
      paramInt2 = 6;
    }
    if (paramInt1 == -1) {
      return -1;
    }
    return paramInt1;
  }
  
  private void handleFullVolumeRec(boolean paramBoolean)
  {
    if (!this.mIsSubmixFullVolume) {
      return;
    }
    IAudioService localIAudioService = IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
    try
    {
      localIAudioService.forceRemoteSubmixFullVolume(paramBoolean, this.mICallBack);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("android.media.AudioRecord", "Error talking to AudioService when handling full submix volume", localRemoteException);
    }
  }
  
  private static void logd(String paramString)
  {
    Log.d("android.media.AudioRecord", paramString);
  }
  
  private static void loge(String paramString)
  {
    Log.e("android.media.AudioRecord", paramString);
  }
  
  private final native void native_disableDeviceCallback();
  
  private final native void native_enableDeviceCallback();
  
  private final native void native_finalize();
  
  private final native int native_getRoutedDeviceId();
  
  private final native int native_get_buffer_size_in_frames();
  
  private final native int native_get_marker_pos();
  
  private static final native int native_get_min_buff_size(int paramInt1, int paramInt2, int paramInt3);
  
  private final native int native_get_pos_update_period();
  
  private final native int native_get_timestamp(AudioTimestamp paramAudioTimestamp, int paramInt);
  
  private final native int native_read_in_byte_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean);
  
  private final native int native_read_in_direct_buffer(Object paramObject, int paramInt, boolean paramBoolean);
  
  private final native int native_read_in_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2, boolean paramBoolean);
  
  private final native int native_read_in_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2, boolean paramBoolean);
  
  private final native boolean native_setInputDevice(int paramInt);
  
  private final native int native_set_marker_pos(int paramInt);
  
  private final native int native_set_pos_update_period(int paramInt);
  
  private final native int native_setup(Object paramObject1, Object paramObject2, int[] paramArrayOfInt1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt2, String paramString, long paramLong);
  
  private final native int native_start(int paramInt1, int paramInt2);
  
  private final native void native_stop();
  
  private static void postEventFromNative(Object paramObject1, int paramInt1, int paramInt2, int paramInt3, Object paramObject2)
  {
    paramObject1 = (AudioRecord)((WeakReference)paramObject1).get();
    if (paramObject1 == null) {
      return;
    }
    if (paramInt1 == 1000)
    {
      ((AudioRecord)paramObject1).broadcastRoutingChange();
      return;
    }
    if (((AudioRecord)paramObject1).mEventHandler != null)
    {
      paramObject2 = ((AudioRecord)paramObject1).mEventHandler.obtainMessage(paramInt1, paramInt2, paramInt3, paramObject2);
      ((AudioRecord)paramObject1).mEventHandler.sendMessage((Message)paramObject2);
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
  
  @Deprecated
  public void addOnRoutingChangedListener(OnRoutingChangedListener paramOnRoutingChangedListener, Handler paramHandler)
  {
    addOnRoutingChangedListener(paramOnRoutingChangedListener, paramHandler);
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
  
  void deferred_connect(long paramLong)
  {
    if (this.mState != 1)
    {
      int[] arrayOfInt = new int[1];
      arrayOfInt[0] = 0;
      WeakReference localWeakReference = new WeakReference(this);
      String str = ActivityThread.currentOpPackageName();
      int i = native_setup(localWeakReference, null, new int[] { 0 }, 0, 0, 0, 0, arrayOfInt, str, paramLong);
      if (i != 0)
      {
        loge("Error code " + i + " when initializing native AudioRecord object.");
        return;
      }
      this.mSessionId = arrayOfInt[0];
      this.mState = 1;
    }
  }
  
  protected void finalize()
  {
    release();
  }
  
  public int getAudioFormat()
  {
    return this.mAudioFormat;
  }
  
  public int getAudioSessionId()
  {
    return this.mSessionId;
  }
  
  public int getAudioSource()
  {
    return this.mRecordSource;
  }
  
  public int getBufferSizeInFrames()
  {
    return native_get_buffer_size_in_frames();
  }
  
  public int getChannelConfiguration()
  {
    return this.mChannelMask;
  }
  
  public int getChannelCount()
  {
    return this.mChannelCount;
  }
  
  public AudioFormat getFormat()
  {
    AudioFormat.Builder localBuilder = new AudioFormat.Builder().setSampleRate(this.mSampleRate).setEncoding(this.mAudioFormat);
    if (this.mChannelMask != 0) {
      localBuilder.setChannelMask(this.mChannelMask);
    }
    if (this.mChannelIndexMask != 0) {
      localBuilder.setChannelIndexMask(this.mChannelIndexMask);
    }
    return localBuilder.build();
  }
  
  public int getNotificationMarkerPosition()
  {
    return native_get_marker_pos();
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
  
  public int getRecordingState()
  {
    synchronized (this.mRecordingStateLock)
    {
      int i = this.mRecordingState;
      return i;
    }
  }
  
  public AudioDeviceInfo getRoutedDevice()
  {
    int j = native_getRoutedDeviceId();
    if (j == 0) {
      return null;
    }
    AudioDeviceInfo[] arrayOfAudioDeviceInfo = AudioManager.getDevicesStatic(1);
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
  
  public int getTimestamp(AudioTimestamp paramAudioTimestamp, int paramInt)
  {
    if ((paramAudioTimestamp == null) || ((paramInt != 1) && (paramInt != 0))) {
      throw new IllegalArgumentException();
    }
    return native_get_timestamp(paramAudioTimestamp, paramInt);
  }
  
  public final native void native_release();
  
  public int read(ByteBuffer paramByteBuffer, int paramInt)
  {
    return read(paramByteBuffer, paramInt, 0);
  }
  
  public int read(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
  {
    boolean bool = true;
    if (this.mState != 1) {
      return -3;
    }
    if ((paramInt2 != 0) && (paramInt2 != 1))
    {
      Log.e("android.media.AudioRecord", "AudioRecord.read() called with invalid blocking mode");
      return -2;
    }
    if ((paramByteBuffer == null) || (paramInt1 < 0)) {
      return -2;
    }
    if (paramInt2 == 0) {}
    for (;;)
    {
      paramInt1 = native_read_in_direct_buffer(paramByteBuffer, paramInt1, bool);
      if (!this.mIsPermGranted) {
        break;
      }
      return paramInt1;
      bool = false;
    }
    return 0;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return read(paramArrayOfByte, paramInt1, paramInt2, 0);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool = true;
    if ((this.mState != 1) || (this.mAudioFormat == 4)) {
      return -3;
    }
    if ((paramInt3 != 0) && (paramInt3 != 1))
    {
      Log.e("android.media.AudioRecord", "AudioRecord.read() called with invalid blocking mode");
      return -2;
    }
    if ((paramArrayOfByte == null) || (paramInt1 < 0)) {}
    while ((paramInt2 < 0) || (paramInt1 + paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length)) {
      return -2;
    }
    if (paramInt3 == 0) {}
    for (;;)
    {
      paramInt1 = native_read_in_byte_array(paramArrayOfByte, paramInt1, paramInt2, bool);
      if (!this.mIsPermGranted) {
        break;
      }
      return paramInt1;
      bool = false;
    }
    return 0;
  }
  
  public int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool = true;
    if (this.mState == 0)
    {
      Log.e("android.media.AudioRecord", "AudioRecord.read() called in invalid state STATE_UNINITIALIZED");
      return -3;
    }
    if (this.mAudioFormat != 4)
    {
      Log.e("android.media.AudioRecord", "AudioRecord.read(float[] ...) requires format ENCODING_PCM_FLOAT");
      return -3;
    }
    if ((paramInt3 != 0) && (paramInt3 != 1))
    {
      Log.e("android.media.AudioRecord", "AudioRecord.read() called with invalid blocking mode");
      return -2;
    }
    if ((paramArrayOfFloat == null) || (paramInt1 < 0)) {}
    while ((paramInt2 < 0) || (paramInt1 + paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfFloat.length)) {
      return -2;
    }
    if (paramInt3 == 0) {}
    for (;;)
    {
      paramInt1 = native_read_in_float_array(paramArrayOfFloat, paramInt1, paramInt2, bool);
      if (!this.mIsPermGranted) {
        break;
      }
      return paramInt1;
      bool = false;
    }
    return 0;
  }
  
  public int read(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    return read(paramArrayOfShort, paramInt1, paramInt2, 0);
  }
  
  public int read(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool = true;
    if ((this.mState != 1) || (this.mAudioFormat == 4)) {
      return -3;
    }
    if ((paramInt3 != 0) && (paramInt3 != 1))
    {
      Log.e("android.media.AudioRecord", "AudioRecord.read() called with invalid blocking mode");
      return -2;
    }
    if ((paramArrayOfShort == null) || (paramInt1 < 0)) {}
    while ((paramInt2 < 0) || (paramInt1 + paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfShort.length)) {
      return -2;
    }
    if (paramInt3 == 0) {}
    for (;;)
    {
      paramInt1 = native_read_in_short_array(paramArrayOfShort, paramInt1, paramInt2, bool);
      if (!this.mIsPermGranted) {
        break;
      }
      return paramInt1;
      bool = false;
    }
    return 0;
  }
  
  public void release()
  {
    try
    {
      stop();
      native_release();
      this.mState = 0;
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      for (;;) {}
    }
  }
  
  @Deprecated
  public void removeOnRoutingChangedListener(OnRoutingChangedListener paramOnRoutingChangedListener)
  {
    removeOnRoutingChangedListener(paramOnRoutingChangedListener);
  }
  
  public void removeOnRoutingChangedListener(AudioRouting.OnRoutingChangedListener paramOnRoutingChangedListener)
  {
    synchronized (this.mRoutingChangeListeners)
    {
      if (this.mRoutingChangeListeners.containsKey(paramOnRoutingChangedListener))
      {
        this.mRoutingChangeListeners.remove(paramOnRoutingChangedListener);
        testDisableNativeRoutingCallbacksLocked();
      }
      return;
    }
  }
  
  public int setNotificationMarkerPosition(int paramInt)
  {
    if (this.mState == 0) {
      return -3;
    }
    return native_set_marker_pos(paramInt);
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
    if ((paramAudioDeviceInfo == null) || (paramAudioDeviceInfo.isSource()))
    {
      if (paramAudioDeviceInfo != null) {
        i = paramAudioDeviceInfo.getId();
      }
      bool = native_setInputDevice(i);
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
  
  public void setRecordPositionUpdateListener(OnRecordPositionUpdateListener paramOnRecordPositionUpdateListener)
  {
    setRecordPositionUpdateListener(paramOnRecordPositionUpdateListener, null);
  }
  
  public void setRecordPositionUpdateListener(OnRecordPositionUpdateListener paramOnRecordPositionUpdateListener, Handler paramHandler)
  {
    for (;;)
    {
      synchronized (this.mPositionListenerLock)
      {
        this.mPositionListener = paramOnRecordPositionUpdateListener;
        if (paramOnRecordPositionUpdateListener != null)
        {
          if (paramHandler != null)
          {
            this.mEventHandler = new NativeEventHandler(this, paramHandler.getLooper());
            return;
          }
          this.mEventHandler = new NativeEventHandler(this, this.mInitializationLooper);
        }
      }
      this.mEventHandler = null;
    }
  }
  
  public void startRecording()
    throws IllegalStateException
  {
    SeempLog.record(70);
    if (OpFeatures.isSupport(new int[] { 12 })) {}
    try
    {
      Context localContext = ActivityThread.currentApplication().getApplicationContext();
      String str = localContext.getApplicationContext().getApplicationInfo().packageName;
      this.mIsPermGranted = new Permission(localContext).requestPermissionAuto("android.permission.RECORD_AUDIO");
      if ((!this.mIsPermGranted) && (str != null))
      {
        boolean bool = Permission.isSpecialHandleForRecordAudio(str);
        if (!bool) {
          break label95;
        }
      }
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;)
      {
        label95:
        Log.e("android.media.AudioRecord", "request permission RECORD_AUDIO fail");
        localNullPointerException.printStackTrace();
      }
      synchronized (this.mRecordingStateLock)
      {
        if (native_start(0, 0) != 0) {
          break label149;
        }
        handleFullVolumeRec(true);
        this.mRecordingState = 3;
        return;
      }
    }
    if (this.mState != 1)
    {
      throw new IllegalStateException("startRecording() called on an uninitialized AudioRecord.");
      throw new IllegalStateException("permission denied");
    }
  }
  
  public void startRecording(MediaSyncEvent paramMediaSyncEvent)
    throws IllegalStateException
  {
    SeempLog.record(70);
    if (this.mState != 1) {
      throw new IllegalStateException("startRecording() called on an uninitialized AudioRecord.");
    }
    synchronized (this.mRecordingStateLock)
    {
      if (native_start(paramMediaSyncEvent.getType(), paramMediaSyncEvent.getAudioSessionId()) == 0)
      {
        handleFullVolumeRec(true);
        this.mRecordingState = 3;
      }
      return;
    }
  }
  
  public void stop()
    throws IllegalStateException
  {
    if (this.mState != 1) {
      throw new IllegalStateException("stop() called on an uninitialized AudioRecord.");
    }
    synchronized (this.mRecordingStateLock)
    {
      handleFullVolumeRec(false);
      native_stop();
      this.mRecordingState = 1;
      return;
    }
  }
  
  public static class Builder
  {
    private AudioAttributes mAttributes;
    private int mBufferSizeInBytes;
    private AudioFormat mFormat;
    private int mSessionId = 0;
    
    public AudioRecord build()
      throws UnsupportedOperationException
    {
      if (this.mFormat == null) {}
      for (this.mFormat = new AudioFormat.Builder().setEncoding(2).setChannelMask(16).build();; this.mFormat = new AudioFormat.Builder(this.mFormat).setChannelMask(16).build()) {
        do
        {
          if (this.mAttributes == null) {
            this.mAttributes = new AudioAttributes.Builder().setInternalCapturePreset(0).build();
          }
          try
          {
            if (this.mBufferSizeInBytes == 0)
            {
              int i = this.mFormat.getChannelCount();
              localObject = this.mFormat;
              this.mBufferSizeInBytes = (i * AudioFormat.getBytesPerSample(this.mFormat.getEncoding()));
            }
            Object localObject = new AudioRecord(this.mAttributes, this.mFormat, this.mBufferSizeInBytes, this.mSessionId);
            if (((AudioRecord)localObject).getState() != 0) {
              break;
            }
            throw new UnsupportedOperationException("Cannot create AudioRecord");
          }
          catch (IllegalArgumentException localIllegalArgumentException)
          {
            throw new UnsupportedOperationException(localIllegalArgumentException.getMessage());
          }
          if (this.mFormat.getEncoding() == 0) {
            this.mFormat = new AudioFormat.Builder(this.mFormat).setEncoding(2).build();
          }
        } while ((this.mFormat.getChannelMask() != 0) || (this.mFormat.getChannelIndexMask() != 0));
      }
      return localIllegalArgumentException;
    }
    
    public Builder setAudioAttributes(AudioAttributes paramAudioAttributes)
      throws IllegalArgumentException
    {
      if (paramAudioAttributes == null) {
        throw new IllegalArgumentException("Illegal null AudioAttributes argument");
      }
      if (paramAudioAttributes.getCapturePreset() == -1) {
        throw new IllegalArgumentException("No valid capture preset in AudioAttributes argument");
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
    
    public Builder setAudioSource(int paramInt)
      throws IllegalArgumentException
    {
      if ((paramInt < 0) || (paramInt > MediaRecorder.getAudioSourceMax())) {
        throw new IllegalArgumentException("Invalid audio source " + paramInt);
      }
      this.mAttributes = new AudioAttributes.Builder().setInternalCapturePreset(paramInt).build();
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
      if (paramInt < 0) {
        throw new IllegalArgumentException("Invalid session ID " + paramInt);
      }
      this.mSessionId = paramInt;
      return this;
    }
  }
  
  private class NativeEventHandler
    extends Handler
  {
    private final AudioRecord mAudioRecord;
    
    NativeEventHandler(AudioRecord paramAudioRecord, Looper paramLooper)
    {
      super();
      this.mAudioRecord = paramAudioRecord;
    }
    
    public void handleMessage(Message paramMessage)
    {
      AudioRecord.OnRecordPositionUpdateListener localOnRecordPositionUpdateListener;
      do
      {
        do
        {
          synchronized (AudioRecord.-get2(AudioRecord.this))
          {
            localOnRecordPositionUpdateListener = AudioRecord.-get1(this.mAudioRecord);
            switch (paramMessage.what)
            {
            default: 
              AudioRecord.-wrap0("Unknown native event type: " + paramMessage.what);
              return;
            }
          }
        } while (localOnRecordPositionUpdateListener == null);
        localOnRecordPositionUpdateListener.onMarkerReached(this.mAudioRecord);
        return;
      } while (localOnRecordPositionUpdateListener == null);
      localOnRecordPositionUpdateListener.onPeriodicNotification(this.mAudioRecord);
    }
  }
  
  private class NativeRoutingEventHandlerDelegate
  {
    private final Handler mHandler;
    
    NativeRoutingEventHandlerDelegate(final AudioRecord paramAudioRecord, final AudioRouting.OnRoutingChangedListener paramOnRoutingChangedListener, Handler paramHandler)
    {
      if (paramHandler != null) {}
      for (this$1 = paramHandler.getLooper(); AudioRecord.this != null; this$1 = AudioRecord.-get0(AudioRecord.this))
      {
        this.mHandler = new Handler(AudioRecord.this)
        {
          public void handleMessage(Message paramAnonymousMessage)
          {
            if (paramAudioRecord == null) {
              return;
            }
            switch (paramAnonymousMessage.what)
            {
            default: 
              AudioRecord.-wrap0("Unknown native event type: " + paramAnonymousMessage.what);
            }
            do
            {
              return;
            } while (paramOnRoutingChangedListener == null);
            paramOnRoutingChangedListener.onRoutingChanged(paramAudioRecord);
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
  
  public static abstract interface OnRecordPositionUpdateListener
  {
    public abstract void onMarkerReached(AudioRecord paramAudioRecord);
    
    public abstract void onPeriodicNotification(AudioRecord paramAudioRecord);
  }
  
  @Deprecated
  public static abstract interface OnRoutingChangedListener
    extends AudioRouting.OnRoutingChangedListener
  {
    public abstract void onRoutingChanged(AudioRecord paramAudioRecord);
    
    public void onRoutingChanged(AudioRouting paramAudioRouting)
    {
      if ((paramAudioRouting instanceof AudioRecord)) {
        onRoutingChanged(paramAudioRouting);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */