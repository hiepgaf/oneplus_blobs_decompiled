package com.oneplus.camera;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.os.SystemClock;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class CameraDeviceManagerImpl
  extends CameraThreadComponent
  implements CameraDeviceManager
{
  private static final boolean ENABLE_LEGACY_CAMERA = true;
  private CameraManager m_CameraManager;
  
  CameraDeviceManagerImpl(CameraThread paramCameraThread)
  {
    super("Camera device manager", paramCameraThread, true);
  }
  
  private void refreshCameraList()
  {
    if (this.m_CameraManager == null)
    {
      Log.e(this.TAG, "refreshCameraList() - No CameraManager");
      return;
    }
    List localList2 = (List)get(PROP_AVAILABLE_CAMERAS);
    long l1 = SystemClock.elapsedRealtime();
    if (((Boolean)getCameraThread().get(CameraThread.PROP_ALL_REQUIRED_PERMISSIONS_GRANTED)).booleanValue()) {}
    for (;;)
    {
      Context localContext;
      int i;
      int j;
      int k;
      Object localObject4;
      Object localObject2;
      Object localObject3;
      Object localObject5;
      Object localObject6;
      Object localObject7;
      List localList1;
      try
      {
        localContext = getContext();
        String[] arrayOfString = this.m_CameraManager.getCameraIdList();
        CameraInfo[] arrayOfCameraInfo = new CameraInfo[arrayOfString.length];
        i = arrayOfString.length - 1;
        if (i >= 0)
        {
          arrayOfCameraInfo[i] = new CameraInfo(localContext, this.m_CameraManager, arrayOfString[i], arrayOfString.length);
          i -= 1;
          continue;
        }
        j = 0;
        k = 0;
        i = arrayOfString.length - 1;
        if (i < 0) {
          break label646;
        }
        switch (((Integer)arrayOfCameraInfo[i].get(CameraInfo.PROP_LENS_FACING)).intValue())
        {
        default: 
          localObject4 = null;
          localObject2 = null;
          localObject3 = null;
          i = arrayOfString.length - 1;
          Object localObject1;
          if (i >= 0)
          {
            localObject1 = arrayOfCameraInfo[i];
            localObject5 = localObject4;
            localObject6 = localObject3;
            localObject7 = localObject2;
          }
          switch (((Integer)((CameraInfo)localObject1).get(CameraInfo.PROP_LENS_FACING)).intValue())
          {
          default: 
            i = arrayOfString.length - 1;
            if (i < 0) {
              continue;
            }
            arrayOfCameraInfo[i].saveCameraInfo(this.m_CameraManager, arrayOfString[i]);
            i -= 1;
            continue;
            if (localObject4 == null)
            {
              if (localObject2 != null)
              {
                Log.w(this.TAG, "refreshCameraList() - Use wide lens as back camera");
                ((CameraInfo)localObject2).set(CameraInfo.PROP_LENS_FACING, Integer.valueOf(1));
                localObject1 = new Camera[arrayOfString.length];
                i = arrayOfString.length - 1;
                if (i < 0) {
                  break label602;
                }
                localObject2 = arrayOfString[i];
                if (localList2 != null)
                {
                  j = localList2.size() - 1;
                  if (j >= 0)
                  {
                    localObject3 = (Camera)localList2.get(j);
                    if (!((String)((Camera)localObject3).get(Camera.PROP_ID)).equals(localObject2)) {
                      break label740;
                    }
                    localObject1[i] = localObject3;
                  }
                }
                if (localObject1[i] != null) {
                  break label733;
                }
                localObject3 = arrayOfCameraInfo[i];
                if (((Integer)((CameraInfo)localObject3).get(CameraInfo.PROP_HARDWARE_LEVEL)).intValue() != 2) {
                  break label578;
                }
                localObject1[i] = new LegacyCameraImpl(localContext, this.m_CameraManager, (String)localObject2, (CameraInfo)localObject3);
                break label733;
              }
              if (localObject3 != null)
              {
                Log.w(this.TAG, "refreshCameraList() - Use tele lens as back camera");
                ((CameraInfo)localObject3).set(CameraInfo.PROP_LENS_FACING, Integer.valueOf(1));
                continue;
                l2 = SystemClock.elapsedRealtime();
              }
            }
            break;
          }
          break;
        }
      }
      catch (Throwable localThrowable)
      {
        Log.e(this.TAG, "refreshCameraList() - Fail to create camera list", localThrowable);
        localList1 = Collections.EMPTY_LIST;
      }
      for (;;)
      {
        long l2;
        Log.v(this.TAG, "refreshCameraList() - Takes ", Long.valueOf(l2 - l1), "ms to refresh list, cameras : ", localList1);
        setReadOnly(PROP_AVAILABLE_CAMERAS, localList1);
        return;
        Log.w(this.TAG, "refreshCameraList() - No back camera");
        break;
        if (localObject2 != null) {
          Log.v(this.TAG, "refreshCameraList() - Wide lens (back) found");
        }
        if (localObject3 == null) {
          break;
        }
        Log.v(this.TAG, "refreshCameraList() - Tele lens (back) found");
        break;
        label578:
        localList1[i] = new CameraImpl(localContext, this.m_CameraManager, (String)localObject2, (CameraInfo)localObject3);
        break label733;
        label602:
        localList1 = Arrays.asList(localList1);
        continue;
        Log.w(this.TAG, "refreshCameraList() - Required permissions not granted");
        localList1 = Collections.EMPTY_LIST;
      }
      for (;;)
      {
        i -= 1;
        break;
        j = 1;
        continue;
        k = 1;
      }
      label646:
      if ((k != 0) && (j != 0))
      {
        continue;
        localObject7 = localObject2;
        localObject6 = localObject3;
        localObject5 = localObject4;
        for (;;)
        {
          i -= 1;
          localObject4 = localObject5;
          localObject3 = localObject6;
          localObject2 = localObject7;
          break;
          localObject5 = localList1;
          localObject6 = localObject3;
          localObject7 = localObject2;
          continue;
          localObject5 = localObject4;
          localObject6 = localObject3;
          localObject7 = localList1;
          continue;
          localObject5 = localObject4;
          localObject6 = localList1;
          localObject7 = localObject2;
        }
        label733:
        i -= 1;
        continue;
        label740:
        j -= 1;
      }
    }
  }
  
  protected void onDeinitialize()
  {
    this.m_CameraManager = null;
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_CameraManager = ((CameraManager)getContext().getSystemService("camera"));
    getCameraThread().addCallback(CameraThread.PROP_ALL_REQUIRED_PERMISSIONS_GRANTED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        CameraDeviceManagerImpl.-wrap0(CameraDeviceManagerImpl.this);
      }
    });
    if (((Boolean)getCameraThread().get(CameraThread.PROP_ALL_REQUIRED_PERMISSIONS_GRANTED)).booleanValue()) {
      refreshCameraList();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraDeviceManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */