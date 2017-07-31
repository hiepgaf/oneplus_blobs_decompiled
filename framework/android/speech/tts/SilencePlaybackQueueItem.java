package android.speech.tts;

import android.os.ConditionVariable;

class SilencePlaybackQueueItem
  extends PlaybackQueueItem
{
  private final ConditionVariable mCondVar = new ConditionVariable();
  private final long mSilenceDurationMs;
  
  SilencePlaybackQueueItem(TextToSpeechService.UtteranceProgressDispatcher paramUtteranceProgressDispatcher, Object paramObject, long paramLong)
  {
    super(paramUtteranceProgressDispatcher, paramObject);
    this.mSilenceDurationMs = paramLong;
  }
  
  public void run()
  {
    getDispatcher().dispatchOnStart();
    boolean bool = false;
    if (this.mSilenceDurationMs > 0L) {
      bool = this.mCondVar.block(this.mSilenceDurationMs);
    }
    if (bool)
    {
      getDispatcher().dispatchOnStop();
      return;
    }
    getDispatcher().dispatchOnSuccess();
  }
  
  void stop(int paramInt)
  {
    this.mCondVar.open();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/SilencePlaybackQueueItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */