package com.s23010255.waste_watcher;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class DailyChallengeActivity extends AppCompatActivity {

    private TextView questionText, totalPointsText;
    private RadioGroup optionsGroup;
    private Button submitBtn;

    private final List<QuizQuestion> quizList = new ArrayList<>();
    private int currentIndex = 0;

    private SharedPreferences prefs;
    private int totalPoints = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_challenge);

        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        submitBtn = findViewById(R.id.submitBtn);
        totalPointsText = findViewById(R.id.totalPointsText);

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        totalPoints = prefs.getInt("total_points", 0);
        updatePointsDisplay();

        loadQuizQuestions();
        showQuestion(currentIndex);

        submitBtn.setOnClickListener(v -> checkAnswer());
    }

    private void loadQuizQuestions() {
        quizList.add(new QuizQuestion("Which bin should a banana peel go into?",
                new String[]{"Plastic Bin", "Compost Bin", "Glass Bin"}, 1));
        quizList.add(new QuizQuestion("What is the best way to reduce plastic waste?",
                new String[]{"Reuse plastic bags", "Burn plastic", "Throw plastic in landfills"}, 0));
        quizList.add(new QuizQuestion("Recycling one glass bottle saves energy for how long?",
                new String[]{"5 minutes", "25 minutes", "1 hour"}, 1));
        quizList.add(new QuizQuestion("Which of these items is NOT recyclable?",
                new String[]{"Cardboard box", "Plastic bottle", "Used tissue"}, 2));
        quizList.add(new QuizQuestion("What does the 3R principle stand for?",
                new String[]{"Reduce, Recycle, Repair", "Reduce, Reuse, Recycle", "Reuse, Replace, Recycle"}, 1));
        quizList.add(new QuizQuestion("Which gas is mainly responsible for global warming?",
                new String[]{"Oxygen", "Nitrogen", "Carbon Dioxide"}, 2));
        quizList.add(new QuizQuestion("How long can it take for a plastic bag to decompose?",
                new String[]{"1 year", "20 years", "500 years"}, 2));
    }

    private void showQuestion(int index) {
        if(index >= quizList.size()) {
            Toast.makeText(this, "🎉 You completed all questions!", Toast.LENGTH_LONG).show();
            submitBtn.setEnabled(false);
            questionText.setText("All questions completed!");
            optionsGroup.removeAllViews();
            return;
        }

        QuizQuestion q = quizList.get(index);
        questionText.setText(q.getQuestion());

        optionsGroup.removeAllViews();
        for(String option : q.getOptions()) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            rb.setTextSize(16f);
            optionsGroup.addView(rb);
        }
        optionsGroup.clearCheck();
    }

    private void checkAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if(selectedId == -1) {
            Toast.makeText(this, "Please select an answer!", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedIndex = optionsGroup.indexOfChild(findViewById(selectedId));
        QuizQuestion q = quizList.get(currentIndex);

        if(selectedIndex == q.getCorrectAnswerIndex()) {
            Toast.makeText(this, "✅ Correct! +2 Points!", Toast.LENGTH_SHORT).show();
            totalPoints += 2;
            prefs.edit().putInt("total_points", totalPoints).apply();
            updatePointsDisplay();
        } else {
            Toast.makeText(this, "❌ Wrong! Correct answer: "
                    + q.getOptions()[q.getCorrectAnswerIndex()], Toast.LENGTH_LONG).show();
        }

        currentIndex++;
        showQuestion(currentIndex);
    }

    private void updatePointsDisplay() {
        totalPointsText.setText("Total Points: " + totalPoints);
    }
}
