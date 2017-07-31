package android.media.midi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.system.OsConstants;
import android.util.Log;
import com.android.internal.midi.MidiDispatcher;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import libcore.io.IoUtils;

public final class MidiDeviceServer
  implements Closeable
{
  private static final String TAG = "MidiDeviceServer";
  private final Callback mCallback;
  private MidiDeviceInfo mDeviceInfo;
  private final CloseGuard mGuard = CloseGuard.get();
  private final int mInputPortCount;
  private final boolean[] mInputPortOpen;
  private final MidiOutputPort[] mInputPortOutputPorts;
  private final MidiReceiver[] mInputPortReceivers;
  private final CopyOnWriteArrayList<MidiInputPort> mInputPorts = new CopyOnWriteArrayList();
  private boolean mIsClosed;
  private final IMidiManager mMidiManager;
  private final int mOutputPortCount;
  private MidiDispatcher[] mOutputPortDispatchers;
  private final int[] mOutputPortOpenCount;
  private final HashMap<IBinder, PortClient> mPortClients = new HashMap();
  private final IMidiDeviceServer mServer = new IMidiDeviceServer.Stub()
  {
    public void closeDevice()
    {
      if (MidiDeviceServer.-get0(MidiDeviceServer.this) != null) {
        MidiDeviceServer.-get0(MidiDeviceServer.this).onClose();
      }
      IoUtils.closeQuietly(MidiDeviceServer.this);
    }
    
    public void closePort(IBinder paramAnonymousIBinder)
    {
      synchronized (MidiDeviceServer.-get10(MidiDeviceServer.this))
      {
        paramAnonymousIBinder = (MidiDeviceServer.PortClient)MidiDeviceServer.-get10(MidiDeviceServer.this).remove(paramAnonymousIBinder);
        if (paramAnonymousIBinder != null) {
          paramAnonymousIBinder.close();
        }
        return;
      }
    }
    
    public int connectPorts(IBinder paramAnonymousIBinder, ParcelFileDescriptor arg2, int paramAnonymousInt)
    {
      Object localObject = new MidiInputPort(???, paramAnonymousInt);
      synchronized (MidiDeviceServer.-get8(MidiDeviceServer.this)[paramAnonymousInt])
      {
        ???.getSender().connect((MidiReceiver)localObject);
        int i = ???.getReceiverCount();
        MidiDeviceServer.-get9(MidiDeviceServer.this)[paramAnonymousInt] = i;
        MidiDeviceServer.-wrap0(MidiDeviceServer.this);
        MidiDeviceServer.-get6(MidiDeviceServer.this).add(localObject);
        localObject = new MidiDeviceServer.OutputPortClient(MidiDeviceServer.this, paramAnonymousIBinder, (MidiInputPort)localObject);
      }
      synchronized (MidiDeviceServer.-get10(MidiDeviceServer.this))
      {
        MidiDeviceServer.-get10(MidiDeviceServer.this).put(paramAnonymousIBinder, localObject);
        return Process.myPid();
        paramAnonymousIBinder = finally;
        throw paramAnonymousIBinder;
      }
    }
    
    public MidiDeviceInfo getDeviceInfo()
    {
      return MidiDeviceServer.-get1(MidiDeviceServer.this);
    }
    
    public ParcelFileDescriptor openInputPort(IBinder paramAnonymousIBinder, int paramAnonymousInt)
    {
      if ((MidiDeviceServer.-get1(MidiDeviceServer.this).isPrivate()) && (Binder.getCallingUid() != Process.myUid())) {
        throw new SecurityException("Can't access private device from different UID");
      }
      if ((paramAnonymousInt < 0) || (paramAnonymousInt >= MidiDeviceServer.-get2(MidiDeviceServer.this)))
      {
        Log.e("MidiDeviceServer", "portNumber out of range in openInputPort: " + paramAnonymousInt);
        return null;
      }
      synchronized (MidiDeviceServer.-get4(MidiDeviceServer.this))
      {
        if (MidiDeviceServer.-get4(MidiDeviceServer.this)[paramAnonymousInt] != null)
        {
          Log.d("MidiDeviceServer", "port " + paramAnonymousInt + " already open");
          return null;
        }
        try
        {
          ParcelFileDescriptor[] arrayOfParcelFileDescriptor = ParcelFileDescriptor.createSocketPair(OsConstants.SOCK_SEQPACKET);
          ??? = new MidiOutputPort(arrayOfParcelFileDescriptor[0], paramAnonymousInt);
          MidiDeviceServer.-get4(MidiDeviceServer.this)[paramAnonymousInt] = ???;
          ((MidiSender)???).connect(MidiDeviceServer.-get5(MidiDeviceServer.this)[paramAnonymousInt]);
          MidiDeviceServer.InputPortClient localInputPortClient = new MidiDeviceServer.InputPortClient(MidiDeviceServer.this, paramAnonymousIBinder, (MidiOutputPort)???);
          synchronized (MidiDeviceServer.-get10(MidiDeviceServer.this))
          {
            MidiDeviceServer.-get10(MidiDeviceServer.this).put(paramAnonymousIBinder, localInputPortClient);
            MidiDeviceServer.-get3(MidiDeviceServer.this)[paramAnonymousInt] = 1;
            MidiDeviceServer.-wrap0(MidiDeviceServer.this);
            paramAnonymousIBinder = arrayOfParcelFileDescriptor[1];
            return paramAnonymousIBinder;
          }
          paramAnonymousIBinder = finally;
        }
        catch (IOException paramAnonymousIBinder)
        {
          Log.e("MidiDeviceServer", "unable to create ParcelFileDescriptors in openInputPort");
          return null;
        }
      }
    }
    
    /* Error */
    public ParcelFileDescriptor openOutputPort(IBinder paramAnonymousIBinder, int paramAnonymousInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	android/media/midi/MidiDeviceServer$1:this$0	Landroid/media/midi/MidiDeviceServer;
      //   4: invokestatic 113	android/media/midi/MidiDeviceServer:-get1	(Landroid/media/midi/MidiDeviceServer;)Landroid/media/midi/MidiDeviceInfo;
      //   7: invokevirtual 123	android/media/midi/MidiDeviceInfo:isPrivate	()Z
      //   10: ifeq +22 -> 32
      //   13: invokestatic 128	android/os/Binder:getCallingUid	()I
      //   16: invokestatic 131	android/os/Process:myUid	()I
      //   19: if_icmpeq +13 -> 32
      //   22: new 133	java/lang/SecurityException
      //   25: dup
      //   26: ldc -121
      //   28: invokespecial 138	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
      //   31: athrow
      //   32: iload_2
      //   33: iflt +14 -> 47
      //   36: iload_2
      //   37: aload_0
      //   38: getfield 12	android/media/midi/MidiDeviceServer$1:this$0	Landroid/media/midi/MidiDeviceServer;
      //   41: invokestatic 211	android/media/midi/MidiDeviceServer:-get7	(Landroid/media/midi/MidiDeviceServer;)I
      //   44: if_icmplt +30 -> 74
      //   47: ldc -112
      //   49: new 146	java/lang/StringBuilder
      //   52: dup
      //   53: invokespecial 147	java/lang/StringBuilder:<init>	()V
      //   56: ldc -43
      //   58: invokevirtual 153	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   61: iload_2
      //   62: invokevirtual 156	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   65: invokevirtual 160	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   68: invokestatic 166	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   71: pop
      //   72: aconst_null
      //   73: areturn
      //   74: getstatic 183	android/system/OsConstants:SOCK_SEQPACKET	I
      //   77: invokestatic 189	android/os/ParcelFileDescriptor:createSocketPair	(I)[Landroid/os/ParcelFileDescriptor;
      //   80: astore 4
      //   82: new 53	android/media/midi/MidiInputPort
      //   85: dup
      //   86: aload 4
      //   88: iconst_0
      //   89: aaload
      //   90: iload_2
      //   91: invokespecial 56	android/media/midi/MidiInputPort:<init>	(Landroid/os/ParcelFileDescriptor;I)V
      //   94: astore 6
      //   96: aload_0
      //   97: getfield 12	android/media/midi/MidiDeviceServer$1:this$0	Landroid/media/midi/MidiDeviceServer;
      //   100: invokestatic 60	android/media/midi/MidiDeviceServer:-get8	(Landroid/media/midi/MidiDeviceServer;)[Lcom/android/internal/midi/MidiDispatcher;
      //   103: iload_2
      //   104: aaload
      //   105: astore 5
      //   107: aload 5
      //   109: monitorenter
      //   110: aload 5
      //   112: invokevirtual 66	com/android/internal/midi/MidiDispatcher:getSender	()Landroid/media/midi/MidiSender;
      //   115: aload 6
      //   117: invokevirtual 72	android/media/midi/MidiSender:connect	(Landroid/media/midi/MidiReceiver;)V
      //   120: aload 5
      //   122: invokevirtual 76	com/android/internal/midi/MidiDispatcher:getReceiverCount	()I
      //   125: istore_3
      //   126: aload_0
      //   127: getfield 12	android/media/midi/MidiDeviceServer$1:this$0	Landroid/media/midi/MidiDeviceServer;
      //   130: invokestatic 80	android/media/midi/MidiDeviceServer:-get9	(Landroid/media/midi/MidiDeviceServer;)[I
      //   133: iload_2
      //   134: iload_3
      //   135: iastore
      //   136: aload_0
      //   137: getfield 12	android/media/midi/MidiDeviceServer$1:this$0	Landroid/media/midi/MidiDeviceServer;
      //   140: invokestatic 83	android/media/midi/MidiDeviceServer:-wrap0	(Landroid/media/midi/MidiDeviceServer;)V
      //   143: aload 5
      //   145: monitorexit
      //   146: aload_0
      //   147: getfield 12	android/media/midi/MidiDeviceServer$1:this$0	Landroid/media/midi/MidiDeviceServer;
      //   150: invokestatic 87	android/media/midi/MidiDeviceServer:-get6	(Landroid/media/midi/MidiDeviceServer;)Ljava/util/concurrent/CopyOnWriteArrayList;
      //   153: aload 6
      //   155: invokevirtual 93	java/util/concurrent/CopyOnWriteArrayList:add	(Ljava/lang/Object;)Z
      //   158: pop
      //   159: new 95	android/media/midi/MidiDeviceServer$OutputPortClient
      //   162: dup
      //   163: aload_0
      //   164: getfield 12	android/media/midi/MidiDeviceServer$1:this$0	Landroid/media/midi/MidiDeviceServer;
      //   167: aload_1
      //   168: aload 6
      //   170: invokespecial 98	android/media/midi/MidiDeviceServer$OutputPortClient:<init>	(Landroid/media/midi/MidiDeviceServer;Landroid/os/IBinder;Landroid/media/midi/MidiInputPort;)V
      //   173: astore 6
      //   175: aload_0
      //   176: getfield 12	android/media/midi/MidiDeviceServer$1:this$0	Landroid/media/midi/MidiDeviceServer;
      //   179: invokestatic 38	android/media/midi/MidiDeviceServer:-get10	(Landroid/media/midi/MidiDeviceServer;)Ljava/util/HashMap;
      //   182: astore 5
      //   184: aload 5
      //   186: monitorenter
      //   187: aload_0
      //   188: getfield 12	android/media/midi/MidiDeviceServer$1:this$0	Landroid/media/midi/MidiDeviceServer;
      //   191: invokestatic 38	android/media/midi/MidiDeviceServer:-get10	(Landroid/media/midi/MidiDeviceServer;)Ljava/util/HashMap;
      //   194: aload_1
      //   195: aload 6
      //   197: invokevirtual 102	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      //   200: pop
      //   201: aload 5
      //   203: monitorexit
      //   204: aload 4
      //   206: iconst_1
      //   207: aaload
      //   208: areturn
      //   209: astore_1
      //   210: aload 5
      //   212: monitorexit
      //   213: aload_1
      //   214: athrow
      //   215: astore_1
      //   216: ldc -112
      //   218: ldc -41
      //   220: invokestatic 166	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   223: pop
      //   224: aconst_null
      //   225: areturn
      //   226: astore_1
      //   227: aload 5
      //   229: monitorexit
      //   230: aload_1
      //   231: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	232	0	this	1
      //   0	232	1	paramAnonymousIBinder	IBinder
      //   0	232	2	paramAnonymousInt	int
      //   125	10	3	i	int
      //   80	125	4	arrayOfParcelFileDescriptor	ParcelFileDescriptor[]
      //   94	102	6	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   110	143	209	finally
      //   74	110	215	java/io/IOException
      //   143	187	215	java/io/IOException
      //   201	204	215	java/io/IOException
      //   210	215	215	java/io/IOException
      //   227	232	215	java/io/IOException
      //   187	201	226	finally
    }
    
    public void setDeviceInfo(MidiDeviceInfo paramAnonymousMidiDeviceInfo)
    {
      if (Binder.getCallingUid() != 1000) {
        throw new SecurityException("setDeviceInfo should only be called by MidiService");
      }
      if (MidiDeviceServer.-get1(MidiDeviceServer.this) != null) {
        throw new IllegalStateException("setDeviceInfo should only be called once");
      }
      MidiDeviceServer.-set0(MidiDeviceServer.this, paramAnonymousMidiDeviceInfo);
    }
  };
  
  MidiDeviceServer(IMidiManager paramIMidiManager, MidiReceiver[] paramArrayOfMidiReceiver, int paramInt, Callback paramCallback)
  {
    this.mMidiManager = paramIMidiManager;
    this.mInputPortReceivers = paramArrayOfMidiReceiver;
    this.mInputPortCount = paramArrayOfMidiReceiver.length;
    this.mOutputPortCount = paramInt;
    this.mCallback = paramCallback;
    this.mInputPortOutputPorts = new MidiOutputPort[this.mInputPortCount];
    this.mOutputPortDispatchers = new MidiDispatcher[paramInt];
    int i = 0;
    while (i < paramInt)
    {
      this.mOutputPortDispatchers[i] = new MidiDispatcher();
      i += 1;
    }
    this.mInputPortOpen = new boolean[this.mInputPortCount];
    this.mOutputPortOpenCount = new int[paramInt];
    this.mGuard.open("close");
  }
  
  MidiDeviceServer(IMidiManager paramIMidiManager, MidiReceiver[] paramArrayOfMidiReceiver, MidiDeviceInfo paramMidiDeviceInfo, Callback paramCallback)
  {
    this(paramIMidiManager, paramArrayOfMidiReceiver, paramMidiDeviceInfo.getOutputPortCount(), paramCallback);
    this.mDeviceInfo = paramMidiDeviceInfo;
  }
  
  private void updateDeviceStatus()
  {
    long l = Binder.clearCallingIdentity();
    MidiDeviceStatus localMidiDeviceStatus = new MidiDeviceStatus(this.mDeviceInfo, this.mInputPortOpen, this.mOutputPortOpenCount);
    if (this.mCallback != null) {
      this.mCallback.onDeviceStatusChanged(this, localMidiDeviceStatus);
    }
    try
    {
      this.mMidiManager.setDeviceStatus(this.mServer, localMidiDeviceStatus);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("MidiDeviceServer", "RemoteException in updateDeviceStatus");
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public IBinder asBinder()
  {
    return this.mServer.asBinder();
  }
  
  public void close()
    throws IOException
  {
    for (;;)
    {
      int i;
      synchronized (this.mGuard)
      {
        boolean bool = this.mIsClosed;
        if (bool) {
          return;
        }
        this.mGuard.close();
        i = 0;
        if (i < this.mInputPortCount)
        {
          localObject1 = this.mInputPortOutputPorts[i];
          if (localObject1 == null) {
            break label149;
          }
          IoUtils.closeQuietly((AutoCloseable)localObject1);
          this.mInputPortOutputPorts[i] = null;
          break label149;
        }
        Object localObject1 = this.mInputPorts.iterator();
        if (((Iterator)localObject1).hasNext()) {
          IoUtils.closeQuietly((MidiInputPort)((Iterator)localObject1).next());
        }
      }
      this.mInputPorts.clear();
      try
      {
        this.mMidiManager.unregisterDeviceServer(this.mServer);
        this.mIsClosed = true;
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("MidiDeviceServer", "RemoteException in unregisterDeviceServer");
        }
      }
      label149:
      i += 1;
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      this.mGuard.warnIfOpen();
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  IMidiDeviceServer getBinderInterface()
  {
    return this.mServer;
  }
  
  public MidiReceiver[] getOutputPortReceivers()
  {
    MidiReceiver[] arrayOfMidiReceiver = new MidiReceiver[this.mOutputPortCount];
    System.arraycopy(this.mOutputPortDispatchers, 0, arrayOfMidiReceiver, 0, this.mOutputPortCount);
    return arrayOfMidiReceiver;
  }
  
  public static abstract interface Callback
  {
    public abstract void onClose();
    
    public abstract void onDeviceStatusChanged(MidiDeviceServer paramMidiDeviceServer, MidiDeviceStatus paramMidiDeviceStatus);
  }
  
  private class InputPortClient
    extends MidiDeviceServer.PortClient
  {
    private final MidiOutputPort mOutputPort;
    
    InputPortClient(IBinder paramIBinder, MidiOutputPort paramMidiOutputPort)
    {
      super(paramIBinder);
      this.mOutputPort = paramMidiOutputPort;
    }
    
    void close()
    {
      this.mToken.unlinkToDeath(this, 0);
      synchronized (MidiDeviceServer.-get4(MidiDeviceServer.this))
      {
        int i = this.mOutputPort.getPortNumber();
        MidiDeviceServer.-get4(MidiDeviceServer.this)[i] = null;
        MidiDeviceServer.-get3(MidiDeviceServer.this)[i] = 0;
        MidiDeviceServer.-wrap0(MidiDeviceServer.this);
        IoUtils.closeQuietly(this.mOutputPort);
        return;
      }
    }
  }
  
  private class OutputPortClient
    extends MidiDeviceServer.PortClient
  {
    private final MidiInputPort mInputPort;
    
    OutputPortClient(IBinder paramIBinder, MidiInputPort paramMidiInputPort)
    {
      super(paramIBinder);
      this.mInputPort = paramMidiInputPort;
    }
    
    void close()
    {
      this.mToken.unlinkToDeath(this, 0);
      int i = this.mInputPort.getPortNumber();
      synchronized (MidiDeviceServer.-get8(MidiDeviceServer.this)[i])
      {
        ???.getSender().disconnect(this.mInputPort);
        int j = ???.getReceiverCount();
        MidiDeviceServer.-get9(MidiDeviceServer.this)[i] = j;
        MidiDeviceServer.-wrap0(MidiDeviceServer.this);
        MidiDeviceServer.-get6(MidiDeviceServer.this).remove(this.mInputPort);
        IoUtils.closeQuietly(this.mInputPort);
        return;
      }
    }
  }
  
  private abstract class PortClient
    implements IBinder.DeathRecipient
  {
    final IBinder mToken;
    
    PortClient(IBinder paramIBinder)
    {
      this.mToken = paramIBinder;
      try
      {
        paramIBinder.linkToDeath(this, 0);
        return;
      }
      catch (RemoteException this$1)
      {
        close();
      }
    }
    
    public void binderDied()
    {
      close();
    }
    
    abstract void close();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/MidiDeviceServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */