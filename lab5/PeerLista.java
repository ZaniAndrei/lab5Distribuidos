/**
 * Lab05: Sistema P2P
 * 
 * Autor: Andrei Fernandes Zani e Mariana Pedroso Naves
 * Ultima atualizacao: 11/12/2024
 * 
 * item A) Mudança na linha 30: adição de um novo peer: PEER4
 * 
 * Referencias: 
 * https://docs.oracle.com/javase/tutorial/essential/io
 * http://fortunes.cat-v.org/
 */

public enum PeerLista {
    
    PEER1 {
        @Override
        public String getNome() {
            return "PEER1";
        }        
    },
    PEER2 {
        public String getNome() {
            return "PEER2";
        }        
    },
    PEER3 {
        public String getNome() {
            return "PEER3";
        }        
    },
    PEER4 {
        public String getNome() {
            return "PEER4";
        }        
    };
    public String getNome(){
        return "NULO";
    }    
}
