/*
 * @(#)Collections.java	1.106 06/04/21
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.bsb.hike.util;

import java.util.Vector;

/**
 *
 * @author Sudheer Keshav Bhat
 */
public class Collections {

    public static void sort(Vector list, Comparator c) {
        Object[] a = new Object[list.size()];
        list.copyInto(a);
        Arrays.sort(a, (Comparator) c);
        for (int j = 0; j < a.length; j++) {
            list.setElementAt(a[j], j);
        }
    }

    public static interface Comparator {

        int compare(Object o1, Object o2);

        boolean equals(Object obj);
    }

    public static interface Comparable {

        public int compareTo(Object o);
    }

    public static class Arrays {

        /**
         * Tuning parameter: list size at or below which insertion sort will be
         * used in preference to mergesort or quicksort.
         */
        private static final int INSERTIONSORT_THRESHOLD = 7;

        public static void sort(Object[] a, Comparator c) {
            Object[] aux = new Object[a.length];
            System.arraycopy(a, 0, aux, 0, a.length);
            if (c == null) {
                mergeSort(aux, a, 0, a.length, 0);
            } else {
                mergeSort(aux, a, 0, a.length, 0, c);
            }
        }

        /**
         * Swaps x[a] with x[b].
         */
        private static void swap(Object[] x, int a, int b) {
            Object t = x[a];
            x[a] = x[b];
            x[b] = t;
        }

        /**
         * Src is the source array that starts at index 0
         * Dest is the (possibly larger) array destination with a possible offset
         * low is the index in dest to start sorting
         * high is the end index in dest to end sorting
         * off is the offset into src corresponding to low in dest
         */
        private static void mergeSort(Object[] src,
                Object[] dest,
                int low, int high, int off,
                Comparator c) {
            int length = high - low;

            // Insertion sort on smallest arrays
            if (length < INSERTIONSORT_THRESHOLD) {
                for (int i = low; i < high; i++) {
                    for (int j = i; j > low && c.compare(dest[j - 1], dest[j]) > 0; j--) {
                        swap(dest, j, j - 1);
                    }
                }
                return;
            }

            // Recursively sort halves of dest into src
            int destLow = low;
            int destHigh = high;
            low += off;
            high += off;
            int mid = (low + high) >>> 1;
            mergeSort(dest, src, low, mid, -off, c);
            mergeSort(dest, src, mid, high, -off, c);

            // If list is already sorted, just copy from src to dest.  This is an
            // optimization that results in faster sorts for nearly ordered lists.
            if (c.compare(src[mid - 1], src[mid]) <= 0) {
                System.arraycopy(src, low, dest, destLow, length);
                return;
            }

            // Merge sorted halves (now in src) into dest
            for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
                if (q >= high || p < mid && c.compare(src[p], src[q]) <= 0) {
                    dest[i] = src[p++];
                } else {
                    dest[i] = src[q++];
                }
            }
        }

        /**
         * Src is the source array that starts at index 0
         * Dest is the (possibly larger) array destination with a possible offset
         * low is the index in dest to start sorting
         * high is the end index in dest to end sorting
         * off is the offset to generate corresponding low, high in src
         */
        private static void mergeSort(Object[] src,
                Object[] dest,
                int low,
                int high,
                int off) {
            int length = high - low;

            // Insertion sort on smallest arrays
            if (length < INSERTIONSORT_THRESHOLD) {
                for (int i = low; i < high; i++) {
                    for (int j = i; j > low
                            && ((Comparable) dest[j - 1]).compareTo(dest[j]) > 0; j--) {
                        swap(dest, j, j - 1);
                    }
                }
                return;
            }

            // Recursively sort halves of dest into src
            int destLow = low;
            int destHigh = high;
            low += off;
            high += off;
            int mid = (low + high) >>> 1;
            mergeSort(dest, src, low, mid, -off);
            mergeSort(dest, src, mid, high, -off);

            // If list is already sorted, just copy from src to dest.  This is an
            // optimization that results in faster sorts for nearly ordered lists.
            if (((Comparable) src[mid - 1]).compareTo(src[mid]) <= 0) {
                System.arraycopy(src, low, dest, destLow, length);
                return;
            }

            // Merge sorted halves (now in src) into dest
            for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
                if (q >= high || p < mid && ((Comparable) src[p]).compareTo(src[q]) <= 0) {
                    dest[i] = src[p++];
                } else {
                    dest[i] = src[q++];
                }
            }
        }
    }
}
