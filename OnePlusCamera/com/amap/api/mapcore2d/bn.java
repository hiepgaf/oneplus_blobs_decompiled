package com.amap.api.mapcore2d;

class bn
{
  private Thread[] a;
  
  public bn(int paramInt, Runnable paramRunnable1, Runnable paramRunnable2)
  {
    this.a = new Thread[paramInt];
    if (i >= paramInt) {
      return;
    }
    if (i != 0) {
      label27:
      this.a[i] = new Thread(paramRunnable2);
    }
    for (;;)
    {
      i += 1;
      break;
      if (paramInt <= 1) {
        break label27;
      }
      this.a[i] = new Thread(paramRunnable1);
    }
  }
  
  public void a()
  {
    Thread[] arrayOfThread = this.a;
    int j = arrayOfThread.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      Thread localThread = arrayOfThread[i];
      localThread.setDaemon(true);
      localThread.start();
      i += 1;
    }
  }
  
  public void b()
  {
    int j;
    int i;
    if (this.a != null)
    {
      j = this.a.length;
      i = 0;
    }
    for (;;)
    {
      if (i >= j)
      {
        this.a = null;
        return;
        return;
      }
      this.a[i].interrupt();
      this.a[i] = null;
      i += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */