package android.os;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import dalvik.system.VMRuntime;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Parcel
{
  private static final boolean DEBUG_ARRAY_MAP = false;
  private static final boolean DEBUG_RECYCLE = false;
  private static final int EX_BAD_PARCELABLE = -2;
  private static final int EX_HAS_REPLY_HEADER = -128;
  private static final int EX_ILLEGAL_ARGUMENT = -3;
  private static final int EX_ILLEGAL_STATE = -5;
  private static final int EX_NETWORK_MAIN_THREAD = -6;
  private static final int EX_NULL_POINTER = -4;
  private static final int EX_SECURITY = -1;
  private static final int EX_SERVICE_SPECIFIC = -8;
  private static final int EX_TRANSACTION_FAILED = -129;
  private static final int EX_UNSUPPORTED_OPERATION = -7;
  private static final int POOL_SIZE = 6;
  public static final Parcelable.Creator<String> STRING_CREATOR = new Parcelable.Creator()
  {
    public String createFromParcel(Parcel paramAnonymousParcel)
    {
      return paramAnonymousParcel.readString();
    }
    
    public String[] newArray(int paramAnonymousInt)
    {
      return new String[paramAnonymousInt];
    }
  };
  private static final String TAG = "Parcel";
  private static final int VAL_BOOLEAN = 9;
  private static final int VAL_BOOLEANARRAY = 23;
  private static final int VAL_BUNDLE = 3;
  private static final int VAL_BYTE = 20;
  private static final int VAL_BYTEARRAY = 13;
  private static final int VAL_CHARSEQUENCE = 10;
  private static final int VAL_CHARSEQUENCEARRAY = 24;
  private static final int VAL_DOUBLE = 8;
  private static final int VAL_DOUBLEARRAY = 28;
  private static final int VAL_FLOAT = 7;
  private static final int VAL_IBINDER = 15;
  private static final int VAL_INTARRAY = 18;
  private static final int VAL_INTEGER = 1;
  private static final int VAL_LIST = 11;
  private static final int VAL_LONG = 6;
  private static final int VAL_LONGARRAY = 19;
  private static final int VAL_MAP = 2;
  private static final int VAL_NULL = -1;
  private static final int VAL_OBJECTARRAY = 17;
  private static final int VAL_PARCELABLE = 4;
  private static final int VAL_PARCELABLEARRAY = 16;
  private static final int VAL_PERSISTABLEBUNDLE = 25;
  private static final int VAL_SERIALIZABLE = 21;
  private static final int VAL_SHORT = 5;
  private static final int VAL_SIZE = 26;
  private static final int VAL_SIZEF = 27;
  private static final int VAL_SPARSEARRAY = 12;
  private static final int VAL_SPARSEBOOLEANARRAY = 22;
  private static final int VAL_STRING = 0;
  private static final int VAL_STRINGARRAY = 14;
  private static final HashMap<ClassLoader, HashMap<String, Parcelable.Creator<?>>> mCreators = new HashMap();
  private static final Parcel[] sHolderPool;
  private static final Parcel[] sOwnedPool = new Parcel[6];
  private long mNativePtr;
  private long mNativeSize;
  private boolean mOwnsNativeParcelObject;
  private RuntimeException mStack;
  
  static
  {
    sHolderPool = new Parcel[6];
  }
  
  private Parcel(long paramLong)
  {
    init(paramLong);
  }
  
  static native void clearFileDescriptor(FileDescriptor paramFileDescriptor);
  
  static native void closeFileDescriptor(FileDescriptor paramFileDescriptor)
    throws IOException;
  
  private void destroy()
  {
    if (this.mNativePtr != 0L)
    {
      if (this.mOwnsNativeParcelObject)
      {
        nativeDestroy(this.mNativePtr);
        updateNativeSize(0L);
      }
      this.mNativePtr = 0L;
    }
  }
  
  static native FileDescriptor dupFileDescriptor(FileDescriptor paramFileDescriptor)
    throws IOException;
  
  private void freeBuffer()
  {
    if (this.mOwnsNativeParcelObject) {
      updateNativeSize(nativeFreeBuffer(this.mNativePtr));
    }
  }
  
  public static native long getGlobalAllocCount();
  
  public static native long getGlobalAllocSize();
  
  private void init(long paramLong)
  {
    if (paramLong != 0L)
    {
      this.mNativePtr = paramLong;
      this.mOwnsNativeParcelObject = false;
      return;
    }
    this.mNativePtr = nativeCreate();
    this.mOwnsNativeParcelObject = true;
  }
  
  private static native long nativeAppendFrom(long paramLong1, long paramLong2, int paramInt1, int paramInt2);
  
  private static native long nativeCreate();
  
  private static native byte[] nativeCreateByteArray(long paramLong);
  
  private static native int nativeDataAvail(long paramLong);
  
  private static native int nativeDataCapacity(long paramLong);
  
  private static native int nativeDataPosition(long paramLong);
  
  private static native int nativeDataSize(long paramLong);
  
  private static native void nativeDestroy(long paramLong);
  
  private static native void nativeEnforceInterface(long paramLong, String paramString);
  
  private static native long nativeFreeBuffer(long paramLong);
  
  private static native long nativeGetBlobAshmemSize(long paramLong);
  
  private static native boolean nativeHasFileDescriptors(long paramLong);
  
  private static native byte[] nativeMarshall(long paramLong);
  
  private static native boolean nativePushAllowFds(long paramLong, boolean paramBoolean);
  
  private static native byte[] nativeReadBlob(long paramLong);
  
  private static native double nativeReadDouble(long paramLong);
  
  private static native FileDescriptor nativeReadFileDescriptor(long paramLong);
  
  private static native float nativeReadFloat(long paramLong);
  
  private static native int nativeReadInt(long paramLong);
  
  private static native long nativeReadLong(long paramLong);
  
  private static native String nativeReadString(long paramLong);
  
  private static native IBinder nativeReadStrongBinder(long paramLong);
  
  private static native void nativeRestoreAllowFds(long paramLong, boolean paramBoolean);
  
  private static native void nativeSetDataCapacity(long paramLong, int paramInt);
  
  private static native void nativeSetDataPosition(long paramLong, int paramInt);
  
  private static native long nativeSetDataSize(long paramLong, int paramInt);
  
  private static native long nativeUnmarshall(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static native void nativeWriteBlob(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static native void nativeWriteByteArray(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static native void nativeWriteDouble(long paramLong, double paramDouble);
  
  private static native long nativeWriteFileDescriptor(long paramLong, FileDescriptor paramFileDescriptor);
  
  private static native void nativeWriteFloat(long paramLong, float paramFloat);
  
  private static native void nativeWriteInt(long paramLong, int paramInt);
  
  private static native void nativeWriteInterfaceToken(long paramLong, String paramString);
  
  private static native void nativeWriteLong(long paramLong1, long paramLong2);
  
  private static native void nativeWriteString(long paramLong, String paramString);
  
  private static native void nativeWriteStrongBinder(long paramLong, IBinder paramIBinder);
  
  public static Parcel obtain()
  {
    Parcel[] arrayOfParcel = sOwnedPool;
    int i = 0;
    while (i < 6)
    {
      Parcel localParcel = arrayOfParcel[i];
      if (localParcel != null)
      {
        arrayOfParcel[i] = null;
        return localParcel;
      }
      i += 1;
    }
    return new Parcel(0L);
  }
  
  protected static final Parcel obtain(int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  protected static final Parcel obtain(long paramLong)
  {
    arrayOfParcel = sHolderPool;
    int i = 0;
    while (i < 6)
    {
      Parcel localParcel = arrayOfParcel[i];
      if (localParcel != null) {
        arrayOfParcel[i] = null;
      }
      try
      {
        localParcel.init(paramLong);
        return localParcel;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
      i += 1;
    }
    return new Parcel(paramLong);
  }
  
  static native FileDescriptor openFileDescriptor(String paramString, int paramInt)
    throws FileNotFoundException;
  
  private void readArrayInternal(Object[] paramArrayOfObject, int paramInt, ClassLoader paramClassLoader)
  {
    int i = 0;
    while (i < paramInt)
    {
      paramArrayOfObject[i] = readValue(paramClassLoader);
      i += 1;
    }
  }
  
  private void readListInternal(List paramList, int paramInt, ClassLoader paramClassLoader)
  {
    while (paramInt > 0)
    {
      paramList.add(readValue(paramClassLoader));
      paramInt -= 1;
    }
  }
  
  private final Serializable readSerializable(final ClassLoader paramClassLoader)
  {
    String str = readString();
    if (str == null) {
      return null;
    }
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(createByteArray());
    try
    {
      paramClassLoader = (Serializable)new ObjectInputStream(localByteArrayInputStream)
      {
        protected Class<?> resolveClass(ObjectStreamClass paramAnonymousObjectStreamClass)
          throws IOException, ClassNotFoundException
        {
          if (paramClassLoader != null)
          {
            Class localClass = Class.forName(paramAnonymousObjectStreamClass.getName(), false, paramClassLoader);
            if (localClass != null) {
              return localClass;
            }
          }
          return super.resolveClass(paramAnonymousObjectStreamClass);
        }
      }.readObject();
      return paramClassLoader;
    }
    catch (ClassNotFoundException paramClassLoader)
    {
      throw new RuntimeException("Parcelable encountered ClassNotFoundException reading a Serializable object (name = " + str + ")", paramClassLoader);
    }
    catch (IOException paramClassLoader)
    {
      throw new RuntimeException("Parcelable encountered IOException reading a Serializable object (name = " + str + ")", paramClassLoader);
    }
  }
  
  private void readSparseArrayInternal(SparseArray paramSparseArray, int paramInt, ClassLoader paramClassLoader)
  {
    while (paramInt > 0)
    {
      paramSparseArray.append(readInt(), readValue(paramClassLoader));
      paramInt -= 1;
    }
  }
  
  private void readSparseBooleanArrayInternal(SparseBooleanArray paramSparseBooleanArray, int paramInt)
  {
    if (paramInt > 0)
    {
      int i = readInt();
      if (readByte() == 1) {}
      for (boolean bool = true;; bool = false)
      {
        paramSparseBooleanArray.append(i, bool);
        paramInt -= 1;
        break;
      }
    }
  }
  
  private void updateNativeSize(long paramLong)
  {
    long l;
    int i;
    if (this.mOwnsNativeParcelObject)
    {
      l = paramLong;
      if (paramLong > 2147483647L) {
        l = 2147483647L;
      }
      if (l != this.mNativeSize)
      {
        i = (int)(l - this.mNativeSize);
        if (i <= 0) {
          break label60;
        }
        VMRuntime.getRuntime().registerNativeAllocation(i);
      }
    }
    for (;;)
    {
      this.mNativeSize = l;
      return;
      label60:
      VMRuntime.getRuntime().registerNativeFree(-i);
    }
  }
  
  public final void appendFrom(Parcel paramParcel, int paramInt1, int paramInt2)
  {
    updateNativeSize(nativeAppendFrom(this.mNativePtr, paramParcel.mNativePtr, paramInt1, paramInt2));
  }
  
  public final IBinder[] createBinderArray()
  {
    int j = readInt();
    if (j >= 0)
    {
      IBinder[] arrayOfIBinder = new IBinder[j];
      int i = 0;
      while (i < j)
      {
        arrayOfIBinder[i] = readStrongBinder();
        i += 1;
      }
      return arrayOfIBinder;
    }
    return null;
  }
  
  public final ArrayList<IBinder> createBinderArrayList()
  {
    int i = readInt();
    if (i < 0) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(i);
    while (i > 0)
    {
      localArrayList.add(readStrongBinder());
      i -= 1;
    }
    return localArrayList;
  }
  
  public final boolean[] createBooleanArray()
  {
    int j = readInt();
    if ((j >= 0) && (j <= dataAvail() >> 2))
    {
      boolean[] arrayOfBoolean = new boolean[j];
      int i = 0;
      if (i < j)
      {
        if (readInt() != 0) {}
        for (int k = 1;; k = 0)
        {
          arrayOfBoolean[i] = k;
          i += 1;
          break;
        }
      }
      return arrayOfBoolean;
    }
    return null;
  }
  
  public final byte[] createByteArray()
  {
    return nativeCreateByteArray(this.mNativePtr);
  }
  
  public final char[] createCharArray()
  {
    int j = readInt();
    if ((j >= 0) && (j <= dataAvail() >> 2))
    {
      char[] arrayOfChar = new char[j];
      int i = 0;
      while (i < j)
      {
        arrayOfChar[i] = ((char)readInt());
        i += 1;
      }
      return arrayOfChar;
    }
    return null;
  }
  
  public final double[] createDoubleArray()
  {
    int j = readInt();
    if ((j >= 0) && (j <= dataAvail() >> 3))
    {
      double[] arrayOfDouble = new double[j];
      int i = 0;
      while (i < j)
      {
        arrayOfDouble[i] = readDouble();
        i += 1;
      }
      return arrayOfDouble;
    }
    return null;
  }
  
  public final float[] createFloatArray()
  {
    int j = readInt();
    if ((j >= 0) && (j <= dataAvail() >> 2))
    {
      float[] arrayOfFloat = new float[j];
      int i = 0;
      while (i < j)
      {
        arrayOfFloat[i] = readFloat();
        i += 1;
      }
      return arrayOfFloat;
    }
    return null;
  }
  
  public final int[] createIntArray()
  {
    int j = readInt();
    if ((j >= 0) && (j <= dataAvail() >> 2))
    {
      int[] arrayOfInt = new int[j];
      int i = 0;
      while (i < j)
      {
        arrayOfInt[i] = readInt();
        i += 1;
      }
      return arrayOfInt;
    }
    return null;
  }
  
  public final long[] createLongArray()
  {
    int j = readInt();
    if ((j >= 0) && (j <= dataAvail() >> 3))
    {
      long[] arrayOfLong = new long[j];
      int i = 0;
      while (i < j)
      {
        arrayOfLong[i] = readLong();
        i += 1;
      }
      return arrayOfLong;
    }
    return null;
  }
  
  public final FileDescriptor[] createRawFileDescriptorArray()
  {
    int j = readInt();
    if (j < 0) {
      return null;
    }
    FileDescriptor[] arrayOfFileDescriptor = new FileDescriptor[j];
    int i = 0;
    while (i < j)
    {
      arrayOfFileDescriptor[i] = readRawFileDescriptor();
      i += 1;
    }
    return arrayOfFileDescriptor;
  }
  
  public final String[] createStringArray()
  {
    int j = readInt();
    if (j >= 0)
    {
      String[] arrayOfString = new String[j];
      int i = 0;
      while (i < j)
      {
        arrayOfString[i] = readString();
        i += 1;
      }
      return arrayOfString;
    }
    return null;
  }
  
  public final ArrayList<String> createStringArrayList()
  {
    int i = readInt();
    if (i < 0) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(i);
    while (i > 0)
    {
      localArrayList.add(readString());
      i -= 1;
    }
    return localArrayList;
  }
  
  public final <T> T[] createTypedArray(Parcelable.Creator<T> paramCreator)
  {
    int j = readInt();
    if (j < 0) {
      return null;
    }
    Object[] arrayOfObject = paramCreator.newArray(j);
    int i = 0;
    while (i < j)
    {
      if (readInt() != 0) {
        arrayOfObject[i] = paramCreator.createFromParcel(this);
      }
      i += 1;
    }
    return arrayOfObject;
  }
  
  public final <T> ArrayList<T> createTypedArrayList(Parcelable.Creator<T> paramCreator)
  {
    int i = readInt();
    if (i < 0) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(i);
    if (i > 0)
    {
      if (readInt() != 0) {
        localArrayList.add(paramCreator.createFromParcel(this));
      }
      for (;;)
      {
        i -= 1;
        break;
        localArrayList.add(null);
      }
    }
    return localArrayList;
  }
  
  public final int dataAvail()
  {
    return nativeDataAvail(this.mNativePtr);
  }
  
  public final int dataCapacity()
  {
    return nativeDataCapacity(this.mNativePtr);
  }
  
  public final int dataPosition()
  {
    return nativeDataPosition(this.mNativePtr);
  }
  
  public final int dataSize()
  {
    return nativeDataSize(this.mNativePtr);
  }
  
  public final void enforceInterface(String paramString)
  {
    nativeEnforceInterface(this.mNativePtr, paramString);
  }
  
  protected void finalize()
    throws Throwable
  {
    destroy();
  }
  
  public long getBlobAshmemSize()
  {
    return nativeGetBlobAshmemSize(this.mNativePtr);
  }
  
  public final boolean hasFileDescriptors()
  {
    return nativeHasFileDescriptors(this.mNativePtr);
  }
  
  public final byte[] marshall()
  {
    return nativeMarshall(this.mNativePtr);
  }
  
  public final boolean pushAllowFds(boolean paramBoolean)
  {
    return nativePushAllowFds(this.mNativePtr, paramBoolean);
  }
  
  public final Object[] readArray(ClassLoader paramClassLoader)
  {
    int i = readInt();
    if (i < 0) {
      return null;
    }
    Object[] arrayOfObject = new Object[i];
    readArrayInternal(arrayOfObject, i, paramClassLoader);
    return arrayOfObject;
  }
  
  public final ArrayList readArrayList(ClassLoader paramClassLoader)
  {
    int i = readInt();
    if (i < 0) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(i);
    readListInternal(localArrayList, i, paramClassLoader);
    return localArrayList;
  }
  
  public void readArrayMap(ArrayMap paramArrayMap, ClassLoader paramClassLoader)
  {
    int i = readInt();
    if (i < 0) {
      return;
    }
    readArrayMapInternal(paramArrayMap, i, paramClassLoader);
  }
  
  void readArrayMapInternal(ArrayMap paramArrayMap, int paramInt, ClassLoader paramClassLoader)
  {
    while (paramInt > 0)
    {
      paramArrayMap.append(readString(), readValue(paramClassLoader));
      paramInt -= 1;
    }
    paramArrayMap.validate();
  }
  
  void readArrayMapSafelyInternal(ArrayMap paramArrayMap, int paramInt, ClassLoader paramClassLoader)
  {
    while (paramInt > 0)
    {
      paramArrayMap.put(readString(), readValue(paramClassLoader));
      paramInt -= 1;
    }
  }
  
  public ArraySet<? extends Object> readArraySet(ClassLoader paramClassLoader)
  {
    int j = readInt();
    if (j < 0) {
      return null;
    }
    ArraySet localArraySet = new ArraySet(j);
    int i = 0;
    while (i < j)
    {
      localArraySet.append(readValue(paramClassLoader));
      i += 1;
    }
    return localArraySet;
  }
  
  public final void readBinderArray(IBinder[] paramArrayOfIBinder)
  {
    int j = readInt();
    if (j == paramArrayOfIBinder.length)
    {
      int i = 0;
      while (i < j)
      {
        paramArrayOfIBinder[i] = readStrongBinder();
        i += 1;
      }
    }
    throw new RuntimeException("bad array lengths");
  }
  
  public final void readBinderList(List<IBinder> paramList)
  {
    int k = paramList.size();
    int m = readInt();
    int j = 0;
    int i;
    for (;;)
    {
      i = j;
      if (j >= k) {
        break;
      }
      i = j;
      if (j >= m) {
        break;
      }
      paramList.set(j, readStrongBinder());
      j += 1;
    }
    for (;;)
    {
      j = i;
      if (i >= m) {
        break;
      }
      paramList.add(readStrongBinder());
      i += 1;
    }
    while (j < k)
    {
      paramList.remove(m);
      j += 1;
    }
  }
  
  public final byte[] readBlob()
  {
    return nativeReadBlob(this.mNativePtr);
  }
  
  public final void readBooleanArray(boolean[] paramArrayOfBoolean)
  {
    int j = readInt();
    if (j == paramArrayOfBoolean.length)
    {
      int i = 0;
      if (i < j)
      {
        if (readInt() != 0) {}
        for (int k = 1;; k = 0)
        {
          paramArrayOfBoolean[i] = k;
          i += 1;
          break;
        }
      }
    }
    else
    {
      throw new RuntimeException("bad array lengths");
    }
  }
  
  public final Bundle readBundle()
  {
    return readBundle(null);
  }
  
  public final Bundle readBundle(ClassLoader paramClassLoader)
  {
    int i = readInt();
    if (i < 0) {
      return null;
    }
    Bundle localBundle = new Bundle(this, i);
    if (paramClassLoader != null) {
      localBundle.setClassLoader(paramClassLoader);
    }
    return localBundle;
  }
  
  public final byte readByte()
  {
    return (byte)(readInt() & 0xFF);
  }
  
  public final void readByteArray(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = createByteArray();
    if (arrayOfByte.length == paramArrayOfByte.length)
    {
      System.arraycopy(arrayOfByte, 0, paramArrayOfByte, 0, arrayOfByte.length);
      return;
    }
    throw new RuntimeException("bad array lengths");
  }
  
  public final void readCharArray(char[] paramArrayOfChar)
  {
    int j = readInt();
    if (j == paramArrayOfChar.length)
    {
      int i = 0;
      while (i < j)
      {
        paramArrayOfChar[i] = ((char)readInt());
        i += 1;
      }
    }
    throw new RuntimeException("bad array lengths");
  }
  
  public final CharSequence readCharSequence()
  {
    return (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(this);
  }
  
  public final CharSequence[] readCharSequenceArray()
  {
    Object localObject = null;
    int j = readInt();
    if (j >= 0)
    {
      CharSequence[] arrayOfCharSequence = new CharSequence[j];
      int i = 0;
      for (;;)
      {
        localObject = arrayOfCharSequence;
        if (i >= j) {
          break;
        }
        arrayOfCharSequence[i] = readCharSequence();
        i += 1;
      }
    }
    return (CharSequence[])localObject;
  }
  
  public final ArrayList<CharSequence> readCharSequenceList()
  {
    Object localObject = null;
    int j = readInt();
    if (j >= 0)
    {
      ArrayList localArrayList = new ArrayList(j);
      int i = 0;
      for (;;)
      {
        localObject = localArrayList;
        if (i >= j) {
          break;
        }
        localArrayList.add(readCharSequence());
        i += 1;
      }
    }
    return (ArrayList<CharSequence>)localObject;
  }
  
  public final <T extends Parcelable> T readCreator(Parcelable.Creator<?> paramCreator, ClassLoader paramClassLoader)
  {
    if ((paramCreator instanceof Parcelable.ClassLoaderCreator)) {
      return (Parcelable)((Parcelable.ClassLoaderCreator)paramCreator).createFromParcel(this, paramClassLoader);
    }
    return (Parcelable)paramCreator.createFromParcel(this);
  }
  
  public final double readDouble()
  {
    return nativeReadDouble(this.mNativePtr);
  }
  
  public final void readDoubleArray(double[] paramArrayOfDouble)
  {
    int j = readInt();
    if (j == paramArrayOfDouble.length)
    {
      int i = 0;
      while (i < j)
      {
        paramArrayOfDouble[i] = readDouble();
        i += 1;
      }
    }
    throw new RuntimeException("bad array lengths");
  }
  
  public final void readException()
  {
    int i = readExceptionCode();
    if (i != 0) {
      readException(i, readString());
    }
  }
  
  public final void readException(int paramInt, String paramString)
  {
    switch (paramInt)
    {
    default: 
      throw new RuntimeException("Unknown exception code: " + paramInt + " msg " + paramString);
    case -1: 
      throw new SecurityException(paramString);
    case -2: 
      throw new BadParcelableException(paramString);
    case -3: 
      throw new IllegalArgumentException(paramString);
    case -4: 
      throw new NullPointerException(paramString);
    case -5: 
      throw new IllegalStateException(paramString);
    case -6: 
      throw new NetworkOnMainThreadException();
    case -7: 
      throw new UnsupportedOperationException(paramString);
    }
    throw new ServiceSpecificException(readInt(), paramString);
  }
  
  public final int readExceptionCode()
  {
    int i = readInt();
    if (i == -128)
    {
      if (readInt() == 0)
      {
        Log.e("Parcel", "Unexpected zero-sized Parcel reply header.");
        return 0;
      }
      StrictMode.readAndHandleBinderCallViolations(this);
      return 0;
    }
    return i;
  }
  
  public final ParcelFileDescriptor readFileDescriptor()
  {
    ParcelFileDescriptor localParcelFileDescriptor = null;
    FileDescriptor localFileDescriptor = nativeReadFileDescriptor(this.mNativePtr);
    if (localFileDescriptor != null) {
      localParcelFileDescriptor = new ParcelFileDescriptor(localFileDescriptor);
    }
    return localParcelFileDescriptor;
  }
  
  public final float readFloat()
  {
    return nativeReadFloat(this.mNativePtr);
  }
  
  public final void readFloatArray(float[] paramArrayOfFloat)
  {
    int j = readInt();
    if (j == paramArrayOfFloat.length)
    {
      int i = 0;
      while (i < j)
      {
        paramArrayOfFloat[i] = readFloat();
        i += 1;
      }
    }
    throw new RuntimeException("bad array lengths");
  }
  
  public final HashMap readHashMap(ClassLoader paramClassLoader)
  {
    int i = readInt();
    if (i < 0) {
      return null;
    }
    HashMap localHashMap = new HashMap(i);
    readMapInternal(localHashMap, i, paramClassLoader);
    return localHashMap;
  }
  
  public final int readInt()
  {
    return nativeReadInt(this.mNativePtr);
  }
  
  public final void readIntArray(int[] paramArrayOfInt)
  {
    int j = readInt();
    if (j == paramArrayOfInt.length)
    {
      int i = 0;
      while (i < j)
      {
        paramArrayOfInt[i] = readInt();
        i += 1;
      }
    }
    throw new RuntimeException("bad array lengths");
  }
  
  public final void readList(List paramList, ClassLoader paramClassLoader)
  {
    readListInternal(paramList, readInt(), paramClassLoader);
  }
  
  public final long readLong()
  {
    return nativeReadLong(this.mNativePtr);
  }
  
  public final void readLongArray(long[] paramArrayOfLong)
  {
    int j = readInt();
    if (j == paramArrayOfLong.length)
    {
      int i = 0;
      while (i < j)
      {
        paramArrayOfLong[i] = readLong();
        i += 1;
      }
    }
    throw new RuntimeException("bad array lengths");
  }
  
  public final void readMap(Map paramMap, ClassLoader paramClassLoader)
  {
    readMapInternal(paramMap, readInt(), paramClassLoader);
  }
  
  void readMapInternal(Map paramMap, int paramInt, ClassLoader paramClassLoader)
  {
    while (paramInt > 0)
    {
      paramMap.put(readValue(paramClassLoader), readValue(paramClassLoader));
      paramInt -= 1;
    }
  }
  
  public final <T extends Parcelable> T readParcelable(ClassLoader paramClassLoader)
  {
    Parcelable.Creator localCreator = readParcelableCreator(paramClassLoader);
    if (localCreator == null) {
      return null;
    }
    if ((localCreator instanceof Parcelable.ClassLoaderCreator)) {
      return (Parcelable)((Parcelable.ClassLoaderCreator)localCreator).createFromParcel(this, paramClassLoader);
    }
    return (Parcelable)localCreator.createFromParcel(this);
  }
  
  public final Parcelable[] readParcelableArray(ClassLoader paramClassLoader)
  {
    int j = readInt();
    if (j < 0) {
      return null;
    }
    Parcelable[] arrayOfParcelable = new Parcelable[j];
    int i = 0;
    while (i < j)
    {
      arrayOfParcelable[i] = readParcelable(paramClassLoader);
      i += 1;
    }
    return arrayOfParcelable;
  }
  
  public final <T extends Parcelable> T[] readParcelableArray(ClassLoader paramClassLoader, Class<T> paramClass)
  {
    int j = readInt();
    if (j < 0) {
      return null;
    }
    paramClass = (Parcelable[])Array.newInstance(paramClass, j);
    int i = 0;
    while (i < j)
    {
      paramClass[i] = readParcelable(paramClassLoader);
      i += 1;
    }
    return paramClass;
  }
  
  public final Parcelable.Creator<?> readParcelableCreator(ClassLoader paramClassLoader)
  {
    String str = readString();
    if (str == null) {
      return null;
    }
    Object localObject1;
    synchronized (mCreators)
    {
      localObject2 = (HashMap)mCreators.get(paramClassLoader);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new HashMap();
        mCreators.put(paramClassLoader, localObject1);
      }
      Parcelable.Creator localCreator = (Parcelable.Creator)((HashMap)localObject1).get(str);
      localObject2 = localCreator;
      if (localCreator != null) {
        break label409;
      }
      if (paramClassLoader != null) {}
    }
    try
    {
      paramClassLoader = getClass().getClassLoader();
      paramClassLoader = Class.forName(str, false, paramClassLoader);
      if (!Parcelable.class.isAssignableFrom(paramClassLoader)) {
        throw new BadParcelableException("Parcelable protocol requires that the class implements Parcelable");
      }
    }
    catch (IllegalAccessException paramClassLoader)
    {
      for (;;)
      {
        Log.e("Parcel", "Illegal access when unmarshalling: " + str, paramClassLoader);
        throw new BadParcelableException("IllegalAccessException when unmarshalling: " + str);
        paramClassLoader = finally;
        throw paramClassLoader;
      }
      paramClassLoader = paramClassLoader.getField("CREATOR");
      if ((paramClassLoader.getModifiers() & 0x8) == 0) {
        throw new BadParcelableException("Parcelable protocol requires the CREATOR object to be static on class " + str);
      }
    }
    catch (ClassNotFoundException paramClassLoader)
    {
      Log.e("Parcel", "Class not found when unmarshalling: " + str, paramClassLoader);
      throw new BadParcelableException("ClassNotFoundException when unmarshalling: " + str);
      if (!Parcelable.Creator.class.isAssignableFrom(paramClassLoader.getType())) {
        throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called CREATOR on class " + str);
      }
    }
    catch (NoSuchFieldException paramClassLoader)
    {
      throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called CREATOR on class " + str);
    }
    Object localObject2 = (Parcelable.Creator)paramClassLoader.get(null);
    if (localObject2 == null) {
      throw new BadParcelableException("Parcelable protocol requires a non-null Parcelable.Creator object called CREATOR on class " + str);
    }
    ((HashMap)localObject1).put(str, localObject2);
    label409:
    return (Parcelable.Creator<?>)localObject2;
  }
  
  public final PersistableBundle readPersistableBundle()
  {
    return readPersistableBundle(null);
  }
  
  public final PersistableBundle readPersistableBundle(ClassLoader paramClassLoader)
  {
    int i = readInt();
    if (i < 0) {
      return null;
    }
    PersistableBundle localPersistableBundle = new PersistableBundle(this, i);
    if (paramClassLoader != null) {
      localPersistableBundle.setClassLoader(paramClassLoader);
    }
    return localPersistableBundle;
  }
  
  public final FileDescriptor readRawFileDescriptor()
  {
    return nativeReadFileDescriptor(this.mNativePtr);
  }
  
  public final void readRawFileDescriptorArray(FileDescriptor[] paramArrayOfFileDescriptor)
  {
    int j = readInt();
    if (j == paramArrayOfFileDescriptor.length)
    {
      int i = 0;
      while (i < j)
      {
        paramArrayOfFileDescriptor[i] = readRawFileDescriptor();
        i += 1;
      }
    }
    throw new RuntimeException("bad array lengths");
  }
  
  public final Serializable readSerializable()
  {
    return readSerializable(null);
  }
  
  public final Size readSize()
  {
    return new Size(readInt(), readInt());
  }
  
  public final SizeF readSizeF()
  {
    return new SizeF(readFloat(), readFloat());
  }
  
  public final SparseArray readSparseArray(ClassLoader paramClassLoader)
  {
    int i = readInt();
    if (i < 0) {
      return null;
    }
    SparseArray localSparseArray = new SparseArray(i);
    readSparseArrayInternal(localSparseArray, i, paramClassLoader);
    return localSparseArray;
  }
  
  public final SparseBooleanArray readSparseBooleanArray()
  {
    int i = readInt();
    if (i < 0) {
      return null;
    }
    SparseBooleanArray localSparseBooleanArray = new SparseBooleanArray(i);
    readSparseBooleanArrayInternal(localSparseBooleanArray, i);
    return localSparseBooleanArray;
  }
  
  public final String readString()
  {
    return nativeReadString(this.mNativePtr);
  }
  
  public final void readStringArray(String[] paramArrayOfString)
  {
    int j = readInt();
    if (j == paramArrayOfString.length)
    {
      int i = 0;
      while (i < j)
      {
        paramArrayOfString[i] = readString();
        i += 1;
      }
    }
    throw new RuntimeException("bad array lengths");
  }
  
  public final String[] readStringArray()
  {
    Object localObject = null;
    int j = readInt();
    if (j >= 0)
    {
      String[] arrayOfString = new String[j];
      int i = 0;
      for (;;)
      {
        localObject = arrayOfString;
        if (i >= j) {
          break;
        }
        arrayOfString[i] = readString();
        i += 1;
      }
    }
    return (String[])localObject;
  }
  
  public final void readStringList(List<String> paramList)
  {
    int k = paramList.size();
    int m = readInt();
    int j = 0;
    int i;
    for (;;)
    {
      i = j;
      if (j >= k) {
        break;
      }
      i = j;
      if (j >= m) {
        break;
      }
      paramList.set(j, readString());
      j += 1;
    }
    for (;;)
    {
      j = i;
      if (i >= m) {
        break;
      }
      paramList.add(readString());
      i += 1;
    }
    while (j < k)
    {
      paramList.remove(m);
      j += 1;
    }
  }
  
  public final IBinder readStrongBinder()
  {
    return nativeReadStrongBinder(this.mNativePtr);
  }
  
  public final <T> void readTypedArray(T[] paramArrayOfT, Parcelable.Creator<T> paramCreator)
  {
    int j = readInt();
    if (j == paramArrayOfT.length)
    {
      int i = 0;
      if (i < j)
      {
        if (readInt() != 0) {
          paramArrayOfT[i] = paramCreator.createFromParcel(this);
        }
        for (;;)
        {
          i += 1;
          break;
          paramArrayOfT[i] = null;
        }
      }
    }
    else
    {
      throw new RuntimeException("bad array lengths");
    }
  }
  
  @Deprecated
  public final <T> T[] readTypedArray(Parcelable.Creator<T> paramCreator)
  {
    return createTypedArray(paramCreator);
  }
  
  public final <T> void readTypedList(List<T> paramList, Parcelable.Creator<T> paramCreator)
  {
    int k = paramList.size();
    int m = readInt();
    int j = 0;
    int i = j;
    if (j < k)
    {
      i = j;
      if (j < m)
      {
        if (readInt() != 0) {
          paramList.set(j, paramCreator.createFromParcel(this));
        }
        for (;;)
        {
          j += 1;
          break;
          paramList.set(j, null);
        }
      }
    }
    j = i;
    if (i < m)
    {
      if (readInt() != 0) {
        paramList.add(paramCreator.createFromParcel(this));
      }
      for (;;)
      {
        i += 1;
        break;
        paramList.add(null);
      }
    }
    while (j < k)
    {
      paramList.remove(m);
      j += 1;
    }
  }
  
  public final <T> T readTypedObject(Parcelable.Creator<T> paramCreator)
  {
    if (readInt() != 0) {
      return (T)paramCreator.createFromParcel(this);
    }
    return null;
  }
  
  public final Object readValue(ClassLoader paramClassLoader)
  {
    boolean bool = true;
    int i = readInt();
    switch (i)
    {
    default: 
      int j = dataPosition();
      throw new RuntimeException("Parcel " + this + ": Unmarshalling unknown type code " + i + " at offset " + (j - 4));
    case -1: 
      return null;
    case 0: 
      return readString();
    case 1: 
      return Integer.valueOf(readInt());
    case 2: 
      return readHashMap(paramClassLoader);
    case 4: 
      return readParcelable(paramClassLoader);
    case 5: 
      return Short.valueOf((short)readInt());
    case 6: 
      return Long.valueOf(readLong());
    case 7: 
      return Float.valueOf(readFloat());
    case 8: 
      return Double.valueOf(readDouble());
    case 9: 
      if (readInt() == 1) {}
      for (;;)
      {
        return Boolean.valueOf(bool);
        bool = false;
      }
    case 10: 
      return readCharSequence();
    case 11: 
      return readArrayList(paramClassLoader);
    case 23: 
      return createBooleanArray();
    case 13: 
      return createByteArray();
    case 14: 
      return readStringArray();
    case 24: 
      return readCharSequenceArray();
    case 15: 
      return readStrongBinder();
    case 17: 
      return readArray(paramClassLoader);
    case 18: 
      return createIntArray();
    case 19: 
      return createLongArray();
    case 20: 
      return Byte.valueOf(readByte());
    case 21: 
      return readSerializable(paramClassLoader);
    case 16: 
      return readParcelableArray(paramClassLoader);
    case 12: 
      return readSparseArray(paramClassLoader);
    case 22: 
      return readSparseBooleanArray();
    case 3: 
      return readBundle(paramClassLoader);
    case 25: 
      return readPersistableBundle(paramClassLoader);
    case 26: 
      return readSize();
    case 27: 
      return readSizeF();
    }
    return createDoubleArray();
  }
  
  public final void recycle()
  {
    freeBuffer();
    Parcel[] arrayOfParcel;
    int i;
    if (this.mOwnsNativeParcelObject)
    {
      arrayOfParcel = sOwnedPool;
      i = 0;
    }
    for (;;)
    {
      if (i >= 6) {
        break label57;
      }
      if (arrayOfParcel[i] == null)
      {
        arrayOfParcel[i] = this;
        return;
        this.mNativePtr = 0L;
        arrayOfParcel = sHolderPool;
        break;
      }
      i += 1;
    }
    label57:
  }
  
  public final void restoreAllowFds(boolean paramBoolean)
  {
    nativeRestoreAllowFds(this.mNativePtr, paramBoolean);
  }
  
  public final void setDataCapacity(int paramInt)
  {
    nativeSetDataCapacity(this.mNativePtr, paramInt);
  }
  
  public final void setDataPosition(int paramInt)
  {
    nativeSetDataPosition(this.mNativePtr, paramInt);
  }
  
  public final void setDataSize(int paramInt)
  {
    updateNativeSize(nativeSetDataSize(this.mNativePtr, paramInt));
  }
  
  public final void unmarshall(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    updateNativeSize(nativeUnmarshall(this.mNativePtr, paramArrayOfByte, paramInt1, paramInt2));
  }
  
  public final void writeArray(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject == null)
    {
      writeInt(-1);
      return;
    }
    int j = paramArrayOfObject.length;
    int i = 0;
    writeInt(j);
    while (i < j)
    {
      writeValue(paramArrayOfObject[i]);
      i += 1;
    }
  }
  
  public void writeArrayMap(ArrayMap<String, Object> paramArrayMap)
  {
    writeArrayMapInternal(paramArrayMap);
  }
  
  void writeArrayMapInternal(ArrayMap<String, Object> paramArrayMap)
  {
    if (paramArrayMap == null)
    {
      writeInt(-1);
      return;
    }
    int j = paramArrayMap.size();
    writeInt(j);
    int i = 0;
    while (i < j)
    {
      writeString((String)paramArrayMap.keyAt(i));
      writeValue(paramArrayMap.valueAt(i));
      i += 1;
    }
  }
  
  public void writeArraySet(ArraySet<? extends Object> paramArraySet)
  {
    if (paramArraySet != null) {}
    for (int i = paramArraySet.size();; i = -1)
    {
      writeInt(i);
      int j = 0;
      while (j < i)
      {
        writeValue(paramArraySet.valueAt(j));
        j += 1;
      }
    }
  }
  
  public final void writeBinderArray(IBinder[] paramArrayOfIBinder)
  {
    if (paramArrayOfIBinder != null)
    {
      int j = paramArrayOfIBinder.length;
      writeInt(j);
      int i = 0;
      while (i < j)
      {
        writeStrongBinder(paramArrayOfIBinder[i]);
        i += 1;
      }
    }
    writeInt(-1);
  }
  
  public final void writeBinderList(List<IBinder> paramList)
  {
    if (paramList == null)
    {
      writeInt(-1);
      return;
    }
    int j = paramList.size();
    int i = 0;
    writeInt(j);
    while (i < j)
    {
      writeStrongBinder((IBinder)paramList.get(i));
      i += 1;
    }
  }
  
  public final void writeBlob(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null) {}
    for (int i = paramArrayOfByte.length;; i = 0)
    {
      writeBlob(paramArrayOfByte, 0, i);
      return;
    }
  }
  
  public final void writeBlob(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null)
    {
      writeInt(-1);
      return;
    }
    Arrays.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
    nativeWriteBlob(this.mNativePtr, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public final void writeBooleanArray(boolean[] paramArrayOfBoolean)
  {
    if (paramArrayOfBoolean != null)
    {
      int k = paramArrayOfBoolean.length;
      writeInt(k);
      int i = 0;
      if (i < k)
      {
        if (paramArrayOfBoolean[i] != 0) {}
        for (int j = 1;; j = 0)
        {
          writeInt(j);
          i += 1;
          break;
        }
      }
    }
    else
    {
      writeInt(-1);
    }
  }
  
  public final void writeBundle(Bundle paramBundle)
  {
    if (paramBundle == null)
    {
      writeInt(-1);
      return;
    }
    paramBundle.writeToParcel(this, 0);
  }
  
  public final void writeByte(byte paramByte)
  {
    writeInt(paramByte);
  }
  
  public final void writeByteArray(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null) {}
    for (int i = paramArrayOfByte.length;; i = 0)
    {
      writeByteArray(paramArrayOfByte, 0, i);
      return;
    }
  }
  
  public final void writeByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null)
    {
      writeInt(-1);
      return;
    }
    Arrays.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
    nativeWriteByteArray(this.mNativePtr, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public final void writeCharArray(char[] paramArrayOfChar)
  {
    if (paramArrayOfChar != null)
    {
      int j = paramArrayOfChar.length;
      writeInt(j);
      int i = 0;
      while (i < j)
      {
        writeInt(paramArrayOfChar[i]);
        i += 1;
      }
    }
    writeInt(-1);
  }
  
  public final void writeCharSequence(CharSequence paramCharSequence)
  {
    TextUtils.writeToParcel(paramCharSequence, this, 0);
  }
  
  public final void writeCharSequenceArray(CharSequence[] paramArrayOfCharSequence)
  {
    if (paramArrayOfCharSequence != null)
    {
      int j = paramArrayOfCharSequence.length;
      writeInt(j);
      int i = 0;
      while (i < j)
      {
        writeCharSequence(paramArrayOfCharSequence[i]);
        i += 1;
      }
    }
    writeInt(-1);
  }
  
  public final void writeCharSequenceList(ArrayList<CharSequence> paramArrayList)
  {
    if (paramArrayList != null)
    {
      int j = paramArrayList.size();
      writeInt(j);
      int i = 0;
      while (i < j)
      {
        writeCharSequence((CharSequence)paramArrayList.get(i));
        i += 1;
      }
    }
    writeInt(-1);
  }
  
  public final void writeDouble(double paramDouble)
  {
    nativeWriteDouble(this.mNativePtr, paramDouble);
  }
  
  public final void writeDoubleArray(double[] paramArrayOfDouble)
  {
    if (paramArrayOfDouble != null)
    {
      int j = paramArrayOfDouble.length;
      writeInt(j);
      int i = 0;
      while (i < j)
      {
        writeDouble(paramArrayOfDouble[i]);
        i += 1;
      }
    }
    writeInt(-1);
  }
  
  public final void writeException(Exception paramException)
  {
    int i = 0;
    if ((paramException instanceof SecurityException)) {
      i = -1;
    }
    for (;;)
    {
      writeInt(i);
      StrictMode.clearGatheredViolations();
      if (i != 0) {
        break label135;
      }
      if (!(paramException instanceof RuntimeException)) {
        break;
      }
      throw ((RuntimeException)paramException);
      if ((paramException instanceof BadParcelableException)) {
        i = -2;
      } else if ((paramException instanceof IllegalArgumentException)) {
        i = -3;
      } else if ((paramException instanceof NullPointerException)) {
        i = -4;
      } else if ((paramException instanceof IllegalStateException)) {
        i = -5;
      } else if ((paramException instanceof NetworkOnMainThreadException)) {
        i = -6;
      } else if ((paramException instanceof UnsupportedOperationException)) {
        i = -7;
      } else if ((paramException instanceof ServiceSpecificException)) {
        i = -8;
      }
    }
    throw new RuntimeException(paramException);
    label135:
    writeString(paramException.getMessage());
    if ((paramException instanceof ServiceSpecificException)) {
      writeInt(((ServiceSpecificException)paramException).errorCode);
    }
  }
  
  public final void writeFileDescriptor(FileDescriptor paramFileDescriptor)
  {
    updateNativeSize(nativeWriteFileDescriptor(this.mNativePtr, paramFileDescriptor));
  }
  
  public final void writeFloat(float paramFloat)
  {
    nativeWriteFloat(this.mNativePtr, paramFloat);
  }
  
  public final void writeFloatArray(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat != null)
    {
      int j = paramArrayOfFloat.length;
      writeInt(j);
      int i = 0;
      while (i < j)
      {
        writeFloat(paramArrayOfFloat[i]);
        i += 1;
      }
    }
    writeInt(-1);
  }
  
  public final void writeInt(int paramInt)
  {
    nativeWriteInt(this.mNativePtr, paramInt);
  }
  
  public final void writeIntArray(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt != null)
    {
      int j = paramArrayOfInt.length;
      writeInt(j);
      int i = 0;
      while (i < j)
      {
        writeInt(paramArrayOfInt[i]);
        i += 1;
      }
    }
    writeInt(-1);
  }
  
  public final void writeInterfaceToken(String paramString)
  {
    nativeWriteInterfaceToken(this.mNativePtr, paramString);
  }
  
  public final void writeList(List paramList)
  {
    if (paramList == null)
    {
      writeInt(-1);
      return;
    }
    int j = paramList.size();
    int i = 0;
    writeInt(j);
    while (i < j)
    {
      writeValue(paramList.get(i));
      i += 1;
    }
  }
  
  public final void writeLong(long paramLong)
  {
    nativeWriteLong(this.mNativePtr, paramLong);
  }
  
  public final void writeLongArray(long[] paramArrayOfLong)
  {
    if (paramArrayOfLong != null)
    {
      int j = paramArrayOfLong.length;
      writeInt(j);
      int i = 0;
      while (i < j)
      {
        writeLong(paramArrayOfLong[i]);
        i += 1;
      }
    }
    writeInt(-1);
  }
  
  public final void writeMap(Map paramMap)
  {
    writeMapInternal(paramMap);
  }
  
  void writeMapInternal(Map<String, Object> paramMap)
  {
    if (paramMap == null)
    {
      writeInt(-1);
      return;
    }
    paramMap = paramMap.entrySet();
    writeInt(paramMap.size());
    paramMap = paramMap.iterator();
    while (paramMap.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramMap.next();
      writeValue(localEntry.getKey());
      writeValue(localEntry.getValue());
    }
  }
  
  public final void writeNoException()
  {
    if (StrictMode.hasGatheredViolations())
    {
      writeInt(-128);
      int i = dataPosition();
      writeInt(0);
      StrictMode.writeGatheredViolationsToParcel(this);
      int j = dataPosition();
      setDataPosition(i);
      writeInt(j - i);
      setDataPosition(j);
      return;
    }
    writeInt(0);
  }
  
  public final void writeParcelable(Parcelable paramParcelable, int paramInt)
  {
    if (paramParcelable == null)
    {
      writeString(null);
      return;
    }
    writeParcelableCreator(paramParcelable);
    paramParcelable.writeToParcel(this, paramInt);
  }
  
  public final <T extends Parcelable> void writeParcelableArray(T[] paramArrayOfT, int paramInt)
  {
    if (paramArrayOfT != null)
    {
      int j = paramArrayOfT.length;
      writeInt(j);
      int i = 0;
      while (i < j)
      {
        writeParcelable(paramArrayOfT[i], paramInt);
        i += 1;
      }
    }
    writeInt(-1);
  }
  
  public final void writeParcelableCreator(Parcelable paramParcelable)
  {
    writeString(paramParcelable.getClass().getName());
  }
  
  public final void writePersistableBundle(PersistableBundle paramPersistableBundle)
  {
    if (paramPersistableBundle == null)
    {
      writeInt(-1);
      return;
    }
    paramPersistableBundle.writeToParcel(this, 0);
  }
  
  public final void writeRawFileDescriptor(FileDescriptor paramFileDescriptor)
  {
    nativeWriteFileDescriptor(this.mNativePtr, paramFileDescriptor);
  }
  
  public final void writeRawFileDescriptorArray(FileDescriptor[] paramArrayOfFileDescriptor)
  {
    if (paramArrayOfFileDescriptor != null)
    {
      int j = paramArrayOfFileDescriptor.length;
      writeInt(j);
      int i = 0;
      while (i < j)
      {
        writeRawFileDescriptor(paramArrayOfFileDescriptor[i]);
        i += 1;
      }
    }
    writeInt(-1);
  }
  
  public final void writeSerializable(Serializable paramSerializable)
  {
    if (paramSerializable == null)
    {
      writeString(null);
      return;
    }
    String str = paramSerializable.getClass().getName();
    writeString(str);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localByteArrayOutputStream);
      localObjectOutputStream.writeObject(paramSerializable);
      localObjectOutputStream.close();
      writeByteArray(localByteArrayOutputStream.toByteArray());
      return;
    }
    catch (IOException paramSerializable)
    {
      throw new RuntimeException("Parcelable encountered IOException writing serializable object (name = " + str + ")", paramSerializable);
    }
  }
  
  public final void writeSize(Size paramSize)
  {
    writeInt(paramSize.getWidth());
    writeInt(paramSize.getHeight());
  }
  
  public final void writeSizeF(SizeF paramSizeF)
  {
    writeFloat(paramSizeF.getWidth());
    writeFloat(paramSizeF.getHeight());
  }
  
  public final void writeSparseArray(SparseArray<Object> paramSparseArray)
  {
    if (paramSparseArray == null)
    {
      writeInt(-1);
      return;
    }
    int j = paramSparseArray.size();
    writeInt(j);
    int i = 0;
    while (i < j)
    {
      writeInt(paramSparseArray.keyAt(i));
      writeValue(paramSparseArray.valueAt(i));
      i += 1;
    }
  }
  
  public final void writeSparseBooleanArray(SparseBooleanArray paramSparseBooleanArray)
  {
    if (paramSparseBooleanArray == null)
    {
      writeInt(-1);
      return;
    }
    int k = paramSparseBooleanArray.size();
    writeInt(k);
    int i = 0;
    if (i < k)
    {
      writeInt(paramSparseBooleanArray.keyAt(i));
      if (paramSparseBooleanArray.valueAt(i)) {}
      for (int j = 1;; j = 0)
      {
        writeByte((byte)j);
        i += 1;
        break;
      }
    }
  }
  
  public final void writeString(String paramString)
  {
    nativeWriteString(this.mNativePtr, paramString);
  }
  
  public final void writeStringArray(String[] paramArrayOfString)
  {
    if (paramArrayOfString != null)
    {
      int j = paramArrayOfString.length;
      writeInt(j);
      int i = 0;
      while (i < j)
      {
        writeString(paramArrayOfString[i]);
        i += 1;
      }
    }
    writeInt(-1);
  }
  
  public final void writeStringList(List<String> paramList)
  {
    if (paramList == null)
    {
      writeInt(-1);
      return;
    }
    int j = paramList.size();
    int i = 0;
    writeInt(j);
    while (i < j)
    {
      writeString((String)paramList.get(i));
      i += 1;
    }
  }
  
  public final void writeStrongBinder(IBinder paramIBinder)
  {
    nativeWriteStrongBinder(this.mNativePtr, paramIBinder);
  }
  
  public final void writeStrongInterface(IInterface paramIInterface)
  {
    Object localObject = null;
    if (paramIInterface == null) {}
    for (paramIInterface = (IInterface)localObject;; paramIInterface = paramIInterface.asBinder())
    {
      writeStrongBinder(paramIInterface);
      return;
    }
  }
  
  public final <T extends Parcelable> void writeTypedArray(T[] paramArrayOfT, int paramInt)
  {
    if (paramArrayOfT != null)
    {
      int j = paramArrayOfT.length;
      writeInt(j);
      int i = 0;
      if (i < j)
      {
        T ? = paramArrayOfT[i];
        if (? != null)
        {
          writeInt(1);
          ?.writeToParcel(this, paramInt);
        }
        for (;;)
        {
          i += 1;
          break;
          writeInt(0);
        }
      }
    }
    else
    {
      writeInt(-1);
    }
  }
  
  public final <T extends Parcelable> void writeTypedList(List<T> paramList)
  {
    if (paramList == null)
    {
      writeInt(-1);
      return;
    }
    int j = paramList.size();
    int i = 0;
    writeInt(j);
    if (i < j)
    {
      Parcelable localParcelable = (Parcelable)paramList.get(i);
      if (localParcelable != null)
      {
        writeInt(1);
        localParcelable.writeToParcel(this, 0);
      }
      for (;;)
      {
        i += 1;
        break;
        writeInt(0);
      }
    }
  }
  
  public final <T extends Parcelable> void writeTypedObject(T paramT, int paramInt)
  {
    if (paramT != null)
    {
      writeInt(1);
      paramT.writeToParcel(this, paramInt);
      return;
    }
    writeInt(0);
  }
  
  public final void writeValue(Object paramObject)
  {
    int i = 1;
    if (paramObject == null)
    {
      writeInt(-1);
      return;
    }
    if ((paramObject instanceof String))
    {
      writeInt(0);
      writeString((String)paramObject);
      return;
    }
    if ((paramObject instanceof Integer))
    {
      writeInt(1);
      writeInt(((Integer)paramObject).intValue());
      return;
    }
    if ((paramObject instanceof Map))
    {
      writeInt(2);
      writeMap((Map)paramObject);
      return;
    }
    if ((paramObject instanceof Bundle))
    {
      writeInt(3);
      writeBundle((Bundle)paramObject);
      return;
    }
    if ((paramObject instanceof PersistableBundle))
    {
      writeInt(25);
      writePersistableBundle((PersistableBundle)paramObject);
      return;
    }
    if ((paramObject instanceof Parcelable))
    {
      writeInt(4);
      writeParcelable((Parcelable)paramObject, 0);
      return;
    }
    if ((paramObject instanceof Short))
    {
      writeInt(5);
      writeInt(((Short)paramObject).intValue());
      return;
    }
    if ((paramObject instanceof Long))
    {
      writeInt(6);
      writeLong(((Long)paramObject).longValue());
      return;
    }
    if ((paramObject instanceof Float))
    {
      writeInt(7);
      writeFloat(((Float)paramObject).floatValue());
      return;
    }
    if ((paramObject instanceof Double))
    {
      writeInt(8);
      writeDouble(((Double)paramObject).doubleValue());
      return;
    }
    if ((paramObject instanceof Boolean))
    {
      writeInt(9);
      if (((Boolean)paramObject).booleanValue()) {}
      for (;;)
      {
        writeInt(i);
        return;
        i = 0;
      }
    }
    if ((paramObject instanceof CharSequence))
    {
      writeInt(10);
      writeCharSequence((CharSequence)paramObject);
      return;
    }
    if ((paramObject instanceof List))
    {
      writeInt(11);
      writeList((List)paramObject);
      return;
    }
    if ((paramObject instanceof SparseArray))
    {
      writeInt(12);
      writeSparseArray((SparseArray)paramObject);
      return;
    }
    if ((paramObject instanceof boolean[]))
    {
      writeInt(23);
      writeBooleanArray((boolean[])paramObject);
      return;
    }
    if ((paramObject instanceof byte[]))
    {
      writeInt(13);
      writeByteArray((byte[])paramObject);
      return;
    }
    if ((paramObject instanceof String[]))
    {
      writeInt(14);
      writeStringArray((String[])paramObject);
      return;
    }
    if ((paramObject instanceof CharSequence[]))
    {
      writeInt(24);
      writeCharSequenceArray((CharSequence[])paramObject);
      return;
    }
    if ((paramObject instanceof IBinder))
    {
      writeInt(15);
      writeStrongBinder((IBinder)paramObject);
      return;
    }
    if ((paramObject instanceof Parcelable[]))
    {
      writeInt(16);
      writeParcelableArray((Parcelable[])paramObject, 0);
      return;
    }
    if ((paramObject instanceof int[]))
    {
      writeInt(18);
      writeIntArray((int[])paramObject);
      return;
    }
    if ((paramObject instanceof long[]))
    {
      writeInt(19);
      writeLongArray((long[])paramObject);
      return;
    }
    if ((paramObject instanceof Byte))
    {
      writeInt(20);
      writeInt(((Byte)paramObject).byteValue());
      return;
    }
    if ((paramObject instanceof Size))
    {
      writeInt(26);
      writeSize((Size)paramObject);
      return;
    }
    if ((paramObject instanceof SizeF))
    {
      writeInt(27);
      writeSizeF((SizeF)paramObject);
      return;
    }
    if ((paramObject instanceof double[]))
    {
      writeInt(28);
      writeDoubleArray((double[])paramObject);
      return;
    }
    Class localClass = paramObject.getClass();
    if ((localClass.isArray()) && (localClass.getComponentType() == Object.class))
    {
      writeInt(17);
      writeArray((Object[])paramObject);
      return;
    }
    if ((paramObject instanceof Serializable))
    {
      writeInt(21);
      writeSerializable((Serializable)paramObject);
      return;
    }
    throw new RuntimeException("Parcel: unable to marshal value " + paramObject);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Parcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */