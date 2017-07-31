package com.amap.api.mapcore2d;

import java.util.HashMap;
import java.util.Map;

public class da
  extends ee
{
  private byte[] a;
  private String b = "1";
  
  public da(byte[] paramArrayOfByte)
  {
    this.a = ((byte[])paramArrayOfByte.clone());
  }
  
  public da(byte[] paramArrayOfByte, String paramString)
  {
    this.a = ((byte[])paramArrayOfByte.clone());
    this.b = paramString;
  }
  
  private String b()
  {
    byte[] arrayOfByte1 = cv.a(cx.a);
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 50];
    System.arraycopy(this.a, 0, arrayOfByte2, 0, 50);
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 50, arrayOfByte1.length);
    return cr.a(arrayOfByte2);
  }
  
  public byte[] a_()
  {
    return this.a;
  }
  
  public Map<String, String> e()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("Content-Type", "application/zip");
    localHashMap.put("Content-Length", String.valueOf(this.a.length));
    return localHashMap;
  }
  
  public Map<String, String> f()
  {
    return null;
  }
  
  public String g()
  {
    return String.format(cx.b, new Object[] { "1", this.b, "1", "open", b() });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/da.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */