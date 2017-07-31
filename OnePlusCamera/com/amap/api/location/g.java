package com.amap.api.location;

public class g
{
  long a;
  public AMapLocationListener b;
  Boolean c;
  
  public g(long paramLong, float paramFloat, AMapLocationListener paramAMapLocationListener, String paramString, boolean paramBoolean)
  {
    this.a = paramLong;
    this.b = paramAMapLocationListener;
    this.c = Boolean.valueOf(paramBoolean);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this != paramObject)
    {
      if (paramObject == null) {
        break label50;
      }
      if (getClass() != paramObject.getClass()) {
        break label52;
      }
      paramObject = (g)paramObject;
      if (this.b == null) {
        break label54;
      }
      if (!this.b.equals(((g)paramObject).b)) {
        break label63;
      }
    }
    label50:
    label52:
    label54:
    while (((g)paramObject).b == null)
    {
      return true;
      return true;
      return false;
      return false;
    }
    return false;
    label63:
    return false;
  }
  
  public int hashCode()
  {
    if (this.b != null) {}
    for (int i = this.b.hashCode();; i = 0) {
      return i + 31;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/g.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */