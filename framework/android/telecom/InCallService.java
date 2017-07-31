package android.telecom;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import com.android.internal.os.SomeArgs;
import com.android.internal.telecom.IInCallAdapter;
import com.android.internal.telecom.IInCallService.Stub;
import java.util.Collections;
import java.util.List;

public abstract class InCallService
  extends Service
{
  private static final int MSG_ADD_CALL = 2;
  private static final int MSG_BRING_TO_FOREGROUND = 6;
  private static final int MSG_ON_CALL_AUDIO_STATE_CHANGED = 5;
  private static final int MSG_ON_CAN_ADD_CALL_CHANGED = 7;
  private static final int MSG_ON_CONNECTION_EVENT = 9;
  private static final int MSG_SET_IN_CALL_ADAPTER = 1;
  private static final int MSG_SET_POST_DIAL_WAIT = 4;
  private static final int MSG_SILENCE_RINGER = 8;
  private static final int MSG_UPDATE_CALL = 3;
  public static final String SERVICE_INTERFACE = "android.telecom.InCallService";
  private final Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      boolean bool2 = true;
      boolean bool1 = true;
      if ((InCallService.-get1(InCallService.this) == null) && (paramAnonymousMessage.what != 1)) {
        return;
      }
      String str2;
      Object localObject2;
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        InCallService.-set0(InCallService.this, new Phone(new InCallAdapter((IInCallAdapter)paramAnonymousMessage.obj)));
        InCallService.-get1(InCallService.this).addListener(InCallService.-get2(InCallService.this));
        InCallService.this.onPhoneCreated(InCallService.-get1(InCallService.this));
        return;
      case 2: 
        InCallService.-get1(InCallService.this).internalAddCall((ParcelableCall)paramAnonymousMessage.obj);
        return;
      case 3: 
        InCallService.-get1(InCallService.this).internalUpdateCall((ParcelableCall)paramAnonymousMessage.obj);
        return;
      case 4: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          String str1 = (String)paramAnonymousMessage.arg1;
          str2 = (String)paramAnonymousMessage.arg2;
          InCallService.-get1(InCallService.this).internalSetPostDialWait(str1, str2);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 5: 
        InCallService.-get1(InCallService.this).internalCallAudioStateChanged((CallAudioState)paramAnonymousMessage.obj);
        return;
      case 6: 
        localObject2 = InCallService.-get1(InCallService.this);
        if (paramAnonymousMessage.arg1 == 1) {}
        for (;;)
        {
          ((Phone)localObject2).internalBringToForeground(bool1);
          return;
          bool1 = false;
        }
      case 7: 
        localObject2 = InCallService.-get1(InCallService.this);
        if (paramAnonymousMessage.arg1 == 1) {}
        for (bool1 = bool2;; bool1 = false)
        {
          ((Phone)localObject2).internalSetCanAddCall(bool1);
          return;
        }
      case 8: 
        InCallService.-get1(InCallService.this).internalSilenceRinger();
        return;
      }
      paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
      try
      {
        localObject2 = (String)paramAnonymousMessage.arg1;
        str2 = (String)paramAnonymousMessage.arg2;
        Bundle localBundle = (Bundle)paramAnonymousMessage.arg3;
        InCallService.-get1(InCallService.this).internalOnConnectionEvent((String)localObject2, str2, localBundle);
        return;
      }
      finally
      {
        paramAnonymousMessage.recycle();
      }
    }
  };
  private Phone mPhone;
  private Phone.Listener mPhoneListener = new Phone.Listener()
  {
    public void onAudioStateChanged(Phone paramAnonymousPhone, AudioState paramAnonymousAudioState)
    {
      InCallService.this.onAudioStateChanged(paramAnonymousAudioState);
    }
    
    public void onBringToForeground(Phone paramAnonymousPhone, boolean paramAnonymousBoolean)
    {
      InCallService.this.onBringToForeground(paramAnonymousBoolean);
    }
    
    public void onCallAdded(Phone paramAnonymousPhone, Call paramAnonymousCall)
    {
      InCallService.this.onCallAdded(paramAnonymousCall);
    }
    
    public void onCallAudioStateChanged(Phone paramAnonymousPhone, CallAudioState paramAnonymousCallAudioState)
    {
      InCallService.this.onCallAudioStateChanged(paramAnonymousCallAudioState);
    }
    
    public void onCallRemoved(Phone paramAnonymousPhone, Call paramAnonymousCall)
    {
      InCallService.this.onCallRemoved(paramAnonymousCall);
    }
    
    public void onCanAddCallChanged(Phone paramAnonymousPhone, boolean paramAnonymousBoolean)
    {
      InCallService.this.onCanAddCallChanged(paramAnonymousBoolean);
    }
    
    public void onSilenceRinger(Phone paramAnonymousPhone)
    {
      InCallService.this.onSilenceRinger();
    }
  };
  
  public final boolean canAddCall()
  {
    if (this.mPhone == null) {
      return false;
    }
    return this.mPhone.canAddCall();
  }
  
  @Deprecated
  public final AudioState getAudioState()
  {
    if (this.mPhone == null) {
      return null;
    }
    return this.mPhone.getAudioState();
  }
  
  public final CallAudioState getCallAudioState()
  {
    if (this.mPhone == null) {
      return null;
    }
    return this.mPhone.getCallAudioState();
  }
  
  public final List<Call> getCalls()
  {
    if (this.mPhone == null) {
      return Collections.emptyList();
    }
    return this.mPhone.getCalls();
  }
  
  @Deprecated
  public Phone getPhone()
  {
    return this.mPhone;
  }
  
  @Deprecated
  public void onAudioStateChanged(AudioState paramAudioState) {}
  
  public IBinder onBind(Intent paramIntent)
  {
    return new InCallServiceBinder(null);
  }
  
  public void onBringToForeground(boolean paramBoolean) {}
  
  public void onCallAdded(Call paramCall) {}
  
  public void onCallAudioStateChanged(CallAudioState paramCallAudioState) {}
  
  public void onCallRemoved(Call paramCall) {}
  
  public void onCanAddCallChanged(boolean paramBoolean) {}
  
  public void onConnectionEvent(Call paramCall, String paramString, Bundle paramBundle) {}
  
  @Deprecated
  public void onPhoneCreated(Phone paramPhone) {}
  
  @Deprecated
  public void onPhoneDestroyed(Phone paramPhone) {}
  
  public void onSilenceRinger() {}
  
  public boolean onUnbind(Intent paramIntent)
  {
    if (this.mPhone != null)
    {
      paramIntent = this.mPhone;
      this.mPhone = null;
      paramIntent.destroy();
      paramIntent.removeListener(this.mPhoneListener);
      onPhoneDestroyed(paramIntent);
    }
    return false;
  }
  
  public final void setAudioRoute(int paramInt)
  {
    if (this.mPhone != null) {
      this.mPhone.setAudioRoute(paramInt);
    }
  }
  
  public final void setMuted(boolean paramBoolean)
  {
    if (this.mPhone != null) {
      this.mPhone.setMuted(paramBoolean);
    }
  }
  
  private final class InCallServiceBinder
    extends IInCallService.Stub
  {
    private InCallServiceBinder() {}
    
    public void addCall(ParcelableCall paramParcelableCall)
    {
      InCallService.-get0(InCallService.this).obtainMessage(2, paramParcelableCall).sendToTarget();
    }
    
    public void bringToForeground(boolean paramBoolean)
    {
      Handler localHandler = InCallService.-get0(InCallService.this);
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler.obtainMessage(6, i, 0).sendToTarget();
        return;
      }
    }
    
    public void onCallAudioStateChanged(CallAudioState paramCallAudioState)
    {
      InCallService.-get0(InCallService.this).obtainMessage(5, paramCallAudioState).sendToTarget();
    }
    
    public void onCanAddCallChanged(boolean paramBoolean)
    {
      Handler localHandler = InCallService.-get0(InCallService.this);
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler.obtainMessage(7, i, 0).sendToTarget();
        return;
      }
    }
    
    public void onConnectionEvent(String paramString1, String paramString2, Bundle paramBundle)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramString1;
      localSomeArgs.arg2 = paramString2;
      localSomeArgs.arg3 = paramBundle;
      InCallService.-get0(InCallService.this).obtainMessage(9, localSomeArgs).sendToTarget();
    }
    
    public void setInCallAdapter(IInCallAdapter paramIInCallAdapter)
    {
      InCallService.-get0(InCallService.this).obtainMessage(1, paramIInCallAdapter).sendToTarget();
    }
    
    public void setPostDial(String paramString1, String paramString2) {}
    
    public void setPostDialWait(String paramString1, String paramString2)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramString1;
      localSomeArgs.arg2 = paramString2;
      InCallService.-get0(InCallService.this).obtainMessage(4, localSomeArgs).sendToTarget();
    }
    
    public void silenceRinger()
    {
      InCallService.-get0(InCallService.this).obtainMessage(8).sendToTarget();
    }
    
    public void updateCall(ParcelableCall paramParcelableCall)
    {
      InCallService.-get0(InCallService.this).obtainMessage(3, paramParcelableCall).sendToTarget();
    }
  }
  
  public static abstract class VideoCall
  {
    public abstract void destroy();
    
    public abstract void registerCallback(Callback paramCallback);
    
    public abstract void registerCallback(Callback paramCallback, Handler paramHandler);
    
    public abstract void requestCallDataUsage();
    
    public abstract void requestCameraCapabilities();
    
    public abstract void sendSessionModifyRequest(VideoProfile paramVideoProfile);
    
    public abstract void sendSessionModifyResponse(VideoProfile paramVideoProfile);
    
    public abstract void setCamera(String paramString);
    
    public abstract void setDeviceOrientation(int paramInt);
    
    public abstract void setDisplaySurface(Surface paramSurface);
    
    public abstract void setPauseImage(Uri paramUri);
    
    public abstract void setPreviewSurface(Surface paramSurface);
    
    public abstract void setZoom(float paramFloat);
    
    public abstract void unregisterCallback(Callback paramCallback);
    
    public static abstract class Callback
    {
      public abstract void onCallDataUsageChanged(long paramLong);
      
      public abstract void onCallSessionEvent(int paramInt);
      
      public abstract void onCameraCapabilitiesChanged(VideoProfile.CameraCapabilities paramCameraCapabilities);
      
      public abstract void onPeerDimensionsChanged(int paramInt1, int paramInt2);
      
      public abstract void onSessionModifyRequestReceived(VideoProfile paramVideoProfile);
      
      public abstract void onSessionModifyResponseReceived(int paramInt, VideoProfile paramVideoProfile1, VideoProfile paramVideoProfile2);
      
      public abstract void onVideoQualityChanged(int paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/InCallService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */