package android.media;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import dalvik.system.VMRuntime;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.NioUtils;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImageReader
  implements AutoCloseable
{
  private static final int ACQUIRE_MAX_IMAGES = 2;
  private static final int ACQUIRE_NO_BUFS = 1;
  private static final int ACQUIRE_SUCCESS = 0;
  private List<Image> mAcquiredImages = new CopyOnWriteArrayList();
  private final Object mCloseLock = new Object();
  private int mEstimatedNativeAllocBytes;
  private final int mFormat;
  private final int mHeight;
  private boolean mIsReaderValid = false;
  private OnImageAvailableListener mListener;
  private ListenerHandler mListenerHandler;
  private final Object mListenerLock = new Object();
  private final int mMaxImages;
  private long mNativeContext;
  private final int mNumPlanes;
  private final Surface mSurface;
  private final int mWidth;
  
  static
  {
    System.loadLibrary("media_jni");
    nativeClassInit();
  }
  
  protected ImageReader(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    this.mFormat = paramInt3;
    this.mMaxImages = paramInt4;
    if ((paramInt1 < 1) || (paramInt2 < 1)) {
      throw new IllegalArgumentException("The image dimensions must be positive");
    }
    if (this.mMaxImages < 1) {
      throw new IllegalArgumentException("Maximum outstanding image count must be at least 1");
    }
    if (paramInt3 == 17) {
      throw new IllegalArgumentException("NV21 format is not supported");
    }
    this.mNumPlanes = ImageUtils.getNumPlanesForFormat(this.mFormat);
    nativeInit(new WeakReference(this), paramInt1, paramInt2, paramInt3, paramInt4);
    this.mSurface = nativeGetSurface();
    this.mIsReaderValid = true;
    this.mEstimatedNativeAllocBytes = ImageUtils.getEstimatedNativeAllocBytes(paramInt1, paramInt2, paramInt3, 1);
    VMRuntime.getRuntime().registerNativeAllocation(this.mEstimatedNativeAllocBytes);
  }
  
  private int acquireNextSurfaceImage(SurfaceImage paramSurfaceImage)
  {
    Object localObject = this.mCloseLock;
    int i = 1;
    for (;;)
    {
      try
      {
        if (!this.mIsReaderValid) {
          break label81;
        }
        i = nativeImageSetup(paramSurfaceImage);
      }
      finally {}
      throw new AssertionError("Unknown nativeImageSetup return code " + i);
      paramSurfaceImage.mIsImageValid = true;
      if (i == 0) {
        this.mAcquiredImages.add(paramSurfaceImage);
      }
      return i;
      label81:
      switch (i)
      {
      }
    }
  }
  
  private boolean isImageOwnedbyMe(Image paramImage)
  {
    boolean bool = false;
    if (!(paramImage instanceof SurfaceImage)) {
      return false;
    }
    if (((SurfaceImage)paramImage).getReader() == this) {
      bool = true;
    }
    return bool;
  }
  
  private static native void nativeClassInit();
  
  private synchronized native void nativeClose();
  
  private synchronized native int nativeDetachImage(Image paramImage);
  
  private synchronized native void nativeDiscardFreeBuffers();
  
  private synchronized native Surface nativeGetSurface();
  
  private synchronized native int nativeImageSetup(Image paramImage);
  
  private synchronized native void nativeInit(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  private synchronized native void nativeReleaseImage(Image paramImage);
  
  public static ImageReader newInstance(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return new ImageReader(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  private static void postEventFromNative(Object arg0)
  {
    Object localObject1 = (ImageReader)((WeakReference)???).get();
    if (localObject1 == null) {
      return;
    }
    synchronized (((ImageReader)localObject1).mListenerLock)
    {
      localObject1 = ((ImageReader)localObject1).mListenerHandler;
      if (localObject1 != null) {
        ((Handler)localObject1).sendEmptyMessage(0);
      }
      return;
    }
  }
  
  private void releaseImage(Image paramImage)
  {
    if (!(paramImage instanceof SurfaceImage)) {
      throw new IllegalArgumentException("This image was not produced by an ImageReader");
    }
    SurfaceImage localSurfaceImage = (SurfaceImage)paramImage;
    if (!localSurfaceImage.mIsImageValid) {
      return;
    }
    if ((localSurfaceImage.getReader() == this) && (this.mAcquiredImages.contains(paramImage)))
    {
      SurfaceImage.-wrap0(localSurfaceImage);
      nativeReleaseImage(paramImage);
      localSurfaceImage.mIsImageValid = false;
      this.mAcquiredImages.remove(paramImage);
      return;
    }
    throw new IllegalArgumentException("This image was not produced by this ImageReader");
  }
  
  public Image acquireLatestImage()
  {
    Image localImage2 = acquireNextImage();
    Image localImage1 = localImage2;
    if (localImage2 == null) {
      return null;
    }
    try
    {
      do
      {
        localImage1.close();
        localImage1 = localImage2;
        localImage2 = acquireNextImageNoThrowISE();
      } while (localImage2 != null);
      return localImage1;
    }
    finally
    {
      if (localImage1 != null) {
        localImage1.close();
      }
    }
  }
  
  public Image acquireNextImage()
  {
    SurfaceImage localSurfaceImage = new SurfaceImage(this.mFormat);
    int i = acquireNextSurfaceImage(localSurfaceImage);
    switch (i)
    {
    default: 
      throw new AssertionError("Unknown nativeImageSetup return code " + i);
    case 0: 
      return localSurfaceImage;
    case 1: 
      return null;
    }
    throw new IllegalStateException(String.format("maxImages (%d) has already been acquired, call #close before acquiring more.", new Object[] { Integer.valueOf(this.mMaxImages) }));
  }
  
  public Image acquireNextImageNoThrowISE()
  {
    SurfaceImage localSurfaceImage = new SurfaceImage(this.mFormat);
    if (acquireNextSurfaceImage(localSurfaceImage) == 0) {
      return localSurfaceImage;
    }
    return null;
  }
  
  public void close()
  {
    setOnImageAvailableListener(null, null);
    if (this.mSurface != null) {
      this.mSurface.release();
    }
    synchronized (this.mCloseLock)
    {
      this.mIsReaderValid = false;
      Iterator localIterator = this.mAcquiredImages.iterator();
      if (localIterator.hasNext()) {
        ((Image)localIterator.next()).close();
      }
    }
    this.mAcquiredImages.clear();
    nativeClose();
    if (this.mEstimatedNativeAllocBytes > 0)
    {
      VMRuntime.getRuntime().registerNativeFree(this.mEstimatedNativeAllocBytes);
      this.mEstimatedNativeAllocBytes = 0;
    }
  }
  
  void detachImage(Image paramImage)
  {
    if (paramImage == null) {
      throw new IllegalArgumentException("input image must not be null");
    }
    if (!isImageOwnedbyMe(paramImage)) {
      throw new IllegalArgumentException("Trying to detach an image that is not owned by this ImageReader");
    }
    SurfaceImage localSurfaceImage = (SurfaceImage)paramImage;
    localSurfaceImage.throwISEIfImageIsInvalid();
    if (localSurfaceImage.isAttachable()) {
      throw new IllegalStateException("Image was already detached from this ImageReader");
    }
    nativeDetachImage(paramImage);
    SurfaceImage.-wrap1(localSurfaceImage, true);
  }
  
  public void discardFreeBuffers()
  {
    synchronized (this.mCloseLock)
    {
      nativeDiscardFreeBuffers();
      return;
    }
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
  
  public int getHeight()
  {
    return this.mHeight;
  }
  
  public int getImageFormat()
  {
    return this.mFormat;
  }
  
  public int getMaxImages()
  {
    return this.mMaxImages;
  }
  
  public Surface getSurface()
  {
    return this.mSurface;
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  public void setOnImageAvailableListener(OnImageAvailableListener paramOnImageAvailableListener, Handler paramHandler)
  {
    Object localObject = this.mListenerLock;
    if (paramOnImageAvailableListener != null)
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
      this.mListener = paramOnImageAvailableListener;
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
      ImageReader.OnImageAvailableListener localOnImageAvailableListener;
      synchronized (ImageReader.-get4(ImageReader.this))
      {
        localOnImageAvailableListener = ImageReader.-get3(ImageReader.this);
      }
      synchronized (ImageReader.-get0(ImageReader.this))
      {
        boolean bool = ImageReader.-get2(ImageReader.this);
        if ((localOnImageAvailableListener != null) && (bool)) {
          localOnImageAvailableListener.onImageAvailable(ImageReader.this);
        }
        return;
        localObject1 = finally;
        throw ((Throwable)localObject1);
      }
    }
  }
  
  public static abstract interface OnImageAvailableListener
  {
    public abstract void onImageAvailable(ImageReader paramImageReader);
  }
  
  private class SurfaceImage
    extends Image
  {
    private int mFormat = 0;
    private AtomicBoolean mIsDetached = new AtomicBoolean(false);
    private long mNativeBuffer;
    private SurfacePlane[] mPlanes;
    private long mTimestamp;
    
    public SurfaceImage(int paramInt)
    {
      this.mFormat = paramInt;
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
    
    private synchronized native int nativeGetFormat(int paramInt);
    
    private synchronized native int nativeGetHeight();
    
    private synchronized native int nativeGetWidth();
    
    private void setDetached(boolean paramBoolean)
    {
      throwISEIfImageIsInvalid();
      this.mIsDetached.getAndSet(paramBoolean);
    }
    
    public void close()
    {
      ImageReader.-wrap0(ImageReader.this, this);
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
      int i = ImageReader.this.getImageFormat();
      if (i == 34) {}
      for (;;)
      {
        this.mFormat = i;
        return this.mFormat;
        i = nativeGetFormat(i);
      }
    }
    
    public int getHeight()
    {
      throwISEIfImageIsInvalid();
      switch (getFormat())
      {
      default: 
        return nativeGetHeight();
      }
      return ImageReader.this.getHeight();
    }
    
    long getNativeContext()
    {
      throwISEIfImageIsInvalid();
      return this.mNativeBuffer;
    }
    
    ImageReader getOwner()
    {
      throwISEIfImageIsInvalid();
      return ImageReader.this;
    }
    
    public Image.Plane[] getPlanes()
    {
      throwISEIfImageIsInvalid();
      if (this.mPlanes == null) {
        this.mPlanes = nativeCreatePlanes(ImageReader.-get5(ImageReader.this), ImageReader.-get1(ImageReader.this));
      }
      return (Image.Plane[])this.mPlanes.clone();
    }
    
    public ImageReader getReader()
    {
      return ImageReader.this;
    }
    
    public long getTimestamp()
    {
      throwISEIfImageIsInvalid();
      return this.mTimestamp;
    }
    
    public int getWidth()
    {
      throwISEIfImageIsInvalid();
      switch (getFormat())
      {
      default: 
        return nativeGetWidth();
      }
      return ImageReader.this.getWidth();
    }
    
    boolean isAttachable()
    {
      throwISEIfImageIsInvalid();
      return this.mIsDetached.get();
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
        ImageReader.SurfaceImage.this.throwISEIfImageIsInvalid();
        return this.mBuffer;
      }
      
      public int getPixelStride()
      {
        ImageReader.SurfaceImage.this.throwISEIfImageIsInvalid();
        if (ImageReader.-get1(ImageReader.this) == 36) {
          throw new UnsupportedOperationException("getPixelStride is not supported for RAW_PRIVATE plane");
        }
        return this.mPixelStride;
      }
      
      public int getRowStride()
      {
        ImageReader.SurfaceImage.this.throwISEIfImageIsInvalid();
        if (ImageReader.-get1(ImageReader.this) == 36) {
          throw new UnsupportedOperationException("getRowStride is not supported for RAW_PRIVATE plane");
        }
        return this.mRowStride;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/ImageReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */