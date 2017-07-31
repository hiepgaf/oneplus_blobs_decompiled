package android.media.tv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class TvView
  extends ViewGroup
{
  private static final boolean DEBUG = false;
  private static final WeakReference<TvView> NULL_TV_VIEW = new WeakReference(null);
  private static final String TAG = "TvView";
  private static final int ZORDER_MEDIA = 0;
  private static final int ZORDER_MEDIA_OVERLAY = 1;
  private static final int ZORDER_ON_TOP = 2;
  private static WeakReference<TvView> sMainTvView = NULL_TV_VIEW;
  private static final Object sMainTvViewLock = new Object();
  private final AttributeSet mAttrs;
  private TvInputCallback mCallback;
  private Boolean mCaptionEnabled;
  private final int mDefStyleAttr;
  private final TvInputManager.Session.FinishedInputEventCallback mFinishedInputEventCallback = new TvInputManager.Session.FinishedInputEventCallback()
  {
    public void onFinishedInputEvent(Object paramAnonymousObject, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean) {
        return;
      }
      paramAnonymousObject = (InputEvent)paramAnonymousObject;
      if (TvView.this.dispatchUnhandledInputEvent((InputEvent)paramAnonymousObject)) {
        return;
      }
      ViewRootImpl localViewRootImpl = TvView.this.getViewRootImpl();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchUnhandledInputEvent((InputEvent)paramAnonymousObject);
      }
    }
  };
  private final Handler mHandler = new Handler();
  private OnUnhandledInputEventListener mOnUnhandledInputEventListener;
  private boolean mOverlayViewCreated;
  private Rect mOverlayViewFrame;
  private final Queue<Pair<String, Bundle>> mPendingAppPrivateCommands = new ArrayDeque();
  private TvInputManager.Session mSession;
  private MySessionCallback mSessionCallback;
  private Float mStreamVolume;
  private Surface mSurface;
  private boolean mSurfaceChanged;
  private int mSurfaceFormat;
  private int mSurfaceHeight;
  private final SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback()
  {
    public void surfaceChanged(SurfaceHolder paramAnonymousSurfaceHolder, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      TvView.-set6(TvView.this, paramAnonymousInt1);
      TvView.-set12(TvView.this, paramAnonymousInt2);
      TvView.-set7(TvView.this, paramAnonymousInt3);
      TvView.-set5(TvView.this, true);
      TvView.-wrap1(TvView.this, TvView.-get8(TvView.this), TvView.-get10(TvView.this), TvView.-get9(TvView.this));
    }
    
    public void surfaceCreated(SurfaceHolder paramAnonymousSurfaceHolder)
    {
      TvView.-set4(TvView.this, paramAnonymousSurfaceHolder.getSurface());
      TvView.-wrap4(TvView.this, TvView.-get6(TvView.this));
    }
    
    public void surfaceDestroyed(SurfaceHolder paramAnonymousSurfaceHolder)
    {
      TvView.-set4(TvView.this, null);
      TvView.-set5(TvView.this, false);
      TvView.-wrap4(TvView.this, null);
    }
  };
  private SurfaceView mSurfaceView;
  private int mSurfaceViewBottom;
  private int mSurfaceViewLeft;
  private int mSurfaceViewRight;
  private int mSurfaceViewTop;
  private int mSurfaceWidth;
  private TimeShiftPositionCallback mTimeShiftPositionCallback;
  private final TvInputManager mTvInputManager;
  private boolean mUseRequestedSurfaceLayout;
  private int mWindowZOrder;
  
  public TvView(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  public TvView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TvView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mAttrs = paramAttributeSet;
    this.mDefStyleAttr = paramInt;
    resetSurfaceView();
    this.mTvInputManager = ((TvInputManager)getContext().getSystemService("tv_input"));
  }
  
  private void createSessionOverlayView()
  {
    if ((this.mSession == null) || (!isAttachedToWindow()) || (this.mOverlayViewCreated) || (this.mWindowZOrder != 0)) {
      return;
    }
    this.mOverlayViewFrame = getViewFrameOnScreen();
    this.mSession.createOverlayView(this, this.mOverlayViewFrame);
    this.mOverlayViewCreated = true;
  }
  
  private void dispatchSurfaceChanged(int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.mSession == null) {
      return;
    }
    this.mSession.dispatchSurfaceChanged(paramInt1, paramInt2, paramInt3);
  }
  
  private void ensurePositionTracking()
  {
    if (this.mSession == null) {
      return;
    }
    TvInputManager.Session localSession = this.mSession;
    if (this.mTimeShiftPositionCallback != null) {}
    for (boolean bool = true;; bool = false)
    {
      localSession.timeShiftEnablePositionTracking(bool);
      return;
    }
  }
  
  private Rect getViewFrameOnScreen()
  {
    int[] arrayOfInt = new int[2];
    getLocationOnScreen(arrayOfInt);
    return new Rect(arrayOfInt[0], arrayOfInt[1], arrayOfInt[0] + getWidth(), arrayOfInt[1] + getHeight());
  }
  
  private void relayoutSessionOverlayView()
  {
    if ((this.mSession == null) || (!isAttachedToWindow()) || (!this.mOverlayViewCreated) || (this.mWindowZOrder != 0)) {
      return;
    }
    Rect localRect = getViewFrameOnScreen();
    if (localRect.equals(this.mOverlayViewFrame)) {
      return;
    }
    this.mSession.relayoutOverlayView(localRect);
    this.mOverlayViewFrame = localRect;
  }
  
  private void removeSessionOverlayView()
  {
    if ((this.mSession != null) && (this.mOverlayViewCreated))
    {
      this.mSession.removeOverlayView();
      this.mOverlayViewCreated = false;
      this.mOverlayViewFrame = null;
      return;
    }
  }
  
  private void resetInternal()
  {
    this.mSessionCallback = null;
    this.mPendingAppPrivateCommands.clear();
    if (this.mSession != null)
    {
      setSessionSurface(null);
      removeSessionOverlayView();
      this.mUseRequestedSurfaceLayout = false;
      this.mSession.release();
      this.mSession = null;
      resetSurfaceView();
    }
  }
  
  private void resetSurfaceView()
  {
    if (this.mSurfaceView != null)
    {
      this.mSurfaceView.getHolder().removeCallback(this.mSurfaceHolderCallback);
      removeView(this.mSurfaceView);
    }
    this.mSurface = null;
    this.mSurfaceView = new SurfaceView(getContext(), this.mAttrs, this.mDefStyleAttr)
    {
      protected void updateWindow(boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
      {
        super.updateWindow(paramAnonymousBoolean1, paramAnonymousBoolean2);
        TvView.-wrap3(TvView.this);
      }
    };
    this.mSurfaceView.setSecure(true);
    this.mSurfaceView.getHolder().addCallback(this.mSurfaceHolderCallback);
    if (this.mWindowZOrder == 1) {
      this.mSurfaceView.setZOrderMediaOverlay(true);
    }
    for (;;)
    {
      addView(this.mSurfaceView);
      return;
      if (this.mWindowZOrder == 2) {
        this.mSurfaceView.setZOrderOnTop(true);
      }
    }
  }
  
  private void setSessionSurface(Surface paramSurface)
  {
    if (this.mSession == null) {
      return;
    }
    this.mSession.setSurface(paramSurface);
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    if (this.mWindowZOrder != 2) {
      paramCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }
    super.dispatchDraw(paramCanvas);
  }
  
  public boolean dispatchGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if (super.dispatchGenericMotionEvent(paramMotionEvent)) {
      return true;
    }
    if (this.mSession == null) {
      return false;
    }
    paramMotionEvent = paramMotionEvent.copy();
    return this.mSession.dispatchInputEvent(paramMotionEvent, paramMotionEvent, this.mFinishedInputEventCallback, this.mHandler) != 0;
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (super.dispatchKeyEvent(paramKeyEvent)) {
      return true;
    }
    if (this.mSession == null) {
      return false;
    }
    paramKeyEvent = paramKeyEvent.copy();
    return this.mSession.dispatchInputEvent(paramKeyEvent, paramKeyEvent, this.mFinishedInputEventCallback, this.mHandler) != 0;
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (super.dispatchTouchEvent(paramMotionEvent)) {
      return true;
    }
    if (this.mSession == null) {
      return false;
    }
    paramMotionEvent = paramMotionEvent.copy();
    return this.mSession.dispatchInputEvent(paramMotionEvent, paramMotionEvent, this.mFinishedInputEventCallback, this.mHandler) != 0;
  }
  
  public boolean dispatchTrackballEvent(MotionEvent paramMotionEvent)
  {
    if (super.dispatchTrackballEvent(paramMotionEvent)) {
      return true;
    }
    if (this.mSession == null) {
      return false;
    }
    paramMotionEvent = paramMotionEvent.copy();
    return this.mSession.dispatchInputEvent(paramMotionEvent, paramMotionEvent, this.mFinishedInputEventCallback, this.mHandler) != 0;
  }
  
  public boolean dispatchUnhandledInputEvent(InputEvent paramInputEvent)
  {
    if ((this.mOnUnhandledInputEventListener != null) && (this.mOnUnhandledInputEventListener.onUnhandledInputEvent(paramInputEvent))) {
      return true;
    }
    return onUnhandledInputEvent(paramInputEvent);
  }
  
  public void dispatchWindowFocusChanged(boolean paramBoolean)
  {
    super.dispatchWindowFocusChanged(paramBoolean);
    Object localObject1 = sMainTvViewLock;
    if (paramBoolean) {}
    try
    {
      if ((this == sMainTvView.get()) && (this.mSession != null)) {
        this.mSession.setMain();
      }
      return;
    }
    finally
    {
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.mWindowZOrder != 2) {
      paramCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }
    super.draw(paramCanvas);
  }
  
  public boolean gatherTransparentRegion(Region paramRegion)
  {
    if ((this.mWindowZOrder != 2) && (paramRegion != null))
    {
      int i = getWidth();
      int j = getHeight();
      if ((i > 0) && (j > 0))
      {
        int[] arrayOfInt = new int[2];
        getLocationInWindow(arrayOfInt);
        int k = arrayOfInt[0];
        int m = arrayOfInt[1];
        paramRegion.op(k, m, k + i, m + j, Region.Op.UNION);
      }
    }
    return super.gatherTransparentRegion(paramRegion);
  }
  
  public String getSelectedTrack(int paramInt)
  {
    if (this.mSession == null) {
      return null;
    }
    return this.mSession.getSelectedTrack(paramInt);
  }
  
  public List<TvTrackInfo> getTracks(int paramInt)
  {
    if (this.mSession == null) {
      return null;
    }
    return this.mSession.getTracks(paramInt);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    createSessionOverlayView();
  }
  
  protected void onDetachedFromWindow()
  {
    removeSessionOverlayView();
    super.onDetachedFromWindow();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mUseRequestedSurfaceLayout)
    {
      this.mSurfaceView.layout(this.mSurfaceViewLeft, this.mSurfaceViewTop, this.mSurfaceViewRight, this.mSurfaceViewBottom);
      return;
    }
    this.mSurfaceView.layout(0, 0, paramInt3 - paramInt1, paramInt4 - paramInt2);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.mSurfaceView.measure(paramInt1, paramInt2);
    int i = this.mSurfaceView.getMeasuredWidth();
    int j = this.mSurfaceView.getMeasuredHeight();
    int k = this.mSurfaceView.getMeasuredState();
    setMeasuredDimension(resolveSizeAndState(i, paramInt1, k), resolveSizeAndState(j, paramInt2, k << 16));
  }
  
  public boolean onUnhandledInputEvent(InputEvent paramInputEvent)
  {
    return false;
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    this.mSurfaceView.setVisibility(paramInt);
    if (paramInt == 0)
    {
      createSessionOverlayView();
      return;
    }
    removeSessionOverlayView();
  }
  
  public void requestUnblockContent(TvContentRating paramTvContentRating)
  {
    unblockContent(paramTvContentRating);
  }
  
  public void reset()
  {
    synchronized (sMainTvViewLock)
    {
      if (this == sMainTvView.get()) {
        sMainTvView = NULL_TV_VIEW;
      }
      resetInternal();
      return;
    }
  }
  
  public void selectTrack(int paramInt, String paramString)
  {
    if (this.mSession != null) {
      this.mSession.selectTrack(paramInt, paramString);
    }
  }
  
  public void sendAppPrivateCommand(String paramString, Bundle paramBundle)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("action cannot be null or an empty string");
    }
    if (this.mSession != null)
    {
      this.mSession.sendAppPrivateCommand(paramString, paramBundle);
      return;
    }
    Log.w("TvView", "sendAppPrivateCommand - session not yet created (action \"" + paramString + "\" pending)");
    this.mPendingAppPrivateCommands.add(Pair.create(paramString, paramBundle));
  }
  
  public void setCallback(TvInputCallback paramTvInputCallback)
  {
    this.mCallback = paramTvInputCallback;
  }
  
  public void setCaptionEnabled(boolean paramBoolean)
  {
    this.mCaptionEnabled = Boolean.valueOf(paramBoolean);
    if (this.mSession != null) {
      this.mSession.setCaptionEnabled(paramBoolean);
    }
  }
  
  public void setMain()
  {
    synchronized (sMainTvViewLock)
    {
      sMainTvView = new WeakReference(this);
      if ((hasWindowFocus()) && (this.mSession != null)) {
        this.mSession.setMain();
      }
      return;
    }
  }
  
  public void setOnUnhandledInputEventListener(OnUnhandledInputEventListener paramOnUnhandledInputEventListener)
  {
    this.mOnUnhandledInputEventListener = paramOnUnhandledInputEventListener;
  }
  
  public void setStreamVolume(float paramFloat)
  {
    this.mStreamVolume = Float.valueOf(paramFloat);
    if (this.mSession == null) {
      return;
    }
    this.mSession.setStreamVolume(paramFloat);
  }
  
  public void setTimeShiftPositionCallback(TimeShiftPositionCallback paramTimeShiftPositionCallback)
  {
    this.mTimeShiftPositionCallback = paramTimeShiftPositionCallback;
    ensurePositionTracking();
  }
  
  public void setZOrderMediaOverlay(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mWindowZOrder = 1;
      removeSessionOverlayView();
    }
    for (;;)
    {
      if (this.mSurfaceView != null)
      {
        this.mSurfaceView.setZOrderOnTop(false);
        this.mSurfaceView.setZOrderMediaOverlay(paramBoolean);
      }
      return;
      this.mWindowZOrder = 0;
      createSessionOverlayView();
    }
  }
  
  public void setZOrderOnTop(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mWindowZOrder = 2;
      removeSessionOverlayView();
    }
    for (;;)
    {
      if (this.mSurfaceView != null)
      {
        this.mSurfaceView.setZOrderMediaOverlay(false);
        this.mSurfaceView.setZOrderOnTop(paramBoolean);
      }
      return;
      this.mWindowZOrder = 0;
      createSessionOverlayView();
    }
  }
  
  public void timeShiftPause()
  {
    if (this.mSession != null) {
      this.mSession.timeShiftPause();
    }
  }
  
  public void timeShiftPlay(String paramString, Uri paramUri)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("inputId cannot be null or an empty string");
    }
    label97:
    do
    {
      synchronized (sMainTvViewLock)
      {
        if (sMainTvView.get() == null) {
          sMainTvView = new WeakReference(this);
        }
        if ((this.mSessionCallback == null) || (!TextUtils.equals(this.mSessionCallback.mInputId, paramString))) {
          break label97;
        }
        if (this.mSession != null)
        {
          this.mSession.timeShiftPlay(paramUri);
          return;
        }
      }
      this.mSessionCallback.mRecordedProgramUri = paramUri;
      return;
      resetInternal();
      this.mSessionCallback = new MySessionCallback(paramString, paramUri);
    } while (this.mTvInputManager == null);
    this.mTvInputManager.createSession(paramString, this.mSessionCallback, this.mHandler);
  }
  
  public void timeShiftResume()
  {
    if (this.mSession != null) {
      this.mSession.timeShiftResume();
    }
  }
  
  public void timeShiftSeekTo(long paramLong)
  {
    if (this.mSession != null) {
      this.mSession.timeShiftSeekTo(paramLong);
    }
  }
  
  public void timeShiftSetPlaybackParams(PlaybackParams paramPlaybackParams)
  {
    if (this.mSession != null) {
      this.mSession.timeShiftSetPlaybackParams(paramPlaybackParams);
    }
  }
  
  public void tune(String paramString, Uri paramUri)
  {
    tune(paramString, paramUri, null);
  }
  
  public void tune(String paramString, Uri paramUri, Bundle paramBundle)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("inputId cannot be null or an empty string");
    }
    label110:
    do
    {
      synchronized (sMainTvViewLock)
      {
        if (sMainTvView.get() == null) {
          sMainTvView = new WeakReference(this);
        }
        if ((this.mSessionCallback == null) || (!TextUtils.equals(this.mSessionCallback.mInputId, paramString))) {
          break label110;
        }
        if (this.mSession != null)
        {
          this.mSession.tune(paramUri, paramBundle);
          return;
        }
      }
      this.mSessionCallback.mChannelUri = paramUri;
      this.mSessionCallback.mTuneParams = paramBundle;
      return;
      resetInternal();
      this.mSessionCallback = new MySessionCallback(paramString, paramUri, paramBundle);
    } while (this.mTvInputManager == null);
    this.mTvInputManager.createSession(paramString, this.mSessionCallback, this.mHandler);
  }
  
  public void unblockContent(TvContentRating paramTvContentRating)
  {
    if (this.mSession != null) {
      this.mSession.unblockContent(paramTvContentRating);
    }
  }
  
  private class MySessionCallback
    extends TvInputManager.SessionCallback
  {
    Uri mChannelUri;
    final String mInputId;
    Uri mRecordedProgramUri;
    Bundle mTuneParams;
    
    MySessionCallback(String paramString, Uri paramUri)
    {
      this.mInputId = paramString;
      this.mRecordedProgramUri = paramUri;
    }
    
    MySessionCallback(String paramString, Uri paramUri, Bundle paramBundle)
    {
      this.mInputId = paramString;
      this.mChannelUri = paramUri;
      this.mTuneParams = paramBundle;
    }
    
    public void onChannelRetuned(TvInputManager.Session paramSession, Uri paramUri)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onChannelRetuned - session not created");
        return;
      }
      if (TvView.-get0(TvView.this) != null) {
        TvView.-get0(TvView.this).onChannelRetuned(this.mInputId, paramUri);
      }
    }
    
    public void onContentAllowed(TvInputManager.Session paramSession)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onContentAllowed - session not created");
        return;
      }
      if (TvView.-get0(TvView.this) != null) {
        TvView.-get0(TvView.this).onContentAllowed(this.mInputId);
      }
    }
    
    public void onContentBlocked(TvInputManager.Session paramSession, TvContentRating paramTvContentRating)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onContentBlocked - session not created");
        return;
      }
      if (TvView.-get0(TvView.this) != null) {
        TvView.-get0(TvView.this).onContentBlocked(this.mInputId, paramTvContentRating);
      }
    }
    
    public void onLayoutSurface(TvInputManager.Session paramSession, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onLayoutSurface - session not created");
        return;
      }
      TvView.-set9(TvView.this, paramInt1);
      TvView.-set11(TvView.this, paramInt2);
      TvView.-set10(TvView.this, paramInt3);
      TvView.-set8(TvView.this, paramInt4);
      TvView.-set13(TvView.this, true);
      TvView.this.requestLayout();
    }
    
    public void onSessionCreated(TvInputManager.Session arg1)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onSessionCreated - session already created");
        if (??? != null) {
          ???.release();
        }
        return;
      }
      TvView.-set2(TvView.this, ???);
      if (??? != null)
      {
        ??? = TvView.-get2(TvView.this).iterator();
        while (???.hasNext())
        {
          Pair localPair = (Pair)???.next();
          TvView.-get3(TvView.this).sendAppPrivateCommand((String)localPair.first, (Bundle)localPair.second);
        }
        TvView.-get2(TvView.this).clear();
      }
      do
      {
        for (;;)
        {
          synchronized (TvView.-get13())
          {
            if ((TvView.this.hasWindowFocus()) && (TvView.this == TvView.-get12().get())) {
              TvView.-get3(TvView.this).setMain();
            }
            if (TvView.-get6(TvView.this) != null)
            {
              TvView.-wrap4(TvView.this, TvView.-get6(TvView.this));
              if (TvView.-get7(TvView.this)) {
                TvView.-wrap1(TvView.this, TvView.-get8(TvView.this), TvView.-get10(TvView.this), TvView.-get9(TvView.this));
              }
            }
            TvView.-wrap0(TvView.this);
            if (TvView.-get5(TvView.this) != null) {
              TvView.-get3(TvView.this).setStreamVolume(TvView.-get5(TvView.this).floatValue());
            }
            if (TvView.-get1(TvView.this) != null) {
              TvView.-get3(TvView.this).setCaptionEnabled(TvView.-get1(TvView.this).booleanValue());
            }
            if (this.mChannelUri != null)
            {
              TvView.-get3(TvView.this).tune(this.mChannelUri, this.mTuneParams);
              TvView.-wrap2(TvView.this);
              return;
            }
          }
          TvView.-get3(TvView.this).timeShiftPlay(this.mRecordedProgramUri);
        }
        TvView.-set3(TvView.this, null);
      } while (TvView.-get0(TvView.this) == null);
      TvView.-get0(TvView.this).onConnectionFailed(this.mInputId);
    }
    
    public void onSessionEvent(TvInputManager.Session paramSession, String paramString, Bundle paramBundle)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onSessionEvent - session not created");
        return;
      }
      if (TvView.-get0(TvView.this) != null) {
        TvView.-get0(TvView.this).onEvent(this.mInputId, paramString, paramBundle);
      }
    }
    
    public void onSessionReleased(TvInputManager.Session paramSession)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onSessionReleased - session not created");
        return;
      }
      TvView.-set0(TvView.this, false);
      TvView.-set1(TvView.this, null);
      TvView.-set3(TvView.this, null);
      TvView.-set2(TvView.this, null);
      if (TvView.-get0(TvView.this) != null) {
        TvView.-get0(TvView.this).onDisconnected(this.mInputId);
      }
    }
    
    public void onTimeShiftCurrentPositionChanged(TvInputManager.Session paramSession, long paramLong)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onTimeShiftCurrentPositionChanged - session not created");
        return;
      }
      if (TvView.-get11(TvView.this) != null) {
        TvView.-get11(TvView.this).onTimeShiftCurrentPositionChanged(this.mInputId, paramLong);
      }
    }
    
    public void onTimeShiftStartPositionChanged(TvInputManager.Session paramSession, long paramLong)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onTimeShiftStartPositionChanged - session not created");
        return;
      }
      if (TvView.-get11(TvView.this) != null) {
        TvView.-get11(TvView.this).onTimeShiftStartPositionChanged(this.mInputId, paramLong);
      }
    }
    
    public void onTimeShiftStatusChanged(TvInputManager.Session paramSession, int paramInt)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onTimeShiftStatusChanged - session not created");
        return;
      }
      if (TvView.-get0(TvView.this) != null) {
        TvView.-get0(TvView.this).onTimeShiftStatusChanged(this.mInputId, paramInt);
      }
    }
    
    public void onTrackSelected(TvInputManager.Session paramSession, int paramInt, String paramString)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onTrackSelected - session not created");
        return;
      }
      if (TvView.-get0(TvView.this) != null) {
        TvView.-get0(TvView.this).onTrackSelected(this.mInputId, paramInt, paramString);
      }
    }
    
    public void onTracksChanged(TvInputManager.Session paramSession, List<TvTrackInfo> paramList)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onTracksChanged - session not created");
        return;
      }
      if (TvView.-get0(TvView.this) != null) {
        TvView.-get0(TvView.this).onTracksChanged(this.mInputId, paramList);
      }
    }
    
    public void onVideoAvailable(TvInputManager.Session paramSession)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onVideoAvailable - session not created");
        return;
      }
      if (TvView.-get0(TvView.this) != null) {
        TvView.-get0(TvView.this).onVideoAvailable(this.mInputId);
      }
    }
    
    public void onVideoSizeChanged(TvInputManager.Session paramSession, int paramInt1, int paramInt2)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onVideoSizeChanged - session not created");
        return;
      }
      if (TvView.-get0(TvView.this) != null) {
        TvView.-get0(TvView.this).onVideoSizeChanged(this.mInputId, paramInt1, paramInt2);
      }
    }
    
    public void onVideoUnavailable(TvInputManager.Session paramSession, int paramInt)
    {
      if (this != TvView.-get4(TvView.this))
      {
        Log.w("TvView", "onVideoUnavailable - session not created");
        return;
      }
      if (TvView.-get0(TvView.this) != null) {
        TvView.-get0(TvView.this).onVideoUnavailable(this.mInputId, paramInt);
      }
    }
  }
  
  public static abstract interface OnUnhandledInputEventListener
  {
    public abstract boolean onUnhandledInputEvent(InputEvent paramInputEvent);
  }
  
  public static abstract class TimeShiftPositionCallback
  {
    public void onTimeShiftCurrentPositionChanged(String paramString, long paramLong) {}
    
    public void onTimeShiftStartPositionChanged(String paramString, long paramLong) {}
  }
  
  public static abstract class TvInputCallback
  {
    public void onChannelRetuned(String paramString, Uri paramUri) {}
    
    public void onConnectionFailed(String paramString) {}
    
    public void onContentAllowed(String paramString) {}
    
    public void onContentBlocked(String paramString, TvContentRating paramTvContentRating) {}
    
    public void onDisconnected(String paramString) {}
    
    public void onEvent(String paramString1, String paramString2, Bundle paramBundle) {}
    
    public void onTimeShiftStatusChanged(String paramString, int paramInt) {}
    
    public void onTrackSelected(String paramString1, int paramInt, String paramString2) {}
    
    public void onTracksChanged(String paramString, List<TvTrackInfo> paramList) {}
    
    public void onVideoAvailable(String paramString) {}
    
    public void onVideoSizeChanged(String paramString, int paramInt1, int paramInt2) {}
    
    public void onVideoUnavailable(String paramString, int paramInt) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/TvView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */