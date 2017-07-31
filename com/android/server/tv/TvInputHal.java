package com.android.server.tv;

import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvStreamConfig;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Surface;
import java.util.LinkedList;
import java.util.Queue;

final class TvInputHal
  implements Handler.Callback
{
  private static final boolean DEBUG = false;
  public static final int ERROR_NO_INIT = -1;
  public static final int ERROR_STALE_CONFIG = -2;
  public static final int ERROR_UNKNOWN = -3;
  public static final int EVENT_DEVICE_AVAILABLE = 1;
  public static final int EVENT_DEVICE_UNAVAILABLE = 2;
  public static final int EVENT_FIRST_FRAME_CAPTURED = 4;
  public static final int EVENT_STREAM_CONFIGURATION_CHANGED = 3;
  public static final int SUCCESS = 0;
  private static final String TAG = TvInputHal.class.getSimpleName();
  private final Callback mCallback;
  private final Handler mHandler;
  private final Object mLock = new Object();
  private final Queue<Message> mPendingMessageQueue = new LinkedList();
  private long mPtr = 0L;
  private final SparseIntArray mStreamConfigGenerations = new SparseIntArray();
  private final SparseArray<TvStreamConfig[]> mStreamConfigs = new SparseArray();
  
  public TvInputHal(Callback paramCallback)
  {
    this.mCallback = paramCallback;
    this.mHandler = new Handler(this);
  }
  
  private void deviceAvailableFromNative(TvInputHardwareInfo paramTvInputHardwareInfo)
  {
    this.mHandler.obtainMessage(1, paramTvInputHardwareInfo).sendToTarget();
  }
  
  private void deviceUnavailableFromNative(int paramInt)
  {
    this.mHandler.obtainMessage(2, paramInt, 0).sendToTarget();
  }
  
  private void firstFrameCapturedFromNative(int paramInt1, int paramInt2)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(3, paramInt1, paramInt2));
  }
  
  private static native int nativeAddOrUpdateStream(long paramLong, int paramInt1, int paramInt2, Surface paramSurface);
  
  private static native void nativeClose(long paramLong);
  
  private static native TvStreamConfig[] nativeGetStreamConfigs(long paramLong, int paramInt1, int paramInt2);
  
  private native long nativeOpen(MessageQueue paramMessageQueue);
  
  private static native int nativeRemoveStream(long paramLong, int paramInt1, int paramInt2);
  
  private void retrieveStreamConfigsLocked(int paramInt)
  {
    int i = this.mStreamConfigGenerations.get(paramInt, 0) + 1;
    this.mStreamConfigs.put(paramInt, nativeGetStreamConfigs(this.mPtr, paramInt, i));
    this.mStreamConfigGenerations.put(paramInt, i);
  }
  
  private void streamConfigsChangedFromNative(int paramInt)
  {
    this.mHandler.obtainMessage(3, paramInt, 0).sendToTarget();
  }
  
  public int addOrUpdateStream(int paramInt, Surface paramSurface, TvStreamConfig paramTvStreamConfig)
  {
    synchronized (this.mLock)
    {
      long l = this.mPtr;
      if (l == 0L) {
        return -1;
      }
      int i = this.mStreamConfigGenerations.get(paramInt, 0);
      int j = paramTvStreamConfig.getGeneration();
      if (i != j) {
        return -2;
      }
      paramInt = nativeAddOrUpdateStream(this.mPtr, paramInt, paramTvStreamConfig.getStreamId(), paramSurface);
      if (paramInt == 0) {
        return 0;
      }
      return -3;
    }
  }
  
  public void close()
  {
    synchronized (this.mLock)
    {
      if (this.mPtr != 0L) {
        nativeClose(this.mPtr);
      }
      return;
    }
  }
  
  public boolean handleMessage(Message arg1)
  {
    TvInputHardwareInfo localTvInputHardwareInfo;
    switch (???.what)
    {
    default: 
      Slog.e(TAG, "Unknown event: " + ???);
      return false;
    case 1: 
      localTvInputHardwareInfo = (TvInputHardwareInfo)???.obj;
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        retrieveStreamConfigsLocked(localTvInputHardwareInfo.getDeviceId());
        TvStreamConfig[] arrayOfTvStreamConfig2 = (TvStreamConfig[])this.mStreamConfigs.get(localTvInputHardwareInfo.getDeviceId());
        this.mCallback.onDeviceAvailable(localTvInputHardwareInfo, arrayOfTvStreamConfig2);
        return true;
      }
      int i = ???.arg1;
      this.mCallback.onDeviceUnavailable(i);
      continue;
      i = ???.arg1;
      int j;
      synchronized (this.mLock)
      {
        retrieveStreamConfigsLocked(i);
        TvStreamConfig[] arrayOfTvStreamConfig1 = (TvStreamConfig[])this.mStreamConfigs.get(i);
        this.mCallback.onStreamConfigurationChanged(i, arrayOfTvStreamConfig1);
      }
    }
  }
  
  public void init()
  {
    synchronized (this.mLock)
    {
      this.mPtr = nativeOpen(this.mHandler.getLooper().getQueue());
      return;
    }
  }
  
  public int removeStream(int paramInt, TvStreamConfig paramTvStreamConfig)
  {
    synchronized (this.mLock)
    {
      long l = this.mPtr;
      if (l == 0L) {
        return -1;
      }
      int i = this.mStreamConfigGenerations.get(paramInt, 0);
      int j = paramTvStreamConfig.getGeneration();
      if (i != j) {
        return -2;
      }
      paramInt = nativeRemoveStream(this.mPtr, paramInt, paramTvStreamConfig.getStreamId());
      if (paramInt == 0) {
        return 0;
      }
      return -3;
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onDeviceAvailable(TvInputHardwareInfo paramTvInputHardwareInfo, TvStreamConfig[] paramArrayOfTvStreamConfig);
    
    public abstract void onDeviceUnavailable(int paramInt);
    
    public abstract void onFirstFrameCaptured(int paramInt1, int paramInt2);
    
    public abstract void onStreamConfigurationChanged(int paramInt, TvStreamConfig[] paramArrayOfTvStreamConfig);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/tv/TvInputHal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */