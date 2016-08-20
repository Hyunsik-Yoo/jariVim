import java.io.*;
import java.net.Socket;

/**
 * Created by macgongmon on 8/18/16.
 */
public class socketTest {

    public static void main(String[] args) {

        try {


            System.out.println("Socket 연결");
            Socket socket = new Socket("127.0.0.1", 50007);
            BufferedReader networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            try {
                String pushItem = "get_market_list_bob";
                networkWriter.write(pushItem);
                networkWriter.flush();


                //String receiveItem = networkReader.readLine();
                char[] temp = new char[1024];
                networkReader.read(temp);

                String result = new String(temp).trim();


                //String[] result2 = result.split(",");

                if(result.equals("null"))
                    System.out.println("True");
                System.out.println("received item was : " + new String(temp).trim());


            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
