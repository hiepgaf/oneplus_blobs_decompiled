package com.android.server.hdmi;

import android.util.SparseArray;

public final class HdmiCecMessageValidator
{
  private static final int DEST_ALL = 3;
  private static final int DEST_BROADCAST = 2;
  private static final int DEST_DIRECT = 1;
  static final int ERROR_DESTINATION = 2;
  static final int ERROR_PARAMETER = 3;
  static final int ERROR_PARAMETER_SHORT = 4;
  static final int ERROR_SOURCE = 1;
  static final int OK = 0;
  private static final int SRC_UNREGISTERED = 4;
  private static final String TAG = "HdmiCecMessageValidator";
  private final HdmiControlService mService;
  final SparseArray<ValidationInfo> mValidationInfo = new SparseArray();
  
  public HdmiCecMessageValidator(HdmiControlService paramHdmiControlService)
  {
    this.mService = paramHdmiControlService;
    paramHdmiControlService = new PhysicalAddressValidator(null);
    addValidationInfo(130, paramHdmiControlService, 6);
    addValidationInfo(157, paramHdmiControlService, 1);
    addValidationInfo(132, new ReportPhysicalAddressValidator(null), 6);
    addValidationInfo(128, new RoutingChangeValidator(null), 6);
    addValidationInfo(129, paramHdmiControlService, 6);
    addValidationInfo(134, paramHdmiControlService, 2);
    addValidationInfo(112, new SystemAudioModeRequestValidator(null), 1);
    paramHdmiControlService = new FixedLengthValidator(0);
    addValidationInfo(255, paramHdmiControlService, 1);
    addValidationInfo(159, paramHdmiControlService, 1);
    addValidationInfo(145, paramHdmiControlService, 5);
    addValidationInfo(113, paramHdmiControlService, 1);
    addValidationInfo(143, paramHdmiControlService, 1);
    addValidationInfo(140, paramHdmiControlService, 5);
    addValidationInfo(70, paramHdmiControlService, 1);
    addValidationInfo(131, paramHdmiControlService, 5);
    addValidationInfo(125, paramHdmiControlService, 1);
    addValidationInfo(4, paramHdmiControlService, 1);
    addValidationInfo(192, paramHdmiControlService, 1);
    addValidationInfo(11, paramHdmiControlService, 1);
    addValidationInfo(15, paramHdmiControlService, 1);
    addValidationInfo(193, paramHdmiControlService, 1);
    addValidationInfo(194, paramHdmiControlService, 1);
    addValidationInfo(195, paramHdmiControlService, 1);
    addValidationInfo(196, paramHdmiControlService, 1);
    addValidationInfo(133, paramHdmiControlService, 6);
    addValidationInfo(54, paramHdmiControlService, 7);
    addValidationInfo(197, paramHdmiControlService, 1);
    addValidationInfo(13, paramHdmiControlService, 1);
    addValidationInfo(6, paramHdmiControlService, 1);
    addValidationInfo(5, paramHdmiControlService, 1);
    addValidationInfo(69, paramHdmiControlService, 1);
    addValidationInfo(139, paramHdmiControlService, 3);
    paramHdmiControlService = new FixedLengthValidator(1);
    addValidationInfo(9, new VariableLengthValidator(1, 8), 1);
    addValidationInfo(10, paramHdmiControlService, 1);
    addValidationInfo(158, paramHdmiControlService, 1);
    addValidationInfo(50, new FixedLengthValidator(3), 2);
    VariableLengthValidator localVariableLengthValidator = new VariableLengthValidator(0, 14);
    addValidationInfo(135, new FixedLengthValidator(3), 2);
    addValidationInfo(137, new VariableLengthValidator(1, 14), 5);
    addValidationInfo(160, new VariableLengthValidator(4, 14), 7);
    addValidationInfo(138, localVariableLengthValidator, 7);
    addValidationInfo(100, localVariableLengthValidator, 1);
    addValidationInfo(71, localVariableLengthValidator, 1);
    addValidationInfo(141, paramHdmiControlService, 1);
    addValidationInfo(142, paramHdmiControlService, 1);
    addValidationInfo(68, new VariableLengthValidator(1, 2), 1);
    addValidationInfo(144, paramHdmiControlService, 1);
    addValidationInfo(0, new FixedLengthValidator(2), 1);
    addValidationInfo(122, paramHdmiControlService, 1);
    addValidationInfo(163, new FixedLengthValidator(3), 1);
    addValidationInfo(164, paramHdmiControlService, 1);
    addValidationInfo(114, paramHdmiControlService, 3);
    addValidationInfo(126, paramHdmiControlService, 1);
    addValidationInfo(154, paramHdmiControlService, 1);
    addValidationInfo(248, localVariableLengthValidator, 6);
  }
  
  private void addValidationInfo(int paramInt1, ParameterValidator paramParameterValidator, int paramInt2)
  {
    this.mValidationInfo.append(paramInt1, new ValidationInfo(paramParameterValidator, paramInt2));
  }
  
  private boolean isValidPhysicalAddress(byte[] paramArrayOfByte, int paramInt)
  {
    if (!this.mService.isTvDevice()) {
      return true;
    }
    paramInt = HdmiUtils.twoBytesToInt(paramArrayOfByte, paramInt);
    if ((paramInt != 65535) && (paramInt == this.mService.getPhysicalAddress())) {
      return true;
    }
    return this.mService.pathToPortId(paramInt) != -1;
  }
  
  static boolean isValidType(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt <= 7)
      {
        bool1 = bool2;
        if (paramInt != 2) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  private static int toErrorCode(boolean paramBoolean)
  {
    if (paramBoolean) {
      return 0;
    }
    return 3;
  }
  
  int isValid(HdmiCecMessage paramHdmiCecMessage)
  {
    int i = paramHdmiCecMessage.getOpcode();
    ValidationInfo localValidationInfo = (ValidationInfo)this.mValidationInfo.get(i);
    if (localValidationInfo == null)
    {
      HdmiLogger.warning("No validation information for the message: " + paramHdmiCecMessage, new Object[0]);
      return 0;
    }
    if ((paramHdmiCecMessage.getSource() == 15) && ((localValidationInfo.addressType & 0x4) == 0))
    {
      HdmiLogger.warning("Unexpected source: " + paramHdmiCecMessage, new Object[0]);
      return 1;
    }
    if (paramHdmiCecMessage.getDestination() == 15)
    {
      if ((localValidationInfo.addressType & 0x2) == 0)
      {
        HdmiLogger.warning("Unexpected broadcast message: " + paramHdmiCecMessage, new Object[0]);
        return 2;
      }
    }
    else if ((localValidationInfo.addressType & 0x1) == 0)
    {
      HdmiLogger.warning("Unexpected direct message: " + paramHdmiCecMessage, new Object[0]);
      return 2;
    }
    i = localValidationInfo.parameterValidator.isValid(paramHdmiCecMessage.getParams());
    if (i != 0)
    {
      HdmiLogger.warning("Unexpected parameters: " + paramHdmiCecMessage, new Object[0]);
      return i;
    }
    return 0;
  }
  
  private static class FixedLengthValidator
    implements HdmiCecMessageValidator.ParameterValidator
  {
    private final int mLength;
    
    public FixedLengthValidator(int paramInt)
    {
      this.mLength = paramInt;
    }
    
    public int isValid(byte[] paramArrayOfByte)
    {
      if (paramArrayOfByte.length < this.mLength) {
        return 4;
      }
      return 0;
    }
  }
  
  static abstract interface ParameterValidator
  {
    public abstract int isValid(byte[] paramArrayOfByte);
  }
  
  private class PhysicalAddressValidator
    implements HdmiCecMessageValidator.ParameterValidator
  {
    private PhysicalAddressValidator() {}
    
    public int isValid(byte[] paramArrayOfByte)
    {
      if (paramArrayOfByte.length < 2) {
        return 4;
      }
      return HdmiCecMessageValidator.-wrap1(HdmiCecMessageValidator.-wrap0(HdmiCecMessageValidator.this, paramArrayOfByte, 0));
    }
  }
  
  private class ReportPhysicalAddressValidator
    implements HdmiCecMessageValidator.ParameterValidator
  {
    private ReportPhysicalAddressValidator() {}
    
    public int isValid(byte[] paramArrayOfByte)
    {
      boolean bool = false;
      if (paramArrayOfByte.length < 3) {
        return 4;
      }
      if (HdmiCecMessageValidator.-wrap0(HdmiCecMessageValidator.this, paramArrayOfByte, 0)) {
        bool = HdmiCecMessageValidator.isValidType(paramArrayOfByte[2]);
      }
      return HdmiCecMessageValidator.-wrap1(bool);
    }
  }
  
  private class RoutingChangeValidator
    implements HdmiCecMessageValidator.ParameterValidator
  {
    private RoutingChangeValidator() {}
    
    public int isValid(byte[] paramArrayOfByte)
    {
      boolean bool = false;
      if (paramArrayOfByte.length < 4) {
        return 4;
      }
      if (HdmiCecMessageValidator.-wrap0(HdmiCecMessageValidator.this, paramArrayOfByte, 0)) {
        bool = HdmiCecMessageValidator.-wrap0(HdmiCecMessageValidator.this, paramArrayOfByte, 2);
      }
      return HdmiCecMessageValidator.-wrap1(bool);
    }
  }
  
  private class SystemAudioModeRequestValidator
    extends HdmiCecMessageValidator.PhysicalAddressValidator
  {
    private SystemAudioModeRequestValidator()
    {
      super(null);
    }
    
    public int isValid(byte[] paramArrayOfByte)
    {
      if (paramArrayOfByte.length == 0) {
        return 0;
      }
      return super.isValid(paramArrayOfByte);
    }
  }
  
  private static class ValidationInfo
  {
    public final int addressType;
    public final HdmiCecMessageValidator.ParameterValidator parameterValidator;
    
    public ValidationInfo(HdmiCecMessageValidator.ParameterValidator paramParameterValidator, int paramInt)
    {
      this.parameterValidator = paramParameterValidator;
      this.addressType = paramInt;
    }
  }
  
  private static class VariableLengthValidator
    implements HdmiCecMessageValidator.ParameterValidator
  {
    private final int mMaxLength;
    private final int mMinLength;
    
    public VariableLengthValidator(int paramInt1, int paramInt2)
    {
      this.mMinLength = paramInt1;
      this.mMaxLength = paramInt2;
    }
    
    public int isValid(byte[] paramArrayOfByte)
    {
      if (paramArrayOfByte.length < this.mMinLength) {
        return 4;
      }
      return 0;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiCecMessageValidator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */