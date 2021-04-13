package util;

import model.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import exceptions.*;



/**
 * A class used for working with files. Provides XML read and write operations. The file is specified with command line argument.
 */
public class FileManager {
    private LinkedHashSet<Worker> collection;
    private String clArg;

    public FileManager(LinkedHashSet<Worker> collection, String envVarName) {
        this.collection = collection;
        this.clArg = envVarName;//System.getenv(envVarName);//
        if (clArg == null) {
            System.out.println("Предупреждение: путь к xml файлу не найден.");
        }
    }

    /**
     * @param collection - a collection to set for working with file manager.
     */
    public void setCollection(LinkedHashSet<Worker> collection) {
        this.collection = collection;
    }

    /**
     * Saves the collection into XML-file specified in envVar.
     */
    public void saveCollectionToFile() {
        System.out.println("edfvrf");
        if (clArg != null) {
            System.out.println(clArg);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            Document document = db.newDocument();
            System.out.println("eded");
            Element root = document.createElement("collection");
            document.appendChild(root);
            System.out.println("edwed2wd");
            // append the document with tickets
            for (Worker t: collection) {
                createXMLWorkerStructure(t, document);
               // System.out.println(t.toString());
            }

            // create the xml file
            //transform the DOM Object to an XML File
            transformIntoFile(document);

            System.out.println("Запись успешна.");

        } else {
            System.out.println("Ошибка записи. Файл не по указанному пути не найден.");
            // System.out.println(System.getenv());
        }

    }

    /**
     * Creates an XML-structure via DOM-parser for the specified ticket.
     * @param t - a ticket to create a structure for
     * @param document - a document the structure is created in.
     */
    public void createXMLWorkerStructure(Worker t, Document document) {
        // root
        Element workerRoot = document.createElement("worker");
        document.getFirstChild().appendChild(workerRoot);

        // id
        Attr attrId = document.createAttribute("id");
        attrId.setValue(String.valueOf(t.getId()));
        workerRoot.setAttributeNode(attrId);

        // name
        Element name = document.createElement("name");
        name.appendChild(document.createTextNode(t.getName()));
        workerRoot.appendChild(name);

        // coordinates
        Element coordinates = document.createElement("coordinates");
        Coordinates c = t.getCoordinates();
        Element x = document.createElement("x");
        Element y = document.createElement("y");
        x.appendChild(document.createTextNode(String.valueOf(c.getX())));
        y.appendChild(document.createTextNode(String.valueOf(c.getY())));
        coordinates.appendChild(x);
        coordinates.appendChild(y);
        workerRoot.appendChild(coordinates);

        // creationDate
        Element creationDate = document.createElement("creationDate");
        //DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-mm-yyyy");
        creationDate.appendChild(document.createTextNode(t.getCreationDate().toString()));
        workerRoot.appendChild(creationDate);

        // salary
        Element salary = document.createElement("salary");
        salary.appendChild(document.createTextNode(String.valueOf(t.getSalary())));
        workerRoot.appendChild(salary);

        // startDate
        Element startDate = document.createElement("startDate");
        startDate.appendChild(document.createTextNode(t.getStartDate().toString()));
        workerRoot.appendChild(startDate);

        // endDate
        Element endDate = document.createElement("endDate");
        endDate.appendChild(document.createTextNode(t.getEndDate().toString()));
        workerRoot.appendChild(endDate);

        // status
        Element status = document.createElement("status");
        status.appendChild(document.createTextNode(t.getStatus().name()));
        workerRoot.appendChild(status);

        // person
        Element person = document.createElement("person");
        Person v = t.getPerson();
        Element vBirthday = document.createElement("birthday");
        Element vHeight = document.createElement("height");
        Element vPassportID = document.createElement("passportID");
        vBirthday.appendChild(document.createTextNode(v.getBirthday().toString()));
        vHeight.appendChild(document.createTextNode(String.valueOf(v.getHeight())));
        vPassportID.appendChild(document.createTextNode(v.getPassportID()));
        person.appendChild(vBirthday);
        person.appendChild(vHeight);
        person.appendChild(vPassportID);
        workerRoot.appendChild(person);


    }

    /**
     * Transforms a document object to a real XML-file on the machine.
     * @param document - a document object to transform into file
     */
    public void transformIntoFile(Document document) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            BufferedOutputStream buffOutStr = getBuffOutStr();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(buffOutStr);
            transformer.transform(domSource, streamResult);
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка записи. Файл не по указанному пути не найден.");
        } catch (TransformerException e) {
            System.out.println("Непредвиденная ошибка конфигурации.");
        } catch (AccessDeniedException e) {
            e.printMessage();
        }
    }

    /**
     * Reads all the workers from the XML-file.
     * @return the LinkedHashSet with Workers parsed.
     */
    public LinkedHashSet<Worker> parseCollectionFromFile() {
        LinkedHashSet<Worker> workers = new LinkedHashSet<>();
        Worker t;

        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = f.newDocumentBuilder();
            Document document = db.parse(getBuffInStr());
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("worker");

            for (int index = 0; index < nodeList.getLength(); index++) {
                Node node = nodeList.item(index);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    // building an object
                    t = new Worker();
                    t.setId(Long.parseLong(e.getAttribute("id")));
                    t.setName(e.getElementsByTagName("name").item(0).getTextContent());
                    // coords
                    Element coordinates = (Element) (e.getElementsByTagName("coordinates").item(0));
                    t.setCoordinates(new Coordinates(Double.parseDouble(coordinates.getElementsByTagName("x").item(0).getTextContent()),
                            Float.parseFloat(coordinates.getElementsByTagName("y").item(0).getTextContent())));
                    t.setCreationDate(LocalDateTime.parse(e.getElementsByTagName("creationDate").item(0).getTextContent()));
                    t.setSalary(Long.parseLong(e.getElementsByTagName("salary").item(0).getTextContent()));
                    t.setStartDate(LocalDateTime.parse(e.getElementsByTagName("startDate").item(0).getTextContent()));
                    try{t.setEndDate(CommandProcessor.parseTheDate((e.getElementsByTagName("endDate").item(0).getTextContent()))) ;}
                    catch (DomainViolationException exx){
                        exx.printMessage();
                    }
                 //   t.setRefundable(Boolean.parseBoolean(e.getElementsByTagName("refundable").item(0).getTextContent()));
                    t.setStatus(Status.valueOf(e.getElementsByTagName("status").item(0).getTextContent()));
                    // person
                    Element vE = (Element) (e.getElementsByTagName("person").item(0));
                    Person v = new Person();
                    v.setBirthday(LocalDateTime.parse(e.getElementsByTagName("birthday").item(0).getTextContent()));
                    v.setHeight(Integer.parseInt(vE.getElementsByTagName("height").item(0).getTextContent()));
                    v.setPassportID(vE.getElementsByTagName("passportID").item(0).getTextContent());

                    workers.add(t);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения.");
        } catch (AccessDeniedException e) {
            e.printMessage();
        } catch (ParserConfigurationException e) {
            System.out.println("Ошибка конфигурации парсера.");
        } catch (SAXException e) {
            System.out.println("Ошибка парсинга. Проверьте структуру XML-файла.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка парсинга. Проверьте правильность введенных данных.");
        } catch (NullPointerException e) {
            System.out.println("Ошибка парсинга. Проверьте, что файл существует и все необходимые поля заполнены.");
        }

        System.out.println("Объектов загружено: " + workers.size());
        return workers;

    }

    /**
     * Tries to access the file and creates an buffered output stream connected to this file.
     * @return BufferedOutputStream connected to the file specified in environment variable
     * @throws FileNotFoundException - no file found in the given path
     * @throws AccessDeniedException - the file is found, but the user has no rights to write into it.
     */
    private BufferedOutputStream getBuffOutStr() throws FileNotFoundException, AccessDeniedException {
        File file = new File(clArg);
        if (file.exists() && !file.canWrite()) throw new AccessDeniedException("Ошибка доступа. Нет прав на запись в файл.");
        return new BufferedOutputStream(new FileOutputStream(file));
    }

    /**
     * Tries to access the file and creates an buffered input stream connected to this file.
     * @return BufferedInputStream connected to the file specified in environment variable.
     * @throws FileNotFoundException - no file found in the given path
     * @throws AccessDeniedException - the file is found, but the user has no rights to read from it.
     */
    private BufferedInputStream getBuffInStr() throws FileNotFoundException, AccessDeniedException {
        File file = new File(clArg);
        if (file.exists() && !file.canRead()) throw new AccessDeniedException("Ошибка доступа. Нет прав на чтение файла.");
        return new BufferedInputStream(new FileInputStream(file));
    }
}




