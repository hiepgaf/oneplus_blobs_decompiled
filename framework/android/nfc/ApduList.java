package android.nfc;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApduList
  implements Parcelable
{
  public static final Parcelable.Creator<ApduList> CREATOR = new Parcelable.Creator()
  {
    public ApduList createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ApduList(paramAnonymousParcel, null);
    }
    
    public ApduList[] newArray(int paramAnonymousInt)
    {
      return new ApduList[paramAnonymousInt];
    }
  };
  private ArrayList<byte[]> commands = new ArrayList();
  
  public ApduList() {}
  
  private ApduList(Parcel paramParcel)
  {
    int j = paramParcel.readInt();
    int i = 0;
    while (i < j)
    {
      byte[] arrayOfByte = new byte[paramParcel.readInt()];
      paramParcel.readByteArray(arrayOfByte);
      this.commands.add(arrayOfByte);
      i += 1;
    }
  }
  
  public void add(byte[] paramArrayOfByte)
  {
    this.commands.add(paramArrayOfByte);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public List<byte[]> get()
  {
    return this.commands;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.commands.size());
    Iterator localIterator = this.commands.iterator();
    while (localIterator.hasNext())
    {
      byte[] arrayOfByte = (byte[])localIterator.next();
      paramParcel.writeInt(arrayOfByte.length);
      paramParcel.writeByteArray(arrayOfByte);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/ApduList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */