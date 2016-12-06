package in.tasha.calllogs.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.tasha.calllogs.model.CallModel;
import in.tasha.calllogs.R;
import in.tasha.calllogs.util.RetrieveCallForHome;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_SMS_CODE = 123;
    @BindView(R.id.incomingButton)
    CardView incomingButton;

    @BindView(R.id.outgoingButton)
    CardView outgoingButton;

    @BindView(R.id.missedButton)
    CardView missedButton;

    @BindView(R.id.incomingText)
    TextView incomingText;

    @BindView(R.id.outgoingText)
    TextView outgoingText;

    @BindView(R.id.missedText)
    TextView missedText;

    @BindView(R.id.refreshList)
    TextView refreshList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        listeners();

    }

    private void listeners(){
        //2
        outgoingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),CallListActivity.class);
                intent.putExtra("type","2");
                startActivity(intent);
            }
        });

        //1
        incomingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),CallListActivity.class);
                intent.putExtra("type","1");
                startActivity(intent);
            }
        });

        //3
        missedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),CallListActivity.class);
                intent.putExtra("type","3");
                startActivity(intent);
            }
        });

        refreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "refreshed", Toast.LENGTH_SHORT).show();
                askPermission();
            }
        });
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new RetrieveCallForHome().getCall(getApplicationContext(), new RetrieveCallForHome.CallDetailCallback() {
                    @Override
                    public void list(final CallModel model) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                incomingText.setText(String.valueOf(model.icTime)+" min ("+String.valueOf(model.icCnt)+")");
                                outgoingText.setText(String.valueOf(model.ocTime)+" min ("+String.valueOf(model.ocCnt)+")");
                                missedText.setText(String.valueOf(model.mcTime)+" min ("+String.valueOf(model.mcCnt)+")");
                            }
                        });
                    }
                });
            }
        }).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        askPermission();
    }

    private void askPermission(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.d("DebugInfo: Permission", "Location Permission Required");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALL_LOG,}, PERMISSION_SMS_CODE);
            return;
        } else {
            getData();
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
                    MainActivity.this.finish();
                    Log.d("Request Permission", "DENIED");
                }
            }
        }
    }

}
