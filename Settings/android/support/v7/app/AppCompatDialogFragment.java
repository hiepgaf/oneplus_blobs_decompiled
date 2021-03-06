package android.support.v7.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.RestrictTo;
import android.support.v4.app.DialogFragment;
import android.view.Window;

public class AppCompatDialogFragment
  extends DialogFragment
{
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    return new AppCompatDialog(getContext(), getTheme());
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public void setupDialog(Dialog paramDialog, int paramInt)
  {
    if ((paramDialog instanceof AppCompatDialog))
    {
      AppCompatDialog localAppCompatDialog = (AppCompatDialog)paramDialog;
      switch (paramInt)
      {
      default: 
        return;
      case 3: 
        paramDialog.getWindow().addFlags(24);
      }
      localAppCompatDialog.supportRequestWindowFeature(1);
      return;
    }
    super.setupDialog(paramDialog, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v7/app/AppCompatDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */