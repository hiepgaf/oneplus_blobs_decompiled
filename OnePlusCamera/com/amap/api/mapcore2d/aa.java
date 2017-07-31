package com.amap.api.mapcore2d;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.RemoteException;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.LatLng;
import java.util.ArrayList;

public abstract interface aa
  extends ab
{
  public abstract void a(float paramFloat)
    throws RemoteException;
  
  public abstract void a(float paramFloat1, float paramFloat2);
  
  public abstract void a(int paramInt)
    throws RemoteException;
  
  public abstract void a(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void a(Canvas paramCanvas, w paramw);
  
  public abstract void a(BitmapDescriptor paramBitmapDescriptor);
  
  public abstract void a(LatLng paramLatLng);
  
  public abstract void a(String paramString);
  
  public abstract void a(ArrayList<BitmapDescriptor> paramArrayList)
    throws RemoteException;
  
  public abstract void a(boolean paramBoolean);
  
  public abstract boolean a()
    throws RemoteException;
  
  public abstract boolean a(aa paramaa);
  
  public abstract Rect b();
  
  public abstract void b(String paramString);
  
  public abstract void b(boolean paramBoolean);
  
  public abstract LatLng c();
  
  public abstract String d();
  
  public abstract r e();
  
  public abstract String f();
  
  public abstract String g();
  
  public abstract boolean h();
  
  public abstract void i();
  
  public abstract void j();
  
  public abstract boolean k();
  
  public abstract void l();
  
  public abstract int m();
  
  public abstract int n();
  
  public abstract int o()
    throws RemoteException;
  
  public abstract ArrayList<BitmapDescriptor> p()
    throws RemoteException;
  
  public abstract boolean q()
    throws RemoteException;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/aa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */