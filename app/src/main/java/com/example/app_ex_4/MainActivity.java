package com.example.app_ex_4;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private EditText editTextUnits, editTextMobile;
    private TextView textViewResult, textViewBreakdown;
    private static final int SMS_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUnits = findViewById(R.id.editTextUnits);
        editTextMobile = findViewById(R.id.editTextMobile);
        textViewResult = findViewById(R.id.textViewResult);
        textViewBreakdown = findViewById(R.id.textViewBreakdown);
        Button buttonCalculate = findViewById(R.id.buttonCalculate);

        buttonCalculate.setOnClickListener(v -> {
            if (validateInput()) {
                int units = Integer.parseInt(editTextUnits.getText().toString());
                String mobile = editTextMobile.getText().toString();
                double billAmount = calculateBill(units);
                String breakdown = getBreakdown(units);
                textViewBreakdown.setText("Breakdown: \n" + breakdown);
                textViewResult.setText("Total Bill: Rs. " + billAmount);

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                } else {
                    sendSMS(mobile, "Your electricity bill amount is Rs. " + billAmount);
                }
            }
        });
    }

    private boolean validateInput() {
        if (editTextUnits.getText().toString().isEmpty() || editTextMobile.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter both units and mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private double calculateBill(int units) {
        if (units <= 100) return units * 2.50;
        if (units <= 200) return 100 * 2.50 + (units - 100) * 3.00;
        if (units <= 300) return 100 * 2.50 + 100 * 3.00 + (units - 200) * 3.50;
        return 100 * 2.50 + 100 * 3.00 + 100 * 3.50 + (units - 300) * 4.00;
    }

    private String getBreakdown(int units) {
        StringBuilder breakdown = new StringBuilder();
        if (units <= 100) {
            breakdown.append(units).append(" x 2.50 = Rs. ").append(units * 2.50);
        } else if (units <= 200) {
            breakdown.append("100 x 2.50 = Rs. 250\n").append((units - 100)).append(" x 3.00 = Rs. ").append((units - 100) * 3.00);
        } else if (units <= 300) {
            breakdown.append("100 x 2.50 = Rs. 250\n100 x 3.00 = Rs. 300\n").append((units - 200)).append(" x 3.50 = Rs. ").append((units - 200) * 3.50);
        } else {
            breakdown.append("100 x 2.50 = Rs. 250\n100 x 3.00 = Rs. 300\n100 x 3.50 = Rs. 350\n").append((units - 300)).append(" x 4.00 = Rs. ").append((units - 300) * 4.00);
        }
        return breakdown.toString();
    }

    private void sendSMS(String mobile, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mobile, null, message, null, null);
            Toast.makeText(this, "SMS sent to " + mobile, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "SMS failed to send", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS Permission Granted. Try sending again.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "SMS Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }
}
