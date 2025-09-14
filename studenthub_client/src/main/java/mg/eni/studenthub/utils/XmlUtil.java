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

/**
 * Utilitaires pour sérialiser/désérialiser les objets StudentRequest et StudentResponse en XML.
 * - Permet de sauvegarder les requêtes et réponses localement
 * - Utilise JAXB pour la conversion en XML
 */
public class XmlUtil {
    private static final Logger LOGGER = Logger.getLogger(XmlUtil.class.getName());

    // Initialisation unique du logger
    static {
        LoggerConfig.setup();
    }

    /**
     * Sérialise un StudentRequest vers un fichier XML.
     *
     * @param request   L'objet StudentRequest à sauvegarder
     * @param filePath  Le chemin du fichier XML
     */
    public static void saveRequestToXml(StudentRequest request, String filePath) {
        try {
            JAXBContext context = JAXBContext.newInstance(StudentRequest.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(request, new File(filePath));

            LOGGER.info("Request saved to XML file: " + filePath);
        } catch (JAXBException e) {
            LOGGER.log(Level.SEVERE, "Failed to save request to XML file: " + filePath, e);
        }
    }

    /**
     * Désérialise un fichier XML vers un StudentRequest.
     *
     * @param filePath  Le chemin du fichier XML
     * @return          L'objet StudentRequest ou null en cas d'erreur
     */
    public static StudentRequest loadRequestFromXml(String filePath) {
        try {
            JAXBContext context = JAXBContext.newInstance(StudentRequest.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StudentRequest request = (StudentRequest) unmarshaller.unmarshal(new File(filePath));

            LOGGER.info("Request loaded successfully from: " + filePath);
            return request;
        } catch (JAXBException e) {
            LOGGER.log(Level.SEVERE, "Failed to load request from XML file: " + filePath, e);
            return null;
        }
    }

    /**
     * Sérialise un StudentResponse vers un fichier XML.
     *
     * @param response  L'objet StudentResponse à sauvegarder
     * @param filePath  Le chemin du fichier XML
     */
    public static void saveStudentResponseToXml(StudentResponse response, String filePath) {
        try {
            JAXBContext context = JAXBContext.newInstance(StudentResponse.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(response, new File(filePath));

            LOGGER.info("Response saved to XML file: " + filePath);
        } catch (JAXBException e) {
            LOGGER.log(Level.SEVERE, "Failed to save response to XML file: " + filePath, e);
        }
    }
}
