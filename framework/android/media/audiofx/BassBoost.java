package android.media.audiofx;

import android.util.Log;
import java.util.StringTokenizer;

public class BassBoost
  extends AudioEffect
{
  public static final int PARAM_STRENGTH = 1;
  public static final int PARAM_STRENGTH_SUPPORTED = 0;
  private static final String TAG = "BassBoost";
  private BaseParameterListener mBaseParamListener = null;
  private OnParameterChangeListener mParamListener = null;
  private final Object mParamListenerLock = new Object();
  private boolean mStrengthSupported = false;
  
  public BassBoost(int paramInt1, int paramInt2)
    throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException, RuntimeException
  {
    super(EFFECT_TYPE_BASS_BOOST, EFFECT_TYPE_NULL, paramInt1, paramInt2);
    if (paramInt2 == 0) {
      Log.w("BassBoost", "WARNING: attaching a BassBoost to global output mix is deprecated!");
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
  
  public boolean getStrengthSupported()
  {
    return this.mStrengthSupported;
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
      synchronized (BassBoost.-get1(BassBoost.this))
      {
        if (BassBoost.-get0(BassBoost.this) != null) {
          paramAudioEffect = BassBoost.-get0(BassBoost.this);
        }
        if (paramAudioEffect != null)
        {
          int i = -1;
          short s = -1;
          if (paramArrayOfByte1.length == 4) {
            i = BassBoost.byteArrayToInt(paramArrayOfByte1, 0);
          }
          if (paramArrayOfByte2.length == 2) {
            s = BassBoost.byteArrayToShort(paramArrayOfByte2, 0);
          }
          if ((i != -1) && (s != -1)) {
            paramAudioEffect.onParameterChange(BassBoost.this, paramInt, i, s);
          }
        }
        return;
      }
    }
  }
  
  public static abstract interface OnParameterChangeListener
  {
    public abstract void onParameterChange(BassBoost paramBassBoost, int paramInt1, int paramInt2, short paramShort);
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
      if (!paramString.equals("BassBoost")) {
        throw new IllegalArgumentException("invalid settings for BassBoost: " + paramString);
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
      return new String("BassBoost;strength=" + Short.toString(this.strength));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiofx/BassBoost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */