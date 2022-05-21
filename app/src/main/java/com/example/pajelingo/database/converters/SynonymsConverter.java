package com.example.pajelingo.database.converters;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

public class SynonymsConverter {
        @TypeConverter
        public static List<Long> fromString(String idsSynonymsString) {
            // A  null String maps an empty list
            if (idsSynonymsString == null){
                return new ArrayList<>();
            }
            // Removing the parenthesis(substring method) and splitting the string in the commas
            String[] arrayIdStrings = idsSynonymsString.substring(1,idsSynonymsString.length()-1).split(",");
            List<Long> idsSynonyms = new ArrayList<>();

            for (String idString : arrayIdStrings) {
                // We need to do a trim, because there may be a space before the number
                idsSynonyms.add(Long.parseLong(idString.trim()));
            }
            return idsSynonyms;
        }

        @TypeConverter
        public static String fromList(List<Long> idsSynonyms) {
            // A  null String maps an empty list
            if (idsSynonyms == null){
                return null;
            }
            return idsSynonyms.toString();
        }
}
