package android.nfc;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.IOException;

public final class TransceiveResult
  implements Parcelable
{
  public static final Parcelable.Creator<TransceiveResult> CREATOR = new Parcelable.Creator()
  {
    public TransceiveResult createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      byte[] arrayOfByte;
      if (i == 0)
      {
        arrayOfByte = new byte[paramAnonymousParcel.readInt()];
        paramAnonymousParcel.readByteArray(arrayOfByte);
      }
      for (paramAnonymousParcel = arrayOfByte;; paramAnonymousParcel = null) {
        return new TransceiveResult(i, paramAnonymousParcel);
      }
    }
    
    public TransceiveResult[] newArray(int paramAnonymousInt)
    {
      return new TransceiveResult[paramAnonymousInt];
    }
  };
  public static final int RESULT_EXCEEDED_LENGTH = 3;
  public static final int RESULT_FAILURE = 1;
  public static final int RESULT_SUCCESS = 0;
  public static final int RESULT_TAGLOST = 2;
  final byte[] mResponseData;
  final int mResult;
  
  public TransceiveResult(int paramInt, byte[] paramArrayOfByte)
  {
    this.mResult = paramInt;
    this.mResponseData = paramArrayOfByte;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public byte[] getResponseOrThrow()
    throws IOException
  {
    switch (this.mResult)
    {
    case 1: 
    default: 
      throw new IOException("Transceive failed");
    case 0: 
      return this.mResponseData;
    case 2: 
      throw new TagLostException("Tag was lost.");
    }
    throw new IOException("Transceive length exceeds supported maximum");
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mResult);
    if (this.mResult == 0)
    {
      paramParcel.writeInt(this.mResponseData.length);
      paramParcel.writeByteArray(this.mResponseData);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/TransceiveResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */