package it.strazz.jsf;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBiMap;

@FacesConverter(value=IntegerToRomanNumeralConverter.CONVERTER_ID)
public class IntegerToRomanNumeralConverter implements Converter{

	private static final int MIN_ROMAN = 1;

	private static final int MAX_ROMAN = 4999;

	public static final String CONVERTER_ID = "it.strazz.jsf.converter.IntegerToRomanNumeralConverter";
	
	private static final char ONE_THOUSAND = 'M';
	private static final char FIVE_HUNDRED = 'D';
	private static final char ONE_HUNDRED = 'C';
	private static final char FIFTY = 'L';
	private static final char TEN = 'X';
	private static final char FIVE = 'V';
	private static final char ONE = 'I';

	public static final char[] ACCEPTED_CHARS = new char[]{ONE,FIFTY,FIVE,FIVE_HUNDRED,ONE_HUNDRED,ONE_THOUSAND,TEN};

	private static final String ONES_ARRAY[] = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
	private static final String TENS_ARRAY[] = {"X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
	private static final String HUNDREDS_ARRAY[] = {"C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};

	private static final Map<Character,Integer> ROMAN_TO_ARABIC_CONVERSION_MAP = HashBiMap.create();

	static{
		ROMAN_TO_ARABIC_CONVERSION_MAP.put(ONE, 1);
		ROMAN_TO_ARABIC_CONVERSION_MAP.put(FIVE, 5);
		ROMAN_TO_ARABIC_CONVERSION_MAP.put(TEN, 10);
		ROMAN_TO_ARABIC_CONVERSION_MAP.put(FIFTY, 50);
		ROMAN_TO_ARABIC_CONVERSION_MAP.put(ONE_HUNDRED, 100);
		ROMAN_TO_ARABIC_CONVERSION_MAP.put(FIVE_HUNDRED, 500);
		ROMAN_TO_ARABIC_CONVERSION_MAP.put(ONE_THOUSAND, 1000);
	}
	
	/**
	 * 
	 * @param number
	 * @return true if is a valid Arabic Number (1 < number < 4999)
	 */
	private static boolean isValidArabic(int number) {
		if (number > MAX_ROMAN || number < MIN_ROMAN) {
			return false;
		}
		return true;
	}

	/**
	 * @param number
	 * @return true if the provided Roman numeral is valid
	 */
	private static boolean isValidRoman(String number) {
		return StringUtils.containsOnly(number, ACCEPTED_CHARS);
	}

	/**
	 * @param number
	 * @return a string containing the entered Arabic numeral in Roman numeral form.
	 * @throws IllegalArgumentException if the parameter is not a valid Arabic Value
	 */
	public static String arabicToRoman(int number) throws IllegalArgumentException{
		Preconditions.checkArgument(isValidArabic(number));

		StringBuilder romanValue = new StringBuilder();

		//Units
		int ones = number % 10;

		//Tens
		number = (number - ones) / 10;
		int tens = number % 10;

		//hundreds
		number = (number - tens) / 10;
		int hundreds = number % 10;

		//thousands
		number = (number - hundreds) / 10;
		
		//grab thousands char
		for (int i = 0; i < number; i++) {
			romanValue.append(ONE_THOUSAND);
		}
		
		//grab houdreds char
		if (hundreds >= 1) {
			romanValue.append(HUNDREDS_ARRAY[hundreds - 1]);
		}

		//grab tens char
		if (tens >= 1) {
			romanValue.append(TENS_ARRAY[tens - 1]);
		}

		//grab units char
		if (ones >= 1) {
			romanValue.append(ONES_ARRAY[ones - 1]);
		}

		return romanValue.toString();
	}

	/**
	 * Convert a Roman Numeral to a int value
	 *
	 * @param roman value to convert
	 *
	 * @throws IllegalArgumentException if the string is not a valid roman numeral
	 */
	public static int romanToArabic(String roman) throws IllegalArgumentException{
		Preconditions.checkArgument(isValidRoman(roman));

		int toReturn = 0;
		int lastDigit = 0;
		int currentDigit = 0;

		for (int i = 0; i < roman.length(); i++) {
			currentDigit = ROMAN_TO_ARABIC_CONVERSION_MAP.get(roman.charAt(i));

			/*
			 * If the last number is smaller than the current number, subtract the last number from the current number
			 * Otherwise, just add the current number. We must also skip the first number from this rule simply because
			 * if someone enters 1799 in which case it would subtract 1 from 7
			 */
			if (lastDigit < currentDigit && lastDigit != 0) {
				currentDigit -= lastDigit;
				toReturn -= lastDigit;
			}
			
			toReturn += currentDigit;
			lastDigit = currentDigit;
			currentDigit = 0;
		}

		return toReturn;
	}


	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		try {
			return romanToArabic(value);
		} catch (IllegalArgumentException e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
			throw new ConverterException(msg);
		}
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		try {
			return arabicToRoman((Integer) value);
		} catch (IllegalArgumentException e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
			throw new ConverterException(msg);
		}
	}

}
