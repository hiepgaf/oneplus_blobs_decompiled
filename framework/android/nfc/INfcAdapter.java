package android.nfc;

import android.app.PendingIntent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.nxp.nfc.INxpNfcAdapter;
import com.nxp.nfc.INxpNfcAdapter.Stub;

public abstract interface INfcAdapter
  extends IInterface
{
  public abstract void addNfcUnlockHandler(INfcUnlockHandler paramINfcUnlockHandler, int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract boolean disable(boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean disableNdefPush()
    throws RemoteException;
  
  public abstract void dispatch(Tag paramTag)
    throws RemoteException;
  
  public abstract boolean enable()
    throws RemoteException;
  
  public abstract boolean enableNdefPush()
    throws RemoteException;
  
  public abstract String getCplc()
    throws RemoteException;
  
  public abstract String getDieId()
    throws RemoteException;
  
  public abstract INfcAdapterExtras getNfcAdapterExtrasInterface(String paramString)
    throws RemoteException;
  
  public abstract INfcCardEmulation getNfcCardEmulationInterface()
    throws RemoteException;
  
  public abstract INfcFCardEmulation getNfcFCardEmulationInterface()
    throws RemoteException;
  
  public abstract INfcTag getNfcTagInterface()
    throws RemoteException;
  
  public abstract INxpNfcAdapter getNxpNfcAdapterInterface()
    throws RemoteException;
  
  public abstract int getState()
    throws RemoteException;
  
  public abstract boolean ignore(int paramInt1, int paramInt2, ITagRemovedCallback paramITagRemovedCallback)
    throws RemoteException;
  
  public abstract void invokeBeam()
    throws RemoteException;
  
  public abstract void invokeBeamInternal(BeamShareData paramBeamShareData)
    throws RemoteException;
  
  public abstract boolean isNdefPushEnabled()
    throws RemoteException;
  
  public abstract void pausePolling(int paramInt)
    throws RemoteException;
  
  public abstract void removeNfcUnlockHandler(INfcUnlockHandler paramINfcUnlockHandler)
    throws RemoteException;
  
  public abstract void resumePolling()
    throws RemoteException;
  
  public abstract void setAidRoute(int paramInt)
    throws RemoteException;
  
  public abstract void setAppCallback(IAppCallback paramIAppCallback)
    throws RemoteException;
  
  public abstract void setForegroundDispatch(PendingIntent paramPendingIntent, IntentFilter[] paramArrayOfIntentFilter, TechListParcel paramTechListParcel)
    throws RemoteException;
  
  public abstract void setP2pModes(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setReaderMode(IBinder paramIBinder, IAppCallback paramIAppCallback, int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void verifyNfcPermission()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INfcAdapter
  {
    private static final String DESCRIPTOR = "android.nfc.INfcAdapter";
    static final int TRANSACTION_addNfcUnlockHandler = 23;
    static final int TRANSACTION_disable = 7;
    static final int TRANSACTION_disableNdefPush = 10;
    static final int TRANSACTION_dispatch = 20;
    static final int TRANSACTION_enable = 8;
    static final int TRANSACTION_enableNdefPush = 9;
    static final int TRANSACTION_getCplc = 26;
    static final int TRANSACTION_getDieId = 27;
    static final int TRANSACTION_getNfcAdapterExtrasInterface = 4;
    static final int TRANSACTION_getNfcCardEmulationInterface = 2;
    static final int TRANSACTION_getNfcFCardEmulationInterface = 3;
    static final int TRANSACTION_getNfcTagInterface = 1;
    static final int TRANSACTION_getNxpNfcAdapterInterface = 5;
    static final int TRANSACTION_getState = 6;
    static final int TRANSACTION_ignore = 19;
    static final int TRANSACTION_invokeBeam = 17;
    static final int TRANSACTION_invokeBeamInternal = 18;
    static final int TRANSACTION_isNdefPushEnabled = 11;
    static final int TRANSACTION_pausePolling = 12;
    static final int TRANSACTION_removeNfcUnlockHandler = 24;
    static final int TRANSACTION_resumePolling = 13;
    static final int TRANSACTION_setAidRoute = 25;
    static final int TRANSACTION_setAppCallback = 16;
    static final int TRANSACTION_setForegroundDispatch = 15;
    static final int TRANSACTION_setP2pModes = 22;
    static final int TRANSACTION_setReaderMode = 21;
    static final int TRANSACTION_verifyNfcPermission = 14;
    
    public Stub()
    {
      attachInterface(this, "android.nfc.INfcAdapter");
    }
    
    public static INfcAdapter asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.nfc.INfcAdapter");
      if ((localIInterface != null) && ((localIInterface instanceof INfcAdapter))) {
        return (INfcAdapter)localIInterface;
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
      boolean bool;
      label518:
      Object localObject1;
      Object localObject2;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.nfc.INfcAdapter");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        paramParcel1 = getNfcTagInterface();
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        paramParcel1 = getNfcCardEmulationInterface();
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        paramParcel1 = getNfcFCardEmulationInterface();
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        paramParcel1 = getNfcAdapterExtrasInterface(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        paramParcel1 = getNxpNfcAdapterInterface();
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        paramInt1 = getState();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = disable(bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label518;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 8: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        bool = enable();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        bool = enableNdefPush();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        bool = disableNdefPush();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        bool = isNdefPushEnabled();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        pausePolling(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        resumePolling();
        paramParcel2.writeNoException();
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        verifyNfcPermission();
        paramParcel2.writeNoException();
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);
          localObject2 = (IntentFilter[])paramParcel1.createTypedArray(IntentFilter.CREATOR);
          if (paramParcel1.readInt() == 0) {
            break label795;
          }
        }
        for (paramParcel1 = (TechListParcel)TechListParcel.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setForegroundDispatch((PendingIntent)localObject1, (IntentFilter[])localObject2, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 16: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        setAppCallback(IAppCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        invokeBeam();
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BeamShareData)BeamShareData.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          invokeBeamInternal(paramParcel1);
          return true;
        }
      case 19: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        bool = ignore(paramParcel1.readInt(), paramParcel1.readInt(), ITagRemovedCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 20: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Tag)Tag.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          dispatch(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 21: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        localObject1 = paramParcel1.readStrongBinder();
        localObject2 = IAppCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setReaderMode((IBinder)localObject1, (IAppCallback)localObject2, paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 22: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        setP2pModes(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 23: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        addNfcUnlockHandler(INfcUnlockHandler.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        return true;
      case 24: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        removeNfcUnlockHandler(INfcUnlockHandler.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 25: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        setAidRoute(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 26: 
        label795:
        paramParcel1.enforceInterface("android.nfc.INfcAdapter");
        paramParcel1 = getCplc();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("android.nfc.INfcAdapter");
      paramParcel1 = getDieId();
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      return true;
    }
    
    private static class Proxy
      implements INfcAdapter
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addNfcUnlockHandler(INfcUnlockHandler paramINfcUnlockHandler, int[] paramArrayOfInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          if (paramINfcUnlockHandler != null) {
            localIBinder = paramINfcUnlockHandler.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeIntArray(paramArrayOfInt);
          this.mRemote.transact(23, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      /* Error */
      public boolean disable(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: iload_1
        //   18: ifeq +5 -> 23
        //   21: iconst_1
        //   22: istore_2
        //   23: aload_3
        //   24: iload_2
        //   25: invokevirtual 68	android/os/Parcel:writeInt	(I)V
        //   28: aload_0
        //   29: getfield 19	android/nfc/INfcAdapter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   32: bipush 7
        //   34: aload_3
        //   35: aload 4
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload 4
        //   46: invokevirtual 58	android/os/Parcel:readException	()V
        //   49: aload 4
        //   51: invokevirtual 72	android/os/Parcel:readInt	()I
        //   54: istore_2
        //   55: iload_2
        //   56: ifeq +16 -> 72
        //   59: iconst_1
        //   60: istore_1
        //   61: aload 4
        //   63: invokevirtual 61	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 61	android/os/Parcel:recycle	()V
        //   70: iload_1
        //   71: ireturn
        //   72: iconst_0
        //   73: istore_1
        //   74: goto -13 -> 61
        //   77: astore 5
        //   79: aload 4
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 61	android/os/Parcel:recycle	()V
        //   88: aload 5
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramBoolean	boolean
        //   1	55	2	i	int
        //   5	80	3	localParcel1	Parcel
        //   9	71	4	localParcel2	Parcel
        //   77	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   11	17	77	finally
        //   23	55	77	finally
      }
      
      /* Error */
      public boolean disableNdefPush()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/nfc/INfcAdapter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 10
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 55 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 58	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 72	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 61	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public void dispatch(Tag paramTag)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 68	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 82	android/nfc/Tag:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/nfc/INfcAdapter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 20
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 61	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 61	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 68	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 61	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramTag	Tag
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public boolean enable()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/nfc/INfcAdapter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 8
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 55 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 58	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 72	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 61	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean enableNdefPush()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/nfc/INfcAdapter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 9
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 55 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 58	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 72	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 61	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      public String getCplc()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          this.mRemote.transact(26, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getDieId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          this.mRemote.transact(27, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.nfc.INfcAdapter";
      }
      
      public INfcAdapterExtras getNfcAdapterExtrasInterface(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          localParcel1.writeString(paramString);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = INfcAdapterExtras.Stub.asInterface(localParcel2.readStrongBinder());
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public INfcCardEmulation getNfcCardEmulationInterface()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          INfcCardEmulation localINfcCardEmulation = INfcCardEmulation.Stub.asInterface(localParcel2.readStrongBinder());
          return localINfcCardEmulation;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public INfcFCardEmulation getNfcFCardEmulationInterface()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          INfcFCardEmulation localINfcFCardEmulation = INfcFCardEmulation.Stub.asInterface(localParcel2.readStrongBinder());
          return localINfcFCardEmulation;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public INfcTag getNfcTagInterface()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          INfcTag localINfcTag = INfcTag.Stub.asInterface(localParcel2.readStrongBinder());
          return localINfcTag;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public INxpNfcAdapter getNxpNfcAdapterInterface()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          INxpNfcAdapter localINxpNfcAdapter = INxpNfcAdapter.Stub.asInterface(localParcel2.readStrongBinder());
          return localINxpNfcAdapter;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean ignore(int paramInt1, int paramInt2, ITagRemovedCallback paramITagRemovedCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 5
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 7
        //   13: aload 6
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 6
        //   22: iload_1
        //   23: invokevirtual 68	android/os/Parcel:writeInt	(I)V
        //   26: aload 6
        //   28: iload_2
        //   29: invokevirtual 68	android/os/Parcel:writeInt	(I)V
        //   32: aload_3
        //   33: ifnull +11 -> 44
        //   36: aload_3
        //   37: invokeinterface 139 1 0
        //   42: astore 5
        //   44: aload 6
        //   46: aload 5
        //   48: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   51: aload_0
        //   52: getfield 19	android/nfc/INfcAdapter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   55: bipush 19
        //   57: aload 6
        //   59: aload 7
        //   61: iconst_0
        //   62: invokeinterface 55 5 0
        //   67: pop
        //   68: aload 7
        //   70: invokevirtual 58	android/os/Parcel:readException	()V
        //   73: aload 7
        //   75: invokevirtual 72	android/os/Parcel:readInt	()I
        //   78: istore_1
        //   79: iload_1
        //   80: ifeq +19 -> 99
        //   83: iconst_1
        //   84: istore 4
        //   86: aload 7
        //   88: invokevirtual 61	android/os/Parcel:recycle	()V
        //   91: aload 6
        //   93: invokevirtual 61	android/os/Parcel:recycle	()V
        //   96: iload 4
        //   98: ireturn
        //   99: iconst_0
        //   100: istore 4
        //   102: goto -16 -> 86
        //   105: astore_3
        //   106: aload 7
        //   108: invokevirtual 61	android/os/Parcel:recycle	()V
        //   111: aload 6
        //   113: invokevirtual 61	android/os/Parcel:recycle	()V
        //   116: aload_3
        //   117: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	118	0	this	Proxy
        //   0	118	1	paramInt1	int
        //   0	118	2	paramInt2	int
        //   0	118	3	paramITagRemovedCallback	ITagRemovedCallback
        //   84	17	4	bool	boolean
        //   1	46	5	localIBinder	IBinder
        //   6	106	6	localParcel1	Parcel
        //   11	96	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	32	105	finally
        //   36	44	105	finally
        //   44	79	105	finally
      }
      
      public void invokeBeam()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.nfc.INfcAdapter");
          this.mRemote.transact(17, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void invokeBeamInternal(BeamShareData paramBeamShareData)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 32
        //   7: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 68	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 145	android/nfc/BeamShareData:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/nfc/INfcAdapter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 18
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 55 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 61	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 68	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 61	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramBeamShareData	BeamShareData
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      /* Error */
      public boolean isNdefPushEnabled()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/nfc/INfcAdapter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 11
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 55 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 58	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 72	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 61	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      public void pausePolling(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removeNfcUnlockHandler(INfcUnlockHandler paramINfcUnlockHandler)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          if (paramINfcUnlockHandler != null) {
            localIBinder = paramINfcUnlockHandler.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(24, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void resumePolling()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setAidRoute(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(25, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setAppCallback(IAppCallback paramIAppCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          if (paramIAppCallback != null) {
            localIBinder = paramIAppCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setForegroundDispatch(PendingIntent paramPendingIntent, IntentFilter[] paramArrayOfIntentFilter, TechListParcel paramTechListParcel)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
            if (paramPendingIntent != null)
            {
              localParcel1.writeInt(1);
              paramPendingIntent.writeToParcel(localParcel1, 0);
              localParcel1.writeTypedArray(paramArrayOfIntentFilter, 0);
              if (paramTechListParcel != null)
              {
                localParcel1.writeInt(1);
                paramTechListParcel.writeToParcel(localParcel1, 0);
                this.mRemote.transact(15, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setP2pModes(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
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
      public void setReaderMode(IBinder paramIBinder, IAppCallback paramIAppCallback, int paramInt, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 5
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 7
        //   13: aload 6
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 6
        //   22: aload_1
        //   23: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   26: aload 5
        //   28: astore_1
        //   29: aload_2
        //   30: ifnull +10 -> 40
        //   33: aload_2
        //   34: invokeinterface 156 1 0
        //   39: astore_1
        //   40: aload 6
        //   42: aload_1
        //   43: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   46: aload 6
        //   48: iload_3
        //   49: invokevirtual 68	android/os/Parcel:writeInt	(I)V
        //   52: aload 4
        //   54: ifnull +50 -> 104
        //   57: aload 6
        //   59: iconst_1
        //   60: invokevirtual 68	android/os/Parcel:writeInt	(I)V
        //   63: aload 4
        //   65: aload 6
        //   67: iconst_0
        //   68: invokevirtual 175	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   71: aload_0
        //   72: getfield 19	android/nfc/INfcAdapter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   75: bipush 21
        //   77: aload 6
        //   79: aload 7
        //   81: iconst_0
        //   82: invokeinterface 55 5 0
        //   87: pop
        //   88: aload 7
        //   90: invokevirtual 58	android/os/Parcel:readException	()V
        //   93: aload 7
        //   95: invokevirtual 61	android/os/Parcel:recycle	()V
        //   98: aload 6
        //   100: invokevirtual 61	android/os/Parcel:recycle	()V
        //   103: return
        //   104: aload 6
        //   106: iconst_0
        //   107: invokevirtual 68	android/os/Parcel:writeInt	(I)V
        //   110: goto -39 -> 71
        //   113: astore_1
        //   114: aload 7
        //   116: invokevirtual 61	android/os/Parcel:recycle	()V
        //   119: aload 6
        //   121: invokevirtual 61	android/os/Parcel:recycle	()V
        //   124: aload_1
        //   125: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	126	0	this	Proxy
        //   0	126	1	paramIBinder	IBinder
        //   0	126	2	paramIAppCallback	IAppCallback
        //   0	126	3	paramInt	int
        //   0	126	4	paramBundle	Bundle
        //   1	26	5	localObject	Object
        //   6	114	6	localParcel1	Parcel
        //   11	104	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	113	finally
        //   33	40	113	finally
        //   40	52	113	finally
        //   57	71	113	finally
        //   71	93	113	finally
        //   104	110	113	finally
      }
      
      public void verifyNfcPermission()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapter");
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/INfcAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */