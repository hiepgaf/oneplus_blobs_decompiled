package android.media;

import android.content.Context;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import java.util.LinkedList;

public class AsyncPlayer
{
  private static final int PLAY = 1;
  private static final int STOP = 2;
  private static final boolean mDebug = false;
  private final LinkedList<Command> mCmdQueue = new LinkedList();
  private MediaPlayer mPlayer;
  private int mState = 2;
  private String mTag;
  private Thread mThread;
  private PowerManager.WakeLock mWakeLock;
  
  public AsyncPlayer(String paramString)
  {
    if (paramString != null)
    {
      this.mTag = paramString;
      return;
    }
    this.mTag = "AsyncPlayer";
  }
  
  private void acquireWakeLock()
  {
    if (this.mWakeLock != null) {
      this.mWakeLock.acquire();
    }
  }
  
  private void enqueueLocked(Command paramCommand)
  {
    this.mCmdQueue.add(paramCommand);
    if (this.mThread == null)
    {
      acquireWakeLock();
      this.mThread = new Thread();
      this.mThread.start();
    }
  }
  
  private void releaseWakeLock()
  {
    if (this.mWakeLock != null) {
      this.mWakeLock.release();
    }
  }
  
  private void startSound(Command paramCommand)
  {
    try
    {
      MediaPlayer localMediaPlayer = new MediaPlayer();
      localMediaPlayer.setAudioAttributes(paramCommand.attributes);
      localMediaPlayer.setDataSource(paramCommand.context, paramCommand.uri);
      localMediaPlayer.setLooping(paramCommand.looping);
      localMediaPlayer.prepare();
      localMediaPlayer.start();
      if (this.mPlayer != null) {
        this.mPlayer.release();
      }
      this.mPlayer = localMediaPlayer;
      long l = SystemClock.uptimeMillis() - paramCommand.requestTime;
      if (l > 1000L) {
        Log.w(this.mTag, "Notification sound delayed by " + l + "msecs");
      }
      return;
    }
    catch (Exception localException)
    {
      Log.w(this.mTag, "error loading sound for " + paramCommand.uri, localException);
    }
  }
  
  public void play(Context paramContext, Uri paramUri, boolean paramBoolean, int paramInt)
  {
    if ((paramContext == null) || (paramUri == null)) {
      return;
    }
    try
    {
      play(paramContext, paramUri, paramBoolean, new AudioAttributes.Builder().setInternalLegacyStreamType(paramInt).build());
      return;
    }
    catch (IllegalArgumentException paramContext)
    {
      Log.e(this.mTag, "Call to deprecated AsyncPlayer.play() method caused:", paramContext);
    }
  }
  
  public void play(Context arg1, Uri paramUri, boolean paramBoolean, AudioAttributes paramAudioAttributes)
    throws IllegalArgumentException
  {
    if ((??? == null) || (paramUri == null)) {}
    while (paramAudioAttributes == null) {
      throw new IllegalArgumentException("Illegal null AsyncPlayer.play() argument");
    }
    Command localCommand = new Command(null);
    localCommand.requestTime = SystemClock.uptimeMillis();
    localCommand.code = 1;
    localCommand.context = ???;
    localCommand.uri = paramUri;
    localCommand.looping = paramBoolean;
    localCommand.attributes = paramAudioAttributes;
    synchronized (this.mCmdQueue)
    {
      enqueueLocked(localCommand);
      this.mState = 1;
      return;
    }
  }
  
  public void setUsesWakeLock(Context paramContext)
  {
    if ((this.mWakeLock != null) || (this.mThread != null)) {
      throw new RuntimeException("assertion failed mWakeLock=" + this.mWakeLock + " mThread=" + this.mThread);
    }
    this.mWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, this.mTag);
  }
  
  public void stop()
  {
    synchronized (this.mCmdQueue)
    {
      if (this.mState != 2)
      {
        Command localCommand = new Command(null);
        localCommand.requestTime = SystemClock.uptimeMillis();
        localCommand.code = 2;
        enqueueLocked(localCommand);
        this.mState = 2;
      }
      return;
    }
  }
  
  private static final class Command
  {
    AudioAttributes attributes;
    int code;
    Context context;
    boolean looping;
    long requestTime;
    Uri uri;
    
    public String toString()
    {
      return "{ code=" + this.code + " looping=" + this.looping + " attr=" + this.attributes + " uri=" + this.uri + " }";
    }
  }
  
  private final class Thread
    extends Thread
  {
    Thread()
    {
      super();
    }
    
    public void run()
    {
      for (;;)
      {
        synchronized (AsyncPlayer.-get0(AsyncPlayer.this))
        {
          AsyncPlayer.Command localCommand1 = (AsyncPlayer.Command)AsyncPlayer.-get0(AsyncPlayer.this).removeFirst();
          switch (localCommand1.code)
          {
          }
        }
        synchronized (AsyncPlayer.-get0(AsyncPlayer.this))
        {
          while (AsyncPlayer.-get0(AsyncPlayer.this).size() == 0)
          {
            AsyncPlayer.-set1(AsyncPlayer.this, null);
            AsyncPlayer.-wrap0(AsyncPlayer.this);
            return;
            localCommand2 = finally;
            throw localCommand2;
            AsyncPlayer.-wrap1(AsyncPlayer.this, localCommand2);
            continue;
            if (AsyncPlayer.-get1(AsyncPlayer.this) != null)
            {
              long l = SystemClock.uptimeMillis() - localCommand2.requestTime;
              if (l > 1000L) {
                Log.w(AsyncPlayer.-get2(AsyncPlayer.this), "Notification stop delayed by " + l + "msecs");
              }
              AsyncPlayer.-get1(AsyncPlayer.this).stop();
              AsyncPlayer.-get1(AsyncPlayer.this).release();
              AsyncPlayer.-set0(AsyncPlayer.this, null);
            }
            else
            {
              Log.w(AsyncPlayer.-get2(AsyncPlayer.this), "STOP command without a player");
            }
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AsyncPlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */