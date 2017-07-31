package android.support.v4.util;

public class Pair<F, S>
{
  public final F first;
  public final S second;
  
  public Pair(F paramF, S paramS)
  {
    this.first = paramF;
    this.second = paramS;
  }
  
  public static <A, B> Pair<A, B> create(A paramA, B paramB)
  {
    return new Pair(paramA, paramB);
  }
  
  private static boolean objectsEqual(Object paramObject1, Object paramObject2)
  {
    boolean bool = false;
    if (paramObject1 == paramObject2) {}
    do
    {
      bool = true;
      do
      {
        return bool;
      } while (paramObject1 == null);
    } while (paramObject1.equals(paramObject2));
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Pair))
    {
      paramObject = (Pair)paramObject;
      if (objectsEqual(((Pair)paramObject).first, this.first)) {
        break label30;
      }
    }
    label30:
    while (!objectsEqual(((Pair)paramObject).second, this.second))
    {
      return false;
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    int j = 0;
    if (this.first != null) {}
    for (int i = this.first.hashCode();; i = 0)
    {
      if (this.second != null) {
        j = this.second.hashCode();
      }
      return i ^ j;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/util/Pair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */