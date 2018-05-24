package dk.picit.picmobilear.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserService {



    public static String visionToISO6346(String s)
    {
        String result = "";

        //split string at newline into array
        String[] nSplit = s.split("\\n");
        ArrayList<String> nSplitLength = new ArrayList<>();

        //remove any strings too small for ISO6346 standard and save without white spaces
        for (String subnSplit:nSplit
             ) {
            if(subnSplit.trim().length() > 10)
            {
                nSplitLength.add(subnSplit.trim().replaceAll("\\s+", ""));
            }
        }

        //remove strings that do not have 4 chars and a minimum of 6 digits
        Iterator<String> spliterator = nSplitLength.iterator();
        String nextIt = "";
        while (spliterator.hasNext())
        {
            nextIt = spliterator.next();
            if(!nextIt.matches(".*[a-zA-Z]{4}\\d{6}.*")){
                spliterator.remove();
            }
        }

        for (String nSplitLenghtMatch: nSplitLength) {
            nSplitLenghtMatch = matchPattern(nSplitLenghtMatch);
        }

        //if theres is at least one result save it and return
        if(nSplitLength.size() > 0)
        {
            result = nSplitLength.get(0);
        }
        return result;
    }

    private static String matchPattern(String string){
        String res = "";

        Pattern pattern = Pattern.compile("[a-zA-Z]{4}\\d{6}");
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()){
            res = matcher.group(1);
        }

        return res;
    }

    public static int getChecksumDigit(String pCid){
        if(pCid == null || !(pCid.length() == 11 || pCid.length() == 10)){
            return -1;
        }
        String char2num = "0123456789A?BCDEFGHIJK?LMNOPQRSTU?VWXYZ";
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            int n = (char2num.indexOf(pCid.charAt(i)));
            n *= Math.pow(2, i);
            sum += n;
        }
        int rem = (sum % 11) % 10;
        return char2num.indexOf(pCid.charAt(10)) == rem;
    }
}
