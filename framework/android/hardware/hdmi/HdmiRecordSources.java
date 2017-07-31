package android.hardware.hdmi;

import android.util.Log;

public final class HdmiRecordSources
{
  public static final int ANALOGUE_BROADCAST_TYPE_CABLE = 0;
  public static final int ANALOGUE_BROADCAST_TYPE_SATELLITE = 1;
  public static final int ANALOGUE_BROADCAST_TYPE_TERRESTRIAL = 2;
  public static final int BROADCAST_SYSTEM_NTSC_M = 3;
  public static final int BROADCAST_SYSTEM_PAL_BG = 0;
  public static final int BROADCAST_SYSTEM_PAL_DK = 8;
  public static final int BROADCAST_SYSTEM_PAL_I = 4;
  public static final int BROADCAST_SYSTEM_PAL_M = 2;
  public static final int BROADCAST_SYSTEM_PAL_OTHER_SYSTEM = 31;
  public static final int BROADCAST_SYSTEM_SECAM_BG = 6;
  public static final int BROADCAST_SYSTEM_SECAM_DK = 5;
  public static final int BROADCAST_SYSTEM_SECAM_L = 7;
  public static final int BROADCAST_SYSTEM_SECAM_LP = 1;
  private static final int CHANNEL_NUMBER_FORMAT_1_PART = 1;
  private static final int CHANNEL_NUMBER_FORMAT_2_PART = 2;
  public static final int DIGITAL_BROADCAST_TYPE_ARIB = 0;
  public static final int DIGITAL_BROADCAST_TYPE_ARIB_BS = 8;
  public static final int DIGITAL_BROADCAST_TYPE_ARIB_CS = 9;
  public static final int DIGITAL_BROADCAST_TYPE_ARIB_T = 10;
  public static final int DIGITAL_BROADCAST_TYPE_ATSC = 1;
  public static final int DIGITAL_BROADCAST_TYPE_ATSC_CABLE = 16;
  public static final int DIGITAL_BROADCAST_TYPE_ATSC_SATELLITE = 17;
  public static final int DIGITAL_BROADCAST_TYPE_ATSC_TERRESTRIAL = 18;
  public static final int DIGITAL_BROADCAST_TYPE_DVB = 2;
  public static final int DIGITAL_BROADCAST_TYPE_DVB_C = 24;
  public static final int DIGITAL_BROADCAST_TYPE_DVB_S = 25;
  public static final int DIGITAL_BROADCAST_TYPE_DVB_S2 = 26;
  public static final int DIGITAL_BROADCAST_TYPE_DVB_T = 27;
  private static final int RECORD_SOURCE_TYPE_ANALOGUE_SERVICE = 3;
  private static final int RECORD_SOURCE_TYPE_DIGITAL_SERVICE = 2;
  private static final int RECORD_SOURCE_TYPE_EXTERNAL_PHYSICAL_ADDRESS = 5;
  private static final int RECORD_SOURCE_TYPE_EXTERNAL_PLUG = 4;
  private static final int RECORD_SOURCE_TYPE_OWN_SOURCE = 1;
  private static final String TAG = "HdmiRecordSources";
  
  public static boolean checkRecordSource(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return false;
    }
    int i = paramArrayOfByte[0];
    int j = paramArrayOfByte.length - 1;
    switch (i)
    {
    default: 
      return false;
    case 1: 
      return j == 0;
    case 2: 
      return j == 7;
    case 3: 
      return j == 4;
    case 4: 
      return j == 1;
    }
    return j == 2;
  }
  
  public static AnalogueServiceSource ofAnalogue(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 < 0) || (paramInt1 > 2))
    {
      Log.w("HdmiRecordSources", "Invalid Broadcast type:" + paramInt1);
      throw new IllegalArgumentException("Invalid Broadcast type:" + paramInt1);
    }
    if ((paramInt2 < 0) || (paramInt2 > 65535))
    {
      Log.w("HdmiRecordSources", "Invalid frequency value[0x0000-0xFFFF]:" + paramInt2);
      throw new IllegalArgumentException("Invalid frequency value[0x0000-0xFFFF]:" + paramInt2);
    }
    if ((paramInt3 < 0) || (paramInt3 > 31))
    {
      Log.w("HdmiRecordSources", "Invalid Broadcast system:" + paramInt3);
      throw new IllegalArgumentException("Invalid Broadcast system:" + paramInt3);
    }
    return new AnalogueServiceSource(paramInt1, paramInt2, paramInt3, null);
  }
  
  public static DigitalServiceSource ofArib(int paramInt, AribData paramAribData)
  {
    if (paramAribData == null) {
      throw new IllegalArgumentException("data should not be null.");
    }
    switch (paramInt)
    {
    default: 
      Log.w("HdmiRecordSources", "Invalid ARIB type:" + paramInt);
      throw new IllegalArgumentException("type should not be null.");
    }
    return new DigitalServiceSource(0, paramInt, paramAribData, null);
  }
  
  public static DigitalServiceSource ofAtsc(int paramInt, AtscData paramAtscData)
  {
    if (paramAtscData == null) {
      throw new IllegalArgumentException("data should not be null.");
    }
    switch (paramInt)
    {
    default: 
      Log.w("HdmiRecordSources", "Invalid ATSC type:" + paramInt);
      throw new IllegalArgumentException("Invalid ATSC type:" + paramInt);
    }
    return new DigitalServiceSource(0, paramInt, paramAtscData, null);
  }
  
  public static DigitalServiceSource ofDigitalChannelId(int paramInt, DigitalChannelData paramDigitalChannelData)
  {
    if (paramDigitalChannelData == null) {
      throw new IllegalArgumentException("data should not be null.");
    }
    switch (paramInt)
    {
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 19: 
    case 20: 
    case 21: 
    case 22: 
    case 23: 
    default: 
      Log.w("HdmiRecordSources", "Invalid broadcast type:" + paramInt);
      throw new IllegalArgumentException("Invalid broadcast system value:" + paramInt);
    }
    return new DigitalServiceSource(1, paramInt, paramDigitalChannelData, null);
  }
  
  public static DigitalServiceSource ofDvb(int paramInt, DvbData paramDvbData)
  {
    if (paramDvbData == null) {
      throw new IllegalArgumentException("data should not be null.");
    }
    switch (paramInt)
    {
    default: 
      Log.w("HdmiRecordSources", "Invalid DVB type:" + paramInt);
      throw new IllegalArgumentException("Invalid DVB type:" + paramInt);
    }
    return new DigitalServiceSource(0, paramInt, paramDvbData, null);
  }
  
  public static ExternalPhysicalAddress ofExternalPhysicalAddress(int paramInt)
  {
    if ((0xFFFF0000 & paramInt) != 0)
    {
      Log.w("HdmiRecordSources", "Invalid physical address:" + paramInt);
      throw new IllegalArgumentException("Invalid physical address:" + paramInt);
    }
    return new ExternalPhysicalAddress(paramInt, null);
  }
  
  public static ExternalPlugData ofExternalPlug(int paramInt)
  {
    if ((paramInt < 1) || (paramInt > 255))
    {
      Log.w("HdmiRecordSources", "Invalid plug number[1-255]" + paramInt);
      throw new IllegalArgumentException("Invalid plug number[1-255]" + paramInt);
    }
    return new ExternalPlugData(paramInt, null);
  }
  
  public static OwnSource ofOwnSource()
  {
    return new OwnSource(null);
  }
  
  private static int shortToByteArray(short paramShort, byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte[paramInt] = ((byte)(paramShort >>> 8 & 0xFF));
    paramArrayOfByte[(paramInt + 1)] = ((byte)(paramShort & 0xFF));
    return 2;
  }
  
  private static int threeFieldsToSixBytes(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, int paramInt4)
  {
    shortToByteArray((short)paramInt1, paramArrayOfByte, paramInt4);
    shortToByteArray((short)paramInt2, paramArrayOfByte, paramInt4 + 2);
    shortToByteArray((short)paramInt3, paramArrayOfByte, paramInt4 + 4);
    return 6;
  }
  
  public static final class AnalogueServiceSource
    extends HdmiRecordSources.RecordSource
  {
    static final int EXTRA_DATA_SIZE = 4;
    private final int mBroadcastSystem;
    private final int mBroadcastType;
    private final int mFrequency;
    
    private AnalogueServiceSource(int paramInt1, int paramInt2, int paramInt3)
    {
      super(4);
      this.mBroadcastType = paramInt1;
      this.mFrequency = paramInt2;
      this.mBroadcastSystem = paramInt3;
    }
    
    int extraParamToByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      paramArrayOfByte[paramInt] = ((byte)this.mBroadcastType);
      HdmiRecordSources.-wrap0((short)this.mFrequency, paramArrayOfByte, paramInt + 1);
      paramArrayOfByte[(paramInt + 3)] = ((byte)this.mBroadcastSystem);
      return 4;
    }
  }
  
  public static final class AribData
    implements HdmiRecordSources.DigitalServiceIdentification
  {
    private final int mOriginalNetworkId;
    private final int mServiceId;
    private final int mTransportStreamId;
    
    public AribData(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mTransportStreamId = paramInt1;
      this.mServiceId = paramInt2;
      this.mOriginalNetworkId = paramInt3;
    }
    
    public int toByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      return HdmiRecordSources.-wrap1(this.mTransportStreamId, this.mServiceId, this.mOriginalNetworkId, paramArrayOfByte, paramInt);
    }
  }
  
  public static final class AtscData
    implements HdmiRecordSources.DigitalServiceIdentification
  {
    private final int mProgramNumber;
    private final int mTransportStreamId;
    
    public AtscData(int paramInt1, int paramInt2)
    {
      this.mTransportStreamId = paramInt1;
      this.mProgramNumber = paramInt2;
    }
    
    public int toByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      return HdmiRecordSources.-wrap1(this.mTransportStreamId, this.mProgramNumber, 0, paramArrayOfByte, paramInt);
    }
  }
  
  private static final class ChannelIdentifier
  {
    private final int mChannelNumberFormat;
    private final int mMajorChannelNumber;
    private final int mMinorChannelNumber;
    
    private ChannelIdentifier(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mChannelNumberFormat = paramInt1;
      this.mMajorChannelNumber = paramInt2;
      this.mMinorChannelNumber = paramInt3;
    }
    
    private int toByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      paramArrayOfByte[paramInt] = ((byte)(this.mChannelNumberFormat << 2 | this.mMajorChannelNumber >>> 8 & 0x3));
      paramArrayOfByte[(paramInt + 1)] = ((byte)(this.mMajorChannelNumber & 0xFF));
      HdmiRecordSources.-wrap0((short)this.mMinorChannelNumber, paramArrayOfByte, paramInt + 2);
      return 4;
    }
  }
  
  public static final class DigitalChannelData
    implements HdmiRecordSources.DigitalServiceIdentification
  {
    private final HdmiRecordSources.ChannelIdentifier mChannelIdentifier;
    
    private DigitalChannelData(HdmiRecordSources.ChannelIdentifier paramChannelIdentifier)
    {
      this.mChannelIdentifier = paramChannelIdentifier;
    }
    
    public static DigitalChannelData ofOneNumber(int paramInt)
    {
      return new DigitalChannelData(new HdmiRecordSources.ChannelIdentifier(1, 0, paramInt, null));
    }
    
    public static DigitalChannelData ofTwoNumbers(int paramInt1, int paramInt2)
    {
      return new DigitalChannelData(new HdmiRecordSources.ChannelIdentifier(2, paramInt1, paramInt2, null));
    }
    
    public int toByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      HdmiRecordSources.ChannelIdentifier.-wrap0(this.mChannelIdentifier, paramArrayOfByte, paramInt);
      paramArrayOfByte[(paramInt + 4)] = 0;
      paramArrayOfByte[(paramInt + 5)] = 0;
      return 6;
    }
  }
  
  private static abstract interface DigitalServiceIdentification
  {
    public abstract int toByteArray(byte[] paramArrayOfByte, int paramInt);
  }
  
  public static final class DigitalServiceSource
    extends HdmiRecordSources.RecordSource
  {
    private static final int DIGITAL_SERVICE_IDENTIFIED_BY_CHANNEL = 1;
    private static final int DIGITAL_SERVICE_IDENTIFIED_BY_DIGITAL_ID = 0;
    static final int EXTRA_DATA_SIZE = 7;
    private final int mBroadcastSystem;
    private final HdmiRecordSources.DigitalServiceIdentification mIdentification;
    private final int mIdentificationMethod;
    
    private DigitalServiceSource(int paramInt1, int paramInt2, HdmiRecordSources.DigitalServiceIdentification paramDigitalServiceIdentification)
    {
      super(7);
      this.mIdentificationMethod = paramInt1;
      this.mBroadcastSystem = paramInt2;
      this.mIdentification = paramDigitalServiceIdentification;
    }
    
    int extraParamToByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      paramArrayOfByte[paramInt] = ((byte)(this.mIdentificationMethod << 7 | this.mBroadcastSystem & 0x7F));
      this.mIdentification.toByteArray(paramArrayOfByte, paramInt + 1);
      return 7;
    }
  }
  
  public static final class DvbData
    implements HdmiRecordSources.DigitalServiceIdentification
  {
    private final int mOriginalNetworkId;
    private final int mServiceId;
    private final int mTransportStreamId;
    
    public DvbData(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mTransportStreamId = paramInt1;
      this.mServiceId = paramInt2;
      this.mOriginalNetworkId = paramInt3;
    }
    
    public int toByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      return HdmiRecordSources.-wrap1(this.mTransportStreamId, this.mServiceId, this.mOriginalNetworkId, paramArrayOfByte, paramInt);
    }
  }
  
  public static final class ExternalPhysicalAddress
    extends HdmiRecordSources.RecordSource
  {
    static final int EXTRA_DATA_SIZE = 2;
    private final int mPhysicalAddress;
    
    private ExternalPhysicalAddress(int paramInt)
    {
      super(2);
      this.mPhysicalAddress = paramInt;
    }
    
    int extraParamToByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      HdmiRecordSources.-wrap0((short)this.mPhysicalAddress, paramArrayOfByte, paramInt);
      return 2;
    }
  }
  
  public static final class ExternalPlugData
    extends HdmiRecordSources.RecordSource
  {
    static final int EXTRA_DATA_SIZE = 1;
    private final int mPlugNumber;
    
    private ExternalPlugData(int paramInt)
    {
      super(1);
      this.mPlugNumber = paramInt;
    }
    
    int extraParamToByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      paramArrayOfByte[paramInt] = ((byte)this.mPlugNumber);
      return 1;
    }
  }
  
  public static final class OwnSource
    extends HdmiRecordSources.RecordSource
  {
    private static final int EXTRA_DATA_SIZE = 0;
    
    private OwnSource()
    {
      super(0);
    }
    
    int extraParamToByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      return 0;
    }
  }
  
  public static abstract class RecordSource
  {
    final int mExtraDataSize;
    final int mSourceType;
    
    RecordSource(int paramInt1, int paramInt2)
    {
      this.mSourceType = paramInt1;
      this.mExtraDataSize = paramInt2;
    }
    
    abstract int extraParamToByteArray(byte[] paramArrayOfByte, int paramInt);
    
    final int getDataSize(boolean paramBoolean)
    {
      if (paramBoolean) {
        return this.mExtraDataSize + 1;
      }
      return this.mExtraDataSize;
    }
    
    final int toByteArray(boolean paramBoolean, byte[] paramArrayOfByte, int paramInt)
    {
      int i = paramInt;
      if (paramBoolean)
      {
        paramArrayOfByte[paramInt] = ((byte)this.mSourceType);
        i = paramInt + 1;
      }
      extraParamToByteArray(paramArrayOfByte, i);
      return getDataSize(paramBoolean);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/hdmi/HdmiRecordSources.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */