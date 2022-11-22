package com.example.pajelingo.activities.games;

import static com.example.pajelingo.utils.Tools.getRandomItemFromList;
import static com.example.pajelingo.utils.Tools.isUserAuthenticated;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.ConjugationDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.synchronization.ScoreUploader;
import com.example.pajelingo.ui.LabeledEditText;
import com.example.pajelingo.ui.LabeledSpinner;

import java.util.ArrayList;
import java.util.List;

public class ConjugationGameActivity extends GameActivity {

    private Language language;
    private Conjugation conjugation;

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
            // Fill the adapter with the name of all the languages available
            ArrayAdapter<Language> adapter = new ArrayAdapter<>(ConjugationGameActivity.this,
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
        setContentView(R.layout.activity_conjugation_game);

        TextView verb = findViewById(R.id.verb_and_tense_text_view);

        LabeledEditText conjugation1 = findViewById(R.id.conjugation_1);
        LabeledEditText conjugation2 = findViewById(R.id.conjugation_2);
        LabeledEditText conjugation3 = findViewById(R.id.conjugation_3);
        LabeledEditText conjugation4 = findViewById(R.id.conjugation_4);
        LabeledEditText conjugation5 = findViewById(R.id.conjugation_5);
        LabeledEditText conjugation6 = findViewById(R.id.conjugation_6);

        Button checkButton = findViewById(R.id.check_button);

        conjugation1.setLabel(language.getPersonalPronoun1());
        conjugation2.setLabel(language.getPersonalPronoun2());
        conjugation3.setLabel(language.getPersonalPronoun3());
        conjugation4.setLabel(language.getPersonalPronoun4());
        conjugation5.setLabel(language.getPersonalPronoun5());
        conjugation6.setLabel(language.getPersonalPronoun6());

        // We want to get only the words that are verbs
        WordDao wordDao = AppDatabase.getInstance(ConjugationGameActivity.this).getWordDao();
        wordDao.getWordsByCategoryAndByLanguageTask("verbs", language.getLanguageName(), result -> {
            // Verify if at least one word is returned
            if (result.isEmpty()){
                finishActivityNotEnoughResources();
                return;
            }
            // Pick a word that is in the category "verb" and whose language corresponds to the selected language
            Word word = getRandomItemFromList(result);
            ConjugationDao conjugationDao = AppDatabase.getInstance(ConjugationGameActivity.this).getConjugationDao();
            conjugationDao.getConjugationsFromVerbTask(word.getId(), result1 -> {
                // Verify if there are at least one conjugation is returned
                if (result1.isEmpty()){
                    finishActivityNotEnoughResources();
                    return;
                }
                // Pick a random conjugation of the chosen verb
                conjugation = getRandomItemFromList(result1);
                verb.setText(word.getWordName() + " - " + conjugation.getTense());
                checkButton.setOnClickListener(v -> {
                    checkButton.setOnClickListener(null);
                    List<String> answers = new ArrayList<>();
                    // Trim the answers since the user may accidentally insert a space before of after them
                    answers.add(conjugation1.getInput().toString().trim());
                    answers.add(conjugation2.getInput().toString().trim());
                    answers.add(conjugation3.getInput().toString().trim());
                    answers.add(conjugation4.getInput().toString().trim());
                    answers.add(conjugation5.getInput().toString().trim());
                    answers.add(conjugation6.getInput().toString().trim());
                    verifyAnswer(answers);
                });
            }).execute();
        }).execute();

    }

    @Override
    protected void verifyAnswer(Object answer) {
        setContentView(R.layout.activity_feedback);

        TextView feedbackTextView = findViewById(R.id.feedback_text_view);
        CardView feedbackCardView = findViewById(R.id.feedback_rounded_background);
        Button newWordButton = findViewById(R.id.new_word_button);

        List<String> answers = (List<String>) answer;

        String answerConjugation1 = answers.get(0);
        String answerConjugation2 = answers.get(1);
        String answerConjugation3 = answers.get(2);
        String answerConjugation4 = answers.get(3);
        String answerConjugation5 = answers.get(4);
        String answerConjugation6 = answers.get(5);

        String feedback;
        // Check if all the conjugations correspond to the user inputs
        if (conjugation.getConjugation1().equals(answerConjugation1) && conjugation.getConjugation2().equals(answerConjugation2) &&
        conjugation.getConjugation3().equals(answerConjugation3) && conjugation.getConjugation4().equals(answerConjugation4) &&
        conjugation.getConjugation5().equals(answerConjugation5) && conjugation.getConjugation6().equals(answerConjugation6)){

            if (isUserAuthenticated(this)){
                ScoreUploader uploader = new ScoreUploader(this, language, game.getId());
                uploader.upload();
            }

            feedback = getString(R.string.correct_answer_message);
            feedbackCardView.setCardBackgroundColor(getResources().getColor(R.color.success));
        }else{
            feedback = getString(R.string.wrong_answer_message);
            feedbackCardView.setCardBackgroundColor(getResources().getColor(R.color.danger));
        }

        feedback += language.getPersonalPronoun1() + " " + conjugation.getConjugation1() + "\n" +
                language.getPersonalPronoun2() + " " + conjugation.getConjugation2() + "\n" +
                language.getPersonalPronoun3() + " " + conjugation.getConjugation3() + "\n" +
                language.getPersonalPronoun4() + " " + conjugation.getConjugation4() + "\n" +
                language.getPersonalPronoun5() + " " + conjugation.getConjugation5() + "\n" +
                language.getPersonalPronoun6() + " " + conjugation.getConjugation6();
        feedbackTextView.setText(feedback);
        // If pressed, the user can play again
        newWordButton.setOnClickListener(v -> startGame());
    }
}