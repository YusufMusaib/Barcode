package com.example.barcode;

import androidx.appcompat.app.AppCompatActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barcode.Model.QRGeoModel;
import com.example.barcode.Model.QRUrlModel;
import com.example.barcode.Model.QRVCardModel;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scannerView=(ZXingScannerView)findViewById(R.id.zxscan);
        txtResult=(TextView)findViewById(R.id.txt_result);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(MainActivity.this);
                        scannerView.startCamera();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this,"PEMERSSION DENIED",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        processRawResult(rawResult.getText()); 
        scannerView.startCamera();
    }

    private void processRawResult(String text) {
        if(text.startsWith("BEGIN:")) {
            String[] tokens = text.split("\n");
            QRVCardModel qrvCardModel =new QRVCardModel();
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].startsWith("BEGIN:")) {
                    qrvCardModel.setType(tokens[i].substring("BEGIN:".length()));
                    txtResult.setText(qrvCardModel.getType());
                } else if (tokens[i].startsWith("N:")) {
                    qrvCardModel.setName(tokens[i].substring("N:".length()));
                    txtResult.setText(qrvCardModel.getType());
                } else if (tokens[i].startsWith("ORG:")) {
                    qrvCardModel.setOrg(tokens[i].substring("ORG:".length()));
                    txtResult.setText(qrvCardModel.getType());
                } else if (tokens[i].startsWith("TEL:")) {
                    qrvCardModel.setTel(tokens[i].substring("TEL:".length()));
                    txtResult.setText(qrvCardModel.getType());
                } else if (tokens[i].startsWith("URL:")) {
                    qrvCardModel.setUrl(tokens[i].substring("URL:".length()));
                    txtResult.setText(qrvCardModel.getType());
                } else if (tokens[i].startsWith("EMAIL:")) {
                    qrvCardModel.setEmail(tokens[i].substring("EMAIL:".length()));
                    txtResult.setText(qrvCardModel.getType());
                } else if (tokens[i].startsWith("ADR:")) {
                    qrvCardModel.setAddress(tokens[i].substring("ADR:".length()));
                    txtResult.setText(qrvCardModel.getType());
                } else if (tokens[i].startsWith("NOTE:")) {
                    qrvCardModel.setNote(tokens[i].substring("NOTE:".length()));
                } else if (tokens[i].startsWith("SUMMARY:")) {
                    qrvCardModel.setSummary(tokens[i].substring("SUMMARY:".length()));
                } else if (tokens[i].startsWith("DTSTART:")) {
                    qrvCardModel.setDtstart(tokens[i].substring("DTSTART:".length()));
                } else if (tokens[i].startsWith("DTEND:")) {
                    qrvCardModel.setDtend(tokens[i].substring("DTEND:".length()));
                }
                txtResult.setText(qrvCardModel.getType());
            }
        }
            else if (text.startsWith("http://") ||
                text.startsWith("https://") ||
                text.startsWith("www."))
        {
             QRUrlModel qrurlModel= new QRUrlModel(text);
             txtResult.setText(qrurlModel.getUrl());
        }
            else if (text.startsWith("geo:"))
        {
            QRGeoModel qrGeoModel=new QRGeoModel();
            String delims = "[ , ?q= ]+";
            String tokens[]= text.split(delims);

            for(int i=0;i<tokens.length;i++)
            {
                if(tokens[i].startsWith("geo:"))
                {qrGeoModel.setLat(tokens[i].substring("geo:".length()));}
            }
            qrGeoModel.setLat(tokens[0].substring("geo:".length()));
            qrGeoModel.setGeo_place(tokens[1]);
            qrGeoModel.setLng(tokens[2]);
            txtResult.setText(qrGeoModel.getLat()+"/"+qrGeoModel.getLng());
        }
            else {
                txtResult.setText(text);
        }

        }

}
