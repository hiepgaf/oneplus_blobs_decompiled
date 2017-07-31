package android.media.tv;

import android.content.Context;
import android.graphics.Rect;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.Surface;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.HandlerCaller.Callback;
import com.android.internal.os.SomeArgs;

public class ITvInputSessionWrapper
  extends ITvInputSession.Stub
  implements HandlerCaller.Callback
{
  private static final int DO_APP_PRIVATE_COMMAND = 9;
  private static final int DO_CREATE_OVERLAY_VIEW = 10;
  private static final int DO_DISPATCH_SURFACE_CHANGED = 4;
  private static final int DO_RELAYOUT_OVERLAY_VIEW = 11;
  private static final int DO_RELEASE = 1;
  private static final int DO_REMOVE_OVERLAY_VIEW = 12;
  private static final int DO_SELECT_TRACK = 8;
  private static final int DO_SET_CAPTION_ENABLED = 7;
  private static final int DO_SET_MAIN = 2;
  private static final int DO_SET_STREAM_VOLUME = 5;
  private static final int DO_SET_SURFACE = 3;
  private static final int DO_START_RECORDING = 20;
  private static final int DO_STOP_RECORDING = 21;
  private static final int DO_TIME_SHIFT_ENABLE_POSITION_TRACKING = 19;
  private static final int DO_TIME_SHIFT_PAUSE = 15;
  private static final int DO_TIME_SHIFT_PLAY = 14;
  private static final int DO_TIME_SHIFT_RESUME = 16;
  private static final int DO_TIME_SHIFT_SEEK_TO = 17;
  private static final int DO_TIME_SHIFT_SET_PLAYBACK_PARAMS = 18;
  private static final int DO_TUNE = 6;
  private static final int DO_UNBLOCK_CONTENT = 13;
  private static final int EXECUTE_MESSAGE_TIMEOUT_LONG_MILLIS = 5000;
  private static final int EXECUTE_MESSAGE_TIMEOUT_SHORT_MILLIS = 50;
  private static final int EXECUTE_MESSAGE_TUNE_TIMEOUT_MILLIS = 2000;
  private static final String TAG = "TvInputSessionWrapper";
  private final HandlerCaller mCaller;
  private InputChannel mChannel;
  private final boolean mIsRecordingSession;
  private TvInputEventReceiver mReceiver;
  private TvInputService.RecordingSession mTvInputRecordingSessionImpl;
  private TvInputService.Session mTvInputSessionImpl;
  
  public ITvInputSessionWrapper(Context paramContext, TvInputService.RecordingSession paramRecordingSession)
  {
    this.mIsRecordingSession = true;
    this.mCaller = new HandlerCaller(paramContext, null, this, true);
    this.mTvInputRecordingSessionImpl = paramRecordingSession;
  }
  
  public ITvInputSessionWrapper(Context paramContext, TvInputService.Session paramSession, InputChannel paramInputChannel)
  {
    this.mIsRecordingSession = false;
    this.mCaller = new HandlerCaller(paramContext, null, this, true);
    this.mTvInputSessionImpl = paramSession;
    this.mChannel = paramInputChannel;
    if (paramInputChannel != null) {
      this.mReceiver = new TvInputEventReceiver(paramInputChannel, paramContext.getMainLooper());
    }
  }
  
  public void appPrivateCommand(String paramString, Bundle paramBundle)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageOO(9, paramString, paramBundle));
  }
  
  public void createOverlayView(IBinder paramIBinder, Rect paramRect)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageOO(10, paramIBinder, paramRect));
  }
  
  public void dispatchSurfaceChanged(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageIIII(4, paramInt1, paramInt2, paramInt3, 0));
  }
  
  public void executeMessage(Message paramMessage)
  {
    if ((this.mIsRecordingSession) && (this.mTvInputRecordingSessionImpl == null)) {}
    while ((!this.mIsRecordingSession) && (this.mTvInputSessionImpl == null)) {
      return;
    }
    long l = System.nanoTime();
    switch (paramMessage.what)
    {
    default: 
      Log.w("TvInputSessionWrapper", "Unhandled message code: " + paramMessage.what);
    }
    for (;;)
    {
      l = (System.nanoTime() - l) / 1000000L;
      if (l <= 50L) {
        return;
      }
      Log.w("TvInputSessionWrapper", "Handling message (" + paramMessage.what + ") took too long time (duration=" + l + "ms)");
      if ((paramMessage.what != 6) || (l <= 2000L)) {
        break;
      }
      throw new RuntimeException("Too much time to handle tune request. (" + l + "ms > " + 2000 + "ms) " + "Consider handling the tune request in a separate thread.");
      if (this.mIsRecordingSession)
      {
        this.mTvInputRecordingSessionImpl.release();
        this.mTvInputRecordingSessionImpl = null;
      }
      else
      {
        this.mTvInputSessionImpl.release();
        this.mTvInputSessionImpl = null;
        if (this.mReceiver != null)
        {
          this.mReceiver.dispose();
          this.mReceiver = null;
        }
        if (this.mChannel != null)
        {
          this.mChannel.dispose();
          this.mChannel = null;
          continue;
          this.mTvInputSessionImpl.setMain(((Boolean)paramMessage.obj).booleanValue());
          continue;
          this.mTvInputSessionImpl.setSurface((Surface)paramMessage.obj);
          continue;
          SomeArgs localSomeArgs = (SomeArgs)paramMessage.obj;
          this.mTvInputSessionImpl.dispatchSurfaceChanged(localSomeArgs.argi1, localSomeArgs.argi2, localSomeArgs.argi3);
          localSomeArgs.recycle();
          continue;
          this.mTvInputSessionImpl.setStreamVolume(((Float)paramMessage.obj).floatValue());
          continue;
          localSomeArgs = (SomeArgs)paramMessage.obj;
          if (this.mIsRecordingSession) {
            this.mTvInputRecordingSessionImpl.tune((Uri)localSomeArgs.arg1, (Bundle)localSomeArgs.arg2);
          }
          for (;;)
          {
            localSomeArgs.recycle();
            break;
            this.mTvInputSessionImpl.tune((Uri)localSomeArgs.arg1, (Bundle)localSomeArgs.arg2);
          }
          this.mTvInputSessionImpl.setCaptionEnabled(((Boolean)paramMessage.obj).booleanValue());
          continue;
          localSomeArgs = (SomeArgs)paramMessage.obj;
          this.mTvInputSessionImpl.selectTrack(((Integer)localSomeArgs.arg1).intValue(), (String)localSomeArgs.arg2);
          localSomeArgs.recycle();
          continue;
          localSomeArgs = (SomeArgs)paramMessage.obj;
          if (this.mIsRecordingSession) {
            this.mTvInputRecordingSessionImpl.appPrivateCommand((String)localSomeArgs.arg1, (Bundle)localSomeArgs.arg2);
          }
          for (;;)
          {
            localSomeArgs.recycle();
            break;
            this.mTvInputSessionImpl.appPrivateCommand((String)localSomeArgs.arg1, (Bundle)localSomeArgs.arg2);
          }
          localSomeArgs = (SomeArgs)paramMessage.obj;
          this.mTvInputSessionImpl.createOverlayView((IBinder)localSomeArgs.arg1, (Rect)localSomeArgs.arg2);
          localSomeArgs.recycle();
          continue;
          this.mTvInputSessionImpl.relayoutOverlayView((Rect)paramMessage.obj);
          continue;
          this.mTvInputSessionImpl.removeOverlayView(true);
          continue;
          this.mTvInputSessionImpl.unblockContent((String)paramMessage.obj);
          continue;
          this.mTvInputSessionImpl.timeShiftPlay((Uri)paramMessage.obj);
          continue;
          this.mTvInputSessionImpl.timeShiftPause();
          continue;
          this.mTvInputSessionImpl.timeShiftResume();
          continue;
          this.mTvInputSessionImpl.timeShiftSeekTo(((Long)paramMessage.obj).longValue());
          continue;
          this.mTvInputSessionImpl.timeShiftSetPlaybackParams((PlaybackParams)paramMessage.obj);
          continue;
          this.mTvInputSessionImpl.timeShiftEnablePositionTracking(((Boolean)paramMessage.obj).booleanValue());
          continue;
          this.mTvInputRecordingSessionImpl.startRecording((Uri)paramMessage.obj);
          continue;
          this.mTvInputRecordingSessionImpl.stopRecording();
        }
      }
    }
    if (l > 5000L) {
      throw new RuntimeException("Too much time to handle a request. (type=" + paramMessage.what + ", " + l + "ms > " + 5000 + "ms).");
    }
  }
  
  public void relayoutOverlayView(Rect paramRect)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(11, paramRect));
  }
  
  public void release()
  {
    if (!this.mIsRecordingSession) {
      this.mTvInputSessionImpl.scheduleOverlayViewCleanup();
    }
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessage(1));
  }
  
  public void removeOverlayView()
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessage(12));
  }
  
  public void selectTrack(int paramInt, String paramString)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageOO(8, Integer.valueOf(paramInt), paramString));
  }
  
  public void setCaptionEnabled(boolean paramBoolean)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(7, Boolean.valueOf(paramBoolean)));
  }
  
  public void setMain(boolean paramBoolean)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(2, Boolean.valueOf(paramBoolean)));
  }
  
  public void setSurface(Surface paramSurface)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(3, paramSurface));
  }
  
  public final void setVolume(float paramFloat)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(5, Float.valueOf(paramFloat)));
  }
  
  public void startRecording(Uri paramUri)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(20, paramUri));
  }
  
  public void stopRecording()
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessage(21));
  }
  
  public void timeShiftEnablePositionTracking(boolean paramBoolean)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(19, Boolean.valueOf(paramBoolean)));
  }
  
  public void timeShiftPause()
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessage(15));
  }
  
  public void timeShiftPlay(Uri paramUri)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(14, paramUri));
  }
  
  public void timeShiftResume()
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessage(16));
  }
  
  public void timeShiftSeekTo(long paramLong)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(17, Long.valueOf(paramLong)));
  }
  
  public void timeShiftSetPlaybackParams(PlaybackParams paramPlaybackParams)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(18, paramPlaybackParams));
  }
  
  public void tune(Uri paramUri, Bundle paramBundle)
  {
    this.mCaller.removeMessages(6);
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageOO(6, paramUri, paramBundle));
  }
  
  public void unblockContent(String paramString)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(13, paramString));
  }
  
  private final class TvInputEventReceiver
    extends InputEventReceiver
  {
    public TvInputEventReceiver(InputChannel paramInputChannel, Looper paramLooper)
    {
      super(paramLooper);
    }
    
    public void onInputEvent(InputEvent paramInputEvent)
    {
      boolean bool = true;
      if (ITvInputSessionWrapper.-get0(ITvInputSessionWrapper.this) == null)
      {
        finishInputEvent(paramInputEvent, false);
        return;
      }
      int i = ITvInputSessionWrapper.-get0(ITvInputSessionWrapper.this).dispatchInputEvent(paramInputEvent, this);
      if (i != -1) {
        if (i != 1) {
          break label49;
        }
      }
      for (;;)
      {
        finishInputEvent(paramInputEvent, bool);
        return;
        label49:
        bool = false;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/ITvInputSessionWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */