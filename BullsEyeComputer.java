import java.util.*;
import java.util.function.Function;

public class BullsEyeComputer {
	
	static Integer guessCount = 0;
	static Integer matchSum = 0;
	static Boolean found = false;
	static Boolean sizeFiltered = false;
	static List<String> probables = new ArrayList<String>();
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Go");
		while(!found){
		  String guessDigits = getNextGuess(guessCount);  
		  guessCount++;
		  if(guessCount >= 15){
			found = true;
			System.out.println("Last chance..");
		  }
		  if(guessDigits.equalsIgnoreCase("q") || probables.size() > 1000){
		  	System.out.println("Bye ! Too many probables");
		  	System.exit(0);
		  }
		  Integer matches = 0;
		  do{
			  System.out.println(guessDigits+" ?");
			  matches = scan.nextInt();
		  } while(matches > 4);
		  
		  String guess = guessDigits;
		  //System.out.println(guess);
		  matchSum += matches;
		  System.out.println("m: "+matches);
		  if(guessCount < 3){
		    if(matches != 0){
		    	populateProbables(guess, matches);    
		    }		
		  } else{
		  	if(!sizeFiltered){
		  		probables.stream().filter(str-> isValidProbable(str));
		  		sizeFiltered = true;
		  		System.out.println("Filtering by size completed.");
		    } else{    
		    	if(matches == 4 ){
		      	found = true;
		        System.out.println("Yay !!");
		        System.exit(0);
		      } else{
		        System.out.println("Filtering probables");
		        final int matchCount = matches;
		        probables.stream().filter(str -> probablesfilter(str, guess, matchCount));
		      }
		    }
		  }
		  System.out.println("Populated probables = " + String.join(", ", probables).toString());
		}
		scan.close();
	}
	
	public static Boolean isValidProbable(String str){
    	if("0".equals(str.charAt(0)+"")){
    		return Boolean.FALSE;
    	}
    	
    	if(str.length() > 4){
    		Set<Character> charSet = new LinkedHashSet<Character>();
        	for (char c : str.toCharArray()) {
        	    charSet.add(c);
        	}

        	StringBuilder sb = new StringBuilder();
        	for (Character character : charSet) {
        	    sb.append(character);
        	}
        	str = sb.toString();
    	}
    	
    	if(str.length() == 4){
    		for(int i=0; i < 4; i++){
            	for(int j=0; j < 4; j++){
    	        	 if(i != j && (str.charAt(i)+"").equalsIgnoreCase(str.charAt(j)+"")){
    	        	 	return Boolean.FALSE;
    	        	 }
            	 }
            }
    	}
        
        return Boolean.TRUE;
    }
	
	public static Boolean probablesfilter(String str, String guess, Integer matches){
    	if(isValidProbable(str)){
			Integer probableMatch = 0;          
	        for(int i=0; i < 4; i++){
	        	probableMatch += ((str.indexOf(guess.charAt(i))== -1)? 0 : 1); 
	        }
	        System.out.println(str +"<->"+guess+"="+probableMatch);
	        return (probableMatch == matches);    	
    	}
    	return Boolean.FALSE;
    }
	
	public static String getNextGuess(Integer guessCount){
		  System.out.println("In getNextGuess. guessCount = "+guessCount);
		  if(guessCount == 0){
		   return "1234";
		  } else if(guessCount == 1){
		  	return "5678";
		  } else if(guessCount == 2){
		  	return "9015";
		  } else{  	
		  	Integer randomIndex = (int) Math.floor((Math.random() * probables.size()));
		    System.out.println(randomIndex+"/"+probables.size());
		    return probables.get(randomIndex);
		  }
	}
	
	public static void populateProbables(String guess, Integer matches) {
		System.out.println("In populateProbables..");
		List<String> combinations = combine(guess, matches);
		System.out.println(combinations.toString());
		if (probables.size() == 0) {
			probables.addAll(combinations);
		} else {
			List<String> newProbables = new ArrayList<String>();
			for (String el : probables) {
				newProbables.addAll(prependToAll(el, combinations));	// originally used lazyProduct
			}
			probables.clear();
			probables.addAll(newProbables);
		}
	}

	/**
	 * This method generates all the combinations of the 
	 * 
	 * @param string
	 * @param comboLength
	 * @return
	 */
	public static List<String> combine(String string, Integer comboLength) {
		List<String> combinations = new ArrayList<String>();
		Integer combinationsCountLimit = calculateNcr(string.length(), comboLength);
		//System.out.println("Limit= "+combinationsCountLimit);
		
		//Initial conditions
		String prefix = "";
		String[] partials = string.split("");
		Integer recursionLevel = 1;
		Integer reqdLength = comboLength;
		
		//We need to keep executing the calls to combinator until the total number of combinations are achieved.
		while(combinations.size() < combinationsCountLimit){
			combinator(combinations, prefix, partials, recursionLevel, reqdLength);
		}
	    return combinations;
	}
	
	/**
	 * 			prefix, remChars, level, reqdLen
		
		Init: 	"", [a,b,c,d,e] , 1 , 3
		
				a, [b,c,d,e] , 2 , 3
				ab, [c,d,e] , 3 , 3 -- 	Note that we need not recurse further. Here the prefix length is reqdLen-1. 
										This also syncs with level = reqdLen. Hence, this is the exit criteria for the recursion.
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
		for (String str : list){
			prependedList.add(pre+str);
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
	
}
