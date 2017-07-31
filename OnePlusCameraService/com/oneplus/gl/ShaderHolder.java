package com.oneplus.gl;

import android.opengl.GLES20;
import java.util.Hashtable;
import java.util.Iterator;

public final class ShaderHolder
{
  private static final ThreadLocal<ShaderHolder> m_ShaderHolder = new ThreadLocal();
  private final Hashtable<ShaderKey, ShaderInfo> m_Shaders = new Hashtable();
  
  private ShaderHolder()
  {
    EglContextManager.addCallback(new EglContextManager.Callback()
    {
      public void onEglContextDestroying()
      {
        ShaderHolder.-get0(ShaderHolder.this).clear();
      }
      
      public void onEglContextReady() {}
    });
  }
  
  public static int createShader(int paramInt, String paramString)
  {
    if (paramString == null) {
      return 0;
    }
    Object localObject2 = (ShaderHolder)m_ShaderHolder.get();
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = new ShaderHolder();
      m_ShaderHolder.set(localObject1);
    }
    localObject2 = new ShaderKey(paramInt, paramString);
    ShaderInfo localShaderInfo = (ShaderInfo)((ShaderHolder)localObject1).m_Shaders.get(localObject2);
    if (localShaderInfo != null)
    {
      localShaderInfo.referenceCount += 1;
      return localShaderInfo.shader;
    }
    paramInt = GLES20.glCreateShader(paramInt);
    if (paramInt == 0) {
      return 0;
    }
    GLES20.glShaderSource(paramInt, paramString);
    GLES20.glCompileShader(paramInt);
    paramString = new int[1];
    GLES20.glGetShaderiv(paramInt, 35713, paramString, 0);
    if (paramString[0] == 0)
    {
      GLES20.glDeleteShader(paramInt);
      paramString = GLES20.glGetShaderInfoLog(paramInt);
      throw new RuntimeException("Fail to compile shader : " + paramString);
    }
    ((ShaderHolder)localObject1).m_Shaders.put(localObject2, new ShaderInfo((ShaderKey)localObject2, paramInt));
    return paramInt;
  }
  
  public static void deleteShader(int paramInt)
  {
    ShaderHolder localShaderHolder = (ShaderHolder)m_ShaderHolder.get();
    if (localShaderHolder == null)
    {
      GLES20.glDeleteShader(paramInt);
      return;
    }
    Iterator localIterator = localShaderHolder.m_Shaders.values().iterator();
    while (localIterator.hasNext())
    {
      ShaderInfo localShaderInfo = (ShaderInfo)localIterator.next();
      if (localShaderInfo.shader == paramInt)
      {
        localShaderInfo.referenceCount -= 1;
        if (localShaderInfo.referenceCount <= 0)
        {
          GLES20.glDeleteShader(paramInt);
          localShaderHolder.m_Shaders.remove(localShaderInfo.key);
        }
      }
    }
  }
  
  private static final class ShaderInfo
  {
    public final ShaderHolder.ShaderKey key;
    public int referenceCount;
    public final int shader;
    
    public ShaderInfo(ShaderHolder.ShaderKey paramShaderKey, int paramInt)
    {
      this.key = paramShaderKey;
      this.shader = paramInt;
      this.referenceCount = 1;
    }
  }
  
  private static final class ShaderKey
  {
    public final String source;
    public final int type;
    
    public ShaderKey(int paramInt, String paramString)
    {
      this.source = paramString;
      this.type = paramInt;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = false;
      if ((paramObject instanceof ShaderKey))
      {
        paramObject = (ShaderKey)paramObject;
        if (this.type == ((ShaderKey)paramObject).type) {
          bool = this.source.equals(((ShaderKey)paramObject).source);
        }
        return bool;
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.source.hashCode();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/ShaderHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */