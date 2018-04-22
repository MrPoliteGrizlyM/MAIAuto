package com.example.android.maiauto

import android.app.ActionBar
import android.content.Intent
import android.content.res.ColorStateList
import android.database.SQLException
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_topic.*
import java.io.IOException
import java.util.ArrayList

class TopicActivity : AppCompatActivity() {

    private val KEY_HINT = "keyHint"
    private val KEY_QUESTION_COUNT = "keyQuestionCount"
    private val KEY_ANSWERED = "keyAnswered"
    private val KEY_QUESTION_LIST = "keyQuestionList"

    private var textTopicBar: TextView? = null
    private var textRightBar: TextView? = null
    private var textViewQuestion: TextView? = null
    private var textHint: TextView? = null

    private var rbGroup: RadioGroup? = null
    private var rb1: RadioButton? = null
    private var rb2: RadioButton? = null
    private var rb3: RadioButton? = null
    private var rb4: RadioButton? = null
    private var rb5: RadioButton? = null

    private var imageQuestion: ImageView? = null
    private var currentImage : String? = null


    private var textColorDefaultRb: ColorStateList? = null


    private var textQuestion: TextView? = null
    private var bundle:  Bundle? = null
    private var textTopic:  String? = null
    private var textInt:  String? = null
    private var questionList: ArrayList<Question>? = null

    private var questionCounter: Int = 0
    private var questionCountTotal: Int = 0
    private var currentQuestion: Question? = null
    private var buttonConfirmNext: Button? = null
    private var buttonNext: Button? = null
    private var buttonPrev: Button? = null
    private var buttonHint: Button? = null

    private var answered: Boolean = false
    private var hint: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM;
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setCustomView(R.layout.abs_layout)

        textTopicBar = findViewById(R.id.question_text)
        textRightBar = findViewById(R.id.countdown_text)
        textHint = findViewById(R.id.hintText)
        //textQuestion = findViewById(R.id.textQuestion)
        buttonConfirmNext = findViewById(R.id.button_confirm_next)
        buttonNext = findViewById(R.id.next)
        buttonPrev = findViewById(R.id.prev)
        buttonHint = findViewById(R.id.hintButton)
        textViewQuestion = findViewById(R.id.text_view_question)
        imageQuestion= findViewById(R.id.image_question)

        rbGroup = findViewById(R.id.radio_group)
        rb1 = findViewById(R.id.radio_button1)
        rb2 = findViewById(R.id.radio_button2)
        rb3 = findViewById(R.id.radio_button3)
        rb4 = findViewById(R.id.radio_button4)
        rb5 = findViewById(R.id.radio_button5)

        textColorDefaultRb = rb1!!.getTextColors();

        bundle = intent.extras
        textTopic = bundle!!.getString("TopicName")
        textInt = bundle!!.getString("TopicInt")


        textTopicBar!!.setText(textTopic)

        if (savedInstanceState == null) {

            val myDbHelper = QuizDbHelper(this@TopicActivity)



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


            questionList = myDbHelper.getTopicQuestions(textInt)

            currentQuestion = questionList?.get(0)

            textRightBar!!.setText("Вопросов: " + questionList!!.size.toString())

            questionCountTotal = questionList!!.size

            buttonPrev!!.visibility = View.INVISIBLE

            showNextQuestion()
        } else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST)
            questionCountTotal = questionList!!.size
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT)
            currentQuestion = questionList!!.get(questionCounter - 1)
            answered = savedInstanceState.getBoolean(KEY_ANSWERED)
            hint = savedInstanceState.getBoolean(KEY_HINT)

            currentImage = "image" + currentQuestion!!.image
            imageQuestion!!.setImageResource(getResourceId(currentImage!!, "drawable", getPackageName()))

            textRightBar!!.setText("Вопрос: $questionCounter/$questionCountTotal")

            checkRadioButtons()

            if (hint){
                showHint()
            }
            else {
                cancelHint()
            }

            if (answered) {
                showSolution()
            }

            checkTopButtons()

        }

        buttonConfirmNext!!.setOnClickListener(View.OnClickListener {
            if (!answered) {
                if (rb1!!.isChecked || rb2!!.isChecked || rb3!!.isChecked || rb4!!.isChecked || rb5!!.isChecked) {
                    answered = true
                    showSolution()
                } else {
                    Toast.makeText(this@TopicActivity, "Выберите ответ", Toast.LENGTH_SHORT).show()
                }
            } else {
                showNextQuestion()
            }
        })

        hintButton!!.setOnClickListener(View.OnClickListener {
            showHint()

        })

        buttonNext!!.setOnClickListener(View.OnClickListener {
            showNextQuestion()
            checkTopButtons()

        })

        buttonPrev!!.setOnClickListener(View.OnClickListener {
            questionCounter -= 2
            showNextQuestion()
            checkTopButtons()

        })




    }


    private fun showNextQuestion() {

        rb1!!.setTextColor(textColorDefaultRb);
        rb2!!.setTextColor(textColorDefaultRb)
        rb3?.setTextColor(textColorDefaultRb)
        rb4?.setTextColor(textColorDefaultRb)
        rb5?.setTextColor(textColorDefaultRb)
        rbGroup?.clearCheck()

        cancelHint()


        if (questionCounter < questionCountTotal) {

            currentQuestion = questionList?.get(questionCounter)

            textHint!!.text = currentQuestion!!.getHint()


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

            checkTopButtons()

            textRightBar!!.setText("Вопрос: $questionCounter/$questionCountTotal")


            answered = false
            buttonConfirmNext!!.setText("Подтвердить")



        } else {
            finishQuiz()
        }
    }

    private fun checkTopButtons(){
        if (questionCounter != 1){
            buttonPrev!!.visibility = View.VISIBLE
        }

        if (questionCounter != questionCountTotal){
            buttonNext!!.visibility = View.VISIBLE
        }

        if (questionCounter == 1 ){
            buttonPrev!!.visibility = View.INVISIBLE
        }

        buttonNext!!.visibility = View.VISIBLE

        if (questionCounter == questionCountTotal){
            buttonNext!!.visibility = View.INVISIBLE
        }

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

    private fun showHint(){
        hintButton.visibility = View.GONE
        hintText.visibility = View.VISIBLE
        hint = true
    }

    private fun cancelHint(){
        hintButton.visibility = View.VISIBLE
        hintText.visibility = View.GONE
        hint = false
    }



    private fun checkRadioButtons() {

        rb3!!.visibility = View.VISIBLE
        rb4!!.visibility = View.VISIBLE
        rb5!!.visibility = View.VISIBLE

        if (currentQuestion!!.option3 == null){
            rb3!!.visibility = View.GONE
            rb4!!.visibility = View.GONE
            rb5!!.visibility = View.GONE
        }
        else if (currentQuestion!!.option4 == null){
            rb4!!.visibility = View.GONE
            rb5!!.visibility = View.GONE
        }
        else if (currentQuestion!!.option5 == null){
            rb5!!.visibility = View.GONE
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

    private fun finishQuiz() {
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //outState.putString(KEY_HINT, textRightBar!!.text.toString())
        outState.putInt(KEY_QUESTION_COUNT, questionCounter)
        outState.putBoolean(KEY_ANSWERED, answered)
        outState.putBoolean(KEY_HINT, hint)
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}
