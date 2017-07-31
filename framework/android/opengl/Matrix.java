package android.opengl;

public class Matrix
{
  private static final float[] sTemp = new float[32];
  
  public static void frustumM(float[] paramArrayOfFloat, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    if (paramFloat1 == paramFloat2) {
      throw new IllegalArgumentException("left == right");
    }
    if (paramFloat4 == paramFloat3) {
      throw new IllegalArgumentException("top == bottom");
    }
    if (paramFloat5 == paramFloat6) {
      throw new IllegalArgumentException("near == far");
    }
    if (paramFloat5 <= 0.0F) {
      throw new IllegalArgumentException("near <= 0.0f");
    }
    if (paramFloat6 <= 0.0F) {
      throw new IllegalArgumentException("far <= 0.0f");
    }
    float f1 = 1.0F / (paramFloat2 - paramFloat1);
    float f2 = 1.0F / (paramFloat4 - paramFloat3);
    float f3 = 1.0F / (paramFloat5 - paramFloat6);
    paramArrayOfFloat[(paramInt + 0)] = (2.0F * (paramFloat5 * f1));
    paramArrayOfFloat[(paramInt + 5)] = (2.0F * (paramFloat5 * f2));
    paramArrayOfFloat[(paramInt + 8)] = ((paramFloat2 + paramFloat1) * f1);
    paramArrayOfFloat[(paramInt + 9)] = ((paramFloat4 + paramFloat3) * f2);
    paramArrayOfFloat[(paramInt + 10)] = ((paramFloat6 + paramFloat5) * f3);
    paramArrayOfFloat[(paramInt + 14)] = (2.0F * (paramFloat6 * paramFloat5 * f3));
    paramArrayOfFloat[(paramInt + 11)] = -1.0F;
    paramArrayOfFloat[(paramInt + 1)] = 0.0F;
    paramArrayOfFloat[(paramInt + 2)] = 0.0F;
    paramArrayOfFloat[(paramInt + 3)] = 0.0F;
    paramArrayOfFloat[(paramInt + 4)] = 0.0F;
    paramArrayOfFloat[(paramInt + 6)] = 0.0F;
    paramArrayOfFloat[(paramInt + 7)] = 0.0F;
    paramArrayOfFloat[(paramInt + 12)] = 0.0F;
    paramArrayOfFloat[(paramInt + 13)] = 0.0F;
    paramArrayOfFloat[(paramInt + 15)] = 0.0F;
  }
  
  public static boolean invertM(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2)
  {
    float f1 = paramArrayOfFloat2[(paramInt2 + 0)];
    float f39 = paramArrayOfFloat2[(paramInt2 + 1)];
    float f2 = paramArrayOfFloat2[(paramInt2 + 2)];
    float f3 = paramArrayOfFloat2[(paramInt2 + 3)];
    float f4 = paramArrayOfFloat2[(paramInt2 + 4)];
    float f40 = paramArrayOfFloat2[(paramInt2 + 5)];
    float f5 = paramArrayOfFloat2[(paramInt2 + 6)];
    float f6 = paramArrayOfFloat2[(paramInt2 + 7)];
    float f7 = paramArrayOfFloat2[(paramInt2 + 8)];
    float f37 = paramArrayOfFloat2[(paramInt2 + 9)];
    float f8 = paramArrayOfFloat2[(paramInt2 + 10)];
    float f9 = paramArrayOfFloat2[(paramInt2 + 11)];
    float f10 = paramArrayOfFloat2[(paramInt2 + 12)];
    float f35 = paramArrayOfFloat2[(paramInt2 + 13)];
    float f11 = paramArrayOfFloat2[(paramInt2 + 14)];
    float f12 = paramArrayOfFloat2[(paramInt2 + 15)];
    float f13 = f8 * f12;
    float f14 = f11 * f9;
    float f15 = f5 * f12;
    float f16 = f11 * f6;
    float f17 = f5 * f9;
    float f18 = f8 * f6;
    float f19 = f2 * f12;
    float f20 = f11 * f3;
    float f21 = f2 * f9;
    float f22 = f8 * f3;
    float f23 = f2 * f6;
    float f24 = f5 * f3;
    float f25 = f13 * f40 + f16 * f37 + f17 * f35 - (f14 * f40 + f15 * f37 + f18 * f35);
    float f26 = f14 * f39 + f19 * f37 + f22 * f35 - (f13 * f39 + f20 * f37 + f21 * f35);
    float f27 = f15 * f39 + f20 * f40 + f23 * f35 - (f16 * f39 + f19 * f40 + f24 * f35);
    float f28 = f18 * f39 + f21 * f40 + f24 * f37 - (f17 * f39 + f22 * f40 + f23 * f37);
    float f29 = f7 * f35;
    float f30 = f10 * f37;
    float f31 = f4 * f35;
    float f32 = f10 * f40;
    float f33 = f4 * f37;
    float f34 = f7 * f40;
    f35 = f1 * f35;
    float f36 = f10 * f39;
    f37 = f1 * f37;
    float f38 = f7 * f39;
    f40 = f1 * f40;
    f39 = f4 * f39;
    float f41 = f1 * f25 + f4 * f26 + f7 * f27 + f10 * f28;
    if (f41 == 0.0F) {
      return false;
    }
    f41 = 1.0F / f41;
    paramArrayOfFloat1[paramInt1] = (f25 * f41);
    paramArrayOfFloat1[(paramInt1 + 1)] = (f26 * f41);
    paramArrayOfFloat1[(paramInt1 + 2)] = (f27 * f41);
    paramArrayOfFloat1[(paramInt1 + 3)] = (f28 * f41);
    paramArrayOfFloat1[(paramInt1 + 4)] = ((f14 * f4 + f15 * f7 + f18 * f10 - (f13 * f4 + f16 * f7 + f17 * f10)) * f41);
    paramArrayOfFloat1[(paramInt1 + 5)] = ((f13 * f1 + f20 * f7 + f21 * f10 - (f14 * f1 + f19 * f7 + f22 * f10)) * f41);
    paramArrayOfFloat1[(paramInt1 + 6)] = ((f16 * f1 + f19 * f4 + f24 * f10 - (f15 * f1 + f20 * f4 + f23 * f10)) * f41);
    paramArrayOfFloat1[(paramInt1 + 7)] = ((f17 * f1 + f22 * f4 + f23 * f7 - (f18 * f1 + f21 * f4 + f24 * f7)) * f41);
    paramArrayOfFloat1[(paramInt1 + 8)] = ((f29 * f6 + f32 * f9 + f33 * f12 - (f30 * f6 + f31 * f9 + f34 * f12)) * f41);
    paramArrayOfFloat1[(paramInt1 + 9)] = ((f30 * f3 + f35 * f9 + f38 * f12 - (f29 * f3 + f36 * f9 + f37 * f12)) * f41);
    paramArrayOfFloat1[(paramInt1 + 10)] = ((f31 * f3 + f36 * f6 + f40 * f12 - (f32 * f3 + f35 * f6 + f39 * f12)) * f41);
    paramArrayOfFloat1[(paramInt1 + 11)] = ((f34 * f3 + f37 * f6 + f39 * f9 - (f33 * f3 + f38 * f6 + f40 * f9)) * f41);
    paramArrayOfFloat1[(paramInt1 + 12)] = ((f31 * f8 + f34 * f11 + f30 * f5 - (f33 * f11 + f29 * f5 + f32 * f8)) * f41);
    paramArrayOfFloat1[(paramInt1 + 13)] = ((f37 * f11 + f29 * f2 + f36 * f8 - (f35 * f8 + f38 * f11 + f30 * f2)) * f41);
    paramArrayOfFloat1[(paramInt1 + 14)] = ((f35 * f5 + f39 * f11 + f32 * f2 - (f40 * f11 + f31 * f2 + f36 * f5)) * f41);
    paramArrayOfFloat1[(paramInt1 + 15)] = ((f40 * f8 + f33 * f2 + f38 * f5 - (f37 * f5 + f39 * f8 + f34 * f2)) * f41);
    return true;
  }
  
  public static float length(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (float)Math.sqrt(paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2 + paramFloat3 * paramFloat3);
  }
  
  public static native void multiplyMM(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, float[] paramArrayOfFloat3, int paramInt3);
  
  public static native void multiplyMV(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, float[] paramArrayOfFloat3, int paramInt3);
  
  public static void orthoM(float[] paramArrayOfFloat, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    if (paramFloat1 == paramFloat2) {
      throw new IllegalArgumentException("left == right");
    }
    if (paramFloat3 == paramFloat4) {
      throw new IllegalArgumentException("bottom == top");
    }
    if (paramFloat5 == paramFloat6) {
      throw new IllegalArgumentException("near == far");
    }
    float f1 = 1.0F / (paramFloat2 - paramFloat1);
    float f2 = 1.0F / (paramFloat4 - paramFloat3);
    float f3 = 1.0F / (paramFloat6 - paramFloat5);
    paramFloat1 = -(paramFloat2 + paramFloat1);
    paramFloat2 = -(paramFloat4 + paramFloat3);
    paramFloat3 = -(paramFloat6 + paramFloat5);
    paramArrayOfFloat[(paramInt + 0)] = (2.0F * f1);
    paramArrayOfFloat[(paramInt + 5)] = (2.0F * f2);
    paramArrayOfFloat[(paramInt + 10)] = (-2.0F * f3);
    paramArrayOfFloat[(paramInt + 12)] = (paramFloat1 * f1);
    paramArrayOfFloat[(paramInt + 13)] = (paramFloat2 * f2);
    paramArrayOfFloat[(paramInt + 14)] = (paramFloat3 * f3);
    paramArrayOfFloat[(paramInt + 15)] = 1.0F;
    paramArrayOfFloat[(paramInt + 1)] = 0.0F;
    paramArrayOfFloat[(paramInt + 2)] = 0.0F;
    paramArrayOfFloat[(paramInt + 3)] = 0.0F;
    paramArrayOfFloat[(paramInt + 4)] = 0.0F;
    paramArrayOfFloat[(paramInt + 6)] = 0.0F;
    paramArrayOfFloat[(paramInt + 7)] = 0.0F;
    paramArrayOfFloat[(paramInt + 8)] = 0.0F;
    paramArrayOfFloat[(paramInt + 9)] = 0.0F;
    paramArrayOfFloat[(paramInt + 11)] = 0.0F;
  }
  
  public static void perspectiveM(float[] paramArrayOfFloat, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    paramFloat1 = 1.0F / (float)Math.tan(paramFloat1 * 0.008726646259971648D);
    float f = 1.0F / (paramFloat3 - paramFloat4);
    paramArrayOfFloat[(paramInt + 0)] = (paramFloat1 / paramFloat2);
    paramArrayOfFloat[(paramInt + 1)] = 0.0F;
    paramArrayOfFloat[(paramInt + 2)] = 0.0F;
    paramArrayOfFloat[(paramInt + 3)] = 0.0F;
    paramArrayOfFloat[(paramInt + 4)] = 0.0F;
    paramArrayOfFloat[(paramInt + 5)] = paramFloat1;
    paramArrayOfFloat[(paramInt + 6)] = 0.0F;
    paramArrayOfFloat[(paramInt + 7)] = 0.0F;
    paramArrayOfFloat[(paramInt + 8)] = 0.0F;
    paramArrayOfFloat[(paramInt + 9)] = 0.0F;
    paramArrayOfFloat[(paramInt + 10)] = ((paramFloat4 + paramFloat3) * f);
    paramArrayOfFloat[(paramInt + 11)] = -1.0F;
    paramArrayOfFloat[(paramInt + 12)] = 0.0F;
    paramArrayOfFloat[(paramInt + 13)] = 0.0F;
    paramArrayOfFloat[(paramInt + 14)] = (2.0F * paramFloat4 * paramFloat3 * f);
    paramArrayOfFloat[(paramInt + 15)] = 0.0F;
  }
  
  public static void rotateM(float[] paramArrayOfFloat, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    synchronized (sTemp)
    {
      setRotateM(sTemp, 0, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
      multiplyMM(sTemp, 16, paramArrayOfFloat, paramInt, sTemp, 0);
      System.arraycopy(sTemp, 16, paramArrayOfFloat, paramInt, 16);
      return;
    }
  }
  
  public static void rotateM(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    synchronized (sTemp)
    {
      setRotateM(sTemp, 0, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
      multiplyMM(paramArrayOfFloat1, paramInt1, paramArrayOfFloat2, paramInt2, sTemp, 0);
      return;
    }
  }
  
  public static void scaleM(float[] paramArrayOfFloat, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    int i = 0;
    while (i < 4)
    {
      int j = paramInt + i;
      paramArrayOfFloat[j] *= paramFloat1;
      int k = j + 4;
      paramArrayOfFloat[k] *= paramFloat2;
      j += 8;
      paramArrayOfFloat[j] *= paramFloat3;
      i += 1;
    }
  }
  
  public static void scaleM(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    int i = 0;
    while (i < 4)
    {
      int j = paramInt1 + i;
      int k = paramInt2 + i;
      paramArrayOfFloat2[k] *= paramFloat1;
      paramArrayOfFloat2[(k + 4)] *= paramFloat2;
      paramArrayOfFloat2[(k + 8)] *= paramFloat3;
      paramArrayOfFloat1[(j + 12)] = paramArrayOfFloat2[(k + 12)];
      i += 1;
    }
  }
  
  public static void setIdentityM(float[] paramArrayOfFloat, int paramInt)
  {
    int i = 0;
    while (i < 16)
    {
      paramArrayOfFloat[(paramInt + i)] = 0.0F;
      i += 1;
    }
    i = 0;
    while (i < 16)
    {
      paramArrayOfFloat[(paramInt + i)] = 1.0F;
      i += 5;
    }
  }
  
  public static void setLookAtM(float[] paramArrayOfFloat, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float paramFloat9)
  {
    paramFloat4 -= paramFloat1;
    paramFloat5 -= paramFloat2;
    paramFloat6 -= paramFloat3;
    float f = 1.0F / length(paramFloat4, paramFloat5, paramFloat6);
    paramFloat4 *= f;
    paramFloat5 *= f;
    paramFloat6 *= f;
    f = paramFloat5 * paramFloat9 - paramFloat6 * paramFloat8;
    paramFloat9 = paramFloat6 * paramFloat7 - paramFloat4 * paramFloat9;
    paramFloat7 = paramFloat4 * paramFloat8 - paramFloat5 * paramFloat7;
    paramFloat8 = 1.0F / length(f, paramFloat9, paramFloat7);
    f *= paramFloat8;
    paramFloat9 *= paramFloat8;
    paramFloat7 *= paramFloat8;
    paramArrayOfFloat[(paramInt + 0)] = f;
    paramArrayOfFloat[(paramInt + 1)] = (paramFloat9 * paramFloat6 - paramFloat7 * paramFloat5);
    paramArrayOfFloat[(paramInt + 2)] = (-paramFloat4);
    paramArrayOfFloat[(paramInt + 3)] = 0.0F;
    paramArrayOfFloat[(paramInt + 4)] = paramFloat9;
    paramArrayOfFloat[(paramInt + 5)] = (paramFloat7 * paramFloat4 - f * paramFloat6);
    paramArrayOfFloat[(paramInt + 6)] = (-paramFloat5);
    paramArrayOfFloat[(paramInt + 7)] = 0.0F;
    paramArrayOfFloat[(paramInt + 8)] = paramFloat7;
    paramArrayOfFloat[(paramInt + 9)] = (f * paramFloat5 - paramFloat9 * paramFloat4);
    paramArrayOfFloat[(paramInt + 10)] = (-paramFloat6);
    paramArrayOfFloat[(paramInt + 11)] = 0.0F;
    paramArrayOfFloat[(paramInt + 12)] = 0.0F;
    paramArrayOfFloat[(paramInt + 13)] = 0.0F;
    paramArrayOfFloat[(paramInt + 14)] = 0.0F;
    paramArrayOfFloat[(paramInt + 15)] = 1.0F;
    translateM(paramArrayOfFloat, paramInt, -paramFloat1, -paramFloat2, -paramFloat3);
  }
  
  public static void setRotateEulerM(float[] paramArrayOfFloat, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f2 = paramFloat1 * 0.017453292F;
    float f1 = paramFloat2 * 0.017453292F;
    float f3 = paramFloat3 * 0.017453292F;
    paramFloat1 = (float)Math.cos(f2);
    paramFloat2 = (float)Math.sin(f2);
    paramFloat3 = (float)Math.cos(f1);
    f1 = (float)Math.sin(f1);
    f2 = (float)Math.cos(f3);
    f3 = (float)Math.sin(f3);
    float f4 = paramFloat1 * f1;
    float f5 = paramFloat2 * f1;
    paramArrayOfFloat[(paramInt + 0)] = (paramFloat3 * f2);
    paramArrayOfFloat[(paramInt + 1)] = (-paramFloat3 * f3);
    paramArrayOfFloat[(paramInt + 2)] = f1;
    paramArrayOfFloat[(paramInt + 3)] = 0.0F;
    paramArrayOfFloat[(paramInt + 4)] = (f4 * f2 + paramFloat1 * f3);
    paramArrayOfFloat[(paramInt + 5)] = (-f4 * f3 + paramFloat1 * f2);
    paramArrayOfFloat[(paramInt + 6)] = (-paramFloat2 * paramFloat3);
    paramArrayOfFloat[(paramInt + 7)] = 0.0F;
    paramArrayOfFloat[(paramInt + 8)] = (-f5 * f2 + paramFloat2 * f3);
    paramArrayOfFloat[(paramInt + 9)] = (f5 * f3 + paramFloat2 * f2);
    paramArrayOfFloat[(paramInt + 10)] = (paramFloat1 * paramFloat3);
    paramArrayOfFloat[(paramInt + 11)] = 0.0F;
    paramArrayOfFloat[(paramInt + 12)] = 0.0F;
    paramArrayOfFloat[(paramInt + 13)] = 0.0F;
    paramArrayOfFloat[(paramInt + 14)] = 0.0F;
    paramArrayOfFloat[(paramInt + 15)] = 1.0F;
  }
  
  public static void setRotateM(float[] paramArrayOfFloat, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    paramArrayOfFloat[(paramInt + 3)] = 0.0F;
    paramArrayOfFloat[(paramInt + 7)] = 0.0F;
    paramArrayOfFloat[(paramInt + 11)] = 0.0F;
    paramArrayOfFloat[(paramInt + 12)] = 0.0F;
    paramArrayOfFloat[(paramInt + 13)] = 0.0F;
    paramArrayOfFloat[(paramInt + 14)] = 0.0F;
    paramArrayOfFloat[(paramInt + 15)] = 1.0F;
    paramFloat1 *= 0.017453292F;
    float f3 = (float)Math.sin(paramFloat1);
    float f4 = (float)Math.cos(paramFloat1);
    if ((1.0F == paramFloat2) && (0.0F == paramFloat3) && (0.0F == paramFloat4))
    {
      paramArrayOfFloat[(paramInt + 5)] = f4;
      paramArrayOfFloat[(paramInt + 10)] = f4;
      paramArrayOfFloat[(paramInt + 6)] = f3;
      paramArrayOfFloat[(paramInt + 9)] = (-f3);
      paramArrayOfFloat[(paramInt + 1)] = 0.0F;
      paramArrayOfFloat[(paramInt + 2)] = 0.0F;
      paramArrayOfFloat[(paramInt + 4)] = 0.0F;
      paramArrayOfFloat[(paramInt + 8)] = 0.0F;
      paramArrayOfFloat[(paramInt + 0)] = 1.0F;
      return;
    }
    if ((0.0F == paramFloat2) && (1.0F == paramFloat3) && (0.0F == paramFloat4))
    {
      paramArrayOfFloat[(paramInt + 0)] = f4;
      paramArrayOfFloat[(paramInt + 10)] = f4;
      paramArrayOfFloat[(paramInt + 8)] = f3;
      paramArrayOfFloat[(paramInt + 2)] = (-f3);
      paramArrayOfFloat[(paramInt + 1)] = 0.0F;
      paramArrayOfFloat[(paramInt + 4)] = 0.0F;
      paramArrayOfFloat[(paramInt + 6)] = 0.0F;
      paramArrayOfFloat[(paramInt + 9)] = 0.0F;
      paramArrayOfFloat[(paramInt + 5)] = 1.0F;
      return;
    }
    if ((0.0F == paramFloat2) && (0.0F == paramFloat3) && (1.0F == paramFloat4))
    {
      paramArrayOfFloat[(paramInt + 0)] = f4;
      paramArrayOfFloat[(paramInt + 5)] = f4;
      paramArrayOfFloat[(paramInt + 1)] = f3;
      paramArrayOfFloat[(paramInt + 4)] = (-f3);
      paramArrayOfFloat[(paramInt + 2)] = 0.0F;
      paramArrayOfFloat[(paramInt + 6)] = 0.0F;
      paramArrayOfFloat[(paramInt + 8)] = 0.0F;
      paramArrayOfFloat[(paramInt + 9)] = 0.0F;
      paramArrayOfFloat[(paramInt + 10)] = 1.0F;
      return;
    }
    float f5 = length(paramFloat2, paramFloat3, paramFloat4);
    float f2 = paramFloat2;
    float f1 = paramFloat3;
    paramFloat1 = paramFloat4;
    if (1.0F != f5)
    {
      paramFloat1 = 1.0F / f5;
      f2 = paramFloat2 * paramFloat1;
      f1 = paramFloat3 * paramFloat1;
      paramFloat1 = paramFloat4 * paramFloat1;
    }
    paramFloat2 = 1.0F - f4;
    paramFloat3 = f2 * f1;
    paramFloat4 = f1 * paramFloat1;
    f5 = paramFloat1 * f2;
    float f6 = f2 * f3;
    float f7 = f1 * f3;
    f3 = paramFloat1 * f3;
    paramArrayOfFloat[(paramInt + 0)] = (f2 * f2 * paramFloat2 + f4);
    paramArrayOfFloat[(paramInt + 4)] = (paramFloat3 * paramFloat2 - f3);
    paramArrayOfFloat[(paramInt + 8)] = (f5 * paramFloat2 + f7);
    paramArrayOfFloat[(paramInt + 1)] = (paramFloat3 * paramFloat2 + f3);
    paramArrayOfFloat[(paramInt + 5)] = (f1 * f1 * paramFloat2 + f4);
    paramArrayOfFloat[(paramInt + 9)] = (paramFloat4 * paramFloat2 - f6);
    paramArrayOfFloat[(paramInt + 2)] = (f5 * paramFloat2 - f7);
    paramArrayOfFloat[(paramInt + 6)] = (paramFloat4 * paramFloat2 + f6);
    paramArrayOfFloat[(paramInt + 10)] = (paramFloat1 * paramFloat1 * paramFloat2 + f4);
  }
  
  public static void translateM(float[] paramArrayOfFloat, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    int i = 0;
    while (i < 4)
    {
      int j = paramInt + i;
      int k = j + 12;
      paramArrayOfFloat[k] += paramArrayOfFloat[j] * paramFloat1 + paramArrayOfFloat[(j + 4)] * paramFloat2 + paramArrayOfFloat[(j + 8)] * paramFloat3;
      i += 1;
    }
  }
  
  public static void translateM(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    int i = 0;
    while (i < 12)
    {
      paramArrayOfFloat1[(paramInt1 + i)] = paramArrayOfFloat2[(paramInt2 + i)];
      i += 1;
    }
    i = 0;
    while (i < 4)
    {
      int j = paramInt2 + i;
      paramArrayOfFloat1[(paramInt1 + i + 12)] = (paramArrayOfFloat2[j] * paramFloat1 + paramArrayOfFloat2[(j + 4)] * paramFloat2 + paramArrayOfFloat2[(j + 8)] * paramFloat3 + paramArrayOfFloat2[(j + 12)]);
      i += 1;
    }
  }
  
  public static void transposeM(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2)
  {
    int i = 0;
    while (i < 4)
    {
      int j = i * 4 + paramInt2;
      paramArrayOfFloat1[(i + paramInt1)] = paramArrayOfFloat2[j];
      paramArrayOfFloat1[(i + 4 + paramInt1)] = paramArrayOfFloat2[(j + 1)];
      paramArrayOfFloat1[(i + 8 + paramInt1)] = paramArrayOfFloat2[(j + 2)];
      paramArrayOfFloat1[(i + 12 + paramInt1)] = paramArrayOfFloat2[(j + 3)];
      i += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/opengl/Matrix.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */