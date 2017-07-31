package com.adobe.xmp;

import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.options.ParseOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPProperty;
import java.util.Calendar;

public abstract interface XMPMeta
  extends Cloneable
{
  public abstract void appendArrayItem(String paramString1, String paramString2, PropertyOptions paramPropertyOptions1, String paramString3, PropertyOptions paramPropertyOptions2)
    throws XMPException;
  
  public abstract void appendArrayItem(String paramString1, String paramString2, String paramString3)
    throws XMPException;
  
  public abstract Object clone();
  
  public abstract int countArrayItems(String paramString1, String paramString2)
    throws XMPException;
  
  public abstract void deleteArrayItem(String paramString1, String paramString2, int paramInt);
  
  public abstract void deleteProperty(String paramString1, String paramString2);
  
  public abstract void deleteQualifier(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract void deleteStructField(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract boolean doesArrayItemExist(String paramString1, String paramString2, int paramInt);
  
  public abstract boolean doesPropertyExist(String paramString1, String paramString2);
  
  public abstract boolean doesQualifierExist(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract boolean doesStructFieldExist(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract String dumpObject();
  
  public abstract XMPProperty getArrayItem(String paramString1, String paramString2, int paramInt)
    throws XMPException;
  
  public abstract XMPProperty getLocalizedText(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMPException;
  
  public abstract String getObjectName();
  
  public abstract String getPacketHeader();
  
  public abstract XMPProperty getProperty(String paramString1, String paramString2)
    throws XMPException;
  
  public abstract byte[] getPropertyBase64(String paramString1, String paramString2)
    throws XMPException;
  
  public abstract Boolean getPropertyBoolean(String paramString1, String paramString2)
    throws XMPException;
  
  public abstract Calendar getPropertyCalendar(String paramString1, String paramString2)
    throws XMPException;
  
  public abstract XMPDateTime getPropertyDate(String paramString1, String paramString2)
    throws XMPException;
  
  public abstract Double getPropertyDouble(String paramString1, String paramString2)
    throws XMPException;
  
  public abstract Integer getPropertyInteger(String paramString1, String paramString2)
    throws XMPException;
  
  public abstract Long getPropertyLong(String paramString1, String paramString2)
    throws XMPException;
  
  public abstract String getPropertyString(String paramString1, String paramString2)
    throws XMPException;
  
  public abstract XMPProperty getQualifier(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMPException;
  
  public abstract XMPProperty getStructField(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMPException;
  
  public abstract void insertArrayItem(String paramString1, String paramString2, int paramInt, String paramString3)
    throws XMPException;
  
  public abstract void insertArrayItem(String paramString1, String paramString2, int paramInt, String paramString3, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract XMPIterator iterator()
    throws XMPException;
  
  public abstract XMPIterator iterator(IteratorOptions paramIteratorOptions)
    throws XMPException;
  
  public abstract XMPIterator iterator(String paramString1, String paramString2, IteratorOptions paramIteratorOptions)
    throws XMPException;
  
  public abstract void normalize(ParseOptions paramParseOptions)
    throws XMPException;
  
  public abstract void setArrayItem(String paramString1, String paramString2, int paramInt, String paramString3)
    throws XMPException;
  
  public abstract void setArrayItem(String paramString1, String paramString2, int paramInt, String paramString3, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract void setLocalizedText(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws XMPException;
  
  public abstract void setLocalizedText(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract void setObjectName(String paramString);
  
  public abstract void setProperty(String paramString1, String paramString2, Object paramObject)
    throws XMPException;
  
  public abstract void setProperty(String paramString1, String paramString2, Object paramObject, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract void setPropertyBase64(String paramString1, String paramString2, byte[] paramArrayOfByte)
    throws XMPException;
  
  public abstract void setPropertyBase64(String paramString1, String paramString2, byte[] paramArrayOfByte, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract void setPropertyBoolean(String paramString1, String paramString2, boolean paramBoolean)
    throws XMPException;
  
  public abstract void setPropertyBoolean(String paramString1, String paramString2, boolean paramBoolean, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract void setPropertyCalendar(String paramString1, String paramString2, Calendar paramCalendar)
    throws XMPException;
  
  public abstract void setPropertyCalendar(String paramString1, String paramString2, Calendar paramCalendar, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract void setPropertyDate(String paramString1, String paramString2, XMPDateTime paramXMPDateTime)
    throws XMPException;
  
  public abstract void setPropertyDate(String paramString1, String paramString2, XMPDateTime paramXMPDateTime, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract void setPropertyDouble(String paramString1, String paramString2, double paramDouble)
    throws XMPException;
  
  public abstract void setPropertyDouble(String paramString1, String paramString2, double paramDouble, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract void setPropertyInteger(String paramString1, String paramString2, int paramInt)
    throws XMPException;
  
  public abstract void setPropertyInteger(String paramString1, String paramString2, int paramInt, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract void setPropertyLong(String paramString1, String paramString2, long paramLong)
    throws XMPException;
  
  public abstract void setPropertyLong(String paramString1, String paramString2, long paramLong, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract void setQualifier(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws XMPException;
  
  public abstract void setQualifier(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract void setStructField(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws XMPException;
  
  public abstract void setStructField(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, PropertyOptions paramPropertyOptions)
    throws XMPException;
  
  public abstract void sort();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/XMPMeta.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */