package com.oneplus.base;

import android.view.View.OnClickListener;

public abstract interface SnackBarHost
  extends BaseObject
{
  public static final PropertyKey<Boolean> PROP_IS_SNACKBAR_VISIBLE = new PropertyKey("IsSnackbarVisible", Boolean.class, SnackBarHost.class, Boolean.valueOf(false));
  public static final PropertyKey<Float> PROP_SNACKBAR_VISIBLE_HEIGHT = new PropertyKey("SnackbarVisibleHeight", Float.class, SnackBarHost.class, Float.valueOf(0.0F));
  
  public abstract Handle showSnackbar(CharSequence paramCharSequence1, CharSequence paramCharSequence2, View.OnClickListener paramOnClickListener);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/SnackBarHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */