package com.google.protobuf.nano.android;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.protobuf.nano.ExtendableMessageNano;

public abstract class ParcelableExtendableMessageNano<M extends ExtendableMessageNano<M>>
  extends ExtendableMessageNano<M>
  implements Parcelable
{
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    ParcelableMessageNanoCreator.writeToParcel(getClass(), this, paramParcel);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/google/protobuf/nano/android/ParcelableExtendableMessageNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */