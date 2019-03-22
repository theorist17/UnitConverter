import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.*;


public class converter {

   public static int countLines(File aFile) throws IOException {
       LineNumberReader reader = null;
       try {
           reader = new LineNumberReader(new FileReader(aFile));
           while ((reader.readLine()) != null);
           return reader.getLineNumber();
       } catch (Exception ex) {
           return -1;
       } finally { 
           if(reader != null) 
               reader.close();
       }
   }
   
   static String[][] readUnitFile(String path) {
      File file = new File(path);
      int lineNo = 0;
      try {
         lineNo = countLines(file);
      } catch (IOException e1) {
         e1.printStackTrace();
      }
      String[][] array = new  String [lineNo][4];
      
      try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));){
         String line = null;
         int i = 0;
         while((line = br.readLine())!=null) {
            array[i++] = line.split("\t");
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return array;
   }
   
   static String[][] readPrefixFile(String path) {
      File file = new File(path);
      int lineNo = 0;
      try {
         lineNo = countLines(file);
      } catch (IOException e1) {
         e1.printStackTrace();
      }
      String[][] array = new  String [lineNo][2];
      
      try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));){
         String line = null;
         int i = 0;
         while((line = br.readLine())!=null) {
            array[i++] = line.split("\t");
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return array;
   }
   
   public static String[] getUnitName(String[][]unit) {
      String [] array = new String[unit.length];
      for(int i = 0 ; i <unit.length;i++) {
         array[i] = unit[i][0];
      }
      return array;
   }
   
   public static String[] parseInputUnit(String line) {
      String[] strings= line.split("\\*");
      return strings;
   }

   public static int matching(String s, String[] unit) {
      int matchedAlphabet = 0;
      String reversed = new StringBuilder(s).reverse().toString(); 
      
      for(int i = 0 ; i < unit.length; i++) {
         String reversed2 = new StringBuilder(unit[i]).reverse().toString();
         for(int j = 0; j < unit[i].length(); j ++) {
            if(j < s.length()) {
               if(reversed.charAt(j) == reversed2.charAt(j)){
                  matchedAlphabet++;
               } }
         }
         if(matchedAlphabet == unit[i].length()) {
            return matchedAlphabet;
         } matchedAlphabet = 0; 
      }

      return -1;
   }
   public static void main(String[] args) {
      
      String[][] unit = readUnitFile("unit.txt");
      String[][] prefix = readPrefixFile("prefix.txt");
      String[] unitNames = getUnitName(unit);
      
      while(true) {
         
         Scanner scan= new Scanner(System.in);
         String[] twoline = scan.nextLine().split("\\?");
         String line = twoline[0];
         String target = twoline[1];
         
         int magnitude = 0;
         
         String reversed = new StringBuilder(line).reverse().toString();
         for(int i = 0 ; i < line.length(); i++) {
            
            if(line.charAt(i) < '0' || '9' < line.charAt(i)) {
               
               magnitude = Integer.parseInt(new StringBuilder(reversed.substring(line.length()-i)).reverse().toString());
               line = line.substring(i);
               break;
            }
         }
         
         print(line, unitNames, magnitude);
         print(target, unitNames, magnitude);
      }
      
   }

   static void print(String line, String[] unitNames, int magnitude) {
      String[] inputUnits = parseInputUnit(line);
      StringBuilder prefixInput = new StringBuilder();
      StringBuilder unitInput = new StringBuilder();
      int indexFound = 0;
      for(int i = 0; i<inputUnits.length;i++) {
         indexFound = matching(inputUnits[i], unitNames);
         if(indexFound > 0) {
            String str = new StringBuilder(inputUnits[i]).reverse().substring(0,indexFound).toString();
            str = new StringBuilder(str).reverse().toString();
            unitInput.append(str + ",");
            
            int temp = inputUnits[i].length() - indexFound;
            String str2 = new StringBuilder(inputUnits[i]).substring(0,temp).toString();
            if(str2!=null) {
               prefixInput.append(str2);
            }
         }
      }
      
      System.out.println("magnitude : " +magnitude);
      System.out.println("total prefix : "+ prefixInput);
      System.out.println("total unit : "+ unitInput);
   }
} // prefix append 전에 생성된 prefix가 txt에 존재하는 것인지도 판단해야할듯??