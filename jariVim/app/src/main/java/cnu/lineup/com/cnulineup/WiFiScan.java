package cnu.lineup.com.cnulineup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by macgongmon on 1/11/17.
 */

public class WiFiScan extends AsyncTask<Integer,Integer,Integer> {
    private Context context;
    private ProgressDialog progressDialog;

    public WiFiScan(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });
        progressDialog.setMax(255);
        progressDialog.setMessage("진행중(WiFi에 따라 시간이 걸릴 수 있습니다.)");
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
            }
        });

        progressDialog.show();

        super.onPreExecute();
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        progressDialog.dismiss();
        Toast.makeText(context,integer.toString()+"명이 같은 와이파이를 사용중입니다",Toast.LENGTH_SHORT);
        super.onPostExecute(integer);
    }

    @Override
    protected Integer doInBackground(Integer... strings) {
        String connections = "";
        InetAddress host;
        try
        {
            String ipStr =
                    String.format("%d.%d.%d.%d",
                            (strings[0] & 0xff),
                            (strings[0] >> 8 & 0xff),
                            (strings[0] >> 16 & 0xff),
                            (strings[0] >> 24 & 0xff));
            host = InetAddress.getByName(ipStr);
            byte[] ip = host.getAddress();

            for(int i = 1; i <= 255; i++)
            {
                publishProgress(i);
                ip[3] = (byte) i;
                InetAddress address = InetAddress.getByAddress(ip);

                if(address.isReachable(10))
                {
                    Log.e("test",address + " machine is turned on and can be pinged");
                    connections+= address+"\n";
                }
                else if(!address.getHostAddress().equals(address.getHostName()))
                {
                    Log.e("test",address + " machine is known in a DNS lookup");
                }
                else{
                    Log.e("test","HostAddress : "+address.getHostAddress()+" HostName : "+address.getHostName());
                }
            }
        }
        catch(UnknownHostException e1)
        {
            e1.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        Log.e("test",connections);

        return countNumMac();
    }

    private int countNumMac()
    {
        int macCount = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    // Basic sanity check
                    String mac = splitted[3];
                    if (!mac.matches("00:00:00:00:00:00")&& !splitted[3].equals("type")) {
                        macCount++;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return macCount;
    }

}
