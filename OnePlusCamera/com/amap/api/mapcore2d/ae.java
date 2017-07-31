package com.amap.api.mapcore2d;

import android.os.RemoteException;
import com.amap.api.maps2d.model.LatLng;
import java.util.List;

public abstract interface ae
  extends ac
{
  public abstract void a(int paramInt)
    throws RemoteException;
  
  public abstract void a(List<LatLng> paramList)
    throws RemoteException;
  
  public abstract boolean a(LatLng paramLatLng)
    throws RemoteException;
  
  public abstract void b(float paramFloat)
    throws RemoteException;
  
  public abstract void b(int paramInt)
    throws RemoteException;
  
  public abstract float g()
    throws RemoteException;
  
  public abstract int h()
    throws RemoteException;
  
  public abstract List<LatLng> i()
    throws RemoteException;
  
  public abstract int j()
    throws RemoteException;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ae.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */