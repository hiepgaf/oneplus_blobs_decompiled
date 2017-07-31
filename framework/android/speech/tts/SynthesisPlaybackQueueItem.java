package android.speech.tts;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class SynthesisPlaybackQueueItem
  extends PlaybackQueueItem
{
  private static final boolean DBG = false;
  private static final long MAX_UNCONSUMED_AUDIO_MS = 500L;
  private static final String TAG = "TTS.SynthQueueItem";
  private final BlockingAudioTrack mAudioTrack;
  private final LinkedList<ListEntry> mDataBufferList = new LinkedList();
  private volatile boolean mDone = false;
  private final Lock mListLock = new ReentrantLock();
  private final AbstractEventLogger mLogger;
  private final Condition mNotFull = this.mListLock.newCondition();
  private final Condition mReadReady = this.mListLock.newCondition();
  private volatile int mStatusCode = 0;
  private volatile boolean mStopped = false;
  private int mUnconsumedBytes = 0;
  
  SynthesisPlaybackQueueItem(TextToSpeechService.AudioOutputParams paramAudioOutputParams, int paramInt1, int paramInt2, int paramInt3, TextToSpeechService.UtteranceProgressDispatcher paramUtteranceProgressDispatcher, Object paramObject, AbstractEventLogger paramAbstractEventLogger)
  {
    super(paramUtteranceProgressDispatcher, paramObject);
    this.mAudioTrack = new BlockingAudioTrack(paramAudioOutputParams, paramInt1, paramInt2, paramInt3);
    this.mLogger = paramAbstractEventLogger;
  }
  
  private byte[] take()
    throws InterruptedException
  {
    try
    {
      this.mListLock.lock();
      for (;;)
      {
        if ((this.mDataBufferList.size() != 0) || (this.mStopped)) {}
        while (this.mDone)
        {
          boolean bool = this.mStopped;
          if (!bool) {
            break;
          }
          return null;
        }
        this.mReadReady.await();
      }
      localObject2 = (ListEntry)this.mDataBufferList.poll();
    }
    finally
    {
      this.mListLock.unlock();
    }
    if (localObject2 == null)
    {
      this.mListLock.unlock();
      return null;
    }
    this.mUnconsumedBytes -= ((ListEntry)localObject2).mBytes.length;
    this.mNotFull.signal();
    Object localObject2 = ((ListEntry)localObject2).mBytes;
    this.mListLock.unlock();
    return (byte[])localObject2;
  }
  
  void done()
  {
    try
    {
      this.mListLock.lock();
      this.mDone = true;
      this.mReadReady.signal();
      this.mNotFull.signal();
      return;
    }
    finally
    {
      this.mListLock.unlock();
    }
  }
  
  void put(byte[] paramArrayOfByte)
    throws InterruptedException
  {
    try
    {
      this.mListLock.lock();
      for (;;)
      {
        if ((this.mAudioTrack.getAudioLengthMs(this.mUnconsumedBytes) <= 500L) || (this.mStopped))
        {
          boolean bool = this.mStopped;
          if (!bool) {
            break;
          }
          return;
        }
        this.mNotFull.await();
      }
      this.mDataBufferList.add(new ListEntry(paramArrayOfByte));
    }
    finally
    {
      this.mListLock.unlock();
    }
    this.mUnconsumedBytes += paramArrayOfByte.length;
    this.mReadReady.signal();
    this.mListLock.unlock();
  }
  
  public void run()
  {
    TextToSpeechService.UtteranceProgressDispatcher localUtteranceProgressDispatcher = getDispatcher();
    localUtteranceProgressDispatcher.dispatchOnStart();
    if (!this.mAudioTrack.init())
    {
      localUtteranceProgressDispatcher.dispatchOnError(-5);
      return;
    }
    try
    {
      for (;;)
      {
        byte[] arrayOfByte = take();
        if (arrayOfByte == null) {
          break;
        }
        this.mAudioTrack.write(arrayOfByte);
        this.mLogger.onAudioDataWritten();
      }
      localUtteranceProgressDispatcher.dispatchOnSuccess();
    }
    catch (InterruptedException localInterruptedException)
    {
      this.mAudioTrack.waitAndRelease();
      if (this.mStatusCode != 0) {}
    }
    for (;;)
    {
      this.mLogger.onCompleted(this.mStatusCode);
      return;
      if (this.mStatusCode == -2) {
        localUtteranceProgressDispatcher.dispatchOnStop();
      } else {
        localUtteranceProgressDispatcher.dispatchOnError(this.mStatusCode);
      }
    }
  }
  
  void stop(int paramInt)
  {
    try
    {
      this.mListLock.lock();
      this.mStopped = true;
      this.mStatusCode = paramInt;
      this.mReadReady.signal();
      this.mNotFull.signal();
      this.mListLock.unlock();
      this.mAudioTrack.stop();
      return;
    }
    finally
    {
      this.mListLock.unlock();
    }
  }
  
  static final class ListEntry
  {
    final byte[] mBytes;
    
    ListEntry(byte[] paramArrayOfByte)
    {
      this.mBytes = paramArrayOfByte;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/SynthesisPlaybackQueueItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */