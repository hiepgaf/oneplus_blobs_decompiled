package android.inputmethodservice;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.KeyEvent.Callback;
import android.view.KeyEvent.DispatcherState;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class SoftInputWindow
  extends Dialog
{
  private final Rect mBounds = new Rect();
  final Callback mCallback;
  final KeyEvent.DispatcherState mDispatcherState;
  final int mGravity;
  final KeyEvent.Callback mKeyEventCallback;
  final String mName;
  final boolean mTakesFocus;
  final int mWindowType;
  
  public SoftInputWindow(Context paramContext, String paramString, int paramInt1, Callback paramCallback, KeyEvent.Callback paramCallback1, KeyEvent.DispatcherState paramDispatcherState, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    super(paramContext, paramInt1);
    this.mName = paramString;
    this.mCallback = paramCallback;
    this.mKeyEventCallback = paramCallback1;
    this.mDispatcherState = paramDispatcherState;
    this.mWindowType = paramInt2;
    this.mGravity = paramInt3;
    this.mTakesFocus = paramBoolean;
    initDockWindow();
  }
  
  private void initDockWindow()
  {
    WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
    localLayoutParams.type = this.mWindowType;
    localLayoutParams.setTitle(this.mName);
    localLayoutParams.gravity = this.mGravity;
    updateWidthHeight(localLayoutParams);
    getWindow().setAttributes(localLayoutParams);
    int i = 266;
    int j;
    if (!this.mTakesFocus) {
      j = 264;
    }
    for (;;)
    {
      getWindow().setFlags(j, i);
      return;
      j = 288;
      i = 298;
    }
  }
  
  private void updateWidthHeight(WindowManager.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams.gravity == 48) || (paramLayoutParams.gravity == 80))
    {
      paramLayoutParams.width = -1;
      paramLayoutParams.height = -2;
      return;
    }
    paramLayoutParams.width = -2;
    paramLayoutParams.height = -1;
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    getWindow().getDecorView().getHitRect(this.mBounds);
    if (paramMotionEvent.isWithinBoundsNoHistory(this.mBounds.left, this.mBounds.top, this.mBounds.right - 1, this.mBounds.bottom - 1)) {
      return super.dispatchTouchEvent(paramMotionEvent);
    }
    paramMotionEvent = paramMotionEvent.clampNoHistory(this.mBounds.left, this.mBounds.top, this.mBounds.right - 1, this.mBounds.bottom - 1);
    boolean bool = super.dispatchTouchEvent(paramMotionEvent);
    paramMotionEvent.recycle();
    return bool;
  }
  
  public int getGravity()
  {
    return getWindow().getAttributes().gravity;
  }
  
  public void onBackPressed()
  {
    if (this.mCallback != null)
    {
      this.mCallback.onBackPressed();
      return;
    }
    super.onBackPressed();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((this.mKeyEventCallback != null) && (this.mKeyEventCallback.onKeyDown(paramInt, paramKeyEvent))) {
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public boolean onKeyLongPress(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((this.mKeyEventCallback != null) && (this.mKeyEventCallback.onKeyLongPress(paramInt, paramKeyEvent))) {
      return true;
    }
    return super.onKeyLongPress(paramInt, paramKeyEvent);
  }
  
  public boolean onKeyMultiple(int paramInt1, int paramInt2, KeyEvent paramKeyEvent)
  {
    if ((this.mKeyEventCallback != null) && (this.mKeyEventCallback.onKeyMultiple(paramInt1, paramInt2, paramKeyEvent))) {
      return true;
    }
    return super.onKeyMultiple(paramInt1, paramInt2, paramKeyEvent);
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((this.mKeyEventCallback != null) && (this.mKeyEventCallback.onKeyUp(paramInt, paramKeyEvent))) {
      return true;
    }
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    super.onWindowFocusChanged(paramBoolean);
    this.mDispatcherState.reset();
  }
  
  public void setGravity(int paramInt)
  {
    WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
    localLayoutParams.gravity = paramInt;
    updateWidthHeight(localLayoutParams);
    getWindow().setAttributes(localLayoutParams);
  }
  
  public void setToken(IBinder paramIBinder)
  {
    WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
    localLayoutParams.token = paramIBinder;
    getWindow().setAttributes(localLayoutParams);
  }
  
  public static abstract interface Callback
  {
    public abstract void onBackPressed();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/inputmethodservice/SoftInputWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */