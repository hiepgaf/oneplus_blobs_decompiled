package android.gesture;

public class Prediction
{
  public final String name;
  public double score;
  
  Prediction(String paramString, double paramDouble)
  {
    this.name = paramString;
    this.score = paramDouble;
  }
  
  public String toString()
  {
    return this.name;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/gesture/Prediction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */