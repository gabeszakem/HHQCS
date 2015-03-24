/*
 * Két byte tömböt add össze (concatenate)
 */
package hhqcs.tools;

/**
 *
 * @author gkovacs02
 */
public class ByteArrayConcat {

    /**
     *
     *
     * @param array1 első byte[]
     * @param array2 második byte[]
     * @return byte[]
     */
    public static byte[] concat(byte[] array1, byte[] array2) {
        if (array1 == null && array2 == null) {
            return null;
        } else if (array2 == null) {
            return array1;
        } else if (array1 == null) {
            return array2;
        } else {
            byte[] mergedArray = new byte[array1.length + array2.length];
            System.arraycopy(array1, 0, mergedArray, 0, array1.length);
            System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);
            return mergedArray;
        }
    }
    
    public static byte[] conpy(byte[] array1) {    
            return array1;  
    }
    
}
