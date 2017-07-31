package android.support.v4.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DialogFragment
  extends Fragment
  implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener
{
  private static final String SAVED_BACK_STACK_ID = "android:backStackId";
  private static final String SAVED_CANCELABLE = "android:cancelable";
  private static final String SAVED_DIALOG_STATE_TAG = "android:savedDialogState";
  private static final String SAVED_SHOWS_DIALOG = "android:showsDialog";
  private static final String SAVED_STYLE = "android:style";
  private static final String SAVED_THEME = "android:theme";
  public static final int STYLE_NORMAL = 0;
  public static final int STYLE_NO_FRAME = 2;
  public static final int STYLE_NO_INPUT = 3;
  public static final int STYLE_NO_TITLE = 1;
  int mBackStackId = -1;
  boolean mCancelable = true;
  Dialog mDialog;
  boolean mDismissed;
  boolean mShownByMe;
  boolean mShowsDialog = true;
  int mStyle = 0;
  int mTheme = 0;
  boolean mViewDestroyed;
  
  public void dismiss()
  {
    dismissInternal(false);
  }
  
  public void dismissAllowingStateLoss()
  {
    dismissInternal(true);
  }
  
  void dismissInternal(boolean paramBoolean)
  {
    if (!this.mDismissed)
    {
      this.mDismissed = true;
      this.mShownByMe = false;
      if (this.mDialog != null) {
        break label61;
      }
    }
    FragmentTransaction localFragmentTransaction;
    for (;;)
    {
      this.mViewDestroyed = true;
      if (this.mBackStackId >= 0) {
        break;
      }
      localFragmentTransaction = getFragmentManager().beginTransaction();
      localFragmentTransaction.remove(this);
      if (paramBoolean) {
        break label94;
      }
      localFragmentTransaction.commit();
      return;
      return;
      label61:
      this.mDialog.dismiss();
      this.mDialog = null;
    }
    getFragmentManager().popBackStack(this.mBackStackId, 1);
    this.mBackStackId = -1;
    return;
    label94:
    localFragmentTransaction.commitAllowingStateLoss();
  }
  
  public Dialog getDialog()
  {
    return this.mDialog;
  }
  
  public LayoutInflater getLayoutInflater(Bundle paramBundle)
  {
    if (this.mShowsDialog)
    {
      this.mDialog = onCreateDialog(paramBundle);
      switch (this.mStyle)
      {
      }
    }
    while (this.mDialog == null)
    {
      return (LayoutInflater)this.mActivity.getSystemService("layout_inflater");
      return super.getLayoutInflater(paramBundle);
      this.mDialog.getWindow().addFlags(24);
      this.mDialog.requestWindowFeature(1);
    }
    return (LayoutInflater)this.mDialog.getContext().getSystemService("layout_inflater");
  }
  
  public boolean getShowsDialog()
  {
    return this.mShowsDialog;
  }
  
  public int getTheme()
  {
    return this.mTheme;
  }
  
  public boolean isCancelable()
  {
    return this.mCancelable;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    View localView;
    if (this.mShowsDialog)
    {
      localView = getView();
      if (localView != null) {
        break label65;
      }
      this.mDialog.setOwnerActivity(getActivity());
      this.mDialog.setCancelable(this.mCancelable);
      this.mDialog.setOnCancelListener(this);
      this.mDialog.setOnDismissListener(this);
      if (paramBundle != null) {
        break label93;
      }
    }
    label65:
    label93:
    do
    {
      return;
      return;
      if (localView.getParent() == null)
      {
        this.mDialog.setContentView(localView);
        break;
      }
      throw new IllegalStateException("DialogFragment can not be attached to a container view");
      paramBundle = paramBundle.getBundle("android:savedDialogState");
    } while (paramBundle == null);
    this.mDialog.onRestoreInstanceState(paramBundle);
  }
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    if (this.mShownByMe) {
      return;
    }
    this.mDismissed = false;
  }
  
  public void onCancel(DialogInterface paramDialogInterface) {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (this.mContainerId != 0) {}
    for (boolean bool = false;; bool = true)
    {
      this.mShowsDialog = bool;
      if (paramBundle != null) {
        break;
      }
      return;
    }
    this.mStyle = paramBundle.getInt("android:style", 0);
    this.mTheme = paramBundle.getInt("android:theme", 0);
    this.mCancelable = paramBundle.getBoolean("android:cancelable", true);
    this.mShowsDialog = paramBundle.getBoolean("android:showsDialog", this.mShowsDialog);
    this.mBackStackId = paramBundle.getInt("android:backStackId", -1);
  }
  
  @NonNull
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    return new Dialog(getActivity(), getTheme());
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    if (this.mDialog == null) {
      return;
    }
    this.mViewDestroyed = true;
    this.mDialog.dismiss();
    this.mDialog = null;
  }
  
  public void onDetach()
  {
    super.onDetach();
    if (this.mShownByMe) {}
    while (this.mDismissed) {
      return;
    }
    this.mDismissed = true;
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    if (this.mViewDestroyed) {
      return;
    }
    dismissInternal(true);
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (this.mDialog == null)
    {
      if (this.mStyle != 0) {
        break label71;
      }
      label19:
      if (this.mTheme != 0) {
        break label84;
      }
      label26:
      if (!this.mCancelable) {
        break label97;
      }
      label33:
      if (!this.mShowsDialog) {
        break label110;
      }
    }
    for (;;)
    {
      if (this.mBackStackId != -1) {
        break label123;
      }
      return;
      Bundle localBundle = this.mDialog.onSaveInstanceState();
      if (localBundle == null) {
        break;
      }
      paramBundle.putBundle("android:savedDialogState", localBundle);
      break;
      label71:
      paramBundle.putInt("android:style", this.mStyle);
      break label19;
      label84:
      paramBundle.putInt("android:theme", this.mTheme);
      break label26;
      label97:
      paramBundle.putBoolean("android:cancelable", this.mCancelable);
      break label33;
      label110:
      paramBundle.putBoolean("android:showsDialog", this.mShowsDialog);
    }
    label123:
    paramBundle.putInt("android:backStackId", this.mBackStackId);
  }
  
  public void onStart()
  {
    super.onStart();
    if (this.mDialog == null) {
      return;
    }
    this.mViewDestroyed = false;
    this.mDialog.show();
  }
  
  public void onStop()
  {
    super.onStop();
    if (this.mDialog == null) {
      return;
    }
    this.mDialog.hide();
  }
  
  public void setCancelable(boolean paramBoolean)
  {
    this.mCancelable = paramBoolean;
    if (this.mDialog == null) {
      return;
    }
    this.mDialog.setCancelable(paramBoolean);
  }
  
  public void setShowsDialog(boolean paramBoolean)
  {
    this.mShowsDialog = paramBoolean;
  }
  
  public void setStyle(int paramInt1, int paramInt2)
  {
    this.mStyle = paramInt1;
    if (this.mStyle == 2) {
      this.mTheme = 16973913;
    }
    for (;;)
    {
      if (paramInt2 != 0) {
        break label36;
      }
      return;
      if (this.mStyle == 3) {
        break;
      }
    }
    label36:
    this.mTheme = paramInt2;
  }
  
  public int show(FragmentTransaction paramFragmentTransaction, String paramString)
  {
    this.mDismissed = false;
    this.mShownByMe = true;
    paramFragmentTransaction.add(this, paramString);
    this.mViewDestroyed = false;
    this.mBackStackId = paramFragmentTransaction.commit();
    return this.mBackStackId;
  }
  
  public void show(FragmentManager paramFragmentManager, String paramString)
  {
    this.mDismissed = false;
    this.mShownByMe = true;
    paramFragmentManager = paramFragmentManager.beginTransaction();
    paramFragmentManager.add(this, paramString);
    paramFragmentManager.commit();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L, 3L})
  private static @interface DialogStyle {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/DialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */