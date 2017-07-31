package com.oneplus.camera;

import android.os.SystemClock;
import com.oneplus.base.EventArgs;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;

public class CountDownTimerImpl
  extends CameraComponent
  implements CountDownTimer
{
  private long m_CountdownSecs = 0L;
  private Handle m_CurrentHandle = null;
  private long m_ElapsedTime = 0L;
  private final long m_Interval = 1000L;
  private Runnable m_Timer = new Runnable()
  {
    public void run()
    {
      CountDownTimerImpl localCountDownTimerImpl1 = CountDownTimerImpl.this;
      PropertyKey localPropertyKey = CountDownTimerImpl.PROP_REMAINING_SECONDS;
      CountDownTimerImpl localCountDownTimerImpl2 = CountDownTimerImpl.this;
      CountDownTimerImpl.-wrap0(localCountDownTimerImpl1, localPropertyKey, Long.valueOf(CountDownTimerImpl.-set0(localCountDownTimerImpl2, CountDownTimerImpl.-get0(localCountDownTimerImpl2) - 1L)));
      if (CountDownTimerImpl.-get0(CountDownTimerImpl.this) != 0L)
      {
        long l = SystemClock.elapsedRealtime();
        HandlerUtils.post(CountDownTimerImpl.this, this, 1000L - (l - CountDownTimerImpl.-get2(CountDownTimerImpl.this) - 1000L));
        CountDownTimerImpl.-set1(CountDownTimerImpl.this, l);
        return;
      }
      CountDownTimerImpl.this.resetComputeData();
    }
  };
  
  CountDownTimerImpl(CameraActivity paramCameraActivity)
  {
    super("CountDown Timer manager", paramCameraActivity, true);
  }
  
  void resetComputeData()
  {
    setReadOnly(PROP_REMAINING_SECONDS, Long.valueOf(0L));
    setReadOnly(PROP_IS_COUNT_DOWN_FLASHLIGHT_ENABLED, Boolean.valueOf(false));
    setReadOnly(PROP_IS_STARTED, Boolean.valueOf(false));
    this.m_CurrentHandle = null;
    this.m_ElapsedTime = 0L;
    this.m_CountdownSecs = 0L;
  }
  
  public Handle start(long paramLong, int paramInt)
  {
    verifyAccess();
    if (this.m_CurrentHandle != null)
    {
      Log.e(this.TAG, "Count Down running ");
      return null;
    }
    if (paramLong <= 0L)
    {
      Log.e(this.TAG, "seconds must greater than zero ");
      return null;
    }
    this.m_CurrentHandle = new Handle("CountDownTimer")
    {
      protected void onClose(int paramAnonymousInt)
      {
        if (this == CountDownTimerImpl.-get1(CountDownTimerImpl.this))
        {
          CountDownTimerImpl.-wrap1(CountDownTimerImpl.this, CountDownTimerImpl.EVENT_CANCELLED, EventArgs.EMPTY);
          HandlerUtils.removeCallbacks(CountDownTimerImpl.this, CountDownTimerImpl.-get3(CountDownTimerImpl.this));
          CountDownTimerImpl.this.resetComputeData();
        }
      }
    };
    this.m_CountdownSecs = paramLong;
    setReadOnly(PROP_IS_STARTED, Boolean.valueOf(true));
    if ((paramInt & 0x1) != 0) {
      setReadOnly(PROP_IS_COUNT_DOWN_FLASHLIGHT_ENABLED, Boolean.valueOf(true));
    }
    setReadOnly(PROP_REMAINING_SECONDS, Long.valueOf(this.m_CountdownSecs));
    this.m_ElapsedTime = SystemClock.elapsedRealtime();
    HandlerUtils.post(this, this.m_Timer, 1000L);
    return this.m_CurrentHandle;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CountDownTimerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */