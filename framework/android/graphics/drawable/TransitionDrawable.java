package android.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.SystemClock;

public class TransitionDrawable
  extends LayerDrawable
  implements Drawable.Callback
{
  private static final int TRANSITION_NONE = 2;
  private static final int TRANSITION_RUNNING = 1;
  private static final int TRANSITION_STARTING = 0;
  private int mAlpha = 0;
  private boolean mCrossFade;
  private int mDuration;
  private int mFrom;
  private int mOriginalDuration;
  private boolean mReverse;
  private long mStartTimeMillis;
  private int mTo;
  private int mTransitionState = 2;
  
  TransitionDrawable()
  {
    this(new TransitionState(null, null, null), (Resources)null);
  }
  
  private TransitionDrawable(TransitionState paramTransitionState, Resources paramResources)
  {
    super(paramTransitionState, paramResources);
  }
  
  private TransitionDrawable(TransitionState paramTransitionState, Drawable[] paramArrayOfDrawable)
  {
    super(paramArrayOfDrawable, paramTransitionState);
  }
  
  public TransitionDrawable(Drawable[] paramArrayOfDrawable)
  {
    this(new TransitionState(null, null, null), paramArrayOfDrawable);
  }
  
  LayerDrawable.LayerState createConstantState(LayerDrawable.LayerState paramLayerState, Resources paramResources)
  {
    return new TransitionState((TransitionState)paramLayerState, this, paramResources);
  }
  
  public void draw(Canvas paramCanvas)
  {
    int i = 1;
    switch (this.mTransitionState)
    {
    }
    int j;
    boolean bool;
    Object localObject;
    do
    {
      for (;;)
      {
        j = this.mAlpha;
        bool = this.mCrossFade;
        localObject = this.mLayerState.mChildren;
        if (i == 0) {
          break;
        }
        if ((!bool) || (j == 0)) {
          localObject[0].mDrawable.draw(paramCanvas);
        }
        if (j == 255) {
          localObject[1].mDrawable.draw(paramCanvas);
        }
        return;
        this.mStartTimeMillis = SystemClock.uptimeMillis();
        i = 0;
        this.mTransitionState = 1;
      }
    } while (this.mStartTimeMillis < 0L);
    float f = (float)(SystemClock.uptimeMillis() - this.mStartTimeMillis) / this.mDuration;
    if (f >= 1.0F) {}
    for (i = 1;; i = 0)
    {
      f = Math.min(f, 1.0F);
      this.mAlpha = ((int)(this.mFrom + (this.mTo - this.mFrom) * f));
      break;
    }
    Drawable localDrawable = localObject[0].mDrawable;
    if (bool) {
      localDrawable.setAlpha(255 - j);
    }
    localDrawable.draw(paramCanvas);
    if (bool) {
      localDrawable.setAlpha(255);
    }
    if (j > 0)
    {
      localObject = localObject[1].mDrawable;
      ((Drawable)localObject).setAlpha(j);
      ((Drawable)localObject).draw(paramCanvas);
      ((Drawable)localObject).setAlpha(255);
    }
    if (i == 0) {
      invalidateSelf();
    }
  }
  
  public boolean isCrossFadeEnabled()
  {
    return this.mCrossFade;
  }
  
  public void resetTransition()
  {
    this.mAlpha = 0;
    this.mTransitionState = 2;
    invalidateSelf();
  }
  
  public void reverseTransition(int paramInt)
  {
    boolean bool = true;
    long l = SystemClock.uptimeMillis();
    if (l - this.mStartTimeMillis > this.mDuration)
    {
      if (this.mTo == 0)
      {
        this.mFrom = 0;
        this.mTo = 255;
        this.mAlpha = 0;
      }
      for (this.mReverse = false;; this.mReverse = true)
      {
        this.mOriginalDuration = paramInt;
        this.mDuration = paramInt;
        this.mTransitionState = 0;
        invalidateSelf();
        return;
        this.mFrom = 255;
        this.mTo = 0;
        this.mAlpha = 255;
      }
    }
    if (this.mReverse) {
      bool = false;
    }
    this.mReverse = bool;
    this.mFrom = this.mAlpha;
    if (this.mReverse)
    {
      paramInt = 0;
      this.mTo = paramInt;
      if (!this.mReverse) {
        break label166;
      }
    }
    label166:
    for (l -= this.mStartTimeMillis;; l = this.mOriginalDuration - (l - this.mStartTimeMillis))
    {
      this.mDuration = ((int)l);
      this.mTransitionState = 0;
      return;
      paramInt = 255;
      break;
    }
  }
  
  public void setCrossFadeEnabled(boolean paramBoolean)
  {
    this.mCrossFade = paramBoolean;
  }
  
  public void startTransition(int paramInt)
  {
    this.mFrom = 0;
    this.mTo = 255;
    this.mAlpha = 0;
    this.mOriginalDuration = paramInt;
    this.mDuration = paramInt;
    this.mReverse = false;
    this.mTransitionState = 0;
    invalidateSelf();
  }
  
  static class TransitionState
    extends LayerDrawable.LayerState
  {
    TransitionState(TransitionState paramTransitionState, TransitionDrawable paramTransitionDrawable, Resources paramResources)
    {
      super(paramTransitionDrawable, paramResources);
    }
    
    public int getChangingConfigurations()
    {
      return this.mChangingConfigurations;
    }
    
    public Drawable newDrawable()
    {
      return new TransitionDrawable(this, (Resources)null, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new TransitionDrawable(this, paramResources, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/TransitionDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */