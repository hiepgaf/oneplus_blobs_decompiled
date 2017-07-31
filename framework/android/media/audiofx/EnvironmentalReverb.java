package android.media.audiofx;

import java.util.StringTokenizer;

public class EnvironmentalReverb
  extends AudioEffect
{
  public static final int PARAM_DECAY_HF_RATIO = 3;
  public static final int PARAM_DECAY_TIME = 2;
  public static final int PARAM_DENSITY = 9;
  public static final int PARAM_DIFFUSION = 8;
  private static final int PARAM_PROPERTIES = 10;
  public static final int PARAM_REFLECTIONS_DELAY = 5;
  public static final int PARAM_REFLECTIONS_LEVEL = 4;
  public static final int PARAM_REVERB_DELAY = 7;
  public static final int PARAM_REVERB_LEVEL = 6;
  public static final int PARAM_ROOM_HF_LEVEL = 1;
  public static final int PARAM_ROOM_LEVEL = 0;
  private static int PROPERTY_SIZE = 26;
  private static final String TAG = "EnvironmentalReverb";
  private BaseParameterListener mBaseParamListener = null;
  private OnParameterChangeListener mParamListener = null;
  private final Object mParamListenerLock = new Object();
  
  public EnvironmentalReverb(int paramInt1, int paramInt2)
    throws IllegalArgumentException, UnsupportedOperationException, RuntimeException
  {
    super(EFFECT_TYPE_ENV_REVERB, EFFECT_TYPE_NULL, paramInt1, paramInt2);
  }
  
  public short getDecayHFRatio()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    byte[] arrayOfByte = new byte[2];
    checkStatus(getParameter(3, arrayOfByte));
    return byteArrayToShort(arrayOfByte);
  }
  
  public int getDecayTime()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    byte[] arrayOfByte = new byte[4];
    checkStatus(getParameter(2, arrayOfByte));
    return byteArrayToInt(arrayOfByte);
  }
  
  public short getDensity()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    byte[] arrayOfByte = new byte[2];
    checkStatus(getParameter(9, arrayOfByte));
    return byteArrayToShort(arrayOfByte);
  }
  
  public short getDiffusion()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    byte[] arrayOfByte = new byte[2];
    checkStatus(getParameter(8, arrayOfByte));
    return byteArrayToShort(arrayOfByte);
  }
  
  public Settings getProperties()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    byte[] arrayOfByte = new byte[PROPERTY_SIZE];
    checkStatus(getParameter(10, arrayOfByte));
    Settings localSettings = new Settings();
    localSettings.roomLevel = byteArrayToShort(arrayOfByte, 0);
    localSettings.roomHFLevel = byteArrayToShort(arrayOfByte, 2);
    localSettings.decayTime = byteArrayToInt(arrayOfByte, 4);
    localSettings.decayHFRatio = byteArrayToShort(arrayOfByte, 8);
    localSettings.reflectionsLevel = byteArrayToShort(arrayOfByte, 10);
    localSettings.reflectionsDelay = byteArrayToInt(arrayOfByte, 12);
    localSettings.reverbLevel = byteArrayToShort(arrayOfByte, 16);
    localSettings.reverbDelay = byteArrayToInt(arrayOfByte, 18);
    localSettings.diffusion = byteArrayToShort(arrayOfByte, 22);
    localSettings.density = byteArrayToShort(arrayOfByte, 24);
    return localSettings;
  }
  
  public int getReflectionsDelay()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    byte[] arrayOfByte = new byte[4];
    checkStatus(getParameter(5, arrayOfByte));
    return byteArrayToInt(arrayOfByte);
  }
  
  public short getReflectionsLevel()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    byte[] arrayOfByte = new byte[2];
    checkStatus(getParameter(4, arrayOfByte));
    return byteArrayToShort(arrayOfByte);
  }
  
  public int getReverbDelay()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    byte[] arrayOfByte = new byte[4];
    checkStatus(getParameter(7, arrayOfByte));
    return byteArrayToInt(arrayOfByte);
  }
  
  public short getReverbLevel()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    byte[] arrayOfByte = new byte[2];
    checkStatus(getParameter(6, arrayOfByte));
    return byteArrayToShort(arrayOfByte);
  }
  
  public short getRoomHFLevel()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    byte[] arrayOfByte = new byte[2];
    checkStatus(getParameter(1, arrayOfByte));
    return byteArrayToShort(arrayOfByte);
  }
  
  public short getRoomLevel()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    byte[] arrayOfByte = new byte[2];
    checkStatus(getParameter(0, arrayOfByte));
    return byteArrayToShort(arrayOfByte);
  }
  
  public void setDecayHFRatio(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(3, shortToByteArray(paramShort)));
  }
  
  public void setDecayTime(int paramInt)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(2, intToByteArray(paramInt)));
  }
  
  public void setDensity(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(9, shortToByteArray(paramShort)));
  }
  
  public void setDiffusion(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(8, shortToByteArray(paramShort)));
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
    checkStatus(setParameter(10, concatArrays(new byte[][] { shortToByteArray(paramSettings.roomLevel), shortToByteArray(paramSettings.roomHFLevel), intToByteArray(paramSettings.decayTime), shortToByteArray(paramSettings.decayHFRatio), shortToByteArray(paramSettings.reflectionsLevel), intToByteArray(paramSettings.reflectionsDelay), shortToByteArray(paramSettings.reverbLevel), intToByteArray(paramSettings.reverbDelay), shortToByteArray(paramSettings.diffusion), shortToByteArray(paramSettings.density) })));
  }
  
  public void setReflectionsDelay(int paramInt)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(5, intToByteArray(paramInt)));
  }
  
  public void setReflectionsLevel(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(4, shortToByteArray(paramShort)));
  }
  
  public void setReverbDelay(int paramInt)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(7, intToByteArray(paramInt)));
  }
  
  public void setReverbLevel(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(6, shortToByteArray(paramShort)));
  }
  
  public void setRoomHFLevel(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(1, shortToByteArray(paramShort)));
  }
  
  public void setRoomLevel(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(0, shortToByteArray(paramShort)));
  }
  
  private class BaseParameterListener
    implements AudioEffect.OnParameterChangeListener
  {
    private BaseParameterListener() {}
    
    public void onParameterChange(AudioEffect paramAudioEffect, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      paramAudioEffect = null;
      for (;;)
      {
        int i;
        synchronized (EnvironmentalReverb.-get1(EnvironmentalReverb.this))
        {
          if (EnvironmentalReverb.-get0(EnvironmentalReverb.this) != null) {
            paramAudioEffect = EnvironmentalReverb.-get0(EnvironmentalReverb.this);
          }
          if (paramAudioEffect != null)
          {
            int j = -1;
            i = -1;
            if (paramArrayOfByte1.length == 4) {
              j = EnvironmentalReverb.byteArrayToInt(paramArrayOfByte1, 0);
            }
            if (paramArrayOfByte2.length != 2) {
              break label107;
            }
            i = EnvironmentalReverb.byteArrayToShort(paramArrayOfByte2, 0);
            if ((j != -1) && (i != -1)) {
              paramAudioEffect.onParameterChange(EnvironmentalReverb.this, paramInt, j, i);
            }
          }
          return;
        }
        label107:
        if (paramArrayOfByte2.length == 4) {
          i = EnvironmentalReverb.byteArrayToInt(paramArrayOfByte2, 0);
        }
      }
    }
  }
  
  public static abstract interface OnParameterChangeListener
  {
    public abstract void onParameterChange(EnvironmentalReverb paramEnvironmentalReverb, int paramInt1, int paramInt2, int paramInt3);
  }
  
  public static class Settings
  {
    public short decayHFRatio;
    public int decayTime;
    public short density;
    public short diffusion;
    public int reflectionsDelay;
    public short reflectionsLevel;
    public int reverbDelay;
    public short reverbLevel;
    public short roomHFLevel;
    public short roomLevel;
    
    public Settings() {}
    
    public Settings(String paramString)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "=;");
      localStringTokenizer.countTokens();
      if (localStringTokenizer.countTokens() != 21) {
        throw new IllegalArgumentException("settings: " + paramString);
      }
      paramString = localStringTokenizer.nextToken();
      if (!paramString.equals("EnvironmentalReverb")) {
        throw new IllegalArgumentException("invalid settings for EnvironmentalReverb: " + paramString);
      }
      try
      {
        String str1 = localStringTokenizer.nextToken();
        paramString = str1;
        if (!str1.equals("roomLevel"))
        {
          paramString = str1;
          throw new IllegalArgumentException("invalid key name: " + str1);
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new IllegalArgumentException("invalid value for key: " + paramString);
      }
      paramString = localNumberFormatException;
      this.roomLevel = Short.parseShort(localStringTokenizer.nextToken());
      paramString = localNumberFormatException;
      String str2 = localStringTokenizer.nextToken();
      paramString = str2;
      if (!str2.equals("roomHFLevel"))
      {
        paramString = str2;
        throw new IllegalArgumentException("invalid key name: " + str2);
      }
      paramString = str2;
      this.roomHFLevel = Short.parseShort(localStringTokenizer.nextToken());
      paramString = str2;
      str2 = localStringTokenizer.nextToken();
      paramString = str2;
      if (!str2.equals("decayTime"))
      {
        paramString = str2;
        throw new IllegalArgumentException("invalid key name: " + str2);
      }
      paramString = str2;
      this.decayTime = Integer.parseInt(localStringTokenizer.nextToken());
      paramString = str2;
      str2 = localStringTokenizer.nextToken();
      paramString = str2;
      if (!str2.equals("decayHFRatio"))
      {
        paramString = str2;
        throw new IllegalArgumentException("invalid key name: " + str2);
      }
      paramString = str2;
      this.decayHFRatio = Short.parseShort(localStringTokenizer.nextToken());
      paramString = str2;
      str2 = localStringTokenizer.nextToken();
      paramString = str2;
      if (!str2.equals("reflectionsLevel"))
      {
        paramString = str2;
        throw new IllegalArgumentException("invalid key name: " + str2);
      }
      paramString = str2;
      this.reflectionsLevel = Short.parseShort(localStringTokenizer.nextToken());
      paramString = str2;
      str2 = localStringTokenizer.nextToken();
      paramString = str2;
      if (!str2.equals("reflectionsDelay"))
      {
        paramString = str2;
        throw new IllegalArgumentException("invalid key name: " + str2);
      }
      paramString = str2;
      this.reflectionsDelay = Integer.parseInt(localStringTokenizer.nextToken());
      paramString = str2;
      str2 = localStringTokenizer.nextToken();
      paramString = str2;
      if (!str2.equals("reverbLevel"))
      {
        paramString = str2;
        throw new IllegalArgumentException("invalid key name: " + str2);
      }
      paramString = str2;
      this.reverbLevel = Short.parseShort(localStringTokenizer.nextToken());
      paramString = str2;
      str2 = localStringTokenizer.nextToken();
      paramString = str2;
      if (!str2.equals("reverbDelay"))
      {
        paramString = str2;
        throw new IllegalArgumentException("invalid key name: " + str2);
      }
      paramString = str2;
      this.reverbDelay = Integer.parseInt(localStringTokenizer.nextToken());
      paramString = str2;
      str2 = localStringTokenizer.nextToken();
      paramString = str2;
      if (!str2.equals("diffusion"))
      {
        paramString = str2;
        throw new IllegalArgumentException("invalid key name: " + str2);
      }
      paramString = str2;
      this.diffusion = Short.parseShort(localStringTokenizer.nextToken());
      paramString = str2;
      str2 = localStringTokenizer.nextToken();
      paramString = str2;
      if (!str2.equals("density"))
      {
        paramString = str2;
        throw new IllegalArgumentException("invalid key name: " + str2);
      }
      paramString = str2;
      this.density = Short.parseShort(localStringTokenizer.nextToken());
    }
    
    public String toString()
    {
      return new String("EnvironmentalReverb;roomLevel=" + Short.toString(this.roomLevel) + ";roomHFLevel=" + Short.toString(this.roomHFLevel) + ";decayTime=" + Integer.toString(this.decayTime) + ";decayHFRatio=" + Short.toString(this.decayHFRatio) + ";reflectionsLevel=" + Short.toString(this.reflectionsLevel) + ";reflectionsDelay=" + Integer.toString(this.reflectionsDelay) + ";reverbLevel=" + Short.toString(this.reverbLevel) + ";reverbDelay=" + Integer.toString(this.reverbDelay) + ";diffusion=" + Short.toString(this.diffusion) + ";density=" + Short.toString(this.density));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiofx/EnvironmentalReverb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */