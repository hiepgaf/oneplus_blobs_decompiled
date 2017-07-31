package com.android.server.policy;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.util.MathUtils;
import android.view.MotionEvent;
import android.view.WindowManagerInternal;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.IAccessibilityManager;
import android.view.accessibility.IAccessibilityManager.Stub;
import com.android.server.LocalServices;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

public class EnableAccessibilityController
{
  private static final int ENABLE_ACCESSIBILITY_DELAY_MILLIS = 6000;
  public static final int MESSAGE_ENABLE_ACCESSIBILITY = 3;
  public static final int MESSAGE_SPEAK_ENABLE_CANCELED = 2;
  public static final int MESSAGE_SPEAK_WARNING = 1;
  private static final int SPEAK_WARNING_DELAY_MILLIS = 2000;
  private static final String TAG = "EnableAccessibilityController";
  private final IAccessibilityManager mAccessibilityManager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService("accessibility"));
  private boolean mCanceled;
  private final Context mContext;
  private boolean mDestroyed;
  private float mFirstPointerDownX;
  private float mFirstPointerDownY;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        paramAnonymousMessage = EnableAccessibilityController.-get0(EnableAccessibilityController.this).getString(17040707);
        EnableAccessibilityController.-get3(EnableAccessibilityController.this).speak(paramAnonymousMessage, 0, null);
        return;
      case 2: 
        paramAnonymousMessage = EnableAccessibilityController.-get0(EnableAccessibilityController.this).getString(17040709);
        EnableAccessibilityController.-get3(EnableAccessibilityController.this).speak(paramAnonymousMessage, 0, null);
        return;
      }
      EnableAccessibilityController.-wrap0(EnableAccessibilityController.this);
      if (EnableAccessibilityController.-get2(EnableAccessibilityController.this) != null) {
        EnableAccessibilityController.-get2(EnableAccessibilityController.this).play();
      }
      EnableAccessibilityController.-get3(EnableAccessibilityController.this).speak(EnableAccessibilityController.-get0(EnableAccessibilityController.this).getString(17040708), 0, null);
    }
  };
  private final Runnable mOnAccessibilityEnabledCallback;
  private float mSecondPointerDownX;
  private float mSecondPointerDownY;
  private Ringtone mTone;
  private final float mTouchSlop;
  private final TextToSpeech mTts;
  private final UserManager mUserManager;
  
  public EnableAccessibilityController(final Context paramContext, Runnable paramRunnable)
  {
    this.mContext = paramContext;
    this.mOnAccessibilityEnabledCallback = paramRunnable;
    this.mUserManager = ((UserManager)this.mContext.getSystemService("user"));
    this.mTts = new TextToSpeech(paramContext, new TextToSpeech.OnInitListener()
    {
      public void onInit(int paramAnonymousInt)
      {
        if (EnableAccessibilityController.-get1(EnableAccessibilityController.this)) {
          EnableAccessibilityController.-get3(EnableAccessibilityController.this).shutdown();
        }
      }
    });
    AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable()
    {
      public void run()
      {
        EnableAccessibilityController.-set0(EnableAccessibilityController.this, RingtoneManager.getRingtone(paramContext, Settings.System.DEFAULT_NOTIFICATION_URI));
        if (EnableAccessibilityController.-get2(EnableAccessibilityController.this) != null) {
          EnableAccessibilityController.-get2(EnableAccessibilityController.this).setStreamType(3);
        }
      }
    });
    this.mTouchSlop = paramContext.getResources().getDimensionPixelSize(17105039);
  }
  
  public static boolean canEnableAccessibilityViaGesture(Context paramContext)
  {
    AccessibilityManager localAccessibilityManager = AccessibilityManager.getInstance(paramContext);
    if ((!localAccessibilityManager.isEnabled()) || (localAccessibilityManager.getEnabledAccessibilityServiceList(1).isEmpty()))
    {
      if ((Settings.Global.getInt(paramContext.getContentResolver(), "enable_accessibility_global_gesture_enabled", 0) != 1) || (getInstalledSpeakingAccessibilityServices(paramContext).isEmpty())) {
        return false;
      }
    }
    else {
      return false;
    }
    return true;
  }
  
  private void cancel()
  {
    this.mCanceled = true;
    if (this.mHandler.hasMessages(1)) {
      this.mHandler.removeMessages(1);
    }
    for (;;)
    {
      this.mHandler.removeMessages(3);
      return;
      if (this.mHandler.hasMessages(3)) {
        this.mHandler.sendEmptyMessage(2);
      }
    }
  }
  
  public static void disableAccessibility(Context paramContext)
  {
    IAccessibilityManager localIAccessibilityManager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService("accessibility"));
    paramContext = getInstalledSpeakingAccessibilityServiceComponent(paramContext);
    if (paramContext == null) {
      return;
    }
    int i = ActivityManager.getCurrentUser();
    try
    {
      localIAccessibilityManager.disableAccessibilityService(paramContext, i);
      return;
    }
    catch (RemoteException paramContext)
    {
      Log.e("EnableAccessibilityController", "cannot disable accessibility " + paramContext);
    }
  }
  
  private void enableAccessibility()
  {
    if (enableAccessibility(this.mContext)) {
      this.mOnAccessibilityEnabledCallback.run();
    }
  }
  
  public static boolean enableAccessibility(Context paramContext)
  {
    IAccessibilityManager localIAccessibilityManager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService("accessibility"));
    WindowManagerInternal localWindowManagerInternal = (WindowManagerInternal)LocalServices.getService(WindowManagerInternal.class);
    UserManager localUserManager = (UserManager)paramContext.getSystemService("user");
    paramContext = getInstalledSpeakingAccessibilityServiceComponent(paramContext);
    if (paramContext == null) {
      return false;
    }
    boolean bool = localWindowManagerInternal.isKeyguardLocked();
    if (localUserManager.getUsers().size() > 1) {}
    for (int i = 1;; i = 0)
    {
      if ((!bool) || (i == 0) || (bool))
      {
        try
        {
          localIAccessibilityManager.temporaryEnableAccessibilityStateUntilKeyguardRemoved(paramContext, true);
          return true;
        }
        catch (RemoteException paramContext)
        {
          Log.e("EnableAccessibilityController", "cannot enable accessibilty: " + paramContext);
        }
        localIAccessibilityManager.enableAccessibilityService(paramContext, ActivityManager.getCurrentUser());
        return true;
      }
      return true;
    }
  }
  
  public static ComponentName getInstalledSpeakingAccessibilityServiceComponent(Context paramContext)
  {
    paramContext = getInstalledSpeakingAccessibilityServices(paramContext);
    if (paramContext.isEmpty()) {
      return null;
    }
    paramContext = ((AccessibilityServiceInfo)paramContext.get(0)).getResolveInfo().serviceInfo;
    return new ComponentName(paramContext.packageName, paramContext.name);
  }
  
  public static List<AccessibilityServiceInfo> getInstalledSpeakingAccessibilityServices(Context paramContext)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(AccessibilityManager.getInstance(paramContext).getInstalledAccessibilityServiceList());
    paramContext = localArrayList.iterator();
    while (paramContext.hasNext()) {
      if ((((AccessibilityServiceInfo)paramContext.next()).feedbackType & 0x1) == 0) {
        paramContext.remove();
      }
    }
    return localArrayList;
  }
  
  public static boolean isAccessibilityEnabled(Context paramContext)
  {
    paramContext = ((AccessibilityManager)paramContext.getSystemService(AccessibilityManager.class)).getEnabledAccessibilityServiceList(1);
    return (paramContext != null) && (!paramContext.isEmpty());
  }
  
  public void onDestroy()
  {
    this.mDestroyed = true;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getActionMasked() == 5) && (paramMotionEvent.getPointerCount() == 2))
    {
      this.mFirstPointerDownX = paramMotionEvent.getX(0);
      this.mFirstPointerDownY = paramMotionEvent.getY(0);
      this.mSecondPointerDownX = paramMotionEvent.getX(1);
      this.mSecondPointerDownY = paramMotionEvent.getY(1);
      this.mHandler.sendEmptyMessageDelayed(1, 2000L);
      this.mHandler.sendEmptyMessageDelayed(3, 6000L);
      return true;
    }
    return false;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getPointerCount();
    int j = paramMotionEvent.getActionMasked();
    if (this.mCanceled)
    {
      if (j == 1) {
        this.mCanceled = false;
      }
      return true;
    }
    switch (j)
    {
    case 4: 
    default: 
    case 5: 
    case 2: 
      do
      {
        do
        {
          return true;
        } while (i <= 2);
        cancel();
        return true;
        if (Math.abs(MathUtils.dist(paramMotionEvent.getX(0), paramMotionEvent.getY(0), this.mFirstPointerDownX, this.mFirstPointerDownY)) > this.mTouchSlop) {
          cancel();
        }
      } while (Math.abs(MathUtils.dist(paramMotionEvent.getX(1), paramMotionEvent.getY(1), this.mSecondPointerDownX, this.mSecondPointerDownY)) <= this.mTouchSlop);
      cancel();
      return true;
    }
    cancel();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/EnableAccessibilityController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */