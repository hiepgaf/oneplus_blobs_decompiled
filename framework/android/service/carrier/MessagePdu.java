package android.service.carrier;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class MessagePdu
  implements Parcelable
{
  public static final Parcelable.Creator<MessagePdu> CREATOR = new Parcelable.Creator()
  {
    public MessagePdu createFromParcel(Parcel paramAnonymousParcel)
    {
      int j = paramAnonymousParcel.readInt();
      Object localObject;
      if (j == -1)
      {
        localObject = null;
        return new MessagePdu((List)localObject);
      }
      ArrayList localArrayList = new ArrayList(j);
      int i = 0;
      for (;;)
      {
        localObject = localArrayList;
        if (i >= j) {
          break;
        }
        localArrayList.add(paramAnonymousParcel.createByteArray());
        i += 1;
      }
    }
    
    public MessagePdu[] newArray(int paramAnonymousInt)
    {
      return new MessagePdu[paramAnonymousInt];
    }
  };
  private static final int NULL_LENGTH = -1;
  private final List<byte[]> mPduList;
  
  public MessagePdu(List<byte[]> paramList)
  {
    if ((paramList == null) || (paramList.contains(null))) {
      throw new IllegalArgumentException("pduList must not be null or contain nulls");
    }
    this.mPduList = paramList;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public List<byte[]> getPdus()
  {
    return this.mPduList;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mPduList == null) {
      paramParcel.writeInt(-1);
    }
    for (;;)
    {
      return;
      paramParcel.writeInt(this.mPduList.size());
      Iterator localIterator = this.mPduList.iterator();
      while (localIterator.hasNext()) {
        paramParcel.writeByteArray((byte[])localIterator.next());
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/carrier/MessagePdu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */