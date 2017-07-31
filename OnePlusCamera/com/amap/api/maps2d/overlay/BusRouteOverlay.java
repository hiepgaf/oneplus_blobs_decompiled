package com.amap.api.maps2d.overlay;

import android.content.Context;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteBusWalkItem;
import com.amap.api.services.route.WalkStep;
import java.util.Iterator;
import java.util.List;

public class BusRouteOverlay
  extends b
{
  private BusPath a;
  private LatLng b;
  
  public BusRouteOverlay(Context paramContext, AMap paramAMap, BusPath paramBusPath, LatLonPoint paramLatLonPoint1, LatLonPoint paramLatLonPoint2)
  {
    super(paramContext);
    this.a = paramBusPath;
    this.startPoint = a.a(paramLatLonPoint1);
    this.endPoint = a.a(paramLatLonPoint2);
    this.mAMap = paramAMap;
  }
  
  private Polyline a(LatLng paramLatLng1, LatLng paramLatLng2)
  {
    return this.mAMap.addPolyline(new PolylineOptions().add(new LatLng[] { paramLatLng1, paramLatLng2 }).width(getBuslineWidth()).color(getWalkColor()));
  }
  
  private Polyline a(LatLonPoint paramLatLonPoint1, LatLonPoint paramLatLonPoint2)
  {
    paramLatLonPoint1 = a.a(paramLatLonPoint1);
    paramLatLonPoint2 = a.a(paramLatLonPoint2);
    if (this.mAMap == null) {
      return null;
    }
    return a(paramLatLonPoint1, paramLatLonPoint2);
  }
  
  private Polyline a(List<LatLng> paramList)
  {
    return this.mAMap.addPolyline(new PolylineOptions().addAll(paramList).color(getWalkColor()).width(getBuslineWidth()));
  }
  
  private void a(LatLng paramLatLng, String paramString1, String paramString2)
  {
    paramLatLng = this.mAMap.addMarker(new MarkerOptions().position(paramLatLng).title(paramString1).snippet(paramString2).visible(this.mNodeIconVisible).anchor(0.5F, 0.5F).icon(getWalkBitmapDescriptor()));
    this.stationMarkers.add(paramLatLng);
  }
  
  private void a(BusStep paramBusStep)
  {
    paramBusStep = paramBusStep.getWalk().getSteps();
    int i = 0;
    if (i >= paramBusStep.size()) {
      return;
    }
    Object localObject1 = (WalkStep)paramBusStep.get(i);
    label36:
    Object localObject2;
    if (i != 0)
    {
      localObject1 = a.a(((WalkStep)localObject1).getPolyline());
      this.b = ((LatLng)((List)localObject1).get(((List)localObject1).size() - 1));
      localObject2 = a((List)localObject1);
      this.allPolyLines.add(localObject2);
      if (i < paramBusStep.size() - 1) {
        break label135;
      }
    }
    for (;;)
    {
      i += 1;
      break;
      a(a.a((LatLonPoint)((WalkStep)localObject1).getPolyline().get(0)), ((WalkStep)localObject1).getRoad(), b(paramBusStep));
      break label36;
      label135:
      localObject1 = (LatLng)((List)localObject1).get(((List)localObject1).size() - 1);
      localObject2 = a.a((LatLonPoint)((WalkStep)paramBusStep.get(i + 1)).getPolyline().get(0));
      if (!((LatLng)localObject1).equals(localObject2))
      {
        localObject1 = a((LatLng)localObject1, (LatLng)localObject2);
        this.allPolyLines.add(localObject1);
      }
    }
  }
  
  private void a(BusStep paramBusStep1, BusStep paramBusStep2)
  {
    paramBusStep1 = a.a(e(paramBusStep1));
    paramBusStep2 = a.a(f(paramBusStep2));
    if (paramBusStep2.latitude - paramBusStep1.latitude > 1.0E-4D) {}
    for (int i = 1;; i = 0)
    {
      if ((i != 0) || (paramBusStep2.longitude - paramBusStep1.longitude > 1.0E-4D)) {
        drawLineArrow(paramBusStep1, paramBusStep2);
      }
      return;
    }
  }
  
  private void a(RouteBusLineItem paramRouteBusLineItem)
  {
    paramRouteBusLineItem = a.a(paramRouteBusLineItem.getPolyline());
    paramRouteBusLineItem = this.mAMap.addPolyline(new PolylineOptions().addAll(paramRouteBusLineItem).color(getBusColor()).width(getBuslineWidth()));
    this.allPolyLines.add(paramRouteBusLineItem);
  }
  
  private String b(List<WalkStep> paramList)
  {
    paramList = paramList.iterator();
    for (float f = 0.0F;; f = ((WalkStep)paramList.next()).getDistance() + f) {
      if (!paramList.hasNext()) {
        return "步行" + f + "米";
      }
    }
  }
  
  private void b(BusStep paramBusStep)
  {
    LatLonPoint localLatLonPoint = d(paramBusStep);
    paramBusStep = f(paramBusStep);
    if (localLatLonPoint.equals(paramBusStep)) {
      return;
    }
    paramBusStep = a(localLatLonPoint, paramBusStep);
    this.allPolyLines.add(paramBusStep);
  }
  
  private void b(BusStep paramBusStep1, BusStep paramBusStep2)
  {
    paramBusStep1 = a.a(e(paramBusStep1));
    paramBusStep2 = a.a(f(paramBusStep2));
    if (paramBusStep1.equals(paramBusStep2)) {
      return;
    }
    drawLineArrow(paramBusStep1, paramBusStep2);
  }
  
  private void b(RouteBusLineItem paramRouteBusLineItem)
  {
    BusStationItem localBusStationItem = paramRouteBusLineItem.getDepartureBusStation();
    paramRouteBusLineItem = this.mAMap.addMarker(new MarkerOptions().position(a.a(localBusStationItem.getLatLonPoint())).title(paramRouteBusLineItem.getBusLineName()).snippet(c(paramRouteBusLineItem)).anchor(0.5F, 0.5F).visible(this.mNodeIconVisible).icon(getBusBitmapDescriptor()));
    this.stationMarkers.add(paramRouteBusLineItem);
  }
  
  private LatLonPoint c(BusStep paramBusStep)
  {
    return (LatLonPoint)((WalkStep)paramBusStep.getWalk().getSteps().get(0)).getPolyline().get(0);
  }
  
  private String c(RouteBusLineItem paramRouteBusLineItem)
  {
    return "(" + paramRouteBusLineItem.getDepartureBusStation().getBusStationName() + "-->" + paramRouteBusLineItem.getArrivalBusStation().getBusStationName() + ") 经过" + (paramRouteBusLineItem.getPassStationNum() + 1) + "站";
  }
  
  private void c(BusStep paramBusStep1, BusStep paramBusStep2)
  {
    paramBusStep1 = e(paramBusStep1);
    paramBusStep2 = c(paramBusStep2);
    if (paramBusStep1.equals(paramBusStep2)) {
      return;
    }
    paramBusStep1 = a(paramBusStep1, paramBusStep2);
    this.allPolyLines.add(paramBusStep1);
  }
  
  private LatLonPoint d(BusStep paramBusStep)
  {
    paramBusStep = paramBusStep.getWalk().getSteps();
    paramBusStep = ((WalkStep)paramBusStep.get(paramBusStep.size() - 1)).getPolyline();
    return (LatLonPoint)paramBusStep.get(paramBusStep.size() - 1);
  }
  
  private LatLonPoint e(BusStep paramBusStep)
  {
    paramBusStep = paramBusStep.getBusLine().getPolyline();
    return (LatLonPoint)paramBusStep.get(paramBusStep.size() - 1);
  }
  
  private LatLonPoint f(BusStep paramBusStep)
  {
    return (LatLonPoint)paramBusStep.getBusLine().getPolyline().get(0);
  }
  
  public void addToMap()
  {
    for (;;)
    {
      int i;
      Object localObject1;
      Object localObject2;
      try
      {
        List localList = this.a.getSteps();
        i = 0;
        if (i >= localList.size())
        {
          addStartAndEndMarker();
          return;
        }
        localObject1 = (BusStep)localList.get(i);
        if (i >= localList.size() - 1)
        {
          if (((BusStep)localObject1).getWalk() != null) {
            break label205;
          }
          if (((BusStep)localObject1).getBusLine() == null) {
            break label228;
          }
          if (((BusStep)localObject1).getBusLine() != null) {
            break label257;
          }
          break label275;
        }
        localObject2 = (BusStep)localList.get(i + 1);
        if (((BusStep)localObject1).getWalk() == null)
        {
          if (((BusStep)localObject1).getBusLine() != null) {
            break label161;
          }
          if (((BusStep)localObject1).getBusLine() != null) {
            break label179;
          }
          if ((((BusStep)localObject1).getBusLine() == null) || (((BusStep)localObject2).getWalk() != null) || (((BusStep)localObject2).getBusLine() == null)) {
            continue;
          }
          a((BusStep)localObject1, (BusStep)localObject2);
          continue;
        }
        if (((BusStep)localObject1).getBusLine() == null) {
          continue;
        }
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
        return;
      }
      b((BusStep)localObject1);
      continue;
      label161:
      if (((BusStep)localObject2).getWalk() != null)
      {
        c((BusStep)localObject1, (BusStep)localObject2);
        continue;
        label179:
        if ((((BusStep)localObject2).getWalk() == null) && (((BusStep)localObject2).getBusLine() != null))
        {
          b((BusStep)localObject1, (BusStep)localObject2);
          continue;
          label205:
          if (((BusStep)localObject1).getWalk().getSteps().size() > 0)
          {
            a((BusStep)localObject1);
            continue;
            label228:
            localObject2 = a(this.b, this.endPoint);
            this.allPolyLines.add(localObject2);
            continue;
            label257:
            localObject1 = ((BusStep)localObject1).getBusLine();
            a((RouteBusLineItem)localObject1);
            b((RouteBusLineItem)localObject1);
            label275:
            i += 1;
          }
        }
      }
    }
  }
  
  public void drawLineArrow(LatLng paramLatLng1, LatLng paramLatLng2)
  {
    this.mAMap.addPolyline(new PolylineOptions().add(new LatLng[] { paramLatLng1, paramLatLng2 }).width(3.0F).color(getBusColor()).width(getBuslineWidth()));
  }
  
  protected float getBuslineWidth()
  {
    return 18.0F;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/overlay/BusRouteOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */