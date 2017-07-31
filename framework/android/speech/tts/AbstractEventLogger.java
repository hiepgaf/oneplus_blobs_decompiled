package android.speech.tts;

import android.os.SystemClock;

abstract class AbstractEventLogger
{
  protected final int mCallerPid;
  protected final int mCallerUid;
  private volatile long mEngineCompleteTime = -1L;
  private volatile long mEngineStartTime = -1L;
  private boolean mLogWritten = false;
  protected long mPlaybackStartTime = -1L;
  protected final long mReceivedTime;
  private volatile long mRequestProcessingStartTime = -1L;
  protected final String mServiceApp;
  
  AbstractEventLogger(int paramInt1, int paramInt2, String paramString)
  {
    this.mCallerUid = paramInt1;
    this.mCallerPid = paramInt2;
    this.mServiceApp = paramString;
    this.mReceivedTime = SystemClock.elapsedRealtime();
  }
  
  protected abstract void logFailure(int paramInt);
  
  protected abstract void logSuccess(long paramLong1, long paramLong2, long paramLong3);
  
  public void onAudioDataWritten()
  {
    if (this.mPlaybackStartTime == -1L) {
      this.mPlaybackStartTime = SystemClock.elapsedRealtime();
    }
  }
  
  public void onCompleted(int paramInt)
  {
    if (this.mLogWritten) {
      return;
    }
    this.mLogWritten = true;
    SystemClock.elapsedRealtime();
    if ((paramInt != 0) || (this.mPlaybackStartTime == -1L)) {}
    while (this.mEngineCompleteTime == -1L)
    {
      logFailure(paramInt);
      return;
    }
    logSuccess(this.mPlaybackStartTime - this.mReceivedTime, this.mEngineStartTime - this.mRequestProcessingStartTime, this.mEngineCompleteTime - this.mRequestProcessingStartTime);
  }
  
  public void onEngineComplete()
  {
    this.mEngineCompleteTime = SystemClock.elapsedRealtime();
  }
  
  public void onEngineDataReceived()
  {
    if (this.mEngineStartTime == -1L) {
      this.mEngineStartTime = SystemClock.elapsedRealtime();
    }
  }
  
  public void onRequestProcessingStart()
  {
    this.mRequestProcessingStartTime = SystemClock.elapsedRealtime();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/AbstractEventLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */