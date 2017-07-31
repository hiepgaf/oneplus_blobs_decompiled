package android.hardware.camera2.legacy;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.ICameraDeviceCallbacks;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.impl.CaptureResultExtras;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.utils.ArrayUtils;
import android.hardware.camera2.utils.SubmitInfo;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class LegacyCameraDevice
  implements AutoCloseable
{
  private static final boolean DEBUG = false;
  private static final int GRALLOC_USAGE_HW_COMPOSER = 2048;
  private static final int GRALLOC_USAGE_HW_RENDER = 512;
  private static final int GRALLOC_USAGE_HW_TEXTURE = 256;
  private static final int GRALLOC_USAGE_HW_VIDEO_ENCODER = 65536;
  private static final int GRALLOC_USAGE_RENDERSCRIPT = 1048576;
  private static final int GRALLOC_USAGE_SW_READ_OFTEN = 3;
  private static final int ILLEGAL_VALUE = -1;
  public static final int MAX_DIMEN_FOR_ROUNDING = 1920;
  public static final int NATIVE_WINDOW_SCALING_MODE_SCALE_TO_WINDOW = 1;
  private final String TAG;
  private final Handler mCallbackHandler;
  private final HandlerThread mCallbackHandlerThread = new HandlerThread("CallbackThread");
  private final int mCameraId;
  private boolean mClosed = false;
  private SparseArray<Surface> mConfiguredSurfaces;
  private final ICameraDeviceCallbacks mDeviceCallbacks;
  private final CameraDeviceState mDeviceState = new CameraDeviceState();
  private final ConditionVariable mIdle = new ConditionVariable(true);
  private final RequestThreadManager mRequestThreadManager;
  private final Handler mResultHandler;
  private final HandlerThread mResultThread = new HandlerThread("ResultThread");
  private final CameraDeviceState.CameraDeviceStateListener mStateListener = new CameraDeviceState.CameraDeviceStateListener()
  {
    public void onBusy()
    {
      LegacyCameraDevice.-get1(LegacyCameraDevice.this).close();
    }
    
    public void onCaptureResult(final CameraMetadataNative paramAnonymousCameraMetadataNative, final RequestHolder paramAnonymousRequestHolder)
    {
      final CaptureResultExtras localCaptureResultExtras = LegacyCameraDevice.-wrap0(LegacyCameraDevice.this, paramAnonymousRequestHolder);
      LegacyCameraDevice.-get2(LegacyCameraDevice.this).post(new Runnable()
      {
        public void run()
        {
          try
          {
            LegacyCameraDevice.-get0(LegacyCameraDevice.this).onResultReceived(paramAnonymousCameraMetadataNative, localCaptureResultExtras);
            return;
          }
          catch (RemoteException localRemoteException)
          {
            throw new IllegalStateException("Received remote exception during onCameraError callback: ", localRemoteException);
          }
        }
      });
    }
    
    public void onCaptureStarted(final RequestHolder paramAnonymousRequestHolder, final long paramAnonymousLong)
    {
      final CaptureResultExtras localCaptureResultExtras = LegacyCameraDevice.-wrap0(LegacyCameraDevice.this, paramAnonymousRequestHolder);
      LegacyCameraDevice.-get2(LegacyCameraDevice.this).post(new Runnable()
      {
        public void run()
        {
          try
          {
            LegacyCameraDevice.-get0(LegacyCameraDevice.this).onCaptureStarted(localCaptureResultExtras, paramAnonymousLong);
            return;
          }
          catch (RemoteException localRemoteException)
          {
            throw new IllegalStateException("Received remote exception during onCameraError callback: ", localRemoteException);
          }
        }
      });
    }
    
    public void onConfiguring() {}
    
    public void onError(final int paramAnonymousInt, final Object paramAnonymousObject, final RequestHolder paramAnonymousRequestHolder)
    {
      switch (paramAnonymousInt)
      {
      }
      for (;;)
      {
        paramAnonymousObject = LegacyCameraDevice.-wrap1(LegacyCameraDevice.this, paramAnonymousRequestHolder, paramAnonymousInt, paramAnonymousObject);
        LegacyCameraDevice.-get2(LegacyCameraDevice.this).post(new Runnable()
        {
          public void run()
          {
            try
            {
              LegacyCameraDevice.-get0(LegacyCameraDevice.this).onDeviceError(paramAnonymousInt, paramAnonymousObject);
              return;
            }
            catch (RemoteException localRemoteException)
            {
              throw new IllegalStateException("Received remote exception during onCameraError callback: ", localRemoteException);
            }
          }
        });
        return;
        LegacyCameraDevice.-get1(LegacyCameraDevice.this).open();
      }
    }
    
    public void onIdle()
    {
      LegacyCameraDevice.-get1(LegacyCameraDevice.this).open();
      LegacyCameraDevice.-get2(LegacyCameraDevice.this).post(new Runnable()
      {
        public void run()
        {
          try
          {
            LegacyCameraDevice.-get0(LegacyCameraDevice.this).onDeviceIdle();
            return;
          }
          catch (RemoteException localRemoteException)
          {
            throw new IllegalStateException("Received remote exception during onCameraIdle callback: ", localRemoteException);
          }
        }
      });
    }
    
    public void onRepeatingRequestError(final long paramAnonymousLong)
    {
      LegacyCameraDevice.-get2(LegacyCameraDevice.this).post(new Runnable()
      {
        public void run()
        {
          try
          {
            LegacyCameraDevice.-get0(LegacyCameraDevice.this).onRepeatingRequestError(paramAnonymousLong);
            return;
          }
          catch (RemoteException localRemoteException)
          {
            throw new IllegalStateException("Received remote exception during onRepeatingRequestError callback: ", localRemoteException);
          }
        }
      });
    }
  };
  private final CameraCharacteristics mStaticCharacteristics;
  
  public LegacyCameraDevice(int paramInt, Camera paramCamera, CameraCharacteristics paramCameraCharacteristics, ICameraDeviceCallbacks paramICameraDeviceCallbacks)
  {
    this.mCameraId = paramInt;
    this.mDeviceCallbacks = paramICameraDeviceCallbacks;
    this.TAG = String.format("CameraDevice-%d-LE", new Object[] { Integer.valueOf(this.mCameraId) });
    this.mResultThread.start();
    this.mResultHandler = new Handler(this.mResultThread.getLooper());
    this.mCallbackHandlerThread.start();
    this.mCallbackHandler = new Handler(this.mCallbackHandlerThread.getLooper());
    this.mDeviceState.setCameraDeviceCallbacks(this.mCallbackHandler, this.mStateListener);
    this.mStaticCharacteristics = paramCameraCharacteristics;
    this.mRequestThreadManager = new RequestThreadManager(paramInt, paramCamera, paramCameraCharacteristics, this.mDeviceState);
    this.mRequestThreadManager.start();
  }
  
  static void connectSurface(Surface paramSurface)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    Preconditions.checkNotNull(paramSurface);
    LegacyExceptionUtils.throwOnError(nativeConnectSurface(paramSurface));
  }
  
  static boolean containsSurfaceId(Surface paramSurface, Collection<Long> paramCollection)
  {
    try
    {
      long l = getSurfaceId(paramSurface);
      return paramCollection.contains(Long.valueOf(l));
    }
    catch (LegacyExceptionUtils.BufferQueueAbandonedException paramSurface) {}
    return false;
  }
  
  public static int detectSurfaceDataspace(Surface paramSurface)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    Preconditions.checkNotNull(paramSurface);
    return LegacyExceptionUtils.throwOnError(nativeDetectSurfaceDataspace(paramSurface));
  }
  
  public static int detectSurfaceType(Surface paramSurface)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    Preconditions.checkNotNull(paramSurface);
    return LegacyExceptionUtils.throwOnError(nativeDetectSurfaceType(paramSurface));
  }
  
  static int detectSurfaceUsageFlags(Surface paramSurface)
  {
    Preconditions.checkNotNull(paramSurface);
    return nativeDetectSurfaceUsageFlags(paramSurface);
  }
  
  static void disconnectSurface(Surface paramSurface)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    if (paramSurface == null) {
      return;
    }
    LegacyExceptionUtils.throwOnError(nativeDisconnectSurface(paramSurface));
  }
  
  static Size findClosestSize(Size paramSize, Size[] paramArrayOfSize)
  {
    if ((paramSize == null) || (paramArrayOfSize == null)) {
      return null;
    }
    Object localObject1 = null;
    int i = 0;
    int j = paramArrayOfSize.length;
    while (i < j)
    {
      Size localSize = paramArrayOfSize[i];
      if (localSize.equals(paramSize)) {
        return paramSize;
      }
      Object localObject2 = localObject1;
      if (localSize.getWidth() <= 1920) {
        if (localObject1 != null)
        {
          localObject2 = localObject1;
          if (findEuclidDistSquare(paramSize, localSize) >= findEuclidDistSquare((Size)localObject1, localSize)) {}
        }
        else
        {
          localObject2 = localSize;
        }
      }
      i += 1;
      localObject1 = localObject2;
    }
    return (Size)localObject1;
  }
  
  static long findEuclidDistSquare(Size paramSize1, Size paramSize2)
  {
    long l1 = paramSize1.getWidth() - paramSize2.getWidth();
    long l2 = paramSize1.getHeight() - paramSize2.getHeight();
    return l1 * l1 + l2 * l2;
  }
  
  private CaptureResultExtras getExtrasFromRequest(RequestHolder paramRequestHolder)
  {
    return getExtrasFromRequest(paramRequestHolder, -1, null);
  }
  
  private CaptureResultExtras getExtrasFromRequest(RequestHolder paramRequestHolder, int paramInt, Object paramObject)
  {
    int j = -1;
    int i = j;
    if (paramInt == 5)
    {
      paramObject = (Surface)paramObject;
      paramInt = this.mConfiguredSurfaces.indexOfValue(paramObject);
      if (paramInt >= 0) {
        break label66;
      }
      Log.e(this.TAG, "Buffer drop error reported for unknown Surface");
    }
    label66:
    for (i = j; paramRequestHolder == null; i = this.mConfiguredSurfaces.keyAt(paramInt)) {
      return new CaptureResultExtras(-1, -1, -1, -1, -1L, -1, -1);
    }
    return new CaptureResultExtras(paramRequestHolder.getRequestId(), paramRequestHolder.getSubsequeceId(), 0, 0, paramRequestHolder.getFrameNumber(), 1, i);
  }
  
  static long getSurfaceId(Surface paramSurface)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    Preconditions.checkNotNull(paramSurface);
    try
    {
      long l = nativeGetSurfaceId(paramSurface);
      return l;
    }
    catch (IllegalArgumentException paramSurface)
    {
      throw new LegacyExceptionUtils.BufferQueueAbandonedException();
    }
  }
  
  static List<Long> getSurfaceIds(SparseArray<Surface> paramSparseArray)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    if (paramSparseArray == null) {
      throw new NullPointerException("Null argument surfaces");
    }
    ArrayList localArrayList = new ArrayList();
    int j = paramSparseArray.size();
    int i = 0;
    while (i < j)
    {
      long l = getSurfaceId((Surface)paramSparseArray.valueAt(i));
      if (l == 0L) {
        throw new IllegalStateException("Configured surface had null native GraphicBufferProducer pointer!");
      }
      localArrayList.add(Long.valueOf(l));
      i += 1;
    }
    return localArrayList;
  }
  
  static List<Long> getSurfaceIds(Collection<Surface> paramCollection)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    if (paramCollection == null) {
      throw new NullPointerException("Null argument surfaces");
    }
    ArrayList localArrayList = new ArrayList();
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      long l = getSurfaceId((Surface)paramCollection.next());
      if (l == 0L) {
        throw new IllegalStateException("Configured surface had null native GraphicBufferProducer pointer!");
      }
      localArrayList.add(Long.valueOf(l));
    }
    return localArrayList;
  }
  
  public static Size getSurfaceSize(Surface paramSurface)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    Preconditions.checkNotNull(paramSurface);
    int[] arrayOfInt = new int[2];
    LegacyExceptionUtils.throwOnError(nativeDetectSurfaceDimens(paramSurface, arrayOfInt));
    return new Size(arrayOfInt[0], arrayOfInt[1]);
  }
  
  static Size getTextureSize(SurfaceTexture paramSurfaceTexture)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    Preconditions.checkNotNull(paramSurfaceTexture);
    int[] arrayOfInt = new int[2];
    LegacyExceptionUtils.throwOnError(nativeDetectTextureDimens(paramSurfaceTexture, arrayOfInt));
    return new Size(arrayOfInt[0], arrayOfInt[1]);
  }
  
  public static boolean isFlexibleConsumer(Surface paramSurface)
  {
    boolean bool2 = false;
    int i = detectSurfaceUsageFlags(paramSurface);
    boolean bool1 = bool2;
    if ((0x110000 & i) == 0)
    {
      bool1 = bool2;
      if ((i & 0x903) != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean isPreviewConsumer(Surface paramSurface)
  {
    int i = detectSurfaceUsageFlags(paramSurface);
    boolean bool;
    if ((0x110003 & i) == 0) {
      if ((i & 0xB00) != 0) {
        bool = true;
      }
    }
    for (;;)
    {
      try
      {
        detectSurfaceType(paramSurface);
        return bool;
      }
      catch (LegacyExceptionUtils.BufferQueueAbandonedException paramSurface)
      {
        throw new IllegalArgumentException("Surface was abandoned", paramSurface);
      }
      bool = false;
      continue;
      bool = false;
    }
  }
  
  public static boolean isVideoEncoderConsumer(Surface paramSurface)
  {
    int i = detectSurfaceUsageFlags(paramSurface);
    boolean bool;
    if ((0x100903 & i) == 0) {
      if ((0x10000 & i) != 0) {
        bool = true;
      }
    }
    for (;;)
    {
      try
      {
        detectSurfaceType(paramSurface);
        return bool;
      }
      catch (LegacyExceptionUtils.BufferQueueAbandonedException paramSurface)
      {
        throw new IllegalArgumentException("Surface was abandoned", paramSurface);
      }
      bool = false;
      continue;
      bool = false;
    }
  }
  
  private static native int nativeConnectSurface(Surface paramSurface);
  
  private static native int nativeDetectSurfaceDataspace(Surface paramSurface);
  
  private static native int nativeDetectSurfaceDimens(Surface paramSurface, int[] paramArrayOfInt);
  
  private static native int nativeDetectSurfaceType(Surface paramSurface);
  
  private static native int nativeDetectSurfaceUsageFlags(Surface paramSurface);
  
  private static native int nativeDetectTextureDimens(SurfaceTexture paramSurfaceTexture, int[] paramArrayOfInt);
  
  private static native int nativeDisconnectSurface(Surface paramSurface);
  
  static native int nativeGetJpegFooterSize();
  
  private static native long nativeGetSurfaceId(Surface paramSurface);
  
  private static native int nativeProduceFrame(Surface paramSurface, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
  
  private static native int nativeSetNextTimestamp(Surface paramSurface, long paramLong);
  
  private static native int nativeSetScalingMode(Surface paramSurface, int paramInt);
  
  private static native int nativeSetSurfaceDimens(Surface paramSurface, int paramInt1, int paramInt2);
  
  private static native int nativeSetSurfaceFormat(Surface paramSurface, int paramInt);
  
  private static native int nativeSetSurfaceOrientation(Surface paramSurface, int paramInt1, int paramInt2);
  
  static boolean needsConversion(Surface paramSurface)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    int i = detectSurfaceType(paramSurface);
    if ((i == 35) || (i == 842094169)) {}
    while (i == 17) {
      return true;
    }
    return false;
  }
  
  static void produceFrame(Surface paramSurface, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    Preconditions.checkNotNull(paramSurface);
    Preconditions.checkNotNull(paramArrayOfByte);
    Preconditions.checkArgumentPositive(paramInt1, "width must be positive.");
    Preconditions.checkArgumentPositive(paramInt2, "height must be positive.");
    LegacyExceptionUtils.throwOnError(nativeProduceFrame(paramSurface, paramArrayOfByte, paramInt1, paramInt2, paramInt3));
  }
  
  static void setNextTimestamp(Surface paramSurface, long paramLong)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    Preconditions.checkNotNull(paramSurface);
    LegacyExceptionUtils.throwOnError(nativeSetNextTimestamp(paramSurface, paramLong));
  }
  
  static void setScalingMode(Surface paramSurface, int paramInt)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    Preconditions.checkNotNull(paramSurface);
    LegacyExceptionUtils.throwOnError(nativeSetScalingMode(paramSurface, paramInt));
  }
  
  static void setSurfaceDimens(Surface paramSurface, int paramInt1, int paramInt2)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    Preconditions.checkNotNull(paramSurface);
    Preconditions.checkArgumentPositive(paramInt1, "width must be positive.");
    Preconditions.checkArgumentPositive(paramInt2, "height must be positive.");
    LegacyExceptionUtils.throwOnError(nativeSetSurfaceDimens(paramSurface, paramInt1, paramInt2));
  }
  
  static void setSurfaceFormat(Surface paramSurface, int paramInt)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    Preconditions.checkNotNull(paramSurface);
    LegacyExceptionUtils.throwOnError(nativeSetSurfaceFormat(paramSurface, paramInt));
  }
  
  static void setSurfaceOrientation(Surface paramSurface, int paramInt1, int paramInt2)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    Preconditions.checkNotNull(paramSurface);
    LegacyExceptionUtils.throwOnError(nativeSetSurfaceOrientation(paramSurface, paramInt1, paramInt2));
  }
  
  public long cancelRequest(int paramInt)
  {
    return this.mRequestThreadManager.cancelRepeating(paramInt);
  }
  
  public void close()
  {
    this.mRequestThreadManager.quit();
    this.mCallbackHandlerThread.quitSafely();
    this.mResultThread.quitSafely();
    try
    {
      this.mCallbackHandlerThread.join();
    }
    catch (InterruptedException localInterruptedException1)
    {
      try
      {
        for (;;)
        {
          this.mResultThread.join();
          this.mClosed = true;
          return;
          localInterruptedException1 = localInterruptedException1;
          Log.e(this.TAG, String.format("Thread %s (%d) interrupted while quitting.", new Object[] { this.mCallbackHandlerThread.getName(), Long.valueOf(this.mCallbackHandlerThread.getId()) }));
        }
      }
      catch (InterruptedException localInterruptedException2)
      {
        for (;;)
        {
          Log.e(this.TAG, String.format("Thread %s (%d) interrupted while quitting.", new Object[] { this.mResultThread.getName(), Long.valueOf(this.mResultThread.getId()) }));
        }
      }
    }
  }
  
  public int configureOutputs(SparseArray<Surface> paramSparseArray)
  {
    ArrayList localArrayList = new ArrayList();
    int i;
    Surface localSurface;
    StreamConfigurationMap localStreamConfigurationMap;
    if (paramSparseArray != null)
    {
      int j = paramSparseArray.size();
      i = 0;
      if (i < j)
      {
        localSurface = (Surface)paramSparseArray.valueAt(i);
        if (localSurface == null)
        {
          Log.e(this.TAG, "configureOutputs - null outputs are not allowed");
          return LegacyExceptionUtils.BAD_VALUE;
        }
        if (!localSurface.isValid())
        {
          Log.e(this.TAG, "configureOutputs - invalid output surfaces are not allowed");
          return LegacyExceptionUtils.BAD_VALUE;
        }
        localStreamConfigurationMap = (StreamConfigurationMap)this.mStaticCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
      }
    }
    for (;;)
    {
      boolean bool;
      Object localObject1;
      try
      {
        Size localSize = getSurfaceSize(localSurface);
        int k = detectSurfaceType(localSurface);
        bool = isFlexibleConsumer(localSurface);
        Object localObject2 = localStreamConfigurationMap.getOutputSizes(k);
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          if ((k >= 1) && (k <= 5)) {
            localObject1 = localStreamConfigurationMap.getOutputSizes(35);
          }
        }
        else
        {
          if (ArrayUtils.contains((Object[])localObject1, localSize)) {
            continue;
          }
          localObject2 = localSize;
          if (!bool) {
            break label425;
          }
          localSize = findClosestSize(localSize, (Size[])localObject1);
          localObject2 = localSize;
          if (localSize == null) {
            break label425;
          }
          localArrayList.add(new Pair(localSurface, localSize));
          setSurfaceDimens(localSurface, localSize.getWidth(), localSize.getHeight());
          i += 1;
          break;
        }
        localObject1 = localObject2;
        if (k != 33) {
          continue;
        }
        localObject1 = localStreamConfigurationMap.getOutputSizes(256);
        continue;
        Log.e(this.TAG, String.format("Surface with size (w=%d, h=%d) and format 0x%x is not valid, %s", new Object[] { Integer.valueOf(((Size)localObject2).getWidth()), Integer.valueOf(((Size)localObject2).getHeight()), Integer.valueOf(k), paramSparseArray }));
        return LegacyExceptionUtils.BAD_VALUE;
        paramSparseArray = "size not in valid set: " + Arrays.toString((Object[])localObject1);
        continue;
        localArrayList.add(new Pair(localSurface, localSize));
        continue;
        bool = false;
      }
      catch (LegacyExceptionUtils.BufferQueueAbandonedException paramSparseArray)
      {
        Log.e(this.TAG, "Surface bufferqueue is abandoned, cannot configure as output: ", paramSparseArray);
        return LegacyExceptionUtils.BAD_VALUE;
      }
      if (this.mDeviceState.setConfiguring())
      {
        this.mRequestThreadManager.configure(localArrayList);
        bool = this.mDeviceState.setIdle();
      }
      if (bool)
      {
        this.mConfiguredSurfaces = paramSparseArray;
        return 0;
      }
      return LegacyExceptionUtils.INVALID_OPERATION;
      label425:
      if (localObject1 == null) {
        paramSparseArray = "format is invalid.";
      }
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
    catch (ServiceSpecificException localServiceSpecificException)
    {
      Log.e(this.TAG, "Got error while trying to finalize, ignoring: " + localServiceSpecificException.getMessage());
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public long flush()
  {
    long l = this.mRequestThreadManager.flush();
    waitUntilIdle();
    return l;
  }
  
  public boolean isClosed()
  {
    return this.mClosed;
  }
  
  public SubmitInfo submitRequest(CaptureRequest paramCaptureRequest, boolean paramBoolean)
  {
    return submitRequestList(new CaptureRequest[] { paramCaptureRequest }, paramBoolean);
  }
  
  public SubmitInfo submitRequestList(CaptureRequest[] paramArrayOfCaptureRequest, boolean paramBoolean)
  {
    int i = 0;
    if ((paramArrayOfCaptureRequest == null) || (paramArrayOfCaptureRequest.length == 0))
    {
      Log.e(this.TAG, "submitRequestList - Empty/null requests are not allowed");
      throw new ServiceSpecificException(LegacyExceptionUtils.BAD_VALUE, "submitRequestList - Empty/null requests are not allowed");
    }
    for (;;)
    {
      Object localObject1;
      Object localObject2;
      try
      {
        if (this.mConfiguredSurfaces == null)
        {
          localObject1 = new ArrayList();
          int j = paramArrayOfCaptureRequest.length;
          if (i >= j) {
            break;
          }
          localObject2 = paramArrayOfCaptureRequest[i];
          if (((CaptureRequest)localObject2).getTargets().isEmpty())
          {
            Log.e(this.TAG, "submitRequestList - Each request must have at least one Surface target");
            throw new ServiceSpecificException(LegacyExceptionUtils.BAD_VALUE, "submitRequestList - Each request must have at least one Surface target");
          }
        }
        else
        {
          localObject1 = getSurfaceIds(this.mConfiguredSurfaces);
          continue;
        }
        localObject2 = ((CaptureRequest)localObject2).getTargets().iterator();
      }
      catch (LegacyExceptionUtils.BufferQueueAbandonedException paramArrayOfCaptureRequest)
      {
        throw new ServiceSpecificException(LegacyExceptionUtils.BAD_VALUE, "submitRequestList - configured surface is abandoned.");
      }
      while (((Iterator)localObject2).hasNext())
      {
        Surface localSurface = (Surface)((Iterator)localObject2).next();
        if (localSurface == null)
        {
          Log.e(this.TAG, "submitRequestList - Null Surface targets are not allowed");
          throw new ServiceSpecificException(LegacyExceptionUtils.BAD_VALUE, "submitRequestList - Null Surface targets are not allowed");
        }
        if (this.mConfiguredSurfaces == null)
        {
          Log.e(this.TAG, "submitRequestList - must configure  device with valid surfaces before submitting requests");
          throw new ServiceSpecificException(LegacyExceptionUtils.INVALID_OPERATION, "submitRequestList - must configure  device with valid surfaces before submitting requests");
        }
        if (!containsSurfaceId(localSurface, (Collection)localObject1))
        {
          Log.e(this.TAG, "submitRequestList - cannot use a surface that wasn't configured");
          throw new ServiceSpecificException(LegacyExceptionUtils.BAD_VALUE, "submitRequestList - cannot use a surface that wasn't configured");
        }
      }
      i += 1;
    }
    this.mIdle.close();
    return this.mRequestThreadManager.submitCaptureRequests(paramArrayOfCaptureRequest, paramBoolean);
  }
  
  public void waitUntilIdle()
  {
    this.mIdle.block();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/LegacyCameraDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */