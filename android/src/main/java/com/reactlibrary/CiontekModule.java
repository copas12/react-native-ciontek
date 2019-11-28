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


public class CiontekModule extends ReactContextBaseJavaModule {

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

    public CiontekModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

//    private void init_Gray() {
//        int flag = getValue();
//        posApiHelper.PrintSetGray(flag);
//    }

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

    @Override
    public String getName() {
        return "Ciontek";
    }

    @ReactMethod
    public void testPrint() {
        if (printThread != null && !printThread.isThreadFinished()) {
            return;
        }

        printThread = new Print_Thread(PRINT_TEST);
        printThread.start();
    }

    @ReactMethod
    public int checkPrinterStatus() {
        ret = posApiHelper.PrintCheckStatus();
        return ret;
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }


    Handler handlers = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            printThread = new Print_Thread(PRINT_UNICODE);
            printThread.start();

            if (RESULT_CODE == 0) {
                editor = preferences.edit();
                editor.putInt("count", ++cycle_num);
                editor.commit();
            }

            handlers.postDelayed(runnable, 9000);
        }
    };

    Print_Thread printThread = null;

    public class Print_Thread extends Thread {
        String content = "1234567890";
        int type;

        public boolean isThreadFinished() {
            return m_bThreadFinished;
        }

        public Print_Thread(int type) {
            this.type = type;
        }

        public void run() {
            synchronized (this) {
                m_bThreadFinished = false;
                try {
                    ret = posApiHelper.PrintInit();
                } catch (PrintInitException e) {
                    e.printStackTrace();
                }

                ret = getValue();

                posApiHelper.PrintSetGray(ret);

                ret = posApiHelper.PrintCheckStatus();
                if (ret == -1) {
                    RESULT_CODE = -1;
//                    Log.e(tag, "Lib_PrnCheckStatus fail, ret = " + ret);
//                    SendMsg("Error, No Paper ");
                    m_bThreadFinished = true;
                    return;
                } else if (ret == -2) {
                    RESULT_CODE = -1;
//                    Log.e(tag, "Lib_PrnCheckStatus fail, ret = " + ret);
//                    SendMsg("Error, Printer Too Hot ");
                    m_bThreadFinished = true;
                    return;
                } else if (ret == -3) {
                    RESULT_CODE = -1;
//                    Log.e(tag, "voltage = " + (BatteryV * 2));
//                    SendMsg("Battery less :" + (BatteryV * 2));
                    m_bThreadFinished = true;
                    return;
                }
                else {
                    RESULT_CODE = 0;
                }

                switch (type) {
                    case PRINT_TEST:
                        posApiHelper.PrintSetFont((byte) 24, (byte) 24, (byte) 0x00);

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

                        ret = posApiHelper.PrintStart();

//                        Log.d("", "Lib_PrnStart ret = " + ret);

                        if (ret != 0) {
                            RESULT_CODE = -1;
                        } else {
                            RESULT_CODE = 0;
                        }

                        break;
                    default:
                        break;
                }
                m_bThreadFinished = true;
            }
        }
    }

}
