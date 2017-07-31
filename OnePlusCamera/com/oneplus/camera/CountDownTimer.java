package com.oneplus.camera;

import com.oneplus.base.EventArgs;
import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface CountDownTimer
  extends Component
{
  public static final EventKey<EventArgs> EVENT_CANCELLED = new EventKey("Cancelled", EventArgs.class, CountDownTimer.class);
  public static final int FLAG_ENABLE_COUNT_DOWN_FLASHLIGHT = 1;
  public static final PropertyKey<Boolean> PROP_IS_COUNT_DOWN_FLASHLIGHT_ENABLED = new PropertyKey("IsCountDownFlashlightEnabled", Boolean.class, CountDownTimer.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_STARTED = new PropertyKey("IsStarted", Boolean.class, CountDownTimer.class, Boolean.valueOf(false));
  public static final PropertyKey<Long> PROP_REMAINING_SECONDS = new PropertyKey("RemainingSeconds", Long.class, CountDownTimer.class, Long.valueOf(0L));
  
  public abstract Handle start(long paramLong, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CountDownTimer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */