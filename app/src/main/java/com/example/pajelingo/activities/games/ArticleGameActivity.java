package com.example.pajelingo.activities.games;

import static com.example.pajelingo.util.Tools.getRandomItemFromList;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.ArticleDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Article;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;

import java.util.ArrayList;
import java.util.List;

public class ArticleGameActivity extends GameActivity {

    private Language language;
    private Word word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setup() {
        setContentView(R.layout.activity_single_language_choice);

        Spinner languageSpinner = findViewById(R.id.language_choice_spinner);
        Button playButton = findViewById(R.id.play_button);
        // Fill the spinner with all languages available
        languageDao.getAllRecordsTask(new OnResultListener<List<Language>>() {
            @Override
            public void onResult(List<Language> result) {
                List<String> languageNames = new ArrayList<>();
                for (Language language : result){
                    if (!language.getLanguageName().equals("English")){
                        languageNames.add(language.getLanguageName());
                    }
                }
                // Verify if there are at least one language
                if (result.isEmpty()){
                    finishActivityNotEnoughResources();
                    return;
                }
                // Fill the adapter with the name of all the languages available
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ArticleGameActivity.this,
                        android.R.layout.simple_spinner_item, languageNames);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the baseLanguageSpinner
                languageSpinner.setAdapter(adapter);
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String languageChosenName = languageSpinner.getSelectedItem().toString();
                        playButton.setOnClickListener(null);
                        languageDao.getLanguageByNameAsyncTask(languageChosenName, new OnResultListener<Language>() {
                            @Override
                            public void onResult(Language result) {
                                language = result;
                                startGame();
                            }
                        }).execute();
                    }
                });
            }
        }).execute();
    }

    @Override
    protected void startGame() {
        setContentView(R.layout.activity_article_game);

        EditText answerInputEditText = findViewById(R.id.answer_input);
        TextView wordTextView = findViewById(R.id.word_text_view);
        Button checkButton = findViewById(R.id.check_button);

        answerInputEditText.setHint(R.string.article_game_hint_input_text);

        WordDao wordDao = AppDatabase.getInstance(this).getWordDao();
        wordDao.getNounsByLanguageAsyncTask(language.getId(), new OnResultListener<List<Word>>() {
            @Override
            public void onResult(List<Word> result) {
                // Verify if there are at least one word
                if (result.isEmpty()){
                    finishActivityNotEnoughResources();
                    return;
                }
                word = getRandomItemFromList(result);
                wordTextView.setText(word.getWordName());

                checkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkButton.setOnClickListener(null);
                        // Trim the answer since the user may accidentally insert a space before of after the article
                        String userAnswer = answerInputEditText.getText().toString().trim();
                        verifyAnswer(userAnswer);
                    }
                });
            }
        }).execute();
    }

    @Override
    protected void verifyAnswer(Object answer) {
        setContentView(R.layout.activity_feedback);

        TextView feedbackTextView = findViewById(R.id.feedback_text_view);
        CardView feedbackCardView = findViewById(R.id.feedback_rounded_background);
        Button newWordButton = findViewById(R.id.new_word_button);

        ArticleDao articleDao = AppDatabase.getInstance(this).getArticleDao();
        // Gets the article related to the word and verifies user's answer
        articleDao.findRecordByIdTask(word.getIdArticle(), new OnResultListener<Article>() {
            @Override
            public void onResult(Article result) {
                // Verify if an article is returned
                if (result == null){
                    finishActivityNotEnoughResources();
                    return;
                }
                String answerString = (String) answer;

                String feedback;
                if (answerString.equals(result.getArticleName())){
                    feedback = getString(R.string.correct_answer_message) +result.getArticleName()+" "+word.getWordName();
                    feedbackCardView.setCardBackgroundColor(getResources().getColor(R.color.correct_answer_color));
                }else{
                    feedback = getString(R.string.wrong_answer_message) +result.getArticleName()+" "+word.getWordName();
                    feedbackCardView.setCardBackgroundColor(getResources().getColor(R.color.wrong_answer_color));
                }
                feedbackTextView.setText(feedback);
                // If pressed, the user can play again
                newWordButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startGame();
                    }
                });
            }
        }).execute();
    }
}
