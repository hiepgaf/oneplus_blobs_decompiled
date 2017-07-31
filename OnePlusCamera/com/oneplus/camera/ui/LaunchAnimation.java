package com.oneplus.camera.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.RectF;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicResize;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import com.oneplus.base.BaseActivity.State;
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
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraCaptureEventArgs;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.media.ImagePlane;
import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

final class LaunchAnimation
  extends CameraComponent
{
  private static final long DURATION_CREATE_PREVIEW_THUMB = 10000L;
  private static final long DURATION_FADE_OUT_ANIMATION = 300L;
  private static final int MSG_CREATE_PREVIEW_THUMB = 10001;
  private static final int MSG_PREVIEW_THUMB_CREATED = 10000;
  private static final int RADIUS_RESIZED_PREVIEW_FRAME_BLUR = 20;
  private static final float RATIO_RESIZED_PREVIEW_FRAME = 0.1F;
  private static final Executor m_BackgroundExecutor = Executors.newFixedThreadPool(1);
  private static Bitmap m_CachedPreviewThumbnailImage;
  private static volatile File m_PreviewFrameCacheFile;
  private ScriptIntrinsicBlur m_BlurScript;
  private Allocation m_BluredPreviewRgbaAllocation;
  private Bitmap m_BluredPreviewThumbnailImage;
  private final EventHandler<CameraCaptureEventArgs> m_CameraPreviewReceivedHandler = new EventHandler()
  {
    public void onEventReceived(final EventSource paramAnonymousEventSource, final EventKey<CameraCaptureEventArgs> paramAnonymousEventKey, CameraCaptureEventArgs paramAnonymousCameraCaptureEventArgs)
    {
      paramAnonymousEventSource.removeHandler(Camera.EVENT_PREVIEW_RECEIVED, this);
      if (paramAnonymousCameraCaptureEventArgs.getPictureFormat() != 17)
      {
        Log.w(LaunchAnimation.-get0(LaunchAnimation.this), "Unknown preview format : " + paramAnonymousCameraCaptureEventArgs.getPictureFormat());
        return;
      }
      paramAnonymousEventKey = paramAnonymousCameraCaptureEventArgs.getPicturePlanes();
      if ((paramAnonymousEventKey == null) || (paramAnonymousEventKey.length != 1))
      {
        Log.w(LaunchAnimation.-get0(LaunchAnimation.this), "Incorrect image plane");
        return;
      }
      paramAnonymousEventSource = (Camera)paramAnonymousEventSource;
      paramAnonymousEventKey = (byte[])paramAnonymousEventKey[0].getData().clone();
      final int i = paramAnonymousCameraCaptureEventArgs.getPictureSize().getWidth();
      final int j = paramAnonymousCameraCaptureEventArgs.getPictureSize().getHeight();
      Log.v(LaunchAnimation.-get0(LaunchAnimation.this), "Preview frame received");
      LaunchAnimation.-get1().execute(new Runnable()
      {
        public void run()
        {
          LaunchAnimation.-wrap3(LaunchAnimation.this, paramAnonymousEventSource, paramAnonymousEventKey, i, j);
        }
      });
    }
  };
  private View m_Container;
  private final Runnable m_GetPreviewFrameRunnable = new Runnable()
  {
    public void run()
    {
      Camera localCamera = (Camera)LaunchAnimation.this.getCameraThread().get(CameraThread.PROP_CAMERA);
      if (localCamera != null)
      {
        Log.v(LaunchAnimation.-get0(LaunchAnimation.this), "Start waiting preview frame");
        localCamera.addHandler(Camera.EVENT_PREVIEW_RECEIVED, LaunchAnimation.-get2(LaunchAnimation.this));
        return;
      }
      Log.w(LaunchAnimation.-get0(LaunchAnimation.this), "No camera to get preview frame");
    }
  };
  private boolean m_IsVisible = true;
  private final Runnable m_LoadPreviewThumbRunnable = new Runnable()
  {
    public void run()
    {
      LaunchAnimation.-wrap0(LaunchAnimation.this);
    }
  };
  private ImageView m_PreviewFrameView;
  private Allocation m_PreviewRgbaAllocation;
  private Allocation m_PreviewYuvAllocation;
  private RenderScript m_RenderScript;
  private Handle m_RenderScriptHandle;
  private ScriptIntrinsicResize m_ResizeScript;
  private Allocation m_ResizedPreviewRgbaAllocation;
  private final Runnable m_SetupRunnable = new Runnable()
  {
    public void run()
    {
      LaunchAnimation.-wrap6(LaunchAnimation.this);
    }
  };
  private ScriptIntrinsicYuvToRGB m_YuvToRgbScript;
  
  LaunchAnimation(CameraActivity paramCameraActivity)
  {
    super("Launch animation", paramCameraActivity, true);
  }
  
  private void createPreviewThumbnailImage()
  {
    if (!((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_RUNNING)).booleanValue()) {
      return;
    }
    HandlerUtils.sendMessage(this, 10001, true, 10000L);
    CameraActivity localCameraActivity = getCameraActivity();
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[getMediaType().ordinal()])
    {
    default: 
      HandlerUtils.post(getCameraThread(), this.m_GetPreviewFrameRunnable);
      return;
    case 1: 
      switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((com.oneplus.camera.PhotoCaptureState)localCameraActivity.get(CameraActivity.PROP_PHOTO_CAPTURE_STATE)).ordinal()])
      {
      }
      return;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((com.oneplus.camera.VideoCaptureState)localCameraActivity.get(CameraActivity.PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    }
  }
  
  private void loadPreviewThumbnail()
  {
    try
    {
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
      HandlerUtils.sendMessage(this, 10000, BitmapFactory.decodeFile(m_PreviewFrameCacheFile.getAbsolutePath(), localOptions));
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "loadPreviewThumbnail() - Fail to load preview thumbnail", localThrowable);
    }
  }
  
  private void onCameraPreviewReceived()
  {
    HandlerUtils.sendMessage(this, 10001, true, 500L);
    if ((!this.m_IsVisible) || (this.m_Container == null)) {
      return;
    }
    this.m_IsVisible = false;
    this.m_Container.animate().alpha(0.0F).setDuration(300L).withEndAction(new Runnable()
    {
      public void run()
      {
        LaunchAnimation.-get3(LaunchAnimation.this).setVisibility(8);
      }
    }).start();
  }
  
  private void onPreviewBoundsChanged(RectF paramRectF)
  {
    if ((this.m_PreviewFrameView == null) || (paramRectF == null)) {
      return;
    }
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.m_PreviewFrameView.getLayoutParams();
    localMarginLayoutParams.leftMargin = ((int)paramRectF.left);
    localMarginLayoutParams.topMargin = ((int)paramRectF.top);
    localMarginLayoutParams.width = Math.round(paramRectF.width());
    localMarginLayoutParams.height = Math.round(paramRectF.height());
    this.m_PreviewFrameView.requestLayout();
    this.m_PreviewFrameView.setVisibility(0);
  }
  
  private void onPreviewThumbnailImageCreated(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      return;
    }
    m_CachedPreviewThumbnailImage = paramBitmap;
    if (this.m_PreviewFrameView != null) {
      this.m_PreviewFrameView.setImageBitmap(paramBitmap);
    }
  }
  
  /* Error */
  private void processPreviewFrame(Camera paramCamera, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 90	com/oneplus/camera/ui/LaunchAnimation:TAG	Ljava/lang/String;
    //   4: ldc_w 426
    //   7: invokestatic 430	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   10: aload_0
    //   11: getfield 432	com/oneplus/camera/ui/LaunchAnimation:m_RenderScriptHandle	Lcom/oneplus/base/Handle;
    //   14: invokestatic 438	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   17: ifne +42 -> 59
    //   20: aload_0
    //   21: aload_0
    //   22: invokevirtual 257	com/oneplus/camera/ui/LaunchAnimation:getCameraActivity	()Lcom/oneplus/camera/CameraActivity;
    //   25: invokestatic 444	com/oneplus/renderscript/RenderScriptManager:createRenderScript	(Landroid/content/Context;)Lcom/oneplus/base/Handle;
    //   28: putfield 432	com/oneplus/camera/ui/LaunchAnimation:m_RenderScriptHandle	Lcom/oneplus/base/Handle;
    //   31: aload_0
    //   32: aload_0
    //   33: getfield 432	com/oneplus/camera/ui/LaunchAnimation:m_RenderScriptHandle	Lcom/oneplus/base/Handle;
    //   36: invokestatic 448	com/oneplus/renderscript/RenderScriptManager:getRenderScript	(Lcom/oneplus/base/Handle;)Landroid/renderscript/RenderScript;
    //   39: putfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   42: aload_0
    //   43: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   46: ifnull +713 -> 759
    //   49: aload_0
    //   50: getfield 90	com/oneplus/camera/ui/LaunchAnimation:TAG	Ljava/lang/String;
    //   53: ldc_w 452
    //   56: invokestatic 430	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   59: aload_0
    //   60: getfield 454	com/oneplus/camera/ui/LaunchAnimation:m_YuvToRgbScript	Landroid/renderscript/ScriptIntrinsicYuvToRGB;
    //   63: ifnonnull +21 -> 84
    //   66: aload_0
    //   67: aload_0
    //   68: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   71: aload_0
    //   72: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   75: invokestatic 460	android/renderscript/Element:U8	(Landroid/renderscript/RenderScript;)Landroid/renderscript/Element;
    //   78: invokestatic 466	android/renderscript/ScriptIntrinsicYuvToRGB:create	(Landroid/renderscript/RenderScript;Landroid/renderscript/Element;)Landroid/renderscript/ScriptIntrinsicYuvToRGB;
    //   81: putfield 454	com/oneplus/camera/ui/LaunchAnimation:m_YuvToRgbScript	Landroid/renderscript/ScriptIntrinsicYuvToRGB;
    //   84: aload_0
    //   85: getfield 468	com/oneplus/camera/ui/LaunchAnimation:m_ResizeScript	Landroid/renderscript/ScriptIntrinsicResize;
    //   88: ifnonnull +14 -> 102
    //   91: aload_0
    //   92: aload_0
    //   93: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   96: invokestatic 473	android/renderscript/ScriptIntrinsicResize:create	(Landroid/renderscript/RenderScript;)Landroid/renderscript/ScriptIntrinsicResize;
    //   99: putfield 468	com/oneplus/camera/ui/LaunchAnimation:m_ResizeScript	Landroid/renderscript/ScriptIntrinsicResize;
    //   102: aload_0
    //   103: getfield 475	com/oneplus/camera/ui/LaunchAnimation:m_BlurScript	Landroid/renderscript/ScriptIntrinsicBlur;
    //   106: ifnonnull +31 -> 137
    //   109: aload_0
    //   110: aload_0
    //   111: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   114: aload_0
    //   115: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   118: invokestatic 478	android/renderscript/Element:RGBA_8888	(Landroid/renderscript/RenderScript;)Landroid/renderscript/Element;
    //   121: invokestatic 483	android/renderscript/ScriptIntrinsicBlur:create	(Landroid/renderscript/RenderScript;Landroid/renderscript/Element;)Landroid/renderscript/ScriptIntrinsicBlur;
    //   124: putfield 475	com/oneplus/camera/ui/LaunchAnimation:m_BlurScript	Landroid/renderscript/ScriptIntrinsicBlur;
    //   127: aload_0
    //   128: getfield 475	com/oneplus/camera/ui/LaunchAnimation:m_BlurScript	Landroid/renderscript/ScriptIntrinsicBlur;
    //   131: ldc_w 484
    //   134: invokevirtual 488	android/renderscript/ScriptIntrinsicBlur:setRadius	(F)V
    //   137: aload_0
    //   138: getfield 490	com/oneplus/camera/ui/LaunchAnimation:m_PreviewYuvAllocation	Landroid/renderscript/Allocation;
    //   141: ifnull +36 -> 177
    //   144: aload_0
    //   145: getfield 490	com/oneplus/camera/ui/LaunchAnimation:m_PreviewYuvAllocation	Landroid/renderscript/Allocation;
    //   148: invokevirtual 496	android/renderscript/Allocation:getType	()Landroid/renderscript/Type;
    //   151: invokevirtual 501	android/renderscript/Type:getX	()I
    //   154: iload_3
    //   155: iload 4
    //   157: imul
    //   158: iconst_3
    //   159: imul
    //   160: iconst_2
    //   161: idiv
    //   162: if_icmpeq +15 -> 177
    //   165: aload_0
    //   166: getfield 490	com/oneplus/camera/ui/LaunchAnimation:m_PreviewYuvAllocation	Landroid/renderscript/Allocation;
    //   169: invokevirtual 504	android/renderscript/Allocation:destroy	()V
    //   172: aload_0
    //   173: aconst_null
    //   174: putfield 490	com/oneplus/camera/ui/LaunchAnimation:m_PreviewYuvAllocation	Landroid/renderscript/Allocation;
    //   177: aload_0
    //   178: getfield 490	com/oneplus/camera/ui/LaunchAnimation:m_PreviewYuvAllocation	Landroid/renderscript/Allocation;
    //   181: ifnonnull +40 -> 221
    //   184: aload_0
    //   185: aload_0
    //   186: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   189: aload_0
    //   190: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   193: invokestatic 460	android/renderscript/Element:U8	(Landroid/renderscript/RenderScript;)Landroid/renderscript/Element;
    //   196: iload_3
    //   197: iload 4
    //   199: imul
    //   200: iconst_3
    //   201: imul
    //   202: iconst_2
    //   203: idiv
    //   204: invokestatic 508	android/renderscript/Allocation:createSized	(Landroid/renderscript/RenderScript;Landroid/renderscript/Element;I)Landroid/renderscript/Allocation;
    //   207: putfield 490	com/oneplus/camera/ui/LaunchAnimation:m_PreviewYuvAllocation	Landroid/renderscript/Allocation;
    //   210: aload_0
    //   211: getfield 454	com/oneplus/camera/ui/LaunchAnimation:m_YuvToRgbScript	Landroid/renderscript/ScriptIntrinsicYuvToRGB;
    //   214: aload_0
    //   215: getfield 490	com/oneplus/camera/ui/LaunchAnimation:m_PreviewYuvAllocation	Landroid/renderscript/Allocation;
    //   218: invokevirtual 512	android/renderscript/ScriptIntrinsicYuvToRGB:setInput	(Landroid/renderscript/Allocation;)V
    //   221: aload_0
    //   222: getfield 514	com/oneplus/camera/ui/LaunchAnimation:m_PreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   225: ifnull +44 -> 269
    //   228: aload_0
    //   229: getfield 514	com/oneplus/camera/ui/LaunchAnimation:m_PreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   232: invokevirtual 496	android/renderscript/Allocation:getType	()Landroid/renderscript/Type;
    //   235: invokevirtual 501	android/renderscript/Type:getX	()I
    //   238: iload_3
    //   239: if_icmpne +18 -> 257
    //   242: aload_0
    //   243: getfield 514	com/oneplus/camera/ui/LaunchAnimation:m_PreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   246: invokevirtual 496	android/renderscript/Allocation:getType	()Landroid/renderscript/Type;
    //   249: invokevirtual 517	android/renderscript/Type:getY	()I
    //   252: iload 4
    //   254: if_icmpeq +15 -> 269
    //   257: aload_0
    //   258: getfield 514	com/oneplus/camera/ui/LaunchAnimation:m_PreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   261: invokevirtual 504	android/renderscript/Allocation:destroy	()V
    //   264: aload_0
    //   265: aconst_null
    //   266: putfield 514	com/oneplus/camera/ui/LaunchAnimation:m_PreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   269: aload_0
    //   270: getfield 514	com/oneplus/camera/ui/LaunchAnimation:m_PreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   273: ifnonnull +62 -> 335
    //   276: new 519	android/renderscript/Type$Builder
    //   279: dup
    //   280: aload_0
    //   281: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   284: aload_0
    //   285: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   288: invokestatic 478	android/renderscript/Element:RGBA_8888	(Landroid/renderscript/RenderScript;)Landroid/renderscript/Element;
    //   291: invokespecial 522	android/renderscript/Type$Builder:<init>	(Landroid/renderscript/RenderScript;Landroid/renderscript/Element;)V
    //   294: astore 5
    //   296: aload 5
    //   298: iload_3
    //   299: invokevirtual 526	android/renderscript/Type$Builder:setX	(I)Landroid/renderscript/Type$Builder;
    //   302: iload 4
    //   304: invokevirtual 529	android/renderscript/Type$Builder:setY	(I)Landroid/renderscript/Type$Builder;
    //   307: pop
    //   308: aload_0
    //   309: aload_0
    //   310: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   313: aload 5
    //   315: invokevirtual 531	android/renderscript/Type$Builder:create	()Landroid/renderscript/Type;
    //   318: invokestatic 535	android/renderscript/Allocation:createTyped	(Landroid/renderscript/RenderScript;Landroid/renderscript/Type;)Landroid/renderscript/Allocation;
    //   321: putfield 514	com/oneplus/camera/ui/LaunchAnimation:m_PreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   324: aload_0
    //   325: getfield 468	com/oneplus/camera/ui/LaunchAnimation:m_ResizeScript	Landroid/renderscript/ScriptIntrinsicResize;
    //   328: aload_0
    //   329: getfield 514	com/oneplus/camera/ui/LaunchAnimation:m_PreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   332: invokevirtual 536	android/renderscript/ScriptIntrinsicResize:setInput	(Landroid/renderscript/Allocation;)V
    //   335: aload_0
    //   336: getfield 490	com/oneplus/camera/ui/LaunchAnimation:m_PreviewYuvAllocation	Landroid/renderscript/Allocation;
    //   339: aload_2
    //   340: invokevirtual 540	android/renderscript/Allocation:copyFrom	([B)V
    //   343: aload_0
    //   344: getfield 454	com/oneplus/camera/ui/LaunchAnimation:m_YuvToRgbScript	Landroid/renderscript/ScriptIntrinsicYuvToRGB;
    //   347: aload_0
    //   348: getfield 514	com/oneplus/camera/ui/LaunchAnimation:m_PreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   351: invokevirtual 543	android/renderscript/ScriptIntrinsicYuvToRGB:forEach	(Landroid/renderscript/Allocation;)V
    //   354: iconst_1
    //   355: iload_3
    //   356: i2f
    //   357: ldc 49
    //   359: fmul
    //   360: f2i
    //   361: invokestatic 547	java/lang/Math:max	(II)I
    //   364: istore_3
    //   365: iconst_1
    //   366: iload 4
    //   368: i2f
    //   369: ldc 49
    //   371: fmul
    //   372: f2i
    //   373: invokestatic 547	java/lang/Math:max	(II)I
    //   376: istore 4
    //   378: aload_0
    //   379: getfield 549	com/oneplus/camera/ui/LaunchAnimation:m_ResizedPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   382: ifnull +44 -> 426
    //   385: aload_0
    //   386: getfield 549	com/oneplus/camera/ui/LaunchAnimation:m_ResizedPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   389: invokevirtual 496	android/renderscript/Allocation:getType	()Landroid/renderscript/Type;
    //   392: invokevirtual 501	android/renderscript/Type:getX	()I
    //   395: iload_3
    //   396: if_icmpne +18 -> 414
    //   399: aload_0
    //   400: getfield 549	com/oneplus/camera/ui/LaunchAnimation:m_ResizedPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   403: invokevirtual 496	android/renderscript/Allocation:getType	()Landroid/renderscript/Type;
    //   406: invokevirtual 517	android/renderscript/Type:getY	()I
    //   409: iload 4
    //   411: if_icmpeq +15 -> 426
    //   414: aload_0
    //   415: getfield 549	com/oneplus/camera/ui/LaunchAnimation:m_ResizedPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   418: invokevirtual 504	android/renderscript/Allocation:destroy	()V
    //   421: aload_0
    //   422: aconst_null
    //   423: putfield 549	com/oneplus/camera/ui/LaunchAnimation:m_ResizedPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   426: aload_0
    //   427: getfield 549	com/oneplus/camera/ui/LaunchAnimation:m_ResizedPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   430: ifnonnull +59 -> 489
    //   433: new 519	android/renderscript/Type$Builder
    //   436: dup
    //   437: aload_0
    //   438: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   441: aload_0
    //   442: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   445: invokestatic 478	android/renderscript/Element:RGBA_8888	(Landroid/renderscript/RenderScript;)Landroid/renderscript/Element;
    //   448: invokespecial 522	android/renderscript/Type$Builder:<init>	(Landroid/renderscript/RenderScript;Landroid/renderscript/Element;)V
    //   451: astore_2
    //   452: aload_2
    //   453: iload_3
    //   454: invokevirtual 526	android/renderscript/Type$Builder:setX	(I)Landroid/renderscript/Type$Builder;
    //   457: iload 4
    //   459: invokevirtual 529	android/renderscript/Type$Builder:setY	(I)Landroid/renderscript/Type$Builder;
    //   462: pop
    //   463: aload_0
    //   464: aload_0
    //   465: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   468: aload_2
    //   469: invokevirtual 531	android/renderscript/Type$Builder:create	()Landroid/renderscript/Type;
    //   472: invokestatic 535	android/renderscript/Allocation:createTyped	(Landroid/renderscript/RenderScript;Landroid/renderscript/Type;)Landroid/renderscript/Allocation;
    //   475: putfield 549	com/oneplus/camera/ui/LaunchAnimation:m_ResizedPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   478: aload_0
    //   479: getfield 475	com/oneplus/camera/ui/LaunchAnimation:m_BlurScript	Landroid/renderscript/ScriptIntrinsicBlur;
    //   482: aload_0
    //   483: getfield 549	com/oneplus/camera/ui/LaunchAnimation:m_ResizedPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   486: invokevirtual 550	android/renderscript/ScriptIntrinsicBlur:setInput	(Landroid/renderscript/Allocation;)V
    //   489: aload_0
    //   490: getfield 468	com/oneplus/camera/ui/LaunchAnimation:m_ResizeScript	Landroid/renderscript/ScriptIntrinsicResize;
    //   493: aload_0
    //   494: getfield 549	com/oneplus/camera/ui/LaunchAnimation:m_ResizedPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   497: invokevirtual 553	android/renderscript/ScriptIntrinsicResize:forEach_bicubic	(Landroid/renderscript/Allocation;)V
    //   500: aload_0
    //   501: getfield 555	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewThumbnailImage	Landroid/graphics/Bitmap;
    //   504: ifnull +26 -> 530
    //   507: aload_0
    //   508: getfield 555	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewThumbnailImage	Landroid/graphics/Bitmap;
    //   511: invokevirtual 560	android/graphics/Bitmap:getWidth	()I
    //   514: iload_3
    //   515: if_icmpne +15 -> 530
    //   518: aload_0
    //   519: getfield 555	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewThumbnailImage	Landroid/graphics/Bitmap;
    //   522: invokevirtual 563	android/graphics/Bitmap:getHeight	()I
    //   525: iload 4
    //   527: if_icmpeq +45 -> 572
    //   530: aload_0
    //   531: iload_3
    //   532: iload 4
    //   534: getstatic 315	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   537: invokestatic 567	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   540: putfield 555	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewThumbnailImage	Landroid/graphics/Bitmap;
    //   543: aload_0
    //   544: getfield 569	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   547: ifnull +10 -> 557
    //   550: aload_0
    //   551: getfield 569	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   554: invokevirtual 504	android/renderscript/Allocation:destroy	()V
    //   557: aload_0
    //   558: aload_0
    //   559: getfield 450	com/oneplus/camera/ui/LaunchAnimation:m_RenderScript	Landroid/renderscript/RenderScript;
    //   562: aload_0
    //   563: getfield 555	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewThumbnailImage	Landroid/graphics/Bitmap;
    //   566: invokestatic 573	android/renderscript/Allocation:createFromBitmap	(Landroid/renderscript/RenderScript;Landroid/graphics/Bitmap;)Landroid/renderscript/Allocation;
    //   569: putfield 569	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   572: aload_0
    //   573: getfield 475	com/oneplus/camera/ui/LaunchAnimation:m_BlurScript	Landroid/renderscript/ScriptIntrinsicBlur;
    //   576: aload_0
    //   577: getfield 569	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   580: invokevirtual 574	android/renderscript/ScriptIntrinsicBlur:forEach	(Landroid/renderscript/Allocation;)V
    //   583: aload_0
    //   584: getfield 569	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewRgbaAllocation	Landroid/renderscript/Allocation;
    //   587: aload_0
    //   588: getfield 555	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewThumbnailImage	Landroid/graphics/Bitmap;
    //   591: invokevirtual 577	android/renderscript/Allocation:copyTo	(Landroid/graphics/Bitmap;)V
    //   594: aload_0
    //   595: getfield 90	com/oneplus/camera/ui/LaunchAnimation:TAG	Ljava/lang/String;
    //   598: ldc_w 579
    //   601: invokestatic 430	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   604: aconst_null
    //   605: astore_2
    //   606: aload_0
    //   607: getfield 555	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewThumbnailImage	Landroid/graphics/Bitmap;
    //   610: ifnull +78 -> 688
    //   613: new 581	android/graphics/Matrix
    //   616: dup
    //   617: invokespecial 582	android/graphics/Matrix:<init>	()V
    //   620: astore_2
    //   621: aload_2
    //   622: ldc_w 583
    //   625: invokevirtual 587	android/graphics/Matrix:postRotate	(F)Z
    //   628: pop
    //   629: aload_1
    //   630: getstatic 592	com/oneplus/camera/Camera:PROP_LENS_FACING	Lcom/oneplus/base/PropertyKey;
    //   633: invokeinterface 593 2 0
    //   638: getstatic 599	com/oneplus/camera/Camera$LensFacing:FRONT	Lcom/oneplus/camera/Camera$LensFacing;
    //   641: if_acmpne +12 -> 653
    //   644: aload_2
    //   645: fconst_1
    //   646: ldc_w 600
    //   649: invokevirtual 604	android/graphics/Matrix:postScale	(FF)Z
    //   652: pop
    //   653: aload_0
    //   654: getfield 555	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewThumbnailImage	Landroid/graphics/Bitmap;
    //   657: iconst_0
    //   658: iconst_0
    //   659: aload_0
    //   660: getfield 555	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewThumbnailImage	Landroid/graphics/Bitmap;
    //   663: invokevirtual 560	android/graphics/Bitmap:getWidth	()I
    //   666: aload_0
    //   667: getfield 555	com/oneplus/camera/ui/LaunchAnimation:m_BluredPreviewThumbnailImage	Landroid/graphics/Bitmap;
    //   670: invokevirtual 563	android/graphics/Bitmap:getHeight	()I
    //   673: aload_2
    //   674: iconst_0
    //   675: invokestatic 607	android/graphics/Bitmap:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   678: astore_2
    //   679: aload_0
    //   680: sipush 10000
    //   683: aload_2
    //   684: invokestatic 335	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
    //   687: pop
    //   688: aload_2
    //   689: ifnull +69 -> 758
    //   692: aconst_null
    //   693: astore 6
    //   695: aconst_null
    //   696: astore 7
    //   698: aconst_null
    //   699: astore 5
    //   701: aconst_null
    //   702: astore 8
    //   704: new 609	java/io/FileOutputStream
    //   707: dup
    //   708: getstatic 320	com/oneplus/camera/ui/LaunchAnimation:m_PreviewFrameCacheFile	Ljava/io/File;
    //   711: invokespecial 612	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   714: astore_1
    //   715: aload_2
    //   716: getstatic 618	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   719: bipush 75
    //   721: aload_1
    //   722: invokevirtual 622	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   725: pop
    //   726: aload 7
    //   728: astore_2
    //   729: aload_1
    //   730: ifnull +10 -> 740
    //   733: aload_1
    //   734: invokevirtual 625	java/io/FileOutputStream:close	()V
    //   737: aload 7
    //   739: astore_2
    //   740: aload_2
    //   741: ifnull +17 -> 758
    //   744: aload_2
    //   745: athrow
    //   746: astore_1
    //   747: aload_0
    //   748: getfield 90	com/oneplus/camera/ui/LaunchAnimation:TAG	Ljava/lang/String;
    //   751: ldc_w 627
    //   754: aload_1
    //   755: invokestatic 343	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   758: return
    //   759: aload_0
    //   760: getfield 90	com/oneplus/camera/ui/LaunchAnimation:TAG	Ljava/lang/String;
    //   763: ldc_w 629
    //   766: invokestatic 631	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   769: return
    //   770: astore_1
    //   771: aload_0
    //   772: getfield 90	com/oneplus/camera/ui/LaunchAnimation:TAG	Ljava/lang/String;
    //   775: ldc_w 633
    //   778: aload_1
    //   779: invokestatic 343	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   782: return
    //   783: astore_2
    //   784: goto -44 -> 740
    //   787: astore_2
    //   788: aload 8
    //   790: astore_1
    //   791: aload_2
    //   792: athrow
    //   793: astore 6
    //   795: aload_1
    //   796: astore 5
    //   798: aload 6
    //   800: astore_1
    //   801: aload_2
    //   802: astore 6
    //   804: aload 5
    //   806: ifnull +11 -> 817
    //   809: aload 5
    //   811: invokevirtual 625	java/io/FileOutputStream:close	()V
    //   814: aload_2
    //   815: astore 6
    //   817: aload 6
    //   819: ifnull +27 -> 846
    //   822: aload 6
    //   824: athrow
    //   825: aload_2
    //   826: astore 6
    //   828: aload_2
    //   829: aload 5
    //   831: if_acmpeq -14 -> 817
    //   834: aload_2
    //   835: aload 5
    //   837: invokevirtual 637	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   840: aload_2
    //   841: astore 6
    //   843: goto -26 -> 817
    //   846: aload_1
    //   847: athrow
    //   848: astore_1
    //   849: aload 6
    //   851: astore_2
    //   852: goto -51 -> 801
    //   855: astore_2
    //   856: aload_1
    //   857: astore 5
    //   859: aload_2
    //   860: astore_1
    //   861: aload 6
    //   863: astore_2
    //   864: goto -63 -> 801
    //   867: astore_2
    //   868: goto -77 -> 791
    //   871: astore_1
    //   872: goto -125 -> 747
    //   875: astore 5
    //   877: aload_2
    //   878: ifnonnull -53 -> 825
    //   881: aload 5
    //   883: astore 6
    //   885: goto -68 -> 817
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	888	0	this	LaunchAnimation
    //   0	888	1	paramCamera	Camera
    //   0	888	2	paramArrayOfByte	byte[]
    //   0	888	3	paramInt1	int
    //   0	888	4	paramInt2	int
    //   294	564	5	localObject1	Object
    //   875	7	5	localThrowable	Throwable
    //   693	1	6	localObject2	Object
    //   793	6	6	localObject3	Object
    //   802	82	6	localObject4	Object
    //   696	42	7	localObject5	Object
    //   702	87	8	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   744	746	746	java/lang/Throwable
    //   0	59	770	java/lang/Throwable
    //   59	84	770	java/lang/Throwable
    //   84	102	770	java/lang/Throwable
    //   102	137	770	java/lang/Throwable
    //   137	177	770	java/lang/Throwable
    //   177	221	770	java/lang/Throwable
    //   221	257	770	java/lang/Throwable
    //   257	269	770	java/lang/Throwable
    //   269	335	770	java/lang/Throwable
    //   335	414	770	java/lang/Throwable
    //   414	426	770	java/lang/Throwable
    //   426	489	770	java/lang/Throwable
    //   489	530	770	java/lang/Throwable
    //   530	557	770	java/lang/Throwable
    //   557	572	770	java/lang/Throwable
    //   572	604	770	java/lang/Throwable
    //   759	769	770	java/lang/Throwable
    //   733	737	783	java/lang/Throwable
    //   704	715	787	java/lang/Throwable
    //   791	793	793	finally
    //   704	715	848	finally
    //   715	726	855	finally
    //   715	726	867	java/lang/Throwable
    //   822	825	871	java/lang/Throwable
    //   834	840	871	java/lang/Throwable
    //   846	848	871	java/lang/Throwable
    //   809	814	875	java/lang/Throwable
  }
  
  private void releaseRenderScript()
  {
    if (this.m_YuvToRgbScript != null)
    {
      this.m_YuvToRgbScript.destroy();
      this.m_YuvToRgbScript = null;
    }
    if (this.m_ResizeScript != null)
    {
      this.m_ResizeScript.destroy();
      this.m_ResizeScript = null;
    }
    if (this.m_BlurScript != null)
    {
      this.m_BlurScript.destroy();
      this.m_BlurScript = null;
    }
    if (this.m_PreviewYuvAllocation != null)
    {
      this.m_PreviewYuvAllocation.destroy();
      this.m_PreviewYuvAllocation = null;
    }
    if (this.m_PreviewRgbaAllocation != null)
    {
      this.m_PreviewRgbaAllocation.destroy();
      this.m_PreviewRgbaAllocation = null;
    }
    if (this.m_ResizedPreviewRgbaAllocation != null)
    {
      this.m_ResizedPreviewRgbaAllocation.destroy();
      this.m_ResizedPreviewRgbaAllocation = null;
    }
    if (this.m_BluredPreviewRgbaAllocation != null)
    {
      this.m_BluredPreviewRgbaAllocation.destroy();
      this.m_BluredPreviewRgbaAllocation = null;
    }
    this.m_RenderScript = null;
    this.m_RenderScriptHandle = Handle.close(this.m_RenderScriptHandle);
  }
  
  private void resetAnimation()
  {
    if ((this.m_IsVisible) || (this.m_Container == null)) {
      return;
    }
    this.m_IsVisible = true;
    this.m_Container.animate().cancel();
    this.m_Container.setVisibility(0);
    this.m_Container.setAlpha(1.0F);
  }
  
  private void setupPreviewFrameCache()
  {
    if (m_PreviewFrameCacheFile != null) {
      return;
    }
    m_PreviewFrameCacheFile = new File(getCameraActivity().getCacheDir(), "LaunchPreviewFrame");
  }
  
  private void setupUI(View paramView)
  {
    this.m_Container = paramView.findViewById(2131361986);
    this.m_PreviewFrameView = ((ImageView)this.m_Container);
    this.m_PreviewFrameView.setImageBitmap(m_CachedPreviewThumbnailImage);
    resetAnimation();
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 10001: 
      createPreviewThumbnailImage();
      return;
    }
    onPreviewThumbnailImageCreated((Bitmap)paramMessage.obj);
  }
  
  protected void onDeinitialize()
  {
    HandlerUtils.removeMessages(this, 10001);
    m_BackgroundExecutor.execute(new Runnable()
    {
      public void run()
      {
        LaunchAnimation.-wrap4(LaunchAnimation.this);
      }
    });
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    findComponent(Viewfinder.class, new ComponentSearchCallback()
    {
      public void onComponentFound(Viewfinder paramAnonymousViewfinder)
      {
        paramAnonymousViewfinder.addCallback(Viewfinder.PROP_PREVIEW_BOUNDS, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymous2PropertySource, PropertyKey<RectF> paramAnonymous2PropertyKey, PropertyChangeEventArgs<RectF> paramAnonymous2PropertyChangeEventArgs)
          {
            LaunchAnimation.-wrap2(LaunchAnimation.this, (RectF)paramAnonymous2PropertyChangeEventArgs.getNewValue());
          }
        });
        LaunchAnimation.-wrap2(LaunchAnimation.this, (RectF)paramAnonymousViewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS));
      }
    });
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.addCallback(CameraActivity.PROP_CONTENT_VIEW, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<View> paramAnonymousPropertyKey, PropertyChangeEventArgs<View> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() != null) {
          LaunchAnimation.-wrap7(LaunchAnimation.this, (View)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_CAMERA_PREVIEW_RECEIVED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          LaunchAnimation.-wrap1(LaunchAnimation.this);
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
        {
        default: 
        case 1: 
          do
          {
            return;
          } while (((Boolean)LaunchAnimation.this.getCameraActivity().get(CameraActivity.PROP_IS_CAMERA_PREVIEW_RECEIVED)).booleanValue());
          LaunchAnimation.-wrap5(LaunchAnimation.this);
          return;
        }
        HandlerUtils.removeMessages(LaunchAnimation.this, 10001);
      }
    });
    m_BackgroundExecutor.execute(this.m_SetupRunnable);
    if (m_CachedPreviewThumbnailImage == null) {
      m_BackgroundExecutor.execute(this.m_LoadPreviewThumbRunnable);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/LaunchAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */