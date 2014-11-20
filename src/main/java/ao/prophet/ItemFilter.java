package ao.prophet;

/**
 * Used to filter so that only some specific items are predicted.
 */
public interface ItemFilter<I>
{
    public boolean accept(I item);


    //----------------------------------------------------
    public static class Impl<I> implements ItemFilter<I>
    {
        public static final ItemFilter ACCEPT_ALL = new Impl();

//        @SuppressWarnings("unchecked")
//        public static <I> ItemFilter<I> acceptAll()
//        {
//            return (ItemFilter<I>) ACCEPT_ALL;
//        }

        //------------------------------------------------------
        private Impl() {}

        public boolean accept(I item)
        {
            return true;
        }
    }
}
