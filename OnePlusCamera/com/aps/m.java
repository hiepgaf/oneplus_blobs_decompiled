package com.aps;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class m
{
  String a(String paramString)
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      return null;
    }
    try
    {
      paramString = new ByteArrayInputStream(paramString.getBytes("UTF-8"));
      localb = new b(null);
      if (paramString == null) {
        return localb.a;
      }
    }
    catch (UnsupportedEncodingException paramString)
    {
      for (;;)
      {
        b localb;
        paramString = null;
        continue;
        try
        {
          SAXParserFactory.newInstance().newSAXParser().parse(paramString, localb);
          paramString.close();
        }
        catch (SAXException paramString)
        {
          paramString = paramString;
        }
        catch (Throwable paramString)
        {
          paramString = paramString;
          paramString.printStackTrace();
          t.a(paramString);
        }
        finally {}
      }
    }
  }
  
  c b(String paramString)
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      return null;
    }
    if (!paramString.contains("SuccessCode=\"0\"")) {}
    try
    {
      paramString = new ByteArrayInputStream(paramString.getBytes("UTF-8"));
      localSAXParserFactory = SAXParserFactory.newInstance();
      locala = new a(null);
      if (paramString == null)
      {
        locala.a.f("network");
        if (locala.a.h() == 0L) {
          locala.a.a(t.a());
        }
        return locala.a;
        return null;
      }
    }
    catch (UnsupportedEncodingException paramString)
    {
      for (;;)
      {
        SAXParserFactory localSAXParserFactory;
        a locala;
        paramString = null;
        continue;
        try
        {
          localSAXParserFactory.newSAXParser().parse(paramString, locala);
          paramString.close();
        }
        catch (Throwable paramString)
        {
          paramString = paramString;
          paramString.printStackTrace();
        }
        finally {}
      }
    }
  }
  
  private static class a
    extends DefaultHandler
  {
    public c a = new c();
    private String b = "";
    
    public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
      this.b = String.valueOf(paramArrayOfChar, paramInt1, paramInt2);
    }
    
    public void endElement(String paramString1, String paramString2, String paramString3)
    {
      for (;;)
      {
        if ((paramString2.equals("retype")) || ((paramString2.equals("adcode")) || ((paramString2.equals("citycode")) || ((paramString2.equals("radius")) || ((paramString2.equals("cenx")) || ((paramString2.equals("ceny")) || ((paramString2.equals("desc")) || ((paramString2.equals("country")) || ((paramString2.equals("province")) || ((paramString2.equals("city")) || ((paramString2.equals("road")) || ((paramString2.equals("street")) || ((paramString2.equals("poiname")) || ((paramString2.equals("BIZ")) || ((paramString2.equals("flr")) || ((paramString2.equals("pid")) || ((paramString2.equals("apiTime")) || ((paramString2.equals("coord")) || ((paramString2.equals("mcell")) || ((paramString2.equals("district")) || (this.a.t() != null))))))))))))))))))))) {}
        try
        {
          for (;;)
          {
            if (paramString2.equals("eab")) {
              break label757;
            }
            if (paramString2.equals("ctl")) {
              break label780;
            }
            if (paramString2.equals("suc")) {
              break label797;
            }
            boolean bool = paramString2.equals("spa");
            if (bool) {
              break label814;
            }
            return;
            this.a.h(this.b);
            break;
            this.a.k(this.b);
            break;
            this.a.i(this.b);
            break;
            try
            {
              this.a.a(Float.valueOf(this.b).floatValue());
            }
            catch (Throwable paramString1)
            {
              paramString1.printStackTrace();
              t.a(paramString1);
              this.a.a(3891.0F);
            }
            break;
            try
            {
              this.b = q.a(Double.valueOf(this.b), "#.000000");
              this.a.a(Double.valueOf(this.b).doubleValue());
            }
            catch (Throwable paramString1)
            {
              paramString1.printStackTrace();
              t.a(paramString1);
              this.a.a(0.0D);
            }
            break;
            try
            {
              this.b = q.a(Double.valueOf(this.b), "#.000000");
              this.a.b(Double.valueOf(this.b).doubleValue());
            }
            catch (Throwable paramString1)
            {
              paramString1.printStackTrace();
              t.a(paramString1);
              this.a.b(0.0D);
            }
            break;
            this.a.j(this.b);
            break;
            this.a.l(this.b);
            break;
            this.a.m(this.b);
            break;
            this.a.n(this.b);
            break;
            this.a.o(this.b);
            break;
            this.a.p(this.b);
            break;
            this.a.q(this.b);
            break;
            if (this.a.t() != null) {}
            for (;;)
            {
              try
              {
                this.a.t().put("BIZ", this.b);
              }
              catch (Throwable paramString1)
              {
                paramString1.printStackTrace();
              }
              break;
              this.a.a(new JSONObject());
            }
            this.a.b(this.b);
            break;
            this.a.a(this.b);
            break;
            try
            {
              if ("".equals(this.b)) {
                break;
              }
              long l = Long.parseLong(this.b);
              this.a.a(l);
            }
            catch (Throwable paramString1)
            {
              paramString1.printStackTrace();
              t.a(paramString1);
              this.a.a(t.a());
            }
            break;
            try
            {
              this.a.d(this.b);
            }
            catch (Throwable paramString1)
            {
              paramString1.printStackTrace();
              t.a(paramString1);
            }
            break;
            try
            {
              this.a.e(this.b);
            }
            catch (Throwable paramString1)
            {
              paramString1.printStackTrace();
              t.a(paramString1);
            }
            break;
            try
            {
              this.a.c(this.b);
            }
            catch (Throwable paramString1)
            {
              paramString1.printStackTrace();
              t.a(paramString1);
            }
            break;
            this.a.a(new JSONObject());
          }
          label757:
          this.a.t().put(paramString2, this.b);
          return;
        }
        catch (Throwable paramString1)
        {
          paramString1.printStackTrace();
          return;
        }
      }
      label780:
      this.a.t().put(paramString2, this.b);
      return;
      label797:
      this.a.t().put(paramString2, this.b);
      return;
      label814:
      this.a.t().put(paramString2, this.b);
    }
    
    public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    {
      this.b = "";
    }
  }
  
  private static class b
    extends DefaultHandler
  {
    public String a = "";
    private boolean b = false;
    
    public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
      if (!this.b) {
        return;
      }
      this.a = String.valueOf(paramArrayOfChar, paramInt1, paramInt2);
    }
    
    public void endElement(String paramString1, String paramString2, String paramString3)
    {
      if (!paramString2.equals("sres")) {
        return;
      }
      this.b = false;
    }
    
    public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    {
      if (!paramString2.equals("sres")) {
        return;
      }
      this.b = true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/m.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */