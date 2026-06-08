# Changelog

## v2.0.0 - Redesign iOS + Reestruturação Business

### Novidades
- **Redesign completo** do sistema de design: paleta iOS (Azul #0058BC / Verde #006E28), tipografia Inter, cantos arredondados 12dp, sombras suaves
- **Novas abas no app Business**: Pessoal, Business, +, Resumo, Definições
- **Tela Business** com blocos organizados: Clientes, Vendas, Dívidas, Produtos, Agenda, Módulos, Relatórios
- **Gráfico de fluxo financeiro** na tela Business
- **Tela de Módulos Ativáveis**: ative/desative módulos por switch
- **Tela de Agenda** (placeholder)
- **Clientes aprimorados**: mostra nome, telefone, valor da dívida, botões WhatsApp, Ligar e Eliminar

### Mudanças
- Paleta de cores totalmente renovada (iOS System Blue/Green/Red)
- Tipografia baseada em Inter com escala Apple Dynamic Type
- Cards: 12dp de borda arredondada com elevação sutil (0.5dp) em vez de bordas
- Ícones containers: RoundedCornerShape(8dp) 36dp em vez de CircleShape 40dp
- Navigation bar estilo iOS com indicador pill e FAB central
- Todas as telas do Pro (Home, Histórico, Metas, Gráficos, Perfil, etc.) atualizadas
- Margens padronizadas em 16dp
- FontWeight Bold → SemiBold em toda a interface
- Cores fixas de categorias → cores do tema (secondaryContainer, errorContainer, primaryContainer)

### Correções
- Removidas referências a tipografia personalizada inexistente (footnote, callout, caption)
- Corrigido uso de MaterialTheme.colorScheme dentro de Canvas
- Corrigido parâmetro shadowElevation não encontrado no NavigationBar
