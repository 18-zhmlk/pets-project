package com.example.pawpal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat

class SignInActivity : AppCompatActivity() {

    // Views из XML
    private lateinit var tvLoginTab: TextView
    private lateinit var tvRegisterTab: TextView
    private lateinit var indicator: View
    private lateinit var etLogin: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var rootCardInner: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ЗАМЕНИ на имя твоего лэйаута, если другое:
        setContentView(R.layout.activity_sign_in)

        // --- findViewById (инициализируем поля класса, НЕ локальные переменные) ---
        tvLoginTab = findViewById(R.id.tv_login_tab)
        tvRegisterTab = findViewById(R.id.tv_register_tab)
        indicator = findViewById(R.id.tab_indicator_line)
        etLogin = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        // Внутренний ConstraintLayout внутри CardView (родитель для indicator и заголовков)
        rootCardInner = indicator.parent as ConstraintLayout

        // Стартовое состояние: вкладка "Войти"
        selectLoginTab(animate = false)

        // Переключатели вкладок
        tvLoginTab.setOnClickListener { selectLoginTab(animate = true) }

        // Когда пользователь нажимает "Регистрация" — открываем экран регистрации и визуально переключаем вкладку
        tvRegisterTab.setOnClickListener {
            selectRegisterTab(animate = true)
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Вход по кнопке
        btnLogin.setOnClickListener {
            hideKeyboard()
            attemptLogin()
        }

        // Вход по Enter на поле пароля
        etPassword.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                hideKeyboard()
                attemptLogin()
                true
            } else false
        }
    }

    // ---- UI: выбор вкладок ----
    private fun selectLoginTab(animate: Boolean) {
        colorTabs(
            selected = tvLoginTab,
            unselected = tvRegisterTab
        )
        moveIndicatorUnder(tvLoginTab, animate)
        btnLogin.text = "Войти"
    }

    private fun selectRegisterTab(animate: Boolean) {
        // ТОЛЬКО обновляем UI — запуск активности делаем в обработчике клика
        colorTabs(
            selected = tvRegisterTab,
            unselected = tvLoginTab
        )
        moveIndicatorUnder(tvRegisterTab, animate)
        btnLogin.text = "Зарегистрироваться"
    }

    private fun colorTabs(selected: TextView, unselected: TextView) {
        val dark = ContextCompat.getColor(this, R.color.text_dark)
        val hint = ContextCompat.getColor(this, R.color.text_hint)
        selected.setTextColor(dark)
        selected.setTypeface(selected.typeface, android.graphics.Typeface.BOLD)
        unselected.setTextColor(hint)
        unselected.setTypeface(unselected.typeface, android.graphics.Typeface.NORMAL)
    }

    private fun moveIndicatorUnder(target: View, animate: Boolean) {
        // Меняем констрейнты линии к выбранной вкладке
        val set = ConstraintSet()
        set.clone(rootCardInner)
        set.clear(indicator.id, ConstraintSet.START)
        set.clear(indicator.id, ConstraintSet.END)
        set.connect(indicator.id, ConstraintSet.START, target.id, ConstraintSet.START)
        set.connect(indicator.id, ConstraintSet.END, target.id, ConstraintSet.END)
        if (animate) {
            androidx.transition.TransitionManager.beginDelayedTransition(rootCardInner)
        }
        set.applyTo(rootCardInner)
    }

    // ---- Логика входа (валидация + переход) ----
    private fun attemptLogin() {
        val login = etLogin.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Простая валидация e-mail/телефон
        if (login.isEmpty()) {
            etLogin.error = "Введите почту или телефон"
            etLogin.requestFocus()
            return
        }
        if (!isEmail(login) && !isPhone(login)) {
            etLogin.error = "Неверный формат (email или телефон)"
            etLogin.requestFocus()
            return
        }
        if (password.length < 6) {
            etPassword.error = "Минимум 6 символов"
            etPassword.requestFocus()
            return
        }

        // TODO: здесь твоя реальная проверка (через сервер/Room и т.д.)
        // Пока просто показываем успех и открываем главную
        Toast.makeText(this, "Добро пожаловать!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish() // закрыть экран входа
    }

    private fun isEmail(s: String): Boolean =
        !TextUtils.isEmpty(s) && Patterns.EMAIL_ADDRESS.matcher(s).matches()

    private fun isPhone(s: String): Boolean =
        s.length in 9..20 && Patterns.PHONE.matcher(s).matches()

    // ---- Утилита: спрятать клавиатуру ----
    private fun hideKeyboard() {
        currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
