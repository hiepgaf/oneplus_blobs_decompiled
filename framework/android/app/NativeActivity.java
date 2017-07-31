package android.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.AttributeSet;
import android.view.InputQueue;
import android.view.InputQueue.Callback;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback2;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import dalvik.system.BaseDexClassLoader;
import java.io.File;

public class NativeActivity
  extends Activity
  implements SurfaceHolder.Callback2, InputQueue.Callback, ViewTreeObserver.OnGlobalLayoutListener
{
  private static final String KEY_NATIVE_SAVED_STATE = "android:native_state";
  public static final String META_DATA_FUNC_NAME = "android.app.func_name";
  public static final String META_DATA_LIB_NAME = "android.app.lib_name";
  private InputQueue mCurInputQueue;
  private SurfaceHolder mCurSurfaceHolder;
  private boolean mDestroyed;
  private boolean mDispatchingUnhandledKey;
  private InputMethodManager mIMM;
  int mLastContentHeight;
  int mLastContentWidth;
  int mLastContentX;
  int mLastContentY;
  final int[] mLocation = new int[2];
  private NativeContentView mNativeContentView;
  private long mNativeHandle;
  
  private static String getAbsolutePath(File paramFile)
  {
    String str = null;
    if (paramFile != null) {
      str = paramFile.getAbsolutePath();
    }
    return str;
  }
  
  private native String getDlError();
  
  private native long loadNativeCode(String paramString1, String paramString2, MessageQueue paramMessageQueue, String paramString3, String paramString4, String paramString5, int paramInt, AssetManager paramAssetManager, byte[] paramArrayOfByte, ClassLoader paramClassLoader, String paramString6);
  
  private native void onConfigurationChangedNative(long paramLong);
  
  private native void onContentRectChangedNative(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  private native void onInputQueueCreatedNative(long paramLong1, long paramLong2);
  
  private native void onInputQueueDestroyedNative(long paramLong1, long paramLong2);
  
  private native void onLowMemoryNative(long paramLong);
  
  private native void onPauseNative(long paramLong);
  
  private native void onResumeNative(long paramLong);
  
  private native byte[] onSaveInstanceStateNative(long paramLong);
  
  private native void onStartNative(long paramLong);
  
  private native void onStopNative(long paramLong);
  
  private native void onSurfaceChangedNative(long paramLong, Surface paramSurface, int paramInt1, int paramInt2, int paramInt3);
  
  private native void onSurfaceCreatedNative(long paramLong, Surface paramSurface);
  
  private native void onSurfaceDestroyedNative(long paramLong);
  
  private native void onSurfaceRedrawNeededNative(long paramLong, Surface paramSurface);
  
  private native void onWindowFocusChangedNative(long paramLong, boolean paramBoolean);
  
  private native void unloadNativeCode(long paramLong);
  
  void hideIme(int paramInt)
  {
    this.mIMM.hideSoftInputFromWindow(this.mNativeContentView.getWindowToken(), paramInt);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (!this.mDestroyed) {
      onConfigurationChangedNative(this.mNativeHandle);
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    Object localObject1 = "main";
    Object localObject4 = "ANativeActivity_onCreate";
    this.mIMM = ((InputMethodManager)getSystemService(InputMethodManager.class));
    getWindow().takeSurface(this);
    getWindow().takeInputQueue(this);
    getWindow().setFormat(4);
    getWindow().setSoftInputMode(16);
    this.mNativeContentView = new NativeContentView(this);
    this.mNativeContentView.mActivity = this;
    setContentView(this.mNativeContentView);
    this.mNativeContentView.requestFocus();
    this.mNativeContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    Object localObject5;
    Object localObject2;
    try
    {
      localObject5 = getPackageManager().getActivityInfo(getIntent().getComponent(), 128);
      localObject2 = localObject4;
      Object localObject3 = localObject1;
      if (((ActivityInfo)localObject5).metaData != null)
      {
        localObject2 = ((ActivityInfo)localObject5).metaData.getString("android.app.lib_name");
        if (localObject2 != null) {
          localObject1 = localObject2;
        }
        localObject5 = ((ActivityInfo)localObject5).metaData.getString("android.app.func_name");
        localObject2 = localObject4;
        localObject3 = localObject1;
        if (localObject5 != null)
        {
          localObject2 = localObject5;
          localObject3 = localObject1;
        }
      }
      localObject4 = (BaseDexClassLoader)getClassLoader();
      localObject5 = ((BaseDexClassLoader)localObject4).findLibrary((String)localObject3);
      if (localObject5 == null) {
        throw new IllegalArgumentException("Unable to find native library " + (String)localObject3 + " using classloader: " + ((BaseDexClassLoader)localObject4).toString());
      }
    }
    catch (PackageManager.NameNotFoundException paramBundle)
    {
      throw new RuntimeException("Error getting activity info", paramBundle);
    }
    if (paramBundle != null) {}
    for (localObject1 = paramBundle.getByteArray("android:native_state");; localObject1 = null)
    {
      this.mNativeHandle = loadNativeCode((String)localObject5, (String)localObject2, Looper.myQueue(), getAbsolutePath(getFilesDir()), getAbsolutePath(getObbDir()), getAbsolutePath(getExternalFilesDir(null)), Build.VERSION.SDK_INT, getAssets(), (byte[])localObject1, (ClassLoader)localObject4, ((BaseDexClassLoader)localObject4).getLdLibraryPath());
      if (this.mNativeHandle != 0L) {
        break;
      }
      throw new UnsatisfiedLinkError("Unable to load native library \"" + (String)localObject5 + "\": " + getDlError());
    }
    super.onCreate(paramBundle);
  }
  
  protected void onDestroy()
  {
    this.mDestroyed = true;
    if (this.mCurSurfaceHolder != null)
    {
      onSurfaceDestroyedNative(this.mNativeHandle);
      this.mCurSurfaceHolder = null;
    }
    if (this.mCurInputQueue != null)
    {
      onInputQueueDestroyedNative(this.mNativeHandle, this.mCurInputQueue.getNativePtr());
      this.mCurInputQueue = null;
    }
    unloadNativeCode(this.mNativeHandle);
    super.onDestroy();
  }
  
  public void onGlobalLayout()
  {
    this.mNativeContentView.getLocationInWindow(this.mLocation);
    int i = this.mNativeContentView.getWidth();
    int j = this.mNativeContentView.getHeight();
    if ((this.mLocation[0] != this.mLastContentX) || (this.mLocation[1] != this.mLastContentY)) {
      break label114;
    }
    for (;;)
    {
      this.mLastContentX = this.mLocation[0];
      this.mLastContentY = this.mLocation[1];
      this.mLastContentWidth = i;
      this.mLastContentHeight = j;
      if (!this.mDestroyed) {
        onContentRectChangedNative(this.mNativeHandle, this.mLastContentX, this.mLastContentY, this.mLastContentWidth, this.mLastContentHeight);
      }
      label114:
      return;
      if (i == this.mLastContentWidth) {
        if (j == this.mLastContentHeight) {
          break;
        }
      }
    }
  }
  
  public void onInputQueueCreated(InputQueue paramInputQueue)
  {
    if (!this.mDestroyed)
    {
      this.mCurInputQueue = paramInputQueue;
      onInputQueueCreatedNative(this.mNativeHandle, paramInputQueue.getNativePtr());
    }
  }
  
  public void onInputQueueDestroyed(InputQueue paramInputQueue)
  {
    if (!this.mDestroyed)
    {
      onInputQueueDestroyedNative(this.mNativeHandle, paramInputQueue.getNativePtr());
      this.mCurInputQueue = null;
    }
  }
  
  public void onLowMemory()
  {
    super.onLowMemory();
    if (!this.mDestroyed) {
      onLowMemoryNative(this.mNativeHandle);
    }
  }
  
  protected void onPause()
  {
    super.onPause();
    onPauseNative(this.mNativeHandle);
  }
  
  protected void onResume()
  {
    super.onResume();
    onResumeNative(this.mNativeHandle);
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    byte[] arrayOfByte = onSaveInstanceStateNative(this.mNativeHandle);
    if (arrayOfByte != null) {
      paramBundle.putByteArray("android:native_state", arrayOfByte);
    }
  }
  
  protected void onStart()
  {
    super.onStart();
    onStartNative(this.mNativeHandle);
  }
  
  protected void onStop()
  {
    super.onStop();
    onStopNative(this.mNativeHandle);
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    super.onWindowFocusChanged(paramBoolean);
    if (!this.mDestroyed) {
      onWindowFocusChangedNative(this.mNativeHandle, paramBoolean);
    }
  }
  
  void setWindowFlags(int paramInt1, int paramInt2)
  {
    getWindow().setFlags(paramInt1, paramInt2);
  }
  
  void setWindowFormat(int paramInt)
  {
    getWindow().setFormat(paramInt);
  }
  
  void showIme(int paramInt)
  {
    this.mIMM.showSoftInput(this.mNativeContentView, paramInt);
  }
  
  public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
  {
    if (!this.mDestroyed)
    {
      this.mCurSurfaceHolder = paramSurfaceHolder;
      onSurfaceChangedNative(this.mNativeHandle, paramSurfaceHolder.getSurface(), paramInt1, paramInt2, paramInt3);
    }
  }
  
  public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
  {
    if (!this.mDestroyed)
    {
      this.mCurSurfaceHolder = paramSurfaceHolder;
      onSurfaceCreatedNative(this.mNativeHandle, paramSurfaceHolder.getSurface());
    }
  }
  
  public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
  {
    this.mCurSurfaceHolder = null;
    if (!this.mDestroyed) {
      onSurfaceDestroyedNative(this.mNativeHandle);
    }
  }
  
  public void surfaceRedrawNeeded(SurfaceHolder paramSurfaceHolder)
  {
    if (!this.mDestroyed)
    {
      this.mCurSurfaceHolder = paramSurfaceHolder;
      onSurfaceRedrawNeededNative(this.mNativeHandle, paramSurfaceHolder.getSurface());
    }
  }
  
  static class NativeContentView
    extends View
  {
    NativeActivity mActivity;
    
    public NativeContentView(Context paramContext)
    {
      super();
    }
    
    public NativeContentView(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/NativeActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */