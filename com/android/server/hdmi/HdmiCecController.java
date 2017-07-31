package com.android.server.hdmi;

import android.hardware.hdmi.HdmiPortInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Predicate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import libcore.util.EmptyArray;

final class HdmiCecController
{
  private static final byte[] EMPTY_BODY = EmptyArray.BYTE;
  private static final int NUM_LOGICAL_ADDRESS = 16;
  private static final String TAG = "HdmiCecController";
  private Handler mControlHandler;
  private Handler mIoHandler;
  private final SparseArray<HdmiCecLocalDevice> mLocalDevices = new SparseArray();
  private volatile long mNativePtr;
  private final Predicate<Integer> mRemoteDeviceAddressPredicate = new Predicate()
  {
    public boolean apply(Integer paramAnonymousInteger)
    {
      return !HdmiCecController.-wrap0(HdmiCecController.this, paramAnonymousInteger.intValue());
    }
  };
  private final HdmiControlService mService;
  private final Predicate<Integer> mSystemAudioAddressPredicate = new Predicate()
  {
    public boolean apply(Integer paramAnonymousInteger)
    {
      return HdmiUtils.getTypeFromAddress(paramAnonymousInteger.intValue()) == 5;
    }
  };
  
  private HdmiCecController(HdmiControlService paramHdmiControlService)
  {
    this.mService = paramHdmiControlService;
  }
  
  private void assertRunOnIoThread()
  {
    if (Looper.myLooper() != this.mIoHandler.getLooper()) {
      throw new IllegalStateException("Should run on io thread.");
    }
  }
  
  private void assertRunOnServiceThread()
  {
    if (Looper.myLooper() != this.mControlHandler.getLooper()) {
      throw new IllegalStateException("Should run on service thread.");
    }
  }
  
  private static byte[] buildBody(int paramInt, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[paramArrayOfByte.length + 1];
    arrayOfByte[0] = ((byte)paramInt);
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 1, paramArrayOfByte.length);
    return arrayOfByte;
  }
  
  static HdmiCecController create(HdmiControlService paramHdmiControlService)
  {
    HdmiCecController localHdmiCecController = new HdmiCecController(paramHdmiControlService);
    long l = nativeInit(localHdmiCecController, paramHdmiControlService.getServiceLooper().getQueue());
    if (l == 0L) {
      return null;
    }
    localHdmiCecController.init(l);
    return localHdmiCecController;
  }
  
  @HdmiAnnotations.IoThreadOnly
  private void handleAllocateLogicalAddress(final int paramInt1, int paramInt2, final AllocateAddressCallback paramAllocateAddressCallback)
  {
    assertRunOnIoThread();
    final int k = paramInt2;
    int j = k;
    if (paramInt2 == 15)
    {
      i = 0;
      j = k;
      if (i < 16)
      {
        if (paramInt1 != HdmiUtils.getTypeFromAddress(i)) {
          break label135;
        }
        j = i;
      }
    }
    int i1 = 15;
    int i = 0;
    for (;;)
    {
      k = i1;
      if (i < 16)
      {
        int i2 = (j + i) % 16;
        if ((i2 != 15) && (paramInt1 == HdmiUtils.getTypeFromAddress(i2)))
        {
          int m = 0;
          k = 0;
          for (;;)
          {
            if (k < 3)
            {
              int n = m;
              if (!sendPollMessage(i2, i2, 1)) {
                n = m + 1;
              }
              k += 1;
              m = n;
              continue;
              label135:
              i += 1;
              break;
            }
          }
          if (m * 2 > 3) {
            k = i2;
          }
        }
      }
      else
      {
        HdmiLogger.debug("New logical address for device [%d]: [preferred:%d, assigned:%d]", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(k) });
        if (paramAllocateAddressCallback != null) {
          runOnServiceThread(new Runnable()
          {
            public void run()
            {
              paramAllocateAddressCallback.onAllocated(paramInt1, k);
            }
          });
        }
        return;
      }
      i += 1;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void handleHotplug(int paramInt, boolean paramBoolean)
  {
    assertRunOnServiceThread();
    HdmiLogger.debug("Hotplug event:[port:%d, connected:%b]", new Object[] { Integer.valueOf(paramInt), Boolean.valueOf(paramBoolean) });
    this.mService.onHotplug(paramInt, paramBoolean);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void handleIncomingCecCommand(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    assertRunOnServiceThread();
    paramArrayOfByte = HdmiCecMessageBuilder.of(paramInt1, paramInt2, paramArrayOfByte);
    HdmiLogger.debug("[R]:" + paramArrayOfByte, new Object[0]);
    onReceiveCommand(paramArrayOfByte);
  }
  
  private void init(long paramLong)
  {
    this.mIoHandler = new Handler(this.mService.getIoLooper());
    this.mControlHandler = new Handler(this.mService.getServiceLooper());
    this.mNativePtr = paramLong;
  }
  
  private boolean isAcceptableAddress(int paramInt)
  {
    if (paramInt == 15) {
      return true;
    }
    return isAllocatedLocalDeviceAddress(paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private boolean isAllocatedLocalDeviceAddress(int paramInt)
  {
    assertRunOnServiceThread();
    int i = 0;
    while (i < this.mLocalDevices.size())
    {
      if (((HdmiCecLocalDevice)this.mLocalDevices.valueAt(i)).isAddressOf(paramInt)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private static native int nativeAddLogicalAddress(long paramLong, int paramInt);
  
  private static native void nativeClearLogicalAddress(long paramLong);
  
  private static native int nativeGetPhysicalAddress(long paramLong);
  
  private static native HdmiPortInfo[] nativeGetPortInfos(long paramLong);
  
  private static native int nativeGetVendorId(long paramLong);
  
  private static native int nativeGetVersion(long paramLong);
  
  private static native long nativeInit(HdmiCecController paramHdmiCecController, MessageQueue paramMessageQueue);
  
  private static native boolean nativeIsConnected(long paramLong, int paramInt);
  
  private static native int nativeSendCecCommand(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte);
  
  private static native void nativeSetAudioReturnChannel(long paramLong, int paramInt, boolean paramBoolean);
  
  private static native void nativeSetOption(long paramLong, int paramInt1, int paramInt2);
  
  @HdmiAnnotations.ServiceThreadOnly
  private void onReceiveCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if ((isAcceptableAddress(paramHdmiCecMessage.getDestination())) && (this.mService.handleCecCommand(paramHdmiCecMessage))) {
      return;
    }
    maySendFeatureAbortCommand(paramHdmiCecMessage, 0);
  }
  
  private List<Integer> pickPollCandidates(int paramInt)
  {
    switch (paramInt & 0x3)
    {
    }
    LinkedList localLinkedList;
    for (Predicate localPredicate = this.mRemoteDeviceAddressPredicate;; localPredicate = this.mSystemAudioAddressPredicate)
    {
      localLinkedList = new LinkedList();
      switch (paramInt & 0x30000)
      {
      default: 
        paramInt = 14;
        while (paramInt >= 0)
        {
          if (localPredicate.apply(Integer.valueOf(paramInt))) {
            localLinkedList.add(Integer.valueOf(paramInt));
          }
          paramInt -= 1;
        }
      }
    }
    paramInt = 0;
    while (paramInt <= 14)
    {
      if (localPredicate.apply(Integer.valueOf(paramInt))) {
        localLinkedList.add(Integer.valueOf(paramInt));
      }
      paramInt += 1;
    }
    return localLinkedList;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void runDevicePolling(final int paramInt1, final List<Integer> paramList1, final int paramInt2, final HdmiControlService.DevicePollingCallback paramDevicePollingCallback, final List<Integer> paramList2)
  {
    assertRunOnServiceThread();
    if (paramList1.isEmpty())
    {
      if (paramDevicePollingCallback != null)
      {
        HdmiLogger.debug("[P]:AllocatedAddress=%s", new Object[] { paramList2.toString() });
        paramDevicePollingCallback.onPollingFinished(paramList2);
      }
      return;
    }
    runOnIoThread(new Runnable()
    {
      public void run()
      {
        if (HdmiCecController.-wrap1(HdmiCecController.this, paramInt1, this.val$candidate.intValue(), paramInt2)) {
          paramList2.add(this.val$candidate);
        }
        HdmiCecController.-wrap6(HdmiCecController.this, new Runnable()
        {
          public void run()
          {
            HdmiCecController.-wrap5(HdmiCecController.this, this.val$sourceAddress, this.val$candidates, this.val$retryCount, this.val$callback, this.val$allocated);
          }
        });
      }
    });
  }
  
  private void runOnIoThread(Runnable paramRunnable)
  {
    this.mIoHandler.post(paramRunnable);
  }
  
  private void runOnServiceThread(Runnable paramRunnable)
  {
    this.mControlHandler.post(paramRunnable);
  }
  
  @HdmiAnnotations.IoThreadOnly
  private boolean sendPollMessage(int paramInt1, int paramInt2, int paramInt3)
  {
    assertRunOnIoThread();
    int i = 0;
    while (i < paramInt3)
    {
      if (nativeSendCecCommand(this.mNativePtr, paramInt1, paramInt2, EMPTY_BODY) == 0) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void addLocalDevice(int paramInt, HdmiCecLocalDevice paramHdmiCecLocalDevice)
  {
    assertRunOnServiceThread();
    this.mLocalDevices.put(paramInt, paramHdmiCecLocalDevice);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  int addLogicalAddress(int paramInt)
  {
    assertRunOnServiceThread();
    if (HdmiUtils.isValidAddress(paramInt)) {
      return nativeAddLogicalAddress(this.mNativePtr, paramInt);
    }
    return -1;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void allocateLogicalAddress(final int paramInt1, final int paramInt2, final AllocateAddressCallback paramAllocateAddressCallback)
  {
    assertRunOnServiceThread();
    runOnIoThread(new Runnable()
    {
      public void run()
      {
        HdmiCecController.-wrap4(HdmiCecController.this, paramInt1, paramInt2, paramAllocateAddressCallback);
      }
    });
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void clearLocalDevices()
  {
    assertRunOnServiceThread();
    this.mLocalDevices.clear();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void clearLogicalAddress()
  {
    assertRunOnServiceThread();
    int i = 0;
    while (i < this.mLocalDevices.size())
    {
      ((HdmiCecLocalDevice)this.mLocalDevices.valueAt(i)).clearAddress();
      i += 1;
    }
    nativeClearLogicalAddress(this.mNativePtr);
  }
  
  void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    int i = 0;
    while (i < this.mLocalDevices.size())
    {
      paramIndentingPrintWriter.println("HdmiCecLocalDevice #" + i + ":");
      paramIndentingPrintWriter.increaseIndent();
      ((HdmiCecLocalDevice)this.mLocalDevices.valueAt(i)).dump(paramIndentingPrintWriter);
      paramIndentingPrintWriter.decreaseIndent();
      i += 1;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void flush(final Runnable paramRunnable)
  {
    assertRunOnServiceThread();
    runOnIoThread(new Runnable()
    {
      public void run()
      {
        HdmiCecController.-wrap6(HdmiCecController.this, paramRunnable);
      }
    });
  }
  
  HdmiCecLocalDevice getLocalDevice(int paramInt)
  {
    return (HdmiCecLocalDevice)this.mLocalDevices.get(paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  List<HdmiCecLocalDevice> getLocalDeviceList()
  {
    assertRunOnServiceThread();
    return HdmiUtils.sparseArrayToList(this.mLocalDevices);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  int getPhysicalAddress()
  {
    assertRunOnServiceThread();
    return nativeGetPhysicalAddress(this.mNativePtr);
  }
  
  HdmiPortInfo[] getPortInfos()
  {
    return nativeGetPortInfos(this.mNativePtr);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  int getVendorId()
  {
    assertRunOnServiceThread();
    return nativeGetVendorId(this.mNativePtr);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  int getVersion()
  {
    assertRunOnServiceThread();
    return nativeGetVersion(this.mNativePtr);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean isConnected(int paramInt)
  {
    assertRunOnServiceThread();
    return nativeIsConnected(this.mNativePtr, paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void maySendFeatureAbortCommand(HdmiCecMessage paramHdmiCecMessage, int paramInt)
  {
    assertRunOnServiceThread();
    int i = paramHdmiCecMessage.getDestination();
    int j = paramHdmiCecMessage.getSource();
    if ((i == 15) || (j == 15)) {
      return;
    }
    int k = paramHdmiCecMessage.getOpcode();
    if (k == 0) {
      return;
    }
    sendCommand(HdmiCecMessageBuilder.buildFeatureAbortCommand(i, j, k, paramInt));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void pollDevices(HdmiControlService.DevicePollingCallback paramDevicePollingCallback, int paramInt1, int paramInt2, int paramInt3)
  {
    assertRunOnServiceThread();
    runDevicePolling(paramInt1, pickPollCandidates(paramInt2), paramInt3, paramDevicePollingCallback, new ArrayList());
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void sendCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    sendCommand(paramHdmiCecMessage, null);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void sendCommand(final HdmiCecMessage paramHdmiCecMessage, final HdmiControlService.SendMessageCallback paramSendMessageCallback)
  {
    assertRunOnServiceThread();
    runOnIoThread(new Runnable()
    {
      public void run()
      {
        HdmiLogger.debug("[S]:" + paramHdmiCecMessage, new Object[0]);
        byte[] arrayOfByte = HdmiCecController.-wrap2(paramHdmiCecMessage.getOpcode(), paramHdmiCecMessage.getParams());
        int i = 0;
        final int j = HdmiCecController.-wrap3(HdmiCecController.-get0(HdmiCecController.this), paramHdmiCecMessage.getSource(), paramHdmiCecMessage.getDestination(), arrayOfByte);
        if (j == 0) {}
        for (;;)
        {
          if (j != 0) {
            Slog.w("HdmiCecController", "Failed to send " + paramHdmiCecMessage);
          }
          if (paramSendMessageCallback != null) {
            HdmiCecController.-wrap6(HdmiCecController.this, new Runnable()
            {
              public void run()
              {
                this.val$callback.onSendCompleted(j);
              }
            });
          }
          return;
          if (i < 1)
          {
            i += 1;
            break;
          }
        }
      }
    });
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void setAudioReturnChannel(int paramInt, boolean paramBoolean)
  {
    assertRunOnServiceThread();
    nativeSetAudioReturnChannel(this.mNativePtr, paramInt, paramBoolean);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void setOption(int paramInt1, int paramInt2)
  {
    assertRunOnServiceThread();
    HdmiLogger.debug("setOption: [flag:%d, value:%d]", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
    nativeSetOption(this.mNativePtr, paramInt1, paramInt2);
  }
  
  static abstract interface AllocateAddressCallback
  {
    public abstract void onAllocated(int paramInt1, int paramInt2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiCecController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */