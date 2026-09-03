package minipascal.lexico.verificacao;

import minipascal.lexico.core.AnalisadorLexico;
import minipascal.lexico.model.Token;
import minipascal.lexico.model.TipoToken;
import minipascal.lexico.tabela.TabelaPalavrasReservadas;

import java.util.ArrayList;
import java.util.List;

/**
 * Testes de estresse: entradas maldosas, mal formadas ou no limite do que
 * a linguagem permite. O objetivo não é validar o "caminho feliz" (isso já
 * é coberto pelas Fases 3-6), e sim tentar quebrar o analisador de propósito.
 */
public class VerificacaoAbsurda {

    private static final TabelaPalavrasReservadas TABELA = new TabelaPalavrasReservadas();

    private static int total = 0;
    private static int falhas = 0;

    public static void main(String[] args) {
        checarNumeroColadoEmIdentificador();
        checarPalavraReservadaEscritaErrada();
        checarPalavraReservadaComoPrefixoDeIdentificador();
        checarCaixaMistaEmPalavraReservada();
        checarPontosMultiplos();
        checarSequenciaDeErros();
        checarArquivoVazio();
        checarSoComentarioOuEspaco();
        checarComentarioAninhado();
        checarOperadoresColadosSemEspaco();
        checarRelacionaisEncadeados();
        checarNumeroComPontoNoInicio();
        checarLetraEIsoladaNaoViraExpoente();
        checarStringComAspaSimplesDentro();
        checarIdentificadorComUnderscoreNoMeio();
        checarQuebraDeLinhaWindowsCRLF();
        checarTabsEEspacosMisturados();
        checarPalavrasConcatenadasSemEspaco();
        checarUnicoCaractereInvalido();
        checarSinalDuploNaoExiste();
        checarStringNaoFechadaNoMeioDoArquivo();
        checarCharVazioEhErro();
        checarCharComMaisDeUmCaractereEhErro();

        System.out.println();
        System.out.println(total + " verificações absurdas, " + falhas + " falha(s).");
        if (falhas > 0) {
            System.exit(1);
        }
    }

    // "123abc" — número colado direto num identificador, sem espaço.
    // Comportamento correto: dois tokens separados, não um "número inválido".
    private static void checarNumeroColadoEmIdentificador() {
        List<Token> tokens = tokenizarTudo("123abc");
        checar("'123abc' vira DOIS tokens: '123' (inteiro) + 'abc' (identificador)",
                tokens.size() == 2
                        && tokens.get(0).getLexema().equals("123") && tokens.get(0).getTipo() == TipoToken.NUMERO_INTEIRO
                        && tokens.get(1).getLexema().equals("abc") && tokens.get(1).getTipo() == TipoToken.IDENTIFICADOR);
    }

    // Palavra reservada com um caractere a mais/a menos deve ser Identificador.
    private static void checarPalavraReservadaEscritaErrada() {
        checarTokenUnico("beginn", TipoToken.IDENTIFICADOR);
        checarTokenUnico("progrm", TipoToken.IDENTIFICADOR);
        checarTokenUnico("integr", TipoToken.IDENTIFICADOR);
        checarTokenUnico("edn", TipoToken.IDENTIFICADOR);
    }

    // "vars", "integer2" etc — contêm uma palavra reservada como prefixo,
    // mas são identificadores válidos e diferentes (match tem que ser exato).
    private static void checarPalavraReservadaComoPrefixoDeIdentificador() {
        checarTokenUnico("vars", TipoToken.IDENTIFICADOR);
        checarTokenUnico("integer2", TipoToken.IDENTIFICADOR);
        checarTokenUnico("beginner", TipoToken.IDENTIFICADOR);
        checarTokenUnico("endereco", TipoToken.IDENTIFICADOR);
    }

    // A busca já era case-insensitive (testado na Fase 3/4), mas com
    // maiúsculas/minúsculas ALTERNADAS dentro da mesma palavra — caso mais
    // extremo de mistura de caixa.
    private static void checarCaixaMistaEmPalavraReservada() {
        checarTokenUnico("PrOgRaM", TipoToken.PALAVRA_RESERVADA);
        checarTokenUnico("BeGiN", TipoToken.PALAVRA_RESERVADA);
    }

    // "1.2.3" — dois pontos decimais. Segundo ponto não tem dígito de sobra
    // pra formar outro real junto ao primeiro, então vira Fim isolado.
    private static void checarPontosMultiplos() {
        List<Token> tokens = tokenizarTudo("1.2.3");
        checar("'1.2.3' vira '1.2' (real) + '.' (Fim) + '3' (inteiro), não trava",
                tokens.size() == 3
                        && tokens.get(0).getLexema().equals("1.2") && tokens.get(0).getTipo() == TipoToken.NUMERO_REAL
                        && tokens.get(1).getLexema().equals(".") && tokens.get(1).getTipo() == TipoToken.FIM
                        && tokens.get(2).getLexema().equals("3") && tokens.get(2).getTipo() == TipoToken.NUMERO_INTEIRO);
    }

    // Vários caracteres inválidos em sequência, sem nada válido entre eles.
    private static void checarSequenciaDeErros() {
        List<Token> tokens = tokenizarTudo("@#$%&");
        boolean todosErro = tokens.size() == 5;
        for (Token t : tokens) {
            todosErro = todosErro && t.getTipo() == TipoToken.ERRO_LEXICO;
        }
        checar("'@#$%&' gera 5 tokens de Erro lexico em sequência, sem travar",
                todosErro);
    }

    private static void checarArquivoVazio() {
        AnalisadorLexico lexer = new AnalisadorLexico("", TABELA);
        checar("arquivo vazio retorna null direto, sem exceção",
                lexer.proximoToken() == null);
    }

    private static void checarSoComentarioOuEspaco() {
        List<Token> tokens = tokenizarTudo("   \n\t /* só isso aqui */   \n  ");
        checar("arquivo só com espaço/comentário gera zero tokens",
                tokens.isEmpty());
    }

    // Pascal não suporta comentário aninhado — /* dentro de /* fecha no
    // PRIMEIRO "*/", sobra "*/" como texto solto (dois operadores '*' e '/').
    private static void checarComentarioAninhado() {
        List<Token> tokens = tokenizarTudo("/* externo /* interno */ */");
        checar("comentário 'aninhado' fecha no primeiro '*/', sobra '*' e '/' soltos",
                tokens.size() == 2
                        && tokens.get(0).getLexema().equals("*") && tokens.get(0).getTipo() == TipoToken.OPERADOR_ARITMETICO
                        && tokens.get(1).getLexema().equals("/") && tokens.get(1).getTipo() == TipoToken.OPERADOR_ARITMETICO);
    }

    // Expressão inteira sem nenhum espaço em lugar nenhum.
    private static void checarOperadoresColadosSemEspaco() {
        List<Token> tokens = tokenizarTudo("x:=1+2*3-4/5;");
        String[] esperado = {"x", ":=", "1", "+", "2", "*", "3", "-", "4", "/", "5", ";"};
        checar("'x:=1+2*3-4/5;' sem espaços gera os 12 tokens certos, na ordem",
                mesmosLexemas(tokens, esperado));
    }

    private static void checarRelacionaisEncadeados() {
        List<Token> tokens = tokenizarTudo("a<=b<>c>=d=e");
        String[] esperado = {"a", "<=", "b", "<>", "c", ">=", "d", "=", "e"};
        checar("operadores relacionais encadeados sem espaço são todos reconhecidos certos",
                mesmosLexemas(tokens, esperado));
    }

    // ".5" — sem dígito antes do ponto. A linguagem não prevê esse formato
    // (todos os exemplos do enunciado têm dígito antes do ponto).
    private static void checarNumeroComPontoNoInicio() {
        List<Token> tokens = tokenizarTudo(".5");
        checar("'.5' vira '.' (Fim) + '5' (inteiro), não um real começando em ponto",
                tokens.size() == 2
                        && tokens.get(0).getTipo() == TipoToken.FIM
                        && tokens.get(1).getLexema().equals("5") && tokens.get(1).getTipo() == TipoToken.NUMERO_INTEIRO);
    }

    // Um 'e' sozinho (fora de um número) é só um identificador de uma letra,
    // não deve ser confundido com marcador de expoente.
    private static void checarLetraEIsoladaNaoViraExpoente() {
        checarTokenUnico("e", TipoToken.IDENTIFICADOR);
        List<Token> tokens = tokenizarTudo("x := e;");
        checar("'e' usado como variável comum continua Identificador",
                tokens.get(2).getLexema().equals("e") && tokens.get(2).getTipo() == TipoToken.IDENTIFICADOR);
    }

    private static void checarStringComAspaSimplesDentro() {
        checarTokenUnico("\"it's a test\"", TipoToken.CONSTANTE_STRING);
    }

    // Identificador tem que COMEÇAR com letra (igual ao Pascal clássico) —
    // underscore só é permitido no meio, como em "Media_das_medias". Um
    // underscore como primeiro caractere não é uma letra, então deve ser
    // rejeitado como Erro lexico, separado do resto que vem depois.
    private static void checarIdentificadorComUnderscoreNoMeio() {
        checarTokenUnico("Media_das_medias", TipoToken.IDENTIFICADOR);

        List<Token> tokens = tokenizarTudo("_comecaComUnderscore");
        checar("identificador NÃO pode começar com '_' — vira Erro lexico + Identificador separados",
                tokens.size() == 2
                        && tokens.get(0).getLexema().equals("_") && tokens.get(0).getTipo() == TipoToken.ERRO_LEXICO
                        && tokens.get(1).getLexema().equals("comecaComUnderscore") && tokens.get(1).getTipo() == TipoToken.IDENTIFICADOR);
    }

    // Arquivos vindos do Windows costumam ter \r\n, não só \n.
    private static void checarQuebraDeLinhaWindowsCRLF() {
        List<Token> tokens = tokenizarTudo("program x;\r\nbegin\r\nend.");
        checar("quebra de linha CRLF (Windows) não gera token nem erro estranho",
                tokens.size() == 6
                        && tokens.stream().noneMatch(t -> t.getTipo() == TipoToken.ERRO_LEXICO));
        checar("CRLF conta como 1 linha (não 2) — 'begin' deve estar na linha 2",
                tokens.get(3).getLexema().equals("begin") && tokens.get(3).getLinha() == 2);
    }

    private static void checarTabsEEspacosMisturados() {
        List<Token> tokens = tokenizarTudo("program\t\t x  ;\t\tbegin\t end .");
        checar("tabs e espaços em qualquer quantidade são todos ignorados igual",
                tokens.size() == 6 && tokens.stream().noneMatch(t -> t.getTipo() == TipoToken.ERRO_LEXICO));
    }

    // Duas palavras reservadas grudadas viram UM identificador só (esperado:
    // sem separador não tem como saber que eram duas palavras diferentes).
    private static void checarPalavrasConcatenadasSemEspaco() {
        checarTokenUnico("beginend", TipoToken.IDENTIFICADOR);
    }

    private static void checarUnicoCaractereInvalido() {
        checarTokenUnico("§", TipoToken.ERRO_LEXICO);
        checarTokenUnico("€", TipoToken.ERRO_LEXICO);
    }

    // "!=" não existe na linguagem (o certo é "<>"). Vira dois tokens: erro + '='.
    private static void checarSinalDuploNaoExiste() {
        List<Token> tokens = tokenizarTudo("!=");
        checar("'!=' (não existe na linguagem) vira erro em '!' + '=' relacional",
                tokens.size() == 2
                        && tokens.get(0).getLexema().equals("!") && tokens.get(0).getTipo() == TipoToken.ERRO_LEXICO
                        && tokens.get(1).getLexema().equals("=") && tokens.get(1).getTipo() == TipoToken.OPERADOR_RELACIONAL);
    }

    // String que nunca fecha NO MEIO do arquivo (não no final) — o scanner
    // deve parar a string na quebra de linha e CONTINUAR tokenizando o
    // resto do arquivo depois, não engolir tudo até o fim.
    private static void checarStringNaoFechadaNoMeioDoArquivo() {
        String programa = "write(\"esqueci de fechar);\nx := 1;\nend.";
        List<Token> tokens = tokenizarTudo(programa);

        boolean temErroDaString = tokens.stream().anyMatch(t -> t.getTipo() == TipoToken.ERRO_LEXICO);
        boolean recuperouRestoDoArquivo = tokens.stream().anyMatch(t -> t.getLexema().equals("end") && t.getTipo() == TipoToken.PALAVRA_RESERVADA);

        checar("string não fechada no meio do arquivo gera Erro lexico E o scanner recupera o resto do arquivo depois",
                temErroDaString && recuperouRestoDoArquivo);
    }

    private static void checarCharVazioEhErro() {
        checarTokenUnico("'a'", TipoToken.CONSTANTE_CHAR); // caso válido continua ok
        List<Token> tokens = tokenizarTudo("''");
        checar("char vazio '' deve ser rejeitado (não pode ter 0 caracteres)",
                tokens.stream().noneMatch(t -> t.getTipo() == TipoToken.CONSTANTE_CHAR));
    }

    private static void checarCharComMaisDeUmCaractereEhErro() {
        List<Token> tokens = tokenizarTudo("'ab'");
        checar("char com 2+ caracteres 'ab' deve ser rejeitado (só aceita exatamente 1)",
                tokens.stream().noneMatch(t -> t.getTipo() == TipoToken.CONSTANTE_CHAR));
    }

    // ---------- utilitários ----------

    private static List<Token> tokenizarTudo(String fonte) {
        AnalisadorLexico lexer = new AnalisadorLexico(fonte, TABELA);
        List<Token> tokens = new ArrayList<>();
        Token t;
        while ((t = lexer.proximoToken()) != null) {
            tokens.add(t);
        }
        return tokens;
    }

    private static void checarTokenUnico(String entrada, TipoToken tipoEsperado) {
        List<Token> tokens = tokenizarTudo(entrada);
        boolean ok = tokens.size() == 1 && tokens.get(0).getTipo() == tipoEsperado;
        checar("'" + entrada + "' -> token único do tipo " + tipoEsperado, ok);
    }

    private static boolean mesmosLexemas(List<Token> tokens, String[] esperados) {
        if (tokens.size() != esperados.length) {
            return false;
        }
        for (int i = 0; i < esperados.length; i++) {
            if (!tokens.get(i).getLexema().equals(esperados[i])) {
                return false;
            }
        }
        return true;
    }

    private static void checar(String descricao, boolean condicao) {
        total++;
        if (condicao) {
            System.out.println("[OK]    " + descricao);
        } else {
            falhas++;
            System.out.println("[FALHA] " + descricao);
        }
    }
}