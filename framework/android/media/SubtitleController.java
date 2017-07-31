package android.media;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.view.accessibility.CaptioningManager;
import android.view.accessibility.CaptioningManager.CaptioningChangeListener;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

public class SubtitleController
{
  private static final int WHAT_HIDE = 2;
  private static final int WHAT_SELECT_DEFAULT_TRACK = 4;
  private static final int WHAT_SELECT_TRACK = 3;
  private static final int WHAT_SHOW = 1;
  private Anchor mAnchor;
  private final Handler.Callback mCallback = new Handler.Callback()
  {
    public boolean handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return false;
      case 1: 
        SubtitleController.-wrap3(SubtitleController.this);
        return true;
      case 2: 
        SubtitleController.-wrap0(SubtitleController.this);
        return true;
      case 3: 
        SubtitleController.-wrap2(SubtitleController.this, (SubtitleTrack)paramAnonymousMessage.obj);
        return true;
      }
      SubtitleController.-wrap1(SubtitleController.this);
      return true;
    }
  };
  private CaptioningManager.CaptioningChangeListener mCaptioningChangeListener = new CaptioningManager.CaptioningChangeListener()
  {
    public void onEnabledChanged(boolean paramAnonymousBoolean)
    {
      SubtitleController.this.selectDefaultTrack();
    }
    
    public void onLocaleChanged(Locale paramAnonymousLocale)
    {
      SubtitleController.this.selectDefaultTrack();
    }
  };
  private CaptioningManager mCaptioningManager;
  private Handler mHandler;
  private Listener mListener;
  private Vector<Renderer> mRenderers;
  private SubtitleTrack mSelectedTrack;
  private boolean mShowing;
  private MediaTimeProvider mTimeProvider;
  private boolean mTrackIsExplicit = false;
  private Vector<SubtitleTrack> mTracks;
  private boolean mVisibilityIsExplicit = false;
  
  static
  {
    if (SubtitleController.class.desiredAssertionStatus()) {}
    for (boolean bool = false;; bool = true)
    {
      -assertionsDisabled = bool;
      return;
    }
  }
  
  public SubtitleController(Context paramContext, MediaTimeProvider paramMediaTimeProvider, Listener paramListener)
  {
    this.mTimeProvider = paramMediaTimeProvider;
    this.mListener = paramListener;
    this.mRenderers = new Vector();
    this.mShowing = false;
    this.mTracks = new Vector();
    this.mCaptioningManager = ((CaptioningManager)paramContext.getSystemService("captioning"));
  }
  
  private void checkAnchorLooper()
  {
    int j = 1;
    int i;
    if (!-assertionsDisabled)
    {
      if (this.mHandler != null) {}
      for (i = 1; i == 0; i = 0) {
        throw new AssertionError("Should have a looper already");
      }
    }
    if (!-assertionsDisabled)
    {
      if (Looper.myLooper() == this.mHandler.getLooper()) {}
      for (i = j; i == 0; i = 0) {
        throw new AssertionError("Must be called from the anchor's looper");
      }
    }
  }
  
  private void doHide()
  {
    this.mVisibilityIsExplicit = true;
    if (this.mSelectedTrack != null) {
      this.mSelectedTrack.hide();
    }
    this.mShowing = false;
  }
  
  private void doSelectDefaultTrack()
  {
    if (this.mTrackIsExplicit)
    {
      if (!this.mVisibilityIsExplicit)
      {
        if ((!this.mCaptioningManager.isEnabled()) && ((this.mSelectedTrack == null) || (this.mSelectedTrack.getFormat().getInteger("is-forced-subtitle", 0) == 0))) {
          break label57;
        }
        show();
      }
      for (;;)
      {
        this.mVisibilityIsExplicit = false;
        return;
        label57:
        if ((this.mSelectedTrack != null) && (this.mSelectedTrack.getTrackType() == 4)) {
          hide();
        }
      }
    }
    SubtitleTrack localSubtitleTrack = getDefaultTrack();
    if (localSubtitleTrack != null)
    {
      selectTrack(localSubtitleTrack);
      this.mTrackIsExplicit = false;
      if (!this.mVisibilityIsExplicit)
      {
        show();
        this.mVisibilityIsExplicit = false;
      }
    }
  }
  
  private void doSelectTrack(SubtitleTrack paramSubtitleTrack)
  {
    this.mTrackIsExplicit = true;
    if (this.mSelectedTrack == paramSubtitleTrack) {
      return;
    }
    if (this.mSelectedTrack != null)
    {
      this.mSelectedTrack.hide();
      this.mSelectedTrack.setTimeProvider(null);
    }
    this.mSelectedTrack = paramSubtitleTrack;
    if (this.mAnchor != null) {
      this.mAnchor.setSubtitleWidget(getRenderingWidget());
    }
    if (this.mSelectedTrack != null)
    {
      this.mSelectedTrack.setTimeProvider(this.mTimeProvider);
      this.mSelectedTrack.show();
    }
    if (this.mListener != null) {
      this.mListener.onSubtitleTrackSelected(paramSubtitleTrack);
    }
  }
  
  private void doShow()
  {
    this.mShowing = true;
    this.mVisibilityIsExplicit = true;
    if (this.mSelectedTrack != null) {
      this.mSelectedTrack.show();
    }
  }
  
  private SubtitleTrack.RenderingWidget getRenderingWidget()
  {
    if (this.mSelectedTrack == null) {
      return null;
    }
    return this.mSelectedTrack.getRenderingWidget();
  }
  
  private void processOnAnchor(Message paramMessage)
  {
    if (!-assertionsDisabled)
    {
      if (this.mHandler != null) {}
      for (int i = 1; i == 0; i = 0) {
        throw new AssertionError("Should have a looper already");
      }
    }
    if (Looper.myLooper() == this.mHandler.getLooper())
    {
      this.mHandler.dispatchMessage(paramMessage);
      return;
    }
    this.mHandler.sendMessage(paramMessage);
  }
  
  public SubtitleTrack addTrack(MediaFormat arg1)
  {
    synchronized (this.mRenderers)
    {
      Iterator localIterator = this.mRenderers.iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = (Renderer)localIterator.next();
        if (((Renderer)localObject2).supports(???))
        {
          localObject2 = ((Renderer)localObject2).createTrack(???);
          if (localObject2 != null) {
            synchronized (this.mTracks)
            {
              if (this.mTracks.size() == 0) {
                this.mCaptioningManager.addCaptioningChangeListener(this.mCaptioningChangeListener);
              }
              this.mTracks.add(localObject2);
              return (SubtitleTrack)localObject2;
            }
          }
        }
      }
    }
    return null;
  }
  
  protected void finalize()
    throws Throwable
  {
    this.mCaptioningManager.removeCaptioningChangeListener(this.mCaptioningChangeListener);
    super.finalize();
  }
  
  public SubtitleTrack getDefaultTrack()
  {
    Object localObject2 = null;
    int j = -1;
    Locale localLocale2 = this.mCaptioningManager.getLocale();
    Locale localLocale1 = localLocale2;
    if (localLocale2 == null) {
      localLocale1 = Locale.getDefault();
    }
    int i;
    if (this.mCaptioningManager.isEnabled()) {
      i = 0;
    }
    synchronized (this.mTracks)
    {
      Iterator localIterator = this.mTracks.iterator();
      label60:
      if (localIterator.hasNext())
      {
        SubtitleTrack localSubtitleTrack = (SubtitleTrack)localIterator.next();
        MediaFormat localMediaFormat = localSubtitleTrack.getFormat();
        String str = localMediaFormat.getString("language");
        int k;
        label112:
        int m;
        label127:
        int n;
        label142:
        boolean bool;
        label186:
        int i1;
        label193:
        int i2;
        label206:
        int i3;
        if (localMediaFormat.getInteger("is-forced-subtitle", 0) != 0)
        {
          k = 1;
          if (localMediaFormat.getInteger("is-autoselect", 1) == 0) {
            break label298;
          }
          m = 1;
          if (localMediaFormat.getInteger("is-default", 0) == 0) {
            break label304;
          }
          n = 1;
          if ((localLocale1 == null) || (localLocale1.getLanguage().equals("")) || (localLocale1.getISO3Language().equals(str))) {
            break label310;
          }
          bool = localLocale1.getLanguage().equals(str);
          if (k == 0) {
            break label316;
          }
          i1 = 0;
          if ((localLocale2 != null) || (n == 0)) {
            break label323;
          }
          i2 = 4;
          if (m == 0) {
            break label329;
          }
          i3 = 0;
          label214:
          if (!bool) {
            break label335;
          }
        }
        label298:
        label304:
        label310:
        label316:
        label323:
        label329:
        label335:
        for (int i4 = 1;; i4 = 0)
        {
          i1 = i1 + i2 + i3 + i4;
          if (((i != 0) && (k == 0)) || (((localLocale2 != null) || (n == 0)) && ((!bool) || ((m == 0) && (k == 0) && (localLocale2 == null)) || (i1 <= j)))) {
            break label60;
          }
          j = i1;
          localObject2 = localSubtitleTrack;
          break label60;
          i = 1;
          break;
          k = 0;
          break label112;
          m = 0;
          break label127;
          n = 0;
          break label142;
          bool = true;
          break label186;
          i1 = 8;
          break label193;
          i2 = 0;
          break label206;
          i3 = 2;
          break label214;
        }
      }
      return (SubtitleTrack)localObject2;
    }
  }
  
  public SubtitleTrack getSelectedTrack()
  {
    return this.mSelectedTrack;
  }
  
  public SubtitleTrack[] getTracks()
  {
    synchronized (this.mTracks)
    {
      SubtitleTrack[] arrayOfSubtitleTrack = new SubtitleTrack[this.mTracks.size()];
      this.mTracks.toArray(arrayOfSubtitleTrack);
      return arrayOfSubtitleTrack;
    }
  }
  
  public boolean hasRendererFor(MediaFormat paramMediaFormat)
  {
    synchronized (this.mRenderers)
    {
      Iterator localIterator = this.mRenderers.iterator();
      while (localIterator.hasNext())
      {
        boolean bool = ((Renderer)localIterator.next()).supports(paramMediaFormat);
        if (bool) {
          return true;
        }
      }
      return false;
    }
  }
  
  public void hide()
  {
    processOnAnchor(this.mHandler.obtainMessage(2));
  }
  
  public void registerRenderer(Renderer paramRenderer)
  {
    synchronized (this.mRenderers)
    {
      if (!this.mRenderers.contains(paramRenderer)) {
        this.mRenderers.add(paramRenderer);
      }
      return;
    }
  }
  
  public void reset()
  {
    checkAnchorLooper();
    hide();
    selectTrack(null);
    this.mTracks.clear();
    this.mTrackIsExplicit = false;
    this.mVisibilityIsExplicit = false;
    this.mCaptioningManager.removeCaptioningChangeListener(this.mCaptioningChangeListener);
  }
  
  public void selectDefaultTrack()
  {
    processOnAnchor(this.mHandler.obtainMessage(4));
  }
  
  public boolean selectTrack(SubtitleTrack paramSubtitleTrack)
  {
    if ((paramSubtitleTrack == null) || (this.mTracks.contains(paramSubtitleTrack)))
    {
      processOnAnchor(this.mHandler.obtainMessage(3, paramSubtitleTrack));
      return true;
    }
    return false;
  }
  
  public void setAnchor(Anchor paramAnchor)
  {
    if (this.mAnchor == paramAnchor) {
      return;
    }
    if (this.mAnchor != null)
    {
      checkAnchorLooper();
      this.mAnchor.setSubtitleWidget(null);
    }
    this.mAnchor = paramAnchor;
    this.mHandler = null;
    if (this.mAnchor != null)
    {
      this.mHandler = new Handler(this.mAnchor.getSubtitleLooper(), this.mCallback);
      checkAnchorLooper();
      this.mAnchor.setSubtitleWidget(getRenderingWidget());
    }
  }
  
  public void show()
  {
    processOnAnchor(this.mHandler.obtainMessage(1));
  }
  
  public static abstract interface Anchor
  {
    public abstract Looper getSubtitleLooper();
    
    public abstract void setSubtitleWidget(SubtitleTrack.RenderingWidget paramRenderingWidget);
  }
  
  public static abstract interface Listener
  {
    public abstract void onSubtitleTrackSelected(SubtitleTrack paramSubtitleTrack);
  }
  
  public static abstract class Renderer
  {
    public abstract SubtitleTrack createTrack(MediaFormat paramMediaFormat);
    
    public abstract boolean supports(MediaFormat paramMediaFormat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/SubtitleController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */