# Laço de Leite Driver App

Aplicativo Android do motorista do projeto Laço de Leite. Ele foi feito para simular o fluxo de transporte de leite materno entre hospital, coleta e entrega, com foco em organização da rota, confirmação de etapas, comunicação com o hospital e registro de ocorrências.

Este app é um protótipo funcional em Jetpack Compose. Os dados principais estão simulados no próprio aplicativo para facilitar apresentação, testes e evolução do projeto.

## O que o app faz

- Mostra uma tela inicial com a identidade visual do Laço de Leite.
- Permite acesso do motorista por uma tela de login demonstrativa.
- Possui cadastro de motorista com nome, CPF, telefone, e-mail, veículo, placa e senha.
- Exibe um painel principal com a entrega atual, total de entregas finalizadas e ocorrências registradas.
- Permite acompanhar uma entrega por etapas clicáveis.
- Abre rotas no Google Maps ou no Waze.
- Permite confirmar a entrega com nome do responsável e observação.
- Possui chat simples com o hospital.
- Permite registrar ocorrências como atraso, problema no veículo, dificuldade na coleta ou leite danificado.
- Mostra perfil do motorista.
- Mostra histórico demonstrativo de entregas.

## Como usar o app

1. Abra o aplicativo Laço de Leite.
2. Na tela inicial, toque em `Iniciar acesso`.
3. Na tela de login, toque em `Entrar` para acessar o painel do motorista.
4. Se quiser testar o cadastro, toque em `Criar cadastro de motorista`, preencha os dados e volte para o login.
5. No painel principal, toque na entrega atual para abrir o acompanhamento da rota.
6. Na tela da entrega, toque nas etapas para atualizar o progresso:
   - Solicitação recebida
   - A caminho do hospital
   - Leite coletado
   - Em trânsito
   - Entregue
   - Confirmado pelo hospital
7. Use os botões `Google Maps` ou `Waze` para abrir o endereço da rota em um app de navegação.
8. Ao finalizar, preencha a confirmação da entrega e toque em `Confirmar entrega`.
9. Use `Falar com hospital` para abrir o chat.
10. Use `Ocorrência` para avisar rapidamente quando houver atraso, problema no veículo ou outra situação importante.

## Como foi feito

O app foi desenvolvido como um projeto Android nativo usando:

- Kotlin
- Jetpack Compose
- Material 3
- Gradle
- Android Gradle Plugin 8.6.1
- Kotlin plugin 2.0.20
- Compose BOM 2024.10.00

A interface é construída com composables. A navegação é controlada por uma classe selada chamada `Screen`, e o estado principal fica na função `LacoLeiteDriverAppV2`.

O projeto usa uma única `MainActivity`, localizada em:

```text
app/src/main/java/com/example/lacoleitedriver/MainActivity.kt
```

Dentro desse arquivo ficam:

- modelos simples de dados, como `Delivery` e `Occurrence`;
- controle de telas;
- tela inicial;
- login;
- cadastro;
- recuperação de senha;
- painel principal;
- acompanhamento da entrega;
- chat;
- ocorrência;
- perfil;
- histórico;
- funções para abrir Google Maps e Waze.

## Estrutura do projeto

```text
LacoLeiteDriverApp/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/lacoleitedriver/MainActivity.kt
│       └── res/
│           ├── drawable/
│           │   ├── logo_laco_icon.png
│           │   └── logo_laco_leite.png
│           └── values/styles.xml
├── build.gradle
├── gradle.properties
├── settings.gradle
├── gradlew
└── gradlew.bat
```

## Dados e funcionamento atual

O app usa dados demonstrativos fixos para simular uma operação real. Por exemplo, a entrega atual começa com:

- código: `LL-2048`
- hospital: `Hospital Municipal`
- destino: `Banco de Leite Zona Sul`
- status inicial: `Leite coletado`
- horário: `14:30`

As ocorrências e mensagens do chat são mantidas em memória enquanto o app está aberto. Ao fechar o app, esses dados simulados podem ser reiniciados.

## Integração com mapas

O app possui duas funções para abrir navegação externa:

- `openGoogleMaps`
- `openWaze`

No `AndroidManifest.xml`, o app declara consulta para os pacotes:

```xml
<package android:name="com.google.android.apps.maps" />
<package android:name="com.waze" />
```

Se o Google Maps estiver instalado, a rota é aberta diretamente nele. Caso contrário, o app tenta abrir uma busca pelo navegador. O Waze é aberto por URL pública.

## Permissões

O app declara permissão de internet:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Essa permissão deixa o projeto preparado para futuras integrações com API, painel hospitalar, banco de dados ou serviço de mensagens.

## Requisitos para abrir e compilar

- Android Studio instalado.
- JDK 21.
- Android SDK com `compileSdk 35`.
- Emulador ou aparelho Android com Android 7.0 ou superior, pois o `minSdk` é 24.

Configuração principal do app:

```text
applicationId: com.example.lacoleitedriver
versionCode: 2
versionName: 2.0
minSdk: 24
targetSdk: 35
compileSdk: 35
```

## Como abrir no Android Studio

1. Abra o Android Studio.
2. Clique em `File > Open`.
3. Escolha a pasta:

```text
C:\Users\Administrator\Desktop\LacoLeiteDriverApp
```

4. Aguarde o Gradle sincronizar.
5. Escolha um emulador ou conecte um celular Android.
6. Clique em `Run`.

## Como compilar pelo terminal

No Windows, dentro da pasta do projeto:

```powershell
.\gradlew.bat assembleDebug
```

O APK debug será gerado em:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Pontos importantes para apresentação

Este aplicativo representa a visão do motorista dentro do ecossistema Laço de Leite. Enquanto o app das mães cuida de doação, solicitação e acompanhamento, este app cuida da operação logística:

- quem é o motorista;
- qual rota está em andamento;
- em qual etapa a entrega está;
- como abrir navegação;
- como falar com o hospital;
- como reportar problemas;
- como confirmar a entrega.

Ele ajuda a mostrar como o transporte do leite materno pode ser acompanhado com mais segurança, registro e comunicação.

## Melhorias futuras

Algumas melhorias naturais para uma próxima versão:

- Login real com autenticação.
- Cadastro persistente do motorista.
- Integração com banco de dados de motoristas.
- Listagem real de rotas atribuídas pelo hospital.
- Sincronização de status da entrega com uma API.
- Envio real de mensagens no chat.
- Upload de CNH e foto do motorista.
- Geolocalização em tempo real.
- Notificações para novas coletas.
- Histórico real de entregas concluídas.

## Resumo

O Laço de Leite Driver App é a parte logística do projeto Laço de Leite. Ele demonstra como o motorista pode receber uma entrega, acompanhar cada etapa, abrir a rota, conversar com o hospital, registrar ocorrências e confirmar a entrega do leite materno.
