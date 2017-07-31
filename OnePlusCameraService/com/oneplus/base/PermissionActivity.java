package com.oneplus.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class PermissionActivity
  extends Activity
{
  private static final int ANDROID_M = 23;
  public static final String EXTRA_REQUEST_CODE = "com.oneplus.base.PermissionActivity.extra.REQUEST_CODE";
  public static final String EXTRA_REQUEST_PERMISSIONS_LIST = "com.oneplus.base.PermissionActivity.extra.REQUEST_PREMISSION_LIST";
  private static final int INTERNAL_REQUEST_CODE = 2000;
  private static final String TAG = PermissionActivity.class.getSimpleName();
  private Handler m_Handler;
  private PermissionManagerImpl m_PermissionManager;
  private long m_RequestCode;
  private Set<String> m_RequestPermissions = new HashSet();
  
  private void requestPermissionsInternal(final String[] paramArrayOfString, final int paramInt)
  {
    if (paramArrayOfString == null)
    {
      Log.e(TAG, "requestPermissionsInternal() - No permission to request");
      return;
    }
    if (Build.VERSION.SDK_INT >= 23) {
      try
      {
        getClass().getMethod("requestPermissions", new Class[] { String[].class, Integer.TYPE }).invoke(this, new Object[] { paramArrayOfString, Integer.valueOf(paramInt) });
        return;
      }
      catch (Throwable paramArrayOfString)
      {
        Log.e(TAG, "requestPermissionsInternal() - Error when request permission", paramArrayOfString);
        return;
      }
    }
    this.m_Handler.post(new Runnable()
    {
      public void run()
      {
        int[] arrayOfInt = new int[paramArrayOfString.length];
        int i = 0;
        while (i < arrayOfInt.length)
        {
          arrayOfInt[i] = 0;
          i += 1;
        }
        PermissionActivity.this.onRequestPermissionsResult(paramInt, paramArrayOfString, arrayOfInt);
      }
    });
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.m_Handler = BaseApplication.current().getHandler();
    this.m_PermissionManager = ((PermissionManagerImpl)BaseApplication.current().findComponent(PermissionManagerImpl.class));
    paramBundle = getIntent();
    if (paramBundle != null)
    {
      this.m_RequestCode = paramBundle.getLongExtra("com.oneplus.base.PermissionActivity.extra.REQUEST_CODE", 0L);
      paramBundle = paramBundle.getStringArrayExtra("com.oneplus.base.PermissionActivity.extra.REQUEST_PREMISSION_LIST");
      Log.v(TAG, "onCreate() - Request permission, request code: ", Long.valueOf(this.m_RequestCode), ", hashcode: ", Integer.valueOf(hashCode()));
      int j = paramBundle.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramBundle[i];
        this.m_RequestPermissions.add(localObject);
        i += 1;
      }
      if (this.m_RequestPermissions.size() > 0) {
        requestPermissionsInternal((String[])this.m_RequestPermissions.toArray(new String[0]), 2000);
      }
    }
  }
  
  protected void onDestroy()
  {
    Log.v(TAG, "onDestroy()");
    if (this.m_PermissionManager != null) {
      this.m_PermissionManager.onRequestPermissionsResult(this.m_RequestCode, null, null);
    }
    this.m_Handler = null;
    this.m_PermissionManager = null;
    this.m_RequestPermissions.clear();
    super.onDestroy();
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    Log.v(TAG, "onRequestPermissionsResult() - Request code: ", Long.valueOf(this.m_RequestCode), ", hashcode: ", Integer.valueOf(hashCode()));
    if (this.m_PermissionManager != null) {
      this.m_PermissionManager.onRequestPermissionsResult(this.m_RequestCode, paramArrayOfString, paramArrayOfInt);
    }
    finish();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/PermissionActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */