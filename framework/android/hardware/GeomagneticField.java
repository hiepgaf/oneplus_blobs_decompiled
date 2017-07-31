package android.hardware;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class GeomagneticField
{
  private static final long BASE_TIME;
  private static final float[][] DELTA_G;
  private static final float[][] DELTA_H;
  private static final float EARTH_REFERENCE_RADIUS_KM = 6371.2F;
  private static final float EARTH_SEMI_MAJOR_AXIS_KM = 6378.137F;
  private static final float EARTH_SEMI_MINOR_AXIS_KM = 6356.7524F;
  private static final float[][] G_COEFF;
  private static final float[][] H_COEFF;
  private static final float[][] SCHMIDT_QUASI_NORM_FACTORS;
  private float mGcLatitudeRad;
  private float mGcLongitudeRad;
  private float mGcRadiusKm;
  private float mX;
  private float mY;
  private float mZ;
  
  static
  {
    if (GeomagneticField.class.desiredAssertionStatus()) {}
    for (boolean bool = false;; bool = true)
    {
      -assertionsDisabled = bool;
      float[] arrayOfFloat1 = { 0.0F };
      float[] arrayOfFloat2 = { -29496.6F, -1586.3F };
      float[] arrayOfFloat3 = { -2396.6F, 3026.1F, 1668.6F };
      float[] arrayOfFloat4 = { 912.6F, 808.9F, 166.7F, -357.1F, 89.4F };
      float[] arrayOfFloat5 = { -230.9F, 357.2F, 200.3F, -141.1F, -163.0F, -7.8F };
      float[] arrayOfFloat6 = { 72.8F, 68.6F, 76.0F, -141.4F, -22.8F, 13.2F, -77.9F };
      float[] arrayOfFloat7 = { 80.5F, -75.1F, -4.7F, 45.3F, 13.9F, 10.4F, 1.7F, 4.9F };
      float[] arrayOfFloat8 = { 24.4F, 8.1F, -14.5F, -5.6F, -19.3F, 11.5F, 10.9F, -14.1F, -3.7F };
      float[] arrayOfFloat9 = { 5.4F, 9.4F, 3.4F, -5.2F, 3.1F, -12.4F, -0.7F, 8.4F, -8.5F, -10.1F };
      float[] arrayOfFloat10 = { -2.0F, -6.3F, 0.9F, -1.1F, -0.2F, 2.5F, -0.3F, 2.2F, 3.1F, -1.0F, -2.8F };
      float[] arrayOfFloat11 = { -2.2F, -0.2F, 0.3F, 1.0F, -0.6F, 0.9F, -0.1F, 0.5F, -0.4F, -0.4F, 0.2F, -0.8F, 0.0F };
      G_COEFF = new float[][] { arrayOfFloat1, arrayOfFloat2, arrayOfFloat3, { 1340.1F, -2326.2F, 1231.9F, 634.0F }, arrayOfFloat4, arrayOfFloat5, arrayOfFloat6, arrayOfFloat7, arrayOfFloat8, arrayOfFloat9, arrayOfFloat10, { 3.0F, -1.5F, -2.1F, 1.7F, -0.5F, 0.5F, -0.8F, 0.4F, 1.8F, 0.1F, 0.7F, 3.8F }, arrayOfFloat11 };
      arrayOfFloat1 = new float[] { 0.0F, -2707.7F, -576.1F };
      arrayOfFloat2 = new float[] { 0.0F, 286.4F, -211.2F, 164.3F, -309.1F };
      arrayOfFloat3 = new float[] { 0.0F, 44.6F, 188.9F, -118.2F, 0.0F, 100.9F };
      arrayOfFloat4 = new float[] { 0.0F, 2.8F, -0.1F, 4.7F, 4.4F, -7.2F, -1.0F, -3.9F, -2.0F, -2.0F, -8.3F };
      H_COEFF = new float[][] { { 0.0F }, { 0.0F, 4944.4F }, arrayOfFloat1, { 0.0F, -160.2F, 251.9F, -536.6F }, arrayOfFloat2, arrayOfFloat3, { 0.0F, -20.8F, 44.1F, 61.5F, -66.3F, 3.1F, 55.0F }, { 0.0F, -57.9F, -21.1F, 6.5F, 24.9F, 7.0F, -27.7F, -3.3F }, { 0.0F, 11.0F, -20.0F, 11.9F, -17.4F, 16.7F, 7.0F, -10.8F, 1.7F }, { 0.0F, -20.5F, 11.5F, 12.8F, -7.2F, -7.4F, 8.0F, 2.1F, -6.1F, 7.0F }, arrayOfFloat4, { 0.0F, 0.2F, 1.7F, -0.6F, -1.8F, 0.9F, -0.4F, -2.5F, -1.3F, -2.1F, -1.9F, -1.8F }, { 0.0F, -0.9F, 0.3F, 2.1F, -2.5F, 0.5F, 0.6F, 0.0F, 0.1F, 0.3F, -0.9F, -0.2F, 0.9F } };
      arrayOfFloat1 = new float[] { 11.6F, 16.5F };
      arrayOfFloat2 = new float[] { -12.1F, -4.4F, 1.9F };
      arrayOfFloat3 = new float[] { -1.8F, 2.3F, -8.7F, 4.6F, -2.1F };
      arrayOfFloat4 = new float[] { -0.1F, 0.1F, -0.6F, 0.2F, -0.2F, 0.3F, 0.3F, -0.6F, 0.2F };
      DELTA_G = new float[][] { { 0.0F }, arrayOfFloat1, arrayOfFloat2, { 0.4F, -4.1F, -2.9F, -7.7F }, arrayOfFloat3, { -1.0F, 0.6F, -1.8F, -1.0F, 0.9F, 1.0F }, { -0.2F, -0.2F, -0.1F, 2.0F, -1.7F, -0.3F, 1.7F }, { 0.1F, -0.1F, -0.6F, 1.3F, 0.4F, 0.3F, -0.7F, 0.6F }, arrayOfFloat4, { 0.0F, -0.1F, 0.0F, 0.3F, -0.4F, -0.3F, 0.1F, -0.1F, -0.4F, -0.2F }, { 0.0F, 0.0F, -0.1F, 0.2F, 0.0F, -0.1F, -0.2F, 0.0F, -0.1F, -0.2F, -0.2F }, { 0.0F, 0.0F, 0.0F, 0.1F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1F, 0.0F }, { 0.0F, 0.0F, 0.1F, 0.1F, -0.1F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1F, 0.1F } };
      arrayOfFloat1 = new float[] { 0.0F };
      arrayOfFloat2 = new float[] { 0.0F, 0.4F, 1.8F, 1.2F, 4.0F, -0.6F };
      arrayOfFloat3 = new float[] { 0.0F, -0.2F, -2.1F, -0.4F, -0.6F, 0.5F, 0.9F };
      arrayOfFloat4 = new float[] { 0.0F, 0.7F, 0.3F, -0.1F, -0.1F, -0.8F, -0.3F, 0.3F };
      arrayOfFloat5 = new float[] { 0.0F, -0.1F, 0.2F, 0.4F, 0.4F, 0.1F, -0.1F, 0.4F, 0.3F };
      arrayOfFloat6 = new float[] { 0.0F, 0.0F, -0.2F, 0.0F, -0.1F, 0.1F, 0.0F, -0.2F, 0.3F, 0.2F };
      arrayOfFloat7 = new float[] { 0.0F, 0.0F, 0.1F, 0.0F, 0.1F, 0.0F, 0.1F, 0.0F, -0.1F, -0.1F, 0.0F, -0.1F };
      DELTA_H = new float[][] { arrayOfFloat1, { 0.0F, -25.9F }, { 0.0F, -22.5F, -11.8F }, { 0.0F, 7.3F, -3.9F, -2.6F }, { 0.0F, 1.1F, 2.7F, 3.9F, -0.8F }, arrayOfFloat2, arrayOfFloat3, arrayOfFloat4, arrayOfFloat5, arrayOfFloat6, { 0.0F, 0.1F, -0.1F, 0.0F, -0.1F, -0.1F, 0.0F, -0.1F, -0.2F, 0.0F, -0.1F }, arrayOfFloat7, { 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F } };
      BASE_TIME = new GregorianCalendar(2010, 1, 1).getTimeInMillis();
      SCHMIDT_QUASI_NORM_FACTORS = computeSchmidtQuasiNormFactors(G_COEFF.length);
      return;
    }
  }
  
  public GeomagneticField(float paramFloat1, float paramFloat2, float paramFloat3, long paramLong)
  {
    int k = G_COEFF.length;
    float f1 = Math.min(89.99999F, Math.max(-89.99999F, paramFloat1));
    computeGeocentricCoordinates(f1, paramFloat2, paramFloat3);
    if (!-assertionsDisabled)
    {
      if (G_COEFF.length == H_COEFF.length) {}
      for (i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    LegendreTable localLegendreTable = new LegendreTable(k - 1, (float)(1.5707963267948966D - this.mGcLatitudeRad));
    float[] arrayOfFloat1 = new float[k + 2];
    arrayOfFloat1[0] = 1.0F;
    arrayOfFloat1[1] = (6371.2F / this.mGcRadiusKm);
    int i = 2;
    while (i < arrayOfFloat1.length)
    {
      arrayOfFloat1[i] = (arrayOfFloat1[(i - 1)] * arrayOfFloat1[1]);
      i += 1;
    }
    float[] arrayOfFloat2 = new float[k];
    float[] arrayOfFloat3 = new float[k];
    arrayOfFloat2[0] = 0.0F;
    arrayOfFloat3[0] = 1.0F;
    arrayOfFloat2[1] = ((float)Math.sin(this.mGcLongitudeRad));
    arrayOfFloat3[1] = ((float)Math.cos(this.mGcLongitudeRad));
    i = 2;
    int j;
    while (i < k)
    {
      j = i >> 1;
      arrayOfFloat2[i] = (arrayOfFloat2[(i - j)] * arrayOfFloat3[j] + arrayOfFloat3[(i - j)] * arrayOfFloat2[j]);
      arrayOfFloat3[i] = (arrayOfFloat3[(i - j)] * arrayOfFloat3[j] - arrayOfFloat2[(i - j)] * arrayOfFloat2[j]);
      i += 1;
    }
    float f2 = 1.0F / (float)Math.cos(this.mGcLatitudeRad);
    float f3 = (float)(paramLong - BASE_TIME) / 3.1536001E10F;
    paramFloat3 = 0.0F;
    paramFloat2 = 0.0F;
    paramFloat1 = 0.0F;
    i = 1;
    while (i < k)
    {
      j = 0;
      while (j <= i)
      {
        float f4 = G_COEFF[i][j] + DELTA_G[i][j] * f3;
        float f5 = H_COEFF[i][j] + DELTA_H[i][j] * f3;
        paramFloat3 += arrayOfFloat1[(i + 2)] * (arrayOfFloat3[j] * f4 + arrayOfFloat2[j] * f5) * localLegendreTable.mPDeriv[i][j] * SCHMIDT_QUASI_NORM_FACTORS[i][j];
        paramFloat2 += arrayOfFloat1[(i + 2)] * j * (arrayOfFloat2[j] * f4 - arrayOfFloat3[j] * f5) * localLegendreTable.mP[i][j] * SCHMIDT_QUASI_NORM_FACTORS[i][j] * f2;
        paramFloat1 -= (i + 1) * arrayOfFloat1[(i + 2)] * (arrayOfFloat3[j] * f4 + arrayOfFloat2[j] * f5) * localLegendreTable.mP[i][j] * SCHMIDT_QUASI_NORM_FACTORS[i][j];
        j += 1;
      }
      i += 1;
    }
    double d = Math.toRadians(f1) - this.mGcLatitudeRad;
    this.mX = ((float)(paramFloat3 * Math.cos(d) + paramFloat1 * Math.sin(d)));
    this.mY = paramFloat2;
    this.mZ = ((float)(-paramFloat3 * Math.sin(d) + paramFloat1 * Math.cos(d)));
  }
  
  private void computeGeocentricCoordinates(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    paramFloat3 /= 1000.0F;
    double d = Math.toRadians(paramFloat1);
    paramFloat1 = (float)Math.cos(d);
    float f1 = (float)Math.sin(d);
    float f2 = f1 / paramFloat1;
    float f3 = (float)Math.sqrt(4.0680636E7F * paramFloat1 * paramFloat1 + 4.04083E7F * f1 * f1);
    this.mGcLatitudeRad = ((float)Math.atan((f3 * paramFloat3 + 4.04083E7F) * f2 / (f3 * paramFloat3 + 4.0680636E7F)));
    this.mGcLongitudeRad = ((float)Math.toRadians(paramFloat2));
    this.mGcRadiusKm = ((float)Math.sqrt(paramFloat3 * paramFloat3 + 2.0F * paramFloat3 * (float)Math.sqrt(4.0680636E7F * paramFloat1 * paramFloat1 + 4.04083E7F * f1 * f1) + (1.65491412E15F * paramFloat1 * paramFloat1 + 1.63283074E15F * f1 * f1) / (4.0680636E7F * paramFloat1 * paramFloat1 + 4.04083E7F * f1 * f1)));
  }
  
  private static float[][] computeSchmidtQuasiNormFactors(int paramInt)
  {
    float[][] arrayOfFloat = new float[paramInt + 1][];
    arrayOfFloat[0] = { 1.0F };
    int i = 1;
    while (i <= paramInt)
    {
      arrayOfFloat[i] = new float[i + 1];
      arrayOfFloat[i][0] = (arrayOfFloat[(i - 1)][0] * (i * 2 - 1) / i);
      int j = 1;
      if (j <= i)
      {
        float[] arrayOfFloat1 = arrayOfFloat[i];
        float f = arrayOfFloat[i][(j - 1)];
        if (j == 1) {}
        for (int k = 2;; k = 1)
        {
          arrayOfFloat1[j] = ((float)Math.sqrt(k * (i - j + 1) / (i + j)) * f);
          j += 1;
          break;
        }
      }
      i += 1;
    }
    return arrayOfFloat;
  }
  
  public float getDeclination()
  {
    return (float)Math.toDegrees(Math.atan2(this.mY, this.mX));
  }
  
  public float getFieldStrength()
  {
    return (float)Math.sqrt(this.mX * this.mX + this.mY * this.mY + this.mZ * this.mZ);
  }
  
  public float getHorizontalStrength()
  {
    return (float)Math.hypot(this.mX, this.mY);
  }
  
  public float getInclination()
  {
    return (float)Math.toDegrees(Math.atan2(this.mZ, getHorizontalStrength()));
  }
  
  public float getX()
  {
    return this.mX;
  }
  
  public float getY()
  {
    return this.mY;
  }
  
  public float getZ()
  {
    return this.mZ;
  }
  
  private static class LegendreTable
  {
    public final float[][] mP;
    public final float[][] mPDeriv;
    
    static
    {
      if (LegendreTable.class.desiredAssertionStatus()) {}
      for (boolean bool = false;; bool = true)
      {
        -assertionsDisabled = bool;
        return;
      }
    }
    
    public LegendreTable(int paramInt, float paramFloat)
    {
      float f1 = (float)Math.cos(paramFloat);
      paramFloat = (float)Math.sin(paramFloat);
      this.mP = new float[paramInt + 1][];
      this.mPDeriv = new float[paramInt + 1][];
      this.mP[0] = { 1.0F };
      this.mPDeriv[0] = { 0.0F };
      int i = 1;
      while (i <= paramInt)
      {
        this.mP[i] = new float[i + 1];
        this.mPDeriv[i] = new float[i + 1];
        int j = 0;
        if (j <= i)
        {
          if (i == j)
          {
            this.mP[i][j] = (this.mP[(i - 1)][(j - 1)] * paramFloat);
            this.mPDeriv[i][j] = (this.mP[(i - 1)][(j - 1)] * f1 + this.mPDeriv[(i - 1)][(j - 1)] * paramFloat);
          }
          for (;;)
          {
            j += 1;
            break;
            if ((i == 1) || (j == i - 1))
            {
              this.mP[i][j] = (this.mP[(i - 1)][j] * f1);
              this.mPDeriv[i][j] = (-paramFloat * this.mP[(i - 1)][j] + this.mPDeriv[(i - 1)][j] * f1);
            }
            else
            {
              if (!-assertionsDisabled)
              {
                if ((i > 1) && (j < i - 1)) {}
                for (int k = 1; k == 0; k = 0) {
                  throw new AssertionError();
                }
              }
              float f2 = ((i - 1) * (i - 1) - j * j) / ((i * 2 - 1) * (i * 2 - 3));
              this.mP[i][j] = (this.mP[(i - 1)][j] * f1 - this.mP[(i - 2)][j] * f2);
              this.mPDeriv[i][j] = (-paramFloat * this.mP[(i - 1)][j] + this.mPDeriv[(i - 1)][j] * f1 - this.mPDeriv[(i - 2)][j] * f2);
            }
          }
        }
        i += 1;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/GeomagneticField.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */