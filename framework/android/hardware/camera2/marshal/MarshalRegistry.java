package android.hardware.camera2.marshal;

import android.hardware.camera2.utils.TypeReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MarshalRegistry
{
  private static final Object sMarshalLock = new Object();
  private static final HashMap<MarshalToken<?>, Marshaler<?>> sMarshalerMap = new HashMap();
  private static final List<MarshalQueryable<?>> sRegisteredMarshalQueryables = new ArrayList();
  
  private MarshalRegistry()
  {
    throw new AssertionError();
  }
  
  public static <T> Marshaler<T> getMarshaler(TypeReference<T> paramTypeReference, int paramInt)
  {
    MarshalToken localMarshalToken;
    Marshaler localMarshaler;
    synchronized (sMarshalLock)
    {
      localMarshalToken = new MarshalToken(paramTypeReference, paramInt);
      localMarshaler = (Marshaler)sMarshalerMap.get(localMarshalToken);
      localObject1 = localMarshaler;
      if (localMarshaler != null) {
        break label170;
      }
      if (sRegisteredMarshalQueryables.size() == 0) {
        throw new AssertionError("No available query marshalers registered");
      }
    }
    Iterator localIterator = sRegisteredMarshalQueryables.iterator();
    do
    {
      localObject1 = localMarshaler;
      if (!localIterator.hasNext()) {
        break;
      }
      localObject1 = (MarshalQueryable)localIterator.next();
    } while (!((MarshalQueryable)localObject1).isTypeMappingSupported(paramTypeReference, paramInt));
    Object localObject1 = ((MarshalQueryable)localObject1).createMarshaler(paramTypeReference, paramInt);
    if (localObject1 == null) {
      throw new UnsupportedOperationException("Could not find marshaler that matches the requested combination of type reference " + paramTypeReference + " and native type " + MarshalHelpers.toStringNativeType(paramInt));
    }
    sMarshalerMap.put(localMarshalToken, localObject1);
    label170:
    return (Marshaler<T>)localObject1;
  }
  
  public static <T> void registerMarshalQueryable(MarshalQueryable<T> paramMarshalQueryable)
  {
    synchronized (sMarshalLock)
    {
      sRegisteredMarshalQueryables.add(paramMarshalQueryable);
      return;
    }
  }
  
  private static class MarshalToken<T>
  {
    private final int hash;
    final int nativeType;
    final TypeReference<T> typeReference;
    
    public MarshalToken(TypeReference<T> paramTypeReference, int paramInt)
    {
      this.typeReference = paramTypeReference;
      this.nativeType = paramInt;
      this.hash = (paramTypeReference.hashCode() ^ paramInt);
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if ((paramObject instanceof MarshalToken))
      {
        paramObject = (MarshalToken)paramObject;
        boolean bool1 = bool2;
        if (this.typeReference.equals(((MarshalToken)paramObject).typeReference))
        {
          bool1 = bool2;
          if (this.nativeType == ((MarshalToken)paramObject).nativeType) {
            bool1 = true;
          }
        }
        return bool1;
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.hash;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/MarshalRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */