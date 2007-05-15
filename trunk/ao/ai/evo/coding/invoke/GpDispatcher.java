package ao.ai.evo.coding.invoke;

import ao.ai.evo.product.Product;

import java.util.Arrays;

/**
 *
 */
public class GpDispatcher
{
    //----------------------------------------------------------------
    private final Message msg;


    //----------------------------------------------------------------
    public GpDispatcher(Message message)
    {
        assert message != null;

        msg = message;
    }


    //----------------------------------------------------------------
    public Product dispatch(Product receiver, Product... args)
    {
        return new MessageProduct(msg, receiver, args);
    }


    //----------------------------------------------------------------
    @Override
    public String toString()
    {
        return msg.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GpDispatcher that = (GpDispatcher) o;
        return msg.equals(that.msg);
    }

    @Override
    public int hashCode()
    {
        return msg.hashCode();
    }


    //----------------------------------------------------------------
    private static class MessageProduct implements Product
    {
        private final Message deleget;
        private final Product receiverProd;
        private final Product arguments[];

        public MessageProduct(
                Message message,
                Product receiver,
                Product args[])
        {
            deleget      = message;
            receiverProd = receiver;
            arguments    = args;
        }

        public Object build()
        {
            boolean  needProd[]    = deleget.productArgs();
            Object[] processedArgs = new Object[arguments.length];

            for (int i = 0; i < arguments.length; i++)
            {
                processedArgs[i] = needProd[i]
                                   ? arguments[i]
                                   : arguments[i].build();
            }

            return deleget.dispatch(
                    (receiverProd == null ? null : receiverProd.build()),
                    processedArgs);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MessageProduct that = (MessageProduct) o;

            return Arrays.equals(arguments, that.arguments) &&
                   deleget.equals(that.deleget) &&
                   !(receiverProd != null
                     ? !receiverProd.equals(that.receiverProd)
                     : that.receiverProd != null);
        }

        @Override
        public int hashCode()
        {
            int result;
            result = deleget.hashCode();
            result = 31 * result + (receiverProd != null ? receiverProd.hashCode() : 0);
            result = 31 * result + Arrays.hashCode(arguments);
            return result;
        }
    }
}
