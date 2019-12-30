package com.example.footballlive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.footballlive.adapter.AddressAdapter;
import com.example.footballlive.data.AddressData;
import com.example.footballlive.retrofit.ApiClient;
import com.example.footballlive.retrofit.GeocodeService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.footballlive.BaseActivity.naverApiSecretKey;

/* 변수명 수정 완료 */
public class AddressActivity extends AppCompatActivity {
    private final String TAG = "AddressActivity";
    RecyclerView listRecyclerView;
    EditText addressEditText;
    List<AddressData.AddData> addDataList;
    ImageView searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        listRecyclerView = findViewById(R.id.address_rv);
        addressEditText = findViewById(R.id.address_et);
        searchButton = findViewById(R.id.address_search_btn);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressEditText.getText().toString();
                getAddressData(address);
            }
        });

    }

    private void getAddressData(String address){

        GeocodeService geocodeService;

        geocodeService = ApiClient.getClient().create(GeocodeService.class);

        geocodeService.getGeocoding(address, getResources().getString(R.string.naver_map_api_client_key)
                , naverApiSecretKey).enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(Call<AddressData> call, Response<AddressData> response) {
                if (response.isSuccessful()) {
                    addDataList = response.body().getAddresses();
                    connectAdapter();
                    Log.e(TAG, "주소 데이터 얻어오기 성공 : " + response.body().addresses + " / " + response.raw().request().url().toString());
                }else {
                    Log.e(TAG, "주소 데이터 얻어오기 실패 : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AddressData> call, Throwable t) {

            }
        });
    }

    private void connectAdapter(){
        listRecyclerView.setAdapter(null); //TODO: 검증 필요 - 주석 처리 후 실행 확인

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        listRecyclerView.setLayoutManager(manager);
        AddressAdapter addressAdapter = new AddressAdapter(addDataList, AddressActivity.this, new JusoListner() {
            @Override
            public void pushData(AddressData.AddData data) {
                Intent intent = new Intent();
                intent.putExtra("jibun_address", data.getJibunAddress());
                intent.putExtra("road_address", data.getRoadAddress());
                intent.putExtra("latitude", data.getY());
                intent.putExtra("longitude", data.getX());
                setResult(RESULT_OK, intent);
                Log.e("AddressActivity", "주소 : " + data.getJibunAddress());
                finish();
            }
        });
        listRecyclerView.setAdapter(addressAdapter);
    }

    public interface JusoListner{
        void pushData(AddressData.AddData data);
    }
}
