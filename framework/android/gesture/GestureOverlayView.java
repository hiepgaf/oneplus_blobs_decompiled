package android.gesture;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.android.internal.R.styleable;
import java.util.ArrayList;

public class GestureOverlayView
  extends FrameLayout
{
  private static final boolean DITHER_FLAG = true;
  private static final int FADE_ANIMATION_RATE = 16;
  private static final boolean GESTURE_RENDERING_ANTIALIAS = true;
  public static final int GESTURE_STROKE_TYPE_MULTIPLE = 1;
  public static final int GESTURE_STROKE_TYPE_SINGLE = 0;
  public static final int ORIENTATION_HORIZONTAL = 0;
  public static final int ORIENTATION_VERTICAL = 1;
  private int mCertainGestureColor = 65280;
  private int mCurrentColor;
  private Gesture mCurrentGesture;
  private float mCurveEndX;
  private float mCurveEndY;
  private long mFadeDuration = 150L;
  private boolean mFadeEnabled = true;
  private long mFadeOffset = 420L;
  private float mFadingAlpha = 1.0F;
  private boolean mFadingHasStarted;
  private final FadeOutRunnable mFadingOut = new FadeOutRunnable(null);
  private long mFadingStart;
  private final Paint mGesturePaint = new Paint();
  private float mGestureStrokeAngleThreshold = 40.0F;
  private float mGestureStrokeLengthThreshold = 50.0F;
  private float mGestureStrokeSquarenessTreshold = 0.275F;
  private int mGestureStrokeType = 0;
  private float mGestureStrokeWidth = 12.0F;
  private boolean mGestureVisible = true;
  private boolean mHandleGestureActions;
  private boolean mInterceptEvents = true;
  private final AccelerateDecelerateInterpolator mInterpolator = new AccelerateDecelerateInterpolator();
  private final Rect mInvalidRect = new Rect();
  private int mInvalidateExtraBorder = 10;
  private boolean mIsFadingOut = false;
  private boolean mIsGesturing = false;
  private boolean mIsListeningForGestures;
  private final ArrayList<OnGestureListener> mOnGestureListeners = new ArrayList();
  private final ArrayList<OnGesturePerformedListener> mOnGesturePerformedListeners = new ArrayList();
  private final ArrayList<OnGesturingListener> mOnGesturingListeners = new ArrayList();
  private int mOrientation = 1;
  private final Path mPath = new Path();
  private boolean mPreviousWasGesturing = false;
  private boolean mResetGesture;
  private final ArrayList<GesturePoint> mStrokeBuffer = new ArrayList(100);
  private float mTotalLength;
  private int mUncertainGestureColor = 1224736512;
  private float mX;
  private float mY;
  
  public GestureOverlayView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public GestureOverlayView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 18219034);
  }
  
  public GestureOverlayView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public GestureOverlayView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.GestureOverlayView, paramInt1, paramInt2);
    this.mGestureStrokeWidth = paramContext.getFloat(1, this.mGestureStrokeWidth);
    this.mInvalidateExtraBorder = Math.max(1, (int)this.mGestureStrokeWidth - 1);
    this.mCertainGestureColor = paramContext.getColor(2, this.mCertainGestureColor);
    this.mUncertainGestureColor = paramContext.getColor(3, this.mUncertainGestureColor);
    this.mFadeDuration = paramContext.getInt(5, (int)this.mFadeDuration);
    this.mFadeOffset = paramContext.getInt(4, (int)this.mFadeOffset);
    this.mGestureStrokeType = paramContext.getInt(6, this.mGestureStrokeType);
    this.mGestureStrokeLengthThreshold = paramContext.getFloat(7, this.mGestureStrokeLengthThreshold);
    this.mGestureStrokeAngleThreshold = paramContext.getFloat(9, this.mGestureStrokeAngleThreshold);
    this.mGestureStrokeSquarenessTreshold = paramContext.getFloat(8, this.mGestureStrokeSquarenessTreshold);
    this.mInterceptEvents = paramContext.getBoolean(10, this.mInterceptEvents);
    this.mFadeEnabled = paramContext.getBoolean(11, this.mFadeEnabled);
    this.mOrientation = paramContext.getInt(0, this.mOrientation);
    paramContext.recycle();
    init();
  }
  
  private void cancelGesture(MotionEvent paramMotionEvent)
  {
    ArrayList localArrayList = this.mOnGestureListeners;
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      ((OnGestureListener)localArrayList.get(i)).onGestureCancelled(this, paramMotionEvent);
      i += 1;
    }
    clear(false);
  }
  
  private void clear(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    setPaintAlpha(255);
    removeCallbacks(this.mFadingOut);
    this.mResetGesture = false;
    this.mFadingOut.fireActionPerformed = paramBoolean2;
    this.mFadingOut.resetMultipleStrokes = false;
    if ((paramBoolean1) && (this.mCurrentGesture != null))
    {
      this.mFadingAlpha = 1.0F;
      this.mIsFadingOut = true;
      this.mFadingHasStarted = false;
      this.mFadingStart = (AnimationUtils.currentAnimationTimeMillis() + this.mFadeOffset);
      postDelayed(this.mFadingOut, this.mFadeOffset);
      return;
    }
    this.mFadingAlpha = 1.0F;
    this.mIsFadingOut = false;
    this.mFadingHasStarted = false;
    if (paramBoolean3)
    {
      this.mCurrentGesture = null;
      this.mPath.rewind();
      invalidate();
      return;
    }
    if (paramBoolean2)
    {
      postDelayed(this.mFadingOut, this.mFadeOffset);
      return;
    }
    if (this.mGestureStrokeType == 1)
    {
      this.mFadingOut.resetMultipleStrokes = true;
      postDelayed(this.mFadingOut, this.mFadeOffset);
      return;
    }
    this.mCurrentGesture = null;
    this.mPath.rewind();
    invalidate();
  }
  
  private void fireOnGesturePerformed()
  {
    ArrayList localArrayList = this.mOnGesturePerformedListeners;
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      ((OnGesturePerformedListener)localArrayList.get(i)).onGesturePerformed(this, this.mCurrentGesture);
      i += 1;
    }
  }
  
  private void init()
  {
    setWillNotDraw(false);
    Paint localPaint = this.mGesturePaint;
    localPaint.setAntiAlias(true);
    localPaint.setColor(this.mCertainGestureColor);
    localPaint.setStyle(Paint.Style.STROKE);
    localPaint.setStrokeJoin(Paint.Join.ROUND);
    localPaint.setStrokeCap(Paint.Cap.ROUND);
    localPaint.setStrokeWidth(this.mGestureStrokeWidth);
    localPaint.setDither(true);
    this.mCurrentColor = this.mCertainGestureColor;
    setPaintAlpha(255);
  }
  
  private boolean processEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getAction())
    {
    }
    do
    {
      do
      {
        do
        {
          return false;
          touchDown(paramMotionEvent);
          invalidate();
          return true;
        } while (!this.mIsListeningForGestures);
        paramMotionEvent = touchMove(paramMotionEvent);
        if (paramMotionEvent != null) {
          invalidate(paramMotionEvent);
        }
        return true;
      } while (!this.mIsListeningForGestures);
      touchUp(paramMotionEvent, false);
      invalidate();
      return true;
    } while (!this.mIsListeningForGestures);
    touchUp(paramMotionEvent, true);
    invalidate();
    return true;
  }
  
  private void setCurrentColor(int paramInt)
  {
    this.mCurrentColor = paramInt;
    if (this.mFadingHasStarted) {
      setPaintAlpha((int)(this.mFadingAlpha * 255.0F));
    }
    for (;;)
    {
      invalidate();
      return;
      setPaintAlpha(255);
    }
  }
  
  private void setPaintAlpha(int paramInt)
  {
    int i = this.mCurrentColor;
    this.mGesturePaint.setColor(this.mCurrentColor << 8 >>> 8 | (i >>> 24) * (paramInt + (paramInt >> 7)) >> 8 << 24);
  }
  
  private void touchDown(MotionEvent paramMotionEvent)
  {
    this.mIsListeningForGestures = true;
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    this.mX = f1;
    this.mY = f2;
    this.mTotalLength = 0.0F;
    this.mIsGesturing = false;
    if ((this.mGestureStrokeType == 0) || (this.mResetGesture))
    {
      if (this.mHandleGestureActions) {
        setCurrentColor(this.mUncertainGestureColor);
      }
      this.mResetGesture = false;
      this.mCurrentGesture = null;
      this.mPath.rewind();
      if (!this.mFadingHasStarted) {
        break label267;
      }
      cancelClearAnimation();
    }
    for (;;)
    {
      if (this.mCurrentGesture == null) {
        this.mCurrentGesture = new Gesture();
      }
      this.mStrokeBuffer.add(new GesturePoint(f1, f2, paramMotionEvent.getEventTime()));
      this.mPath.moveTo(f1, f2);
      int i = this.mInvalidateExtraBorder;
      this.mInvalidRect.set((int)f1 - i, (int)f2 - i, (int)f1 + i, (int)f2 + i);
      this.mCurveEndX = f1;
      this.mCurveEndY = f2;
      ArrayList localArrayList = this.mOnGestureListeners;
      int j = localArrayList.size();
      i = 0;
      while (i < j)
      {
        ((OnGestureListener)localArrayList.get(i)).onGestureStarted(this, paramMotionEvent);
        i += 1;
      }
      if (((this.mCurrentGesture != null) && (this.mCurrentGesture.getStrokesCount() != 0)) || (!this.mHandleGestureActions)) {
        break;
      }
      setCurrentColor(this.mUncertainGestureColor);
      break;
      label267:
      if (this.mIsFadingOut)
      {
        setPaintAlpha(255);
        this.mIsFadingOut = false;
        this.mFadingHasStarted = false;
        removeCallbacks(this.mFadingOut);
      }
    }
  }
  
  private Rect touchMove(MotionEvent paramMotionEvent)
  {
    Object localObject = null;
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    float f3 = this.mX;
    float f4 = this.mY;
    float f5 = Math.abs(f1 - f3);
    float f6 = Math.abs(f2 - f4);
    if ((f5 >= 3.0F) || (f6 >= 3.0F))
    {
      Rect localRect = this.mInvalidRect;
      int i = this.mInvalidateExtraBorder;
      localRect.set((int)this.mCurveEndX - i, (int)this.mCurveEndY - i, (int)this.mCurveEndX + i, (int)this.mCurveEndY + i);
      float f7 = (f1 + f3) / 2.0F;
      this.mCurveEndX = f7;
      float f8 = (f2 + f4) / 2.0F;
      this.mCurveEndY = f8;
      this.mPath.quadTo(f3, f4, f7, f8);
      localRect.union((int)f3 - i, (int)f4 - i, (int)f3 + i, (int)f4 + i);
      localRect.union((int)f7 - i, (int)f8 - i, (int)f7 + i, (int)f8 + i);
      this.mX = f1;
      this.mY = f2;
      this.mStrokeBuffer.add(new GesturePoint(f1, f2, paramMotionEvent.getEventTime()));
      if ((!this.mHandleGestureActions) || (this.mIsGesturing)) {}
      int j;
      do
      {
        do
        {
          ArrayList localArrayList = this.mOnGestureListeners;
          j = localArrayList.size();
          i = 0;
          for (;;)
          {
            localObject = localRect;
            if (i >= j) {
              break;
            }
            ((OnGestureListener)localArrayList.get(i)).onGesture(this, paramMotionEvent);
            i += 1;
          }
          this.mTotalLength += (float)Math.hypot(f5, f6);
        } while (this.mTotalLength <= this.mGestureStrokeLengthThreshold);
        localObject = GestureUtils.computeOrientedBoundingBox(this.mStrokeBuffer);
        f2 = Math.abs(((OrientedBoundingBox)localObject).orientation);
        f1 = f2;
        if (f2 > 90.0F) {
          f1 = 180.0F - f2;
        }
        if (((OrientedBoundingBox)localObject).squareness > this.mGestureStrokeSquarenessTreshold) {
          break;
        }
        if (this.mOrientation != 1) {
          break label465;
        }
      } while (f1 >= this.mGestureStrokeAngleThreshold);
      for (;;)
      {
        this.mIsGesturing = true;
        setCurrentColor(this.mCertainGestureColor);
        localObject = this.mOnGesturingListeners;
        j = ((ArrayList)localObject).size();
        i = 0;
        while (i < j)
        {
          ((OnGesturingListener)((ArrayList)localObject).get(i)).onGesturingStarted(this);
          i += 1;
        }
        break;
        label465:
        if (f1 <= this.mGestureStrokeAngleThreshold) {
          break;
        }
      }
    }
    return (Rect)localObject;
  }
  
  private void touchUp(MotionEvent paramMotionEvent, boolean paramBoolean)
  {
    this.mIsListeningForGestures = false;
    int j;
    int i;
    boolean bool;
    if (this.mCurrentGesture != null)
    {
      this.mCurrentGesture.addStroke(new GestureStroke(this.mStrokeBuffer));
      if (!paramBoolean)
      {
        ArrayList localArrayList = this.mOnGestureListeners;
        j = localArrayList.size();
        i = 0;
        while (i < j)
        {
          ((OnGestureListener)localArrayList.get(i)).onGestureEnded(this, paramMotionEvent);
          i += 1;
        }
        if (this.mHandleGestureActions)
        {
          paramBoolean = this.mFadeEnabled;
          if (!this.mHandleGestureActions) {
            break label176;
          }
          bool = this.mIsGesturing;
          label103:
          clear(paramBoolean, bool, false);
        }
      }
    }
    for (;;)
    {
      this.mStrokeBuffer.clear();
      this.mPreviousWasGesturing = this.mIsGesturing;
      this.mIsGesturing = false;
      paramMotionEvent = this.mOnGesturingListeners;
      j = paramMotionEvent.size();
      i = 0;
      while (i < j)
      {
        ((OnGesturingListener)paramMotionEvent.get(i)).onGesturingEnded(this);
        i += 1;
      }
      paramBoolean = false;
      break;
      label176:
      bool = false;
      break label103;
      cancelGesture(paramMotionEvent);
      continue;
      cancelGesture(paramMotionEvent);
    }
  }
  
  public void addOnGestureListener(OnGestureListener paramOnGestureListener)
  {
    this.mOnGestureListeners.add(paramOnGestureListener);
  }
  
  public void addOnGesturePerformedListener(OnGesturePerformedListener paramOnGesturePerformedListener)
  {
    this.mOnGesturePerformedListeners.add(paramOnGesturePerformedListener);
    if (this.mOnGesturePerformedListeners.size() > 0) {
      this.mHandleGestureActions = true;
    }
  }
  
  public void addOnGesturingListener(OnGesturingListener paramOnGesturingListener)
  {
    this.mOnGesturingListeners.add(paramOnGesturingListener);
  }
  
  public void cancelClearAnimation()
  {
    setPaintAlpha(255);
    this.mIsFadingOut = false;
    this.mFadingHasStarted = false;
    removeCallbacks(this.mFadingOut);
    this.mPath.rewind();
    this.mCurrentGesture = null;
  }
  
  public void cancelGesture()
  {
    this.mIsListeningForGestures = false;
    this.mCurrentGesture.addStroke(new GestureStroke(this.mStrokeBuffer));
    long l = SystemClock.uptimeMillis();
    Object localObject = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
    ArrayList localArrayList = this.mOnGestureListeners;
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      ((OnGestureListener)localArrayList.get(i)).onGestureCancelled(this, (MotionEvent)localObject);
      i += 1;
    }
    ((MotionEvent)localObject).recycle();
    clear(false);
    this.mIsGesturing = false;
    this.mPreviousWasGesturing = false;
    this.mStrokeBuffer.clear();
    localObject = this.mOnGesturingListeners;
    j = ((ArrayList)localObject).size();
    i = 0;
    while (i < j)
    {
      ((OnGesturingListener)((ArrayList)localObject).get(i)).onGesturingEnded(this);
      i += 1;
    }
  }
  
  public void clear(boolean paramBoolean)
  {
    clear(paramBoolean, false, true);
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (isEnabled())
    {
      if ((this.mIsGesturing) || ((this.mCurrentGesture != null) && (this.mCurrentGesture.getStrokesCount() > 0) && (this.mPreviousWasGesturing))) {}
      for (boolean bool = this.mInterceptEvents;; bool = false)
      {
        processEvent(paramMotionEvent);
        if (bool) {
          paramMotionEvent.setAction(3);
        }
        super.dispatchTouchEvent(paramMotionEvent);
        return true;
      }
    }
    return super.dispatchTouchEvent(paramMotionEvent);
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if ((this.mCurrentGesture != null) && (this.mGestureVisible)) {
      paramCanvas.drawPath(this.mPath, this.mGesturePaint);
    }
  }
  
  public ArrayList<GesturePoint> getCurrentStroke()
  {
    return this.mStrokeBuffer;
  }
  
  public long getFadeOffset()
  {
    return this.mFadeOffset;
  }
  
  public Gesture getGesture()
  {
    return this.mCurrentGesture;
  }
  
  public int getGestureColor()
  {
    return this.mCertainGestureColor;
  }
  
  public Paint getGesturePaint()
  {
    return this.mGesturePaint;
  }
  
  public Path getGesturePath()
  {
    return this.mPath;
  }
  
  public Path getGesturePath(Path paramPath)
  {
    paramPath.set(this.mPath);
    return paramPath;
  }
  
  public float getGestureStrokeAngleThreshold()
  {
    return this.mGestureStrokeAngleThreshold;
  }
  
  public float getGestureStrokeLengthThreshold()
  {
    return this.mGestureStrokeLengthThreshold;
  }
  
  public float getGestureStrokeSquarenessTreshold()
  {
    return this.mGestureStrokeSquarenessTreshold;
  }
  
  public int getGestureStrokeType()
  {
    return this.mGestureStrokeType;
  }
  
  public float getGestureStrokeWidth()
  {
    return this.mGestureStrokeWidth;
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public int getUncertainGestureColor()
  {
    return this.mUncertainGestureColor;
  }
  
  public boolean isEventsInterceptionEnabled()
  {
    return this.mInterceptEvents;
  }
  
  public boolean isFadeEnabled()
  {
    return this.mFadeEnabled;
  }
  
  public boolean isGestureVisible()
  {
    return this.mGestureVisible;
  }
  
  public boolean isGesturing()
  {
    return this.mIsGesturing;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    cancelClearAnimation();
  }
  
  public void removeAllOnGestureListeners()
  {
    this.mOnGestureListeners.clear();
  }
  
  public void removeAllOnGesturePerformedListeners()
  {
    this.mOnGesturePerformedListeners.clear();
    this.mHandleGestureActions = false;
  }
  
  public void removeAllOnGesturingListeners()
  {
    this.mOnGesturingListeners.clear();
  }
  
  public void removeOnGestureListener(OnGestureListener paramOnGestureListener)
  {
    this.mOnGestureListeners.remove(paramOnGestureListener);
  }
  
  public void removeOnGesturePerformedListener(OnGesturePerformedListener paramOnGesturePerformedListener)
  {
    this.mOnGesturePerformedListeners.remove(paramOnGesturePerformedListener);
    if (this.mOnGesturePerformedListeners.size() <= 0) {
      this.mHandleGestureActions = false;
    }
  }
  
  public void removeOnGesturingListener(OnGesturingListener paramOnGesturingListener)
  {
    this.mOnGesturingListeners.remove(paramOnGesturingListener);
  }
  
  public void setEventsInterceptionEnabled(boolean paramBoolean)
  {
    this.mInterceptEvents = paramBoolean;
  }
  
  public void setFadeEnabled(boolean paramBoolean)
  {
    this.mFadeEnabled = paramBoolean;
  }
  
  public void setFadeOffset(long paramLong)
  {
    this.mFadeOffset = paramLong;
  }
  
  public void setGesture(Gesture paramGesture)
  {
    if (this.mCurrentGesture != null) {
      clear(false);
    }
    setCurrentColor(this.mCertainGestureColor);
    this.mCurrentGesture = paramGesture;
    paramGesture = this.mCurrentGesture.toPath();
    RectF localRectF = new RectF();
    paramGesture.computeBounds(localRectF, true);
    this.mPath.rewind();
    this.mPath.addPath(paramGesture, -localRectF.left + (getWidth() - localRectF.width()) / 2.0F, -localRectF.top + (getHeight() - localRectF.height()) / 2.0F);
    this.mResetGesture = true;
    invalidate();
  }
  
  public void setGestureColor(int paramInt)
  {
    this.mCertainGestureColor = paramInt;
  }
  
  public void setGestureStrokeAngleThreshold(float paramFloat)
  {
    this.mGestureStrokeAngleThreshold = paramFloat;
  }
  
  public void setGestureStrokeLengthThreshold(float paramFloat)
  {
    this.mGestureStrokeLengthThreshold = paramFloat;
  }
  
  public void setGestureStrokeSquarenessTreshold(float paramFloat)
  {
    this.mGestureStrokeSquarenessTreshold = paramFloat;
  }
  
  public void setGestureStrokeType(int paramInt)
  {
    this.mGestureStrokeType = paramInt;
  }
  
  public void setGestureStrokeWidth(float paramFloat)
  {
    this.mGestureStrokeWidth = paramFloat;
    this.mInvalidateExtraBorder = Math.max(1, (int)paramFloat - 1);
    this.mGesturePaint.setStrokeWidth(paramFloat);
  }
  
  public void setGestureVisible(boolean paramBoolean)
  {
    this.mGestureVisible = paramBoolean;
  }
  
  public void setOrientation(int paramInt)
  {
    this.mOrientation = paramInt;
  }
  
  public void setUncertainGestureColor(int paramInt)
  {
    this.mUncertainGestureColor = paramInt;
  }
  
  private class FadeOutRunnable
    implements Runnable
  {
    boolean fireActionPerformed;
    boolean resetMultipleStrokes;
    
    private FadeOutRunnable() {}
    
    public void run()
    {
      long l;
      if (GestureOverlayView.-get4(GestureOverlayView.this))
      {
        l = AnimationUtils.currentAnimationTimeMillis() - GestureOverlayView.-get2(GestureOverlayView.this);
        if (l > GestureOverlayView.-get0(GestureOverlayView.this))
        {
          if (this.fireActionPerformed) {
            GestureOverlayView.-wrap0(GestureOverlayView.this);
          }
          GestureOverlayView.-set4(GestureOverlayView.this, false);
          GestureOverlayView.-set3(GestureOverlayView.this, false);
          GestureOverlayView.-set2(GestureOverlayView.this, false);
          GestureOverlayView.-get5(GestureOverlayView.this).rewind();
          GestureOverlayView.-set0(GestureOverlayView.this, null);
          GestureOverlayView.-wrap1(GestureOverlayView.this, 255);
        }
      }
      for (;;)
      {
        GestureOverlayView.this.invalidate();
        return;
        GestureOverlayView.-set2(GestureOverlayView.this, true);
        float f = Math.max(0.0F, Math.min(1.0F, (float)l / (float)GestureOverlayView.-get0(GestureOverlayView.this)));
        GestureOverlayView.-set1(GestureOverlayView.this, 1.0F - GestureOverlayView.-get3(GestureOverlayView.this).getInterpolation(f));
        GestureOverlayView.-wrap1(GestureOverlayView.this, (int)(GestureOverlayView.-get1(GestureOverlayView.this) * 255.0F));
        GestureOverlayView.this.postDelayed(this, 16L);
        continue;
        if (this.resetMultipleStrokes)
        {
          GestureOverlayView.-set5(GestureOverlayView.this, true);
        }
        else
        {
          GestureOverlayView.-wrap0(GestureOverlayView.this);
          GestureOverlayView.-set2(GestureOverlayView.this, false);
          GestureOverlayView.-get5(GestureOverlayView.this).rewind();
          GestureOverlayView.-set0(GestureOverlayView.this, null);
          GestureOverlayView.-set4(GestureOverlayView.this, false);
          GestureOverlayView.-wrap1(GestureOverlayView.this, 255);
        }
      }
    }
  }
  
  public static abstract interface OnGestureListener
  {
    public abstract void onGesture(GestureOverlayView paramGestureOverlayView, MotionEvent paramMotionEvent);
    
    public abstract void onGestureCancelled(GestureOverlayView paramGestureOverlayView, MotionEvent paramMotionEvent);
    
    public abstract void onGestureEnded(GestureOverlayView paramGestureOverlayView, MotionEvent paramMotionEvent);
    
    public abstract void onGestureStarted(GestureOverlayView paramGestureOverlayView, MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnGesturePerformedListener
  {
    public abstract void onGesturePerformed(GestureOverlayView paramGestureOverlayView, Gesture paramGesture);
  }
  
  public static abstract interface OnGesturingListener
  {
    public abstract void onGesturingEnded(GestureOverlayView paramGestureOverlayView);
    
    public abstract void onGesturingStarted(GestureOverlayView paramGestureOverlayView);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/gesture/GestureOverlayView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */