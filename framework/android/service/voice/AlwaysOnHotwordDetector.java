package android.service.voice;

import android.content.Intent;
import android.hardware.soundtrigger.IRecognitionStatusCallback.Stub;
import android.hardware.soundtrigger.KeyphraseEnrollmentInfo;
import android.hardware.soundtrigger.KeyphraseMetadata;
import android.hardware.soundtrigger.SoundTrigger.ConfidenceLevel;
import android.hardware.soundtrigger.SoundTrigger.GenericRecognitionEvent;
import android.hardware.soundtrigger.SoundTrigger.KeyphraseRecognitionEvent;
import android.hardware.soundtrigger.SoundTrigger.KeyphraseRecognitionExtra;
import android.hardware.soundtrigger.SoundTrigger.ModuleProperties;
import android.hardware.soundtrigger.SoundTrigger.RecognitionConfig;
import android.media.AudioFormat;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.app.IVoiceInteractionManagerService;
import java.io.PrintWriter;
import java.util.Locale;

public class AlwaysOnHotwordDetector
{
  static final boolean DBG = false;
  public static final int MANAGE_ACTION_ENROLL = 0;
  public static final int MANAGE_ACTION_RE_ENROLL = 1;
  public static final int MANAGE_ACTION_UN_ENROLL = 2;
  private static final int MSG_AVAILABILITY_CHANGED = 1;
  private static final int MSG_DETECTION_ERROR = 3;
  private static final int MSG_DETECTION_PAUSE = 4;
  private static final int MSG_DETECTION_RESUME = 5;
  private static final int MSG_HOTWORD_DETECTED = 2;
  public static final int RECOGNITION_FLAG_ALLOW_MULTIPLE_TRIGGERS = 2;
  public static final int RECOGNITION_FLAG_CAPTURE_TRIGGER_AUDIO = 1;
  public static final int RECOGNITION_FLAG_NONE = 0;
  public static final int RECOGNITION_MODE_USER_IDENTIFICATION = 2;
  public static final int RECOGNITION_MODE_VOICE_TRIGGER = 1;
  public static final int STATE_HARDWARE_UNAVAILABLE = -2;
  private static final int STATE_INVALID = -3;
  public static final int STATE_KEYPHRASE_ENROLLED = 2;
  public static final int STATE_KEYPHRASE_UNENROLLED = 1;
  public static final int STATE_KEYPHRASE_UNSUPPORTED = -1;
  private static final int STATE_NOT_READY = 0;
  private static final int STATUS_ERROR = Integer.MIN_VALUE;
  private static final int STATUS_OK = 0;
  static final String TAG = "AlwaysOnHotwordDetector";
  private int mAvailability = 0;
  private final Callback mExternalCallback;
  private final Handler mHandler;
  private final SoundTriggerListener mInternalCallback;
  private final KeyphraseEnrollmentInfo mKeyphraseEnrollmentInfo;
  private final KeyphraseMetadata mKeyphraseMetadata;
  private final Locale mLocale;
  private final Object mLock = new Object();
  private final IVoiceInteractionManagerService mModelManagementService;
  private final String mText;
  private final IVoiceInteractionService mVoiceInteractionService;
  
  public AlwaysOnHotwordDetector(String paramString, Locale paramLocale, Callback paramCallback, KeyphraseEnrollmentInfo paramKeyphraseEnrollmentInfo, IVoiceInteractionService paramIVoiceInteractionService, IVoiceInteractionManagerService paramIVoiceInteractionManagerService)
  {
    this.mText = paramString;
    this.mLocale = paramLocale;
    this.mKeyphraseEnrollmentInfo = paramKeyphraseEnrollmentInfo;
    this.mKeyphraseMetadata = this.mKeyphraseEnrollmentInfo.getKeyphraseMetadata(paramString, paramLocale);
    this.mExternalCallback = paramCallback;
    this.mHandler = new MyHandler();
    this.mInternalCallback = new SoundTriggerListener(this.mHandler);
    this.mVoiceInteractionService = paramIVoiceInteractionService;
    this.mModelManagementService = paramIVoiceInteractionManagerService;
    new RefreshAvailabiltyTask().execute(new Void[0]);
  }
  
  private Intent getManageIntentLocked(int paramInt)
  {
    if (this.mAvailability == -3) {
      throw new IllegalStateException("getManageIntent called on an invalid detector");
    }
    if ((this.mAvailability != 2) && (this.mAvailability != 1)) {
      throw new UnsupportedOperationException("Managing the given keyphrase is not supported");
    }
    return this.mKeyphraseEnrollmentInfo.getManageKeyphraseIntent(paramInt, this.mText, this.mLocale);
  }
  
  private int getSupportedRecognitionModesLocked()
  {
    if (this.mAvailability == -3) {
      throw new IllegalStateException("getSupportedRecognitionModes called on an invalid detector");
    }
    if ((this.mAvailability != 2) && (this.mAvailability != 1)) {
      throw new UnsupportedOperationException("Getting supported recognition modes for the keyphrase is not supported");
    }
    return this.mKeyphraseMetadata.recognitionModeFlags;
  }
  
  private void notifyStateChangedLocked()
  {
    Message localMessage = Message.obtain(this.mHandler, 1);
    localMessage.arg1 = this.mAvailability;
    localMessage.sendToTarget();
  }
  
  private int startRecognitionLocked(int paramInt)
  {
    SoundTrigger.KeyphraseRecognitionExtra localKeyphraseRecognitionExtra = new SoundTrigger.KeyphraseRecognitionExtra(this.mKeyphraseMetadata.id, this.mKeyphraseMetadata.recognitionModeFlags, 0, new SoundTrigger.ConfidenceLevel[0]);
    boolean bool1;
    if ((paramInt & 0x1) != 0)
    {
      bool1 = true;
      if ((paramInt & 0x2) == 0) {
        break label138;
      }
    }
    label138:
    for (boolean bool2 = true;; bool2 = false)
    {
      paramInt = Integer.MIN_VALUE;
      try
      {
        int i = this.mModelManagementService.startRecognition(this.mVoiceInteractionService, this.mKeyphraseMetadata.id, this.mLocale.toLanguageTag(), this.mInternalCallback, new SoundTrigger.RecognitionConfig(bool1, bool2, new SoundTrigger.KeyphraseRecognitionExtra[] { localKeyphraseRecognitionExtra }, null));
        paramInt = i;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Slog.w("AlwaysOnHotwordDetector", "RemoteException in startRecognition!", localRemoteException);
        }
      }
      if (paramInt != 0) {
        Slog.w("AlwaysOnHotwordDetector", "startRecognition() failed with error code " + paramInt);
      }
      return paramInt;
      bool1 = false;
      break;
    }
  }
  
  private int stopRecognitionLocked()
  {
    int i = Integer.MIN_VALUE;
    try
    {
      int j = this.mModelManagementService.stopRecognition(this.mVoiceInteractionService, this.mKeyphraseMetadata.id, this.mInternalCallback);
      i = j;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w("AlwaysOnHotwordDetector", "RemoteException in stopRecognition!", localRemoteException);
      }
    }
    if (i != 0) {
      Slog.w("AlwaysOnHotwordDetector", "stopRecognition() failed with error code " + i);
    }
    return i;
  }
  
  public Intent createEnrollIntent()
  {
    synchronized (this.mLock)
    {
      Intent localIntent = getManageIntentLocked(0);
      return localIntent;
    }
  }
  
  public Intent createReEnrollIntent()
  {
    synchronized (this.mLock)
    {
      Intent localIntent = getManageIntentLocked(1);
      return localIntent;
    }
  }
  
  public Intent createUnEnrollIntent()
  {
    synchronized (this.mLock)
    {
      Intent localIntent = getManageIntentLocked(2);
      return localIntent;
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    synchronized (this.mLock)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Text=");
      paramPrintWriter.println(this.mText);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Locale=");
      paramPrintWriter.println(this.mLocale);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Availability=");
      paramPrintWriter.println(this.mAvailability);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("KeyphraseMetadata=");
      paramPrintWriter.println(this.mKeyphraseMetadata);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("EnrollmentInfo=");
      paramPrintWriter.println(this.mKeyphraseEnrollmentInfo);
      return;
    }
  }
  
  public int getSupportedRecognitionModes()
  {
    synchronized (this.mLock)
    {
      int i = getSupportedRecognitionModesLocked();
      return i;
    }
  }
  
  void invalidate()
  {
    synchronized (this.mLock)
    {
      this.mAvailability = -3;
      notifyStateChangedLocked();
      return;
    }
  }
  
  void onSoundModelsChanged()
  {
    synchronized (this.mLock)
    {
      if ((this.mAvailability == -3) || (this.mAvailability == -2)) {}
      while (this.mAvailability == -1)
      {
        Slog.w("AlwaysOnHotwordDetector", "Received onSoundModelsChanged for an unsupported keyphrase/config");
        return;
      }
      stopRecognitionLocked();
      new RefreshAvailabiltyTask().execute(new Void[0]);
      return;
    }
  }
  
  public boolean startRecognition(int paramInt)
  {
    boolean bool = false;
    synchronized (this.mLock)
    {
      if (this.mAvailability == -3) {
        throw new IllegalStateException("startRecognition called on an invalid detector");
      }
    }
    if (this.mAvailability != 2) {
      throw new UnsupportedOperationException("Recognition for the given keyphrase is not supported");
    }
    paramInt = startRecognitionLocked(paramInt);
    if (paramInt == 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean stopRecognition()
  {
    boolean bool = false;
    synchronized (this.mLock)
    {
      if (this.mAvailability == -3) {
        throw new IllegalStateException("stopRecognition called on an invalid detector");
      }
    }
    if (this.mAvailability != 2) {
      throw new UnsupportedOperationException("Recognition for the given keyphrase is not supported");
    }
    int i = stopRecognitionLocked();
    if (i == 0) {
      bool = true;
    }
    return bool;
  }
  
  public static abstract class Callback
  {
    public abstract void onAvailabilityChanged(int paramInt);
    
    public abstract void onDetected(AlwaysOnHotwordDetector.EventPayload paramEventPayload);
    
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
  
  class MyHandler
    extends Handler
  {
    MyHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      synchronized (AlwaysOnHotwordDetector.-get4(AlwaysOnHotwordDetector.this))
      {
        if (AlwaysOnHotwordDetector.-get0(AlwaysOnHotwordDetector.this) == -3)
        {
          Slog.w("AlwaysOnHotwordDetector", "Received message: " + paramMessage.what + " for an invalid detector");
          return;
        }
        switch (paramMessage.what)
        {
        default: 
          super.handleMessage(paramMessage);
          return;
        }
      }
      AlwaysOnHotwordDetector.-get1(AlwaysOnHotwordDetector.this).onAvailabilityChanged(paramMessage.arg1);
      return;
      AlwaysOnHotwordDetector.-get1(AlwaysOnHotwordDetector.this).onDetected((AlwaysOnHotwordDetector.EventPayload)paramMessage.obj);
      return;
      AlwaysOnHotwordDetector.-get1(AlwaysOnHotwordDetector.this).onError();
      return;
      AlwaysOnHotwordDetector.-get1(AlwaysOnHotwordDetector.this).onRecognitionPaused();
      return;
      AlwaysOnHotwordDetector.-get1(AlwaysOnHotwordDetector.this).onRecognitionResumed();
    }
  }
  
  class RefreshAvailabiltyTask
    extends AsyncTask<Void, Void, Void>
  {
    RefreshAvailabiltyTask() {}
    
    private int internalGetInitialAvailability()
    {
      synchronized (AlwaysOnHotwordDetector.-get4(AlwaysOnHotwordDetector.this))
      {
        int i = AlwaysOnHotwordDetector.-get0(AlwaysOnHotwordDetector.this);
        if (i == -3) {
          return -3;
        }
        ??? = null;
        try
        {
          SoundTrigger.ModuleProperties localModuleProperties = AlwaysOnHotwordDetector.-get5(AlwaysOnHotwordDetector.this).getDspModuleProperties(AlwaysOnHotwordDetector.-get6(AlwaysOnHotwordDetector.this));
          ??? = localModuleProperties;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.w("AlwaysOnHotwordDetector", "RemoteException in getDspProperties!", localRemoteException);
          }
          if (AlwaysOnHotwordDetector.-get2(AlwaysOnHotwordDetector.this) != null) {
            break label92;
          }
          return -1;
        }
        if (??? == null) {
          return -2;
        }
      }
      label92:
      return 0;
    }
    
    private boolean internalGetIsEnrolled(int paramInt, Locale paramLocale)
    {
      try
      {
        boolean bool = AlwaysOnHotwordDetector.-get5(AlwaysOnHotwordDetector.this).isEnrolledForKeyphrase(AlwaysOnHotwordDetector.-get6(AlwaysOnHotwordDetector.this), paramInt, paramLocale.toLanguageTag());
        return bool;
      }
      catch (RemoteException paramLocale)
      {
        Slog.w("AlwaysOnHotwordDetector", "RemoteException in listRegisteredKeyphraseSoundModels!", paramLocale);
      }
      return false;
    }
    
    public Void doInBackground(Void... arg1)
    {
      int j = internalGetInitialAvailability();
      if ((j == 0) || (j == 1)) {}
      for (;;)
      {
        int i;
        if (!internalGetIsEnrolled(AlwaysOnHotwordDetector.-get2(AlwaysOnHotwordDetector.this).id, AlwaysOnHotwordDetector.-get3(AlwaysOnHotwordDetector.this))) {
          i = 1;
        }
        synchronized (AlwaysOnHotwordDetector.-get4(AlwaysOnHotwordDetector.this))
        {
          do
          {
            AlwaysOnHotwordDetector.-set0(AlwaysOnHotwordDetector.this, i);
            AlwaysOnHotwordDetector.-wrap0(AlwaysOnHotwordDetector.this);
            return null;
            i = j;
          } while (j != 2);
          continue;
          i = 2;
        }
      }
    }
  }
  
  static final class SoundTriggerListener
    extends IRecognitionStatusCallback.Stub
  {
    private final Handler mHandler;
    
    public SoundTriggerListener(Handler paramHandler)
    {
      this.mHandler = paramHandler;
    }
    
    public void onError(int paramInt)
    {
      Slog.i("AlwaysOnHotwordDetector", "onError: " + paramInt);
      this.mHandler.sendEmptyMessage(3);
    }
    
    public void onGenericSoundTriggerDetected(SoundTrigger.GenericRecognitionEvent paramGenericRecognitionEvent)
    {
      Slog.w("AlwaysOnHotwordDetector", "Generic sound trigger event detected at AOHD: " + paramGenericRecognitionEvent);
    }
    
    public void onKeyphraseDetected(SoundTrigger.KeyphraseRecognitionEvent paramKeyphraseRecognitionEvent)
    {
      Slog.i("AlwaysOnHotwordDetector", "onDetected");
      Message.obtain(this.mHandler, 2, new AlwaysOnHotwordDetector.EventPayload(paramKeyphraseRecognitionEvent.triggerInData, paramKeyphraseRecognitionEvent.captureAvailable, paramKeyphraseRecognitionEvent.captureFormat, paramKeyphraseRecognitionEvent.captureSession, paramKeyphraseRecognitionEvent.data, null)).sendToTarget();
    }
    
    public void onRecognitionPaused()
    {
      Slog.i("AlwaysOnHotwordDetector", "onRecognitionPaused");
      this.mHandler.sendEmptyMessage(4);
    }
    
    public void onRecognitionResumed()
    {
      Slog.i("AlwaysOnHotwordDetector", "onRecognitionResumed");
      this.mHandler.sendEmptyMessage(5);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/voice/AlwaysOnHotwordDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */