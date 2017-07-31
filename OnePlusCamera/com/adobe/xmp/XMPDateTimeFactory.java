package com.adobe.xmp;

import com.adobe.xmp.impl.XMPDateTimeImpl;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class XMPDateTimeFactory
{
  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
  
  public static XMPDateTime convertToLocalTime(XMPDateTime paramXMPDateTime)
  {
    long l = paramXMPDateTime.getCalendar().getTimeInMillis();
    paramXMPDateTime = new GregorianCalendar();
    paramXMPDateTime.setTimeInMillis(l);
    return new XMPDateTimeImpl(paramXMPDateTime);
  }
  
  public static XMPDateTime convertToUTCTime(XMPDateTime paramXMPDateTime)
  {
    long l = paramXMPDateTime.getCalendar().getTimeInMillis();
    paramXMPDateTime = new GregorianCalendar(UTC);
    paramXMPDateTime.setGregorianChange(new Date(Long.MIN_VALUE));
    paramXMPDateTime.setTimeInMillis(l);
    return new XMPDateTimeImpl(paramXMPDateTime);
  }
  
  public static XMPDateTime create(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    XMPDateTimeImpl localXMPDateTimeImpl = new XMPDateTimeImpl();
    localXMPDateTimeImpl.setYear(paramInt1);
    localXMPDateTimeImpl.setMonth(paramInt2);
    localXMPDateTimeImpl.setDay(paramInt3);
    localXMPDateTimeImpl.setHour(paramInt4);
    localXMPDateTimeImpl.setMinute(paramInt5);
    localXMPDateTimeImpl.setSecond(paramInt6);
    localXMPDateTimeImpl.setNanoSecond(paramInt7);
    return localXMPDateTimeImpl;
  }
  
  public static XMPDateTime createFromCalendar(Calendar paramCalendar)
  {
    return new XMPDateTimeImpl(paramCalendar);
  }
  
  public static XMPDateTime createFromISO8601(String paramString)
    throws XMPException
  {
    return new XMPDateTimeImpl(paramString);
  }
  
  public static XMPDateTime getCurrentDateTime()
  {
    return new XMPDateTimeImpl(new GregorianCalendar());
  }
  
  public static XMPDateTime setLocalTimeZone(XMPDateTime paramXMPDateTime)
  {
    paramXMPDateTime = paramXMPDateTime.getCalendar();
    paramXMPDateTime.setTimeZone(TimeZone.getDefault());
    return new XMPDateTimeImpl(paramXMPDateTime);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/XMPDateTimeFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */