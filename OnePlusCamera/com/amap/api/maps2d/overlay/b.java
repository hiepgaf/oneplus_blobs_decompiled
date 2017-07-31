package com.amap.api.maps2d.overlay;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import com.amap.api.mapcore2d.cj;
import com.amap.api.mapcore2d.p;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.LatLngBounds.Builder;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class b
{
  private Marker a;
  protected List<Polyline> allPolyLines = new ArrayList();
  private Marker b;
  private Context c;
  private Bitmap d;
  private Bitmap e;
  protected LatLng endPoint;
  private Bitmap f;
  private Bitmap g;
  private Bitmap h;
  private AssetManager i;
  protected AMap mAMap;
  protected boolean mNodeIconVisible = true;
  protected LatLng startPoint;
  protected List<Marker> stationMarkers = new ArrayList();
  
  public b(Context paramContext)
  {
    this.c = paramContext;
    this.i = this.c.getResources().getAssets();
  }
  
  private void a()
  {
    if (this.d == null)
    {
      if (this.e != null) {
        break label51;
      }
      label14:
      if (this.f != null) {
        break label66;
      }
      label21:
      if (this.g != null) {
        break label81;
      }
    }
    for (;;)
    {
      if (this.h != null) {
        break label96;
      }
      return;
      this.d.recycle();
      this.d = null;
      break;
      label51:
      this.e.recycle();
      this.e = null;
      break label14;
      label66:
      this.f.recycle();
      this.f = null;
      break label21;
      label81:
      this.g.recycle();
      this.g = null;
    }
    label96:
    this.h.recycle();
    this.h = null;
  }
  
  protected void addStartAndEndMarker()
  {
    this.a = this.mAMap.addMarker(new MarkerOptions().position(this.startPoint).icon(getStartBitmapDescriptor()).title("起点"));
    this.b = this.mAMap.addMarker(new MarkerOptions().position(this.endPoint).icon(getEndBitmapDescriptor()).title("终点"));
  }
  
  protected BitmapDescriptor getBitDes(Bitmap paramBitmap, String paramString)
  {
    localBitmap1 = paramBitmap;
    localBitmap2 = paramBitmap;
    try
    {
      paramString = this.i.open(paramString);
      localBitmap1 = paramBitmap;
      localBitmap2 = paramBitmap;
      paramBitmap = BitmapFactory.decodeStream(paramString);
      localBitmap1 = paramBitmap;
      localBitmap2 = paramBitmap;
      paramBitmap = a.a(paramBitmap, p.a);
      localBitmap1 = paramBitmap;
      localBitmap2 = paramBitmap;
      paramString.close();
    }
    catch (IOException paramBitmap)
    {
      for (;;)
      {
        cj.a(paramBitmap, "RouteOverlay", "getBitDes");
        paramBitmap = localBitmap1;
      }
    }
    catch (Exception paramBitmap)
    {
      for (;;)
      {
        cj.a(paramBitmap, "RouteOverlay", "getBitDes");
        paramBitmap = localBitmap2;
      }
    }
    return BitmapDescriptorFactory.fromBitmap(paramBitmap);
  }
  
  protected BitmapDescriptor getBusBitmapDescriptor()
  {
    return getBitDes(this.f, "amap_bus.png");
  }
  
  protected int getBusColor()
  {
    return Color.parseColor("#537edc");
  }
  
  protected BitmapDescriptor getDriveBitmapDescriptor()
  {
    return getBitDes(this.h, "amap_car.png");
  }
  
  protected int getDriveColor()
  {
    return Color.parseColor("#537edc");
  }
  
  protected BitmapDescriptor getEndBitmapDescriptor()
  {
    return getBitDes(this.e, "amap_end.png");
  }
  
  protected LatLngBounds getLatLngBounds()
  {
    LatLngBounds.Builder localBuilder = LatLngBounds.builder();
    localBuilder.include(new LatLng(this.startPoint.latitude, this.startPoint.longitude));
    localBuilder.include(new LatLng(this.endPoint.latitude, this.endPoint.longitude));
    return localBuilder.build();
  }
  
  protected BitmapDescriptor getStartBitmapDescriptor()
  {
    return getBitDes(this.d, "amap_start.png");
  }
  
  protected BitmapDescriptor getWalkBitmapDescriptor()
  {
    return getBitDes(this.g, "amap_man.png");
  }
  
  protected int getWalkColor()
  {
    return Color.parseColor("#6db74d");
  }
  
  public void removeFromMap()
  {
    label14:
    Iterator localIterator;
    if (this.a == null)
    {
      if (this.b != null) {
        break label67;
      }
      localIterator = this.stationMarkers.iterator();
      label24:
      if (localIterator.hasNext()) {
        break label77;
      }
      localIterator = this.allPolyLines.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        a();
        return;
        this.a.remove();
        break;
        label67:
        this.b.remove();
        break label14;
        label77:
        ((Marker)localIterator.next()).remove();
        break label24;
      }
      ((Polyline)localIterator.next()).remove();
    }
  }
  
  public void setNodeIconVisibility(boolean paramBoolean)
  {
    this.mNodeIconVisible = paramBoolean;
    Iterator localIterator = this.stationMarkers.iterator();
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        this.mAMap.postInvalidate();
        return;
      }
      ((Marker)localIterator.next()).setVisible(paramBoolean);
    }
  }
  
  public void zoomToSpan()
  {
    if (this.startPoint == null) {}
    while (this.endPoint == null) {
      return;
    }
    if (this.mAMap != null)
    {
      LatLngBounds localLatLngBounds = getLatLngBounds();
      this.mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(localLatLngBounds, 50));
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/overlay/b.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */