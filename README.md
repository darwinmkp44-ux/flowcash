# 📱 FlowCash — Finanças Pessoais de Alta Performance

<div align="center">
  <img alt="Banner FlowCash" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" width="100%" style="border-radius: 16px;" />
  <p><i>Gerenciador financeiro pessoal moderno, minimalista e 100% dinâmico desenvolvido em Android Nativo com Jetpack Compose.</i></p>
</div>

---

## 🚀 Sobre o Projeto
O **FlowCash** é um aplicativo nativo para Android projetado para fornecer uma experiência de gestão financeira fluida, bonita e intuitiva. Ele foi construído seguindo os mais recentes padrões de design e engenharia de software Android, oferecendo relatórios em tempo real, controle de metas de poupança e histórico detalhado de transações.

Nesta versão premium, **todos os dados mockados foram removidos**, dando lugar a uma base de dados local dinâmica baseada no **Room (SQLite)** e preferências do usuário persistidas via **SharedPreferences**.

---

## ✨ Funcionalidades Principais
- **💳 Saldo e Fluxo de Caixa Reais:** Controle de entradas (Receitas) e saídas (Despesas) atualizados instantaneamente à medida que o usuário adiciona transações.
- **📈 Gráfico de Evolução 30 dias:** Gráfico de linha desenhado dinamicamente via Compose Canvas, ilustrando a oscilação do saldo nos últimos 30 dias.
- **📊 Estatísticas & Categorias:** Gráfico de rosca dinâmico dividindo gastos por categorias (`Compras`, `Alimentação`, `Transporte`, `Outros`) e comparador de barras mensal dos últimos 6 meses.
- **🎯 Metas de Poupança (Goals):** Defina objetivos financeiros, acompanhe o progresso percentual e adicione economias diretamente para cada meta.
- **👤 Perfil Editável:** Gerenciamento do perfil (Nome e Email) editável diretamente pela interface e salvo de forma persistente através das `SharedPreferences`.
- **🌙 Suporte a Tema Escuro:** Alternância de temas claro/escuro de forma nativa e integrada às configurações do sistema.

---

## 🛠️ Arquitetura e Tecnologias
O projeto adota os padrões recomendados pelo Google para arquitetura de software Android (MAD - Modern Android Development):
- **UI:** Jetpack Compose (Kotlin DSL), utilizando Material Design 3 e animações fluidas.
- **Banco de Dados:** [Room Database](https://developer.android.com/training/data-storage/room) para persistência local robusta com suporte a coroutines e Flow.
- **Gerenciamento de Estado:** ViewModel unificado com `StateFlow` e `combine` do Kotlin Flow para processamento reativo de dados.
- **Assinatura Dinâmica:** Configuração no Gradle preparada para assinar o app em ambiente local ou CI/CD de forma flexível.
- **Testes de UI:** [Robolectric](https://robolectric.org/) combinados com [Roborazzi](https://github.com/takahirom/roborazzi) para testes rápidos de screenshot no pipeline.

---

## 📦 Como Compilar Sem o Android Studio (CLI)

Se você não tem o Android Studio instalado ou prefere compilar utilizando a linha de comando, é possível fazê-lo de duas formas:

### Opção 1: Usando Gradle instalado na máquina
Se você tiver o Java JDK 21 instalado, pode rodar o Gradle diretamente para compilar os APKs (o pipeline de CI/CD usa esse mesmo método):
```bash
# Compilar APK de desenvolvimento (Debug)
gradle assembleDebug

# Compilar APK de produção (Release)
gradle assembleRelease
```
Os APKs gerados estarão disponíveis em:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

### Opção 2: Compilar usando Docker (Sem instalar SDK/Gradle localmente)
Caso sua máquina não tenha capacidade ou ferramentas Android instaladas, você pode delegar a compilação para um contêiner Docker oficial com todas as ferramentas pré-instaladas:
```bash
docker run --rm -v "$PWD":/home/developer/app -w /home/developer/app runmymind/docker-android-sdk:latest ./gradlew assembleDebug
```

---

## 🚀 CI/CD & Deploy Automático no GitHub Actions

Configuramos um fluxo automatizado de Integração e Entrega Contínua (CI/CD) no GitHub Actions que elimina a necessidade de compilar os arquivos localmente:

1. **Geração Automática de Keystore:** O pipeline cria uma chave de assinatura de upload temporária (`my-upload-key.jks`) dinamicamente no runner.
2. **Execução de Testes:** Executa todos os testes unitários e de UI a cada commit.
3. **Geração de Artefato:** A cada push na branch principal (`main` ou `master`), o APK Release é compilado e anexado aos detalhes do fluxo de execução no GitHub.
4. **Deploy Automático de Releases:** Sempre que uma tag de versão for criada (ex: `v1.0.0`), o GitHub Actions compilará a versão de Release e publicará automaticamente uma **GitHub Release** pública com o APK em anexo pronto para instalação.

### Como acionar um Deploy de Release:
Para gerar uma nova Release oficial no GitHub com o APK anexo, basta criar e enviar uma tag git:
```bash
git tag v1.0.0
git push origin v1.0.0
```

---

## 📁 Estrutura do Código
```
├── .github/workflows/      # Configuração do Pipeline CI/CD (GitHub Actions)
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt        # Ponto de entrada e rotas de abas
│   │   │   │   ├── data/                  # Entidades, DAOs e Repositório Room
│   │   │   │   └── ui/                    # Telas Compose e ViewModel central
│   │   │   └── AndroidManifest.xml
│   │   └── test/                          # Testes unitários e Roborazzi
│   └── build.gradle.kts                   # Configuração de dependências e build do App
├── gradle/
│   └── libs.versions.toml                 # Catálogo centralizado de versões
├── build.gradle.kts                       # Script do Gradle root
└── settings.gradle.kts                    # Configurações do Gradle root
```
