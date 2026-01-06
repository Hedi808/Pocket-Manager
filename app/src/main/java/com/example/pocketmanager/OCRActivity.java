package com.example.pocketmanager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OCRActivity extends AppCompatActivity {

    private static final String API_KEY = "YOUR_API_KEY_HERE"; // Put your OCR.space API key here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        // Assuming you already have a file you want to send for OCR processing.
        File file = new File(getExternalFilesDir(null), "receipt.jpg"); // Replace with actual image path
        if (!file.exists()) {
            Log.e("OCR Error", "File does not exist!");
            return;
        }

        // Prepare the image file for upload
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        // Get the Retrofit client and create the OCR API service
        OCRApiService apiService = ApiClient.getClient().create(OCRApiService.class);

        // Make the API call to OCR.space
        Call<ResponseBody> call = apiService.getOCRResult(body, API_KEY);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Get OCR response as a string
                        String result = response.body().string();
                        Log.d("OCR Result", result);

                        // Here you can parse the result and extract the OCR text
                        // Example parsing the response (assuming it's in JSON format)
                        if (result != null && !result.isEmpty()) {
                            Toast.makeText(OCRActivity.this, "OCR Result: " + result, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(OCRActivity.this, "No text detected", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e("OCR Error", "Error parsing OCR result", e);
                    }
                } else {
                    Log.e("OCR Error", "OCR request failed with code: " + response.code());
                    Toast.makeText(OCRActivity.this, "OCR request failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("OCR Error", "OCR request failed: " + t.getMessage());
                Toast.makeText(OCRActivity.this, "OCR request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
