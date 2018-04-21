package com.example.android.maiauto

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.maiauto.R.id.message
import kotlinx.android.synthetic.main.activity_main.*
import android.R.id.edit
import android.app.Activity
import android.content.SharedPreferences
import android.widget.*


class MainActivity : AppCompatActivity() {


    val manager = supportFragmentManager

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {

                setListView(1)
                setStartTest(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
//                message.setText(R.string.title_dashboard)

                setListView(0)
                setStartTest(1)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListView(1)
        setStartTest(0)



        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    private class MyCustomAdapter(context: Context): BaseAdapter() {

        private val mContext: Context = context

        private val themes = arrayListOf<String>(

                "1. Основные понятия и термины.", "2. Обязанности водителей.", "3. Обязанности пешеходов.",
                "4. Обязанности пассажиров.", "5.1. Предупреждающие знаки.", "5.2. Знаки приоритета.",
                "5.3. Запрещающие знаки.", "5.4. Предписывающие знаки", "5.5. Информационно-указательные знаки.", "5.6. Знаки сервиса и знаки дополнительной информации (таблички).",
                "6. Дорожная разметка.", "7. Сигналы светофора и \nрегулировщика.", "8. Применение специальных сигналов.",
                "9. Применение аварийной \nсигнализации и знака аварийной остановки.", "10. Маневрирование.",
                "11. Расположение транспортных средств на проезжей части.", "12. Скорость движения.",
                "13. Обгон и встречный разъезд.", "14. Остановка и стоянка.", "15. Движение через \nжелезнодорожные пути.",
                "16. Применение внешних световых приборов.", "17. Буксировка транспортных средств.", "18. Учебная езда.",
                "19. Перевозка пассажиров.", "20. Перевозка грузов.",
                "21. Дополнительные требования к движению велосипедов, мопедов, \nгужевых повозок и прогону животных.",
                "22. Основные положения по допуску транспортных средств к эксплуатации.",
                "23. Перечень неисправностей и \nусловий при которых запрещается \nэксплуатация транспортных средств.",
                "24. Безопасность дорожного \nдвижения.",
                "25. Ответственность водителей за нарушения ПДД.", "26. Первая помощь пострадавшим \nпри дтп.",
                " "
                )

        override fun getCount(): Int {
            return themes.size
        }

        //Just ignore this 2 guys
        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getItem(p0: Int): Any {
            return themes.get(p0)
        }


        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

            val layoutInfl = LayoutInflater.from(mContext)
            val rowThemes = layoutInfl.inflate(R.layout.row_themes, viewGroup, false)

            val textThemes = rowThemes.findViewById<TextView>(R.id.theme_textView)
            textThemes.text =  themes.get(position)

            return rowThemes
        }





    }

    private fun setListView(visibility : Int){


        val listView = findViewById<ListView>(R.id.main_listview)

        if (visibility == 0) {
            listView.visibility = View.INVISIBLE
        }else{
            listView.adapter = MyCustomAdapter(this )
            listView.visibility = View.VISIBLE

            listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val intent = Intent(this@MainActivity, TopicActivity::class.java)
                intent.putExtra("TopicName", listView.getItemAtPosition(i).toString())
                if ((i+1) < 5) {
                    intent.putExtra("TopicInt", (i + 1).toString())
                }else{
                    when (i+1) {
                        5 -> {
                            intent.putExtra("TopicInt", "5.1")
                        }
                        6 -> {
                            intent.putExtra("TopicInt", "5.2")
                        }
                        7 -> {
                            intent.putExtra("TopicInt", "5.3")
                        }
                        8 -> {
                            intent.putExtra("TopicInt", "5.4")
                        }
                        9 -> {
                            intent.putExtra("TopicInt", "5.5")
                        }
                        10 -> {
                            intent.putExtra("TopicInt", "5.6")
                        }
                        else -> {
                            intent.putExtra("TopicInt", (i - 4).toString())
                        }
                    }
                }
                startActivity(intent)
            }
        }


    }

    private fun setStartTest(visibility: Int){
        val button = findViewById<Button>(R.id.start_test)

        if (visibility == 0) {
            button.visibility = View.INVISIBLE

        }else {
            button.visibility = View.VISIBLE


            button.setOnClickListener{
                val intent = Intent(this, TestActivity::class.java)
                startActivity(intent)
            }
        }


    }






}
