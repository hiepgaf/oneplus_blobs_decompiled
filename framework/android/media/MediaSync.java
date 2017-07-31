package android.media;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class MediaSync
{
  private static final int CB_RETURN_AUDIO_BUFFER = 1;
  private static final int EVENT_CALLBACK = 1;
  private static final int EVENT_SET_CALLBACK = 2;
  public static final int MEDIASYNC_ERROR_AUDIOTRACK_FAIL = 1;
  public static final int MEDIASYNC_ERROR_SURFACE_FAIL = 2;
  private static final String TAG = "MediaSync";
  private List<AudioBuffer> mAudioBuffers = new LinkedList();
  private Handler mAudioHandler = null;
  private final Object mAudioLock = new Object();
  private Looper mAudioLooper = null;
  private Thread mAudioThread = null;
  private AudioTrack mAudioTrack = null;
  private Callback mCallback = null;
  private Handler mCallbackHandler = null;
  private final Object mCallbackLock = new Object();
  private long mNativeContext;
  private OnErrorListener mOnErrorListener = null;
  private Handler mOnErrorListenerHandler = null;
  private final Object mOnErrorListenerLock = new Object();
  private float mPlaybackRate = 0.0F;
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  public MediaSync()
  {
    native_setup();
  }
  
  private void createAudioThread()
  {
    this.mAudioThread = new Thread()
    {
      public void run()
      {
        
        synchronized (MediaSync.-get1(MediaSync.this))
        {
          MediaSync.-set1(MediaSync.this, Looper.myLooper());
          MediaSync.-set0(MediaSync.this, new Handler());
          MediaSync.-get1(MediaSync.this).notify();
          Looper.loop();
          return;
        }
      }
    };
    this.mAudioThread.start();
    try
    {
      synchronized (this.mAudioLock)
      {
        this.mAudioLock.wait();
        return;
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;) {}
    }
  }
  
  private final native void native_finalize();
  
  private final native void native_flush();
  
  private final native long native_getPlayTimeForPendingAudioFrames();
  
  private final native boolean native_getTimestamp(MediaTimestamp paramMediaTimestamp);
  
  private static final native void native_init();
  
  private final native void native_release();
  
  private final native void native_setAudioTrack(AudioTrack paramAudioTrack);
  
  private native float native_setPlaybackParams(PlaybackParams paramPlaybackParams);
  
  private final native void native_setSurface(Surface paramSurface);
  
  private native float native_setSyncParams(SyncParams paramSyncParams);
  
  private final native void native_setup();
  
  private final native void native_updateQueuedAudioData(int paramInt, long paramLong);
  
  private void postRenderAudio(long paramLong)
  {
    this.mAudioHandler.postDelayed(new Runnable()
    {
      public void run()
      {
        synchronized (MediaSync.-get1(MediaSync.this))
        {
          float f = MediaSync.-get6(MediaSync.this);
          if (f == 0.0D) {
            return;
          }
          boolean bool = MediaSync.-get0(MediaSync.this).isEmpty();
          if (bool) {
            return;
          }
          MediaSync.AudioBuffer localAudioBuffer = (MediaSync.AudioBuffer)MediaSync.-get0(MediaSync.this).get(0);
          int i = localAudioBuffer.mByteBuffer.remaining();
          int j;
          if (i > 0)
          {
            j = MediaSync.-get2(MediaSync.this).getPlayState();
            if (j == 3) {}
          }
          try
          {
            MediaSync.-get2(MediaSync.this).play();
            j = MediaSync.-get2(MediaSync.this).write(localAudioBuffer.mByteBuffer, i, 1);
            if (j > 0)
            {
              if (localAudioBuffer.mPresentationTimeUs != -1L)
              {
                MediaSync.-wrap1(MediaSync.this, i, localAudioBuffer.mPresentationTimeUs);
                localAudioBuffer.mPresentationTimeUs = -1L;
              }
              if (j == i)
              {
                MediaSync.-wrap3(MediaSync.this, localAudioBuffer);
                MediaSync.-get0(MediaSync.this).remove(0);
                if (!MediaSync.-get0(MediaSync.this).isEmpty()) {
                  MediaSync.-wrap2(MediaSync.this, 0L);
                }
                return;
              }
            }
          }
          catch (IllegalStateException localIllegalStateException)
          {
            for (;;)
            {
              Log.w("MediaSync", "could not start audio track");
            }
          }
        }
        long l = TimeUnit.MICROSECONDS.toMillis(MediaSync.-wrap0(MediaSync.this));
        MediaSync.-wrap2(MediaSync.this, l / 2L);
      }
    }, paramLong);
  }
  
  private final void postReturnByteBuffer(final AudioBuffer paramAudioBuffer)
  {
    synchronized (this.mCallbackLock)
    {
      if (this.mCallbackHandler != null) {
        this.mCallbackHandler.post(new Runnable()
        {
          public void run()
          {
            synchronized (MediaSync.-get5(MediaSync.this))
            {
              MediaSync.Callback localCallback = MediaSync.-get3(MediaSync.this);
              if (MediaSync.-get4(MediaSync.this) != null)
              {
                Thread localThread1 = MediaSync.-get4(MediaSync.this).getLooper().getThread();
                Thread localThread2 = Thread.currentThread();
                if (localThread1 == localThread2) {}
              }
              else
              {
                return;
              }
              if (localCallback != null) {
                localCallback.onAudioBufferConsumed(jdField_this, paramAudioBuffer.mByteBuffer, paramAudioBuffer.mBufferIndex);
              }
              return;
            }
          }
        });
      }
      return;
    }
  }
  
  private final void returnAudioBuffers()
  {
    synchronized (this.mAudioLock)
    {
      Iterator localIterator = this.mAudioBuffers.iterator();
      if (localIterator.hasNext()) {
        postReturnByteBuffer((AudioBuffer)localIterator.next());
      }
    }
    this.mAudioBuffers.clear();
  }
  
  public final native Surface createInputSurface();
  
  protected void finalize()
  {
    native_finalize();
  }
  
  public void flush()
  {
    synchronized (this.mAudioLock)
    {
      this.mAudioBuffers.clear();
      this.mCallbackHandler.removeCallbacksAndMessages(null);
      if (this.mAudioTrack != null)
      {
        this.mAudioTrack.pause();
        this.mAudioTrack.flush();
        this.mAudioTrack.stop();
      }
      native_flush();
      return;
    }
  }
  
  public native PlaybackParams getPlaybackParams();
  
  public native SyncParams getSyncParams();
  
  public MediaTimestamp getTimestamp()
  {
    try
    {
      MediaTimestamp localMediaTimestamp = new MediaTimestamp();
      boolean bool = native_getTimestamp(localMediaTimestamp);
      if (bool) {
        return localMediaTimestamp;
      }
      return null;
    }
    catch (IllegalStateException localIllegalStateException) {}
    return null;
  }
  
  public void queueAudio(ByteBuffer paramByteBuffer, int paramInt, long paramLong)
  {
    if ((this.mAudioTrack == null) || (this.mAudioThread == null)) {
      throw new IllegalStateException("AudioTrack is NOT set or audio thread is not created");
    }
    synchronized (this.mAudioLock)
    {
      this.mAudioBuffers.add(new AudioBuffer(paramByteBuffer, paramInt, paramLong));
      if (this.mPlaybackRate != 0.0D) {
        postRenderAudio(0L);
      }
      return;
    }
  }
  
  public final void release()
  {
    returnAudioBuffers();
    if ((this.mAudioThread != null) && (this.mAudioLooper != null)) {
      this.mAudioLooper.quit();
    }
    setCallback(null, null);
    native_release();
  }
  
  public void setAudioTrack(AudioTrack paramAudioTrack)
  {
    native_setAudioTrack(paramAudioTrack);
    this.mAudioTrack = paramAudioTrack;
    if ((paramAudioTrack != null) && (this.mAudioThread == null)) {
      createAudioThread();
    }
  }
  
  public void setCallback(Callback paramCallback, Handler paramHandler)
  {
    Object localObject = this.mCallbackLock;
    if (paramHandler != null) {}
    for (;;)
    {
      try
      {
        this.mCallbackHandler = paramHandler;
        this.mCallback = paramCallback;
        return;
      }
      finally {}
      Looper localLooper = Looper.myLooper();
      paramHandler = localLooper;
      if (localLooper == null) {
        paramHandler = Looper.getMainLooper();
      }
      if (paramHandler == null) {
        this.mCallbackHandler = null;
      } else {
        this.mCallbackHandler = new Handler(paramHandler);
      }
    }
  }
  
  public void setOnErrorListener(OnErrorListener paramOnErrorListener, Handler paramHandler)
  {
    Object localObject = this.mOnErrorListenerLock;
    if (paramHandler != null) {}
    for (;;)
    {
      try
      {
        this.mOnErrorListenerHandler = paramHandler;
        this.mOnErrorListener = paramOnErrorListener;
        return;
      }
      finally {}
      Looper localLooper = Looper.myLooper();
      paramHandler = localLooper;
      if (localLooper == null) {
        paramHandler = Looper.getMainLooper();
      }
      if (paramHandler == null) {
        this.mOnErrorListenerHandler = null;
      } else {
        this.mOnErrorListenerHandler = new Handler(paramHandler);
      }
    }
  }
  
  public void setPlaybackParams(PlaybackParams paramPlaybackParams)
  {
    synchronized (this.mAudioLock)
    {
      this.mPlaybackRate = native_setPlaybackParams(paramPlaybackParams);
      if ((this.mPlaybackRate != 0.0D) && (this.mAudioThread != null)) {
        postRenderAudio(0L);
      }
      return;
    }
  }
  
  public void setSurface(Surface paramSurface)
  {
    native_setSurface(paramSurface);
  }
  
  public void setSyncParams(SyncParams paramSyncParams)
  {
    synchronized (this.mAudioLock)
    {
      this.mPlaybackRate = native_setSyncParams(paramSyncParams);
      if ((this.mPlaybackRate != 0.0D) && (this.mAudioThread != null)) {
        postRenderAudio(0L);
      }
      return;
    }
  }
  
  private static class AudioBuffer
  {
    public int mBufferIndex;
    public ByteBuffer mByteBuffer;
    long mPresentationTimeUs;
    
    public AudioBuffer(ByteBuffer paramByteBuffer, int paramInt, long paramLong)
    {
      this.mByteBuffer = paramByteBuffer;
      this.mBufferIndex = paramInt;
      this.mPresentationTimeUs = paramLong;
    }
  }
  
  public static abstract class Callback
  {
    public abstract void onAudioBufferConsumed(MediaSync paramMediaSync, ByteBuffer paramByteBuffer, int paramInt);
  }
  
  public static abstract interface OnErrorListener
  {
    public abstract void onError(MediaSync paramMediaSync, int paramInt1, int paramInt2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaSync.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */