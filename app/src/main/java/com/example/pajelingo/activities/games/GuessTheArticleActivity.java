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

import androidx.cardview.widget.CardView;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.ArticleDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.ArticleGameAnswer;
import com.example.pajelingo.models.GameAnswerFeedback;
import com.example.pajelingo.models.GameRoundWord;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit_calls.GameRoundCall;
import com.example.pajelingo.ui.LabeledSpinner;
import com.example.pajelingo.ui.LoadingSpinner;

import retrofit2.Call;
import retrofit2.Response;

public class GuessTheArticleActivity extends GameActivity {

    private Language language;
    private Word word;

    @Override
    protected void setup() {
        setContentView(R.layout.activity_single_language_choice);

        TextView instructionsTextView = findViewById(R.id.instructions_text_view);
        LabeledSpinner languageInput = findViewById(R.id.language_input);
        Spinner languageSpinner = languageInput.getSpinner();
        Button playButton = findViewById(R.id.play_button);

        instructionsTextView.setText(game.getInstructions());
        // Fill the spinner with all languages available
        languageDao.getAllRecords(result -> {
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
                languageDao.getLanguageByName(languageChosenName, result1 -> {
                    language = result1;
                    startGame();
                });
            });
        });
    }

    @Override
    protected void startGame() {
        setContentView(R.layout.activity_guess_the_article);

        setLoadingLayout();

        if (isUserAuthenticated(this)) {
            getWordFromAPI();
        }else {
            getWordFromLocalDatabase();
        }
    }

    private void getWordFromAPI() {
        Call<GameRoundWord> call =
                this.languageSchoolAPI.getWordForArticleGame(getAuthToken(this), language.getLanguageName());

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

    protected void getWordFromLocalDatabase() {
        wordDao.getNounsByLanguage(language.getLanguageName(), words -> {
            // Verify if there are at least one word
            if (words.isEmpty()){
                finishActivityNotEnoughResources();
                return;
            }

            fillGameLayout(getRandomItemFromList(words));
        });
    }

    private void setLoadingLayout() {
        TextView wordTextView = findViewById(R.id.word_text_view);
        EditText answerInputEditText = findViewById(R.id.answer_input);
        Button checkButton = findViewById(R.id.check_button);
        LoadingSpinner loadingSpinner = findViewById(R.id.loading_spinner);

        wordTextView.setVisibility(View.GONE);
        answerInputEditText.setVisibility(View.GONE);
        checkButton.setVisibility(View.GONE);
        loadingSpinner.setVisibility(View.VISIBLE);
    }

    private void fillGameLayout(Word word) {
        TextView wordTextView = findViewById(R.id.word_text_view);
        EditText answerInputEditText = findViewById(R.id.answer_input);
        Button checkButton = findViewById(R.id.check_button);
        LoadingSpinner loadingSpinner = findViewById(R.id.loading_spinner);

        this.word = word;
        wordTextView.setText(word.getWordName());

        checkButton.setOnClickListener(v -> {
            checkButton.setOnClickListener(null);
            // Trim the answer since the user may accidentally insert a space before of after the article
            String userAnswer = answerInputEditText.getText().toString().trim();
            verifyAnswer(userAnswer);
        });

        wordTextView.setVisibility(View.VISIBLE);
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

        ArticleDao articleDao = AppDatabase.getInstance(this).getArticleDao();
        // Gets the article related to the word and verifies user's answer
        articleDao.getRecordById(word.getIdArticle(), result -> {
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
                    gameScoreCall.execute(call);
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
        });
    }
}
