package com.classoft.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AdfTool {
  private static final int BUFFER_SIZE = 8192;

  public static void main(String[] args) {
    int operation = -1; // -1 = unset; 0 = encryption; 1 = decryption
    String inFilename = null, outFilename = null;
    if (args.length < 3) {
      System.out.println("Bad input! Run me this way:");
      System.out.println("java AdfTool -e inputMp3.mp3 outputAdf.adf");
      System.out.println("java AdfTool -d inputAdf.adf outputMp3.mp3");
      System.exit(1);
    }
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-e")) {
        operation = 0;
      } else if (args[i].equals("-d")) {
        operation = 1;
      }
    }
    if (operation == -1) {
      System.out.println("You need to specify the operation!");
      System.exit(1);
    }
    for (int i = 0; i < args.length; i++) {
      if (args[i].contains(".adf")) {
        if (operation == 0) {
          outFilename = args[i];
        } else if (operation == 1) {
          inFilename = args[i];
        }
      } else if (args[i].contains(".mp3")) {
        if (operation == 0) {
          inFilename = args[i];
        } else if (operation == 1) {
          outFilename = args[i];
        }
      }
    }
    if (inFilename == null || outFilename == null) {
      System.out.println("Couldn't figure out some of the filenames...");
      System.exit(1);
    }
    System.out.println("*************************************");
    System.out.println("Input: " + inFilename);
    System.out.println("Output: " + outFilename);
    System.out.println("Operation: " + (operation == 0 ? "encryption" : "decryption"));
    System.out.println("*************************************");
    if (operation == 1) {
      decrypt(inFilename, outFilename);
    } else if (operation == 0) {
      encrypt(inFilename, outFilename);
    }
  }

  public static FileInputStream safeOpen(String filename) {
    try {
      return new FileInputStream(filename);
    } catch (FileNotFoundException e) {
      System.out.println("Error opening file: " + filename);
      System.exit(1);
    }
    return null;
  }

  public static FileOutputStream safeWrite(String filename) {
    try {
      return new FileOutputStream(filename);
    } catch (FileNotFoundException e) {
      System.out.println("Error writing to file: " + filename);
      System.exit(1);
    }
    return null;
  }

  public static void encrypt(String inFilename, String outFilename) {
    try {
      FileInputStream inputFile = safeOpen(inFilename);
      FileOutputStream outputFile = safeWrite(outFilename);

      byte[] buffer = new byte[BUFFER_SIZE];
      long length = inputFile.available();
      int percentage = 0;
      long unit = length / 100;
      long currUnit = 0;

      for (int i = 0; i < length; i += BUFFER_SIZE) {
        int bytesRead = inputFile.read(buffer);
        for (int j = 0; j < bytesRead; j++) {
          buffer[j] = (byte) (buffer[j] ^ 34);
        }
        outputFile.write(buffer, 0, bytesRead);
        currUnit += bytesRead;
        if (currUnit >= unit) {
          System.out.printf("Encrypting %3d%%\r", ++percentage);
          currUnit = 0;
        }
      }

      inputFile.close();
      outputFile.close();
    } catch (IOException e) {
      System.out.println("Error in encryption: " + e.getMessage());
      System.exit(1);
    }
  }

  public static void decrypt(String inFilename, String outFilename) {
    try {
      FileInputStream inputFile = safeOpen(inFilename);
      FileOutputStream outputFile = safeWrite(outFilename);

      byte[] buffer = new byte[BUFFER_SIZE];
      long length = inputFile.available();
      int percentage = 0;
      long unit = length / 100;
      long currUnit = 0;

      for (int i = 0; i < length; i += BUFFER_SIZE) {
        int bytesRead = inputFile.read(buffer);
        for (int j = 0; j < bytesRead; j++) {
          buffer[j] = (byte) (buffer[j] ^ 34);
        }
        outputFile.write(buffer, 0, bytesRead);
        currUnit += bytesRead;
        if (currUnit >= unit) {
          System.out.printf("Decrypting %3d%%\r", ++percentage);
          currUnit = 0;
        }
      }

      inputFile.close();
      outputFile.close();
    } catch (IOException e) {
      System.out.println("Error in decryption: " + e.getMessage());
      System.exit(1);
    }
  }
}
