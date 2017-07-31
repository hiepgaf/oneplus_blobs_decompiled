package android.renderscript;

public class Float2
{
  public float x;
  public float y;
  
  public Float2() {}
  
  public Float2(float paramFloat1, float paramFloat2)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
  }
  
  public Float2(Float2 paramFloat2)
  {
    this.x = paramFloat2.x;
    this.y = paramFloat2.y;
  }
  
  public static Float2 add(Float2 paramFloat2, float paramFloat)
  {
    Float2 localFloat2 = new Float2();
    paramFloat2.x += paramFloat;
    paramFloat2.y += paramFloat;
    return localFloat2;
  }
  
  public static Float2 add(Float2 paramFloat21, Float2 paramFloat22)
  {
    Float2 localFloat2 = new Float2();
    paramFloat21.x += paramFloat22.x;
    paramFloat21.y += paramFloat22.y;
    return localFloat2;
  }
  
  public static Float2 div(Float2 paramFloat2, float paramFloat)
  {
    Float2 localFloat2 = new Float2();
    paramFloat2.x /= paramFloat;
    paramFloat2.y /= paramFloat;
    return localFloat2;
  }
  
  public static Float2 div(Float2 paramFloat21, Float2 paramFloat22)
  {
    Float2 localFloat2 = new Float2();
    paramFloat21.x /= paramFloat22.x;
    paramFloat21.y /= paramFloat22.y;
    return localFloat2;
  }
  
  public static float dotProduct(Float2 paramFloat21, Float2 paramFloat22)
  {
    return paramFloat22.x * paramFloat21.x + paramFloat22.y * paramFloat21.y;
  }
  
  public static Float2 mul(Float2 paramFloat2, float paramFloat)
  {
    Float2 localFloat2 = new Float2();
    paramFloat2.x *= paramFloat;
    paramFloat2.y *= paramFloat;
    return localFloat2;
  }
  
  public static Float2 mul(Float2 paramFloat21, Float2 paramFloat22)
  {
    Float2 localFloat2 = new Float2();
    paramFloat21.x *= paramFloat22.x;
    paramFloat21.y *= paramFloat22.y;
    return localFloat2;
  }
  
  public static Float2 sub(Float2 paramFloat2, float paramFloat)
  {
    Float2 localFloat2 = new Float2();
    paramFloat2.x -= paramFloat;
    paramFloat2.y -= paramFloat;
    return localFloat2;
  }
  
  public static Float2 sub(Float2 paramFloat21, Float2 paramFloat22)
  {
    Float2 localFloat2 = new Float2();
    paramFloat21.x -= paramFloat22.x;
    paramFloat21.y -= paramFloat22.y;
    return localFloat2;
  }
  
  public void add(float paramFloat)
  {
    this.x += paramFloat;
    this.y += paramFloat;
  }
  
  public void add(Float2 paramFloat2)
  {
    this.x += paramFloat2.x;
    this.y += paramFloat2.y;
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
    }
    this.y += paramFloat;
  }
  
  public void addMultiple(Float2 paramFloat2, float paramFloat)
  {
    this.x += paramFloat2.x * paramFloat;
    this.y += paramFloat2.y * paramFloat;
  }
  
  public void copyTo(float[] paramArrayOfFloat, int paramInt)
  {
    paramArrayOfFloat[paramInt] = this.x;
    paramArrayOfFloat[(paramInt + 1)] = this.y;
  }
  
  public void div(float paramFloat)
  {
    this.x /= paramFloat;
    this.y /= paramFloat;
  }
  
  public void div(Float2 paramFloat2)
  {
    this.x /= paramFloat2.x;
    this.y /= paramFloat2.y;
  }
  
  public float dotProduct(Float2 paramFloat2)
  {
    return this.x * paramFloat2.x + this.y * paramFloat2.y;
  }
  
  public float elementSum()
  {
    return this.x + this.y;
  }
  
  public float get(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      return this.x;
    }
    return this.y;
  }
  
  public int length()
  {
    return 2;
  }
  
  public void mul(float paramFloat)
  {
    this.x *= paramFloat;
    this.y *= paramFloat;
  }
  
  public void mul(Float2 paramFloat2)
  {
    this.x *= paramFloat2.x;
    this.y *= paramFloat2.y;
  }
  
  public void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
  }
  
  public void set(Float2 paramFloat2)
  {
    this.x = paramFloat2.x;
    this.y = paramFloat2.y;
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
    }
    this.y = paramFloat;
  }
  
  public void setValues(float paramFloat1, float paramFloat2)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
  }
  
  public void sub(float paramFloat)
  {
    this.x -= paramFloat;
    this.y -= paramFloat;
  }
  
  public void sub(Float2 paramFloat2)
  {
    this.x -= paramFloat2.x;
    this.y -= paramFloat2.y;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Float2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */