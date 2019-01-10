import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by samansadeghyan on 11/15/16.
 */
public class Server {
    private final int PORT_NUM = 5000;
    private ServerSocket serverSocket;
    private Socket connectedClientSocket;
    private String path;
    private String info = "nullll";
    private HashMap<String, String> infoPair = new HashMap<String, String>();


    public Server(){
        try{
            //initiating stuff

            serverSocket = new ServerSocket(PORT_NUM);

            System.out.println("listening on " + InetAddress.getLocalHost().getHostAddress() + ":" + PORT_NUM);

        }catch(IOException e){
            e.printStackTrace();

        }

    }


    public void run() throws ParseException{
        try {
            connectedClientSocket = serverSocket.accept(); // The accept method waits until a client starts up and requests a connection

            System.out.println("client connected : " + connectedClientSocket.getRemoteSocketAddress());

            System.out.println(InetAddress.getLocalHost().getHostAddress());

        } catch (IOException e) {
            e.printStackTrace();
        }

        long lStartTime = 0;
        long lEndTime = 0;


        while(true) {

            System.out.println("choose whachu wanna do?");

            System.out.println("1. chat ");
            System.out.println("2. exchanging files");
            System.out.println("3. exit \n");

            lStartTime = new Date().getTime();


            Scanner scan = new Scanner(System.in);

            int userDecision = scan.nextInt();

            if (userDecision == 1) {

                System.out.println("currently is not available!");
                System.out.println("exited!");
                closeSocket();
                System.exit(0);

            } else if (userDecision == 2) {

                //------convension----------------
                // 1. send "file" message           // 1. receive "file" message
                // 2. receive "ok-file" message     // 2. send "ok-file" message
                // 3. send file info                // 3. receive file info
                // 4. receive "ok-dispatch"         // 4. send "ok-dispatch"


                System.out.println("enter the path please :");

                path = scan.next();

                File f = new File(path);

                if (!f.exists()) {
                    System.out.println("not valid path!");
                    continue;

                }

                sendingProcess(path);


            } else if (userDecision == 3) {
                System.out.println("exited!");
                closeSocket();
                System.exit(0);
            } else {
                System.out.println("wrong input!");
                closeSocket();
                System.exit(0);
            }

            lEndTime = new Date().getTime();
            long output = lEndTime - lStartTime;
            System.out.println("Elapsed time in milliseconds: " + output);
        }


    }


    public void sendMessage(String msg){

        try {

            DataOutputStream out = new DataOutputStream(connectedClientSocket.getOutputStream());
            out.writeUTF(msg);
            // java runtime closes automatically in and out streams

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String beAwareForMessage(){
        System.out.println("beAwareForMessage");
        while(true) {
            try {
                DataInputStream in = new DataInputStream(connectedClientSocket.getInputStream());
                if(in.available() > 0){ //Returns: an estimate of the number of bytes that can be read
                    String str = in.readUTF();

                    //System.out.println(str);
                    return str;
                }

//                // java runtime closes automatically in and out streams
//                //// TODO: 11/15/16 whether it is needed or not.
//                if(in != null){
//                    System.out.println("data received, (most likely)");
//                    break;
//                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    public void sendingProcess(String curPath){

        File f = new File(curPath);

        if (f.isDirectory()) {
            System.out.println(f.getName()+"---base file is folder");
            for (File item : f.listFiles()) {

                if(item.isDirectory()){
                    System.out.println(item.getName()+"---second file is a folder");
                    sendingProcess(curPath+"/"+item.getName());
                }else{
                    System.out.println(item.getName()+"---second file is a file");
                    if (!item.getName().equals(".DS_Store")) {
                        sendThisFile(curPath + "/" + item.getName());
                    }
                }
            }

        }else{
            System.out.println(f.getName()+"---base file is a file");
            sendThisFile(curPath);

        }


    }


    public void sendThisFile(String filePath){
        System.out.println("sendThisFile");

        File f = new File(filePath);
        sendMessage("file");

        String recMsg = beAwareForMessage();


        info = f.getName() + "---size_" + f.length() + "---last-modified_" + new Date(f.lastModified());

        // TODO: 11/17/16 infoPair
//        infoPair.put()

        sendMessage(info);

        recMsg = beAwareForMessage();

//        System.out.println("sending the file ::: "+info);

        String fileSize = info.split("---")[1];
        fileSize = fileSize.split("_")[1];

        System.out.println("Sending ("+ f.getName() + " ----***----- " + fileSize + " bytes)");



        try {

            byte[] mybytearray = new byte[8192];
            FileInputStream fis = new FileInputStream(f);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            OutputStream os;



            os = connectedClientSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(f.getName());
            dos.writeLong(Long.parseLong(fileSize));
            int read;
            int total = 0;


            while((read = dis.read(mybytearray)) != -1){
                dos.write(mybytearray, 0, read);
                total = total + read;

                if (total >= Integer.parseInt(fileSize)){
                    break;
                }
            }


            //dos.flush();

//        try {
//
//            byte [] myBuffer  = new byte [8192];
//
//            FileInputStream fileInputStream = new FileInputStream(f);
//            OutputStream outputStream = connectedClientSocket.getOutputStream();
//
//
//            int count;
//            int totalReadBytes = 0;
//
//            while ((count = fileInputStream.read(myBuffer)) != -1){
//                totalReadBytes = count + totalReadBytes;
//                System.out.println(String.valueOf(totalReadBytes));
//
//                fileInputStream.read(myBuffer, 0, count);
//
//                outputStream.write(myBuffer, 0, count);
//
//            }
//            outputStream.flush();

            System.out.println("Done!");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // TODO: 11/17/16 trying to close streams and others
            // it's really important to avoid serious resource leaks

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void closeSocket(){
        try {
            connectedClientSocket.close();
        } catch (IOException e) {
            System.out.println("couldn't close the socket!");
            e.printStackTrace();
        }
    }





}
