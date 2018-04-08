package com.example.android.maiauto


import android.annotation.SuppressLint
import android.app.Activity
import android.database.Cursor
import android.database.SQLException
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View

import java.io.IOException
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import java.util.*

import android.content.Intent
import kotlin.concurrent.timerTask
import android.os.CountDownTimer
import android.R.string.cancel
import android.app.ActionBar
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.MenuInflater
import android.widget.*


class TestActivity : AppCompatActivity() {

    internal var c: Cursor? = null
    private val COUNTDOWN_IN_MILLIS: Long = 120000


    private var textViewQuestion: TextView? = null
    private var textViewScore: TextView? = null

    private var textViewCountDown: TextView? = null
    private var rbGroup: RadioGroup? = null
    private var rb1: RadioButton? = null
    private var rb2: RadioButton? = null
    private var rb3: RadioButton? = null
    private var rb4: RadioButton? = null
    private var rb5: RadioButton? = null
    private var buttonConfirmNext: Button? = null
    private var textQuestionBar: TextView? = null
    private var textTimeBar: TextView? = null
    private var imageQuestion: ImageView? = null


    private var textColorDefaultRb: ColorStateList? = null
    private var textColorDefaultCd: ColorStateList? = null

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0

    private var questionList: List<Question>? = null
    private var questionCounter: Int = 0
    private var questionCountTotal: Int = 0
    private var currentQuestion: Question? = null
    private var currentImage : String? = null

    private var score: Int = 0
    private var answered: Boolean = false





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM;
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setCustomView(R.layout.abs_layout)

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textQuestionBar = findViewById(R.id.question_text)
        textTimeBar = findViewById(R.id.countdown_text)
        imageQuestion= findViewById(R.id.image_question)



        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);
        rb5 = findViewById(R.id.radio_button5);

        buttonConfirmNext = findViewById(R.id.button_confirm_next);



        textColorDefaultRb = rb1!!.getTextColors();
        textColorDefaultCd = textTimeBar!!.getTextColors();


        timeLeftInMillis = COUNTDOWN_IN_MILLIS
        startCountDown()


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



        // Log.d("mes", questionList!!.get(0).option1.toString())


        questionCountTotal = questionList!!.size
        Collections.shuffle(questionList)


        showNextQuestion()

        buttonConfirmNext!!.setOnClickListener(View.OnClickListener {
            if (!answered) {
                if (rb1!!.isChecked || rb2!!.isChecked || rb3!!.isChecked || rb4!!.isChecked || rb5!!.isChecked) {
                    checkAnswer()
                } else {
                    Toast.makeText(this@TestActivity, "Выберите ответ", Toast.LENGTH_SHORT).show()
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
        rb4?.setTextColor(textColorDefaultRb)
        rb5?.setTextColor(textColorDefaultRb)
        rbGroup?.clearCheck()


        if (questionCounter < questionCountTotal  &&  buttonConfirmNext!!.text != "Завершить") {
            currentQuestion = questionList?.get(questionCounter)

            rb3!!.visibility = View.VISIBLE
            rb4!!.visibility = View.VISIBLE
            rb5!!.visibility = View.VISIBLE


            if (currentQuestion!!.option3 == null){
                rb3!!.visibility = View.INVISIBLE
                rb4!!.visibility = View.INVISIBLE
                rb5!!.visibility = View.INVISIBLE
            }
            else if (currentQuestion!!.option4 == null){
                rb4!!.visibility = View.INVISIBLE
                rb5!!.visibility = View.INVISIBLE
            }
            else if (currentQuestion!!.option5 == null){
                rb5!!.visibility = View.INVISIBLE
            }

            textViewQuestion!!.setText(currentQuestion!!.getQuestion())
            rb1!!.setText(currentQuestion!!.getOption1())
            rb2!!.setText(currentQuestion!!.getOption2())
            rb3!!.setText(currentQuestion!!.getOption3())
            rb4!!.setText(currentQuestion!!.getOption4())
            rb5!!.setText(currentQuestion!!.getOption5())


            currentImage = "image" + currentQuestion!!.image
            imageQuestion!!.setImageResource(getResourceId(currentImage!!, "drawable", getPackageName()))
            //imageQuestion!!.setImageURI(Uri.parse("drawable://$currentImage.jpg"))
            //Log.d("image", currentQuestion!!.image)


            questionCounter++

            textQuestionBar!!.setText("Вопрос: $questionCounter/$questionCountTotal")


            answered = false
            buttonConfirmNext!!.setText("Подтвердить")



        } else {
            finishQuiz()
        }
    }

    private fun startCountDown() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateCountDownText()
                checkAnswer()
                //Toast.makeText(this@TestActivity, "Время вышло", Toast.LENGTH_SHORT).show()
                textViewScore!!.setText("Ошибок: $score")
                buttonConfirmNext!!.setText("Завершить")

            }
        }.start()
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000).toInt() / 60
        val seconds = (timeLeftInMillis / 1000).toInt() % 60

        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        textTimeBar!!.setText(timeFormatted)
        textQuestionBar!!.setText("Вопрос: $questionCounter/$questionCountTotal")

        if (timeLeftInMillis < 60000) {
            textTimeBar!!.setTextColor(Color.RED)
        } else {
            textTimeBar!!.setTextColor(textColorDefaultCd)
        }
    }

    @SuppressLint("ShowToast")
    private fun checkAnswer() {

        answered = true




        val rbSelected = findViewById<RadioButton>(rbGroup!!.checkedRadioButtonId)
        val answerNr = rbGroup!!.indexOfChild(rbSelected) + 1

        if (answerNr != currentQuestion!!.getAnswerNr()) {
            if (score == 1){ // 2 errors -> end
                Toast.makeText(this, "Вы все еще не готовы", Toast.LENGTH_SHORT).show()
                score++
                textViewScore!!.setText("Ошибок: $score")
                countDownTimer!!.cancel()
                buttonConfirmNext!!.setText("Завершить")
            } else {
                score++
                textViewScore!!.setText("Ошибок: $score")
            }
        }



        showSolution()
    }

    private fun showSolution() {
        rb1!!.setTextColor(Color.RED)
        rb2!!.setTextColor(Color.RED)
        rb3!!.setTextColor(Color.RED)
        rb4!!.setTextColor(Color.RED)
        rb5!!.setTextColor(Color.RED)

        when (currentQuestion!!.getAnswerNr()) {
            1 -> {
                rb1!!.setTextColor(Color.GREEN)
                textViewQuestion!!.setText("Ответ №1 правильный")
            }
            2 -> {
                rb2!!.setTextColor(Color.GREEN)
                textViewQuestion!!.setText("Ответ №2 правильный")
            }
            3 -> {
                rb3!!.setTextColor(Color.GREEN)
                textViewQuestion!!.setText("Ответ №3 правильный")
            }
            4 -> {
                rb3!!.setTextColor(Color.GREEN)
                textViewQuestion!!.setText("Ответ №4 правильный")
            }
            5 -> {
                rb3!!.setTextColor(Color.GREEN)
                textViewQuestion!!.setText("Ответ №5 правильный")
            }
        }

        if (questionCounter < questionCountTotal && buttonConfirmNext!!.text != "Завершить") {
            buttonConfirmNext!!.setText("Следующий")
        } else {
            buttonConfirmNext!!.setText("Завершить")
        }
    }

    private fun finishQuiz() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
    }

    fun getResourceId(pVariableName: String, pResourcename: String, pPackageName: String): Int {
        try {
            return resources.getIdentifier(pVariableName, pResourcename, pPackageName)
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }

    }







}
