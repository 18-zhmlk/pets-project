package com.example.pawpal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.pawpal.models.Pet

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Скрываем ActionBar для полноэкранного вида, если это нужно
        supportActionBar?.hide()

        setContentView(R.layout.activity_sign_up)

        // Настройка Spinner для выбора типа питомца
        val spinnerPetType: Spinner = findViewById(R.id.spinner_pet_type)

        val signUpButton: android.widget.Button = this.findViewById(R.id.btn_register)
        val signInText: TextView = findViewById(R.id.tv_login_tab) // Если "Войти" - это текст

        // Поля ввода данных питомца и владельца (добавь эти id в свой XML, если их ещё нет)
        val etPetName = findViewById<EditText>(R.id.et_pet_name)
        val etPetBreed = findViewById<EditText>(R.id.et_breed)
        val etPetBirth = findViewById<EditText>(R.id.etBirthdate)
        val etPetGender = findViewById<EditText>(R.id.etGender)
        val etOwnerName = findViewById<EditText>(R.id.et_owner_name)
        val etOwnerEmail = findViewById<EditText>(R.id.etOwnerEmail)
        val etOwnerPhone = findViewById<EditText>(R.id.etOwnerPhone)

        signUpButton.setOnClickListener {
            // Собираем данные
            val petName = etPetName.text.toString().trim()
            val petType = spinnerPetType.selectedItem?.toString()?.trim().orEmpty()
            val petBreed = etPetBreed.text.toString().trim()
            val petGender = etPetGender.text.toString().trim()
            val petBirth = etPetBirth.text.toString().trim()

            val ownerName = etOwnerName.text.toString().trim()
            val ownerEmail = etOwnerEmail.text.toString().trim()
            val ownerPhone = etOwnerPhone.text.toString().trim()

            // Проверяем поля
            if (petName.isEmpty()) {
                etPetName.error = "Введите имя питомца"
                etPetName.requestFocus()
                return@setOnClickListener
            }
            if (ownerName.isEmpty()) {
                etOwnerName.error = "Введите имя владельца"
                etOwnerName.requestFocus()
                return@setOnClickListener
            }

            // Сохраняем в базу Room
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val dao = AppDatabaseProvider.get(this@SignUpActivity).petDao()
                    val pet = Pet(
                        name = petName,
                        type = petType,
                        breed = petBreed,
                        gender = petGender,
                        birthdate = petBirth,
                        ownerName = ownerName,
                        ownerEmail = ownerEmail,
                        ownerPhone = ownerPhone
                    )
                    dao.insert(pet)
                }

                // После успешной регистрации
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUpActivity, "Регистрация успешна!", Toast.LENGTH_SHORT).show()

                    // Переходим на экран входа
                    val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        // Переход на экран входа
        signInText.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // Создание адаптера для выбора типа питомца
        ArrayAdapter.createFromResource(
            this,
            R.array.pet_types, // Этот массив нужно добавить в strings.xml
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPetType.adapter = adapter
        }
    }
}
