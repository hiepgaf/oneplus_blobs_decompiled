package android.media;

import android.graphics.Rect;
import android.hardware.camera2.utils.SurfaceUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Size;
import android.view.Surface;
import dalvik.system.VMRuntime;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.NioUtils;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ImageWriter
  implements AutoCloseable
{
  private List<Image> mDequeuedImages = new CopyOnWriteArrayList();
  private int mEstimatedNativeAllocBytes;
  private OnImageReleasedListener mListener;
  private ListenerHandler mListenerHandler;
  private final Object mListenerLock = new Object();
  private final int mMaxImages;
  private long mNativeContext;
  private int mWriterFormat;
  
  static
  {
    System.loadLibrary("media_jni");
    nativeClassInit();
  }
  
  protected ImageWriter(Surface paramSurface, int paramInt)
  {
    if ((paramSurface == null) || (paramInt < 1)) {
      throw new IllegalArgumentException("Illegal input argument: surface " + paramSurface + ", maxImages: " + paramInt);
    }
    this.mMaxImages = paramInt;
    this.mNativeContext = nativeInit(new WeakReference(this), paramSurface, paramInt);
    Size localSize = SurfaceUtils.getSurfaceSize(paramSurface);
    paramInt = SurfaceUtils.getSurfaceFormat(paramSurface);
    this.mEstimatedNativeAllocBytes = ImageUtils.getEstimatedNativeAllocBytes(localSize.getWidth(), localSize.getHeight(), paramInt, 1);
    VMRuntime.getRuntime().registerNativeAllocation(this.mEstimatedNativeAllocBytes);
  }
  
  private void abortImage(Image paramImage)
  {
    if (paramImage == null) {
      throw new IllegalArgumentException("image shouldn't be null");
    }
    if (!this.mDequeuedImages.contains(paramImage)) {
      throw new IllegalStateException("It is illegal to abort some image that is not dequeued yet");
    }
    WriterSurfaceImage localWriterSurfaceImage = (WriterSurfaceImage)paramImage;
    if (!localWriterSurfaceImage.mIsImageValid) {
      return;
    }
    cancelImage(this.mNativeContext, paramImage);
    this.mDequeuedImages.remove(paramImage);
    WriterSurfaceImage.-wrap0(localWriterSurfaceImage);
    localWriterSurfaceImage.mIsImageValid = false;
  }
  
  private void attachAndQueueInputImage(Image paramImage)
  {
    if (paramImage == null) {
      throw new IllegalArgumentException("image shouldn't be null");
    }
    if (isImageOwnedByMe(paramImage)) {
      throw new IllegalArgumentException("Can not attach an image that is owned ImageWriter already");
    }
    if (!paramImage.isAttachable()) {
      throw new IllegalStateException("Image was not detached from last owner, or image  is not detachable");
    }
    Rect localRect = paramImage.getCropRect();
    nativeAttachAndQueueImage(this.mNativeContext, paramImage.getNativeContext(), paramImage.getFormat(), paramImage.getTimestamp(), localRect.left, localRect.top, localRect.right, localRect.bottom);
  }
  
  private synchronized native void cancelImage(long paramLong, Image paramImage);
  
  private boolean isImageOwnedByMe(Image paramImage)
  {
    if (!(paramImage instanceof WriterSurfaceImage)) {
      return false;
    }
    return ((WriterSurfaceImage)paramImage).getOwner() == this;
  }
  
  private synchronized native int nativeAttachAndQueueImage(long paramLong1, long paramLong2, int paramInt1, long paramLong3, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  private static native void nativeClassInit();
  
  private synchronized native void nativeClose(long paramLong);
  
  private synchronized native void nativeDequeueInputImage(long paramLong, Image paramImage);
  
  private synchronized native long nativeInit(Object paramObject, Surface paramSurface, int paramInt);
  
  private synchronized native void nativeQueueInputImage(long paramLong1, Image paramImage, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public static ImageWriter newInstance(Surface paramSurface, int paramInt)
  {
    return new ImageWriter(paramSurface, paramInt);
  }
  
  private static void postEventFromNative(Object arg0)
  {
    Object localObject1 = (ImageWriter)((WeakReference)???).get();
    if (localObject1 == null) {
      return;
    }
    synchronized (((ImageWriter)localObject1).mListenerLock)
    {
      localObject1 = ((ImageWriter)localObject1).mListenerHandler;
      if (localObject1 != null) {
        ((Handler)localObject1).sendEmptyMessage(0);
      }
      return;
    }
  }
  
  public void close()
  {
    setOnImageReleasedListener(null, null);
    Iterator localIterator = this.mDequeuedImages.iterator();
    while (localIterator.hasNext()) {
      ((Image)localIterator.next()).close();
    }
    this.mDequeuedImages.clear();
    nativeClose(this.mNativeContext);
    this.mNativeContext = 0L;
    if (this.mEstimatedNativeAllocBytes > 0)
    {
      VMRuntime.getRuntime().registerNativeFree(this.mEstimatedNativeAllocBytes);
      this.mEstimatedNativeAllocBytes = 0;
    }
  }
  
  public Image dequeueInputImage()
  {
    if (this.mWriterFormat == 34) {
      throw new IllegalStateException("PRIVATE format ImageWriter doesn't support this operation since the images are inaccessible to the application!");
    }
    if (this.mDequeuedImages.size() >= this.mMaxImages) {
      throw new IllegalStateException("Already dequeued max number of Images " + this.mMaxImages);
    }
    WriterSurfaceImage localWriterSurfaceImage = new WriterSurfaceImage(this);
    nativeDequeueInputImage(this.mNativeContext, localWriterSurfaceImage);
    this.mDequeuedImages.add(localWriterSurfaceImage);
    localWriterSurfaceImage.mIsImageValid = true;
    return localWriterSurfaceImage;
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
  
  public int getFormat()
  {
    return this.mWriterFormat;
  }
  
  public int getMaxImages()
  {
    return this.mMaxImages;
  }
  
  public void queueInputImage(Image paramImage)
  {
    if (paramImage == null) {
      throw new IllegalArgumentException("image shouldn't be null");
    }
    boolean bool2 = isImageOwnedByMe(paramImage);
    if ((!bool2) || (((WriterSurfaceImage)paramImage).mIsImageValid))
    {
      bool1 = bool2;
      localObject = paramImage;
      if (bool2) {
        break label145;
      }
      if (!(paramImage.getOwner() instanceof ImageReader)) {
        throw new IllegalArgumentException("Only images from ImageReader can be queued to ImageWriter, other image source is not supported yet!");
      }
    }
    else
    {
      throw new IllegalStateException("Image from ImageWriter is invalid");
    }
    Object localObject = (ImageReader)paramImage.getOwner();
    if (paramImage.getFormat() == 34)
    {
      ((ImageReader)localObject).detachImage(paramImage);
      attachAndQueueInputImage(paramImage);
      paramImage.close();
      return;
    }
    localObject = dequeueInputImage();
    ((Image)localObject).setTimestamp(paramImage.getTimestamp());
    ((Image)localObject).setCropRect(paramImage.getCropRect());
    ImageUtils.imageCopy(paramImage, (Image)localObject);
    paramImage.close();
    boolean bool1 = true;
    label145:
    paramImage = ((Image)localObject).getCropRect();
    nativeQueueInputImage(this.mNativeContext, (Image)localObject, ((Image)localObject).getTimestamp(), paramImage.left, paramImage.top, paramImage.right, paramImage.bottom);
    if (bool1)
    {
      this.mDequeuedImages.remove(localObject);
      paramImage = (WriterSurfaceImage)localObject;
      WriterSurfaceImage.-wrap0(paramImage);
      paramImage.mIsImageValid = false;
    }
  }
  
  public void setOnImageReleasedListener(OnImageReleasedListener paramOnImageReleasedListener, Handler paramHandler)
  {
    Object localObject = this.mListenerLock;
    if (paramOnImageReleasedListener != null)
    {
      if (paramHandler != null) {}
      for (;;)
      {
        try
        {
          paramHandler = paramHandler.getLooper();
          if (paramHandler != null) {
            break;
          }
          throw new IllegalArgumentException("handler is null but the current thread is not a looper");
        }
        finally {}
        paramHandler = Looper.myLooper();
      }
      if ((this.mListenerHandler == null) || (this.mListenerHandler.getLooper() != paramHandler)) {
        this.mListenerHandler = new ListenerHandler(paramHandler);
      }
      this.mListener = paramOnImageReleasedListener;
    }
    for (;;)
    {
      return;
      this.mListener = null;
      this.mListenerHandler = null;
    }
  }
  
  private final class ListenerHandler
    extends Handler
  {
    public ListenerHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message arg1)
    {
      synchronized (ImageWriter.-get1(ImageWriter.this))
      {
        ImageWriter.OnImageReleasedListener localOnImageReleasedListener = ImageWriter.-get0(ImageWriter.this);
        if (localOnImageReleasedListener != null) {
          localOnImageReleasedListener.onImageReleased(ImageWriter.this);
        }
        return;
      }
    }
  }
  
  public static abstract interface OnImageReleasedListener
  {
    public abstract void onImageReleased(ImageWriter paramImageWriter);
  }
  
  private static class WriterSurfaceImage
    extends Image
  {
    private final long DEFAULT_TIMESTAMP = Long.MIN_VALUE;
    private int mFormat = -1;
    private int mHeight = -1;
    private long mNativeBuffer;
    private int mNativeFenceFd = -1;
    private ImageWriter mOwner;
    private SurfacePlane[] mPlanes;
    private long mTimestamp = Long.MIN_VALUE;
    private int mWidth = -1;
    
    public WriterSurfaceImage(ImageWriter paramImageWriter)
    {
      this.mOwner = paramImageWriter;
    }
    
    private void clearSurfacePlanes()
    {
      if ((this.mIsImageValid) && (this.mPlanes != null))
      {
        int i = 0;
        while (i < this.mPlanes.length)
        {
          if (this.mPlanes[i] != null)
          {
            SurfacePlane.-wrap0(this.mPlanes[i]);
            this.mPlanes[i] = null;
          }
          i += 1;
        }
      }
    }
    
    private synchronized native SurfacePlane[] nativeCreatePlanes(int paramInt1, int paramInt2);
    
    private synchronized native int nativeGetFormat();
    
    private synchronized native int nativeGetHeight();
    
    private synchronized native int nativeGetWidth();
    
    public void close()
    {
      if (this.mIsImageValid) {
        ImageWriter.-wrap0(getOwner(), this);
      }
    }
    
    protected final void finalize()
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
    
    public int getFormat()
    {
      throwISEIfImageIsInvalid();
      if (this.mFormat == -1) {
        this.mFormat = nativeGetFormat();
      }
      return this.mFormat;
    }
    
    public int getHeight()
    {
      throwISEIfImageIsInvalid();
      if (this.mHeight == -1) {
        this.mHeight = nativeGetHeight();
      }
      return this.mHeight;
    }
    
    long getNativeContext()
    {
      throwISEIfImageIsInvalid();
      return this.mNativeBuffer;
    }
    
    ImageWriter getOwner()
    {
      throwISEIfImageIsInvalid();
      return this.mOwner;
    }
    
    public Image.Plane[] getPlanes()
    {
      throwISEIfImageIsInvalid();
      if (this.mPlanes == null) {
        this.mPlanes = nativeCreatePlanes(ImageUtils.getNumPlanesForFormat(getFormat()), getOwner().getFormat());
      }
      return (Image.Plane[])this.mPlanes.clone();
    }
    
    public long getTimestamp()
    {
      throwISEIfImageIsInvalid();
      return this.mTimestamp;
    }
    
    public int getWidth()
    {
      throwISEIfImageIsInvalid();
      if (this.mWidth == -1) {
        this.mWidth = nativeGetWidth();
      }
      return this.mWidth;
    }
    
    boolean isAttachable()
    {
      throwISEIfImageIsInvalid();
      return false;
    }
    
    public void setTimestamp(long paramLong)
    {
      throwISEIfImageIsInvalid();
      this.mTimestamp = paramLong;
    }
    
    private class SurfacePlane
      extends Image.Plane
    {
      private ByteBuffer mBuffer;
      private final int mPixelStride;
      private final int mRowStride;
      
      private SurfacePlane(int paramInt1, int paramInt2, ByteBuffer paramByteBuffer)
      {
        this.mRowStride = paramInt1;
        this.mPixelStride = paramInt2;
        this.mBuffer = paramByteBuffer;
        this.mBuffer.order(ByteOrder.nativeOrder());
      }
      
      private void clearBuffer()
      {
        if (this.mBuffer == null) {
          return;
        }
        if (this.mBuffer.isDirect()) {
          NioUtils.freeDirectBuffer(this.mBuffer);
        }
        this.mBuffer = null;
      }
      
      public ByteBuffer getBuffer()
      {
        ImageWriter.WriterSurfaceImage.this.throwISEIfImageIsInvalid();
        return this.mBuffer;
      }
      
      public int getPixelStride()
      {
        ImageWriter.WriterSurfaceImage.this.throwISEIfImageIsInvalid();
        return this.mPixelStride;
      }
      
      public int getRowStride()
      {
        ImageWriter.WriterSurfaceImage.this.throwISEIfImageIsInvalid();
        return this.mRowStride;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/ImageWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */