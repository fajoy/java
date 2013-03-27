package chatserver;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class RegexHelper {
	public static Matcher getMatcher(String pattern, String input) {
		Pattern p = Pattern.compile(pattern);
		return p.matcher(input);
	}

	public static String[] getSubString(String pattern, CharSequence input) {
		Pattern p = Pattern.compile(pattern);
		return getSubString(p, input);
	}

	public static String[] getSubString(Pattern pattern, CharSequence input) {
		return getSubString(pattern.matcher(input));
	}

	public static String[] getSubString(Matcher matcher) {
		if (!matcher.find())
			return null;
		String str[] = new String[matcher.groupCount() + 1];
		for (int i = 0; i <= matcher.groupCount(); i++) {
			str[i] = matcher.group(i);
		}
		return str;
	}

	public static List<String[]> getSubStrings(String pattern,
			CharSequence input) {
		return getSubStrings(Pattern.compile(pattern), input);
	}

	public static List<String[]> getSubStrings(Pattern pattern,
			CharSequence input) {
		List<String[]> result = new ArrayList<String[]>();
		Matcher m = pattern.matcher(input);
		String[] str = getSubString(m);
		while (str != null) {
			result.add(str);
			str = getSubString(m);
		}
		return result;
	}

	public static void printResult(String pattern, CharSequence input) {
		printResult(Pattern.compile(pattern), input);
	}

	public static void printResult(Pattern pattern, CharSequence input) {
		List<String[]> result = RegexHelper.getSubStrings(pattern, input);
		for (String[] strings : result) {
			System.out.print(String.format("[%s", strings[0]));
			for (int i = 1; i < strings.length; i++) {
				System.out.print(String.format(",%s", strings[i]));
			}
			System.out.println("]");
		}
	}

}