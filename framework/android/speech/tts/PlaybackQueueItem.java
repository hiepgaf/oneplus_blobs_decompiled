package android.speech.tts;

abstract class PlaybackQueueItem
  implements Runnable
{
  private final Object mCallerIdentity;
  private final TextToSpeechService.UtteranceProgressDispatcher mDispatcher;
  
  PlaybackQueueItem(TextToSpeechService.UtteranceProgressDispatcher paramUtteranceProgressDispatcher, Object paramObject)
  {
    this.mDispatcher = paramUtteranceProgressDispatcher;
    this.mCallerIdentity = paramObject;
  }
  
  Object getCallerIdentity()
  {
    return this.mCallerIdentity;
  }
  
  protected TextToSpeechService.UtteranceProgressDispatcher getDispatcher()
  {
    return this.mDispatcher;
  }
  
  public abstract void run();
  
  abstract void stop(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/PlaybackQueueItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */