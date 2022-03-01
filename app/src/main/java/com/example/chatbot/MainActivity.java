package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RecyclerView chatsRV;
    private EditText userMsgEdt;
    private FloatingActionButton sendMsgFAB;
    private final String BOT_KEY="bot";
    private final String USER_KEY="user";
    private ArrayList<ChatsModel> chatsModelArrayList;
    private ChatRVAdapter chatRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatsRV=findViewById(R.id.idRVChats);
        userMsgEdt=findViewById(R.id.idEdtMsg);
        sendMsgFAB=findViewById(R.id.idFABSend);

        chatsModelArrayList= new ArrayList<>();
        chatRVAdapter=new ChatRVAdapter(chatsModelArrayList,this);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        chatsRV.setLayoutManager(linearLayoutManager);

        chatsRV.setAdapter(chatRVAdapter);
        sendMsgFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userMsgEdt.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter your message", Toast.LENGTH_SHORT).show();
                    return;
                }
                getResponse(userMsgEdt.getText().toString());
                userMsgEdt.setText("");
            }
        });

    }
    private void getResponse(String message){
        chatsModelArrayList.add(new ChatsModel(message,USER_KEY));
        chatRVAdapter.notifyDataSetChanged();
        String url= "http://api.brainshop.ai/get?bid=164283&key=RHc6Jy4Dsb0QM4Zf&uid=[uid]&msg="+message;
        String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit= new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitApi retrofitApi= retrofit.create(RetrofitApi.class);
        Call<MsgModel> call = retrofitApi.getMessage(url);
        call.enqueue(new Callback<MsgModel>() {

            @Override
            public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                if(response.isSuccessful()){
                    MsgModel model= response.body();
                    chatsModelArrayList.add(new ChatsModel(model.getCnt(),BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                    chatsRV.smoothScrollToPosition(chatRVAdapter.getItemCount() - 1);


                }
            }


            @Override
            public void onFailure(Call<MsgModel> call, Throwable t) {
                chatsModelArrayList.add(new ChatsModel("Please revert your question", BOT_KEY));
                chatsRV.smoothScrollToPosition(chatRVAdapter.getItemCount() - 1 );
                chatRVAdapter.notifyDataSetChanged();

            }
        });
    }
}