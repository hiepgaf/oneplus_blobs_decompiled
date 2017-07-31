package android.media.audiofx;

import android.util.Log;

public class AutomaticGainControl
  extends AudioEffect
{
  private static final String TAG = "AutomaticGainControl";
  
  private AutomaticGainControl(int paramInt)
    throws IllegalArgumentException, UnsupportedOperationException, RuntimeException
  {
    super(EFFECT_TYPE_AGC, EFFECT_TYPE_NULL, 0, paramInt);
  }
  
  public static AutomaticGainControl create(int paramInt)
  {
    try
    {
      AutomaticGainControl localAutomaticGainControl = new AutomaticGainControl(paramInt);
      return localAutomaticGainControl;
    }
    catch (RuntimeException localRuntimeException)
    {
      Log.w("AutomaticGainControl", "not enough memory");
      return null;
    }
    catch (UnsupportedOperationException localUnsupportedOperationException)
    {
      Log.w("AutomaticGainControl", "not enough resources");
      return null;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Log.w("AutomaticGainControl", "not implemented on this device " + null);
    }
    return null;
  }
  
  public static boolean isAvailable()
  {
    return AudioEffect.isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_AGC);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiofx/AutomaticGainControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */