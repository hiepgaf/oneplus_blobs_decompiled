package com.aps;

import java.util.List;

public final class w
{
  private boolean a = false;
  private String b = "";
  private boolean c = false;
  private double d = 0.0D;
  private double e = 0.0D;
  
  protected w(List paramList, String paramString1, String paramString2, String paramString3)
  {
    this.b = paramString3;
    d();
  }
  
  private void d()
  {
    int k = 0;
    Object localObject = this.b;
    int i;
    if (localObject == null)
    {
      i = 0;
      label15:
      if (i != 0) {
        break label101;
      }
    }
    int j;
    for (;;)
    {
      this.a = this.c;
      return;
      if (((String)localObject).length() <= 8) {
        break;
      }
      i = 1;
      j = 0;
      for (;;)
      {
        if (i >= ((String)localObject).length() - 3)
        {
          if (!Integer.toHexString(j).equalsIgnoreCase(((String)localObject).substring(((String)localObject).length() - 2, ((String)localObject).length()))) {
            break;
          }
          i = 1;
          break label15;
        }
        j ^= ((String)localObject).charAt(i);
        i += 1;
      }
      label101:
      localObject = this.b.substring(0, this.b.length() - 3);
      j = 0;
      i = k;
      if (i < ((String)localObject).length()) {
        break label268;
      }
      localObject = ((String)localObject).split(",", j + 1);
      if (localObject.length < 6) {
        return;
      }
      if ((!localObject[2].equals("")) && (!localObject[(localObject.length - 3)].equals("")) && (!localObject[(localObject.length - 2)].equals("")) && (!localObject[(localObject.length - 1)].equals("")))
      {
        Integer.valueOf(localObject[2]).intValue();
        this.d = Double.valueOf(localObject[(localObject.length - 3)]).doubleValue();
        this.e = Double.valueOf(localObject[(localObject.length - 2)]).doubleValue();
        this.c = true;
      }
    }
    label268:
    if (((String)localObject).charAt(i) != ',') {}
    for (;;)
    {
      i += 1;
      break;
      j += 1;
    }
  }
  
  protected final boolean a()
  {
    return this.a;
  }
  
  protected final double b()
  {
    return this.d;
  }
  
  protected final double c()
  {
    return this.e;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/w.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */