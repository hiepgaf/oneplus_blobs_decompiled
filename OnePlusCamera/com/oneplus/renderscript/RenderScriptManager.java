package com.oneplus.renderscript;

import android.content.Context;
import android.renderscript.RenderScript;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;

public final class RenderScriptManager
{
  private static final String TAG = "RenderScriptManager";
  private static final ThreadLocal<RenderScriptState> m_RenderScriptState = new ThreadLocal();
  
  public static Handle createRenderScript(Context paramContext)
  {
    RenderScriptState localRenderScriptState = (RenderScriptState)m_RenderScriptState.get();
    if (localRenderScriptState == null)
    {
      paramContext = new RenderScriptState(RenderScript.create(paramContext));
      m_RenderScriptState.set(paramContext);
    }
    for (;;)
    {
      return new RenderScriptHandle();
      localRenderScriptState.referenceCounter += 1;
    }
  }
  
  private static void destroyRenderScript(RenderScriptHandle paramRenderScriptHandle)
  {
    if (paramRenderScriptHandle.thread != Thread.currentThread()) {
      throw new IllegalAccessError("Cannot destroy RenderScript context from another thread.");
    }
    paramRenderScriptHandle = (RenderScriptState)m_RenderScriptState.get();
    if (paramRenderScriptHandle != null)
    {
      paramRenderScriptHandle.referenceCounter -= 1;
      if (paramRenderScriptHandle.referenceCounter <= 0)
      {
        m_RenderScriptState.set(null);
        paramRenderScriptHandle.renderScript.finish();
        paramRenderScriptHandle.renderScript.destroy();
      }
    }
  }
  
  public static RenderScript getRenderScript(Handle paramHandle)
  {
    if (paramHandle == null)
    {
      Log.e("RenderScriptManager", "getRenderScript() - Null handle");
      return null;
    }
    if (((paramHandle instanceof RenderScriptHandle)) && (Handle.isValid(paramHandle)))
    {
      if (((RenderScriptHandle)paramHandle).thread != Thread.currentThread()) {
        throw new IllegalAccessError("Cannot get RenderScript context from another thread.");
      }
    }
    else
    {
      Log.e("RenderScriptManager", "getRenderScript() - Invalid handle");
      return null;
    }
    paramHandle = (RenderScriptState)m_RenderScriptState.get();
    if (paramHandle != null) {
      return paramHandle.renderScript;
    }
    Log.e("RenderScriptManager", "getRenderScript() - No RenderScript context");
    return null;
  }
  
  private static final class RenderScriptHandle
    extends Handle
  {
    public final Thread thread = Thread.currentThread();
    
    public RenderScriptHandle()
    {
      super();
    }
    
    protected void onClose(int paramInt)
    {
      RenderScriptManager.-wrap0(this);
    }
  }
  
  private static final class RenderScriptState
  {
    public int referenceCounter = 1;
    public final RenderScript renderScript;
    
    public RenderScriptState(RenderScript paramRenderScript)
    {
      this.renderScript = paramRenderScript;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/renderscript/RenderScriptManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */