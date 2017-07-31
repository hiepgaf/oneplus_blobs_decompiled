package com.android.server.policy.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.server.policy.OpGlobalActions.Action;
import com.android.server.policy.OpGlobalActions.ActionState;
import java.util.ArrayList;

public class OpGlobalActionEntry
  extends FrameLayout
{
  private static final int RIPPLE_COLOR = -1;
  private OpGlobalActions.Action mAction;
  private TextView mActionConfirmLabel = null;
  private ImageView mActionIcon = null;
  private TextView mActionLabel = null;
  private OpGlobalActionEntryAnimations mAnims;
  private Context mContext;
  private Animation.AnimationListener mOnGlobalActionAnimationListener;
  private OnGlobalActionClickListener mOnGlobalActionClickListener;
  private boolean mSelected = false;
  private OpGlobalActions.ActionState mState = OpGlobalActions.ActionState.INIT;
  
  public OpGlobalActionEntry(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
    LayoutInflater.from(paramContext).inflate(84082689, this, true);
  }
  
  public OpGlobalActionEntry(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public OpGlobalActionEntry(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  private void setAction(OpGlobalActions.Action paramAction)
  {
    this.mAction = paramAction;
  }
  
  private void setActionConfirmText(int paramInt)
  {
    this.mActionConfirmLabel = ((TextView)findViewById(84672515));
    this.mActionConfirmLabel.setText(paramInt);
    this.mActionConfirmLabel.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        OpGlobalActionEntry.-get3(OpGlobalActionEntry.this).onMissClicked();
      }
    });
  }
  
  private void setActionIcon(Drawable paramDrawable, int paramInt)
  {
    this.mActionIcon = ((ImageView)findViewById(84672513));
    this.mActionIcon.setBackgroundResource(paramInt);
    this.mActionIcon.getBackground().setAlpha(255);
    this.mActionIcon.setImageDrawable(paramDrawable);
    this.mActionIcon.getLayoutParams().height = 216;
    this.mActionIcon.getLayoutParams().width = 216;
    this.mActionIcon.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if ((OpGlobalActionEntry.-get5(OpGlobalActionEntry.this) == OpGlobalActions.ActionState.SHOWING) || (OpGlobalActionEntry.-get5(OpGlobalActionEntry.this) == OpGlobalActions.ActionState.ADVANCED_SHOWN)) {
          OpGlobalActionEntry.-set0(OpGlobalActionEntry.this, true);
        }
        if (OpGlobalActionEntry.-get4(OpGlobalActionEntry.this))
        {
          OpGlobalActionEntry.-get3(OpGlobalActionEntry.this).onClicked(OpGlobalActionEntry.-get0(OpGlobalActionEntry.this));
          return;
        }
        OpGlobalActionEntry.-get3(OpGlobalActionEntry.this).onMissClicked();
      }
    });
    this.mActionIcon.setOnLongClickListener(new View.OnLongClickListener()
    {
      public boolean onLongClick(View paramAnonymousView)
      {
        if (OpGlobalActionEntry.-get0(OpGlobalActionEntry.this) == OpGlobalActions.Action.POWER_OFF) {
          OpGlobalActionEntry.-set0(OpGlobalActionEntry.this, true);
        }
        if (OpGlobalActionEntry.-get4(OpGlobalActionEntry.this)) {
          return OpGlobalActionEntry.-get3(OpGlobalActionEntry.this).onLongPressed(OpGlobalActionEntry.-get0(OpGlobalActionEntry.this));
        }
        return false;
      }
    });
  }
  
  private void setActionText(int paramInt)
  {
    this.mActionLabel = ((TextView)findViewById(84672514));
    this.mActionLabel.setText(paramInt);
    this.mActionLabel.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        OpGlobalActionEntry.-get3(OpGlobalActionEntry.this).onMissClicked();
      }
    });
  }
  
  private void setAnimation(OpGlobalActionEntryAnimations paramOpGlobalActionEntryAnimations)
  {
    this.mAnims = paramOpGlobalActionEntryAnimations;
  }
  
  private void setOnGlobalActionAnimationListener(Animation.AnimationListener paramAnimationListener)
  {
    this.mOnGlobalActionAnimationListener = paramAnimationListener;
  }
  
  private void setOnGlobalActionClickListener(OnGlobalActionClickListener paramOnGlobalActionClickListener)
  {
    this.mOnGlobalActionClickListener = paramOnGlobalActionClickListener;
  }
  
  private void startAnimateBackground()
  {
    ValueAnimator localValueAnimator = ValueAnimator.ofInt(new int[] { 0, 400 });
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        int i = ((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue();
        float f = (400.0F - i) / 400.0F;
        this.val$bg.setAlpha((int)(f * 255.0F));
        OpGlobalActionEntry.-get1(OpGlobalActionEntry.this).setAlpha((400.0F - i) / 400.0F * 0.87F);
      }
    });
    localValueAnimator.setDuration(400L);
    localValueAnimator.setInterpolator(new AccelerateInterpolator());
    localValueAnimator.setStartDelay(400L);
    localValueAnimator.start();
  }
  
  private void startAnimateIcon()
  {
    ((OpGlobalActionIcon)this.mActionIcon.getDrawable()).startAnimateConfirmed();
    this.mActionIcon.setForeground(null);
    this.mActionIcon.setEnabled(false);
  }
  
  public int getMyIndex()
  {
    return this.mAction.ordinal();
  }
  
  public void setRippleColor(long paramLong)
  {
    RippleDrawable localRippleDrawable = (RippleDrawable)this.mContext.getDrawable(84017159);
    int i = (int)paramLong;
    localRippleDrawable.setColor(new ColorStateList(new int[][] { new int[0] }, new int[] { i }));
    this.mActionIcon.setForeground(localRippleDrawable);
  }
  
  public void setup(Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, OnGlobalActionClickListener paramOnGlobalActionClickListener, Animation.AnimationListener paramAnimationListener, OpGlobalActions.Action paramAction, OpGlobalActionEntryAnimations paramOpGlobalActionEntryAnimations, int paramInt4)
  {
    setActionIcon(paramDrawable, paramInt1);
    setActionText(paramInt2);
    setActionConfirmText(paramInt3);
    setOnGlobalActionClickListener(paramOnGlobalActionClickListener);
    setOnGlobalActionAnimationListener(paramAnimationListener);
    setAction(paramAction);
    setAnimation(paramOpGlobalActionEntryAnimations);
    paramDrawable = new FrameLayout.LayoutParams(-2, -2);
    paramDrawable.gravity = 1;
    paramDrawable.topMargin = paramInt4;
    setLayoutParams(paramDrawable);
    setRippleColor(-1L);
  }
  
  public void startAnimate(OpGlobalActions.ActionState paramActionState)
  {
    AnimationSet localAnimationSet = null;
    this.mState = paramActionState;
    if (paramActionState == OpGlobalActions.ActionState.SHOWING)
    {
      localAnimationSet = (AnimationSet)this.mAnims.getShowAnimSets().get(getMyIndex());
      this.mSelected = false;
    }
    do
    {
      for (;;)
      {
        if (localAnimationSet != null) {
          startAnimation(localAnimationSet);
        }
        return;
        if (paramActionState == OpGlobalActions.ActionState.ADVANCED_SHOWN)
        {
          if (this.mAction == OpGlobalActions.Action.POWER_OFF) {
            localAnimationSet = this.mAnims.getHideAnimSet();
          }
          for (;;)
          {
            this.mSelected = false;
            break;
            if (this.mAction == OpGlobalActions.Action.REBOOT)
            {
              paramActionState = new FrameLayout.LayoutParams(-2, -2);
              paramActionState.topMargin = 477;
              paramActionState.gravity = 1;
              setLayoutParams(paramActionState);
              localAnimationSet = (AnimationSet)this.mAnims.getAdvShowAnimSets().get(getMyIndex());
              localAnimationSet.setAnimationListener(this.mOnGlobalActionAnimationListener);
            }
            else
            {
              localAnimationSet = (AnimationSet)this.mAnims.getAdvShowAnimSets().get(getMyIndex());
            }
          }
        }
        if (paramActionState == OpGlobalActions.ActionState.SELECTED)
        {
          if (this.mSelected)
          {
            paramActionState = new FrameLayout.LayoutParams(-2, -2);
            paramActionState.topMargin = 819;
            paramActionState.gravity = 1;
            setLayoutParams(paramActionState);
            setZ(0.1F);
            localAnimationSet = (AnimationSet)this.mAnims.getSelectedAnimSets().get(getMyIndex());
            startSelectedAnimator();
          }
          else
          {
            localAnimationSet = this.mAnims.getHideAnimSet();
            localAnimationSet.setAnimationListener(this.mOnGlobalActionAnimationListener);
          }
        }
        else
        {
          if (paramActionState != OpGlobalActions.ActionState.ADVANCE_SELECTED) {
            break;
          }
          if (this.mSelected)
          {
            paramActionState = new FrameLayout.LayoutParams(-2, -2);
            paramActionState.topMargin = 819;
            paramActionState.gravity = 1;
            setLayoutParams(paramActionState);
            setZ(0.1F);
            localAnimationSet = (AnimationSet)this.mAnims.getAdvSelectedAnimSets().get(getMyIndex());
            startSelectedAnimator();
          }
          else if (this.mAction == OpGlobalActions.Action.POWER_OFF)
          {
            clearAnimation();
            setVisibility(8);
          }
          else
          {
            localAnimationSet = this.mAnims.getHideAnimSet();
            localAnimationSet.setAnimationListener(this.mOnGlobalActionAnimationListener);
          }
        }
      }
    } while (paramActionState != OpGlobalActions.ActionState.CONFIRMED);
    if (!this.mSelected)
    {
      clearAnimation();
      setVisibility(8);
      return;
    }
    startConfirmedAnimator();
  }
  
  public void startAnimateShowAdv()
  {
    startAnimation((AnimationSet)this.mAnims.getAdvShowAnimSets().get(getMyIndex()));
  }
  
  public void startConfirmedAnimator()
  {
    startAnimateBackground();
    startAnimateIcon();
  }
  
  public void startSelectedAnimator()
  {
    ValueAnimator localValueAnimator = ValueAnimator.ofInt(new int[] { 0, 400 });
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        int i = ((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue();
        if (i < 200)
        {
          f = (200.0F - i) / 200.0F;
          OpGlobalActionEntry.-get2(OpGlobalActionEntry.this).setAlpha(f * 0.87F);
          OpGlobalActionEntry.-get1(OpGlobalActionEntry.this).setAlpha(0.0F);
          return;
        }
        float f = (i - 200.0F) / 200.0F;
        OpGlobalActionEntry.-get1(OpGlobalActionEntry.this).setAlpha(f * 0.87F);
        OpGlobalActionEntry.-get2(OpGlobalActionEntry.this).setAlpha(0.0F);
      }
    });
    localValueAnimator.setDuration(400L);
    localValueAnimator.setInterpolator(new AccelerateInterpolator());
    localValueAnimator.setStartDelay(400L);
    localValueAnimator.start();
  }
  
  public static abstract interface OnGlobalActionClickListener
  {
    public abstract void onClicked(OpGlobalActions.Action paramAction);
    
    public abstract boolean onLongPressed(OpGlobalActions.Action paramAction);
    
    public abstract void onMissClicked();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/ui/OpGlobalActionEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */