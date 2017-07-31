package android.media.audiofx;

import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.StringTokenizer;

public class Virtualizer
  extends AudioEffect
{
  private static final boolean DEBUG = false;
  public static final int PARAM_FORCE_VIRTUALIZATION_MODE = 3;
  public static final int PARAM_STRENGTH = 1;
  public static final int PARAM_STRENGTH_SUPPORTED = 0;
  public static final int PARAM_VIRTUALIZATION_MODE = 4;
  public static final int PARAM_VIRTUAL_SPEAKER_ANGLES = 2;
  private static final String TAG = "Virtualizer";
  public static final int VIRTUALIZATION_MODE_AUTO = 1;
  public static final int VIRTUALIZATION_MODE_BINAURAL = 2;
  public static final int VIRTUALIZATION_MODE_OFF = 0;
  public static final int VIRTUALIZATION_MODE_TRANSAURAL = 3;
  private BaseParameterListener mBaseParamListener = null;
  private OnParameterChangeListener mParamListener = null;
  private final Object mParamListenerLock = new Object();
  private boolean mStrengthSupported = false;
  
  public Virtualizer(int paramInt1, int paramInt2)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException, RuntimeException
  {
    super(EFFECT_TYPE_VIRTUALIZER, EFFECT_TYPE_NULL, paramInt1, paramInt2);
    if (paramInt2 == 0) {
      Log.w("Virtualizer", "WARNING: attaching a Virtualizer to global output mix is deprecated!");
    }
    int[] arrayOfInt = new int[1];
    checkStatus(getParameter(0, arrayOfInt));
    if (arrayOfInt[0] != 0) {}
    for (;;)
    {
      this.mStrengthSupported = bool;
      return;
      bool = false;
    }
  }
  
  private static int deviceToMode(int paramInt)
  {
    switch (paramInt)
    {
    case 15: 
    case 16: 
    case 17: 
    case 18: 
    default: 
      return 0;
    case 1: 
    case 3: 
    case 4: 
    case 7: 
      return 2;
    }
    return 3;
  }
  
  private boolean getAnglesInt(int paramInt1, int paramInt2, int[] paramArrayOfInt)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    if (paramInt1 == 0) {
      throw new IllegalArgumentException("Virtualizer: illegal CHANNEL_INVALID channel mask");
    }
    if (paramInt1 == 1) {
      paramInt1 = 12;
    }
    int i;
    for (;;)
    {
      i = AudioFormat.channelCountFromOutChannelMask(paramInt1);
      if ((paramArrayOfInt == null) || (paramArrayOfInt.length >= i * 3)) {
        break;
      }
      Log.e("Virtualizer", "Size of array for angles cannot accomodate number of channels in mask (" + i + ")");
      throw new IllegalArgumentException("Virtualizer: array for channel / angle pairs is too small: is " + paramArrayOfInt.length + ", should be " + i * 3);
    }
    ByteBuffer localByteBuffer = ByteBuffer.allocate(12);
    localByteBuffer.order(ByteOrder.nativeOrder());
    localByteBuffer.putInt(2);
    localByteBuffer.putInt(AudioFormat.convertChannelOutMaskToNativeMask(paramInt1));
    localByteBuffer.putInt(AudioDeviceInfo.convertDeviceTypeToInternalDevice(paramInt2));
    byte[] arrayOfByte = new byte[i * 4 * 3];
    paramInt1 = getParameter(localByteBuffer.array(), arrayOfByte);
    if (paramInt1 >= 0)
    {
      if (paramArrayOfInt != null)
      {
        localByteBuffer = ByteBuffer.wrap(arrayOfByte);
        localByteBuffer.order(ByteOrder.nativeOrder());
        paramInt1 = 0;
        while (paramInt1 < i)
        {
          paramArrayOfInt[(paramInt1 * 3)] = AudioFormat.convertNativeChannelMaskToOutMask(localByteBuffer.getInt(paramInt1 * 4 * 3));
          paramArrayOfInt[(paramInt1 * 3 + 1)] = localByteBuffer.getInt(paramInt1 * 4 * 3 + 4);
          paramArrayOfInt[(paramInt1 * 3 + 2)] = localByteBuffer.getInt(paramInt1 * 4 * 3 + 8);
          paramInt1 += 1;
        }
      }
      return true;
    }
    if (paramInt1 == -4) {
      return false;
    }
    checkStatus(paramInt1);
    Log.e("Virtualizer", "unexpected status code " + paramInt1 + " after getParameter(PARAM_VIRTUAL_SPEAKER_ANGLES)");
    return false;
  }
  
  private static int getDeviceForModeForce(int paramInt)
    throws IllegalArgumentException
  {
    if (paramInt == 1) {
      return 0;
    }
    return getDeviceForModeQuery(paramInt);
  }
  
  private static int getDeviceForModeQuery(int paramInt)
    throws IllegalArgumentException
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Virtualizer: illegal virtualization mode " + paramInt);
    case 2: 
      return 4;
    }
    return 2;
  }
  
  public boolean canVirtualize(int paramInt1, int paramInt2)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    return getAnglesInt(paramInt1, getDeviceForModeQuery(paramInt2), null);
  }
  
  public boolean forceVirtualizationMode(int paramInt)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    paramInt = setParameter(3, AudioDeviceInfo.convertDeviceTypeToInternalDevice(getDeviceForModeForce(paramInt)));
    if (paramInt >= 0) {
      return true;
    }
    if (paramInt == -4) {
      return false;
    }
    checkStatus(paramInt);
    Log.e("Virtualizer", "unexpected status code " + paramInt + " after setParameter(PARAM_FORCE_VIRTUALIZATION_MODE)");
    return false;
  }
  
  public Settings getProperties()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    Settings localSettings = new Settings();
    short[] arrayOfShort = new short[1];
    checkStatus(getParameter(1, arrayOfShort));
    localSettings.strength = arrayOfShort[0];
    return localSettings;
  }
  
  public short getRoundedStrength()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    short[] arrayOfShort = new short[1];
    checkStatus(getParameter(1, arrayOfShort));
    return arrayOfShort[0];
  }
  
  public boolean getSpeakerAngles(int paramInt1, int paramInt2, int[] paramArrayOfInt)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    if (paramArrayOfInt == null) {
      throw new IllegalArgumentException("Virtualizer: illegal null channel / angle array");
    }
    return getAnglesInt(paramInt1, getDeviceForModeQuery(paramInt2), paramArrayOfInt);
  }
  
  public boolean getStrengthSupported()
  {
    return this.mStrengthSupported;
  }
  
  public int getVirtualizationMode()
    throws IllegalStateException, UnsupportedOperationException
  {
    int[] arrayOfInt = new int[1];
    int i = getParameter(4, arrayOfInt);
    if (i >= 0) {
      return deviceToMode(AudioDeviceInfo.convertInternalDeviceToDeviceType(arrayOfInt[0]));
    }
    if (i == -4) {
      return 0;
    }
    checkStatus(i);
    Log.e("Virtualizer", "unexpected status code " + i + " after getParameter(PARAM_VIRTUALIZATION_MODE)");
    return 0;
  }
  
  public void setParameterListener(OnParameterChangeListener paramOnParameterChangeListener)
  {
    synchronized (this.mParamListenerLock)
    {
      if (this.mParamListener == null)
      {
        this.mParamListener = paramOnParameterChangeListener;
        this.mBaseParamListener = new BaseParameterListener(null);
        super.setParameterListener(this.mBaseParamListener);
      }
      return;
    }
  }
  
  public void setProperties(Settings paramSettings)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(1, paramSettings.strength));
  }
  
  public void setStrength(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(1, paramShort));
  }
  
  private class BaseParameterListener
    implements AudioEffect.OnParameterChangeListener
  {
    private BaseParameterListener() {}
    
    public void onParameterChange(AudioEffect paramAudioEffect, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      paramAudioEffect = null;
      synchronized (Virtualizer.-get1(Virtualizer.this))
      {
        if (Virtualizer.-get0(Virtualizer.this) != null) {
          paramAudioEffect = Virtualizer.-get0(Virtualizer.this);
        }
        if (paramAudioEffect != null)
        {
          int i = -1;
          short s = -1;
          if (paramArrayOfByte1.length == 4) {
            i = Virtualizer.byteArrayToInt(paramArrayOfByte1, 0);
          }
          if (paramArrayOfByte2.length == 2) {
            s = Virtualizer.byteArrayToShort(paramArrayOfByte2, 0);
          }
          if ((i != -1) && (s != -1)) {
            paramAudioEffect.onParameterChange(Virtualizer.this, paramInt, i, s);
          }
        }
        return;
      }
    }
  }
  
  public static abstract interface OnParameterChangeListener
  {
    public abstract void onParameterChange(Virtualizer paramVirtualizer, int paramInt1, int paramInt2, short paramShort);
  }
  
  public static class Settings
  {
    public short strength;
    
    public Settings() {}
    
    public Settings(String paramString)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "=;");
      localStringTokenizer.countTokens();
      if (localStringTokenizer.countTokens() != 3) {
        throw new IllegalArgumentException("settings: " + paramString);
      }
      paramString = localStringTokenizer.nextToken();
      if (!paramString.equals("Virtualizer")) {
        throw new IllegalArgumentException("invalid settings for Virtualizer: " + paramString);
      }
      try
      {
        String str = localStringTokenizer.nextToken();
        paramString = str;
        if (!str.equals("strength"))
        {
          paramString = str;
          throw new IllegalArgumentException("invalid key name: " + str);
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new IllegalArgumentException("invalid value for key: " + paramString);
      }
      paramString = localNumberFormatException;
      this.strength = Short.parseShort(localStringTokenizer.nextToken());
    }
    
    public String toString()
    {
      return new String("Virtualizer;strength=" + Short.toString(this.strength));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiofx/Virtualizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */