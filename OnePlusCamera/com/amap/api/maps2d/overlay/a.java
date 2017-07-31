package com.amap.api.maps2d.overlay;

import android.graphics.Bitmap;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class a
{
  private static int a = 2048;
  
  public static Bitmap a(Bitmap paramBitmap, float paramFloat)
  {
    if (paramBitmap != null) {
      return Bitmap.createScaledBitmap(paramBitmap, (int)(paramBitmap.getWidth() * paramFloat), (int)(paramBitmap.getHeight() * paramFloat), true);
    }
    return null;
  }
  
  public static LatLng a(LatLonPoint paramLatLonPoint)
  {
    return new LatLng(paramLatLonPoint.getLatitude(), paramLatLonPoint.getLongitude());
  }
  
  public static ArrayList<LatLng> a(List<LatLonPoint> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    paramList = paramList.iterator();
    for (;;)
    {
      if (!paramList.hasNext()) {
        return localArrayList;
      }
      localArrayList.add(a((LatLonPoint)paramList.next()));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/overlay/a.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */