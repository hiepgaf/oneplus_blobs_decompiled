package android.hardware.camera2.dispatch;

import android.hardware.camera2.utils.UncheckedThrow;
import com.android.internal.util.Preconditions;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class MethodNameInvoker<T>
{
  private final ConcurrentHashMap<String, Method> mMethods = new ConcurrentHashMap();
  private final Dispatchable<T> mTarget;
  private final Class<T> mTargetClass;
  
  public MethodNameInvoker(Dispatchable<T> paramDispatchable, Class<T> paramClass)
  {
    this.mTargetClass = paramClass;
    this.mTarget = paramDispatchable;
  }
  
  public <K> K invoke(String paramString, Object... paramVarArgs)
  {
    Preconditions.checkNotNull(paramString, "methodName must not be null");
    Method localMethod = (Method)this.mMethods.get(paramString);
    Object localObject2 = localMethod;
    if (localMethod == null)
    {
      Method[] arrayOfMethod = this.mTargetClass.getMethods();
      int i = 0;
      int j = arrayOfMethod.length;
      for (;;)
      {
        Object localObject1 = localMethod;
        if (i < j)
        {
          localObject2 = arrayOfMethod[i];
          if ((((Method)localObject2).getName().equals(paramString)) && (paramVarArgs.length == ((Method)localObject2).getParameterTypes().length))
          {
            localObject1 = localObject2;
            this.mMethods.put(paramString, localObject2);
          }
        }
        else
        {
          localObject2 = localObject1;
          if (localObject1 != null) {
            break;
          }
          throw new IllegalArgumentException("Method " + paramString + " does not exist on class " + this.mTargetClass);
        }
        i += 1;
      }
    }
    try
    {
      paramString = this.mTarget.dispatch((Method)localObject2, paramVarArgs);
      return paramString;
    }
    catch (Throwable paramString)
    {
      UncheckedThrow.throwAnyException(paramString);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/dispatch/MethodNameInvoker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */