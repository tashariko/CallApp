package in.tasha.calllogs.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.tasha.calllogs.model.CallModel;
import in.tasha.calllogs.adapter.CallTypeDetailAdapter;
import in.tasha.calllogs.R;
import in.tasha.calllogs.util.RetrieveCallForHome;

/**
 * Created by Puru Chauhan on 29/11/16.
 */

public class CallListActivity extends AppCompatActivity {

    private static final int PERMISSION_SMS_CODE = 123;
    @BindView(R.id.callList)
    ListView callList;

    @BindView(R.id.titleText)
    TextView titleText;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String type="";

    CallTypeDetailAdapter adapter;
    ArrayList<CallModel> callModelsArrayList=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        type=getIntent().getExtras().getString("type");

        handleTitle();

        callList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+callModelsArrayList.get(position).otherNumber));
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.d("DebugInfo: Permission", "Location Permission Required");
            ActivityCompat.requestPermissions(CallListActivity.this, new String[]{Manifest.permission.READ_CALL_LOG,}, PERMISSION_SMS_CODE);
            return;
        } else {
            getData();
        }
    }

    private void handleTitle() {
        if(type.equals("1")){
            titleText.setText("Incoming Calls");
        }else if(type.equals("2")){
            titleText.setText("Outgoing Calls");
        }else if(type.equals("3")){
            titleText.setText("Missed Calls");
        }
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new RetrieveCallForHome().getCallsFromType(type, getApplicationContext(), new RetrieveCallForHome.CallTypeDetailCallback() {
                    @Override
                    public void list(ArrayList<CallModel> adapterList) {

                        handleForSpecificType(adapterList);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter=new CallTypeDetailAdapter(getApplicationContext(),R.layout.list_item_detail,callModelsArrayList);
                                callList.setAdapter(adapter);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void handleForSpecificType(ArrayList<CallModel> adapterList) {
        callModelsArrayList.clear();
        for(CallModel model:adapterList){
            if(type.equals(String.valueOf(model.type))){
                callModelsArrayList.add(model);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_SMS_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getData();
                    Log.d("Request Permission", "GRANTED");
                } else {
                    Toast.makeText(this, "Without permission the app will not work.", Toast.LENGTH_SHORT).show();
                    CallListActivity.this.finish();
                    Log.d("Request Permission", "DENIED");
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }
}
