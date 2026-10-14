package com.example.pawpal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView // Импорт для CalendarView
import android.widget.TextView // Импорт для TextView (если используется)
import androidx.fragment.app.DialogFragment // КЛЮЧЕВОЙ ИМПОРТ ДЛЯ DIALOG FRAGMENT
import com.example.pawpal.R
import java.util.Calendar

class CalendarDialogFragment : DialogFragment() {

    interface DateSelectionListener {
        fun onDateSelected(calendar: Calendar)
    }

    private var dateSelectionListener: DateSelectionListener? = null
    private lateinit var calendarView: CalendarView
    private var selectedCalendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация View
        calendarView = view.findViewById(R.id.calendarView)
        val btnClose: Button = view.findViewById(R.id.btn_close)
        val btnToday: Button = view.findViewById(R.id.btn_today)

        // Связываем Listener с родительским фрагментом
        if (parentFragment is DateSelectionListener) {
            dateSelectionListener = parentFragment as DateSelectionListener
        }

        // 1. Обработка выбора дня в CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedCalendar.set(year, month, dayOfMonth)
        }

        // 2. Обработка кнопок
        btnClose.setOnClickListener {
            // Передаем последнюю выбранную дату
            dateSelectionListener?.onDateSelected(selectedCalendar)
            dismiss()
        }

        btnToday.setOnClickListener {
            // Передаем текущую дату
            val today = Calendar.getInstance()
            dateSelectionListener?.onDateSelected(today)
            dismiss()
        }
    }
}