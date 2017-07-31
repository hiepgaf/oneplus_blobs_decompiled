package com.oneplus.camera.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.EventArgs;
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
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.base.component.ComponentUtils;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.FlashController;
import com.oneplus.camera.FlashController.FlashDisabledReason;
import com.oneplus.camera.KeyEventHandler;
import com.oneplus.camera.KeyEventHandler.KeyResult;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.VideoCaptureState;
import com.oneplus.camera.io.FileManager;
import com.oneplus.camera.media.MediaEventArgs;
import com.oneplus.camera.widget.RotateRelativeLayout;
import com.oneplus.io.Path;
import com.oneplus.media.BitmapPool;
import com.oneplus.media.BitmapPool.Callback;
import java.io.File;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class PreviewGallery
  extends UIComponent
  implements GalleryUI, KeyEventHandler
{
  private static final float ALPHA_MAX = 1.0F;
  private static final float ALPHA_MIN = 0.0F;
  private static final String DEFAULT_GALLERY = "com.android.gallery3d";
  private static final int DELAY_DELETED_PHOTO = 5000;
  private static final String KEY_FILES_LIST = "FILES_LIST";
  private static final String KEY_SECURE_MODE = "SECURE_MODE";
  private static final int MESSAGE_LAUNCH_PENDING = 1003;
  private static final int MESSAGE_REDECODE = 1004;
  private static final int MESSAGE_TRULY_DELETED = 1005;
  private static final int MESSAGE_UNDO_DELETED = 1006;
  private static final int MESSAGE_UPDATE_ADDED = 1001;
  public static final int MESSAGE_UPDATE_DELETED = 1002;
  private static final int MESSAGE_UPDATE_RESET = 1000;
  private static final int PAGE_OFFSET = 2;
  private static final int REQUEST_GALLERY_URI = 100;
  private static final int SECURE_MODE_MAX_SIZE = 100;
  private static final int TARGET = 3;
  private PreviewPagerAdapter m_Adapter;
  private ViewPropertyAnimator m_Animator;
  private AnimatorListenerAdapter m_AnimatorListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      PreviewGallery.-get13(PreviewGallery.this).setVisibility(8);
    }
  };
  private View m_BG;
  private RotateRelativeLayout m_CameraUndoDeletion;
  private Handle m_CaptureModeChangeCUDHandle;
  private String m_DeletedCountString = null;
  private String m_DeletedFilePath;
  private int m_DeletedIndex;
  private FileManager m_FileManager;
  private Handle m_FlashDisableHandle;
  private boolean m_Front;
  private boolean m_HandleByActivity;
  private boolean m_HasDefaultGallery;
  private int m_ImageHeight;
  private int m_ImageLandHeight;
  private int m_ImageLandWidth;
  private int m_ImageWidth;
  private Handle m_KeyEventHandle;
  private boolean m_LockPreviewGallery;
  private boolean m_MultiTouch;
  private int m_OffsetX;
  private int m_OffsetY;
  private int m_OrignalZ;
  private List<String> m_PendingUris;
  private TextView m_PhotoDeletedCountView = null;
  private List<Integer> m_PhotoDeletedCurrent = null;
  private List<String> m_PhotoDeletedUrls = null;
  private RotateRelativeLayout m_PreviewGallery;
  private int m_PreviousPosition;
  private Resources m_Res;
  private boolean m_ResetFromLaunch = false;
  private ToastManager m_ToastManager;
  private ViewGroup m_UndoDeletionBar;
  private TextView m_UndoDeletionBtton = null;
  private PreviewPagerAdapter m_VerticalAdapter;
  private CameraPager m_VerticalViewPager;
  private CameraPager m_ViewPager;
  
  PreviewGallery(CameraActivity paramCameraActivity)
  {
    super("Preview Gallery", paramCameraActivity, true);
  }
  
  private void animatePhotograph(final View paramView, float paramFloat, final long paramLong, int paramInt1, final int paramInt2)
  {
    if (this.m_Animator != null) {
      return;
    }
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
            this.val$image.scrollTo(0, 0);
            this.val$image.setPivotX(this.val$width / 2);
            this.val$image.setPivotY(this.val$height / 2);
            PreviewGallery.-set0(PreviewGallery.this, null);
            if (((Boolean)PreviewGallery.this.getCameraActivity().get(CameraActivity.PROP_IS_BURST_PHOTO_ON_CAPTURE)).booleanValue())
            {
              Log.d(PreviewGallery.-get0(PreviewGallery.this), "burst , not to decode now");
              return;
            }
            PreviewGallery.-wrap12(PreviewGallery.this, 1, false);
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
  
  private void bringToBack()
  {
    this.m_BG.setAlpha(0.0F);
    if (!this.m_Front) {
      return;
    }
    Log.v(this.TAG, "bringToBack()");
    this.m_Front = false;
    setReadOnly(PROP_IS_GALLERY_VISIBLE, Boolean.valueOf(this.m_Front));
    ViewGroup localViewGroup = (ViewGroup)this.m_PreviewGallery.getParent();
    localViewGroup.removeView(this.m_PreviewGallery);
    localViewGroup.addView(this.m_PreviewGallery, this.m_OrignalZ);
    hideUndoDeletionBar(false);
    Handle.close(this.m_KeyEventHandle);
    Handle.close(this.m_FlashDisableHandle);
  }
  
  private void bringToFront()
  {
    bringToFront(true);
  }
  
  private void bringToFront(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.m_BG.setAlpha(1.0F);
    }
    if (this.m_Front) {
      return;
    }
    Log.v(this.TAG, "bringToFront()");
    this.m_Front = true;
    setReadOnly(PROP_IS_GALLERY_VISIBLE, Boolean.valueOf(this.m_Front));
    this.m_PreviewGallery.bringToFront();
    if (!Handle.isValid(this.m_KeyEventHandle)) {
      this.m_KeyEventHandle = getCameraActivity().setKeyEventHandler(this);
    }
    FlashController localFlashController = (FlashController)getCameraActivity().findComponent(FlashController.class);
    if (localFlashController != null) {
      this.m_FlashDisableHandle = localFlashController.disableFlash(FlashController.FlashDisabledReason.NOT_SUPPORTED_IN_SCENE, 0);
    }
  }
  
  private void disableCamera()
  {
    if (!Handle.isValid(this.m_CaptureModeChangeCUDHandle)) {
      this.m_CaptureModeChangeCUDHandle = getCameraActivity().disableCaptureUI("PreviewGallery", 1);
    }
  }
  
  private void enableCamera()
  {
    this.m_CaptureModeChangeCUDHandle = Handle.close(this.m_CaptureModeChangeCUDHandle);
  }
  
  private void finishPagerScroll(MotionEvent paramMotionEvent)
  {
    paramMotionEvent = MotionEvent.obtain(paramMotionEvent);
    paramMotionEvent.setAction(1);
    this.m_ViewPager.onTouchEvent(paramMotionEvent);
    this.m_VerticalViewPager.onTouchEvent(paramMotionEvent);
  }
  
  private void initLandscape(CameraActivity paramCameraActivity)
  {
    this.m_VerticalViewPager = ((CameraPager)this.m_PreviewGallery.findViewById(2131362040));
    this.m_VerticalViewPager.setOverScrollMode(2);
    this.m_VerticalViewPager.setOffscreenPageLimit(2);
    this.m_VerticalViewPager.setPageMargin(this.m_Res.getDimensionPixelOffset(2131296501));
    this.m_VerticalAdapter = new PreviewPagerAdapter(true);
    setOnPageChangeListener(this.m_VerticalViewPager, this.m_VerticalAdapter);
    preFetch(this.m_VerticalAdapter, 0, true);
    setOnTouchListener(this.m_VerticalViewPager);
  }
  
  private void initPager(final CameraActivity paramCameraActivity)
  {
    ComponentUtils.findComponent(getCameraThread(), FileManager.class, this, new ComponentSearchCallback()
    {
      public void onComponentFound(FileManager paramAnonymousFileManager)
      {
        Log.d(PreviewGallery.-get0(PreviewGallery.this), "onComponentFound");
        PreviewGallery.-set1(PreviewGallery.this, paramAnonymousFileManager);
        PreviewGallery.-wrap8(PreviewGallery.this, paramCameraActivity);
        PreviewGallery.-wrap7(PreviewGallery.this, paramCameraActivity);
        if ((Rotation.PORTRAIT == PreviewGallery.-wrap0(PreviewGallery.this)) || (Rotation.INVERSE_PORTRAIT == PreviewGallery.-wrap0(PreviewGallery.this)))
        {
          PreviewGallery.-get14(PreviewGallery.this).setVisibility(4);
          PreviewGallery.-get15(PreviewGallery.this).setVisibility(0);
        }
        for (;;)
        {
          HandlerUtils.post(PreviewGallery.-get3(PreviewGallery.this), new Runnable()
          {
            public void run()
            {
              Log.v(PreviewGallery.-get0(PreviewGallery.this), "run()");
              PreviewGallery.-get3(PreviewGallery.this).addHandler(FileManager.EVENT_MEDIA_FILES_RESET, new EventHandler()
              {
                public void onEventReceived(EventSource paramAnonymous3EventSource, EventKey<EventArgs> paramAnonymous3EventKey, EventArgs paramAnonymous3EventArgs)
                {
                  HandlerUtils.sendMessage(PreviewGallery.this, 1000);
                }
              });
              PreviewGallery.-get3(PreviewGallery.this).addHandler(FileManager.EVENT_MEDIA_SAVE_FAILED, new EventHandler()
              {
                public void onEventReceived(EventSource paramAnonymous3EventSource, EventKey<MediaEventArgs> paramAnonymous3EventKey, MediaEventArgs paramAnonymous3MediaEventArgs)
                {
                  Log.d(PreviewGallery.-get0(PreviewGallery.this), "EVENT_MEDIA_SAVE_FAILED");
                  HandlerUtils.sendMessage(PreviewGallery.this, 1000);
                }
              });
              PreviewGallery.-get3(PreviewGallery.this).addHandler(FileManager.EVENT_MEDIA_FILE_ADDED, new EventHandler()
              {
                public void onEventReceived(EventSource paramAnonymous3EventSource, EventKey<MediaEventArgs> paramAnonymous3EventKey, MediaEventArgs paramAnonymous3MediaEventArgs)
                {
                  Log.d(PreviewGallery.-get0(PreviewGallery.this), "EVENT_MEDIA_FILE_ADDED onEventReceived ");
                  if (paramAnonymous3MediaEventArgs.getThumbnail() != null)
                  {
                    Log.d(PreviewGallery.-get0(PreviewGallery.this), "EVENT_MEDIA_FILE_ADDED e.getThumbnail() " + paramAnonymous3MediaEventArgs.getThumbnail());
                    HandlerUtils.sendMessage(PreviewGallery.this, 1001, 0, 0, paramAnonymous3MediaEventArgs);
                    return;
                  }
                  Log.e(PreviewGallery.-get0(PreviewGallery.this), "no thumbnail error");
                }
              });
              PreviewGallery.-get3(PreviewGallery.this).addHandler(FileManager.EVENT_MEDIA_FILE_DELETED, new EventHandler()
              {
                public void onEventReceived(EventSource paramAnonymous3EventSource, EventKey<MediaEventArgs> paramAnonymous3EventKey, MediaEventArgs paramAnonymous3MediaEventArgs) {}
              });
              PreviewGallery.-get3(PreviewGallery.this).addHandler(FileManager.EVENT_MEDIA_SAVED, new EventHandler()
              {
                public void onEventReceived(EventSource paramAnonymous3EventSource, EventKey<MediaEventArgs> paramAnonymous3EventKey, MediaEventArgs paramAnonymous3MediaEventArgs)
                {
                  HandlerUtils.sendMessage(PreviewGallery.this, 1003, 0, 0, paramAnonymous3MediaEventArgs);
                  if (PreviewGallery.-get1(PreviewGallery.this) == null) {
                    HandlerUtils.sendMessage(PreviewGallery.this, 1004, 0, 0, paramAnonymous3MediaEventArgs);
                  }
                }
              });
              HandlerUtils.sendMessage(PreviewGallery.this, 1000);
            }
          });
          return;
          PreviewGallery.-get15(PreviewGallery.this).setVisibility(4);
          PreviewGallery.-get14(PreviewGallery.this).setVisibility(0);
        }
      }
    });
  }
  
  private void initPortrait(CameraActivity paramCameraActivity)
  {
    this.m_ViewPager = ((CameraPager)this.m_PreviewGallery.findViewById(2131362039));
    this.m_ViewPager.setOverScrollMode(2);
    this.m_ViewPager.setOffscreenPageLimit(2);
    this.m_ViewPager.setPageMargin(this.m_Res.getDimensionPixelOffset(2131296501));
    this.m_Adapter = new PreviewPagerAdapter(false);
    setOnPageChangeListener(this.m_ViewPager, this.m_Adapter);
    preFetch(this.m_Adapter, 0, true);
    setOnTouchListener(this.m_ViewPager);
  }
  
  private static void launchGallery(PreviewGallery paramPreviewGallery, String paramString, boolean paramBoolean)
  {
    Object localObject = paramPreviewGallery.m_FileManager.getFileUri(paramString);
    Log.d(paramPreviewGallery.TAG, "onClick uri: " + localObject);
    if ((paramBoolean) && (localObject == null))
    {
      paramPreviewGallery.m_PendingUris.add(paramString);
      return;
    }
    if (!new File(paramString).exists())
    {
      if (paramPreviewGallery.m_ToastManager == null) {
        paramPreviewGallery.m_ToastManager = ((ToastManager)paramPreviewGallery.findComponent(ToastManager.class));
      }
      if (paramPreviewGallery.m_ToastManager != null)
      {
        paramPreviewGallery.m_ToastManager.showToast(paramPreviewGallery.getCameraActivity().getString(2131558586), 0);
        paramPreviewGallery.m_ResetFromLaunch = true;
      }
      paramPreviewGallery.m_FileManager.scanFiles();
      return;
    }
    paramPreviewGallery.m_PendingUris.clear();
    Intent localIntent = new Intent();
    localIntent.setAction("android.intent.action.VIEW");
    localIntent.setDataAndType((Uri)localObject, paramPreviewGallery.getContext().getContentResolver().getType((Uri)localObject));
    if (paramPreviewGallery.m_HasDefaultGallery)
    {
      localIntent.setPackage("com.android.gallery3d");
      if (((Boolean)paramPreviewGallery.getCameraActivity().get(CameraActivity.PROP_IS_SECURE_MODE)).booleanValue())
      {
        localObject = new Bundle();
        ((Bundle)localObject).putBoolean("SECURE_MODE", true);
        ((Bundle)localObject).putSerializable("FILES_LIST", (Serializable)PreviewPagerAdapter.-get0(paramPreviewGallery.m_Adapter));
        localIntent.putExtras((Bundle)localObject);
      }
    }
    localObject = paramPreviewGallery.getCameraActivity();
    localIntent.addFlags(65536);
    try
    {
      ((CameraActivity)localObject).startActivityForResult(localIntent, 100);
      ((CameraActivity)localObject).overridePendingTransition(0, 0);
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e(paramPreviewGallery.TAG, "startActivityForResult - Fail to launch gallery, file path :" + paramString);
    }
  }
  
  private void lockPreviewGallery(boolean paramBoolean)
  {
    this.m_LockPreviewGallery = paramBoolean;
  }
  
  private void preFetch(PreviewPagerAdapter paramPreviewPagerAdapter, int paramInt, boolean paramBoolean)
  {
    this.m_FileManager.setCurrent(paramInt);
    int i = 0;
    if (i < 3)
    {
      if (i == 0) {
        if (paramInt != 0) {
          PreviewPagerAdapter.-wrap7(paramPreviewPagerAdapter, paramInt, paramBoolean);
        }
      }
      for (;;)
      {
        i += 1;
        break;
        int j = paramInt + i;
        if (j < paramPreviewPagerAdapter.getCount()) {
          PreviewPagerAdapter.-wrap7(paramPreviewPagerAdapter, j, paramBoolean);
        }
        j = paramInt - i;
        if (j > 0) {
          PreviewPagerAdapter.-wrap7(paramPreviewPagerAdapter, j, paramBoolean);
        }
      }
    }
  }
  
  private void resetCache(int paramInt, boolean paramBoolean)
  {
    Log.d(this.TAG, "position " + paramInt);
    if (paramInt < 1)
    {
      Log.d(this.TAG, "position error : " + paramInt);
      return;
    }
    if (paramInt > this.m_Adapter.getCount() - 1)
    {
      Log.d(this.TAG, "position error : " + paramInt);
      return;
    }
    PreviewPagerAdapter.-get1(this.m_Adapter).clear();
    PreviewPagerAdapter.-get1(this.m_VerticalAdapter).clear();
    if ((Rotation.PORTRAIT == getRotation()) || (Rotation.INVERSE_PORTRAIT == getRotation()))
    {
      preFetch(this.m_Adapter, paramInt, paramBoolean);
      return;
    }
    preFetch(this.m_VerticalAdapter, paramInt, paramBoolean);
  }
  
  private void setOnPageChangeListener(final CameraPager paramCameraPager, final PreviewPagerAdapter paramPreviewPagerAdapter)
  {
    paramCameraPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
    {
      public void onPageScrollStateChanged(int paramAnonymousInt)
      {
        if (paramAnonymousInt == 1) {
          PreviewGallery.PreviewPagerAdapter.-wrap5(paramPreviewPagerAdapter);
        }
      }
      
      public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2)
      {
        int i = paramCameraPager.getCurrentItem();
        int j = paramCameraPager.getVisibility();
        Log.d(PreviewGallery.-get0(PreviewGallery.this), "positionOffset: " + paramAnonymousFloat + " positionOffsetPixels: " + paramAnonymousInt2 + " current: " + i + " position: " + paramAnonymousInt1);
        if ((paramAnonymousInt1 == 0) && (j == 0) && ((i == 0) || (i == 1)))
        {
          if (paramAnonymousFloat != 0.0F)
          {
            Log.d(PreviewGallery.-get0(PreviewGallery.this), "set alpha" + 1.0F * paramAnonymousFloat);
            PreviewGallery.-get2(PreviewGallery.this).setAlpha(1.0F * paramAnonymousFloat);
          }
          if ((!PreviewGallery.-get4(PreviewGallery.this)) || (paramAnonymousInt2 != 0)) {
            break label208;
          }
          PreviewGallery.-wrap1(PreviewGallery.this);
        }
        for (;;)
        {
          if ((j == 0) && (PreviewGallery.-get4(PreviewGallery.this)) && (paramAnonymousInt2 == 0) && ((i == 0) || (i == 1))) {
            PreviewGallery.-wrap2(PreviewGallery.this);
          }
          return;
          label208:
          if (paramAnonymousInt2 > 100) {
            PreviewGallery.-wrap3(PreviewGallery.this, false);
          }
        }
      }
      
      public void onPageSelected(int paramAnonymousInt)
      {
        boolean bool = false;
        Log.v(PreviewGallery.-get0(PreviewGallery.this), "onPageSelected() - Type : portrait, position : ", Integer.valueOf(paramAnonymousInt));
        PreviewGallery localPreviewGallery;
        PreviewGallery.PreviewPagerAdapter localPreviewPagerAdapter;
        if (paramAnonymousInt == 0)
        {
          PreviewGallery.-wrap1(PreviewGallery.this);
          PreviewGallery.-wrap5(PreviewGallery.this);
          localPreviewGallery = PreviewGallery.this;
          localPreviewPagerAdapter = paramPreviewPagerAdapter;
          if (PreviewGallery.-get12(PreviewGallery.this) != 0) {
            break label92;
          }
        }
        for (;;)
        {
          PreviewGallery.-wrap11(localPreviewGallery, localPreviewPagerAdapter, paramAnonymousInt, bool);
          PreviewGallery.-set4(PreviewGallery.this, paramAnonymousInt);
          return;
          PreviewGallery.-wrap2(PreviewGallery.this);
          PreviewGallery.-wrap4(PreviewGallery.this);
          break;
          label92:
          bool = true;
        }
      }
    });
  }
  
  private void setOnTouchListener(final CameraPager paramCameraPager)
  {
    paramCameraPager.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        if (PreviewGallery.-get10(PreviewGallery.this)) {
          return true;
        }
        boolean bool3 = false;
        boolean bool2 = false;
        int i = paramCameraPager.getCurrentItem();
        int j = paramAnonymousMotionEvent.getAction() & 0xFF;
        int k = paramAnonymousMotionEvent.getPointerCount();
        MotionEvent localMotionEvent;
        label121:
        boolean bool1;
        if (j == 1)
        {
          PreviewGallery.-set2(PreviewGallery.this, false);
          if (j == 5) {
            PreviewGallery.-wrap6(PreviewGallery.this, paramAnonymousMotionEvent);
          }
          paramAnonymousView = PreviewGallery.this.getCameraActivity();
          if (i == 0)
          {
            localMotionEvent = MotionEvent.obtain(paramAnonymousMotionEvent);
            localMotionEvent.setLocation(paramAnonymousMotionEvent.getRawX(), paramAnonymousMotionEvent.getRawY());
            if (!PreviewGallery.-get5(PreviewGallery.this)) {
              break label304;
            }
            paramAnonymousView.onTouchEvent(localMotionEvent);
          }
          bool1 = bool3;
          if (!PreviewGallery.-get4(PreviewGallery.this))
          {
            bool1 = bool3;
            if (i == 0)
            {
              if (PreviewGallery.-get11(PreviewGallery.this)) {
                bool2 = true;
              }
              if (k > 1) {
                PreviewGallery.-set3(PreviewGallery.this, true);
              }
              bool1 = bool2;
              if (k == 1)
              {
                bool1 = bool2;
                if (j == 1)
                {
                  PreviewGallery.-set3(PreviewGallery.this, false);
                  bool1 = bool2;
                }
              }
            }
          }
          paramAnonymousMotionEvent = (VideoCaptureState)paramAnonymousView.get(CameraActivity.PROP_VIDEO_CAPTURE_STATE);
          if ((paramAnonymousMotionEvent != VideoCaptureState.CAPTURING) && (paramAnonymousMotionEvent != VideoCaptureState.STARTING)) {
            break label339;
          }
        }
        for (;;)
        {
          bool1 = true;
          label304:
          label339:
          do
          {
            if (!((Boolean)paramAnonymousView.get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue()) {
              bool1 = true;
            }
            if (((Boolean)paramAnonymousView.get(CameraActivity.PROP_IS_SELF_TIMER_STARTED)).booleanValue()) {
              bool1 = true;
            }
            if (PreviewGallery.-get5(PreviewGallery.this)) {
              bool1 = true;
            }
            return bool1;
            if (j != 3) {
              break;
            }
            PreviewGallery.-set2(PreviewGallery.this, false);
            break;
            PreviewGallery.-set2(PreviewGallery.this, paramAnonymousView.onTouchEvent(localMotionEvent));
            if (!PreviewGallery.-get5(PreviewGallery.this)) {
              break label121;
            }
            PreviewGallery.-wrap6(PreviewGallery.this, paramAnonymousMotionEvent);
            break label121;
          } while (paramAnonymousMotionEvent != VideoCaptureState.STOPPING);
        }
      }
    });
  }
  
  protected void handleMessage(Message paramMessage)
  {
    if ((this.m_ViewPager == null) || (this.m_VerticalViewPager == null)) {
      return;
    }
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
    case 1002: 
    case 1000: 
    case 1001: 
    case 1003: 
    case 1004: 
    case 1005: 
      label290:
      label1259:
      do
      {
        for (;;)
        {
          return;
          paramMessage = (File)paramMessage.obj;
          int j;
          if ((Rotation.PORTRAIT == getRotation()) || (Rotation.INVERSE_PORTRAIT == getRotation()))
          {
            i = this.m_ViewPager.getCurrentItem();
            this.m_DeletedIndex = i;
            this.m_ViewPager.setAdapter(null);
            this.m_VerticalViewPager.setAdapter(null);
            PreviewPagerAdapter.-wrap3(this.m_Adapter, paramMessage);
            PreviewPagerAdapter.-wrap3(this.m_VerticalAdapter, paramMessage);
            this.m_ViewPager.setAdapter(this.m_Adapter);
            this.m_VerticalViewPager.setAdapter(this.m_VerticalAdapter);
            j = Math.min(i, this.m_Adapter.getCount() - 1);
            this.m_ViewPager.setCurrentItem(j, false);
            this.m_VerticalViewPager.setCurrentItem(j, false);
            if ((j != 0) || (this.m_Adapter.getCount() > 1)) {
              break label290;
            }
            Log.v(this.TAG, "handleMessage() - All items are deleted");
            bringToBack();
            enableCamera();
          }
          do
          {
            resetCache(j, true);
            this.m_DeletedFilePath = paramMessage.getPath();
            HandlerUtils.sendMessage(this, 1005, 0, 0, paramMessage.getPath(), 5000L);
            return;
            i = this.m_VerticalViewPager.getCurrentItem();
            break;
            this.m_PhotoDeletedCountView.setText(String.format(this.m_DeletedCountString, new Object[] { Integer.valueOf(1) }));
          } while (this.m_UndoDeletionBar.getVisibility() == 0);
          Object localObject1 = getCameraActivity().getResources();
          if (getRotation().isLandscape()) {}
          for (int i = 2131296583;; i = 2131296584)
          {
            i = ((Resources)localObject1).getDimensionPixelSize(i);
            this.m_CameraUndoDeletion.setRotation((Rotation)getCameraActivity().get(CameraActivity.PROP_ROTATION));
            this.m_UndoDeletionBar.setAlpha(0.0F);
            this.m_UndoDeletionBar.setVisibility(0);
            this.m_UndoDeletionBar.setPadding(i, 0, i, 0);
            this.m_UndoDeletionBar.animate().setDuration(200L).alpha(1.0F).setListener(null).start();
            break;
          }
          if (this.m_UndoDeletionBar != null) {
            this.m_UndoDeletionBar.setVisibility(8);
          }
          this.m_ViewPager.setAdapter(null);
          this.m_VerticalViewPager.setAdapter(null);
          PreviewPagerAdapter.-wrap4(this.m_Adapter, this);
          PreviewPagerAdapter.-wrap4(this.m_VerticalAdapter, this);
          this.m_ViewPager.setAdapter(this.m_Adapter);
          this.m_VerticalViewPager.setAdapter(this.m_VerticalAdapter);
          if (!this.m_ResetFromLaunch)
          {
            resetCache(1, true);
            if (this.m_ViewPager != null) {
              this.m_ViewPager.setCurrentItem(0, false);
            }
            if (this.m_VerticalViewPager != null) {
              this.m_VerticalViewPager.setCurrentItem(0, false);
            }
            this.m_PreviousPosition = 0;
            bringToBack();
            enableCamera();
            return;
          }
          i = Math.min(this.m_PreviousPosition, this.m_Adapter.getCount() - 1);
          this.m_ViewPager.setCurrentItem(i, false);
          this.m_VerticalViewPager.setCurrentItem(i, false);
          resetCache(i, true);
          this.m_ResetFromLaunch = false;
          if ((i == 0) && (this.m_Adapter.getCount() <= 1))
          {
            bringToBack();
            enableCamera();
            return;
            Log.d(this.TAG, "MESSAGE_UPDATE_ADDED");
            localObject1 = (MediaEventArgs)paramMessage.obj;
            paramMessage = ((MediaEventArgs)localObject1).getThumbnail();
            if (paramMessage == null)
            {
              Log.e(this.TAG, "bitmap null ");
              return;
            }
            localObject1 = ((MediaEventArgs)localObject1).getFilePath();
            if (localObject1 == null)
            {
              Log.e(this.TAG, "path null ");
              return;
            }
            Object localObject2 = new File((String)localObject1);
            if ((Rotation.PORTRAIT == getRotation()) || (Rotation.INVERSE_PORTRAIT == getRotation()))
            {
              this.m_ViewPager.getCurrentItem();
              PreviewPagerAdapter.-wrap1(this.m_Adapter, (File)localObject2, 0);
              PreviewPagerAdapter.-wrap1(this.m_VerticalAdapter, (File)localObject2, 0);
              localObject2 = (ScaleImageView)((View)PreviewPagerAdapter.-get2(this.m_Adapter).get(0)).findViewById(2131362045);
              ((ScaleImageView)localObject2).setPhoto(paramMessage, (String)localObject1, this, false);
              ((ScaleImageView)localObject2).scrollTo(this.m_OffsetX, 0);
              ((ScaleImageView)localObject2).setPivotX(0.0F);
              ((ScaleImageView)localObject2).setPivotY(this.m_ImageHeight);
              animatePhotograph((View)localObject2, -5.0F, 100L, this.m_ImageWidth, this.m_ImageHeight);
              return;
            }
            this.m_VerticalViewPager.getCurrentItem();
            PreviewPagerAdapter.-wrap1(this.m_VerticalAdapter, (File)localObject2, 0);
            PreviewPagerAdapter.-wrap1(this.m_Adapter, (File)localObject2, 0);
            localObject2 = (ScaleImageView)((View)PreviewPagerAdapter.-get2(this.m_VerticalAdapter).get(0)).findViewById(2131362045);
            i = paramMessage.getWidth();
            j = paramMessage.getHeight();
            this.m_OffsetY = ((int)((this.m_ImageLandHeight - this.m_ImageLandWidth * (i / j)) / 2.0F));
            ((ScaleImageView)localObject2).setPhoto(paramMessage, (String)localObject1, this, false);
            if (Rotation.LANDSCAPE == getRotation())
            {
              ((ScaleImageView)localObject2).scrollTo(0, this.m_OffsetY);
              ((ScaleImageView)localObject2).setPivotX(0.0F);
              ((ScaleImageView)localObject2).setPivotY(0.0F);
            }
            for (float f = -10.0F;; f = 10.0F)
            {
              animatePhotograph((View)localObject2, f, 100L, this.m_ImageLandWidth, this.m_ImageLandHeight);
              return;
              ((ScaleImageView)localObject2).scrollTo(0, -this.m_OffsetY);
              ((ScaleImageView)localObject2).setPivotX(0.0F);
              ((ScaleImageView)localObject2).setPivotY(this.m_ImageLandHeight);
            }
            paramMessage = (MediaEventArgs)paramMessage.obj;
            j = 0;
            localObject1 = this.m_PendingUris.iterator();
            do
            {
              i = j;
              if (!((Iterator)localObject1).hasNext()) {
                break;
              }
              localObject2 = (String)((Iterator)localObject1).next();
            } while (!paramMessage.getFilePath().equals(localObject2));
            i = 1;
            if (i != 0)
            {
              launchGallery(this, paramMessage.getFilePath(), false);
              return;
              localObject1 = (MediaEventArgs)paramMessage.obj;
              if ((Rotation.PORTRAIT == getRotation()) || (Rotation.INVERSE_PORTRAIT == getRotation()))
              {
                paramMessage = this.m_Adapter;
                localObject2 = PreviewPagerAdapter.-get0(paramMessage);
                j = ((List)localObject2).size();
                i = 0;
              }
              for (;;)
              {
                if (i >= j) {
                  break label1259;
                }
                if (((File)((List)localObject2).get(i)).getAbsolutePath().equals(((MediaEventArgs)localObject1).getFilePath()))
                {
                  PreviewPagerAdapter.-wrap7(paramMessage, i + 1, false);
                  Log.d(this.TAG, "redecode success position: " + i);
                  return;
                  paramMessage = this.m_VerticalAdapter;
                  break;
                }
                i += 1;
              }
            }
          }
        }
        paramMessage = (String)paramMessage.obj;
        this.m_FileManager.deleteFile(paramMessage);
      } while ((paramMessage == null) || (!paramMessage.equals(this.m_DeletedFilePath)));
      this.m_DeletedFilePath = null;
      this.m_DeletedIndex = -1;
      hideUndoDeletionBar(false);
      return;
    }
    if ((PreviewPagerAdapter.-get0(this.m_Adapter).size() <= 0) || (this.m_DeletedIndex > PreviewPagerAdapter.-get0(this.m_Adapter).size() + 1)) {}
    while ((this.m_DeletedIndex < 0) || (this.m_DeletedFilePath == null)) {
      return;
    }
    HandlerUtils.removeMessages(this, 1005, this.m_DeletedFilePath);
    paramMessage = new File(this.m_DeletedFilePath);
    PreviewPagerAdapter.-wrap1(this.m_Adapter, paramMessage, this.m_DeletedIndex - 1);
    PreviewPagerAdapter.-wrap1(this.m_VerticalAdapter, paramMessage, this.m_DeletedIndex - 1);
    this.m_DeletedIndex = Math.min(this.m_DeletedIndex, this.m_Adapter.getCount() - 1);
    this.m_ViewPager.setCurrentItem(this.m_DeletedIndex, true);
    this.m_VerticalViewPager.setCurrentItem(this.m_DeletedIndex, true);
    resetCache(this.m_DeletedIndex, false);
    this.m_DeletedFilePath = null;
    this.m_DeletedIndex = -1;
    hideUndoDeletionBar(true);
  }
  
  public void hideUndoDeletionBar(boolean paramBoolean)
  {
    if ((this.m_UndoDeletionBar != null) && (this.m_UndoDeletionBar.getVisibility() == 0))
    {
      if (!paramBoolean) {
        break label90;
      }
      this.m_UndoDeletionBar.animate().setDuration(200L).alpha(0.0F).setListener(this.m_AnimatorListener).start();
    }
    for (;;)
    {
      if (this.m_DeletedFilePath != null)
      {
        HandlerUtils.removeMessages(this, 1005, this.m_DeletedFilePath);
        this.m_FileManager.deleteFile(this.m_DeletedFilePath);
        this.m_DeletedFilePath = null;
        this.m_DeletedIndex = -1;
      }
      return;
      label90:
      this.m_UndoDeletionBar.setVisibility(8);
    }
  }
  
  protected void onDeinitialize()
  {
    if (this.m_ViewPager != null) {
      this.m_ViewPager.setAdapter(null);
    }
    if (this.m_VerticalViewPager != null) {
      this.m_VerticalViewPager.setAdapter(null);
    }
    if (this.m_ViewPager != null) {
      this.m_ViewPager.removeAllViews();
    }
    if (this.m_VerticalViewPager != null) {
      this.m_VerticalViewPager.removeAllViews();
    }
    if (this.m_VerticalAdapter != null) {
      PreviewPagerAdapter.-wrap2(this.m_VerticalAdapter);
    }
    if (this.m_Adapter != null) {
      PreviewPagerAdapter.-wrap2(this.m_Adapter);
    }
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    final CameraActivity localCameraActivity = getCameraActivity();
    this.m_PreviewGallery = ((RotateRelativeLayout)localCameraActivity.findViewById(2131362037));
    this.m_BG = this.m_PreviewGallery.findViewById(2131362038);
    this.m_CameraUndoDeletion = ((RotateRelativeLayout)this.m_PreviewGallery.findViewById(2131362041));
    this.m_Res = getContext().getResources();
    Object localObject = getScreenSize();
    this.m_ImageWidth = ((ScreenSize)localObject).getWidth();
    this.m_ImageHeight = ((ScreenSize)localObject).getHeight();
    this.m_ImageLandWidth = ((ScreenSize)localObject).getWidth();
    this.m_ImageLandHeight = ((ScreenSize)localObject).getHeight();
    this.m_OffsetX = ((((ScreenSize)localObject).getWidth() - this.m_ImageWidth) / 2);
    this.m_PendingUris = new ArrayList();
    this.m_UndoDeletionBar = ((ViewGroup)this.m_PreviewGallery.findViewById(2131362042));
    this.m_UndoDeletionBar.setClickable(true);
    this.m_DeletedCountString = getCameraActivity().getString(2131558585);
    this.m_PhotoDeletedCountView = ((TextView)this.m_PreviewGallery.findViewById(2131362043));
    this.m_UndoDeletionBtton = ((TextView)this.m_PreviewGallery.findViewById(2131362044));
    this.m_UndoDeletionBtton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        HandlerUtils.sendMessage(PreviewGallery.this, 1006);
      }
    });
    localObject = (ViewGroup)this.m_PreviewGallery.getParent();
    int i = 0;
    while (i < ((ViewGroup)localObject).getChildCount())
    {
      if (((ViewGroup)localObject).getChildAt(i).getId() == 2131362037) {
        this.m_OrignalZ = i;
      }
      i += 1;
    }
    initPager(getCameraActivity());
    localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.RESUMING)
        {
          if (PreviewGallery.-get3(PreviewGallery.this) != null) {
            PreviewGallery.-get3(PreviewGallery.this).scanFiles();
          }
          if (PreviewGallery.-get15(PreviewGallery.this) != null) {
            PreviewGallery.-get15(PreviewGallery.this).setCurrentItem(0, false);
          }
          if (PreviewGallery.-get14(PreviewGallery.this) != null) {
            PreviewGallery.-get14(PreviewGallery.this).setCurrentItem(0, false);
          }
          if (((Boolean)localCameraActivity.get(CameraActivity.PROP_IS_SECURE_MODE)).booleanValue()) {
            HandlerUtils.sendMessage(PreviewGallery.this, 1000);
          }
        }
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.PAUSING) {
          PreviewGallery.this.hideUndoDeletionBar(false);
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_SECURE_MODE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        HandlerUtils.sendMessage(PreviewGallery.this, 1000);
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_BURST_PHOTO_ON_CAPTURE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          PreviewGallery.-wrap12(PreviewGallery.this, 1, false);
        }
      }
    });
    findComponent(CaptureButtons.class, new ComponentSearchCallback()
    {
      public void onComponentFound(CaptureButtons paramAnonymousCaptureButtons)
      {
        paramAnonymousCaptureButtons.addHandler(CaptureButtons.EVENT_BUTTON_PRESSED, new EventHandler()
        {
          public void onEventReceived(EventSource paramAnonymous2EventSource, EventKey<CaptureButtonEventArgs> paramAnonymous2EventKey, CaptureButtonEventArgs paramAnonymous2CaptureButtonEventArgs)
          {
            PreviewGallery.-wrap10(PreviewGallery.this, true);
          }
        });
        paramAnonymousCaptureButtons.addHandler(CaptureButtons.EVENT_BUTTON_RELEASED, new EventHandler()
        {
          public void onEventReceived(EventSource paramAnonymous2EventSource, EventKey<CaptureButtonEventArgs> paramAnonymous2EventKey, CaptureButtonEventArgs paramAnonymous2CaptureButtonEventArgs)
          {
            PreviewGallery.-wrap10(PreviewGallery.this, false);
          }
        });
      }
    });
    if (this.m_PreviewGallery.getContext().getPackageManager().queryIntentActivities(new Intent().setPackage("com.android.gallery3d"), 65536).size() > 0) {
      this.m_HasDefaultGallery = true;
    }
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
    Object localObject = KeyEventHandler.KeyResult.NOT_HANDLED;
    if (paramInt == 4)
    {
      paramKeyEventArgs = (KeyEventArgs)localObject;
      if (this.m_ViewPager != null)
      {
        paramKeyEventArgs = (KeyEventArgs)localObject;
        if (this.m_ViewPager.getCurrentItem() != 0)
        {
          paramKeyEventArgs = KeyEventHandler.KeyResult.HANDLED;
          PreviewPagerAdapter.-wrap5(this.m_Adapter);
          this.m_ViewPager.setCurrentItem(0, true);
        }
      }
      localObject = paramKeyEventArgs;
      if (this.m_VerticalViewPager != null)
      {
        localObject = paramKeyEventArgs;
        if (this.m_VerticalViewPager.getCurrentItem() != 0)
        {
          localObject = KeyEventHandler.KeyResult.HANDLED;
          PreviewPagerAdapter.-wrap5(this.m_VerticalAdapter);
          this.m_VerticalViewPager.setCurrentItem(0, true);
        }
      }
    }
    while ((paramInt != 25) && (paramInt != 24)) {
      return (KeyEventHandler.KeyResult)localObject;
    }
    return KeyEventHandler.KeyResult.HANDLED_AND_PASS_TO_SYSTEM;
  }
  
  protected void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    super.onRotationChanged(paramRotation1, paramRotation2);
    if ((Rotation.PORTRAIT == paramRotation2) || (Rotation.INVERSE_PORTRAIT == paramRotation2))
    {
      if ((Rotation.LANDSCAPE == paramRotation1) || (Rotation.INVERSE_LANDSCAPE == paramRotation1))
      {
        this.m_VerticalViewPager.setVisibility(4);
        this.m_PreviousPosition = this.m_VerticalViewPager.getCurrentItem();
        preFetch(this.m_Adapter, this.m_PreviousPosition, true);
        this.m_ViewPager.setVisibility(0);
        this.m_ViewPager.setCurrentItem(this.m_PreviousPosition, true);
        i = this.m_PreviousPosition - 2;
        if ((i <= this.m_PreviousPosition) && (i < PreviewPagerAdapter.-get2(this.m_VerticalAdapter).size() - 1))
        {
          if (i < 0) {}
          for (;;)
          {
            i += 1;
            break;
            ((ScaleImageView)((View)PreviewPagerAdapter.-get2(this.m_VerticalAdapter).get(i)).findViewById(2131362045)).cancelDeocdingFullSizeImage();
          }
        }
      }
      PreviewPagerAdapter.-wrap5(this.m_Adapter);
      this.m_PreviewGallery.setRotation(paramRotation2);
      if ((this.m_UndoDeletionBar != null) && (this.m_UndoDeletionBar.getVisibility() == 0))
      {
        paramRotation1 = getCameraActivity().getResources();
        if (!paramRotation2.isLandscape()) {
          break label432;
        }
      }
    }
    label432:
    for (int i = 2131296583;; i = 2131296584)
    {
      i = paramRotation1.getDimensionPixelSize(i);
      this.m_CameraUndoDeletion.setRotation(paramRotation2);
      this.m_UndoDeletionBar.setPadding(i, 0, i, 0);
      return;
      if ((Rotation.PORTRAIT == paramRotation1) || (Rotation.INVERSE_PORTRAIT == paramRotation1))
      {
        this.m_ViewPager.setVisibility(4);
        this.m_VerticalViewPager.setVisibility(0);
        this.m_PreviousPosition = this.m_ViewPager.getCurrentItem();
        this.m_VerticalViewPager.setCurrentItem(this.m_PreviousPosition);
        preFetch(this.m_VerticalAdapter, this.m_PreviousPosition, true);
        i = this.m_PreviousPosition - 2;
        if ((i <= this.m_PreviousPosition) && (i < PreviewPagerAdapter.-get2(this.m_Adapter).size() - 1))
        {
          if (i < 0) {}
          for (;;)
          {
            i += 1;
            break;
            ((ScaleImageView)((View)PreviewPagerAdapter.-get2(this.m_Adapter).get(i)).findViewById(2131362045)).cancelDeocdingFullSizeImage();
          }
        }
      }
      PreviewPagerAdapter.-wrap5(this.m_VerticalAdapter);
      if (Rotation.LANDSCAPE == paramRotation2)
      {
        PreviewPagerAdapter.-wrap6(this.m_VerticalAdapter, 0.0F);
        break;
      }
      PreviewPagerAdapter.-wrap6(this.m_VerticalAdapter, 180.0F);
      break;
    }
  }
  
  public void setSwipeable(boolean paramBoolean)
  {
    this.m_ViewPager.setSwipeable(paramBoolean);
    this.m_VerticalViewPager.setSwipeable(paramBoolean);
  }
  
  private static class PreviewPagerAdapter
    extends PagerAdapter
  {
    private static final String TAG = PreviewPagerAdapter.class.getSimpleName();
    private FileManager m_FileManager;
    private List<File> m_Files;
    private boolean m_IsVertical;
    private SparseArray<String> m_Map = new SparseArray();
    private float m_PageAngle;
    private int m_PageSize = 7;
    private List<View> m_Pagers = new ArrayList();
    private PreviewGallery m_PreviewGallery;
    private int m_ReqHeight;
    private int m_ReqWidth;
    
    public PreviewPagerAdapter(boolean paramBoolean)
    {
      this.m_IsVertical = paramBoolean;
    }
    
    private void addFile(File paramFile, int paramInt)
    {
      if (this.m_Files == null)
      {
        Log.w(TAG, "Failed on adding " + paramFile.getName());
        return;
      }
      if (((Boolean)this.m_PreviewGallery.getCameraActivity().get(CameraActivity.PROP_IS_SECURE_MODE)).booleanValue())
      {
        if (this.m_Files.size() != 100) {
          break label100;
        }
        this.m_Files.remove(99);
      }
      for (;;)
      {
        this.m_Files.add(paramInt, paramFile);
        notifyDataSetChanged();
        return;
        label100:
        if (this.m_Files.size() > 100) {
          Log.e(TAG, "m_Files addFile over max size error in secure mode, max size: 100 m_Files.size(): " + this.m_Files.size());
        }
      }
    }
    
    private void deinitialize()
    {
      this.m_Pagers.clear();
    }
    
    private void deleteFile(File paramFile)
    {
      if (this.m_Files == null)
      {
        Log.w(TAG, "Failed on deleting " + paramFile.getName());
        return;
      }
      Iterator localIterator = this.m_Files.iterator();
      while (localIterator.hasNext()) {
        if (((File)localIterator.next()).getAbsoluteFile().equals(paramFile.getAbsoluteFile())) {
          localIterator.remove();
        }
      }
      notifyDataSetChanged();
    }
    
    private int getCurrent()
    {
      if ((Rotation.PORTRAIT == PreviewGallery.-wrap0(this.m_PreviewGallery)) || (Rotation.INVERSE_PORTRAIT == PreviewGallery.-wrap0(this.m_PreviewGallery))) {
        return PreviewGallery.-get15(this.m_PreviewGallery).getCurrentItem();
      }
      return PreviewGallery.-get14(this.m_PreviewGallery).getCurrentItem();
    }
    
    private void handleLaunchGallery(ScaleImageView paramScaleImageView, final int paramInt, final String paramString)
    {
      paramScaleImageView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (paramInt == PreviewGallery.PreviewPagerAdapter.-wrap0(PreviewGallery.PreviewPagerAdapter.this)) {
            PreviewGallery.-wrap9(PreviewGallery.this, paramString, true);
          }
        }
      });
    }
    
    private void initialize(PreviewGallery paramPreviewGallery)
    {
      Log.v(TAG, "initialize()");
      this.m_PreviewGallery = paramPreviewGallery;
      Object localObject = this.m_PreviewGallery.getContext();
      this.m_FileManager = PreviewGallery.-get3(paramPreviewGallery);
      LayoutInflater localLayoutInflater;
      int i;
      if (((Boolean)this.m_PreviewGallery.getCameraActivity().get(CameraActivity.PROP_IS_SECURE_MODE)).booleanValue())
      {
        this.m_Files = new ArrayList();
        localLayoutInflater = LayoutInflater.from((Context)localObject);
        localObject = new FrameLayout((Context)localObject);
        this.m_Pagers.clear();
        i = 0;
        label91:
        if (i >= this.m_PageSize) {
          return;
        }
        if (!this.m_IsVertical) {
          break label159;
        }
      }
      label159:
      for (paramPreviewGallery = localLayoutInflater.inflate(2130903090, (ViewGroup)localObject, false);; paramPreviewGallery = localLayoutInflater.inflate(2130903089, (ViewGroup)localObject, false))
      {
        paramPreviewGallery.setRotation(this.m_PageAngle);
        this.m_Pagers.add(paramPreviewGallery);
        i += 1;
        break label91;
        this.m_Files = this.m_FileManager.getMediaFiles();
        break;
      }
    }
    
    private void resetPages()
    {
      Iterator localIterator = this.m_Pagers.iterator();
      while (localIterator.hasNext()) {
        ((ScaleImageView)((View)localIterator.next()).findViewById(2131362045)).reset();
      }
    }
    
    private void rotatePages(float paramFloat)
    {
      this.m_PageAngle = paramFloat;
      Iterator localIterator = this.m_Pagers.iterator();
      while (localIterator.hasNext()) {
        ((View)localIterator.next()).setRotation(paramFloat);
      }
    }
    
    private void setPageData(int paramInt, boolean paramBoolean)
    {
      if ((this.m_Files == null) || (this.m_Files.size() == 0)) {
        return;
      }
      final int i = (paramInt - 1) % this.m_PageSize;
      Object localObject1 = (File)this.m_Files.get(paramInt - 1);
      if (localObject1 == null) {
        return;
      }
      if (this.m_Pagers == null)
      {
        Log.d(TAG, "m_Pagers =null");
        return;
      }
      if (this.m_Pagers.size() == 0)
      {
        Log.d(TAG, "onDeinitialize m_Pagers size == 0");
        return;
      }
      if (!((File)localObject1).exists())
      {
        Log.d(TAG, "file does not exist : cacheIndex: " + i + ", position: " + paramInt + ", path: " + ((File)localObject1).getAbsolutePath());
        return;
      }
      localObject1 = ((File)localObject1).getAbsolutePath();
      Object localObject2 = (String)this.m_Map.get(i);
      if ((!TextUtils.isEmpty((CharSequence)localObject2)) && (((String)localObject2).equals(localObject1)))
      {
        Log.d(TAG, "setPageData already set return : cacheIndex: " + i + " position: " + paramInt);
        return;
      }
      this.m_Map.put(i, localObject1);
      Log.d(TAG, "cacheIndex(" + i + "): path = " + (String)localObject1);
      final Object localObject4 = (View)this.m_Pagers.get(i);
      if (this.m_IsVertical) {
        this.m_ReqWidth = PreviewGallery.-get8(this.m_PreviewGallery);
      }
      for (this.m_ReqHeight = PreviewGallery.-get7(this.m_PreviewGallery);; this.m_ReqHeight = PreviewGallery.-get6(this.m_PreviewGallery))
      {
        localObject2 = (ScaleImageView)((View)localObject4).findViewById(2131362045);
        ((ScaleImageView)localObject2).setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            return false;
          }
        });
        ((ScaleImageView)localObject2).setClickable(false);
        Object localObject5 = new SoftReference(localObject4);
        final Object localObject3 = new SoftReference(localObject2);
        if (paramBoolean) {
          ((ScaleImageView)localObject2).setImageDrawable(null);
        }
        localObject4 = new SoftReference((ImageView)((View)localObject4).findViewById(2131362046));
        ((ImageView)((SoftReference)localObject4).get()).setVisibility(8);
        localObject5 = (View)((SoftReference)localObject5).get();
        localObject3 = (ScaleImageView)((SoftReference)localObject3).get();
        handleLaunchGallery((ScaleImageView)localObject2, paramInt, (String)localObject1);
        BitmapPool.DEFAULT_THUMBNAIL.decode((String)localObject1, 0, 1920, 1920, 3, new BitmapPool.Callback()
        {
          public void onBitmapDecoded(Handle paramAnonymousHandle, String paramAnonymousString, Bitmap paramAnonymousBitmap)
          {
            if ((localObject3 == null) || (PreviewGallery.this == null)) {
              return;
            }
            paramAnonymousHandle = (String)PreviewGallery.PreviewPagerAdapter.-get1(PreviewGallery.PreviewPagerAdapter.this).get(i);
            if ((paramAnonymousHandle != null) && (paramAnonymousHandle.equals(paramAnonymousString)))
            {
              if (!Path.getExtension(paramAnonymousString).toLowerCase().equals(".mp4")) {
                break label101;
              }
              ((ImageView)localObject4.get()).setVisibility(0);
            }
            label101:
            for (boolean bool = true;; bool = false)
            {
              localObject3.setPhoto(paramAnonymousBitmap, paramAnonymousString, PreviewGallery.this, bool);
              return;
              return;
            }
          }
        }, this.m_PreviewGallery.getHandler());
        return;
        this.m_ReqWidth = PreviewGallery.-get9(this.m_PreviewGallery);
      }
    }
    
    public void destroyItem(View paramView, int paramInt, Object paramObject)
    {
      Log.d(TAG, "destroyItem:" + paramInt);
    }
    
    public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
    {
      Log.d(TAG, "destroyItem:" + paramInt);
    }
    
    public int getCount()
    {
      if (this.m_Files == null)
      {
        Log.w(TAG, "Return 0 due to failed on getCount.");
        return 0;
      }
      return this.m_Files.size() + 1;
    }
    
    public CharSequence getPageTitle(int paramInt)
    {
      return "Page " + paramInt;
    }
    
    public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
    {
      Log.d(TAG, "instantiateItem:" + paramInt);
      View localView2 = null;
      if (paramInt == 0) {
        localView1 = localView2;
      }
      try
      {
        return new View(this.m_PreviewGallery.getContext());
      }
      catch (Exception paramViewGroup)
      {
        int i;
        Log.e(TAG, paramViewGroup.getMessage());
      }
      View localView1 = localView2;
      i = this.m_PageSize;
      localView1 = localView2;
      localView2 = (View)this.m_Pagers.get((paramInt - 1) % i);
      localView1 = localView2;
      paramViewGroup.removeView(localView2);
      localView1 = localView2;
      paramViewGroup.addView(localView2);
      return localView2;
      return localView1;
    }
    
    public boolean isViewFromObject(View paramView, Object paramObject)
    {
      return paramView == paramObject;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/PreviewGallery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */