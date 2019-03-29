import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class UnitConverter {
	public String[][] unitArray;
	public String[][] prefixArray;
	int midindex = 0;

	String[] parseSide(StringBuilder line) {
		String[] side = line.toString().split("\\=");
		return side;
	}

	String parseConstant(StringBuilder line) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < line.length(); i++) {
			if ((line.charAt(i) <= '9' && line.charAt(i) >= '0') || line.charAt(i) == '-' || line.charAt(i) == '.') {
				sb.append(line.charAt(i));
			} else {
				line.delete(0, i);
				return sb.toString();
			}
		}
		return null;
	}

	StringBuilder[] parsePhrase(StringBuilder line) {
		String[] word = line.toString().split("\\*|\\/|=\\?");
		StringBuilder[] phrase = new StringBuilder[word.length];
		for (int i = 0; i < word.length; i++) {
			phrase[i] = new StringBuilder(word[i]);
		}
		return phrase;
	}

	int[] parseExponent(StringBuilder line) {
		String[] phrase = line.toString().split("\\*|\\/|=\\?");
		int[] exponent = new int[phrase.length];

		// determine base exponent for each phrase
		// if it's located before first occurrence of '/', assign 1 for the all phrases
		// assign -1 otherwise
		// '=' sets the exponent sequence to 1, for that right side of equation starts
		int one = 1;
		int index = 1;
		exponent[0] = one;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '*') {
				exponent[index++] = one;
			} else if (line.charAt(i) == '/') {
				one *= -1;
				exponent[index++] = one;
			} else if (line.charAt(i) == '=') {
				one = 1;
				midindex = index;
				exponent[index++] = one;
			}
		}

		// get dimension and multiply to base exponent
		for (int i = 0; i < exponent.length; i++) {
			int magnitude = 1;
			
			// search for pattern like ^(1), ^(-1), and ^1 (not ^-1)
			if (phrase[i].contains("^")) {
				String subString = phrase[i].substring(phrase[i].indexOf("^"));
				StringBuilder sb = new StringBuilder();
				if (Pattern.matches("\\^{1}(([0-9]{1})|(\\(-?[0-9]{1}\\)))", subString)) {
					for (int j = 0; j < phrase[i].length(); j++) {
						if (('0' <= phrase[i].charAt(j) && phrase[i].charAt(j) <= '9') || phrase[i].charAt(j) == '-') {
							sb.append(phrase[i].charAt(j));
						}
					}
				}
				magnitude = Integer.parseInt(sb.toString());
			} 
			exponent[i] *= magnitude;
		}

		return exponent;
	}

	StringBuilder[] parseBottom(StringBuilder[] phrase) {
		StringBuilder[] bottom = new StringBuilder[phrase.length];
		Pattern pattern = Pattern.compile("([a-z|A-Z|가-힣]+)");
		for (int i = 0; i < phrase.length; i++) {
			Matcher matcher = pattern.matcher(phrase[i]);
			if (matcher.find())
				bottom[i] = new StringBuilder(matcher.group());
		}
		return bottom;
	}

	String[] getPrefixName() {
		String[] unitName = new String[prefixArray.length];
		for (int i = 0; i < prefixArray.length; i++) {
			unitName[i] = prefixArray[i][0];
		}
		return unitName;
	}

	String[] getUnitName() {
		String[] unitName = new String[unitArray.length];
		for (int i = 0; i < unitArray.length; i++) {
			unitName[i] = unitArray[i][0];
		}
		return unitName;
	}
	
	String[] parseUnit(StringBuilder[] bottom) {
		readUnitFile("unit.txt");
		String[] units = getUnitName();
		String[] unit = new String[bottom.length];
		for (int i = 0; i < bottom.length; i++) {
			for (int j = 0; j < units.length; j++) {
				if (bottom[i].toString().endsWith(units[j])) {
					unit[i] = units[j];
					//remove detected unit, leaving only prefix inside the bottom clause
					String onlyPrefix = bottom[i].toString().replaceAll(units[j], "");
					bottom[i] = new StringBuilder(onlyPrefix);
					break;
				}
			}
//			if (unit[i] == null)
//				System.out.println("No match for " + bottom[i]);
		}
		return unit;
	}
	
	String[] parsePrefix(StringBuilder[] bottom) {
		readPrefixFile("prefix.txt");
		String[] prefixs = getPrefixName();
		String[] prefix = new String[bottom.length];
		for (int i = 0; i < bottom.length; i++) {
			for (int j = 0; j < prefixs.length; j++) {
				if (bottom[i].toString().equals(prefixs[j])) {
					prefix[i] = prefixs[j];
				}
			}
		}
		return prefix;
	}

	int countLines(File aFile) throws IOException {
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

	void readUnitFile(String path) {
		File file = new File(path);
		int lineNo = 0;
		try {
			lineNo = countLines(file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		unitArray = new String[lineNo][5];

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));) {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				unitArray[i++] = line.split("\t");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void readPrefixFile(String path) {
		File file = new File(path);
		int lineNo = 0;
		try {
			lineNo = countLines(file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		prefixArray = new String[lineNo][2];

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));) {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				prefixArray[i++] = line.split("\t");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	String[] solveDimension(String unit) {
		String[] gmsr = new String[4];
		for (int i = 0; i < unitArray.length; i++) {
			if (unit.equals(unitArray[i][0])) {
				gmsr[0] = unitArray[i][1];
				gmsr[1] = unitArray[i][2];
				gmsr[2] = unitArray[i][3];
				gmsr[3] = unitArray[i][4];
			}
		}
		return gmsr;
	}
	
	BigDecimal aggregateDimension(String[] unit, int[] exponent){
		String[] left = new String[] {"0", "0", "0", "1"};
		String[] right = new String[] {"0", "0", "0", "1"};
		
		// aggregating left units
		for(int i = 0 ; i < midindex;i++) {
			String[] gms = solveDimension(unit[i]);
			BigDecimal g = new BigDecimal(gms[0]).multiply(new BigDecimal(exponent[i]));
			BigDecimal m = new BigDecimal(gms[1]).multiply(new BigDecimal(exponent[i]));
			BigDecimal s = new BigDecimal(gms[2]).multiply(new BigDecimal(exponent[i]));
			BigDecimal r = new BigDecimal(gms[3]);
			left[0] = new BigDecimal(left[0]).add(g).toString(); //g
			left[1] = new BigDecimal(left[1]).add(m).toString(); //m
			left[2] = new BigDecimal(left[2]).add(s).toString(); //s
			for(int j = 0 ; j <exponent[i]; j++) {
				left[3] = new BigDecimal(left[3]).multiply(r).toString(); //s
			}
		}
		
		for(int i = midindex ; i < unit.length;i++) {
			String[] gms = solveDimension(unit[i]);
			BigDecimal g = new BigDecimal(gms[0]).multiply(new BigDecimal(exponent[i]));
			BigDecimal m = new BigDecimal(gms[1]).multiply(new BigDecimal(exponent[i]));
			BigDecimal s = new BigDecimal(gms[2]).multiply(new BigDecimal(exponent[i]));
			BigDecimal r = new BigDecimal(gms[3]);
			right[0] = new BigDecimal(right[0]).add(g).toString();
			right[1] = new BigDecimal(right[1]).add(m).toString();
			right[2] = new BigDecimal(right[2]).add(s).toString();
			for(int j = 0 ; j <exponent[i]; j++) {
				right[3] = new BigDecimal(right[3]).multiply(r).toString(); //s
			}
		}
		
		// comapre total gms of the left side with of the right
		for (int i = 0 ; i < 3; i++) {
			if(!left[i].equals(right[i]))
				return null;
		}
		
		return new BigDecimal(left[3]).divide(new BigDecimal(right[3]));
	}
	

	BigDecimal aggregatePrefix(String[] prefix, int[] exponent){
		int left = 0;
		int right = 0;
		
		// aggregating prefixs on left side
		for(int i = 0 ; i < midindex;i++) {
			if(prefix[i]!=null) {
				for(int j = 0; j < prefixArray.length; j++) {
					if(prefix[i].equals(prefixArray[j][0])) {
						left += Integer.parseInt(prefixArray[j][1]) * exponent[i];
					}
				}
			}
		}
		
		// aggregating prefixs on right side
		for(int i = midindex ; i < prefix.length;i++) {
			if(prefix[i]!=null)
				for(int j = 0; j < prefixArray.length; j++) {
					if(prefix[i].equals(prefixArray[j][0])) {
						right += Integer.parseInt(prefixArray[j][1]) * exponent[i];
					}
				}
		}
		
		return new BigDecimal(left - right);
	}

	void convert(String input) {

		StringBuilder line = new StringBuilder(input);

		String constant = parseConstant(line);
		int[] exponent = parseExponent(line);
		StringBuilder[] phrase = parsePhrase(line);
		StringBuilder[] bottom = parseBottom(phrase);
		String[] unit = parseUnit(bottom);
		String[] prefix = parsePrefix(bottom);
		
			
		BigDecimal totalConnstant = new BigDecimal(constant);
		BigDecimal totalRatio = aggregateDimension(unit, exponent);
		BigDecimal totalPrefix = aggregatePrefix(prefix, exponent);
		BigDecimal totalMultiplier = new BigDecimal(Math.pow(10, totalPrefix.doubleValue()));
		BigDecimal answer = totalRatio.multiply(totalConnstant).multiply(totalMultiplier);
		
		System.out.println(answer);
	}
}

public class converter {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine().replaceAll(" ", "");
		new UnitConverter().convert(input);
		scanner.close();
	}
}