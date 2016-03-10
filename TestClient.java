
import java.io.*;
import java.net.*;
import java.util.*;

public class TestClient {

    private static Socket s = null;
    private static BufferedReader br = null;
    private static PrintWriter pr = null;

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

    public static void main(String args[]) {
        try {
            s = new Socket("localhost", 5000);

            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pr = new PrintWriter(s.getOutputStream());
        } catch (Exception e) {
            System.err.println("Problem in connecting with the server. Exiting main.");
            System.exit(1);
        }

        Scanner input = new Scanner(System.in);
        String strSend = null, strRecv = null;
        boolean f1 = true, f2 = true;
        File direc = new File("C:\\Users\\User\\Desktop\\shared");
        //int SOCKET_PORT = 13267;
        Socket clientSocket = null;
        try {
            strRecv = br.readLine();
            if (strRecv != null) {
                System.out.println("Server says: " + strRecv);
            } else {
                System.err.println("Error in reading from the socket. Exiting main.");
                cleanUp();
                System.exit(0);
            }
        } catch (Exception e) {
            System.err.println("Error in reading from the socket. Exiting main.");
            cleanUp();
            System.exit(0);
        }

        while (f1) {
            while (f2) {
                System.out.print("Enter Username: ");
                try {
                    strSend = input.nextLine();
                } catch (Exception e) {
                    continue;
                }
                pr.println(strSend);
                pr.flush();

                System.out.print("Enter PassWord: ");
                try {
                    strSend = input.nextLine();
                } catch (Exception e) {
                    continue;
                }
                pr.println(strSend);
                pr.flush();
                try {
                    String check = br.readLine();
                    if (check.equals("ok")) {
                        f2 = false;
                        for (File file : direc.listFiles()) {
                            if (file.isFile()) {
                                pr.println(file.getName());
                                pr.flush();
                                pr.println(file.length());
                                pr.flush();
                            }
                        }
                        pr.println("Hello");
                        pr.flush();
                        
                        clientSocket = new Socket("localhost", 3248);
                    } else if (check.equals("not ok")) {
                        System.out.println("Wrong Username or Password");
                    }
                } catch (Exception e) {
                    continue;
                }

            }
            System.out.print("Enter a string: ");
            try {
                strSend = input.nextLine();
            } catch (Exception e) {
                continue;
            }

            pr.println(strSend);
            pr.flush();
            System.out.println("baicha asi");
            if (strSend.equals("GetList")) {
                try {
                    System.out.println("List of files");
                    System.out.println("fileName    Size     users");
                    while ((strRecv = br.readLine()) != null) {
                        if (strRecv.equals("hello")) {
                            break;
                        }
                        System.out.println(strRecv);

                    }

                } catch (Exception e) {
                    System.out.println("Could not get list");
                }
                continue;

            } else if (strSend.equals("BYE")) {
                System.out.println("Client wishes to terminate the connection. Exiting main.");
                f1 = false;
                continue;
            } else if (strSend.equals("DL")) {
                System.out.println("ekhono baicha asi");
                //String serverIP = "127.0.0.1";
                //int serverPort = 3248;
                String fileOutput = "C:\\Users\\User\\Desktop\\capture1.png";

                byte[] aByte = new byte[1];
                int bytesRead;

                
                InputStream is = null;

                try {
                    
                    is = clientSocket.getInputStream();
                } catch (IOException ex) {
                    // Do exception handling
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                if (is != null) {

                    FileOutputStream fos = null;
                    BufferedOutputStream bos = null;
                    try {
                        fos = new FileOutputStream(fileOutput);
                        bos = new BufferedOutputStream(fos);
                        bytesRead = is.read(aByte, 0, aByte.length);

                        do {
                            baos.write(aByte);
                            bytesRead = is.read(aByte);
                        } while (bytesRead != -1);

                        bos.write(baos.toByteArray());
                        bos.flush();
                        bos.close();
                        //clientSocket.close();
                        //strRecv=br.readLine();
                        //System.out.println(strRecv);
                        //String line=br.readLine();
                        
                    } catch (IOException ex) {
                        // Do exception handling
                    }
                   
                    
                }
               
                System.out.println("Client e boisha nai");
                //continue;
            } else {
                try {
                    strRecv = br.readLine();
                    if (strRecv != null) {
                        System.out.println("Server says2: " + strRecv);
                    } else {
                        System.err.println("Error in reading from the socket. Exiting main.");
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Error in reading from the socket. Exiting main.");
                    break;
                }
            }

        }

        cleanUp();
    }

    private static void cleanUp() {
        try {
            br.close();
            pr.close();
            s.close();
        } catch (Exception e) {

        }
    }
}
