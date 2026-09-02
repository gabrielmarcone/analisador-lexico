package minipascal.lexico.core;

import minipascal.lexico.model.Token;
import minipascal.lexico.model.TipoToken;
import minipascal.lexico.tabela.TabelaPalavrasReservadas;

/**
 * Lê uma cadeia de caracteres e devolve, a cada chamada de proximoToken(),
 * o próximo par lexema/token. Retorna null quando o código-fonte acaba.
 *
 * Espaços, tabs, quebras de linha e comentários de bloco são consumidos
 * sem gerar token, antes de cada chamada tentar reconhecer algo.
 */
public class AnalisadorLexico {

    private static final int TAMANHO_MAXIMO_IDENTIFICADOR = 63;

    private final String fonte;
    private final TabelaPalavrasReservadas palavrasReservadas;
    private int pos;
    private int linha;
    private boolean comentarioNaoFechado;
    private int linhaComentarioNaoFechado;

    public AnalisadorLexico(String fonte, TabelaPalavrasReservadas palavrasReservadas) {
        this.fonte = fonte;
        this.palavrasReservadas = palavrasReservadas;
        this.pos = 0;
        this.linha = 1;
    }

    public Token proximoToken() {
        comentarioNaoFechado = false;
        pularNaoSignificativos();

        if (comentarioNaoFechado) {
            return new Token("/*", TipoToken.ERRO_LEXICO, linhaComentarioNaoFechado);
        }

        if (pos >= fonte.length()) {
            return null;
        }

        char c = fonte.charAt(pos);
        int linhaToken = linha;

        if (Character.isLetter(c)) {
            return lerIdentificadorOuPalavra(linhaToken);
        }
        if (Character.isDigit(c)) {
            return lerNumero(linhaToken);
        }
        if (c == '"') {
            return lerString(linhaToken);
        }
        if (c == '\'') {
            return lerChar(linhaToken);
        }

        switch (c) {
            case '+':
                pos++;
                return new Token("+", TipoToken.OPERADOR_ARITMETICO, linhaToken);
            case '-':
                pos++;
                return new Token("-", TipoToken.OPERADOR_ARITMETICO, linhaToken);
            case '*':
                pos++;
                return new Token("*", TipoToken.OPERADOR_ARITMETICO, linhaToken);
            case '/':
                pos++;
                return new Token("/", TipoToken.OPERADOR_ARITMETICO, linhaToken);
            case '=':
                pos++;
                return new Token("=", TipoToken.OPERADOR_RELACIONAL, linhaToken);
            case '>':
                return lerMaior(linhaToken);
            case '<':
                return lerMenor(linhaToken);
            case '(':
                pos++;
                return new Token("(", TipoToken.SIMBOLO_ESPECIAL, linhaToken);
            case ')':
                pos++;
                return new Token(")", TipoToken.SIMBOLO_ESPECIAL, linhaToken);
            case ',':
                pos++;
                return new Token(",", TipoToken.SIMBOLO_ESPECIAL, linhaToken);
            case ';':
                pos++;
                return new Token(";", TipoToken.SIMBOLO_ESPECIAL, linhaToken);
            case ':':
                return lerDoisPontos(linhaToken);
            case '.':
                pos++;
                return new Token(".", TipoToken.FIM, linhaToken);
            default:
                pos++;
                return new Token(String.valueOf(c), TipoToken.ERRO_LEXICO, linhaToken);
        }
    }

    // Consome espaço/tab/enter e comentários de bloco. Se cair num '/' que
    // não é seguido de '*', para aqui e devolve pro switch tratar como
    // divisão - é o mesmo prefixo comum resolvido com 1 caractere de lookahead.
    private void pularNaoSignificativos() {
        while (pos < fonte.length()) {
            char c = fonte.charAt(pos);

            if (c == '\n') {
                linha++;
                pos++;
            } else if (Character.isWhitespace(c)) {
                pos++;
            } else if (c == '/' && caractereSeguinteEh('*')) {
                int linhaAbertura = linha;
                if (!pularComentario()) {
                    comentarioNaoFechado = true;
                    linhaComentarioNaoFechado = linhaAbertura;
                    return;
                }
            } else {
                break;
            }
        }
    }

    // Retorna false se chegar ao fim do arquivo sem encontrar o "*/" de fechamento.
    private boolean pularComentario() {
        pos += 2; // "/*"
        while (pos < fonte.length() && !(fonte.charAt(pos) == '*' && caractereSeguinteEh('/'))) {
            if (fonte.charAt(pos) == '\n') {
                linha++;
            }
            pos++;
        }
        if (pos >= fonte.length()) {
            return false;
        }
        pos += 2; // "*/"
        return true;
    }

    private Token lerIdentificadorOuPalavra(int linhaToken) {
        int inicio = pos;
        while (pos < fonte.length() && (Character.isLetterOrDigit(fonte.charAt(pos)) || fonte.charAt(pos) == '_')) {
            pos++;
        }
        int fim = Math.min(pos, inicio + TAMANHO_MAXIMO_IDENTIFICADOR);
        String lexema = fonte.substring(inicio, fim);
        TipoToken tipo = palavrasReservadas.buscar(lexema);
        return new Token(lexema, tipo != null ? tipo : TipoToken.IDENTIFICADOR, linhaToken);
    }

    // Cobre inteiro, real e real com expoente (com ou sem sinal). Se depois
    // do "." não vier dígito, ou depois do "e/E" não vier sinal/dígito, o
    // ponteiro volta pra onde estava e o número fica só com a parte válida.
    private Token lerNumero(int linhaToken) {
        int inicio = pos;
        while (pos < fonte.length() && Character.isDigit(fonte.charAt(pos))) {
            pos++;
        }

        if (pos < fonte.length() && fonte.charAt(pos) == '.' && proximoCaractereEhDigito()) {
            pos++;
            while (pos < fonte.length() && Character.isDigit(fonte.charAt(pos))) {
                pos++;
            }
            lerExpoenteSePresente();
        }

        String lexema = fonte.substring(inicio, pos);
        TipoToken tipo = lexema.indexOf('.') >= 0 ? TipoToken.NUMERO_REAL : TipoToken.NUMERO_INTEIRO;
        return new Token(lexema, tipo, linhaToken);
    }

    private void lerExpoenteSePresente() {
        if (pos >= fonte.length() || (fonte.charAt(pos) != 'e' && fonte.charAt(pos) != 'E')) {
            return;
        }
        int marca = pos;
        pos++;
        if (pos < fonte.length() && (fonte.charAt(pos) == '+' || fonte.charAt(pos) == '-')) {
            pos++;
        }
        if (pos < fonte.length() && Character.isDigit(fonte.charAt(pos))) {
            while (pos < fonte.length() && Character.isDigit(fonte.charAt(pos))) {
                pos++;
            }
        } else {
            pos = marca;
        }
    }

    private Token lerString(int linhaToken) {
        int inicio = pos;
        pos++;
        while (pos < fonte.length() && fonte.charAt(pos) != '"') {
            if (fonte.charAt(pos) == '\n') {
                linha++;
            }
            pos++;
        }
        boolean fechada = pos < fonte.length();
        if (fechada) {
            pos++;
        }
        String lexema = fonte.substring(inicio, pos);
        return new Token(lexema, fechada ? TipoToken.CONSTANTE_STRING : TipoToken.ERRO_LEXICO, linhaToken);
    }

    private Token lerChar(int linhaToken) {
        int inicio = pos;
        pos++;
        if (pos < fonte.length() && fonte.charAt(pos) != '\'') {
            pos++;
        }
        boolean fechado = pos < fonte.length() && fonte.charAt(pos) == '\'';
        if (fechado) {
            pos++;
        }
        String lexema = fonte.substring(inicio, pos);
        return new Token(lexema, fechado ? TipoToken.CONSTANTE_CHAR : TipoToken.ERRO_LEXICO, linhaToken);
    }

    private Token lerMaior(int linhaToken) {
        pos++;
        if (proximoCaractereEh('=')) {
            pos++;
            return new Token(">=", TipoToken.OPERADOR_RELACIONAL, linhaToken);
        }
        return new Token(">", TipoToken.OPERADOR_RELACIONAL, linhaToken);
    }

    private Token lerMenor(int linhaToken) {
        pos++;
        if (proximoCaractereEh('=')) {
            pos++;
            return new Token("<=", TipoToken.OPERADOR_RELACIONAL, linhaToken);
        }
        if (proximoCaractereEh('>')) {
            pos++;
            return new Token("<>", TipoToken.OPERADOR_RELACIONAL, linhaToken);
        }
        return new Token("<", TipoToken.OPERADOR_RELACIONAL, linhaToken);
    }

    private Token lerDoisPontos(int linhaToken) {
        pos++;
        if (proximoCaractereEh('=')) {
            pos++;
            return new Token(":=", TipoToken.ATRIBUICAO, linhaToken);
        }
        return new Token(":", TipoToken.SIMBOLO_ESPECIAL, linhaToken);
    }

    private boolean proximoCaractereEh(char esperado) {
        return pos < fonte.length() && fonte.charAt(pos) == esperado;
    }

    private boolean caractereSeguinteEh(char esperado) {
        return pos + 1 < fonte.length() && fonte.charAt(pos + 1) == esperado;
    }

    private boolean proximoCaractereEhDigito() {
        return pos + 1 < fonte.length() && Character.isDigit(fonte.charAt(pos + 1));
    }
}