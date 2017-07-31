package android.content;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.ArrayMap;
import java.util.ArrayList;

public class UndoManager
{
  public static final int MERGE_MODE_ANY = 2;
  public static final int MERGE_MODE_NONE = 0;
  public static final int MERGE_MODE_UNIQUE = 1;
  private int mCommitId = 1;
  private int mHistorySize = 20;
  private boolean mInUndo;
  private boolean mMerged;
  private int mNextSavedIdx;
  private final ArrayMap<String, UndoOwner> mOwners = new ArrayMap(1);
  private final ArrayList<UndoState> mRedos = new ArrayList();
  private UndoOwner[] mStateOwners;
  private int mStateSeq;
  private final ArrayList<UndoState> mUndos = new ArrayList();
  private int mUpdateCount;
  private UndoState mWorking;
  
  private void createWorkingState()
  {
    int i = this.mCommitId;
    this.mCommitId = (i + 1);
    this.mWorking = new UndoState(this, i);
    if (this.mCommitId < 0) {
      this.mCommitId = 1;
    }
  }
  
  private void pushWorkingState()
  {
    int i = this.mUndos.size() + 1;
    if (this.mWorking.hasData())
    {
      this.mUndos.add(this.mWorking);
      forgetRedos(null, -1);
      this.mWorking.commit();
      if (i >= 2) {
        ((UndoState)this.mUndos.get(i - 2)).makeExecuted();
      }
    }
    for (;;)
    {
      this.mWorking = null;
      if ((this.mHistorySize >= 0) && (i > this.mHistorySize)) {
        forgetUndos(null, i - this.mHistorySize);
      }
      return;
      this.mWorking.destroy();
    }
  }
  
  public void addOperation(UndoOperation<?> paramUndoOperation, int paramInt)
  {
    if (this.mWorking == null) {
      throw new IllegalStateException("Must be called during an update");
    }
    if (paramUndoOperation.getOwner().mManager != this) {
      throw new IllegalArgumentException("Given operation's owner is not in this undo manager.");
    }
    if ((paramInt == 0) || (this.mMerged)) {}
    for (;;)
    {
      this.mWorking.addOperation(paramUndoOperation);
      return;
      if (!this.mWorking.hasData())
      {
        UndoState localUndoState = getTopUndo(null);
        if ((localUndoState != null) && ((paramInt == 2) || (!localUndoState.hasMultipleOwners())) && (localUndoState.canMerge()) && (localUndoState.hasOperation(paramUndoOperation.getOwner())))
        {
          this.mWorking.destroy();
          this.mWorking = localUndoState;
          this.mUndos.remove(localUndoState);
          this.mMerged = true;
        }
      }
    }
  }
  
  public void beginUpdate(CharSequence paramCharSequence)
  {
    if (this.mInUndo) {
      throw new IllegalStateException("Can't being update while performing undo/redo");
    }
    if (this.mUpdateCount <= 0)
    {
      createWorkingState();
      this.mMerged = false;
      this.mUpdateCount = 0;
    }
    this.mWorking.updateLabel(paramCharSequence);
    this.mUpdateCount += 1;
  }
  
  public int commitState(UndoOwner paramUndoOwner)
  {
    if ((this.mWorking != null) && (this.mWorking.hasData()))
    {
      if ((paramUndoOwner == null) || (this.mWorking.hasOperation(paramUndoOwner)))
      {
        this.mWorking.setCanMerge(false);
        int i = this.mWorking.getCommitId();
        pushWorkingState();
        createWorkingState();
        this.mMerged = true;
        return i;
      }
    }
    else
    {
      UndoState localUndoState = getTopUndo(null);
      if ((localUndoState != null) && ((paramUndoOwner == null) || (localUndoState.hasOperation(paramUndoOwner))))
      {
        localUndoState.setCanMerge(false);
        return localUndoState.getCommitId();
      }
    }
    return -1;
  }
  
  public int countRedos(UndoOwner[] paramArrayOfUndoOwner)
  {
    if (paramArrayOfUndoOwner == null) {
      return this.mRedos.size();
    }
    int i = 0;
    int j = 0;
    for (;;)
    {
      j = findNextState(this.mRedos, paramArrayOfUndoOwner, j);
      if (j < 0) {
        break;
      }
      i += 1;
      j += 1;
    }
    return i;
  }
  
  public int countUndos(UndoOwner[] paramArrayOfUndoOwner)
  {
    if (paramArrayOfUndoOwner == null) {
      return this.mUndos.size();
    }
    int i = 0;
    int j = 0;
    for (;;)
    {
      j = findNextState(this.mUndos, paramArrayOfUndoOwner, j);
      if (j < 0) {
        break;
      }
      i += 1;
      j += 1;
    }
    return i;
  }
  
  public void endUpdate()
  {
    if (this.mWorking == null) {
      throw new IllegalStateException("Must be called during an update");
    }
    this.mUpdateCount -= 1;
    if (this.mUpdateCount == 0) {
      pushWorkingState();
    }
  }
  
  int findNextState(ArrayList<UndoState> paramArrayList, UndoOwner[] paramArrayOfUndoOwner, int paramInt)
  {
    int j = paramArrayList.size();
    int i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    if (i >= j) {
      return -1;
    }
    paramInt = i;
    if (paramArrayOfUndoOwner == null) {
      return i;
    }
    do
    {
      paramInt += 1;
      if (paramInt >= j) {
        break;
      }
    } while (!matchOwners((UndoState)paramArrayList.get(paramInt), paramArrayOfUndoOwner));
    return paramInt;
    return -1;
  }
  
  int findPrevState(ArrayList<UndoState> paramArrayList, UndoOwner[] paramArrayOfUndoOwner, int paramInt)
  {
    int j = paramArrayList.size();
    int i = paramInt;
    if (paramInt == -1) {
      i = j - 1;
    }
    if (i >= j) {
      return -1;
    }
    paramInt = i;
    if (paramArrayOfUndoOwner == null) {
      return i;
    }
    do
    {
      paramInt -= 1;
      if (paramInt < 0) {
        break;
      }
    } while (!matchOwners((UndoState)paramArrayList.get(paramInt), paramArrayOfUndoOwner));
    return paramInt;
    return -1;
  }
  
  public int forgetRedos(UndoOwner[] paramArrayOfUndoOwner, int paramInt)
  {
    int i = paramInt;
    if (paramInt < 0) {
      i = this.mRedos.size();
    }
    paramInt = 0;
    int j = 0;
    while ((j < this.mRedos.size()) && (paramInt < i))
    {
      UndoState localUndoState = (UndoState)this.mRedos.get(j);
      if ((i > 0) && (matchOwners(localUndoState, paramArrayOfUndoOwner)))
      {
        localUndoState.destroy();
        this.mRedos.remove(j);
        paramInt += 1;
      }
      else
      {
        j += 1;
      }
    }
    return paramInt;
  }
  
  public int forgetUndos(UndoOwner[] paramArrayOfUndoOwner, int paramInt)
  {
    int i = paramInt;
    if (paramInt < 0) {
      i = this.mUndos.size();
    }
    paramInt = 0;
    int j = 0;
    while ((j < this.mUndos.size()) && (paramInt < i))
    {
      UndoState localUndoState = (UndoState)this.mUndos.get(j);
      if ((i > 0) && (matchOwners(localUndoState, paramArrayOfUndoOwner)))
      {
        localUndoState.destroy();
        this.mUndos.remove(j);
        paramInt += 1;
      }
      else
      {
        j += 1;
      }
    }
    return paramInt;
  }
  
  public int getHistorySize()
  {
    return this.mHistorySize;
  }
  
  public UndoOperation<?> getLastOperation(int paramInt)
  {
    return getLastOperation(null, null, paramInt);
  }
  
  public UndoOperation<?> getLastOperation(UndoOwner paramUndoOwner, int paramInt)
  {
    return getLastOperation(null, paramUndoOwner, paramInt);
  }
  
  public <T extends UndoOperation> T getLastOperation(Class<T> paramClass, UndoOwner paramUndoOwner, int paramInt)
  {
    if (this.mWorking == null) {
      throw new IllegalStateException("Must be called during an update");
    }
    if ((paramInt == 0) || (this.mMerged)) {}
    UndoState localUndoState;
    UndoOperation localUndoOperation;
    do
    {
      do
      {
        do
        {
          return this.mWorking.getLastOperation(paramClass, paramUndoOwner);
        } while (this.mWorking.hasData());
        localUndoState = getTopUndo(null);
      } while ((localUndoState == null) || ((paramInt != 2) && (localUndoState.hasMultipleOwners())) || (!localUndoState.canMerge()));
      localUndoOperation = localUndoState.getLastOperation(paramClass, paramUndoOwner);
    } while ((localUndoOperation == null) || (!localUndoOperation.allowMerge()));
    this.mWorking.destroy();
    this.mWorking = localUndoState;
    this.mUndos.remove(localUndoState);
    this.mMerged = true;
    return localUndoOperation;
  }
  
  public UndoOwner getOwner(String paramString, Object paramObject)
  {
    if (paramString == null) {
      throw new NullPointerException("tag can't be null");
    }
    if (paramObject == null) {
      throw new NullPointerException("data can't be null");
    }
    UndoOwner localUndoOwner = (UndoOwner)this.mOwners.get(paramString);
    if (localUndoOwner != null)
    {
      if (localUndoOwner.mData != paramObject)
      {
        if (localUndoOwner.mData != null) {
          throw new IllegalStateException("Owner " + localUndoOwner + " already exists with data " + localUndoOwner.mData + " but giving different data " + paramObject);
        }
        localUndoOwner.mData = paramObject;
      }
      return localUndoOwner;
    }
    localUndoOwner = new UndoOwner(paramString, this);
    localUndoOwner.mData = paramObject;
    this.mOwners.put(paramString, localUndoOwner);
    return localUndoOwner;
  }
  
  public CharSequence getRedoLabel(UndoOwner[] paramArrayOfUndoOwner)
  {
    Object localObject = null;
    UndoState localUndoState = getTopRedo(paramArrayOfUndoOwner);
    paramArrayOfUndoOwner = (UndoOwner[])localObject;
    if (localUndoState != null) {
      paramArrayOfUndoOwner = localUndoState.getLabel();
    }
    return paramArrayOfUndoOwner;
  }
  
  UndoState getTopRedo(UndoOwner[] paramArrayOfUndoOwner)
  {
    Object localObject = null;
    if (this.mRedos.size() <= 0) {
      return null;
    }
    int i = findPrevState(this.mRedos, paramArrayOfUndoOwner, -1);
    paramArrayOfUndoOwner = (UndoOwner[])localObject;
    if (i >= 0) {
      paramArrayOfUndoOwner = (UndoState)this.mRedos.get(i);
    }
    return paramArrayOfUndoOwner;
  }
  
  UndoState getTopUndo(UndoOwner[] paramArrayOfUndoOwner)
  {
    Object localObject = null;
    if (this.mUndos.size() <= 0) {
      return null;
    }
    int i = findPrevState(this.mUndos, paramArrayOfUndoOwner, -1);
    paramArrayOfUndoOwner = (UndoOwner[])localObject;
    if (i >= 0) {
      paramArrayOfUndoOwner = (UndoState)this.mUndos.get(i);
    }
    return paramArrayOfUndoOwner;
  }
  
  public CharSequence getUndoLabel(UndoOwner[] paramArrayOfUndoOwner)
  {
    Object localObject = null;
    UndoState localUndoState = getTopUndo(paramArrayOfUndoOwner);
    paramArrayOfUndoOwner = (UndoOwner[])localObject;
    if (localUndoState != null) {
      paramArrayOfUndoOwner = localUndoState.getLabel();
    }
    return paramArrayOfUndoOwner;
  }
  
  public int getUpdateNestingLevel()
  {
    return this.mUpdateCount;
  }
  
  public boolean hasOperation(UndoOwner paramUndoOwner)
  {
    if (this.mWorking == null) {
      throw new IllegalStateException("Must be called during an update");
    }
    return this.mWorking.hasOperation(paramUndoOwner);
  }
  
  public boolean isInUndo()
  {
    return this.mInUndo;
  }
  
  public boolean isInUpdate()
  {
    boolean bool = false;
    if (this.mUpdateCount > 0) {
      bool = true;
    }
    return bool;
  }
  
  boolean matchOwners(UndoState paramUndoState, UndoOwner[] paramArrayOfUndoOwner)
  {
    if (paramArrayOfUndoOwner == null) {
      return true;
    }
    int i = 0;
    while (i < paramArrayOfUndoOwner.length)
    {
      if (paramUndoState.matchOwner(paramArrayOfUndoOwner[i])) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public int redo(UndoOwner[] paramArrayOfUndoOwner, int paramInt)
  {
    if (this.mWorking != null) {
      throw new IllegalStateException("Can't be called during an update");
    }
    int k = 0;
    int j = -1;
    this.mInUndo = true;
    int i = paramInt;
    paramInt = k;
    while (i > 0)
    {
      j = findPrevState(this.mRedos, paramArrayOfUndoOwner, j);
      if (j < 0) {
        break;
      }
      UndoState localUndoState = (UndoState)this.mRedos.remove(j);
      localUndoState.redo();
      this.mUndos.add(localUndoState);
      i -= 1;
      paramInt += 1;
    }
    this.mInUndo = false;
    return paramInt;
  }
  
  void removeOwner(UndoOwner paramUndoOwner) {}
  
  public void restoreInstanceState(Parcel paramParcel, ClassLoader paramClassLoader)
  {
    if (this.mUpdateCount > 0) {
      throw new IllegalStateException("Can't save state while updating");
    }
    forgetUndos(null, -1);
    forgetRedos(null, -1);
    this.mHistorySize = paramParcel.readInt();
    this.mStateOwners = new UndoOwner[paramParcel.readInt()];
    for (;;)
    {
      int i = paramParcel.readInt();
      if (i == 0) {
        break;
      }
      UndoState localUndoState = new UndoState(this, paramParcel, paramClassLoader);
      if (i == 1) {
        this.mUndos.add(0, localUndoState);
      } else {
        this.mRedos.add(0, localUndoState);
      }
    }
  }
  
  UndoOwner restoreOwner(Parcel paramParcel)
  {
    int i = paramParcel.readInt();
    Object localObject2 = this.mStateOwners[i];
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject2 = paramParcel.readString();
      int j = paramParcel.readInt();
      localObject1 = new UndoOwner((String)localObject2, this);
      ((UndoOwner)localObject1).mOpCount = j;
      this.mStateOwners[i] = localObject1;
      this.mOwners.put(localObject2, localObject1);
    }
    return (UndoOwner)localObject1;
  }
  
  public void saveInstanceState(Parcel paramParcel)
  {
    if (this.mUpdateCount > 0) {
      throw new IllegalStateException("Can't save state while updating");
    }
    this.mStateSeq += 1;
    if (this.mStateSeq <= 0) {
      this.mStateSeq = 0;
    }
    this.mNextSavedIdx = 0;
    paramParcel.writeInt(this.mHistorySize);
    paramParcel.writeInt(this.mOwners.size());
    int i = this.mUndos.size();
    while (i > 0)
    {
      paramParcel.writeInt(1);
      i -= 1;
      ((UndoState)this.mUndos.get(i)).writeToParcel(paramParcel);
    }
    i = this.mRedos.size();
    paramParcel.writeInt(i);
    while (i > 0)
    {
      paramParcel.writeInt(2);
      i -= 1;
      ((UndoState)this.mRedos.get(i)).writeToParcel(paramParcel);
    }
    paramParcel.writeInt(0);
  }
  
  void saveOwner(UndoOwner paramUndoOwner, Parcel paramParcel)
  {
    if (paramUndoOwner.mStateSeq == this.mStateSeq)
    {
      paramParcel.writeInt(paramUndoOwner.mSavedIdx);
      return;
    }
    paramUndoOwner.mStateSeq = this.mStateSeq;
    paramUndoOwner.mSavedIdx = this.mNextSavedIdx;
    paramParcel.writeInt(paramUndoOwner.mSavedIdx);
    paramParcel.writeString(paramUndoOwner.mTag);
    paramParcel.writeInt(paramUndoOwner.mOpCount);
    this.mNextSavedIdx += 1;
  }
  
  public void setHistorySize(int paramInt)
  {
    this.mHistorySize = paramInt;
    if ((this.mHistorySize >= 0) && (countUndos(null) > this.mHistorySize)) {
      forgetUndos(null, countUndos(null) - this.mHistorySize);
    }
  }
  
  public void setUndoLabel(CharSequence paramCharSequence)
  {
    if (this.mWorking == null) {
      throw new IllegalStateException("Must be called during an update");
    }
    this.mWorking.setLabel(paramCharSequence);
  }
  
  public void suggestUndoLabel(CharSequence paramCharSequence)
  {
    if (this.mWorking == null) {
      throw new IllegalStateException("Must be called during an update");
    }
    this.mWorking.updateLabel(paramCharSequence);
  }
  
  public boolean uncommitState(int paramInt, UndoOwner paramUndoOwner)
  {
    if ((this.mWorking != null) && (this.mWorking.getCommitId() == paramInt))
    {
      if ((paramUndoOwner == null) || (this.mWorking.hasOperation(paramUndoOwner))) {
        return this.mWorking.setCanMerge(true);
      }
    }
    else
    {
      UndoState localUndoState = getTopUndo(null);
      if ((localUndoState != null) && ((paramUndoOwner == null) || (localUndoState.hasOperation(paramUndoOwner))) && (localUndoState.getCommitId() == paramInt)) {
        return localUndoState.setCanMerge(true);
      }
    }
    return false;
  }
  
  public int undo(UndoOwner[] paramArrayOfUndoOwner, int paramInt)
  {
    if (this.mWorking != null) {
      throw new IllegalStateException("Can't be called during an update");
    }
    int m = 0;
    int n = -1;
    this.mInUndo = true;
    UndoState localUndoState = getTopUndo(null);
    int i = n;
    int j = m;
    int k = paramInt;
    if (localUndoState != null)
    {
      localUndoState.makeExecuted();
      k = paramInt;
      j = m;
      i = n;
    }
    while (k > 0)
    {
      i = findPrevState(this.mUndos, paramArrayOfUndoOwner, i);
      if (i < 0) {
        break;
      }
      localUndoState = (UndoState)this.mUndos.remove(i);
      localUndoState.undo();
      this.mRedos.add(localUndoState);
      k -= 1;
      j += 1;
    }
    this.mInUndo = false;
    return j;
  }
  
  static final class UndoState
  {
    private boolean mCanMerge = true;
    private final int mCommitId;
    private boolean mExecuted;
    private CharSequence mLabel;
    private final UndoManager mManager;
    private final ArrayList<UndoOperation<?>> mOperations = new ArrayList();
    private ArrayList<UndoOperation<?>> mRecent;
    
    UndoState(UndoManager paramUndoManager, int paramInt)
    {
      this.mManager = paramUndoManager;
      this.mCommitId = paramInt;
    }
    
    UndoState(UndoManager paramUndoManager, Parcel paramParcel, ClassLoader paramClassLoader)
    {
      this.mManager = paramUndoManager;
      this.mCommitId = paramParcel.readInt();
      if (paramParcel.readInt() != 0)
      {
        bool1 = true;
        this.mCanMerge = bool1;
        if (paramParcel.readInt() == 0) {
          break label151;
        }
      }
      label151:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        this.mExecuted = bool1;
        this.mLabel = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
        int j = paramParcel.readInt();
        int i = 0;
        while (i < j)
        {
          paramUndoManager = this.mManager.restoreOwner(paramParcel);
          UndoOperation localUndoOperation = (UndoOperation)paramParcel.readParcelable(paramClassLoader);
          localUndoOperation.mOwner = paramUndoManager;
          this.mOperations.add(localUndoOperation);
          i += 1;
        }
        bool1 = false;
        break;
      }
    }
    
    void addOperation(UndoOperation<?> paramUndoOperation)
    {
      if (this.mOperations.contains(paramUndoOperation)) {
        throw new IllegalStateException("Already holds " + paramUndoOperation);
      }
      this.mOperations.add(paramUndoOperation);
      if (this.mRecent == null)
      {
        this.mRecent = new ArrayList();
        this.mRecent.add(paramUndoOperation);
      }
      paramUndoOperation = paramUndoOperation.mOwner;
      paramUndoOperation.mOpCount += 1;
    }
    
    boolean canMerge()
    {
      return (this.mCanMerge) && (!this.mExecuted);
    }
    
    void commit()
    {
      if (this.mRecent != null) {}
      for (int i = this.mRecent.size();; i = 0)
      {
        int j = 0;
        while (j < i)
        {
          ((UndoOperation)this.mRecent.get(j)).commit();
          j += 1;
        }
      }
      this.mRecent = null;
    }
    
    int countOperations()
    {
      return this.mOperations.size();
    }
    
    void destroy()
    {
      int i = this.mOperations.size() - 1;
      while (i >= 0)
      {
        UndoOwner localUndoOwner = ((UndoOperation)this.mOperations.get(i)).mOwner;
        localUndoOwner.mOpCount -= 1;
        if (localUndoOwner.mOpCount <= 0)
        {
          if (localUndoOwner.mOpCount < 0) {
            throw new IllegalStateException("Underflow of op count on owner " + localUndoOwner + " in op " + this.mOperations.get(i));
          }
          this.mManager.removeOwner(localUndoOwner);
        }
        i -= 1;
      }
    }
    
    int getCommitId()
    {
      return this.mCommitId;
    }
    
    CharSequence getLabel()
    {
      return this.mLabel;
    }
    
    <T extends UndoOperation> T getLastOperation(Class<T> paramClass, UndoOwner paramUndoOwner)
    {
      UndoOperation localUndoOperation = null;
      int i = this.mOperations.size();
      if ((paramClass == null) && (paramUndoOwner == null))
      {
        paramClass = localUndoOperation;
        if (i > 0) {
          paramClass = (UndoOperation)this.mOperations.get(i - 1);
        }
        return paramClass;
      }
      i -= 1;
      while (i >= 0)
      {
        localUndoOperation = (UndoOperation)this.mOperations.get(i);
        if ((paramUndoOwner != null) && (localUndoOperation.getOwner() != paramUndoOwner))
        {
          i -= 1;
        }
        else
        {
          if ((paramClass != null) && (localUndoOperation.getClass() != paramClass)) {
            return null;
          }
          return localUndoOperation;
        }
      }
      return null;
    }
    
    boolean hasData()
    {
      int i = this.mOperations.size() - 1;
      while (i >= 0)
      {
        if (((UndoOperation)this.mOperations.get(i)).hasData()) {
          return true;
        }
        i -= 1;
      }
      return false;
    }
    
    boolean hasMultipleOwners()
    {
      int j = this.mOperations.size();
      if (j <= 1) {
        return false;
      }
      UndoOwner localUndoOwner = ((UndoOperation)this.mOperations.get(0)).getOwner();
      int i = 1;
      while (i < j)
      {
        if (((UndoOperation)this.mOperations.get(i)).getOwner() != localUndoOwner) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    boolean hasOperation(UndoOwner paramUndoOwner)
    {
      int j = this.mOperations.size();
      if (paramUndoOwner == null) {
        return j != 0;
      }
      int i = 0;
      while (i < j)
      {
        if (((UndoOperation)this.mOperations.get(i)).getOwner() == paramUndoOwner) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    void makeExecuted()
    {
      this.mExecuted = true;
    }
    
    boolean matchOwner(UndoOwner paramUndoOwner)
    {
      int i = this.mOperations.size() - 1;
      while (i >= 0)
      {
        if (((UndoOperation)this.mOperations.get(i)).matchOwner(paramUndoOwner)) {
          return true;
        }
        i -= 1;
      }
      return false;
    }
    
    void redo()
    {
      int j = this.mOperations.size();
      int i = 0;
      while (i < j)
      {
        ((UndoOperation)this.mOperations.get(i)).redo();
        i += 1;
      }
    }
    
    boolean setCanMerge(boolean paramBoolean)
    {
      if ((paramBoolean) && (this.mExecuted)) {
        return false;
      }
      this.mCanMerge = paramBoolean;
      return true;
    }
    
    void setLabel(CharSequence paramCharSequence)
    {
      this.mLabel = paramCharSequence;
    }
    
    void undo()
    {
      int i = this.mOperations.size() - 1;
      while (i >= 0)
      {
        ((UndoOperation)this.mOperations.get(i)).undo();
        i -= 1;
      }
    }
    
    void updateLabel(CharSequence paramCharSequence)
    {
      if (this.mLabel != null) {
        this.mLabel = paramCharSequence;
      }
    }
    
    void writeToParcel(Parcel paramParcel)
    {
      int j = 1;
      if (this.mRecent != null) {
        throw new IllegalStateException("Can't save state before committing");
      }
      paramParcel.writeInt(this.mCommitId);
      if (this.mCanMerge)
      {
        i = 1;
        paramParcel.writeInt(i);
        if (!this.mExecuted) {
          break label129;
        }
      }
      label129:
      for (int i = j;; i = 0)
      {
        paramParcel.writeInt(i);
        TextUtils.writeToParcel(this.mLabel, paramParcel, 0);
        j = this.mOperations.size();
        paramParcel.writeInt(j);
        i = 0;
        while (i < j)
        {
          UndoOperation localUndoOperation = (UndoOperation)this.mOperations.get(i);
          this.mManager.saveOwner(localUndoOperation.mOwner, paramParcel);
          paramParcel.writeParcelable(localUndoOperation, 0);
          i += 1;
        }
        i = 0;
        break;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/UndoManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */