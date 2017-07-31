package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class LevelListDrawable
  extends DrawableContainer
{
  private LevelListState mLevelListState;
  private boolean mMutated;
  
  public LevelListDrawable()
  {
    this(null, null);
  }
  
  private LevelListDrawable(LevelListState paramLevelListState, Resources paramResources)
  {
    setConstantState(new LevelListState(paramLevelListState, this, paramResources));
    onLevelChange(getLevel());
  }
  
  private void inflateChildElements(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth() + 1;
    do
    {
      j = paramXmlPullParser.next();
      if (j == 1) {
        break;
      }
      k = paramXmlPullParser.getDepth();
      if ((k < i) && (j == 3)) {
        break;
      }
    } while ((j != 2) || (k > i) || (!paramXmlPullParser.getName().equals("item")));
    Object localObject = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.LevelListDrawableItem);
    int j = ((TypedArray)localObject).getInt(1, 0);
    int k = ((TypedArray)localObject).getInt(2, 0);
    int m = ((TypedArray)localObject).getResourceId(0, 0);
    ((TypedArray)localObject).recycle();
    if (k < 0) {
      throw new XmlPullParserException(paramXmlPullParser.getPositionDescription() + ": <item> tag requires a 'maxLevel' attribute");
    }
    if (m != 0) {}
    for (localObject = paramResources.getDrawable(m, paramTheme);; localObject = Drawable.createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme))
    {
      this.mLevelListState.addLevel(j, k, (Drawable)localObject);
      break;
      do
      {
        m = paramXmlPullParser.next();
      } while (m == 4);
      if (m != 2) {
        throw new XmlPullParserException(paramXmlPullParser.getPositionDescription() + ": <item> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
      }
    }
    onLevelChange(getLevel());
  }
  
  public void addLevel(int paramInt1, int paramInt2, Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      this.mLevelListState.addLevel(paramInt1, paramInt2, paramDrawable);
      onLevelChange(getLevel());
    }
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    this.mMutated = false;
  }
  
  LevelListState cloneConstantState()
  {
    return new LevelListState(this.mLevelListState, this, null);
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    updateDensity(paramResources);
    inflateChildElements(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      LevelListState.-wrap0(this.mLevelListState);
      this.mMutated = true;
    }
    return this;
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    if (selectDrawable(this.mLevelListState.indexOfLevel(paramInt))) {
      return true;
    }
    return super.onLevelChange(paramInt);
  }
  
  protected void setConstantState(DrawableContainer.DrawableContainerState paramDrawableContainerState)
  {
    super.setConstantState(paramDrawableContainerState);
    if ((paramDrawableContainerState instanceof LevelListState)) {
      this.mLevelListState = ((LevelListState)paramDrawableContainerState);
    }
  }
  
  private static final class LevelListState
    extends DrawableContainer.DrawableContainerState
  {
    private int[] mHighs;
    private int[] mLows;
    
    LevelListState(LevelListState paramLevelListState, LevelListDrawable paramLevelListDrawable, Resources paramResources)
    {
      super(paramLevelListDrawable, paramResources);
      if (paramLevelListState != null)
      {
        this.mLows = paramLevelListState.mLows;
        this.mHighs = paramLevelListState.mHighs;
        return;
      }
      this.mLows = new int[getCapacity()];
      this.mHighs = new int[getCapacity()];
    }
    
    private void mutate()
    {
      this.mLows = ((int[])this.mLows.clone());
      this.mHighs = ((int[])this.mHighs.clone());
    }
    
    public void addLevel(int paramInt1, int paramInt2, Drawable paramDrawable)
    {
      int i = addChild(paramDrawable);
      this.mLows[i] = paramInt1;
      this.mHighs[i] = paramInt2;
    }
    
    public void growArray(int paramInt1, int paramInt2)
    {
      super.growArray(paramInt1, paramInt2);
      int[] arrayOfInt = new int[paramInt2];
      System.arraycopy(this.mLows, 0, arrayOfInt, 0, paramInt1);
      this.mLows = arrayOfInt;
      arrayOfInt = new int[paramInt2];
      System.arraycopy(this.mHighs, 0, arrayOfInt, 0, paramInt1);
      this.mHighs = arrayOfInt;
    }
    
    public int indexOfLevel(int paramInt)
    {
      int[] arrayOfInt1 = this.mLows;
      int[] arrayOfInt2 = this.mHighs;
      int j = getChildCount();
      int i = 0;
      while (i < j)
      {
        if ((paramInt >= arrayOfInt1[i]) && (paramInt <= arrayOfInt2[i])) {
          return i;
        }
        i += 1;
      }
      return -1;
    }
    
    public Drawable newDrawable()
    {
      return new LevelListDrawable(this, null, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new LevelListDrawable(this, paramResources, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/LevelListDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */