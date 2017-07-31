package com.android.server.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.Fingerprint;
import android.os.Vibrator;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import java.util.List;

public class FingerprintUtils
{
  public static long[] FP_ERROR_VIBRATE_PATTERN = { -2L, 0L, 250L };
  public static long[] FP_SUCCESS_VIBRATE_PATTERN = { -2L, 0L, 10L };
  private static FingerprintUtils sInstance;
  private static final Object sInstanceLock = new Object();
  @GuardedBy("this")
  private final SparseArray<FingerprintsUserState> mUsers = new SparseArray();
  
  public static FingerprintUtils getInstance()
  {
    synchronized (sInstanceLock)
    {
      if (sInstance == null) {
        sInstance = new FingerprintUtils();
      }
      return sInstance;
    }
  }
  
  private FingerprintsUserState getStateForUser(Context paramContext, int paramInt)
  {
    try
    {
      FingerprintsUserState localFingerprintsUserState2 = (FingerprintsUserState)this.mUsers.get(paramInt);
      FingerprintsUserState localFingerprintsUserState1 = localFingerprintsUserState2;
      if (localFingerprintsUserState2 == null)
      {
        localFingerprintsUserState1 = new FingerprintsUserState(paramContext, paramInt);
        this.mUsers.put(paramInt, localFingerprintsUserState1);
      }
      return localFingerprintsUserState1;
    }
    finally {}
  }
  
  public static void vibrateFingerprintError(Context paramContext)
  {
    Vibrator localVibrator = (Vibrator)paramContext.getSystemService(Vibrator.class);
    if (localVibrator != null)
    {
      int i = Settings.System.getIntForUser(paramContext.getContentResolver(), "vibrate_on_touch_intensity", 1, -2);
      FP_ERROR_VIBRATE_PATTERN[0] = ((i + 1) * -1);
      localVibrator.vibrate(FP_ERROR_VIBRATE_PATTERN, -1);
    }
  }
  
  public static void vibrateFingerprintSuccess(Context paramContext)
  {
    Vibrator localVibrator = (Vibrator)paramContext.getSystemService(Vibrator.class);
    if (localVibrator != null)
    {
      int i = Settings.System.getIntForUser(paramContext.getContentResolver(), "vibrate_on_touch_intensity", 1, -2);
      FP_SUCCESS_VIBRATE_PATTERN[0] = ((i + 1) * -1);
      localVibrator.vibrate(FP_SUCCESS_VIBRATE_PATTERN, -1);
    }
  }
  
  public void addFingerprintForUser(Context paramContext, int paramInt1, int paramInt2)
  {
    getStateForUser(paramContext, paramInt2).addFingerprint(paramInt1, paramInt2);
  }
  
  public List<Fingerprint> getFingerprintsForUser(Context paramContext, int paramInt)
  {
    return getStateForUser(paramContext, paramInt).getFingerprints();
  }
  
  public void removeFingerprintIdForUser(Context paramContext, int paramInt1, int paramInt2)
  {
    getStateForUser(paramContext, paramInt2).removeFingerprint(paramInt1);
  }
  
  public void renameFingerprintForUser(Context paramContext, int paramInt1, int paramInt2, CharSequence paramCharSequence)
  {
    if (TextUtils.isEmpty(paramCharSequence)) {
      return;
    }
    getStateForUser(paramContext, paramInt2).renameFingerprint(paramInt1, paramCharSequence);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/fingerprint/FingerprintUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */