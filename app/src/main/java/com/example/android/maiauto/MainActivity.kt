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
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.example.android.maiauto.R.id.message
import kotlinx.android.synthetic.main.activity_main.*

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
                "1. Основные понятия и термины","2. Обязанности водителей","3. Обязанности пешеходов",
                "4. Обязанности пассажиров","5. Обязанности водителей","6. Знаки приоритета и проезд перекрестков",
                "7. Основные понятия и термины","8. Обязанности водителей","9. Обязанности пешеходов",
                "10. Основные понятия и термины","11. Обязанности водителей","12. Обязанности пешеходов",
                "13. Основные понятия и термины","14. Обязанности водителей","15. Обязанности пешеходов",
                "16. Основные понятия и термины","17. Обязанности водителей","18. Обязанности пешеходов",
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
            return "TEST"
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
        }


    }

    private fun setStartTest(visibility: Int){
        var button = findViewById<Button>(R.id.start_test)
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
