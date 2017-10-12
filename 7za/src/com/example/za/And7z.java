package com.example.za;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class And7z {
    private final static String TAG = "And7z";
    
    public static final int AND_7Z_FAILED = -1;
    
    public static final int AND_7Z_DETAIL = 0;
    
    public static final int AND_7Z_SUCCEED = 1;
    
    public interface And7zCallback {
        public void onProgress(int percent);
        public void onResult(int result, String message);
    }
    
    private static final int WHAT_7Z_OUTPUT = 1;
    
    private static final int WHAT_7Z_PROGRESS = 2;
    
    private And7zCallback mAnd7zCallback = null;
    
    /**
     * @param path
     * 
     *   Signature   The signature of the local file header. This is always '\x50\x4b\x03\x04'.
     *   Version PKZip version needed to extract
     *   Flags   General purpose bit flag:
     *   Bit 00: encrypted file
     *   Bit 01: compression option 
     *   Bit 02: compression option 
     *   Bit 03: data descriptor
     *   Bit 04: enhanced deflation
     *   Bit 05: compressed patched data
     *   Bit 06: strong encryption
     *   Bit 07-10: unused
     *   Bit 11: language encoding
     *   Bit 12: reserved
     *   Bit 13: mask header values
     *   Bit 14-15: reserved
     */
    public boolean isEncryptedZip(String path){
        if(!new File(path).exists()){
            Log.d(TAG, path + "is not exist.");
            return false;
        }
        
        if(!path.endsWith(".zip")){
            Log.d(TAG, path + "is not zip file.");
            return false;
        }
        
        InputStream in = null;
        try {
            byte[] tempbytes = new byte[8];
            in = new FileInputStream(path);
            in.read(tempbytes);
                
            if((tempbytes[6] & 1) == 0x1){
                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, Log.getStackTraceString(e));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    Log.d(TAG, Log.getStackTraceString(e1));
                }
            }
        }
        
        return false;
    }
    
    /**
     * ./7za a /sdcard/log.zip /sdcard/log.txt -bsp2
     * ./7za a /sdcard/log.zip /sdcard/log/ -bsp2
     * @param srcPath
     * @param dstPath
     * @param listener
     */
    public void And7zCompression(String srcPath, String dstPath, And7zCallback listener){
        String cmd = "/data/user/7za a " + dstPath + " " + srcPath + " -bsp2 -bse1";
        And7zCommand(cmd, listener);
    }
    
    /**
     * ./7za a /sdcard/log.zip /sdcard/log.txt -psecret -bsp2
     * ./7za a /sdcard/log.zip /sdcard/log/ -psecret -bsp2
     * @param srcPath
     * @param dstPath
     * @param pwd
     * @param listener
     */
    public void And7zCompression(String srcPath, String dstPath, String pwd, And7zCallback listener){
        String cmd = "/data/user/7za a " + dstPath + " " + srcPath + " -p" + pwd + " -bsp2 -bse1";
        And7zCommand(cmd, listener);
    }
    
    /**
     * ./7za x /sdcard/update.zip -o/sdcard/ -aoa -bsp2
     * @param srcPath
     * @param dstPath
     * @param listener
     */
    public void And7zDecompression(String srcPath, String dstPath, And7zCallback listener){
       String cmd = "/data/user/7za x " + srcPath + " -o" + dstPath + " -aoa -bsp2 -bse1";
       Log.d(TAG, "cmd = " + cmd);
       And7zCommand(cmd, listener);
    }
    
    /**
     * ./7za x /sdcard/update.zip -o/sdcard/ -psecret -aoa -bsp2
     * @param srcPath
     * @param dstPath
     * @param pwd
     * @param listener
     */
    public void And7zDecompression(String srcPath, String dstPath, String pwd, And7zCallback listener){
        String cmd = "/data/user/7za x " + srcPath + " -o" + dstPath + " -p" + pwd + " -aoa -bsp2 -bse1";
        And7zCommand(cmd, listener);
    }
    
    private Handler mAnd7zHandler = new Handler(){
        
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_7Z_OUTPUT) {
                String lineDetail = (String)msg.obj;
                Log.d(TAG, "======" + lineDetail);
                
                if(lineDetail.trim().isEmpty()){
                    return;
                }
                
                if(mAnd7zCallback != null){
                    if(msg.arg1 == AND_7Z_FAILED){
                        mAnd7zCallback.onResult(AND_7Z_FAILED, lineDetail);
                    }else{
                        if(lineDetail.contains("Everything is Ok")){
                            mAnd7zCallback.onResult(AND_7Z_SUCCEED, lineDetail);
                        }else if(lineDetail.contains("WARNING:") || lineDetail.contains("ERROR:")){
                            mAnd7zCallback.onResult(AND_7Z_FAILED, lineDetail);
                        }else{
                            mAnd7zCallback.onResult(AND_7Z_DETAIL, lineDetail);
                        }
                    }
                }
            } else if (msg.what == WHAT_7Z_PROGRESS) {
                String lineProgress = (String)msg.obj;
                Log.d(TAG, "******" + lineProgress);
                int pos = lineProgress.indexOf("%", 1);
                
                if(pos != -1){
                    String progress = lineProgress.substring(pos - 1, pos).trim();
                    
                    if(pos > 1){
                        if(Character.isDigit(lineProgress.charAt(pos - 2))){
                            progress = lineProgress.substring(pos - 2, pos).trim();
                        }
                    }
                    
                    if(pos > 2){
                        if(Character.isDigit(lineProgress.charAt(pos - 3))){
                            progress = lineProgress.substring(pos - 3, pos).trim();
                        }
                    }
                    
                    if(mAnd7zCallback != null){
                        mAnd7zCallback.onProgress(Integer.parseInt(progress));
                    }
                }
            }
        }
    };
    
    private synchronized void And7zCommand(String cmd, And7zCallback listener){
        
        mAnd7zCallback = listener;
        
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(cmd);

            //获取进程的标准输入流
            final InputStream isInput = process.getInputStream();
            //获取进程错误流
            final InputStream isError = process.getErrorStream();
                
            //启动2个线程，一个线程负责读标准输出流，一个负责读标准错误流
            new Thread(new Runnable() {
                    
                @Override
                public void run() {
                    BufferedReader brInput = null;
                    try {
                        brInput = new BufferedReader(new InputStreamReader(isInput, "utf-8"));
                        String line = null;
                        while((line = brInput.readLine()) != null) {
                            mAnd7zHandler.sendMessage(mAnd7zHandler.obtainMessage(WHAT_7Z_OUTPUT, AND_7Z_DETAIL, 0, line));
                            //Log.d(TAG, line);
                        }
                    } catch(Exception e) {
                        String exception = Log.getStackTraceString(e);
                        //Log.d(TAG, exception);
                        mAnd7zHandler.sendMessage(mAnd7zHandler.obtainMessage(WHAT_7Z_OUTPUT, AND_7Z_FAILED, 0, exception));
                    } finally {
                        if (brInput != null) {
                            try {
                                brInput.close();
                            } catch (Exception e){
                                Log.d(TAG, Log.getStackTraceString(e));
                            }
                        }
                    }
                }
            }).start();
                
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    BufferedReader brError = null;
                    try {
                        brError = new BufferedReader(new InputStreamReader(isError, "utf-8"));
                        // 一次读多个字符
                        char[] tempChars = new char[7];
                        int charread = 0;
                        while((charread = brError.read(tempChars)) != -1) {
                            mAnd7zHandler.sendMessage(mAnd7zHandler.obtainMessage(WHAT_7Z_PROGRESS, String.valueOf(tempChars)));
                            //Log.d(TAG, String.valueOf(tempChars));
                        }
                    } catch(Exception e) {
                        String exception = Log.getStackTraceString(e);
                        //Log.d(TAG, exception);
                        mAnd7zHandler.sendMessage(mAnd7zHandler.obtainMessage(WHAT_7Z_OUTPUT, AND_7Z_FAILED, 0, exception));
                    } finally {
                        if (brError != null) {
                            try {
                                brError.close();
                            } catch (Exception e){
                                Log.d(TAG, Log.getStackTraceString(e));
                            }
                        }
                    }
                }
            }).start();    
    
            process.waitFor();
            process.destroy();
            
//            Log.d(TAG, "7za compression succeed.");
        } catch (Exception e1) {
            String exception = Log.getStackTraceString(e1);
            //Log.d(TAG, exception);
            mAnd7zHandler.sendMessage(mAnd7zHandler.obtainMessage(WHAT_7Z_OUTPUT, AND_7Z_FAILED, 0, exception));
            
            try {
                process.getInputStream().close();
                process.getErrorStream().close();
            } catch (IOException e2) {
                Log.d(TAG, Log.getStackTraceString(e2));
            }
        }
    }
    
}
