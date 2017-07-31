package android.os;

import android.util.MathUtils;

public class ParcelableParcel
  implements Parcelable
{
  public static final Parcelable.ClassLoaderCreator<ParcelableParcel> CREATOR = new Parcelable.ClassLoaderCreator()
  {
    public ParcelableParcel createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ParcelableParcel(paramAnonymousParcel, null);
    }
    
    public ParcelableParcel createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
    {
      return new ParcelableParcel(paramAnonymousParcel, paramAnonymousClassLoader);
    }
    
    public ParcelableParcel[] newArray(int paramAnonymousInt)
    {
      return new ParcelableParcel[paramAnonymousInt];
    }
  };
  final ClassLoader mClassLoader;
  final Parcel mParcel = Parcel.obtain();
  
  public ParcelableParcel(Parcel paramParcel, ClassLoader paramClassLoader)
  {
    this.mClassLoader = paramClassLoader;
    int i = paramParcel.readInt();
    if (i < 0) {
      throw new IllegalArgumentException("Negative size read from parcel");
    }
    int j = paramParcel.dataPosition();
    paramParcel.setDataPosition(MathUtils.addOrThrow(j, i));
    this.mParcel.appendFrom(paramParcel, j, i);
  }
  
  public ParcelableParcel(ClassLoader paramClassLoader)
  {
    this.mClassLoader = paramClassLoader;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public ClassLoader getClassLoader()
  {
    return this.mClassLoader;
  }
  
  public Parcel getParcel()
  {
    this.mParcel.setDataPosition(0);
    return this.mParcel;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mParcel.dataSize());
    paramParcel.appendFrom(this.mParcel, 0, this.mParcel.dataSize());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/ParcelableParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */