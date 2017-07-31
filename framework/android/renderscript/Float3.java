package android.renderscript;

public class Float3
{
  public float x;
  public float y;
  public float z;
  
  public Float3() {}
  
  public Float3(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.z = paramFloat3;
  }
  
  public Float3(Float3 paramFloat3)
  {
    this.x = paramFloat3.x;
    this.y = paramFloat3.y;
    this.z = paramFloat3.z;
  }
  
  public static Float3 add(Float3 paramFloat3, float paramFloat)
  {
    Float3 localFloat3 = new Float3();
    paramFloat3.x += paramFloat;
    paramFloat3.y += paramFloat;
    paramFloat3.z += paramFloat;
    return localFloat3;
  }
  
  public static Float3 add(Float3 paramFloat31, Float3 paramFloat32)
  {
    Float3 localFloat3 = new Float3();
    paramFloat31.x += paramFloat32.x;
    paramFloat31.y += paramFloat32.y;
    paramFloat31.z += paramFloat32.z;
    return localFloat3;
  }
  
  public static Float3 div(Float3 paramFloat3, float paramFloat)
  {
    Float3 localFloat3 = new Float3();
    paramFloat3.x /= paramFloat;
    paramFloat3.y /= paramFloat;
    paramFloat3.z /= paramFloat;
    return localFloat3;
  }
  
  public static Float3 div(Float3 paramFloat31, Float3 paramFloat32)
  {
    Float3 localFloat3 = new Float3();
    paramFloat31.x /= paramFloat32.x;
    paramFloat31.y /= paramFloat32.y;
    paramFloat31.z /= paramFloat32.z;
    return localFloat3;
  }
  
  public static Float dotProduct(Float3 paramFloat31, Float3 paramFloat32)
  {
    return new Float(paramFloat32.x * paramFloat31.x + paramFloat32.y * paramFloat31.y + paramFloat32.z * paramFloat31.z);
  }
  
  public static Float3 mul(Float3 paramFloat3, float paramFloat)
  {
    Float3 localFloat3 = new Float3();
    paramFloat3.x *= paramFloat;
    paramFloat3.y *= paramFloat;
    paramFloat3.z *= paramFloat;
    return localFloat3;
  }
  
  public static Float3 mul(Float3 paramFloat31, Float3 paramFloat32)
  {
    Float3 localFloat3 = new Float3();
    paramFloat31.x *= paramFloat32.x;
    paramFloat31.y *= paramFloat32.y;
    paramFloat31.z *= paramFloat32.z;
    return localFloat3;
  }
  
  public static Float3 sub(Float3 paramFloat3, float paramFloat)
  {
    Float3 localFloat3 = new Float3();
    paramFloat3.x -= paramFloat;
    paramFloat3.y -= paramFloat;
    paramFloat3.z -= paramFloat;
    return localFloat3;
  }
  
  public static Float3 sub(Float3 paramFloat31, Float3 paramFloat32)
  {
    Float3 localFloat3 = new Float3();
    paramFloat31.x -= paramFloat32.x;
    paramFloat31.y -= paramFloat32.y;
    paramFloat31.z -= paramFloat32.z;
    return localFloat3;
  }
  
  public void add(float paramFloat)
  {
    this.x += paramFloat;
    this.y += paramFloat;
    this.z += paramFloat;
  }
  
  public void add(Float3 paramFloat3)
  {
    this.x += paramFloat3.x;
    this.y += paramFloat3.y;
    this.z += paramFloat3.z;
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
    }
    this.z += paramFloat;
  }
  
  public void addMultiple(Float3 paramFloat3, float paramFloat)
  {
    this.x += paramFloat3.x * paramFloat;
    this.y += paramFloat3.y * paramFloat;
    this.z += paramFloat3.z * paramFloat;
  }
  
  public void copyTo(float[] paramArrayOfFloat, int paramInt)
  {
    paramArrayOfFloat[paramInt] = this.x;
    paramArrayOfFloat[(paramInt + 1)] = this.y;
    paramArrayOfFloat[(paramInt + 2)] = this.z;
  }
  
  public void div(float paramFloat)
  {
    this.x /= paramFloat;
    this.y /= paramFloat;
    this.z /= paramFloat;
  }
  
  public void div(Float3 paramFloat3)
  {
    this.x /= paramFloat3.x;
    this.y /= paramFloat3.y;
    this.z /= paramFloat3.z;
  }
  
  public Float dotProduct(Float3 paramFloat3)
  {
    return new Float(this.x * paramFloat3.x + this.y * paramFloat3.y + this.z * paramFloat3.z);
  }
  
  public Float elementSum()
  {
    return new Float(this.x + this.y + this.z);
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
    }
    return this.z;
  }
  
  public int length()
  {
    return 3;
  }
  
  public void mul(float paramFloat)
  {
    this.x *= paramFloat;
    this.y *= paramFloat;
    this.z *= paramFloat;
  }
  
  public void mul(Float3 paramFloat3)
  {
    this.x *= paramFloat3.x;
    this.y *= paramFloat3.y;
    this.z *= paramFloat3.z;
  }
  
  public void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
    this.z = (-this.z);
  }
  
  public void set(Float3 paramFloat3)
  {
    this.x = paramFloat3.x;
    this.y = paramFloat3.y;
    this.z = paramFloat3.z;
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
    }
    this.z = paramFloat;
  }
  
  public void setValues(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.z = paramFloat3;
  }
  
  public void sub(float paramFloat)
  {
    this.x -= paramFloat;
    this.y -= paramFloat;
    this.z -= paramFloat;
  }
  
  public void sub(Float3 paramFloat3)
  {
    this.x -= paramFloat3.x;
    this.y -= paramFloat3.y;
    this.z -= paramFloat3.z;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Float3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */