package com.oneplus.camera.ui;

import android.graphics.drawable.Drawable;
import android.view.View.OnClickListener;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface OnScreenHint
  extends Component
{
  public static final int FLAG_HAS_BACKGROUND = 4;
  public static final int FLAG_HIGHLIGHT_BACKGROUND = 32;
  public static final int FLAG_HIGH_PRIORITY = 8;
  public static final int FLAG_INAVTIVE = 64;
  public static final int FLAG_INVISIBLE_WHEN_SELF_TIMER = 16;
  public static final int FLAG_NO_ANIMATION = 2;
  public static final int FLAG_VISIBLE_WHEN_CAPTURING = 1;
  public static final PropertyKey<Boolean> PROP_IS_VISIBLE = new PropertyKey("IsVisible", Boolean.class, OnScreenHint.class, Boolean.valueOf(false));
  
  public abstract Handle showHint(Drawable paramDrawable, View.OnClickListener paramOnClickListener, int paramInt);
  
  public abstract Handle showHint(CharSequence paramCharSequence, int paramInt);
  
  public abstract Handle showHint(CharSequence paramCharSequence, View.OnClickListener paramOnClickListener, int paramInt);
  
  public abstract Handle showSecondaryHint(CharSequence paramCharSequence, int paramInt);
  
  public abstract boolean updateHint(Handle paramHandle, Drawable paramDrawable, int paramInt);
  
  public abstract boolean updateHint(Handle paramHandle, CharSequence paramCharSequence, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/OnScreenHint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */