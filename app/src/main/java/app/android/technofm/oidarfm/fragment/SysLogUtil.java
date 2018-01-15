package app.android.technofm.oidarfm.fragment;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Asus on 1/9/2018.
 */
public class SysLogUtil {
    // Priorities.
    public static final int LOG_EMERG = 0; // system is unusable
    public static final int LOG_ALERT = 1; // action must be taken immediately
    public static final int LOG_CRIT = 2; // critical conditions
    public static final int LOG_ERR = 3; // error conditions
    public static final int LOG_WARNING = 4; // warning conditions
    public static final int LOG_NOTICE = 5; // normal but significant condition
    public static final int LOG_INFO = 6; // informational
    public static final int LOG_DEBUG = 7; // debug-level messages
    public static final int LOG_PRIMASK = 0x0007; // mask to extract priority

    // Facilities.
    public static final int LOG_KERN = (0<<3); // kernel messages
    public static final int LOG_USER = (1<<3); // random user-level messages
    public static final int LOG_MAIL = (2<<3); // mail system
    public static final int LOG_DAEMON = (3<<3); // system daemons
    public static final int LOG_AUTH = (4<<3); // security/authorization
    public static final int LOG_SYSLOG = (5<<3); // internal syslogd use
    public static final int LOG_LPR = (6<<3); // line printer subsystem
    public static final int LOG_NEWS = (7<<3); // network news subsystem
    public static final int LOG_UUCP = (8<<3); // UUCP subsystem
    public static final int LOG_CRON = (15<<3); // clock daemon
    // Other codes through 15 reserved for system use.
    public static final int LOG_LOCAL0 = (16<<3); // reserved for local use
    public static final int LOG_LOCAL1 = (17<<3); // reserved for local use
    public static final int LOG_LOCAL2 = (18<<3); // reserved for local use
    public static final int LOG_LOCAL3 = (19<<3); // reserved for local use
    public static final int LOG_LOCAL4 = (20<<3); // reserved for local use
    public static final int LOG_LOCAL5 = (21<<3); // reserved for local use
    public static final int LOG_LOCAL6 = (22<<3); // reserved for local use
    public static final int LOG_LOCAL7 = (23<<3); // reserved for local use

    public static final int LOG_FACMASK = 0x03F8; // mask to extract facility

    // Option flags.
    public static final int LOG_PID = 0x01; // log the pid with each message
    public static final int LOG_CONS = 0x02; // log on the console if errors
    public static final int LOG_NDELAY = 0x08; // don't delay open
    public static final int LOG_NOWAIT = 0x10; // don't wait for console forks


    private static final int PORT = 514;
    private static final String TAG = "SysLog";

    private static String  ident;
    private static int   logopt;
    private static int   facility;

    private static InetAddress address;
    private static DatagramPacket packet;
    private static DatagramSocket socket;


    /// Creating a Syslog instance is equivalent of the Unix openlog() call.
    // @exception SyslogException if there was a problem
    public SysLogUtil(String ident, int logopt, int facility ) throws SyslogException
    {
        if ( ident == null )
            this.ident = new String( Thread.currentThread().getName() );
        else
            this.ident = ident;
        this.logopt = logopt;
        this.facility = facility;
    }

    /// Use this method to log your syslog messages. The facility and
    // level are the same as their Unix counterparts, and the Syslog
    // class provides constants for these fields. The msg is what is
    // actually logged.
    // @exception SyslogException if there was a problem
    public static void sendSyslog( boolean dock,String ident, int logopt, int facility,int priority, String msg ,String mClass) throws SyslogException
    {

        Log.d(TAG, "Syslog msg "+msg);
        try {
            if(address == null)
                address = InetAddress.getByName("log server"); // log server - server address
        } catch (UnknownHostException e) {
            e.printStackTrace();
            address = null;
        }
        if ( ident == null )
            ident = new String( Thread.currentThread().getName() );

        int  pricode;
        int  length;
        int  idx, sidx, nidx;
        StringBuffer buffer;
        byte[]  data;
        byte[]  numbuf = new byte[32];
        String  strObj;

        pricode = MakePriorityCode( facility, priority );
        Integer priObj = new Integer( pricode );

        length = 4 + ident.length() + msg.length() + 1;
        length += ( pricode > 99 ) ? 3 : ( ( pricode > 9 ) ? 2 : 1 );

        data = new byte[length];

        idx = 0;
        data[idx++] = '<';

        strObj = priObj.toString( priObj.intValue() );
        strObj.getBytes(0, strObj.length(), data, idx );
        idx += strObj.length();

        data[idx++] = '>';

        ident.getBytes(0, ident.length(), data, idx );
        idx += ident.length();

        data[idx++] = ':';
        data[idx++] = ' ';

        msg.getBytes( 0, msg.length(), data, idx );
        idx += msg.length();

        data[idx] = 0;
        try
        {
            socket = new DatagramSocket();
        }
        catch (SocketException e )
        {
            throw new SyslogException(
                    "error creating syslog udp socket: " + e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new SyslogException(
                    "error creating syslog udp socket: " + e.getMessage() );
        }

        packet = new DatagramPacket( data, length, address, PORT );

        try
        {
            if(socket != null)
                socket.send(packet);
        }
        catch ( IOException e )
        {
            throw new SyslogException(
                    "error sending message: '" + e.getMessage() + "'" );
        }
    }
    private static int MakePriorityCode( int facility, int priority )
    {
        return ( ( facility & LOG_FACMASK ) | priority );
    }
}