package android.os;

import java.util.Arrays;

public class WorkSource
  implements Parcelable
{
  public static final Parcelable.Creator<WorkSource> CREATOR = new Parcelable.Creator()
  {
    public WorkSource createFromParcel(Parcel paramAnonymousParcel)
    {
      return new WorkSource(paramAnonymousParcel);
    }
    
    public WorkSource[] newArray(int paramAnonymousInt)
    {
      return new WorkSource[paramAnonymousInt];
    }
  };
  static final boolean DEBUG = false;
  static final String TAG = "WorkSource";
  static WorkSource sGoneWork;
  static WorkSource sNewbWork;
  static final WorkSource sTmpWorkSource = new WorkSource(0);
  String[] mNames;
  int mNum;
  int[] mUids;
  
  public WorkSource()
  {
    this.mNum = 0;
  }
  
  public WorkSource(int paramInt)
  {
    this.mNum = 1;
    this.mUids = new int[] { paramInt, 0 };
    this.mNames = null;
  }
  
  public WorkSource(int paramInt, String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("Name can't be null");
    }
    this.mNum = 1;
    this.mUids = new int[] { paramInt, 0 };
    this.mNames = new String[] { paramString, null };
  }
  
  WorkSource(Parcel paramParcel)
  {
    this.mNum = paramParcel.readInt();
    this.mUids = paramParcel.createIntArray();
    this.mNames = paramParcel.createStringArray();
  }
  
  public WorkSource(WorkSource paramWorkSource)
  {
    if (paramWorkSource == null)
    {
      this.mNum = 0;
      return;
    }
    this.mNum = paramWorkSource.mNum;
    if (paramWorkSource.mUids != null)
    {
      this.mUids = ((int[])paramWorkSource.mUids.clone());
      if (paramWorkSource.mNames != null) {}
      for (paramWorkSource = (String[])paramWorkSource.mNames.clone();; paramWorkSource = null)
      {
        this.mNames = paramWorkSource;
        return;
      }
    }
    this.mUids = null;
    this.mNames = null;
  }
  
  private static WorkSource addWork(WorkSource paramWorkSource, int paramInt)
  {
    if (paramWorkSource == null) {
      return new WorkSource(paramInt);
    }
    paramWorkSource.insert(paramWorkSource.mNum, paramInt);
    return paramWorkSource;
  }
  
  private static WorkSource addWork(WorkSource paramWorkSource, int paramInt, String paramString)
  {
    if (paramWorkSource == null) {
      return new WorkSource(paramInt, paramString);
    }
    paramWorkSource.insert(paramWorkSource.mNum, paramInt, paramString);
    return paramWorkSource;
  }
  
  private int compare(WorkSource paramWorkSource, int paramInt1, int paramInt2)
  {
    int i = this.mUids[paramInt1] - paramWorkSource.mUids[paramInt2];
    if (i != 0) {
      return i;
    }
    return this.mNames[paramInt1].compareTo(paramWorkSource.mNames[paramInt2]);
  }
  
  private void insert(int paramInt1, int paramInt2)
  {
    if (this.mUids == null)
    {
      this.mUids = new int[4];
      this.mUids[0] = paramInt2;
      this.mNum = 1;
      return;
    }
    if (this.mNum >= this.mUids.length)
    {
      int[] arrayOfInt = new int[this.mNum * 3 / 2];
      if (paramInt1 > 0) {
        System.arraycopy(this.mUids, 0, arrayOfInt, 0, paramInt1);
      }
      if (paramInt1 < this.mNum) {
        System.arraycopy(this.mUids, paramInt1, arrayOfInt, paramInt1 + 1, this.mNum - paramInt1);
      }
      this.mUids = arrayOfInt;
      this.mUids[paramInt1] = paramInt2;
      this.mNum += 1;
      return;
    }
    if (paramInt1 < this.mNum) {
      System.arraycopy(this.mUids, paramInt1, this.mUids, paramInt1 + 1, this.mNum - paramInt1);
    }
    this.mUids[paramInt1] = paramInt2;
    this.mNum += 1;
  }
  
  private void insert(int paramInt1, int paramInt2, String paramString)
  {
    if (this.mUids == null)
    {
      this.mUids = new int[4];
      this.mUids[0] = paramInt2;
      this.mNames = new String[4];
      this.mNames[0] = paramString;
      this.mNum = 1;
      return;
    }
    if (this.mNum >= this.mUids.length)
    {
      int[] arrayOfInt = new int[this.mNum * 3 / 2];
      String[] arrayOfString = new String[this.mNum * 3 / 2];
      if (paramInt1 > 0)
      {
        System.arraycopy(this.mUids, 0, arrayOfInt, 0, paramInt1);
        System.arraycopy(this.mNames, 0, arrayOfString, 0, paramInt1);
      }
      if (paramInt1 < this.mNum)
      {
        System.arraycopy(this.mUids, paramInt1, arrayOfInt, paramInt1 + 1, this.mNum - paramInt1);
        System.arraycopy(this.mNames, paramInt1, arrayOfString, paramInt1 + 1, this.mNum - paramInt1);
      }
      this.mUids = arrayOfInt;
      this.mNames = arrayOfString;
      this.mUids[paramInt1] = paramInt2;
      this.mNames[paramInt1] = paramString;
      this.mNum += 1;
      return;
    }
    if (paramInt1 < this.mNum)
    {
      System.arraycopy(this.mUids, paramInt1, this.mUids, paramInt1 + 1, this.mNum - paramInt1);
      System.arraycopy(this.mNames, paramInt1, this.mNames, paramInt1 + 1, this.mNum - paramInt1);
    }
    this.mUids[paramInt1] = paramInt2;
    this.mNames[paramInt1] = paramString;
    this.mNum += 1;
  }
  
  private boolean removeUids(WorkSource paramWorkSource)
  {
    int k = this.mNum;
    int[] arrayOfInt = this.mUids;
    int m = paramWorkSource.mNum;
    paramWorkSource = paramWorkSource.mUids;
    boolean bool = false;
    int j = 0;
    int i = 0;
    while ((j < k) && (i < m)) {
      if (paramWorkSource[i] == arrayOfInt[j])
      {
        k -= 1;
        bool = true;
        if (j < k) {
          System.arraycopy(arrayOfInt, j + 1, arrayOfInt, j, k - j);
        }
        i += 1;
      }
      else if (paramWorkSource[i] > arrayOfInt[j])
      {
        j += 1;
      }
      else
      {
        i += 1;
      }
    }
    this.mNum = k;
    return bool;
  }
  
  private boolean removeUidsAndNames(WorkSource paramWorkSource)
  {
    int k = this.mNum;
    int[] arrayOfInt1 = this.mUids;
    String[] arrayOfString = this.mNames;
    int m = paramWorkSource.mNum;
    int[] arrayOfInt2 = paramWorkSource.mUids;
    paramWorkSource = paramWorkSource.mNames;
    boolean bool = false;
    int j = 0;
    int i = 0;
    while ((j < k) && (i < m)) {
      if ((arrayOfInt2[i] == arrayOfInt1[j]) && (paramWorkSource[i].equals(arrayOfString[j])))
      {
        k -= 1;
        bool = true;
        if (j < k)
        {
          System.arraycopy(arrayOfInt1, j + 1, arrayOfInt1, j, k - j);
          System.arraycopy(arrayOfString, j + 1, arrayOfString, j, k - j);
        }
        i += 1;
      }
      else if ((arrayOfInt2[i] > arrayOfInt1[j]) || ((arrayOfInt2[i] == arrayOfInt1[j]) && (paramWorkSource[i].compareTo(arrayOfString[j]) > 0)))
      {
        j += 1;
      }
      else
      {
        i += 1;
      }
    }
    this.mNum = k;
    return bool;
  }
  
  private boolean updateLocked(WorkSource paramWorkSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((this.mNames == null) && (paramWorkSource.mNames == null)) {
      return updateUidsLocked(paramWorkSource, paramBoolean1, paramBoolean2);
    }
    if ((this.mNum > 0) && (this.mNames == null)) {
      throw new IllegalArgumentException("Other " + paramWorkSource + " has names, but target " + this + " does not");
    }
    if ((paramWorkSource.mNum > 0) && (paramWorkSource.mNames == null)) {
      throw new IllegalArgumentException("Target " + this + " has names, but other " + paramWorkSource + " does not");
    }
    return updateUidsAndNamesLocked(paramWorkSource, paramBoolean1, paramBoolean2);
  }
  
  private boolean updateUidsAndNamesLocked(WorkSource paramWorkSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i1 = paramWorkSource.mNum;
    int[] arrayOfInt = paramWorkSource.mUids;
    String[] arrayOfString = paramWorkSource.mNames;
    boolean bool = false;
    int i = 0;
    int j = 0;
    while ((i < this.mNum) || (j < i1))
    {
      int k = -1;
      int m;
      if (i < this.mNum)
      {
        if (j < i1)
        {
          m = compare(paramWorkSource, i, j);
          k = m;
          if (m <= 0) {}
        }
      }
      else
      {
        bool = true;
        insert(i, arrayOfInt[j], arrayOfString[j]);
        if (paramBoolean2) {
          sNewbWork = addWork(sNewbWork, arrayOfInt[j], arrayOfString[j]);
        }
        i += 1;
        j += 1;
        continue;
      }
      if (!paramBoolean1)
      {
        m = j;
        if (j < i1)
        {
          m = j;
          if (k == 0) {
            m = j + 1;
          }
        }
        i += 1;
        j = m;
      }
      else
      {
        m = i;
        for (;;)
        {
          int n = m;
          m = n;
          if (k < 0)
          {
            sGoneWork = addWork(sGoneWork, this.mUids[n], this.mNames[n]);
            m = n + 1;
            if (m < this.mNum) {}
          }
          else
          {
            n = m;
            if (i < m)
            {
              System.arraycopy(this.mUids, m, this.mUids, i, this.mNum - m);
              System.arraycopy(this.mNames, m, this.mNames, i, this.mNum - m);
              this.mNum -= m - i;
              n = i;
            }
            i = n;
            if (n >= this.mNum) {
              break;
            }
            i = n;
            if (k != 0) {
              break;
            }
            i = n + 1;
            j += 1;
            break;
          }
          if (j < i1) {
            k = compare(paramWorkSource, m, j);
          } else {
            k = -1;
          }
        }
      }
    }
    return bool;
  }
  
  private boolean updateUidsLocked(WorkSource paramWorkSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    int k = this.mNum;
    int[] arrayOfInt1 = this.mUids;
    int i2 = paramWorkSource.mNum;
    int[] arrayOfInt2 = paramWorkSource.mUids;
    boolean bool = false;
    int i = 0;
    int j = 0;
    paramWorkSource = arrayOfInt1;
    while ((i < k) || (j < i2))
    {
      if ((i >= k) || ((j < i2) && (arrayOfInt2[j] < paramWorkSource[i])))
      {
        bool = true;
        if (paramWorkSource == null)
        {
          paramWorkSource = new int[4];
          paramWorkSource[0] = arrayOfInt2[j];
        }
        for (;;)
        {
          if (paramBoolean2) {
            sNewbWork = addWork(sNewbWork, arrayOfInt2[j]);
          }
          k += 1;
          i += 1;
          j += 1;
          break;
          if (k >= paramWorkSource.length)
          {
            arrayOfInt1 = new int[paramWorkSource.length * 3 / 2];
            if (i > 0) {
              System.arraycopy(paramWorkSource, 0, arrayOfInt1, 0, i);
            }
            if (i < k) {
              System.arraycopy(paramWorkSource, i, arrayOfInt1, i + 1, k - i);
            }
            paramWorkSource = arrayOfInt1;
            arrayOfInt1[i] = arrayOfInt2[j];
          }
          else
          {
            if (i < k) {
              System.arraycopy(paramWorkSource, i, paramWorkSource, i + 1, k - i);
            }
            paramWorkSource[i] = arrayOfInt2[j];
          }
        }
      }
      int m;
      if (!paramBoolean1)
      {
        m = j;
        if (j < i2)
        {
          m = j;
          if (arrayOfInt2[j] == paramWorkSource[i]) {
            m = j + 1;
          }
        }
        i += 1;
        j = m;
      }
      else
      {
        int i1;
        for (m = i;; m = i1 + 1)
        {
          i1 = m;
          if ((i1 >= k) || ((j < i2) && (arrayOfInt2[j] <= paramWorkSource[i1]))) {
            break;
          }
          sGoneWork = addWork(sGoneWork, paramWorkSource[i1]);
        }
        int n = k;
        m = i1;
        if (i < i1)
        {
          System.arraycopy(paramWorkSource, i1, paramWorkSource, i, k - i1);
          n = k - (i1 - i);
          m = i;
        }
        k = n;
        i = m;
        if (m < n)
        {
          k = n;
          i = m;
          if (j < i2)
          {
            k = n;
            i = m;
            if (arrayOfInt2[j] == paramWorkSource[m])
            {
              i = m + 1;
              j += 1;
              k = n;
            }
          }
        }
      }
    }
    this.mNum = k;
    this.mUids = paramWorkSource;
    return bool;
  }
  
  public boolean add(int paramInt)
  {
    if (this.mNum <= 0)
    {
      this.mNames = null;
      insert(0, paramInt);
      return true;
    }
    if (this.mNames != null) {
      throw new IllegalArgumentException("Adding without name to named " + this);
    }
    int i = Arrays.binarySearch(this.mUids, 0, this.mNum, paramInt);
    if (i >= 0) {
      return false;
    }
    insert(-i - 1, paramInt);
    return true;
  }
  
  public boolean add(int paramInt, String paramString)
  {
    if (this.mNum <= 0)
    {
      insert(0, paramInt, paramString);
      return true;
    }
    if (this.mNames == null) {
      throw new IllegalArgumentException("Adding name to unnamed " + this);
    }
    int i = 0;
    for (;;)
    {
      if ((i >= this.mNum) || (this.mUids[i] > paramInt)) {}
      int j;
      do
      {
        insert(i, paramInt, paramString);
        return true;
        if (this.mUids[i] != paramInt) {
          break;
        }
        j = this.mNames[i].compareTo(paramString);
      } while (j > 0);
      if (j == 0) {
        return false;
      }
      i += 1;
    }
  }
  
  public boolean add(WorkSource paramWorkSource)
  {
    synchronized (sTmpWorkSource)
    {
      boolean bool = updateLocked(paramWorkSource, false, false);
      return bool;
    }
  }
  
  public WorkSource addReturningNewbs(int paramInt)
  {
    synchronized (sTmpWorkSource)
    {
      sNewbWork = null;
      sTmpWorkSource.mUids[0] = paramInt;
      updateLocked(sTmpWorkSource, false, true);
      WorkSource localWorkSource2 = sNewbWork;
      return localWorkSource2;
    }
  }
  
  public WorkSource addReturningNewbs(WorkSource paramWorkSource)
  {
    synchronized (sTmpWorkSource)
    {
      sNewbWork = null;
      updateLocked(paramWorkSource, false, true);
      paramWorkSource = sNewbWork;
      return paramWorkSource;
    }
  }
  
  public void clear()
  {
    this.mNum = 0;
  }
  
  public void clearNames()
  {
    if (this.mNames != null)
    {
      this.mNames = null;
      int k = 1;
      int j = this.mNum;
      int i = 1;
      if (i < this.mNum)
      {
        if (this.mUids[i] == this.mUids[(i - 1)]) {
          j -= 1;
        }
        for (;;)
        {
          i += 1;
          break;
          this.mUids[k] = this.mUids[i];
          k += 1;
        }
      }
      this.mNum = j;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean diff(WorkSource paramWorkSource)
  {
    int j = this.mNum;
    if (j != paramWorkSource.mNum) {
      return true;
    }
    int[] arrayOfInt1 = this.mUids;
    int[] arrayOfInt2 = paramWorkSource.mUids;
    String[] arrayOfString = this.mNames;
    paramWorkSource = paramWorkSource.mNames;
    int i = 0;
    while (i < j)
    {
      if (arrayOfInt1[i] != arrayOfInt2[i]) {
        return true;
      }
      if ((arrayOfString == null) || (paramWorkSource == null) || (arrayOfString[i].equals(paramWorkSource[i]))) {
        i += 1;
      } else {
        return true;
      }
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof WorkSource)) && (!diff((WorkSource)paramObject));
  }
  
  public int get(int paramInt)
  {
    return this.mUids[paramInt];
  }
  
  public String getName(int paramInt)
  {
    String str = null;
    if (this.mNames != null) {
      str = this.mNames[paramInt];
    }
    return str;
  }
  
  public int hashCode()
  {
    int i = 0;
    int j = 0;
    while (j < this.mNum)
    {
      i = (i << 4 | i >>> 28) ^ this.mUids[j];
      j += 1;
    }
    int k = i;
    if (this.mNames != null)
    {
      j = 0;
      for (;;)
      {
        k = i;
        if (j >= this.mNum) {
          break;
        }
        i = (i << 4 | i >>> 28) ^ this.mNames[j].hashCode();
        j += 1;
      }
    }
    return k;
  }
  
  public boolean remove(WorkSource paramWorkSource)
  {
    if ((this.mNum <= 0) || (paramWorkSource.mNum <= 0)) {
      return false;
    }
    if ((this.mNames == null) && (paramWorkSource.mNames == null)) {
      return removeUids(paramWorkSource);
    }
    if (this.mNames == null) {
      throw new IllegalArgumentException("Other " + paramWorkSource + " has names, but target " + this + " does not");
    }
    if (paramWorkSource.mNames == null) {
      throw new IllegalArgumentException("Target " + this + " has names, but other " + paramWorkSource + " does not");
    }
    return removeUidsAndNames(paramWorkSource);
  }
  
  public void set(int paramInt)
  {
    this.mNum = 1;
    if (this.mUids == null) {
      this.mUids = new int[2];
    }
    this.mUids[0] = paramInt;
    this.mNames = null;
  }
  
  public void set(int paramInt, String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("Name can't be null");
    }
    this.mNum = 1;
    if (this.mUids == null)
    {
      this.mUids = new int[2];
      this.mNames = new String[2];
    }
    this.mUids[0] = paramInt;
    this.mNames[0] = paramString;
  }
  
  public void set(WorkSource paramWorkSource)
  {
    if (paramWorkSource == null)
    {
      this.mNum = 0;
      return;
    }
    this.mNum = paramWorkSource.mNum;
    if (paramWorkSource.mUids != null)
    {
      if ((this.mUids != null) && (this.mUids.length >= this.mNum)) {
        System.arraycopy(paramWorkSource.mUids, 0, this.mUids, 0, this.mNum);
      }
      while (paramWorkSource.mNames != null) {
        if ((this.mNames != null) && (this.mNames.length >= this.mNum))
        {
          System.arraycopy(paramWorkSource.mNames, 0, this.mNames, 0, this.mNum);
          return;
          this.mUids = ((int[])paramWorkSource.mUids.clone());
        }
        else
        {
          this.mNames = ((String[])paramWorkSource.mNames.clone());
          return;
        }
      }
      this.mNames = null;
      return;
    }
    this.mUids = null;
    this.mNames = null;
  }
  
  public WorkSource[] setReturningDiffs(WorkSource paramWorkSource)
  {
    synchronized (sTmpWorkSource)
    {
      sNewbWork = null;
      sGoneWork = null;
      updateLocked(paramWorkSource, true, true);
      if ((sNewbWork != null) || (sGoneWork != null))
      {
        paramWorkSource = sNewbWork;
        WorkSource localWorkSource2 = sGoneWork;
        return new WorkSource[] { paramWorkSource, localWorkSource2 };
      }
      return null;
    }
  }
  
  public int size()
  {
    return this.mNum;
  }
  
  public WorkSource stripNames()
  {
    if (this.mNum <= 0) {
      return new WorkSource();
    }
    WorkSource localWorkSource = new WorkSource();
    int i = 0;
    while (i < this.mNum)
    {
      int j = this.mUids[i];
      if ((i == 0) || (-1 != j)) {
        localWorkSource.add(j);
      }
      i += 1;
    }
    return localWorkSource;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("WorkSource{");
    int i = 0;
    while (i < this.mNum)
    {
      if (i != 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append(this.mUids[i]);
      if (this.mNames != null)
      {
        localStringBuilder.append(" ");
        localStringBuilder.append(this.mNames[i]);
      }
      i += 1;
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mNum);
    paramParcel.writeIntArray(this.mUids);
    paramParcel.writeStringArray(this.mNames);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/WorkSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */