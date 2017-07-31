package com.android.server.location;

import android.content.Context;
import android.hardware.location.IFusedLocationHardware;
import android.hardware.location.IFusedLocationHardware.Stub;
import android.hardware.location.IFusedLocationHardwareSink;
import android.location.FusedBatchOptions;
import android.os.RemoteException;

public class FusedLocationHardwareSecure
  extends IFusedLocationHardware.Stub
{
  private final Context mContext;
  private final IFusedLocationHardware mLocationHardware;
  private final String mPermissionId;
  
  public FusedLocationHardwareSecure(IFusedLocationHardware paramIFusedLocationHardware, Context paramContext, String paramString)
  {
    this.mLocationHardware = paramIFusedLocationHardware;
    this.mContext = paramContext;
    this.mPermissionId = paramString;
  }
  
  private void checkPermissions()
  {
    this.mContext.enforceCallingPermission(this.mPermissionId, String.format("Permission '%s' not granted to access FusedLocationHardware", new Object[] { this.mPermissionId }));
  }
  
  public void flushBatchedLocations()
    throws RemoteException
  {
    checkPermissions();
    this.mLocationHardware.flushBatchedLocations();
  }
  
  public int getSupportedBatchSize()
    throws RemoteException
  {
    checkPermissions();
    return this.mLocationHardware.getSupportedBatchSize();
  }
  
  public int getVersion()
    throws RemoteException
  {
    checkPermissions();
    return this.mLocationHardware.getVersion();
  }
  
  public void injectDeviceContext(int paramInt)
    throws RemoteException
  {
    checkPermissions();
    this.mLocationHardware.injectDeviceContext(paramInt);
  }
  
  public void injectDiagnosticData(String paramString)
    throws RemoteException
  {
    checkPermissions();
    this.mLocationHardware.injectDiagnosticData(paramString);
  }
  
  public void registerSink(IFusedLocationHardwareSink paramIFusedLocationHardwareSink)
    throws RemoteException
  {
    checkPermissions();
    this.mLocationHardware.registerSink(paramIFusedLocationHardwareSink);
  }
  
  public void requestBatchOfLocations(int paramInt)
    throws RemoteException
  {
    checkPermissions();
    this.mLocationHardware.requestBatchOfLocations(paramInt);
  }
  
  public void startBatching(int paramInt, FusedBatchOptions paramFusedBatchOptions)
    throws RemoteException
  {
    checkPermissions();
    this.mLocationHardware.startBatching(paramInt, paramFusedBatchOptions);
  }
  
  public void stopBatching(int paramInt)
    throws RemoteException
  {
    checkPermissions();
    this.mLocationHardware.stopBatching(paramInt);
  }
  
  public boolean supportsDeviceContextInjection()
    throws RemoteException
  {
    checkPermissions();
    return this.mLocationHardware.supportsDeviceContextInjection();
  }
  
  public boolean supportsDiagnosticDataInjection()
    throws RemoteException
  {
    checkPermissions();
    return this.mLocationHardware.supportsDiagnosticDataInjection();
  }
  
  public void unregisterSink(IFusedLocationHardwareSink paramIFusedLocationHardwareSink)
    throws RemoteException
  {
    checkPermissions();
    this.mLocationHardware.unregisterSink(paramIFusedLocationHardwareSink);
  }
  
  public void updateBatchingOptions(int paramInt, FusedBatchOptions paramFusedBatchOptions)
    throws RemoteException
  {
    checkPermissions();
    this.mLocationHardware.updateBatchingOptions(paramInt, paramFusedBatchOptions);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/FusedLocationHardwareSecure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */