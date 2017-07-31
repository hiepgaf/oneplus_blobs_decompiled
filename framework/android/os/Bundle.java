package android.os;

import android.util.ArrayMap;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Bundle
  extends BaseBundle
  implements Cloneable, Parcelable
{
  public static final Parcelable.Creator<Bundle> CREATOR = new Parcelable.Creator()
  {
    public Bundle createFromParcel(Parcel paramAnonymousParcel)
    {
      return paramAnonymousParcel.readBundle();
    }
    
    public Bundle[] newArray(int paramAnonymousInt)
    {
      return new Bundle[paramAnonymousInt];
    }
  };
  public static final Bundle EMPTY = new Bundle();
  private static final int FLAG_ALLOW_FDS = 1024;
  private static final int FLAG_HAS_FDS = 256;
  private static final int FLAG_HAS_FDS_KNOWN = 512;
  
  static
  {
    EMPTY.mMap = ArrayMap.EMPTY;
  }
  
  public Bundle()
  {
    this.mFlags = 1536;
  }
  
  public Bundle(int paramInt)
  {
    super(paramInt);
    this.mFlags = 1536;
  }
  
  public Bundle(Bundle paramBundle)
  {
    super(paramBundle);
    this.mFlags = paramBundle.mFlags;
  }
  
  Bundle(Parcel paramParcel)
  {
    super(paramParcel);
    this.mFlags = 1536;
    if (this.mParcelledData.hasFileDescriptors()) {
      this.mFlags |= 0x100;
    }
  }
  
  Bundle(Parcel paramParcel, int paramInt)
  {
    super(paramParcel, paramInt);
    this.mFlags = 1536;
    if (this.mParcelledData.hasFileDescriptors()) {
      this.mFlags |= 0x100;
    }
  }
  
  public Bundle(PersistableBundle paramPersistableBundle)
  {
    super(paramPersistableBundle);
    this.mFlags = 1536;
  }
  
  public Bundle(ClassLoader paramClassLoader)
  {
    super(paramClassLoader);
    this.mFlags = 1536;
  }
  
  public static Bundle forPair(String paramString1, String paramString2)
  {
    Bundle localBundle = new Bundle(1);
    localBundle.putString(paramString1, paramString2);
    return localBundle;
  }
  
  public static Bundle setDefusable(Bundle paramBundle, boolean paramBoolean)
  {
    if (paramBundle != null) {
      paramBundle.setDefusable(paramBoolean);
    }
    return paramBundle;
  }
  
  public void clear()
  {
    super.clear();
    this.mFlags = 1536;
  }
  
  public Object clone()
  {
    return new Bundle(this);
  }
  
  public int describeContents()
  {
    int i = 0;
    if (hasFileDescriptors()) {
      i = 1;
    }
    return i;
  }
  
  public Bundle filterValues()
  {
    unparcel();
    Object localObject1 = this;
    Object localObject3 = localObject1;
    if (this.mMap != null)
    {
      Object localObject2 = this.mMap;
      int i = ((ArrayMap)localObject2).size() - 1;
      localObject3 = localObject1;
      if (i >= 0)
      {
        Object localObject5 = ((ArrayMap)localObject2).valueAt(i);
        Object localObject4;
        if (PersistableBundle.isValidType(localObject5))
        {
          localObject4 = localObject2;
          localObject3 = localObject1;
        }
        for (;;)
        {
          i -= 1;
          localObject1 = localObject3;
          localObject2 = localObject4;
          break;
          if ((localObject5 instanceof Bundle))
          {
            Bundle localBundle = ((Bundle)localObject5).filterValues();
            localObject3 = localObject1;
            localObject4 = localObject2;
            if (localBundle != localObject5)
            {
              localObject4 = localObject2;
              if (localObject2 == this.mMap)
              {
                localObject1 = new Bundle(this);
                localObject4 = ((Bundle)localObject1).mMap;
              }
              ((ArrayMap)localObject4).setValueAt(i, localBundle);
              localObject3 = localObject1;
            }
          }
          else
          {
            localObject3 = localObject1;
            localObject4 = localObject2;
            if (!localObject5.getClass().getName().startsWith("android."))
            {
              localObject4 = localObject2;
              if (localObject2 == this.mMap)
              {
                localObject1 = new Bundle(this);
                localObject4 = ((Bundle)localObject1).mMap;
              }
              ((ArrayMap)localObject4).removeAt(i);
              localObject3 = localObject1;
            }
          }
        }
      }
    }
    this.mFlags |= 0x200;
    this.mFlags &= 0xFEFF;
    return (Bundle)localObject3;
  }
  
  public IBinder getBinder(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      IBinder localIBinder = (IBinder)localObject;
      return localIBinder;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "IBinder", localClassCastException);
    }
    return null;
  }
  
  public Bundle getBundle(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      Bundle localBundle = (Bundle)localObject;
      return localBundle;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Bundle", localClassCastException);
    }
    return null;
  }
  
  public byte getByte(String paramString)
  {
    return super.getByte(paramString);
  }
  
  public Byte getByte(String paramString, byte paramByte)
  {
    return super.getByte(paramString, paramByte);
  }
  
  public byte[] getByteArray(String paramString)
  {
    return super.getByteArray(paramString);
  }
  
  public char getChar(String paramString)
  {
    return super.getChar(paramString);
  }
  
  public char getChar(String paramString, char paramChar)
  {
    return super.getChar(paramString, paramChar);
  }
  
  public char[] getCharArray(String paramString)
  {
    return super.getCharArray(paramString);
  }
  
  public CharSequence getCharSequence(String paramString)
  {
    return super.getCharSequence(paramString);
  }
  
  public CharSequence getCharSequence(String paramString, CharSequence paramCharSequence)
  {
    return super.getCharSequence(paramString, paramCharSequence);
  }
  
  public CharSequence[] getCharSequenceArray(String paramString)
  {
    return super.getCharSequenceArray(paramString);
  }
  
  public ArrayList<CharSequence> getCharSequenceArrayList(String paramString)
  {
    return super.getCharSequenceArrayList(paramString);
  }
  
  public ClassLoader getClassLoader()
  {
    return super.getClassLoader();
  }
  
  public float getFloat(String paramString)
  {
    return super.getFloat(paramString);
  }
  
  public float getFloat(String paramString, float paramFloat)
  {
    return super.getFloat(paramString, paramFloat);
  }
  
  public float[] getFloatArray(String paramString)
  {
    return super.getFloatArray(paramString);
  }
  
  @Deprecated
  public IBinder getIBinder(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      IBinder localIBinder = (IBinder)localObject;
      return localIBinder;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "IBinder", localClassCastException);
    }
    return null;
  }
  
  public ArrayList<Integer> getIntegerArrayList(String paramString)
  {
    return super.getIntegerArrayList(paramString);
  }
  
  public <T extends Parcelable> T getParcelable(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      Parcelable localParcelable = (Parcelable)localObject;
      return localParcelable;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Parcelable", localClassCastException);
    }
    return null;
  }
  
  public Parcelable[] getParcelableArray(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      Parcelable[] arrayOfParcelable = (Parcelable[])localObject;
      return arrayOfParcelable;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Parcelable[]", localClassCastException);
    }
    return null;
  }
  
  public <T extends Parcelable> ArrayList<T> getParcelableArrayList(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      ArrayList localArrayList = (ArrayList)localObject;
      return localArrayList;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "ArrayList", localClassCastException);
    }
    return null;
  }
  
  public Serializable getSerializable(String paramString)
  {
    return super.getSerializable(paramString);
  }
  
  public short getShort(String paramString)
  {
    return super.getShort(paramString);
  }
  
  public short getShort(String paramString, short paramShort)
  {
    return super.getShort(paramString, paramShort);
  }
  
  public short[] getShortArray(String paramString)
  {
    return super.getShortArray(paramString);
  }
  
  public Size getSize(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    try
    {
      Size localSize = (Size)localObject;
      return localSize;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Size", localClassCastException);
    }
    return null;
  }
  
  public SizeF getSizeF(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    try
    {
      SizeF localSizeF = (SizeF)localObject;
      return localSizeF;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "SizeF", localClassCastException);
    }
    return null;
  }
  
  public <T extends Parcelable> SparseArray<T> getSparseParcelableArray(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      SparseArray localSparseArray = (SparseArray)localObject;
      return localSparseArray;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "SparseArray", localClassCastException);
    }
    return null;
  }
  
  public ArrayList<String> getStringArrayList(String paramString)
  {
    return super.getStringArrayList(paramString);
  }
  
  public boolean hasFileDescriptors()
  {
    boolean bool = false;
    int j;
    int i;
    if ((this.mFlags & 0x200) == 0)
    {
      j = 0;
      i = 0;
      if (this.mParcelledData == null) {
        break label82;
      }
      if (this.mParcelledData.hasFileDescriptors()) {
        i = 1;
      }
      if (i == 0) {
        break label385;
      }
    }
    label82:
    label160:
    label193:
    label202:
    label385:
    for (this.mFlags |= 0x100;; this.mFlags &= 0xFEFF)
    {
      this.mFlags |= 0x200;
      if ((this.mFlags & 0x100) != 0) {
        bool = true;
      }
      return bool;
      int k = this.mMap.size() - 1;
      i = j;
      if (k < 0) {
        break;
      }
      Object localObject = this.mMap.valueAt(k);
      if ((localObject instanceof Parcelable))
      {
        i = j;
        if ((((Parcelable)localObject).describeContents() & 0x1) == 0) {
          break label193;
        }
        i = 1;
        break;
      }
      Parcelable localParcelable;
      if ((localObject instanceof Parcelable[]))
      {
        localObject = (Parcelable[])localObject;
        m = localObject.length - 1;
        i = j;
        if (m >= 0)
        {
          localParcelable = localObject[m];
          if ((localParcelable == null) || ((localParcelable.describeContents() & 0x1) == 0)) {
            break label202;
          }
          i = 1;
        }
      }
      do
      {
        do
        {
          do
          {
            k -= 1;
            j = i;
            break;
            m -= 1;
            break label160;
            if ((localObject instanceof SparseArray))
            {
              localObject = (SparseArray)localObject;
              m = ((SparseArray)localObject).size() - 1;
              for (;;)
              {
                i = j;
                if (m < 0) {
                  break;
                }
                localParcelable = (Parcelable)((SparseArray)localObject).valueAt(m);
                if ((localParcelable != null) && ((localParcelable.describeContents() & 0x1) != 0))
                {
                  i = 1;
                  break;
                }
                m -= 1;
              }
            }
            i = j;
          } while (!(localObject instanceof ArrayList));
          localObject = (ArrayList)localObject;
          i = j;
        } while (((ArrayList)localObject).isEmpty());
        i = j;
      } while (!(((ArrayList)localObject).get(0) instanceof Parcelable));
      int m = ((ArrayList)localObject).size() - 1;
      for (;;)
      {
        i = j;
        if (m < 0) {
          break;
        }
        localParcelable = (Parcelable)((ArrayList)localObject).get(m);
        if ((localParcelable != null) && ((localParcelable.describeContents() & 0x1) != 0))
        {
          i = 1;
          break;
        }
        m -= 1;
      }
    }
  }
  
  public void putAll(Bundle paramBundle)
  {
    unparcel();
    paramBundle.unparcel();
    this.mMap.putAll(paramBundle.mMap);
    if ((paramBundle.mFlags & 0x100) != 0) {
      this.mFlags |= 0x100;
    }
    if ((paramBundle.mFlags & 0x200) == 0) {
      this.mFlags &= 0xFDFF;
    }
  }
  
  public void putBinder(String paramString, IBinder paramIBinder)
  {
    unparcel();
    this.mMap.put(paramString, paramIBinder);
  }
  
  public void putBundle(String paramString, Bundle paramBundle)
  {
    unparcel();
    this.mMap.put(paramString, paramBundle);
  }
  
  public void putByte(String paramString, byte paramByte)
  {
    super.putByte(paramString, paramByte);
  }
  
  public void putByteArray(String paramString, byte[] paramArrayOfByte)
  {
    super.putByteArray(paramString, paramArrayOfByte);
  }
  
  public void putChar(String paramString, char paramChar)
  {
    super.putChar(paramString, paramChar);
  }
  
  public void putCharArray(String paramString, char[] paramArrayOfChar)
  {
    super.putCharArray(paramString, paramArrayOfChar);
  }
  
  public void putCharSequence(String paramString, CharSequence paramCharSequence)
  {
    super.putCharSequence(paramString, paramCharSequence);
  }
  
  public void putCharSequenceArray(String paramString, CharSequence[] paramArrayOfCharSequence)
  {
    super.putCharSequenceArray(paramString, paramArrayOfCharSequence);
  }
  
  public void putCharSequenceArrayList(String paramString, ArrayList<CharSequence> paramArrayList)
  {
    super.putCharSequenceArrayList(paramString, paramArrayList);
  }
  
  public void putFloat(String paramString, float paramFloat)
  {
    super.putFloat(paramString, paramFloat);
  }
  
  public void putFloatArray(String paramString, float[] paramArrayOfFloat)
  {
    super.putFloatArray(paramString, paramArrayOfFloat);
  }
  
  @Deprecated
  public void putIBinder(String paramString, IBinder paramIBinder)
  {
    unparcel();
    this.mMap.put(paramString, paramIBinder);
  }
  
  public void putIntegerArrayList(String paramString, ArrayList<Integer> paramArrayList)
  {
    super.putIntegerArrayList(paramString, paramArrayList);
  }
  
  public void putParcelable(String paramString, Parcelable paramParcelable)
  {
    unparcel();
    this.mMap.put(paramString, paramParcelable);
    this.mFlags &= 0xFDFF;
  }
  
  public void putParcelableArray(String paramString, Parcelable[] paramArrayOfParcelable)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayOfParcelable);
    this.mFlags &= 0xFDFF;
  }
  
  public void putParcelableArrayList(String paramString, ArrayList<? extends Parcelable> paramArrayList)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayList);
    this.mFlags &= 0xFDFF;
  }
  
  public void putParcelableList(String paramString, List<? extends Parcelable> paramList)
  {
    unparcel();
    this.mMap.put(paramString, paramList);
    this.mFlags &= 0xFDFF;
  }
  
  public void putSerializable(String paramString, Serializable paramSerializable)
  {
    super.putSerializable(paramString, paramSerializable);
  }
  
  public void putShort(String paramString, short paramShort)
  {
    super.putShort(paramString, paramShort);
  }
  
  public void putShortArray(String paramString, short[] paramArrayOfShort)
  {
    super.putShortArray(paramString, paramArrayOfShort);
  }
  
  public void putSize(String paramString, Size paramSize)
  {
    unparcel();
    this.mMap.put(paramString, paramSize);
  }
  
  public void putSizeF(String paramString, SizeF paramSizeF)
  {
    unparcel();
    this.mMap.put(paramString, paramSizeF);
  }
  
  public void putSparseParcelableArray(String paramString, SparseArray<? extends Parcelable> paramSparseArray)
  {
    unparcel();
    this.mMap.put(paramString, paramSparseArray);
    this.mFlags &= 0xFDFF;
  }
  
  public void putStringArrayList(String paramString, ArrayList<String> paramArrayList)
  {
    super.putStringArrayList(paramString, paramArrayList);
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    super.readFromParcelInner(paramParcel);
    this.mFlags = 1536;
    if (this.mParcelledData.hasFileDescriptors()) {
      this.mFlags |= 0x100;
    }
  }
  
  public void remove(String paramString)
  {
    super.remove(paramString);
    if ((this.mFlags & 0x100) != 0) {
      this.mFlags &= 0xFDFF;
    }
  }
  
  public boolean setAllowFds(boolean paramBoolean)
  {
    if ((this.mFlags & 0x400) != 0) {}
    for (boolean bool = true; paramBoolean; bool = false)
    {
      this.mFlags |= 0x400;
      return bool;
    }
    this.mFlags &= 0xFBFF;
    return bool;
  }
  
  public void setClassLoader(ClassLoader paramClassLoader)
  {
    super.setClassLoader(paramClassLoader);
  }
  
  public void setDefusable(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mFlags |= 0x1;
      return;
    }
    this.mFlags &= 0xFFFFFFFE;
  }
  
  public String toString()
  {
    try
    {
      if (this.mParcelledData != null)
      {
        if (isEmptyParcel()) {
          return "Bundle[EMPTY_PARCEL]";
        }
        str = "Bundle[mParcelledData.dataSize=" + this.mParcelledData.dataSize() + "]";
        return str;
      }
      String str = "Bundle[" + this.mMap.toString() + "]";
      return str;
    }
    finally {}
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    boolean bool = false;
    if ((this.mFlags & 0x400) != 0) {
      bool = true;
    }
    bool = paramParcel.pushAllowFds(bool);
    try
    {
      super.writeToParcelInner(paramParcel, paramInt);
      return;
    }
    finally
    {
      paramParcel.restoreAllowFds(bool);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Bundle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */