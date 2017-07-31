package android.location;

import android.app.PendingIntent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.android.internal.location.ProviderProperties;
import java.util.ArrayList;
import java.util.List;

public abstract interface ILocationManager
  extends IInterface
{
  public abstract boolean addGnssMeasurementsListener(IGnssMeasurementsListener paramIGnssMeasurementsListener, String paramString)
    throws RemoteException;
  
  public abstract boolean addGnssNavigationMessageListener(IGnssNavigationMessageListener paramIGnssNavigationMessageListener, String paramString)
    throws RemoteException;
  
  public abstract void addTestProvider(String paramString1, ProviderProperties paramProviderProperties, String paramString2)
    throws RemoteException;
  
  public abstract void clearAllPendingBroadcastsLocked()
    throws RemoteException;
  
  public abstract void clearTestProviderEnabled(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void clearTestProviderLocation(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void clearTestProviderStatus(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract boolean geocoderIsPresent()
    throws RemoteException;
  
  public abstract List<String> getAllProviders()
    throws RemoteException;
  
  public abstract String getBestProvider(Criteria paramCriteria, boolean paramBoolean)
    throws RemoteException;
  
  public abstract List<String> getCurrentProviderPackageList(String paramString)
    throws RemoteException;
  
  public abstract String getFromLocation(double paramDouble1, double paramDouble2, int paramInt, GeocoderParams paramGeocoderParams, List<Address> paramList)
    throws RemoteException;
  
  public abstract String getFromLocationName(String paramString, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt, GeocoderParams paramGeocoderParams, List<Address> paramList)
    throws RemoteException;
  
  public abstract int getGnssYearOfHardware()
    throws RemoteException;
  
  public abstract Location getLastKnownLocation()
    throws RemoteException;
  
  public abstract Location getLastLocation(LocationRequest paramLocationRequest, String paramString)
    throws RemoteException;
  
  public abstract String getNetworkProviderPackage()
    throws RemoteException;
  
  public abstract ProviderProperties getProviderProperties(String paramString)
    throws RemoteException;
  
  public abstract List<String> getProviders(Criteria paramCriteria, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean isProviderEnabled(String paramString)
    throws RemoteException;
  
  public abstract void locationCallbackFinished(ILocationListener paramILocationListener)
    throws RemoteException;
  
  public abstract boolean providerMeetsCriteria(String paramString, Criteria paramCriteria)
    throws RemoteException;
  
  public abstract boolean registerGnssStatusCallback(IGnssStatusListener paramIGnssStatusListener, String paramString)
    throws RemoteException;
  
  public abstract void removeGeofence(Geofence paramGeofence, PendingIntent paramPendingIntent, String paramString)
    throws RemoteException;
  
  public abstract void removeGnssMeasurementsListener(IGnssMeasurementsListener paramIGnssMeasurementsListener)
    throws RemoteException;
  
  public abstract void removeGnssNavigationMessageListener(IGnssNavigationMessageListener paramIGnssNavigationMessageListener)
    throws RemoteException;
  
  public abstract void removeTestProvider(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void removeUpdates(ILocationListener paramILocationListener, PendingIntent paramPendingIntent, String paramString)
    throws RemoteException;
  
  public abstract void reportLocation(Location paramLocation, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void requestGeofence(LocationRequest paramLocationRequest, Geofence paramGeofence, PendingIntent paramPendingIntent, String paramString)
    throws RemoteException;
  
  public abstract void requestLocationUpdates(LocationRequest paramLocationRequest, ILocationListener paramILocationListener, PendingIntent paramPendingIntent, String paramString)
    throws RemoteException;
  
  public abstract boolean sendExtraCommand(String paramString1, String paramString2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract boolean sendNiResponse(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setTestProviderEnabled(String paramString1, boolean paramBoolean, String paramString2)
    throws RemoteException;
  
  public abstract void setTestProviderLocation(String paramString1, Location paramLocation, String paramString2)
    throws RemoteException;
  
  public abstract void setTestProviderStatus(String paramString1, int paramInt, Bundle paramBundle, long paramLong, String paramString2)
    throws RemoteException;
  
  public abstract void unregisterGnssStatusCallback(IGnssStatusListener paramIGnssStatusListener)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ILocationManager
  {
    private static final String DESCRIPTOR = "android.location.ILocationManager";
    static final int TRANSACTION_addGnssMeasurementsListener = 12;
    static final int TRANSACTION_addGnssNavigationMessageListener = 14;
    static final int TRANSACTION_addTestProvider = 24;
    static final int TRANSACTION_clearAllPendingBroadcastsLocked = 35;
    static final int TRANSACTION_clearTestProviderEnabled = 29;
    static final int TRANSACTION_clearTestProviderLocation = 27;
    static final int TRANSACTION_clearTestProviderStatus = 31;
    static final int TRANSACTION_geocoderIsPresent = 8;
    static final int TRANSACTION_getAllProviders = 17;
    static final int TRANSACTION_getBestProvider = 19;
    static final int TRANSACTION_getCurrentProviderPackageList = 37;
    static final int TRANSACTION_getFromLocation = 9;
    static final int TRANSACTION_getFromLocationName = 10;
    static final int TRANSACTION_getGnssYearOfHardware = 16;
    static final int TRANSACTION_getLastKnownLocation = 36;
    static final int TRANSACTION_getLastLocation = 5;
    static final int TRANSACTION_getNetworkProviderPackage = 22;
    static final int TRANSACTION_getProviderProperties = 21;
    static final int TRANSACTION_getProviders = 18;
    static final int TRANSACTION_isProviderEnabled = 23;
    static final int TRANSACTION_locationCallbackFinished = 34;
    static final int TRANSACTION_providerMeetsCriteria = 20;
    static final int TRANSACTION_registerGnssStatusCallback = 6;
    static final int TRANSACTION_removeGeofence = 4;
    static final int TRANSACTION_removeGnssMeasurementsListener = 13;
    static final int TRANSACTION_removeGnssNavigationMessageListener = 15;
    static final int TRANSACTION_removeTestProvider = 25;
    static final int TRANSACTION_removeUpdates = 2;
    static final int TRANSACTION_reportLocation = 33;
    static final int TRANSACTION_requestGeofence = 3;
    static final int TRANSACTION_requestLocationUpdates = 1;
    static final int TRANSACTION_sendExtraCommand = 32;
    static final int TRANSACTION_sendNiResponse = 11;
    static final int TRANSACTION_setTestProviderEnabled = 28;
    static final int TRANSACTION_setTestProviderLocation = 26;
    static final int TRANSACTION_setTestProviderStatus = 30;
    static final int TRANSACTION_unregisterGnssStatusCallback = 7;
    
    public Stub()
    {
      attachInterface(this, "android.location.ILocationManager");
    }
    
    public static ILocationManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.location.ILocationManager");
      if ((localIInterface != null) && ((localIInterface instanceof ILocationManager))) {
        return (ILocationManager)localIInterface;
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
      Object localObject1;
      Object localObject3;
      Object localObject2;
      label417:
      label531:
      label578:
      label584:
      label662:
      label733:
      boolean bool;
      double d1;
      double d2;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.location.ILocationManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (LocationRequest)LocationRequest.CREATOR.createFromParcel(paramParcel1);
          localObject3 = ILocationListener.Stub.asInterface(paramParcel1.readStrongBinder());
          if (paramParcel1.readInt() == 0) {
            break label417;
          }
        }
        for (localObject2 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          requestLocationUpdates((LocationRequest)localObject1, (ILocationListener)localObject3, (PendingIntent)localObject2, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        localObject2 = ILocationListener.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          removeUpdates((ILocationListener)localObject2, (PendingIntent)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (LocationRequest)LocationRequest.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label578;
          }
          localObject2 = (Geofence)Geofence.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label584;
          }
        }
        for (localObject3 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);; localObject3 = null)
        {
          requestGeofence((LocationRequest)localObject1, (Geofence)localObject2, (PendingIntent)localObject3, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label531;
        }
      case 4: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Geofence)Geofence.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label662;
          }
        }
        for (localObject2 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          removeGeofence((Geofence)localObject1, (PendingIntent)localObject2, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 5: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (LocationRequest)LocationRequest.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getLastLocation((LocationRequest)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label733;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 6: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        bool = registerGnssStatusCallback(IGnssStatusListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        unregisterGnssStatusCallback(IGnssStatusListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        bool = geocoderIsPresent();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        d1 = paramParcel1.readDouble();
        d2 = paramParcel1.readDouble();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (GeocoderParams)GeocoderParams.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          localObject1 = new ArrayList();
          paramParcel1 = getFromLocation(d1, d2, paramInt1, paramParcel1, (List)localObject1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          paramParcel2.writeTypedList((List)localObject1);
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        localObject1 = paramParcel1.readString();
        d1 = paramParcel1.readDouble();
        d2 = paramParcel1.readDouble();
        double d3 = paramParcel1.readDouble();
        double d4 = paramParcel1.readDouble();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (GeocoderParams)GeocoderParams.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          localObject2 = new ArrayList();
          paramParcel1 = getFromLocationName((String)localObject1, d1, d2, d3, d4, paramInt1, paramParcel1, (List)localObject2);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          paramParcel2.writeTypedList((List)localObject2);
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        bool = sendNiResponse(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        bool = addGnssMeasurementsListener(IGnssMeasurementsListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        removeGnssMeasurementsListener(IGnssMeasurementsListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        bool = addGnssNavigationMessageListener(IGnssNavigationMessageListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        removeGnssNavigationMessageListener(IGnssNavigationMessageListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        paramInt1 = getGnssYearOfHardware();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        paramParcel1 = getAllProviders();
        paramParcel2.writeNoException();
        paramParcel2.writeStringList(paramParcel1);
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Criteria)Criteria.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1331;
          }
        }
        for (bool = true;; bool = false)
        {
          paramParcel1 = getProviders((Criteria)localObject1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeStringList(paramParcel1);
          return true;
          localObject1 = null;
          break;
        }
      case 19: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Criteria)Criteria.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1400;
          }
        }
        for (bool = true;; bool = false)
        {
          paramParcel1 = getBestProvider((Criteria)localObject1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
          localObject1 = null;
          break;
        }
      case 20: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Criteria)Criteria.CREATOR.createFromParcel(paramParcel1);
          bool = providerMeetsCriteria((String)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1470;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 21: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        paramParcel1 = getProviderProperties(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 22: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        paramParcel1 = getNetworkProviderPackage();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 23: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        bool = isProviderEnabled(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 24: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        localObject2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ProviderProperties)ProviderProperties.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          addTestProvider((String)localObject2, (ProviderProperties)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 25: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        removeTestProvider(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 26: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        localObject2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Location)Location.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setTestProviderLocation((String)localObject2, (Location)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 27: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        clearTestProviderLocation(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 28: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setTestProviderEnabled((String)localObject1, bool, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 29: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        clearTestProviderEnabled(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 30: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        localObject2 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setTestProviderStatus((String)localObject2, paramInt1, (Bundle)localObject1, paramParcel1.readLong(), paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 31: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        clearTestProviderStatus(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 32: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        localObject1 = paramParcel1.readString();
        localObject2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          bool = sendExtraCommand((String)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1990;
          }
          paramInt1 = 1;
          paramParcel2.writeInt(paramInt1);
          if (paramParcel1 == null) {
            break label1995;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramInt1 = 0;
          break label1963;
          paramParcel2.writeInt(0);
        }
      case 33: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Location)Location.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2060;
          }
        }
        for (bool = true;; bool = false)
        {
          reportLocation((Location)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 34: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        locationCallbackFinished(ILocationListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 35: 
        paramParcel1.enforceInterface("android.location.ILocationManager");
        clearAllPendingBroadcastsLocked();
        paramParcel2.writeNoException();
        return true;
      case 36: 
        label1331:
        label1400:
        label1470:
        label1963:
        label1990:
        label1995:
        label2060:
        paramParcel1.enforceInterface("android.location.ILocationManager");
        paramParcel1 = getLastKnownLocation();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      }
      paramParcel1.enforceInterface("android.location.ILocationManager");
      paramParcel1 = getCurrentProviderPackageList(paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeStringList(paramParcel1);
      return true;
    }
    
    private static class Proxy
      implements ILocationManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public boolean addGnssMeasurementsListener(IGnssMeasurementsListener paramIGnssMeasurementsListener, String paramString)
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
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 42 1 0
        //   30: astore 5
        //   32: aload 6
        //   34: aload 5
        //   36: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload 6
        //   41: aload_2
        //   42: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   45: aload_0
        //   46: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   49: bipush 12
        //   51: aload 6
        //   53: aload 7
        //   55: iconst_0
        //   56: invokeinterface 54 5 0
        //   61: pop
        //   62: aload 7
        //   64: invokevirtual 57	android/os/Parcel:readException	()V
        //   67: aload 7
        //   69: invokevirtual 61	android/os/Parcel:readInt	()I
        //   72: istore_3
        //   73: iload_3
        //   74: ifeq +19 -> 93
        //   77: iconst_1
        //   78: istore 4
        //   80: aload 7
        //   82: invokevirtual 64	android/os/Parcel:recycle	()V
        //   85: aload 6
        //   87: invokevirtual 64	android/os/Parcel:recycle	()V
        //   90: iload 4
        //   92: ireturn
        //   93: iconst_0
        //   94: istore 4
        //   96: goto -16 -> 80
        //   99: astore_1
        //   100: aload 7
        //   102: invokevirtual 64	android/os/Parcel:recycle	()V
        //   105: aload 6
        //   107: invokevirtual 64	android/os/Parcel:recycle	()V
        //   110: aload_1
        //   111: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	112	0	this	Proxy
        //   0	112	1	paramIGnssMeasurementsListener	IGnssMeasurementsListener
        //   0	112	2	paramString	String
        //   72	2	3	i	int
        //   78	17	4	bool	boolean
        //   1	34	5	localIBinder	IBinder
        //   6	100	6	localParcel1	Parcel
        //   11	90	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	99	finally
        //   24	32	99	finally
        //   32	73	99	finally
      }
      
      /* Error */
      public boolean addGnssNavigationMessageListener(IGnssNavigationMessageListener paramIGnssNavigationMessageListener, String paramString)
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
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 70 1 0
        //   30: astore 5
        //   32: aload 6
        //   34: aload 5
        //   36: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload 6
        //   41: aload_2
        //   42: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   45: aload_0
        //   46: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   49: bipush 14
        //   51: aload 6
        //   53: aload 7
        //   55: iconst_0
        //   56: invokeinterface 54 5 0
        //   61: pop
        //   62: aload 7
        //   64: invokevirtual 57	android/os/Parcel:readException	()V
        //   67: aload 7
        //   69: invokevirtual 61	android/os/Parcel:readInt	()I
        //   72: istore_3
        //   73: iload_3
        //   74: ifeq +19 -> 93
        //   77: iconst_1
        //   78: istore 4
        //   80: aload 7
        //   82: invokevirtual 64	android/os/Parcel:recycle	()V
        //   85: aload 6
        //   87: invokevirtual 64	android/os/Parcel:recycle	()V
        //   90: iload 4
        //   92: ireturn
        //   93: iconst_0
        //   94: istore 4
        //   96: goto -16 -> 80
        //   99: astore_1
        //   100: aload 7
        //   102: invokevirtual 64	android/os/Parcel:recycle	()V
        //   105: aload 6
        //   107: invokevirtual 64	android/os/Parcel:recycle	()V
        //   110: aload_1
        //   111: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	112	0	this	Proxy
        //   0	112	1	paramIGnssNavigationMessageListener	IGnssNavigationMessageListener
        //   0	112	2	paramString	String
        //   72	2	3	i	int
        //   78	17	4	bool	boolean
        //   1	34	5	localIBinder	IBinder
        //   6	100	6	localParcel1	Parcel
        //   11	90	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	99	finally
        //   24	32	99	finally
        //   32	73	99	finally
      }
      
      /* Error */
      public void addTestProvider(String paramString1, ProviderProperties paramProviderProperties, String paramString2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_2
        //   24: ifnull +55 -> 79
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 82	com/android/internal/location/ProviderProperties:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: aload_3
        //   43: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload_0
        //   47: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 24
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 54 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 57	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 64	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 64	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   85: goto -45 -> 40
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 64	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 64	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramString1	String
        //   0	101	2	paramProviderProperties	ProviderProperties
        //   0	101	3	paramString2	String
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	88	finally
        //   27	40	88	finally
        //   40	68	88	finally
        //   79	85	88	finally
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void clearAllPendingBroadcastsLocked()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          this.mRemote.transact(35, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clearTestProviderEnabled(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(29, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clearTestProviderLocation(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(27, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clearTestProviderStatus(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
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
      public boolean geocoderIsPresent()
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
        //   16: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 8
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 54 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 57	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
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
      
      public List<String> getAllProviders()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createStringArrayList();
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getBestProvider(Criteria paramCriteria, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.location.ILocationManager");
            if (paramCriteria != null)
            {
              localParcel1.writeInt(1);
              paramCriteria.writeToParcel(localParcel1, 0);
              break label112;
              localParcel1.writeInt(i);
              this.mRemote.transact(19, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramCriteria = localParcel2.readString();
              return paramCriteria;
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label112:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public List<String> getCurrentProviderPackageList(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(37, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createStringArrayList();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public String getFromLocation(double paramDouble1, double paramDouble2, int paramInt, GeocoderParams paramGeocoderParams, List<Address> paramList)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 8
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 9
        //   10: aload 8
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 8
        //   19: dload_1
        //   20: invokevirtual 115	android/os/Parcel:writeDouble	(D)V
        //   23: aload 8
        //   25: dload_3
        //   26: invokevirtual 115	android/os/Parcel:writeDouble	(D)V
        //   29: aload 8
        //   31: iload 5
        //   33: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   36: aload 6
        //   38: ifnull +69 -> 107
        //   41: aload 8
        //   43: iconst_1
        //   44: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   47: aload 6
        //   49: aload 8
        //   51: iconst_0
        //   52: invokevirtual 118	android/location/GeocoderParams:writeToParcel	(Landroid/os/Parcel;I)V
        //   55: aload_0
        //   56: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   59: bipush 9
        //   61: aload 8
        //   63: aload 9
        //   65: iconst_0
        //   66: invokeinterface 54 5 0
        //   71: pop
        //   72: aload 9
        //   74: invokevirtual 57	android/os/Parcel:readException	()V
        //   77: aload 9
        //   79: invokevirtual 106	android/os/Parcel:readString	()Ljava/lang/String;
        //   82: astore 6
        //   84: aload 9
        //   86: aload 7
        //   88: getstatic 124	android/location/Address:CREATOR	Landroid/os/Parcelable$Creator;
        //   91: invokevirtual 128	android/os/Parcel:readTypedList	(Ljava/util/List;Landroid/os/Parcelable$Creator;)V
        //   94: aload 9
        //   96: invokevirtual 64	android/os/Parcel:recycle	()V
        //   99: aload 8
        //   101: invokevirtual 64	android/os/Parcel:recycle	()V
        //   104: aload 6
        //   106: areturn
        //   107: aload 8
        //   109: iconst_0
        //   110: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   113: goto -58 -> 55
        //   116: astore 6
        //   118: aload 9
        //   120: invokevirtual 64	android/os/Parcel:recycle	()V
        //   123: aload 8
        //   125: invokevirtual 64	android/os/Parcel:recycle	()V
        //   128: aload 6
        //   130: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	131	0	this	Proxy
        //   0	131	1	paramDouble1	double
        //   0	131	3	paramDouble2	double
        //   0	131	5	paramInt	int
        //   0	131	6	paramGeocoderParams	GeocoderParams
        //   0	131	7	paramList	List<Address>
        //   3	121	8	localParcel1	Parcel
        //   8	111	9	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	36	116	finally
        //   41	55	116	finally
        //   55	94	116	finally
        //   107	113	116	finally
      }
      
      /* Error */
      public String getFromLocationName(String paramString, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt, GeocoderParams paramGeocoderParams, List<Address> paramList)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 13
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 14
        //   10: aload 13
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 13
        //   19: aload_1
        //   20: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 13
        //   25: dload_2
        //   26: invokevirtual 115	android/os/Parcel:writeDouble	(D)V
        //   29: aload 13
        //   31: dload 4
        //   33: invokevirtual 115	android/os/Parcel:writeDouble	(D)V
        //   36: aload 13
        //   38: dload 6
        //   40: invokevirtual 115	android/os/Parcel:writeDouble	(D)V
        //   43: aload 13
        //   45: dload 8
        //   47: invokevirtual 115	android/os/Parcel:writeDouble	(D)V
        //   50: aload 13
        //   52: iload 10
        //   54: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   57: aload 11
        //   59: ifnull +67 -> 126
        //   62: aload 13
        //   64: iconst_1
        //   65: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   68: aload 11
        //   70: aload 13
        //   72: iconst_0
        //   73: invokevirtual 118	android/location/GeocoderParams:writeToParcel	(Landroid/os/Parcel;I)V
        //   76: aload_0
        //   77: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   80: bipush 10
        //   82: aload 13
        //   84: aload 14
        //   86: iconst_0
        //   87: invokeinterface 54 5 0
        //   92: pop
        //   93: aload 14
        //   95: invokevirtual 57	android/os/Parcel:readException	()V
        //   98: aload 14
        //   100: invokevirtual 106	android/os/Parcel:readString	()Ljava/lang/String;
        //   103: astore_1
        //   104: aload 14
        //   106: aload 12
        //   108: getstatic 124	android/location/Address:CREATOR	Landroid/os/Parcelable$Creator;
        //   111: invokevirtual 128	android/os/Parcel:readTypedList	(Ljava/util/List;Landroid/os/Parcelable$Creator;)V
        //   114: aload 14
        //   116: invokevirtual 64	android/os/Parcel:recycle	()V
        //   119: aload 13
        //   121: invokevirtual 64	android/os/Parcel:recycle	()V
        //   124: aload_1
        //   125: areturn
        //   126: aload 13
        //   128: iconst_0
        //   129: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   132: goto -56 -> 76
        //   135: astore_1
        //   136: aload 14
        //   138: invokevirtual 64	android/os/Parcel:recycle	()V
        //   141: aload 13
        //   143: invokevirtual 64	android/os/Parcel:recycle	()V
        //   146: aload_1
        //   147: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	148	0	this	Proxy
        //   0	148	1	paramString	String
        //   0	148	2	paramDouble1	double
        //   0	148	4	paramDouble2	double
        //   0	148	6	paramDouble3	double
        //   0	148	8	paramDouble4	double
        //   0	148	10	paramInt	int
        //   0	148	11	paramGeocoderParams	GeocoderParams
        //   0	148	12	paramList	List<Address>
        //   3	139	13	localParcel1	Parcel
        //   8	129	14	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	135	finally
        //   62	76	135	finally
        //   76	114	135	finally
        //   126	132	135	finally
      }
      
      public int getGnssYearOfHardware()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
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
      
      public String getInterfaceDescriptor()
      {
        return "android.location.ILocationManager";
      }
      
      /* Error */
      public Location getLastKnownLocation()
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
        //   14: aload_0
        //   15: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 36
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 54 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 57	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 61	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 139	android/location/Location:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 145 2 0
        //   49: checkcast 138	android/location/Location
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 64	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 64	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 64	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localLocation	Location
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public Location getLastLocation(LocationRequest paramLocationRequest, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.location.ILocationManager");
            if (paramLocationRequest != null)
            {
              localParcel1.writeInt(1);
              paramLocationRequest.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(5, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramLocationRequest = (Location)Location.CREATOR.createFromParcel(localParcel2);
                return paramLocationRequest;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramLocationRequest = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public String getNetworkProviderPackage()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public ProviderProperties getProviderProperties(String paramString)
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
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: bipush 21
        //   25: aload_2
        //   26: aload_3
        //   27: iconst_0
        //   28: invokeinterface 54 5 0
        //   33: pop
        //   34: aload_3
        //   35: invokevirtual 57	android/os/Parcel:readException	()V
        //   38: aload_3
        //   39: invokevirtual 61	android/os/Parcel:readInt	()I
        //   42: ifeq +26 -> 68
        //   45: getstatic 154	com/android/internal/location/ProviderProperties:CREATOR	Landroid/os/Parcelable$Creator;
        //   48: aload_3
        //   49: invokeinterface 145 2 0
        //   54: checkcast 78	com/android/internal/location/ProviderProperties
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: aload_1
        //   67: areturn
        //   68: aconst_null
        //   69: astore_1
        //   70: goto -12 -> 58
        //   73: astore_1
        //   74: aload_3
        //   75: invokevirtual 64	android/os/Parcel:recycle	()V
        //   78: aload_2
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload_1
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	Proxy
        //   0	84	1	paramString	String
        //   3	76	2	localParcel1	Parcel
        //   7	68	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	58	73	finally
      }
      
      public List<String> getProviders(Criteria paramCriteria, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.location.ILocationManager");
            if (paramCriteria != null)
            {
              localParcel1.writeInt(1);
              paramCriteria.writeToParcel(localParcel1, 0);
              break label112;
              localParcel1.writeInt(i);
              this.mRemote.transact(18, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramCriteria = localParcel2.createStringArrayList();
              return paramCriteria;
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label112:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      /* Error */
      public boolean isProviderEnabled(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 23
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 54 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 57	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 61	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 64	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 64	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 64	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      public void locationCallbackFinished(ILocationListener paramILocationListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          if (paramILocationListener != null) {
            localIBinder = paramILocationListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(34, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean providerMeetsCriteria(String paramString, Criteria paramCriteria)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.location.ILocationManager");
            localParcel1.writeString(paramString);
            if (paramCriteria != null)
            {
              localParcel1.writeInt(1);
              paramCriteria.writeToParcel(localParcel1, 0);
              this.mRemote.transact(20, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public boolean registerGnssStatusCallback(IGnssStatusListener paramIGnssStatusListener, String paramString)
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
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 171 1 0
        //   30: astore 5
        //   32: aload 6
        //   34: aload 5
        //   36: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload 6
        //   41: aload_2
        //   42: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   45: aload_0
        //   46: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   49: bipush 6
        //   51: aload 6
        //   53: aload 7
        //   55: iconst_0
        //   56: invokeinterface 54 5 0
        //   61: pop
        //   62: aload 7
        //   64: invokevirtual 57	android/os/Parcel:readException	()V
        //   67: aload 7
        //   69: invokevirtual 61	android/os/Parcel:readInt	()I
        //   72: istore_3
        //   73: iload_3
        //   74: ifeq +19 -> 93
        //   77: iconst_1
        //   78: istore 4
        //   80: aload 7
        //   82: invokevirtual 64	android/os/Parcel:recycle	()V
        //   85: aload 6
        //   87: invokevirtual 64	android/os/Parcel:recycle	()V
        //   90: iload 4
        //   92: ireturn
        //   93: iconst_0
        //   94: istore 4
        //   96: goto -16 -> 80
        //   99: astore_1
        //   100: aload 7
        //   102: invokevirtual 64	android/os/Parcel:recycle	()V
        //   105: aload 6
        //   107: invokevirtual 64	android/os/Parcel:recycle	()V
        //   110: aload_1
        //   111: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	112	0	this	Proxy
        //   0	112	1	paramIGnssStatusListener	IGnssStatusListener
        //   0	112	2	paramString	String
        //   72	2	3	i	int
        //   78	17	4	bool	boolean
        //   1	34	5	localIBinder	IBinder
        //   6	100	6	localParcel1	Parcel
        //   11	90	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	99	finally
        //   24	32	99	finally
        //   32	73	99	finally
      }
      
      public void removeGeofence(Geofence paramGeofence, PendingIntent paramPendingIntent, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.location.ILocationManager");
            if (paramGeofence != null)
            {
              localParcel1.writeInt(1);
              paramGeofence.writeToParcel(localParcel1, 0);
              if (paramPendingIntent != null)
              {
                localParcel1.writeInt(1);
                paramPendingIntent.writeToParcel(localParcel1, 0);
                localParcel1.writeString(paramString);
                this.mRemote.transact(4, localParcel1, localParcel2, 0);
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
      
      public void removeGnssMeasurementsListener(IGnssMeasurementsListener paramIGnssMeasurementsListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          if (paramIGnssMeasurementsListener != null) {
            localIBinder = paramIGnssMeasurementsListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void removeGnssNavigationMessageListener(IGnssNavigationMessageListener paramIGnssNavigationMessageListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          if (paramIGnssNavigationMessageListener != null) {
            localIBinder = paramIGnssNavigationMessageListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removeTestProvider(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
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
      
      /* Error */
      public void removeUpdates(ILocationListener paramILocationListener, PendingIntent paramPendingIntent, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 164 1 0
        //   30: astore 4
        //   32: aload 5
        //   34: aload 4
        //   36: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_2
        //   40: ifnull +54 -> 94
        //   43: aload 5
        //   45: iconst_1
        //   46: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   49: aload_2
        //   50: aload 5
        //   52: iconst_0
        //   53: invokevirtual 179	android/app/PendingIntent:writeToParcel	(Landroid/os/Parcel;I)V
        //   56: aload 5
        //   58: aload_3
        //   59: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   62: aload_0
        //   63: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   66: iconst_2
        //   67: aload 5
        //   69: aload 6
        //   71: iconst_0
        //   72: invokeinterface 54 5 0
        //   77: pop
        //   78: aload 6
        //   80: invokevirtual 57	android/os/Parcel:readException	()V
        //   83: aload 6
        //   85: invokevirtual 64	android/os/Parcel:recycle	()V
        //   88: aload 5
        //   90: invokevirtual 64	android/os/Parcel:recycle	()V
        //   93: return
        //   94: aload 5
        //   96: iconst_0
        //   97: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   100: goto -44 -> 56
        //   103: astore_1
        //   104: aload 6
        //   106: invokevirtual 64	android/os/Parcel:recycle	()V
        //   109: aload 5
        //   111: invokevirtual 64	android/os/Parcel:recycle	()V
        //   114: aload_1
        //   115: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	116	0	this	Proxy
        //   0	116	1	paramILocationListener	ILocationListener
        //   0	116	2	paramPendingIntent	PendingIntent
        //   0	116	3	paramString	String
        //   1	34	4	localIBinder	IBinder
        //   6	104	5	localParcel1	Parcel
        //   11	94	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	103	finally
        //   24	32	103	finally
        //   32	39	103	finally
        //   43	56	103	finally
        //   56	83	103	finally
        //   94	100	103	finally
      }
      
      public void reportLocation(Location paramLocation, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.location.ILocationManager");
            if (paramLocation != null)
            {
              localParcel1.writeInt(1);
              paramLocation.writeToParcel(localParcel1, 0);
              break label105;
              localParcel1.writeInt(i);
              this.mRemote.transact(33, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label105:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void requestGeofence(LocationRequest paramLocationRequest, Geofence paramGeofence, PendingIntent paramPendingIntent, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.location.ILocationManager");
            if (paramLocationRequest != null)
            {
              localParcel1.writeInt(1);
              paramLocationRequest.writeToParcel(localParcel1, 0);
              if (paramGeofence != null)
              {
                localParcel1.writeInt(1);
                paramGeofence.writeToParcel(localParcel1, 0);
                if (paramPendingIntent == null) {
                  break label138;
                }
                localParcel1.writeInt(1);
                paramPendingIntent.writeToParcel(localParcel1, 0);
                localParcel1.writeString(paramString);
                this.mRemote.transact(3, localParcel1, localParcel2, 0);
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
          continue;
          label138:
          localParcel1.writeInt(0);
        }
      }
      
      public void requestLocationUpdates(LocationRequest paramLocationRequest, ILocationListener paramILocationListener, PendingIntent paramPendingIntent, String paramString)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.location.ILocationManager");
            if (paramLocationRequest != null)
            {
              localParcel1.writeInt(1);
              paramLocationRequest.writeToParcel(localParcel1, 0);
              paramLocationRequest = (LocationRequest)localObject;
              if (paramILocationListener != null) {
                paramLocationRequest = paramILocationListener.asBinder();
              }
              localParcel1.writeStrongBinder(paramLocationRequest);
              if (paramPendingIntent != null)
              {
                localParcel1.writeInt(1);
                paramPendingIntent.writeToParcel(localParcel1, 0);
                localParcel1.writeString(paramString);
                this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
      
      public boolean sendExtraCommand(String paramString1, String paramString2, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.location.ILocationManager");
            localParcel1.writeString(paramString1);
            localParcel1.writeString(paramString2);
            if (paramBundle != null)
            {
              localParcel1.writeInt(1);
              paramBundle.writeToParcel(localParcel1, 0);
              this.mRemote.transact(32, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                bool = true;
                if (localParcel2.readInt() != 0) {
                  paramBundle.readFromParcel(localParcel2);
                }
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public boolean sendNiResponse(int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 11
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 54 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 57	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 61	android/os/Parcel:readInt	()I
        //   56: istore_1
        //   57: iload_1
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 64	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 64	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore 6
        //   82: aload 5
        //   84: invokevirtual 64	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: invokevirtual 64	android/os/Parcel:recycle	()V
        //   92: aload 6
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramInt1	int
        //   0	95	2	paramInt2	int
        //   62	15	3	bool	boolean
        //   3	85	4	localParcel1	Parcel
        //   8	75	5	localParcel2	Parcel
        //   80	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      public void setTestProviderEnabled(String paramString1, boolean paramBoolean, String paramString2)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          localParcel1.writeString(paramString1);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(28, localParcel1, localParcel2, 0);
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
      public void setTestProviderLocation(String paramString1, Location paramLocation, String paramString2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_2
        //   24: ifnull +55 -> 79
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 189	android/location/Location:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: aload_3
        //   43: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload_0
        //   47: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 26
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 54 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 57	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 64	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 64	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   85: goto -45 -> 40
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 64	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 64	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramString1	String
        //   0	101	2	paramLocation	Location
        //   0	101	3	paramString2	String
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	88	finally
        //   27	40	88	finally
        //   40	68	88	finally
        //   79	85	88	finally
      }
      
      /* Error */
      public void setTestProviderStatus(String paramString1, int paramInt, Bundle paramBundle, long paramLong, String paramString2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 7
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 8
        //   10: aload 7
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 7
        //   19: aload_1
        //   20: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 7
        //   25: iload_2
        //   26: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   29: aload_3
        //   30: ifnull +63 -> 93
        //   33: aload 7
        //   35: iconst_1
        //   36: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   39: aload_3
        //   40: aload 7
        //   42: iconst_0
        //   43: invokevirtual 198	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload 7
        //   48: lload 4
        //   50: invokevirtual 214	android/os/Parcel:writeLong	(J)V
        //   53: aload 7
        //   55: aload 6
        //   57: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   60: aload_0
        //   61: getfield 19	android/location/ILocationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   64: bipush 30
        //   66: aload 7
        //   68: aload 8
        //   70: iconst_0
        //   71: invokeinterface 54 5 0
        //   76: pop
        //   77: aload 8
        //   79: invokevirtual 57	android/os/Parcel:readException	()V
        //   82: aload 8
        //   84: invokevirtual 64	android/os/Parcel:recycle	()V
        //   87: aload 7
        //   89: invokevirtual 64	android/os/Parcel:recycle	()V
        //   92: return
        //   93: aload 7
        //   95: iconst_0
        //   96: invokevirtual 76	android/os/Parcel:writeInt	(I)V
        //   99: goto -53 -> 46
        //   102: astore_1
        //   103: aload 8
        //   105: invokevirtual 64	android/os/Parcel:recycle	()V
        //   108: aload 7
        //   110: invokevirtual 64	android/os/Parcel:recycle	()V
        //   113: aload_1
        //   114: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	115	0	this	Proxy
        //   0	115	1	paramString1	String
        //   0	115	2	paramInt	int
        //   0	115	3	paramBundle	Bundle
        //   0	115	4	paramLong	long
        //   0	115	6	paramString2	String
        //   3	106	7	localParcel1	Parcel
        //   8	96	8	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	29	102	finally
        //   33	46	102	finally
        //   46	82	102	finally
        //   93	99	102	finally
      }
      
      public void unregisterGnssStatusCallback(IGnssStatusListener paramIGnssStatusListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.ILocationManager");
          if (paramIGnssStatusListener != null) {
            localIBinder = paramIGnssStatusListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/ILocationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */