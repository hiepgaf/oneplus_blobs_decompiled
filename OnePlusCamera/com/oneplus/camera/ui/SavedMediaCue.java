package com.oneplus.camera.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.Rotation;
import com.oneplus.base.ScreenSize;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CaptureEventArgs;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.media.MediaEventArgs;
import com.oneplus.camera.media.MediaType;
import com.oneplus.media.BitmapPool;
import com.oneplus.media.BitmapPool.Callback;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

final class SavedMediaCue
  extends UIComponent
{
  private static final int ANIMATION_DURATION = 100;
  private static final int DECODED_IMAGE_SIZE = 256;
  public static BitmapPool m_ImageDecoder = new BitmapPool("AnimationBitmapPool", 524288L, 524288L, Bitmap.Config.RGB_565, 1, 0);
  private LinkedList<Bitmap> m_AnimationBitmapQueue = new LinkedList();
  private ViewPropertyAnimator m_Animator;
  private BitmapPool.Callback m_BitmapDecodeCallback = new BitmapPool.Callback()
  {
    public void onBitmapDecoded(Handle paramAnonymousHandle, String paramAnonymousString, Bitmap paramAnonymousBitmap)
    {
      SavedMediaCue.-get4(SavedMediaCue.this).remove(paramAnonymousHandle);
      SavedMediaCue.-wrap3(SavedMediaCue.this, paramAnonymousBitmap);
    }
  };
  private int m_BurstImageSaveCount = 0;
  private int m_BurstShotCount = 0;
  private RelativeLayout m_CueFrameContainer;
  private ImageView m_CueFrameLandscape;
  private ImageView m_CueFramePortrait;
  private ArrayList<Handle> m_DecodeHandleList = new ArrayList();
  private MediaType m_ValidBurstFileMediaType;
  private String m_ValidBurstFilePath;
  
  SavedMediaCue(CameraActivity paramCameraActivity)
  {
    super("Saved Media Cue", paramCameraActivity, false);
  }
  
  private void animatePhotograph(final View paramView, float paramFloat, final long paramLong, int paramInt1, final int paramInt2)
  {
    this.m_Animator = paramView.animate().rotation(paramFloat).setDuration(paramLong).setListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        paramView.animate().rotation(0.0F).setDuration(paramLong).setListener(new Animator.AnimatorListener()
        {
          public void onAnimationCancel(Animator paramAnonymous2Animator) {}
          
          public void onAnimationEnd(Animator paramAnonymous2Animator)
          {
            SavedMediaCue.-get1(SavedMediaCue.this).setTranslationX(0.0F);
            SavedMediaCue.-get1(SavedMediaCue.this).setTranslationY(0.0F);
            this.val$image.setPivotX(this.val$width / 2);
            this.val$image.setPivotY(this.val$height / 2);
            SavedMediaCue.-set0(SavedMediaCue.this, null);
            SavedMediaCue.-get1(SavedMediaCue.this).setVisibility(8);
            SavedMediaCue.-get3(SavedMediaCue.this).setVisibility(8);
            SavedMediaCue.-get2(SavedMediaCue.this).setVisibility(8);
            if (!SavedMediaCue.-get0(SavedMediaCue.this).isEmpty()) {
              SavedMediaCue.-wrap3(SavedMediaCue.this, (Bitmap)SavedMediaCue.-get0(SavedMediaCue.this).poll());
            }
          }
          
          public void onAnimationRepeat(Animator paramAnonymous2Animator) {}
          
          public void onAnimationStart(Animator paramAnonymous2Animator) {}
        }).start();
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator) {}
    });
    this.m_Animator.start();
  }
  
  private void decodeImageForAnimation(String paramString, MediaType paramMediaType)
  {
    if (TextUtils.isEmpty(paramString))
    {
      Log.w(this.TAG, "decodeImageForAnimation() - filePath is empty");
      return;
    }
    if (paramMediaType == MediaType.PHOTO) {}
    for (int i = 1;; i = 3)
    {
      paramString = m_ImageDecoder.decode(paramString, i, 256, 256, 1, this.m_BitmapDecodeCallback, getCameraActivity().getHandler());
      this.m_DecodeHandleList.add(paramString);
      return;
    }
  }
  
  private void onBurstPhotoReceived(CaptureEventArgs paramCaptureEventArgs)
  {
    if (!((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_BURST_PHOTO_ON_CAPTURE)).booleanValue())
    {
      Log.w(this.TAG, "onBurstPhotoReceived() - wrong state");
      return;
    }
    this.m_BurstShotCount = (paramCaptureEventArgs.getFrameIndex() + 1);
    Log.v(this.TAG, "onBurstPhotoReceived() - m_BurstShotCount is " + this.m_BurstShotCount);
  }
  
  private void onBurstSaveEventReceived()
  {
    this.m_BurstImageSaveCount += 1;
    Log.v(this.TAG, "onBurstSaveEventReceived() - m_BurstImageSaveCount is " + this.m_BurstImageSaveCount);
    if (this.m_BurstImageSaveCount < this.m_BurstShotCount)
    {
      Log.v(this.TAG, "onBurstSaveEventReceived() - waiting for more save count");
      return;
    }
    if (this.m_BurstImageSaveCount == this.m_BurstShotCount)
    {
      Log.v(this.TAG, "onBurstSaveEventReceived() - save count is enough, start animation");
      decodeImageForAnimation(this.m_ValidBurstFilePath, this.m_ValidBurstFileMediaType);
      this.m_BurstImageSaveCount = 0;
      this.m_BurstShotCount = 0;
      this.m_ValidBurstFilePath = null;
      return;
    }
    Log.e(this.TAG, "onBurstSaveEventReceived() - wrong state");
    this.m_BurstImageSaveCount = 0;
    this.m_BurstShotCount = 0;
    this.m_ValidBurstFilePath = null;
  }
  
  private void showCueAnimation(Bitmap paramBitmap)
  {
    if (paramBitmap == null)
    {
      Log.w(this.TAG, "showCueAnimation() - bitmap is empty");
      return;
    }
    if (this.m_Animator != null)
    {
      Log.v(this.TAG, "showCueAnimation() - animation is running");
      this.m_AnimationBitmapQueue.add(paramBitmap);
      return;
    }
    int i = getScreenSize().getWidth();
    int j = getScreenSize().getHeight();
    this.m_CueFrameContainer.setVisibility(0);
    if ((Rotation.PORTRAIT == getRotation()) || (Rotation.INVERSE_PORTRAIT == getRotation()))
    {
      this.m_CueFrameContainer.setTranslationX(i);
      this.m_CueFrameContainer.setPivotX(0.0F);
      this.m_CueFrameContainer.setPivotY(j);
      this.m_CueFramePortrait.setVisibility(0);
      this.m_CueFramePortrait.setImageBitmap(paramBitmap);
      animatePhotograph(this.m_CueFrameContainer, -5.0F, 100L, i, j);
      return;
    }
    Matrix localMatrix = new Matrix();
    if (Rotation.LANDSCAPE == getRotation())
    {
      localMatrix.postRotate(90.0F);
      this.m_CueFrameContainer.setPivotX(0.0F);
    }
    for (float f = -10.0F;; f = 10.0F)
    {
      paramBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
      this.m_CueFrameLandscape.setVisibility(0);
      this.m_CueFrameLandscape.setImageBitmap(paramBitmap);
      this.m_CueFrameContainer.setTranslationY(j);
      this.m_CueFrameContainer.setPivotY(0.0F);
      animatePhotograph(this.m_CueFrameContainer, f, 100L, i, j);
      return;
      localMatrix.postRotate(-90.0F);
      this.m_CueFrameContainer.setPivotX(i);
    }
  }
  
  protected void onDeinitialize()
  {
    super.onDeinitialize();
    if (!this.m_DecodeHandleList.isEmpty())
    {
      Iterator localIterator = this.m_DecodeHandleList.iterator();
      while (localIterator.hasNext()) {
        Handle.close((Handle)localIterator.next());
      }
      this.m_DecodeHandleList.clear();
    }
    if (!this.m_AnimationBitmapQueue.isEmpty()) {
      this.m_AnimationBitmapQueue.clear();
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.addHandler(CameraActivity.EVENT_BURST_PHOTO_RECEIVED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
      {
        SavedMediaCue.-wrap1(SavedMediaCue.this, paramAnonymousCaptureEventArgs);
      }
    });
    localCameraActivity.addHandler(CameraActivity.EVENT_MEDIA_FILE_SAVED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
      {
        if (!paramAnonymousMediaEventArgs.getCaptureHandle().isBurstPhotoCapture()) {
          SavedMediaCue.-wrap0(SavedMediaCue.this, paramAnonymousMediaEventArgs.getFilePath(), paramAnonymousMediaEventArgs.getCaptureHandle().getMediaType());
        }
      }
    });
    localCameraActivity.addHandler(CameraActivity.EVENT_MEDIA_SAVE_CANCELLED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
      {
        if (paramAnonymousMediaEventArgs.getCaptureHandle().isBurstPhotoCapture()) {
          SavedMediaCue.-wrap2(SavedMediaCue.this);
        }
      }
    });
    localCameraActivity.addHandler(CameraActivity.EVENT_MEDIA_SAVE_FAILED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
      {
        if (paramAnonymousMediaEventArgs.getCaptureHandle().isBurstPhotoCapture()) {
          SavedMediaCue.-wrap2(SavedMediaCue.this);
        }
      }
    });
    localCameraActivity.addHandler(CameraActivity.EVENT_MEDIA_SAVED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
      {
        if (paramAnonymousMediaEventArgs.getCaptureHandle().isBurstPhotoCapture())
        {
          SavedMediaCue.-set2(SavedMediaCue.this, paramAnonymousMediaEventArgs.getFilePath());
          SavedMediaCue.-set1(SavedMediaCue.this, paramAnonymousMediaEventArgs.getCaptureHandle().getMediaType());
          SavedMediaCue.-wrap2(SavedMediaCue.this);
        }
      }
    });
    this.m_CueFrameContainer = ((RelativeLayout)localCameraActivity.findViewById(2131362057));
    this.m_CueFramePortrait = ((ImageView)localCameraActivity.findViewById(2131362058));
    this.m_CueFrameLandscape = ((ImageView)localCameraActivity.findViewById(2131362059));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/SavedMediaCue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */