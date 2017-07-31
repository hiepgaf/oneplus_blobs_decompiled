package android.support.v4.view;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public class ViewCompat
{
  public static final int ACCESSIBILITY_LIVE_REGION_ASSERTIVE = 2;
  public static final int ACCESSIBILITY_LIVE_REGION_NONE = 0;
  public static final int ACCESSIBILITY_LIVE_REGION_POLITE = 1;
  private static final long FAKE_FRAME_TIME = 10L;
  static final ViewCompatImpl IMPL;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS = 4;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1;
  public static final int LAYER_TYPE_HARDWARE = 2;
  public static final int LAYER_TYPE_NONE = 0;
  public static final int LAYER_TYPE_SOFTWARE = 1;
  public static final int LAYOUT_DIRECTION_INHERIT = 2;
  public static final int LAYOUT_DIRECTION_LOCALE = 3;
  public static final int LAYOUT_DIRECTION_LTR = 0;
  public static final int LAYOUT_DIRECTION_RTL = 1;
  public static final int MEASURED_HEIGHT_STATE_SHIFT = 16;
  public static final int MEASURED_SIZE_MASK = 16777215;
  public static final int MEASURED_STATE_MASK = -16777216;
  public static final int MEASURED_STATE_TOO_SMALL = 16777216;
  public static final int OVER_SCROLL_ALWAYS = 0;
  public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
  public static final int OVER_SCROLL_NEVER = 2;
  private static final String TAG = "ViewCompat";
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 21)
    {
      if (i < 19)
      {
        if (i >= 17) {
          break label85;
        }
        if (i >= 16) {
          break label96;
        }
        if (i >= 14) {
          break label107;
        }
        if (i >= 11) {
          break label118;
        }
        if (i >= 9) {
          break label129;
        }
        if (i >= 7) {
          break label140;
        }
        IMPL = new BaseViewCompatImpl();
      }
    }
    else
    {
      IMPL = new Api21ViewCompatImpl();
      return;
    }
    IMPL = new KitKatViewCompatImpl();
    return;
    label85:
    IMPL = new JbMr1ViewCompatImpl();
    return;
    label96:
    IMPL = new JBViewCompatImpl();
    return;
    label107:
    IMPL = new ICSViewCompatImpl();
    return;
    label118:
    IMPL = new HCViewCompatImpl();
    return;
    label129:
    IMPL = new GBViewCompatImpl();
    return;
    label140:
    IMPL = new EclairMr1ViewCompatImpl();
  }
  
  public static ViewPropertyAnimatorCompat animate(View paramView)
  {
    return IMPL.animate(paramView);
  }
  
  public static boolean canScrollHorizontally(View paramView, int paramInt)
  {
    return IMPL.canScrollHorizontally(paramView, paramInt);
  }
  
  public static boolean canScrollVertically(View paramView, int paramInt)
  {
    return IMPL.canScrollVertically(paramView, paramInt);
  }
  
  public static void dispatchFinishTemporaryDetach(View paramView)
  {
    IMPL.dispatchFinishTemporaryDetach(paramView);
  }
  
  public static void dispatchStartTemporaryDetach(View paramView)
  {
    IMPL.dispatchStartTemporaryDetach(paramView);
  }
  
  public static int getAccessibilityLiveRegion(View paramView)
  {
    return IMPL.getAccessibilityLiveRegion(paramView);
  }
  
  public static AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View paramView)
  {
    return IMPL.getAccessibilityNodeProvider(paramView);
  }
  
  public static float getAlpha(View paramView)
  {
    return IMPL.getAlpha(paramView);
  }
  
  public static float getElevation(View paramView)
  {
    return IMPL.getElevation(paramView);
  }
  
  public static boolean getFitsSystemWindows(View paramView)
  {
    return IMPL.getFitsSystemWindows(paramView);
  }
  
  public static int getImportantForAccessibility(View paramView)
  {
    return IMPL.getImportantForAccessibility(paramView);
  }
  
  public static int getLabelFor(View paramView)
  {
    return IMPL.getLabelFor(paramView);
  }
  
  public static int getLayerType(View paramView)
  {
    return IMPL.getLayerType(paramView);
  }
  
  public static int getLayoutDirection(View paramView)
  {
    return IMPL.getLayoutDirection(paramView);
  }
  
  public static int getMeasuredHeightAndState(View paramView)
  {
    return IMPL.getMeasuredHeightAndState(paramView);
  }
  
  public static int getMeasuredState(View paramView)
  {
    return IMPL.getMeasuredState(paramView);
  }
  
  public static int getMeasuredWidthAndState(View paramView)
  {
    return IMPL.getMeasuredWidthAndState(paramView);
  }
  
  public static int getMinimumHeight(View paramView)
  {
    return IMPL.getMinimumHeight(paramView);
  }
  
  public static int getMinimumWidth(View paramView)
  {
    return IMPL.getMinimumWidth(paramView);
  }
  
  public static int getOverScrollMode(View paramView)
  {
    return IMPL.getOverScrollMode(paramView);
  }
  
  public static int getPaddingEnd(View paramView)
  {
    return IMPL.getPaddingEnd(paramView);
  }
  
  public static int getPaddingStart(View paramView)
  {
    return IMPL.getPaddingStart(paramView);
  }
  
  public static ViewParent getParentForAccessibility(View paramView)
  {
    return IMPL.getParentForAccessibility(paramView);
  }
  
  public static float getPivotX(View paramView)
  {
    return IMPL.getPivotX(paramView);
  }
  
  public static float getPivotY(View paramView)
  {
    return IMPL.getPivotY(paramView);
  }
  
  public static float getRotation(View paramView)
  {
    return IMPL.getRotation(paramView);
  }
  
  public static float getRotationX(View paramView)
  {
    return IMPL.getRotationX(paramView);
  }
  
  public static float getRotationY(View paramView)
  {
    return IMPL.getRotationY(paramView);
  }
  
  public static float getScaleX(View paramView)
  {
    return IMPL.getScaleX(paramView);
  }
  
  public static float getScaleY(View paramView)
  {
    return IMPL.getScaleY(paramView);
  }
  
  public static String getTransitionName(View paramView)
  {
    return IMPL.getTransitionName(paramView);
  }
  
  public static float getTranslationX(View paramView)
  {
    return IMPL.getTranslationX(paramView);
  }
  
  public static float getTranslationY(View paramView)
  {
    return IMPL.getTranslationY(paramView);
  }
  
  public static float getTranslationZ(View paramView)
  {
    return IMPL.getTranslationZ(paramView);
  }
  
  public static int getWindowSystemUiVisibility(View paramView)
  {
    return IMPL.getWindowSystemUiVisibility(paramView);
  }
  
  public static float getX(View paramView)
  {
    return IMPL.getX(paramView);
  }
  
  public static float getY(View paramView)
  {
    return IMPL.getY(paramView);
  }
  
  public static boolean hasAccessibilityDelegate(View paramView)
  {
    return IMPL.hasAccessibilityDelegate(paramView);
  }
  
  public static boolean hasTransientState(View paramView)
  {
    return IMPL.hasTransientState(paramView);
  }
  
  public static boolean isOpaque(View paramView)
  {
    return IMPL.isOpaque(paramView);
  }
  
  public static void jumpDrawablesToCurrentState(View paramView)
  {
    IMPL.jumpDrawablesToCurrentState(paramView);
  }
  
  public static void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    IMPL.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
  }
  
  public static void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    IMPL.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
  }
  
  public static void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    IMPL.onPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
  }
  
  public static boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
  {
    return IMPL.performAccessibilityAction(paramView, paramInt, paramBundle);
  }
  
  public static void postInvalidateOnAnimation(View paramView)
  {
    IMPL.postInvalidateOnAnimation(paramView);
  }
  
  public static void postInvalidateOnAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    IMPL.postInvalidateOnAnimation(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static void postOnAnimation(View paramView, Runnable paramRunnable)
  {
    IMPL.postOnAnimation(paramView, paramRunnable);
  }
  
  public static void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong)
  {
    IMPL.postOnAnimationDelayed(paramView, paramRunnable, paramLong);
  }
  
  public static void requestApplyInsets(View paramView)
  {
    IMPL.requestApplyInsets(paramView);
  }
  
  public static int resolveSizeAndState(int paramInt1, int paramInt2, int paramInt3)
  {
    return IMPL.resolveSizeAndState(paramInt1, paramInt2, paramInt3);
  }
  
  public static void setAccessibilityDelegate(View paramView, AccessibilityDelegateCompat paramAccessibilityDelegateCompat)
  {
    IMPL.setAccessibilityDelegate(paramView, paramAccessibilityDelegateCompat);
  }
  
  public static void setAccessibilityLiveRegion(View paramView, int paramInt)
  {
    IMPL.setAccessibilityLiveRegion(paramView, paramInt);
  }
  
  public static void setActivated(View paramView, boolean paramBoolean)
  {
    IMPL.setActivated(paramView, paramBoolean);
  }
  
  public static void setAlpha(View paramView, float paramFloat)
  {
    IMPL.setAlpha(paramView, paramFloat);
  }
  
  public static void setChildrenDrawingOrderEnabled(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    IMPL.setChildrenDrawingOrderEnabled(paramViewGroup, paramBoolean);
  }
  
  public static void setElevation(View paramView, float paramFloat)
  {
    IMPL.setElevation(paramView, paramFloat);
  }
  
  public static void setFitsSystemWindows(View paramView, boolean paramBoolean)
  {
    IMPL.setFitsSystemWindows(paramView, paramBoolean);
  }
  
  public static void setHasTransientState(View paramView, boolean paramBoolean)
  {
    IMPL.setHasTransientState(paramView, paramBoolean);
  }
  
  public static void setImportantForAccessibility(View paramView, int paramInt)
  {
    IMPL.setImportantForAccessibility(paramView, paramInt);
  }
  
  public static void setLabelFor(View paramView, int paramInt)
  {
    IMPL.setLabelFor(paramView, paramInt);
  }
  
  public static void setLayerPaint(View paramView, Paint paramPaint)
  {
    IMPL.setLayerPaint(paramView, paramPaint);
  }
  
  public static void setLayerType(View paramView, int paramInt, Paint paramPaint)
  {
    IMPL.setLayerType(paramView, paramInt, paramPaint);
  }
  
  public static void setLayoutDirection(View paramView, int paramInt)
  {
    IMPL.setLayoutDirection(paramView, paramInt);
  }
  
  public static void setOnApplyWindowInsetsListener(View paramView, OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener)
  {
    IMPL.setOnApplyWindowInsetsListener(paramView, paramOnApplyWindowInsetsListener);
  }
  
  public static void setOverScrollMode(View paramView, int paramInt)
  {
    IMPL.setOverScrollMode(paramView, paramInt);
  }
  
  public static void setPaddingRelative(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    IMPL.setPaddingRelative(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static void setPivotX(View paramView, float paramFloat)
  {
    IMPL.setPivotX(paramView, paramFloat);
  }
  
  public static void setPivotY(View paramView, float paramFloat)
  {
    IMPL.setPivotX(paramView, paramFloat);
  }
  
  public static void setRotation(View paramView, float paramFloat)
  {
    IMPL.setRotation(paramView, paramFloat);
  }
  
  public static void setRotationX(View paramView, float paramFloat)
  {
    IMPL.setRotationX(paramView, paramFloat);
  }
  
  public static void setRotationY(View paramView, float paramFloat)
  {
    IMPL.setRotationY(paramView, paramFloat);
  }
  
  public static void setSaveFromParentEnabled(View paramView, boolean paramBoolean)
  {
    IMPL.setSaveFromParentEnabled(paramView, paramBoolean);
  }
  
  public static void setScaleX(View paramView, float paramFloat)
  {
    IMPL.setScaleX(paramView, paramFloat);
  }
  
  public static void setScaleY(View paramView, float paramFloat)
  {
    IMPL.setScaleY(paramView, paramFloat);
  }
  
  public static void setTransitionName(View paramView, String paramString)
  {
    IMPL.setTransitionName(paramView, paramString);
  }
  
  public static void setTranslationX(View paramView, float paramFloat)
  {
    IMPL.setTranslationX(paramView, paramFloat);
  }
  
  public static void setTranslationY(View paramView, float paramFloat)
  {
    IMPL.setTranslationY(paramView, paramFloat);
  }
  
  public static void setTranslationZ(View paramView, float paramFloat)
  {
    IMPL.setTranslationZ(paramView, paramFloat);
  }
  
  public static void setX(View paramView, float paramFloat)
  {
    IMPL.setX(paramView, paramFloat);
  }
  
  public static void setY(View paramView, float paramFloat)
  {
    IMPL.setY(paramView, paramFloat);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L})
  private static @interface AccessibilityLiveRegion {}
  
  static class Api21ViewCompatImpl
    extends ViewCompat.KitKatViewCompatImpl
  {
    public float getElevation(View paramView)
    {
      return ViewCompatApi21.getElevation(paramView);
    }
    
    public String getTransitionName(View paramView)
    {
      return ViewCompatApi21.getTransitionName(paramView);
    }
    
    public float getTranslationZ(View paramView)
    {
      return ViewCompatApi21.getTranslationZ(paramView);
    }
    
    public boolean isImportantForAccessibility(View paramView)
    {
      return ViewCompatApi21.isImportantForAccessibility(paramView);
    }
    
    public void requestApplyInsets(View paramView)
    {
      ViewCompatApi21.requestApplyInsets(paramView);
    }
    
    public void setElevation(View paramView, float paramFloat)
    {
      ViewCompatApi21.setElevation(paramView, paramFloat);
    }
    
    public void setOnApplyWindowInsetsListener(View paramView, OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener)
    {
      ViewCompatApi21.setOnApplyWindowInsetsListener(paramView, paramOnApplyWindowInsetsListener);
    }
    
    public void setTransitionName(View paramView, String paramString)
    {
      ViewCompatApi21.setTransitionName(paramView, paramString);
    }
    
    public void setTranslationZ(View paramView, float paramFloat)
    {
      ViewCompatApi21.setTranslationZ(paramView, paramFloat);
    }
  }
  
  static class BaseViewCompatImpl
    implements ViewCompat.ViewCompatImpl
  {
    private Method mDispatchFinishTemporaryDetach;
    private Method mDispatchStartTemporaryDetach;
    private boolean mTempDetachBound;
    WeakHashMap<View, ViewPropertyAnimatorCompat> mViewPropertyAnimatorCompatMap = null;
    
    private void bindTempDetach()
    {
      try
      {
        this.mDispatchStartTemporaryDetach = View.class.getDeclaredMethod("dispatchStartTemporaryDetach", new Class[0]);
        this.mDispatchFinishTemporaryDetach = View.class.getDeclaredMethod("dispatchFinishTemporaryDetach", new Class[0]);
        this.mTempDetachBound = true;
        return;
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        for (;;)
        {
          Log.e("ViewCompat", "Couldn't find method", localNoSuchMethodException);
        }
      }
    }
    
    public ViewPropertyAnimatorCompat animate(View paramView)
    {
      return new ViewPropertyAnimatorCompat(paramView);
    }
    
    public boolean canScrollHorizontally(View paramView, int paramInt)
    {
      return false;
    }
    
    public boolean canScrollVertically(View paramView, int paramInt)
    {
      return false;
    }
    
    public void dispatchFinishTemporaryDetach(View paramView)
    {
      if (this.mTempDetachBound) {}
      while (this.mDispatchFinishTemporaryDetach == null)
      {
        paramView.onFinishTemporaryDetach();
        return;
        bindTempDetach();
      }
      try
      {
        this.mDispatchFinishTemporaryDetach.invoke(paramView, new Object[0]);
        return;
      }
      catch (Exception paramView)
      {
        Log.d("ViewCompat", "Error calling dispatchFinishTemporaryDetach", paramView);
      }
    }
    
    public void dispatchStartTemporaryDetach(View paramView)
    {
      if (this.mTempDetachBound) {}
      while (this.mDispatchStartTemporaryDetach == null)
      {
        paramView.onStartTemporaryDetach();
        return;
        bindTempDetach();
      }
      try
      {
        this.mDispatchStartTemporaryDetach.invoke(paramView, new Object[0]);
        return;
      }
      catch (Exception paramView)
      {
        Log.d("ViewCompat", "Error calling dispatchStartTemporaryDetach", paramView);
      }
    }
    
    public int getAccessibilityLiveRegion(View paramView)
    {
      return 0;
    }
    
    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View paramView)
    {
      return null;
    }
    
    public float getAlpha(View paramView)
    {
      return 1.0F;
    }
    
    public float getElevation(View paramView)
    {
      return 0.0F;
    }
    
    public boolean getFitsSystemWindows(View paramView)
    {
      return false;
    }
    
    long getFrameTime()
    {
      return 10L;
    }
    
    public int getImportantForAccessibility(View paramView)
    {
      return 0;
    }
    
    public int getLabelFor(View paramView)
    {
      return 0;
    }
    
    public int getLayerType(View paramView)
    {
      return 0;
    }
    
    public int getLayoutDirection(View paramView)
    {
      return 0;
    }
    
    public int getMeasuredHeightAndState(View paramView)
    {
      return paramView.getMeasuredHeight();
    }
    
    public int getMeasuredState(View paramView)
    {
      return 0;
    }
    
    public int getMeasuredWidthAndState(View paramView)
    {
      return paramView.getMeasuredWidth();
    }
    
    public int getMinimumHeight(View paramView)
    {
      return 0;
    }
    
    public int getMinimumWidth(View paramView)
    {
      return 0;
    }
    
    public int getOverScrollMode(View paramView)
    {
      return 2;
    }
    
    public int getPaddingEnd(View paramView)
    {
      return paramView.getPaddingRight();
    }
    
    public int getPaddingStart(View paramView)
    {
      return paramView.getPaddingLeft();
    }
    
    public ViewParent getParentForAccessibility(View paramView)
    {
      return paramView.getParent();
    }
    
    public float getPivotX(View paramView)
    {
      return 0.0F;
    }
    
    public float getPivotY(View paramView)
    {
      return 0.0F;
    }
    
    public float getRotation(View paramView)
    {
      return 0.0F;
    }
    
    public float getRotationX(View paramView)
    {
      return 0.0F;
    }
    
    public float getRotationY(View paramView)
    {
      return 0.0F;
    }
    
    public float getScaleX(View paramView)
    {
      return 0.0F;
    }
    
    public float getScaleY(View paramView)
    {
      return 0.0F;
    }
    
    public String getTransitionName(View paramView)
    {
      return null;
    }
    
    public float getTranslationX(View paramView)
    {
      return 0.0F;
    }
    
    public float getTranslationY(View paramView)
    {
      return 0.0F;
    }
    
    public float getTranslationZ(View paramView)
    {
      return 0.0F;
    }
    
    public int getWindowSystemUiVisibility(View paramView)
    {
      return 0;
    }
    
    public float getX(View paramView)
    {
      return 0.0F;
    }
    
    public float getY(View paramView)
    {
      return 0.0F;
    }
    
    public boolean hasAccessibilityDelegate(View paramView)
    {
      return false;
    }
    
    public boolean hasTransientState(View paramView)
    {
      return false;
    }
    
    public boolean isImportantForAccessibility(View paramView)
    {
      return true;
    }
    
    public boolean isOpaque(View paramView)
    {
      paramView = paramView.getBackground();
      if (paramView == null) {
        return false;
      }
      return paramView.getOpacity() == -1;
    }
    
    public void jumpDrawablesToCurrentState(View paramView) {}
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent) {}
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat) {}
    
    public void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent) {}
    
    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      return false;
    }
    
    public void postInvalidateOnAnimation(View paramView)
    {
      paramView.invalidate();
    }
    
    public void postInvalidateOnAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramView.invalidate(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void postOnAnimation(View paramView, Runnable paramRunnable)
    {
      paramView.postDelayed(paramRunnable, getFrameTime());
    }
    
    public void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong)
    {
      paramView.postDelayed(paramRunnable, getFrameTime() + paramLong);
    }
    
    public void requestApplyInsets(View paramView) {}
    
    public int resolveSizeAndState(int paramInt1, int paramInt2, int paramInt3)
    {
      return View.resolveSize(paramInt1, paramInt2);
    }
    
    public void setAccessibilityDelegate(View paramView, AccessibilityDelegateCompat paramAccessibilityDelegateCompat) {}
    
    public void setAccessibilityLiveRegion(View paramView, int paramInt) {}
    
    public void setActivated(View paramView, boolean paramBoolean) {}
    
    public void setAlpha(View paramView, float paramFloat) {}
    
    public void setChildrenDrawingOrderEnabled(ViewGroup paramViewGroup, boolean paramBoolean) {}
    
    public void setElevation(View paramView, float paramFloat) {}
    
    public void setFitsSystemWindows(View paramView, boolean paramBoolean) {}
    
    public void setHasTransientState(View paramView, boolean paramBoolean) {}
    
    public void setImportantForAccessibility(View paramView, int paramInt) {}
    
    public void setLabelFor(View paramView, int paramInt) {}
    
    public void setLayerPaint(View paramView, Paint paramPaint) {}
    
    public void setLayerType(View paramView, int paramInt, Paint paramPaint) {}
    
    public void setLayoutDirection(View paramView, int paramInt) {}
    
    public void setOnApplyWindowInsetsListener(View paramView, OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener) {}
    
    public void setOverScrollMode(View paramView, int paramInt) {}
    
    public void setPaddingRelative(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramView.setPadding(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void setPivotX(View paramView, float paramFloat) {}
    
    public void setPivotY(View paramView, float paramFloat) {}
    
    public void setRotation(View paramView, float paramFloat) {}
    
    public void setRotationX(View paramView, float paramFloat) {}
    
    public void setRotationY(View paramView, float paramFloat) {}
    
    public void setSaveFromParentEnabled(View paramView, boolean paramBoolean) {}
    
    public void setScaleX(View paramView, float paramFloat) {}
    
    public void setScaleY(View paramView, float paramFloat) {}
    
    public void setTransitionName(View paramView, String paramString) {}
    
    public void setTranslationX(View paramView, float paramFloat) {}
    
    public void setTranslationY(View paramView, float paramFloat) {}
    
    public void setTranslationZ(View paramView, float paramFloat) {}
    
    public void setX(View paramView, float paramFloat) {}
    
    public void setY(View paramView, float paramFloat) {}
  }
  
  static class EclairMr1ViewCompatImpl
    extends ViewCompat.BaseViewCompatImpl
  {
    public boolean isOpaque(View paramView)
    {
      return ViewCompatEclairMr1.isOpaque(paramView);
    }
    
    public void setChildrenDrawingOrderEnabled(ViewGroup paramViewGroup, boolean paramBoolean)
    {
      ViewCompatEclairMr1.setChildrenDrawingOrderEnabled(paramViewGroup, paramBoolean);
    }
  }
  
  static class GBViewCompatImpl
    extends ViewCompat.EclairMr1ViewCompatImpl
  {
    public int getOverScrollMode(View paramView)
    {
      return ViewCompatGingerbread.getOverScrollMode(paramView);
    }
    
    public void setOverScrollMode(View paramView, int paramInt)
    {
      ViewCompatGingerbread.setOverScrollMode(paramView, paramInt);
    }
  }
  
  static class HCViewCompatImpl
    extends ViewCompat.GBViewCompatImpl
  {
    public float getAlpha(View paramView)
    {
      return ViewCompatHC.getAlpha(paramView);
    }
    
    long getFrameTime()
    {
      return ViewCompatHC.getFrameTime();
    }
    
    public int getLayerType(View paramView)
    {
      return ViewCompatHC.getLayerType(paramView);
    }
    
    public int getMeasuredHeightAndState(View paramView)
    {
      return ViewCompatHC.getMeasuredHeightAndState(paramView);
    }
    
    public int getMeasuredState(View paramView)
    {
      return ViewCompatHC.getMeasuredState(paramView);
    }
    
    public int getMeasuredWidthAndState(View paramView)
    {
      return ViewCompatHC.getMeasuredWidthAndState(paramView);
    }
    
    public float getPivotX(View paramView)
    {
      return ViewCompatHC.getPivotX(paramView);
    }
    
    public float getPivotY(View paramView)
    {
      return ViewCompatHC.getPivotY(paramView);
    }
    
    public float getRotation(View paramView)
    {
      return ViewCompatHC.getRotation(paramView);
    }
    
    public float getRotationX(View paramView)
    {
      return ViewCompatHC.getRotationX(paramView);
    }
    
    public float getRotationY(View paramView)
    {
      return ViewCompatHC.getRotationY(paramView);
    }
    
    public float getScaleX(View paramView)
    {
      return ViewCompatHC.getScaleX(paramView);
    }
    
    public float getScaleY(View paramView)
    {
      return ViewCompatHC.getScaleY(paramView);
    }
    
    public float getTranslationX(View paramView)
    {
      return ViewCompatHC.getTranslationX(paramView);
    }
    
    public float getTranslationY(View paramView)
    {
      return ViewCompatHC.getTranslationY(paramView);
    }
    
    public float getX(View paramView)
    {
      return ViewCompatHC.getX(paramView);
    }
    
    public float getY(View paramView)
    {
      return ViewCompatHC.getY(paramView);
    }
    
    public void jumpDrawablesToCurrentState(View paramView)
    {
      ViewCompatHC.jumpDrawablesToCurrentState(paramView);
    }
    
    public int resolveSizeAndState(int paramInt1, int paramInt2, int paramInt3)
    {
      return ViewCompatHC.resolveSizeAndState(paramInt1, paramInt2, paramInt3);
    }
    
    public void setActivated(View paramView, boolean paramBoolean)
    {
      ViewCompatHC.setActivated(paramView, paramBoolean);
    }
    
    public void setAlpha(View paramView, float paramFloat)
    {
      ViewCompatHC.setAlpha(paramView, paramFloat);
    }
    
    public void setLayerPaint(View paramView, Paint paramPaint)
    {
      setLayerType(paramView, getLayerType(paramView), paramPaint);
      paramView.invalidate();
    }
    
    public void setLayerType(View paramView, int paramInt, Paint paramPaint)
    {
      ViewCompatHC.setLayerType(paramView, paramInt, paramPaint);
    }
    
    public void setPivotX(View paramView, float paramFloat)
    {
      ViewCompatHC.setPivotX(paramView, paramFloat);
    }
    
    public void setPivotY(View paramView, float paramFloat)
    {
      ViewCompatHC.setPivotY(paramView, paramFloat);
    }
    
    public void setRotation(View paramView, float paramFloat)
    {
      ViewCompatHC.setRotation(paramView, paramFloat);
    }
    
    public void setRotationX(View paramView, float paramFloat)
    {
      ViewCompatHC.setRotationX(paramView, paramFloat);
    }
    
    public void setRotationY(View paramView, float paramFloat)
    {
      ViewCompatHC.setRotationY(paramView, paramFloat);
    }
    
    public void setSaveFromParentEnabled(View paramView, boolean paramBoolean)
    {
      ViewCompatHC.setSaveFromParentEnabled(paramView, paramBoolean);
    }
    
    public void setScaleX(View paramView, float paramFloat)
    {
      ViewCompatHC.setScaleX(paramView, paramFloat);
    }
    
    public void setScaleY(View paramView, float paramFloat)
    {
      ViewCompatHC.setScaleY(paramView, paramFloat);
    }
    
    public void setTranslationX(View paramView, float paramFloat)
    {
      ViewCompatHC.setTranslationX(paramView, paramFloat);
    }
    
    public void setTranslationY(View paramView, float paramFloat)
    {
      ViewCompatHC.setTranslationY(paramView, paramFloat);
    }
    
    public void setX(View paramView, float paramFloat)
    {
      ViewCompatHC.setX(paramView, paramFloat);
    }
    
    public void setY(View paramView, float paramFloat)
    {
      ViewCompatHC.setY(paramView, paramFloat);
    }
  }
  
  static class ICSViewCompatImpl
    extends ViewCompat.HCViewCompatImpl
  {
    static boolean accessibilityDelegateCheckFailed = false;
    static Field mAccessibilityDelegateField;
    
    public ViewPropertyAnimatorCompat animate(View paramView)
    {
      if (this.mViewPropertyAnimatorCompatMap != null) {}
      for (;;)
      {
        localViewPropertyAnimatorCompat = (ViewPropertyAnimatorCompat)this.mViewPropertyAnimatorCompatMap.get(paramView);
        if (localViewPropertyAnimatorCompat == null) {
          break;
        }
        return localViewPropertyAnimatorCompat;
        this.mViewPropertyAnimatorCompatMap = new WeakHashMap();
      }
      ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat = new ViewPropertyAnimatorCompat(paramView);
      this.mViewPropertyAnimatorCompatMap.put(paramView, localViewPropertyAnimatorCompat);
      return localViewPropertyAnimatorCompat;
    }
    
    public boolean canScrollHorizontally(View paramView, int paramInt)
    {
      return ViewCompatICS.canScrollHorizontally(paramView, paramInt);
    }
    
    public boolean canScrollVertically(View paramView, int paramInt)
    {
      return ViewCompatICS.canScrollVertically(paramView, paramInt);
    }
    
    public boolean hasAccessibilityDelegate(View paramView)
    {
      if ((accessibilityDelegateCheckFailed) || (mAccessibilityDelegateField != null)) {}
      try
      {
        for (;;)
        {
          paramView = mAccessibilityDelegateField.get(paramView);
          if (paramView == null)
          {
            return false;
            return false;
            try
            {
              mAccessibilityDelegateField = View.class.getDeclaredField("mAccessibilityDelegate");
              mAccessibilityDelegateField.setAccessible(true);
            }
            catch (Throwable paramView)
            {
              accessibilityDelegateCheckFailed = true;
              return false;
            }
          }
        }
        return true;
      }
      catch (Throwable paramView)
      {
        accessibilityDelegateCheckFailed = true;
      }
      return false;
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      ViewCompatICS.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      ViewCompatICS.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat.getInfo());
    }
    
    public void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      ViewCompatICS.onPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
    }
    
    public void setAccessibilityDelegate(View paramView, @Nullable AccessibilityDelegateCompat paramAccessibilityDelegateCompat)
    {
      Object localObject = null;
      if (paramAccessibilityDelegateCompat != null) {
        localObject = paramAccessibilityDelegateCompat.getBridge();
      }
      ViewCompatICS.setAccessibilityDelegate(paramView, localObject);
    }
    
    public void setFitsSystemWindows(View paramView, boolean paramBoolean)
    {
      ViewCompatICS.setFitsSystemWindows(paramView, paramBoolean);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L, 4L})
  private static @interface ImportantForAccessibility {}
  
  static class JBViewCompatImpl
    extends ViewCompat.ICSViewCompatImpl
  {
    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View paramView)
    {
      paramView = ViewCompatJB.getAccessibilityNodeProvider(paramView);
      if (paramView == null) {
        return null;
      }
      return new AccessibilityNodeProviderCompat(paramView);
    }
    
    public boolean getFitsSystemWindows(View paramView)
    {
      return ViewCompatJB.getFitsSystemWindows(paramView);
    }
    
    public int getImportantForAccessibility(View paramView)
    {
      return ViewCompatJB.getImportantForAccessibility(paramView);
    }
    
    public int getMinimumHeight(View paramView)
    {
      return ViewCompatJB.getMinimumHeight(paramView);
    }
    
    public int getMinimumWidth(View paramView)
    {
      return ViewCompatJB.getMinimumWidth(paramView);
    }
    
    public ViewParent getParentForAccessibility(View paramView)
    {
      return ViewCompatJB.getParentForAccessibility(paramView);
    }
    
    public boolean hasTransientState(View paramView)
    {
      return ViewCompatJB.hasTransientState(paramView);
    }
    
    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      return ViewCompatJB.performAccessibilityAction(paramView, paramInt, paramBundle);
    }
    
    public void postInvalidateOnAnimation(View paramView)
    {
      ViewCompatJB.postInvalidateOnAnimation(paramView);
    }
    
    public void postInvalidateOnAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      ViewCompatJB.postInvalidateOnAnimation(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void postOnAnimation(View paramView, Runnable paramRunnable)
    {
      ViewCompatJB.postOnAnimation(paramView, paramRunnable);
    }
    
    public void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong)
    {
      ViewCompatJB.postOnAnimationDelayed(paramView, paramRunnable, paramLong);
    }
    
    public void requestApplyInsets(View paramView)
    {
      ViewCompatJB.requestApplyInsets(paramView);
    }
    
    public void setHasTransientState(View paramView, boolean paramBoolean)
    {
      ViewCompatJB.setHasTransientState(paramView, paramBoolean);
    }
    
    public void setImportantForAccessibility(View paramView, int paramInt)
    {
      if (paramInt != 4) {}
      for (;;)
      {
        ViewCompatJB.setImportantForAccessibility(paramView, paramInt);
        return;
        paramInt = 2;
      }
    }
  }
  
  static class JbMr1ViewCompatImpl
    extends ViewCompat.JBViewCompatImpl
  {
    public int getLabelFor(View paramView)
    {
      return ViewCompatJellybeanMr1.getLabelFor(paramView);
    }
    
    public int getLayoutDirection(View paramView)
    {
      return ViewCompatJellybeanMr1.getLayoutDirection(paramView);
    }
    
    public int getPaddingEnd(View paramView)
    {
      return ViewCompatJellybeanMr1.getPaddingEnd(paramView);
    }
    
    public int getPaddingStart(View paramView)
    {
      return ViewCompatJellybeanMr1.getPaddingStart(paramView);
    }
    
    public int getWindowSystemUiVisibility(View paramView)
    {
      return ViewCompatJellybeanMr1.getWindowSystemUiVisibility(paramView);
    }
    
    public void setLabelFor(View paramView, int paramInt)
    {
      ViewCompatJellybeanMr1.setLabelFor(paramView, paramInt);
    }
    
    public void setLayerPaint(View paramView, Paint paramPaint)
    {
      ViewCompatJellybeanMr1.setLayerPaint(paramView, paramPaint);
    }
    
    public void setLayoutDirection(View paramView, int paramInt)
    {
      ViewCompatJellybeanMr1.setLayoutDirection(paramView, paramInt);
    }
    
    public void setPaddingRelative(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      ViewCompatJellybeanMr1.setPaddingRelative(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  static class KitKatViewCompatImpl
    extends ViewCompat.JbMr1ViewCompatImpl
  {
    public int getAccessibilityLiveRegion(View paramView)
    {
      return ViewCompatKitKat.getAccessibilityLiveRegion(paramView);
    }
    
    public void setAccessibilityLiveRegion(View paramView, int paramInt)
    {
      ViewCompatKitKat.setAccessibilityLiveRegion(paramView, paramInt);
    }
    
    public void setImportantForAccessibility(View paramView, int paramInt)
    {
      ViewCompatJB.setImportantForAccessibility(paramView, paramInt);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L})
  private static @interface LayerType {}
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L, 3L})
  private static @interface LayoutDirectionMode {}
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 1L})
  private static @interface OverScroll {}
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L})
  private static @interface ResolvedLayoutDirectionMode {}
  
  static abstract interface ViewCompatImpl
  {
    public abstract ViewPropertyAnimatorCompat animate(View paramView);
    
    public abstract boolean canScrollHorizontally(View paramView, int paramInt);
    
    public abstract boolean canScrollVertically(View paramView, int paramInt);
    
    public abstract void dispatchFinishTemporaryDetach(View paramView);
    
    public abstract void dispatchStartTemporaryDetach(View paramView);
    
    public abstract int getAccessibilityLiveRegion(View paramView);
    
    public abstract AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View paramView);
    
    public abstract float getAlpha(View paramView);
    
    public abstract float getElevation(View paramView);
    
    public abstract boolean getFitsSystemWindows(View paramView);
    
    public abstract int getImportantForAccessibility(View paramView);
    
    public abstract int getLabelFor(View paramView);
    
    public abstract int getLayerType(View paramView);
    
    public abstract int getLayoutDirection(View paramView);
    
    public abstract int getMeasuredHeightAndState(View paramView);
    
    public abstract int getMeasuredState(View paramView);
    
    public abstract int getMeasuredWidthAndState(View paramView);
    
    public abstract int getMinimumHeight(View paramView);
    
    public abstract int getMinimumWidth(View paramView);
    
    public abstract int getOverScrollMode(View paramView);
    
    public abstract int getPaddingEnd(View paramView);
    
    public abstract int getPaddingStart(View paramView);
    
    public abstract ViewParent getParentForAccessibility(View paramView);
    
    public abstract float getPivotX(View paramView);
    
    public abstract float getPivotY(View paramView);
    
    public abstract float getRotation(View paramView);
    
    public abstract float getRotationX(View paramView);
    
    public abstract float getRotationY(View paramView);
    
    public abstract float getScaleX(View paramView);
    
    public abstract float getScaleY(View paramView);
    
    public abstract String getTransitionName(View paramView);
    
    public abstract float getTranslationX(View paramView);
    
    public abstract float getTranslationY(View paramView);
    
    public abstract float getTranslationZ(View paramView);
    
    public abstract int getWindowSystemUiVisibility(View paramView);
    
    public abstract float getX(View paramView);
    
    public abstract float getY(View paramView);
    
    public abstract boolean hasAccessibilityDelegate(View paramView);
    
    public abstract boolean hasTransientState(View paramView);
    
    public abstract boolean isImportantForAccessibility(View paramView);
    
    public abstract boolean isOpaque(View paramView);
    
    public abstract void jumpDrawablesToCurrentState(View paramView);
    
    public abstract void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent);
    
    public abstract void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat);
    
    public abstract void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent);
    
    public abstract boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle);
    
    public abstract void postInvalidateOnAnimation(View paramView);
    
    public abstract void postInvalidateOnAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract void postOnAnimation(View paramView, Runnable paramRunnable);
    
    public abstract void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong);
    
    public abstract void requestApplyInsets(View paramView);
    
    public abstract int resolveSizeAndState(int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void setAccessibilityDelegate(View paramView, @Nullable AccessibilityDelegateCompat paramAccessibilityDelegateCompat);
    
    public abstract void setAccessibilityLiveRegion(View paramView, int paramInt);
    
    public abstract void setActivated(View paramView, boolean paramBoolean);
    
    public abstract void setAlpha(View paramView, float paramFloat);
    
    public abstract void setChildrenDrawingOrderEnabled(ViewGroup paramViewGroup, boolean paramBoolean);
    
    public abstract void setElevation(View paramView, float paramFloat);
    
    public abstract void setFitsSystemWindows(View paramView, boolean paramBoolean);
    
    public abstract void setHasTransientState(View paramView, boolean paramBoolean);
    
    public abstract void setImportantForAccessibility(View paramView, int paramInt);
    
    public abstract void setLabelFor(View paramView, int paramInt);
    
    public abstract void setLayerPaint(View paramView, Paint paramPaint);
    
    public abstract void setLayerType(View paramView, int paramInt, Paint paramPaint);
    
    public abstract void setLayoutDirection(View paramView, int paramInt);
    
    public abstract void setOnApplyWindowInsetsListener(View paramView, OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener);
    
    public abstract void setOverScrollMode(View paramView, int paramInt);
    
    public abstract void setPaddingRelative(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract void setPivotX(View paramView, float paramFloat);
    
    public abstract void setPivotY(View paramView, float paramFloat);
    
    public abstract void setRotation(View paramView, float paramFloat);
    
    public abstract void setRotationX(View paramView, float paramFloat);
    
    public abstract void setRotationY(View paramView, float paramFloat);
    
    public abstract void setSaveFromParentEnabled(View paramView, boolean paramBoolean);
    
    public abstract void setScaleX(View paramView, float paramFloat);
    
    public abstract void setScaleY(View paramView, float paramFloat);
    
    public abstract void setTransitionName(View paramView, String paramString);
    
    public abstract void setTranslationX(View paramView, float paramFloat);
    
    public abstract void setTranslationY(View paramView, float paramFloat);
    
    public abstract void setTranslationZ(View paramView, float paramFloat);
    
    public abstract void setX(View paramView, float paramFloat);
    
    public abstract void setY(View paramView, float paramFloat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/view/ViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */