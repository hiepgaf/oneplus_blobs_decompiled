package android.speech;

import android.os.Bundle;

public abstract interface RecognitionListener
{
  public abstract void onBeginningOfSpeech();
  
  public abstract void onBufferReceived(byte[] paramArrayOfByte);
  
  public abstract void onEndOfSpeech();
  
  public abstract void onError(int paramInt);
  
  public abstract void onEvent(int paramInt, Bundle paramBundle);
  
  public abstract void onPartialResults(Bundle paramBundle);
  
  public abstract void onReadyForSpeech(Bundle paramBundle);
  
  public abstract void onResults(Bundle paramBundle);
  
  public abstract void onRmsChanged(float paramFloat);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/RecognitionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */