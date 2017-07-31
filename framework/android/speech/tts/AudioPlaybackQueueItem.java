package android.speech.tts;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.ConditionVariable;
import android.util.Log;

class AudioPlaybackQueueItem
  extends PlaybackQueueItem
{
  private static final String TAG = "TTS.AudioQueueItem";
  private final TextToSpeechService.AudioOutputParams mAudioParams;
  private final Context mContext;
  private final ConditionVariable mDone;
  private volatile boolean mFinished;
  private MediaPlayer mPlayer;
  private final Uri mUri;
  
  AudioPlaybackQueueItem(TextToSpeechService.UtteranceProgressDispatcher paramUtteranceProgressDispatcher, Object paramObject, Context paramContext, Uri paramUri, TextToSpeechService.AudioOutputParams paramAudioOutputParams)
  {
    super(paramUtteranceProgressDispatcher, paramObject);
    this.mContext = paramContext;
    this.mUri = paramUri;
    this.mAudioParams = paramAudioOutputParams;
    this.mDone = new ConditionVariable();
    this.mPlayer = null;
    this.mFinished = false;
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
  
  private void finish()
  {
    try
    {
      this.mPlayer.stop();
      this.mPlayer.release();
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      for (;;) {}
    }
  }
  
  private static void setupVolume(MediaPlayer paramMediaPlayer, float paramFloat1, float paramFloat2)
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
      paramMediaPlayer.setVolume(f1, f2);
      return;
      f1 = paramFloat2;
      if (f3 < 0.0F)
      {
        f2 = paramFloat1 * (1.0F + f3);
        f1 = paramFloat2;
      }
    }
  }
  
  public void run()
  {
    TextToSpeechService.UtteranceProgressDispatcher localUtteranceProgressDispatcher = getDispatcher();
    localUtteranceProgressDispatcher.dispatchOnStart();
    int i = this.mAudioParams.mSessionId;
    Context localContext = this.mContext;
    Uri localUri = this.mUri;
    AudioAttributes localAudioAttributes = this.mAudioParams.mAudioAttributes;
    if (i > 0) {}
    for (;;)
    {
      this.mPlayer = MediaPlayer.create(localContext, localUri, null, localAudioAttributes, i);
      if (this.mPlayer != null) {
        break;
      }
      localUtteranceProgressDispatcher.dispatchOnError(-5);
      return;
      i = 0;
    }
    try
    {
      this.mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener()
      {
        public boolean onError(MediaPlayer paramAnonymousMediaPlayer, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          Log.w("TTS.AudioQueueItem", "Audio playback error: " + paramAnonymousInt1 + ", " + paramAnonymousInt2);
          AudioPlaybackQueueItem.-get0(AudioPlaybackQueueItem.this).open();
          return true;
        }
      });
      this.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
      {
        public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
        {
          AudioPlaybackQueueItem.-set0(AudioPlaybackQueueItem.this, true);
          AudioPlaybackQueueItem.-get0(AudioPlaybackQueueItem.this).open();
        }
      });
      setupVolume(this.mPlayer, this.mAudioParams.mVolume, this.mAudioParams.mPan);
      this.mPlayer.start();
      this.mDone.block();
      finish();
      if (this.mFinished)
      {
        localUtteranceProgressDispatcher.dispatchOnSuccess();
        return;
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;)
      {
        Log.w("TTS.AudioQueueItem", "MediaPlayer failed", localIllegalArgumentException);
        this.mDone.open();
      }
      localUtteranceProgressDispatcher.dispatchOnStop();
    }
  }
  
  void stop(int paramInt)
  {
    this.mDone.open();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/AudioPlaybackQueueItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */