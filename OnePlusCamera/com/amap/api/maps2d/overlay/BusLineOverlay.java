package com.amap.api.maps2d.overlay;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.LatLngBounds.Builder;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.core.LatLonPoint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BusLineOverlay
{
  private BusLineItem a;
  private AMap b;
  private ArrayList<Marker> c = new ArrayList();
  private Polyline d;
  private List<BusStationItem> e;
  private BitmapDescriptor f;
  private BitmapDescriptor g;
  private BitmapDescriptor h;
  private AssetManager i;
  private Context j;
  
  public BusLineOverlay(Context paramContext, AMap paramAMap, BusLineItem paramBusLineItem)
  {
    this.j = paramContext;
    this.a = paramBusLineItem;
    this.b = paramAMap;
    this.e = this.a.getBusStations();
    this.i = this.j.getResources().getAssets();
  }
  
  /* Error */
  private BitmapDescriptor a(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore_3
    //   5: aload_0
    //   6: getfield 62	com/amap/api/maps2d/overlay/BusLineOverlay:i	Landroid/content/res/AssetManager;
    //   9: aload_1
    //   10: invokevirtual 72	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   13: astore_2
    //   14: aload_2
    //   15: astore_1
    //   16: aload 4
    //   18: astore_3
    //   19: aload_2
    //   20: invokestatic 78	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;)Landroid/graphics/Bitmap;
    //   23: astore 4
    //   25: aload_2
    //   26: astore_1
    //   27: aload 4
    //   29: astore_3
    //   30: aload 4
    //   32: getstatic 83	com/amap/api/mapcore2d/p:a	F
    //   35: invokestatic 88	com/amap/api/mapcore2d/cj:a	(Landroid/graphics/Bitmap;F)Landroid/graphics/Bitmap;
    //   38: astore 4
    //   40: aload 4
    //   42: astore_1
    //   43: aload_2
    //   44: ifnonnull +8 -> 52
    //   47: aload_1
    //   48: invokestatic 94	com/amap/api/maps2d/model/BitmapDescriptorFactory:fromBitmap	(Landroid/graphics/Bitmap;)Lcom/amap/api/maps2d/model/BitmapDescriptor;
    //   51: areturn
    //   52: aload_2
    //   53: invokevirtual 99	java/io/InputStream:close	()V
    //   56: goto -9 -> 47
    //   59: astore_2
    //   60: aload_2
    //   61: ldc 101
    //   63: ldc 103
    //   65: invokestatic 106	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   68: goto -21 -> 47
    //   71: astore 4
    //   73: aconst_null
    //   74: astore_2
    //   75: aload_2
    //   76: astore_1
    //   77: aload 4
    //   79: ldc 101
    //   81: ldc 103
    //   83: invokestatic 106	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   86: aload_3
    //   87: astore_1
    //   88: aload_2
    //   89: ifnull -42 -> 47
    //   92: aload_2
    //   93: invokevirtual 99	java/io/InputStream:close	()V
    //   96: aload_3
    //   97: astore_1
    //   98: goto -51 -> 47
    //   101: astore_1
    //   102: aload_1
    //   103: ldc 101
    //   105: ldc 103
    //   107: invokestatic 106	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   110: aload_3
    //   111: astore_1
    //   112: goto -65 -> 47
    //   115: astore_2
    //   116: aconst_null
    //   117: astore_1
    //   118: aload_1
    //   119: ifnonnull +5 -> 124
    //   122: aload_2
    //   123: athrow
    //   124: aload_1
    //   125: invokevirtual 99	java/io/InputStream:close	()V
    //   128: goto -6 -> 122
    //   131: astore_1
    //   132: aload_1
    //   133: ldc 101
    //   135: ldc 103
    //   137: invokestatic 106	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   140: goto -18 -> 122
    //   143: astore_2
    //   144: goto -26 -> 118
    //   147: astore 4
    //   149: goto -74 -> 75
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	152	0	this	BusLineOverlay
    //   0	152	1	paramString	String
    //   13	40	2	localInputStream	java.io.InputStream
    //   59	2	2	localIOException1	java.io.IOException
    //   74	19	2	localObject1	Object
    //   115	8	2	localObject2	Object
    //   143	1	2	localObject3	Object
    //   4	107	3	localBitmap1	android.graphics.Bitmap
    //   1	40	4	localBitmap2	android.graphics.Bitmap
    //   71	7	4	localIOException2	java.io.IOException
    //   147	1	4	localIOException3	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   52	56	59	java/io/IOException
    //   5	14	71	java/io/IOException
    //   92	96	101	java/io/IOException
    //   5	14	115	finally
    //   124	128	131	java/io/IOException
    //   19	25	143	finally
    //   30	40	143	finally
    //   77	86	143	finally
    //   19	25	147	java/io/IOException
    //   30	40	147	java/io/IOException
  }
  
  private LatLngBounds a(List<LatLonPoint> paramList)
  {
    LatLngBounds.Builder localBuilder = LatLngBounds.builder();
    int k = 0;
    for (;;)
    {
      if (k >= paramList.size()) {
        return localBuilder.build();
      }
      localBuilder.include(new LatLng(((LatLonPoint)paramList.get(k)).getLatitude(), ((LatLonPoint)paramList.get(k)).getLongitude()));
      k += 1;
    }
  }
  
  private MarkerOptions a(int paramInt)
  {
    MarkerOptions localMarkerOptions = new MarkerOptions().position(new LatLng(((BusStationItem)this.e.get(paramInt)).getLatLonPoint().getLatitude(), ((BusStationItem)this.e.get(paramInt)).getLatLonPoint().getLongitude())).title(getTitle(paramInt)).snippet(getSnippet(paramInt));
    if (paramInt != 0)
    {
      if (paramInt != this.e.size() - 1)
      {
        localMarkerOptions.anchor(0.5F, 0.5F);
        localMarkerOptions.icon(getBusBitmapDescriptor());
        return localMarkerOptions;
      }
    }
    else
    {
      localMarkerOptions.icon(getStartBitmapDescriptor());
      return localMarkerOptions;
    }
    localMarkerOptions.icon(getEndBitmapDescriptor());
    return localMarkerOptions;
  }
  
  private void a()
  {
    if (this.f == null) {
      if (this.g != null) {
        break label37;
      }
    }
    for (;;)
    {
      if (this.h != null) {
        break label52;
      }
      return;
      this.f.recycle();
      this.f = null;
      break;
      label37:
      this.g.recycle();
      this.g = null;
    }
    label52:
    this.h.recycle();
    this.h = null;
  }
  
  public void addToMap()
  {
    int k = 1;
    Object localObject = a.a(this.a.getDirectionsCoordinates());
    this.d = this.b.addPolyline(new PolylineOptions().addAll((Iterable)localObject).color(getBusColor()).width(getBuslineWidth()));
    if (this.e.size() >= 1) {}
    for (;;)
    {
      if (k >= this.e.size() - 1)
      {
        localObject = this.b.addMarker(a(0));
        this.c.add(localObject);
        localObject = this.b.addMarker(a(this.e.size() - 1));
        this.c.add(localObject);
        return;
        return;
      }
      localObject = this.b.addMarker(a(k));
      this.c.add(localObject);
      k += 1;
    }
  }
  
  protected BitmapDescriptor getBusBitmapDescriptor()
  {
    this.h = a("amap_bus.png");
    return this.h;
  }
  
  protected int getBusColor()
  {
    return Color.parseColor("#537edc");
  }
  
  public int getBusStationIndex(Marker paramMarker)
  {
    int k = 0;
    for (;;)
    {
      if (k >= this.c.size()) {
        return -1;
      }
      if (((Marker)this.c.get(k)).equals(paramMarker)) {
        break;
      }
      k += 1;
    }
    return k;
  }
  
  public BusStationItem getBusStationItem(int paramInt)
  {
    if (paramInt < 0) {}
    while (paramInt >= this.e.size()) {
      return null;
    }
    return (BusStationItem)this.e.get(paramInt);
  }
  
  protected float getBuslineWidth()
  {
    return 18.0F;
  }
  
  protected BitmapDescriptor getEndBitmapDescriptor()
  {
    this.g = a("amap_end.png");
    return this.g;
  }
  
  protected String getSnippet(int paramInt)
  {
    return "";
  }
  
  protected BitmapDescriptor getStartBitmapDescriptor()
  {
    this.f = a("amap_start.png");
    return this.f;
  }
  
  protected String getTitle(int paramInt)
  {
    return ((BusStationItem)this.e.get(paramInt)).getBusStationName();
  }
  
  public void removeFromMap()
  {
    Iterator localIterator;
    if (this.d == null) {
      localIterator = this.c.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        a();
        return;
        this.d.remove();
        break;
      }
      ((Marker)localIterator.next()).remove();
    }
  }
  
  public void zoomToSpan()
  {
    if (this.b != null)
    {
      localObject = this.a.getDirectionsCoordinates();
      if (localObject != null) {
        break label21;
      }
    }
    label21:
    while (((List)localObject).size() <= 0)
    {
      return;
      return;
    }
    Object localObject = a((List)localObject);
    this.b.moveCamera(CameraUpdateFactory.newLatLngBounds((LatLngBounds)localObject, 5));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/overlay/BusLineOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */