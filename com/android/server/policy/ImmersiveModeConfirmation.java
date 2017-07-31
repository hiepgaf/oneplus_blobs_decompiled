package com.android.server.policy;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.service.vr.IVrManager;
import android.service.vr.IVrManager.Stub;
import android.service.vr.IVrStateCallbacks;
import android.service.vr.IVrStateCallbacks.Stub;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.ViewTreeObserver.OnComputeInternalInsetsListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class ImmersiveModeConfirmation
{
  private static final String CONFIRMED = "confirmed";
  private static final boolean DEBUG = false;
  private static final boolean DEBUG_SHOW_EVERY_TIME = false;
  private static final String TAG = "ImmersiveModeConfirmation";
  private ClingWindowView mClingWindow;
  private final Runnable mConfirm = new Runnable()
  {
    public void run()
    {
      if (!ImmersiveModeConfirmation.-get0(ImmersiveModeConfirmation.this))
      {
        ImmersiveModeConfirmation.-set0(ImmersiveModeConfirmation.this, true);
        ImmersiveModeConfirmation.-wrap2(ImmersiveModeConfirmation.this);
      }
      ImmersiveModeConfirmation.-wrap0(ImmersiveModeConfirmation.this);
    }
  };
  private boolean mConfirmed;
  private final Context mContext;
  private int mCurrentUserId;
  private final H mHandler;
  private final long mPanicThresholdMs;
  private long mPanicTime;
  private final long mShowDelayMs;
  boolean mVrModeEnabled = false;
  private final IVrStateCallbacks mVrStateCallbacks = new IVrStateCallbacks.Stub()
  {
    public void onVrStateChanged(boolean paramAnonymousBoolean)
      throws RemoteException
    {
      ImmersiveModeConfirmation.this.mVrModeEnabled = paramAnonymousBoolean;
      if (ImmersiveModeConfirmation.this.mVrModeEnabled)
      {
        ImmersiveModeConfirmation.-get1(ImmersiveModeConfirmation.this).removeMessages(1);
        ImmersiveModeConfirmation.-get1(ImmersiveModeConfirmation.this).sendEmptyMessage(2);
      }
    }
  };
  private WindowManager mWindowManager;
  private final IBinder mWindowToken = new Binder();
  
  public ImmersiveModeConfirmation(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHandler = new H(null);
    this.mShowDelayMs = (getNavBarExitDuration() * 3L);
    this.mPanicThresholdMs = paramContext.getResources().getInteger(17694867);
    this.mWindowManager = ((WindowManager)this.mContext.getSystemService("window"));
  }
  
  private long getNavBarExitDuration()
  {
    Animation localAnimation = AnimationUtils.loadAnimation(this.mContext, 17432612);
    if (localAnimation != null) {
      return localAnimation.getDuration();
    }
    return 0L;
  }
  
  private void handleHide()
  {
    if (this.mClingWindow != null)
    {
      this.mWindowManager.removeView(this.mClingWindow);
      this.mClingWindow = null;
    }
  }
  
  private void handleShow()
  {
    this.mClingWindow = new ClingWindowView(this.mContext, this.mConfirm);
    this.mClingWindow.setSystemUiVisibility(768);
    WindowManager.LayoutParams localLayoutParams = getClingWindowLayoutParams();
    this.mWindowManager.addView(this.mClingWindow, localLayoutParams);
  }
  
  private void saveSetting()
  {
    try
    {
      if (this.mConfirmed) {}
      for (String str = "confirmed";; str = null)
      {
        Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "immersive_mode_confirmations", str, -2);
        return;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      Slog.w("ImmersiveModeConfirmation", "Error saving confirmations, mConfirmed=" + this.mConfirmed, localThrowable);
    }
  }
  
  public void confirmCurrentPrompt()
  {
    if (this.mClingWindow != null) {
      this.mHandler.post(this.mConfirm);
    }
  }
  
  public FrameLayout.LayoutParams getBubbleLayoutParams()
  {
    return new FrameLayout.LayoutParams(this.mContext.getResources().getDimensionPixelSize(17105057), -2, 49);
  }
  
  public WindowManager.LayoutParams getClingWindowLayoutParams()
  {
    WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(-1, -1, 2014, 16777472, -3);
    localLayoutParams.privateFlags |= 0x10;
    localLayoutParams.setTitle("ImmersiveModeConfirmation");
    localLayoutParams.windowAnimations = 16974583;
    localLayoutParams.token = getWindowToken();
    return localLayoutParams;
  }
  
  public IBinder getWindowToken()
  {
    return this.mWindowToken;
  }
  
  public void immersiveModeChangedLw(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.mHandler.removeMessages(1);
    if (paramBoolean1)
    {
      if ((PolicyControl.disableImmersiveConfirmation(paramString)) || (this.mConfirmed)) {}
      while ((!paramBoolean2) || (this.mVrModeEnabled) || (paramBoolean3) || (UserManager.isDeviceInDemoMode(this.mContext))) {
        return;
      }
      this.mHandler.sendEmptyMessageDelayed(1, this.mShowDelayMs);
      return;
    }
    this.mHandler.sendEmptyMessage(2);
  }
  
  public void loadSetting(int paramInt)
  {
    this.mConfirmed = false;
    this.mCurrentUserId = paramInt;
    Object localObject = null;
    try
    {
      String str = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "immersive_mode_confirmations", -2);
      localObject = str;
      this.mConfirmed = "confirmed".equals(str);
      return;
    }
    catch (Throwable localThrowable)
    {
      Slog.w("ImmersiveModeConfirmation", "Error loading confirmations, value=" + (String)localObject, localThrowable);
    }
  }
  
  public boolean onPowerKeyDown(boolean paramBoolean1, long paramLong, boolean paramBoolean2, boolean paramBoolean3)
  {
    boolean bool = false;
    if ((!paramBoolean1) && (paramLong - this.mPanicTime < this.mPanicThresholdMs))
    {
      paramBoolean1 = bool;
      if (this.mClingWindow == null) {
        paramBoolean1 = true;
      }
      return paramBoolean1;
    }
    if ((!paramBoolean1) || (!paramBoolean2) || (paramBoolean3))
    {
      this.mPanicTime = 0L;
      return false;
    }
    this.mPanicTime = paramLong;
    return false;
  }
  
  void systemReady()
  {
    IVrManager localIVrManager = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
    if (localIVrManager != null) {}
    try
    {
      localIVrManager.registerListener(this.mVrStateCallbacks);
      this.mVrModeEnabled = localIVrManager.getVrModeState();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private class ClingWindowView
    extends FrameLayout
  {
    private static final int ANIMATION_DURATION = 250;
    private static final int BGCOLOR = Integer.MIN_VALUE;
    private static final int OFFSET_DP = 96;
    private ViewGroup mClingLayout;
    private final ColorDrawable mColor = new ColorDrawable(0);
    private ValueAnimator mColorAnim;
    private final Runnable mConfirm;
    private ViewTreeObserver.OnComputeInternalInsetsListener mInsetsListener = new ViewTreeObserver.OnComputeInternalInsetsListener()
    {
      private final int[] mTmpInt2 = new int[2];
      
      public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo paramAnonymousInternalInsetsInfo)
      {
        ImmersiveModeConfirmation.ClingWindowView.-get0(ImmersiveModeConfirmation.ClingWindowView.this).getLocationInWindow(this.mTmpInt2);
        paramAnonymousInternalInsetsInfo.setTouchableInsets(3);
        paramAnonymousInternalInsetsInfo.touchableRegion.set(this.mTmpInt2[0], this.mTmpInt2[1], this.mTmpInt2[0] + ImmersiveModeConfirmation.ClingWindowView.-get0(ImmersiveModeConfirmation.ClingWindowView.this).getWidth(), this.mTmpInt2[1] + ImmersiveModeConfirmation.ClingWindowView.-get0(ImmersiveModeConfirmation.ClingWindowView.this).getHeight());
      }
    };
    private final Interpolator mInterpolator;
    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (paramAnonymousIntent.getAction().equals("android.intent.action.CONFIGURATION_CHANGED")) {
          ImmersiveModeConfirmation.ClingWindowView.this.post(ImmersiveModeConfirmation.ClingWindowView.-get5(ImmersiveModeConfirmation.ClingWindowView.this));
        }
      }
    };
    private Runnable mUpdateLayoutRunnable = new Runnable()
    {
      public void run()
      {
        if ((ImmersiveModeConfirmation.ClingWindowView.-get0(ImmersiveModeConfirmation.ClingWindowView.this) != null) && (ImmersiveModeConfirmation.ClingWindowView.-get0(ImmersiveModeConfirmation.ClingWindowView.this).getParent() != null)) {
          ImmersiveModeConfirmation.ClingWindowView.-get0(ImmersiveModeConfirmation.ClingWindowView.this).setLayoutParams(ImmersiveModeConfirmation.this.getBubbleLayoutParams());
        }
      }
    };
    
    public ClingWindowView(Context paramContext, Runnable paramRunnable)
    {
      super();
      this.mConfirm = paramRunnable;
      setBackground(this.mColor);
      setImportantForAccessibility(2);
      this.mInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563662);
    }
    
    public void onAttachedToWindow()
    {
      super.onAttachedToWindow();
      final Object localObject = new DisplayMetrics();
      ImmersiveModeConfirmation.-get2(ImmersiveModeConfirmation.this).getDefaultDisplay().getMetrics((DisplayMetrics)localObject);
      float f = ((DisplayMetrics)localObject).density;
      getViewTreeObserver().addOnComputeInternalInsetsListener(this.mInsetsListener);
      this.mClingLayout = ((ViewGroup)View.inflate(getContext(), 17367149, null));
      ((Button)this.mClingLayout.findViewById(16909191)).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ImmersiveModeConfirmation.ClingWindowView.-get3(ImmersiveModeConfirmation.ClingWindowView.this).run();
        }
      });
      addView(this.mClingLayout, ImmersiveModeConfirmation.this.getBubbleLayoutParams());
      if (ActivityManager.isHighEndGfx())
      {
        localObject = this.mClingLayout;
        ((View)localObject).setAlpha(0.0F);
        ((View)localObject).setTranslationY(-96.0F * f);
        postOnAnimation(new Runnable()
        {
          public void run()
          {
            localObject.animate().alpha(1.0F).translationY(0.0F).setDuration(250L).setInterpolator(ImmersiveModeConfirmation.ClingWindowView.-get4(ImmersiveModeConfirmation.ClingWindowView.this)).withLayer().start();
            ImmersiveModeConfirmation.ClingWindowView.-set0(ImmersiveModeConfirmation.ClingWindowView.this, ValueAnimator.ofObject(new ArgbEvaluator(), new Object[] { Integer.valueOf(0), Integer.valueOf(Integer.MIN_VALUE) }));
            ImmersiveModeConfirmation.ClingWindowView.-get2(ImmersiveModeConfirmation.ClingWindowView.this).addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
              public void onAnimationUpdate(ValueAnimator paramAnonymous2ValueAnimator)
              {
                int i = ((Integer)paramAnonymous2ValueAnimator.getAnimatedValue()).intValue();
                ImmersiveModeConfirmation.ClingWindowView.-get1(ImmersiveModeConfirmation.ClingWindowView.this).setColor(i);
              }
            });
            ImmersiveModeConfirmation.ClingWindowView.-get2(ImmersiveModeConfirmation.ClingWindowView.this).setDuration(250L);
            ImmersiveModeConfirmation.ClingWindowView.-get2(ImmersiveModeConfirmation.ClingWindowView.this).setInterpolator(ImmersiveModeConfirmation.ClingWindowView.-get4(ImmersiveModeConfirmation.ClingWindowView.this));
            ImmersiveModeConfirmation.ClingWindowView.-get2(ImmersiveModeConfirmation.ClingWindowView.this).start();
          }
        });
      }
      for (;;)
      {
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"));
        return;
        this.mColor.setColor(Integer.MIN_VALUE);
      }
    }
    
    public void onDetachedFromWindow()
    {
      this.mContext.unregisterReceiver(this.mReceiver);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return true;
    }
  }
  
  private final class H
    extends Handler
  {
    private static final int HIDE = 2;
    private static final int SHOW = 1;
    
    private H() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        ImmersiveModeConfirmation.-wrap1(ImmersiveModeConfirmation.this);
        return;
      }
      ImmersiveModeConfirmation.-wrap0(ImmersiveModeConfirmation.this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/ImmersiveModeConfirmation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */