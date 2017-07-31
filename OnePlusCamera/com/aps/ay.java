package com.aps;

import android.location.Location;
import android.net.wifi.ScanResult;
import android.telephony.CellLocation;
import java.util.List;

public final class ay
{
  private static int c = 10;
  private static int d = 100;
  private static float f = 0.5F;
  protected bc a = new bc(this);
  protected az b = new az(this);
  private ak e;
  
  protected ay(ak paramak)
  {
    this.e = paramak;
  }
  
  protected static void a() {}
  
  protected static void a(int paramInt)
  {
    c = paramInt;
  }
  
  protected static void b(int paramInt)
  {
    d = paramInt;
  }
  
  protected final boolean a(Location paramLocation)
  {
    ba localba = null;
    boolean bool = false;
    List localList;
    if (this.e != null)
    {
      localList = this.e.j();
      if (localList != null) {
        break label30;
      }
    }
    label30:
    while (paramLocation == null)
    {
      return false;
      return false;
    }
    new StringBuilder("cell.list.size: ").append(localList.size()).toString();
    if (localList.size() < 2)
    {
      paramLocation = localba;
      if (!bool) {
        return bool;
      }
    }
    else
    {
      localba = new ba((CellLocation)localList.get(1));
      if (this.b.b != null)
      {
        if (paramLocation.distanceTo(this.b.b) <= d) {
          break label163;
        }
        bool = true;
        label128:
        if (!bool) {
          break label168;
        }
      }
      for (;;)
      {
        new StringBuilder("collect cell?: ").append(bool).toString();
        paramLocation = localba;
        break;
        paramLocation = localba;
        bool = true;
        break;
        label163:
        bool = false;
        break label128;
        label168:
        paramLocation = this.b.a;
        if (localba.e != paramLocation.e) {}
        label188:
        for (int i = 0;; i = 1)
        {
          if (i == 0) {
            break label252;
          }
          bool = false;
          break;
          if ((localba.d != paramLocation.d) || (localba.c != paramLocation.c) || (localba.b != paramLocation.b) || (localba.a != paramLocation.a)) {
            break label188;
          }
        }
        label252:
        bool = true;
      }
    }
    this.b.a = paramLocation;
    return bool;
  }
  
  protected final boolean b(Location paramLocation)
  {
    int m = 0;
    List localList;
    boolean bool1;
    if (this.e != null)
    {
      localList = this.e.k();
      if (localList.size() >= 2) {
        break label45;
      }
      paramLocation = null;
      bool1 = false;
      if (bool1) {
        break label341;
      }
    }
    for (;;)
    {
      return bool1;
      return false;
      label45:
      localList = (List)localList.get(1);
      if (this.a.b != null)
      {
        if (localList != null) {
          break label91;
        }
        label73:
        paramLocation = localList;
        bool1 = false;
        break;
      }
      bool1 = true;
      label85:
      label91:
      boolean bool2;
      label123:
      do
      {
        paramLocation = localList;
        break;
        if (localList.size() <= 0) {
          break label73;
        }
        if (paramLocation.distanceTo(this.a.b) <= c) {
          break label163;
        }
        bool2 = true;
        bool1 = bool2;
      } while (bool2);
      paramLocation = this.a.a;
      float f1 = f;
      if (localList == null) {}
      label149:
      label152:
      label163:
      int i1;
      label252:
      label258:
      for (;;)
      {
        i = 0;
        int n;
        float f2;
        if (i != 0)
        {
          bool1 = false;
          break label85;
          bool2 = false;
          break label123;
          if ((paramLocation == null) || (localList == null) || (paramLocation == null)) {
            continue;
          }
          n = localList.size();
          i1 = paramLocation.size();
          f2 = n + i1;
          if (n == 0) {
            break label252;
          }
        }
        for (;;)
        {
          if ((n == 0) || (i1 == 0)) {
            break label258;
          }
          j = 0;
          i = 0;
          if (j < n) {
            break label260;
          }
          if (i << 1 < f2 * f1) {
            break label149;
          }
          do
          {
            i = 1;
            break label152;
            break;
          } while (i1 == 0);
        }
      }
      label260:
      String str = ((ScanResult)localList.get(j)).BSSID;
      if (str == null) {}
      for (;;)
      {
        j += 1;
        break;
        int k = 0;
        while (k < i1)
        {
          if (str.equals(((bb)paramLocation.get(k)).a)) {
            break label332;
          }
          k += 1;
        }
        continue;
        label332:
        i += 1;
      }
      label341:
      this.a.a.clear();
      int j = paramLocation.size();
      int i = m;
      while (i < j)
      {
        this.a.a.add(new bb(((ScanResult)paramLocation.get(i)).BSSID));
        i += 1;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/ay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */