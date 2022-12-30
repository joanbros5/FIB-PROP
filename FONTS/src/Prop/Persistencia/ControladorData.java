package Prop.Persistencia;

import Prop.Exceptions.EmptyFolder;

import Prop.Dominio.*;
import Prop.Dominio.Busqueda.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * @author Arnau Moran Riera
 * Clase que contiene los atributos y métodos relacionados con la capa de Persistencia.
 */
public class ControladorData {

    public static void borrarDirectorio(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    borrarDirectorio(entry);
                }
            }
        }
        Files.delete(path);
    }

    /**
     * Guardar el estado actual del controlador de dominio en un directorio
     * @param path Directorio donde se quiere guardar el estado (separado en 2 subdirectorios)
     */
    public static void guardarEstado (String path) {
        try {
            Path folder = Paths.get(path);
            if(!Files.exists(folder)) {
                try {
                    Files.createDirectories(folder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            borrarDirectorio(folder);

            conjDocumentos cd = ControladorDominio.getInstance().getConjDocs();
            for (DocumentKey dk : cd.getListaDKS()) {
                Documento d = cd.getDoc(dk);
                guardarDocumento(path + "/docs", d, 0);
            }

            FileWriter fd = new FileWriter(path + "/exprs.txt");
            ArrayList<ConsultaBooleana> cbool = ControladorDominio.getInstance().getConjExpr();
            for (ConsultaBooleana c : cbool) {
                String expr = c.getStrExpresion();
                fd.write(expr + "\n");
            }
            fd.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Cargar un estado completo del controlador de dominio
     * @param path Directorio completo de toda la información a cargar (separada en 2 subdirectorios)
     */
    public static void cargarEstado (String path) throws Exception {
        try {
            Path folder = Paths.get(path);
            if(!Files.exists(folder)) {
                try {
                    ControladorDominio.getInstance().first=true;
                    Files.createDirectories(folder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ControladorDominio.getInstance().first=false;
                cargarDocumento(path + "/docs");

                File file = new File(path + "/exprs.txt");
                Scanner input = new Scanner(file);
                String expr = input.nextLine();
                ControladorDominio ctrld = ControladorDominio.getInstance();
                while (expr != null)
                {
                    //ConsultaBooleana c = new ConsultaBooleana(expr);
                    ctrld.crearExpresion(expr);
                    expr = input.nextLine();
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Cargar un documento en el conjunto decumentos desde un directorio deseado
     * @param path Directorio del documento que se quiere importar
     * @throws Exception Al añadir el documento al conjunto se comprueban las excepciones pertinentes
     */
    public static void cargarDocumento(String path) throws Exception {
        if (!path.endsWith(".txt") && !path.endsWith(".xml")) {
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles == null)
            {
                throw new EmptyFolder("Directorio vacío");
            }
            for (File f : listOfFiles) {
                cargarDocumento(path + "/" + f.getName());
            }
        } else {
            try {
                File file = new File(path);
                Scanner input = new Scanner(file);
                String titulo = input.nextLine();
                String autor = input.nextLine();
                DocumentKey dk = new DocumentKey(titulo, autor);
                String body = input.nextLine();
                String[] frases = body.replace(". ", "@").split("@");
                ArrayList<String> c = new ArrayList<String>();
                for (String s : frases) {
                    c.add(s);
                }
                Documento d = new Documento(dk, c);
                conjDocumentos cd = ControladorDominio.getInstance().getConjDocs();
                cd.altaDocumento(d);
                ControladorDominio.getInstance().setConjDocumentos(cd);
                
                conjAutores nca = ControladorDominio.getInstance().getConjAutores();
                nca.insert(d.getAutor());
                ControladorDominio.getInstance().setConjAutores(nca);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Guardar un documento del conjunto en un directorio deseado
     * @param path Directorio en el que se desea exportar el documento
     * @param d Documento que se quiere exportar
     * @param format Formato en el que se quiere exportar el documento (txt / xml)
     */
    public static void guardarDocumento (String path, Documento d, int format) {
        try {
            Path folder = Paths.get(path);
            if (!Files.exists(folder)) {
                try {
                    Files.createDirectories(folder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String name=d.getTitulo();
            if (format==0) {
                FileWriter fd = new FileWriter(path + "/" + name + ".txt");
                fd.write(name + "\n");
                fd.write(d.getAutor() + "\n");
                for (String s : d.getContenido()) {
                    fd.write(s + ". ");
                }
                fd.close();
            } else if (format==1) {
                FileWriter fd = new FileWriter(path + "/" + name + ".xml");
                fd.write(name + "\n");
                fd.write(d.getAutor() + "\n");
                for (String s : d.getContenido()) {
                    fd.write(s + ". ");
                }
                fd.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
