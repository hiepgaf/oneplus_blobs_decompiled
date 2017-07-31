package com.oneplus.embryo;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;

final class b
{
  private View i;
  private Configuration j;
  private int k;
  private int l;
  
  private ClassLoader f(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
  {
    if ((paramClassLoader1 == null) || (paramClassLoader2 == null)) {
      return null;
    }
    if (paramClassLoader1 == paramClassLoader2) {
      return paramClassLoader1;
    }
    ClassLoader localClassLoader = f(paramClassLoader1.getParent(), paramClassLoader2);
    paramClassLoader1 = f(paramClassLoader1, paramClassLoader2.getParent());
    if (localClassLoader == paramClassLoader1) {
      return localClassLoader;
    }
    if (localClassLoader != null) {
      return localClassLoader;
    }
    return paramClassLoader1;
  }
  
  private boolean h(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
  {
    ClassLoader localClassLoader = f(paramClassLoader1, paramClassLoader2);
    return (localClassLoader == paramClassLoader1) || (localClassLoader == paramClassLoader2);
  }
  
  public void c(int paramInt1, View paramView, Configuration paramConfiguration, int paramInt2)
  {
    this.k = paramInt1;
    this.i = paramView;
    this.j = new Configuration(paramConfiguration);
    this.l = paramInt2;
  }
  
  public void d()
  {
    this.k = -1;
    this.l = -1;
    this.i = null;
    this.j = null;
  }
  
  public View e(Context paramContext)
  {
    View localView = this.i;
    d();
    localView.switchContext(paramContext);
    return localView;
  }
  
  public boolean g(Context paramContext, int paramInt)
  {
    if ((paramInt == this.k) && (this.l == paramContext.getThemeResId()))
    {
      if (this.j.diff(paramContext.getResources().getConfiguration()) == 0) {
        return h(this.i.getClass().getClassLoader(), paramContext.getClassLoader());
      }
      d();
      return false;
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/embryo/b.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */