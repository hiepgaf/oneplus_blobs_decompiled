package android.speech.tts;

abstract class AbstractSynthesisCallback
  implements SynthesisCallback
{
  protected final boolean mClientIsUsingV2;
  
  AbstractSynthesisCallback(boolean paramBoolean)
  {
    this.mClientIsUsingV2 = paramBoolean;
  }
  
  int errorCodeOnStop()
  {
    if (this.mClientIsUsingV2) {
      return -2;
    }
    return -1;
  }
  
  abstract void stop();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/AbstractSynthesisCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */