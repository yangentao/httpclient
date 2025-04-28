package io.github.yangentao.httpclient


interface HttpProgress {
    fun onStart(total: Int)

    fun onProgress(current: Int, total: Int, percent: Int)

    fun onFinish(success: Boolean)
}

class DelayHttpProgress(private val wraped: HttpProgress, private val delayMill: Long = 50) : HttpProgress {
    private var fireTime: Long = 0
    private var progressTime: Long = 0

    private var lastTotal: Int = 0
    private var lastCurrent: Int = 0
    private var lastPercent: Int = 0

    override fun onStart(total: Int) {
        wraped.onStart(total)
    }

    override fun onProgress(current: Int, total: Int, percent: Int) {
        val tm = System.currentTimeMillis()
        progressTime = tm
        if (fireTime != 0L && tm - fireTime < delayMill) {
            lastCurrent = current
            lastPercent = percent
            lastTotal = total
            return
        }
        fireTime = tm
        wraped.onProgress(current, total, percent)
    }

    override fun onFinish(success: Boolean) {
        if (fireTime != progressTime) {
            fireTime = progressTime
            wraped.onProgress(lastCurrent, lastTotal, lastPercent)
        }
        wraped.onFinish(success)
    }

}