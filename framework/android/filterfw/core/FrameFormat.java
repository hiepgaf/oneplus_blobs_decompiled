package android.filterfw.core;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class FrameFormat
{
  public static final int BYTES_PER_SAMPLE_UNSPECIFIED = 1;
  protected static final int SIZE_UNKNOWN = -1;
  public static final int SIZE_UNSPECIFIED = 0;
  public static final int TARGET_GPU = 3;
  public static final int TARGET_NATIVE = 2;
  public static final int TARGET_RS = 5;
  public static final int TARGET_SIMPLE = 1;
  public static final int TARGET_UNSPECIFIED = 0;
  public static final int TARGET_VERTEXBUFFER = 4;
  public static final int TYPE_BIT = 1;
  public static final int TYPE_BYTE = 2;
  public static final int TYPE_DOUBLE = 6;
  public static final int TYPE_FLOAT = 5;
  public static final int TYPE_INT16 = 3;
  public static final int TYPE_INT32 = 4;
  public static final int TYPE_OBJECT = 8;
  public static final int TYPE_POINTER = 7;
  public static final int TYPE_UNSPECIFIED = 0;
  protected int mBaseType = 0;
  protected int mBytesPerSample = 1;
  protected int[] mDimensions;
  protected KeyValueMap mMetaData;
  protected Class mObjectClass;
  protected int mSize = -1;
  protected int mTarget = 0;
  
  protected FrameFormat() {}
  
  public FrameFormat(int paramInt1, int paramInt2)
  {
    this.mBaseType = paramInt1;
    this.mTarget = paramInt2;
    initDefaults();
  }
  
  public static String baseTypeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown";
    case 0: 
      return "unspecified";
    case 1: 
      return "bit";
    case 2: 
      return "byte";
    case 3: 
      return "int";
    case 4: 
      return "int";
    case 5: 
      return "float";
    case 6: 
      return "double";
    case 7: 
      return "pointer";
    }
    return "object";
  }
  
  public static int bytesPerSampleOf(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 1;
    case 1: 
    case 2: 
      return 1;
    case 3: 
      return 2;
    case 4: 
    case 5: 
    case 7: 
      return 4;
    }
    return 8;
  }
  
  public static String dimensionsToString(int[] paramArrayOfInt)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (paramArrayOfInt != null)
    {
      int j = paramArrayOfInt.length;
      int i = 0;
      if (i < j)
      {
        if (paramArrayOfInt[i] == 0) {
          localStringBuffer.append("[]");
        }
        for (;;)
        {
          i += 1;
          break;
          localStringBuffer.append("[" + String.valueOf(paramArrayOfInt[i]) + "]");
        }
      }
    }
    return localStringBuffer.toString();
  }
  
  private void initDefaults()
  {
    this.mBytesPerSample = bytesPerSampleOf(this.mBaseType);
  }
  
  public static String metaDataToString(KeyValueMap paramKeyValueMap)
  {
    if (paramKeyValueMap == null) {
      return "";
    }
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("{ ");
    paramKeyValueMap = paramKeyValueMap.entrySet().iterator();
    while (paramKeyValueMap.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramKeyValueMap.next();
      localStringBuffer.append((String)localEntry.getKey() + ": " + localEntry.getValue() + " ");
    }
    localStringBuffer.append("}");
    return localStringBuffer.toString();
  }
  
  public static int readTargetString(String paramString)
  {
    if ((paramString.equalsIgnoreCase("CPU")) || (paramString.equalsIgnoreCase("NATIVE"))) {
      return 2;
    }
    if (paramString.equalsIgnoreCase("GPU")) {
      return 3;
    }
    if (paramString.equalsIgnoreCase("SIMPLE")) {
      return 1;
    }
    if (paramString.equalsIgnoreCase("VERTEXBUFFER")) {
      return 4;
    }
    if (paramString.equalsIgnoreCase("UNSPECIFIED")) {
      return 0;
    }
    throw new RuntimeException("Unknown target type '" + paramString + "'!");
  }
  
  public static String targetToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown";
    case 0: 
      return "unspecified";
    case 1: 
      return "simple";
    case 2: 
      return "native";
    case 3: 
      return "gpu";
    case 4: 
      return "vbo";
    }
    return "renderscript";
  }
  
  public static FrameFormat unspecified()
  {
    return new FrameFormat(0, 0);
  }
  
  int calcSize(int[] paramArrayOfInt)
  {
    int i = 0;
    if ((paramArrayOfInt != null) && (paramArrayOfInt.length > 0))
    {
      int j = getBytesPerSample();
      int k = paramArrayOfInt.length;
      while (i < k)
      {
        j *= paramArrayOfInt[i];
        i += 1;
      }
      return j;
    }
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof FrameFormat)) {
      return false;
    }
    boolean bool1 = bool2;
    if (((FrameFormat)paramObject).mBaseType == this.mBaseType)
    {
      bool1 = bool2;
      if (((FrameFormat)paramObject).mTarget == this.mTarget)
      {
        bool1 = bool2;
        if (((FrameFormat)paramObject).mBytesPerSample == this.mBytesPerSample)
        {
          bool1 = bool2;
          if (Arrays.equals(((FrameFormat)paramObject).mDimensions, this.mDimensions)) {
            bool1 = ((FrameFormat)paramObject).mMetaData.equals(this.mMetaData);
          }
        }
      }
    }
    return bool1;
  }
  
  public int getBaseType()
  {
    return this.mBaseType;
  }
  
  public int getBytesPerSample()
  {
    return this.mBytesPerSample;
  }
  
  public int getDepth()
  {
    if ((this.mDimensions != null) && (this.mDimensions.length >= 3)) {
      return this.mDimensions[2];
    }
    return -1;
  }
  
  public int getDimension(int paramInt)
  {
    return this.mDimensions[paramInt];
  }
  
  public int getDimensionCount()
  {
    if (this.mDimensions == null) {
      return 0;
    }
    return this.mDimensions.length;
  }
  
  public int[] getDimensions()
  {
    return this.mDimensions;
  }
  
  public int getHeight()
  {
    if ((this.mDimensions != null) && (this.mDimensions.length >= 2)) {
      return this.mDimensions[1];
    }
    return -1;
  }
  
  public int getLength()
  {
    if ((this.mDimensions != null) && (this.mDimensions.length >= 1)) {
      return this.mDimensions[0];
    }
    return -1;
  }
  
  public Object getMetaValue(String paramString)
  {
    Object localObject = null;
    if (this.mMetaData != null) {
      localObject = this.mMetaData.get(paramString);
    }
    return localObject;
  }
  
  public int getNumberOfDimensions()
  {
    if (this.mDimensions != null) {
      return this.mDimensions.length;
    }
    return 0;
  }
  
  public Class getObjectClass()
  {
    return this.mObjectClass;
  }
  
  public int getSize()
  {
    if (this.mSize == -1) {
      this.mSize = calcSize(this.mDimensions);
    }
    return this.mSize;
  }
  
  public int getTarget()
  {
    return this.mTarget;
  }
  
  public int getValuesPerSample()
  {
    return this.mBytesPerSample / bytesPerSampleOf(this.mBaseType);
  }
  
  public int getWidth()
  {
    return getLength();
  }
  
  public boolean hasMetaKey(String paramString)
  {
    if (this.mMetaData != null) {
      return this.mMetaData.containsKey(paramString);
    }
    return false;
  }
  
  public boolean hasMetaKey(String paramString, Class paramClass)
  {
    if ((this.mMetaData != null) && (this.mMetaData.containsKey(paramString)))
    {
      if (!paramClass.isAssignableFrom(this.mMetaData.get(paramString).getClass())) {
        throw new RuntimeException("FrameFormat meta-key '" + paramString + "' is of type " + this.mMetaData.get(paramString).getClass() + " but expected to be of type " + paramClass + "!");
      }
      return true;
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.mBaseType ^ 0x1073 ^ this.mBytesPerSample ^ getSize();
  }
  
  public boolean isBinaryDataType()
  {
    return (this.mBaseType >= 1) && (this.mBaseType <= 6);
  }
  
  public boolean isCompatibleWith(FrameFormat paramFrameFormat)
  {
    if ((paramFrameFormat.getBaseType() != 0) && (getBaseType() != paramFrameFormat.getBaseType())) {
      return false;
    }
    if ((paramFrameFormat.getTarget() != 0) && (getTarget() != paramFrameFormat.getTarget())) {
      return false;
    }
    if ((paramFrameFormat.getBytesPerSample() != 1) && (getBytesPerSample() != paramFrameFormat.getBytesPerSample())) {
      return false;
    }
    if ((paramFrameFormat.getDimensionCount() > 0) && (getDimensionCount() != paramFrameFormat.getDimensionCount())) {
      return false;
    }
    int i = 0;
    while (i < paramFrameFormat.getDimensionCount())
    {
      int j = paramFrameFormat.getDimension(i);
      if ((j != 0) && (getDimension(i) != j)) {
        return false;
      }
      i += 1;
    }
    Iterator localIterator;
    if ((paramFrameFormat.getObjectClass() == null) || ((getObjectClass() != null) && (paramFrameFormat.getObjectClass().isAssignableFrom(getObjectClass()))))
    {
      if (paramFrameFormat.mMetaData != null) {
        localIterator = paramFrameFormat.mMetaData.keySet().iterator();
      }
    }
    else {
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if ((this.mMetaData == null) || (!this.mMetaData.containsKey(str)) || (!this.mMetaData.get(str).equals(paramFrameFormat.mMetaData.get(str))))
        {
          return false;
          return false;
        }
      }
    }
    return true;
  }
  
  boolean isReplaceableBy(FrameFormat paramFrameFormat)
  {
    if ((this.mTarget == paramFrameFormat.mTarget) && (getSize() == paramFrameFormat.getSize())) {
      return Arrays.equals(paramFrameFormat.mDimensions, this.mDimensions);
    }
    return false;
  }
  
  public boolean mayBeCompatibleWith(FrameFormat paramFrameFormat)
  {
    if ((paramFrameFormat.getBaseType() != 0) && (getBaseType() != 0) && (getBaseType() != paramFrameFormat.getBaseType())) {
      return false;
    }
    if ((paramFrameFormat.getTarget() != 0) && (getTarget() != 0) && (getTarget() != paramFrameFormat.getTarget())) {
      return false;
    }
    if ((paramFrameFormat.getBytesPerSample() != 1) && (getBytesPerSample() != 1) && (getBytesPerSample() != paramFrameFormat.getBytesPerSample())) {
      return false;
    }
    if ((paramFrameFormat.getDimensionCount() > 0) && (getDimensionCount() > 0) && (getDimensionCount() != paramFrameFormat.getDimensionCount())) {
      return false;
    }
    int i = 0;
    while (i < paramFrameFormat.getDimensionCount())
    {
      int j = paramFrameFormat.getDimension(i);
      if ((j != 0) && (getDimension(i) != 0) && (getDimension(i) != j)) {
        return false;
      }
      i += 1;
    }
    if ((paramFrameFormat.getObjectClass() != null) && (getObjectClass() != null) && (!paramFrameFormat.getObjectClass().isAssignableFrom(getObjectClass()))) {
      return false;
    }
    if ((paramFrameFormat.mMetaData != null) && (this.mMetaData != null))
    {
      Iterator localIterator = paramFrameFormat.mMetaData.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if ((this.mMetaData.containsKey(str)) && (!this.mMetaData.get(str).equals(paramFrameFormat.mMetaData.get(str)))) {
          return false;
        }
      }
    }
    return true;
  }
  
  public MutableFrameFormat mutableCopy()
  {
    KeyValueMap localKeyValueMap = null;
    MutableFrameFormat localMutableFrameFormat = new MutableFrameFormat();
    localMutableFrameFormat.setBaseType(getBaseType());
    localMutableFrameFormat.setTarget(getTarget());
    localMutableFrameFormat.setBytesPerSample(getBytesPerSample());
    localMutableFrameFormat.setDimensions(getDimensions());
    localMutableFrameFormat.setObjectClass(getObjectClass());
    if (this.mMetaData == null) {}
    for (;;)
    {
      localMutableFrameFormat.mMetaData = localKeyValueMap;
      return localMutableFrameFormat;
      localKeyValueMap = (KeyValueMap)this.mMetaData.clone();
    }
  }
  
  public String toString()
  {
    int i = getValuesPerSample();
    String str1;
    String str2;
    if (i == 1)
    {
      str1 = "";
      if (this.mTarget != 0) {
        break label96;
      }
      str2 = "";
      label23:
      if (this.mObjectClass != null) {
        break label125;
      }
    }
    label96:
    label125:
    for (String str3 = "";; str3 = " class(" + this.mObjectClass.getSimpleName() + ") ")
    {
      return str2 + baseTypeToString(this.mBaseType) + str1 + dimensionsToString(this.mDimensions) + str3 + metaDataToString(this.mMetaData);
      str1 = String.valueOf(i);
      break;
      str2 = targetToString(this.mTarget) + " ";
      break label23;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/FrameFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */