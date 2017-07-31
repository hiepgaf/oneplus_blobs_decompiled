package android.media.midi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IMidiDeviceServer
  extends IInterface
{
  public abstract void closeDevice()
    throws RemoteException;
  
  public abstract void closePort(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract int connectPorts(IBinder paramIBinder, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt)
    throws RemoteException;
  
  public abstract MidiDeviceInfo getDeviceInfo()
    throws RemoteException;
  
  public abstract ParcelFileDescriptor openInputPort(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor openOutputPort(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void setDeviceInfo(MidiDeviceInfo paramMidiDeviceInfo)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMidiDeviceServer
  {
    private static final String DESCRIPTOR = "android.media.midi.IMidiDeviceServer";
    static final int TRANSACTION_closeDevice = 4;
    static final int TRANSACTION_closePort = 3;
    static final int TRANSACTION_connectPorts = 5;
    static final int TRANSACTION_getDeviceInfo = 6;
    static final int TRANSACTION_openInputPort = 1;
    static final int TRANSACTION_openOutputPort = 2;
    static final int TRANSACTION_setDeviceInfo = 7;
    
    public Stub()
    {
      attachInterface(this, "android.media.midi.IMidiDeviceServer");
    }
    
    public static IMidiDeviceServer asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.midi.IMidiDeviceServer");
      if ((localIInterface != null) && ((localIInterface instanceof IMidiDeviceServer))) {
        return (IMidiDeviceServer)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.media.midi.IMidiDeviceServer");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.midi.IMidiDeviceServer");
        paramParcel1 = openInputPort(paramParcel1.readStrongBinder(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.media.midi.IMidiDeviceServer");
        paramParcel1 = openOutputPort(paramParcel1.readStrongBinder(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.media.midi.IMidiDeviceServer");
        closePort(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.media.midi.IMidiDeviceServer");
        closeDevice();
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.media.midi.IMidiDeviceServer");
        IBinder localIBinder = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (ParcelFileDescriptor localParcelFileDescriptor = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);; localParcelFileDescriptor = null)
        {
          paramInt1 = connectPorts(localIBinder, localParcelFileDescriptor, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.media.midi.IMidiDeviceServer");
        paramParcel1 = getDeviceInfo();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      }
      paramParcel1.enforceInterface("android.media.midi.IMidiDeviceServer");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (MidiDeviceInfo)MidiDeviceInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        setDeviceInfo(paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IMidiDeviceServer
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void closeDevice()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.midi.IMidiDeviceServer");
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void closePort(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.midi.IMidiDeviceServer");
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public int connectPorts(IBinder paramIBinder, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 33
        //   14: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 54	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload_2
        //   24: ifnull +61 -> 85
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 66	android/os/ParcelFileDescriptor:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/media/midi/IMidiDeviceServer$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: iconst_5
        //   51: aload 4
        //   53: aload 5
        //   55: iconst_0
        //   56: invokeinterface 43 5 0
        //   61: pop
        //   62: aload 5
        //   64: invokevirtual 46	android/os/Parcel:readException	()V
        //   67: aload 5
        //   69: invokevirtual 70	android/os/Parcel:readInt	()I
        //   72: istore_3
        //   73: aload 5
        //   75: invokevirtual 49	android/os/Parcel:recycle	()V
        //   78: aload 4
        //   80: invokevirtual 49	android/os/Parcel:recycle	()V
        //   83: iload_3
        //   84: ireturn
        //   85: aload 4
        //   87: iconst_0
        //   88: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   91: goto -51 -> 40
        //   94: astore_1
        //   95: aload 5
        //   97: invokevirtual 49	android/os/Parcel:recycle	()V
        //   100: aload 4
        //   102: invokevirtual 49	android/os/Parcel:recycle	()V
        //   105: aload_1
        //   106: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	107	0	this	Proxy
        //   0	107	1	paramIBinder	IBinder
        //   0	107	2	paramParcelFileDescriptor	ParcelFileDescriptor
        //   0	107	3	paramInt	int
        //   3	98	4	localParcel1	Parcel
        //   8	88	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	94	finally
        //   27	40	94	finally
        //   40	73	94	finally
        //   85	91	94	finally
      }
      
      /* Error */
      public MidiDeviceInfo getDeviceInfo()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/media/midi/IMidiDeviceServer$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 6
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 43 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 46	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 70	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 78	android/media/midi/MidiDeviceInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 84 2 0
        //   49: checkcast 74	android/media/midi/MidiDeviceInfo
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 49	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 49	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 49	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 49	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localMidiDeviceInfo	MidiDeviceInfo
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.media.midi.IMidiDeviceServer";
      }
      
      /* Error */
      public ParcelFileDescriptor openInputPort(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 54	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/media/midi/IMidiDeviceServer$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_1
        //   30: aload_3
        //   31: aload 4
        //   33: iconst_0
        //   34: invokeinterface 43 5 0
        //   39: pop
        //   40: aload 4
        //   42: invokevirtual 46	android/os/Parcel:readException	()V
        //   45: aload 4
        //   47: invokevirtual 70	android/os/Parcel:readInt	()I
        //   50: ifeq +28 -> 78
        //   53: getstatic 89	android/os/ParcelFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
        //   56: aload 4
        //   58: invokeinterface 84 2 0
        //   63: checkcast 62	android/os/ParcelFileDescriptor
        //   66: astore_1
        //   67: aload 4
        //   69: invokevirtual 49	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 49	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: areturn
        //   78: aconst_null
        //   79: astore_1
        //   80: goto -13 -> 67
        //   83: astore_1
        //   84: aload 4
        //   86: invokevirtual 49	android/os/Parcel:recycle	()V
        //   89: aload_3
        //   90: invokevirtual 49	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramIBinder	IBinder
        //   0	95	2	paramInt	int
        //   3	87	3	localParcel1	Parcel
        //   7	78	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	67	83	finally
      }
      
      /* Error */
      public ParcelFileDescriptor openOutputPort(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 54	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/media/midi/IMidiDeviceServer$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_2
        //   30: aload_3
        //   31: aload 4
        //   33: iconst_0
        //   34: invokeinterface 43 5 0
        //   39: pop
        //   40: aload 4
        //   42: invokevirtual 46	android/os/Parcel:readException	()V
        //   45: aload 4
        //   47: invokevirtual 70	android/os/Parcel:readInt	()I
        //   50: ifeq +28 -> 78
        //   53: getstatic 89	android/os/ParcelFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
        //   56: aload 4
        //   58: invokeinterface 84 2 0
        //   63: checkcast 62	android/os/ParcelFileDescriptor
        //   66: astore_1
        //   67: aload 4
        //   69: invokevirtual 49	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 49	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: areturn
        //   78: aconst_null
        //   79: astore_1
        //   80: goto -13 -> 67
        //   83: astore_1
        //   84: aload 4
        //   86: invokevirtual 49	android/os/Parcel:recycle	()V
        //   89: aload_3
        //   90: invokevirtual 49	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramIBinder	IBinder
        //   0	95	2	paramInt	int
        //   3	87	3	localParcel1	Parcel
        //   7	78	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	67	83	finally
      }
      
      /* Error */
      public void setDeviceInfo(MidiDeviceInfo paramMidiDeviceInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 93	android/media/midi/MidiDeviceInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/media/midi/IMidiDeviceServer$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 7
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 43 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 46	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 49	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 49	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 49	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 49	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramMidiDeviceInfo	MidiDeviceInfo
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/IMidiDeviceServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */