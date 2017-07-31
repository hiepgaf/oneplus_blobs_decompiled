package com.aps;

import android.text.TextUtils;
import java.util.zip.CRC32;

public class o
{
  public byte[] A = null;
  public String B = null;
  public String C = null;
  public String D = null;
  public String E = null;
  public String a = "1";
  public short b = 0;
  public String c = null;
  public String d = null;
  public String e = null;
  public String f = null;
  public String g = null;
  public String h = null;
  public String i = null;
  public String j = null;
  public String k = null;
  public String l = null;
  public String m = null;
  public String n = null;
  public String o = null;
  public String p = null;
  public String q = null;
  public String r = null;
  public String s = null;
  public String t = null;
  public String u = null;
  public String v = null;
  public String w = null;
  public String x = null;
  public String y = null;
  public String z = null;
  
  private String a(String paramString, int paramInt)
  {
    String[] arrayOfString = this.w.split("\\*")[paramInt].split(",");
    if (!paramString.equals("lac"))
    {
      if (!paramString.equals("cellid"))
      {
        if (paramString.equals("signal")) {
          break label54;
        }
        return null;
      }
    }
    else {
      return arrayOfString[0];
    }
    return arrayOfString[1];
    label54:
    return arrayOfString[2];
  }
  
  private byte[] a(String paramString)
  {
    paramString = paramString.split(":");
    int i1;
    if (paramString == null)
    {
      paramString = new String[6];
      i1 = 0;
    }
    byte[] arrayOfByte;
    for (;;)
    {
      if (i1 >= paramString.length) {
        for (;;)
        {
          arrayOfByte = new byte[6];
          i1 = 0;
          if (i1 < paramString.length) {
            break label62;
          }
          return arrayOfByte;
          if (paramString.length != 6) {
            break;
          }
        }
      }
      paramString[i1] = "0";
      i1 += 1;
    }
    label62:
    if (paramString[i1].length() <= 2) {}
    for (;;)
    {
      arrayOfByte[i1] = ((byte)(byte)Integer.parseInt(paramString[i1], 16));
      i1 += 1;
      break;
      paramString[i1] = paramString[i1].substring(0, 2);
    }
  }
  
  private String b(String paramString)
  {
    if (this.v.contains(paramString + ">"))
    {
      int i1 = this.v.indexOf(paramString + ">");
      int i2 = this.v.indexOf("</" + paramString);
      return this.v.substring(i1 + paramString.length() + 1, i2);
    }
    return "0";
  }
  
  private void b()
  {
    if (!TextUtils.isEmpty(this.a))
    {
      if (TextUtils.isEmpty(this.c)) {
        break label403;
      }
      label20:
      if (TextUtils.isEmpty(this.d)) {
        break label412;
      }
      label30:
      if (TextUtils.isEmpty(this.e)) {
        break label421;
      }
      label40:
      if (TextUtils.isEmpty(this.f)) {
        break label430;
      }
      label50:
      if (TextUtils.isEmpty(this.g)) {
        break label439;
      }
      label60:
      if (TextUtils.isEmpty(this.h)) {
        break label448;
      }
      label70:
      if (TextUtils.isEmpty(this.i)) {
        break label457;
      }
      label80:
      if (TextUtils.isEmpty(this.j)) {
        break label466;
      }
      if (!this.j.equals("1")) {
        break label475;
      }
      label102:
      if (TextUtils.isEmpty(this.D)) {
        break label496;
      }
      if (!this.D.equals("0")) {
        break label505;
      }
      label124:
      if (TextUtils.isEmpty(this.k)) {
        break label526;
      }
      this.k = String.valueOf(Double.valueOf(Double.parseDouble(this.k) * 1200000.0D).intValue());
      label158:
      if (TextUtils.isEmpty(this.l)) {
        break label535;
      }
      this.l = String.valueOf(Double.valueOf(Double.parseDouble(this.l) * 1000000.0D).intValue());
      label192:
      if (TextUtils.isEmpty(this.m)) {
        break label544;
      }
      label202:
      if (TextUtils.isEmpty(this.n)) {
        break label553;
      }
      label212:
      if (TextUtils.isEmpty(this.o)) {
        break label562;
      }
      label222:
      if (TextUtils.isEmpty(this.p)) {
        break label571;
      }
      label232:
      if (TextUtils.isEmpty(this.q)) {
        break label580;
      }
      label242:
      if (TextUtils.isEmpty(this.r)) {
        break label589;
      }
      label252:
      if (TextUtils.isEmpty(this.B)) {
        break label598;
      }
      label262:
      if (TextUtils.isEmpty(this.C)) {
        break label607;
      }
      label272:
      if (TextUtils.isEmpty(this.s)) {
        break label616;
      }
      label282:
      if (TextUtils.isEmpty(this.t)) {
        break label625;
      }
      if (!this.t.equals("1")) {
        break label634;
      }
      label304:
      if (TextUtils.isEmpty(this.u)) {
        break label655;
      }
      if (!this.u.equals("1")) {
        break label664;
      }
      label326:
      if (TextUtils.isEmpty(this.v)) {
        break label685;
      }
      label336:
      if (TextUtils.isEmpty(this.w)) {
        break label694;
      }
      label346:
      if (TextUtils.isEmpty(this.x)) {
        break label703;
      }
      label356:
      if (TextUtils.isEmpty(this.y)) {
        break label712;
      }
      label366:
      if (TextUtils.isEmpty(this.E)) {
        break label721;
      }
      label376:
      if (TextUtils.isEmpty(this.z)) {
        break label730;
      }
    }
    for (;;)
    {
      if (this.A == null) {
        break label739;
      }
      return;
      this.a = "";
      break;
      label403:
      this.c = "";
      break label20;
      label412:
      this.d = "";
      break label30;
      label421:
      this.e = "";
      break label40;
      label430:
      this.f = "";
      break label50;
      label439:
      this.g = "";
      break label60;
      label448:
      this.h = "";
      break label70;
      label457:
      this.i = "";
      break label80;
      label466:
      this.j = "0";
      break label102;
      label475:
      if (this.j.equals("2")) {
        break label102;
      }
      this.j = "0";
      break label102;
      label496:
      this.D = "0";
      break label124;
      label505:
      if (this.D.equals("1")) {
        break label124;
      }
      this.D = "0";
      break label124;
      label526:
      this.k = "";
      break label158;
      label535:
      this.l = "";
      break label192;
      label544:
      this.m = "";
      break label202;
      label553:
      this.n = "";
      break label212;
      label562:
      this.o = "";
      break label222;
      label571:
      this.p = "";
      break label232;
      label580:
      this.q = "";
      break label242;
      label589:
      this.r = "";
      break label252;
      label598:
      this.B = "";
      break label262;
      label607:
      this.C = "";
      break label272;
      label616:
      this.s = "";
      break label282;
      label625:
      this.t = "0";
      break label304;
      label634:
      if (this.t.equals("2")) {
        break label304;
      }
      this.t = "0";
      break label304;
      label655:
      this.u = "0";
      break label326;
      label664:
      if (this.u.equals("2")) {
        break label326;
      }
      this.u = "0";
      break label326;
      label685:
      this.v = "";
      break label336;
      label694:
      this.w = "";
      break label346;
      label703:
      this.x = "";
      break label356;
      label712:
      this.y = "";
      break label366;
      label721:
      this.E = "";
      break label376;
      label730:
      this.z = "";
    }
    label739:
    this.A = new byte[0];
  }
  
  public byte[] a()
  {
    b();
    i1 = 3072;
    if (this.A == null) {}
    for (;;)
    {
      localObject2 = new byte[i1];
      localObject2[0] = Byte.parseByte(this.a);
      Object localObject1 = q.b(this.b);
      System.arraycopy(localObject1, 0, localObject2, 1, localObject1.length);
      i2 = localObject1.length + 1;
      i1 = i2;
      try
      {
        localObject1 = this.c.getBytes("GBK");
        i1 = i2;
        localObject2[i2] = ((byte)(byte)localObject1.length);
        i2 += 1;
        i1 = i2;
        System.arraycopy(localObject1, 0, localObject2, i2, localObject1.length);
        i1 = i2;
        i3 = localObject1.length;
        i1 = i2 + i3;
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          localObject2[i1] = 0;
          i1 += 1;
        }
      }
      i2 = i1;
      try
      {
        localObject1 = this.d.getBytes("GBK");
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
        }
      }
      i2 = i1;
      try
      {
        localObject1 = this.n.getBytes("GBK");
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException3)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
        }
      }
      i2 = i1;
      try
      {
        localObject1 = this.e.getBytes("GBK");
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException4)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
        }
      }
      i2 = i1;
      try
      {
        localObject1 = this.f.getBytes("GBK");
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException5)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
        }
      }
      i2 = i1;
      try
      {
        localObject1 = this.g.getBytes("GBK");
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException6)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
        }
      }
      i2 = i1;
      try
      {
        localObject1 = this.r.getBytes("GBK");
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException7)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
        }
      }
      i2 = i1;
      try
      {
        localObject1 = this.h.getBytes("GBK");
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException8)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
        }
      }
      i2 = i1;
      try
      {
        localObject1 = this.o.getBytes("GBK");
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException9)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
        }
      }
      i2 = i1;
      try
      {
        localObject1 = this.p.getBytes("GBK");
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException10)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
          continue;
          localObject2[i1] = 0;
          i1 += 1;
        }
      }
      if (TextUtils.isEmpty(this.q)) {
        break label1526;
      }
      localObject1 = a(this.q);
      localObject2[i1] = ((byte)(byte)localObject1.length);
      i1 += 1;
      System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
      i1 += localObject1.length;
      i2 = i1;
      try
      {
        localObject1 = this.B.getBytes("GBK");
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException11)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
        }
      }
      i2 = i1;
      try
      {
        localObject1 = this.C.getBytes("GBK");
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException12)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
        }
      }
      i2 = i1;
      try
      {
        localObject1 = this.s.getBytes("GBK");
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException13)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
          continue;
          localObject2[i1] = Byte.parseByte(this.D);
          i1 += 1;
          continue;
          if (!this.j.equals("2"))
          {
            continue;
            if (!this.j.equals("2"))
            {
              continue;
              if (!this.j.equals("2"))
              {
                continue;
                arrayOfByte1 = q.b(b("mcc"));
                System.arraycopy(arrayOfByte1, 0, localObject2, i1, arrayOfByte1.length);
                i1 += arrayOfByte1.length;
                arrayOfByte1 = q.b(b("mnc"));
                System.arraycopy(arrayOfByte1, 0, localObject2, i1, arrayOfByte1.length);
                i1 += arrayOfByte1.length;
                arrayOfByte1 = q.b(b("lac"));
                System.arraycopy(arrayOfByte1, 0, localObject2, i1, arrayOfByte1.length);
                i1 += arrayOfByte1.length;
                arrayOfByte1 = q.a(b("cellid"));
                System.arraycopy(arrayOfByte1, 0, localObject2, i1, arrayOfByte1.length);
                i2 = arrayOfByte1.length + i1;
                i1 = Integer.parseInt(b("signal"));
                if (i1 <= 127)
                {
                  if (i1 < -128) {
                    break label1943;
                  }
                  localObject2[i2] = ((byte)(byte)i1);
                  i1 = i2 + 1;
                  if (this.w.length() == 0) {
                    break label1948;
                  }
                  i4 = this.w.split("\\*").length;
                  localObject2[i1] = ((byte)(byte)i4);
                  i2 = i1 + 1;
                  i3 = 0;
                  i1 = i2;
                  if (i3 >= i4) {
                    continue;
                  }
                  arrayOfByte1 = q.b(a("lac", i3));
                  System.arraycopy(arrayOfByte1, 0, localObject2, i2, arrayOfByte1.length);
                  i1 = i2 + arrayOfByte1.length;
                  arrayOfByte1 = q.a(a("cellid", i3));
                  System.arraycopy(arrayOfByte1, 0, localObject2, i1, arrayOfByte1.length);
                  i2 = arrayOfByte1.length + i1;
                  i1 = Integer.parseInt(a("signal", i3));
                  if (i1 > 127) {
                    break label1960;
                  }
                  if (i1 < -128) {
                    break label1965;
                  }
                }
                for (;;)
                {
                  localObject2[i2] = ((byte)(byte)i1);
                  i3 += 1;
                  i2 += 1;
                  break label1829;
                  i1 = 0;
                  break label1782;
                  i1 = 0;
                  break label1782;
                  localObject2[i1] = 0;
                  i1 += 1;
                  break;
                  i1 = 0;
                  continue;
                  i1 = 0;
                }
                arrayOfByte1 = q.b(b("mcc"));
                System.arraycopy(arrayOfByte1, 0, localObject2, i1, arrayOfByte1.length);
                i1 += arrayOfByte1.length;
                arrayOfByte1 = q.b(b("sid"));
                System.arraycopy(arrayOfByte1, 0, localObject2, i1, arrayOfByte1.length);
                i1 += arrayOfByte1.length;
                arrayOfByte1 = q.b(b("nid"));
                System.arraycopy(arrayOfByte1, 0, localObject2, i1, arrayOfByte1.length);
                i1 += arrayOfByte1.length;
                arrayOfByte1 = q.b(b("bid"));
                System.arraycopy(arrayOfByte1, 0, localObject2, i1, arrayOfByte1.length);
                i1 += arrayOfByte1.length;
                arrayOfByte1 = q.a(b("lon"));
                System.arraycopy(arrayOfByte1, 0, localObject2, i1, arrayOfByte1.length);
                i1 += arrayOfByte1.length;
                arrayOfByte1 = q.a(b("lat"));
                System.arraycopy(arrayOfByte1, 0, localObject2, i1, arrayOfByte1.length);
                i2 = arrayOfByte1.length + i1;
                i1 = Integer.parseInt(b("signal"));
                if (i1 <= 127) {
                  if (i1 < -128) {
                    break label2199;
                  }
                }
                for (;;)
                {
                  localObject2[i2] = ((byte)(byte)i1);
                  i1 = i2 + 1;
                  localObject2[i1] = 0;
                  i1 += 1;
                  break;
                  i1 = 0;
                  continue;
                  i1 = 0;
                }
                localObject2[i1] = 0;
                i1 += 1;
              }
            }
          }
        }
      }
      localObject2[i1] = Byte.parseByte(this.t);
      i1 += 1;
      localObject2[i1] = Byte.parseByte(this.j);
      i1 += 1;
      if (this.j.equals("1")) {
        break label1580;
      }
      if (!this.j.equals("1")) {
        break label1598;
      }
      localObject1 = q.a(Integer.parseInt(this.k));
      System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
      i1 += localObject1.length;
      if (!this.j.equals("1")) {
        break label1613;
      }
      localObject1 = q.a(Integer.parseInt(this.l));
      System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
      i1 += localObject1.length;
      if (!this.j.equals("1")) {
        break label1628;
      }
      localObject1 = q.b(this.m);
      System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
      i1 += localObject1.length;
      localObject2[i1] = Byte.parseByte(this.u);
      i1 += 1;
      if (this.u.equals("1")) {
        break label1643;
      }
      if (this.u.equals("2")) {
        break label1970;
      }
      if (this.x.length() == 0) {
        break label2204;
      }
      localObject2[i1] = 1;
      i2 = i1 + 1;
      i1 = i2;
      for (;;)
      {
        try
        {
          localObject1 = this.x.split(",");
          i1 = i2;
          arrayOfByte3 = a(localObject1[0]);
          i1 = i2;
          System.arraycopy(arrayOfByte3, 0, localObject2, i2, arrayOfByte3.length);
          i1 = i2;
          i3 = arrayOfByte3.length;
          i3 = i2 + i3;
          i2 = i3;
          i1 = i3;
        }
        catch (Throwable localThrowable)
        {
          byte[] arrayOfByte3;
          int i4;
          byte[] arrayOfByte1;
          byte[] arrayOfByte2 = a("00:00:00:00:00:00");
          System.arraycopy(arrayOfByte2, 0, localObject2, i1, arrayOfByte2.length);
          i1 += arrayOfByte2.length;
          localObject2[i1] = 0;
          i1 += 1;
          localObject2[i1] = Byte.parseByte("0");
          i1 += 1;
          continue;
          if (arrayOfByte2.length == 0) {
            continue;
          }
          localObject2[i1] = ((byte)(byte)arrayOfByte2.length);
          i1 += 1;
          i2 = 0;
          if (i2 < arrayOfByte2.length) {
            continue;
          }
          if (this.E != null) {
            continue;
          }
          continue;
          String[] arrayOfString = arrayOfByte2[i2].split(",");
          byte[] arrayOfByte4 = a(arrayOfString[0]);
          System.arraycopy(arrayOfByte4, 0, localObject2, i1, arrayOfByte4.length);
          i3 = i1 + arrayOfByte4.length;
          i1 = i3;
          try
          {
            arrayOfByte4 = arrayOfString[2].getBytes("GBK");
            i1 = i3;
            localObject2[i3] = ((byte)(byte)arrayOfByte4.length);
            i3 += 1;
            i1 = i3;
            System.arraycopy(arrayOfByte4, 0, localObject2, i3, arrayOfByte4.length);
            i1 = i3;
            i4 = arrayOfByte4.length;
            i3 += i4;
          }
          catch (Exception localException16)
          {
            localObject2[i1] = 0;
            i3 = i1 + 1;
            continue;
            i1 = 0;
            continue;
            i1 = 0;
            continue;
          }
          i1 = Integer.parseInt(arrayOfString[1]);
          if (i1 > 127) {
            continue;
          }
          if (i1 < -128) {
            continue;
          }
          localObject2[i3] = Byte.parseByte(String.valueOf(i1));
          i1 = i3 + 1;
          i2 += 1;
          continue;
          if (this.E.length() <= 0) {
            continue;
          }
          arrayOfByte2 = q.b(Integer.parseInt(this.E));
          System.arraycopy(arrayOfByte2, 0, localObject2, i1, arrayOfByte2.length);
          i1 += arrayOfByte2.length;
          continue;
          arrayOfByte2 = null;
          continue;
          localObject2[i1] = 0;
          i1 += 1;
          continue;
        }
        try
        {
          arrayOfByte3 = localObject1[2].getBytes("GBK");
          i2 = i3;
          i1 = i3;
          localObject2[i3] = ((byte)(byte)arrayOfByte3.length);
          i3 += 1;
          i2 = i3;
          i1 = i3;
          System.arraycopy(arrayOfByte3, 0, localObject2, i3, arrayOfByte3.length);
          i2 = i3;
          i1 = i3;
          i4 = arrayOfByte3.length;
          i2 = i3 + i4;
        }
        catch (Exception localException15)
        {
          localObject2[i2] = 0;
          i2 += 1;
          continue;
          i3 = 0;
          continue;
          i3 = 0;
        }
      }
      i1 = i2;
      i3 = Integer.parseInt(localObject1[1]);
      if (i3 > 127) {
        break label2230;
      }
      if (i3 < -128) {
        break label2235;
      }
      i1 = i2;
      localObject2[i2] = Byte.parseByte(String.valueOf(i3));
      i1 = i2 + 1;
      localObject1 = this.y.split("\\*");
      if (!TextUtils.isEmpty(this.y)) {
        break label2294;
      }
      localObject2[i1] = 0;
      i1 += 1;
      i2 = i1;
      try
      {
        localObject1 = this.z.getBytes("GBK");
        i2 = i1;
        if (localObject1.length > 127) {
          break label2534;
        }
        if (localObject1 == null) {
          break label2540;
        }
        i2 = i1;
        localObject2[i1] = ((byte)(byte)localObject1.length);
        i1 += 1;
        i2 = i1;
        System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
        i2 = i1;
        i3 = localObject1.length;
        i1 += i3;
      }
      catch (Exception localException14)
      {
        for (;;)
        {
          localObject2[i2] = 0;
          i1 = i2 + 1;
          continue;
          i2 = this.A.length;
          continue;
          System.arraycopy(this.A, 0, localObject2, i1, this.A.length);
          i1 += this.A.length;
        }
      }
      if (this.A != null) {
        break label2566;
      }
      i2 = 0;
      localObject1 = q.b(i2);
      System.arraycopy(localObject1, 0, localObject2, i1, localObject1.length);
      i1 += localObject1.length;
      if (i2 > 0) {
        break label2575;
      }
      localObject1 = new byte[i1];
      System.arraycopy(localObject2, 0, localObject1, 0, i1);
      localObject2 = new CRC32();
      ((CRC32)localObject2).update((byte[])localObject1);
      localObject2 = q.a(((CRC32)localObject2).getValue());
      arrayOfByte3 = new byte[localObject2.length + i1];
      System.arraycopy(localObject1, 0, arrayOfByte3, 0, i1);
      System.arraycopy(localObject2, 0, arrayOfByte3, i1, localObject2.length);
      i1 = localObject2.length;
      return arrayOfByte3;
      i1 = this.A.length + 1 + 3072;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/o.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */