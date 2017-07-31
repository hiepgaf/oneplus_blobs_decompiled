package com.oneplus.camera.ui;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
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
import com.oneplus.base.Rotation;
import com.oneplus.base.ScreenSize;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraCaptureEventArgs;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.CaptureEventArgs;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.camera.KeyEventHandler;
import com.oneplus.camera.KeyEventHandler.KeyResult;
import com.oneplus.camera.MediaInfo;
import com.oneplus.camera.MediaResultInfo;
import com.oneplus.camera.PhotoCaptureState;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.media.ImagePlane;
import com.oneplus.camera.media.MediaEventArgs;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.media.YuvToBitmapWorker;
import com.oneplus.camera.media.YuvToBitmapWorker.OnBitmapAvailableListener;
import com.oneplus.media.BitmapPool;
import com.oneplus.media.BitmapPool.Callback;
import com.oneplus.media.ImageUtils;
import com.oneplus.util.SizeUtils;
import java.io.File;

public class ReviewScreenImpl
  extends UIComponent
  implements ReviewScreen, KeyEventHandler
{
  private static final int DURATION_ANIMATION_IN = 400;
  private static final int MAX_THUMBNAIL_IMAGE_SIDE = 1920;
  private static final int MSG_ASYNC_PROCESS_REVIEW_IMAGE = 20001;
  private static final int MSG_ON_MEDIA_SAVED_TIMEOUT = 10006;
  private static final int MSG_ON_REVIEW_IMAGE_CREATED = 10001;
  private static final int MSG_ON_REVIEW_IMAGE_TIMEOUT = 10005;
  private static final int MSG_SET_LAST_MEDIA_INFO = 10002;
  private static final int TIMEOUT_MEDIA_SAVED = 10000;
  private static final int TIMEOUT_PROCESS_REVIEW_IMAGE = 10000;
  private ImageButton m_CancelButton;
  private volatile CaptureHandle m_CaptureHandle;
  private PropertyChangedCallback<Boolean> m_IsCameraPreviewReceivedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      ReviewScreenImpl.-wrap8(ReviewScreenImpl.this);
    }
  };
  private boolean m_IsPostviewReceived;
  private boolean m_IsSetupUI;
  private boolean m_IsVideoBitmapDecoding;
  private Handle m_KeyEventHandle;
  private volatile MediaInfo m_LastMediaInfo;
  private volatile MediaResultType m_MediaResultType;
  private boolean m_MediaSaveFailed;
  private ImageButton m_OKButton;
  private Handle m_ProcessingCancelHandle;
  private ProcessingDialog m_ProcessingDialog;
  private Handle m_ProcessingOkHandle;
  private Handle m_ProcessingRetakeHandle;
  private ImageButton m_RetakeButton;
  private View m_ReviewControls;
  private Bitmap m_ReviewImage;
  private ImageView m_ReviewImageView;
  private View m_ReviewScreen;
  private Handle m_ReviewScreenHandle;
  private Bitmap m_ThumbnailImage;
  private BitmapPool.Callback m_VideoDecodedCallback = new BitmapPool.Callback()
  {
    public void onBitmapDecoded(Handle paramAnonymousHandle, Uri paramAnonymousUri, Bitmap paramAnonymousBitmap)
    {
      ReviewScreenImpl.-wrap9(ReviewScreenImpl.this, paramAnonymousUri, paramAnonymousBitmap);
    }
    
    public void onBitmapDecoded(Handle paramAnonymousHandle, String paramAnonymousString, Bitmap paramAnonymousBitmap)
    {
      ReviewScreenImpl.-wrap9(ReviewScreenImpl.this, paramAnonymousString, paramAnonymousBitmap);
    }
  };
  private Viewfinder m_Viewfinder;
  private YuvToBitmapWorker m_YuvToBitmapWorker;
  
  protected ReviewScreenImpl(CameraActivity paramCameraActivity)
  {
    super("ReviewScreen", paramCameraActivity, true, true);
  }
  
  private void deleteLastMedia()
  {
    if (this.m_LastMediaInfo == null) {
      return;
    }
    AsyncTask.execute(new Runnable()
    {
      public void run()
      {
        if (!this.val$contentUri.toString().startsWith("file://")) {}
        File localFile;
        do
        {
          try
          {
            Log.v(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "deleteLastMedia() - By content Uri: ", this.val$contentUri);
            ReviewScreenImpl.this.getCameraActivity().getContentResolver().delete(this.val$contentUri, null, null);
            return;
          }
          catch (Exception localException)
          {
            Log.e(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "deleteLastMedia() - Error when delete content Uri");
            localException.printStackTrace();
            return;
          }
          localFile = new File(this.val$filePath);
          if (!localFile.exists()) {
            return;
          }
          Log.v(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "deleteLastMedia() - By file path: ", this.val$filePath);
        } while (localFile.delete());
        Log.e(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "deleteLastMedia() - Deletes file failed, file path: " + this.val$filePath);
      }
    });
  }
  
  private void hideReviewScreen(int paramInt)
  {
    if (!this.m_IsSetupUI) {
      return;
    }
    Log.v(this.TAG, "hideReviewScreen() - Hide");
    HandlerUtils.removeMessages(this, 10005);
    HandlerUtils.removeMessages(this, 10006);
    if ((paramInt & 0x1) != 0) {
      setViewVisibility(this.m_ReviewControls, false);
    }
    for (;;)
    {
      this.m_CaptureHandle = null;
      this.m_IsVideoBitmapDecoding = false;
      this.m_LastMediaInfo = null;
      this.m_ReviewImage = null;
      this.m_ThumbnailImage = null;
      this.m_MediaResultType = null;
      this.m_MediaSaveFailed = false;
      Handle.close(this.m_KeyEventHandle);
      Handle.close(this.m_ProcessingOkHandle);
      Handle.close(this.m_ProcessingCancelHandle);
      Handle.close(this.m_ProcessingRetakeHandle);
      setReadOnly(PROP_IS_VISIBLE, Boolean.valueOf(false));
      return;
      setViewVisibility(this.m_ReviewScreen, false);
    }
  }
  
  private boolean needMediaInfo()
  {
    return (this.m_MediaResultType != null) && (this.m_MediaResultType != MediaResultType.PHOTO_THUMBNAIL);
  }
  
  private void onClickCancelButton()
  {
    if (Handle.isValid(this.m_ProcessingCancelHandle))
    {
      Log.v(this.TAG, "onClickCancelButton() - Processing dialog is showing.");
      return;
    }
    Handle.close(this.m_ProcessingCancelHandle);
    if ((!needMediaInfo()) || (this.m_LastMediaInfo != null) || (this.m_MediaSaveFailed))
    {
      deleteLastMedia();
      getCameraActivity().setMediaResult(0, (MediaInfo)null);
      return;
    }
    Log.v(this.TAG, "onClickCancelButton() - Last media content Uri or file path is null, skip");
    if (this.m_ProcessingDialog != null) {
      this.m_ProcessingCancelHandle = this.m_ProcessingDialog.showProcessingDialog(getCameraActivity().getString(2131558503), 0);
    }
  }
  
  private void onClickOKButton()
  {
    if (Handle.isValid(this.m_ProcessingOkHandle))
    {
      Log.v(this.TAG, "onClickOKButton() - Processing dialog is showing.");
      return;
    }
    Handle.close(this.m_ProcessingOkHandle);
    Log.v(this.TAG, "onClickOKButton() - needMediaInfo() : " + needMediaInfo() + " , m_LastMediaInfo : " + this.m_LastMediaInfo + " , m_MediaSaveFailed : " + this.m_MediaSaveFailed);
    if ((!needMediaInfo()) || (this.m_LastMediaInfo != null) || (this.m_MediaSaveFailed))
    {
      if ((needMediaInfo()) && (this.m_LastMediaInfo != null)) {
        getCameraActivity().setMediaResult(1, this.m_LastMediaInfo);
      }
    }
    else
    {
      Log.v(this.TAG, "onClickOKButton() - Last media content Uri or file path is null, skip");
      if (this.m_ProcessingDialog != null) {
        this.m_ProcessingOkHandle = this.m_ProcessingDialog.showProcessingDialog(getCameraActivity().getString(2131558503), 0);
      }
      return;
    }
    if (this.m_ThumbnailImage != null)
    {
      getCameraActivity().setMediaResult(1, this.m_ThumbnailImage);
      return;
    }
    Log.e(this.TAG, "onClickOKButton() - Fail to decode captured picture");
  }
  
  private void onClickRetakeButton(boolean paramBoolean)
  {
    if ((!paramBoolean) && (Handle.isValid(this.m_ProcessingRetakeHandle)))
    {
      Log.v(this.TAG, "onClickRetakeButton() - Processing dialog is showing.");
      return;
    }
    Handle.close(this.m_ProcessingRetakeHandle);
    if ((paramBoolean) || (!needMediaInfo()) || (this.m_LastMediaInfo != null) || (this.m_MediaSaveFailed))
    {
      CameraActivity localCameraActivity = getCameraActivity();
      localCameraActivity.setMediaResult(2, (MediaInfo)null);
      setViewVisibility(this.m_ReviewControls, false);
      localCameraActivity.addCallback(CameraActivity.PROP_IS_CAMERA_PREVIEW_RECEIVED, this.m_IsCameraPreviewReceivedCallback);
      return;
    }
    Log.v(this.TAG, "onClickRetakeButton() - Last media content Uri or file path is null, skip");
    if (this.m_ProcessingDialog != null) {
      this.m_ProcessingRetakeHandle = this.m_ProcessingDialog.showProcessingDialog(getCameraActivity().getString(2131558503), 0);
    }
  }
  
  private void onPostViewReviewImageCreated(Bitmap paramBitmap1, Bitmap paramBitmap2)
  {
    if (!((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_RUNNING)).booleanValue())
    {
      Log.w(this.TAG, "onPostViewReviewImageCreated() - Camera is not running, ignore review image");
      return;
    }
    if (this.m_ReviewScreen == null)
    {
      Log.w(this.TAG, "onPostViewReviewImageCreated() - Review screen is null, ignore review image");
      return;
    }
    if (this.m_ReviewScreen.getVisibility() == 0)
    {
      Log.w(this.TAG, "onPostViewReviewImageCreated() - Review screen is shown, ignore review image");
      return;
    }
    Log.v(this.TAG, "onPostViewReviewImageCreated()");
    this.m_ReviewImage = paramBitmap1;
    this.m_ThumbnailImage = paramBitmap2;
    if (paramBitmap1 != null) {
      this.m_ReviewImageView.setImageBitmap(this.m_ReviewImage);
    }
    for (;;)
    {
      if (this.m_MediaResultType == null) {
        updateMediaResultType();
      }
      if (!Handle.isValid(this.m_ReviewScreenHandle)) {
        break;
      }
      Log.v(this.TAG, "onPostViewReviewImageCreated() - ReviewScreenHandle is valid, show directly.");
      showReviewScreenDirectly();
      return;
      Log.e(this.TAG, "onPostViewReviewImageCreated() - No image");
      this.m_ReviewImageView.setImageDrawable(null);
    }
    Log.v(this.TAG, "onPostViewReviewImageCreated() - ReviewScreenHandle is invalid");
    showReviewScreen();
  }
  
  private void onPostviewReceived(CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    final CaptureHandle localCaptureHandle = this.m_CaptureHandle;
    Log.v(this.TAG, "onPostviewReceived() - Handle", localCaptureHandle);
    this.m_IsPostviewReceived = true;
    Size localSize = paramCameraCaptureEventArgs.getPictureSize();
    Object localObject = getScreenSize();
    int i = Math.max(((ScreenSize)localObject).getWidth(), ((ScreenSize)localObject).getHeight());
    localObject = SizeUtils.getRatioStretchedSize(localSize.getWidth(), localSize.getHeight(), i, i, true);
    if (this.m_YuvToBitmapWorker == null) {
      this.m_YuvToBitmapWorker = new YuvToBitmapWorker(getContext(), 1, localSize.getWidth(), localSize.getHeight(), ((Size)localObject).getWidth(), ((Size)localObject).getHeight(), Bitmap.Config.RGB_565, 1);
    }
    for (;;)
    {
      this.m_YuvToBitmapWorker.setOnBitmapAvailableListener(new YuvToBitmapWorker.OnBitmapAvailableListener()
      {
        public void onBitmapAvailable(YuvToBitmapWorker paramAnonymousYuvToBitmapWorker)
        {
          Log.v(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "onPostviewReceived() - Bitmap available, handle: ", localCaptureHandle);
          Object localObject2 = paramAnonymousYuvToBitmapWorker.acquireLastBitmap();
          if (localObject2 == null)
          {
            Log.e(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "onPostviewReceived() - Worker bitmap is null");
            return;
          }
          Object localObject1 = ((Bitmap)localObject2).copy(Bitmap.Config.RGB_565, false);
          paramAnonymousYuvToBitmapWorker.releaseBitmap((Bitmap)localObject2);
          paramAnonymousYuvToBitmapWorker = (Camera)ReviewScreenImpl.this.getCameraActivity().get(CameraActivity.PROP_CAMERA);
          localObject2 = new Matrix();
          if (localObject1 != null)
          {
            ((Matrix)localObject2).postRotate(((Integer)paramAnonymousYuvToBitmapWorker.get(Camera.PROP_SENSOR_ORIENTATION)).intValue());
            if (localCaptureHandle.isMirrored()) {
              ((Matrix)localObject2).postScale(-1.0F, 1.0F);
            }
            localObject2 = Bitmap.createBitmap((Bitmap)localObject1, 0, 0, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight(), (Matrix)localObject2, true);
            localObject1 = null;
            if (ReviewScreenImpl.-get4(ReviewScreenImpl.this) == null) {
              ReviewScreenImpl.-wrap12(ReviewScreenImpl.this);
            }
            paramAnonymousYuvToBitmapWorker = (YuvToBitmapWorker)localObject1;
            if (localObject2 != null)
            {
              paramAnonymousYuvToBitmapWorker = (YuvToBitmapWorker)localObject1;
              if (ReviewScreenImpl.-get4(ReviewScreenImpl.this) == ReviewScreenImpl.MediaResultType.PHOTO_THUMBNAIL)
              {
                paramAnonymousYuvToBitmapWorker = new Matrix();
                localObject1 = SizeUtils.getRatioStretchedSize(((Bitmap)localObject2).getWidth(), ((Bitmap)localObject2).getHeight(), 260, 260, true);
                Log.v(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "handleAsyncMessage() - Thumb size: ", localObject1);
                paramAnonymousYuvToBitmapWorker.postScale(((Size)localObject1).getWidth() / ((Bitmap)localObject2).getWidth(), ((Size)localObject1).getHeight() / ((Bitmap)localObject2).getHeight());
                paramAnonymousYuvToBitmapWorker = Bitmap.createBitmap((Bitmap)localObject2, 0, 0, ((Bitmap)localObject2).getWidth(), ((Bitmap)localObject2).getHeight(), paramAnonymousYuvToBitmapWorker, true);
              }
            }
            if (!ReviewScreenImpl.-get3(ReviewScreenImpl.this)) {
              ReviewScreenImpl.-wrap11(ReviewScreenImpl.this);
            }
            ReviewScreenImpl.-wrap6(ReviewScreenImpl.this, (Bitmap)localObject2, paramAnonymousYuvToBitmapWorker);
            return;
          }
          Log.e(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "onPostviewReceived() - Cannot decode review image");
        }
      }, getHandler());
      this.m_YuvToBitmapWorker.addNV21Frame(paramCameraCaptureEventArgs.getPicturePlanes()[0].getData());
      return;
      this.m_YuvToBitmapWorker.reconfigureInput(localSize.getWidth(), localSize.getHeight());
      this.m_YuvToBitmapWorker.reconfigureOutput(((Size)localObject).getWidth(), ((Size)localObject).getHeight());
    }
  }
  
  private void onPreviewReceived()
  {
    hideReviewScreen(0);
    getCameraActivity().removeCallback(CameraActivity.PROP_IS_CAMERA_PREVIEW_RECEIVED, this.m_IsCameraPreviewReceivedCallback);
  }
  
  private void onReviewImageCreated(Bitmap paramBitmap1, Bitmap paramBitmap2)
  {
    if (!((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_RUNNING)).booleanValue())
    {
      Log.w(this.TAG, "onReviewImageCreated() - Camera is not running, ignore review image");
      return;
    }
    if (!Handle.isValid(this.m_ReviewScreenHandle))
    {
      Log.w(this.TAG, "onReviewImageCreated() - Review screen handle is invalid, ignore review image");
      return;
    }
    if (this.m_ReviewScreen == null)
    {
      Log.w(this.TAG, "onReviewImageCreated() - Review screen is null, ignore review image");
      return;
    }
    if (this.m_ReviewScreen.getVisibility() == 0)
    {
      Log.w(this.TAG, "onReviewImageCreated() - Review screen is shown, ignore review image");
      return;
    }
    Log.v(this.TAG, "onReviewImageCreated()");
    HandlerUtils.removeMessages(this, 10005);
    this.m_ReviewImage = paramBitmap1;
    this.m_ThumbnailImage = paramBitmap2;
    if (paramBitmap1 != null) {
      this.m_ReviewImageView.setImageBitmap(this.m_ReviewImage);
    }
    for (;;)
    {
      if (Handle.isValid(this.m_ReviewScreenHandle)) {
        showReviewScreenDirectly();
      }
      return;
      Log.e(this.TAG, "onReviewImageCreated() - No image");
      this.m_ReviewImageView.setImageDrawable(null);
    }
  }
  
  private void onVideoBitmapDecoded(Object paramObject, Bitmap paramBitmap)
  {
    if (Handle.isValid(this.m_CaptureHandle)) {
      return;
    }
    this.m_IsVideoBitmapDecoding = false;
    if (paramBitmap != null)
    {
      if (this.m_ReviewImageView == null) {
        break label50;
      }
      Log.w(this.TAG, "onVideoBitmapDecoded() - setImageBitmap");
      this.m_ReviewImageView.setImageBitmap(paramBitmap);
    }
    for (;;)
    {
      showReviewScreenDirectly();
      return;
      label50:
      Log.w(this.TAG, "onVideoBitmapDecoded() - m_ReviewImageView is null.");
    }
  }
  
  private void setupUI()
  {
    if (this.m_IsSetupUI) {
      return;
    }
    this.m_ReviewScreen = ((ViewStub)getCameraActivity().findViewById(2131361831)).inflate();
    this.m_ReviewScreen.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.m_ReviewImageView = ((ImageView)this.m_ReviewScreen.findViewById(2131362052));
    this.m_ReviewControls = this.m_ReviewScreen.findViewById(2131362053);
    this.m_CancelButton = ((ImageButton)this.m_ReviewControls.findViewById(2131362056));
    this.m_OKButton = ((ImageButton)this.m_ReviewControls.findViewById(2131362055));
    this.m_RetakeButton = ((ImageButton)this.m_ReviewControls.findViewById(2131362054));
    this.m_CancelButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ReviewScreenImpl.-wrap3(ReviewScreenImpl.this);
      }
    });
    this.m_OKButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ReviewScreenImpl.-wrap4(ReviewScreenImpl.this);
      }
    });
    this.m_RetakeButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ReviewScreenImpl.-wrap5(ReviewScreenImpl.this, false);
      }
    });
    addAutoRotateView(this.m_CancelButton);
    addAutoRotateView(this.m_OKButton);
    addAutoRotateView(this.m_RetakeButton);
    addNavBarAlignedView(this.m_ReviewControls);
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[getMediaType().ordinal()])
    {
    }
    for (;;)
    {
      this.m_IsSetupUI = true;
      return;
      this.m_ReviewScreen.setBackgroundColor(-16777216);
      continue;
      this.m_ReviewScreen.setBackgroundColor(0);
    }
  }
  
  private void showReviewScreenDirectly()
  {
    if (this.m_IsVideoBitmapDecoding) {
      return;
    }
    Log.v(this.TAG, "showReviewScreenDirectly() - Show");
    if (this.m_Viewfinder != null)
    {
      RectF localRectF = (RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
      RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)this.m_ReviewImageView.getLayoutParams();
      localLayoutParams.width = Math.round(localRectF.width());
      localLayoutParams.height = Math.round(localRectF.height());
      localLayoutParams.topMargin = Math.round(localRectF.top);
    }
    for (;;)
    {
      this.m_ReviewImageView.requestLayout();
      setViewVisibility(this.m_ReviewControls, true, 400L, null);
      setViewVisibility(this.m_ReviewScreen, true, 400L, null);
      this.m_OKButton.requestFocus();
      this.m_KeyEventHandle = getCameraActivity().setKeyEventHandler(this);
      setReadOnly(PROP_IS_VISIBLE, Boolean.valueOf(true));
      return;
      Log.e(this.TAG, "showReviewScreenDirectly() - No Viewfinder");
    }
  }
  
  private void updateMediaResultType()
  {
    MediaResultInfo localMediaResultInfo = getCameraActivity().getMediaResultInfo();
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[getMediaType().ordinal()])
    {
    default: 
      return;
    case 1: 
      if ((localMediaResultInfo != null) && (localMediaResultInfo.extraOutput != null))
      {
        this.m_MediaResultType = MediaResultType.PHOTO_CONTENT_URI;
        return;
      }
      this.m_MediaResultType = MediaResultType.PHOTO_THUMBNAIL;
      return;
    }
    this.m_MediaResultType = MediaResultType.VIDEO_CONTENT_URI;
  }
  
  protected void handleAsyncMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleAsyncMessage(paramMessage);
      return;
    }
    Log.v(this.TAG, "handleAsyncMessage() - Decode review image");
    HandlerUtils.sendMessage(this, 10005, 10000L);
    Object localObject1 = getScreenSize();
    int i = Math.max(((ScreenSize)localObject1).getWidth(), ((ScreenSize)localObject1).getHeight());
    paramMessage = ImageUtils.decodeBitmap((byte[])paramMessage.obj, i, i, Bitmap.Config.ARGB_8888);
    localObject1 = new Matrix();
    Object localObject2;
    if (paramMessage != null)
    {
      localObject2 = (Rotation)getCameraThread().get(CameraThread.PROP_CAPTURE_ROTATION);
      switch (-getcom-oneplus-base-RotationSwitchesValues()[localObject2.ordinal()])
      {
      default: 
        paramMessage = Bitmap.createBitmap(paramMessage, 0, 0, paramMessage.getWidth(), paramMessage.getHeight(), (Matrix)localObject1, true);
        label165:
        localObject2 = null;
        if (this.m_MediaResultType == null) {
          updateMediaResultType();
        }
        localObject1 = localObject2;
        if (paramMessage != null)
        {
          localObject1 = localObject2;
          if (this.m_MediaResultType == MediaResultType.PHOTO_THUMBNAIL)
          {
            localObject1 = new Matrix();
            localObject2 = SizeUtils.getRatioStretchedSize(paramMessage.getWidth(), paramMessage.getHeight(), 260, 260, true);
            Log.v(this.TAG, "handleAsyncMessage() - Thumb size: ", localObject2);
            ((Matrix)localObject1).postScale(((Size)localObject2).getWidth() / paramMessage.getWidth(), ((Size)localObject2).getHeight() / paramMessage.getHeight());
            Rotation localRotation = getRotation();
            switch (-getcom-oneplus-base-RotationSwitchesValues()[localRotation.ordinal()])
            {
            }
          }
        }
        break;
      }
    }
    for (;;)
    {
      localObject1 = Bitmap.createBitmap(paramMessage, 0, 0, paramMessage.getWidth(), paramMessage.getHeight(), (Matrix)localObject1, true);
      HandlerUtils.removeMessages(this, 10005);
      HandlerUtils.sendMessage(this, 10001, 0, 0, new Bitmap[] { paramMessage, localObject1 });
      return;
      ((Matrix)localObject1).postRotate(90.0F);
      ((Matrix)localObject1).postTranslate(paramMessage.getHeight(), 0.0F);
      break;
      ((Matrix)localObject1).postRotate(-90.0F);
      ((Matrix)localObject1).postTranslate(0.0F, paramMessage.getWidth());
      break;
      ((Matrix)localObject1).postRotate(180.0F);
      ((Matrix)localObject1).postTranslate(0.0F, paramMessage.getWidth());
      break;
      Log.e(this.TAG, "handleAsyncMessage() - Cannot decode review image");
      break label165;
      ((Matrix)localObject1).postRotate(-90.0F, ((Size)localObject2).getWidth() / 2, ((Size)localObject2).getHeight() / 2);
      continue;
      ((Matrix)localObject1).postRotate(90.0F, ((Size)localObject2).getWidth() / 2, ((Size)localObject2).getHeight() / 2);
      continue;
      ((Matrix)localObject1).postRotate(180.0F, ((Size)localObject2).getWidth() / 2, ((Size)localObject2).getHeight() / 2);
    }
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    case 10003: 
    case 10004: 
    default: 
      super.handleMessage(paramMessage);
    case 10001: 
    case 10002: 
    case 10005: 
      do
      {
        do
        {
          do
          {
            return;
            paramMessage = (Bitmap[])paramMessage.obj;
            onReviewImageCreated(paramMessage[0], paramMessage[1]);
            return;
            if ((!Handle.isValid(this.m_ReviewScreenHandle)) || (!needMediaInfo())) {
              break;
            }
            HandlerUtils.removeMessages(this, 10006);
            this.m_LastMediaInfo = ((MediaInfo)paramMessage.obj);
            if (this.m_MediaResultType == MediaResultType.VIDEO_CONTENT_URI) {
              showReviewScreenDirectly();
            }
            if (Handle.isValid(this.m_ProcessingOkHandle))
            {
              Handle.close(this.m_ProcessingOkHandle);
              onClickOKButton();
            }
            if (Handle.isValid(this.m_ProcessingCancelHandle))
            {
              Handle.close(this.m_ProcessingCancelHandle);
              onClickCancelButton();
            }
          } while (!Handle.isValid(this.m_ProcessingRetakeHandle));
          Handle.close(this.m_ProcessingRetakeHandle);
          onClickRetakeButton(false);
          return;
        } while (Handle.isValid(this.m_ReviewScreenHandle));
        Handle.close(this.m_ProcessingOkHandle);
        Handle.close(this.m_ProcessingCancelHandle);
        Handle.close(this.m_ProcessingRetakeHandle);
        return;
        Log.e(this.TAG, "handleMessage() - On review image timeout");
      } while (!Handle.isValid(this.m_ReviewScreenHandle));
      showReviewScreenDirectly();
      return;
    }
    Log.e(this.TAG, "handleMessage() - On media saved timeout");
    onClickRetakeButton(true);
  }
  
  protected void onDeinitialize()
  {
    super.onDeinitialize();
    if (this.m_YuvToBitmapWorker != null) {
      this.m_YuvToBitmapWorker.close();
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_ProcessingDialog = ((ProcessingDialog)findComponent(ProcessingDialog.class));
    this.m_Viewfinder = ((Viewfinder)findComponent(Viewfinder.class));
    if (this.m_Viewfinder == null) {
      Log.e(this.TAG, "onInitialize() - Cannot find Viewfinder");
    }
    final CameraActivity localCameraActivity = getCameraActivity();
    final CameraThread localCameraThread = getCameraThread();
    HandlerUtils.post(localCameraThread, new Runnable()
    {
      public void run()
      {
        localCameraThread.addHandler(CameraThread.EVENT_CAPTURE_STARTED, new EventHandler()
        {
          public void onEventReceived(EventSource paramAnonymous2EventSource, EventKey<CaptureEventArgs> paramAnonymous2EventKey, CaptureEventArgs paramAnonymous2CaptureEventArgs)
          {
            if (this.val$cameraActivity.isServiceMode())
            {
              ReviewScreenImpl.-set1(ReviewScreenImpl.this, false);
              ReviewScreenImpl.-set0(ReviewScreenImpl.this, paramAnonymous2CaptureEventArgs.getCaptureHandle());
            }
          }
        });
        localCameraThread.addHandler(CameraThread.EVENT_MEDIA_SAVED, new EventHandler()
        {
          public void onEventReceived(EventSource paramAnonymous2EventSource, EventKey<MediaEventArgs> paramAnonymous2EventKey, MediaEventArgs paramAnonymous2MediaEventArgs)
          {
            if ((this.val$cameraActivity.isServiceMode()) && (paramAnonymous2MediaEventArgs.getCaptureHandle() == ReviewScreenImpl.-get1(ReviewScreenImpl.this)))
            {
              Log.v(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "EVENT_MEDIA_SAVED - Current handle: ", ReviewScreenImpl.-get1(ReviewScreenImpl.this), ", handle in media: ", paramAnonymous2MediaEventArgs.getCaptureHandle());
              HandlerUtils.removeMessages(ReviewScreenImpl.this, 10006);
              if (ReviewScreenImpl.-get4(ReviewScreenImpl.this) == null) {
                ReviewScreenImpl.-wrap12(ReviewScreenImpl.this);
              }
              paramAnonymous2EventSource = new MediaInfo(paramAnonymous2MediaEventArgs.getContentUri(), paramAnonymous2MediaEventArgs.getFilePath());
              HandlerUtils.sendMessage(ReviewScreenImpl.this, 10002, 0, 0, paramAnonymous2EventSource);
              if (ReviewScreenImpl.-wrap1(ReviewScreenImpl.this) == MediaType.VIDEO)
              {
                ReviewScreenImpl.-set2(ReviewScreenImpl.this, true);
                if (paramAnonymous2MediaEventArgs.getFilePath() == null) {
                  break label202;
                }
                BitmapPool.DEFAULT_THUMBNAIL.decode(paramAnonymous2MediaEventArgs.getFilePath(), 3, 1920, 1920, 3, ReviewScreenImpl.-get6(ReviewScreenImpl.this), ReviewScreenImpl.this.getHandler());
              }
            }
            return;
            label202:
            if (paramAnonymous2MediaEventArgs.getContentUri() != null)
            {
              BitmapPool.DEFAULT_THUMBNAIL.decode(ReviewScreenImpl.this.getCameraActivity(), paramAnonymous2MediaEventArgs.getContentUri(), 3, 1920, 1920, 3, ReviewScreenImpl.-get6(ReviewScreenImpl.this), ReviewScreenImpl.this.getHandler());
              return;
            }
            Log.e(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "onEventReceived - No path or uri to decode bitmap.");
          }
        });
        localCameraThread.addCallback(CameraThread.PROP_PHOTO_CAPTURE_STATE, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymous2PropertySource, PropertyKey<PhotoCaptureState> paramAnonymous2PropertyKey, PropertyChangeEventArgs<PhotoCaptureState> paramAnonymous2PropertyChangeEventArgs)
          {
            if (paramAnonymous2PropertyChangeEventArgs.getOldValue() == PhotoCaptureState.CAPTURING)
            {
              if (ReviewScreenImpl.-get2(ReviewScreenImpl.this))
              {
                Log.v(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "Postview is already received, use it for review screen.");
                return;
              }
              paramAnonymous2PropertySource = ReviewScreenImpl.this.getCameraThread().getLastCapturedJpeg();
              if (paramAnonymous2PropertySource != null) {
                HandlerUtils.sendAsyncMessage(ReviewScreenImpl.this, 20001, 0, 0, paramAnonymous2PropertySource);
              }
            }
            else
            {
              return;
            }
            Log.w(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "No JPEG after capturing");
          }
        });
        localCameraThread.addHandler(CameraThread.EVENT_MEDIA_SAVE_FAILED, new EventHandler()
        {
          public void onEventReceived(EventSource paramAnonymous2EventSource, EventKey<MediaEventArgs> paramAnonymous2EventKey, MediaEventArgs paramAnonymous2MediaEventArgs)
          {
            if (ReviewScreenImpl.-get1(ReviewScreenImpl.this) == paramAnonymous2MediaEventArgs.getCaptureHandle())
            {
              Log.d(ReviewScreenImpl.-get0(ReviewScreenImpl.this), "EVENT_MEDIA_SAVE_FAILED - Current handle: " + paramAnonymous2MediaEventArgs.getCaptureHandle());
              ReviewScreenImpl.-set3(ReviewScreenImpl.this, true);
              HandlerUtils.removeMessages(ReviewScreenImpl.this, 10006);
            }
          }
        });
        localCameraThread.addHandler(CameraThread.EVENT_POSTVIEW_RECEIVED, new EventHandler()
        {
          public void onEventReceived(EventSource paramAnonymous2EventSource, EventKey<CameraCaptureEventArgs> paramAnonymous2EventKey, CameraCaptureEventArgs paramAnonymous2CameraCaptureEventArgs)
          {
            ReviewScreenImpl.-wrap7(ReviewScreenImpl.this, paramAnonymous2CameraCaptureEventArgs);
          }
        });
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_RUNNING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if ((!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) && (ReviewScreenImpl.-get5(ReviewScreenImpl.this) != null) && (ReviewScreenImpl.-get5(ReviewScreenImpl.this).getVisibility() == 0))
        {
          ReviewScreenImpl.-wrap10(ReviewScreenImpl.this, ReviewScreenImpl.-get5(ReviewScreenImpl.this), false);
          ReviewScreenImpl.-wrap0(ReviewScreenImpl.this, ReviewScreenImpl.PROP_IS_VISIBLE, Boolean.valueOf(false));
        }
      }
    });
  }
  
  public KeyEventHandler.KeyResult onKeyDown(int paramInt, KeyEventArgs paramKeyEventArgs)
  {
    if ((paramInt == 25) || (paramInt == 24)) {
      return KeyEventHandler.KeyResult.HANDLED_AND_PASS_TO_SYSTEM;
    }
    return KeyEventHandler.KeyResult.NOT_HANDLED;
  }
  
  public KeyEventHandler.KeyResult onKeyUp(int paramInt, KeyEventArgs paramKeyEventArgs)
  {
    if ((paramInt == 25) || (paramInt == 24)) {
      return KeyEventHandler.KeyResult.HANDLED_AND_PASS_TO_SYSTEM;
    }
    return KeyEventHandler.KeyResult.NOT_HANDLED;
  }
  
  public Handle showReviewScreen()
  {
    if (!this.m_IsSetupUI) {
      setupUI();
    }
    if (Handle.isValid(this.m_ReviewScreenHandle))
    {
      Log.v(this.TAG, "showReviewScreen() - Already show review screen, skip");
      return this.m_ReviewScreenHandle;
    }
    Log.v(this.TAG, "showReviewScreen()");
    this.m_ReviewScreenHandle = new ReviewScreenHandle("Show review screen");
    if (this.m_MediaResultType == null) {
      updateMediaResultType();
    }
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[getMediaType().ordinal()])
    {
    }
    for (;;)
    {
      return this.m_ReviewScreenHandle;
      if (this.m_ReviewImage != null)
      {
        showReviewScreenDirectly();
      }
      else
      {
        Log.v(this.TAG, "showReviewScreen() - Wait for review image created");
        if (this.m_MediaResultType == MediaResultType.PHOTO_CONTENT_URI)
        {
          HandlerUtils.sendMessage(this, 10006, 10000L);
          continue;
          if (this.m_LastMediaInfo != null)
          {
            showReviewScreenDirectly();
          }
          else
          {
            Log.v(this.TAG, "showReviewScreen() - Wait for video media saved");
            HandlerUtils.sendMessage(this, 10006, 10000L);
          }
        }
      }
    }
  }
  
  private static enum MediaResultType
  {
    PHOTO_CONTENT_URI,  PHOTO_THUMBNAIL,  VIDEO_CONTENT_URI;
  }
  
  private class ReviewScreenHandle
    extends Handle
  {
    protected ReviewScreenHandle(String paramString)
    {
      super();
    }
    
    protected void onClose(int paramInt)
    {
      ReviewScreenImpl.-wrap2(ReviewScreenImpl.this, paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ReviewScreenImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */