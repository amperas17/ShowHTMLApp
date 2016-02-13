package com.amperas17.showhtmlapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements GetContentResultReceiver.Receiver {

    static final String TEXT_CONTENT_TAG = "textContentTag";
    static final String URL_PATH_TAG = "urlPathTag";
    static final String WEB_VIEW_VISIBILITY_TAG = "wvVisibility";

    RelativeLayout mRlMain;
    TextView mTvContent;
    Button mBtAsyncTask,mBtService,mBtWebView;
    ProgressBar mPbLoading;
    EditText mEtInputPath;
    WebView mWvContent;
    ScrollView mSvTvContent;

    GetContentResultReceiver mReceiver;

    Boolean mIsAsyncTaskRunning = false;
    Boolean mIsServiceRunning = false;
    Boolean mIsWebViewRunning = false;


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTvContent.setText(savedInstanceState.getString(TEXT_CONTENT_TAG));
        mEtInputPath.setText(savedInstanceState.getString(URL_PATH_TAG));
        mWvContent.restoreState(savedInstanceState);
        mIsWebViewRunning = savedInstanceState.getBoolean(WEB_VIEW_VISIBILITY_TAG);
        if (mIsWebViewRunning){
            mWvContent.setVisibility(View.VISIBLE);
            mSvTvContent.setVisibility(View.INVISIBLE);
        } else {
            mWvContent.setVisibility(View.INVISIBLE);
            mSvTvContent.setVisibility(View.VISIBLE);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvContent = (TextView) findViewById(R.id.tvContent);

        mPbLoading = (ProgressBar)findViewById(R.id.pbLoading);
        mEtInputPath = (EditText)findViewById(R.id.etInputPath);

        mBtAsyncTask = (Button) findViewById(R.id.btAsyncTask);
        mBtAsyncTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEditTextFocus(mEtInputPath);

                if (isNetworkConnected()) {
                    if (!mIsAsyncTaskRunning) {
                        new ProgressTask().execute(mEtInputPath.getText().toString());
                        mTvContent.setText("");
                        mPbLoading.setVisibility(View.VISIBLE);
                        mIsAsyncTaskRunning = true;

                        mWvContent.setVisibility(View.INVISIBLE);
                        mIsWebViewRunning = false;
                        mSvTvContent.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.async_running_message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mTvContent.setText(R.string.no_internet_message);
                }
            }
        });

        mBtService = (Button) findViewById(R.id.btService);
        mBtService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEditTextFocus(mEtInputPath);

                if (isNetworkConnected()) {
                    if (!mIsServiceRunning) {
                        mTvContent.setText("");
                        mPbLoading.setVisibility(View.VISIBLE);

                        mReceiver = new GetContentResultReceiver(new Handler());

                        mReceiver.setReceiver(MainActivity.this);

                        Intent intent = new Intent(MainActivity.this, GetContentService.class);
                        intent.putExtra(AppContract.PATH_TAG, mEtInputPath.getText().toString());
                        intent.putExtra(AppContract.RECEIVER_TAG, mReceiver);
                        startService(intent);
                        mIsServiceRunning = true;

                        mWvContent.setVisibility(View.INVISIBLE);
                        mIsWebViewRunning = false;
                        mSvTvContent.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(MainActivity.this, R.string.service_running_message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mTvContent.setText(R.string.no_internet_message);
                }
            }
        });

        mTvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEditTextFocus(mEtInputPath);
            }
        });

        mRlMain = (RelativeLayout)findViewById(R.id.rlMain);
        mRlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEditTextFocus(mEtInputPath);
            }
        });

        mSvTvContent = (ScrollView)findViewById(R.id.svTvContent);

        mWvContent = (WebView)findViewById(R.id.wvContent);
        mWvContent.getSettings().setSupportZoom(true);
        mWvContent.getSettings().setBuiltInZoomControls(true);
        mWvContent.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWvContent.setScrollbarFadingEnabled(true);
        mWvContent.getSettings().setLoadsImagesAutomatically(true);

        mWvContent.getSettings().setJavaScriptEnabled(true);
        mWvContent.setWebViewClient(new MyWebViewClient());


        mBtWebView = (Button)findViewById(R.id.btWebView);
        mBtWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEditTextFocus(mEtInputPath);
                mSvTvContent.setVisibility(View.INVISIBLE);
                mWvContent.setVisibility(View.VISIBLE);
                mWvContent.loadUrl(mEtInputPath.getText().toString());

                mWvContent.setVisibility(View.VISIBLE);
                mIsWebViewRunning = true;
            }
        });

    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        mTvContent.setText(resultData.getString(AppContract.HTML_CONTENT_TAG));
        mPbLoading.setVisibility(View.INVISIBLE);
        mIsServiceRunning = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TEXT_CONTENT_TAG, mTvContent.getText().toString());
        outState.putString(URL_PATH_TAG, mEtInputPath.getText().toString());
        mWvContent.saveState(outState);
        outState.putBoolean(WEB_VIEW_VISIBILITY_TAG, mIsWebViewRunning);
    }

    @Override
    public void onBackPressed() {
        if(mWvContent.canGoBack()) {
            mWvContent.goBack();
        } else {
            super.onBackPressed();
        }
    }

    class ProgressTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path) {

            String content = GetHttpContent.getContent(path[0]);
            return content;
        }
        @Override
        protected void onProgressUpdate(Void... items) {
        }
        @Override
        protected void onPostExecute(String content) {
            mTvContent.setText(content);
            mPbLoading.setVisibility(View.INVISIBLE);
            mIsAsyncTaskRunning=false;
        }

    }



    public void removeEditTextFocus(EditText editText){
        editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return (networkInfo==null?false:true);
    }


    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }

}
