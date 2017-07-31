package android.animation;

public abstract class BidirectionalTypeConverter<T, V>
  extends TypeConverter<T, V>
{
  private BidirectionalTypeConverter mInvertedConverter;
  
  public BidirectionalTypeConverter(Class<T> paramClass, Class<V> paramClass1)
  {
    super(paramClass, paramClass1);
  }
  
  public abstract T convertBack(V paramV);
  
  public BidirectionalTypeConverter<V, T> invert()
  {
    if (this.mInvertedConverter == null) {
      this.mInvertedConverter = new InvertedConverter(this);
    }
    return this.mInvertedConverter;
  }
  
  private static class InvertedConverter<From, To>
    extends BidirectionalTypeConverter<From, To>
  {
    private BidirectionalTypeConverter<To, From> mConverter;
    
    public InvertedConverter(BidirectionalTypeConverter<To, From> paramBidirectionalTypeConverter)
    {
      super(paramBidirectionalTypeConverter.getSourceType());
      this.mConverter = paramBidirectionalTypeConverter;
    }
    
    public To convert(From paramFrom)
    {
      return (To)this.mConverter.convertBack(paramFrom);
    }
    
    public From convertBack(To paramTo)
    {
      return (From)this.mConverter.convert(paramTo);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/BidirectionalTypeConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */