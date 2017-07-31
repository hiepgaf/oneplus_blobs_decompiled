package android.inputmethodservice;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyboardView
  extends View
  implements View.OnClickListener
{
  private static final int DEBOUNCE_TIME = 70;
  private static final boolean DEBUG = false;
  private static final int DELAY_AFTER_PREVIEW = 70;
  private static final int DELAY_BEFORE_PREVIEW = 0;
  private static final int[] KEY_DELETE = { -5 };
  private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
  private static final int[] LONG_PRESSABLE_STATE_SET = { 16843324 };
  private static int MAX_NEARBY_KEYS = 12;
  private static final int MSG_LONGPRESS = 4;
  private static final int MSG_REMOVE_PREVIEW = 2;
  private static final int MSG_REPEAT = 3;
  private static final int MSG_SHOW_PREVIEW = 1;
  private static final int MULTITAP_INTERVAL = 800;
  private static final int NOT_A_KEY = -1;
  private static final int REPEAT_INTERVAL = 50;
  private static final int REPEAT_START_DELAY = 400;
  private boolean mAbortKey;
  private AccessibilityManager mAccessibilityManager;
  private AudioManager mAudioManager;
  private float mBackgroundDimAmount;
  private Bitmap mBuffer;
  private Canvas mCanvas;
  private Rect mClipRegion = new Rect(0, 0, 0, 0);
  private final int[] mCoordinates = new int[2];
  private int mCurrentKey = -1;
  private int mCurrentKeyIndex = -1;
  private long mCurrentKeyTime;
  private Rect mDirtyRect = new Rect();
  private boolean mDisambiguateSwipe;
  private int[] mDistances = new int[MAX_NEARBY_KEYS];
  private int mDownKey = -1;
  private long mDownTime;
  private boolean mDrawPending;
  private GestureDetector mGestureDetector;
  Handler mHandler;
  private boolean mHeadsetRequiredToHearPasswordsAnnounced;
  private boolean mInMultiTap;
  private Keyboard.Key mInvalidatedKey;
  private Drawable mKeyBackground;
  private int[] mKeyIndices = new int[12];
  private int mKeyTextColor;
  private int mKeyTextSize;
  private Keyboard mKeyboard;
  private OnKeyboardActionListener mKeyboardActionListener;
  private boolean mKeyboardChanged;
  private Keyboard.Key[] mKeys;
  private int mLabelTextSize;
  private int mLastCodeX;
  private int mLastCodeY;
  private int mLastKey;
  private long mLastKeyTime;
  private long mLastMoveTime;
  private int mLastSentIndex;
  private long mLastTapTime;
  private int mLastX;
  private int mLastY;
  private KeyboardView mMiniKeyboard;
  private Map<Keyboard.Key, View> mMiniKeyboardCache;
  private View mMiniKeyboardContainer;
  private int mMiniKeyboardOffsetX;
  private int mMiniKeyboardOffsetY;
  private boolean mMiniKeyboardOnScreen;
  private int mOldPointerCount = 1;
  private float mOldPointerX;
  private float mOldPointerY;
  private Rect mPadding;
  private Paint mPaint;
  private PopupWindow mPopupKeyboard;
  private int mPopupLayout;
  private View mPopupParent;
  private int mPopupPreviewX;
  private int mPopupPreviewY;
  private int mPopupX;
  private int mPopupY;
  private boolean mPossiblePoly;
  private boolean mPreviewCentered = false;
  private int mPreviewHeight;
  private StringBuilder mPreviewLabel = new StringBuilder(1);
  private int mPreviewOffset;
  private PopupWindow mPreviewPopup;
  private TextView mPreviewText;
  private int mPreviewTextSizeLarge;
  private boolean mProximityCorrectOn;
  private int mProximityThreshold;
  private int mRepeatKeyIndex = -1;
  private int mShadowColor;
  private float mShadowRadius;
  private boolean mShowPreview = true;
  private boolean mShowTouchPoints = true;
  private int mStartX;
  private int mStartY;
  private int mSwipeThreshold;
  private SwipeTracker mSwipeTracker = new SwipeTracker(null);
  private int mTapCount;
  private int mVerticalCorrection;
  
  public KeyboardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 18219140);
  }
  
  public KeyboardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public KeyboardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, android.R.styleable.KeyboardView, paramInt1, paramInt2);
    LayoutInflater localLayoutInflater = (LayoutInflater)paramContext.getSystemService("layout_inflater");
    paramInt2 = 0;
    int i = paramAttributeSet.getIndexCount();
    paramInt1 = 0;
    if (paramInt1 < i)
    {
      int j = paramAttributeSet.getIndex(paramInt1);
      switch (j)
      {
      }
      for (;;)
      {
        paramInt1 += 1;
        break;
        this.mKeyBackground = paramAttributeSet.getDrawable(j);
        continue;
        this.mVerticalCorrection = paramAttributeSet.getDimensionPixelOffset(j, 0);
        continue;
        paramInt2 = paramAttributeSet.getResourceId(j, 0);
        continue;
        this.mPreviewOffset = paramAttributeSet.getDimensionPixelOffset(j, 0);
        continue;
        this.mPreviewHeight = paramAttributeSet.getDimensionPixelSize(j, 80);
        continue;
        this.mKeyTextSize = paramAttributeSet.getDimensionPixelSize(j, 18);
        continue;
        this.mKeyTextColor = paramAttributeSet.getColor(j, -16777216);
        continue;
        this.mLabelTextSize = paramAttributeSet.getDimensionPixelSize(j, 14);
        continue;
        this.mPopupLayout = paramAttributeSet.getResourceId(j, 0);
        continue;
        this.mShadowColor = paramAttributeSet.getColor(j, 0);
        continue;
        this.mShadowRadius = paramAttributeSet.getFloat(j, 0.0F);
      }
    }
    this.mBackgroundDimAmount = this.mContext.obtainStyledAttributes(com.android.internal.R.styleable.Theme).getFloat(2, 0.5F);
    this.mPreviewPopup = new PopupWindow(paramContext);
    if (paramInt2 != 0)
    {
      this.mPreviewText = ((TextView)localLayoutInflater.inflate(paramInt2, null));
      this.mPreviewTextSizeLarge = ((int)this.mPreviewText.getTextSize());
      this.mPreviewPopup.setContentView(this.mPreviewText);
      this.mPreviewPopup.setBackgroundDrawable(null);
    }
    for (;;)
    {
      this.mPreviewPopup.setTouchable(false);
      this.mPopupKeyboard = new PopupWindow(paramContext);
      this.mPopupKeyboard.setBackgroundDrawable(null);
      this.mPopupParent = this;
      this.mPaint = new Paint();
      this.mPaint.setAntiAlias(true);
      this.mPaint.setTextSize(0.0F);
      this.mPaint.setTextAlign(Paint.Align.CENTER);
      this.mPaint.setAlpha(255);
      this.mPadding = new Rect(0, 0, 0, 0);
      this.mMiniKeyboardCache = new HashMap();
      this.mKeyBackground.getPadding(this.mPadding);
      this.mSwipeThreshold = ((int)(getResources().getDisplayMetrics().density * 500.0F));
      this.mDisambiguateSwipe = getResources().getBoolean(17956944);
      this.mAccessibilityManager = AccessibilityManager.getInstance(paramContext);
      this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
      resetMultiTap();
      return;
      this.mShowPreview = false;
    }
  }
  
  private CharSequence adjustCase(CharSequence paramCharSequence)
  {
    Object localObject = paramCharSequence;
    if (this.mKeyboard.isShifted())
    {
      localObject = paramCharSequence;
      if (paramCharSequence != null)
      {
        localObject = paramCharSequence;
        if (paramCharSequence.length() < 3)
        {
          localObject = paramCharSequence;
          if (Character.isLowerCase(paramCharSequence.charAt(0))) {
            localObject = paramCharSequence.toString().toUpperCase();
          }
        }
      }
    }
    return (CharSequence)localObject;
  }
  
  private void checkMultiTap(long paramLong, int paramInt)
  {
    if (paramInt == -1) {
      return;
    }
    Keyboard.Key localKey = this.mKeys[paramInt];
    if (localKey.codes.length > 1)
    {
      this.mInMultiTap = true;
      if ((paramLong < this.mLastTapTime + 800L) && (paramInt == this.mLastSentIndex))
      {
        this.mTapCount = ((this.mTapCount + 1) % localKey.codes.length);
        return;
      }
      this.mTapCount = -1;
      return;
    }
    if ((paramLong > this.mLastTapTime + 800L) || (paramInt != this.mLastSentIndex)) {
      resetMultiTap();
    }
  }
  
  private void computeProximityThreshold(Keyboard paramKeyboard)
  {
    if (paramKeyboard == null) {
      return;
    }
    paramKeyboard = this.mKeys;
    if (paramKeyboard == null) {
      return;
    }
    int k = paramKeyboard.length;
    int j = 0;
    int i = 0;
    while (i < k)
    {
      Object localObject = paramKeyboard[i];
      j += Math.min(((Keyboard.Key)localObject).width, ((Keyboard.Key)localObject).height) + ((Keyboard.Key)localObject).gap;
      i += 1;
    }
    if ((j < 0) || (k == 0)) {
      return;
    }
    this.mProximityThreshold = ((int)(j * 1.4F / k));
    this.mProximityThreshold *= this.mProximityThreshold;
  }
  
  private void detectAndSendKey(int paramInt1, int paramInt2, int paramInt3, long paramLong)
  {
    Keyboard.Key localKey;
    if ((paramInt1 != -1) && (paramInt1 < this.mKeys.length))
    {
      localKey = this.mKeys[paramInt1];
      if (localKey.text != null)
      {
        this.mKeyboardActionListener.onText(localKey.text);
        this.mKeyboardActionListener.onRelease(-1);
        this.mLastSentIndex = paramInt1;
        this.mLastTapTime = paramLong;
      }
    }
    else
    {
      return;
    }
    int i = localKey.codes[0];
    int[] arrayOfInt = new int[MAX_NEARBY_KEYS];
    Arrays.fill(arrayOfInt, -1);
    getKeyIndices(paramInt2, paramInt3, arrayOfInt);
    paramInt2 = i;
    if (this.mInMultiTap)
    {
      if (this.mTapCount == -1) {
        break label165;
      }
      this.mKeyboardActionListener.onKey(-5, KEY_DELETE);
    }
    for (;;)
    {
      paramInt2 = localKey.codes[this.mTapCount];
      this.mKeyboardActionListener.onKey(paramInt2, arrayOfInt);
      this.mKeyboardActionListener.onRelease(paramInt2);
      break;
      label165:
      this.mTapCount = 0;
    }
  }
  
  private void dismissPopupKeyboard()
  {
    if (this.mPopupKeyboard.isShowing())
    {
      this.mPopupKeyboard.dismiss();
      this.mMiniKeyboardOnScreen = false;
      invalidateAllKeys();
    }
  }
  
  private int getKeyIndices(int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    Keyboard.Key[] arrayOfKey = this.mKeys;
    int j = -1;
    int i = -1;
    int k = this.mProximityThreshold + 1;
    Arrays.fill(this.mDistances, Integer.MAX_VALUE);
    int[] arrayOfInt = this.mKeyboard.getNearestKeys(paramInt1, paramInt2);
    int i5 = arrayOfInt.length;
    int n = 0;
    if (n < i5)
    {
      Keyboard.Key localKey = arrayOfKey[arrayOfInt[n]];
      int m = 0;
      boolean bool = localKey.isInside(paramInt1, paramInt2);
      int i1 = j;
      if (bool) {
        i1 = arrayOfInt[n];
      }
      j = m;
      if (this.mProximityCorrectOn)
      {
        m = localKey.squaredDistanceFrom(paramInt1, paramInt2);
        j = m;
        if (m < this.mProximityThreshold) {
          j = m;
        }
      }
      int i2;
      int i3;
      int i6;
      for (;;)
      {
        i2 = i;
        i3 = k;
        if (localKey.codes[0] > 32)
        {
          i6 = localKey.codes.length;
          m = i;
          i = k;
          if (j < k)
          {
            i = j;
            m = arrayOfInt[n];
          }
          if (paramArrayOfInt != null) {
            break label234;
          }
          i3 = i;
          i2 = m;
        }
        do
        {
          n += 1;
          i = i2;
          k = i3;
          j = i1;
          break;
          i2 = i;
          i3 = k;
        } while (!bool);
      }
      label234:
      k = 0;
      for (;;)
      {
        i2 = m;
        i3 = i;
        if (k >= this.mDistances.length) {
          break;
        }
        if (this.mDistances[k] > j)
        {
          System.arraycopy(this.mDistances, k, this.mDistances, k + i6, this.mDistances.length - k - i6);
          System.arraycopy(paramArrayOfInt, k, paramArrayOfInt, k + i6, paramArrayOfInt.length - k - i6);
          int i4 = 0;
          for (;;)
          {
            i2 = m;
            i3 = i;
            if (i4 >= i6) {
              break;
            }
            paramArrayOfInt[(k + i4)] = localKey.codes[i4];
            this.mDistances[(k + i4)] = j;
            i4 += 1;
          }
        }
        k += 1;
      }
    }
    paramInt1 = j;
    if (j == -1) {
      paramInt1 = i;
    }
    return paramInt1;
  }
  
  private CharSequence getPreviewText(Keyboard.Key paramKey)
  {
    int i = 0;
    if (this.mInMultiTap)
    {
      this.mPreviewLabel.setLength(0);
      StringBuilder localStringBuilder = this.mPreviewLabel;
      paramKey = paramKey.codes;
      if (this.mTapCount < 0) {}
      for (;;)
      {
        localStringBuilder.append((char)paramKey[i]);
        return adjustCase(this.mPreviewLabel);
        i = this.mTapCount;
      }
    }
    return adjustCase(paramKey.label);
  }
  
  private void initGestureDetector()
  {
    if (this.mGestureDetector == null)
    {
      this.mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener()
      {
        public boolean onFling(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2)
        {
          if (KeyboardView.-get3(KeyboardView.this)) {
            return false;
          }
          float f1 = Math.abs(paramAnonymousFloat1);
          float f2 = Math.abs(paramAnonymousFloat2);
          float f3 = paramAnonymousMotionEvent2.getX() - paramAnonymousMotionEvent1.getX();
          float f4 = paramAnonymousMotionEvent2.getY() - paramAnonymousMotionEvent1.getY();
          int i = KeyboardView.this.getWidth() / 2;
          int k = KeyboardView.this.getHeight() / 2;
          KeyboardView.-get8(KeyboardView.this).computeCurrentVelocity(1000);
          float f5 = KeyboardView.-get8(KeyboardView.this).getXVelocity();
          float f6 = KeyboardView.-get8(KeyboardView.this).getYVelocity();
          int j = 0;
          if ((paramAnonymousFloat1 > KeyboardView.-get7(KeyboardView.this)) && (f2 < f1) && (f3 > i)) {
            if ((KeyboardView.-get0(KeyboardView.this)) && (f5 < paramAnonymousFloat1 / 4.0F)) {
              i = 1;
            }
          }
          for (;;)
          {
            if (i != 0) {
              KeyboardView.-wrap2(KeyboardView.this, KeyboardView.-get1(KeyboardView.this), KeyboardView.-get5(KeyboardView.this), KeyboardView.-get6(KeyboardView.this), paramAnonymousMotionEvent1.getEventTime());
            }
            return false;
            KeyboardView.this.swipeRight();
            return true;
            if ((paramAnonymousFloat1 < -KeyboardView.-get7(KeyboardView.this)) && (f2 < f1) && (f3 < -i))
            {
              if ((KeyboardView.-get0(KeyboardView.this)) && (f5 > paramAnonymousFloat1 / 4.0F))
              {
                i = 1;
              }
              else
              {
                KeyboardView.this.swipeLeft();
                return true;
              }
            }
            else if ((paramAnonymousFloat2 < -KeyboardView.-get7(KeyboardView.this)) && (f1 < f2) && (f4 < -k))
            {
              if ((KeyboardView.-get0(KeyboardView.this)) && (f6 > paramAnonymousFloat2 / 4.0F))
              {
                i = 1;
              }
              else
              {
                KeyboardView.this.swipeUp();
                return true;
              }
            }
            else
            {
              i = j;
              if (paramAnonymousFloat2 > KeyboardView.-get7(KeyboardView.this))
              {
                i = j;
                if (f1 < f2 / 2.0F)
                {
                  i = j;
                  if (f4 > k)
                  {
                    if ((!KeyboardView.-get0(KeyboardView.this)) || (f6 >= paramAnonymousFloat2 / 4.0F)) {
                      break;
                    }
                    i = 1;
                  }
                }
              }
            }
          }
          KeyboardView.this.swipeDown();
          return true;
        }
      });
      this.mGestureDetector.setIsLongpressEnabled(false);
    }
  }
  
  private void onBufferDraw()
  {
    if ((this.mBuffer == null) || (this.mKeyboardChanged))
    {
      if ((this.mBuffer == null) || ((this.mKeyboardChanged) && ((this.mBuffer.getWidth() != getWidth()) || (this.mBuffer.getHeight() != getHeight()))))
      {
        this.mBuffer = Bitmap.createBitmap(Math.max(1, getWidth()), Math.max(1, getHeight()), Bitmap.Config.ARGB_8888);
        this.mCanvas = new Canvas(this.mBuffer);
      }
      invalidateAllKeys();
      this.mKeyboardChanged = false;
    }
    Canvas localCanvas = this.mCanvas;
    localCanvas.clipRect(this.mDirtyRect, Region.Op.REPLACE);
    if (this.mKeyboard == null) {
      return;
    }
    Paint localPaint = this.mPaint;
    Drawable localDrawable = this.mKeyBackground;
    Object localObject = this.mClipRegion;
    Rect localRect1 = this.mPadding;
    int k = this.mPaddingLeft;
    int m = this.mPaddingTop;
    Keyboard.Key[] arrayOfKey = this.mKeys;
    Keyboard.Key localKey1 = this.mInvalidatedKey;
    localPaint.setColor(this.mKeyTextColor);
    int j = 0;
    int i = j;
    if (localKey1 != null)
    {
      i = j;
      if (localCanvas.getClipBounds((Rect)localObject))
      {
        i = j;
        if (localKey1.x + k - 1 <= ((Rect)localObject).left)
        {
          i = j;
          if (localKey1.y + m - 1 <= ((Rect)localObject).top)
          {
            i = j;
            if (localKey1.x + localKey1.width + k + 1 >= ((Rect)localObject).right)
            {
              i = j;
              if (localKey1.y + localKey1.height + m + 1 >= ((Rect)localObject).bottom) {
                i = 1;
              }
            }
          }
        }
      }
    }
    localCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    int n = arrayOfKey.length;
    j = 0;
    while (j < n)
    {
      Keyboard.Key localKey2 = arrayOfKey[j];
      if ((i != 0) && (localKey1 != localKey2))
      {
        j += 1;
      }
      else
      {
        localDrawable.setState(localKey2.getCurrentDrawableState());
        if (localKey2.label == null)
        {
          localObject = null;
          label370:
          Rect localRect2 = localDrawable.getBounds();
          if ((localKey2.width != localRect2.right) || (localKey2.height != localRect2.bottom)) {
            localDrawable.setBounds(0, 0, localKey2.width, localKey2.height);
          }
          localCanvas.translate(localKey2.x + k, localKey2.y + m);
          localDrawable.draw(localCanvas);
          if (localObject == null) {
            break label660;
          }
          if ((((String)localObject).length() <= 1) || (localKey2.codes.length >= 2)) {
            break label638;
          }
          localPaint.setTextSize(this.mLabelTextSize);
          localPaint.setTypeface(Typeface.DEFAULT_BOLD);
          label492:
          localPaint.setShadowLayer(this.mShadowRadius, 0.0F, 0.0F, this.mShadowColor);
          localCanvas.drawText((String)localObject, (localKey2.width - localRect1.left - localRect1.right) / 2 + localRect1.left, (localKey2.height - localRect1.top - localRect1.bottom) / 2 + (localPaint.getTextSize() - localPaint.descent()) / 2.0F + localRect1.top, localPaint);
          localPaint.setShadowLayer(0.0F, 0.0F, 0.0F, 0);
        }
        for (;;)
        {
          localCanvas.translate(-localKey2.x - k, -localKey2.y - m);
          break;
          localObject = adjustCase(localKey2.label).toString();
          break label370;
          label638:
          localPaint.setTextSize(this.mKeyTextSize);
          localPaint.setTypeface(Typeface.DEFAULT);
          break label492;
          label660:
          if (localKey2.icon != null)
          {
            int i1 = (localKey2.width - localRect1.left - localRect1.right - localKey2.icon.getIntrinsicWidth()) / 2 + localRect1.left;
            int i2 = (localKey2.height - localRect1.top - localRect1.bottom - localKey2.icon.getIntrinsicHeight()) / 2 + localRect1.top;
            localCanvas.translate(i1, i2);
            localKey2.icon.setBounds(0, 0, localKey2.icon.getIntrinsicWidth(), localKey2.icon.getIntrinsicHeight());
            localKey2.icon.draw(localCanvas);
            localCanvas.translate(-i1, -i2);
          }
        }
      }
    }
    this.mInvalidatedKey = null;
    if (this.mMiniKeyboardOnScreen)
    {
      localPaint.setColor((int)(this.mBackgroundDimAmount * 255.0F) << 24);
      localCanvas.drawRect(0.0F, 0.0F, getWidth(), getHeight(), localPaint);
    }
    this.mDrawPending = false;
    this.mDirtyRect.setEmpty();
  }
  
  private boolean onModifiedTouchEvent(MotionEvent paramMotionEvent, boolean paramBoolean)
  {
    int m = (int)paramMotionEvent.getX() - this.mPaddingLeft;
    int j = (int)paramMotionEvent.getY() - this.mPaddingTop;
    int i = j;
    if (j >= -this.mVerticalCorrection) {
      i = j + this.mVerticalCorrection;
    }
    j = paramMotionEvent.getAction();
    long l = paramMotionEvent.getEventTime();
    int n = getKeyIndices(m, i, null);
    this.mPossiblePoly = paramBoolean;
    if (j == 0) {
      this.mSwipeTracker.clear();
    }
    this.mSwipeTracker.addMovement(paramMotionEvent);
    if ((this.mAbortKey) && (j != 0) && (j != 3)) {
      return true;
    }
    if (this.mGestureDetector.onTouchEvent(paramMotionEvent))
    {
      showPreview(-1);
      this.mHandler.removeMessages(3);
      this.mHandler.removeMessages(4);
      return true;
    }
    if ((this.mMiniKeyboardOnScreen) && (j != 3)) {
      return true;
    }
    int k;
    switch (j)
    {
    default: 
      k = m;
    }
    for (;;)
    {
      this.mLastX = k;
      this.mLastY = i;
      return true;
      this.mAbortKey = false;
      this.mStartX = m;
      this.mStartY = i;
      this.mLastCodeX = m;
      this.mLastCodeY = i;
      this.mLastKeyTime = 0L;
      this.mCurrentKeyTime = 0L;
      this.mLastKey = -1;
      this.mCurrentKey = n;
      this.mDownKey = n;
      this.mDownTime = paramMotionEvent.getEventTime();
      this.mLastMoveTime = this.mDownTime;
      checkMultiTap(l, n);
      Object localObject = this.mKeyboardActionListener;
      if (n != -1) {}
      for (j = this.mKeys[n].codes[0];; j = 0)
      {
        ((OnKeyboardActionListener)localObject).onPress(j);
        if ((this.mCurrentKey < 0) || (!this.mKeys[this.mCurrentKey].repeatable)) {
          break label405;
        }
        this.mRepeatKeyIndex = this.mCurrentKey;
        localObject = this.mHandler.obtainMessage(3);
        this.mHandler.sendMessageDelayed((Message)localObject, 400L);
        repeatKey();
        if (!this.mAbortKey) {
          break label405;
        }
        this.mRepeatKeyIndex = -1;
        k = m;
        break;
      }
      label405:
      if (this.mCurrentKey != -1)
      {
        paramMotionEvent = this.mHandler.obtainMessage(4, paramMotionEvent);
        this.mHandler.sendMessageDelayed(paramMotionEvent, LONGPRESS_TIMEOUT);
      }
      showPreview(n);
      k = m;
      continue;
      k = 0;
      j = k;
      if (n != -1)
      {
        if (this.mCurrentKey != -1) {
          break label554;
        }
        this.mCurrentKey = n;
        this.mCurrentKeyTime = (l - this.mDownTime);
        j = k;
      }
      for (;;)
      {
        if (j == 0)
        {
          this.mHandler.removeMessages(4);
          if (n != -1)
          {
            paramMotionEvent = this.mHandler.obtainMessage(4, paramMotionEvent);
            this.mHandler.sendMessageDelayed(paramMotionEvent, LONGPRESS_TIMEOUT);
          }
        }
        showPreview(this.mCurrentKey);
        this.mLastMoveTime = l;
        k = m;
        break;
        label554:
        if (n == this.mCurrentKey)
        {
          this.mCurrentKeyTime += l - this.mLastMoveTime;
          j = 1;
        }
        else
        {
          j = k;
          if (this.mRepeatKeyIndex == -1)
          {
            resetMultiTap();
            this.mLastKey = this.mCurrentKey;
            this.mLastCodeX = this.mLastX;
            this.mLastCodeY = this.mLastY;
            this.mLastKeyTime = (this.mCurrentKeyTime + l - this.mLastMoveTime);
            this.mCurrentKey = n;
            this.mCurrentKeyTime = 0L;
            j = k;
          }
        }
      }
      removeMessages();
      if (n == this.mCurrentKey)
      {
        this.mCurrentKeyTime += l - this.mLastMoveTime;
        label688:
        k = m;
        j = i;
        if (this.mCurrentKeyTime < this.mLastKeyTime)
        {
          k = m;
          j = i;
          if (this.mCurrentKeyTime < 70L)
          {
            k = m;
            j = i;
            if (this.mLastKey != -1)
            {
              this.mCurrentKey = this.mLastKey;
              k = this.mLastCodeX;
              j = this.mLastCodeY;
            }
          }
        }
        showPreview(-1);
        Arrays.fill(this.mKeyIndices, -1);
        if ((this.mRepeatKeyIndex == -1) && (!this.mMiniKeyboardOnScreen)) {
          break label847;
        }
      }
      for (;;)
      {
        invalidateKey(n);
        this.mRepeatKeyIndex = -1;
        i = j;
        break;
        resetMultiTap();
        this.mLastKey = this.mCurrentKey;
        this.mLastKeyTime = (this.mCurrentKeyTime + l - this.mLastMoveTime);
        this.mCurrentKey = n;
        this.mCurrentKeyTime = 0L;
        break label688;
        label847:
        if (!this.mAbortKey) {
          detectAndSendKey(this.mCurrentKey, k, j, l);
        }
      }
      removeMessages();
      dismissPopupKeyboard();
      this.mAbortKey = true;
      showPreview(-1);
      invalidateKey(this.mCurrentKey);
      k = m;
    }
  }
  
  private boolean openPopupIfRequired(MotionEvent paramMotionEvent)
  {
    if (this.mPopupLayout == 0) {
      return false;
    }
    if ((this.mCurrentKey < 0) || (this.mCurrentKey >= this.mKeys.length)) {
      return false;
    }
    boolean bool = onLongPress(this.mKeys[this.mCurrentKey]);
    if (bool)
    {
      this.mAbortKey = true;
      showPreview(-1);
    }
    return bool;
  }
  
  private void removeMessages()
  {
    if (this.mHandler != null)
    {
      this.mHandler.removeMessages(3);
      this.mHandler.removeMessages(4);
      this.mHandler.removeMessages(1);
    }
  }
  
  private boolean repeatKey()
  {
    Keyboard.Key localKey = this.mKeys[this.mRepeatKeyIndex];
    detectAndSendKey(this.mCurrentKey, localKey.x, localKey.y, this.mLastTapTime);
    return true;
  }
  
  private void resetMultiTap()
  {
    this.mLastSentIndex = -1;
    this.mTapCount = 0;
    this.mLastTapTime = -1L;
    this.mInMultiTap = false;
  }
  
  private void sendAccessibilityEventForUnicodeCharacter(int paramInt1, int paramInt2)
  {
    int i = 0;
    AccessibilityEvent localAccessibilityEvent;
    String str;
    if (this.mAccessibilityManager.isEnabled())
    {
      localAccessibilityEvent = AccessibilityEvent.obtain(paramInt1);
      onInitializeAccessibilityEvent(localAccessibilityEvent);
      if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "speak_password", 0, -3) != 0) {
        i = 1;
      }
      if ((i == 0) && (!this.mAudioManager.isBluetoothA2dpOn()) && (!this.mAudioManager.isWiredHeadsetOn())) {
        break label271;
      }
      switch (paramInt2)
      {
      default: 
        str = String.valueOf((char)paramInt2);
      }
    }
    for (;;)
    {
      localAccessibilityEvent.getText().add(str);
      this.mAccessibilityManager.sendAccessibilityEvent(localAccessibilityEvent);
      return;
      str = this.mContext.getString(17040585);
      continue;
      str = this.mContext.getString(17040586);
      continue;
      str = this.mContext.getString(17040587);
      continue;
      str = this.mContext.getString(17040588);
      continue;
      str = this.mContext.getString(17040589);
      continue;
      str = this.mContext.getString(17040590);
      continue;
      str = this.mContext.getString(17040591);
      continue;
      label271:
      if (!this.mHeadsetRequiredToHearPasswordsAnnounced)
      {
        if (paramInt1 == 256) {
          this.mHeadsetRequiredToHearPasswordsAnnounced = true;
        }
        str = this.mContext.getString(17040598);
      }
      else
      {
        str = this.mContext.getString(17040599);
      }
    }
  }
  
  private void showKey(int paramInt)
  {
    PopupWindow localPopupWindow = this.mPreviewPopup;
    Object localObject1 = this.mKeys;
    if ((paramInt < 0) || (paramInt >= this.mKeys.length)) {
      return;
    }
    Keyboard.Key localKey = localObject1[paramInt];
    label75:
    int i;
    if (localKey.icon != null)
    {
      Object localObject2 = this.mPreviewText;
      if (localKey.iconPreview != null)
      {
        localObject1 = localKey.iconPreview;
        ((TextView)localObject2).setCompoundDrawables(null, null, null, (Drawable)localObject1);
        this.mPreviewText.setText(null);
        this.mPreviewText.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        paramInt = Math.max(this.mPreviewText.getMeasuredWidth(), localKey.width + this.mPreviewText.getPaddingLeft() + this.mPreviewText.getPaddingRight());
        i = this.mPreviewHeight;
        localObject1 = this.mPreviewText.getLayoutParams();
        if (localObject1 != null)
        {
          ((ViewGroup.LayoutParams)localObject1).width = paramInt;
          ((ViewGroup.LayoutParams)localObject1).height = i;
        }
        if (this.mPreviewCentered) {
          break label515;
        }
        this.mPopupPreviewX = (localKey.x - this.mPreviewText.getPaddingLeft() + this.mPaddingLeft);
        this.mPopupPreviewY = (localKey.y - i + this.mPreviewOffset);
        label196:
        this.mHandler.removeMessages(2);
        getLocationInWindow(this.mCoordinates);
        localObject1 = this.mCoordinates;
        localObject1[0] += this.mMiniKeyboardOffsetX;
        localObject1 = this.mCoordinates;
        localObject1[1] += this.mMiniKeyboardOffsetY;
        localObject2 = this.mPreviewText.getBackground();
        if (localKey.popupResId == 0) {
          break label547;
        }
        localObject1 = LONG_PRESSABLE_STATE_SET;
        label265:
        ((Drawable)localObject2).setState((int[])localObject1);
        this.mPopupPreviewX += this.mCoordinates[0];
        this.mPopupPreviewY += this.mCoordinates[1];
        getLocationOnScreen(this.mCoordinates);
        if (this.mPopupPreviewY + this.mCoordinates[1] < 0)
        {
          if (localKey.x + localKey.width > getWidth() / 2) {
            break label554;
          }
          this.mPopupPreviewX += (int)(localKey.width * 2.5D);
          label364:
          this.mPopupPreviewY += i;
        }
        if (!localPopupWindow.isShowing()) {
          break label577;
        }
        localPopupWindow.update(this.mPopupPreviewX, this.mPopupPreviewY, paramInt, i);
      }
    }
    for (;;)
    {
      this.mPreviewText.setVisibility(0);
      return;
      localObject1 = localKey.icon;
      break;
      this.mPreviewText.setCompoundDrawables(null, null, null, null);
      this.mPreviewText.setText(getPreviewText(localKey));
      if ((localKey.label.length() > 1) && (localKey.codes.length < 2))
      {
        this.mPreviewText.setTextSize(0, this.mKeyTextSize);
        this.mPreviewText.setTypeface(Typeface.DEFAULT_BOLD);
        break label75;
      }
      this.mPreviewText.setTextSize(0, this.mPreviewTextSizeLarge);
      this.mPreviewText.setTypeface(Typeface.DEFAULT);
      break label75;
      label515:
      this.mPopupPreviewX = (160 - this.mPreviewText.getMeasuredWidth() / 2);
      this.mPopupPreviewY = (-this.mPreviewText.getMeasuredHeight());
      break label196;
      label547:
      localObject1 = EMPTY_STATE_SET;
      break label265;
      label554:
      this.mPopupPreviewX -= (int)(localKey.width * 2.5D);
      break label364;
      label577:
      localPopupWindow.setWidth(paramInt);
      localPopupWindow.setHeight(i);
      localPopupWindow.showAtLocation(this.mPopupParent, 0, this.mPopupPreviewX, this.mPopupPreviewY);
    }
  }
  
  private void showPreview(int paramInt)
  {
    int i = this.mCurrentKeyIndex;
    PopupWindow localPopupWindow = this.mPreviewPopup;
    this.mCurrentKeyIndex = paramInt;
    Object localObject1 = this.mKeys;
    Object localObject2;
    if (i != this.mCurrentKeyIndex) {
      if ((i != -1) && (localObject1.length > i))
      {
        localObject2 = localObject1[i];
        if (this.mCurrentKeyIndex != -1) {
          break label243;
        }
      }
    }
    label243:
    for (boolean bool = true;; bool = false)
    {
      ((Keyboard.Key)localObject2).onReleased(bool);
      invalidateKey(i);
      int j = localObject2.codes[0];
      sendAccessibilityEventForUnicodeCharacter(256, j);
      sendAccessibilityEventForUnicodeCharacter(65536, j);
      if ((this.mCurrentKeyIndex != -1) && (localObject1.length > this.mCurrentKeyIndex))
      {
        localObject1 = localObject1[this.mCurrentKeyIndex];
        ((Keyboard.Key)localObject1).onPressed();
        invalidateKey(this.mCurrentKeyIndex);
        j = localObject1.codes[0];
        sendAccessibilityEventForUnicodeCharacter(128, j);
        sendAccessibilityEventForUnicodeCharacter(32768, j);
      }
      if ((i != this.mCurrentKeyIndex) && (this.mShowPreview))
      {
        this.mHandler.removeMessages(1);
        if ((localPopupWindow.isShowing()) && (paramInt == -1)) {
          this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(2), 70L);
        }
        if (paramInt != -1)
        {
          if ((!localPopupWindow.isShowing()) || (this.mPreviewText.getVisibility() != 0)) {
            break;
          }
          showKey(paramInt);
        }
      }
      return;
    }
    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, paramInt, 0), 0L);
  }
  
  public void closing()
  {
    if (this.mPreviewPopup.isShowing()) {
      this.mPreviewPopup.dismiss();
    }
    removeMessages();
    dismissPopupKeyboard();
    this.mBuffer = null;
    this.mCanvas = null;
    this.mMiniKeyboardCache.clear();
  }
  
  public Keyboard getKeyboard()
  {
    return this.mKeyboard;
  }
  
  protected OnKeyboardActionListener getOnKeyboardActionListener()
  {
    return this.mKeyboardActionListener;
  }
  
  public boolean handleBack()
  {
    if (this.mPopupKeyboard.isShowing())
    {
      dismissPopupKeyboard();
      return true;
    }
    return false;
  }
  
  public void invalidateAllKeys()
  {
    this.mDirtyRect.union(0, 0, getWidth(), getHeight());
    this.mDrawPending = true;
    invalidate();
  }
  
  public void invalidateKey(int paramInt)
  {
    if (this.mKeys == null) {
      return;
    }
    if ((paramInt < 0) || (paramInt >= this.mKeys.length)) {
      return;
    }
    Keyboard.Key localKey = this.mKeys[paramInt];
    this.mInvalidatedKey = localKey;
    this.mDirtyRect.union(localKey.x + this.mPaddingLeft, localKey.y + this.mPaddingTop, localKey.x + localKey.width + this.mPaddingLeft, localKey.y + localKey.height + this.mPaddingTop);
    onBufferDraw();
    invalidate(localKey.x + this.mPaddingLeft, localKey.y + this.mPaddingTop, localKey.x + localKey.width + this.mPaddingLeft, localKey.y + localKey.height + this.mPaddingTop);
  }
  
  public boolean isPreviewEnabled()
  {
    return this.mShowPreview;
  }
  
  public boolean isProximityCorrectionEnabled()
  {
    return this.mProximityCorrectOn;
  }
  
  public boolean isShifted()
  {
    if (this.mKeyboard != null) {
      return this.mKeyboard.isShifted();
    }
    return false;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    initGestureDetector();
    if (this.mHandler == null) {
      this.mHandler = new Handler()
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          switch (paramAnonymousMessage.what)
          {
          default: 
          case 1: 
          case 2: 
          case 3: 
            do
            {
              return;
              KeyboardView.-wrap4(KeyboardView.this, paramAnonymousMessage.arg1);
              return;
              KeyboardView.-get4(KeyboardView.this).setVisibility(4);
              return;
            } while (!KeyboardView.-wrap1(KeyboardView.this));
            sendMessageDelayed(Message.obtain(this, 3), 50L);
            return;
          }
          KeyboardView.-wrap0(KeyboardView.this, (MotionEvent)paramAnonymousMessage.obj);
        }
      };
    }
  }
  
  public void onClick(View paramView)
  {
    dismissPopupKeyboard();
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    closing();
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if ((this.mDrawPending) || (this.mBuffer == null)) {}
    for (;;)
    {
      onBufferDraw();
      do
      {
        paramCanvas.drawBitmap(this.mBuffer, 0.0F, 0.0F, null);
        return;
      } while (!this.mKeyboardChanged);
    }
  }
  
  public boolean onHoverEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mAccessibilityManager.isTouchExplorationEnabled()) && (paramMotionEvent.getPointerCount() == 1))
    {
      switch (paramMotionEvent.getAction())
      {
      }
      for (;;)
      {
        return onTouchEvent(paramMotionEvent);
        paramMotionEvent.setAction(0);
        continue;
        paramMotionEvent.setAction(2);
        continue;
        paramMotionEvent.setAction(1);
      }
    }
    return true;
  }
  
  protected boolean onLongPress(Keyboard.Key paramKey)
  {
    int i = paramKey.popupResId;
    if (i != 0)
    {
      this.mMiniKeyboardContainer = ((View)this.mMiniKeyboardCache.get(paramKey));
      Object localObject;
      int j;
      label217:
      int k;
      if (this.mMiniKeyboardContainer == null)
      {
        this.mMiniKeyboardContainer = ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(this.mPopupLayout, null);
        this.mMiniKeyboard = ((KeyboardView)this.mMiniKeyboardContainer.findViewById(16908326));
        localObject = this.mMiniKeyboardContainer.findViewById(16908327);
        if (localObject != null) {
          ((View)localObject).setOnClickListener(this);
        }
        this.mMiniKeyboard.setOnKeyboardActionListener(new OnKeyboardActionListener()
        {
          public void onKey(int paramAnonymousInt, int[] paramAnonymousArrayOfInt)
          {
            KeyboardView.-get2(KeyboardView.this).onKey(paramAnonymousInt, paramAnonymousArrayOfInt);
            KeyboardView.-wrap3(KeyboardView.this);
          }
          
          public void onPress(int paramAnonymousInt)
          {
            KeyboardView.-get2(KeyboardView.this).onPress(paramAnonymousInt);
          }
          
          public void onRelease(int paramAnonymousInt)
          {
            KeyboardView.-get2(KeyboardView.this).onRelease(paramAnonymousInt);
          }
          
          public void onText(CharSequence paramAnonymousCharSequence)
          {
            KeyboardView.-get2(KeyboardView.this).onText(paramAnonymousCharSequence);
            KeyboardView.-wrap3(KeyboardView.this);
          }
          
          public void swipeDown() {}
          
          public void swipeLeft() {}
          
          public void swipeRight() {}
          
          public void swipeUp() {}
        });
        if (paramKey.popupCharacters != null)
        {
          localObject = getContext();
          CharSequence localCharSequence = paramKey.popupCharacters;
          j = getPaddingLeft();
          localObject = new Keyboard((Context)localObject, i, localCharSequence, -1, getPaddingRight() + j);
          this.mMiniKeyboard.setKeyboard((Keyboard)localObject);
          this.mMiniKeyboard.setPopupParent(this);
          this.mMiniKeyboardContainer.measure(View.MeasureSpec.makeMeasureSpec(getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getHeight(), Integer.MIN_VALUE));
          this.mMiniKeyboardCache.put(paramKey, this.mMiniKeyboardContainer);
          getLocationInWindow(this.mCoordinates);
          this.mPopupX = (paramKey.x + this.mPaddingLeft);
          this.mPopupY = (paramKey.y + this.mPaddingTop);
          this.mPopupX = (this.mPopupX + paramKey.width - this.mMiniKeyboardContainer.getMeasuredWidth());
          this.mPopupY -= this.mMiniKeyboardContainer.getMeasuredHeight();
          j = this.mPopupX + this.mMiniKeyboardContainer.getPaddingRight() + this.mCoordinates[0];
          k = this.mPopupY + this.mMiniKeyboardContainer.getPaddingBottom() + this.mCoordinates[1];
          paramKey = this.mMiniKeyboard;
          if (j >= 0) {
            break label458;
          }
        }
      }
      label458:
      for (i = 0;; i = j)
      {
        paramKey.setPopupOffset(i, k);
        this.mMiniKeyboard.setShifted(isShifted());
        this.mPopupKeyboard.setContentView(this.mMiniKeyboardContainer);
        this.mPopupKeyboard.setWidth(this.mMiniKeyboardContainer.getMeasuredWidth());
        this.mPopupKeyboard.setHeight(this.mMiniKeyboardContainer.getMeasuredHeight());
        this.mPopupKeyboard.showAtLocation(this, 0, j, k);
        this.mMiniKeyboardOnScreen = true;
        invalidateAllKeys();
        return true;
        localObject = new Keyboard(getContext(), i);
        break;
        this.mMiniKeyboard = ((KeyboardView)this.mMiniKeyboardContainer.findViewById(16908326));
        break label217;
      }
    }
    return false;
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.mKeyboard == null)
    {
      setMeasuredDimension(this.mPaddingLeft + this.mPaddingRight, this.mPaddingTop + this.mPaddingBottom);
      return;
    }
    int i = this.mKeyboard.getMinWidth() + this.mPaddingLeft + this.mPaddingRight;
    paramInt2 = i;
    if (View.MeasureSpec.getSize(paramInt1) < i + 10) {
      paramInt2 = View.MeasureSpec.getSize(paramInt1);
    }
    setMeasuredDimension(paramInt2, this.mKeyboard.getHeight() + this.mPaddingTop + this.mPaddingBottom);
  }
  
  public void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mKeyboard != null) {
      this.mKeyboard.resize(paramInt1, paramInt2);
    }
    this.mBuffer = null;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getPointerCount();
    int j = paramMotionEvent.getAction();
    long l = paramMotionEvent.getEventTime();
    boolean bool;
    if (i != this.mOldPointerCount) {
      if (i == 1)
      {
        MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 0, paramMotionEvent.getX(), paramMotionEvent.getY(), paramMotionEvent.getMetaState());
        bool = onModifiedTouchEvent(localMotionEvent, false);
        localMotionEvent.recycle();
        if (j == 1) {
          bool = onModifiedTouchEvent(paramMotionEvent, true);
        }
      }
    }
    for (;;)
    {
      this.mOldPointerCount = i;
      return bool;
      paramMotionEvent = MotionEvent.obtain(l, l, 1, this.mOldPointerX, this.mOldPointerY, paramMotionEvent.getMetaState());
      bool = onModifiedTouchEvent(paramMotionEvent, true);
      paramMotionEvent.recycle();
      continue;
      if (i == 1)
      {
        bool = onModifiedTouchEvent(paramMotionEvent, false);
        this.mOldPointerX = paramMotionEvent.getX();
        this.mOldPointerY = paramMotionEvent.getY();
      }
      else
      {
        bool = true;
      }
    }
  }
  
  public void setKeyboard(Keyboard paramKeyboard)
  {
    if (this.mKeyboard != null) {
      showPreview(-1);
    }
    removeMessages();
    this.mKeyboard = paramKeyboard;
    List localList = this.mKeyboard.getKeys();
    this.mKeys = ((Keyboard.Key[])localList.toArray(new Keyboard.Key[localList.size()]));
    requestLayout();
    this.mKeyboardChanged = true;
    invalidateAllKeys();
    computeProximityThreshold(paramKeyboard);
    this.mMiniKeyboardCache.clear();
    this.mAbortKey = true;
  }
  
  public void setOnKeyboardActionListener(OnKeyboardActionListener paramOnKeyboardActionListener)
  {
    this.mKeyboardActionListener = paramOnKeyboardActionListener;
  }
  
  public void setPopupOffset(int paramInt1, int paramInt2)
  {
    this.mMiniKeyboardOffsetX = paramInt1;
    this.mMiniKeyboardOffsetY = paramInt2;
    if (this.mPreviewPopup.isShowing()) {
      this.mPreviewPopup.dismiss();
    }
  }
  
  public void setPopupParent(View paramView)
  {
    this.mPopupParent = paramView;
  }
  
  public void setPreviewEnabled(boolean paramBoolean)
  {
    this.mShowPreview = paramBoolean;
  }
  
  public void setProximityCorrectionEnabled(boolean paramBoolean)
  {
    this.mProximityCorrectOn = paramBoolean;
  }
  
  public boolean setShifted(boolean paramBoolean)
  {
    if ((this.mKeyboard != null) && (this.mKeyboard.setShifted(paramBoolean)))
    {
      invalidateAllKeys();
      return true;
    }
    return false;
  }
  
  public void setVerticalCorrection(int paramInt) {}
  
  protected void swipeDown()
  {
    this.mKeyboardActionListener.swipeDown();
  }
  
  protected void swipeLeft()
  {
    this.mKeyboardActionListener.swipeLeft();
  }
  
  protected void swipeRight()
  {
    this.mKeyboardActionListener.swipeRight();
  }
  
  protected void swipeUp()
  {
    this.mKeyboardActionListener.swipeUp();
  }
  
  public static abstract interface OnKeyboardActionListener
  {
    public abstract void onKey(int paramInt, int[] paramArrayOfInt);
    
    public abstract void onPress(int paramInt);
    
    public abstract void onRelease(int paramInt);
    
    public abstract void onText(CharSequence paramCharSequence);
    
    public abstract void swipeDown();
    
    public abstract void swipeLeft();
    
    public abstract void swipeRight();
    
    public abstract void swipeUp();
  }
  
  private static class SwipeTracker
  {
    static final int LONGEST_PAST_TIME = 200;
    static final int NUM_PAST = 4;
    final long[] mPastTime = new long[4];
    final float[] mPastX = new float[4];
    final float[] mPastY = new float[4];
    float mXVelocity;
    float mYVelocity;
    
    private void addPoint(float paramFloat1, float paramFloat2, long paramLong)
    {
      int j = -1;
      long[] arrayOfLong = this.mPastTime;
      int i = 0;
      for (;;)
      {
        if ((i >= 4) || (arrayOfLong[i] == 0L))
        {
          int k = j;
          if (i == 4)
          {
            k = j;
            if (j < 0) {
              k = 0;
            }
          }
          j = k;
          if (k == i) {
            j = k - 1;
          }
          float[] arrayOfFloat1 = this.mPastX;
          float[] arrayOfFloat2 = this.mPastY;
          k = i;
          if (j >= 0)
          {
            k = j + 1;
            int m = 4 - j - 1;
            System.arraycopy(arrayOfFloat1, k, arrayOfFloat1, 0, m);
            System.arraycopy(arrayOfFloat2, k, arrayOfFloat2, 0, m);
            System.arraycopy(arrayOfLong, k, arrayOfLong, 0, m);
            k = i - (j + 1);
          }
          arrayOfFloat1[k] = paramFloat1;
          arrayOfFloat2[k] = paramFloat2;
          arrayOfLong[k] = paramLong;
          i = k + 1;
          if (i < 4) {
            arrayOfLong[i] = 0L;
          }
          return;
        }
        if (arrayOfLong[i] < paramLong - 200L) {
          j = i;
        }
        i += 1;
      }
    }
    
    public void addMovement(MotionEvent paramMotionEvent)
    {
      long l = paramMotionEvent.getEventTime();
      int j = paramMotionEvent.getHistorySize();
      int i = 0;
      while (i < j)
      {
        addPoint(paramMotionEvent.getHistoricalX(i), paramMotionEvent.getHistoricalY(i), paramMotionEvent.getHistoricalEventTime(i));
        i += 1;
      }
      addPoint(paramMotionEvent.getX(), paramMotionEvent.getY(), l);
    }
    
    public void clear()
    {
      this.mPastTime[0] = 0L;
    }
    
    public void computeCurrentVelocity(int paramInt)
    {
      computeCurrentVelocity(paramInt, Float.MAX_VALUE);
    }
    
    public void computeCurrentVelocity(int paramInt, float paramFloat)
    {
      float[] arrayOfFloat1 = this.mPastX;
      float[] arrayOfFloat2 = this.mPastY;
      long[] arrayOfLong = this.mPastTime;
      float f4 = arrayOfFloat1[0];
      float f5 = arrayOfFloat2[0];
      long l = arrayOfLong[0];
      float f3 = 0.0F;
      float f2 = 0.0F;
      int i = 0;
      int j;
      label64:
      int k;
      float f1;
      if ((i >= 4) || (arrayOfLong[i] == 0L))
      {
        j = 1;
        if (j >= i) {
          break label201;
        }
        k = (int)(arrayOfLong[j] - l);
        if (k != 0) {
          break label111;
        }
        f1 = f2;
      }
      for (;;)
      {
        j += 1;
        f2 = f1;
        break label64;
        i += 1;
        break;
        label111:
        f1 = (arrayOfFloat1[j] - f4) / k * paramInt;
        if (f3 == 0.0F) {}
        for (;;)
        {
          f3 = (arrayOfFloat2[j] - f5) / k * paramInt;
          if (f2 != 0.0F) {
            break label182;
          }
          f2 = f3;
          f3 = f1;
          f1 = f2;
          break;
          f1 = (f3 + f1) * 0.5F;
        }
        label182:
        f2 = (f2 + f3) * 0.5F;
        f3 = f1;
        f1 = f2;
      }
      label201:
      if (f3 < 0.0F)
      {
        f1 = Math.max(f3, -paramFloat);
        this.mXVelocity = f1;
        if (f2 >= 0.0F) {
          break label252;
        }
      }
      label252:
      for (paramFloat = Math.max(f2, -paramFloat);; paramFloat = Math.min(f2, paramFloat))
      {
        this.mYVelocity = paramFloat;
        return;
        f1 = Math.min(f3, paramFloat);
        break;
      }
    }
    
    public float getXVelocity()
    {
      return this.mXVelocity;
    }
    
    public float getYVelocity()
    {
      return this.mYVelocity;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/inputmethodservice/KeyboardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */