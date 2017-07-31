package android.app.assist;

import android.app.Activity;
import android.content.ComponentName;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.BadParcelableException;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PooledStringReader;
import android.os.PooledStringWriter;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.ViewStructure;
import android.view.WindowManagerGlobal;
import java.util.ArrayList;

public class AssistStructure
  implements Parcelable
{
  public static final Parcelable.Creator<AssistStructure> CREATOR = new Parcelable.Creator()
  {
    public AssistStructure createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AssistStructure(paramAnonymousParcel);
    }
    
    public AssistStructure[] newArray(int paramAnonymousInt)
    {
      return new AssistStructure[paramAnonymousInt];
    }
  };
  static final boolean DEBUG_PARCEL = false;
  static final boolean DEBUG_PARCEL_CHILDREN = false;
  static final boolean DEBUG_PARCEL_TREE = false;
  static final String DESCRIPTOR = "android.app.AssistStructure";
  static final String TAG = "AssistStructure";
  static final int TRANSACTION_XFER = 2;
  static final int VALIDATE_VIEW_TOKEN = 572662306;
  static final int VALIDATE_WINDOW_TOKEN = 286331153;
  ComponentName mActivityComponent;
  boolean mHaveData;
  final ArrayList<ViewNodeBuilder> mPendingAsyncChildren = new ArrayList();
  IBinder mReceiveChannel;
  SendChannel mSendChannel;
  Rect mTmpRect = new Rect();
  final ArrayList<WindowNode> mWindowNodes = new ArrayList();
  
  public AssistStructure()
  {
    this.mHaveData = true;
    this.mActivityComponent = null;
  }
  
  public AssistStructure(Activity paramActivity)
  {
    this.mHaveData = true;
    this.mActivityComponent = paramActivity.getComponentName();
    paramActivity = WindowManagerGlobal.getInstance().getRootViews(paramActivity.getActivityToken());
    int i = 0;
    while (i < paramActivity.size())
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)paramActivity.get(i);
      this.mWindowNodes.add(new WindowNode(this, localViewRootImpl));
      i += 1;
    }
  }
  
  public AssistStructure(Parcel paramParcel)
  {
    this.mReceiveChannel = paramParcel.readStrongBinder();
  }
  
  public void clearSendChannel()
  {
    if (this.mSendChannel != null) {
      this.mSendChannel.mAssistStructure = null;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump()
  {
    Log.i("AssistStructure", "Activity: " + this.mActivityComponent.flattenToShortString());
    int j = getWindowNodeCount();
    int i = 0;
    while (i < j)
    {
      WindowNode localWindowNode = getWindowNodeAt(i);
      Log.i("AssistStructure", "Window #" + i + " [" + localWindowNode.getLeft() + "," + localWindowNode.getTop() + " " + localWindowNode.getWidth() + "x" + localWindowNode.getHeight() + "]" + " " + localWindowNode.getTitle());
      dump("  ", localWindowNode.getRootViewNode());
      i += 1;
    }
  }
  
  void dump(String paramString, ViewNode paramViewNode)
  {
    Log.i("AssistStructure", paramString + "View [" + paramViewNode.getLeft() + "," + paramViewNode.getTop() + " " + paramViewNode.getWidth() + "x" + paramViewNode.getHeight() + "]" + " " + paramViewNode.getClassName());
    int i = paramViewNode.getId();
    if (i != 0)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append("  ID: #");
      ((StringBuilder)localObject).append(Integer.toHexString(i));
      String str1 = paramViewNode.getIdEntry();
      if (str1 != null)
      {
        String str2 = paramViewNode.getIdType();
        String str3 = paramViewNode.getIdPackage();
        ((StringBuilder)localObject).append(" ");
        ((StringBuilder)localObject).append(str3);
        ((StringBuilder)localObject).append(":");
        ((StringBuilder)localObject).append(str2);
        ((StringBuilder)localObject).append("/");
        ((StringBuilder)localObject).append(str1);
      }
      Log.i("AssistStructure", ((StringBuilder)localObject).toString());
    }
    i = paramViewNode.getScrollX();
    int j = paramViewNode.getScrollY();
    if ((i != 0) || (j != 0)) {
      Log.i("AssistStructure", paramString + "  Scroll: " + i + "," + j);
    }
    Object localObject = paramViewNode.getTransformation();
    if (localObject != null) {
      Log.i("AssistStructure", paramString + "  Transformation: " + localObject);
    }
    float f = paramViewNode.getElevation();
    if (f != 0.0F) {
      Log.i("AssistStructure", paramString + "  Elevation: " + f);
    }
    if (paramViewNode.getAlpha() != 0.0F) {
      Log.i("AssistStructure", paramString + "  Alpha: " + f);
    }
    localObject = paramViewNode.getContentDescription();
    if (localObject != null) {
      Log.i("AssistStructure", paramString + "  Content description: " + (CharSequence)localObject);
    }
    localObject = paramViewNode.getText();
    if (localObject != null)
    {
      Log.i("AssistStructure", paramString + "  Text (sel " + paramViewNode.getTextSelectionStart() + "-" + paramViewNode.getTextSelectionEnd() + "): " + (CharSequence)localObject);
      Log.i("AssistStructure", paramString + "  Text size: " + paramViewNode.getTextSize() + " , style: #" + paramViewNode.getTextStyle());
      Log.i("AssistStructure", paramString + "  Text color fg: #" + Integer.toHexString(paramViewNode.getTextColor()) + ", bg: #" + Integer.toHexString(paramViewNode.getTextBackgroundColor()));
    }
    localObject = paramViewNode.getHint();
    if (localObject != null) {
      Log.i("AssistStructure", paramString + "  Hint: " + (String)localObject);
    }
    localObject = paramViewNode.getExtras();
    if (localObject != null) {
      Log.i("AssistStructure", paramString + "  Extras: " + localObject);
    }
    if (paramViewNode.isAssistBlocked()) {
      Log.i("AssistStructure", paramString + "  BLOCKED");
    }
    j = paramViewNode.getChildCount();
    if (j > 0)
    {
      Log.i("AssistStructure", paramString + "  Children:");
      paramString = paramString + "    ";
      i = 0;
      while (i < j)
      {
        dump(paramString, paramViewNode.getChildAt(i));
        i += 1;
      }
    }
  }
  
  public void ensureData()
  {
    if (this.mHaveData) {
      return;
    }
    this.mHaveData = true;
    new ParcelTransferReader(this.mReceiveChannel).go();
  }
  
  public ComponentName getActivityComponent()
  {
    ensureData();
    return this.mActivityComponent;
  }
  
  public WindowNode getWindowNodeAt(int paramInt)
  {
    ensureData();
    return (WindowNode)this.mWindowNodes.get(paramInt);
  }
  
  public int getWindowNodeCount()
  {
    ensureData();
    return this.mWindowNodes.size();
  }
  
  boolean waitForReady()
  {
    int i = 0;
    try
    {
      long l1 = SystemClock.uptimeMillis() + 5000L;
      while (this.mPendingAsyncChildren.size() > 0)
      {
        long l2 = SystemClock.uptimeMillis();
        if (l2 >= l1) {
          break;
        }
        try
        {
          wait(l1 - l2);
        }
        catch (InterruptedException localInterruptedException) {}
      }
      if (this.mPendingAsyncChildren.size() > 0)
      {
        Log.w("AssistStructure", "Skipping assist structure, waiting too long for async children (have " + this.mPendingAsyncChildren.size() + " remaining");
        i = 1;
      }
      if (i != 0) {
        return false;
      }
    }
    finally {}
    return true;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mHaveData)
    {
      if (this.mSendChannel == null) {
        this.mSendChannel = new SendChannel(this);
      }
      paramParcel.writeStrongBinder(this.mSendChannel);
      return;
    }
    paramParcel.writeStrongBinder(this.mReceiveChannel);
  }
  
  final class ParcelTransferReader
  {
    private final IBinder mChannel;
    private Parcel mCurParcel;
    int mNumReadViews;
    int mNumReadWindows;
    PooledStringReader mStringReader;
    final float[] mTmpMatrix = new float[9];
    private IBinder mTransferToken;
    
    ParcelTransferReader(IBinder paramIBinder)
    {
      this.mChannel = paramIBinder;
    }
    
    private void fetchData()
    {
      Parcel localParcel = Parcel.obtain();
      localParcel.writeInterfaceToken("android.app.AssistStructure");
      localParcel.writeStrongBinder(this.mTransferToken);
      if (this.mCurParcel != null) {
        this.mCurParcel.recycle();
      }
      this.mCurParcel = Parcel.obtain();
      try
      {
        this.mChannel.transact(2, localParcel, this.mCurParcel, 0);
        localParcel.recycle();
        this.mNumReadViews = 0;
        this.mNumReadWindows = 0;
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("AssistStructure", "Failure reading AssistStructure data", localRemoteException);
        throw new IllegalStateException("Failure reading AssistStructure data: " + localRemoteException);
      }
    }
    
    void go()
    {
      fetchData();
      AssistStructure.this.mActivityComponent = ComponentName.readFromParcel(this.mCurParcel);
      int j = this.mCurParcel.readInt();
      if (j > 0)
      {
        this.mStringReader = new PooledStringReader(this.mCurParcel);
        int i = 0;
        while (i < j)
        {
          AssistStructure.this.mWindowNodes.add(new AssistStructure.WindowNode(this));
          i += 1;
        }
      }
    }
    
    Parcel readParcel(int paramInt1, int paramInt2)
    {
      paramInt2 = this.mCurParcel.readInt();
      if (paramInt2 != 0)
      {
        if (paramInt2 != paramInt1) {
          throw new BadParcelableException("Got token " + Integer.toHexString(paramInt2) + ", expected token " + Integer.toHexString(paramInt1));
        }
        return this.mCurParcel;
      }
      this.mTransferToken = this.mCurParcel.readStrongBinder();
      if (this.mTransferToken == null) {
        throw new IllegalStateException("Reached end of partial data without transfer token");
      }
      fetchData();
      this.mStringReader = new PooledStringReader(this.mCurParcel);
      this.mCurParcel.readInt();
      return this.mCurParcel;
    }
  }
  
  static final class ParcelTransferWriter
    extends Binder
  {
    AssistStructure.ViewStackEntry mCurViewStackEntry;
    int mCurViewStackPos;
    int mCurWindow;
    int mNumWindows;
    int mNumWrittenViews;
    int mNumWrittenWindows;
    final float[] mTmpMatrix = new float[9];
    final ArrayList<AssistStructure.ViewStackEntry> mViewStack = new ArrayList();
    final boolean mWriteStructure;
    
    ParcelTransferWriter(AssistStructure paramAssistStructure, Parcel paramParcel)
    {
      this.mWriteStructure = paramAssistStructure.waitForReady();
      ComponentName.writeToParcel(paramAssistStructure.mActivityComponent, paramParcel);
      this.mNumWindows = paramAssistStructure.mWindowNodes.size();
      if ((this.mWriteStructure) && (this.mNumWindows > 0))
      {
        paramParcel.writeInt(this.mNumWindows);
        return;
      }
      paramParcel.writeInt(0);
    }
    
    void pushViewStackEntry(AssistStructure.ViewNode paramViewNode, int paramInt)
    {
      AssistStructure.ViewStackEntry localViewStackEntry;
      if (paramInt >= this.mViewStack.size())
      {
        localViewStackEntry = new AssistStructure.ViewStackEntry();
        this.mViewStack.add(localViewStackEntry);
      }
      for (;;)
      {
        localViewStackEntry.node = paramViewNode;
        localViewStackEntry.numChildren = paramViewNode.getChildCount();
        localViewStackEntry.curChild = 0;
        this.mCurViewStackEntry = localViewStackEntry;
        return;
        localViewStackEntry = (AssistStructure.ViewStackEntry)this.mViewStack.get(paramInt);
      }
    }
    
    boolean writeNextEntryToParcel(AssistStructure paramAssistStructure, Parcel paramParcel, PooledStringWriter paramPooledStringWriter)
    {
      if (this.mCurViewStackEntry != null)
      {
        if (this.mCurViewStackEntry.curChild < this.mCurViewStackEntry.numChildren)
        {
          paramAssistStructure = this.mCurViewStackEntry.node.mChildren[this.mCurViewStackEntry.curChild];
          AssistStructure.ViewStackEntry localViewStackEntry = this.mCurViewStackEntry;
          localViewStackEntry.curChild += 1;
          writeView(paramAssistStructure, paramParcel, paramPooledStringWriter, 1);
          return true;
        }
        do
        {
          this.mCurViewStackEntry = ((AssistStructure.ViewStackEntry)this.mViewStack.get(i));
          if (this.mCurViewStackEntry.curChild < this.mCurViewStackEntry.numChildren) {
            break;
          }
          i = this.mCurViewStackPos - 1;
          this.mCurViewStackPos = i;
        } while (i >= 0);
        this.mCurViewStackEntry = null;
        return true;
      }
      int i = this.mCurWindow;
      if (i < this.mNumWindows)
      {
        paramAssistStructure = (AssistStructure.WindowNode)paramAssistStructure.mWindowNodes.get(i);
        this.mCurWindow += 1;
        paramParcel.writeInt(286331153);
        paramAssistStructure.writeSelfToParcel(paramParcel, paramPooledStringWriter, this.mTmpMatrix);
        this.mNumWrittenWindows += 1;
        paramAssistStructure = paramAssistStructure.mRoot;
        this.mCurViewStackPos = 0;
        writeView(paramAssistStructure, paramParcel, paramPooledStringWriter, 0);
        return true;
      }
      return false;
    }
    
    void writeToParcel(AssistStructure paramAssistStructure, Parcel paramParcel)
    {
      int i = paramParcel.dataPosition();
      this.mNumWrittenWindows = 0;
      this.mNumWrittenViews = 0;
      boolean bool = writeToParcelInner(paramAssistStructure, paramParcel);
      StringBuilder localStringBuilder = new StringBuilder().append("Flattened ");
      if (bool) {}
      for (paramAssistStructure = "partial";; paramAssistStructure = "final")
      {
        Log.i("AssistStructure", paramAssistStructure + " assist data: " + (paramParcel.dataPosition() - i) + " bytes, containing " + this.mNumWrittenWindows + " windows, " + this.mNumWrittenViews + " views");
        return;
      }
    }
    
    boolean writeToParcelInner(AssistStructure paramAssistStructure, Parcel paramParcel)
    {
      if (this.mNumWindows == 0) {
        return false;
      }
      PooledStringWriter localPooledStringWriter = new PooledStringWriter(paramParcel);
      while (writeNextEntryToParcel(paramAssistStructure, paramParcel, localPooledStringWriter)) {
        if (paramParcel.dataSize() > 65536)
        {
          paramParcel.writeInt(0);
          paramParcel.writeStrongBinder(this);
          localPooledStringWriter.finish();
          return true;
        }
      }
      localPooledStringWriter.finish();
      this.mViewStack.clear();
      return false;
    }
    
    void writeView(AssistStructure.ViewNode paramViewNode, Parcel paramParcel, PooledStringWriter paramPooledStringWriter, int paramInt)
    {
      paramParcel.writeInt(572662306);
      paramInt = paramViewNode.writeSelfToParcel(paramParcel, paramPooledStringWriter, this.mTmpMatrix);
      this.mNumWrittenViews += 1;
      if ((0x100000 & paramInt) != 0)
      {
        paramParcel.writeInt(paramViewNode.mChildren.length);
        paramInt = this.mCurViewStackPos + 1;
        this.mCurViewStackPos = paramInt;
        pushViewStackEntry(paramViewNode, paramInt);
      }
    }
  }
  
  static final class SendChannel
    extends Binder
  {
    volatile AssistStructure mAssistStructure;
    
    SendChannel(AssistStructure paramAssistStructure)
    {
      this.mAssistStructure = paramAssistStructure;
    }
    
    protected boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      if (paramInt1 == 2)
      {
        AssistStructure localAssistStructure = this.mAssistStructure;
        if (localAssistStructure == null) {
          return true;
        }
        paramParcel1.enforceInterface("android.app.AssistStructure");
        paramParcel1 = paramParcel1.readStrongBinder();
        if (paramParcel1 != null)
        {
          if ((paramParcel1 instanceof AssistStructure.ParcelTransferWriter))
          {
            paramParcel1.writeToParcel(localAssistStructure, paramParcel2);
            return true;
          }
          Log.w("AssistStructure", "Caller supplied bad token type: " + paramParcel1);
          return true;
        }
        new AssistStructure.ParcelTransferWriter(localAssistStructure, paramParcel2).writeToParcel(localAssistStructure, paramParcel2);
        return true;
      }
      return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
    }
  }
  
  public static class ViewNode
  {
    static final int FLAGS_ACCESSIBILITY_FOCUSED = 4096;
    static final int FLAGS_ACTIVATED = 8192;
    static final int FLAGS_ALL_CONTROL = -1048576;
    static final int FLAGS_ASSIST_BLOCKED = 128;
    static final int FLAGS_CHECKABLE = 256;
    static final int FLAGS_CHECKED = 512;
    static final int FLAGS_CLICKABLE = 1024;
    static final int FLAGS_CONTEXT_CLICKABLE = 16384;
    static final int FLAGS_DISABLED = 1;
    static final int FLAGS_FOCUSABLE = 16;
    static final int FLAGS_FOCUSED = 32;
    static final int FLAGS_HAS_ALPHA = 536870912;
    static final int FLAGS_HAS_CHILDREN = 1048576;
    static final int FLAGS_HAS_COMPLEX_TEXT = 8388608;
    static final int FLAGS_HAS_CONTENT_DESCRIPTION = 33554432;
    static final int FLAGS_HAS_ELEVATION = 268435456;
    static final int FLAGS_HAS_EXTRAS = 4194304;
    static final int FLAGS_HAS_ID = 2097152;
    static final int FLAGS_HAS_LARGE_COORDS = 67108864;
    static final int FLAGS_HAS_MATRIX = 1073741824;
    static final int FLAGS_HAS_SCROLL = 134217728;
    static final int FLAGS_HAS_TEXT = 16777216;
    static final int FLAGS_LONG_CLICKABLE = 2048;
    static final int FLAGS_SELECTED = 64;
    static final int FLAGS_VISIBILITY_MASK = 12;
    public static final int TEXT_COLOR_UNDEFINED = 1;
    public static final int TEXT_STYLE_BOLD = 1;
    public static final int TEXT_STYLE_ITALIC = 2;
    public static final int TEXT_STYLE_STRIKE_THRU = 8;
    public static final int TEXT_STYLE_UNDERLINE = 4;
    float mAlpha = 1.0F;
    ViewNode[] mChildren;
    String mClassName;
    CharSequence mContentDescription;
    float mElevation;
    Bundle mExtras;
    int mFlags;
    int mHeight;
    int mId = -1;
    String mIdEntry;
    String mIdPackage;
    String mIdType;
    Matrix mMatrix;
    int mScrollX;
    int mScrollY;
    AssistStructure.ViewNodeText mText;
    int mWidth;
    int mX;
    int mY;
    
    ViewNode() {}
    
    ViewNode(AssistStructure.ParcelTransferReader paramParcelTransferReader, int paramInt)
    {
      Parcel localParcel = paramParcelTransferReader.readParcel(572662306, paramInt);
      paramParcelTransferReader.mNumReadViews += 1;
      PooledStringReader localPooledStringReader = paramParcelTransferReader.mStringReader;
      this.mClassName = localPooledStringReader.readString();
      this.mFlags = localParcel.readInt();
      int i = this.mFlags;
      if ((0x200000 & i) != 0)
      {
        this.mId = localParcel.readInt();
        if (this.mId != 0)
        {
          this.mIdEntry = localPooledStringReader.readString();
          if (this.mIdEntry != null)
          {
            this.mIdType = localPooledStringReader.readString();
            this.mIdPackage = localPooledStringReader.readString();
          }
        }
      }
      if ((0x4000000 & i) != 0)
      {
        this.mX = localParcel.readInt();
        this.mY = localParcel.readInt();
        this.mWidth = localParcel.readInt();
        this.mHeight = localParcel.readInt();
        if ((0x8000000 & i) != 0)
        {
          this.mScrollX = localParcel.readInt();
          this.mScrollY = localParcel.readInt();
        }
        if ((0x40000000 & i) != 0)
        {
          this.mMatrix = new Matrix();
          localParcel.readFloatArray(paramParcelTransferReader.mTmpMatrix);
          this.mMatrix.setValues(paramParcelTransferReader.mTmpMatrix);
        }
        if ((0x10000000 & i) != 0) {
          this.mElevation = localParcel.readFloat();
        }
        if ((0x20000000 & i) != 0) {
          this.mAlpha = localParcel.readFloat();
        }
        if ((0x2000000 & i) != 0) {
          this.mContentDescription = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(localParcel));
        }
        if ((0x1000000 & i) != 0) {
          if ((0x800000 & i) != 0) {
            break label447;
          }
        }
      }
      label447:
      for (boolean bool = true;; bool = false)
      {
        this.mText = new AssistStructure.ViewNodeText(localParcel, bool);
        if ((0x400000 & i) != 0) {
          this.mExtras = localParcel.readBundle();
        }
        if ((0x100000 & i) == 0) {
          return;
        }
        int j = localParcel.readInt();
        this.mChildren = new ViewNode[j];
        i = 0;
        while (i < j)
        {
          this.mChildren[i] = new ViewNode(paramParcelTransferReader, paramInt + 1);
          i += 1;
        }
        j = localParcel.readInt();
        this.mX = (j & 0x7FFF);
        this.mY = (j >> 16 & 0x7FFF);
        j = localParcel.readInt();
        this.mWidth = (j & 0x7FFF);
        this.mHeight = (j >> 16 & 0x7FFF);
        break;
      }
    }
    
    public float getAlpha()
    {
      return this.mAlpha;
    }
    
    public ViewNode getChildAt(int paramInt)
    {
      return this.mChildren[paramInt];
    }
    
    public int getChildCount()
    {
      if (this.mChildren != null) {
        return this.mChildren.length;
      }
      return 0;
    }
    
    public String getClassName()
    {
      return this.mClassName;
    }
    
    public CharSequence getContentDescription()
    {
      return this.mContentDescription;
    }
    
    public float getElevation()
    {
      return this.mElevation;
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public int getHeight()
    {
      return this.mHeight;
    }
    
    public String getHint()
    {
      String str = null;
      if (this.mText != null) {
        str = this.mText.mHint;
      }
      return str;
    }
    
    public int getId()
    {
      return this.mId;
    }
    
    public String getIdEntry()
    {
      return this.mIdEntry;
    }
    
    public String getIdPackage()
    {
      return this.mIdPackage;
    }
    
    public String getIdType()
    {
      return this.mIdType;
    }
    
    public int getLeft()
    {
      return this.mX;
    }
    
    public int getScrollX()
    {
      return this.mScrollX;
    }
    
    public int getScrollY()
    {
      return this.mScrollY;
    }
    
    public CharSequence getText()
    {
      CharSequence localCharSequence = null;
      if (this.mText != null) {
        localCharSequence = this.mText.mText;
      }
      return localCharSequence;
    }
    
    public int getTextBackgroundColor()
    {
      if (this.mText != null) {
        return this.mText.mTextBackgroundColor;
      }
      return 1;
    }
    
    public int getTextColor()
    {
      if (this.mText != null) {
        return this.mText.mTextColor;
      }
      return 1;
    }
    
    public int[] getTextLineBaselines()
    {
      int[] arrayOfInt = null;
      if (this.mText != null) {
        arrayOfInt = this.mText.mLineBaselines;
      }
      return arrayOfInt;
    }
    
    public int[] getTextLineCharOffsets()
    {
      int[] arrayOfInt = null;
      if (this.mText != null) {
        arrayOfInt = this.mText.mLineCharOffsets;
      }
      return arrayOfInt;
    }
    
    public int getTextSelectionEnd()
    {
      if (this.mText != null) {
        return this.mText.mTextSelectionEnd;
      }
      return -1;
    }
    
    public int getTextSelectionStart()
    {
      if (this.mText != null) {
        return this.mText.mTextSelectionStart;
      }
      return -1;
    }
    
    public float getTextSize()
    {
      if (this.mText != null) {
        return this.mText.mTextSize;
      }
      return 0.0F;
    }
    
    public int getTextStyle()
    {
      if (this.mText != null) {
        return this.mText.mTextStyle;
      }
      return 0;
    }
    
    public int getTop()
    {
      return this.mY;
    }
    
    public Matrix getTransformation()
    {
      return this.mMatrix;
    }
    
    public int getVisibility()
    {
      return this.mFlags & 0xC;
    }
    
    public int getWidth()
    {
      return this.mWidth;
    }
    
    public boolean isAccessibilityFocused()
    {
      boolean bool = false;
      if ((this.mFlags & 0x1000) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isActivated()
    {
      boolean bool = false;
      if ((this.mFlags & 0x2000) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isAssistBlocked()
    {
      boolean bool = false;
      if ((this.mFlags & 0x80) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isCheckable()
    {
      boolean bool = false;
      if ((this.mFlags & 0x100) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isChecked()
    {
      boolean bool = false;
      if ((this.mFlags & 0x200) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isClickable()
    {
      boolean bool = false;
      if ((this.mFlags & 0x400) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isContextClickable()
    {
      boolean bool = false;
      if ((this.mFlags & 0x4000) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isEnabled()
    {
      boolean bool = false;
      if ((this.mFlags & 0x1) == 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isFocusable()
    {
      boolean bool = false;
      if ((this.mFlags & 0x10) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isFocused()
    {
      boolean bool = false;
      if ((this.mFlags & 0x20) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isLongClickable()
    {
      boolean bool = false;
      if ((this.mFlags & 0x800) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isSelected()
    {
      boolean bool = false;
      if ((this.mFlags & 0x40) != 0) {
        bool = true;
      }
      return bool;
    }
    
    int writeSelfToParcel(Parcel paramParcel, PooledStringWriter paramPooledStringWriter, float[] paramArrayOfFloat)
    {
      boolean bool = true;
      int j = this.mFlags & 0xFFFFF;
      int i = j;
      if (this.mId != -1) {
        i = j | 0x200000;
      }
      label61:
      int k;
      if (((this.mX & 0x8000) != 0) || ((this.mY & 0x8000) != 0))
      {
        j = i | 0x4000000;
        if (this.mScrollX == 0)
        {
          i = j;
          if (this.mScrollY == 0) {}
        }
        else
        {
          i = j | 0x8000000;
        }
        j = i;
        if (this.mMatrix != null) {
          j = i | 0x40000000;
        }
        i = j;
        if (this.mElevation != 0.0F) {
          i = j | 0x10000000;
        }
        j = i;
        if (this.mAlpha != 1.0F) {
          j = i | 0x20000000;
        }
        k = j;
        if (this.mContentDescription != null) {
          k = j | 0x2000000;
        }
        i = k;
        if (this.mText != null)
        {
          j = k | 0x1000000;
          i = j;
          if (!this.mText.isSimple()) {
            i = j | 0x800000;
          }
        }
        j = i;
        if (this.mExtras != null) {
          j = i | 0x400000;
        }
        i = j;
        if (this.mChildren != null) {
          i = j | 0x100000;
        }
        paramPooledStringWriter.writeString(this.mClassName);
        paramParcel.writeInt(i);
        if ((i & 0x200000) != 0)
        {
          paramParcel.writeInt(this.mId);
          if (this.mId != 0)
          {
            paramPooledStringWriter.writeString(this.mIdEntry);
            if (this.mIdEntry != null)
            {
              paramPooledStringWriter.writeString(this.mIdType);
              paramPooledStringWriter.writeString(this.mIdPackage);
            }
          }
        }
        if ((0x4000000 & i) == 0) {
          break label541;
        }
        paramParcel.writeInt(this.mX);
        paramParcel.writeInt(this.mY);
        paramParcel.writeInt(this.mWidth);
        paramParcel.writeInt(this.mHeight);
        label345:
        if ((0x8000000 & i) != 0)
        {
          paramParcel.writeInt(this.mScrollX);
          paramParcel.writeInt(this.mScrollY);
        }
        if ((0x40000000 & i) != 0)
        {
          this.mMatrix.getValues(paramArrayOfFloat);
          paramParcel.writeFloatArray(paramArrayOfFloat);
        }
        if ((0x10000000 & i) != 0) {
          paramParcel.writeFloat(this.mElevation);
        }
        if ((0x20000000 & i) != 0) {
          paramParcel.writeFloat(this.mAlpha);
        }
        if ((0x2000000 & i) != 0) {
          TextUtils.writeToParcel(this.mContentDescription, paramParcel, 0);
        }
        if ((0x1000000 & i) != 0)
        {
          paramPooledStringWriter = this.mText;
          if ((0x800000 & i) != 0) {
            break label576;
          }
        }
      }
      for (;;)
      {
        paramPooledStringWriter.writeToParcel(paramParcel, bool);
        if ((i & 0x400000) != 0) {
          paramParcel.writeBundle(this.mExtras);
        }
        return i;
        if ((this.mWidth & 0x8000) != 0)
        {
          k = 1;
          label500:
          if ((this.mHeight & 0x8000) == 0) {
            break label535;
          }
        }
        label535:
        for (int m = 1;; m = 0)
        {
          j = i;
          if ((k | m) == 0) {
            break label61;
          }
          break;
          k = 0;
          break label500;
        }
        label541:
        paramParcel.writeInt(this.mY << 16 | this.mX);
        paramParcel.writeInt(this.mHeight << 16 | this.mWidth);
        break label345;
        label576:
        bool = false;
      }
    }
  }
  
  static class ViewNodeBuilder
    extends ViewStructure
  {
    final AssistStructure mAssist;
    final boolean mAsync;
    final AssistStructure.ViewNode mNode;
    
    ViewNodeBuilder(AssistStructure paramAssistStructure, AssistStructure.ViewNode paramViewNode, boolean paramBoolean)
    {
      this.mAssist = paramAssistStructure;
      this.mNode = paramViewNode;
      this.mAsync = paramBoolean;
    }
    
    private final AssistStructure.ViewNodeText getNodeText()
    {
      if (this.mNode.mText != null) {
        return this.mNode.mText;
      }
      this.mNode.mText = new AssistStructure.ViewNodeText();
      return this.mNode.mText;
    }
    
    public int addChildCount(int paramInt)
    {
      if (this.mNode.mChildren == null)
      {
        setChildCount(paramInt);
        return 0;
      }
      int i = this.mNode.mChildren.length;
      AssistStructure.ViewNode[] arrayOfViewNode = new AssistStructure.ViewNode[i + paramInt];
      System.arraycopy(this.mNode.mChildren, 0, arrayOfViewNode, 0, i);
      this.mNode.mChildren = arrayOfViewNode;
      return i;
    }
    
    public void asyncCommit()
    {
      synchronized (this.mAssist)
      {
        if (!this.mAsync) {
          throw new IllegalStateException("Child " + this + " was not created with ViewStructure.asyncNewChild");
        }
      }
      if (!this.mAssist.mPendingAsyncChildren.remove(this)) {
        throw new IllegalStateException("Child " + this + " already committed");
      }
      this.mAssist.notifyAll();
    }
    
    public ViewStructure asyncNewChild(int paramInt)
    {
      synchronized (this.mAssist)
      {
        Object localObject1 = new AssistStructure.ViewNode();
        this.mNode.mChildren[paramInt] = localObject1;
        localObject1 = new ViewNodeBuilder(this.mAssist, (AssistStructure.ViewNode)localObject1, true);
        this.mAssist.mPendingAsyncChildren.add(localObject1);
        return (ViewStructure)localObject1;
      }
    }
    
    public int getChildCount()
    {
      if (this.mNode.mChildren != null) {
        return this.mNode.mChildren.length;
      }
      return 0;
    }
    
    public Bundle getExtras()
    {
      if (this.mNode.mExtras != null) {
        return this.mNode.mExtras;
      }
      this.mNode.mExtras = new Bundle();
      return this.mNode.mExtras;
    }
    
    public CharSequence getHint()
    {
      String str = null;
      if (this.mNode.mText != null) {
        str = this.mNode.mText.mHint;
      }
      return str;
    }
    
    public Rect getTempRect()
    {
      return this.mAssist.mTmpRect;
    }
    
    public CharSequence getText()
    {
      CharSequence localCharSequence = null;
      if (this.mNode.mText != null) {
        localCharSequence = this.mNode.mText.mText;
      }
      return localCharSequence;
    }
    
    public int getTextSelectionEnd()
    {
      if (this.mNode.mText != null) {
        return this.mNode.mText.mTextSelectionEnd;
      }
      return -1;
    }
    
    public int getTextSelectionStart()
    {
      if (this.mNode.mText != null) {
        return this.mNode.mText.mTextSelectionStart;
      }
      return -1;
    }
    
    public boolean hasExtras()
    {
      return this.mNode.mExtras != null;
    }
    
    public ViewStructure newChild(int paramInt)
    {
      AssistStructure.ViewNode localViewNode = new AssistStructure.ViewNode();
      this.mNode.mChildren[paramInt] = localViewNode;
      return new ViewNodeBuilder(this.mAssist, localViewNode, false);
    }
    
    public void setAccessibilityFocused(boolean paramBoolean)
    {
      AssistStructure.ViewNode localViewNode = this.mNode;
      int j = this.mNode.mFlags;
      if (paramBoolean) {}
      for (int i = 4096;; i = 0)
      {
        localViewNode.mFlags = (i | j & 0xEFFF);
        return;
      }
    }
    
    public void setActivated(boolean paramBoolean)
    {
      AssistStructure.ViewNode localViewNode = this.mNode;
      int j = this.mNode.mFlags;
      if (paramBoolean) {}
      for (int i = 8192;; i = 0)
      {
        localViewNode.mFlags = (i | j & 0xDFFF);
        return;
      }
    }
    
    public void setAlpha(float paramFloat)
    {
      this.mNode.mAlpha = paramFloat;
    }
    
    public void setAssistBlocked(boolean paramBoolean)
    {
      AssistStructure.ViewNode localViewNode = this.mNode;
      int j = this.mNode.mFlags;
      if (paramBoolean) {}
      for (int i = 128;; i = 0)
      {
        localViewNode.mFlags = (i | j & 0xFF7F);
        return;
      }
    }
    
    public void setCheckable(boolean paramBoolean)
    {
      AssistStructure.ViewNode localViewNode = this.mNode;
      int j = this.mNode.mFlags;
      if (paramBoolean) {}
      for (int i = 256;; i = 0)
      {
        localViewNode.mFlags = (i | j & 0xFEFF);
        return;
      }
    }
    
    public void setChecked(boolean paramBoolean)
    {
      AssistStructure.ViewNode localViewNode = this.mNode;
      int j = this.mNode.mFlags;
      if (paramBoolean) {}
      for (int i = 512;; i = 0)
      {
        localViewNode.mFlags = (i | j & 0xFDFF);
        return;
      }
    }
    
    public void setChildCount(int paramInt)
    {
      this.mNode.mChildren = new AssistStructure.ViewNode[paramInt];
    }
    
    public void setClassName(String paramString)
    {
      this.mNode.mClassName = paramString;
    }
    
    public void setClickable(boolean paramBoolean)
    {
      AssistStructure.ViewNode localViewNode = this.mNode;
      int j = this.mNode.mFlags;
      if (paramBoolean) {}
      for (int i = 1024;; i = 0)
      {
        localViewNode.mFlags = (i | j & 0xFBFF);
        return;
      }
    }
    
    public void setContentDescription(CharSequence paramCharSequence)
    {
      this.mNode.mContentDescription = paramCharSequence;
    }
    
    public void setContextClickable(boolean paramBoolean)
    {
      AssistStructure.ViewNode localViewNode = this.mNode;
      int j = this.mNode.mFlags;
      if (paramBoolean) {}
      for (int i = 16384;; i = 0)
      {
        localViewNode.mFlags = (i | j & 0xBFFF);
        return;
      }
    }
    
    public void setDimens(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      this.mNode.mX = paramInt1;
      this.mNode.mY = paramInt2;
      this.mNode.mScrollX = paramInt3;
      this.mNode.mScrollY = paramInt4;
      this.mNode.mWidth = paramInt5;
      this.mNode.mHeight = paramInt6;
    }
    
    public void setElevation(float paramFloat)
    {
      this.mNode.mElevation = paramFloat;
    }
    
    public void setEnabled(boolean paramBoolean)
    {
      AssistStructure.ViewNode localViewNode = this.mNode;
      int j = this.mNode.mFlags;
      if (paramBoolean) {}
      for (int i = 0;; i = 1)
      {
        localViewNode.mFlags = (i | j & 0xFFFFFFFE);
        return;
      }
    }
    
    public void setFocusable(boolean paramBoolean)
    {
      AssistStructure.ViewNode localViewNode = this.mNode;
      int j = this.mNode.mFlags;
      if (paramBoolean) {}
      for (int i = 16;; i = 0)
      {
        localViewNode.mFlags = (i | j & 0xFFFFFFEF);
        return;
      }
    }
    
    public void setFocused(boolean paramBoolean)
    {
      AssistStructure.ViewNode localViewNode = this.mNode;
      int j = this.mNode.mFlags;
      if (paramBoolean) {}
      for (int i = 32;; i = 0)
      {
        localViewNode.mFlags = (i | j & 0xFFFFFFDF);
        return;
      }
    }
    
    public void setHint(CharSequence paramCharSequence)
    {
      String str = null;
      AssistStructure.ViewNodeText localViewNodeText = getNodeText();
      if (paramCharSequence != null) {
        str = paramCharSequence.toString();
      }
      localViewNodeText.mHint = str;
    }
    
    public void setId(int paramInt, String paramString1, String paramString2, String paramString3)
    {
      this.mNode.mId = paramInt;
      this.mNode.mIdPackage = paramString1;
      this.mNode.mIdType = paramString2;
      this.mNode.mIdEntry = paramString3;
    }
    
    public void setLongClickable(boolean paramBoolean)
    {
      AssistStructure.ViewNode localViewNode = this.mNode;
      int j = this.mNode.mFlags;
      if (paramBoolean) {}
      for (int i = 2048;; i = 0)
      {
        localViewNode.mFlags = (i | j & 0xF7FF);
        return;
      }
    }
    
    public void setSelected(boolean paramBoolean)
    {
      AssistStructure.ViewNode localViewNode = this.mNode;
      int j = this.mNode.mFlags;
      if (paramBoolean) {}
      for (int i = 64;; i = 0)
      {
        localViewNode.mFlags = (i | j & 0xFFFFFFBF);
        return;
      }
    }
    
    public void setText(CharSequence paramCharSequence)
    {
      AssistStructure.ViewNodeText localViewNodeText = getNodeText();
      localViewNodeText.mText = paramCharSequence;
      localViewNodeText.mTextSelectionEnd = -1;
      localViewNodeText.mTextSelectionStart = -1;
    }
    
    public void setText(CharSequence paramCharSequence, int paramInt1, int paramInt2)
    {
      AssistStructure.ViewNodeText localViewNodeText = getNodeText();
      localViewNodeText.mText = paramCharSequence;
      localViewNodeText.mTextSelectionStart = paramInt1;
      localViewNodeText.mTextSelectionEnd = paramInt2;
    }
    
    public void setTextLines(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      AssistStructure.ViewNodeText localViewNodeText = getNodeText();
      localViewNodeText.mLineCharOffsets = paramArrayOfInt1;
      localViewNodeText.mLineBaselines = paramArrayOfInt2;
    }
    
    public void setTextStyle(float paramFloat, int paramInt1, int paramInt2, int paramInt3)
    {
      AssistStructure.ViewNodeText localViewNodeText = getNodeText();
      localViewNodeText.mTextColor = paramInt1;
      localViewNodeText.mTextBackgroundColor = paramInt2;
      localViewNodeText.mTextSize = paramFloat;
      localViewNodeText.mTextStyle = paramInt3;
    }
    
    public void setTransformation(Matrix paramMatrix)
    {
      if (paramMatrix == null)
      {
        this.mNode.mMatrix = null;
        return;
      }
      this.mNode.mMatrix = new Matrix(paramMatrix);
    }
    
    public void setVisibility(int paramInt)
    {
      this.mNode.mFlags = (this.mNode.mFlags & 0xFFFFFFF3 | paramInt);
    }
  }
  
  static final class ViewNodeText
  {
    String mHint;
    int[] mLineBaselines;
    int[] mLineCharOffsets;
    CharSequence mText;
    int mTextBackgroundColor = 1;
    int mTextColor = 1;
    int mTextSelectionEnd;
    int mTextSelectionStart;
    float mTextSize;
    int mTextStyle;
    
    ViewNodeText() {}
    
    ViewNodeText(Parcel paramParcel, boolean paramBoolean)
    {
      this.mText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.mTextSize = paramParcel.readFloat();
      this.mTextStyle = paramParcel.readInt();
      this.mTextColor = paramParcel.readInt();
      if (!paramBoolean)
      {
        this.mTextBackgroundColor = paramParcel.readInt();
        this.mTextSelectionStart = paramParcel.readInt();
        this.mTextSelectionEnd = paramParcel.readInt();
        this.mLineCharOffsets = paramParcel.createIntArray();
        this.mLineBaselines = paramParcel.createIntArray();
        this.mHint = paramParcel.readString();
      }
    }
    
    boolean isSimple()
    {
      if ((this.mTextBackgroundColor == 1) && (this.mTextSelectionStart == 0) && (this.mTextSelectionEnd == 0) && (this.mLineCharOffsets == null) && (this.mLineBaselines == null)) {
        return this.mHint == null;
      }
      return false;
    }
    
    void writeToParcel(Parcel paramParcel, boolean paramBoolean)
    {
      TextUtils.writeToParcel(this.mText, paramParcel, 0);
      paramParcel.writeFloat(this.mTextSize);
      paramParcel.writeInt(this.mTextStyle);
      paramParcel.writeInt(this.mTextColor);
      if (!paramBoolean)
      {
        paramParcel.writeInt(this.mTextBackgroundColor);
        paramParcel.writeInt(this.mTextSelectionStart);
        paramParcel.writeInt(this.mTextSelectionEnd);
        paramParcel.writeIntArray(this.mLineCharOffsets);
        paramParcel.writeIntArray(this.mLineBaselines);
        paramParcel.writeString(this.mHint);
      }
    }
  }
  
  static final class ViewStackEntry
  {
    int curChild;
    AssistStructure.ViewNode node;
    int numChildren;
  }
  
  public static class WindowNode
  {
    final int mDisplayId;
    final int mHeight;
    final AssistStructure.ViewNode mRoot;
    final CharSequence mTitle;
    final int mWidth;
    final int mX;
    final int mY;
    
    WindowNode(AssistStructure.ParcelTransferReader paramParcelTransferReader)
    {
      Parcel localParcel = paramParcelTransferReader.readParcel(286331153, 0);
      paramParcelTransferReader.mNumReadWindows += 1;
      this.mX = localParcel.readInt();
      this.mY = localParcel.readInt();
      this.mWidth = localParcel.readInt();
      this.mHeight = localParcel.readInt();
      this.mTitle = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(localParcel));
      this.mDisplayId = localParcel.readInt();
      this.mRoot = new AssistStructure.ViewNode(paramParcelTransferReader, 0);
    }
    
    WindowNode(AssistStructure paramAssistStructure, ViewRootImpl paramViewRootImpl)
    {
      View localView = paramViewRootImpl.getView();
      Rect localRect = new Rect();
      localView.getBoundsOnScreen(localRect);
      this.mX = (localRect.left - localView.getLeft());
      this.mY = (localRect.top - localView.getTop());
      this.mWidth = localRect.width();
      this.mHeight = localRect.height();
      this.mTitle = paramViewRootImpl.getTitle();
      this.mDisplayId = paramViewRootImpl.getDisplayId();
      this.mRoot = new AssistStructure.ViewNode();
      paramAssistStructure = new AssistStructure.ViewNodeBuilder(paramAssistStructure, this.mRoot, false);
      if ((paramViewRootImpl.getWindowFlags() & 0x2000) != 0)
      {
        localView.onProvideStructure(paramAssistStructure);
        paramAssistStructure.setAssistBlocked(true);
        return;
      }
      localView.dispatchProvideStructure(paramAssistStructure);
    }
    
    public int getDisplayId()
    {
      return this.mDisplayId;
    }
    
    public int getHeight()
    {
      return this.mHeight;
    }
    
    public int getLeft()
    {
      return this.mX;
    }
    
    public AssistStructure.ViewNode getRootViewNode()
    {
      return this.mRoot;
    }
    
    public CharSequence getTitle()
    {
      return this.mTitle;
    }
    
    public int getTop()
    {
      return this.mY;
    }
    
    public int getWidth()
    {
      return this.mWidth;
    }
    
    void writeSelfToParcel(Parcel paramParcel, PooledStringWriter paramPooledStringWriter, float[] paramArrayOfFloat)
    {
      paramParcel.writeInt(this.mX);
      paramParcel.writeInt(this.mY);
      paramParcel.writeInt(this.mWidth);
      paramParcel.writeInt(this.mHeight);
      TextUtils.writeToParcel(this.mTitle, paramParcel, 0);
      paramParcel.writeInt(this.mDisplayId);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/assist/AssistStructure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */