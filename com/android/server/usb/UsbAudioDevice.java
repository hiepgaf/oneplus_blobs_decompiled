package com.android.server.usb;

public final class UsbAudioDevice
{
  protected static final boolean DEBUG = false;
  private static final String TAG = "UsbAudioDevice";
  public static final int kAudioDeviceClassMask = 16777215;
  public static final int kAudioDeviceClass_External = 2;
  public static final int kAudioDeviceClass_Internal = 1;
  public static final int kAudioDeviceClass_Undefined = 0;
  public static final int kAudioDeviceMetaMask = -16777216;
  public static final int kAudioDeviceMeta_Alsa = Integer.MIN_VALUE;
  public int mCard;
  public int mDevice;
  public int mDeviceClass;
  public String mDeviceDescription = "";
  public String mDeviceName = "";
  public boolean mHasCapture;
  public boolean mHasPlayback;
  
  public UsbAudioDevice(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3)
  {
    this.mCard = paramInt1;
    this.mDevice = paramInt2;
    this.mHasPlayback = paramBoolean1;
    this.mHasCapture = paramBoolean2;
    this.mDeviceClass = paramInt3;
  }
  
  public String toShortString()
  {
    return "[card:" + this.mCard + " device:" + this.mDevice + " " + this.mDeviceName + "]";
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UsbAudioDevice: [card: ").append(this.mCard);
    localStringBuilder.append(", device: ").append(this.mDevice);
    localStringBuilder.append(", name: ").append(this.mDeviceName);
    localStringBuilder.append(", hasPlayback: ").append(this.mHasPlayback);
    localStringBuilder.append(", hasCapture: ").append(this.mHasCapture);
    localStringBuilder.append(", class: 0x").append(Integer.toHexString(this.mDeviceClass)).append("]");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usb/UsbAudioDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */