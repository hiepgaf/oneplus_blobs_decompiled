package android.media.audiofx;

import android.util.Log;
import java.util.StringTokenizer;

public class LoudnessEnhancer
  extends AudioEffect
{
  public static final int PARAM_TARGET_GAIN_MB = 0;
  private static final String TAG = "LoudnessEnhancer";
  private BaseParameterListener mBaseParamListener = null;
  private OnParameterChangeListener mParamListener = null;
  private final Object mParamListenerLock = new Object();
  
  public LoudnessEnhancer(int paramInt)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException, RuntimeException
  {
    super(EFFECT_TYPE_LOUDNESS_ENHANCER, EFFECT_TYPE_NULL, 0, paramInt);
    if (paramInt == 0) {
      Log.w("LoudnessEnhancer", "WARNING: attaching a LoudnessEnhancer to global output mix is deprecated!");
    }
  }
  
  public LoudnessEnhancer(int paramInt1, int paramInt2)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException, RuntimeException
  {
    super(EFFECT_TYPE_LOUDNESS_ENHANCER, EFFECT_TYPE_NULL, paramInt1, paramInt2);
    if (paramInt2 == 0) {
      Log.w("LoudnessEnhancer", "WARNING: attaching a LoudnessEnhancer to global output mix is deprecated!");
    }
  }
  
  public Settings getProperties()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    Settings localSettings = new Settings();
    int[] arrayOfInt = new int[1];
    checkStatus(getParameter(0, arrayOfInt));
    localSettings.targetGainmB = arrayOfInt[0];
    return localSettings;
  }
  
  public float getTargetGain()
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    int[] arrayOfInt = new int[1];
    checkStatus(getParameter(0, arrayOfInt));
    return arrayOfInt[0];
  }
  
  public void setParameterListener(OnParameterChangeListener paramOnParameterChangeListener)
  {
    synchronized (this.mParamListenerLock)
    {
      if (this.mParamListener == null)
      {
        this.mBaseParamListener = new BaseParameterListener(null);
        super.setParameterListener(this.mBaseParamListener);
      }
      this.mParamListener = paramOnParameterChangeListener;
      return;
    }
  }
  
  public void setProperties(Settings paramSettings)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(0, paramSettings.targetGainmB));
  }
  
  public void setTargetGain(int paramInt)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException
  {
    checkStatus(setParameter(0, paramInt));
  }
  
  private class BaseParameterListener
    implements AudioEffect.OnParameterChangeListener
  {
    private BaseParameterListener() {}
    
    public void onParameterChange(AudioEffect paramAudioEffect, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      if (paramInt != 0) {
        return;
      }
      paramAudioEffect = null;
      synchronized (LoudnessEnhancer.-get1(LoudnessEnhancer.this))
      {
        if (LoudnessEnhancer.-get0(LoudnessEnhancer.this) != null) {
          paramAudioEffect = LoudnessEnhancer.-get0(LoudnessEnhancer.this);
        }
        if (paramAudioEffect != null)
        {
          paramInt = -1;
          int i = Integer.MIN_VALUE;
          if (paramArrayOfByte1.length == 4) {
            paramInt = LoudnessEnhancer.byteArrayToInt(paramArrayOfByte1, 0);
          }
          if (paramArrayOfByte2.length == 4) {
            i = LoudnessEnhancer.byteArrayToInt(paramArrayOfByte2, 0);
          }
          if ((paramInt != -1) && (i != Integer.MIN_VALUE)) {
            paramAudioEffect.onParameterChange(LoudnessEnhancer.this, paramInt, i);
          }
        }
        return;
      }
    }
  }
  
  public static abstract interface OnParameterChangeListener
  {
    public abstract void onParameterChange(LoudnessEnhancer paramLoudnessEnhancer, int paramInt1, int paramInt2);
  }
  
  public static class Settings
  {
    public int targetGainmB;
    
    public Settings() {}
    
    public Settings(String paramString)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "=;");
      if (localStringTokenizer.countTokens() != 3) {
        throw new IllegalArgumentException("settings: " + paramString);
      }
      paramString = localStringTokenizer.nextToken();
      if (!paramString.equals("LoudnessEnhancer")) {
        throw new IllegalArgumentException("invalid settings for LoudnessEnhancer: " + paramString);
      }
      try
      {
        String str = localStringTokenizer.nextToken();
        paramString = str;
        if (!str.equals("targetGainmB"))
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
      this.targetGainmB = Integer.parseInt(localStringTokenizer.nextToken());
    }
    
    public String toString()
    {
      return new String("LoudnessEnhancer;targetGainmB=" + Integer.toString(this.targetGainmB));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiofx/LoudnessEnhancer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */