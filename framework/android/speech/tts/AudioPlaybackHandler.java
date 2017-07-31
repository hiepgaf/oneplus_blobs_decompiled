package android.speech.tts;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

class AudioPlaybackHandler
{
  private static final boolean DBG = false;
  private static final String TAG = "TTS.AudioPlaybackHandler";
  private volatile PlaybackQueueItem mCurrentWorkItem = null;
  private final Thread mHandlerThread = new Thread(new MessageLoop(null), "TTS.AudioPlaybackThread");
  private final LinkedBlockingQueue<PlaybackQueueItem> mQueue = new LinkedBlockingQueue();
  
  private void removeAllMessages()
  {
    this.mQueue.clear();
  }
  
  private void removeWorkItemsFor(Object paramObject)
  {
    Iterator localIterator = this.mQueue.iterator();
    while (localIterator.hasNext()) {
      if (((PlaybackQueueItem)localIterator.next()).getCallerIdentity() == paramObject) {
        localIterator.remove();
      }
    }
  }
  
  private void stop(PlaybackQueueItem paramPlaybackQueueItem)
  {
    if (paramPlaybackQueueItem == null) {
      return;
    }
    paramPlaybackQueueItem.stop(-2);
  }
  
  public void enqueue(PlaybackQueueItem paramPlaybackQueueItem)
  {
    try
    {
      this.mQueue.put(paramPlaybackQueueItem);
      return;
    }
    catch (InterruptedException paramPlaybackQueueItem) {}
  }
  
  public boolean isSpeaking()
  {
    return (this.mQueue.peek() != null) || (this.mCurrentWorkItem != null);
  }
  
  public void quit()
  {
    removeAllMessages();
    stop(this.mCurrentWorkItem);
    this.mHandlerThread.interrupt();
  }
  
  public void start()
  {
    this.mHandlerThread.start();
  }
  
  public void stop()
  {
    removeAllMessages();
    stop(this.mCurrentWorkItem);
  }
  
  public void stopForApp(Object paramObject)
  {
    removeWorkItemsFor(paramObject);
    PlaybackQueueItem localPlaybackQueueItem = this.mCurrentWorkItem;
    if ((localPlaybackQueueItem != null) && (localPlaybackQueueItem.getCallerIdentity() == paramObject)) {
      stop(localPlaybackQueueItem);
    }
  }
  
  private final class MessageLoop
    implements Runnable
  {
    private MessageLoop() {}
    
    public void run()
    {
      try
      {
        for (;;)
        {
          PlaybackQueueItem localPlaybackQueueItem = (PlaybackQueueItem)AudioPlaybackHandler.-get0(AudioPlaybackHandler.this).take();
          AudioPlaybackHandler.-set0(AudioPlaybackHandler.this, localPlaybackQueueItem);
          localPlaybackQueueItem.run();
          AudioPlaybackHandler.-set0(AudioPlaybackHandler.this, null);
        }
        return;
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/AudioPlaybackHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */