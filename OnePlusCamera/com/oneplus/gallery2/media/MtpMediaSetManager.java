package com.oneplus.gallery2.media;

import android.hardware.usb.UsbDevice;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Log;
import com.oneplus.io.UsbDeviceEventArgs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class MtpMediaSetManager
  extends MediaSourceComponent<MtpMediaSource>
{
  private final EventHandler<UsbDeviceEventArgs> m_DeviceAttachedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<UsbDeviceEventArgs> paramAnonymousEventKey, UsbDeviceEventArgs paramAnonymousUsbDeviceEventArgs)
    {
      MtpMediaSetManager.this.onDeviceAttached(paramAnonymousUsbDeviceEventArgs.getDevice());
    }
  };
  private final EventHandler<UsbDeviceEventArgs> m_DeviceDetachedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<UsbDeviceEventArgs> paramAnonymousEventKey, UsbDeviceEventArgs paramAnonymousUsbDeviceEventArgs)
    {
      MtpMediaSetManager.this.onDeviceDetached(paramAnonymousUsbDeviceEventArgs.getDevice());
    }
  };
  private final Map<Integer, DeviceInfo> m_DeviceInfoTable = new HashMap();
  private final Map<MediaType, List<MediaSetListImpl>> m_OpenedMediaSetLists = new HashMap();
  
  MtpMediaSetManager(BaseApplication paramBaseApplication)
  {
    super("MTP media set manager", paramBaseApplication, MtpMediaSource.class);
  }
  
  private void addToMediaSetList(DeviceInfo paramDeviceInfo, MediaType paramMediaType, MediaSetListImpl paramMediaSetListImpl, boolean paramBoolean)
  {
    paramDeviceInfo = (PtpCameraRollMediaSet)paramDeviceInfo.ptpCameraRollMediaSets.get(paramMediaType);
    if (paramDeviceInfo == null) {
      return;
    }
    paramMediaSetListImpl.addMediaSet(paramDeviceInfo, paramBoolean);
  }
  
  private void addToOpenedMediaSetLists(DeviceInfo paramDeviceInfo, MediaType paramMediaType)
  {
    List localList = (List)this.m_OpenedMediaSetLists.get(paramMediaType);
    if (localList == null) {}
    for (;;)
    {
      return;
      int i = localList.size() - 1;
      while (i >= 0)
      {
        addToMediaSetList(paramDeviceInfo, paramMediaType, (MediaSetListImpl)localList.get(i), true);
        i -= 1;
      }
    }
  }
  
  private void createMediaSets(DeviceInfo paramDeviceInfo, MediaType paramMediaType)
  {
    if ((PtpCameraRollMediaSet)paramDeviceInfo.ptpCameraRollMediaSets.get(paramMediaType) != null) {
      return;
    }
    PtpCameraRollMediaSet localPtpCameraRollMediaSet = new PtpCameraRollMediaSet((MtpMediaSource)getMediaSource(), paramDeviceInfo.device, paramMediaType);
    paramDeviceInfo.ptpCameraRollMediaSets.put(paramMediaType, localPtpCameraRollMediaSet);
  }
  
  private void onDeviceAttached(UsbDevice paramUsbDevice)
  {
    int i = paramUsbDevice.getDeviceId();
    if ((DeviceInfo)this.m_DeviceInfoTable.get(Integer.valueOf(i)) == null) {}
    for (;;)
    {
      Log.v(this.TAG, "onDeviceAttached() - Device ", Integer.valueOf(i));
      paramUsbDevice = new DeviceInfo(paramUsbDevice);
      this.m_DeviceInfoTable.put(Integer.valueOf(i), paramUsbDevice);
      Iterator localIterator = this.m_OpenedMediaSetLists.keySet().iterator();
      while (localIterator.hasNext())
      {
        MediaType localMediaType = (MediaType)localIterator.next();
        createMediaSets(paramUsbDevice, localMediaType);
        addToOpenedMediaSetLists(paramUsbDevice, localMediaType);
      }
      Log.w(this.TAG, "onDeviceAttached() - Duplicate device " + i);
      onDeviceDetached(paramUsbDevice);
    }
  }
  
  private void onDeviceDetached(UsbDevice paramUsbDevice)
  {
    int i = paramUsbDevice.getDeviceId();
    DeviceInfo localDeviceInfo = (DeviceInfo)this.m_DeviceInfoTable.remove(Integer.valueOf(i));
    if (localDeviceInfo != null)
    {
      if (paramUsbDevice.equals(localDeviceInfo.device))
      {
        removeFromOpenedMediaSetLists(localDeviceInfo);
        releaseMediaSets(localDeviceInfo);
      }
    }
    else {
      return;
    }
    Log.e(this.TAG, "onDeviceDetached() - Unknown device : " + paramUsbDevice);
    this.m_DeviceInfoTable.put(Integer.valueOf(i), localDeviceInfo);
  }
  
  private void onMediaSetListReleased(MediaSetListImpl paramMediaSetListImpl)
  {
    verifyAccess();
    MediaType localMediaType = paramMediaSetListImpl.targetMediaType;
    List localList = (List)this.m_OpenedMediaSetLists.get(localMediaType);
    if (localList == null) {}
    while ((!localList.remove(paramMediaSetListImpl)) || (!localList.isEmpty())) {
      return;
    }
    this.m_OpenedMediaSetLists.remove(localMediaType);
    paramMediaSetListImpl = this.m_DeviceInfoTable.values().iterator();
    while (paramMediaSetListImpl.hasNext()) {
      releaseMediaSets((DeviceInfo)paramMediaSetListImpl.next(), localMediaType);
    }
  }
  
  private void releaseMediaSets(DeviceInfo paramDeviceInfo)
  {
    MediaType[] arrayOfMediaType = MediaType.values();
    int j = arrayOfMediaType.length;
    int i = 0;
    while (i < j)
    {
      releaseMediaSets(paramDeviceInfo, arrayOfMediaType[i]);
      i += 1;
    }
  }
  
  private void releaseMediaSets(DeviceInfo paramDeviceInfo, MediaType paramMediaType)
  {
    paramDeviceInfo = (MediaSet)paramDeviceInfo.ptpCameraRollMediaSets.remove(paramMediaType);
    if (paramDeviceInfo == null) {
      return;
    }
    paramDeviceInfo.release();
  }
  
  private void removeFromOpenedMediaSetLists(DeviceInfo paramDeviceInfo)
  {
    Iterator localIterator = this.m_OpenedMediaSetLists.entrySet().iterator();
    if (localIterator.hasNext())
    {
      Object localObject2 = (Map.Entry)localIterator.next();
      Object localObject1 = (MediaType)((Map.Entry)localObject2).getKey();
      localObject1 = (PtpCameraRollMediaSet)paramDeviceInfo.ptpCameraRollMediaSets.get(localObject1);
      localObject2 = (List)((Map.Entry)localObject2).getValue();
      int i = ((List)localObject2).size() - 1;
      label85:
      MediaSetListImpl localMediaSetListImpl;
      if (i >= 0)
      {
        localMediaSetListImpl = (MediaSetListImpl)((List)localObject2).get(i);
        if (localObject1 != null) {
          break label114;
        }
      }
      for (;;)
      {
        i -= 1;
        break label85;
        break;
        label114:
        localMediaSetListImpl.removeMediaSet((MediaSet)localObject1, true);
      }
    }
  }
  
  protected void onBindToMediaSource(MtpMediaSource paramMtpMediaSource)
  {
    super.onBindToMediaSource(paramMtpMediaSource);
    paramMtpMediaSource.addHandler(MtpMediaSource.EVENT_DEVICE_ATTACHED, this.m_DeviceAttachedHandler);
    paramMtpMediaSource.addHandler(MtpMediaSource.EVENT_DEVICE_DETACHED, this.m_DeviceDetachedHandler);
    paramMtpMediaSource = ((List)paramMtpMediaSource.get(MtpMediaSource.PROP_DEVICE_LIST)).iterator();
    while (paramMtpMediaSource.hasNext()) {
      onDeviceAttached((UsbDevice)paramMtpMediaSource.next());
    }
  }
  
  protected void onUnbindFromMediaSource(MtpMediaSource paramMtpMediaSource)
  {
    paramMtpMediaSource.removeHandler(MtpMediaSource.EVENT_DEVICE_ATTACHED, this.m_DeviceAttachedHandler);
    paramMtpMediaSource.removeHandler(MtpMediaSource.EVENT_DEVICE_DETACHED, this.m_DeviceDetachedHandler);
    super.onUnbindFromMediaSource(paramMtpMediaSource);
  }
  
  public MediaSetList openMtpMediaSetList(MediaSetComparator paramMediaSetComparator, MediaType paramMediaType, int paramInt)
  {
    verifyAccess();
    MediaSetListImpl localMediaSetListImpl;
    if (isRunningOrInitializing(true))
    {
      localMediaSetListImpl = new MediaSetListImpl(paramMediaSetComparator, paramMediaType);
      paramMediaSetComparator = (List)this.m_OpenedMediaSetLists.get(paramMediaType);
      if (paramMediaSetComparator == null) {
        break label108;
      }
    }
    for (;;)
    {
      paramMediaSetComparator.add(localMediaSetListImpl);
      paramMediaSetComparator = this.m_DeviceInfoTable.values().iterator();
      while (paramMediaSetComparator.hasNext())
      {
        DeviceInfo localDeviceInfo = (DeviceInfo)paramMediaSetComparator.next();
        createMediaSets(localDeviceInfo, paramMediaType);
        addToMediaSetList(localDeviceInfo, paramMediaType, localMediaSetListImpl, false);
      }
      return null;
      label108:
      paramMediaSetComparator = new ArrayList();
      this.m_OpenedMediaSetLists.put(paramMediaType, paramMediaSetComparator);
    }
    return localMediaSetListImpl;
  }
  
  public MediaSetList openMtpMediaSetList(MediaType paramMediaType, int paramInt)
  {
    return openMtpMediaSetList(MediaSetComparator.DEFAULT, paramMediaType, paramInt);
  }
  
  private final class DeviceInfo
  {
    public final UsbDevice device;
    public final int deviceId;
    public final Map<MediaType, PtpCameraRollMediaSet> ptpCameraRollMediaSets = new HashMap();
    
    public DeviceInfo(UsbDevice paramUsbDevice)
    {
      this.device = paramUsbDevice;
      this.deviceId = paramUsbDevice.getDeviceId();
    }
  }
  
  private final class MediaSetListImpl
    extends BaseMediaSetList
  {
    public final MediaType targetMediaType;
    
    protected MediaSetListImpl(MediaSetComparator paramMediaSetComparator, MediaType paramMediaType)
    {
      super(false);
      this.targetMediaType = paramMediaType;
      setReadOnly(PROP_IS_READY, Boolean.valueOf(true));
    }
    
    public void release()
    {
      super.release();
      MtpMediaSetManager.this.onMediaSetListReleased(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MtpMediaSetManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */