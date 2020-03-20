/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ao.app.boleia.motorista.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ao.app.boleia.motorista.common.Common;
import ao.app.boleia.motorista.helper.MessageListener;

public class SmsReceiver extends BroadcastReceiver {


    final SmsManager sms = SmsManager.getDefault();
    private static String SMS_BODY_UNITEL= "UNITEL: Restam 20 MB de volume do teu plano de net. Obrigado.";
    private static MessageListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0; i<pdus.length; i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String message = "Sender : " + smsMessage.getDisplayOriginatingAddress()
                    + "Email From: " + smsMessage.getEmailFrom()
                    + "Emal Body: " + smsMessage.getEmailBody()
                    + "Display message body: " + smsMessage.getDisplayMessageBody()
                    + "Time in millisecond: " + smsMessage.getTimestampMillis()
                    + "Message: " + smsMessage.getMessageBody();
            mListener.messageReceived(message);

            if(smsMessage.getMessageBody().equals(SMS_BODY_UNITEL)){
                Intent in = new Intent("SmsMessage.intent.MAIN").
                        putExtra("smsMessage", smsMessage.getMessageBody());

                context.sendBroadcast(in);
            }

         /* Intent smsIntent = new Intent(context,ViagemActivity.class);
            smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            smsIntent.putExtra("Message" , smsMessage.getEmailBody());*/
            Toast.makeText(context,"SMS ----------- : " +smsMessage.getOriginatingAddress()+"\n"+smsMessage.getMessageBody(),Toast.LENGTH_LONG);
        }
    }

    public static void bindListener(MessageListener listener){
        mListener = listener;
    }
}