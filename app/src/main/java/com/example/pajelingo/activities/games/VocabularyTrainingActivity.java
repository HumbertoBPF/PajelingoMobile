package com.example.pajelingo.activities.games;

import static com.example.pajelingo.utils.SharedPreferences.getAuthToken;
import static com.example.pajelingo.utils.SharedPreferences.isUserAuthenticated;
import static com.example.pajelingo.utils.Tools.getRandomItemFromList;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.pajelingo.R;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.GameAnswerFeedback;
import com.example.pajelingo.models.GameRoundWord;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.VocabularyGameAnswer;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit_calls.GameRoundCall;
import com.example.pajelingo.ui.LabeledSpinner;
import com.example.pajelingo.ui.LoadingSpinner;

import retrofit2.Call;
import retrofit2.Response;

public class VocabularyTrainingActivity extends GameActivity {

    private Language baseLanguage;
    private Language targetLanguage;
    private Word wordToTranslate;

    @Override
    protected void setup() {
        setContentView(R.layout.activity_dual_language_choice);

        TextView instructionsTextView = findViewById(R.id.instructions_text_view);
        LabeledSpinner baseLanguageInput = findViewById(R.id.base_language_input);
        LabeledSpinner targetLanguageInput = findViewById(R.id.target_language_input);
        Spinner baseLanguageSpinner = baseLanguageInput.getSpinner();
        Spinner targetLanguageSpinner = targetLanguageInput.getSpinner();
        Button playButton = findViewById(R.id.play_button);

        instructionsTextView.setText(game.getInstructions());
        // Create an ArrayAdapter using the string array and a default baseLanguageSpinner layout
        languageDao.getAllRecords(result -> {
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
            playButton.setOnClickListener(v -> {
                String baseLanguageName = baseLanguageSpinner.getSelectedItem().toString();
                String targetLanguageName = targetLanguageSpinner.getSelectedItem().toString();
                // The base and target languages must be different
                if (baseLanguageName.equals(targetLanguageName)){
                    Toast.makeText(VocabularyTrainingActivity.this, R.string.warning_base_target_len_eq,
                            Toast.LENGTH_LONG).show();
                }else{
                    playButton.setOnClickListener(null);
                    // Set base and target languages attributes according to the user's choices and start game
                    languageDao.getLanguageByName(baseLanguageName, result1 -> {
                        baseLanguage = result1;
                        languageDao.getLanguageByName(targetLanguageName, result2 -> {
                            targetLanguage = result2;
                            startGame();
                        });
                    });
                }
            });
        });
    }

    @Override
    protected void startGame() {
        setContentView(R.layout.activity_vocabulary_training);

        setLoadingLayout();

        if (isUserAuthenticated(this)) {
            getWordFromAPI();
        }else{
            getWordFromLocalDatabase();
        }
    }

    private void getWordFromAPI() {
        Call<GameRoundWord> call =
                languageSchoolAPI.getWordForVocabularyGame(getAuthToken(this),
                        baseLanguage.getLanguageName(), targetLanguage.getLanguageName());

        GameRoundCall gameRoundCall = new GameRoundCall();

        gameRoundCall.execute(call, new HttpResponseInterface<GameRoundWord>() {
            @Override
            public void onSuccess(GameRoundWord gameRoundWord) {
                wordDao.getRecordById(gameRoundWord.getId(), word -> {
                    if (word == null) {
                        getWordFromLocalDatabase();
                        return;
                    }

                    fillGameLayout(word);
                });
            }

            @Override
            public void onError(Response<GameRoundWord> response) {
                getWordFromLocalDatabase();
            }

            @Override
            public void onFailure() {
                getWordFromLocalDatabase();
            }
        });
    }

    private void getWordFromLocalDatabase() {
        wordDao.getWordsByLanguage(targetLanguage.getLanguageName(), words -> {
            // Verify if there is at least one element in the list
            if (words.isEmpty()){
                finishActivityNotEnoughResources();
                return;
            }

            fillGameLayout(getRandomItemFromList(words));
        });
    }

    private void setLoadingLayout() {
        TextView wordToTranslateTextView = findViewById(R.id.word_text_view);
        EditText answerInputEditText = findViewById(R.id.answer_input);
        Button checkButton = findViewById(R.id.check_button);
        LoadingSpinner loadingSpinner = findViewById(R.id.loading_spinner);

        wordToTranslateTextView.setVisibility(View.GONE);
        answerInputEditText.setVisibility(View.GONE);
        checkButton.setVisibility(View.GONE);
        loadingSpinner.setVisibility(View.VISIBLE);
    }

    private void fillGameLayout(Word word) {
        TextView wordToTranslateTextView = findViewById(R.id.word_text_view);
        EditText answerInputEditText = findViewById(R.id.answer_input);
        Button checkButton = findViewById(R.id.check_button);
        LoadingSpinner loadingSpinner = findViewById(R.id.loading_spinner);

        wordToTranslate = word;

        wordToTranslateTextView.setText(wordToTranslate.getWordName());
        answerInputEditText.setHint(getString(R.string.instruction_vocabulary_game)+baseLanguage.getLanguageName());

        checkButton.setOnClickListener(v -> {
            checkButton.setOnClickListener(null);
            // Trim the answer since the user may accidentally insert a space before of after the word
            String userAnswer = answerInputEditText.getText().toString().trim();
            verifyAnswer(userAnswer);
        });

        wordToTranslateTextView.setVisibility(View.VISIBLE);
        answerInputEditText.setVisibility(View.VISIBLE);
        checkButton.setVisibility(View.VISIBLE);
        loadingSpinner.setVisibility(View.GONE);
    }

    @Override
    protected void verifyAnswer(Object answer) {
        setContentView(R.layout.activity_feedback);

        TextView feedbackTextView = findViewById(R.id.feedback_text_view);
        CardView feedbackCardView = findViewById(R.id.feedback_rounded_background);
        Button newWordButton = findViewById(R.id.new_word_button);

        wordDao.getSynonyms(wordToTranslate, baseLanguage, synonyms -> {
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
                    VocabularyGameAnswer vocabularyGameAnswer = new VocabularyGameAnswer(wordToTranslate.getId(),
                            baseLanguage.getLanguageName(), userTranslation);
                    Call<GameAnswerFeedback> call =
                            languageSchoolAPI.submitVocabularyGameAnswer(getAuthToken(VocabularyTrainingActivity.this), vocabularyGameAnswer);
                    gameScoreCall.execute(call);
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
            newWordButton.setOnClickListener(v -> startGame());
        });
    }
}