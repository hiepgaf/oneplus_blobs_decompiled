package android.gesture;

import java.util.ArrayList;
import java.util.Set;

public abstract class GestureLibrary
{
  protected final GestureStore mStore = new GestureStore();
  
  public void addGesture(String paramString, Gesture paramGesture)
  {
    this.mStore.addGesture(paramString, paramGesture);
  }
  
  public Set<String> getGestureEntries()
  {
    return this.mStore.getGestureEntries();
  }
  
  public ArrayList<Gesture> getGestures(String paramString)
  {
    return this.mStore.getGestures(paramString);
  }
  
  public Learner getLearner()
  {
    return this.mStore.getLearner();
  }
  
  public int getOrientationStyle()
  {
    return this.mStore.getOrientationStyle();
  }
  
  public int getSequenceType()
  {
    return this.mStore.getSequenceType();
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  public abstract boolean load();
  
  public ArrayList<Prediction> recognize(Gesture paramGesture)
  {
    return this.mStore.recognize(paramGesture);
  }
  
  public void removeEntry(String paramString)
  {
    this.mStore.removeEntry(paramString);
  }
  
  public void removeGesture(String paramString, Gesture paramGesture)
  {
    this.mStore.removeGesture(paramString, paramGesture);
  }
  
  public abstract boolean save();
  
  public void setOrientationStyle(int paramInt)
  {
    this.mStore.setOrientationStyle(paramInt);
  }
  
  public void setSequenceType(int paramInt)
  {
    this.mStore.setSequenceType(paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/gesture/GestureLibrary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */