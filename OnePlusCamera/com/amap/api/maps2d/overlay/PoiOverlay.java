package com.amap.api.maps2d.overlay;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.LatLngBounds.Builder;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PoiOverlay
{
  private List<PoiItem> a;
  private AMap b;
  private ArrayList<Marker> c = new ArrayList();
  
  public PoiOverlay(AMap paramAMap, List<PoiItem> paramList)
  {
    this.b = paramAMap;
    this.a = paramList;
  }
  
  private LatLngBounds a()
  {
    LatLngBounds.Builder localBuilder = LatLngBounds.builder();
    int i = 0;
    for (;;)
    {
      if (i >= this.a.size()) {
        return localBuilder.build();
      }
      localBuilder.include(new LatLng(((PoiItem)this.a.get(i)).getLatLonPoint().getLatitude(), ((PoiItem)this.a.get(i)).getLatLonPoint().getLongitude()));
      i += 1;
    }
  }
  
  private MarkerOptions a(int paramInt)
  {
    return new MarkerOptions().position(new LatLng(((PoiItem)this.a.get(paramInt)).getLatLonPoint().getLatitude(), ((PoiItem)this.a.get(paramInt)).getLatLonPoint().getLongitude())).title(getTitle(paramInt)).snippet(getSnippet(paramInt)).icon(getBitmapDescriptor(paramInt));
  }
  
  public void addToMap()
  {
    int i = 0;
    for (;;)
    {
      if (i >= this.a.size()) {
        return;
      }
      Marker localMarker = this.b.addMarker(a(i));
      localMarker.setObject(Integer.valueOf(i));
      this.c.add(localMarker);
      i += 1;
    }
  }
  
  protected BitmapDescriptor getBitmapDescriptor(int paramInt)
  {
    return null;
  }
  
  public int getPoiIndex(Marker paramMarker)
  {
    int i = 0;
    for (;;)
    {
      if (i >= this.c.size()) {
        return -1;
      }
      if (((Marker)this.c.get(i)).equals(paramMarker)) {
        break;
      }
      i += 1;
    }
    return i;
  }
  
  public PoiItem getPoiItem(int paramInt)
  {
    if (paramInt < 0) {}
    while (paramInt >= this.a.size()) {
      return null;
    }
    return (PoiItem)this.a.get(paramInt);
  }
  
  protected String getSnippet(int paramInt)
  {
    return ((PoiItem)this.a.get(paramInt)).getSnippet();
  }
  
  protected String getTitle(int paramInt)
  {
    return ((PoiItem)this.a.get(paramInt)).getTitle();
  }
  
  public void removeFromMap()
  {
    Iterator localIterator = this.c.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      ((Marker)localIterator.next()).remove();
    }
  }
  
  public void zoomToSpan()
  {
    if (this.a == null) {}
    while (this.a.size() <= 0) {
      return;
    }
    if (this.b != null)
    {
      LatLngBounds localLatLngBounds = a();
      this.b.moveCamera(CameraUpdateFactory.newLatLngBounds(localLatLngBounds, 5));
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/overlay/PoiOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */