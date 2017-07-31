package com.android.server.usb;

import android.content.Context;
import android.media.midi.MidiDeviceServer;
import android.media.midi.MidiDeviceServer.Callback;
import android.media.midi.MidiManager;
import android.media.midi.MidiReceiver;
import android.os.Bundle;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructPollfd;
import android.util.Log;
import com.android.internal.midi.MidiEventScheduler;
import com.android.internal.midi.MidiEventScheduler.MidiEvent;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import libcore.io.IoUtils;

public final class UsbMidiDevice
  implements Closeable
{
  private static final int BUFFER_SIZE = 512;
  private static final String TAG = "UsbMidiDevice";
  private final int mAlsaCard;
  private final int mAlsaDevice;
  private final MidiDeviceServer.Callback mCallback = new MidiDeviceServer.Callback()
  {
    public void onClose() {}
    
    /* Error */
    public void onDeviceStatusChanged(MidiDeviceServer paramAnonymousMidiDeviceServer, android.media.midi.MidiDeviceStatus paramAnonymousMidiDeviceStatus)
    {
      // Byte code:
      //   0: aload_2
      //   1: invokevirtual 27	android/media/midi/MidiDeviceStatus:getDeviceInfo	()Landroid/media/midi/MidiDeviceInfo;
      //   4: astore_1
      //   5: aload_1
      //   6: invokevirtual 33	android/media/midi/MidiDeviceInfo:getInputPortCount	()I
      //   9: istore 7
      //   11: aload_1
      //   12: invokevirtual 36	android/media/midi/MidiDeviceInfo:getOutputPortCount	()I
      //   15: istore 6
      //   17: iconst_0
      //   18: istore 5
      //   20: iconst_0
      //   21: istore 4
      //   23: iload 5
      //   25: istore_3
      //   26: iload 4
      //   28: iload 7
      //   30: if_icmpge +14 -> 44
      //   33: aload_2
      //   34: iload 4
      //   36: invokevirtual 40	android/media/midi/MidiDeviceStatus:isInputPortOpen	(I)Z
      //   39: ifeq +87 -> 126
      //   42: iconst_1
      //   43: istore_3
      //   44: iload_3
      //   45: istore 5
      //   47: iload_3
      //   48: ifne +28 -> 76
      //   51: iconst_0
      //   52: istore 4
      //   54: iload_3
      //   55: istore 5
      //   57: iload 4
      //   59: iload 6
      //   61: if_icmpge +15 -> 76
      //   64: aload_2
      //   65: iload 4
      //   67: invokevirtual 44	android/media/midi/MidiDeviceStatus:getOutputPortOpenCount	(I)I
      //   70: ifle +65 -> 135
      //   73: iconst_1
      //   74: istore 5
      //   76: aload_0
      //   77: getfield 14	com/android/server/usb/UsbMidiDevice$1:this$0	Lcom/android/server/usb/UsbMidiDevice;
      //   80: invokestatic 48	com/android/server/usb/UsbMidiDevice:-get2	(Lcom/android/server/usb/UsbMidiDevice;)Ljava/lang/Object;
      //   83: astore_1
      //   84: aload_1
      //   85: monitorenter
      //   86: iload 5
      //   88: ifeq +13 -> 101
      //   91: aload_0
      //   92: getfield 14	com/android/server/usb/UsbMidiDevice$1:this$0	Lcom/android/server/usb/UsbMidiDevice;
      //   95: invokestatic 52	com/android/server/usb/UsbMidiDevice:-get1	(Lcom/android/server/usb/UsbMidiDevice;)Z
      //   98: ifeq +46 -> 144
      //   101: iload 5
      //   103: ifne +20 -> 123
      //   106: aload_0
      //   107: getfield 14	com/android/server/usb/UsbMidiDevice$1:this$0	Lcom/android/server/usb/UsbMidiDevice;
      //   110: invokestatic 52	com/android/server/usb/UsbMidiDevice:-get1	(Lcom/android/server/usb/UsbMidiDevice;)Z
      //   113: ifeq +10 -> 123
      //   116: aload_0
      //   117: getfield 14	com/android/server/usb/UsbMidiDevice$1:this$0	Lcom/android/server/usb/UsbMidiDevice;
      //   120: invokestatic 55	com/android/server/usb/UsbMidiDevice:-wrap1	(Lcom/android/server/usb/UsbMidiDevice;)V
      //   123: aload_1
      //   124: monitorexit
      //   125: return
      //   126: iload 4
      //   128: iconst_1
      //   129: iadd
      //   130: istore 4
      //   132: goto -109 -> 23
      //   135: iload 4
      //   137: iconst_1
      //   138: iadd
      //   139: istore 4
      //   141: goto -87 -> 54
      //   144: aload_0
      //   145: getfield 14	com/android/server/usb/UsbMidiDevice$1:this$0	Lcom/android/server/usb/UsbMidiDevice;
      //   148: invokestatic 58	com/android/server/usb/UsbMidiDevice:-wrap0	(Lcom/android/server/usb/UsbMidiDevice;)Z
      //   151: pop
      //   152: goto -29 -> 123
      //   155: astore_2
      //   156: aload_1
      //   157: monitorexit
      //   158: aload_2
      //   159: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	160	0	this	1
      //   0	160	1	paramAnonymousMidiDeviceServer	MidiDeviceServer
      //   0	160	2	paramAnonymousMidiDeviceStatus	android.media.midi.MidiDeviceStatus
      //   25	30	3	i	int
      //   21	119	4	j	int
      //   18	84	5	k	int
      //   15	47	6	m	int
      //   9	22	7	n	int
      // Exception table:
      //   from	to	target	type
      //   91	101	155	finally
      //   106	123	155	finally
      //   144	152	155	finally
    }
  };
  private MidiEventScheduler[] mEventSchedulers;
  private FileDescriptor[] mFileDescriptors;
  private final InputReceiverProxy[] mInputPortReceivers;
  private FileInputStream[] mInputStreams;
  private boolean mIsOpen;
  private final Object mLock = new Object();
  private FileOutputStream[] mOutputStreams;
  private int mPipeFD = -1;
  private StructPollfd[] mPollFDs;
  private MidiDeviceServer mServer;
  private final int mSubdeviceCount;
  
  private UsbMidiDevice(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mAlsaCard = paramInt1;
    this.mAlsaDevice = paramInt2;
    this.mSubdeviceCount = paramInt3;
    this.mInputPortReceivers = new InputReceiverProxy[paramInt3];
    paramInt1 = 0;
    while (paramInt1 < paramInt3)
    {
      this.mInputPortReceivers[paramInt1] = new InputReceiverProxy(null);
      paramInt1 += 1;
    }
  }
  
  private void closeLocked()
  {
    int i = 0;
    while (i < this.mEventSchedulers.length)
    {
      this.mInputPortReceivers[i].setReceiver(null);
      this.mEventSchedulers[i].close();
      i += 1;
    }
    this.mEventSchedulers = null;
    i = 0;
    while (i < this.mInputStreams.length)
    {
      IoUtils.closeQuietly(this.mInputStreams[i]);
      i += 1;
    }
    this.mInputStreams = null;
    i = 0;
    while (i < this.mOutputStreams.length)
    {
      IoUtils.closeQuietly(this.mOutputStreams[i]);
      i += 1;
    }
    this.mOutputStreams = null;
    nativeClose(this.mFileDescriptors);
    this.mFileDescriptors = null;
    this.mIsOpen = false;
  }
  
  public static UsbMidiDevice create(Context paramContext, Bundle paramBundle, int paramInt1, int paramInt2)
  {
    int i = nativeGetSubdeviceCount(paramInt1, paramInt2);
    if (i <= 0)
    {
      Log.e("UsbMidiDevice", "nativeGetSubdeviceCount failed");
      return null;
    }
    UsbMidiDevice localUsbMidiDevice = new UsbMidiDevice(paramInt1, paramInt2, i);
    if (!localUsbMidiDevice.register(paramContext, paramBundle))
    {
      IoUtils.closeQuietly(localUsbMidiDevice);
      Log.e("UsbMidiDevice", "createDeviceServer failed");
      return null;
    }
    return localUsbMidiDevice;
  }
  
  private native void nativeClose(FileDescriptor[] paramArrayOfFileDescriptor);
  
  private static native int nativeGetSubdeviceCount(int paramInt1, int paramInt2);
  
  private native FileDescriptor[] nativeOpen(int paramInt1, int paramInt2, int paramInt3);
  
  private boolean openLocked()
  {
    final Object localObject1 = nativeOpen(this.mAlsaCard, this.mAlsaDevice, this.mSubdeviceCount);
    if (localObject1 == null)
    {
      Log.e("UsbMidiDevice", "nativeOpen failed");
      return false;
    }
    this.mFileDescriptors = ((FileDescriptor[])localObject1);
    int k = localObject1.length;
    int j = localObject1.length - 1;
    this.mPollFDs = new StructPollfd[k];
    this.mInputStreams = new FileInputStream[k];
    final int i = 0;
    final Object localObject2;
    while (i < k)
    {
      localObject2 = localObject1[i];
      StructPollfd localStructPollfd = new StructPollfd();
      localStructPollfd.fd = ((FileDescriptor)localObject2);
      localStructPollfd.events = ((short)OsConstants.POLLIN);
      this.mPollFDs[i] = localStructPollfd;
      this.mInputStreams[i] = new FileInputStream((FileDescriptor)localObject2);
      i += 1;
    }
    this.mOutputStreams = new FileOutputStream[j];
    this.mEventSchedulers = new MidiEventScheduler[j];
    i = 0;
    while (i < j)
    {
      this.mOutputStreams[i] = new FileOutputStream(localObject1[i]);
      localObject2 = new MidiEventScheduler();
      this.mEventSchedulers[i] = localObject2;
      this.mInputPortReceivers[i].setReceiver(((MidiEventScheduler)localObject2).getReceiver());
      i += 1;
    }
    new Thread("UsbMidiDevice input thread")
    {
      public void run()
      {
        byte[] arrayOfByte = new byte['Ȁ'];
        try
        {
          l = System.nanoTime();
        }
        catch (IOException localIOException)
        {
          synchronized (UsbMidiDevice.-get2(UsbMidiDevice.this))
          {
            long l;
            boolean bool = UsbMidiDevice.-get1(UsbMidiDevice.this);
            if (!bool)
            {
              Log.d("UsbMidiDevice", "input thread exit");
              return;
            }
            int i = 0;
            StructPollfd localStructPollfd;
            if (i < UsbMidiDevice.-get3(UsbMidiDevice.this).length)
            {
              localStructPollfd = UsbMidiDevice.-get3(UsbMidiDevice.this)[i];
              j = localStructPollfd.revents;
              int k = OsConstants.POLLERR;
              int m = OsConstants.POLLHUP;
              if ((j & (k | m)) == 0) {
                break label130;
              }
            }
            do
            {
              Os.poll(UsbMidiDevice.-get3(UsbMidiDevice.this), -1);
              break;
              localIOException = localIOException;
              Log.d("UsbMidiDevice", "reader thread exiting");
              break label41;
              if ((localStructPollfd.revents & OsConstants.POLLIN) == 0) {
                break label192;
              }
              localStructPollfd.revents = 0;
            } while (i == UsbMidiDevice.-get0(UsbMidiDevice.this).length - 1);
            int j = UsbMidiDevice.-get0(UsbMidiDevice.this)[i].read(arrayOfByte);
            this.val$outputReceivers[i].send(arrayOfByte, 0, j, l);
            i += 1;
          }
        }
        catch (ErrnoException localErrnoException)
        {
          for (;;)
          {
            label41:
            label130:
            label192:
            Log.d("UsbMidiDevice", "reader thread exiting");
          }
        }
      }
    }.start();
    i = 0;
    while (i < j)
    {
      localObject1 = this.mEventSchedulers[i];
      localObject2 = this.mOutputStreams[i];
      new Thread("UsbMidiDevice output thread " + i)
      {
        public void run()
        {
          try
          {
            for (;;)
            {
              MidiEventScheduler.MidiEvent localMidiEvent = (MidiEventScheduler.MidiEvent)localObject1.waitNextEvent();
              if (localMidiEvent == null)
              {
                Log.d("UsbMidiDevice", "output thread exit");
                return;
              }
              try
              {
                localObject2.write(localMidiEvent.data, 0, localMidiEvent.count);
                localObject1.addEventToPool(localMidiEvent);
              }
              catch (IOException localIOException)
              {
                for (;;)
                {
                  Log.e("UsbMidiDevice", "write failed for port " + i);
                }
              }
            }
          }
          catch (InterruptedException localInterruptedException) {}
        }
      }.start();
      i += 1;
    }
    this.mIsOpen = true;
    return true;
  }
  
  private boolean register(Context paramContext, Bundle paramBundle)
  {
    paramContext = (MidiManager)paramContext.getSystemService("midi");
    if (paramContext == null)
    {
      Log.e("UsbMidiDevice", "No MidiManager in UsbMidiDevice.create()");
      return false;
    }
    this.mServer = paramContext.createDeviceServer(this.mInputPortReceivers, this.mSubdeviceCount, null, null, paramBundle, 1, this.mCallback);
    return this.mServer != null;
  }
  
  public void close()
    throws IOException
  {
    synchronized (this.mLock)
    {
      if (this.mIsOpen) {
        closeLocked();
      }
      if (this.mServer != null) {
        IoUtils.closeQuietly(this.mServer);
      }
      return;
    }
  }
  
  private final class InputReceiverProxy
    extends MidiReceiver
  {
    private MidiReceiver mReceiver;
    
    private InputReceiverProxy() {}
    
    public void onFlush()
      throws IOException
    {
      MidiReceiver localMidiReceiver = this.mReceiver;
      if (localMidiReceiver != null) {
        localMidiReceiver.flush();
      }
    }
    
    public void onSend(byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong)
      throws IOException
    {
      MidiReceiver localMidiReceiver = this.mReceiver;
      if (localMidiReceiver != null) {
        localMidiReceiver.send(paramArrayOfByte, paramInt1, paramInt2, paramLong);
      }
    }
    
    public void setReceiver(MidiReceiver paramMidiReceiver)
    {
      this.mReceiver = paramMidiReceiver;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usb/UsbMidiDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */