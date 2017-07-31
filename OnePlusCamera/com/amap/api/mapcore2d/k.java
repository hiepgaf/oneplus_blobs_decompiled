package com.amap.api.mapcore2d;

import java.util.ArrayList;
import java.util.Iterator;

class k
{
  private static k a = new k();
  private ArrayList<a> b = new ArrayList();
  
  public static k a()
  {
    return a;
  }
  
  public void a(a parama)
  {
    if (parama == null) {
      return;
    }
    this.b.add(parama);
  }
  
  public void b()
  {
    Iterator localIterator = this.b.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      a locala = (a)localIterator.next();
      if (locala != null) {
        locala.Q();
      }
    }
  }
  
  public void b(a parama)
  {
    if (parama == null) {
      return;
    }
    this.b.remove(parama);
  }
  
  public static abstract interface a
  {
    public abstract void Q();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/k.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */