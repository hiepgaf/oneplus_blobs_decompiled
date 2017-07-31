package com.oneplus.camera;

import android.app.Dialog;
import android.content.DialogInterface.OnDismissListener;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface DialogManager
  extends Component
{
  public static final int FLAG_CUSTOM_KEY_LISTENER = 1;
  public static final PropertyKey<Integer> PROP_DEFAULT_DIALOG_THEME = new PropertyKey("DefaultDialogTheme", Integer.class, DialogManager.class, Integer.valueOf(16974394));
  public static final PropertyKey<Boolean> PROP_HAS_DIALOG = new PropertyKey("HasDialog", Boolean.class, DialogManager.class, Boolean.valueOf(false));
  
  public abstract Handle showDialog(Dialog paramDialog, DialogInterface.OnDismissListener paramOnDismissListener, DialogParams paramDialogParams1, DialogParams paramDialogParams2, int paramInt);
  
  public static class DialogParams
    implements Cloneable
  {
    public int bottomMargin;
    public int height = -2;
    public int leftMargin;
    public int maxHeight = -1;
    public int maxWidth = -1;
    public int rightMargin;
    public int topMargin;
    public int width = -1;
    
    public DialogParams clone()
    {
      try
      {
        DialogParams localDialogParams = (DialogParams)super.clone();
        return localDialogParams;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new RuntimeException(localCloneNotSupportedException);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/DialogManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */