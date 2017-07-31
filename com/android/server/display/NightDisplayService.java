package com.android.server.display;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.AlarmManager;
import android.app.AlarmManager.OnAlarmListener;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.service.vr.IVrStateCallbacks.Stub;
import android.util.MathUtils;
import android.util.Slog;
import android.view.animation.AnimationUtils;
import com.android.internal.app.NightDisplayController;
import com.android.internal.app.NightDisplayController.Callback;
import com.android.internal.app.NightDisplayController.LocalTime;
import com.android.server.SystemService;
import com.android.server.twilight.TwilightListener;
import com.android.server.twilight.TwilightManager;
import com.android.server.twilight.TwilightState;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NightDisplayService
  extends SystemService
  implements NightDisplayController.Callback
{
  private static final ColorMatrixEvaluator COLOR_MATRIX_EVALUATOR = new ColorMatrixEvaluator(null);
  private static final boolean DEBUG = false;
  private static final float[] MATRIX_IDENTITY;
  private static final float[] MATRIX_NIGHT = { 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.754F, 0.0F, 0.0F, 0.0F, 0.0F, 0.516F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F };
  private static final String TAG = "NightDisplayService";
  private AutoMode mAutoMode;
  private boolean mBootCompleted;
  private ValueAnimator mColorMatrixAnimator;
  private NightDisplayController mController;
  private int mCurrentUser = 55536;
  private final Handler mHandler = new Handler(Looper.getMainLooper());
  private final AtomicBoolean mIgnoreAllColorMatrixChanges = new AtomicBoolean();
  private Boolean mIsActivated;
  private ContentObserver mUserSetupObserver;
  private final IVrStateCallbacks mVrStateCallbacks = new IVrStateCallbacks.Stub()
  {
    public void onVrStateChanged(final boolean paramAnonymousBoolean)
    {
      NightDisplayService.-get7(NightDisplayService.this).set(paramAnonymousBoolean);
      NightDisplayService.-get6(NightDisplayService.this).post(new Runnable()
      {
        public void run()
        {
          if (NightDisplayService.-get3(NightDisplayService.this) != null) {
            NightDisplayService.-get3(NightDisplayService.this).cancel();
          }
          DisplayTransformManager localDisplayTransformManager = (DisplayTransformManager)NightDisplayService.-wrap1(NightDisplayService.this, DisplayTransformManager.class);
          if (paramAnonymousBoolean) {
            localDisplayTransformManager.setColorMatrix(100, NightDisplayService.-get0());
          }
          while (!NightDisplayService.-get4(NightDisplayService.this).isActivated()) {
            return;
          }
          localDisplayTransformManager.setColorMatrix(100, NightDisplayService.-get1());
        }
      });
    }
  };
  
  static
  {
    MATRIX_IDENTITY = new float[16];
    Matrix.setIdentityM(MATRIX_IDENTITY, 0);
  }
  
  public NightDisplayService(Context paramContext)
  {
    super(paramContext);
  }
  
  private static boolean isUserSetupCompleted(ContentResolver paramContentResolver, int paramInt)
  {
    return Settings.Secure.getIntForUser(paramContentResolver, "user_setup_complete", 0, paramInt) == 1;
  }
  
  private void onUserChanged(int paramInt)
  {
    final ContentResolver localContentResolver = getContext().getContentResolver();
    if (this.mCurrentUser != 55536)
    {
      if (this.mUserSetupObserver == null) {
        break label100;
      }
      localContentResolver.unregisterContentObserver(this.mUserSetupObserver);
      this.mUserSetupObserver = null;
    }
    label100:
    do
    {
      for (;;)
      {
        this.mCurrentUser = paramInt;
        if (this.mCurrentUser != 55536)
        {
          if (isUserSetupCompleted(localContentResolver, this.mCurrentUser)) {
            break;
          }
          this.mUserSetupObserver = new ContentObserver(this.mHandler)
          {
            public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
            {
              if (NightDisplayService.-wrap0(localContentResolver, NightDisplayService.-get5(NightDisplayService.this)))
              {
                localContentResolver.unregisterContentObserver(this);
                NightDisplayService.-set1(NightDisplayService.this, null);
                if (NightDisplayService.-get2(NightDisplayService.this)) {
                  NightDisplayService.-wrap2(NightDisplayService.this);
                }
              }
            }
          };
          localContentResolver.registerContentObserver(Settings.Secure.getUriFor("user_setup_complete"), false, this.mUserSetupObserver, this.mCurrentUser);
        }
        return;
        if (this.mBootCompleted) {
          tearDown();
        }
      }
    } while (!this.mBootCompleted);
    setUp();
  }
  
  private void setUp()
  {
    Slog.d("NightDisplayService", "setUp: currentUser=" + this.mCurrentUser);
    this.mController = new NightDisplayController(getContext(), this.mCurrentUser);
    this.mController.setListener(this);
    onAutoModeChanged(this.mController.getAutoMode());
    if (this.mIsActivated == null) {
      onActivated(this.mController.isActivated());
    }
  }
  
  private void tearDown()
  {
    Slog.d("NightDisplayService", "tearDown: currentUser=" + this.mCurrentUser);
    if (this.mController != null)
    {
      this.mController.setListener(null);
      this.mController = null;
    }
    if (this.mAutoMode != null)
    {
      this.mAutoMode.onStop();
      this.mAutoMode = null;
    }
    if (this.mColorMatrixAnimator != null)
    {
      this.mColorMatrixAnimator.end();
      this.mColorMatrixAnimator = null;
    }
    this.mIsActivated = null;
  }
  
  public void onActivated(boolean paramBoolean)
  {
    final Object localObject1;
    final DisplayTransformManager localDisplayTransformManager;
    ColorMatrixEvaluator localColorMatrixEvaluator;
    Object localObject2;
    if ((this.mIsActivated == null) || (this.mIsActivated.booleanValue() != paramBoolean))
    {
      if (paramBoolean) {}
      for (localObject1 = "Turning on night display";; localObject1 = "Turning off night display")
      {
        Slog.i("NightDisplayService", (String)localObject1);
        if (this.mAutoMode != null) {
          this.mAutoMode.onActivated(paramBoolean);
        }
        this.mIsActivated = Boolean.valueOf(paramBoolean);
        if (this.mColorMatrixAnimator != null) {
          this.mColorMatrixAnimator.cancel();
        }
        if (!this.mIgnoreAllColorMatrixChanges.get()) {
          break;
        }
        return;
      }
      localDisplayTransformManager = (DisplayTransformManager)getLocalService(DisplayTransformManager.class);
      localObject3 = localDisplayTransformManager.getColorMatrix(100);
      if (!this.mIsActivated.booleanValue()) {
        break label253;
      }
      localObject1 = MATRIX_NIGHT;
      localColorMatrixEvaluator = COLOR_MATRIX_EVALUATOR;
      localObject2 = localObject3;
      if (localObject3 == null) {
        localObject2 = MATRIX_IDENTITY;
      }
      if (localObject1 != null) {
        break label258;
      }
    }
    label253:
    label258:
    for (Object localObject3 = MATRIX_IDENTITY;; localObject3 = localObject1)
    {
      this.mColorMatrixAnimator = ValueAnimator.ofObject(localColorMatrixEvaluator, new Object[] { localObject2, localObject3 });
      this.mColorMatrixAnimator.setDuration(getContext().getResources().getInteger(17694722));
      this.mColorMatrixAnimator.setInterpolator(AnimationUtils.loadInterpolator(getContext(), 17563661));
      this.mColorMatrixAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          paramAnonymousValueAnimator = (float[])paramAnonymousValueAnimator.getAnimatedValue();
          localDisplayTransformManager.setColorMatrix(100, paramAnonymousValueAnimator);
        }
      });
      this.mColorMatrixAnimator.addListener(new AnimatorListenerAdapter()
      {
        private boolean mIsCancelled;
        
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          this.mIsCancelled = true;
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (!this.mIsCancelled) {
            localDisplayTransformManager.setColorMatrix(100, localObject1);
          }
          NightDisplayService.-set0(NightDisplayService.this, null);
        }
      });
      this.mColorMatrixAnimator.start();
      return;
      localObject1 = null;
      break;
    }
  }
  
  public void onAutoModeChanged(int paramInt)
  {
    Slog.d("NightDisplayService", "onAutoModeChanged: autoMode=" + paramInt);
    if (this.mAutoMode != null)
    {
      this.mAutoMode.onStop();
      this.mAutoMode = null;
    }
    if (paramInt == 1) {
      this.mAutoMode = new CustomAutoMode();
    }
    for (;;)
    {
      if (this.mAutoMode != null) {
        this.mAutoMode.onStart();
      }
      return;
      if (paramInt == 2) {
        this.mAutoMode = new TwilightAutoMode();
      }
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    IVrManager localIVrManager;
    if (paramInt == 500)
    {
      localIVrManager = (IVrManager)getBinderService("vrmanager");
      if (localIVrManager == null) {}
    }
    do
    {
      do
      {
        try
        {
          localIVrManager.registerListener(this.mVrStateCallbacks);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Slog.e("NightDisplayService", "Failed to register VR mode state listener: " + localRemoteException);
          return;
        }
      } while (paramInt != 1000);
      this.mBootCompleted = true;
    } while ((this.mCurrentUser == 55536) || (this.mUserSetupObserver != null));
    setUp();
  }
  
  public void onCustomEndTimeChanged(NightDisplayController.LocalTime paramLocalTime)
  {
    Slog.d("NightDisplayService", "onCustomEndTimeChanged: endTime=" + paramLocalTime);
    if (this.mAutoMode != null) {
      this.mAutoMode.onCustomEndTimeChanged(paramLocalTime);
    }
  }
  
  public void onCustomStartTimeChanged(NightDisplayController.LocalTime paramLocalTime)
  {
    Slog.d("NightDisplayService", "onCustomStartTimeChanged: startTime=" + paramLocalTime);
    if (this.mAutoMode != null) {
      this.mAutoMode.onCustomStartTimeChanged(paramLocalTime);
    }
  }
  
  public void onStart() {}
  
  public void onStartUser(int paramInt)
  {
    super.onStartUser(paramInt);
    if (this.mCurrentUser == 55536) {
      onUserChanged(paramInt);
    }
  }
  
  public void onStopUser(int paramInt)
  {
    super.onStopUser(paramInt);
    if (this.mCurrentUser == paramInt) {
      onUserChanged(55536);
    }
  }
  
  public void onSwitchUser(int paramInt)
  {
    super.onSwitchUser(paramInt);
    onUserChanged(paramInt);
  }
  
  private abstract class AutoMode
    implements NightDisplayController.Callback
  {
    private AutoMode() {}
    
    public abstract void onStart();
    
    public abstract void onStop();
  }
  
  private static class ColorMatrixEvaluator
    implements TypeEvaluator<float[]>
  {
    private final float[] mResultMatrix = new float[16];
    
    public float[] evaluate(float paramFloat, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      int i = 0;
      while (i < this.mResultMatrix.length)
      {
        this.mResultMatrix[i] = MathUtils.lerp(paramArrayOfFloat1[i], paramArrayOfFloat2[i], paramFloat);
        i += 1;
      }
      return this.mResultMatrix;
    }
  }
  
  private class CustomAutoMode
    extends NightDisplayService.AutoMode
    implements AlarmManager.OnAlarmListener
  {
    private final AlarmManager mAlarmManager = (AlarmManager)NightDisplayService.this.getContext().getSystemService("alarm");
    private NightDisplayController.LocalTime mEndTime;
    private Calendar mLastActivatedTime;
    private NightDisplayController.LocalTime mStartTime;
    private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        NightDisplayService.CustomAutoMode.-wrap0(NightDisplayService.CustomAutoMode.this);
      }
    };
    
    public CustomAutoMode()
    {
      super(null);
    }
    
    private void updateActivated()
    {
      Calendar localCalendar = Calendar.getInstance();
      Object localObject = this.mStartTime.getDateTimeBefore(localCalendar);
      boolean bool3 = localCalendar.before(this.mEndTime.getDateTimeAfter((Calendar)localObject));
      boolean bool2;
      boolean bool1;
      if ((NightDisplayService.-get8(NightDisplayService.this) == null) || (this.mLastActivatedTime == null))
      {
        bool2 = true;
        bool1 = bool2;
        if (!bool2)
        {
          bool1 = bool2;
          if (NightDisplayService.-get8(NightDisplayService.this).booleanValue() != bool3)
          {
            localObject = localCalendar.getTimeZone();
            if (!((TimeZone)localObject).equals(this.mLastActivatedTime.getTimeZone()))
            {
              int i = this.mLastActivatedTime.get(1);
              int j = this.mLastActivatedTime.get(6);
              int k = this.mLastActivatedTime.get(11);
              int m = this.mLastActivatedTime.get(12);
              this.mLastActivatedTime.setTimeZone((TimeZone)localObject);
              this.mLastActivatedTime.set(1, i);
              this.mLastActivatedTime.set(6, j);
              this.mLastActivatedTime.set(11, k);
              this.mLastActivatedTime.set(12, m);
            }
            if (!NightDisplayService.-get8(NightDisplayService.this).booleanValue()) {
              break label285;
            }
            if (localCalendar.before(this.mStartTime.getDateTimeBefore(this.mLastActivatedTime))) {
              break label279;
            }
            bool1 = localCalendar.after(this.mEndTime.getDateTimeAfter(this.mLastActivatedTime));
          }
        }
      }
      for (;;)
      {
        if (bool1) {
          NightDisplayService.-get4(NightDisplayService.this).setActivated(bool3);
        }
        updateNextAlarm(NightDisplayService.-get8(NightDisplayService.this), localCalendar);
        return;
        bool2 = false;
        break;
        label279:
        bool1 = true;
        continue;
        label285:
        if (!localCalendar.before(this.mEndTime.getDateTimeBefore(this.mLastActivatedTime))) {
          bool1 = localCalendar.after(this.mStartTime.getDateTimeAfter(this.mLastActivatedTime));
        } else {
          bool1 = true;
        }
      }
    }
    
    private void updateNextAlarm(Boolean paramBoolean, Calendar paramCalendar)
    {
      if (paramBoolean != null) {
        if (!paramBoolean.booleanValue()) {
          break label37;
        }
      }
      label37:
      for (paramBoolean = this.mEndTime.getDateTimeAfter(paramCalendar);; paramBoolean = this.mStartTime.getDateTimeAfter(paramCalendar))
      {
        this.mAlarmManager.setExact(1, paramBoolean.getTimeInMillis(), "NightDisplayService", this, null);
        return;
      }
    }
    
    public void onActivated(boolean paramBoolean)
    {
      Calendar localCalendar = Calendar.getInstance();
      if (NightDisplayService.-get8(NightDisplayService.this) != null) {
        this.mLastActivatedTime = localCalendar;
      }
      updateNextAlarm(Boolean.valueOf(paramBoolean), localCalendar);
    }
    
    public void onAlarm()
    {
      Slog.d("NightDisplayService", "onAlarm");
      updateActivated();
    }
    
    public void onCustomEndTimeChanged(NightDisplayController.LocalTime paramLocalTime)
    {
      this.mEndTime = paramLocalTime;
      this.mLastActivatedTime = null;
      updateActivated();
    }
    
    public void onCustomStartTimeChanged(NightDisplayController.LocalTime paramLocalTime)
    {
      this.mStartTime = paramLocalTime;
      this.mLastActivatedTime = null;
      updateActivated();
    }
    
    public void onStart()
    {
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.TIME_SET");
      localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
      NightDisplayService.this.getContext().registerReceiver(this.mTimeChangedReceiver, localIntentFilter);
      this.mStartTime = NightDisplayService.-get4(NightDisplayService.this).getCustomStartTime();
      this.mEndTime = NightDisplayService.-get4(NightDisplayService.this).getCustomEndTime();
      updateActivated();
    }
    
    public void onStop()
    {
      NightDisplayService.this.getContext().unregisterReceiver(this.mTimeChangedReceiver);
      this.mAlarmManager.cancel(this);
      this.mLastActivatedTime = null;
    }
  }
  
  private class TwilightAutoMode
    extends NightDisplayService.AutoMode
    implements TwilightListener
  {
    private Calendar mLastActivatedTime;
    private final TwilightManager mTwilightManager = (TwilightManager)NightDisplayService.-wrap1(NightDisplayService.this, TwilightManager.class);
    
    public TwilightAutoMode()
    {
      super(null);
    }
    
    private void updateActivated(TwilightState paramTwilightState)
    {
      boolean bool1 = true;
      boolean bool3;
      boolean bool2;
      label41:
      Calendar localCalendar;
      if (paramTwilightState != null)
      {
        bool3 = paramTwilightState.isNight();
        bool2 = bool1;
        if (NightDisplayService.-get8(NightDisplayService.this) != null)
        {
          if (NightDisplayService.-get8(NightDisplayService.this).booleanValue() == bool3) {
            break label127;
          }
          bool2 = bool1;
        }
        bool1 = bool2;
        if (bool2)
        {
          bool1 = bool2;
          if (paramTwilightState != null)
          {
            bool1 = bool2;
            if (this.mLastActivatedTime != null)
            {
              localCalendar = paramTwilightState.sunrise();
              paramTwilightState = paramTwilightState.sunset();
              if (!localCalendar.before(paramTwilightState)) {
                break label137;
              }
              if (this.mLastActivatedTime.before(localCalendar)) {
                break label132;
              }
              bool1 = this.mLastActivatedTime.after(paramTwilightState);
            }
          }
        }
      }
      for (;;)
      {
        if (bool1) {
          NightDisplayService.-get4(NightDisplayService.this).setActivated(bool3);
        }
        return;
        bool3 = false;
        break;
        label127:
        bool2 = false;
        break label41;
        label132:
        bool1 = true;
        continue;
        label137:
        if (!this.mLastActivatedTime.before(paramTwilightState)) {
          bool1 = this.mLastActivatedTime.after(localCalendar);
        } else {
          bool1 = true;
        }
      }
    }
    
    public void onActivated(boolean paramBoolean)
    {
      if (NightDisplayService.-get8(NightDisplayService.this) != null) {
        this.mLastActivatedTime = Calendar.getInstance();
      }
    }
    
    public void onStart()
    {
      this.mTwilightManager.registerListener(this, NightDisplayService.-get6(NightDisplayService.this));
      updateActivated(this.mTwilightManager.getLastTwilightState());
    }
    
    public void onStop()
    {
      this.mTwilightManager.unregisterListener(this);
      this.mLastActivatedTime = null;
    }
    
    public void onTwilightStateChanged(TwilightState paramTwilightState)
    {
      Object localObject = null;
      StringBuilder localStringBuilder = new StringBuilder().append("onTwilightStateChanged: isNight=");
      if (paramTwilightState == null) {}
      for (;;)
      {
        Slog.d("NightDisplayService", localObject);
        updateActivated(paramTwilightState);
        return;
        localObject = Boolean.valueOf(paramTwilightState.isNight());
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/NightDisplayService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */