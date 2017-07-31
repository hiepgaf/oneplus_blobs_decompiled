package com.android.server.wm;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Debug;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.Message;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.util.SparseArray;
import android.view.AppTransitionAnimationSpec;
import android.view.IAppTransitionAnimationSpecsFuture;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerInternal.AppTransitionListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ClipRectAnimation;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import com.android.internal.R.styleable;
import com.android.internal.util.DumpUtils.Dump;
import com.android.server.AttributeCache;
import com.android.server.AttributeCache.Entry;
import com.android.server.wm.animation.ClipRectLRAnimation;
import com.android.server.wm.animation.ClipRectTBAnimation;
import com.android.server.wm.animation.CurvedTranslateAnimation;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppTransition
  implements DumpUtils.Dump
{
  private static final int APP_STATE_IDLE = 0;
  private static final int APP_STATE_READY = 1;
  private static final int APP_STATE_RUNNING = 2;
  private static final int APP_STATE_TIMEOUT = 3;
  private static final long APP_TRANSITION_TIMEOUT_MS = 5000L;
  private static final int CLIP_REVEAL_TRANSLATION_Y_DP = 8;
  static final int DEFAULT_APP_TRANSITION_DURATION = 200;
  private static final int MAX_CLIP_REVEAL_TRANSITION_DURATION = 420;
  private static final int NEXT_TRANSIT_TYPE_CLIP_REVEAL = 8;
  private static final int NEXT_TRANSIT_TYPE_CUSTOM = 1;
  private static final int NEXT_TRANSIT_TYPE_CUSTOM_IN_PLACE = 7;
  private static final int NEXT_TRANSIT_TYPE_NONE = 0;
  private static final int NEXT_TRANSIT_TYPE_SCALE_UP = 2;
  private static final int NEXT_TRANSIT_TYPE_THUMBNAIL_ASPECT_SCALE_DOWN = 6;
  private static final int NEXT_TRANSIT_TYPE_THUMBNAIL_ASPECT_SCALE_UP = 5;
  private static final int NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_DOWN = 4;
  private static final int NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_UP = 3;
  private static final float RECENTS_THUMBNAIL_FADEIN_FRACTION = 0.5F;
  private static final float RECENTS_THUMBNAIL_FADEOUT_FRACTION = 0.5F;
  private static final String TAG = "WindowManager";
  private static final int THUMBNAIL_APP_TRANSITION_DURATION = 200;
  private static final Interpolator THUMBNAIL_DOCK_INTERPOLATOR = new PathInterpolator(0.85F, 0.0F, 1.0F, 1.0F);
  private static final int THUMBNAIL_TRANSITION_ENTER_SCALE_DOWN = 2;
  private static final int THUMBNAIL_TRANSITION_ENTER_SCALE_UP = 0;
  private static final int THUMBNAIL_TRANSITION_EXIT_SCALE_DOWN = 3;
  private static final int THUMBNAIL_TRANSITION_EXIT_SCALE_UP = 1;
  static final Interpolator TOUCH_RESPONSE_INTERPOLATOR = new PathInterpolator(0.3F, 0.0F, 0.1F, 1.0F);
  public static final int TRANSIT_ACTIVITY_CLOSE = 7;
  public static final int TRANSIT_ACTIVITY_OPEN = 6;
  public static final int TRANSIT_ACTIVITY_RELAUNCH = 18;
  public static final int TRANSIT_DOCK_TASK_FROM_RECENTS = 19;
  public static final int TRANSIT_NONE = 0;
  public static final int TRANSIT_TASK_CLOSE = 9;
  public static final int TRANSIT_TASK_IN_PLACE = 17;
  public static final int TRANSIT_TASK_OPEN = 8;
  public static final int TRANSIT_TASK_OPEN_BEHIND = 16;
  public static final int TRANSIT_TASK_TO_BACK = 11;
  public static final int TRANSIT_TASK_TO_FRONT = 10;
  public static final int TRANSIT_UNSET = -1;
  public static final int TRANSIT_WALLPAPER_CLOSE = 12;
  public static final int TRANSIT_WALLPAPER_INTRA_CLOSE = 15;
  public static final int TRANSIT_WALLPAPER_INTRA_OPEN = 14;
  public static final int TRANSIT_WALLPAPER_OPEN = 13;
  private IRemoteCallback mAnimationFinishedCallback;
  private int mAppTransitionState = 0;
  private final Interpolator mClipHorizontalInterpolator = new PathInterpolator(0.0F, 0.0F, 0.4F, 1.0F);
  private final int mClipRevealTranslationY;
  private final int mConfigShortAnimTime;
  private final Context mContext;
  private int mCurrentUserId = 0;
  private final Interpolator mDecelerateInterpolator;
  private final ExecutorService mDefaultExecutor = Executors.newSingleThreadExecutor();
  private AppTransitionAnimationSpec mDefaultNextAppTransitionAnimationSpec;
  private final Interpolator mFastOutLinearInInterpolator;
  private final Interpolator mFastOutSlowInInterpolator;
  private int mLastClipRevealMaxTranslation;
  private long mLastClipRevealTransitionDuration = 200L;
  private String mLastClosingApp;
  private boolean mLastHadClipReveal;
  private String mLastOpeningApp;
  private int mLastUsedAppTransition = -1;
  private final Interpolator mLinearOutSlowInInterpolator;
  private final ArrayList<WindowManagerInternal.AppTransitionListener> mListeners = new ArrayList();
  private int mNextAppTransition = -1;
  private final SparseArray<AppTransitionAnimationSpec> mNextAppTransitionAnimationsSpecs = new SparseArray();
  private IAppTransitionAnimationSpecsFuture mNextAppTransitionAnimationsSpecsFuture;
  private boolean mNextAppTransitionAnimationsSpecsPending;
  private IRemoteCallback mNextAppTransitionCallback;
  private int mNextAppTransitionEnter;
  private int mNextAppTransitionExit;
  private IRemoteCallback mNextAppTransitionFutureCallback;
  private int mNextAppTransitionInPlace;
  private Rect mNextAppTransitionInsets = new Rect();
  private String mNextAppTransitionPackage;
  private boolean mNextAppTransitionScaleUp;
  private int mNextAppTransitionType = 0;
  private boolean mProlongedAnimationsEnded;
  private final WindowManagerService mService;
  private final Interpolator mThumbnailFadeInInterpolator;
  private final Interpolator mThumbnailFadeOutInterpolator;
  private Rect mTmpFromClipRect = new Rect();
  private final Rect mTmpRect = new Rect();
  private Rect mTmpToClipRect = new Rect();
  
  AppTransition(Context paramContext, WindowManagerService paramWindowManagerService)
  {
    this.mContext = paramContext;
    this.mService = paramWindowManagerService;
    this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(paramContext, 17563662);
    this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(paramContext, 17563663);
    this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(paramContext, 17563661);
    this.mConfigShortAnimTime = paramContext.getResources().getInteger(17694720);
    this.mDecelerateInterpolator = AnimationUtils.loadInterpolator(paramContext, 17563651);
    this.mThumbnailFadeInInterpolator = new Interpolator()
    {
      public float getInterpolation(float paramAnonymousFloat)
      {
        if (paramAnonymousFloat < 0.5F) {
          return 0.0F;
        }
        paramAnonymousFloat = (paramAnonymousFloat - 0.5F) / 0.5F;
        return AppTransition.-get1(AppTransition.this).getInterpolation(paramAnonymousFloat);
      }
    };
    this.mThumbnailFadeOutInterpolator = new Interpolator()
    {
      public float getInterpolation(float paramAnonymousFloat)
      {
        if (paramAnonymousFloat < 0.5F)
        {
          paramAnonymousFloat /= 0.5F;
          return AppTransition.-get2(AppTransition.this).getInterpolation(paramAnonymousFloat);
        }
        return 1.0F;
      }
    };
    this.mClipRevealTranslationY = ((int)(this.mContext.getResources().getDisplayMetrics().density * 8.0F));
  }
  
  private String appStateToString()
  {
    switch (this.mAppTransitionState)
    {
    default: 
      return "unknown state=" + this.mAppTransitionState;
    case 0: 
      return "APP_STATE_IDLE";
    case 1: 
      return "APP_STATE_READY";
    case 2: 
      return "APP_STATE_RUNNING";
    }
    return "APP_STATE_TIMEOUT";
  }
  
  public static String appTransitionToString(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 17: 
    default: 
      return "<UNKNOWN>";
    case -1: 
      return "TRANSIT_UNSET";
    case 0: 
      return "TRANSIT_NONE";
    case 6: 
      return "TRANSIT_ACTIVITY_OPEN";
    case 7: 
      return "TRANSIT_ACTIVITY_CLOSE";
    case 8: 
      return "TRANSIT_TASK_OPEN";
    case 9: 
      return "TRANSIT_TASK_CLOSE";
    case 10: 
      return "TRANSIT_TASK_TO_FRONT";
    case 11: 
      return "TRANSIT_TASK_TO_BACK";
    case 12: 
      return "TRANSIT_WALLPAPER_CLOSE";
    case 13: 
      return "TRANSIT_WALLPAPER_OPEN";
    case 14: 
      return "TRANSIT_WALLPAPER_INTRA_OPEN";
    case 15: 
      return "TRANSIT_WALLPAPER_INTRA_CLOSE";
    case 16: 
      return "TRANSIT_TASK_OPEN_BEHIND";
    case 18: 
      return "TRANSIT_ACTIVITY_RELAUNCH";
    }
    return "TRANSIT_DOCK_TASK_FROM_RECENTS";
  }
  
  private long calculateClipRevealTransitionDuration(boolean paramBoolean, float paramFloat1, float paramFloat2, Rect paramRect)
  {
    if (!paramBoolean) {
      return 200L;
    }
    return (220.0F * Math.max(Math.abs(paramFloat1) / paramRect.width(), Math.abs(paramFloat2) / paramRect.height()) + 200.0F);
  }
  
  private static float computePivot(int paramInt, float paramFloat)
  {
    paramFloat -= 1.0F;
    if (Math.abs(paramFloat) < 1.0E-4F) {
      return paramInt;
    }
    return -paramInt / paramFloat;
  }
  
  private Animation createAspectScaledThumbnailEnterFreeformAnimationLocked(Rect paramRect1, Rect paramRect2, int paramInt)
  {
    getNextAppTransitionStartRect(paramInt, this.mTmpRect);
    return createAspectScaledThumbnailFreeformAnimationLocked(this.mTmpRect, paramRect1, paramRect2, true);
  }
  
  private Animation createAspectScaledThumbnailExitFreeformAnimationLocked(Rect paramRect1, Rect paramRect2, int paramInt)
  {
    getNextAppTransitionStartRect(paramInt, this.mTmpRect);
    return createAspectScaledThumbnailFreeformAnimationLocked(paramRect1, this.mTmpRect, paramRect2, false);
  }
  
  private AnimationSet createAspectScaledThumbnailFreeformAnimationLocked(final Rect paramRect1, Rect paramRect2, Rect paramRect3, boolean paramBoolean)
  {
    float f3 = paramRect1.width();
    float f4 = paramRect1.height();
    float f6 = paramRect2.width();
    float f5 = paramRect2.height();
    float f1;
    float f2;
    label52:
    AnimationSet localAnimationSet;
    int i;
    label69:
    int j;
    label76:
    label85:
    label104:
    label137:
    int m;
    int k;
    if (paramBoolean)
    {
      f1 = f3 / f6;
      if (!paramBoolean) {
        break label282;
      }
      f2 = f4 / f5;
      localAnimationSet = new AnimationSet(true);
      if (paramRect3 != null) {
        break label292;
      }
      i = 0;
      if (paramRect3 != null) {
        break label306;
      }
      j = 0;
      if (!paramBoolean) {
        break label320;
      }
      f3 = f6;
      f6 = (i + f3) / 2.0F;
      if (!paramBoolean) {
        break label323;
      }
      f3 = f5;
      f3 = (j + f3) / 2.0F;
      if (!paramBoolean) {
        break label330;
      }
      paramRect3 = new ScaleAnimation(f1, 1.0F, f2, 1.0F, f6, f3);
      i = paramRect1.left + paramRect1.width() / 2;
      j = paramRect1.top + paramRect1.height() / 2;
      m = paramRect2.left + paramRect2.width() / 2;
      k = paramRect2.top + paramRect2.height() / 2;
      if (!paramBoolean) {
        break label351;
      }
      i -= m;
      label201:
      if (!paramBoolean) {
        break label361;
      }
      j -= k;
      label213:
      if (!paramBoolean) {
        break label371;
      }
    }
    label282:
    label292:
    label306:
    label320:
    label323:
    label330:
    label351:
    label361:
    label371:
    for (paramRect1 = new TranslateAnimation(i, 0.0F, j, 0.0F);; paramRect1 = new TranslateAnimation(0.0F, i, 0.0F, j))
    {
      localAnimationSet.addAnimation(paramRect3);
      localAnimationSet.addAnimation(paramRect1);
      paramRect1 = this.mAnimationFinishedCallback;
      if (paramRect1 != null) {
        localAnimationSet.setAnimationListener(new Animation.AnimationListener()
        {
          public void onAnimationEnd(Animation paramAnonymousAnimation)
          {
            AppTransition.-get5(AppTransition.this).mH.obtainMessage(26, paramRect1).sendToTarget();
          }
          
          public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
          
          public void onAnimationStart(Animation paramAnonymousAnimation) {}
        });
      }
      return localAnimationSet;
      f1 = f6 / f3;
      break;
      f2 = f5 / f4;
      break label52;
      i = paramRect3.left + paramRect3.right;
      break label69;
      j = paramRect3.top + paramRect3.bottom;
      break label76;
      break label85;
      f3 = f4;
      break label104;
      paramRect3 = new ScaleAnimation(1.0F, f1, 1.0F, f2, f6, f3);
      break label137;
      i = m - i;
      break label201;
      j = k - j;
      break label213;
    }
  }
  
  private Animation createClipRevealAnimationLocked(int paramInt, boolean paramBoolean, Rect paramRect1, Rect paramRect2)
  {
    long l1;
    if (paramBoolean)
    {
      int m = paramRect1.width();
      int n = paramRect1.height();
      getDefaultNextAppTransitionStartRect(this.mTmpRect);
      int k = 0;
      paramInt = 0;
      int i1 = this.mTmpRect.centerX();
      int i3 = this.mTmpRect.centerY();
      int i2 = this.mTmpRect.width() / 2;
      int i4 = this.mTmpRect.height() / 2;
      int i = i1 - i2 - paramRect1.left;
      int j = i3 - i4 - paramRect1.top;
      paramBoolean = false;
      if (paramRect1.top > i3 - i4)
      {
        k = i3 - i4 - paramRect1.top;
        j = 0;
        paramBoolean = true;
      }
      if (paramRect1.left > i1 - i2)
      {
        paramInt = i1 - i2 - paramRect1.left;
        i = 0;
        paramBoolean = true;
      }
      if (paramRect1.right < i1 + i2)
      {
        paramInt = i1 + i2 - paramRect1.right;
        i = m - this.mTmpRect.width();
        paramBoolean = true;
      }
      l1 = calculateClipRevealTransitionDuration(paramBoolean, paramInt, k, paramRect2);
      paramRect2 = new ClipRectLRAnimation(i, this.mTmpRect.width() + i, 0, m);
      paramRect2.setInterpolator(this.mClipHorizontalInterpolator);
      paramRect2.setDuration(l1);
      TranslateAnimation localTranslateAnimation = new TranslateAnimation(paramInt, 0.0F, k, 0.0F);
      AnimationSet localAnimationSet;
      if (paramBoolean)
      {
        paramRect1 = TOUCH_RESPONSE_INTERPOLATOR;
        localTranslateAnimation.setInterpolator(paramRect1);
        localTranslateAnimation.setDuration(l1);
        paramRect1 = new ClipRectTBAnimation(j, this.mTmpRect.height() + j, 0, n, 0, 0, this.mLinearOutSlowInInterpolator);
        paramRect1.setInterpolator(TOUCH_RESPONSE_INTERPOLATOR);
        paramRect1.setDuration(l1);
        long l2 = l1 / 2L;
        AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.0F, 1.0F);
        localAlphaAnimation.setDuration(l2);
        localAlphaAnimation.setInterpolator(this.mLinearOutSlowInInterpolator);
        localAnimationSet = new AnimationSet(false);
        localAnimationSet.addAnimation(paramRect2);
        localAnimationSet.addAnimation(paramRect1);
        localAnimationSet.addAnimation(localTranslateAnimation);
        localAnimationSet.addAnimation(localAlphaAnimation);
        localAnimationSet.setZAdjustment(1);
        localAnimationSet.initialize(m, n, m, n);
        this.mLastHadClipReveal = true;
        this.mLastClipRevealTransitionDuration = l1;
        if (!paramBoolean) {
          break label457;
        }
      }
      label457:
      for (paramInt = Math.max(Math.abs(k), Math.abs(paramInt));; paramInt = 0)
      {
        this.mLastClipRevealMaxTranslation = paramInt;
        return localAnimationSet;
        paramRect1 = this.mLinearOutSlowInInterpolator;
        break;
      }
    }
    switch (paramInt)
    {
    default: 
      l1 = 200L;
      if ((paramInt == 14) || (paramInt == 15))
      {
        paramRect1 = new AlphaAnimation(1.0F, 0.0F);
        paramRect1.setDetachWallpaper(true);
      }
      break;
    }
    for (;;)
    {
      paramRect1.setInterpolator(this.mDecelerateInterpolator);
      paramRect1.setDuration(l1);
      paramRect1.setFillAfter(true);
      return paramRect1;
      l1 = this.mConfigShortAnimTime;
      break;
      paramRect1 = new AlphaAnimation(1.0F, 1.0F);
    }
  }
  
  private Animation createCurvedMotion(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if ((Math.abs(paramFloat2 - paramFloat1) < 1.0F) || (this.mNextAppTransition != 19)) {
      return new TranslateAnimation(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    }
    return new CurvedTranslateAnimation(createCurvedPath(paramFloat1, paramFloat2, paramFloat3, paramFloat4));
  }
  
  private Path createCurvedPath(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    Path localPath = new Path();
    localPath.moveTo(paramFloat1, paramFloat3);
    if (paramFloat3 > paramFloat4)
    {
      localPath.cubicTo(paramFloat1, paramFloat3, paramFloat2, 0.9F * paramFloat3 + 0.1F * paramFloat4, paramFloat2, paramFloat4);
      return localPath;
    }
    localPath.cubicTo(paramFloat1, paramFloat3, paramFloat1, 0.1F * paramFloat3 + 0.9F * paramFloat4, paramFloat2, paramFloat4);
    return localPath;
  }
  
  private Animation createRelaunchAnimation(Rect paramRect1, Rect paramRect2)
  {
    getDefaultNextAppTransitionStartRect(this.mTmpFromClipRect);
    int j = this.mTmpFromClipRect.left;
    int k = this.mTmpFromClipRect.top;
    this.mTmpFromClipRect.offset(-j, -k);
    this.mTmpToClipRect.set(0, 0, paramRect1.width(), paramRect1.height());
    AnimationSet localAnimationSet = new AnimationSet(true);
    float f1 = this.mTmpFromClipRect.width();
    float f2 = this.mTmpToClipRect.width();
    float f3 = this.mTmpFromClipRect.height();
    float f4 = this.mTmpToClipRect.height() - paramRect2.top - paramRect2.bottom;
    int i = 0;
    if ((f1 <= f2) && (f3 <= f4)) {
      localAnimationSet.addAnimation(new ClipRectAnimation(this.mTmpFromClipRect, this.mTmpToClipRect));
    }
    for (;;)
    {
      localAnimationSet.addAnimation(new TranslateAnimation(j - paramRect1.left, 0.0F, k - paramRect1.top - i, 0.0F));
      localAnimationSet.setDuration(200L);
      localAnimationSet.setZAdjustment(1);
      return localAnimationSet;
      localAnimationSet.addAnimation(new ScaleAnimation(f1 / f2, 1.0F, f3 / f4, 1.0F));
      i = (int)(paramRect2.top * f3 / f4);
    }
  }
  
  private Animation createScaleUpAnimationLocked(int paramInt, boolean paramBoolean, Rect paramRect)
  {
    getDefaultNextAppTransitionStartRect(this.mTmpRect);
    int i = paramRect.width();
    int j = paramRect.height();
    if (paramBoolean)
    {
      float f1 = this.mTmpRect.width() / i;
      float f2 = this.mTmpRect.height() / j;
      ScaleAnimation localScaleAnimation = new ScaleAnimation(f1, 1.0F, f2, 1.0F, computePivot(this.mTmpRect.left, f1), computePivot(this.mTmpRect.top, f2));
      localScaleAnimation.setInterpolator(this.mDecelerateInterpolator);
      AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.0F, 1.0F);
      localAlphaAnimation.setInterpolator(this.mThumbnailFadeOutInterpolator);
      paramRect = new AnimationSet(false);
      paramRect.addAnimation(localScaleAnimation);
      paramRect.addAnimation(localAlphaAnimation);
      paramRect.setDetachWallpaper(true);
      switch (paramInt)
      {
      }
    }
    for (long l = 200L;; l = this.mConfigShortAnimTime)
    {
      paramRect.setDuration(l);
      paramRect.setFillAfter(true);
      paramRect.setInterpolator(this.mDecelerateInterpolator);
      paramRect.initialize(i, j, i, j);
      return paramRect;
      if ((paramInt == 14) || (paramInt == 15))
      {
        paramRect = new AlphaAnimation(1.0F, 0.0F);
        paramRect.setDetachWallpaper(true);
        break;
      }
      paramRect = new AlphaAnimation(1.0F, 1.0F);
      break;
    }
  }
  
  private void fetchAppTransitionSpecsFromFuture()
  {
    if (this.mNextAppTransitionAnimationsSpecsFuture != null)
    {
      this.mNextAppTransitionAnimationsSpecsPending = true;
      final IAppTransitionAnimationSpecsFuture localIAppTransitionAnimationSpecsFuture = this.mNextAppTransitionAnimationsSpecsFuture;
      this.mNextAppTransitionAnimationsSpecsFuture = null;
      this.mDefaultExecutor.execute(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aconst_null
          //   1: astore_1
          //   2: aload_0
          //   3: getfield 21	com/android/server/wm/AppTransition$4:val$future	Landroid/view/IAppTransitionAnimationSpecsFuture;
          //   6: invokeinterface 33 1 0
          //   11: astore_2
          //   12: aload_2
          //   13: astore_1
          //   14: aload_0
          //   15: getfield 19	com/android/server/wm/AppTransition$4:this$0	Lcom/android/server/wm/AppTransition;
          //   18: invokestatic 37	com/android/server/wm/AppTransition:-get5	(Lcom/android/server/wm/AppTransition;)Lcom/android/server/wm/WindowManagerService;
          //   21: getfield 43	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
          //   24: astore_2
          //   25: aload_2
          //   26: monitorenter
          //   27: aload_0
          //   28: getfield 19	com/android/server/wm/AppTransition$4:this$0	Lcom/android/server/wm/AppTransition;
          //   31: iconst_0
          //   32: invokestatic 47	com/android/server/wm/AppTransition:-set0	(Lcom/android/server/wm/AppTransition;Z)Z
          //   35: pop
          //   36: aload_0
          //   37: getfield 19	com/android/server/wm/AppTransition$4:this$0	Lcom/android/server/wm/AppTransition;
          //   40: aload_1
          //   41: aload_0
          //   42: getfield 19	com/android/server/wm/AppTransition$4:this$0	Lcom/android/server/wm/AppTransition;
          //   45: invokestatic 51	com/android/server/wm/AppTransition:-get3	(Lcom/android/server/wm/AppTransition;)Landroid/os/IRemoteCallback;
          //   48: aconst_null
          //   49: aload_0
          //   50: getfield 19	com/android/server/wm/AppTransition$4:this$0	Lcom/android/server/wm/AppTransition;
          //   53: invokestatic 55	com/android/server/wm/AppTransition:-get4	(Lcom/android/server/wm/AppTransition;)Z
          //   56: invokevirtual 59	com/android/server/wm/AppTransition:overridePendingAppTransitionMultiThumb	([Landroid/view/AppTransitionAnimationSpec;Landroid/os/IRemoteCallback;Landroid/os/IRemoteCallback;Z)V
          //   59: aload_0
          //   60: getfield 19	com/android/server/wm/AppTransition$4:this$0	Lcom/android/server/wm/AppTransition;
          //   63: aconst_null
          //   64: invokestatic 63	com/android/server/wm/AppTransition:-set1	(Lcom/android/server/wm/AppTransition;Landroid/os/IRemoteCallback;)Landroid/os/IRemoteCallback;
          //   67: pop
          //   68: aload_1
          //   69: ifnull +21 -> 90
          //   72: aload_0
          //   73: getfield 19	com/android/server/wm/AppTransition$4:this$0	Lcom/android/server/wm/AppTransition;
          //   76: invokestatic 37	com/android/server/wm/AppTransition:-get5	(Lcom/android/server/wm/AppTransition;)Lcom/android/server/wm/WindowManagerService;
          //   79: aload_1
          //   80: aload_0
          //   81: getfield 19	com/android/server/wm/AppTransition$4:this$0	Lcom/android/server/wm/AppTransition;
          //   84: invokestatic 55	com/android/server/wm/AppTransition:-get4	(Lcom/android/server/wm/AppTransition;)Z
          //   87: invokevirtual 67	com/android/server/wm/WindowManagerService:prolongAnimationsFromSpecs	([Landroid/view/AppTransitionAnimationSpec;Z)V
          //   90: aload_2
          //   91: monitorexit
          //   92: aload_0
          //   93: getfield 19	com/android/server/wm/AppTransition$4:this$0	Lcom/android/server/wm/AppTransition;
          //   96: invokestatic 37	com/android/server/wm/AppTransition:-get5	(Lcom/android/server/wm/AppTransition;)Lcom/android/server/wm/WindowManagerService;
          //   99: invokevirtual 70	com/android/server/wm/WindowManagerService:requestTraversal	()V
          //   102: return
          //   103: astore_2
          //   104: invokestatic 74	com/android/server/wm/AppTransition:-get0	()Ljava/lang/String;
          //   107: new 76	java/lang/StringBuilder
          //   110: dup
          //   111: invokespecial 77	java/lang/StringBuilder:<init>	()V
          //   114: ldc 79
          //   116: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   119: aload_2
          //   120: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
          //   123: invokevirtual 89	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   126: invokestatic 95	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
          //   129: pop
          //   130: goto -116 -> 14
          //   133: astore_1
          //   134: aload_2
          //   135: monitorexit
          //   136: aload_1
          //   137: athrow
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	138	0	this	4
          //   1	79	1	localObject1	Object
          //   133	4	1	localObject2	Object
          //   103	32	2	localRemoteException	android.os.RemoteException
          // Exception table:
          //   from	to	target	type
          //   2	12	103	android/os/RemoteException
          //   27	68	133	finally
          //   72	90	133	finally
        }
      });
    }
  }
  
  private long getAspectScaleDuration()
  {
    if (this.mNextAppTransition == 19) {
      return 270L;
    }
    return 200L;
  }
  
  private Interpolator getAspectScaleInterpolator()
  {
    if (this.mNextAppTransition == 19) {
      return this.mFastOutSlowInInterpolator;
    }
    return TOUCH_RESPONSE_INTERPOLATOR;
  }
  
  private AttributeCache.Entry getCachedAnimations(WindowManager.LayoutParams paramLayoutParams)
  {
    if (WindowManagerDebugConfig.DEBUG_ANIM)
    {
      String str2 = TAG;
      StringBuilder localStringBuilder = new StringBuilder().append("Loading animations: layout params pkg=");
      if (paramLayoutParams != null)
      {
        str1 = paramLayoutParams.packageName;
        localStringBuilder = localStringBuilder.append(str1).append(" resId=0x");
        if (paramLayoutParams == null) {
          break label173;
        }
        str1 = Integer.toHexString(paramLayoutParams.windowAnimations);
        label61:
        Slog.v(str2, str1);
      }
    }
    else
    {
      if ((paramLayoutParams == null) || (paramLayoutParams.windowAnimations == 0)) {
        break label185;
      }
      if (paramLayoutParams.packageName == null) {
        break label178;
      }
    }
    label173:
    label178:
    for (String str1 = paramLayoutParams.packageName;; str1 = "android")
    {
      int i = paramLayoutParams.windowAnimations;
      if ((0xFF000000 & i) == 16777216) {
        str1 = "android";
      }
      if (WindowManagerDebugConfig.DEBUG_ANIM) {
        Slog.v(TAG, "Loading animations: picked package=" + str1);
      }
      return AttributeCache.instance().get(str1, i, R.styleable.WindowAnimation, this.mCurrentUserId);
      str1 = null;
      break;
      str1 = null;
      break label61;
    }
    label185:
    return null;
  }
  
  private AttributeCache.Entry getCachedAnimations(String paramString, int paramInt)
  {
    if (WindowManagerDebugConfig.DEBUG_ANIM) {
      Slog.v(TAG, "Loading animations: package=" + paramString + " resId=0x" + Integer.toHexString(paramInt));
    }
    if (paramString != null)
    {
      if ((0xFF000000 & paramInt) == 16777216) {
        paramString = "android";
      }
      if (WindowManagerDebugConfig.DEBUG_ANIM) {
        Slog.v(TAG, "Loading animations: picked package=" + paramString);
      }
      return AttributeCache.instance().get(paramString, paramInt, R.styleable.WindowAnimation, this.mCurrentUserId);
    }
    return null;
  }
  
  private void getDefaultNextAppTransitionStartRect(Rect paramRect)
  {
    if ((this.mDefaultNextAppTransitionAnimationSpec == null) || (this.mDefaultNextAppTransitionAnimationSpec.rect == null))
    {
      Slog.wtf(TAG, "Starting rect for app requested, but none available", new Throwable());
      paramRect.setEmpty();
      return;
    }
    paramRect.set(this.mDefaultNextAppTransitionAnimationSpec.rect);
  }
  
  private boolean isTvUiMode(int paramInt)
  {
    boolean bool = false;
    if ((paramInt & 0x4) > 0) {
      bool = true;
    }
    return bool;
  }
  
  private Animation loadAnimationRes(String paramString, int paramInt)
  {
    int j = 0;
    Context localContext2 = this.mContext;
    int i = j;
    Context localContext1 = localContext2;
    if (paramInt >= 0)
    {
      paramString = getCachedAnimations(paramString, paramInt);
      i = j;
      localContext1 = localContext2;
      if (paramString != null)
      {
        localContext1 = paramString.context;
        i = paramInt;
      }
    }
    if (i != 0) {
      return AnimationUtils.loadAnimation(localContext1, i);
    }
    return null;
  }
  
  private void notifyAppTransitionCancelledLocked()
  {
    int i = 0;
    while (i < this.mListeners.size())
    {
      ((WindowManagerInternal.AppTransitionListener)this.mListeners.get(i)).onAppTransitionCancelledLocked();
      i += 1;
    }
  }
  
  private void notifyAppTransitionPendingLocked()
  {
    int i = 0;
    while (i < this.mListeners.size())
    {
      ((WindowManagerInternal.AppTransitionListener)this.mListeners.get(i)).onAppTransitionPendingLocked();
      i += 1;
    }
  }
  
  private void notifyAppTransitionStartingLocked(IBinder paramIBinder1, IBinder paramIBinder2, Animation paramAnimation1, Animation paramAnimation2)
  {
    int i = 0;
    while (i < this.mListeners.size())
    {
      ((WindowManagerInternal.AppTransitionListener)this.mListeners.get(i)).onAppTransitionStartingLocked(paramIBinder1, paramIBinder2, paramAnimation1, paramAnimation2);
      i += 1;
    }
  }
  
  private boolean prepare()
  {
    if (!isRunning())
    {
      this.mAppTransitionState = 0;
      notifyAppTransitionPendingLocked();
      this.mLastHadClipReveal = false;
      this.mLastClipRevealMaxTranslation = 0;
      this.mLastClipRevealTransitionDuration = 200L;
      return true;
    }
    return false;
  }
  
  private void putDefaultNextAppTransitionCoordinates(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Bitmap paramBitmap)
  {
    this.mDefaultNextAppTransitionAnimationSpec = new AppTransitionAnimationSpec(-1, paramBitmap, new Rect(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4));
  }
  
  private void setAppTransition(int paramInt)
  {
    this.mNextAppTransition = paramInt;
    setLastAppTransition(-1, null, null);
  }
  
  private String transitTypeToString()
  {
    switch (this.mNextAppTransitionType)
    {
    default: 
      return "unknown type=" + this.mNextAppTransitionType;
    case 0: 
      return "NEXT_TRANSIT_TYPE_NONE";
    case 1: 
      return "NEXT_TRANSIT_TYPE_CUSTOM";
    case 7: 
      return "NEXT_TRANSIT_TYPE_CUSTOM_IN_PLACE";
    case 2: 
      return "NEXT_TRANSIT_TYPE_SCALE_UP";
    case 3: 
      return "NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_UP";
    case 4: 
      return "NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_DOWN";
    case 5: 
      return "NEXT_TRANSIT_TYPE_THUMBNAIL_ASPECT_SCALE_UP";
    }
    return "NEXT_TRANSIT_TYPE_THUMBNAIL_ASPECT_SCALE_DOWN";
  }
  
  boolean canSkipFirstFrame()
  {
    if ((this.mNextAppTransitionType != 1) && (this.mNextAppTransitionType != 7)) {
      return this.mNextAppTransitionType != 8;
    }
    return false;
  }
  
  void clear()
  {
    this.mNextAppTransitionType = 0;
    this.mNextAppTransitionPackage = null;
    this.mNextAppTransitionAnimationsSpecs.clear();
    this.mNextAppTransitionAnimationsSpecsFuture = null;
    this.mDefaultNextAppTransitionAnimationSpec = null;
    this.mAnimationFinishedCallback = null;
    this.mProlongedAnimationsEnded = false;
  }
  
  Animation createAspectScaledThumbnailEnterExitAnimationLocked(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rect paramRect1, Rect paramRect2, Rect paramRect3, boolean paramBoolean, int paramInt5)
  {
    int m = paramRect1.width();
    int n = paramRect1.height();
    getDefaultNextAppTransitionStartRect(this.mTmpRect);
    int j = this.mTmpRect.width();
    label40:
    float f1;
    int k;
    if (j > 0)
    {
      i = j;
      f1 = i;
      k = this.mTmpRect.height();
      if (k <= 0) {
        break label153;
      }
    }
    float f2;
    int i1;
    label153:
    for (int i = k;; i = 1)
    {
      f2 = i;
      i = this.mTmpRect.left - paramRect1.left - paramRect2.left;
      i1 = this.mTmpRect.top - paramRect1.top;
      switch (paramInt1)
      {
      default: 
        throw new RuntimeException("Invalid thumbnail transition state");
        i = 1;
        break label40;
      }
    }
    if (paramInt1 == 0)
    {
      paramInt1 = 1;
      if ((!paramBoolean) || (paramInt1 == 0)) {
        break label210;
      }
      paramRect1 = createAspectScaledThumbnailEnterFreeformAnimationLocked(paramRect1, paramRect3, paramInt5);
    }
    for (;;)
    {
      return prepareThumbnailAnimationWithDuration(paramRect1, m, n, getAspectScaleDuration(), getAspectScaleInterpolator());
      paramInt1 = 0;
      break;
      label210:
      if (paramBoolean)
      {
        paramRect1 = createAspectScaledThumbnailExitFreeformAnimationLocked(paramRect1, paramRect3, paramInt5);
      }
      else
      {
        paramRect3 = new AnimationSet(true);
        this.mTmpFromClipRect.set(paramRect1);
        this.mTmpToClipRect.set(paramRect1);
        this.mTmpFromClipRect.offsetTo(0, 0);
        this.mTmpToClipRect.offsetTo(0, 0);
        this.mTmpFromClipRect.inset(paramRect2);
        this.mNextAppTransitionInsets.set(paramRect2);
        if ((isTvUiMode(paramInt2)) || (paramInt3 == 1))
        {
          f1 /= (m - paramRect2.left - paramRect2.right);
          paramInt2 = (int)(f2 / f1);
          this.mTmpFromClipRect.bottom = (this.mTmpFromClipRect.top + paramInt2);
          this.mNextAppTransitionInsets.set(paramRect2);
          label367:
          float f3;
          label374:
          float f4;
          label382:
          float f5;
          label389:
          ScaleAnimation localScaleAnimation;
          if (paramInt1 != 0)
          {
            f2 = f1;
            if (paramInt1 == 0) {
              break label611;
            }
            f3 = 1.0F;
            if (paramInt1 == 0) {
              break label618;
            }
            f4 = f1;
            if (paramInt1 == 0) {
              break label624;
            }
            f5 = 1.0F;
            localScaleAnimation = new ScaleAnimation(f2, f3, f4, f5, paramRect1.width() / 2.0F, paramRect1.height() / 2.0F + paramRect2.top);
            f5 = this.mTmpRect.left - paramRect1.left;
            float f6 = paramRect1.width() / 2.0F;
            float f7 = paramRect1.width() / 2.0F;
            f2 = this.mTmpRect.top - paramRect1.top;
            f3 = paramRect1.height() / 2.0F;
            f4 = paramRect1.height() / 2.0F;
            f5 -= f6 - f7 * f1;
            f1 = f2 - (f3 - f4 * f1);
            if (paramInt1 == 0) {
              break label631;
            }
            paramRect1 = new ClipRectAnimation(this.mTmpFromClipRect, this.mTmpToClipRect);
            label548:
            if (paramInt1 == 0) {
              break label651;
            }
          }
          label611:
          label618:
          label624:
          label631:
          label651:
          for (paramRect2 = createCurvedMotion(f5, 0.0F, f1 - paramRect2.top, 0.0F);; paramRect2 = createCurvedMotion(0.0F, f5, 0.0F, f1 - paramRect2.top))
          {
            paramRect3.addAnimation(paramRect1);
            paramRect3.addAnimation(localScaleAnimation);
            paramRect3.addAnimation(paramRect2);
            paramRect1 = paramRect3;
            paramRect3.setZAdjustment(1);
            break;
            f2 = 1.0F;
            break label367;
            f3 = f1;
            break label374;
            f4 = 1.0F;
            break label382;
            f5 = f1;
            break label389;
            paramRect1 = new ClipRectAnimation(this.mTmpToClipRect, this.mTmpFromClipRect);
            break label548;
          }
        }
        this.mTmpFromClipRect.bottom = (this.mTmpFromClipRect.top + k);
        this.mTmpFromClipRect.right = (this.mTmpFromClipRect.left + j);
        if (paramInt1 != 0)
        {
          paramRect1 = new ClipRectAnimation(this.mTmpFromClipRect, this.mTmpToClipRect);
          label728:
          if (paramInt1 == 0) {
            break label789;
          }
        }
        label789:
        for (paramRect2 = createCurvedMotion(i, 0.0F, i1 - paramRect2.top, 0.0F);; paramRect2 = createCurvedMotion(0.0F, i, 0.0F, i1 - paramRect2.top))
        {
          paramRect3.addAnimation(paramRect1);
          paramRect3.addAnimation(paramRect2);
          break;
          paramRect1 = new ClipRectAnimation(this.mTmpToClipRect, this.mTmpFromClipRect);
          break label728;
        }
        if (paramInt4 == 14)
        {
          paramRect1 = new AlphaAnimation(1.0F, 0.0F);
        }
        else
        {
          paramRect1 = new AlphaAnimation(1.0F, 1.0F);
          continue;
          if (paramInt4 == 14) {
            paramRect1 = new AlphaAnimation(0.0F, 1.0F);
          } else {
            paramRect1 = new AlphaAnimation(1.0F, 1.0F);
          }
        }
      }
    }
  }
  
  Animation createThumbnailAspectScaleAnimationLocked(Rect paramRect1, Rect paramRect2, Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3)
  {
    int j = paramBitmap.getWidth();
    int i;
    float f1;
    int k;
    float f7;
    float f3;
    float f4;
    float f5;
    float f6;
    float f2;
    label154:
    long l2;
    Interpolator localInterpolator;
    Object localObject1;
    Object localObject2;
    label230:
    long l1;
    if (j > 0)
    {
      i = j;
      f1 = i;
      i = paramBitmap.getHeight();
      k = paramRect1.width();
      f7 = k / f1;
      getNextAppTransitionStartRect(paramInt1, this.mTmpRect);
      if ((!isTvUiMode(paramInt2)) && (paramInt3 != 1)) {
        break label487;
      }
      f3 = this.mTmpRect.left;
      f4 = this.mTmpRect.top;
      f5 = this.mTmpRect.width() / 2 * (f7 - 1.0F) + paramRect1.left;
      f6 = paramRect1.height() / 2 * (1.0F - 1.0F / f7) + paramRect1.top;
      f1 = this.mTmpRect.width() / 2;
      f2 = paramRect1.height() / 2 / f7;
      l2 = getAspectScaleDuration();
      localInterpolator = getAspectScaleInterpolator();
      if (!this.mNextAppTransitionScaleUp) {
        break label545;
      }
      localObject1 = new ScaleAnimation(1.0F, f7, 1.0F, f7, f1, f2);
      ((Animation)localObject1).setInterpolator(localInterpolator);
      ((Animation)localObject1).setDuration(l2);
      localObject2 = new AlphaAnimation(1.0F, 0.0F);
      if (this.mNextAppTransition != 19) {
        break label530;
      }
      paramBitmap = THUMBNAIL_DOCK_INTERPOLATOR;
      ((Animation)localObject2).setInterpolator(paramBitmap);
      if (this.mNextAppTransition != 19) {
        break label538;
      }
      l1 = l2 / 2L;
      label253:
      ((Animation)localObject2).setDuration(l1);
      paramBitmap = createCurvedMotion(f3, f5, f4, f6);
      paramBitmap.setInterpolator(localInterpolator);
      paramBitmap.setDuration(l2);
      this.mTmpFromClipRect.set(0, 0, j, i);
      this.mTmpToClipRect.set(paramRect1);
      this.mTmpToClipRect.offsetTo(0, 0);
      this.mTmpToClipRect.right = ((int)(this.mTmpToClipRect.right / f7));
      this.mTmpToClipRect.bottom = ((int)(this.mTmpToClipRect.bottom / f7));
      if (paramRect2 != null) {
        this.mTmpToClipRect.inset((int)(-paramRect2.left * f7), (int)(-paramRect2.top * f7), (int)(-paramRect2.right * f7), (int)(-paramRect2.bottom * f7));
      }
      ClipRectAnimation localClipRectAnimation = new ClipRectAnimation(this.mTmpFromClipRect, this.mTmpToClipRect);
      localClipRectAnimation.setInterpolator(localInterpolator);
      localClipRectAnimation.setDuration(l2);
      paramRect2 = new AnimationSet(false);
      paramRect2.addAnimation((Animation)localObject1);
      paramRect2.addAnimation((Animation)localObject2);
      paramRect2.addAnimation(paramBitmap);
      paramRect2.addAnimation(localClipRectAnimation);
    }
    for (;;)
    {
      return prepareThumbnailAnimationWithDuration(paramRect2, k, paramRect1.height(), 0L, null);
      i = 1;
      break;
      label487:
      f1 = 0.0F;
      f2 = 0.0F;
      f3 = this.mTmpRect.left;
      f4 = this.mTmpRect.top;
      f5 = paramRect1.left;
      f6 = paramRect1.top;
      break label154;
      label530:
      paramBitmap = this.mThumbnailFadeOutInterpolator;
      break label230;
      label538:
      l1 = l2;
      break label253;
      label545:
      paramBitmap = new ScaleAnimation(f7, 1.0F, f7, 1.0F, f1, f2);
      paramBitmap.setInterpolator(localInterpolator);
      paramBitmap.setDuration(l2);
      localObject1 = new AlphaAnimation(0.0F, 1.0F);
      ((Animation)localObject1).setInterpolator(this.mThumbnailFadeInInterpolator);
      ((Animation)localObject1).setDuration(l2);
      localObject2 = createCurvedMotion(f5, f3, f6, f4);
      ((Animation)localObject2).setInterpolator(localInterpolator);
      ((Animation)localObject2).setDuration(l2);
      paramRect2 = new AnimationSet(false);
      paramRect2.addAnimation(paramBitmap);
      paramRect2.addAnimation((Animation)localObject1);
      paramRect2.addAnimation((Animation)localObject2);
    }
  }
  
  Animation createThumbnailEnterExitAnimationLocked(int paramInt1, Rect paramRect, int paramInt2, int paramInt3)
  {
    int i = paramRect.width();
    int j = paramRect.height();
    paramRect = getAppTransitionThumbnailHeader(paramInt3);
    getDefaultNextAppTransitionStartRect(this.mTmpRect);
    label37:
    label42:
    float f2;
    if (paramRect != null)
    {
      paramInt3 = paramRect.getWidth();
      if (paramInt3 <= 0) {
        break label118;
      }
      f2 = paramInt3;
      if (paramRect == null) {
        break label124;
      }
      paramInt3 = paramRect.getHeight();
      label57:
      if (paramInt3 <= 0) {
        break label131;
      }
    }
    float f1;
    for (;;)
    {
      f1 = paramInt3;
      switch (paramInt1)
      {
      default: 
        throw new RuntimeException("Invalid thumbnail transition state");
        paramInt3 = i;
        break label37;
        label118:
        paramInt3 = 1;
        break label42;
        label124:
        paramInt3 = j;
        break label57;
        label131:
        paramInt3 = 1;
      }
    }
    f2 /= i;
    f1 /= j;
    paramRect = new ScaleAnimation(f2, 1.0F, f1, 1.0F, computePivot(this.mTmpRect.left, f2), computePivot(this.mTmpRect.top, f1));
    for (;;)
    {
      return prepareThumbnailAnimation(paramRect, i, j, paramInt2);
      if (paramInt2 == 14)
      {
        paramRect = new AlphaAnimation(1.0F, 0.0F);
      }
      else
      {
        paramRect = new AlphaAnimation(1.0F, 1.0F);
        continue;
        paramRect = new AlphaAnimation(1.0F, 1.0F);
        continue;
        f2 /= i;
        f1 /= j;
        ScaleAnimation localScaleAnimation = new ScaleAnimation(1.0F, f2, 1.0F, f1, computePivot(this.mTmpRect.left, f2), computePivot(this.mTmpRect.top, f1));
        AlphaAnimation localAlphaAnimation = new AlphaAnimation(1.0F, 0.0F);
        paramRect = new AnimationSet(true);
        paramRect.addAnimation(localScaleAnimation);
        paramRect.addAnimation(localAlphaAnimation);
        paramRect.setZAdjustment(1);
      }
    }
  }
  
  Animation createThumbnailScaleAnimationLocked(int paramInt1, int paramInt2, int paramInt3, Bitmap paramBitmap)
  {
    getDefaultNextAppTransitionStartRect(this.mTmpRect);
    int i = paramBitmap.getWidth();
    float f2;
    label37:
    float f1;
    if (i > 0)
    {
      f2 = i;
      i = paramBitmap.getHeight();
      if (i <= 0) {
        break label175;
      }
      f1 = i;
      if (!this.mNextAppTransitionScaleUp) {
        break label181;
      }
      f2 = paramInt1 / f2;
      f1 = paramInt2 / f1;
      ScaleAnimation localScaleAnimation = new ScaleAnimation(1.0F, f2, 1.0F, f1, computePivot(this.mTmpRect.left, 1.0F / f2), computePivot(this.mTmpRect.top, 1.0F / f1));
      localScaleAnimation.setInterpolator(this.mDecelerateInterpolator);
      AlphaAnimation localAlphaAnimation = new AlphaAnimation(1.0F, 0.0F);
      localAlphaAnimation.setInterpolator(this.mThumbnailFadeOutInterpolator);
      paramBitmap = new AnimationSet(false);
      paramBitmap.addAnimation(localScaleAnimation);
      paramBitmap.addAnimation(localAlphaAnimation);
    }
    for (;;)
    {
      return prepareThumbnailAnimation(paramBitmap, paramInt1, paramInt2, paramInt3);
      i = 1;
      break;
      label175:
      i = 1;
      break label37;
      label181:
      f2 = paramInt1 / f2;
      f1 = paramInt2 / f1;
      paramBitmap = new ScaleAnimation(f2, 1.0F, f1, 1.0F, computePivot(this.mTmpRect.left, 1.0F / f2), computePivot(this.mTmpRect.top, 1.0F / f1));
    }
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.println(this);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mAppTransitionState=");
    paramPrintWriter.println(appStateToString());
    if (this.mNextAppTransitionType != 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAppTransitionType=");
      paramPrintWriter.println(transitTypeToString());
    }
    switch (this.mNextAppTransitionType)
    {
    }
    for (;;)
    {
      if (this.mNextAppTransitionCallback != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mNextAppTransitionCallback=");
        paramPrintWriter.println(this.mNextAppTransitionCallback);
      }
      if (this.mLastUsedAppTransition != 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mLastUsedAppTransition=");
        paramPrintWriter.println(appTransitionToString(this.mLastUsedAppTransition));
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mLastOpeningApp=");
        paramPrintWriter.println(this.mLastOpeningApp);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mLastClosingApp=");
        paramPrintWriter.println(this.mLastClosingApp);
      }
      return;
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAppTransitionPackage=");
      paramPrintWriter.println(this.mNextAppTransitionPackage);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAppTransitionEnter=0x");
      paramPrintWriter.print(Integer.toHexString(this.mNextAppTransitionEnter));
      paramPrintWriter.print(" mNextAppTransitionExit=0x");
      paramPrintWriter.println(Integer.toHexString(this.mNextAppTransitionExit));
      continue;
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAppTransitionPackage=");
      paramPrintWriter.println(this.mNextAppTransitionPackage);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAppTransitionInPlace=0x");
      paramPrintWriter.print(Integer.toHexString(this.mNextAppTransitionInPlace));
      continue;
      getDefaultNextAppTransitionStartRect(this.mTmpRect);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAppTransitionStartX=");
      paramPrintWriter.print(this.mTmpRect.left);
      paramPrintWriter.print(" mNextAppTransitionStartY=");
      paramPrintWriter.println(this.mTmpRect.top);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAppTransitionStartWidth=");
      paramPrintWriter.print(this.mTmpRect.width());
      paramPrintWriter.print(" mNextAppTransitionStartHeight=");
      paramPrintWriter.println(this.mTmpRect.height());
      continue;
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mDefaultNextAppTransitionAnimationSpec=");
      paramPrintWriter.println(this.mDefaultNextAppTransitionAnimationSpec);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAppTransitionAnimationsSpecs=");
      paramPrintWriter.println(this.mNextAppTransitionAnimationsSpecs);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAppTransitionScaleUp=");
      paramPrintWriter.println(this.mNextAppTransitionScaleUp);
    }
  }
  
  void freeze()
  {
    setAppTransition(-1);
    clear();
    setReady();
    notifyAppTransitionCancelledLocked();
  }
  
  int getAppStackClipMode()
  {
    if ((this.mNextAppTransition == 18) || (this.mNextAppTransition == 19)) {}
    while (this.mNextAppTransitionType == 8) {
      return 2;
    }
    return 0;
  }
  
  int getAppTransition()
  {
    return this.mNextAppTransition;
  }
  
  Bitmap getAppTransitionThumbnailHeader(int paramInt)
  {
    Bitmap localBitmap = null;
    AppTransitionAnimationSpec localAppTransitionAnimationSpec2 = (AppTransitionAnimationSpec)this.mNextAppTransitionAnimationsSpecs.get(paramInt);
    AppTransitionAnimationSpec localAppTransitionAnimationSpec1 = localAppTransitionAnimationSpec2;
    if (localAppTransitionAnimationSpec2 == null) {
      localAppTransitionAnimationSpec1 = this.mDefaultNextAppTransitionAnimationSpec;
    }
    if (localAppTransitionAnimationSpec1 != null) {
      localBitmap = localAppTransitionAnimationSpec1.bitmap;
    }
    return localBitmap;
  }
  
  int getLastClipRevealMaxTranslation()
  {
    return this.mLastClipRevealMaxTranslation;
  }
  
  long getLastClipRevealTransitionDuration()
  {
    return this.mLastClipRevealTransitionDuration;
  }
  
  void getNextAppTransitionStartRect(int paramInt, Rect paramRect)
  {
    AppTransitionAnimationSpec localAppTransitionAnimationSpec2 = (AppTransitionAnimationSpec)this.mNextAppTransitionAnimationsSpecs.get(paramInt);
    AppTransitionAnimationSpec localAppTransitionAnimationSpec1 = localAppTransitionAnimationSpec2;
    if (localAppTransitionAnimationSpec2 == null) {
      localAppTransitionAnimationSpec1 = this.mDefaultNextAppTransitionAnimationSpec;
    }
    if ((localAppTransitionAnimationSpec1 == null) || (localAppTransitionAnimationSpec1.rect == null))
    {
      Slog.wtf(TAG, "Starting rect for task: " + paramInt + " requested, but not available", new Throwable());
      paramRect.setEmpty();
      return;
    }
    paramRect.set(localAppTransitionAnimationSpec1.rect);
  }
  
  int getThumbnailTransitionState(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (this.mNextAppTransitionScaleUp) {
        return 0;
      }
      return 2;
    }
    if (this.mNextAppTransitionScaleUp) {
      return 1;
    }
    return 3;
  }
  
  void goodToGo(AppWindowAnimator paramAppWindowAnimator1, AppWindowAnimator paramAppWindowAnimator2, ArraySet<AppWindowToken> paramArraySet1, ArraySet<AppWindowToken> paramArraySet2)
  {
    Animation localAnimation = null;
    int i = this.mNextAppTransition;
    this.mNextAppTransition = -1;
    this.mAppTransitionState = 2;
    IBinder localIBinder;
    if (paramAppWindowAnimator1 != null)
    {
      paramArraySet2 = paramAppWindowAnimator1.mAppToken.token;
      if (paramAppWindowAnimator2 == null) {
        break label114;
      }
      localIBinder = paramAppWindowAnimator2.mAppToken.token;
      label45:
      if (paramAppWindowAnimator1 == null) {
        break label120;
      }
      paramAppWindowAnimator1 = paramAppWindowAnimator1.animation;
      label54:
      if (paramAppWindowAnimator2 != null) {
        localAnimation = paramAppWindowAnimator2.animation;
      }
      notifyAppTransitionStartingLocked(paramArraySet2, localIBinder, paramAppWindowAnimator1, localAnimation);
      this.mService.getDefaultDisplayContentLocked().getDockedDividerController().notifyAppTransitionStarting(paramArraySet1, i);
      if ((this.mNextAppTransition == 19) && (!this.mProlongedAnimationsEnded)) {
        break label125;
      }
    }
    for (;;)
    {
      return;
      paramArraySet2 = null;
      break;
      label114:
      localIBinder = null;
      break label45;
      label120:
      paramAppWindowAnimator1 = null;
      break label54;
      label125:
      i = paramArraySet1.size() - 1;
      while (i >= 0)
      {
        ((AppWindowToken)paramArraySet1.valueAt(i)).mAppAnimator.startProlongAnimation(2);
        i -= 1;
      }
    }
  }
  
  boolean hadClipRevealAnimation()
  {
    return this.mLastHadClipReveal;
  }
  
  boolean isFetchingAppTransitionsSpecs()
  {
    return this.mNextAppTransitionAnimationsSpecsPending;
  }
  
  boolean isNextAppTransitionThumbnailDown()
  {
    return (this.mNextAppTransitionType == 4) || (this.mNextAppTransitionType == 6);
  }
  
  boolean isNextAppTransitionThumbnailUp()
  {
    return (this.mNextAppTransitionType == 3) || (this.mNextAppTransitionType == 5);
  }
  
  boolean isNextThumbnailTransitionAspectScaled()
  {
    return (this.mNextAppTransitionType == 5) || (this.mNextAppTransitionType == 6);
  }
  
  boolean isNextThumbnailTransitionScaleUp()
  {
    return this.mNextAppTransitionScaleUp;
  }
  
  boolean isReady()
  {
    return (this.mAppTransitionState == 1) || (this.mAppTransitionState == 3);
  }
  
  boolean isRunning()
  {
    return this.mAppTransitionState == 2;
  }
  
  boolean isTimeout()
  {
    return this.mAppTransitionState == 3;
  }
  
  boolean isTransitionEqual(int paramInt)
  {
    return this.mNextAppTransition == paramInt;
  }
  
  boolean isTransitionSet()
  {
    return this.mNextAppTransition != -1;
  }
  
  Animation loadAnimation(WindowManager.LayoutParams paramLayoutParams, int paramInt1, boolean paramBoolean1, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, boolean paramBoolean2, boolean paramBoolean3, int paramInt4)
  {
    if (paramBoolean2) {
      if ((paramInt1 == 6) || (paramInt1 == 8))
      {
        if (!paramBoolean1) {
          break label242;
        }
        paramInt2 = 17432737;
        paramRect1 = loadAnimationRes(paramLayoutParams, paramInt2);
        if (!WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS)
        {
          paramLayoutParams = paramRect1;
          if (!WindowManagerDebugConfig.DEBUG_ANIM) {}
        }
        else
        {
          Slog.v(TAG, "applyAnimation voice: anim=" + paramRect1 + " transit=" + appTransitionToString(paramInt1) + " isEntrance=" + paramBoolean1 + " Callers=" + Debug.getCallers(3));
          paramLayoutParams = paramRect1;
        }
      }
    }
    label117:
    label142:
    label242:
    label357:
    label365:
    label532:
    label595:
    label631:
    label694:
    label727:
    label800:
    label864:
    label952:
    label965:
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              return paramLayoutParams;
              if (paramInt1 == 10) {
                break;
              }
              if (paramBoolean2) {
                if ((paramInt1 == 7) || (paramInt1 == 9)) {
                  if (!paramBoolean1) {
                    break label357;
                  }
                }
              }
              for (paramInt2 = 17432735;; paramInt2 = 17432736)
              {
                paramRect1 = loadAnimationRes(paramLayoutParams, paramInt2);
                if (!WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS)
                {
                  paramLayoutParams = paramRect1;
                  if (!WindowManagerDebugConfig.DEBUG_ANIM) {
                    break label117;
                  }
                }
                Slog.v(TAG, "applyAnimation voice: anim=" + paramRect1 + " transit=" + appTransitionToString(paramInt1) + " isEntrance=" + paramBoolean1 + " Callers=" + Debug.getCallers(3));
                return paramRect1;
                paramInt2 = 17432738;
                break;
                if (paramInt1 == 11) {
                  break label142;
                }
                if (paramInt1 != 18) {
                  break label365;
                }
                paramRect1 = createRelaunchAnimation(paramRect1, paramRect3);
                if (!WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS)
                {
                  paramLayoutParams = paramRect1;
                  if (!WindowManagerDebugConfig.DEBUG_ANIM) {
                    break label117;
                  }
                }
                Slog.v(TAG, "applyAnimation: anim=" + paramRect1 + " nextAppTransition=" + this.mNextAppTransition + " transit=" + appTransitionToString(paramInt1) + " Callers=" + Debug.getCallers(3));
                return paramRect1;
              }
              if (this.mNextAppTransitionType == 1)
              {
                paramLayoutParams = this.mNextAppTransitionPackage;
                if (paramBoolean1) {}
                for (paramInt2 = this.mNextAppTransitionEnter;; paramInt2 = this.mNextAppTransitionExit)
                {
                  paramRect1 = loadAnimationRes(paramLayoutParams, paramInt2);
                  if (!WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS)
                  {
                    paramLayoutParams = paramRect1;
                    if (!WindowManagerDebugConfig.DEBUG_ANIM) {
                      break;
                    }
                  }
                  Slog.v(TAG, "applyAnimation: anim=" + paramRect1 + " nextAppTransition=ANIM_CUSTOM" + " transit=" + appTransitionToString(paramInt1) + " isEntrance=" + paramBoolean1 + " Callers=" + Debug.getCallers(3));
                  return paramRect1;
                }
              }
              if (this.mNextAppTransitionType != 7) {
                break label595;
              }
              paramRect1 = loadAnimationRes(this.mNextAppTransitionPackage, this.mNextAppTransitionInPlace);
              if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
                break label532;
              }
              paramLayoutParams = paramRect1;
            } while (!WindowManagerDebugConfig.DEBUG_ANIM);
            Slog.v(TAG, "applyAnimation: anim=" + paramRect1 + " nextAppTransition=ANIM_CUSTOM_IN_PLACE" + " transit=" + appTransitionToString(paramInt1) + " Callers=" + Debug.getCallers(3));
            return paramRect1;
            if (this.mNextAppTransitionType != 8) {
              break label694;
            }
            paramRect1 = createClipRevealAnimationLocked(paramInt1, paramBoolean1, paramRect1, paramRect2);
            if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
              break label631;
            }
            paramLayoutParams = paramRect1;
          } while (!WindowManagerDebugConfig.DEBUG_ANIM);
          Slog.v(TAG, "applyAnimation: anim=" + paramRect1 + " nextAppTransition=ANIM_CLIP_REVEAL" + " transit=" + appTransitionToString(paramInt1) + " Callers=" + Debug.getCallers(3));
          return paramRect1;
          if (this.mNextAppTransitionType != 2) {
            break label800;
          }
          paramRect1 = createScaleUpAnimationLocked(paramInt1, paramBoolean1, paramRect1);
          if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
            break label727;
          }
          paramLayoutParams = paramRect1;
        } while (!WindowManagerDebugConfig.DEBUG_ANIM);
        Slog.v(TAG, "applyAnimation: anim=" + paramRect1 + " nextAppTransition=ANIM_SCALE_UP" + " transit=" + appTransitionToString(paramInt1) + " isEntrance=" + paramBoolean1 + " Callers=" + Debug.getCallers(3));
        return paramRect1;
        if ((this.mNextAppTransitionType != 3) && (this.mNextAppTransitionType != 4)) {
          break label965;
        }
        if (this.mNextAppTransitionType != 3) {
          break label952;
        }
        paramBoolean2 = true;
        this.mNextAppTransitionScaleUp = paramBoolean2;
        paramRect1 = createThumbnailEnterExitAnimationLocked(getThumbnailTransitionState(paramBoolean1), paramRect1, paramInt1, paramInt4);
        if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
          break label864;
        }
        paramLayoutParams = paramRect1;
      } while (!WindowManagerDebugConfig.DEBUG_ANIM);
      if (this.mNextAppTransitionScaleUp) {}
      for (paramLayoutParams = "ANIM_THUMBNAIL_SCALE_UP";; paramLayoutParams = "ANIM_THUMBNAIL_SCALE_DOWN")
      {
        Slog.v(TAG, "applyAnimation: anim=" + paramRect1 + " nextAppTransition=" + paramLayoutParams + " transit=" + appTransitionToString(paramInt1) + " isEntrance=" + paramBoolean1 + " Callers=" + Debug.getCallers(3));
        return paramRect1;
        paramBoolean2 = false;
        break;
      }
      if ((this.mNextAppTransitionType != 5) && (this.mNextAppTransitionType != 6)) {
        break label1141;
      }
      if (this.mNextAppTransitionType != 5) {
        break label1128;
      }
      paramBoolean2 = true;
      this.mNextAppTransitionScaleUp = paramBoolean2;
      paramRect1 = createAspectScaledThumbnailEnterExitAnimationLocked(getThumbnailTransitionState(paramBoolean1), paramInt2, paramInt3, paramInt1, paramRect1, paramRect3, paramRect4, paramBoolean3, paramInt4);
      if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
        break label1040;
      }
      paramLayoutParams = paramRect1;
    } while (!WindowManagerDebugConfig.DEBUG_ANIM);
    label1040:
    if (this.mNextAppTransitionScaleUp) {}
    for (paramLayoutParams = "ANIM_THUMBNAIL_ASPECT_SCALE_UP";; paramLayoutParams = "ANIM_THUMBNAIL_ASPECT_SCALE_DOWN")
    {
      Slog.v(TAG, "applyAnimation: anim=" + paramRect1 + " nextAppTransition=" + paramLayoutParams + " transit=" + appTransitionToString(paramInt1) + " isEntrance=" + paramBoolean1 + " Callers=" + Debug.getCallers(3));
      return paramRect1;
      label1128:
      paramBoolean2 = false;
      break;
    }
    label1141:
    paramInt3 = 0;
    paramInt2 = paramInt3;
    switch (paramInt1)
    {
    default: 
      paramInt2 = paramInt3;
    case 17: 
    case 18: 
      label1224:
      if (paramInt2 == 0) {
        break;
      }
    }
    for (paramRect1 = loadAnimationAttr(paramLayoutParams, paramInt2);; paramRect1 = null)
    {
      if (!WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS)
      {
        paramLayoutParams = paramRect1;
        if (!WindowManagerDebugConfig.DEBUG_ANIM) {
          break;
        }
      }
      Slog.v(TAG, "applyAnimation: anim=" + paramRect1 + " animAttr=0x" + Integer.toHexString(paramInt2) + " transit=" + appTransitionToString(paramInt1) + " isEntrance=" + paramBoolean1 + " Callers=" + Debug.getCallers(3));
      return paramRect1;
      if (paramBoolean1)
      {
        paramInt2 = 4;
        break label1224;
      }
      paramInt2 = 5;
      break label1224;
      if (paramBoolean1)
      {
        paramInt2 = 6;
        break label1224;
      }
      paramInt2 = 7;
      break label1224;
      if (paramBoolean1)
      {
        paramInt2 = 8;
        break label1224;
      }
      paramInt2 = 9;
      break label1224;
      if (paramBoolean1)
      {
        paramInt2 = 10;
        break label1224;
      }
      paramInt2 = 11;
      break label1224;
      if (paramBoolean1)
      {
        paramInt2 = 12;
        break label1224;
      }
      paramInt2 = 13;
      break label1224;
      if (paramBoolean1)
      {
        paramInt2 = 14;
        break label1224;
      }
      paramInt2 = 15;
      break label1224;
      if (paramBoolean1)
      {
        paramInt2 = 16;
        break label1224;
      }
      paramInt2 = 17;
      break label1224;
      if (paramBoolean1)
      {
        paramInt2 = 18;
        break label1224;
      }
      paramInt2 = 19;
      break label1224;
      if (paramBoolean1)
      {
        paramInt2 = 20;
        break label1224;
      }
      paramInt2 = 21;
      break label1224;
      if (paramBoolean1)
      {
        paramInt2 = 22;
        break label1224;
      }
      paramInt2 = 23;
      break label1224;
      if (paramBoolean1)
      {
        paramInt2 = 25;
        break label1224;
      }
      paramInt2 = 24;
      break label1224;
    }
  }
  
  Animation loadAnimationAttr(WindowManager.LayoutParams paramLayoutParams, int paramInt)
  {
    int j = 0;
    Context localContext2 = this.mContext;
    int i = j;
    Context localContext1 = localContext2;
    if (paramInt >= 0)
    {
      paramLayoutParams = getCachedAnimations(paramLayoutParams);
      i = j;
      localContext1 = localContext2;
      if (paramLayoutParams != null)
      {
        localContext1 = paramLayoutParams.context;
        i = paramLayoutParams.array.getResourceId(paramInt, 0);
      }
    }
    if (i != 0) {
      return AnimationUtils.loadAnimation(localContext1, i);
    }
    return null;
  }
  
  Animation loadAnimationRes(WindowManager.LayoutParams paramLayoutParams, int paramInt)
  {
    Context localContext = this.mContext;
    if (paramInt >= 0)
    {
      AttributeCache.Entry localEntry = getCachedAnimations(paramLayoutParams);
      paramLayoutParams = localContext;
      if (localEntry != null) {
        paramLayoutParams = localEntry.context;
      }
      return AnimationUtils.loadAnimation(paramLayoutParams, paramInt);
    }
    return null;
  }
  
  public void notifyAppTransitionFinishedLocked(IBinder paramIBinder)
  {
    int i = 0;
    while (i < this.mListeners.size())
    {
      ((WindowManagerInternal.AppTransitionListener)this.mListeners.get(i)).onAppTransitionFinishedLocked(paramIBinder);
      i += 1;
    }
  }
  
  void notifyProlongedAnimationsEnded()
  {
    this.mProlongedAnimationsEnded = true;
  }
  
  void overrideInPlaceAppTransition(String paramString, int paramInt)
  {
    if (isTransitionSet())
    {
      clear();
      this.mNextAppTransitionType = 7;
      this.mNextAppTransitionPackage = paramString;
      this.mNextAppTransitionInPlace = paramInt;
      return;
    }
    postAnimationCallback();
  }
  
  void overridePendingAppTransition(String paramString, int paramInt1, int paramInt2, IRemoteCallback paramIRemoteCallback)
  {
    if (isTransitionSet())
    {
      clear();
      this.mNextAppTransitionType = 1;
      this.mNextAppTransitionPackage = paramString;
      this.mNextAppTransitionEnter = paramInt1;
      this.mNextAppTransitionExit = paramInt2;
      postAnimationCallback();
      this.mNextAppTransitionCallback = paramIRemoteCallback;
      return;
    }
    postAnimationCallback();
  }
  
  void overridePendingAppTransitionAspectScaledThumb(Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4, IRemoteCallback paramIRemoteCallback, boolean paramBoolean)
  {
    if (isTransitionSet())
    {
      clear();
      if (paramBoolean) {}
      for (int i = 5;; i = 6)
      {
        this.mNextAppTransitionType = i;
        this.mNextAppTransitionScaleUp = paramBoolean;
        putDefaultNextAppTransitionCoordinates(paramInt1, paramInt2, paramInt3, paramInt4, paramBitmap);
        postAnimationCallback();
        this.mNextAppTransitionCallback = paramIRemoteCallback;
        return;
      }
    }
    postAnimationCallback();
  }
  
  void overridePendingAppTransitionClipReveal(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (isTransitionSet())
    {
      clear();
      this.mNextAppTransitionType = 8;
      putDefaultNextAppTransitionCoordinates(paramInt1, paramInt2, paramInt3, paramInt4, null);
      postAnimationCallback();
    }
  }
  
  public void overridePendingAppTransitionMultiThumb(AppTransitionAnimationSpec[] paramArrayOfAppTransitionAnimationSpec, IRemoteCallback paramIRemoteCallback1, IRemoteCallback paramIRemoteCallback2, boolean paramBoolean)
  {
    if (isTransitionSet())
    {
      clear();
      if (paramBoolean) {}
      for (int i = 5;; i = 6)
      {
        this.mNextAppTransitionType = i;
        this.mNextAppTransitionScaleUp = paramBoolean;
        if (paramArrayOfAppTransitionAnimationSpec == null) {
          break;
        }
        i = 0;
        while (i < paramArrayOfAppTransitionAnimationSpec.length)
        {
          AppTransitionAnimationSpec localAppTransitionAnimationSpec = paramArrayOfAppTransitionAnimationSpec[i];
          if (localAppTransitionAnimationSpec != null)
          {
            this.mNextAppTransitionAnimationsSpecs.put(localAppTransitionAnimationSpec.taskId, localAppTransitionAnimationSpec);
            if (i == 0)
            {
              Rect localRect = localAppTransitionAnimationSpec.rect;
              putDefaultNextAppTransitionCoordinates(localRect.left, localRect.top, localRect.width(), localRect.height(), localAppTransitionAnimationSpec.bitmap);
            }
          }
          i += 1;
        }
      }
      postAnimationCallback();
      this.mNextAppTransitionCallback = paramIRemoteCallback1;
      this.mAnimationFinishedCallback = paramIRemoteCallback2;
      return;
    }
    postAnimationCallback();
  }
  
  void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture paramIAppTransitionAnimationSpecsFuture, IRemoteCallback paramIRemoteCallback, boolean paramBoolean)
  {
    if (isTransitionSet())
    {
      clear();
      if (!paramBoolean) {
        break label40;
      }
    }
    label40:
    for (int i = 5;; i = 6)
    {
      this.mNextAppTransitionType = i;
      this.mNextAppTransitionAnimationsSpecsFuture = paramIAppTransitionAnimationSpecsFuture;
      this.mNextAppTransitionScaleUp = paramBoolean;
      this.mNextAppTransitionFutureCallback = paramIRemoteCallback;
      return;
    }
  }
  
  void overridePendingAppTransitionScaleUp(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (isTransitionSet())
    {
      clear();
      this.mNextAppTransitionType = 2;
      putDefaultNextAppTransitionCoordinates(paramInt1, paramInt2, paramInt3, paramInt4, null);
      postAnimationCallback();
    }
  }
  
  void overridePendingAppTransitionThumb(Bitmap paramBitmap, int paramInt1, int paramInt2, IRemoteCallback paramIRemoteCallback, boolean paramBoolean)
  {
    if (isTransitionSet())
    {
      clear();
      if (paramBoolean) {}
      for (int i = 3;; i = 4)
      {
        this.mNextAppTransitionType = i;
        this.mNextAppTransitionScaleUp = paramBoolean;
        putDefaultNextAppTransitionCoordinates(paramInt1, paramInt2, 0, 0, paramBitmap);
        postAnimationCallback();
        this.mNextAppTransitionCallback = paramIRemoteCallback;
        return;
      }
    }
    postAnimationCallback();
  }
  
  void postAnimationCallback()
  {
    if (this.mNextAppTransitionCallback != null)
    {
      this.mService.mH.sendMessage(this.mService.mH.obtainMessage(26, this.mNextAppTransitionCallback));
      this.mNextAppTransitionCallback = null;
    }
  }
  
  boolean prepareAppTransitionLocked(int paramInt, boolean paramBoolean)
  {
    if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
      Slog.v(TAG, "Prepare app transition: transit=" + appTransitionToString(paramInt) + " " + this + " alwaysKeepCurrent=" + paramBoolean + " Callers=" + Debug.getCallers(3));
    }
    if ((!isTransitionSet()) || (this.mNextAppTransition == 0)) {
      setAppTransition(paramInt);
    }
    for (;;)
    {
      paramBoolean = prepare();
      if (isTransitionSet())
      {
        this.mService.mH.removeMessages(13);
        this.mService.mH.sendEmptyMessageDelayed(13, 5000L);
      }
      return paramBoolean;
      if (!paramBoolean) {
        if ((paramInt == 8) && (isTransitionEqual(9))) {
          setAppTransition(paramInt);
        } else if ((paramInt == 6) && (isTransitionEqual(7))) {
          setAppTransition(paramInt);
        }
      }
    }
  }
  
  Animation prepareThumbnailAnimation(Animation paramAnimation, int paramInt1, int paramInt2, int paramInt3)
  {
    switch (paramInt3)
    {
    }
    for (paramInt3 = 200;; paramInt3 = this.mConfigShortAnimTime) {
      return prepareThumbnailAnimationWithDuration(paramAnimation, paramInt1, paramInt2, paramInt3, this.mDecelerateInterpolator);
    }
  }
  
  Animation prepareThumbnailAnimationWithDuration(Animation paramAnimation, int paramInt1, int paramInt2, long paramLong, Interpolator paramInterpolator)
  {
    if (paramLong > 0L) {
      paramAnimation.setDuration(paramLong);
    }
    paramAnimation.setFillAfter(true);
    if (paramInterpolator != null) {
      paramAnimation.setInterpolator(paramInterpolator);
    }
    paramAnimation.initialize(paramInt1, paramInt2, paramInt1, paramInt2);
    return paramAnimation;
  }
  
  void registerListenerLocked(WindowManagerInternal.AppTransitionListener paramAppTransitionListener)
  {
    this.mListeners.add(paramAppTransitionListener);
  }
  
  public void setCurrentUser(int paramInt)
  {
    this.mCurrentUserId = paramInt;
  }
  
  void setIdle()
  {
    this.mAppTransitionState = 0;
  }
  
  void setLastAppTransition(int paramInt, AppWindowToken paramAppWindowToken1, AppWindowToken paramAppWindowToken2)
  {
    this.mLastUsedAppTransition = paramInt;
    this.mLastOpeningApp = ("" + paramAppWindowToken1);
    this.mLastClosingApp = ("" + paramAppWindowToken2);
  }
  
  void setReady()
  {
    this.mAppTransitionState = 1;
    fetchAppTransitionSpecsFromFuture();
  }
  
  void setTimeout()
  {
    this.mAppTransitionState = 3;
  }
  
  public String toString()
  {
    return "mNextAppTransition=" + appTransitionToString(this.mNextAppTransition);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/AppTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */