package com.amap.api.mapcore2d;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

public class ct
  extends Thread
  implements ea.a
{
  private static String h = "sodownload";
  private static String i = "sofail";
  private ea a;
  private a b;
  private RandomAccessFile c;
  private String d;
  private String e;
  private String f;
  private Context g;
  
  public ct(Context paramContext, String paramString1, String paramString2, String paramString3)
  {
    this.g = paramContext;
    this.f = paramString3;
    this.d = a(paramContext, paramString1 + "temp.so");
    this.e = a(paramContext, "libwgs2gcj.so");
    this.b = new a(paramString2);
    this.a = new ea(this.b);
  }
  
  public static String a(Context paramContext, String paramString)
  {
    return paramContext.getFilesDir().getAbsolutePath() + File.separator + "libso" + File.separator + paramString;
  }
  
  private static String b(Context paramContext, String paramString)
  {
    return a(paramContext, paramString);
  }
  
  private void d()
  {
    File localFile = new File(this.d);
    if (!localFile.exists()) {
      return;
    }
    localFile.delete();
  }
  
  public void a()
  {
    if (this.b == null) {}
    while ((TextUtils.isEmpty(this.b.g())) || (!this.b.g().contains("libJni_wgs2gcj.so")) || (!this.b.g().contains(Build.CPU_ABI))) {
      return;
    }
    if (!new File(this.e).exists())
    {
      start();
      return;
    }
  }
  
  public void a(Throwable paramThrowable)
  {
    try
    {
      if (this.c == null) {}
      for (;;)
      {
        d();
        paramThrowable = new File(b(this.g, "tempfile"));
        if (!paramThrowable.exists()) {
          break;
        }
        return;
        this.c.close();
      }
      try
      {
        localFile = paramThrowable.getParentFile();
        if (localFile.exists())
        {
          paramThrowable.createNewFile();
          return;
        }
      }
      catch (Throwable paramThrowable)
      {
        cy.a(paramThrowable, "SDKCoordinatorDownload", "onException");
        return;
      }
    }
    catch (Throwable paramThrowable)
    {
      cy.a(paramThrowable, "SDKCoordinatorDownload", "onException");
      return;
    }
    for (;;)
    {
      File localFile;
      localFile.mkdir();
    }
  }
  
  public void a(byte[] paramArrayOfByte, long paramLong)
  {
    try
    {
      if (this.c != null)
      {
        localObject = this.c;
        if (localObject == null) {
          return;
        }
      }
    }
    catch (Throwable paramArrayOfByte)
    {
      Object localObject;
      boolean bool;
      d();
      cy.a(paramArrayOfByte, "SDKCoordinatorDownload", "onDownload");
      return;
    }
    try
    {
      this.c.seek(paramLong);
      this.c.write(paramArrayOfByte);
      return;
    }
    catch (IOException paramArrayOfByte)
    {
      d();
      cy.a(paramArrayOfByte, "SDKCoordinatorDownload", "onDownload");
      return;
    }
    localObject = new File(this.d);
    File localFile = ((File)localObject).getParentFile();
    bool = localFile.exists();
    if (bool) {}
    for (;;)
    {
      try
      {
        this.c = new RandomAccessFile((File)localObject, "rw");
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        cy.a(localFileNotFoundException, "SDKCoordinatorDownload", "onDownload");
        d();
      }
      break;
      localFile.mkdirs();
    }
  }
  
  public void b()
  {
    d();
  }
  
  public void c()
  {
    File localFile;
    try
    {
      if (this.c == null) {}
      for (;;)
      {
        String str = cr.a(this.d);
        if (str != null) {
          break;
        }
        d();
        return;
        this.c.close();
      }
      cy.a(localThrowable, "SDKCoordinatorDownload", "onFinish");
    }
    catch (Throwable localThrowable)
    {
      d();
      localFile = new File(this.e);
      if (localFile.exists()) {}
    }
    for (;;)
    {
      return;
      if (!localThrowable.equalsIgnoreCase(this.f)) {
        break;
      }
      if (!new File(this.e).exists())
      {
        new File(this.d).renameTo(new File(this.e));
        return;
      }
      d();
      return;
      localFile.delete();
    }
  }
  
  public void run()
  {
    try
    {
      File localFile = new File(b(this.g, "tempfile"));
      if (!localFile.exists()) {}
      for (;;)
      {
        this.a.a(this);
        return;
        localFile.delete();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      cy.a(localThrowable, "SDKCoordinatorDownload", "run");
      d();
    }
  }
  
  private static class a
    extends ee
  {
    private String a;
    
    a(String paramString)
    {
      this.a = paramString;
    }
    
    public Map<String, String> e()
    {
      return null;
    }
    
    public Map<String, String> f()
    {
      return null;
    }
    
    public String g()
    {
      return this.a;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ct.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */