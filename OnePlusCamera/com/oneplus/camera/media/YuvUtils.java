package com.oneplus.camera.media;

import android.media.Image;
import android.media.Image.Plane;

public final class YuvUtils
{
  static
  {
    System.loadLibrary("opcameralib");
  }
  
  public static void multiPlaneYuvToNV21(Image paramImage, byte[] paramArrayOfByte)
  {
    if (paramImage.getFormat() != 35) {
      throw new IllegalArgumentException("Invalid image format.");
    }
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("No result buffer.");
    }
    multiPlaneYuvToNV21Direct(paramImage.getPlanes(), paramArrayOfByte, paramImage.getWidth(), paramImage.getHeight());
  }
  
  public static native void multiPlaneYuvToNV21(ImagePlane[] paramArrayOfImagePlane, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static native void multiPlaneYuvToNV21Direct(Image.Plane[] paramArrayOfPlane, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/YuvUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */