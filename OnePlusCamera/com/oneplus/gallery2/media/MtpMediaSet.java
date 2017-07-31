package com.oneplus.gallery2.media;

import android.hardware.usb.UsbDevice;

public abstract class MtpMediaSet
  extends BaseMediaSet
{
  private final UsbDevice m_Device;
  private final int m_DeviceId;
  private final MediaSet.Type m_Type;
  
  protected MtpMediaSet(MtpMediaSource paramMtpMediaSource, MediaSet.Type paramType, UsbDevice paramUsbDevice, MediaType paramMediaType)
  {
    super(paramMtpMediaSource, paramMediaType);
    this.m_Type = paramType;
    this.m_Device = paramUsbDevice;
    this.m_DeviceId = paramUsbDevice.getDeviceId();
  }
  
  public final UsbDevice getDevice()
  {
    return this.m_Device;
  }
  
  public final int getDeviceId()
  {
    return this.m_DeviceId;
  }
  
  public final MediaSet.Type getType()
  {
    return this.m_Type;
  }
  
  public boolean isVirtual()
  {
    return false;
  }
  
  protected boolean shouldContainsMedia(Media paramMedia, int paramInt)
  {
    if (!(paramMedia instanceof MtpMedia)) {
      return false;
    }
    return ((MtpMedia)paramMedia).getDeviceId() == this.m_DeviceId;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MtpMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */