package mg.eni.studenthub.utils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import mg.eni.studenthub.config.LoggerConfig;
import mg.eni.studenthub.shared.StudentRequest;
import mg.eni.studenthub.shared.StudentResponse;

public class XmlUtil {
    private static final Logger LOGGER = Logger.getLogger(XmlUtil.class.getName());

    // Serialization: StudentRequest object to XML file
    public static void saveRequestToXml(StudentRequest request, String filePath) {
        LoggerConfig.setup();
        try {
            JAXBContext context = JAXBContext.newInstance(StudentRequest.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(request, new File(filePath));
            LOGGER.info("💾 Requête sauvegardée dans " + filePath);
            
        } catch (JAXBException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    // Deserialization: XML Request to StudentRequest
    public static StudentRequest loadRequestFromXml(String filePath) {
        LoggerConfig.setup();
        try {
            JAXBContext context = JAXBContext.newInstance(StudentRequest.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StudentRequest request = (StudentRequest) unmarshaller.unmarshal(new File(filePath));
            LOGGER.info("📂 Requête chargée : " + request);
            return request;
        } catch (JAXBException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors du chargement de la requête : ", e.getMessage());
            return null;
        }
    }

    // Serialization: StudentResponse to fichier XML
    public static void saveStudentResponseToXml(StudentResponse response, String filePath) {
        LoggerConfig.setup();
        try {
            JAXBContext context = JAXBContext.newInstance(StudentResponse.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(response, new File(filePath));
            
        } catch (JAXBException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

}
