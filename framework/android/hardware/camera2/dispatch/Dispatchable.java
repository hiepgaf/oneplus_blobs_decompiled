package android.hardware.camera2.dispatch;

import java.lang.reflect.Method;

public abstract interface Dispatchable<T>
{
  public abstract Object dispatch(Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/dispatch/Dispatchable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */