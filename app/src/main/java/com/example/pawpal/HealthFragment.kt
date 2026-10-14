package com.example.pawpal.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AlertDialog
import com.example.pawpal.R
import com.example.pawpal.AppDatabaseProvider
import com.example.pawpal.health.*
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.widget.TextView
import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button


class HealthFragment : Fragment(R.layout.fragment_health) {

    // Внутри класса HealthFragment
    private val PREFS = "health_prefs"
    private val KEY_VET_PHONE = "vet_phone"

    private fun showEditVetPhoneDialog(tv: TextView) {
        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = tv.text.toString()

        val input = EditText(requireContext()).apply {
            hint = "Номер телефона"
            setText(current)
            setSelection(text.length)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Номер ветеринара")
            .setView(input)
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Сохранить") { _, _ ->
                val newPhone = input.text.toString().trim()
                if (newPhone.isEmpty() || !Patterns.PHONE.matcher(newPhone).matches()) {
                    // простая проверка формата
                    tv.error = "Некорректный номер"
                    return@setPositiveButton
                }
                prefs.edit().putString(KEY_VET_PHONE, newPhone).apply()
                tv.text = newPhone
            }
            .show()
    }


    private lateinit var vacAdapter: VaccinationAdapter
    private lateinit var allAdapter: AllergyAdapter
    private val db by lazy { AppDatabaseProvider.get(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<CardView>(R.id.card_vaccinations_container)
            .setOnClickListener { openFullVaccinationsSheet() }

        view.findViewById<CardView>(R.id.card_allergies_container)
            .setOnClickListener { openFullAllergiesSheet() }

        val tvVetPhone = view.findViewById<TextView>(R.id.tv_vet_phone)

        // 1) загрузили сохранённый номер (или дефолтный)
        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val savedPhone = prefs.getString(KEY_VET_PHONE, "+7 777 777 14 18")!!
        tvVetPhone.text = savedPhone

        // 2) короткий тап — открыть звонилку
        tvVetPhone.setOnClickListener {
            val clean = savedPhone.filter { !it.isWhitespace() }   // убираем пробелы
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clean")))
        }

        // 3) долгий тап — редактирование и сохранение
        tvVetPhone.setOnLongClickListener {
            showEditVetPhoneDialog(tvVetPhone)
            true
        }

        // RV: вакцинации
        val rvVac = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_vaccinations)
        rvVac.layoutManager = LinearLayoutManager(requireContext())
        vacAdapter = VaccinationAdapter { item -> openVaccinationDialog(item) }
        rvVac.adapter = vacAdapter

        // RV: аллергии
        val rvAll = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_allergies)
        rvAll.layoutManager = LinearLayoutManager(requireContext())
        allAdapter = AllergyAdapter { item -> openAllergyDialog(item) }
        rvAll.adapter = allAdapter

        // Кнопки "+"
        view.findViewById<View>(R.id.btn_add_vaccine).setOnClickListener { openVaccinationDialog(null) }
        view.findViewById<View>(R.id.btn_add_allergy).setOnClickListener { openAllergyDialog(null) }

        // Подписки на Room (Flow)
        viewLifecycleOwner.lifecycleScope.launch {
            db.vaccinationDao().observeAll().collect { vacAdapter.submit(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            db.allergyDao().observeAll().collect { allAdapter.submit(it) }
        }



    }


    private fun openFullVaccinationsSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val content = layoutInflater.inflate(R.layout.bottomsheet_health_list, null)
        dialog.setContentView(content)

        val title = content.findViewById<TextView>(R.id.tvTitle)
        val rv = content.findViewById<RecyclerView>(R.id.rvList)
        val btnAdd = content.findViewById<Button>(R.id.btnAdd)

        title.text = "Все записи о вакцинации"
        rv.layoutManager = LinearLayoutManager(requireContext())

        // отдельный адаптер (чтобы не конфликтовал с тем, что на экране)
        val sheetAdapter = VaccinationAdapter { item -> openVaccinationDialog(item) }
        rv.adapter = sheetAdapter

        // подгружаем из Room
        viewLifecycleOwner.lifecycleScope.launch {
            db.vaccinationDao().observeAll().collect { list ->
                sheetAdapter.submit(list)
            }
        }

        btnAdd.setOnClickListener { openVaccinationDialog(null) }

        dialog.show()
    }

    // ===== BottomSheet со всеми аллергиями =====
    private fun openFullAllergiesSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val content = layoutInflater.inflate(R.layout.bottomsheet_health_list, null)
        dialog.setContentView(content)

        val title = content.findViewById<TextView>(R.id.tvTitle)
        val rv = content.findViewById<RecyclerView>(R.id.rvList)
        val btnAdd = content.findViewById<Button>(R.id.btnAdd)

        title.text = "Все аллергии"
        rv.layoutManager = LinearLayoutManager(requireContext())

        val sheetAdapter = AllergyAdapter { item -> openAllergyDialog(item) }
        rv.adapter = sheetAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            db.allergyDao().observeAll().collect { list ->
                sheetAdapter.submit(list)
            }
        }

        btnAdd.setOnClickListener { openAllergyDialog(null) }

        dialog.show()
    }



    private fun openVaccinationDialog(item: Vaccination?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_vaccination, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etDate  = dialogView.findViewById<EditText>(R.id.etDate)
        if (item != null) { etTitle.setText(item.title); etDate.setText(item.date) }

        val b = AlertDialog.Builder(requireContext())
            .setTitle(if (item == null) "Новая вакцинация" else "Редактирование вакцинации")
            .setView(dialogView)
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Сохранить") { _, _ ->
                val updated = Vaccination(
                    id = item?.id ?: 0,
                    title = etTitle.text.toString().trim(),
                    date  = etDate.text.toString().trim()
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    if (item == null) db.vaccinationDao().insert(updated)
                    else db.vaccinationDao().update(updated)
                }
            }

        if (item != null) {
            b.setNeutralButton("Удалить") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    db.vaccinationDao().delete(item)
                }
            }
        }
        b.show()
    }

    private fun openAllergyDialog(item: Allergy?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_allergy, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etNote  = dialogView.findViewById<EditText>(R.id.etNote)
        if (item != null) { etTitle.setText(item.title); etNote.setText(item.note) }

        val b = AlertDialog.Builder(requireContext())
            .setTitle(if (item == null) "Новая аллергия" else "Редактирование аллергии")
            .setView(dialogView)
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Сохранить") { _, _ ->
                val updated = Allergy(
                    id = item?.id ?: 0,
                    title = etTitle.text.toString().trim(),
                    note  = etNote.text.toString().trim()
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    if (item == null) db.allergyDao().insert(updated)
                    else db.allergyDao().update(updated)
                }
            }

        if (item != null) {
            b.setNeutralButton("Удалить") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    db.allergyDao().delete(item)
                }
            }
        }
        b.show()
    }
}
