/*
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 * @version $Id: Verhoeff.java 1790 2011-07-22 20:42:40Z joe.isaac $
 */  
package org.tolven.checkdigit;

/**
 * Compute and validate Verhoeff Check Digit
 * @author John Churin
 */
public class Verhoeff {

    /* D - multiplication table */
    private static final int[][] D = new int[][] {
        {0,  1,  2,  3,  4,  5,  6,  7,  8,  9}, 
        {1,  2,  3,  4,  0,  6,  7,  8,  9,  5},
        {2,  3,  4,  0,  1,  7,  8,  9,  5,  6},
        {3,  4,  0,  1,  2,  8,  9,  5,  6,  7},
        {4,  0,  1,  2,  3,  9,  5,  6,  7,  8},
        {5,  9,  8,  7,  6,  0,  4,  3,  2,  1},
        {6,  5,  9,  8,  7,  1,  0,  4,  3,  2},
        {7,  6,  5,  9,  8,  2,  1,  0,  4,  3},
        {8,  7,  6,  5,  9,  3,  2,  1,  0,  4},
        {9,  8,  7,  6,  5,  4,  3,  2,  1,  0}};

    /* P - permutation table */
    private static final int[][] P = new int[][] {
        {0,  1,  2,  3,  4,  5,  6,  7,  8,  9},
        {1,  5,  7,  6,  2,  8,  3,  0,  9,  4},
        {5,  8,  0,  3,  7,  9,  6,  1,  4,  2},
        {8,  9,  1,  6,  0,  4,  3,  5,  2,  7},
        {9,  4,  5,  3,  1,  2,  6,  8,  7,  0},
        {4,  2,  8,  6,  5,  7,  3,  9,  0,  1},
        {2,  7,  9,  3,  8,  0,  6,  4,  1,  5},
        {7,  0,  4,  6,  9,  1,  3,  2,  5,  8}};

    /* inv: inverse table */
    private static final int[] INV = new int[]
        {0,  4,  3,  2,  1,  5,  6,  7,  8,  9};

    /**
     * Validate the Verhoeff Check Digit in a number.
     * @param code The code to validate
     * @return <code>true</code> if the check digit is valid,
     * otherwise <code>false</code>
     */
    public static boolean check(long code) {
        int checksum = 0;
        int pos = 0;
        while (code!=0) {
            int digit = (int) code % 10;
            code = code / 10;
            checksum = D[checksum][P[pos % 8][digit]];
            pos++;
        }
        return (checksum == 0);
    }

    /**
     * Calculate a Verhoeff <i>Check Digit</i> for a code.
     *
     * @param code The code to calculate the Check Digit for
     * @return The full number with the calculated Check Digit appended (LSD)
     */
    public static long appendCheckDigit(long code) {
        int checksum = calculate(code);
        return (code*10)+INV[checksum];
    }

    /**
     * Calculate Verhoeff checksum.
     * @param code The code to calculate the checksum for.
     * @return The checksum value
     * @throws RuntimeException if the code contains a non-numeric)
     */
    public static int calculate(long code ) {
        int checksum = 0;
        int pos = 1;
        while (code!=0) {
            int digit = (int) code % 10;
            code = code / 10;
            checksum = D[checksum][P[pos % 8][digit]];
            pos++;
        }
        return checksum;
    }

}
