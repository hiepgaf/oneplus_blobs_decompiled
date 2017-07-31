package android.media.soundtrigger;

import android.hardware.soundtrigger.IRecognitionStatusCallback.Stub;
import android.hardware.soundtrigger.SoundTrigger.GenericRecognitionEvent;
import android.hardware.soundtrigger.SoundTrigger.KeyphraseRecognitionEvent;
import android.hardware.soundtrigger.SoundTrigger.RecognitionConfig;
import android.hardware.soundtrigger.SoundTrigger.RecognitionEvent;
import android.media.AudioFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.app.ISoundTriggerService;
import java.io.PrintWriter;
import java.util.UUID;

public final class SoundTriggerDetector
{
  private static final boolean DBG = false;
  private static final int MSG_AVAILABILITY_CHANGED = 1;
  private static final int MSG_DETECTION_ERROR = 3;
  private static final int MSG_DETECTION_PAUSE = 4;
  private static final int MSG_DETECTION_RESUME = 5;
  private static final int MSG_SOUND_TRIGGER_DETECTED = 2;
  public static final int RECOGNITION_FLAG_ALLOW_MULTIPLE_TRIGGERS = 2;
  public static final int RECOGNITION_FLAG_CAPTURE_TRIGGER_AUDIO = 1;
  public static final int RECOGNITION_FLAG_NONE = 0;
  private static final String TAG = "SoundTriggerDetector";
  private final Callback mCallback;
  private final Handler mHandler;
  private final Object mLock = new Object();
  private final RecognitionCallback mRecognitionCallback;
  private final UUID mSoundModelId;
  private final ISoundTriggerService mSoundTriggerService;
  
  SoundTriggerDetector(ISoundTriggerService paramISoundTriggerService, UUID paramUUID, Callback paramCallback, Handler paramHandler)
  {
    this.mSoundTriggerService = paramISoundTriggerService;
    this.mSoundModelId = paramUUID;
    this.mCallback = paramCallback;
    if (paramHandler == null) {}
    for (this.mHandler = new MyHandler();; this.mHandler = new MyHandler(paramHandler.getLooper()))
    {
      this.mRecognitionCallback = new RecognitionCallback(null);
      return;
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramString = this.mLock;
  }
  
  public boolean startRecognition(int paramInt)
  {
    boolean bool3 = false;
    boolean bool1;
    if ((paramInt & 0x1) != 0)
    {
      bool1 = true;
      if ((paramInt & 0x2) == 0) {
        break label71;
      }
    }
    for (boolean bool2 = true;; bool2 = false)
    {
      label71:
      try
      {
        paramInt = this.mSoundTriggerService.startRecognition(new ParcelUuid(this.mSoundModelId), this.mRecognitionCallback, new SoundTrigger.RecognitionConfig(bool1, bool2, null, null));
        bool1 = bool3;
        if (paramInt == 0) {
          bool1 = true;
        }
        return bool1;
      }
      catch (RemoteException localRemoteException) {}
      bool1 = false;
      break;
    }
    return false;
  }
  
  public boolean stopRecognition()
  {
    boolean bool = false;
    try
    {
      int i = this.mSoundTriggerService.stopRecognition(new ParcelUuid(this.mSoundModelId), this.mRecognitionCallback);
      if (i == 0) {
        bool = true;
      }
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public static abstract class Callback
  {
    public abstract void onAvailabilityChanged(int paramInt);
    
    public abstract void onDetected(SoundTriggerDetector.EventPayload paramEventPayload);
    
    public abstract void onError();
    
    public abstract void onRecognitionPaused();
    
    public abstract void onRecognitionResumed();
  }
  
  public static class EventPayload
  {
    private final AudioFormat mAudioFormat;
    private final boolean mCaptureAvailable;
    private final int mCaptureSession;
    private final byte[] mData;
    private final boolean mTriggerAvailable;
    
    private EventPayload(boolean paramBoolean1, boolean paramBoolean2, AudioFormat paramAudioFormat, int paramInt, byte[] paramArrayOfByte)
    {
      this.mTriggerAvailable = paramBoolean1;
      this.mCaptureAvailable = paramBoolean2;
      this.mCaptureSession = paramInt;
      this.mAudioFormat = paramAudioFormat;
      this.mData = paramArrayOfByte;
    }
    
    public AudioFormat getCaptureAudioFormat()
    {
      return this.mAudioFormat;
    }
    
    public Integer getCaptureSession()
    {
      if (this.mCaptureAvailable) {
        return Integer.valueOf(this.mCaptureSession);
      }
      return null;
    }
    
    public byte[] getTriggerAudio()
    {
      if (this.mTriggerAvailable) {
        return this.mData;
      }
      return null;
    }
  }
  
  private class MyHandler
    extends Handler
  {
    MyHandler() {}
    
    MyHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (SoundTriggerDetector.-get0(SoundTriggerDetector.this) == null)
      {
        Slog.w("SoundTriggerDetector", "Received message: " + paramMessage.what + " for NULL callback.");
        return;
      }
      switch (paramMessage.what)
      {
      default: 
        super.handleMessage(paramMessage);
        return;
      case 2: 
        SoundTriggerDetector.-get0(SoundTriggerDetector.this).onDetected((SoundTriggerDetector.EventPayload)paramMessage.obj);
        return;
      case 3: 
        SoundTriggerDetector.-get0(SoundTriggerDetector.this).onError();
        return;
      case 4: 
        SoundTriggerDetector.-get0(SoundTriggerDetector.this).onRecognitionPaused();
        return;
      }
      SoundTriggerDetector.-get0(SoundTriggerDetector.this).onRecognitionResumed();
    }
  }
  
  private class RecognitionCallback
    extends IRecognitionStatusCallback.Stub
  {
    private RecognitionCallback() {}
    
    public void onError(int paramInt)
    {
      Slog.d("SoundTriggerDetector", "onError()" + paramInt);
      SoundTriggerDetector.-get1(SoundTriggerDetector.this).sendEmptyMessage(3);
    }
    
    public void onGenericSoundTriggerDetected(SoundTrigger.GenericRecognitionEvent paramGenericRecognitionEvent)
    {
      Slog.d("SoundTriggerDetector", "onGenericSoundTriggerDetected()" + paramGenericRecognitionEvent);
      Message.obtain(SoundTriggerDetector.-get1(SoundTriggerDetector.this), 2, new SoundTriggerDetector.EventPayload(paramGenericRecognitionEvent.triggerInData, paramGenericRecognitionEvent.captureAvailable, paramGenericRecognitionEvent.captureFormat, paramGenericRecognitionEvent.captureSession, paramGenericRecognitionEvent.data, null)).sendToTarget();
    }
    
    public void onKeyphraseDetected(SoundTrigger.KeyphraseRecognitionEvent paramKeyphraseRecognitionEvent)
    {
      Slog.e("SoundTriggerDetector", "Ignoring onKeyphraseDetected() called for " + paramKeyphraseRecognitionEvent);
    }
    
    public void onRecognitionPaused()
    {
      Slog.d("SoundTriggerDetector", "onRecognitionPaused()");
      SoundTriggerDetector.-get1(SoundTriggerDetector.this).sendEmptyMessage(4);
    }
    
    public void onRecognitionResumed()
    {
      Slog.d("SoundTriggerDetector", "onRecognitionResumed()");
      SoundTriggerDetector.-get1(SoundTriggerDetector.this).sendEmptyMessage(5);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/soundtrigger/SoundTriggerDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */