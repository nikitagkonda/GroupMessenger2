package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

import static java.lang.Math.max;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 *
 * @author stevko
 *
 */


/*
Algorithms referred:
1. http://www.cse.buffalo.edu/~stevko/courses/cse486/spring17/lectures/12-multicast2.pdf
2. http://www.inf.ed.ac.uk/teaching/courses/ds/handouts/handout13.pdf
3. https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
*/

public class GroupMessengerActivity extends Activity {

    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static final int SERVER_PORT = 10000;
    int sequence_no = 0;
    int[] unique_message_i= new int[]{0,10,20,30,40};
    int[] proposed_seq = new int[]{-1,-1,-1,-1,-1};
    int[] agreed_seq = new int[]{-1,-1,-1,-1,-1};

    HashMap<Integer, String> identifier_message = new HashMap<Integer, String>();

    PriorityQueue<HoldBackQueue> holdBackQueue=new PriorityQueue<HoldBackQueue>(10, new Comparator<HoldBackQueue>() {
        public int compare(HoldBackQueue h1, HoldBackQueue h2) {
            if (h1.getSeq_no() < h2.getSeq_no()) return -1;
            if (h1.getSeq_no() > h2.getSeq_no()) return 1;
            if (h1.getMid() < h2.getMid()) return -1;
            if (h1.getMid() > h2.getMid()) return 1;
            return 0;
        }
    });

    List<Integer> proposed_nos= new ArrayList<Integer>();
    //private final ContentResolver mContentResolver=getContentResolver();
    private final Uri mUri= buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger2.provider");

    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*TODO: Use the TextView to display your messages. Though there is no grading component
        * on how you display the messages, if you implement it, it'll make your debugging easier.
                */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());

        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));

        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */





        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        try {
            /*
             * Create a server socket as well as a thread (AsyncTask) that listens on the server
             * port.
             *
             * AsyncTask is a simplified thread construct that Android provides. Please make sure
             * you know how it works by reading
             * http://developer.android.com/reference/android/os/AsyncTask.html
             */
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            /*
             * Log is a good way to debug your code. LogCat prints out all the messages that
             * Log class writes.
             *
             * Please read http://developer.android.com/tools/debugging/debugging-projects.html
             * and http://developer.android.com/tools/debugging/debugging-log.html
             * for more information on debugging.
             */
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }




        final EditText editText = (EditText) findViewById(R.id.editText1);

        findViewById(R.id.button4).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String msg = editText.getText().toString() + "\n";
                        editText.setText("");

                        switch (Integer.valueOf(myPort)){
                            case 11108:
                                unique_message_i[0]++;
                                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort, String.valueOf(unique_message_i[0]));
                                break;
                            case 11112:
                                unique_message_i[1]++;
                                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort, String.valueOf(unique_message_i[1]));
                                break;
                            case 11116:
                                unique_message_i[2]++;
                                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort, String.valueOf(unique_message_i[2]));
                                break;
                            case 11120:
                                unique_message_i[3]++;
                                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort, String.valueOf(unique_message_i[3]));
                                break;
                            case 11124:
                                unique_message_i[4]++;
                                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort, String.valueOf(unique_message_i[4]));
                                break;
                        }



                    }
                }
        );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }

    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];

            while(true) {
                try {
                    Socket server = serverSocket.accept();

                    ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
                    String[] received = (String[]) ois.readObject();

                    //Decide on a proposed sequence number and put the message in the queue but mark it as undeliverable

                    if(received[3].equals("first")) {

                        identifier_message.put(Integer.valueOf(received[2]), received[0]);
                        int proposed_no = 0;

                        switch (Integer.valueOf(received[1])) {
                            case 11108:
                                proposed_seq[0] = max(agreed_seq[0], proposed_seq[0]) + 1;
                                proposed_no = proposed_seq[0];
                                break;
                            case 11112:
                                proposed_seq[1] = max(agreed_seq[1], proposed_seq[1]) + 1;
                                proposed_no = proposed_seq[1];
                                break;
                            case 11116:
                                proposed_seq[2] = max(agreed_seq[2], proposed_seq[2]) + 1;
                                proposed_no = proposed_seq[2];
                                break;
                            case 11120:
                                proposed_seq[3] = max(agreed_seq[3], proposed_seq[3]) + 1;
                                proposed_no = proposed_seq[3];
                                break;
                            case 11124:
                                proposed_seq[4] = max(agreed_seq[4], proposed_seq[4]) + 1;
                                proposed_no = proposed_seq[4];
                                break;

                        }
                        HoldBackQueue obj = new HoldBackQueue(Integer.valueOf(received[2]),false,proposed_no,Integer.valueOf(received[4]));
                        holdBackQueue.add(obj);

                        ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
                        oos.writeObject(proposed_no);
                    }

                    //Set the agreed sequence number in the queue and mark it as deliverable

                    else{
                        switch (Integer.valueOf(received[1])) {
                            case 11108:
                                agreed_seq[0] = max(agreed_seq[0], Integer.valueOf(received[0]));
                                break;
                            case 11112:
                                agreed_seq[1] = max(agreed_seq[1], Integer.valueOf(received[0]));
                                break;
                            case 11116:
                                agreed_seq[2] = max(agreed_seq[2], Integer.valueOf(received[0]));
                                break;
                            case 11120:
                                agreed_seq[3] = max(agreed_seq[3], Integer.valueOf(received[0]));
                                break;
                            case 11124:
                                agreed_seq[4] = max(agreed_seq[4], Integer.valueOf(received[0]));
                                break;

                        }

                        //Failed port detection

                        String failed_port=null;
                        if (received[4]!=null){
                            failed_port = received[4];
                        }


                        Iterator<HoldBackQueue> it = holdBackQueue.iterator();
                        HoldBackQueue current;


                        while(it.hasNext()){
                            current = it.next();
                            if(current.getMid() == Integer.valueOf(received[2])){
                                holdBackQueue.remove(current);
                                current.seq_no = Integer.valueOf(received[0]);
                                current.isDeliverable = true;
                                holdBackQueue.add(current);
                            }


                            //Remove the undeliverable messages of the failed port from the queue

                            if(failed_port!=null){
                                if((current.getPort_no() == Integer.valueOf(failed_port)) &&  (!current.isDeliverable()))
                                    holdBackQueue.remove(current);



                            }


                        }

                        if(failed_port!=null)
                            if(holdBackQueue.size()>4){
                                current =holdBackQueue.peek();
                                if(!current.isDeliverable()) {
                                    Iterator<HoldBackQueue> it3 = holdBackQueue.iterator();
                                    HoldBackQueue q;
                                    while (it3.hasNext()) {
                                        q = it3.next();
                                        if (q.getPort_no() == current.getPort_no() && q.isDeliverable() && current.getMid() == q.getMid() - 1) {
                                            holdBackQueue.remove(current);
                                            current.isDeliverable = true;
                                            holdBackQueue.add(current);
                                            break;
                                        }
                                    }
                                }

                            }


                        //Publish the deliverable messages at the front of the queue

                        while((current = holdBackQueue.peek())!=null && current.isDeliverable()){
                            holdBackQueue.remove(current);
                            publishProgress(identifier_message.get(current.getMid()));
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }

        protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().
             */

            String strReceived = strings[0].trim();

            TextView tv = (TextView) findViewById(R.id.textView1);
            tv.setMovementMethod(new ScrollingMovementMethod());
            tv.append(strReceived + "\n");

            //content resolver
            ContentValues values= new ContentValues();
            values.put("key",String.valueOf(sequence_no));
            values.put("value",strReceived);
            Uri newUri = getContentResolver().insert(
                    mUri,    // assume we already created a Uri object with our provider URI
                    values
            );

            sequence_no=sequence_no+1;
            return;
        }
    }

    /***
     * ClientTask is an AsyncTask that should send a string over the network.
     * It is created by ClientTask.executeOnExecutor() call whenever OnKeyListener.onKey() detects
     * an enter key press event.
     *
     * @author stevko
     *
     */
    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

            String[] remotePort =  new String[5];
            remotePort[0]=REMOTE_PORT0;
            remotePort[1]=REMOTE_PORT1;
            remotePort[2]=REMOTE_PORT2;
            remotePort[3]=REMOTE_PORT3;
            remotePort[4]=REMOTE_PORT4;
            String failed_port = null;

            //Broadcasting the message along with its unique identifier, sender port number and receiver port number

            for(int i=0;i<remotePort.length;i++)
            {
                Socket socket = null;
                try {
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort[i]));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String msgToSend = msgs[0];
                String port_no = msgs[1];
                String unique_id = msgs[2];

                String[] sendObject = new String[5];
                sendObject[0] =msgToSend;
                sendObject[1]=remotePort[i];
                sendObject[2]=unique_id;
                sendObject[3]="first";
                sendObject[4]=port_no;

                ObjectOutputStream oos = null;
                if(socket!=null)
                    try {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                if(oos!=null)
                    try {
                        oos.writeObject(sendObject);
                    } catch (IOException e) {
                        e.printStackTrace();
                        failed_port = remotePort[i];
                    }

                //Receiving the proposed sequence number from all the avds

                ObjectInputStream ois = null;
                if(socket!=null)
                    try {
                        ois = new ObjectInputStream(socket.getInputStream());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        failed_port = remotePort[i];
                    }
                if(ois!=null)
                    try {
                        proposed_nos.add((Integer) ois.readObject());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    } catch (OptionalDataException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(socket!=null)
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            //Deciding the agreed sequence number

            int a =Collections.max(proposed_nos);
            proposed_nos.clear();

            //Broadcasting the sequence number along with unique identifier,sender port number and receiver port number

            for(int i=0;i<remotePort.length;i++)
            {
                Socket socket = null;
                try {
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort[i]));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String unique_id = msgs[2];

                String[] sendObject = new String[5];
                sendObject[0] =String.valueOf(a);
                sendObject[1]=remotePort[i];
                sendObject[2]=unique_id;
                sendObject[3]="second";
                sendObject[4]=failed_port;

                ObjectOutputStream oos = null;
                if(socket!=null)
                    try {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                if(oos!=null)
                    try {
                        oos.writeObject(sendObject);
                    } catch (IOException e) {
                        e.printStackTrace();
                        failed_port = remotePort[i];
                    }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(socket!=null)
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            return null;
        }
    }
}