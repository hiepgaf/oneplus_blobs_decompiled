package android.telecom;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import com.android.internal.os.SomeArgs;
import com.android.internal.telecom.ICallScreeningAdapter;
import com.android.internal.telecom.ICallScreeningService.Stub;

public abstract class CallScreeningService
  extends Service
{
  private static final int MSG_SCREEN_CALL = 1;
  public static final String SERVICE_INTERFACE = "android.telecom.CallScreeningService";
  private ICallScreeningAdapter mCallScreeningAdapter;
  private final Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
      try
      {
        CallScreeningService.-set0(CallScreeningService.this, (ICallScreeningAdapter)paramAnonymousMessage.arg1);
        CallScreeningService.this.onScreenCall(Call.Details.createFromParcelableCall((ParcelableCall)paramAnonymousMessage.arg2));
        return;
      }
      finally
      {
        paramAnonymousMessage.recycle();
      }
    }
  };
  
  public IBinder onBind(Intent paramIntent)
  {
    Log.v(this, "onBind", new Object[0]);
    return new CallScreeningBinder(null);
  }
  
  public abstract void onScreenCall(Call.Details paramDetails);
  
  public boolean onUnbind(Intent paramIntent)
  {
    Log.v(this, "onUnbind", new Object[0]);
    return false;
  }
  
  public final void respondToCall(Call.Details paramDetails, CallResponse paramCallResponse)
  {
    boolean bool2 = false;
    for (;;)
    {
      try
      {
        if (paramCallResponse.getDisallowCall())
        {
          ICallScreeningAdapter localICallScreeningAdapter = this.mCallScreeningAdapter;
          paramDetails = paramDetails.getTelecomCallId();
          boolean bool3 = paramCallResponse.getRejectCall();
          if (paramCallResponse.getSkipCallLog())
          {
            bool1 = false;
            if (!paramCallResponse.getSkipNotification()) {
              break label78;
            }
            localICallScreeningAdapter.disallowCall(paramDetails, bool3, bool1, bool2);
          }
        }
        else
        {
          this.mCallScreeningAdapter.allowCall(paramDetails.getTelecomCallId());
          return;
        }
      }
      catch (RemoteException paramDetails)
      {
        return;
      }
      boolean bool1 = true;
      continue;
      label78:
      bool2 = true;
    }
  }
  
  public static class CallResponse
  {
    private final boolean mShouldDisallowCall;
    private final boolean mShouldRejectCall;
    private final boolean mShouldSkipCallLog;
    private final boolean mShouldSkipNotification;
    
    private CallResponse(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
    {
      if ((!paramBoolean1) && ((paramBoolean2) || (paramBoolean3) || (paramBoolean4))) {
        throw new IllegalStateException("Invalid response state for allowed call.");
      }
      this.mShouldDisallowCall = paramBoolean1;
      this.mShouldRejectCall = paramBoolean2;
      this.mShouldSkipCallLog = paramBoolean3;
      this.mShouldSkipNotification = paramBoolean4;
    }
    
    public boolean getDisallowCall()
    {
      return this.mShouldDisallowCall;
    }
    
    public boolean getRejectCall()
    {
      return this.mShouldRejectCall;
    }
    
    public boolean getSkipCallLog()
    {
      return this.mShouldSkipCallLog;
    }
    
    public boolean getSkipNotification()
    {
      return this.mShouldSkipNotification;
    }
    
    public static class Builder
    {
      private boolean mShouldDisallowCall;
      private boolean mShouldRejectCall;
      private boolean mShouldSkipCallLog;
      private boolean mShouldSkipNotification;
      
      public CallScreeningService.CallResponse build()
      {
        return new CallScreeningService.CallResponse(this.mShouldDisallowCall, this.mShouldRejectCall, this.mShouldSkipCallLog, this.mShouldSkipNotification, null);
      }
      
      public Builder setDisallowCall(boolean paramBoolean)
      {
        this.mShouldDisallowCall = paramBoolean;
        return this;
      }
      
      public Builder setRejectCall(boolean paramBoolean)
      {
        this.mShouldRejectCall = paramBoolean;
        return this;
      }
      
      public Builder setSkipCallLog(boolean paramBoolean)
      {
        this.mShouldSkipCallLog = paramBoolean;
        return this;
      }
      
      public Builder setSkipNotification(boolean paramBoolean)
      {
        this.mShouldSkipNotification = paramBoolean;
        return this;
      }
    }
  }
  
  private final class CallScreeningBinder
    extends ICallScreeningService.Stub
  {
    private CallScreeningBinder() {}
    
    public void screenCall(ICallScreeningAdapter paramICallScreeningAdapter, ParcelableCall paramParcelableCall)
    {
      Log.v(this, "screenCall", new Object[0]);
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramICallScreeningAdapter;
      localSomeArgs.arg2 = paramParcelableCall;
      CallScreeningService.-get0(CallScreeningService.this).obtainMessage(1, localSomeArgs).sendToTarget();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/CallScreeningService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */