package minipascal.lexico.verificacao;

import minipascal.lexico.core.AnalisadorLexico;
import minipascal.lexico.model.Token;
import minipascal.lexico.model.TipoToken;
import minipascal.lexico.tabela.TabelaPalavrasReservadas;

/**
 * Foca em comentários e erros léxicos: casos que a Fase 4 já cobria em
 * parte, mais os casos de borda que faltavam (comentário sem fechamento,
 * string/char sem fechamento, sequência de erros seguida de código válido).
 */
public class VerificacaoFase5 {

    private static final TabelaPalavrasReservadas TABELA = new TabelaPalavrasReservadas();

    private static int total = 0;
    private static int falhas = 0;

    public static void main(String[] args) {
        checarComentarioNaoFechado();
        checarComentarioFechadoNormalmenteAindaFunciona();
        checarStringNaoFechada();
        checarCharNaoFechado();
        checarErroNaoTravaOScanner();
        checarLinhaDoErroDeComentario();

        System.out.println();
        System.out.println(total + " verificações, " + falhas + " falha(s).");
        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void checarComentarioNaoFechado() {
        AnalisadorLexico lexer = new AnalisadorLexico("x\n/* comentario sem fechar\ny", TABELA);
        lexer.proximoToken(); // "x"
        Token erro = lexer.proximoToken();
        checar("comentário sem '*/' deve gerar Erro lexico",
                erro != null && erro.getTipo() == TipoToken.ERRO_LEXICO);
        checar("depois do erro de comentário, não sobra mais nada (EOF)",
                lexer.proximoToken() == null);
    }

    private static void checarComentarioFechadoNormalmenteAindaFunciona() {
        AnalisadorLexico lexer = new AnalisadorLexico("/* ok */ x", TABELA);
        Token t = lexer.proximoToken();
        checar("comentário fechado normalmente continua sem gerar token",
                t != null && t.getLexema().equals("x"));
    }

    private static void checarStringNaoFechada() {
        AnalisadorLexico lexer = new AnalisadorLexico("\"abc", TABELA);
        Token t = lexer.proximoToken();
        checar("string sem aspas de fechamento deve gerar Erro lexico",
                t != null && t.getTipo() == TipoToken.ERRO_LEXICO);
    }

    private static void checarCharNaoFechado() {
        AnalisadorLexico lexer = new AnalisadorLexico("'a", TABELA);
        Token t = lexer.proximoToken();
        checar("char sem aspa de fechamento deve gerar Erro lexico",
                t != null && t.getTipo() == TipoToken.ERRO_LEXICO);
    }

    private static void checarErroNaoTravaOScanner() {
        AnalisadorLexico lexer = new AnalisadorLexico("@ # x", TABELA);
        Token t1 = lexer.proximoToken();
        Token t2 = lexer.proximoToken();
        Token t3 = lexer.proximoToken();
        checar("primeiro caractere inválido gera Erro lexico",
                t1 != null && t1.getTipo() == TipoToken.ERRO_LEXICO);
        checar("segundo caractere inválido também gera Erro lexico",
                t2 != null && t2.getTipo() == TipoToken.ERRO_LEXICO);
        checar("scanner continua e reconhece o identificador normal depois",
                t3 != null && t3.getLexema().equals("x") && t3.getTipo() == TipoToken.IDENTIFICADOR);
    }

    private static void checarLinhaDoErroDeComentario() {
        AnalisadorLexico lexer = new AnalisadorLexico("a\nb\n/* nunca fecha", TABELA);
        lexer.proximoToken();
        lexer.proximoToken();
        Token erro = lexer.proximoToken();
        checar("erro de comentário reporta a linha onde o '/*' começou (linha 3)",
                erro != null && erro.getLinha() == 3);
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