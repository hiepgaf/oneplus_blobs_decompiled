package android.speech.tts;

import android.util.Log;

class PlaybackSynthesisCallback
  extends AbstractSynthesisCallback
{
  private static final boolean DBG = false;
  private static final int MIN_AUDIO_BUFFER_SIZE = 8192;
  private static final String TAG = "PlaybackSynthesisRequest";
  private final TextToSpeechService.AudioOutputParams mAudioParams;
  private final AudioPlaybackHandler mAudioTrackHandler;
  private final Object mCallerIdentity;
  private final TextToSpeechService.UtteranceProgressDispatcher mDispatcher;
  private volatile boolean mDone = false;
  private SynthesisPlaybackQueueItem mItem = null;
  private final AbstractEventLogger mLogger;
  private final Object mStateLock = new Object();
  protected int mStatusCode;
  
  PlaybackSynthesisCallback(TextToSpeechService.AudioOutputParams paramAudioOutputParams, AudioPlaybackHandler paramAudioPlaybackHandler, TextToSpeechService.UtteranceProgressDispatcher paramUtteranceProgressDispatcher, Object paramObject, AbstractEventLogger paramAbstractEventLogger, boolean paramBoolean)
  {
    super(paramBoolean);
    this.mAudioParams = paramAudioOutputParams;
    this.mAudioTrackHandler = paramAudioPlaybackHandler;
    this.mDispatcher = paramUtteranceProgressDispatcher;
    this.mCallerIdentity = paramObject;
    this.mLogger = paramAbstractEventLogger;
    this.mStatusCode = 0;
  }
  
  public int audioAvailable(byte[] arg1, int paramInt1, int paramInt2)
  {
    if ((paramInt2 > getMaxBufferSize()) || (paramInt2 <= 0)) {
      throw new IllegalArgumentException("buffer is too large or of zero length (" + paramInt2 + " bytes)");
    }
    SynthesisPlaybackQueueItem localSynthesisPlaybackQueueItem;
    synchronized (this.mStateLock)
    {
      if (this.mItem == null)
      {
        this.mStatusCode = -5;
        return -1;
      }
      int i = this.mStatusCode;
      if (i != 0) {
        return -1;
      }
      if (this.mStatusCode == -2)
      {
        paramInt1 = errorCodeOnStop();
        return paramInt1;
      }
      localSynthesisPlaybackQueueItem = this.mItem;
      ??? = new byte[paramInt2];
      System.arraycopy(???, paramInt1, (byte[])???, 0, paramInt2);
      this.mDispatcher.dispatchOnAudioAvailable((byte[])???);
    }
  }
  
  public int done()
  {
    synchronized (this.mStateLock)
    {
      if (this.mDone)
      {
        Log.w("PlaybackSynthesisRequest", "Duplicate call to done()");
        return -1;
      }
      if (this.mStatusCode == -2)
      {
        i = errorCodeOnStop();
        return i;
      }
      this.mDone = true;
      if (this.mItem == null)
      {
        Log.w("PlaybackSynthesisRequest", "done() was called before start() call");
        if (this.mStatusCode == 0)
        {
          this.mDispatcher.dispatchOnSuccess();
          this.mLogger.onEngineComplete();
          return -1;
        }
        this.mDispatcher.dispatchOnError(this.mStatusCode);
      }
    }
    SynthesisPlaybackQueueItem localSynthesisPlaybackQueueItem = this.mItem;
    int i = this.mStatusCode;
    if (i == 0) {
      localSynthesisPlaybackQueueItem.done();
    }
    for (;;)
    {
      this.mLogger.onEngineComplete();
      return 0;
      localSynthesisPlaybackQueueItem.stop(i);
    }
  }
  
  public void error()
  {
    error(-3);
  }
  
  public void error(int paramInt)
  {
    synchronized (this.mStateLock)
    {
      boolean bool = this.mDone;
      if (bool) {
        return;
      }
      this.mStatusCode = paramInt;
      return;
    }
  }
  
  public int getMaxBufferSize()
  {
    return 8192;
  }
  
  public boolean hasFinished()
  {
    synchronized (this.mStateLock)
    {
      boolean bool = this.mDone;
      return bool;
    }
  }
  
  public boolean hasStarted()
  {
    synchronized (this.mStateLock)
    {
      SynthesisPlaybackQueueItem localSynthesisPlaybackQueueItem = this.mItem;
      if (localSynthesisPlaybackQueueItem != null)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  public int start(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt2 != 3) && (paramInt2 != 2) && (paramInt2 != 4)) {
      Log.w("PlaybackSynthesisRequest", "Audio format encoding " + paramInt2 + " not supported. Please use one " + "of AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT or " + "AudioFormat.ENCODING_PCM_FLOAT");
    }
    this.mDispatcher.dispatchOnBeginSynthesis(paramInt1, paramInt2, paramInt3);
    int i = BlockingAudioTrack.getChannelConfig(paramInt3);
    localObject1 = this.mStateLock;
    if (i == 0) {}
    try
    {
      Log.e("PlaybackSynthesisRequest", "Unsupported number of channels :" + paramInt3);
      this.mStatusCode = -5;
      return -1;
    }
    finally {}
    if (this.mStatusCode == -2)
    {
      paramInt1 = errorCodeOnStop();
      return paramInt1;
    }
    i = this.mStatusCode;
    if (i != 0) {
      return -1;
    }
    if (this.mItem != null)
    {
      Log.e("PlaybackSynthesisRequest", "Start called twice");
      return -1;
    }
    SynthesisPlaybackQueueItem localSynthesisPlaybackQueueItem = new SynthesisPlaybackQueueItem(this.mAudioParams, paramInt1, paramInt2, paramInt3, this.mDispatcher, this.mCallerIdentity, this.mLogger);
    this.mAudioTrackHandler.enqueue(localSynthesisPlaybackQueueItem);
    this.mItem = localSynthesisPlaybackQueueItem;
    return 0;
  }
  
  void stop()
  {
    synchronized (this.mStateLock)
    {
      boolean bool = this.mDone;
      if (bool) {
        return;
      }
      if (this.mStatusCode == -2)
      {
        Log.w("PlaybackSynthesisRequest", "stop() called twice");
        return;
      }
      SynthesisPlaybackQueueItem localSynthesisPlaybackQueueItem = this.mItem;
      this.mStatusCode = -2;
      if (localSynthesisPlaybackQueueItem != null)
      {
        localSynthesisPlaybackQueueItem.stop(-2);
        return;
      }
    }
    this.mLogger.onCompleted(-2);
    this.mDispatcher.dispatchOnStop();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/PlaybackSynthesisCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */