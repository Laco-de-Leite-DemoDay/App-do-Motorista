package com.example.lacoleitedriver

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LacoLeiteDriverAppV2() }
    }
}

// ============================================================
// PALETA DE CORES DO APP
// Para mudar a identidade visual do app inteiro, altere essas cores.
// ============================================================

private val DarkPurple = Color(0xFF4B006E)
private val DeepPurple = Color(0xFF5D36A8)
private val Purple = Color(0xFF7B4DCC)
private val Lilac = Color(0xFFB58CF0)
private val LilacLight = Color(0xFFEDE3FF)
private val SoftBg = Color(0xFFF8F3FF)
private val Success = Color(0xFF3B8A62)
private val Danger = Color(0xFFD15353)

// ============================================================
// TELAS DO APP
// O app troca de tela usando a variável "screen" dentro da função principal.
// ============================================================

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Cadastro : Screen()
    object Senha : Screen()
    object Home : Screen()
    object Entrega : Screen()
    object Chat : Screen()
    object Ocorrencia : Screen()
    object Perfil : Screen()
    object Historico : Screen()
}

// ============================================================
// MODELOS DE DADOS
// Delivery = dados da entrega atual.
// Occurrence = ocorrência registrada pelo motorista.
// ============================================================

data class Delivery(
    val id: String,
    val hospital: String,
    val destino: String,
    val status: String,
    val hora: String,
    val etapa: Int
)

data class Occurrence(
    val tipo: String,
    val detalhe: String
)

// ============================================================
// FUNÇÃO PRINCIPAL DO APP
// Aqui ficam os estados principais e a navegação entre telas.
// ============================================================

@Composable
fun LacoLeiteDriverAppV2() {
    var screen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var entregasFinalizadas by remember { mutableIntStateOf(8) }
    val ocorrencias = remember { mutableStateListOf<Occurrence>() }

    var currentDelivery by remember {
        mutableStateOf(
            Delivery(
                id = "LL-2048",
                hospital = "Hospital Municipal",
                destino = "Banco de Leite Zona Sul",
                status = "Leite coletado",
                hora = "14:30",
                etapa = 2
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SoftBg
    ) {
        when (screen) {
            Screen.Splash -> SplashScreen {
                screen = Screen.Login
            }

            Screen.Login -> LoginScreen(
                onEnter = { screen = Screen.Home },
                onRegister = { screen = Screen.Cadastro },
                onForgot = { screen = Screen.Senha }
            )

            Screen.Cadastro -> RegisterScreen {
                screen = Screen.Login
            }

            Screen.Senha -> ForgotPasswordScreen {
                screen = Screen.Login
            }

            Screen.Home -> HomeScreen(
                delivery = currentDelivery,
                finalizadas = entregasFinalizadas,
                ocorrencias = ocorrencias.size,
                onDelivery = { screen = Screen.Entrega },
                onChat = { screen = Screen.Chat },
                onOccurrence = { screen = Screen.Ocorrencia },
                onProfile = { screen = Screen.Perfil },
                onHistory = { screen = Screen.Historico }
            )

            Screen.Entrega -> DeliveryProcessScreen(
                delivery = currentDelivery,
                onBack = { screen = Screen.Home },
                onStepChange = { novaEtapa ->
                    currentDelivery = currentDelivery.copy(
                        etapa = novaEtapa,
                        status = deliveryStatus(novaEtapa)
                    )
                },
                onFinish = {
                    entregasFinalizadas++
                    currentDelivery = currentDelivery.copy(
                        etapa = 5,
                        status = "Confirmado pelo hospital"
                    )
                    screen = Screen.Home
                },
                onOccurrence = { screen = Screen.Ocorrencia },
                onChat = { screen = Screen.Chat }
            )

            Screen.Chat -> HospitalChatScreen(
                onBack = { screen = Screen.Home },
                onOccurrence = { screen = Screen.Ocorrencia }
            )

            Screen.Ocorrencia -> OccurrenceScreen(
                onBack = { screen = Screen.Home },
                onSave = { tipo, detalhe ->
                    ocorrencias.add(Occurrence(tipo, detalhe))
                    screen = Screen.Home
                }
            )

            Screen.Perfil -> ProfileScreen {
                screen = Screen.Home
            }

            Screen.Historico -> HistoryScreen {
                screen = Screen.Home
            }
        }
    }
}

// ============================================================
// STATUS DA ENTREGA
// Cada número representa uma etapa do processo.
// ============================================================

private fun deliveryStatus(step: Int) = listOf(
    "Solicitação recebida",
    "A caminho do hospital",
    "Leite coletado",
    "Em trânsito",
    "Entregue",
    "Confirmado pelo hospital"
)[step.coerceIn(0, 5)]

// ============================================================
// TELA INICIAL / SPLASH SCREEN
// Alterações feitas:
// - Removido clique na tela inteira.
// - Fundo mais limpo para a logo aparecer melhor.
// - Apenas uma logo.
// - Botão elegante e discreto "Iniciar".
// ============================================================

@Composable
fun SplashScreen(onContinue: () -> Unit) {
    // ============================================================
    // PRIMEIRA TELA DO APP / TELA DE ABERTURA
    // Ajustes feitos:
    // - Removi o texto "App do motorista".
    // - Removi o quadrado/card atrás da logo.
    // - Mantive a logo livre, limpa e mais elegante.
    // - Mantive o visual premium com as cores lilás/roxo.
    // ============================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF8F3FF),
                        Color(0xFFEDE3FF),
                        Color(0xFFDCCDF8),
                        Color(0xFFB58CF0)
                    )
                )
            )
    ) {
        // Círculo decorativo superior.
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = (-95).dp, y = (-80).dp)
                .clip(CircleShape)
                .background(Purple.copy(alpha = 0.20f))
        )

        // Círculo decorativo inferior.
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 85.dp, y = 85.dp)
                .clip(CircleShape)
                .background(DarkPurple.copy(alpha = 0.16f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 26.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(18.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // LOGO SEM QUADRADO ATRÁS.
                // Agora a logo fica livre na tela, sem card branco.
                // Para trocar a logo, altere:
                // res/drawable/logo_laco_leite.png
                Image(
                    painter = painterResource(R.drawable.logo_laco_leite),
                    contentDescription = "Logo Laço de Leite",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(Modifier.height(22.dp))

                Text(
                    text = "Entregando cuidado,\nvida e esperança.",
                    color = DarkPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 29.sp,
                    lineHeight = 35.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Coletas, rotas e entregas de leite materno acompanhadas com segurança, carinho e responsabilidade.",
                    color = DeepPurple,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                }

                Spacer(Modifier.height(22.dp))

                Button(
                    onClick = onContinue,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkPurple
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                ) {
                    Text(
                        text = "Iniciar acesso",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Transporte humanizado de leite materno",
                    color = DarkPurple.copy(alpha = 0.82f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MiniSplashCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.72f)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = DarkPurple,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                color = Purple,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ============================================================
// TELA DE LOGIN
// ============================================================

@Composable
fun LoginScreen(
    onEnter: () -> Unit,
    onRegister: () -> Unit,
    onForgot: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    AppGradientBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(34.dp))

                // Logo sem fundo quadrado para manter o mesmo padrão da tela inicial.
                Image(
                    painter = painterResource(R.drawable.logo_laco_leite),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(135.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(Modifier.height(22.dp))
            }

            item {
                Card(
                    shape = RoundedCornerShape(34.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            "Acesso do motorista",
                            color = DarkPurple,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "Entre para acompanhar suas coletas e entregas.",
                            color = Purple,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )

                        Spacer(Modifier.height(22.dp))

                        AppTextField(
                            value = email,
                            onChange = { email = it },
                            label = "E-mail"
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = senha,
                            onValueChange = { senha = it },
                            label = { Text("Senha") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(4.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = onForgot) {
                                Text("Recuperar senha", color = Purple)
                            }

                            Text("🔐 seguro", color = Success, fontSize = 13.sp)
                        }

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = onEnter,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkPurple),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text(
                                "Entrar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        TextButton(
                            onClick = onRegister,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                "Criar cadastro de motorista",
                                color = DarkPurple,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "Transporte humanizado de leite materno",
                    color = DarkPurple.copy(alpha = 0.82f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

// ============================================================
// TELA DE CADASTRO
// ============================================================

@Composable
fun RegisterScreen(onBack: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var veiculo by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    AppGradientBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TopBack("Cadastro", onBack)

                Text(
                    text = "Dados do motorista",
                    color = DarkPurple,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Preencha os dados para liberação de acesso.",
                    color = DeepPurple,
                    fontSize = 14.sp
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppTextField(nome, { nome = it }, "Nome completo")
                        AppTextField(cpf, { cpf = it }, "CPF")
                        AppTextField(telefone, { telefone = it }, "Telefone")
                        AppTextField(email, { email = it }, "E-mail")
                        AppTextField(veiculo, { veiculo = it }, "Modelo do veículo")
                        AppTextField(placa, { placa = it }, "Placa")

                        OutlinedTextField(
                            value = senha,
                            onValueChange = { senha = it },
                            label = { Text("Senha") },
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        InfoBox("Depois, esta tela pode receber upload da CNH e foto do motorista.")

                        PrimaryButton("Finalizar cadastro", onBack)
                    }
                }
            }
        }
    }
}

// ============================================================
// TELA DE RECUPERAR SENHA
// ============================================================

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }

    AppGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TopBack("Recuperar senha", onBack)

            Spacer(Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Esqueceu sua senha?",
                        color = DarkPurple,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Informe seu e-mail cadastrado para receber o código de recuperação.",
                        color = Purple,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    AppTextField(email, { email = it }, "E-mail cadastrado")

                    PrimaryButton("Enviar código de recuperação", onBack)
                }
            }
        }
    }
}

// ============================================================
// DASHBOARD PRINCIPAL
// ============================================================

@Composable
fun HomeScreen(
    delivery: Delivery,
    finalizadas: Int,
    ocorrencias: Int,
    onDelivery: () -> Unit,
    onChat: () -> Unit,
    onOccurrence: () -> Unit,
    onProfile: () -> Unit,
    onHistory: () -> Unit
) {
    AppGradientBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Purple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "M",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            "Bom dia, motorista",
                            color = DarkPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )

                        Text(
                            "Você tem uma coleta em andamento",
                            color = DeepPurple
                        )
                    }

                    Text(
                        "☰",
                        color = DarkPurple,
                        fontSize = 28.sp,
                        modifier = Modifier.clickable { onProfile() }
                    )
                }
            }

            item { MissionCard(delivery, onDelivery) }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard("Hoje", "3", "entregas", Modifier.weight(1f))
                    StatCard("Finalizadas", "$finalizadas", "total", Modifier.weight(1f))
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard("Ocorrências", "$ocorrencias", "registradas", Modifier.weight(1f))
                    StatCard("Próxima coleta", "16:20", "hospital", Modifier.weight(1f))
                }
            }

            item { SectionTitle("Ações rápidas") }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuickAction("💬", "Chat hospital", onChat, Modifier.weight(1f))
                    QuickAction("⚠️", "Ocorrência", onOccurrence, Modifier.weight(1f))
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuickAction("📍", "Abrir entrega", onDelivery, Modifier.weight(1f))
                    QuickAction("📊", "Histórico", onHistory, Modifier.weight(1f))
                }
            }
        }
    }
}

// ============================================================
// CARD DA MISSÃO EM ANDAMENTO
// ============================================================

@Composable
fun MissionCard(
    delivery: Delivery,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DarkPurple),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "Missão em andamento",
                color = LilacLight,
                fontSize = 14.sp
            )

            Text(
                delivery.id,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "${delivery.hospital} → ${delivery.destino}",
                color = LilacLight
            )

            Spacer(Modifier.height(14.dp))

            DeliveryProgress(delivery.etapa)

            Spacer(Modifier.height(10.dp))

            Text(
                "Status: ${delivery.status}",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ============================================================
// TELA DO PROCESSO DA ENTREGA
// Alteração principal:
// O motorista agora clica diretamente nas bolinhas/etapas.
// Removi a necessidade do botão "Avançar etapa".
// ============================================================

@Composable
fun DeliveryProcessScreen(
    delivery: Delivery,
    onBack: () -> Unit,
    onStepChange: (Int) -> Unit,
    onFinish: () -> Unit,
    onOccurrence: () -> Unit,
    onChat: () -> Unit
) {
    val context = LocalContext.current

    var endereco by remember { mutableStateOf(delivery.destino) }
    var responsavel by remember { mutableStateOf("") }
    var obs by remember { mutableStateOf("") }

    // Essa variável controla qual bolinha/etapa está marcada.
    var etapa by remember { mutableIntStateOf(delivery.etapa) }

    val steps = listOf(
        "Solicitação",
        "Hospital",
        "Coleta",
        "Trânsito",
        "Entregue",
        "Confirmado"
    )

    AppGradientBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                TopBack("Processo da entrega", onBack)
            }

            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            delivery.id,
                            color = DarkPurple,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "Toque em cada etapa para atualizar o processo.",
                            color = Purple
                        )

                        Spacer(Modifier.height(14.dp))

                        DeliveryProgress(etapa)

                        Spacer(Modifier.height(10.dp))

                        // ETAPAS CLICÁVEIS:
                        // Aqui cada bolinha vira um botão.
                        // Quando o motorista clicar, atualiza a etapa e o status da entrega.
                        steps.forEachIndexed { index, title ->
                            ClickableStepLine(
                                selected = index <= etapa,
                                current = index == etapa,
                                title = title,
                                desc = deliveryStatus(index),
                                onClick = {
                                    etapa = index
                                    onStepChange(index)
                                }
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            "Rota da entrega",
                            color = DarkPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Field(endereco, { endereco = it }, "Endereço ou hospital de destino")

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { openGoogleMaps(context, endereco) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Google Maps")
                            }

                            OutlinedButton(
                                onClick = { openWaze(context, endereco) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Waze")
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            "Confirmação da entrega",
                            color = DarkPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Field(responsavel, { responsavel = it }, "Nome de quem recebeu")
                        Field(obs, { obs = it }, "Observação rápida")

                        PrimaryButton("Confirmar entrega", onFinish)
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onOccurrence,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ocorrência")
                    }

                    OutlinedButton(
                        onClick = onChat,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Falar com hospital")
                    }
                }
            }
        }
    }
}

// ============================================================
// CHAT COM HOSPITAL
// ============================================================

@Composable
fun HospitalChatScreen(
    onBack: () -> Unit,
    onOccurrence: () -> Unit
) {
    var msg by remember { mutableStateOf("") }

    val mensagens = remember {
        mutableStateListOf(
            "Hospital: confirme a coleta ao chegar.",
            "Motorista: estou a caminho do hospital."
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        TopBack("Chat com hospital", onBack)

        LazyColumn(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mensagens) { mensagem ->
                val mine = mensagem.startsWith("Motorista")

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (mine) Purple else Color.White
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth(0.82f)
                    ) {
                        Text(
                            mensagem,
                            color = if (mine) Color.White else DarkPurple,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = msg,
                onValueChange = { msg = it },
                label = { Text("Mensagem ou ocorrência") },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    if (msg.isNotBlank()) {
                        mensagens.add("Motorista: $msg")
                        msg = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Purple)
            ) {
                Text("Enviar")
            }
        }

        TextButton(
            onClick = onOccurrence,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Reportar ocorrência rápida", color = Danger)
        }
    }
}

// ============================================================
// TELA DE OCORRÊNCIA
// ============================================================

@Composable
fun OccurrenceScreen(
    onBack: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var tipo by remember { mutableStateOf("Atraso na rota") }
    var detalhe by remember { mutableStateOf("") }

    val tipos = listOf(
        "Atraso na rota",
        "Problema com veículo",
        "Dificuldade na coleta",
        "Dificuldade na entrega",
        "Leite danificado",
        "Emergência"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TopBack("Reportar ocorrência", onBack)
        }

        item {
            InfoBox("Use esta tela para avisar o hospital rapidamente caso algo aconteça durante a coleta ou entrega.")
        }

        items(tipos) { item ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (tipo == item) LilacLight else Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { tipo = item }
            ) {
                Text(
                    text = if (tipo == item) "✓ $item" else item,
                    color = DarkPurple,
                    modifier = Modifier.padding(14.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            Field(detalhe, { detalhe = it }, "Descreva o que aconteceu")

            Button(
                onClick = { onSave(tipo, detalhe) },
                colors = ButtonDefaults.buttonColors(containerColor = Danger),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Enviar ocorrência ao hospital")
            }
        }
    }
}

// ============================================================
// PERFIL DO MOTORISTA
// ============================================================

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    FormScaffold(
        title = "Perfil do motorista",
        subtitle = "Dados importantes para identificação"
    ) {
        Box(
            Modifier
                .size(86.dp)
                .clip(CircleShape)
                .background(Lilac),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "M",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        }

        InfoRow("Nome", "Motorista Laço de Leite")
        InfoRow("Telefone", "(11) 99999-0000")
        InfoRow("Veículo", "Moto / Carro cadastrado")
        InfoRow("Placa", "ABC-1234")
        InfoRow("Status", "Liberado para entregas")

        PrimaryButton("Voltar ao painel", onBack)
    }
}

// ============================================================
// HISTÓRICO DE ENTREGAS
// ============================================================

@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val historico = listOf(
        "LL-2045 • Entregue • 09:10",
        "LL-2046 • Entregue • 11:45",
        "LL-2047 • Confirmado • 13:20"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TopBack("Histórico de entregas", onBack)
        }

        items(historico) { item ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    item,
                    color = DarkPurple,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ============================================================
// COMPONENTES REUTILIZÁVEIS
// ============================================================

@Composable
fun AppGradientBackground(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF8F3FF),
                        Color(0xFFEDE3FF),
                        Color(0xFFDCCDF8)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(230.dp)
                .offset(x = (-80).dp, y = (-70).dp)
                .clip(CircleShape)
                .background(Purple.copy(alpha = 0.16f))
        )

        Box(
            modifier = Modifier
                .size(210.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 75.dp, y = 75.dp)
                .clip(CircleShape)
                .background(DarkPurple.copy(alpha = 0.12f))
        )

        content()
    }
}

@Composable
fun FormScaffold(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                title,
                color = DarkPurple,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                subtitle,
                color = Purple
            )
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content
            )
        }
    }
}

@Composable
fun Field(
    value: String,
    onChange: (String) -> Unit,
    label: String
) {
    AppTextField(value, onChange, label)
}

@Composable
fun AppTextField(
    value: String,
    onChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = false,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Purple),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(text)
    }
}

@Composable
fun InfoBox(text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LilacLight),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text,
            color = DarkPurple,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text,
        color = DarkPurple,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(86.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50.dp))
                .background(Purple.copy(alpha = 0.75f))
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title.uppercase(),
                color = DeepPurple,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp
            )

            Text(
                text = value,
                color = DarkPurple,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 31.sp
            )

            Text(
                text = subtitle,
                color = Purple,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun QuickAction(
    icon: String,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.90f)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 28.sp)

            Text(
                title,
                color = DarkPurple,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TopBack(
    title: String,
    onBack: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "‹",
            fontSize = 36.sp,
            color = DarkPurple,
            modifier = Modifier
                .clickable { onBack() }
                .padding(end = 8.dp)
        )

        Text(
            title,
            color = DarkPurple,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DeliveryProgress(step: Int) {
    LinearProgressIndicator(
        progress = { (step + 1) / 6f },
        color = Success,
        trackColor = LilacLight,
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(20.dp))
    )
}

// ============================================================
// ETAPA CLICÁVEL DA ENTREGA
// Essa é a parte nova.
// Cada bolinha é clicável e atualiza o processo.
// ============================================================

@Composable
fun ClickableStepLine(
    selected: Boolean,
    current: Boolean,
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp)
            .border(
                width = if (current) 1.5.dp else 0.dp,
                color = if (current) Purple else Color.Transparent,
                shape = RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (current) LilacLight else Color.White
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) Success else LilacLight
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selected) "✓" else "•",
                    color = if (selected) Color.White else Purple,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    color = DarkPurple,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    desc,
                    color = Purple,
                    fontSize = 12.sp
                )
            }

            if (current) {
                Text(
                    "Atual",
                    color = Purple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Mantive o StepLine antigo caso você queira usar em outro lugar depois.
@Composable
fun StepLine(
    done: Boolean,
    title: String,
    desc: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Box(
            Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (done) Success else LilacLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (done) "✓" else "•",
                color = if (done) Color.White else Purple,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.width(10.dp))

        Column {
            Text(
                title,
                color = DarkPurple,
                fontWeight = FontWeight.Bold
            )

            Text(
                desc,
                color = Purple,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.90f)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                label,
                color = Purple,
                fontSize = 12.sp
            )

            Text(
                value,
                color = DarkPurple,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ============================================================
// GOOGLE MAPS
// ============================================================

fun openGoogleMaps(
    context: Context,
    address: String
) {
    try {
        val uri = Uri.parse("google.navigation:q=" + Uri.encode(address))

        context.startActivity(
            Intent(Intent.ACTION_VIEW, uri)
                .setPackage("com.google.android.apps.maps")
        )
    } catch (_: Exception) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    "https://www.google.com/maps/search/?api=1&query=" +
                            Uri.encode(address)
                )
            )
        )
    }
}

// ============================================================
// WAZE
// ============================================================

fun openWaze(
    context: Context,
    address: String
) {
    context.startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                "https://waze.com/ul?q=" +
                        Uri.encode(address) +
                        "&navigate=yes"
            )
        )
    )
}