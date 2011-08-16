/**
 * 
 */
package org.tipitaka.search;

public class RomanScriptHelper {
    
    public static String removeDiacritcals(String text) {
        return text
                .replace("ṇ", "n").replace("ḍ", "d").replace("ḷ", "l")
                .replace("ū", "u").replace("ñ", "n").replace("ṭ", "t")
                .replace("ṃ", "m").replace("ṅ", "n").replace("ā", "a")
                .replace("ī", "i")
                .replace("Ṇ", "N").replace("Ḍ", "D").replace("Ḷ", "L")
                .replace("Ū", "U").replace("Ñ", "N").replace("Ṭ", "T")
                .replace("Ṃ", "M").replace("Ṅ", "N").replace("Ã", "A")
                .replace("Ī", "I");
    }
}