package com.oneplus.camera.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicResize;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type.Builder;
import android.util.Size;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraCaptureEventArgs;
import com.oneplus.camera.OperationState;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.media.ImagePlane;
import com.oneplus.camera.media.YuvUtils;
import com.oneplus.renderscript.RenderScriptManager;

public abstract class BaseSwitchAnimationImpl
  extends UIComponent
  implements SwitchAnimation
{
  private static final int MSG_ASYNC_CLOSE_RENDER_SCRIPT = -20001;
  private static final int MSG_ASYNC_PROCESS_IMAGE_PLANE = -20002;
  private static final int MSG_ON_FIRST_PREVIEW_FRAME_TIMEOUT = -10020;
  private static final int MSG_ON_LAST_PREVIEW_FRAME_TIMEOUT = -10021;
  private static final int MSG_ON_LAST_PREVIEW_IMAGE_CREATED = -10015;
  private static final int MSG_ON_PROCESS_IMAGE_PLANES_TIMEOUT = -10022;
  private static final int TIMEOUT_PREVIEW_FRAME = 5000;
  private static final int TIMEOUT_PROCESS_IMAGE_PLANES = 3000;
  private Handle m_CaptureUIDisableHandle;
  private RenderScript m_RenderScript;
  private Handle m_RenderScriptHandle;
  private ScriptIntrinsicResize m_ResizeScript;
  private ScriptIntrinsicBlur m_RgbBlurScript;
  private Viewfinder m_Viewfinder;
  private ScriptIntrinsicYuvToRGB m_YuvToRgbScript;
  
  protected BaseSwitchAnimationImpl(CameraActivity paramCameraActivity)
  {
    this("Base Switch Animation Impl", paramCameraActivity);
  }
  
  protected BaseSwitchAnimationImpl(String paramString, CameraActivity paramCameraActivity)
  {
    super(paramString, paramCameraActivity, true, true);
  }
  
  protected void handleAsyncMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleAsyncMessage(paramMessage);
      return;
    case -20001: 
      Handle.close(this.m_RenderScriptHandle);
      return;
    }
    if (get(PROP_ANIMATION_STATE) == OperationState.STOPPED)
    {
      Log.w(this.TAG, "handleAsyncMessage() - Already stopped");
      return;
    }
    int i = paramMessage.arg1;
    int j = paramMessage.arg2;
    paramMessage = (Object[])paramMessage.obj;
    Camera.LensFacing localLensFacing = (Camera.LensFacing)paramMessage[1];
    RectF localRectF = (RectF)paramMessage[2];
    Bitmap localBitmap;
    if ((paramMessage[0] instanceof Bitmap))
    {
      localBitmap = (Bitmap)paramMessage[0];
      paramMessage = null;
      Log.v(this.TAG, "handleAsyncMessage() - Process image planes, width: ", Integer.valueOf(i), ", height: ", Integer.valueOf(j), ", preview bounds: ", localRectF);
      HandlerUtils.sendMessage(this, 55514, 3000L);
      if (localBitmap == null) {
        break label207;
      }
    }
    for (paramMessage = processImageBitmap(localBitmap, localRectF);; paramMessage = processImagePlanes(i, j, paramMessage, localLensFacing, localRectF))
    {
      label207:
      try
      {
        notifyAll();
        HandlerUtils.removeMessages(this, 55514);
        return;
      }
      finally {}
      localBitmap = null;
      paramMessage = (ImagePlane[])paramMessage[0];
      break;
    }
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    case -10019: 
    case -10018: 
    case -10017: 
    case -10016: 
    default: 
      super.handleMessage(paramMessage);
      return;
    case -10015: 
      onLastPreviewImageCreated((Bitmap[])paramMessage.obj);
      return;
    case -10020: 
      Log.e(this.TAG, "handleMessage() - On first preview frame timeout");
      reset();
      return;
    case -10021: 
      Log.e(this.TAG, "handleMessage() - On last frame timeout");
      reset();
      return;
    }
    Log.e(this.TAG, "handleMessage() - On process image planes timeout");
    reset();
  }
  
  protected void onDeinitialize()
  {
    HandlerUtils.sendAsyncMessage(this, 45535);
    super.onDeinitialize();
  }
  
  protected abstract void onFirstPreviewFrameReceived();
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_Viewfinder = ((Viewfinder)localCameraActivity.findComponent(Viewfinder.class));
    if (this.m_Viewfinder == null) {
      Log.w(this.TAG, "onInitialize() - Cannot find Viewfinder component");
    }
    localCameraActivity.addCallback(CameraActivity.PROP_CAMERA_PREVIEW_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<OperationState> paramAnonymousPropertyKey, PropertyChangeEventArgs<OperationState> paramAnonymousPropertyChangeEventArgs)
      {
        if ((BaseSwitchAnimationImpl.this.get(BaseSwitchAnimationImpl.PROP_ANIMATION_STATE) == OperationState.STARTED) && (paramAnonymousPropertyChangeEventArgs.getNewValue() == OperationState.STOPPED)) {
          HandlerUtils.sendMessage(BaseSwitchAnimationImpl.this, 55516, 5000L);
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_CAMERA_PREVIEW_RECEIVED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if ((BaseSwitchAnimationImpl.this.get(BaseSwitchAnimationImpl.PROP_ANIMATION_STATE) == OperationState.STARTED) && (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()))
        {
          HandlerUtils.removeMessages(BaseSwitchAnimationImpl.this, 55516);
          BaseSwitchAnimationImpl.this.onFirstPreviewFrameReceived();
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_RUNNING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if ((!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) && (BaseSwitchAnimationImpl.this.get(BaseSwitchAnimationImpl.PROP_ANIMATION_STATE) != OperationState.STOPPED)) {
          BaseSwitchAnimationImpl.this.reset();
        }
      }
    });
  }
  
  protected abstract void onLastPreviewImageCreated(Bitmap[] paramArrayOfBitmap);
  
  protected abstract void onReset();
  
  protected abstract void onStarted();
  
  protected Bitmap[] processImageBitmap(Bitmap paramBitmap, RectF paramRectF)
  {
    if (this.m_RenderScript == null)
    {
      this.m_RenderScriptHandle = RenderScriptManager.createRenderScript(getCameraActivity());
      this.m_RenderScript = RenderScriptManager.getRenderScript(this.m_RenderScriptHandle);
      this.m_YuvToRgbScript = ScriptIntrinsicYuvToRGB.create(this.m_RenderScript, Element.RGBA_8888(this.m_RenderScript));
      this.m_RgbBlurScript = ScriptIntrinsicBlur.create(this.m_RenderScript, Element.RGBA_8888(this.m_RenderScript));
      this.m_ResizeScript = ScriptIntrinsicResize.create(this.m_RenderScript);
    }
    Object localObject1 = Allocation.createFromBitmap(this.m_RenderScript, paramBitmap);
    int i = paramBitmap.getWidth() / 10;
    int j = paramBitmap.getHeight() / 10;
    Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
    Object localObject2 = Allocation.createFromBitmap(this.m_RenderScript, localBitmap);
    Object localObject3 = new Type.Builder(this.m_RenderScript, Element.RGBA_8888(this.m_RenderScript));
    ((Type.Builder)localObject3).setX(i);
    ((Type.Builder)localObject3).setY(j);
    localObject3 = Allocation.createTyped(this.m_RenderScript, ((Type.Builder)localObject3).create(), 1);
    this.m_ResizeScript.setInput((Allocation)localObject1);
    this.m_ResizeScript.forEach_bicubic((Allocation)localObject3);
    this.m_RgbBlurScript.setRadius(25.0F);
    this.m_RgbBlurScript.setInput((Allocation)localObject3);
    this.m_RgbBlurScript.forEach((Allocation)localObject2);
    ((Allocation)localObject2).copyTo(localBitmap);
    localObject1 = new Matrix();
    localObject2 = new Matrix();
    ((Matrix)localObject1).postScale(paramRectF.width() / localBitmap.getHeight(), paramRectF.height() / localBitmap.getWidth());
    ((Matrix)localObject2).postScale(paramRectF.width() / paramBitmap.getHeight(), paramRectF.height() / paramBitmap.getWidth());
    return new Bitmap[] { paramBitmap, localBitmap };
  }
  
  protected Bitmap[] processImagePlanes(int paramInt1, int paramInt2, ImagePlane[] paramArrayOfImagePlane, Camera.LensFacing paramLensFacing, RectF paramRectF)
  {
    Object localObject1 = new byte[paramInt1 * paramInt2 * 3 / 2];
    for (;;)
    {
      Object localObject2;
      Object localObject3;
      try
      {
        YuvUtils.multiPlaneYuvToNV21(paramArrayOfImagePlane, (byte[])localObject1, paramInt1, paramInt2);
        if (this.m_RenderScript == null)
        {
          this.m_RenderScriptHandle = RenderScriptManager.createRenderScript(getCameraActivity());
          this.m_RenderScript = RenderScriptManager.getRenderScript(this.m_RenderScriptHandle);
          this.m_YuvToRgbScript = ScriptIntrinsicYuvToRGB.create(this.m_RenderScript, Element.RGBA_8888(this.m_RenderScript));
          this.m_RgbBlurScript = ScriptIntrinsicBlur.create(this.m_RenderScript, Element.RGBA_8888(this.m_RenderScript));
          this.m_ResizeScript = ScriptIntrinsicResize.create(this.m_RenderScript);
        }
        paramArrayOfImagePlane = new Type.Builder(this.m_RenderScript, Element.U8(this.m_RenderScript));
        paramArrayOfImagePlane.setX(paramInt1 * paramInt2 * 3 / 2);
        paramArrayOfImagePlane = Allocation.createTyped(this.m_RenderScript, paramArrayOfImagePlane.create(), 1);
        localObject2 = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
        Allocation localAllocation1 = Allocation.createFromBitmap(this.m_RenderScript, (Bitmap)localObject2);
        paramInt1 /= 10;
        paramInt2 /= 10;
        localObject3 = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
        Allocation localAllocation2 = Allocation.createFromBitmap(this.m_RenderScript, (Bitmap)localObject3);
        Object localObject4 = new Type.Builder(this.m_RenderScript, Element.RGBA_8888(this.m_RenderScript));
        ((Type.Builder)localObject4).setX(paramInt1);
        ((Type.Builder)localObject4).setY(paramInt2);
        localObject4 = Allocation.createTyped(this.m_RenderScript, ((Type.Builder)localObject4).create(), 1);
        paramArrayOfImagePlane.copyFrom((byte[])localObject1);
        this.m_YuvToRgbScript.setInput(paramArrayOfImagePlane);
        this.m_YuvToRgbScript.forEach(localAllocation1);
        this.m_ResizeScript.setInput(localAllocation1);
        this.m_ResizeScript.forEach_bicubic((Allocation)localObject4);
        this.m_RgbBlurScript.setRadius(25.0F);
        this.m_RgbBlurScript.setInput((Allocation)localObject4);
        this.m_RgbBlurScript.forEach(localAllocation2);
        localAllocation1.copyTo((Bitmap)localObject2);
        localAllocation2.copyTo((Bitmap)localObject3);
        localObject1 = localObject3;
        paramArrayOfImagePlane = (ImagePlane[])localObject2;
        if (paramLensFacing == Camera.LensFacing.FRONT)
        {
          paramArrayOfImagePlane = new Matrix();
          paramArrayOfImagePlane.postScale(1.0F, -1.0F);
          localObject1 = Bitmap.createBitmap((Bitmap)localObject3, 0, 0, ((Bitmap)localObject3).getWidth(), ((Bitmap)localObject3).getHeight(), paramArrayOfImagePlane, true);
          paramArrayOfImagePlane = Bitmap.createBitmap((Bitmap)localObject2, 0, 0, ((Bitmap)localObject2).getWidth(), ((Bitmap)localObject2).getHeight(), paramArrayOfImagePlane, true);
        }
        localObject3 = new Matrix();
        localObject2 = new Matrix();
        ((Matrix)localObject3).postScale(paramRectF.width() / ((Bitmap)localObject1).getHeight(), paramRectF.height() / ((Bitmap)localObject1).getWidth());
        ((Matrix)localObject2).postScale(paramRectF.width() / paramArrayOfImagePlane.getHeight(), paramRectF.height() / paramArrayOfImagePlane.getWidth());
        if (paramLensFacing == Camera.LensFacing.BACK)
        {
          ((Matrix)localObject3).postRotate(90.0F);
          ((Matrix)localObject3).postTranslate(paramRectF.width(), 0.0F);
          ((Matrix)localObject2).postRotate(90.0F);
          ((Matrix)localObject2).postTranslate(paramRectF.width(), 0.0F);
          paramLensFacing = Bitmap.createBitmap((Bitmap)localObject1, 0, 0, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight(), (Matrix)localObject3, true);
          return new Bitmap[] { Bitmap.createBitmap(paramArrayOfImagePlane, 0, 0, paramArrayOfImagePlane.getWidth(), paramArrayOfImagePlane.getHeight(), (Matrix)localObject2, true), paramLensFacing };
        }
      }
      catch (Throwable paramArrayOfImagePlane)
      {
        Log.e(this.TAG, "processImagePlanes() - Error to transfer to NV21");
        paramArrayOfImagePlane.printStackTrace();
        return null;
      }
      if (paramLensFacing == Camera.LensFacing.FRONT)
      {
        ((Matrix)localObject3).postRotate(-90.0F);
        ((Matrix)localObject3).postTranslate(0.0F, paramRectF.height());
        ((Matrix)localObject2).postRotate(-90.0F);
        ((Matrix)localObject2).postTranslate(0.0F, paramRectF.height());
      }
    }
  }
  
  protected void reset()
  {
    if (get(PROP_ANIMATION_STATE) == OperationState.STOPPED)
    {
      Log.w(this.TAG, "reset() - Already stopped");
      return;
    }
    Log.v(this.TAG, "reset()");
    onReset();
    HandlerUtils.removeMessages(this, 55516);
    HandlerUtils.removeMessages(this, 55515);
    HandlerUtils.removeMessages(this, 55514);
    setReadOnly(PROP_ANIMATION_STATE, OperationState.STOPPED);
    this.m_CaptureUIDisableHandle = Handle.close(this.m_CaptureUIDisableHandle);
  }
  
  public void start(int paramInt)
  {
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_ANIMATION_STATE)).ordinal()])
    {
    default: 
      Log.w(this.TAG, "start() - Previous animation is running, skip");
      return;
    }
    setReadOnly(PROP_ANIMATION_STATE, OperationState.STARTING);
    if (!Handle.isValid(this.m_CaptureUIDisableHandle)) {
      this.m_CaptureUIDisableHandle = getCameraActivity().disableCaptureUI(this.TAG);
    }
    final RectF localRectF;
    final Camera localCamera;
    if (this.m_Viewfinder != null)
    {
      localRectF = (RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
      localCamera = getCamera();
      if (localCamera == null)
      {
        Log.w(this.TAG, "start() - No camera so switch directly");
        reset();
      }
    }
    else
    {
      Log.e(this.TAG, "start() - No viewfinder");
      reset();
      return;
    }
    Log.v(this.TAG, "start()");
    HandlerUtils.sendMessage(this, 55515, 5000L);
    Bitmap localBitmap2 = null;
    Bitmap localBitmap1 = localBitmap2;
    if (this.m_Viewfinder != null)
    {
      localBitmap1 = localBitmap2;
      if (((Boolean)this.m_Viewfinder.get(Viewfinder.PROP_IS_DISPLAY_PREVIEW_FRAME_COPY_SUPPORTED)).booleanValue())
      {
        localBitmap1 = localBitmap2;
        if (localRectF.width() > 0.0F)
        {
          localBitmap1 = localBitmap2;
          if (localRectF.height() > 0.0F)
          {
            paramInt = (int)localRectF.width();
            int i = (int)localRectF.height();
            localBitmap2 = Bitmap.createBitmap(paramInt, i, Bitmap.Config.ARGB_8888);
            localBitmap1 = localBitmap2;
            if (this.m_Viewfinder.copyDisplayPreviewFrame(localBitmap2, 0)) {
              HandlerUtils.sendAsyncMessage(this, 45534, paramInt, i, new Object[] { localBitmap2, localCamera.get(Camera.PROP_LENS_FACING), localRectF });
            }
          }
        }
      }
    }
    try
    {
      wait();
      localBitmap1 = localBitmap2;
      if (localBitmap1 == null) {
        HandlerUtils.post(getCameraThread(), new Runnable()
        {
          public void run()
          {
            localCamera.addHandler(Camera.EVENT_PREVIEW_RECEIVED, new EventHandler()
            {
              public void onEventReceived(EventSource paramAnonymous2EventSource, EventKey<CameraCaptureEventArgs> paramAnonymous2EventKey, CameraCaptureEventArgs paramAnonymous2CameraCaptureEventArgs)
              {
                this.val$camera.removeHandler(Camera.EVENT_PREVIEW_RECEIVED, this);
                if (BaseSwitchAnimationImpl.this.get(BaseSwitchAnimationImpl.PROP_ANIMATION_STATE) == OperationState.STOPPED)
                {
                  Log.w(BaseSwitchAnimationImpl.-get0(BaseSwitchAnimationImpl.this), "onEventReceived() - Invalid state");
                  return;
                }
                Log.v(BaseSwitchAnimationImpl.-get0(BaseSwitchAnimationImpl.this), "onEventReceived() - Receive preview frame");
                HandlerUtils.removeMessages(BaseSwitchAnimationImpl.this, 55515);
                paramAnonymous2EventSource = (Camera.LensFacing)this.val$camera.get(Camera.PROP_LENS_FACING);
                int i = paramAnonymous2CameraCaptureEventArgs.getPictureSize().getWidth();
                int j = paramAnonymous2CameraCaptureEventArgs.getPictureSize().getHeight();
                paramAnonymous2EventKey = paramAnonymous2CameraCaptureEventArgs.getPicturePlanes();
                paramAnonymous2CameraCaptureEventArgs = this.val$previewBounds;
                HandlerUtils.sendAsyncMessage(BaseSwitchAnimationImpl.this, 45534, i, j, new Object[] { paramAnonymous2EventKey, paramAnonymous2EventSource, paramAnonymous2CameraCaptureEventArgs });
              }
            });
          }
        });
      }
      onStarted();
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(this.TAG, "start() - Fail to wait asynchronous image processing", localThrowable);
      }
    }
    finally {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/BaseSwitchAnimationImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */