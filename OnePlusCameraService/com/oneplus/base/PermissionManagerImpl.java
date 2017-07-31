package com.oneplus.base;

import android.app.Application;
import android.content.Intent;
import android.os.Build.VERSION;
import com.oneplus.base.component.BasicComponent;
import com.oneplus.base.component.ComponentOwner;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class PermissionManagerImpl
  extends BasicComponent
  implements PermissionManager
{
  private static final Object SYNC_REQUEST_PERMISSIONS = new Object();
  private Method m_CheckSelfPermissionMethod;
  private Map<Long, Long> m_ContextRequestCount = new Hashtable();
  private Map<Long, List<String>> m_PenddingRequestPermissions = new Hashtable();
  private Map<Long, BaseActivity> m_RequestingContexts = new Hashtable();
  private Map<Long, List<String>> m_RequestingPermissions = new Hashtable();
  
  public PermissionManagerImpl(ComponentOwner paramComponentOwner)
  {
    super("Permission Manager Impl", paramComponentOwner, true);
    enableEventLogs(EVENT_PERMISSION_GRANTED, 256);
    enableEventLogs(EVENT_PERMISSION_DENIED, 256);
  }
  
  private void startPermissionActivity(BaseActivity paramBaseActivity)
  {
    if (paramBaseActivity == null)
    {
      Log.e(this.TAG, "startPermissionActivity() - context is null.");
      return;
    }
    long l = paramBaseActivity.getId();
    Intent localIntent = new Intent(paramBaseActivity, PermissionActivity.class);
    List localList = (List)this.m_RequestingPermissions.get(Long.valueOf(l));
    String[] arrayOfString = new String[localList.size()];
    int i = localList.size() - 1;
    while (i >= 0)
    {
      arrayOfString[i] = ((String)localList.get(i));
      i -= 1;
    }
    localIntent.putExtra("com.oneplus.base.PermissionActivity.extra.REQUEST_PREMISSION_LIST", arrayOfString);
    localIntent.putExtra("com.oneplus.base.PermissionActivity.extra.REQUEST_CODE", l);
    paramBaseActivity.startActivity(localIntent);
  }
  
  public boolean checkPermission(String paramString)
  {
    if (Build.VERSION.SDK_INT < 23) {
      return true;
    }
    try
    {
      Class localClass;
      if (this.m_CheckSelfPermissionMethod == null) {
        localClass = Application.class;
      }
      for (;;)
      {
        if (localClass != null) {}
        try
        {
          this.m_CheckSelfPermissionMethod = localClass.getDeclaredMethod("checkSelfPermission", new Class[] { String.class });
          Log.v(this.TAG, "checkPermission() - Implemented in ", localClass.getSimpleName());
          if (((Integer)this.m_CheckSelfPermissionMethod.invoke(BaseApplication.current(), new Object[] { paramString })).intValue() != 0) {
            break;
          }
          return true;
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          localClass = localClass.getSuperclass();
        }
      }
      return false;
    }
    catch (Throwable paramString)
    {
      Log.e(this.TAG, "checkPermission() - Fail to check permission", paramString);
    }
    return false;
  }
  
  protected void onDeinitialize()
  {
    this.m_CheckSelfPermissionMethod = null;
    this.m_RequestingContexts.clear();
    this.m_PenddingRequestPermissions.clear();
    this.m_RequestingPermissions.clear();
    this.m_ContextRequestCount.clear();
    super.onDeinitialize();
  }
  
  void onRequestPermissionsResult(long paramLong, String[] arg3, final int[] paramArrayOfInt)
  {
    if ((??? == null) || (paramArrayOfInt == null)) {
      synchronized (SYNC_REQUEST_PERMISSIONS)
      {
        paramArrayOfInt = (Long)this.m_ContextRequestCount.get(Long.valueOf(paramLong));
        if ((paramArrayOfInt != null) && (paramArrayOfInt.longValue() > 1L))
        {
          Log.v(this.TAG, "onRequestPermissionsResult() - request permission again, id : " + paramLong);
          this.m_ContextRequestCount.put(Long.valueOf(paramLong), Long.valueOf(paramArrayOfInt.longValue() - 1L));
          startPermissionActivity((BaseActivity)this.m_RequestingContexts.get(Long.valueOf(paramLong)));
          return;
        }
        this.m_RequestingContexts.remove(Long.valueOf(paramLong));
        this.m_PenddingRequestPermissions.remove(Long.valueOf(paramLong));
        this.m_RequestingPermissions.remove(Long.valueOf(paramLong));
        Log.v(this.TAG, "onRequestPermissionsResult() - Remove request: ", Long.valueOf(paramLong));
      }
    }
    final Object localObject = BaseApplication.current();
    int i = ???.length - 1;
    if (i >= 0)
    {
      PermissionEventArgs localPermissionEventArgs = new PermissionEventArgs(???[i]);
      if (paramArrayOfInt[i] == 0)
      {
        ((BaseApplication)localObject).notifyPermissionGranted(???[i]);
        raise(EVENT_PERMISSION_GRANTED, localPermissionEventArgs);
      }
      for (;;)
      {
        i -= 1;
        break;
        ((BaseApplication)localObject).notifyPermissionDenied(???[i]);
        raise(EVENT_PERMISSION_DENIED, localPermissionEventArgs);
      }
    }
    Log.v(this.TAG, "onRequestPermissionsResult() - Request permissions: ", Arrays.toString(???), ", result: ", Arrays.toString(paramArrayOfInt));
    synchronized (SYNC_REQUEST_PERMISSIONS)
    {
      this.m_RequestingPermissions.remove(Long.valueOf(paramLong));
      if (this.m_ContextRequestCount.get(Long.valueOf(paramLong)) != null) {
        this.m_ContextRequestCount.put(Long.valueOf(paramLong), Long.valueOf(((Long)this.m_ContextRequestCount.get(Long.valueOf(paramLong))).longValue() - 1L));
      }
      paramArrayOfInt = (BaseActivity)this.m_RequestingContexts.remove(Long.valueOf(paramLong));
      localObject = (List)this.m_PenddingRequestPermissions.remove(Long.valueOf(paramLong));
      if ((localObject != null) && (((List)localObject).size() > 0)) {
        HandlerUtils.post(this, new Runnable()
        {
          public void run()
          {
            PermissionManagerImpl.this.requestPermissions(paramArrayOfInt, (String[])localObject.toArray(new String[0]), 0);
          }
        });
      }
      return;
    }
  }
  
  public void requestPermissions(BaseActivity paramBaseActivity, String[] paramArrayOfString, int paramInt)
  {
    if ((paramBaseActivity == null) || (paramArrayOfString == null)) {}
    while (paramArrayOfString.length < 1)
    {
      Log.v(this.TAG, "requestPermissions() - Permission list is empty, ignore");
      return;
    }
    for (;;)
    {
      long l;
      Object localObject3;
      ArrayList localArrayList;
      synchronized (SYNC_REQUEST_PERMISSIONS)
      {
        l = paramBaseActivity.getId();
        Log.v(this.TAG, "requestPermissions() - Id: ", Long.valueOf(l));
        if (this.m_ContextRequestCount.get(Long.valueOf(l)) != null)
        {
          this.m_ContextRequestCount.put(Long.valueOf(l), Long.valueOf(((Long)this.m_ContextRequestCount.get(Long.valueOf(l))).longValue() + 1L));
          localObject2 = (List)this.m_RequestingPermissions.get(Long.valueOf(l));
          localObject3 = (List)this.m_PenddingRequestPermissions.remove(Long.valueOf(l));
          if (localObject2 == null) {
            break label330;
          }
          localArrayList = new ArrayList();
          if ((localObject3 != null) && (((List)localObject3).size() > 0)) {
            localArrayList.addAll((Collection)localObject3);
          }
          paramInt = 0;
          i = paramArrayOfString.length;
          if (paramInt >= i) {
            break label273;
          }
          localObject3 = paramArrayOfString[paramInt];
          if (localArrayList.contains(localObject3)) {
            break label483;
          }
          if (((List)localObject2).contains(localObject3)) {
            break label483;
          }
        }
        else
        {
          this.m_ContextRequestCount.put(Long.valueOf(l), Long.valueOf(1L));
        }
      }
      localArrayList.add(localObject3);
      break label483;
      label273:
      if (localArrayList.size() > 0)
      {
        Log.v(this.TAG, "requestPermissions() - Pendding permissions request");
        this.m_PenddingRequestPermissions.put(Long.valueOf(l), localArrayList);
        this.m_RequestingContexts.put(Long.valueOf(l), paramBaseActivity);
      }
      return;
      label330:
      Log.v(this.TAG, "requestPermissions() - Request permissions: ", Arrays.toString(paramArrayOfString));
      Object localObject2 = new ArrayList();
      paramInt = 0;
      int i = paramArrayOfString.length;
      while (paramInt < i)
      {
        ((List)localObject2).add(paramArrayOfString[paramInt]);
        paramInt += 1;
      }
      if (localObject3 != null)
      {
        paramArrayOfString = ((Iterable)localObject3).iterator();
        while (paramArrayOfString.hasNext())
        {
          localObject3 = (String)paramArrayOfString.next();
          if (!((List)localObject2).contains(localObject3)) {
            ((List)localObject2).add(localObject3);
          }
        }
      }
      this.m_RequestingPermissions.put(Long.valueOf(l), localObject2);
      this.m_RequestingContexts.put(Long.valueOf(l), paramBaseActivity);
      startPermissionActivity(paramBaseActivity);
      return;
      label483:
      paramInt += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/PermissionManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */