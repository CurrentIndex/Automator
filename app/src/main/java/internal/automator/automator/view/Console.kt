package internal.automator.automator.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.RelativeLayout
import com.google.android.material.textview.MaterialTextView
import internal.automator.automator.R

class Console : RelativeLayout {
    private var adapter: ArrayAdapter<String>
    private var clearTextView: MaterialTextView
    private var commandTextView: MaterialTextView


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        LayoutInflater.from(context).inflate(R.layout.console, this)
        adapter = ArrayAdapter(context!!, R.layout.console_list_item, R.id.console_list_item)
        val listView = findViewById<ListView>(R.id.console_listView)
        listView.adapter = adapter
        clearTextView = findViewById(R.id.clear)
        clearTextView.setOnClickListener { adapter.clear() }
        commandTextView = findViewById(R.id.command)
    }

    fun log(content: Any?, samePrint: Boolean = true) {
        adapter.add((content ?: "").toString())
        if (samePrint) println(content)
    }

    fun setOnCommandListener(listener: OnClickListener) {
        this.commandTextView.setOnClickListener(listener)
    }
}