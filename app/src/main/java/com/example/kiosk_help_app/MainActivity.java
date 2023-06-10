package com.example.kiosk_help_app;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kiosk_help_app.adapters.ChatAdapter;
import com.example.kiosk_help_app.helpers.SendMessageInBg;
import com.example.kiosk_help_app.interfaces.BotReply;
import com.example.kiosk_help_app.models.Message;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.example.kiosk_help_app.alone.fastfood.AloneFastfoodSelectStorePackageActivity;

public class MainActivity extends AppCompatActivity implements BotReply {
    TextToSpeech _tts;
    RecyclerView chatView; //챗봇 화면
    ChatAdapter chatAdapter;
    List<Message> messageList = new ArrayList<>();
    EditText editMessage;
    ConstraintLayout constraintLayout;
    ConstraintLayout constraintLayout2;
    ImageView imageView;
    int mic_status = 0;
    Handler handler;
    Button btn_start;
    ImageButton btnSend;
    Button voiceButton;


    //dialogFlow
    private SessionsClient sessionsClient;
    private SessionName sessionName; // session name은uuid와project id를 사용
    private String uuid = UUID.randomUUID().toString(); //유일한 식별자 생성
    private String TAG = "Mainactivity"; //현재 사용하는 클래스에tag상수 선언 규칙

    private String command ="default";
    private int cntTime =0;

    final int PERMISSION =1;
    RecognitionListener listener;
    SpeechRecognizer mRecognizer;
    Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatView = findViewById(R.id.chatView);
        editMessage = findViewById(R.id.editMessage);
        btn_start = findViewById(R.id.btn_start);
        constraintLayout = findViewById(R.id.constraintLayout);
        btnSend = findViewById(R.id.btnSend);
        voiceButton=findViewById(R.id.voiceButton);

        chatAdapter = new ChatAdapter(messageList, this);
        chatView.setAdapter(chatAdapter);

        editMessage.setVisibility(View.VISIBLE);



        //퍼미션 체크( 인터넷, 오디오)
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

//RecognizerIntent 객체 생성
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko_KR");



    voiceButton.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick (View v){
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);
        }
    });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
            Intent intent = new Intent(getApplicationContext(), AloneFastfoodSelectStorePackageActivity.class);
            startActivity(intent);
        }
        });

        listener = new RecognitionListener() {
            @Override
            public void onRmsChanged(float rmsdB) {
// TODO Auto-generated method stub

            }
            @Override
            public void onResults(Bundle results) {
// TODO Auto-generated method stub
                // 아래 코드는 음성인식된 결과를ArrayList로 모아옵니다.
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String result="";
                // 이후for문으로textView에setText로 음성인식된 결과를 수정해줍니다.
                for (int i = 0; i < matches.size(); i++) {
                    result=matches.get(i);
                }
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

                //command 를default로 설정
                setCommand("default");
                if (!result.isEmpty()) {
//메시지를 담아sendMessageToBot() 호출
                    sendMessageToBot(result);
                }
            }
            @Override
            public void onReadyForSpeech (Bundle params){
// TODO Auto-generated method stub

            }

            @Override
            public void onPartialResults (Bundle partialResults){
// TODO Auto-generated method stub

            }

            @Override
            public void onEvent ( int eventType, Bundle params){
// TODO Auto-generated method stub

            }

            @Override
            public void onError ( int error){
// TODO Auto-generated method stub
                String message;

                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "오디오 에러";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "클라이언트 에러";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "퍼미션 없음";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "네트워크 에러";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "네트웍 타임아웃";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "찾을 수 없음";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RECOGNIZER가 바쁨";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "서버가 이상함";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "말하는 시간초과";
                        break;
                    default:
                        message = "알 수 없는 오류임";
                        break;
                }

                Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onEndOfSpeech () {
// TODO Auto-generated method stub

            }

            @Override
            public void onBufferReceived ( byte[] buffer){
// TODO Auto-generated method stub

            }

            @Override
            public void onBeginningOfSpeech () {
// TODO Auto-generated method stub

            }
        };




        btnSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
//command 를default로 설정
                setCommand("default");

                String message = editMessage.getText().toString();
                if (!message.isEmpty()) {
//UI or something to do Task
                    editMessage.setText("");
                    //메시지를 담아sendMessageToBot() 호출
                    sendMessageToBot(message);
                } else {
                    Toast.makeText(MainActivity.this, "보낼 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //main Thread에서만UI 작업을 할 수 있으므로Handler설정
        boolean handler = new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
//UI THREAD CODE HERE
                Toast.makeText(MainActivity.this, "따르릉~ 알람시간이 되었습니다. 알람 쿼리를 가져옵니다!", Toast.LENGTH_SHORT).show();
            }
        });



        //dialogflowAgent key정보Setup
        setUpBot();
    }

    //credential(GoogleService 자격 증명서) 파일을 통해session 설정
    private void setUpBot () {
        try {
            InputStream stream = this.getResources().openRawResource(R.raw.credential);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            sessionName = SessionName.of(projectId, uuid);

            Log.d(TAG, "projectId : " + projectId);
        } catch (Exception e) {
            Log.d(TAG, "setUpBot: " + e.getMessage());
        }
    }

    //dialogflow로message를 보내는 메서드
    private void sendMessageToBot (String message){
        QueryInput input = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(message).setLanguageCode("ko-KR")).build();
        new SendMessageInBg(this, sessionName, sessionsClient, input).execute();
    }

    @Override
    public void callback (DetectIntentResponse returnResponse){
//dialogflowAgent와 통신 성공한 경우
        if(returnResponse!=null) {
            String botReply = returnResponse.getQueryResult().getFulfillmentText();
            if(!botReply.isEmpty()){
                messageList.add(new Message(botReply, true));
                chatAdapter.notifyDataSetChanged();
                Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);
            }else {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "failed to connect!", Toast.LENGTH_SHORT).show();
        }
    }

    public void setCommand (String command){
        this.command = command;
    }

}


