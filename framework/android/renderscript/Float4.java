package android.renderscript;

public class Float4
{
  public float w;
  public float x;
  public float y;
  public float z;
  
  public Float4() {}
  
  public Float4(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.z = paramFloat3;
    this.w = paramFloat4;
  }
  
  public Float4(Float4 paramFloat4)
  {
    this.x = paramFloat4.x;
    this.y = paramFloat4.y;
    this.z = paramFloat4.z;
    this.w = paramFloat4.w;
  }
  
  public static Float4 add(Float4 paramFloat4, float paramFloat)
  {
    Float4 localFloat4 = new Float4();
    paramFloat4.x += paramFloat;
    paramFloat4.y += paramFloat;
    paramFloat4.z += paramFloat;
    paramFloat4.w += paramFloat;
    return localFloat4;
  }
  
  public static Float4 add(Float4 paramFloat41, Float4 paramFloat42)
  {
    Float4 localFloat4 = new Float4();
    paramFloat41.x += paramFloat42.x;
    paramFloat41.y += paramFloat42.y;
    paramFloat41.z += paramFloat42.z;
    paramFloat41.w += paramFloat42.w;
    return localFloat4;
  }
  
  public static Float4 div(Float4 paramFloat4, float paramFloat)
  {
    Float4 localFloat4 = new Float4();
    paramFloat4.x /= paramFloat;
    paramFloat4.y /= paramFloat;
    paramFloat4.z /= paramFloat;
    paramFloat4.w /= paramFloat;
    return localFloat4;
  }
  
  public static Float4 div(Float4 paramFloat41, Float4 paramFloat42)
  {
    Float4 localFloat4 = new Float4();
    paramFloat41.x /= paramFloat42.x;
    paramFloat41.y /= paramFloat42.y;
    paramFloat41.z /= paramFloat42.z;
    paramFloat41.w /= paramFloat42.w;
    return localFloat4;
  }
  
  public static float dotProduct(Float4 paramFloat41, Float4 paramFloat42)
  {
    return paramFloat42.x * paramFloat41.x + paramFloat42.y * paramFloat41.y + paramFloat42.z * paramFloat41.z + paramFloat42.w * paramFloat41.w;
  }
  
  public static Float4 mul(Float4 paramFloat4, float paramFloat)
  {
    Float4 localFloat4 = new Float4();
    paramFloat4.x *= paramFloat;
    paramFloat4.y *= paramFloat;
    paramFloat4.z *= paramFloat;
    paramFloat4.w *= paramFloat;
    return localFloat4;
  }
  
  public static Float4 mul(Float4 paramFloat41, Float4 paramFloat42)
  {
    Float4 localFloat4 = new Float4();
    paramFloat41.x *= paramFloat42.x;
    paramFloat41.y *= paramFloat42.y;
    paramFloat41.z *= paramFloat42.z;
    paramFloat41.w *= paramFloat42.w;
    return localFloat4;
  }
  
  public static Float4 sub(Float4 paramFloat4, float paramFloat)
  {
    Float4 localFloat4 = new Float4();
    paramFloat4.x -= paramFloat;
    paramFloat4.y -= paramFloat;
    paramFloat4.z -= paramFloat;
    paramFloat4.w -= paramFloat;
    return localFloat4;
  }
  
  public static Float4 sub(Float4 paramFloat41, Float4 paramFloat42)
  {
    Float4 localFloat4 = new Float4();
    paramFloat41.x -= paramFloat42.x;
    paramFloat41.y -= paramFloat42.y;
    paramFloat41.z -= paramFloat42.z;
    paramFloat41.w -= paramFloat42.w;
    return localFloat4;
  }
  
  public void add(float paramFloat)
  {
    this.x += paramFloat;
    this.y += paramFloat;
    this.z += paramFloat;
    this.w += paramFloat;
  }
  
  public void add(Float4 paramFloat4)
  {
    this.x += paramFloat4.x;
    this.y += paramFloat4.y;
    this.z += paramFloat4.z;
    this.w += paramFloat4.w;
  }
  
  public void addAt(int paramInt, float paramFloat)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      this.x += paramFloat;
      return;
    case 1: 
      this.y += paramFloat;
      return;
    case 2: 
      this.z += paramFloat;
      return;
    }
    this.w += paramFloat;
  }
  
  public void addMultiple(Float4 paramFloat4, float paramFloat)
  {
    this.x += paramFloat4.x * paramFloat;
    this.y += paramFloat4.y * paramFloat;
    this.z += paramFloat4.z * paramFloat;
    this.w += paramFloat4.w * paramFloat;
  }
  
  public void copyTo(float[] paramArrayOfFloat, int paramInt)
  {
    paramArrayOfFloat[paramInt] = this.x;
    paramArrayOfFloat[(paramInt + 1)] = this.y;
    paramArrayOfFloat[(paramInt + 2)] = this.z;
    paramArrayOfFloat[(paramInt + 3)] = this.w;
  }
  
  public void div(float paramFloat)
  {
    this.x /= paramFloat;
    this.y /= paramFloat;
    this.z /= paramFloat;
    this.w /= paramFloat;
  }
  
  public void div(Float4 paramFloat4)
  {
    this.x /= paramFloat4.x;
    this.y /= paramFloat4.y;
    this.z /= paramFloat4.z;
    this.w /= paramFloat4.w;
  }
  
  public float dotProduct(Float4 paramFloat4)
  {
    return this.x * paramFloat4.x + this.y * paramFloat4.y + this.z * paramFloat4.z + this.w * paramFloat4.w;
  }
  
  public float elementSum()
  {
    return this.x + this.y + this.z + this.w;
  }
  
  public float get(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      return this.x;
    case 1: 
      return this.y;
    case 2: 
      return this.z;
    }
    return this.w;
  }
  
  public int length()
  {
    return 4;
  }
  
  public void mul(float paramFloat)
  {
    this.x *= paramFloat;
    this.y *= paramFloat;
    this.z *= paramFloat;
    this.w *= paramFloat;
  }
  
  public void mul(Float4 paramFloat4)
  {
    this.x *= paramFloat4.x;
    this.y *= paramFloat4.y;
    this.z *= paramFloat4.z;
    this.w *= paramFloat4.w;
  }
  
  public void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
    this.z = (-this.z);
    this.w = (-this.w);
  }
  
  public void set(Float4 paramFloat4)
  {
    this.x = paramFloat4.x;
    this.y = paramFloat4.y;
    this.z = paramFloat4.z;
    this.w = paramFloat4.w;
  }
  
  public void setAt(int paramInt, float paramFloat)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      this.x = paramFloat;
      return;
    case 1: 
      this.y = paramFloat;
      return;
    case 2: 
      this.z = paramFloat;
      return;
    }
    this.w = paramFloat;
  }
  
  public void setValues(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.z = paramFloat3;
    this.w = paramFloat4;
  }
  
  public void sub(float paramFloat)
  {
    this.x -= paramFloat;
    this.y -= paramFloat;
    this.z -= paramFloat;
    this.w -= paramFloat;
  }
  
  public void sub(Float4 paramFloat4)
  {
    this.x -= paramFloat4.x;
    this.y -= paramFloat4.y;
    this.z -= paramFloat4.z;
    this.w -= paramFloat4.w;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Float4.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */