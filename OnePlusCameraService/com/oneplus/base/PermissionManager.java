package com.oneplus.base;

import com.oneplus.base.component.Component;

public abstract interface PermissionManager
  extends Component
{
  public static final EventKey<PermissionEventArgs> EVENT_PERMISSION_DENIED = new EventKey("PermissionGranted", PermissionEventArgs.class, PermissionManager.class);
  public static final EventKey<PermissionEventArgs> EVENT_PERMISSION_GRANTED = new EventKey("PermissionGranted", PermissionEventArgs.class, PermissionManager.class);
  
  public abstract boolean checkPermission(String paramString);
  
  public abstract void requestPermissions(BaseActivity paramBaseActivity, String[] paramArrayOfString, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/PermissionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */