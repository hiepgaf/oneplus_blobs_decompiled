package android.hardware.camera2.dispatch;

import java.lang.reflect.Method;

public class NullDispatcher<T>
  implements Dispatchable<T>
{
  public Object dispatch(Method paramMethod, Object[] paramArrayOfObject)
  {
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/dispatch/NullDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */