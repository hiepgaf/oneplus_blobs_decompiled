package android.hardware.camera2.dispatch;

import com.android.internal.util.Preconditions;
import java.lang.reflect.Method;

public class DuckTypingDispatcher<TFrom, T>
  implements Dispatchable<TFrom>
{
  private final MethodNameInvoker<T> mDuck;
  
  public DuckTypingDispatcher(Dispatchable<T> paramDispatchable, Class<T> paramClass)
  {
    Preconditions.checkNotNull(paramClass, "targetClass must not be null");
    Preconditions.checkNotNull(paramDispatchable, "target must not be null");
    this.mDuck = new MethodNameInvoker(paramDispatchable, paramClass);
  }
  
  public Object dispatch(Method paramMethod, Object[] paramArrayOfObject)
  {
    return this.mDuck.invoke(paramMethod.getName(), paramArrayOfObject);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/dispatch/DuckTypingDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */