/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hhqcs.compress;

import java.io.*;
import java.util.zip.*;

import org.apache.commons.io.IOUtils;

public class Compressor{
    @SuppressWarnings("ConvertToTryWithResources")
    public static byte[] compress(byte[] content){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try{
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(content);
            gzipOutputStream.close();
        } catch(IOException e){
            throw new RuntimeException(e);
        }
        System.out.println("Tömörítési arány: " + (1.0f * content.length/byteArrayOutputStream.size()));
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] decompress(byte[] contentBytes){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try{
            IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(contentBytes)), out);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    public static boolean notWorthCompressing(String contentType){
        return contentType.contains("jpeg")
                || contentType.contains("pdf")
                || contentType.contains("zip")
                || contentType.contains("mpeg")
                || contentType.contains("avi");
    }
}