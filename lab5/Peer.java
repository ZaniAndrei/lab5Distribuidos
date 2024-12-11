/**
 * Lab05: Sistema P2P
 * 
 * Autor: Andrei Fernandes Zani e Mariana Pedroso Naves
 * Ultima atualizacao: 11/12/2024
 * 
 * item B) método printPeers implementado para printar todos os peers que estão
 * ativos e não podem ser conectados 
 * item D) método escolhaPeer implementado, foi substituido a linha que 
 * pega um indice aleatório para o peer e agora aparece um menu com os peers
 * da rede para escolher, se o peer escolhido estiver ativo, ele pede para 
 * escolher denovo. A escolha do usuário é utilizada como parâmetro para iniciar 
 * a conexão com o cliente, para isso eu implementei setEscolha e getEscolha para
 * passar a variável em escopos diferentes. A classe ClienteRMI também possui 
 * mudança em relação a esse item. 
 * 
 * 
 * Referencias: 
 * https://docs.oracle.com/javase/tutorial/essential/io
 * http://fortunes.cat-v.org/
 */

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Peer implements IMensagem{
    
	ArrayList<PeerLista> alocados;
	int escolha;
    public Peer() {
          alocados = new ArrayList<>();
    }
    
    //Cliente: invoca o metodo remoto 'enviar'
    //Servidor: invoca o metodo local 'enviar'
    @Override
    public Mensagem enviar(Mensagem mensagem) throws RemoteException {
        Mensagem resposta;
        try {
        	System.out.println("Mensagem recebida: " + mensagem.getMensagem());
			resposta = new Mensagem(parserJSON(mensagem.getMensagem()));
		} catch (Exception e) {
			e.printStackTrace();
			resposta = new Mensagem("{\n" + "\"result\": false\n" + "}");
		}
        return resposta;
    }    
    
    public String parserJSON(String json) {
		String result = "false";

		String fortune = "-1";		
		
		String[] v = json.split(":");
		System.out.println(">>>" + v[1]);
		String[] v1 = v[1].split("\"");
		System.out.println(">>>" + v1[1]);
		if (v1[1].equals("write")) {
			String [] p = json.split("\\["); 
			 System.out.println(p[1]); 
			 String [] p1 = p[1].split("]"); 
			 System.out.println(p1[0]); 
			 String [] p2 = p1[0].split("\""); 
			 System.out.println(p2[1]); 
			 fortune = p2[1];
			 
			// Write in file
			Principal pv2 = new Principal();
			pv2.write(fortune);
		} else if (v1[1].equals("read")) {
			// Read file
			Principal pv2 = new Principal();
			fortune = pv2.read();
		} 

		result = "{\n" + "\"result\": \"" + fortune + "\"" + "}";
		System.out.println(result);

		return result;
	}
    
    public void printPeers(String [] listaAlocados){
        if(listaAlocados.length == 0 ){
                    System.out.println("Nenhum peer ativo.");
                }else{
                    for(int i=0; i<listaAlocados.length;i++)
    			System.out.print(listaAlocados[i]+" ativo. ");
                }
        System.out.println("");
    }
    
    public int escolhaPeer(List<PeerLista> listaPeers){
        Scanner scanner = new Scanner(System.in); 
        System.out.println("Escolha um peer para se conectar: ");
        for(int i = 0; i< listaPeers.size() ; i++){
            System.out.println("(" + i + ")" + "para Peer " + (i+1));
        }
        return scanner.nextInt();
    }

    public int getEscolha() {
        return escolha;
    }

    public void setEscolha(int escolha) {
        this.escolha = escolha;
    }
    
    
    
    public void iniciar(){

    try {
    		//Adquire aleatoriamente um ID do PeerList
    		List<PeerLista> listaPeers = new ArrayList<>();
    		for( PeerLista peer : PeerLista.values())
    			listaPeers.add(peer);
    		
    		Registry servidorRegistro;
    		try {
    			servidorRegistro = LocateRegistry.createRegistry(1099);
    		} catch (java.rmi.server.ExportException e){ //Registro jah iniciado 
    			System.out.print("Registro jah iniciado. Usar o ativo.\n");
    		}
    		servidorRegistro = LocateRegistry.getRegistry(); //Registro eh unico para todos os peers
    		String [] listaAlocados = servidorRegistro.list();
                
                printPeers(listaAlocados);
                
                
    		SecureRandom sr = new SecureRandom();
    		PeerLista peer = listaPeers.get(sr.nextInt(listaPeers.size()));
                
                
                
    		int tentativas=0;
                
    		boolean repetido = true; 
    		boolean cheio = false;
    		while(repetido && !cheio){
    			repetido=false;    	
                        escolha = escolhaPeer(listaPeers);
                        setEscolha(escolha);
    			peer = listaPeers.get(escolha);
    			for(int i=0; i<listaAlocados.length && !repetido; i++){
    				
    				if(listaAlocados[i].equals(peer.getNome())){
    					System.out.println(peer.getNome() + " ativo. Tentando proximo...");
    					repetido=true;
    					tentativas=i+1;
    				}    			  
    				
    			}
    			//System.out.println(tentativas+" "+listaAlocados.length);
    			    			
    			//Verifica se o registro estah cheio (todos alocados)
    			if(listaAlocados.length>0 && //Para o caso inicial em que nao ha servidor alocado,
    					                     //caso contrario, o teste abaixo sempre serah true
    				tentativas==listaPeers.size()){ 
    				cheio=true;
    			}
    		}
    		
    		if(cheio){
    			System.out.println("Sistema cheio. Tente mais tarde.");
    			System.exit(1);
    		}
            IMensagem skeleton  = (IMensagem) UnicastRemoteObject.exportObject(this, 0); //0: sistema operacional indica a porta (porta anonima)            
            	
            servidorRegistro.rebind(peer.getNome(), skeleton);
            
            System.out.print(peer.getNome() +" Servidor RMI: Aguardando conexoes...");
                        
            //---Cliente RMI
            new ClienteRMI().iniciarCliente(getEscolha());
            
            
            
        } catch(Exception e) {
            e.printStackTrace();
        }        

    }
    
    public static void main(String[] args) {
        Peer servidor = new Peer();
        servidor.iniciar();
    }    
}
