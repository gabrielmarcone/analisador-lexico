# Analisador Léxico — Mini_Pascal (Compiladores, UESB)

Trabalho da Unidade I. Java 8 + JavaFX embutido.

## Estado atual

- [x] Fase 3 — `model` (`Token`, `TipoToken`) e `tabela` (`TabelaPalavrasReservadas`)
- [x] Fase 4 — núcleo do lexer (`AnalisadorLexico`)
- [x] Fase 5 — comentários e erros léxicos
- [x] Fase 6 — leitura/escrita de arquivos (`io`, `Main`)
- [ ] Fase 7 — interface JavaFX
- [ ] Fase 8 — testes finais e documentação

## Como abrir no IntelliJ

1. `File > Open` e selecione a pasta `mini-pascal-lexico`
2. Quando a IDE perguntar sobre o SDK do projeto, aponte para um **JDK 8**
3. Marque a pasta `src` como *Sources Root* (botão direito > Mark Directory as > Sources Root)

## Como compilar (sem IDE)

```bash
javac --release 8 -encoding UTF-8 -d out $(find src -name "*.java")
```
No PowerShell, troque o `$(find ...)` por:
```powershell
javac --release 8 -encoding UTF-8 -d out (Get-ChildItem -Path src -Recurse -Filter *.java).FullName
```

## Como rodar os testes (ainda sem UI)

Cada fase tem uma classe de verificação própria, sem depender de JUnit — roda e imprime
`[OK]`/`[FALHA]` linha a linha, com um resumo no final:

```bash
java -cp out minipascal.lexico.verificacao.VerificacaoFase3
java -cp out minipascal.lexico.verificacao.VerificacaoFase4
java -cp out minipascal.lexico.verificacao.VerificacaoFase5
java -cp out minipascal.lexico.verificacao.VerificacaoFase6
```
Se algum teste falhar, o programa termina com código de saída 1 (útil pra script/CI depois).

## Como rodar o analisador de verdade contra um arquivo

```bash
java -cp out minipascal.lexico.Main testes-integracao/entrada/01_programa_simples.txt
```
Isso gera `testes-integracao/entrada/01_programa_simples_saida.txt` (mesmo nome + `_saida`).
Pra escolher o nome do arquivo de saída, passe como segundo argumento:
```bash
java -cp out minipascal.lexico.Main caminho/entrada.txt caminho/saida.txt
```
Os dois arquivos em `testes-integracao/entrada/` são os exemplos do próprio enunciado — a
saída do primeiro já foi conferida byte a byte contra o texto exato que o professor deu.

## Setup do Git (primeira vez)

```bash
cd mini-pascal-lexico
git init
git add .
git commit -m "chore: estrutura inicial do projeto (model + tabela)"
git branch -M main
git remote add origin <URL_DO_REPOSITORIO_NO_GITHUB>
git push -u origin main
```

A partir daqui, sigam o fluxo de branches descrito no documento de planejamento (seção 9).

## Nota sobre encoding

Os textos dos tokens têm acento (`"Número real"`, `"Atribuição"`...). `LeitorArquivoFonte` e
`EscritorArquivoSaida` já leem/escrevem sempre em UTF-8 explícito — não dependem do charset
default da plataforma.

## Próximo passo

Fase 7 — interface JavaFX (`gui/MainApp.java`, `gui/AnalisadorController.java`): tela com
botão de carregar arquivo, executar, exibir resultado e salvar saída, chamando a mesma
camada `io`/`core` que já existe hoje.