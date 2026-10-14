package com.example.pawpal.fragments // Убедитесь, что package правильный

import android.net.Uri // <--- ДОБАВЬТЕ ЭТОТ ИМПОРТ
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pawpal.AppDatabaseProvider
import com.example.pawpal.R
import com.example.pawpal.dao.PetDao
import com.example.pawpal.models.Pet
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var petDao: PetDao
    private var currentPet: Pet? = null

    // Объявляем ivPet на уровне класса, чтобы иметь к нему доступ в разных методах
    private lateinit var ivPet: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация Room
        petDao = AppDatabaseProvider.get(requireContext()).petDao()

        // Инициализация ivPet
        ivPet = view.findViewById(R.id.ivPet)

        // Загрузка данных из БД
        viewLifecycleOwner.lifecycleScope.launch {
            val pet = withContext(Dispatchers.IO) {
                // ... (ваш код загрузки питомца остается без изменений)
                val existing = petDao.getAll().firstOrNull()
                existing ?: run {
                    val created = Pet(
                        name = "Пушистик",
                        type = "Кот",
                        breed = "Шотландская",
                        gender = "Женский",
                        birthdate = "28/04/2021",
                        ownerName = "Жамиля Ляйля",
                        ownerEmail = "qamelova@gmail.com",
                        ownerPhone = "+7 777 777 14 18"
                    )
                    petDao.insert(created)
                    petDao.getAll().first()
                }
            }
            currentPet = pet
            bindPetToUi(pet)
            hookButtons()
        }

        // --- ИСПРАВЛЕННЫЙ БЛОК ДЛЯ АВАТАРА ---
        val cardAvatar: MaterialCardView = view.findViewById(R.id.cardAvatar)

        // Регистрация ActivityResultLauncher для получения изображения из галереи
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Устанавливаем выбранное изображение в ImageView
                ivPet.setImageURI(it)

                // Сохраняем URI в базу данных
                currentPet?.let { pet ->
                    pet.avatarUri = it.toString()
                    savePet(pet)
                }
            }
        }

        cardAvatar.setOnClickListener {
            // Запускаем выбор изображения из галереи
            pickImageLauncher.launch("image/*")
        }
    }

    private fun bindPetToUi(p: Pet) {
        // Устанавливаем аватар, если он есть
        p.avatarUri?.let {
            ivPet.setImageURI(Uri.parse(it))
        }

        setRow(R.id.row_name,      "Имя:",            p.name)
        setRow(R.id.row_type,      "Тип:",            p.type)
        // ... (остальной код биндинга)
        setRow(R.id.row_breed,     "Порода:",         p.breed)
        setRow(R.id.row_gender,    "Пол:",            p.gender)
        setRow(R.id.row_birthdate, "Дата рождения:",  p.birthdate)
        setRow(R.id.row_owner_name,  "ФИО:",           p.ownerName)
        setRow(R.id.row_owner_email, "Адрес почты:",   p.ownerEmail)
        setRow(R.id.row_owner_phone, "Номер телефона:",p.ownerPhone)
    }

    private fun savePet(updated: Pet) {
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                petDao.update(updated)
            }
            currentPet = updated
            // Перерисовывать UI не нужно, если обновляется только аватар,
            // но для остальных данных это необходимо.
            bindPetToUi(updated)
        }
    }

    // --- Остальные ваши хелперы и диалоги (без изменений) ---

    private fun row(rootId: Int): View? = view?.findViewById(rootId)

    private fun setRow(rootId: Int, label: String, value: String?) {
        val root = row(rootId) ?: return
        root.findViewById<TextView>(R.id.tvKey)?.text = label
        root.findViewById<TextView>(R.id.tvValue)?.text = value.orEmpty()
    }

    private fun hookButtons() {
        view?.findViewById<View>(R.id.btnEditPet)?.setOnClickListener { showEditPetDialog() }
        view?.findViewById<View>(R.id.btnEditOwner)?.setOnClickListener { showEditOwnerDialog() }
    }

    private fun showEditPetDialog() {
        // ... (без изменений)
        val pet = currentPet ?: return
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_pet, null, false)
        val etName      = dialogView.findViewById<EditText>(R.id.etName).apply      { setText(pet.name) }
        val etType      = dialogView.findViewById<EditText>(R.id.etType).apply      { setText(pet.type) }
        val etBreed     = dialogView.findViewById<EditText>(R.id.etBreed).apply     { setText(pet.breed) }
        val etGender    = dialogView.findViewById<EditText>(R.id.etGender).apply    { setText(pet.gender) }
        val etBirthdate = dialogView.findViewById<EditText>(R.id.etBirthdate).apply { setText(pet.birthdate) }

        AlertDialog.Builder(requireContext())
            .setTitle("Редактировать данные питомца")
            .setView(dialogView)
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Сохранить") { _, _ ->
                val updated = pet.copy(
                    name = etName.text.toString().trim(),
                    type = etType.text.toString().trim(),
                    breed = etBreed.text.toString().trim(),
                    gender = etGender.text.toString().trim(),
                    birthdate = etBirthdate.text.toString().trim()
                )
                savePet(updated)
            }
            .show()
    }

    private fun showEditOwnerDialog() {
        // ... (без изменений)
        val pet = currentPet ?: return
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_owner, null, false)
        val etOwnerName  = dialogView.findViewById<EditText>(R.id.etOwnerName).apply  { setText(pet.ownerName) }
        val etOwnerEmail = dialogView.findViewById<EditText>(R.id.etOwnerEmail).apply { setText(pet.ownerEmail) }
        val etOwnerPhone = dialogView.findViewById<EditText>(R.id.etOwnerPhone).apply { setText(pet.ownerPhone) }

        AlertDialog.Builder(requireContext())
            .setTitle("Редактировать данные владельца")
            .setView(dialogView)
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Сохранить") { _, _ ->
                val updated = pet.copy(
                    ownerName  = etOwnerName.text.toString().trim(),
                    ownerEmail = etOwnerEmail.text.toString().trim(),
                    ownerPhone = etOwnerPhone.text.toString().trim()
                )
                savePet(updated)
            }
            .show()
    }
}
