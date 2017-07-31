package com.oneplus.gl;

import android.opengl.EGL14;
import android.opengl.GLUtils;
import com.oneplus.base.Log;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class EglContextManager
{
  private static final String TAG = "EglContextManager";
  public static final long THRESHOLD_GL_OPERATION_DURATION = 20L;
  private static final ThreadLocal<EglContextState> m_CurrentEglContextState = new ThreadLocal();
  
  public static void addCallback(Callback paramCallback)
  {
    EglContextState localEglContextState = getEglContextState(true);
    List localList = localEglContextState.callbacks;
    Object localObject = localList;
    if (localList == null)
    {
      localObject = new ArrayList();
      localEglContextState.callbacks = ((List)localObject);
    }
    ((List)localObject).add(paramCallback);
    if (localEglContextState.isEglContextReady) {
      paramCallback.onEglContextReady();
    }
  }
  
  private static EglContextState getEglContextState(boolean paramBoolean)
  {
    EglContextState localEglContextState2 = (EglContextState)m_CurrentEglContextState.get();
    EglContextState localEglContextState1 = localEglContextState2;
    if (localEglContextState2 == null)
    {
      localEglContextState1 = localEglContextState2;
      if (paramBoolean)
      {
        localEglContextState1 = new EglContextState(null);
        m_CurrentEglContextState.set(localEglContextState1);
      }
    }
    return localEglContextState1;
  }
  
  public static boolean isEglContextReady()
  {
    boolean bool = false;
    EglContextState localEglContextState = getEglContextState(false);
    if (localEglContextState != null) {
      bool = localEglContextState.isEglContextReady;
    }
    return bool;
  }
  
  public static boolean isGLProfilingEnabled()
  {
    return false;
  }
  
  public static void notifyEglContextDestroying()
  {
    EglContextState localEglContextState = getEglContextState(false);
    int i;
    int j;
    EglObjectHolder localEglObjectHolder;
    if ((localEglContextState != null) && (localEglContextState.isEglContextReady))
    {
      localEglContextState.isEglContextReady = true;
      i = 0;
      j = 0;
      localObject = localEglContextState.activeEglObjectHolders;
      if (localObject == null) {
        break label91;
      }
      localEglObjectHolder = ((EglObjectHolder)localObject).nextHolder;
      EglObject localEglObject = (EglObject)((EglObjectHolder)localObject).eglObject.get();
      if (localEglObject == null) {
        break label78;
      }
      i += 1;
      localEglObject.onEglContextDestroying();
    }
    for (;;)
    {
      localObject = localEglObjectHolder;
      break;
      return;
      label78:
      j += 1;
      recycleEglObjectHolder(localEglContextState, (EglObjectHolder)localObject);
    }
    label91:
    Object localObject = localEglContextState.callbacks;
    if (localObject != null)
    {
      int k = ((List)localObject).size() - 1;
      while (k >= 0)
      {
        ((Callback)((List)localObject).get(k)).onEglContextDestroying();
        k -= 1;
      }
    }
    Log.v("EglContextManager", "notifyEglContextDestroying() - ", Integer.valueOf(i), " notified, ", Integer.valueOf(j), " recycled");
  }
  
  public static void notifyEglContextReady()
  {
    EglContextState localEglContextState = getEglContextState(true);
    if (localEglContextState.isEglContextReady) {
      return;
    }
    localEglContextState.isEglContextReady = true;
    Object localObject = localEglContextState.callbacks;
    if (localObject != null)
    {
      i = ((List)localObject).size() - 1;
      while (i >= 0)
      {
        ((Callback)((List)localObject).get(i)).onEglContextReady();
        i -= 1;
      }
    }
    int i = 0;
    int j = 0;
    localObject = localEglContextState.activeEglObjectHolders;
    if (localObject != null)
    {
      EglObjectHolder localEglObjectHolder = ((EglObjectHolder)localObject).nextHolder;
      EglObject localEglObject = (EglObject)((EglObjectHolder)localObject).eglObject.get();
      if (localEglObject != null)
      {
        i += 1;
        localEglObject.onEglContextReady();
      }
      for (;;)
      {
        localObject = localEglObjectHolder;
        break;
        j += 1;
        recycleEglObjectHolder(localEglContextState, (EglObjectHolder)localObject);
      }
    }
    Log.v("EglContextManager", "notifyEglContextReady() - ", Integer.valueOf(i), " notified, ", Integer.valueOf(j), " recycled");
  }
  
  private static void recycleEglObjectHolder(EglContextState paramEglContextState, EglObjectHolder paramEglObjectHolder)
  {
    if ((paramEglContextState != null) && (paramEglObjectHolder != null))
    {
      if (paramEglObjectHolder.prevHolder != null) {
        paramEglObjectHolder.prevHolder.nextHolder = paramEglObjectHolder.nextHolder;
      }
      if (paramEglObjectHolder.nextHolder != null) {
        paramEglObjectHolder.nextHolder.prevHolder = paramEglObjectHolder.prevHolder;
      }
      paramEglObjectHolder.prevHolder = null;
      paramEglObjectHolder.nextHolder = paramEglContextState.freeEglObjectHolders;
      if (paramEglContextState.freeEglObjectHolders != null) {
        paramEglContextState.freeEglObjectHolders.prevHolder = paramEglObjectHolder;
      }
      paramEglContextState.freeEglObjectHolders = paramEglObjectHolder;
      paramEglObjectHolder.eglObject = null;
    }
  }
  
  static void registerEglObject(EglObject paramEglObject)
  {
    EglContextState localEglContextState = getEglContextState(true);
    EglObjectHolder localEglObjectHolder2 = localEglContextState.freeEglObjectHolders;
    if (localEglObjectHolder2 != null)
    {
      localEglObjectHolder2.prevHolder = null;
      localEglObjectHolder1 = localEglObjectHolder2;
      if (localEglObjectHolder2.nextHolder != null) {
        localEglObjectHolder2.nextHolder.prevHolder = null;
      }
    }
    for (EglObjectHolder localEglObjectHolder1 = localEglObjectHolder2;; localEglObjectHolder1 = new EglObjectHolder())
    {
      localEglObjectHolder1.eglObject = new WeakReference(paramEglObject);
      localEglObjectHolder1.nextHolder = localEglContextState.activeEglObjectHolders;
      if (localEglContextState.activeEglObjectHolders != null) {
        localEglContextState.activeEglObjectHolders.prevHolder = localEglObjectHolder1;
      }
      paramEglObject.holder = localEglObjectHolder1;
      return;
    }
  }
  
  static void throwEglError(String paramString)
  {
    int i = EGL14.eglGetError();
    if (i != 12288) {
      throw new RuntimeException(paramString + ", error : " + i + ", message : " + GLUtils.getEGLErrorString(i));
    }
    throw new RuntimeException(paramString);
  }
  
  static void unregisterEglObject(EglObject paramEglObject)
  {
    recycleEglObjectHolder(getEglContextState(false), paramEglObject.holder);
    paramEglObject.holder = null;
  }
  
  public static abstract interface Callback
  {
    public abstract void onEglContextDestroying();
    
    public abstract void onEglContextReady();
  }
  
  private static final class EglContextState
  {
    public EglObjectHolder activeEglObjectHolders;
    public List<EglContextManager.Callback> callbacks;
    public EglObjectHolder freeEglObjectHolders;
    public boolean isEglContextReady;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/EglContextManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */