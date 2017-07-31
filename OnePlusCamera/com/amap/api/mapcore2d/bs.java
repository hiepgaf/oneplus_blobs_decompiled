package com.amap.api.mapcore2d;

import android.content.Context;
import com.amap.api.maps2d.AMapException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class bs
  extends e<bp, bp>
  implements bz
{
  private Context d;
  private am e;
  private ay f;
  private al g = new al();
  
  public bs(ay paramay, Context paramContext, am paramam)
  {
    super(paramay, paramContext);
    this.e = paramam;
    this.d = paramContext;
    this.a = new bo();
    paramay.c.a(this);
    this.f = paramay;
    a();
  }
  
  private ArrayList<bp> a(ArrayList<bp> paramArrayList, am paramam, float paramFloat, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramArrayList == null) {}
    while (paramam == null) {
      return null;
    }
    if (paramam.a())
    {
      if (paramam.p == null) {
        break label72;
      }
      paramam.p.clear();
      if (paramFloat <= paramam.c) {
        break label74;
      }
    }
    label72:
    label74:
    for (int i = 1; (i != 0) || (paramFloat < paramam.d); i = 0)
    {
      return null;
      return null;
      return null;
    }
    int j = paramArrayList.size();
    if (j > 0)
    {
      i = 0;
      if (i >= j) {
        return localArrayList;
      }
    }
    else
    {
      return null;
    }
    bp localbp1 = (bp)paramArrayList.get(i);
    bp localbp2;
    if (localbp1 != null)
    {
      int k = paramam.n.a(localbp1.c());
      localbp2 = new bp(localbp1.b, localbp1.c, localbp1.d, paramam.l);
      localbp2.h = k;
      localbp2.g = localbp1.g;
      paramam.p.add(localbp2);
      if (a(localbp2)) {
        break label209;
      }
    }
    label209:
    while ((paramBoolean) || (this.g.contains(localbp2)))
    {
      i += 1;
      break;
    }
    if (paramam.f) {}
    for (;;)
    {
      localArrayList.add(localbp2);
      break;
      localbp2.a = -1;
    }
  }
  
  private void a(ArrayList<bp> paramArrayList, boolean paramBoolean)
  {
    if (this.a == null) {}
    while (paramArrayList == null) {
      return;
    }
    if (paramArrayList.size() == 0) {
      return;
    }
    this.a.a(paramArrayList, paramBoolean);
  }
  
  private boolean a(bp parambp)
  {
    if (parambp == null) {}
    while (parambp.h < 0) {
      return true;
    }
    return false;
  }
  
  private void c(ArrayList<bp> paramArrayList)
  {
    if (paramArrayList == null) {}
    while (this.g == null) {
      return;
    }
    int j = paramArrayList.size();
    int i;
    if (j != 0) {
      i = 0;
    }
    for (;;)
    {
      if (i >= j)
      {
        return;
        return;
      }
      this.g.a((bp)paramArrayList.get(i));
      i += 1;
    }
  }
  
  private boolean j()
  {
    if (this.b == null) {}
    while (this.b.e == null) {
      return false;
    }
    if (this.b.e.a != null)
    {
      if (this.b.e.a.size() > 0) {
        return this.e.a();
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  protected ArrayList<bp> a(ArrayList<bp> paramArrayList)
    throws AMapException
  {
    if (paramArrayList == null) {}
    while (paramArrayList.size() == 0) {
      return null;
    }
    if (this.b == null) {}
    while ((this.b.e == null) || (this.b.e.a == null)) {
      return null;
    }
    a(paramArrayList);
    ArrayList localArrayList;
    if (paramArrayList.size() != 0)
    {
      if (this.e.j == null) {
        break label124;
      }
      bt localbt = new bt(this.d, paramArrayList, this.e.k);
      localbt.a(this.e);
      localArrayList = (ArrayList)localbt.a();
      localbt.a(null);
      label108:
      c(paramArrayList);
      if (this.b != null) {
        break label139;
      }
    }
    label124:
    label139:
    while (this.b.e == null)
    {
      return localArrayList;
      return null;
      if (this.e.k != null) {
        break;
      }
      localArrayList = null;
      break label108;
    }
    this.b.e.b();
    return localArrayList;
  }
  
  public void a(List<bp> paramList)
  {
    int j = 0;
    int i;
    if (paramList != null)
    {
      i = paramList.size();
      if (i != 0) {
        if (j < i) {
          break label25;
        }
      }
    }
    else
    {
      return;
    }
    return;
    label25:
    if (this.g.b((bp)paramList.get(j))) {}
    for (;;)
    {
      j += 1;
      break;
      paramList.remove(j);
      j -= 1;
      i -= 1;
    }
  }
  
  public void a(boolean paramBoolean1, boolean paramBoolean2)
  {
    ArrayList localArrayList1;
    ArrayList localArrayList2;
    if (j())
    {
      localArrayList1 = this.b.i.a(this.b.i.l, this.b.i.b(), this.b.c.c(), this.b.c.d());
      if (localArrayList1 == null) {
        break label115;
      }
      if (localArrayList1.size() <= 0) {
        break label116;
      }
      localArrayList2 = a(localArrayList1, this.e, this.b.c.e(), paramBoolean2);
      if (localArrayList2 != null) {
        break label117;
      }
    }
    for (;;)
    {
      localArrayList1.clear();
      this.b.c.g().invalidate();
      return;
      return;
      label115:
      return;
      label116:
      return;
      label117:
      a(localArrayList2, true);
      localArrayList2.clear();
    }
  }
  
  protected ArrayList<bp> b(ArrayList<bp> paramArrayList)
  {
    int j = 0;
    int i;
    int k;
    Object localObject;
    if (paramArrayList != null)
    {
      i = paramArrayList.size();
      if (i != 0)
      {
        k = -1;
        localObject = null;
        if (j < i) {
          break label33;
        }
        return (ArrayList<bp>)localObject;
      }
    }
    else
    {
      return null;
    }
    return null;
    label33:
    bp localbp1 = (bp)paramArrayList.get(j);
    label55:
    int m;
    if (localbp1 != null)
    {
      if (this.b == null) {
        return null;
      }
    }
    else
    {
      m = j;
      j = i;
      i = k;
      k = m;
    }
    for (;;)
    {
      m = j;
      j = k + 1;
      k = i;
      i = m;
      break;
      if ((this.b.e == null) || (this.b.e.a == null)) {
        break label55;
      }
      this.b.e.a.size();
      if (this.e.f) {}
      try
      {
        m = this.e.o.a(localbp1);
        k = m;
        if (k < 0)
        {
          if (localObject == null) {
            break label357;
          }
          localbp1 = new bp(localbp1);
          localbp1.a = -1;
          ((ArrayList)localObject).add(localbp1);
          m = k;
          n = i;
          k = j;
          i = m;
          j = n;
          continue;
          m = k;
          n = i;
          k = j;
          i = m;
          j = n;
        }
      }
      catch (Throwable localThrowable)
      {
        label272:
        label357:
        for (;;)
        {
          localThrowable.printStackTrace();
          continue;
          paramArrayList.remove(j);
          i -= 1;
          j -= 1;
          bk localbk = this.e.p;
          if (localbk == null) {
            break label369;
          }
          try
          {
            Iterator localIterator = localbk.iterator();
            if (!localIterator.hasNext()) {}
            for (;;)
            {
              m = k;
              n = i;
              k = j;
              i = m;
              j = n;
              break;
              bp localbp2 = (bp)localIterator.next();
              if ((localbp2 == null) || (!localbp2.equals(localbp1))) {
                break label272;
              }
              localbp2.h = k;
              this.b.e.b();
            }
            localObject = new ArrayList();
          }
          finally {}
        }
        label369:
        m = k;
        int n = i;
        k = j;
        i = m;
        j = n;
      }
    }
  }
  
  public void b()
  {
    super.b();
    this.g.clear();
    if (this.f == null) {}
    while (this.f.c == null) {
      return;
    }
    this.f.c.b(this);
  }
  
  protected int f()
  {
    return 4;
  }
  
  protected int g()
  {
    return 1;
  }
  
  public void i()
  {
    a(false, false);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */