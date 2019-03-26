import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.*;
import java.util.regex.Pattern;

public class converter {

	public static int countLines(File aFile) throws IOException {
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new FileReader(aFile));
			while ((reader.readLine()) != null)
				;
			return reader.getLineNumber();
		} catch (Exception ex) {
			return -1;
		} finally {
			if (reader != null)
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
		String[][] array = new String[lineNo][4];

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));) {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
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
		String[][] array = new String[lineNo][2];

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));) {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				array[i++] = line.split("\t");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return array;
	}

	public static String[] getUnitName(String[][] unit) {
		String[] array = new String[unit.length];
		for (int i = 0; i < unit.length; i++) {
			array[i] = unit[i][0];
		}
		return array;
	}

	/**
	 * @param target
	 * @param candidates
	 * @return
	 */
	public static int isUnit(String target, String[] candidates) {
		int matchedAlphabet = 0;
		String reversed = new StringBuilder(target).reverse().toString();

		for (int i = 0; i < candidates.length; i++) {
			String reversed2 = new StringBuilder(candidates[i]).reverse().toString();
			for (int j = 0; j < candidates[i].length(); j++) {
				if (j < target.length()) {
					if (reversed.charAt(j) == reversed2.charAt(j)) {
						matchedAlphabet++;
					}
				}
			}
			if (matchedAlphabet == candidates[i].length()) {
				return matchedAlphabet;
			}
			matchedAlphabet = 0;
		}

		return -1;
	}
	
	public static String[] deliminateUnit(String side) {

		StringBuilder sb = new StringBuilder(side);
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '/') {
				sb.setCharAt(i, '*');
			}
		}
		String[] strings = sb.toString().split("\\*");
		return strings;
	}
	
	static String parseConstant(String side) {
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < side.length(); i++) {
			if(side.charAt(i)<='9'&&side.charAt(i)>='0') {
				sb.append(side.charAt(i));
			}
		}
		return sb.toString();
	}
	
	static String parseUnit(String side) {
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < side.length(); i++) {
			if(Pattern.matches("\\^{1}((-?[0-9]{1})|(\\(-?[0-9]{1}\\)))", side.substring(i)))
				
		}
		
		return sb.toString();
	}
	public static int parseDimension(String unit) {
		// if contains ^
		if (unit.contains("^")) {
			String substring = unit.substring(unit.indexOf("^"));
			int beginIndex = 0, endIndex = 0;

			// if contains regex
			if (Pattern.matches("\\^{1}((-?[0-9]{1})|(\\(-?[0-9]{1}\\)))", substring)) {
				for (int i = 0; i < unit.length(); i++) {
					if (('0' <= unit.charAt(i) && unit.charAt(i) <= '9') || unit.charAt(i) == '-') {
						if (beginIndex == 0)
							beginIndex = i;
						endIndex = i;
					}
				}
				return Integer.parseInt(unit, beginIndex, endIndex + 1, 10);
				// if not regex, an error
			} else {
				return -1;
			}
			// if nothing
		} else {
			return 1;
		}
	}
	public static void main(String[] args) {

		String[][] unit = readUnitFile("unit.txt");
		String[][] prefixs = readPrefixFile("prefix.txt");
		String[] units = getUnitName(unit);

//while (true){
		Scanner scan = new Scanner(System.in);
		String input = scan.nextLine().replaceAll(" ", "");
		String[] equation = input.split("\\=");
		String left = equation[0];
		String right = equation[1];

		System.out.println("left : "+left);
		System.out.println("right : "+right);
		convert(left.replaceAll("[0-9]", ""), units, parseConstant(left));
		convert(right.replaceAll("[0-9]", ""), units, parseConstant(right));
//}
		scan.close();
	}

	static void convert(String line, String[] units, String constant) {
		
		String[] word = deliminateUnit(line);

		int[] dimension = new int[word.length];
		StringBuilder prefix = new StringBuilder();
		StringBuilder unit = new StringBuilder();

		int indexFound = 0;
		for (int i = 0; i < word.length; i++) {
			indexFound = isUnit(word[i], units);

			// if not a unit, error
			if (indexFound < 0)
				return;

			// if regex fail, error
			dimension[i] = parseDimension(word[i]);
			if (dimension[i] < 0)
				return;
			System.out.print(dimension[i] + ", ");

			// print unit and prefix for each corresponding words
			String str = new StringBuilder(word[i]).reverse().substring(0, indexFound).toString();
			str = new StringBuilder(str).reverse().toString();
			unit.append(str + ",");

			int temp = word[i].length() - indexFound;
			String str2 = new StringBuilder(word[i]).substring(0, temp).toString();
			if (str2 != null) {
				prefix.append(str2);
			}
		}
		System.out.println();

		if(constant==null) {
			System.out.println("constant : " + "null");
		} else {
			System.out.println("constant : " + constant);
		}
		System.out.println("total prefix : " + prefix);
		System.out.println("total unit : " + unit);
	}
} // prefix append 전에 생성된 prefix가 txt에 존재하는 것인지도 판단해야할듯??