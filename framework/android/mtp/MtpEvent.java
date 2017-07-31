package android.mtp;

public class MtpEvent
{
  public static final int EVENT_CANCEL_TRANSACTION = 16385;
  public static final int EVENT_CAPTURE_COMPLETE = 16397;
  public static final int EVENT_DEVICE_INFO_CHANGED = 16392;
  public static final int EVENT_DEVICE_PROP_CHANGED = 16390;
  public static final int EVENT_DEVICE_RESET = 16395;
  public static final int EVENT_OBJECT_ADDED = 16386;
  public static final int EVENT_OBJECT_INFO_CHANGED = 16391;
  public static final int EVENT_OBJECT_PROP_CHANGED = 51201;
  public static final int EVENT_OBJECT_PROP_DESC_CHANGED = 51202;
  public static final int EVENT_OBJECT_REFERENCES_CHANGED = 51203;
  public static final int EVENT_OBJECT_REMOVED = 16387;
  public static final int EVENT_REQUEST_OBJECT_TRANSFER = 16393;
  public static final int EVENT_STORAGE_INFO_CHANGED = 16396;
  public static final int EVENT_STORE_ADDED = 16388;
  public static final int EVENT_STORE_FULL = 16394;
  public static final int EVENT_STORE_REMOVED = 16389;
  public static final int EVENT_UNDEFINED = 16384;
  public static final int EVENT_UNREPORTED_STATUS = 16398;
  private int mEventCode = 16384;
  private int mParameter1;
  private int mParameter2;
  private int mParameter3;
  
  public int getDevicePropCode()
  {
    switch (this.mEventCode)
    {
    default: 
      throw new IllegalParameterAccess("devicePropCode", this.mEventCode);
    }
    return this.mParameter1;
  }
  
  public int getEventCode()
  {
    return this.mEventCode;
  }
  
  public int getObjectFormatCode()
  {
    switch (this.mEventCode)
    {
    default: 
      throw new IllegalParameterAccess("objectFormatCode", this.mEventCode);
    }
    return this.mParameter2;
  }
  
  public int getObjectHandle()
  {
    switch (this.mEventCode)
    {
    default: 
      throw new IllegalParameterAccess("objectHandle", this.mEventCode);
    case 16386: 
      return this.mParameter1;
    case 16387: 
      return this.mParameter1;
    case 16391: 
      return this.mParameter1;
    case 16393: 
      return this.mParameter1;
    case 51201: 
      return this.mParameter1;
    }
    return this.mParameter1;
  }
  
  public int getObjectPropCode()
  {
    switch (this.mEventCode)
    {
    default: 
      throw new IllegalParameterAccess("objectPropCode", this.mEventCode);
    case 51201: 
      return this.mParameter2;
    }
    return this.mParameter1;
  }
  
  public int getParameter1()
  {
    return this.mParameter1;
  }
  
  public int getParameter2()
  {
    return this.mParameter2;
  }
  
  public int getParameter3()
  {
    return this.mParameter3;
  }
  
  public int getStorageId()
  {
    switch (this.mEventCode)
    {
    case 16390: 
    case 16391: 
    case 16392: 
    case 16393: 
    case 16395: 
    default: 
      throw new IllegalParameterAccess("storageID", this.mEventCode);
    case 16388: 
      return this.mParameter1;
    case 16389: 
      return this.mParameter1;
    case 16394: 
      return this.mParameter1;
    }
    return this.mParameter1;
  }
  
  public int getTransactionId()
  {
    switch (this.mEventCode)
    {
    default: 
      throw new IllegalParameterAccess("transactionID", this.mEventCode);
    }
    return this.mParameter1;
  }
  
  private static class IllegalParameterAccess
    extends UnsupportedOperationException
  {
    public IllegalParameterAccess(String paramString, int paramInt)
    {
      super();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/mtp/MtpEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */