package android.nfc;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class NdefMessage
  implements Parcelable
{
  public static final Parcelable.Creator<NdefMessage> CREATOR = new Parcelable.Creator()
  {
    public NdefMessage createFromParcel(Parcel paramAnonymousParcel)
    {
      NdefRecord[] arrayOfNdefRecord = new NdefRecord[paramAnonymousParcel.readInt()];
      paramAnonymousParcel.readTypedArray(arrayOfNdefRecord, NdefRecord.CREATOR);
      return new NdefMessage(arrayOfNdefRecord);
    }
    
    public NdefMessage[] newArray(int paramAnonymousInt)
    {
      return new NdefMessage[paramAnonymousInt];
    }
  };
  private final NdefRecord[] mRecords;
  
  public NdefMessage(NdefRecord paramNdefRecord, NdefRecord... paramVarArgs)
  {
    if (paramNdefRecord == null) {
      throw new NullPointerException("record cannot be null");
    }
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      if (paramVarArgs[i] == null) {
        throw new NullPointerException("record cannot be null");
      }
      i += 1;
    }
    this.mRecords = new NdefRecord[paramVarArgs.length + 1];
    this.mRecords[0] = paramNdefRecord;
    System.arraycopy(paramVarArgs, 0, this.mRecords, 1, paramVarArgs.length);
  }
  
  public NdefMessage(byte[] paramArrayOfByte)
    throws FormatException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException("data is null");
    }
    paramArrayOfByte = ByteBuffer.wrap(paramArrayOfByte);
    this.mRecords = NdefRecord.parse(paramArrayOfByte, false);
    if (paramArrayOfByte.remaining() > 0) {
      throw new FormatException("trailing data");
    }
  }
  
  public NdefMessage(NdefRecord[] paramArrayOfNdefRecord)
  {
    if (paramArrayOfNdefRecord.length < 1) {
      throw new IllegalArgumentException("must have at least one record");
    }
    int i = 0;
    int j = paramArrayOfNdefRecord.length;
    while (i < j)
    {
      if (paramArrayOfNdefRecord[i] == null) {
        throw new NullPointerException("records cannot contain null");
      }
      i += 1;
    }
    this.mRecords = paramArrayOfNdefRecord;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (NdefMessage)paramObject;
    return Arrays.equals(this.mRecords, ((NdefMessage)paramObject).mRecords);
  }
  
  public int getByteArrayLength()
  {
    int j = 0;
    NdefRecord[] arrayOfNdefRecord = this.mRecords;
    int i = 0;
    int k = arrayOfNdefRecord.length;
    while (i < k)
    {
      j += arrayOfNdefRecord[i].getByteLength();
      i += 1;
    }
    return j;
  }
  
  public NdefRecord[] getRecords()
  {
    return this.mRecords;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(this.mRecords);
  }
  
  public byte[] toByteArray()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(getByteArrayLength());
    int i = 0;
    if (i < this.mRecords.length)
    {
      boolean bool1;
      if (i == 0)
      {
        bool1 = true;
        label26:
        if (i != this.mRecords.length - 1) {
          break label64;
        }
      }
      label64:
      for (boolean bool2 = true;; bool2 = false)
      {
        this.mRecords[i].writeToByteBuffer(localByteBuffer, bool1, bool2);
        i += 1;
        break;
        bool1 = false;
        break label26;
      }
    }
    return localByteBuffer.array();
  }
  
  public String toString()
  {
    return "NdefMessage " + Arrays.toString(this.mRecords);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mRecords.length);
    paramParcel.writeTypedArray(this.mRecords, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/NdefMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */