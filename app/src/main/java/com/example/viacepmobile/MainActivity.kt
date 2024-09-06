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

        // Set the UI using Jetpack Compose
        setContent {
            ViacepmobileTheme {
                // Scaffold layout to include top bars, fab, etc.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding -> // innerPadding é passado aqui
                    // Variáveis de estado para armazenar os dados
                    var usuarioList by remember { mutableStateOf<List<Usuario>>(emptyList()) }
                    var endereco by remember { mutableStateOf<Endereco?>(null) }

                    // Chamar Firebase e Retrofit ao iniciar
                    LaunchedEffect(Unit) {
                        loadUsuarios { usuarios ->
                            usuarioList = usuarios
                        }
                        endereco = buscarEndereco()
                    }

                    // Adiciona o padding interno ao layout
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) { // Usando innerPadding no layout

                        // Exibir dados do endereço se carregado
                        if (endereco != null) {
                            EnderecoInfo(endereco!!)
                        }

                        // Exibir dados do primeiro usuário ou uma mensagem padrão
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

    // Método para buscar usuários no Firebase
    private fun loadUsuarios(onResult: (List<Usuario>) -> Unit) {
        val usuarioDAO = UsuarioDAO()
        usuarioDAO.buscar { usuarios ->
            onResult(usuarios)
        }
    }

    // Método para buscar o endereço usando Retrofit com coroutines
    private suspend fun buscarEndereco(): Endereco? {
        return withContext(Dispatchers.IO) {
            try {
                RetrofitClient.usuarioService.getEndereço()
            } catch (e: Exception) {
                null // Retorna null se falhar
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
