package com.example.pajelingo.database.converters;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

public class SynonymsConverter {
        @TypeConverter
        public static List<Long> fromString(String idsSynonymsString) {
            List<Long> idsSynonyms = new ArrayList<>();
            for (int i = 1;i < idsSynonymsString.length() - 1;i++){
                char currentChar = idsSynonymsString.charAt(i);
                if (Character.isDigit(currentChar)){
                    idsSynonyms.add(Long.parseLong(currentChar+""));
                }
            }
            return idsSynonyms;
        }

        @TypeConverter
        public static String fromList(List<Long> idsSynonyms) {
            if (idsSynonyms == null){
                return null;
            }
            return idsSynonyms.toString();
        }
}
