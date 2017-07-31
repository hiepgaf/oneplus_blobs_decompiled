package android.renderscript;

public class AllocationAdapter
  extends Allocation
{
  Type mWindow;
  
  AllocationAdapter(long paramLong, RenderScript paramRenderScript, Allocation paramAllocation, Type paramType)
  {
    super(paramLong, paramRenderScript, paramAllocation.mType, paramAllocation.mUsage);
    this.mAdaptedAllocation = paramAllocation;
    this.mWindow = paramType;
  }
  
  public static AllocationAdapter create1D(RenderScript paramRenderScript, Allocation paramAllocation)
  {
    paramRenderScript.validate();
    return createTyped(paramRenderScript, paramAllocation, Type.createX(paramRenderScript, paramAllocation.getElement(), paramAllocation.getType().getX()));
  }
  
  public static AllocationAdapter create2D(RenderScript paramRenderScript, Allocation paramAllocation)
  {
    paramRenderScript.validate();
    return createTyped(paramRenderScript, paramAllocation, Type.createXY(paramRenderScript, paramAllocation.getElement(), paramAllocation.getType().getX(), paramAllocation.getType().getY()));
  }
  
  public static AllocationAdapter createTyped(RenderScript paramRenderScript, Allocation paramAllocation, Type paramType)
  {
    paramRenderScript.validate();
    if (paramAllocation.mAdaptedAllocation != null) {
      throw new RSInvalidStateException("Adapters cannot be nested.");
    }
    if (!paramAllocation.getType().getElement().equals(paramType.getElement())) {
      throw new RSInvalidStateException("Element must match Allocation type.");
    }
    if ((paramType.hasFaces()) || (paramType.hasMipmaps())) {
      throw new RSInvalidStateException("Adapters do not support window types with Mipmaps or Faces.");
    }
    Type localType = paramAllocation.getType();
    if ((paramType.getX() > localType.getX()) || (paramType.getY() > localType.getY())) {}
    while ((paramType.getZ() > localType.getZ()) || (paramType.getArrayCount() > localType.getArrayCount())) {
      throw new RSInvalidStateException("Type cannot have dimension larger than the source allocation.");
    }
    if (paramType.getArrayCount() > 0)
    {
      int i = 0;
      while (i < paramType.getArray(i))
      {
        if (paramType.getArray(i) > localType.getArray(i)) {
          throw new RSInvalidStateException("Type cannot have dimension larger than the source allocation.");
        }
        i += 1;
      }
    }
    long l = paramRenderScript.nAllocationAdapterCreate(paramAllocation.getID(paramRenderScript), paramType.getID(paramRenderScript));
    if (l == 0L) {
      throw new RSRuntimeException("AllocationAdapter creation failed.");
    }
    return new AllocationAdapter(l, paramRenderScript, paramAllocation, paramType);
  }
  
  private void updateOffsets()
  {
    int m = 0;
    int i = 0;
    int n = 0;
    int j = 0;
    int i1 = 0;
    int k = 0;
    int i3 = 0;
    int i2 = i3;
    if (this.mSelectedArray != null)
    {
      if (this.mSelectedArray.length > 0) {
        i = this.mSelectedArray[0];
      }
      if (this.mSelectedArray.length > 1) {
        j = this.mSelectedArray[2];
      }
      if (this.mSelectedArray.length > 2) {
        k = this.mSelectedArray[2];
      }
      m = i;
      n = j;
      i1 = k;
      i2 = i3;
      if (this.mSelectedArray.length > 3)
      {
        i2 = this.mSelectedArray[3];
        i1 = k;
        n = j;
        m = i;
      }
    }
    this.mRS.nAllocationAdapterOffset(getID(this.mRS), this.mSelectedX, this.mSelectedY, this.mSelectedZ, this.mSelectedLOD, this.mSelectedFace.mID, m, n, i1, i2);
  }
  
  void initLOD(int paramInt)
  {
    if (paramInt < 0) {
      throw new RSIllegalArgumentException("Attempting to set negative lod (" + paramInt + ").");
    }
    int i1 = this.mAdaptedAllocation.mType.getX();
    int k = this.mAdaptedAllocation.mType.getY();
    int i = this.mAdaptedAllocation.mType.getZ();
    int j = 0;
    while (j < paramInt)
    {
      if ((i1 == 1) && (k == 1) && (i == 1)) {
        throw new RSIllegalArgumentException("Attempting to set lod (" + paramInt + ") out of range.");
      }
      int m = i1;
      if (i1 > 1) {
        m = i1 >> 1;
      }
      int n = k;
      if (k > 1) {
        n = k >> 1;
      }
      int i2 = i;
      if (i > 1) {
        i2 = i >> 1;
      }
      j += 1;
      i1 = m;
      k = n;
      i = i2;
    }
    this.mCurrentDimX = i1;
    this.mCurrentDimY = k;
    this.mCurrentDimZ = i;
    this.mCurrentCount = this.mCurrentDimX;
    if (this.mCurrentDimY > 1) {
      this.mCurrentCount *= this.mCurrentDimY;
    }
    if (this.mCurrentDimZ > 1) {
      this.mCurrentCount *= this.mCurrentDimZ;
    }
    this.mSelectedY = 0;
    this.mSelectedZ = 0;
  }
  
  public void resize(int paramInt)
  {
    try
    {
      throw new RSInvalidStateException("Resize not allowed for Adapters.");
    }
    finally {}
  }
  
  public void setArray(int paramInt1, int paramInt2)
  {
    if (this.mAdaptedAllocation.getType().getArray(paramInt1) == 0) {
      throw new RSInvalidStateException("Cannot set arrayNum when the allocation type does not include arrayNum dim.");
    }
    if (this.mAdaptedAllocation.getType().getArray(paramInt1) <= paramInt2) {
      throw new RSInvalidStateException("Cannot set arrayNum greater than dimension of allocation.");
    }
    if (this.mWindow.getArray(paramInt1) == this.mAdaptedAllocation.getType().getArray(paramInt1)) {
      throw new RSInvalidStateException("Cannot set arrayNum when the adapter includes arrayNum.");
    }
    if (this.mWindow.getArray(paramInt1) + paramInt2 >= this.mAdaptedAllocation.getType().getArray(paramInt1)) {
      throw new RSInvalidStateException("Cannot set (arrayNum + window) which would be larger than dimension of allocation.");
    }
    this.mSelectedArray[paramInt1] = paramInt2;
    updateOffsets();
  }
  
  public void setFace(Type.CubemapFace paramCubemapFace)
  {
    if (!this.mAdaptedAllocation.getType().hasFaces()) {
      throw new RSInvalidStateException("Cannot set Face when the allocation type does not include faces.");
    }
    if (this.mWindow.hasFaces()) {
      throw new RSInvalidStateException("Cannot set face when the adapter includes faces.");
    }
    if (paramCubemapFace == null) {
      throw new RSIllegalArgumentException("Cannot set null face.");
    }
    this.mSelectedFace = paramCubemapFace;
    updateOffsets();
  }
  
  public void setLOD(int paramInt)
  {
    if (!this.mAdaptedAllocation.getType().hasMipmaps()) {
      throw new RSInvalidStateException("Cannot set LOD when the allocation type does not include mipmaps.");
    }
    if (this.mWindow.hasMipmaps()) {
      throw new RSInvalidStateException("Cannot set LOD when the adapter includes mipmaps.");
    }
    initLOD(paramInt);
    this.mSelectedLOD = paramInt;
    updateOffsets();
  }
  
  public void setX(int paramInt)
  {
    if (this.mAdaptedAllocation.getType().getX() <= paramInt) {
      throw new RSInvalidStateException("Cannot set X greater than dimension of allocation.");
    }
    if (this.mWindow.getX() == this.mAdaptedAllocation.getType().getX()) {
      throw new RSInvalidStateException("Cannot set X when the adapter includes X.");
    }
    if (this.mWindow.getX() + paramInt >= this.mAdaptedAllocation.getType().getX()) {
      throw new RSInvalidStateException("Cannot set (X + window) which would be larger than dimension of allocation.");
    }
    this.mSelectedX = paramInt;
    updateOffsets();
  }
  
  public void setY(int paramInt)
  {
    if (this.mAdaptedAllocation.getType().getY() == 0) {
      throw new RSInvalidStateException("Cannot set Y when the allocation type does not include Y dim.");
    }
    if (this.mAdaptedAllocation.getType().getY() <= paramInt) {
      throw new RSInvalidStateException("Cannot set Y greater than dimension of allocation.");
    }
    if (this.mWindow.getY() == this.mAdaptedAllocation.getType().getY()) {
      throw new RSInvalidStateException("Cannot set Y when the adapter includes Y.");
    }
    if (this.mWindow.getY() + paramInt >= this.mAdaptedAllocation.getType().getY()) {
      throw new RSInvalidStateException("Cannot set (Y + window) which would be larger than dimension of allocation.");
    }
    this.mSelectedY = paramInt;
    updateOffsets();
  }
  
  public void setZ(int paramInt)
  {
    if (this.mAdaptedAllocation.getType().getZ() == 0) {
      throw new RSInvalidStateException("Cannot set Z when the allocation type does not include Z dim.");
    }
    if (this.mAdaptedAllocation.getType().getZ() <= paramInt) {
      throw new RSInvalidStateException("Cannot set Z greater than dimension of allocation.");
    }
    if (this.mWindow.getZ() == this.mAdaptedAllocation.getType().getZ()) {
      throw new RSInvalidStateException("Cannot set Z when the adapter includes Z.");
    }
    if (this.mWindow.getZ() + paramInt >= this.mAdaptedAllocation.getType().getZ()) {
      throw new RSInvalidStateException("Cannot set (Z + window) which would be larger than dimension of allocation.");
    }
    this.mSelectedZ = paramInt;
    updateOffsets();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/AllocationAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */