package com.adobe.xmp.impl;

import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public final class ISO8601Converter
{
  public static XMPDateTime parse(String paramString)
    throws XMPException
  {
    return parse(paramString, new XMPDateTimeImpl());
  }
  
  public static XMPDateTime parse(String paramString, XMPDateTime paramXMPDateTime)
    throws XMPException
  {
    int i1 = 1;
    int n = 0;
    ParameterAsserts.assertNotNull(paramString);
    paramString = new ParseState(paramString);
    int i;
    label31:
    label62:
    label107:
    label123:
    int j;
    int k;
    if (paramString.ch(0) == 'T')
    {
      i = 1;
      if (i == 0) {
        break label239;
      }
      paramXMPDateTime.setMonth(1);
      paramXMPDateTime.setDay(1);
      if (paramString.ch() == 'T') {
        break label437;
      }
      if (i == 0) {
        break label444;
      }
      i = paramString.gatherInt("Invalid hour in date string", 23);
      if (paramString.ch() != ':') {
        break label455;
      }
      paramXMPDateTime.setHour(i);
      paramString.skip();
      i = paramString.gatherInt("Invalid minute in date string", 59);
      if (paramString.hasNext()) {
        break label466;
      }
      paramXMPDateTime.setMinute(i);
      if (paramString.ch() == ':') {
        break label513;
      }
      if (paramString.ch() == 'Z') {
        break label714;
      }
      if (paramString.hasNext()) {
        break label729;
      }
      i = 0;
      j = 0;
      k = n;
    }
    for (;;)
    {
      paramXMPDateTime.setTimeZone(new SimpleTimeZone((i * 3600 * 1000 + k * 60 * 1000) * j, ""));
      if (paramString.hasNext()) {
        break label813;
      }
      return paramXMPDateTime;
      if (paramString.length() < 2) {
        label200:
        if (paramString.length() >= 3) {
          break label226;
        }
      }
      label226:
      while (paramString.ch(2) != ':')
      {
        i = 0;
        break label31;
        if (paramString.ch(1) != ':') {
          break label200;
        }
        break;
      }
      break;
      label239:
      if (paramString.ch(0) != '-')
      {
        label249:
        j = paramString.gatherInt("Invalid year in date string", 9999);
        if (paramString.hasNext()) {
          break label367;
        }
        label266:
        if (paramString.ch(0) == '-') {
          break label387;
        }
        label276:
        paramXMPDateTime.setYear(j);
        if (!paramString.hasNext()) {
          break label393;
        }
        paramString.skip();
        j = paramString.gatherInt("Invalid month in date string", 12);
        if (paramString.hasNext()) {
          break label395;
        }
        label310:
        paramXMPDateTime.setMonth(j);
        if (!paramString.hasNext()) {
          break label415;
        }
        paramString.skip();
        j = paramString.gatherInt("Invalid day in date string", 31);
        if (paramString.hasNext()) {
          break label417;
        }
      }
      label367:
      label387:
      label393:
      label395:
      label415:
      label417:
      while (paramString.ch() == 'T')
      {
        paramXMPDateTime.setDay(j);
        if (paramString.hasNext()) {
          break;
        }
        return paramXMPDateTime;
        paramString.skip();
        break label249;
        if (paramString.ch() == '-') {
          break label266;
        }
        throw new XMPException("Invalid date string, after year", 5);
        j = -j;
        break label276;
        return paramXMPDateTime;
        if (paramString.ch() == '-') {
          break label310;
        }
        throw new XMPException("Invalid date string, after month", 5);
        return paramXMPDateTime;
      }
      throw new XMPException("Invalid date string, after day", 5);
      label437:
      paramString.skip();
      break label62;
      label444:
      throw new XMPException("Invalid date string, missing 'T' after date", 5);
      label455:
      throw new XMPException("Invalid date string, after hour", 5);
      label466:
      if ((paramString.ch() == ':') || (paramString.ch() == 'Z') || (paramString.ch() == '+') || (paramString.ch() == '-')) {
        break label107;
      }
      throw new XMPException("Invalid date string, after minute", 5);
      label513:
      paramString.skip();
      i = paramString.gatherInt("Invalid whole seconds in date string", 59);
      if (!paramString.hasNext())
      {
        paramXMPDateTime.setSecond(i);
        if (paramString.ch() != '.') {
          break label123;
        }
        paramString.skip();
        i = paramString.pos();
        j = paramString.gatherInt("Invalid fractional seconds in date string", 999999999);
        if (paramString.ch() != 'Z') {
          break label654;
        }
      }
      int m;
      label654:
      while ((paramString.ch() == '+') || (paramString.ch() == '-'))
      {
        i = paramString.pos() - i;
        for (;;)
        {
          k = i;
          m = j;
          if (i <= 9) {
            break;
          }
          j /= 10;
          i -= 1;
        }
        if ((paramString.ch() == '.') || (paramString.ch() == 'Z') || (paramString.ch() == '+') || (paramString.ch() == '-')) {
          break;
        }
        throw new XMPException("Invalid date string, after whole seconds", 5);
      }
      throw new XMPException("Invalid date string, after fractional second", 5);
      while (k < 9)
      {
        m *= 10;
        k += 1;
      }
      paramXMPDateTime.setNanoSecond(m);
      break label123;
      label714:
      paramString.skip();
      i = 0;
      j = 0;
      k = n;
      continue;
      label729:
      j = i1;
      if (paramString.ch() != '+')
      {
        if (paramString.ch() != '-') {
          throw new XMPException("Time zone must begin with 'Z', '+', or '-'", 5);
        }
        j = -1;
      }
      paramString.skip();
      i = paramString.gatherInt("Invalid time zone hour in date string", 23);
      if (paramString.ch() != ':') {
        break label802;
      }
      paramString.skip();
      k = paramString.gatherInt("Invalid time zone minute in date string", 59);
    }
    label802:
    throw new XMPException("Invalid date string, after time zone hour", 5);
    label813:
    throw new XMPException("Invalid date string, extra chars at end", 5);
  }
  
  public static String render(XMPDateTime paramXMPDateTime)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    DecimalFormat localDecimalFormat = new DecimalFormat("0000", new DecimalFormatSymbols(Locale.ENGLISH));
    localStringBuffer.append(localDecimalFormat.format(paramXMPDateTime.getYear()));
    if (paramXMPDateTime.getMonth() != 0)
    {
      localDecimalFormat.applyPattern("'-'00");
      localStringBuffer.append(localDecimalFormat.format(paramXMPDateTime.getMonth()));
      if (paramXMPDateTime.getDay() == 0) {
        break label248;
      }
      localStringBuffer.append(localDecimalFormat.format(paramXMPDateTime.getDay()));
      if (paramXMPDateTime.getHour() == 0) {
        break label254;
      }
      localStringBuffer.append('T');
      localDecimalFormat.applyPattern("00");
      localStringBuffer.append(localDecimalFormat.format(paramXMPDateTime.getHour()));
      localStringBuffer.append(':');
      localStringBuffer.append(localDecimalFormat.format(paramXMPDateTime.getMinute()));
      if (paramXMPDateTime.getSecond() == 0) {
        break label305;
      }
      label186:
      double d1 = paramXMPDateTime.getSecond();
      double d2 = paramXMPDateTime.getNanoSecond() / 1.0E9D;
      localDecimalFormat.applyPattern(":00.#########");
      localStringBuffer.append(localDecimalFormat.format(d1 + d2));
      label227:
      if (paramXMPDateTime.getTimeZone() != null) {
        break label317;
      }
    }
    for (;;)
    {
      return localStringBuffer.toString();
      return localStringBuffer.toString();
      label248:
      return localStringBuffer.toString();
      label254:
      if ((paramXMPDateTime.getMinute() != 0) || (paramXMPDateTime.getSecond() != 0) || (paramXMPDateTime.getNanoSecond() != 0)) {
        break;
      }
      if (paramXMPDateTime.getTimeZone() != null)
      {
        if (paramXMPDateTime.getTimeZone().getRawOffset() != 0) {
          break;
        }
        continue;
        label305:
        if (paramXMPDateTime.getNanoSecond() != 0) {
          break label186;
        }
        break label227;
        label317:
        long l = paramXMPDateTime.getCalendar().getTimeInMillis();
        int j = paramXMPDateTime.getTimeZone().getOffset(l);
        if (j != 0)
        {
          int i = j / 3600000;
          j = Math.abs(j % 3600000 / 60000);
          localDecimalFormat.applyPattern("+00;-00");
          localStringBuffer.append(localDecimalFormat.format(i));
          localDecimalFormat.applyPattern(":00");
          localStringBuffer.append(localDecimalFormat.format(j));
        }
        else
        {
          localStringBuffer.append('Z');
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/ISO8601Converter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */