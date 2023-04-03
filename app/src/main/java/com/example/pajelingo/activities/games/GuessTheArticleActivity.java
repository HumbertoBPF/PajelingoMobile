package com.example.pajelingo.activities.games;

import static com.example.pajelingo.utils.Tools.getAuthToken;
import static com.example.pajelingo.utils.Tools.getRandomItemFromList;
import static com.example.pajelingo.utils.Tools.handleGameAnswerFeedback;
import static com.example.pajelingo.utils.Tools.isUserAuthenticated;

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
import com.example.pajelingo.models.ArticleGameAnswer;
import com.example.pajelingo.models.GameAnswerFeedback;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.ui.LabeledSpinner;

import retrofit2.Call;

public class GuessTheArticleActivity extends GameActivity {

    private Language language;
    private Word word;

    @Override
    protected void setup() {
        setContentView(R.layout.activity_single_language_choice);

        LabeledSpinner languageInput = findViewById(R.id.language_input);
        Spinner languageSpinner = languageInput.getSpinner();
        Button playButton = findViewById(R.id.play_button);
        // Fill the spinner with all languages available
        languageDao.getAllRecordsTask(result -> {
            // Verify if there are at least one language
            if (result.isEmpty()){
                finishActivityNotEnoughResources();
                return;
            }
            // Remove English from the list of results
            int indexEnglish = -1;
            for (int i = 0;i< result.size();i++){
                if (result.get(i).getLanguageName().equals("English")){
                    indexEnglish = i;
                    break;
                }
            }
            if (indexEnglish != -1){
                result.remove(indexEnglish);
            }
            // Fill the adapter with the name of all the languages available
            ArrayAdapter<Language> adapter = new ArrayAdapter<>(GuessTheArticleActivity.this,
                    android.R.layout.simple_spinner_item, result);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the baseLanguageSpinner
            languageSpinner.setAdapter(adapter);
            playButton.setOnClickListener(v -> {
                String languageChosenName = languageSpinner.getSelectedItem().toString();
                playButton.setOnClickListener(null);
                languageDao.getLanguageByNameAsyncTask(languageChosenName, result1 -> {
                    language = result1;
                    startGame();
                }).execute();
            });
        }).execute();
    }

    @Override
    protected void startGame() {
        setContentView(R.layout.activity_guess_the_article);

        EditText answerInputEditText = findViewById(R.id.answer_input);
        TextView wordTextView = findViewById(R.id.word_text_view);
        Button checkButton = findViewById(R.id.check_button);

        WordDao wordDao = AppDatabase.getInstance(this).getWordDao();
        wordDao.getNounsByLanguageAsyncTask(language.getLanguageName(), result -> {
            // Verify if there are at least one word
            if (result.isEmpty()){
                finishActivityNotEnoughResources();
                return;
            }
            word = getRandomItemFromList(result);
            wordTextView.setText(word.getWordName());

            checkButton.setOnClickListener(v -> {
                checkButton.setOnClickListener(null);
                // Trim the answer since the user may accidentally insert a space before of after the article
                String userAnswer = answerInputEditText.getText().toString().trim();
                verifyAnswer(userAnswer);
            });
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
        articleDao.getRecordByIdTask(word.getIdArticle(), result -> {
            // Verify if an article is returned
            if (result == null){
                finishActivityNotEnoughResources();
                return;
            }
            String answerString = (String) answer;

            String feedback;

            if (answerString.equals(result.getArticleName())){

                if (isUserAuthenticated(GuessTheArticleActivity.this)){
                    ArticleGameAnswer articleGameAnswer = new ArticleGameAnswer(word.getId(), answerString);
                    Call<GameAnswerFeedback> call =
                            this.languageSchoolAPI.submitArticleGameAnswer(getAuthToken(GuessTheArticleActivity.this), articleGameAnswer);
                    handleGameAnswerFeedback(GuessTheArticleActivity.this, call);
                }

                feedback = getString(R.string.correct_answer_message) +result.getArticleName()+" "+word.getWordName();
                feedbackCardView.setCardBackgroundColor(getResources().getColor(R.color.success));
            }else{
                feedback = getString(R.string.wrong_answer_message) +result.getArticleName()+" "+word.getWordName();
                feedbackCardView.setCardBackgroundColor(getResources().getColor(R.color.danger));
            }
            feedbackTextView.setText(feedback);
            // If pressed, the user can play again
            newWordButton.setOnClickListener(v -> startGame());
        }).execute();
    }
}
