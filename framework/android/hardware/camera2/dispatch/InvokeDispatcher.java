package android.hardware.camera2.dispatch;

import android.hardware.camera2.utils.UncheckedThrow;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InvokeDispatcher<T>
  implements Dispatchable<T>
{
  private static final String TAG = "InvocationSink";
  private final T mTarget;
  
  public InvokeDispatcher(T paramT)
  {
    this.mTarget = Preconditions.checkNotNull(paramT, "target must not be null");
  }
  
  public Object dispatch(Method paramMethod, Object[] paramArrayOfObject)
  {
    try
    {
      paramArrayOfObject = paramMethod.invoke(this.mTarget, paramArrayOfObject);
      return paramArrayOfObject;
    }
    catch (IllegalArgumentException paramArrayOfObject)
    {
      Log.wtf("InvocationSink", "IllegalArgumentException while invoking " + paramMethod, paramArrayOfObject);
      return null;
    }
    catch (IllegalAccessException paramArrayOfObject)
    {
      for (;;)
      {
        Log.wtf("InvocationSink", "IllegalAccessException while invoking " + paramMethod, paramArrayOfObject);
      }
    }
    catch (InvocationTargetException paramMethod)
    {
      for (;;)
      {
        UncheckedThrow.throwAnyException(paramMethod.getTargetException());
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/dispatch/InvokeDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */