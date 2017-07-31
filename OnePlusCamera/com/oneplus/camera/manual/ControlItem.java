package com.oneplus.camera.manual;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

class ControlItem
{
  private final float DISABLED_ALPHA = 0.4F;
  private final View m_ControlContainer;
  private final ImageView m_ControlImageView;
  private ControlItemListener m_ControlItemListener;
  private final TextView m_ControlTextView;
  private final View m_ControlTouchView;
  private final ControlType m_ControlType;
  
  public ControlItem(ControlType paramControlType, View paramView1, ImageView paramImageView, TextView paramTextView, View paramView2)
  {
    this.m_ControlContainer = paramView1;
    this.m_ControlImageView = paramImageView;
    this.m_ControlTextView = paramTextView;
    this.m_ControlTouchView = paramView2;
    this.m_ControlType = paramControlType;
    this.m_ControlTouchView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (ControlItem.-get0(ControlItem.this) != null) {
          ControlItem.-get0(ControlItem.this).onControlItemClick(ControlItem.-get1(ControlItem.this));
        }
      }
    });
  }
  
  public View getControlContainer()
  {
    return this.m_ControlContainer;
  }
  
  public ControlType getControlType()
  {
    return this.m_ControlType;
  }
  
  public void setControlItemListener(ControlItemListener paramControlItemListener)
  {
    this.m_ControlItemListener = paramControlItemListener;
  }
  
  public void setImage(Drawable paramDrawable)
  {
    if (this.m_ControlImageView != null) {
      this.m_ControlImageView.setImageDrawable(paramDrawable);
    }
  }
  
  public void setSelected(boolean paramBoolean)
  {
    if (this.m_ControlContainer != null) {
      this.m_ControlContainer.setSelected(paramBoolean);
    }
  }
  
  public void setText(String paramString)
  {
    if (this.m_ControlTextView != null) {
      this.m_ControlTextView.setText(paramString);
    }
  }
  
  public void setTextAppearance(Context paramContext, int paramInt)
  {
    if (this.m_ControlTextView != null) {
      this.m_ControlTextView.setTextAppearance(paramContext, paramInt);
    }
  }
  
  public void setTouchEnabled(boolean paramBoolean)
  {
    if (this.m_ControlTouchView != null) {
      this.m_ControlTouchView.setEnabled(paramBoolean);
    }
  }
  
  public void setUIEnabled(boolean paramBoolean)
  {
    float f2 = 1.0F;
    Object localObject;
    if (this.m_ControlImageView != null)
    {
      localObject = this.m_ControlImageView;
      if (paramBoolean)
      {
        f1 = 1.0F;
        ((ImageView)localObject).setAlpha(f1);
      }
    }
    else if (this.m_ControlTextView != null)
    {
      localObject = this.m_ControlTextView;
      if (!paramBoolean) {
        break label59;
      }
    }
    label59:
    for (float f1 = f2;; f1 = 0.4F)
    {
      ((TextView)localObject).setAlpha(f1);
      return;
      f1 = 0.4F;
      break;
    }
  }
  
  public static abstract interface ControlItemListener
  {
    public abstract void onControlItemClick(ControlType paramControlType);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/ControlItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */