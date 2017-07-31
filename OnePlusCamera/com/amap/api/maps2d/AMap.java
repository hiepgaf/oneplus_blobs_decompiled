package com.amap.api.maps2d;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.amap.api.mapcore2d.cj;
import com.amap.api.mapcore2d.w;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.GroundOverlay;
import com.amap.api.maps2d.model.GroundOverlayOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.Polygon;
import com.amap.api.maps2d.model.PolygonOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.maps2d.model.RuntimeRemoteException;
import com.amap.api.maps2d.model.Text;
import com.amap.api.maps2d.model.TextOptions;
import com.amap.api.maps2d.model.TileOverlay;
import com.amap.api.maps2d.model.TileOverlayOptions;
import java.util.List;

public final class AMap
{
  public static final String CHINESE = "zh_cn";
  public static final String ENGLISH = "en";
  public static final int MAP_TYPE_NORMAL = 1;
  public static final int MAP_TYPE_SATELLITE = 2;
  private final w a;
  private UiSettings b;
  private Projection c;
  
  protected AMap(w paramw)
  {
    this.a = paramw;
  }
  
  private w a()
  {
    return this.a;
  }
  
  public static String getVersion()
  {
    return "2.9.2";
  }
  
  public final Circle addCircle(CircleOptions paramCircleOptions)
  {
    try
    {
      paramCircleOptions = new Circle(a().a(paramCircleOptions));
      return paramCircleOptions;
    }
    catch (RemoteException paramCircleOptions)
    {
      cj.a(paramCircleOptions, "AMap", "addCircle");
      throw new RuntimeRemoteException(paramCircleOptions);
    }
  }
  
  public final GroundOverlay addGroundOverlay(GroundOverlayOptions paramGroundOverlayOptions)
  {
    try
    {
      paramGroundOverlayOptions = new GroundOverlay(a().a(paramGroundOverlayOptions));
      return paramGroundOverlayOptions;
    }
    catch (RemoteException paramGroundOverlayOptions)
    {
      cj.a(paramGroundOverlayOptions, "AMap", "addGroundOverlay");
      throw new RuntimeRemoteException(paramGroundOverlayOptions);
    }
  }
  
  public final Marker addMarker(MarkerOptions paramMarkerOptions)
  {
    try
    {
      paramMarkerOptions = a().a(paramMarkerOptions);
      return paramMarkerOptions;
    }
    catch (RemoteException paramMarkerOptions)
    {
      cj.a(paramMarkerOptions, "AMap", "addMarker");
      throw new RuntimeRemoteException(paramMarkerOptions);
    }
  }
  
  public final Polygon addPolygon(PolygonOptions paramPolygonOptions)
  {
    try
    {
      paramPolygonOptions = new Polygon(a().a(paramPolygonOptions));
      return paramPolygonOptions;
    }
    catch (RemoteException paramPolygonOptions)
    {
      cj.a(paramPolygonOptions, "AMap", "addPolygon");
      throw new RuntimeRemoteException(paramPolygonOptions);
    }
  }
  
  public final Polyline addPolyline(PolylineOptions paramPolylineOptions)
  {
    try
    {
      paramPolylineOptions = new Polyline(a().a(paramPolylineOptions));
      return paramPolylineOptions;
    }
    catch (RemoteException paramPolylineOptions)
    {
      cj.a(paramPolylineOptions, "AMap", "addPolyline");
      throw new RuntimeRemoteException(paramPolylineOptions);
    }
  }
  
  public final Text addText(TextOptions paramTextOptions)
  {
    try
    {
      paramTextOptions = this.a.a(paramTextOptions);
      return paramTextOptions;
    }
    catch (RemoteException paramTextOptions)
    {
      cj.a(paramTextOptions, "AMap", "addText");
      throw new RuntimeRemoteException(paramTextOptions);
    }
  }
  
  public final TileOverlay addTileOverlay(TileOverlayOptions paramTileOverlayOptions)
  {
    try
    {
      paramTileOverlayOptions = a().a(paramTileOverlayOptions);
      return paramTileOverlayOptions;
    }
    catch (RemoteException paramTileOverlayOptions)
    {
      cj.a(paramTileOverlayOptions, "AMap", "addtileOverlay");
      throw new RuntimeRemoteException(paramTileOverlayOptions);
    }
  }
  
  public final void animateCamera(CameraUpdate paramCameraUpdate)
  {
    try
    {
      a().b(paramCameraUpdate.a());
      return;
    }
    catch (RemoteException paramCameraUpdate)
    {
      cj.a(paramCameraUpdate, "AMap", "animateCamera");
      throw new RuntimeRemoteException(paramCameraUpdate);
    }
  }
  
  public final void animateCamera(CameraUpdate paramCameraUpdate, long paramLong, CancelableCallback paramCancelableCallback)
  {
    if (paramLong > 0L) {}
    for (int i = 1;; i = 0)
    {
      if (i == 0) {}
      try
      {
        Log.w("AMap", "durationMs must be positive");
        a().a(paramCameraUpdate.a(), paramLong, paramCancelableCallback);
        return;
      }
      catch (RemoteException paramCameraUpdate)
      {
        cj.a(paramCameraUpdate, "AMap", "animateCamera");
        throw new RuntimeRemoteException(paramCameraUpdate);
      }
    }
  }
  
  public final void animateCamera(CameraUpdate paramCameraUpdate, CancelableCallback paramCancelableCallback)
  {
    try
    {
      a().a(paramCameraUpdate.a(), paramCancelableCallback);
      return;
    }
    catch (RemoteException paramCameraUpdate)
    {
      cj.a(paramCameraUpdate, "AMap", "animateCamera");
      throw new RuntimeRemoteException(paramCameraUpdate);
    }
  }
  
  public final void clear()
  {
    try
    {
      if (a() == null) {
        return;
      }
      a().k();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "clear");
      throw new RuntimeRemoteException(localRemoteException);
    }
    catch (Throwable localThrowable)
    {
      cj.a(localThrowable, "AMap", "clear");
    }
  }
  
  public final CameraPosition getCameraPosition()
  {
    try
    {
      CameraPosition localCameraPosition = a().g();
      return localCameraPosition;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "getCameraPosition");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final List<Marker> getMapScreenMarkers()
  {
    try
    {
      List localList = this.a.S();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "getMapScreenaMarkers");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void getMapScreenShot(OnMapScreenShotListener paramOnMapScreenShotListener)
  {
    a().a(paramOnMapScreenShotListener);
    invalidate();
  }
  
  public final int getMapType()
  {
    try
    {
      int i = a().l();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "getMapType");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final float getMaxZoomLevel()
  {
    return a().h();
  }
  
  public final float getMinZoomLevel()
  {
    return a().i();
  }
  
  public final Location getMyLocation()
  {
    try
    {
      Location localLocation = a().p();
      return localLocation;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "getMyLocation");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  /* Error */
  public final Projection getProjection()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 233	com/amap/api/maps2d/AMap:c	Lcom/amap/api/maps2d/Projection;
    //   4: ifnull +8 -> 12
    //   7: aload_0
    //   8: getfield 233	com/amap/api/maps2d/AMap:c	Lcom/amap/api/maps2d/Projection;
    //   11: areturn
    //   12: aload_0
    //   13: new 235	com/amap/api/maps2d/Projection
    //   16: dup
    //   17: aload_0
    //   18: invokespecial 82	com/amap/api/maps2d/AMap:a	()Lcom/amap/api/mapcore2d/w;
    //   21: invokeinterface 239 1 0
    //   26: invokespecial 242	com/amap/api/maps2d/Projection:<init>	(Lcom/amap/api/mapcore2d/ag;)V
    //   29: putfield 233	com/amap/api/maps2d/AMap:c	Lcom/amap/api/maps2d/Projection;
    //   32: goto -25 -> 7
    //   35: astore_1
    //   36: aload_1
    //   37: ldc 92
    //   39: ldc -13
    //   41: invokestatic 98	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   44: new 100	com/amap/api/maps2d/model/RuntimeRemoteException
    //   47: dup
    //   48: aload_1
    //   49: invokespecial 103	com/amap/api/maps2d/model/RuntimeRemoteException:<init>	(Landroid/os/RemoteException;)V
    //   52: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	53	0	this	AMap
    //   35	14	1	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   0	7	35	android/os/RemoteException
    //   7	12	35	android/os/RemoteException
    //   12	32	35	android/os/RemoteException
  }
  
  public float getScalePerPixel()
  {
    return a().w();
  }
  
  /* Error */
  public final UiSettings getUiSettings()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 250	com/amap/api/maps2d/AMap:b	Lcom/amap/api/maps2d/UiSettings;
    //   4: ifnull +8 -> 12
    //   7: aload_0
    //   8: getfield 250	com/amap/api/maps2d/AMap:b	Lcom/amap/api/maps2d/UiSettings;
    //   11: areturn
    //   12: aload_0
    //   13: new 252	com/amap/api/maps2d/UiSettings
    //   16: dup
    //   17: aload_0
    //   18: invokespecial 82	com/amap/api/maps2d/AMap:a	()Lcom/amap/api/mapcore2d/w;
    //   21: invokeinterface 256 1 0
    //   26: invokespecial 259	com/amap/api/maps2d/UiSettings:<init>	(Lcom/amap/api/mapcore2d/ak;)V
    //   29: putfield 250	com/amap/api/maps2d/AMap:b	Lcom/amap/api/maps2d/UiSettings;
    //   32: goto -25 -> 7
    //   35: astore_1
    //   36: aload_1
    //   37: ldc 92
    //   39: ldc_w 260
    //   42: invokestatic 98	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   45: new 100	com/amap/api/maps2d/model/RuntimeRemoteException
    //   48: dup
    //   49: aload_1
    //   50: invokespecial 103	com/amap/api/maps2d/model/RuntimeRemoteException:<init>	(Landroid/os/RemoteException;)V
    //   53: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	54	0	this	AMap
    //   35	15	1	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   0	7	35	android/os/RemoteException
    //   7	12	35	android/os/RemoteException
    //   12	32	35	android/os/RemoteException
  }
  
  public void invalidate()
  {
    postInvalidate();
  }
  
  public final boolean isMyLocationEnabled()
  {
    try
    {
      boolean bool = a().n();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "isMyLocationEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final boolean isTrafficEnabled()
  {
    try
    {
      boolean bool = a().m();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "isTrafficEnable");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void moveCamera(CameraUpdate paramCameraUpdate)
  {
    try
    {
      a().a(paramCameraUpdate.a());
      return;
    }
    catch (RemoteException paramCameraUpdate)
    {
      cj.a(paramCameraUpdate, "AMap", "moveCamera");
      throw new RuntimeRemoteException(paramCameraUpdate);
    }
  }
  
  public void postInvalidate()
  {
    a().R();
  }
  
  public void removecache()
  {
    try
    {
      this.a.T();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "removecache");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void removecache(OnCacheRemoveListener paramOnCacheRemoveListener)
  {
    try
    {
      this.a.a(paramOnCacheRemoveListener);
      return;
    }
    catch (RemoteException paramOnCacheRemoveListener)
    {
      cj.a(paramOnCacheRemoveListener, "AMap", "removecache");
      throw new RuntimeRemoteException(paramOnCacheRemoveListener);
    }
  }
  
  public final void setInfoWindowAdapter(InfoWindowAdapter paramInfoWindowAdapter)
  {
    try
    {
      a().a(paramInfoWindowAdapter);
      return;
    }
    catch (RemoteException paramInfoWindowAdapter)
    {
      cj.a(paramInfoWindowAdapter, "AMap", "setInfoWindowAdapter");
      throw new RuntimeRemoteException(paramInfoWindowAdapter);
    }
  }
  
  public final void setLocationSource(LocationSource paramLocationSource)
  {
    try
    {
      a().a(paramLocationSource);
      return;
    }
    catch (RemoteException paramLocationSource)
    {
      cj.a(paramLocationSource, "AMap", "setLocationSource");
      throw new RuntimeRemoteException(paramLocationSource);
    }
  }
  
  public void setMapLanguage(String paramString)
  {
    try
    {
      this.a.c(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      cj.a(paramString, "AMap", "setMapLanguage");
      throw new RuntimeRemoteException(paramString);
    }
  }
  
  public final void setMapType(int paramInt)
  {
    try
    {
      a().a(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "setMapType");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setMyLocationEnabled(boolean paramBoolean)
  {
    try
    {
      a().c(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "setMyLocationEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setMyLocationRotateAngle(float paramFloat)
  {
    try
    {
      this.a.b(paramFloat);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "setMyLocationRoteteAngle");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setMyLocationStyle(MyLocationStyle paramMyLocationStyle)
  {
    try
    {
      a().a(paramMyLocationStyle);
      return;
    }
    catch (RemoteException paramMyLocationStyle)
    {
      cj.a(paramMyLocationStyle, "AMap", "setMyLocationStyle");
      throw new RuntimeRemoteException(paramMyLocationStyle);
    }
  }
  
  public final void setOnCameraChangeListener(OnCameraChangeListener paramOnCameraChangeListener)
  {
    try
    {
      a().a(paramOnCameraChangeListener);
      return;
    }
    catch (RemoteException paramOnCameraChangeListener)
    {
      cj.a(paramOnCameraChangeListener, "AMap", "setOnCameraChangeListener");
      throw new RuntimeRemoteException(paramOnCameraChangeListener);
    }
  }
  
  public final void setOnInfoWindowClickListener(OnInfoWindowClickListener paramOnInfoWindowClickListener)
  {
    try
    {
      a().a(paramOnInfoWindowClickListener);
      return;
    }
    catch (RemoteException paramOnInfoWindowClickListener)
    {
      cj.a(paramOnInfoWindowClickListener, "AMap", "setOnInfoWindowClickListener");
      throw new RuntimeRemoteException(paramOnInfoWindowClickListener);
    }
  }
  
  public final void setOnMapClickListener(OnMapClickListener paramOnMapClickListener)
  {
    try
    {
      a().a(paramOnMapClickListener);
      return;
    }
    catch (RemoteException paramOnMapClickListener)
    {
      cj.a(paramOnMapClickListener, "AMap", "setOnMapClickListener");
      throw new RuntimeRemoteException(paramOnMapClickListener);
    }
  }
  
  public final void setOnMapLoadedListener(OnMapLoadedListener paramOnMapLoadedListener)
  {
    try
    {
      a().a(paramOnMapLoadedListener);
      return;
    }
    catch (RemoteException paramOnMapLoadedListener)
    {
      cj.a(paramOnMapLoadedListener, "AMap", "setOnMapLoadedListener");
      throw new RuntimeRemoteException(paramOnMapLoadedListener);
    }
  }
  
  public final void setOnMapLongClickListener(OnMapLongClickListener paramOnMapLongClickListener)
  {
    try
    {
      a().a(paramOnMapLongClickListener);
      return;
    }
    catch (RemoteException paramOnMapLongClickListener)
    {
      cj.a(paramOnMapLongClickListener, "AMap", "setOnMapLongClickListener");
      throw new RuntimeRemoteException(paramOnMapLongClickListener);
    }
  }
  
  public final void setOnMapTouchListener(OnMapTouchListener paramOnMapTouchListener)
  {
    try
    {
      this.a.a(paramOnMapTouchListener);
      return;
    }
    catch (RemoteException paramOnMapTouchListener)
    {
      cj.a(paramOnMapTouchListener, "AMap", "setOnMapTouchListener");
      throw new RuntimeRemoteException(paramOnMapTouchListener);
    }
  }
  
  public final void setOnMarkerClickListener(OnMarkerClickListener paramOnMarkerClickListener)
  {
    try
    {
      a().a(paramOnMarkerClickListener);
      return;
    }
    catch (RemoteException paramOnMarkerClickListener)
    {
      cj.a(paramOnMarkerClickListener, "AMap", "setOnMarkerClickListener");
      throw new RuntimeRemoteException(paramOnMarkerClickListener);
    }
  }
  
  public final void setOnMarkerDragListener(OnMarkerDragListener paramOnMarkerDragListener)
  {
    try
    {
      a().a(paramOnMarkerDragListener);
      return;
    }
    catch (RemoteException paramOnMarkerDragListener)
    {
      cj.a(paramOnMarkerDragListener, "AMap", "setOnMarkerDragListener");
      throw new RuntimeRemoteException(paramOnMarkerDragListener);
    }
  }
  
  public final void setOnMyLocationChangeListener(OnMyLocationChangeListener paramOnMyLocationChangeListener)
  {
    try
    {
      a().a(paramOnMyLocationChangeListener);
      return;
    }
    catch (RemoteException paramOnMyLocationChangeListener)
    {
      cj.a(paramOnMyLocationChangeListener, "AMap", "setOnMyLocaitonChangeListener");
      throw new RuntimeRemoteException(paramOnMyLocationChangeListener);
    }
  }
  
  public void setTrafficEnabled(boolean paramBoolean)
  {
    try
    {
      a().b(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "setTradficEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void stopAnimation()
  {
    try
    {
      a().j();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "AMap", "stopAnimation");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public static abstract interface CancelableCallback
  {
    public abstract void onCancel();
    
    public abstract void onFinish();
  }
  
  public static abstract interface InfoWindowAdapter
  {
    public abstract View getInfoContents(Marker paramMarker);
    
    public abstract View getInfoWindow(Marker paramMarker);
  }
  
  public static abstract interface OnCacheRemoveListener
  {
    public abstract void onRemoveCacheFinish(boolean paramBoolean);
  }
  
  public static abstract interface OnCameraChangeListener
  {
    public abstract void onCameraChange(CameraPosition paramCameraPosition);
    
    public abstract void onCameraChangeFinish(CameraPosition paramCameraPosition);
  }
  
  public static abstract interface OnInfoWindowClickListener
  {
    public abstract void onInfoWindowClick(Marker paramMarker);
  }
  
  public static abstract interface OnMapClickListener
  {
    public abstract void onMapClick(LatLng paramLatLng);
  }
  
  public static abstract interface OnMapLoadedListener
  {
    public abstract void onMapLoaded();
  }
  
  public static abstract interface OnMapLongClickListener
  {
    public abstract void onMapLongClick(LatLng paramLatLng);
  }
  
  public static abstract interface OnMapScreenShotListener
  {
    public abstract void onMapScreenShot(Bitmap paramBitmap);
  }
  
  public static abstract interface OnMapTouchListener
  {
    public abstract void onTouch(MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnMarkerClickListener
  {
    public abstract boolean onMarkerClick(Marker paramMarker);
  }
  
  public static abstract interface OnMarkerDragListener
  {
    public abstract void onMarkerDrag(Marker paramMarker);
    
    public abstract void onMarkerDragEnd(Marker paramMarker);
    
    public abstract void onMarkerDragStart(Marker paramMarker);
  }
  
  public static abstract interface OnMyLocationChangeListener
  {
    public abstract void onMyLocationChange(Location paramLocation);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/AMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */