package com.fingerprints.extension.sensortest;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.fingerprints.extension.util.Logger;
import java.util.Arrays;
import java.util.HashMap;

public class SensorInfo
  implements Parcelable
{
  public static final String COMPANION_CHIP_HARDWARE_ID = "companion_chip_hardware_id";
  public static final String COMPANION_CHIP_LOT_ID = "companion_chip_lot_id";
  public static final Parcelable.Creator<SensorInfo> CREATOR = new Parcelable.Creator()
  {
    public SensorInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SensorInfo(paramAnonymousParcel, null);
    }
    
    public SensorInfo[] newArray(int paramAnonymousInt)
    {
      return new SensorInfo[paramAnonymousInt];
    }
  };
  public static final String HARDWARE_ID = "hardware_id";
  public static final String LOT_ID = "lot_id";
  public static final String MAX_NUM_OTP_BIT_ERRORS_IN_BYTE = "max_num_otp_bit_errors_in_byte";
  public static final String PRODUCTION_TIMESTAMP = "production_timestamp";
  public static final String PRODUCT_TYPE = "product_type";
  public static final String TOTAL_NUM_OTP_BIT_ERRORS = "total_num_otp_bit_errors";
  public static final String VENDOR_DATA = "vendor_data";
  public static final String WAFER_ID = "wafer_id";
  public static final String WAFER_POSITION_X = "wafer_position_x";
  public static final String WAFER_POSITION_Y = "wafer_position_y";
  private int mCompanionChipHardwareId;
  private String mCompanionChipLotId;
  private int mHardwareId;
  private Logger mLogger = new Logger(getClass().getSimpleName());
  private String mLotId;
  private int mMaxNumOtpBitErrorsInByte;
  private HashMap<String, Object> mParameterMap;
  private int mProductType;
  private String mProductionTimestamp;
  private int mTotalNumOtpBitErrors;
  private byte[] mVendorData;
  private int mWaferId;
  private int mWaferPositionX;
  private int mWaferPositionY;
  
  public SensorInfo() {}
  
  private SensorInfo(Parcel paramParcel)
  {
    try
    {
      this.mHardwareId = paramParcel.readInt();
      this.mLotId = paramParcel.readString();
      this.mWaferId = paramParcel.readInt();
      this.mWaferPositionX = paramParcel.readInt();
      this.mWaferPositionY = paramParcel.readInt();
      this.mProductionTimestamp = paramParcel.readString();
      this.mCompanionChipHardwareId = paramParcel.readInt();
      this.mCompanionChipLotId = paramParcel.readString();
      this.mVendorData = paramParcel.createByteArray();
      this.mTotalNumOtpBitErrors = paramParcel.readInt();
      this.mMaxNumOtpBitErrorsInByte = paramParcel.readInt();
      this.mProductType = paramParcel.readInt();
      return;
    }
    catch (Exception paramParcel)
    {
      this.mLogger.e("Exception: " + paramParcel);
    }
  }
  
  private int extractSubBitValue(int paramInt1, int paramInt2, int paramInt3)
  {
    return paramInt1 >>> paramInt3 & (1 << paramInt2) - 1;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getDeviceId()
  {
    return extractSubBitValue(this.mHardwareId, 8, 8);
  }
  
  public String getDeviceIdHex()
  {
    return Integer.toHexString(getDeviceId());
  }
  
  public int getHardwareId()
  {
    return this.mHardwareId;
  }
  
  public String getHardwareIdHex()
  {
    return Integer.toHexString(getHardwareId());
  }
  
  public HashMap<String, Object> getParameterMap()
  {
    HashMap localHashMap;
    if (this.mParameterMap == null)
    {
      this.mParameterMap = new HashMap();
      localHashMap = this.mParameterMap;
      if (this.mHardwareId != 65535) {
        break label223;
      }
    }
    label223:
    for (String str = "N/A";; str = "0x" + Integer.toHexString(this.mHardwareId))
    {
      localHashMap.put("hardware_id", str);
      this.mParameterMap.put("lot_id", this.mLotId);
      this.mParameterMap.put("wafer_id", Integer.valueOf(this.mWaferId));
      this.mParameterMap.put("wafer_position_x", Integer.valueOf(this.mWaferPositionX));
      this.mParameterMap.put("wafer_position_y", Integer.valueOf(this.mWaferPositionY));
      this.mParameterMap.put("production_timestamp", this.mProductionTimestamp);
      this.mParameterMap.put("companion_chip_hardware_id", Integer.valueOf(this.mCompanionChipHardwareId));
      this.mParameterMap.put("companion_chip_lot_id", this.mCompanionChipLotId);
      this.mParameterMap.put("vendor_data", this.mVendorData);
      this.mParameterMap.put("total_num_otp_bit_errors", Integer.valueOf(this.mTotalNumOtpBitErrors));
      this.mParameterMap.put("max_num_otp_bit_errors_in_byte", Integer.valueOf(this.mMaxNumOtpBitErrorsInByte));
      this.mParameterMap.put("product_type", Integer.valueOf(this.mProductType));
      return this.mParameterMap;
    }
  }
  
  public int getProductType()
  {
    return this.mProductType;
  }
  
  public String getProductTypeString()
  {
    return Integer.toString(getProductType());
  }
  
  public int getRevision()
  {
    return extractSubBitValue(this.mHardwareId, 4, 0);
  }
  
  public String getRevisionHex()
  {
    return Integer.toHexString(getRevision());
  }
  
  public int getVarId()
  {
    return extractSubBitValue(this.mHardwareId, 4, 4);
  }
  
  public String getVarIdHex()
  {
    return Integer.toHexString(getVarId());
  }
  
  public void print()
  {
    this.mLogger.i("SensorInfo" + toString());
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nHardwareId: ");
    if (this.mHardwareId == 65535)
    {
      localObject = "N/A";
      localStringBuilder.append((String)localObject);
      localStringBuilder.append("\nLotId: ");
      if ((this.mLotId != null) && (this.mLotId.trim().length() != 0)) {
        break label351;
      }
      localObject = "N/A";
      label63:
      localStringBuilder.append((String)localObject);
      localStringBuilder.append("\nWaferId: ");
      if (this.mWaferId != 255) {
        break label359;
      }
      localObject = "N/A";
      label89:
      localStringBuilder.append(localObject);
      localStringBuilder.append("\nWaferPositionX: ");
      if (this.mWaferPositionX != 255) {
        break label370;
      }
      localObject = "N/A";
      label115:
      localStringBuilder.append(localObject);
      localStringBuilder.append("\nWaferPositionY: ");
      if (this.mWaferPositionY != 255) {
        break label381;
      }
      localObject = "N/A";
      label141:
      localStringBuilder.append(localObject);
      localStringBuilder.append("\nProductionTimestamp: ");
      if ((this.mProductionTimestamp != null) && (this.mProductionTimestamp.trim().length() != 0)) {
        break label392;
      }
      localObject = "N/A";
      label177:
      localStringBuilder.append((String)localObject);
      localStringBuilder.append("\nCompanionChipHardwareId: ");
      if (this.mCompanionChipHardwareId != 65535) {
        break label400;
      }
      localObject = "N/A";
      label202:
      localStringBuilder.append((String)localObject);
      localStringBuilder.append("\nCompanionChipLotId: ");
      if ((this.mCompanionChipLotId != null) && (this.mCompanionChipLotId.trim().length() != 0)) {
        break label429;
      }
      localObject = "N/A";
      label238:
      localStringBuilder.append((String)localObject);
      localStringBuilder.append("\nVendorData: ");
      if ((this.mVendorData != null) && (this.mVendorData.length != 0)) {
        break label437;
      }
    }
    label351:
    label359:
    label370:
    label381:
    label392:
    label400:
    label429:
    label437:
    for (Object localObject = "N/A";; localObject = Arrays.toString(this.mVendorData))
    {
      localStringBuilder.append((String)localObject);
      localStringBuilder.append("\nTotalNumOtpBitErrors: ").append(this.mTotalNumOtpBitErrors);
      localStringBuilder.append("\nMaxNumOtpBitErrorsInByte: ").append(this.mMaxNumOtpBitErrorsInByte);
      localStringBuilder.append("\nProductType: ").append(this.mProductType);
      return localStringBuilder.toString();
      localObject = "0x" + Integer.toHexString(this.mHardwareId);
      break;
      localObject = this.mLotId;
      break label63;
      localObject = Integer.valueOf(this.mWaferId);
      break label89;
      localObject = Integer.valueOf(this.mWaferPositionX);
      break label115;
      localObject = Integer.valueOf(this.mWaferPositionY);
      break label141;
      localObject = this.mProductionTimestamp;
      break label177;
      localObject = "0x" + Integer.toHexString(this.mCompanionChipHardwareId);
      break label202;
      localObject = this.mCompanionChipLotId;
      break label238;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mHardwareId);
    paramParcel.writeString(this.mLotId);
    paramParcel.writeInt(this.mWaferId);
    paramParcel.writeInt(this.mWaferPositionX);
    paramParcel.writeInt(this.mWaferPositionY);
    paramParcel.writeString(this.mProductionTimestamp);
    paramParcel.writeInt(this.mCompanionChipHardwareId);
    paramParcel.writeString(this.mCompanionChipLotId);
    paramParcel.writeByteArray(this.mVendorData);
    paramParcel.writeInt(this.mTotalNumOtpBitErrors);
    paramParcel.writeInt(this.mMaxNumOtpBitErrorsInByte);
    paramParcel.writeInt(this.mProductType);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/sensortest/SensorInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */