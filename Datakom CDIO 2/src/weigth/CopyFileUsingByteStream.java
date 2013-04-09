package weigth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
public class CopyFileUsingByteStream {
    public static void main(String args[]) { 
        if (args.length !=2) {
            System.out.println(
                    "Usage: java CopyFileUsingByteStream fromfile tofile");
            System.exit(0);
        }
        copyFile(args[0], args[1]);
    }
    public static boolean copyFile(String fileIn, String fileOut) {
        return copyFile(new File(fileIn), new File(fileOut));
    }
    public static boolean copyFile(File inFile, File outFile) {
        // Uncomment this if you dont know where you are...
        System.out.println(inFile.getAbsoluteFile().getParent());
        FileInputStream fis = null; FileOutputStream fos = null;
        Byte inByte;
        boolean succesfull = false;
        try {
            // 1 checking that the input file is not a directory
            if(inFile.isDirectory()) {
                System.out.println("Error: The input file is a directory");
                return false;
            }
            // 2 checking the input file exist
            if (! inFile.exists()) {
                System.out.println("Error: The input file findes ikke");
                return false;
            }
            // 3 if outfile is a directory, append infile name to outfile path.
            if(outFile.isDirectory())
                outFile = new File(outFile, inFile.getName());
            
            // 4 if outfile exist ask for confirmation before overwrite.
            if (outFile.exists()) {
                System.out.println("file "+outFile.getName()+" already exists overwrite?(y/n): ");
                if(System.in.read()!=(int)'y')
                    return false;
            } 
            else { //if not check that the parent exist and prompt user for making directorys.
                File parent = outFile.getParentFile();
                if(parent != null && !parent.exists()) {
                    System.out.println("The directory/ -s " + outFile.getParent() + " does not exist, create?(y/n): ");
                    if(System.in.read()==(int)'y')
                        parent.mkdirs();
                    else {
                        System.out.println("Could not create file without directory: " + outFile.getAbsolutePath());
                        return false;
                    }
                }
            }
            //open the streams, these should never be open for longer than nescesary
            fis = new FileInputStream(inFile);
            fos = new FileOutputStream(outFile);
            /**
             * continuously read a byte from fis and write it to fos
             * notice the difference from the slides, here we check that the inByte read is
             * different from EOF, BEFORE we write it to the fos.
            */
            while ((inByte = (byte)fis.read()) != -1) {
                fos.write(inByte);
            }
            succesfull=true;
            
        } catch (FileNotFoundException e) {
            System.out.println("File not found: "+e.getMessage());
            succesfull=false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            succesfull=false;
        } finally {
            try { //close files
                if (fis != null) fis.close();
                if (fos != null) fos.close();
            } catch (IOException e) {}
        }
        return succesfull;
    }
}