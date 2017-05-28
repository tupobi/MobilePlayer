package com.example.administrator.mobileplayer.aty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.util.JsonParser;
import com.example.administrator.mobileplayer.util.LogUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/5/28.
 */

public class AtySearch extends Activity {
    private EditText etInput;
    private Button btnVoiceReceive;
    private TextView tvSearch;
    private ProgressBar pbLoadingContentOfSearch;
    private TextView tvLoadingContentOfSearch;
    private TextView tvNotFoundData;
    private HashMap<String, String> mIatResults;

    public static void actionStart(Context context){
        context.startActivity(new Intent(context, AtySearch.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //该方法写错，可能导致界面一片空白！！
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView() {
        setContentView(R.layout.aty_search);
        // 将“12345678”替换成您申请的 APPID，申请地址：http://www.xfyun.cn // 请勿在“=”与 appid 之间添加任务空字符或者转义符
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=592a471a");

        etInput = (EditText)findViewById( R.id.et_input );
        btnVoiceReceive = (Button)findViewById( R.id.btn_voiceReceive );
        tvSearch = (TextView)findViewById( R.id.tv_search );
        pbLoadingContentOfSearch = (ProgressBar)findViewById( R.id.pb_loadingContentOfSearch );
        tvLoadingContentOfSearch = (TextView)findViewById( R.id.tv_loadingContentOfSearch );
        tvNotFoundData = (TextView)findViewById( R.id.tv_notFoundData );
        mIatResults = new LinkedHashMap<String, String>();

        tvSearch.setFocusable(true);
        tvSearch.setFocusableInTouchMode(true);
        tvSearch.requestFocus();
        tvSearch.requestFocusFromTouch();

        btnVoiceReceive.setOnClickListener(new MyOnClickListener());
        tvSearch.setOnClickListener(new MyOnClickListener());
    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (view == btnVoiceReceive){
//                Toast.makeText(AtySearch.this, "语音输入", Toast.LENGTH_SHORT).show();
                showVoiceRecognizerDialog();
            }else if (view == tvSearch){
                Toast.makeText(AtySearch.this, "点击搜索", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showVoiceRecognizerDialog() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解 //结果 //
//        mDialog.setParameter("asr_sch", "1"); //
//        mDialog.setParameter("nlp_version", "2.0");

        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener()); //4.显示dialog，接收语音输入
        mDialog.show();
    }

    //初始化接口
    class MyInitListener implements InitListener {

        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(AtySearch.this, "初始化失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //语音识别接口
    class MyRecognizerDialogListener implements RecognizerDialogListener {

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String result = recognizerResult.getResultString();
            LogUtil.e("result == " + result);
            printResult(recognizerResult);
        }

        @Override
        public void onError(SpeechError speechError) {
            String error = speechError.getErrorDescription();
            LogUtil.e("error == " + error);
        }
    }
    //打印结果
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        etInput.setText(resultBuffer.toString());
        etInput.setSelection(etInput.length());
    }
}
