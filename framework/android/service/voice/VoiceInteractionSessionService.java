package android.service.voice;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.internal.app.IVoiceInteractionManagerService.Stub;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.HandlerCaller.Callback;
import com.android.internal.os.SomeArgs;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class VoiceInteractionSessionService
  extends Service
{
  static final int MSG_NEW_SESSION = 1;
  HandlerCaller mHandlerCaller;
  final HandlerCaller.Callback mHandlerCallerCallback = new HandlerCaller.Callback()
  {
    public void executeMessage(Message paramAnonymousMessage)
    {
      SomeArgs localSomeArgs = (SomeArgs)paramAnonymousMessage.obj;
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      VoiceInteractionSessionService.this.doNewSession((IBinder)localSomeArgs.arg1, (Bundle)localSomeArgs.arg2, localSomeArgs.argi1);
    }
  };
  IVoiceInteractionSessionService mInterface = new IVoiceInteractionSessionService.Stub()
  {
    public void newSession(IBinder paramAnonymousIBinder, Bundle paramAnonymousBundle, int paramAnonymousInt)
    {
      VoiceInteractionSessionService.this.mHandlerCaller.sendMessage(VoiceInteractionSessionService.this.mHandlerCaller.obtainMessageIOO(1, paramAnonymousInt, paramAnonymousIBinder, paramAnonymousBundle));
    }
  };
  VoiceInteractionSession mSession;
  IVoiceInteractionManagerService mSystemService;
  
  void doNewSession(IBinder paramIBinder, Bundle paramBundle, int paramInt)
  {
    if (this.mSession != null)
    {
      this.mSession.doDestroy();
      this.mSession = null;
    }
    this.mSession = onNewSession(paramBundle);
    try
    {
      this.mSystemService.deliverNewSession(paramIBinder, this.mSession.mSession, this.mSession.mInteractor);
      this.mSession.doCreate(this.mSystemService, paramIBinder);
      return;
    }
    catch (RemoteException paramIBinder) {}
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mSession == null)
    {
      paramPrintWriter.println("(no active session)");
      return;
    }
    paramPrintWriter.println("VoiceInteractionSession:");
    this.mSession.dump("  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return this.mInterface.asBinder();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.mSession != null) {
      this.mSession.onConfigurationChanged(paramConfiguration);
    }
  }
  
  public void onCreate()
  {
    super.onCreate();
    this.mSystemService = IVoiceInteractionManagerService.Stub.asInterface(ServiceManager.getService("voiceinteraction"));
    this.mHandlerCaller = new HandlerCaller(this, Looper.myLooper(), this.mHandlerCallerCallback, true);
  }
  
  public void onLowMemory()
  {
    super.onLowMemory();
    if (this.mSession != null) {
      this.mSession.onLowMemory();
    }
  }
  
  public abstract VoiceInteractionSession onNewSession(Bundle paramBundle);
  
  public void onTrimMemory(int paramInt)
  {
    super.onTrimMemory(paramInt);
    if (this.mSession != null) {
      this.mSession.onTrimMemory(paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/voice/VoiceInteractionSessionService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */