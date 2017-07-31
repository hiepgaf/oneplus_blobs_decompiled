package com.aps;

import java.util.concurrent.Callable;

class h
  implements Callable<Void>
{
  h(g paramg) {}
  
  public Void a()
    throws Exception
  {
    synchronized (this.a)
    {
      if (g.a(this.a) != null)
      {
        g.b(this.a);
        if (!g.c(this.a)) {
          return null;
        }
      }
      else
      {
        return null;
      }
      g.d(this.a);
      g.a(this.a, 0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/h.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */