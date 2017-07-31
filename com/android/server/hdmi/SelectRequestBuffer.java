package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import android.os.RemoteException;
import android.util.Slog;

public class SelectRequestBuffer
{
  public static final SelectRequestBuffer EMPTY_BUFFER = new SelectRequestBuffer()
  {
    public void process() {}
  };
  private static final String TAG = "SelectRequestBuffer";
  private SelectRequest mRequest;
  
  public static DeviceSelectRequest newDeviceSelect(HdmiControlService paramHdmiControlService, int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
  {
    return new DeviceSelectRequest(paramHdmiControlService, paramInt, paramIHdmiControlCallback, null);
  }
  
  public static PortSelectRequest newPortSelect(HdmiControlService paramHdmiControlService, int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
  {
    return new PortSelectRequest(paramHdmiControlService, paramInt, paramIHdmiControlCallback, null);
  }
  
  public void clear()
  {
    this.mRequest = null;
  }
  
  public void process()
  {
    if (this.mRequest != null)
    {
      this.mRequest.process();
      clear();
    }
  }
  
  public void set(SelectRequest paramSelectRequest)
  {
    this.mRequest = paramSelectRequest;
  }
  
  public static class DeviceSelectRequest
    extends SelectRequestBuffer.SelectRequest
  {
    private DeviceSelectRequest(HdmiControlService paramHdmiControlService, int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
    {
      super(paramInt, paramIHdmiControlCallback);
    }
    
    public void process()
    {
      if (isLocalDeviceReady())
      {
        Slog.v("SelectRequestBuffer", "calling delayed deviceSelect id:" + this.mId);
        tv().deviceSelect(this.mId, this.mCallback);
      }
    }
  }
  
  public static class PortSelectRequest
    extends SelectRequestBuffer.SelectRequest
  {
    private PortSelectRequest(HdmiControlService paramHdmiControlService, int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
    {
      super(paramInt, paramIHdmiControlCallback);
    }
    
    public void process()
    {
      if (isLocalDeviceReady())
      {
        Slog.v("SelectRequestBuffer", "calling delayed portSelect id:" + this.mId);
        tv().doManualPortSwitching(this.mId, this.mCallback);
      }
    }
  }
  
  public static abstract class SelectRequest
  {
    protected final IHdmiControlCallback mCallback;
    protected final int mId;
    protected final HdmiControlService mService;
    
    public SelectRequest(HdmiControlService paramHdmiControlService, int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
    {
      this.mService = paramHdmiControlService;
      this.mId = paramInt;
      this.mCallback = paramIHdmiControlCallback;
    }
    
    private void invokeCallback(int paramInt)
    {
      try
      {
        if (this.mCallback != null) {
          this.mCallback.onComplete(paramInt);
        }
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("SelectRequestBuffer", "Invoking callback failed:" + localRemoteException);
      }
    }
    
    protected boolean isLocalDeviceReady()
    {
      if (tv() == null)
      {
        Slog.e("SelectRequestBuffer", "Local tv device not available");
        invokeCallback(2);
        return false;
      }
      return true;
    }
    
    public abstract void process();
    
    protected HdmiCecLocalDeviceTv tv()
    {
      return this.mService.tv();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/SelectRequestBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */