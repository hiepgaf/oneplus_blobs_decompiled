package android.speech;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public abstract class RecognitionService
  extends Service
{
  private static final boolean DBG = false;
  private static final int MSG_CANCEL = 3;
  private static final int MSG_RESET = 4;
  private static final int MSG_START_LISTENING = 1;
  private static final int MSG_STOP_LISTENING = 2;
  public static final String SERVICE_INTERFACE = "android.speech.RecognitionService";
  public static final String SERVICE_META_DATA = "android.speech";
  private static final String TAG = "RecognitionService";
  private RecognitionServiceBinder mBinder = new RecognitionServiceBinder(this);
  private Callback mCurrentCallback = null;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        paramAnonymousMessage = (RecognitionService.StartListeningArgs)paramAnonymousMessage.obj;
        RecognitionService.-wrap3(RecognitionService.this, paramAnonymousMessage.mIntent, paramAnonymousMessage.mListener, paramAnonymousMessage.mCallingUid);
        return;
      case 2: 
        RecognitionService.-wrap4(RecognitionService.this, (IRecognitionListener)paramAnonymousMessage.obj);
        return;
      case 3: 
        RecognitionService.-wrap1(RecognitionService.this, (IRecognitionListener)paramAnonymousMessage.obj);
        return;
      }
      RecognitionService.-wrap2(RecognitionService.this);
    }
  };
  
  private boolean checkPermissions(IRecognitionListener paramIRecognitionListener)
  {
    if (checkCallingOrSelfPermission("android.permission.RECORD_AUDIO") == 0) {
      return true;
    }
    try
    {
      Log.e("RecognitionService", "call for recognition service without RECORD_AUDIO permissions");
      paramIRecognitionListener.onError(9);
      return false;
    }
    catch (RemoteException paramIRecognitionListener)
    {
      Log.e("RecognitionService", "sending ERROR_INSUFFICIENT_PERMISSIONS message failed", paramIRecognitionListener);
    }
    return false;
  }
  
  private void dispatchCancel(IRecognitionListener paramIRecognitionListener)
  {
    if (this.mCurrentCallback == null) {
      return;
    }
    if (Callback.-get0(this.mCurrentCallback).asBinder() != paramIRecognitionListener.asBinder())
    {
      Log.w("RecognitionService", "cancel called by client who did not call startListening - ignoring");
      return;
    }
    onCancel(this.mCurrentCallback);
    this.mCurrentCallback = null;
  }
  
  private void dispatchClearCallback()
  {
    this.mCurrentCallback = null;
  }
  
  private void dispatchStartListening(Intent paramIntent, final IRecognitionListener paramIRecognitionListener, int paramInt)
  {
    if (this.mCurrentCallback == null) {
      try
      {
        paramIRecognitionListener.asBinder().linkToDeath(new IBinder.DeathRecipient()
        {
          public void binderDied()
          {
            RecognitionService.-get0(RecognitionService.this).sendMessage(RecognitionService.-get0(RecognitionService.this).obtainMessage(3, paramIRecognitionListener));
          }
        }, 0);
        this.mCurrentCallback = new Callback(paramIRecognitionListener, paramInt, null);
        onStartListening(paramIntent, this.mCurrentCallback);
        return;
      }
      catch (RemoteException paramIntent)
      {
        Log.e("RecognitionService", "dead listener on startListening");
        return;
      }
    }
    try
    {
      paramIRecognitionListener.onError(8);
      Log.i("RecognitionService", "concurrent startListening received - ignoring this call");
      return;
    }
    catch (RemoteException paramIntent)
    {
      for (;;)
      {
        Log.d("RecognitionService", "onError call from startListening failed");
      }
    }
  }
  
  private void dispatchStopListening(IRecognitionListener paramIRecognitionListener)
  {
    try
    {
      if (this.mCurrentCallback == null)
      {
        paramIRecognitionListener.onError(5);
        Log.w("RecognitionService", "stopListening called with no preceding startListening - ignoring");
        return;
      }
      if (Callback.-get0(this.mCurrentCallback).asBinder() != paramIRecognitionListener.asBinder())
      {
        paramIRecognitionListener.onError(8);
        Log.w("RecognitionService", "stopListening called by other caller than startListening - ignoring");
        return;
      }
    }
    catch (RemoteException paramIRecognitionListener)
    {
      Log.d("RecognitionService", "onError call from stopListening failed");
      return;
    }
    onStopListening(this.mCurrentCallback);
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    return this.mBinder;
  }
  
  protected abstract void onCancel(Callback paramCallback);
  
  public void onDestroy()
  {
    this.mCurrentCallback = null;
    this.mBinder.clearReference();
    super.onDestroy();
  }
  
  protected abstract void onStartListening(Intent paramIntent, Callback paramCallback);
  
  protected abstract void onStopListening(Callback paramCallback);
  
  public class Callback
  {
    private final int mCallingUid;
    private final IRecognitionListener mListener;
    
    private Callback(IRecognitionListener paramIRecognitionListener, int paramInt)
    {
      this.mListener = paramIRecognitionListener;
      this.mCallingUid = paramInt;
    }
    
    public void beginningOfSpeech()
      throws RemoteException
    {
      this.mListener.onBeginningOfSpeech();
    }
    
    public void bufferReceived(byte[] paramArrayOfByte)
      throws RemoteException
    {
      this.mListener.onBufferReceived(paramArrayOfByte);
    }
    
    public void endOfSpeech()
      throws RemoteException
    {
      this.mListener.onEndOfSpeech();
    }
    
    public void error(int paramInt)
      throws RemoteException
    {
      Message.obtain(RecognitionService.-get0(RecognitionService.this), 4).sendToTarget();
      this.mListener.onError(paramInt);
    }
    
    public int getCallingUid()
    {
      return this.mCallingUid;
    }
    
    public void partialResults(Bundle paramBundle)
      throws RemoteException
    {
      this.mListener.onPartialResults(paramBundle);
    }
    
    public void readyForSpeech(Bundle paramBundle)
      throws RemoteException
    {
      this.mListener.onReadyForSpeech(paramBundle);
    }
    
    public void results(Bundle paramBundle)
      throws RemoteException
    {
      Message.obtain(RecognitionService.-get0(RecognitionService.this), 4).sendToTarget();
      this.mListener.onResults(paramBundle);
    }
    
    public void rmsChanged(float paramFloat)
      throws RemoteException
    {
      this.mListener.onRmsChanged(paramFloat);
    }
  }
  
  private static final class RecognitionServiceBinder
    extends IRecognitionService.Stub
  {
    private final WeakReference<RecognitionService> mServiceRef;
    
    public RecognitionServiceBinder(RecognitionService paramRecognitionService)
    {
      this.mServiceRef = new WeakReference(paramRecognitionService);
    }
    
    public void cancel(IRecognitionListener paramIRecognitionListener)
    {
      RecognitionService localRecognitionService = (RecognitionService)this.mServiceRef.get();
      if ((localRecognitionService != null) && (RecognitionService.-wrap0(localRecognitionService, paramIRecognitionListener))) {
        RecognitionService.-get0(localRecognitionService).sendMessage(Message.obtain(RecognitionService.-get0(localRecognitionService), 3, paramIRecognitionListener));
      }
    }
    
    public void clearReference()
    {
      this.mServiceRef.clear();
    }
    
    public void startListening(Intent paramIntent, IRecognitionListener paramIRecognitionListener)
    {
      RecognitionService localRecognitionService = (RecognitionService)this.mServiceRef.get();
      if ((localRecognitionService != null) && (RecognitionService.-wrap0(localRecognitionService, paramIRecognitionListener)))
      {
        Handler localHandler1 = RecognitionService.-get0(localRecognitionService);
        Handler localHandler2 = RecognitionService.-get0(localRecognitionService);
        localRecognitionService.getClass();
        localHandler1.sendMessage(Message.obtain(localHandler2, 1, new RecognitionService.StartListeningArgs(localRecognitionService, paramIntent, paramIRecognitionListener, Binder.getCallingUid())));
      }
    }
    
    public void stopListening(IRecognitionListener paramIRecognitionListener)
    {
      RecognitionService localRecognitionService = (RecognitionService)this.mServiceRef.get();
      if ((localRecognitionService != null) && (RecognitionService.-wrap0(localRecognitionService, paramIRecognitionListener))) {
        RecognitionService.-get0(localRecognitionService).sendMessage(Message.obtain(RecognitionService.-get0(localRecognitionService), 2, paramIRecognitionListener));
      }
    }
  }
  
  private class StartListeningArgs
  {
    public final int mCallingUid;
    public final Intent mIntent;
    public final IRecognitionListener mListener;
    
    public StartListeningArgs(Intent paramIntent, IRecognitionListener paramIRecognitionListener, int paramInt)
    {
      this.mIntent = paramIntent;
      this.mListener = paramIRecognitionListener;
      this.mCallingUid = paramInt;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/RecognitionService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */