package com.oneplus.gallery2;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import com.oneplus.base.BaseActivity;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;

public abstract class GalleryDialogFragment
  extends DialogFragment
{
  private static final String TAG = "GalleryDialogFragment";
  private final PropertyChangedCallback<Boolean> m_ActivityStateCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {}
      for (;;)
      {
        GalleryDialogFragment.this.m_BaseActivity.removeCallback(paramAnonymousPropertyKey, this);
        return;
        if (GalleryDialogFragment.this.m_DialogShowLater) {
          GalleryDialogFragment.this.show(GalleryDialogFragment.this.m_BaseActivity, GalleryDialogFragment.this.m_FragmentTag);
        }
      }
    }
  };
  private BaseActivity m_BaseActivity;
  private Handle m_DialogHandle;
  private boolean m_DialogShowLater;
  private String m_FragmentTag;
  private boolean m_IsDismissDelayed;
  private boolean m_IsInstanceStateSaved;
  
  public GalleryDialogFragment()
  {
    setRetainInstance(true);
  }
  
  public void dismiss()
  {
    if (this.m_DialogShowLater) {}
    for (;;)
    {
      this.m_DialogShowLater = false;
      this.m_BaseActivity.removeCallback(BaseActivity.PROP_IS_RUNNING, this.m_ActivityStateCallback);
      return;
      super.dismiss();
      this.m_DialogHandle = Handle.close(this.m_DialogHandle);
    }
  }
  
  public void dismissAllowingStateLoss()
  {
    if (!this.m_IsInstanceStateSaved) {}
    try
    {
      super.dismissAllowingStateLoss();
      for (;;)
      {
        this.m_DialogHandle = Handle.close(this.m_DialogHandle);
        return;
        Log.w("GalleryDialogFragment", "dismissAllowingStateLoss() - Dismiss after saving instance state");
        this.m_IsDismissDelayed = true;
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e("GalleryDialogFragment", "dismissAllowingStateLoss() - Error when dismiss dialog fragment", localThrowable);
      }
    }
  }
  
  public GalleryActivity getGalleryActivity()
  {
    return (GalleryActivity)getActivity();
  }
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    if (!this.m_IsDismissDelayed)
    {
      if (!Handle.isValid(this.m_DialogHandle)) {}
    }
    else
    {
      Log.w("GalleryDialogFragment", "onAttach() - Dismiss");
      this.m_IsDismissDelayed = false;
      dismiss();
      return;
    }
    this.m_DialogHandle = ((GalleryActivity)paramActivity).getGallery().notifyShowDialog();
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    dismissAllowingStateLoss();
  }
  
  public void onDetach()
  {
    this.m_DialogHandle = Handle.close(this.m_DialogHandle);
    super.onDetach();
  }
  
  public void onDismiss(DialogInterface paramDialogInterface) {}
  
  public void onResume()
  {
    super.onResume();
    this.m_IsInstanceStateSaved = false;
    if (!this.m_IsDismissDelayed) {
      return;
    }
    Log.w("GalleryDialogFragment", "onResume() - Dismiss");
    this.m_IsDismissDelayed = false;
    dismiss();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    this.m_IsInstanceStateSaved = true;
  }
  
  @Deprecated
  public int show(FragmentTransaction paramFragmentTransaction, String paramString)
  {
    Log.w("GalleryDialogFragment", "show(FragmentTransaction...) - this method is deprecated, use show(BaseActivity baseActivity...) instead");
    return -1;
  }
  
  @Deprecated
  public void show(FragmentManager paramFragmentManager, String paramString)
  {
    Log.w("GalleryDialogFragment", "show(FragmentManager...) - this method is deprecated, use show(BaseActivity baseActivity...) instead");
  }
  
  public void show(BaseActivity paramBaseActivity, String paramString)
  {
    this.m_FragmentTag = paramString;
    this.m_BaseActivity = paramBaseActivity;
    if (((Boolean)paramBaseActivity.get(BaseActivity.PROP_IS_RUNNING)).booleanValue())
    {
      super.show(paramBaseActivity.getFragmentManager(), paramString);
      return;
    }
    Log.w("GalleryDialogFragment", "show() - activity is not running, show later");
    this.m_DialogShowLater = true;
    paramBaseActivity.addCallback(BaseActivity.PROP_IS_RUNNING, this.m_ActivityStateCallback);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/GalleryDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */