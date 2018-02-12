/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.example.quizexample;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.udacity.example.droidtermsprovider.DroidTermsExampleContract;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private Cursor mData;

    private int mCurrentState;

    private Button mButton;

    private int mDefCol;
    private int mWordCol;

    private TextView mDefinitionTextView;
    private TextView mWordTextView;

    private final int STATE_HIDDEN = 0;
    private final int STATE_SHOWN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.button_next);
        mDefinitionTextView = (TextView) findViewById(R.id.text_view_definition);
        mWordTextView = (TextView) findViewById(R.id.text_view_word);

        new WordFetchTask().execute();
    }

    public void onButtonClick(View view) {

        switch (mCurrentState) {
            case STATE_HIDDEN:
                showDefinition();
                break;
            case STATE_SHOWN:
                nextWord();
                break;
        }
    }

    public void nextWord() {
        if (mData != null) {

            if (!mData.moveToNext()) {
                mData.moveToFirst();
            }

            mDefinitionTextView.setVisibility(View.INVISIBLE);
            mButton.setText(getString(R.string.show_definition));
            mWordTextView.setText(mData.getString(mWordCol));
            mDefinitionTextView.setText(mData.getString(mDefCol));

            mCurrentState = STATE_HIDDEN;
        }
    }

    public void showDefinition() {

        mButton.setText(getString(R.string.next_word));

        mDefinitionTextView.setVisibility(View.VISIBLE);
        mCurrentState = STATE_SHOWN;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mData.close();
    }

    public class WordFetchTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {

            ContentResolver resolver = getContentResolver();

            Cursor cursor = resolver.query(DroidTermsExampleContract.CONTENT_URI,
                    null, null, null, null);
            return cursor;
        }


        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            mData = cursor;
            mDefCol = mData.getColumnIndex(DroidTermsExampleContract.COLUMN_DEFINITION);
            mWordCol = mData.getColumnIndex(DroidTermsExampleContract.COLUMN_WORD);

            nextWord();
        }
    }

}
