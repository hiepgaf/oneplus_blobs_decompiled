package com.oneplus.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import com.oneplus.base.Log;
import java.util.ArrayList;
import java.util.List;

public class FilmstripView
  extends ViewGroup
{
  private static final long DURATION_ITEM_ADD_ANIMATION = 500L;
  private static final long DURATION_ITEM_REMOVE_ANIMATION = 300L;
  private static final long DURATION_ITEM_REMOVE_ANIMATION_FAST = 100L;
  private static final long DURATION_SCROLL_TO_ITEM = 600L;
  private static final float FLY_ACCELERATION = -8000.0F;
  private static final long INTERVAL_UPDATE_ITEMS_LAYOUT = 15L;
  private static final float MIN_SCROLL_TO_ITEM_OFFSET = 5.0F;
  private static final int MSG_FAST_LAYOUT = 10010;
  private static final int MSG_FLY = 10001;
  private static final int MSG_SCROLL_TO_ITEM = 10002;
  private static final int MSG_UPDATE_ITEMS_LAYOUT = 10000;
  private static final boolean PRINT_TRACE_LOGS = false;
  public static final int SCROLL_MODE_DISABLED = -1;
  public static final int SCROLL_MODE_MULTIPLE_ITEMS = 1;
  public static final int SCROLL_MODE_SINGLE_ITEM = 0;
  private static final String TAG = FilmstripView.class.getSimpleName();
  private static final int THRESHOLD_MOVE_TO_NEIGHBOR_ITEM = 500;
  private ItemInfo m_ActiveItemInfoHead;
  private ItemInfo m_ActiveItemInfoTail;
  private Adapter m_Adapter;
  private ItemInfo m_AnchorItemInfo;
  private int m_FastLayoutCounter;
  private ItemInfo m_FreeItemInfos;
  private final GestureDetector m_GestureDetector;
  private final GestureDetector.OnGestureListener m_GestureListener = new GestureDetector.OnGestureListener()
  {
    public boolean onDown(MotionEvent paramAnonymousMotionEvent)
    {
      FilmstripView.-wrap2(FilmstripView.this, paramAnonymousMotionEvent);
      return false;
    }
    
    public boolean onFling(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2)
    {
      FilmstripView.-wrap3(FilmstripView.this, paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, paramAnonymousFloat1, paramAnonymousFloat2);
      return false;
    }
    
    public void onLongPress(MotionEvent paramAnonymousMotionEvent) {}
    
    public boolean onScroll(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2)
    {
      FilmstripView.-wrap4(FilmstripView.this, paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, paramAnonymousFloat1, paramAnonymousFloat2);
      return false;
    }
    
    public void onShowPress(MotionEvent paramAnonymousMotionEvent) {}
    
    public boolean onSingleTapUp(MotionEvent paramAnonymousMotionEvent)
    {
      return false;
    }
  };
  private Handler m_Handler;
  private boolean m_HasMultiPointers;
  private int m_Height;
  private boolean m_IsFlying;
  private boolean m_IsOverScrolled;
  private Boolean m_IsScrollLeftRight;
  private boolean m_IsScrolling;
  private int m_ItemMargin = 50;
  private int m_LastPosition;
  private View.OnTouchListener m_OnTouchListener;
  private int m_ReportedCurrentPosition = -1;
  private ScrollListener m_ScrollListener;
  private int m_ScrollMode = 0;
  private long m_ScrollToItemStartTime;
  private float m_TotalScrollDistanceX;
  private int m_Width;
  
  public FilmstripView(Context paramContext)
  {
    super(paramContext);
    setupHandler();
    this.m_GestureDetector = new GestureDetector(paramContext, this.m_GestureListener);
  }
  
  public FilmstripView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setupHandler();
    this.m_GestureDetector = new GestureDetector(paramContext, this.m_GestureListener);
  }
  
  private int calculateItemDefaultLeft(ItemInfo paramItemInfo, boolean paramBoolean)
  {
    int j = this.m_Width / 2;
    if (paramBoolean) {}
    for (int i = paramItemInfo.targetWidth;; i = paramItemInfo.width) {
      return j - i / 2;
    }
  }
  
  private void clearItems()
  {
    ItemInfo localItemInfo;
    for (Object localObject = this.m_ActiveItemInfoHead; localObject != null; localObject = localItemInfo)
    {
      localItemInfo = ((ItemInfo)localObject).next;
      releaseItem((ItemInfo)localObject);
    }
    this.m_ActiveItemInfoHead = null;
    this.m_ActiveItemInfoTail = null;
    this.m_AnchorItemInfo = null;
    this.m_IsOverScrolled = false;
  }
  
  private void fastLayout()
  {
    for (ItemInfo localItemInfo = this.m_ActiveItemInfoHead; localItemInfo != null; localItemInfo = localItemInfo.next) {
      localItemInfo.layout(this.m_Width, this.m_Height, true, true);
    }
  }
  
  private ItemInfo findFirstVisibleItemInfo()
  {
    return this.m_ActiveItemInfoHead;
  }
  
  private ItemInfo findItemInfo(float paramFloat1, float paramFloat2)
  {
    if ((paramFloat2 >= 0.0F) && (paramFloat2 < this.m_Height))
    {
      paramFloat2 = this.m_ItemMargin / 2;
      for (ItemInfo localItemInfo = this.m_ActiveItemInfoHead; localItemInfo != null; localItemInfo = localItemInfo.next) {
        if ((paramFloat1 >= localItemInfo.left - paramFloat2) && (paramFloat1 < localItemInfo.left + localItemInfo.width + paramFloat2)) {
          return localItemInfo;
        }
      }
    }
    return null;
  }
  
  private ItemInfo findItemInfo(int paramInt)
  {
    return findItemInfo(paramInt, false);
  }
  
  private ItemInfo findItemInfo(int paramInt, boolean paramBoolean)
  {
    for (ItemInfo localItemInfo = this.m_ActiveItemInfoHead; localItemInfo != null; localItemInfo = localItemInfo.next) {
      if ((localItemInfo.position == paramInt) && ((!localItemInfo.isRemoving) || (paramBoolean))) {
        return localItemInfo;
      }
    }
    return null;
  }
  
  private ItemInfo findLastVisibleItemInfo()
  {
    return this.m_ActiveItemInfoTail;
  }
  
  private ItemInfo findNextNormalItemInfo(ItemInfo paramItemInfo)
  {
    if (paramItemInfo != null)
    {
      for (paramItemInfo = paramItemInfo.next; (paramItemInfo != null) && (paramItemInfo.isRemoving); paramItemInfo = paramItemInfo.next) {}
      return paramItemInfo;
    }
    return null;
  }
  
  private ItemInfo findPreviousNormalItemInfo(ItemInfo paramItemInfo)
  {
    if (paramItemInfo != null)
    {
      for (paramItemInfo = paramItemInfo.previous; (paramItemInfo != null) && (paramItemInfo.isRemoving); paramItemInfo = paramItemInfo.previous) {}
      return paramItemInfo;
    }
    return null;
  }
  
  private void fly(float paramFloat, long paramLong)
  {
    if (!this.m_IsFlying) {
      return;
    }
    long l = SystemClock.elapsedRealtime();
    float f = (float)(l - paramLong) / 1000.0F;
    int j;
    int i;
    if ((scrollBy(Math.round(paramFloat * f + -8000.0F * f * f * 0.5F)) != 0.0F) || (paramFloat > 0.0F))
    {
      paramFloat = Math.max(0.0F, -8000.0F * f + paramFloat);
      j = getCurrentItem();
      if (this.m_Adapter == null) {
        break label147;
      }
      i = this.m_Adapter.getCount();
      label92:
      if ((Math.abs(paramFloat) > 0.001F) && ((paramFloat <= 0.0F) || (j > 0))) {
        break label153;
      }
    }
    label147:
    label153:
    while ((paramFloat < 0.0F) && (j >= i - 1))
    {
      this.m_IsFlying = false;
      if (j >= 0) {
        scrollToItem(j, true);
      }
      return;
      paramFloat = Math.min(0.0F, paramFloat - -8000.0F * f);
      break;
      i = 0;
      break label92;
    }
    this.m_Handler.sendMessageDelayed(Message.obtain(this.m_Handler, 10001, new Object[] { Float.valueOf(paramFloat), Long.valueOf(l) }), 10L);
  }
  
  private ItemInfo getCurrentItemInfo()
  {
    float f4 = this.m_Width / 2.0F;
    float f1 = this.m_Width;
    Object localObject = null;
    ItemInfo localItemInfo = this.m_ActiveItemInfoHead;
    while (localItemInfo != null)
    {
      float f3 = Math.abs(f4 - (localItemInfo.left + localItemInfo.width / 2.0F));
      float f2;
      if (localObject != null)
      {
        f2 = f1;
        if (f1 <= f3) {}
      }
      else
      {
        localObject = localItemInfo;
        f2 = f3;
      }
      localItemInfo = localItemInfo.next;
      f1 = f2;
    }
    return (ItemInfo)localObject;
  }
  
  private void handleMessage(Message paramMessage)
  {
    boolean bool = false;
    switch (paramMessage.what)
    {
    default: 
      return;
    case 10010: 
      fastLayout();
      return;
    case 10001: 
      paramMessage = (Object[])paramMessage.obj;
      fly(((Float)paramMessage[0]).floatValue(), ((Long)paramMessage[1]).longValue());
      return;
    case 10002: 
      int i = paramMessage.arg1;
      if (paramMessage.arg2 != 0) {
        bool = true;
      }
      scrollToItem(i, bool);
      return;
    }
    if ((paramMessage.obj instanceof ItemInfo))
    {
      updateItemsLayout((ItemInfo)paramMessage.obj, true);
      return;
    }
    if ((paramMessage.obj instanceof Integer))
    {
      updateItemsLayout(((Integer)paramMessage.obj).intValue(), true);
      return;
    }
    updateItemsLayout(true);
  }
  
  private ItemInfo obtainItemInfo(int paramInt)
  {
    ItemInfo localItemInfo = this.m_FreeItemInfos;
    if (localItemInfo != null)
    {
      this.m_FreeItemInfos = localItemInfo.next;
      localItemInfo.remove();
      localItemInfo.isRemoving = false;
      localItemInfo.container.setAlpha(1.0F);
    }
    for (;;)
    {
      localItemInfo.position = paramInt;
      return localItemInfo;
      localItemInfo = new ItemInfo(null);
      localItemInfo.container = new ItemContainerView(getContext(), localItemInfo);
    }
  }
  
  private void onDataSetChanged()
  {
    refreshItems(true);
  }
  
  private void onGestureDown(MotionEvent paramMotionEvent)
  {
    stopAutoScroll();
    this.m_TotalScrollDistanceX = 0.0F;
    this.m_AnchorItemInfo = findItemInfo(paramMotionEvent.getX(), paramMotionEvent.getY());
    this.m_LastPosition = getCurrentItem();
    this.m_IsScrollLeftRight = null;
  }
  
  private void onGestureFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    int i = 0;
    if (this.m_HasMultiPointers) {
      return;
    }
    switch (this.m_ScrollMode)
    {
    }
    label71:
    label125:
    label127:
    do
    {
      do
      {
        return;
        if (this.m_Adapter != null) {
          i = this.m_Adapter.getCount();
        }
      } while (i <= 0);
      int j;
      if (this.m_LastPosition >= 0)
      {
        j = this.m_LastPosition;
        if (Math.abs(paramFloat1) < 500.0F) {
          break label125;
        }
        if (paramFloat1 <= 0.0F) {
          break label127;
        }
      }
      for (int k = -1;; k = 1)
      {
        j += k;
        if ((j < 0) || (j >= i)) {
          break;
        }
        scrollToItem(j, true);
        return;
        j = getCurrentItem();
        break label71;
        break;
      }
    } while ((this.m_Adapter == null) || ((this.m_ActiveItemInfoHead != null) && (this.m_ActiveItemInfoHead.position == 0) && (paramFloat1 > 0.0F) && (getCurrentItemInfo() == this.m_ActiveItemInfoHead)) || ((this.m_ActiveItemInfoTail != null) && (this.m_ActiveItemInfoTail.position >= this.m_Adapter.getCount()) && (paramFloat1 < 0.0F) && (getCurrentItemInfo() == this.m_ActiveItemInfoTail)));
    startFly(paramFloat1);
  }
  
  private void onGestureScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    if (this.m_HasMultiPointers) {
      return;
    }
    if (this.m_ScrollMode == -1) {
      return;
    }
    if (this.m_IsScrollLeftRight == null) {
      if (Math.abs(paramFloat1) <= Math.abs(paramFloat2)) {
        break label56;
      }
    }
    label56:
    for (this.m_IsScrollLeftRight = Boolean.valueOf(true); !this.m_IsScrollLeftRight.booleanValue(); this.m_IsScrollLeftRight = Boolean.valueOf(false)) {
      return;
    }
    this.m_TotalScrollDistanceX += paramFloat1;
    if ((!this.m_IsScrolling) && (Math.abs(this.m_TotalScrollDistanceX) >= 50.0F))
    {
      this.m_IsScrolling = true;
      if ((this.m_ScrollListener != null) && (!this.m_ScrollListener.isScrollStartedCalled)) {
        break label132;
      }
    }
    for (;;)
    {
      scrollBy(Math.round(-paramFloat1));
      return;
      label132:
      this.m_ScrollListener.isScrollStartedCalled = true;
      this.m_ScrollListener.onScrollStarted();
    }
  }
  
  private void onGestureUp(MotionEvent paramMotionEvent)
  {
    this.m_IsScrolling = false;
    this.m_IsOverScrolled = false;
    this.m_AnchorItemInfo = null;
    if ((this.m_IsFlying) || (this.m_Handler.hasMessages(10002))) {}
    int i;
    do
    {
      return;
      i = getCurrentItem();
    } while (i < 0);
    scrollToItem(i, true);
  }
  
  private void onItemAdded(int paramInt1, int paramInt2)
  {
    if (this.m_Adapter == null) {
      return;
    }
    int i = this.m_Adapter.getCount();
    if ((paramInt1 < 0) || (paramInt1 >= i)) {}
    while (paramInt2 < paramInt1) {
      return;
    }
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject1 = this.m_ActiveItemInfoHead;
    while (localObject1 != null)
    {
      if (((ItemInfo)localObject1).position >= paramInt1) {
        ((ItemInfo)localObject1).position += paramInt2 - paramInt1 + 1;
      }
      localObject3 = localObject2;
      if (localObject2 == null) {
        localObject3 = localObject1;
      }
      localObject4 = localObject1;
      localObject1 = ((ItemInfo)localObject1).next;
      localObject2 = localObject3;
      localObject3 = localObject4;
    }
    Object localObject4 = null;
    int j = 1;
    if ((localObject2 != null) && (((ItemInfo)localObject2).position > paramInt2))
    {
      localObject1 = localObject2;
      i = 0;
    }
    while (localObject1 == null)
    {
      localObject1 = prepareItem(0);
      ((ItemInfo)localObject1).container.setAlpha(0.0F);
      ((ItemInfo)localObject1).container.animate().alpha(1.0F).setDuration(500L).start();
      this.m_ActiveItemInfoHead = ((ItemInfo)localObject1);
      this.m_ActiveItemInfoTail = ((ItemInfo)localObject1);
      if (!this.m_Handler.hasMessages(10000)) {
        updateItemsLayout(0, false);
      }
      return;
      if ((localObject3 != null) && (((ItemInfo)localObject3).position < paramInt1))
      {
        localObject1 = localObject3;
        i = j;
      }
      else
      {
        i = j;
        localObject1 = localObject4;
        if (localObject2 != null) {
          for (localObject2 = ((ItemInfo)localObject2).next;; localObject2 = ((ItemInfo)localObject2).next)
          {
            i = j;
            localObject1 = localObject4;
            if (localObject2 == null) {
              break;
            }
            if (((ItemInfo)localObject2).position == paramInt1 - 1)
            {
              localObject1 = localObject2;
              i = j;
              break;
            }
            if (((ItemInfo)localObject2).position == paramInt2 + 1)
            {
              localObject1 = localObject2;
              i = 0;
              break;
            }
          }
        }
      }
    }
    if (i != 0)
    {
      localObject2 = localObject1;
      f = ((ItemInfo)localObject1).left + ((ItemInfo)localObject1).width + this.m_ItemMargin;
      while ((paramInt1 <= paramInt2) && (f < this.m_Width))
      {
        localObject3 = prepareItem(paramInt1);
        ((ItemInfo)localObject3).addAfter((ItemInfo)localObject2);
        ((ItemInfo)localObject3).container.setAlpha(0.0F);
        ((ItemInfo)localObject3).container.animate().alpha(1.0F).setDuration(500L).start();
        if (this.m_ActiveItemInfoTail == localObject2) {
          this.m_ActiveItemInfoTail = ((ItemInfo)localObject3);
        }
        localObject2 = localObject3;
        f += ((ItemInfo)localObject3).width + this.m_ItemMargin;
        paramInt1 += 1;
      }
    }
    localObject2 = localObject1;
    float f = ((ItemInfo)localObject1).left - this.m_ItemMargin;
    while ((paramInt2 >= paramInt1) && (f > 0.0F))
    {
      localObject3 = prepareItem(paramInt2);
      ((ItemInfo)localObject3).addBefore((ItemInfo)localObject2);
      ((ItemInfo)localObject3).container.setAlpha(0.0F);
      ((ItemInfo)localObject3).container.animate().alpha(1.0F).setDuration(500L).start();
      if (this.m_ActiveItemInfoHead == localObject2) {
        this.m_ActiveItemInfoHead = ((ItemInfo)localObject3);
      }
      localObject2 = localObject3;
      f -= ((ItemInfo)localObject3).width + this.m_ItemMargin;
      paramInt2 -= 1;
    }
    if (!this.m_Handler.hasMessages(10000)) {
      updateItemsLayout(((ItemInfo)localObject1).position, true);
    }
  }
  
  private void onItemRemoved(int paramInt1, int paramInt2)
  {
    if (this.m_Adapter == null) {
      return;
    }
    int k = this.m_Adapter.getCount();
    if ((paramInt1 < 0) || (paramInt1 > k)) {}
    while (paramInt2 < paramInt1) {
      return;
    }
    if (k == 0)
    {
      refreshItems(false);
      return;
    }
    final ItemInfo localItemInfo = findFirstVisibleItemInfo();
    Object localObject = findLastVisibleItemInfo();
    int j = -1;
    if ((localItemInfo != null) && (localItemInfo.position > paramInt2))
    {
      i = localItemInfo.position;
      if ((i < 0) && (paramInt2 >= k - 1)) {
        break label233;
      }
      label91:
      localItemInfo = this.m_ActiveItemInfoHead;
      label97:
      if (localItemInfo == null) {
        break label324;
      }
      if (localItemInfo.position <= paramInt2) {
        break label240;
      }
      localItemInfo.position -= paramInt2 - paramInt1 + 1;
    }
    label233:
    label240:
    while (localItemInfo.position < paramInt1)
    {
      localItemInfo = localItemInfo.next;
      break label97;
      if ((localObject != null) && (((ItemInfo)localObject).position < paramInt1))
      {
        i = ((ItemInfo)localObject).position;
        break;
      }
      i = j;
      if (localItemInfo == null) {
        break;
      }
      for (localItemInfo = localItemInfo.next;; localItemInfo = localItemInfo.next)
      {
        i = j;
        if (localItemInfo == null) {
          break;
        }
        if (localItemInfo.position == paramInt1 - 1)
        {
          i = localItemInfo.position;
          break;
        }
        if (localItemInfo.position == paramInt2 + 1)
        {
          i = localItemInfo.position;
          break;
        }
      }
      if (paramInt1 <= 0) {
        break label91;
      }
      break label91;
    }
    localItemInfo.isRemoving = true;
    localObject = localItemInfo.container.animate().alpha(0.0F);
    if ((localItemInfo.previous != null) || (localItemInfo.next != null)) {}
    for (long l = 100L;; l = 300L)
    {
      ((ViewPropertyAnimator)localObject).setDuration(l).withEndAction(new Runnable()
      {
        public void run()
        {
          FilmstripView.-wrap7(FilmstripView.this, localItemInfo);
        }
      }).start();
      break;
    }
    label324:
    int i = 0;
    paramInt2 = i;
    if (this.m_IsScrolling)
    {
      localItemInfo = this.m_ActiveItemInfoHead;
      paramInt2 = i;
      if (localItemInfo != null)
      {
        if (localItemInfo.isRemoving) {
          break label370;
        }
        paramInt2 = 1;
      }
    }
    if ((this.m_IsScrolling) && (paramInt2 != 0)) {}
    label370:
    do
    {
      return;
      localItemInfo = localItemInfo.next;
      break;
      localItemInfo = getCurrentItemInfo();
      if (localItemInfo == null)
      {
        refreshItems(true);
        return;
      }
      if ((!localItemInfo.isRemoving) || (localItemInfo.position != paramInt1)) {
        break label510;
      }
      if (paramInt1 < k)
      {
        if ((this.m_ScrollListener == null) || (this.m_ScrollListener.isScrollStartedCalled)) {}
        for (;;)
        {
          scrollToItem(paramInt1, true);
          return;
          this.m_ScrollListener.isScrollStartedCalled = true;
          this.m_ScrollListener.onScrollStarted();
        }
      }
    } while (paramInt1 <= 0);
    if ((this.m_ScrollListener == null) || (this.m_ScrollListener.isScrollStartedCalled)) {}
    for (;;)
    {
      scrollToItem(paramInt1 - 1, true);
      return;
      this.m_ScrollListener.isScrollStartedCalled = true;
      this.m_ScrollListener.onScrollStarted();
    }
    label510:
    reportCurrentPosition(localItemInfo.position);
  }
  
  private void onItemRemovingAnimationCompleted(ItemInfo paramItemInfo)
  {
    if (this.m_Adapter == null) {
      return;
    }
    if (this.m_Adapter.getCount() <= 0)
    {
      refreshItems(0);
      return;
    }
    Log.v(TAG, "onItemRemovingAnimationCompleted() - Item : ", paramItemInfo);
    paramItemInfo.targetWidth = (-this.m_ItemMargin);
    if (paramItemInfo == this.m_AnchorItemInfo)
    {
      this.m_AnchorItemInfo = findPreviousNormalItemInfo(paramItemInfo);
      if (this.m_AnchorItemInfo == null) {
        this.m_AnchorItemInfo = findNextNormalItemInfo(paramItemInfo);
      }
    }
    updateItemsLayout(this.m_AnchorItemInfo, true);
  }
  
  private void onItemSizeChanged(int paramInt)
  {
    ItemInfo localItemInfo2 = getCurrentItemInfo();
    if (localItemInfo2 != null)
    {
      int i = localItemInfo2.targetWidth;
      if (!localItemInfo2.isRemoving) {
        localItemInfo2.targetWidth = this.m_Adapter.getItemWidth(localItemInfo2.position, this.m_Width);
      }
      if (localItemInfo2.targetWidth - i != 0) {}
      for (ItemInfo localItemInfo1 = localItemInfo2.previous; localItemInfo1 != null; localItemInfo1 = localItemInfo1.previous)
      {
        i = localItemInfo1.targetWidth;
        if (!localItemInfo1.isRemoving) {
          localItemInfo1.targetWidth = this.m_Adapter.getItemWidth(localItemInfo1.position, this.m_Width);
        }
        if (localItemInfo1.targetWidth - i == 0) {}
      }
      for (localItemInfo1 = localItemInfo2.next; localItemInfo1 != null; localItemInfo1 = localItemInfo1.next)
      {
        i = localItemInfo1.targetWidth;
        if (!localItemInfo1.isRemoving) {
          localItemInfo1.targetWidth = this.m_Adapter.getItemWidth(localItemInfo1.position, this.m_Width);
        }
        if (localItemInfo1.targetWidth - i == 0) {}
      }
      updateItemsLayout(paramInt, true);
    }
  }
  
  private ItemInfo prepareItem(int paramInt)
  {
    ItemInfo localItemInfo = obtainItemInfo(paramInt);
    localItemInfo.targetWidth = this.m_Adapter.getItemWidth(paramInt, this.m_Width);
    localItemInfo.width = localItemInfo.targetWidth;
    this.m_FastLayoutCounter += 1;
    this.m_Adapter.prepareItemView(paramInt, localItemInfo.container);
    this.m_FastLayoutCounter -= 1;
    if (localItemInfo.container.getParent() != null)
    {
      localItemInfo.container.setAlpha(1.0F);
      return localItemInfo;
    }
    addView(localItemInfo.container);
    return localItemInfo;
  }
  
  private void refreshItems(int paramInt)
  {
    if (this.m_Adapter != null) {}
    for (int j = this.m_Adapter.getCount();; j = 0)
    {
      i = paramInt;
      if (paramInt < 0) {
        i = 0;
      }
      paramInt = i;
      if (i >= j) {
        paramInt = j - 1;
      }
      clearItems();
      if ((j > 0) && (paramInt >= 0)) {
        break;
      }
      reportCurrentPosition(-1);
      return;
    }
    ItemInfo localItemInfo1 = prepareItem(paramInt);
    localItemInfo1.moveTo(calculateItemDefaultLeft(localItemInfo1, false));
    this.m_ActiveItemInfoHead = localItemInfo1;
    this.m_ActiveItemInfoTail = localItemInfo1;
    int i = paramInt - 1;
    float f = localItemInfo1.left;
    while ((f > 0.0F) && (i >= 0))
    {
      ItemInfo localItemInfo2 = prepareItem(i);
      localItemInfo2.moveTo(f - localItemInfo2.width - this.m_ItemMargin);
      localItemInfo2.addBefore(this.m_ActiveItemInfoHead);
      this.m_ActiveItemInfoHead = localItemInfo2;
      f = localItemInfo2.left;
      i -= 1;
    }
    i = paramInt + 1;
    f = localItemInfo1.left + localItemInfo1.targetWidth;
    while ((f < this.m_Width) && (i < j))
    {
      localItemInfo1 = prepareItem(i);
      localItemInfo1.moveTo(this.m_ItemMargin + f);
      localItemInfo1.addAfter(this.m_ActiveItemInfoTail);
      this.m_ActiveItemInfoTail = localItemInfo1;
      f = localItemInfo1.left + localItemInfo1.targetWidth;
      i += 1;
    }
    reportCurrentPosition(paramInt);
    if (this.m_ScrollListener != null)
    {
      this.m_ScrollListener.onItemSelected(paramInt);
      if ((!this.m_IsScrolling) && (this.m_ScrollListener.isScrollStartedCalled))
      {
        this.m_ScrollListener.isScrollStartedCalled = false;
        this.m_ScrollListener.onScrollStopped();
      }
    }
  }
  
  private void refreshItems(boolean paramBoolean)
  {
    refreshItems(getCurrentItem());
  }
  
  private void releaseItem(ItemInfo paramItemInfo)
  {
    this.m_FastLayoutCounter += 1;
    paramItemInfo.container.setAlpha(0.0F);
    if (this.m_Adapter != null) {
      this.m_Adapter.releaseItemView(paramItemInfo.position, paramItemInfo.container);
    }
    for (;;)
    {
      this.m_FastLayoutCounter -= 1;
      releaseItemInfo(paramItemInfo);
      return;
      Log.w(TAG, "releaseItem() - No adapter to release item " + paramItemInfo.position);
    }
  }
  
  private void releaseItemInfo(ItemInfo paramItemInfo)
  {
    paramItemInfo.container.animate().cancel();
    paramItemInfo.remove();
    paramItemInfo.addBefore(this.m_FreeItemInfos);
    this.m_FreeItemInfos = paramItemInfo;
  }
  
  private void reportCurrentPosition(int paramInt)
  {
    if (this.m_ReportedCurrentPosition != paramInt)
    {
      int i = this.m_ReportedCurrentPosition;
      this.m_ReportedCurrentPosition = paramInt;
      if (this.m_ScrollListener != null) {
        this.m_ScrollListener.onCurrentItemChanged(i, paramInt);
      }
    }
  }
  
  private float scrollBy(float paramFloat)
  {
    if (this.m_Adapter != null) {}
    for (int j = this.m_Adapter.getCount(); j <= 0; j = 0) {
      return 0.0F;
    }
    if (this.m_ActiveItemInfoHead == null) {
      return 0.0F;
    }
    int m = 0;
    int n = 0;
    int k = 0;
    boolean bool3 = false;
    boolean bool2 = false;
    int i;
    boolean bool1;
    float f;
    if (paramFloat > 0.0F)
    {
      i = k;
      bool1 = bool2;
      f = paramFloat;
      if (this.m_ActiveItemInfoHead.position == 0)
      {
        if (!this.m_ActiveItemInfoHead.isRemoving) {
          break label130;
        }
        f = paramFloat;
        bool1 = bool2;
        i = k;
      }
    }
    while (f != 0.0F)
    {
      ItemInfo localItemInfo = this.m_ActiveItemInfoHead;
      for (;;)
      {
        if (localItemInfo != null)
        {
          localItemInfo.moveBy(f);
          localItemInfo = localItemInfo.next;
          continue;
          label130:
          f = calculateItemDefaultLeft(this.m_ActiveItemInfoHead, false) - (this.m_ActiveItemInfoHead.left + paramFloat);
          if (f < 0.0F)
          {
            i = m;
            bool1 = bool3;
            if (!this.m_IsOverScrolled)
            {
              this.m_IsOverScrolled = true;
              i = 1;
              bool1 = true;
            }
            f = paramFloat + f;
            break;
          }
          this.m_IsOverScrolled = false;
          i = k;
          bool1 = bool2;
          f = paramFloat;
          break;
          i = k;
          bool1 = bool2;
          f = paramFloat;
          if (this.m_ActiveItemInfoTail.position != j - 1) {
            break;
          }
          i = k;
          bool1 = bool2;
          f = paramFloat;
          if (this.m_ActiveItemInfoTail.isRemoving) {
            break;
          }
          f = calculateItemDefaultLeft(this.m_ActiveItemInfoTail, false) + this.m_ActiveItemInfoTail.width - (this.m_ActiveItemInfoTail.left + this.m_ActiveItemInfoTail.width + paramFloat);
          if (f > 0.0F)
          {
            i = n;
            if (!this.m_IsOverScrolled)
            {
              this.m_IsOverScrolled = true;
              i = 1;
            }
            f = paramFloat + f;
            bool1 = bool2;
            break;
          }
          this.m_IsOverScrolled = false;
          i = k;
          bool1 = bool2;
          f = paramFloat;
          break;
        }
      }
      updateItemsLayout(this.m_AnchorItemInfo, true);
    }
    if (i != 0)
    {
      Log.v(TAG, "scrollBy() - Over-scroll");
      if (this.m_ScrollListener != null) {
        this.m_ScrollListener.onOverScroll(bool1);
      }
    }
    return f;
  }
  
  private void scrollToItem(int paramInt, boolean paramBoolean)
  {
    this.m_Handler.removeMessages(10002);
    stopFly();
    if (this.m_ActiveItemInfoHead == null) {
      return;
    }
    ItemInfo localItemInfo = findItemInfo(paramInt);
    float f2;
    long l;
    label70:
    float f1;
    if (localItemInfo != null)
    {
      this.m_AnchorItemInfo = localItemInfo;
      f2 = calculateItemDefaultLeft(localItemInfo, false) - localItemInfo.left;
      if (!paramBoolean) {
        break label167;
      }
      this.m_ScrollToItemStartTime = SystemClock.elapsedRealtime();
      l = 0L;
      if (localItemInfo == null) {
        break label247;
      }
      if ((Math.abs(f2) <= 1.0F) || (l >= 600L)) {
        break label192;
      }
      float f3 = f2 / 6.5F;
      f1 = f3;
      if (Math.abs(f3) <= 5.0F)
      {
        if (f3 <= 0.0F) {
          break label180;
        }
        f1 = Math.min(f2, 5.0F);
      }
      label131:
      scrollBy(f1);
      this.m_Handler.sendMessageDelayed(Message.obtain(this.m_Handler, 10002, paramInt, 0), 15L);
    }
    label167:
    label180:
    label192:
    do
    {
      do
      {
        return;
        f2 = 0.0F;
        break;
        l = SystemClock.elapsedRealtime() - this.m_ScrollToItemStartTime;
        break label70;
        f1 = Math.max(f2, -5.0F);
        break label131;
        scrollBy(f2);
      } while (this.m_ScrollListener == null);
      this.m_ScrollListener.onItemSelected(paramInt);
    } while ((this.m_IsScrolling) || (!this.m_ScrollListener.isScrollStartedCalled));
    this.m_ScrollListener.isScrollStartedCalled = false;
    this.m_ScrollListener.onScrollStopped();
    return;
    label247:
    localItemInfo = getCurrentItemInfo();
    if ((localItemInfo != null) && (l < 600L))
    {
      int j = paramInt - localItemInfo.position;
      int i = j;
      if (localItemInfo.isRemoving) {
        if (localItemInfo.position > paramInt) {
          break label354;
        }
      }
      label354:
      for (i = 1;; i = -1)
      {
        i = j + i;
        if (Math.abs(i) <= 2) {
          break;
        }
        refreshItems(localItemInfo.position + i / 2);
        this.m_Handler.sendMessageDelayed(Message.obtain(this.m_Handler, 10002, paramInt, 0), 15L);
        return;
      }
      j = this.m_Width / 2;
      if (i >= 0) {}
      for (i = -1;; i = 1)
      {
        scrollBy(j * i);
        this.m_Handler.sendMessageDelayed(Message.obtain(this.m_Handler, 10002, paramInt, 0), 15L);
        return;
      }
    }
    refreshItems(paramInt);
  }
  
  private void setupHandler()
  {
    this.m_Handler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        FilmstripView.-wrap0(FilmstripView.this, paramAnonymousMessage);
      }
    };
  }
  
  private void startFly(float paramFloat)
  {
    stopFly();
    this.m_IsFlying = true;
    this.m_Handler.sendMessageDelayed(Message.obtain(this.m_Handler, 10001, new Object[] { Float.valueOf(paramFloat), Long.valueOf(SystemClock.elapsedRealtime()) }), 10L);
  }
  
  private void stopAutoScroll()
  {
    stopFly();
    this.m_Handler.removeMessages(10002);
  }
  
  private void stopFly()
  {
    if (this.m_IsFlying)
    {
      this.m_IsFlying = false;
      this.m_Handler.removeMessages(10001);
    }
  }
  
  private void updateItemsLayout(int paramInt, boolean paramBoolean)
  {
    Object localObject;
    if ((paramInt < 0) || ((this.m_Adapter != null) && (paramInt >= this.m_Adapter.getCount()))) {
      localObject = getCurrentItemInfo();
    }
    for (;;)
    {
      updateItemsLayout((ItemInfo)localObject, paramBoolean);
      return;
      ItemInfo localItemInfo = findItemInfo(paramInt);
      localObject = localItemInfo;
      if (localItemInfo == null) {
        localObject = getCurrentItemInfo();
      }
    }
  }
  
  private void updateItemsLayout(ItemInfo paramItemInfo, boolean paramBoolean)
  {
    if (this.m_Adapter != null) {}
    for (int k = this.m_Adapter.getCount(); k <= 0; k = 0) {
      return;
    }
    ItemInfo localItemInfo1 = paramItemInfo;
    if (paramItemInfo == null) {
      localItemInfo1 = this.m_ActiveItemInfoHead;
    }
    int i = 0;
    paramItemInfo = this.m_ActiveItemInfoHead;
    if (paramItemInfo != null)
    {
      ItemInfo localItemInfo2 = paramItemInfo.next;
      int n = i;
      int m;
      if (paramItemInfo.width != paramItemInfo.targetWidth)
      {
        n = paramItemInfo.targetWidth - paramItemInfo.width;
        if ((Math.abs(n) <= 5) || (!paramBoolean)) {
          break label166;
        }
        m = n / 3;
        paramItemInfo.width += m;
        j = 1;
      }
      for (;;)
      {
        paramItemInfo.layout(this.m_Width, this.m_Height, true, true);
        n = j;
        if (paramItemInfo == localItemInfo1)
        {
          paramItemInfo.moveBy(-(m / 2));
          n = j;
        }
        paramItemInfo = localItemInfo2;
        i = n;
        break;
        label166:
        paramItemInfo.width = paramItemInfo.targetWidth;
        m = n;
        j = i;
        if (paramItemInfo.width <= -this.m_ItemMargin)
        {
          m = n;
          j = i;
          if (paramItemInfo.isRemoving)
          {
            if (this.m_ActiveItemInfoHead == paramItemInfo) {
              this.m_ActiveItemInfoHead = paramItemInfo.next;
            }
            if (this.m_ActiveItemInfoTail == paramItemInfo) {
              this.m_ActiveItemInfoTail = paramItemInfo.previous;
            }
            releaseItem(paramItemInfo);
            m = n;
            j = i;
          }
        }
      }
    }
    if ((this.m_ActiveItemInfoHead == null) || (this.m_ActiveItemInfoTail == null))
    {
      Log.e(TAG, "updateItemsLayout() - No active items");
      return;
    }
    float f;
    if (localItemInfo1 != null)
    {
      paramItemInfo = localItemInfo1.previous;
      f = localItemInfo1.left;
      while (paramItemInfo != null)
      {
        f -= paramItemInfo.width + this.m_ItemMargin;
        paramItemInfo.moveTo(f);
        paramItemInfo = paramItemInfo.previous;
      }
      paramItemInfo = localItemInfo1.next;
      f = localItemInfo1.left + localItemInfo1.width + this.m_ItemMargin;
      while (paramItemInfo != null)
      {
        paramItemInfo.moveTo(f);
        f += paramItemInfo.width + this.m_ItemMargin;
        paramItemInfo = paramItemInfo.next;
      }
    }
    int j = this.m_ActiveItemInfoHead.position - 1;
    while ((this.m_ActiveItemInfoHead.left > this.m_ItemMargin) && (j >= 0))
    {
      paramItemInfo = prepareItem(j);
      paramItemInfo.moveTo(this.m_ActiveItemInfoHead.left - paramItemInfo.width - this.m_ItemMargin);
      paramItemInfo.addBefore(this.m_ActiveItemInfoHead);
      this.m_ActiveItemInfoHead = paramItemInfo;
      j -= 1;
    }
    if (this.m_ActiveItemInfoTail.isRemoving) {}
    for (j = this.m_ActiveItemInfoTail.position;; j = this.m_ActiveItemInfoTail.position + 1)
    {
      f = this.m_ActiveItemInfoTail.left + this.m_ActiveItemInfoTail.width;
      while ((f < this.m_Width - this.m_ItemMargin) && (j < k))
      {
        paramItemInfo = prepareItem(j);
        paramItemInfo.moveTo(this.m_ActiveItemInfoTail.left + this.m_ActiveItemInfoTail.width + this.m_ItemMargin);
        paramItemInfo.addAfter(this.m_ActiveItemInfoTail);
        this.m_ActiveItemInfoTail = paramItemInfo;
        f = paramItemInfo.left + paramItemInfo.width;
        j += 1;
      }
    }
    while ((this.m_ActiveItemInfoTail.left >= this.m_Width) && (this.m_ActiveItemInfoTail != this.m_ActiveItemInfoHead))
    {
      paramItemInfo = this.m_ActiveItemInfoTail.previous;
      releaseItem(this.m_ActiveItemInfoTail);
      if (this.m_AnchorItemInfo == this.m_ActiveItemInfoTail) {
        this.m_AnchorItemInfo = paramItemInfo;
      }
      this.m_ActiveItemInfoTail = paramItemInfo;
    }
    while ((this.m_ActiveItemInfoHead.left + this.m_ActiveItemInfoHead.width <= 0.0F) && (this.m_ActiveItemInfoTail != this.m_ActiveItemInfoHead))
    {
      paramItemInfo = this.m_ActiveItemInfoHead.next;
      releaseItem(this.m_ActiveItemInfoHead);
      if (this.m_AnchorItemInfo == this.m_ActiveItemInfoHead) {
        this.m_AnchorItemInfo = paramItemInfo;
      }
      this.m_ActiveItemInfoHead = paramItemInfo;
    }
    if (i != 0)
    {
      if (localItemInfo1 != null) {
        break label790;
      }
      if (!this.m_Handler.hasMessages(10000)) {
        this.m_Handler.sendEmptyMessageDelayed(10000, 15L);
      }
    }
    for (;;)
    {
      reportCurrentPosition(getCurrentItem());
      return;
      label790:
      this.m_Handler.removeMessages(10000);
      this.m_Handler.sendMessageDelayed(Message.obtain(this.m_Handler, 10000, localItemInfo1), 15L);
    }
  }
  
  private void updateItemsLayout(boolean paramBoolean)
  {
    updateItemsLayout(-1, paramBoolean);
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() == 0) {
      this.m_HasMultiPointers = false;
    }
    if ((paramMotionEvent.getPointerCount() > 1) && (!this.m_HasMultiPointers))
    {
      scrollToItem(getCurrentItem(), true);
      this.m_HasMultiPointers = true;
    }
    int i;
    if (this.m_IsScrolling)
    {
      i = 0;
      this.m_GestureDetector.onTouchEvent(paramMotionEvent);
      switch (paramMotionEvent.getAction())
      {
      case 2: 
      default: 
        label88:
        if (this.m_IsScrolling) {
          if (i != 0)
          {
            Log.v(TAG, "dispatchTouchEvent() - Dispatch ACTION_CANCEL to child");
            paramMotionEvent.setAction(3);
            super.dispatchTouchEvent(paramMotionEvent);
          }
        }
        break;
      }
    }
    for (;;)
    {
      if (this.m_OnTouchListener != null) {
        this.m_OnTouchListener.onTouch(this, paramMotionEvent);
      }
      return true;
      i = 1;
      break;
      onGestureUp(paramMotionEvent);
      break label88;
      super.dispatchTouchEvent(paramMotionEvent);
    }
  }
  
  public int findPositionOfView(View paramView)
  {
    int i = -1;
    if (paramView == null) {
      return -1;
    }
    if ((paramView instanceof ViewParent)) {
      paramView = (ViewParent)paramView;
    }
    for (;;)
    {
      if ((paramView == null) || ((paramView instanceof ItemContainerView)))
      {
        if ((paramView instanceof ItemContainerView)) {
          i = ((ItemContainerView)paramView).itemInfo.position;
        }
        return i;
        paramView = paramView.getParent();
      }
      else
      {
        paramView = paramView.getParent();
      }
    }
  }
  
  public int getCurrentItem()
  {
    int i = 0;
    if (this.m_Adapter != null) {
      i = this.m_Adapter.getCount();
    }
    if (i <= 0) {
      return -1;
    }
    ItemInfo localItemInfo = getCurrentItemInfo();
    if (localItemInfo != null) {
      return Math.min(localItemInfo.position, i - 1);
    }
    return -1;
  }
  
  public int getFirstVisibltItem()
  {
    for (ItemInfo localItemInfo = this.m_ActiveItemInfoHead; (localItemInfo != null) && (localItemInfo.isRemoving); localItemInfo = localItemInfo.next) {}
    if (localItemInfo != null) {
      return localItemInfo.position;
    }
    return -1;
  }
  
  public int getLastVisibltItem()
  {
    for (ItemInfo localItemInfo = this.m_ActiveItemInfoTail; (localItemInfo != null) && (localItemInfo.isRemoving); localItemInfo = localItemInfo.previous) {}
    if (localItemInfo != null) {
      return localItemInfo.position;
    }
    return -1;
  }
  
  public boolean isScrolling()
  {
    return this.m_IsScrolling;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    for (ItemInfo localItemInfo = this.m_ActiveItemInfoHead; localItemInfo != null; localItemInfo = localItemInfo.next) {
      localItemInfo.layout(this.m_Width, this.m_Height, true, true);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (View.MeasureSpec.getMode(paramInt1) == 1073741824)
    {
      paramInt1 = View.MeasureSpec.getSize(paramInt1);
      if (View.MeasureSpec.getMode(paramInt2) != 1073741824) {
        break label44;
      }
    }
    label44:
    for (paramInt2 = View.MeasureSpec.getSize(paramInt2);; paramInt2 = 32767)
    {
      setMeasuredDimension(paramInt1, paramInt2);
      return;
      paramInt1 = 32767;
      break;
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    paramInt3 = getCurrentItem();
    this.m_Width = paramInt1;
    this.m_Height = paramInt2;
    refreshItems(paramInt3);
  }
  
  public void requestLayout()
  {
    if ((this.m_FastLayoutCounter <= 0) || (isLayoutRequested()))
    {
      if (this.m_Handler != null) {
        this.m_Handler.removeMessages(10010);
      }
      super.requestLayout();
      return;
    }
    if (!this.m_Handler.hasMessages(10010)) {
      this.m_Handler.sendMessageAtFrontOfQueue(Message.obtain(this.m_Handler, 10010));
    }
  }
  
  public void setAdapter(Adapter paramAdapter)
  {
    if (this.m_Adapter != paramAdapter)
    {
      if (this.m_Adapter != null) {
        this.m_Adapter.detach(this);
      }
      clearItems();
      this.m_Adapter = paramAdapter;
      if (paramAdapter != null)
      {
        paramAdapter.attach(this);
        refreshItems(false);
      }
    }
  }
  
  public void setCurrentItem(int paramInt, boolean paramBoolean)
  {
    stopAutoScroll();
    if (paramBoolean) {
      scrollToItem(paramInt, true);
    }
    do
    {
      do
      {
        return;
        ItemInfo localItemInfo = findItemInfo(paramInt);
        if (localItemInfo == null) {
          break;
        }
        scrollBy(calculateItemDefaultLeft(localItemInfo, false) - localItemInfo.left);
      } while (this.m_ScrollListener == null);
      this.m_ScrollListener.onItemSelected(paramInt);
    } while ((this.m_IsScrolling) || (!this.m_ScrollListener.isScrollStartedCalled));
    this.m_ScrollListener.isScrollStartedCalled = false;
    this.m_ScrollListener.onScrollStopped();
    return;
    refreshItems(paramInt);
  }
  
  public void setItemMargin(int paramInt)
  {
    this.m_ItemMargin = paramInt;
    updateItemsLayout(true);
  }
  
  public void setOnTouchListener(View.OnTouchListener paramOnTouchListener)
  {
    this.m_OnTouchListener = paramOnTouchListener;
  }
  
  public void setScrollListener(ScrollListener paramScrollListener)
  {
    this.m_ScrollListener = paramScrollListener;
  }
  
  public void setScrollMode(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unknown scroll mode : " + paramInt + ".");
    case 0: 
    case 1: 
      this.m_ScrollMode = paramInt;
    }
    do
    {
      return;
      this.m_IsScrolling = false;
      this.m_ScrollMode = paramInt;
      paramInt = getCurrentItem();
    } while (paramInt < 0);
    setCurrentItem(paramInt, true);
  }
  
  public static abstract class Adapter
  {
    private static final int MSG_NOTIFY_ITEM_SIZE_CHANGED = 10000;
    private final List<FilmstripView> m_FilmstripViews = new ArrayList();
    private final Handler m_Handler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        FilmstripView.Adapter.-wrap0(FilmstripView.Adapter.this, paramAnonymousMessage);
      }
    };
    
    private void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      }
      for (;;)
      {
        return;
        int i = this.m_FilmstripViews.size() - 1;
        while (i >= 0)
        {
          FilmstripView.-wrap8((FilmstripView)this.m_FilmstripViews.get(i), paramMessage.arg1);
          i -= 1;
        }
      }
    }
    
    final void attach(FilmstripView paramFilmstripView)
    {
      this.m_FilmstripViews.add(paramFilmstripView);
    }
    
    final void detach(FilmstripView paramFilmstripView)
    {
      this.m_FilmstripViews.remove(paramFilmstripView);
    }
    
    public abstract int getCount();
    
    public int getItemWidth(int paramInt1, int paramInt2)
    {
      return paramInt2;
    }
    
    public void notifyDataSetChanged()
    {
      int i = this.m_FilmstripViews.size() - 1;
      while (i >= 0)
      {
        FilmstripView.-wrap1((FilmstripView)this.m_FilmstripViews.get(i));
        i -= 1;
      }
    }
    
    public void notifyItemAdded(int paramInt)
    {
      notifyItemAdded(paramInt, paramInt);
    }
    
    public void notifyItemAdded(int paramInt1, int paramInt2)
    {
      int i = this.m_FilmstripViews.size() - 1;
      while (i >= 0)
      {
        FilmstripView.-wrap5((FilmstripView)this.m_FilmstripViews.get(i), paramInt1, paramInt2);
        i -= 1;
      }
    }
    
    public void notifyItemRemoved(int paramInt)
    {
      notifyItemRemoved(paramInt, paramInt);
    }
    
    public void notifyItemRemoved(int paramInt1, int paramInt2)
    {
      int i = this.m_FilmstripViews.size() - 1;
      while (i >= 0)
      {
        FilmstripView.-wrap6((FilmstripView)this.m_FilmstripViews.get(i), paramInt1, paramInt2);
        i -= 1;
      }
    }
    
    public void notifyItemSizeChanged()
    {
      notifyItemSizeChanged(-1);
    }
    
    public void notifyItemSizeChanged(int paramInt)
    {
      this.m_Handler.removeMessages(10000);
      Message.obtain(this.m_Handler, 10000, paramInt, 0).sendToTarget();
    }
    
    public abstract void prepareItemView(int paramInt, ViewGroup paramViewGroup);
    
    public void releaseItemView(int paramInt, ViewGroup paramViewGroup)
    {
      paramViewGroup.removeAllViews();
    }
  }
  
  private static final class ItemContainerView
    extends FrameLayout
  {
    public final FilmstripView.ItemInfo itemInfo;
    
    public ItemContainerView(Context paramContext, FilmstripView.ItemInfo paramItemInfo)
    {
      super();
      this.itemInfo = paramItemInfo;
    }
  }
  
  private final class ItemInfo
  {
    public FilmstripView.ItemContainerView container;
    public boolean isRemoving;
    public float left;
    public ItemInfo next;
    public int position;
    public ItemInfo previous;
    public int targetWidth;
    public int width;
    
    private ItemInfo() {}
    
    public void addAfter(ItemInfo paramItemInfo)
    {
      if (paramItemInfo != null)
      {
        this.next = paramItemInfo.next;
        paramItemInfo.next = this;
      }
      if (this.next != null) {
        this.next.previous = this;
      }
      this.previous = paramItemInfo;
    }
    
    public void addBefore(ItemInfo paramItemInfo)
    {
      if (paramItemInfo != null)
      {
        this.previous = paramItemInfo.previous;
        paramItemInfo.previous = this;
      }
      if (this.previous != null) {
        this.previous.next = this;
      }
      this.next = paramItemInfo;
    }
    
    public void layout(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    {
      if (!paramBoolean2)
      {
        ViewGroup.LayoutParams localLayoutParams = this.container.getLayoutParams();
        if (localLayoutParams != null)
        {
          localLayoutParams.width = this.width;
          localLayoutParams.height = paramInt2;
          this.container.requestLayout();
          return;
        }
      }
      if (paramBoolean1) {
        this.container.measure(View.MeasureSpec.makeMeasureSpec(this.width, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
      }
      this.container.layout(0, 0, this.width, paramInt2);
    }
    
    public void moveBy(float paramFloat)
    {
      this.left += paramFloat;
      this.container.setTranslationX(this.left);
    }
    
    public void moveTo(float paramFloat)
    {
      this.left = paramFloat;
      this.container.setTranslationX(paramFloat);
    }
    
    public void remove()
    {
      if (this.previous != null) {
        this.previous.next = this.next;
      }
      if (this.next != null) {
        this.next.previous = this.previous;
      }
      this.previous = null;
      this.next = null;
    }
    
    public String toString()
    {
      return "[Position=" + this.position + ", isRemoving=" + this.isRemoving + "]";
    }
  }
  
  public static abstract class ScrollListener
  {
    boolean isScrollStartedCalled;
    
    public void onCurrentItemChanged(int paramInt1, int paramInt2) {}
    
    public void onItemSelected(int paramInt) {}
    
    public void onOverScroll(boolean paramBoolean) {}
    
    public void onScrollStarted() {}
    
    public void onScrollStopped() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/widget/FilmstripView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */