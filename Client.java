import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * Created by samansadeghyan on 11/15/16.
 */
public class Client {

    private Socket connectedServerSocket;
    private String serverIp = "192.168.1.55";
    private final int PORT_NUM = 5000;
    public int fileSize = 6022386;
    private String resultPath = null;
    private String[] parts;

    public Client(String ip){
        serverIp = ip;
        try {

            connectedServerSocket = new Socket(serverIp, PORT_NUM);
            System.out.println("Connected to server : " + connectedServerSocket.getRemoteSocketAddress());

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public void run(){

        long lStartTime = 0;
        long lEndTime = 0;

        while (true) {

            lStartTime = new Date().getTime();

            System.out.println("running and currently waiting for server requests...");

            String recMsg = beAwareForMessage();

            if (recMsg.equals("file")) {
                System.out.println("server wanted to send a file :");
                sendMessage("ok-file");
                recMsg = beAwareForMessage();

                parts = recMsg.split("---");

                createResultDir();
                sendMessage("ok-dispatch");
                receiveFile();

            } else {
                System.out.println("problem in receiving message");
                System.out.println(recMsg);

            }

            lEndTime = new Date().getTime();
            long output = lEndTime - lStartTime;
            System.out.println("Elapsed time in milliseconds: " + output);
        }

    }



    public void sendMessage(String msg){
        try {

            DataOutputStream out = new DataOutputStream(connectedServerSocket.getOutputStream());
            out.writeUTF(msg);
            // java runtime closes automatically in and out streams

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String beAwareForMessage(){
        while(true) {
            try {
                DataInputStream in = new DataInputStream(connectedServerSocket.getInputStream());
                if(in.available() > 0){ //Returns: an estimate of the number of bytes that can be read
                    String str = in.readUTF();

                    //System.out.println(str);
                    return str;
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "fuck";
            }

        }

    }


    public void createResultDir(){
        File dir = new File("inbox");
        boolean success = dir.mkdir();

        if(!success){
            System.out.println("inbox folder hasn't been created!");
            resultPath = System.getProperty("user.dir") + "/inbox";
        }else{
            resultPath = System.getProperty("user.dir") + "/inbox";

        }
    }


    public void receiveFile(){

        if(resultPath == null){
            resultPath = System.getProperty("user.dir");
        }
        String[] s = parts[1].split("_");
        fileSize = Integer.parseInt(s[1]);

        int bytesRead;
        InputStream in;
        int bufferSize=0;

        try {
            bufferSize = connectedServerSocket.getReceiveBufferSize();
            in= connectedServerSocket.getInputStream();
            DataInputStream clientData = new DataInputStream(in);
            String fileName = clientData.readUTF();
            Long fileSize = clientData.readLong();
            System.out.println(fileName +"-----size::::"+ String.valueOf(fileSize));
            OutputStream output = new FileOutputStream(resultPath+"/"+parts[0]);

            byte[] buffer = new byte[bufferSize];
            int read;
            int total = 0;
            while((read = clientData.read(buffer)) != -1){

                output.write(buffer, 0, read);
                total = total + read;
                if (total >= fileSize ){
                    break;
                }
            }






//        try {
//
//            byte [] myBuffer  = new byte [8192];
//            InputStream InputStream = connectedServerSocket.getInputStream();
//            FileOutputStream fileOutputStream = new FileOutputStream(resultPath+"/"+parts[0]);
//
//
////            read(byte[] b, int offset, int len)
//
//
//            int len;
//            int totalReadBytes = 0;
//
//            while ((len = InputStream.read(myBuffer)) > 0){
//                totalReadBytes = len + totalReadBytes;
//                System.out.println(String.valueOf(totalReadBytes));
//                if (totalReadBytes == fileSize){
//                    break;
//                }
//
//                InputStream.read(myBuffer, 0, len);
//
//                fileOutputStream.write(myBuffer, 0, len);
//
//            }
//            fileOutputStream.flush();




            System.out.println("File " + resultPath + " downloaded (" + "lllll" + " bytes read)");






//            byte [] myByteArray  = new byte [fileSize];
//            InputStream inputStream = connectedServerSocket.getInputStream();
//            FileOutputStream fileOutputStream = new FileOutputStream(resultPath+"/"+parts[0]);
//            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
//            int bytesRead = inputStream.read(myByteArray,0,myByteArray.length);
//            int current = bytesRead;
//
//            do {
//                bytesRead = inputStream.read(myByteArray, current, (myByteArray.length-current));
//                if(bytesRead >= 0) current += bytesRead;
//            } while(bytesRead > 0);
//
//
//            bufferedOutputStream.write(myByteArray, 0 , current);
//            bufferedOutputStream.flush();
//
//
//
//            System.out.println("File " + resultPath + " downloaded (" + current + " bytes read)");




        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void closeSocket(){
        try {
            connectedServerSocket.close();
        } catch (IOException e) {
            System.out.println("couldn't close the socket!");
            e.printStackTrace();
        }
    }




}







