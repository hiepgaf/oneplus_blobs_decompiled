package android.gesture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

class InstanceLearner
  extends Learner
{
  private static final Comparator<Prediction> sComparator = new Comparator()
  {
    public int compare(Prediction paramAnonymousPrediction1, Prediction paramAnonymousPrediction2)
    {
      double d1 = paramAnonymousPrediction1.score;
      double d2 = paramAnonymousPrediction2.score;
      if (d1 > d2) {
        return -1;
      }
      if (d1 < d2) {
        return 1;
      }
      return 0;
    }
  };
  
  ArrayList<Prediction> classify(int paramInt1, int paramInt2, float[] paramArrayOfFloat)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject = getInstances();
    int j = ((ArrayList)localObject).size();
    TreeMap localTreeMap = new TreeMap();
    int i = 0;
    while (i < j)
    {
      Instance localInstance = (Instance)((ArrayList)localObject).get(i);
      if (localInstance.vector.length != paramArrayOfFloat.length)
      {
        i += 1;
      }
      else
      {
        if (paramInt1 == 2)
        {
          d = GestureUtils.minimumCosineDistance(localInstance.vector, paramArrayOfFloat, paramInt2);
          label91:
          if (d != 0.0D) {
            break label168;
          }
        }
        label168:
        for (double d = Double.MAX_VALUE;; d = 1.0D / d)
        {
          Double localDouble = (Double)localTreeMap.get(localInstance.label);
          if ((localDouble != null) && (d <= localDouble.doubleValue())) {
            break;
          }
          localTreeMap.put(localInstance.label, Double.valueOf(d));
          break;
          d = GestureUtils.squaredEuclideanDistance(localInstance.vector, paramArrayOfFloat);
          break label91;
        }
      }
    }
    paramArrayOfFloat = localTreeMap.keySet().iterator();
    while (paramArrayOfFloat.hasNext())
    {
      localObject = (String)paramArrayOfFloat.next();
      localArrayList.add(new Prediction((String)localObject, ((Double)localTreeMap.get(localObject)).doubleValue()));
    }
    Collections.sort(localArrayList, sComparator);
    return localArrayList;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/gesture/InstanceLearner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */