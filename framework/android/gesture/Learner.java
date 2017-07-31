package android.gesture;

import java.util.ArrayList;

abstract class Learner
{
  private final ArrayList<Instance> mInstances = new ArrayList();
  
  void addInstance(Instance paramInstance)
  {
    this.mInstances.add(paramInstance);
  }
  
  abstract ArrayList<Prediction> classify(int paramInt1, int paramInt2, float[] paramArrayOfFloat);
  
  ArrayList<Instance> getInstances()
  {
    return this.mInstances;
  }
  
  void removeInstance(long paramLong)
  {
    ArrayList localArrayList = this.mInstances;
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      Instance localInstance = (Instance)localArrayList.get(i);
      if (paramLong == localInstance.id)
      {
        localArrayList.remove(localInstance);
        return;
      }
      i += 1;
    }
  }
  
  void removeInstances(String paramString)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = this.mInstances;
    int j = localArrayList2.size();
    int i = 0;
    if (i < j)
    {
      Instance localInstance = (Instance)localArrayList2.get(i);
      if ((localInstance.label == null) && (paramString == null)) {}
      for (;;)
      {
        localArrayList1.add(localInstance);
        do
        {
          i += 1;
          break;
        } while ((localInstance.label == null) || (!localInstance.label.equals(paramString)));
      }
    }
    localArrayList2.removeAll(localArrayList1);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/gesture/Learner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */