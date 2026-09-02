package minipascal.lexico.model;

/**
 * Representa um par (lexema, token) reconhecido pelo analisador léxico,
 * junto com o número da linha onde foi encontrado (útil para mensagens de
 * erro e, mais adiante, para o analisador sintático da Unidade II).
 *
 * Classe imutável de propósito: uma vez reconhecido, um Token não muda.
 */
public class Token {

    private final String lexema;
    private final TipoToken tipo;
    private final int linha;

    public Token(String lexema, TipoToken tipo, int linha) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linha = linha;
    }

    public String getLexema() {
        return lexema;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    public int getLinha() {
        return linha;
    }

    /**
     * Formato de uma linha do arquivo de saída: "<lexema>\t<token>",
     * igual ao exemplo do professor (uma coluna de lexema, uma de token,
     * separadas por espaço/tab).
     */
    @Override
    public String toString() {
        return lexema + "\t" + tipo.getDescricao();
    }
}
