package android.hardware.camera2.legacy;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.utils.SizeAreaComparator;
import android.hardware.camera2.utils.SubmitInfo;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RequestThreadManager
{
  private static final float ASPECT_RATIO_TOLERANCE = 0.01F;
  private static final boolean DEBUG = false;
  private static final int JPEG_FRAME_TIMEOUT = 4000;
  private static final int MAX_IN_FLIGHT_REQUESTS = 2;
  private static final int MSG_CLEANUP = 3;
  private static final int MSG_CONFIGURE_OUTPUTS = 1;
  private static final int MSG_SUBMIT_CAPTURE_REQUEST = 2;
  private static final int PREVIEW_FRAME_TIMEOUT = 1000;
  private static final int REQUEST_COMPLETE_TIMEOUT = 4000;
  private static final boolean USE_BLOB_FORMAT_OVERRIDE = true;
  private static final boolean VERBOSE = false;
  private final String TAG;
  private final List<Surface> mCallbackOutputs = new ArrayList();
  private Camera mCamera;
  private final int mCameraId;
  private final CaptureCollector mCaptureCollector;
  private final CameraCharacteristics mCharacteristics;
  private final CameraDeviceState mDeviceState;
  private Surface mDummySurface;
  private SurfaceTexture mDummyTexture;
  private final Camera.ErrorCallback mErrorCallback = new Camera.ErrorCallback()
  {
    public void onError(int paramAnonymousInt, Camera paramAnonymousCamera)
    {
      switch (paramAnonymousInt)
      {
      default: 
        Log.e(RequestThreadManager.-get0(RequestThreadManager.this), "Received error " + paramAnonymousInt + " from the Camera1 ErrorCallback");
        RequestThreadManager.-get5(RequestThreadManager.this).setError(1);
        return;
      }
      RequestThreadManager.this.flush();
      RequestThreadManager.-get5(RequestThreadManager.this).setError(0);
    }
  };
  private final LegacyFaceDetectMapper mFaceDetectMapper;
  private final LegacyFocusStateMapper mFocusStateMapper;
  private GLThreadManager mGLThreadManager;
  private final Object mIdleLock = new Object();
  private Size mIntermediateBufferSize;
  private final Camera.PictureCallback mJpegCallback = new Camera.PictureCallback()
  {
    public void onPictureTaken(byte[] paramAnonymousArrayOfByte, Camera paramAnonymousCamera)
    {
      Log.i(RequestThreadManager.-get0(RequestThreadManager.this), "Received jpeg.");
      paramAnonymousCamera = RequestThreadManager.-get3(RequestThreadManager.this).jpegProduced();
      if ((paramAnonymousCamera == null) || (paramAnonymousCamera.first == null))
      {
        Log.e(RequestThreadManager.-get0(RequestThreadManager.this), "Dropping jpeg frame.");
        return;
      }
      Object localObject = (RequestHolder)paramAnonymousCamera.first;
      long l = ((Long)paramAnonymousCamera.second).longValue();
      paramAnonymousCamera = ((RequestHolder)localObject).getHolderTargets().iterator();
      while (paramAnonymousCamera.hasNext())
      {
        localObject = (Surface)paramAnonymousCamera.next();
        try
        {
          if (LegacyCameraDevice.containsSurfaceId((Surface)localObject, RequestThreadManager.-get10(RequestThreadManager.this)))
          {
            Log.i(RequestThreadManager.-get0(RequestThreadManager.this), "Producing jpeg buffer...");
            int i = paramAnonymousArrayOfByte.length;
            int j = LegacyCameraDevice.nativeGetJpegFooterSize();
            LegacyCameraDevice.setNextTimestamp((Surface)localObject, l);
            LegacyCameraDevice.setSurfaceFormat((Surface)localObject, 1);
            i = (int)Math.ceil(Math.sqrt(i + j + 3 & 0xFFFFFFFC)) + 15 & 0xFFFFFFF0;
            LegacyCameraDevice.setSurfaceDimens((Surface)localObject, i, i);
            LegacyCameraDevice.produceFrame((Surface)localObject, paramAnonymousArrayOfByte, i, i, 33);
          }
        }
        catch (LegacyExceptionUtils.BufferQueueAbandonedException localBufferQueueAbandonedException)
        {
          Log.w(RequestThreadManager.-get0(RequestThreadManager.this), "Surface abandoned, dropping frame. ", localBufferQueueAbandonedException);
        }
      }
      RequestThreadManager.-get13(RequestThreadManager.this).open();
    }
  };
  private final Camera.ShutterCallback mJpegShutterCallback = new Camera.ShutterCallback()
  {
    public void onShutter()
    {
      RequestThreadManager.-get3(RequestThreadManager.this).jpegCaptured(SystemClock.elapsedRealtimeNanos());
    }
  };
  private final List<Long> mJpegSurfaceIds = new ArrayList();
  private LegacyRequest mLastRequest = null;
  private Camera.Parameters mParams;
  private final FpsCounter mPrevCounter = new FpsCounter("Incoming Preview");
  private final SurfaceTexture.OnFrameAvailableListener mPreviewCallback = new SurfaceTexture.OnFrameAvailableListener()
  {
    public void onFrameAvailable(SurfaceTexture paramAnonymousSurfaceTexture)
    {
      RequestThreadManager.-get8(RequestThreadManager.this).queueNewFrame();
    }
  };
  private final List<Surface> mPreviewOutputs = new ArrayList();
  private boolean mPreviewRunning = false;
  private SurfaceTexture mPreviewTexture;
  private final AtomicBoolean mQuit = new AtomicBoolean(false);
  private final ConditionVariable mReceivedJpeg = new ConditionVariable(false);
  private final FpsCounter mRequestCounter = new FpsCounter("Incoming Requests");
  private final Handler.Callback mRequestHandlerCb = new Handler.Callback()
  {
    private boolean mCleanup = false;
    private final LegacyResultMapper mMapper = new LegacyResultMapper();
    
    /* Error */
    public boolean handleMessage(android.os.Message paramAnonymousMessage)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 23	android/hardware/camera2/legacy/RequestThreadManager$5:mCleanup	Z
      //   4: ifeq +5 -> 9
      //   7: iconst_1
      //   8: ireturn
      //   9: aload_1
      //   10: getfield 43	android/os/Message:what	I
      //   13: tableswitch	default:+35->48, -1:+190->203, 0:+35->48, 1:+70->83, 2:+225->238, 3:+1271->1284
      //   48: new 45	java/lang/AssertionError
      //   51: dup
      //   52: new 47	java/lang/StringBuilder
      //   55: dup
      //   56: invokespecial 48	java/lang/StringBuilder:<init>	()V
      //   59: ldc 50
      //   61: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   64: aload_1
      //   65: getfield 43	android/os/Message:what	I
      //   68: invokevirtual 57	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   71: ldc 59
      //   73: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   76: invokevirtual 63	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   79: invokespecial 66	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
      //   82: athrow
      //   83: aload_1
      //   84: getfield 70	android/os/Message:obj	Ljava/lang/Object;
      //   87: checkcast 72	android/hardware/camera2/legacy/RequestThreadManager$ConfigureHolder
      //   90: astore_1
      //   91: aload_1
      //   92: getfield 76	android/hardware/camera2/legacy/RequestThreadManager$ConfigureHolder:surfaces	Ljava/util/Collection;
      //   95: ifnull +110 -> 205
      //   98: aload_1
      //   99: getfield 76	android/hardware/camera2/legacy/RequestThreadManager$ConfigureHolder:surfaces	Ljava/util/Collection;
      //   102: invokeinterface 82 1 0
      //   107: istore_2
      //   108: aload_0
      //   109: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   112: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   115: new 47	java/lang/StringBuilder
      //   118: dup
      //   119: invokespecial 48	java/lang/StringBuilder:<init>	()V
      //   122: ldc 88
      //   124: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   127: iload_2
      //   128: invokevirtual 57	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   131: ldc 90
      //   133: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   136: invokevirtual 63	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   139: invokestatic 96	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   142: pop
      //   143: aload_0
      //   144: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   147: invokestatic 100	android/hardware/camera2/legacy/RequestThreadManager:-get3	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CaptureCollector;
      //   150: ldc2_w 101
      //   153: getstatic 108	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
      //   156: invokevirtual 114	android/hardware/camera2/legacy/CaptureCollector:waitForEmpty	(JLjava/util/concurrent/TimeUnit;)Z
      //   159: ifne +26 -> 185
      //   162: aload_0
      //   163: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   166: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   169: ldc 116
      //   171: invokestatic 119	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   174: pop
      //   175: aload_0
      //   176: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   179: invokestatic 100	android/hardware/camera2/legacy/RequestThreadManager:-get3	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CaptureCollector;
      //   182: invokevirtual 122	android/hardware/camera2/legacy/CaptureCollector:failAll	()V
      //   185: aload_0
      //   186: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   189: aload_1
      //   190: getfield 76	android/hardware/camera2/legacy/RequestThreadManager$ConfigureHolder:surfaces	Ljava/util/Collection;
      //   193: invokestatic 126	android/hardware/camera2/legacy/RequestThreadManager:-wrap0	(Landroid/hardware/camera2/legacy/RequestThreadManager;Ljava/util/Collection;)V
      //   196: aload_1
      //   197: getfield 130	android/hardware/camera2/legacy/RequestThreadManager$ConfigureHolder:condition	Landroid/os/ConditionVariable;
      //   200: invokevirtual 135	android/os/ConditionVariable:open	()V
      //   203: iconst_1
      //   204: ireturn
      //   205: iconst_0
      //   206: istore_2
      //   207: goto -99 -> 108
      //   210: astore_1
      //   211: aload_0
      //   212: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   215: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   218: ldc -119
      //   220: invokestatic 119	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   223: pop
      //   224: aload_0
      //   225: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   228: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   231: iconst_1
      //   232: invokevirtual 147	android/hardware/camera2/legacy/CameraDeviceState:setError	(I)V
      //   235: goto -32 -> 203
      //   238: aload_0
      //   239: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   242: invokestatic 151	android/hardware/camera2/legacy/RequestThreadManager:-get15	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/RequestHandlerThread;
      //   245: invokevirtual 157	android/hardware/camera2/legacy/RequestHandlerThread:getHandler	()Landroid/os/Handler;
      //   248: astore 8
      //   250: iconst_0
      //   251: istore_3
      //   252: aload_0
      //   253: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   256: invokestatic 161	android/hardware/camera2/legacy/RequestThreadManager:-get14	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/RequestQueue;
      //   259: invokevirtual 167	android/hardware/camera2/legacy/RequestQueue:getNext	()Landroid/util/Pair;
      //   262: astore 7
      //   264: aload 7
      //   266: astore_1
      //   267: aload 7
      //   269: ifnonnull +121 -> 390
      //   272: aload_0
      //   273: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   276: invokestatic 100	android/hardware/camera2/legacy/RequestThreadManager:-get3	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CaptureCollector;
      //   279: ldc2_w 101
      //   282: getstatic 108	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
      //   285: invokevirtual 114	android/hardware/camera2/legacy/CaptureCollector:waitForEmpty	(JLjava/util/concurrent/TimeUnit;)Z
      //   288: ifne +26 -> 314
      //   291: aload_0
      //   292: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   295: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   298: ldc -87
      //   300: invokestatic 119	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   303: pop
      //   304: aload_0
      //   305: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   308: invokestatic 100	android/hardware/camera2/legacy/RequestThreadManager:-get3	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CaptureCollector;
      //   311: invokevirtual 122	android/hardware/camera2/legacy/CaptureCollector:failAll	()V
      //   314: aload_0
      //   315: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   318: invokestatic 173	android/hardware/camera2/legacy/RequestThreadManager:-get9	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/Object;
      //   321: astore 7
      //   323: aload 7
      //   325: monitorenter
      //   326: aload_0
      //   327: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   330: invokestatic 161	android/hardware/camera2/legacy/RequestThreadManager:-get14	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/RequestQueue;
      //   333: invokevirtual 167	android/hardware/camera2/legacy/RequestQueue:getNext	()Landroid/util/Pair;
      //   336: astore_1
      //   337: aload_1
      //   338: ifnonnull +49 -> 387
      //   341: aload_0
      //   342: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   345: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   348: invokevirtual 177	android/hardware/camera2/legacy/CameraDeviceState:setIdle	()Z
      //   351: pop
      //   352: aload 7
      //   354: monitorexit
      //   355: goto -152 -> 203
      //   358: astore_1
      //   359: aload_0
      //   360: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   363: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   366: ldc -77
      //   368: aload_1
      //   369: invokestatic 182	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   372: pop
      //   373: aload_0
      //   374: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   377: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   380: iconst_1
      //   381: invokevirtual 147	android/hardware/camera2/legacy/CameraDeviceState:setError	(I)V
      //   384: goto -181 -> 203
      //   387: aload 7
      //   389: monitorexit
      //   390: aload_1
      //   391: ifnull +10 -> 401
      //   394: aload 8
      //   396: iconst_2
      //   397: invokevirtual 188	android/os/Handler:sendEmptyMessage	(I)Z
      //   400: pop
      //   401: aload_1
      //   402: getfield 193	android/util/Pair:first	Ljava/lang/Object;
      //   405: checkcast 195	android/hardware/camera2/legacy/BurstHolder
      //   408: aload_1
      //   409: getfield 198	android/util/Pair:second	Ljava/lang/Object;
      //   412: checkcast 200	java/lang/Long
      //   415: invokevirtual 204	java/lang/Long:longValue	()J
      //   418: invokevirtual 208	android/hardware/camera2/legacy/BurstHolder:produceRequestHolders	(J)Ljava/util/List;
      //   421: invokeinterface 214 1 0
      //   426: astore 7
      //   428: aload 7
      //   430: invokeinterface 219 1 0
      //   435: ifeq +254 -> 689
      //   438: aload 7
      //   440: invokeinterface 223 1 0
      //   445: checkcast 225	android/hardware/camera2/legacy/RequestHolder
      //   448: astore 8
      //   450: aload 8
      //   452: invokevirtual 229	android/hardware/camera2/legacy/RequestHolder:getRequest	()Landroid/hardware/camera2/CaptureRequest;
      //   455: astore 9
      //   457: iconst_0
      //   458: istore_2
      //   459: iconst_0
      //   460: istore 4
      //   462: aload_0
      //   463: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   466: invokestatic 233	android/hardware/camera2/legacy/RequestThreadManager:-get11	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/LegacyRequest;
      //   469: ifnull +18 -> 487
      //   472: aload_0
      //   473: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   476: invokestatic 233	android/hardware/camera2/legacy/RequestThreadManager:-get11	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/LegacyRequest;
      //   479: getfield 239	android/hardware/camera2/legacy/LegacyRequest:captureRequest	Landroid/hardware/camera2/CaptureRequest;
      //   482: aload 9
      //   484: if_acmpeq +111 -> 595
      //   487: aload_0
      //   488: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   491: invokestatic 243	android/hardware/camera2/legacy/RequestThreadManager:-get12	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/Camera$Parameters;
      //   494: invokevirtual 249	android/hardware/Camera$Parameters:getPreviewSize	()Landroid/hardware/Camera$Size;
      //   497: invokestatic 255	android/hardware/camera2/legacy/ParameterUtils:convertSize	(Landroid/hardware/Camera$Size;)Landroid/util/Size;
      //   500: astore 10
      //   502: new 235	android/hardware/camera2/legacy/LegacyRequest
      //   505: dup
      //   506: aload_0
      //   507: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   510: invokestatic 259	android/hardware/camera2/legacy/RequestThreadManager:-get4	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/CameraCharacteristics;
      //   513: aload 9
      //   515: aload 10
      //   517: aload_0
      //   518: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   521: invokestatic 243	android/hardware/camera2/legacy/RequestThreadManager:-get12	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/Camera$Parameters;
      //   524: invokespecial 262	android/hardware/camera2/legacy/LegacyRequest:<init>	(Landroid/hardware/camera2/CameraCharacteristics;Landroid/hardware/camera2/CaptureRequest;Landroid/util/Size;Landroid/hardware/Camera$Parameters;)V
      //   527: astore 10
      //   529: aload 10
      //   531: invokestatic 268	android/hardware/camera2/legacy/LegacyMetadataMapper:convertRequestMetadata	(Landroid/hardware/camera2/legacy/LegacyRequest;)V
      //   534: iload 4
      //   536: istore_2
      //   537: aload_0
      //   538: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   541: invokestatic 243	android/hardware/camera2/legacy/RequestThreadManager:-get12	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/Camera$Parameters;
      //   544: aload 10
      //   546: getfield 272	android/hardware/camera2/legacy/LegacyRequest:parameters	Landroid/hardware/Camera$Parameters;
      //   549: invokevirtual 276	android/hardware/Camera$Parameters:same	(Landroid/hardware/Camera$Parameters;)Z
      //   552: ifne +33 -> 585
      //   555: aload_0
      //   556: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   559: invokestatic 280	android/hardware/camera2/legacy/RequestThreadManager:-get2	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/Camera;
      //   562: aload 10
      //   564: getfield 272	android/hardware/camera2/legacy/LegacyRequest:parameters	Landroid/hardware/Camera$Parameters;
      //   567: invokevirtual 286	android/hardware/Camera:setParameters	(Landroid/hardware/Camera$Parameters;)V
      //   570: iconst_1
      //   571: istore_2
      //   572: aload_0
      //   573: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   576: aload 10
      //   578: getfield 272	android/hardware/camera2/legacy/LegacyRequest:parameters	Landroid/hardware/Camera$Parameters;
      //   581: invokestatic 290	android/hardware/camera2/legacy/RequestThreadManager:-set3	(Landroid/hardware/camera2/legacy/RequestThreadManager;Landroid/hardware/Camera$Parameters;)Landroid/hardware/Camera$Parameters;
      //   584: pop
      //   585: aload_0
      //   586: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   589: aload 10
      //   591: invokestatic 294	android/hardware/camera2/legacy/RequestThreadManager:-set2	(Landroid/hardware/camera2/legacy/RequestThreadManager;Landroid/hardware/camera2/legacy/LegacyRequest;)Landroid/hardware/camera2/legacy/LegacyRequest;
      //   594: pop
      //   595: aload_0
      //   596: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   599: invokestatic 100	android/hardware/camera2/legacy/RequestThreadManager:-get3	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CaptureCollector;
      //   602: aload 8
      //   604: aload_0
      //   605: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   608: invokestatic 233	android/hardware/camera2/legacy/RequestThreadManager:-get11	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/LegacyRequest;
      //   611: ldc2_w 101
      //   614: getstatic 108	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
      //   617: invokevirtual 298	android/hardware/camera2/legacy/CaptureCollector:queueRequest	(Landroid/hardware/camera2/legacy/RequestHolder;Landroid/hardware/camera2/legacy/LegacyRequest;JLjava/util/concurrent/TimeUnit;)Z
      //   620: ifne +167 -> 787
      //   623: aload_0
      //   624: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   627: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   630: ldc_w 300
      //   633: invokestatic 119	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   636: pop
      //   637: aload 8
      //   639: invokevirtual 303	android/hardware/camera2/legacy/RequestHolder:failRequest	()V
      //   642: aload_0
      //   643: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   646: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   649: aload 8
      //   651: lconst_0
      //   652: iconst_3
      //   653: invokevirtual 307	android/hardware/camera2/legacy/CameraDeviceState:setCaptureStart	(Landroid/hardware/camera2/legacy/RequestHolder;JI)Z
      //   656: pop
      //   657: goto -229 -> 428
      //   660: astore 7
      //   662: aload_0
      //   663: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   666: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   669: ldc_w 309
      //   672: aload 7
      //   674: invokestatic 182	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   677: pop
      //   678: aload_0
      //   679: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   682: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   685: iconst_1
      //   686: invokevirtual 147	android/hardware/camera2/legacy/CameraDeviceState:setError	(I)V
      //   689: iload_3
      //   690: ifeq -487 -> 203
      //   693: aload_1
      //   694: getfield 193	android/util/Pair:first	Ljava/lang/Object;
      //   697: checkcast 195	android/hardware/camera2/legacy/BurstHolder
      //   700: invokevirtual 312	android/hardware/camera2/legacy/BurstHolder:isRepeating	()Z
      //   703: ifeq -500 -> 203
      //   706: aload_0
      //   707: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   710: aload_1
      //   711: getfield 193	android/util/Pair:first	Ljava/lang/Object;
      //   714: checkcast 195	android/hardware/camera2/legacy/BurstHolder
      //   717: invokevirtual 315	android/hardware/camera2/legacy/BurstHolder:getRequestId	()I
      //   720: invokevirtual 319	android/hardware/camera2/legacy/RequestThreadManager:cancelRepeating	(I)J
      //   723: lstore 5
      //   725: aload_0
      //   726: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   729: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   732: lload 5
      //   734: invokevirtual 323	android/hardware/camera2/legacy/CameraDeviceState:setRepeatingRequestError	(J)V
      //   737: goto -534 -> 203
      //   740: astore_1
      //   741: aload 7
      //   743: monitorexit
      //   744: aload_1
      //   745: athrow
      //   746: astore 9
      //   748: aload_0
      //   749: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   752: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   755: ldc_w 325
      //   758: aload 9
      //   760: invokestatic 182	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   763: pop
      //   764: aload 8
      //   766: invokevirtual 303	android/hardware/camera2/legacy/RequestHolder:failRequest	()V
      //   769: aload_0
      //   770: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   773: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   776: aload 8
      //   778: lconst_0
      //   779: iconst_3
      //   780: invokevirtual 307	android/hardware/camera2/legacy/CameraDeviceState:setCaptureStart	(Landroid/hardware/camera2/legacy/RequestHolder;JI)Z
      //   783: pop
      //   784: goto -356 -> 428
      //   787: aload 8
      //   789: invokevirtual 328	android/hardware/camera2/legacy/RequestHolder:hasPreviewTargets	()Z
      //   792: ifeq +12 -> 804
      //   795: aload_0
      //   796: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   799: aload 8
      //   801: invokestatic 332	android/hardware/camera2/legacy/RequestThreadManager:-wrap3	(Landroid/hardware/camera2/legacy/RequestThreadManager;Landroid/hardware/camera2/legacy/RequestHolder;)V
      //   804: aload 8
      //   806: invokevirtual 335	android/hardware/camera2/legacy/RequestHolder:hasJpegTargets	()Z
      //   809: ifeq +100 -> 909
      //   812: aload_0
      //   813: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   816: invokestatic 100	android/hardware/camera2/legacy/RequestThreadManager:-get3	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CaptureCollector;
      //   819: ldc2_w 336
      //   822: getstatic 108	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
      //   825: invokevirtual 340	android/hardware/camera2/legacy/CaptureCollector:waitForPreviewsEmpty	(JLjava/util/concurrent/TimeUnit;)Z
      //   828: ifne +62 -> 890
      //   831: aload_0
      //   832: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   835: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   838: ldc_w 342
      //   841: invokestatic 119	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   844: pop
      //   845: aload_0
      //   846: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   849: invokestatic 100	android/hardware/camera2/legacy/RequestThreadManager:-get3	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CaptureCollector;
      //   852: invokevirtual 345	android/hardware/camera2/legacy/CaptureCollector:failNextPreview	()V
      //   855: goto -43 -> 812
      //   858: astore 7
      //   860: aload_0
      //   861: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   864: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   867: ldc_w 347
      //   870: aload 7
      //   872: invokestatic 182	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   875: pop
      //   876: aload_0
      //   877: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   880: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   883: iconst_1
      //   884: invokevirtual 147	android/hardware/camera2/legacy/CameraDeviceState:setError	(I)V
      //   887: goto -198 -> 689
      //   890: aload_0
      //   891: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   894: invokestatic 351	android/hardware/camera2/legacy/RequestThreadManager:-get13	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/os/ConditionVariable;
      //   897: invokevirtual 354	android/os/ConditionVariable:close	()V
      //   900: aload_0
      //   901: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   904: aload 8
      //   906: invokestatic 357	android/hardware/camera2/legacy/RequestThreadManager:-wrap1	(Landroid/hardware/camera2/legacy/RequestThreadManager;Landroid/hardware/camera2/legacy/RequestHolder;)V
      //   909: aload_0
      //   910: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   913: invokestatic 361	android/hardware/camera2/legacy/RequestThreadManager:-get6	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/LegacyFaceDetectMapper;
      //   916: aload 9
      //   918: aload_0
      //   919: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   922: invokestatic 243	android/hardware/camera2/legacy/RequestThreadManager:-get12	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/Camera$Parameters;
      //   925: invokevirtual 367	android/hardware/camera2/legacy/LegacyFaceDetectMapper:processFaceDetectMode	(Landroid/hardware/camera2/CaptureRequest;Landroid/hardware/Camera$Parameters;)V
      //   928: aload_0
      //   929: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   932: invokestatic 371	android/hardware/camera2/legacy/RequestThreadManager:-get7	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/LegacyFocusStateMapper;
      //   935: aload 9
      //   937: aload_0
      //   938: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   941: invokestatic 243	android/hardware/camera2/legacy/RequestThreadManager:-get12	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/Camera$Parameters;
      //   944: invokevirtual 376	android/hardware/camera2/legacy/LegacyFocusStateMapper:processRequestTriggers	(Landroid/hardware/camera2/CaptureRequest;Landroid/hardware/Camera$Parameters;)V
      //   947: aload 8
      //   949: invokevirtual 335	android/hardware/camera2/legacy/RequestHolder:hasJpegTargets	()Z
      //   952: ifeq +52 -> 1004
      //   955: aload_0
      //   956: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   959: aload 8
      //   961: invokestatic 379	android/hardware/camera2/legacy/RequestThreadManager:-wrap2	(Landroid/hardware/camera2/legacy/RequestThreadManager;Landroid/hardware/camera2/legacy/RequestHolder;)V
      //   964: aload_0
      //   965: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   968: invokestatic 351	android/hardware/camera2/legacy/RequestThreadManager:-get13	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/os/ConditionVariable;
      //   971: ldc2_w 101
      //   974: invokevirtual 383	android/os/ConditionVariable:block	(J)Z
      //   977: ifne +27 -> 1004
      //   980: aload_0
      //   981: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   984: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   987: ldc_w 385
      //   990: invokestatic 119	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   993: pop
      //   994: aload_0
      //   995: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   998: invokestatic 100	android/hardware/camera2/legacy/RequestThreadManager:-get3	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CaptureCollector;
      //   1001: invokevirtual 388	android/hardware/camera2/legacy/CaptureCollector:failNextJpeg	()V
      //   1004: iload_2
      //   1005: ifeq +38 -> 1043
      //   1008: aload_0
      //   1009: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1012: aload_0
      //   1013: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1016: invokestatic 280	android/hardware/camera2/legacy/RequestThreadManager:-get2	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/Camera;
      //   1019: invokevirtual 392	android/hardware/Camera:getParameters	()Landroid/hardware/Camera$Parameters;
      //   1022: invokestatic 290	android/hardware/camera2/legacy/RequestThreadManager:-set3	(Landroid/hardware/camera2/legacy/RequestThreadManager;Landroid/hardware/Camera$Parameters;)Landroid/hardware/Camera$Parameters;
      //   1025: pop
      //   1026: aload_0
      //   1027: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1030: invokestatic 233	android/hardware/camera2/legacy/RequestThreadManager:-get11	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/LegacyRequest;
      //   1033: aload_0
      //   1034: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1037: invokestatic 243	android/hardware/camera2/legacy/RequestThreadManager:-get12	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/Camera$Parameters;
      //   1040: invokevirtual 393	android/hardware/camera2/legacy/LegacyRequest:setParameters	(Landroid/hardware/Camera$Parameters;)V
      //   1043: new 395	android/util/MutableLong
      //   1046: dup
      //   1047: lconst_0
      //   1048: invokespecial 397	android/util/MutableLong:<init>	(J)V
      //   1051: astore 9
      //   1053: aload_0
      //   1054: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1057: invokestatic 100	android/hardware/camera2/legacy/RequestThreadManager:-get3	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CaptureCollector;
      //   1060: aload 8
      //   1062: ldc2_w 101
      //   1065: getstatic 108	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
      //   1068: aload 9
      //   1070: invokevirtual 401	android/hardware/camera2/legacy/CaptureCollector:waitForRequestCompleted	(Landroid/hardware/camera2/legacy/RequestHolder;JLjava/util/concurrent/TimeUnit;Landroid/util/MutableLong;)Z
      //   1073: ifne +27 -> 1100
      //   1076: aload_0
      //   1077: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1080: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   1083: ldc_w 403
      //   1086: invokestatic 119	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   1089: pop
      //   1090: aload_0
      //   1091: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1094: invokestatic 100	android/hardware/camera2/legacy/RequestThreadManager:-get3	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CaptureCollector;
      //   1097: invokevirtual 122	android/hardware/camera2/legacy/CaptureCollector:failAll	()V
      //   1100: aload_0
      //   1101: getfield 28	android/hardware/camera2/legacy/RequestThreadManager$5:mMapper	Landroid/hardware/camera2/legacy/LegacyResultMapper;
      //   1104: aload_0
      //   1105: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1108: invokestatic 233	android/hardware/camera2/legacy/RequestThreadManager:-get11	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/LegacyRequest;
      //   1111: aload 9
      //   1113: getfield 407	android/util/MutableLong:value	J
      //   1116: invokevirtual 411	android/hardware/camera2/legacy/LegacyResultMapper:cachedConvertResultMetadata	(Landroid/hardware/camera2/legacy/LegacyRequest;J)Landroid/hardware/camera2/impl/CameraMetadataNative;
      //   1119: astore 9
      //   1121: aload_0
      //   1122: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1125: invokestatic 371	android/hardware/camera2/legacy/RequestThreadManager:-get7	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/LegacyFocusStateMapper;
      //   1128: aload 9
      //   1130: invokevirtual 415	android/hardware/camera2/legacy/LegacyFocusStateMapper:mapResultTriggers	(Landroid/hardware/camera2/impl/CameraMetadataNative;)V
      //   1133: aload_0
      //   1134: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1137: invokestatic 361	android/hardware/camera2/legacy/RequestThreadManager:-get6	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/LegacyFaceDetectMapper;
      //   1140: aload 9
      //   1142: aload_0
      //   1143: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1146: invokestatic 233	android/hardware/camera2/legacy/RequestThreadManager:-get11	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/LegacyRequest;
      //   1149: invokevirtual 419	android/hardware/camera2/legacy/LegacyFaceDetectMapper:mapResultFaces	(Landroid/hardware/camera2/impl/CameraMetadataNative;Landroid/hardware/camera2/legacy/LegacyRequest;)V
      //   1152: aload 8
      //   1154: invokevirtual 422	android/hardware/camera2/legacy/RequestHolder:requestFailed	()Z
      //   1157: ifne +18 -> 1175
      //   1160: aload_0
      //   1161: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1164: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   1167: aload 8
      //   1169: aload 9
      //   1171: invokevirtual 426	android/hardware/camera2/legacy/CameraDeviceState:setCaptureResult	(Landroid/hardware/camera2/legacy/RequestHolder;Landroid/hardware/camera2/impl/CameraMetadataNative;)Z
      //   1174: pop
      //   1175: aload 8
      //   1177: invokevirtual 429	android/hardware/camera2/legacy/RequestHolder:isOutputAbandoned	()Z
      //   1180: ifeq -752 -> 428
      //   1183: iconst_1
      //   1184: istore_3
      //   1185: goto -757 -> 428
      //   1188: astore 7
      //   1190: aload_0
      //   1191: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1194: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   1197: ldc_w 309
      //   1200: aload 7
      //   1202: invokestatic 182	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1205: pop
      //   1206: aload_0
      //   1207: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1210: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   1213: iconst_1
      //   1214: invokevirtual 147	android/hardware/camera2/legacy/CameraDeviceState:setError	(I)V
      //   1217: goto -528 -> 689
      //   1220: astore 7
      //   1222: aload_0
      //   1223: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1226: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   1229: ldc_w 431
      //   1232: aload 7
      //   1234: invokestatic 182	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1237: pop
      //   1238: aload_0
      //   1239: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1242: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   1245: iconst_1
      //   1246: invokevirtual 147	android/hardware/camera2/legacy/CameraDeviceState:setError	(I)V
      //   1249: goto -560 -> 689
      //   1252: astore 7
      //   1254: aload_0
      //   1255: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1258: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   1261: ldc_w 433
      //   1264: aload 7
      //   1266: invokestatic 182	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1269: pop
      //   1270: aload_0
      //   1271: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1274: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   1277: iconst_1
      //   1278: invokevirtual 147	android/hardware/camera2/legacy/CameraDeviceState:setError	(I)V
      //   1281: goto -592 -> 689
      //   1284: aload_0
      //   1285: iconst_1
      //   1286: putfield 23	android/hardware/camera2/legacy/RequestThreadManager$5:mCleanup	Z
      //   1289: aload_0
      //   1290: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1293: invokestatic 100	android/hardware/camera2/legacy/RequestThreadManager:-get3	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CaptureCollector;
      //   1296: ldc2_w 101
      //   1299: getstatic 108	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
      //   1302: invokevirtual 114	android/hardware/camera2/legacy/CaptureCollector:waitForEmpty	(JLjava/util/concurrent/TimeUnit;)Z
      //   1305: ifne +27 -> 1332
      //   1308: aload_0
      //   1309: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1312: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   1315: ldc_w 435
      //   1318: invokestatic 119	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   1321: pop
      //   1322: aload_0
      //   1323: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1326: invokestatic 100	android/hardware/camera2/legacy/RequestThreadManager:-get3	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CaptureCollector;
      //   1329: invokevirtual 122	android/hardware/camera2/legacy/CaptureCollector:failAll	()V
      //   1332: aload_0
      //   1333: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1336: invokestatic 439	android/hardware/camera2/legacy/RequestThreadManager:-get8	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/GLThreadManager;
      //   1339: ifnull +22 -> 1361
      //   1342: aload_0
      //   1343: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1346: invokestatic 439	android/hardware/camera2/legacy/RequestThreadManager:-get8	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/GLThreadManager;
      //   1349: invokevirtual 444	android/hardware/camera2/legacy/GLThreadManager:quit	()V
      //   1352: aload_0
      //   1353: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1356: aconst_null
      //   1357: invokestatic 448	android/hardware/camera2/legacy/RequestThreadManager:-set1	(Landroid/hardware/camera2/legacy/RequestThreadManager;Landroid/hardware/camera2/legacy/GLThreadManager;)Landroid/hardware/camera2/legacy/GLThreadManager;
      //   1360: pop
      //   1361: aload_0
      //   1362: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1365: invokestatic 280	android/hardware/camera2/legacy/RequestThreadManager:-get2	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/Camera;
      //   1368: ifnull +22 -> 1390
      //   1371: aload_0
      //   1372: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1375: invokestatic 280	android/hardware/camera2/legacy/RequestThreadManager:-get2	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/Camera;
      //   1378: invokevirtual 451	android/hardware/Camera:release	()V
      //   1381: aload_0
      //   1382: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1385: aconst_null
      //   1386: invokestatic 455	android/hardware/camera2/legacy/RequestThreadManager:-set0	(Landroid/hardware/camera2/legacy/RequestThreadManager;Landroid/hardware/Camera;)Landroid/hardware/Camera;
      //   1389: pop
      //   1390: aload_0
      //   1391: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1394: aload_0
      //   1395: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1398: invokestatic 459	android/hardware/camera2/legacy/RequestThreadManager:-get1	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/util/List;
      //   1401: invokestatic 462	android/hardware/camera2/legacy/RequestThreadManager:-wrap4	(Landroid/hardware/camera2/legacy/RequestThreadManager;Ljava/util/Collection;)V
      //   1404: goto -1201 -> 203
      //   1407: astore_1
      //   1408: aload_0
      //   1409: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1412: invokestatic 86	android/hardware/camera2/legacy/RequestThreadManager:-get0	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Ljava/lang/String;
      //   1415: ldc -77
      //   1417: aload_1
      //   1418: invokestatic 182	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1421: pop
      //   1422: aload_0
      //   1423: getfield 18	android/hardware/camera2/legacy/RequestThreadManager$5:this$0	Landroid/hardware/camera2/legacy/RequestThreadManager;
      //   1426: invokestatic 141	android/hardware/camera2/legacy/RequestThreadManager:-get5	(Landroid/hardware/camera2/legacy/RequestThreadManager;)Landroid/hardware/camera2/legacy/CameraDeviceState;
      //   1429: iconst_1
      //   1430: invokevirtual 147	android/hardware/camera2/legacy/CameraDeviceState:setError	(I)V
      //   1433: goto -101 -> 1332
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	1436	0	this	5
      //   0	1436	1	paramAnonymousMessage	android.os.Message
      //   107	898	2	i	int
      //   251	934	3	j	int
      //   460	75	4	k	int
      //   723	10	5	l	long
      //   262	177	7	localObject1	Object
      //   660	82	7	localIOException	IOException
      //   858	13	7	localInterruptedException1	InterruptedException
      //   1188	13	7	localRuntimeException1	RuntimeException
      //   1220	13	7	localRuntimeException2	RuntimeException
      //   1252	13	7	localInterruptedException2	InterruptedException
      //   248	928	8	localObject2	Object
      //   455	59	9	localCaptureRequest	CaptureRequest
      //   746	190	9	localRuntimeException3	RuntimeException
      //   1051	119	9	localObject3	Object
      //   500	90	10	localObject4	Object
      // Exception table:
      //   from	to	target	type
      //   143	185	210	java/lang/InterruptedException
      //   272	314	358	java/lang/InterruptedException
      //   595	657	660	java/io/IOException
      //   787	804	660	java/io/IOException
      //   804	812	660	java/io/IOException
      //   812	855	660	java/io/IOException
      //   890	909	660	java/io/IOException
      //   909	1004	660	java/io/IOException
      //   326	337	740	finally
      //   341	352	740	finally
      //   555	570	746	java/lang/RuntimeException
      //   595	657	858	java/lang/InterruptedException
      //   787	804	858	java/lang/InterruptedException
      //   804	812	858	java/lang/InterruptedException
      //   812	855	858	java/lang/InterruptedException
      //   890	909	858	java/lang/InterruptedException
      //   909	1004	858	java/lang/InterruptedException
      //   595	657	1188	java/lang/RuntimeException
      //   787	804	1188	java/lang/RuntimeException
      //   804	812	1188	java/lang/RuntimeException
      //   812	855	1188	java/lang/RuntimeException
      //   890	909	1188	java/lang/RuntimeException
      //   909	1004	1188	java/lang/RuntimeException
      //   1008	1026	1220	java/lang/RuntimeException
      //   1053	1100	1252	java/lang/InterruptedException
      //   1289	1332	1407	java/lang/InterruptedException
    }
  };
  private final RequestQueue mRequestQueue = new RequestQueue(this.mJpegSurfaceIds);
  private final RequestHandlerThread mRequestThread;
  
  public RequestThreadManager(int paramInt, Camera paramCamera, CameraCharacteristics paramCameraCharacteristics, CameraDeviceState paramCameraDeviceState)
  {
    this.mCamera = ((Camera)Preconditions.checkNotNull(paramCamera, "camera must not be null"));
    this.mCameraId = paramInt;
    this.mCharacteristics = ((CameraCharacteristics)Preconditions.checkNotNull(paramCameraCharacteristics, "characteristics must not be null"));
    paramCamera = String.format("RequestThread-%d", new Object[] { Integer.valueOf(paramInt) });
    this.TAG = paramCamera;
    this.mDeviceState = ((CameraDeviceState)Preconditions.checkNotNull(paramCameraDeviceState, "deviceState must not be null"));
    this.mFocusStateMapper = new LegacyFocusStateMapper(this.mCamera);
    this.mFaceDetectMapper = new LegacyFaceDetectMapper(this.mCamera, this.mCharacteristics);
    this.mCaptureCollector = new CaptureCollector(2, this.mDeviceState);
    this.mRequestThread = new RequestHandlerThread(paramCamera, this.mRequestHandlerCb);
    this.mCamera.setErrorCallback(this.mErrorCallback);
  }
  
  private Size calculatePictureSize(List<Surface> paramList, List<Size> paramList1, Camera.Parameters paramParameters)
  {
    if (paramList.size() != paramList1.size()) {
      throw new IllegalStateException("Input collections must be same length");
    }
    Object localObject = new ArrayList();
    paramList1 = paramList1.iterator();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      Surface localSurface = (Surface)paramList.next();
      Size localSize = (Size)paramList1.next();
      if (LegacyCameraDevice.containsSurfaceId(localSurface, this.mJpegSurfaceIds)) {
        ((List)localObject).add(localSize);
      }
    }
    if (!((List)localObject).isEmpty())
    {
      int j = -1;
      int i = -1;
      paramList = ((Iterable)localObject).iterator();
      while (paramList.hasNext())
      {
        paramList1 = (Size)paramList.next();
        int k = j;
        if (paramList1.getWidth() > j) {
          k = paramList1.getWidth();
        }
        j = k;
        if (paramList1.getHeight() > i)
        {
          i = paramList1.getHeight();
          j = k;
        }
      }
      paramList = new Size(j, i);
      paramParameters = ParameterUtils.convertSizeList(paramParameters.getSupportedPictureSizes());
      paramList1 = new ArrayList();
      paramParameters = paramParameters.iterator();
      while (paramParameters.hasNext())
      {
        localObject = (Size)paramParameters.next();
        if ((((Size)localObject).getWidth() >= j) && (((Size)localObject).getHeight() >= i)) {
          paramList1.add(localObject);
        }
      }
      if (paramList1.isEmpty()) {
        throw new AssertionError("Could not find any supported JPEG sizes large enough to fit " + paramList);
      }
      paramList1 = (Size)Collections.min(paramList1, new SizeAreaComparator());
      if (!paramList1.equals(paramList)) {
        Log.w(this.TAG, String.format("configureOutputs - Will need to crop picture %s into smallest bound size %s", new Object[] { paramList1, paramList }));
      }
      return paramList1;
    }
    return null;
  }
  
  private static boolean checkAspectRatiosMatch(Size paramSize1, Size paramSize2)
  {
    return Math.abs(paramSize1.getWidth() / paramSize1.getHeight() - paramSize2.getWidth() / paramSize2.getHeight()) < 0.01F;
  }
  
  /* Error */
  private void configureOutputs(Collection<Pair<Surface, Size>> paramCollection)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 436	android/hardware/camera2/legacy/RequestThreadManager:stopPreview	()V
    //   4: aload_0
    //   5: getfield 135	android/hardware/camera2/legacy/RequestThreadManager:mCamera	Landroid/hardware/Camera;
    //   8: aconst_null
    //   9: invokevirtual 440	android/hardware/Camera:setPreviewTexture	(Landroid/graphics/SurfaceTexture;)V
    //   12: aload_0
    //   13: getfield 159	android/hardware/camera2/legacy/RequestThreadManager:mGLThreadManager	Landroid/hardware/camera2/legacy/GLThreadManager;
    //   16: ifnull +24 -> 40
    //   19: aload_0
    //   20: getfield 159	android/hardware/camera2/legacy/RequestThreadManager:mGLThreadManager	Landroid/hardware/camera2/legacy/GLThreadManager;
    //   23: invokevirtual 445	android/hardware/camera2/legacy/GLThreadManager:waitUntilStarted	()V
    //   26: aload_0
    //   27: getfield 159	android/hardware/camera2/legacy/RequestThreadManager:mGLThreadManager	Landroid/hardware/camera2/legacy/GLThreadManager;
    //   30: invokevirtual 448	android/hardware/camera2/legacy/GLThreadManager:ignoreNewFrames	()V
    //   33: aload_0
    //   34: getfield 159	android/hardware/camera2/legacy/RequestThreadManager:mGLThreadManager	Landroid/hardware/camera2/legacy/GLThreadManager;
    //   37: invokevirtual 451	android/hardware/camera2/legacy/GLThreadManager:waitUntilIdle	()V
    //   40: aload_0
    //   41: aload_0
    //   42: getfield 108	android/hardware/camera2/legacy/RequestThreadManager:mCallbackOutputs	Ljava/util/List;
    //   45: invokespecial 195	android/hardware/camera2/legacy/RequestThreadManager:resetJpegSurfaceFormats	(Ljava/util/Collection;)V
    //   48: aload_0
    //   49: getfield 108	android/hardware/camera2/legacy/RequestThreadManager:mCallbackOutputs	Ljava/util/List;
    //   52: invokeinterface 328 1 0
    //   57: astore 13
    //   59: aload 13
    //   61: invokeinterface 334 1 0
    //   66: ifeq +103 -> 169
    //   69: aload 13
    //   71: invokeinterface 338 1 0
    //   76: checkcast 340	android/view/Surface
    //   79: astore 14
    //   81: aload 14
    //   83: invokestatic 455	android/hardware/camera2/legacy/LegacyCameraDevice:disconnectSurface	(Landroid/view/Surface;)V
    //   86: goto -27 -> 59
    //   89: astore 14
    //   91: aload_0
    //   92: getfield 103	android/hardware/camera2/legacy/RequestThreadManager:TAG	Ljava/lang/String;
    //   95: ldc_w 457
    //   98: aload 14
    //   100: invokestatic 460	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   103: pop
    //   104: goto -45 -> 59
    //   107: astore_1
    //   108: aload_0
    //   109: getfield 103	android/hardware/camera2/legacy/RequestThreadManager:TAG	Ljava/lang/String;
    //   112: ldc_w 462
    //   115: aload_1
    //   116: invokestatic 465	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   119: pop
    //   120: aload_0
    //   121: getfield 147	android/hardware/camera2/legacy/RequestThreadManager:mDeviceState	Landroid/hardware/camera2/legacy/CameraDeviceState;
    //   124: iconst_1
    //   125: invokevirtual 469	android/hardware/camera2/legacy/CameraDeviceState:setError	(I)V
    //   128: return
    //   129: astore_1
    //   130: aload_0
    //   131: getfield 103	android/hardware/camera2/legacy/RequestThreadManager:TAG	Ljava/lang/String;
    //   134: ldc_w 462
    //   137: aload_1
    //   138: invokestatic 465	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   141: pop
    //   142: aload_0
    //   143: getfield 147	android/hardware/camera2/legacy/RequestThreadManager:mDeviceState	Landroid/hardware/camera2/legacy/CameraDeviceState;
    //   146: iconst_1
    //   147: invokevirtual 469	android/hardware/camera2/legacy/CameraDeviceState:setError	(I)V
    //   150: return
    //   151: astore 13
    //   153: aload_0
    //   154: getfield 103	android/hardware/camera2/legacy/RequestThreadManager:TAG	Ljava/lang/String;
    //   157: ldc_w 471
    //   160: aload 13
    //   162: invokestatic 460	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   165: pop
    //   166: goto -154 -> 12
    //   169: aload_0
    //   170: getfield 207	android/hardware/camera2/legacy/RequestThreadManager:mPreviewOutputs	Ljava/util/List;
    //   173: invokeinterface 474 1 0
    //   178: aload_0
    //   179: getfield 108	android/hardware/camera2/legacy/RequestThreadManager:mCallbackOutputs	Ljava/util/List;
    //   182: invokeinterface 474 1 0
    //   187: aload_0
    //   188: getfield 111	android/hardware/camera2/legacy/RequestThreadManager:mJpegSurfaceIds	Ljava/util/List;
    //   191: invokeinterface 474 1 0
    //   196: aload_0
    //   197: aconst_null
    //   198: putfield 476	android/hardware/camera2/legacy/RequestThreadManager:mPreviewTexture	Landroid/graphics/SurfaceTexture;
    //   201: new 204	java/util/ArrayList
    //   204: dup
    //   205: invokespecial 205	java/util/ArrayList:<init>	()V
    //   208: astore 16
    //   210: new 204	java/util/ArrayList
    //   213: dup
    //   214: invokespecial 205	java/util/ArrayList:<init>	()V
    //   217: astore 13
    //   219: aload_0
    //   220: getfield 143	android/hardware/camera2/legacy/RequestThreadManager:mCharacteristics	Landroid/hardware/camera2/CameraCharacteristics;
    //   223: getstatic 480	android/hardware/camera2/CameraCharacteristics:LENS_FACING	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   226: invokevirtual 484	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   229: checkcast 270	java/lang/Integer
    //   232: invokevirtual 487	java/lang/Integer:intValue	()I
    //   235: istore_2
    //   236: aload_0
    //   237: getfield 143	android/hardware/camera2/legacy/RequestThreadManager:mCharacteristics	Landroid/hardware/camera2/CameraCharacteristics;
    //   240: getstatic 490	android/hardware/camera2/CameraCharacteristics:SENSOR_ORIENTATION	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   243: invokevirtual 484	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   246: checkcast 270	java/lang/Integer
    //   249: invokevirtual 487	java/lang/Integer:intValue	()I
    //   252: istore_3
    //   253: aload_1
    //   254: ifnull +189 -> 443
    //   257: aload_1
    //   258: invokeinterface 328 1 0
    //   263: astore_1
    //   264: aload_1
    //   265: invokeinterface 334 1 0
    //   270: ifeq +173 -> 443
    //   273: aload_1
    //   274: invokeinterface 338 1 0
    //   279: checkcast 492	android/util/Pair
    //   282: astore 15
    //   284: aload 15
    //   286: getfield 495	android/util/Pair:first	Ljava/lang/Object;
    //   289: checkcast 340	android/view/Surface
    //   292: astore 14
    //   294: aload 15
    //   296: getfield 498	android/util/Pair:second	Ljava/lang/Object;
    //   299: checkcast 342	android/util/Size
    //   302: astore 15
    //   304: aload 14
    //   306: invokestatic 502	android/hardware/camera2/legacy/LegacyCameraDevice:detectSurfaceType	(Landroid/view/Surface;)I
    //   309: istore 4
    //   311: aload 14
    //   313: iload_2
    //   314: iload_3
    //   315: invokestatic 506	android/hardware/camera2/legacy/LegacyCameraDevice:setSurfaceOrientation	(Landroid/view/Surface;II)V
    //   318: iload 4
    //   320: tableswitch	default:+646->966, 33:+69->389
    //   340: aload 14
    //   342: iconst_1
    //   343: invokestatic 510	android/hardware/camera2/legacy/LegacyCameraDevice:setScalingMode	(Landroid/view/Surface;I)V
    //   346: aload_0
    //   347: getfield 207	android/hardware/camera2/legacy/RequestThreadManager:mPreviewOutputs	Ljava/util/List;
    //   350: aload 14
    //   352: invokeinterface 352 2 0
    //   357: pop
    //   358: aload 16
    //   360: aload 15
    //   362: invokeinterface 352 2 0
    //   367: pop
    //   368: goto -104 -> 264
    //   371: astore 14
    //   373: aload_0
    //   374: getfield 103	android/hardware/camera2/legacy/RequestThreadManager:TAG	Ljava/lang/String;
    //   377: ldc_w 457
    //   380: aload 14
    //   382: invokestatic 460	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   385: pop
    //   386: goto -122 -> 264
    //   389: aload 14
    //   391: iconst_1
    //   392: invokestatic 513	android/hardware/camera2/legacy/LegacyCameraDevice:setSurfaceFormat	(Landroid/view/Surface;I)V
    //   395: aload_0
    //   396: getfield 111	android/hardware/camera2/legacy/RequestThreadManager:mJpegSurfaceIds	Ljava/util/List;
    //   399: aload 14
    //   401: invokestatic 517	android/hardware/camera2/legacy/LegacyCameraDevice:getSurfaceId	(Landroid/view/Surface;)J
    //   404: invokestatic 522	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   407: invokeinterface 352 2 0
    //   412: pop
    //   413: aload_0
    //   414: getfield 108	android/hardware/camera2/legacy/RequestThreadManager:mCallbackOutputs	Ljava/util/List;
    //   417: aload 14
    //   419: invokeinterface 352 2 0
    //   424: pop
    //   425: aload 13
    //   427: aload 15
    //   429: invokeinterface 352 2 0
    //   434: pop
    //   435: aload 14
    //   437: invokestatic 525	android/hardware/camera2/legacy/LegacyCameraDevice:connectSurface	(Landroid/view/Surface;)V
    //   440: goto -176 -> 264
    //   443: aload_0
    //   444: aload_0
    //   445: getfield 135	android/hardware/camera2/legacy/RequestThreadManager:mCamera	Landroid/hardware/Camera;
    //   448: invokevirtual 529	android/hardware/Camera:getParameters	()Landroid/hardware/Camera$Parameters;
    //   451: putfield 119	android/hardware/camera2/legacy/RequestThreadManager:mParams	Landroid/hardware/Camera$Parameters;
    //   454: aload_0
    //   455: aload_0
    //   456: getfield 119	android/hardware/camera2/legacy/RequestThreadManager:mParams	Landroid/hardware/Camera$Parameters;
    //   459: invokevirtual 532	android/hardware/Camera$Parameters:getSupportedPreviewFpsRange	()Ljava/util/List;
    //   462: invokespecial 536	android/hardware/camera2/legacy/RequestThreadManager:getPhotoPreviewFpsRange	(Ljava/util/List;)[I
    //   465: astore_1
    //   466: aload_0
    //   467: getfield 119	android/hardware/camera2/legacy/RequestThreadManager:mParams	Landroid/hardware/Camera$Parameters;
    //   470: aload_1
    //   471: iconst_0
    //   472: iaload
    //   473: aload_1
    //   474: iconst_1
    //   475: iaload
    //   476: invokevirtual 539	android/hardware/Camera$Parameters:setPreviewFpsRange	(II)V
    //   479: aload_0
    //   480: aload_0
    //   481: getfield 108	android/hardware/camera2/legacy/RequestThreadManager:mCallbackOutputs	Ljava/util/List;
    //   484: aload 13
    //   486: aload_0
    //   487: getfield 119	android/hardware/camera2/legacy/RequestThreadManager:mParams	Landroid/hardware/Camera$Parameters;
    //   490: invokespecial 541	android/hardware/camera2/legacy/RequestThreadManager:calculatePictureSize	(Ljava/util/List;Ljava/util/List;Landroid/hardware/Camera$Parameters;)Landroid/util/Size;
    //   493: astore 14
    //   495: aload 16
    //   497: invokeinterface 316 1 0
    //   502: ifle +374 -> 876
    //   505: aload 16
    //   507: invokestatic 545	android/hardware/camera2/utils/SizeAreaComparator:findLargestByArea	(Ljava/util/List;)Landroid/util/Size;
    //   510: astore 13
    //   512: aload_0
    //   513: getfield 119	android/hardware/camera2/legacy/RequestThreadManager:mParams	Landroid/hardware/Camera$Parameters;
    //   516: invokestatic 549	android/hardware/camera2/legacy/ParameterUtils:getLargestSupportedJpegSizeByArea	(Landroid/hardware/Camera$Parameters;)Landroid/util/Size;
    //   519: astore_1
    //   520: aload 14
    //   522: ifnull +157 -> 679
    //   525: aload 14
    //   527: astore_1
    //   528: aload_0
    //   529: getfield 119	android/hardware/camera2/legacy/RequestThreadManager:mParams	Landroid/hardware/Camera$Parameters;
    //   532: invokevirtual 552	android/hardware/Camera$Parameters:getSupportedPreviewSizes	()Ljava/util/List;
    //   535: invokestatic 376	android/hardware/camera2/legacy/ParameterUtils:convertSizeList	(Ljava/util/List;)Ljava/util/List;
    //   538: astore 15
    //   540: aload 13
    //   542: invokevirtual 361	android/util/Size:getHeight	()I
    //   545: i2l
    //   546: lstore 5
    //   548: aload 13
    //   550: invokevirtual 358	android/util/Size:getWidth	()I
    //   553: i2l
    //   554: lstore 7
    //   556: aload 15
    //   558: invokestatic 545	android/hardware/camera2/utils/SizeAreaComparator:findLargestByArea	(Ljava/util/List;)Landroid/util/Size;
    //   561: astore 13
    //   563: aload 15
    //   565: invokeinterface 328 1 0
    //   570: astore 17
    //   572: aload 17
    //   574: invokeinterface 334 1 0
    //   579: ifeq +103 -> 682
    //   582: aload 17
    //   584: invokeinterface 338 1 0
    //   589: checkcast 342	android/util/Size
    //   592: astore 15
    //   594: aload 15
    //   596: invokevirtual 358	android/util/Size:getWidth	()I
    //   599: aload 15
    //   601: invokevirtual 361	android/util/Size:getHeight	()I
    //   604: imul
    //   605: i2l
    //   606: lstore 9
    //   608: aload 13
    //   610: invokevirtual 358	android/util/Size:getWidth	()I
    //   613: aload 13
    //   615: invokevirtual 361	android/util/Size:getHeight	()I
    //   618: imul
    //   619: i2l
    //   620: lstore 11
    //   622: aload_1
    //   623: aload 15
    //   625: invokestatic 554	android/hardware/camera2/legacy/RequestThreadManager:checkAspectRatiosMatch	(Landroid/util/Size;Landroid/util/Size;)Z
    //   628: ifeq -56 -> 572
    //   631: lload 9
    //   633: lload 11
    //   635: lcmp
    //   636: ifge -64 -> 572
    //   639: lload 9
    //   641: lload 5
    //   643: lload 7
    //   645: lmul
    //   646: lcmp
    //   647: iflt -75 -> 572
    //   650: aload 15
    //   652: astore 13
    //   654: goto -82 -> 572
    //   657: astore_1
    //   658: aload_0
    //   659: getfield 103	android/hardware/camera2/legacy/RequestThreadManager:TAG	Ljava/lang/String;
    //   662: ldc_w 556
    //   665: aload_1
    //   666: invokestatic 465	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   669: pop
    //   670: aload_0
    //   671: getfield 147	android/hardware/camera2/legacy/RequestThreadManager:mDeviceState	Landroid/hardware/camera2/legacy/CameraDeviceState;
    //   674: iconst_1
    //   675: invokevirtual 469	android/hardware/camera2/legacy/CameraDeviceState:setError	(I)V
    //   678: return
    //   679: goto -151 -> 528
    //   682: aload_0
    //   683: aload 13
    //   685: putfield 558	android/hardware/camera2/legacy/RequestThreadManager:mIntermediateBufferSize	Landroid/util/Size;
    //   688: aload_0
    //   689: getfield 119	android/hardware/camera2/legacy/RequestThreadManager:mParams	Landroid/hardware/Camera$Parameters;
    //   692: aload_0
    //   693: getfield 558	android/hardware/camera2/legacy/RequestThreadManager:mIntermediateBufferSize	Landroid/util/Size;
    //   696: invokevirtual 358	android/util/Size:getWidth	()I
    //   699: aload_0
    //   700: getfield 558	android/hardware/camera2/legacy/RequestThreadManager:mIntermediateBufferSize	Landroid/util/Size;
    //   703: invokevirtual 361	android/util/Size:getHeight	()I
    //   706: invokevirtual 561	android/hardware/Camera$Parameters:setPreviewSize	(II)V
    //   709: aload 14
    //   711: ifnull +49 -> 760
    //   714: aload_0
    //   715: getfield 103	android/hardware/camera2/legacy/RequestThreadManager:TAG	Ljava/lang/String;
    //   718: new 380	java/lang/StringBuilder
    //   721: dup
    //   722: invokespecial 381	java/lang/StringBuilder:<init>	()V
    //   725: ldc_w 563
    //   728: invokevirtual 387	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   731: aload 14
    //   733: invokevirtual 390	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   736: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   739: invokestatic 566	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   742: pop
    //   743: aload_0
    //   744: getfield 119	android/hardware/camera2/legacy/RequestThreadManager:mParams	Landroid/hardware/Camera$Parameters;
    //   747: aload 14
    //   749: invokevirtual 358	android/util/Size:getWidth	()I
    //   752: aload 14
    //   754: invokevirtual 361	android/util/Size:getHeight	()I
    //   757: invokevirtual 569	android/hardware/Camera$Parameters:setPictureSize	(II)V
    //   760: aload_0
    //   761: getfield 159	android/hardware/camera2/legacy/RequestThreadManager:mGLThreadManager	Landroid/hardware/camera2/legacy/GLThreadManager;
    //   764: ifnonnull +30 -> 794
    //   767: aload_0
    //   768: new 442	android/hardware/camera2/legacy/GLThreadManager
    //   771: dup
    //   772: aload_0
    //   773: getfield 262	android/hardware/camera2/legacy/RequestThreadManager:mCameraId	I
    //   776: iload_2
    //   777: aload_0
    //   778: getfield 147	android/hardware/camera2/legacy/RequestThreadManager:mDeviceState	Landroid/hardware/camera2/legacy/CameraDeviceState;
    //   781: invokespecial 572	android/hardware/camera2/legacy/GLThreadManager:<init>	(IILandroid/hardware/camera2/legacy/CameraDeviceState;)V
    //   784: putfield 159	android/hardware/camera2/legacy/RequestThreadManager:mGLThreadManager	Landroid/hardware/camera2/legacy/GLThreadManager;
    //   787: aload_0
    //   788: getfield 159	android/hardware/camera2/legacy/RequestThreadManager:mGLThreadManager	Landroid/hardware/camera2/legacy/GLThreadManager;
    //   791: invokevirtual 575	android/hardware/camera2/legacy/GLThreadManager:start	()V
    //   794: aload_0
    //   795: getfield 159	android/hardware/camera2/legacy/RequestThreadManager:mGLThreadManager	Landroid/hardware/camera2/legacy/GLThreadManager;
    //   798: invokevirtual 445	android/hardware/camera2/legacy/GLThreadManager:waitUntilStarted	()V
    //   801: new 204	java/util/ArrayList
    //   804: dup
    //   805: invokespecial 205	java/util/ArrayList:<init>	()V
    //   808: astore_1
    //   809: aload 16
    //   811: invokeinterface 325 1 0
    //   816: astore 13
    //   818: aload_0
    //   819: getfield 207	android/hardware/camera2/legacy/RequestThreadManager:mPreviewOutputs	Ljava/util/List;
    //   822: invokeinterface 328 1 0
    //   827: astore 14
    //   829: aload 14
    //   831: invokeinterface 334 1 0
    //   836: ifeq +48 -> 884
    //   839: aload_1
    //   840: new 492	android/util/Pair
    //   843: dup
    //   844: aload 14
    //   846: invokeinterface 338 1 0
    //   851: checkcast 340	android/view/Surface
    //   854: aload 13
    //   856: invokeinterface 338 1 0
    //   861: checkcast 342	android/util/Size
    //   864: invokespecial 578	android/util/Pair:<init>	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   867: invokeinterface 352 2 0
    //   872: pop
    //   873: goto -44 -> 829
    //   876: aload_0
    //   877: aconst_null
    //   878: putfield 558	android/hardware/camera2/legacy/RequestThreadManager:mIntermediateBufferSize	Landroid/util/Size;
    //   881: goto -172 -> 709
    //   884: aload_0
    //   885: getfield 159	android/hardware/camera2/legacy/RequestThreadManager:mGLThreadManager	Landroid/hardware/camera2/legacy/GLThreadManager;
    //   888: aload_1
    //   889: aload_0
    //   890: getfield 139	android/hardware/camera2/legacy/RequestThreadManager:mCaptureCollector	Landroid/hardware/camera2/legacy/CaptureCollector;
    //   893: invokevirtual 582	android/hardware/camera2/legacy/GLThreadManager:setConfigurationAndWait	(Ljava/util/Collection;Landroid/hardware/camera2/legacy/CaptureCollector;)V
    //   896: aload_0
    //   897: getfield 159	android/hardware/camera2/legacy/RequestThreadManager:mGLThreadManager	Landroid/hardware/camera2/legacy/GLThreadManager;
    //   900: invokevirtual 585	android/hardware/camera2/legacy/GLThreadManager:allowNewFrames	()V
    //   903: aload_0
    //   904: aload_0
    //   905: getfield 159	android/hardware/camera2/legacy/RequestThreadManager:mGLThreadManager	Landroid/hardware/camera2/legacy/GLThreadManager;
    //   908: invokevirtual 589	android/hardware/camera2/legacy/GLThreadManager:getCurrentSurfaceTexture	()Landroid/graphics/SurfaceTexture;
    //   911: putfield 476	android/hardware/camera2/legacy/RequestThreadManager:mPreviewTexture	Landroid/graphics/SurfaceTexture;
    //   914: aload_0
    //   915: getfield 476	android/hardware/camera2/legacy/RequestThreadManager:mPreviewTexture	Landroid/graphics/SurfaceTexture;
    //   918: ifnull +14 -> 932
    //   921: aload_0
    //   922: getfield 476	android/hardware/camera2/legacy/RequestThreadManager:mPreviewTexture	Landroid/graphics/SurfaceTexture;
    //   925: aload_0
    //   926: getfield 247	android/hardware/camera2/legacy/RequestThreadManager:mPreviewCallback	Landroid/graphics/SurfaceTexture$OnFrameAvailableListener;
    //   929: invokevirtual 595	android/graphics/SurfaceTexture:setOnFrameAvailableListener	(Landroid/graphics/SurfaceTexture$OnFrameAvailableListener;)V
    //   932: aload_0
    //   933: getfield 135	android/hardware/camera2/legacy/RequestThreadManager:mCamera	Landroid/hardware/Camera;
    //   936: aload_0
    //   937: getfield 119	android/hardware/camera2/legacy/RequestThreadManager:mParams	Landroid/hardware/Camera$Parameters;
    //   940: invokevirtual 599	android/hardware/Camera:setParameters	(Landroid/hardware/Camera$Parameters;)V
    //   943: return
    //   944: astore_1
    //   945: aload_0
    //   946: getfield 103	android/hardware/camera2/legacy/RequestThreadManager:TAG	Ljava/lang/String;
    //   949: ldc_w 601
    //   952: aload_1
    //   953: invokestatic 465	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   956: pop
    //   957: aload_0
    //   958: getfield 147	android/hardware/camera2/legacy/RequestThreadManager:mDeviceState	Landroid/hardware/camera2/legacy/CameraDeviceState;
    //   961: iconst_1
    //   962: invokevirtual 469	android/hardware/camera2/legacy/CameraDeviceState:setError	(I)V
    //   965: return
    //   966: goto -626 -> 340
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	969	0	this	RequestThreadManager
    //   0	969	1	paramCollection	Collection<Pair<Surface, Size>>
    //   235	542	2	i	int
    //   252	63	3	j	int
    //   309	10	4	k	int
    //   546	96	5	l1	long
    //   554	90	7	l2	long
    //   606	34	9	l3	long
    //   620	14	11	l4	long
    //   57	13	13	localIterator1	Iterator
    //   151	10	13	localIOException	IOException
    //   217	638	13	localObject1	Object
    //   79	3	14	localSurface1	Surface
    //   89	10	14	localBufferQueueAbandonedException1	LegacyExceptionUtils.BufferQueueAbandonedException
    //   292	59	14	localSurface2	Surface
    //   371	65	14	localBufferQueueAbandonedException2	LegacyExceptionUtils.BufferQueueAbandonedException
    //   493	352	14	localObject2	Object
    //   282	369	15	localObject3	Object
    //   208	602	16	localArrayList	ArrayList
    //   570	13	17	localIterator2	Iterator
    // Exception table:
    //   from	to	target	type
    //   81	86	89	android/hardware/camera2/legacy/LegacyExceptionUtils$BufferQueueAbandonedException
    //   0	4	107	java/lang/RuntimeException
    //   4	12	129	java/lang/RuntimeException
    //   4	12	151	java/io/IOException
    //   304	318	371	android/hardware/camera2/legacy/LegacyExceptionUtils$BufferQueueAbandonedException
    //   340	368	371	android/hardware/camera2/legacy/LegacyExceptionUtils$BufferQueueAbandonedException
    //   389	440	371	android/hardware/camera2/legacy/LegacyExceptionUtils$BufferQueueAbandonedException
    //   443	454	657	java/lang/RuntimeException
    //   932	943	944	java/lang/RuntimeException
  }
  
  private void createDummySurface()
  {
    if ((this.mDummyTexture == null) || (this.mDummySurface == null))
    {
      this.mDummyTexture = new SurfaceTexture(0);
      this.mDummyTexture.setDefaultBufferSize(640, 480);
      this.mDummySurface = new Surface(this.mDummyTexture);
    }
  }
  
  private void doJpegCapture(RequestHolder paramRequestHolder)
  {
    this.mCamera.takePicture(this.mJpegShutterCallback, null, this.mJpegCallback);
    this.mPreviewRunning = false;
  }
  
  private void doJpegCapturePrepare(RequestHolder paramRequestHolder)
    throws IOException
  {
    if (!this.mPreviewRunning)
    {
      createDummySurface();
      this.mCamera.setPreviewTexture(this.mDummyTexture);
      startPreview();
    }
  }
  
  private void doPreviewCapture(RequestHolder paramRequestHolder)
    throws IOException
  {
    if (this.mPreviewRunning) {
      return;
    }
    if (this.mPreviewTexture == null) {
      throw new IllegalStateException("Preview capture called with no preview surfaces configured.");
    }
    this.mPreviewTexture.setDefaultBufferSize(this.mIntermediateBufferSize.getWidth(), this.mIntermediateBufferSize.getHeight());
    this.mCamera.setPreviewTexture(this.mPreviewTexture);
    startPreview();
  }
  
  private int[] getPhotoPreviewFpsRange(List<int[]> paramList)
  {
    if (paramList.size() == 0)
    {
      Log.e(this.TAG, "No supported frame rates returned!");
      return null;
    }
    int j = 0;
    int m = 0;
    int i1 = 0;
    int i = 0;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      int[] arrayOfInt = (int[])localIterator.next();
      int i4 = arrayOfInt[0];
      int i3 = arrayOfInt[1];
      int i2;
      int n;
      int k;
      if (i3 <= m)
      {
        i2 = i1;
        n = m;
        k = j;
        if (i3 == m)
        {
          i2 = i1;
          n = m;
          k = j;
          if (i4 <= j) {}
        }
      }
      else
      {
        k = i4;
        n = i3;
        i2 = i;
      }
      i += 1;
      i1 = i2;
      m = n;
      j = k;
    }
    return (int[])paramList.get(i1);
  }
  
  private void resetJpegSurfaceFormats(Collection<Surface> paramCollection)
  {
    if (paramCollection == null) {
      return;
    }
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      Surface localSurface = (Surface)paramCollection.next();
      if ((localSurface != null) && (localSurface.isValid())) {
        try
        {
          LegacyCameraDevice.setSurfaceFormat(localSurface, 33);
        }
        catch (LegacyExceptionUtils.BufferQueueAbandonedException localBufferQueueAbandonedException)
        {
          Log.w(this.TAG, "Surface abandoned, skipping...", localBufferQueueAbandonedException);
        }
      } else {
        Log.w(this.TAG, "Jpeg surface is invalid, skipping...");
      }
    }
  }
  
  private void startPreview()
  {
    if (!this.mPreviewRunning)
    {
      this.mCamera.startPreview();
      this.mPreviewRunning = true;
    }
  }
  
  private void stopPreview()
  {
    if (this.mPreviewRunning)
    {
      this.mCamera.stopPreview();
      this.mPreviewRunning = false;
    }
  }
  
  public long cancelRepeating(int paramInt)
  {
    return this.mRequestQueue.stopRepeating(paramInt);
  }
  
  public void configure(Collection<Pair<Surface, Size>> paramCollection)
  {
    Handler localHandler = this.mRequestThread.waitAndGetHandler();
    ConditionVariable localConditionVariable = new ConditionVariable(false);
    localHandler.sendMessage(localHandler.obtainMessage(1, 0, 0, new ConfigureHolder(localConditionVariable, paramCollection)));
    localConditionVariable.block();
  }
  
  public long flush()
  {
    Log.i(this.TAG, "Flushing all pending requests.");
    long l = this.mRequestQueue.stopRepeating();
    this.mCaptureCollector.failAll();
    return l;
  }
  
  public void quit()
  {
    if (!this.mQuit.getAndSet(true))
    {
      Handler localHandler = this.mRequestThread.waitAndGetHandler();
      localHandler.sendMessageAtFrontOfQueue(localHandler.obtainMessage(3));
      this.mRequestThread.quitSafely();
    }
    try
    {
      this.mRequestThread.join();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      Log.e(this.TAG, String.format("Thread %s (%d) interrupted while quitting.", new Object[] { this.mRequestThread.getName(), Long.valueOf(this.mRequestThread.getId()) }));
    }
  }
  
  public void start()
  {
    this.mRequestThread.start();
  }
  
  public SubmitInfo submitCaptureRequests(CaptureRequest[] paramArrayOfCaptureRequest, boolean paramBoolean)
  {
    Handler localHandler = this.mRequestThread.waitAndGetHandler();
    synchronized (this.mIdleLock)
    {
      paramArrayOfCaptureRequest = this.mRequestQueue.submit(paramArrayOfCaptureRequest, paramBoolean);
      localHandler.sendEmptyMessage(2);
      return paramArrayOfCaptureRequest;
    }
  }
  
  private static class ConfigureHolder
  {
    public final ConditionVariable condition;
    public final Collection<Pair<Surface, Size>> surfaces;
    
    public ConfigureHolder(ConditionVariable paramConditionVariable, Collection<Pair<Surface, Size>> paramCollection)
    {
      this.condition = paramConditionVariable;
      this.surfaces = paramCollection;
    }
  }
  
  public static class FpsCounter
  {
    private static final long NANO_PER_SECOND = 1000000000L;
    private static final String TAG = "FpsCounter";
    private int mFrameCount = 0;
    private double mLastFps = 0.0D;
    private long mLastPrintTime = 0L;
    private long mLastTime = 0L;
    private final String mStreamType;
    
    public FpsCounter(String paramString)
    {
      this.mStreamType = paramString;
    }
    
    public double checkFps()
    {
      try
      {
        double d = this.mLastFps;
        return d;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void countAndLog()
    {
      try
      {
        countFrame();
        staggeredLog();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void countFrame()
    {
      try
      {
        this.mFrameCount += 1;
        long l1 = SystemClock.elapsedRealtimeNanos();
        if (this.mLastTime == 0L) {
          this.mLastTime = l1;
        }
        if (l1 > this.mLastTime + 1000000000L)
        {
          long l2 = this.mLastTime;
          this.mLastFps = (this.mFrameCount * (1.0E9D / (l1 - l2)));
          this.mFrameCount = 0;
          this.mLastTime = l1;
        }
        return;
      }
      finally {}
    }
    
    public void staggeredLog()
    {
      try
      {
        if (this.mLastTime > this.mLastPrintTime + 5000000000L)
        {
          this.mLastPrintTime = this.mLastTime;
          Log.d("FpsCounter", "FPS for " + this.mStreamType + " stream: " + this.mLastFps);
        }
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/RequestThreadManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */