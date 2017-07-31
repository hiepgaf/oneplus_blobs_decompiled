package com.amap.api.mapcore2d;

import java.util.ArrayList;
import java.util.LinkedList;

class bo
  extends bl<bp>
{
  protected ArrayList<bp> b(int paramInt, boolean paramBoolean)
  {
    int j = 0;
    label117:
    label210:
    label219:
    label240:
    label253:
    label269:
    for (;;)
    {
      int m;
      int k;
      int i;
      try
      {
        ArrayList localArrayList;
        if (this.a != null)
        {
          m = this.a.size();
          if (paramInt <= m)
          {
            k = paramInt;
            localArrayList = new ArrayList(k);
            i = 0;
            paramInt = m;
            break label210;
            b();
            return localArrayList;
          }
        }
        else
        {
          return null;
        }
        k = m;
        continue;
        bp localbp = (bp)this.a.get(j);
        if (localbp == null) {
          break label240;
        }
        m = localbp.a;
        if (paramBoolean) {
          break label253;
        }
        if (m >= 0)
        {
          m = paramInt;
          paramInt = j;
          j = m;
          break label219;
          localArrayList.add(localbp);
          this.a.remove(j);
          j -= 1;
          i += 1;
          m = paramInt - 1;
          paramInt = j;
          j = m;
        }
        else
        {
          localArrayList.add(localbp);
          this.a.remove(j);
          j -= 1;
          i += 1;
          m = paramInt - 1;
          paramInt = j;
          j = m;
        }
      }
      finally {}
      if (j >= paramInt) {}
      for (;;)
      {
        if (i >= k) {
          break label269;
        }
        for (;;)
        {
          m = j;
          j = paramInt + 1;
          paramInt = m;
          break label210;
          break;
          m = paramInt;
          paramInt = j;
          j = m;
        }
        if (m == 0) {
          break label117;
        }
        m = paramInt;
        paramInt = j;
        j = m;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */