package in.tasha.calllogs.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import java.util.ArrayList;

import in.tasha.calllogs.model.CallModel;

import static android.provider.BaseColumns._ID;

/**
 * Created by tashariko on 20/7/16.
 */

public class RetrieveCallForHome {

    public void getCall( Context context, CallDetailCallback callback) {

        CallModel callModel=new CallModel();
        initCallModel(callModel);

        String[] request = new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE,
                CallLog.Calls.DATE, CallLog.Calls.DURATION};
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, request, null, null, "date DESC");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String callType = "";
            while (!cursor.isAfterLast()) {

                callType= cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));

                if(callType.equals(String.valueOf(CallLog.Calls.MISSED_TYPE))){
                    callModel.mcCnt++;
                    callModel.mcTime+=(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION)))/60;
                }else if(callType.equals(String.valueOf(CallLog.Calls.INCOMING_TYPE))){
                    callModel.icCnt++;
                    callModel.icTime+=(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION)))/60;
                }else if(callType.equals(String.valueOf(CallLog.Calls.OUTGOING_TYPE))){
                    callModel.ocCnt++;
                    callModel.ocTime+=(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION)))/60;
                }

                callModel.otherNumber=cursor.getLong(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                cursor.moveToNext();
            }

            cursor.close();
        }

        callback.list(callModel);
    }


   public void getCallsFromType(String type,Context context,CallTypeDetailCallback callback){

       ArrayList<CallModel> adapterList=new ArrayList<>();

       String[] request = new String[]{_ID,CallLog.Calls.NUMBER, CallLog.Calls.TYPE,
               CallLog.Calls.DATE, CallLog.Calls.DURATION};
       ContentResolver contentResolver = context.getContentResolver();

       Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, request, null, null, "date DESC");

       if (cursor != null && cursor.getCount() > 0) {
           cursor.moveToFirst();
           String callType = "";
           while (!cursor.isAfterLast()) {
                CallModel callModel=new CallModel();
               callType= cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));

               if(callType.equals(String.valueOf(CallLog.Calls.MISSED_TYPE))){
                   callModel.mcCnt++;
               }else if(callType.equals(String.valueOf(CallLog.Calls.INCOMING_TYPE))){
                   callModel.icCnt++;
               }else if(callType.equals(String.valueOf(CallLog.Calls.OUTGOING_TYPE))){
                   callModel.ocCnt++;
               }

               callModel.id=cursor.getString(cursor.getColumnIndex(_ID));
               callModel.time=cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
               callModel.date=cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
               callModel.type=cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
               callModel.otherNumber=cursor.getLong(cursor.getColumnIndex(CallLog.Calls.NUMBER));

               adapterList.add(callModel);

               cursor.moveToNext();
           }

           cursor.close();
       }

       callback.list(adapterList);
    }

    private void initCallModel(CallModel callModel) {
        callModel.ocCnt=0;
        callModel.ocTime=0;

        callModel.icCnt=0;
        callModel.icTime=0;

        callModel.mcCnt=0;
        callModel.mcTime=0;
    }

    public interface CallDetailCallback{
        void list(CallModel model);
    }

    public interface CallTypeDetailCallback{
        void list(ArrayList<CallModel> adapterList);
    }

}
