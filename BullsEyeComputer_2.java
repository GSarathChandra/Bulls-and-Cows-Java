import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class BullsEyeComputer_2 {
	
	static Integer guessCount = 0;
	static Random rand = new Random();
	static Boolean found = false;
	static Boolean reachedFourCows = false;
	static List<String> probables = new ArrayList<String>();
	static Map<String, String> attempts = new LinkedHashMap<String, String>();
	static Map<String, String> probableScores = new LinkedHashMap<String, String>();
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Go");
		while (!found) {
			String guessDigits = getNextGuess(guessCount);
			if (guessCount >= 150) {	//No of attempts
				found = true;
				System.out.println("Last chance..");
			}
			if (guessDigits.equalsIgnoreCase("q") || probables.size() > 10000) {
				System.out.println("Bye ! Too many probables");
				System.exit(0);
			}
			Integer bulls = 0;
			Integer cows = 0;
			do {
				System.out.println(guessCount+1+") "+guessDigits + " ?");
				
				System.out.print(guessCount+1+") How many Bull(s) (in place) ? ");
				bulls = scan.nextInt();
				
				if(bulls != 4){
					System.out.print(guessCount+1+") How many Cow(s) (not in place) ? ");
					cows = scan.nextInt();
				}				
				
				attempts.put(guessDigits, bulls+""+cows);
			} while (bulls < 0 || bulls > 4 || cows < 0 || cows > 4 || cows+bulls > 4);

			String guess = guessDigits;
			if (bulls == 4) {
				found = true;
				System.out.println("Yay !!");
				System.exit(0);
			} else {
				populateProbables(guess, bulls, cows);
			}
			guessCount++;
			System.out.println("Populated probables = "+String.join(", ", probables));
			System.out.println("Attempts list = " + attempts.toString());
		}
		scan.close();
	}
	
	public static Boolean isValidProbable(String str){
    	if("0".equals(str.charAt(0)+"")){
    		return Boolean.FALSE;
    	}
    	
    	if(str.length() > 4){
    		str = removeDuplicateCharacters(str);
        	if (str.length() > 4){
        		return Boolean.FALSE;
        	}
    	}
    	
    	if(str.length() == 4){
    		for(int i=0; i < 4; i++){
            	for(int j=0; j < 4; j++){
    	        	 if(i != j && (str.charAt(i)+"").equalsIgnoreCase(str.charAt(j)+"")){
    	        	 	return Boolean.FALSE;
    	        	 }
            	 }
            }
    	} else {
    		return Boolean.FALSE;
    	}
        
        return Boolean.TRUE;
    }
	
	public static Boolean probablesfilter(String probable, String guess, Integer bulls, Integer cows){
    	if(isValidProbable(probable)){
			Integer bullsCount = 0;          
			Integer cowsCount = 0;
	        for(int i=0; i < 4; i++){
	        	if(probable.charAt(i) == guess.charAt(i)){
	        		bullsCount++;
	        	}
	        }
	        
	        for(int i=0; i < 4; i++){
	        	for(int j=0; j < 4; j++){
	        		if(probable.charAt(i) == guess.charAt(j) && i!=j){
		        		cowsCount++;
		        	}
	        	}
	        }
	        return (bullsCount >= bulls && cowsCount >= cows);    	
    	}
    	return Boolean.FALSE;
    }
	
	public static String getNextGuess(Integer guessCount) {
		System.out.println("In getNextGuess. guessCount = " + guessCount);
		System.out.println(probableScores.toString());
		if (guessCount == 0) {
			return "1234";
		} else if (guessCount == 1) {
			return "5678";
		} else if (guessCount == 2) {
			return "9015";
		} else {
			String randomProbable = "";
			boolean searchNextProbable = true;
			do{
				Integer randomIndex = rand.nextInt(probables.size());	// size may go to zero for a lie
				randomProbable = probables.get(randomIndex);
				System.out.println(randomIndex + "/" + probables.size() +" = "+randomProbable);
				if(isValidProbable(randomProbable) && !attempts.keySet().contains(randomProbable)){
					searchNextProbable = false;
				}
				probables.remove(randomProbable); //Remove this probable from being selected in future attempts
			} while(searchNextProbable);
			
			return randomProbable;
		}
	}
	
	public static void populateProbables(String guess, Integer bulls, Integer cows) {
		if(bulls+cows > 0){
			if(bulls+cows != 4){
				//System.out.println("In populateProbables..");
				List<String> combinations = combine(guess, bulls, cows);
				System.out.println("Probable substrings of current guess = "+combinations.toString());
				if (probables.size() == 0) {
					probables.addAll(combinations);
				} else {
					List<String> newProbables = new ArrayList<String>();
					for (String el : probables) {
						newProbables.addAll(prependToAll(el, combinations));	// originally used lazyProduct
					}
					probables.clear();
					probables.addAll(new HashSet<String>(newProbables));
				}
			} else {
				if(!reachedFourCows){	// to be executed only once
					reachedFourCows = true;
					probables.clear();
					for (String el : getAllPermutations(guess)) {
						if(isValidProbable(el) && !attempts.keySet().contains(el)){
							probables.add(el);
						}						
					}
				}
			}
		} else {
			System.out.println("In populateProbables for Bullshit ..");
			List<String> newProbables = new ArrayList<String>();
			for (String el : probables) {
				if(el.indexOf(guess.substring(0,1)) == -1 
						&& el.indexOf(guess.substring(1,2)) == -1
						&& el.indexOf(guess.substring(2,3)) == -1
						&& el.indexOf(guess.substring(3,4)) == -1){
					newProbables.add(el);	// add only entries which do not match even one character with the current guess
				}				
			}
			probables.clear();
			probables.addAll(newProbables);
		}
		
		if(attempts.size() > 3){
			List<String> probablesPerAttempt = new ArrayList<String>();
			for (String el : probables) {
				for(String elperm : getAllPermutations(el)){
					if(isValidProbablePerAttempts(elperm)){
						probablesPerAttempt.add(elperm);
					}
				}				
			}
			probables.clear();
			probables.addAll(probablesPerAttempt);
		}
	}

	/**
	 * This method generates all the combinations of the 
	 * 
	 * @param string
	 * @param comboLength
	 * @return
	 */
	public static List<String> combine(String guess, Integer bulls, Integer cows) {
		List<String> combinations = new ArrayList<String>();
		Integer combinationsCountLimit = calculateNcr(guess.length(), bulls+cows);
		//System.out.println("Limit= "+combinationsCountLimit);
		
		//Initial conditions
		String prefix = "";
		String[] partials = guess.split("");
		Integer recursionLevel = 1;
		Integer reqdLength = bulls+cows;
		
		//We need to keep executing the calls to combinator until the total number of combinations are achieved.
		while(combinations.size() < combinationsCountLimit){
			combinator(combinations, prefix, partials, recursionLevel, reqdLength);
		}
		combinations.stream().filter(str-> isValidProbable(str));
	    return combinations;
	}
	
	/**
	 * 			prefix, remChars, level, reqdLen
		
		Init: 	"", [a,b,c,d,e] , 1 , 3
		
				a, [b,c,d,e] , 2 , 3
				ab, [c,d,e] , 3 , 3 -- 	Note that we need not recurse further. Here the prefix length is reqdLen-1. 
										This also synchronizes with "level = reqdLen". Hence, this is the exit criteria for the recursion.
				b, [c,d,e] , 2 , 3
				c, [d,e], 2 , 3

	 * @param combinations
	 * @param prefix
	 * @param partials
	 * @param recursionLevel
	 * @param reqdLength
	 */
	public static void combinator(List<String> combinations, String prefix, String[] partials, Integer recursionLevel, Integer reqdLength){
		//System.out.println("Entering combinator with "+prefix + "-" +Arrays.toString(partials) + "-"+recursionLevel+"-"+reqdLength);
		if(recursionLevel == reqdLength){
			combinations.addAll(prependToAll(prefix+"", Arrays.asList(partials)));
		} else {
			for(int i =0; i<partials.length; i++){
				combinator(combinations, prefix+partials[i]+"", Arrays.copyOfRange(partials, i+1, partials.length),recursionLevel+1, reqdLength);
			}
		}
	}
	
	public static List<String> prependToAll(String pre, List<String> list){
		//System.out.print("- Entering prependToAll with "+pre+"-"+"list: "+list.toString());
		List<String> prependedList = new ArrayList<String>();
		String temp = "";
		for (String str : list){
			temp = pre+str;
			if(temp.length() >= 4){
				temp = removeDuplicateCharacters(temp);
			}
			if(temp.length() <= 4){
				prependedList.add(temp);
			}			
		}
		//System.out.println(" => "+prependedList.toString());
		return prependedList;
	}
	
	public static Integer calculateNcr(Integer n, Integer r){
		Long nf = factorial(n);
		Long rf = factorial(r);
		Long nrf = factorial(n-r);
		
		return Math.toIntExact(nf/(nrf * rf));
	}
	
	public static Long factorial(Integer n){
		Long factorial = 1L;
		while(n > 1){
			factorial *= n;
			n--;
		}
		return factorial;
	}
	
	public static String removeDuplicateCharacters(String str){
		Set<Character> charSet = new LinkedHashSet<Character>();
    	for (char c : str.toCharArray()) {
    	    charSet.add(c);
    	}

    	StringBuilder sb = new StringBuilder();
    	for (Character character : charSet) {
    	    sb.append(character);
    	}
    	return sb.toString();
	}	
	
	public static Boolean isValidProbablePerAttempts(String prob){
		if(isValidProbable(prob)){
			String cumulativeResult = "";
			for(String attempt : attempts.keySet()){					
				Integer bullsCount = 0;          
		        for(int i=0; i < 4; i++){
		        	if(prob.charAt(i) == attempt.charAt(i)){
		        		bullsCount++;
		        	}
		        }
		        
		        Integer cowsCount = 0;
		        for(int i=0; i < 4; i++){
		        	for(int j=0; j < 4; j++){
		        		if(prob.charAt(i) == attempt.charAt(j) && i!=j){
			        		cowsCount++;
			        	}
		        	}
		        }
		        if(bullsCount > 0 && cowsCount > 0){
		        	Integer probableScore = (bullsCount*10)+cowsCount;
			        Integer attemptScore = Integer.parseInt(attempts.get(attempt));
			        /*if(!reachedFourCows && probableScore >= attemptScore){
			        	cumulativeResult += probableScore+"-";
			        } else if(reachedFourCows && probableScore > attemptScore){
			        	cumulativeResult += probableScore+"-";
			        } else {
			        	cumulativeResult += "X-";
			        }*/
			        
			        if(probableScore >= attemptScore){
			        	cumulativeResult += probableScore+"-";
			        } else {
			        	cumulativeResult += "X-";
			        }
		        }		        
			}
			System.out.println("cumResult for "+prob+" : "+cumulativeResult);
			if(!cumulativeResult.trim().isEmpty() && !cumulativeResult.contains("X")){
				probableScores.put(prob, cumulativeResult);
			}
			return !cumulativeResult.contains("X");
    	}
		return false;
	}
	
	public static List<String> getAllPermutations(String guess){
		if(!isValidProbable(guess)){
			return Collections.emptyList();
		}
		
		Set<String> newProbables = new HashSet<String>();
		newProbables.add(""+guess.charAt(0)+guess.charAt(1)+guess.charAt(2)+guess.charAt(3));
		newProbables.add(""+guess.charAt(0)+guess.charAt(1)+guess.charAt(3)+guess.charAt(2));
		newProbables.add(""+guess.charAt(0)+guess.charAt(2)+guess.charAt(1)+guess.charAt(3));
		newProbables.add(""+guess.charAt(0)+guess.charAt(2)+guess.charAt(3)+guess.charAt(1));
		newProbables.add(""+guess.charAt(0)+guess.charAt(3)+guess.charAt(1)+guess.charAt(2));
		newProbables.add(""+guess.charAt(0)+guess.charAt(3)+guess.charAt(2)+guess.charAt(1));
		
		newProbables.add(""+guess.charAt(1)+guess.charAt(0)+guess.charAt(2)+guess.charAt(3));
		newProbables.add(""+guess.charAt(1)+guess.charAt(0)+guess.charAt(3)+guess.charAt(2));
		newProbables.add(""+guess.charAt(1)+guess.charAt(2)+guess.charAt(0)+guess.charAt(3));
		newProbables.add(""+guess.charAt(1)+guess.charAt(2)+guess.charAt(3)+guess.charAt(0));
		newProbables.add(""+guess.charAt(1)+guess.charAt(3)+guess.charAt(0)+guess.charAt(2));
		newProbables.add(""+guess.charAt(1)+guess.charAt(3)+guess.charAt(2)+guess.charAt(0));
		
		newProbables.add(""+guess.charAt(2)+guess.charAt(0)+guess.charAt(1)+guess.charAt(3));
		newProbables.add(""+guess.charAt(2)+guess.charAt(0)+guess.charAt(3)+guess.charAt(1));
		newProbables.add(""+guess.charAt(2)+guess.charAt(1)+guess.charAt(0)+guess.charAt(3));
		newProbables.add(""+guess.charAt(2)+guess.charAt(1)+guess.charAt(3)+guess.charAt(0));
		newProbables.add(""+guess.charAt(2)+guess.charAt(3)+guess.charAt(0)+guess.charAt(1));
		newProbables.add(""+guess.charAt(2)+guess.charAt(3)+guess.charAt(1)+guess.charAt(0));
		
		newProbables.add(""+guess.charAt(3)+guess.charAt(0)+guess.charAt(1)+guess.charAt(2));
		newProbables.add(""+guess.charAt(3)+guess.charAt(0)+guess.charAt(2)+guess.charAt(1));
		newProbables.add(""+guess.charAt(3)+guess.charAt(1)+guess.charAt(0)+guess.charAt(2));
		newProbables.add(""+guess.charAt(3)+guess.charAt(1)+guess.charAt(2)+guess.charAt(0));
		newProbables.add(""+guess.charAt(3)+guess.charAt(2)+guess.charAt(0)+guess.charAt(1));
		newProbables.add(""+guess.charAt(3)+guess.charAt(2)+guess.charAt(1)+guess.charAt(0));
		
		return new ArrayList<String>(newProbables); 
	}
}
