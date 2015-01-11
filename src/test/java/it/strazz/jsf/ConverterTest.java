package it.strazz.jsf;

import javax.faces.convert.ConverterException;

import org.junit.Assert;
import org.junit.Test;

public class ConverterTest {

	private IntegerToRomanNumeralConverter c = new IntegerToRomanNumeralConverter();
	
	/**
	 * The max int value is 4999
	 */
	@Test(expected=ConverterException.class)
	public void invalidMax() {
		c.getAsString(null, null, 5000);
	}
	
	/**
	 * The min int value is 1
	 */
	@Test(expected=ConverterException.class)
	public void invalidMin() {
		c.getAsString(null, null, 0);
	}
	
	@Test
	public void toRoman() {
		String roman = c.getAsString(null, null, 4999);
		Assert.assertTrue(roman.equals("MMMMCMXCIX"));
	}
	
	@Test
	public void toInt() {
		int arabic = (Integer) c.getAsObject(null, null, "MMMMCMXCIX");
		Assert.assertTrue(arabic == 4999);
	}
}
