package android.media.midi;

import android.app.Service;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public abstract class MidiDeviceService
  extends Service
{
  public static final String SERVICE_INTERFACE = "android.media.midi.MidiDeviceService";
  private static final String TAG = "MidiDeviceService";
  private final MidiDeviceServer.Callback mCallback = new MidiDeviceServer.Callback()
  {
    public void onClose()
    {
      MidiDeviceService.this.onClose();
    }
    
    public void onDeviceStatusChanged(MidiDeviceServer paramAnonymousMidiDeviceServer, MidiDeviceStatus paramAnonymousMidiDeviceStatus)
    {
      MidiDeviceService.this.onDeviceStatusChanged(paramAnonymousMidiDeviceStatus);
    }
  };
  private MidiDeviceInfo mDeviceInfo;
  private IMidiManager mMidiManager;
  private MidiDeviceServer mServer;
  
  public final MidiDeviceInfo getDeviceInfo()
  {
    return this.mDeviceInfo;
  }
  
  public final MidiReceiver[] getOutputPortReceivers()
  {
    if (this.mServer == null) {
      return null;
    }
    return this.mServer.getOutputPortReceivers();
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    if (("android.media.midi.MidiDeviceService".equals(paramIntent.getAction())) && (this.mServer != null)) {
      return this.mServer.getBinderInterface().asBinder();
    }
    return null;
  }
  
  public void onClose() {}
  
  public void onCreate()
  {
    this.mMidiManager = IMidiManager.Stub.asInterface(ServiceManager.getService("midi"));
    try
    {
      MidiDeviceInfo localMidiDeviceInfo = this.mMidiManager.getServiceDeviceInfo(getPackageName(), getClass().getName());
      if (localMidiDeviceInfo == null)
      {
        Log.e("MidiDeviceService", "Could not find MidiDeviceInfo for MidiDeviceService " + this);
        return;
      }
      this.mDeviceInfo = localMidiDeviceInfo;
      MidiReceiver[] arrayOfMidiReceiver = onGetInputPortReceivers();
      localObject1 = arrayOfMidiReceiver;
      if (arrayOfMidiReceiver == null) {
        localObject1 = new MidiReceiver[0];
      }
      localObject1 = new MidiDeviceServer(this.mMidiManager, (MidiReceiver[])localObject1, localMidiDeviceInfo, this.mCallback);
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Object localObject1;
        Log.e("MidiDeviceService", "RemoteException in IMidiManager.getServiceDeviceInfo");
        Object localObject2 = null;
      }
    }
    this.mServer = ((MidiDeviceServer)localObject1);
  }
  
  public void onDeviceStatusChanged(MidiDeviceStatus paramMidiDeviceStatus) {}
  
  public abstract MidiReceiver[] onGetInputPortReceivers();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/MidiDeviceService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */