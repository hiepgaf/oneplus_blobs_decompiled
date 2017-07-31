package android.media.effect;

import android.filterfw.core.CachedFrameManager;
import android.filterfw.core.FilterContext;
import android.filterfw.core.GLEnvironment;
import android.opengl.GLES20;

public class EffectContext
{
  private final int GL_STATE_ARRAYBUFFER = 2;
  private final int GL_STATE_COUNT = 3;
  private final int GL_STATE_FBO = 0;
  private final int GL_STATE_PROGRAM = 1;
  private EffectFactory mFactory;
  FilterContext mFilterContext = new FilterContext();
  private int[] mOldState = new int[3];
  
  private EffectContext()
  {
    this.mFilterContext.setFrameManager(new CachedFrameManager());
    this.mFactory = new EffectFactory(this);
  }
  
  public static EffectContext createWithCurrentGlContext()
  {
    EffectContext localEffectContext = new EffectContext();
    localEffectContext.initInCurrentGlContext();
    return localEffectContext;
  }
  
  private void initInCurrentGlContext()
  {
    if (!GLEnvironment.isAnyContextActive()) {
      throw new RuntimeException("Attempting to initialize EffectContext with no active GL context!");
    }
    GLEnvironment localGLEnvironment = new GLEnvironment();
    localGLEnvironment.initWithCurrentContext();
    this.mFilterContext.initGLEnvironment(localGLEnvironment);
  }
  
  final void assertValidGLState()
  {
    GLEnvironment localGLEnvironment = this.mFilterContext.getGLEnvironment();
    if ((localGLEnvironment != null) && (localGLEnvironment.isContextActive())) {
      return;
    }
    if (GLEnvironment.isAnyContextActive()) {
      throw new RuntimeException("Applying effect in wrong GL context!");
    }
    throw new RuntimeException("Attempting to apply effect without valid GL context!");
  }
  
  public EffectFactory getFactory()
  {
    return this.mFactory;
  }
  
  public void release()
  {
    this.mFilterContext.tearDown();
    this.mFilterContext = null;
  }
  
  final void restoreGLState()
  {
    GLES20.glBindFramebuffer(36160, this.mOldState[0]);
    GLES20.glUseProgram(this.mOldState[1]);
    GLES20.glBindBuffer(34962, this.mOldState[2]);
  }
  
  final void saveGLState()
  {
    GLES20.glGetIntegerv(36006, this.mOldState, 0);
    GLES20.glGetIntegerv(35725, this.mOldState, 1);
    GLES20.glGetIntegerv(34964, this.mOldState, 2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/EffectContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */