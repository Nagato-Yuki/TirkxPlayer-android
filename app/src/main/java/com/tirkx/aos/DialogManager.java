package com.tirkx.aos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

/**
 * Created by Pakkapon on 22/5/2558.
 */

// Dialog Template

public class DialogManager {
    public static void authenError(final Context context, final MainActivity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("พบข้อผิดพลาด")
                .setMessage("เซิฟเวอร์ของ Tirkx ปฏิเสธการเชื่อมต่อของแอป แอปจะทำการปิดตัวเองลง คุณจำเป็นต้องเปิดแอปใหม่อีกครั้งหนึ่ง")
                .setCancelable(false)
                .setPositiveButton("ตกลง",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                });
        builder.create().show();
    }
}
