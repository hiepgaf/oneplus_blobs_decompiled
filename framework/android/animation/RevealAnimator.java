package android.animation;

import android.view.RenderNodeAnimator;
import android.view.View;

public class RevealAnimator
  extends RenderNodeAnimator
{
  private View mClipView;
  
  public RevealAnimator(View paramView, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
  {
    super(paramInt1, paramInt2, paramFloat1, paramFloat2);
    this.mClipView = paramView;
    setTarget(this.mClipView);
  }
  
  protected void onFinished()
  {
    this.mClipView.setRevealClip(false, 0.0F, 0.0F, 0.0F);
    super.onFinished();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/RevealAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */