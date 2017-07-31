package android.media.audiofx;

import android.util.Log;

public class AcousticEchoCanceler
  extends AudioEffect
{
  private static final String TAG = "AcousticEchoCanceler";
  
  private AcousticEchoCanceler(int paramInt)
    throws IllegalArgumentException, UnsupportedOperationException, RuntimeException
  {
    super(EFFECT_TYPE_AEC, EFFECT_TYPE_NULL, 0, paramInt);
  }
  
  public static AcousticEchoCanceler create(int paramInt)
  {
    try
    {
      AcousticEchoCanceler localAcousticEchoCanceler = new AcousticEchoCanceler(paramInt);
      return localAcousticEchoCanceler;
    }
    catch (RuntimeException localRuntimeException)
    {
      Log.w("AcousticEchoCanceler", "not enough memory");
      return null;
    }
    catch (UnsupportedOperationException localUnsupportedOperationException)
    {
      Log.w("AcousticEchoCanceler", "not enough resources");
      return null;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Log.w("AcousticEchoCanceler", "not implemented on this device" + null);
    }
    return null;
  }
  
  public static boolean isAvailable()
  {
    return AudioEffect.isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_AEC);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiofx/AcousticEchoCanceler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */