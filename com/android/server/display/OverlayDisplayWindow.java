package com.android.server.display;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.util.Slog;
import android.view.Display;
import android.view.Display.Mode;
import android.view.DisplayInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import com.android.internal.util.DumpUtils.Dump;
import java.io.PrintWriter;

final class OverlayDisplayWindow
  implements DumpUtils.Dump
{
  private static final boolean DEBUG = false;
  private static final String TAG = "OverlayDisplayWindow";
  private final boolean DISABLE_MOVE_AND_RESIZE = false;
  private final float INITIAL_SCALE = 0.5F;
  private final float MAX_SCALE = 1.0F;
  private final float MIN_SCALE = 0.3F;
  private final float WINDOW_ALPHA = 0.8F;
  private final Context mContext;
  private final Display mDefaultDisplay;
  private final DisplayInfo mDefaultDisplayInfo = new DisplayInfo();
  private int mDensityDpi;
  private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener()
  {
    public void onDisplayAdded(int paramAnonymousInt) {}
    
    public void onDisplayChanged(int paramAnonymousInt)
    {
      if (paramAnonymousInt == OverlayDisplayWindow.-get0(OverlayDisplayWindow.this).getDisplayId())
      {
        if (OverlayDisplayWindow.-wrap0(OverlayDisplayWindow.this))
        {
          OverlayDisplayWindow.this.relayout();
          OverlayDisplayWindow.-get3(OverlayDisplayWindow.this).onStateChanged(OverlayDisplayWindow.-get1(OverlayDisplayWindow.this).state);
        }
      }
      else {
        return;
      }
      OverlayDisplayWindow.this.dismiss();
    }
    
    public void onDisplayRemoved(int paramAnonymousInt)
    {
      if (paramAnonymousInt == OverlayDisplayWindow.-get0(OverlayDisplayWindow.this).getDisplayId()) {
        OverlayDisplayWindow.this.dismiss();
      }
    }
  };
  private final DisplayManager mDisplayManager;
  private GestureDetector mGestureDetector;
  private final int mGravity;
  private int mHeight;
  private final Listener mListener;
  private float mLiveScale = 1.0F;
  private float mLiveTranslationX;
  private float mLiveTranslationY;
  private final String mName;
  private final GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener()
  {
    public boolean onScroll(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2)
    {
      paramAnonymousMotionEvent1 = OverlayDisplayWindow.this;
      OverlayDisplayWindow.-set1(paramAnonymousMotionEvent1, OverlayDisplayWindow.-get5(paramAnonymousMotionEvent1) - paramAnonymousFloat1);
      paramAnonymousMotionEvent1 = OverlayDisplayWindow.this;
      OverlayDisplayWindow.-set2(paramAnonymousMotionEvent1, OverlayDisplayWindow.-get6(paramAnonymousMotionEvent1) - paramAnonymousFloat2);
      OverlayDisplayWindow.this.relayout();
      return true;
    }
  };
  private final ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener()
  {
    public boolean onScale(ScaleGestureDetector paramAnonymousScaleGestureDetector)
    {
      OverlayDisplayWindow localOverlayDisplayWindow = OverlayDisplayWindow.this;
      OverlayDisplayWindow.-set0(localOverlayDisplayWindow, OverlayDisplayWindow.-get4(localOverlayDisplayWindow) * paramAnonymousScaleGestureDetector.getScaleFactor());
      OverlayDisplayWindow.this.relayout();
      return true;
    }
  };
  private final View.OnTouchListener mOnTouchListener = new View.OnTouchListener()
  {
    public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
    {
      float f1 = paramAnonymousMotionEvent.getX();
      float f2 = paramAnonymousMotionEvent.getY();
      paramAnonymousMotionEvent.setLocation(paramAnonymousMotionEvent.getRawX(), paramAnonymousMotionEvent.getRawY());
      OverlayDisplayWindow.-get2(OverlayDisplayWindow.this).onTouchEvent(paramAnonymousMotionEvent);
      OverlayDisplayWindow.-get7(OverlayDisplayWindow.this).onTouchEvent(paramAnonymousMotionEvent);
      switch (paramAnonymousMotionEvent.getActionMasked())
      {
      }
      for (;;)
      {
        paramAnonymousMotionEvent.setLocation(f1, f2);
        return true;
        OverlayDisplayWindow.-wrap1(OverlayDisplayWindow.this);
      }
    }
  };
  private ScaleGestureDetector mScaleGestureDetector;
  private final boolean mSecure;
  private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener()
  {
    public void onSurfaceTextureAvailable(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      OverlayDisplayWindow.-get3(OverlayDisplayWindow.this).onWindowCreated(paramAnonymousSurfaceTexture, OverlayDisplayWindow.-get1(OverlayDisplayWindow.this).getMode().getRefreshRate(), OverlayDisplayWindow.-get1(OverlayDisplayWindow.this).presentationDeadlineNanos, OverlayDisplayWindow.-get1(OverlayDisplayWindow.this).state);
    }
    
    public boolean onSurfaceTextureDestroyed(SurfaceTexture paramAnonymousSurfaceTexture)
    {
      OverlayDisplayWindow.-get3(OverlayDisplayWindow.this).onWindowDestroyed();
      return true;
    }
    
    public void onSurfaceTextureSizeChanged(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2) {}
    
    public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymousSurfaceTexture) {}
  };
  private TextureView mTextureView;
  private String mTitle;
  private TextView mTitleTextView;
  private int mWidth;
  private View mWindowContent;
  private final WindowManager mWindowManager;
  private WindowManager.LayoutParams mWindowParams;
  private float mWindowScale;
  private boolean mWindowVisible;
  private int mWindowX;
  private int mWindowY;
  
  public OverlayDisplayWindow(Context paramContext, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean, Listener paramListener)
  {
    this.mContext = paramContext;
    this.mName = paramString;
    this.mGravity = paramInt4;
    this.mSecure = paramBoolean;
    this.mListener = paramListener;
    this.mDisplayManager = ((DisplayManager)paramContext.getSystemService("display"));
    this.mWindowManager = ((WindowManager)paramContext.getSystemService("window"));
    this.mDefaultDisplay = this.mWindowManager.getDefaultDisplay();
    updateDefaultDisplayInfo();
    resize(paramInt1, paramInt2, paramInt3, false);
    createWindow();
  }
  
  private void clearLiveState()
  {
    this.mLiveTranslationX = 0.0F;
    this.mLiveTranslationY = 0.0F;
    this.mLiveScale = 1.0F;
  }
  
  private void createWindow()
  {
    int j = 0;
    this.mWindowContent = LayoutInflater.from(this.mContext).inflate(17367198, null);
    this.mWindowContent.setOnTouchListener(this.mOnTouchListener);
    this.mTextureView = ((TextureView)this.mWindowContent.findViewById(16909260));
    this.mTextureView.setPivotX(0.0F);
    this.mTextureView.setPivotY(0.0F);
    this.mTextureView.getLayoutParams().width = this.mWidth;
    this.mTextureView.getLayoutParams().height = this.mHeight;
    this.mTextureView.setOpaque(false);
    this.mTextureView.setSurfaceTextureListener(this.mSurfaceTextureListener);
    this.mTitleTextView = ((TextView)this.mWindowContent.findViewById(16909261));
    this.mTitleTextView.setText(this.mTitle);
    this.mWindowParams = new WindowManager.LayoutParams(2026);
    WindowManager.LayoutParams localLayoutParams = this.mWindowParams;
    localLayoutParams.flags |= 0x1000328;
    if (this.mSecure)
    {
      localLayoutParams = this.mWindowParams;
      localLayoutParams.flags |= 0x2000;
    }
    localLayoutParams = this.mWindowParams;
    localLayoutParams.privateFlags |= 0x2;
    this.mWindowParams.alpha = 0.8F;
    this.mWindowParams.gravity = 51;
    this.mWindowParams.setTitle(this.mTitle);
    this.mGestureDetector = new GestureDetector(this.mContext, this.mOnGestureListener);
    this.mScaleGestureDetector = new ScaleGestureDetector(this.mContext, this.mOnScaleGestureListener);
    if ((this.mGravity & 0x3) == 3)
    {
      i = 0;
      this.mWindowX = i;
      if ((this.mGravity & 0x30) != 48) {
        break label328;
      }
    }
    label328:
    for (int i = j;; i = this.mDefaultDisplayInfo.logicalHeight)
    {
      this.mWindowY = i;
      this.mWindowScale = 0.5F;
      return;
      i = this.mDefaultDisplayInfo.logicalWidth;
      break;
    }
  }
  
  private void resize(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    this.mDensityDpi = paramInt3;
    this.mTitle = this.mContext.getResources().getString(17040670, new Object[] { this.mName, Integer.valueOf(this.mWidth), Integer.valueOf(this.mHeight), Integer.valueOf(this.mDensityDpi) });
    if (this.mSecure) {
      this.mTitle += this.mContext.getResources().getString(17040671);
    }
    if (paramBoolean) {
      relayout();
    }
  }
  
  private void saveWindowParams()
  {
    this.mWindowX = this.mWindowParams.x;
    this.mWindowY = this.mWindowParams.y;
    this.mWindowScale = this.mTextureView.getScaleX();
    clearLiveState();
  }
  
  private boolean updateDefaultDisplayInfo()
  {
    if (!this.mDefaultDisplay.getDisplayInfo(this.mDefaultDisplayInfo))
    {
      Slog.w("OverlayDisplayWindow", "Cannot show overlay display because there is no default display upon which to show it.");
      return false;
    }
    return true;
  }
  
  private void updateWindowParams()
  {
    float f1 = Math.max(0.3F, Math.min(1.0F, Math.min(Math.min(this.mWindowScale * this.mLiveScale, this.mDefaultDisplayInfo.logicalWidth / this.mWidth), this.mDefaultDisplayInfo.logicalHeight / this.mHeight)));
    float f2 = (f1 / this.mWindowScale - 1.0F) * 0.5F;
    int i = (int)(this.mWidth * f1);
    int j = (int)(this.mHeight * f1);
    int m = (int)(this.mWindowX + this.mLiveTranslationX - i * f2);
    int k = (int)(this.mWindowY + this.mLiveTranslationY - j * f2);
    m = Math.max(0, Math.min(m, this.mDefaultDisplayInfo.logicalWidth - i));
    k = Math.max(0, Math.min(k, this.mDefaultDisplayInfo.logicalHeight - j));
    this.mTextureView.setScaleX(f1);
    this.mTextureView.setScaleY(f1);
    this.mWindowParams.x = m;
    this.mWindowParams.y = k;
    this.mWindowParams.width = i;
    this.mWindowParams.height = j;
  }
  
  public void dismiss()
  {
    if (this.mWindowVisible)
    {
      this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
      this.mWindowManager.removeView(this.mWindowContent);
      this.mWindowVisible = false;
    }
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println("mWindowVisible=" + this.mWindowVisible);
    paramPrintWriter.println("mWindowX=" + this.mWindowX);
    paramPrintWriter.println("mWindowY=" + this.mWindowY);
    paramPrintWriter.println("mWindowScale=" + this.mWindowScale);
    paramPrintWriter.println("mWindowParams=" + this.mWindowParams);
    if (this.mTextureView != null)
    {
      paramPrintWriter.println("mTextureView.getScaleX()=" + this.mTextureView.getScaleX());
      paramPrintWriter.println("mTextureView.getScaleY()=" + this.mTextureView.getScaleY());
    }
    paramPrintWriter.println("mLiveTranslationX=" + this.mLiveTranslationX);
    paramPrintWriter.println("mLiveTranslationY=" + this.mLiveTranslationY);
    paramPrintWriter.println("mLiveScale=" + this.mLiveScale);
  }
  
  public void relayout()
  {
    if (this.mWindowVisible)
    {
      updateWindowParams();
      this.mWindowManager.updateViewLayout(this.mWindowContent, this.mWindowParams);
    }
  }
  
  public void resize(int paramInt1, int paramInt2, int paramInt3)
  {
    resize(paramInt1, paramInt2, paramInt3, true);
  }
  
  public void show()
  {
    if (!this.mWindowVisible)
    {
      this.mDisplayManager.registerDisplayListener(this.mDisplayListener, null);
      if (!updateDefaultDisplayInfo())
      {
        this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
        return;
      }
      clearLiveState();
      updateWindowParams();
      this.mWindowManager.addView(this.mWindowContent, this.mWindowParams);
      this.mWindowVisible = true;
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onStateChanged(int paramInt);
    
    public abstract void onWindowCreated(SurfaceTexture paramSurfaceTexture, float paramFloat, long paramLong, int paramInt);
    
    public abstract void onWindowDestroyed();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/OverlayDisplayWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */