package com.oneplus.gallery2.media;

import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import java.util.Locale;

public class PtpCameraRollMediaSet
  extends MtpMediaSet
{
  private final String m_Id;
  
  PtpCameraRollMediaSet(MtpMediaSource paramMtpMediaSource, UsbDevice paramUsbDevice, MediaType paramMediaType)
  {
    super(paramMtpMediaSource, MediaSet.Type.SYSTEM, paramUsbDevice, paramMediaType);
    this.m_Id = ("PTP-CameraRoll/" + paramUsbDevice.getDeviceId());
    updateName();
  }
  
  private void updateName()
  {
    Object localObject = BaseApplication.current().getResources();
    int i = ((Resources)localObject).getIdentifier("media_set_name_camera_roll", "string", "com.oneplus.gallery");
    StringBuilder localStringBuilder = new StringBuilder();
    if (i <= 0) {}
    for (localObject = "Camera";; localObject = ((Resources)localObject).getString(i))
    {
      localStringBuilder.append((String)localObject);
      localStringBuilder.append(" (");
      localStringBuilder.append(getDevice().getProductName());
      localStringBuilder.append(")");
      setReadOnly(PROP_NAME, localStringBuilder.toString());
      return;
    }
  }
  
  public Handle deleteMedia(Media paramMedia, Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    return null;
  }
  
  public String getId()
  {
    return this.m_Id;
  }
  
  protected void onLocaleChanged(Locale paramLocale1, Locale paramLocale2)
  {
    super.onLocaleChanged(paramLocale1, paramLocale2);
    updateName();
  }
  
  protected void startDeletion(Handle paramHandle, int paramInt) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/PtpCameraRollMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */