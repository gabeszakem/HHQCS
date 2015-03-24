/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hhqcs.centralograf;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 *
 * @author gabesz
 * 
 */
public class FillDataToBuffer {

    /**
     * 
     * @param DB Ennek az osztálynak a mezőinek az értékeit teszi be a bytebufferbe
     * @param buffeSize A lefoglalt buffer mérete
     * @return Az elkészített buferrel tér vissza.
     */
    @SuppressWarnings({"UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch"})
    public byte[] load(Object DB, int buffeSize) {
        Field[] fields = DB.getClass().getDeclaredFields(); // A DB.-ben található mezők
        int pointer = 0;    // A mutató kezdő címe
        int shortLength = 2; // A "short" típusu adat hossza
        int floatLength = 4; // A "float" típusu adat hossza
        int byteLength = 1; // A "float" típusu adat hossza
        byte[] sendTelegram=new byte[buffeSize];

        for (Field field : fields) {
            try {
                // A mező adattípusának meghatározása
                String s = field.getType().getName();
                // "short" típusu adat átalakítása, és hozzárendelése a bytebufferhez
                if (s.equals("short")) {
                    ByteBuffer.wrap(sendTelegram, pointer, shortLength).putShort(field.getShort(DB));
                    pointer += shortLength;
                    // "float" típusu adat átalakítása, és hozzárendelése a a bytebufferhez
                } else if (s.equals("float")) {
                    ByteBuffer.wrap(sendTelegram, pointer, floatLength).putFloat(field.getFloat(DB));
                    pointer += floatLength;
                    // "plctesztpc.tools.S7String" típusu adat átalakítása, és hozzárendelése a a bytebufferhez
                }  else {
                    System.err.println("!!!! " + s);
                }

            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
        byte[] telegram=new byte[pointer];
        System.arraycopy(sendTelegram, 0, telegram,0, pointer);
        return telegram;
    }
    private static byte[] stringToByteBuffer(String string){
        return string.getBytes();
    }

}
