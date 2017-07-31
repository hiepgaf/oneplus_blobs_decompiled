package com.oneplus.gallery2.media;

import com.oneplus.base.BitFlagsGroup;
import com.oneplus.base.Handle;

public abstract interface VideoMedia
  extends Media
{
  public static final int FLAG_DURATION_CHANGED = FLAGS_GROUP.nextIntFlag();
  
  public abstract Handle getDuration(DurationCallback paramDurationCallback);
  
  public abstract boolean isSlowMotion();
  
  public abstract boolean isTimeLapse();
  
  public abstract Long peekDuration();
  
  public static abstract interface DurationCallback
  {
    public abstract void onDurationObtained(VideoMedia paramVideoMedia, long paramLong);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/VideoMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */