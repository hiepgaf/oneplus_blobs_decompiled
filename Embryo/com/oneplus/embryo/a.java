package com.oneplus.embryo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

class a
{
  private boolean a = true;
  private b b;
  private String c;
  private String d;
  private String e;
  private String f;
  private String g;
  private String h;
  
  public a(String paramString)
  {
    this.h = paramString;
    this.e = ("layout_preload_" + this.h);
    this.f = ("layout_" + this.h);
    this.d = ("layout_name_" + this.h);
    this.g = ("layout_theme_" + this.h);
    this.c = ("layout_fail_" + this.h);
    this.b = new b(null);
  }
  
  public View a(Context paramContext, int paramInt)
  {
    if (this.a)
    {
      SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("embryo", 0);
      if ((localSharedPreferences.getInt(this.f, -1) != paramInt) || (localSharedPreferences.getInt(this.g, -1) != paramContext.getThemeResId())) {
        localSharedPreferences.edit().putBoolean(this.e, true).putInt(this.f, paramInt).putString(this.d, paramContext.getResources().getResourceName(paramInt)).putInt(this.g, paramContext.getThemeResId()).putInt(this.c, -1).apply();
      }
      this.a = false;
    }
    if (this.b.g(paramContext, paramInt)) {
      return this.b.e(paramContext);
    }
    return null;
  }
  
  public boolean b(Context paramContext, SharedPreferences paramSharedPreferences, LayoutInflater paramLayoutInflater)
  {
    int i;
    if ((paramSharedPreferences != null) && (paramSharedPreferences.contains(this.f)))
    {
      i = paramSharedPreferences.getInt(this.f, -1);
      if (i == -1) {
        return false;
      }
    }
    else
    {
      return false;
    }
    if (i == paramSharedPreferences.getInt(this.c, -1)) {
      return false;
    }
    int j = paramSharedPreferences.getInt(this.g, paramContext.getThemeResId());
    paramContext.setTheme(j);
    try
    {
      paramLayoutInflater = paramLayoutInflater.preInflate(i);
      this.b.c(i, paramLayoutInflater, paramContext.getResources().getConfiguration(), j);
      return true;
    }
    catch (Throwable paramLayoutInflater)
    {
      Log.e("Embryo", paramContext.getApplicationInfo().packageName + " preload layout failed. " + this.h);
      paramSharedPreferences.edit().putInt(this.c, i).commit();
      Process.killProcess(Process.myPid());
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/embryo/a.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */