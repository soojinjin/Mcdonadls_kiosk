package com.example.kiosk_help_app;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kiosk_help_app.adapters.ChatAdapter;
import com.example.kiosk_help_app.alone.fastfood.AloneFastfoodStoreActivity;
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
    Button startbtn;


    //dialogFlow
    private SessionsClient sessionsClient;
    private SessionName sessionName; // session name은uuid와project id를 사용
    private String uuid = UUID.randomUUID().toString(); //유일한 식별자 생성
    private String TAG = "mainactivity"; //현재 사용하는 클래스에tag상수 선언 규칙
    SpeechRecognizer sttrec = SpeechRecognizer.createSpeechRecognizer(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatView = findViewById(R.id.chatView);
        editMessage = findViewById(R.id.editMessage);
        chatAdapter = new ChatAdapter(messageList, this);
        chatView.setAdapter(chatAdapter);
        startbtn = findViewById(R.id.btn_start);


        editMessage.setVisibility(View.GONE);


        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AloneFastfoodSelectStorePackageActivity.class);
                startActivity(intent);
            }
        });
        setUpBot();
    }

    private TextToSpeech.OnInitListener ttsInitListener = new TextToSpeech.OnInitListener() //시작할떄 사용되는listnenr
    {
        @Override
        public void onInit(int status)
        {
            if (status == TextToSpeech.SUCCESS) {
                _tts.setOnUtteranceProgressListener(completedListener);
            }
            else{
                return;
            }


        }
    };
    private UtteranceProgressListener completedListener = new UtteranceProgressListener() {
        @SuppressLint("LongLogTag")
        @Override
        public void onStart(String utteranceId) {
            Log.i("MainActivity.java | UtteranceProgressListener", "|" + "new system TTS speak start" + "|");

        }

        @SuppressLint("LongLogTag")
        @Override
        public void onDone(String utteranceId) {

            Log.i("MainActivity.java | UtteranceProgressListener", "|" + "new system TTS speak complete" + "|");

            setHandler();
        }

        @Override
        public void onError(String utteranceId) {

        }
    };
    public void setHandler(){
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sttRecog();
            }
        }, 30);
    }

    public void sttRecog(){
        switch (mic_status){
            case 0:
            case 2:
                mic_status = 1;
                inputVoice();
                break;
            case 1:
                mic_status = 0;
                sttrec.destroy();
                break;
        }

    }
    //이건 아마stt 음성인식 기능에서 제공해주는 것
    public void inputVoice() {
        Log.d("inputVoice", "in function");
        try {

            Intent intentSTT = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intentSTT.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            intentSTT.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
            sttrec = SpeechRecognizer.createSpeechRecognizer(this);
            sttrec.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                }

                @Override
                public void onBeginningOfSpeech() {
                    Log.d("STTstart", "start here");
                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {
// toast("음성입력종료");
                    Log.d("STTend", "end here");
                }

                @Override
                public void onError(int error) {
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
                            speakResponse("다시 한번 말씀해주세요");
                            mic_status = 0;
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


                    Log.d("onError", "error : "+message);
                    mic_status = 1;
                    sttrec.destroy();
                }
                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> result = (ArrayList<String>) results.get(sttrec.RESULTS_RECOGNITION);
                    editMessage.setText( result.get(0) + "\n");
                    messageList.add(new Message(result.get(0).toString(),false));
                    Log.v(TAG, "result: "+result.get(0));
                    if(result.get(0).equals("네")){
                        startbtn.performClick();
                    }
                    sendMessageToBot(result.get(0));
                    Objects.requireNonNull(chatView.getAdapter()).notifyDataSetChanged(); //chatview에서 데이터 변환확인
                    Objects.requireNonNull(chatView.getLayoutManager())
                            .scrollToPosition(messageList.size() - 1);
                    mic_status = 2;
                    sttrec.destroy();
                    //onListeningCompleted();
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
            sttrec.startListening(intentSTT);
            new CountDownTimer(3000,1000){
                public void onTick(long m){
                }
                public void onFinish(){
                    sttrec.stopListening();
                }
            }.start();


        }
        catch (Exception e) {
            Log.e(TAG, e.toString() +" 에러메세지");
// Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }




    // 한번 받은 말이 넘어가나 시험
    @SuppressLint("LongLogTag")
    private void speakResponse(String botReply) {
        mic_status = 0;
        Log.i("MainActivity.java | speak", "|System TTS speak|" + botReply + "|");
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
        try {
            _tts.speak(botReply, TextToSpeech.QUEUE_FLUSH, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sttRecog();
    }



    private void setUpBot() {
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

    // message 를 봇에게 보내는 역할을 한다
    private void sendMessageToBot(String message) {
        QueryInput input = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(message).setLanguageCode("ko-KR")).build();
        new SendMessageInBg(this, sessionName, sessionsClient, input).execute();
    }



    //답변을 가져오는 부분
    @Override
    public void callback(DetectIntentResponse returnResponse) {
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
}