package ao.prophet.test;

import ao.prophet.Prophet;
import ao.prophet.impl.ProphetImpl;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Interactive play with Prophet.
 */
public class Interactive
{
    //--------------------------------------------------------------------
    private Interactive() {}


    //--------------------------------------------------------------------
    public static void main(String[] args)
            throws IOException, InterruptedException
    {
        Prophet<String, String> prophet = new ProphetImpl<String, String>();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // pre populate
        prophet.likes("a", "1");
        prophet.likes("a", "2");
        prophet.likes("a", "3");

//        for (int i = 1; i <= 9; i++)
//        {
//            prophet.dislikes("x", String.valueOf(i));
//        }

        prophet.likes("b", "4");
        prophet.likes("b", "5");
        prophet.likes("b", "6");

        prophet.likes("c", "7");
        prophet.likes("c", "8");
        prophet.likes("c", "9");

        prophet.likes("d", "1");
        prophet.dislikes("d", "4");


        Thread.sleep( 500 );
        while (true)
        {
            System.out.print("User: ");
            String user = br.readLine();

            if (user.equals("exit")) break;

            System.out.print("Item: ");
            String item = br.readLine();

            prophet.likes( user, item );
            Thread.sleep( 500 );

            System.out.print("Prediction: ");
            System.out.println(prophet.predict( user, 20 ));
        }

        System.out.println("bye");
        prophet = null;
        System.gc();
    }
}
