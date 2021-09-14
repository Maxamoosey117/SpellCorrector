package spell;

import java.io.IOException;
import java.util.Scanner;

/**
 * A simple main class for running the spelling corrector. This class is not
 * used by the passoff program.
 */
public class Main {
	
	/**
	 * Give the dictionary file name as the first argument and the word to correct
	 * as the second argument.
	 */
	public static void main(String[] args) throws IOException {
		
		String dictionaryFileName = args[0];
		String inputWord = args[1];
		
		//
        //Create an instance of your corrector here
        //
		ISpellCorrector corrector = new SpellCorrector();
		
		corrector.useDictionary(dictionaryFileName);
		
		String suggestion = corrector.suggestSimilarWord(inputWord);
		if (suggestion == null) {
		    suggestion = "No similar word found";
		}
		
		System.out.println("Suggestion is: " + suggestion);
		
		String input = "";
		while (true) {
			System.out.println("Please input a word to check: ");
			@SuppressWarnings("resource")
			Scanner in = new Scanner(System.in);
			input = in.next();
			System.out.println("You typed: " + input);
			
			ISpellCorrector correction = new SpellCorrector();
			correction.useDictionary(dictionaryFileName);
			String suggest = correction.suggestSimilarWord(input);
			if (suggest == null) {
			    suggest= "No similar word found";
			}
			System.out.println("Suggestion is: " + suggest + "\n");
			
		}
		
	}

}
