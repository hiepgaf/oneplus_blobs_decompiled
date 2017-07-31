package android.hardware.camera2.dispatch;

import com.android.internal.util.Preconditions;
import java.lang.reflect.Method;

public class ArgumentReplacingDispatcher<T, TArg>
  implements Dispatchable<T>
{
  private final int mArgumentIndex;
  private final TArg mReplaceWith;
  private final Dispatchable<T> mTarget;
  
  public ArgumentReplacingDispatcher(Dispatchable<T> paramDispatchable, int paramInt, TArg paramTArg)
  {
    this.mTarget = ((Dispatchable)Preconditions.checkNotNull(paramDispatchable, "target must not be null"));
    this.mArgumentIndex = Preconditions.checkArgumentNonnegative(paramInt, "argumentIndex must not be negative");
    this.mReplaceWith = Preconditions.checkNotNull(paramTArg, "replaceWith must not be null");
  }
  
  private static Object[] arrayCopy(Object[] paramArrayOfObject)
  {
    int j = paramArrayOfObject.length;
    Object[] arrayOfObject = new Object[j];
    int i = 0;
    while (i < j)
    {
      arrayOfObject[i] = paramArrayOfObject[i];
      i += 1;
    }
    return arrayOfObject;
  }
  
  public Object dispatch(Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    Object[] arrayOfObject = paramArrayOfObject;
    if (paramArrayOfObject.length > this.mArgumentIndex)
    {
      arrayOfObject = arrayCopy(paramArrayOfObject);
      arrayOfObject[this.mArgumentIndex] = this.mReplaceWith;
    }
    return this.mTarget.dispatch(paramMethod, arrayOfObject);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/dispatch/ArgumentReplacingDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */