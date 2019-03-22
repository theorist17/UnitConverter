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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
		int index = -1;
		int matchedAlphabet = 0;
		
		for (int i = 0; i < s.length(); i++) {
			for(int j = 0 ; j < unit.length; j++) {
				for(int k = 0; k < unit[j].length(); k++) {
					
					if(s.charAt(i) == unit[j].charAt(k)) {
						matchedAlphabet++;
						if (k==0)
							index = i;
						if(matchedAlphabet == unit[j].length())
							return index;
					}					
				}
			}
		}
		
		return index;
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
		for(int i = 0; i<inputUnits.length;i++) {
			int indexFound = matching(inputUnits[i], unitNames);
			if(indexFound > -1) {
				unitInput.append(inputUnits[i].substring(indexFound)+", ");
				String str = new StringBuilder(inputUnits[i]).reverse().substring(inputUnits[i].length()-indexFound).toString();
				str = new StringBuilder(str).reverse().toString();
				if(str!=null) {
					prefixInput.append(str);
				}
			}
		}
		
		System.out.println("magnitude : " +magnitude);
		System.out.println("total prefix : "+ prefixInput);
		System.out.println("total unit : "+ unitInput);
	}
}

// 나누기처리해~~ 지수승 계산~~ 차원카운팅 ~~

