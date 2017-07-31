package com.amap.api.mapcore2d;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.amap.api.maps2d.AMapOptions;

public abstract interface z
{
  public abstract View a(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    throws RemoteException;
  
  public abstract w a()
    throws RemoteException;
  
  public abstract void a(Activity paramActivity, AMapOptions paramAMapOptions, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void a(Context paramContext);
  
  public abstract void a(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void a(AMapOptions paramAMapOptions);
  
  public abstract void b()
    throws RemoteException;
  
  public abstract void b(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void c()
    throws RemoteException;
  
  public abstract void d()
    throws RemoteException;
  
  public abstract void e()
    throws RemoteException;
  
  public abstract void f()
    throws RemoteException;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/z.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */