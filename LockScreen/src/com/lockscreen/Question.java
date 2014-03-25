package com.lockscreen;

import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

public class Question {
	private String question;
	private ArrayList<String> answers;

	public Question(String question, ArrayList<String> answers) {
		this.answers = new ArrayList<String>();
		this.question = question;
		this.answers.addAll(answers);
		for (int j = 0; j < this.answers.size(); j++)
			Log.d("Answers", question + " " + this.answers.get(j));
	}

	public boolean isCorrect(String answer) {
		for (String s : answers)
			if (answer.equals(s))
				return true;
		return false;
	}
	
	public ArrayList<String> getAnswers() {
		return this.answers;
	}

	public String getQuestion() {
		return question;
	}
}
