package android.service.voice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.soundtrigger.KeyphraseEnrollmentInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings.Secure;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.internal.app.IVoiceInteractionManagerService.Stub;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Locale;

public class VoiceInteractionService
  extends Service
{
  static final int MSG_LAUNCH_VOICE_ASSIST_FROM_KEYGUARD = 4;
  static final int MSG_READY = 1;
  static final int MSG_SHUTDOWN = 2;
  static final int MSG_SOUND_MODELS_CHANGED = 3;
  public static final String SERVICE_INTERFACE = "android.service.voice.VoiceInteractionService";
  public static final String SERVICE_META_DATA = "android.voice_interaction";
  MyHandler mHandler;
  private AlwaysOnHotwordDetector mHotwordDetector;
  IVoiceInteractionService mInterface = new IVoiceInteractionService.Stub()
  {
    public void launchVoiceAssistFromKeyguard()
      throws RemoteException
    {
      VoiceInteractionService.this.mHandler.sendEmptyMessage(4);
    }
    
    public void ready()
    {
      VoiceInteractionService.this.mHandler.sendEmptyMessage(1);
    }
    
    public void shutdown()
    {
      VoiceInteractionService.this.mHandler.sendEmptyMessage(2);
    }
    
    public void soundModelsChanged()
    {
      VoiceInteractionService.this.mHandler.sendEmptyMessage(3);
    }
  };
  private KeyphraseEnrollmentInfo mKeyphraseEnrollmentInfo;
  private final Object mLock = new Object();
  IVoiceInteractionManagerService mSystemService;
  
  public static boolean isActiveService(Context paramContext, ComponentName paramComponentName)
  {
    paramContext = Settings.Secure.getString(paramContext.getContentResolver(), "voice_interaction_service");
    if ((paramContext == null) || (paramContext.isEmpty())) {
      return false;
    }
    paramContext = ComponentName.unflattenFromString(paramContext);
    if (paramContext == null) {
      return false;
    }
    return paramContext.equals(paramComponentName);
  }
  
  private void onShutdownInternal()
  {
    onShutdown();
    safelyShutdownHotwordDetector();
  }
  
  private void onSoundModelsChangedInternal()
  {
    try
    {
      if (this.mHotwordDetector != null) {
        this.mHotwordDetector.onSoundModelsChanged();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void safelyShutdownHotwordDetector()
  {
    try
    {
      synchronized (this.mLock)
      {
        if (this.mHotwordDetector != null)
        {
          this.mHotwordDetector.stopRecognition();
          this.mHotwordDetector.invalidate();
          this.mHotwordDetector = null;
        }
        return;
      }
      return;
    }
    catch (Exception localException) {}
  }
  
  public final AlwaysOnHotwordDetector createAlwaysOnHotwordDetector(String paramString, Locale paramLocale, AlwaysOnHotwordDetector.Callback paramCallback)
  {
    if (this.mSystemService == null) {
      throw new IllegalStateException("Not available until onReady() is called");
    }
    synchronized (this.mLock)
    {
      safelyShutdownHotwordDetector();
      this.mHotwordDetector = new AlwaysOnHotwordDetector(paramString, paramLocale, paramCallback, this.mKeyphraseEnrollmentInfo, this.mInterface, this.mSystemService);
      return this.mHotwordDetector;
    }
  }
  
  protected void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("VOICE INTERACTION");
    synchronized (this.mLock)
    {
      paramPrintWriter.println("  AlwaysOnHotwordDetector");
      if (this.mHotwordDetector == null)
      {
        paramPrintWriter.println("    NULL");
        return;
      }
      this.mHotwordDetector.dump("    ", paramPrintWriter);
    }
  }
  
  public int getDisabledShowContext()
  {
    try
    {
      int i = this.mSystemService.getDisabledShowContext();
      return i;
    }
    catch (RemoteException localRemoteException) {}
    return 0;
  }
  
  protected final KeyphraseEnrollmentInfo getKeyphraseEnrollmentInfo()
  {
    return this.mKeyphraseEnrollmentInfo;
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    if ("android.service.voice.VoiceInteractionService".equals(paramIntent.getAction())) {
      return this.mInterface.asBinder();
    }
    return null;
  }
  
  public void onCreate()
  {
    super.onCreate();
    this.mHandler = new MyHandler();
  }
  
  public void onLaunchVoiceAssistFromKeyguard() {}
  
  public void onReady()
  {
    this.mSystemService = IVoiceInteractionManagerService.Stub.asInterface(ServiceManager.getService("voiceinteraction"));
    this.mKeyphraseEnrollmentInfo = new KeyphraseEnrollmentInfo(getPackageManager());
  }
  
  public void onShutdown() {}
  
  public void setDisabledShowContext(int paramInt)
  {
    try
    {
      this.mSystemService.setDisabledShowContext(paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void showSession(Bundle paramBundle, int paramInt)
  {
    if (this.mSystemService == null) {
      throw new IllegalStateException("Not available until onReady() is called");
    }
    try
    {
      this.mSystemService.showSession(this.mInterface, paramBundle, paramInt);
      return;
    }
    catch (RemoteException paramBundle) {}
  }
  
  class MyHandler
    extends Handler
  {
    MyHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        super.handleMessage(paramMessage);
        return;
      case 1: 
        VoiceInteractionService.this.onReady();
        return;
      case 2: 
        VoiceInteractionService.-wrap0(VoiceInteractionService.this);
        return;
      case 3: 
        VoiceInteractionService.-wrap1(VoiceInteractionService.this);
        return;
      }
      VoiceInteractionService.this.onLaunchVoiceAssistFromKeyguard();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/voice/VoiceInteractionService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */