# Analisador Léxico — Mini_Pascal (Compiladores, UESB)

Trabalho da Unidade I. Java 8 + JavaFX embutido.

## Estado atual

- [x] Fase 3 — `model` (`Token`, `TipoToken`) e `tabela` (`TabelaPalavrasReservadas`)
- [ ] Fase 4 — núcleo do lexer (`AnalisadorLexico`)
- [ ] Fase 5 — comentários e erros léxicos
- [ ] Fase 6 — leitura/escrita de arquivos
- [ ] Fase 7 — interface JavaFX
- [ ] Fase 8 — testes finais e documentação

## Como abrir no IntelliJ

1. `File > Open` e selecione a pasta `mini-pascal-lexico`
2. Quando a IDE perguntar sobre o SDK do projeto, aponte para um **JDK 8**
3. Marque a pasta `src` como *Sources Root* (botão direito > Mark Directory as > Sources Root)

## Como compilar/rodar pelo terminal (sem IDE)

```bash
javac --release 8 -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out minipascal.lexico.verificacao.VerificacaoFase3
```

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

A partir daqui, sigam o fluxo de branches descrito no documento de planejamento (seção 9):
`git checkout -b feature/04-lexer-core` para a próxima fase.

## Nota sobre encoding

Os textos dos tokens têm acento (`"Número real"`, `"Atribuição"`...). Ao implementar a Fase 6
(I/O), leiam e escrevam os arquivos **sempre em UTF-8 explícito** — nunca dependam do charset
default da plataforma:

```java
new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8);
new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8);
```

## Próximo passo

Implementar `core/AnalisadorLexico.java` (Fase 4), usando `TabelaPalavrasReservadas` para
desambiguar identificador/palavra reservada/mod/and/or/not, seguindo os estados do AFD
documentado na Atividade 1.
