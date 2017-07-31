package android.media.audiofx;

import java.util.StringTokenizer;

public class PresetReverb
  extends AudioEffect
{
  public static final int PARAM_PRESET = 0;
  public static final short PRESET_LARGEHALL = 5;
  public static final short PRESET_LARGEROOM = 3;
  public static final short PRESET_MEDIUMHALL = 4;
  public static final short PRESET_MEDIUMROOM = 2;
  public static final short PRESET_NONE = 0;
  public static final short PRESET_PLATE = 6;
  public static final short PRESET_SMALLROOM = 1;
  private static final String TAG = "PresetReverb";
  private BaseParameterListener mBaseParamListener = null;
  private OnParameterChangeListener mParamListener = null;
  private final Object mParamListenerLock = new Object();
  
  public PresetReverb(int paramInt1, int paramInt2)
    throws IllegalArgumentException, UnsupportedOperationException, RuntimeException
  {
    super(EFFECT_TYPE_PRESET_REVERB, EFFECT_TYPE_NULL, paramInt1, paramInt2);
  }
  
  public short getPreset()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    short[] arrayOfShort = new short[1];
    checkStatus(getParameter(0, arrayOfShort));
    return arrayOfShort[0];
  }
  
  public Settings getProperties()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    Settings localSettings = new Settings();
    short[] arrayOfShort = new short[1];
    checkStatus(getParameter(0, arrayOfShort));
    localSettings.preset = arrayOfShort[0];
    return localSettings;
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
  
  public void setPreset(short paramShort)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(0, paramShort));
  }
  
  public void setProperties(Settings paramSettings)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(0, paramSettings.preset));
  }
  
  private class BaseParameterListener
    implements AudioEffect.OnParameterChangeListener
  {
    private BaseParameterListener() {}
    
    public void onParameterChange(AudioEffect paramAudioEffect, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      paramAudioEffect = null;
      synchronized (PresetReverb.-get1(PresetReverb.this))
      {
        if (PresetReverb.-get0(PresetReverb.this) != null) {
          paramAudioEffect = PresetReverb.-get0(PresetReverb.this);
        }
        if (paramAudioEffect != null)
        {
          int i = -1;
          short s = -1;
          if (paramArrayOfByte1.length == 4) {
            i = PresetReverb.byteArrayToInt(paramArrayOfByte1, 0);
          }
          if (paramArrayOfByte2.length == 2) {
            s = PresetReverb.byteArrayToShort(paramArrayOfByte2, 0);
          }
          if ((i != -1) && (s != -1)) {
            paramAudioEffect.onParameterChange(PresetReverb.this, paramInt, i, s);
          }
        }
        return;
      }
    }
  }
  
  public static abstract interface OnParameterChangeListener
  {
    public abstract void onParameterChange(PresetReverb paramPresetReverb, int paramInt1, int paramInt2, short paramShort);
  }
  
  public static class Settings
  {
    public short preset;
    
    public Settings() {}
    
    public Settings(String paramString)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "=;");
      localStringTokenizer.countTokens();
      if (localStringTokenizer.countTokens() != 3) {
        throw new IllegalArgumentException("settings: " + paramString);
      }
      paramString = localStringTokenizer.nextToken();
      if (!paramString.equals("PresetReverb")) {
        throw new IllegalArgumentException("invalid settings for PresetReverb: " + paramString);
      }
      try
      {
        String str = localStringTokenizer.nextToken();
        paramString = str;
        if (!str.equals("preset"))
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
      this.preset = Short.parseShort(localStringTokenizer.nextToken());
    }
    
    public String toString()
    {
      return new String("PresetReverb;preset=" + Short.toString(this.preset));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiofx/PresetReverb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */