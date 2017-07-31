package android.media.audiofx;

import android.util.Log;

public class NoiseSuppressor
  extends AudioEffect
{
  private static final String TAG = "NoiseSuppressor";
  
  private NoiseSuppressor(int paramInt)
    throws IllegalArgumentException, UnsupportedOperationException, RuntimeException
  {
    super(EFFECT_TYPE_NS, EFFECT_TYPE_NULL, 0, paramInt);
  }
  
  public static NoiseSuppressor create(int paramInt)
  {
    try
    {
      NoiseSuppressor localNoiseSuppressor = new NoiseSuppressor(paramInt);
      return localNoiseSuppressor;
    }
    catch (RuntimeException localRuntimeException)
    {
      Log.w("NoiseSuppressor", "not enough memory");
      return null;
    }
    catch (UnsupportedOperationException localUnsupportedOperationException)
    {
      Log.w("NoiseSuppressor", "not enough resources");
      return null;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Log.w("NoiseSuppressor", "not implemented on this device " + null);
    }
    return null;
  }
  
  public static boolean isAvailable()
  {
    return AudioEffect.isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_NS);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiofx/NoiseSuppressor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */