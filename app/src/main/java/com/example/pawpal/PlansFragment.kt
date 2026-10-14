package com.example.pawpal.fragments

import android.app.DatePickerDialog // ИСПРАВЛЕНО: Правильный импорт
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpal.R
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlansFragment : Fragment(R.layout.fragment_plans) {

    // UI
    private lateinit var tvDate: TextView
    private lateinit var btnAddTask: ImageButton
    private lateinit var btnAddEvent: ImageButton
    private lateinit var rvTasks: RecyclerView
    private lateinit var rvEvents: RecyclerView

    // Data
    private val calendar: Calendar = Calendar.getInstance()
    private val dateFmtUi = SimpleDateFormat("MMMM\ndd", Locale.ENGLISH)
    private val dateKeyFmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private val prefsName = "plans_prefs"

    private val taskAdapter = TaskAdapter(
        onToggle = { position -> toggleTaskDone(position) },
        onLongDelete = { position -> confirmDeleteTask(position) }
    )
    private val eventAdapter = EventAdapter(
        onLongDelete = { position -> confirmDeleteEvent(position) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // find views
        tvDate = view.findViewById(R.id.tv_selected_date)
        btnAddTask = view.findViewById(R.id.btn_add_task)
        btnAddEvent = view.findViewById(R.id.btn_add_event)
        rvTasks = view.findViewById(R.id.rv_tasks)
        rvEvents = view.findViewById(R.id.rv_events)

        // lists
        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = taskAdapter

        rvEvents.layoutManager = LinearLayoutManager(requireContext())
        rvEvents.adapter = eventAdapter

        // date chip
        updateDateChip()
        tvDate.setOnClickListener { showDatePicker() }

        // add buttons
        btnAddTask.setOnClickListener { showAddDialog(isTask = true) }
        btnAddEvent.setOnClickListener { showAddDialog(isTask = false) }

        // load data for today
        loadForCurrentDate()
    }

    // -------- Date handling --------
    private fun updateDateChip() {
        tvDate.text = dateFmtUi.format(calendar.time).uppercase(Locale.ENGLISH)
    }

    private fun showDatePicker() {
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH)
        val d = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            updateDateChip()
            loadForCurrentDate()
        }, y, m, d).show()
    }

    private fun currentDateKey(): String = dateKeyFmt.format(calendar.time)

    // -------- Storage (SharedPreferences + JSON) --------
    private fun loadForCurrentDate() {
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val key = currentDateKey()

        // ИЗМЕНЕНИЕ: Если данных нет, загружаем пустые списки, а не списки по умолчанию.
        val tasks = prefs.getString("tasks_$key", null)?.let { fromJsonTasks(it) } ?: emptyList()
        val events = prefs.getString("events_$key", null)?.let { fromJsonEvents(it) } ?: emptyList()

        taskAdapter.submit(tasks)
        eventAdapter.submit(events)
    }

    private fun saveCurrent() {
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val key = currentDateKey()
        prefs.edit {
            putString("tasks_$key", toJson(taskAdapter.items))
            putString("events_$key", toJson(eventAdapter.items))
        }
    }

    // -------- Add / Toggle / Delete --------
    private fun showAddDialog(isTask: Boolean) {
        val input = EditText(requireContext())
        input.hint = if (isTask) "Новая задача" else "Новое событие"
        input.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(requireContext())
            .setTitle(if (isTask) "Добавить задачу" else "Добавить событие")
            .setView(input)
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Добавить") { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) {
                    if (isTask) taskAdapter.add(Task(text, false)) else eventAdapter.add(Event(text))
                    saveCurrent()
                }
            }
            .show()
    }

    private fun toggleTaskDone(position: Int) {
        val task = taskAdapter.items[position]
        task.done = !task.done
        taskAdapter.notifyItemChanged(position)
        saveCurrent()
    }

    private fun confirmDeleteTask(position: Int) {
        AlertDialog.Builder(requireContext())
            .setMessage("Удалить задачу?")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Удалить") { _, _ ->
                taskAdapter.remove(position); saveCurrent()
            }
            .show()
    }

    private fun confirmDeleteEvent(position: Int) {
        AlertDialog.Builder(requireContext())
            .setMessage("Удалить событие?")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Удалить") { _, _ ->
                eventAdapter.remove(position); saveCurrent()
            }
            .show()
    }

    // -------- Models --------
    data class Task(var title: String, var done: Boolean)
    data class Event(var title: String)

    // -------- Adapters --------

    // ViewHolder для ЗАДАЧ, использует ID из item_task.xml
    private class TaskVH(v: View) : RecyclerView.ViewHolder(v) {
        // Безопасно ищем все элементы по их настоящим ID из XML
        val tvTitle: TextView? = try { v.findViewById(R.id.tv_task_title) } catch (e: Exception) { null }
        val checkView: View? = try { v.findViewById(R.id.iv_checkbox) } catch (e: Exception) { null }
    }

    private inner class TaskAdapter(
        val onToggle: (Int) -> Unit,
        val onLongDelete: (Int) -> Unit
    ) : RecyclerView.Adapter<TaskVH>() {

        val items = mutableListOf<Task>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task, parent, false)
            return TaskVH(v)
        }

        override fun onBindViewHolder(h: TaskVH, position: Int) {
            val item = items[position]
            // Проверяем, что tvTitle не null, перед использованием
            h.tvTitle?.let {
                it.text = item.title
                it.paint.isStrikeThruText = item.done
            }

            h.checkView?.isVisible = true
            h.checkView?.setOnClickListener { onToggle(position) }
            h.itemView.setOnClickListener { onToggle(position) }
            h.itemView.setOnLongClickListener { onLongDelete(position); true }
        }

        override fun getItemCount() = items.size
        fun submit(list: List<Task>) { items.setAll(list); notifyDataSetChanged() }
        fun add(t: Task) { items.add(t); notifyItemInserted(items.lastIndex) }
        fun remove(pos: Int) { items.removeAt(pos); notifyItemRemoved(pos) }
    }

    // ViewHolder для СОБЫТИЙ, использует ID из item_event.xml
    private class EventVH(v: View) : RecyclerView.ViewHolder(v) {
        // Безопасно ищем элемент по его настоящему ID из XML
        val tvTitle: TextView? = try { v.findViewById(R.id.tv_event_title) } catch (e: Exception) { null }
    }

    private inner class EventAdapter(
        val onLongDelete: (Int) -> Unit
    ) : RecyclerView.Adapter<EventVH>() {

        val items = mutableListOf<Event>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventVH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event, parent, false)
            return EventVH(v)
        }

        override fun onBindViewHolder(h: EventVH, position: Int) {
            val item = items[position]
            // Проверяем, что tvTitle не null, перед использованием
            h.tvTitle?.text = item.title
            h.itemView.setOnLongClickListener { onLongDelete(position); true }
        }

        override fun getItemCount() = items.size
        fun submit(list: List<Event>) { items.setAll(list); notifyDataSetChanged() }
        fun add(e: Event) { items.add(e); notifyItemInserted(items.lastIndex) }
        fun remove(pos: Int) { items.removeAt(pos); notifyItemRemoved(pos) }
    }

    // -------- Utils --------
    private fun <T> MutableList<T>.setAll(newList: List<T>) { clear(); addAll(newList) }

    private fun toJson(tasks: List<Task>): String {
        val arr = JSONArray()
        tasks.forEach { t ->
            arr.put(JSONObject().apply {
                put("title", t.title)
                put("done", t.done)
            })
        }
        return arr.toString()
    }

    @JvmName("eventsToJson")
    private fun toJson(events: List<Event>): String {
        val arr = JSONArray()
        events.forEach { e -> arr.put(JSONObject().put("title", e.title)) }
        return arr.toString()
    }

    private fun fromJsonTasks(s: String): List<Task> {
        val arr = JSONArray(s)
        val out = mutableListOf<Task>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out.add(Task(o.optString("title"), o.optBoolean("done")))
        }
        return out
    }

    private fun fromJsonEvents(s: String): List<Event> {
        val arr = JSONArray(s)
        val out = mutableListOf<Event>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out.add(Event(o.optString("title")))
        }
        return out
    }

}
