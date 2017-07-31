package com.oneplus.camera.ui;

import android.os.Message;
import android.os.SystemClock;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.Rotation;
import com.oneplus.base.ScreenSize;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.UIComponent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GestureDetectorImpl
  extends UIComponent
  implements GestureDetector
{
  private static final int MSG_SINGLE_TAP_UP = 10001;
  private static final int THRESHOLD_SLIDE_DISTANCE = 200;
  private static final long THRESHOLD_SLIDE_TIME_MILLIS = 800L;
  private android.view.GestureDetector m_GestureDetector;
  private List<GestureHandlerHandle> m_GestureHandles = new ArrayList();
  private GestureDetector.OnGestureListener m_GestureListener = new GestureDetector.SimpleOnGestureListener()
  {
    private GestureDetectorImpl.GestureSlide m_DetectedSlideGesture;
    private boolean m_DetectedSlideNotified;
    private long m_GestureStartTimeMillis;
    private GestureDetector.GestureHandler m_ScrollHandler;
    
    public boolean onDoubleTap(MotionEvent paramAnonymousMotionEvent)
    {
      if (Math.abs(System.currentTimeMillis() - GestureDetectorImpl.-get3(GestureDetectorImpl.this)) > 0L)
      {
        Log.v(GestureDetectorImpl.-get0(GestureDetectorImpl.this), "onDoubleTap() - Double tap comes but too late, ignore.");
        return false;
      }
      HandlerUtils.removeMessages(GestureDetectorImpl.this, 10001);
      int i = GestureDetectorImpl.-get1(GestureDetectorImpl.this).size() - 1;
      while (i >= 0)
      {
        GestureDetectorImpl.GestureHandlerHandle localGestureHandlerHandle = (GestureDetectorImpl.GestureHandlerHandle)GestureDetectorImpl.-get1(GestureDetectorImpl.this).get(i);
        if ((localGestureHandlerHandle.consumeGesture) && (localGestureHandlerHandle.handler.onDoubleTap(paramAnonymousMotionEvent))) {
          return true;
        }
        i -= 1;
      }
      return false;
    }
    
    public boolean onDown(MotionEvent paramAnonymousMotionEvent)
    {
      this.m_ScrollHandler = null;
      this.m_DetectedSlideGesture = null;
      this.m_DetectedSlideNotified = false;
      this.m_GestureStartTimeMillis = SystemClock.elapsedRealtime();
      GestureDetectorImpl.-wrap5(GestureDetectorImpl.this, paramAnonymousMotionEvent, GestureDetectorImpl.-wrap0(GestureDetectorImpl.this), GestureDetectorImpl.-wrap1(GestureDetectorImpl.this));
      int i = GestureDetectorImpl.-get1(GestureDetectorImpl.this).size() - 1;
      while (i >= 0)
      {
        GestureDetectorImpl.GestureHandlerHandle localGestureHandlerHandle = (GestureDetectorImpl.GestureHandlerHandle)GestureDetectorImpl.-get1(GestureDetectorImpl.this).get(i);
        localGestureHandlerHandle.consumeGesture = localGestureHandlerHandle.handler.onGestureStart(paramAnonymousMotionEvent);
        i -= 1;
      }
      return false;
    }
    
    public boolean onFling(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2)
    {
      if (this.m_ScrollHandler != null)
      {
        Rotation localRotation1 = GestureDetectorImpl.-wrap0(GestureDetectorImpl.this);
        Rotation localRotation2 = GestureDetectorImpl.-wrap1(GestureDetectorImpl.this);
        GestureDetectorImpl.-wrap5(GestureDetectorImpl.this, paramAnonymousMotionEvent1, localRotation1, localRotation2);
        GestureDetectorImpl.-wrap5(GestureDetectorImpl.this, paramAnonymousMotionEvent2, localRotation1, localRotation2);
        float f = GestureDetectorImpl.-wrap2(GestureDetectorImpl.this, paramAnonymousFloat1, paramAnonymousFloat2, localRotation1, localRotation2);
        paramAnonymousFloat1 = GestureDetectorImpl.-wrap3(GestureDetectorImpl.this, paramAnonymousFloat1, paramAnonymousFloat2, localRotation1, localRotation2);
        this.m_ScrollHandler.onFling(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, f, paramAnonymousFloat1);
        return true;
      }
      return false;
    }
    
    public void onLongPress(MotionEvent paramAnonymousMotionEvent)
    {
      GestureDetectorImpl.-wrap5(GestureDetectorImpl.this, paramAnonymousMotionEvent, GestureDetectorImpl.-wrap0(GestureDetectorImpl.this), GestureDetectorImpl.-wrap1(GestureDetectorImpl.this));
      int i = GestureDetectorImpl.-get1(GestureDetectorImpl.this).size() - 1;
      while (i >= 0)
      {
        GestureDetectorImpl.GestureHandlerHandle localGestureHandlerHandle = (GestureDetectorImpl.GestureHandlerHandle)GestureDetectorImpl.-get1(GestureDetectorImpl.this).get(i);
        if ((localGestureHandlerHandle.consumeGesture) && (localGestureHandlerHandle.handler.onLongPress(paramAnonymousMotionEvent))) {
          return;
        }
        i -= 1;
      }
    }
    
    public boolean onScroll(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2)
    {
      Object localObject = GestureDetectorImpl.-wrap0(GestureDetectorImpl.this);
      Rotation localRotation = GestureDetectorImpl.-wrap1(GestureDetectorImpl.this);
      GestureDetectorImpl.-wrap5(GestureDetectorImpl.this, paramAnonymousMotionEvent1, (Rotation)localObject, localRotation);
      GestureDetectorImpl.-wrap5(GestureDetectorImpl.this, paramAnonymousMotionEvent2, (Rotation)localObject, localRotation);
      float f1 = GestureDetectorImpl.-wrap2(GestureDetectorImpl.this, paramAnonymousFloat1, paramAnonymousFloat2, (Rotation)localObject, localRotation);
      paramAnonymousFloat1 = GestureDetectorImpl.-wrap3(GestureDetectorImpl.this, paramAnonymousFloat1, paramAnonymousFloat2, (Rotation)localObject, localRotation);
      paramAnonymousFloat2 = paramAnonymousMotionEvent2.getX() - paramAnonymousMotionEvent1.getX();
      float f2 = paramAnonymousMotionEvent2.getY() - paramAnonymousMotionEvent1.getY();
      int i;
      label168:
      boolean bool1;
      if ((this.m_DetectedSlideGesture != null) || (SystemClock.elapsedRealtime() - this.m_GestureStartTimeMillis >= 800L) || (GestureDetectorImpl.-get2(GestureDetectorImpl.this)))
      {
        if (GestureDetectorImpl.-get2(GestureDetectorImpl.this)) {
          this.m_DetectedSlideGesture = null;
        }
        boolean bool2 = false;
        if (this.m_ScrollHandler != null) {
          break label385;
        }
        i = GestureDetectorImpl.-get1(GestureDetectorImpl.this).size() - 1;
        bool1 = bool2;
        if (i >= 0)
        {
          localObject = (GestureDetectorImpl.GestureHandlerHandle)GestureDetectorImpl.-get1(GestureDetectorImpl.this).get(i);
          if ((!((GestureDetectorImpl.GestureHandlerHandle)localObject).consumeGesture) || (!((GestureDetectorImpl.GestureHandlerHandle)localObject).handler.onScroll(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, f1, paramAnonymousFloat1))) {
            break label376;
          }
          bool1 = true;
          this.m_ScrollHandler = ((GestureDetectorImpl.GestureHandlerHandle)localObject).handler;
        }
        label234:
        if ((bool1) && (!this.m_DetectedSlideNotified) && (this.m_DetectedSlideGesture != null)) {
          switch (-getcom-oneplus-camera-ui-GestureDetectorImpl$GestureSlideSwitchesValues()[this.m_DetectedSlideGesture.ordinal()])
          {
          }
        }
      }
      for (;;)
      {
        this.m_DetectedSlideNotified = true;
        return bool1;
        if (paramAnonymousFloat2 < -200.0F)
        {
          this.m_DetectedSlideGesture = GestureDetectorImpl.GestureSlide.SLIDE_LEFT;
          break;
        }
        if (paramAnonymousFloat2 > 200.0F)
        {
          this.m_DetectedSlideGesture = GestureDetectorImpl.GestureSlide.SLIDE_RIGHT;
          break;
        }
        if (f2 < -200.0F)
        {
          this.m_DetectedSlideGesture = GestureDetectorImpl.GestureSlide.SLIDE_TOP;
          break;
        }
        if (f2 <= 200.0F) {
          break;
        }
        this.m_DetectedSlideGesture = GestureDetectorImpl.GestureSlide.SLIDE_BOTTOM;
        break;
        label376:
        i -= 1;
        break label168;
        label385:
        this.m_ScrollHandler.onScroll(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, f1, paramAnonymousFloat1);
        bool1 = true;
        break label234;
        if (this.m_ScrollHandler.onSlideDown(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2))
        {
          this.m_DetectedSlideNotified = true;
          continue;
          if (this.m_ScrollHandler.onSlideLeft(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2))
          {
            this.m_DetectedSlideNotified = true;
            continue;
            if (this.m_ScrollHandler.onSlideRight(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2))
            {
              this.m_DetectedSlideNotified = true;
              continue;
              if (this.m_ScrollHandler.onSlideUp(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2)) {
                this.m_DetectedSlideNotified = true;
              }
            }
          }
        }
      }
    }
    
    public void onShowPress(MotionEvent paramAnonymousMotionEvent) {}
    
    public boolean onSingleTapUp(MotionEvent paramAnonymousMotionEvent)
    {
      GestureDetectorImpl.-set0(GestureDetectorImpl.this, System.currentTimeMillis());
      return HandlerUtils.sendMessage(GestureDetectorImpl.this, 10001, 0, 0, paramAnonymousMotionEvent, true, 0L);
    }
  };
  private boolean m_HasMultiPointers;
  private boolean m_IsGestureStarted;
  private long m_OnSingleTapTimestamp;
  
  GestureDetectorImpl(CameraActivity paramCameraActivity)
  {
    super("Gesture Detector Impl", paramCameraActivity, true);
  }
  
  private void handleTouchEventInternal(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getAction())
    {
    default: 
      if (!this.m_IsGestureStarted) {
        return;
      }
      break;
    case 0: 
      this.m_HasMultiPointers = false;
      this.m_IsGestureStarted = true;
    }
    this.m_GestureDetector.onTouchEvent(paramMotionEvent);
    if (paramMotionEvent.getPointerCount() > 1) {
      this.m_HasMultiPointers = true;
    }
    switch (paramMotionEvent.getAction())
    {
    case 2: 
    default: 
      return;
    }
    int i = this.m_GestureHandles.size() - 1;
    while (i >= 0)
    {
      ((GestureHandlerHandle)this.m_GestureHandles.get(i)).handler.onGestureEnd(paramMotionEvent);
      i -= 1;
    }
    this.m_IsGestureStarted = false;
  }
  
  private void onSingleTapReceived(MotionEvent paramMotionEvent)
  {
    rotateMotionEvent(paramMotionEvent, getCameraActivityRotation(), getRotation());
    int i = this.m_GestureHandles.size() - 1;
    while (i >= 0)
    {
      GestureHandlerHandle localGestureHandlerHandle = (GestureHandlerHandle)this.m_GestureHandles.get(i);
      if ((localGestureHandlerHandle.consumeGesture) && (localGestureHandlerHandle.handler.onSingleTapUp(paramMotionEvent))) {
        return;
      }
      i -= 1;
    }
  }
  
  private float rotateDistanceX(float paramFloat1, float paramFloat2, Rotation paramRotation1, Rotation paramRotation2)
  {
    int j = paramRotation2.getDeviceOrientation() - paramRotation1.getDeviceOrientation();
    int i = j;
    if (j < 0) {
      i = j + 360;
    }
    switch (i)
    {
    default: 
      return paramFloat1;
    case 90: 
      return -paramFloat2;
    case 270: 
      return paramFloat2;
    }
    return -paramFloat1;
  }
  
  private float rotateDistanceY(float paramFloat1, float paramFloat2, Rotation paramRotation1, Rotation paramRotation2)
  {
    int j = paramRotation2.getDeviceOrientation() - paramRotation1.getDeviceOrientation();
    int i = j;
    if (j < 0) {
      i = j + 360;
    }
    switch (i)
    {
    default: 
      return paramFloat2;
    case 90: 
      return paramFloat1;
    case 270: 
      return -paramFloat1;
    }
    return -paramFloat2;
  }
  
  private void rotateMotionEvent(MotionEvent paramMotionEvent, Rotation paramRotation1, Rotation paramRotation2)
  {
    float f1 = paramMotionEvent.getRawX();
    float f2 = paramMotionEvent.getRawY();
    int j = paramRotation2.getDeviceOrientation() - paramRotation1.getDeviceOrientation();
    int i = j;
    if (j < 0) {
      i = j + 360;
    }
    switch (i)
    {
    default: 
      return;
    case 90: 
      paramMotionEvent.setLocation(getScreenSize().getHeight() - f2, f1);
      return;
    case 270: 
      paramMotionEvent.setLocation(f2, getScreenSize().getWidth() - f1);
      return;
    }
    paramRotation1 = getScreenSize();
    paramMotionEvent.setLocation(paramRotation1.getWidth() - f1, paramRotation1.getHeight() - f2);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    onSingleTapReceived((MotionEvent)paramMessage.obj);
  }
  
  public void handleTouchEvent(MotionEvent paramMotionEvent)
  {
    verifyAccess();
    paramMotionEvent.setLocation(paramMotionEvent.getRawX(), paramMotionEvent.getRawY());
    handleTouchEventInternal(paramMotionEvent);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_GestureDetector = new android.view.GestureDetector(localCameraActivity, this.m_GestureListener);
    localCameraActivity.addHandler(CameraActivity.EVENT_TOUCH, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MotionEventArgs> paramAnonymousEventKey, MotionEventArgs paramAnonymousMotionEventArgs)
      {
        GestureDetectorImpl.-wrap4(GestureDetectorImpl.this, paramAnonymousMotionEventArgs.getMotionEvent());
      }
    });
  }
  
  public Handle setGestureHandler(GestureDetector.GestureHandler paramGestureHandler, int paramInt)
  {
    verifyAccess();
    Object localObject2 = null;
    Iterator localIterator = this.m_GestureHandles.iterator();
    Object localObject1;
    do
    {
      localObject1 = localObject2;
      if (!localIterator.hasNext()) {
        break;
      }
      localObject1 = (GestureHandlerHandle)localIterator.next();
    } while (paramGestureHandler != ((GestureHandlerHandle)localObject1).handler);
    if (localObject1 != null) {
      this.m_GestureHandles.remove(localObject1);
    }
    paramGestureHandler = new GestureHandlerHandle(paramGestureHandler, paramInt);
    this.m_GestureHandles.add(paramGestureHandler);
    return paramGestureHandler;
  }
  
  private class GestureHandlerHandle
    extends Handle
  {
    public boolean consumeGesture;
    public final int flags;
    public final GestureDetector.GestureHandler handler;
    
    public GestureHandlerHandle(GestureDetector.GestureHandler paramGestureHandler, int paramInt)
    {
      super();
      this.flags = paramInt;
      this.handler = paramGestureHandler;
    }
    
    protected void onClose(int paramInt)
    {
      GestureDetectorImpl.-get1(GestureDetectorImpl.this).remove(this);
    }
  }
  
  static enum GestureSlide
  {
    SLIDE_BOTTOM,  SLIDE_LEFT,  SLIDE_RIGHT,  SLIDE_TOP;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/GestureDetectorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */