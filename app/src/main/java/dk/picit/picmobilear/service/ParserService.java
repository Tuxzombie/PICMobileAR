package dk.picit.picmobilear.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserService {


    public static String visionToISO6346(String s) {
        String result = "";

        //split string at newline into array
        String[] nSplit = s.split("\\n");
        ArrayList<String> nSplitLength = new ArrayList<>();

        //remove any strings too small for ISO6346 standard and save without white spaces
        for (String subnSplit : nSplit) {
            if (subnSplit.trim().length() >= 10) {
                nSplitLength.add(subnSplit.trim().replaceAll("\\s+", ""));
            }
        }

        //remove strings that do not have 4 chars and a minimum of 6 digits
        Iterator<String> spliterator = nSplitLength.iterator();
        String nextIt = "";
        while (spliterator.hasNext()) {
            nextIt = spliterator.next();
            if (!nextIt.matches(".*[a-zA-Z]{4}\\d{6}.*")) {
                spliterator.remove();
            }
        }

        //remove anything before the 4 letters, and anything after a sequence of 6 digits.
        for (int i = 0; i < nSplitLength.size(); i++) {
            nSplitLength.set(i, matchPattern(nSplitLength.get(i)));
        }

        String res = "";
        for (String nSplitCheckDigitMatch : nSplitLength) {
            res = nSplitCheckDigitMatch.substring(0, 10);
            int checkDigit = getChecksumDigit(res);
            // if there is more than 10 characters, there might be a check digit
            if (nSplitCheckDigitMatch.length() > 10) {
                res += nSplitCheckDigitMatch.charAt(10);
                //the check digit match, this is a valid container number
                if (Integer.toString(checkDigit).equals("" + nSplitCheckDigitMatch.charAt(10))) {
                    result = res;
                    // the first number might be a pole, mistaken for a 1. Try again, if index 4 is a 1.
                } else if (nSplitCheckDigitMatch.charAt(4) == '1') {
                    res = nSplitCheckDigitMatch.substring(0, 4) +
                          nSplitCheckDigitMatch.substring(5, 11);
                    checkDigit = getChecksumDigit(res);
                    if (nSplitCheckDigitMatch.length() > 11) {
                        res += nSplitCheckDigitMatch.charAt(11);
                        if (Integer.toString(checkDigit)
                                   .equals("" + nSplitCheckDigitMatch.charAt(11))) {
                            result = res;
                        }
                    } else {
                        res += checkDigit;
                    }
                }
            } else {
                res += checkDigit;
            }
        }

        // if result is empty, set the last container number as result.
        if (result.isEmpty()) {
            result = res;
        }


        return result;
    }

    private static String matchPattern(String string) {
        String res = "";

        Pattern pattern = Pattern.compile("([a-zA-Z]{4}\\d{6,})");
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            res = matcher.group(1);
        }

        return res;
    }

    private static int getChecksumDigit(String pCid) {
        if (pCid == null || !(pCid.length() == 11 || pCid.length() == 10)) {
            return -1;
        }
        String char2num = "0123456789A?BCDEFGHIJK?LMNOPQRSTU?VWXYZ";
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            int n = (char2num.indexOf(pCid.charAt(i)));
            n *= Math.pow(2, i);
            sum += n;
        }
        int checkDigit = (sum % 11) % 10;
        return checkDigit;
    }
}
