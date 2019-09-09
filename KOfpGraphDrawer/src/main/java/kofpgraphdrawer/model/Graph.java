package kofpgraphdrawer.model;

import java.awt.Point;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import kofpgraphdrawer.view.View;



/*
**
**
pensavo di aggiungere una variabile booleana per togliere i metodi che ho aggiunto per lavorare con nodi e archi "importati"
ho modificato i vari cast che avevi fatto, in quanto ora le due arraylist sono tipizzate
*/
        
public class Graph /*implements GraphInterface*/{
	//FileManager file;

	protected ArrayList<Node> nodeList;
	protected ArrayList<Edge> edgeList;
        
        protected ArrayList<Edge> kfpEdgeList = new ArrayList();
        private ArrayList<Edge> finalList = new ArrayList();
        
        //uso una linkedList per avere le stringhe del file letto
        protected LinkedList<String> listOfStringByFile = null;
	
	
        protected boolean isAFanPlanarGraph = true;
        //diventa true se la funzione trova che non c'è rappresentazione antifan
        protected static Graph graph = null;
        
        private int COMB_COUNT=0;
	

    public Graph(/*String string*/){
       
		//file = new FileManager(fileHandler.getPathFile());
                
                
		nodeList = new ArrayList<>(0);
		edgeList = new ArrayList<>(0);

               	/*if(file.getStatus()){
			edges=0;
			nodes=0;
		}else{
			edges=nodeList.size();
			nodes=edgeList.size();
		}*/
    }
    
    
    public ArrayList<Node> getNodeList(){
        return this.nodeList;
    }
    
    public ArrayList<Edge> getEdgeList(){
        return this.edgeList;
    }
    //TO-DO: sostituire 0,0 con valori ricevuti cliccando sulla circonferenza
   /* public boolean addNode(){
    	return addNode(new Node(0, 0,"1" , "node " + (nodeList.size()+1)));
        //non va bene che cambia il label 
    }*/
    
    //per import da file, con id e label "fissati"
    public boolean addNodeImported(String ID, String label){
        return addNode(new Node(0,0,ID, label));
    }
    
    public boolean addNode(Node n){      
        return nodeList.add(n);
    }
    
    public boolean delNode(Node n){
        boolean result=false;

    	if (nodeList.remove(n))
    		result=true;
        
        //Rimuovo tutti gli archi connessi al nodo
        if(result){
            Iterator<Edge> it = edgeList.iterator();
            
            while(it.hasNext()){
                Edge e = it.next();
                if(e.getNode1().equals(n) || e.getNode2().equals(n)){
                    it.remove();
                }
            }
        } 
        
        return result;
    }

    public boolean addEdge(Edge e){
    	e.setVectorIndex(edgeList.size()+1);
    	return edgeList.add(e);
    }
    

    public boolean delEdge(Edge e){
        String s = e.getLabel();
        Iterator<Edge> it = edgeList.iterator();
            while (it.hasNext()) {
                Edge o = it.next();
                if (o.getLabel().equals(s)){
                it.remove();
                }
            }
            return true;
        //this.edgeList.remove(e);
    }
    
    
    //funziona
    public void stringFromFile(){
        try{
            //boolean importingStatus = false;
            this.listOfStringByFile = new LinkedList<>();
            String auxiliaryBuffer = null;
            BufferedReader importFile = View.getInstance().getGMLFileHandler().getOpenedFile();   
            while((auxiliaryBuffer=importFile.readLine())!=null){
                if(!auxiliaryBuffer.isEmpty())//se la stringa ausiliaria non contiene quei caratteri, allora salva dentro la linkedList
                    this.listOfStringByFile.add(auxiliaryBuffer.trim());
            }
                
        }
        catch(FileNotFoundException fnfe){
        }
        catch(IOException ioe){
        }
        catch(NullPointerException npe){
            View.getInstance().getInfoPanel().setTextOfLogArea("error during import, file not imported!");
        }
        
    }
       
        /*
        l'intento è questo; nel metodo NodeFromFile io richiamo il metodo per riempire la lista con le righe del file
        scorro tale lista, in cerca del tag "node"
        se lo trovo, e nella posizione successiva trovo "["
            A questo punto, se in i+3 trovo ID e in i+4 label
            salvo le etichette
            creo il nodo e lo salvo nella nodeList!
            Altrimenti, ho ID in i+4 e in i+3 label
        */
         
        //funziona
        public boolean nodeFromFile(){
            this.stringFromFile();
            String auxiliaryId;
            String auxiliaryLabel;
            boolean result=true;
            for(int i=0; i<this.listOfStringByFile.size(); i++){
                auxiliaryId = null;
                auxiliaryLabel = null;
                if(this.listOfStringByFile.get(i).contains("node")){
                    i++;
                    
                    while(!this.listOfStringByFile.get(i).contains("]")){
                        if(this.listOfStringByFile.get(i).toLowerCase().contains("id"))
                            auxiliaryId = listOfStringByFile.get(i).substring(3);
                        else if(this.listOfStringByFile.get(i).toLowerCase().contains("label")){
                            String tempString = listOfStringByFile.get(i);
                            int firstInd = tempString.indexOf("\"");
                            int secondInd = tempString.indexOf("\"", firstInd + 1);
                            auxiliaryLabel = tempString.substring(firstInd+1, secondInd);
                        }
                            i++;
                    }
                    //In assenza di label, ne assegna una vuota
                    if(auxiliaryLabel == null)
                        auxiliaryLabel=auxiliaryId;
                    if(auxiliaryId != null)
                        result=this.addNodeImported(auxiliaryId, auxiliaryLabel);
                    
                } 
            }
            
            return result;
            
        }
        
                
        //funziona
        public boolean edgeFromFile(){
            stringFromFile();
            
            boolean result=true;
            
            Node auxiliaryNodeA = null;
            Node auxiliaryNodeB = null;
            String auxiliaryLabel = null;
            
            for(int i =0; i< this.listOfStringByFile.size(); i++){
                
                if(this.listOfStringByFile.get(i).contains("edge")){
                    i++;
                    while(!this.listOfStringByFile.get(i).contains("]")){
                        
                        if(this.listOfStringByFile.get(i).toLowerCase().contains("source")){     
                            auxiliaryLabel = this.listOfStringByFile.get(i).substring(7);
                            for(int j =0; j<nodeList.size(); j++){
                                if(nodeList.get(j).getID().equals(auxiliaryLabel))
                                    auxiliaryNodeA = nodeList.get(j);
                            }   
                        }
                        
                        if(this.listOfStringByFile.get(i).toLowerCase().contains("target")){
                            auxiliaryLabel = this.listOfStringByFile.get(i).substring(7);
                            for(int j=0; j<nodeList.size(); j++){
                                if(nodeList.get(j).getID().equals(auxiliaryLabel))
                                    auxiliaryNodeB = nodeList.get(j);
                            }
                        }
                        i++;
                    }  
                    
                    result=this.addEdge(new Edge(auxiliaryNodeA,auxiliaryNodeB)); 
                }
            }
            return result;
        }
               
	
        //forse non serve, guarda GMLFileHandler
        public boolean saveToFile(String filePath){
            boolean ris = false;
            PrintWriter printWriter = null;
            try{
                printWriter= new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(new FileOutputStream(filePath),"UTF-8")),true);
                printWriter.print(this.graphToString());
                ris = true;

            }
            catch(UnsupportedEncodingException | FileNotFoundException uee){
                //View.getInstance().addTextToLogArea("Error during saving, graph not saved!");
                ris = false;
            }
            finally{
                try{
                    printWriter.close();
                    }catch(NullPointerException npe){
                        View.getInstance().getInfoPanel().setTextOfLogArea("Error during saving,\n graph not saved!");
                    } 
                }
                
            return ris;
	}
        
        
        public boolean isFanPlanar(int k){
            
            //evito computazioni inutili, isPossible ci dice se dobbiamo analizzarlo oppure no
            boolean isPossible=false;
            
            if(this.nodeList.size()>=(2*k+2))
                isPossible=true;
                           
            //Lista di archi indipendenti da restituire; suppongo siano tutti utili
            ArrayList<Edge> indEdgeList= (ArrayList<Edge>) this.edgeList.clone();
            
            //Elimino gli archi più esterni (formati da due nodi consecutivi)
            indEdgeList = this.removeOuterEdges(indEdgeList);
            
            //Lista temporanea; mi serve solo per lavorare
            ArrayList<Edge> workingList = new ArrayList<>();
            
            //Potrei avere qualche configurazione proibita; se sì, la cerco
            if(isPossible){          
                int numberOfNodes=this.nodeList.size();      
                
                //Affinchè ci sia qualche configurazione proibita, l'eventuale arco deve essere fatto da due nodi a distanza almeno k
                //ossia con almeno k nodi intermedi; mi fermo alla metà perchè in quel caso l'arco "salt" più nodi possibili
                for(int kValues=k+1; kValues<=numberOfNodes/2; kValues++){
                    //Lavoro su tutte le possibili coppie di nodi a distanza almeno k
                    for(int i=0; i<numberOfNodes; i++){
                        Node tempN1 = this.nodeList.get(i);
                        Node tempN2 = this.nodeList.get((i+kValues)%numberOfNodes);     //gestione circolare
                        Edge refEdge=null;
                        
                        //La ripulisco dopo ogni analisi; se l'operazione si ripete, vuol dire che non ho trovato alcuna configurazione proibita
                        workingList.clear();
                        
                        //Verifico se esiste l'arco con i nodi prefissati
                        for (int j=0; j<indEdgeList.size(); j++){                 
                            //Ho trovato un potenziale arco di riferimento per una configurazione proibita
                            if (indEdgeList.get(j).contains(tempN1) && indEdgeList.get(j).contains(tempN2)){
                                refEdge = indEdgeList.get(j);
                            }
                            //Aggiungo alla lista temporanea (workingList) tutti gli altri archi, 
                            //esclusi quelli dipendenti da quello di riferimento
                            if(!indEdgeList.get(j).contains(tempN1) && !indEdgeList.get(j).contains(tempN2))
                                workingList.add(indEdgeList.get(j));
                        }
                        
                        //Effettivamente ho trovato un arco di riferimento: continuo
                        if(refEdge!=null){
                            
                            //Come prima cosa elimino tutti gli archi che non intersecano quello di riferimento, poichè inutili
                            workingList = this.removeNotIntersectingEdges(workingList, refEdge);
                            
                            //Genero tutte le n-uple con n<=k<=numberOfEdges
                            Edge[] nEdges = new Edge[k+1];

                            this.workNEdges(workingList, nEdges, k);   

                            if(!isAFanPlanarGraph){
                                this.kfpEdgeList.add(refEdge);
                                this.isAFanPlanarGraph=true;
                                return false;
                                
                            }
                        }
                    }
                }
            }
            if(this.kfpEdgeList!=null)
                this.kfpEdgeList.clear();
            return true;
	}
        
        public ArrayList<Edge> getKfpEdgeList(){
            return this.kfpEdgeList;
        }
        
        //Metodo che effettua una clicque su tutti i nodi passati come argomento
        public boolean Clique(Node[] n){
            boolean ris = true;
            for (int i=0; i<n.length-1; i++){
                for (int j=i+1; j<n.length; j++){
                    if(!this.addEdge(new Edge(n[i], n[j]))){
                        ris=false;
                        //Si potrebbe avvisare l'utente dicendo l'arco non creato
                    }
                }
            }
            return ris;
        }
            
        //Metodo che effettua una clicque su tutti i nodi del grafo
        public boolean Clique(){
            boolean ris = true;
            boolean toAdd;
            for (int i=0; i<nodeList.size(); i++){
                for (int j=i+1; j<nodeList.size(); j++){
                    //Devo accertarmi che l'arco che voglio inserire non sia già presente
                    toAdd=true;
                    for(int k=0; k<edgeList.size(); k++){
                        if((edgeList.get(k).getNode1()==nodeList.get(i) && edgeList.get(k).getNode2()==nodeList.get(j)) || (edgeList.get(k).getNode1()==nodeList.get(j) && edgeList.get(k).getNode2()==nodeList.get(i))){
                            toAdd=false;
                        }
                    }
                    if(toAdd == true)
                        this.addEdge(new Edge(nodeList.get(i), nodeList.get(j)));
                }
            }
            return ris;
        }
        
        
        public String graphToString(){
    	String result = "graph\r\n[\r\n";
    	for (int i=0; i<nodeList.size(); i++){
    		Node tempNode = nodeList.get(i);
    		result+="\tnode\r\n\t[\r\n" + "\t\tid " + tempNode.getID() + "\r\n\t\tlabel " + "\"" + tempNode.getLabel() + "\"\r\n"
                        +"\t\tgraphics\r\n\t\t[\r\n" + "\t\t\tx "+ tempNode.getCoordinates().x + "\r\n\t\t\ty " +tempNode.getCoordinates().y +"\r\n\t\t]\r\n\t]\r\n";
 
    	}

    	for (int i=0; i<edgeList.size(); i++){
    		Edge tempEdge = edgeList.get(i);
    		result+="\tedge\r\n\t[\r\n" + "\t\tsource " + tempEdge.getNode1().getID() + "\r\n\t\ttarget " + tempEdge.getNode2().getID() + "\r\n\t\t"
                        + "graphics\r\n\t\t[\r\n\t\t\tline\r\n\t\t\t[\r\n\t\t\t\tpoint\r\n\t\t\t\t[\r\n\t\t\t\t\tx "+tempEdge.getNode1().getCoordinates().x +"\r\n\t\t\t\t\ty "+tempEdge.getNode2().getCoordinates().y
                        +"\r\n\t\t\t\t]\r\n\t\t\t\tpoint\r\n\t\t\t\t[\r\n\t\t\t\t\tx "+tempEdge.getNode2().getCoordinates().x+"\r\n\t\t\t\t\ty "+tempEdge.getNode2().getCoordinates().y
                        +"\r\n\t\t\t\t]\r\n\t\t\t]\r\n\t\t]\r\n"
                        + "\t\tlabel " + "\"" + tempEdge.getLabel() + "\"\r\n\t]\r\n";
    	}
    	result += "]";
    	return result;
        }
        
        /*
        Metodo per riordinare la disposizione degli angoli;
        Il primo che viene fissato è quello in angolo 0, si continua poi in senso orario
        */
        protected void checkNodesOrder(){
            Collections.sort(this.nodeList, new SortByAngle());
            
            for(Node n:nodeList){
                double angle = Math.atan2(n.getCoordinates().getY(), n.getCoordinates().getX());
            }
        }
        
        class SortByAngle implements Comparator<Node> { 
            // Used for sorting in ascending order of angle 
            public int compare(Node n1, Node n2) 
            { 
                return (int)(n1.angle-n2.angle);
            } 
        } 
        
        /*
        Metodo che elimina dalla lista tutti gli archi formati da due nodi consecutivi
        */
        private ArrayList<Edge> removeOuterEdges (ArrayList<Edge> list){
            Iterator<Edge> it = list.iterator();
            while(it.hasNext()){
                Edge e = it.next();
                int nOfNodes = this.nodeList.size();
                for(int index=0; index<nOfNodes; index++){
                    //Controllo vero e proprio
                    if(e.contains(this.nodeList.get(index%nOfNodes)) && e.contains(this.nodeList.get((index+1)%nOfNodes)))
                        it.remove();
                }
            }
            return list;
        }
        
        private ArrayList<Edge> removeNotIntersectingEdges (ArrayList<Edge> list, Edge refEdge){
            Iterator<Edge> it = list.iterator();
            while(it.hasNext()){
                Edge e = it.next();
                int nOfNodes = this.nodeList.size();
                if (!e.cross(refEdge))
                    it.remove();
            }
            return list;
        }
        
        private ArrayList<Edge> onlyIndipendentEdges (ArrayList<Edge> list){
            //Intanto elimino i doppioni
            list = this.clearClonedEdges(list);
            ArrayList<Edge> newList = new ArrayList<>();
            boolean found = false;
            
            //Passo adesso all'eliminazione degli archi dipendenti
            for (Edge e:list){
                for(Edge newEdges:newList)
                    if(e.contains(newEdges.getNode1()) || e.contains(newEdges.getNode2()))
                        found=true;
                if(!found)
                    newList.add(e);
                found=false;
            }
            
            return newList;
        }
        
        private ArrayList<Edge> clearClonedEdges (ArrayList<Edge> list){
            ArrayList<Edge> newList = new ArrayList<>();
            newList.clear();
            boolean found=false;
            for (Edge e:list){
                for(Edge newEdges:newList)
                    if(e.toString().equals(newEdges.toString()))
                        found=true;
                if(!found)
                    newList.add(e);
                found=false;
            }
            return newList;
        }
        
        private void workNEdges(ArrayList<Edge> workingList, Edge[] edgeArray, int k){
            this.COMB_COUNT=0;
            workNEdges(0, workingList, edgeArray, k);
        }
   
        private void workNEdges(int j, ArrayList<Edge> workingList, Edge[] edgeArray, int k){
            for(int i = 0; i<workingList.size(); i++){
                edgeArray[j] = workingList.get(i);
                if((j < edgeArray.length - 1) && this.isAFanPlanarGraph){
                    workNEdges(j+1, workingList, edgeArray, k);
                }
                else {
                    this.finalList.clear();
                    for (int q=0; q<edgeArray.length; q++){
                        if(edgeArray[q]!=null)
                            this.finalList.add(edgeArray[q]);
                    }

                    this.finalList = this.onlyIndipendentEdges(this.finalList);

                    if (this.finalList.size() > k){
                        this.kfpEdgeList = (ArrayList<Edge>) this.finalList.clone();
                        this.isAFanPlanarGraph=false;
                    }
                    this.COMB_COUNT++;
         	}
            }
        }
}