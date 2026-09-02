package minipascal.lexico.model;

/**
 * Par lexema/token reconhecido pelo analisador, com a linha de origem.
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

    @Override
    public String toString() {
        return lexema + "\t" + tipo.getDescricao();
    }
}
