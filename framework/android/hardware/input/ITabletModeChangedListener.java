package android.hardware.input;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ITabletModeChangedListener
  extends IInterface
{
  public abstract void onTabletModeChanged(long paramLong, boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITabletModeChangedListener
  {
    private static final String DESCRIPTOR = "android.hardware.input.ITabletModeChangedListener";
    static final int TRANSACTION_onTabletModeChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.input.ITabletModeChangedListener");
    }
    
    public static ITabletModeChangedListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.input.ITabletModeChangedListener");
      if ((localIInterface != null) && ((localIInterface instanceof ITabletModeChangedListener))) {
        return (ITabletModeChangedListener)localIInterface;
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
      boolean bool = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.hardware.input.ITabletModeChangedListener");
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.input.ITabletModeChangedListener");
      long l = paramParcel1.readLong();
      if (paramParcel1.readInt() != 0) {
        bool = true;
      }
      onTabletModeChanged(l, bool);
      return true;
    }
    
    private static class Proxy
      implements ITabletModeChangedListener
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
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.input.ITabletModeChangedListener";
      }
      
      /* Error */
      public void onTabletModeChanged(long paramLong, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 4
        //   3: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload 5
        //   17: lload_1
        //   18: invokevirtual 44	android/os/Parcel:writeLong	(J)V
        //   21: iload_3
        //   22: ifeq +31 -> 53
        //   25: aload 5
        //   27: iload 4
        //   29: invokevirtual 48	android/os/Parcel:writeInt	(I)V
        //   32: aload_0
        //   33: getfield 19	android/hardware/input/ITabletModeChangedListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   36: iconst_1
        //   37: aload 5
        //   39: aconst_null
        //   40: iconst_1
        //   41: invokeinterface 54 5 0
        //   46: pop
        //   47: aload 5
        //   49: invokevirtual 57	android/os/Parcel:recycle	()V
        //   52: return
        //   53: iconst_0
        //   54: istore 4
        //   56: goto -31 -> 25
        //   59: astore 6
        //   61: aload 5
        //   63: invokevirtual 57	android/os/Parcel:recycle	()V
        //   66: aload 6
        //   68: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	69	0	this	Proxy
        //   0	69	1	paramLong	long
        //   0	69	3	paramBoolean	boolean
        //   1	54	4	i	int
        //   6	56	5	localParcel	Parcel
        //   59	8	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   8	21	59	finally
        //   25	47	59	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/input/ITabletModeChangedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */