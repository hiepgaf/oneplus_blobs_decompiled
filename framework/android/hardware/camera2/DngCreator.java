package android.hardware.camera2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.location.Location;
import android.media.Image;
import android.media.Image.Plane;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public final class DngCreator
  implements AutoCloseable
{
  private static final int BYTES_PER_RGB_PIX = 3;
  private static final int DEFAULT_PIXEL_STRIDE = 2;
  private static final String GPS_DATE_FORMAT_STR = "yyyy:MM:dd";
  private static final String GPS_LAT_REF_NORTH = "N";
  private static final String GPS_LAT_REF_SOUTH = "S";
  private static final String GPS_LONG_REF_EAST = "E";
  private static final String GPS_LONG_REF_WEST = "W";
  public static final int MAX_THUMBNAIL_DIMENSION = 256;
  private static final String TAG = "DngCreator";
  private static final int TAG_ORIENTATION_UNKNOWN = 9;
  private static final String TIFF_DATETIME_FORMAT = "yyyy:MM:dd HH:mm:ss";
  private static final DateFormat sExifGPSDateStamp = new SimpleDateFormat("yyyy:MM:dd");
  private final Calendar mGPSTimeStampCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
  private long mNativeContext;
  
  static
  {
    sExifGPSDateStamp.setTimeZone(TimeZone.getTimeZone("UTC"));
    nativeClassInit();
  }
  
  public DngCreator(CameraCharacteristics paramCameraCharacteristics, CaptureResult paramCaptureResult)
  {
    if ((paramCameraCharacteristics == null) || (paramCaptureResult == null)) {
      throw new IllegalArgumentException("Null argument to DngCreator constructor");
    }
    long l2 = System.currentTimeMillis();
    int i = ((Integer)paramCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE)).intValue();
    long l1;
    if (i == 1) {
      l1 = l2 - SystemClock.elapsedRealtime();
    }
    for (;;)
    {
      Object localObject = (Long)paramCaptureResult.get(CaptureResult.SENSOR_TIMESTAMP);
      if (localObject != null) {
        l2 = ((Long)localObject).longValue() / 1000000L + l1;
      }
      localObject = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
      ((DateFormat)localObject).setTimeZone(TimeZone.getDefault());
      localObject = ((DateFormat)localObject).format(Long.valueOf(l2));
      nativeInit(paramCameraCharacteristics.getNativeCopy(), paramCaptureResult.getNativeCopy(), (String)localObject);
      return;
      if (i == 0)
      {
        l1 = l2 - SystemClock.uptimeMillis();
      }
      else
      {
        Log.w("DngCreator", "Sensor timestamp source is unexpected: " + i);
        l1 = l2 - SystemClock.uptimeMillis();
      }
    }
  }
  
  private static void colorToRgb(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    paramArrayOfByte[paramInt2] = ((byte)Color.red(paramInt1));
    paramArrayOfByte[(paramInt2 + 1)] = ((byte)Color.green(paramInt1));
    paramArrayOfByte[(paramInt2 + 2)] = ((byte)Color.blue(paramInt1));
  }
  
  private static ByteBuffer convertToRGB(Bitmap paramBitmap)
  {
    int k = paramBitmap.getWidth();
    int m = paramBitmap.getHeight();
    ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(k * 3 * m);
    int[] arrayOfInt = new int[k];
    byte[] arrayOfByte = new byte[k * 3];
    int i = 0;
    while (i < m)
    {
      paramBitmap.getPixels(arrayOfInt, 0, k, 0, i, k, 1);
      int j = 0;
      while (j < k)
      {
        colorToRgb(arrayOfInt[j], j * 3, arrayOfByte);
        j += 1;
      }
      localByteBuffer.put(arrayOfByte);
      i += 1;
    }
    localByteBuffer.rewind();
    return localByteBuffer;
  }
  
  private static ByteBuffer convertToRGB(Image paramImage)
  {
    int k = paramImage.getWidth();
    int m = paramImage.getHeight();
    ByteBuffer localByteBuffer1 = ByteBuffer.allocateDirect(k * 3 * m);
    Object localObject1 = paramImage.getPlanes()[0];
    Object localObject2 = paramImage.getPlanes()[1];
    Object localObject3 = paramImage.getPlanes()[2];
    paramImage = ((Image.Plane)localObject1).getBuffer();
    ByteBuffer localByteBuffer2 = ((Image.Plane)localObject2).getBuffer();
    ByteBuffer localByteBuffer3 = ((Image.Plane)localObject3).getBuffer();
    paramImage.rewind();
    localByteBuffer2.rewind();
    localByteBuffer3.rewind();
    int n = ((Image.Plane)localObject1).getRowStride();
    int i1 = ((Image.Plane)localObject3).getRowStride();
    int i2 = ((Image.Plane)localObject2).getRowStride();
    int i3 = ((Image.Plane)localObject1).getPixelStride();
    int i4 = ((Image.Plane)localObject3).getPixelStride();
    int i5 = ((Image.Plane)localObject2).getPixelStride();
    localObject1 = new byte[3];
    Object tmp132_130 = localObject1;
    tmp132_130[0] = 0;
    Object tmp137_132 = tmp132_130;
    tmp137_132[1] = 0;
    Object tmp142_137 = tmp137_132;
    tmp142_137[2] = 0;
    tmp142_137;
    localObject2 = new byte[(k - 1) * i3 + 1];
    localObject3 = new byte[(k / 2 - 1) * i5 + 1];
    byte[] arrayOfByte1 = new byte[(k / 2 - 1) * i4 + 1];
    byte[] arrayOfByte2 = new byte[k * 3];
    int i = 0;
    while (i < m)
    {
      int j = i / 2;
      paramImage.position(n * i);
      paramImage.get((byte[])localObject2);
      localByteBuffer2.position(i2 * j);
      localByteBuffer2.get((byte[])localObject3);
      localByteBuffer3.position(i1 * j);
      localByteBuffer3.get(arrayOfByte1);
      j = 0;
      while (j < k)
      {
        int i6 = j / 2;
        localObject1[0] = localObject2[(i3 * j)];
        localObject1[1] = localObject3[(i5 * i6)];
        localObject1[2] = arrayOfByte1[(i4 * i6)];
        yuvToRgb((byte[])localObject1, j * 3, arrayOfByte2);
        j += 1;
      }
      localByteBuffer1.put(arrayOfByte2);
      i += 1;
    }
    paramImage.rewind();
    localByteBuffer2.rewind();
    localByteBuffer3.rewind();
    localByteBuffer1.rewind();
    return localByteBuffer1;
  }
  
  private static native void nativeClassInit();
  
  private synchronized native void nativeDestroy();
  
  private synchronized native void nativeInit(CameraMetadataNative paramCameraMetadataNative1, CameraMetadataNative paramCameraMetadataNative2, String paramString);
  
  private synchronized native void nativeSetDescription(String paramString);
  
  private synchronized native void nativeSetGpsTags(int[] paramArrayOfInt1, String paramString1, int[] paramArrayOfInt2, String paramString2, String paramString3, int[] paramArrayOfInt3);
  
  private synchronized native void nativeSetOrientation(int paramInt);
  
  private synchronized native void nativeSetThumbnail(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2);
  
  private synchronized native void nativeWriteImage(OutputStream paramOutputStream, int paramInt1, int paramInt2, ByteBuffer paramByteBuffer, int paramInt3, int paramInt4, long paramLong, boolean paramBoolean)
    throws IOException;
  
  private synchronized native void nativeWriteInputStream(OutputStream paramOutputStream, InputStream paramInputStream, int paramInt1, int paramInt2, long paramLong)
    throws IOException;
  
  private static int[] toExifLatLong(double paramDouble)
  {
    paramDouble = Math.abs(paramDouble);
    int i = (int)paramDouble;
    paramDouble = (paramDouble - i) * 60.0D;
    int j = (int)paramDouble;
    return new int[] { i, 1, j, 1, (int)((paramDouble - j) * 6000.0D), 100 };
  }
  
  private void writeByteBuffer(int paramInt1, int paramInt2, ByteBuffer paramByteBuffer, OutputStream paramOutputStream, int paramInt3, int paramInt4, long paramLong)
    throws IOException
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      throw new IllegalArgumentException("Image with invalid width, height: (" + paramInt1 + "," + paramInt2 + ") passed to write");
    }
    long l1 = paramByteBuffer.capacity();
    long l2 = paramInt4 * paramInt2 + paramLong;
    if (l1 < l2) {
      throw new IllegalArgumentException("Image size " + l1 + " is too small (must be larger than " + l2 + ")");
    }
    int i = paramInt3 * paramInt1;
    if (i > paramInt4) {
      throw new IllegalArgumentException("Invalid image pixel stride, row byte width " + i + " is too large, expecting " + paramInt4);
    }
    paramByteBuffer.clear();
    nativeWriteImage(paramOutputStream, paramInt1, paramInt2, paramByteBuffer, paramInt4, paramInt3, paramLong, paramByteBuffer.isDirect());
    paramByteBuffer.clear();
  }
  
  private static void yuvToRgb(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
  {
    float f1 = paramArrayOfByte1[0] & 0xFF;
    float f2 = paramArrayOfByte1[1] & 0xFF;
    float f3 = paramArrayOfByte1[2] & 0xFF;
    paramArrayOfByte2[paramInt] = ((byte)(int)Math.max(0.0F, Math.min(255.0F, f1 + (f3 - 128.0F) * 1.402F)));
    paramArrayOfByte2[(paramInt + 1)] = ((byte)(int)Math.max(0.0F, Math.min(255.0F, f1 - (f2 - 128.0F) * 0.34414F - (f3 - 128.0F) * 0.71414F)));
    paramArrayOfByte2[(paramInt + 2)] = ((byte)(int)Math.max(0.0F, Math.min(255.0F, f1 + (f2 - 128.0F) * 1.772F)));
  }
  
  public void close()
  {
    nativeDestroy();
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public DngCreator setDescription(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Null description passed to setDescription.");
    }
    nativeSetDescription(paramString);
    return this;
  }
  
  public DngCreator setLocation(Location paramLocation)
  {
    if (paramLocation == null) {
      throw new IllegalArgumentException("Null location passed to setLocation");
    }
    double d1 = paramLocation.getLatitude();
    double d2 = paramLocation.getLongitude();
    long l = paramLocation.getTime();
    int[] arrayOfInt1 = toExifLatLong(d1);
    int[] arrayOfInt2 = toExifLatLong(d2);
    if (d1 >= 0.0D)
    {
      paramLocation = "N";
      if (d2 < 0.0D) {
        break label160;
      }
    }
    label160:
    for (String str1 = "E";; str1 = "W")
    {
      String str2 = sExifGPSDateStamp.format(Long.valueOf(l));
      this.mGPSTimeStampCalendar.setTimeInMillis(l);
      nativeSetGpsTags(arrayOfInt1, paramLocation, arrayOfInt2, str1, str2, new int[] { this.mGPSTimeStampCalendar.get(11), 1, this.mGPSTimeStampCalendar.get(12), 1, this.mGPSTimeStampCalendar.get(13), 1 });
      return this;
      paramLocation = "S";
      break;
    }
  }
  
  public DngCreator setOrientation(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 8)) {
      throw new IllegalArgumentException("Orientation " + paramInt + " is not a valid EXIF orientation value");
    }
    int i = paramInt;
    if (paramInt == 0) {
      i = 9;
    }
    nativeSetOrientation(i);
    return this;
  }
  
  public DngCreator setThumbnail(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      throw new IllegalArgumentException("Null argument to setThumbnail");
    }
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    if ((i > 256) || (j > 256)) {
      throw new IllegalArgumentException("Thumbnail dimensions width,height (" + i + "," + j + ") too large, dimensions must be smaller than " + 256);
    }
    nativeSetThumbnail(convertToRGB(paramBitmap), i, j);
    return this;
  }
  
  public DngCreator setThumbnail(Image paramImage)
  {
    if (paramImage == null) {
      throw new IllegalArgumentException("Null argument to setThumbnail");
    }
    int i = paramImage.getFormat();
    if (i != 35) {
      throw new IllegalArgumentException("Unsupported Image format " + i);
    }
    i = paramImage.getWidth();
    int j = paramImage.getHeight();
    if ((i > 256) || (j > 256)) {
      throw new IllegalArgumentException("Thumbnail dimensions width,height (" + i + "," + j + ") too large, dimensions must be smaller than " + 256);
    }
    nativeSetThumbnail(convertToRGB(paramImage), i, j);
    return this;
  }
  
  public void writeByteBuffer(OutputStream paramOutputStream, Size paramSize, ByteBuffer paramByteBuffer, long paramLong)
    throws IOException
  {
    if (paramOutputStream == null) {
      throw new IllegalArgumentException("Null dngOutput passed to writeByteBuffer");
    }
    if (paramSize == null) {
      throw new IllegalArgumentException("Null size passed to writeByteBuffer");
    }
    if (paramByteBuffer == null) {
      throw new IllegalArgumentException("Null pixels passed to writeByteBuffer");
    }
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Negative offset passed to writeByteBuffer");
    }
    int i = paramSize.getWidth();
    writeByteBuffer(i, paramSize.getHeight(), paramByteBuffer, paramOutputStream, 2, i * 2, paramLong);
  }
  
  public void writeImage(OutputStream paramOutputStream, Image paramImage)
    throws IOException
  {
    if (paramOutputStream == null) {
      throw new IllegalArgumentException("Null dngOutput to writeImage");
    }
    if (paramImage == null) {
      throw new IllegalArgumentException("Null pixels to writeImage");
    }
    int i = paramImage.getFormat();
    if (i != 32) {
      throw new IllegalArgumentException("Unsupported image format " + i);
    }
    Image.Plane[] arrayOfPlane = paramImage.getPlanes();
    if ((arrayOfPlane == null) || (arrayOfPlane.length <= 0)) {
      throw new IllegalArgumentException("Image with no planes passed to writeImage");
    }
    ByteBuffer localByteBuffer = arrayOfPlane[0].getBuffer();
    writeByteBuffer(paramImage.getWidth(), paramImage.getHeight(), localByteBuffer, paramOutputStream, arrayOfPlane[0].getPixelStride(), arrayOfPlane[0].getRowStride(), 0L);
  }
  
  public void writeInputStream(OutputStream paramOutputStream, Size paramSize, InputStream paramInputStream, long paramLong)
    throws IOException
  {
    if (paramOutputStream == null) {
      throw new IllegalArgumentException("Null dngOutput passed to writeInputStream");
    }
    if (paramSize == null) {
      throw new IllegalArgumentException("Null size passed to writeInputStream");
    }
    if (paramInputStream == null) {
      throw new IllegalArgumentException("Null pixels passed to writeInputStream");
    }
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Negative offset passed to writeInputStream");
    }
    int i = paramSize.getWidth();
    int j = paramSize.getHeight();
    if ((i <= 0) || (j <= 0)) {
      throw new IllegalArgumentException("Size with invalid width, height: (" + i + "," + j + ") passed to writeInputStream");
    }
    nativeWriteInputStream(paramOutputStream, paramInputStream, i, j, paramLong);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/DngCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */