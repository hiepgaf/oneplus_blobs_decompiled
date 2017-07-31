package com.oneplus.drawable;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.SystemClock;

public class RepeatTransitionDrawable
  extends TransitionDrawable
{
  private static final String TAG = RepeatTransitionDrawable.class.getSimpleName();
  private final int m_Duration;
  private final int m_Loops;
  private final Runnable[] m_TransitionRunnables = { new Runnable()new Runnable
  {
    public void run()
    {
      RepeatTransitionDrawable.this.startTransition(RepeatTransitionDrawable.-get0(RepeatTransitionDrawable.this));
    }
  }, new Runnable()
  {
    public void run()
    {
      RepeatTransitionDrawable.this.reverseTransition(RepeatTransitionDrawable.-get0(RepeatTransitionDrawable.this));
    }
  } };
  
  public RepeatTransitionDrawable(Drawable[] paramArrayOfDrawable, int paramInt1, int paramInt2)
  {
    super(paramArrayOfDrawable);
    this.m_Duration = paramInt1;
    this.m_Loops = paramInt2;
  }
  
  public void startRepeatTransition()
  {
    long l = SystemClock.uptimeMillis();
    int i = 0;
    while (i < this.m_Loops)
    {
      scheduleSelf(this.m_TransitionRunnables[(i % 2)], this.m_Duration * i + l);
      i += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/drawable/RepeatTransitionDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */