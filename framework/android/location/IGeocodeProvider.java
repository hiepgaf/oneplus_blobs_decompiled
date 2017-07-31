package android.location;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IGeocodeProvider
  extends IInterface
{
  public abstract String getFromLocation(double paramDouble1, double paramDouble2, int paramInt, GeocoderParams paramGeocoderParams, List<Address> paramList)
    throws RemoteException;
  
  public abstract String getFromLocationName(String paramString, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt, GeocoderParams paramGeocoderParams, List<Address> paramList)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGeocodeProvider
  {
    private static final String DESCRIPTOR = "android.location.IGeocodeProvider";
    static final int TRANSACTION_getFromLocation = 1;
    static final int TRANSACTION_getFromLocationName = 2;
    
    public Stub()
    {
      attachInterface(this, "android.location.IGeocodeProvider");
    }
    
    public static IGeocodeProvider asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.location.IGeocodeProvider");
      if ((localIInterface != null) && ((localIInterface instanceof IGeocodeProvider))) {
        return (IGeocodeProvider)localIInterface;
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
        paramParcel2.writeString("android.location.IGeocodeProvider");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.location.IGeocodeProvider");
        d1 = paramParcel1.readDouble();
        d2 = paramParcel1.readDouble();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (GeocoderParams)GeocoderParams.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          localObject = new ArrayList();
          paramParcel1 = getFromLocation(d1, d2, paramInt1, paramParcel1, (List)localObject);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          paramParcel2.writeTypedList((List)localObject);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.location.IGeocodeProvider");
      Object localObject = paramParcel1.readString();
      double d1 = paramParcel1.readDouble();
      double d2 = paramParcel1.readDouble();
      double d3 = paramParcel1.readDouble();
      double d4 = paramParcel1.readDouble();
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (GeocoderParams)GeocoderParams.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        ArrayList localArrayList = new ArrayList();
        paramParcel1 = getFromLocationName((String)localObject, d1, d2, d3, d4, paramInt1, paramParcel1, localArrayList);
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        paramParcel2.writeTypedList(localArrayList);
        return true;
      }
    }
    
    private static class Proxy
      implements IGeocodeProvider
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
      
      /* Error */
      public String getFromLocation(double paramDouble1, double paramDouble2, int paramInt, GeocoderParams paramGeocoderParams, List<Address> paramList)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 8
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 9
        //   10: aload 8
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 8
        //   19: dload_1
        //   20: invokevirtual 42	android/os/Parcel:writeDouble	(D)V
        //   23: aload 8
        //   25: dload_3
        //   26: invokevirtual 42	android/os/Parcel:writeDouble	(D)V
        //   29: aload 8
        //   31: iload 5
        //   33: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   36: aload 6
        //   38: ifnull +68 -> 106
        //   41: aload 8
        //   43: iconst_1
        //   44: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   47: aload 6
        //   49: aload 8
        //   51: iconst_0
        //   52: invokevirtual 52	android/location/GeocoderParams:writeToParcel	(Landroid/os/Parcel;I)V
        //   55: aload_0
        //   56: getfield 19	android/location/IGeocodeProvider$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   59: iconst_1
        //   60: aload 8
        //   62: aload 9
        //   64: iconst_0
        //   65: invokeinterface 58 5 0
        //   70: pop
        //   71: aload 9
        //   73: invokevirtual 61	android/os/Parcel:readException	()V
        //   76: aload 9
        //   78: invokevirtual 65	android/os/Parcel:readString	()Ljava/lang/String;
        //   81: astore 6
        //   83: aload 9
        //   85: aload 7
        //   87: getstatic 71	android/location/Address:CREATOR	Landroid/os/Parcelable$Creator;
        //   90: invokevirtual 75	android/os/Parcel:readTypedList	(Ljava/util/List;Landroid/os/Parcelable$Creator;)V
        //   93: aload 9
        //   95: invokevirtual 78	android/os/Parcel:recycle	()V
        //   98: aload 8
        //   100: invokevirtual 78	android/os/Parcel:recycle	()V
        //   103: aload 6
        //   105: areturn
        //   106: aload 8
        //   108: iconst_0
        //   109: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   112: goto -57 -> 55
        //   115: astore 6
        //   117: aload 9
        //   119: invokevirtual 78	android/os/Parcel:recycle	()V
        //   122: aload 8
        //   124: invokevirtual 78	android/os/Parcel:recycle	()V
        //   127: aload 6
        //   129: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	130	0	this	Proxy
        //   0	130	1	paramDouble1	double
        //   0	130	3	paramDouble2	double
        //   0	130	5	paramInt	int
        //   0	130	6	paramGeocoderParams	GeocoderParams
        //   0	130	7	paramList	List<Address>
        //   3	120	8	localParcel1	Parcel
        //   8	110	9	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	36	115	finally
        //   41	55	115	finally
        //   55	93	115	finally
        //   106	112	115	finally
      }
      
      /* Error */
      public String getFromLocationName(String paramString, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt, GeocoderParams paramGeocoderParams, List<Address> paramList)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 13
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 14
        //   10: aload 13
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 13
        //   19: aload_1
        //   20: invokevirtual 86	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 13
        //   25: dload_2
        //   26: invokevirtual 42	android/os/Parcel:writeDouble	(D)V
        //   29: aload 13
        //   31: dload 4
        //   33: invokevirtual 42	android/os/Parcel:writeDouble	(D)V
        //   36: aload 13
        //   38: dload 6
        //   40: invokevirtual 42	android/os/Parcel:writeDouble	(D)V
        //   43: aload 13
        //   45: dload 8
        //   47: invokevirtual 42	android/os/Parcel:writeDouble	(D)V
        //   50: aload 13
        //   52: iload 10
        //   54: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   57: aload 11
        //   59: ifnull +66 -> 125
        //   62: aload 13
        //   64: iconst_1
        //   65: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   68: aload 11
        //   70: aload 13
        //   72: iconst_0
        //   73: invokevirtual 52	android/location/GeocoderParams:writeToParcel	(Landroid/os/Parcel;I)V
        //   76: aload_0
        //   77: getfield 19	android/location/IGeocodeProvider$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   80: iconst_2
        //   81: aload 13
        //   83: aload 14
        //   85: iconst_0
        //   86: invokeinterface 58 5 0
        //   91: pop
        //   92: aload 14
        //   94: invokevirtual 61	android/os/Parcel:readException	()V
        //   97: aload 14
        //   99: invokevirtual 65	android/os/Parcel:readString	()Ljava/lang/String;
        //   102: astore_1
        //   103: aload 14
        //   105: aload 12
        //   107: getstatic 71	android/location/Address:CREATOR	Landroid/os/Parcelable$Creator;
        //   110: invokevirtual 75	android/os/Parcel:readTypedList	(Ljava/util/List;Landroid/os/Parcelable$Creator;)V
        //   113: aload 14
        //   115: invokevirtual 78	android/os/Parcel:recycle	()V
        //   118: aload 13
        //   120: invokevirtual 78	android/os/Parcel:recycle	()V
        //   123: aload_1
        //   124: areturn
        //   125: aload 13
        //   127: iconst_0
        //   128: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   131: goto -55 -> 76
        //   134: astore_1
        //   135: aload 14
        //   137: invokevirtual 78	android/os/Parcel:recycle	()V
        //   140: aload 13
        //   142: invokevirtual 78	android/os/Parcel:recycle	()V
        //   145: aload_1
        //   146: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	147	0	this	Proxy
        //   0	147	1	paramString	String
        //   0	147	2	paramDouble1	double
        //   0	147	4	paramDouble2	double
        //   0	147	6	paramDouble3	double
        //   0	147	8	paramDouble4	double
        //   0	147	10	paramInt	int
        //   0	147	11	paramGeocoderParams	GeocoderParams
        //   0	147	12	paramList	List<Address>
        //   3	138	13	localParcel1	Parcel
        //   8	128	14	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	134	finally
        //   62	76	134	finally
        //   76	113	134	finally
        //   125	131	134	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.location.IGeocodeProvider";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/IGeocodeProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */