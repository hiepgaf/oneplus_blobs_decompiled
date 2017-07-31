package com.oneplus.camera;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.oneplus.base.EventArgs;
import com.oneplus.base.Log;
import com.oneplus.camera.media.MediaEventArgs;
import com.oneplus.camera.watermark.Watermark;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PictureProcessServiceProxy
  extends UIComponent
  implements PictureProcessService
{
  private static final String SERVICE_CLASS_NAME = "com.oneplus.camera.OPPictureProcessService";
  private static final String SERVICE_PACKAGE = "com.oneplus.camera";
  private BroadcastReceiver m_ClearCacheReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      PictureProcessServiceProxy.-wrap0(PictureProcessServiceProxy.this, paramAnonymousIntent);
    }
  };
  private final Set<String> m_ProcessingFilePaths = new HashSet();
  private final Set<String> m_ProcessingWatermarkFilePaths = new HashSet();
  private ServiceConnection m_ServiceConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      PictureProcessServiceProxy.-wrap1(PictureProcessServiceProxy.this, paramAnonymousComponentName, paramAnonymousIBinder);
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      PictureProcessServiceProxy.-wrap2(PictureProcessServiceProxy.this, paramAnonymousComponentName);
    }
  };
  private Messenger m_ServiceMessenger;
  
  PictureProcessServiceProxy(CameraActivity paramCameraActivity)
  {
    super("Picture Process Service Proxy", paramCameraActivity, true);
    enablePropertyLogs(PROP_IS_CONNECTED, 1);
    enablePropertyLogs(PROP_IS_PROCESSING, 1);
  }
  
  private void connectToService()
  {
    try
    {
      CameraActivity localCameraActivity = getCameraActivity();
      Intent localIntent = new Intent();
      localIntent.setClassName("com.oneplus.camera", "com.oneplus.camera.OPPictureProcessService");
      localCameraActivity.startService(localIntent);
      localCameraActivity.bindService(localIntent, this.m_ServiceConnection, 1);
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "connectToService() - Error when bind service", localThrowable);
    }
  }
  
  private void onClearCacheIntentReceived(Intent paramIntent)
  {
    Uri localUri = paramIntent.getData();
    String str1 = paramIntent.getStringExtra("pictureId");
    String str2 = paramIntent.getStringExtra("filePath");
    paramIntent = paramIntent.getStringArrayListExtra("processTypes");
    if (str2 == null)
    {
      Log.w(this.TAG, "onClearCacheIntentReceived() - File path is empty");
      return;
    }
    if ((paramIntent == null) || (paramIntent.size() == 0))
    {
      Log.w(this.TAG, "onClearCacheIntentReceived() - Process types is empty");
      return;
    }
    paramIntent = paramIntent.iterator();
    while (paramIntent.hasNext())
    {
      Object localObject = (PictureProcessService.ProcessType)Enum.valueOf(PictureProcessService.ProcessType.class, (String)paramIntent.next());
      switch (-getcom-oneplus-camera-PictureProcessService$ProcessTypeSwitchesValues()[localObject.ordinal()])
      {
      default: 
        break;
      case 1: 
        Log.v(this.TAG, "onClearCacheIntentReceived() - Offline picture processed: ", str2);
        if (!this.m_ProcessingFilePaths.remove(str2))
        {
          Log.v(this.TAG, "onClearCacheIntentReceived() - Duplicated file path processed: ", str2);
        }
        else
        {
          localObject = new MediaEventArgs(null, str1, 0, str2, localUri, null);
          raise(EVENT_PICTURE_PROCESSED, (EventArgs)localObject);
          if (this.m_ProcessingFilePaths.isEmpty()) {
            setReadOnly(PROP_IS_PROCESSING, Boolean.valueOf(false));
          } else {
            Log.d(this.TAG, "onClearCacheIntentReceived() - ", Integer.valueOf(this.m_ProcessingFilePaths.size()), " picture(s) left");
          }
        }
        break;
      case 2: 
        Log.v(this.TAG, "onClearCacheIntentReceived() - Watermark picture processed: ", str2);
        if (!this.m_ProcessingWatermarkFilePaths.remove(str2))
        {
          Log.v(this.TAG, "onClearCacheIntentReceived() - Duplicated file path processed: ", str2);
        }
        else
        {
          localObject = new MediaEventArgs(null, str1, 0, str2, localUri, null);
          raise(EVENT_WATERMARK_PROCESSED, (EventArgs)localObject);
          if (!this.m_ProcessingWatermarkFilePaths.isEmpty()) {
            Log.d(this.TAG, "onClearCacheIntentReceived() - ", Integer.valueOf(this.m_ProcessingWatermarkFilePaths.size()), " picture(s) is processing watermark");
          }
        }
        break;
      }
    }
  }
  
  private void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    this.m_ServiceMessenger = new Messenger(paramIBinder);
    setReadOnly(PROP_IS_CONNECTED, Boolean.valueOf(true));
  }
  
  private void onServiceDisconnected(ComponentName paramComponentName)
  {
    this.m_ServiceMessenger = null;
    setReadOnly(PROP_IS_CONNECTED, Boolean.valueOf(false));
  }
  
  public boolean isPictureProcessing(String paramString)
  {
    if (paramString != null) {
      return this.m_ProcessingFilePaths.contains(paramString);
    }
    return false;
  }
  
  public boolean isWatermarkProcessing(String paramString)
  {
    if (paramString != null) {
      return this.m_ProcessingWatermarkFilePaths.contains(paramString);
    }
    return false;
  }
  
  protected void onDeinitialize()
  {
    getCameraActivity().unregisterReceiver(this.m_ClearCacheReceiver);
    try
    {
      getCameraActivity().unbindService(this.m_ServiceConnection);
      setReadOnly(PROP_IS_PROCESSING, Boolean.valueOf(false));
      super.onDeinitialize();
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(this.TAG, "onDeinitialize() - Error when unbind service", localThrowable);
      }
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    connectToService();
    IntentFilter localIntentFilter = new IntentFilter("com.oneplus.camera.service.CLEAR_IMAGE_CACHE");
    try
    {
      localIntentFilter.addDataType("image/*");
      getCameraActivity().registerReceiver(this.m_ClearCacheReceiver, localIntentFilter);
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(this.TAG, "onInitialize() - Error to add data type", localThrowable);
      }
    }
  }
  
  public void onUnprocessedPictureReceived(String paramString1, String paramString2)
  {
    if (!((Boolean)get(PROP_IS_CONNECTED)).booleanValue())
    {
      connectToService();
      return;
    }
    Log.v(this.TAG, "onUnprocessedPictureReceived() - Picture id: ", paramString1, ", HAL picture id: ", paramString2);
    try
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("pictureId", paramString1);
      localBundle.putString("halPictureId", paramString2);
      paramString1 = Message.obtain(null, -130001, 0, 0, localBundle);
      this.m_ServiceMessenger.send(paramString1);
      return;
    }
    catch (RemoteException paramString1)
    {
      Log.e(this.TAG, "onUnprocessedPictureReceived() - Send message failed", paramString1);
    }
  }
  
  public void onUnprocessedPictureSaved(String paramString1, String paramString2, Uri paramUri)
  {
    if (!((Boolean)get(PROP_IS_CONNECTED)).booleanValue())
    {
      connectToService();
      return;
    }
    Log.v(this.TAG, "onUnprocessedPictureSaved() - Picture id: ", paramString1, ", filePath: ", paramString2, ", contentUri: ", paramUri);
    try
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("pictureId", paramString1);
      localBundle.putString("filePath", paramString2);
      localBundle.putString("contentUri", paramUri.toString());
      paramString1 = Message.obtain(null, -130002, 0, 0, localBundle);
      this.m_ServiceMessenger.send(paramString1);
      this.m_ProcessingFilePaths.add(paramString2);
      if (!this.m_ProcessingFilePaths.isEmpty()) {
        setReadOnly(PROP_IS_PROCESSING, Boolean.valueOf(true));
      }
      return;
    }
    catch (RemoteException paramString1)
    {
      for (;;)
      {
        Log.e(this.TAG, "onUnprocessedPictureSaved() - Send message failed", paramString1);
        this.m_ProcessingFilePaths.remove(paramString2);
      }
    }
  }
  
  public void scheduleProcessWatermark(String paramString1, String paramString2, Watermark paramWatermark, Rect paramRect, String paramString3)
  {
    if (!((Boolean)get(PROP_IS_CONNECTED)).booleanValue())
    {
      connectToService();
      return;
    }
    Log.v(this.TAG, "scheduleProcessWatermark() - Picture id: ", new Object[] { paramString1, ", file: ", paramString2, ", watermark: ", paramWatermark, ", bounds: ", paramRect, ", text: ", paramString3 });
    try
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("pictureId", paramString1);
      localBundle.putString("filePath", paramString2);
      localBundle.putSerializable("watermark", paramWatermark);
      localBundle.putParcelable("watermarkBounds", paramRect);
      localBundle.putString("watermarkText", paramString3);
      paramWatermark = Message.obtain(null, -130005, 0, 0, localBundle);
      this.m_ServiceMessenger.send(paramWatermark);
      this.m_ProcessingWatermarkFilePaths.add(paramString2);
      paramString1 = new MediaEventArgs(null, paramString1, 0, paramString2, null, null);
      raise(EVENT_WATERMARK_PROCESSING, paramString1);
      return;
    }
    catch (RemoteException paramString1)
    {
      Log.e(this.TAG, "scheduleProcessWatermark() - Send message failed", paramString1);
      this.m_ProcessingWatermarkFilePaths.remove(paramString2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/PictureProcessServiceProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */