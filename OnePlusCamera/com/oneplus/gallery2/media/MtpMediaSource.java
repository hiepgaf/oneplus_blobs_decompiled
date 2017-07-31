package com.oneplus.gallery2.media;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.mtp.MtpDevice;
import android.mtp.MtpObjectInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.Ref;
import com.oneplus.base.SimpleRef;
import com.oneplus.io.UsbDeviceEventArgs;
import com.oneplus.io.UsbManager;
import com.oneplus.io.UsbManager.OpenDeviceCallback;
import com.oneplus.io.UsbManager.PermissionCallback;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MtpMediaSource
  extends ExternalMediaSource
{
  public static final EventKey<UsbDeviceEventArgs> EVENT_DEVICE_ATTACHED = new EventKey("DeviceAttached", UsbDeviceEventArgs.class, MtpMediaSource.class);
  public static final EventKey<UsbDeviceEventArgs> EVENT_DEVICE_DETACHED = new EventKey("DeviceDetached", UsbDeviceEventArgs.class, MtpMediaSource.class);
  private static final int MSG_CLOSE_MTP_DEVICE = 10002;
  private static final int MSG_MTP_DEVICE_OPENED = 10010;
  private static final int MSG_OPEN_MTP_DEVICE = 10001;
  private static final int MSG_QUERY_ALL_MTP_OBJECTS = 10020;
  private static final int MSG_QUERY_SINGLE_MTP_OBJECT = 10021;
  private static final int MSG_SYNC_MEDIA_WITH_MTP_DEVICE = 10030;
  private static final int MSG_SYNC_SINGLE_MEDIA_WITH_MTP_DEVICE = 10031;
  private static final int MTP_OBJECTS_QUERY_CHUNK_SIZE = 64;
  public static final PropertyKey<List<UsbDevice>> PROP_DEVICE_LIST = new PropertyKey("DeviceList", List.class, MtpMediaSource.class, Collections.EMPTY_LIST);
  private List<Integer> m_AllObjectIdNeedToBeQueried;
  private final Map<Integer, Set<Media>> m_CandidateMediaToRemove = new HashMap();
  private final Set<Integer> m_DeviceIdNeedToBeSync = new HashSet();
  private final Map<Integer, DeviceInfo> m_Devices = new ConcurrentHashMap();
  private Object m_MediaSyncToken;
  private final UsbManager.OpenDeviceCallback m_UsbDeviceOpenCallback = new UsbManager.OpenDeviceCallback()
  {
    public void onFailed(UsbDevice paramAnonymousUsbDevice)
    {
      Log.e(MtpMediaSource.this.TAG, "Fail to open device " + paramAnonymousUsbDevice.getDeviceId());
    }
    
    public void onOpened(UsbDevice paramAnonymousUsbDevice, UsbDeviceConnection paramAnonymousUsbDeviceConnection)
    {
      MtpMediaSource.this.onUsbDeviceOpened(paramAnonymousUsbDevice, paramAnonymousUsbDeviceConnection);
    }
  };
  private UsbManager m_UsbManager;
  private final UsbManager.PermissionCallback m_UsbPermissionCallback = new UsbManager.PermissionCallback()
  {
    public void onPermissionRejected(UsbDevice paramAnonymousUsbDevice)
    {
      Log.w(MtpMediaSource.this.TAG, "User rejected for accessing device " + paramAnonymousUsbDevice.getDeviceId());
    }
    
    public void onPermissionRequested(UsbDevice paramAnonymousUsbDevice)
    {
      MtpMediaSource.this.onUsbDevicePermissionsReady(paramAnonymousUsbDevice);
    }
  };
  
  MtpMediaSource(BaseApplication paramBaseApplication)
  {
    super("MTP media source", paramBaseApplication);
  }
  
  private void callOnMediaObtained(DeviceInfo paramDeviceInfo, int paramInt, String paramString, MtpMedia paramMtpMedia)
  {
    MediaSource.MediaObtainCallback localMediaObtainCallback = null;
    List localList = (List)paramDeviceInfo.mediaObtainHandles.remove(Integer.valueOf(paramInt));
    if (localList == null) {
      return;
    }
    if (paramMtpMedia == null)
    {
      paramDeviceInfo = localMediaObtainCallback;
      label35:
      paramInt = localList.size() - 1;
      label45:
      if (paramInt >= 0)
      {
        localMediaObtainCallback = (MediaSource.MediaObtainCallback)((CallbackHandle)localList.get(paramInt)).getCallback();
        if (localMediaObtainCallback != null) {
          break label89;
        }
      }
    }
    for (;;)
    {
      paramInt -= 1;
      break label45;
      break;
      paramDeviceInfo = paramMtpMedia.getContentUri();
      break label35;
      label89:
      localMediaObtainCallback.onMediaObtained(this, paramDeviceInfo, paramString, paramMtpMedia, 0);
    }
  }
  
  private void closeMtpDevice(MtpDevice paramMtpDevice)
  {
    if (paramMtpDevice != null)
    {
      if (!isWorkerThread())
      {
        Log.d(this.TAG, "closeMtpDevice() - Start closing MTP device ", Integer.valueOf(paramMtpDevice.getDeviceId()));
        HandlerUtils.sendMessage(getWorkerThread(), 10002, paramMtpDevice);
      }
    }
    else {
      return;
    }
    try
    {
      Log.d(this.TAG, "closeMtpDevice() - Closing device " + paramMtpDevice.getDeviceId());
      paramMtpDevice.close();
      Log.d(this.TAG, "closeMtpDevice() - Device closed");
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "closeMtpDevice() - Fail to close device " + paramMtpDevice.getDeviceId(), localThrowable);
    }
  }
  
  private void closeUsbDevice(DeviceInfo paramDeviceInfo, boolean paramBoolean)
  {
    int i;
    if (paramDeviceInfo.mtpDevice == null)
    {
      i = paramDeviceInfo.deviceId;
      Log.d(this.TAG, "closeUsbDevice() - Close device ", Integer.valueOf(i));
      paramDeviceInfo.deviceOpenHandle = Handle.close(paramDeviceInfo.deviceOpenHandle);
      paramDeviceInfo.connection = null;
      if (paramBoolean) {
        break label63;
      }
    }
    for (;;)
    {
      return;
      closeMtpDevice(paramDeviceInfo.mtpDevice);
      paramDeviceInfo.mtpDevice = null;
      break;
      label63:
      paramDeviceInfo = new ArrayList();
      Object localObject;
      if (getOpenedDeviceCount() > 0)
      {
        localObject = getMedia().iterator();
        while (((Iterator)localObject).hasNext())
        {
          Media localMedia = (Media)((Iterator)localObject).next();
          if (((localMedia instanceof MtpMedia)) && (((MtpMedia)localMedia).getDeviceId() == i)) {
            paramDeviceInfo.add(localMedia);
          }
        }
      }
      paramDeviceInfo.addAll(getMedia());
      Log.v(this.TAG, "closeUsbDevice() - Remove ", Integer.valueOf(paramDeviceInfo.size()), " media");
      i = paramDeviceInfo.size() - 1;
      while (i >= 0)
      {
        removeMedia((Media)paramDeviceInfo.get(i), false, 0);
        i -= 1;
      }
      i = paramDeviceInfo.size() - 1;
      while (i >= 0)
      {
        localObject = (MtpMedia)paramDeviceInfo.get(i);
        notifyMediaDeleted((Media)localObject, 0);
        ((MtpMedia)localObject).release();
        i -= 1;
      }
    }
  }
  
  private int getOpenedDeviceCount()
  {
    Iterator localIterator = this.m_Devices.values().iterator();
    int i = 0;
    while (localIterator.hasNext()) {
      if (Handle.isValid(((DeviceInfo)localIterator.next()).deviceOpenHandle)) {
        i += 1;
      }
    }
    return i;
  }
  
  private static boolean isCameraDevice(UsbDevice paramUsbDevice)
  {
    if (paramUsbDevice != null)
    {
      int i = paramUsbDevice.getInterfaceCount();
      int j;
      do
      {
        j = i - 1;
        if (j < 0) {
          break;
        }
        i = j;
      } while (paramUsbDevice.getInterface(j).getInterfaceClass() != 6);
      return true;
    }
    return false;
    return false;
  }
  
  private boolean isValidCacheKey(final MediaCacheKeyImpl paramMediaCacheKeyImpl)
  {
    final SimpleRef localSimpleRef;
    if (paramMediaCacheKeyImpl != null) {
      localSimpleRef = new SimpleRef(Boolean.valueOf(false));
    }
    try
    {
      runInWorkerThreadAndWait(new Runnable()
      {
        public void run()
        {
          Iterator localIterator = MtpMediaSource.this.m_Devices.values().iterator();
          Object localObject;
          for (;;)
          {
            if (localIterator.hasNext())
            {
              localObject = (MtpMediaSource.DeviceInfo)localIterator.next();
              if (paramMediaCacheKeyImpl.deviceSerialNumber.equals(((MtpMediaSource.DeviceInfo)localObject).deviceSerialNumber))
              {
                localObject = ((MtpMediaSource.DeviceInfo)localObject).mtpDevice;
                if (localObject == null) {
                  break;
                }
              }
            }
          }
          for (;;)
          {
            boolean bool;
            try
            {
              localObject = ((MtpDevice)localObject).getObjectInfo(paramMediaCacheKeyImpl.objectId);
              localRef = localSimpleRef;
              if (localObject != null) {
                break label145;
              }
            }
            catch (Throwable localThrowable)
            {
              Ref localRef;
              localSimpleRef.set(Boolean.valueOf(false));
            }
            localRef.set(Boolean.valueOf(bool));
            break;
            break;
            localSimpleRef.set(Boolean.valueOf(false));
            return;
            label145:
            long l1 = localThrowable.getDateModified();
            long l2 = paramMediaCacheKeyImpl.lastModifiedTime;
            if (l1 == l2) {
              bool = true;
            } else {
              bool = false;
            }
          }
        }
      });
      return ((Boolean)localSimpleRef.get()).booleanValue();
    }
    catch (Throwable paramMediaCacheKeyImpl) {}
    return false;
    return false;
  }
  
  private void onMtpDeviceOpened(DeviceInfo paramDeviceInfo, MtpDevice paramMtpDevice)
  {
    Integer localInteger = Integer.valueOf(paramDeviceInfo.deviceId);
    if (!isRunningOrInitializing()) {}
    while ((this.m_Devices.get(localInteger) != paramDeviceInfo) || (!Handle.isValid(paramDeviceInfo.deviceOpenHandle)))
    {
      Log.w(this.TAG, "onMtpDeviceOpened() - No need to open MTP device " + localInteger);
      closeMtpDevice(paramMtpDevice);
      return;
    }
    Log.v(this.TAG, "onMtpDeviceOpened() - MTP device ", localInteger, " opened");
    paramDeviceInfo.mtpDevice = paramMtpDevice;
    onMtpDeviceReady(paramDeviceInfo);
  }
  
  private void onMtpDeviceReady(DeviceInfo paramDeviceInfo)
  {
    if (!this.m_DeviceIdNeedToBeSync.remove(Integer.valueOf(paramDeviceInfo.deviceId))) {
      return;
    }
    syncMediaWithMtpDevice(paramDeviceInfo.mtpDevice);
  }
  
  private void onUsbDeviceAttached(UsbDevice paramUsbDevice)
  {
    int i = paramUsbDevice.getDeviceId();
    DeviceInfo localDeviceInfo = (DeviceInfo)this.m_Devices.get(Integer.valueOf(i));
    if (localDeviceInfo == null)
    {
      Log.d(this.TAG, "onUsbDeviceAttached() : ", paramUsbDevice.getDeviceName(), " (", Integer.valueOf(i), ")");
      if (!isCameraDevice(paramUsbDevice)) {
        break label138;
      }
      if (!((Boolean)get(PROP_IS_ACTIVE)).booleanValue()) {
        break label156;
      }
      if (localDeviceInfo == null) {
        break label167;
      }
    }
    for (;;)
    {
      if (localDeviceInfo.isPermissionReady) {
        break label194;
      }
      if (!localDeviceInfo.isRequestingPermission) {
        break label200;
      }
      return;
      if (paramUsbDevice.equals(localDeviceInfo.device)) {
        break;
      }
      Log.w(this.TAG, "onUsbDeviceAttached() - Duplicate device " + i);
      onUsbDeviceDetached(localDeviceInfo.device);
      break;
      label138:
      Log.d(this.TAG, "onUsbDeviceAttached() - Device ", Integer.valueOf(i), " is not a Camera device");
      return;
      label156:
      Log.v(this.TAG, "onUsbDeviceAttached() - Access device when activated");
      return;
      label167:
      localDeviceInfo = new DeviceInfo(paramUsbDevice);
      this.m_Devices.put(Integer.valueOf(i), localDeviceInfo);
    }
    label194:
    onUsbDevicePermissionsReady(paramUsbDevice);
    return;
    label200:
    this.m_DeviceIdNeedToBeSync.add(Integer.valueOf(i));
    this.m_UsbManager.requestPermission(paramUsbDevice, this.m_UsbPermissionCallback, getHandler(), 0);
    localDeviceInfo.isRequestingPermission = true;
  }
  
  private void onUsbDeviceDetached(UsbDevice paramUsbDevice)
  {
    int i = paramUsbDevice.getDeviceId();
    this.m_DeviceIdNeedToBeSync.remove(Integer.valueOf(i));
    DeviceInfo localDeviceInfo = (DeviceInfo)this.m_Devices.remove(Integer.valueOf(i));
    if (localDeviceInfo != null)
    {
      if (paramUsbDevice.equals(localDeviceInfo.device))
      {
        updateDeviceListProperty();
        raise(EVENT_DEVICE_DETACHED, new UsbDeviceEventArgs(paramUsbDevice));
        closeUsbDevice(localDeviceInfo, true);
      }
    }
    else {
      return;
    }
    Log.w(this.TAG, "onUsbDeviceDetached() - Unknown device : " + paramUsbDevice);
    this.m_Devices.put(Integer.valueOf(i), localDeviceInfo);
  }
  
  private void onUsbDeviceOpened(UsbDevice paramUsbDevice, UsbDeviceConnection paramUsbDeviceConnection)
  {
    Integer localInteger = Integer.valueOf(paramUsbDevice.getDeviceId());
    DeviceInfo localDeviceInfo;
    if (isRunningOrInitializing())
    {
      localDeviceInfo = (DeviceInfo)this.m_Devices.get(localInteger);
      if (localDeviceInfo != null)
      {
        if (!paramUsbDevice.equals(localDeviceInfo.device)) {
          break label86;
        }
        Log.d(this.TAG, "onUsbDeviceOpened() - Device : ", localInteger);
        localDeviceInfo.connection = paramUsbDeviceConnection;
        if (localDeviceInfo.mtpDevice == null) {
          break label111;
        }
        onMtpDeviceOpened(localDeviceInfo, localDeviceInfo.mtpDevice);
      }
    }
    else
    {
      return;
    }
    return;
    label86:
    Log.w(this.TAG, "onUsbDeviceOpened() - Unknown device : " + paramUsbDevice);
    return;
    label111:
    openMtpDevice(localDeviceInfo, paramUsbDeviceConnection);
  }
  
  private void onUsbDevicePermissionsReady(UsbDevice paramUsbDevice)
  {
    DeviceInfo localDeviceInfo;
    if (isRunningOrInitializing())
    {
      int i = paramUsbDevice.getDeviceId();
      localDeviceInfo = (DeviceInfo)this.m_Devices.get(Integer.valueOf(i));
      if (localDeviceInfo == null) {
        break label132;
      }
      if (!paramUsbDevice.equals(localDeviceInfo.device)) {
        break label133;
      }
      Log.d(this.TAG, "onUsbDevicePermissionsReady() - Device : ", Integer.valueOf(i));
      localDeviceInfo.isRequestingPermission = false;
      localDeviceInfo.isPermissionReady = true;
      if (Handle.isValid(localDeviceInfo.deviceOpenHandle)) {
        break label158;
      }
      localDeviceInfo.deviceOpenHandle = this.m_UsbManager.openDevice(paramUsbDevice, this.m_UsbDeviceOpenCallback, getHandler(), 0);
      if (!Handle.isValid(localDeviceInfo.deviceOpenHandle)) {
        break label175;
      }
    }
    for (;;)
    {
      updateDeviceListProperty();
      raise(EVENT_DEVICE_ATTACHED, new UsbDeviceEventArgs(paramUsbDevice));
      return;
      return;
      label132:
      return;
      label133:
      Log.w(this.TAG, "onUsbDevicePermissionsReady() - Unknown device : " + paramUsbDevice);
      return;
      label158:
      if (localDeviceInfo.connection == null) {
        break;
      }
      onUsbDeviceOpened(paramUsbDevice, localDeviceInfo.connection);
      return;
      label175:
      Log.e(this.TAG, "onUsbDevicePermissionsReady() - Fail to start opening device " + paramUsbDevice.getDeviceId());
    }
  }
  
  private void openMtpDevice(DeviceInfo paramDeviceInfo, UsbDeviceConnection paramUsbDeviceConnection)
  {
    if (!isWorkerThread())
    {
      if (Handle.isValid(paramDeviceInfo.deviceOpenHandle))
      {
        Log.d(this.TAG, "openMtpDevice() - Start opening MTP device ", Integer.valueOf(paramDeviceInfo.deviceId));
        HandlerUtils.sendMessage(getWorkerThread(), 10001, new Object[] { paramDeviceInfo, paramUsbDeviceConnection });
      }
    }
    else {
      try
      {
        Log.d(this.TAG, "openMtpDevice() - Start opening MTP device ", Integer.valueOf(paramDeviceInfo.deviceId));
        MtpDevice localMtpDevice = new MtpDevice(paramDeviceInfo.device);
        localMtpDevice.open(paramUsbDeviceConnection);
        Log.d(this.TAG, "openMtpDevice() - MTP device opened");
        HandlerUtils.sendMessage(this, 10010, new Object[] { paramDeviceInfo, localMtpDevice });
        return;
      }
      catch (Throwable paramDeviceInfo)
      {
        Log.e(this.TAG, "openMtpDevice() - Fail to create MTP device", paramDeviceInfo);
        return;
      }
    }
    Log.w(this.TAG, "openMtpDevice() - MTP device " + paramDeviceInfo.deviceId + " is already opened");
  }
  
  private void queryAllMtpObjects(Object paramObject, MtpDevice paramMtpDevice, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      i = paramMtpDevice.getDeviceId();
      l = SystemClock.elapsedRealtime();
      Log.v(this.TAG, "queryAllMtpObjects() - Start querying objects in device ", Integer.valueOf(i), ", offset : ", Integer.valueOf(paramInt));
      if (this.m_AllObjectIdNeedToBeQueried == null)
      {
        arrayOfInt1 = paramMtpDevice.getStorageIds();
        this.m_AllObjectIdNeedToBeQueried = new ArrayList();
        if (arrayOfInt1 != null) {
          break label197;
        }
        this.m_AllObjectIdNeedToBeQueried = Collections.EMPTY_LIST;
        Log.v(this.TAG, "queryAllMtpObjects() - Total object count : ", Integer.valueOf(this.m_AllObjectIdNeedToBeQueried.size()));
        n = this.m_AllObjectIdNeedToBeQueried.size();
        k = 64;
        i = 0;
        j = paramInt;
        if (k > 0) {
          break label330;
        }
      }
    }
    catch (Throwable localThrowable1)
    {
      for (;;)
      {
        try
        {
          long l;
          int[] arrayOfInt1;
          int n;
          Log.d(this.TAG, "queryAllMtpObjects() - Query ", Integer.valueOf(localArrayList.size()), " objects in ", Long.valueOf(SystemClock.elapsedRealtime() - l), " ms");
          HandlerUtils.sendMessage(this, 10030, paramInt, i, new Object[] { paramObject, paramMtpDevice, localArrayList });
          return;
          if (paramInt != 0)
          {
            continue;
            label197:
            i = arrayOfInt1.length;
            j = i - 1;
            if (j >= 0)
            {
              n = arrayOfInt1[j];
              i = MtpMedia.OBJECT_FORMATS.length;
              k = i - 1;
              i = j;
              if (k >= 0)
              {
                int[] arrayOfInt2 = paramMtpDevice.getObjectHandles(n, MtpMedia.OBJECT_FORMATS[k], 0);
                i = k;
                if (arrayOfInt2 != null)
                {
                  i = arrayOfInt2.length;
                  m = i - 1;
                  i = k;
                  if (m >= 0)
                  {
                    this.m_AllObjectIdNeedToBeQueried.add(Integer.valueOf(arrayOfInt2[m]));
                    i = m;
                    continue;
                    localThrowable1 = localThrowable1;
                    i = 0;
                    Log.e(this.TAG, "queryAllMtpObjects() - Fail to query objects", localThrowable1);
                    continue;
                    label330:
                    if (j >= n) {}
                  }
                }
              }
            }
          }
        }
        catch (Throwable localThrowable2)
        {
          try
          {
            int i;
            int k;
            Object localObject = this.m_AllObjectIdNeedToBeQueried;
            int m = j + 1;
            localObject = paramMtpDevice.getObjectInfo(((Integer)((List)localObject).get(j)).intValue());
            k -= 1;
            if (localObject == null)
            {
              i += 1;
              j = m;
              continue;
            }
            localArrayList.add(localObject);
            int j = m;
          }
          catch (Throwable localThrowable3) {}
          localThrowable2 = localThrowable2;
        }
      }
    }
  }
  
  private void queryMtpObject(MtpDevice paramMtpDevice, int paramInt)
  {
    Object localObject = null;
    try
    {
      MtpObjectInfo localMtpObjectInfo = paramMtpDevice.getObjectInfo(paramInt);
      localObject = localMtpObjectInfo;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(this.TAG, "queryMtpObject() - Fail to get info for object " + paramInt, localThrowable);
      }
    }
    HandlerUtils.sendMessage(this, 10031, paramInt, 0, new Object[] { paramMtpDevice, localObject });
  }
  
  private void syncMediaWithMtpDevice(MtpDevice paramMtpDevice)
  {
    this.m_MediaSyncToken = new Object();
    int i = paramMtpDevice.getDeviceId();
    HashSet localHashSet = new HashSet();
    Iterator localIterator = getMedia().iterator();
    while (localIterator.hasNext())
    {
      Media localMedia = (Media)localIterator.next();
      if (((MtpMedia)localMedia).getDeviceId() == i) {
        localHashSet.add(localMedia);
      }
    }
    this.m_CandidateMediaToRemove.put(Integer.valueOf(i), localHashSet);
    HandlerUtils.sendMessage(getWorkerThread(), 10020, new Object[] { this.m_MediaSyncToken, paramMtpDevice });
  }
  
  private void syncMediaWithMtpDevice(MtpDevice paramMtpDevice, int paramInt)
  {
    HandlerUtils.sendMessage(getWorkerThread(), 10021, paramInt, 0, paramMtpDevice);
  }
  
  private void syncMediaWithMtpDevice(MtpDevice paramMtpDevice, int paramInt, MtpObjectInfo paramMtpObjectInfo)
  {
    int i = paramMtpDevice.getDeviceId();
    DeviceInfo localDeviceInfo = (DeviceInfo)this.m_Devices.get(Integer.valueOf(i));
    if (localDeviceInfo == null) {}
    while (localDeviceInfo.mtpDevice != paramMtpDevice)
    {
      Log.w(this.TAG, "syncMediaWithMtpDevice() - Invalid MTP device " + i + ", ignore");
      return;
    }
    String str = MtpMedia.getId(i, paramInt);
    MtpMedia localMtpMedia = (MtpMedia)getMedia(str, 0);
    if (paramMtpObjectInfo == null)
    {
      if (localMtpMedia != null) {
        break label176;
      }
      paramMtpDevice = localMtpMedia;
    }
    for (;;)
    {
      callOnMediaObtained(localDeviceInfo, paramInt, str, paramMtpDevice);
      return;
      if (localMtpMedia != null)
      {
        i = localMtpMedia.update(paramMtpDevice, paramMtpObjectInfo);
        paramMtpDevice = localMtpMedia;
        if (i != 0)
        {
          notifyMediaUpdated(localMtpMedia, i);
          paramMtpDevice = localMtpMedia;
        }
      }
      else
      {
        paramMtpObjectInfo = MtpMedia.create(this, paramMtpDevice, paramMtpObjectInfo);
        paramMtpDevice = paramMtpObjectInfo;
        if (paramMtpObjectInfo != null)
        {
          addMedia(paramMtpObjectInfo, true, 0);
          paramMtpDevice = paramMtpObjectInfo;
          continue;
          label176:
          removeMedia(localMtpMedia, true, 0);
          paramMtpDevice = null;
        }
      }
    }
  }
  
  private void syncMediaWithMtpDevice(Object paramObject, MtpDevice paramMtpDevice, List<MtpObjectInfo> paramList, int paramInt1, int paramInt2)
  {
    Object localObject1;
    DeviceInfo localDeviceInfo;
    if (this.m_MediaSyncToken == paramObject)
    {
      localObject1 = Integer.valueOf(paramMtpDevice.getDeviceId());
      localDeviceInfo = (DeviceInfo)this.m_Devices.get(localObject1);
      if (localDeviceInfo != null) {
        break label81;
      }
    }
    label81:
    while (localDeviceInfo.mtpDevice != paramMtpDevice)
    {
      Log.w(this.TAG, "syncMediaWithMtpDevice() - Invalid MTP device " + localObject1 + ", ignore");
      return;
      Log.w(this.TAG, "syncMediaWithMtpDevice() - Different token, ignore");
      return;
    }
    Log.v(this.TAG, "syncMediaWithMtpDevice() - Device : ", localObject1, ", object count : ", Integer.valueOf(paramList.size()), ", offset : ", Integer.valueOf(paramInt1));
    Set localSet = (Set)this.m_CandidateMediaToRemove.get(localObject1);
    if (!paramList.isEmpty()) {}
    int i;
    Object localObject2;
    Object localObject3;
    MtpMedia localMtpMedia;
    while (paramInt2 > 0)
    {
      localObject1 = null;
      i = paramList.size() - 1;
      for (;;)
      {
        if (i < 0) {
          break label406;
        }
        localObject2 = (MtpObjectInfo)paramList.get(i);
        localObject3 = MtpMedia.getId(paramMtpDevice, (MtpObjectInfo)localObject2);
        localMtpMedia = (MtpMedia)getMedia((String)localObject3, 0);
        if (localMtpMedia != null) {
          break;
        }
        localObject3 = MtpMedia.create(this, paramMtpDevice, (MtpObjectInfo)localObject2);
        localObject2 = localObject1;
        if (localObject3 != null)
        {
          localObject2 = localObject1;
          if (addMedia((Media)localObject3, false, 0))
          {
            if (localObject1 == null) {
              break label394;
            }
            ((List)localObject1).add(localObject3);
            localObject2 = localObject1;
          }
        }
        i -= 1;
        localObject1 = localObject2;
      }
    }
    Log.v(this.TAG, "syncMediaWithMtpDevice() - Last chunk");
    this.m_CandidateMediaToRemove.remove(localObject1);
    if (localSet == null) {}
    for (;;)
    {
      setReadOnly(PROP_IS_MEDIA_TABLE_READY, Boolean.valueOf(true));
      return;
      int j = localMtpMedia.update(paramMtpDevice, (MtpObjectInfo)localObject2);
      if (j == 0) {
        label328:
        if (localSet != null) {
          break label381;
        }
      }
      for (;;)
      {
        callOnMediaObtained(localDeviceInfo, ((MtpObjectInfo)localObject2).getObjectHandle(), (String)localObject3, localMtpMedia);
        localObject2 = localObject1;
        break;
        Log.d(this.TAG, "syncMediaWithMtpDevice() - Media ", localMtpMedia, " updated");
        notifyMediaUpdated(localMtpMedia, j);
        break label328;
        label381:
        localSet.remove(localMtpMedia);
      }
      label394:
      localObject1 = new ArrayList();
      break;
      label406:
      if (localObject1 == null) {}
      for (;;)
      {
        HandlerUtils.sendMessage(getWorkerThread(), 10020, paramList.size() + paramInt1 + paramInt2, 0, new Object[] { paramObject, paramMtpDevice });
        return;
        Log.d(this.TAG, "syncMediaWithMtpDevice() - Add ", Integer.valueOf(((List)localObject1).size()), " media");
        i = ((List)localObject1).size() - 1;
        while (i >= 0)
        {
          localObject2 = (MtpMedia)((List)localObject1).get(i);
          notifyMediaCreated((Media)localObject2, 0);
          callOnMediaObtained(localDeviceInfo, ((MtpMedia)localObject2).getObjectId(), ((MtpMedia)localObject2).getId(), (MtpMedia)localObject2);
          i -= 1;
        }
      }
      if (!localSet.isEmpty())
      {
        Log.v(this.TAG, "syncMediaWithMtpDevice() - Remove ", Integer.valueOf(localSet.size()), " media");
        paramObject = localSet.iterator();
        while (((Iterator)paramObject).hasNext()) {
          removeMedia((Media)((Iterator)paramObject).next(), false, 0);
        }
        paramObject = localSet.iterator();
        while (((Iterator)paramObject).hasNext())
        {
          paramMtpDevice = (Media)((Iterator)paramObject).next();
          notifyMediaDeleted(paramMtpDevice, 0);
          ((MtpMedia)paramMtpDevice).release();
        }
      }
    }
  }
  
  private void updateDeviceListProperty()
  {
    ArrayList localArrayList = null;
    Iterator localIterator = this.m_Devices.values().iterator();
    if (localIterator.hasNext())
    {
      DeviceInfo localDeviceInfo = (DeviceInfo)localIterator.next();
      if (localArrayList != null) {}
      for (;;)
      {
        localArrayList.add(localDeviceInfo.device);
        break;
        localArrayList = new ArrayList();
      }
    }
    if (localArrayList == null)
    {
      setReadOnly(PROP_DEVICE_LIST, Collections.EMPTY_LIST);
      return;
    }
    setReadOnly(PROP_DEVICE_LIST, Collections.unmodifiableList(localArrayList));
  }
  
  final MediaCacheKey createMediaCacheKey(int paramInt1, int paramInt2, long paramLong)
  {
    DeviceInfo localDeviceInfo = (DeviceInfo)this.m_Devices.get(Integer.valueOf(paramInt1));
    if (localDeviceInfo == null) {
      return null;
    }
    return new MediaCacheKeyImpl(this, localDeviceInfo, paramInt2, paramLong);
  }
  
  final Handle deleteMedia(MtpMedia paramMtpMedia, Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    return null;
  }
  
  public UsbDevice findDeviceBySerialNumber(String paramString)
  {
    if (paramString != null)
    {
      Iterator localIterator = this.m_Devices.values().iterator();
      DeviceInfo localDeviceInfo;
      do
      {
        if (!localIterator.hasNext()) {
          break;
        }
        localDeviceInfo = (DeviceInfo)localIterator.next();
      } while (!paramString.equals(localDeviceInfo.deviceSerialNumber));
      return localDeviceInfo.device;
    }
    return null;
    return null;
  }
  
  public GroupedMedia[] getGroupedMedia(Media paramMedia, int paramInt)
  {
    return null;
  }
  
  public Handle getMedia(String paramString, MediaSource.MediaObtainCallback paramMediaObtainCallback, final int paramInt)
  {
    SimpleRef localSimpleRef1;
    final Object localObject;
    if (paramString != null)
    {
      localSimpleRef1 = new SimpleRef();
      SimpleRef localSimpleRef2 = new SimpleRef();
      if (!MtpMedia.parseId(paramString, localSimpleRef1, localSimpleRef2)) {
        break label152;
      }
      verifyAccess();
      if (!isRunningOrInitializing(true)) {
        break label154;
      }
      if ((FLAG_ALWAYS_REFRESH & paramInt) == 0) {
        break label156;
      }
      localObject = (DeviceInfo)this.m_Devices.get(localSimpleRef1.get());
      if (localObject == null) {
        break label205;
      }
      paramInt = ((Integer)localSimpleRef2.get()).intValue();
      paramMediaObtainCallback = new CallbackHandle("GetMtpMedia", paramMediaObtainCallback, null)
      {
        protected void onClose(int paramAnonymousInt)
        {
          List localList = (List)localObject.mediaObtainHandles.get(Integer.valueOf(paramInt));
          if (localList == null) {
            return;
          }
          localList.remove(this);
        }
      };
      paramString = (List)((DeviceInfo)localObject).mediaObtainHandles.get(Integer.valueOf(paramInt));
      if (paramString == null) {
        break label243;
      }
    }
    for (;;)
    {
      paramString.add(paramMediaObtainCallback);
      if (((DeviceInfo)localObject).mtpDevice != null) {
        break label270;
      }
      return paramMediaObtainCallback;
      return null;
      label152:
      return null;
      label154:
      return null;
      label156:
      localObject = getMedia(paramString, 0);
      if (localObject == null) {
        break;
      }
      if (paramMediaObtainCallback == null) {}
      for (;;)
      {
        return new EmptyHandle("GetMtpMedia");
        paramMediaObtainCallback.onMediaObtained(this, ((Media)localObject).getContentUri(), paramString, (Media)localObject, 0);
      }
      label205:
      Log.e(this.TAG, "getMedia() - Device " + localSimpleRef1.get() + " does not exist");
      return null;
      label243:
      paramString = new ArrayList();
      ((DeviceInfo)localObject).mediaObtainHandles.put(Integer.valueOf(paramInt), paramString);
    }
    label270:
    syncMediaWithMtpDevice(((DeviceInfo)localObject).mtpDevice, paramInt);
    return paramMediaObtainCallback;
  }
  
  public String getMediaId(Uri paramUri, String paramString)
  {
    return null;
  }
  
  protected Iterable<Media> getRecycledMedia(MediaType paramMediaType, int paramInt)
  {
    return null;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 10010: 
      paramMessage = (Object[])paramMessage.obj;
      onMtpDeviceOpened((DeviceInfo)paramMessage[0], (MtpDevice)paramMessage[1]);
      return;
    case 10030: 
      arrayOfObject = (Object[])paramMessage.obj;
      syncMediaWithMtpDevice(arrayOfObject[0], (MtpDevice)arrayOfObject[1], (List)arrayOfObject[2], paramMessage.arg1, paramMessage.arg2);
      return;
    }
    Object[] arrayOfObject = (Object[])paramMessage.obj;
    syncMediaWithMtpDevice((MtpDevice)arrayOfObject[0], paramMessage.arg1, (MtpObjectInfo)arrayOfObject[1]);
  }
  
  protected boolean handleWorkerThreadMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return super.handleWorkerThreadMessage(paramMessage);
    case 10002: 
      closeMtpDevice((MtpDevice)paramMessage.obj);
      return true;
    case 10001: 
      paramMessage = (Object[])paramMessage.obj;
      openMtpDevice((DeviceInfo)paramMessage[0], (UsbDeviceConnection)paramMessage[1]);
      return true;
    case 10020: 
      Object[] arrayOfObject = (Object[])paramMessage.obj;
      queryAllMtpObjects(arrayOfObject[0], (MtpDevice)arrayOfObject[1], paramMessage.arg1);
      return true;
    }
    queryMtpObject((MtpDevice)paramMessage.obj, paramMessage.arg1);
    return true;
  }
  
  public boolean isMediaIdSupported(String paramString)
  {
    return MtpMedia.parseId(paramString, null, null);
  }
  
  public boolean isRecycledMedia(Media paramMedia)
  {
    return false;
  }
  
  public boolean isSubMedia(Media paramMedia)
  {
    return false;
  }
  
  protected void onActivated()
  {
    super.onActivated();
    List localList = (List)this.m_UsbManager.get(UsbManager.PROP_DEVICE_LIST);
    int i = localList.size() - 1;
    while (i >= 0)
    {
      onUsbDeviceAttached((UsbDevice)localList.get(i));
      i -= 1;
    }
    if (!this.m_Devices.isEmpty()) {
      return;
    }
    setReadOnly(PROP_IS_MEDIA_TABLE_READY, Boolean.valueOf(true));
  }
  
  protected void onDeinitialize()
  {
    List localList = (List)this.m_UsbManager.get(UsbManager.PROP_DEVICE_LIST);
    int i = localList.size() - 1;
    while (i >= 0)
    {
      onUsbDeviceDetached((UsbDevice)localList.get(i));
      i -= 1;
    }
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_UsbManager = ((UsbManager)BaseApplication.current().findComponent(UsbManager.class));
    if (this.m_UsbManager != null)
    {
      this.m_UsbManager.addHandler(UsbManager.EVENT_DEVICE_ATTACHED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<UsbDeviceEventArgs> paramAnonymousEventKey, UsbDeviceEventArgs paramAnonymousUsbDeviceEventArgs)
        {
          MtpMediaSource.this.onUsbDeviceAttached(paramAnonymousUsbDeviceEventArgs.getDevice());
        }
      });
      this.m_UsbManager.addHandler(UsbManager.EVENT_DEVICE_DETACHED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<UsbDeviceEventArgs> paramAnonymousEventKey, UsbDeviceEventArgs paramAnonymousUsbDeviceEventArgs)
        {
          MtpMediaSource.this.onUsbDeviceDetached(paramAnonymousUsbDeviceEventArgs.getDevice());
        }
      });
      return;
    }
    throw new RuntimeException("No UsbManager");
  }
  
  protected File onSetupMediaDataCacheDirectory(BaseApplication paramBaseApplication)
  {
    return new File(paramBaseApplication.getCacheDir(), "MTP_Objects");
  }
  
  final InputStream openMtpObjectInputStream(final int paramInt1, final int paramInt2)
    throws IOException
  {
    final SimpleRef localSimpleRef1 = new SimpleRef();
    final SimpleRef localSimpleRef2 = new SimpleRef();
    try
    {
      runInWorkerThreadAndWait(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aconst_null
          //   1: astore_2
          //   2: aload_0
          //   3: getfield 23	com/oneplus/gallery2/media/MtpMediaSource$7:this$0	Lcom/oneplus/gallery2/media/MtpMediaSource;
          //   6: invokestatic 42	com/oneplus/gallery2/media/MtpMediaSource:access$4	(Lcom/oneplus/gallery2/media/MtpMediaSource;)Ljava/util/Map;
          //   9: aload_0
          //   10: getfield 25	com/oneplus/gallery2/media/MtpMediaSource$7:val$deviceId	I
          //   13: invokestatic 48	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
          //   16: invokeinterface 54 2 0
          //   21: checkcast 56	com/oneplus/gallery2/media/MtpMediaSource$DeviceInfo
          //   24: astore_3
          //   25: aload_3
          //   26: ifnonnull +66 -> 92
          //   29: aconst_null
          //   30: astore_1
          //   31: aload_1
          //   32: ifnull +68 -> 100
          //   35: aload_1
          //   36: aload_0
          //   37: getfield 29	com/oneplus/gallery2/media/MtpMediaSource$7:val$objectId	I
          //   40: invokevirtual 62	android/mtp/MtpDevice:getObjectInfo	(I)Landroid/mtp/MtpObjectInfo;
          //   43: astore 4
          //   45: aload 4
          //   47: ifnull +132 -> 179
          //   50: new 64	com/oneplus/gallery2/media/MtpMediaSource$MediaCacheKeyImpl
          //   53: dup
          //   54: aload_0
          //   55: getfield 23	com/oneplus/gallery2/media/MtpMediaSource$7:this$0	Lcom/oneplus/gallery2/media/MtpMediaSource;
          //   58: aload_3
          //   59: aload_0
          //   60: getfield 29	com/oneplus/gallery2/media/MtpMediaSource$7:val$objectId	I
          //   63: aload 4
          //   65: invokevirtual 70	android/mtp/MtpObjectInfo:getDateModified	()J
          //   68: invokespecial 73	com/oneplus/gallery2/media/MtpMediaSource$MediaCacheKeyImpl:<init>	(Lcom/oneplus/gallery2/media/MtpMediaSource;Lcom/oneplus/gallery2/media/MtpMediaSource$DeviceInfo;IJ)V
          //   71: astore 4
          //   73: aload_0
          //   74: getfield 31	com/oneplus/gallery2/media/MtpMediaSource$7:val$resultRef	Lcom/oneplus/base/Ref;
          //   77: aload_0
          //   78: getfield 23	com/oneplus/gallery2/media/MtpMediaSource$7:this$0	Lcom/oneplus/gallery2/media/MtpMediaSource;
          //   81: aload 4
          //   83: invokevirtual 77	com/oneplus/gallery2/media/MtpMediaSource:openCachedMediaDataInputStream	(Ljava/io/Serializable;)Ljava/io/InputStream;
          //   86: invokeinterface 83 2 0
          //   91: return
          //   92: aload_3
          //   93: getfield 87	com/oneplus/gallery2/media/MtpMediaSource$DeviceInfo:mtpDevice	Landroid/mtp/MtpDevice;
          //   96: astore_1
          //   97: goto -66 -> 31
          //   100: aload_0
          //   101: getfield 27	com/oneplus/gallery2/media/MtpMediaSource$7:val$exRef	Lcom/oneplus/base/Ref;
          //   104: new 89	java/io/IOException
          //   107: dup
          //   108: new 91	java/lang/StringBuilder
          //   111: dup
          //   112: ldc 93
          //   114: invokespecial 96	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
          //   117: aload_0
          //   118: getfield 25	com/oneplus/gallery2/media/MtpMediaSource$7:val$deviceId	I
          //   121: invokevirtual 100	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
          //   124: ldc 102
          //   126: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   129: invokevirtual 109	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   132: invokespecial 110	java/io/IOException:<init>	(Ljava/lang/String;)V
          //   135: invokeinterface 83 2 0
          //   140: return
          //   141: astore_1
          //   142: aload_0
          //   143: getfield 27	com/oneplus/gallery2/media/MtpMediaSource$7:val$exRef	Lcom/oneplus/base/Ref;
          //   146: new 89	java/io/IOException
          //   149: dup
          //   150: new 91	java/lang/StringBuilder
          //   153: dup
          //   154: ldc 112
          //   156: invokespecial 96	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
          //   159: aload_0
          //   160: getfield 29	com/oneplus/gallery2/media/MtpMediaSource$7:val$objectId	I
          //   163: invokevirtual 100	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
          //   166: invokevirtual 109	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   169: aload_1
          //   170: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
          //   173: invokeinterface 83 2 0
          //   178: return
          //   179: aload_0
          //   180: getfield 27	com/oneplus/gallery2/media/MtpMediaSource$7:val$exRef	Lcom/oneplus/base/Ref;
          //   183: new 89	java/io/IOException
          //   186: dup
          //   187: new 91	java/lang/StringBuilder
          //   190: dup
          //   191: ldc 117
          //   193: invokespecial 96	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
          //   196: aload_0
          //   197: getfield 29	com/oneplus/gallery2/media/MtpMediaSource$7:val$objectId	I
          //   200: invokevirtual 100	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
          //   203: ldc 119
          //   205: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   208: invokevirtual 109	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   211: invokespecial 110	java/io/IOException:<init>	(Ljava/lang/String;)V
          //   214: invokeinterface 83 2 0
          //   219: return
          //   220: astore_3
          //   221: ldc 121
          //   223: aconst_null
          //   224: invokestatic 127	java/io/File:createTempFile	(Ljava/lang/String;Ljava/lang/String;)Ljava/io/File;
          //   227: astore_3
          //   228: aload_3
          //   229: astore_2
          //   230: aload_0
          //   231: getfield 23	com/oneplus/gallery2/media/MtpMediaSource$7:this$0	Lcom/oneplus/gallery2/media/MtpMediaSource;
          //   234: invokestatic 131	com/oneplus/gallery2/media/MtpMediaSource:access$1	(Lcom/oneplus/gallery2/media/MtpMediaSource;)Ljava/lang/String;
          //   237: ldc -123
          //   239: aload_0
          //   240: getfield 29	com/oneplus/gallery2/media/MtpMediaSource$7:val$objectId	I
          //   243: invokestatic 48	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
          //   246: ldc -121
          //   248: aload_0
          //   249: getfield 25	com/oneplus/gallery2/media/MtpMediaSource$7:val$deviceId	I
          //   252: invokestatic 48	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
          //   255: invokestatic 141	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
          //   258: aload_3
          //   259: astore_2
          //   260: aload_1
          //   261: aload_0
          //   262: getfield 29	com/oneplus/gallery2/media/MtpMediaSource$7:val$objectId	I
          //   265: aload_3
          //   266: invokevirtual 144	java/io/File:getAbsolutePath	()Ljava/lang/String;
          //   269: invokevirtual 148	android/mtp/MtpDevice:importFile	(ILjava/lang/String;)Z
          //   272: pop
          //   273: aload_3
          //   274: astore_2
          //   275: aload_0
          //   276: getfield 23	com/oneplus/gallery2/media/MtpMediaSource$7:this$0	Lcom/oneplus/gallery2/media/MtpMediaSource;
          //   279: invokestatic 131	com/oneplus/gallery2/media/MtpMediaSource:access$1	(Lcom/oneplus/gallery2/media/MtpMediaSource;)Ljava/lang/String;
          //   282: ldc -106
          //   284: aload_0
          //   285: getfield 29	com/oneplus/gallery2/media/MtpMediaSource$7:val$objectId	I
          //   288: invokestatic 48	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
          //   291: ldc -121
          //   293: aload_0
          //   294: getfield 25	com/oneplus/gallery2/media/MtpMediaSource$7:val$deviceId	I
          //   297: invokestatic 48	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
          //   300: invokestatic 141	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
          //   303: aload_0
          //   304: getfield 23	com/oneplus/gallery2/media/MtpMediaSource$7:this$0	Lcom/oneplus/gallery2/media/MtpMediaSource;
          //   307: aload 4
          //   309: aload_3
          //   310: iconst_1
          //   311: invokevirtual 154	com/oneplus/gallery2/media/MtpMediaSource:addFileToMediaDataCache	(Ljava/io/Serializable;Ljava/io/File;Z)Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;
          //   314: astore_1
          //   315: aload_1
          //   316: ifnull +90 -> 406
          //   319: aload_0
          //   320: getfield 31	com/oneplus/gallery2/media/MtpMediaSource$7:val$resultRef	Lcom/oneplus/base/Ref;
          //   323: aload_1
          //   324: invokevirtual 160	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:openInputStream	()Ljava/io/InputStream;
          //   327: invokeinterface 83 2 0
          //   332: return
          //   333: astore_1
          //   334: aload_0
          //   335: getfield 23	com/oneplus/gallery2/media/MtpMediaSource$7:this$0	Lcom/oneplus/gallery2/media/MtpMediaSource;
          //   338: invokestatic 131	com/oneplus/gallery2/media/MtpMediaSource:access$1	(Lcom/oneplus/gallery2/media/MtpMediaSource;)Ljava/lang/String;
          //   341: new 91	java/lang/StringBuilder
          //   344: dup
          //   345: ldc -94
          //   347: invokespecial 96	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
          //   350: aload_0
          //   351: getfield 29	com/oneplus/gallery2/media/MtpMediaSource$7:val$objectId	I
          //   354: invokevirtual 100	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
          //   357: ldc -121
          //   359: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   362: aload_0
          //   363: getfield 25	com/oneplus/gallery2/media/MtpMediaSource$7:val$deviceId	I
          //   366: invokevirtual 100	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
          //   369: invokevirtual 109	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   372: aload_1
          //   373: invokestatic 166	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
          //   376: aload_2
          //   377: ifnonnull +21 -> 398
          //   380: aload_0
          //   381: getfield 27	com/oneplus/gallery2/media/MtpMediaSource$7:val$exRef	Lcom/oneplus/base/Ref;
          //   384: new 89	java/io/IOException
          //   387: dup
          //   388: aload_1
          //   389: invokespecial 169	java/io/IOException:<init>	(Ljava/lang/Throwable;)V
          //   392: invokeinterface 83 2 0
          //   397: return
          //   398: aload_2
          //   399: invokevirtual 173	java/io/File:delete	()Z
          //   402: pop
          //   403: goto -23 -> 380
          //   406: aload_0
          //   407: getfield 27	com/oneplus/gallery2/media/MtpMediaSource$7:val$exRef	Lcom/oneplus/base/Ref;
          //   410: new 89	java/io/IOException
          //   413: dup
          //   414: new 91	java/lang/StringBuilder
          //   417: dup
          //   418: ldc -81
          //   420: invokespecial 96	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
          //   423: aload_0
          //   424: getfield 29	com/oneplus/gallery2/media/MtpMediaSource$7:val$objectId	I
          //   427: invokevirtual 100	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
          //   430: invokevirtual 109	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   433: invokespecial 110	java/io/IOException:<init>	(Ljava/lang/String;)V
          //   436: invokeinterface 83 2 0
          //   441: return
          //   442: astore_1
          //   443: aload_0
          //   444: getfield 27	com/oneplus/gallery2/media/MtpMediaSource$7:val$exRef	Lcom/oneplus/base/Ref;
          //   447: new 89	java/io/IOException
          //   450: dup
          //   451: new 91	java/lang/StringBuilder
          //   454: dup
          //   455: ldc -79
          //   457: invokespecial 96	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
          //   460: aload_0
          //   461: getfield 29	com/oneplus/gallery2/media/MtpMediaSource$7:val$objectId	I
          //   464: invokevirtual 100	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
          //   467: invokevirtual 109	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   470: aload_1
          //   471: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
          //   474: invokeinterface 83 2 0
          //   479: return
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	480	0	this	7
          //   30	67	1	localMtpDevice	MtpDevice
          //   141	120	1	localThrowable1	Throwable
          //   314	10	1	localCachedMediaData	ExternalMediaSource.CachedMediaData
          //   333	56	1	localThrowable2	Throwable
          //   442	29	1	localThrowable3	Throwable
          //   1	398	2	localObject1	Object
          //   24	69	3	localDeviceInfo	MtpMediaSource.DeviceInfo
          //   220	1	3	localThrowable4	Throwable
          //   227	83	3	localFile	File
          //   43	265	4	localObject2	Object
          // Exception table:
          //   from	to	target	type
          //   35	45	141	java/lang/Throwable
          //   73	91	220	java/lang/Throwable
          //   221	228	333	java/lang/Throwable
          //   230	258	333	java/lang/Throwable
          //   260	273	333	java/lang/Throwable
          //   275	303	333	java/lang/Throwable
          //   319	332	442	java/lang/Throwable
        }
      });
      Object localObject = localSimpleRef2.get();
      if (localObject == null) {
        return (InputStream)localSimpleRef1.get();
      }
      throw ((Throwable)localSimpleRef2.get());
    }
    catch (Throwable localThrowable)
    {
      throw new IOException("Fail to open stream in worker thread", localThrowable);
    }
  }
  
  final InputStream openMtpObjectThumbnailImageInputStream(int paramInt1, int paramInt2)
    throws IOException
  {
    throw new IOException("Not supported");
  }
  
  private static final class DeviceInfo
  {
    public UsbDeviceConnection connection;
    public final UsbDevice device;
    public final int deviceId;
    public Handle deviceOpenHandle;
    public final String deviceSerialNumber;
    public boolean isPermissionReady;
    public boolean isRequestingPermission;
    public final Map<Integer, List<CallbackHandle<MediaSource.MediaObtainCallback>>> mediaObtainHandles = new HashMap();
    public MtpDevice mtpDevice;
    
    public DeviceInfo(UsbDevice paramUsbDevice)
    {
      this.device = paramUsbDevice;
      this.deviceId = paramUsbDevice.getDeviceId();
      this.deviceSerialNumber = paramUsbDevice.getSerialNumber();
    }
  }
  
  private static final class MediaCacheKeyImpl
    implements MediaCacheKey, Serializable
  {
    private static final long serialVersionUID = -8459305811753788062L;
    public final String deviceSerialNumber;
    public final long lastModifiedTime;
    public final int objectId;
    public volatile transient MtpMediaSource source;
    
    public MediaCacheKeyImpl(MtpMediaSource paramMtpMediaSource, MtpMediaSource.DeviceInfo paramDeviceInfo, int paramInt, long paramLong)
    {
      this.source = paramMtpMediaSource;
      this.deviceSerialNumber = paramDeviceInfo.deviceSerialNumber;
      this.objectId = paramInt;
      this.lastModifiedTime = paramLong;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof MediaCacheKeyImpl)) {
        return false;
      }
      paramObject = (MediaCacheKeyImpl)paramObject;
      if (!this.deviceSerialNumber.equals(((MediaCacheKeyImpl)paramObject).deviceSerialNumber)) {}
      while ((this.objectId != ((MediaCacheKeyImpl)paramObject).objectId) || (this.lastModifiedTime != ((MediaCacheKeyImpl)paramObject).lastModifiedTime)) {
        return false;
      }
      return true;
    }
    
    public Uri getContentUri()
    {
      return null;
    }
    
    public String getFilePath()
    {
      return null;
    }
    
    public int hashCode()
    {
      return (int)(this.lastModifiedTime & 0x7FFFFFFF);
    }
    
    public boolean isExpired()
    {
      if (this.source != null) {}
      while (!this.source.isValidCacheKey(this))
      {
        return true;
        this.source = ((MtpMediaSource)BaseApplication.current().findComponent(MtpMediaSource.class));
        if (this.source == null) {
          return true;
        }
      }
      return false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MtpMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */