package com.oneplus.camera.media;

import android.media.CamcorderProfile;
import android.util.Size;

public final class FileSizeEstimator
{
  private static final double[][] JPEG_SIZE_COEFF = { null, null, null, null, null, null, null, null, { -79919.0D, 199467.0D, -21429.0D, 913.05D }, { -158108.0D, 339150.0D, -39067.0D, 2058.6D }, { -266210.0D, 698098.0D, -71898.0D, 3763.4D } };
  
  public static long estimateJpegFileSize(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt3 < 80) || (paramInt3 > 100)) {
      throw new RuntimeException("Invalid JPEG quality : " + paramInt3 + ".");
    }
    int i = paramInt3 / 10 * 10;
    double[] arrayOfDouble1 = JPEG_SIZE_COEFF[(i / 10)];
    if ((i == paramInt3) && (arrayOfDouble1 != null)) {
      return estimateJpegFileSize(paramInt1, paramInt2, arrayOfDouble1);
    }
    int k = i + 10;
    double[] arrayOfDouble3 = JPEG_SIZE_COEFF[(k / 10)];
    int j;
    double[] arrayOfDouble2;
    for (;;)
    {
      j = k;
      arrayOfDouble2 = arrayOfDouble3;
      if (arrayOfDouble1 != null) {
        break;
      }
      i -= 10;
      arrayOfDouble1 = JPEG_SIZE_COEFF[(i / 10)];
    }
    while (arrayOfDouble2 == null)
    {
      j += 10;
      arrayOfDouble2 = JPEG_SIZE_COEFF[(j / 10)];
    }
    double d = estimateJpegFileSize(paramInt1, paramInt2, arrayOfDouble1);
    return ((estimateJpegFileSize(paramInt1, paramInt2, arrayOfDouble2) - d) * (paramInt3 - i) / (j - i) + d);
  }
  
  public static long estimateJpegFileSize(int paramInt1, int paramInt2, double[] paramArrayOfDouble)
  {
    double d3 = paramInt1 * paramInt2 / 1000000.0D;
    double d1 = 0.0D;
    paramInt1 = paramArrayOfDouble.length - 1;
    while (paramInt1 >= 0)
    {
      double d2 = paramArrayOfDouble[paramInt1];
      paramInt2 = paramInt1;
      while (paramInt2 > 0)
      {
        d2 *= d3;
        paramInt2 -= 1;
      }
      d1 += d2;
      paramInt1 -= 1;
    }
    return d1;
  }
  
  public static long estimateJpegFileSize(Size paramSize, int paramInt)
  {
    return estimateJpegFileSize(paramSize.getWidth(), paramSize.getHeight(), paramInt);
  }
  
  public static long estimateJpegFileSize(Resolution paramResolution, int paramInt)
  {
    return estimateJpegFileSize(paramResolution.getWidth(), paramResolution.getHeight(), paramInt);
  }
  
  public static final long estimateVideoFileSize(CamcorderProfile paramCamcorderProfile, long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Invalid duration : " + paramLong + ".");
    }
    if (paramLong > 0L) {
      return (paramCamcorderProfile.audioBitRate + paramCamcorderProfile.videoBitRate) / 8 * paramLong;
    }
    return 0L;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/FileSizeEstimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */