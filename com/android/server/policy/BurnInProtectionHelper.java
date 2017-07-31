package com.android.server.policy;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.hardware.display.DisplayManagerInternal;
import android.os.SystemClock;
import android.view.Display;
import android.view.animation.LinearInterpolator;
import com.android.server.LocalServices;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class BurnInProtectionHelper
  implements DisplayManager.DisplayListener, Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener
{
  private static final String ACTION_BURN_IN_PROTECTION = "android.internal.policy.action.BURN_IN_PROTECTION";
  private static final long BURNIN_PROTECTION_MINIMAL_INTERVAL_MS = TimeUnit.SECONDS.toMillis(10L);
  private static final long BURNIN_PROTECTION_WAKEUP_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1L);
  public static final int BURN_IN_MAX_RADIUS_DEFAULT = -1;
  private static final int BURN_IN_SHIFT_STEP = 2;
  private static final long CENTERING_ANIMATION_DURATION_MS = 100L;
  private static final boolean DEBUG = false;
  private static final String TAG = "BurnInProtection";
  private final AlarmManager mAlarmManager;
  private int mAppliedBurnInXOffset = 0;
  private int mAppliedBurnInYOffset = 0;
  private boolean mBurnInProtectionActive;
  private final PendingIntent mBurnInProtectionIntent;
  private BroadcastReceiver mBurnInProtectionReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      BurnInProtectionHelper.-wrap0(BurnInProtectionHelper.this);
    }
  };
  private final int mBurnInRadiusMaxSquared;
  private final ValueAnimator mCenteringAnimator;
  private final Display mDisplay;
  private final DisplayManagerInternal mDisplayManagerInternal;
  private boolean mFirstUpdate;
  private int mLastBurnInXOffset = 0;
  private int mLastBurnInYOffset = 0;
  private final int mMaxHorizontalBurnInOffset;
  private final int mMaxVerticalBurnInOffset;
  private final int mMinHorizontalBurnInOffset;
  private final int mMinVerticalBurnInOffset;
  private int mXOffsetDirection = 1;
  private int mYOffsetDirection = 1;
  
  public BurnInProtectionHelper(Context paramContext, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this.mMinHorizontalBurnInOffset = paramInt1;
    this.mMaxHorizontalBurnInOffset = paramInt2;
    this.mMinVerticalBurnInOffset = paramInt3;
    this.mMaxVerticalBurnInOffset = paramInt4;
    if (paramInt5 != -1) {}
    for (this.mBurnInRadiusMaxSquared = (paramInt5 * paramInt5);; this.mBurnInRadiusMaxSquared = -1)
    {
      this.mDisplayManagerInternal = ((DisplayManagerInternal)LocalServices.getService(DisplayManagerInternal.class));
      this.mAlarmManager = ((AlarmManager)paramContext.getSystemService("alarm"));
      paramContext.registerReceiver(this.mBurnInProtectionReceiver, new IntentFilter("android.internal.policy.action.BURN_IN_PROTECTION"));
      Intent localIntent = new Intent("android.internal.policy.action.BURN_IN_PROTECTION");
      localIntent.setPackage(paramContext.getPackageName());
      localIntent.setFlags(1073741824);
      this.mBurnInProtectionIntent = PendingIntent.getBroadcast(paramContext, 0, localIntent, 134217728);
      paramContext = (DisplayManager)paramContext.getSystemService("display");
      this.mDisplay = paramContext.getDisplay(0);
      paramContext.registerDisplayListener(this, null);
      this.mCenteringAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, 0.0F });
      this.mCenteringAnimator.setDuration(100L);
      this.mCenteringAnimator.setInterpolator(new LinearInterpolator());
      this.mCenteringAnimator.addListener(this);
      this.mCenteringAnimator.addUpdateListener(this);
      return;
    }
  }
  
  private void adjustOffsets()
  {
    do
    {
      int i = this.mXOffsetDirection * 2;
      this.mLastBurnInXOffset += i;
      if ((this.mLastBurnInXOffset > this.mMaxHorizontalBurnInOffset) || (this.mLastBurnInXOffset < this.mMinHorizontalBurnInOffset))
      {
        this.mLastBurnInXOffset -= i;
        this.mXOffsetDirection *= -1;
        i = this.mYOffsetDirection * 2;
        this.mLastBurnInYOffset += i;
        if ((this.mLastBurnInYOffset > this.mMaxVerticalBurnInOffset) || (this.mLastBurnInYOffset < this.mMinVerticalBurnInOffset))
        {
          this.mLastBurnInYOffset -= i;
          this.mYOffsetDirection *= -1;
        }
      }
    } while ((this.mBurnInRadiusMaxSquared != -1) && (this.mLastBurnInXOffset * this.mLastBurnInXOffset + this.mLastBurnInYOffset * this.mLastBurnInYOffset > this.mBurnInRadiusMaxSquared));
  }
  
  private void updateBurnInProtection()
  {
    if (this.mBurnInProtectionActive)
    {
      if (this.mFirstUpdate) {
        this.mFirstUpdate = false;
      }
      for (;;)
      {
        long l1 = System.currentTimeMillis();
        long l2 = SystemClock.elapsedRealtime();
        long l3 = l1 + BURNIN_PROTECTION_MINIMAL_INTERVAL_MS;
        long l4 = BURNIN_PROTECTION_WAKEUP_INTERVAL_MS;
        long l5 = BURNIN_PROTECTION_WAKEUP_INTERVAL_MS;
        this.mAlarmManager.setExact(3, l2 + (l3 - l3 % l4 + l5 - l1), this.mBurnInProtectionIntent);
        return;
        adjustOffsets();
        this.mAppliedBurnInXOffset = this.mLastBurnInXOffset;
        this.mAppliedBurnInYOffset = this.mLastBurnInYOffset;
        this.mDisplayManagerInternal.setDisplayOffsets(this.mDisplay.getDisplayId(), this.mLastBurnInXOffset, this.mLastBurnInYOffset);
      }
    }
    this.mAlarmManager.cancel(this.mBurnInProtectionIntent);
    this.mCenteringAnimator.start();
  }
  
  public void cancelBurnInProtection()
  {
    if (this.mBurnInProtectionActive)
    {
      this.mBurnInProtectionActive = false;
      updateBurnInProtection();
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println(paramString + "BurnInProtection");
    paramString = paramString + "  ";
    paramPrintWriter.println(paramString + "mBurnInProtectionActive=" + this.mBurnInProtectionActive);
    paramPrintWriter.println(paramString + "mHorizontalBurnInOffsetsBounds=(" + this.mMinHorizontalBurnInOffset + ", " + this.mMaxHorizontalBurnInOffset + ")");
    paramPrintWriter.println(paramString + "mVerticalBurnInOffsetsBounds=(" + this.mMinVerticalBurnInOffset + ", " + this.mMaxVerticalBurnInOffset + ")");
    paramPrintWriter.println(paramString + "mBurnInRadiusMaxSquared=" + this.mBurnInRadiusMaxSquared);
    paramPrintWriter.println(paramString + "mLastBurnInOffset=(" + this.mLastBurnInXOffset + ", " + this.mLastBurnInYOffset + ")");
    paramPrintWriter.println(paramString + "mOfsetChangeDirections=(" + this.mXOffsetDirection + ", " + this.mYOffsetDirection + ")");
  }
  
  public void onAnimationCancel(Animator paramAnimator) {}
  
  public void onAnimationEnd(Animator paramAnimator)
  {
    if ((paramAnimator != this.mCenteringAnimator) || (this.mBurnInProtectionActive)) {
      return;
    }
    this.mAppliedBurnInXOffset = 0;
    this.mAppliedBurnInYOffset = 0;
    this.mDisplayManagerInternal.setDisplayOffsets(this.mDisplay.getDisplayId(), 0, 0);
  }
  
  public void onAnimationRepeat(Animator paramAnimator) {}
  
  public void onAnimationStart(Animator paramAnimator) {}
  
  public void onAnimationUpdate(ValueAnimator paramValueAnimator)
  {
    if (!this.mBurnInProtectionActive)
    {
      float f = ((Float)paramValueAnimator.getAnimatedValue()).floatValue();
      this.mDisplayManagerInternal.setDisplayOffsets(this.mDisplay.getDisplayId(), (int)(this.mAppliedBurnInXOffset * f), (int)(this.mAppliedBurnInYOffset * f));
    }
  }
  
  public void onDisplayAdded(int paramInt) {}
  
  public void onDisplayChanged(int paramInt)
  {
    if (paramInt == this.mDisplay.getDisplayId())
    {
      if ((this.mDisplay.getState() == 3) || (this.mDisplay.getState() == 4)) {
        startBurnInProtection();
      }
    }
    else {
      return;
    }
    cancelBurnInProtection();
  }
  
  public void onDisplayRemoved(int paramInt) {}
  
  public void startBurnInProtection()
  {
    if (!this.mBurnInProtectionActive)
    {
      this.mBurnInProtectionActive = true;
      this.mFirstUpdate = true;
      this.mCenteringAnimator.cancel();
      updateBurnInProtection();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/BurnInProtectionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */