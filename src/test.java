
public class test {
	static String parseUnit(String phrase) {
		
		for(int i = phrase.length()-1 ; i >= 0; i--) {
			if(phrase.charAt(i)=='^') {
				return new StringBuilder(phrase).reverse().substring(i+1);
			}
		}
		return phrase;
	}
	public static void main(String[] args) {
		System.out.println(parseUnit("kg^2"));

	}

}
