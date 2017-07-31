package android.hardware.camera2.dispatch;

import android.hardware.camera2.utils.UncheckedThrow;
import android.os.Handler;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerDispatcher<T>
  implements Dispatchable<T>
{
  private static final String TAG = "HandlerDispatcher";
  private final Dispatchable<T> mDispatchTarget;
  private final Handler mHandler;
  
  public HandlerDispatcher(Dispatchable<T> paramDispatchable, Handler paramHandler)
  {
    this.mDispatchTarget = ((Dispatchable)Preconditions.checkNotNull(paramDispatchable, "dispatchTarget must not be null"));
    this.mHandler = ((Handler)Preconditions.checkNotNull(paramHandler, "handler must not be null"));
  }
  
  public Object dispatch(final Method paramMethod, final Object[] paramArrayOfObject)
    throws Throwable
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        try
        {
          HandlerDispatcher.-get0(HandlerDispatcher.this).dispatch(paramMethod, paramArrayOfObject);
          return;
        }
        catch (Throwable localThrowable)
        {
          UncheckedThrow.throwAnyException(localThrowable);
          return;
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          Log.wtf("HandlerDispatcher", "IllegalArgumentException while invoking " + paramMethod, localIllegalArgumentException);
          return;
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          Log.wtf("HandlerDispatcher", "IllegalAccessException while invoking " + paramMethod, localIllegalAccessException);
          return;
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          UncheckedThrow.throwAnyException(localInvocationTargetException.getTargetException());
        }
      }
    });
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/dispatch/HandlerDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */