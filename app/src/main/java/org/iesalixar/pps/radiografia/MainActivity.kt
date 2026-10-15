package org.iesalixar.pps.radiografia

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.iesalixar.pps.radiografia.data.NoticeRepository
import org.iesalixar.pps.radiografia.validation.NoticeValidator

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val configText = findViewById<TextView>(R.id.configText)
        val countText = findViewById<TextView>(R.id.noticeCountText)
        val addButton = findViewById<Button>(R.id.addNoticeButton)

        configText.text = getString(
            R.string.config_summary,
            BuildConfig.APP_ENV,
            BuildConfig.API_BASE_URL
        )

        fun refreshCount() {
            countText.text = getString(
                R.string.notice_count,
                NoticeRepository.findAll().size
            )
        }

        addButton.setOnClickListener {
            val title = "Aviso creado desde la app"
            val severity = "low"

            if (NoticeValidator.isValid(title, severity)) {
                NoticeRepository.add(title, severity)
                refreshCount()
            }
        }

        refreshCount()
    }
}
