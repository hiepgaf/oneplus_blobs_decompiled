package com.oneplus.camera.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicResize;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type.Builder;
import android.util.Size;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerObject;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraCaptureEventArgs;
import com.oneplus.camera.media.ImagePlane;
import com.oneplus.media.ImageUtils;
import com.oneplus.renderscript.RenderScriptManager;
import com.oneplus.util.SizeUtils;

public class BlurPreviewCoverProducer
  extends BasePreviewCoverProducer
{
  private static final long ANIMATION_FADE_IN_DURATION = 80L;
  private static final long ANIMATION_FADE_OUT_DURATION = 80L;
  private static final int MSG_ON_IMAGE_BITMAPS_PREPARED = 10001;
  private Handler m_ImageProcessingHandler;
  private HandlerThread m_ImageProcessingThread = new HandlerThread("Blur preview cover processing thread");
  private final Runnable m_OnFadeInAnimationEndRunnable = new Runnable()
  {
    public void run()
    {
      BlurPreviewCoverProducer.-wrap2(BlurPreviewCoverProducer.this);
    }
  };
  private final Runnable m_OnFadeInAnimationStartRunnable = new Runnable()
  {
    public void run()
    {
      BlurPreviewCoverProducer.-wrap3(BlurPreviewCoverProducer.this);
    }
  };
  private final Runnable m_OnFadeOutAnimationEndRunnable = new Runnable()
  {
    public void run()
    {
      BlurPreviewCoverProducer.-wrap4(BlurPreviewCoverProducer.this);
    }
  };
  private final Runnable m_OnFadeOutAnimationStartRunnable = new Runnable()
  {
    public void run()
    {
      BlurPreviewCoverProducer.-wrap5(BlurPreviewCoverProducer.this);
    }
  };
  private final Runnable m_OnReverseFadeOutAnimationEndRunnable = new Runnable()
  {
    public void run()
    {
      BlurPreviewCoverProducer.-wrap6(BlurPreviewCoverProducer.this);
    }
  };
  private RenderScript m_RenderScript;
  private Handle m_RenderScriptHandle;
  private ScriptIntrinsicResize m_ResizeScript;
  private ScriptIntrinsicBlur m_RgbBlurScript;
  private Viewfinder m_Viewfinder;
  private ScriptIntrinsicYuvToRGB m_YuvToRgbScript;
  
  public BlurPreviewCoverProducer(CameraActivity paramCameraActivity)
  {
    super(paramCameraActivity);
    this.m_ImageProcessingThread.start();
    this.m_ImageProcessingHandler = new Handler(this.m_ImageProcessingThread.getLooper());
  }
  
  private void onFadeInAnimationEnd()
  {
    getHandler().post(new Runnable()
    {
      public void run()
      {
        if (BlurPreviewCoverProducer.this.get(BlurPreviewCoverProducer.PROP_STATE) == PreviewCoverProducer.State.IN_ANIMATION) {
          BlurPreviewCoverProducer.-wrap1(BlurPreviewCoverProducer.this, BlurPreviewCoverProducer.PROP_STATE, PreviewCoverProducer.State.READY_TO_OUT_ANIMATION);
        }
      }
    });
  }
  
  private void onFadeInAnimationStart() {}
  
  private void onFadeOutAnimationEnd()
  {
    View localView1 = getPreviewCoverContainer();
    View localView2 = getPreviewCoverBackgroundView();
    ImageView localImageView1 = getPreviewCoverBaseImageView();
    ImageView localImageView2 = getPreviewCoverTopImageView();
    localView1.setVisibility(8);
    localView2.setVisibility(8);
    localImageView1.setVisibility(8);
    localImageView2.setVisibility(8);
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.STOPPED);
  }
  
  private void onFadeOutAnimationStart() {}
  
  private void onPreviewCoverPrepared(Bitmap paramBitmap1, Bitmap paramBitmap2, RectF paramRectF)
  {
    if (get(PROP_STATE) != PreviewCoverProducer.State.PREPARING)
    {
      Log.w(this.TAG, "onPreviewCoverPrepared() - Current state is " + get(PROP_STATE));
      return;
    }
    ImageView localImageView2 = getPreviewCoverBaseImageView();
    ImageView localImageView1 = getPreviewCoverTopImageView();
    localImageView2.setImageBitmap(paramBitmap1);
    paramBitmap1 = (RelativeLayout.LayoutParams)localImageView2.getLayoutParams();
    paramBitmap1.topMargin = Math.round(paramRectF.top);
    paramBitmap1.width = Math.round(paramRectF.width());
    paramBitmap1.height = Math.round(paramRectF.height());
    localImageView1.setImageBitmap(paramBitmap2);
    paramBitmap1 = (RelativeLayout.LayoutParams)localImageView1.getLayoutParams();
    paramBitmap1.topMargin = Math.round(paramRectF.top);
    paramBitmap1.width = Math.round(paramRectF.width());
    paramBitmap1.height = Math.round(paramRectF.height());
    getHandler().post(new Runnable()
    {
      public void run()
      {
        if (BlurPreviewCoverProducer.this.get(BlurPreviewCoverProducer.PROP_STATE) == PreviewCoverProducer.State.PREPARING)
        {
          BlurPreviewCoverProducer.-wrap1(BlurPreviewCoverProducer.this, BlurPreviewCoverProducer.PROP_STATE, PreviewCoverProducer.State.READY_TO_IN_ANIMATION);
          return;
        }
        Log.w(BlurPreviewCoverProducer.-get0(BlurPreviewCoverProducer.this), "onPreviewCoverPrepared() - Current state is " + BlurPreviewCoverProducer.this.get(BlurPreviewCoverProducer.PROP_STATE) + ", keep current state");
      }
    });
  }
  
  private void onReverseFadeOutAnimationEnd()
  {
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.READY_TO_OUT_ANIMATION);
  }
  
  private Bitmap[] processImageBitmap(Bitmap paramBitmap, RectF paramRectF)
  {
    if (this.m_RenderScript == null)
    {
      this.m_RenderScriptHandle = RenderScriptManager.createRenderScript(getCameraActivity());
      this.m_RenderScript = RenderScriptManager.getRenderScript(this.m_RenderScriptHandle);
    }
    if (this.m_RgbBlurScript == null) {
      this.m_RgbBlurScript = ScriptIntrinsicBlur.create(this.m_RenderScript, Element.RGBA_8888(this.m_RenderScript));
    }
    if (this.m_ResizeScript == null) {
      this.m_ResizeScript = ScriptIntrinsicResize.create(this.m_RenderScript);
    }
    paramRectF = Allocation.createFromBitmap(this.m_RenderScript, paramBitmap);
    int i = paramBitmap.getWidth() / 10;
    int j = paramBitmap.getHeight() / 10;
    Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
    Allocation localAllocation = Allocation.createFromBitmap(this.m_RenderScript, localBitmap);
    Object localObject = new Type.Builder(this.m_RenderScript, Element.RGBA_8888(this.m_RenderScript));
    ((Type.Builder)localObject).setX(i);
    ((Type.Builder)localObject).setY(j);
    localObject = Allocation.createTyped(this.m_RenderScript, ((Type.Builder)localObject).create(), 1);
    this.m_ResizeScript.setInput(paramRectF);
    this.m_ResizeScript.forEach_bicubic((Allocation)localObject);
    this.m_RgbBlurScript.setRadius(25.0F);
    this.m_RgbBlurScript.setInput((Allocation)localObject);
    this.m_RgbBlurScript.forEach(localAllocation);
    localAllocation.copyTo(localBitmap);
    return new Bitmap[] { paramBitmap, localBitmap };
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    paramMessage = (Object[])paramMessage.obj;
    onPreviewCoverPrepared((Bitmap)paramMessage[0], (Bitmap)paramMessage[1], (RectF)paramMessage[2]);
  }
  
  public boolean isAlphaBlending()
  {
    return true;
  }
  
  protected void onRelease()
  {
    if (this.m_ImageProcessingThread != null)
    {
      this.m_ImageProcessingHandler.post(new Runnable()
      {
        public void run()
        {
          BlurPreviewCoverProducer.-set1(BlurPreviewCoverProducer.this, Handle.close(BlurPreviewCoverProducer.-get3(BlurPreviewCoverProducer.this)));
        }
      });
      this.m_ImageProcessingThread.quitSafely();
    }
    super.onRelease();
  }
  
  protected boolean preparePreviewCover()
  {
    final Object localObject = getCameraActivity();
    if (this.m_Viewfinder == null) {
      this.m_Viewfinder = ((Viewfinder)((CameraActivity)localObject).findComponent(Viewfinder.class));
    }
    if (this.m_Viewfinder == null)
    {
      Log.e(this.TAG, "preparePreviewCover() - No viewfinder");
      return false;
    }
    localObject = (Camera)((CameraActivity)localObject).get(CameraActivity.PROP_CAMERA);
    if (localObject == null)
    {
      Log.w(this.TAG, "preparePreviewCover() - No camera");
      return false;
    }
    final RectF localRectF = (RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
    if ((this.m_Viewfinder != null) && (((Boolean)this.m_Viewfinder.get(Viewfinder.PROP_IS_DISPLAY_PREVIEW_FRAME_COPY_SUPPORTED)).booleanValue()) && (localRectF.width() > 0.0F) && (localRectF.height() > 0.0F))
    {
      final Bitmap localBitmap = Bitmap.createBitmap((int)localRectF.width(), (int)localRectF.height(), Bitmap.Config.ARGB_8888);
      if (this.m_Viewfinder.copyDisplayPreviewFrame(localBitmap, 0))
      {
        this.m_ImageProcessingHandler.post(new Runnable()
        {
          public void run()
          {
            Bitmap[] arrayOfBitmap = BlurPreviewCoverProducer.-wrap0(BlurPreviewCoverProducer.this, localBitmap, localRectF);
            HandlerUtils.sendMessage(BlurPreviewCoverProducer.this, 10001, 0, 0, new Object[] { arrayOfBitmap[0], arrayOfBitmap[1], localRectF });
          }
        });
        return true;
      }
    }
    HandlerUtils.post((HandlerObject)localObject, new Runnable()
    {
      public void run()
      {
        Log.v(BlurPreviewCoverProducer.-get0(BlurPreviewCoverProducer.this), "preparePreviewCover() - Waiting for preview frame");
        localObject.addHandler(Camera.EVENT_PREVIEW_RECEIVED, new EventHandler()
        {
          public void onEventReceived(EventSource paramAnonymous2EventSource, EventKey<CameraCaptureEventArgs> paramAnonymous2EventKey, final CameraCaptureEventArgs paramAnonymous2CameraCaptureEventArgs)
          {
            Log.v(BlurPreviewCoverProducer.-get0(BlurPreviewCoverProducer.this), "preparePreviewCover() - Preview frame received");
            final Object localObject = paramAnonymous2CameraCaptureEventArgs.getPicturePlanes();
            if ((paramAnonymous2CameraCaptureEventArgs.getPictureFormat() == 17) && (localObject != null) && (localObject.length > 0))
            {
              localObject = (byte[])localObject[0].getData().clone();
              paramAnonymous2CameraCaptureEventArgs = paramAnonymous2CameraCaptureEventArgs.getPictureSize();
              BlurPreviewCoverProducer.-get1(BlurPreviewCoverProducer.this).post(new Runnable()
              {
                public void run()
                {
                  if (BlurPreviewCoverProducer.-get2(BlurPreviewCoverProducer.this) == null)
                  {
                    BlurPreviewCoverProducer.-set1(BlurPreviewCoverProducer.this, RenderScriptManager.createRenderScript(BlurPreviewCoverProducer.this.getCameraActivity()));
                    BlurPreviewCoverProducer.-set0(BlurPreviewCoverProducer.this, RenderScriptManager.getRenderScript(BlurPreviewCoverProducer.-get3(BlurPreviewCoverProducer.this)));
                  }
                  if (BlurPreviewCoverProducer.-get4(BlurPreviewCoverProducer.this) == null) {
                    BlurPreviewCoverProducer.-set2(BlurPreviewCoverProducer.this, ScriptIntrinsicYuvToRGB.create(BlurPreviewCoverProducer.-get2(BlurPreviewCoverProducer.this), Element.U8(BlurPreviewCoverProducer.-get2(BlurPreviewCoverProducer.this))));
                  }
                  Object localObject1 = SizeUtils.getRatioStretchedSize(paramAnonymous2CameraCaptureEventArgs.getWidth(), paramAnonymous2CameraCaptureEventArgs.getHeight(), (int)this.val$previewBounds.height(), (int)this.val$previewBounds.width(), true);
                  int j = ((Size)localObject1).getWidth();
                  int k = ((Size)localObject1).getHeight();
                  int i = j;
                  if (j % 2 != 0) {
                    i = j + 1;
                  }
                  j = k;
                  if (k % 2 != 0) {
                    j = k + 1;
                  }
                  Object localObject2 = new Size(i, j);
                  localObject1 = ImageUtils.rotateNV21Image(ImageUtils.scaleNV21Image(localObject, paramAnonymous2CameraCaptureEventArgs.getWidth(), paramAnonymous2CameraCaptureEventArgs.getHeight(), ((Size)localObject2).getWidth(), ((Size)localObject2).getHeight()), ((Size)localObject2).getWidth(), ((Size)localObject2).getHeight(), 90);
                  Bitmap localBitmap = Bitmap.createBitmap(((Size)localObject2).getHeight(), ((Size)localObject2).getWidth(), Bitmap.Config.ARGB_8888);
                  localObject2 = Allocation.createSized(BlurPreviewCoverProducer.-get2(BlurPreviewCoverProducer.this), Element.U8(BlurPreviewCoverProducer.-get2(BlurPreviewCoverProducer.this)), ((Size)localObject2).getWidth() * ((Size)localObject2).getHeight() * 3 / 2);
                  Allocation localAllocation = Allocation.createFromBitmap(BlurPreviewCoverProducer.-get2(BlurPreviewCoverProducer.this), localBitmap);
                  ((Allocation)localObject2).copyFrom((byte[])localObject1);
                  BlurPreviewCoverProducer.-get4(BlurPreviewCoverProducer.this).setInput((Allocation)localObject2);
                  BlurPreviewCoverProducer.-get4(BlurPreviewCoverProducer.this).forEach(localAllocation);
                  localAllocation.copyTo(localBitmap);
                  Log.v(BlurPreviewCoverProducer.-get0(BlurPreviewCoverProducer.this), "preparePreviewCover() - YUV -> RGBA");
                  ((Allocation)localObject2).destroy();
                  localAllocation.destroy();
                  localObject1 = localBitmap;
                  if (this.val$camera.get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT)
                  {
                    localObject1 = new Matrix();
                    ((Matrix)localObject1).postScale(1.0F, -1.0F);
                    localObject1 = Bitmap.createBitmap(localBitmap, 0, 0, localBitmap.getWidth(), localBitmap.getHeight(), (Matrix)localObject1, false);
                  }
                  Log.v(BlurPreviewCoverProducer.-get0(BlurPreviewCoverProducer.this), "preparePreviewCover() - Ready to process");
                  localObject1 = BlurPreviewCoverProducer.-wrap0(BlurPreviewCoverProducer.this, (Bitmap)localObject1, this.val$previewBounds);
                  Log.v(BlurPreviewCoverProducer.-get0(BlurPreviewCoverProducer.this), "preparePreviewCover() - Processed");
                  HandlerUtils.sendMessage(BlurPreviewCoverProducer.this, 10001, 0, 0, new Object[] { localObject1[0], localObject1[1], this.val$previewBounds });
                }
              });
            }
            for (;;)
            {
              paramAnonymous2EventSource.removeHandler(paramAnonymous2EventKey, this);
              return;
              Log.e(BlurPreviewCoverProducer.-get0(BlurPreviewCoverProducer.this), "preparePreviewCover() - Invalid preview frame data");
              HandlerUtils.sendMessage(BlurPreviewCoverProducer.this, 10001, 0, 0, new Object[] { null, null, this.val$previewBounds });
            }
          }
        });
      }
    });
    return true;
  }
  
  protected void reverseInAnimation() {}
  
  protected void reverseOutAnimation()
  {
    if (get(PROP_STATE) != PreviewCoverProducer.State.OUT_ANIMATION) {
      return;
    }
    Log.v(this.TAG, "reverseOutAnimation()");
    View localView = getPreviewCoverContainer();
    localView.animate().cancel();
    localView.animate().alpha(1.0F).setDuration(80L).withEndAction(this.m_OnReverseFadeOutAnimationEndRunnable);
  }
  
  protected void startInAnimation(boolean paramBoolean)
  {
    if (get(PROP_STATE) != PreviewCoverProducer.State.READY_TO_IN_ANIMATION) {
      return;
    }
    if (!paramBoolean)
    {
      this.m_OnFadeInAnimationEndRunnable.run();
      return;
    }
    Log.v(this.TAG, "startInAnimation()");
    View localView1 = getPreviewCoverContainer();
    View localView2 = getPreviewCoverBackgroundView();
    ImageView localImageView1 = getPreviewCoverBaseImageView();
    ImageView localImageView2 = getPreviewCoverTopImageView();
    localView1.setVisibility(0);
    localView1.setAlpha(0.0F);
    localView2.setVisibility(0);
    localView2.setAlpha(1.0F);
    localImageView1.setVisibility(0);
    localImageView2.setVisibility(0);
    localView1.animate().alpha(1.0F).setDuration(80L).withStartAction(this.m_OnFadeInAnimationStartRunnable).withEndAction(this.m_OnFadeInAnimationEndRunnable);
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.IN_ANIMATION);
  }
  
  protected void startOutAnimation(boolean paramBoolean)
  {
    if (get(PROP_STATE) != PreviewCoverProducer.State.READY_TO_OUT_ANIMATION) {
      return;
    }
    if (!paramBoolean)
    {
      this.m_OnFadeOutAnimationEndRunnable.run();
      return;
    }
    Log.v(this.TAG, "startOutAnimation()");
    getPreviewCoverContainer().animate().alpha(0.0F).setDuration(80L).withStartAction(this.m_OnFadeOutAnimationStartRunnable).withEndAction(this.m_OnFadeOutAnimationEndRunnable);
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.OUT_ANIMATION);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/BlurPreviewCoverProducer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */