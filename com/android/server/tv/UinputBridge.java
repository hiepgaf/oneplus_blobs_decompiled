package com.android.server.tv;

import android.os.IBinder;
import dalvik.system.CloseGuard;
import java.io.IOException;

public final class UinputBridge
{
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private long mPtr;
  private IBinder mToken = null;
  
  public UinputBridge(IBinder paramIBinder, String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    if ((paramInt1 < 1) || (paramInt2 < 1)) {
      throw new IllegalArgumentException("Touchpad must be at least 1x1.");
    }
    if ((paramInt3 < 1) || (paramInt3 > 32)) {
      throw new IllegalArgumentException("Touchpad must support between 1 and 32 pointers.");
    }
    if (paramIBinder == null) {
      throw new IllegalArgumentException("Token cannot be null");
    }
    this.mPtr = nativeOpen(paramString, paramIBinder.toString(), paramInt1, paramInt2, paramInt3);
    if (this.mPtr == 0L) {
      throw new IOException("Could not open uinput device " + paramString);
    }
    this.mToken = paramIBinder;
    this.mCloseGuard.open("close");
  }
  
  private static native void nativeClear(long paramLong);
  
  private static native void nativeClose(long paramLong);
  
  private static native long nativeOpen(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3);
  
  private static native void nativeSendKey(long paramLong, int paramInt, boolean paramBoolean);
  
  private static native void nativeSendPointerDown(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  private static native void nativeSendPointerSync(long paramLong);
  
  private static native void nativeSendPointerUp(long paramLong, int paramInt);
  
  private static native void nativeSendTimestamp(long paramLong1, long paramLong2);
  
  public void clear(IBinder paramIBinder)
  {
    if (isTokenValid(paramIBinder)) {
      nativeClear(this.mPtr);
    }
  }
  
  public void close(IBinder paramIBinder)
  {
    if ((isTokenValid(paramIBinder)) && (this.mPtr != 0L))
    {
      clear(paramIBinder);
      nativeClose(this.mPtr);
      this.mPtr = 0L;
      this.mCloseGuard.close();
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mPtr != 0L) {
        this.mCloseGuard.warnIfOpen();
      }
      close(this.mToken);
      return;
    }
    finally
    {
      this.mToken = null;
      super.finalize();
    }
  }
  
  public IBinder getToken()
  {
    return this.mToken;
  }
  
  protected boolean isTokenValid(IBinder paramIBinder)
  {
    return this.mToken.equals(paramIBinder);
  }
  
  public void sendKeyDown(IBinder paramIBinder, int paramInt)
  {
    if (isTokenValid(paramIBinder)) {
      nativeSendKey(this.mPtr, paramInt, true);
    }
  }
  
  public void sendKeyUp(IBinder paramIBinder, int paramInt)
  {
    if (isTokenValid(paramIBinder)) {
      nativeSendKey(this.mPtr, paramInt, false);
    }
  }
  
  public void sendPointerDown(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3)
  {
    if (isTokenValid(paramIBinder)) {
      nativeSendPointerDown(this.mPtr, paramInt1, paramInt2, paramInt3);
    }
  }
  
  public void sendPointerSync(IBinder paramIBinder)
  {
    if (isTokenValid(paramIBinder)) {
      nativeSendPointerSync(this.mPtr);
    }
  }
  
  public void sendPointerUp(IBinder paramIBinder, int paramInt)
  {
    if (isTokenValid(paramIBinder)) {
      nativeSendPointerUp(this.mPtr, paramInt);
    }
  }
  
  public void sendTimestamp(IBinder paramIBinder, long paramLong)
  {
    if (isTokenValid(paramIBinder)) {
      nativeSendTimestamp(this.mPtr, paramLong);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/tv/UinputBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */