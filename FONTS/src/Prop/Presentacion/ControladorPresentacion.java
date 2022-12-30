/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prop.Presentacion;

import Prop.Dominio.ControladorDominio;
import Prop.Dominio.DocumentKey;
import Prop.Dominio.conjDocumentos;
import Prop.Exceptions.NullAttr;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 *
 * @author JOAN
 * @brief Contiene el Controlador de la capa de Presentacion
 */
public class ControladorPresentacion {
    private static ControladorPresentacion ctrlP;
    private ControladorDominio ctrlD;
    private InicioNuevo frameNuevo;
    private static FrameInicial frameIni;
    private Errores frameErr;
    private Exito frameExito;
    private Confirmar frameConfirm;
    
    private void ControladorPresentacion() {
        
    }
    
    public static void main(String[] args) {
        frameIni = new FrameInicial();
        
        ControladorDominio c = ControladorDominio.getInstance();
        try {
            c.cargarEstado();
        }
        catch (Exception e) {
        }
        
        frameIni.updateData();
        frameIni.setVisible(true);
        
        
        
    }
    
    //Retorna una instancia possible
    public static ControladorPresentacion getInstance() {
        if (ctrlP == null) {
            ctrlP = new ControladorPresentacion();
        }
        return ctrlP;
        
    }
    
    //Devuelve el controlador de dominio
    public ControladorDominio getControladorDominio() {
        return ctrlD.getInstance();
    }
    
    public void crearDocumento(String tit, String aut, String cont, int idioma) { 
        cont += ". * ";
        ctrlD = ControladorDominio.getInstance();
        ArrayList<String> frases = new ArrayList<>();
        ArrayList<String> palabras = new ArrayList<>();
        for (String nl : cont.replace("\n", " ").replace(". ", "\n").split("\n")) {
            System.out.println(nl);
            if (nl.isEmpty()) this.error("Alguna de las frases está vacía");
            frases.add(nl);
        }

        StringTokenizer jl = new StringTokenizer(cont, " .',;!?¡¿|@#~()[]{}-_:’&=*–+\n“”"); //ya de paso le quitamos los signos de puntuacion LESGO
            while (jl.hasMoreTokens()) {
                palabras.add(jl.nextToken());
            }
        try {
            DocumentKey dk = new DocumentKey(tit, aut);
            ctrlD.altaDocumento(dk,frases, palabras, idioma);
            this.updatePrincipal();
        }
        catch (Exception e) {
            this.error(e.getMessage());
        }
    }
    
    public void borrarDocumento(String titulo, String autor) {
        ctrlD = ControladorDominio.getInstance();
        
        try {
            DocumentKey dk = new DocumentKey(titulo,autor);
            ctrlD.bajaDocumento(dk);
            this.updatePrincipal();
        }
        catch (Exception e) {
            this.error(e.getMessage());
        }
    }
    
    public void duplicarDocumento(String titulo, String autor) {
        ctrlD = ControladorDominio.getInstance();
        
        try {
            DocumentKey dk = new DocumentKey(titulo,autor);
            ctrlD.duplicarDocumento(dk);
            this.updatePrincipal();
        }
        catch (Exception e) {
            this.error(e.getMessage());
        }
    }
    
    public void josearDocumento(String titulo, String autOri, String autNuevo) {
        ctrlD = ControladorDominio.getInstance();
        
        try {
            DocumentKey dk = new DocumentKey(titulo, autOri);
            ctrlD.josearDocumento(dk, autNuevo);
            this.updatePrincipal();
        }
        catch (Exception e) {
            this.error(e.getMessage());
        }
    }
    
    public void modTitulo(DocumentKey dk, DocumentKey oldDK) {
        ctrlD = ControladorDominio.getInstance();
        
        try {
            ctrlD.modTitulo(dk, oldDK);
        }
        catch (Exception e) {
            this.error(e.getMessage());
        }
    }
    
    public void modAutor(DocumentKey dk, DocumentKey oldDK) {
        ctrlD = ControladorDominio.getInstance();
        
        try {
            ctrlD.modAutor(dk, oldDK);
        }
        catch (Exception e) {
            this.error(e.getMessage());
        }
    }
    
    public void modContenido(DocumentKey dk, String cont) {
        ctrlD = ControladorDominio.getInstance();
        
        try {
            ArrayList<String> frases = new ArrayList<>();
            ArrayList<String> palabras = new ArrayList<>();
            for (String nl : cont.replace("\n", " ").replace(". ", "\n").split("\n")) {
                System.out.println(nl);
                if (nl.isEmpty()) this.error("Alguna de las frases está vacía");
                frases.add(nl);
            }

            StringTokenizer jl = new StringTokenizer(cont, " .',;!?¡¿|@#~()[]{}-_:’&=*–+\n“”"); //ya de paso le quitamos los signos de puntuacion LESGO
                while (jl.hasMoreTokens()) {
                    palabras.add(jl.nextToken());
                }
            ctrlD.modContenido(dk, palabras, frases, 0);
        }
        catch (Exception e) {
            this.error(e.getMessage());
        }
    }
    
    
    public void guardarEnSession() {
        ctrlD = ControladorDominio.getInstance();
        ctrlD.guardarEstado();
    }
    
    
    public void updatePrincipal() {
        frameIni.updateData();
        frameIni.setVisible(true);
    }
    
    public ArrayList<String> getListaExpresiones() {
        ctrlD = ControladorDominio.getInstance();
        return ctrlD.listarExpresiones();
    }
    
    public void deleteExpresion(int idxEliminar) {
        ctrlD = ControladorDominio.getInstance();
        try {
            ctrlD.eliminarExpresion(idxEliminar);
        }
        catch (Exception var22) {
            this.error(var22.getMessage());
        }
    }
    
    public ArrayList<String> listarAutoresPrefijo(String prefix) {
        ctrlD = ControladorDominio.getInstance();
        return ctrlD.listarAutores(prefix);
    }
    
    public ArrayList<String> listaTitulosAutor(String autor) {
        ctrlD = ControladorDominio.getInstance();
        try{
            return ctrlD.listarTitulos(autor);
        }
        catch (Exception e) {
            this.error(e.getMessage());
        }
        return null;
    }
    
    public void modificarExpresion(String expr, int id) {
        ctrlD = ControladorDominio.getInstance();
        try {
            ctrlD.modificarExpresion(expr, id);
        }
        catch (Exception var22) {
            this.error(var22.getMessage());
        }
        
        
    }
    
    public void nuevaExpresion(String e) {
        ctrlD = ControladorDominio.getInstance();
        try {
            ctrlD.crearExpresion(e);
        }
        catch (Exception var22) {
            this.error(var22.getMessage());
        }
        
    }
    
    public ArrayList<DocumentKey> nuevaConsultaExpresion(int e) {
        ctrlD = ControladorDominio.getInstance();
        try {
            return ctrlD.resultadoExpresion(e);
        }
        catch (Exception var22) {
            this.error(var22.getMessage());
        }
        return null;
    }
    
    public ArrayList<DocumentKey> nuevaConsultaExpresion(String e) {
        ctrlD = ControladorDominio.getInstance();
        try {
            return ctrlD.resultadoExpresion(e);
        }
        catch (Exception var22) {
            this.error(var22.getMessage());
        }
        return null;
    }
    
    public conjDocumentos getAllDocs() {
        ctrlD = ControladorDominio.getInstance();
        try {
            return ctrlD.getConjDocs();
        }
        catch (Exception var22) {
            this.error(var22.getMessage());
        }
        return null;
    }
    
    public TreeMap<Double,DocumentKey> consultakDocumentosParecidos (DocumentKey dk, int k) {
        ctrlD = ControladorDominio.getInstance();
        try {
            return ctrlD.consultakDocumentos (dk, k);
        }
        catch (Exception var22) {
            this.error(var22.getMessage());
        }
        return null;
        
    }
    
    public ArrayList<String> getContenidoDoc(DocumentKey dk) {
        ctrlD = ControladorDominio.getInstance();
        try {
            return ctrlD.Contenido(dk);
        }
        catch (Exception var22) {
            this.error(var22.getMessage());
        }
        return null;
    }

    public void importarDocumento(String path) {
        ctrlD = ControladorDominio.getInstance();
        try {
            ctrlD.importarDocumento(path);
        }
        catch (Exception var22) {
            this.error(var22.getMessage());
        }
    }

    public void exportarDocumento(String path, DocumentKey dk, int format) {
        ctrlD = ControladorDominio.getInstance();
        try {
            ctrlD.exportarDocumento(path, dk, format);
        }
        catch (Exception var22) {
            this.error(var22.getMessage());
        }
    }
    
    //Crea un frame con un error personalizado
    public void error(String m) {
        frameErr = new Errores(m);
        frameErr.setTitle("Algo salió mal");
        frameErr.show();
        frameErr.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    //Crea un frame de éxito con un mensaje personalizado
    public void exito(String m) {
        frameExito = new Exito(m);
        frameExito.setTitle("¡Enhorabuena!");
        frameExito.show();
    }
    
    //Crea un frame de confirmación personalizado y devuelve un booleano según el botón que se haya pulsado
    public void confirmar(String m) {
        frameConfirm = new Confirmar(m);
        frameConfirm.show();
        
        frameIni.setEnabled(false);
        
        //Espera activa a la confirmación
        //while (!frameConfirm.getConfirmado() || frameConfirm.????);
        
        frameIni.setEnabled(true);
    }
    
    public void inicializar() {
        
        Path folder = Paths.get("log");
        if(!Files.exists(folder)) {
                try {
                    Files.createDirectories(folder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        
        File directory = new File("log");
        String arr[] = directory.list();
        
        frameIni = new FrameInicial();
        
        if (arr.length == 0){
            frameNuevo = new InicioNuevo();
            frameNuevo.show();
            frameIni.setVisible(false);
        }
        else {
            frameIni.setVisible(true);
            frameIni.updateData();
        }
    }
    
    public void muestraFrameInicial() {
        frameIni.setVisible(true);
        frameIni.show();
    }
}
