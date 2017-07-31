package android.support.v4.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewParentCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class ExploreByTouchHelper
  extends AccessibilityDelegateCompat
{
  private static final String DEFAULT_CLASS_NAME = View.class.getName();
  public static final int INVALID_ID = Integer.MIN_VALUE;
  private int mFocusedVirtualViewId = Integer.MIN_VALUE;
  private int mHoveredVirtualViewId = Integer.MIN_VALUE;
  private final AccessibilityManager mManager;
  private ExploreByTouchNodeProvider mNodeProvider;
  private final int[] mTempGlobalRect = new int[2];
  private final Rect mTempParentRect = new Rect();
  private final Rect mTempScreenRect = new Rect();
  private final Rect mTempVisibleRect = new Rect();
  private final View mView;
  
  public ExploreByTouchHelper(View paramView)
  {
    if (paramView != null)
    {
      this.mView = paramView;
      this.mManager = ((AccessibilityManager)paramView.getContext().getSystemService("accessibility"));
      return;
    }
    throw new IllegalArgumentException("View may not be null");
  }
  
  private boolean clearAccessibilityFocus(int paramInt)
  {
    if (!isAccessibilityFocused(paramInt)) {
      return false;
    }
    this.mFocusedVirtualViewId = Integer.MIN_VALUE;
    this.mView.invalidate();
    sendEventForVirtualView(paramInt, 65536);
    return true;
  }
  
  private AccessibilityEvent createEvent(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      return createEventForChild(paramInt1, paramInt2);
    }
    return createEventForHost(paramInt2);
  }
  
  private AccessibilityEvent createEventForChild(int paramInt1, int paramInt2)
  {
    AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(paramInt2);
    localAccessibilityEvent.setEnabled(true);
    localAccessibilityEvent.setClassName(DEFAULT_CLASS_NAME);
    onPopulateEventForVirtualView(paramInt1, localAccessibilityEvent);
    if (!localAccessibilityEvent.getText().isEmpty()) {}
    while (localAccessibilityEvent.getContentDescription() != null)
    {
      localAccessibilityEvent.setPackageName(this.mView.getContext().getPackageName());
      AccessibilityEventCompat.asRecord(localAccessibilityEvent).setSource(this.mView, paramInt1);
      return localAccessibilityEvent;
    }
    throw new RuntimeException("Callbacks must add text or a content description in populateEventForVirtualViewId()");
  }
  
  private AccessibilityEvent createEventForHost(int paramInt)
  {
    AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(paramInt);
    ViewCompat.onInitializeAccessibilityEvent(this.mView, localAccessibilityEvent);
    return localAccessibilityEvent;
  }
  
  private AccessibilityNodeInfoCompat createNode(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return createNodeForChild(paramInt);
    }
    return createNodeForHost();
  }
  
  private AccessibilityNodeInfoCompat createNodeForChild(int paramInt)
  {
    AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain();
    localAccessibilityNodeInfoCompat.setEnabled(true);
    localAccessibilityNodeInfoCompat.setClassName(DEFAULT_CLASS_NAME);
    onPopulateNodeForVirtualView(paramInt, localAccessibilityNodeInfoCompat);
    int i;
    if (localAccessibilityNodeInfoCompat.getText() != null)
    {
      localAccessibilityNodeInfoCompat.getBoundsInParent(this.mTempParentRect);
      if (this.mTempParentRect.isEmpty()) {
        break label200;
      }
      i = localAccessibilityNodeInfoCompat.getActions();
      if ((i & 0x40) != 0) {
        break label210;
      }
      if ((i & 0x80) != 0) {
        break label220;
      }
      localAccessibilityNodeInfoCompat.setPackageName(this.mView.getContext().getPackageName());
      localAccessibilityNodeInfoCompat.setSource(this.mView, paramInt);
      localAccessibilityNodeInfoCompat.setParent(this.mView);
      if (this.mFocusedVirtualViewId == paramInt) {
        break label230;
      }
      localAccessibilityNodeInfoCompat.setAccessibilityFocused(false);
      localAccessibilityNodeInfoCompat.addAction(64);
      label117:
      if (intersectVisibleToUser(this.mTempParentRect)) {
        break label245;
      }
    }
    for (;;)
    {
      this.mView.getLocationOnScreen(this.mTempGlobalRect);
      paramInt = this.mTempGlobalRect[0];
      i = this.mTempGlobalRect[1];
      this.mTempScreenRect.set(this.mTempParentRect);
      this.mTempScreenRect.offset(paramInt, i);
      localAccessibilityNodeInfoCompat.setBoundsInScreen(this.mTempScreenRect);
      return localAccessibilityNodeInfoCompat;
      if (localAccessibilityNodeInfoCompat.getContentDescription() != null) {
        break;
      }
      throw new RuntimeException("Callbacks must add text or a content description in populateNodeForVirtualViewId()");
      label200:
      throw new RuntimeException("Callbacks must set parent bounds in populateNodeForVirtualViewId()");
      label210:
      throw new RuntimeException("Callbacks must not add ACTION_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
      label220:
      throw new RuntimeException("Callbacks must not add ACTION_CLEAR_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
      label230:
      localAccessibilityNodeInfoCompat.setAccessibilityFocused(true);
      localAccessibilityNodeInfoCompat.addAction(128);
      break label117;
      label245:
      localAccessibilityNodeInfoCompat.setVisibleToUser(true);
      localAccessibilityNodeInfoCompat.setBoundsInParent(this.mTempParentRect);
    }
  }
  
  private AccessibilityNodeInfoCompat createNodeForHost()
  {
    AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain(this.mView);
    ViewCompat.onInitializeAccessibilityNodeInfo(this.mView, localAccessibilityNodeInfoCompat);
    Object localObject = new LinkedList();
    getVisibleVirtualViews((List)localObject);
    localObject = ((LinkedList)localObject).iterator();
    for (;;)
    {
      if (!((Iterator)localObject).hasNext()) {
        return localAccessibilityNodeInfoCompat;
      }
      Integer localInteger = (Integer)((Iterator)localObject).next();
      localAccessibilityNodeInfoCompat.addChild(this.mView, localInteger.intValue());
    }
  }
  
  private boolean intersectVisibleToUser(Rect paramRect)
  {
    if (paramRect == null) {}
    while (paramRect.isEmpty()) {
      return false;
    }
    if (this.mView.getWindowVisibility() == 0)
    {
      localObject = this.mView.getParent();
      if (!(localObject instanceof View))
      {
        if (localObject == null) {
          break label109;
        }
        if (!this.mView.getLocalVisibleRect(this.mTempVisibleRect)) {
          break label111;
        }
        return paramRect.intersect(this.mTempVisibleRect);
      }
    }
    else
    {
      return false;
    }
    Object localObject = (View)localObject;
    if (ViewCompat.getAlpha((View)localObject) <= 0.0F) {}
    for (int i = 1;; i = 0)
    {
      if ((i != 0) || (((View)localObject).getVisibility() != 0)) {
        break label107;
      }
      localObject = ((View)localObject).getParent();
      break;
    }
    label107:
    return false;
    label109:
    return false;
    label111:
    return false;
  }
  
  private boolean isAccessibilityFocused(int paramInt)
  {
    return this.mFocusedVirtualViewId == paramInt;
  }
  
  private boolean manageFocusForChild(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    switch (paramInt2)
    {
    default: 
      return false;
    case 64: 
      return requestAccessibilityFocus(paramInt1);
    }
    return clearAccessibilityFocus(paramInt1);
  }
  
  private boolean performAction(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    switch (paramInt1)
    {
    default: 
      return performActionForChild(paramInt1, paramInt2, paramBundle);
    }
    return performActionForHost(paramInt2, paramBundle);
  }
  
  private boolean performActionForChild(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    switch (paramInt2)
    {
    default: 
      return onPerformActionForVirtualView(paramInt1, paramInt2, paramBundle);
    }
    return manageFocusForChild(paramInt1, paramInt2, paramBundle);
  }
  
  private boolean performActionForHost(int paramInt, Bundle paramBundle)
  {
    return ViewCompat.performAccessibilityAction(this.mView, paramInt, paramBundle);
  }
  
  private boolean requestAccessibilityFocus(int paramInt)
  {
    if (!this.mManager.isEnabled()) {}
    while (!AccessibilityManagerCompat.isTouchExplorationEnabled(this.mManager)) {
      return false;
    }
    if (isAccessibilityFocused(paramInt)) {
      return false;
    }
    this.mFocusedVirtualViewId = paramInt;
    this.mView.invalidate();
    sendEventForVirtualView(paramInt, 32768);
    return true;
  }
  
  private void updateHoveredVirtualView(int paramInt)
  {
    if (this.mHoveredVirtualViewId != paramInt)
    {
      int i = this.mHoveredVirtualViewId;
      this.mHoveredVirtualViewId = paramInt;
      sendEventForVirtualView(paramInt, 128);
      sendEventForVirtualView(i, 256);
      return;
    }
  }
  
  public boolean dispatchHoverEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mManager.isEnabled()) {}
    while (!AccessibilityManagerCompat.isTouchExplorationEnabled(this.mManager)) {
      return false;
    }
    switch (paramMotionEvent.getAction())
    {
    case 8: 
    default: 
      return false;
    case 7: 
    case 9: 
      int i = getVirtualViewAt(paramMotionEvent.getX(), paramMotionEvent.getY());
      updateHoveredVirtualView(i);
      return i != Integer.MIN_VALUE;
    }
    if (this.mFocusedVirtualViewId == Integer.MIN_VALUE) {
      return false;
    }
    updateHoveredVirtualView(Integer.MIN_VALUE);
    return true;
  }
  
  public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View paramView)
  {
    if (this.mNodeProvider != null) {}
    for (;;)
    {
      return this.mNodeProvider;
      this.mNodeProvider = new ExploreByTouchNodeProvider(null);
    }
  }
  
  public int getFocusedVirtualView()
  {
    return this.mFocusedVirtualViewId;
  }
  
  protected abstract int getVirtualViewAt(float paramFloat1, float paramFloat2);
  
  protected abstract void getVisibleVirtualViews(List<Integer> paramList);
  
  public void invalidateRoot()
  {
    invalidateVirtualView(-1);
  }
  
  public void invalidateVirtualView(int paramInt)
  {
    sendEventForVirtualView(paramInt, 2048);
  }
  
  protected abstract boolean onPerformActionForVirtualView(int paramInt1, int paramInt2, Bundle paramBundle);
  
  protected abstract void onPopulateEventForVirtualView(int paramInt, AccessibilityEvent paramAccessibilityEvent);
  
  protected abstract void onPopulateNodeForVirtualView(int paramInt, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat);
  
  public boolean sendEventForVirtualView(int paramInt1, int paramInt2)
  {
    if (paramInt1 == Integer.MIN_VALUE) {}
    while (!this.mManager.isEnabled()) {
      return false;
    }
    ViewParent localViewParent = this.mView.getParent();
    if (localViewParent != null)
    {
      AccessibilityEvent localAccessibilityEvent = createEvent(paramInt1, paramInt2);
      return ViewParentCompat.requestSendAccessibilityEvent(localViewParent, this.mView, localAccessibilityEvent);
    }
    return false;
  }
  
  private class ExploreByTouchNodeProvider
    extends AccessibilityNodeProviderCompat
  {
    private ExploreByTouchNodeProvider() {}
    
    public AccessibilityNodeInfoCompat createAccessibilityNodeInfo(int paramInt)
    {
      return ExploreByTouchHelper.this.createNode(paramInt);
    }
    
    public boolean performAction(int paramInt1, int paramInt2, Bundle paramBundle)
    {
      return ExploreByTouchHelper.this.performAction(paramInt1, paramInt2, paramBundle);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/widget/ExploreByTouchHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */