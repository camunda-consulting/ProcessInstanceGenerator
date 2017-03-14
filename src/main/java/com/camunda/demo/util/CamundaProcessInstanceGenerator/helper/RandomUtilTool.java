package com.camunda.demo.util.CamundaProcessInstanceGenerator.helper;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.UUID;


/**
 * This is just a class to Help generating random numbers, string and Dates.
 * It's also used to calculate probability 
 * 
 * @author Niall
 *
 */
public class RandomUtilTool {

	public String randomUUID(){
        // Creating a random UUID (Universally unique identifier).
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();

        return randomUUIDString;
	}

	
	
	public boolean randomBoolean(int chanceOutOf100)
	{
	   int value = randomNumber(100);
	   
	   if(value < chanceOutOf100)
		   return true; //it's a hit!
	   else
		   return false; // it's a miss!
	}
	
	public int randomNumber(int upTo){
		int value = (int)(Math.random() * upTo);
		return value;
	}
	

    public Date randomDate(){

        GregorianCalendar gc = new GregorianCalendar();

        int year = randomNumberBetween(2011, 2016);

        gc.set(gc.YEAR, year);

        int dayOfYear = randomNumberBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));

        gc.set(gc.DAY_OF_YEAR, dayOfYear);

        System.out.println(gc.get(gc.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH));
        return gc.getTime();

    }
    
    public Date randomDate(int yearFrom, int yearTo){

        GregorianCalendar gc = new GregorianCalendar();

        int year = randomNumberBetween(yearFrom, yearTo);

        gc.set(gc.YEAR, year);

        int dayOfYear = randomNumberBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));

        gc.set(gc.DAY_OF_YEAR, dayOfYear);

        System.out.println(gc.get(gc.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH));
        return gc.getTime();

    }

    public static int randomNumberBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
    
    public String randomWords(int numberOfWords)
    {
        String randomStrings = "";
        Random random = new Random();
        for(int i = 0; i < numberOfWords; i++)
        {
            char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
            for(int j = 0; j < word.length; j++)
            {
                word[j] = (char)('a' + random.nextInt(26));
            }
            randomStrings = randomStrings + word + "";
        }
        return randomStrings;
    }

}
