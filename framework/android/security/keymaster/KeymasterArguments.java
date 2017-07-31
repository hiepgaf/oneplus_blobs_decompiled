package android.security.keymaster;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class KeymasterArguments
  implements Parcelable
{
  public static final Parcelable.Creator<KeymasterArguments> CREATOR = new Parcelable.Creator()
  {
    public KeymasterArguments createFromParcel(Parcel paramAnonymousParcel)
    {
      return new KeymasterArguments(paramAnonymousParcel, null);
    }
    
    public KeymasterArguments[] newArray(int paramAnonymousInt)
    {
      return new KeymasterArguments[paramAnonymousInt];
    }
  };
  public static final long UINT32_MAX_VALUE = 4294967295L;
  private static final long UINT32_RANGE = 4294967296L;
  public static final BigInteger UINT64_MAX_VALUE;
  private static final BigInteger UINT64_RANGE = BigInteger.ONE.shiftLeft(64);
  private List<KeymasterArgument> mArguments;
  
  static
  {
    UINT64_MAX_VALUE = UINT64_RANGE.subtract(BigInteger.ONE);
  }
  
  public KeymasterArguments()
  {
    this.mArguments = new ArrayList();
  }
  
  private KeymasterArguments(Parcel paramParcel)
  {
    this.mArguments = paramParcel.createTypedArrayList(KeymasterArgument.CREATOR);
  }
  
  private void addEnumTag(int paramInt1, int paramInt2)
  {
    this.mArguments.add(new KeymasterIntArgument(paramInt1, paramInt2));
  }
  
  private void addLongTag(int paramInt, BigInteger paramBigInteger)
  {
    if ((paramBigInteger.signum() == -1) || (paramBigInteger.compareTo(UINT64_MAX_VALUE) > 0)) {
      throw new IllegalArgumentException("Long tag value out of range: " + paramBigInteger);
    }
    this.mArguments.add(new KeymasterLongArgument(paramInt, paramBigInteger.longValue()));
  }
  
  private KeymasterArgument getArgumentByTag(int paramInt)
  {
    Iterator localIterator = this.mArguments.iterator();
    while (localIterator.hasNext())
    {
      KeymasterArgument localKeymasterArgument = (KeymasterArgument)localIterator.next();
      if (localKeymasterArgument.tag == paramInt) {
        return localKeymasterArgument;
      }
    }
    return null;
  }
  
  private int getEnumTagValue(KeymasterArgument paramKeymasterArgument)
  {
    return ((KeymasterIntArgument)paramKeymasterArgument).value;
  }
  
  private BigInteger getLongTagValue(KeymasterArgument paramKeymasterArgument)
  {
    return toUint64(((KeymasterLongArgument)paramKeymasterArgument).value);
  }
  
  public static BigInteger toUint64(long paramLong)
  {
    if (paramLong >= 0L) {
      return BigInteger.valueOf(paramLong);
    }
    return BigInteger.valueOf(paramLong).add(UINT64_RANGE);
  }
  
  public void addBoolean(int paramInt)
  {
    if (KeymasterDefs.getTagType(paramInt) != 1879048192) {
      throw new IllegalArgumentException("Not a boolean tag: " + paramInt);
    }
    this.mArguments.add(new KeymasterBooleanArgument(paramInt));
  }
  
  public void addBytes(int paramInt, byte[] paramArrayOfByte)
  {
    if (KeymasterDefs.getTagType(paramInt) != -1879048192) {
      throw new IllegalArgumentException("Not a bytes tag: " + paramInt);
    }
    if (paramArrayOfByte == null) {
      throw new NullPointerException("value == nulll");
    }
    this.mArguments.add(new KeymasterBlobArgument(paramInt, paramArrayOfByte));
  }
  
  public void addDate(int paramInt, Date paramDate)
  {
    if (KeymasterDefs.getTagType(paramInt) != 1610612736) {
      throw new IllegalArgumentException("Not a date tag: " + paramInt);
    }
    if (paramDate == null) {
      throw new NullPointerException("value == nulll");
    }
    if (paramDate.getTime() < 0L) {
      throw new IllegalArgumentException("Date tag value out of range: " + paramDate);
    }
    this.mArguments.add(new KeymasterDateArgument(paramInt, paramDate));
  }
  
  public void addDateIfNotNull(int paramInt, Date paramDate)
  {
    if (KeymasterDefs.getTagType(paramInt) != 1610612736) {
      throw new IllegalArgumentException("Not a date tag: " + paramInt);
    }
    if (paramDate != null) {
      addDate(paramInt, paramDate);
    }
  }
  
  public void addEnum(int paramInt1, int paramInt2)
  {
    int i = KeymasterDefs.getTagType(paramInt1);
    if ((i != 268435456) && (i != 536870912)) {
      throw new IllegalArgumentException("Not an enum or repeating enum tag: " + paramInt1);
    }
    addEnumTag(paramInt1, paramInt2);
  }
  
  public void addEnums(int paramInt, int... paramVarArgs)
  {
    if (KeymasterDefs.getTagType(paramInt) != 536870912) {
      throw new IllegalArgumentException("Not a repeating enum tag: " + paramInt);
    }
    int i = 0;
    int j = paramVarArgs.length;
    while (i < j)
    {
      addEnumTag(paramInt, paramVarArgs[i]);
      i += 1;
    }
  }
  
  public void addUnsignedInt(int paramInt, long paramLong)
  {
    int i = KeymasterDefs.getTagType(paramInt);
    if ((i != 805306368) && (i != 1073741824)) {
      throw new IllegalArgumentException("Not an int or repeating int tag: " + paramInt);
    }
    if ((paramLong < 0L) || (paramLong > 4294967295L)) {
      throw new IllegalArgumentException("Int tag value out of range: " + paramLong);
    }
    this.mArguments.add(new KeymasterIntArgument(paramInt, (int)paramLong));
  }
  
  public void addUnsignedLong(int paramInt, BigInteger paramBigInteger)
  {
    int i = KeymasterDefs.getTagType(paramInt);
    if ((i != 1342177280) && (i != -1610612736)) {
      throw new IllegalArgumentException("Not a long or repeating long tag: " + paramInt);
    }
    addLongTag(paramInt, paramBigInteger);
  }
  
  public boolean containsTag(int paramInt)
  {
    return getArgumentByTag(paramInt) != null;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean getBoolean(int paramInt)
  {
    if (KeymasterDefs.getTagType(paramInt) != 1879048192) {
      throw new IllegalArgumentException("Not a boolean tag: " + paramInt);
    }
    return getArgumentByTag(paramInt) != null;
  }
  
  public byte[] getBytes(int paramInt, byte[] paramArrayOfByte)
  {
    if (KeymasterDefs.getTagType(paramInt) != -1879048192) {
      throw new IllegalArgumentException("Not a bytes tag: " + paramInt);
    }
    KeymasterArgument localKeymasterArgument = getArgumentByTag(paramInt);
    if (localKeymasterArgument == null) {
      return paramArrayOfByte;
    }
    return ((KeymasterBlobArgument)localKeymasterArgument).blob;
  }
  
  public Date getDate(int paramInt, Date paramDate)
  {
    if (KeymasterDefs.getTagType(paramInt) != 1610612736) {
      throw new IllegalArgumentException("Tag is not a date type: " + paramInt);
    }
    KeymasterArgument localKeymasterArgument = getArgumentByTag(paramInt);
    if (localKeymasterArgument == null) {
      return paramDate;
    }
    paramDate = ((KeymasterDateArgument)localKeymasterArgument).date;
    if (paramDate.getTime() < 0L) {
      throw new IllegalArgumentException("Tag value too large. Tag: " + paramInt);
    }
    return paramDate;
  }
  
  public int getEnum(int paramInt1, int paramInt2)
  {
    if (KeymasterDefs.getTagType(paramInt1) != 268435456) {
      throw new IllegalArgumentException("Not an enum tag: " + paramInt1);
    }
    KeymasterArgument localKeymasterArgument = getArgumentByTag(paramInt1);
    if (localKeymasterArgument == null) {
      return paramInt2;
    }
    return getEnumTagValue(localKeymasterArgument);
  }
  
  public List<Integer> getEnums(int paramInt)
  {
    if (KeymasterDefs.getTagType(paramInt) != 536870912) {
      throw new IllegalArgumentException("Not a repeating enum tag: " + paramInt);
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mArguments.iterator();
    while (localIterator.hasNext())
    {
      KeymasterArgument localKeymasterArgument = (KeymasterArgument)localIterator.next();
      if (localKeymasterArgument.tag == paramInt) {
        localArrayList.add(Integer.valueOf(getEnumTagValue(localKeymasterArgument)));
      }
    }
    return localArrayList;
  }
  
  public long getUnsignedInt(int paramInt, long paramLong)
  {
    if (KeymasterDefs.getTagType(paramInt) != 805306368) {
      throw new IllegalArgumentException("Not an int tag: " + paramInt);
    }
    KeymasterArgument localKeymasterArgument = getArgumentByTag(paramInt);
    if (localKeymasterArgument == null) {
      return paramLong;
    }
    return ((KeymasterIntArgument)localKeymasterArgument).value & 0xFFFFFFFF;
  }
  
  public List<BigInteger> getUnsignedLongs(int paramInt)
  {
    if (KeymasterDefs.getTagType(paramInt) != -1610612736) {
      throw new IllegalArgumentException("Tag is not a repeating long: " + paramInt);
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mArguments.iterator();
    while (localIterator.hasNext())
    {
      KeymasterArgument localKeymasterArgument = (KeymasterArgument)localIterator.next();
      if (localKeymasterArgument.tag == paramInt) {
        localArrayList.add(getLongTagValue(localKeymasterArgument));
      }
    }
    return localArrayList;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    paramParcel.readTypedList(this.mArguments, KeymasterArgument.CREATOR);
  }
  
  public int size()
  {
    return this.mArguments.size();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeTypedList(this.mArguments);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keymaster/KeymasterArguments.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */