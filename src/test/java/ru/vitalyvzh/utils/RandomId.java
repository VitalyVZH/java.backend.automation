package ru.vitalyvzh.utils;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

public class RandomId {

   public static String randomId() {

       RandomStringGenerator randomStringGenerator =
               new RandomStringGenerator.Builder()
                       .withinRange('0', 'z')
                       .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                       .build();
       return randomStringGenerator.generate(12);
   }

}
