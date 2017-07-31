package com.oneplus.camera.ui;

import android.view.View;
import android.widget.ProgressBar;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import java.util.List;

public abstract interface SecondLayerBar
  extends Component
{
  public static final int FLAG_NO_ANIMATION = 1;
  public static final PropertyKey<Boolean> PROP_IS_VISIBLE = new PropertyKey("IsVisible", Boolean.class, SecondLayerBar.class, 2, Boolean.valueOf(false));
  
  public abstract <TValue extends View> Handle show(float paramFloat1, float paramFloat2, List<TValue> paramList, int paramInt);
  
  public abstract <TValue extends View> Handle show(float paramFloat1, float paramFloat2, List<TValue> paramList, ProgressBar paramProgressBar, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/SecondLayerBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */