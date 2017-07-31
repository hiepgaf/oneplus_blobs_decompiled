package com.oneplus.gallery.ui;

import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface ProcessingDialog
  extends Component
{
  public static final PropertyKey<Boolean> PROP_IS_VISIBLE = new PropertyKey("IsVisible", Boolean.class, ProcessingDialog.class, Boolean.valueOf(false));
  
  public abstract Handle showProcessingDialog(CharSequence paramCharSequence, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/ui/ProcessingDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */