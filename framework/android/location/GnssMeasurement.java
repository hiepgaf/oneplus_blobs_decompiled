package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class GnssMeasurement
  implements Parcelable
{
  private static final int ADR_ALL = 7;
  public static final int ADR_STATE_CYCLE_SLIP = 4;
  public static final int ADR_STATE_RESET = 2;
  public static final int ADR_STATE_UNKNOWN = 0;
  public static final int ADR_STATE_VALID = 1;
  public static final Parcelable.Creator<GnssMeasurement> CREATOR = new Parcelable.Creator()
  {
    public GnssMeasurement createFromParcel(Parcel paramAnonymousParcel)
    {
      GnssMeasurement localGnssMeasurement = new GnssMeasurement();
      GnssMeasurement.-set9(localGnssMeasurement, paramAnonymousParcel.readInt());
      GnssMeasurement.-set17(localGnssMeasurement, paramAnonymousParcel.readInt());
      GnssMeasurement.-set8(localGnssMeasurement, paramAnonymousParcel.readInt());
      GnssMeasurement.-set18(localGnssMeasurement, paramAnonymousParcel.readDouble());
      GnssMeasurement.-set16(localGnssMeasurement, paramAnonymousParcel.readInt());
      GnssMeasurement.-set13(localGnssMeasurement, paramAnonymousParcel.readLong());
      GnssMeasurement.-set14(localGnssMeasurement, paramAnonymousParcel.readLong());
      GnssMeasurement.-set7(localGnssMeasurement, paramAnonymousParcel.readDouble());
      GnssMeasurement.-set11(localGnssMeasurement, paramAnonymousParcel.readDouble());
      GnssMeasurement.-set12(localGnssMeasurement, paramAnonymousParcel.readDouble());
      GnssMeasurement.-set1(localGnssMeasurement, paramAnonymousParcel.readInt());
      GnssMeasurement.-set0(localGnssMeasurement, paramAnonymousParcel.readDouble());
      GnssMeasurement.-set2(localGnssMeasurement, paramAnonymousParcel.readDouble());
      GnssMeasurement.-set4(localGnssMeasurement, paramAnonymousParcel.readFloat());
      GnssMeasurement.-set3(localGnssMeasurement, paramAnonymousParcel.readLong());
      GnssMeasurement.-set5(localGnssMeasurement, paramAnonymousParcel.readDouble());
      GnssMeasurement.-set6(localGnssMeasurement, paramAnonymousParcel.readDouble());
      GnssMeasurement.-set10(localGnssMeasurement, paramAnonymousParcel.readInt());
      GnssMeasurement.-set15(localGnssMeasurement, paramAnonymousParcel.readDouble());
      return localGnssMeasurement;
    }
    
    public GnssMeasurement[] newArray(int paramAnonymousInt)
    {
      return new GnssMeasurement[paramAnonymousInt];
    }
  };
  private static final int HAS_CARRIER_CYCLES = 1024;
  private static final int HAS_CARRIER_FREQUENCY = 512;
  private static final int HAS_CARRIER_PHASE = 2048;
  private static final int HAS_CARRIER_PHASE_UNCERTAINTY = 4096;
  private static final int HAS_NO_FLAGS = 0;
  private static final int HAS_SNR = 1;
  public static final int MULTIPATH_INDICATOR_DETECTED = 1;
  public static final int MULTIPATH_INDICATOR_NOT_DETECTED = 2;
  public static final int MULTIPATH_INDICATOR_NOT_USED = 2;
  public static final int MULTIPATH_INDICATOR_UNKNOWN = 0;
  private static final int STATE_ALL = 16383;
  public static final int STATE_BDS_D2_BIT_SYNC = 256;
  public static final int STATE_BDS_D2_SUBFRAME_SYNC = 512;
  public static final int STATE_BIT_SYNC = 2;
  public static final int STATE_CODE_LOCK = 1;
  public static final int STATE_GAL_E1BC_CODE_LOCK = 1024;
  public static final int STATE_GAL_E1B_PAGE_SYNC = 4096;
  public static final int STATE_GAL_E1C_2ND_CODE_LOCK = 2048;
  public static final int STATE_GLO_STRING_SYNC = 64;
  public static final int STATE_GLO_TOD_DECODED = 128;
  public static final int STATE_MSEC_AMBIGUOUS = 16;
  public static final int STATE_SBAS_SYNC = 8192;
  public static final int STATE_SUBFRAME_SYNC = 4;
  public static final int STATE_SYMBOL_SYNC = 32;
  public static final int STATE_TOW_DECODED = 8;
  public static final int STATE_UNKNOWN = 0;
  private double mAccumulatedDeltaRangeMeters;
  private int mAccumulatedDeltaRangeState;
  private double mAccumulatedDeltaRangeUncertaintyMeters;
  private long mCarrierCycles;
  private float mCarrierFrequencyHz;
  private double mCarrierPhase;
  private double mCarrierPhaseUncertainty;
  private double mCn0DbHz;
  private int mConstellationType;
  private int mFlags;
  private int mMultipathIndicator;
  private double mPseudorangeRateMetersPerSecond;
  private double mPseudorangeRateUncertaintyMetersPerSecond;
  private long mReceivedSvTimeNanos;
  private long mReceivedSvTimeUncertaintyNanos;
  private double mSnrInDb;
  private int mState;
  private int mSvid;
  private double mTimeOffsetNanos;
  
  public GnssMeasurement()
  {
    initialize();
  }
  
  private String getAccumulatedDeltaRangeStateString()
  {
    if (this.mAccumulatedDeltaRangeState == 0) {
      return "Unknown";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if ((this.mAccumulatedDeltaRangeState & 0x1) == 1) {
      localStringBuilder.append("Valid|");
    }
    if ((this.mAccumulatedDeltaRangeState & 0x2) == 2) {
      localStringBuilder.append("Reset|");
    }
    if ((this.mAccumulatedDeltaRangeState & 0x4) == 4) {
      localStringBuilder.append("CycleSlip|");
    }
    int i = this.mAccumulatedDeltaRangeState & 0xFFFFFFF8;
    if (i > 0)
    {
      localStringBuilder.append("Other(");
      localStringBuilder.append(Integer.toBinaryString(i));
      localStringBuilder.append(")|");
    }
    localStringBuilder.deleteCharAt(localStringBuilder.length() - 1);
    return localStringBuilder.toString();
  }
  
  private String getMultipathIndicatorString()
  {
    switch (this.mMultipathIndicator)
    {
    default: 
      return "<Invalid:" + this.mMultipathIndicator + ">";
    case 0: 
      return "Unknown";
    case 1: 
      return "Detected";
    }
    return "NotUsed";
  }
  
  private String getStateString()
  {
    if (this.mState == 0) {
      return "Unknown";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if ((this.mState & 0x1) != 0) {
      localStringBuilder.append("CodeLock|");
    }
    if ((this.mState & 0x2) != 0) {
      localStringBuilder.append("BitSync|");
    }
    if ((this.mState & 0x4) != 0) {
      localStringBuilder.append("SubframeSync|");
    }
    if ((this.mState & 0x8) != 0) {
      localStringBuilder.append("TowDecoded|");
    }
    if ((this.mState & 0x10) != 0) {
      localStringBuilder.append("MsecAmbiguous|");
    }
    if ((this.mState & 0x20) != 0) {
      localStringBuilder.append("SymbolSync|");
    }
    if ((this.mState & 0x40) != 0) {
      localStringBuilder.append("GloStringSync|");
    }
    if ((this.mState & 0x80) != 0) {
      localStringBuilder.append("GloTodDecoded|");
    }
    if ((this.mState & 0x100) != 0) {
      localStringBuilder.append("BdsD2BitSync|");
    }
    if ((this.mState & 0x200) != 0) {
      localStringBuilder.append("BdsD2SubframeSync|");
    }
    if ((this.mState & 0x400) != 0) {
      localStringBuilder.append("GalE1bcCodeLock|");
    }
    if ((this.mState & 0x800) != 0) {
      localStringBuilder.append("E1c2ndCodeLock|");
    }
    if ((this.mState & 0x1000) != 0) {
      localStringBuilder.append("GalE1bPageSync|");
    }
    if ((this.mState & 0x2000) != 0) {
      localStringBuilder.append("SbasSync|");
    }
    int i = this.mState & 0xC000;
    if (i > 0)
    {
      localStringBuilder.append("Other(");
      localStringBuilder.append(Integer.toBinaryString(i));
      localStringBuilder.append(")|");
    }
    localStringBuilder.setLength(localStringBuilder.length() - 1);
    return localStringBuilder.toString();
  }
  
  private void initialize()
  {
    this.mFlags = 0;
    setSvid(0);
    setTimeOffsetNanos(-9.223372036854776E18D);
    setState(0);
    setReceivedSvTimeNanos(Long.MIN_VALUE);
    setReceivedSvTimeUncertaintyNanos(Long.MAX_VALUE);
    setCn0DbHz(Double.MIN_VALUE);
    setPseudorangeRateMetersPerSecond(Double.MIN_VALUE);
    setPseudorangeRateUncertaintyMetersPerSecond(Double.MIN_VALUE);
    setAccumulatedDeltaRangeState(0);
    setAccumulatedDeltaRangeMeters(Double.MIN_VALUE);
    setAccumulatedDeltaRangeUncertaintyMeters(Double.MIN_VALUE);
    resetCarrierFrequencyHz();
    resetCarrierCycles();
    resetCarrierPhase();
    resetCarrierPhaseUncertainty();
    setMultipathIndicator(0);
    resetSnrInDb();
  }
  
  private boolean isFlagSet(int paramInt)
  {
    return (this.mFlags & paramInt) == paramInt;
  }
  
  private void resetFlag(int paramInt)
  {
    this.mFlags &= paramInt;
  }
  
  private void setFlag(int paramInt)
  {
    this.mFlags |= paramInt;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public double getAccumulatedDeltaRangeMeters()
  {
    return this.mAccumulatedDeltaRangeMeters;
  }
  
  public int getAccumulatedDeltaRangeState()
  {
    return this.mAccumulatedDeltaRangeState;
  }
  
  public double getAccumulatedDeltaRangeUncertaintyMeters()
  {
    return this.mAccumulatedDeltaRangeUncertaintyMeters;
  }
  
  public long getCarrierCycles()
  {
    return this.mCarrierCycles;
  }
  
  public float getCarrierFrequencyHz()
  {
    return this.mCarrierFrequencyHz;
  }
  
  public double getCarrierPhase()
  {
    return this.mCarrierPhase;
  }
  
  public double getCarrierPhaseUncertainty()
  {
    return this.mCarrierPhaseUncertainty;
  }
  
  public double getCn0DbHz()
  {
    return this.mCn0DbHz;
  }
  
  public int getConstellationType()
  {
    return this.mConstellationType;
  }
  
  public int getMultipathIndicator()
  {
    return this.mMultipathIndicator;
  }
  
  public double getPseudorangeRateMetersPerSecond()
  {
    return this.mPseudorangeRateMetersPerSecond;
  }
  
  public double getPseudorangeRateUncertaintyMetersPerSecond()
  {
    return this.mPseudorangeRateUncertaintyMetersPerSecond;
  }
  
  public long getReceivedSvTimeNanos()
  {
    return this.mReceivedSvTimeNanos;
  }
  
  public long getReceivedSvTimeUncertaintyNanos()
  {
    return this.mReceivedSvTimeUncertaintyNanos;
  }
  
  public double getSnrInDb()
  {
    return this.mSnrInDb;
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  public int getSvid()
  {
    return this.mSvid;
  }
  
  public double getTimeOffsetNanos()
  {
    return this.mTimeOffsetNanos;
  }
  
  public boolean hasCarrierCycles()
  {
    return isFlagSet(1024);
  }
  
  public boolean hasCarrierFrequencyHz()
  {
    return isFlagSet(512);
  }
  
  public boolean hasCarrierPhase()
  {
    return isFlagSet(2048);
  }
  
  public boolean hasCarrierPhaseUncertainty()
  {
    return isFlagSet(4096);
  }
  
  public boolean hasSnrInDb()
  {
    return isFlagSet(1);
  }
  
  public void reset()
  {
    initialize();
  }
  
  public void resetCarrierCycles()
  {
    resetFlag(1024);
    this.mCarrierCycles = Long.MIN_VALUE;
  }
  
  public void resetCarrierFrequencyHz()
  {
    resetFlag(512);
    this.mCarrierFrequencyHz = NaN.0F;
  }
  
  public void resetCarrierPhase()
  {
    resetFlag(2048);
    this.mCarrierPhase = NaN.0D;
  }
  
  public void resetCarrierPhaseUncertainty()
  {
    resetFlag(4096);
    this.mCarrierPhaseUncertainty = NaN.0D;
  }
  
  public void resetSnrInDb()
  {
    resetFlag(1);
    this.mSnrInDb = NaN.0D;
  }
  
  public void set(GnssMeasurement paramGnssMeasurement)
  {
    this.mFlags = paramGnssMeasurement.mFlags;
    this.mSvid = paramGnssMeasurement.mSvid;
    this.mConstellationType = paramGnssMeasurement.mConstellationType;
    this.mTimeOffsetNanos = paramGnssMeasurement.mTimeOffsetNanos;
    this.mState = paramGnssMeasurement.mState;
    this.mReceivedSvTimeNanos = paramGnssMeasurement.mReceivedSvTimeNanos;
    this.mReceivedSvTimeUncertaintyNanos = paramGnssMeasurement.mReceivedSvTimeUncertaintyNanos;
    this.mCn0DbHz = paramGnssMeasurement.mCn0DbHz;
    this.mPseudorangeRateMetersPerSecond = paramGnssMeasurement.mPseudorangeRateMetersPerSecond;
    this.mPseudorangeRateUncertaintyMetersPerSecond = paramGnssMeasurement.mPseudorangeRateUncertaintyMetersPerSecond;
    this.mAccumulatedDeltaRangeState = paramGnssMeasurement.mAccumulatedDeltaRangeState;
    this.mAccumulatedDeltaRangeMeters = paramGnssMeasurement.mAccumulatedDeltaRangeMeters;
    this.mAccumulatedDeltaRangeUncertaintyMeters = paramGnssMeasurement.mAccumulatedDeltaRangeUncertaintyMeters;
    this.mCarrierFrequencyHz = paramGnssMeasurement.mCarrierFrequencyHz;
    this.mCarrierCycles = paramGnssMeasurement.mCarrierCycles;
    this.mCarrierPhase = paramGnssMeasurement.mCarrierPhase;
    this.mCarrierPhaseUncertainty = paramGnssMeasurement.mCarrierPhaseUncertainty;
    this.mMultipathIndicator = paramGnssMeasurement.mMultipathIndicator;
    this.mSnrInDb = paramGnssMeasurement.mSnrInDb;
  }
  
  public void setAccumulatedDeltaRangeMeters(double paramDouble)
  {
    this.mAccumulatedDeltaRangeMeters = paramDouble;
  }
  
  public void setAccumulatedDeltaRangeState(int paramInt)
  {
    this.mAccumulatedDeltaRangeState = paramInt;
  }
  
  public void setAccumulatedDeltaRangeUncertaintyMeters(double paramDouble)
  {
    this.mAccumulatedDeltaRangeUncertaintyMeters = paramDouble;
  }
  
  public void setCarrierCycles(long paramLong)
  {
    setFlag(1024);
    this.mCarrierCycles = paramLong;
  }
  
  public void setCarrierFrequencyHz(float paramFloat)
  {
    setFlag(512);
    this.mCarrierFrequencyHz = paramFloat;
  }
  
  public void setCarrierPhase(double paramDouble)
  {
    setFlag(2048);
    this.mCarrierPhase = paramDouble;
  }
  
  public void setCarrierPhaseUncertainty(double paramDouble)
  {
    setFlag(4096);
    this.mCarrierPhaseUncertainty = paramDouble;
  }
  
  public void setCn0DbHz(double paramDouble)
  {
    this.mCn0DbHz = paramDouble;
  }
  
  public void setConstellationType(int paramInt)
  {
    this.mConstellationType = paramInt;
  }
  
  public void setMultipathIndicator(int paramInt)
  {
    this.mMultipathIndicator = paramInt;
  }
  
  public void setPseudorangeRateMetersPerSecond(double paramDouble)
  {
    this.mPseudorangeRateMetersPerSecond = paramDouble;
  }
  
  public void setPseudorangeRateUncertaintyMetersPerSecond(double paramDouble)
  {
    this.mPseudorangeRateUncertaintyMetersPerSecond = paramDouble;
  }
  
  public void setReceivedSvTimeNanos(long paramLong)
  {
    this.mReceivedSvTimeNanos = paramLong;
  }
  
  public void setReceivedSvTimeUncertaintyNanos(long paramLong)
  {
    this.mReceivedSvTimeUncertaintyNanos = paramLong;
  }
  
  public void setSnrInDb(double paramDouble)
  {
    setFlag(1);
    this.mSnrInDb = paramDouble;
  }
  
  public void setState(int paramInt)
  {
    this.mState = paramInt;
  }
  
  public void setSvid(int paramInt)
  {
    this.mSvid = paramInt;
  }
  
  public void setTimeOffsetNanos(double paramDouble)
  {
    this.mTimeOffsetNanos = paramDouble;
  }
  
  public String toString()
  {
    Object localObject2 = null;
    StringBuilder localStringBuilder = new StringBuilder("GnssMeasurement:\n");
    localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "Svid", Integer.valueOf(this.mSvid) }));
    localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "ConstellationType", Integer.valueOf(this.mConstellationType) }));
    localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "TimeOffsetNanos", Double.valueOf(this.mTimeOffsetNanos) }));
    localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "State", getStateString() }));
    localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "ReceivedSvTimeNanos", Long.valueOf(this.mReceivedSvTimeNanos), "ReceivedSvTimeUncertaintyNanos", Long.valueOf(this.mReceivedSvTimeUncertaintyNanos) }));
    localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "Cn0DbHz", Double.valueOf(this.mCn0DbHz) }));
    localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "PseudorangeRateMetersPerSecond", Double.valueOf(this.mPseudorangeRateMetersPerSecond), "PseudorangeRateUncertaintyMetersPerSecond", Double.valueOf(this.mPseudorangeRateUncertaintyMetersPerSecond) }));
    localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "AccumulatedDeltaRangeState", getAccumulatedDeltaRangeStateString() }));
    localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "AccumulatedDeltaRangeMeters", Double.valueOf(this.mAccumulatedDeltaRangeMeters), "AccumulatedDeltaRangeUncertaintyMeters", Double.valueOf(this.mAccumulatedDeltaRangeUncertaintyMeters) }));
    Object localObject1;
    if (hasCarrierFrequencyHz())
    {
      localObject1 = Float.valueOf(this.mCarrierFrequencyHz);
      localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "CarrierFrequencyHz", localObject1 }));
      if (!hasCarrierCycles()) {
        break label575;
      }
      localObject1 = Long.valueOf(this.mCarrierCycles);
      label400:
      localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "CarrierCycles", localObject1 }));
      if (!hasCarrierPhase()) {
        break label580;
      }
      localObject1 = Double.valueOf(this.mCarrierPhase);
      label441:
      if (!hasCarrierPhaseUncertainty()) {
        break label585;
      }
    }
    label575:
    label580:
    label585:
    for (Double localDouble = Double.valueOf(this.mCarrierPhaseUncertainty);; localDouble = null)
    {
      localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "CarrierPhase", localObject1, "CarrierPhaseUncertainty", localDouble }));
      localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "MultipathIndicator", getMultipathIndicatorString() }));
      localObject1 = localObject2;
      if (hasSnrInDb()) {
        localObject1 = Double.valueOf(this.mSnrInDb);
      }
      localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "SnrInDb", localObject1 }));
      return localStringBuilder.toString();
      localObject1 = null;
      break;
      localObject1 = null;
      break label400;
      localObject1 = null;
      break label441;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mFlags);
    paramParcel.writeInt(this.mSvid);
    paramParcel.writeInt(this.mConstellationType);
    paramParcel.writeDouble(this.mTimeOffsetNanos);
    paramParcel.writeInt(this.mState);
    paramParcel.writeLong(this.mReceivedSvTimeNanos);
    paramParcel.writeLong(this.mReceivedSvTimeUncertaintyNanos);
    paramParcel.writeDouble(this.mCn0DbHz);
    paramParcel.writeDouble(this.mPseudorangeRateMetersPerSecond);
    paramParcel.writeDouble(this.mPseudorangeRateUncertaintyMetersPerSecond);
    paramParcel.writeInt(this.mAccumulatedDeltaRangeState);
    paramParcel.writeDouble(this.mAccumulatedDeltaRangeMeters);
    paramParcel.writeDouble(this.mAccumulatedDeltaRangeUncertaintyMeters);
    paramParcel.writeFloat(this.mCarrierFrequencyHz);
    paramParcel.writeLong(this.mCarrierCycles);
    paramParcel.writeDouble(this.mCarrierPhase);
    paramParcel.writeDouble(this.mCarrierPhaseUncertainty);
    paramParcel.writeInt(this.mMultipathIndicator);
    paramParcel.writeDouble(this.mSnrInDb);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GnssMeasurement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */