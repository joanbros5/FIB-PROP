package Prop.Dominio;

import java.util.*;
import java.util.Scanner;

import Prop.Dominio.Busqueda.ConsultaBooleana;
import Prop.Exceptions.NullAttr;

import Prop.Presentacion.ControladorPresentacion;

public class Main {
    public static void main(String[] args) throws Exception {
        ControladorDominio ctrld = ControladorDominio.getInstance();
        ctrld.cargarEstado();
        ControladorPresentacion ctrlP = ControladorPresentacion.getInstance();
        ctrlP.inicializar();
        ejecutarComando();
    }

    public static DocumentKey leerDK() throws Exception{
        System.out.println("Titulo");
        Scanner sc1 = new Scanner(System.in);
        String titulo = sc1.nextLine();
        System.out.println("Autor");
        Scanner sc2 = new Scanner(System.in);
        String autor = sc2.nextLine();
        if(titulo.equals("") || autor.equals("")) throw new NullAttr("Pon un título y autor correcto.");
        if(titulo.equals("\n") || autor.equals("\n")) throw new NullAttr("Pon un título y autor correcto.");
        else return new DocumentKey(titulo,autor);
    }

    private static void ejecutarComando() throws Exception{
        ControladorDominio ctrld = ControladorDominio.getInstance();
        Scanner sc = new Scanner(System.in);
        System.out.println("Qué quieres hacer?");
        String comando = sc.nextLine();

        //COMANDOS BASICOS

        while (!comando.equals("q")) {
            switch (comando) {
                case "cd": { //crear documento OK
                    DocumentKey dk = leerDK();
                    System.out.println("Contenido");
                    Scanner sc3 = new Scanner(System.in);
                    //2 contenidos: uno dividido por frases para busqueda booleana y otro por palabras para tf-idf
                    ArrayList<String> contenidoFrase = new ArrayList<>();
                    ArrayList<String> contenidoWord = new ArrayList<>();
                    boolean finish = false;
                    while (!finish) {
                        for (String nl : sc3.nextLine().replace("\n", " ").replace(". ", "\n").split("\n")) {
                            if (nl.isEmpty()) throw new NullAttr("Está vacío");
                            if (nl.equals("*")) {
                                finish = true;
                                break;
                            }
                            contenidoFrase.add(nl);
                        }
                    }
                    String Word = contenidoFrase.toString();
                    StringTokenizer jl = new StringTokenizer(Word, " .',;!?¡¿|@#~()[]{}-_:’&=*–+\n“”"); //ya de paso le quitamos los signos de puntuacion LESGO
                    while (jl.hasMoreTokens()) {
                        contenidoWord.add(jl.nextToken());
                    }
                    //quitar stopwords con fileReader de los archivos de la web
                    System.out.println("En qué idioma está el texto?");
                    System.out.println("1 para inglés, 2 para español, 3 para catalán");
                    Scanner idioma = new Scanner(System.in);
                    int i = idioma.nextInt();
                    ctrld.altaDocumento(dk, contenidoFrase, contenidoWord, i);
                    break;
                }
                case "ed": { //eliminar documento OK
                    DocumentKey dk = leerDK();
                    ctrld.bajaDocumento(dk);
                    break;
                }
                case "ld":   //listar los documentos OK
                    ArrayList<DocumentKey> listaDocs = ctrld.listarDocumentos();
                    if (listaDocs.isEmpty()) throw new NullAttr("El conjunto está vacío");
                    else {
                        System.out.println("Listando los documentos del conjunto:");
                        System.out.println("Alfabéticamente o inverso? 1 para sí 2 para no");
                        Scanner inv = new Scanner(System.in);
                        int a = inv.nextInt();
                        if (a == 2) Collections.reverse(listaDocs);
                        int i = 0;
                        for (DocumentKey dk: listaDocs) {
                            System.out.println("Documento " + (i+1) + ": " + dk.getTitulo() + " " + dk.getAutor());
                            ++i;
                        }
                    }
                    break;
                case "md": { //modificar documento
                    DocumentKey dk = leerDK();
                    Scanner cambiar = new Scanner(System.in);
                    System.out.println("Qué quieres cambiar? 1 para título, 2 para autor, 3 para contenido");
                    int i = cambiar.nextInt();
                    if (i == 1) { //OK
                        System.out.println("Introduce el nuevo título");
                        Scanner nuevoT = new Scanner(System.in);
                        String nuevoTitulo = nuevoT.nextLine();
                        if(nuevoTitulo.isEmpty()) throw new NullAttr("El nuevo título está vacío.");
                        ctrld.modTitulo(new DocumentKey(nuevoTitulo,dk.getAutor()),dk);
                    }
                    else if (i == 2) { //OK
                        System.out.println("Introduce el nuevo autor");
                        Scanner nuevoA = new Scanner(System.in);
                        String nuevoAutor = nuevoA.nextLine();
                        if(nuevoAutor.isEmpty()) throw new NullAttr("El nuevo autor está vacío.");
                        ctrld.modAutor(new DocumentKey(dk.getTitulo(),nuevoAutor),dk);
                    }
                    else { //i == 3
                        System.out.println("Introduce el nuevo contenido");
                        Scanner nuevoC = new Scanner(System.in);
                        //2 contenidos: uno dividido por frases para busqueda booleana y otro por palabras para tf-idf
                        ArrayList<String> NcontenidoFrase = new ArrayList<>();
                        ArrayList<String> NcontenidoWord = new ArrayList<>();
                        boolean finish = false;
                        //String nuevoContenido = nuevoC.nextLine();
                            while (!finish) {
                                for (String nl : nuevoC.nextLine().replace("\n", " ").replace(". ", "\n").split("\n")) {
                                    if (nl.equals("*")) {
                                        finish = true;
                                        break;
                                    }
                                    NcontenidoFrase.add(nl);
                                }
                            }
                            String Word = NcontenidoFrase.toString();
                            StringTokenizer jl = new StringTokenizer(Word, " .',;!?¡¿|@#~()[]{}-_–:’&=*\n“”"); //ya de paso le quitamos los signos de puntuacion LESGO
                            while (jl.hasMoreTokens()) {
                                NcontenidoWord.add(jl.nextToken());
                            }
                        System.out.println("En qué idioma está el texto?");
                        System.out.println("1 para inglés, 2 para español, 3 para catalán");
                        Scanner idioma = new Scanner(System.in);
                        Integer j = idioma.nextInt();
                        ctrld.modContenido(dk,NcontenidoWord,NcontenidoFrase, j);
                        if (NcontenidoFrase.isEmpty()) throw new NullAttr("El nuevo contenido está vacío.");
                    }
                    break;
                }
                case "lt": { //lista de titulos de un autor
                    System.out.println("Autor");
                    Scanner sc1 = new Scanner(System.in);
                    String autor = sc1.nextLine();
                    if (!ctrld.getConjAutores().existsAutor(autor)) throw new NullAttr("El autor no existe");
                    ArrayList<String> titulosDeAutor = ctrld.listarTitulos(autor);
                    if(titulosDeAutor.isEmpty()) throw new NullAttr("No hay titulos de ese autor");
                    System.out.println("lista de títulos del autor " + autor + ":");
                    System.out.println("Alfabéticamente o inverso? 1 para sí 2 para no");
                    Scanner inv = new Scanner(System.in);
                    int a = inv.nextInt();
                    if (a == 2) Collections.reverse(titulosDeAutor);
                    for (String s : titulosDeAutor) {
                        System.out.println(s);
                    }
                    break;
                }
                case "id": { //importar documento
                    System.out.println("De qué directorio quieres importar el documento?");
                    Scanner path = new Scanner(System.in);
                    String p = path.nextLine();
                    ctrld.importarDocumento(p);
                    break;
                }

                case "exp": { //exportar documento + nombredoc
                    System.out.println("Qué documento quieres exportar?");
                    DocumentKey dk = leerDK();
                    System.out.println("A qué directorio quieres exportar el documento?");
                    Scanner path = new Scanner(System.in);
                    String p = path.nextLine();
                    System.out.println("A qué formato? 0 para .txt, 1 para .XML");
                    Scanner formato = new Scanner(System.in);
                    int f = formato.nextInt();
                    ctrld.exportarDocumento(p, dk, f);
                    break;
                }

                /* OPCIONAL */
                case "dd": { //duplicar documento OK
                    DocumentKey dk = leerDK(); //del documento a duplicar
                    ctrld.duplicarDocumento(dk);
                    break;
                }
                case "josea": { //josear documento OK
                    DocumentKey dk = leerDK(); //del documento a robar
                    System.out.println("Autor nuevo?");
                    Scanner sc2 = new Scanner(System.in);
                    String autor = sc2.nextLine();
                    ctrld.josearDocumento(dk, autor);
                    break;
                }

                //CONSULTAS
                case "c": { //contenido de documento dado titulo y autor
                    DocumentKey dk = leerDK();
                    ArrayList<String> cont = ctrld.Contenido(dk);
                    for (String s : cont) {
                        System.out.println(s);
                    }
                    break;
                }
                case "la":  //lista de autores dado un prefijo
                    System.out.println("Prefijo");
                    Scanner sc3 = new Scanner(System.in);
                    String pre = sc3.nextLine();
                    ArrayList<String> autores = ctrld.listarAutores(pre);
                    if(autores.isEmpty()) throw new NullAttr("No hay autores con este prefijo");
                    System.out.println("lista de autores con el prefijo " + pre + ":");
                    System.out.println("Alfabéticamente o inverso? 1 para sí 2 para no");
                    Scanner inv2 = new Scanner(System.in);
                    int b = inv2.nextInt();
                    if (b == 2) Collections.reverse(autores);
                    for (String a : autores) {
                        System.out.println(a);
                    }
                    break;
                case "ce": { //crear e.booleana
                    System.out.println("Expresión");
                    Scanner sc4 = new Scanner(System.in);
                    String consulta = sc4.nextLine();
                    ctrld.crearExpresion(consulta);
                    break;
                }
                case "ee": { //eliminar e.booleana
                    System.out.println("ID de la Expresión");
                    Scanner sc5 = new Scanner(System.in);
                    int id = sc5.nextInt();
                    ctrld.eliminarExpresion(id);
                    break;
                }
                case "me": { //modificar e.booleana
                    System.out.println("ID de la Expresión");
                    Scanner sc1 = new Scanner(System.in);
                    int id = sc1.nextInt();
                    System.out.println("Nueva Expresión");
                    Scanner sc2 = new Scanner(System.in);
                    String consulta = sc2.nextLine();
                    ctrld.modificarExpresion(consulta, id);
                    break;
                }
                case "ge": { //Devuelve una expresion booleana
                    System.out.println("ID de la Expresión");
                    Scanner sc1 = new Scanner(System.in);
                    int id = sc1.nextInt();
                    System.out.println(ctrld.getExpresion(id));
                    break;
                }
                case "le": { //listar expresiones booleanas
                    ArrayList<ConsultaBooleana> cbool = ctrld.getConjExpr();
                    int i = 0;
                    System.out.println("Alfabéticamente o inverso? 1 para sí 2 para no");
                    Scanner inv3 = new Scanner(System.in);
                    int a = inv3.nextInt();
                    if (a == 2) Collections.reverse(cbool);
                    for (ConsultaBooleana consulta: cbool) {
                        System.out.println("Expresion Booleana: "+consulta.getStrExpresion());
                        ++i;
                    }
                    break;
                }
                case "ckd": { //consultar los k documentos mas parecidos
                    DocumentKey dk = leerDK();
                    Scanner sc1 = new Scanner(System.in);
                    System.out.println("k?");
                    int k = sc1.nextInt();
                    TreeMap<Double,DocumentKey> simil = ctrld.consultakDocumentos(dk, k);
                    int count = 0;
                    for (Double d : simil.keySet()) {
                        if (count >= k) break;
                        System.out.println("documento " + simil.get(d).getTitulo() + " " + simil.get(d).getAutor() + " " + "con similaridad " + d);

                        ++count;
                    }
                    break;
                }
                case "re": { //resultado expresion booleana (aplicarla y devolver resultado)
                    System.out.println("ID de la Expresión");
                    Scanner sc6 = new Scanner(System.in);
                    int exp = sc6.nextInt();
                    ArrayList<DocumentKey> arrayKey = ctrld.resultadoExpresion(exp);
                    System.out.println("Alfabéticamente o inverso? 1 para sí 2 para no");
                    Scanner inv = new Scanner(System.in);
                    int a = inv.nextInt();
                    if (a == 2) Collections.reverse(arrayKey);
                    for (DocumentKey ak: arrayKey) {
                        System.out.println("Titulo: "+ak.getTitulo()+"; Autor: "+ak.getAutor());
                    }
                    break;
                }
            }
            System.out.println("Algo más?");
            comando = sc.nextLine();
        }
        ctrld.guardarEstado();
    }
}
