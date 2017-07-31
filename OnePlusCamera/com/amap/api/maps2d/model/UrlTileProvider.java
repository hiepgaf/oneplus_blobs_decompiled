package com.amap.api.maps2d.model;

import com.amap.api.mapcore2d.ch;
import com.amap.api.mapcore2d.cj;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public abstract class UrlTileProvider
  implements TileProvider
{
  private final int a;
  private final int b;
  
  public UrlTileProvider(int paramInt1, int paramInt2)
  {
    this.a = paramInt1;
    this.b = paramInt2;
  }
  
  private static long a(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['က'];
    int i;
    for (long l = 0L;; l += i)
    {
      i = paramInputStream.read(arrayOfByte);
      if (i == -1) {
        break;
      }
      paramOutputStream.write(arrayOfByte, 0, i);
    }
    return l;
  }
  
  private static byte[] a(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    a(paramInputStream, localByteArrayOutputStream);
    return localByteArrayOutputStream.toByteArray();
  }
  
  public final Tile getTile(int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject = getTileUrl(paramInt1, paramInt2, paramInt3);
    if (localObject != null) {
      ch.a("UrlTileProvider", "url: " + ((URL)localObject).toString(), 111);
    }
    try
    {
      localObject = new Tile(this.a, this.b, a(((URL)localObject).openStream()));
      return (Tile)localObject;
    }
    catch (IOException localIOException)
    {
      cj.a(localIOException, "UrlTileProvider", "getTile");
    }
    return NO_TILE;
    return NO_TILE;
  }
  
  public int getTileHeight()
  {
    return this.b;
  }
  
  public abstract URL getTileUrl(int paramInt1, int paramInt2, int paramInt3);
  
  public int getTileWidth()
  {
    return this.a;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/UrlTileProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */