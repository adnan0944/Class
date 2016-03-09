
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
            s = new Socket("localhost", 5555);

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
            if(strSend.equals("GetList")){
                try{
                    
                }catch(Exception e){
                    System.out.println("Could not get list");
                }
            }
            if (strSend.equals("BYE")) {
                System.out.println("Client wishes to terminate the connection. Exiting main.");
                f1 = false;
            }
            if (strSend.equals("DL")) {

                try {
                    strRecv = br.readLine();					//These two lines are used to determine
                    int filesize = Integer.parseInt(strRecv);		//the size of the receiving file
                    byte[] contents = new byte[10000];

                    FileOutputStream fos = new FileOutputStream("C:\\Users\\User\\Desktop\\capture1.png");
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    InputStream is = s.getInputStream();

                    int bytesRead = 0;
                    int total = 0;			//how many bytes read

                    while (total <= filesize) //loop is continued until received byte=totalfilesize
                    {
                        bytesRead = is.read(contents);
                        total += bytesRead;
                        bos.write(contents, 0, bytesRead);
                    }
                    bos.flush();
                } catch (Exception e) {
                    System.err.println("Could not transfer file.");
                }

            }
            try {
                strRecv = br.readLine();
                if (strRecv != null) {
                    System.out.println("Server says: " + strRecv);
                } else {
                    System.err.println("Error in reading from the socket. Exiting main.");
                    break;
                }
            } catch (Exception e) {
                System.err.println("Error in reading from the socket. Exiting main.");
                break;
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
