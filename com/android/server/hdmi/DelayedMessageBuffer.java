package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import java.util.ArrayList;
import java.util.Iterator;

final class DelayedMessageBuffer
{
  private final ArrayList<HdmiCecMessage> mBuffer = new ArrayList();
  private final HdmiCecLocalDevice mDevice;
  
  DelayedMessageBuffer(HdmiCecLocalDevice paramHdmiCecLocalDevice)
  {
    this.mDevice = paramHdmiCecLocalDevice;
  }
  
  private void removeActiveSource()
  {
    Iterator localIterator = this.mBuffer.iterator();
    while (localIterator.hasNext()) {
      if (((HdmiCecMessage)localIterator.next()).getOpcode() == 130) {
        localIterator.remove();
      }
    }
  }
  
  void add(HdmiCecMessage paramHdmiCecMessage)
  {
    int i = 1;
    switch (paramHdmiCecMessage.getOpcode())
    {
    default: 
      i = 0;
    }
    for (;;)
    {
      if (i != 0) {
        HdmiLogger.debug("Buffering message:" + paramHdmiCecMessage, new Object[0]);
      }
      return;
      removeActiveSource();
      this.mBuffer.add(paramHdmiCecMessage);
      continue;
      this.mBuffer.add(paramHdmiCecMessage);
    }
  }
  
  boolean isBuffered(int paramInt)
  {
    Iterator localIterator = this.mBuffer.iterator();
    while (localIterator.hasNext()) {
      if (((HdmiCecMessage)localIterator.next()).getOpcode() == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  void processActiveSource(int paramInt)
  {
    Object localObject = new ArrayList(this.mBuffer);
    this.mBuffer.clear();
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      HdmiCecMessage localHdmiCecMessage = (HdmiCecMessage)((Iterator)localObject).next();
      if ((localHdmiCecMessage.getOpcode() == 130) && (localHdmiCecMessage.getSource() == paramInt))
      {
        this.mDevice.onMessage(localHdmiCecMessage);
        HdmiLogger.debug("Processing message:" + localHdmiCecMessage, new Object[0]);
      }
      else
      {
        this.mBuffer.add(localHdmiCecMessage);
      }
    }
  }
  
  void processAllMessages()
  {
    Object localObject = new ArrayList(this.mBuffer);
    this.mBuffer.clear();
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      HdmiCecMessage localHdmiCecMessage = (HdmiCecMessage)((Iterator)localObject).next();
      this.mDevice.onMessage(localHdmiCecMessage);
      HdmiLogger.debug("Processing message:" + localHdmiCecMessage, new Object[0]);
    }
  }
  
  void processMessagesForDevice(int paramInt)
  {
    Object localObject = new ArrayList(this.mBuffer);
    this.mBuffer.clear();
    HdmiLogger.debug("Checking message for address:" + paramInt, new Object[0]);
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      HdmiCecMessage localHdmiCecMessage = (HdmiCecMessage)((Iterator)localObject).next();
      if (localHdmiCecMessage.getSource() != paramInt)
      {
        this.mBuffer.add(localHdmiCecMessage);
      }
      else if ((localHdmiCecMessage.getOpcode() != 130) || (this.mDevice.isInputReady(HdmiDeviceInfo.idForCecDevice(paramInt))))
      {
        this.mDevice.onMessage(localHdmiCecMessage);
        HdmiLogger.debug("Processing message:" + localHdmiCecMessage, new Object[0]);
      }
      else
      {
        this.mBuffer.add(localHdmiCecMessage);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/DelayedMessageBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */