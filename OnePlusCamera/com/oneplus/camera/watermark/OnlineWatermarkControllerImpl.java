package com.oneplus.camera.watermark;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Message;
import android.text.TextUtils;
import android.util.Size;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.CaptureEventArgs;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class OnlineWatermarkControllerImpl
  extends CameraComponent
  implements OnlineWatermarkController
{
  private static final long DELAY_UPDATE_WATERMARK = 200L;
  private static final boolean ENBALE_WATERMARK_BOUNDS = true;
  private static final int MSG_APPLY_PARAMETERS = 10005;
  private static final int MSG_ENTER = 10001;
  private static final int MSG_EXIT = 10002;
  private static final int MSG_SET_WATERMARK = 10010;
  private static final int MSG_UPDATE_WATERMARK = 10015;
  private static final Executor PROCESS_WATERMARK_EXECUTOR = Executors.newFixedThreadPool(1);
  private static final boolean SAVE_ARGB_WATERMARKS = false;
  private static final boolean SAVE_NV21_WATERMARKS = true;
  private static final boolean SAVE_PNG_WATERMARKS = false;
  private static final String SUFFIX_NV21A = "_nv21a";
  private static final String WATERMARK_FILE_TEMPLATE = "watermark_v%1$d_%2$d_%3$d_%4$d";
  private static final Object WATERMARK_PROCESSING_LOCK = new Object();
  private static final int WATERMARK_VERSION = 1;
  private final PropertyChangedCallback<List<Size>> m_AlternativePictureSizeChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Size>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Size>> paramAnonymousPropertyChangeEventArgs)
    {
      OnlineWatermarkControllerImpl.-wrap2(OnlineWatermarkControllerImpl.this, 200L);
    }
  };
  private File m_CachePath;
  private ProcessWatermarkTask m_CurrentTask;
  private boolean m_DeleteOldFiles;
  private boolean m_IsBokeh;
  private boolean m_IsEnter;
  private Boolean m_IsSupported;
  private final PropertyChangedCallback<Size> m_PictureSizeChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Size> paramAnonymousPropertyKey, PropertyChangeEventArgs<Size> paramAnonymousPropertyChangeEventArgs)
    {
      OnlineWatermarkControllerImpl.-wrap2(OnlineWatermarkControllerImpl.this, 200L);
    }
  };
  private volatile ProcessWatermarkTask m_ProcessedTask;
  private String m_SloganAuthorText;
  private SloganWatermarkDrawable m_SloganWatermarkDrawable;
  private Watermark m_Watermark = Watermark.NONE;
  private Map<Size, Rect> m_WatermarkBoundsTable = new HashMap();
  private Map<Size, String> m_WatermarkFilePathsTable = new HashMap();
  
  OnlineWatermarkControllerImpl(CameraThread paramCameraThread)
  {
    super("Online Watermark Controller", paramCameraThread, true);
  }
  
  private void applyParameters()
  {
    if (!this.m_IsSupported.booleanValue()) {
      return;
    }
    if (!this.m_IsEnter) {
      return;
    }
    HandlerUtils.removeMessages(this, 10005);
    if (this.m_Watermark != Watermark.NONE) {}
    for (boolean bool = true;; bool = false)
    {
      Log.v(this.TAG, "applyParameters() - Enabled : ", Boolean.valueOf(bool), ", files : ", this.m_WatermarkFilePathsTable, ", bounds : ", this.m_WatermarkBoundsTable);
      applyParameters(this.m_WatermarkFilePathsTable, this.m_WatermarkBoundsTable);
      return;
    }
  }
  
  private void applyParameters(Map<Size, String> paramMap, Map<Size, Rect> paramMap1)
  {
    Camera localCamera = (Camera)getCameraThread().get(CameraThread.PROP_CAMERA);
    if (localCamera == null) {
      return;
    }
    localCamera.set(Camera.PROP_WATERMARK_BOUNDS_MAP, paramMap1);
    localCamera.set(Camera.PROP_WATERMARK_FILE_PATHS_MAP, paramMap);
  }
  
  private void deleteWatermarkFiles()
  {
    if (!this.m_IsEnter)
    {
      this.m_DeleteOldFiles = true;
      return;
    }
    if (!isCachePathAvailable())
    {
      Log.e(this.TAG, "deleteWatermarkFiles() - Failed to delete files, cache path is null.");
      this.m_DeleteOldFiles = true;
      return;
    }
    if (this.m_CachePath.isDirectory())
    {
      File[] arrayOfFile = this.m_CachePath.listFiles();
      if ((arrayOfFile != null) && (arrayOfFile.length > 0))
      {
        int j = arrayOfFile.length;
        int i = 0;
        while (i < j)
        {
          File localFile = arrayOfFile[i];
          Log.v(this.TAG, "deleteWatermarkFiles() - File : ", localFile.getAbsolutePath());
          localFile.delete();
          i += 1;
        }
      }
    }
    this.m_DeleteOldFiles = false;
  }
  
  private boolean isCachePathAvailable()
  {
    if (this.m_CachePath != null) {
      return true;
    }
    this.m_CachePath = BaseApplication.current().getExternalFilesDir(null);
    return this.m_CachePath != null;
  }
  
  private void onCameraChanged(Camera paramCamera1, Camera paramCamera2)
  {
    if (paramCamera1 != null)
    {
      paramCamera1.removeCallback(Camera.PROP_ALTERNATIVE_PICTURE_SIZES, this.m_AlternativePictureSizeChangedCallback);
      paramCamera1.removeCallback(Camera.PROP_PICTURE_SIZE, this.m_PictureSizeChangedCallback);
    }
    if (paramCamera2 != null)
    {
      this.m_IsSupported = ((Boolean)paramCamera2.get(Camera.PROP_IS_WATERMARK_SUPPORTED));
      if (this.m_IsSupported.booleanValue())
      {
        paramCamera2.addCallback(Camera.PROP_ALTERNATIVE_PICTURE_SIZES, this.m_AlternativePictureSizeChangedCallback);
        paramCamera2.addCallback(Camera.PROP_PICTURE_SIZE, this.m_PictureSizeChangedCallback);
        updateWatermark();
      }
    }
  }
  
  private void onCaptureStarted()
  {
    if (!this.m_IsSupported.booleanValue()) {
      return;
    }
    if ((!this.m_IsEnter) && (this.m_CurrentTask == null)) {
      return;
    }
    ProcessWatermarkTask localProcessWatermarkTask1;
    ProcessWatermarkTask localProcessWatermarkTask2;
    synchronized (WATERMARK_PROCESSING_LOCK)
    {
      localProcessWatermarkTask1 = this.m_CurrentTask;
      localProcessWatermarkTask2 = this.m_ProcessedTask;
      if (localProcessWatermarkTask1 == localProcessWatermarkTask2) {
        return;
      }
    }
    try
    {
      do
      {
        Log.v(this.TAG, "onCaptureStarted() - Wait for watermark processing");
        WATERMARK_PROCESSING_LOCK.wait();
        localProcessWatermarkTask1 = this.m_CurrentTask;
        localProcessWatermarkTask2 = this.m_ProcessedTask;
      } while (localProcessWatermarkTask1 != localProcessWatermarkTask2);
      applyParameters();
      return;
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;) {}
    }
  }
  
  private void updateWatermark()
  {
    if (!this.m_IsSupported.booleanValue()) {
      return;
    }
    if (!this.m_IsEnter) {
      return;
    }
    if (!isCachePathAvailable())
    {
      Log.e(this.TAG, "updateWatermark() - Failed to update files, cache path is null.");
      return;
    }
    Log.d(this.TAG, "updateWatermark() - Start, watermark : ", this.m_Watermark, ", Author text : ", this.m_SloganAuthorText);
    if (this.m_Watermark == Watermark.NONE)
    {
      applyParameters(Collections.EMPTY_MAP, Collections.EMPTY_MAP);
      return;
    }
    final Object localObject1 = getCameraThread();
    Object localObject3 = (Camera)((CameraThread)localObject1).get(CameraThread.PROP_CAMERA);
    if (localObject3 == null) {
      return;
    }
    Object localObject2 = (Rotation)((CameraThread)localObject1).get(CameraThread.PROP_CAPTURE_ROTATION);
    if (!this.m_IsBokeh) {}
    Object localObject4;
    Size localSize;
    for (localObject1 = (Size)((Camera)localObject3).get(Camera.PROP_PICTURE_SIZE);; localObject1 = (Size)((Camera)localObject3).get(Camera.PROP_BOKEH_PICTURE_SIZE))
    {
      localObject4 = (List)((Camera)localObject3).get(Camera.PROP_ALTERNATIVE_PICTURE_SIZES);
      localObject3 = new ArrayList();
      ((List)localObject3).add(localObject1);
      ((List)localObject3).addAll((Collection)localObject4);
      localObject1 = new ArrayList();
      if (!((Rotation)localObject2).isPortrait()) {
        break label584;
      }
      localObject4 = ((Iterable)localObject3).iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localSize = (Size)((Iterator)localObject4).next();
        ((List)localObject1).add(new Size(localSize.getHeight(), localSize.getWidth()));
      }
    }
    int i;
    int j;
    label310:
    Object localObject6;
    Object localObject5;
    if (localObject2 == Rotation.PORTRAIT)
    {
      i = 270;
      this.m_WatermarkBoundsTable.clear();
      this.m_WatermarkFilePathsTable.clear();
      localObject4 = new ArrayList();
      j = 0;
      if (j >= ((List)localObject3).size()) {
        break label812;
      }
      localSize = (Size)((List)localObject3).get(j);
      localObject6 = (Size)((List)localObject1).get(j);
      localObject5 = new Rect();
      this.m_SloganWatermarkDrawable.calculateWatermarkBounds(((Size)localObject6).getWidth(), ((Size)localObject6).getHeight(), (Rect)localObject5);
      switch (-getcom-oneplus-base-RotationSwitchesValues()[localObject2.ordinal()])
      {
      case 3: 
      default: 
        label416:
        this.m_WatermarkBoundsTable.put(localSize, localObject5);
        localObject5 = this.m_CachePath.getAbsolutePath() + "/" + String.format("watermark_v%1$d_%2$d_%3$d_%4$d", new Object[] { Integer.valueOf(1), Integer.valueOf(localSize.getWidth()), Integer.valueOf(localSize.getHeight()), Integer.valueOf(i) });
        this.m_WatermarkFilePathsTable.put(localSize, (String)localObject5 + "_nv21a");
        if (!new File((String)localObject5 + "_nv21a").exists()) {
          break;
        }
      }
    }
    for (;;)
    {
      j += 1;
      break label310;
      i = 90;
      break;
      label584:
      localObject4 = ((Iterable)localObject3).iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localSize = (Size)((Iterator)localObject4).next();
        ((List)localObject1).add(new Size(localSize.getWidth(), localSize.getHeight()));
      }
      if (localObject2 == Rotation.LANDSCAPE)
      {
        i = 0;
        break;
      }
      i = 180;
      break;
      ((Rect)localObject5).set(0, 0, ((Rect)localObject5).right, ((Rect)localObject5).height());
      break label416;
      ((Rect)localObject5).set(localSize.getWidth() - ((Rect)localObject5).height(), 0, localSize.getWidth(), localSize.getHeight());
      break label416;
      ((Rect)localObject5).set(0, 0, ((Rect)localObject5).height(), localSize.getHeight());
      break label416;
      this.m_SloganWatermarkDrawable.setSubtitleText(this.m_SloganAuthorText);
      localObject6 = this.m_SloganWatermarkDrawable.createWatermarkBitmap(((Size)localObject6).getWidth(), ((Size)localObject6).getHeight());
      WatermarkInfo localWatermarkInfo = new WatermarkInfo(null);
      localWatermarkInfo.bitmap = ((Bitmap)localObject6);
      localWatermarkInfo.filePath = ((String)localObject5);
      localWatermarkInfo.pictureSize = localSize;
      localWatermarkInfo.rotateDegree = i;
      ((List)localObject4).add(localWatermarkInfo);
    }
    label812:
    if (((List)localObject4).isEmpty())
    {
      applyParameters();
      Log.d(this.TAG, "updateWatermark() - End");
      return;
    }
    localObject1 = new ProcessWatermarkTask(null);
    localObject2 = ((Iterable)localObject4).iterator();
    while (((Iterator)localObject2).hasNext()) {
      ((ProcessWatermarkTask)localObject1).addWatermarkInfo((WatermarkInfo)((Iterator)localObject2).next());
    }
    ((ProcessWatermarkTask)localObject1).setEndAction(new Runnable()
    {
      public void run()
      {
        HandlerUtils.sendMessage(OnlineWatermarkControllerImpl.this, 10005, true);
        synchronized (OnlineWatermarkControllerImpl.-get0())
        {
          OnlineWatermarkControllerImpl.-set0(OnlineWatermarkControllerImpl.this, localObject1);
          OnlineWatermarkControllerImpl.-get0().notifyAll();
          return;
        }
      }
    });
    PROCESS_WATERMARK_EXECUTOR.execute((Runnable)localObject1);
    if (this.m_CurrentTask != null) {
      this.m_CurrentTask.cancel();
    }
    this.m_CurrentTask = ((ProcessWatermarkTask)localObject1);
    Log.d(this.TAG, "updateWatermark() - End");
  }
  
  private void updateWatermarkDelay(long paramLong)
  {
    if (!HandlerUtils.hasMessages(this, 10015)) {
      HandlerUtils.sendMessage(this, 10015, paramLong);
    }
  }
  
  public boolean enter(int paramInt)
  {
    boolean bool = false;
    if (isDependencyThread())
    {
      if (this.m_IsEnter) {
        return true;
      }
      Log.d(this.TAG, "enter() - Flags : ", Integer.valueOf(paramInt));
      this.m_IsEnter = true;
      if ((paramInt & 0x1) != 0) {
        bool = true;
      }
      this.m_IsBokeh = bool;
      if (this.m_DeleteOldFiles) {
        deleteWatermarkFiles();
      }
      updateWatermark();
      return true;
    }
    HandlerUtils.sendMessage(this, 10001, paramInt, 0, null);
    return true;
  }
  
  public void exit(int paramInt)
  {
    if (isDependencyThread())
    {
      if (!this.m_IsEnter) {
        return;
      }
      Log.d(this.TAG, "exit() - Flags : ", Integer.valueOf(paramInt));
      applyParameters(Collections.EMPTY_MAP, Collections.EMPTY_MAP);
      this.m_IsEnter = false;
      return;
    }
    HandlerUtils.sendMessage(this, 10002, paramInt, 0, null);
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_IS_SUPPORTED) {
      return this.m_IsSupported;
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 10005: 
      applyParameters();
      return;
    case 10001: 
      enter(paramMessage.arg1);
      return;
    case 10002: 
      exit(paramMessage.arg1);
      return;
    case 10010: 
      paramMessage = (Object[])paramMessage.obj;
      setWatermark((Watermark)paramMessage[0], (String)paramMessage[1]);
      return;
    }
    updateWatermark();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_SloganWatermarkDrawable = new SloganWatermarkDrawable();
    CameraThread localCameraThread = getCameraThread();
    localCameraThread.addCallback(CameraThread.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        OnlineWatermarkControllerImpl.-wrap0(OnlineWatermarkControllerImpl.this, (Camera)paramAnonymousPropertyChangeEventArgs.getOldValue(), (Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    localCameraThread.addCallback(CameraThread.PROP_CAPTURE_ROTATION, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Rotation> paramAnonymousPropertyKey, PropertyChangeEventArgs<Rotation> paramAnonymousPropertyChangeEventArgs)
      {
        OnlineWatermarkControllerImpl.-wrap3(OnlineWatermarkControllerImpl.this);
      }
    });
    localCameraThread.addHandler(CameraThread.EVENT_CAPTURE_STARTED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
      {
        OnlineWatermarkControllerImpl.-wrap1(OnlineWatermarkControllerImpl.this);
      }
    });
    onCameraChanged(null, (Camera)localCameraThread.get(CameraThread.PROP_CAMERA));
  }
  
  public void setWatermark(Watermark paramWatermark, String paramString)
  {
    if (isDependencyThread())
    {
      this.m_Watermark = paramWatermark;
      if (!TextUtils.equals(this.m_SloganAuthorText, paramString))
      {
        this.m_SloganAuthorText = paramString;
        this.m_DeleteOldFiles = true;
      }
      if (this.m_IsEnter)
      {
        if (this.m_DeleteOldFiles) {
          deleteWatermarkFiles();
        }
        updateWatermark();
      }
      return;
    }
    HandlerUtils.sendMessage(this, 10010, new Object[] { paramWatermark, paramString });
  }
  
  private static class ProcessWatermarkTask
    implements Runnable
  {
    private static final String TAG = ProcessWatermarkTask.class.getSimpleName();
    private Runnable m_EndAction;
    private volatile boolean m_IsCanceled;
    private List<OnlineWatermarkControllerImpl.WatermarkInfo> m_WatermarkInfos = new ArrayList();
    
    void addWatermarkInfo(OnlineWatermarkControllerImpl.WatermarkInfo paramWatermarkInfo)
    {
      this.m_WatermarkInfos.add(paramWatermarkInfo);
    }
    
    void cancel()
    {
      if (!this.m_IsCanceled) {
        this.m_IsCanceled = true;
      }
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: getstatic 28	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$ProcessWatermarkTask:TAG	Ljava/lang/String;
      //   3: ldc 55
      //   5: invokestatic 61	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
      //   8: aload_0
      //   9: getfield 50	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$ProcessWatermarkTask:m_IsCanceled	Z
      //   12: ifeq +4 -> 16
      //   15: return
      //   16: iconst_0
      //   17: istore_1
      //   18: iload_1
      //   19: aload_0
      //   20: getfield 37	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$ProcessWatermarkTask:m_WatermarkInfos	Ljava/util/List;
      //   23: invokeinterface 65 1 0
      //   28: if_icmpge +346 -> 374
      //   31: aload_0
      //   32: getfield 37	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$ProcessWatermarkTask:m_WatermarkInfos	Ljava/util/List;
      //   35: iload_1
      //   36: invokeinterface 69 2 0
      //   41: checkcast 71	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$WatermarkInfo
      //   44: astore 5
      //   46: aload 5
      //   48: getfield 75	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$WatermarkInfo:rotateDegree	I
      //   51: istore_2
      //   52: aload 5
      //   54: getfield 79	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$WatermarkInfo:bitmap	Landroid/graphics/Bitmap;
      //   57: astore 6
      //   59: aload 5
      //   61: getfield 82	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$WatermarkInfo:filePath	Ljava/lang/String;
      //   64: astore 10
      //   66: aload 6
      //   68: astore 5
      //   70: iload_2
      //   71: ifeq +87 -> 158
      //   74: invokestatic 88	android/os/SystemClock:elapsedRealtime	()J
      //   77: lstore_3
      //   78: new 90	android/graphics/Matrix
      //   81: dup
      //   82: invokespecial 91	android/graphics/Matrix:<init>	()V
      //   85: astore 5
      //   87: aload 5
      //   89: iload_2
      //   90: i2f
      //   91: aload 6
      //   93: invokevirtual 96	android/graphics/Bitmap:getWidth	()I
      //   96: iconst_2
      //   97: idiv
      //   98: i2f
      //   99: aload 6
      //   101: invokevirtual 99	android/graphics/Bitmap:getHeight	()I
      //   104: iconst_2
      //   105: idiv
      //   106: i2f
      //   107: invokevirtual 103	android/graphics/Matrix:setRotate	(FFF)V
      //   110: aload 6
      //   112: iconst_0
      //   113: iconst_0
      //   114: aload 6
      //   116: invokevirtual 96	android/graphics/Bitmap:getWidth	()I
      //   119: aload 6
      //   121: invokevirtual 99	android/graphics/Bitmap:getHeight	()I
      //   124: aload 5
      //   126: iconst_1
      //   127: invokestatic 107	android/graphics/Bitmap:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
      //   130: astore 5
      //   132: aload_0
      //   133: getfield 50	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$ProcessWatermarkTask:m_IsCanceled	Z
      //   136: ifeq +4 -> 140
      //   139: return
      //   140: getstatic 28	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$ProcessWatermarkTask:TAG	Ljava/lang/String;
      //   143: ldc 109
      //   145: invokestatic 88	android/os/SystemClock:elapsedRealtime	()J
      //   148: lload_3
      //   149: lsub
      //   150: invokestatic 115	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   153: ldc 117
      //   155: invokestatic 120	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
      //   158: aload 5
      //   160: ifnull +207 -> 367
      //   163: invokestatic 88	android/os/SystemClock:elapsedRealtime	()J
      //   166: lstore_3
      //   167: aload 5
      //   169: invokestatic 126	com/oneplus/media/ImageUtils:bitmapToNV21	(Landroid/graphics/Bitmap;)[B
      //   172: astore 11
      //   174: getstatic 28	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$ProcessWatermarkTask:TAG	Ljava/lang/String;
      //   177: ldc -128
      //   179: invokestatic 88	android/os/SystemClock:elapsedRealtime	()J
      //   182: lload_3
      //   183: lsub
      //   184: invokestatic 115	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   187: ldc 117
      //   189: invokestatic 120	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
      //   192: aconst_null
      //   193: astore 6
      //   195: aconst_null
      //   196: astore 8
      //   198: aconst_null
      //   199: astore 7
      //   201: aconst_null
      //   202: astore 9
      //   204: new 130	java/io/FileOutputStream
      //   207: dup
      //   208: new 132	java/lang/StringBuilder
      //   211: dup
      //   212: invokespecial 133	java/lang/StringBuilder:<init>	()V
      //   215: aload 10
      //   217: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   220: ldc -117
      //   222: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   225: invokevirtual 142	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   228: invokespecial 145	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
      //   231: astore 5
      //   233: aload 5
      //   235: aload 11
      //   237: invokevirtual 149	java/io/FileOutputStream:write	([B)V
      //   240: aload 8
      //   242: astore 6
      //   244: aload 5
      //   246: ifnull +12 -> 258
      //   249: aload 5
      //   251: invokevirtual 152	java/io/FileOutputStream:close	()V
      //   254: aload 8
      //   256: astore 6
      //   258: aload 6
      //   260: ifnull +31 -> 291
      //   263: aload 6
      //   265: athrow
      //   266: astore 5
      //   268: getstatic 28	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$ProcessWatermarkTask:TAG	Ljava/lang/String;
      //   271: ldc -102
      //   273: aload 5
      //   275: invokestatic 158	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   278: aload_0
      //   279: getfield 50	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$ProcessWatermarkTask:m_IsCanceled	Z
      //   282: ifeq +85 -> 367
      //   285: return
      //   286: astore 6
      //   288: goto -30 -> 258
      //   291: goto -13 -> 278
      //   294: astore 6
      //   296: aload 9
      //   298: astore 5
      //   300: aload 6
      //   302: athrow
      //   303: astore 8
      //   305: aload 5
      //   307: astore 7
      //   309: aload 8
      //   311: astore 5
      //   313: aload 6
      //   315: astore 8
      //   317: aload 7
      //   319: ifnull +12 -> 331
      //   322: aload 7
      //   324: invokevirtual 152	java/io/FileOutputStream:close	()V
      //   327: aload 6
      //   329: astore 8
      //   331: aload 8
      //   333: ifnull +31 -> 364
      //   336: aload 8
      //   338: athrow
      //   339: aload 6
      //   341: astore 8
      //   343: aload 6
      //   345: aload 7
      //   347: if_acmpeq -16 -> 331
      //   350: aload 6
      //   352: aload 7
      //   354: invokevirtual 162	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
      //   357: aload 6
      //   359: astore 8
      //   361: goto -30 -> 331
      //   364: aload 5
      //   366: athrow
      //   367: iload_1
      //   368: iconst_1
      //   369: iadd
      //   370: istore_1
      //   371: goto -353 -> 18
      //   374: aload_0
      //   375: getfield 164	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$ProcessWatermarkTask:m_EndAction	Ljava/lang/Runnable;
      //   378: ifnull +12 -> 390
      //   381: aload_0
      //   382: getfield 164	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$ProcessWatermarkTask:m_EndAction	Ljava/lang/Runnable;
      //   385: invokeinterface 166 1 0
      //   390: getstatic 28	com/oneplus/camera/watermark/OnlineWatermarkControllerImpl$ProcessWatermarkTask:TAG	Ljava/lang/String;
      //   393: ldc -88
      //   395: invokestatic 61	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
      //   398: return
      //   399: astore 5
      //   401: goto -88 -> 313
      //   404: astore 8
      //   406: aload 5
      //   408: astore 7
      //   410: aload 8
      //   412: astore 5
      //   414: goto -101 -> 313
      //   417: astore 6
      //   419: goto -119 -> 300
      //   422: astore 5
      //   424: goto -156 -> 268
      //   427: astore 7
      //   429: aload 6
      //   431: ifnonnull -92 -> 339
      //   434: aload 7
      //   436: astore 8
      //   438: goto -107 -> 331
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	441	0	this	ProcessWatermarkTask
      //   17	354	1	i	int
      //   51	39	2	j	int
      //   77	106	3	l	long
      //   44	206	5	localObject1	Object
      //   266	8	5	localThrowable1	Throwable
      //   298	67	5	localObject2	Object
      //   399	8	5	localObject3	Object
      //   412	1	5	localObject4	Object
      //   422	1	5	localThrowable2	Throwable
      //   57	207	6	localObject5	Object
      //   286	1	6	localThrowable3	Throwable
      //   294	64	6	localThrowable4	Throwable
      //   417	13	6	localThrowable5	Throwable
      //   199	210	7	localObject6	Object
      //   427	8	7	localThrowable6	Throwable
      //   196	59	8	localObject7	Object
      //   303	7	8	localObject8	Object
      //   315	45	8	localThrowable7	Throwable
      //   404	7	8	localObject9	Object
      //   436	1	8	localObject10	Object
      //   202	95	9	localObject11	Object
      //   64	152	10	str	String
      //   172	64	11	arrayOfByte	byte[]
      // Exception table:
      //   from	to	target	type
      //   263	266	266	java/lang/Throwable
      //   249	254	286	java/lang/Throwable
      //   204	233	294	java/lang/Throwable
      //   300	303	303	finally
      //   204	233	399	finally
      //   233	240	404	finally
      //   233	240	417	java/lang/Throwable
      //   336	339	422	java/lang/Throwable
      //   350	357	422	java/lang/Throwable
      //   364	367	422	java/lang/Throwable
      //   322	327	427	java/lang/Throwable
    }
    
    void setEndAction(Runnable paramRunnable)
    {
      this.m_EndAction = paramRunnable;
    }
  }
  
  private static class WatermarkInfo
  {
    Bitmap bitmap;
    String filePath;
    Size pictureSize;
    int rotateDegree;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/watermark/OnlineWatermarkControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */