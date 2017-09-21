package edu.buffalo.cse.cse486586.groupmessenger2;

/**
 * Created by nikita on 3/22/17.
 */

public class HoldBackQueue {
    int mid;
    boolean isDeliverable;
    int seq_no;
    int port_no;

    public HoldBackQueue(int m, boolean isDel, int s_no, int p_no){
        mid=m;
        isDeliverable=isDel;
        seq_no=s_no;
        port_no = p_no;
    }

    public int getMid(){
        return mid;
    }

    public boolean isDeliverable(){
        return isDeliverable;
    }

    public int getSeq_no(){
        return seq_no;
    }

    public int getPort_no(){
        return port_no;
    }
}
