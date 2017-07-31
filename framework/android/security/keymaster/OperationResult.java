package android.security.keymaster;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class OperationResult
  implements Parcelable
{
  public static final Parcelable.Creator<OperationResult> CREATOR = new Parcelable.Creator()
  {
    public OperationResult createFromParcel(Parcel paramAnonymousParcel)
    {
      return new OperationResult(paramAnonymousParcel);
    }
    
    public OperationResult[] newArray(int paramAnonymousInt)
    {
      return new OperationResult[paramAnonymousInt];
    }
  };
  public final int inputConsumed;
  public final long operationHandle;
  public final KeymasterArguments outParams;
  public final byte[] output;
  public final int resultCode;
  public final IBinder token;
  
  public OperationResult(int paramInt1, IBinder paramIBinder, long paramLong, int paramInt2, byte[] paramArrayOfByte, KeymasterArguments paramKeymasterArguments)
  {
    this.resultCode = paramInt1;
    this.token = paramIBinder;
    this.operationHandle = paramLong;
    this.inputConsumed = paramInt2;
    this.output = paramArrayOfByte;
    this.outParams = paramKeymasterArguments;
  }
  
  protected OperationResult(Parcel paramParcel)
  {
    this.resultCode = paramParcel.readInt();
    this.token = paramParcel.readStrongBinder();
    this.operationHandle = paramParcel.readLong();
    this.inputConsumed = paramParcel.readInt();
    this.output = paramParcel.createByteArray();
    this.outParams = ((KeymasterArguments)KeymasterArguments.CREATOR.createFromParcel(paramParcel));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.resultCode);
    paramParcel.writeStrongBinder(this.token);
    paramParcel.writeLong(this.operationHandle);
    paramParcel.writeInt(this.inputConsumed);
    paramParcel.writeByteArray(this.output);
    this.outParams.writeToParcel(paramParcel, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keymaster/OperationResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */