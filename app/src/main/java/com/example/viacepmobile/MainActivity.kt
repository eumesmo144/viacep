import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.viacepmobile.model.dados.Endereco
import com.example.viacepmobile.model.dados.RetrofitClient
import com.example.viacepmobile.model.dados.Usuario
import com.example.viacepmobile.model.dados.UsuarioDAO
import com.example.viacepmobile.ui.theme.ViacepmobileTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ViacepmobileTheme {

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var usuarioList by remember { mutableStateOf<List<Usuario>>(emptyList()) }
                    var endereco by remember { mutableStateOf<Endereco?>(null) }

                    LaunchedEffect(Unit) {
                        loadUsuarios { usuarios ->
                            usuarioList = usuarios
                        }
                        endereco = buscarEndereco()
                    }

                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {

                        if (endereco != null) {
                            EnderecoInfo(endereco!!)
                        }

                        if (usuarioList.isNotEmpty()) {
                            Greeting(name = usuarioList[0].nome)
                        } else {
                            Greeting(name = "Nenhum usuário encontrado")
                        }
                    }
                }
            }
        }
    }

    private fun loadUsuarios(onResult: (List<Usuario>) -> Unit) {
        val usuarioDAO = UsuarioDAO()
        usuarioDAO.buscar { usuarios ->
            onResult(usuarios)
        }
    }

    private suspend fun buscarEndereco(): Endereco? {
        return withContext(Dispatchers.IO) {
            try {
                RetrofitClient.usuarioService.getEndereço()
            } catch (e: Exception) {
                null
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Composable
fun EnderecoInfo(endereco: Endereco) {
    Text(text = "Endereço: ${endereco.logradouro}, ${endereco.localidade}, ${endereco.uf}")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ViacepmobileTheme {
        Greeting("Android")
    }
}
