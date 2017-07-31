package android.speech.tts;

public abstract class UtteranceProgressListener
{
  static UtteranceProgressListener from(TextToSpeech.OnUtteranceCompletedListener paramOnUtteranceCompletedListener)
  {
    new UtteranceProgressListener()
    {
      public void onDone(String paramAnonymousString)
      {
        try
        {
          this.val$listener.onUtteranceCompleted(paramAnonymousString);
          return;
        }
        finally
        {
          paramAnonymousString = finally;
          throw paramAnonymousString;
        }
      }
      
      public void onError(String paramAnonymousString)
      {
        this.val$listener.onUtteranceCompleted(paramAnonymousString);
      }
      
      public void onStart(String paramAnonymousString) {}
      
      public void onStop(String paramAnonymousString, boolean paramAnonymousBoolean)
      {
        this.val$listener.onUtteranceCompleted(paramAnonymousString);
      }
    };
  }
  
  public void onAudioAvailable(String paramString, byte[] paramArrayOfByte) {}
  
  public void onBeginSynthesis(String paramString, int paramInt1, int paramInt2, int paramInt3) {}
  
  public abstract void onDone(String paramString);
  
  @Deprecated
  public abstract void onError(String paramString);
  
  public void onError(String paramString, int paramInt)
  {
    onError(paramString);
  }
  
  public abstract void onStart(String paramString);
  
  public void onStop(String paramString, boolean paramBoolean) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/UtteranceProgressListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */