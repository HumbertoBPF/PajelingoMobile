package com.example.pajelingo.util;

import java.util.List;
import java.util.Random;

public class Tools{

    /**
     * Picks a random item from a list.
     * @param list list from which the item is going to be extracted.
     * @param <E> same generic type as the list.
     * @return A random item from the list.
     */
    public static <E> E getRandomItemFromList(List<E> list){
        Random rand = new Random();
        int randomIndex = rand.nextInt(list.size());

        return list.get(randomIndex);
    }

}
