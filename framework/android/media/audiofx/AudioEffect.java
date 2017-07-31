package android.media.audiofx;

import android.app.ActivityThread;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class AudioEffect
{
  public static final String ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION = "android.media.action.CLOSE_AUDIO_EFFECT_CONTROL_SESSION";
  public static final String ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL = "android.media.action.DISPLAY_AUDIO_EFFECT_CONTROL_PANEL";
  public static final String ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION = "android.media.action.OPEN_AUDIO_EFFECT_CONTROL_SESSION";
  public static final int ALREADY_EXISTS = -2;
  public static final int CONTENT_TYPE_GAME = 2;
  public static final int CONTENT_TYPE_MOVIE = 1;
  public static final int CONTENT_TYPE_MUSIC = 0;
  public static final int CONTENT_TYPE_VOICE = 3;
  public static final String EFFECT_AUXILIARY = "Auxiliary";
  public static final String EFFECT_INSERT = "Insert";
  public static final String EFFECT_PRE_PROCESSING = "Pre Processing";
  public static final UUID EFFECT_TYPE_AEC;
  public static final UUID EFFECT_TYPE_AGC;
  public static final UUID EFFECT_TYPE_BASS_BOOST;
  public static final UUID EFFECT_TYPE_ENV_REVERB;
  public static final UUID EFFECT_TYPE_EQUALIZER;
  public static final UUID EFFECT_TYPE_LOUDNESS_ENHANCER = UUID.fromString("fe3199be-aed0-413f-87bb-11260eb63cf1");
  public static final UUID EFFECT_TYPE_NS;
  public static final UUID EFFECT_TYPE_NULL = UUID.fromString("ec7178ec-e5e1-4432-a3f4-4657e6795210");
  public static final UUID EFFECT_TYPE_PRESET_REVERB;
  public static final UUID EFFECT_TYPE_VIRTUALIZER;
  public static final int ERROR = -1;
  public static final int ERROR_BAD_VALUE = -4;
  public static final int ERROR_DEAD_OBJECT = -7;
  public static final int ERROR_INVALID_OPERATION = -5;
  public static final int ERROR_NO_INIT = -3;
  public static final int ERROR_NO_MEMORY = -6;
  public static final String EXTRA_AUDIO_SESSION = "android.media.extra.AUDIO_SESSION";
  public static final String EXTRA_CONTENT_TYPE = "android.media.extra.CONTENT_TYPE";
  public static final String EXTRA_PACKAGE_NAME = "android.media.extra.PACKAGE_NAME";
  public static final int NATIVE_EVENT_CONTROL_STATUS = 0;
  public static final int NATIVE_EVENT_ENABLED_STATUS = 1;
  public static final int NATIVE_EVENT_PARAMETER_CHANGED = 2;
  public static final int STATE_INITIALIZED = 1;
  public static final int STATE_UNINITIALIZED = 0;
  public static final int SUCCESS = 0;
  private static final String TAG = "AudioEffect-JAVA";
  private OnControlStatusChangeListener mControlChangeStatusListener = null;
  private Descriptor mDescriptor;
  private OnEnableStatusChangeListener mEnableStatusChangeListener = null;
  private int mId;
  private long mJniData;
  public final Object mListenerLock = new Object();
  private long mNativeAudioEffect;
  public NativeEventHandler mNativeEventHandler = null;
  private OnParameterChangeListener mParameterChangeListener = null;
  private int mState = 0;
  private final Object mStateLock = new Object();
  
  static
  {
    System.loadLibrary("audioeffect_jni");
    native_init();
    EFFECT_TYPE_ENV_REVERB = UUID.fromString("c2e5d5f0-94bd-4763-9cac-4e234d06839e");
    EFFECT_TYPE_PRESET_REVERB = UUID.fromString("47382d60-ddd8-11db-bf3a-0002a5d5c51b");
    EFFECT_TYPE_EQUALIZER = UUID.fromString("0bed4300-ddd6-11db-8f34-0002a5d5c51b");
    EFFECT_TYPE_BASS_BOOST = UUID.fromString("0634f220-ddd4-11db-a0fc-0002a5d5c51b");
    EFFECT_TYPE_VIRTUALIZER = UUID.fromString("37cc2c00-dddd-11db-8577-0002a5d5c51b");
    EFFECT_TYPE_AGC = UUID.fromString("0a8abfe0-654c-11e0-ba26-0002a5d5c51b");
    EFFECT_TYPE_AEC = UUID.fromString("7b491460-8d4d-11e0-bd61-0002a5d5c51b");
    EFFECT_TYPE_NS = UUID.fromString("58b4b260-8e06-11e0-aa8e-0002a5d5c51b");
  }
  
  public AudioEffect(UUID arg1, UUID paramUUID2, int paramInt1, int paramInt2)
    throws IllegalArgumentException, UnsupportedOperationException, RuntimeException
  {
    int[] arrayOfInt = new int[1];
    Descriptor[] arrayOfDescriptor = new Descriptor[1];
    paramInt1 = native_setup(new WeakReference(this), ???.toString(), paramUUID2.toString(), paramInt1, paramInt2, arrayOfInt, arrayOfDescriptor, ActivityThread.currentOpPackageName());
    if ((paramInt1 != 0) && (paramInt1 != -2))
    {
      Log.e("AudioEffect-JAVA", "Error code " + paramInt1 + " when initializing AudioEffect.");
      switch (paramInt1)
      {
      default: 
        throw new RuntimeException("Cannot initialize effect engine for type: " + ??? + " Error: " + paramInt1);
      case -4: 
        throw new IllegalArgumentException("Effect type: " + ??? + " not supported.");
      }
      throw new UnsupportedOperationException("Effect library not loaded");
    }
    this.mId = arrayOfInt[0];
    this.mDescriptor = arrayOfDescriptor[0];
    synchronized (this.mStateLock)
    {
      this.mState = 1;
      return;
    }
  }
  
  public static int byteArrayToInt(byte[] paramArrayOfByte)
  {
    return byteArrayToInt(paramArrayOfByte, 0);
  }
  
  public static int byteArrayToInt(byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte = ByteBuffer.wrap(paramArrayOfByte);
    paramArrayOfByte.order(ByteOrder.nativeOrder());
    return paramArrayOfByte.getInt(paramInt);
  }
  
  public static short byteArrayToShort(byte[] paramArrayOfByte)
  {
    return byteArrayToShort(paramArrayOfByte, 0);
  }
  
  public static short byteArrayToShort(byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte = ByteBuffer.wrap(paramArrayOfByte);
    paramArrayOfByte.order(ByteOrder.nativeOrder());
    return paramArrayOfByte.getShort(paramInt);
  }
  
  public static byte[] concatArrays(byte[]... paramVarArgs)
  {
    int j = 0;
    int k = paramVarArgs.length;
    int i = 0;
    while (i < k)
    {
      j += paramVarArgs[i].length;
      i += 1;
    }
    byte[] arrayOfByte1 = new byte[j];
    j = 0;
    k = paramVarArgs.length;
    i = 0;
    while (i < k)
    {
      byte[] arrayOfByte2 = paramVarArgs[i];
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, j, arrayOfByte2.length);
      j += arrayOfByte2.length;
      i += 1;
    }
    return arrayOfByte1;
  }
  
  private void createNativeEventHandler()
  {
    Looper localLooper = Looper.myLooper();
    if (localLooper != null)
    {
      this.mNativeEventHandler = new NativeEventHandler(this, localLooper);
      return;
    }
    localLooper = Looper.getMainLooper();
    if (localLooper != null)
    {
      this.mNativeEventHandler = new NativeEventHandler(this, localLooper);
      return;
    }
    this.mNativeEventHandler = null;
  }
  
  public static byte[] intToByteArray(int paramInt)
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(4);
    localByteBuffer.order(ByteOrder.nativeOrder());
    localByteBuffer.putInt(paramInt);
    return localByteBuffer.array();
  }
  
  public static boolean isEffectTypeAvailable(UUID paramUUID)
  {
    Descriptor[] arrayOfDescriptor = queryEffects();
    if (arrayOfDescriptor == null) {
      return false;
    }
    int i = 0;
    while (i < arrayOfDescriptor.length)
    {
      if (arrayOfDescriptor[i].type.equals(paramUUID)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static boolean isError(int paramInt)
  {
    boolean bool = false;
    if (paramInt < 0) {
      bool = true;
    }
    return bool;
  }
  
  private final native int native_command(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, int paramInt3, byte[] paramArrayOfByte2);
  
  private final native void native_finalize();
  
  private final native boolean native_getEnabled();
  
  private final native int native_getParameter(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2);
  
  private final native boolean native_hasControl();
  
  private static final native void native_init();
  
  private static native Object[] native_query_effects();
  
  private static native Object[] native_query_pre_processing(int paramInt);
  
  private final native void native_release();
  
  private final native int native_setEnabled(boolean paramBoolean);
  
  private final native int native_setParameter(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2);
  
  private final native int native_setup(Object paramObject, String paramString1, String paramString2, int paramInt1, int paramInt2, int[] paramArrayOfInt, Object[] paramArrayOfObject, String paramString3);
  
  private static void postEventFromNative(Object paramObject1, int paramInt1, int paramInt2, int paramInt3, Object paramObject2)
  {
    paramObject1 = (AudioEffect)((WeakReference)paramObject1).get();
    if (paramObject1 == null) {
      return;
    }
    if (((AudioEffect)paramObject1).mNativeEventHandler != null)
    {
      paramObject2 = ((AudioEffect)paramObject1).mNativeEventHandler.obtainMessage(paramInt1, paramInt2, paramInt3, paramObject2);
      ((AudioEffect)paramObject1).mNativeEventHandler.sendMessage((Message)paramObject2);
    }
  }
  
  public static Descriptor[] queryEffects()
  {
    return (Descriptor[])native_query_effects();
  }
  
  public static Descriptor[] queryPreProcessings(int paramInt)
  {
    return (Descriptor[])native_query_pre_processing(paramInt);
  }
  
  public static byte[] shortToByteArray(short paramShort)
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(2);
    localByteBuffer.order(ByteOrder.nativeOrder());
    localByteBuffer.putShort(paramShort);
    return localByteBuffer.array();
  }
  
  public void checkState(String paramString)
    throws IllegalStateException
  {
    synchronized (this.mStateLock)
    {
      if (this.mState != 1) {
        throw new IllegalStateException(paramString + " called on uninitialized AudioEffect.");
      }
    }
  }
  
  public void checkStatus(int paramInt)
  {
    if (isError(paramInt))
    {
      switch (paramInt)
      {
      default: 
        throw new RuntimeException("AudioEffect: set/get parameter error");
      case -4: 
        throw new IllegalArgumentException("AudioEffect: bad parameter value");
      }
      throw new UnsupportedOperationException("AudioEffect: invalid parameter operation");
    }
  }
  
  public int command(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws IllegalStateException
  {
    checkState("command()");
    return native_command(paramInt, paramArrayOfByte1.length, paramArrayOfByte1, paramArrayOfByte2.length, paramArrayOfByte2);
  }
  
  protected void finalize()
  {
    native_finalize();
  }
  
  public Descriptor getDescriptor()
    throws IllegalStateException
  {
    checkState("getDescriptor()");
    return this.mDescriptor;
  }
  
  public boolean getEnabled()
    throws IllegalStateException
  {
    checkState("getEnabled()");
    return native_getEnabled();
  }
  
  public int getId()
    throws IllegalStateException
  {
    checkState("getId()");
    return this.mId;
  }
  
  public int getParameter(int paramInt, byte[] paramArrayOfByte)
    throws IllegalStateException
  {
    return getParameter(intToByteArray(paramInt), paramArrayOfByte);
  }
  
  public int getParameter(int paramInt, int[] paramArrayOfInt)
    throws IllegalStateException
  {
    if (paramArrayOfInt.length > 2) {
      return -4;
    }
    byte[] arrayOfByte1 = intToByteArray(paramInt);
    byte[] arrayOfByte2 = new byte[paramArrayOfInt.length * 4];
    paramInt = getParameter(arrayOfByte1, arrayOfByte2);
    if ((paramInt == 4) || (paramInt == 8))
    {
      paramArrayOfInt[0] = byteArrayToInt(arrayOfByte2);
      if (paramInt == 8) {
        paramArrayOfInt[1] = byteArrayToInt(arrayOfByte2, 4);
      }
      return paramInt / 4;
    }
    return -1;
  }
  
  public int getParameter(int paramInt, short[] paramArrayOfShort)
    throws IllegalStateException
  {
    if (paramArrayOfShort.length > 2) {
      return -4;
    }
    byte[] arrayOfByte1 = intToByteArray(paramInt);
    byte[] arrayOfByte2 = new byte[paramArrayOfShort.length * 2];
    paramInt = getParameter(arrayOfByte1, arrayOfByte2);
    if ((paramInt == 2) || (paramInt == 4))
    {
      paramArrayOfShort[0] = byteArrayToShort(arrayOfByte2);
      if (paramInt == 4) {
        paramArrayOfShort[1] = byteArrayToShort(arrayOfByte2, 2);
      }
      return paramInt / 2;
    }
    return -1;
  }
  
  public int getParameter(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws IllegalStateException
  {
    checkState("getParameter()");
    return native_getParameter(paramArrayOfByte1.length, paramArrayOfByte1, paramArrayOfByte2.length, paramArrayOfByte2);
  }
  
  public int getParameter(int[] paramArrayOfInt, byte[] paramArrayOfByte)
    throws IllegalStateException
  {
    if (paramArrayOfInt.length > 2) {
      return -4;
    }
    byte[] arrayOfByte2 = intToByteArray(paramArrayOfInt[0]);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (paramArrayOfInt.length > 1) {
      arrayOfByte1 = concatArrays(new byte[][] { arrayOfByte2, intToByteArray(paramArrayOfInt[1]) });
    }
    return getParameter(arrayOfByte1, paramArrayOfByte);
  }
  
  public int getParameter(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    throws IllegalStateException
  {
    if ((paramArrayOfInt1.length > 2) || (paramArrayOfInt2.length > 2)) {
      return -4;
    }
    byte[] arrayOfByte2 = intToByteArray(paramArrayOfInt1[0]);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (paramArrayOfInt1.length > 1) {
      arrayOfByte1 = concatArrays(new byte[][] { arrayOfByte2, intToByteArray(paramArrayOfInt1[1]) });
    }
    paramArrayOfInt1 = new byte[paramArrayOfInt2.length * 4];
    int i = getParameter(arrayOfByte1, paramArrayOfInt1);
    if ((i == 4) || (i == 8))
    {
      paramArrayOfInt2[0] = byteArrayToInt(paramArrayOfInt1);
      if (i == 8) {
        paramArrayOfInt2[1] = byteArrayToInt(paramArrayOfInt1, 4);
      }
      return i / 4;
    }
    return -1;
  }
  
  public int getParameter(int[] paramArrayOfInt, short[] paramArrayOfShort)
    throws IllegalStateException
  {
    if ((paramArrayOfInt.length > 2) || (paramArrayOfShort.length > 2)) {
      return -4;
    }
    byte[] arrayOfByte2 = intToByteArray(paramArrayOfInt[0]);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (paramArrayOfInt.length > 1) {
      arrayOfByte1 = concatArrays(new byte[][] { arrayOfByte2, intToByteArray(paramArrayOfInt[1]) });
    }
    paramArrayOfInt = new byte[paramArrayOfShort.length * 2];
    int i = getParameter(arrayOfByte1, paramArrayOfInt);
    if ((i == 2) || (i == 4))
    {
      paramArrayOfShort[0] = byteArrayToShort(paramArrayOfInt);
      if (i == 4) {
        paramArrayOfShort[1] = byteArrayToShort(paramArrayOfInt, 2);
      }
      return i / 2;
    }
    return -1;
  }
  
  public boolean hasControl()
    throws IllegalStateException
  {
    checkState("hasControl()");
    return native_hasControl();
  }
  
  public void release()
  {
    synchronized (this.mStateLock)
    {
      native_release();
      this.mState = 0;
      return;
    }
  }
  
  public void setControlStatusListener(OnControlStatusChangeListener paramOnControlStatusChangeListener)
  {
    synchronized (this.mListenerLock)
    {
      this.mControlChangeStatusListener = paramOnControlStatusChangeListener;
      if ((paramOnControlStatusChangeListener != null) && (this.mNativeEventHandler == null)) {
        createNativeEventHandler();
      }
      return;
    }
  }
  
  public void setEnableStatusListener(OnEnableStatusChangeListener paramOnEnableStatusChangeListener)
  {
    synchronized (this.mListenerLock)
    {
      this.mEnableStatusChangeListener = paramOnEnableStatusChangeListener;
      if ((paramOnEnableStatusChangeListener != null) && (this.mNativeEventHandler == null)) {
        createNativeEventHandler();
      }
      return;
    }
  }
  
  public int setEnabled(boolean paramBoolean)
    throws IllegalStateException
  {
    checkState("setEnabled()");
    return native_setEnabled(paramBoolean);
  }
  
  public int setParameter(int paramInt1, int paramInt2)
    throws IllegalStateException
  {
    return setParameter(intToByteArray(paramInt1), intToByteArray(paramInt2));
  }
  
  public int setParameter(int paramInt, short paramShort)
    throws IllegalStateException
  {
    return setParameter(intToByteArray(paramInt), shortToByteArray(paramShort));
  }
  
  public int setParameter(int paramInt, byte[] paramArrayOfByte)
    throws IllegalStateException
  {
    return setParameter(intToByteArray(paramInt), paramArrayOfByte);
  }
  
  public int setParameter(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws IllegalStateException
  {
    checkState("setParameter()");
    return native_setParameter(paramArrayOfByte1.length, paramArrayOfByte1, paramArrayOfByte2.length, paramArrayOfByte2);
  }
  
  public int setParameter(int[] paramArrayOfInt, byte[] paramArrayOfByte)
    throws IllegalStateException
  {
    if (paramArrayOfInt.length > 2) {
      return -4;
    }
    byte[] arrayOfByte2 = intToByteArray(paramArrayOfInt[0]);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (paramArrayOfInt.length > 1) {
      arrayOfByte1 = concatArrays(new byte[][] { arrayOfByte2, intToByteArray(paramArrayOfInt[1]) });
    }
    return setParameter(arrayOfByte1, paramArrayOfByte);
  }
  
  public int setParameter(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    throws IllegalStateException
  {
    if ((paramArrayOfInt1.length > 2) || (paramArrayOfInt2.length > 2)) {
      return -4;
    }
    byte[] arrayOfByte2 = intToByteArray(paramArrayOfInt1[0]);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (paramArrayOfInt1.length > 1) {
      arrayOfByte1 = concatArrays(new byte[][] { arrayOfByte2, intToByteArray(paramArrayOfInt1[1]) });
    }
    arrayOfByte2 = intToByteArray(paramArrayOfInt2[0]);
    paramArrayOfInt1 = arrayOfByte2;
    if (paramArrayOfInt2.length > 1) {
      paramArrayOfInt1 = concatArrays(new byte[][] { arrayOfByte2, intToByteArray(paramArrayOfInt2[1]) });
    }
    return setParameter(arrayOfByte1, paramArrayOfInt1);
  }
  
  public int setParameter(int[] paramArrayOfInt, short[] paramArrayOfShort)
    throws IllegalStateException
  {
    if ((paramArrayOfInt.length > 2) || (paramArrayOfShort.length > 2)) {
      return -4;
    }
    byte[] arrayOfByte2 = intToByteArray(paramArrayOfInt[0]);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (paramArrayOfInt.length > 1) {
      arrayOfByte1 = concatArrays(new byte[][] { arrayOfByte2, intToByteArray(paramArrayOfInt[1]) });
    }
    arrayOfByte2 = shortToByteArray(paramArrayOfShort[0]);
    paramArrayOfInt = arrayOfByte2;
    if (paramArrayOfShort.length > 1) {
      paramArrayOfInt = concatArrays(new byte[][] { arrayOfByte2, shortToByteArray(paramArrayOfShort[1]) });
    }
    return setParameter(arrayOfByte1, paramArrayOfInt);
  }
  
  public void setParameterListener(OnParameterChangeListener paramOnParameterChangeListener)
  {
    synchronized (this.mListenerLock)
    {
      this.mParameterChangeListener = paramOnParameterChangeListener;
      if ((paramOnParameterChangeListener != null) && (this.mNativeEventHandler == null)) {
        createNativeEventHandler();
      }
      return;
    }
  }
  
  public static class Descriptor
  {
    public String connectMode;
    public String implementor;
    public String name;
    public UUID type;
    public UUID uuid;
    
    public Descriptor() {}
    
    public Descriptor(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    {
      this.type = UUID.fromString(paramString1);
      this.uuid = UUID.fromString(paramString2);
      this.connectMode = paramString3;
      this.name = paramString4;
      this.implementor = paramString5;
    }
  }
  
  private class NativeEventHandler
    extends Handler
  {
    private AudioEffect mAudioEffect;
    
    public NativeEventHandler(AudioEffect paramAudioEffect, Looper paramLooper)
    {
      super();
      this.mAudioEffect = paramAudioEffect;
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool2 = true;
      boolean bool1 = true;
      if (this.mAudioEffect == null) {
        return;
      }
      switch (paramMessage.what)
      {
      default: 
        Log.e("AudioEffect-JAVA", "handleMessage() Unknown event type: " + paramMessage.what);
      }
      for (;;)
      {
        return;
        for (;;)
        {
          synchronized (AudioEffect.this.mListenerLock)
          {
            ??? = AudioEffect.-get1(this.mAudioEffect);
            if (??? == null) {
              break;
            }
            ??? = this.mAudioEffect;
            if (paramMessage.arg1 != 0)
            {
              ((AudioEffect.OnEnableStatusChangeListener)???).onEnableStatusChange((AudioEffect)???, bool1);
              return;
            }
          }
          bool1 = false;
        }
        for (;;)
        {
          synchronized (AudioEffect.this.mListenerLock)
          {
            ??? = AudioEffect.-get0(this.mAudioEffect);
            if (??? == null) {
              break;
            }
            ??? = this.mAudioEffect;
            if (paramMessage.arg1 != 0)
            {
              bool1 = bool2;
              ((AudioEffect.OnControlStatusChangeListener)???).onControlStatusChange((AudioEffect)???, bool1);
              return;
            }
          }
          bool1 = false;
        }
        synchronized (AudioEffect.this.mListenerLock)
        {
          ??? = AudioEffect.-get2(this.mAudioEffect);
          if (??? == null) {
            continue;
          }
          int i = paramMessage.arg1;
          paramMessage = (byte[])paramMessage.obj;
          int j = AudioEffect.byteArrayToInt(paramMessage, 0);
          int k = AudioEffect.byteArrayToInt(paramMessage, 4);
          int m = AudioEffect.byteArrayToInt(paramMessage, 8);
          ??? = new byte[k];
          byte[] arrayOfByte = new byte[m];
          System.arraycopy(paramMessage, 12, (byte[])???, 0, k);
          System.arraycopy(paramMessage, i, arrayOfByte, 0, m);
          ((AudioEffect.OnParameterChangeListener)???).onParameterChange(this.mAudioEffect, j, (byte[])???, arrayOfByte);
          return;
        }
      }
    }
  }
  
  public static abstract interface OnControlStatusChangeListener
  {
    public abstract void onControlStatusChange(AudioEffect paramAudioEffect, boolean paramBoolean);
  }
  
  public static abstract interface OnEnableStatusChangeListener
  {
    public abstract void onEnableStatusChange(AudioEffect paramAudioEffect, boolean paramBoolean);
  }
  
  public static abstract interface OnParameterChangeListener
  {
    public abstract void onParameterChange(AudioEffect paramAudioEffect, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiofx/AudioEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */