package android.speech.tts;

import android.media.AudioFormat;
import android.media.AudioFormat.Builder;
import android.media.AudioTrack;
import android.util.Log;

class BlockingAudioTrack
{
  private static final boolean DBG = false;
  private static final long MAX_PROGRESS_WAIT_MS = 2500L;
  private static final long MAX_SLEEP_TIME_MS = 2500L;
  private static final int MIN_AUDIO_BUFFER_SIZE = 8192;
  private static final long MIN_SLEEP_TIME_MS = 20L;
  private static final String TAG = "TTS.BlockingAudioTrack";
  private int mAudioBufferSize;
  private final int mAudioFormat;
  private final TextToSpeechService.AudioOutputParams mAudioParams;
  private AudioTrack mAudioTrack;
  private Object mAudioTrackLock = new Object();
  private final int mBytesPerFrame;
  private int mBytesWritten = 0;
  private final int mChannelCount;
  private boolean mIsShortUtterance;
  private final int mSampleRateInHz;
  private int mSessionId;
  private volatile boolean mStopped;
  
  BlockingAudioTrack(TextToSpeechService.AudioOutputParams paramAudioOutputParams, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mAudioParams = paramAudioOutputParams;
    this.mSampleRateInHz = paramInt1;
    this.mAudioFormat = paramInt2;
    this.mChannelCount = paramInt3;
    this.mBytesPerFrame = (AudioFormat.getBytesPerSample(this.mAudioFormat) * this.mChannelCount);
    this.mIsShortUtterance = false;
    this.mAudioBufferSize = 0;
    this.mBytesWritten = 0;
    this.mAudioTrack = null;
    this.mStopped = false;
  }
  
  private void blockUntilCompletion(AudioTrack paramAudioTrack)
  {
    int k = this.mBytesWritten / this.mBytesPerFrame;
    int i = -1;
    long l1 = 0L;
    for (;;)
    {
      int j = paramAudioTrack.getPlaybackHeadPosition();
      if ((j >= k) || (paramAudioTrack.getPlayState() != 3) || (this.mStopped)) {
        return;
      }
      long l3 = clip((k - j) * 1000 / paramAudioTrack.getSampleRate(), 20L, 2500L);
      if (j == i)
      {
        long l2 = l1 + l3;
        l1 = l2;
        if (l2 > 2500L) {
          Log.w("TTS.BlockingAudioTrack", "Waited unsuccessfully for 2500ms for AudioTrack to make progress, Aborting");
        }
      }
      else
      {
        l1 = 0L;
      }
      i = j;
      try
      {
        Thread.sleep(l3);
      }
      catch (InterruptedException paramAudioTrack) {}
    }
  }
  
  private void blockUntilDone(AudioTrack paramAudioTrack)
  {
    if (this.mBytesWritten <= 0) {
      return;
    }
    if (this.mIsShortUtterance)
    {
      blockUntilEstimatedCompletion();
      return;
    }
    blockUntilCompletion(paramAudioTrack);
  }
  
  private void blockUntilEstimatedCompletion()
  {
    long l = this.mBytesWritten / this.mBytesPerFrame * 1000 / this.mSampleRateInHz;
    try
    {
      Thread.sleep(l);
      return;
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  private static final float clip(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 < paramFloat2) {
      return paramFloat2;
    }
    if (paramFloat1 < paramFloat3) {
      return paramFloat1;
    }
    return paramFloat3;
  }
  
  private static final long clip(long paramLong1, long paramLong2, long paramLong3)
  {
    if (paramLong1 < paramLong2) {
      return paramLong2;
    }
    if (paramLong1 < paramLong3) {
      return paramLong1;
    }
    return paramLong3;
  }
  
  private AudioTrack createStreamingAudioTrack()
  {
    int i = getChannelConfig(this.mChannelCount);
    int j = Math.max(8192, AudioTrack.getMinBufferSize(this.mSampleRateInHz, i, this.mAudioFormat));
    Object localObject = new AudioFormat.Builder().setChannelMask(i).setEncoding(this.mAudioFormat).setSampleRate(this.mSampleRateInHz).build();
    localObject = new AudioTrack(this.mAudioParams.mAudioAttributes, (AudioFormat)localObject, j, 1, this.mAudioParams.mSessionId);
    if (((AudioTrack)localObject).getState() != 1)
    {
      Log.w("TTS.BlockingAudioTrack", "Unable to create audio track.");
      ((AudioTrack)localObject).release();
      return null;
    }
    this.mAudioBufferSize = j;
    setupVolume((AudioTrack)localObject, this.mAudioParams.mVolume, this.mAudioParams.mPan);
    return (AudioTrack)localObject;
  }
  
  static int getChannelConfig(int paramInt)
  {
    if (paramInt == 1) {
      return 4;
    }
    if (paramInt == 2) {
      return 12;
    }
    return 0;
  }
  
  private static void setupVolume(AudioTrack paramAudioTrack, float paramFloat1, float paramFloat2)
  {
    paramFloat1 = clip(paramFloat1, 0.0F, 1.0F);
    float f3 = clip(paramFloat2, -1.0F, 1.0F);
    paramFloat2 = paramFloat1;
    float f2 = paramFloat1;
    float f1;
    if (f3 > 0.0F) {
      f1 = paramFloat1 * (1.0F - f3);
    }
    for (;;)
    {
      if (paramAudioTrack.setStereoVolume(f1, f2) != 0) {
        Log.e("TTS.BlockingAudioTrack", "Failed to set volume");
      }
      return;
      f1 = paramFloat2;
      if (f3 < 0.0F)
      {
        f2 = paramFloat1 * (1.0F + f3);
        f1 = paramFloat2;
      }
    }
  }
  
  private static int writeToAudioTrack(AudioTrack paramAudioTrack, byte[] paramArrayOfByte)
  {
    if (paramAudioTrack.getPlayState() != 3) {
      paramAudioTrack.play();
    }
    int i = 0;
    for (;;)
    {
      int j;
      if (i < paramArrayOfByte.length)
      {
        j = paramAudioTrack.write(paramArrayOfByte, i, paramArrayOfByte.length);
        if (j > 0) {}
      }
      else
      {
        return i;
      }
      i += j;
    }
  }
  
  long getAudioLengthMs(int paramInt)
  {
    return paramInt / this.mBytesPerFrame * 1000 / this.mSampleRateInHz;
  }
  
  public boolean init()
  {
    AudioTrack localAudioTrack = createStreamingAudioTrack();
    synchronized (this.mAudioTrackLock)
    {
      this.mAudioTrack = localAudioTrack;
      if (localAudioTrack == null) {
        return false;
      }
    }
    return true;
  }
  
  public void stop()
  {
    synchronized (this.mAudioTrackLock)
    {
      if (this.mAudioTrack != null) {
        this.mAudioTrack.stop();
      }
      this.mStopped = true;
      return;
    }
  }
  
  public void waitAndRelease()
  {
    synchronized (this.mAudioTrackLock)
    {
      AudioTrack localAudioTrack = this.mAudioTrack;
      if (localAudioTrack == null) {
        return;
      }
    }
    if ((this.mBytesWritten >= this.mAudioBufferSize) || (this.mStopped)) {}
    for (;;)
    {
      if (!this.mStopped) {
        blockUntilDone(this.mAudioTrack);
      }
      synchronized (this.mAudioTrackLock)
      {
        this.mAudioTrack = null;
        ((AudioTrack)localObject2).release();
        return;
        this.mIsShortUtterance = true;
        ((AudioTrack)localObject2).stop();
      }
    }
  }
  
  public int write(byte[] paramArrayOfByte)
  {
    AudioTrack localAudioTrack;
    synchronized (this.mAudioTrackLock)
    {
      localAudioTrack = this.mAudioTrack;
      if ((localAudioTrack == null) || (this.mStopped)) {
        return -1;
      }
    }
    int i = writeToAudioTrack(localAudioTrack, paramArrayOfByte);
    this.mBytesWritten += i;
    return i;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/BlockingAudioTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */