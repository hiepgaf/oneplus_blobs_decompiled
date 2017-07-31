package android.speech;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.util.SeempLog;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SpeechRecognizer
{
  public static final String CONFIDENCE_SCORES = "confidence_scores";
  private static final boolean DBG = false;
  public static final int ERROR_AUDIO = 3;
  public static final int ERROR_CLIENT = 5;
  public static final int ERROR_INSUFFICIENT_PERMISSIONS = 9;
  public static final int ERROR_NETWORK = 2;
  public static final int ERROR_NETWORK_TIMEOUT = 1;
  public static final int ERROR_NO_MATCH = 7;
  public static final int ERROR_RECOGNIZER_BUSY = 8;
  public static final int ERROR_SERVER = 4;
  public static final int ERROR_SPEECH_TIMEOUT = 6;
  private static final int MSG_CANCEL = 3;
  private static final int MSG_CHANGE_LISTENER = 4;
  private static final int MSG_START = 1;
  private static final int MSG_STOP = 2;
  public static final String RESULTS_RECOGNITION = "results_recognition";
  private static final String TAG = "SpeechRecognizer";
  private Connection mConnection;
  private final Context mContext;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        SpeechRecognizer.-wrap2(SpeechRecognizer.this, (Intent)paramAnonymousMessage.obj);
        return;
      case 2: 
        SpeechRecognizer.-wrap3(SpeechRecognizer.this);
        return;
      case 3: 
        SpeechRecognizer.-wrap0(SpeechRecognizer.this);
        return;
      }
      SpeechRecognizer.-wrap1(SpeechRecognizer.this, (RecognitionListener)paramAnonymousMessage.obj);
    }
  };
  private final InternalListener mListener = new InternalListener(null);
  private final Queue<Message> mPendingTasks = new LinkedList();
  private IRecognitionService mService;
  private final ComponentName mServiceComponent;
  
  private SpeechRecognizer(Context paramContext, ComponentName paramComponentName)
  {
    this.mContext = paramContext;
    this.mServiceComponent = paramComponentName;
  }
  
  private static void checkIsCalledFromMainThread()
  {
    if (Looper.myLooper() != Looper.getMainLooper()) {
      throw new RuntimeException("SpeechRecognizer should be used only from the application's main thread");
    }
  }
  
  private boolean checkOpenConnection()
  {
    if (this.mService != null) {
      return true;
    }
    this.mListener.onError(5);
    Log.e("SpeechRecognizer", "not connected to the recognition service");
    return false;
  }
  
  public static SpeechRecognizer createSpeechRecognizer(Context paramContext)
  {
    return createSpeechRecognizer(paramContext, null);
  }
  
  public static SpeechRecognizer createSpeechRecognizer(Context paramContext, ComponentName paramComponentName)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("Context cannot be null)");
    }
    checkIsCalledFromMainThread();
    return new SpeechRecognizer(paramContext, paramComponentName);
  }
  
  private void handleCancelMessage()
  {
    if (!checkOpenConnection()) {
      return;
    }
    try
    {
      this.mService.cancel(this.mListener);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SpeechRecognizer", "cancel() failed", localRemoteException);
      this.mListener.onError(5);
    }
  }
  
  private void handleChangeListener(RecognitionListener paramRecognitionListener)
  {
    InternalListener.-set0(this.mListener, paramRecognitionListener);
  }
  
  private void handleStartListening(Intent paramIntent)
  {
    if (!checkOpenConnection()) {
      return;
    }
    try
    {
      this.mService.startListening(paramIntent, this.mListener);
      return;
    }
    catch (RemoteException paramIntent)
    {
      Log.e("SpeechRecognizer", "startListening() failed", paramIntent);
      this.mListener.onError(5);
    }
  }
  
  private void handleStopMessage()
  {
    if (!checkOpenConnection()) {
      return;
    }
    try
    {
      this.mService.stopListening(this.mListener);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SpeechRecognizer", "stopListening() failed", localRemoteException);
      this.mListener.onError(5);
    }
  }
  
  public static boolean isRecognitionAvailable(Context paramContext)
  {
    boolean bool2 = false;
    paramContext = paramContext.getPackageManager().queryIntentServices(new Intent("android.speech.RecognitionService"), 0);
    boolean bool1 = bool2;
    if (paramContext != null)
    {
      bool1 = bool2;
      if (paramContext.size() != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private void putMessage(Message paramMessage)
  {
    if (this.mService == null)
    {
      this.mPendingTasks.offer(paramMessage);
      return;
    }
    this.mHandler.sendMessage(paramMessage);
  }
  
  public void cancel()
  {
    checkIsCalledFromMainThread();
    putMessage(Message.obtain(this.mHandler, 3));
  }
  
  public void destroy()
  {
    if (this.mService != null) {}
    try
    {
      this.mService.cancel(this.mListener);
      if (this.mConnection != null) {
        this.mContext.unbindService(this.mConnection);
      }
      this.mPendingTasks.clear();
      this.mService = null;
      this.mConnection = null;
      InternalListener.-set0(this.mListener, null);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public void setRecognitionListener(RecognitionListener paramRecognitionListener)
  {
    checkIsCalledFromMainThread();
    putMessage(Message.obtain(this.mHandler, 4, paramRecognitionListener));
  }
  
  public void startListening(Intent paramIntent)
  {
    SeempLog.record(72);
    if (paramIntent == null) {
      throw new IllegalArgumentException("intent must not be null");
    }
    checkIsCalledFromMainThread();
    if (this.mConnection == null)
    {
      this.mConnection = new Connection(null);
      Intent localIntent = new Intent("android.speech.RecognitionService");
      if (this.mServiceComponent == null)
      {
        String str = Settings.Secure.getString(this.mContext.getContentResolver(), "voice_recognition_service");
        if (TextUtils.isEmpty(str))
        {
          Log.e("SpeechRecognizer", "no selected voice recognition service");
          this.mListener.onError(5);
          return;
        }
        localIntent.setComponent(ComponentName.unflattenFromString(str));
      }
      while (!this.mContext.bindService(localIntent, this.mConnection, 1))
      {
        Log.e("SpeechRecognizer", "bind to recognition service failed");
        this.mConnection = null;
        this.mService = null;
        this.mListener.onError(5);
        return;
        localIntent.setComponent(this.mServiceComponent);
      }
    }
    putMessage(Message.obtain(this.mHandler, 1, paramIntent));
  }
  
  public void stopListening()
  {
    checkIsCalledFromMainThread();
    putMessage(Message.obtain(this.mHandler, 2));
  }
  
  private class Connection
    implements ServiceConnection
  {
    private Connection() {}
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      SpeechRecognizer.-set1(SpeechRecognizer.this, IRecognitionService.Stub.asInterface(paramIBinder));
      while (!SpeechRecognizer.-get1(SpeechRecognizer.this).isEmpty()) {
        SpeechRecognizer.-get0(SpeechRecognizer.this).sendMessage((Message)SpeechRecognizer.-get1(SpeechRecognizer.this).poll());
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      SpeechRecognizer.-set1(SpeechRecognizer.this, null);
      SpeechRecognizer.-set0(SpeechRecognizer.this, null);
      SpeechRecognizer.-get1(SpeechRecognizer.this).clear();
    }
  }
  
  private static class InternalListener
    extends IRecognitionListener.Stub
  {
    private static final int MSG_BEGINNING_OF_SPEECH = 1;
    private static final int MSG_BUFFER_RECEIVED = 2;
    private static final int MSG_END_OF_SPEECH = 3;
    private static final int MSG_ERROR = 4;
    private static final int MSG_ON_EVENT = 9;
    private static final int MSG_PARTIAL_RESULTS = 7;
    private static final int MSG_READY_FOR_SPEECH = 5;
    private static final int MSG_RESULTS = 6;
    private static final int MSG_RMS_CHANGED = 8;
    private final Handler mInternalHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        if (SpeechRecognizer.InternalListener.-get0(SpeechRecognizer.InternalListener.this) == null) {
          return;
        }
        switch (paramAnonymousMessage.what)
        {
        default: 
          return;
        case 1: 
          SpeechRecognizer.InternalListener.-get0(SpeechRecognizer.InternalListener.this).onBeginningOfSpeech();
          return;
        case 2: 
          SpeechRecognizer.InternalListener.-get0(SpeechRecognizer.InternalListener.this).onBufferReceived((byte[])paramAnonymousMessage.obj);
          return;
        case 3: 
          SpeechRecognizer.InternalListener.-get0(SpeechRecognizer.InternalListener.this).onEndOfSpeech();
          return;
        case 4: 
          SpeechRecognizer.InternalListener.-get0(SpeechRecognizer.InternalListener.this).onError(((Integer)paramAnonymousMessage.obj).intValue());
          return;
        case 5: 
          SpeechRecognizer.InternalListener.-get0(SpeechRecognizer.InternalListener.this).onReadyForSpeech((Bundle)paramAnonymousMessage.obj);
          return;
        case 6: 
          SpeechRecognizer.InternalListener.-get0(SpeechRecognizer.InternalListener.this).onResults((Bundle)paramAnonymousMessage.obj);
          return;
        case 7: 
          SpeechRecognizer.InternalListener.-get0(SpeechRecognizer.InternalListener.this).onPartialResults((Bundle)paramAnonymousMessage.obj);
          return;
        case 8: 
          SpeechRecognizer.InternalListener.-get0(SpeechRecognizer.InternalListener.this).onRmsChanged(((Float)paramAnonymousMessage.obj).floatValue());
          return;
        }
        SpeechRecognizer.InternalListener.-get0(SpeechRecognizer.InternalListener.this).onEvent(paramAnonymousMessage.arg1, (Bundle)paramAnonymousMessage.obj);
      }
    };
    private RecognitionListener mInternalListener;
    
    public void onBeginningOfSpeech()
    {
      Message.obtain(this.mInternalHandler, 1).sendToTarget();
    }
    
    public void onBufferReceived(byte[] paramArrayOfByte)
    {
      Message.obtain(this.mInternalHandler, 2, paramArrayOfByte).sendToTarget();
    }
    
    public void onEndOfSpeech()
    {
      Message.obtain(this.mInternalHandler, 3).sendToTarget();
    }
    
    public void onError(int paramInt)
    {
      Message.obtain(this.mInternalHandler, 4, Integer.valueOf(paramInt)).sendToTarget();
    }
    
    public void onEvent(int paramInt, Bundle paramBundle)
    {
      Message.obtain(this.mInternalHandler, 9, paramInt, paramInt, paramBundle).sendToTarget();
    }
    
    public void onPartialResults(Bundle paramBundle)
    {
      Message.obtain(this.mInternalHandler, 7, paramBundle).sendToTarget();
    }
    
    public void onReadyForSpeech(Bundle paramBundle)
    {
      Message.obtain(this.mInternalHandler, 5, paramBundle).sendToTarget();
    }
    
    public void onResults(Bundle paramBundle)
    {
      Message.obtain(this.mInternalHandler, 6, paramBundle).sendToTarget();
    }
    
    public void onRmsChanged(float paramFloat)
    {
      Message.obtain(this.mInternalHandler, 8, Float.valueOf(paramFloat)).sendToTarget();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/SpeechRecognizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */