package com.lockscreen;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class LockScreenAppActivity extends Activity {

	/** Called when the activity is first created. */
	KeyguardManager.KeyguardLock k1;
	TextView question;
	EditText answer;
	Button skip, submit;
	int currentQuestion;
	int numQuestions;
	int countdown = 10;
	ArrayList<Question> questions;
	Random r = new Random();

	public void submitMessage(View view) {
		String a = formatAnswer(answer.getText().toString());
		if (questions.get(currentQuestion).isCorrect(a)) {
			toast("Correct!");
			finish();
		} else {
			toast("Incorrect!");
			displayAnswer();
		}
	}

	private void displayAnswer() {
		question.setText("Answer: "
				+ questions.get(currentQuestion).getAnswers().get(0));
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				finish();
			}
		}, 10000);
	}

	private void toast(String text) {
		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		toast.show();
	}

	private String formatAnswer(String answer) {
		answer = answer.replaceAll("\\s+", "");
		answer.toLowerCase();
		return answer;
	}

	private void loadQuestions(String file) {
		try {
			String questionBuffer;
			ArrayList<String> answerBuffer = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getAssets().open(file)));
			numQuestions = Integer.parseInt(reader.readLine());
			for (int i = 0; i < numQuestions; i++) {
				questionBuffer = reader.readLine();
				int numAnswers = Integer.parseInt(reader.readLine());
				for (int j = 0; j < numAnswers; j++) {
					answerBuffer.add(reader.readLine());
				}
				questions.add(new Question(questionBuffer, answerBuffer));
				answerBuffer.clear();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		this.getWindow().setType(
				WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG
						| WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onAttachedToWindow();
	}

	public void onCreate(Bundle savedInstanceState) {
		questions = new ArrayList<Question>();
		super.onCreate(savedInstanceState);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);
		
		// LOAD QUESTIONS
		loadQuestions("satVocab");
		question = (TextView) findViewById(R.id.question);
		answer = (EditText) findViewById(R.id.answer);
		submit = (Button) findViewById(R.id.submit);
		skip = (Button) findViewById(R.id.skip);

		currentQuestion = r.nextInt(numQuestions);
		question.setText(questions.get(currentQuestion).getQuestion());
		
		if (getIntent() != null && getIntent().hasExtra("kill")
				&& getIntent().getExtras().getInt("kill") == 1) {
			finish();
		}

		try {
			startService(new Intent(this, MyService.class));
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onBackPressed() {
		// Don't allow back to dismiss.
		return;
	}

	// only used in lockdown mode
	@Override
	protected void onPause() {
		super.onPause();

		// Don't hang around.
		// finish();
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
				|| (keyCode == KeyEvent.KEYCODE_POWER)
				|| (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
				|| (keyCode == KeyEvent.KEYCODE_CAMERA)) {
			// this is where I can do my stuff
			return true; // because I handled the event
		}
		if ((keyCode == KeyEvent.KEYCODE_HOME)) {

			return true;
		}

		return false;

	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_POWER
				|| (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
				|| (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
			return false;
		}
		if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {
			return true;
		}
		return false;
	}

	public void onDestroy() {
		super.onDestroy();
	}

}