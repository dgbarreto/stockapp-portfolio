import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.danilobarreto.stockapp.portfolio.sample.SampleApp

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Portfolio Sample") {
        SampleApp()
    }
}
