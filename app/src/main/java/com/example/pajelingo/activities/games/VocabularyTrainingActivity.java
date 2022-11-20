package com.example.pajelingo.activities.games;

import static com.example.pajelingo.utils.Tools.getRandomItemFromList;
import static com.example.pajelingo.utils.Tools.isUserAuthenticated;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.pajelingo.R;
import com.example.pajelingo.async_tasks.SynonymsByLanguageTask;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.synchronization.ScoreUploader;

import java.util.List;

public class VocabularyTrainingActivity extends GameActivity {

    private Language baseLanguage;
    private Language targetLanguage;
    private Word wordToTranslate;

    @Override
    protected void setup() {
        setContentView(R.layout.activity_dual_language_choice);

        Spinner baseLanguageSpinner = findViewById(R.id.base_language_spinner);
        Spinner targetLanguageSpinner = findViewById(R.id.target_language_spinner);
        Button playButton = findViewById(R.id.play_button);
        // Create an ArrayAdapter using the string array and a default baseLanguageSpinner layout
        languageDao.getAllRecordsTask(new OnResultListener<List<Language>>() {
            @Override
            public void onResult(List<Language> result) {
                // Verifying if there are at least two languages
                if (result.size() < 2){
                    finishActivityNotEnoughResources();
                    return;
                }
                // Fill the adapter with the name of all the languages available
                ArrayAdapter<Language> adapter = new ArrayAdapter<>(VocabularyTrainingActivity.this,
                        android.R.layout.simple_spinner_item, result);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the baseLanguageSpinner
                baseLanguageSpinner.setAdapter(adapter);
                targetLanguageSpinner.setAdapter(adapter);
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String baseLanguageName = baseLanguageSpinner.getSelectedItem().toString();
                        String targetLanguageName = targetLanguageSpinner.getSelectedItem().toString();
                        // The base and target languages must be different
                        if (baseLanguageName.equals(targetLanguageName)){
                            Toast.makeText(VocabularyTrainingActivity.this, R.string.warning_base_target_len_eq,
                                    Toast.LENGTH_LONG).show();
                        }else{
                            playButton.setOnClickListener(null);
                            // Set base and target languages attributes according to the user's choices and start game
                            languageDao.getLanguageByNameAsyncTask(baseLanguageName, new OnResultListener<Language>() {
                                @Override
                                public void onResult(Language result) {
                                    baseLanguage = result;
                                    languageDao.getLanguageByNameAsyncTask(targetLanguageName, new OnResultListener<Language>() {
                                        @Override
                                        public void onResult(Language result) {
                                            targetLanguage = result;
                                            startGame();
                                        }
                                    }).execute();
                                }
                            }).execute();
                        }
                    }
                });
            }
        }).execute();
    }

    @Override
    protected void startGame() {
        setContentView(R.layout.activity_vocabulary_training);

        TextView wordToTranslateTextView = findViewById(R.id.word_text_view);
        EditText answerInputEditText = findViewById(R.id.answer_input);
        Button checkButton = findViewById(R.id.check_button);

        answerInputEditText.setHint(getString(R.string.instruction_vocabulary_game)+baseLanguage.getLanguageName());

        WordDao wordDao = AppDatabase.getInstance(this).getWordDao();
        wordDao.getWordsByLanguageAsyncTask(targetLanguage.getLanguageName(), new OnResultListener<List<Word>>() {
            @Override
            public void onResult(List<Word> result) {
                // Verify if there is at least one element in the list
                if (result.isEmpty()){
                    finishActivityNotEnoughResources();
                    return;
                }
                wordToTranslate = getRandomItemFromList(result);
                wordToTranslateTextView.setText(wordToTranslate.getWordName());
                checkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkButton.setOnClickListener(null);
                        // Trim the answer since the user may accidentally insert a space before of after the word
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

        new SynonymsByLanguageTask(this, wordToTranslate, baseLanguage, new OnResultListener<List<String>>() {
            @Override
            public void onResult(List<String> synonyms) {
                String userTranslation = (String) answer;
                int numberOfSynonyms = synonyms.size();
                boolean isAnswerCorrect = false;
                String feedback;
                String correctAnswer = wordToTranslate.getWordName()+": ";

                for (int i = 0;i < numberOfSynonyms;i++){
                    String synonym = synonyms.get(i);

                    correctAnswer += synonym;

                    if (i != numberOfSynonyms - 1){
                        correctAnswer += ", ";
                    }

                    if (synonym.equals(userTranslation)){
                        isAnswerCorrect = true;
                    }
                }

                if (isAnswerCorrect){

                    if (isUserAuthenticated(VocabularyTrainingActivity.this)){
                        ScoreUploader uploader = new ScoreUploader(VocabularyTrainingActivity.this,
                                targetLanguage, game.getId());
                        uploader.upload();
                    }

                    feedback = getString(R.string.correct_answer_message);
                    feedbackCardView.setCardBackgroundColor(getResources().getColor(R.color.success));
                }else{
                    feedback = getString(R.string.wrong_answer_message);
                    feedbackCardView.setCardBackgroundColor(getResources().getColor(R.color.danger));
                }
                feedback += correctAnswer;
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