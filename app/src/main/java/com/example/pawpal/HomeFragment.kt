package com.example.pawpal.fragments // Создайте пакет fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import com.example.pawpal.R
import kotlin.random.Random

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var tvThoughtOfTheDay: TextView

    // Список фраз "Мысль дня"
    private val petThoughts = listOf(
        "Моя миска пуста. Это катастрофа.",
        "Зачем человек уходит, если можно просто погладить меня?",
        "Сплю. Не будить. Мир подождет.",
        "Срочно нужна прогулка. И пять минут копания в земле.",
        "Кажется, я слышал, как открыли холодильник. Надо проверить.",
        "Это моя лежанка. И твой тапок тоже мой.",
        "Сегодня был отличный день для того, чтобы подремать.",
        "Люблю смотреть в окно. Вдруг там белка?"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvThoughtOfTheDay = view.findViewById(R.id.tv_thought_text)

        // Устанавливаем случайную "Мысль дня"
        setThoughtOfTheDay()
    }

    private fun setThoughtOfTheDay() {
        val randomIndex = Random.nextInt(petThoughts.size)
        tvThoughtOfTheDay.text = "😺 ${petThoughts[randomIndex]}"
    }
}