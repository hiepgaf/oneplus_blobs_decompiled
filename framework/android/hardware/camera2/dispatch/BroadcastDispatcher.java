package android.hardware.camera2.dispatch;

import com.android.internal.util.Preconditions;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BroadcastDispatcher<T>
  implements Dispatchable<T>
{
  private final List<Dispatchable<T>> mDispatchTargets;
  
  @SafeVarargs
  public BroadcastDispatcher(Dispatchable<T>... paramVarArgs)
  {
    this.mDispatchTargets = Arrays.asList((Dispatchable[])Preconditions.checkNotNull(paramVarArgs, "dispatchTargets must not be null"));
  }
  
  public Object dispatch(Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    Object localObject1 = null;
    int i = 0;
    Iterator localIterator = this.mDispatchTargets.iterator();
    while (localIterator.hasNext())
    {
      Object localObject2 = ((Dispatchable)localIterator.next()).dispatch(paramMethod, paramArrayOfObject);
      if (i == 0)
      {
        i = 1;
        localObject1 = localObject2;
      }
    }
    return localObject1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/dispatch/BroadcastDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */