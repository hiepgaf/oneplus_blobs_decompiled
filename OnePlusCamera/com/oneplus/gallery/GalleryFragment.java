package com.oneplus.gallery;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.BaseFragment;
import com.oneplus.base.BaseFragment.State;
import com.oneplus.base.EventKey;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;

public abstract class GalleryFragment
  extends BaseFragment
{
  public static final String ACTION_ID_BACK = "GalleryFragment.Action.Back";
  public static final EventKey<ActionItemEventArgs> EVENT_ACTION_ITEM_CLICKED = new EventKey("ActionItemClicked", ActionItemEventArgs.class, GalleryFragment.class);
  private static final int MSG_BACK_TO_INITIAL_UI_STATE = -10000;
  public static final PropertyKey<Boolean> PROP_HAS_ACTION_BAR = new PropertyKey("HasActionBar", Boolean.class, GalleryFragment.class, 2, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_BACK_ACTION_NEEDED = new PropertyKey("IsBackActionNeeded", Boolean.class, GalleryFragment.class, 2, Boolean.valueOf(true));
  private final PropertyChangedCallback<BaseActivity.State> m_ActivityStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
    {
      switch ($SWITCH_TABLE$com$oneplus$base$BaseActivity$State()[((BaseActivity.State)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
      {
      case 6: 
      case 8: 
      default: 
        return;
      case 7: 
        GalleryFragment.this.onActivityPause();
        return;
      case 5: 
        GalleryFragment.this.onActivityResume();
        return;
      case 4: 
        GalleryFragment.this.onActivityStart();
        return;
      }
      GalleryFragment.this.onActivityStop();
    }
  };
  private Gallery m_Gallery;
  private GalleryActivity m_GalleryActivity;
  private boolean m_IsInitialUIStateNeeded;
  
  protected GalleryFragment()
  {
    setRetainInstance(true);
  }
  
  public void backToInitialUIState()
  {
    verifyAccess();
    if (this.m_GalleryActivity == null) {}
    while (get(PROP_STATE) == BaseFragment.State.NEW)
    {
      this.m_IsInitialUIStateNeeded = true;
      return;
    }
    this.m_IsInitialUIStateNeeded = false;
    getHandler().removeMessages(55536);
    onBackToInitialUIState();
  }
  
  public final Gallery getGallery()
  {
    return this.m_Gallery;
  }
  
  public final GalleryActivity getGalleryActivity()
  {
    return this.m_GalleryActivity;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    this.m_IsInitialUIStateNeeded = false;
    onBackToInitialUIState();
  }
  
  public final boolean isAttachedToGallery()
  {
    return this.m_Gallery != null;
  }
  
  protected void onActivityPause() {}
  
  protected void onActivityResume() {}
  
  protected void onActivityStart() {}
  
  protected void onActivityStop() {}
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    this.m_GalleryActivity = ((GalleryActivity)paramActivity);
    this.m_GalleryActivity.addCallback(GalleryActivity.PROP_STATE, this.m_ActivityStateChangedCallback);
    if (this.m_Gallery != null) {
      if (this.m_IsInitialUIStateNeeded) {
        break label73;
      }
    }
    label73:
    while (get(PROP_STATE) == BaseFragment.State.NEW)
    {
      return;
      Log.v(this.TAG, "onAttach() - Attach to Gallery");
      this.m_Gallery = this.m_GalleryActivity.getGallery();
      onAttachToGallery(this.m_Gallery);
      break;
    }
    this.m_IsInitialUIStateNeeded = false;
    getHandler().sendMessageAtFrontOfQueue(Message.obtain(getHandler(), 55536));
  }
  
  protected void onAttachToGallery(Gallery paramGallery) {}
  
  protected void onBackToInitialUIState() {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (!this.m_IsInitialUIStateNeeded) {}
    while (this.m_GalleryActivity == null) {
      return;
    }
    this.m_IsInitialUIStateNeeded = false;
    getHandler().sendMessageAtFrontOfQueue(Message.obtain(getHandler(), 55536));
  }
  
  public void onDetach()
  {
    if (this.m_GalleryActivity == null) {}
    for (;;)
    {
      getHandler().removeMessages(55536);
      super.onDetach();
      return;
      this.m_GalleryActivity.removeCallback(GalleryActivity.PROP_STATE, this.m_ActivityStateChangedCallback);
      this.m_GalleryActivity = null;
    }
  }
  
  protected void setSystemUiVisibility(boolean paramBoolean)
  {
    Gallery localGallery = getGallery();
    if (localGallery != null)
    {
      Log.v(this.TAG, "setSystemUiVisibility() - Visible: ", Boolean.valueOf(paramBoolean));
      if (!paramBoolean)
      {
        localGallery.setNavigationBarVisibility(false);
        localGallery.setStatusBarVisibility(false);
      }
    }
    else
    {
      Log.e(this.TAG, "setSystemUiVisibility() - No gallery");
      return;
    }
    localGallery.setNavigationBarVisibility(true);
    localGallery.setStatusBarVisibility(true);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/GalleryFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */