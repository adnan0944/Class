
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TestServer {

    public static int workerThreadCount = 0;

    public static void main(String args[]) {
        int id = 1;

        try {
            ServerSocket ss = new ServerSocket(5000);
            System.out.println("Server has been started successfully.");

            while (true) {
                Socket s = ss.accept();		//TCP Connection
                WorkerThread wt = new WorkerThread(s, id);
                Thread t = new Thread(wt);
                t.start();
                workerThreadCount++;
                System.out.println("Client [" + id + "] is now connected. No. of worker threads = " + workerThreadCount);
                id++;
            }
        } catch (Exception e) {
            System.err.println("Problem in ServerSocket operation. Exiting main.");
        }
    }
}

class WorkerThread implements Runnable {

    private Socket socket;
    private InputStream is;
    private OutputStream os;
    ServerSocket welcomeSocket = null;
    Socket connectionSocket = null;
    
    private int id = 0;

    public void removeLineFromFile(String file, String lineToRemove) {

        try {

            File inFile = new File(file);

            if (!inFile.isFile()) {
                System.out.println("Parameter is not an existing file");
                return;
            }

            //Construct the new file that will later be renamed to the original filename. 
            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            //Read from the original file and write to the new 
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {

                if (!line.trim().equals(lineToRemove)) {

                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            //Delete the original file
            if (!inFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inFile)) {
                System.out.println("Could not rename file");
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void listFiles(String directoryName) {
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                System.out.println(file.getName());
            }
        }
    }

    public WorkerThread(Socket s, int id) {
        this.socket = s;

        try {
            this.is = this.socket.getInputStream();
            this.os = this.socket.getOutputStream();
        } catch (Exception e) {
            System.err.println("Sorry. Cannot manage client [" + id + "] properly.");
        }

        this.id = id;
    }

    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(this.is));
        PrintWriter pr = new PrintWriter(this.os);

        pr.println("Your id is: " + this.id);
        pr.flush();

        String str;
        String user = null, pass;
        String line = null;
        //ArrayList<Data> dataArr=new ArrayList<Data>();

        String fileName1 = "C:\\Users\\User\\Desktop\\login.txt";

        String fileName2 = "C:\\Users\\User\\Desktop\\active.txt";
        boolean f1 = true, f2 = true;
        while (f1) {
            try {
                while (f2) {
                    str = br.readLine();
                    user = str;
                    // System.out.println(user);
                    pass = br.readLine();
                    //System.out.println(pass);
                    FileReader fileReader = new FileReader(fileName1);

                    boolean ok = false;
                    String line2 = null;
                    BufferedReader bufferedReader
                            = new BufferedReader(fileReader);

                    while ((line = bufferedReader.readLine()) != null) {
                        //System.out.println(line);
                        //break;
                        if (line.equals(user)) {
                            line2 = bufferedReader.readLine();
                            if (line2.equals(pass)) {
                                System.out.print(user);
                                System.out.println(" has logged in");

                                // Assume default encoding.
                                FileWriter fileWriter
                                        = new FileWriter(fileName2, true);

                                // Always wrap FileWriter in BufferedWriter.
                                BufferedWriter bufferedWriter
                                        = new BufferedWriter(fileWriter);

                                bufferedWriter.write(user);
                                bufferedWriter.newLine();
                                ok = true;
                                pr.println("ok");
                                pr.flush();
                                bufferedWriter.close();

                                break;
                            }

                        }
                    }

                    // Always close files.
                    bufferedReader.close();
                    /*if (user.equals("adnan") && pass.equals("0944")) {
                     System.out.println("Login Successful");
                     pr.println("ok");
                     pr.flush();
                     break;
                     }*/
                    int c = 0;
                    String temp = null;

                    if (ok) {
                        while ((str = br.readLine()) != null) {
                            c++;
                            //System.out.println(str);
                            if (str.equals("Hello")) {
                                break;
                            }

                            if (c == 2) {
                                c = 0;
                                int y = Integer.parseInt(str);
                                Data var = new Data(temp, y, user);
                                //dataArr[dataSize++]=var;
                                boolean flag = true;
                                for (int i = 0; i < Globals.dataSize; i++) {
                                    if (Globals.dataArr[i].fileName.equals(temp)) {
                                        Globals.dataArr[i].arr.add(user);
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    Globals.dataArr[Globals.dataSize++] = var;
                                }

                            }
                            temp = str;
                        }
                        f2 = false;
                        welcomeSocket = new ServerSocket(3248);
                        connectionSocket = welcomeSocket.accept();
                        break;

                    } else {
                        pr.println("not ok");
                        pr.flush();
                    }
                }
                /*System.out.println("Contents of Data Array");
                 for (int i = 0; i < Globals.dataSize; i++) {
                 line =null;
                 System.out.print(Globals.dataArr[i].fileName);
                 System.out.print(Globals.dataArr[i].size);
                 System.out.println(Globals.dataArr[i].arr);
                 }
                 */
                String prev=null,garbage=null;
                System.out.println("mori nai");
                //if(prev.equals("DL"))garbage=br.readLine();
                if ((str = br.readLine()) != null) {
                    System.out.println(str);
                    System.out.println("Ekhono mori nai");
                    if (str.equals("BYE")) {
                        System.out.println("[" + id + "] says: BYE. Worker thread will terminate now.");
                        removeLineFromFile(fileName2, user); // removing user from active list
                        for (int i = 0; i < Globals.dataSize; i++) {
                            Globals.dataArr[i].arr.remove(user);
                        }
                        f1 = false; // terminate the loop; it will terminate the thread also
                    } else if (str.equals("GetList")) {

                        //line.concat(String.valueOf(Globals.dataArr[0].size));
                        //StringBuilder sb = new StringBuilder();
                        //sb.append("");
                        //sb.append(Globals.dataArr[0].size);
                        for (int i = 0; i < Globals.dataSize; i++) {
                            line = null;
                            line = Globals.dataArr[i].fileName;
                            line = line.concat("  ");
                            String strI = Integer.toString(Globals.dataArr[i].size);
                            line = line.concat(strI);
                            line = line.concat("  ");
                            //System.out.println(line);
                            //System.out.println(Globals.dataArr[0].size);
                            for (String j : Globals.dataArr[i].arr) {
                                line = line.concat(j);
                                line = line.concat(" ");
                            }
                            pr.println(line);
                            pr.flush();
                            //pr.println(Globals.dataArr[0].arr);
                            //pr.flush();
                        }
                        pr.println("hello");
                        pr.flush();

                    } else if (str.equals("DL")) {
                        
                        BufferedOutputStream outToClient = null;
                        String fileToSend="C:\\Users\\User\\Desktop\\capture.png";
                        try {
                            
                            outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());
                        } catch (IOException ex) {
                            // Do exception handling
                        }
                        System.out.println("valo hoiya jao");
                        if (outToClient != null) {
                            File myFile = new File(fileToSend);
                            byte[] mybytearray = new byte[(int) myFile.length()];

                            FileInputStream fis = null;

                            try {
                                fis = new FileInputStream(myFile);
                            } catch (FileNotFoundException ex) {
                                // Do exception handling
                            }
                            BufferedInputStream bis = new BufferedInputStream(fis);

                            try {
                                bis.read(mybytearray, 0, mybytearray.length);
                                outToClient.write(mybytearray, 0, mybytearray.length);
                                System.out.println("ei j eikhane ami");
                                outToClient.flush();
                                outToClient.close();
                                //connectionSocket.close();
                                System.out.println("ei j eikhane ami2");
                                // File sent, exit the main method
                                //return;
                            } catch (IOException ex) {
                                // Do exception handling
                            }
                            
                        }

                        //pr.println("Downloaded.");
                        //pr.flush();
                       //line=br.readLine();
                       
                       

                    } else {
                        System.out.println("[" + id + "] says: " + str);
                        pr.println("Got it. You sent \"" + str + "\"");
                        pr.flush();
                    }
                } else {
                    System.out.println("[" + id + "] terminated connection. Worker thread will terminate now.");
                    break;
                }
            } catch (Exception e) {
                System.err.println("Problem in communicating with the client [" + id + "]. Terminating worker thread.");
                break;
            }
        }

        try {
            this.is.close();
            this.os.close();
            this.socket.close();
        } catch (Exception e) {

        }

        TestServer.workerThreadCount--;
        System.out.println("Client [" + id + "] is now terminating. No. of worker threads = "
                + TestServer.workerThreadCount);

    }

    private void delay(int p) {
        int k = 0;
        while (k < p) {
            k++;
        }
    }

}
