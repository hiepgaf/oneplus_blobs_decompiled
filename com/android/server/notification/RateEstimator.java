package com.android.server.notification;

public class RateEstimator
{
  private static final double MINIMUM_DT = 5.0E-4D;
  private static final double RATE_ALPHA = 0.8D;
  private double mInterarrivalTime = 1000.0D;
  private Long mLastEventTime;
  
  private double getInterarrivalEstimate(long paramLong)
  {
    double d = Math.max((paramLong - this.mLastEventTime.longValue()) / 1000.0D, 5.0E-4D);
    return this.mInterarrivalTime * 0.8D + 0.19999999999999996D * d;
  }
  
  public float getRate(long paramLong)
  {
    if (this.mLastEventTime == null) {
      return 0.0F;
    }
    return (float)(1.0D / getInterarrivalEstimate(paramLong));
  }
  
  public float update(long paramLong)
  {
    if (this.mLastEventTime == null) {}
    for (float f = 0.0F;; f = (float)(1.0D / this.mInterarrivalTime))
    {
      this.mLastEventTime = Long.valueOf(paramLong);
      return f;
      this.mInterarrivalTime = getInterarrivalEstimate(paramLong);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/RateEstimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */