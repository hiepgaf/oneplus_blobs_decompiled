package android.graphics.drawable;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.PropertyValuesHolder.PropertyValues;
import android.animation.PropertyValuesHolder.PropertyValues.DataSource;
import android.animation.ValueAnimator;
import android.app.ActivityThread;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.IntArray;
import android.util.Log;
import android.util.LongArray;
import android.util.PathParser.PathData;
import android.util.Property;
import android.view.Choreographer;
import android.view.DisplayListCanvas;
import android.view.RenderNode;
import android.view.RenderNodeAnimatorSetHelper;
import com.android.internal.R.styleable;
import com.android.internal.util.VirtualRefBasePtr;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AnimatedVectorDrawable
  extends Drawable
  implements Animatable2
{
  private static final String ANIMATED_VECTOR = "animated-vector";
  private static final boolean DBG_ANIMATION_VECTOR_DRAWABLE = false;
  private static final String LOGTAG = "AnimatedVectorDrawable";
  private static final String TARGET = "target";
  private AnimatedVectorDrawableState mAnimatedVectorState;
  private ArrayList<Animatable2.AnimationCallback> mAnimationCallbacks = null;
  private Animator.AnimatorListener mAnimatorListener = null;
  private VectorDrawableAnimator mAnimatorSet;
  private AnimatorSet mAnimatorSetFromXml = null;
  private final Drawable.Callback mCallback = new Drawable.Callback()
  {
    public void invalidateDrawable(Drawable paramAnonymousDrawable)
    {
      AnimatedVectorDrawable.this.invalidateSelf();
    }
    
    public void scheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable, long paramAnonymousLong)
    {
      AnimatedVectorDrawable.this.scheduleSelf(paramAnonymousRunnable, paramAnonymousLong);
    }
    
    public void unscheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable)
    {
      AnimatedVectorDrawable.this.unscheduleSelf(paramAnonymousRunnable);
    }
  };
  private boolean mMutated;
  private Resources mRes;
  
  public AnimatedVectorDrawable()
  {
    this(null, null);
  }
  
  private AnimatedVectorDrawable(AnimatedVectorDrawableState paramAnimatedVectorDrawableState, Resources paramResources)
  {
    this.mAnimatedVectorState = new AnimatedVectorDrawableState(paramAnimatedVectorDrawableState, this.mCallback, paramResources);
    this.mAnimatorSet = new VectorDrawableAnimatorRT(this);
    this.mRes = paramResources;
  }
  
  private static boolean containsSameValueType(PropertyValuesHolder paramPropertyValuesHolder, Property paramProperty)
  {
    paramPropertyValuesHolder = paramPropertyValuesHolder.getValueType();
    paramProperty = paramProperty.getType();
    if ((paramPropertyValuesHolder == Float.TYPE) || (paramPropertyValuesHolder == Float.class)) {
      return (paramProperty == Float.TYPE) || (paramProperty == Float.class);
    }
    if ((paramPropertyValuesHolder == Integer.TYPE) || (paramPropertyValuesHolder == Integer.class)) {
      return (paramProperty == Integer.TYPE) || (paramProperty == Integer.class);
    }
    return paramPropertyValuesHolder == paramProperty;
  }
  
  private void ensureAnimatorSet()
  {
    if (this.mAnimatorSetFromXml == null)
    {
      this.mAnimatorSetFromXml = new AnimatorSet();
      this.mAnimatedVectorState.prepareLocalAnimators(this.mAnimatorSetFromXml, this.mRes);
      this.mAnimatorSet.init(this.mAnimatorSetFromXml);
      this.mRes = null;
    }
  }
  
  private void fallbackOntoUI()
  {
    if ((this.mAnimatorSet instanceof VectorDrawableAnimatorRT))
    {
      VectorDrawableAnimatorRT localVectorDrawableAnimatorRT = (VectorDrawableAnimatorRT)this.mAnimatorSet;
      this.mAnimatorSet = new VectorDrawableAnimatorUI(this);
      if (this.mAnimatorSetFromXml != null) {
        this.mAnimatorSet.init(this.mAnimatorSetFromXml);
      }
      if (VectorDrawableAnimatorRT.-get0(localVectorDrawableAnimatorRT) != null) {
        this.mAnimatorSet.setListener(VectorDrawableAnimatorRT.-get0(localVectorDrawableAnimatorRT));
      }
      VectorDrawableAnimatorRT.-wrap0(localVectorDrawableAnimatorRT, this.mAnimatorSet);
    }
  }
  
  private static native void nAddAnimator(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, int paramInt1, int paramInt2);
  
  private static native long nCreateAnimatorSet();
  
  private static native long nCreateGroupPropertyHolder(long paramLong, int paramInt, float paramFloat1, float paramFloat2);
  
  private static native long nCreatePathColorPropertyHolder(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  private static native long nCreatePathDataPropertyHolder(long paramLong1, long paramLong2, long paramLong3);
  
  private static native long nCreatePathPropertyHolder(long paramLong, int paramInt, float paramFloat1, float paramFloat2);
  
  private static native long nCreateRootAlphaPropertyHolder(long paramLong, float paramFloat1, float paramFloat2);
  
  private static native void nEnd(long paramLong);
  
  private static native void nReset(long paramLong);
  
  private static native void nReverse(long paramLong, VectorDrawableAnimatorRT paramVectorDrawableAnimatorRT, int paramInt);
  
  private static native void nSetPropertyHolderData(long paramLong, float[] paramArrayOfFloat, int paramInt);
  
  private static native void nSetPropertyHolderData(long paramLong, int[] paramArrayOfInt, int paramInt);
  
  private static native void nSetVectorDrawableTarget(long paramLong1, long paramLong2);
  
  private static native void nStart(long paramLong, VectorDrawableAnimatorRT paramVectorDrawableAnimatorRT, int paramInt);
  
  private void removeAnimatorSetListener()
  {
    if (this.mAnimatorListener != null)
    {
      this.mAnimatorSet.removeListener(this.mAnimatorListener);
      this.mAnimatorListener = null;
    }
  }
  
  private static boolean shouldIgnoreInvalidAnimation()
  {
    Application localApplication = ActivityThread.currentApplication();
    if ((localApplication == null) || (localApplication.getApplicationInfo() == null)) {
      return true;
    }
    return localApplication.getApplicationInfo().targetSdkVersion < 24;
  }
  
  private static void updateAnimatorProperty(Animator paramAnimator, String paramString, VectorDrawable paramVectorDrawable, boolean paramBoolean)
  {
    if ((paramAnimator instanceof ObjectAnimator))
    {
      PropertyValuesHolder[] arrayOfPropertyValuesHolder = ((ObjectAnimator)paramAnimator).getValues();
      int i = 0;
      if (i < arrayOfPropertyValuesHolder.length)
      {
        PropertyValuesHolder localPropertyValuesHolder = arrayOfPropertyValuesHolder[i];
        String str = localPropertyValuesHolder.getPropertyName();
        Object localObject = paramVectorDrawable.getTargetByName(paramString);
        paramAnimator = null;
        if ((localObject instanceof VectorDrawable.VObject))
        {
          paramAnimator = ((VectorDrawable.VObject)localObject).getProperty(str);
          label69:
          if (paramAnimator != null)
          {
            if (!containsSameValueType(localPropertyValuesHolder, paramAnimator)) {
              break label119;
            }
            localPropertyValuesHolder.setProperty(paramAnimator);
          }
        }
        label119:
        while (paramBoolean)
        {
          i += 1;
          break;
          if (!(localObject instanceof VectorDrawable.VectorDrawableState)) {
            break label69;
          }
          paramAnimator = ((VectorDrawable.VectorDrawableState)localObject).getProperty(str);
          break label69;
        }
        throw new RuntimeException("Wrong valueType for Property: " + str + ".  Expected type: " + paramAnimator.getType().toString() + ". Actual " + "type defined in resources: " + localPropertyValuesHolder.getValueType().toString());
      }
    }
    else if ((paramAnimator instanceof AnimatorSet))
    {
      paramAnimator = ((AnimatorSet)paramAnimator).getChildAnimations().iterator();
      while (paramAnimator.hasNext()) {
        updateAnimatorProperty((Animator)paramAnimator.next(), paramString, paramVectorDrawable, paramBoolean);
      }
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    VectorDrawable localVectorDrawable = this.mAnimatedVectorState.mVectorDrawable;
    if ((localVectorDrawable != null) && (localVectorDrawable.canApplyTheme())) {
      localVectorDrawable.applyTheme(paramTheme);
    }
    if (paramTheme != null) {
      this.mAnimatedVectorState.inflatePendingAnimators(paramTheme.getResources(), paramTheme);
    }
    if (this.mAnimatedVectorState.mPendingAnims == null) {
      this.mRes = null;
    }
  }
  
  public boolean canApplyTheme()
  {
    if ((this.mAnimatedVectorState == null) || (!this.mAnimatedVectorState.canApplyTheme())) {
      return super.canApplyTheme();
    }
    return true;
  }
  
  public boolean canReverse()
  {
    return this.mAnimatorSet.canReverse();
  }
  
  public void clearAnimationCallbacks()
  {
    removeAnimatorSetListener();
    if (this.mAnimationCallbacks == null) {
      return;
    }
    this.mAnimationCallbacks.clear();
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    if (this.mAnimatedVectorState.mVectorDrawable != null) {
      this.mAnimatedVectorState.mVectorDrawable.clearMutated();
    }
    this.mMutated = false;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if ((!paramCanvas.isHardwareAccelerated()) && ((this.mAnimatorSet instanceof VectorDrawableAnimatorRT)) && (!this.mAnimatorSet.isRunning()) && (VectorDrawableAnimatorRT.-get1((VectorDrawableAnimatorRT)this.mAnimatorSet).size() > 0)) {
      fallbackOntoUI();
    }
    this.mAnimatorSet.onDraw(paramCanvas);
    this.mAnimatedVectorState.mVectorDrawable.draw(paramCanvas);
  }
  
  public void forceAnimationOnUI()
  {
    if ((this.mAnimatorSet instanceof VectorDrawableAnimatorRT))
    {
      if (((VectorDrawableAnimatorRT)this.mAnimatorSet).isRunning()) {
        throw new UnsupportedOperationException("Cannot force Animated Vector Drawable to run on UI thread when the animation has started on RenderThread.");
      }
      fallbackOntoUI();
    }
  }
  
  public int getAlpha()
  {
    return this.mAnimatedVectorState.mVectorDrawable.getAlpha();
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.mAnimatedVectorState.getChangingConfigurations();
  }
  
  public ColorFilter getColorFilter()
  {
    return this.mAnimatedVectorState.mVectorDrawable.getColorFilter();
  }
  
  public Drawable.ConstantState getConstantState()
  {
    this.mAnimatedVectorState.mChangingConfigurations = getChangingConfigurations();
    return this.mAnimatedVectorState;
  }
  
  public int getIntrinsicHeight()
  {
    return this.mAnimatedVectorState.mVectorDrawable.getIntrinsicHeight();
  }
  
  public int getIntrinsicWidth()
  {
    return this.mAnimatedVectorState.mVectorDrawable.getIntrinsicWidth();
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public Insets getOpticalInsets()
  {
    return this.mAnimatedVectorState.mVectorDrawable.getOpticalInsets();
  }
  
  public void getOutline(Outline paramOutline)
  {
    this.mAnimatedVectorState.mVectorDrawable.getOutline(paramOutline);
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    AnimatedVectorDrawableState localAnimatedVectorDrawableState = this.mAnimatedVectorState;
    int i = paramXmlPullParser.getEventType();
    float f1 = 1.0F;
    int j = paramXmlPullParser.getDepth();
    if ((i != 1) && ((paramXmlPullParser.getDepth() >= j + 1) || (i != 3)))
    {
      float f2 = f1;
      if (i == 2)
      {
        localObject1 = paramXmlPullParser.getName();
        if (!"animated-vector".equals(localObject1)) {
          break label190;
        }
        localObject1 = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.AnimatedVectorDrawable);
        i = ((TypedArray)localObject1).getResourceId(0, 0);
        if (i != 0)
        {
          localObject2 = (VectorDrawable)paramResources.getDrawable(i, paramTheme).mutate();
          ((VectorDrawable)localObject2).setAllowCaching(false);
          ((VectorDrawable)localObject2).setCallback(this.mCallback);
          f1 = ((VectorDrawable)localObject2).getPixelSize();
          if (localAnimatedVectorDrawableState.mVectorDrawable != null) {
            localAnimatedVectorDrawableState.mVectorDrawable.setCallback(null);
          }
          localAnimatedVectorDrawableState.mVectorDrawable = ((VectorDrawable)localObject2);
        }
        ((TypedArray)localObject1).recycle();
        f2 = f1;
      }
      label190:
      do
      {
        i = paramXmlPullParser.next();
        f1 = f2;
        break;
        f2 = f1;
      } while (!"target".equals(localObject1));
      Object localObject1 = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.AnimatedVectorDrawableTarget);
      Object localObject2 = ((TypedArray)localObject1).getString(0);
      i = ((TypedArray)localObject1).getResourceId(1, 0);
      if (i != 0)
      {
        if (paramTheme == null) {
          break label293;
        }
        Animator localAnimator = AnimatorInflater.loadAnimator(paramResources, paramTheme, i, f1);
        updateAnimatorProperty(localAnimator, (String)localObject2, localAnimatedVectorDrawableState.mVectorDrawable, AnimatedVectorDrawableState.-get0(localAnimatedVectorDrawableState));
        localAnimatedVectorDrawableState.addTargetAnimator((String)localObject2, localAnimator);
      }
      for (;;)
      {
        ((TypedArray)localObject1).recycle();
        f2 = f1;
        break;
        label293:
        localAnimatedVectorDrawableState.addPendingAnimator(i, f1, (String)localObject2);
      }
    }
    if (localAnimatedVectorDrawableState.mPendingAnims == null) {
      paramResources = null;
    }
    this.mRes = paramResources;
  }
  
  public boolean isRunning()
  {
    return this.mAnimatorSet.isRunning();
  }
  
  public boolean isStateful()
  {
    return this.mAnimatedVectorState.mVectorDrawable.isStateful();
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      this.mAnimatedVectorState = new AnimatedVectorDrawableState(this.mAnimatedVectorState, this.mCallback, this.mRes);
      this.mMutated = true;
    }
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    this.mAnimatedVectorState.mVectorDrawable.setBounds(paramRect);
  }
  
  public boolean onLayoutDirectionChanged(int paramInt)
  {
    return this.mAnimatedVectorState.mVectorDrawable.setLayoutDirection(paramInt);
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    return this.mAnimatedVectorState.mVectorDrawable.setLevel(paramInt);
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    return this.mAnimatedVectorState.mVectorDrawable.setState(paramArrayOfInt);
  }
  
  public void registerAnimationCallback(Animatable2.AnimationCallback paramAnimationCallback)
  {
    if (paramAnimationCallback == null) {
      return;
    }
    if (this.mAnimationCallbacks == null) {
      this.mAnimationCallbacks = new ArrayList();
    }
    this.mAnimationCallbacks.add(paramAnimationCallback);
    if (this.mAnimatorListener == null) {
      this.mAnimatorListener = new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          paramAnonymousAnimator = new ArrayList(AnimatedVectorDrawable.-get1(AnimatedVectorDrawable.this));
          int j = paramAnonymousAnimator.size();
          int i = 0;
          while (i < j)
          {
            ((Animatable2.AnimationCallback)paramAnonymousAnimator.get(i)).onAnimationEnd(AnimatedVectorDrawable.this);
            i += 1;
          }
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          paramAnonymousAnimator = new ArrayList(AnimatedVectorDrawable.-get1(AnimatedVectorDrawable.this));
          int j = paramAnonymousAnimator.size();
          int i = 0;
          while (i < j)
          {
            ((Animatable2.AnimationCallback)paramAnonymousAnimator.get(i)).onAnimationStart(AnimatedVectorDrawable.this);
            i += 1;
          }
        }
      };
    }
    this.mAnimatorSet.setListener(this.mAnimatorListener);
  }
  
  public void reset()
  {
    ensureAnimatorSet();
    this.mAnimatorSet.reset();
  }
  
  public void reverse()
  {
    ensureAnimatorSet();
    if (!canReverse())
    {
      Log.w("AnimatedVectorDrawable", "AnimatedVectorDrawable can't reverse()");
      return;
    }
    this.mAnimatorSet.reverse();
  }
  
  public void setAlpha(int paramInt)
  {
    this.mAnimatedVectorState.mVectorDrawable.setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mAnimatedVectorState.mVectorDrawable.setColorFilter(paramColorFilter);
  }
  
  public void setHotspot(float paramFloat1, float paramFloat2)
  {
    this.mAnimatedVectorState.mVectorDrawable.setHotspot(paramFloat1, paramFloat2);
  }
  
  public void setHotspotBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mAnimatedVectorState.mVectorDrawable.setHotspotBounds(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    this.mAnimatedVectorState.mVectorDrawable.setTintList(paramColorStateList);
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    this.mAnimatedVectorState.mVectorDrawable.setTintMode(paramMode);
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((this.mAnimatorSet.isInfinite()) && (this.mAnimatorSet.isStarted()))
    {
      if (!paramBoolean1) {
        break label57;
      }
      this.mAnimatorSet.resume();
    }
    for (;;)
    {
      this.mAnimatedVectorState.mVectorDrawable.setVisible(paramBoolean1, paramBoolean2);
      return super.setVisible(paramBoolean1, paramBoolean2);
      label57:
      this.mAnimatorSet.pause();
    }
  }
  
  public void start()
  {
    ensureAnimatorSet();
    this.mAnimatorSet.start();
  }
  
  public void stop()
  {
    this.mAnimatorSet.end();
  }
  
  public boolean unregisterAnimationCallback(Animatable2.AnimationCallback paramAnimationCallback)
  {
    if ((this.mAnimationCallbacks == null) || (paramAnimationCallback == null)) {
      return false;
    }
    boolean bool = this.mAnimationCallbacks.remove(paramAnimationCallback);
    if (this.mAnimationCallbacks.size() == 0) {
      removeAnimatorSetListener();
    }
    return bool;
  }
  
  private static class AnimatedVectorDrawableState
    extends Drawable.ConstantState
  {
    ArrayList<Animator> mAnimators;
    int mChangingConfigurations;
    ArrayList<PendingAnimator> mPendingAnims;
    private final boolean mShouldIgnoreInvalidAnim = AnimatedVectorDrawable.-wrap0();
    ArrayMap<Animator, String> mTargetNameMap;
    VectorDrawable mVectorDrawable;
    
    public AnimatedVectorDrawableState(AnimatedVectorDrawableState paramAnimatedVectorDrawableState, Drawable.Callback paramCallback, Resources paramResources)
    {
      if (paramAnimatedVectorDrawableState != null)
      {
        this.mChangingConfigurations = paramAnimatedVectorDrawableState.mChangingConfigurations;
        Drawable.ConstantState localConstantState;
        if (paramAnimatedVectorDrawableState.mVectorDrawable != null)
        {
          localConstantState = paramAnimatedVectorDrawableState.mVectorDrawable.getConstantState();
          if (paramResources == null) {
            break label182;
          }
        }
        label182:
        for (this.mVectorDrawable = ((VectorDrawable)localConstantState.newDrawable(paramResources));; this.mVectorDrawable = ((VectorDrawable)localConstantState.newDrawable()))
        {
          this.mVectorDrawable = ((VectorDrawable)this.mVectorDrawable.mutate());
          this.mVectorDrawable.setCallback(paramCallback);
          this.mVectorDrawable.setLayoutDirection(paramAnimatedVectorDrawableState.mVectorDrawable.getLayoutDirection());
          this.mVectorDrawable.setBounds(paramAnimatedVectorDrawableState.mVectorDrawable.getBounds());
          this.mVectorDrawable.setAllowCaching(false);
          if (paramAnimatedVectorDrawableState.mAnimators != null) {
            this.mAnimators = new ArrayList(paramAnimatedVectorDrawableState.mAnimators);
          }
          if (paramAnimatedVectorDrawableState.mTargetNameMap != null) {
            this.mTargetNameMap = new ArrayMap(paramAnimatedVectorDrawableState.mTargetNameMap);
          }
          if (paramAnimatedVectorDrawableState.mPendingAnims != null) {
            this.mPendingAnims = new ArrayList(paramAnimatedVectorDrawableState.mPendingAnims);
          }
          return;
        }
      }
      this.mVectorDrawable = new VectorDrawable();
    }
    
    private Animator prepareLocalAnimator(int paramInt)
    {
      Object localObject = (Animator)this.mAnimators.get(paramInt);
      Animator localAnimator = ((Animator)localObject).clone();
      localObject = (String)this.mTargetNameMap.get(localObject);
      localAnimator.setTarget(this.mVectorDrawable.getTargetByName((String)localObject));
      return localAnimator;
    }
    
    public void addPendingAnimator(int paramInt, float paramFloat, String paramString)
    {
      if (this.mPendingAnims == null) {
        this.mPendingAnims = new ArrayList(1);
      }
      this.mPendingAnims.add(new PendingAnimator(paramInt, paramFloat, paramString));
    }
    
    public void addTargetAnimator(String paramString, Animator paramAnimator)
    {
      if (this.mAnimators == null)
      {
        this.mAnimators = new ArrayList(1);
        this.mTargetNameMap = new ArrayMap(1);
      }
      this.mAnimators.add(paramAnimator);
      this.mTargetNameMap.put(paramAnimator, paramString);
    }
    
    public boolean canApplyTheme()
    {
      if (((this.mVectorDrawable != null) && (this.mVectorDrawable.canApplyTheme())) || (this.mPendingAnims != null)) {
        return true;
      }
      return super.canApplyTheme();
    }
    
    public int getChangingConfigurations()
    {
      return this.mChangingConfigurations;
    }
    
    public void inflatePendingAnimators(Resources paramResources, Resources.Theme paramTheme)
    {
      ArrayList localArrayList = this.mPendingAnims;
      if (localArrayList != null)
      {
        this.mPendingAnims = null;
        int i = 0;
        int j = localArrayList.size();
        while (i < j)
        {
          PendingAnimator localPendingAnimator = (PendingAnimator)localArrayList.get(i);
          Animator localAnimator = localPendingAnimator.newInstance(paramResources, paramTheme);
          AnimatedVectorDrawable.-wrap15(localAnimator, localPendingAnimator.target, this.mVectorDrawable, this.mShouldIgnoreInvalidAnim);
          addTargetAnimator(localPendingAnimator.target, localAnimator);
          i += 1;
        }
      }
    }
    
    public Drawable newDrawable()
    {
      return new AnimatedVectorDrawable(this, null, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new AnimatedVectorDrawable(this, paramResources, null);
    }
    
    public void prepareLocalAnimators(AnimatorSet paramAnimatorSet, Resources paramResources)
    {
      if (this.mPendingAnims != null)
      {
        if (paramResources != null)
        {
          inflatePendingAnimators(paramResources, null);
          this.mPendingAnims = null;
        }
      }
      else {
        if (this.mAnimators != null) {
          break label85;
        }
      }
      label85:
      for (int i = 0;; i = this.mAnimators.size())
      {
        if (i <= 0) {
          return;
        }
        paramAnimatorSet = paramAnimatorSet.play(prepareLocalAnimator(0));
        int j = 1;
        while (j < i)
        {
          paramAnimatorSet.with(prepareLocalAnimator(j));
          j += 1;
        }
        Log.e("AnimatedVectorDrawable", "Failed to load animators. Either the AnimatedVectorDrawable must be created using a Resources object or applyTheme() must be called with a non-null Theme object.");
        break;
      }
    }
    
    private static class PendingAnimator
    {
      public final int animResId;
      public final float pathErrorScale;
      public final String target;
      
      public PendingAnimator(int paramInt, float paramFloat, String paramString)
      {
        this.animResId = paramInt;
        this.pathErrorScale = paramFloat;
        this.target = paramString;
      }
      
      public Animator newInstance(Resources paramResources, Resources.Theme paramTheme)
      {
        return AnimatorInflater.loadAnimator(paramResources, paramTheme, this.animResId, this.pathErrorScale);
      }
    }
  }
  
  private static abstract interface VectorDrawableAnimator
  {
    public abstract boolean canReverse();
    
    public abstract void end();
    
    public abstract void init(AnimatorSet paramAnimatorSet);
    
    public abstract boolean isInfinite();
    
    public abstract boolean isRunning();
    
    public abstract boolean isStarted();
    
    public abstract void onDraw(Canvas paramCanvas);
    
    public abstract void pause();
    
    public abstract void removeListener(Animator.AnimatorListener paramAnimatorListener);
    
    public abstract void reset();
    
    public abstract void resume();
    
    public abstract void reverse();
    
    public abstract void setListener(Animator.AnimatorListener paramAnimatorListener);
    
    public abstract void start();
  }
  
  public static class VectorDrawableAnimatorRT
    implements AnimatedVectorDrawable.VectorDrawableAnimator
  {
    private static final int END_ANIMATION = 4;
    private static final int MAX_SAMPLE_POINTS = 300;
    private static final int RESET_ANIMATION = 3;
    private static final int REVERSE_ANIMATION = 2;
    private static final int START_ANIMATION = 1;
    private boolean mContainsSequentialAnimators = false;
    private final AnimatedVectorDrawable mDrawable;
    private boolean mInitialized = false;
    private boolean mIsInfinite = false;
    private boolean mIsReversible = false;
    private int mLastListenerId = 0;
    private WeakReference<RenderNode> mLastSeenTarget = null;
    private Animator.AnimatorListener mListener = null;
    private final IntArray mPendingAnimationActions = new IntArray();
    private long mSetPtr = 0L;
    private final VirtualRefBasePtr mSetRefBasePtr;
    private final LongArray mStartDelays = new LongArray();
    private boolean mStarted = false;
    private PropertyValuesHolder.PropertyValues mTmpValues = new PropertyValuesHolder.PropertyValues();
    
    VectorDrawableAnimatorRT(AnimatedVectorDrawable paramAnimatedVectorDrawable)
    {
      this.mDrawable = paramAnimatedVectorDrawable;
      this.mSetPtr = AnimatedVectorDrawable.-wrap1();
      this.mSetRefBasePtr = new VirtualRefBasePtr(this.mSetPtr);
    }
    
    private void addPendingAction(int paramInt)
    {
      invalidateOwningView();
      this.mPendingAnimationActions.add(paramInt);
    }
    
    private static void callOnFinished(VectorDrawableAnimatorRT paramVectorDrawableAnimatorRT, int paramInt)
    {
      paramVectorDrawableAnimatorRT.onAnimationEnd(paramInt);
    }
    
    private static float[] createFloatDataPoints(PropertyValuesHolder.PropertyValues.DataSource paramDataSource, long paramLong)
    {
      int j = getFrameCount(paramLong);
      float[] arrayOfFloat = new float[j];
      float f = j - 1;
      int i = 0;
      while (i < j)
      {
        arrayOfFloat[i] = ((Float)paramDataSource.getValueAtFraction(i / f)).floatValue();
        i += 1;
      }
      return arrayOfFloat;
    }
    
    private static int[] createIntDataPoints(PropertyValuesHolder.PropertyValues.DataSource paramDataSource, long paramLong)
    {
      int j = getFrameCount(paramLong);
      int[] arrayOfInt = new int[j];
      float f = j - 1;
      int i = 0;
      while (i < j)
      {
        arrayOfInt[i] = ((Integer)paramDataSource.getValueAtFraction(i / f)).intValue();
        i += 1;
      }
      return arrayOfInt;
    }
    
    private void createNativeChildAnimator(long paramLong1, long paramLong2, ObjectAnimator paramObjectAnimator)
    {
      long l2 = paramObjectAnimator.getDuration();
      int i = paramObjectAnimator.getRepeatCount();
      long l3 = paramObjectAnimator.getStartDelay();
      long l1 = RenderNodeAnimatorSetHelper.createNativeInterpolator(paramObjectAnimator.getInterpolator(), l2);
      paramLong2 = ((float)(paramLong2 + l3) * ValueAnimator.getDurationScale());
      l2 = ((float)l2 * ValueAnimator.getDurationScale());
      this.mStartDelays.add(paramLong2);
      AnimatedVectorDrawable.-wrap7(this.mSetPtr, paramLong1, l1, paramLong2, l2, i, paramObjectAnimator.getRepeatMode());
    }
    
    private void createRTAnimator(ObjectAnimator paramObjectAnimator, long paramLong)
    {
      PropertyValuesHolder[] arrayOfPropertyValuesHolder = paramObjectAnimator.getValues();
      Object localObject = paramObjectAnimator.getTarget();
      if ((localObject instanceof VectorDrawable.VGroup)) {
        createRTAnimatorForGroup(arrayOfPropertyValuesHolder, paramObjectAnimator, (VectorDrawable.VGroup)localObject, paramLong);
      }
      label45:
      label113:
      label135:
      do
      {
        return;
        if ((localObject instanceof VectorDrawable.VPath))
        {
          int i = 0;
          if (i < arrayOfPropertyValuesHolder.length)
          {
            arrayOfPropertyValuesHolder[i].getPropertyValues(this.mTmpValues);
            if ((!(this.mTmpValues.endValue instanceof PathParser.PathData)) || (!this.mTmpValues.propertyName.equals("pathData"))) {
              break label113;
            }
            createRTAnimatorForPath(paramObjectAnimator, (VectorDrawable.VPath)localObject, paramLong);
          }
          do
          {
            for (;;)
            {
              i += 1;
              break label45;
              break;
              if (!(localObject instanceof VectorDrawable.VFullPath)) {
                break label135;
              }
              createRTAnimatorForFullPath(paramObjectAnimator, (VectorDrawable.VFullPath)localObject, paramLong);
            }
          } while (AnimatedVectorDrawable.AnimatedVectorDrawableState.-get0(AnimatedVectorDrawable.-get0(this.mDrawable)));
          throw new IllegalArgumentException("ClipPath only supports PathData property");
        }
        if ((localObject instanceof VectorDrawable.VectorDrawableState))
        {
          createRTAnimatorForRootGroup(arrayOfPropertyValuesHolder, paramObjectAnimator, (VectorDrawable.VectorDrawableState)localObject, paramLong);
          return;
        }
      } while (AnimatedVectorDrawable.AnimatedVectorDrawableState.-get0(AnimatedVectorDrawable.-get0(this.mDrawable)));
      if ("Target should be either VGroup, VPath, or ConstantState, " + localObject == null) {}
      for (paramObjectAnimator = "Null target";; paramObjectAnimator = localObject.getClass() + " is not supported") {
        throw new UnsupportedOperationException(paramObjectAnimator);
      }
    }
    
    private void createRTAnimatorForFullPath(ObjectAnimator paramObjectAnimator, VectorDrawable.VFullPath paramVFullPath, long paramLong)
    {
      int i = paramVFullPath.getPropertyIndex(this.mTmpValues.propertyName);
      long l1 = paramVFullPath.getNativePtr();
      long l2;
      if ((this.mTmpValues.type == Float.class) || (this.mTmpValues.type == Float.TYPE))
      {
        if (i < 0)
        {
          if (AnimatedVectorDrawable.AnimatedVectorDrawableState.-get0(AnimatedVectorDrawable.-get0(this.mDrawable))) {
            return;
          }
          throw new IllegalArgumentException("Property: " + this.mTmpValues.propertyName + " is not supported for FullPath");
        }
        l2 = AnimatedVectorDrawable.-wrap5(l1, i, ((Float)this.mTmpValues.startValue).floatValue(), ((Float)this.mTmpValues.endValue).floatValue());
        l1 = l2;
        if (this.mTmpValues.dataSource != null)
        {
          paramVFullPath = createFloatDataPoints(this.mTmpValues.dataSource, paramObjectAnimator.getDuration());
          AnimatedVectorDrawable.-wrap11(l2, paramVFullPath, paramVFullPath.length);
          l1 = l2;
        }
      }
      for (;;)
      {
        createNativeChildAnimator(l1, paramLong, paramObjectAnimator);
        return;
        if ((this.mTmpValues.type != Integer.class) && (this.mTmpValues.type != Integer.TYPE)) {
          break;
        }
        l2 = AnimatedVectorDrawable.-wrap3(l1, i, ((Integer)this.mTmpValues.startValue).intValue(), ((Integer)this.mTmpValues.endValue).intValue());
        l1 = l2;
        if (this.mTmpValues.dataSource != null)
        {
          paramVFullPath = createIntDataPoints(this.mTmpValues.dataSource, paramObjectAnimator.getDuration());
          AnimatedVectorDrawable.-wrap12(l2, paramVFullPath, paramVFullPath.length);
          l1 = l2;
        }
      }
      if (AnimatedVectorDrawable.AnimatedVectorDrawableState.-get0(AnimatedVectorDrawable.-get0(this.mDrawable))) {
        return;
      }
      throw new UnsupportedOperationException("Unsupported type: " + this.mTmpValues.type + ". Only float, int or PathData value is " + "supported for Paths.");
    }
    
    private void createRTAnimatorForGroup(PropertyValuesHolder[] paramArrayOfPropertyValuesHolder, ObjectAnimator paramObjectAnimator, VectorDrawable.VGroup paramVGroup, long paramLong)
    {
      long l1 = paramVGroup.getNativePtr();
      int i = 0;
      if (i < paramArrayOfPropertyValuesHolder.length)
      {
        paramArrayOfPropertyValuesHolder[i].getPropertyValues(this.mTmpValues);
        int j = VectorDrawable.VGroup.getPropertyIndex(this.mTmpValues.propertyName);
        if ((this.mTmpValues.type != Float.class) && (this.mTmpValues.type != Float.TYPE)) {}
        for (;;)
        {
          i += 1;
          break;
          if (j >= 0)
          {
            long l2 = AnimatedVectorDrawable.-wrap2(l1, j, ((Float)this.mTmpValues.startValue).floatValue(), ((Float)this.mTmpValues.endValue).floatValue());
            if (this.mTmpValues.dataSource != null)
            {
              paramVGroup = createFloatDataPoints(this.mTmpValues.dataSource, paramObjectAnimator.getDuration());
              AnimatedVectorDrawable.-wrap11(l2, paramVGroup, paramVGroup.length);
            }
            createNativeChildAnimator(l2, paramLong, paramObjectAnimator);
          }
        }
      }
    }
    
    private void createRTAnimatorForPath(ObjectAnimator paramObjectAnimator, VectorDrawable.VPath paramVPath, long paramLong)
    {
      createNativeChildAnimator(AnimatedVectorDrawable.-wrap4(paramVPath.getNativePtr(), ((PathParser.PathData)this.mTmpValues.startValue).getNativePtr(), ((PathParser.PathData)this.mTmpValues.endValue).getNativePtr()), paramLong, paramObjectAnimator);
    }
    
    private void createRTAnimatorForRootGroup(PropertyValuesHolder[] paramArrayOfPropertyValuesHolder, ObjectAnimator paramObjectAnimator, VectorDrawable.VectorDrawableState paramVectorDrawableState, long paramLong)
    {
      long l = paramVectorDrawableState.getNativeRenderer();
      if (!paramObjectAnimator.getPropertyName().equals("alpha"))
      {
        if (AnimatedVectorDrawable.AnimatedVectorDrawableState.-get0(AnimatedVectorDrawable.-get0(this.mDrawable))) {
          return;
        }
        throw new UnsupportedOperationException("Only alpha is supported for root group");
      }
      Object localObject2 = null;
      Object localObject3 = null;
      int i = 0;
      Object localObject1;
      for (;;)
      {
        localObject1 = localObject3;
        paramVectorDrawableState = (VectorDrawable.VectorDrawableState)localObject2;
        if (i < paramArrayOfPropertyValuesHolder.length)
        {
          paramArrayOfPropertyValuesHolder[i].getPropertyValues(this.mTmpValues);
          if (this.mTmpValues.propertyName.equals("alpha"))
          {
            paramVectorDrawableState = (Float)this.mTmpValues.startValue;
            localObject1 = (Float)this.mTmpValues.endValue;
          }
        }
        else
        {
          if ((paramVectorDrawableState != null) || (localObject1 != null)) {
            break label160;
          }
          if (!AnimatedVectorDrawable.AnimatedVectorDrawableState.-get0(AnimatedVectorDrawable.-get0(this.mDrawable))) {
            break;
          }
          return;
        }
        i += 1;
      }
      throw new UnsupportedOperationException("No alpha values are specified");
      label160:
      l = AnimatedVectorDrawable.-wrap6(l, paramVectorDrawableState.floatValue(), ((Float)localObject1).floatValue());
      if (this.mTmpValues.dataSource != null)
      {
        paramArrayOfPropertyValuesHolder = createFloatDataPoints(this.mTmpValues.dataSource, paramObjectAnimator.getDuration());
        AnimatedVectorDrawable.-wrap11(l, paramArrayOfPropertyValuesHolder, paramArrayOfPropertyValuesHolder.length);
      }
      createNativeChildAnimator(l, paramLong, paramObjectAnimator);
    }
    
    private void endAnimation()
    {
      AnimatedVectorDrawable.-wrap8(this.mSetPtr);
      invalidateOwningView();
    }
    
    private static int getFrameCount(long paramLong)
    {
      int i = (int)(Choreographer.getInstance().getFrameIntervalNanos() / 1000000L);
      int j = Math.max(2, (int)Math.ceil(paramLong / i));
      i = j;
      if (j > 300)
      {
        Log.w("AnimatedVectorDrawable", "Duration for the animation is too long :" + paramLong + ", the animation will subsample the keyframe or path data.");
        i = 300;
      }
      return i;
    }
    
    private void handlePendingAction(int paramInt)
    {
      if (paramInt == 1)
      {
        startAnimation();
        return;
      }
      if (paramInt == 2)
      {
        reverseAnimation();
        return;
      }
      if (paramInt == 3)
      {
        resetAnimation();
        return;
      }
      if (paramInt == 4)
      {
        endAnimation();
        return;
      }
      throw new UnsupportedOperationException("Animation action " + paramInt + "is not supported");
    }
    
    private void invalidateOwningView()
    {
      this.mDrawable.invalidateSelf();
    }
    
    private void onAnimationEnd(int paramInt)
    {
      if (paramInt != this.mLastListenerId) {
        return;
      }
      this.mStarted = false;
      invalidateOwningView();
      if (this.mListener != null) {
        this.mListener.onAnimationEnd(null);
      }
    }
    
    private void parseAnimatorSet(AnimatorSet paramAnimatorSet, long paramLong)
    {
      ArrayList localArrayList = paramAnimatorSet.getChildAnimations();
      boolean bool = paramAnimatorSet.shouldPlayTogether();
      int i = 0;
      if (i < localArrayList.size())
      {
        paramAnimatorSet = (Animator)localArrayList.get(i);
        if ((paramAnimatorSet instanceof AnimatorSet)) {
          parseAnimatorSet((AnimatorSet)paramAnimatorSet, paramLong);
        }
        for (;;)
        {
          long l = paramLong;
          if (!bool)
          {
            l = paramLong + paramAnimatorSet.getTotalDuration();
            this.mContainsSequentialAnimators = true;
          }
          i += 1;
          paramLong = l;
          break;
          if ((paramAnimatorSet instanceof ObjectAnimator)) {
            createRTAnimator((ObjectAnimator)paramAnimatorSet, paramLong);
          }
        }
      }
    }
    
    private void resetAnimation()
    {
      AnimatedVectorDrawable.-wrap9(this.mSetPtr);
      invalidateOwningView();
    }
    
    private void reverseAnimation()
    {
      this.mStarted = true;
      long l = this.mSetPtr;
      int i = this.mLastListenerId + 1;
      this.mLastListenerId = i;
      AnimatedVectorDrawable.-wrap10(l, this, i);
      invalidateOwningView();
      if (this.mListener != null) {
        this.mListener.onAnimationStart(null);
      }
    }
    
    private void startAnimation()
    {
      this.mStarted = true;
      long l = this.mSetPtr;
      int i = this.mLastListenerId + 1;
      this.mLastListenerId = i;
      AnimatedVectorDrawable.-wrap14(l, this, i);
      invalidateOwningView();
      if (this.mListener != null) {
        this.mListener.onAnimationStart(null);
      }
    }
    
    private void transferPendingActions(AnimatedVectorDrawable.VectorDrawableAnimator paramVectorDrawableAnimator)
    {
      int i = 0;
      if (i < this.mPendingAnimationActions.size())
      {
        int j = this.mPendingAnimationActions.get(i);
        if (j == 1) {
          paramVectorDrawableAnimator.start();
        }
        for (;;)
        {
          i += 1;
          break;
          if (j == 4)
          {
            paramVectorDrawableAnimator.end();
          }
          else if (j == 2)
          {
            paramVectorDrawableAnimator.reverse();
          }
          else
          {
            if (j != 3) {
              break label82;
            }
            paramVectorDrawableAnimator.reset();
          }
        }
        label82:
        throw new UnsupportedOperationException("Animation action " + j + "is not supported");
      }
      this.mPendingAnimationActions.clear();
    }
    
    private boolean useLastSeenTarget()
    {
      if (this.mLastSeenTarget != null) {
        return useTarget((RenderNode)this.mLastSeenTarget.get());
      }
      return false;
    }
    
    private boolean useTarget(RenderNode paramRenderNode)
    {
      if ((paramRenderNode != null) && (paramRenderNode.isAttached()))
      {
        paramRenderNode.registerVectorDrawableAnimator(this);
        return true;
      }
      return false;
    }
    
    public boolean canReverse()
    {
      return this.mIsReversible;
    }
    
    public void end()
    {
      if (!this.mInitialized) {
        return;
      }
      if (useLastSeenTarget())
      {
        endAnimation();
        return;
      }
      addPendingAction(4);
    }
    
    public long getAnimatorNativePtr()
    {
      return this.mSetPtr;
    }
    
    public void init(AnimatorSet paramAnimatorSet)
    {
      if (this.mInitialized) {
        throw new UnsupportedOperationException("VectorDrawableAnimator cannot be re-initialized");
      }
      parseAnimatorSet(paramAnimatorSet, 0L);
      long l = AnimatedVectorDrawable.-get0(this.mDrawable).mVectorDrawable.getNativeTree();
      AnimatedVectorDrawable.-wrap13(this.mSetPtr, l);
      this.mInitialized = true;
      boolean bool;
      if (paramAnimatorSet.getTotalDuration() == -1L)
      {
        bool = true;
        this.mIsInfinite = bool;
        this.mIsReversible = true;
        if (!this.mContainsSequentialAnimators) {
          break label95;
        }
        this.mIsReversible = false;
      }
      for (;;)
      {
        return;
        bool = false;
        break;
        label95:
        int i = 0;
        while (i < this.mStartDelays.size())
        {
          if (this.mStartDelays.get(i) > 0L)
          {
            this.mIsReversible = false;
            return;
          }
          i += 1;
        }
      }
    }
    
    public boolean isInfinite()
    {
      return this.mIsInfinite;
    }
    
    public boolean isRunning()
    {
      if (!this.mInitialized) {
        return false;
      }
      return this.mStarted;
    }
    
    public boolean isStarted()
    {
      return this.mStarted;
    }
    
    public void onDraw(Canvas paramCanvas)
    {
      if (paramCanvas.isHardwareAccelerated()) {
        recordLastSeenTarget((DisplayListCanvas)paramCanvas);
      }
    }
    
    public void pause() {}
    
    protected void recordLastSeenTarget(DisplayListCanvas paramDisplayListCanvas)
    {
      paramDisplayListCanvas = RenderNodeAnimatorSetHelper.getTarget(paramDisplayListCanvas);
      this.mLastSeenTarget = new WeakReference(paramDisplayListCanvas);
      if (((this.mInitialized) || (this.mPendingAnimationActions.size() > 0)) && (useTarget(paramDisplayListCanvas)))
      {
        int i = 0;
        while (i < this.mPendingAnimationActions.size())
        {
          handlePendingAction(this.mPendingAnimationActions.get(i));
          i += 1;
        }
        this.mPendingAnimationActions.clear();
      }
    }
    
    public void removeListener(Animator.AnimatorListener paramAnimatorListener)
    {
      this.mListener = null;
    }
    
    public void reset()
    {
      if (!this.mInitialized) {
        return;
      }
      if (useLastSeenTarget())
      {
        resetAnimation();
        return;
      }
      addPendingAction(3);
    }
    
    public void resume() {}
    
    public void reverse()
    {
      if ((this.mIsReversible) && (this.mInitialized))
      {
        if (useLastSeenTarget()) {
          reverseAnimation();
        }
      }
      else {
        return;
      }
      addPendingAction(2);
    }
    
    public void setListener(Animator.AnimatorListener paramAnimatorListener)
    {
      this.mListener = paramAnimatorListener;
    }
    
    public void start()
    {
      if (!this.mInitialized) {
        return;
      }
      if (useLastSeenTarget())
      {
        startAnimation();
        return;
      }
      addPendingAction(1);
    }
  }
  
  private static class VectorDrawableAnimatorUI
    implements AnimatedVectorDrawable.VectorDrawableAnimator
  {
    private final Drawable mDrawable;
    private boolean mIsInfinite = false;
    private ArrayList<Animator.AnimatorListener> mListenerArray = null;
    private AnimatorSet mSet = null;
    
    VectorDrawableAnimatorUI(AnimatedVectorDrawable paramAnimatedVectorDrawable)
    {
      this.mDrawable = paramAnimatedVectorDrawable;
    }
    
    private void invalidateOwningView()
    {
      this.mDrawable.invalidateSelf();
    }
    
    public boolean canReverse()
    {
      if (this.mSet != null) {
        return this.mSet.canReverse();
      }
      return false;
    }
    
    public void end()
    {
      if (this.mSet == null) {
        return;
      }
      this.mSet.end();
    }
    
    public void init(AnimatorSet paramAnimatorSet)
    {
      if (this.mSet != null) {
        throw new UnsupportedOperationException("VectorDrawableAnimator cannot be re-initialized");
      }
      this.mSet = paramAnimatorSet.clone();
      if (this.mSet.getTotalDuration() == -1L) {}
      for (boolean bool = true;; bool = false)
      {
        this.mIsInfinite = bool;
        if ((this.mListenerArray != null) && (!this.mListenerArray.isEmpty())) {
          break;
        }
        return;
      }
      int i = 0;
      while (i < this.mListenerArray.size())
      {
        this.mSet.addListener((Animator.AnimatorListener)this.mListenerArray.get(i));
        i += 1;
      }
      this.mListenerArray.clear();
      this.mListenerArray = null;
    }
    
    public boolean isInfinite()
    {
      return this.mIsInfinite;
    }
    
    public boolean isRunning()
    {
      if (this.mSet != null) {
        return this.mSet.isRunning();
      }
      return false;
    }
    
    public boolean isStarted()
    {
      if (this.mSet != null) {
        return this.mSet.isStarted();
      }
      return false;
    }
    
    public void onDraw(Canvas paramCanvas)
    {
      if ((this.mSet != null) && (this.mSet.isStarted())) {
        invalidateOwningView();
      }
    }
    
    public void pause()
    {
      if (this.mSet == null) {
        return;
      }
      this.mSet.pause();
    }
    
    public void removeListener(Animator.AnimatorListener paramAnimatorListener)
    {
      if (this.mSet == null)
      {
        if (this.mListenerArray == null) {
          return;
        }
        this.mListenerArray.remove(paramAnimatorListener);
        return;
      }
      this.mSet.removeListener(paramAnimatorListener);
    }
    
    public void reset()
    {
      if (this.mSet == null) {
        return;
      }
      start();
      this.mSet.cancel();
    }
    
    public void resume()
    {
      if (this.mSet == null) {
        return;
      }
      this.mSet.resume();
    }
    
    public void reverse()
    {
      if (this.mSet == null) {
        return;
      }
      this.mSet.reverse();
      invalidateOwningView();
    }
    
    public void setListener(Animator.AnimatorListener paramAnimatorListener)
    {
      if (this.mSet == null)
      {
        if (this.mListenerArray == null) {
          this.mListenerArray = new ArrayList();
        }
        this.mListenerArray.add(paramAnimatorListener);
        return;
      }
      this.mSet.addListener(paramAnimatorListener);
    }
    
    public void start()
    {
      if ((this.mSet == null) || (this.mSet.isStarted())) {
        return;
      }
      this.mSet.start();
      invalidateOwningView();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/AnimatedVectorDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */