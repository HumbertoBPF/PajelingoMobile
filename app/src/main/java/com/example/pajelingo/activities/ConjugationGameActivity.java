package com.example.pajelingo.activities;

import static com.example.pajelingo.util.Tools.getRandomItemFromList;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.CategoryDao;
import com.example.pajelingo.daos.ConjugationDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Category;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;

import java.util.ArrayList;
import java.util.List;

public class ConjugationGameActivity extends GameActivity {

    private Language language;
    private Conjugation conjugation;

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
                    languageNames.add(language.getLanguageName());
                }
                // TODO verify if there are at least one languages
                // Fill the adapter with the name of all the languages available
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ConjugationGameActivity.this,
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
        setContentView(R.layout.activity_conjugation_game);

        TextView verb = findViewById(R.id.verb);
        TextView pronoun1 = findViewById(R.id.pronoun_1);
        TextView pronoun2 = findViewById(R.id.pronoun_2);
        TextView pronoun3 = findViewById(R.id.pronoun_3);
        TextView pronoun4 = findViewById(R.id.pronoun_4);
        TextView pronoun5 = findViewById(R.id.pronoun_5);
        TextView pronoun6 = findViewById(R.id.pronoun_6);
        EditText conjugation1 = findViewById(R.id.conjugation_1);
        EditText conjugation2 = findViewById(R.id.conjugation_2);
        EditText conjugation3 = findViewById(R.id.conjugation_3);
        EditText conjugation4 = findViewById(R.id.conjugation_4);
        EditText conjugation5 = findViewById(R.id.conjugation_5);
        EditText conjugation6 = findViewById(R.id.conjugation_6);
        Button checkButton = findViewById(R.id.check_button);

        pronoun1.setText(language.getPersonalPronoun1());
        pronoun2.setText(language.getPersonalPronoun2());
        pronoun3.setText(language.getPersonalPronoun3());
        pronoun4.setText(language.getPersonalPronoun4());
        pronoun5.setText(language.getPersonalPronoun5());
        pronoun6.setText(language.getPersonalPronoun6());

        CategoryDao categoryDao = AppDatabase.getInstance(this).getCategoryDao();
        // We want to get the object corresponding to the category "verb"
        categoryDao.getCategoryByNameTask("verbs", new OnResultListener<Category>() {
            @Override
            public void onResult(Category result) {
                // TODO verify if one category is returned
                WordDao wordDao = AppDatabase.getInstance(ConjugationGameActivity.this).getWordDao();
                wordDao.getWordsByCategoryAndByLanguageTask(result.getId(), language.getId(), new OnResultListener<List<Word>>() {
                    @Override
                    public void onResult(List<Word> result) {
                        // TODO verify if at least one word is returned
                        // Pick a word that is in the category "verb" and whose language corresponds to the selected language
                        Word word = getRandomItemFromList(result);
                        ConjugationDao conjugationDao = AppDatabase.getInstance(ConjugationGameActivity.this).getConjugationDao();
                        conjugationDao.getConjugationsFromVerbTask(word.getId(), new OnResultListener<List<Conjugation>>() {
                            @Override
                            public void onResult(List<Conjugation> result) {
                                // TODO verify if there are at least one conjugation is returned
                                // Pick a random conjugation of the chosen verb
                                conjugation = getRandomItemFromList(result);
                                verb.setText(word.getWordName() + " - " + conjugation.getTense());
                                checkButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        checkButton.setOnClickListener(null);
                                        List<String> answers = new ArrayList<>();
                                        // Trim the answers since the user may accidentally insert a space before of after them
                                        answers.add(conjugation1.getText().toString().trim());
                                        answers.add(conjugation2.getText().toString().trim());
                                        answers.add(conjugation3.getText().toString().trim());
                                        answers.add(conjugation4.getText().toString().trim());
                                        answers.add(conjugation5.getText().toString().trim());
                                        answers.add(conjugation6.getText().toString().trim());
                                        verifyAnswer(answers);
                                    }
                                });
                            }
                        }).execute();
                    }
                }).execute();
            }
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
            feedback = "Correct :)\n";
            feedbackCardView.setCardBackgroundColor(getResources().getColor(R.color.correct_answer_color));
        }else{
            feedback = "Wrong answer\n";
            feedbackCardView.setCardBackgroundColor(getResources().getColor(R.color.wrong_answer_color));
        }

        feedback += language.getPersonalPronoun1() + " " + conjugation.getConjugation1() + "\n" +
                language.getPersonalPronoun2() + " " + conjugation.getConjugation2() + "\n" +
                language.getPersonalPronoun3() + " " + conjugation.getConjugation3() + "\n" +
                language.getPersonalPronoun4() + " " + conjugation.getConjugation4() + "\n" +
                language.getPersonalPronoun5() + " " + conjugation.getConjugation5() + "\n" +
                language.getPersonalPronoun6() + " " + conjugation.getConjugation6();
        feedbackTextView.setText(feedback);
        // If pressed, the user can play again
        newWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
    }
}