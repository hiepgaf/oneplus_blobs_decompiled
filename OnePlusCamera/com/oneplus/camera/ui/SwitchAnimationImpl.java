package com.oneplus.camera.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Message;
import android.os.SystemClock;
import android.util.Size;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.OperationState;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SwitchAnimationImpl
  extends BaseSwitchAnimationImpl
  implements CaptureModeSwitchAnimation, PreviewSizeSwitchAnimation
{
  private static final long ANIMATION_FADE_IN_DURATION = 400L;
  private static final long ANIMATION_FADE_OUT_DELAY = 0L;
  private static final long ANIMATION_FADE_OUT_DURATION = 200L;
  private static final boolean ENABLE_ANIMATION_LOG = false;
  private static final int MSG_CAMERA_PREVIEW_OVERLAY_INVALIDATE = 10005;
  private static final int MSG_FADE_OUT = 10020;
  private static final int MSG_ON_ANIMATION_TIMEOUT = 10010;
  private static final long TIMEOUT = 5000L;
  private List<RectF> m_BackgroundRectangles;
  private Paint m_BitmapPaint;
  private CameraPreviewOverlay m_CameraPreviewOverlay;
  private long m_FadeInStartTime;
  private OperationState m_FadeInState = OperationState.STOPPED;
  private long m_FadeOutStartTime;
  private OperationState m_FadeOutState = OperationState.STOPPED;
  private boolean m_IsFirstPreviewFrameReceived;
  private Paint m_Paint;
  private Bitmap m_PreviewBlurImage;
  private RectF m_PreviewBounds;
  private Size m_PreviewContainerSize;
  private Bitmap m_PreviewImage;
  private Viewfinder m_Viewfinder;
  
  protected SwitchAnimationImpl(CameraActivity paramCameraActivity)
  {
    this("Switch Animation Impl", paramCameraActivity);
  }
  
  protected SwitchAnimationImpl(String paramString, CameraActivity paramCameraActivity)
  {
    super(paramString, paramCameraActivity);
  }
  
  private boolean hasAnimation()
  {
    return (this.m_FadeInState != OperationState.STOPPED) || (this.m_FadeOutState != OperationState.STOPPED);
  }
  
  private void onFadeInAnimationFinished()
  {
    Log.v(this.TAG, "onFadeInAnimationFinished()");
    HandlerUtils.removeMessages(this, 10010);
    setReadOnly(PROP_ANIMATION_STATE, OperationState.STARTED);
    if (this.m_IsFirstPreviewFrameReceived) {
      startFadeOutAnimation();
    }
  }
  
  private void onFadeOutAnimationFinished()
  {
    Log.v(this.TAG, "onFadeOutAnimationFinished()");
    reset();
  }
  
  private void onPreviewOverlayRender(Canvas paramCanvas, CameraPreviewOverlay.RenderingParams paramRenderingParams)
  {
    if (this.m_PreviewBlurImage != null)
    {
      if (this.m_BackgroundRectangles == null)
      {
        this.m_BackgroundRectangles = new ArrayList();
        if (this.m_PreviewBounds.top > 0.0F) {
          this.m_BackgroundRectangles.add(new RectF(0.0F, 0.0F, this.m_PreviewContainerSize.getHeight(), this.m_PreviewBounds.top));
        }
        if (this.m_PreviewBounds.bottom < this.m_PreviewContainerSize.getWidth()) {
          this.m_BackgroundRectangles.add(new RectF(0.0F, this.m_PreviewBounds.bottom, this.m_PreviewContainerSize.getHeight(), this.m_PreviewContainerSize.getWidth()));
        }
      }
      int i = 0;
      if (this.m_FadeInState != OperationState.STOPPED) {
        switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[this.m_FadeInState.ordinal()])
        {
        }
      }
      while (i != 0)
      {
        paramRenderingParams = this.m_BackgroundRectangles.iterator();
        for (;;)
        {
          if (paramRenderingParams.hasNext())
          {
            paramCanvas.drawRect((RectF)paramRenderingParams.next(), this.m_Paint);
            continue;
            long l = SystemClock.uptimeMillis() - this.m_FadeInStartTime;
            float f;
            if (l < 400L)
            {
              f = (float)l / 400.0F;
              this.m_Paint.setAlpha(255);
              this.m_BitmapPaint.setAlpha(Math.round(f * 255.0F));
            }
            for (;;)
            {
              int j = 1;
              HandlerUtils.sendMessage(this, 10005, true);
              i = j;
              if (get(PROP_ANIMATION_STATE) != OperationState.STARTING) {
                break;
              }
              setReadOnly(PROP_ANIMATION_STATE, OperationState.STARTED);
              i = j;
              break;
              this.m_Paint.setAlpha(255);
              this.m_BitmapPaint.setAlpha(255);
              this.m_FadeInState = OperationState.STOPPING;
            }
            this.m_Paint.setAlpha(0);
            this.m_BitmapPaint.setAlpha(0);
            i = 1;
            this.m_FadeInStartTime = SystemClock.uptimeMillis();
            this.m_FadeInState = OperationState.STARTED;
            HandlerUtils.sendMessage(this, 10005, true);
            break;
            i = 1;
            this.m_FadeInState = OperationState.STOPPED;
            onFadeInAnimationFinished();
            break;
            if (this.m_FadeOutState != OperationState.STOPPED) {
              switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[this.m_FadeOutState.ordinal()])
              {
              default: 
                break;
              case 1: 
                l = SystemClock.uptimeMillis() - this.m_FadeOutStartTime;
                if (l < 200L)
                {
                  f = (1.0F - (float)l / 200.0F) * 255.0F;
                  this.m_Paint.setAlpha(Math.round(f));
                  this.m_BitmapPaint.setAlpha(Math.round(f));
                }
                for (;;)
                {
                  i = 1;
                  HandlerUtils.sendMessage(this, 10005, true);
                  break;
                  this.m_Paint.setAlpha(0);
                  this.m_BitmapPaint.setAlpha(0);
                  this.m_FadeOutState = OperationState.STOPPING;
                }
              case 2: 
                this.m_Paint.setAlpha(255);
                this.m_BitmapPaint.setAlpha(255);
                i = 1;
                this.m_FadeOutStartTime = SystemClock.uptimeMillis();
                this.m_FadeOutState = OperationState.STARTED;
                HandlerUtils.sendMessage(this, 10005, true);
                break;
              case 3: 
                this.m_FadeOutState = OperationState.STOPPED;
                onFadeOutAnimationFinished();
                break;
              }
            }
            i = 1;
            break;
          }
        }
        if (this.m_FadeInState == OperationState.STARTED) {
          paramCanvas.drawBitmap(this.m_PreviewImage, null, this.m_PreviewBounds, null);
        }
        paramCanvas.drawBitmap(this.m_PreviewBlurImage, null, this.m_PreviewBounds, this.m_BitmapPaint);
      }
    }
  }
  
  private boolean startFadeInAnimation()
  {
    if (this.m_CameraPreviewOverlay == null)
    {
      Log.e(this.TAG, "startFadeOutAnimation() - No camera preview overlay");
      return false;
    }
    if (hasAnimation())
    {
      Log.w(this.TAG, "startFadeInAnimation() - Already has animation, skip");
      return false;
    }
    Log.v(this.TAG, "startFadeInAnimation()");
    this.m_FadeInState = OperationState.STARTING;
    this.m_CameraPreviewOverlay.invalidateCameraPreviewOverlay();
    return true;
  }
  
  private boolean startFadeOutAnimation()
  {
    if (this.m_CameraPreviewOverlay == null)
    {
      Log.e(this.TAG, "startFadeOutAnimation() - No camera preview overlay");
      return false;
    }
    if (hasAnimation())
    {
      Log.w(this.TAG, "startFadeOutAnimation() - Already has animation, skip");
      return false;
    }
    Log.v(this.TAG, "startFadeOutAnimation()");
    this.m_FadeOutState = OperationState.STARTING;
    this.m_CameraPreviewOverlay.invalidateCameraPreviewOverlay();
    return true;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
    case 10005: 
    case 10020: 
      do
      {
        do
        {
          return;
        } while (this.m_CameraPreviewOverlay == null);
        this.m_CameraPreviewOverlay.invalidateCameraPreviewOverlay();
        return;
      } while (hasAnimation());
      startFadeOutAnimation();
      return;
    }
    if (get(PROP_ANIMATION_STATE) == OperationState.STOPPED)
    {
      Log.w(this.TAG, "handleMessage() - Already stopped");
      return;
    }
    Log.e(this.TAG, "handleMessage() - On animation timeout");
    startFadeOutAnimation();
  }
  
  protected void onFirstPreviewFrameReceived()
  {
    Log.v(this.TAG, "onFirstPreviewFrameReceived()");
    this.m_IsFirstPreviewFrameReceived = true;
    if (!hasAnimation())
    {
      HandlerUtils.sendMessage(this, 10020, 0L);
      return;
    }
    HandlerUtils.sendMessage(this, 10010, 5000L);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_Paint = new Paint();
    this.m_Paint.setAntiAlias(true);
    this.m_Paint.setColor(-16777216);
    this.m_Paint.setStyle(Paint.Style.FILL);
    this.m_BitmapPaint = new Paint();
    this.m_BitmapPaint.setFilterBitmap(true);
    this.m_BitmapPaint.setColor(-16777216);
    this.m_BitmapPaint.setStyle(Paint.Style.FILL);
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_CameraPreviewOverlay = ((CameraPreviewOverlay)localCameraActivity.findComponent(CameraPreviewOverlay.class));
    if (this.m_CameraPreviewOverlay != null) {
      this.m_CameraPreviewOverlay.addRenderer(new CameraPreviewOverlay.Renderer()
      {
        public void onRender(Canvas paramAnonymousCanvas, CameraPreviewOverlay.RenderingParams paramAnonymousRenderingParams)
        {
          SwitchAnimationImpl.-wrap0(SwitchAnimationImpl.this, paramAnonymousCanvas, paramAnonymousRenderingParams);
        }
      }, 0);
    }
    for (;;)
    {
      this.m_Viewfinder = ((Viewfinder)localCameraActivity.findComponent(Viewfinder.class));
      if (this.m_Viewfinder == null) {
        Log.w(this.TAG, "onInitialize() - Cannot find Viewfinder component");
      }
      return;
      Log.w(this.TAG, "onInitialize() - Cannot find CameraPreviewOverlay component");
    }
  }
  
  protected void onLastPreviewImageCreated(Bitmap[] paramArrayOfBitmap)
  {
    if (get(PROP_ANIMATION_STATE) == OperationState.STOPPED)
    {
      Log.w(this.TAG, "onLastPreviewImageCreated() - Already stopped");
      return;
    }
    if (paramArrayOfBitmap == null)
    {
      Log.e(this.TAG, "onLastPreviewImageCreated() - Image is null, reset");
      reset();
      return;
    }
    Log.v(this.TAG, "onLastPreviewImageCreated()");
    this.m_PreviewImage = paramArrayOfBitmap[0];
    this.m_PreviewBlurImage = paramArrayOfBitmap[1];
    startFadeInAnimation();
  }
  
  protected void onReset()
  {
    Log.v(this.TAG, "onReset()");
    HandlerUtils.removeMessages(this, 10005);
    HandlerUtils.removeMessages(this, 10010);
    HandlerUtils.removeMessages(this, 10020);
    this.m_PreviewBlurImage = null;
    this.m_PreviewImage = null;
    this.m_PreviewBounds = null;
    this.m_BackgroundRectangles = null;
    if (this.m_CameraPreviewOverlay != null) {
      this.m_CameraPreviewOverlay.invalidateCameraPreviewOverlay();
    }
    this.m_FadeInState = OperationState.STOPPED;
    this.m_FadeOutState = OperationState.STOPPED;
  }
  
  protected void onStarted()
  {
    this.m_IsFirstPreviewFrameReceived = false;
    if (this.m_Viewfinder != null)
    {
      this.m_PreviewBounds = ((RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS));
      this.m_PreviewContainerSize = ((Size)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_CONTAINER_SIZE));
      return;
    }
    Log.e(this.TAG, "start() - No viewfinder");
    reset();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/SwitchAnimationImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */