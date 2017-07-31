package com.oneplus.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public final class HandlerUtils
{
  private static final String TAG = "HandlerUtils";
  
  public static boolean hasAsyncMessages(AsyncHandlerObject paramAsyncHandlerObject, int paramInt)
  {
    if (paramAsyncHandlerObject == null) {
      return false;
    }
    paramAsyncHandlerObject = paramAsyncHandlerObject.getAsyncHandler();
    if (paramAsyncHandlerObject == null) {
      return false;
    }
    return paramAsyncHandlerObject.hasMessages(paramInt);
  }
  
  public static boolean hasMessages(HandlerObject paramHandlerObject, int paramInt)
  {
    if (paramHandlerObject == null) {
      return false;
    }
    paramHandlerObject = paramHandlerObject.getHandler();
    if (paramHandlerObject == null) {
      return false;
    }
    return paramHandlerObject.hasMessages(paramInt);
  }
  
  public static boolean post(Handler paramHandler, Runnable paramRunnable, long paramLong)
  {
    if (paramHandler == null)
    {
      Log.e("HandlerUtils", "post() - No handler to post");
      return false;
    }
    if (paramLong <= 0L) {
      return paramHandler.post(paramRunnable);
    }
    return paramHandler.postDelayed(paramRunnable, paramLong);
  }
  
  public static boolean post(HandlerObject paramHandlerObject, Runnable paramRunnable)
  {
    return post(paramHandlerObject, paramRunnable, 0L);
  }
  
  public static boolean post(HandlerObject paramHandlerObject, Runnable paramRunnable, long paramLong)
  {
    if (paramHandlerObject == null)
    {
      Log.e("HandlerUtils", "post() - No target to post");
      return false;
    }
    paramHandlerObject = paramHandlerObject.getHandler();
    if (paramHandlerObject == null)
    {
      Log.e("HandlerUtils", "post() - No Handler to post");
      return false;
    }
    return post(paramHandlerObject, paramRunnable, paramLong);
  }
  
  public static void postAndWait(HandlerObject paramHandlerObject, Runnable paramRunnable)
    throws InterruptedException
  {
    postAndWait(paramHandlerObject, paramRunnable, 0L, -1L);
  }
  
  public static void postAndWait(HandlerObject paramHandlerObject, Runnable paramRunnable, long paramLong)
    throws InterruptedException
  {
    postAndWait(paramHandlerObject, paramRunnable, paramLong, -1L);
  }
  
  public static boolean postAndWait(Handler paramHandler, Runnable paramRunnable, long paramLong1, long paramLong2)
    throws InterruptedException
  {
    if (paramHandler == null)
    {
      Log.e("HandlerUtils", "postAndWait() - No handler to post");
      return false;
    }
    if (Thread.currentThread() != paramHandler.getLooper().getThread())
    {
      final boolean[] arrayOfBoolean = new boolean[1];
      arrayOfBoolean[0] = false;
      paramRunnable = new Runnable()
      {
        public void run()
        {
          this.val$r.run();
          synchronized (arrayOfBoolean)
          {
            arrayOfBoolean[0] = true;
            arrayOfBoolean.notifyAll();
            return;
          }
        }
      };
      try
      {
        if (post(paramHandler, paramRunnable, paramLong1))
        {
          if (paramLong2 < 0L) {
            break label85;
          }
          arrayOfBoolean.wait(paramLong2);
        }
        for (;;)
        {
          int i = arrayOfBoolean[0];
          return i;
          label85:
          arrayOfBoolean.wait();
        }
        paramRunnable.run();
      }
      finally {}
    }
    return true;
  }
  
  public static boolean postAndWait(HandlerObject paramHandlerObject, Runnable paramRunnable, long paramLong1, long paramLong2)
    throws InterruptedException
  {
    if (paramHandlerObject == null)
    {
      Log.e("HandlerUtils", "postAndWait() - No target to post");
      return false;
    }
    return postAndWait(paramHandlerObject.getHandler(), paramRunnable, paramLong1, paramLong2);
  }
  
  public static boolean postAsync(AsyncHandlerObject paramAsyncHandlerObject, Runnable paramRunnable)
  {
    return postAsync(paramAsyncHandlerObject, paramRunnable, 0L);
  }
  
  public static boolean postAsync(AsyncHandlerObject paramAsyncHandlerObject, Runnable paramRunnable, long paramLong)
  {
    if (paramAsyncHandlerObject == null)
    {
      Log.e("HandlerUtils", "postAsync() - No target to post");
      return false;
    }
    paramAsyncHandlerObject = paramAsyncHandlerObject.getAsyncHandler();
    if (paramAsyncHandlerObject == null)
    {
      Log.e("HandlerUtils", "postAsync() - No Handler to post");
      return false;
    }
    if (paramLong <= 0L) {
      return paramAsyncHandlerObject.post(paramRunnable);
    }
    return paramAsyncHandlerObject.postDelayed(paramRunnable, paramLong);
  }
  
  public static void removeAsyncCallbacks(AsyncHandlerObject paramAsyncHandlerObject, Runnable paramRunnable)
  {
    if (paramAsyncHandlerObject == null) {
      return;
    }
    paramAsyncHandlerObject = paramAsyncHandlerObject.getAsyncHandler();
    if (paramAsyncHandlerObject == null) {
      return;
    }
    paramAsyncHandlerObject.removeCallbacks(paramRunnable);
  }
  
  public static void removeAsyncMessages(AsyncHandlerObject paramAsyncHandlerObject, int paramInt)
  {
    if (paramAsyncHandlerObject == null) {
      return;
    }
    paramAsyncHandlerObject = paramAsyncHandlerObject.getAsyncHandler();
    if (paramAsyncHandlerObject == null) {
      return;
    }
    paramAsyncHandlerObject.removeMessages(paramInt);
  }
  
  public static void removeAsyncMessages(AsyncHandlerObject paramAsyncHandlerObject, int paramInt, Object paramObject)
  {
    if (paramAsyncHandlerObject == null) {
      return;
    }
    paramAsyncHandlerObject = paramAsyncHandlerObject.getAsyncHandler();
    if (paramAsyncHandlerObject == null) {
      return;
    }
    paramAsyncHandlerObject.removeMessages(paramInt, paramObject);
  }
  
  public static void removeCallbacks(HandlerObject paramHandlerObject, Runnable paramRunnable)
  {
    if (paramHandlerObject == null) {
      return;
    }
    paramHandlerObject = paramHandlerObject.getHandler();
    if (paramHandlerObject == null) {
      return;
    }
    paramHandlerObject.removeCallbacks(paramRunnable);
  }
  
  public static void removeMessages(HandlerObject paramHandlerObject, int paramInt)
  {
    if (paramHandlerObject == null) {
      return;
    }
    paramHandlerObject = paramHandlerObject.getHandler();
    if (paramHandlerObject == null) {
      return;
    }
    paramHandlerObject.removeMessages(paramInt);
  }
  
  public static void removeMessages(HandlerObject paramHandlerObject, int paramInt, Object paramObject)
  {
    if (paramHandlerObject == null) {
      return;
    }
    paramHandlerObject = paramHandlerObject.getHandler();
    if (paramHandlerObject == null) {
      return;
    }
    paramHandlerObject.removeMessages(paramInt, paramObject);
  }
  
  public static boolean sendAsyncMessage(AsyncHandlerObject paramAsyncHandlerObject, int paramInt)
  {
    return sendAsyncMessage(paramAsyncHandlerObject, paramInt, 0, 0, null, false, 0L);
  }
  
  public static boolean sendAsyncMessage(AsyncHandlerObject paramAsyncHandlerObject, int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    return sendAsyncMessage(paramAsyncHandlerObject, paramInt1, paramInt2, paramInt3, paramObject, false, 0L);
  }
  
  public static boolean sendAsyncMessage(AsyncHandlerObject paramAsyncHandlerObject, int paramInt1, int paramInt2, int paramInt3, Object paramObject, long paramLong)
  {
    return sendAsyncMessage(paramAsyncHandlerObject, paramInt1, paramInt2, paramInt3, paramObject, false, paramLong);
  }
  
  public static boolean sendAsyncMessage(AsyncHandlerObject paramAsyncHandlerObject, int paramInt1, int paramInt2, int paramInt3, Object paramObject, boolean paramBoolean)
  {
    return sendAsyncMessage(paramAsyncHandlerObject, paramInt1, paramInt2, paramInt3, paramObject, paramBoolean, 0L);
  }
  
  public static boolean sendAsyncMessage(AsyncHandlerObject paramAsyncHandlerObject, int paramInt1, int paramInt2, int paramInt3, Object paramObject, boolean paramBoolean, long paramLong)
  {
    if (paramAsyncHandlerObject == null)
    {
      Log.e("HandlerUtils", "sendAsyncMessage() - No target to send " + paramInt1);
      return false;
    }
    paramAsyncHandlerObject = paramAsyncHandlerObject.getAsyncHandler();
    if (paramAsyncHandlerObject == null)
    {
      Log.e("HandlerUtils", "sendAsyncMessage() - No Handler to send " + paramInt1);
      return false;
    }
    if (paramBoolean) {
      paramAsyncHandlerObject.removeMessages(paramInt1);
    }
    paramObject = Message.obtain(paramAsyncHandlerObject, paramInt1, paramInt2, paramInt3, paramObject);
    if (paramLong <= 0L) {
      return paramAsyncHandlerObject.sendMessage((Message)paramObject);
    }
    return paramAsyncHandlerObject.sendMessageDelayed((Message)paramObject, paramLong);
  }
  
  public static boolean sendAsyncMessage(AsyncHandlerObject paramAsyncHandlerObject, int paramInt, long paramLong)
  {
    return sendAsyncMessage(paramAsyncHandlerObject, paramInt, 0, 0, null, false, paramLong);
  }
  
  public static boolean sendAsyncMessage(AsyncHandlerObject paramAsyncHandlerObject, int paramInt, Object paramObject)
  {
    return sendAsyncMessage(paramAsyncHandlerObject, paramInt, 0, 0, paramObject, false, 0L);
  }
  
  public static boolean sendAsyncMessage(AsyncHandlerObject paramAsyncHandlerObject, int paramInt, boolean paramBoolean)
  {
    return sendAsyncMessage(paramAsyncHandlerObject, paramInt, 0, 0, null, paramBoolean, 0L);
  }
  
  public static boolean sendAsyncMessage(AsyncHandlerObject paramAsyncHandlerObject, int paramInt, boolean paramBoolean, long paramLong)
  {
    return sendAsyncMessage(paramAsyncHandlerObject, paramInt, 0, 0, null, paramBoolean, paramLong);
  }
  
  public static boolean sendMessage(HandlerObject paramHandlerObject, int paramInt)
  {
    return sendMessage(paramHandlerObject, paramInt, 0, 0, null, false, 0L);
  }
  
  public static boolean sendMessage(HandlerObject paramHandlerObject, int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    return sendMessage(paramHandlerObject, paramInt1, paramInt2, paramInt3, paramObject, false, 0L);
  }
  
  public static boolean sendMessage(HandlerObject paramHandlerObject, int paramInt1, int paramInt2, int paramInt3, Object paramObject, long paramLong)
  {
    return sendMessage(paramHandlerObject, paramInt1, paramInt2, paramInt3, paramObject, false, paramLong);
  }
  
  public static boolean sendMessage(HandlerObject paramHandlerObject, int paramInt1, int paramInt2, int paramInt3, Object paramObject, boolean paramBoolean)
  {
    return sendMessage(paramHandlerObject, paramInt1, paramInt2, paramInt3, paramObject, paramBoolean, 0L);
  }
  
  public static boolean sendMessage(HandlerObject paramHandlerObject, int paramInt1, int paramInt2, int paramInt3, Object paramObject, boolean paramBoolean, long paramLong)
  {
    if (paramHandlerObject == null)
    {
      Log.e("HandlerUtils", "sendMessage() - No target to send " + paramInt1);
      return false;
    }
    paramHandlerObject = paramHandlerObject.getHandler();
    if (paramHandlerObject == null)
    {
      Log.e("HandlerUtils", "sendMessage() - No Handler to send " + paramInt1);
      return false;
    }
    if (paramBoolean) {
      paramHandlerObject.removeMessages(paramInt1);
    }
    paramObject = Message.obtain(paramHandlerObject, paramInt1, paramInt2, paramInt3, paramObject);
    if (paramLong <= 0L) {
      return paramHandlerObject.sendMessage((Message)paramObject);
    }
    return paramHandlerObject.sendMessageDelayed((Message)paramObject, paramLong);
  }
  
  public static boolean sendMessage(HandlerObject paramHandlerObject, int paramInt, long paramLong)
  {
    return sendMessage(paramHandlerObject, paramInt, 0, 0, null, false, paramLong);
  }
  
  public static boolean sendMessage(HandlerObject paramHandlerObject, int paramInt, Object paramObject)
  {
    return sendMessage(paramHandlerObject, paramInt, 0, 0, paramObject, false, 0L);
  }
  
  public static boolean sendMessage(HandlerObject paramHandlerObject, int paramInt, boolean paramBoolean)
  {
    return sendMessage(paramHandlerObject, paramInt, 0, 0, null, paramBoolean, 0L);
  }
  
  public static boolean sendMessage(HandlerObject paramHandlerObject, int paramInt, boolean paramBoolean, long paramLong)
  {
    return sendMessage(paramHandlerObject, paramInt, 0, 0, null, paramBoolean, paramLong);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/HandlerUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */