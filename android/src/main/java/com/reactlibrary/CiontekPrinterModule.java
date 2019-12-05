package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import vpos.apipackage.PosApiHelper;
import vpos.apipackage.Print;
import vpos.apipackage.PrintInitException;

import android.content.Context;
import android.content.SharedPreferences;
//import android.util.Log;
import android.os.Handler;


public class CiontekPrinterModule extends ReactContextBaseJavaModule {

    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    SharedPreferences sp;
    Context mContext;
    private final ReactApplicationContext reactContext;
    private boolean m_bThreadFinished = true;
    private int RESULT_CODE = 0;
    private int cycle_num = 0;

    final int PRINT_TEST = 0;
    final int PRINT_UNICODE = 1;

    int ret = -1;

    PosApiHelper posApiHelper = PosApiHelper.getInstance();

    public CiontekPrinterModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "CiontekPrinter";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }

    private void setValue(int val) {
        sp = mContext.getSharedPreferences("Gray", mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("value", val);
        editor.commit();
    }

    private int getValue() {
        sp = mContext.getSharedPreferences("Gray", mContext.MODE_PRIVATE);
        int value = sp.getInt("value", 2);
        return value;
    }

    @ReactMethod
    public void testPrint() {
        Thread myThread = new Thread() {
            public void run() {
                ret = posApiHelper.PrintInit(2, 24, 24, 0);
                ret = posApiHelper.PrintSetFont((byte) 24, (byte) 24, (byte) 0x00);
            
        
                posApiHelper.PrintStr("中文:你好，好久不见。\n");
                posApiHelper.PrintStr("英语:Hello, Long time no see   ￡ ：2089.22\n");
                posApiHelper.PrintStr("意大利语Italian :Ciao, non CI vediamo da Molto Tempo.\n");
                posApiHelper.PrintStr("西班牙语:España, ¡Hola! Cuánto tiempo sin verte!\n");
                posApiHelper.PrintStr("Arabic:");
                posApiHelper.PrintStr("قل مرحبا عند مقابلتك");
                posApiHelper.PrintStr("الفبای فارسی گروه سی‌ودوگانهٔ");
                posApiHelper.PrintStr("سی‌ودوگانهٔ");
                posApiHelper.PrintStr("حروف الفبا یا حروف هجای فارسی می‌گویند");
                posApiHelper.PrintStr("الفبای فارسی گروه سی‌ودوگانهٔ حروف (اَشکال نوشتاری) در خط فارسی است که نمایندهٔ نگاشتن (همخوان‌ها یا صامت‌ها) در زبان فارسی است و");
                posApiHelper.PrintStr("است که نمایندهٔ نگاشتن (همخوان‌ها یا صامت‌ها) در زبان فارسی است و");
                posApiHelper.PrintStr("泰语:สวัสดีครับไม่เจอกันนานเลยนะ!\n");
                posApiHelper.PrintStr("法语:Bonjour! Ça fait longtemps!\n");
                posApiHelper.PrintStr("                                         \n");
                posApiHelper.PrintStr("                                         \n");
        
                try {
                    ret = posApiHelper.PrintStart();
                } catch (Exception ex) {
                    // BaseActivity.showSnackBar(null, "Printer cannot found", 1500);
                }

            }
       
        };
        myThread.start();
     
    }

    @ReactMethod
    public int checkPrinterStatus() {
        ret = posApiHelper.PrintCheckStatus();
        return ret;
    }

    
    
}

