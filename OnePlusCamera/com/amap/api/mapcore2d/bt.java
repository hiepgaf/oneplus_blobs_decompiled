package com.amap.api.mapcore2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import com.amap.api.maps2d.AMapException;
import com.amap.api.maps2d.model.Tile;
import com.amap.api.maps2d.model.TileProvider;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class bt
  extends bh<ArrayList<bp>, ArrayList<bp>>
{
  private Context b;
  private am f = null;
  private TileProvider g;
  
  public bt(Context paramContext, ArrayList<bp> paramArrayList, TileProvider paramTileProvider)
  {
    super(paramArrayList);
    this.b = paramContext;
    this.g = paramTileProvider;
    a(cs.a(this.b));
    a(5000);
    b(50000);
  }
  
  private void a(bp parambp, int paramInt)
  {
    if (parambp == null) {}
    while (paramInt < 0) {
      return;
    }
    if (this.f == null) {}
    while (this.f.p == null) {
      return;
    }
    bk localbk = this.f.p;
    label104:
    label118:
    for (;;)
    {
      int j;
      int i;
      synchronized (this.f)
      {
        j = localbk.size();
        i = 0;
        break label104;
        return;
        bp localbp = (bp)localbk.get(i);
        if ((localbp != null) && (localbp.equals(parambp))) {
          localbp.h = paramInt;
        }
      }
      for (;;)
      {
        if (i < j) {
          break label118;
        }
        break;
        i += 1;
      }
    }
  }
  
  private byte[] a(Bitmap paramBitmap)
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      paramBitmap.compress(Bitmap.CompressFormat.PNG, 100, localByteArrayOutputStream);
      paramBitmap = localByteArrayOutputStream.toByteArray();
      return paramBitmap;
    }
    catch (Throwable paramBitmap)
    {
      cj.a(paramBitmap, "TileServerHandler", "Bitmap2Bytes");
    }
    return null;
  }
  
  public int a(byte[] paramArrayOfByte, bp parambp)
  {
    if (parambp == null) {}
    while (paramArrayOfByte == null) {
      return -1;
    }
    if (this.f == null) {}
    while (this.f.n == null) {
      return -1;
    }
    int i = this.f.n.a(null, paramArrayOfByte, false, null, parambp.c());
    if (i >= 0)
    {
      a(parambp, i);
      if (this.f == null) {
        break label80;
      }
      if (this.f.f == true) {
        break label82;
      }
    }
    label80:
    label82:
    do
    {
      return i;
      return -1;
      return i;
      paramArrayOfByte = a(this.f.n.a(i));
    } while ((this.f == null) || (this.f.o == null));
    this.f.o.a(paramArrayOfByte, parambp);
    return i;
  }
  
  public void a(am paramam)
  {
    this.f = paramam;
  }
  
  protected ArrayList<bp> b(byte[] paramArrayOfByte)
    throws AMapException
  {
    if (this.a == null) {}
    while (paramArrayOfByte == null) {
      return null;
    }
    int j = ((ArrayList)this.a).size();
    Object localObject = null;
    int i = 0;
    bp localbp;
    for (;;)
    {
      if (i >= j) {
        return (ArrayList<bp>)localObject;
      }
      localbp = (bp)((ArrayList)this.a).get(i);
      if (a(paramArrayOfByte, localbp) < 0) {
        break;
      }
      i += 1;
    }
    if (localObject != null)
    {
      label75:
      localbp = new bp(localbp);
      if (this.f.h) {
        break label119;
      }
    }
    for (;;)
    {
      ((ArrayList)localObject).add(localbp);
      break;
      localObject = new ArrayList();
      break label75;
      label119:
      if ((localbp.d > 9) && (!ci.a(localbp.b, localbp.c, localbp.d))) {
        localbp.i = true;
      }
    }
  }
  
  protected byte[] b()
    throws AMapException
  {
    if (this.g == null) {
      return super.b();
    }
    return this.g.getTile(((bp)((ArrayList)this.a).get(0)).b, ((bp)((ArrayList)this.a).get(0)).c, ((bp)((ArrayList)this.a).get(0)).d).data;
  }
  
  protected ArrayList<bp> d()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = ((ArrayList)this.a).iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return localArrayList;
      }
      localArrayList.add(new bp((bp)localIterator.next()));
    }
  }
  
  public Map<String, String> e()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("User-Agent", "AMAP_SDK_Android_2DMap_2.9.2");
    localHashMap.put("Accept-Encoding", "gzip");
    localHashMap.put("platinfo", String.format("platform=Android&sdkversion=%s&product=%s", new Object[] { "2.9.2", "2dmap" }));
    localHashMap.put("X-INFO", co.a(this.b));
    localHashMap.put("key", cl.f(this.b));
    localHashMap.put("logversion", "2.1");
    return localHashMap;
  }
  
  public Map<String, String> f()
  {
    return null;
  }
  
  public String g()
  {
    int j;
    int i;
    if (p.i != 0)
    {
      j = (int)Math.pow(2.0D, ((bp)((ArrayList)this.a).get(0)).d);
      i = ((bp)((ArrayList)this.a).get(0)).b;
      if (i >= j) {
        break label227;
      }
      if (i < 0) {
        break label234;
      }
    }
    for (;;)
    {
      String str = this.f.j.a(i, ((bp)((ArrayList)this.a).get(0)).c, ((bp)((ArrayList)this.a).get(0)).d);
      ((bp)((ArrayList)this.a).get(0)).b();
      return str;
      if (!((bp)((ArrayList)this.a).get(0)).i) {
        break;
      }
      return String.format(aw.a().d(), new Object[] { Integer.valueOf(((bp)((ArrayList)this.a).get(0)).b), Integer.valueOf(((bp)((ArrayList)this.a).get(0)).c), Integer.valueOf(((bp)((ArrayList)this.a).get(0)).d) });
      label227:
      i -= j;
      continue;
      label234:
      i += j;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */