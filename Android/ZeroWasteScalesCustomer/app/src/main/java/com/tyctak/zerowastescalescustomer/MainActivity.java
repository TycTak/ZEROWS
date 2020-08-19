package com.tyctak.zerowastescalescustomer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    final MainActivity activity = this;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    ZXingScannerView qrCodeScanner;
    ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.barcodeReader);
        final TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
        qrCodeScanner = (ZXingScannerView) findViewById(R.id.qrCodeScanner);
        qrCodeImageView = (ImageView) findViewById(R.id.qrCodeImageView);
        setScannerProperties();

        barcodeDetector = new BarcodeDetector.Builder(activity).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(activity, barcodeDetector).setRequestedPreviewSize(640, 480).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                try {
                    cameraSource.start(holder);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if (qrCodes.size() != 0) {
                    txtMessage.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            String value = qrCodes.valueAt(0).displayValue;
                            if (!value.equals(txtMessage.getText())) {
                                vibrator.vibrate(1000);
                                txtMessage.setText(value);
                            }
                        }
                    });
                }
            }
        });
    }

    private void setScannerProperties() {
        qrCodeScanner.setFormats(listOf(BarcodeFormat.QR_CODE));
        qrCodeScanner.setAutoFocus(true);
        qrCodeScanner.setLaserColor(R.color.colorAccent);
        qrCodeScanner.setMaskColor(R.color.colorAccent);
//        if (Build.MANUFACTURER.equals(HUAWEI, ignoreCase = true))
//            qrCodeScanner.setAspectTolerance(0.5f);
    }

    public Integer MY_CAMERA_REQUEST_CODE = 0;

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                return;
            }
        }

        qrCodeScanner.startCamera();
        qrCodeScanner.setResultHandler(activity);
    }

    @Override
    public void handleResult(Result result) {
        if (result != null) {
            Toast.makeText( this, result.getText(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeScanner.stopCamera();
    }

    public void btnGenerate(View view) {
//        if (checkEditText()) {
            //val user = UserObject(fullName = fullNameEditText.text.toString(), age = Integer.parseInt(ageEditText.text.toString()))
            String value = "HERE IT IS"; //Gson().toJson(user)
            Bitmap bitmap = QRCodeHelper
                    .newInstance(this)
                    .setContent(value)
                    .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
                    .setMargin(2)
                    .getQRCOde();

            qrCodeImageView.setImageBitmap(bitmap);
//        }
    }
}
