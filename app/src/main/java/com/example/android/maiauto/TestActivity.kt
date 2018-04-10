package com.example.android.maiauto


import android.annotation.SuppressLint
import android.database.Cursor
import android.database.SQLException
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import java.io.IOException
import android.content.res.ColorStateList
import android.graphics.Color
import java.util.*
import android.os.CountDownTimer
import android.app.ActionBar
import android.util.Log
import android.widget.*


class TestActivity : AppCompatActivity() {

    internal var c: Cursor? = null
    private val COUNTDOWN_IN_MILLIS: Long = 60000 * 40

    private val KEY_SCORE = "keyScore"
    private val KEY_QUESTION_COUNT = "keyQuestionCount"
    private val KEY_MILLIS_LEFT = "keyMillisLeft"
    private val KEY_ANSWERED = "keyAnswered"
    private val KEY_QUESTION_LIST = "keyQuestionList"


    private var textViewQuestion: TextView? = null
    private var textViewScore: TextView? = null

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

    private var questionList: ArrayList<Question>? = null
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






        if (savedInstanceState == null) {

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


            Collections.shuffle(questionList)


            val questionList : List<Question> = questionList!!.subList(0,40)

            questionCountTotal = questionList.size

            showNextQuestion()

            timeLeftInMillis = COUNTDOWN_IN_MILLIS
            startCountDown()

        } else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST)
            val questionList : List<Question> = questionList!!.subList(0,40)
            questionCountTotal = questionList.size
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT)
            currentQuestion = questionList.get(questionCounter - 1)
            score = savedInstanceState.getInt(KEY_SCORE)
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT)
            answered = savedInstanceState.getBoolean(KEY_ANSWERED)


            currentImage = "image" + currentQuestion!!.image
            imageQuestion!!.setImageResource(getResourceId(currentImage!!, "drawable", getPackageName()))


            checkRadioButtons()

            if (!answered) {
                startCountDown();
            } else {
                updateCountDownText();
                showSolution();
            }


        }

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

            checkRadioButtons()

            textViewQuestion!!.text = currentQuestion!!.getQuestion()
            rb1!!.text = currentQuestion!!.getOption1()
            rb2!!.text = currentQuestion!!.getOption2()
            rb3!!.text = currentQuestion!!.getOption3()
            rb4!!.text = currentQuestion!!.getOption4()
            rb5!!.text = currentQuestion!!.getOption5()


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

    private fun checkRadioButtons() {

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
                textViewScore!!.setText("Ошибок: $score из 9")
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

        if (timeLeftInMillis < (60000 * 10)) {
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
            if (score == 8){ // 9 errors -> end
                Toast.makeText(this, "Вы все еще не готовы", Toast.LENGTH_SHORT).show()
                score++
                textViewScore!!.setText("Ошибок: $score из 9")
                countDownTimer!!.cancel()
                buttonConfirmNext!!.setText("Завершить")
            } else {
                score++
                textViewScore!!.setText("Ошибок: $score из 9")
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

            }
            2 -> {
                rb2!!.setTextColor(Color.GREEN)

            }
            3 -> {
                rb3!!.setTextColor(Color.GREEN)

            }
            4 -> {
                rb4!!.setTextColor(Color.GREEN)

            }
            5 -> {
                rb5!!.setTextColor(Color.GREEN)

            }
        }

        if (questionCounter < questionCountTotal && buttonConfirmNext!!.text != "Завершить") {
            buttonConfirmNext!!.setText("Следующий")
        } else {
            buttonConfirmNext!!.setText("Завершить")
        }
    }

    private fun finishQuiz() {
        countDownTimer!!.cancel()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SCORE, score)
        outState.putInt(KEY_QUESTION_COUNT, questionCounter)
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMillis)
        outState.putBoolean(KEY_ANSWERED, answered)
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList)
    }






// for file in *.jpg; do mv "$file" "image${file//-/_}"; done

}
