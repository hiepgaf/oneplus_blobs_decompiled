package android.gesture;

import java.util.ArrayList;

class Instance
{
  private static final float[] ORIENTATIONS = { 0.0F, 0.7853982F, 1.5707964F, 2.3561945F, 3.1415927F, 0.0F, -0.7853982F, -1.5707964F, -2.3561945F, -3.1415927F };
  private static final int PATCH_SAMPLE_SIZE = 16;
  private static final int SEQUENCE_SAMPLE_SIZE = 16;
  final long id;
  final String label;
  final float[] vector;
  
  private Instance(long paramLong, float[] paramArrayOfFloat, String paramString)
  {
    this.id = paramLong;
    this.vector = paramArrayOfFloat;
    this.label = paramString;
  }
  
  static Instance createInstance(int paramInt1, int paramInt2, Gesture paramGesture, String paramString)
  {
    if (paramInt1 == 2)
    {
      arrayOfFloat = temporalSampler(paramInt2, paramGesture);
      paramGesture = new Instance(paramGesture.getID(), arrayOfFloat, paramString);
      paramGesture.normalize();
      return paramGesture;
    }
    float[] arrayOfFloat = spatialSampler(paramGesture);
    return new Instance(paramGesture.getID(), arrayOfFloat, paramString);
  }
  
  private void normalize()
  {
    float[] arrayOfFloat = this.vector;
    float f = 0.0F;
    int j = arrayOfFloat.length;
    int i = 0;
    while (i < j)
    {
      f += arrayOfFloat[i] * arrayOfFloat[i];
      i += 1;
    }
    f = (float)Math.sqrt(f);
    i = 0;
    while (i < j)
    {
      arrayOfFloat[i] /= f;
      i += 1;
    }
  }
  
  private static float[] spatialSampler(Gesture paramGesture)
  {
    return GestureUtils.spatialSampling(paramGesture, 16, false);
  }
  
  private static float[] temporalSampler(int paramInt, Gesture paramGesture)
  {
    paramGesture = GestureUtils.temporalSampling((GestureStroke)paramGesture.getStrokes().get(0), 16);
    float[] arrayOfFloat = GestureUtils.computeCentroid(paramGesture);
    float f4 = (float)Math.atan2(paramGesture[1] - arrayOfFloat[1], paramGesture[0] - arrayOfFloat[0]);
    float f1 = -f4;
    float f2 = f1;
    if (paramInt != 1)
    {
      int i = ORIENTATIONS.length;
      paramInt = 0;
      for (;;)
      {
        f2 = f1;
        if (paramInt >= i) {
          break;
        }
        float f3 = ORIENTATIONS[paramInt] - f4;
        f2 = f1;
        if (Math.abs(f3) < Math.abs(f1)) {
          f2 = f3;
        }
        paramInt += 1;
        f1 = f2;
      }
    }
    GestureUtils.translate(paramGesture, -arrayOfFloat[0], -arrayOfFloat[1]);
    GestureUtils.rotate(paramGesture, f2);
    return paramGesture;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/gesture/Instance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */