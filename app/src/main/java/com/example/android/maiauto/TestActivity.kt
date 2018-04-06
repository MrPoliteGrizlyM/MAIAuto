package com.example.android.maiauto


import android.annotation.SuppressLint
import android.database.Cursor
import android.database.SQLException
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast

import java.io.IOException
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.content.res.ColorStateList
import android.graphics.Color
import java.util.*





class TestActivity : AppCompatActivity() {

    internal var c: Cursor? = null

    private var textViewQuestion: TextView? = null
    private var textViewScore: TextView? = null
    private var textViewQuestionCount: TextView? = null
    private var textViewCountDown: TextView? = null
    private var rbGroup: RadioGroup? = null
    private var rb1: RadioButton? = null
    private var rb2: RadioButton? = null
    private var rb3: RadioButton? = null
    private var buttonConfirmNext: Button? = null

    private var textColorDefaultRb: ColorStateList? = null

    private var questionList: List<Question>? = null
    private var questionCounter: Int = 0
    private var questionCountTotal: Int = 0
    private var currentQuestion: Question? = null

    private var score: Int = 0
    private var answered: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewCountDown = findViewById(R.id.text_view_countdown);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);

        textColorDefaultRb = rb1!!.getTextColors();

        val myDbHelper = QuizDbHelper(this@TestActivity)

        try {
                myDbHelper.createDataBase()
            } catch (ioe: IOException) {
                throw Error("Unable to create database")
            }

            try {
                myDbHelper.openDataBase()
            } catch (sqle: SQLException) {
                throw sqle
            }


        questionList = myDbHelper.getAllQuestions();



        questionCountTotal = questionList!!.size
        Collections.shuffle(questionList)

        showNextQuestion()

        buttonConfirmNext!!.setOnClickListener(View.OnClickListener {
            if (!answered) {
                if (rb1!!.isChecked || rb2!!.isChecked || rb3!!.isChecked) {
                    checkAnswer()
                } else {
                    Toast.makeText(this@TestActivity, "Please select an answer", Toast.LENGTH_SHORT).show()
                }
            } else {
                showNextQuestion()
            }
        })



//        (findViewById<Button>(R.id.button_confirm_next)).setOnClickListener {
//            val myDbHelper = QuizDbHelper(this@TestActivity)
//            try {
//                myDbHelper.createDataBase()
//            } catch (ioe: IOException) {
//                throw Error("Unable to create database")
//            }
//
//            try {
//                myDbHelper.openDataBase()
//            } catch (sqle: SQLException) {
//                throw sqle
//            }
//
//            //Toast.makeText(this@TestActivity, "Successfully Imported", Toast.LENGTH_SHORT).show()
//            c = myDbHelper.query("example", null, null, null, null, null, null)
//            if (c!!.moveToPosition(3)) {
//                    Toast.makeText(this@TestActivity,
//                            "question: " + c!!.getString(1) + "\n",
//                            Toast.LENGTH_LONG).show()
//
//            }
//        }

    }

    private fun showNextQuestion() {

        rb1!!.setTextColor(textColorDefaultRb);
        rb2!!.setTextColor(textColorDefaultRb)
        rb3?.setTextColor(textColorDefaultRb)
        rbGroup?.clearCheck()

        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList?.get(questionCounter)

            textViewQuestion!!.setText(currentQuestion!!.getQuestion())
            rb1!!.setText(currentQuestion!!.getOption1())
            rb2!!.setText(currentQuestion!!.getOption2())
            rb3!!.setText(currentQuestion!!.getOption3())

            questionCounter++
            textViewQuestionCount!!.setText("Question: $questionCounter/$questionCountTotal")
            answered = false
            buttonConfirmNext!!.setText("Confirm")
        } else {
            Toast.makeText(this,"Congratulations!",Toast.LENGTH_SHORT).show()
            finishQuiz()
        }
    }

    @SuppressLint("ShowToast")
    private fun checkAnswer() {

        answered = true

        val rbSelected = findViewById<RadioButton>(rbGroup!!.checkedRadioButtonId)
        val answerNr = rbGroup!!.indexOfChild(rbSelected) + 1

        if (answerNr != currentQuestion!!.getAnswerNr()) {
            if (score == 2){
                Toast.makeText(this, "You're so fucking stupid!", Toast.LENGTH_SHORT).show()
                finishQuiz()
            } else {
                score++
                textViewScore!!.setText("Score: $score")
            }
        }



        showSolution()
    }

    private fun showSolution() {
        rb1!!.setTextColor(Color.RED)
        rb2!!.setTextColor(Color.RED)
        rb3!!.setTextColor(Color.RED)

        when (currentQuestion!!.getAnswerNr()) {
            1 -> {
                rb1!!.setTextColor(Color.GREEN)
                textViewQuestion!!.setText("Answer 1 is correct")
            }
            2 -> {
                rb2!!.setTextColor(Color.GREEN)
                textViewQuestion!!.setText("Answer 2 is correct")
            }
            3 -> {
                rb3!!.setTextColor(Color.GREEN)
                textViewQuestion!!.setText("Answer 3 is correct")
            }
        }

        if (questionCounter < questionCountTotal) {
            buttonConfirmNext!!.setText("Next")
        } else {
            buttonConfirmNext!!.setText("Finish")
        }
    }

    private fun finishQuiz() {
        finish()
    }




}
