package android.hardware.camera2.utils;

import com.android.internal.util.Preconditions;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public abstract class TypeReference<T>
{
  private final int mHash;
  private final Type mType;
  
  protected TypeReference()
  {
    this.mType = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    if (containsTypeVariable(this.mType)) {
      throw new IllegalArgumentException("Including a type variable in a type reference is not allowed");
    }
    this.mHash = this.mType.hashCode();
  }
  
  private TypeReference(Type paramType)
  {
    this.mType = paramType;
    if (containsTypeVariable(this.mType)) {
      throw new IllegalArgumentException("Including a type variable in a type reference is not allowed");
    }
    this.mHash = this.mType.hashCode();
  }
  
  public static boolean containsTypeVariable(Type paramType)
  {
    boolean bool = true;
    if (paramType == null) {
      return false;
    }
    if ((paramType instanceof TypeVariable)) {
      return true;
    }
    if ((paramType instanceof Class))
    {
      paramType = (Class)paramType;
      if (paramType.getTypeParameters().length != 0) {
        return true;
      }
      return containsTypeVariable(paramType.getDeclaringClass());
    }
    if ((paramType instanceof ParameterizedType))
    {
      paramType = ((ParameterizedType)paramType).getActualTypeArguments();
      int j = paramType.length;
      int i = 0;
      while (i < j)
      {
        if (containsTypeVariable(paramType[i])) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    if ((paramType instanceof WildcardType))
    {
      paramType = (WildcardType)paramType;
      if (!containsTypeVariable(paramType.getLowerBounds())) {
        bool = containsTypeVariable(paramType.getUpperBounds());
      }
      return bool;
    }
    return false;
  }
  
  private static boolean containsTypeVariable(Type[] paramArrayOfType)
  {
    if (paramArrayOfType == null) {
      return false;
    }
    int j = paramArrayOfType.length;
    int i = 0;
    while (i < j)
    {
      if (containsTypeVariable(paramArrayOfType[i])) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static <T> TypeReference<T> createSpecializedTypeReference(Class<T> paramClass)
  {
    return new SpecializedTypeReference(paramClass);
  }
  
  public static TypeReference<?> createSpecializedTypeReference(Type paramType)
  {
    return new SpecializedBaseTypeReference(paramType);
  }
  
  private static final Class<?> getArrayClass(Class<?> paramClass)
  {
    return Array.newInstance(paramClass, 0).getClass();
  }
  
  private static Type getComponentType(Type paramType)
  {
    Preconditions.checkNotNull(paramType, "type must not be null");
    if ((paramType instanceof Class)) {
      return ((Class)paramType).getComponentType();
    }
    if ((paramType instanceof ParameterizedType)) {
      return null;
    }
    if ((paramType instanceof GenericArrayType)) {
      return ((GenericArrayType)paramType).getGenericComponentType();
    }
    if ((paramType instanceof WildcardType)) {
      throw new UnsupportedOperationException("TODO: support wild card components");
    }
    if ((paramType instanceof TypeVariable)) {
      throw new AssertionError("Type variables are not allowed in type references");
    }
    throw new AssertionError("Unhandled branch to get component type for type " + paramType);
  }
  
  private static final Class<?> getRawType(Type paramType)
  {
    if (paramType == null) {
      throw new NullPointerException("type must not be null");
    }
    if ((paramType instanceof Class)) {
      return (Class)paramType;
    }
    if ((paramType instanceof ParameterizedType)) {
      return (Class)((ParameterizedType)paramType).getRawType();
    }
    if ((paramType instanceof GenericArrayType)) {
      return getArrayClass(getRawType(((GenericArrayType)paramType).getGenericComponentType()));
    }
    if ((paramType instanceof WildcardType)) {
      return getRawType(((WildcardType)paramType).getUpperBounds());
    }
    if ((paramType instanceof TypeVariable)) {
      throw new AssertionError("Type variables are not allowed in type references");
    }
    throw new AssertionError("Unhandled branch to get raw type for type " + paramType);
  }
  
  private static final Class<?> getRawType(Type[] paramArrayOfType)
  {
    if (paramArrayOfType == null) {
      return null;
    }
    int i = 0;
    int j = paramArrayOfType.length;
    while (i < j)
    {
      Class localClass = getRawType(paramArrayOfType[i]);
      if (localClass != null) {
        return localClass;
      }
      i += 1;
    }
    return null;
  }
  
  private static void toString(Type paramType, StringBuilder paramStringBuilder)
  {
    if (paramType == null) {
      return;
    }
    if ((paramType instanceof TypeVariable))
    {
      paramStringBuilder.append(((TypeVariable)paramType).getName());
      return;
    }
    if ((paramType instanceof Class))
    {
      paramType = (Class)paramType;
      paramStringBuilder.append(paramType.getName());
      toString(paramType.getTypeParameters(), paramStringBuilder);
      return;
    }
    if ((paramType instanceof ParameterizedType))
    {
      paramType = (ParameterizedType)paramType;
      paramStringBuilder.append(((Class)paramType.getRawType()).getName());
      toString(paramType.getActualTypeArguments(), paramStringBuilder);
      return;
    }
    if ((paramType instanceof GenericArrayType))
    {
      toString(((GenericArrayType)paramType).getGenericComponentType(), paramStringBuilder);
      paramStringBuilder.append("[]");
      return;
    }
    paramStringBuilder.append(paramType.toString());
  }
  
  private static void toString(Type[] paramArrayOfType, StringBuilder paramStringBuilder)
  {
    if (paramArrayOfType == null) {
      return;
    }
    if (paramArrayOfType.length == 0) {
      return;
    }
    paramStringBuilder.append("<");
    int i = 0;
    while (i < paramArrayOfType.length)
    {
      toString(paramArrayOfType[i], paramStringBuilder);
      if (i != paramArrayOfType.length - 1) {
        paramStringBuilder.append(", ");
      }
      i += 1;
    }
    paramStringBuilder.append(">");
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof TypeReference)) {
      return this.mType.equals(((TypeReference)paramObject).mType);
    }
    return false;
  }
  
  public TypeReference<?> getComponentType()
  {
    TypeReference localTypeReference = null;
    Type localType = getComponentType(this.mType);
    if (localType != null) {
      localTypeReference = createSpecializedTypeReference(localType);
    }
    return localTypeReference;
  }
  
  public final Class<? super T> getRawType()
  {
    return getRawType(this.mType);
  }
  
  public Type getType()
  {
    return this.mType;
  }
  
  public int hashCode()
  {
    return this.mHash;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("TypeReference<");
    toString(getType(), localStringBuilder);
    localStringBuilder.append(">");
    return localStringBuilder.toString();
  }
  
  private static class SpecializedBaseTypeReference
    extends TypeReference
  {
    public SpecializedBaseTypeReference(Type paramType)
    {
      super(null);
    }
  }
  
  private static class SpecializedTypeReference<T>
    extends TypeReference<T>
  {
    public SpecializedTypeReference(Class<T> paramClass)
    {
      super(null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/utils/TypeReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */