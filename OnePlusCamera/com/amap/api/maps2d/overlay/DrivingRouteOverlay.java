package com.amap.api.maps2d.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.LatLngBounds.Builder;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveStep;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DrivingRouteOverlay
  extends b
{
  private DrivePath a;
  private Bitmap b;
  private List<LatLonPoint> c;
  private boolean d = true;
  protected List<Marker> mPassByMarkers = new ArrayList();
  
  public DrivingRouteOverlay(Context paramContext, AMap paramAMap, DrivePath paramDrivePath, LatLonPoint paramLatLonPoint1, LatLonPoint paramLatLonPoint2)
  {
    super(paramContext);
    this.mAMap = paramAMap;
    this.a = paramDrivePath;
    this.startPoint = a.a(paramLatLonPoint1);
    this.endPoint = a.a(paramLatLonPoint2);
  }
  
  public DrivingRouteOverlay(Context paramContext, AMap paramAMap, DrivePath paramDrivePath, LatLonPoint paramLatLonPoint1, LatLonPoint paramLatLonPoint2, List<LatLonPoint> paramList)
  {
    super(paramContext);
    this.mAMap = paramAMap;
    this.a = paramDrivePath;
    this.startPoint = a.a(paramLatLonPoint1);
    this.endPoint = a.a(paramLatLonPoint2);
    this.c = paramList;
  }
  
  private void a()
  {
    if (this.c == null) {}
    while (this.c.size() == 0) {
      return;
    }
    Iterator localIterator = this.c.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      Object localObject = a.a((LatLonPoint)localIterator.next());
      localObject = this.mAMap.addMarker(new MarkerOptions().position((LatLng)localObject).title("途经点").visible(this.d).icon(getPassedByBitmapDescriptor()));
      this.mPassByMarkers.add(localObject);
    }
  }
  
  public void addToMap()
  {
    List localList = this.a.getSteps();
    int i = 0;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    for (;;)
    {
      if (i >= localList.size())
      {
        a();
        addStartAndEndMarker();
        return;
      }
      localObject1 = (DriveStep)localList.get(i);
      localObject2 = a.a((LatLonPoint)((DriveStep)localObject1).getPolyline().get(0));
      if (i < localList.size() - 1) {
        break;
      }
      localObject3 = a.a((LatLonPoint)((DriveStep)localObject1).getPolyline().get(((DriveStep)localObject1).getPolyline().size() - 1));
      localObject3 = this.mAMap.addPolyline(new PolylineOptions().add(new LatLng[] { localObject3, this.endPoint }).color(getDriveColor()).width(getBuslineWidth()));
      this.allPolyLines.add(localObject3);
      localObject2 = this.mAMap.addMarker(new MarkerOptions().position((LatLng)localObject2).title("方向:" + ((DriveStep)localObject1).getAction() + "\n道路:" + ((DriveStep)localObject1).getRoad()).snippet(((DriveStep)localObject1).getInstruction()).anchor(0.5F, 0.5F).visible(this.mNodeIconVisible).icon(getDriveBitmapDescriptor()));
      this.stationMarkers.add(localObject2);
      localObject1 = this.mAMap.addPolyline(new PolylineOptions().addAll(a.a(((DriveStep)localObject1).getPolyline())).color(getDriveColor()).width(getBuslineWidth()));
      this.allPolyLines.add(localObject1);
      i += 1;
    }
    if (i != 0) {}
    for (;;)
    {
      localObject3 = a.a((LatLonPoint)((DriveStep)localObject1).getPolyline().get(((DriveStep)localObject1).getPolyline().size() - 1));
      LatLng localLatLng = a.a((LatLonPoint)((DriveStep)localList.get(i + 1)).getPolyline().get(0));
      if (((LatLng)localObject3).equals(localLatLng)) {
        break;
      }
      localObject3 = this.mAMap.addPolyline(new PolylineOptions().add(new LatLng[] { localObject3, localLatLng }).color(getDriveColor()).width(getBuslineWidth()));
      this.allPolyLines.add(localObject3);
      break;
      localObject3 = this.mAMap.addPolyline(new PolylineOptions().add(new LatLng[] { this.startPoint, localObject2 }).color(getDriveColor()).width(getBuslineWidth()));
      this.allPolyLines.add(localObject3);
    }
  }
  
  protected float getBuslineWidth()
  {
    return 18.0F;
  }
  
  protected LatLngBounds getLatLngBounds()
  {
    LatLngBounds.Builder localBuilder = LatLngBounds.builder();
    localBuilder.include(new LatLng(this.startPoint.latitude, this.startPoint.longitude));
    localBuilder.include(new LatLng(this.endPoint.latitude, this.endPoint.longitude));
    if (this.c == null) {}
    for (;;)
    {
      return localBuilder.build();
      if (this.c.size() > 0)
      {
        int i = 0;
        while (i < this.c.size())
        {
          localBuilder.include(new LatLng(((LatLonPoint)this.c.get(i)).getLatitude(), ((LatLonPoint)this.c.get(i)).getLongitude()));
          i += 1;
        }
      }
    }
  }
  
  protected BitmapDescriptor getPassedByBitmapDescriptor()
  {
    return getBitDes(this.b, "amap_throughpoint.png");
  }
  
  public void removeFromMap()
  {
    super.removeFromMap();
    Iterator localIterator = this.mPassByMarkers.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      ((Marker)localIterator.next()).remove();
    }
  }
  
  public void setThroughPointIconVisibility(boolean paramBoolean)
  {
    this.d = paramBoolean;
    Iterator localIterator = this.mPassByMarkers.iterator();
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
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/overlay/DrivingRouteOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */