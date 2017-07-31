package com.android.server;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.IConsumerIrService.Stub;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Slog;

public class ConsumerIrService
  extends IConsumerIrService.Stub
{
  private static final int MAX_XMIT_TIME = 2000000;
  private static final String TAG = "ConsumerIrService";
  private final Context mContext;
  private final Object mHalLock = new Object();
  private final long mNativeHal;
  private final PowerManager.WakeLock mWakeLock;
  
  ConsumerIrService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "ConsumerIrService");
    this.mWakeLock.setReferenceCounted(true);
    this.mNativeHal = halOpen();
    if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.consumerir"))
    {
      if (this.mNativeHal == 0L) {
        throw new RuntimeException("FEATURE_CONSUMER_IR present, but no IR HAL loaded!");
      }
    }
    else if (this.mNativeHal != 0L) {
      throw new RuntimeException("IR HAL present, but FEATURE_CONSUMER_IR is not set!");
    }
  }
  
  private static native int[] halGetCarrierFrequencies(long paramLong);
  
  private static native long halOpen();
  
  private static native int halTransmit(long paramLong, int paramInt, int[] paramArrayOfInt);
  
  private void throwIfNoIrEmitter()
  {
    if (this.mNativeHal == 0L) {
      throw new UnsupportedOperationException("IR emitter not available");
    }
  }
  
  public int[] getCarrierFrequencies()
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.TRANSMIT_IR") != 0) {
      throw new SecurityException("Requires TRANSMIT_IR permission");
    }
    throwIfNoIrEmitter();
    synchronized (this.mHalLock)
    {
      int[] arrayOfInt = halGetCarrierFrequencies(this.mNativeHal);
      return arrayOfInt;
    }
  }
  
  public boolean hasIrEmitter()
  {
    return this.mNativeHal != 0L;
  }
  
  public void transmit(String arg1, int paramInt, int[] paramArrayOfInt)
  {
    int i = 0;
    if (this.mContext.checkCallingOrSelfPermission("android.permission.TRANSMIT_IR") != 0) {
      throw new SecurityException("Requires TRANSMIT_IR permission");
    }
    long l = 0L;
    int j = paramArrayOfInt.length;
    while (i < j)
    {
      int k = paramArrayOfInt[i];
      if (k <= 0) {
        throw new IllegalArgumentException("Non-positive IR slice");
      }
      l += k;
      i += 1;
    }
    if (l > 2000000L) {
      throw new IllegalArgumentException("IR pattern too long");
    }
    throwIfNoIrEmitter();
    synchronized (this.mHalLock)
    {
      paramInt = halTransmit(this.mNativeHal, paramInt, paramArrayOfInt);
      if (paramInt < 0) {
        Slog.e("ConsumerIrService", "Error transmitting: " + paramInt);
      }
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/ConsumerIrService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */