package com.s23010255.waste_watcher;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class WeeklyChallengeActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_weekly_challenge);

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
        quizList.add(new QuizQuestion(
                "Which of the following is the best way to reduce household waste?",
                new String[]{"Recycling, reusing, and composting", "Throwing everything in the trash", "Burning plastic waste"}, 0));

        quizList.add(new QuizQuestion(
                "Which energy source produces the least greenhouse gases?",
                new String[]{"Wind energy", "Coal energy", "Petrol"}, 0));

        quizList.add(new QuizQuestion(
                "Which of these can be composted at home?",
                new String[]{"Banana peels and vegetable scraps", "Plastic bottles", "Glass jars"}, 0));

        quizList.add(new QuizQuestion(
                "Which item takes the longest to decompose in a landfill?",
                new String[]{"Plastic bag", "Paper towel", "Apple core"}, 0));

        quizList.add(new QuizQuestion(
                "What does the term 'upcycling' mean?",
                new String[]{"Turning waste into a higher-value product", "Throwing away old items", "Burning old materials"}, 0));

        quizList.add(new QuizQuestion(
                "Which is a correct way to reduce water consumption at home?",
                new String[]{"Fix leaking taps", "Leave taps running", "Take longer showers"}, 0));

        quizList.add(new QuizQuestion(
                "Which type of bag is most eco-friendly?",
                new String[]{"Reusable cloth bag", "Single-use plastic bag", "Paper bag used once"}, 0));
    }

    private void showQuestion(int index) {
        if(index >= quizList.size()) {
            Toast.makeText(this, "🎉 You completed all weekly challenges!", Toast.LENGTH_LONG).show();
            submitBtn.setEnabled(false);
            questionText.setText("All weekly questions completed!");
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
            Toast.makeText(this, "✅ Correct! +3 Points!", Toast.LENGTH_SHORT).show();
            totalPoints += 3;
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
