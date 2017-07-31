package android.media.audiofx;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

public class Equalizer
  extends AudioEffect
{
  public static final int PARAM_BAND_FREQ_RANGE = 4;
  public static final int PARAM_BAND_LEVEL = 2;
  public static final int PARAM_CENTER_FREQ = 3;
  public static final int PARAM_CURRENT_PRESET = 6;
  public static final int PARAM_GET_BAND = 5;
  public static final int PARAM_GET_NUM_OF_PRESETS = 7;
  public static final int PARAM_GET_PRESET_NAME = 8;
  public static final int PARAM_LEVEL_RANGE = 1;
  public static final int PARAM_NUM_BANDS = 0;
  private static final int PARAM_PROPERTIES = 9;
  public static final int PARAM_STRING_SIZE_MAX = 32;
  private static final String TAG = "Equalizer";
  private BaseParameterListener mBaseParamListener = null;
  private short mNumBands = 0;
  private int mNumPresets;
  private OnParameterChangeListener mParamListener = null;
  private final Object mParamListenerLock = new Object();
  private String[] mPresetNames;
  
  public Equalizer(int paramInt1, int paramInt2)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException, RuntimeException
  {
    super(EFFECT_TYPE_EQUALIZER, EFFECT_TYPE_NULL, paramInt1, paramInt2);
    if (paramInt2 == 0) {
      Log.w("Equalizer", "WARNING: attaching an Equalizer to global output mix is deprecated!");
    }
    getNumberOfBands();
    this.mNumPresets = getNumberOfPresets();
    if (this.mNumPresets != 0)
    {
      this.mPresetNames = new String[this.mNumPresets];
      byte[] arrayOfByte = new byte[32];
      int[] arrayOfInt = new int[2];
      arrayOfInt[0] = 8;
      paramInt1 = 0;
      for (;;)
      {
        if (paramInt1 < this.mNumPresets)
        {
          arrayOfInt[1] = paramInt1;
          checkStatus(getParameter(arrayOfInt, arrayOfByte));
          paramInt2 = 0;
          while (arrayOfByte[paramInt2] != 0) {
            paramInt2 += 1;
          }
          try
          {
            this.mPresetNames[paramInt1] = new String(arrayOfByte, 0, paramInt2, "ISO-8859-1");
            paramInt1 += 1;
          }
          catch (UnsupportedEncodingException localUnsupportedEncodingException)
          {
            for (;;)
            {
              Log.e("Equalizer", "preset name decode error");
            }
          }
        }
      }
    }
  }
  
  public short getBand(int paramInt)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    short[] arrayOfShort = new short[1];
    checkStatus(getParameter(new int[] { 5, paramInt }, arrayOfShort));
    return arrayOfShort[0];
  }
  
  public int[] getBandFreqRange(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    int[] arrayOfInt = new int[2];
    checkStatus(getParameter(new int[] { 4, paramShort }, arrayOfInt));
    return arrayOfInt;
  }
  
  public short getBandLevel(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    short[] arrayOfShort = new short[1];
    checkStatus(getParameter(new int[] { 2, paramShort }, arrayOfShort));
    return arrayOfShort[0];
  }
  
  public short[] getBandLevelRange()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    short[] arrayOfShort = new short[2];
    checkStatus(getParameter(1, arrayOfShort));
    return arrayOfShort;
  }
  
  public int getCenterFreq(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    int[] arrayOfInt = new int[1];
    checkStatus(getParameter(new int[] { 3, paramShort }, arrayOfInt));
    return arrayOfInt[0];
  }
  
  public short getCurrentPreset()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    short[] arrayOfShort = new short[1];
    checkStatus(getParameter(6, arrayOfShort));
    return arrayOfShort[0];
  }
  
  public short getNumberOfBands()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    if (this.mNumBands != 0) {
      return this.mNumBands;
    }
    short[] arrayOfShort = new short[1];
    checkStatus(getParameter(new int[] { 0 }, arrayOfShort));
    this.mNumBands = arrayOfShort[0];
    return this.mNumBands;
  }
  
  public short getNumberOfPresets()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    short[] arrayOfShort = new short[1];
    checkStatus(getParameter(7, arrayOfShort));
    return arrayOfShort[0];
  }
  
  public String getPresetName(short paramShort)
  {
    if ((paramShort >= 0) && (paramShort < this.mNumPresets)) {
      return this.mPresetNames[paramShort];
    }
    return "";
  }
  
  public Settings getProperties()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    byte[] arrayOfByte = new byte[this.mNumBands * 2 + 4];
    checkStatus(getParameter(9, arrayOfByte));
    Settings localSettings = new Settings();
    localSettings.curPreset = byteArrayToShort(arrayOfByte, 0);
    localSettings.numBands = byteArrayToShort(arrayOfByte, 2);
    localSettings.bandLevels = new short[this.mNumBands];
    int i = 0;
    while (i < this.mNumBands)
    {
      localSettings.bandLevels[i] = byteArrayToShort(arrayOfByte, i * 2 + 4);
      i += 1;
    }
    return localSettings;
  }
  
  public void setBandLevel(short paramShort1, short paramShort2)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(new int[] { 2, paramShort1 }, new short[] { paramShort2 }));
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
    if ((paramSettings.numBands != paramSettings.bandLevels.length) || (paramSettings.numBands != this.mNumBands)) {
      throw new IllegalArgumentException("settings invalid band count: " + paramSettings.numBands);
    }
    byte[] arrayOfByte = concatArrays(new byte[][] { shortToByteArray(paramSettings.curPreset), shortToByteArray(this.mNumBands) });
    int i = 0;
    while (i < this.mNumBands)
    {
      arrayOfByte = concatArrays(new byte[][] { arrayOfByte, shortToByteArray(paramSettings.bandLevels[i]) });
      i += 1;
    }
    checkStatus(setParameter(9, arrayOfByte));
  }
  
  public void usePreset(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(6, paramShort));
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
        synchronized (Equalizer.-get1(Equalizer.this))
        {
          if (Equalizer.-get0(Equalizer.this) != null) {
            paramAudioEffect = Equalizer.-get0(Equalizer.this);
          }
          if (paramAudioEffect != null)
          {
            int j = -1;
            int m = -1;
            i = -1;
            int k = m;
            if (paramArrayOfByte1.length >= 4)
            {
              int n = Equalizer.byteArrayToInt(paramArrayOfByte1, 0);
              j = n;
              k = m;
              if (paramArrayOfByte1.length >= 8)
              {
                k = Equalizer.byteArrayToInt(paramArrayOfByte1, 4);
                j = n;
              }
            }
            if (paramArrayOfByte2.length != 2) {
              break label142;
            }
            i = Equalizer.byteArrayToShort(paramArrayOfByte2, 0);
            if ((j != -1) && (i != -1)) {
              paramAudioEffect.onParameterChange(Equalizer.this, paramInt, j, k, i);
            }
          }
          return;
        }
        label142:
        if (paramArrayOfByte2.length == 4) {
          i = Equalizer.byteArrayToInt(paramArrayOfByte2, 0);
        }
      }
    }
  }
  
  public static abstract interface OnParameterChangeListener
  {
    public abstract void onParameterChange(Equalizer paramEqualizer, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  }
  
  public static class Settings
  {
    public short[] bandLevels = null;
    public short curPreset;
    public short numBands = 0;
    
    public Settings() {}
    
    public Settings(String paramString)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "=;");
      localStringTokenizer.countTokens();
      if (localStringTokenizer.countTokens() < 5) {
        throw new IllegalArgumentException("settings: " + paramString);
      }
      Object localObject = localStringTokenizer.nextToken();
      if (!((String)localObject).equals("Equalizer")) {
        throw new IllegalArgumentException("invalid settings for Equalizer: " + (String)localObject);
      }
      try
      {
        str = localStringTokenizer.nextToken();
        localObject = str;
        if (!str.equals("curPreset"))
        {
          localObject = str;
          throw new IllegalArgumentException("invalid key name: " + str);
        }
      }
      catch (NumberFormatException paramString)
      {
        throw new IllegalArgumentException("invalid value for key: " + (String)localObject);
      }
      localObject = str;
      this.curPreset = Short.parseShort(localStringTokenizer.nextToken());
      localObject = str;
      String str = localStringTokenizer.nextToken();
      localObject = str;
      if (!str.equals("numBands"))
      {
        localObject = str;
        throw new IllegalArgumentException("invalid key name: " + str);
      }
      localObject = str;
      this.numBands = Short.parseShort(localStringTokenizer.nextToken());
      localObject = str;
      if (localStringTokenizer.countTokens() != this.numBands * 2)
      {
        localObject = str;
        throw new IllegalArgumentException("settings: " + paramString);
      }
      localObject = str;
      this.bandLevels = new short[this.numBands];
      int i = 0;
      paramString = str;
      for (;;)
      {
        localObject = paramString;
        if (i >= this.numBands) {
          break;
        }
        localObject = paramString;
        paramString = localStringTokenizer.nextToken();
        localObject = paramString;
        if (!paramString.equals("band" + (i + 1) + "Level"))
        {
          localObject = paramString;
          throw new IllegalArgumentException("invalid key name: " + paramString);
        }
        localObject = paramString;
        this.bandLevels[i] = Short.parseShort(localStringTokenizer.nextToken());
        i += 1;
      }
    }
    
    public String toString()
    {
      String str = new String("Equalizer;curPreset=" + Short.toString(this.curPreset) + ";numBands=" + Short.toString(this.numBands));
      int i = 0;
      while (i < this.numBands)
      {
        str = str.concat(";band" + (i + 1) + "Level=" + Short.toString(this.bandLevels[i]));
        i += 1;
      }
      return str;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiofx/Equalizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */